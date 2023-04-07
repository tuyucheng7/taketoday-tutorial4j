## 一、概述

Java 8 引入的一项重要新功能是[Stream API](https://www.baeldung.com/java-8-streams)。此外，它附带一组*[收集器](https://www.baeldung.com/java-8-collectors)*，允许我们调用*Stream.collect()*方法将流中的元素收集到所需的集合中，例如*List*、*Set*、*Map*等。

在本教程中，我们将讨论*collect()*方法是否可以返回*空*值。

## 二、问题介绍

问题是“ *Stream*的*collect()*方法可以返回*null*吗？” 有两层意思：

-   当我们将此方法用于标准收集器时，我们必须执行*null检查吗？*
-   如果我们真的想要，是否可以让*collect()*方法返回*null ？*

我们在本教程中的讨论将涵盖这两种观点。

首先，让我们创建一个字符串列表作为输入数据，以便稍后进行演示：

```Java
final List<String> LANGUAGES = Arrays.asList("Kotlin", null, null, "Java", "Python", "Rust");复制
```

如我们所见，*LANGUAGES*列表包含六个字符串元素。值得一提的是，列表中有两个元素是*空*值。

稍后，我们将使用此列表作为输入来构建流。此外，为简单起见，我们将使用单元测试断言来验证*collect()*方法调用是否返回*空*值。

## 3.标准库附带的*收集器*不会返回*null*

我们知道 Java Stream API 引入了一套[标准的收集器](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html)。首先，让我们看一下标准收集器是否可以返回*null。*

### 3.1. *null*元素不会使*collect()*方法返回*null*

如果流包含 null 元素，它们将**作为*****null值包含在\******collect()\*****操作的结果中，而不是导致\*collect()\*方法返回\*null\*本身**。让我们写一个小测试来验证它：

```Java
List<String> result = LANGUAGES.stream()
  .filter(Objects::isNull)
  .collect(toList());
assertNotNull(result);
assertEquals(Arrays.asList(null, null), result);复制
```

如上面的测试所示，首先，我们使用[*filter()*](https://www.baeldung.com/java-stream-filter-lambda)方法只获取*null*元素。然后，我们将过滤后的*空*值收集在一个列表中。事实证明，结果列表中成功收集了两个*null元素。*因此，流中的***null\*元素不会导致\*collect()\*方法返回\*null\***。

### 3.2. 空流不会使*collect()*方法返回*null*

**当我们使用标准收集器时，即使流为空， Java Stream API 的\*collect()\*方法也不会返回\*null\***。

假设要收集的流是空的。在这种情况下，*collect()*方法将返回一个空的结果容器，例如一个空的*List*、一个空的*Map*或一个空的*array*，具体取决于 *collect()*方法中使用的收集器。

接下来我们拿三个常用的收集器来验证一下：

```Java
List<String> result = LANGUAGES.stream()
  .filter(s -> s != null && s.length() == 1)
  .collect(toList());
assertNotNull(result);
assertTrue(result.isEmpty());

Map<Character, String> result2 = LANGUAGES.stream()
  .filter(s -> s != null && s.length() == 1)
  .collect(toMap(s -> s.charAt(0), Function.identity()));
assertNotNull(result2);
assertTrue(result2.isEmpty());

Map<Character, List<String>> result3 = LANGUAGES.stream()
  .filter(s -> s != null && s.length() == 1)
  .collect(groupingBy(s -> s.charAt(0)));
assertNotNull(result3);
assertTrue(result3.isEmpty());复制
```

在上面的测试中，*filter(s -> s != null && s.length() == 1)*方法将返回一个空流，因为没有元素匹配条件。然后，正如我们所见，*[toList()](https://www.baeldung.com/java-stream-to-list-collecting)*、*[toMap()](https://www.baeldung.com/java-collectors-tomap)*和*[groupingBy()](https://www.baeldung.com/java-groupingby-collector)*收集器不会返回*null。*相反，它们会生成一个空列表或映射作为结果。

**因此，所有标准收集器都不会返回\*null。\***

## 4. 是否可以使*Stream.collect()*方法返回*null*？

我们了解到标准收集器不会使*collect()*方法返回*null。*那么现在，让我们转向这个问题，如果我们希望*Stream.collect()*方法返回*null*怎么办？是否可以？简短的回答是：是的。

那么接下来，让我们看看如何做到这一点。

### 4.1. 创建自定义收集器

标准收集器不返回*null。*因此，*Stream.collect()*方法也不会返回*null*。但是，**如果我们可以创建自己的收集器来返回可为 null 的结果，则\*Stream.collect()\*也可能返回\*null\*。**

Stream API 提供了静态*Collector.of()*方法，允许我们创建自定义收集器。Collector.of *()*方法有四个参数：

-   一个*[Supplier](https://www.baeldung.com/java-callable-vs-supplier#supplier)*函数——为收集操作返回一个可变的结果容器。
-   累加器函数——修改可变容器以合并当前元素。
-   组合器功能——当流被并行处理时，将中间结果合并到一个最终结果容器中。
-   一个可选的完成函数——获取可变结果容器并在返回收集操作的最终结果之前对其执行所需的最终转换。

我们应该注意，最后一个参数，即 finisher 函数，是可选的。这是因为许多收集器可以简单地使用可变容器作为最终结果。然而，**我们可以利用这个 finisher 函数来要求收集器返回一个可为 null 的容器**。

接下来，让我们创建一个名为*emptyListToNullCollector*的自定义收集器。顾名思义，我们希望收集器的工作方式与标准的*toList()*收集器几乎相同，只是当结果列表为空时，收集器将返回*null* 而不是空列表：

```Java
Collector<String, ArrayList<String>, ArrayList<String>> emptyListToNullCollector = Collector.of(ArrayList::new, ArrayList::add, (a, b) -> {
    a.addAll(b);
    return a;
}, a -> a.isEmpty() ? null : a);复制
```

现在，让我们用*LANGUAGES输入测试我们的**emptyListToNullCollector*收集器：

```Java
List<String> notNullResult = LANGUAGES.stream()
  .filter(Objects::isNull)
  .collect(emptyListToNullCollector);
assertNotNull(notNullResult);
assertEquals(Arrays.asList(null, null), notNullResult);

List<String> nullResult = LANGUAGES.stream()
  .filter(s -> s != null && s.length() == 1)
  .collect(emptyListToNullCollector);
assertNull(nullResult);复制
```

正如我们在上面的测试中看到的，当流不为空时，我们的*emptyListToNullCollector与标准的**toList()*收集器一样工作。但如果流为空，则返回*null*而不是空列表。

### 4.2. 使用*collectingAndThen()*方法

Java Stream API 提供了*collectingAndThen()*方法。**此方法允许我们****对收集器的结果应用整理功能**。它需要两个参数：

-   一个收集器对象——例如，标准的*toList()*
-   一个整理函数——获取收集操作的结果并在返回流操作的结果之前对其执行任何最终转换

例如，我们可以使用*collectingAndThen()*函数使返回的列表[不可修改](https://www.baeldung.com/java-immutable-list)：

```Java
List<String> notNullResult = LANGUAGES.stream()
  .filter(Objects::nonNull)
  .collect(collectingAndThen(toList(), Collections::unmodifiableList));
assertNotNull(notNullResult);
assertEquals(Arrays.asList("Kotlin", "Java", "Python", "Rust"), notNullResult);
                                                                                   
//the result list becomes immutable
assertThrows(UnsupportedOperationException.class, () -> notNullResult.add("Oops"));复制
```

我们刚刚学习了如何使用*Collector.of()*创建自定义收集器。这种方式可以让我们自由的实现采集逻辑。此外，我们知道如果 finisher 函数参数返回*null*，*Stream.collect(ourCustomCollector)*也可以返回*null 。*

如果我们只想通过添加一个 finisher 函数来扩展一个标准的收集器，我们也可以使用 collectingAndThen *()* 方法。它比创建自定义收集器更直接。

那么接下来，让我们使用*collectingAndThen()*方法来实现*emptyListToNullCollector*的功能：

```Java
List<String> nullResult = LANGUAGES.stream()
  .filter(s -> s != null && s.length() == 1)
  .collect(collectingAndThen(toList(), strings -> strings.isEmpty() ? null : strings));
assertNull(nullResult);复制
```

通过使用带有 finisher 函数的*collectingAndThen()*方法，我们可以看到*Stream.collect()*方法在流为空时返回*null 。*

### 4.3. *关于可空收集器*的一句话

我们已经看到了两种使收集器返回*null 的*方法，以便*Stream.collect()*也返回*null 。*但是，我们可能要三思而后行，是否必须让收集器可以为空。

通常，**避免使用可为空的收集器被认为是一种好的做法，除非有充分的理由这样做**。这是因为*null*值可能会引入意想不到的方式，并使其更难推断代码的行为。**如果使用可为空的收集器，重要的是要确保它的使用始终如一，并且任何下游处理都可以适当地处理\*空\*值**。

因此，所有标准收集器都不会返回*null。*

## 5.结论

在本文中，我们讨论了*Stream.collect()*是否可以返回*null。*

我们了解到所有标准收集器都不会返回*null。*此外，如果需要，我们可以使用*Collector.of()*或*collectingAndThen()使**Stream.collect()*返回*null*。但是，一般来说，除非万不得已，否则我们应该避免使用可为空的收集器。