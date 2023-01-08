## 1. 概述

在本教程中，我们将介绍最常见的 Jackson 异常— JsonMappingException和UnrecognizedPropertyException。

最后，我们将简要讨论 Jackson 的“No such method”错误。

## 延伸阅读：

## [Jackson——自定义序列化器](https://www.baeldung.com/jackson-custom-serialization)

通过使用自定义序列化器控制 Jackson 2 的 JSON 输出。

[阅读更多](https://www.baeldung.com/jackson-custom-serialization)→

## [Jackson 注解示例](https://www.baeldung.com/jackson-annotations)

Jackson 的核心基本上是一组注解——确保你理解这些。

[阅读更多](https://www.baeldung.com/jackson-annotations)→

## [Jackson 中的自定义反序列化入门](https://www.baeldung.com/jackson-deserialization)

使用 Jackson 将自定义 JSON 映射到任何 java 实体图，并完全控制反序列化过程。

[阅读更多](https://www.baeldung.com/jackson-deserialization)→



## 2. JsonMappingException：无法构造实例

### 2.1. 问题

首先，让我们看一下 JsonMappingException: Can Not Construct Instance Of。

如果Jackson 无法创建该类的实例，则会抛出此异常，如果该类是抽象类或它只是一个接口，则会发生这种情况。

在这里，我们将尝试反序列化Zoo类的一个实例，该实例具有抽象类型为Animal的动物属性：

```java
public class Zoo {
    public Animal animal;
    
    public Zoo() { }
}

abstract class Animal {
    public String name;
    
    public Animal() { }
}

class Cat extends Animal {
    public int lives;
    
    public Cat() { }
}
```

当我们尝试将 JSON字符串反序列化为Zoo 实例时，它抛出 JsonMappingException: Can Not Construct Instance Of:

```java
@Test(expected = JsonMappingException.class)
public void givenAbstractClass_whenDeserializing_thenException() 
  throws IOException {
    String json = "{"animal":{"name":"lacy"}}";
    ObjectMapper mapper = new ObjectMapper();

    mapper.reader().forType(Zoo.class).readValue(json);
}
```

这是完整的例外：

```bash
com.fasterxml.jackson.databind.JsonMappingException: 
Can not construct instance of org.baeldung.jackson.exception.Animal,
  problem: abstract types either need to be mapped to concrete types, 
  have custom deserializer, 
  or be instantiated with additional type information
  at 
[Source: {"animal":{"name":"lacy"}}; line: 1, column: 2] 
(through reference chain: org.baeldung.jackson.exception.Zoo["animal"])
	at c.f.j.d.JsonMappingException.from(JsonMappingException.java:148)
```

### 2.2. 解决方案

我们可以通过一个简单的注解来解决这个问题——抽象类上的@JsonDeserialize ：

```java
@JsonDeserialize(as = Cat.class)
abstract class Animal {...}
```

请注意，如果我们有多个抽象类的子类型，我们应该考虑包括子类型信息，如文章[Inheritance With Jackson](https://www.baeldung.com/jackson-inheritance)中所示。

## 3. JsonMappingException：没有合适的构造函数

### 3.1. 问题

现在让我们看看常见的 JsonMappingException: No Suitable Constructor found for type。

如果Jackson 无法访问构造函数，则会抛出此异常。

在以下示例中，类User没有默认构造函数：

```java
public class User {
    public int id;
    public String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
```

当我们尝试将 JSON 字符串反序列化为用户时，会抛出 JsonMappingException: No Suitable Constructor Found：

```java
@Test(expected = JsonMappingException.class)
public void givenNoDefaultConstructor_whenDeserializing_thenException() 
  throws IOException {
    String json = "{"id":1,"name":"John"}";
    ObjectMapper mapper = new ObjectMapper();

    mapper.reader().forType(User.class).readValue(json);
}
```

这是完全例外：

```bash
com.fasterxml.jackson.databind.JsonMappingException: 
No suitable constructor found for type 
[simple type, class org.baeldung.jackson.exception.User]:
 can not instantiate from JSON object (need to add/enable type information?)
 at [Source: {"id":1,"name":"John"}; line: 1, column: 2]
        at c.f.j.d.JsonMappingException.from(JsonMappingException.java:148)
```

### 3.2. 解决方案

为了解决这个问题，我们只需添加一个默认构造函数：

```java
public class User {
    public int id;
    public String name;

    public User() {
        super();
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
```

现在，当我们反序列化时，该过程将正常工作：

```java
@Test
public void givenDefaultConstructor_whenDeserializing_thenCorrect() 
  throws IOException {
 
    String json = "{"id":1,"name":"John"}";
    ObjectMapper mapper = new ObjectMapper();

    User user = mapper.reader()
      .forType(User.class).readValue(json);
    assertEquals("John", user.name);
}
```

## 4. JsonMappingException：根名称与预期不匹配

### 4.1. 问题

接下来我们看一下JsonMappingException: Root Name Does Not Match Expected。

如果JSON 与 Jackson 正在寻找的内容不完全匹配，则会抛出此异常。

例如，可以包装主 JSON：

```java
@Test(expected = JsonMappingException.class)
public void givenWrappedJsonString_whenDeserializing_thenException()
  throws IOException {
    String json = "{"user":{"id":1,"name":"John"}}";

    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);

    mapper.reader().forType(User.class).readValue(json);
}
```

这是完整的例外：

```bash
com.fasterxml.jackson.databind.JsonMappingException:
Root name 'user' does not match expected ('User') for type
 [simple type, class org.baeldung.jackson.dtos.User]
 at [Source: {"user":{"id":1,"name":"John"}}; line: 1, column: 2]
   at c.f.j.d.JsonMappingException.from(JsonMappingException.java:148)

```

### 4.2. 解决方案

我们可以使用注解@JsonRootName来解决这个问题：

```java
@JsonRootName(value = "user")
public class UserWithRoot {
    public int id;
    public String name;
}
```

当我们尝试反序列化包装的 JSON 时，它可以正常工作：

```java
@Test
public void 
  givenWrappedJsonStringAndConfigureClass_whenDeserializing_thenCorrect() 
  throws IOException {
 
    String json = "{"user":{"id":1,"name":"John"}}";

    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);

    UserWithRoot user = mapper.reader()
      .forType(UserWithRoot.class)
      .readValue(json);
    assertEquals("John", user.name);
}
```

## 5. JsonMappingException：找不到类的序列化程序

### 5.1. 问题

现在让我们看一下 JsonMappingException: No Serializer Found for Class。

如果我们尝试序列化一个实例，而它的属性和它们的 getter 是私有的，就会抛出这个异常。

我们将尝试序列化UserWithPrivateFields：

```java
public class UserWithPrivateFields {
    int id;
    String name;
}
```

当我们尝试序列化UserWithPrivateFields的实例时，抛出 JsonMappingException: No Serializer Found for Class：

```java
@Test(expected = JsonMappingException.class)
public void givenClassWithPrivateFields_whenSerializing_thenException() 
  throws IOException {
    UserWithPrivateFields user = new UserWithPrivateFields(1, "John");

    ObjectMapper mapper = new ObjectMapper();
    mapper.writer().writeValueAsString(user);
}
```

这是完全例外：

```bash
com.fasterxml.jackson.databind.JsonMappingException: 
No serializer found for class org.baeldung.jackson.exception.UserWithPrivateFields
 and no properties discovered to create BeanSerializer 
(to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) )
  at c.f.j.d.ser.impl.UnknownSerializer.failForEmpty(UnknownSerializer.java:59)
```

### 5.2. 解决方案

我们可以通过配置ObjectMapper可见性来解决这个问题：

```java
@Test
public void givenClassWithPrivateFields_whenConfigureSerializing_thenCorrect() 
  throws IOException {
 
    UserWithPrivateFields user = new UserWithPrivateFields(1, "John");

    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

    String result = mapper.writer().writeValueAsString(user);
    assertThat(result, containsString("John"));
}
```

或者我们可以使用注解@JsonAutoDetect：

```java
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class UserWithPrivateFields { ... }
```

当然，如果我们确实可以选择修改类的源代码，我们也可以添加 getters 供 Jackson 使用。

## 6. JsonMappingException：无法反序列化实例

### 6.1. 问题

接下来我们看一下JsonMappingException: Can Not Deserialize Instance Of。

如果在反序列化时使用了错误的类型，则会抛出此异常。

在这个例子中，我们试图反序列化一个用户列表：

```java
@Test(expected = JsonMappingException.class)
public void givenJsonOfArray_whenDeserializing_thenException() 
  throws JsonProcessingException, IOException {
 
    String json 
      = "[{"id":1,"name":"John"},{"id":2,"name":"Adam"}]";
    ObjectMapper mapper = new ObjectMapper();
    mapper.reader().forType(User.class).readValue(json);
}
```

这是完整的例外：

```bash
com.fasterxml.jackson.databind.JsonMappingException:
Can not deserialize instance of 
  org.baeldung.jackson.dtos.User out of START_ARRAY token
  at [Source: [{"id":1,"name":"John"},{"id":2,"name":"Adam"}]; line: 1, column: 1]
  at c.f.j.d.JsonMappingException.from(JsonMappingException.java:148)
```

### 6.2. 解决方案

我们可以通过将类型从User更改为List<User>来解决这个问题：

```java
@Test
public void givenJsonOfArray_whenDeserializing_thenCorrect() 
  throws JsonProcessingException, IOException {
 
    String json
      = "[{"id":1,"name":"John"},{"id":2,"name":"Adam"}]";
   
    ObjectMapper mapper = new ObjectMapper();
    List<User> users = mapper.reader()
      .forType(new TypeReference<List<User>>() {})
      .readValue(json);

    assertEquals(2, users.size());
}
```

## 7.无法识别的属性异常

### 7.1. 问题

现在让我们看看UnrecognizedPropertyException。

如果在反序列化时 JSON 字符串中存在未知属性，则会抛出此异常。

我们将尝试反序列化带有额外属性“ checked ”的 JSON 字符串：

```java
@Test(expected = UnrecognizedPropertyException.class)
public void givenJsonStringWithExtra_whenDeserializing_thenException() 
  throws IOException {
 
    String json = "{"id":1,"name":"John", "checked":true}";

    ObjectMapper mapper = new ObjectMapper();
    mapper.reader().forType(User.class).readValue(json);
}
```

这是完整的例外：

```bash
com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException:
Unrecognized field "checked" (class org.baeldung.jackson.dtos.User),
 not marked as ignorable (2 known properties: "id", "name"])
 at [Source: {"id":1,"name":"John", "checked":true}; line: 1, column: 38]
 (through reference chain: org.baeldung.jackson.dtos.User["checked"])
  at c.f.j.d.exc.UnrecognizedPropertyException.from(
    UnrecognizedPropertyException.java:51)
```

### 7.2. 解决方案

我们可以通过配置ObjectMapper来解决这个问题：

```java
@Test
public void givenJsonStringWithExtra_whenConfigureDeserializing_thenCorrect() 
  throws IOException {
 
    String json = "{"id":1,"name":"John", "checked":true}";

    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    User user = mapper.reader().forType(User.class).readValue(json);
    assertEquals("John", user.name);
}
```

或者我们可以使用注解@JsonIgnoreProperties：

```java
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {...}
```

## 8. JsonParseException：意外字符(“”(代码 39))

### 8.1. 问题

接下来，让我们讨论JsonParseException: Unexpected character (”' (code 39))。

如果要反序列化的 JSON 字符串包含单引号而不是双引号，则会抛出此异常。

我们将尝试反序列化包含单引号的 JSON 字符串：

```java
@Test(expected = JsonParseException.class)
public void givenStringWithSingleQuotes_whenDeserializing_thenException() 
  throws JsonProcessingException, IOException {
 
    String json = "{'id':1,'name':'John'}";
    ObjectMapper mapper = new ObjectMapper();

    mapper.reader()
      .forType(User.class).readValue(json);
}
```

这是完整的例外：

```bash
com.fasterxml.jackson.core.JsonParseException:
Unexpected character (''' (code 39)): 
  was expecting double-quote to start field name
  at [Source: {'id':1,'name':'John'}; line: 1, column: 3]
  at c.f.j.core.JsonParser._constructError(JsonParser.java:1419)
```

### 8.2. 解决方案

我们可以通过配置ObjectMapper允许单引号来解决这个问题：

```java
@Test
public void 
  givenStringWithSingleQuotes_whenConfigureDeserializing_thenCorrect() 
  throws JsonProcessingException, IOException {
 
    String json = "{'id':1,'name':'John'}";

    JsonFactory factory = new JsonFactory();
    factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
    ObjectMapper mapper = new ObjectMapper(factory);

    User user = mapper.reader().forType(User.class)
      .readValue(json);
 
    assertEquals("John", user.name);
}
```

## 9.杰克逊NoSuchMethodError

最后，让我们快速讨论一下 Jackson 的“No such method”错误。

当抛出java.lang.NoSuchMethodError Exception 时，通常是因为我们的类路径上有多个(且不兼容的)Jackson jar 版本。

这是完整的例外：

```bash
java.lang.NoSuchMethodError:
com.fasterxml.jackson.core.JsonParser.getValueAsString()Ljava/lang/String;
 at c.f.j.d.deser.std.StringDeserializer.deserialize(StringDeserializer.java:24)
```

## 10.总结

在这篇文章中，我们深入研究了最常见的 Jackson 问题——异常和错误——寻找潜在原因和每个问题的解决方案。