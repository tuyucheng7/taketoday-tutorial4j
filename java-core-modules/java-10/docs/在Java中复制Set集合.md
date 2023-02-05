## 1. 概述

简单地说，Set是一个不包含重复元素的集合。在Java中，Set是一个扩展[Collection](https://www.baeldung.com/java-collections)接口的接口。

在这个快速教程中，我们将介绍在Java中复制Set的不同方法。

## 2. 复制构造函数

复制Set的一种方法是使用Set实现的复制构造函数：

```java
Set<T> copy = new HashSet<>(original);
```

**复制构造函数是一种特殊类型的构造函数，用于通过**[复制现有对象](https://www.baeldung.com/java-deep-copy)**来创建新对象**。

在这里，我们并没有真正克隆给定Set的元素，而只是将对象引用复制到新集合中。因此，对一个元素所做的每个更改都将影响两个集合。

## 3. Set.addAll

Set接口有一个[addAll](https://www.baeldung.com/java-set-operations)方法，它将集合中的元素添加到目标集合。因此，我们可以使用addAll方法将现有集合的元素复制到一个空集合中：

```java
Set<T> copy = new HashSet<>();
copy.addAll(original);
```

## 4. Set.clone

请记住，Set是一个扩展Collection接口的接口，因此**我们需要引用一个实现Set接口的对象来创建另一个Set实例**。HashSet、TreeSet、LinkedHashSet和EnumSet都是Java中Set的现有实现。

**所有这些Set实现都有一个clone方法，因为它们都实现了**[Cloneable](https://www.baeldung.com/java-deep-copy)**接口**。

因此，作为复制集合的另一种方法，我们可以调用集合的克隆方法：

```java
Set<T> copy = (Set<T>) original.clone();
```

**我们还要注意，克隆最初来自Object.clone，Set实现会覆盖Object类的clone方法**。克隆的性质取决于实际的实现。例如，HashSet只做一个浅拷贝，尽管我们可以编写代码来做[深拷贝](https://www.baeldung.com/java-deep-copy)。

正如我们所看到的，由于**克隆方法实际上返回了一个Object**，我们需要被迫将克隆对象类型转换为Set<T\>。

## 5. JSON

复制集合的另一种方法是将其序列化为JSON字符串并从生成的JSON字符串创建一个新集合。还值得注意的是，**对于这种方法，集合和引用元素中的所有元素都必须是可序列化的**，并且我们将**对所有对象执行深度复制**。

在本例中，我们将使用Google Gson库的[序列化](https://www.baeldung.com/gson-serialization-guide)和[反序列化](https://www.baeldung.com/gson-deserialization-guide)方法复制该集合：

```java
Gson gson = new Gson();
String jsonStr = gson.toJson(original);
Set<T> copy = gson.fromJson(jsonStr, Set.class);
```

## 6. Apache Commons Lang

Apache Commons Lang有一个类SerializationUtils，它提供了一个特殊的clone方法，可以用来克隆给定的对象。我们可以使用这个方法来复制集合：

```java
for (T item : original) {
    copy.add(SerializationUtils.clone(item));
}
```

请注意，**SerializationUtils.clone需要其参数扩展Serializable类**。

## 7. Collectors.toSet

或者，我们可以使用Java 8的[Stream API](https://www.baeldung.com/java-8-streams)和[Collectors](https://www.baeldung.com/java-8-collectors)来克隆一个集合：

```java
Set<T> copy = original
    .stream()
    .collect(Collectors.toSet());
```

Stream API的一个优点是它允许我们使用[skips](https://www.baeldung.com/java-8-streams)、[filters](https://www.baeldung.com/java-stream-filter-lambda)等，从而提供了更多的便利。

## 8. 使用Java 10

Java 10为Set接口引入了一个新特性，它允许我们**从给定集合的元素创建一个不可变集合**：

```java
Set<T> copy = Set.copyOf(original);
```

**请注意，Set.copyOf需要一个非null参数**。

## 9. 总结

在本文中，我们探讨了在Java中复制Set的不同方法。