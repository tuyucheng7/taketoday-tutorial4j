## 1. 概述

某些项目可能需要将 JSON 对象保存在关系数据库中。

在本教程中，我们将了解如何获取 JSON 对象并将其保存在关系数据库中。

有几个可用的框架可以提供此功能，但我们将查看一些仅使用[Hibernate](https://www.baeldung.com/hibernate-5-spring)和[Jackson](https://www.baeldung.com/jackson)的简单、通用的选项。

## 2.依赖关系

我们将在本教程中使用[基本的 Hibernate Core 依赖项](https://search.maven.org/search?q=g:org.hibernate AND a:hibernate-core) ：

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.4.12.Final</version>
</dependency>
```

我们还将使用[Jackson](https://search.maven.org/search?q=g:com.fasterxml.jackson.core AND a:jackson-databind)作为我们的 JSON 库：

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.3</version>
</dependency>
```

请注意，这些技术不限于这两个库。我们可以替换我们最喜欢的 JPA 提供程序和 JSON 库。

## 3.序列化和反序列化方法

在关系数据库中持久化 JSON 对象的最基本方法是在持久化之前将对象转换为String 。然后，当我们从数据库中检索它时，我们将它转换回一个对象。

我们可以通过几种不同的方式来做到这一点。

我们要看的第一个是使用自定义序列化和反序列化方法。

我们将从一个简单的Customer实体开始，该实体存储客户的名字和姓氏，以及有关该客户的一些属性。

标准的 JSON 对象会将这些属性表示为 HashMap ，因此我们将在此处使用：

```java
@Entity
@Table(name = "Customers")
public class Customer {

    @Id
    private int id;

    private String firstName;

    private String lastName;

    private String customerAttributeJSON;

    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> customerAttributes;
}
```

我们不会将属性保存在单独的表中，而是将它们作为 JSON 存储在Customers表的列中。这有助于降低模式复杂性并提高查询性能。

首先，我们将创建一个序列化方法，它将获取我们的customerAttributes并将其转换为 JSON 字符串：

```java
public void serializeCustomerAttributes() throws JsonProcessingException {
    this.customerAttributeJSON = objectMapper.writeValueAsString(customerAttributes);
}
```

我们可以在持久化之前手动调用此方法，也可以从setCustomerAttributes方法中调用它，以便每次更新属性时，JSON 字符串也会更新。

接下来，我们将创建一个方法，当我们从数据库中检索Customer时，将 JSON 字符串反序列化回HashMap对象。我们可以通过将TypeReference参数传递给readValue()来做到这一点：

```java
public void deserializeCustomerAttributes() throws IOException {
    this.customerAttributes = objectMapper.readValue(customerAttributeJSON, 
    	new TypeReference<Map<String, Object>>() {});
}
```

再一次，我们可以从几个不同的地方调用这个方法，但是，在这个例子中，我们将手动调用它。

因此，持久化和检索我们的Customer对象看起来像这样：

```java
@Test
public void whenStoringAJsonColumn_thenDeserializedVersionMatches() {
    Customer customer = new Customer();
    customer.setFirstName("first name");
    customer.setLastName("last name");

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("address", "123 Main Street");
    attributes.put("zipcode", 12345);

    customer.setCustomerAttributes(attributes);
    customer.serializeCustomerAttributes();

    String serialized = customer.getCustomerAttributeJSON();

    customer.setCustomerAttributeJSON(serialized);
    customer.deserializeCustomerAttributes();

    assertEquals(attributes, customer.getCustomerAttributes());
}
```

## 4.属性转换器

如果我们使用 JPA 2.1 或更高版本，我们可以使用AttributeConverters来简化这个过程。

首先，我们将创建AttributeConverter的实现。我们将重用之前的代码：

```java
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    @Override
    public String convertToDatabaseColumn(Map<String, Object> customerInfo) {

        String customerInfoJson = null;
        try {
            customerInfoJson = objectMapper.writeValueAsString(customerInfo);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
        }

        return customerInfoJson;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String customerInfoJSON) {

        Map<String, Object> customerInfo = null;
        try {
            customerInfo = objectMapper.readValue(customerInfoJSON, 
            	new TypeReference<HashMap<String, Object>>() {});
        } catch (final IOException e) {
            logger.error("JSON reading error", e);
        }

        return customerInfo;
    }
}
```

接下来，我们告诉 Hibernate 将我们新的AttributeConverter用于customerAttributes字段，我们就完成了：

```java
@Convert(converter = HashMapConverter.class)
private Map<String, Object> customerAttributes;
```

使用这种方法，我们不再需要手动调用序列化和反序列化方法， 因为 Hibernate 会为我们处理这些事情。我们可以简单地正常保存和检索Customer对象。

## 5.总结

在本文中，我们看到了几个如何使用 Hibernate 和 Jackson 来持久化 JSON 对象的示例。

我们的第一个示例着眼于使用自定义序列化和反序列化方法的简单、兼容的方法。其次，我们引入了AttributeConverters作为简化代码的强大方法。