## 1. 概述

本教程演示如何使用JDK、Guava以及Apache Commons Collections4使ArrayList不可变。

## 2. 使用JDK

首先，JDK提供了一种从现有集合中获取不可修改集合的方法：

```java
Collections.unmodifiableList(list);
```

此时不应再修改新集合：

```java
@Test
final void givenUsingTheJdk_whenUnmodifiableListIsCreated_thenNotModifiable() {
	final List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
	final List<String> unmodifiableList = Collections.unmodifiableList(list);
    assertThrows(UnsupportedOperationException.class, () -> unmodifiableList.add("four"));
}
```

### 2.1 使用Java 9

从Java 9开始，我们可以使用List<E>.of(E... elements)静态工厂方法来创建不可变集合：

```java
@Test
final void givenUsingTheJava9_whenUnmodifiableListIsCreated_thenNotModifiable() {
	final List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
	final List<String> unmodifiableList = List.of(list.toArray(new String[]{}));
	assertThrows(UnsupportedOperationException.class, () -> unmodifiableList.add("four"));
}
```

请注意我们必须将现有集合转换为数组，这是因为List.of(elements)接收可变参数。

## 3. Guava

Guava提供了类似的功能来创建自己的ImmutableList版本：

```java
ImmutableList.copyOf(list);
```

同样，创建的集合应该是不可修改的：

```java
@Test
final void givenUsingGuava_whenUnmodifiableListIsCreated_thenNotModifiable() {
	final List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
	final List<String> unmodifiableList = ImmutableList.copyOf(list);
	assertThrows(UnsupportedOperationException.class, () -> unmodifiableList.add("four"));
}
```

请注意，此操作实际上会创建原始集合的副本，而不仅仅是视图。

Guava还提供了一个构建器这将返回强类型的ImmutableList而不是简单的List：

```java
@Test
final void givenUsingGuavaBuilder_whenUnmodifiableListIsCreated_thenNoLongerModifiable() {
	final List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
	final ImmutableList<String> unmodifiableList = ImmutableList.<String>builder().addAll(list).build();
	assertThrows(UnsupportedOperationException.class, () -> unmodifiableList.add("four"));
}
```

## 4. 使用Apache Collections Commons

最后，Commons Collection还提供了一个API来创建一个不可修改的集合：

```java
ListUtils.unmodifiableList(list);
```

同样，修改结果集合应该会导致UnsupportedOperationException：

```java
@Test
final void givenUsingCommonsCollections_whenUnmodifiableListIsCreated_thenNotModifiable() {
	final List<String> list = new ArrayList<>(Arrays.asList("one", "two", "three"));
	final List<String> unmodifiableList = ListUtils.unmodifiableList(list);
	assertThrows(UnsupportedOperationException.class, () -> unmodifiableList.add("four"));
}
```

## 5. 总结

本文说明了如何使用核心JDK、Google Guava或Apache Commons Collections从现有ArrayList创建不可修改的List。