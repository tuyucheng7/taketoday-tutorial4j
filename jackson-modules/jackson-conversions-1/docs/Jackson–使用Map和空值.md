## 1. 概述

在这篇简短的文章中，我们将研究使用Jackson的更高级用例——使用包含空值或空键的地图。

## 2.忽略地图中的空值

Jackson 有一个简单但有用的方法来全局控制在序列化 Map 时空值发生的情况：

```java
ObjectMapper mapper = new ObjectMapper();
mapper.setSerializationInclusion(Include.NON_NULL);
```

现在，通过此映射器序列化的 Map 对象中的任何空值都将被忽略：

```java
@Test
public void givenIgnoringNullValuesInMap_whenWritingMapObjectWithNullValue_thenIgnored() 
  throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);

    MyDto dtoObject1 = new MyDto();

    Map<String, MyDto> dtoMap = new HashMap<String, MyDto>();
    dtoMap.put("dtoObject1", dtoObject1);
    dtoMap.put("dtoObject2", null);

    String dtoMapAsString = mapper.writeValueAsString(dtoMap);

    assertThat(dtoMapAsString, containsString("dtoObject1"));
    assertThat(dtoMapAsString, not(containsString("dtoObject2")));
}
```

## 3. 使用空键序列化地图

默认情况下，Jackson 不允许使用空键序列化 Map。如果你确实尝试写出这样的地图，你会得到以下异常：

```bash
c.f.j.c.JsonGenerationException: 
  Null key for a Map not allowed in JSON (use a converting NullKeySerializer?)
    at c.f.j.d.s.i.FailingSerializer.serialize(FailingSerializer.java:36)
```

然而，该库足够灵活，你可以定义自定义的空键序列化程序并覆盖默认行为：

```java
class MyDtoNullKeySerializer extends StdSerializer<Object> {
    public MyDtoNullKeySerializer() {
        this(null);
    }

    public MyDtoNullKeySerializer(Class<Object> t) {
        super(t);
    }
    
    @Override
    public void serialize(Object nullKey, JsonGenerator jsonGenerator, SerializerProvider unused) 
      throws IOException, JsonProcessingException {
        jsonGenerator.writeFieldName("");
    }
}
```

现在带有 null 键的 Map 可以正常工作——并且 null 键将被写为一个空字符串：

```java
@Test
public void givenAllowingMapObjectWithNullKey_whenWriting_thenCorrect() 
throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.getSerializerProvider().setNullKeySerializer(new MyDtoNullKeySerializer());

    MyDto dtoObject = new MyDto();
    dtoObject.setStringValue("dtoObjectString");
 
    Map<String, MyDto> dtoMap = new HashMap<String, MyDto>();
    dtoMap.put(null, dtoObject);

    String dtoMapAsString = mapper.writeValueAsString(dtoMap);

    assertThat(dtoMapAsString, containsString(""""));
    assertThat(dtoMapAsString, containsString("dtoObjectString"));
}
```

## 4.忽略空字段

除了 Maps，Jackson 还提供了很多配置和灵活性来忽略/处理一般的空字段。你可以查看[本教程](https://www.baeldung.com/jackson-ignore-null-fields)以确切了解其工作原理。

## 5.总结

序列化 Map 对象很常见，因此我们需要一个能够很好地处理序列化过程细微差别的库。Jackson 提供了一些方便的定制选项来帮助你很好地塑造这个序列化过程的输出。

它还提供了许多在更一般意义上[处理集合的可靠方法。](https://www.baeldung.com/jackson-collection-array)