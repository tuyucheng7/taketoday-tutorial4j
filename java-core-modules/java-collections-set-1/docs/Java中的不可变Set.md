## 1. 概述

在本教程中，我们将了解在Java中构建不可变集的不同方法。

但首先，让我们了解不可变集并了解我们为什么需要它。

## 2.什么是不可变集？

通常，不可[变对象](https://www.baeldung.com/java-immutable-object)一旦创建就不会改变其内部状态。 这使得它默认是线程安全的。同样的逻辑适用于不可变集。

假设我们有一个带有一些值的[HashSet](https://www.baeldung.com/java-hashset)实例。使其不可变将创建我们集合的“只读”版本。因此， 任何修改其状态的尝试都将抛出UnsupportedOperationException。

那么，我们为什么需要它？

当然，不可变集最常见的用例是多线程环境。因此，我们可以跨线程共享不可变数据而不用担心同步。

同时，有一点需要牢记：不变性仅适用于集合而不适用于其元素。此外，我们可以毫无问题地修改集合元素的实例引用。

## 3. 在 CoreJava中创建不可变集

只要掌握了核心Java类，我们就可以使用Collections。unmodifiableSet() 方法来包装原始Set。

首先，让我们创建一个简单的[HashSet](https://www.baeldung.com/java-hashset)实例并使用String值对其进行初始化：

```java
Set<String> set = new HashSet<>();
set.add("Canada");
set.add("USA");
```

接下来，让我们用 Collections来结束它。不可修改的集合()：

```java
Set<String> unmodifiableSet = Collections.unmodifiableSet(set);
```

最后，为了确保我们的unmodifiableSet实例是不可变的，让我们创建一个简单的测试用例：

```java
@Test(expected = UnsupportedOperationException.class)
public void testUnmodifiableSet() {
    // create and initialize the set instance

    Set<String> unmodifiableSet = Collections.unmodifiableSet(set);
    unmodifiableSet.add("Costa Rica");
}
```

正如我们所料，测试将成功运行。此外， add()操作在 unmodifiableSet实例 上被禁止，并且会抛出UnsupportedOperationException。

 现在，让我们通过向其添加相同的值来更改初始集合实例：

```java
set.add("Costa Rica");
```

这样，我们间接修改了不可修改的集合。因此，当我们打印 unmodifiableSet 实例时：

```plaintext
[Canada, USA, Costa Rica]
```

正如我们所看到的， “哥斯达黎加”项目也出现在 不可修改的集合中。

## 4. 在Java9 中创建不可变集

从Java9 开始， Set.of(elements)静态工厂方法可用于创建不可变集合：

```java
Set<String> immutable = Set.of("Canada", "USA");
```

## 5. 在 Guava 中创建不可变集

另一种构造不可变集的方法是使用 Guava 的ImmutableSet 类。它将现有数据到一个新的不可变实例中。因此，当我们改变原始Set时， ImmutableSet中的数据 不会改变。

与核心Java实现一样，任何修改创建的不可变实例的尝试都将抛出 UnsupportedOperationException。

现在，让我们探索创建不可变实例的不同方法。

### 5.1. 使用 不可变集。备份()

简单地说，就是ImmutableSet。copyOf()方法返回集合中所有元素的副本：

```java
Set<String> immutable = ImmutableSet.copyOf(set);
```

因此，在更改初始集合后，不可变实例将保持不变：

```plaintext
[Canada, USA]
```

### 5.2. 使用 ImmutableSet.of ()

类似地，使用 ImmutableSet.of() 方法我们可以立即创建一个具有给定值的不可变集合：

```java
Set<String> immutable = ImmutableSet.of("Canada", "USA");
```

当我们不指定任何元素时， ImmutableSet.of()将返回一个空的不可变集。

这可以与Java9 的Set.of () 进行比较。

## 六，总结

在这篇简短的文章中，我们讨论了Java语言中的不可变集。此外，我们展示了如何 使用来自核心 Java、Java 9 和 Guava 库的 Collections API创建不可变集。