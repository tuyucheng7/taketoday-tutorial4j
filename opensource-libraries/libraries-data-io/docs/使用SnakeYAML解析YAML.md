## 1. 概述

在本教程中，我们将学习如何使用[SnakeYAML](https://bitbucket.org/asomov/snakeyaml/overview)库将Java 对象序列化为 YAML 文档，反之亦然。

## 2.项目设置

为了在我们的项目中使用 SnakeYAML，我们将添加以下 Maven 依赖项(最新版本可以在[这里](https://search.maven.org/classic/#search|gav|1|g%3A"org.yaml" AND a%3A"snakeyaml")找到)：

```xml
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>1.21</version>            
</dependency>
```

## 3.切入点

Yaml类是 API 的入口点：

```java
Yaml yaml = new Yaml();
```

由于实现不是线程安全的，不同的线程必须有自己的Yaml实例。

## 4.加载YAML文档

该库支持从String 或InputStream加载文档。这里的大部分代码示例将基于解析InputStream。

让我们从定义一个简单的 YAML 文档开始，并将文件命名为customer.yaml：

```plaintext
firstName: "John"
lastName: "Doe"
age: 20
```

### 4.1. 基本用法

现在我们将使用Yaml类解析上述 YAML 文档：

```java
Yaml yaml = new Yaml();
InputStream inputStream = this.getClass()
  .getClassLoader()
  .getResourceAsStream("customer.yaml");
Map<String, Object> obj = yaml.load(inputStream);
System.out.println(obj);
```

上面的代码生成以下输出：

```plaintext
{firstName=John, lastName=Doe, age=20}
```

默认情况下，load()方法返回一个Map实例。每次查询Map对象都需要我们提前知道属性键名，而且遍历嵌套属性也不是一件容易的事。

### 4.2. 自定义类型

该库还提供了一种将文档作为自定义类加载的方法。此选项将允许轻松遍历内存中的数据。

让我们定义一个Customer类并尝试再次加载文档：

```java
public class Customer {

    private String firstName;
    private String lastName;
    private int age;

    // getters and setters
}
```

假设 YAML 文档被反序列化为已知类型，我们可以在文档中指定一个显式的全局标记。

让我们更新文档并将其存储在新文件customer_with_type.yaml 中：

```plaintext
!!com.baeldung.snakeyaml.Customer
firstName: "John"
lastName: "Doe"
age: 20
```

请注意文档中的第一行，其中包含有关加载类时要使用的类的信息。

现在我们将更新上面使用的代码，并将新文件名作为输入传递：

```java
Yaml yaml = new Yaml();
InputStream inputStream = this.getClass()
 .getClassLoader()
 .getResourceAsStream("yaml/customer_with_type.yaml");
Customer customer = yaml.load(inputStream);

```

load()方法现在返回Customer类型的一个实例。 这种方法的缺点是必须将类型导出为库才能在需要的地方使用。

虽然，我们可以使用我们不需要导出库的显式本地标签。

加载自定义类型的另一种方法是使用 Constructor类。这样我们就可以指定要解析的 YAML 文档的根类型。让我们创建一个以Customer类型作为根类型的Constructor实例，并将其传递给Yaml实例。

现在加载customer.yaml， 我们将获得Customer对象：

```java
Yaml yaml = new Yaml(new Constructor(Customer.class));
```

### 4.3. 隐式类型

如果没有为给定属性定义类型，库会自动将该值转换为隐式类型。

例如：

```plaintext
1.0 -> Float
42 -> Integer
2009-03-30 -> Date
```

让我们使用测试用例来测试这个隐式类型转换：

```java
@Test
public void whenLoadYAML_thenLoadCorrectImplicitTypes() {
   Yaml yaml = new Yaml();
   Map<Object, Object> document = yaml.load("3.0: 2018-07-22");
 
   assertNotNull(document);
   assertEquals(1, document.size());
   assertTrue(document.containsKey(3.0d));   
}
```

### 4.4. 嵌套对象和集合

给定顶级类型，库会自动检测嵌套对象的类型，除非它们是接口或抽象类，并将文档反序列化为相关的嵌套类型。

让我们将联系人 和地址 详细信息添加到customer.yaml，并将新文件另存为 customer_with_contact_details_and_address.yaml。 

现在我们将解析新的 YAML 文档：

```plaintext
firstName: "John"
lastName: "Doe"
age: 31
contactDetails:
   - type: "mobile"
     number: 123456789
   - type: "landline"
     number: 456786868
homeAddress:
   line: "Xyz, DEF Street"
   city: "City Y"
   state: "State Y"
   zip: 345657

```

客户类也应该反映这些变化。这是更新后的课程：

```java
public class Customer {
    private String firstName;
    private String lastName;
    private int age;
    private List<Contact> contactDetails;
    private Address homeAddress;    
    // getters and setters
}

```

让我们看看 Contact和Address类的样子：

```java
public class Contact {
    private String type;
    private int number;
    // getters and setters
}
public class Address {
    private String line;
    private String city;
    private String state;
    private Integer zip;
    // getters and setters
}
```

现在我们将使用给定的测试用例测试Yaml #load ( ) ：

```java
@Test
public void 
  whenLoadYAMLDocumentWithTopLevelClass_thenLoadCorrectJavaObjectWithNestedObjects() {
 
    Yaml yaml = new Yaml(new Constructor(Customer.class));
    InputStream inputStream = this.getClass()
      .getClassLoader()
      .getResourceAsStream("yaml/customer_with_contact_details_and_address.yaml");
    Customer customer = yaml.load(inputStream);
 
    assertNotNull(customer);
    assertEquals("John", customer.getFirstName());
    assertEquals("Doe", customer.getLastName());
    assertEquals(31, customer.getAge());
    assertNotNull(customer.getContactDetails());
    assertEquals(2, customer.getContactDetails().size());
    
    assertEquals("mobile", customer.getContactDetails()
      .get(0)
      .getType());
    assertEquals(123456789, customer.getContactDetails()
      .get(0)
      .getNumber());
    assertEquals("landline", customer.getContactDetails()
      .get(1)
      .getType());
    assertEquals(456786868, customer.getContactDetails()
      .get(1)
      .getNumber());
    assertNotNull(customer.getHomeAddress());
    assertEquals("Xyz, DEF Street", customer.getHomeAddress()
      .getLine());
}
```

### 4.5. 类型安全的集合

当给定Java类的一个或多个属性是类型安全(通用)集合时，指定TypeDescription以便识别正确的参数化类型很重要。

让我们假设一个 Customer拥有多个Contact，并尝试加载它：

```plaintext
firstName: "John"
lastName: "Doe"
age: 31
contactDetails:
   - { type: "mobile", number: 123456789}
   - { type: "landline", number: 123456789}
```

为了加载此文档，我们可以为顶级类的给定属性指定TypeDescription ：

```java
Constructor constructor = new Constructor(Customer.class);
TypeDescription customTypeDescription = new TypeDescription(Customer.class);
customTypeDescription.addPropertyParameters("contactDetails", Contact.class);
constructor.addTypeDescription(customTypeDescription);
Yaml yaml = new Yaml(constructor);
```

### 4.6. 加载多个文档

可能存在这样的情况，在一个文件中有多个 YAML 文档，我们想要解析所有这些文档。Yaml类提供了一个loadAll ()方法来执行此类解析。

默认情况下，该方法返回Iterable<Object>的实例，其中每个对象的类型都是Map<String, Object>。 如果需要自定义类型，那么我们可以使用上面讨论的Constructor 实例。 

考虑单个文件中的以下文档：

```plaintext
---
firstName: "John"
lastName: "Doe"
age: 20
---
firstName: "Jack"
lastName: "Jones"
age: 25
```

我们可以使用loadAll()方法解析以上内容，如下面的代码示例所示：

```java
@Test
public void whenLoadMultipleYAMLDocuments_thenLoadCorrectJavaObjects() {
    Yaml yaml = new Yaml(new Constructor(Customer.class));
    InputStream inputStream = this.getClass()
      .getClassLoader()
      .getResourceAsStream("yaml/customers.yaml");

    int count = 0;
    for (Object object : yaml.loadAll(inputStream)) {
        count++;
        assertTrue(object instanceof Customer);
    }
    assertEquals(2,count);
}
```

## 5. 转储 YAML 文件

该库还提供了一种将给定Java对象转储到 YAML 文档中的方法。输出可以是字符串或指定的文件/流。

### 5.1. 基本用法

我们将从一个将Map<String, Object>的实例转储到 YAML 文档 ( String ) 的简单示例开始：

```java
@Test
public void whenDumpMap_thenGenerateCorrectYAML() {
    Map<String, Object> data = new LinkedHashMap<String, Object>();
    data.put("name", "Silenthand Olleander");
    data.put("race", "Human");
    data.put("traits", new String[] { "ONE_HAND", "ONE_EYE" });
    Yaml yaml = new Yaml();
    StringWriter writer = new StringWriter();
    yaml.dump(data, writer);
    String expectedYaml = "name: Silenthand Olleandernrace: Humanntraits: [ONE_HAND, ONE_EYE]n";

    assertEquals(expectedYaml, writer.toString());
}
```

上面的代码产生以下输出(请注意，使用LinkedHashMap的实例会保留输出数据的顺序)：

```java
name: Silenthand Olleander
race: Human
traits: [ONE_HAND, ONE_EYE]
```

### 5.2. 自定义Java对象

我们还可以选择将自定义Java类型转储到输出流中。但是，这会将全局显式标记添加到输出文档：

```java
@Test
public void whenDumpACustomType_thenGenerateCorrectYAML() {
    Customer customer = new Customer();
    customer.setAge(45);
    customer.setFirstName("Greg");
    customer.setLastName("McDowell");
    Yaml yaml = new Yaml();
    StringWriter writer = new StringWriter();
    yaml.dump(customer, writer);        
    String expectedYaml = "!!com.baeldung.snakeyaml.Customer {age: 45, contactDetails: null, firstName: Greg,n  homeAddress: null, lastName: McDowell}n";

    assertEquals(expectedYaml, writer.toString());
}
```

通过上述方法，我们仍然将标签信息转储到 YAML 文档中。

这意味着我们必须将我们的类导出为一个库，供任何反序列化它的消费者使用。为了避免输出文件中出现标签名，我们可以使用库提供的 dumpAs()方法。

所以在上面的代码中，我们可以调整以下内容来删除标签：

```java
yaml.dumpAs(customer, Tag.MAP, null);
```

## 六. 总结

本文说明了如何使用 SnakeYAML 库将Java对象序列化为 YAML，反之亦然。