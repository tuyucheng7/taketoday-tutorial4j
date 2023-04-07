## 一、概述

Java 是一种通用编程语言，专注于 WORA（一次编写，随处运行）原则。

它运行在负责抽象底层操作系统的JVM（[Java 虚拟机）上，允许 Java 程序几乎无处不在，从应用服务器到手机。](https://www.baeldung.com/jvm-vs-jre-vs-jdk)

在学习一门新语言时，“Hello World”通常是我们编写的第一个程序。

在本教程中，我们将**学习一些基本的 Java 语法并编写一个简单的“Hello World”程序**。

## 2.编写Hello World程序

让我们打开任何 IDE 或文本编辑器并创建一个名为*HelloWorld.java*的简单文件：

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}复制
```

在我们的示例中，**我们创建了一个名为\*HelloWorld的 Java\**类\*，其中包含一个**将一些文本写入控制台的***主要\*****方法。**

当我们执行程序时，Java 会运行*main*方法，打印出“Hello World!” [在控制台](https://www.baeldung.com/java-console-input-output)上。

现在，让我们看看如何编译和执行我们的程序。

## 3. 编译和执行程序

为了编译 Java 程序，我们需要从命令行调用[Java 编译器：](https://www.baeldung.com/javac)

```bash
$ javac HelloWorld.java复制
```

编译器生成*HelloWorld.class*文件，这是我们代码的编译字节码版本。

让我们通过调用来运行它：

```java
$ java HelloWorld复制
```

并查看结果：

```plaintext
Hello World!复制
```

## 4。结论

通过这个简单的示例，我们创建了一个 Java 类，其中默认的*main方法在*[系统控制台](https://www.baeldung.com/java-lang-system)上打印出一个字符串。

我们了解了如何创建、编译和执行 Java 程序，并熟悉了一些基本语法。我们在这里看到的 Java 代码和命令在每个支持 Java 的操作系统上都是相同的。