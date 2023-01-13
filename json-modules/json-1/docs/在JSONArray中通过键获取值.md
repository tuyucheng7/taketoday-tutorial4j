## 1. 概述

JSON 是一种轻量级且独立于语言的数据交换格式，用于大多数客户端-服务器通信。

JSONObject和JSONArray是大多数 JSON 处理库中通常可用的两个通用类。JSONObject存储无序键值对，很像JavaMap实现。另一方面，JSONArray是一个有序的值序列，很像Java中的List或Vector 。

在本教程中，我们将使用[JSON-Java](https://stleary.github.io/JSON-java/index.html) ( org.json ) 库并学习如何处理JSONArray以提取给定键的值。如果需要，我们提供了该库[的介绍。](https://www.baeldung.com/java-org-json)

## 2.Maven依赖

我们首先在 POM 中添加以下依赖项：

```xml
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20180813</version>
</dependency>
```

我们总能 在[Maven Central上找到最新版本的](https://search.maven.org/classic/#search|gav|1|g%3A"org.json" AND a%3A"json")JSON-Java。

## 3. 语境构建

JSON 消息通常由 JSON 对象和数组组成，它们可以相互嵌套。JSONArray对象包含在方括号[ ]中，而JSONObject包含在花括号{}中。例如，让我们考虑这个 JSON 消息：

```xml
[
    {
        "name": "John",
        "city": "chicago",
        "age": "22"
    },
    {
        "name": "Gary",
        "city": "florida",
        "age": "35"
    },
    {
        "name": "Selena",
        "city": "vegas",
        "age": "18"
    }
]
```

显然，它是一个 JSON 对象数组。此数组中的每个 JSON 对象代表我们的客户记录，其具有名称、年龄和城市作为其属性或键。

## 4.处理JSONArray

给定上述 JSON，如果我们想找出所有客户的姓名怎么办？换句话说，在我们的示例中给定一个键“name”，我们如何才能在给定的 JSON 数组中找到映射到该键的所有值？

正如我们所知，JSONArray是 JSON 对象的列表。因此，让我们找到给定键的所有值：

```java
public List<String> getValuesForGivenKey(String jsonArrayStr, String key) {
    JSONArray jsonArray = new JSONArray(jsonArrayStr);
    return IntStream.range(0, jsonArray.length())
      .mapToObj(index -> ((JSONObject)jsonArray.get(index)).optString(key))
      .collect(Collectors.toList());
}
```

在前面的例子中：

-   首先，我们遍历 JSON 数组中的整个对象列表
-   然后对于每个JSONObject，我们得到映射到给定键的值

此外，如果不存在这样的键，则optString()方法将返回一个空字符串。

在调用getValuesForGivenKey(jsonArrayStr, “name”)时，jsonArrayStr是我们的示例 JSON，我们将获得所有名称的列表作为输出：

```bash
[John, Gary, Selena]
```

## 5.总结

在这篇快速文章中，我们学习了如何解析JSONArray以获取给定键的所有映射值。在这里，我们使用了JSON-Java (org.json)库。

[JSON.simple](https://code.google.com/archive/p/json-simple/)是在Java中使用 JSON 的另一个类似且强大的替代方案。请随时探索。