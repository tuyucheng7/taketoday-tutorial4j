## 1. 概述

在本教程中，我们将演示如何使用Java8 Streams实现 if/else 逻辑。作为本教程的一部分，我们将创建一个简单的算法来识别奇数和偶数。

我们可以看看[这篇文章](https://www.baeldung.com/java-8-streams-introduction)来了解Java8 Stream的基础知识。

## 2. forEach() 中的常规 if/else逻辑

首先，让我们创建一个整数列表，然后在 整数 流 forEach()方法中使用传统的 if/else 逻辑：

```java
List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

ints.stream()
    .forEach(i -> {
        if (i.intValue() % 2 == 0) {
            Assert.assertTrue(i.intValue() % 2 == 0);
        } else {
            Assert.assertTrue(i.intValue() % 2 != 0);
        }
    });
```

我们的forEach方法包含 if-else 逻辑，它使用Java模数运算符验证Integer是奇数还是偶数。

## 3. if/else逻辑与filter()

其次，让我们看一下使用Stream filter()方法的更优雅的实现：

```java
Stream<Integer> evenIntegers = ints.stream()
    .filter(i -> i.intValue() % 2 == 0);
Stream<Integer> oddIntegers = ints.stream()
    .filter(i -> i.intValue() % 2 != 0);

evenIntegers.forEach(i -> Assert.assertTrue(i.intValue() % 2 == 0));
oddIntegers.forEach(i -> Assert.assertTrue(i.intValue() % 2 != 0));
```

上面我们使用Stream filter()方法实现了 if/else 逻辑，将Integer List分成两个Stream，一个用于偶数，另一个用于奇数。

## 4。总结

在这篇简短的文章中，我们探索了如何创建Java8 Stream以及如何使用forEach()方法实现 if/else 逻辑。

此外，我们还学习了如何使用Stream 过滤器方法以更优雅的方式获得类似的结果。