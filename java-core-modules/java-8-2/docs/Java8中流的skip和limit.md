## 1. 简介

在这篇简短的文章中，我们将讨论[Java Stream API](https://www.baeldung.com/java-8-streams)的skip()和limit()方法，并强调它们的异同点。

尽管这两个操作乍一看可能非常相似，但实际上它们的行为非常不同，并且不能彼此代替地使用。实际上，它们是互补的，一起使用时可以很方便。

## 2. skip()方法

**skip(n)方法是一个中间操作，它丢弃流的前n个元素**。参数n不能为负数，如果n大于流的大小，skip()将返回一个空流。

让我们看一个例子：

```java
Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    .filter(i -> i % 2 == 0)
    .skip(2)
    .forEach(i -> System.out.print(i + " "));
```

在这个流中，我们选取流的偶数，然后基于经过过滤后的流跳过前两个。因此，我们得到的结果是：

```shell
6 8 10
```

当这个流被执行时，forEach开始请求元素。当它到达skip()时，该操作知道必须丢弃前两个元素，因此不会将它们添加到结果流中。之后，它会创建并返回一个包含剩余元素的流。

为了做到这一点，skip()操作必须保持每个时刻看到的元素的状态。出于这个原因，**我们说skip()是一个有状态的操作**。

## 3. limit()方法

**limit(n)方法是另一个中间操作，它返回的流长度不超过请求的大小**。和之前一样，参数n不能为负数。

让我们在示例中使用它：

```java
Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    .filter(i -> i % 2 == 0)
    .limit(2)
    .forEach(i -> System.out.print(i + " "));
```

在本例中，我们只从int流中获取两个偶数：

```shell
2 4
```

与skip()操作一样，limit()也是一个有状态操作，因为它必须保持正在经过流的元素的状态。

但与消耗整个流的skip()不同，一旦limit()达到最大元素数，它就不再消耗更多的元素并简单地返回结果流。因此，**我们说limit()是一个短路操作**。

当处理无限流时，limit()对于将流截断为有限流非常有用：

```java
Stream.iterate(0, i -> i + 1)
    .filter(i -> i % 2 == 0)
    .limit(10)
    .forEach(System.out::println);
```

在此示例中，我们将无限的数字流截断为只有十个偶数的流。

## 4. 结合skip()和limit()

正如我们前面提到的，skip和limit操作是互补的，如果我们将它们结合起来，它们在某些情况下会非常有用。

假设我们要修改前面的示例，使其以十个为一组获得偶数。我们可以简单地通过在同一个流上同时使用skip()和limit()来做到这一点：

```java
private static List<Integer> getEvenNumbers(int offset, int limit) {
	return Stream.iterate(0, i -> i + 1)
	    .filter(i -> i % 2 == 0)
	    .skip(offset)
	    .limit(limit)
	    .collect(Collectors.toList());
}
```

如我们所见，使用这种方法，我们可以很容易地对流进行分页。尽管这是一个非常简单的分页，但我们可以看到它在对流进行切片时有多么强大。

## 5. 总结

在这篇简短的文章中，我们展示了Java Stream API的skip()和limit()方法的异同，并实现了一些简单的示例来展示我们如何使用这些方法。