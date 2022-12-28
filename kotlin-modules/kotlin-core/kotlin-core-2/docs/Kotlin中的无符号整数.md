## 1. 概述

有时我们可能需要在域模型中只表示正数，[从Kotlin 1.3开始](https://kotlinlang.org/docs/reference/whatsnew13.html#unsigned-integers)，Kotlin支持无符号整数以满足这一要求。

在这个简短的教程中，我们将熟悉在Kotlin中声明和使用无符号整数。

## 2. 无符号整数

**Kotlin 1.3引入了无符号整数作为一项实验性功能**，目前，Kotlin仅支持以下无符号类型：

-   kotlin.UByte是一个无符号的8位整数(0到255)
-   kotlin.UShort是一个无符号的16位整数(0到65535)
-   kotlin.UInt是一个无符号的32位整数(0到2^32–1)
-   kotlin.ULong是一个无符号的64位整数(0到2^64-1)

**为了给这些无符号类型分配数字字面量，Kotlin提供了一个新的u/U后缀，类似于我们为浮点数提供的后缀f**。例如，这里我们将一些字面量分配给无符号数据类型：

```kotlin
val uByte: UByte = 42u 
val uShort: UShort = 42u 
val uInt: UInt = 42U 
val uLong: ULong = 42u
```

如上所示，我们使用u或U足以将字面量标记为无符号整数，声明的类型将确定确切的变量类型。例如，在第一个示例中，“42u”字面量是一个无符号的UInt，但声明的类型是UByte，因此字面量将被转换为UByte。

当然，如果我们省略类型，编译器会根据字面量的大小推断出UInt或ULong：

```kotlin
val inferredUInt = 42U 
val inferredULong = 0xFFFF_FFFF_FFFFu
```

编译器应该推断这两种类型，因为它们被省略了。对于第一个，因为42适合UInt，推断类型将是UInt。相反，第二个值大于UInt容量，因此推断类型为ULong。

此外，甚至可以将数字字面量显式标记为带有uL后缀的ULong：

```kotlin
val explicitULong = 42uL
```

此外，**值得一提的是，无符号整数是使用Kotlin 1.3中称为**[内联类](https://www.baeldung.com/kotlin-inline-classes)**的另一个实验性功能实现的**。

## 3. 实验功能

在撰写本文时，这个新的无符号整数功能还处于实验阶段。因此，如果我们在代码中使用它们，编译器将发出警告，说明将来可能发生不兼容的更改：

```shell
This declaration is experimental and its usage should be marked with '@kotlin.ExperimentalUnsignedTypes' or '@OptIn(kotlin.ExperimentalUnsignedTypes::class)'
```

幸运的是，警告本身是非常自我描述的。因此，如果我们确定要使用这个实验性功能，**我们可以使用**[@ExperimentalUnsignedTypes](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-experimental-unsigned-types/)**或**[@OptIn](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-opt-in/)**(kotlin.ExperimentalUnsignedTypes::class)注解标注封闭类或函数**：

```kotlin
@ExperimentalUnsignedTypes 
fun main() { 
    // use unsigned integers here without warning 
}
```

当Kotlin编译器看到这些注解时，它会跳过警告。

[从Kotlin 1.5](https://kotlinlang.org/docs/whatsnew15.html#stable-unsigned-integer-types)开始，UInt、ULong、UByte和UShort无符号整数类型是稳定的，这同样适用于对这些类型的操作，以及它们的范围和级数。因此，它们无需选择加入即可使用，并且可以安全地用于现实生活中的项目。另一方面，无符号类型的数组仍处于测试阶段。

## 4. 无符号数组

除了单数无符号整数之外，**还可以创建具有无符号分量的数组**。事实上，对于每个无符号整数，都有一个对应的数组类型。更具体地说，它们是UByteArray、UShortArray、UIntArray和ULongArray。

要创建一个包含无符号整数组件的数组，我们可以使用它们的构造函数：

```kotlin
val ba = UByteArray(42)
```

在这里，我们创建了一个长度为42的UByte数组。类似地，其他无符号数组提供具有相同签名的构造函数。

除了构造函数之外，我们还可以使用ubyteArrayOf()工厂方法来创建具有初始元素的数组：

```kotlin
val ba2 = ubyteArrayOf(42u, 43u)
```

在这里，我们创建了一个包含两个元素的UByte数组。同样，Kotlin也为其他无符号数组提供了带有u*ArrayOf()语法的工厂方法。

## 5. 操作无符号类型

**无符号整数支持与有符号整数相同的一组操作**。例如，我们可以将两个无符号类型加在一起，对它们执行左移，以及许多其他常见的算术运算：

```kotlin
assertEquals(3u, 2u + 1u)
assertEquals(16u, 2u shl 3)
```

同样，无符号数组提供与有符号数组相同的API：

```kotlin
uintArrayOf(42u, 43u).map { it * it }.forEach { println(it) }
```

此外，可以将有符号整数转换为无符号整数，反之亦然：

```kotlin
val anInt = 42 
val converted = anInt.toUInt()
```

显然，对于每一种无符号数据类型，Kotlin都提供了toU*()方法。

请注意，有符号整数中最高有效位是符号位。相反，该位只是无符号整数中的常规位。因此，将负符号整数转换为无符号整数可能会很棘手：

```kotlin
assertEquals((255).toUByte(), (-1).toUByte())
assertEquals((65535).toUShort(), (-1).toUShort())
assertEquals(4294967295u, (-1).toUInt())
assertEquals(18446744073709551615uL, (-1L).toULong())
```

**-1整数的二进制表示是“1111 1111 1111 1111 1111 1111 1111 1111”。因此，它被转换为UByte、UShort、UInt和ULong中的最大可能数**。因此，当我们将Int转换为其对应的UInt时，我们不能期望总是得到相同的数字。同样，当我们将UInt转换为Int时也是如此：

```kotlin
assertEquals(-1, (4294967295u).toInt())
```

也可以将有符号数组转换为无符号数组：

```kotlin
val toUIntArray = intArrayOf(-1, -2).toUIntArray()
```

## 6. 总结

在本教程中，我们熟悉了Kotlin中的无符号整数。我们看到了几种不同的方法来声明此类数据类型、操作它们，当然还有从相应的签名类型创建它们。我们还看到了如何从这些数据类型中创建数组。