## 1. 概述

在本快速教程中，我们将探索在Java中创建无限循环的方法。

简单地说，无限循环就是当不满足终止条件时无限循环的指令序列。创建无限循环可能是编程错误，但也可能是基于应用程序行为的有意为之。

## 2. 使用while

让我们从while循环开始。这里我们将使用布尔字面量true来编写while循环条件：

```java
public void infiniteLoopUsingWhile() {
    while (true) {
        // do something
    }
}
```

## 3. for

现在，让我们使用for循环来创建一个无限循环：

```java
public void infiniteLoopUsingFor() {
    for (;;) {
        // do something
    }
}
```

## 4. 使用do-while

也可以使用Java中不太常见的do-while循环来创建无限循环。这里的循环条件是在第一次执行后评估的：

```java
public void infiniteLoopUsingDoWhile() {
    do {
        // do something
    } while (true);
}
```

## 5. 总结

尽管在大多数情况下我们会避免创建无限循环，但在某些情况下我们可能需要创建一个。在这种情况下，循环将在应用程序退出时终止。