## 1. 概述

我们可能需要从字符串中删除字符，我们应该注意，字符串在Kotlin中是不可变的，因此要从字符串中删除一个字符，我们实际上是在创建一个新字符。

在这个简短的教程中，我们将介绍一些在Kotlin中从字符串中删除字符的方法。

## 2. 使用replace

字符串是字符序列，**我们可以使用替换replace函数来删除字符串中的特定字符**，它返回一个新字符串，其中删除了所有出现的旧字符：

```kotlin
val string = "Tu.yucheng"
assertEquals("Tuyucheng", string.replace(".", ""))
```

## 3. 使用过滤

让我们尝试另一个扩展函数，**函数filterNot返回一个字符串，该字符串仅包含那些与谓词不匹配的字符**：

```kotlin
val string = "Tu.yucheng"
assertEquals("Tuyucheng", string.filterNot { it == '.' })
```

## 4. 使用deleteAt

从字符串中删除单个字符的下一种方法是使用StringBuilder。

首先，我们将字符串传递给StringBuilder构造函数；然后，**我们可以使用deleteAt方法，从这个StringBuilder中删除指定索引处的字符**。之后，我们可以使用toString函数获取一个新的String：

```kotlin
val string = "Tu.yucheng"
val stringBuilder = StringBuilder(string)
assertEquals("Tuyucheng", stringBuilder.deleteAt(2).toString())
```

## 5. 使用removeRange

removeRange是另一个有用的扩展函数，它删除给定范围内的字符串部分，此范围是排除性的，因此它不会删除提供的第二个索引处的字符：

```kotlin
val string = "Tu.yucheng"
assertEquals("Tuyucheng", string.removeRange(2, 3))
```

## 6. 使用removeSuffix和removePrefix

在Kotlin，有一些用于操作字符串的便捷函数，**如果我们只想删除字符串的开头，则可以使用removePrefix函数**。如果字符串以给定的前缀开头，则该函数将返回该字符串的副本，并删除前缀。否则，它返回原始字符串：

```kotlin
val string = "Tuyucheng"
assertEquals("uyucheng", string.removePrefix("T"))
assertEquals("Tuyucheng", string.removePrefix("Z"))
```

**为了删除字符串的末尾，我们可以使用removeSuffix**。如果一个字符串以给定的后缀结尾，它返回该字符串的一个副本，并删除后缀：

```kotlin
val string = "Tuyucheng"
assertEquals("Tuyuchen", string.removeSuffix("g"))
assertEquals("Tuyucheng", string.removeSuffix("Z"))
```

## 7. 总结

在本文中，我们了解了在Kotlin中从字符串中删除字符的最常见方法。