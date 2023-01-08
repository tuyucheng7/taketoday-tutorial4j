## 1. 概述

在本教程中，我们将了解 Jackson 2.x 的解组过程，特别是如何处理具有未知属性的 JSON 内容。

要更深入地了解我们可以用 Jackson 做的其他很酷的事情，我们可以查看[主要的 Jackson 教程](https://www.baeldung.com/jackson)。

## 2. 解组带有附加/未知字段的 JSON

JSON 输入有各种形状和大小，大多数时候，我们需要将它映射到具有一定数量字段的预定义Java对象。目标是简单地忽略任何不能映射到现有Java字段的 JSON 属性。

例如，假设我们需要将 JSON 解组为以下Java实体：

```java
public class MyDto {

    private String stringValue;
    private int intValue;
    private boolean booleanValue;

    // standard constructor, getters and setters 
}
```

### 2.1. 未知字段上的UnrecognizedPropertyException

尝试将具有未知属性的 JSON 解组到这个简单的Java实体将导致com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException：

```java
@Test(expected = UnrecognizedPropertyException.class)
public void givenJsonHasUnknownValues_whenDeserializing_thenException()
  throws JsonParseException, JsonMappingException, IOException {
    String jsonAsString = 
        "{"stringValue":"a"," +
        ""intValue":1," +
        ""booleanValue":true," +
        ""stringValue2":"something"}";
    ObjectMapper mapper = new ObjectMapper();

    MyDto readValue = mapper.readValue(jsonAsString, MyDto.class);

    assertNotNull(readValue);
}
```

这将失败并出现以下异常：

```bash
com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: 
Unrecognized field "stringValue2" (class org.baeldung.jackson.ignore.MyDto), 
not marked as ignorable (3 known properties: "stringValue", "booleanValue", "intValue"])
```

### 2.2. 使用ObjectMapper处理未知字段

我们现在可以配置完整的ObjectMapper以忽略 JSON 中的未知属性：

```java
new ObjectMapper()
  .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
```

然后我们应该能够将这种 JSON 读入预定义的Java实体中：

```java
@Test
public void givenJsonHasUnknownValuesButJacksonIsIgnoringUnknowns_whenDeserializing_thenCorrect()
  throws JsonParseException, JsonMappingException, IOException {
 
    String jsonAsString = 
        "{"stringValue":"a"," +
        ""intValue":1," +
        ""booleanValue":true," +
        ""stringValue2":"something"}";
    ObjectMapper mapper = new ObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    MyDto readValue = mapper.readValue(jsonAsString, MyDto.class);

    assertNotNull(readValue);
    assertThat(readValue.getStringValue(), equalTo("a"));
    assertThat(readValue.isBooleanValue(), equalTo(true));
    assertThat(readValue.getIntValue(), equalTo(1));
}
```

### 2.3. 在类级别处理未知字段

我们还可以将单个类标记为接受未知字段，而不是整个 Jackson ObjectMapper：

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class MyDtoIgnoreUnknown { ... }
```

现在我们应该能够测试与以前相同的行为。未知字段被忽略，只映射已知字段：

```java
@Test
public void givenJsonHasUnknownValuesButIgnoredOnClass_whenDeserializing_thenCorrect() 
  throws JsonParseException, JsonMappingException, IOException {
 
    String jsonAsString =
        "{"stringValue":"a"," +
        ""intValue":1," +
        ""booleanValue":true," +
        ""stringValue2":"something"}";
    ObjectMapper mapper = new ObjectMapper();

    MyDtoIgnoreUnknown readValue = mapper
      .readValue(jsonAsString, MyDtoIgnoreUnknown.class);

    assertNotNull(readValue);
    assertThat(readValue.getStringValue(), equalTo("a"));
    assertThat(readValue.isBooleanValue(), equalTo(true));
    assertThat(readValue.getIntValue(), equalTo(1));
}
```

## 3.解组不完整的JSON

与其他未知字段类似，解组不完整的 JSON(不包含Java类中的所有字段的 JSON)对 Jackson 来说不是问题：

```java
@Test
public void givenNotAllFieldsHaveValuesInJson_whenDeserializingAJsonToAClass_thenCorrect() 
  throws JsonParseException, JsonMappingException, IOException {
    String jsonAsString = "{"stringValue":"a","booleanValue":true}";
    ObjectMapper mapper = new ObjectMapper();

    MyDto readValue = mapper.readValue(jsonAsString, MyDto.class);

    assertNotNull(readValue);
    assertThat(readValue.getStringValue(), equalTo("a"));
    assertThat(readValue.isBooleanValue(), equalTo(true));
}
```

## 4. 总结

在本文中，我们讨论了使用 Jackson 反序列化具有额外未知属性的 JSON。

这是使用 Jackson 时最常见的配置之一，因为我们经常需要将外部 REST API 的 JSON 结果映射到 API 实体的内部Java表示。