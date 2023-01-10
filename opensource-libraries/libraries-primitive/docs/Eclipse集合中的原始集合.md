## 1. 简介

在本教程中，我们将讨论Java中的原始集合以及[Eclipse 集合](https://www.baeldung.com/eclipse-collections)如何提供帮助。

## 2.动机

假设我们要创建一个简单的整数列表：

```java
List<Integer> myList = new ArrayList<>; 
int one = 1; 
myList.add(one);
```

由于集合只能保存对象引用，因此在幕后，该对象 会在此过程中转换为 Integer。当然，装箱和拆箱不是免费的。因此，此过程中存在[性能](https://www.baeldung.com/java-list-primitive-performance)损失。

因此，首先，使用 Eclipse Collections 中的原始集合可以提高我们的速度。

其次，它减少了内存占用。下图比较了Eclipse Collections中传统ArrayList和IntArrayList的内存使用情况：

[![整数](https://www.baeldung.com/wp-content/uploads/2019/09/ints-300x207.png)](https://www.baeldung.com/wp-content/uploads/2019/09/ints.png)

图片摘自 https://www.eclipse.org/collections/#concept

当然，我们不要忘记，实现的多样性是 Eclipse Collections 的一大卖点。

另请注意，到目前为止，Java 不支持原始集合。然而，通过[JEP 218](https://openjdk.java.net/jeps/218)的Valhalla 项目 旨在添加它。

## 3.依赖关系

我们将使用[Maven](https://search.maven.org/search?q=g:org.eclipse.collections)来包含所需的依赖项：

```xml
<dependency>
    <groupId>org.eclipse.collections</groupId>
    <artifactId>eclipse-collections-api</artifactId>
    <version>10.0.0</version>
</dependency>

<dependency>
    <groupId>org.eclipse.collections</groupId>
    <artifactId>eclipse-collections</artifactId>
    <version>10.0.0</version>
</dependency>
```

## 4.长列表

[Eclipse Collections 具有针对所有原始](https://www.baeldung.com/java-primitives)类型 的内存优化列表、集合、堆栈、映射和包。让我们来看几个例子。

首先，让我们看一下long的列表：

```java
@Test
public void whenListOfLongHasOneTwoThree_thenSumIsSix() {
    MutableLongList longList = LongLists.mutable.of(1L, 2L, 3L);
    assertEquals(6, longList.sum());
}
```

## 5.整数列表

同样，我们可以创建一个不可变的int列表：

```java
@Test
public void whenListOfIntHasOneTwoThree_thenMaxIsThree() {
    ImmutableIntList intList = IntLists.immutable.of(1, 2, 3);
    assertEquals(3, intList.max());
}
```

## 6.地图

除了Map接口方法之外，Eclipse Collections 还为每个原始配对提供了新方法：

```java
@Test
public void testOperationsOnIntIntMap() {
    MutableIntIntMap map = new IntIntHashMap();
    assertEquals(5, map.addToValue(0, 5));
    assertEquals(5, map.get(0));
    assertEquals(3, map.getIfAbsentPut(1, 3));
}
```

## 7. 从可迭代到原始集合

此外，Eclipse Collections 与 Iterable一起工作：

```java
@Test
public void whenConvertFromIterableToPrimitive_thenValuesAreEqual() {
    Iterable<Integer> iterable = Interval.oneTo(3);
    MutableIntSet intSet = IntSets.mutable.withAll(iterable);
    IntInterval intInterval = IntInterval.oneTo(3);
    assertEquals(intInterval.toSet(), intSet);
}
```

此外，我们可以从Iterable创建一个原始映射：

```java
@Test
public void whenCreateMapFromStream_thenValuesMustMatch() {
    Iterable<Integer> integers = Interval.oneTo(3);
    MutableIntIntMap map = 
      IntIntMaps.mutable.from(
        integers,
        key -> key,
        value -> value  value);
    MutableIntIntMap expected = IntIntMaps.mutable.empty()
      .withKeyValue(1, 1)
      .withKeyValue(2, 4)
      .withKeyValue(3, 9);
    assertEquals(expected, map);
}
```

## 8.原语流

由于Java已经带有原始流，并且 Eclipse Collections 与它们很好地集成：

```java
@Test
public void whenCreateDoubleStream_thenAverageIsThree() {
    DoubleStream doubleStream = DoubleLists
      .mutable.with(1.0, 2.0, 3.0, 4.0, 5.0)
      .primitiveStream();
    assertEquals(3, doubleStream.average().getAsDouble(), 0.001);
}
```

## 9.总结

总之，本教程介绍了 Eclipse Collections 中的原始集合。我们展示了使用它的理由，并展示了我们如何轻松地将它添加到我们的应用程序中。