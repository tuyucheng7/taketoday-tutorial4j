## 1. 概述

在本教程中，我们将说明如何使用 Guava 过滤和转换集合。

我们将使用[Predicates](https://www.baeldung.com/cs/predicates)进行过滤，使用库提供的函数进行转换，最后，我们将了解如何结合过滤和转换。

## 延伸阅读：

## [Guava 21 中的新流、比较器和收集器](https://www.baeldung.com/guava-21-new)

Guava 21 中 common.collect 包中工具的快速实用指南。

[阅读更多](https://www.baeldung.com/guava-21-new)→

## [Guava Multimap 指南](https://www.baeldung.com/guava-multimap)

Guava Multimap 与标准 java.util.Map 比较的简短指南

[阅读更多](https://www.baeldung.com/guava-multimap)→

## [Guava RangeSet 指南](https://www.baeldung.com/guava-rangeset)

通过实际示例了解如何使用 Google Guava RangeSet 及其实现。

[阅读更多](https://www.baeldung.com/guava-rangeset)→

## 2.过滤集合

让我们从过滤集合的简单示例开始。我们将使用由库提供并通过Predicates实用程序类构建的开箱即用的Predicate：

```java
@Test
public void whenFilterWithIterables_thenFiltered() {
    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    Iterable<String> result 
      = Iterables.filter(names, Predicates.containsPattern("a"));

    assertThat(result, containsInAnyOrder("Jane", "Adam"));
}
```

如你所见，我们正在过滤名称列表以仅获取包含字符“a”的名称——我们正在使用Iterables.filter()来执行此操作。

或者，我们也可以充分利用Collections2.filter() API：

```java
@Test
public void whenFilterWithCollections2_thenFiltered() {
    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    Collection<String> result 
      = Collections2.filter(names, Predicates.containsPattern("a"));
    
    assertEquals(2, result.size());
    assertThat(result, containsInAnyOrder("Jane", "Adam"));

    result.add("anna");
    assertEquals(5, names.size());
}
```

这里需要注意一些事情——首先，Collections.filter()的输出是原始集合的实时视图——对一个集合的更改将反映在另一个集合中。

同样重要的是要理解，现在结果受谓词约束——如果我们添加一个不满足谓词的元素，将抛出IllegalArgumentException ：

```java
@Test(expected = IllegalArgumentException.class)
public void givenFilteredCollection_whenAddingInvalidElement_thenException() {
    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    Collection<String> result 
      = Collections2.filter(names, Predicates.containsPattern("a"));

    result.add("elvis");
}
```

## 3. 编写自定义过滤谓词

接下来——让我们编写自己的Predicate，而不是使用库提供的 Predicate。在下面的示例中——我们将定义一个谓词，它只获取以“A”或“J”开头的名称：

```java
@Test
public void whenFilterCollectionWithCustomPredicate_thenFiltered() {
    Predicate<String> predicate = new Predicate<String>() {
        @Override
        public boolean apply(String input) {
            return input.startsWith("A") || input.startsWith("J");
        }
    };

    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    Collection<String> result = Collections2.filter(names, predicate);

    assertEquals(3, result.size());
    assertThat(result, containsInAnyOrder("John", "Jane", "Adam"));
}
```

## 4.组合多个谓词

我们可以使用Predicates.or()和Predicates.and()组合多个谓词。
在下面的示例中——我们过滤名称列表以获取以“J”开头或不包含“a”的名称：

```java
@Test
public void whenFilterUsingMultiplePredicates_thenFiltered() {
    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    Collection<String> result = Collections2.filter(names, 
      Predicates.or(Predicates.containsPattern("J"), 
      Predicates.not(Predicates.containsPattern("a"))));

    assertEquals(3, result.size());
    assertThat(result, containsInAnyOrder("John", "Jane", "Tom"));
}
```

## 5.过滤集合时删除空值

我们可以通过使用Predicates.notNull()过滤集合来清除集合中的空值，如下例所示：

```java
@Test
public void whenRemoveNullFromCollection_thenRemoved() {
    List<String> names = 
      Lists.newArrayList("John", null, "Jane", null, "Adam", "Tom");
    Collection<String> result = 
      Collections2.filter(names, Predicates.notNull());

    assertEquals(4, result.size());
    assertThat(result, containsInAnyOrder("John", "Jane", "Adam", "Tom"));
}
```

## 6. 检查集合中的所有元素是否都匹配条件

接下来，让我们检查 Collection 中的所有元素是否都符合特定条件。我们将使用Iterables.all()检查是否所有名称都包含“n”或“m”，然后我们将检查是否所有元素都包含“a”：

```java
@Test
public void whenCheckingIfAllElementsMatchACondition_thenCorrect() {
    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");

    boolean result = Iterables.all(names, Predicates.containsPattern("n|m"));
    assertTrue(result);

    result = Iterables.all(names, Predicates.containsPattern("a"));
    assertFalse(result);
}
```

## 7. 转换集合

现在 - 让我们看看如何使用 Guava Function转换集合。在下面的示例中——我们使用Iterables.transform()将名称列表转换为整数列表(名称的长度) ：

```java
@Test
public void whenTransformWithIterables_thenTransformed() {
    Function<String, Integer> function = new Function<String, Integer>() {
        @Override
        public Integer apply(String input) {
            return input.length();
        }
    };

    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    Iterable<Integer> result = Iterables.transform(names, function);

    assertThat(result, contains(4, 4, 4, 3));
}
```

我们还可以使用Collections2.transform() API，如下例所示：

```java
@Test
public void whenTransformWithCollections2_thenTransformed() {
    Function<String,Integer> func = new Function<String,Integer>(){
        @Override
        public Integer apply(String input) {
            return input.length();
        }
    };

    List<String> names = 
      Lists.newArrayList("John", "Jane", "Adam", "Tom");
    Collection<Integer> result = Collections2.transform(names, func);

    assertEquals(4, result.size());
    assertThat(result, contains(4, 4, 4, 3));

    result.remove(3);
    assertEquals(3, names.size());
}
```

请注意，Collections.transform()的输出是原始Collection 的实时视图——改变一个会影响另一个。

并且 - 和以前一样 - 如果我们尝试向输出Collection添加一个元素，则会抛出UnsupportedOperationException 。

## 8.从谓词创建函数

我们还可以使用Functions.fromPredicate()从Predicate创建Function。当然，这将是一个根据谓词的条件将输入转换为Boolean的函数。

在下面的示例中，我们将名称列表转换为布尔值列表，其中每个元素表示名称是否包含“m”：

```java
@Test
public void whenCreatingAFunctionFromAPredicate_thenCorrect() {
    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    Collection<Boolean> result =
      Collections2.transform(names,
      Functions.forPredicate(Predicates.containsPattern("m")));

    assertEquals(4, result.size());
    assertThat(result, contains(false, false, true, true));
}
```

## 9. 两个函数的组合

接下来 – 让我们看看如何使用组合函数转换 Collection 。

Functions.compose()返回两个函数的组合，因为它将第二个函数应用于第一个函数的输出。

在下面的示例中——第一个函数将名称转换为其长度，然后第二个函数将长度转换为一个布尔值，该值表示名称的长度是否为偶数：

```java
@Test
public void whenTransformingUsingComposedFunction_thenTransformed() {
    Function<String,Integer> f1 = new Function<String,Integer>(){
        @Override
        public Integer apply(String input) {
            return input.length();
        }
    };

    Function<Integer,Boolean> f2 = new Function<Integer,Boolean>(){
        @Override
        public Boolean apply(Integer input) {
            return input % 2 == 0;
        }
    };

    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    Collection<Boolean> result = 
      Collections2.transform(names, Functions.compose(f2, f1));

    assertEquals(4, result.size());
    assertThat(result, contains(true, true, true, false));
}
```

## 10.结合过滤和转换

现在——让我们看看 Guava 拥有的另一个很酷的 API——一个实际上允许我们将过滤和转换链接在一起的 API—— FluentIterable。

在下面的示例中——我们过滤名称列表，然后使用FluentIterable对其进行转换：

```java
@Test
public void whenFilteringAndTransformingCollection_thenCorrect() {
    Predicate<String> predicate = new Predicate<String>() {
        @Override
        public boolean apply(String input) {
            return input.startsWith("A") || input.startsWith("T");
        }
    };

    Function<String, Integer> func = new Function<String,Integer>(){
        @Override
        public Integer apply(String input) {
            return input.length();
        }
    };

    List<String> names = Lists.newArrayList("John", "Jane", "Adam", "Tom");
    Collection<Integer> result = FluentIterable.from(names)
                                               .filter(predicate)
                                               .transform(func)
                                               .toList();

    assertEquals(2, result.size());
    assertThat(result, containsInAnyOrder(4, 3));
}
```

值得一提的是，在某些情况下，命令式版本更具可读性，应该优先于函数式方法。

## 11.总结

最后，我们学习了如何使用 Guava 过滤和转换集合。我们使用Collections2.filter()和Iterables.filter() API 进行过滤，并使用Collections2.transform()和Iterables.transform()转换集合。

最后，我们快速浏览了非常有趣的FluentIterable fluent API 以结合过滤和转换。