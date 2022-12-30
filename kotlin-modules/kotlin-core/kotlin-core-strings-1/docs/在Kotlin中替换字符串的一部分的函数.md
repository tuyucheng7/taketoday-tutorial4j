## 1. 概述

在本教程中，我们将学习一些在Kotlin中用其他内容替换字符串的一部分的方法。

## 2. 替换单个字符

为了替换字符串中的单个字符，我们可以使用[replace(oldChar, newChar)](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/common/src/kotlin/TextH.kt#L211)扩展函数：

```kotlin
val txt = "I am a robot"
val replaced = txt.replace('a', 'A')
assertEquals("I Am A robot", replaced)
```

如上所示，这个特定的方法非常简单，因为它只是用第二个参数替换所有出现的第一个参数。

**请注意，Kotlin中replace扩展函数的所有变体都将返回一个新字符串，而不是就地更改它**，这是因为字符串在Kotlin中是不可变的。

默认情况下，**replace(oldChar, newChar)方法确实关心区分大小写**。但是，我们可以通过将可选的ignoreCase参数作为最后一个参数传递来禁用此设置：

```kotlin
val replaced = txt.replace('i', 'i', ignoreCase = true)
assertEquals("i am a robot", replaced)
```

在这里，我们正在寻找小写和大写的“i”字符(由第一个和第三个参数指定)并将它们替换为小写字符(第二个参数)。

## 3. 替换子字符串

除了单个字符之外，还可以用目标字符串中的其他内容[替换子字符串](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/common/src/kotlin/TextH.kt#L219)：

```kotlin
val txt = "I am a robot"
val replaced = txt.replace("robot", "human")
assertEquals("I am a human", replaced)
```

如上所示，整个“robot”字符串被替换为“human”。同样，我们可以**通过将布尔标志作为最后一个参数传递来启用不区分大小写的版本**：

```kotlin
val replaced = txt.replace("i am a", "we are", true)
assertEquals("we are robot", replaced)
```

## 4. 替换为正则表达式

除了简单的字符序列之外，我们还可以使用正则表达式替换模式：

```kotlin
val txt = "<div>This is a div tag</div>"
val regex = "</?.*?>".toRegex() // matches with every <tag> or </tag>
val replaced = txt.replace(regex, "")
assertEquals("This is a div tag", replaced)
```

**在这里，**[replace(pattern, newString)](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/src/kotlin/text/Strings.kt#L754)**将所有出现的HTML标记替换为空字符串**。基本上，我们正在剥离所有HTML标签。

非常有趣的是，我们可以传递一个[(MatchResult) -> CharSequence](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/src/kotlin/text/Strings.kt#L762) lambda来根据匹配的部分确定替换字符串。例如，在这里，我们只是将匹配的部分变成大写：

```kotlin
val replaced = txt.replace(regex) {
    it.value.toUpperCase()
}
assertEquals("<DIV>This is a div tag</DIV>", replaced)
```

如上所示，我们可以通过在给定的lambda中返回我们想要的任何内容来动态确定替换字符串。

## 5. 替换第一个匹配项

到目前为止，我们已经用替换字符串替换了所有出现的单个字符、字符串或模式。除此之外，**还可以用一些专门的、重载的扩展函数来替换第一次出现的地方**。

为了用另一个字符替换第一次出现的单个字符，我们可以使用[replaceFirst(oldChar, newChar)](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/common/src/kotlin/TextH.kt#L224)扩展函数：

```kotlin
val txt = "I am a robot"
val replaced = txt.replaceFirst('a', 'A')
assertEquals("I Am a robot", replaced)
```

如上所示，只有第一个“a”被替换为其大写版本，非常相似，有一个变体可以用另一个字符串替换[第一个匹配](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/common/src/kotlin/TextH.kt#L230)的子字符串：

```kotlin
val txt = "42 42"
val replaced = txt.replaceFirst("42", "The answer is")
assertEquals("The answer is 42", replaced)
```

默认情况下，这两个版本都区分大小写，并且还支持关闭它们的功能。最后，还有一个用于替换第一次出现的正则表达式的重载版本：

```kotlin
val txt = "<div>this is a div</div>"
val replaced = txt.replaceFirst("</?.*?>".toRegex(), "")
assertEquals("this is a div</div>", replaced)
```

很容易理解，这里只删除了第一个HTML标签。

## 6. 范围替换

甚至可以通过几种不同的方式将字符串中的某些字符范围替换为其他字符，让我们熟悉这些方法。

### 6.1 数值范围

为了替换一系列字符，我们可以使用[replaceRange(startIndex, endIndex, replacement)](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/src/kotlin/text/Strings.kt#L503)扩展函数：

```kotlin
val txt = "42 is the answer"
val replaced = txt.replaceRange(0, 3, "")
assertEquals("is the answer", replaced)
```

和往常一样，**startIndex是包含性的，endIndex是排除性的**。因此，在这个例子中，我们基本上是用空字符串替换前三个字符。

由于Kotlin对[范围](https://www.baeldung.com/kotlin/ranges)有一流的支持，我们可以使用[replaceRange(intRange, replacement)](https://github.com/JetBrains/kotlin/blob/80cce1dc5280eb9135390270c8644a7b8d198071/libraries/stdlib/src/kotlin/text/Strings.kt#L528)扩展函数来使用[IntRange](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.ranges/-int-range/)语法：

```kotlin
assertEquals("is the answer", txt.replaceRange(0..2, ""))
assertEquals("is the answer", txt.replaceRange(0 until 3, ""))
```

在第一个示例中，“0..2”创建了一个封闭范围(包括起始索引和结束索引)。因此，第一个示例将前三个字符替换为空字符串。另一方面，第二个示例创建了一个开放范围，因此结束位置是排他性的。因此，为了获得相同的输出，我们应该使用“0 until 3”语法。

### 6.2 范围之前

除了明确的int范围之外，**我们还可以用替换字符串替换字符或字符串之前的所有内容**：

```kotlin
assertEquals("is the answer", txt.replaceBefore('i', ""))
assertEquals("is the answer", txt.replaceBefore("is", ""))
```

在上面的示例中，字符“i”和字符串“is”第一次出现之前的所有字符将被替换为空字符串。

默认情况下，当给定的搜索字符或字符串没有匹配项时，[replaceBefore()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/replace-before.html)将按原样返回整个字符串：

```kotlin
assertEquals("42 is the answer", txt.replaceBefore("not a match", ""))
```

但是，也可以通过传递第三个参数在不匹配的情况下返回另一个字符串：

```kotlin
assertEquals("default", txt.replaceBefore("not a match", "", "default"))
```

如上所示，由于没有匹配给定的字符串，该函数返回“default”字符串。

默认情况下，replaceBefore()函数会替换给定参数第一次出现之前的所有内容，**也可以使用**[replaceBeforeLast()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/replace-before-last.html)**重载版本根据给定字符的最后一次出现来做同样的事情**：

```kotlin
assertEquals("swer", txt.replaceBeforeLast('s', ""))
```

这里，'s'字符在文本中出现了两次，上面的函数选择最后一次出现作为替换的结束点。

### 6.3 范围之后

与replaceBefore()非常相似，有一些扩展函数可以以相反的方式工作。也就是说，可以在特定字符或字符串的第一次或最后一次出现之后替换所有字符：

```kotlin
assertEquals("42 i", txt.replaceAfter('i', ""))
assertEquals("42 is", txt.replaceAfter("is", ""))
assertEquals("42 is the answer", txt.replaceAfter("not a match", ""))
assertEquals("default", txt.replaceAfter("not a match", "", "default"))
assertEquals("42 is the ans", txt.replaceAfterLast('s', ""))
```

如上所示，这些重载函数与我们在上一节中看到的非常相似。

## 7. 替换缩进

我们可以使用[replaceIndent()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/replace-indent.html)扩展函数删除起始缩进(以一个或多个空格开头的句子)：

```kotlin
assertEquals("starts with indent", "    starts with indent".replaceIndent())
```

如上所示， replaceIndent()函数删除了句子开头的几个多余空格，非常有趣的是，**我们甚至可以用其他字符串替换这些缩进**：

```kotlin
assertEquals("==> starts with indent", "    starts with indent".replaceIndent("==> "))
```

在这里，我们将开头的多余空格替换为“==>”标记。

## 8. 其他功能

有些函数的名称中没有replace前缀，但其中一些函数与替换功能相关。在本节中，我们将熟悉其中的一些。

### 8.1 修剪

**与trim相关的API将帮助我们从字符串的开头或结尾删除一些字符集**，在前面的部分中，我们看到我们可以用空字符串替换所有空格字符：

```kotlin
assertEquals("both ends", " both ends ".replace(" ", ""))
```

这样，我们基本上是在修剪字符串两端的额外空格。好消息是，由于扩展函数的灵活性，在Kotlin中有更简单的方法可以实现这一点。

例如，[trim()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/trim.html)扩展函数会删除字符串两端的多余空格：

```kotlin
assertEquals("both ends", "  both ends ".trim())
```

默认情况下，此函数查找空格字符，但我们可以更改修剪字符：

```sql
assertEquals
("both ends", "###both ends!!".trim('#', '!'))
```

在上面的示例中，**我们明确告诉trim()查找#和!，而不是空格，作为修剪字符**。甚至可以传递谓词lambda来动态确定修剪字符：

```kotlin
assertEquals("both ends", "#?!both ends@".trim { !it.isLetter() && it != ' ' })
```

这里，除了字母和空格字符之外的所有字符都被认为是修剪字符。

与trim()相反，[trimStart()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/trim-start.html)函数仅从字符串的开头删除空格字符(默认情况下)：

```kotlin
assertEquals("just the beginning  ", "  just the beginning  ".trimStart())
assertEquals("just the beginning##", "##just the beginning##".trimStart('#'))
assertEquals("just the beginning  ", " #%just the beginning  ".trimStart { !it.isLetter() })
```

如上所示，即使它删除了字符串开头的空格字符，修剪字符也是可配置的。我们可以使用[trimEnd()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/trim-end.html)扩展函数对字符串的另一端做同样的事情：

```kotlin
assertEquals("  just the ending", "  just the ending  ".trimEnd())
assertEquals("##just the beginning", "##just the beginning##".trimEnd('#'))
assertEquals(" #%just the beginning", " #%just the beginning  ".trimEnd { !it.isLetter() })
```

好消息是，与修剪相关的API在API签名方面彼此非常相似。因此，默认情况下，所有这些都会删除空格字符。但是，修剪字符可以更改为一组静态字符或由lambda函数动态确定。

### 8.2 删除

到目前为止，我们已经使用了一些替换方法来将字符串的某些部分替换为空字符串，**我们可以简单地使用一些特殊函数从字符串中删除目标部分，而不是明确提及空字符串作为替换**。

与trimStart()类似，还有另一个名为[removePrefix(prefix)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/remove-prefix.html)的扩展函数，可以从任何字符串中删除前缀：

```kotlin
assertEquals("single line comment", "//single line comment".removePrefix("//"))
```

在这里，我们从给定字符串的开头删除“//”标记。同样，我们可以从字符串中[删除特定的后缀](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/remove-suffix.html)：

```kotlin
assertEquals("end of multiline comment", "end of multiline comment*/".removeSuffix("*/"))
```

此外，可以从字符串的开头和结尾删除：

```kotlin
assertEquals("some regex", "/some regex/".removeSurrounding("/"))
```

**当且仅当字符串以给定参数开头和结尾时，上述函数才有效**。否则，它会原封不动地返回相同的字符串：

```kotlin
assertEquals("/sample", "/sample".removeSurrounding("/"))
```

由于字符串不以“/”结尾，[removeSurrounding()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/remove-surrounding.html)返回相同的字符串。为了从字符串的两端删除不同的前缀和后缀，我们可以使用removeSurrounding(prefix, suffix)扩展函数：

```kotlin
assertEquals("multiline comment", "/*multiline comment*/".removeSurrounding("/*", "*/"))
```

同样，此函数仅在字符串以给定前缀开头并以给定后缀结尾时才有效。

最后，**还可以使用**[removeRange()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/remove-range.html)**扩展函数删除给定字符串中的一系列字符**：

```kotlin
assertEquals("a robot", "I'm a robot".removeRange(0..3))
assertEquals("a robot", "I'm a robot".removeRange(0 until 4))
```

与replaceRange()类似，此函数接受Kotlin的范围语法。在上面的示例中，“0..3”语法创建了一个封闭范围，因此第三个索引上的字符也将被删除。第二个示例不是这种情况，因为“0 until 4”创建了一个开放范围。

甚至可以将开始和结束索引作为单独的参数传递：

```kotlin
assertEquals("a robot", "I'm a robot".removeRange(0, 4))
```

与往常一样，结束索引是排除的，因此不会删除第4个索引处的字符。

### 8.3 删除一些字符

[drop(n: Int)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/drop.html)**扩展函数从字符串的开头删除前n个字符**：

```kotlin
assertEquals("is the answer", txt.drop(3))
```

在这里，我们将删除前三个字符。另一方面，我们也可以删除最后n个字符：

```kotlin
assertEquals("42 is the", txt.dropLast(7))
```

甚至可以在条件为true时，从字符串的开头删除字符：

```kotlin
assertEquals(" is the answer", txt.dropWhile { it != ' ' })
```

在这里，我们将删除所有字符，直到遇到第一个空格字符。同样，我们可以从字符串的另一端做同样的事情：

```kotlin
assertEquals("42 is the ", txt.dropLastWhile { it != ' ' })
```

### 8.4 保留一些字符

与drop\*()扩展函数集相反，**我们可以使用take\*()扩展函数集来保留字符**，而不是删除字符。例如，要仅保留字符串开头的前n个字符，我们可以使用[take(n: Int)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/take.html)：

```kotlin
assertEquals("42", txt.take(2))
```

在这里，我们保留前两个字符并丢弃字符串的其余部分。其余与take相关的API与我们看到的与drop相关的API非常相似：

```kotlin
assertEquals("answer", txt.takeLast(6))
assertEquals("42", txt.takeWhile { it != ' ' })
assertEquals("answer", txt.takeLastWhile { it != ' ' })
```

## 9. 总结

得益于扩展函数的灵活性，Kotlin为字符串操作提供了极其丰富的API，包括替换相关功能。在这篇文章中，我们看到了相当多的用各种不同的方式来替换部分字符串的方法，比如用正则表达式或范围进行匹配。