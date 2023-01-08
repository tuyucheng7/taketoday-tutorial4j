## 1. 概述

本教程将说明我们如何使用 Jackson 仅序列化满足特定自定义条件的字段。

例如，假设我们只想序列化一个整数值，如果它是正数——如果不是，我们想完全跳过它。

如果你想更深入地了解并学习你可以使用 Jackson 2 做的其他很酷的事情——请转到[主要的 Jackson 教程](https://www.baeldung.com/jackson)。

## 2.使用Jackson Filter控制序列化过程

首先，我们需要使用@JsonFilter注解在我们的实体上定义过滤器：

```java
@JsonFilter("myFilter")
public class MyDto {
    private int intValue;

    public MyDto() {
        super();
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
}
```

然后，我们需要定义我们的自定义PropertyFilter：

```java
PropertyFilter theFilter = new SimpleBeanPropertyFilter() {
   @Override
   public void serializeAsField
    (Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
     throws Exception {
      if (include(writer)) {
         if (!writer.getName().equals("intValue")) {
            writer.serializeAsField(pojo, jgen, provider);
            return;
         }
         int intValue = ((MyDtoWithFilter) pojo).getIntValue();
         if (intValue >= 0) {
            writer.serializeAsField(pojo, jgen, provider);
         }
      } else if (!jgen.canOmitFields()) { // since 2.3
         writer.serializeAsOmittedField(pojo, jgen, provider);
      }
   }
   @Override
   protected boolean include(BeanPropertyWriter writer) {
      return true;
   }
   @Override
   protected boolean include(PropertyWriter writer) {
      return true;
   }
};
```

此过滤器包含根据其值决定是否要序列化intValue字段的实际逻辑。

接下来，我们将这个过滤器挂接到ObjectMapper中，然后序列化一个实体：

```java
FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter", theFilter);
MyDto dtoObject = new MyDto();
dtoObject.setIntValue(-1);

ObjectMapper mapper = new ObjectMapper();
String dtoAsString = mapper.writer(filters).writeValueAsString(dtoObject);
```

最后，我们可以检查intValue字段确实不是编组 JSON 输出的一部分：

```java
assertThat(dtoAsString, not(containsString("intValue")));
```

## 3.有条件地跳过对象

现在 - 让我们讨论如何在基于属性值序列化时跳过对象。我们将跳过属性hidden为true的所有对象：

### 3.1. 可隐藏类

首先，让我们看一下我们的可隐藏界面：

```java
@JsonIgnoreProperties("hidden")
public interface Hidable {
    boolean isHidden();
}
```

我们有两个简单的类实现这个接口Person，Address：

人员类别：

```java
public class Person implements Hidable {
    private String name;
    private Address address;
    private boolean hidden;
}
```

和地址类：

```java
public class Address implements Hidable {
    private String city;
    private String country;
    private boolean hidden;
}
```

注意：我们使用@JsonIgnoreProperties(“hidden”)来确保隐藏属性本身不包含在 JSON 中

### 3.2. 自定义序列化器

接下来——这是我们的自定义序列化程序：

```java
public class HidableSerializer extends JsonSerializer<Hidable> {

    private JsonSerializer<Object> defaultSerializer;

    public HidableSerializer(JsonSerializer<Object> serializer) {
        defaultSerializer = serializer;
    }

    @Override
    public void serialize(Hidable value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException, JsonProcessingException {
        if (value.isHidden())
            return;
        defaultSerializer.serialize(value, jgen, provider);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Hidable value) {
        return (value == null || value.isHidden());
    }
}
```

注意：

-   当对象不会被跳过时，我们将序列化委托给默认注入的序列化程序。
-   我们覆盖了isEmpty()方法——以确保在 Hidable 对象是属性的情况下，属性名称也从 JSON 中排除。

### 3.3. 使用BeanSerializerModifier

最后，我们需要使用BeanSerializerModifier在我们的自定义HidableSerializer中注入默认序列化程序——如下所示：

```java
ObjectMapper mapper = new ObjectMapper();
mapper.setSerializationInclusion(Include.NON_EMPTY);
mapper.registerModule(new SimpleModule() {
    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addBeanSerializerModifier(new BeanSerializerModifier() {
            @Override
            public JsonSerializer<?> modifySerializer(
              SerializationConfig config, BeanDescription desc, JsonSerializer<?> serializer) {
                if (Hidable.class.isAssignableFrom(desc.getBeanClass())) {
                    return new HidableSerializer((JsonSerializer<Object>) serializer);
                }
                return serializer;
            }
        });
    }
});
```

### 3.4. 示例输出

这是一个简单的序列化示例：

```java
Address ad1 = new Address("tokyo", "jp", true);
Address ad2 = new Address("london", "uk", false);
Address ad3 = new Address("ny", "usa", false);
Person p1 = new Person("john", ad1, false);
Person p2 = new Person("tom", ad2, true);
Person p3 = new Person("adam", ad3, false);

System.out.println(mapper.writeValueAsString(Arrays.asList(p1, p2, p3)));
```

输出是：

```bash
[
    {
        "name":"john"
    },
    {
        "name":"adam",
        "address":{
            "city":"ny",
            "country":"usa"
        }
    }
]
```

### 3.5. 测试

最后——这里有几个测试用例：

第一种情况，没有隐藏任何内容：

```java
@Test
public void whenNotHidden_thenCorrect() throws JsonProcessingException {
    Address ad = new Address("ny", "usa", false);
    Person person = new Person("john", ad, false);
    String result = mapper.writeValueAsString(person);

    assertTrue(result.contains("name"));
    assertTrue(result.contains("john"));
    assertTrue(result.contains("address"));
    assertTrue(result.contains("usa"));
}
```

接下来，只隐藏地址：

```java
@Test
public void whenAddressHidden_thenCorrect() throws JsonProcessingException {
    Address ad = new Address("ny", "usa", true);
    Person person = new Person("john", ad, false);
    String result = mapper.writeValueAsString(person);

    assertTrue(result.contains("name"));
    assertTrue(result.contains("john"));
    assertFalse(result.contains("address"));
    assertFalse(result.contains("usa"));
}
```

现在，整个人都被隐藏了：

```java
@Test
public void whenAllHidden_thenCorrect() throws JsonProcessingException {
    Address ad = new Address("ny", "usa", false);
    Person person = new Person("john", ad, true);
    String result = mapper.writeValueAsString(person);

    assertTrue(result.length() == 0);
}
```

## 4. 总结

这种类型的高级过滤非常强大，并且在使用 Jackson 序列化复杂对象时允许非常灵活地自定义 json。

一种更灵活但也更复杂的替代方法是使用完全自定义的序列化程序来控制 JSON 输出——因此，如果此解决方案不够灵活，可能值得研究一下。