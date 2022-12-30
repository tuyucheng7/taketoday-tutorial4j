## 1. 简介

字符串是Kotlin中的基本变量类型之一，对普通字符串进行操作是一个非常常见的用例。在本教程中，我们将学习如何通过索引从字符串中轻松获取字符。

## 2. 使用索引

字符串是字符序列，**我们可以使用索引操作从字符串中获取特定字符**，索引从零开始，就像在Java中一样，因此第一个字符的索引为零。为了表示一个特定的字符，我们应该把这个字符的索引放在方括号之间：

```kotlin
val string = "Tuyucheng"
assertEquals('u', string[3])
```

## 3. 使用get() 

**String类有一个函数get()，它返回指定索引处给定字符串的字符**。就像前面的例子一样，索引从零开始：

```kotlin
val string = "Tuyucheng"
assertEquals('u', string.get(3))
```

与在Java中一样，如果指定索引超出给定字符串的范围，则get()方法会抛出StringIndexOutOfBoundsException。

## 4. 使用first()、last()和single()

在Kotlin中，几乎没有用于操作字符串的便捷函数，**如果我们只想检索字符串的第一个字母，我们可以使用first()函数**：

```kotlin
val string = "Tuyucheng"
assertEquals('T', string.first())
```

以类似的方式，**我们可以使用last()函数来检索字符串的最后一个字符**：

```kotlin
val string = "Tuyucheng"
assertEquals('g', string.last())
```

另一个有用的函数是single()，**single()函数返回序列的单个字符，如果字符序列为空或包含多个字符，则抛出异常**：

```kotlin
val string = "A"
assertEquals('A', string.single())
```

## 5. 使用字符数组

**从字符串中获取单个字符的下一种方法涉及首先使用内置函数toCharArray()将其转换为CharArray**，然后，我们可以通过数组的索引访问字符，让我们看一个例子：

```kotlin
val string = "Tuyucheng"
val toCharArray = string.toCharArray()
assertEquals('u', toCharArray[3])
```

## 6. 使用subSequence()

**从字符串中检索单个字符的另一种方法是使用字符串本身的子序列**。首先，我们从字符串中获取一个子字符串，然后将其转换为单个字符。让我们看看它的实际效果：

```kotlin
val string = "Tuyucheng"
val substring = string.subSequence(3, 4).single()
assertEquals('u', substring)
```

## 7. 总结

在本文中，我们了解了在Kotlin中从字符串中获取字符的最常见方法，使用Kotlin的内置函数可以让我们的代码更简洁并遵循语言的约定。