## 1. 概述

由于Vavr主要在Java生态系统中工作，因此始终需要将Vavr的数据结构转换为Java可理解的数据结构。

例如，考虑一个返回io.vavr.collection.List的函数，我们需要将结果传递给另一个接受java.util.List的函数。这就是Java-Vavr互操作性派上用场的地方。

在本教程中，我们将了解如何将多个Vavr数据结构转换为我们的标准Java集合，反之亦然。

## 2. Vavr到Java的转换

Vavr中的Value接口是大多数Vavr工具的基础接口。因此，所有Vavr的集合都继承了Value的属性。

这很有用，因为Value接口带有许多toJavaXXX()方法，允许我们将Vavr数据结构转换为Java等价物。

让我们看看如何从Vavr的列表或流中获取Java列表：

```java
List<String> vavrStringList = List.of("JAVA", "Javascript", "Scala");
java.util.List<String> javaStringList = vavrStringList.toJavaList();
Stream<String> vavrStream = Stream.of("JAVA", "Javascript", "Scala");
java.util.List<String> javaStringList = vavrStream.toJavaList();
```

第一个示例将Vavr列表转换为Java列表，下一个示例将流转换为Java列表。这两个示例都依赖于toJavaList()方法。

同样，我们可以从Vavr对象中获取其他Java集合。

让我们看另一个将VavrMap转换为Java Map的示例：

```java
Map<String, String> vavrMap = HashMap.of("1", "a", "2", "b", "3", "c");
java.util.Map<String, String> javaMap = vavrMap.toJavaMap();
```

除了标准的Java集合，Vavr还提供了将值转换为Java流和Optionals的API。

让我们看一个使用toJavaOptional()方法获取Optional的例子：

```java
List<String> vavrList = List.of("Java");
Optional<String> optional = vavrList.toJavaOptional();
assertEquals("Java", optional.get());
```

作为此类Vavr方法的概述，我们有：

-   toJavaArray()
-   toJavaCollection()
-   toJavaList()
-   toJavaMap()
-   toJavaSet()
-   toJavaOptional()
-   toJavaParallelStream()
-   toJavaStream()

可以在[此处](https://www.javadoc.io/doc/io.vavr/vavr/0.9.2)找到有用的API的完整列表。

## 3. Java到Vavr的转换

Vavr中的所有集合实现都有一个基本类型Traversable。因此，每个集合类型都有一个静态方法ofAll()，它接受一个Iterable并将其转换为相应的Vavr集合。

让我们看看如何将java.util.List转换为VavrList：

```java
java.util.List<String> javaList = Arrays.asList("Java", "Haskell", "Scala");
List<String> vavrList = List.ofAll(javaList);
```

同样，我们可以使用ofAll()方法将Java流转换为Vavr集合：

```java
java.util.stream.Stream<String> javaStream = Arrays.asList("Java", "Haskell", "Scala").stream();
Stream<String> vavrStream = Stream.ofAll(javaStream);
```

## 4. Java集合视图

Vavr库还提供Java集合视图，将调用委托给底层Vavr集合。

Vavr到Java的转换方法通过遍历所有元素来构建一个Java集合来创建一个新实例。这意味着转换的性能是线性的，而创建集合视图的性能是恒定的。

在撰写本文时，Vavr仅支持列表视图。

对于List，有两种方法可用于获取我们的视图。第一个是asJava()，它返回一个不可变列表，下一个是asJavaMutable()。

这是一个演示不可变JavaList的示例：

```java
@Test(expected = UnsupportedOperationException.class)
public void givenParams_whenVavrListConverted_thenException() {
    java.util.List<String> javaList = List.of("Java", "Haskell", "Scala").asJava();
    
    javaList.add("Python");
    assertEquals(4, javaList.size());
}
```

由于List是不可变的，因此对其进行任何修改都会抛出UnsupportedOperationException。

我们还可以 通过调用List上的asJavaMutable()方法来获取可变列表。

我们是这样做的：

```java
@Test
public void givenParams_whenVavrListConvertedToMutable_thenRetunMutableList() {
    java.util.List<String> javaList = List.of("Java", "Haskell", "Scala")
      	.asJavaMutable();
    javaList.add("Python");
 
    assertEquals(4, javaList.size());
}
```

## 5. Vavr对象之间的转换

类似于Java到Vavr之间的转换，反之亦然，我们可以将Vavr中的值类型转换为其他值类型。此转换功能有助于在需要时在Vavr对象之间进行转换。

例如，我们有一个项目列表，我们希望在保留顺序的同时过滤重复项。在这种情况下，我们需要一个LinkedHashSet。这是一个演示用例的示例：

```java
List<String> vavrList = List.of("Java", "Haskell", "Scala", "Java");
Set<String> linkedSet = vavrList.toLinkedSet();
assertEquals(3, linkedSet.size());
assertTrue(linkedSet instanceof LinkedHashSet);
```

Value接口中还有许多其他方法可以帮助我们根据用例将集合转换为不同的类型。

可以在[此处](https://static.javadoc.io/io.javaslang/javaslang/2.1.0-alpha/javaslang/Value.html)找到API的完整列表。

## 6. 总结

在本文中，我们了解了Vavr和Java集合类型之间的转换。要查看框架根据用例为转换提供的更多API，请参阅[JavaDoc](https://static.javadoc.io/io.vavr/vavr/0.9.0/io/vavr/collection/package-frame.html)和[用户指南](http://www.vavr.io/vavr-docs/)。