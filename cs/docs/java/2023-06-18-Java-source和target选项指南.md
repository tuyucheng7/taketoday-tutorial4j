---
layout: post
title:  使用Selenium处理浏览器选项卡
category: java
copyright: java
excerpt: Java
---

## 1.概述

在本教程中，我们将探讨Java提供的–source和–target选项。此外，我们还将了解这些选项如何在Java 8中工作以及它们如何从Java 9开始演变。

## 2.向后兼容旧的Java版本

由于Java发布和更新频繁，应用程序可能无法每次都迁移到更新的版本。应用程序有时需要确保其代码向后兼容旧版本的Java。javac中的target和source选项可以轻松完成此操作。

要详细了解这一点，首先，让我们创建一个示例类并使用Java 9中添加但Java 8中不存在的List.of()方法：

```java
public class TestForSourceAndTarget {
    public static void main(String[] args) {
        System.out.println(List.of("Hello", "Tuyucheng"));
    }
}
```

假设我们使用Java 9编译代码并希望与Java 8兼容。我们可以使用-source和-target
来实现：

```java
/jdk9path/bin/javac TestForSourceAndTarget.java -source 8 -target 8
```

现在，我们在编译时收到警告，但编译成功：

```shell
warning: [options] bootstrap class path not set in conjunction with -source 8
1 warning
```

让我们用Java 8运行代码，我们可以看到错误：

```java
$ /jdk8path/bin/java TestForSourceAndTarget
Exception in thread "main" java.lang.NoSuchMethodError: ↩
  java.util.List.of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;
  at cn.tuyucheng.taketoday.TestForSourceAndTarget.main(TestForSourceAndTarget.java:7)
```

在Java 8中，List.of()不存在。理想情况下，Java应该在编译时抛出这个错误。然而，在编译过程中，我们只得到了一个警告。

让我们看一下我们在编译过程中得到的那个警告。javac通知我们引导程序类未与–source8结合使用。事实证明，我们必须提供引导程序类文件路径，以便javac可以选择正确的文件进行交叉编译。在我们的例子中，我们想要Java 8的兼容性，但默认情况下选择了Java 9引导程序类。

为此，我们必须使用–Xbootclasspath指向需要交叉编译的Java版本的路径：

```java
/jdk9path/bin/javac TestForSourceAndTarget.java -source 8 -target 8 -Xbootclasspath ${jdk8path}/jre/lib/rt.jar
```

现在，让我们编译它，我们可以看到编译时的错误：

```java
TestForSourceAndTarget.java:7: error: cannot find symbol
        System.out.println(List.of("Hello", "Tuyucheng"));
                               ^
  symbol:   method of(String, 
String)
  location: interface List
1 error
```

## 3.来源选项

–source选项指定编译器接受的Java源代码版本：

```java
/jdk9path/bin/javac TestForSourceAndTarget.java -source 8 -target 8
```

如果没有-source选项，编译器将根据所使用的Java版本使用源代码进行编译。

在我们的示例中，如果未提供-source8，编译器将按照Java 9规范编译源代码。

-source值8也意味着我们不能使用任何特定于Java 9的API。为了使用Java 9中引入的任何API，例如List.of()，我们必须将source选项的值设置为9.

## 4.目标选项

target选项指定要生成的类文件的Java版本。目标版本必须等于或高于源选项：

```java
/jdk9path/bin/javac TestForSourceAndTarget.java -source 8 -target 8
```

这里，-target值为8表示这将生成一个需要Java 8或更高版本才能运行的类文件。
如果我们在Java 7中运行上面的类文件，我们会得到一个错误。

## 5.Java 8及更早版本中的源和目标

从我们的示例中可以看出，要让交叉编译在Java 8之前正常工作，我们需要提供三个选项，即–source、-target和-Xbootclasspath。例如，如果我们需要使用Java 9构建代码，但它需要与Java 8兼容：

```java
/jdk9path/bin/javac TestForSourceAndTarget.java -source 8 -target 8 -Xbootclasspath ${jdk8path}/jre/lib/rt.jar
```

从JDK 8开始，不推荐使用1.5或更早版本的源或目标，而在JDK 9中，完全删除了对1.5或更早版本的源或目标的支持。

## 6.Java 9及更高版本中的源和目标

尽管交叉编译在Java 8中运行良好，但仍需要三个命令行选项。当我们有三个选项时，可能很难让它们都保持最新状态。

作为Java 9的一部分，引入了[-release选项](https://www.baeldung.com/java-compiler-release-option)来简化交叉编译过程[。](https://www.baeldung.com/java-compiler-release-option)通过-release选项，我们可以完成和前面选项一样的交叉编译。

让我们使用–release选项来编译我们之前的示例类：

```java
/jdk9path/bin/javac TestForSourceAndTarget.java —release 8
TestForSourceAndTarget.java:7: error: cannot find symbol
        System.out.println(List.of("Hello", "Tuyucheng"));
                               ^
  symbol:   method of(String,String)
  location: interface List
1 error
```

很明显，在编译期间只需要一个选项-release，错误表明javac已经在内部为-source、-target和-Xbootclasspath分配了正确的值。

## 七、总结

在本文中，我们了解了javac的–source和–target选项及其与交叉编译的关系。此外，我们还发现了它们在Java 8及更高版本中的使用方式。此外，我们还了解了Java 9中引入的-release选项。
