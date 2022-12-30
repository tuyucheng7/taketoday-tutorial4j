## 1. 概述

在这个快速教程中，我们将探讨如何在Kotlin中填充数字。

## 2. 问题介绍

当我们谈论填充数字时，**通常有两种常见的填充要求：开始(左)填充和结束(右)填充**。

像往常一样，让我们通过示例来理解问题，假设我们得到了一个数字列表：

```kotlin
val inputNumbers = listOf(7, 42, 4200, 42000, 420000)
```

现在，假设我们的第一个要求是对于长度小于5的数字，**我们希望在这些数字的开头填充零，以便它们的长度均为5**。如果数字的长度为5或更大，我们将保持原样。因此，下面是此要求的预期结果：

```kotlin
val expectedPadStart = listOf("00007", "00042", "04200", "42000", "420000")
```

我们的第二个要求是，对于长度小于5的数字，**我们希望在这些数字的末尾填充空格，以便它们的长度均为5**。如果数字的长度为5或更大，则不会更改它。因此，对于给定的数字，这是我们期望的结果：

```kotlin
val expectedPadEnd = listOf("7    ", "42   ", "4200 ", "42000", "420000")
```

一些外部库提供了工具方法来轻松地进行字符串填充，但是，在本教程中，我们将重点关注Kotlin标准库。一段时间后，我们会发现使用Kotlin标准库填充数字也非常简单。

为简单起见，我们将使用单元测试断言来验证我们的解决方案是否按预期工作。

## 3. 使用String.format()函数填充数字

在Java中，我们可以使用[String.format()](https://www.baeldung.com/java-pad-string#3-using-stringformat)方法填充字符串；同样，Kotlin的String也有一个format()函数。

如果我们看一下它的实现，它就是一个[扩展函数](https://www.baeldung.com/kotlin/extension-methods)，在内部，它调用Java的String.format()方法：

```kotlin
public inline fun String.format(vararg args: Any?): String = java.lang.String.format(this, *args)
```

现在我们已经了解了Kotlin的format()函数的作用，我们可以构建格式字符串并解决问题：

```kotlin
inputNumbers.map { "%05d".format(it) }.let {
    assertThat(it).isEqualTo(expectedPadStart)
}

inputNumbers.map { "%-5d".format(it) }.let {
    assertThat(it).isEqualTo(expectedPadEnd)
}
```

如上面的代码所示，我们使用“%05d”作为格式字符串来进行零左填充，这意味着我们想用“0”左填充十进制整数，使其宽度为5。

然后，我们将“%-5d”作为格式字符串进行正确的空格填充。

如果我们运行测试，它们就会通过。因此，问题由format()函数解决。

但是，值得一提的是，**Java的Formatter仅支持空格和零(仅用于左填充)作为填充字符**，因为空格字符已在java.util.Formatter.justify()私有方法中进行了硬编码。

因此，例如，如果我们想用'#'右填充我们的数字，我们需要首先用空格填充它们，然后用'#'替换空格：

```kotlin
inputNumbers.map { "%-5d".format(it).replace(" ", "#") }.let {
    assertThat(it).isEqualTo(listOf("7####", "42###", "4200#", "42000", "420000"))
}
```

## 4. 使用padStart()和padEnd()函数填充数字

Kotlin在String类中引入了两个非常方便的函数来填充字符串：[padStart()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/pad-start.html)和[padEnd()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/pad-end.html)。

**这两个函数都接收两个参数：填充后所需的字符串长度和用于填充字符串的字符**。 

接下来，让我们使用padStart()和padEnd()函数解决问题：

```kotlin
inputNumbers.map { "$it".padStart(5, '0') }.let {
    assertThat(it).isEqualTo(expectedPadStart)
}

inputNumbers.map { "$it".padEnd(5, ' ') }.let {
    assertThat(it).isEqualTo(expectedPadEnd)
}
```

正如我们在上面的代码中看到的，我们首先使用Kotlin的[字符串模板](https://www.baeldung.com/kotlin/string-templates)将整数转换为字符串；然后，我们调用了padStart()和padEnd()函数来应用实际的填充操作。

如果我们运行，测试就会通过。

由于padStart()和padEnd()函数支持自定义填充字符，因此如果需要非零或空格以外的填充字符，这两个函数非常方便。例如，我们可以用下划线右填充数字：

```kotlin
inputNumbers.map { "$it".padEnd(5, '_') }.let {
    assertThat(it).isEqualTo(listOf("7____", "42___", "4200_", "42000", "420000"))
}
```

## 5. 总结

在本文中，我们通过示例了解了如何在Kotlin中填充数字，我们介绍了两种方法：String.format()和padStart()/padEnd()。

**padStart()和padEnd()方法更直接，因为我们不需要构建格式字符串**。此外，我们还可以在这两种方法中自定义填充字符。