## 1. 概述

Java 9引入了很多人期待已久的语法糖，用于使用简洁的单行代码创建不可修改的小型Collection实例。根据[JEP 269](https://openjdk.java.net/jeps/269)，新的方便工厂方法包含在JDK 9中。

在本文中，我们将介绍它的用法以及实现细节。

## 2. 历史和动机

使用传统方式在Java中创建一个小型不可变集合非常冗长，以Set为例：

```java
Set<String> set = new HashSet<>();
set.add("foo");
set.add("bar");
set.add("baz");
set = Collections.unmodifiableSet(set);
```

对于一个简单的任务来说，代码实在太多了，这本应该在一个表达式中完成；在使用Map时也是如此。

但是，对于List，有一个工厂方法：

```java
List<String> list = Arrays.asList("foo", "bar", "baz");
```

尽管这种List的创建方式比构造函数初始化要好，但这一点并不明显，因为通常的直觉不会是通过Arrays类的方法来创建List：

还有其他减少冗长的方法，例如双大括号初始化方法：

```java
Set<String> set = Collections.unmodifiableSet(new HashSet<String>() {{
    add("foo"); add("bar"); add("baz");
}});
```

或使用Java 8 Streams：

```java
Stream.of("foo", "bar", "baz")
  .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));
```

双大括号方式只是稍微不那么冗长，但大大降低了可读性(并且被认为是一种反模式)。

然而，Java 8版本是一个单行表达式，它也有一些问题。首先，它并不明显和直观；其次，它仍然很冗长；第三，它涉及创建不必要的对象；第四，此方法不能用于创建Map。

总结这些缺点，上述方法都没有处理特定用例创建一个小的不可修改的Collection一流问题。

## 3. 说明与使用

Java 9为List、Set和Map接口提供了静态方法，它们将元素作为参数并分别返回List、Set和Map的实例。对于所有三个接口，此方法都命名为of(...) 。

### 3.1 List和Set

List和Set工厂方法的签名和特征是相同的：

```java
static <E> List<E> of(E e1, E e2, E e3)
static <E> Set<E>  of(E e1, E e2, E e3)
```

方法的使用：

```java
List<String> list = List.of("foo", "bar", "baz");
Set<String> set = Set.of("foo", "bar", "baz");
```

正如我们所看到的，它非常简单、简洁。在本例中，我们使用的方法恰好采用三个元素作为参数并返回大小为3的List/Set。

但是，这个方法有12个重载版本：其中11个分别接收有0到10个参数，另1个接收可变参数：

```java
static <E> List<E> of()
static <E> List<E> of(E e1)
static <E> List<E> of(E e1, E e2)
// ....and so on

static <E> List<E> of(E... elems)
```

对于大多数实际用途，10个元素就足够了，但如果需要更多，可以使用可变参数版本。

那么，我们的疑问是，既然有一个可变参数版本可以用于任意数量的元素，那么提供11个额外的方法有什么意义。

答案是性能；每个可变参数方法调用都会隐式创建一个数组，提供重载的方法可以避免不必要的对象创建及其垃圾收集开销。而Arrays.asList总是会创建这个隐式数组，因此当元素数量较少时效率较低。

在使用工厂方法创建Set的过程中，如果将重复元素作为参数传递，则会在运行时抛出IllegalArgumentException ：

```java
@Test
void onDuplicateElem_IfIllegalArgExp_thenSuccess() {
    assertThrows(IllegalArgumentException.class, () -> Set.of("foo", "bar", "baz", "foo"));
}
```

这里要注意的重要一点是，由于工厂方法使用泛型，原始类型会自动装箱。如果传递了原始类型的数组，则返回该原始类型数组的集合。

例如：

```java
int[] arr = { 1, 2, 3, 4, 5 };
List<int[]> list = List.of(arr);
```

在这种情况下，返回大小为1的List<int[]>，索引0处的元素包含该数组。

### 3.2 Map

Map中的工厂方法签名为：

```java
static <K,V> Map<K,V> of(K k1, V v1, K k2, V v2, K k3, V v3)
```

以及用法：

```java
Map<String, String> map = Map.of("foo", "a", "bar", "b", "baz", "c");
```

与List和Set类似，of(...)方法被重载为接收0到10个键值对。

在Map的情况下，对于超过10个键值对有一种不同的方法：

```java
static <K,V> Map<K,V> ofEntries(Map.Entry<? extends K,? extends V>... entries)
```

下面是它的用法：

```java
Map<String, String> map = Map.ofEntries(
		new AbstractMap.SimpleEntry<>("foo", "a"),
		new AbstractMap.SimpleEntry<>("bar", "b"),
		new AbstractMap.SimpleEntry<>("baz", "c"));
```

传递Key的重复值会抛出IllegalArgumentException：

```java
@Test
void givenDuplicateKeys_ifIllegalArgExp_thenSuccess() {
	assertThrows(IllegalArgumentException.class, () -> Map.of("foo", "a", "foo", "b"));
}
```

同样，在Map的情况下，原始类型也是自动装箱的。

## 4. 实现说明

使用工厂方法创建的集合不是我们通常使用的实现，例如，List不是ArrayList并且Map不是HashMap。这些是Java9中引入的不同实现，这些实现是内部的，并且它们的构造函数具有受限的访问权限。

在本节中，我们介绍所有三种类型的集合共有的一些重要的实现差异。

### 4.1 不可变

使用工厂方法创建的集合是不可变的，更改元素、添加新元素或删除元素会抛出UnsupportedOperationException：

```java
@Test
void onElemAdd_ifUnSupportedOpExpnThrown_thenSuccess() {
	Set<String> set = Set.of("foo", "bar");
	assertThrows(UnsupportedOperationException.class, () -> set.add("baz"));
}

@Test
void onElemAdd_ifUnSupportedOpExpnThrown_thenSuccess() {
	List<String> list = List.of("foo", "bar");
	assertThrows(UnsupportedOperationException.class, () -> list.add("baz"));
}

@Test
void onElemAdd_ifUnSupportedOpExpnThrown_thenSuccess() {
	Map<String, String> map = Map.of("foo", "a", "bar", "b");
	assertThrows(UnsupportedOperationException.class, () -> map.put("baz", "c"));
}
```

### 4.2 不允许空元素

对于List和Set，任何元素都不能为null；对于Map，key和value都不能为null。传递null参数会引发NullPointerException：

```java
@Test
void onNullElem_ifNullPtrExpnThrown_thenSuccess() {
	assertThrows(NullPointerException.class, () -> List.of("foo", "bar", null));
}
```

与List.of不同，Arrays.asList方法允许传递null值。

### 4.3 基于值的实例

工厂方法创建的实例是基于值的，这意味着工厂可以自由地创建新实例或返回现有实例。

因此，如果我们创建具有相同值的集合，它们可能会，也可能不会引用堆上的相同对象：

```java
List<String> list1 = List.of("foo", "bar");
List<String> list2 = List.of("foo", "bar");
```

在这种情况下，list1 == list2的结果可能会，也可能不会为true，具体取决于JVM。

### 4.4 序列化

如果集合的元素是可序列化的，则从工厂方法创建的集合是可序列化的。

## 5. 总结

在本文中，我们介绍了Java 9中引入的Collections的新工厂方法。

通过回顾过去创建不可修改集合的一些方法，我们总结了为什么此功能是一个受欢迎的增强。我们介绍了它的用法，并强调了使用它们时要考虑的关键点。最后，我们指出了这些集合与常用的实现不同，并阐明了关键差异。
