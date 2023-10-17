## 1. 概述

在本教程中，我们将学习如何在Java中使用if-else语句。

if-else语句是所有控制结构中最基本的，**它也可能是编程中最常见的决策语句**。

它允许我们**仅在满足特定条件时才执行特定代码段**。

## 2. If-Else的语法

if语句**总是需要一个[布尔](https://www.baeldung.com/java-primitives)表达式作为它的参数**。

```java
if (condition) {
    // Executes when condition is true.
} else {
    // Executes when condition is false.
}
```

它后面可以跟一个可选的else语句，如果布尔表达式为false，将执行其内容。

## 3. If示例

那么，让我们从一些非常基本的东西开始。

假设我们只希望在count变量大于1时发生某些事情：

```java
if (count > 1) {
    System.out.println("Count is higher than 1");
}
```

消息“Count is higher than 1”只有在条件通过时才会打印出来。

另外请注意，在这种情况下，从技术上讲我们可以删除大括号，因为块中只有一行代码。但是，**我们应该始终使用大括号来提高可读性；即使它只是一行**。

当然，如果我们愿意，我们可以向块中添加更多代码：

```java
if (count > 1) {
    System.out.println("Count is higher than 1");
    System.out.println("Count is equal to: " + count);
}
```

## 4. If-Else示例 

接下来，我们可以**结合使用if和else在两个操作过程之间进行选择**：

```java
if (count > 2) {
    System.out.println("Count is higher than 2");
} else {
    System.out.println("Count is lower or equal than 2");
}
```

**请注意，else不能单独存在**。它必须与if同时使用。

## 5. If-Else Else-If示例

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

## 6. 总结

在这篇简短的文章中，我们了解了什么是if-else语句以及如何使用它来管理Java程序中的流程控制。