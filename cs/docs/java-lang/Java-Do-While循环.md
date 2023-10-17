## 1. 概述

在本文中，我们将了解Java语言的一个核心方面-使用do-while循环重复执行一条语句或一组语句。

## 2. Do-While循环

do-while循环的工作方式与while循环类似，只是**第一个条件求值发生在循环的第一次迭代之后**：

```java
do {
    statement;
} while (Boolean-expression);
```

让我们看一个简单的例子：

```java
int i = 0;
do {
    System.out.println("Do-While loop: i = " + i++);
} while (i < 5);
```

## 3. 总结

在本快速教程中，我们探讨了Java的do-while循环。