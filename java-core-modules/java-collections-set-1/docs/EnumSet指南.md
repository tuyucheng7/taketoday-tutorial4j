## 1. 概述

在本教程中，我们将探索java.util包中的EnumSet集合 并讨论其特性。

我们将首先展示该集合的主要特性，然后，我们将浏览该类的内部结构以了解它的好处。

最后，我们将介绍它提供的主要操作并实现一些基本示例。

## 2. 什么是EnumSet

EnumSet是一个 专门的Set集合，用于处理枚举类。它实现了Set接口并从AbstractSet扩展：

[![枚举集-1-2](https://www.baeldung.com/wp-content/uploads/2018/10/EnumSet-1-2.jpg)](https://www.baeldung.com/wp-content/uploads/2018/10/EnumSet-1-2.jpg)

尽管AbstractSet和AbstractCollection为Set和Collection接口的几乎所有方法提供了实现，但EnumSet覆盖了其中的大部分方法。

当我们计划使用EnumSet时，我们必须考虑一些要点：

-   它只能包含枚举值，并且所有值都必须属于同一个枚举
-   它不允许添加空值，尝试这样做会抛出NullPointerException
-   它不是线程安全的，所以如果需要我们需要在外部同步它
-   元素按照它们在枚举中声明的顺序存储
-   它使用在副本上工作的故障安全迭代器，因此如果在迭代集合时修改集合，它不会抛出 ConcurrentModificationException

## 3. 为什么使用EnumSet

根据经验，当我们存储枚举值时， EnumSet应始终优先于任何其他Set实现。

在接下来的部分中，我们将了解是什么让这个系列比其他系列更好。为此，我们将简要展示该类的内部结构以便更好地理解。

### 3.1. 实施细节

EnumSet是一个公共 抽象类，包含多个允许我们创建实例的静态工厂方法。JDK 提供了 2 种不同的实现——包私有的并由位向量支持：

-   RegularEnumSet和
-   巨型枚举集

RegularEnumSet使用单个long来表示位向量。long元素的每一位代表enum的一个值。枚举的第 i 个值将存储在第 i 个位中，因此很容易知道一个值是否存在。 由于long是 64 位数据类型，因此此实现最多可以存储 64 个元素。

另一方面，JumboEnumSet使用长元素数组作为位向量。 这使此实现可以存储超过 64 个元素。它的工作方式与RegularEnumSet非常相似， 但会进行一些额外的计算以找到存储值的数组索引。

不出所料，数组的第一个 long 元素将存储enum的前 64 个值，第二个元素存储下一个 64，依此类推。

EnumSet工厂方法根据枚举的元素数量创建一个或另一个实现的实例：

```java
if (universe.length <= 64)
    return new RegularEnumSet<>(elementType, universe);
else
    return new JumboEnumSet<>(elementType, universe);
```

请记住，它只考虑枚举类的大小，而不是将存储在集合中的元素数量。

### 3.2. 使用EnumSet的好处

由于 我们上面描述的EnumSet的实现，EnumSet 中的所有方法都是使用算术按位运算实现的。这些计算非常快，因此所有基本操作都在恒定时间内执行。

如果我们将EnumSet与其他Set实现(如HashSet )进行比较，第一个通常更快，因为值以可预测的顺序存储，并且每次计算只需要检查一位。与HashSet不同，无需计算哈希码即可找到正确的存储桶。

此外，由于位向量的性质，EnumSet非常紧凑和高效。因此，它使用更少的内存，并具有它带来的所有好处。

## 四、主要业务

EnumSet的大部分方法与任何其他Set一样工作，但创建实例的方法除外。

在接下来的部分中，我们将详细介绍所有创建方法，并简要介绍其余方法。

在我们的示例中，我们将使用Color 枚举：

```java
public enum Color {
    RED, YELLOW, GREEN, BLUE, BLACK, WHITE
}
```

### 4.1. 创作方法

创建EnumSet的最简单方法是allOf()和noneOf()。这样我们就可以轻松创建一个包含Color枚举的所有元素的EnumSet ：

```java
EnumSet.allOf(Color.class);
```

同样，我们可以使用noneOf()做相反的事情并创建一个空的Color集合：

```java
EnumSet.noneOf(Color.class);
```

如果我们想用枚举元素的子集创建一个EnumSet ，我们可以使用重载 的 of()方法。区分具有最多 5 个不同参数的固定参数的方法和使用varargs的方法很重要：

[![的-1](https://www.baeldung.com/wp-content/uploads/2018/10/of-1.png)](https://www.baeldung.com/wp-content/uploads/2018/10/of-1.png)

Javadoc 声明可变参数版本的性能可能比其他版本慢，因为数组的创建。因此，只有在我们最初需要添加超过 5 个元素时才应该使用它。

另一种创建枚举子集的方法是使用range()方法：

```java
EnumSet.range(Color.YELLOW, Color.BLUE);
```

在上面的示例中， EnumSet包含从黄色到蓝色的所有元素。 它们遵循枚举中定义的顺序：

```shell
[YELLOW, GREEN, BLUE]
```

请注意，它包括指定的第一个和最后一个元素。

另一个有用的工厂方法是complementOf()，它允许我们排除作为参数传递的元素。让我们用除黑色和白色之外的所有颜色元素创建一个EnumSet ：

```java
EnumSet.complementOf(EnumSet.of(Color.BLACK, Color.WHITE));
```

如果我们打印这个集合，我们可以看到它包含所有其他元素：

```shell
[RED, YELLOW, GREEN, BLUE]
```

最后，我们可以通过从另一个EnumSet所有元素来创建一个EnumSet：

```java
EnumSet.copyOf(EnumSet.of(Color.BLACK, Color.WHITE));
```

在内部，它调用克隆方法。

此外，我们还可以从任何包含枚举元素的集合中所有元素。让我们用它来列表的所有元素：

```java
List<Color> colorsList = new ArrayList<>();
colorsList.add(Color.RED);
EnumSet<Color> listCopy = EnumSet.copyOf(colorsList);
```

在这种情况下，listCopy仅包含红色。

### 4.2. 其他操作

其余操作的工作方式与任何其他Set实现完全相同，并且在如何使用它们方面没有区别。

因此，我们可以很容易地创建一个空的EnumSet并添加一些元素：

```java
EnumSet<Color> set = EnumSet.noneOf(Color.class);
set.add(Color.RED);
set.add(Color.YELLOW)
```

检查集合是否包含特定元素：

```java
set.contains(Color.RED);
```

遍历元素：

```java
set.forEach(System.out::println);
```

或者简单地删除元素：

```java
set.remove(Color.RED);
```

当然，这是Set 支持的所有其他操作之一。

## 5.总结

在本文中，我们展示了EnumSet的主要特性、其内部实现以及我们如何从中受益。

我们还介绍了它提供的主要方法，并实现了一些示例来展示我们如何使用它们。