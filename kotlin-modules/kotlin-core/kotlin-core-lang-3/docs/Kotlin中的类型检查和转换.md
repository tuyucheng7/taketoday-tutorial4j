## 1. 概述

在本教程中，我们将首先了解如何在Kotlin中检查给定对象的类型；接下来，我们将学习Kotlin的两种类型转换方法：智能类型转换和显式类型转换。

为简单起见，我们将使用测试断言来验证示例方法的结果。

## 2. 类型检查

在Java中，我们可以使用[instanceof](https://www.baeldung.com/java-instanceof)运算符来检查给定对象的类型。例如，我们可以使用“instanceof String”来测试一个对象是否是String类型：

```java
Object obj = "I am a string";
if (obj instanceof String) {
    ...
}
```

**在Kotlin中，我们使用**'[is](https://kotlinlang.org/docs/typecasts.html#is-and-is-operators)'**运算符来检查给定对象是否属于特定类型**，它的否定形式是'!is'。

接下来，让我们创建几个Kotlin函数来解决is和!is运算符的使用问题：

```kotlin
fun isString(obj: Any): Boolean = obj is String

fun isNotString(obj: Any): Boolean = obj !is String
```

如上面的代码所示，我们创建了两个函数，两者都接收Any类型的参数，并检查对象obj是否为String类型。

值得一提的是，Kotlin的Any与Java的Object非常相似，唯一的区别是Any表示不可为空的类型。

现在，让我们创建一个测试来验证函数是否按预期工作：

```kotlin
val aString: Any = "I am a String"
val aLong: Any = 42L

assertThat(isString(aString)).isTrue
assertThat(isString(aLong)).isFalse

assertThat(isNotString(aString)).isFalse
assertThat(isNotString(aLong)).isTrue
```

如果我们运行，测试就会通过。从上面的示例中，我们意识到is和!is运算符非常简单，此外，它们易于阅读。

接下来，让我们看看Kotlin是如何处理类型转换的。

## 3. 显式转换

在Java中，我们使用(TheType) someObject将对象转换为目标类型。例如，(BigDecimal) numObj将numObj强制转换为BigDecimal对象。

**在Kotlin中，我们使用as和as?运算符来强制转换类型**。

接下来，让我们学习如何在Kotlin中进行类型转换。此外，我们将讨论as和as?之间的区别。

as被称为不安全的转换运算符，这是因为**如果as转换失败，比如Java，Kotlin将抛出ClassCastException**。

一个例子可以快速解释它，首先，让我们创建一个简单的函数来将给定对象强制转换为String并返回它：

```kotlin
fun unsafeCastToString(obj: Any): String = obj as String
```

接下来，让我们测试一下：

```kotlin
val aString: Any = "I am a String"
val aLong: Any = 42L

assertThat(unsafeCastToString(aString)).isEqualTo(aString)
assertFailsWith<java.lang.ClassCastException> {
    unsafeCastToString(aLong)
}
```

如上面的测试所示，我们将一个String和一个Long传递给函数，此外，我们使用Kotlin的[assertFailsWith](https://www.baeldung.com/kotlin/assertfailswith#using-kotlins-assertfailswith-method)函数来验证是否抛出ClassCastException。

如果我们执行测试，它就会通过。

有时，**如果类型转换失败，我们不希望函数抛出异常；相反，我们希望有一个空值**。在Java中，我们可以通过捕获ClassCastException并返回null来做到这一点。但是，在Kotlin中，我们可以使用安全转换运算符as?来实现它。

那么，我们还是来了解下as？的用法吧，下面是函数的定义以及对应的测试：

```kotlin
fun safeCastToString(obj: Any): String? = obj as? String

val aString: Any = "I am a String"
val aLong: Any = 42L

assertThat(unsafeCastToString(aString)).isEqualTo(aString)
assertThat(unsafeCastToString(aLong)).isNull()
```

正如我们在上面的测试中看到的，这一次，当我们尝试将Long对象强制转换为String时，我们得到了一个空值。此外，不会抛出异常。

## 4. 智能转换

通常，我们希望在类型检查成功后执行转换，在Kotlin中，**如果类型检查成功，编译器会跟踪类型信息并自动将对象转换为“is”检查为true范围内的目标类型**：

```kotlin
val obj: Any = "..."
if (obj is String) {
    // obj is smart-casted to a String
    obj.subString(...)
}
```

接下来，为了更好地理解Kotlin的智能转换，我们将使用它来解决一个小问题。

假设我们收到一个Any类型的对象(obj)，根据obj的具体类型，我们想应用不同的操作：

-   String：复制字符串并返回obj + obj
-   Long：将值加倍，返回obj * 2
-   List：返回一个包含原始列表中重复元素的新列表，例如，给定{1 ,2, 3}，它返回{1, 2, 3, 1, 2, 3}
-   其他类型：返回一个字符串：“Unsupported Type Found.”

现在，让我们构建一个函数并使用智能转换来解决问题：

```kotlin
fun doubleTheValue(obj: Any): Any =
    when (obj) {
        is String -> obj.repeat(2)
        is Long -> obj * 2
        is List<*> -> obj + obj
        else -> "Unsupported Type Found."
    }
```

如我们所见，使用Kotlin的智能转换可以很方便的解决这个问题。

像往常一样，让我们创建一个测试来验证我们的函数是否按预期工作：

```kotlin
val aString: Any = "I am a String"
val aLong: Any = 42L
val aList: Any = listOf(1, 2, 3)
val aDate: Any = Instant.now()

assertThat(doubleTheValue(aString)).isEqualTo("$aString$aString")
assertThat(doubleTheValue(aLong)).isEqualTo(84L)
assertThat(doubleTheValue(aList)).isEqualTo(listOf(1, 2, 3, 1, 2, 3))
assertThat(doubleTheValue(aDate)).isEqualTo("Unsupported Type Found")
```

如果我们运行它，测试就会通过。

## 5. 总结

在本文中，我们学习了如何在Kotlin中执行类型检查和强制转换。