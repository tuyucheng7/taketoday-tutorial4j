## 1. 概述

在本教程中，我们将讨论如何在Kotlin中创建字节数组。我们将看到字节在Kotlin中是如何表示的；此外，我们将展示如何使用有符号和无符号字节创建字节数组。

## 2. Kotlin中的字节表示

首先，让我们展示字节在Kotlin中是如何表示的。最重要的是，**字节由Byte类型表示，此外，Byte类型包含一个**[带符号的值](https://www.baeldung.com/cs/signed-vs-unsigned-variables)，这意味着如果值是正数或负数，则保留一位用于信息。因此，它可以存储-128到127之间的值。**此外，Kotlin支持无符号字节**，[Ubyte类型](https://www.baeldung.com/kotlin/unsigned-integers#unsigned-integers)作为实验性功能提供，它可以存储0到225之间的值。

## 3. 使用有符号字节创建字节数组

现在让我们看看如何创建字节数组，**Kotlin提供了一个内置方法byteArrayOf**，此外，它以Byte类型的多个值作为参数。

现在，让我们用一个简单的例子来演示它：

```kotlin
@Test
fun `create a byte array using signed byte`() {
    val byteArray = byteArrayOf(0x48, 101, 108, 108, 111)
    val string = String(byteArray)
    assertThat(string).isEqualTo("Hello")
}
```

参数是一个Byte对象，它表示一个8位有符号整数，我们可以使用，即十六进制值或整数值作为参数。此外，值必须在-128和127的范围内。因此，我们可以创建一个负值和正值数组：

```kotlin
@Test
fun `create a byte array with negative values`() {
    val byteArray = byteArrayOf(Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE)
    assertThat(byteArray)
        .hasSize(5)
        .containsExactly(-128,-1, 0, 1, 127)
}
```

因此，当我们提供超出范围的值时，会导致编译错误：“The integer literal does not conform to the expected type Byte”。

## 4. 使用无符号字节创建字节数组

之后，让我们看看如何使用无符号字节创建字节数组，**Kotlin语言提供了一个内联类UByteArray**，整个类是实验性的功能。它提供了一个方法ubyteArrayOf，它创建了一个Ubyte对象的集合。此外，该方法也是一个实验功能，我们应该使用[@OptIn(ExperimentalUnsignedTypes::class)](https://kotlinlang.org/docs/opt-in-requirements.html)或[@ExperimentalUnsignedTypes](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-experimental-unsigned-types/)标记用法以避免编译警告。 

让我们编写一个示例：

```kotlin
@Test
@OptIn(ExperimentalUnsignedTypes::class)
fun `create a byte array using unsigned byte`(){
    val uByteArray = ubyteArrayOf(UByte.MIN_VALUE, 130U, 131u, UByte.MAX_VALUE)
    val intValues = uByteArray.map { it.toInt() }
    assertThat(intValues)
        .hasSize(4)
        .containsExactly(0, 130, 131, 255)
}
```

如上所示，我们使用U或u后缀来声明无符号值。此外，我们只能使用从0到255范围内的正值，如果我们提供超出范围的值，则会导致编译错误：“The integer literal does not conform to the expected type UByte”。

## 5. 总结

在这篇简短的文章中，我们讨论了字节数组的创建。我们描述了Kotlin中的字节表示，并介绍了如何使用有符号字节创建字节数组。最后，我们学习了如何使用无符号字节创建数组。