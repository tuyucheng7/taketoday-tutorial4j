## 一、概述

在 Java 中工作时，通常有两个需要关联的单独列表。换句话说，我们有两个列表，一个包含键，另一个包含值。然后我们想要得到一个*Map*，它将键列表的每个元素与值列表中的相应元素相关联。

在本教程中，我们将探索如何以不同的方式实现这一目标。

## 二、问题介绍

像往常一样，让我们通过一个例子来理解这个问题。假设我们有两个列表：

```java
final List<String> KEY_LIST = Arrays.asList("Number One", "Number Two", "Number Three", "Number Four", "Number Five");
final List<Integer> VALUE_LIST = Arrays.asList(1, 2, 3, 4, 5);复制
```

现在，我们想将上面的两个列表与*Map*相关联。但首先，让我们[初始化一个](https://www.baeldung.com/java-initialize-hashmap)包含预期键值对的[*HashMap ：*](https://www.baeldung.com/java-initialize-hashmap)

```java
final Map<String, Integer> EXPECTED_MAP = new HashMap<String, Integer>() {{
    put("Number One", 1);
    put("Number Two", 2);
    put("Number Three", 3);
    put("Number Four", 4);
    put("Number Five", 5);
}};复制
```

如上面的代码所示，合并两个列表的规则非常简单。接下来，让我们看看如何实现这一目标。

## 3.关于验证的一句话

现在我们理解了这个问题，我们可能已经意识到给定的两个列表必须包含相同数量的元素，比如*KEY_LIST*和*VALUE_LIST*。然而，在实践中，由于我们无法预测我们所获得的数据质量，因此**两个给定的列表可能具有不同的大小**。如果是这种情况，我们必须按照要求进行进一步的操作。通常，可能有两种选择：

-   抛出异常并中断关联操作
-   将不匹配问题报告为警告并继续创建*Map*对象以仅包含匹配的元素

我们可以使用一个简单的 *if*检查来实现它：

```java
int size = KEY_LIST.size();
if (KEY_LIST.size() != VALUE_LIST.size()) {
    // throw an exception or print a warning and take the smaller size and continue:
    size = min(KEY_LIST.size(), VALUE_LIST.size());
}

// using the size variable for further processings复制
```

为简单起见，我们假设这两个列表始终具有相同的大小，并在进一步的代码示例中省略此验证。此外，我们将使用单元测试断言来验证该方法是否返回预期结果。

## 4.循环填充*地图*

**由于两个输入列表的大小相同，我们可以将这两个列表与一个循环相关联**。接下来，让我们看看它是如何完成的：

```java
Map<String, Integer> result = new HashMap<>();

for (int i = 0; i < KEY_LIST.size(); i++) {
    result.put(KEY_LIST.get(i), VALUE_LIST.get(i));
}
assertEquals(EXPECTED_MAP, result);复制
```

如上例所示，我们创建了一个名为*result的新**HashMap*。然后我们使用*for*循环遍历*KEY_LIST*中的每个元素，对于每个元素，我们使用相同的索引*i从**VALUE_LIST*中检索相应的元素。然后，*put()*方法将键值对填充到*结果*映射中。

## 5. 使用流 API

[Stream API](https://www.baeldung.com/java-8-streams)提供了许多简洁高效的方法来操作 Java 集合。那么接下来，让我们使用 Java Stream API 来关联两个列表：

```java
Map<String, Integer> result = IntStream.range(0, KEY_LIST.size())
  .boxed()
  .collect(Collectors.toMap(KEY_LIST::get, VALUE_LIST::get));
assertEquals(EXPECTED_MAP, result);复制
```

正如我们在上面的代码中看到的，[*IntStream.range()*](https://www.baeldung.com/java-listing-numbers-within-a-range#intstream)*方法生成一个从0*到 KEY_LIST 大小的整数流*。*值得一提的是***IntStream\*是一个[原始流](https://www.baeldung.com/java-8-primitive-streams#primitive-streams)**。因此，我们使用[*boxed()*](https://www.baeldung.com/java-8-primitive-streams#boxing-and-unboxing)*方法将IntStream*转换为*Stream<Integer>*，这允许我们使用[*collect()*](https://www.baeldung.com/java-8-collectors)方法将元素收集到*Map 中。*

## 6. 使用*迭代器*

我们已经学习了两种方法来关联两个列表并得到一个 *Map*作为结果。但是，如果我们仔细研究这两个解决方案，我们会发现这两种方法都使用了*List.get()*方法。换句话说，**我们在建立关联时调用\*List.get(i)\* 通过索引访问元素。**这称为随机访问。

如果我们的列表是[*ArrayList*](https://www.baeldung.com/java-arraylist)，这可能是最常见的情况，则数据由数组支持。因此，[随机访问速度很快](https://www.baeldung.com/java-arraylist-linkedlist#2-access-by-index)。

然而，如果我们有两个大的*[LinkedList](https://www.baeldung.com/java-linkedlist)，*[通过索引访问元素可能会很慢。](https://www.baeldung.com/java-arraylist-linkedlist#2-access-by-index-1)这是因为***LinkedList\*需要从列表的开头迭代到所需的索引**。

因此，使用[*Iterator*](https://www.baeldung.com/java-iterator)可能是一种更有效的遍历列表的方法，尤其是对于大型列表：

```java
Map<String, Integer> result = new HashMap<>();

Iterator<String> ik = KEY_LIST.iterator();
Iterator<Integer> iv = VALUE_LIST.iterator();
while (ik.hasNext() && iv.hasNext()) {
    result.put(ik.next(), iv.next());
}

assertEquals(EXPECTED_MAP, result);复制
```

在此示例中，我们创建了两个*Iterator*对象，每个对象对应一个列表。然后，我们使用[*while*循环](https://www.baeldung.com/java-while-loop)同时遍历两个列表，使用每个*Iterator的**next()*方法检索列表中的下一个元素。对于每一对元素，我们将键和值放入结果*HashMap*中，就像前面的示例一样。

## 七、结论

在本文中，我们通过示例学习了三种将两个给定列表组合成地图的方法。

*首先，我们用一个for*循环和基于列表随机访问的*Stream*解决了这个问题。然后，我们讨论了当输入为*LinkedList*时随机访问方法的性能问题。

最后，我们看到了基于*Iterator*的解决方案，这样无论我们有哪种*List*实现，我们都可以获得更好的性能。