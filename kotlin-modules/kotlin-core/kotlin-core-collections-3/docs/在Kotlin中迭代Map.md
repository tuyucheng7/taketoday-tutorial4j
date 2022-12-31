## 1. 概述

在这个简短的教程中，我们将了解如何在Kotlin中迭代Map，**我们将看到如何使用for循环、迭代器和一些方便的扩展函数来做到这一点**。

## 2. 用for循环迭代

迭代Map的最简单方法是**迭代它的**[Entry](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/-entry/)**对象**：

```kotlin
val map = mapOf("Key1" to "Value1", "Key2" to "Value2", "Key3" to "Value3")
map.forEach { entry ->
    print("${entry.key} : ${entry.value}")
}
```

在这里，**in运算符为我们提供了Map条目属性的迭代器句柄**，这与以下内容基本相同：

```kotlin
for (entry in map.entries.iterator()) {
    print("${entry.key} : ${entry.value}")
}
```

## 3. 使用forEach 

我们还可以使用方便的forEach扩展函数：

```kotlin
map.forEach { entry ->
    print("${entry.key} : ${entry.value}")
}
```

为了进一步减少样板文件，我们可以使用[解构](https://kotlinlang.org/docs/destructuring-declarations.html)，这样做会将[Entry](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/-entry/)扩展为方便的键和值变量：

```kotlin
map.forEach { (key, value) ->
    print("$key : $value")
}
```

## 4. 使用Map.keys属性

有时，我们只想遍历Map中的键。在这种情况下，我们可以使用Map的keys 属性：

```kotlin
for (key in map.keys) {
    print(key)
}
```

在这里，**我们得到了Map中所有键的**[集合](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/)，这也意味着我们可以使用Set的forEach扩展函数：

```kotlin
map.keys.forEach { key ->
    val value = map[key]
    print("$key : $value")
}
```

**我们应该注意到，以这种方式迭代键和值的效率较低**。这是因为我们在Map上进行了多次get调用以获取给定键的值。例如，这可以通过使用[Entry](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/-entry/)迭代方法来避免。

## 5. 使用map.values属性

同样，我们可以使用Map的values属性来获取所有值的集合：

```kotlin
for (value in map.values) {
    print(value)
}
```

由于**values属性为我们提供了一个集合**，因此我们可以在此处使用forEach模式：

```kotlin
map.values.forEach { value ->
    print(value)
}
```

## 6. 总结

在本文中，我们看到了几种遍历Map的方法。有些方法更灵活，但会带来性能成本。

对于大多数用例，在条目上使用forEach是一个不错的选择。