## 一、简介

在本教程中，我们将了解如何使用[spring-boot-thin-launcher](https://github.com/dsyer/spring-boot-thin-launcher)项目将 Spring Boot 项目构建到瘦 JAR 文件中。

Spring Boot 以其“胖”JAR 部署而闻名，其中单个可执行工件包含应用程序代码及其所有依赖项。

Boot 还广泛用于开发微服务。这有时可能与“胖 JAR”方法不一致，因为在许多工件中一遍又一遍地包含相同的依赖关系会成为一种重要的资源浪费。

## 2.先决条件

首先，我们当然需要一个 Spring Boot 项目。在本文中，我们将了解 Maven 构建和 Gradle 构建最常见的配置。

不可能涵盖所有的构建系统和构建配置，但希望我们能充分了解一般原则，以便你能够将它们应用到你的特定设置中。

### 2.1. Maven 项目

在使用 Maven 构建的 Boot 项目中，我们应该在项目的pom.xml文件、其父项或其祖先之一中配置 Spring Boot Maven 插件：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>    
</plugin>
```

Spring Boot 依赖项的版本通常通过使用 BOM 或从父 POM 继承来决定，如我们的参考项目中所示：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.4.0</version>
    <relativePath/>
</parent>
```

### 2.2. 摇篮项目

在使用 Gradle 构建的 Boot 项目中，我们将拥有 Boot Gradle 插件：

```groovy
buildscript {
    ext {
        springBootPlugin = 'org.springframework.boot:spring-boot-gradle-plugin'
        springBootVersion = '2.4.0'
    }
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("${springBootPlugin}:${springBootVersion}")
    }
}

// elided

apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

springBoot {
    mainClassName = 'com.baeldung.DemoApplication'
}
```

请注意，在本文中，我们将只考虑 Boot 2.x 和更高版本的项目。Thin Launcher 也支持早期版本，但它需要略有不同的 Gradle 配置，为简单起见我们将其省略。请查看该项目的主页以获取更多详细信息。

## 3.如何创建瘦JAR？

Spring Boot Thin Launcher 是一个小型库，它从存档本身中捆绑的文件中读取工件的依赖项，从 Maven 存储库下载它们，最后启动应用程序的主类。

因此，当我们使用该库构建项目时，我们会得到一个包含代码的 JAR 文件、一个枚举其依赖项的文件以及执行上述任务的库中的主类。

当然，事情比我们简化的解释要微妙一些；我们将在本文后面深入讨论一些主题。

## 4. 基本用法

现在让我们看看如何从我们的常规 Spring Boot 应用程序构建一个“瘦”JAR。

我们将使用通常的java -jar <my-app-1.0.jar> 启动应用程序，并使用可选的附加命令行参数来控制 Thin Launcher。我们将在以下部分中看到其中的几个；该项目的主页包含完整列表。

### 4.1. Maven 项目

在 Maven 项目中，我们必须修改 Boot 插件的声明(参见第 2.1 节)以包含对自定义“瘦”布局的依赖：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <dependencies>
        <!-- The following enables the "thin jar" deployment option. -->
        <dependency>
            <groupId>org.springframework.boot.experimental</groupId>
            <artifactId>spring-boot-thin-layout</artifactId>
            <version>1.0.11.RELEASE</version>
        </dependency>
    </dependencies>
</plugin>
```

[启动器](https://search.maven.org/classic/#search|ga|1|a%3A"spring-boot-thin-layout")将从pom.xml 文件中读取依赖项，Maven 存储在META-INF/maven目录中生成的 JAR 中。

我们将像往常一样执行构建，例如，使用mvn install。

如果我们希望能够生成瘦构建和胖构建(例如在具有多个模块的项目中)，我们可以在专用的 Maven 配置文件中声明自定义布局。

### 4.2. Maven 和依赖项：thin.properties

除了pom.xml之外，我们还可以让 Maven 生成一个thin.properties文件。在这种情况下，该文件将包含完整的依赖项列表，包括可传递的依赖项，并且启动器会优先使用它而不是pom.xml。

这样做的 mojo(插件)是spring-boot-thin-maven-plugin:properties，默认情况下，它在src/main/resources/META-INF中输出thin.properties文件，但我们可以指定它的位置thin.output属性：

```shell
$ mvn org.springframework.boot.experimental:spring-boot-thin-maven-plugin:properties -Dthin.output=.
```

请注意，输出目录必须存在才能使目标成功，即使我们保留默认目录也是如此。

### 4.3. 摇篮项目

相反，在 Gradle 项目中，我们添加了一个专用插件：

```groovy
buildscript {
    ext {
        //...
        thinPlugin = 'org.springframework.boot.experimental:spring-boot-thin-gradle-plugin'
        thinVersion = '1.0.11.RELEASE'
    }
    //...
    dependencies {
        //...
        classpath("${thinPlugin}:${thinVersion}")
    }
}

//elided

apply plugin: 'maven'
apply plugin: 'org.springframework.boot.experimental.thin-launcher'
```

为了获得精简构建，我们将告诉 Gradle 执行thinJar任务：

```shell
~/projects/baeldung/spring-boot-gradle $ ./gradlew thinJar
```

### 4.4. Gradle 和依赖项：pom.xml

在上一节的代码示例中，除了 Thin Launcher(以及我们已经在先决条件部分中看到的引导和依赖管理插件)之外，我们还声明了 Maven 插件。

这是因为，就像我们之前看到的 Maven 案例一样，工件将包含并使用pom.xml文件来枚举应用程序的依赖项。pom.xml文件由名为thinPom的任务生成，它是任何 jar 任务的隐式依赖项。

我们可以通过专门的任务自定义生成的pom.xml文件。在这里，我们将只瘦插件已经自动完成的工作：

```groovy
task createPom {
    def basePath = 'build/resources/main/META-INF/maven'
    doLast {
        pom {
            withXml(dependencyManagement.pomConfigurer)
        }.writeTo("${basePath}/${project.group}/${project.name}/pom.xml")
    }
}
```

要使用我们的自定义pom.xml文件，我们将上述任务添加到 jar 任务的依赖项中：

```groovy
bootJar.dependsOn = [createPom]
```

### 4.5. Gradle 和依赖项：thin.properties

我们还可以让 Gradle 生成一个thin.properties文件而不是pom.xml，就像我们之前使用 Maven 所做的那样。

生成thin.properties文件的任务称为thinProperties，默认情况下不使用它。我们可以将其添加为 jar 任务的依赖项：

```groovy
bootJar.dependsOn = [thinProperties]
```

## 5. 存储依赖

thin jar 的全部意义在于避免将依赖项与应用程序捆绑在一起。但是，依赖项不会神奇地消失，它们只是存储在别处。

特别是，Thin Launcher 使用 Maven 基础架构来解决依赖关系，因此：

1.  它检查本地 Maven 存储库，默认情况下位于~/.m2/repository但可以移动到其他地方；
2.  然后，它从 Maven Central(或任何其他配置的存储库)下载缺少的依赖项；
3.  最后，它将它们缓存在本地存储库中，这样下次我们运行应用程序时就不必再次下载它们了。

当然，下载阶段是过程中缓慢且容易出错的部分，因为它需要通过 Internet 访问 Maven Central，或者访问本地代理，我们都知道那些东西通常是不可靠的。

幸运的是，有多种方法可以将依赖项与应用程序一起部署，例如在用于云部署的预打包容器中。

### 5.1. 运行应用程序进行热身

缓存依赖项的最简单方法是在目标环境中对应用程序进行预热运行。正如我们之前看到的，这将导致依赖项被下载并缓存在本地 Maven 存储库中。如果我们运行多个应用程序，存储库最终将包含所有依赖项，而不会重复。

由于运行应用程序可能会产生不需要的副作用，我们还可以执行“空运行”，只解析和下载依赖项而不运行任何用户代码：

```shell
$ java -Dthin.dryrun=true -jar my-app-1.0.jar
```

请注意，根据 Spring Boot 约定，我们还可以使用应用程序的–thin.dryrun命令行参数或THIN_DRYRUN系统属性来设置 -Dthin.dryrun 属性。除false之外的任何值都将指示 Thin Launcher 执行空运行。

### 5.2. 在构建期间打包依赖项

另一种选择是在构建期间收集依赖项，而不将它们捆绑在 JAR 中。然后，作为部署过程的一部分，我们可以将它们到目标环境。

这通常更简单，因为不需要在目标环境中运行应用程序。但是，如果我们要部署多个应用程序，则必须手动或使用脚本合并它们的依赖项。

Maven 和 Gradle 的瘦插件在构建期间打包依赖项的格式与 Maven 本地存储库相同：

```plaintext
root/
    repository/
        com/
        net/
        org/
        ...
```

事实上，我们可以在运行时使用 Thin Launcher 将应用程序指向任何此类目录(包括本地 Maven 存储库)，并使用thin.root属性：

```shell
$ java -jar my-app-1.0.jar --thin.root=my-app/deps
```

我们还可以安全地合并多个这样的目录，方法是将它们一个接一个地，从而获得一个具有所有必要依赖项的 Maven 存储库。

### 5.3. 使用 Maven 打包依赖

为了让 Maven 为我们打包依赖项，我们使用spring-boot-thin-maven-plugin的resolve目标。我们可以在我们的pom.xml 中手动或自动调用它：

```xml
<plugin>
    <groupId>org.springframework.boot.experimental</groupId>
    <artifactId>spring-boot-thin-maven-plugin</artifactId>
    <version>${thin.version}</version>
    <executions>
        <execution>
        <!-- Download the dependencies at build time -->
        <id>resolve</id>
        <goals>
            <goal>resolve</goal>
        </goals>
        <inherited>false</inherited>
        </execution>
    </executions>
</plugin>
```

构建项目后，我们将找到一个目录target/thin/root/，其结构与我们在上一节中讨论过的一样。

### 5.4. 使用 Gradle 打包依赖

相反，如果我们将 Gradle 与thin-launcher插件一起使用，我们将有一个thinResolve任务可用。该任务会将应用程序及其依赖项保存在build/thin/root/目录中，类似于上一节的 Maven 插件：

```shell
$ gradlew thinResolve
```

## 6. 总结和延伸阅读

在这篇文章中，我们研究了如何制作我们的薄罐子。我们还了解了如何使用 Maven 基础结构来下载和存储它们的依赖项。

thin launcher的[主页](https://github.com/dsyer/spring-boot-thin-launcher)有一些更多的 HOW-TO 指南，用于 Heroku 的云部署等场景，以及支持的命令行参数的完整列表。

所有 Maven 示例和代码片段的实现都可以在[GitHub 项目](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-bootstrap)中找到——作为一个 Maven 项目，因此它应该很容易导入和运行。

同样，所有 Gradle 示例都引用[这个 GitHub 项目](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-gradle)。