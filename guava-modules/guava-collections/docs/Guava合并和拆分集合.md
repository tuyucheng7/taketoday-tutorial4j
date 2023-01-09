## 1. 概述

在本教程中，我们将学习如何使用Guava 库中的Joiner和Splitter。我们将使用Joiner将集合转换为 String，并使用Splitter将 String 拆分为集合。

## 2.使用Joiner将列表转换为字符串

让我们从一个简单的示例开始，使用Joiner将List连接到String中。在下面的示例中，我们使用逗号“,”作为分隔符将名称列表连接到一个字符串中：

```java
@Test
public void whenConvertListToString_thenConverted() {
    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    String result = Joiner.on(",").join(names);

    assertEquals(result, "John,Jane,Adam,Tom");
}
```

## 3.使用Joiner将Map转换为字符串

接下来 – 让我们看看如何使用Joiner将Map转换为String。在以下示例中，我们使用withKeyValueSeparator()将键与其值连接起来：

```java
@Test
public void whenConvertMapToString_thenConverted() {
    Map<String, Integer> salary = Maps.newHashMap();
    salary.put("John", 1000);
    salary.put("Jane", 1500);
    String result = Joiner.on(" , ").withKeyValueSeparator(" = ")
                                    .join(salary);

    assertThat(result, containsString("John = 1000"));
    assertThat(result, containsString("Jane = 1500"));
}
```

## 4.加入嵌套集合

现在 - 让我们看看如何将嵌套集合连接到String中。在下面的示例中，我们加入了将每个List转换为String的结果：

```java
@Test
public void whenJoinNestedCollections_thenJoined() {
    List<ArrayList<String>> nested = Lists.newArrayList(
      Lists.newArrayList("apple", "banana", "orange"),
      Lists.newArrayList("cat", "dog", "bird"),
      Lists.newArrayList("John", "Jane", "Adam"));
    String result = Joiner.on(";").join(Iterables.transform(nested,
      new Function<List<String>, String>() {
          @Override
          public String apply(List<String> input) {
              return Joiner.on("-").join(input);
          }
      }));

    assertThat(result, containsString("apple-banana-orange"));
    assertThat(result, containsString("cat-dog-bird"));
    assertThat(result, containsString("John-Jane-Adam"));
}
```

## 5. 使用Joiner处理空值

现在 - 让我们看看使用 Joiner 时处理 Null 值的不同方法。

要在加入集合时跳过空值，请使用skipNulls()，如以下示例所示：

```java
@Test
public void whenConvertListToStringAndSkipNull_thenConverted() {
    List<String> names = Lists.newArrayList("John", null, "Jane", "Adam", "Tom");
    String result = Joiner.on(",").skipNulls().join(names);

    assertEquals(result, "John,Jane,Adam,Tom");
}
```

如果你不想跳过空值并想替换它们，请使用useForNull()，如下例所示：

```java
@Test
public void whenUseForNull_thenUsed() {
    List<String> names = Lists.newArrayList("John", null, "Jane", "Adam", "Tom");
    String result = Joiner.on(",").useForNull("nameless").join(names);

    assertEquals(result, "John,nameless,Jane,Adam,Tom");
}
```

请注意，useForNull()不会更改原始列表，它只会影响连接的输出。

## 6.使用拆分器从字符串创建列表

现在 - 让我们看看如何将String拆分为List。在下面的示例中，我们使用“-”分隔符将输入String拆分为List：

```java
@Test
public void whenCreateListFromString_thenCreated() {
    String input = "apple - banana - orange";
    List<String> result = Splitter.on("-").trimResults()
                                          .splitToList(input);

    assertThat(result, contains("apple", "banana", "orange"));
}
```

请注意，trimResults()会从生成的子字符串中删除前导和尾随空格。

## 7.使用Splitter从字符串创建映射

接下来 – 让我们看看如何使用 Splitter 从字符串创建映射。在下面的示例中，我们使用withKeyValueSeparator()将String拆分为Map：

```java
@Test
public void whenCreateMapFromString_thenCreated() {
    String input = "John=first,Adam=second";
    Map<String, String> result = Splitter.on(",")
                                         .withKeyValueSeparator("=")
                                         .split(input);

    assertEquals("first", result.get("John"));
    assertEquals("second", result.get("Adam"));
}
```

## 8.使用多个分隔符拆分字符串

现在 - 让我们看看如何使用多个分隔符拆分字符串。在下面的示例中，我们同时使用“.” 和“，”来拆分我们的字符串：

```java
@Test
public void whenSplitStringOnMultipleSeparator_thenSplit() {
    String input = "apple.banana,,orange,,.";
    List<String> result = Splitter.onPattern("[.,]")
                                  .omitEmptyStrings()
                                  .splitToList(input);

    assertThat(result, contains("apple", "banana", "orange"));
}
```

请注意，omitEmptyStrings()会忽略空字符串，并且不会将它们添加到生成的List中。

## 9.以特定长度拆分字符串

接下来——让我们看一下如何以特定长度拆分字符串。在以下示例中，我们每 3 个字符拆分一次字符串：

```java
@Test
public void whenSplitStringOnSpecificLength_thenSplit() {
    String input = "Hello world";
    List<String> result = Splitter.fixedLength(3).splitToList(input);

    assertThat(result, contains("Hel", "lo ", "wor", "ld"));
}
```

## 10.限制拆分结果

最后 - 让我们看看如何限制拆分结果。如果你希望Splitter在特定数量的项目后停止拆分– 使用limit()，如下例所示：

```java
@Test
public void whenLimitSplitting_thenLimited() {
    String input = "a,b,c,d,e";
    List<String> result = Splitter.on(",")
                                  .limit(4)
                                  .splitToList(input);

    assertEquals(4, result.size());
    assertThat(result, contains("a", "b", "c", "d,e"));
}
```

## 11.总结

在本教程中，我们演示了如何在 Guava 中使用Joiner和Splitter在集合和字符串之间进行各种转换。