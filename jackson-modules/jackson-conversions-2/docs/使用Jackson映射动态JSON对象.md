## 1. 简介

使用 Jackson 处理预定义的 JSON 数据结构非常简单。然而，有时我们需要处理 具有未知属性的动态 JSON 对象。

在本快速教程中，我们将学习将动态 JSON 对象映射到Java类的多种方法。

请注意，在所有测试中，我们假设我们有com.fasterxml.jackson.databind.ObjectMapper类型的字段objectMapper。

## 延伸阅读：

## [使用 Jackson 映射嵌套值](https://www.baeldung.com/jackson-nested-values)

了解使用 Jackson 库在Java中反序列化嵌套 JSON 值的三种方法。

[阅读更多](https://www.baeldung.com/jackson-nested-values)→

## [与 Jackson 一起使用 Optional](https://www.baeldung.com/jackson-optional)

快速概述我们如何将 Optional 与 Jackson 一起使用。

[阅读更多](https://www.baeldung.com/jackson-optional)→

## 2.使用JsonNode

假设我们要处理网上商店的产品规格。所有的产品都有一些共同的特性，但它们也有不同的特性，这取决于产品的类型。

例如，我们想知道手机显示屏的纵横比，但这个属性对于鞋子来说意义不大。

数据结构如下所示：

```javascript
{
    "name": "Pear yPhone 72",
    "category": "cellphone",
    "details": {
        "displayAspectRatio": "97:3",
        "audioConnector": "none"
    }
}
```

我们将动态属性存储在详细信息对象中。

我们可以使用以下Java类映射公共属性：

```java
class Product {

    String name;
    String category;

    // standard getters and setters
}
```

最重要的是，我们需要对详细信息对象进行适当的表示。例如，com.fasterxml.jackson.databind.JsonNode可以处理动态键。

要使用它，我们必须将它作为一个字段添加到我们的Product类中：

```java
class Product {

    // common fields

    JsonNode details;

    // standard getters and setters
}
```

最后，我们验证它是否有效：

```java
String json = "<json object>";

Product product = objectMapper.readValue(json, Product.class);

assertThat(product.getName()).isEqualTo("Pear yPhone 72");
assertThat(product.getDetails().get("audioConnector").asText()).isEqualTo("none");
```

但是，此解决方案存在问题；我们的课程依赖于 Jackson 库，因为我们有一个JsonNode字段。

## 3.使用地图

我们可以通过在详细信息字段中使用java.util.Map来解决这个问题。更准确地说，我们必须使用Map<String, Object>。

其他一切都可以保持不变：

```java
class Product {

    // common fields

    Map<String, Object> details;

    // standard getters and setters
}
```

然后我们可以通过测试来验证它：

```java
String json = "<json object>";

Product product = objectMapper.readValue(json, Product.class);

assertThat(product.getName()).isEqualTo("Pear yPhone 72");
assertThat(product.getDetails().get("audioConnector")).isEqualTo("none");
```

## 4.使用@JsonAnySetter

当对象只包含动态属性时，前面的解决方案是不错的选择。但是，有时我们将固定属性和动态属性混合在一个 JSON 对象中。

例如，我们可能需要扁平化我们的产品表示：

```javascript
{
    "name": "Pear yPhone 72",
    "category": "cellphone",
    "displayAspectRatio": "97:3",
    "audioConnector": "none"
}
```

我们可以将这种结构视为一个动态对象。不幸的是，这意味着我们不能定义公共属性，我们也必须动态地处理它们。

或者，我们可以使用@JsonAnySetter来标记用于处理其他未知属性的方法。这样的方法应该接受两个参数，即属性的名称和值：

```java
class Product {

    // common fields

    Map<String, Object> details = new LinkedHashMap<>();

    @JsonAnySetter
    void setDetail(String key, Object value) {
        details.put(key, value);
    }

    // standard getters and setters
}
```

请注意，我们必须实例化详细信息对象以避免NullPointerExceptions。

由于我们将动态属性存储在Map中，我们可以像以前一样使用它：

```java
String json = "<json object>";

Product product = objectMapper.readValue(json, Product.class);

assertThat(product.getName()).isEqualTo("Pear yPhone 72");
assertThat(product.getDetails().get("audioConnector")).isEqualTo("none");
```

## 5. 创建自定义反序列化器

对于大多数情况，这些解决方案工作得很好；然而，有时我们需要更多的控制。例如，我们可以将有关 JSON 对象的反序列化信息存储在数据库中。

我们可以使用自定义反序列化器来针对这些情况。由于这是一个更复杂的主题，我们将在另一篇文章[Getting Started with Custom Deserialization in Jackson 中](https://www.baeldung.com/jackson-deserialization)介绍它。

## 六. 总结

在本文中，我们讨论了使用 Jackson 处理动态 JSON 对象的多种方法。