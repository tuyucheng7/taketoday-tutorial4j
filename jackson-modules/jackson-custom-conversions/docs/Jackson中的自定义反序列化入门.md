## 1. 概述

本快速教程将说明如何使用 Jackson 2 通过自定义反序列化器反序列化 JSON。

要更深入地了解我们可以用 Jackson 2 做的其他很酷的事情，请转到[主要的 Jackson 教程](https://www.baeldung.com/jackson)。

## 延伸阅读：

## [Jackson ObjectMapper 简介](https://www.baeldung.com/jackson-object-mapper-tutorial)

本文讨论了 Jackson 的核心 ObjectMapper 类、基本序列化和反序列化以及配置这两个进程。

[阅读更多](https://www.baeldung.com/jackson-object-mapper-tutorial)→

## [Jackson——决定哪些字段被序列化/反序列化](https://www.baeldung.com/jackson-field-serializable-deserializable-or-not)

如何控制哪些字段被 Jackson 序列化/反序列化以及哪些字段被忽略。

[阅读更多](https://www.baeldung.com/jackson-field-serializable-deserializable-or-not)→

## [Jackson——自定义序列化器](https://www.baeldung.com/jackson-custom-serialization)

通过使用自定义序列化器控制 Jackson 2 的 JSON 输出。

[阅读更多](https://www.baeldung.com/jackson-custom-serialization)→

## 2.标准反序列化

让我们从定义两个实体开始，看看 Jackson 如何在不进行任何自定义的情况下将 JSON 表示反序列化为这些实体：

```java
public class User {
    public int id;
    public String name;
}
public class Item {
    public int id;
    public String itemName;
    public User owner;
}
```

现在让我们定义我们想要反序列化的 JSON 表示：

```javascript
{
    "id": 1,
    "itemName": "theItem",
    "owner": {
        "id": 2,
        "name": "theUser"
    }
}
```

最后，让我们将此 JSON 解组为Java实体：

```java
Item itemWithOwner = new ObjectMapper().readValue(json, Item.class);
```

## 3. ObjectMapper上的自定义反序列化器

在前面的示例中，JSON 表示与Java实体完美匹配。

接下来，我们将简化 JSON：

```javascript
{
    "id": 1,
    "itemName": "theItem",
    "createdBy": 2
}
```

默认情况下，当将其解组为完全相同的实体时，这当然会失败：

```bash
com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException: 
Unrecognized field "createdBy" (class org.baeldung.jackson.dtos.Item), 
not marked as ignorable (3 known properties: "id", "owner", "itemName"])
 at [Source: java.io.StringReader@53c7a917; line: 1, column: 43] 
 (through reference chain: org.baeldung.jackson.dtos.Item["createdBy"])
```

我们将通过使用自定义 Deserializer 进行自己的反序列化来解决此问题：

```java
public class ItemDeserializer extends StdDeserializer<Item> { 

    public ItemDeserializer() { 
        this(null); 
    } 

    public ItemDeserializer(Class<?> vc) { 
        super(vc); 
    }

    @Override
    public Item deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        int id = (Integer) ((IntNode) node.get("id")).numberValue();
        String itemName = node.get("itemName").asText();
        int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();

        return new Item(id, itemName, new User(userId, null));
    }
}
```

正如我们所见，反序列化器正在使用 JSON 的标准 Jackson 表示形式—— JsonNode。一旦输入 JSON 表示为JsonNode，我们现在就可以从中提取相关信息并构建我们自己的Item实体。

简单的说，我们需要注册这个自定义反序列化器，正常反序列化JSON：

```java
ObjectMapper mapper = new ObjectMapper();
SimpleModule module = new SimpleModule();
module.addDeserializer(Item.class, new ItemDeserializer());
mapper.registerModule(module);

Item readValue = mapper.readValue(json, Item.class);
```

## 4.类上的自定义反序列化器

或者，我们也可以直接在类上注册反序列化器：

```java
@JsonDeserialize(using = ItemDeserializer.class)
public class Item {
    ...
}
```

使用在类级别定义的反序列化器，无需在ObjectMapper上注册它——默认映射器可以正常工作：

```java
Item itemWithOwner = new ObjectMapper().readValue(json, Item.class);
```

这种类型的每类配置在我们可能无法直接访问原始ObjectMapper进行配置的情况下非常有用。

## 5.总结

本文展示了如何利用 Jackson 2读取非标准 JSON 输入，以及如何将该输入映射到任何Java实体图并完全控制映射。