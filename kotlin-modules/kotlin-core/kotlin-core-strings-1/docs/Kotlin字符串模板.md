## 1. 概述

在本教程中，我们将解释Kotlin的字符串模板是什么以及如何使用它们。

为了熟悉其他功能并了解如何使用Kotlin，请查看我们的[Kotlin教程](https://www.baeldung.com/kotlin-overview)之一。

## 2. Kotlin中的字符串

与Java一样，Kotlin中的字符串是不可变的，这意味着一旦创建字符串，我们就无法对其进行更改。但是，我们可以从给定的字符串中派生出另一个字符串。

Kotlin通过附加功能丰富了Java String类。

例如，方法padEnd()允许我们格式化一个字符串，以便表达式：

```kotlin
"Hello".padEnd(10, '!')
```

这会产生一个新字符串“Hello!!!!!” .

## 3. 字符串模板

**字符串模板是包含嵌入表达式的字符串文本**。

例如，Java中的这段代码：

```java
String message = "n = " + n;
```

在Kotlin中只是：

```kotlin
val message = "n = $n"
```

**任何有效的Kotlin表达式都可以在字符串模板中使用**：

```kotlin
val message = "n + 1 = ${n + 1}"
```

**与Java不同，Kotlin的许多构造(当然不是全部)都是表达式**。

因此，字符串模板也可能包含逻辑：

```kotlin
val message = "$n is ${if(n > 0) "positive" else "not positive"}"
```

请注意，在大括号内有一个有效的Kotlin表达式，这就是我们不转义嵌套双引号的原因。

字符串模板通过计算表达式并根据计算结果调用toString()方法来解析。

字符串模板可以嵌套：

```kotlin
val message = "$n is ${if (n > 0) "positive" else
    if (n < 0) "negative and ${if (n % 2 == 0) "even" else "odd"}" else "zero"}"
```

字符串模板解析器开始从最嵌套的模板解析它，对其进行计算，并在其上调用toString()方法。

虽然字符串模板可以嵌套，但最好让它们尽可能简单。这一点也不难，因为Kotlin为我们提供了许多有用的工具。

**如果我们想使用原始美元符号而不是作为字符串模板的一部分怎么办？**

然后我们通过在它前面放一个反斜杠来转义它：

```kotlin
val message = "n = \$n"
```

美元符号后面的内容变成了一个普通的字符串-它不再被计算，而是按原样解释。

## 4. 原始字符串

此外，在Kotlin中，我们有三引号的原始字符串，它们可以包含特殊字符而无需转义它们。

结果字符串包含在三个连续的非重叠出现的三重双引号"""之间。

例如，在Java中，为了正确创建一个包含Windows风格文件路径的字符串，该文件路径指向位于C:\Repository\read.me的资源，我们应该这样定义它：

```java
String path = "C:\\Repository\\read.me";
```

在Kotlin中，我们可以使用三引号表示法来获得相同的结果：

```kotlin
val path = """C:\Repository\read.me"""
```

我们可以使用这种表示法来创建多行字符串：

```kotlin
val receipt = """Item 1: $1.00
Item 2: $0.50"""
```

这将创建一个正好跨越两行的字符串，如果我们更喜欢这种缩进：

```kotlin
val receipt = """Item 1: $1.00
                >Item 2: $0.50""".trimMargin(">")
```

我们使用trimMargin()方法来消除从每一行的开头到第一次出现边距前缀(在上面示例中>)的最终空白。

**三引号字符串不支持任何转义序列**，这意味着如果我们写

```kotlin
val receipt = """Item 1: $1.00\nItem 2: $0.50"""
```

为了获得一个两行字符串，我们将得到一个包含字符\n的单行，而不是预期的换行符。

**不过，三引号字符串确实支持模板**。 

这意味着任何以美元符号开头的序列都会以我们在上一节中描述的方式解析为字符串，我们可以使用这个事实来使转义字符起作用：

```kotlin
val receipt = """Item 1: $1.00${"\n"}Item 2: $0.50"""
```

## 5. 总结

在本文中，我们介绍了Kotlin语言的一个Java中没有的特性-字符串模板，我们说明了它们在普通字符串和多行字符串的情况下的用法。