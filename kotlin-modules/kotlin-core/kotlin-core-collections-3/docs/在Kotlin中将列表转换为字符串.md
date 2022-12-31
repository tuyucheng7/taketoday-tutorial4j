## 1. 简介

List是Kotlin中的基本集合之一，将元素集合转换为普通字符串是一个非常常见的用例。在本教程中，我们将学习如何轻松地将List转换为String。

## 2. 使用joinToString将List转换为字符串

Kotlin Collections API有许多有用的方法来对集合执行操作，其中之一是[joinToString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/join-to-string.html)，**此方法从集合中的所有元素创建一个字符串**，默认分隔符是逗号。

让我们定义我们的List并检查joinToString的输出：

```kotlin
val numbers = listOf(11, 22, 3, 41, 52, 6)
val string = numbers.joinToString()
assertEquals("11, 22, 3, 41, 52, 6", string)
```

我们可以定义分隔符、前缀和后缀参数来自定义输出字符串：

```kotlin
val numbers = listOf(11, 22, 3, 41, 52, 6)
val string = numbers.joinToString(prefix = "<", postfix = ">", separator = "")
assertEquals("<1122341526>", string)
```

如果我们的List太长，我们也可以设置一个限制，我们甚至可以设置一个转换lambda：

```kotlin
val chars = charArrayOf('h', 'e', 'l', 'l', 'o', 'o', 'o', 'o')
val string = chars.joinToString(separator = "", limit = 5, truncated = "!") { it.uppercaseChar().toString() }
assertEquals("HELLO!", string)
```

## 3. 使用joinTo将List附加到现有文本

**如果我们已经有了一个字符串或一段文本，并且想向其中添加我们List的元素，我们可以使用joinTo方法，它使用StringBuilder将由所有List元素形成的字符串附加到现有字符串**。和前面的例子一样，如果我们提供参数，我们可以自定义我们的输出字符串：

```kotlin
val sb = StringBuilder("An existing string and a list: ")
val numbers = listOf(11, 22, 3, 41, 52, 6)
val string = numbers.joinTo(sb).toString()
assertEquals("An existing string and a list: 11, 22, 3, 41, 52, 6", string)
```

事实上，joinTo将任何实现Appendable接口的类作为其缓冲区参数。但是，在标准库中只有StringBuilder 会这样做。该方法返回与其缓冲区参数相同的类型，因此它很可能是StringBuilder。

## 4. 使用reduce和fold从带有反馈的List创建字符串

我们还可以使用reduce函数从List中形成一个字符串，**reduce函数从第一个元素开始累积值，然后，它将操作从左到右应用于当前累加器值和每个后续元素**。因此，累加器的每个下一个值可能取决于它已经拥有的值：

```kotlin
val strings = listOf("a", "b", "a", "c", "c", "d", "b", "a")
val uniqueSubstrings = strings.reduce { acc, string -> if (string !in acc) acc + string else acc }
assertEquals("abcd", uniqueSubstrings)
```

如所写，此代码将在每次迭代时实例化一个新的String实例。这意味着，对于大型集合，这种方法可能对应用程序性能非常不利。

其次，**不能归约空集合**；相反，它会抛出异常。因此，我们必须密切关注我们使用reduce的代码部分。

**完成同一任务的另一种方法可能是fold函数**，它是归约joinTo为joinToString-我们必须提供一个初始值，它在第一次迭代中用作累加器：

```kotlin
val strings = listOf("a", "b", "a", "c", "c", "d", "b", "a")
val uniqueSubstrings =
    strings.fold(StringBuilder()) { acc, string -> if (string !in acc) acc.append(string) else acc }
assertEquals("abcd", uniqueSubstrings.toString())
```

这样，我们可以在我们的集合上使用StringBuilder，从而避免reduce的内存和性能问题。

## 5. 循环

最后，**我们可以使用简单的拼接逐个字符地构建字符串**，我们可以通过遍历List并将每个字符一次一个地连接到字符串来做到这一点。让我们看一个例子：

```kotlin
val elements = listOf("a", "b", "c", "d", "e")
var string = ""

for(s in elements){
    string += s
}

assertEquals("abcde", string)
```

这种方法与我们刚刚看到的reduce类似，**在涉及大型集合时是一种危险**。相反，我们可以使用StringBuilder来做同样的事情：

```kotlin
val letters = listOf("a", "b", "c", "d", "e", "f")
val builder = StringBuilder()

for(s in letters){
    builder.append(s)
}

assertEquals("abcdef", builder.toString())
```

事实上，**Kotlin有一个很好的语法糖，可以使处理StringBuilder更加舒适**：

```kotlin
val letters = listOf("a", "b", "c", "d", "e", "f")
val alreadyAString = buildString { for (s in letters) append(s) } // `this` is a StringBuilder inside the lambda
assertEquals("abcdef", alreadyAString)
```

要自定义输出，我们可以在将StringBuilder作为String返回后使用像removeSuffix()这样的内置函数：

```kotlin
val letters = listOf("a", "b", "c", "d", "e", "f")
val string = buildString { letters.forEach(::append) }
val withoutSuffix = string.removeSuffix("f")
assertEquals("abcde", withoutSuffix)
```

## 6. 总结

在本文中，我们了解了在Kotlin中将List转换为字符串的最常见方法，使用Kotlin的内置函数可以让我们的代码更简洁并遵循语言的约定，我们还可以轻松自定义输出字符串。