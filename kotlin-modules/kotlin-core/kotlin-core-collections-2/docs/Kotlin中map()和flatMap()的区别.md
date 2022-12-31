## 1. 概述

在本教程中，我们将熟悉Kotlin中map()和flatMap()之间的细微差别。

## 2. map()

[map()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/map.html)是Kotlin中的扩展函数，定义为：

```kotlin
fun <T, R> Iterable<T>.map(transform: (T) -> R): List<R> 
```

如上所示，此函数逐一迭代Iterable<T\>的所有元素，**在此迭代期间，它将T类型的每个元素转换为R类型的另一个元素**。最后，它转换接收集合的所有元素，我们将以List<R\>结束。

**这个函数通常在一对一映射的情况下很有用**，例如，假设每个订单由许多订单行组成，作为其详细的采购项目：

```kotlin
class Order(val lines: List<OrderLine>)
class OrderLine(val name: String, val price: Int)
```

现在，如果我们有一个Order，我们可以使用map()来查找每个项目的名称：

```kotlin
val order = Order(
  listOf(OrderLine("Tomato", 2), OrderLine("Garlic", 3), OrderLine("Chives", 2))
)
val names = order.lines.map { it.name }

assertThat(names).containsExactly("Tomato", "Garlic", "Chives")
```

在这里，我们通过传递一个简单的转换函数将List<OrderLine\>转换为List<String\>，此函数接收每个OrderLine作为输入(it变量)并将其转换为String。再举一个例子，我们可以像这样计算一个订单的总价：

```kotlin
val totalPrice = order.lines.map { it.price }.sum()
assertEquals(7, totalPrice)
```

基本上，map()等同于以下命令式编码风格：

```kotlin
val result = mutableListOf<R>()
for (each in this) {
    result += transform(each)
}
```

**当我们使用map()时，我们只需要编写转换部分**，定义一个新的集合、迭代并将每个转换后的元素添加到该集合只是一些样板代码和部分实现细节。

## 3. flatMap()

与map()相反，[flatMap()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/flat-map.html)**通常用于扁平化一对多关系**。因此，它的签名看起来像：

```kotlin
fun <T, R> Iterable<T>.flatMap(transform: (T) -> Iterable<R>): List<R>
```

如上所示，它将类型T的每个元素转换为类型R的集合。尽管如此，**flatMap()并没有以List<Iterable<R\>>结束**，而是将每个Iterable<R\>扁平化为其单独的元素；因此，我们将得到一个List<R\>作为结果。 

例如，假设我们有一个订单集合，我们将找到所有不同的商品名称：

```kotlin
val orders = listOf(
    Order(listOf(OrderLine("Garlic", 1), OrderLine("Chives", 2))),
    Order(listOf(OrderLine("Tomato", 1), OrderLine("Garlic", 2))),
    Order(listOf(OrderLine("Potato", 1), OrderLine("Chives", 2))),
)
```

首先，我们应该以某种方式将List<Order\>转换为List<OrderLine\>，如果我们在这里使用map()，我们最终会得到一个List<List<OrderLine\>>，这是不可取的：

```kotlin
orders.map { it.lines } // List<List<OrderLine>>
```

由于我们需要在此处将List<OrderLine\>扁平化为单个OrderLine，因此我们可以使用flatMap()函数：

```kotlin
val lines: List<OrderLine> = orders.flatMap { it.lines }
val names = lines.map { it.name }.distinct()
assertThat(names).containsExactlyInAnyOrder("Garlic", "Chives", "Tomato", "Potato")
```

如上所示，**flatMap()函数将每个Order与其OrderLines之间的一对多关系展平**。

等同于flatMap()的命令式样式类似于：

```kotlin
val result = mutableListOf<OrderLine>()
for (order in orders) {
    val transformedList = order.lines
    for (individual in transformedList) {
        result += individual
    }
}
```

同样，集合初始化、迭代和展平是flatMap()的隐藏实现细节的一部分，我们所要做的就是提供转换功能。

## 4. 总结

在本文中，我们了解了Kotlin中map()和flatMap()之间的区别。综上所述，map()通常对一对一映射有用，而flatMap()对于扁平化一对多映射更有用。