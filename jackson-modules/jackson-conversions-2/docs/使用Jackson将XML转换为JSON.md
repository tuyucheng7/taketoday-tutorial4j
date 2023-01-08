## 1. 概述

在本教程中，我们将了解如何使用 Jackson 将 XML 消息转换为 JSON。

对于 Jackson 的新读者，请考虑先熟悉 [基础知识](https://www.baeldung.com/jackson)。

## 2. 杰克逊简介

我们可以考虑使用 Jackson 以三种不同的方式解析 JSON：

-   第一个也是最常见的是使用[ObjectMapper进行数据绑定](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/ObjectMapper.html)
-   第二个是使用[TreeTraversingParser](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/node/TreeTraversingParser.html)和[JsonNode映射到树数据结构](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/JsonNode.html)
-   第三个是使用 [JsonParser](https://fasterxml.github.io/jackson-core/javadoc/2.9/com/fasterxml/jackson/core/JsonParser.html)和[JsonGenerator按令牌流式传输树数据结构](https://fasterxml.github.io/jackson-core/javadoc/2.9/com/fasterxml/jackson/core/JsonGenerator.html?is-external=true)

现在，Jackson 还支持前两种 XML 数据。因此，让我们看看 Jackson 如何帮助我们完成从一种格式到另一种格式的转换。

## 3.依赖关系

首先，我们需要将 [jackson-databind](https://search.maven.org/classic/#search|ga|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind") 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.3</version>
</dependency>
```

这个库将允许我们使用数据绑定 API。

第二个是 [jackson-dataformat-xml](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.dataformat" AND a%3A"jackson-dataformat-xml") ，它添加了 Jackson 的 XML 支持：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.13.3</version>
</dependency>
```

## 4.数据绑定

数据绑定，简单的说就是我们想把序列化的数据直接映射到一个Java对象上。

为了探索这一点，让我们用Flower 和 Color 属性定义我们的 XML ：

```xml
<Flower>
    <name>Poppy</name>
    <color>RED</color>
    <petals>9</petals>
</Flower>

```

这类似于此Java表示法：

```java
public class Flower {
    private String name;
    private Color color;
    private Integer petals;
    // getters and setters
}

public enum Color { PINK, BLUE, YELLOW, RED; }
```

我们的第一步是将 XML 解析为 Flower实例。为此，让我们创建一个XmlMapper的实例，Jackson 的 ObjectMapper XML 等价物并使用它的 readValue 方法：

```java
XmlMapper xmlMapper = new XmlMapper();
Flower poppy = xmlMapper.readValue(xml, Flower.class);
```

一旦我们有了Flower实例，我们就想使用熟悉的 ObjectMapper将它写成 JSON ：

```java
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(poppy);
```

结果，我们得到了等效的 JSON：

```xml
{
    "name":"Poppy",
    "color":"RED",
    "petals":9
}
```

## 5.树遍历

有时，直接查看树结构可以提供更大的灵活性，例如在我们不想维护中间类或者我们只想转换结构的一部分的情况下。

但是，正如我们将要看到的，它需要进行一些权衡。

第一步类似于我们使用数据绑定时的第一步。不过这一次，我们将使用readTree方法：

```java
XmlMapper xmlMapper = new XmlMapper();
JsonNode node = xmlMapper.readTree(xml.getBytes());
```

完成此操作后，我们将有一个JsonNode，它有 3 个孩子，正如我们预期的那样：name、color和petals。

然后，我们可以再次使用ObjectMapper，只需发送我们的JsonNode 即可 ：

```java
ObjectMapper jsonMapper = new ObjectMapper();
String json = jsonMapper.writeValueAsString(node);
```

现在，与我们上一个示例相比，结果略有不同：

```xml
{
    "name":"Poppy",
    "color":"RED",
    "petals":"9"
}
```

仔细观察，发现petals属性被序列化为字符串，而不是数字！ 这是因为readTree不会在没有明确定义的情况下推断数据类型。

### 5.1. 限制

而且，Jackson 的 XML 树遍历支持存在某些限制：

-   Jackson 无法区分对象和数组。 由于 XML 缺少将对象与对象列表区分开来的本机结构，Jackson 将简单地将重复的元素整理成单个值。
-   而且，由于 Jackson 想要将每个 XML 元素映射到一个 JSON 节点，因此它不支持混合内容。

由于这些原因，[官方 Jackson 文档建议不要使用 Tree 模型来解析 XML](https://github.com/FasterXML/jackson-dataformat-xml#known-limitations)。

## 6.内存限制

现在，这两者都有一个显着的缺点，即整个 XML 需要立即在内存中才能执行转换。 在 Jackson 支持将树结构作为标记流式传输之前，我们将受制于此约束，或者我们需要考虑使用 [XMLStreamReader](https://docs.oracle.com/en/java/javase/11/docs/api/java.xml/javax/xml/stream/XMLStreamReader.html)之类的东西来滚动我们自己的结构。

## 七. 总结

在本教程中，我们简要了解了 Jackson 读取 XML 数据并将其写入 JSON 的不同方式。此外，我们快速浏览了每种受支持方法的局限性。