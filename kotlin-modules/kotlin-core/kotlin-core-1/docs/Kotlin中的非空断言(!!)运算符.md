## 1. 概述

在Kotlin中，可空性是一种类型系统特性，可以帮助我们避免NullPointerException。

在本快速教程中，我们将介绍非空断言(!!)运算符并了解我们应该如何以及何时使用它。

## 2. 非空断言

**Kotlin中的每种类型都有一种可为空的形式和另一种不可为空的形式**。例如，Int类型可以容纳非空的32位整数，除了相同的值外，Int?类型也可以包含空值。

Kotlin提供了许多选项来促进[以安全的方式处理可为空的数据类型](https://www.baeldung.com/kotlin-null-safety)。

但是，**有时我们需要强制将可空类型转换为其不可空的形式**。事实证明，Kotlin的非空断言运算符!!就是这样做的：

```kotlin
val answer: String? = "42"
assertEquals(42, answer!!.toInt())
```

因为我们确定answer不为空，所以我们可以使用!!运算符。否则，我们无法调用toInt方法。

**如果我们将此运算符应用于空值，Kotlin将抛出一个java.lang.NullPointerException的实例**：

```kotlin
@Test
fun givenNullableValue_WhenIsNull_ThenShouldThrow() {
    assertFailsWith<NullPointerException> {
        val noAnswer: String? = null
            noAnswer!!
    }
}
```

另外，如果我们对不可为null的类型应用!!，编译器仍将编译代码：

```kotlin
val answer = "42"
answer!!.toInt()
```

但是它会抱怨，并给我们一个警告信息：

```shell
Unnecessary non-null assertion (!!) on a non-null receiver of type String
```

## 3. 使用案例

**大多数时候，我们不应该在纯Kotlin代码中应用!!运算符**，最好使用其他更安全的选项，例如空安全调用或[Elvis运算符](https://www.baeldung.com/kotlin-null-safety#elvis-operator)。

但是，在使用某些第三方库时，我们可能无论如何都必须使用此运算符。例如，假设我们在Spring DTO上使用bean验证注解：

```kotlin
data class NewUserDto(
    @get:NotNull
    val username: String? = null
)
```

Spring会自动将验证规则应用于请求数据，因此，验证完成后，我们确定username不为null：

```kotlin
val request: NewUserDto = // from controller
userService.registerUser(request.username!!)
```

即使我们确实从上下文中知道该值不为空，但Kotlin编译器也无法推断出此信息。所以，我们必须在这里使用!!运算符。

## 4. 总结

在这个快速教程中，我们熟悉了Kotlin中可空性的另一个方面：非空断言运算符(!!)。