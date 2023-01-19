## 1. 概述

在本文中，我们介绍))sourceCompatibility和targetCompatibility Java配置之间的区别))以及它们在Gradle中的用法。

你可以阅读我们的[Gradle简介](https://www.baeldung.com/gradle)文章以了解有关基础知识的更多信息。

## 2. 在Java中处理版本

当我们使用javac编译Java程序时，我们可以为版本处理提供编译参数。有两种选择：

-   -))source))：))它的值与Java版本匹配，直到我们用于编译的JDK))(例如，JDK8为1.8)。我们))提供的版本值将限制我们可以在源代码中使用的语言特性))，使其仅限于各自的Java版本。
-   -))target))：类似地，它控制生成的类文件的版本。这意味着我们提供的版本值将是我们的))程序可以运行的最低Java版本))。

例如：

```bash
javac HelloWorld.java -source 1.6 -target 1.8
```

上面的命令会))生成一个需要Java 8或更高版本才能运行的class文件。此外，源代码不能包含lambda表达式或Java 6中不可用的任何功能))。

## 3. 使用Gradle处理版本

Gradle和Java插件允许我们使用java任务的sourceCompatibility和targetCompatibility配置来设置source和target参数。同样，))我们使用与javac相同的值))。

下面是build.gradle文件包含的配置：

```groovy
plugins {
    id 'java'
}

group 'cn.tuyucheng.taketoday'

java {
    sourceCompatibility = "1.6"
    targetCompatibility = "1.8"
}
```

## 4. HelloWorldApp示例编译

我们可以创建一个非常简单的类，并通过使用上述脚本构建它来演示这些参数的功能：

```java
public class HelloWorldApp {
    
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}
```

当我们使用gradle build命令构建它时，Gradle将生成一个名为HelloWorldApp.class的class文件。我们可以使用Java自带的javap命令行工具来检查这个类文件生成的字节码版本：

```bash
javap -verbose HelloWorldApp.class
```

这个命令会打印很多信息，但在前几行中，我们可以看到以下内容：

```bash
public class cn.tuyucheng.taketoday.helloworld.HelloWorldApp
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
```

major version字段的值为52，这是Java 8类文件的版本号。意味着我们的))HelloWorldApp.class只能使用Java 8及更高版本运行))。

要测试sourceCompatibility属性配置，我们可以更改源代码并引入Java 6中不可用的功能。我们尝试在HelloWorldApp中使用lambda表达式：

```java
public class HelloWorldApp {

    public static void main(String[] args) {
        Runnable helloLambda = () -> System.out.println("Hello World!");
        helloLambda.run();
    }
}
```

如果我们尝试使用Gradle构建代码，我们会得到一个编译错误：

```bash
error: lambda expressions are not supported in -source 1.6
```

-source参数是sourceCompatibility Gradle配置的Java等效方式，它阻止我们的代码编译。基本上，))如果我们不想引入更高版本的功能，它可以防止我们错误地使用它们))。例如，我们可能希望我们的应用程序也能够在Java 6运行时上运行。

## 5. 总结

在本文中，我们解释了如何使用-source和-target编译参数来处理我们的Java源代码和目标运行时的版本。此外，我们还了解了这些参数如何通过Java插件映射到Gradle的sourceCompatibility和targetCompatibility配置，并在实践中演示了它们的功能。