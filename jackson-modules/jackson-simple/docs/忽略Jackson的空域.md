## 1. 概述

本快速教程将介绍如何设置Jackson 以在序列化Java 类时忽略空字段。

如果我们想更深入地挖掘并学习与 Jackson 2 相关的其他有趣的事情，我们可以继续[阅读主要的 Jackson 教程](https://www.baeldung.com/jackson)。

## 延伸阅读：

## [杰克逊 - 更改字段名称](https://www.baeldung.com/jackson-name-of-property)

Jackson - 更改字段名称以符合特定的 JSON 格式。

[阅读更多](https://www.baeldung.com/jackson-name-of-property)→

## [Jackson——决定哪些字段被序列化/反序列化](https://www.baeldung.com/jackson-field-serializable-deserializable-or-not)

如何控制哪些字段被 Jackson 序列化/反序列化以及哪些字段被忽略。

[阅读更多](https://www.baeldung.com/jackson-field-serializable-deserializable-or-not)→

## 2.忽略类中的空字段

Jackson 允许我们在类级别控制此行为：

```java
@JsonInclude(Include.NON_NULL)
public class MyDto { ... }
```

或者在字段级别更细粒度：

```java
public class MyDto {

    @JsonInclude(Include.NON_NULL)
    private String stringValue;

    private int intValue;

    // standard getters and setters
}
```

现在我们应该能够测试null值确实不是最终 JSON 输出的一部分：

```java
@Test
public void givenNullsIgnoredOnClass_whenWritingObjectWithNullField_thenIgnored()
  throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    MyDto dtoObject = new MyDto();

    String dtoAsString = mapper.writeValueAsString(dtoObject);

    assertThat(dtoAsString, containsString("intValue"));
    assertThat(dtoAsString, not(containsString("stringValue")));
}
```

## 3.全局忽略空字段

Jackson 还允许我们在ObjectMapper上全局配置此行为：

```java
mapper.setSerializationInclusion(Include.NON_NULL);
```

现在，通过此映射器序列化的任何类中的任何空字段都将被忽略：

```java
@Test
public void givenNullsIgnoredGlobally_whenWritingObjectWithNullField_thenIgnored() 
  throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    MyDto dtoObject = new MyDto();

    String dtoAsString = mapper.writeValueAsString(dtoObject);

    assertThat(dtoAsString, containsString("intValue"));
    assertThat(dtoAsString, containsString("booleanValue"));
    assertThat(dtoAsString, not(containsString("stringValue")));
}
```

## 4. 总结

忽略空字段是一种常见的 Jackson 配置，因为通常情况下我们需要更好地控制 JSON 输出。本文演示了如何为类执行此操作。但是，还有更高级的用例，例如[在序列化 Map 时忽略空值](https://www.baeldung.com/jackson-map-null-values-or-null-key)。