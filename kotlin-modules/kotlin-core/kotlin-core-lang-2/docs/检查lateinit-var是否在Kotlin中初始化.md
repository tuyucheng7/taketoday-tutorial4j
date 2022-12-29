## 1. 概述

在Kotlin中，有几种不同的方法来[惰性初始化](https://www.baeldung.com/kotlin-lazy-initialization)甚至[延迟初始化](https://www.baeldung.com/kotlin-lazy-initialization#kotlins-lateinit)变量和属性。

在本快速教程中，我们将学习检查lateinit变量的初始化状态。

## 2. 延迟初始化

使用lateinit变量，我们可以推迟变量的初始化，这在使用依赖注入或测试框架时特别有用。

尽管很有用，但在使用它们时有一个很大的警告：**如果我们访问一个未初始化的lateinit变量，Kotlin会抛出一个异常**：

```kotlin
private lateinit var answer: String

@Test(expected = UninitializedPropertyAccessException::class)
fun givenLateInit_WhenNotInitialized_ShouldThrowAnException() {
    answer.length
}
```

UninitializedPropertyAccessException异常表示底层变量尚未初始化的事实。

为了检查初始化状态，我们可以在[属性引用](https://www.baeldung.com/kotlin-reflection#3-kotlin-property-references)上使用[isInitialized](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/is-initialized.html)方法：

```kotlin
assertFalse { this::answer.isInitialized }
```

在这里，使用::语法，我们获得了对该属性的引用，当我们为变量赋值时，这显然会返回true：

```kotlin
answer = "42"
assertTrue { this::answer.isInitialized }
```

请注意，此功能仅适用于[Kotlin 1.2](https://kotlinlang.org/docs/reference/properties.html#checking-whether-a-lateinit-var-is-initialized-since-12)或更新版本。

## 3. 总结

在本教程中，我们了解了如何检查lateinit变量的初始化状态。