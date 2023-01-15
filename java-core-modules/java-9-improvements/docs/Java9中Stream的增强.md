## 1. 概述

在这篇简短的文章中，我们重点关注Java 9中对Stream API的加强。

## 2. Takewhile/Dropwhile

关于这些方法的讨论在StackOverflow上反复出现(最流行的是[这个](https://stackoverflow.com/questions/20746429/limit-a-stream-by-a-predicate))。

想象一下，我们想通过在前一个Stream的值上添加一个字符来生成一个String的流，直到这个Stream中的当前值的长度小于10。

我们如何在Java 8中解决它？我们可以使用一个短路中间操作，例如[limit](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html#limit(long))，[allMatch](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html#allMatch(java.util.function.Predicate))，但它们实际上用于其他目的；或者基于[Spliterator编写我们自己的](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Spliterator.html)[takeWhile实现](https://stackoverflow.com/a/20765715/4922375)，但这反过来又使这样一个简单的问题复杂化。

如果使用Java 9，解决方案很简单：

```java
return Stream.iterate("", s -> s + "s")
    .takeWhile(s -> s.length() < 10);
```

takeWhile操作接受一个Predicate，该Predicate应用于元素以确定这些元素的最长前缀(如果流是有序的)或流元素的子集(如果流是无序的)。

为了更好的说明，我们最好理解术语“最长前缀”和“流的子集”的含义：

-   最长前缀是与给定谓词匹配的流元素的连续序列，序列的第一个元素是这个流的第一个元素，紧跟在序列最后一个元素之后的元素与给定的谓词不匹配。
-   流的子集是Stream的一些(但不是全部)元素与给定谓词匹配的集合。

理解了这些关键术语后，我们就可以引入另一个新的dropWhile操作了。

它与takeWhile正好相反；如果流是有序的，则dropWile在删除与给定谓词匹配的元素的最长前缀后，会返回由该Stream的剩余元素组成的流。

否则，如果流是无序的，则dropWile在删除与给定谓词匹配的元素子集后返回由该Stream的剩余元素组成的流。

让我们使用前面获得的Stream丢弃前五个元素：

```java
stream.dropWhile(s -> !s.contains("sssss"));
```

简单地说，对于元素的给定谓词返回true，dropWhile操作将删除元素，并在第一个谓词为false时停止删除。

## 3. 流迭代

下一个新特性是用于有限流生成的重载迭代方法，不要与返回由某个函数产生的无限顺序有序流的[finite](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html#iterate(T,java.util.function.UnaryOperator))变体混淆。

新的迭代通过添加应用于元素的谓词来稍微修改此方法，以确定Stream何时必须终止。它的用法非常方便简洁：

```java
Stream.iterate(0, i -> i < 10, i -> i + 1)
  .forEach(System.out::println);
```

它可以与相应的for语句相关联：

```java
for (int i = 0; i < 10; ++i) {
    System.out.println(i);
}
```

## 4. StreamOfnullable

在某些情况下，我们需要将元素放入Stream中。有时，这个元素可能为null，但我们不希望Stream包含这样的值。它导致编写if语句或三元运算符来检查元素是否为空。

假设collection和map变量已经创建并填充成功，看以下例子：

```java
collection.stream()
  .flatMap(s -> {
      Integer temp = map.get(s);
      return temp != null ? Stream.of(temp) : Stream.empty();
  })
  .collect(Collectors.toList());
```

为了避免这种样板代码，Java 9已将[ofNullable](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/stream/Stream.html#ofNullable(T))方法添加到Stream类中。通过这种方法，前面的例子可以简单地转换为：

```java
collection.stream()
  .flatMap(s -> Stream.ofNullable(map.get(s)))
  .collect(Collectors.toList());
```

## 5. 总结

我们概括了Java 9中Stream API的重大变化，以及这些改进将如何帮助我们以更少的工作量编写更为突出的程序。