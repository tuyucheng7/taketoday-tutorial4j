## 1. 概述

在本文中，我们将讨论**Kotlin中“==”和“===”运算符之间的区别**。

在Kotlin中，就像在Java中一样，我们有两个不同的相等概念，Referential equality(引用相等)和Structural equality(结构相等)。

## 2. 引用相等

对于引用相等，我们使用===符号，它允许我们评估一个对象的引用(如果它指向同一个对象)，这相当于Java中的“==”运算符。

假设我们定义了两个整数：

```kotlin
val a = Integer(10)
val b = Integer(10)
```

我们通过执行a === b来检查它们，这将返回false，因为它们是两个独立的对象，每个对象都指向内存中的不同位置。

## 3. 结构相等

现在对于结构相等，我们使用==符号来评估两个值是否相同(或相等)，这通常是通过在Java中实现equals()方法来实现的。

因此，使用相同的整数示例，我们只需要执行a == b，在这种情况下，它将返回true，因为两个变量具有相同的值。

## 4. 比较复杂的对象

如果我们想在更复杂的对象上检查相等性，符号的行为将相同。假设我们有一个User，它有一个hobbies集合：

```kotlin
data class User(val name: String, val age: Int, val hobbies: List<String>)
```

===将检查引用相等性，并且通过方便地使用List<>我们可以利用==运算符，它将**检查list中包含的对象和数据**。

## 5. 数组相等

对于数组，从Kotlin 1.1开始，我们可以使用中缀函数[contentEquals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/content-equals.html)和[contentDeepEquals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/content-deep-equals.html)检查数组结构相等性：

```kotlin
val hobbies = arrayOf("Hiking, Chess")
val hobbies2 = arrayOf("Hiking, Chess")

assertTrue(hobbies contentEquals hobbies2)
```

## 6. 总结

这个快速教程通过一个非常简单的示例演示了Kotlin中引用相等性和结构相等性之间的区别。