## 1. 概述

简单地说，Set是一个不包含重复元素的集合。在Java中，Set是扩展[Collection](https://www.baeldung.com/java-collections)接口的接口。

在本快速教程中，我们将介绍在Java中集合的不同方法。

## 2.构造函数

Set的一种方法是 使用Set实现的[构造函数](https://www.baeldung.com/java-constructors)：

```java
Set<T> copy = new HashSet<>(original);
```

[构造函数是一种特殊类型的构造函数，用于通过现有对象](https://www.baeldung.com/java-deep-copy)来创建新对象。

在这里，我们并没有真正克隆给定集合的元素。我们只是将对象引用到新集合中。因此，对一个元素所做的每个更改都会影响两个集合。

## 3.设置.addAll

Set接口有 [一个](https://www.baeldung.com/java-set-operations) addAll方法。 它将集合中的元素添加到目标集中。因此，我们可以使用 addAll方法将现有集合的元素到空集合中：

```java
Set<T> copy = new HashSet<>();
copy.addAll(original);
```

## 4.设置.克隆

让我们记住Set是一个扩展Collection接口的接口，因此我们需要引用一个实现Set接口的对象来创建 Set 的另一个实例。 HashSet、TreeSet、LinkedHashSet和EnumSet 都是Java中Set实现的例子。

所有这些Set实现都有一个 clone 方法，因为它们都实现了[Cloneable](https://www.baeldung.com/java-deep-copy)接口。

因此，作为集合的另一种方法，我们可以调用集合的克隆方法：

```java
Set<T> copy = (Set<T>) original.clone();
```

我们还要注意，克隆最初来自Object.clone。Set 实现覆盖了Object类的克隆方法。克隆的性质取决于实际的实现。例如，HashSet只进行浅拷贝，尽管我们可以编写代码来[进行深拷贝](https://www.baeldung.com/java-deep-copy)。

如我们所见，我们被迫将克隆的对象类型转换为Set<T> ， 因为克隆方法实际上返回一个Object。

## 5. JSON

集合的另一种方法是将其序列化为JSON String并从生成的JSON String创建一个新集合。 还值得注意的是， 对于这种方法，集合中的所有元素和引用的元素都必须是可序列化的，并且我们将执行所有对象的深层。

在此示例中，我们将使用Google 的Gson库的[序列化](https://www.baeldung.com/gson-serialization-guide)和[反序列化方法集合：](https://www.baeldung.com/gson-deserialization-guide)

```java
Gson gson = new Gson();
String jsonStr = gson.toJson(original);
Set<T> copy = gson.fromJson(jsonStr, Set.class);
```

## 6.Apache Commons 语言

[Apache Commons Lang](https://www.baeldung.com/java-commons-lang-3)有一个类SerializationUtils，它提供了一种特殊的方法——克隆——可以用来克隆给定的对象。我们可以利用这个方法来一个集合：

```java
for (T item : original) {
    copy.add(SerializationUtils.clone(item));
}
```

请注意， SerializationUtils.clone期望其参数扩展 Serializable类。

## 7.收集器.toSet

或者，我们可以使用[Java 8 的Stream API](https://www.baeldung.com/java-8-streams)和[Collectors](https://www.baeldung.com/java-8-collectors)来克隆一个集合：

```java
Set<T> copy = original.stream()
    .collect(Collectors.toSet());
```

Stream API的一个优点是它允许我们使用[skips](https://www.baeldung.com/java-8-streams)、[filters](https://www.baeldung.com/java-stream-filter-lambda)等，从而提供更多便利。

## 8. 使用Java10

Java 10 为Set 接口带来了一个新特性，允许我们从给定集合的元素创建一个不可变集合：

```java
Set<T> copy = Set.copyOf(original);
```

请注意，Set.copyOf 需要一个非null参数。

## 9.总结

在本文中，我们探讨了在Java中集合的不同方法。