## 1. 概述

本文涵盖了上一篇文章[“Jackson 注解指南”](https://www.baeldung.com/jackson-annotations)中未涵盖的一些其他注解——我们将介绍其中的七个注解。

## 2. @JsonIdentityReference

@JsonIdentityReference用于自定义对对象的引用，这些对象将被序列化为对象标识而不是完整的 POJO。它与@JsonIdentityInfo合作，强制在每个序列化中使用对象标识，这与@JsonIdentityReference不存在时的所有但第一次不同。这对注解在处理对象之间的循环依赖时最有用。请参阅[杰克逊 – 双向关系](https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion)文章的第 4 节以获取更多信息。

为了演示@JsonIdentityReference的使用，我们将定义两个不同的 bean 类，不带和带此注解。

没有@JsonIdentityReference的 bean ：

```java
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class BeanWithoutIdentityReference {
    private int id;
    private String name;

    // constructor, getters and setters
}
```

对于使用@JsonIdentityReference的 bean ，我们选择id属性作为对象标识：

```java
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIdentityReference(alwaysAsId = true)
public class BeanWithIdentityReference {
    private int id;
    private String name;
    
    // constructor, getters and setters
}
```

在第一种情况下，@JsonIdentityReference不存在，该 bean 被序列化并包含其属性的完整详细信息：

```java
BeanWithoutIdentityReference bean 
  = new BeanWithoutIdentityReference(1, "Bean Without Identity Reference Annotation");
String jsonString = mapper.writeValueAsString(bean);
```

上面序列化的输出：

```java
{
    "id": 1,
    "name": "Bean Without Identity Reference Annotation"
}
```

当使用@JsonIdentityReference时，bean 被序列化为一个简单的标识：

```java
BeanWithIdentityReference bean 
  = new BeanWithIdentityReference(1, "Bean With Identity Reference Annotation");
String jsonString = mapper.writeValueAsString(bean);
assertEquals("1", jsonString);
```

## 3. @JsonAppend

@JsonAppend注解用于在序列化对象时向对象添加虚拟属性以及常规属性。当我们想将补充信息直接添加到 JSON 字符串中而不是更改类定义时，这是必需的。例如，将 bean 的版本元数据插入相应的 JSON 文档可能比为其提供附加属性更方便。

假设我们有一个没有@JsonAppend的 bean ，如下所示：

```java
public class BeanWithoutAppend {
    private int id;
    private String name;

    // constructor, getters and setters
}
```

测试将确认在没有@JsonAppend注解的情况下，序列化输出不包含有关补充版本属性的信息，尽管我们试图添加到ObjectWriter对象：

```java
BeanWithoutAppend bean = new BeanWithoutAppend(2, "Bean Without Append Annotation");
ObjectWriter writer 
  = mapper.writerFor(BeanWithoutAppend.class).withAttribute("version", "1.0");
String jsonString = writer.writeValueAsString(bean);
```

序列化输出：

```java
{
    "id": 2,
    "name": "Bean Without Append Annotation"
}
```

现在，假设我们有一个用@JsonAppend注解的 bean ：

```java
@JsonAppend(attrs = { 
  @JsonAppend.Attr(value = "version") 
})
public class BeanWithAppend {
    private int id;
    private String name;

    // constructor, getters and setters
}
```

与前一个类似的测试将验证在应用@JsonAppend注解时，补充属性在序列化后包含在内：

```java
BeanWithAppend bean = new BeanWithAppend(2, "Bean With Append Annotation");
ObjectWriter writer 
  = mapper.writerFor(BeanWithAppend.class).withAttribute("version", "1.0");
String jsonString = writer.writeValueAsString(bean);
```

该序列化的输出显示已添加版本属性：

```java
{
    "id": 2,
    "name": "Bean With Append Annotation",
    "version": "1.0"
}
```

## 4. @JsonNaming

@JsonNaming注解用于选择序列化中属性的命名策略，覆盖默认值。使用value元素，我们可以指定任何策略，包括自定义策略。

除了默认的LOWER_CAMEL_CASE(例如lowerCamelCase)之外，Jackson 库还为我们提供了四种其他内置属性命名策略以方便使用：

-   KEBAB_CASE：名称元素由连字符分隔，例如kebab-case。
-   LOWER_CASE：所有字母都是小写的，没有分隔符，例如lowercase。
-   SNAKE_CASE：所有字母均为小写字母，下划线作为名称元素之间的分隔符，例如snake_case。
-   UPPER_CAMEL_CASE：所有名称元素，包括第一个元素，都以大写字母开头，然后是小写字母，并且没有分隔符，例如UpperCamelCase。

此示例将说明使用 snake case 名称序列化属性的方法，其中名为beanName的属性被序列化为bean_name。

给定一个 bean 定义：

```java
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NamingBean {
    private int id;
    private String beanName;

    // constructor, getters and setters
}
```

下面的测试表明指定的命名规则按要求工作：

```java
NamingBean bean = new NamingBean(3, "Naming Bean");
String jsonString = mapper.writeValueAsString(bean);        
assertThat(jsonString, containsString("bean_name"));
```

jsonString变量包含以下数据：

```java
{
    "id": 3,
    "bean_name": "Naming Bean"
}
```

## 5. @JsonPropertyDescription

Jackson 库能够在名为[JSON Schema](https://github.com/FasterXML/jackson-module-jsonSchema)的单独模块的帮助下为Java类型创建 JSON 模式。当我们想要在序列化Java对象时指定预期输出，或者在反序列化之前验证 JSON 文档时，该模式很有用。

@JsonPropertyDescription注解允许通过提供描述字段将人类可读的描述添加到创建的 JSON 模式中。

本节使用下面声明的 bean 来演示@JsonPropertyDescription的功能：

```java
public class PropertyDescriptionBean {
    private int id;
    @JsonPropertyDescription("This is a description of the name property")
    private String name;

    // getters and setters
}
```

添加description字段生成 JSON schema 的方法如下所示：

```java
SchemaFactoryWrapper wrapper = new SchemaFactoryWrapper();
mapper.acceptJsonFormatVisitor(PropertyDescriptionBean.class, wrapper);
JsonSchema jsonSchema = wrapper.finalSchema();
String jsonString = mapper.writeValueAsString(jsonSchema);
assertThat(jsonString, containsString("This is a description of the name property"));
```

可以看到，JSON schema 生成成功：

```java
{
    "type": "object",
    "id": "urn:jsonschema:com:baeldung:jackson:annotation:extra:PropertyDescriptionBean",
    "properties": 
    {
        "name": 
        {
            "type": "string",
            "description": "This is a description of the name property"
        },

        "id": 
        {
            "type": "integer"
        }
    }
}
```

## 6. @JsonPOJOBuilder

@JsonPOJOBuilder注解用于配置构建器类，以在命名约定与默认约定不同时自定义 JSON 文档的反序列化以恢复 POJO 。

假设我们需要反序列化以下 JSON 字符串：

```java
{
    "id": 5,
    "name": "POJO Builder Bean"
}
```

该 JSON 源将用于创建POJOBuilderBean的实例：

```java
@JsonDeserialize(builder = BeanBuilder.class)
public class POJOBuilderBean {
    private int identity;
    private String beanName;

    // constructor, getters and setters
}
```

bean 的属性名称与 JSON 字符串中字段的名称不同。这是@JsonPOJOBuilder来救援的地方。

@JsonPOJOBuilder注解带有两个属性：

-   buildMethodName：无参数方法的名称，用于在将 JSON 字段绑定到该 bean 的属性后实例化预期的 bean。默认名称是build。
-   withPrefix：用于自动检测 JSON 和 bean 属性之间匹配的名称前缀。默认前缀是with。

这个例子使用了下面的BeanBuilder类，它用在POJOBuilderBean上：

```java
@JsonPOJOBuilder(buildMethodName = "createBean", withPrefix = "construct")
public class BeanBuilder {
    private int idValue;
    private String nameValue;

    public BeanBuilder constructId(int id) {
        idValue = id;
        return this;
    }

    public BeanBuilder constructName(String name) {
        nameValue = name;
        return this;
    }

    public POJOBuilderBean createBean() {
        return new POJOBuilderBean(idValue, nameValue);
    }
}
```

在上面的代码中，我们将@JsonPOJOBuilder配置为使用名为createBean的构建方法和用于匹配属性的构造前缀。

@JsonPOJOBuilder对bean的应用描述和测试如下：

```java
String jsonString = "{"id":5,"name":"POJO Builder Bean"}";
POJOBuilderBean bean = mapper.readValue(jsonString, POJOBuilderBean.class);

assertEquals(5, bean.getIdentity());
assertEquals("POJO Builder Bean", bean.getBeanName());
```

结果表明，尽管属性名称不匹配，但已成功从 JSON 源重新创建新数据对象。

## 7. @JsonTypeId

@JsonTypeId注解用于指示被注解的属性在包含多态类型信息时应序列化为类型 id，而不是作为常规属性。该多态元数据在反序列化期间用于重新创建与序列化之前相同的子类型的对象，而不是声明的超类型。

有关 Jackson 处理继承的更多信息，请参阅 Jackson 的继承的第 2[部分](https://www.baeldung.com/jackson-inheritance)。

假设我们有一个 bean 类定义如下：

```java
public class TypeIdBean {
    private int id;
    @JsonTypeId
    private String name;

    // constructor, getters and setters
}
```

以下测试验证@JsonTypeId 是否按预期工作：

```java
mapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
TypeIdBean bean = new TypeIdBean(6, "Type Id Bean");
String jsonString = mapper.writeValueAsString(bean);
        
assertThat(jsonString, containsString("Type Id Bean"));
```

序列化过程的输出：

```java
[
    "Type Id Bean",
    {
        "id": 6
    }
]
```

## 8. @JsonTypeIdResolver

@JsonTypeIdResolver注解用于表示序列化和反序列化中的自定义类型标识处理程序。该处理程序负责Java类型和 JSON 文档中包含的类型 ID 之间的转换。

假设我们要在处理以下类层次结构时将类型信息嵌入到 JSON 字符串中。

AbstractBean超类：

```java
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME, 
  include = JsonTypeInfo.As.PROPERTY, 
  property = "@type"
)
@JsonTypeIdResolver(BeanIdResolver.class)
public class AbstractBean {
    private int id;

    protected AbstractBean(int id) {
        this.id = id;
    }

    // no-arg constructor, getter and setter
}
```

FirstBean子类：

```java
public class FirstBean extends AbstractBean {
    String firstName;

    public FirstBean(int id, String name) {
        super(id);
        setFirstName(name);
    }

    // no-arg constructor, getter and setter
}
```

LastBean子类：

```java
public class LastBean extends AbstractBean {
    String lastName;

    public LastBean(int id, String name) {
        super(id);
        setLastName(name);
    }

    // no-arg constructor, getter and setter
}
```

这些类的实例用于填充BeanContainer对象：

```java
public class BeanContainer {
    private List<AbstractBean> beans;

    // getter and setter
}
```

我们可以看到AbstractBean类用@JsonTypeIdResolver注解，表明它使用自定义TypeIdResolver来决定如何在序列化中包含子类型信息以及如何反过来使用该元数据。

这是处理类型信息包含的解析器类：

```java
public class BeanIdResolver extends TypeIdResolverBase {
    
    private JavaType superType;

    @Override
    public void init(JavaType baseType) {
        superType = baseType;
    }

    @Override
    public Id getMechanism() {
        return Id.NAME;
    }

    @Override
    public String idFromValue(Object obj) {
        return idFromValueAndType(obj, obj.getClass());
    }

    @Override
    public String idFromValueAndType(Object obj, Class<?> subType) {
        String typeId = null;
        switch (subType.getSimpleName()) {
        case "FirstBean":
            typeId = "bean1";
            break;
        case "LastBean":
            typeId = "bean2";
        }
        return typeId;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
        Class<?> subType = null;
        switch (id) {
        case "bean1":
            subType = FirstBean.class;
            break;
        case "bean2":
            subType = LastBean.class;
        }
        return context.constructSpecializedType(superType, subType);
    }
}
```

两个最值得注意的方法是idFromValueAndType和typeFromId，前者告诉我们在序列化 POJO 时如何包含类型信息，后者使用该元数据确定重新创建的对象的子类型。

为了确保序列化和反序列化都能正常工作，让我们编写一个测试来验证完整的进度。

首先，我们需要实例化一个 bean 容器和 bean 类，然后用 bean 实例填充该容器：

```java
FirstBean bean1 = new FirstBean(1, "Bean 1");
LastBean bean2 = new LastBean(2, "Bean 2");

List<AbstractBean> beans = new ArrayList<>();
beans.add(bean1);
beans.add(bean2);

BeanContainer serializedContainer = new BeanContainer();
serializedContainer.setBeans(beans);
```

接下来，BeanContainer对象被序列化，我们确认生成的字符串包含类型信息：

```java
String jsonString = mapper.writeValueAsString(serializedContainer);
assertThat(jsonString, containsString("bean1"));
assertThat(jsonString, containsString("bean2"));
```

序列化的输出如下所示：

```java
{
    "beans": 
    [
        {
            "@type": "bean1",
            "id": 1,
            "firstName": "Bean 1"
        },

        {
            "@type": "bean2",
            "id": 2,
            "lastName": "Bean 2"
        }
    ]
}
```

该 JSON 结构将用于重新创建与序列化之前相同子类型的对象。下面是反序列化的实现步骤：

```java
BeanContainer deserializedContainer = mapper.readValue(jsonString, BeanContainer.class);
List<AbstractBean> beanList = deserializedContainer.getBeans();
assertThat(beanList.get(0), instanceOf(FirstBean.class));
assertThat(beanList.get(1), instanceOf(LastBean.class));
```

## 9.总结

本教程详细解释了几个不太常见的 Jackson 注解。