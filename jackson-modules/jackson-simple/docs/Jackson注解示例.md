## 1. 概述

在本教程中，我们将深入探讨Jackson Annotations。

我们将看到如何使用现有注解，如何创建自定义注解，最后，如何禁用它们。

## 延伸阅读：

## [更多杰克逊注解](https://www.baeldung.com/jackson-advanced-annotations)

本文介绍了 Jackson 提供的一些鲜为人知的 JSON 处理注解。

[阅读更多](https://www.baeldung.com/jackson-advanced-annotations)→

## [杰克逊——双向关系](https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion)

如何使用 Jackson 来破解双向关系的无限递归问题。

[阅读更多](https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion)→

## [Jackson 中的自定义反序列化入门](https://www.baeldung.com/jackson-deserialization)

使用 Jackson 将自定义 JSON 映射到任何 java 实体图，并完全控制反序列化过程。

[阅读更多](https://www.baeldung.com/jackson-deserialization)→

## 2. Jackson 连载注解

首先，我们将看一下序列化注解。

### 2.1. @JsonAnyGetter

@JsonAnyGetter注解允许灵活地将Map字段用作标准属性。

例如，ExtendableBean实体具有名称属性和一组键/值对形式的可扩展属性：

```java
public class ExtendableBean {
    public String name;
    private Map<String, String> properties;

    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return properties;
    }
}
```

当我们序列化这个实体的一个实例时，我们将Map中的所有键值作为标准的、普通的属性：

```bash
{
    "name":"My bean",
    "attr2":"val2",
    "attr1":"val1"
}
```

下面是这个实体的序列化在实践中的样子：

```java
@Test
public void whenSerializingUsingJsonAnyGetter_thenCorrect()
  throws JsonProcessingException {
 
    ExtendableBean bean = new ExtendableBean("My bean");
    bean.add("attr1", "val1");
    bean.add("attr2", "val2");

    String result = new ObjectMapper().writeValueAsString(bean);
 
    assertThat(result, containsString("attr1"));
    assertThat(result, containsString("val1"));
}
```

我们还可以使用可选参数enabled为false来禁用@JsonAnyGetter()。在这种情况下，Map将被转换为 JSON，并在序列化后出现在properties变量下。

### 2.2. @JsonGetter

@JsonGetter注解是@JsonProperty 注解的替代方法，它将方法标记为getter 方法。

在下面的示例中，我们将方法getTheName()指定为MyBean实体的名称属性的 getter 方法：

```java
public class MyBean {
    public int id;
    private String name;

    @JsonGetter("name")
    public String getTheName() {
        return name;
    }
}
```

这是它在实践中的工作原理：

```java
@Test
public void whenSerializingUsingJsonGetter_thenCorrect()
  throws JsonProcessingException {
 
    MyBean bean = new MyBean(1, "My bean");

    String result = new ObjectMapper().writeValueAsString(bean);
 
    assertThat(result, containsString("My bean"));
    assertThat(result, containsString("1"));
}
```

### 2.3. @JsonPropertyOrder

我们可以使用@JsonPropertyOrder注解来指定属性在序列化时的顺序。

让我们为MyBean实体的属性设置自定义顺序：

```java
@JsonPropertyOrder({ "name", "id" })
public class MyBean {
    public int id;
    public String name;
}
```

这是序列化的输出：

```bash
{
    "name":"My bean",
    "id":1
}
```

那么我们可以做一个简单的测试：

```java
@Test
public void whenSerializingUsingJsonPropertyOrder_thenCorrect()
  throws JsonProcessingException {
 
    MyBean bean = new MyBean(1, "My bean");

    String result = new ObjectMapper().writeValueAsString(bean);
    assertThat(result, containsString("My bean"));
    assertThat(result, containsString("1"));
}
```

我们还可以使用@JsonPropertyOrder(alphabetic=true)按字母顺序排列属性。在这种情况下，序列化的输出将是：

```bash
{
    "id":1,
    "name":"My bean"
}
```

### 2.4. @JsonRawValue

@JsonRawValue注解可以指示 Jackson 完全按原样序列化属性。

在下面的示例中，我们使用@JsonRawValue将一些自定义 JSON 作为实体的值嵌入：

```java
public class RawBean {
    public String name;

    @JsonRawValue
    public String json;
}
```

序列化实体的输出是：

```java
{
    "name":"My bean",
    "json":{
        "attr":false
    }
}
```

接下来是一个简单的测试：

```java
@Test
public void whenSerializingUsingJsonRawValue_thenCorrect()
  throws JsonProcessingException {
 
    RawBean bean = new RawBean("My bean", "{"attr":false}");

    String result = new ObjectMapper().writeValueAsString(bean);
    assertThat(result, containsString("My bean"));
    assertThat(result, containsString("{"attr":false}"));
}
```

我们还可以使用可选的布尔参数值来定义此注解是否处于活动状态。

### 2.5. @JsonValue

@JsonValue指示库将用于序列化整个实例的单个方法。

例如，在枚举中，我们用@JsonValue注解getName以便任何此类实体都通过其名称进行序列化：

```java
public enum TypeEnumWithValue {
    TYPE1(1, "Type A"), TYPE2(2, "Type 2");

    private Integer id;
    private String name;

    // standard constructors

    @JsonValue
    public String getName() {
        return name;
    }
}
```

现在这是我们的测试：

```java
@Test
public void whenSerializingUsingJsonValue_thenCorrect()
  throws JsonParseException, IOException {
 
    String enumAsString = new ObjectMapper()
      .writeValueAsString(TypeEnumWithValue.TYPE1);

    assertThat(enumAsString, is(""Type A""));
}
```

### 2.6. @JsonRootName

如果启用了包装，则使用@JsonRootName注解来指定要使用的根包装器的名称。

包装意味着不是将User序列化为：

```javascript
{
    "id": 1,
    "name": "John"
}
```

它将像这样包装：

```javascript
{
    "User": {
        "id": 1,
        "name": "John"
    }
}
```

那么让我们看一个例子。我们将使用@JsonRootName注解来指示此潜在包装器实体的名称：

```java
@JsonRootName(value = "user")
public class UserWithRoot {
    public int id;
    public String name;
}
```

默认情况下，包装器的名称将是类的名称 – UserWithRoot。通过使用注解，我们得到了看起来更干净的用户：

```java
@Test
public void whenSerializingUsingJsonRootName_thenCorrect()
  throws JsonProcessingException {
 
    UserWithRoot user = new User(1, "John");

    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
    String result = mapper.writeValueAsString(user);

    assertThat(result, containsString("John"));
    assertThat(result, containsString("user"));
}
```

这是序列化的输出：

```java
{
    "user":{
        "id":1,
        "name":"John"
    }
}
```

从 Jackson 2.4 开始，一个新的可选参数命名空间可用于 XML 等数据格式。如果我们添加它，它将成为完全限定名称的一部分：

```java
@JsonRootName(value = "user", namespace="users")
public class UserWithRootNamespace {
    public int id;
    public String name;

    // ...
}
```

如果我们用XmlMapper 序列化它，输出将是：

```xml
<user xmlns="users">
    <id xmlns="">1</id>
    <name xmlns="">John</name>
    <items xmlns=""/>
</user>
```

### 2.7. @Json序列化

@JsonSerialize指示在编组实体时使用的自定义序列化程序。

让我们看一个简单的例子。我们将使用@JsonSerialize通过CustomDateSerializer序列化eventDate属性：

```java
public class EventWithSerializer {
    public String name;

    @JsonSerialize(using = CustomDateSerializer.class)
    public Date eventDate;
}
```

这是简单的自定义 Jackson 序列化程序：

```java
public class CustomDateSerializer extends StdSerializer<Date> {

    private static SimpleDateFormat formatter 
      = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    public CustomDateSerializer() { 
        this(null); 
    } 

    public CustomDateSerializer(Class<Date> t) {
        super(t); 
    }

    @Override
    public void serialize(
      Date value, JsonGenerator gen, SerializerProvider arg2) 
      throws IOException, JsonProcessingException {
        gen.writeString(formatter.format(value));
    }
}
```

现在让我们在测试中使用它们：

```java
@Test
public void whenSerializingUsingJsonSerialize_thenCorrect()
  throws JsonProcessingException, ParseException {
 
    SimpleDateFormat df
      = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    String toParse = "20-12-2014 02:30:00";
    Date date = df.parse(toParse);
    EventWithSerializer event = new EventWithSerializer("party", date);

    String result = new ObjectMapper().writeValueAsString(event);
    assertThat(result, containsString(toParse));
}
```

## 3. Jackson反序列化注解

接下来让我们探讨一下 Jackson 反序列化注解。

### 3.1. @JsonCreator

我们可以使用@JsonCreator注解来调整反序列化中使用的构造函数/工厂。

当我们需要反序列化一些与我们需要获取的目标实体不完全匹配的JSON时，它非常有用。

让我们看一个例子。假设我们需要反序列化以下 JSON：

```bash
{
    "id":1,
    "theName":"My bean"
}
```

但是，我们的目标实体中没有theName字段，只有一个name字段。现在我们不想更改实体本身，我们只需要通过使用 @JsonCreator 注解构造函数并使用 @JsonProperty 注解来对解组过程进行更多控制：

```java
public class BeanWithCreator {
    public int id;
    public String name;

    @JsonCreator
    public BeanWithCreator(
      @JsonProperty("id") int id, 
      @JsonProperty("theName") String name) {
        this.id = id;
        this.name = name;
    }
}
```

让我们看看实际效果：

```java
@Test
public void whenDeserializingUsingJsonCreator_thenCorrect()
  throws IOException {
 
    String json = "{"id":1,"theName":"My bean"}";

    BeanWithCreator bean = new ObjectMapper()
      .readerFor(BeanWithCreator.class)
      .readValue(json);
    assertEquals("My bean", bean.name);
}
```

### 3.2. @杰克逊注入

@JacksonInject表示属性将从注入中获取其值，而不是从 JSON 数据中获取。

在下面的示例中，我们使用@JacksonInject来注入属性id：

```java
public class BeanWithInject {
    @JacksonInject
    public int id;
    
    public String name;
}
```

它是这样工作的：

```java
@Test
public void whenDeserializingUsingJsonInject_thenCorrect()
  throws IOException {
 
    String json = "{"name":"My bean"}";
    
    InjectableValues inject = new InjectableValues.Std()
      .addValue(int.class, 1);
    BeanWithInject bean = new ObjectMapper().reader(inject)
      .forType(BeanWithInject.class)
      .readValue(json);
    
    assertEquals("My bean", bean.name);
    assertEquals(1, bean.id);
}
```

### 3.3. @JsonAnySetter

@JsonAnySetter允许我们灵活地使用Map作为标准属性。在反序列化时，JSON 中的属性将简单地添加到地图中。

首先，我们将使用@JsonAnySetter反序列化实体ExtendableBean：

```java
public class ExtendableBean {
    public String name;
    private Map<String, String> properties;

    @JsonAnySetter
    public void add(String key, String value) {
        properties.put(key, value);
    }
}
```

这是我们需要反序列化的 JSON：

```bash
{
    "name":"My bean",
    "attr2":"val2",
    "attr1":"val1"
}
```

那么这就是它们如何联系在一起的：

```java
@Test
public void whenDeserializingUsingJsonAnySetter_thenCorrect()
  throws IOException {
    String json
      = "{"name":"My bean","attr2":"val2","attr1":"val1"}";

    ExtendableBean bean = new ObjectMapper()
      .readerFor(ExtendableBean.class)
      .readValue(json);
    
    assertEquals("My bean", bean.name);
    assertEquals("val2", bean.getProperties().get("attr2"));
}
```

### 3.4. @JsonSetter

@JsonSetter是@JsonProperty 的替代方法，它将方法标记为setter 方法。

当我们需要读取一些 JSON 数据，但目标实体类与该数据不完全匹配时，这非常有用，因此我们需要调整流程以使其适合。

在下面的示例中，我们将方法 s etTheName()指定为MyBean实体中名称属性的设置器：

```java
public class MyBean {
    public int id;
    private String name;

    @JsonSetter("name")
    public void setTheName(String name) {
        this.name = name;
    }
}
```

现在，当我们需要解组一些 JSON 数据时，这非常有效：

```java
@Test
public void whenDeserializingUsingJsonSetter_thenCorrect()
  throws IOException {
 
    String json = "{"id":1,"name":"My bean"}";

    MyBean bean = new ObjectMapper()
      .readerFor(MyBean.class)
      .readValue(json);
    assertEquals("My bean", bean.getTheName());
}
```

### 3.5. @JsonDeserialize

@JsonDeserialize表示使用自定义反序列化器。

首先，我们将使用@JsonDeserialize通过CustomDateDeserializer反序列化eventDate属性：

```java
public class EventWithSerializer {
    public String name;

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public Date eventDate;
}
```

这是自定义反序列化器：

```java
public class CustomDateDeserializer
  extends StdDeserializer<Date> {

    private static SimpleDateFormat formatter
      = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

    public CustomDateDeserializer() { 
        this(null); 
    } 

    public CustomDateDeserializer(Class<?> vc) { 
        super(vc); 
    }

    @Override
    public Date deserialize(
      JsonParser jsonparser, DeserializationContext context) 
      throws IOException {
        
        String date = jsonparser.getText();
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
```

接下来是背靠背测试：

```java
@Test
public void whenDeserializingUsingJsonDeserialize_thenCorrect()
  throws IOException {
 
    String json
      = "{"name":"party","eventDate":"20-12-2014 02:30:00"}";

    SimpleDateFormat df
      = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    EventWithSerializer event = new ObjectMapper()
      .readerFor(EventWithSerializer.class)
      .readValue(json);
    
    assertEquals(
      "20-12-2014 02:30:00", df.format(event.eventDate));
}
```

### 3.6. @JsonAlias

@JsonAlias在反序列化期间为属性定义一个或多个替代名称。

让我们通过一个简单的例子看看这个注解是如何工作的：

```java
public class AliasBean {
    @JsonAlias({ "fName", "f_name" })
    private String firstName;   
    private String lastName;
}
```

这里我们有一个 POJO，我们想要将具有fName、f_name和firstName等值的 JSON 反序列化到 POJO的firstName变量中。

下面是确保此注解按预期工作的测试：

```java
@Test
public void whenDeserializingUsingJsonAlias_thenCorrect() throws IOException {
    String json = "{"fName": "John", "lastName": "Green"}";
    AliasBean aliasBean = new ObjectMapper().readerFor(AliasBean.class).readValue(json);
    assertEquals("John", aliasBean.getFirstName());
}
```

## 4.杰克逊财产包含注解

### 4.1. @JsonIgnoreProperties

@JsonIgnoreProperties是一个类级别的注解，它标记 Jackson 将忽略的属性或属性列表。

让我们看一个忽略序列化中的属性id的简单示例：

```java
@JsonIgnoreProperties({ "id" })
public class BeanWithIgnore {
    public int id;
    public String name;
}
```

现在这是确保忽略发生的测试：

```java
@Test
public void whenSerializingUsingJsonIgnoreProperties_thenCorrect()
  throws JsonProcessingException {
 
    BeanWithIgnore bean = new BeanWithIgnore(1, "My bean");

    String result = new ObjectMapper()
      .writeValueAsString(bean);
    
    assertThat(result, containsString("My bean"));
    assertThat(result, not(containsString("id")));
}
```

要无一例外地忽略 JSON 输入中的任何未知属性，我们可以设置@JsonIgnoreProperties 注解的ignoreUnknown=true。

### 4.2. @Json忽略

相比之下，@JsonIgnore注解用于标记要在字段级别忽略的属性。

让我们使用@JsonIgnore来忽略序列化中的属性id：

```java
public class BeanWithIgnore {
    @JsonIgnore
    public int id;

    public String name;
}
```

然后我们将测试以确保id被成功忽略：

```java
@Test
public void whenSerializingUsingJsonIgnore_thenCorrect()
  throws JsonProcessingException {
 
    BeanWithIgnore bean = new BeanWithIgnore(1, "My bean");

    String result = new ObjectMapper()
      .writeValueAsString(bean);
    
    assertThat(result, containsString("My bean"));
    assertThat(result, not(containsString("id")));
}
```

### 4.3. @JsonIgnoreType

@JsonIgnoreType标记要忽略的注解类型的所有属性。

我们可以使用注解来标记要忽略的所有Name类型的属性：

```java
public class User {
    public int id;
    public Name name;

    @JsonIgnoreType
    public static class Name {
        public String firstName;
        public String lastName;
    }
}
```

我们还可以测试以确保忽略正常工作：

```java
@Test
public void whenSerializingUsingJsonIgnoreType_thenCorrect()
  throws JsonProcessingException, ParseException {
 
    User.Name name = new User.Name("John", "Doe");
    User user = new User(1, name);

    String result = new ObjectMapper()
      .writeValueAsString(user);

    assertThat(result, containsString("1"));
    assertThat(result, not(containsString("name")));
    assertThat(result, not(containsString("John")));
}
```

### 4.4. @JsonInclude

我们可以使用@JsonInclude来排除具有空/空/默认值的属性。

让我们看一个从序列化中排除空值的例子：

```java
@JsonInclude(Include.NON_NULL)
public class MyBean {
    public int id;
    public String name;
}
```

这是完整的测试：

```java
public void whenSerializingUsingJsonInclude_thenCorrect()
  throws JsonProcessingException {
 
    MyBean bean = new MyBean(1, null);

    String result = new ObjectMapper()
      .writeValueAsString(bean);
    
    assertThat(result, containsString("1"));
    assertThat(result, not(containsString("name")));
}
```

### 4.5. @JsonAutoDetect

@JsonAutoDetect可以覆盖属性可见和不可见的默认语义。

首先，让我们通过一个简单的例子来看看注解是如何发挥作用的；让我们启用序列化私有属性：

```java
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class PrivateBean {
    private int id;
    private String name;
}
```

然后是测试：

```java
@Test
public void whenSerializingUsingJsonAutoDetect_thenCorrect()
  throws JsonProcessingException {
 
    PrivateBean bean = new PrivateBean(1, "My bean");

    String result = new ObjectMapper()
      .writeValueAsString(bean);
    
    assertThat(result, containsString("1"));
    assertThat(result, containsString("My bean"));
}
```

## 5. Jackson 多态类型处理注解

接下来我们看一下 Jackson 多态类型处理注解：

-   @JsonTypeInfo – 指示要包含在序列化中的类型信息的详细信息
-   @JsonSubTypes – 表示注解类型的子类型
-   @JsonTypeName – 定义一个逻辑类型名称以用于带注解的类

让我们检查一个更复杂的示例，并使用所有三个 – @JsonTypeInfo、@JsonSubTypes和@JsonTypeName –来序列化/反序列化实体Zoo：

```java
public class Zoo {
    public Animal animal;

    @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME, 
      include = As.PROPERTY, 
      property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Dog.class, name = "dog"),
        @JsonSubTypes.Type(value = Cat.class, name = "cat")
    })
    public static class Animal {
        public String name;
    }

    @JsonTypeName("dog")
    public static class Dog extends Animal {
        public double barkVolume;
    }

    @JsonTypeName("cat")
    public static class Cat extends Animal {
        boolean likesCream;
        public int lives;
    }
}
```

当我们进行序列化时：

```java
@Test
public void whenSerializingPolymorphic_thenCorrect()
  throws JsonProcessingException {
    Zoo.Dog dog = new Zoo.Dog("lacy");
    Zoo zoo = new Zoo(dog);

    String result = new ObjectMapper()
      .writeValueAsString(zoo);

    assertThat(result, containsString("type"));
    assertThat(result, containsString("dog"));
}
```

以下是使用Dog序列化Zoo实例的结果：

```javascript
{
    "animal": {
        "type": "dog",
        "name": "lacy",
        "barkVolume": 0
    }
}
```

现在进行反序列化。让我们从以下 JSON 输入开始：

```bash
{
    "animal":{
        "name":"lacy",
        "type":"cat"
    }
}
```

然后让我们看看如何将其解组到Zoo实例：

```java
@Test
public void whenDeserializingPolymorphic_thenCorrect()
throws IOException {
    String json = "{"animal":{"name":"lacy","type":"cat"}}";

    Zoo zoo = new ObjectMapper()
      .readerFor(Zoo.class)
      .readValue(json);

    assertEquals("lacy", zoo.animal.name);
    assertEquals(Zoo.Cat.class, zoo.animal.getClass());
}
```

## 6. 杰克逊总注解

接下来让我们讨论杰克逊的一些更一般的注解。

### 6.1. @JsonProperty

我们可以添加@JsonProperty注解来表示 JSON 中的属性名称。

当我们处理非标准的 getter 和 setter 时，让我们使用@JsonProperty来序列化/反序列化属性名称：

```java
public class MyBean {
    public int id;
    private String name;

    @JsonProperty("name")
    public void setTheName(String name) {
        this.name = name;
    }

    @JsonProperty("name")
    public String getTheName() {
        return name;
    }
}
```

接下来是我们的测试：

```java
@Test
public void whenUsingJsonProperty_thenCorrect()
  throws IOException {
    MyBean bean = new MyBean(1, "My bean");

    String result = new ObjectMapper().writeValueAsString(bean);
    
    assertThat(result, containsString("My bean"));
    assertThat(result, containsString("1"));

    MyBean resultBean = new ObjectMapper()
      .readerFor(MyBean.class)
      .readValue(result);
    assertEquals("My bean", resultBean.getTheName());
}
```

### 6.2. @Json格式

@JsonFormat注解指定序列化日期/时间值时的格式。

在下面的示例中，我们使用@JsonFormat来控制属性eventDate的格式：

```java
public class EventWithFormat {
    public String name;

    @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "dd-MM-yyyy hh:mm:ss")
    public Date eventDate;
}
```

然后是测试：

```java
@Test
public void whenSerializingUsingJsonFormat_thenCorrect()
  throws JsonProcessingException, ParseException {
    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    df.setTimeZone(TimeZone.getTimeZone("UTC"));

    String toParse = "20-12-2014 02:30:00";
    Date date = df.parse(toParse);
    EventWithFormat event = new EventWithFormat("party", date);
    
    String result = new ObjectMapper().writeValueAsString(event);
    
    assertThat(result, containsString(toParse));
}
```

### 6.3. @JsonUnwrapped

@JsonUnwrapped定义了在序列化/反序列化时应该展开/展平的值。

让我们看看这是如何工作的；我们将使用注解来解包属性名称：

```java
public class UnwrappedUser {
    public int id;

    @JsonUnwrapped
    public Name name;

    public static class Name {
        public String firstName;
        public String lastName;
    }
}
```

现在让我们序列化这个类的一个实例：

```java
@Test
public void whenSerializingUsingJsonUnwrapped_thenCorrect()
  throws JsonProcessingException, ParseException {
    UnwrappedUser.Name name = new UnwrappedUser.Name("John", "Doe");
    UnwrappedUser user = new UnwrappedUser(1, name);

    String result = new ObjectMapper().writeValueAsString(user);
    
    assertThat(result, containsString("John"));
    assertThat(result, not(containsString("name")));
}
```

最后，这是输出的样子——静态嵌套类的字段与其他字段一起展开：

```java
{
    "id":1,
    "firstName":"John",
    "lastName":"Doe"
}
```

### 6.4. @JsonView

@JsonView指示将在其中包含属性以进行序列化/反序列化的视图。

例如，我们将使用@JsonView来序列化Item实体的实例。

首先，让我们从视图开始：

```java
public class Views {
    public static class Public {}
    public static class Internal extends Public {}
}
```

接下来是使用视图的Item实体：

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

最后，完整测试：

```java
@Test
public void whenSerializingUsingJsonView_thenCorrect()
  throws JsonProcessingException {
    Item item = new Item(2, "book", "John");

    String result = new ObjectMapper()
      .writerWithView(Views.Public.class)
      .writeValueAsString(item);

    assertThat(result, containsString("book"));
    assertThat(result, containsString("2"));
    assertThat(result, not(containsString("John")));
}
```

### 6.5. @JsonManagedReference，@JsonBackReference

@JsonManagedReference和@JsonBackReference注解可以处理父/子关系并解决循环。

在以下示例中，我们使用@JsonManagedReference和@JsonBackReference来序列化我们的ItemWithRef实体：

```java
public class ItemWithRef {
    public int id;
    public String itemName;

    @JsonManagedReference
    public UserWithRef owner;
}
```

我们的UserWithRef实体：

```java
public class UserWithRef {
    public int id;
    public String name;

    @JsonBackReference
    public List<ItemWithRef> userItems;
}
```

然后是测试：

```java
@Test
public void whenSerializingUsingJacksonReferenceAnnotation_thenCorrect()
  throws JsonProcessingException {
    UserWithRef user = new UserWithRef(1, "John");
    ItemWithRef item = new ItemWithRef(2, "book", user);
    user.addItem(item);

    String result = new ObjectMapper().writeValueAsString(item);

    assertThat(result, containsString("book"));
    assertThat(result, containsString("John"));
    assertThat(result, not(containsString("userItems")));
}
```

### 6.6. @JsonIdentityInfo

@JsonIdentityInfo表示在序列化/反序列化值时应该使用对象标识，例如在处理无限递归类型的问题时。

在以下示例中，我们有一个ItemWithIdentity实体，它与UserWithIdentity实体具有双向关系：

```java
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class,
  property = "id")
public class ItemWithIdentity {
    public int id;
    public String itemName;
    public UserWithIdentity owner;
}
```

UserWithIdentity实体：

```java
@JsonIdentityInfo(
  generator = ObjectIdGenerators.PropertyGenerator.class,
  property = "id")
public class UserWithIdentity {
    public int id;
    public String name;
    public List<ItemWithIdentity> userItems;
}
```

现在让我们看看如何处理无限递归问题：

```java
@Test
public void whenSerializingUsingJsonIdentityInfo_thenCorrect()
  throws JsonProcessingException {
    UserWithIdentity user = new UserWithIdentity(1, "John");
    ItemWithIdentity item = new ItemWithIdentity(2, "book", user);
    user.addItem(item);

    String result = new ObjectMapper().writeValueAsString(item);

    assertThat(result, containsString("book"));
    assertThat(result, containsString("John"));
    assertThat(result, containsString("userItems"));
}
```

这是序列化项目和用户的完整输出：

```javascript
{
    "id": 2,
    "itemName": "book",
    "owner": {
        "id": 1,
        "name": "John",
        "userItems": [
            2
        ]
    }
}
```

### 6.7. @Json过滤器

@JsonFilter注解指定在序列化期间使用的过滤器。

首先，我们定义实体并指向过滤器：

```java
@JsonFilter("myFilter")
public class BeanWithFilter {
    public int id;
    public String name;
}
```

现在在完整测试中，我们定义了过滤器，它从序列化中排除了除name之外的所有其他属性：

```java
@Test
public void whenSerializingUsingJsonFilter_thenCorrect()
  throws JsonProcessingException {
    BeanWithFilter bean = new BeanWithFilter(1, "My bean");

    FilterProvider filters 
      = new SimpleFilterProvider().addFilter(
        "myFilter", 
        SimpleBeanPropertyFilter.filterOutAllExcept("name"));

    String result = new ObjectMapper()
      .writer(filters)
      .writeValueAsString(bean);

    assertThat(result, containsString("My bean"));
    assertThat(result, not(containsString("id")));
}
```

## 7.自定义杰克逊注解

接下来让我们看看如何创建自定义 Jackson 注解。我们可以使用@JacksonAnnotationsInside注解：

```java
@Retention(RetentionPolicy.RUNTIME)
    @JacksonAnnotationsInside
    @JsonInclude(Include.NON_NULL)
    @JsonPropertyOrder({ "name", "id", "dateCreated" })
    public @interface CustomAnnotation {}
```

现在，如果我们在实体上使用新注解：

```java
@CustomAnnotation
public class BeanWithCustomAnnotation {
    public int id;
    public String name;
    public Date dateCreated;
}
```

我们可以看到它如何将现有注解组合成一个简单的自定义注解，我们可以将其用作速记：

```java
@Test
public void whenSerializingUsingCustomAnnotation_thenCorrect()
  throws JsonProcessingException {
    BeanWithCustomAnnotation bean 
      = new BeanWithCustomAnnotation(1, "My bean", null);

    String result = new ObjectMapper().writeValueAsString(bean);

    assertThat(result, containsString("My bean"));
    assertThat(result, containsString("1"));
    assertThat(result, not(containsString("dateCreated")));
}
```

序列化过程的输出：

```bash
{
    "name":"My bean",
    "id":1
}
```

## 8. Jackson MixIn 注解

接下来我们看看如何使用Jackson MixIn注解。

例如，让我们使用 MixIn 注解来忽略User类型的属性：

```java
public class Item {
    public int id;
    public String itemName;
    public User owner;
}
@JsonIgnoreType
public class MyMixInForIgnoreType {}
```

然后让我们看看实际效果：

```java
@Test
public void whenSerializingUsingMixInAnnotation_thenCorrect() 
  throws JsonProcessingException {
    Item item = new Item(1, "book", null);

    String result = new ObjectMapper().writeValueAsString(item);
    assertThat(result, containsString("owner"));

    ObjectMapper mapper = new ObjectMapper();
    mapper.addMixIn(User.class, MyMixInForIgnoreType.class);

    result = mapper.writeValueAsString(item);
    assertThat(result, not(containsString("owner")));
}
```

## 9.禁用杰克逊注解

最后，让我们看看如何禁用所有 Jackson 注解。我们可以通过禁用MapperFeature 来做到这一点。USE_ANNOTATIONS 如下例所示：

```java
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "name", "id" })
public class MyBean {
    public int id;
    public String name;
}
```

现在，在禁用注解后，这些应该没有效果并且应该应用库的默认值：

```java
@Test
public void whenDisablingAllAnnotations_thenAllDisabled()
  throws IOException {
    MyBean bean = new MyBean(1, null);

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(MapperFeature.USE_ANNOTATIONS);
    String result = mapper.writeValueAsString(bean);
    
    assertThat(result, containsString("1"));
    assertThat(result, containsString("name"));
}
```

禁用注解前的序列化结果：

```bash
{"id":1}
```

禁用注解后的序列化结果：

```bash
{
    "id":1,
    "name":null
}
```

## 10.总结

在本文中，我们研究了 Jackson 注解，只是触及了通过正确使用它们可以获得的灵活性的皮毛。