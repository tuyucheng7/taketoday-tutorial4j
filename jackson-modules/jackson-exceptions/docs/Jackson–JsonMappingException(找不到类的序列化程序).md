## 1. 概述

在这个快速教程中，我们将分析没有 getter 的实体的编组以及Jackson JsonMappingException异常的解决方案。

如果你想更深入地了解并学习你可以使用 Jackson 2 做的其他很酷的事情——请转到[主要的 Jackson 教程](https://www.baeldung.com/jackson)。

## 延伸阅读：

## [Jackson ObjectMapper 简介](https://www.baeldung.com/jackson-object-mapper-tutorial)

本文讨论了 Jackson 的核心 ObjectMapper 类、基本序列化和反序列化以及配置这两个进程。

[阅读更多](https://www.baeldung.com/jackson-object-mapper-tutorial)→

## [与 Jackson 一起使用 Optional](https://www.baeldung.com/jackson-optional)

快速概述我们如何将 Optional 与 Jackson 一起使用。

[阅读更多](https://www.baeldung.com/jackson-optional)→

## [Spring JSON-P 与杰克逊](https://www.baeldung.com/spring-jackson-jsonp)

本文重点展示如何使用 Spring 4.1 中新的 JSON-P 支持。

[阅读更多](https://www.baeldung.com/spring-jackson-jsonp)→

## 2.问题

默认情况下，Jackson 2 将仅使用公共字段或具有公共 getter 方法的字段 -序列化具有所有字段私有或包私有的实体将失败：

```java
public class MyDtoNoAccessors {
    String stringValue;
    int intValue;
    boolean booleanValue;

    public MyDtoNoAccessors() {
        super();
    }

    // no getters
}
@Test(expected = JsonMappingException.class)
public void givenObjectHasNoAccessors_whenSerializing_thenException() 
  throws JsonParseException, IOException {
    String dtoAsString = new ObjectMapper().writeValueAsString(new MyDtoNoAccessors());

    assertThat(dtoAsString, notNullValue());
}
```

完整的例外是：

```bash
com.fasterxml.jackson.databind.JsonMappingException: 
No serializer found for class dtos.MyDtoNoAccessors 
and no properties discovered to create BeanSerializer 
(to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) )
```

## 3.解决方案

显而易见的解决方案是为字段添加 getter——如果实体在我们的控制之下。如果不是这种情况并且修改实体的来源是不可能的——那么 Jackson 为我们提供了一些替代方案。

### 3.1. 全局自动检测具有任何可见性的字段

此问题的第一个解决方案是全局配置ObjectMapper以检测所有字段，而不管它们的可见性：

```java
objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
```

这将允许在没有 getter 的情况下检测私有和包私有字段，并且序列化将正常工作：

```java
@Test
public void givenObjectHasNoAccessors_whenSerializingWithAllFieldsDetected_thenNoException() 
  throws JsonParseException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    String dtoAsString = objectMapper.writeValueAsString(new MyDtoNoAccessors());

    assertThat(dtoAsString, containsString("intValue"));
    assertThat(dtoAsString, containsString("stringValue"));
    assertThat(dtoAsString, containsString("booleanValue"));
}
```

### 3.2. 在类级别检测到所有字段

Jackson 2 提供的另一个选项是——而不是全局配置——通过@JsonAutoDetect注解控制类级别的字段可见性：

```java
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class MyDtoNoAccessors { ... }
```

有了这个注解，序列化现在应该可以正确地处理这个特定的类了：

```java
@Test
public void givenObjectHasNoAccessorsButHasVisibleFields_whenSerializing_thenNoException() 
  throws JsonParseException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    String dtoAsString = objectMapper.writeValueAsString(new MyDtoNoAccessors());

    assertThat(dtoAsString, containsString("intValue"));
    assertThat(dtoAsString, containsString("stringValue"));
    assertThat(dtoAsString, containsString("booleanValue"));
}
```

## 4. 总结

本文说明了如何通过在ObjectMapper或单个类上全局配置自定义可见性来绕过 Jackson 中的默认字段可见性。Jackson 通过提供选项来精确控制具有特定可见性的 getter、setter 或字段如何被映射器看到，从而允许进一步定制。