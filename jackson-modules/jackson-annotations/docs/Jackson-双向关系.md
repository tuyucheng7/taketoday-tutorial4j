## 1. 概述

在本教程中，我们将研究处理Jackson 中双向关系的最佳方法。

首先，我们将讨论 Jackson JSON 无限递归问题。然后我们将看到如何序列化具有双向关系的实体。最后，我们将反序列化它们。

## 2.无限递归

让我们看一下杰克逊无限递归问题。在下面的示例中，我们有两个实体，“用户”和“项目”，具有简单的一对多关系：

“用户”实体：

```java
public class User {
    public int id;
    public String name;
    public List<Item> userItems;
}
```

“项目”实体：

```java
public class Item {
    public int id;
    public String itemName;
    public User owner;
}
```

当我们尝试序列化“ Item ”的实例时，Jackson 会抛出JsonMappingException异常：

```java
@Test(expected = JsonMappingException.class)
public void givenBidirectionRelation_whenSerializing_thenException()
  throws JsonProcessingException {
 
    User user = new User(1, "John");
    Item item = new Item(2, "book", user);
    user.addItem(item);

    new ObjectMapper().writeValueAsString(item);
}
```

完整的例外是：

```bash
com.fasterxml.jackson.databind.JsonMappingException:
Infinite recursion (StackOverflowError) 
(through reference chain: 
org.baeldung.jackson.bidirection.Item["owner"]
->org.baeldung.jackson.bidirection.User["userItems"]
->java.util.ArrayList[0]
->org.baeldung.jackson.bidirection.Item["owner"]
->…..
```

在接下来的几节中，我们将看到如何解决这个问题。

## 3.使用@JsonManagedReference，@JsonBackReference

首先，让我们注解与@JsonManagedReference和@JsonBackReference的关系，以便 Jackson 更好地处理关系：

这是“用户”实体：

```java
public class User {
    public int id;
    public String name;

    @JsonManagedReference
    public List<Item> userItems;
}
```

和“项目”：

```java
public class Item {
    public int id;
    public String itemName;

    @JsonBackReference
    public User owner;
}
```

现在让我们测试新实体：

```java
@Test
public void givenBidirectionRelation_whenUsingJacksonReferenceAnnotationWithSerialization_thenCorrect() throws JsonProcessingException {
    final User user = new User(1, "John");
    final Item item = new Item(2, "book", user);
    user.addItem(item);

    final String itemJson = new ObjectMapper().writeValueAsString(item);
    final String userJson = new ObjectMapper().writeValueAsString(user);

    assertThat(itemJson, containsString("book"));
    assertThat(itemJson, not(containsString("John")));

    assertThat(userJson, containsString("John"));
    assertThat(userJson, containsString("userItems"));
    assertThat(userJson, containsString("book"));
}
```

这是序列化 Item 对象的输出：

```bash
{
 "id":2,
 "itemName":"book"
}
```

这是序列化 User 对象的输出：

```bash
{
 "id":1,
 "name":"John",
 "userItems":[{
   "id":2,
   "itemName":"book"}]
}
```

注意：

-   @JsonManagedReference是引用的前向部分，即正常序列化的部分。
-   @JsonBackReference是引用的后面部分；它将从序列化中省略。
-   序列化的Item对象不包含对User对象的引用。

另请注意，我们无法切换注解。以下将用于序列化：

```java
@JsonBackReference
public List<Item> userItems;

@JsonManagedReference
public User owner;
```

但是当我们尝试反序列化对象时，它会抛出异常，因为@JsonBackReference不能用于集合。

如果我们想让序列化的 Item 对象包含对 User 的引用，我们需要使用 @JsonIdentityInfo。我们将在下一节中对此进行介绍。

## 4.使用@JsonIdentityInfo

现在让我们学习如何使用@JsonIdentityInfo帮助序列化具有双向关系的实体。

我们将类级别注解添加到我们的“用户”实体：

```java
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id")
public class User { ... }
```

对于“ Item ”实体：

```java
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id")
public class Item { ... }
```

测试时间：

```java
@Test
public void givenBidirectionRelation_whenUsingJsonIdentityInfo_thenCorrect()
  throws JsonProcessingException {
 
    User user = new User(1, "John");
    Item item = new Item(2, "book", user);
    user.addItem(item);

    String result = new ObjectMapper().writeValueAsString(item);

    assertThat(result, containsString("book"));
    assertThat(result, containsString("John"));
    assertThat(result, containsString("userItems"));
}
```

这是序列化的输出：

```bash
{
 "id":2,
 "itemName":"book",
 "owner":
    {
        "id":1,
        "name":"John",
        "userItems":[2]
    }
}
```

## 5.使用@JsonIgnore

或者，我们可以使用@JsonIgnore注解来简单地忽略关系的一侧，从而打破链条。

在下面的示例中，我们将通过忽略序列化中的“ User ”属性“ userItems ”来防止无限递归：

这是“用户”实体：

```java
public class User {
    public int id;
    public String name;

    @JsonIgnore
    public List<Item> userItems;
}
```

这是我们的测试：

```java
@Test
public void givenBidirectionRelation_whenUsingJsonIgnore_thenCorrect()
  throws JsonProcessingException {
 
    User user = new User(1, "John");
    Item item = new Item(2, "book", user);
    user.addItem(item);

    String result = new ObjectMapper().writeValueAsString(item);

    assertThat(result, containsString("book"));
    assertThat(result, containsString("John"));
    assertThat(result, not(containsString("userItems")));
}
```

最后，这是序列化的输出：

```bash
{
 "id":2,
 "itemName":"book",
 "owner":
    {
        "id":1,
        "name":"John"
    }
}
```

## 6.使用@JsonView

我们还可以使用较新的@JsonView注解来排除关系的一侧。

在下面的示例中，我们将使用两个 JSON 视图，Public和Internal，其中Internal扩展了Public：

```java
public class Views {
    public static class Public {}

    public static class Internal extends Public {}
}
```

我们将在公共视图中包含所有用户和项目字段，但用户字段userItems 除外，它将包含在内部视图中：

这是我们的“用户”实体：

```java
public class User {
    @JsonView(Views.Public.class)
    public int id;

    @JsonView(Views.Public.class)
    public String name;

    @JsonView(Views.Internal.class)
    public List<Item> userItems;
}
```

这是我们的“项目”实体：

```java
public class Item {
    @JsonView(Views.Public.class)
    public int id;

    @JsonView(Views.Public.class)
    public String itemName;

    @JsonView(Views.Public.class)
    public User owner;
}
```

当我们使用Public视图进行序列化时，它可以正常工作，因为我们将userItems排除在序列化之外：

```java
@Test
public void givenBidirectionRelation_whenUsingPublicJsonView_thenCorrect() 
  throws JsonProcessingException {
 
    User user = new User(1, "John");
    Item item = new Item(2, "book", user);
    user.addItem(item);

    String result = new ObjectMapper().writerWithView(Views.Public.class)
      .writeValueAsString(item);

    assertThat(result, containsString("book"));
    assertThat(result, containsString("John"));
    assertThat(result, not(containsString("userItems")));
}
```

但是，如果我们使用内部视图进行序列化，则会抛出JsonMappingException，因为所有字段都包含在内：

```java
@Test(expected = JsonMappingException.class)
public void givenBidirectionRelation_whenUsingInternalJsonView_thenException()
  throws JsonProcessingException {
 
    User user = new User(1, "John");
    Item item = new Item(2, "book", user);
    user.addItem(item);

    new ObjectMapper()
      .writerWithView(Views.Internal.class)
      .writeValueAsString(item);
}
```

## 7.使用自定义序列化器

接下来，我们将了解如何使用自定义序列化程序序列化具有双向关系的实体。

在下面的示例中，我们将使用自定义序列化程序来序列化“ User ”属性“ userItems: ”

这是“用户”实体：

```java
public class User {
    public int id;
    public String name;

    @JsonSerialize(using = CustomListSerializer.class)
    public List<Item> userItems;
}
```

这是“ CustomListSerializer： ”

```java
public class CustomListSerializer extends StdSerializer<List<Item>>{

   public CustomListSerializer() {
        this(null);
    }

    public CustomListSerializer(Class<List> t) {
        super(t);
    }

    @Override
    public void serialize(
      List<Item> items, 
      JsonGenerator generator, 
      SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        
        List<Integer> ids = new ArrayList<>();
        for (Item item : items) {
            ids.add(item.id);
        }
        generator.writeObject(ids);
    }
}
```

现在让我们测试序列化程序。正如我们所见，正在生成正确类型的输出：

```java
@Test
public void givenBidirectionRelation_whenUsingCustomSerializer_thenCorrect()
  throws JsonProcessingException {
    User user = new User(1, "John");
    Item item = new Item(2, "book", user);
    user.addItem(item);

    String result = new ObjectMapper().writeValueAsString(item);

    assertThat(result, containsString("book"));
    assertThat(result, containsString("John"));
    assertThat(result, containsString("userItems"));
}
```

这是使用自定义序列化程序进行序列化的最终输出：

```bash
{
 "id":2,
 "itemName":"book",
 "owner":
    {
        "id":1,
        "name":"John",
        "userItems":[2]
    }
}
```

## 8.反序列化@JsonIdentityInfo

现在让我们看看如何使用@JsonIdentityInfo反序列化具有双向关系的实体。

这是“用户”实体：

```java
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id")
public class User { ... }
```

而“ Item ”实体：

```java
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class, 
  property = "id")
public class Item { ... }
```

我们将编写一个快速测试，从我们要解析的一些手动 JSON 数据开始，并以正确构造的实体结束：

```java
@Test
public void givenBidirectionRelation_whenDeserializingWithIdentity_thenCorrect() 
  throws JsonProcessingException, IOException {
    String json = 
      "{"id":2,"itemName":"book","owner":{"id":1,"name":"John","userItems":[2]}}";

    ItemWithIdentity item
      = new ObjectMapper().readerFor(ItemWithIdentity.class).readValue(json);
    
    assertEquals(2, item.id);
    assertEquals("book", item.itemName);
    assertEquals("John", item.owner.name);
}
```

## 9.使用自定义反序列化器

最后，让我们使用自定义反序列化器反序列化具有双向关系的实体。

在下面的示例中，我们将使用自定义反序列化器来解析“ User ”属性“ userItems: ”

这是“用户”实体：

```java
public class User {
    public int id;
    public String name;

    @JsonDeserialize(using = CustomListDeserializer.class)
    public List<Item> userItems;
}
```

这是我们的“ CustomListDeserializer： ”

```java
public class CustomListDeserializer extends StdDeserializer<List<Item>>{

    public CustomListDeserializer() {
        this(null);
    }

    public CustomListDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public List<Item> deserialize(
      JsonParser jsonparser, 
      DeserializationContext context) 
      throws IOException, JsonProcessingException {
        
        return new ArrayList<>();
    }
}
```

最后，这是简单的测试：

```java
@Test
public void givenBidirectionRelation_whenUsingCustomDeserializer_thenCorrect()
  throws JsonProcessingException, IOException {
    String json = 
      "{"id":2,"itemName":"book","owner":{"id":1,"name":"John","userItems":[2]}}";

    Item item = new ObjectMapper().readerFor(Item.class).readValue(json);
 
    assertEquals(2, item.id);
    assertEquals("book", item.itemName);
    assertEquals("John", item.owner.name);
}
```

## 10.总结

在本文中，我们说明了如何使用 Jackson 序列化/反序列化具有双向关系的实体。