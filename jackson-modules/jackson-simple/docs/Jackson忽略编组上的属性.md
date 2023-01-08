## 1. 概述

本教程将展示如何在使用 Jackson 2.x将对象序列化为 JSON 时忽略某些字段。

当 Jackson 默认值不够用并且我们需要准确控制序列化为 JSON 的内容时，这非常有用——有多种方法可以忽略属性。

要深入挖掘并了解我们可以用 Jackson 做的其他很酷的事情，请转到[主要的 Jackson 教程](https://www.baeldung.com/jackson)。

## 延伸阅读：

## [Jackson ObjectMapper 简介](https://www.baeldung.com/jackson-object-mapper-tutorial)

本文讨论了 Jackson 的核心 ObjectMapper 类、基本序列化和反序列化以及配置这两个进程。

[阅读更多](https://www.baeldung.com/jackson-object-mapper-tutorial)→

## [杰克逊流媒体 API](https://www.baeldung.com/jackson-streaming-api)

快速概览 Jackson 用于处理 JSON 的 Streaming API，包括示例

[阅读更多](https://www.baeldung.com/jackson-streaming-api)→

## [Jackson 中的@JsonFormat 指南](https://www.baeldung.com/jackson-jsonformat)

Jackson 中@JsonFormat 注解的快速实用指南。

[阅读更多](https://www.baeldung.com/jackson-jsonformat)→

## 2.忽略类级别的字段

我们可以在类级别忽略特定字段，使用@JsonIgnoreProperties注解并按名称指定字段：

```java
@JsonIgnoreProperties(value = { "intValue" })
public class MyDto {

    private String stringValue;
    private int intValue;
    private boolean booleanValue;

    public MyDto() {
        super();
    }

    // standard setters and getters are not shown
}
```

我们现在可以测试一下，对象写入JSON后，该字段确实不是输出的一部分：

```java
@Test
public void givenFieldIsIgnoredByName_whenDtoIsSerialized_thenCorrect()
  throws JsonParseException, IOException {
 
    ObjectMapper mapper = new ObjectMapper();
    MyDto dtoObject = new MyDto();

    String dtoAsString = mapper.writeValueAsString(dtoObject);

    assertThat(dtoAsString, not(containsString("intValue")));
}
```

## 3. 在字段级别忽略字段

我们也可以直接通过字段上的@JsonIgnore注解直接忽略一个字段：

```java
public class MyDto {

    private String stringValue;
    @JsonIgnore
    private int intValue;
    private boolean booleanValue;

    public MyDto() {
        super();
    }

    // standard setters and getters are not shown
}
```

我们现在可以测试intValue字段确实不是序列化 JSON 输出的一部分：

```java
@Test
public void givenFieldIsIgnoredDirectly_whenDtoIsSerialized_thenCorrect() 
  throws JsonParseException, IOException {
 
    ObjectMapper mapper = new ObjectMapper();
    MyDto dtoObject = new MyDto();

    String dtoAsString = mapper.writeValueAsString(dtoObject);

    assertThat(dtoAsString, not(containsString("intValue")));
}
```

## 4.按类型忽略所有字段

最后，我们可以忽略指定类型的所有字段，使用@JsonIgnoreType注解。如果我们控制类型，那么我们可以直接注解类：

```java
@JsonIgnoreType
public class SomeType { ... }
```

然而，通常情况下，我们无法控制类本身。在这种情况下，我们可以很好地利用 Jackson mixins。

首先，我们为要忽略的类型定义一个 MixIn，并改为使用@JsonIgnoreType 对其进行注解：

```java
@JsonIgnoreType
public class MyMixInForIgnoreType {}
```

然后我们注册该 mixin 以在编组期间替换(并忽略)所有String[]类型：

```java
mapper.addMixInAnnotations(String[].class, MyMixInForIgnoreType.class);
```

此时，所有 String 数组将被忽略，而不是编组为 JSON：

```java
@Test
public final void givenFieldTypeIsIgnored_whenDtoIsSerialized_thenCorrect()
  throws JsonParseException, IOException {
 
    ObjectMapper mapper = new ObjectMapper();
    mapper.addMixIn(String[].class, MyMixInForIgnoreType.class);
    MyDtoWithSpecialField dtoObject = new MyDtoWithSpecialField();
    dtoObject.setBooleanValue(true);

    String dtoAsString = mapper.writeValueAsString(dtoObject);

    assertThat(dtoAsString, containsString("intValue"));
    assertThat(dtoAsString, containsString("booleanValue"));
    assertThat(dtoAsString, not(containsString("stringValue")));
}
```

这是我们的 DTO：

```java
public class MyDtoWithSpecialField {
    private String[] stringValue;
    private int intValue;
    private boolean booleanValue;
}
```

注意：从2.5版本开始，似乎不能用这种方法来忽略原始数据类型，但是我们可以用它来自定义数据类型和数组。

## 5.使用过滤器忽略字段

最后，我们还可以使用过滤器来忽略Jackson 中的特定字段。

首先，我们需要在Java对象上定义过滤器：

```java
@JsonFilter("myFilter")
public class MyDtoWithFilter { ... }
```

然后我们定义一个简单的过滤器，它将忽略intValue字段：

```java
SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter
  .serializeAllExcept("intValue");
FilterProvider filters = new SimpleFilterProvider()
  .addFilter("myFilter", theFilter);
```

现在我们可以序列化对象并确保intValue字段不出现在 JSON 输出中：

```java
@Test
public final void givenTypeHasFilterThatIgnoresFieldByName_whenDtoIsSerialized_thenCorrect() 
  throws JsonParseException, IOException {
 
    ObjectMapper mapper = new ObjectMapper();
    SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter
      .serializeAllExcept("intValue");
    FilterProvider filters = new SimpleFilterProvider()
      .addFilter("myFilter", theFilter);

    MyDtoWithFilter dtoObject = new MyDtoWithFilter();
    String dtoAsString = mapper.writer(filters).writeValueAsString(dtoObject);

    assertThat(dtoAsString, not(containsString("intValue")));
    assertThat(dtoAsString, containsString("booleanValue"));
    assertThat(dtoAsString, containsString("stringValue"));
    System.out.println(dtoAsString);
}
```

## 六. 总结

本文说明了如何在序列化时忽略字段。我们首先通过名称然后直接执行此操作，最后，我们使用 MixIns 忽略了整个 java 类型并使用过滤器来更好地控制输出。