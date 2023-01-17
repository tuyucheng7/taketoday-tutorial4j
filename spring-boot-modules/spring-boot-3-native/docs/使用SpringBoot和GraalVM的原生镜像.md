## 一、概述

在本文中，我们将了解原生镜像以及如何从 Spring Boot 应用程序和 GraalVM 的原生镜像构建器创建原生镜像。我们指的是 Spring Boot 3，但我们将在文章末尾解决与 Spring Boot 2 的差异。

## 2.原生镜像

原生映像是一种将 Java 代码构建为独立可执行文件的技术。此可执行文件包括应用程序类、来自其依赖项的类、运行时库类以及来自 JDK 的静态链接原生代码。JVM 被打包到原生映像中，因此目标系统上不需要任何 Java 运行时环境，但构建工件是平台相关的。所以我们需要为每个支持的目标系统构建一个，当我们使用像 Docker 这样的容器技术时，这会更容易，我们可以在其中构建一个容器作为可以部署到任何 Docker 运行时的目标系统。

### 2.1. GraalVM和原生镜像生成器

通用递归应用和算法语言虚拟机(Graal VM) 是为 Java 和其他 JVM 语言编写的高性能 JDK 发行版，同时支持 JavaScript、Ruby、Python 和其他几种语言。它提供了一个Native Image构建器——一种从 Java 应用程序构建原生代码并将其与 VM 一起打包成独立可执行文件的工具。它由 Spring Boot [Maven](https://docs.spring.io/spring-boot/docs/3.0.0/maven-plugin/reference/htmlsingle/)和[Gradle](https://docs.spring.io/spring-boot/docs/3.0.0/gradle-plugin/reference/htmlsingle/)插件正式支持，但[有一些例外](https://github.com/spring-projects/spring-boot/wiki/Known-GraalVM-Native-Image-Limitations)（最糟糕的是 Mockito 目前不支持原生测试）。

### 2.2. 特殊功能

在构建原生镜像时，我们会遇到两个典型特征。

提前 (AOT) 编译是将高级 Java 代码编译为原生可执行代码的过程。通常，这是由 JVM 的即时编译器 (JIT) 在运行时完成的，它允许在执行应用程序时进行观察和优化。在 AOT 编译的情况下，这个优势就失去了。

通常，在 AOT 编译之前，可以有一个可选的单独步骤称为AOT 处理，即从代码中收集元数据并将它们提供给 AOT 编译器。分为这两个步骤是有道理的，因为 AOT 处理可以是特定于框架的，而 AOT 编译器更通用。下图给出了一个概览：

[![概述：原生构建步骤](https://www.baeldung.com/wp-content/uploads/2021/06/build.png)](https://www.baeldung.com/wp-content/uploads/2021/06/build.png)

Java 平台的另一个特点是它在目标系统上的可扩展性，只需将 JAR 放入类路径即可。由于启动时的反射和注释扫描，我们随后在应用程序中获得了扩展行为。

不幸的是，这会减慢启动时间并且不会带来任何好处，尤其是对于云原生应用程序，甚至服务器运行时和 Java 基类都被打包到 JAR 中。因此，我们省去了此功能，然后可以使用Closed World Optimization构建应用程序。

这两个功能都减少了需要在运行时执行的工作量。

### 2.3. 好处

原生镜像提供各种优势，如即时启动和减少内存消耗。它们可以打包到轻量级容器镜像中，以实现更快、更高效的部署，并且它们呈现出更小的攻击面。

### 2.4. 限制

由于封闭世界优化，存在一些[限制](https://www.graalvm.org/22.1/reference-manual/native-image/Limitations/)，我们在编写应用程序代码和使用框架时必须注意这些限制。不久：

-   可以在构建时执行类初始值设定项，以加快启动速度并提高峰值性能。但我们必须意识到，这可能会破坏代码中的一些假设，例如，当加载一个文件时，该文件必须在构建时可用。
-   反射和动态代理在运行时开销很大，因此在封闭世界假设下在构建时进行了优化。在构建时执行时，我们可以在类初始值设定项中不受限制地使用它。必须向 AOT 编译器声明任何其他用法，原生映像构建器会尝试通过执行静态代码分析来实现。如果失败，我们必须提供此信息，例如，通过[配置文件](https://www.graalvm.org/22.1/reference-manual/native-image/BuildConfiguration/)。
-   这同样适用于所有基于反射的技术，如 JNI 和序列化。
-   此外，Native Image 构建器提供了自己的本地接口，该接口比 JNI 简单得多且开销更低。
-   对于原生映像构建，字节码在运行时不再可用，因此无法使用针对 JVMTI 的工具进行调试和监视。然后我们必须使用原生调试器和监控工具。

关于 Spring Boot，我们必须意识到配置文件、条件 bean 和.enable属性等[功能在运行时不再完全受支持](https://docs.spring.io/spring-boot/docs/3.0.0/reference/htmlsingle/#native-image.introducing-graalvm-native-images.understanding-aot-processing)。如果我们使用配置文件，则必须在构建时指定它们。

## 3. 基本设置

在我们构建原生镜像之前，我们必须安装工具。

### 3.1. GraalVM 和原生镜像

[首先，我们按照安装说明](https://graalvm.github.io/native-build-tools/latest/graalvm-setup.html)安装当前版本的 GraalVM 和本机映像构建器。（Spring Boot 要求版本 22.3）我们应该确保安装目录可通过GRAALVM_HOME环境变量获得，并且“<GRAALVM_HOME>/bin”已添加到PATH变量中。

### 3.2. 原生编译器

在构建期间，原生映像构建器调用特定于平台的原生编译器。因此，我们需要这个本地编译器，遵循我们平台的[“先决条件”说明](https://www.graalvm.org/22.3/reference-manual/native-image/)。这将使构建平台依赖。我们必须知道，只能在特定于平台的命令行中运行构建。例如，使用 Git Bash 在 Windows 上运行构建将不起作用。我们需要改用 Windows 命令行。

### 3.3. 码头工人

作为先决条件，我们将确保安装[Docker，稍后需要它来运行原生镜像](https://www.baeldung.com/dockerizing-spring-boot-application)。Spring Boot Maven 和 Gradle 插件使用[Paketo Tiny Builder](https://github.com/paketo-buildpacks/tiny-builder)构建容器。

## 4. 使用 Spring Boot 配置和构建项目

在 Spring Boot 中使用原生构建功能非常简单。我们创建我们的项目，例如，通过使用[Spring Initializr](https://start.spring.io/)并添加应用程序代码。然后，要使用 GraalVM 的原生镜像构建器构建原生镜像，我们需要使用 GraalVM 本身提供的 Maven 或 Gradle 插件来扩展我们的构建。

### 4.1. 行家

[Spring Boot Maven 插件](https://docs.spring.io/spring-boot/docs/3.0.0/maven-plugin/reference/htmlsingle/)的目标是 AOT 处理（即，不是 AOT 编译本身，而是为 AOT 编译器收集元数据，例如，在代码中注册反射的使用）和构建可以与 Docker 一起运行的 OCI 镜像。我们可以直接调用这些目标：

```bash
mvn spring-boot:process-aot
mvn spring-boot:process-test-aot
mvn spring-boot:build-image
```

我们不需要这样做，因为 Spring Boot 父 POM 定义了一个将这些目标绑定到构建的原生配置文件。我们需要使用这个激活的配置文件进行构建：

```bash
mvn clean package -Pnative
```

如果我们还想执行原生测试，我们可以激活第二个配置文件：

```bash
mvn clean package -Pnative,nativeTest
```

如果我们要构建一个原生镜像，我们必须添加相应的native-maven-plugin目标。因此，我们也可以定义一个本地配置文件。因为这个插件是由父POM管理的，所以我们可以留下版本号：

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

目前，原生测试执行不支持 Mockito。因此，我们可以排除 Mocking 测试，或者通过将其添加到我们的 POM 来简单地跳过原生测试：

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

### 4.2. 在没有父 POM 的情况下使用 Spring Boot

如果我们不能从 Spring Boot Parent POM 继承，而是将其用作[import - scoped 依赖](https://docs.spring.io/spring-boot/docs/3.0.0/maven-plugin/reference/htmlsingle/#using.import)项，我们必须自己配置插件和配置文件。然后，我们必须将其添加到我们的 POM 中：

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

### 4.3. 摇篮

[Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/3.0.0/gradle-plugin/reference/htmlsingle/)为AOT 处理（即，不是 AOT 编译本身，而是为 AOT 编译器收集元数据，例如，在代码中注册反射的使用）和构建可以与 Docker 一起运行的 OCI 映像提供任务：

```bash
gradle processAot
gradle processTestAot
gradle bootBuildImage
```

如果我们想要构建原生镜像，我们必须添加[Gradle 插件来构建 GraalVM 原生镜像](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)：

```groovy
plugins {
    // ...
    id 'org.graalvm.buildtools.native' version '0.9.17'
}
```

然后，我们可以运行测试并通过调用构建项目

```groovy
gradle nativeTest
gradle nativeCompile
```

目前，原生测试执行不支持 Mockito。因此，我们可以通过配置graalvmNative扩展来排除 Mocking 测试或跳过原生测试，如下所示：

```groovy
graalvmNative {
    testSupport = false
}
```

## 5.扩展原生镜像构建配置

如前所述，我们必须为 AOT 编译器注册反射、类路径扫描、动态代理等的每个用法。因为内置原生支持Spring是一个很年轻的特性，目前并不是所有的Spring模块都有内置支持，所以目前需要我们自己来添加。这可以通过手动创建构建配置来完成。不过，使用 Spring Boot 提供的接口更容易，这样 Maven 和 Gradle 插件都可以在 AOT 处理期间使用我们的代码来生成构建配置。

指定额外原生配置的一种可能性是[Native Hints](https://docs.spring.io/spring-framework/docs/6.0.0/reference/html/core.html#aot-hints)。因此，让我们看一下当前缺少内置支持的两个示例，以及如何将其添加到我们的应用程序以使其正常工作。

### 5.1. 示例：Jackson 的PropertyNamingStrategy

在 MVC web 应用程序中，REST 控制器方法的每个返回值都由 Jackson 序列化，自动将每个属性命名为 JSON 元素。我们可以通过在应用程序属性文件中配置 Jackson 的 PropertyNamingStrategy 来全局影响名称映射：

```properties
spring.jacksonproperty-naming-strategy=SNAKE_CASE
```

SNAKE_CASE是PropertyNamingStrategies类型的静态成员的名称。不幸的是，这个成员是通过反射解决的。所以 AOT 编译器需要知道这一点，否则，我们会收到一条错误消息：

```bash
Caused by: java.lang.IllegalArgumentException: Constant named 'SNAKE_CASE' not found
  at org.springframework.util.Assert.notNull(Assert.java:219) ~[na:na]
  at org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
        $Jackson2ObjectMapperBuilderCustomizerConfiguration
        $StandardJackson2ObjectMapperBuilderCustomizer.configurePropertyNamingStrategyField(JacksonAutoConfiguration.java:287) ~[spring-features.exe:na]
```

为此，我们可以通过如下简单的方式实现和注册RuntimeHintsRegistrar ：

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

注意：自版本 3.0.0-RC2 以来，在 Spring Boot 中解决此问题的[拉取请求](https://github.com/spring-projects/spring-boot/pull/33080)已经合并，因此它可以开箱即用地与 Spring Boot 3 一起使用。

### 5.2. 示例：GraphQL 模式文件

如果我们想要[实现一个 GraphQL API](https://www.baeldung.com/spring-graphql)，我们需要创建一个模式文件并将其定位在“classpath:/graphql/.graphqls”下，Springs GraphQL 自动配置会自动检测到它。这是通过类路径扫描以及集成的 GraphiQL 测试客户端的欢迎页面完成的。因此，为了在原生可执行文件中正常工作，AOT 编译器需要知道这一点。我们可以用同样的方式注册：

```java
@ImportRuntimeHints(GraphQlRuntimeHints.GraphQlResourcesRegistrar.class)
@Configuration
public class GraphQlRuntimeHints {

    static class GraphQlResourcesRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.resources()
              .registerPattern("graphql//")
              .registerPattern("graphiql/index.html");
        }
    }

}
```

Spring GraphQL 团队已经[在着手解决这个问题](https://github.com/spring-projects/spring-graphql/issues/495)，所以我们可能会在未来的版本中内置它。

## 6. 编写测试

要测试RuntimeHintsRegistrar实现，我们甚至不需要运行 Spring Boot 测试，我们可以创建一个简单的 JUnit 测试，如下所示：

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

如果我们想通过集成测试来测试它，我们可以检查 Jackson ObjectMapper是否具有正确的配置：

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

要使用原生模式对其进行测试，我们必须运行原生测试：

```bash
# Maven
mvn clean package -Pnative,nativeTest
# Gradle
gradle nativeTest
```

如果我们需要为 Spring Boot 测试提供测试特定的 AOT 支持，我们可以使用[AotTestExecutionListener](https://docs.spring.io/spring-framework/docs/6.0.0/javadoc-api/org/springframework/test/context/aot/AotTestExecutionListener.html)[接口实现](https://docs.spring.io/spring-framework/docs/6.0.0/javadoc-api/org/springframework/test/context/aot/AotTestExecutionListener.html)[TestRuntimeHintsRegistrar](https://docs.spring.io/spring-framework/docs/6.0.0/javadoc-api/org/springframework/test/context/aot/TestRuntimeHintsRegistrar.html)或 [TestExecutionListener](https://www.baeldung.com/spring-testexecutionlistener) 。我们可以在[官方文档](https://docs.spring.io/spring-framework/docs/6.0.0/reference/html/testing.html#testcontext-aot)中找到详细信息。

## 7. 弹簧启动 2

Spring 6 和 Spring Boot 3 在原生镜像构建方面迈出了一大步。但是对于之前的大版本，这也是可以的。我们只需要知道还没有内置支持，即，有一个补充的[Spring Native 计划](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)来处理这个主题。因此，我们必须在我们的项目中手动包含和配置它。对于 AOT 处理，有一个单独的 Maven 和 Gradle 插件，它没有合并到 Spring Boot 插件中。当然，集成库并没有像现在这样提供本地支持（将来会更多）。

### 7.1. Spring 原生依赖

首先，我们必须为 Spring Native 添加 Maven 依赖：

```xml
<dependency>
    <groupId>org.springframework.experimental</groupId>
    <artifactId>spring-native</artifactId>
    <version>0.12.1</version>
</dependency>
```

但是，对于Gradle项目，Spring Native是由Spring AOT插件自动添加的。

我们应该注意，每个 Spring Native 版本仅支持特定的 Spring Boot 版本——例如，Spring Native 0.12.1 仅支持 Spring Boot 2.7.1。因此，我们应该确保在我们的pom.xml中使用兼容的 Spring Boot Maven 依赖项。

### 7.2. 构建包

要构建 OCI 映像，我们需要显式[配置构建包](https://www.baeldung.com/spring-boot-docker-images#buildpacks)。

对于 Maven，我们需要使用[Paketo Java buildpacks](https://paketo.io/docs/buildpacks/language-family-buildpacks/java/)的原生镜像配置的[spring-boot-maven-plugin](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-maven-plugin)：

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

在这里，我们将使用各种可用构建器中的微型构建器，例如base和full来构建原生镜像。此外，我们通过为BP_NATIVE_IMAGE环境变量提供真实值来启用 buildpack。

同样，在使用 Gradle 时，我们可以将tiny builder 连同BP_NATIVE_IMAGE环境变量添加到build.gradle文件中：

```plaintext
bootBuildImage {
    builder = "paketobuildpacks/builder:tiny"
    environment = [
        "BP_NATIVE_IMAGE" : "true"
    ]
}
```

### 7.3. Spring AOT 插件

接下来，我们需要添加执行[提前转换的](https://www.baeldung.com/ahead-of-time-compilation)[Spring AOT](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#spring-aot)插件，这有助于改善原生镜像的占用空间和兼容性。

因此，让我们将最新的[spring-aot-maven-plugin](https://repo.spring.io/artifactory/release/org/springframework/experimental/spring-aot-maven-plugin/) Maven 依赖项添加到我们的pom.xml中：

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

同样，对于一个Gradle项目，我们可以在build.gradle 文件中添加最新的[org.springframework.experimental.aot](https://repo.spring.io/artifactory/release/org/springframework/experimental/aot/org.springframework.experimental.aot.gradle.plugin/)依赖：

```plaintext
plugins {
    id 'org.springframework.experimental.aot' version '0.10.0'
}
```

此外，正如我们之前提到的，这会自动将 Spring Native 依赖项添加到 Gradle 项目中。

Spring AOT 插件提供[了几个选项来确定源代码生成](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/#spring-aot-configuration)。例如，像removeYamlSupport和removeJmxSupport这样的选项分别删除了[Spring Boot Yaml](https://www.baeldung.com/spring-yaml)和 Spring Boot [JMX](https://www.baeldung.com/java-management-extensions)支持。

### 7.4. 构建和运行镜像

就是这样！我们已准备好使用 Maven 命令构建我们的 Spring Boot 项目的原生映像：

```powershell
$ mvn spring-boot:build-image
```

### 7.5. 原生映像构建

接下来，我们将添加一个名为native的配置文件，其中包含一些插件的构建支持，例如[native-maven-plugin](https://search.maven.org/search?q=g:org.graalvm.buildtools a:native-maven-plugin)和spring-boot-maven-plugin：

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

此配置文件将在打包阶段从构建中调用本机映像编译器。

但是，在使用 Gradle 时，我们会将最新的[org.graalvm.buildtools.native](https://search.maven.org/search?q=g:org.graalvm.buildtools.native a:org.graalvm.buildtools.native.gradle.plugin)插件添加到build.gradle文件中：

```plaintext
plugins {
    id 'org.graalvm.buildtools.native' version '0.9.17'
}
```

就是这样！我们已准备好通过在 Maven包命令中提供原生配置文件来构建我们的原生映像：

```powershell
mvn clean package -Pnative
```

## 八、结论

在本教程中，我们探索了使用 Spring Boot 和 GraalVM 的原生构建工具构建原生镜像。我们了解了 Spring 的内置原生支持。