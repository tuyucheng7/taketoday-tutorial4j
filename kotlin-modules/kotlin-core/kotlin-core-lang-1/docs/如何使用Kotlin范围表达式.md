## 1. 简介

**范围是由开始、结束和步骤定义的值序列**。

在这个快速教程中，我们将了解如何在Kotlin中定义和使用范围。

## 2. 使用Kotlin范围

**在Kotlin中，我们可以使用rangeTo()和downTo()函数或..运算符创建范围**。

我们可以对任何可比较的类型使用范围。

**默认情况下，它们是包含在内的**，这意味着1..4表达式对应于值1、2、3和4。

此外，**还有另一个默认值**：**两个值之间的距离**，称为步长，隐含值为1。

那么现在，让我们看一下创建范围和使用其他有用方法来操作它们的几个示例。

### 2.1 创建范围

**范围实现了一个通用接口-ClosedRange<T\>**，ClosedRange的结果是一个级数(例如IntProgression、LongProgression或CharProgression)。

此级数包含开始、包含结束和步骤，它是Iterable<N\>的子类型，其中N是Int、Long或Char。

**让我们首先看一下创建范围的最简单方法，使用“..”和in运算符**：

```kotlin
(i in 1..9)
```

另外，如果我们想定义一个向后的范围，我们可以使用downTo运算符：

```kotlin
(i in 9 downTo 1)
```

我们还可以将这个表达式用作if语句的一部分来检查一个值是否属于某个范围：

```kotlin
if (3 in 1..9)
    print("yes")
```

### 2.2 迭代范围

**现在，虽然我们可以将范围与任何可比较的东西一起使用，但如果我们想要迭代，那么我们需要一个整数类型的范围**。

现在让我们看一下迭代范围的代码：

```kotlin
for (i in 1.rangeTo(9)) {
    print(i) // Print 123456789
}
  
for (i in 9.downTo(1)) {
    print(i) // Print 987654321
}
```

相同的用例适用于字符：

```kotlin
for (ch in 'a'..'f') {
    print(ch) // Print abcdef
}
  
for (ch in 'f' downTo 'a') {
    print(ch) // Print fedcba
}
```

## 3. 使用step()函数

step()函数的使用非常直观：**我们可以用它来定义范围值之间的距离**：

```kotlin
for(i in 1..9 step 2) {
    print(i) // Print 13579
}

for (i in 9 downTo 1 step 2) {
    print(i) // Print 97531
}
```

在此示例中，我们向前和向后迭代1-9的值，步长值为2。

## 4. 使用reversed()函数

顾名思义，reversed()函数将反转范围的顺序：

```kotlin
(1..9).reversed().forEach {
    print(it) // Print 987654321
}

(1..9).reversed().step(3).forEach {
    print(it) // Print 963
}
```

## 5. 使用until()函数

当我们想要创建一个不包括结束元素的范围时，我们可以使用until()：

```kotlin
for (i in 1 until 9) {
    print(i) // Print 12345678
}
```

## 6. last、first、step元素

如果我们需要找到范围的第一个、步长或最后一个值，有一些函数可以将它们返回给我们：

```kotlin
print((1..9).first) // Print 1
print((1..9 step 2).step) // Print 2
print((3..9).reversed().last) // Print 3
```

## 7. 过滤范围

filter()函数将返回与给定谓词匹配的元素列表：

```kotlin
val r = 1..10
val f = r.filter { it -> it % 2 == 0 } // Print [2, 4, 6, 8, 10]
```

我们还可以将其他函数(例如map()和reduce())应用于我们的范围：

```kotlin
val m = r.map { it -> it * it } // Print [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]
val rdc = r.reduce{a, b -> a + b} // Print 55
```

## 8. 其他实用函数

我们还可以将许多其他函数应用于我们的范围，例如min、max、sum、average、count、distinct：

```kotlin
val r = 1..20
print(r.min()) // Print 1
print(r.max()) // Print 20
print(r.sum()) // Print 210
print(r.average()) // Print 10.5
print(r.count()) // Print 20

val repeated = listOf(1, 1, 2, 4, 4, 6, 10)
print(repeated.distinct()) // Print [1, 2, 4, 6, 10]
```

## 9. 自定义对象

**也可以在自定义对象上创建一个范围**。为此，唯一的要求是扩展Comparable接口。

枚举就是一个很好的例子，Kotlin中的所有枚举都扩展了Comparable，这意味着默认情况下，元素按照它们出现的顺序排序。

让我们快速创建一个Color枚举：

```kotlin
enum class Color(val rgb: Int) : Comparable<Color> {
    BLUE(0x0000FF),
    GREEN(0x008000),
    RED(0xFF0000),
    MAGENTA(0xFF00FF),
    YELLOW(0xFFFF00);
}
```

然后在一些if语句中使用它：

```kotlin
val range = red..yellow
if (range.contains(Color.MAGENTA)) println("true") // Print true
if (Color.RED in Color.GREEN..Color.YELLOW) println("true") // Print true
if (Color.RED !in Color.MAGENTA..Color.YELLOW) println("true") // Print true
```

但是，由于这不是一个整型，我们不能迭代它。如果我们尝试这么做，我们会得到一个编译错误：

```kotlin
fun main(args: Array<String>) {
    for (c in Color.BLUE.rangeTo(Color.YELLOW)) println(c) // for-loop range must have an iterator() method
}
```

如果我们确实想要一个可以迭代的自定义范围，我们只需要实现ClosedRange和Iterator。

## 10. 总结

在本文中，我们演示了如何在Kotlin中使用范围表达式以及我们可以应用的不同函数。