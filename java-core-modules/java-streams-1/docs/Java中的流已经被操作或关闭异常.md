## 1. 概述

在这篇简短的文章中，我们讨论在Java 8中使用Stream类时可能遇到的常见异常：

```shell
IllegalStateException: stream has already been operated upon or closed.
```

我们重现发生此异常时的场景，以及避免它的可能方法，以及实际示例。

## 2. 原因

在Java 8中，每个Stream类代表一个一次性使用的数据序列，并支持多个I/O操作。

>   Stream应该只被操作一次(调用中间或终端流操作)。如果Stream实现检测到Stream被重用，它可能会抛出IllegalStateException。

每当对Stream对象调用终端操作时，该实例就会被消耗并关闭。

因此，**我们只被允许执行一个使用Stream的操作**，否则，我们将得到一个异常，表明Stream已经被操作或关闭。

让我们看看如何将其转化为实际示例：

```java
Stream<String> stringStream = Stream.of("A", "B", "C", "D");
Optional<String> result1 = stringStream.findAny(); 
System.out.println(result1.get()); 
Optional<String> result2 = stringStream.findFirst();
```

因此：

```shell
Exception in thread "main" java.lang.IllegalStateException: stream has already been operated upon or closed
```

调用#findAny()方法后，stringStream将关闭，因此，对Stream的任何进一步操作都会抛出IllegalStateException，这就是调用#findFirst()方法后发生的情况。

## 3. 解决方案

简而言之，解决方案包括每次我们需要时创建一个新的Stream。

当然，我们可以手动执行此操作，但这正是Supplier函数接口变得非常方便的地方：

```java
Supplier<Stream<String>> streamSupplier = () -> Stream.of("A", "B", "C", "D");
Optional<String> result1 = streamSupplier.get().findAny();
System.out.println(result1.get());
Optional<String> result2 = streamSupplier.get().findFirst();
System.out.println(result2.get());
```

输出为：

```shell
A
A
```

我们定义了Stream<String\>类型的streamSupplier对象，它与#get()方法返回的类型完全相同。Supplier基于lambda表达式，它不接收任何输入并返回一个新的Stream。

在Supplier上调用函数方法get()会返回一个新创建的Stream对象，我们可以在该对象上安全地执行另一个Stream操作。

## 4. 总结

在本快速教程中，我们了解了如何多次对Stream执行终端操作，同时避免在Stream已关闭或对其进行操作时抛出著名的IllegalStateException 。