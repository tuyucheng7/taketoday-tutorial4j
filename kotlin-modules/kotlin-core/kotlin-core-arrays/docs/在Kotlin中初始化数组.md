## 1. 概述

在本快速教程中，我们将了解如何在Kotlin中初始化数组。

## 2. arrayOf库方法

Kotlin有一个内置的arrayOf方法，可以将提供的枚举值转换为给定类型的数组：

```kotlin
val strings = arrayOf("January", "February", "March")
```

## 3. 原始数组

我们还可以使用带有原始值的arrayOf方法。

然而，Kotlin会将原始值自动装箱到它们相应的对象包装器类中，这将对性能产生不利影响。为了避免这种开销，Kotlin广泛支持原始数组，**以下类型有专用的arrayOf方法：double、float、long、int、char、short、byte、boolean**。

我们可以使用其专用的arrayOf方法轻松初始化原始int数组：

```kotlin
val integers = intArrayOf(1, 2, 3, 4)
```

## 4. 索引的后期初始化

有时我们不想在实例化时定义数组的值。在这种情况下，我们可以创建一个空值数组。

实例化后，我们可以访问和设置数组的字段。有几种方法可以做到这一点，但一种常见的方法是使用Kotlin的indices属性，此属性返回数组的一系列有效索引，我们可以使用范围在for循环中访问和设置数组的值。

让我们使用这种方法用平方数初始化我们的数组：

```kotlin
val array = arrayOfNulls<Number>(5)

for (i in array.indices) {
    array[i] = i * i
}
```

## 5. 使用初始化器生成值

**原始数组和对象数组都有接收初始化函数作为第二个参数的构造函数**，此初始化函数将索引作为输入参数，使用该函数将其转换为适当的值，并将其插入到数组中。

我们可以在一行中用平方数初始化一个数组：

```kotlin
val generatedArray = IntArray(10) { i -> i * i }
```

如前所述，这种构造函数也可用于对象数组：

```kotlin
val generatedStringArray = Array(10) { i -> "Number of index: $i"  }
```

## 6. 总结

在本教程中，我们了解了如何在Kotlin中初始化数组，我们发现了对原始数组的广泛支持，我们还观察了如何使用带有初始化函数的数组构造函数来编写简洁的代码。