## 1. 概述

在这个快速教程中，我们将熟悉在Kotlin中将字符串拆分为一系列元素的几种方法。

## 2. 分隔符分割

要仅使用一个分隔符拆分字符串，**我们可以使用**[split(delimiter: String)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/split.html)**扩展函数**：

```kotlin
val info = "Name,Year,Location"
assertThat(info.split(",")).containsExactly("Name", "Year", "Location")
```

在这里，split()函数按预期将逗号分隔符周围的给定字符串拆分，非常有趣的是，我们甚至**可以将多个分隔符传递给同一个函数**：

```kotlin
val info = "Name,Year,Location/Time"
assertThat(info.split(",", "/")).containsExactly("Name", "Year", "Location", "Time")
```

在上面的示例中，我们同时使用逗号和斜杠字符作为分隔符，也可以限制拆分部分的数量：

```kotlin
val info = "Name,Year,Location/Time/Date"
assertThat(info.split(",", limit = 2)).containsExactly("Name", "Year,Location/Time/Date")
assertThat(info.split(",", "/", limit = 4)).containsExactly("Name", "Year", "Location", "Time/Date")
```

例如，当限制参数为2时，返回的列表中最多包含2个元素。

此外，**可以以不区分大小写的方式围绕分隔符进行拆分**：

```kotlin
val info = "127.0.0.1aaFirefoxAA58"
assertThat(info.split("aa", ignoreCase = true)).containsExactly("127.0.0.1", "Firefox", "58")
```

除了split()函数之外，我们还可以使用特殊的[lines()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/lines.html)扩展函数以换行作为分隔符进行拆分：

```kotlin
val info = "First line\nsecond line\rthird"
assertThat(info.lines()).containsExactly("First line", "second line", "third")
```

lines()函数使用三个字符或字符序列作为其分隔符：\n、\r和\r\n。

### 2.1 惰性拆分

**split()扩展函数的所有变体都返回一个List<String\>—一个热切评估的部分集合**。与简单的split()相反，有一个[splitToSequence()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/split-to-sequence.html)变体返回一个[Sequence](https://www.baeldung.com/kotlin/sequences)，这是一个延迟计算的集合：

```kotlin
val info = "random_text,".repeat(1000)
assertThat(info.splitToSequence(",").first()).isEqualTo("random_text")
```

在这里，splitToSequence()不会创建包含1,000个元素的集合，相反，它返回某种迭代器，因此每个部分的计算将被推迟，直到我们要求它。在上面的例子中，只计算了第一个拆分部分。

就像其他序列一样，如果我们对返回的Sequence<String\>执行一系列操作，我们不会在每个步骤结束时得到中间结果，这可能是有益的，尤其是当拆分部分和操作的数量非常多时。

## 3. 按正则表达式拆分

除了文本字符，**我们还可以使用正则表达式作为分隔符**，例如：

```kotlin
val info = "28 + 32 * 2 / 64 = 29"
val regex = "\\D+".toRegex()
assertThat(info.split(regex)).containsExactly("28", "32", "2", "64", "29")
```

在上面的示例中，我们使用任何非数字序列作为分隔符，非常类似地，我们也可以将Java的[Pattern](https://www.baeldung.com/regular-expressions-java)作为正则表达式传递：

```kotlin
val pattern = Pattern.compile("\\D+")
assertThat(info.split(pattern)).containsExactly("28", "32", "2", "64", "29")
```

也可以限制拆分后字符串的数量：

```kotlin
assertThat(info.split(regex, 3)).containsExactly("28", "32", "2 / 64 = 29")
assertThat(info.split(pattern, 3)).containsExactly("28", "32", "2 / 64 = 29")
```

## 4. 总结

在这个简短的教程中，我们看到了如何使用文本字符和正则表达式作为分隔符来拆分Kotlin字符串，此外，我们还学会了以惰性的方式计算拆分部分。