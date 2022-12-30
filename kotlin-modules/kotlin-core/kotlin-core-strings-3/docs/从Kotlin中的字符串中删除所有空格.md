## 1. 概述

从字符串中删除空格是软件开发中的一项常见任务，许多编程语言都有强大的内置支持来帮助解决这个问题。

在本教程中，我们将看到一些使用Kotlin从字符串中删除空格的技术。

## 2. replace方法

**使用原生方法replace()方法，我们可以删除与空字符串匹配的任何字符**：

```kotlin
val example = "House Of The Dragon"
val withOutSpaces = example.replace(" ", "")

assertThat(withOutSpaces).isEqualTo("HouseOfTheDragon")
```

我们还可以定义一个正则表达式来传递给replace方法：

```kotlin
val example = "House Of The Dragon"
val withOutSpaces = example.replace("\\s".toRegex(), "")

assertThat(withOutSpaces).isEqualTo("HouseOfTheDragon")
```

有时，空白字符可能是非标准的，**有几个Unicode字符作为空格出现，其他更简单的替换机制可能无法很好地处理这些字符**。为了解决这个问题，我们可以定义一个匹配这些Unicode空白字符的正则表达式：

```kotlin
val example = "House Of The Dragon"
val withOutSpaces = example.replace("\\p{Zs}+".toRegex(), "")

assertThat(withOutSpaces).isEqualTo("HouseOfTheDragon")
```

## 3. 字符过滤

我们还可以利用对字符串的过滤支持来测试单个字符，**有一个内置函数isWhitespace()可以确定Char是否为空白字符**，我们可以把这些放在一起解决问题：

```kotlin
val example = "House Of The Dragon"
val withOutSpaces = example.filterNot { it.isWhitespace() }

assertThat(withOutSpaces).isEqualTo("HouseOfTheDragon")
```

## 4. trim方法的变体

有时，我们只需要删除字符串开头或结尾的空格，trim()方法及其变体可以帮助我们完成这个任务：

```kotlin
val example = " House Of The Dragon "
val trimmed = example.trim()

assertThat(trimmed).isEqualTo("House Of The Dragon")
```

同样，我们可以只删除字符串开头或结尾的空格：

```kotlin
val example = "  House Of The Dragon  "
val trimmedAtStart = example.trimStart()
val trimmedAtEnd = example.trimEnd()

assertThat(trimmedAtStart).isEqualTo("House Of The Dragon  ")
assertThat(trimmedAtEnd).isEqualTo("  House Of The Dragon")
```

## 5. 总结

在本教程中，我们探讨了一些从字符串中删除所有空白字符的技术，我们还看到了如何使用trim()函数的变体删除字符串开头或结尾的空格。