## 1. 概述

在本教程中，我们将讨论如何在Kotlin中将字符串转换为字节数组。此外，我们还将了解如何将子字符串转换为字节数组。最后，我们将描述相反的转换，从字节数组转化为字符串对象。

## 2. 将字符串转换为字节数组

从字符串到字节数组的转换是编程语言中的日常用例，Kotlin语言为这种情况提供了一个直接的解决方案。

[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/)**类提供了一个toByteArray()方法，它将字符串对象转换为字节数组对象**。此外，它使用[UTF-8 ](https://www.baeldung.com/java-char-encoding#2-utf-8)[Charset](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/nio/charset/Charset.html)转换对象，可以通过向toByteArray()方法提供参数来设置字符集。

让我们看一个例子：

```kotlin
@Test
fun `should convert string to byte array`() {
    val string = "Hello world"
    val byteArray = string.toByteArray()
    assertThat(byteArray).isEqualTo(byteArrayOf(72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100))
}
```

因此，每个字符在字节数组中都有其表示形式。

同样，我们可以使用定义的字符集转换字符串：

```kotlin
val byteArray = string.toByteArray(Charsets.ISO_8859_1)
```

## 3. 将子字符串转换为字节数组

现在，让我们看一下如何将字符串的一部分转换为字节数组。同样，String类中有一个专用方法，**该类提供了一个encodeToByteArray()方法，它将字符串转换为字节数组**。此外，encodeToByteArray()方法在内部以字符集UTF-8作为参数调用toByteArray()方法。

此外，String类公开了一个具有三个参数的重载encodeToByteArray()方法：

-   startIndex：子字符串的起始位置，包括在内
-   endIndex：子字符串的结束位置，不包括在内
-   throwOnInvalidSequence：指定我们是否希望它在格式错误的字符序列上抛出异常

默认情况下，startIndex参数等于零；另一方面，endIndex参数等于字符串的长度。

让我们看一个例子：

```kotlin
@Test
fun `should convert substring to byte array`() {
    val string = "Hello world"
    val byteArray = string.encodeToByteArray(0, 5)
    assertThat(byteArray).isEqualTo(byteArrayOf(72, 101, 108, 108, 111))
}
```

在这里，我们只将“Hello”一词转换为字节数组。

## 4. 将字节数组转换为字符串

之后，让我们看看如何将字节数组转换为字符串对象。同样，String类具有处理它的内置功能：

```kotlin
@Test
fun `should convert byte array to string`() {
    val byteArray = byteArrayOf(72, 101, 108, 108, 111)
    val string = String(byteArray)
    assertThat(string).isEqualTo("Hello")
}
```

**在这里，构造函数将字节数组转换为字符并将它们组合为String返回**，转换使用UTF-8字符集进行解码。

## 5. 总结

在这篇简短的文章中，我们讨论了将字符串转换为字节数组，我们看到了如何转换整个字符串及其子字符串。最后，我们学习了如何将字节数组转换为字符串对象。