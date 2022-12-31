## 1. 概述

在本教程中，我们将了解一些在Kotlin中初始化[Map](https://www.baeldung.com/kotlin/maps)的方法。首先，**我们将介绍mapOf和mutableMapOf等方法**。

然后，我们将查看一些函数来创建更具体的Map实现。

## 2. 使用mapOf

初始化Map最常见的方法是使用[mapOf](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/map-of.html)工厂方法，**使用mapOf，我们可以创建一个带有一些初始条目的不可变**[Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/)：

```kotlin
val mapWithValues = mapOf("Key1" to "Value1", "Key2" to "Value2", "Key3" to "Value3")
```

请注意，我们不需要在此处明确指定类型。相反，当我们创建一个空Map时情况并非如此：

```kotlin
val mapWithoutValues = mapOf<String, String>()
```

这将创建一个指定类型的空不可变映射，这与使用[emptyMap](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/empty-map.html)相同：

```kotlin
val emptyMap = emptyMap<String, String>()
```

## 3. 使用mutableMapOf

同样，我们可以使用[mutableMapOf](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/mutable-map-of.html)工厂方法来创建一个[MutableMap](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-mutable-map/)：

```kotlin
val emptyMutableMap = mutableMapOf<String, String>()
emptyMutableMap["Key"] = "Value"
```

或者，我们可以用一些值初始化Map，并且仍然能够在以后添加/修改Map：

```kotlin
val mutableMap = mutableMapOf("Key1" to "Value1", "Key2" to "Value2", "Key3" to "Value3")
mutableMap["Key3"] = "Value10" // modify value
mutableMap["Key4"] = "Value4" // add entry
mutableMap.remove("Key1") // delete existing value
```

## 4. 使用hashMapOf

使用[hashMapOf](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/hash-map-of.html)函数，我们可以创建一个[HashMap](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-hash-map/)：

```kotlin
val hashMap = hashMapOf("Key1" to "Value1", "Key2" to "Value2", "Key3" to "Value3")
```

众所周知，HashMap是可变的，因此，**我们可以在创建后添加/修改其条目**：

```kotlin
hashMap["Key3"] = "Value10" // modify value
hashMap["Key4"] = "Value4" // add entry
hashMap.remove("Key1") // delete existing value
```

## 5. 使用sortedMapOf和linkedMapOf

在Kotlin中，我们还可以访问创建其他特定Map的函数：

```kotlin
val sortedMap = sortedMapOf("Key3" to "Value3", "Key1" to "Value1", "Key2" to "Value2")
val linkedMap = linkedMapOf("Key3" to "Value3", "Key1" to "Value1", "Key2" to "Value2")
```

顾名思义，sortedMapOf创建一个可变的SortedMap；同样，linkedMapOf函数创建一个LinkedMap对象。

## 6. 总结

Kotlin有多种函数来创建Map，**最常用的是mapOf和mutableMapOf**。

另一方面，一些用例(例如遗留代码库)可能需要特定的Map，对于这些情况，我们还可以访问更具体的工厂功能。