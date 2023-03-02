## 1. 概述

在本文中，我们将了解原生镜像以及如何从Spring Boot应用程序和GraalVM的原生镜像构建器创建原生镜像。**我们指的是Spring Boot 3，但我们将在文章末尾解决与Spring Boot 2的差异**。

## 2. 原生镜像

**原生镜像是一种将Java代码构建为独立可执行文件的技术**。此可执行文件包括应用程序类、来自其依赖项的类、运行时库类以及来自JDK的静态链接本机代码。JVM被打包到原生镜像中，因此目标系统上不需要任何Java运行时环境，但构建工件是平台相关的。因此我们需要为每个支持的目标系统构建一个，当我们使用像Docker这样的容器技术时，这会更容易，我们可以在其中构建一个容器作为可以部署到任何Docker运行时的目标系统。

### 2.1 GraalVM和原生镜像构建器

通用递归应用和算法语言虚拟机(General Recursive Applicative and Algorithmic Language Virtual Machine)Graal VM是为Java和其他JVM语言编写的高性能JDK发行版，同时支持JavaScript、Ruby、Python和其他几种语言。它提供了一个原生镜像构建器-一种从Java应用程序构建本机代码并将其与VM一起打包成独立可执行文件的工具。它由Spring Boot [Maven](https://docs.spring.io/spring-boot/docs/3.0.0/maven-plugin/reference/htmlsingle/)和[Gradle](https://docs.spring.io/spring-boot/docs/3.0.0/gradle-plugin/reference/htmlsingle/)插件正式支持，但有[少数例外](https://github.com/spring-projects/spring-boot/wiki/Known-GraalVM-Native-Image-Limitations)(最糟糕的是Mockito目前不支持本机测试)。

### 2.2 特殊功能

在构建原生镜像时，我们会遇到两个典型功能。

提前(AOT)编译是将高级Java代码编译为本机可执行代码的过程。通常，这是由JVM的即时编译器(JIT)在运行时完成的，它允许在执行应用程序时进行观察和优化。在AOT编译的情况下，这个优势就失去了。

通常，在AOT编译之前，可以选择有一个称为AOT处理的单独步骤，即从代码中收集元数据并将它们提供给AOT编译器。分为这两个步骤是有意义的，因为AOT处理可以是特定于框架的，而AOT编译器更通用。下图给出了一个概览：

<img src="../assets/img.png">

Java平台的另一个特点是它在目标系统上的可扩展性，只需将JAR放入类路径即可。由于启动时的反射和注解扫描，我们随后在应用程序中获得了扩展行为。

不幸的是，这会减慢启动时间并且不会带来任何好处，尤其是对于云原生应用程序，甚至服务器运行时和Java基类都被打包到JAR中。因此，我们省去了此功能，然后可以使用Closed World Optimization构建应用程序。

这两个功能都减少了需要在运行时执行的工作量。

### 2.3 优点

**原生镜像提供各种优势，例如即时启动和减少内存消耗**。它们可以打包到轻量级容器镜像中，以实现更快、更高效的部署，并且它们呈现出更小的攻击面。

### 2.4 限制

由于封闭世界优化，存在一些[限制](https://www.graalvm.org/22.1/reference-manual/native-image/Limitations/)，我们在编写应用程序代码和使用框架时必须注意这些限制。目前：

-   可以在构建时执行类初始值设定项，以加快启动速度并提高峰值性能。但我们必须意识到，这可能会破坏代码中的一些假设，例如，当加载一个文件时，该文件必须在构建时可用。
-   反射和动态代理在运行时成本高昂，因此在封闭世界假设下在构建时进行了优化。在构建时执行时，我们可以在类初始值设定项中不受限制地使用它。必须向AOT编译器声明任何其他用法，原生镜像构建器会尝试通过执行静态代码分析来实现。如果失败，我们必须提供此信息，例如，通过[配置文件](https://www.graalvm.org/22.1/reference-manual/native-image/BuildConfiguration/)。
-   这同样适用于所有基于反射的技术，如JNI和序列化。
-   此外，原生镜像构建器提供了自己的本机接口，该接口比JNI简单得多且开销更低。
-   对于原生镜像构建，字节码在运行时不再可用，因此无法使用针对JVMTI的工具进行调试和监视。然后，我们必须使用本机调试器和监控工具。

**关于Spring Boot，我们必须意识到Profiles、条件bean和.enable属性等功能[在运行时不再完全受支持](https://docs.spring.io/spring-boot/docs/3.0.0/reference/htmlsingle/#native-image.introducing-graalvm-native-images.understanding-aot-processing)**。如果我们使用Profile，则必须在构建时指定它们。

## 3. 基本设置

在我们构建原生镜像之前，我们必须安装这些工具。

### 3.1 GraalVM和原生镜像

首先，我们按照[安装说明](https://graalvm.github.io/native-build-tools/latest/graalvm-setup.html)安装当前版本的GraalVM和原生镜像构建器(Spring Boot要求版本22.3)。我们应该确保安装目录可以通过GRAALVM_HOME环境变量获得，并且“<GRAALVM_HOME\>/bin”已添加到PATH变量中。

### 3.2 本机编译器

在构建期间，原生镜像构建器调用特定于平台的本机编译器。因此，我们需要这个本机编译器，遵循我们平台的[“先决条件”说明](https://www.graalvm.org/22.3/reference-manual/native-image/)。这将使构建依赖平台。我们必须意识到，只能在特定于平台的命令行中运行构建。例如，使用Git Bash在Windows上运行构建将不起作用。我们需要改用Windows命令行。

### 3.3 Docker

作为先决条件，我们将确保安装[Docker](https://www.baeldung.com/dockerizing-spring-boot-application)，稍后需要它来运行原生镜像。Spring Boot Maven和Gradle插件使用[Paketo Tiny Builder](https://github.com/paketo-buildpacks/tiny-builder)构建容器。

## 4. 使用Spring Boot配置和构建项目

在Spring Boot中使用本机构建功能非常简单。我们创建我们的项目，例如，通过使用[Spring Initializr](https://start.spring.io/)并添加应用程序代码。然后，要使用GraalVM的原生镜像构建器构建原生镜像，我们需要使用GraalVM本身提供的Maven或Gradle插件来扩展我们的构建。

### 4.1 Maven

[Spring Boot Maven插件](https://docs.spring.io/spring-boot/docs/3.0.0/maven-plugin/reference/htmlsingle/)的目标(goal)是AOT处理(即，不是AOT编译本身，而是为AOT编译器收集元数据，例如，在代码中注册反射的使用)和构建可以与Docker一起运行的OCI镜像。我们可以直接调用这些目标：

```shell
mvn spring-boot:process-aot
mvn spring-boot:process-test-aot
mvn spring-boot:build-image
```

我们不需要这样做，因为Spring Boot父POM定义了一个将这些目标绑定到构建的native Profile。我们需要使用这个激活的Profile进行构建：

```shell
mvn clean package -Pnative
```

如果我们还想执行本机测试，我们可以激活第二个Profile：

```shell
mvn clean package -Pnative,nativeTest
```

如果我们要构建一个原生镜像，我们必须添加相应的native-maven-plugin目标。因此，我们也可以定义一个native Profile。因为这个插件是由父POM管理的，所以我们可以省略版本号：

```xml
<profiles>
    <profile>
        <id>native</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.graalvm.buildtools</groupId>
                    <artifactId>native-maven-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>build-native</id>
                            <goals>
                                <goal>compile-no-fork</goal>
                            </goals>
                            <phase>package</phase>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

**目前，本机测试执行不支持Mockito**。因此，我们可以排除Mocking测试，或者通过将其添加到我们的POM来简单地跳过本机测试：

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <configuration>
                    <skipNativeTests>true</skipNativeTests>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

### 4.2 在没有父POM的情况下使用Spring Boot

如果我们不能从Spring Boot父POM继承，而是将其用作[import范围依赖项](https://docs.spring.io/spring-boot/docs/3.0.0/maven-plugin/reference/htmlsingle/#using.import)，我们必须自己配置插件和Profile。然后，我们必须将其添加到我们的POM中：

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <version>${native-build-tools-plugin.version}</version>
                <extensions>true</extensions>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
<profiles>
    <profile>
        <id>native</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <configuration>
                        <image>
                            <builder>paketobuildpacks/builder:tiny</builder>
                            <env>
                                <BP_NATIVE_IMAGE>true</BP_NATIVE_IMAGE>
                            </env>
                        </image>
                    </configuration>
                    <executions>
                        <execution>
                            <id>process-aot</id>
                            <goals>
                                <goal>process-aot</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>org.graalvm.buildtools</groupId>
                    <artifactId>native-maven-plugin</artifactId>
                    <configuration>
                        <classesDirectory>${project.build.outputDirectory}</classesDirectory>
                        <metadataRepository>
                            <enabled>true</enabled>
                        </metadataRepository>
                        <requiredVersion>22.3</requiredVersion>
                    </configuration>
                    <executions>
                        <execution>
                            <id>add-reachability-metadata</id>
                            <goals>
                                <goal>add-reachability-metadata</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
    <profile>
        <id>nativeTest</id>
        <dependencies>
            <dependency>
                <groupId>org.junit.platform</groupId>
                <artifactId>junit-platform-launcher</artifactId>
                <scope>test</scope>
            </dependency>
        </dependencies>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>process-test-aot</id>
                            <goals>
                                <goal>process-test-aot</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>org.graalvm.buildtools</groupId>
                    <artifactId>native-maven-plugin</artifactId>
                    <configuration>
                        <classesDirectory>${project.build.outputDirectory}</classesDirectory>
                        <metadataRepository>
                            <enabled>true</enabled>
                        </metadataRepository>
                        <requiredVersion>22.3</requiredVersion>
                    </configuration>
                    <executions>
                        <execution>
                            <id>native-test</id>
                            <goals>
                                <goal>test</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
<properties>
    <native-build-tools-plugin.version>0.9.17</native-build-tools-plugin.version>
</properties>
```

### 4.3 Gradle

[Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/3.0.0/gradle-plugin/reference/htmlsingle/)为AOT处理(即，不是AOT编译本身，而是为AOT编译器收集元数据，例如，在代码中注册反射的使用)和构建可以与Docker一起运行的OCI镜像提供任务：

```shell
gradle processAot
gradle processTestAot
gradle bootBuildImage
```

如果我们想要构建原生镜像，我们必须添加[Gradle插件](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)来构建GraalVM原生镜像：

```groovy
plugins {
    // ...
    id 'org.graalvm.buildtools.native' version '0.9.17'
}
```

然后，我们可以运行测试并构建项目

```shell
gradle nativeTest
gradle nativeCompile
```

**目前，本机测试执行不支持Mockito**。因此，我们可以通过配置graalvmNative扩展来排除Mocking测试或跳过本机测试，如下所示：

```groovy
graalvmNative {
    testSupport = false
}
```

## 5. 扩展原生镜像构建配置

如前所述，我们必须为AOT编译器注册反射、类路径扫描、动态代理等的每个用法。**因为Spring的内置原生支持是一个非常年轻的特性，目前并不是所有的Spring模块都有内置支持，所以目前需要我们自己来添加**。这可以通过手动创建构建配置来完成。不过，使用Spring Boot提供的接口更容易，这样Maven和Gradle插件都可以在AOT处理期间使用我们的代码来生成构建配置。

指定额外本机配置的一种可能性是[Native Hints](https://docs.spring.io/spring-framework/docs/6.0.0/reference/html/core.html#aot-hints)。因此，让我们看一下当前缺少内置支持的两个示例，以及如何将其添加到我们的应用程序以使其正常工作。

### 5.1 示例：Jackson的PropertyNamingStrategy

在MVC Web应用程序中，REST控制器方法的每个返回值都由Jackson序列化，自动将每个属性命名为JSON元素。我们可以通过在application.properties文件中配置Jackson的PropertyNamingStrategy来全局影响名称映射：

```properties
spring.jacksonproperty-naming-strategy=SNAKE_CASE
```

SNAKE_CASE是PropertyNamingStrategies类型的静态成员的名称。不幸的是，这个成员是通过反射解决的。所以AOT编译器需要知道这一点，否则，我们会收到一条错误消息：

```shell
Caused by: java.lang.IllegalArgumentException: Constant named 'SNAKE_CASE' not found
  at org.springframework.util.Assert.notNull(Assert.java:219) ~[na:na]
  at org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
        $Jackson2ObjectMapperBuilderCustomizerConfiguration
        $StandardJackson2ObjectMapperBuilderCustomizer.configurePropertyNamingStrategyField(JacksonAutoConfiguration.java:287) ~[spring-features.exe:na]
```

为此，我们可以通过如下简单的方式实现和注册RuntimeHintsRegistrar：

```java
@Configuration
@ImportRuntimeHints(JacksonRuntimeHints.PropertyNamingStrategyRegistrar.class)
public class JacksonRuntimeHints {

    static class PropertyNamingStrategyRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            try {
                hints
                      .reflection()
                      .registerField(PropertyNamingStrategies.class.getDeclaredField("SNAKE_CASE"));
            } catch (NoSuchFieldException e) {
                // ...
            }
        }
    }
}
```

**注意：自版本3.0.0-RC2以来，在Spring Boot中解决此问题的[pull request](https://github.com/spring-projects/spring-boot/pull/33080)已经合并，因此它可以开箱即用地与Spring Boot 3一起使用**。

### 5.2 示例：GraphQL模式文件

如果我们想要实现一个[GraphQL API](https://www.baeldung.com/spring-graphql)，我们需要创建一个模式文件并将其定位在“classpath:/graphql/*.graphqls”下，Springs GraphQL自动配置会自动检测到它。这是通过类路径扫描以及集成的GraphiQL测试客户端的欢迎页面完成的。因此，为了在本机可执行文件中正常工作，AOT编译器需要知道这一点。我们可以用同样的方式注册：

```java
@ImportRuntimeHints(GraphQlRuntimeHints.GraphQlResourcesRegistrar.class)
@Configuration
public class GraphQlRuntimeHints {

    static class GraphQlResourcesRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources()
                  .registerPattern("graphql/**/")
                  .registerPattern("graphiql/index.html");
        }
    }
}
```

Spring GraphQL团队已经在着手[解决这个问题](https://github.com/spring-projects/spring-graphql/issues/495)，所以我们可能会在未来的版本中内置它。

## 6. 编写测试

要测试RuntimeHintsRegistrar实现，我们甚至不需要运行Spring Boot测试，我们可以创建一个简单的JUnit测试，如下所示：

```java
@Test
void shouldRegisterSnakeCasePropertyNamingStrategy() {
    // arrange
    final var hints = new RuntimeHints();
    final var expectSnakeCaseHint = RuntimeHintsPredicates
        .reflection()
        .onField(PropertyNamingStrategies.class, "SNAKE_CASE");
    // act
    new JacksonRuntimeHints.PropertyNamingStrategyRegistrar()
        .registerHints(hints, getClass().getClassLoader());
    // assert
    assertThat(expectSnakeCaseHint).accepts(hints);
}
```

如果我们想通过集成测试来测试它，我们可以检查Jackson ObjectMapper是否具有正确的配置：

```java
@SpringBootTest
class JacksonAutoConfigurationIntegrationTest {

    @Autowired
    ObjectMapper mapper;

    @Test
    void shouldUseSnakeCasePropertyNamingStrategy() {
        assertThat(mapper.getPropertyNamingStrategy())
              .isSameAs(PropertyNamingStrategies.SNAKE_CASE);
    }
}
```

要使用本机模式对其进行测试，我们必须运行本机测试：

```shell
# Maven
mvn clean package -Pnative,nativeTest
# Gradle
gradle nativeTest
```

如果我们需要为Spring Boot测试提供特定于测试的AOT支持，我们可以使用[AotTestExecutionListener](https://docs.spring.io/spring-framework/docs/6.0.0/javadoc-api/org/springframework/test/context/aot/AotTestExecutionListener.html)接口实现[TestRuntimeHintsRegistrar](https://docs.spring.io/spring-framework/docs/6.0.0/javadoc-api/org/springframework/test/context/aot/TestRuntimeHintsRegistrar.html)或[TestExecutionListener](https://www.baeldung.com/spring-testexecutionlistener)。我们可以在[官方文档](https://docs.spring.io/spring-framework/docs/6.0.0/reference/html/testing.html#testcontext-aot)中找到详细信息。

## 7. Spring Boot 2

Spring 6和Spring Boot 3在原生镜像构建方面迈出了一大步。但是对于之前的大版本，这也是可以的。我们只需要知道还没有内置支持，即，有一个补充的[Spring Native计划](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)来处理这个主题。因此，我们必须在我们的项目中手动包含和配置它。对于AOT处理，有一个单独的Maven和Gradle插件，它没有合并到Spring Boot插件中。当然，集成库并没有像现在这样提供原生支持(将来会更多)。

### 7.1 Spring Native依赖

首先，我们必须为Spring Native添加Maven依赖：

```xml
<dependency>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-native</artifactId>
    <version>0.12.1</version>
</dependency>
```

但是，**对于Gradle项目，Spring Native是由Spring AOT插件自动添加的**。

我们应该注意，**每个Spring Native版本仅支持特定的Spring Boot版本**-例如，Spring Native 0.12.1仅支持Spring Boot 2.7.1。因此，我们应该确保在我们的pom.xml中使用兼容的Spring Boot Maven依赖项。

### 7.2 Buildpacks

要构建OCI镜像，我们需要显式[配置构建包](https://www.baeldung.com/spring-boot-docker-images#buildpacks)。

对于Maven，我们需要使用[Paketo Java buildpacks](https://paketo.io/docs/buildpacks/language-family-buildpacks/java/)的原生镜像配置的[spring-boot-maven-plugin](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-maven-plugin/3.0.3)：

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <image>
                        <builder>paketobuildpacks/builder:tiny</builder>
                        <env>
                            <BP_NATIVE_IMAGE>true</BP_NATIVE_IMAGE>
                        </env>
                    </image>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

在这里，**我们将使用各种可用构建器中的微型构建器，例如base和full来构建原生镜像**。此外，我们通过为BP_NATIVE_IMAGE环境变量提供true值来启用buildpack。

同样，在使用Gradle时，我们可以将tiny构建器连同BP_NATIVE_IMAGE环境变量添加到build.gradle文件中：

```groovy
bootBuildImage {
    builder = "paketobuildpacks/builder:tiny"
    environment = [
            "BP_NATIVE_IMAGE" : "true"
    ]
}
```

### 7.3 Spring AOT 插件

接下来，我们需要添加执行[提前转换](https://www.baeldung.com/ahead-of-time-compilation)的[Spring AOT](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#spring-aot)插件，这有助于改善原生镜像的占用空间和兼容性。

因此，让我们将最新的[spring-aot-maven-plugin](https://repo.spring.io/artifactory/release/org/springframework/experimental/spring-aot-maven-plugin/) Maven依赖项添加到我们的pom.xml中：

```xml
<plugin>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-aot-maven-plugin</artifactId>
    <version>0.12.1</version>
    <executions>
        <execution>
            <id>generate</id>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>

```

同样，对于一个Gradle项目，我们可以在build.gradle文件中添加最新的[org.springframework.experimental.aot](https://repo.spring.io/artifactory/release/org/springframework/experimental/aot/org.springframework.experimental.aot.gradle.plugin/)依赖 ：

```groovy
plugins {
    id 'org.springframework.experimental.aot' version '0.10.0'
}
```

此外，正如我们之前提到的，这会自动将Spring Native依赖项添加到Gradle项目中。

Spring AOT插件提供了[几个选项来确定源代码生成](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#spring-aot-configuration)。例如，像removeYamlSupport和removeJmxSupport这样的选项分别删除了[Spring Boot Yaml](https://www.baeldung.com/spring-yaml)和Spring Boot [JMX](https://www.baeldung.com/java-management-extensions)支持。

### 7.4 构建和运行镜像

就是这样！我们已准备好使用Maven命令构建我们的Spring Boot项目的原生镜像：

```shell
$ mvn spring-boot:build-image
```

### 7.5 原生镜像构建

接下来，我们将添加一个名为native的Profile，其中包含一些插件的构建支持，例如[native-maven-plugin](https://central.sonatype.com/artifact/org.graalvm.buildtools/native-maven-plugin/0.9.20)和spring-boot-maven-plugin：

```xml
<profiles>
    <profile>
        <id>native</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.graalvm.buildtools</groupId>
                    <artifactId>native-maven-plugin</artifactId>
                    <version>0.9.17</version>
                    <executions>
                        <execution>
                            <id>build-native</id>
                            <goals>
                                <goal>build</goal>
                            </goals>
                            <phase>package</phase>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <configuration>
                        <classifier>exec</classifier>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

此Profile将在package阶段从构建中调用native-image编译器。

但是，在使用Gradle时，我们会将最新的[org.graalvm.buildtools.native](https://central.sonatype.com/artifact/org.graalvm.buildtools.native/org.graalvm.buildtools.native.gradle.plugin/0.9.20)插件添加到build.gradle文件中：

```groovy
plugins {
    id 'org.graalvm.buildtools.native' version '0.9.17'
}
```

就是这样！我们已准备好通过在Maven package命令中提供native Profile来构建我们的原生镜像：

```shell
mvn clean package -Pnative
```

## 8. 总结

在本教程中，我们探索了使用Spring Boot和GraalVM的原生构建工具构建原生镜像。我们了解了Spring的内置原生支持。