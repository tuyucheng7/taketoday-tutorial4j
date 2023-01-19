## 1. 概述

[Gradle](https://www.baeldung.com/gradle) 6.0 版本带来了多项新功能，有助于使我们的构建更加高效和健壮。这些功能包括改进的依赖管理、模块元数据发布、任务配置避免以及对 JDK 13 的支持。

在本教程中，我们将介绍 Gradle 6.0 中可用的新功能。我们的示例构建文件将使用 Gradle 的 Kotlin DSL。

## 2. 依赖管理改进

在最近几年的每个版本中，Gradle 都对项目管理依赖项的方式进行了增量改进。这些依赖性改进在 Gradle 6.0 中达到顶峰。让我们回顾一下现在稳定的依赖管理改进。

### 2.1. API和实现分离

)java-library)插件帮助我们创建一个可重用的Java库。该插件鼓励我们将属于我们库公共 API 的依赖项与作为实现细节的依赖项分开。))这种分离使构建更加稳定，因为用户不会意外引用不属于库公共 API 的类型。))

Gradle 3.4 中引入了java )-library)插件及其)api) 和)实现配置。)虽然这个插件不是 Gradle 6.0 的新功能，但它提供的增强的依赖管理功能是 Gradle 6.0 实现的全面依赖管理的一部分。

### 2.2. 丰富的版本

我们的项目依赖关系图通常具有相同依赖关系的多个版本。当发生这种情况时，Gradle 需要选择项目最终将使用哪个版本的依赖项。

Gradle 6.0 允许我们向依赖项添加[丰富的版本信息。](https://docs.gradle.org/6.0.1/userguide/rich_versions.html)))丰富的版本信息有助于 Gradle 在解决依赖冲突时做出最佳选择。))

例如，考虑一个依赖于 Guava 的项目。进一步假设这个项目使用 Guava 版本 28.1-jre，尽管我们知道它只使用从 10.0 版本开始稳定的 Guava API。

我们可以使用)require)声明告诉 Gradle 这个项目可以使用自 10.0 以来的任何版本的 Guava，并且我们使用)prefer) 声明告诉 Gradle 它应该使用 28.1-jre 如果没有其他限制阻止它这样做。)because)声明添加了注解来解释这个丰富的版本信息：

```plaintext
implementation("com.google.guava:guava") {
    version {
        require("10.0")
        prefer("28.1-jre")
        because("Uses APIs introduced in 10.0. Tested with 28.1-jre")
    }
}
```

这如何帮助我们的构建更稳定？假设这个项目还依赖了一个必须使用 Guava 16.0 版本的依赖)foo 。))foo)项目的构建文件会将依赖项声明为：

```plaintext
dependencies {
    implementation("com.google.guava:guava:16.0")
}
```

由于)foo)项目依赖于 Guava 16.0，而我们的项目同时依赖于 Guava 28.1-jre 和)foo)版本，所以我们有冲突。))Gradle 的默认行为是选择最新版本。然而，在这种情况下，选择最新版本是错误的选择))，因为)foo)必须使用版本 16.0.

在 Gradle 6.0 之前，用户必须自己解决冲突。因为 Gradle 6.0 允许我们告诉 Gradle 我们的项目可能使用低至 10.0 的 Guava 版本，Gradle 会正确解决这个冲突并选择 16.0 版本。

除了)require)和)prefer)声明之外，我们还可以使用)strict)和)reject)声明。)严格)声明描述了我们的项目必须使用的依赖版本范围。)拒绝)声明描述了与我们的项目不兼容的依赖版本。

如果我们的项目依赖于我们知道将在 Guava 29 中删除的 API，那么我们使用)严格)声明来防止 Gradle 使用大于 28 的 Guava 版本。同样，如果我们知道 Guava 27.0 中存在一个错误导致我们项目的问题，我们使用)reject)来排除它：

```plaintext
implementation("com.google.guava:guava") {
    version {
        strictly("[10.0, 28[")
        prefer("28.1-jre")
        reject("27.0")
        because("""
            Uses APIs introduced in 10.0 but removed in 29. Tested with 28.1-jre.
            Known issues with 27.0
        """)
    }
}
```

### 2.3. 平台

)java-platform)插件允许我们跨项目重用一组依赖约束。平台作者声明一组紧密耦合的依赖项，其版本由平台控制。

))依赖于平台的项目不需要为平台控制的任何依赖项指定版本。))Maven 用户会发现这类似于 Maven 父 POM 的)dependencyManagement)特性。

平台在多项目构建中特别有用。多项目构建中的每个项目都可能使用相同的外部依赖项，我们不希望这些依赖项的版本不同步。

让我们创建一个新平台，以确保我们的多项目构建跨项目使用相同版本的 Apache HTTP 客户端。首先，我们创建一个使用)java-platform)插件的项目)httpclient-platform ：)

```plaintext
plugins {
    `java-platform`
}
```

接下来，我们声明对该平台中包含))的依赖项的约束。))在此示例中，我们将选择要在项目中使用的 Apache HTTP 组件的版本：

```plaintext
dependencies {
    constraints {
        api("org.apache.httpcomponents:fluent-hc:4.5.10")
        api("org.apache.httpcomponents:httpclient:4.5.10")
    }
}
```

最后，让我们添加一个使用 Apache HTTP Client Fluent API 的)person-rest-client)项目。在这里，我们使用)platform)方法添加对我们的)httpclient-platform)项目的依赖。我们还将添加对)org.apache.httpcomponents:fluent-hc)的依赖。此依赖项不包括版本，因为)httpclient-platform)确定要使用的版本：

```plaintext
plugins {
    `java-library`
}

dependencies {
    api(platform(project(":httpclient-platform")))
    implementation("org.apache.httpcomponents:fluent-hc")
}
```

)))java-platform)插件有助于避免由于构建中的依赖关系未对齐而在运行时出现不受欢迎的意外。))

### 2.4. 测试夹具

在 Gradle 6.0 之前，想要跨项目共享测试装置的构建作者将这些装置提取到另一个库项目。现在，))构建作者可以使用)java-test-fixtures)插件从他们的项目中发布测试装置。))

让我们构建一个定义抽象的库，并发布测试装置来验证该抽象所期望的契约。

在这个例子中，我们的抽象是一个 Fibonacci 数列生成器，测试夹具是一个[JUnit 5 测试混入](https://junit.org/junit5/docs/current/user-guide/#writing-tests-test-interfaces-and-default-methods)。Fibonacci 序列生成器的实现者可以使用测试混合来验证他们是否正确地实现了序列生成器。

首先，让我们为我们的抽象和测试装置创建一个新项目)fibonacci-spi 。)这个项目需要)java-library)和)java-test-fixtures)插件：

```plaintext
plugins {
    `java-library`
    `java-test-fixtures`
}
```

接下来，让我们将 JUnit 5 依赖项添加到我们的测试装置中。正如)java-library)插件定义)api)和)实现)配置一样，)java-test-fixtures)插件定义)testFixturesApi)和)testFixturesImplementation)配置：

```plaintext
dependencies {
    testFixturesApi("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}
```

有了我们的依赖项，让我们将 JUnit 5 测试混合添加到)java-test-fixtures)插件创建的)src/testFixtures/java)源集中。这个测试混合验证我们的)FibonacciSequenceGenerator)抽象的契约：

```java
public interface FibonacciSequenceGeneratorFixture {

    FibonacciSequenceGenerator provide();

    @Test
    default void whenSequenceIndexIsNegative_thenThrows() {
        FibonacciSequenceGenerator generator = provide();
        assertThrows(IllegalArgumentException.class, () -> generator.generate(-1));
    }

    @Test
    default void whenGivenIndex_thenGeneratesFibonacciNumber() {
        FibonacciSequenceGenerator generator = provide();
        int[] sequence = { 0, 1, 1, 2, 3, 5, 8 };
        for (int i = 0; i < sequence.length; i++) {
            assertEquals(sequence[i], generator.generate(i));
        }
    }
}
```

))这就是我们与其他项目共享此测试夹具))所需要做的全部工作。

现在，让我们创建一个新项目)fibonacci-recursive)，它将重用这个测试夹具。)该项目将使用dependencies)块中的)testFixtures)方法声明对来自我们)fibonacci-spi)项目的测试装置的依赖：

```plaintext
dependencies {
    api(project(":fibonacci-spi"))
    
    testImplementation(testFixtures(project(":fibonacci-spi")))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}
```

最后，我们现在可以使用)fibonacci-spi)项目中定义的测试混合来为我们的递归斐波那契序列生成器创建一个新测试：

```java
class RecursiveFibonacciUnitTest implements FibonacciSequenceGeneratorFixture {
    @Override
    public FibonacciSequenceGenerator provide() {
        return new RecursiveFibonacci();
    }
}
```

Gradle 6.0 )java-test-fixtures)插件))让构建作者更灵活地跨项目共享他们的测试装置))。

## 3. Gradle模块元数据发布

传统上，Gradle 项目将构建工件发布到 Ivy 或 Maven 存储库。这包括分别生成 ivy.xml 或 pom.xml 元数据文件。

ivy.xml 和 pom.xml 模型无法存储我们在本文中讨论的丰富的依赖信息。这意味着))当我们将库发布到 Maven 或 Ivy 存储库时，下游项目无法从这些丰富的依赖信息中获益))。

Gradle 6.0 通过引入[Gradle Module Metadata 规范](https://github.com/gradle/gradle/blob/master/subprojects/docs/src/docs/design/gradle-module-metadata-latest-specification.md)弥补了这一差距。))Gradle Module Metadata 规范是一种 JSON 格式，支持存储 Gradle 6.0 中引入的所有增强模块依赖元数据。))

除了传统的 ivy.xml 和 pom.xml 元数据文件之外，项目还可以构建此元数据文件并将其发布到 Ivy 和 Maven 存储库。这种向后兼容性允许 Gradle 6.0 项目在不破坏遗留工具的情况下))利用此模块元数据(如果存在) 。))

要发布 Gradle 模块元数据文件，项目必须使用新的[Maven Publish Plugin](https://docs.gradle.org/current/userguide/publishing_maven.html)或[Ivy Publish Plugin](https://docs.gradle.org/current/userguide/publishing_ivy.html)。从 Gradle 6.0 开始，这些插件默认发布 Gradle 模块元数据文件。这些插件取代了[旧版发布系统。](https://docs.gradle.org/current/userguide/artifact_management.html)

### 3.1. 将 Gradle 模块元数据发布到 Maven

让我们配置一个构建以将 Gradle 模块元数据发布到 Maven。首先，我们在构建文件中包含)maven-publish ：)

```plaintext
plugins {
    `java-library`
    `maven-publish`
}
```

接下来，我们配置发布。发布可以包含任意数量的工件。)让我们添加与java)配置关联的工件：

```plaintext
publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}
```

)maven-publish)插件添加了)publishToMavenLocal)任务。让我们使用此任务来测试我们的 Gradle 模块元数据发布：

```shell
./gradlew publishToMavenLocal
```

接下来，让我们在本地 Maven 存储库中列出此工件的目录：

```plaintext
ls ~/.m2/repository/com/baeldung/gradle-6/1.0.0/
gradle-6-1.0.0.jar	gradle-6-1.0.0.module	gradle-6-1.0.0.pom
```

正如我们在控制台输出中看到的，除了 Maven POM 之外，Gradle 还生成了模块元数据文件。

## 4.配置规避API

自 5.1 版以来，Gradle 鼓励插件开发人员使用新的、正在孵化的配置规避 API。))这些 API 有助于构建尽可能避免相对较慢的任务配置步骤))。Gradle 将此性能改进称为[Task Configuration Avoidance](https://docs.gradle.org/current/userguide/task_configuration_avoidance.html)。Gradle 6.0 将这个正在孵化的 API 提升为稳定版。

虽然 Configuration Avoidance 功能主要影响插件作者，但在其构建中创建任何自定义)Configuration)、)Task)或)Property)的构建作者也会受到影响。插件作者和构建作者现在可以))使用新的[惰性配置 API](https://docs.gradle.org/current/userguide/lazy_configuration.html)来包装具有)Provider)类型的对象，这样 Gradle 将避免在需要这些对象之前“实现”这些对象))。

让我们使用惰性 API 添加自定义任务。首先，我们使用)TaskContainer.registering)扩展方法注册任务。由于)注册)返回一个)TaskProvider)，因此)Task)实例的创建被推迟到 Gradle 或构建作者调用)TaskProvider.get())。最后，我们提供了一个闭包，它将在 Gradle 创建)任务)后配置我们的任务：

```plaintext
val copyExtraLibs by tasks.registering(Copy::class) {
    from(extralibs)
    into(extraLibsDir)
}
```

Gradle 的 Task Configuration Avoidance [Migration Guide](https://docs.gradle.org/current/userguide/task_configuration_avoidance.html#sec:task_configuration_avoidance_migration_guidelines)帮助插件作者和构建作者迁移到新的 API。构建作者最常见的迁移包括：

-   )tasks.register)而不是)tasks.create)
-   )tasks.named)而不是)tasks.getByName)
-   )configurations.register)而不是)configurations.create)
-   )project.layout.buildDirectory.dir(“foo”))而不是)File(project.buildDir, “foo”))

## 5. JDK 13 支持

Gradle 6.0 引入了对使用 JDK 13 构建项目的支持。我们可以使用熟悉的)sourceCompatibility)和)targetCompatibility)设置将Java构建配置为使用Java13：

```plaintext
sourceCompatibility = JavaVersion.VERSION_13
targetCompatibility = JavaVersion.VERSION_13
```

JDK 13 的一些))最令人兴奋的语言功能，例如原始字符串文字，仍处于预览状态))。让我们在Java构建中配置任务以启用这些预览功能：

```plaintext
tasks.compileJava {
    options.compilerArgs.add("--enable-preview")
}
tasks.test {
    jvmArgs.add("--enable-preview")
}
tasks.javadoc {
    val javadocOptions = options as CoreJavadocOptions
    javadocOptions.addStringOption("source", "13")
    javadocOptions.addBooleanOption("-enable-preview", true)
}
```

## 六. 总结

在本文中，我们讨论了 Gradle 6.0 中的一些新功能。

我们介绍了增强的依赖关系管理、发布 Gradle 模块元数据、避免任务配置，以及早期采用者如何配置他们的构建以使用Java13 预览语言功能。