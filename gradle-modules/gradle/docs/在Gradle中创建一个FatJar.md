## 1. 概述

在这篇文章中，我们介绍在Gradle中创建一个“fat jar”。基本上，))fat jar(也称为uber-jar)是一个完整的包，其中包含运行应用程序所需的类和依赖项))。

## 2. 初始设置

让我们从一个包含两个依赖项的Java项目的简单build.gradle文件开始：

```groovy
apply plugin: 'java'

repositories {
    mavenCentral()
}

dependencies {
    implementation group: 'org.slf4j', name: 'slf4j-api', version: '1.7.32'
    implementation group: 'org.slf4j', name: 'slf4j-simple', version: '1.7.32'
}
```

## 3. 使用Java插件中的Jar任务

让我们从修改Java Gradle插件的jar任务开始。默认情况下，此任务生成没有任何依赖关系的jars。

我们可以通过添加几行代码来覆盖这种行为，通过指定以下内容：

+ Manifest文件中的Main-Class属性
+ 包含依赖项jar

让我们对jar任务进行一些修改：

```groovy
jar {
    manifest {
        attributes "Main-Class": "cn.tuyucheng.taketoday.fatjar.Application"
    }

    from {
        configurations.compile.collect { it.isDirectory() ? it : zipTree(it) }
    }
}
```

## 4. 创建单独的任务

如果我们想保持原来的jar任务不变，我们可以创建一个单独的任务来实现相同的目的。

以下代码添加一个名为customFatJar的新任务：

```groovy
task customFatJar(type: Jar) {
    manifest {
        attributes 'Main-Class': 'cn.tuyucheng.taketoday.fatjar.Application'
    }
    baseName = 'all-in-one-jar'
    from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
    with jar
}
```

## 5. 使用专用插件

我们还可以使用现有的Gradle插件来构建一个fat jar。在本例中，我们将使用Shadow插件：

```groovy
buildscript {
    repositories {
        maven { url 'https://maven.aliyun.com/nexus/content/groups/public/' }
        maven { url 'https://repo.spring.io/milestone' }
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath 'com.github.jengelman.gradle.plugins:shadow:2.0.1'
    }
}

apply plugin: 'java'
apply plugin: 'com.github.johnrengelman.shadow'
```

一旦我们应用了Shadow插件，shadowJar任务就可以使用了。

## 6. 总结

在本教程中，我们介绍了几种在Gradle中创建fat jar的不同方法。包括覆盖默认的jar任务，创建一个单独的任务和使用shadow插件。推荐哪种方法？答案是视情况而定。

在简单的项目中，覆盖默认的jar任务或创建一个新任务就足够了。但随着项目的发展，我们强烈建议使用插件，因为它们已经解决了更棘手的问题，例如与外部META-INF文件的冲突。