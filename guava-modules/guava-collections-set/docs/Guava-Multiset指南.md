## 1. 概述

在本教程中，我们将探索[Guava](https://www.baeldung.com/category/guava/)集合之一——Multiset。与java.util.Set一样，它允许在没有保证顺序的情况下高效地存储和检索项目。

但是，与Set不同的是，它通过跟踪它包含的每个唯一元素的计数来允许同一元素多次出现。

## 2.Maven依赖

首先，让我们添加[guava依赖项](https://search.maven.org/classic/#search|gav|1|g%3A"com.google.guava" AND a%3A"guava")：

```java
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

## 3.使用多重集

让我们考虑一家拥有多本不同书籍的书店。我们可能想要执行添加副本、获取副本数量以及在售出时移除副本等操作。由于Set不允许同一元素多次出现，因此它无法满足此要求。

让我们从添加书名的副本开始。Multiset应该返回标题存在并为我们提供正确的计数：

```java
Multiset<String> bookStore = HashMultiset.create();
bookStore.add("Potter");
bookStore.add("Potter");
bookStore.add("Potter");

assertThat(bookStore.contains("Potter")).isTrue();
assertThat(bookStore.count("Potter")).isEqualTo(3);
```

现在让我们删除一个副本。我们希望相应地更新计数：

```java
bookStore.remove("Potter");
assertThat(bookStore.contains("Potter")).isTrue();
assertThat(bookStore.count("Potter")).isEqualTo(2);
```

实际上，我们可以只设置计数而不是执行各种添加操作：

```java
bookStore.setCount("Potter", 50); 
assertThat(bookStore.count("Potter")).isEqualTo(50);
```

Multiset验证计数值。如果我们将其设置为负数，则会抛出 IllegalArgumentException：

```java
assertThatThrownBy(() -> bookStore.setCount("Potter", -1))
  .isInstanceOf(IllegalArgumentException.class);
```

## 4. 与地图的比较

在不访问Multiset的情况下，我们可以通过使用java.util.Map实现我们自己的逻辑来实现上述所有操作：

```java
Map<String, Integer> bookStore = new HashMap<>();
// adding 3 copies
bookStore.put("Potter", 3);

assertThat(bookStore.containsKey("Potter")).isTrue();
assertThat(bookStore.get("Potter")).isEqualTo(3);

// removing 1 copy
bookStore.put("Potter", 2);
assertThat(bookStore.get("Potter")).isEqualTo(2);
```

当我们想要使用Map添加或删除副本时，我们需要记住当前计数并相应地进行调整。我们还需要每次都在我们的调用代码中实现这个逻辑，或者为此构建我们自己的库。我们的代码还需要控制值参数。如果我们不小心，我们可以很容易地将值设置为null或负数，即使这两个值都无效：

```java
bookStore.put("Potter", null);
assertThat(bookStore.containsKey("Potter")).isTrue();

bookStore.put("Potter", -1);
assertThat(bookStore.containsKey("Potter")).isTrue();

```

如我们所见，使用Multiset而不是Map更方便。

## 5.并发

当我们想在并发环境中使用Multiset时，我们可以使用ConcurrentHashMultiset，这是一个线程安全的Multiset实现。

但是，我们应该注意，线程安全并不能保证一致性。使用add或remove方法在多线程环境中会很好地工作，但是如果多个线程调用setCount方法怎么办？ 

如果我们使用setCount方法，最终结果将取决于跨线程的执行顺序，这不一定是可以预测的。添加 和 删除方法是增量的，并且 ConcurrentHashMultiset 能够保护它们的行为。直接设置计数不是递增的，因此并发使用时会导致意外结果。

但是，setCount方法还有另一种风格，它仅在其当前值与传递的参数匹配时才更新计数。如果操作成功，该方法返回 true，这是一种乐观锁定的形式：

```java
Multiset<String> bookStore = HashMultiset.create();
// updates the count to 2 if current count is 0
assertThat(bookStore.setCount("Potter", 0, 2)).isTrue();
// updates the count to 5 if the current value is 50
assertThat(bookStore.setCount("Potter", 50, 5)).isFalse();
```

如果我们要在并发代码中使用setCount方法，我们应该使用上面的版本来保证一致性。如果更改计数失败，多线程客户端可以执行重试。

## 六. 总结

在这个简短的教程中，我们讨论了何时以及如何使用Multiset，将其与标准Map进行了比较，并研究了如何在并发应用程序中最好地使用它。