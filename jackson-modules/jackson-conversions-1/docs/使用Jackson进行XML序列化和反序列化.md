## 1. 概述

在本教程中，我们将学习如何使用 Jackson 2.x 将Java对象序列化为 XML 数据，并将它们反序列化回 POJO。

我们将专注于不需要大量复杂性或自定义的基本操作。

## 2. XmlMapper对象

XmlMapper是 Jackson 2.x 中帮助我们序列化的主要类，因此我们需要创建它的一个实例：

```java
XmlMapper mapper = new XmlMapper();
```

这个映射器在jackson-dataformat-xml jar 中可用，所以我们必须将它作为依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.11.1</version>
</dependency>
```

请检查Maven 存储库中[最新版本的 jackson-dataformat-xml 依赖项。](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.dataformat" AND a%3A"jackson-dataformat-xml")

## 3.将Java序列化为XML

XmlMapper是ObjectMapper的子类，用于JSON序列化；但是，它向父类添加了一些 XML 特定的调整。

让我们看看如何使用它来进行实际的序列化。我们先创建一个Java类：

```java
class SimpleBean {
    private int x = 1;
    private int y = 2;
    
    //standard setters and getters
}
```

### 3.1. 序列化为 XML字符串

我们可以将Java对象序列化为 XML字符串：

```java
@Test
public void whenJavaSerializedToXmlStr_thenCorrect() throws JsonProcessingException {
    XmlMapper xmlMapper = new XmlMapper();
    String xml = xmlMapper.writeValueAsString(new SimpleBean());
    assertNotNull(xml);
}
```

结果，我们将得到：

```xml
<SimpleBean>
    <x>1</x>
    <y>2</y>
</SimpleBean>
```

### 3.2. 序列化到 XML 文件

我们还可以将Java对象序列化为 XML 文件：

```java
@Test
public void whenJavaSerializedToXmlFile_thenCorrect() throws IOException {
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.writeValue(new File("simple_bean.xml"), new SimpleBean());
    File file = new File("simple_bean.xml");
    assertNotNull(file);
}
```

在下面我们可以看到名为simple_bean.xml的结果文件的内容：

```xml
<SimpleBean>
    <x>1</x>
    <y>2</y>
</SimpleBean>
```

## 4.将XML反序列化为Java

在本节中，我们将了解如何从 XML 中获取Java对象。

### 4.1. 从 XML 字符串反序列化

与序列化一样，我们也可以将 XML String 反序列化回Java对象：

```java
@Test
public void whenJavaGotFromXmlStr_thenCorrect() throws IOException {
    XmlMapper xmlMapper = new XmlMapper();
    SimpleBean value
      = xmlMapper.readValue("<SimpleBean><x>1</x><y>2</y></SimpleBean>", SimpleBean.class);
    assertTrue(value.getX() == 1 && value.getY() == 2);
}
```

### 4.2. 从 XML 文件反序列化

同样，如果我们有一个 XML 文件，我们可以将它转换回Java对象。

```java
@Test
public void whenJavaGotFromXmlFile_thenCorrect() throws IOException {
    File file = new File("simple_bean.xml");
    XmlMapper xmlMapper = new XmlMapper();
    SimpleBean value = xmlMapper.readValue(file, SimpleBean.class);
    assertTrue(value.getX() == 1 && value.getY() == 2);
}
```

## 5. 处理大写元素

在本节中，我们将讨论如何处理以下场景：我们有要反序列化的带有大写元素的 XML，或者我们需要将Java对象序列化为带有一个或多个大写元素的 XML。

### 5.1. 从 XML字符串反序列化

假设我们有一个 XML，其中一个字段大写：

```xml
<SimpleBeanForCapitalizedFields>
    <X>1</X>
    <y>2</y>
</SimpleBeanForCapitalizedFields>
```

为了正确处理大写元素，我们需要用@JsonProperty注解来注解“x”字段：

```java
class SimpleBeanForCapitalizedFields {
    @JsonProperty("X")
    private int x = 1;
    private int y = 2;

    // standard getters, setters
}
```

我们现在可以正确地将 XML字符串反序列化回Java对象：

```java
@Test
public void whenJavaGotFromXmlStrWithCapitalElem_thenCorrect() throws IOException {
    XmlMapper xmlMapper = new XmlMapper();
    SimpleBeanForCapitalizedFields value
      = xmlMapper.readValue(
      "<SimpleBeanForCapitalizedFields><X>1</X><y>2</y></SimpleBeanForCapitalizedFields>",
      SimpleBeanForCapitalizedFields.class);
    assertTrue(value.getX() == 1 && value.getY() == 2);
}
```

### 5.2. 序列化为 XML 字符串

通过使用@JsonProperty注解必填字段，我们可以正确地将Java对象序列化为具有一个或多个大写元素的 XML字符串：

```java
@Test
public void whenJavaSerializedToXmlFileWithCapitalizedField_thenCorrect()
  throws IOException {
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.writeValue(new File("target/simple_bean_capitalized.xml"),
      new SimpleBeanForCapitalizedFields());
    File file = new File("target/simple_bean_capitalized.xml");
    assertNotNull(file);
}
```

## 6.将列表序列化为XML

XmlMapper能够将整个Javabean 序列化为文档。要将Java对象转换为 XML，我们将使用一个包含嵌套对象和数组的简单示例。

我们的目的是将一个Person对象及其组合的Address对象序列化为 XML。

我们最终的 XML 看起来像这样：

```xml
<Person>
    <firstName>Rohan</firstName>
    <lastName>Daye</lastName>
    <phoneNumbers>
        <phoneNumbers>9911034731</phoneNumbers>
        <phoneNumbers>9911033478</phoneNumbers>
    </phoneNumbers>
    <address>
        <streetName>Name1</streetName>
        <city>City1</city>
    </address>
    <address>
        <streetName>Name2</streetName>
        <city>City2</city>
    </address>
</Person>
```

请注意，我们的电话号码封装在phoneNumbers包装器中，而我们的地址则没有。

我们可以通过Person类中的@JacksonXMLElementWrapper注解来表达这种细微差别：

```java
public final class Person {
    private String firstName;
    private String lastName;
    private List<String> phoneNumbers = new ArrayList<>();
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Address> address = new ArrayList<>();

    //standard setters and getters
}
```

事实上，我们可以使用@JacksonXmlElementWrapper(localName = 'phoneNumbers') 更改包装元素名称。或者，如果我们不想包装我们的元素，我们可以使用@JacksonXmlElementWrapper(useWrapping = false)禁用映射。

然后我们将定义我们的地址类型：

```java
public class Address {
    String streetName;
    String city;
    //standard setters and getters
}
```

杰克逊为我们处理剩下的事情。和以前一样，我们可以简单地再次调用writeValue：

```java
private static final String XML = "<Person>...</Person>";

@Test
public void whenJavaSerializedToXmlFile_thenSuccess() throws IOException {
    XmlMapper xmlMapper = new XmlMapper();
    Person person = testPerson(); // test data
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    xmlMapper.writeValue(byteArrayOutputStream, person); 
    assertEquals(XML, byteArrayOutputStream.toString()); 
}
```

## 7.反序列化XML到列表

Jackson 也可以读取包含对象列表的 XML。

如果我们采用与以前相同的 XML，readValue方法就可以正常工作：

```java
@Test
public void whenJavaDeserializedFromXmlFile_thenCorrect() throws IOException {
    XmlMapper xmlMapper = new XmlMapper();
    Person value = xmlMapper.readValue(XML, Person.class);
    assertEquals("City1", value.getAddress().get(0).getCity());
    assertEquals("City2", value.getAddress().get(1).getCity());
}
```

## 八. 总结

这篇简短的文章说明了如何将简单的 POJO 序列化为 XML，以及如何从基本的 XML 数据中获取 POJO。

我们还探讨了如何序列化和反序列化包含集合的复杂 bean。