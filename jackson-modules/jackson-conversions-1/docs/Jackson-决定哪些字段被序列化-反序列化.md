## 1. 概述

在这篇文章中，我们将探讨我们可以控制的各种方式，如果一个字段被 Jackson 序列化/反序列化。

## 2.公共领域

确保字段既可序列化又可反序列化的最简单方法是将其公开。

让我们声明一个简单的类，它有一个公共的、一个包私有的和一个私有的

```java
public class MyDtoAccessLevel {
    private String stringValue;
    int intValue;
    protected float floatValue;
    public boolean booleanValue;
    // NO setters or getters
}
```

在该类的四个字段中，默认情况下只有 public booleanValue将被序列化为 JSON：

```java
@Test
public void givenDifferentAccessLevels_whenPublic_thenSerializable() 
  throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    MyDtoAccessLevel dtoObject = new MyDtoAccessLevel();

    String dtoAsString = mapper.writeValueAsString(dtoObject);
    assertThat(dtoAsString, not(containsString("stringValue")));
    assertThat(dtoAsString, not(containsString("intValue")));
    assertThat(dtoAsString, not(containsString("floatValue")));

    assertThat(dtoAsString, containsString("booleanValue"));
}
```

## 3. Getter 使非公共字段可序列化和反序列化

现在，另一种使字段(尤其是非公共字段)可序列化的简单方法是为其添加一个 getter：

```java
public class MyDtoWithGetter {
    private String stringValue;
    private int intValue;

    public String getStringValue() {
        return stringValue;
    }
}
```

我们现在期望stringValue字段是可序列化的，而另一个私有字段不是，因为它没有 getter：

```java
@Test
public void givenDifferentAccessLevels_whenGetterAdded_thenSerializable() 
  throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    MyDtoGetter dtoObject = new MyDtoGetter();

    String dtoAsString = mapper.writeValueAsString(dtoObject);
    assertThat(dtoAsString, containsString("stringValue"));
    assertThat(dtoAsString, not(containsString("intValue")));
}
```

出乎意料的是，吸气剂也使私有字段也可反序列化——因为一旦它有了吸气剂，该字段就被视为一个属性。

让我们看看它是如何工作的：

```java
@Test
public void givenDifferentAccessLevels_whenGetterAdded_thenDeserializable() 
  throws JsonProcessingException, JsonMappingException, IOException {
    String jsonAsString = "{"stringValue":"dtoString"}";
    ObjectMapper mapper = new ObjectMapper();
    MyDtoWithGetter dtoObject = mapper.readValue(jsonAsString, MyDtoWithGetter.class);

    assertThat(dtoObject.getStringValue(), equalTo("dtoString"));
}
```

## 4. Setter 使非公共字段只能反序列化

我们看到了 getter 如何使私有字段既可序列化又可反序列化。另一方面，setter 只会将非公共字段标记为可反序列化：

```java
public class MyDtoWithSetter {
    private int intValue;

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public int accessIntValue() {
        return intValue;
    }
}
```

如你所见，这次私有intValue字段只有一个 setter。我们确实有办法访问该值，但这不是标准的 getter。

intValue的解组过程应该可以正常工作：

```java
@Test
public void givenDifferentAccessLevels_whenSetterAdded_thenDeserializable() 
  throws JsonProcessingException, JsonMappingException, IOException {
    String jsonAsString = "{"intValue":1}";
    ObjectMapper mapper = new ObjectMapper();

    MyDtoSetter dtoObject = mapper.readValue(jsonAsString, MyDtoSetter.class);

    assertThat(dtoObject.anotherGetIntValue(), equalTo(1));
}
```

正如我们提到的，setter 应该只使字段可反序列化，而不是可序列化：

```java
@Test
public void givenDifferentAccessLevels_whenSetterAdded_thenStillNotSerializable() 
  throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    MyDtoSetter dtoObject = new MyDtoSetter();

    String dtoAsString = mapper.writeValueAsString(dtoObject);
    assertThat(dtoAsString, not(containsString("intValue")));
}
```

## 5. 使所有字段全局可序列化

在某些情况下，例如，你实际上可能无法直接修改源代码——我们需要配置 Jackson 从外部处理非公共字段的方式。

这种全局配置可以在 ObjectMapper 级别完成，方法是打开AutoDetect函数以使用公共字段或 getter/setter 方法进行序列化，或者可能为所有字段打开序列化：

```java
ObjectMapper mapper = new ObjectMapper();
mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
```

以下测试用例验证MyDtoAccessLevel的所有成员字段(包括非公共字段)都是可序列化的：

```java
@Test
public void givenDifferentAccessLevels_whenSetVisibility_thenSerializable() 
  throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

    MyDtoAccessLevel dtoObject = new MyDtoAccessLevel();

    String dtoAsString = mapper.writeValueAsString(dtoObject);
    assertThat(dtoAsString, containsString("stringValue"));
    assertThat(dtoAsString, containsString("intValue"));
    assertThat(dtoAsString, containsString("booleanValue"));
}
```

## 6.更改序列化/反序列化的属性名称

除了控制哪个字段被序列化或反序列化之外，你还可以控制字段映射到 JSON 和返回的方式。我[在这里介绍了这个配置](https://www.baeldung.com/jackson-name-of-property)。

## 7.忽略序列化或反序列化的字段

在[本教程](https://www.baeldung.com/jackson-ignore-properties-on-serialization)之后，我们有一个关于如何在序列化和反序列化中完全忽略一个字段的指南。

然而，有时我们只需要忽略其中一个字段，而不是同时忽略两个字段。Jackson 也足够灵活以适应这个有趣的用例。

以下示例显示了一个用户对象，其中包含不应序列化为 JSON 的敏感密码信息。

为此，我们只需在password的 getter 上添加@JsonIgnore注解，并通过在 setter 上应用@JsonProperty注解来启用该字段的反序列化：

```java
@JsonIgnore
public String getPassword() {
    return password;
}
@JsonProperty
public void setPassword(String password) {
    this.password = password;
}
```

现在密码信息不会被序列化为 JSON：

```java
@Test
public void givenFieldTypeIsIgnoredOnlyAtSerialization_whenUserIsSerialized_thenIgnored() 
  throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    User userObject = new User();
    userObject.setPassword("thePassword");

    String userAsString = mapper.writeValueAsString(userObject);
    assertThat(userAsString, not(containsString("password")));
    assertThat(userAsString, not(containsString("thePassword")));
}
```

但是，包含密码的 JSON 将成功反序列化为User对象：

```java
@Test
public void givenFieldTypeIsIgnoredOnlyAtSerialization_whenUserIsDeserialized_thenCorrect() 
  throws JsonParseException, JsonMappingException, IOException {
    String jsonAsString = "{"password":"thePassword"}";
    ObjectMapper mapper = new ObjectMapper();

    User userObject = mapper.readValue(jsonAsString, User.class);

    assertThat(userObject.getPassword(), equalTo("thePassword"));
}
```

## 八. 总结

本教程介绍了 Jackson 如何选择哪个字段被序列化/反序列化以及在该过程中忽略哪个字段的基础知识，当然还有如何完全控制它。

你还可以通过更深入地阅读诸如[忽略字段](https://www.baeldung.com/jackson-ignore-properties-on-serialization)、[将 JSON 数组反序列化为Java数组或集合](https://www.baeldung.com/jackson-collection-array)等文章来了解 Jackson 2 的下一步。