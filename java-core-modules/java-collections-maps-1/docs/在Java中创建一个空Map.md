##  一、概述

在本文中，我们将探讨在 Java中初始化空*Map的不同可能方法。*

我们将使用 Java 8 和 Java 9 来检查不同的方法。

## 2. 使用 Java 集合

我们可以使用Java Collections 模块提供的*emptyMap()*方法创建一个空的*Map 。***这将形成一个本质上可序列化****的空*****Map 。\*****该方法是在 Java 1.5 的 Collections Library 下引入的。**这将创建一个不可变的*Map*：

```java
Map<String, String> emptyMap = Collections.emptyMap();复制
```

注意：由于创建的*Map*本质上是不可变的，因此它不允许用户添加任何条目或对*Map*执行任何类型的修改。***这将在尝试添加或修改Map\*****中的任何键值对****时抛出\*java.lang.UnsupportedOperationException\*。**

我们还有两个方法支持空*Map*的创建和初始化。**emptySortedMap \*()\*返回一个不可变类型的空\*SortedMap\*。***Sorted* *Map*是一种在其键上提供进一步总排序的映射。通过此方法创建的*Map本质上是可序列化的：*

```java
SortedMap<String, String> sortedMap = Collections.emptySortedMap();复制
```

**Java Collections 提供的另一个方法是\*emptyNavigableMap()\*，它返回一个空的\*NavigableMap\*。***它具有与空排序Map*相同的属性。唯一的区别是此方法返回可导航的*Map*。*Navigable* *Map*是传统排序*Map*实现的扩展，它返回给定搜索目标的最接近匹配项。

```java
NavigableMap<String, String> navigableMap = Collections.emptyNavigableMap();复制
```

上述所有方法返回本质上不可变的*地图*，我们将无法向这些*地图*添加任何新条目。这会在强制尝试添加、删除或修改任何键值对时抛出*UnsupportedOperationException 。*

## 3.使用构造函数初始化地图

我们可以使用不同*Map实现的构造函数初始化**Maps* ，即*HashMap、LinkedHashMap、TreeMap*。所有这些初始化都会创建一个空*Map*，如果需要，我们可以稍后向其添加条目：

```java
Map hashMap = new HashMap();
Map linkedHashMap = new LinkedHashMap();
Map treeMap = new TreeMap();复制
```

**上面的\*映射\*是可变的，可以接受新条目，这是使用这种方法的优点之一。**在这种类型的初始化过程中创建的*映射*是空的。我们可以[在](https://drafts.baeldung.com/java-initialize-hashmap#the-static-initializer-for-a-static-hashmap)[*静态*](https://drafts.baeldung.com/java-initialize-hashmap#the-static-initializer-for-a-static-hashmap)[代码块中定义](https://drafts.baeldung.com/java-initialize-hashmap#the-static-initializer-for-a-static-hashmap)[空*映射*](https://drafts.baeldung.com/java-initialize-hashmap#the-static-initializer-for-a-static-hashmap)。

## 4. Java 9 使用*Map.of()的方法*

Java 9 带来了许多新特性，例如*接口私有方法、匿名类、平台模块系统*等等。**Map.of \*()\*是 Java 9 版本中引入的工厂方法。**此方法返回一个创建零映射的不可变映射*。*此方法提供的接口属于[Java 集合框架](https://www.baeldung.com/java-collections)。Map.of *(key1, value1, key2, value2, …..)*最多只能有 10 个键值对。

为了初始化一个空的*Map*，我们不会在此方法中传递任何键值对：

```java
Map<String, String> emptyMapUsingJava9 = Map.of();复制
```

这个工厂方法产生一个不可变的*Map*，因此我们将无法添加、删除或修改任何键值对。初始化后尝试在*Map中进行任何更改时抛出**UnsupportedOperationException 。*这。也不支持添加或删除键值对，将导致抛出上述异常。

注意：Java 9中的*Map.of()方法*[简化](https://www.baeldung.com/java-initialize-hashmap#the-java-9-way)了具有所需键值对的不可变*映射*的初始化。

## 5.使用番石榴

到目前为止，我们已经研究了使用核心 Java初始化空*Map的不同方法。*现在让我们继续检查如何使用 Guava 库初始化*Map ：*

```java
Map<String, String> articles = ImmutableMap.of();复制
```

**上述方法将使用 Guava 库创建一个不可变的空\*Map 。\***

在某些情况下，我们不需要不可变的*Map*。我们可以使用*Maps* 类初始化一个可变的*Map ：*

```java
Map<String, String> emptyMap = Maps.newHashMap();复制
```

**这种类型的初始化创建了一个可变的\*Map\*，即我们可以向这个\*Map\*添加条目。**但是这个*Map*的基本初始化是空的，不包含任何条目。

我们还可以使用特定的键和值类型来初始化*Map 。*这将创建一个具有预定义元素类型的*Map*并在未遵循时抛出异常：

```java
Map genericEmptyMap = Maps.<String, Integer>newHashMap();复制
```

简而言之，这将创建一个空*Map*，其键为字符串，值为整数。**用于初始化的一对尖括号被称为\*Diamond Syntax\*。**这将创建一个具有定义的类型参数的*Map*，它调用*Maps*类的构造函数。

我们还可以使用以下语法在番石榴中创建一个可变的*地图：*

```java
Map<String, String> emptyMapUsingGuava = Maps.newHashMap(ImmutableMap.of());复制
```

总之，上述方法在 Java 中创建了一个空的*Map 。*我们可以向这个*Map*添加条目，因为它本质上是可变的。

*ImmutableMap.of()*还重载了用于创建带有条目的*映射*的方法版本。由于我们正在创建一个空的*Map*，我们不需要在方法括号内传递任何参数来使用重载方法。

## 七、结论

在本文中，我们探讨了初始化*Empty* *Map*的不同方法。我们可以看到，自 Java 9 以来，这个领域有了巨大的改进。我们有新的工厂方法来创建和[初始化*Maps*](https://www.baeldung.com/java-initialize-hashmap)。