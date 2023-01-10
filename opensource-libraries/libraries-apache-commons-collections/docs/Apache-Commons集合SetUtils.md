## 1. 概述

在本文中，我们将探索 Apache Commons Collections 库的SetUtils API。简而言之，这些实用程序可用于在Java中对Set数据结构执行某些操作。

## 2.依赖安装

为了让我们在项目中使用SetUtils库，我们需要将以下依赖项添加到项目的pom.xml文件中：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.1</version>
</dependency>
```

或者，如果我们的项目是基于 Gradle 的，我们应该将依赖项添加到项目的build.gradle文件中。此外，我们需要将mavenCentral()添加到build.gradle文件的存储库部分：

```groovy
compile 'org.apache.commons:commons-collections4:4.1'
```

## 3.谓词集

SetUtils库的predicatedSet()方法允许定义要插入到集合中的所有元素应满足的条件。它接受一个源Set对象和一个谓词。

我们可以使用它来轻松验证Set的所有元素是否满足特定条件，这在开发第三方库/API 时非常方便。

如果任何元素的验证失败，将抛出IllegalArgumentException 。下面的代码片段防止将 不以“L”开头的字符串添加到sourceSet或返回的validatingSet中：

```java
Set<String> validatingSet
  = SetUtils.predicatedSet(sourceSet, s -> s.startsWith("L"));
```

该库还有predicatedSortedSet()和predicatedNavigableSet()分别用于处理SortedSet和NavigableSet。

## 4. 集合的并集、差集和交集

该库具有可以计算Set元素的并集、差集和交集的方法。

difference()方法采用两个Set对象并返回一个不可变的SetUtils。设置视图对象。返回的SetUtils。SetView包含集合a但不在集合b中的元素：

```java
Set<Integer> a = new HashSet<>(Arrays.asList(1, 2, 5));
Set<Integer> b = new HashSet<>(Arrays.asList(1, 2));
SetUtils.SetView<Integer> result = SetUtils.difference(a, b);
 
assertTrue(result.size() == 1 && result.contains(5));
```

请注意，尝试对返回的SetUtils 执行写操作，如add()或addAll() 。SetView将抛出UnsupportedOperationException。

要修改返回结果，我们需要调用返回的SetUtils的toSet()方法。SetView获取一个可写的Set对象：

```java
Set<Integer> mutableSet = result.toSet();
```

SetUtils库的union方法就像它听起来的那样——它返回集合a和b的所有元素。union方法还返回一个不可变的SetUtil.SetView对象：

```java
Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2, 5));
SetUtils.SetView<Integer> union = SetUtils.union(a, b);
 
assertTrue(SetUtils.isEqualSet(expected, union));
```

请注意assert 语句中使用的isEqualSet()方法。它是SetUtils库的一个方便的静态方法，可以有效地检查两个集合是否相等。

要获取集合的交集，即同时存在于集合a和集合b中的元素，我们将使用SetUtils。交集()方法。此方法还返回一个SetUtil.SetView对象：

```java
Set<Integer> expected = new HashSet<>(Arrays.asList(1, 2));
SetUtils.SetView<Integer> intersect = SetUtils.intersection(a, b);
 
assertTrue(SetUtils.isEqualSet(expected, intersect));
```

## 5. 转换集合元素

让我们来看看另一个令人兴奋的方法——SetUtils。变换集()。此方法接受一个Set对象和一个Transformer接口。在源集的支持下，它使用Transformer接口的transform()方法来转换集合中的每个元素。

转换逻辑在Transformer接口的transform()方法中定义，它应用于添加到集合中的每个元素。下面的代码片段将添加到集合中的每个元素乘以 2：

```java
Set<Integer> a = SetUtils.transformedSet(new HashSet<>(), e -> e  2  );
a.add(2);
 
assertEquals(a.toArray()[0], 4);
```

transformedSet()方法非常方便——它们甚至可以用于转换集合的元素——比如从字符串到整数。只需确保输出类型是输入的子类型即可。

假设我们正在使用SortedSet或NavigableSet而不是HashSet，我们可以分别使用transformedSortedSet()或transformedNavigableSet()。

请注意，一个新的HashSet实例被传递给transformedSet()方法。在将现有的非空Set传递给方法的情况下，不会转换预先存在的元素。

如果我们想要转换预先存在的元素(以及之后添加的元素)，我们需要使用org.apache.commons.collections4.set.TransformedSet的transformedSet()方法：

```java
Set<Integer> source = new HashSet<>(Arrays.asList(1));
Set<Integer> newSet = TransformedSet.transformedSet(source, e -> e  2);
 
assertEquals(newSet.toArray()[0], 2);
assertEquals(source.toArray()[0], 2);
```

请注意，源集中的元素被转换，结果被到返回的newSet。

## 6. 集合析取

SetUtils库提供了一个可用于查找集合析取的静态方法。集合a和集合b的析取是集合a和集合b唯一的所有元素。

让我们看看如何使用SetUtils库的disjunction()方法：

```java
Set<Integer> a = new HashSet<>(Arrays.asList(1, 2, 5));
Set<Integer> b = new HashSet<>(Arrays.asList(1, 2, 3));
SetUtils.SetView<Integer> result = SetUtils.disjunction(a, b);
 
assertTrue(
  result.toSet().contains(5) && result.toSet().contains(3));
```

## 7. SetUtils库中的其他方法

SetUtils库中还有其他方法可以轻松处理集合数据：

-   我们可以使用synchronizedSet()或synchronizedSortedSet()来获得线程安全的Set。但是，如文档中所述，我们必须手动同步返回集的迭代器以避免非确定性行为
-   我们可以使用SetUtils.unmodifiableSet()来获取只读集。请注意，尝试向返回的Set对象添加元素将抛出UnsupportedOperationException
-   还有SetUtils.emptySet()方法返回一个类型安全的、不可变的空集
-   SetUtils.emptyIfNull ()方法接受可为空的Set对象。如果提供的Set为空，它返回一个空的、只读的Set；否则，它返回提供的Set
-   SetUtils.orderedSet()将返回一个Set对象，该对象维护添加元素的顺序
-   SetUtils.hashCodeForSet()可以为一个集合生成哈希码——这样两组相同元素将具有相同的哈希码
-   SetUtils.newIdentityHashSet()将返回一个HashSet，它使用==来匹配元素而不是equals()方法。[请在此处](https://commons.apache.org/proper/commons-collections/apidocs/org/apache/commons/collections4/SetUtils.html#newIdentityHashSet--)阅读其注意事项

## 八. 总结

在本文中，我们探索了SetUtils库的本质。该实用程序类提供静态方法，使使用集合数据结构变得简单而令人兴奋。它还可以提高生产力。