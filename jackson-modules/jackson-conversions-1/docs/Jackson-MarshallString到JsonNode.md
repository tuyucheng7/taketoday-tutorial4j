## 1. 概述

本快速教程将展示如何使用 Jackson 2 将 JSON 字符串转换为JsonNode ( com.fasterxml.jackson.databind.JsonNode )。

如果你想更深入地了解并学习你可以使用 Jackson 2 做的其他很酷的事情——请转到[主要的 Jackson 教程](https://www.baeldung.com/jackson)。

## 2.快速解析

非常简单，要解析 JSON 字符串，我们只需要一个ObjectMapper：

```java
@Test
public void whenParsingJsonStringIntoJsonNode_thenCorrect() 
  throws JsonParseException, IOException {
    String jsonString = "{"k1":"v1","k2":"v2"}";

    ObjectMapper mapper = new ObjectMapper();
    JsonNode actualObj = mapper.readTree(jsonString);

    assertNotNull(actualObj);
}
```

## 3. 低级解析

如果出于某种原因，你需要比这更低的级别，则以下示例公开了负责实际解析字符串的JsonParser ：

```java
@Test
public void givenUsingLowLevelApi_whenParsingJsonStringIntoJsonNode_thenCorrect() 
  throws JsonParseException, IOException {
    String jsonString = "{"k1":"v1","k2":"v2"}";

    ObjectMapper mapper = new ObjectMapper();
    JsonFactory factory = mapper.getFactory();
    JsonParser parser = factory.createParser(jsonString);
    JsonNode actualObj = mapper.readTree(parser);

    assertNotNull(actualObj);
}
```

## 4. 使用JsonNode

将 JSON 解析为 JsonNode 对象后，我们可以使用 Jackson JSON 树模型：

```java
@Test
public void givenTheJsonNode_whenRetrievingDataFromId_thenCorrect() 
  throws JsonParseException, IOException {
    String jsonString = "{"k1":"v1","k2":"v2"}";
    ObjectMapper mapper = new ObjectMapper();
    JsonNode actualObj = mapper.readTree(jsonString);

    // When
    JsonNode jsonNode1 = actualObj.get("k1");
    assertThat(jsonNode1.textValue(), equalTo("v1"));
}
```

## 5.总结

本文说明了如何将 JSON 字符串解析为 Jackson JsonNode模型以启用对 JSON 对象的结构化处理。