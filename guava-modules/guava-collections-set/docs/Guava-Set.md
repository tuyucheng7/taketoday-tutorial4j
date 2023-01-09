## 1. 概述

在本教程中，我们将说明你可以利用 Guava 与JavaSets 一起工作的最有用的方法。

让我们从非常简单的开始，使用 Guava创建一个没有 new 运算符的HashSet ：

```java
Set<String> aNewSet = Sets.newHashSet();
```

## 2. 集合并集

首先，让我们看一下如何对集合进行联合操作——使用简单的Sets.union() API：

```java
@Test
public void whenCalculatingUnionOfSets_thenCorrect() {
    Set<Character> first = ImmutableSet.of('a', 'b', 'c');
    Set<Character> second = ImmutableSet.of('b', 'c', 'd');

    Set<Character> union = Sets.union(first, second);
    assertThat(union, containsInAnyOrder('a', 'b', 'c', 'd'));
}
```

## 3. 集合的笛卡尔积

我们还可以使用Sets.cartesianProduct()获得两个集合的乘积，如下例所示：

```java
@Test
public void whenCalculatingCartesianProductOfSets_thenCorrect() {
    Set<Character> first = ImmutableSet.of('a', 'b');
    Set<Character> second = ImmutableSet.of('c', 'd');
    Set<List<Character>> result =
      Sets.cartesianProduct(ImmutableList.of(first, second));

    Function<List<Character>, String> func =
      new Function<List<Character>, String>() {
        public String apply(List<Character> input) {
            return Joiner.on(" ").join(input);
        }
    };
    Iterable<String> joined = Iterables.transform(result, func);
    assertThat(joined, containsInAnyOrder("a c", "a d", "b c", "b d"));
}
```

请注意——为了能够轻松测试结果，我们使用函数和连接器将复杂的Set<List<Character>>结构转换为更易于管理的Iterable<String>。

## 4.设置交叉口

接下来——让我们看看如何使用Sets.intersection() API获取两个集合之间的交集：

```java
@Test
public void whenCalculatingSetIntersection_thenCorrect() {
    Set<Character> first = ImmutableSet.of('a', 'b', 'c');
    Set<Character> second = ImmutableSet.of('b', 'c', 'd');

    Set<Character> intersection = Sets.intersection(first, second);
    assertThat(intersection, containsInAnyOrder('b', 'c'));
}
```

## 5. 集合的对称差分

现在，让我们看一下两个集合的对称差异——所有元素都包含在集合 1 或集合 2 中，但不包含在两者中：

```java
@Test
public void whenCalculatingSetSymmetricDifference_thenCorrect() {
    Set<Character> first = ImmutableSet.of('a', 'b', 'c');
    Set<Character> second = ImmutableSet.of('b', 'c', 'd');

    Set<Character> intersection = Sets.symmetricDifference(first, second);
    assertThat(intersection, containsInAnyOrder('a', 'd'));
}
```

## 6.动力装置

现在——让我们看看如何计算幂集——该集合的所有可能子集的集合。

在以下示例中——我们使用Sets.powerSet()来计算给定字符集的幂集：

```java
@Test
public void whenCalculatingPowerSet_thenCorrect() {
    Set<Character> chars = ImmutableSet.of('a', 'b');

    Set<Set<Character>> result = Sets.powerSet(chars);

    Set<Character> empty =  ImmutableSet.<Character> builder().build();
    Set<Character> a = ImmutableSet.of('a');
    Set<Character> b = ImmutableSet.of('b');
    Set<Character> aB = ImmutableSet.of('a', 'b');

    assertThat(result, contains(empty, a, b, aB));
}
```

## 7.连续集

接下来——让我们看一下一组经过排序的连续值——ContiguousSet。

在下面的示例中——我们将一组整数 [10, 11, …, 30] 放入ContiguousSet中：

```java
@Test
public void whenCreatingRangeOfIntegersSet_thenCreated() {
    int start = 10;
    int end = 30;
    ContiguousSet<Integer> set = ContiguousSet.create(
      Range.closed(start, end), DiscreteDomain.integers());

    assertEquals(21, set.size());
    assertEquals(10, set.first().intValue());
    assertEquals(30, set.last().intValue());
}
```

这种类型的数据结构当然是你可以在纯Java中使用TreeSet来实现的——但是如果你需要以这种方式表示数据，那么这种特殊类型的集合的语义会更好用。

## 8.范围集

现在 - 让我们来看看RangeSet。我们可以使用RangeSet来保存断开的和非空的范围。

在下面的示例中——从 2 个断开连接的范围开始，然后我们将它们连接成一个大范围：

```java
@Test
public void whenUsingRangeSet_thenCorrect() {
    RangeSet<Integer> rangeSet = TreeRangeSet.create();
    rangeSet.add(Range.closed(1, 10));
    rangeSet.add(Range.closed(12, 15));

    assertEquals(2, rangeSet.asRanges().size());

    rangeSet.add(Range.closed(10, 12));
    assertTrue(rangeSet.encloses(Range.closed(1, 15)));
    assertEquals(1, rangeSet.asRanges().size());
}
```

让我们详细看一下这个例子：


-   首先——我们插入 2 个不相连的范围：[1, 10]和[12, 15]
-   接下来——我们添加第三个范围来连接现有的 2：[10, 12]
-   最后——我们验证RangeSet是否足够聪明，可以看到 3 个范围现在是一个大范围，并将它们合并为：[1, 15]

## 9.多组

接下来——让我们讨论如何使用Multiset。与普通集合相反，Multiset确实支持添加重复元素——它计算为出现次数。

在下面的例子中——我们通过一些简单的多集逻辑：

```java
@Test
public void whenInsertDuplicatesInMultiSet_thenInserted() {
    Multiset<String> names = HashMultiset.create();
    names.add("John");
    names.add("Adam", 3);
    names.add("John");

    assertEquals(2, names.count("John"));
    names.remove("John");
    assertEquals(1, names.count("John"));

    assertEquals(3, names.count("Adam"));
    names.remove("Adam", 2);
    assertEquals(1, names.count("Adam"));
}
```

## 10. 获取MultiSet中的前 N 个元素

现在 - 让我们看一个使用MultiSet的更复杂和有用的示例。我们将获得集合中出现次数最多的 N 个元素——基本上是最常见的元素。

在下面的示例中——我们使用Multisets.copyHighCountFirst()对Multiset中的元素进行排序：

```java
@Test
public void whenGetTopOcurringElementsWithMultiSet_thenCorrect() {
    Multiset<String> names = HashMultiset.create();
    names.add("John");
    names.add("Adam", 5);
    names.add("Jane");
    names.add("Tom", 2);

    Set<String> sorted = Multisets.copyHighestCountFirst(names).elementSet();
    List<String> sortedAsList = Lists.newArrayList(sorted);
    assertEquals("Adam", sortedAsList.get(0));
    assertEquals("Tom", sortedAsList.get(1));
}
```

## 11.总结

在本快速教程中，我们讨论了使用 Guava 库处理集合的最常见和最有用的用例。