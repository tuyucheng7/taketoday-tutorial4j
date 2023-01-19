## 一、简介

JVM 语言向其用户承诺：“一次编写——随处运行”。然而，当涉及到运输时，会出现许多小问题。生态系统已经发展，现在创建任何应用程序都离不开数十个依赖库。所有这些都必须在类路径中结束，如果没有——很可能，应用程序甚至不会启动。

有时我们可以负担得起创建另一个目录并将所有依赖项 jar 放在那里。但有时我们需要 uber-jar 或 fat-jar，它们可以使用java命令运行：

```bash
java -jar my-application.jar
```

[我们的站点上已经讨论了](https://www.baeldung.com/gradle-fat-jar#using-dedicated-plugins)使用 Gradle 实现该目标的方法之一。让我们讨论其他的可能性。我们还将在构建文件中使用 Kotlin Gradle DSL 而不是 Groovy。

## 2. 使用 Gradle 插件的轻量级应用

现在，“自执行 jar”可能有点用词不当。如果我们希望我们的应用程序可以在 Windows、Linux 和 macOS 平台上运行而无需任何 CLI 大惊小怪，我们可以使用标准的 Gradle 插件 –应用程序：

```kotlin
plugins {
    application // enabling the plugin here
    kotlin("jvm") version "1.6.0"
}

// Other configuration here

application {
    mainClass.set("the.path.to.the.MainClass")
}
```

我们唯一需要做的就是指向应用程序的主类——包含我们要在开始时调用的函数main(args: Array<String>)的类。

Application Plugin 还自动包含 Distribution Plugin。Distribution Plugin 创建两个档案：TAR 和 ZIP。它们的内容相同，包括项目 jar、所有依赖项 jar 和两个脚本：Bash 和.bat文件。然后，分发我们的应用程序完全没有问题：我们可以使用our-project/build/distributions/our-project-1.0.1.zip，解压它，然后运行可执行脚本：

```bash
unzip our-project-1.0.1.zip
./our-project-1.0.1/bin/our-project-1.0.1
```

这将在 Linux 和 macOS 上启动我们的应用程序。命令./our-project-1.0.1/bin/our-project-1.0.1.bat将在 Windows 上启动它。

然而，这种打包和分发软件的方式并不理想。事实上，我们必须假设目标主机上存在 TAR 或 ZIP 解压缩程序，以及有效的 JRE。如果我们想要传输最少数量的文件并以最直接的方式启动应用程序怎么办？

进入脂肪罐。

## 3. 轻量级应用的“Fat Jar”

发布我们的应用程序的下一个方法是构建应用程序插件，这在上一章中讨论过。我们将添加一个自定义任务来实现我们的目标。

如果我们不打算将我们的项目用作某个第三方项目的依赖项，那么我们可以将每个依赖项打包到一个 jar 中并发布：

```kotlin
tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources")) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) } // Provided we set it up in the application plugin configuration
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
}
```

仅当确实需要隐藏我们的依赖项时，才有必要使用 John Engelman 的[ShadowJar插件。](https://github.com/johnrengelman/shadow)遮蔽一个依赖项就是重写它的包名，以免与相同依赖项的另一个版本的重复类文件冲突。但是，可执行 jar 不太可能成为依赖项的候选者，我们很可能可以在每个项目中跳过它。

上面的代码在我们项目的build/libs目录中生成了另一个 jar 文件 ：our-project-1.0.1-standalone.jar。我们可以直接运行它：

```bash
java -jar our-project-1.0.1-standalone.jar
```

## 3. Spring Boot 应用

Spring Boot 应用程序开箱即用。Spring Boot 插件不像我们的[第二种方法](https://www.baeldung.com/kotlin/gradle-executable-jar#fat-jar-for-the-light-weight-application)那样重新打包所有的 jar，而是用另一个 jar 包装依赖项 jar。在这种情况下，项目的创建很简单。我们可以使用[Spring Initializr](https://start.spring.io/)，在我们的项目中选择我们需要的技术，并下载一个现成的项目。插件就是我们想要的：

```kotlin
plugins {
    id("org.springframework.boot") version "2.6.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
}
```

然后， 我们的应用程序必须是一个有效的 Spring Boot 应用程序，因为要操作类路径，Spring Boot 使用它的启动器。Gradle 构建成功后，我们将在build/libs目录中找到our-project-1.0.1.jar 。

```bash
java -jar our-project-1.0.1.jar
```

## 4。总结

根据项目、项目的大小和使用的技术，我们可能会选择解压缩的分发版、自建的胖 jar、具有阴影依赖项的 jar 或 Spring Boot 应用程序。在所有情况下，Gradle 都提供了简单的工具来实现我们的目标。