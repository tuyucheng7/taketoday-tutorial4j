## 1. 概述

有时最好禁止修改 java.util.Map ，例如跨线程共享只读数据。为此，我们可以使用 Unmodifiable Map 或 Immutable Map。

在本快速教程中，我们将了解它们之间的区别。然后，我们将介绍创建不可变映射的各种方法。

## 2.不可修改与不可变

Unmodifiable Map 只是对可修改 map 的包装，它不允许直接对其进行修改：

```java
Map<String, String> mutableMap = new HashMap<>();
mutableMap.put("USA", "North America");

Map<String, String> unmodifiableMap = Collections.unmodifiableMap(mutableMap);
assertThrows(UnsupportedOperationException.class,
  () -> unmodifiableMap.put("Canada", "North America"));
```

但是底层的可变映射仍然可以更改，并且修改也会反映在不可修改的映射中：

```java
mutableMap.remove("USA");
assertFalse(unmodifiableMap.containsKey("USA"));
		
mutableMap.put("Mexico", "North America");
assertTrue(unmodifiableMap.containsKey("Mexico"));
```

另一方面，不可变映射包含其自己的私有数据并且不允许对其进行修改。因此，一旦创建了 Immutable Map 的实例，数据就不能以任何方式更改。

## 3. Guava 的不可变映射

[Guava](https://github.com/google/guava)提供了每个java.util的不可变版本。使用 ImmutableMap映射 。每当我们尝试修改它时，它都会抛出 UnsupportedOperationException 。

由于它包含自己的私有数据，因此当原始地图发生变化时，这些数据不会改变。

我们现在将讨论创建 ImmutableMap 实例的各种方法。

### 3.1. 使用copyOf()方法

首先，让我们使用ImmutableMap.copyOf() 方法返回原始映射中所有条目的副本：

```java
ImmutableMap<String, String> immutableMap = ImmutableMap.copyOf(mutableMap);
assertTrue(immutableMap.containsKey("USA"));
```

它不能直接或间接修改：

```java
assertThrows(UnsupportedOperationException.class,
  () -> immutableMap.put("Canada", "North America"));
		
mutableMap.remove("USA");
assertTrue(immutableMap.containsKey("USA"));
		
mutableMap.put("Mexico", "North America");
assertFalse(immutableMap.containsKey("Mexico"));
```

### 3.2. 使用builder()方法

我们还可以使用 ImmutableMap.builder() 方法创建原始地图中所有条目的副本。

此外，我们可以使用此方法添加原始地图中不存在的其他条目：

```java
ImmutableMap<String, String> immutableMap = ImmutableMap.<String, String>builder()
  .putAll(mutableMap)
  .put("Costa Rica", "North America")
  .build();
assertTrue(immutableMap.containsKey("USA"));
assertTrue(immutableMap.containsKey("Costa Rica"));
```

和前面的例子一样，我们不能直接或间接修改它：

```java
assertThrows(UnsupportedOperationException.class,
  () -> immutableMap.put("Canada", "North America"));
		
mutableMap.remove("USA");
assertTrue(immutableMap.containsKey("USA"));
		
mutableMap.put("Mexico", "North America");
assertFalse(immutableMap.containsKey("Mexico"));
```

### 3.3. 使用of()方法

最后，我们可以使用ImmutableMap.of() 方法创建一个不可变映射，其中包含一组动态提供的条目。它最多支持五个键/值对：

```java
ImmutableMap<String, String> immutableMap
  = ImmutableMap.of("USA", "North America", "Costa Rica", "North America");
assertTrue(immutableMap.containsKey("USA"));
assertTrue(immutableMap.containsKey("Costa Rica"));
```

我们也不能修改它：

```java
assertThrows(UnsupportedOperationException.class,
  () -> immutableMap.put("Canada", "North America"));
```

## 4。总结

在这篇简短的文章中，我们讨论了 Unmodifiable Map 和 Immutable Map 之间的区别。

我们还了解了创建 Guava 的ImmutableMap 的不同方法。