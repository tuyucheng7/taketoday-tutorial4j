## 1. 概述

在本文中，我们将采用整体方法来**了解Kotlin中对数组的支持**。

## 2. get和set函数

首先，让我们用字符串值[初始化一个数组](https://www.baeldung.com/kotlin/initialize-array)来存储一些运动的名称：

```kotlin
sports = arrayOf("Soccer", "Cricket", "Rugby")
```

与大多数现代编程语言一样，**Kotlin使用从零开始的数组索引**。此外，Kotlin中的Array.kt类提供了一个get函数，通过运算符重载将其变成[]：

```kotlin
public operator fun get(index: Int): T
```

让我们使用get函数通过索引访问数组成员：

```kotlin
Assertions.assertEquals("Soccer", sports.get(0))
Assertions.assertEquals("Cricket", sports.get(1))
Assertions.assertEquals("Rugby", sports.get(2))
```

**如果我们尝试访问数组的无效索引，Kotlin会抛出IndexOutOfBoundsException**：

```kotlin
Assertions.assertThrows(IndexOutOfBoundsException::class.java) {
    sports.get(100)
}
Assertions.assertThrows(IndexOutOfBoundsException::class.java) {
    sports.get(-1)
}
```

接下来，让我们使用set函数来修改数组中的一个值：

```kotlin
sports.set(0, "Football")
Assertions.assertEquals("Football", sports[0])
```

最后，让我们尝试使用无效索引修改值并验证我们是否看到IndexOutOfBoundsException：

```kotlin
Assertions.assertThrows(IndexOutOfBoundsException::class.java) {
    sports.set(10, "Football")
}
```

## 3. 数组遍历

Kotlin支持多种遍历数组的方式，在本节中，我们将探讨大多数典型的**数组遍历模式**。但是，在此之前，让我们定义一个整数值数组来存储写在普通骰子六个面上的每个面上的值：

```kotlin
val dice = arrayOf(1, 2, 3, 4, 5, 6)
```

首先，让我们使用for[循环](https://www.baeldung.com/kotlin/loops)来遍历dice数组：

```kotlin
for (faceValue in dice) {
    println(faceValue)
}
```

接下来，让我们看看如何初始化数组迭代器来迭代值：

```kotlin
val iterator = dice.iterator()
while (iterator.hasNext()) {
    val faceValue = iterator.next()
    println(faceValue)
}
```

循环遍历数组的另一种典型模式是使用forEach循环：

```kotlin
dice.forEach { faceValue ->
    println(faceValue)
}
```

最后，让我们看看如何使用**forEachIndexed模式**，它为我们**提供数组中的索引和该索引处的值**：

```kotlin
dice.forEachIndexed { index, faceValue ->
    println("Value at $index position is $faceValue")
}
```

## 4. 就地重新排序

在本节中，我们将学习一些对数组值进行就地重新排序的常用方法。

### 4.1 设置

让我们定义辅助函数来生成和打印整数数组的值，以便我们可以重用它们来验证多个场景。

首先，我们有initArray()函数来返回一个int数组：

```kotlin
fun initArray(): Array<Int> {
    return arrayOf(3, 2, 50, 15, 10, 1)
}
```

其次，我们有printArray()函数来打印数组的值：

```kotlin
fun printArray(array: Array<Int>) {
    array.forEach { print("$it ") }
    println()
}
```

### 4.2 reverse

**Kotlin支持数组的部分和完全就地反转**，我们将通过在同一个数组上运行来独立处理每个场景：

```kotlin
3 2 50 15 10 1
```

首先，让我们看一下数字数组的完全反转：

```kotlin
numbers = initArray()
numbers.reverse()
printArray(numbers)
```

正如预期的那样，输出显示整个数组被颠倒了：

```kotlin
1 10 15 50 2 3
```

现在，让我们反转索引4(含)和6(不含)之间的数组：

```kotlin
numbers = initArray()
numbers.reverse(4, 6)
printArray(numbers)
```

我们可以看到只有数组中的最后两个值发生了变化，而直到index = 3的值保持不变：

```kotlin
3 2 50 15 1 10
```

### 4.3 sort

Kotlin**支持根据元素的自然顺序对数组进行就地稳定排序**。同样，我们将在所有排序场景中使用原始数字数组：

```kotlin
3 2 50 15 10 1
```

首先，让我们对数字数组的整个范围进行排序：

```kotlin
numbers = initArray()
numbers.sort()
printArray(numbers)
```

正如预期的那样，数字按升序排序：

```kotlin
1 2 3 10 15 50
```

接下来，让我们对原始数字数组从index = 4(含)到最后一个位置进行排序：

```kotlin
numbers = initArray()
numbers.sort(4)
printArray(numbers)
```

我们可以看到index = 3之前的值保持不变，而index = 4的值按升序排序：

```kotlin
3 2 50 15 1 10
```

最后，让我们对索引0(含)和2(不含)之间的数字数组进行排序：

```kotlin
numbers = initArray()
numbers.sort(0, 2)
printArray(numbers)
```

正如预期的那样，只有前两个元素被排序到位：

```kotlin
2 3 50 15 10 1
```

### 4.4 洗牌

Kotlin提供了两个重载函数，它们使用[Fisher-Yates混洗算法](https://en.wikipedia.org/wiki/Fisher–Yates_shuffle#The_modern_algorithm)对数组进行混洗：

```kotlin
public fun <T> Array<T>.shuffle(): Unit
public fun <T> Array<T>.shuffle(random: Random): Unit
```

首先，让我们调用无参数shuffle()函数并查看重新排序的数组值：

```kotlin
numbers = initArray()
numbers.shuffle()
printArray(numbers)
```

正如预期的那样，这些值被原地洗牌：

```kotlin
2 10 3 1 50 15
```

接下来，让我们传递一个种子值为2的Random实例：

```kotlin
numbers = initArray()
numbers.shuffle(Random(2))
printArray(numbers)
```

同样，洗牌从输出中非常明显：

```kotlin
1 3 50 15 10 2
```

我们必须注意到种子值在洗牌中起着至关重要的作用，**给定相同的数组值和具有相同种子值的Random实例，我们将在第一次洗牌后始终获得相同顺序的值**。

## 5. 构建Map

在本节中，我们将探索一些函数，它们可以帮助我们编写漂亮的代码来**通过转换数组值来构建**[Map](https://www.baeldung.com/kotlin/maps)。

### 5.1 associate、associateBy和associateWith

我们可以转换数组的值以生成Map的键和值，此外，我们还可以决定保留数组值来表示键集或值集。为了支持这些多个用例，Kotlin提供了函数associate()、associateBy()和asssociateWith()。

让我们初始化一个数组来存储一些常见水果的名称：

```kotlin
val fruits = arrayOf("Pear", "Apple", "Papaya", "Banana")
```

现在，如果我们想要构建一个水果名称与名称长度的Map，那么我们可以使用associate()函数：

```kotlin
println(fruits.associate { Pair(it, it.length) })
{Pear=4, Apple=5, Papaya=6, Banana=6}
```

但是，由于我们直接从数组值生成键，我们还可以使用associateWith()函数来决定如何为每个键生成映射值：

```kotlin
println(fruits.associateWith { it.length })
{Pear=4, Apple=5, Papaya=6, Banana=6}
```

最后，如果我们想构建一个以水果名称的长度为键，水果名称为值的Map，那么我们可以使用associateBy()函数：

```kotlin
println(fruits.associateBy { it.length })
{4=Pear, 5=Apple, 6=Banana}
```

### 5.2 associateTo、associateByTo和associateWithTo

当我们使用associate()、associateBy()或associateWith()时，Kotlin会**创建一个新Map并填充其数据**。但是，我们也可以使用这些函数的associateTo()、associateByTo()和associateWithTo()变体在现有Map中填充数据。

假设我们有水果名称和名称长度的现有Map：

```kotlin
val nameVsLengthMap = mutableMapOf("Pomegranate" to 11, "Pea" to 3)
val lengthVsNameMap = mutableMapOf(11 to "Pomegranate", 3 to "Pea")
```

现在，我们可以使用associateWithTo()和associateByTo()函数用新值填充这些Map：

```kotlin
println(fruits.associateWithTo(nameVsLengthMap, { it.length }))
println(fruits.associateByTo(lengthVsNameMap, { it.length }))
{Pomegranate=11, Pea=3, Pear=4, Apple=5, Papaya=6, Banana=6}
{11=Pomegranate, 3=Pea, 4=Pear, 5=Apple, 6=Banana}
```

## 6. 总结

在本文中，我们探索了使用Kotlin支持的几个函数来处理数组的方法。