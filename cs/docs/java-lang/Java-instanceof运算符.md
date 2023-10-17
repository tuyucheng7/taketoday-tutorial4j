## 1. 概述

在本快速教程中，我们将了解Java中的instanceof运算符。

## 2. 什么是instanceof操作符？

instanceof是我们用来测试对象是否属于给定类型的二元运算符。操作的结果是true或false。它也称为类型比较运算符，因为它将实例与类型进行比较。

在[转换](https://www.baeldung.com/java-type-casting)未知对象之前，应始终使用instanceof检查。这样做有助于避免在运行时出现ClassCastException。

instanceof运算符的基本语法是：

```java
(object) instanceof (type)
```

现在让我们看一下instanceof运算符的基本示例。首先，我们将创建一个Round类：

```java
public class Round {
    // implementation details
}
```

接下来，我们将创建一个扩展Round的Ring类：

```java
public class Ring extends Round {
    // implementation details
}
```

我们可以使用instanceof来检查Ring的实例是否为Round类型：

```java
@Test
public void givenWhenInstanceIsCorrect_thenReturnTrue() {
    Ring ring = new Ring();
    Assert.assertTrue(ring instanceof Round);
}
```

## 3. instanceofOperator是如何工作的？

instanceof运算符的工作原理是is-a关系。is-a关系的概念基于类[继承](https://www.baeldung.com/java-inheritance-composition)或接口实现。

为了演示这一点，我们将创建一个Shape接口：

```java
public interface Shape {
    // implementation details
}
```

我们还将创建一个Circle类，它实现了Shape接口并扩展了Round类：

```java
public class Circle extends Round implements Shape {
    // implementation details
}
```

如果对象是以下类型的实例，则instanceof结果将为真：

```java
@Test
public void givenWhenObjectIsInstanceOfType_thenReturnTrue() {
    Circle circle = new Circle();
    Assert.assertTrue(circle instanceof Circle);
}
```

如果对象是该类型的子类的实例，它也将为真：

```java
@Test
public void giveWhenInstanceIsOfSubtype_thenReturnTrue() {
    Circle circle = new Circle();
    Assert.assertTrue(circle instanceof Round);
}
```

如果类型是接口，如果对象实现了接口，它将返回true：

```java
@Test
public void givenWhenTypeIsInterface_thenReturnTrue() {
    Circle circle = new Circle();
    Assert.assertTrue(circle instanceof Shape);
}
```

如果被比较的对象与被比较的类型之间没有关系，则不能使用instanceof运算符。

我们将创建一个新类Triangle，它实现了Shape，但与Circle没有任何关系：

```java
public class Triangle implements Shape {
    // implementation details
}
```

现在，如果我们使用instanceof来检查Circle是否是Triangle的实例：

```java
@Test
public void givenWhenComparingClassInDiffHierarchy_thenCompilationError() {
    Circle circle = new Circle();
    Assert.assertFalse(circle instanceof Triangle);
}
```

我们会得到一个编译错误，因为Circle和Triangle类之间没有关系：

```shell
java.lang.Error: Unresolved compilation problem:
  Incompatible conditional operand types Circle and Triangle
```

## 4. 将instanceof与对象类型一起使用

在Java中，每个类都隐式继承自Object类。因此，将instanceof运算符与Object类型一起使用将始终评估为true：

```java
@Test
public void givenWhenTypeIsOfObjectType_thenReturnTrue() {
    Thread thread = new Thread();
    Assert.assertTrue(thread instanceof Object);
}
```

## 5. 当对象为空时使用instanceof运算符

如果我们在任何为null的对象上使用instanceof运算符，它会返回false。在使用instanceof运算符时，我们也不需要空值检查。

```java
@Test
public void givenWhenInstanceValueIsNull_thenReturnFalse() {
    Circle circle = null;
    Assert.assertFalse(circle instanceof Round);
}
```

## 6. instanceof和泛型

实例测试和转换依赖于在运行时检查类型信息。因此，我们不能将instanceof与已[擦除的泛型](https://www.baeldung.com/java-type-erasure)一起使用。

例如，如果我们尝试编译以下代码片段：

```java
public static <T> void sort(List<T> collection) {
    if (collection instanceof List<String>) {
        // sort strings differently
    }
        
    // omitted
}
```

然后我们得到这个编译错误：

```shell
error: illegal generic type for instanceof
        if (collection instanceof List<String>) {                        ^
```

从技术上讲，我们只允许在Java中使用instanceof和具体化类型。如果类型信息在运行时存在，则类型被具体化。

Java中具体化的类型如下：

-   原始类型，如int
-   非泛型类和接口，如String或Random
-   所有类型都是无限通配符的通用类型，如Set<?>或Map<?, ?\>
-   原始类型，如List或HashMap
-   其他可具体化类型的数组，如String[]、List[]或Map<?, ?\>[]

因为泛型类型参数没有具体化，所以我们也不能使用它们：

```java
public static <T> boolean isOfType(Object input) {
    return input instanceof T; // won't compile
}
```

但是，可以针对List<?\>之类的内容进行测试：

```java
if (collection instanceof List<?>) {
    // do something
}
```

## 7. 总结

在这篇简短的文章中，我们了解了instanceof运算符以及如何使用它。