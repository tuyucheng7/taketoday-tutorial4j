## 1. 概述

在本文中，我们将研究来自Guava库的[布隆过滤器](https://www.baeldung.com/cs/bloom-filter)构造。[布隆过滤器](https://www.baeldung.com/cs/bloom-filter)是一种内存高效的概率数据结构，我们可以使用它来回答给定元素是否在集合中的问题。

布隆过滤器没有漏报，所以当它返回false时，我们可以 100% 确定该元素不在集合中。

然而，Bloom filter可以返回 false positives，所以当它返回true时，元素在集合中的可能性很高，但我们不能 100% 确定。

要更深入地分析布隆过滤器的工作原理，你可以阅读[本教程](https://llimllib.github.io/bloomfilter-tutorial/)。

## 2.Maven依赖

我们将使用 Guava 的布隆过滤器实现，所以让我们添加guava依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

最新版本可以在[Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")上找到。

## 3. 为什么要使用布隆过滤器？

Bloom 过滤器旨在节省空间且速度快。使用它时，我们可以指定我们可以接受的误报响应的概率，并且根据该配置，布隆过滤器将占用尽可能少的内存。

由于这种空间效率，即使对于大量元素， Bloom 过滤器也很容易适合内存。一些数据库，包括 Cassandra 和 Oracle，使用这个过滤器作为进入磁盘或缓存之前的第一个检查，例如，当一个特定 ID 的请求进来时。

如果过滤器返回 ID 不存在，数据库可以停止进一步处理请求并返回给客户端。否则，它会转到磁盘并返回在磁盘上找到的元素。

## 4. 创建布隆过滤器

假设我们要为最多 500 个整数创建布隆过滤器，并且我们可以容忍百分之一 (0.01) 的误报概率。

我们可以使用Guava库中的BloomFilter类来实现这一点。我们需要传递我们希望插入过滤器的元素数量和所需的误报概率：

```java
BloomFilter<Integer> filter = BloomFilter.create(
  Funnels.integerFunnel(),
  500,
  0.01);
```

现在让我们向过滤器添加一些数字：

```java
filter.put(1);
filter.put(2);
filter.put(3);
```

我们只添加了三个元素，并且我们定义了最大插入次数为 500，因此我们的 Bloom 过滤器应该会产生非常准确的结果。让我们使用mightContain()方法对其进行测试：

```java
assertThat(filter.mightContain(1)).isTrue();
assertThat(filter.mightContain(2)).isTrue();
assertThat(filter.mightContain(3)).isTrue();

assertThat(filter.mightContain(100)).isFalse();
```

顾名思义，当方法返回true时，我们不能 100% 确定给定元素实际上在过滤器中。

当mightContain()在我们的示例中返回true时，我们可以 99% 确定该元素在过滤器中，并且结果为误报的概率为 1%。当过滤器返回false时，我们可以 100% 确定该元素不存在。

## 5. 过饱和布隆过滤器

当我们设计我们的布隆过滤器时，重要的是我们为预期的元素数量提供一个合理准确的值。否则，我们的过滤器将以比预期高得多的速度返回误报。让我们看一个例子。

假设我们创建了一个过滤器，其所需的误报概率为 1%，并且预期一些元素等于 5，但随后我们插入了 100,000 个元素：

```java
BloomFilter<Integer> filter = BloomFilter.create(
  Funnels.integerFunnel(),
  5,
  0.01);

IntStream.range(0, 100_000).forEach(filter::put);

```

因为预期的元素数量很少，过滤器将占用很少的内存。

然而，当我们添加的项目比预期的多时，过滤器变得过饱和并且返回误报结果的概率比所需的百分之一高得多：

```java
assertThat(filter.mightContain(1)).isTrue();
assertThat(filter.mightContain(2)).isTrue();
assertThat(filter.mightContain(3)).isTrue();
assertThat(filter.mightContain(1_000_000)).isTrue();
```

请注意，即使对于我们之前未插入过滤器的值， mightContatin()方法也会返回true 。

## 六. 总结

在本快速教程中，我们研究了布隆过滤器数据结构的概率性质——利用Guava实现。