## 1. 概述

在本教程中，我们将了解如何使用Kotlin语言在列表中查找元素。此外，我们将按条件过滤此列表。

## 2. 在列表中查找元素

假设我们有一个扮演蝙蝠侠的演员列表：

```kotlin
var batmans: List<String> = listOf("Christian Bale", "Michael Keaton", "Ben Affleck", "George Clooney")
```

假设我们想要找到“第一个蝙蝠侠”(来自蒂姆·伯顿的电影)-迈克尔·基顿。

在Kotlin中，我们可以使用find来返回与给定谓词匹配的第一个元素：

```kotlin
val theFirstBatman = batmans.find { actor -> "Michael Keaton".equals(actor) }
assertEquals(theFirstBatman, "Michael Keaton")
```

但是，如果没有找到这样的元素，则find将返回null。

## 3. 过滤列表中的元素

另一方面，我们要过滤蝙蝠侠列表。

我们只想保留最酷的蝙蝠侠演员。同样，那将是迈克尔·基顿，原版，以及诺兰三部曲中的克里斯蒂安·贝尔。

在这种情况下，让我们使用filter函数和谓词来获取包含最酷蝙蝠侠的列表：

```kotlin
val theCoolestBatmans = batmans.filter { actor -> actor.contains("a") }
assertTrue(theCoolestBatmans.contains("Christian Bale") &&
        theCoolestBatmans.contains("Michael Keaton"))
```

或者，如果没有这样的元素与谓词匹配，我们将得到一个空列表。

## 4. 使用filterNot过滤列表中的元素

此外，我们可以使用filterNot来使用反向过滤器：

```kotlin
val theMehBatmans = batmans.filterNot { actor -> actor.contains("a") }
assertFalse(theMehBatmans.contains("Christian Bale") &&
        theMehBatmans.contains("Michael Keaton"))
assertTrue(theMehBatmans.contains("Ben Affleck") &&
        theMehBatmans.contains("George Clooney"))
```

## 5. 总结

在本教程中，我们了解了使用Kotlin和列表来查找和过滤元素是多么容易。

我们可以在Kotlin的官方[find文档](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/find.html)、[filter文档](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/filter.html)和[filterNot文档](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/filter-not.html)中找到更详细的信息。