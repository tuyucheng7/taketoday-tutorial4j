## 1. 概述

本快速教程说明了如何更改字段名称以映射到序列化时的另一个 JSON 属性。

如果你想更深入地了解并学习你可以使用 Jackson 2 做的其他很酷的事情——请转到[主要的 Jackson 教程](https://www.baeldung.com/jackson)。

## 2.更改序列化字段名称

使用一个简单的实体：

```java
public class MyDto {
    private String stringValue;

    public MyDto() {
        super();
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
```

序列化它将产生以下 JSON：

```javascript
{"stringValue":"some value"}
```

要自定义该输出，以便我们得到的不是stringValue——例如——strVal，我们需要简单地注解 getter：

```java
@JsonProperty("strVal")
public String getStringValue() {
    return stringValue;
}
```

现在，在序列化时，我们将获得所需的输出：

```javascript
{"strVal":"some value"}
```

一个简单的单元测试应该验证输出是否正确：

```java
@Test
public void givenNameOfFieldIsChanged_whenSerializing_thenCorrect() 
  throws JsonParseException, IOException {
    ObjectMapper mapper = new ObjectMapper();
    MyDtoFieldNameChanged dtoObject = new MyDtoFieldNameChanged();
    dtoObject.setStringValue("a");

    String dtoAsString = mapper.writeValueAsString(dtoObject);

    assertThat(dtoAsString, not(containsString("stringValue")));
    assertThat(dtoAsString, containsString("strVal"));
}
```

## 3.总结

编组一个实体以遵循特定的 JSON 格式是一项常见的任务——本文展示了如何简单地使用@JsonProperty注解来做到这一点。