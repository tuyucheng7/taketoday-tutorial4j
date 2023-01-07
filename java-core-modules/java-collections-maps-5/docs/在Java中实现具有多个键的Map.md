## 1. 概述

我们经常在我们的程序中使用映射，作为将键与值相关联的一种方式。通常在我们的Java程序中，特别是自从引入[泛型](https://www.baeldung.com/java-generics)以来，我们会让所有的键都是相同的类型，所有的值都是相同的类型。例如，ID 到数据存储中的值的映射。

在某些情况下，我们可能希望使用键不总是相同类型的映射。例如，如果我们将 ID 类型从Long更改为String， 那么我们的数据存储将需要支持两种键类型——Long用于旧条目，String用于新条目。

不幸的是，Java Map接口不允许多个键类型，因此我们需要找到另一种解决方案。我们将在本文中探索实现这一目标的几种方法。

## 2. 使用泛型超类型

实现这一点的最简单方法是拥有一个映射，其中键类型是最接近我们所有键的超类型。在某些情况下，这可能很容易——例如，如果我们的键是Long和Double那么最接近的超类型是Number：

```java
Map<Number, User> users = new HashMap<>();

users.get(longId);
users.get(doubleId);
```

但是，在其他情况下，最接近的超类型是Object。这样做的缺点是它从我们的地图中完全消除了类型安全：

```java
Map<Object, User> users = new HashMap<>();

users.get(longId); /// Works.
users.get(stringId); // Works.
users.get(Instant.now()); // Also works.
```

在这种情况下，编译器不会阻止我们传递错误的类型，有效地从我们的映射中删除所有类型安全。在某些情况下，这可能没问题。例如，如果另一个类封装映射以强制类型安全本身，这可能会很好。

然而，它仍然在如何使用地图方面带来风险。

## 3.多张地图

如果类型安全很重要，并且我们将把我们的地图封装在另一个类中，另一个简单的选择是拥有多个地图。在这种情况下，我们将为每个支持的键提供不同的映射：

```java
Map<Long, User> usersByLong = new HashMap<>();
Map<String, User> usersByString = new HashMap<>();
```

这样做可以确保编译器为我们保持类型安全。如果我们尝试在这里使用Instant，那么编译器不会让我们这样做，所以我们在这里是安全的。

不幸的是，这增加了复杂性，因为我们需要知道要使用哪些地图。这意味着我们要么使用不同的方法处理不同的映射，要么我们到处都在进行类型检查。

这也不能很好地扩展。如果我们需要添加新的密钥类型，我们将需要添加一个新的映射和新的检查。对于两个或三个密钥类型，这是可以管理的，但很快就会变得太多。

## 4. 密钥包装器类型

如果我们需要类型安全，并且我们不想要许多映射的可维护性负担，那么我们需要找到一种方法来拥有一个可以在键中具有不同值的映射。这意味着我们需要找到一些方法来拥有一个实际上是不同类型的单一类型。我们可以通过两种不同的方式实现这一点——使用单个包装器或使用接口和子类。

### 4.1. 单一包装类

我们的一个选择是编写一个可以包装任何可能的密钥类型的类。这将有一个用于实际键值的字段、正确的equals和hashCode方法，然后为每种可能的类型都有一个构造函数：

```java
class MultiKeyWrapper {
    private final Object key;

    MultiKeyWrapper(Long key) {
        this.key = key;
    }

    MultiKeyWrapper(String key) {
        this.key = key;
    }

    @Override
    public bool equals(Object other) { ... }

    @Override
    public int hashCode() { ... }
}
```

这保证是类型安全的，因为它只能用Long或String构造。我们可以在地图中将其用作单一类型，因为它本身就是一个类：

```java
Map<MultiKeyWrapper, User> users = new HashMap<>();
users.get(new MultiKeyWrapper(longId)); // Works
users.get(new MultiKeyWrapper(stringId)); // Works
users.get(new MultiKeyWrapper(Instant.now())); // Compilation error
```

我们只需要在每次访问地图时将Long或String包装在我们新的MultiKeyWrapper中。

这个比较简单，但是会让扩展稍微有点难度。每当我们想要支持任何其他类型时，我们都需要更改我们的MultiKeyWrapper类以支持它。

### 4.2. 接口和子类

另一种选择是编写一个接口来表示我们的密钥包装器，然后为我们要支持的每种类型编写该接口的实现：

```java
interface MultiKeyWrapper {}

record LongMultiKeyWrapper(Long value) implements MultiKeyWrapper {}
record StringMultiKeyWrapper(String value) implements MultiKeyWrapper {}
```

正如我们所看到的，这些实现可以使用Java14 中引入的[Record 功能](https://www.baeldung.com/java-record-keyword)，这将使实现变得更加容易。

和以前一样，我们可以使用我们的MultiKeyWrapper作为地图的单一键类型。然后，我们对要使用的密钥类型使用适当的实现：

```java
Map<MultiKeyWrapper, User> users = new HashMap<>();
users.get(new LongMultiKeyWrapper(longId)); // Works
users.get(new StringMultiKeyWrapper(stringId)); // Works

```

在这种情况下，我们没有可用于任何其他用途的类型，因此我们甚至不能一开始就编写无效代码。

通过这个解决方案，我们支持额外的键类型，而不是通过改变现有的类，而是通过编写一个新的类。这更容易支持，但这也意味着我们对支持哪些密钥类型的控制较少。

然而，这可以通过正确使用[可见性修饰符](https://www.baeldung.com/java-access-modifiers)来管理。类只有在有权访问它时才能实现我们的接口，因此如果我们将其设为包私有，则只有同一包中的类才能实现它。

## 5.总结

在这里，我们已经看到了一些表示键到值映射的方法，但是键并不总是属于同一类型。