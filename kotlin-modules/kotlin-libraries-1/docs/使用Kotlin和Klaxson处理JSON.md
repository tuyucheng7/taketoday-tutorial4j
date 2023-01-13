## 1. 概述

[Klaxon是我们可以用来在](https://github.com/cbeust/klaxon)[Kotlin](https://www.baeldung.com/kotlin)中解析 JSON 的开源库之一。

在本教程中，我们将了解它的功能。

## 2.Maven依赖

首先，我们需要将库依赖项添加到我们的 Maven 项目中：

```xml
<dependency>
    <groupId>com.beust</groupId>
    <artifactId>klaxon</artifactId>
    <version>3.0.4</version>
</dependency>
```

最新版本可以在 [jcenter](https://bintray.com/cbeust/maven/klaxon) 或 [Spring Plugins Repository](https://repo.spring.io/plugins-release/com/beust/klaxon/)中找到。

## 3. API 特点

Klaxon 有四个 API 来处理 JSON 文档。我们将在以下部分中探讨这些内容。

## 4.对象绑定API

使用此 API， 我们可以将 JSON 文档绑定到 Kotlin 对象，反之亦然。
首先，让我们定义以下 JSON 文档：

```javascript
{
    "name": "HDD"
}
```

接下来，我们将创建 用于绑定的Product类：

```java
class Product(val name: String)
```

现在，我们可以测试序列化：

```java
@Test
fun givenProduct_whenSerialize_thenGetJsonString() {
    val product = Product("HDD")
    val result = Klaxon().toJsonString(product)

    assertThat(result).isEqualTo("""{"name" : "HDD"}""")
}
```

我们可以测试反序列化：

```java
@Test
fun givenJsonString_whenDeserialize_thenGetProduct() {
    val result = Klaxon().parse<Product>(
    """
        {
            "name" : "RAM"
        }
    """)

    assertThat(result?.name).isEqualTo("RAM")
}
```

此 API 还支持使用数据类以及可变和不可变类。

Klaxon 允许我们使用@Json注解自定义映射过程。这个注解有两个属性：

-   名称 - 为字段设置不同的名称
-   ignored – 用于忽略映射过程的字段

让我们创建一个CustomProduct类，看看它们是如何工作的：

```java
class CustomProduct(
    @Json(name = "productName")
    val name: String,
    @Json(ignored = true)
    val id: Int)
```

现在，让我们通过测试来验证它：

```java
@Test
fun givenCustomProduct_whenSerialize_thenGetJsonString() {
    val product = CustomProduct("HDD", 1)
    val result = Klaxon().toJsonString(product)

    assertThat(result).isEqualTo("""{"productName" : "HDD"}""")
}
```

如我们所见，name属性被序列化为productName，而id属性被忽略。

## 5. 流式 API

使用 Streaming API，我们可以通过从流中读取来处理巨大的 JSON 文档。此功能允许我们的代码在读取时处理 JSON 值。

我们需要使用 API 中的JsonReader 类来读取 JSON 流。这个类有两个特殊的函数来处理流：

-   beginObject() – 确保下一个标记是对象的开始
-   beginArray() – 确保下一个标记是数组的开头

使用这些函数，我们可以确保流被正确定位并且在使用对象或数组后它被关闭。

让我们针对以下ProductData类的数组测试流式 API ：

```java
data class ProductData(val name: String, val capacityInGb: Int)
@Test
fun givenJsonArray_whenStreaming_thenGetProductArray() {
    val jsonArray = """
    [
        { "name" : "HDD", "capacityInGb" : 512 },
        { "name" : "RAM", "capacityInGb" : 16 }
    ]"""
    val expectedArray = arrayListOf(
      ProductData("HDD", 512),
      ProductData("RAM", 16))
    val klaxon = Klaxon()
    val productArray = arrayListOf<ProductData>()
    JsonReader(StringReader(jsonArray)).use { 
        reader -> reader.beginArray {
            while (reader.hasNext()) {
                val product = klaxon.parse<ProductData>(reader)
                productArray.add(product!!)
            }
        }
    }

    assertThat(productArray).hasSize(2).isEqualTo(expectedArray)
}
```

## 6. JSON路径查询接口

Klaxon 支持 JSON 路径规范中的元素定位功能。使用此 API，我们可以定义路径匹配器来定位文档中的特定条目。

请注意，此 API 也是流式传输的，我们将在找到并解析元素后收到通知。

我们需要使用 PathMatcher接口。当 JSON 路径找到正则表达式的匹配项时，将调用此接口。

要使用它，我们需要实现它的方法：

-   pathMatches() – 如果我们想观察这条路径，则返回 true
-   onMatch() ——找到路径时触发；请注意，该值只能是基本类型(例如 int、String)，而不能是JsonObject或JsonArray

让我们进行测试以查看其实际效果。

首先，让我们定义一个清单 JSON 文档作为数据源：

```javascript
{
    "inventory" : {
        "disks" : [
            {
                "type" : "HDD",
                "sizeInGb" : 1000
            },
            {
                "type" : "SDD",
                "sizeInGb" : 512
            }
        ]
    }
}
```

现在，我们实现PathMatcher接口如下：

```java
val pathMatcher = object : PathMatcher {
    override fun pathMatches(path: String)
      = Pattern.matches(".inventory.disks.type.", path)

    override fun onMatch(path: String, value: Any) {
        when (path) {
            "$.inventory.disks[0].type"
              -> assertThat(value).isEqualTo("HDD")
            "$.inventory.disks[1].type"
              -> assertThat(value).isEqualTo("SDD")
        }
    }
}
```

请注意，我们定义了正则表达式以匹配我们的清单文档的磁盘类型。

现在，我们准备定义我们的测试：

```java
@Test
fun givenDiskInventory_whenRegexMatches_thenGetTypes() {
    val jsonString = """..."""
    val pathMatcher = //...
    Klaxon().pathMatcher(pathMatcher)
      .parseJsonObject(StringReader(jsonString))
}
```

## 7.低级API

使用 Klaxon，我们可以处理 JSON 文档，例如 Map或List。 为此，我们可以使用 API 中的类JsonObject和JsonArray。

让我们进行测试以查看JsonObject的运行情况：

```java
@Test
fun givenJsonString_whenParser_thenGetJsonObject() {
    val jsonString = StringBuilder("""
        {
            "name" : "HDD",
            "capacityInGb" : 512,
            "sizeInInch" : 2.5
        }
    """)
    val parser = Parser()
    val json = parser.parse(jsonString) as JsonObject

    assertThat(json)
      .hasSize(3)
      .containsEntry("name", "HDD")
      .containsEntry("capacityInGb", 512)
      .containsEntry("sizeInInch", 2.5)
}
```

现在，让我们测试一下JsonArray的功能：

```java
@Test
fun givenJsonStringArray_whenParser_thenGetJsonArray() {
    val jsonString = StringBuilder("""
    [
        { "name" : "SDD" },
        { "madeIn" : "Taiwan" },
        { "warrantyInYears" : 5 }
    ]""")
    val parser = Parser()
    val json = parser.parse(jsonString) as JsonArray<JsonObject>

    assertSoftly({
        softly ->
            softly.assertThat(json).hasSize(3)
            softly.assertThat(json[0]["name"]).isEqualTo("SDD")
            softly.assertThat(json[1]["madeIn"]).isEqualTo("Taiwan")
            softly.assertThat(json[2]["warrantyInYears"]).isEqualTo(5)
    })
}
```

正如我们在这两种情况下看到的那样，我们在没有定义特定类的情况下进行了转换。

## 八、总结

在本文中，我们探讨了 Klaxon 库及其用于处理 JSON 文档的 API。