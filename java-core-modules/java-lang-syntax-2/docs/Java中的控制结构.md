## 1. 概述

在最基本的意义上，程序是指令列表。**控制结构是编程块，可以改变我们通过这些指令所采用的路径**。

在本教程中，我们将探索Java中的控制结构。

**控制结构分为三种**：

-   条件分支，我们用它来**在两条或多条路径之间进行选择**。Java中有三种类型：if/else/else if、三元运算符和switch。
-   用于**遍历多个值/对象并重复运行特定代码块的循环**。Java中的基本循环类型是for、while和do while。
-   分支语句，用于**改变循环中的控制流**。Java中有两种类型：break和continue。

## 2. If/Else/Else If

if/else语句是[最基本的控制结构](https://www.baeldung.com/java-if-else)，但也可以被视为编程决策的基础。

虽然if可以单独使用，但最常见的使用场景是使用if/else在两条路径之间进行选择：

```java
if (count > 2) {
    System.out.println("Count is higher than 2");
} else {
    System.out.println("Count is lower or equal than 2");
}
```

**理论上，我们可以无限链接或嵌套if/else块，但这会损害代码的可读性，这就是不建议这样做的原因**。

我们将在本文的其余部分探讨替代语句。

## 3. 三元运算符

**我们可以使用[三元运算符](https://www.baeldung.com/java-ternary-operator)作为简写表达式，其工作方式类似于if/else语句**。

让我们再看看我们的if/else例子：

```java
if (count > 2) {
    System.out.println("Count is higher than 2");
} else {
    System.out.println("Count is lower or equal than 2");
}
```

我们可以用三元表达式重构它，如下所示：

```java
System.out.println(count > 2 ? "Count is higher than 2" : "Count is lower or equal than 2");
```

虽然三元表达式可以是使我们的代码更具可读性的好方法，但它并不总是if/else的良好替代品。

## 4. Switch

**如果我们有多种情况可供选择，我们可以使用switch语句**。

让我们再看一个简单的例子：

```java
int count = 3;
switch (count) {
case 0:
    System.out.println("Count is equal to 0");
    break;
case 1:
    System.out.println("Count is equal to 1");
    break;
default:
    System.out.println("Count is either negative, or higher than 1");
    break;
}
```

三个或更多if/else语句可能难以阅读。作为可能的解决方法之一，我们可以使用switch， 如上所示。

还要记住[switch有范围和输入限制](https://www.baeldung.com/java-switch)，我们在使用它之前需要记住这些限制。

## 5. 循环

**当我们需要连续多次重复相同的代码时，我们使用[循环](https://www.baeldung.com/java-loops)**。

让我们看一个可比较的for和while循环类型的快速示例：

```java
for (int i = 1; i <= 50; i++) {
    methodToRepeat();
}

int whileCounter = 1;
while (whileCounter <= 50) {
    methodToRepeat();
    whileCounter++;
}
```

上面的两个代码块都会调用methodToRepeat 50次。

## 6. Break

**我们需要使用[break](https://www.baeldung.com/java-continue-and-break)提前退出循环**。

让我们看一个简单的例子：

```java
List<String> names = getNameList();
String name = "John Doe";
int index = 0;
for ( ; index < names.length; index++) {
    if (names[index].equals(name)) {
        break;
    }
}
```

在这里，我们正在names列表中查找一个名称”John Doe“，一旦找到它，我们就想停止查找。

一个循环通常会完成，但我们在这里使用break来缩短循环并提前退出。

## 7. Continue

简单地说，**[continue](https://www.baeldung.com/java-continue-and-break)意味着跳过我们所在的循环的其余部分**：

```java
List<String> names = getNameList();
String name = "John Doe";
String list = "";
for (int i = 0; i < names.length; i++) { 
    if (names[i].equals(name)) {
        continue;
    }
    list += names[i];
}
```

在这里，我们跳过将重复名称拼接到list中。

**正如我们在这里看到的，break和continue在迭代时会很方便，尽管它们通常可以用return语句或其他逻辑重写**。

## 8. 总结

在这篇简短的文章中，我们了解了什么是控制结构以及如何使用它们来管理Java程序中的流程控制。