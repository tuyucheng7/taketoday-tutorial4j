## 1. 概述

在Java中，我们可以使用[printf格式化字符串](https://en.wikipedia.org/wiki/Printf_format_string)来[格式化数字](https://www.baeldung.com/java-printstream-printf#number)。

在本快速教程中，我们将探讨如何在Kotlin中格式化数字，主要是十进制数字。

## 2. 问题简介

当我们需要将Float或Double数字转换为String时，我们通常希望指定数字的精度或小数位数，一个例子可以快速解释它。假设我们有一个Double实例PI：

```kotlin
private const val PI = 3.141592653589793
```

现在，我们要构建一种方法，将Double数字转换为String并应用给定的精度，例如：

```kotlin
private const val EXPECTED_SCALE_0 = "3"
private const val EXPECTED_SCALE_2 = "3.14"
private const val EXPECTED_SCALE_4 = "3.1416"
```

在本教程中，我们将探讨一些完成这项工作的方法。为简单起见，我们将使用单元测试的断言来验证我们的格式化方法是否按预期工作。

## 3. 使用Java的String.format方法

Java的[String.format](https://www.baeldung.com/string/format)方法可以方便地使用printf格式字符串格式化输入，我们可以轻松地在Kotlin中构建一个方法，该方法调用Java方法将Kotlin Double数字转换为String：

```kotlin
fun usingJavaStringFormat(input: Double, scale: Int) = String.format("%.${scale}f", input)
```

如上面的代码所示，usingJavaStringFormat方法接收两个参数，Double类型的输入和小数位数。

接下来，让我们编写一个小测试方法来验证该方法是否返回预期的String：

```kotlin
val out0 = usingJavaStringFormat(PI, 0)
assertThat(out0).isEqualTo(EXPECTED_SCALE_0)

val out2 = usingJavaStringFormat(PI, 2)
assertThat(out2).isEqualTo(EXPECTED_SCALE_2)

val out4 = usingJavaStringFormat(PI, 4)
assertThat(out4).isEqualTo(EXPECTED_SCALE_4)
```

如果我们执行它，测试就会通过。因此，Java的String.format方法可以完成这项工作。

值得一提的是，在usingJavaStringFormat方法中，我们使用了Kotlin的[String模板](https://www.baeldung.com/kotlin/string-templates)来设置小数位数：${scale}。

事实上，标准printf格式字符串的语法支持'*'来定义动态宽度，让我们以shell的[printf](https://www.baeldung.com/linux/printf-echo#printf)命令来演示一些例子：

```shell
$ printf "%.*f\n" 0 3.1415926
3

$ printf "%.*f\n" 2 3.1415926
3.14

$ printf "%.*f\n" 4 3.1415926
3.1416
```

但是，**Java的format方法不支持在格式字符串中使用“*”作为动态宽度的占位符**。因此，如果我们想让我们的格式支持动态宽度，我们需要在Java中拼接不同的部分，例如“%”。+ scale + “f”，或者使用Kotlin中的字符串模板，就像我们在上面的方法中所做的那样。

## 4. 使用Kotlin的String.format函数

为了让Java的String.format方法更容易使用，Kotlin在标准库中定义了一个[扩展函数](https://www.baeldung.com/kotlin/extension-methods)String.format ：

```kotlin
public inline fun String.format(vararg args: Any?): String = java.lang.String.format(this, *args)
```

正如我们所看到的，**Kotlin的String.format函数在内部调用了Java的String.format方法**。

使用这个方便的扩展函数，我们可以创建一个类似的函数来接收格式的指定比例：

```kotlin
fun usingKotlinStringFormat(input: Double, scale: Int) = "%.${scale}f".format(input)
```

让我们测试它是否适用于我们的PI示例：

```kotlin
val out0 = usingKotlinStringFormat(PI, 0)
assertThat(out0).isEqualTo(EXPECTED_SCALE_0)

val out2 = usingKotlinStringFormat(PI, 2)
assertThat(out2).isEqualTo(EXPECTED_SCALE_2)

val out4 = usingKotlinStringFormat(PI, 4)
assertThat(out4).isEqualTo(EXPECTED_SCALE_4)

```

当我们运行测试时，结果证明我们的usingKotlinStringFormat函数按预期工作。

## 5. 在Double类上创建扩展函数

现在，我们已经提到了Kotlin的扩展函数，这个问题的另一个解决方案是**创建一个扩展函数来对输入数据类进行格式化**。

例如，由于我们的示例输入是Double数字，我们可以在Double类上创建一个扩展函数：

```kotlin
fun Double.format(scale: Int) = "%.${scale}f".format(this)
```

正如我们所见，**上面的扩展函数调用了Kotlin的String.format函数。不过，我们可以更流畅地调用它**，例如：

```kotlin
val text = "Pi (${PI.format(4)}) is an important concept that appears in all aspects of math!"
```

最后，让我们验证一下我们的扩展函数是否有效：

```kotlin
val out0 = PI.format(0)
assertThat(out0).isEqualTo(EXPECTED_SCALE_0)

val out2 = PI.format(2)
assertThat(out2).isEqualTo(EXPECTED_SCALE_2)

val out4 = PI.format(4)
assertThat(out4).isEqualTo(EXPECTED_SCALE_4)
```

当我们执行测试时，测试通过了。

## 6. 总结

在本文中，我们介绍了在Kotlin中格式化小数的三种方法。