## 1. 概述

在本快速教程中，我们将探讨如何使用 JsonPath 对 JSON 文档中的对象和数组进行计数。

JsonPath 提供了一种标准机制来遍历 JSON 文档的特定部分。我们可以说 JsonPath 之于 JSON 就像 XPath 之于 XML。

## 2. 所需依赖

我们正在使用以下[JsonPath](https://github.com/json-path/JsonPath) Maven 依赖项，当然，它在[Maven Central](https://search.maven.org/classic/#search|ga|1|g%3A"com.jayway.jsonpath")上可用：

```xml
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <version>2.4.0</version>
</dependency>
```

## 3. 示例 JSON

以下 JSON 将用于说明示例：

```javascript
{
    "items":{
        "book":[
            {
                "author":"Arthur Conan Doyle",
                "title":"Sherlock Holmes",
                "price":8.99
            },
            {
                "author":"J. R. R. Tolkien",
                "title":"The Lord of the Rings",
                "isbn":"0-395-19395-8",
                "price":22.99
            }
        ],
        "bicycle":{
            "color":"red",
            "price":19.95
        }
    },
    "url":"mystore.com",
    "owner":"baeldung"
}
```

## 4. 统计 JSON 对象

根元素由美元符号“ $”表示。在下面的 JUnit 测试中，我们 使用 JSON字符串和我们要计算的 JSON 路径“$”调用JsonPath.read() ：

```java
public void shouldMatchCountOfObjects() {
    Map<String, String> objectMap = JsonPath.read(json, "$");
    assertEquals(3, objectMap.keySet().size());
}
```

通过计算生成的Map 的大小，我们可以知道 JSON 结构中给定路径上有多少元素。

## 5. 统计 JSON 数组大小

在下面的 JUnit 测试中，我们查询 JSON 以查找包含items元素下所有书籍的数组：

```java
public void shouldMatchCountOfArrays() {
    JSONArray jsonArray = JsonPath.read(json, "$.items.book[]");
    assertEquals(2, jsonArray.size());
}
```

## 六. 总结

在本文中，我们介绍了一些有关如何对 JSON 结构中的项目进行计数的基本示例。

[你可以在官方 JsonPath 文档](https://github.com/json-path/JsonPath#path-examples)中探索更多路径示例。