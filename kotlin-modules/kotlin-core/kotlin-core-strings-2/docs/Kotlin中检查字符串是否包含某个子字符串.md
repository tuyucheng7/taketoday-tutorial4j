## 1. 概述

软件开发中最常见的任务之一是检查字符串中的子字符串，例如，我们可能希望在大型文本文件中搜索一些字符串或替换一些子字符串。

在本教程中，我们将看到一些在Kotlin中检查字符串是否包含子字符串的有效方法。

## 2. 实现

**Kotlin中有内置函数来检查子字符串和一些有价值的操作来模拟预期的功能**，让我们从原生Kotlin支持开始。

### 2.1 原生contains方法

首先，使用核心Kotlin支持，**我们可以使用contains方法，该方法返回true或false，具体取决于子字符串是否包含在调用的字符串对象中**：

```kotlin
@Test
fun `given a string when search for contained substrings then should return true`() {
    val string = "Kotlin is a programming language"
    val substring = "programming"

    val contains = string.contains(substring)
    assertThat(contains).isTrue
}
```

或者，**我们可以通过使用in运算符搜索子字符串来获得相同的结果**：

```kotlin
@Test
fun `given a string when search for substrings in the string then should return true`() {
    val string = "Kotlin is a programming language"
    val substring = "programming"

    val contains = substring in string
    assertThat(contains).isTrue
}
```

在某些情况下，我们可能希望搜索忽略字符大小写的子字符串。为此，**contains方法可以接收一个标志ignoreCase来指示我们想要以这种方式搜索子字符串**：

```kotlin
@Test
fun givenString_whenSearchSubstringsIgnoringCase_thenReturnsTrue() {
    val string = "Kotlin is a programming language"
    val substring = "Programming Language"

    val contains = string.contains(substring, true)
    assertThat(contains).isTrue
}
```

### 2.2 正则表达式

另一种检查子字符串的方法是定义正则表达式来搜索字符串中的匹配模式：

```kotlin
@Test
fun givenString_whenSearchSubstringsUsingRegex_thenReturnsTrue() {
    val string = "Kotlin is a programming language"
    val substring = "programming language"
    val regex = substring.toRegex()

    val contains = regex.containsMatchIn(string)
    assertThat(contains).isTrue
}
```

### 2.3 子串索引

Kotlin支持搜索和检索给定子字符串的索引，**如果在主字符串中找到子字符串，则lastIndexOf()方法将返回一个非负数**：

```kotlin
@Test
fun givenString_whenSearchSubstringsByIndexOf_thenReturnsNonNegative() {
    val string = "Kotlin is a programming language"
    val substring = "programming"
    val index = string.lastIndexOf(substring)

    assertThat(index).isNotNegative
}
```

### 2.4 寻找子串

**使用findLastAnyOf()方法，我们可以找到给定子字符串的出现次数**，该方法将返回一个Pair对象，其中填充了子字符串和给定子字符串的最后一次出现的索引，如果没有子字符串匹配，则返回null：

```kotlin
@Test
fun givenString_whenSearchSubstringsWithFindLast_thenReturnsNotNullPair() {
    val string = "Kotlin is a programming language"
    val substring = "programming language"
    val found = string.findLastAnyOf(listOf(substring))

    assertThat(found).isNotNull
}
```

## 3. 总结

在本教程中，我们探索了一些检查其他字符串中的子字符串的有效技术。Kotlin公开了原生支持和一些方便的方法来实现这一目标。