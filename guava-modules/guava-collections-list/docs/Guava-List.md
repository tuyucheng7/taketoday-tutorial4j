## 1. 概述

在本教程中，我们将说明使用 Guava 库处理列表的最常见和最有用的方法。

让我们从简单开始——看看使用 Guava 语法创建一个新的ArrayList—— 没有new：

```java
List<String> names = Lists.newArrayList("John", "Adam", "Jane");
```

## 2.反转列表

首先，让我们使用Lists.reverse()反转List，如下例所示：

```java
@Test
public void whenReverseList_thenReversed() {
    List<String> names = Lists.newArrayList("John", "Adam", "Jane");

    List<String> reversed = Lists.reverse(names);
    assertThat(reversed, contains("Jane", "Adam", "John"));
}
```

## 3.从字符串生成字符 列表

现在 - 让我们看看如何将 String 分解为Characters列表。

在以下示例中——我们使用Lists.CharactersOf() API从字符串“John”创建字符 列表：

```java
@Test
public void whenCreateCharacterListFromString_thenCreated() {
    List<Character> chars = Lists.charactersOf("John");

    assertEquals(4, chars.size());
    assertThat(chars, contains('J', 'o', 'h', 'n'));
}
```

## 4. 对列表进行分区

接下来 – 让我们看看如何对List进行分区 。

在下面的示例中——我们使用Lists.partition()来获取大小为 2 的连续子列表：

```java
@Test
public void whenPartitionList_thenPartitioned(){
    List<String> names = Lists.newArrayList("John","Jane","Adam","Tom","Viki","Tyler");

    List<List<String>> result = Lists.partition(names, 2);

    assertEquals(3, result.size());
    assertThat(result.get(0), contains("John", "Jane"));
    assertThat(result.get(1), contains("Adam", "Tom"));
    assertThat(result.get(2), contains("Viki", "Tyler"));
}
```

## 5.从列表中删除重复项

现在 - 让我们使用一个简单的技巧从List中删除重复项。

在下面的示例中——我们将元素到一个Set中，然后我们从剩余的元素中创建一个List ：

```java
@Test
public void whenRemoveDuplicatesFromList_thenRemoved() {
    List<Character> chars = Lists.newArrayList('h','e','l','l','o');
    assertEquals(5, chars.size());

    List<Character> result = ImmutableSet.copyOf(chars).asList();
    assertThat(result, contains('h', 'e', 'l', 'o'));
}
```

## 6. 从列表中删除空值

接下来 – 让我们看看如何从List中删除空值。

在以下示例中——我们使用非常有用的Iterables.removeIf() API 和库本身提供的谓词删除所有空值：

```java
@Test
public void whenRemoveNullFromList_thenRemoved() {
    List<String> names = Lists.newArrayList("John", null, "Adam", null, "Jane");
    Iterables.removeIf(names, Predicates.isNull());

    assertEquals(3, names.size());
    assertThat(names, contains("John", "Adam", "Jane"));
}
```

## 7. 将列表转换为不可变列表

最后——让我们看看如何使用ImmutableList.copyOf() API创建一个List的不可变副本——一个ImmutableList ：

```java
@Test
public void whenCreateImmutableList_thenCreated() {
    List<String> names = Lists.newArrayList("John", "Adam", "Jane");

    names.add("Tom");
    assertEquals(4, names.size());

    ImmutableList<String> immutable = ImmutableList.copyOf(names);
    assertThat(immutable, contains("John", "Adam", "Jane", "Tom"));
}
```

## 八. 总结

我们在这里 - 一个快速教程，介绍了你可以使用 Guava 对列表进行的大部分有用的事情。

要进一步深入研究列表，请查看列表[的谓词和函数指南](https://www.baeldung.com/guava-filter-and-transform-a-collection)以及Guava[中加入和拆分列表](https://www.baeldung.com/guava-joiner-and-splitter-tutorial)的深入指南。