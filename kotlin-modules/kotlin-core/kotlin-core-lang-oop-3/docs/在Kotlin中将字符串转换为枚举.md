## 1. 概述

Kotlin中的[枚举](https://www.baeldung.com/kotlin/enum)使处理常量类型安全、不易出错且自我记录。

在本教程中，我们将探讨如何将字符串转换为枚举对象。

## 2. 问题简介

像往常一样，让我们通过一个例子来理解这个问题，假设我们有一个枚举类TuyuchengNumber：

```kotlin
enum class TuyuchengNumber {
    ONE, TWO, THREE, FOUR, FIVE, UNKNOWN,
}
```

假设给定了一个字符串，例如“TWO”，我们希望得到枚举对象TuyuchengNumber.TWO作为结果。换句话说，**我们要检查枚举对象的名称并返回名称与给定字符串匹配的实例**。

该要求可以有一些变体，例如我们是否希望字符串检查不区分大小写，如果没有找到枚举实例，我们将返回什么结果，等等。

接下来，我们将讨论将字符串转换为枚举实例的几种方法，并讨论这些需求变体。

为简单起见，我们将使用单元测试断言来验证我们的解决方案是否按预期工作。

## 3. 使用枚举类的valueOf()函数

在Kotlin中，每个枚举类都有一个[valueOf()](https://kotlinlang.org/docs/enum-classes.html#working-with-enum-constants)函数，valueOf()函数正是我们正在寻找的，因为它允许我们**通过名称获取枚举常量**。

例如，此测试通过：

```kotlin
assertEquals(ONE, TuyuchengNumber.valueOf("ONE"))
```

正如我们所看到的，使用valueOf()函数非常简单。但是，当我们使用该函数通过名称获取枚举的常量时，我们需要注意以下几点：

-   要查找枚举常量，**给定的字符串必须与枚举常量名称完全匹配**。
-   如果找不到常量，该函数将抛出IllegalArgumentException。

我们可以通过一个简单的测试来验证它们：

```kotlin
assertThrows<IllegalArgumentException> {
    TuyuchengNumber.valueOf("one")
}
```

## 4. 使用enumValueOf()函数

[enumValueOf()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/enum-value-of.html)函数与valueOf()函数非常相似，唯一的区别是**使用enumValueOf() ，我们可以以**[通用](https://www.baeldung.com/kotlin/generics)**方式访问枚举类中的常量**。

接下来，让我们写一个测试，看看如何调用该函数：

```kotlin
val theOne = enumValueOf<TuyuchengNumber>("ONE")
assertEquals(ONE, theOne)

assertThrows<IllegalArgumentException> {
    enumValueOf<TuyuchengNumber>("one")
}
```

如果我们运行它，测试就会通过。因此，与valueOf()函数一样，**enumValueOf()对常量名称进行精确匹配，如果未找到匹配项，也会引发IllegalArgumentException**。

## 5. 检查所有常量

valueOf()和enumValueOf()方法都易于使用，但是他们会进行精确的字符串匹配，并在找不到匹配项时抛出异常。有时，这并不方便；例如，如果我们想不区分大小写地按名称查找常量，或者如果找不到匹配项则获取空值。

解决该问题的另一个想法是遍历枚举实例并根据我们要求的规则检查它们的名称，例如不区分大小写。

Enum的[values()](https://www.baeldung.com/kotlin/enum#2-iterating-through-enum-constants)函数返回数组中的所有常量，因此，我们可以检查返回数组中的每个常量，直到找到预期的常量：

```kotlin
val theOne = TuyuchengNumber.values().firstOrNull { it.name.equals("one", true) }
assertEquals(ONE, theOne)

val theTwo = TuyuchengNumber.values().firstOrNull { it.name.equals("TWO", true) }
assertEquals(TWO, theTwo)

val theNull = TuyuchengNumber.values().firstOrNull { it.name.equals("whatever", true) }
assertNull(theNull)
```

如上面的代码所示，**firstOrNull()函数负责查找匹配的常量**，如果未找到匹配项，则返回空值。

此外，我们将自己的匹配逻辑(不区分大小写的相等检查)用作firstOrNull()中的预测函数。如果我们运行，测试就会通过。

在实践中，我们可能希望在枚举的[伴生对象](https://www.baeldung.com/kotlin/companion-object)块中创建一个函数：

```kotlin
enum class TuyuchengNumber {
    ONE, TWO, THREE ...

    companion object {
        fun byNameIgnoreCaseOrNull(input: String): TuyuchengNumber? {
            return values().firstOrNull { it.name.equals(input, true) }
        }
    }
}
```

然后我们可以调用它，因为它是一个[“静态”函数](https://www.baeldung.com/kotlin/enum-static-method)：

```kotlin
assertEquals(ONE, TuyuchengNumber.byNameIgnoreCaseOrNull("one"))
assertNull(TuyuchengNumber.byNameIgnoreCaseOrNull("whatever"))
```

## 6. 默认常量支持

我们的byNameIgnoreCaseOrNull()函数支持不区分大小写的匹配并返回空值，而不是在找不到常量的情况下抛出异常。但是，有时我们希望为未找到匹配的情况设置一个默认常量，例如TuyuchengNumber.UNKNOWN常量。此外，如果此函数是通用的，可以在不更改枚举的情况下适用于所有枚举类，那就太好了。

接下来，让我们创建一个函数来实现这一点：

```kotlin
inline fun <reified T : Enum<T>> enumByNameIgnoreCase(input: String, default: T? = null): T? {
    return enumValues<T>().firstOrNull { it.name.equals(input, true) } ?: default
}
```

如上面的函数所示，我们为enumByNameIgnoreCase()函数引入了一个新的可为空的default参数。此外，我们为参数分配了一个[默认值](https://www.baeldung.com/kotlin/default-named-arguments#default-arguments)null。

此外，**我们使用标准**[具体化的enumValues](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/enum-values.html)[函数](https://www.baeldung.com/kotlin/reified-functions)**来获取Enum<T\>的所有实例**。

接下来，让我们创建一个测试来验证函数是否按预期工作：

```kotlin
val theOne = enumByNameIgnoreCase<TuyuchengNumber>("ONE")
assertEquals(ONE, theOne)

val theTwo = enumByNameIgnoreCase<TuyuchengNumber>("two")
assertEquals(TWO, theTwo)

val theNull = enumByNameIgnoreCase<TuyuchengNumber>("whatever")
assertNull(theNull)

val theDefault = enumByNameIgnoreCase("whatever", UNKNOWN)
assertEquals(UNKNOWN, theDefault)
```

当我们运行测试时，它通过了。因此，enumByNameIgnoreCase()函数非常灵活，它可以不区分大小写地按名称查找常量。

以下是它在未找到匹配的情况下的行为方式：

-   如果我们没有定义默认值，该函数将返回null。
-   如果我们传递一个默认参数，例如UNKNOWN，将返回默认值。

## 7. 总结

在本文中，我们学习了如何将字符串转换为枚举常量。