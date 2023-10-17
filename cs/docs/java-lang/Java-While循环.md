## 1. 概述

在本文中，我们将了解Java语言的一个核心方面-使用while循环重复执行一条语句或一组语句。

## 2. While循环

while循环是Java最基本的循环语句。当其控制布尔表达式为true时，它会重复一条语句或语句块。

while循环的语法是：

```java
while (Boolean-expression) 
    statement;
```

**循环的布尔表达式在循环的第一次迭代之前被评估**-这意味着如果条件被评估为false，循环可能甚至不会运行一次。

让我们看一个简单的例子：

```java
int i = 0;
while (i < 5) {
    System.out.println("While loop: i = " + i++);
}
```

## 3. 总结

在本快速教程中，我们探讨了Java的while循环。