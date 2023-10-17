## 1. 概述

在本教程中，我们研究在Gradle构建脚本中声明依赖项；对于我们的示例，我们将使用Gradle 7.4。

## 2. 常规结构

首先，我们从一个用于Java项目的简单Gradle脚本开始：

```groovy
plugins {
    id 'java'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter:2.6.1'
    testImplementation 'org.springframework.boot:spring-boot-starter-test:2.6.1'
}
```

如上所示，我们有三个代码块：plugins、repositories和dependencies。

首先，plugins块告诉我们这是一个Java项目。其次，dependencies块声明了编译项目生产源代码所需的spring-boot-starter依赖项的2.6.1版本。此外，它还指出项目的测试套件需要spring-boot-starter-test才能编译。

Gradle构建从Maven中央仓库中获取所有依赖项，由repositories块定义。

## 3. 依赖项配置

我们可以在不同的配置中声明依赖项。在这方面，我们可以选择或多或少精确，稍后我们会看到。

### 3.1 如何声明依赖

首先，配置有4个部分：

+ ))group)) – 组织、公司或项目的标识符
+ ))name)) - 依赖标识符
+ ))version)) – 我们要导入的版本
+ ))classifier)) - 用于区分具有相同group、name和version的依赖项

我们可以通过两种格式声明依赖关系，简短格式允许我们将依赖项声明为字符串：

```groovy
implementation 'org.springframework.boot:spring-boot-starter:2.6.1'
```

相反，扩展格式允许我们将其编写为Map：

```groovy
implementation group: 'org.springframework.boot', name: 'spring-boot-starter', version: '2.6.1'
```

### 3.2 配置类型

此外，Gradle提供了许多依赖配置类型：

+ ))api)) – 用于使依赖项显式化并在类路径中公开它们。例如，当实现对库消费者透明的库时。
+ ))implementation)) - 编译生产源代码所必需的，并且纯粹是内部的。它们不会暴露在包之外。
+ ))compileOnly)) - 当它们只需要在编译时声明时使用，例如源注解或注解处理器。它们不会出现在运行时类路径或测试类路径中。
+ ))compileOnlyApi)) – 在编译时需要以及需要在类路径中对消费者可见时使用。
+ ))runtimeOnly)) - 用于声明仅在运行时需要且在编译时不可用的依赖项。
+ ))testImplementation)) - 用于声明编译测试代码所需的依赖。
+ ))testCompileOnly)) – 仅在测试编译时需要。
+ ))testRuntimeOnly)) – 仅在测试运行时需要。

我们应该注意到，最新版本的Gradle弃用了一些配置，如compile、testCompile、runtime和testRuntime。在撰写本文时，它们仍然可用。

## 4. 外部依赖的类型

让我们深入研究在Gradle构建脚本中遇到的外部依赖项的类型。

### 4.1 模块依赖

基本上，声明依赖项的最常见方法是引用仓库；Gradle仓库是按group、name和version组织的模块集合。

事实上，Gradle会从repositories块中的指定仓库中拉取依赖项：

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter:2.6.1'
}
```

### 4.2 文件依赖

考虑到项目并不总是使用自动化的依赖项管理，一些项目将依赖项组织为源代码或本地文件系统的一部分。因此，我们需要指定依赖项所在的确切位置。

为此，我们可以使用files来包含依赖项集合：

```groovy
dependencies {
    runtimeOnly files('libs/sampleOne.jar', 'libs/sampleTwo.jar')
}
```

同样，我们可以使用fileTree在目录中包含jar文件的层次结构：

```groovy
dependencies {
    runtimeOnly fileTree('libs') { include ').jar' }
}
```

### 4.3 项目依赖

由于一个项目可以依赖另一个项目来重用代码，Gradle为我们提供了这样的功能。

假设我们要声明我们的项目依赖于shared项目：

```groovy
dependencies {
    implementation project(':shared')
}
```

### 4.4 Gradle依赖

在某些情况下，例如开发任务或插件，我们可以定义属于我们正在使用的Gradle版本的依赖项：

```groovy
dependencies {
    implementation gradleApi()
}
```

## 5. buildScript

正如我们之前看到的，我们可以在dependencies块中声明源代码和测试的外部依赖项。同样，buildScript块允许我们声明Gradle构建的依赖项，例如第三方插件和任务类。特别是，如果没有buildScript块，我们只能使用Gradle现成的特性。

下面我们声明我们要通过从Maven Central下载来使用Spring Boot插件：

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'org.springframework.boot:spring-boot-gradle-plugin:2.6.1'
    }
}
apply plugin: 'org.springframework.boot'
```

因此，我们需要指定下载外部依赖项的源，因为没有默认源。

上述内容是在Gradle旧版本中可用的；相反，在较新的版本中，可以使用更简洁的形式：

```groovy
plugins {
    id 'org.springframework.boot' version '2.6.1'
}
```

## 6. 总结

在本文中，我们介绍了Gradle依赖项、如何声明它们以及不同的配置类型。