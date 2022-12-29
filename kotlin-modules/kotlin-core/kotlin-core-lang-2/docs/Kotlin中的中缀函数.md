## 1. 简介

Kotlin是一种添加了许多新功能以允许编写更清晰、更易于阅读的代码的语言。

反过来，这使我们的代码更容易维护，并允许我们的开发获得更好的最终结果，[中缀表示法](https://kotlinlang.org/docs/reference/functions.html#infix-notation)就是此类功能之一。

## 2. 什么是中缀表示法？

Kotlin允许在不使用句点和括号的情况下调用某些函数，**这些方法称为中缀方法，使用它们可以使代码看起来更像自然语言**。

这在内联Map定义中最常见：

```kotlin
map(
    1 to "one",
    2 to "two",
    3 to "three"
)
```

“to”可能看起来像一个特殊的关键字，但在此示例中，这是一个利用中缀表示法并返回Pair<A, B>的to()方法。

## 3. 通用标准库中缀函数

除了用于创建Pair<A, B>实例的to()函数之外，还有一些其他函数被定义为中缀。

例如，各种数字类Byte、Short、Int和Long都定义了按位函数and()、or()、shl()、shr()、ushr()和xor()，允许一些更易读的表达式：

```kotlin
val color = 0x123456
val red = (color and 0xff0000) shr 16
val green = (color and 0x00ff00) shr 8
val blue = (color and 0x0000ff) shr 0
```

Boolean类以类似的方式定义and()、or()和xor()逻辑函数：

```kotlin
if ((targetUser.isEnabled and !targetUser.isBlocked) or currentUser.admin) {
    // Do something if the current user is an Admin, or the target user is active
}
```

String类还将match和zip函数定义为中缀，允许一些易于阅读的代码：

```kotlin
"Hello, World" matches "^Hello".toRegex()
```

在整个标准库中还可以找到一些其他示例，但这些示例可能是最常见的。

## 4. 编写自定义中缀方法

通常，我们会想要编写自己的中缀方法。**这些可能特别有用，例如，在为我们的应用程序编写领域特定语言时，允许DSL代码更具可读性**。

一些Kotlin库已经使用它取得了很好的效果。

**例如，mockito-kotlin库定义了一些中缀函数doAnswer、doReturn和doThrow用于定义Mock行为**。

**编写中缀函数是遵循以下三个规则的简单情况**：

1.  该函数要么在类上定义，要么是类的扩展方法
2.  该函数只接收一个参数
3.  该函数是使用infix关键字定义的

作为一个简单的例子，让我们定义一个简单的断言框架用于测试，我们将允许使用中缀函数从左到右很好地阅读表达式：

```kotlin
class Assertion<T>(private val target: T) {
    infix fun isEqualTo(other: T) {
        Assert.assertEquals(other, target)
    }

    infix fun isDifferentFrom(other: T) {
        Assert.assertNotEquals(other, target)
    }
}
```

**这看起来很简单，似乎与任何其他Kotlin代码没有任何不同**。但是，infix关键字的存在使我们可以编写这样的代码：

```kotlin
val result = Assertion(5)

result isEqualTo 5 // This passes
result isEqualTo 6 // This fails the assertion
result isDifferentFrom 5 // This also fails the assertion
```

立即，这阅读起来更清晰，更容易理解。

**请注意，中缀函数也可以编写为现有类的扩展方法**。这可能很强大，因为它允许我们扩充来自其他地方(包括标准库)的现有类以满足我们的需要。

例如，让我们向String添加一个函数，以提取与给定正则表达式匹配的所有子字符串：

```kotlin
infix fun String.substringMatches(r: Regex) : List<String> {
    return r.findAll(this)
        .map { it.value }
        .toList()
}

val matches = "a bc def" substringMatches ".*? ".toRegex()
Assert.assertEquals(listOf("a ", "bc "), matches)
```

## 5. 总结

这个快速教程演示了一些可以使用中缀函数完成的事情，包括如何利用一些现有的中缀函数以及如何创建我们自己的中缀函数以使我们的代码更清晰、更易于阅读。