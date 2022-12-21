## 1. 概述

本文重点介绍[AssertJ](https://joel-costigliola.github.io/assertj/) Guava相关的断言，这是AssertJ系列的第二篇文章，如果你想了解有关AssertJ的一般信息，请查看AssertJ系列中的[第一篇]()文章。

## 2. Maven依赖

为了将AssertJ与Guava一起使用，你需要将以下依赖项添加到你的pom.xml中：

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-guava</artifactId>
    <version>3.0.0</version>
    <scope>test</scope>
</dependency>
```

请注意，从3.0.0版本开始，AssertJ Guava依赖于Java 8和AssertJ Core 3.x。

## 3. Guava断言实践

AssertJ具有Guava类型的自定义断言：ByteSource、Multimap、Optional、Range、RangeMap和Table。

### 3.1 ByteSource断言

首先我们创建两个空的临时文件：

```java
File temp1 = File.createTempFile("tuu", "yucheng1");
File temp2 = File.createTempFile("tuu", "yucheng2");
```

并从中创建ByteSource实例：

```java
ByteSource byteSource1 = Files.asByteSource(temp1);
ByteSource byteSource2 = Files.asByteSource(temp2);
```

现在我们可以编写以下断言：

```java
assertThat(buteSource1)
    .hasSize(0)
    .hasSameContentAs(byteSource2);
```

### 3.2 Multimap断言

Multimap是可以将多个值与给定键关联的Map，Multimap断言的使用方式与普通Map实现非常相似。

我们首先创建一个Multimap实例并添加一些键值对：

```java
Multimap<Integer, String> mmap = Multimaps.newMultimap(new HashMap<>(), Sets::newHashSet);
mmap.put(1, "one");
mmap.put(1, "1");
```

现在我们可以断言：

```java
assertThat(mmap)
    .hasSize(2)
    .containsKeys(1)
    .contains(entry(1, "one"))
    .contains(entry(1, "1"));
```

还有两个额外的断言可用，它们之间有细微的区别：

-   containsAllEntriesOf
-   hasSameEntriesAs

首先，我们定义一些Map：

```java
final Multimap<Integer, String> mmap1 = ArrayListMultimap.create();
mmap1.put(1, "one");
mmap1.put(1, "1");
mmap1.put(2, "two");
mmap1.put(2, "2");

final Multimap<Integer, String> mmap1_clone = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
mmap1_clone.put(1, "one");
mmap1_clone.put(1, "1");
mmap1_clone.put(2, "two");
mmap1_clone.put(2, "2");

final Multimap<Integer, String> mmap2 = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);
mmap2.put(1, "one");
mmap2.put(1, "1");
```

如你所见，mmap1和mmap1_clone包含完全相同的键值对，但是它们是两种不同Map类型的两个不同对象。mmap2包含一个在所有Map之间共享的键值对。以下断言是正确的：

```java
assertThat(mmap1)
    .containsAllEntriesOf(mmap2)
    .containsAllEntriesOf(mmap1_clone)
    .hasSameEntriesAs(mmap1_clone);
```

### 3.3 Optional断言

Guava的Optional的断言涉及值存在性检查和用于提取内部值的工具方法，首先我们创建一个Optional实例：

```java
Optional<String> something = Optional.of("something");
```

现在我们可以检查值的存在性并断言Optional的内容：

```java
assertThat(something)
    .isPresent()
    .extractingValue()
    .isEqualTo("something");
```

### 3.4 Range断言

Guava的Range类断言涉及检查Range的下限和上限，或者某个值是否在给定范围内。

我们通过执行以下操作来定义一个简单的字符范围：

```java
Range<String> range = Range.openClosed("a", "g");
```

现在我们可以执行以下测试：

```java
assertThat(range)
    .hasOpenedLowerBound()
    .isNotEmpty()
    .hasClosedUpperBound()
    .contains("b");
```

### 3.5 Table断言

AssertJ的特定于Table的断言允许检查行数和列数以及单元格值的存在。

让我们创建一个简单的Table实例：

```java
Table<Integer, String, String> table = HashBasedTable.create(2, 2);
table.put(1, "A", "PRESENT");
table.put(1, "B", "ABSENT");
```

现在我们可以执行以下断言：

```java
assertThat(table)
    .hasRowCount(1)
    .containsValues("ABSENT")
    .containsCell(1, "B", "ABSENT");
```

## 4. 总结

在AssertJ系列的这篇文章中，我们探索了所有与Guava相关的特性。