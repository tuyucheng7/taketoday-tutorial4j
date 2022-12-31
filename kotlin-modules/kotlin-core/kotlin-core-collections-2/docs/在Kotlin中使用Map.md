## 1. 概述

在本教程中，我们将了解Kotlin中的Map集合类型，我们将从Map的定义及其特征开始。

然后我们将研究如何在Kotlin中创建Map，本文的其余部分将讨论读取条目、更改条目和数据转换等常见操作。

## 2. Kotlin Maps

Map是计算机科学中的一种常见数据结构，它们在其他编程语言中也称为字典或关联数组，Map可以存储零个或多个键值对的集合。

**Map中的每个键都是唯一的，并且只能与一个值相关联**。但是，相同的值可以与多个键相关联。

[Map接口](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/)是Kotlin中的主要集合类型之一，我们可以将键和值声明为任何类型；没有限制：

```kotlin
interface Map<K, out V>
```

在Kotlin中，这些键值对称为条目，由Entry接口表示：

```kotlin
interface Entry<out K, out V>
```

**请注意，Map实例是不可变的，创建后我们无法添加、删除或更改条目**。如果我们需要一个可变Map，Kotlin提供了一个MutableMap类型，它允许我们在创建后修改条目：

```kotlin
interface MutableMap<K, V> : Map<K, V>
```

Map是程序员的强大工具，因为它们支持快速读写访问，即使是大型数据集。这是因为键查找和插入通常是通过哈希实现的，这是一个O(1)操作。让我们看看如何在Kotlin中使用Map！

## 3. 构造Map

Kotlin在标准库中包含多个Map实现。两种主要类型是LinkedHashMap和HashMap，**它们之间的主要区别在于LinkedHashMap在迭代其条目时维护插入顺序**。与Kotlin中的任何类一样，你可以使用默认构造函数实例化它们：

```kotlin
val iceCreamInventory = LinkedHashMap<String, Int>()
iceCreamInventory["Vanilla"] = 24
```

但是，Kotlin提供了通过Collections API创建Map的更有效方法，接下来我们看看这些。

### 3.1 工厂函数

要在一次操作中声明和填充Map实例，我们使用mapOf和mutableMapOf函数。他们接收可以作为可变参数传递的Pairs列表。我们还使用“to”中缀运算符动态创建这些Pair：

```kotlin
val iceCreamInventory = mapOf("Vanilla" to 24, "Chocolate" to 14, "Rocky Road" to 7)
```

mapOf函数返回一个不可变的Map类型，而mutableMapOf返回一个MutableMap，仅当我们需要在创建后修改条目时才应使用后者。

### 3.2 使用Kotlin函数式API

**Kotlin的标准库附带了许多有用的API，使我们能够以非常简洁的方式生成Map**。例如，假设我们有一个IceCreamShipment对象列表。每个IceCreamShipment都有一个flavor和一个quantity属性：

```kotlin
val shipments = listOf(
    IceCreamShipment("Chocolate", 3),
    IceCreamShipment("Strawberry", 7),
    IceCreamShipment("Vanilla", 5),
    IceCreamShipment("Chocolate", 5),
    IceCreamShipment("Vanilla", 1),
    IceCreamShipment("Rocky Road", 10),
)
```

我们想从这个列表中生成我们的库存Map，一种常见的方法是遍历列表，每批货物都会为其风格创建或更新Map条目：

```kotlin
val iceCreamInventory = mutableMapOf<String, Int>()

for (shipment in shipments){
    val currentQuantity = iceCreamInventory[shipment.flavor] ?: 0
    iceCreamInventory[shipment.flavor] = currentQuantity + shipment.quantity
}
```

此实现可行，但生成此Map的更惯用的方法是使用Kotlin的函数式API：

```kotlin
val iceCreamInventory = shipments
    .groupBy({ it.flavor }, { it.quantity })
    .mapValues { it.value.sum() }
```

我们使用groupBy将口味与其数量相关联，然后我们使用mapValues函数将数量列表缩减为单个总和。如果我们知道我们的列表没有每个键(在本例中为冰淇淋口味)的多个条目，我们可以使用[map或associateBy](https://www.baeldung.com/kotlin/list-to-map)函数来代替。

如果我们发现我们使用MutableMap只是为了最初填充它，通常有更好的方法来代替使用Kotlin的函数式API来生成它。一般来说，Kotlin提供了许多有用的方法来实现常见的目标，例如将集合转换为Map。

## 4. 访问Map整体

我们使用get方法从Map中检索值，Kotlin还允许使用括号表示法作为get方法的简写：

```kotlin
val map = mapOf("Vanilla" to 24)

assertEquals(24, map.get("Vanilla"))
assertEquals(24, map["Vanilla"])
```

有一些getter方法定义了默认操作，以防Map中不存在键，如果找不到给定的键，getValue方法将抛出异常：

```kotlin
assertThrows(NoSuchElementException::class.java) { map.getValue("Banana") }
```

getOrElse方法接收一个lambda函数，该函数在键不在Map上时执行，lambda中的最终语句也用作返回值：

```kotlin
assertEquals(0, map.getOrElse("Banana", { print("Warning: Flavor not found in map"); 0 }))
```

最后，如果键不存在，getOrDefault方法返回提供的默认值：

```kotlin
assertEquals(0, map.getOrDefault("Banana", 0))
```

## 5. 添加和更新条目

如果我们使用的是MutableMap，那么我们可以使用put方法添加新条目，我们可以再次使用括号符号作为速记：

```kotlin
val iceCreamSales = mutableMapOf<String, Int>()

iceCreamSales.put("Chocolate", 1)
iceCreamSales["Vanilla"] = 2
```

也可以使用putAll方法添加多个条目，该方法接收要添加到Map的Pairs集合。或者，我们可以使用加号赋值运算符(+=)将所有条目从一个Map添加到另一个Map：

```kotlin
iceCreamSales.putAll(setOf("Strawberry" to 3, "Rocky Road" to 2))
iceCreamSales += mapOf("Maple Walnut" to 1, "Mint Chocolate" to 4)
```

**请注意，如果键已存在于Map中，则上述所有方法都将覆盖当前值。如果我们想更新一个条目，最好的方法是使用merge方法**。例如：

```kotlin
val iceCreamSales = mutableMapOf("Chocolate" to 2)
iceCreamSales.merge("Chocolate", 1, Int::plus)
assertEquals(3, iceCreamSales["Chocolate"])
```

merge方法接收一个键、一个值和一个remapping函数，remapping函数定义了如果键已经存在，我们希望如何合并旧值和新值。就我们的冰淇淋销售而言，我们只想将它们加在一起。

## 6. 删除条目

可变Map还提供了删除条目的方法，remove方法接收我们要从Map中删除的键参数。**如果键不存在，调用remove不会抛出异常**。我们可以选择使用减号(-=)运算符来执行相同的操作：

```kotlin
val map = mutableMapOf("Chocolate" to 14, "Strawberry" to 9)

map.remove("Strawberry")
map -= "Chocolate"
assertNull(map["Strawberry"])
assertNull(map["Chocolate"])
```

MutableMap接口还定义了一个clear方法，可以一次删除所有Map的条目。

## 7. 转换Map

与Kotlin中的其他集合类型一样，有许多方法可以根据我们应用程序的需要转换Map。让我们看一些有用的操作。对于下面显示的所有示例，这是我们库存Map中的初始数据：

```kotlin
val inventory = mutableMapOf(
    "Vanilla" to 24,
    "Chocolate" to 14,
    "Strawberry" to 9,
)
```

### 7.1 过滤

Kotlin为Map提供了几种过滤方法，要通过输入键或值进行过滤，分别有filterKeys和filterValues。如果我们需要同时进行过滤，可以使用filter方法，这是按剩余数量过滤冰淇淋库存的示例：

```kotlin
val lotsLeft = inventory.filterValues { qty -> qty > 10 }
assertEquals(setOf("Vanilla", "Chocolate"), lotsLeft.keys)
```

**filterValues方法将给定的谓词函数应用于Map中每个条目的值，过滤后的Map是匹配谓词条件的条目的集合**。

如果我们想过滤掉不符合这个条件的条目，我们可以使用filterNot方法来代替。

### 7.2 映射

map方法接收一个将每个条目转换为其他内容的转换函数，它返回Map值的列表：

```kotlin
val asStrings = inventory.map { (flavor, qty) -> "$qty tubs of $flavor" }

assertTrue(asStrings.containsAll(setOf("24 tubs of Vanilla", "14 tubs of Chocolate", "9 tubs of Strawberry")))
assertEquals(3, asStrings.size)
```

在这里，我们使用map方法生成描述我们当前库存的字符串列表。

### 7.3 使用forEach

作为最后一个示例，我们将使用我们已经学到的知识并介绍forEach方法，**forEach方法对给定Map中的每个条目执行操作**。在收货和销售冰淇淋一天后，我们需要更新我们商店的库存Map，我们将减去销售Map中的所有条目，然后添加出货Map中的所有条目以更新每种口味的数量：

```kotlin
val sales = mapOf("Vanilla" to 7, "Chocolate" to 4, "Strawberry" to 5)

val shipments = mapOf("Chocolate" to 3, "Strawberry" to 7, "Rocky Road" to 5)

with(inventory) {
    sales.forEach { merge(it.key, it.value, Int::minus) }
    shipments.forEach { merge(it.key, it.value, Int::plus) }
}

assertEquals(17, inventory["Vanilla"]) // 24 - 7 + 0
assertEquals(13, inventory["Chocolate"]) // 14 - 4 + 3
assertEquals(11, inventory["Strawberry"]) // 9 - 5 + 7
assertEquals(5, inventory["Rocky Road"]) // 0 - 0 + 5
```

我们在这里使用[with作用域函数](https://www.baeldung.com/kotlin/scope-functions#with)来保持我们的代码整洁，此示例展示了如何使用Kotlin强大的API轻松执行复杂的操作。

## 8. 总结

在本文中，我们介绍了如何在Kotlin中使用Map，Map是编写高效代码的重要工具，有很多方法可用；不止于此。我们应该始终参考[文档](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/#functions)以确保我们在代码中使用最合适的方法。