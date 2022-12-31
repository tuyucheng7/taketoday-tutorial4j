## 1. 概述

在本快速教程中，我们将学习如何创建包含重复元素的集合。

## 2. 创建重复集合

**从Kotlin 1.1开始，我们可以使用**[List(size: Int, init: (index: Int) -> T)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list.html)**函数来创建具有指定大小的只读列表**。此外，我们可以使用给定的init函数初始化每个元素，该函数将元素位置作为输入：

```kotlin
val repeated = List(3) { index -> "Hello" }
assertThat(repeated).hasSize(3)
assertThat(repeated.toSet()).containsExactly("Hello")
```

在这里，我们创建了一个包含三个元素的列表，都等于“hello”，由于我们不使用lambda参数，我们也可以将其替换为下划线字符：

```kotlin
val repeated = List(3) { _ -> "Hello" }
```

更简洁的是，我们甚至可以去掉下划线：

```kotlin
val repeated = List(3) { "Hello" }
```

这样，我们默认使用“it” lambda参数名称，但我们从不使用它。同样，我们可以对其他类型的集合使用相同的方法，例如MutableList或不同类型的数组：

```kotlin
val mutable = MutableList(3) { "Hello" }
assertThat(mutable.toSet()).containsExactly("Hello")

val charArray = CharArray(3) { 'H' }
assertThat(charArray.toSet()).containsExactly('H')

val array = Array(3) { "Hello" }
assertThat(array.toSet()).containsExactly("Hello")

val intArray = IntArray(3) { 42 }
assertThat(intArray.toSet()).containsExactly(42)
```

除此之外，**我们可以使用**[generateSequence(nextFunction: () -> T?)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/generate-sequence.html)**函数创建一个重复相同元素的序列**：

```kotlin
val repeated = generateSequence { "Hello" }.take(3).toList()
assertThat(repeated).hasSize(3)
assertThat(repeated.toSet()).containsExactly("Hello")
```

在这里，我们获取无限序列的前三个元素并将生成的有限序列转换为列表。

## 3. 总结

在本教程中，我们了解了如何创建不同的集合，例如List、MutableList甚至Array，其中包含重复元素。