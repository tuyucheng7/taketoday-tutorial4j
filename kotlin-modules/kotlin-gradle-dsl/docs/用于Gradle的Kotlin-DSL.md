## 1. 概述

Groovy是编写[Gradle](https://www.baeldung.com/gradle)脚本的默认语言，但是，从Gradle 5.0版本开始，我们也可以使用Kotlin编写这些脚本。

在本教程中，我们将了解如何使用Kotlin编写Gradle脚本，我们还将了解使用Kotlin DSL脚本的一些优点和缺点。

## 2. 如何创建Kotlin DSL脚本

要编写Kotlin DSL脚本，我们需要使用Gradle 5.0或更高版本，要激活Kotlin DSL并获得最佳的IDE支持，我们需要使用以下扩展：

-   .gradle.kts而不是.gradle用于我们的构建脚本
-   .settings.gradle.kts而不是所有设置脚本的.settings.gradle
-   .init.gradle.kts而不是.init.gradle用于初始化脚本

## 3. 如何为Java库编写基本的Gradle脚本

在本节中，我们将介绍用Kotlin DSL脚本编写的Gradle脚本的不同构建块，我们还将研究与在Groovy DSL中编写相同脚本时的区别。

### 3.1 应用插件

我们可以应用java-library插件，这是一个核心插件：

```kotlin
plugins {
    `java-library`
}
```

请注意，我们不需要为核心插件指定id函数-这与Groovy DSL不同。

我们可以通过指定完全限定的插件ID和版本来应用社区插件：

```kotlin
plugins {
    id("org.flywaydb.flyway") version "8.0.2"
}
```

### 3.2 声明依赖

我们可以使用类型安全访问器声明不同类型的依赖：

```kotlin
dependencies {
    api("com.google.inject:guice:5.0.1")
    implementation("com.google.guava:guava:31.0-jre")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}
```

这里需要注意的重要一点是，**Kotlin DSL在plugins{}块之后立即在Gradle脚本的主体中提供所有四个访问器**，这意味着在编写此脚本时，我们将在受支持的IDE中进行类型安全的自动完成，这会带来快速而卓越的脚本体验。

### 3.3 声明仓库

我们可以使用类型安全访问器声明内置和自定义存储库：

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.springframework.org/release")
    }
}
```

在解析依赖项时，Gradle将首先检查maven-central仓库，然后检查springframework仓库。

### 3.4 配置源集

假设我们要为我们的集成测试定义一个单独的源集。项目布局为：

```bash
gradle-kotlin-dsl 
  ├── src 
  │    └── main 
  │    |    ├── java 
  │    |    │    ├── DefaultSorter.java
  │    |    │    ├── InMemoryRepository.java
  │    |    │    ├── Reporter.java
  │    |    │    ├── Repository.java
  │    |    │    ├── Sorter.java
  │    ├── test 
  │    |    ├── java 
  │    |    │    └── DefaultSorterTest.java
  │    |    │    └── InMemoryRepositoryTest.java
  │    |    │    └── ReporterTest.java
  │    └── integrationTest 
  │         └── java 
  │              └── ReporterIntegrationTest.java
  └── build.gradle
```

我们可以将integrationTest源集定义为：

```kotlin
sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}
```

通过此设置，我们创建了一个名为compileIntegrationTestJava的Gradle任务，使用此任务，我们可以编译src/integrationTest/java目录中的源文件。

### 3.5 定义自定义Gradle任务

我们需要一个任务来运行集成测试。我们可以使用Kotlin DSL创建它：

```kotlin
val integrationTest = task<Test>("integrationTest") {
    description = "Task to run integration tests"
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")
}
```

在这里，我们使用附加到Project对象的Kotlin扩展函数任务创建了一个自定义任务，这与Groovy语法不同，因为我们将使用TaskContainer对象来创建自定义任务。这在Kotlin DSL中也是可能的，但它更冗长。

## 4. IDE支持

由于Kotlin是一种静态类型语言，与Groovy不同，IDE可以提供多种有用的功能，例如自动完成、源代码导航、重构支持和Gradle脚本的错误突出显示，这意味着我们可以享受与通常使用Kotlin或Java编写应用程序代码时类似的编码体验。目前，IntelliJ IDEA和Android Studio为Kotlin DSL提供了最好的支持。

## 5. 限制

与Groovy相比，Kotlin DSL有一些局限性，尤其是在比较脚本编译速度时，[Gradle官方网站](https://docs.gradle.org/current/userguide/kotlin_dsl.html#kotdsl:limitations)上提到了其他一些不常见的限制。

## 6. 总结

在本文中，我们学习了如何使用Kotlin DSL编写Gradle脚本，我们还研究了用Groovy编写的Gradle脚本与用Kotlin DSL编写的脚本之间的一些差异。