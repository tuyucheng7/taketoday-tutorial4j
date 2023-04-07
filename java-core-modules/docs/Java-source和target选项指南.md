## **一、概述**

在本教程中，我们将探讨Java 提供的*–source*和*–target选项。*此外，我们还将了解这些选项如何在 Java 8 中工作以及它们如何从 Java 9 开始演变。

## **2. 向后兼容旧的 Java 版本**

由于 Java 发布和更新频繁，应用程序可能无法每次都迁移到更新的版本。**应用程序有时需要确保其代码向后兼容旧版本的 Java。\*javac\*中的target和\*source\*选项可以轻松完成此操作\*。\***

要详细了解这一点，首先，让我们创建一个示例类并使用Java 9 中添加但 Java 8 中不存在的*List.of()方法：*

```java
public class TestForSourceAndTarget {
    public static void main(String[] args) {
        System.out.println(List.of("Hello", "Baeldung"));
    }
}复制
```

假设我们使用 Java 9 编译代码并希望与 Java 8 兼容。我们可以使用*-source*和*-target*
来实现：

```java
/jdk9path/bin/javac TestForSourceAndTarget.java -source 8 -target 8复制
```

现在，我们在编译时收到警告，但编译成功：

```shell
warning: [options] bootstrap class path not set in conjunction with -source 8
1 warning复制
```

让我们用 Java 8 运行代码，我们可以看到错误：

```java
$ /jdk8path/bin/java TestForSourceAndTarget
Exception in thread "main" java.lang.NoSuchMethodError: ↩
  java.util.List.of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;
  at com.baeldung.TestForSourceAndTarget.main(TestForSourceAndTarget.java:7)复制
```

在 Java 8 中，*List.of()*不存在。理想情况下，Java 应该在编译时抛出这个错误。然而，在编译过程中，我们只得到了一个警告。

让我们看一下我们在编译过程中得到的那个警告。*javac*通知我们引导程序类未与 –source 8 结合使用。*事实*证明，**我们必须提供引导程序类文件路径，以便 \*javac\*可以选择正确的文件进行交叉编译。**在我们的例子中，我们想要 Java 8 的兼容性，但默认情况下选择了 Java 9 引导程序类。

为此，**我们必须使用 –Xbootclasspath\*指向\*需要交叉编译的 Java 版本的路径**：

```java
/jdk9path/bin/javac TestForSourceAndTarget.java -source 8 -target 8 -Xbootclasspath ${jdk8path}/jre/lib/rt.jar复制
```

现在，让我们编译它，我们可以看到编译时的错误：

```java
TestForSourceAndTarget.java:7: error: cannot find symbol
        System.out.println(List.of("Hello", "Baeldung"));
                               ^
  symbol:   method of(String, 
String)
  location: interface List
1 error复制
```

## **3.来源选项**

**–source\*选项\*指定编译器接受的 Java 源代码版本：**

```java
/jdk9path/bin/javac TestForSourceAndTarget.java -source 8 -target 8复制
```

如果没有*-source*选项，编译器将根据所使用的 Java 版本使用源代码进行编译。

在我们的示例中，如果未提供*-source* 8，编译器将按照 Java 9 规范编译源代码。

-source值 8 也意味着我们不能使用任何特定于 Java 9 的 API。为了使用 Java 9 中引入的任何 API，例如*List.of()* *，*我们必须将 source 选项的值设置为 9 .

## **4.目标选项**

**target 选项指定要生成的类文件的 Java 版本。目标版本必须等于或高于源选项：**

```java
/jdk9path/bin/javac TestForSourceAndTarget.java -source 8 -target 8复制
```

这里，**-target值为 8 表示这将生成一个需要 Java 8 或更高版本\*才能\*** **运行的****类文件**。
如果我们在 Java 7 中运行上面的类文件，我们会得到一个错误。

## 5. Java 8 及更早版本中的源和目标

**从我们的示例中可以看出，要让交叉编译在 Java 8 之前正常工作，我们需要提供三个选项，即 – \*source、-target\*和\*-Xbootclasspath。\***例如，如果我们需要使用 Java 9 构建代码，但它需要与 Java 8 兼容：

```java
/jdk9path/bin/javac TestForSourceAndTarget.java -source 8 -target 8 -Xbootclasspath ${jdk8path}/jre/lib/rt.jar复制
```

从 JDK 8 开始，不推荐使用 1.5 或更早版本的源或目标，而在 JDK 9 中，完全删除了对 1.5 或更早版本的源或目标的支持。

## 6. Java 9 及更高版本中的源和目标

**尽管交叉编译在 Java 8 中运行良好，但仍需要三个命令行选项。当我们有三个选项时，可能很难让它们都保持最新状态。**

**作为 Java 9 的一部分，引入了[-release选项](https://www.baeldung.com/java-compiler-release-option)来简化交叉编译过程[\*。\*](https://www.baeldung.com/java-compiler-release-option)**通过-release*选项*，我们可以完成和前面选项一样的交叉编译。

让我们使用*–release*选项来编译我们之前的示例类：

```java
/jdk9path/bin/javac TestForSourceAndTarget.java —release 8复制
TestForSourceAndTarget.java:7: error: cannot find symbol
        System.out.println(List.of("Hello", "Baeldung"));
                               ^
  symbol:   method of(String,String)
  location: interface List
1 error复制
```

很明显，在编译期间只需要一个选项*-release*，错误表明*javac已经在内部为**-source、-target*和-Xbootclasspath分配了正确的值*。*

## **七、结论**

在本文中，我们了解了*javac的**–source*和*–target*选项及其与交叉编译的关系。此外，我们还发现了它们在 Java 8 及更高版本中的使用方式。此外，我们还了解了Java 9 中引入的*-release*选项。