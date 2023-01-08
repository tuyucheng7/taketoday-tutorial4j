## 1. 概述

在本教程中，我们将介绍如何使用 Jackson JSON Views 来序列化/反序列化对象、自定义视图以及最后 - 如何开始与 Spring 集成。

## 2. 使用 JSON 视图序列化

首先——让我们看一个简单的例子——用@JsonView序列化一个对象。

这是我们的观点：

```java
public class Views {
    public static class Public {
    }
}
```

而“用户”实体：

```java
public class User {
    public int id;

    @JsonView(Views.Public.class)
    public String name;
}
```

现在让我们使用我们的视图序列化一个“ User ”实例：

```java
@Test
public void whenUseJsonViewToSerialize_thenCorrect() 
  throws JsonProcessingException {
 
    User user = new User(1, "John");

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

    String result = mapper
      .writerWithView(Views.Public.class)
      .writeValueAsString(user);

    assertThat(result, containsString("John"));
    assertThat(result, not(containsString("1")));
}
```

请注意，因为我们在特定视图处于活动状态的情况下进行序列化，所以我们只看到正确的字段被序列化。

同样重要的是要理解，默认情况下，所有未明确标记为视图一部分的属性都是序列化的。我们正在使用方便的DEFAULT_VIEW_INCLUSION功能禁用该行为。

## 3. 使用多个 JSON 视图

接下来——让我们看看如何使用多个 JSON 视图——每个视图都有不同的字段，如下例所示：

在这里，我们必须查看Internal extends Public的视图，内部视图扩展了公共视图：

```java
public class Views {
    public static class Public {
    }

    public static class Internal extends Public {
    }
}
```

这是我们的实体“ Item ”，其中只有字段id和name包含在公共视图中：

```java
public class Item {
 
    @JsonView(Views.Public.class)
    public int id;

    @JsonView(Views.Public.class)
    public String itemName;

    @JsonView(Views.Internal.class)
    public String ownerName;
}
```

如果我们使用Public视图进行序列化——只有id和name会被序列化为 JSON：

```java
@Test
public void whenUsePublicView_thenOnlyPublicSerialized() 
  throws JsonProcessingException {
 
    Item item = new Item(2, "book", "John");

    ObjectMapper mapper = new ObjectMapper();
    String result = mapper
      .writerWithView(Views.Public.class)
      .writeValueAsString(item);

    assertThat(result, containsString("book"));
    assertThat(result, containsString("2"));

    assertThat(result, not(containsString("John")));
}
```

但是如果我们使用Internal视图执行序列化，所有字段都将成为 JSON 输出的一部分：

```java
@Test
public void whenUseInternalView_thenAllSerialized() 
  throws JsonProcessingException {
 
    Item item = new Item(2, "book", "John");

    ObjectMapper mapper = new ObjectMapper();
    String result = mapper
      .writerWithView(Views.Internal.class)
      .writeValueAsString(item);

    assertThat(result, containsString("book"));
    assertThat(result, containsString("2"));

    assertThat(result, containsString("John"));
}
```

## 4. 使用 JSON 视图反序列化

现在 - 让我们看看如何使用 JSON 视图反序列化对象 - 具体来说，一个User实例：

```java
@Test
public void whenUseJsonViewToDeserialize_thenCorrect() 
  throws IOException {
    String json = "{"id":1,"name":"John"}";

    ObjectMapper mapper = new ObjectMapper();
    User user = mapper
      .readerWithView(Views.Public.class)
      .forType(User.class)
      .readValue(json);

    assertEquals(1, user.getId());
    assertEquals("John", user.getName());
}
```

请注意我们如何使用readerWithView() API 来使用给定视图创建ObjectReader 。

## 5. 自定义 JSON 视图

接下来——让我们看看如何自定义 JSON 视图。在下一个示例中——我们希望在序列化结果中将用户“名称”设为大写。

我们将使用BeanPropertyWriter和BeanSerializerModifier来定制我们的 JSON 视图。首先——这里是BeanPropertyWriter UpperCasingWriter将用户 名转换为大写：

```java
public class UpperCasingWriter extends BeanPropertyWriter {
    BeanPropertyWriter _writer;

    public UpperCasingWriter(BeanPropertyWriter w) {
        super(w);
        _writer = w;
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator gen, 
      SerializerProvider prov) throws Exception {
        String value = ((User) bean).name;
        value = (value == null) ? "" : value.toUpperCase();
        gen.writeStringField("name", value);
    }
}
```

这里是BeanSerializerModifier设置用户名BeanPropertyWriter与我们的自定义UpperCasingWriter：

```java
public class MyBeanSerializerModifier extends BeanSerializerModifier{

    @Override
    public List<BeanPropertyWriter> changeProperties(
      SerializationConfig config, BeanDescription beanDesc, 
      List<BeanPropertyWriter> beanProperties) {
        for (int i = 0; i < beanProperties.size(); i++) {
            BeanPropertyWriter writer = beanProperties.get(i);
            if (writer.getName() == "name") {
                beanProperties.set(i, new UpperCasingWriter(writer));
            }
        }
        return beanProperties;
    }
}
```

现在——让我们使用修改后的 Serializer 序列化一个User实例：

```java
@Test
public void whenUseCustomJsonViewToSerialize_thenCorrect() 
  throws JsonProcessingException {
    User user = new User(1, "John");
    SerializerFactory serializerFactory = BeanSerializerFactory.instance
      .withSerializerModifier(new MyBeanSerializerModifier());

    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializerFactory(serializerFactory);

    String result = mapper
      .writerWithView(Views.Public.class)
      .writeValueAsString(user);

    assertThat(result, containsString("JOHN"));
    assertThat(result, containsString("1"));
}
```

## 6. 在 Spring 中使用 JSON 视图

最后，让我们快速了解一下在Spring Framework中使用 JSON 视图。我们可以利用@JsonView注解在 API 级别自定义我们的 JSON 响应。

在下面的例子中——我们使用了Public视图来响应：

```java
@JsonView(Views.Public.class)
@RequestMapping("/items/{id}")
public Item getItemPublic(@PathVariable int id) {
    return ItemManager.getById(id);
}
```

响应是：

```html
{"id":2,"itemName":"book"}
```

当我们使用Internal视图时，如下所示：

```java
@JsonView(Views.Internal.class)
@RequestMapping("/items/internal/{id}")
public Item getItemInternal(@PathVariable int id) {
    return ItemManager.getById(id);
}
```

那是回应：

```html
{"id":2,"itemName":"book","ownerName":"John"}
```

如果你想更深入地使用 Spring 4.1 中的视图，你应该查看[Spring 4.1 中的 Jackson 改进](https://spring.io/blog/2014/12/02/latest-jackson-integration-improvements-in-spring)。

## 七. 总结

在本快速教程中，我们了解了 Jackson JSON 视图和 @JsonView 注解。我们展示了如何使用 JSON 视图对我们的序列化/反序列化过程进行细粒度控制——使用单个或多个视图。