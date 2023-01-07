## 1. 概述

在本教程中，我们将学习如何在 Java中使用if-else语句。

if-else语句是所有控制结构中最基本的，它也可能是 编程中最常见的决策语句。

它允许我们仅在满足特定条件时才执行特定代码段。

## 2. If-Else的语法

if语句总是需要一个[布尔](https://www.baeldung.com/java-primitives)表达式作为它的参数。

```java
if (condition) {
    // Executes when condition is true.
} else {
    // Executes when condition is false.
}
```

它后面可以跟一个可选的else语句，如果布尔表达式为false，将执行其内容。

## 3.如果的例子 

那么，让我们从一些非常基本的东西开始。

假设我们只希望在计数变量大于 1 时发生某些事情：

```java
if (count > 1) {
    System.out.println("Count is higher than 1");
}
```

消息Count is higher than 1只有在条件通过时才会打印出来。

另外请注意，在这种情况下，技术上我们可以删除大括号，因为块中只有一行。但是，我们应该始终使用大括号来提高可读性；即使它只是一条线。

当然，如果我们愿意，我们可以向块中添加更多指令：

```java
if (count > 1) {
    System.out.println("Count is higher than 1");
    System.out.println("Count is equal to: " + count);
}
```

## 4. If-Else示例 

接下来，我们可以结合使用if和else在两个操作过程中进行选择：

```java
if (count > 2) {
    System.out.println("Count is higher than 2");
} else {
    System.out.println("Count is lower or equal than 2");
}
```

请注意，else不能单独存在。它必须与if连接。

## 5. If-Else If-Else示例

最后，让我们以组合的if/else/else if语法示例结束。

我们可以使用它在三个或更多选项之间进行选择：

```java
if (count > 2) {
    System.out.println("Count is higher than 2");
} else if (count <= 0) {
    System.out.println("Count is less or equal than zero");
} else {
    System.out.println("Count is either equal to one, or two");
}
```

## 六，总结

在这篇快速文章中，我们了解了什么是if-else语句以及如何使用它来管理Java程序中的流程控制。