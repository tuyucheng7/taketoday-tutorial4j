## 1. 概述

在本教程中，我们将了解[Protonpack](https://github.com/poetix/protonpack)的主要功能，它是一个通过添加一些免费功能 来扩展标准[Stream API的库。](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/stream/Stream.html#takeWhile(java.util.function.Predicate))

请参阅[此处的这篇文章](https://www.baeldung.com/java-8-streams-introduction) ，了解JavaStream API 的基础知识。

## 2.Maven依赖

要使用 Protonpack 库，我们需要在pom.xml文件中添加依赖项：

```xml
<dependency>
    <groupId>com.codepoetics</groupId>
    <artifactId>protonpack</artifactId>
    <version>1.15</version>
</dependency>
```

[在Maven Central](https://search.maven.org/classic/#search|gav|1|g%3A"com.codepoetics" AND a%3A"protonpack")检查最新版本 。

## 3.流工具

这是扩展Java标准Stream API 的主要类。

这里讨论的所有方法都是[中间操作](https://www.baeldung.com/java-8-streams-introduction#operations)，这意味着它们修改 Stream但不会触发其处理。

### 3.1. takeWhile()和takeUntil()

只要满足提供的条件，takeWhile() 就会从源流中获取值：

```java
Stream<Integer> streamOfInt = Stream
  .iterate(1, i -> i + 1);
List<Integer> result = StreamUtils
  .takeWhile(streamOfInt, i -> i < 5)
  .collect(Collectors.toList());
assertThat(result).contains(1, 2, 3, 4);
```

相反，takeUntil() 获取值 直到值满足提供的条件然后停止：

```java
Stream<Integer> streamOfInt = Stream
  .iterate(1, i -> i + 1);
List<Integer> result = StreamUtils
  .takeUntil(streamOfInt, i -> i >= 5)
  .collect(Collectors.toList());
assertThat(result).containsExactly(1, 2, 3, 4);
```

在Java9 之后，takeWhile()是标准[Stream API](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/stream/Stream.html#takeWhile(java.util.function.Predicate))的一部分。

### 3.2. 压缩()

zip() 将两个或三个流作为输入和组合器函数。该方法从每个流的相同位置获取一个值并将其传递给组合器。

它会这样做，直到其中一个流用完值为止：

```java
String[] clubs = {"Juventus", "Barcelona", "Liverpool", "PSG"};
String[] players = {"Ronaldo", "Messi", "Salah"};
Set<String> zippedFrom2Sources = StreamUtils
  .zip(stream(clubs), stream(players), (club, player) -> club + " " + player)
  .collect(Collectors.toSet());
 
assertThat(zippedFrom2Sources)
  .contains("Juventus Ronaldo", "Barcelona Messi", "Liverpool Salah");

```

同样，一个重载的zip()需要三个源流：

```java
String[] leagues = { "Serie A", "La Liga", "Premier League" };
Set<String> zippedFrom3Sources = StreamUtils
  .zip(stream(clubs), stream(players), stream(leagues), 
    (club, player, league) -> club + " " + player + " " + league)
  .collect(Collectors.toSet());
 
assertThat(zippedFrom3Sources).contains(
  "Juventus Ronaldo Serie A", 
  "Barcelona Messi La Liga", 
  "Liverpool Salah Premier League");
```

### 3.3. zipWithIndex()

zipWithIndex() 获取值并将每个值与其索引压缩以创建索引值流：

```java
Stream<String> streamOfClubs = Stream
  .of("Juventus", "Barcelona", "Liverpool");
Set<Indexed<String>> zipsWithIndex = StreamUtils
  .zipWithIndex(streamOfClubs)
  .collect(Collectors.toSet());
assertThat(zipsWithIndex)
  .contains(Indexed.index(0, "Juventus"), Indexed.index(1, "Barcelona"), 
    Indexed.index(2, "Liverpool"));
```

### 3.4. 合并()

merge()适用于多个源流和一个组合器。它从每个源流中获取相同索引位置的值并将其传递给组合器。

该方法的工作原理是从种子值开始连续从每个流的相同索引中获取 1个值。

然后将值传递给组合器，将组合后的值反馈给组合器以创建下一个值：

```java
Stream<String> streamOfClubs = Stream
  .of("Juventus", "Barcelona", "Liverpool", "PSG");
Stream<String> streamOfPlayers = Stream
  .of("Ronaldo", "Messi", "Salah");
Stream<String> streamOfLeagues = Stream
  .of("Serie A", "La Liga", "Premier League");

Set<String> merged = StreamUtils.merge(
  () ->  "",
  (valOne, valTwo) -> valOne + " " + valTwo,
  streamOfClubs,
  streamOfPlayers,
  streamOfLeagues)
  .collect(Collectors.toSet());

assertThat(merged)
  .contains("Juventus Ronaldo Serie A", "Barcelona Messi La Liga", 
    "Liverpool Salah Premier League", "PSG");
```

### 3.5. 合并列表()

mergeToList()将多个流作为输入。它将每个流中相同索引的值合并到一个 列表中：

```java
Stream<String> streamOfClubs = Stream
  .of("Juventus", "Barcelona", "PSG");
Stream<String> streamOfPlayers = Stream
  .of("Ronaldo", "Messi");

Stream<List<String>> mergedStreamOfList = StreamUtils
  .mergeToList(streamOfClubs, streamOfPlayers);
List<List<String>> mergedListOfList = mergedStreamOfList
  .collect(Collectors.toList());

assertThat(mergedListOfList.get(0))
  .containsExactly("Juventus", "Ronaldo");
assertThat(mergedListOfList.get(1))
  .containsExactly("Barcelona", "Messi");
assertThat(mergedListOfList.get(2))
  .containsExactly("PSG");
```

### 3.6. 交织()

interleave()使用 选择器创建从多个流中获取的交替值。

该方法将包含每个流中的一个值的集合提供给选择器， 选择器将选择一个值。

然后，所选值将从集合中删除，并替换为所选值所源自的下一个值。这种迭代一直持续到所有源都用完值为止。

下一个示例使用 interleave() 通过循环策略创建交替值 ：

```java
Stream<String> streamOfClubs = Stream
  .of("Juventus", "Barcelona", "Liverpool");
Stream<String> streamOfPlayers = Stream
  .of("Ronaldo", "Messi");
Stream<String> streamOfLeagues = Stream
  .of("Serie A", "La Liga");

List<String> interleavedList = StreamUtils
  .interleave(Selectors.roundRobin(), streamOfClubs, streamOfPlayers, streamOfLeagues)
  .collect(Collectors.toList());
  
assertThat(interleavedList)
  .hasSize(7)
  .containsExactly("Juventus", "Ronaldo", "Serie A", "Barcelona", "Messi", "La Liga", "Liverpool");

```

请注意，以上代码仅用于教程目的，因为循环选择器 由库提供为 [Selectors.roundRobin()](https://github.com/poetix/protonpack/blob/master/src/main/java/com/codepoetics/protonpack/selectors/Selectors.java)。

### 3.7. skipUntil() 和 skipWhile()

skipUntil()跳过值直到值满足条件：

```java
Integer[] numbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
List skippedUntilGreaterThan5 = StreamUtils
  .skipUntil(stream(numbers), i -> i > 5)
  .collect(Collectors.toList());
 
assertThat(skippedUntilGreaterThan5).containsExactly(6, 7, 8, 9, 10);

```

相反， 当值满足条件时， skipWhile() 会跳过这些值：

```java
Integer[] numbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
List skippedWhileLessThanEquals5 = StreamUtils
  .skipWhile(stream(numbers), i -> i <= 5 || )
  .collect(Collectors.toList());
 
assertThat(skippedWhileLessThanEquals5).containsExactly(6, 7, 8, 9, 10);

```

关于skipWhile() 的一个重要事情是它会在找到第一个不满足条件的值后继续流式传输：

```java
List skippedWhileGreaterThan5 = StreamUtils
  .skipWhile(stream(numbers), i -> i > 5)
  .collect(Collectors.toList());
assertThat(skippedWhileGreaterThan5).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

```

在Java9 之后， 标准Stream API 中的[dropWhile ( )](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/stream/Stream.html#dropWhile(java.util.function.Predicate))提供与skipWhile()相同的功能 。

### 3.8. 展开()

unfold() 通过将自定义生成器应用于种子值然后应用于每个生成的值来生成潜在的无限流——可以通过返回 Optional.empty() 来终止流：

```java
Stream<Integer> unfolded = StreamUtils
  .unfold(2, i -> (i < 100) 
    ? Optional.of(i  i) : Optional.empty());

assertThat(unfolded.collect(Collectors.toList()))
  .containsExactly(2, 4, 16, 256);
```

### 3.9. 窗口化()

windowed() 创建源流的多个子集作为 List的流。 该方法将源流、窗口大小和跳过值作为参数。

列表长度等于窗口 大小， 而 s kip 值确定子集相对于前一个子集的开始位置：

```java
Integer[] numbers = { 1, 2, 3, 4, 5, 6, 7, 8 };

List<List> windowedWithSkip1 = StreamUtils
  .windowed(stream(numbers), 3, 1)
  .collect(Collectors.toList());
assertThat(windowedWithSkip1)
  .containsExactly(asList(1, 2, 3), asList(2, 3, 4), asList(3, 4, 5), asList(4, 5, 6), asList(5, 6, 7));

```

此外，最后一个窗口保证具有所需的大小，如我们在以下示例中所见：

```java
List<List> windowedWithSkip2 = StreamUtils.windowed(stream(numbers), 3, 2).collect(Collectors.toList());
assertThat(windowedWithSkip2).containsExactly(asList(1, 2, 3), asList(3, 4, 5), asList(5, 6, 7));

```

### 3.10. 总计的()

有两种 aggregate() 方法的工作方式截然不同。

第一个 aggregate() 根据给定的谓词将等值的元素组合在一起：

```java
Integer[] numbers = { 1, 2, 2, 3, 4, 4, 4, 5 };
List<List> aggregated = StreamUtils
  .aggregate(Arrays.stream(numbers), (int1, int2) -> int1.compareTo(int2) == 0)
  .collect(Collectors.toList());
assertThat(aggregated).containsExactly(asList(1), asList(2, 2), asList(3), asList(4, 4, 4), asList(5));

```

谓词以连续的方式接收值。因此，如果数字未排序，上述将给出不同的结果。

另一方面，第二个aggregate()仅用于 将源流中的元素分组到所需大小的组中：

```java
List<List> aggregatedFixSize = StreamUtils
  .aggregate(stream(numbers), 5)
  .collect(Collectors.toList());
assertThat(aggregatedFixSize).containsExactly(asList(1, 2, 2, 3, 4), asList(4, 4, 5));

```

### 3.11. aggregateOnListCondition()

aggregateOnListCondition()根据谓词和当前活动组对 值进行分组。谓词被赋予当前活动组作为 列表和下一个值。然后它必须确定该组是否应该继续或开始一个新组。

以下示例解决了将连续整数值分组到一个组中的要求，其中每个组中的值之和不得大于 5：

```java
Integer[] numbers = { 1, 1, 2, 3, 4, 4, 5 };
Stream<List<Integer>> aggregated = StreamUtils
  .aggregateOnListCondition(stream(numbers), 
    (currentList, nextInt) -> currentList.stream().mapToInt(Integer::intValue).sum() + nextInt <= 5);
assertThat(aggregated)
  .containsExactly(asList(1, 1, 2), asList(3), asList(4), asList(4), asList(5));
```

## 4.可流<T>

Stream 的实例不可重用。出于这个原因， Streamable 通过包装和公开与 Stream 相同的方法来提供可重用的 流：

```java
Streamable<String> s = Streamable.of("a", "b", "c", "d");
List<String> collected1 = s.collect(Collectors.toList());
List<String> collected2 = s.collect(Collectors.toList());
assertThat(collected1).hasSize(4);
assertThat(collected2).hasSize(4);
```

## 5.收集器工具

CollectorUtils通过添加几个有用的收集器方法来补充标准 收集器。

### 5.1. maxBy() 和 minBy()

maxBy() 使用提供的投影逻辑在流中找到最大值：

```java
Stream<String> clubs = Stream.of("Juventus", "Barcelona", "PSG");
Optional<String> longestName = clubs.collect(CollectorUtils.maxBy(String::length));
assertThat(longestName).contains("Barcelona");
```

相反， minBy() 使用提供的投影逻辑找到最小值。

### 5.2. 独特的()

unique()收集器做了一件非常简单的事情：如果给定的流只有 1 个元素，它返回唯一的值：

```java
Stream<Integer> singleElement = Stream.of(1);
Optional<Integer> unique = singleElement.collect(CollectorUtils.unique());
assertThat(unique).contains(1);

```

否则，unique() 将抛出异常：

```java
Stream multipleElement = Stream.of(1, 2, 3);
assertThatExceptionOfType(NonUniqueValueException.class).isThrownBy(() -> {
    multipleElement.collect(CollectorUtils.unique());
});

```

## 六. 总结

在本文中，我们了解了 Protonpack 库如何扩展JavaStream API 以使其更易于使用。它添加了我们可能常用但标准 API 中缺少的有用方法。

从Java9 开始，Protonpack 提供的一些功能将在标准 Stream API 中可用。