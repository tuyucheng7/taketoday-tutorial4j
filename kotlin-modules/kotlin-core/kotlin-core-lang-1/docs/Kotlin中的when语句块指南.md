## 1. 概述

本教程介绍Kotlin语言中的when{}块，并演示我们可以使用它的各种方式。

## 2. Kotlin的when{}块

**when{}块本质上是Java中已知的switch-case语句的高级形式**。

在Kotlin中，如果找到匹配的case，则只执行相应case块中的代码，并且继续执行when块之后的下一条语句。

这实质上意味着我们不需要在每个case块的末尾使用break语句。

为了演示when{}的用法，让我们定义一个枚举类，它保存Unix中某些文件类型的权限字段中的第一个字母：

```kotlin
enum class UnixFileType {
    D, HYPHEN_MINUS, L
}
```

让我们也定义一个类的层次结构用于对各个Unix文件类型进行建模：

```kotlin
sealed class UnixFile {

    abstract fun getFileType(): UnixFileType

    class RegularFile(val content: String) : UnixFile() {
        override fun getFileType(): UnixFileType {
            return UnixFileType.HYPHEN_MINUS
        }
    }

    class Directory(val children: List<UnixFile>) : UnixFile() {
        override fun getFileType(): UnixFileType {
            return UnixFileType.D
        }
    }

    class SymbolicLink(val originalFile: UnixFile) : UnixFile() {
        override fun getFileType(): UnixFileType {
            return UnixFileType.L
        }
    }
}
```

### 2.1 when{}作为表达式

与Java的switch语句的一个很大区别是**我们可以在Kotlin中将when{}块用作语句和表达式**。Kotlin遵循其他函数式语言的原则，流程控制结构是表达式，它们的计算结果可以返回给调用者。

如果返回值被赋值给一个变量，编译器会检查返回值的类型是否与客户端期望的类型兼容，如果不兼容会通知我们：

```kotlin
@Test
fun testWhenExpression() {
    val directoryType = UnixFileType.D

    val objectType = when (directoryType) {
        UnixFileType.D -> "d"
        UnixFileType.HYPHEN_MINUS -> "-"
        UnixFileType.L -> "l"
    }

    assertEquals("d", objectType)
}
```

在Kotlin中使用when作为表达式时有两点需要注意。

首先，返回给调用者的值是匹配case块的值，或者换句话说，是块中最后一个定义的值。

要注意的第二件事是我们需要保证调用者得到一个值。为此，我们需要确保when块中的case涵盖可以分配给参数的每个可能值。

### 2.2 when{}作为带有默认case的表达式

默认case将匹配任何与正常case不匹配的参数值，并且在Kotlin中使用else子句声明。

在任何情况下，Kotlin编译器都会假定每个可能的参数值都包含在when块中，如果没有包含则会报错。

以下是如何在Kotlin的when表达式中添加默认case：

```kotlin
@Test
fun testWhenExpressionWithDefaultCase() {
    val fileType = UnixFileType.L

    val result = when (fileType) {
        UnixFileType.L -> "linking to another file"
        else -> "not a link"
    }

    assertEquals("linking to another file", result)
}
```

### 2.3 when{}带有抛出异常的case表达式

**在Kotlin中，throw返回一个类型为Nothing的值**。

在这种情况下，我们使用Nothing来声明表达式计算值失败，Nothing是继承自Kotlin中所有用户定义和内置类型的类型。

因此，由于该类型与我们将在when块中使用的任何参数兼容，因此即使我们将when块用作表达式，从case中抛出异常也是完全有效的。

让我们定义一个when表达式，其中一个case会抛出异常：

```kotlin
@Test(expected = IllegalArgumentException::class)
fun testWhenExpressionWithThrowException() {
    val fileType = UnixFileType.L

    val result: Boolean = when (fileType) {
        UnixFileType.HYPHEN_MINUS -> true
        else -> throw IllegalArgumentException("Wrong type of file")
    }
}
```

### 2.4 when{}用作语句

我们还可以将when块用作语句。

在这种情况下，我们不需要涵盖参数的每个可能值，并且在每个case块中计算的值(如果有的话)将被忽略。作为一个语句，我们可以像在Java中使用switch语句一样使用when块。

让我们看看when块作为一个语句：

```kotlin
@Test
fun testWhenStatement() {
    val fileType = UnixFileType.HYPHEN_MINUS

    when (fileType) {
        UnixFileType.HYPHEN_MINUS -> println("Regular file type")
        UnixFileType.D -> println("Directory file type")
    }
}
```

我们可以看到，当我们使用when作为语句时，并不强制涵盖所有可能的参数值。

### 2.5 合并when{} case

**Kotlin的when表达式允许我们通过用逗号连接匹配条件来将不同的case组合成一个**。

只需一种case必须匹配才能执行相应的代码块，因此逗号充当OR运算符。

让我们创建一个结合两个条件的case：

```kotlin
@Test
fun testCaseCombination() {
    val fileType = UnixFileType.D

    val frequentFileType: Boolean = when (fileType) {
        UnixFileType.HYPHEN_MINUS, UnixFileType.D -> true
        else -> false
    }

    assertTrue(frequentFileType)
}
```

### 2.6 when{}不带参数使用

Kotlin允许我们在when块中省略参数值。

这实质上将when变成了一个简单的if-elseif表达式，该表达式按顺序检查case并运行第一个匹配case的代码块。如果我们在when块中省略参数，则case表达式的计算结果应该为true或false。

让我们创建一个省略参数的when块：

```kotlin
@Test
fun testWhenWithoutArgument() {
    val fileType = UnixFileType.L

    val objectType = when {
        fileType === UnixFileType.L -> "l"
        fileType === UnixFileType.HYPHEN_MINUS -> "-"
        fileType === UnixFileType.D -> "d"
        else -> "unknown file type"
    }

    assertEquals("l", objectType)
}
```

### 2.7 动态case表达式

在Java中，我们只能将switch语句与原始类型及其包装类型、枚举和String类一起使用。

相比之下，**Kotlin允许我们将when块与任何内置或用户定义的类型一起使用**。

此外，case不必像在Java中那样是常量表达式，Kotlin中的case可以是在运行时计算的动态表达式。例如，只要函数返回类型与when块参数的类型兼容，case就可以是函数的结果。

让我们用动态case表达式定义一个when块：

```kotlin
@Test
fun testDynamicCaseExpression() {
    val unixFile = UnixFile.SymbolicLink(UnixFile.RegularFile("Content"))

    when {
        unixFile.getFileType() == UnixFileType.D -> println("It's a directory!")
        unixFile.getFileType() == UnixFileType.HYPHEN_MINUS -> println("It's a regular file!")
        unixFile.getFileType() == UnixFileType.L -> println("It's a soft link!")
    }
}
```

### 2.8 范围和集合case表达式

可以在when块中定义一个case用于检查给定集合或值范围是否包含参数。

出于这个原因，Kotlin提供了in运算符，它是contains()方法的语法糖。这意味着Kotlin在幕后将case元素转换为collection.contains(element)。

以下是检查参数是否在列表中的方法：

```kotlin
@Test
fun testCollectionCaseExpressions() {
    val regularFile = UnixFile.RegularFile("Test Content")
    val symbolicLink = UnixFile.SymbolicLink(regularFile)
    val directory = UnixFile.Directory(listOf(regularFile, symbolicLink))

    val isRegularFileInDirectory = when (regularFile) {
        in directory.children -> true
        else -> false
    }

    val isSymbolicLinkInDirectory = when {
        symbolicLink in directory.children -> true
        else -> false
    }

    assertTrue(isRegularFileInDirectory)
    assertTrue(isSymbolicLinkInDirectory)
}
```

现在我们将检查参数是否在一个范围内：

```kotlin
@Test
fun testRangeCaseExpressions() {
    val fileType = UnixFileType.HYPHEN_MINUS

    val isCorrectType = when (fileType) {
        in UnixFileType.D..UnixFileType.L -> true
        else -> false
    }

    assertTrue(isCorrectType)
}
```

尽管REGULAR_FILE类型未明确包含在范围内，但其序号介于DIRECTORY和SYMBOLIC_LINK的序号之间，因此测试成功。

### 2.9 is运算符和智能case

我们可以使用Kotlin的is运算符来检查参数是否是指定类型的实例，is运算符类似于Java中的instanceof运算符。

然而，Kotlin为我们提供了一个叫做smart cast的特性，在我们检查参数是否是给定类型的实例之后，我们不必显式地将参数转换为该类型，因为编译器会为我们执行此操作。

因此，我们可以直接在case块中使用给定类型中定义的方法和属性。

让我们在when块中使用具有智能转换功能的is运算符：

```kotlin
@Test
fun testWhenWithIsOperatorWithSmartCase() {
    val unixFile: UnixFile = UnixFile.RegularFile("Test Content")

    val result = when (unixFile) {
        is UnixFile.RegularFile -> unixFile.content
        is UnixFile.Directory -> unixFile.children.map { it.getFileType() }.joinToString(", ")
        is UnixFile.SymbolicLink -> unixFile.originalFile.getFileType()
    }

    assertEquals("Test Content", result)
}
```

无需将unixFile显式转换为RegularFile、Directory或SymbolicLink，我们就可以分别使用RegularFile.content、Directory.children和SymbolicLink.originalFile。

### 2.10 when表达式和循环

从[Kotlin 1.4](https://kotlinlang.org/docs/reference/whatsnew14.html#using-break-and-continue-inside-when-expressions-included-in-loops)开始，即使在when表达式中也可以break或continue循环：

```kotlin
val colors = setOf("Red", "Green", "Blue")
for (color in colors) {
    when(color) {
        "Red" -> break
        "Green" -> continue
        "Blue" -> println("This is blue")
    }
}
```

这里break终止了最近的封闭循环，并且continue继续进行下一步，正如预期的那样。

**然而，在Kotlin 1.4之前，循环内的when表达式中只允许符合条件的break和continue**：

```kotlin
LOOP@ for (color in colors) {
    when(color) {
        "Red" -> break@LOOP
        "Green" -> continue@LOOP
        "Blue" -> println("This is blue")
    }
}
```

如上所示，**break和continue由@LOOP表达式限定**。

## 3. 总结

在本文中，我们看到了如何使用Kotlin语言提供的when块的几个示例。

尽管在Kotlin中使用when进行模式匹配是不可能的，就像Scala和其他JVM语言中的相应结构一样，但when块的通用性足以让我们完全忘记这些功能。