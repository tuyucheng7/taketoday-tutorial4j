## 1. 概述

很长一段时间，Java 中没有处理 JSON 的标准。用于 JSON 处理的最常见的库是 Jackson 和 Gson。

最近，Java EE7 附带了一个用于解析和生成 JSON 的 API([JSR 353：用于 JSON 处理的JavaAPI](https://www.jcp.org/en/jsr/detail?id=353))。

最后，随着 JEE 8 的发布，有一个标准化的 API([JSR 367：用于 JSON 绑定的JavaAPI(JSON-B)](https://jcp.org/en/jsr/detail?id=367))。

目前，它的主要实现是[Eclipse Yasson (RI)](https://github.com/eclipse/yasson)和[Apache Johnzon](https://johnzon.apache.org/)。

## 2. JSON-B API

### 2.1. Maven 依赖

让我们从添加必要的依赖项开始。

请记住，在许多情况下，包含所选实现的依赖项就足够了，并且javax.json.bind-api将被传递地包含：

```xml
<dependency>
    <groupId>javax.json.bind</groupId>
    <artifactId>javax.json.bind-api</artifactId>
    <version>1.0</version>
</dependency>
```

最新版本可以在[Maven Central](https://search.maven.org/classic/#search|ga|1|javax.json.bind-api)找到。

## 3. 使用 Eclipse 亚森

Eclipse Yasson 是JSON Binding API ( [JSR-367](https://jcp.org/en/jsr/detail?id=367) ) 的官方参考实现。

### 3.1. Maven 依赖

要使用它，我们需要在我们的 Maven 项目中包含以下依赖项：

```xml
<dependency>
    <groupId>org.eclipse</groupId>
    <artifactId>yasson</artifactId>
    <version>1.0.1</version>
</dependency>
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>javax.json</artifactId>
    <version>1.1.2</version>
</dependency>
```

最新版本可以在[Maven Central 找到。](https://search.maven.org/classic/#search|ga|1|a%3A"yasson")

## 4. 使用 Apache Johnzon

我们可以使用的另一个实现是 Apache Johnzon，它符合 JSON-P (JSR-353) 和 JSON-B (JSR-367) API。

### 4.1. Maven 依赖

要使用它，我们需要在我们的 Maven 项目中包含以下依赖项：

```xml
<dependency>
    <groupId>org.apache.geronimo.specs</groupId>
    <artifactId>geronimo-json_1.1_spec</artifactId>
    <version>1.0</version>
</dependency>
<dependency>
    <groupId>org.apache.johnzon</groupId>
    <artifactId>johnzon-jsonb</artifactId>
    <version>1.1.4</version>
</dependency>
```

最新版本可以在[Maven Central 找到。](https://search.maven.org/classic/#search|ga|1|a%3A"johnzon-jsonb")

## 5. API 特点

API 提供用于自定义序列化/反序列化的注解。

让我们创建一个简单的类并查看示例配置的样子：

```java
public class Person {

    private int id;

    @JsonbProperty("person-name")
    private String name;
    
    @JsonbProperty(nillable = true)
    private String email;
    
    @JsonbTransient
    private int age;
     
    @JsonbDateFormat("dd-MM-yyyy")
    private LocalDate registeredDate;
    
    private BigDecimal salary;
    
    @JsonbNumberFormat(locale = "en_US", value = "#0.0")
    public BigDecimal getSalary() {
        return salary;
    }
 
    // standard getters and setters
}
```

序列化后，此类的对象将如下所示：

```javascript
{
   "email":"jhon@test.com",
   "id":1,
   "person-name":"Jhon",
   "registeredDate":"07-09-2019",
   "salary":"1000.0"
}
```

这里使用的注解是：

-   @JsonbProperty – 用于指定自定义字段名称
-   @JsonbTransient – 当我们想在反序列化/序列化过程中忽略该字段时
-   @JsonbDateFormat – 当我们要定义日期的显示格式时
-   @JsonbNumberFormat – 用于指定数值的显示格式
-   @JsonbNillable – 用于启用空值的序列化

### 5.1. 序列化和反序列化

首先，要获取对象的 JSON 表示形式，我们需要使用JsonbBuilder类及其toJson()方法。

首先，让我们创建一个简单的Person对象，如下所示：

```java
Person person = new Person(
  1, 
  "Jhon", 
  "jhon@test.com", 
  20, 
  LocalDate.of(2019, 9, 7), 
  BigDecimal.valueOf(1000));
```

并且，实例化Jsonb类：

```html
Jsonb jsonb = JsonbBuilder.create();
```

然后，我们使用toJson方法：

```java
String jsonPerson = jsonb.toJson(person);
```

要获取以下 JSON 表示形式：

```javascript
{
    "email":"jhon@test.com",
    "id":1,
    "person-name":"Jhon",
    "registeredDate":"07-09-2019",
    "salary":"1000.0"
}
```

如果我们想以其他方式进行转换，我们可以使用fromJson方法：

```java
Person person = jsonb.fromJson(jsonPerson, Person.class);
```

当然，我们也可以处理集合：

```java
List<Person> personList = Arrays.asList(...);
String jsonArrayPerson = jsonb.toJson(personList);
```

要获取以下 JSON 表示形式：

```javascript
[ 
    {
      "email":"jhon@test.com",
      "id":1,
      "person-name":"Jhon", 
      "registeredDate":"09-09-2019",
      "salary":"1000.0"
    },
    {
      "email":"jhon1@test.com",
      "id":2,
      "person-name":"Jhon",
      "registeredDate":"09-09-2019",
      "salary":"1500.0"
    },
    ...
]
```

要从 JSON 数组转换为列表，我们将使用fromJson API：

```java
List<Person> personList = jsonb.fromJson(
  personJsonArray, 
  new ArrayList<Person>(){}.getClass().getGenericSuperclass()
);
```

### 5.2. 使用JsonbConfig自定义映射

JsonbConfig类允许我们自定义所有类的映射过程。

例如，我们可以更改默认命名策略或属性顺序。

现在，我们将使用LOWER_CASE_WITH_UNDERSCORES策略：

```java
JsonbConfig config = new JsonbConfig().withPropertyNamingStrategy(
  PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
Jsonb jsonb = JsonbBuilder.create(config);
String jsonPerson = jsonb.toJson(person);
```

要获取以下 JSON 表示形式：

```javascript
{
   "email":"jhon@test.com",
   "id":1,
   "person-name":"Jhon",
   "registered_date":"07-09-2019",
   "salary":"1000.0"
}
```

现在，我们将使用REVERSE策略更改属性顺序。使用此策略，属性的顺序与字典顺序相反。
这也可以在编译时使用注解@JsonbPropertyOrder 进行配置。让我们看看它的实际效果：

```java
JsonbConfig config 
  = new JsonbConfig().withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE);
Jsonb jsonb = JsonbBuilder.create(config);
String jsonPerson = jsonb.toJson(person);

```

要获取以下 JSON 表示形式：

```javascript
{
    "salary":"1000.0",
    "registeredDate":"07-09-2019",
    "person-name":"Jhon",
    "id":1,
    "email":"jhon@test.com"
}
```

### 5.3. 使用适配器自定义映射

当注解和JsonbConfig类对我们来说不够时，我们可以使用适配器。

要使用它们，我们需要实现JsonbAdapter接口，该接口定义了以下方法：

-   adaptToJson – 使用此方法，我们可以为序列化过程使用自定义转换逻辑。
-   adaptFromJson – 此方法允许我们为反序列化过程使用自定义转换逻辑。

让我们创建一个PersonAdapter来处理Person类的id和name属性：

```java
public class PersonAdapter implements JsonbAdapter<Person, JsonObject> {

    @Override
    public JsonObject adaptToJson(Person p) throws Exception {
        return Json.createObjectBuilder()
          .add("id", p.getId())
          .add("name", p.getName())
          .build();
    }

    @Override
    public Person adaptFromJson(JsonObject adapted) throws Exception {
        Person person = new Person();
        person.setId(adapted.getInt("id"));
        person.setName(adapted.getString("name"));
        return person;
    }
}
```

此外，我们会将适配器分配给我们的JsonbConfig实例：

```java
JsonbConfig config = new JsonbConfig().withAdapters(new PersonAdapter());
Jsonb jsonb = JsonbBuilder.create(config);
```

我们将获得以下 JSON 表示形式：

```javascript
{
    "id":1, 
    "name":"Jhon"
}
```

## 六. 总结

在本教程中，我们看到了如何使用可用实现将 JSON-B API 与Java应用程序集成的示例，以及在编译和运行时自定义序列化和反序列化的示例。