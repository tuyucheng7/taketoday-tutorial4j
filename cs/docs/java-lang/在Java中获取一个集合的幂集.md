## 1. 概述

在本教程中，我们将研究在Java中生成给定集合的[幂集的过程。](https://en.wikipedia.org/wiki/Power_set)

快速提醒一下，对于每个大小为n的集合，都有一个大小为2 n的幂集。我们将学习如何使用各种技术来获取它。

## 2.幂集的定义

给定集合S的幂集是S的所有子集的集合，包括S本身和空集。

例如，对于给定的集合：

```plaintext
{"APPLE", "ORANGE", "MANGO"}
```

幂集是：

```plaintext
{
    {},
    {"APPLE"},
    {"ORANGE"},
    {"APPLE", "ORANGE"},
    {"MANGO"},
    {"APPLE", "MANGO"},
    {"ORANGE", "MANGO"},
    {"APPLE", "ORANGE", "MANGO"}
}
```

由于它也是一组子集，因此其内部子集的顺序并不重要，它们可以以任何顺序出现：

```plaintext
{
    {},
    {"MANGO"},
    {"ORANGE"},
    {"ORANGE", "MANGO"},
    {"APPLE"},
    {"APPLE", "MANGO"},
    {"APPLE", "ORANGE"},
    {"APPLE", "ORANGE", "MANGO"}
}
```

## 3.番石榴图书馆

Google [Guava 库有一些有用的Set utilities](https://www.baeldung.com/guava-sets)，例如 power set。因此，我们也可以很容易地使用它来获得给定集合的幂集：

```java
@Test
public void givenSet_WhenGuavaLibraryGeneratePowerSet_ThenItContainsAllSubsets() {
    ImmutableSet<String> set = ImmutableSet.of("APPLE", "ORANGE", "MANGO");
    Set<Set<String>> powerSet = Sets.powerSet(set);
    Assertions.assertEquals((1 << set.size()), powerSet.size());
    MatcherAssert.assertThat(powerSet, Matchers.containsInAnyOrder(
      ImmutableSet.of(),
      ImmutableSet.of("APPLE"),
      ImmutableSet.of("ORANGE"),
      ImmutableSet.of("APPLE", "ORANGE"),
      ImmutableSet.of("MANGO"),
      ImmutableSet.of("APPLE", "MANGO"),
      ImmutableSet.of("ORANGE", "MANGO"),
      ImmutableSet.of("APPLE", "ORANGE", "MANGO")
   ));
}
```

Guava powerSet在内部操作Iterator接口，当请求下一个子集时，计算并返回子集。因此，空间复杂度减少到O(n)而不是O(2 n )。

但是，Guava 是如何做到这一点的呢？

## 4. 幂集生成方法

### 4.1. 算法

现在让我们讨论为该操作创建算法的可能步骤。

空集的幂集是{{}}其中它只包含一个空集，所以这是我们最简单的情况。

对于除空集之外的每个集合S，我们首先提取一个元素并将其命名为 - element。然后，对于集合 subsetWithoutElement的其余元素，我们递归地计算它们的幂集——并将其命名为类似powerSet S ubsetWithoutElement的东西。然后，通过将提取的 元素添加到powerSet S ubsetWithoutElement中的所有集合，我们得到powerSet S ubsetWithElement。

现在，幂集S是powerSetSubsetWithoutElement和powerSetSubsetWithElement的并集：

[
](https://www.baeldung.com/wp-content/uploads/2020/01/powerSet-Example.png)[![powerSet 算法 1](https://www.baeldung.com/wp-content/uploads/2020/01/powerSet-Algorithm-1.png)](https://www.baeldung.com/wp-content/uploads/2020/01/powerSet-Algorithm-1.png)

让我们看一下给定集合{“APPLE”, “ORANGE”, “MANGO”}的递归幂集堆栈的示例。

为了提高图像的可读性，我们使用缩写形式的名称：P表示幂集函数，“A”、“O”、“M”分别是“APPLE”、“ORANGE”和“MANGO”的缩写形式：

[![幂集示例](https://www.baeldung.com/wp-content/uploads/2020/01/powerSet-Example.png)](https://www.baeldung.com/wp-content/uploads/2020/01/powerSet-Example.png)

### 4.2. 执行

因此，首先，让我们编写提取一个元素并获取剩余子集的Java代码：

```java
T element = set.iterator().next();
Set<T> subsetWithoutElement = new HashSet<>();
for (T s : set) {
    if (!s.equals(element)) {
        subsetWithoutElement.add(s);
    }
}
```

然后我们想要获得 subsetWithoutElement 的幂集：

```java
Set<Set<T>> powersetSubSetWithoutElement = recursivePowerSet(subsetWithoutElement);
```

接下来，我们需要将该 powerset 添加回原来的：

```java
Set<Set<T>> powersetSubSetWithElement = new HashSet<>();
for (Set<T> subsetWithoutElement : powerSetSubSetWithoutElement) {
    Set<T> subsetWithElement = new HashSet<>(subsetWithoutElement);
    subsetWithElement.add(element);
    powerSetSubSetWithElement.add(subsetWithElement);
}
```

最后 powerSetSubSetWithoutElement和 powerSetSubSetWithElement的并集是给定输入集的幂集：

```java
Set<Set<T>> powerSet = new HashSet<>();
powerSet.addAll(powerSetSubSetWithoutElement);
powerSet.addAll(powerSetSubSetWithElement);
```

如果我们将所有代码片段放在一起，我们可以看到我们的最终产品：

```java
public Set<Set<T>> recursivePowerSet(Set<T> set) {
    if (set.isEmpty()) {
        Set<Set<T>> ret = new HashSet<>();
        ret.add(set);
        return ret;
    }

    T element = set.iterator().next();
    Set<T> subSetWithoutElement = getSubSetWithoutElement(set, element);
    Set<Set<T>> powerSetSubSetWithoutElement = recursivePowerSet(subSetWithoutElement);
    Set<Set<T>> powerSetSubSetWithElement = addElementToAll(powerSetSubSetWithoutElement, element);

    Set<Set<T>> powerSet = new HashSet<>();
    powerSet.addAll(powerSetSubSetWithoutElement);
    powerSet.addAll(powerSetSubSetWithElement);
    return powerSet;
}

```

### 4.3. 单元测试注意事项

现在让我们测试一下。我们在这里有一些标准来确认：

-   首先，我们检查幂集的大小，对于大小为n的集合，它必须为2 n。
-   然后，每个元素将在一个子集中和2 n-1 个不同的子集中只出现一次。
-   最后，每个子集必须出现一次。

如果所有这些条件都通过，我们就可以确定我们的函数可以正常工作。现在，由于我们使用了Set<Set>，我们已经知道没有重复。在那种情况下，我们只需要检查幂集的大小，以及每个元素在子集中出现的次数。

要检查幂集的大小，我们可以使用：

```java
MatcherAssert.assertThat(powerSet, IsCollectionWithSize.hasSize((1 << set.size())));
```

并检查每个元素的出现次数：

```java
Map<String, Integer> counter = new HashMap<>();
for (Set<String> subset : powerSet) { 
    for (String name : subset) {
        int num = counter.getOrDefault(name, 0);
        counter.put(name, num + 1);
    }
}
counter.forEach((k, v) -> Assertions.assertEquals((1 << (set.size() - 1)), v.intValue()));
```

最后，如果我们可以将所有内容放在一个单元测试中：

```java
@Test
public void givenSet_WhenPowerSetIsCalculated_ThenItContainsAllSubsets() {
    Set<String> set = RandomSetOfStringGenerator.generateRandomSet();
    Set<Set<String>> powerSet = new PowerSet<String>().recursivePowerSet(set);
    MatcherAssert.assertThat(powerSet, IsCollectionWithSize.hasSize((1 << set.size())));
   
    Map<String, Integer> counter = new HashMap<>();
    for (Set<String> subset : powerSet) {
        for (String name : subset) {
            int num = counter.getOrDefault(name, 0);
            counter.put(name, num + 1);
        }
    }
    counter.forEach((k, v) -> Assertions.assertEquals((1 << (set.size() - 1)), v.intValue()));
}
```

## 5.优化

在本节中，我们将尽量减少空间并减少内部操作的数量，以最佳方式计算幂集。

### 5.1. 数据结构

正如我们在给定方法中看到的那样，我们需要在递归调用中进行大量减法运算，这会消耗大量时间和内存。

相反，我们可以将每个集合或子集映射到一些其他概念以减少操作次数。

首先，我们需要为给定集合S中的每个对象分配一个从 0 开始递增的数字，这意味着我们使用有序的数字列表。

例如，对于给定的集合{“APPLE”、“ORANGE”、“MANGO”}，我们得到：

“苹果”-> 0

“橙色”-> 1

“芒果” -> 2

因此，从现在开始，我们不再生成S的子集，而是为 [0, 1, 2] 的有序列表生成它们，并且按照它的顺序，我们可以通过起始索引模拟减法。

例如，如果起始索引为 1，则表示我们生成 [1,2] 的幂集。

为了从对象中检索映射的 id，反之亦然，我们存储映射的两边。使用我们的示例，我们存储了(“MANGO” -> 2)和(2 -> “MANGO”)。由于数字的映射从零开始，因此对于反向映射，我们可以使用一个简单的数组来检索相应的对象。

此功能的可能实现之一是：

```java
private Map<T, Integer> map = new HashMap<>();
private List<T> reverseMap = new ArrayList<>();

private void initializeMap(Collection<T> collection) {
    int mapId = 0;
    for (T c : collection) {
        map.put(c, mapId++);
        reverseMap.add(c);
    }
}
```

现在，为了表示子集，有两个众所周知的想法：

1.  指标表示
2.  二进制表示

### 5.2. 指标表示

每个子集都由其值的索引表示。例如，给定集合{“APPLE”、“ORANGE”、“MANGO”}的索引映射为：

```plaintext
{
   {} -> {}
   [0] -> {"APPLE"}
   [1] -> {"ORANGE"}
   [0,1] -> {"APPLE", "ORANGE"}
   [2] -> {"MANGO"}
   [0,2] -> {"APPLE", "MANGO"}
   [1,2] -> {"ORANGE", "MANGO"}
   [0,1,2] -> {"APPLE", "ORANGE", "MANGO"}
}
```

因此，我们可以使用给定的映射从索引的子集中检索相应的集合：

```java
private Set<Set<T>> unMapIndex(Set<Set<Integer>> sets) {
    Set<Set<T>> ret = new HashSet<>();
    for (Set<Integer> s : sets) {
        HashSet<T> subset = new HashSet<>();
        for (Integer i : s) {
            subset.add(reverseMap.get(i));
        }
        ret.add(subset);
    }
    return ret;
}
```

### 5.3. 二进制表示

或者，我们可以使用二进制表示每个子集。如果该子集中存在实际集合的元素，则其各自的值为1；否则为0。

对于我们的水果示例，幂集为：

```plaintext
{
    [0,0,0] -> {}
    [1,0,0] -> {"APPLE"}
    [0,1,0] -> {"ORANGE"}
    [1,1,0] -> {"APPLE", "ORANGE"}
    [0,0,1] -> {"MANGO"}
    [1,0,1] -> {"APPLE", "MANGO"}
    [0,1,1] -> {"ORANGE", "MANGO"}
    [1,1,1] -> {"APPLE", "ORANGE", "MANGO"}
}
```

因此，我们可以使用给定的映射从二进制子集中检索相应的集合：

```java
private Set<Set<T>> unMapBinary(Collection<List<Boolean>> sets) {
    Set<Set<T>> ret = new HashSet<>();
    for (List<Boolean> s : sets) {
        HashSet<T> subset = new HashSet<>();
        for (int i = 0; i < s.size(); i++) {
            if (s.get(i)) {
                subset.add(reverseMap.get(i));
            }
        }
        ret.add(subset);
    }
    return ret;
}
```

### 5.4. 递归算法实现

在这一步中，我们将尝试使用这两种数据结构来实现前面的代码。

在调用这些函数之一之前，我们需要调用initializeMap方法来获取有序列表。此外，在创建我们的数据结构后，我们需要调用相应的unMap函数来检索实际对象：

```java
public Set<Set<T>> recursivePowerSetIndexRepresentation(Collection<T> set) {
    initializeMap(set);
    Set<Set<Integer>> powerSetIndices = recursivePowerSetIndexRepresentation(0, set.size());
    return unMapIndex(powerSetIndices);
}
```

那么，让我们尝试一下索引表示：

```java
private Set<Set<Integer>> recursivePowerSetIndexRepresentation(int idx, int n) {
    if (idx == n) {
        Set<Set<Integer>> empty = new HashSet<>();
        empty.add(new HashSet<>());
        return empty;
    }
    Set<Set<Integer>> powerSetSubset = recursivePowerSetIndexRepresentation(idx + 1, n);
    Set<Set<Integer>> powerSet = new HashSet<>(powerSetSubset);
    for (Set<Integer> s : powerSetSubset) {
        HashSet<Integer> subSetIdxInclusive = new HashSet<>(s);
        subSetIdxInclusive.add(idx);
        powerSet.add(subSetIdxInclusive);
    }
    return powerSet;
}
```

现在，让我们看看二进制方法：

```java
private Set<List<Boolean>> recursivePowerSetBinaryRepresentation(int idx, int n) {
    if (idx == n) {
        Set<List<Boolean>> powerSetOfEmptySet = new HashSet<>();
        powerSetOfEmptySet.add(Arrays.asList(new Boolean[n]));
        return powerSetOfEmptySet;
    }
    Set<List<Boolean>> powerSetSubset = recursivePowerSetBinaryRepresentation(idx + 1, n);
    Set<List<Boolean>> powerSet = new HashSet<>();
    for (List<Boolean> s : powerSetSubset) {
        List<Boolean> subSetIdxExclusive = new ArrayList<>(s);
        subSetIdxExclusive.set(idx, false);
        powerSet.add(subSetIdxExclusive);
        List<Boolean> subSetIdxInclusive = new ArrayList<>(s);
        subSetIdxInclusive.set(idx, true);
        powerSet.add(subSetIdxInclusive);
    }
    return powerSet;
}
```

### 5.5. 遍历[0, 2 n )

现在，我们可以对二进制表示进行很好的优化。如果我们看一下，我们可以看到每一行都相当于[0, 2 n ) 中数字的二进制格式。

因此，如果我们遍历从0到 2 n的数字，我们可以将该索引转换为二进制，并使用它来创建每个子集的布尔表示：

```java
private List<List<Boolean>> iterativePowerSetByLoopOverNumbers(int n) {
    List<List<Boolean>> powerSet = new ArrayList<>();
    for (int i = 0; i < (1 << n); i++) {
        List<Boolean> subset = new ArrayList<>(n);
        for (int j = 0; j < n; j++)
            subset.add(((1 << j) & i) > 0);
        powerSet.add(subset);
    }
    return powerSet;
}
```

### 5.6. 格雷码的最小变化子集

现在，如果我们定义从长度n的二进制表示到[0, 2 n )中的数字的任何[双射](https://en.wikipedia.org/wiki/Bijection)函数，我们可以按我们想要的任何顺序生成子集。

[格雷码](https://en.wikipedia.org/wiki/Gray_code)是一个众所周知的函数，用于生成数字的二进制表示，使得连续数字的二进制表示仅相差一位(即使最后一个数字和第一个数字的差是一个)。

因此，我们可以进一步优化它：

```java
private List<List<Boolean>> iterativePowerSetByLoopOverNumbersWithGrayCodeOrder(int n) {
    List<List<Boolean>> powerSet = new ArrayList<>();
    for (int i = 0; i < (1 << n); i++) {
        List<Boolean> subset = new ArrayList<>(n);
        for (int j = 0; j < n; j++) {
            int grayEquivalent = i ^ (i >> 1);
            subset.add(((1 << j) & grayEquivalent) > 0);
        }
        powerSet.add(subset);
    }
    return powerSet;
}
```

## 6.延迟加载

为了最小化幂集的空间使用，即O(2 n )，我们可以利用Iterator接口来延迟获取每个子集，以及每个子集中的每个元素。

### 6.1. 列表迭代器

首先，为了能够从0迭代到2 n，我们应该有一个特殊的 Iterator在这个范围内循环但不预先消耗整个范围。

为了解决这个问题，我们将使用两个变量；一个用于大小，即2 n，另一个用于当前子集索引。我们的hasNext()函数将检查position是否小于size ：

```java
abstract class ListIterator<K> implements Iterator<K> {
    protected int position = 0;
    private int size;
    public ListIterator(int size) {
        this.size = size;
    }
    @Override
    public boolean hasNext() {
        return position < size;
    }
}
```

而我们的next()函数返回当前位置的子集并将位置的值增加 一：

```java
@Override
public Set<E> next() {
    return new Subset<>(map, reverseMap, position++);
}
```

### 6.2. 子集

为了延迟加载Subset，我们定义了一个扩展AbstractSet的类，并覆盖了它的一些功能。

通过遍历Subset的接收掩码(或位置) 中所有为1的位，我们可以在AbstractSet中实现Iterator和其他方法。

例如，size()是接收掩码中1的数量：

```java
@Override
public int size() { 
    return Integer.bitCount(mask);
}
```

而contains() 函数只是掩码中的相应位是否为1：

```java
@Override
public boolean contains(@Nullable Object o) {
    Integer index = map.get(o);
    return index != null && (mask & (1 << index)) != 0;
}
```

我们使用另一个变量 - remainingSetBits - 每当我们在子集中检索它的相应元素时修改它，我们将该位更改为0。然后，hasNext()检查remainingSetBits是否不为零(也就是说，它至少有一个值为1的位)：

```java
@Override
public boolean hasNext() {
    return remainingSetBits != 0;
}
```

而 next()函数使用remainingSetBits中最右边的1，然后将其转换为0，并返回相应的元素：

```java
@Override
public E next() {
    int index = Integer.numberOfTrailingZeros(remainingSetBits);
    if (index == 32) {
        throw new NoSuchElementException();
    }
    remainingSetBits &= ~(1 << index);
    return reverseMap.get(index);
}
```

### 6.3. 幂集

要拥有延迟加载的PowerSet类，我们需要一个扩展AbstractSet<Set<T>> 的类。

size()函数只是集合大小的 2 次方：

```java
@Override
public int size() {
    return (1 << this.set.size());
}
```

由于幂集将包含输入集的所有可能子集，因此contains(Object o)函数检查对象 o的所有元素是否存在于reverseMap(或输入集中)中：

```java
@Override
public boolean contains(@Nullable Object obj) {
    if (obj instanceof Set) {
        Set<?> set = (Set<?>) obj;
        return reverseMap.containsAll(set);
    }
    return false;
}
```

要检查给定Object 与此类的相等性，我们只能检查输入集是否等于给定Object：

```java
@Override
public boolean equals(@Nullable Object obj) {
    if (obj instanceof PowerSet) {
        PowerSet<?> that = (PowerSet<?>) obj;
        return set.equals(that.set);
    }
    return super.equals(obj);
}
```

iterator()函数返回我们已经定义的ListIterator实例：

```java
@Override
public Iterator<Set<E>> iterator() {
    return new ListIterator<Set<E>>(this.size()) {
        @Override
        public Set<E> next() {
            return new Subset<>(map, reverseMap, position++);
        }
    };
}
```

Guava 库使用了这种延迟加载的思想，这些PowerSet和Subset是 Guava 库的等效实现。

有关更多信息，请查看其[源代码](https://github.com/google/guava/blob/master/guava/src/com/google/common/collect/Sets.java)和[文档](https://guava.dev/releases/22.0/api/docs/com/google/common/collect/Sets.html)。

此外，如果我们想对PowerSet中的子集进行并行操作，我们可以为ThreadPool中的不同值调用Subset。

## 七、总结

综上所述，首先，我们研究了什么是幂集。然后，我们使用 Guava 库生成它。之后，我们研究了这个方法以及我们应该如何实现它，以及如何为它编写单元测试。

最后，我们利用Iterator 接口来优化子集的生成空间及其内部元素。