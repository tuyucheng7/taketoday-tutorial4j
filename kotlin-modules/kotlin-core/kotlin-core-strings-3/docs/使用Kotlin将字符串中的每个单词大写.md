## 1. 简介

Kotlin标准库提供了很多功能，它不仅包含集合的原始类型函数，还包含一组广泛的字符串工具函数等等。

感受标准库的最好方法是完成一项实际任务，并展示我们如何使用Kotlin SDK以不同的方式解决它。

## 2. 一个简单的解决方案

任何可能需要将其单词大写的短字符串都包含由空格分隔的字母序列的单词，因此，**我们可以使用String.split()函数从输入字符串中生成一个单词数组，并遍历它以将每个单词大写**。

然后我们可以将字符串重新组合在一起：

```kotlin
input.split(' ')
    .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
```

joinToString()函数通过从集合中生成字符串并使用lambda转换该集合来组合。

该解决方案的唯一缺点是它需要三倍于存储初始字符串所需的内存，诚然，在绝大多数真实案例中，这不会成为问题。

但是，如果我们为了实验的缘故，假设字符串可能具有任意长度，那么我们可能需要一个更经济的解决方案。

## 3. 更节省内存的解决方案

与其将整个字符串拆分为一个数组，**我们可以逐字逐句地提取它，将每个单词大写**，然后将其拼接到结果字符串中。sequence{}构建器将帮助我们分离我们对字符串所做的各种转换的关注点：

```kotlin
sequence {
    var startIndex = 0
    while (startIndex < input.length) {
        val endIndex = input.indexOf(' ', startIndex).takeIf { it > 0 } ?: input.length
        yield(input.substring(startIndex, endIndex))
        startIndex = endIndex + 1
    }
}.joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
```

类似地，也为序列定义了扩展函数joinToString()，此代码只需要存储输入字符串所需地内存量的两倍。如果我们需要进一步节省，我们需要考虑流式IO。

不幸的是，输入并不总是干净有效的，如果我们的字符串中的某些空格加倍怎么办？

## 4. 支持多个空格

天真的方法几乎不会改变，**除了按字符拆分，我们还可以按正则表达式拆分**。虽然按字符函数拆分是Java开发工具包的一部分，但按正则表达式拆分是Kotlin独有的变体：

```kotlin
input.split("\\W+".toRegex())
    .joinToString(" ") { it.replaceFirstChar(Char::uppercaseChar) }
```

但是对于使用序列的解决方案，我们需要做一些额外的工作。首先，让我们创建一个函数，该函数将从指定位置逐个字符地搜索字符串，直到满足条件：

```kotlin
fun String.findFirstSince(position: Int, test: (Char) -> Boolean): Int {
    for (i in position until length) {
        if (test(this[i])) return i
    }
    return length
}
```

然后我们可以在输入字符串中搜索每个单词的开头和结尾，从而按顺序生成它：

```kotlin
sequence {
    var startIndex = 0
    while (startIndex < input.length) {
        val endIndex = input.findFirstSince(startIndex) { it == ' ' }
        yield(input.substring(startIndex, endIndex))
        startIndex = input.findFirstSince(endIndex) { it != ' ' }
    }
}
```

其余代码与上一节相同。

## 5. 像新闻媒体一样大写

在印刷品中，**大多数出版商不会在句子中间将小写单词大写**，为了支持这个业务逻辑，我们需要稍微复杂一点的代码。我们将从创建单词字典开始，如果它们不是句子中的第一个或最后一个单词，我们将不会大写：

```kotlin
val NON_CAPITALIZED_WORDS = setOf(
    "as", "at", "but", "by", "for", // and so on
)
```

然后我们将像往常一样使用split()函数拆分字符串：

```kotlin
val components = input.split("\\W+".toRegex())
```

最后，我们将使用Kotlin标准库中的另一个原语构建String{}：

```kotlin
buildString {
    components.forEachIndexed { index, word ->
        when (index) {
            in 1..components.size - 2 -> word.capitalizeMiddleWord() // Some short auxiliary words aren't capitalized
            else -> word.replaceFirstChar(Char::uppercaseChar) // The first and the last words are always capitalized
        }.let { append(it).append(' ') }
    }
    deleteCharAt(length - 1) // Drop the last whitespace
}
```

**capitalizeMiddleWord()函数包含了我们解决方案的大部分复杂性**：

```kotlin
private fun String.capitalizeMiddleWord(): String =
    if (length > 3 || this !in NON_CAPITALIZED_WORDS) replaceFirstChar(Char::uppercaseChar) else this
```

请注意，我们首先进行了长度检查，这比检查集合是否包含值要便宜。

## 6. 总结

在本教程中，我们尝试提供各种解决方案来将字符串中的每个单词大写，根据任务的条件，我们可能需要不同的方法：如果输入字符串不占用应用程序内存的很大一部分，我们可以直接split()它；否则，可以使用更复杂的解决方案。如果需要，我们还可以支持更多的业务规则。