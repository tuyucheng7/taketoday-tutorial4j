## 1 . 概述

在本文中，我们将研究 Jackson Streaming API。它支持读取和写入，通过使用它，我们可以编写高性能和快速的 JSON 解析器。

另一方面，它使用起来有点困难——JSON 数据的每个细节都需要在代码中明确处理。

## 2.Maven依赖

首先，我们需要向[jackson-core](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")添加一个 Maven 依赖项：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.11.1</version>
</dependency>
```

## 3.写入JSON

我们可以使用[JsonGenerator](https://fasterxml.github.io/jackson-core/javadoc/2.6/com/fasterxml/jackson/core/JsonGenerator.html)类将 JSON 内容直接写入OutputStream 。首先，我们需要创建该对象的实例：

```java
ByteArrayOutputStream stream = new ByteArrayOutputStream();
JsonFactory jfactory = new JsonFactory();
JsonGenerator jGenerator = jfactory
  .createGenerator(stream, JsonEncoding.UTF8);
```

接下来，假设我们要编写具有以下结构的 JSON：

```javascript
{  
   "name":"Tom",
   "age":25,
   "address":[  
      "Poland",
      "5th avenue"
   ]
}
```

我们可以使用JsonGenerator的实例将特定字段直接写入OutputStream：

```java
jGenerator.writeStartObject();
jGenerator.writeStringField("name", "Tom");
jGenerator.writeNumberField("age", 25);
jGenerator.writeFieldName("address");
jGenerator.writeStartArray();
jGenerator.writeString("Poland");
jGenerator.writeString("5th avenue");
jGenerator.writeEndArray();
jGenerator.writeEndObject();
jGenerator.close();
```

要检查是否创建了正确的 JSON，我们可以创建一个包含 JSON 对象的String对象：

```java
String json = new String(stream.toByteArray(), "UTF-8");
assertEquals(
  json, 
  "{"name":"Tom","age":25,"address":["Poland","5th avenue"]}");
```

## 4.解析JSON

当我们得到一个 JSON字符串作为输入时，我们想从中提取特定的字段，可以使用[JsonParser类：](https://fasterxml.github.io/jackson-core/javadoc/2.6/com/fasterxml/jackson/core/JsonParser.html)

```java
String json
  = "{"name":"Tom","age":25,"address":["Poland","5th avenue"]}";
JsonFactory jfactory = new JsonFactory();
JsonParser jParser = jfactory.createParser(json);

String parsedName = null;
Integer parsedAge = null;
List<String> addresses = new LinkedList<>();
```

我们想从输入的 JSON中获取parsedName、parsedAge 和 addresses字段。为此，我们需要处理低级解析逻辑并自行实现：

```java
while (jParser.nextToken() != JsonToken.END_OBJECT) {
    String fieldname = jParser.getCurrentName();
    if ("name".equals(fieldname)) {
        jParser.nextToken();
        parsedName = jParser.getText();
    }

    if ("age".equals(fieldname)) {
        jParser.nextToken();
        parsedAge = jParser.getIntValue();
    }

    if ("address".equals(fieldname)) {
        jParser.nextToken();
        while (jParser.nextToken() != JsonToken.END_ARRAY) {
            addresses.add(jParser.getText());
        }
    }
}
jParser.close();
```

根据字段名称，我们将其提取并分配给适当的字段。解析文档后，所有字段都应该有正确的数据：

```java
assertEquals(parsedName, "Tom");
assertEquals(parsedAge, (Integer) 25);
assertEquals(addresses, Arrays.asList("Poland", "5th avenue"));
```

## 5.提取JSON部分

有时，当我们解析 JSON 文档时，我们只对一个特定字段感兴趣。

理想情况下，在这些情况下，我们只想解析文档的开头，一旦找到所需的字段，我们就可以中止处理。

假设我们只对输入 JSON的年龄字段感兴趣。在这种情况下，我们可以实现解析逻辑以在找到所需字段后停止解析：

```java
while (jParser.nextToken() != JsonToken.END_OBJECT) {
    String fieldname = jParser.getCurrentName();

    if ("age".equals(fieldname)) {
        jParser.nextToken();
        parsedAge = jParser.getIntValue();
        return;
    }

}
jParser.close();
```

处理后，唯一的parsedAge字段将有一个值：

```java
assertNull(parsedName);
assertEquals(parsedAge, (Integer) 25);
assertTrue(addresses.isEmpty());
```

多亏了这一点，JSON 文档的解析会快很多，因为我们不需要阅读整个文档，只需要阅读其中的一小部分。

## 六. 总结

在这篇简短的文章中，我们正在研究如何利用 Jackson 的流处理 API。