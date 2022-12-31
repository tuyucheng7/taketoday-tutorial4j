## 1. 概述

在本教程中，我们将学习如何[在Kotlin中对数组](https://www.baeldung.com/kotlin/arrays)进行相等性检查。

## 2. 相等

Kotlin中有两种相等-即引用相等和结构相等，此外，**我们可以使用===运算符检查引用相等性，使用==运算符检查结构相等性**。

让我们用相同的值定义两个名为first和second的数组，然后定义指向与第一个数组相同的对象的第三个数组：

```kotlin
val first = arrayOf("java", "python", "kotlin")
val second = arrayOf("java", "python", "kotlin")
val third = first
```

现在，让我们检查这些数组之间的引用相等性：

```kotlin
Assertions.assertTrue(first === third)
Assertions.assertFalse(first === second)
Assertions.assertFalse(second === third)
```

接下来，让我们检查这些数组之间的结构是否相等：

```kotlin
Assertions.assertTrue(first == third)
Assertions.assertFalse(first == second)
Assertions.assertFalse(second == third)
```

首先，我们必须注意到，尽管第一个和第二个数组在结构上是相同的，**但==运算符无法证明在结构上检查它们的预期行为**。

在以下部分中，我们将探讨如何在Kotlin中对数组进行结构相等性检查。

## 3. contentEquals函数

contentEquals函数是Kotlin中的一个[中缀函数](https://www.baeldung.com/kotlin/infix-functions)，可用于对数组进行结构比较。

让我们使用与之前相同的数组，即first、second和third，并查看contentEquals函数的作用：

```kotlin
val first = arrayOf("java", "python", "kotlin")
val second = arrayOf("java", "python", "kotlin")
val third = first

Assertions.assertTrue(first contentEquals third)
Assertions.assertTrue(first contentEquals second)
Assertions.assertTrue(second contentEquals third)
```

正如预期的那样，我们可以看到使用contentEquals函数，我们可以断言所有三个数组在结构上都是相同的。

接下来，让我们来看一个我们有嵌套数组的场景-r1、r2和r3：

```kotlin
val r1 = arrayOf(arrayOf("R1", "R2"), arrayOf("R3", "R4"))
val r2 = arrayOf(arrayOf("R1", "R2"), arrayOf("R3", "R4"))
val r3 = r1
```

现在，我们将看到**contentEquals函数存在局限性，因为它没有对这些嵌套数组进行全面的结构比较**：

```kotlin
Assertions.assertTrue(r1 contentEquals r3)
Assertions.assertFalse(r1 contentEquals r2)
Assertions.assertFalse(r2 contentEquals r3)
```

## 4. contentDeepEquals函数

Kotlin提供了contentDeepEquals中缀函数来解决contentEquals函数在嵌套数组情况下的结构相等比较限制。

让我们使用contentDeepEquals函数比较同一组嵌套数组：

```kotlin
val r1 = arrayOf(arrayOf("R1", "R2"), arrayOf("R3", "R4"))
val r2 = arrayOf(arrayOf("R1", "R2"), arrayOf("R3", "R4"))
val r3 = r1

Assertions.assertTrue(r1 contentDeepEquals r3)
Assertions.assertTrue(r1 contentDeepEquals r2)
Assertions.assertTrue(r2 contentDeepEquals r3)
```

正如预期的那样，我们可以看到三个嵌套数组的结构相等性得到确认。

## 5. 用户自定义对象数组

到目前为止，我们已经看到了[String值的数组比较](https://www.baeldung.com/kotlin/string-comparison)，现在，让我们继续扩展我们的理解，比较包含用户定义对象的数组。

让我们定义一个Person[类](https://www.baeldung.com/kotlin/intro#classes)并初始化两个包含Person类实例的数组：

```kotlin
class Person (var name: String?, var age: Int?)
val first = arrayOf(Person("John", 20), Person("Mary", 15))
val second = arrayOf(Person("John", 20), Person("Mary", 15))
```

对于这种情况，**contentEquals函数将无法对数组进行结构比较**：

```kotlin
Assertions.assertFalse(first contentEquals second)
```

那是**因为我们没有在Person类中定义equals()方法**，因此，默认情况下，contentEquals函数执行引用相等性检查。

我们可以通过将Person类定义为[数据类](https://www.baeldung.com/kotlin/data-classes)来解决这个问题：

```kotlin
data class Person (var name: String?, var age: Int?)
val first = arrayOf(Person("John", 20), Person("Mary", 15))
val second = arrayOf(Person("John", 20), Person("Mary", 15))

Assertions.assertTrue(first contentEquals second)
```

由于数据类固有地定义了用于值比较的equals()方法，我们可以断言包含Person类型对象的第一个和第二个数组在结构上是相等的。

此外，我们还可以使用contentDeepEquals函数验证包含Person类型对象的嵌套数组的结构相等性：

```kotlin
data class Person (var name: String?, var age: Int?)
val p1 = arrayOf(arrayOf(Person("John", 20), Person("Mary", 15)))
val p2 = arrayOf(arrayOf(Person("John", 20), Person("Mary", 15)))
Assertions.assertTrue(p1 contentDeepEquals p2)
```

## 6. 总结

在本文中，我们了解了如何在Kotlin中对一维和嵌套数组进行相等性检查。