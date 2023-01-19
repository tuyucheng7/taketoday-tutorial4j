## **一、概述**

这是有关 XStream
的系列文章中的第三篇。如果您想了解它在[将 Java 对象转换为 XML](https://www.baeldung.com/xstream-serialize-object-to-xml)
以及[反之](https://www.baeldung.com/xstream-deserialize-xml-to-object)的基本用法，请参阅之前的文章。

除了其 XML 处理功能外，XStream 还可以将 Java 对象与 JSON 相互转换。在本教程中，我们将了解这些功能。

## **2.先决条件**

在阅读本教程之前，请阅读本[系列的第一篇文章](https://www.baeldung.com/xstream-serialize-object-to-xml)*，*其中我们解释了库的基础知识。

## **3.依赖关系**

```xml
<dependency>
    <groupId>com.thoughtworks.xstream</groupId>
    <artifactId>xstream</artifactId>
    <version>1.4.18</version>
</dependency>复制
```

## **4. JSON 驱动程序**

在前面的文章中，我们学习了如何设置 XStream 实例和选择 XML 驱动程序。同样，有两个驱动程序可用于将对象与 JSON 相互转换：**
*[JsonHierarchicalStreamDriver](https://x-stream.github.io/javadoc/com/thoughtworks/xstream/io/json/JsonHierarchicalStreamDriver.html)
*** 和[***JettisonMappedXmlDriver
***](https://x-stream.github.io/javadoc/com/thoughtworks/xstream/io/json/JettisonMappedXmlDriver.html)。

### **4.1. \*JsonHierarchicalStreamDriver\***

此驱动程序类可以将对象序列化为 JSON，但不能反序列化回对象。它不需要任何额外的依赖，而且它的驱动类是自包含的。

### **4.2. \*JettisonMappedXmlDriver\***

该驱动程序类能够将 JSON 与对象相互转换。使用这个驱动类，我们需要为*jettison添加一个额外的依赖**。***

```xml
<dependency>
    <groupId>org.codehaus.jettison</groupId>
    <artifactId>jettison</artifactId>
    <version>1.4.1</version>
</dependency>复制
```

## **5. 将对象序列化为 JSON**

让我们创建一个*客户*类：

```java
public class Customer {

    private String firstName;
    private String lastName;
    private Date dob;
    private String age;
    private List<ContactDetails> contactDetailsList;
       
    // getters and setters
}复制
```

请注意，我们已经（可能出乎意料地）将*age*创建为*String*。我们稍后会解释这个选择。

### **5.1. 使用\*JsonHierarchicalStreamDriver\***

我们将传递一个*JsonHierarchicalStreamDriver*来创建一个 XStream 实例。

```java
xstream = new XStream(new JsonHierarchicalStreamDriver());
dataJson = xstream.toXML(customer);复制
```

这会生成以下 JSON：

```javascript
{
  "com.baeldung.pojo.Customer": {
    "firstName": "John",
    "lastName": "Doe",
    "dob": "1986-02-14 16:22:18.186 UTC",
    "age": "30",
    "contactDetailsList": [
      {
        "mobile": "6673543265",
        "landline": "0124-2460311"
      },
      {
        "mobile": "4676543565",
        "landline": "0120-223312"
      }
    ]
  }
}复制
```

### **5.2. \*JettisonMappedXmlDriver\*实现**

我们将传递一个*JettisonMappedXmlDriver*类来创建一个实例。

```java
xstream = new XStream(new JettisonMappedXmlDriver());
dataJson = xstream.toXML(customer);复制
```

这会生成以下 JSON：

```javascript
{
  "com.baeldung.pojo.Customer": {
    "firstName": "John",
    "lastName": "Doe",
    "dob": "1986-02-14 16:25:50.745 UTC",
    "age": 30,
    "contactDetailsList": [
      {
        "com.baeldung.pojo.ContactDetails": [
          {
            "mobile": 6673543265,
            "landline": "0124-2460311"
          },
          {
            "mobile": 4676543565,
            "landline": "0120-223312"
          }
        ]
      }
    ]
  }
}复制
```

### **5.3. 分析**

根据两个驱动程序的输出，我们可以清楚地看到生成的 JSON 存在一些细微差别。例如，*JettisonMappedXmlDriver*省略了数值的双引号，尽管数据类型是
*java.lang.String*：*

*

```javascript
"mobile": 4676543565,
"age": 30,复制
```

另一方面，*JsonHierarchicalStreamDriver保留双引号。****
\***

## **6. 将 JSON 反序列化为对象**

让我们将以下 JSON 转换回*Customer*对象：

```javascript
{
  "customer": {
    "firstName": "John",
    "lastName": "Doe",
    "dob": "1986-02-14 16:41:01.987 UTC",
    "age": 30,
    "contactDetailsList": [
      {
        "com.baeldung.pojo.ContactDetails": [
          {
            "mobile": 6673543265,
            "landline": "0124-2460311"
          },
          {
            "mobile": 4676543565,
            "landline": "0120-223312"
          }
        ]
      }
    ]
  }
}复制
```

回想一下，只有一个驱动程序 ( *JettisonMappedXMLDriver* ) 可以反序列化 JSON。尝试为此目的使用*JsonHierarchicalStreamDriver*
将导致*UnsupportedOperationException*。

使用 Jettison 驱动程序，我们可以反序列化*Customer*对象：

```java
customer = (Customer) xstream.fromXML(dataJson);复制
```

## **七、结论**

在本文中，我们介绍了 JSON 处理功能 XStream，将对象与 JSON 相互转换。我们还研究了如何调整我们的 JSON 输出，使其更短、更简单和更具可读性。

与 XStream 的 XML 处理一样，我们可以通过其他方式进一步自定义 JSON
的序列化方式，方法是使用注释或编程配置配置实例。更多细节和示例请参考[本系列的第一篇文章](https://www.baeldung.com/xstream-serialize-object-to-xml)。