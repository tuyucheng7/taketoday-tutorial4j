## 1. 概述

**Spring Boot Gradle插件帮助我们管理Spring Boot依赖项，以及在使用Gradle作为构建工具时打包和运行我们的应用程序**。

在本教程中，我们将讨论如何添加和配置插件，然后我们将了解如何构建和运行Spring Boot项目。

## 2. 构建文件配置

首先，**我们需要将Spring Boot插件添加到我们的build.gradle文件中**，方法是将其包含在我们的plugins部分中：

```groovy
plugins {
    id "org.springframework.boot" version "2.0.1.RELEASE"
}
```

如果我们使用的是2.1之前的Gradle版本或者我们需要动态配置，我们可以像这样添加它：

```groovy
buildscript {
    ext {
        springBootVersion = '2.0.1.RELEASE'
    }
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}

apply plugin: 'org.springframework.boot'
```

## 3. 打包应用程序

**我们可以通过使用build命令构建它来将我们的应用程序打包为可执行存档(jar或war文件)**：

```shell
./gradlew build
```

因此，生成的可执行文件将被放置在build/libs目录中。

如果我们想生成一个可执行的jar文件，那么我们还需要应用java插件：

```groovy
apply plugin: 'java'
```

另一方面，如果我们需要一个war文件，我们将应用war插件：

```groovy
apply plugin: 'war'
```

构建应用程序将为Spring Boot 1.x和2.x生成可执行文件。但是，对于每个版本，Gradle会触发不同的任务。

接下来，让我们仔细看看每个Boot版本的构建过程。

### 3.1 Spring Boot 2.x

**在Boot 2.x中，bootJar和bootWar任务负责打包应用程序**。

bootJar任务负责创建可执行jar文件。这是在应用java插件后自动创建的。

让我们看看如何直接执行bootJar任务：

```shell
./gradlew bootJar
```

类似地，bootWar生成一个可执行的war文件，并在应用war插件后创建。

我们可以使用以下命令执行bootWar任务：

```shell
./gradlew bootWar
```

**请注意，对于Spring Boot 2.x，我们需要使用Gradle 4.0或更高版本**。

我们也可以配置这两个任务。例如，让我们使用mainClassName属性设置主类：

```groovy
bootJar {
    mainClassName = 'cn.tuyucheng.taketoday.Application'
}
```

或者，我们可以使用Spring Boot DSL中的相同属性：

```groovy
springBoot {
    mainClassName = 'cn.tuyucheng.taketoday.Application'
}
```

### 3.2 Spring Boot 1.x

**在Spring Boot 1.x中，bootRepackage负责创建可执行存档(jar或war文件)**，具体取决于配置。

我们可以直接使用以下命令执行bootRepackage任务：

```shell
./gradlew bootRepackage
```

与Boot 2.x版本类似，我们可以在我们的build.gradle中的bootRepackage任务中添加配置：

```groovy
bootRepackage {
    mainClass = 'com.example.demo.Application'
}
```

我们还可以通过将enabled选项设置为false来禁用bootRepackage任务：

```groovy
bootRepackage {
    enabled = false
}
```

## 4. 运行应用程序

构建应用程序后，我们可以**在生成的可执行jar文件上使用java -jar命令来运行它**：

```shell
java -jar build/libs/demo.jar
```

**Spring Boot Gradle插件还为我们提供了bootRun任务**，使我们能够运行应用程序而无需先构建它：

```shell
./gradlew bootRun
```

bootRun任务可以在build.gradle中简单配置。

例如，我们可以定义主类：

```groovy
bootRun {
    main = 'com.example.demo.Application'
}
```

## 5. 与其他插件的关系

### 5.1 依赖管理插件

对于Spring Boot 1.x，它曾经自动应用依赖管理插件。这将导入Spring Boot依赖项BOM，其作用类似于Maven的依赖项管理。

但是从Spring Boot 2.x开始，如果需要此功能，我们需要在我们的build.gradle中显式地应用它：

```groovy
apply plugin: 'io.spring.dependency-management'
```

### 5.2 Java插件

当我们应用java插件时，Spring Boot Gradle插件会执行多个操作，例如：

-   创建一个bootJar任务，我们可以用它来生成一个可执行的jar文件
-   创建一个bootRun任务，我们可以使用它来直接运行我们的应用程序
-   禁用jar任务

### 5.3 War插件

同样，当我们应用war插件时，会导致：

-   创建bootWar任务，我们可以使用该任务生成可执行的war文件
-   禁用war任务

## 6. 总结

在这个快速教程中，我们了解了Spring Boot Gradle插件及其不同的任务。

此外，我们还讨论了它如何与其他插件交互。