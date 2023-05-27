## 1. 概述

在本教程中，我们将讨论如何使用[Java Stream](https://www.baeldung.com/java-8-streams-introduction)来处理[Map](https://www.baeldung.com/java-hashmap)的一些示例。值得注意的是，其中一些练习可以使用双向Map数据结构来解决，但我们在这里对函数式方法感兴趣。

首先，我们将解释我们将用于处理Map和Stream的基本概念。然后我们将介绍几个与Map相关的不同问题及其使用Stream的具体解决方案。

## 2. 基本思路

要注意的主要事情是Stream是可以从Collection中轻松获取的元素序列。

Map具有不同的结构，从键到值的映射，没有顺序。但是，这并不意味着我们不能将Map结构转换为不同的序列，从而允许我们使用Stream API以自然的方式工作。

让我们看看从Map获取不同Collection的方法，然后我们可以将其转换为Stream：

```java
Map<String, Integer> someMap = new HashMap<>();
```

我们可以获得一组键值对：

```java
Set<Map.Entry<String, Integer>> entries = someMap.entrySet();
```

我们还可以获得与Map关联的键集：

```java
Set<String> keySet = someMap.keySet();
```

或者我们可以直接使用值集：

```java
Collection<Integer> values = someMap.values();
```

这些每个都为我们提供了一个入口点，通过从它们获取流来处理这些集合：

```java
Stream<Map.Entry<String, Integer>> entriesStream = entries.stream();
Stream<Integer> valuesStream = values.stream();
Stream<String> keysStream = keySet.stream();
```

## 3. 使用Stream获取Map的键

### 3.1 输入数据

假设我们有一个Map：

```java
Map<String, String> books = new HashMap<>();
books.put("978-0201633610", "Design patterns : elements of reusable object-oriented software");
books.put("978-1617291999", "Java 8 in Action: Lambdas, Streams, and functional-style programming");
books.put("978-0134685991", "Effective Java");
```

我们有兴趣为名为“Effective Java”的书查找ISBN。

### 3.2 检索匹配项

由于我们的Map中不存在书名，因此我们希望能够表明它没有关联的ISBN。我们可以使用[Optional](https://www.baeldung.com/java-optional)来表达：

对于这个例子，我们假设我们对与该书名匹配的书籍的任何键感兴趣：

```java
Optional<String> optionalIsbn = books.entrySet().stream()
    .filter(e -> "Effective Java".equals(e.getValue()))
    .map(Map.Entry::getKey)
    .findFirst();

assertEquals("978-0134685991", optionalIsbn.get());
```

让我们分析一下代码。首先，我们从Map中获取entrySet，如前所述。

我们只想考虑标题为“Effective Java”的条目，因此第一个中间操作将是一个[过滤器](https://www.baeldung.com/java-stream-filter-lambda)。

我们对整个Map条目不感兴趣，但对每个条目的键感兴趣。所以下一个链式中间操作就是这样做的：它是一个映射操作，它将生成一个新的流作为输出，它将只包含与我们正在寻找的标题匹配的条目的键。

因为我们只想要一个结果，所以我们可以应用findFirst()终端操作，它将Stream中的初始值作为Optional对象提供。

让我们看一个标题不存在的情况：

```java
Optional<String> optionalIsbn = books.entrySet().stream()
    .filter(e -> "Non Existent Title".equals(e.getValue()))
    .map(Map.Entry::getKey).findFirst();

assertEquals(false, optionalIsbn.isPresent());
```

### 3.3 检索多个结果

现在让我们改变问题，看看我们如何处理返回多个结果而不是一个结果。

要返回多个结果，让我们将以下书籍添加到我们的Map中：

```java
books.put("978-0321356680", "Effective Java: Second Edition");
```

所以现在如果我们查找所有以“Effective Java”开头的书籍，我们将得到不止一个结果：

```java
List<String> isbnCodes = books.entrySet().stream()
    .filter(e -> e.getValue().startsWith("Effective Java"))
    .map(Map.Entry::getKey)
    .collect(Collectors.toList());

assertTrue(isbnCodes.contains("978-0321356680"));
assertTrue(isbnCodes.contains("978-0134685991"));
```

我们在这种情况下所做的是替换过滤条件以验证Map中的值是否以“Effective Java”开头，而不是比较String是否相等。

这次我们收集结果，而不是只选择第一个，并将匹配项放入一个列表中。

## 4. 使用Stream获取Map的值 

现在让我们关注Map的另一个问题。我们将尝试根据ISBN获取书名，而不是根据书名获取ISBN。

让我们使用原始Map。我们想要查找ISBN以“978-0”开头的图书。

```java
List<String> titles = books.entrySet().stream()
    .filter(e -> e.getKey().startsWith("978-0"))
    .map(Map.Entry::getValue)
    .collect(Collectors.toList());

assertEquals(2, titles.size());
assertTrue(titles.contains("Design patterns : elements of reusable object-oriented software"));
assertTrue(titles.contains("Effective Java"));
```

这个解决方案类似于我们之前的一组问题的解决方案；我们流式传输条目集，然后过滤、映射和收集。

同样和以前一样，如果我们只想返回第一个匹配项，那么在map方法之后我们可以调用findFirst()方法，而不是将所有结果收集到一个List中。

## 5. 总结

在本文中，我们演示了如何以函数式方式处理Map。

特别是，我们已经看到，一旦我们切换到使用Map的关联集合，使用Stream的处理就会变得更加容易和直观。