## 1. 简介

在本快速教程中，我们将创建能够从任何 URL 读取 JSON 数据的方法。我们将从核心Java类开始。然后，我们将使用一些库来简化我们的代码。

## 2. 使用核心Java类

在Java中从 URL 读取数据的最简单方法之一是使用URL类。要使用它，我们打开一个到URL的输入流，创建一个输入流读取器，然后读取所有字符。我们会将这些字符附加到StringBuilder中，然后将其作为String返回：

```java
public static String stream(URL url) {
    try (InputStream input = url.openStream()) {
        InputStreamReader isr = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder json = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            json.append((char) c);
        }
        return json.toString();
    }
}
```

因此，代码包含大量样板文件。此外，如果我们想将[JSON](https://www.baeldung.com/java-json) 转换为地图或[POJO](https://www.baeldung.com/java-pojo-class) ，还需要更多代码。即使使用新的Java11 [HttpClient](https://baeldung.com/java-9-http-client)，一个简单的 GET 请求也有很多代码。此外，它无助于将响应从字符串转换为 POJO。所以，让我们探索更简单的方法来做到这一点。

## 3. 使用 commons-io 和 org.json

一个非常流行的库是[Apache Commons IO](https://www.baeldung.com/apache-commons-io)。我们将使用IOUtils读取 URL 并返回一个String。然后，要将其转换为JSONObject，我们将使用[JSON-Java (org.json)](https://www.baeldung.com/java-org-json)库。[这是来自json.org](https://json.org/)的Java参考实现。让我们将它们组合成一个新方法：

```java
public static JSONObject getJson(URL url) {
    String json = IOUtils.toString(url, Charset.forName("UTF-8"));
    return new JSONObject(json);
}
```

使用JSONObject，我们可以为任何属性调用get()并获得一个Object。特定类型有类似命名的方法。例如：

```java
jsonObject.getString("stringProperty");
```

## 4. 使用 Jackson 和ObjectMapper减少代码

有许多将 JSON 转换为 POJO 的解决方案，反之亦然。但是，[Jackson](https://www.baeldung.com/jackson)广泛用于[Jersey](https://www.baeldung.com/jersey-rest-api-with-spring)和其他[JAX-RS](https://www.baeldung.com/jax-rs-spec-and-implementations)实现等项目中。让我们将我们需要的依赖添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.3</version>
</dependency>
```

有了这个，我们不仅可以毫不费力地从 URL 中读取 JSON，还可以同时将其转换为 POJO。

### 4.1. 反序列化为通用对象

Jackson 中的大部分动作都来自[ObjectMapper](https://www.baeldung.com/jackson-object-mapper-tutorial)。ObjectMapper最常见的场景是给它一个String输入并取回一个对象。幸运的是，ObjectMapper还可以直接从 Internet URL 读取输入：

```java
public static JsonNode get(URL url) {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(url);
}
```

使用readTree()，我们得到一个JsonNode，它是一个[树状](https://www.baeldung.com/java-binary-tree)结构。我们使用它的get()方法读取属性：

```java
json.get("propertyName");
```

因此，如果我们不想的话，我们不需要将我们的响应映射到特定的类。

### 4.2. 反序列化为自定义类

但是，对于更复杂的对象，创建一个表示我们期望的 JSON 结构的类会很有帮助。我们可以使用[泛型](https://www.baeldung.com/java-generics)来创建我们的方法的一个版本，该方法能够将响应映射到我们想要的任何类readValue()：

```java
public static <T> T get(URL url, Class<T> type) {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(url, type);
}
```

然后，只要我们对象的属性和结构匹配，我们就会得到一个新实例，其中填充了来自 JSON 响应的值。

## 5.总结

在本文中，我们学习了如何向 URL 发出请求并返回 JSON 字符串。然后，我们使用了一些库来简化我们的代码。最后，我们在几行中读取 JSON 响应并将其映射到 POJO。