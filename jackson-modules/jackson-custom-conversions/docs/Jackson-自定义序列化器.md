## 1. 概述

本快速教程将展示如何使用自定义序列化程序使用 Jackson 2 序列化Java实体。

如果你想更深入地了解并学习你可以使用 Jackson 2 做的其他很酷的事情——请转到[主要的 Jackson 教程](https://www.baeldung.com/jackson)。

## 2. 对象图的标准序列化

让我们定义 2 个简单的实体，看看 Jackson 如何在没有任何自定义逻辑的情况下序列化它们：

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

现在，让我们使用User实体序列化Item实体：

```java
Item myItem = new Item(1, "theItem", new User(2, "theUser"));
String serialized = new ObjectMapper().writeValueAsString(myItem);
```

这将为两个实体生成完整的 JSON 表示：

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

## 3. ObjectMapper上的自定义序列化程序 

现在，让我们通过仅序列化User的id而不是整个User对象来简化上面的 JSON 输出；我们希望获得以下更简单的 JSON：

```javascript
{
    "id": 25,
    "itemName": "FEDUfRgS",
    "owner": 15
}
```

简而言之，我们必须为Item对象定义一个自定义序列化程序：

```java
public class ItemSerializer extends StdSerializer<Item> {
    
    public ItemSerializer() {
        this(null);
    }
  
    public ItemSerializer(Class<Item> t) {
        super(t);
    }

    @Override
    public void serialize(
      Item value, JsonGenerator jgen, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
 
        jgen.writeStartObject();
        jgen.writeNumberField("id", value.id);
        jgen.writeStringField("itemName", value.itemName);
        jgen.writeNumberField("owner", value.owner.id);
        jgen.writeEndObject();
    }
}
```

现在，我们需要使用 Item 类的 ObjectMapper 注册这个自定义序列化程序，并执行序列化：

```java
Item myItem = new Item(1, "theItem", new User(2, "theUser"));
ObjectMapper mapper = new ObjectMapper();

SimpleModule module = new SimpleModule();
module.addSerializer(Item.class, new ItemSerializer());
mapper.registerModule(module);

String serialized = mapper.writeValueAsString(myItem);
```

就是这样——我们现在有一个更简单的、自定义的Item->User实体的 JSON 序列化。

## 4.类上的自定义序列化程序

我们也可以直接在类上注册序列化器，而不是在ObjectMapper上：

```java
@JsonSerialize(using = ItemSerializer.class)
public class Item {
    ...
}
```

现在，在执行标准序列化时：

```java
Item myItem = new Item(1, "theItem", new User(2, "theUser"));
String serialized = new ObjectMapper().writeValueAsString(myItem);
```

我们将获得由序列化程序创建的自定义 JSON 输出，通过@JsonSerialize指定：

```javascript
{
    "id": 25,
    "itemName": "FEDUfRgS",
    "owner": 15
}
```

这在无法直接访问和配置ObjectMapper时很有用。

## 5.总结

本文说明了如何通过使用序列化程序使用 Jackson 2 获得自定义 JSON 输出。