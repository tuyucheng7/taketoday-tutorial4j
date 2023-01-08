## 1. 概述

在本快速教程中，我们将介绍有关将IntStream转换为其他类型的所有可能性。

[建议阅读有关装箱和拆箱](https://www.baeldung.com/java-8-primitive-streams)或[迭代](https://www.baeldung.com/java-stream-indices)的有趣读物作为本教程的补充。

## 2. IntStream转数组

让我们开始探索如何将IntStream对象转换为int数组。

为了这个例子，让我们生成前 50 个偶数并将它们作为结果存储在一个数组中：

```java
@Test
public void intStreamToArray() {
  int[] first50EvenNumbers = IntStream.iterate(0, i -> i + 2)
    .limit(50)
    .toArray();

  assertThat(first50EvenNumbers).hasSize(50);
  assertThat(first50EvenNumbers[2]).isEqualTo(4);
}
```

首先，让我们创建一个无限的整数流，从 0 开始，并通过向每个元素加 2 来进行迭代。紧接着，我们需要添加一个中间操作限制，以使该操作以某种方式终止。

最后，让我们使用终止操作collect将此Stream收集到一个数组中。

这是生成int s数组的直接方法。

## 3. IntStream列表_

现在让我们将IntStream转换为Integers列表。

在这种情况下，为了让示例更加多样化，让我们使用方法range而不是方法iterate。此方法将生成一个从int 0 到int 50 的IntStream(不包括在内，因为它是一个开放范围)：

```java
@Test
public void intStreamToList() {
  List<Integer> first50IntegerNumbers = IntStream.range(0, 50)
    .boxed()
    .collect(Collectors.toList());

  assertThat(first50IntegerNumbers).hasSize(50);
  assertThat(first50IntegerNumbers.get(2)).isEqualTo(2);
}
```

在这个例子中，我们使用方法range。这里最臭名昭著的部分是使用方法boxed，正如其名称所指出的那样，它将装箱IntStream中的所有int元素并返回一个Stream<Integer>。

最后，我们可以使用收集器来获取整数列表。

## 4. IntStream转String

对于我们的最后一个主题，让我们探讨如何从IntStream中获取String。

在这种情况下，我们将只生成前 3 个int(0、1 和 2)：

```java
@Test
public void intStreamToString() {
  String first3numbers = IntStream.of(0, 1, 2)
    .mapToObj(String::valueOf)
    .collect(Collectors.joining(", ", "[", "]"));

  assertThat(first3numbers).isEqualTo("[0, 1, 2]");
}
```

首先，在这种情况下，我们使用构造函数IntStream.of()构造一个IntStream。拥有Stream之后，我们需要以某种方式从IntStream生成Stream<String>。因此，我们可以使用中间的mapToObj方法，该方法将接受一个IntStream并将返回一个Stream，该方法是在调用的方法中映射的结果对象的类型。

最后，我们使用采用Stream<String>的收集器连接，并可以使用定界符以及可选的前缀和后缀附加Stream的每个元素。

## 5。总结

在本快速教程中，我们探索了需要将IntStream转换为任何其他类型时的所有替代方案。特别是，我们通过示例来生成一个数组、一个List和一个String。