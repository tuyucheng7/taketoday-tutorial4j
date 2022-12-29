## 1. 简介

在Kotlin中，可以将Long值转换为Int值。但是，存在一些细微的差别，让我们来看看。

## 2. toInt()辅助函数

在Kotlin中，**Long类有一个有用的辅助函数，称为**[toInt()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/to-int.html)。使用此函数，我们可以将Long值转换为Int值：

```kotlin
val longValue = 100L
val intValue = longValue.toInt()
```

在这里，我们使用toInt函数将Long值转换为Int值，但是，此转换存在局限性。

## 3. 截断问题

正如我们所知，Long是比Int更“大”的类型，这意味着如果我们尝试转换小于[Int.MIN_VALUE](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/-m-i-n_-v-a-l-u-e.html)或大于[Int.MAX_VALUE](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/-m-a-x_-v-a-l-u-e.html)的Long值，我们可能会得到截断的结果。

具体来说，**toInt()函数返回一个Int值，该值由此类转换的给定Long值的最低有效32位表示**。

## 4. 使用扩展函数处理截断

在某些情况下，toInt函数的默认截断行为可能不是可取的。有趣的是，我们可以编写自己的扩展函数来实现所需的行为。例如：

```kotlin
fun Long.toIntOrNull(): Int? {
    return if (this >= Int.MIN_VALUE && this <= Int.MAX_VALUE) {
        toInt()
    } else {
        null
    }
}
```

在此扩展函数中，如果Long值大于[Int.MIN_VALUE](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/-m-i-n_-v-a-l-u-e.html)且小于[Int.MAX_VALUE](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/-m-a-x_-v-a-l-u-e.html)，我们将返回预期的Int值。但是，如果Long值超出该范围，我们将返回null。

这允许调用代码比toInt的默认截断行为更容易识别和处理“缩小”转换。

## 5. 总结

将Long值转换为Int值的一种简单方法是使用toInt()辅助函数，此外，为了实现自定义截断行为，我们还可以编写自己的扩展函数。