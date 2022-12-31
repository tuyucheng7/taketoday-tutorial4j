## 1. 简介

我们可以在几乎所有类型的软件中找到[正则表达式](https://www.baeldung.com/regular-expressions-kotlin)的使用(或滥用)，从快速脚本到极其复杂的应用程序。

在本文中，我们将了解如何在Kotlin中使用正则表达式。

我们不会讨论正则表达式语法；通常，需要熟悉正则表达式才能充分理解本文，并且特别推荐了解[Java Pattern语法](https://www.baeldung.com/regular-expressions-kotlin)。

## 2. 设置

虽然正则表达式不是Kotlin语言的一部分，但它们确实随其标准库一起提供。

我们可能已经将它作为我们项目的依赖项：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib</artifactId>
    <version>1.2.21</version>
</dependency>
```

我们可以在Maven Central上找到最新版本的[kotlin-stdlib](https://search.maven.org/search?q=a:kotlin-stdlib)。

## 3. 创建正则表达式对象

正则表达式是kotlin.text.Regex类的实例，我们可以通过多种方式创建一个。

一种可能性是调用Regex构造函数：

```kotlin
Regex("a[bc]+d?")
```

或者我们可以在String上调用toRegex方法：

```kotlin
"a[bc]+d?".toRegex()
```

最后，我们可以使用静态工厂方法：

```kotlin
Regex.fromLiteral("a[bc]+d?")
```

除了下一节中解释的差异外，这些选项是等同的并且相当于个人偏好。请记住要保持一致！

**提示：正则表达式通常包含将被解释为字符串文字中的转义序列的字符。因此，我们可以使用原始字符串来忘记多级转义**：

```kotlin
"""a[bc]+d?\W""".toRegex()
```

### 3.1 匹配选项

Regex构造函数和toRegex方法都允许我们指定一个附加选项或一个集合：

```kotlin
Regex("a(b|c)+d?", CANON_EQ)
Regex("a(b|c)+d?", setOf(DOT_MATCHES_ALL, COMMENTS))
"a(b|c)+d?".toRegex(MULTILINE)
"a(b|c)+d?".toRegex(setOf(IGNORE_CASE, COMMENTS, UNIX_LINES))
```

选项在RegexOption类中枚举，我们在上面的示例中方便地静态导入了该类：

-   IGNORE_CASE：启用不区分大小写的匹配
-   MULTILINE：改变^和$的含义(参见[Pattern](https://www.baeldung.com/regular-expressions-kotlin#Pattern))
-   LITERAL：导致模式中的元字符或转义序列不被赋予任何特殊含义
-   UNIX_LINES：在此模式下，只有n被识别为行终止符
-   COMMENTS：允许模式中的空格和注解
-   DOT_MATCHES_ALL：使点匹配任何字符，包括行终止符
-   CANON_EQ：通过规范分解实现等价(参见[Pattern](https://www.baeldung.com/regular-expressions-kotlin#Pattern))

## 4. 匹配

**我们主要使用正则表达式来匹配输入字符串**，有时也用于提取或替换其中的一部分。

我们现在将详细了解Kotlin的Regex类提供的用于匹配字符串的方法。

### 4.1 检查部分或全部匹配

在这些用例中，我们有兴趣**了解字符串或字符串的一部分是否满足我们的正则表达式**。

如果我们只需要部分匹配，我们可以使用containsMatchIn：

```kotlin
val regex = """a([bc]+)d?""".toRegex()

assertTrue(regex.containsMatchIn("xabcdy"))
```

如果我们希望整个String匹配，我们使用matches：

```kotlin
assertTrue(regex.matches("abcd"))
```

请注意，我们也可以将匹配用作中缀运算符：

```kotlin
assertFalse(regex matches "xabcdy")
```

### 4.2 提取匹配组件

在这些用例中，我们希望**将字符串与正则表达式进行匹配并提取字符串的一部分**。

我们可能想要匹配整个字符串：

```kotlin
val matchResult = regex.matchEntire("abbccbbd")
```

或者我们可能想要找到匹配的第一个子字符串：

```kotlin
val matchResult = regex.find("abcbabbd")
```

或者一次找到所有匹配的子字符串，作为Set：

```kotlin
val matchResults = regex.findAll("abcb abbd")
```

在任何一种情况下，如果匹配成功，结果将是MatchResult类的一个或多个实例。在下一节中，我们将了解如何使用它。

相反，如果匹配不成功，这些方法返回null或findAll的空Set。

### 4.3 MatchResult类

MatchResult类的实例表示某些输入字符串与正则表达式的成功匹配；完全匹配或部分匹配(参见上一节)。

因此，它们有一个value，它是匹配的字符串或子字符串：

```kotlin
val regex = """a([bc]+)d?""".toRegex()
val matchResult = regex.find("abcb abbd")

assertEquals("abcb", matchResult.value)
```

他们有一系列索引来指示匹配的输入部分：

```kotlin
assertEquals(IntRange(0, 3), matchResult.range)
```

### 4.4 分组和解构

我们还可以从MatchResult实例中提取组(匹配的子字符串)。

我们可以将它们作为字符串获取：

```kotlin
assertEquals(listOf("abcb", "bcb"), matchResult.groupValues)
```

或者我们也可以将它们视为由值和范围组成的MatchGroup对象：

```kotlin
assertEquals(IntRange(1, 3), matchResult.groups[1].range)
```

索引为0的组始终是整个匹配的字符串，相反，大于0的索引表示正则表达式中的组，由括号分隔，例如我们示例中的([bc]+)。

我们还可以在赋值语句中解构MatchResult实例：

```kotlin
val regex = """([\w\s]+) is (\d+) years old""".toRegex()
val matchResult = regex.find("Mickey Mouse is 95 years old")
val (name, age) = matchResult!!.destructured

assertEquals("Mickey Mouse", name)
assertEquals("95", age)
```

### 4.5 多匹配

MatchResult还有一个next方法，我们可以使用它来**获取输入String与正则表达式的下一个匹配项(如果有的话)**：

```kotlin
val regex = """a([bc]+)d?""".toRegex()
var matchResult = regex.find("abcb abbd")

assertEquals("abcb", matchResult!!.value)

matchResult = matchResult.next()
assertEquals("abbd", matchResult!!.value)

matchResult = matchResult.next()
assertNull(matchResult)
```

如我们所见，当没有更多匹配项时，next返回null。

## 5. 更换

正则表达式的另一个常见用途是**用其他字符串替换匹配的子字符串**。

为此，我们在标准库中提供了两种现成的方法。

一个是replace，用于替换匹配字符串的所有匹配项：

```kotlin
val regex = """(red|green|blue)""".toRegex()
val beautiful = "Roses are red, Violets are blue"
val grim = regex.replace(beautiful, "dark")

assertEquals("Roses are dark, Violets are dark", grim)
```

另一个replaceFirst用于仅替换第一次出现的情况：

```kotlin
val shiny = regex.replaceFirst(beautiful, "rainbow")

assertEquals("Roses are rainbow, Violets are blue", shiny)
```

### 5.1 复杂的替换

对于更高级的场景，当我们不想用常量字符串替换匹配项，而是想应用转换时，Regex仍然可以提供我们需要的东西。

输入关闭的替换重载：

```kotlin
val reallyBeautiful = regex.replace(beautiful) {
    m -> m.value.toUpperCase() + "!"
}

assertEquals("Roses are RED!, Violets are BLUE!", reallyBeautiful)
```

如我们所见，对于每个匹配项，我们可以使用该匹配项计算一个替换字符串。

## 6. 拆分

最后，我们可能希望**根据正则表达式将字符串拆分为子字符串列表**。同样，Kotlin的Regex为我们提供了帮助：

```kotlin
val regex = """\W+""".toRegex()
val beautiful = "Roses are red, Violets are blue"

assertEquals(listOf(
  "Roses", "are", "red", "Violets", "are", "blue"), regex.split(beautiful))
```

在这里，正则表达式匹配一个或多个非单词字符，因此拆分操作的结果是一个单词列表。

我们还可以限制结果列表的长度：

```kotlin
assertEquals(listOf("Roses", "are", "red", "Violets are blue"), regex.split(beautiful, 4))
```

## 7. Java互操作性

如果我们需要将我们的正则表达式传递给Java代码，或者其他一些需要kotlin.util.regex.Pattern实例的JVM语言API，我们可以简单地转换我们的Regex：

```kotlin
regex.toPattern()
```

## 8. 总结

在本文中，我们研究了Kotlin标准库中的正则表达式支持。

有关详细信息，请参阅[Kotlin参考资料](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)。