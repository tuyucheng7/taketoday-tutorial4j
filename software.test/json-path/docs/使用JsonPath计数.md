## 1. 概述

在本快速教程中，**我们将探讨如何使用JsonPath对JSON文档中的对象和数组进行计数**。

JsonPath提供了一种标准机制来遍历JSON文档的特定部分。我们可以说JsonPath之于JSON就像XPath之于XML。

## 2. 所需依赖

我们使用以下[JsonPath](https://github.com/json-path/JsonPath) Maven依赖项，当然，它在[Maven Central](https://central.sonatype.com/artifact/com.jayway.jsonpath/json-path/2.8.0)上可用：

```xml
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <version>2.4.0</version>
</dependency>
```

## 3. 示例JSON

以下JSON将用于说明示例：

```json
{
    "items": {
        "book": [
            {
                "author": "Arthur Conan Doyle",
                "title": "Sherlock Holmes",
                "price": 8.99
            },
            {
                "author": "J. R. R. Tolkien",
                "title": "The Lord of the Rings",
                "isbn": "0-395-19395-8",
                "price": 22.99
            }
        ],
        "bicycle": {
            "color": "red",
            "price": 19.95
        }
    },
    "url": "mystore.com",
    "owner": "tuyucheng"
}
```

## 4. 统计JSON对象

根元素由美元符号“$”表示。在下面的JUnit测试中，我们使用JSON字符串和我们要计算的JSON路径“$”调用JsonPath.read()：

```java
public void shouldMatchCountOfObjects() {
    Map<String, String> objectMap = JsonPath.read(json, "$");
    assertEquals(3, objectMap.keySet().size());
}
```

通过计算结果Map的大小，我们可以知道JSON结构中给定路径上有多少元素。

## 5. 统计JSON数组大小

在下面的JUnit测试中，我们查询JSON以查找items元素下包含所有book的数组：

```java
public void shouldMatchCountOfArrays() {
    JSONArray jsonArray = JsonPath.read(json, "$.items.book[*]");
    assertEquals(2, jsonArray.size());
}
```

## 6. 总结

在本文中，我们介绍了一些有关如何对JSON结构中的元素进行计数的基本示例。

你可以在[官方JsonPath文档](https://github.com/json-path/JsonPath#path-examples)中探索更多路径示例。