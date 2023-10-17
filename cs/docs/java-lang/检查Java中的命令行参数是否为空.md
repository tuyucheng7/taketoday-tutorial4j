## 一、概述

[命令行参数](https://www.baeldung.com/java-command-line-arguments)是一个强大而有用的工具，用于在运行时向命令行程序提供附加信息和指令。

在 Java 中，可以通过*String对象的**args*数组访问它们，当使用命令行参数调用程序时，Java 运行时会自动创建该数组。但是，重要的是检查命令行参数是否为空，以便正确处理未提供它们或它们无效或意外的情况。

在本教程中，我们将讨论如何检查命令行参数是否丢失。

## 2. 访问命令行参数

要在程序中访问和使用命令行参数，我们可以简单地引用*args*数组的元素：

```java
public class CommandLineWithoutErrorHandling {

    public static void main(String[] args) {
        System.out.println(args[0]);
    }
}复制
```

该程序只是将第一个命令行参数打印到控制台：

```java
java CommandLineWithoutErrorHandling.java arg1 arg2 arg3复制
```

此命令行的输出是*arg1。*

此外，我们可以以类似的方式访问和使用其他命令行参数。例如，要访问第二个命令行参数，我们可以使用*args[1]*，依此类推。

但是，如果*args*数组为空，则尝试访问其元素将导致*ArrayIndexOutOfBoundsException：*

```java
@Test(expected = NullPointerException.class)
public void givenNullCommandLineArgument_whenPassedToMainFunction_thenExpectNullPointerException() {

    CommandLineWithoutErrorHandling.main(null);
}复制
```

重要的是要注意，我们应该始终检查***args\*****数组的长度，以确保在尝试访问其元素之前它是非空的**：

```java
public static void main(String[] args) {
        
    if (args.length > 0) {
        System.out.println(args[0]);
    } else {
        System.out.println("No command line arguments were provided.");
    }
}
复制
```

因此，如果提供了第一个命令行参数，该程序将输出它；如果*args*数组为空，则输出一条消息，说明没有提供任何命令行参数。

## 3. 检查命令行参数是否丢失

要检查是否缺少命令行参数，我们可以使用以下方法之一。

首先，我们可以**检查\*args\*数组是否为\*null\***：

```java
if (args == null) {
    // No command line arguments were provided
} else {
    // Command line arguments were provided
}
复制
```

其次，我们可以检查***args\*****数组****的长度**以确定是否提供了任何命令行参数。如果长度为零，则表示未提供任何参数：

```java
if (args.length == 0) {
    // No command line arguments were provided
} else {
    // Command line arguments were provided
}复制
```

最后，我们可以**检查是否提供了任何命令行参数，无论它们是否为\*空\***：

```java
if (args.length > 0) {
    // Command line arguments were provided
} else {
    // No command line arguments were provided
}复制
```

这些方法中的每一种都允许我们确定是否向我们的程序提供了命令行参数。

## 4。结论

在本文中，我们研究了检查 Java 程序中是否缺少命令行参数的不同方法。

我们讨论了每种方法的优点和注意事项，并强调了检查空参数的重要性，以便处理未提供所需参数或收到无效参数的情况。这对于确定程序的正确行为并确保其顺利运行至关重要。