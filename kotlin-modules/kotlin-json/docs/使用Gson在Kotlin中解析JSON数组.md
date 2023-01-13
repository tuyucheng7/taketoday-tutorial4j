## 1. 概述

Google Gson 是处理 JSON 对象最流行的库之一，许多流行的编程语言都支持它，包括 Kotlin。

在本教程中，我们将探讨如何使用 Gson 通过 Kotlin 序列化和反序列化 JSON 数组。

## 2.谷歌GSON

在 Google Gson 的上下文中，[序列化](https://www.baeldung.com/gson-string-to-jsonobject)是一种将对象的状态转换为 JSON 表示的机制。另一方面，[反序列](https://www.baeldung.com/gson-deserialization-guide)化是相反的过程，其中 JSON 用于在内存中重新创建实际对象。

Gson 通过为 Kotlin 提供 JSON 数据绑定支持来帮助我们处理这些过程，它是管理复杂数据类型的优秀工具。

### 2.1. Maven 依赖

首先，我们需要将[Gson](https://search.maven.org/search?q=g:com.google.code.gson a:gson) Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.8</version>
</dependency>
```

## 3.实施

对于我们的案例研究，我们将考虑作者和文章的示例。一个作者可以有多篇文章，一篇文章只能属于一个作者。我们将利用 Kotlin 中的数据类来实现[序列化/反序列化](https://www.baeldung.com/kotlin/json-convert-data-class)过程。

让我们首先创建我们将使用 Gson 解析的Article和Author类：

```kotlin
data class Article(
    var title: String,
    var category: String,
    var views: Int
) {
}
data class Author(
    var name: String,
    var type: String? = null,
    var articles: List<Article>? = null
) {
}
```

### 3.1. 数组序列化

Gson 具有自动将数组或列表解析为 JSON 对象的内置功能，反之亦然。例如，我们可以定义一个仅包含名称的Author对象列表，并将其序列化为一个 JSON Array对象：

```kotlin
@Test
fun serializeObjectListTest() {
    val authors = listOf(
      Author("John", "Technical Author"),
      Author("Jane", "Technical Author"),
      Author("William", "Technical Editor")
    )
    val serialized = Gson().toJson(authors)

    val json =
      """[{"name":"John","type":"Technical Author"},{"name":"Jane","type":"Technical Author"},{"name":"William","type":"Technical Editor"}]"""
    assertEquals(serialized, json)
}
```

Gson 还提供了使用不匹配的 JSON 键和对象字段名称序列化数组的能力。例如，我们可以使用不同的名称序列化文章列表：

```kotlin
data class Author(
    var name: String,
    var type: String? = null,
    @SerializedName("author_articles")
    var articles: List<Article>? = null,
) {
}
```

结果，文章数组将使用不同的名称序列化到生成的 JSON 中：

```kotlin
@Test
fun serializeObjectListWithNonMatchingKeysTest() {
    val authors = listOf(
        Author(
          "John",
          "Technical Author",
          listOf(Article("Streams in Java", "Java", 3), Article("Lambda Expressions", "Java", 5))
        ),
        Author("Jane", "Technical Author", listOf(Article("Functional Interfaces", "Java", 2))),
        Author("William", "Technical Editor")
    )
    val serialized = Gson().toJson(authors)

    val json =
      """[{"name":"John","type":"Technical Author","author_articles":[{"title":"Streams in Java","category":"Java","views":3},{"title":"Lambda Expressions","category":"Java","views":5}]},{"name":"Jane","type":"Technical Author","author_articles":[{"title":"Functional Interfaces","category":"Java","views":2}]},{"name":"William","type":"Technical Editor"}]"""
    assertEquals(serialized, json)
}
```

### 3.2. 数组反序列化

使用 Gson 反序列化 JSON 数组需要数组中所有元素的通用类型表示。因此，我们需要调用TokenType构造函数并封装 Gson 将评估的对象：

```kotlin
@Test
fun deserializeObjectListTest() {
    val json =
      """[{"name":"John","type":"Technical Author"},{"name":"Jane","type":"Technical Author"},{"name":"William","type":"Technical Editor"}]"""
    val typeToken = object : TypeToken<List>() {}.type
    val authors = Gson().fromJson<List>(json, typeToken)

    assertThat(authors).isNotEmpty
    assertThat(authors).hasSize(3)
    assertThat(authors).anyMatch { a -> a.name == "John" }
    assertThat(authors).anyMatch { a -> a.type == "Technical Editor" }
}
```

需要注意的是，默认情况下， Gson 将为 JSON 结果对象中缺失的字段设置空值。因此，在像[Jackson这样的其他](https://www.baeldung.com/kotlin/jackson-kotlin)[库](https://www.baeldung.com/jackson-vs-gson)中，我们需要期望即使对于具有默认值的类也会有这种行为。

在这里，我们的 JSON 字符串缺少数组中所有元素的类型字段：

```kotlin
@Test
fun deserializeObjectListWithMissingFieldsTest() {
    val json =
      """[{"name":"John"},{"name":"Jane"},{"name":"William"}]"""
    val typeToken = object : TypeToken<List<Author>>() {}.type
    val authors = Gson().fromJson<List<Author>>(json, typeToken)

    assertThat(authors).isNotEmpty
    assertThat(authors).hasSize(3)
    assertThat(authors).anyMatch { a -> a.name == "John" }
    assertThat(authors).allMatch { a -> a.type == null }
}
```

## 4。总结

在本教程中，我们探讨了如何使用 Google Gson 库通过 Kotlin 序列化和反序列化 JSON 数组。我们还了解了如何管理缺少字段的 JSON 数组的解析，以及如何指定要用于 JSON 数组的自定义键名称。