## 一、简介

在本文中，我们将学习在Java*列表*中查找重复项的不同方法。

给定一个包含重复元素的整数列表，我们将在其中找到重复元素。例如，给定输入列表*[1, 2, 3, 3, 4, 4, 5]，* 输出*列表*将为*[3, 4]*。

## *2. 使用Collection*查找重复项

在本节中，我们将讨论使用*Collections* 提取列表中存在的重复元素的两种方法。

### 2.1. 使用*Set的**contains()*方法

*Java 中的Set*不包含重复项。*Set*中的contains *()*方法仅当元素已存在于其中时才返回*true 。*

如果*contains()*返回*false ，我们将向**Set*添加元素。否则，我们会将元素添加到输出列表中。因此，输出列表包含重复元素：

```java
List<Integer> listDuplicateUsingSet(List<Integer> list) {
    List<Integer> duplicates = new ArrayList<>();
    Set<Integer> set = new HashSet<>();
    for (Integer i : list) {
        if (set.contains(i)) {
            duplicates.add(i);
        } else {
            set.add(i);
        }
    }
    return duplicates;
}复制
```

让我们编写一个测试来检查列表*重复项*是否仅包含重复元素：

```java
@Test
void givenList_whenUsingSet_thenReturnDuplicateElements() {
    List<Integer> list = Arrays.asList(1, 2, 3, 3, 4, 4, 5);
    List<Integer> duplicates = listDuplicate.listDuplicateUsingSet(list);
    Assert.assertEquals(duplicates.size(), 2);
    Assert.assertEquals(duplicates.contains(3), true);
    Assert.assertEquals(duplicates.contains(4), true);
    Assert.assertEquals(duplicates.contains(1), false);
}复制
```

这里我们看到输出列表只包含两个元素*3*和*4*。

**这种方法对列表中的 n 个元素花费[O(n) 时间，对集合占用大小为 n 的额外空间。](https://www.baeldung.com/java-collections-complexity)**

### 2.2. 使用*地图*并存储元素的频率

我们可以使用*Map*来存储每个元素的频率，然后仅当元素的频率不为*1*时才将它们添加到输出列表：

```java
List<Integer> listDuplicateUsingMap(List<Integer> list) {
    List<Integer> duplicates = new ArrayList<>();
    Map<Integer, Integer> frequencyMap = new HashMap<>();
    for (Integer number : list) {
        frequencyMap.put(number, frequencyMap.getOrDefault(number, 0) + 1);
    }
    for (int number : frequencyMap.keySet()) {
        if (frequencyMap.get(number) != 1) {
            duplicates.add(number);
        }
    }
    return duplicates;
}
复制
```

让我们编写一个测试来检查列表重复项是否仅包含重复元素：

```java
@Test
void givenList_whenUsingFrequencyMap_thenReturnDuplicateElements() {
    List<Integer> list = Arrays.asList(1, 2, 3, 3, 4, 4, 5);
    List<Integer> duplicates = listDuplicate.listDuplicateUsingMap(list);
    Assert.assertEquals(duplicates.size(), 2);
    Assert.assertEquals(duplicates.contains(3), true);
    Assert.assertEquals(duplicates.contains(4), true);
    Assert.assertEquals(duplicates.contains(1), false);
}
复制
```

这里我们看到输出列表只包含两个元素，*3*和*4*。

**这种方法对列表中的 n 个元素花费 O(n) 时间，对映射占用大小为 n 的额外空间。**

## 3.在 Java 8 中使用*Stream*

在本节中，我们将讨论使用*Stream*提取列表中存在的重复元素的三种方法。

### 3.1. 使用*filter()*和*Set.add()*方法

*Set.add()*将指定的元素添加到此集合中（如果它不存在）。如果此集合已包含该元素，则调用保持集合不变并返回*false*。

在这里，我们将使用*Set*并将列表转换为流。将stream添加到*Set中，*将重复的元素[过滤](https://www.baeldung.com/java-stream-filter-lambda)收集到*List中：*

```java
List<Integer> listDuplicateUsingFilterAndSetAdd(List<Integer> list) {
    Set<Integer> elements = new HashSet<Integer>();
    return list.stream()
      .filter(n -> !elements.add(n))
      .collect(Collectors.toList());
}
复制
```

让我们编写一个测试来检查列表重复项是否仅包含重复元素：

```java
@Test
void givenList_whenUsingFilterAndSetAdd_thenReturnDuplicateElements() {
    List<Integer> list = Arrays.asList(1, 2, 3, 3, 4, 4, 5);
    List<Integer> duplicates = listDuplicate.listDuplicateUsingFilterAndSetAdd(list);
    Assert.assertEquals(duplicates.size(), 2);
    Assert.assertEquals(duplicates.contains(3), true);
    Assert.assertEquals(duplicates.contains(4), true);
    Assert.assertEquals(duplicates.contains(1), false);
}
复制
```

在这里，我们看到输出元素仅包含两个元素，如预期的那样， *3*和*4*。

**这种将\*filter()\*与\*Set.add()\*结合使用的方法是查找具有 O(n) 时间复杂度和集合大小为 n 的额外空间的重复元素的最快算法。**

### 3.2. 使用*Collections.frequency()*

*Collections.frequency()*返回指定集合中元素的数量，该数量等于指定值。*在这里，我们将List*转换为*Stream并仅从**Collections.frequency()*中过滤掉返回值大于 1 的元素。

我们会将这些元素收集到*Set*中以避免重复，最后将*Set*转换为*List*：

```java
List<Integer> listDuplicateUsingCollectionsFrequency(List<Integer> list) {
    List<Integer> duplicates = new ArrayList<>();
    Set<Integer> set = list.stream()
      .filter(i -> Collections.frequency(list, i) > 1)
      .collect(Collectors.toSet());
    duplicates.addAll(set);
    return duplicates;
}复制
```

让我们编写一个测试来检查重复项是否仅包含重复元素：

```java
@Test
void givenList_whenUsingCollectionsFrequency_thenReturnDuplicateElements() {
    List<Integer> list = Arrays.asList(1, 2, 3, 3, 4, 4, 5);
    List<Integer> duplicates = listDuplicate.listDuplicateUsingCollectionsFrequency(list);
    Assert.assertEquals(duplicates.size(), 2);
    Assert.assertEquals(duplicates.contains(3), true);
    Assert.assertEquals(duplicates.contains(4), true);
    Assert.assertEquals(duplicates.contains(1), false);
}
复制
```

正如预期的那样，输出列表仅包含两个元素，3 和 4。

**这种使用\*Collections.frequency()\*的方法是最慢的，因为它将每个元素与一个列表进行比较——Collections.frequency \*(list, i)\*，其复杂度为 O(n)。所以整体复杂度为 O(n\*n)。它还需要一个大小为 n 的额外空间用于集合。**

### 3.3. 使用*Map*和*Collectors.groupingBy()*

[*Collectors.groupingBy()*](https://www.baeldung.com/java-groupingby-collector)返回一个收集器，该收集器对输入元素实施级联“分组依据”操作。

它根据分类函数对元素进行分组，然后使用指定的下游收集器对具有给定键的关联值执行归约操作。分类函数将元素映射到某个键类型*K*。*下游收集器对输入元素进行操作并产生D*类型的结果。生成的收集器生成一个*Map<K, D>*。

在这里，我们将使用*Function.identity()*作为分类函数，使用*Collectors.counting()*作为下游收集器。

*Function.identity()*返回一个始终返回其输入参数的函数。*Collectors.counting()*返回一个接受元素的收集器，该元素计算输入元素的数量。如果不存在任何元素，则结果为零。*因此，我们将使用Collectors.groupingBy()*获得元素及其频率的映射。

然后我们将这个*Map的**EntrySet*转成一个*Stream*，只过滤掉大于1的元素，收集到一个*Set*中，避免重复。然后 将*Set*转换为*List：* 

```java
List<Integer> listDuplicateUsingMapAndCollectorsGroupingBy(List<Integer> list) {
    List<Integer> duplicates = new ArrayList<>();
    Set<Integer> set = list.stream()
      .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
      .entrySet()
      .stream()
      .filter(m -> m.getValue() > 1)
      .map(Map.Entry::getKey)
      .collect(Collectors.toSet());
    duplicates.addAll(set);
    return duplicates;
}复制
```

让我们编写一个测试来检查列表*重复项*是否仅包含重复元素：

```java
@Test
void givenList_whenUsingMapAndCollectorsGroupingBy_thenReturnDuplicateElements() {
    List<Integer> list = Arrays.asList(1, 2, 3, 3, 4, 4, 5);
    List<Integer> duplicates = listDuplicate.listDuplicateUsingCollectionsFrequency(list);
    Assert.assertEquals(duplicates.size(), 2);
    Assert.assertEquals(duplicates.contains(3), true);
    Assert.assertEquals(duplicates.contains(4), true);
    Assert.assertEquals(duplicates.contains(1), false);
}
复制
```

这里我们看到输出元素只包含两个元素*3*和*4*。

***Collectors.groupingBy()\*需要 O(n) 时间。对生成的 EntrySet 执行 filter() 操作，\*但\**复杂\*度仍然为 O(n)，因为地图查找时间为 O(1)。它还需要一个额外的空间 n 用于集合。**

## 4。结论

在本文中，我们了解了在 Java 中从*List*中提取重复元素的不同方法。

我们讨论了使用*Set*和*Map 的*方法及其使用*Stream 的*相应方法。*使用Stream 的*代码 更具声明性，并且无需外部迭代器即可清楚地传达代码的意图。