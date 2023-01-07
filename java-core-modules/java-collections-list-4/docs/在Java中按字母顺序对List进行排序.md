## 1. 概述

在本教程中，我们将探讨在Java中按字母顺序对列表进行[排序](https://www.baeldung.com/java-sorting)的各种方法。

首先，我们将从[Collections](https://www.baeldung.com/java-collections)类开始，然后使用[Comparator](https://www.baeldung.com/java-comparator-comparable)接口。我们还将使用List 的API 按字母顺序排序，然后是[流](https://www.baeldung.com/java-streams)，最后使用[TreeSet。](https://www.baeldung.com/java-tree-set)

此外，我们将扩展我们的示例以探索几种不同的场景，包括根据[特定区域设置](https://www.baeldung.com/java-8-localization)对列表进行排序、对[重音列表](https://www.baeldung.com/java-remove-accents-from-text)进行排序以及使用[RuleBasedCollator](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/RuleBasedCollator.html) 来定义我们的自定义排序规则。

## 2.使用集合类排序

首先，让我们看看如何使用 Collections类对列表进行排序。

Collections类提供了一个静态的重载方法sort ，它可以获取列表并按自然顺序对其进行排序，或者我们可以使用Comparator提供自定义排序逻辑。

### 2.1. 按自然/词典顺序排序

首先，我们将定义输入列表：

```java
private static List<String> INPUT_NAMES = Arrays.asList("john", "mike", "usmon", "ken", "harry");
```

现在我们将首先使用 Collections类按自然顺序对列表进行排序，也称为[字典顺序](https://en.wikipedia.org/wiki/Lexicographic_order)：

```java
@Test
void givenListOfStrings_whenUsingCollections_thenListIsSorted() {
    Collections.sort(INPUT_NAMES);
    assertThat(INPUT_NAMES).isEqualTo(EXPECTED_NATURAL_ORDER);
}
```

其中 EXPECTED_NATURAL_ORDER是：

```java
private static List<String> EXPECTED_NATURAL_ORDER = Arrays.asList("harry", "john", "ken", "mike", "usmon");

```

这里需要注意的一些要点是：

-   该列表根据其元素的自然顺序按升序排序
-   我们可以将任何Collection传递给其元素是Comparable的sort方法(实现Comparable接口)
-   在这里，我们传递了一个String 类列表，它是一个 Comparable类，因此排序有效
-   Collection 类更改传递给sort的List对象的状态。因此我们不需要返回列表

### 2.2. 倒序排序

让我们看看我们如何以相反的字母顺序对同一个列表进行排序。

让我们再次使用sort方法，但现在提供一个Comparator：

```java
Comparator<String> reverseComparator = (first, second) -> second.compareTo(first);
```

或者，我们可以简单地使用Comparator 接口中的这个静态方法：

```java
Comparator<String> reverseComparator = Comparator.reverseOrder();
```

一旦我们有了反向比较器，我们就可以简单地将它传递给sort：

```java
@Test
void givenListOfStrings_whenUsingCollections_thenListIsSortedInReverse() {
    Comparator<String> reverseComparator = Comparator.reverseOrder();
    Collections.sort(INPUT_NAMES, reverseComparator); 
    assertThat(INPUT_NAMES).isEqualTo(EXPECTED_REVERSE_ORDER); 
}
```

其中 EXPECTED_REVERSE_ORDER 是：

```java
private static List<String> EXPECTED_REVERSE_ORDER = Arrays.asList("usmon", "mike", "ken", "john", "harry");

```

从中得出的一些相关总结是：

-   由于String类是final类，我们不能扩展重写Comparable接口的compareTo方法进行反向排序
-   我们可以使用Comparator接口来实现自定义的排序策略，即按字母降序排序
-   由于Comparator是一个函数式接口，我们可以使用 lambda 表达式

## 3. 使用比较器接口自定义排序

通常，我们必须对需要一些自定义排序逻辑的字符串列表进行排序。那是我们实现Comparator接口并提供我们想要的排序标准的时候。

### 3.1. 使用大写和小写字符串对列表进行排序的比较器

可以调用自定义排序的典型场景可以是字符串的混合列表，从大写和小写开始。

让我们设置并测试这个场景：

```java
@Test
void givenListOfStringsWithUpperAndLowerCaseMixed_whenCustomComparator_thenListIsSortedCorrectly() {
    List<String> movieNames = Arrays.asList("amazing SpiderMan", "Godzilla", "Sing", "Minions");
    List<String> naturalSortOrder = Arrays.asList("Godzilla", "Minions", "Sing", "amazing SpiderMan");
    List<String> comparatorSortOrder = Arrays.asList("amazing SpiderMan", "Godzilla", "Minions", "Sing");

    Collections.sort(movieNames);
    assertThat(movieNames).isEqualTo(naturalSortOrder);

    Collections.sort(movieNames, Comparator.comparing(s -> s.toLowerCase()));
    assertThat(movieNames).isEqualTo(comparatorSortOrder);
}
```

此处注意：

-   Comparator之前的排序结果将是不正确的：[“Godzilla, “Minions” “Sing”, “amazing SpiderMan”]
-   String::toLowerCase是键提取器，它从类型String中提取Comparable排序键并返回Comparator<String>
-   使用自定义比较器排序后的正确结果 将是：[“amazing SpiderMan”、“Godzilla”、“Minions”、“Sing”]

### 3.2. 用于对特殊字符进行排序的比较器

让我们考虑另一个列表示例，其中某些名称以特殊字符“@”开头。

我们希望它们排在列表的末尾，其余的应该按自然顺序排序：

```java
@Test
void givenListOfStringsIncludingSomeWithSpecialCharacter_whenCustomComparator_thenListIsSortedWithSpecialCharacterLast() {
    List<String> listWithSpecialCharacters = Arrays.asList("@laska", "blah", "jo", "@sk", "foo");

    List<String> sortedNaturalOrder = Arrays.asList("@laska", "@sk", "blah", "foo", "jo");
    List<String> sortedSpecialCharacterLast = Arrays.asList("blah", "foo", "jo", "@laska", "@sk");

    Collections.sort(listWithSpecialCharacters);
    assertThat(listWithSpecialCharacters).isEqualTo(sortedNaturalOrder);

    Comparator<String> specialSignComparator = Comparator.<String, Boolean>comparing(s -> s.startsWith("@"));
    Comparator<String> specialCharacterComparator = specialSignComparator.thenComparing(Comparator.naturalOrder());

    listWithSpecialCharacters.sort(specialCharacterComparator);
    assertThat(listWithSpecialCharacters).isEqualTo(sortedSpecialCharacterLast);
}
```

最后，一些关键点是：

-   没有比较器的排序输出是：[“@alaska”、“@ask”、“blah”、“foo”、“jo”]，这对我们的场景来说是不正确的
-   像以前一样，我们有一个键提取器，它从类型String中提取Comparable排序键并返回一个 specialCharacterLastComparator
-   我们可以使用[thenComparing](https://www.baeldung.com/java-8-comparator-comparing)链接specialCharacterLastComparator进行二次排序，期待另一个比较器
-   Comparator.naturalOrder以自然顺序比较Comparable对象。
-   排序中 比较器之后的最终输出是： [“blah”、“foo”、“jo”、“@alaska”、“@ask”]

## 4.使用流排序

现在，让我们使用[Java 8 Streams](https://www.baeldung.com/java-streams)对String列表进行排序。

### 4.1. 按自然顺序排序

我们先按自然顺序排序：

```java
@Test
void givenListOfStrings_whenSortWithStreams_thenListIsSortedInNaturalOrder() {
    List<String> sortedList = INPUT_NAMES.stream()
      .sorted()
      .collect(Collectors.toList());

    assertThat(sortedList).isEqualTo(EXPECTED_NATURAL_ORDER);
}
```

重要的是，这里的sorted方法返回一个按自然顺序排序的 String 流。

此外，此 Stream 的元素是 Comparable。

### 4.2. 倒序排序

接下来，让我们将一个Comparator传递给sorted，它定义了反向排序策略：

```java
@Test
void givenListOfStrings_whenSortWithStreamsUsingComparator_thenListIsSortedInReverseOrder() {
    List<String> sortedList = INPUT_NAMES.stream()
      .sorted((element1, element2) -> element2.compareTo(element1))
      .collect(Collectors.toList());
    assertThat(sortedList).isEqualTo(EXPECTED_REVERSE_ORDER);
}
```

注意，这里我们使用[Lamda](https://www.baeldung.com/java-8-sort-lambda)函数来定义 Comparator

或者，我们可以获得相同的Comparator：

```java
Comparator<String> reverseOrderComparator = Comparator.reverseOrder();
```

然后我们将简单地将这个reverseOrderComparator传递给sorted：

```java
List<String> sortedList = INPUT_NAMES.stream()
  .sorted(reverseOrder)
  .collect(Collectors.toList());
```

## 5. 使用TreeSet排序

TreeSet 使用Comparable接口按排序和升序存储对象。

我们可以简单地将未排序的列表转换为TreeSet  ，然后将其作为 列表收集回来：

```java
@Test
void givenNames_whenUsingTreeSet_thenListIsSorted() {
    SortedSet<String> sortedSet = new TreeSet<>(INPUT_NAMES);
    List<String> sortedList = new ArrayList<>(sortedSet);
    assertThat(sortedList).isEqualTo(EXPECTED_NATURAL_ORDER);
}
```

这种方法的一个限制是我们的原始列表不应该有任何它想在排序列表t 中保留的重复值。

## 6. 使用sort on List进行排序

我们还可以使用 List 的 sort方法对列表进行排序：

```scss
@Test
void givenListOfStrings_whenSortOnList_thenListIsSorted() {
    INPUT_NAMES.sort(Comparator.reverseOrder());
    assertThat(INPUT_NAMES).isEqualTo(EXPECTED_REVERSE_ORDER);
}

```

请注意，sort方法根据指定的 Comparator 规定的顺序对该列表进行排序。

## 7. Locale 敏感列表排序

根据语言环境，按字母顺序排序的结果可能会因语言规则而异。

让我们举一个字符串列表的例子：

```java
 List<String> accentedStrings = Arrays.asList("único", "árbol", "cosas", "fútbol");
```

让我们先对它们进行正常排序：

```java
 Collections.sort(accentedStrings);
```

输出将是： [ “things”, “football”, “tree”, “unique” ]。

但是，我们希望使用特定的语言规则对它们进行排序。

让我们创建一个具有特定语言环境的Collator实例：

```java
Collator esCollator = Collator.getInstance(new Locale("es"));

```

注意，这里我们使用es locale创建了一个 Collator 实例。

然后我们可以将此 Collator 作为Comparator传递，用于对list、Collection或使用Streams 进行排序：

```java
accentedStrings.sort((s1, s2) -> {
    return esCollator.compare(s1, s2);
});
```

排序的结果现在是： [ tree, things, soccer, unique ]。

最后，让我们一起展示一下：

```java
@Test
void givenListOfStringsWithAccent_whenSortWithTheCollator_thenListIsSorted() {
    List<String> accentedStrings = Arrays.asList("único", "árbol", "cosas", "fútbol");
    List<String> sortedNaturalOrder = Arrays.asList("cosas", "fútbol", "árbol", "único");
    List<String> sortedLocaleSensitive = Arrays.asList("árbol", "cosas", "fútbol", "único");

    Collections.sort(accentedStrings);
    assertThat(accentedStrings).isEqualTo(sortedNaturalOrder);

    Collator esCollator = Collator.getInstance(new Locale("es"));

    accentedStrings.sort((s1, s2) -> {
        return esCollator.compare(s1, s2);
    });

    assertThat(accentedStrings).isEqualTo(sortedLocaleSensitive);
}
```

## 8. 带重音字符的排序列表

带有重音符号或其他装饰的字符可以在 Unicode 中以几种不同的方式编码，从而以不同的方式排序。

我们可以在排序之前[规范化重音字符，也可以从字符中删除重音。](https://www.baeldung.com/java-remove-accents-from-text)

让我们研究一下这两种对重音列表进行排序的方法。

### 8.1. 标准化重音列表和排序

为了准确地对这样的字符串列表进行排序，让我们使用 java.text.Normalizer规范化重音字符

让我们从重音字符串列表开始：

```java
List<String> accentedStrings = Arrays.asList("único","árbol", "cosas", "fútbol");
```

接下来，让我们定义一个带有Normalize函数的Comparator并将其传递给我们的sort方法：

```java
Collections.sort(accentedStrings, (o1, o2) -> {
    o1 = Normalizer.normalize(o1, Normalizer.Form.NFD);
    o2 = Normalizer.normalize(o2, Normalizer.Form.NFD);
    return o1.compareTo(o2);
});
```

归一化后的排序列表将是： [ tree, things, soccer, unique ]

重要的是，我们在这里使用Normalizer.Form.NFD 对重音字符使用 Canonical 分解的形式对数据进行规范化。还有一些其他形式也可用于规范化。

### 8.2. 剥离口音和排序

让我们 在Comparator中使用StringUtils stripAccents方法并将其传递给sort方法：

```typescript
@Test
void givenListOfStrinsWithAccent_whenComparatorWithNormalizer_thenListIsNormalizedAndSorted() {
    List<String> accentedStrings = Arrays.asList("único", "árbol", "cosas", "fútbol");

    List<String> naturalOrderSorted = Arrays.asList("cosas", "fútbol", "árbol", "único");
    List<String> stripAccentSorted = Arrays.asList("árbol","cosas", "fútbol","único");

    Collections.sort(accentedStrings);
    assertThat(accentedStrings).isEqualTo(naturalOrderSorted);
    Collections.sort(accentedStrings, Comparator.comparing(input -> StringUtils.stripAccents(input)));
    assertThat(accentedStrings).isEqualTostripAccentSorted); 
}
```

## 9. 使用基于规则的整理器进行排序

我们还可以使用 java text.RuleBasedCollator 定义自定义规则以按字母顺序排序。

在这里，让我们定义自定义排序规则并将其传递给[RuleBasedCollator](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/RuleBasedCollator.html)：

```java
@Test
void givenListofStrings_whenUsingRuleBasedCollator_then ListIsSortedUsingRuleBasedCollator() throws ParseException {
    List<String> movieNames = Arrays.asList(
      "Godzilla","AmazingSpiderMan","Smurfs", "Minions");

    List<String> naturalOrderExpected = Arrays.asList(
      "AmazingSpiderMan", "Godzilla", "Minions", "Smurfs");
    Collections.sort(movieNames);

    List<String> rulesBasedExpected = Arrays.asList(
      "Smurfs", "Minions", "AmazingSpiderMan", "Godzilla");

    assertThat(movieNames).isEqualTo(naturalOrderExpected);

    String rule = "< s, S < m, M < a, A < g, G";

    RuleBasedCollator rulesCollator = new RuleBasedCollator(rule);
    movieNames.sort(rulesCollator);

    assertThat(movieNames).isEqualTo(rulesBasedExpected);
}

```

需要考虑的一些要点是：

-   RuleBasedCollator将字符映射到排序键
-   上面定义的规则是 <relations> 形式，但规则中也有其他[形式](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/RuleBasedCollator.html)
-   字符s 小于 m，M 小于 a，其中 A 小于 g 根据规则定义
-   如果规则的构建过程失败，将抛出格式异常
-   RuleBasedCollator实现了Comparator接口，因此可以传递给排序

## 10.总结

在本文中，我们研究了在Java中按字母顺序对列表进行排序的各种技术。

首先，我们使用了Collections，然后我们用一些常见的例子解释了Comparator接口。

在Comparator之后，我们研究了List的排序方法，然后是TreeSet。

我们还探讨了如何在不同的语言环境中处理字符串列表的排序、重音列表的规范化和排序，以及使用带有自定义规则的RuleBasedCollator。