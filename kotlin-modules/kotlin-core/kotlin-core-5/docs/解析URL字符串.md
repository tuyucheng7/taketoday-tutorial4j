## 1. 概述

在本教程中，我们将展示如何使用URL和URI类解析URL字符串；此外，我们还将解释如何使用Regex类从URL中提取参数。

## 2. 使用JDK URL类解析

解析URL是许多应用程序中的典型案例，由于URL字符串包含特殊字符，因此对其进行解析并非易事。在这里，我们将介绍如何从给定的URL字符串中提取参数。

首先我们使用JDK提供的[URL类](https://www.baeldung.com/java-url)，为此，**我们将扩展我们的测试类并为URL类添加一个扩展函数**：

```kotlin
private fun URL.findParameterValue(parameterName: String): String? {
    return query.split('&').map {
        val parts = it.split('=')
        val name = parts.firstOrNull() ?: ""
        val value = parts.drop(1).firstOrNull() ?: ""
        Pair(name, value)
    }.firstOrNull { it.first == parameterName }?.second
}
```

**该函数解析query对象并提取给定名称的值**，澄清一下，该函数不进行验证。这是一个如何提取参数值的简单示例，它按参数名称拆分所有提供的参数和过滤器。

之后，我们来测试一下：

```kotlin
@Test
fun `given url when parsed with URL class returns user parameter`() {
    val url = URL(urlToParse)
    val userNameFromUrl = url.findParameterValue("user")
    assertThat(userNameFromUrl).isEqualTo("Bob")
    assertThat(url.protocol).isEqualTo("https")
    assertThat(url.host).isEqualTo("www.tuyucheng.com")
}
```

我们证明了findParameterValue函数可以正常工作，此外，我们还测试了URL类提供的另外两个值。

## 3. 使用URI类

之后，让我们看一下[URI](https://www.baeldung.com/java-url-vs-uri#uri-and-url)类，它提供了与URL类类似的API。此外，我们可以像前面的例子一样创建一个扩展函数：

```kotlin
private fun URI.findParameterValue(parameterName: String): String? {
    return rawQuery.split('&').map {
        val parts = it.split('=')
        val name = parts.firstOrNull() ?: ""
        val value = parts.drop(1).firstOrNull() ?: ""
        Pair(name, value)
    }.firstOrNull { it.first == parameterName }?.second
}
```

之后，让我们在测试中验证一下：

```kotlin
@Test
fun `given url when parsed with URI class returns user parameter`() {
    val uri = URI(urlToParse)
    val userNameFromUrl = uri.findParameterValue("user")
    assertThat(userNameFromUrl).isEqualTo("Bob")
    assertThat(uri.scheme).isEqualTo("https")
    assertThat(uri.host).isEqualTo("www.tuyucheng.com")
}
```

与前面的示例一样，该函数找到了正确的参数值。URI类比URL类更通用，而且它也提供了主机和协议。

## 4. 使用Regex类提取参数

现在，让我们只使用内置的[Regex](https://www.baeldung.com/kotlin/regular-expressions)类从URL中提取参数：

```kotlin
class ParseUrlTest {

    companion object {
        private const val urlToParse = "https://www.tuyucheng.com/?reqNo=3&user=Bob&age=12"
        private val REGEX_PATTERN = """https://www.tuyucheng.com/?.*[?&]user=([^#&]+).*""".toRegex()
    }

    @Test
    fun `given url when parsed should return user parameter`() {
        val userNameFromUrl = REGEX_PATTERN.matchEntire(urlToParse)?.groups?.get(1)?.value
        assertThat(userNameFromUrl).isEqualTo("Bob")
    }
}
```

首先，我们在伴生对象中创建了要测试的模式和字符串，我们在其他参数中间定义了用户参数。此外，正则表达式在参数之前处理可选的“/”字符。然后，**我们使用扩展方法来创建Regex对象**；除此之外，也可以使用构造函数初始化Regex对象。

之后，我们测试了我们的正则表达式，matchEntire方法检查整个字符串是否与模式匹配，我们从match中取出第二组，因为索引为零的组对应于整个match。

## 5. 总结

在这篇简短的文章中，我们了解了如何使用URL和URI类解析URL字符串；此外，我们演示了如何使用内置的Regex类提取参数。