## **一、概述**

在本教程中，我们将学习如何使用[XStream](https://x-stream.github.io/)库将 Java 对象序列化为 XML。

## **2.特点**

使用 XStream 序列化和反序列化 XML 有很多有趣的好处：

- 配置正确，它会生成非常**干净的 XML**
- **为自定义**XML 输出提供重要机会
- 支持**对象图**，包括循环引用
- 对于大多数用例，XStream 实例是**线程安全的，一旦配置**（使用注释时有警告）
- **在异常处理**过程中提供清晰的消息以帮助诊断问题
- 从 1.4.7 版开始，我们提供了**安全功能**来禁止某些类型的序列化

## **3.项目设置**

为了在我们的项目中使用 XStream，我们将添加以下 Maven 依赖项：

```xml
<dependency>
    <groupId>com.thoughtworks.xstream</groupId>
    <artifactId>xstream</artifactId>
    <version>1.4.18</version>
</dependency>复制
```

## **4. 基本用法**

*XStream*类是 API 的外观。创建*XStream*实例时，我们还需要注意线程安全问题：

```java
XStream xstream = new XStream();复制
```

创建和配置实例后，除非启用注释处理，否则它可能会在多个线程之间共享以进行编组/解组。

### **4.1. 司机**

支持多个驱动程序，例如*DomDriver*、*StaxDriver*、*XppDriver*等。这些驱动程序具有不同的性能和资源使用特征。

默认情况下使用 XPP3 驱动程序，但当然我们可以轻松更改驱动程序：

```java
XStream xstream = new XStream(new StaxDriver());
复制
```

### **4.2. 生成 XML**

让我们从为*Customer*定义一个简单的 POJO 开始：

```java
public class Customer {

    private String firstName;
    private String lastName;
    private Date dob;

    // standard constructor, setters, and getters
}复制
```

现在让我们生成对象的 XML 表示：

```java
Customer customer = new Customer("John", "Doe", new Date());
String dataXml = xstream.toXML(customer);复制
```

使用默认设置，会产生以下输出：

```xml
<com.baeldung.pojo.Customer>
    <firstName>John</firstName>
    <lastName>Doe</lastName>
    <dob>1986-02-14 03:46:16.381 UTC</dob>
</com.baeldung.pojo.Customer>
复制
```

从这个输出中，我们可以清楚地看到包含标签默认使用*Customer的完全限定类名**。*

我们可能会认为默认行为不符合我们的需要的原因有很多。例如，我们可能不愿意公开应用程序的包结构。此外，生成的 XML 明显更长。

## **5.别名**

**别名**是我们希望为元素使用的名称，而不是使用默认名称。

例如，我们可以通过为*Customer*类注册别名来将*com.baeldung.pojo.Customer*替换为*customer 。*我们还可以为类的属性添加别名。通过使用别名，我们可以使我们的
XML 输出更具可读性并且更少特定于 Java。

### **5.1. 类别名**

可以通过编程方式或使用注释来注册别名。

*现在让我们用@XStreamAlias*注释我们的*Customer*类：

```java
@XStreamAlias("customer")复制
```

现在我们需要配置我们的实例来使用这个注解：

```java
xstream.processAnnotations(Customer.class);复制
```

或者，如果我们希望以编程方式配置别名，我们可以使用以下代码：

```java
xstream.alias("customer", Customer.class);复制
```

无论是使用别名还是编程配置，*Customer*对象的输出都会更清晰：

```xml
<customer>
    <firstName>John</firstName>
    <lastName>Doe</lastName>
    <dob>1986-02-14 03:46:16.381 UTC</dob>
</customer>
复制
```

### **5.2. 字段别名**

我们还可以使用用于别名类的相同注释为字段添加别名。例如，如果我们希望在 XML 表示中将字段*firstName*替换为*fn*，我们可以使用以下注释：

```java
@XStreamAlias("fn")
private String firstName;复制
```

或者，我们可以通过编程方式实现相同的目标：

```java
xstream.aliasField("fn", Customer.class, "firstName");复制
```

aliasField方法接受三个参数：我们希望使用的别名、定义属性的类以及我们希望作为别名的属性名称*。*

无论使用哪种方法，输出都是相同的：

```xml
<customer>
    <fn>John</fn>
    <lastName>Doe</lastName>
    <dob>1986-02-14 03:46:16.381 UTC</dob>
</customer>复制
```

### **5.3. 默认别名**

有几个为课程预先注册的别名——以下是其中的几个：

```java
alias("float", Float.class);
alias("date", Date.class);
alias("gregorian-calendar", Calendar.class);
alias("url", URL.class);
alias("list", List.class);
alias("locale", Locale.class);
alias("currency", Currency.class);复制
```

## **6.收藏品**

现在我们将在*Customer*类中添加*ContactDetails列表。*

```java
private List<ContactDetails> contactDetailsList;复制
```

使用集合处理的默认设置，这是输出：

```xml
<customer>
    <firstName>John</firstName>
    <lastName>Doe</lastName>
    <dob>1986-02-14 04:14:05.874 UTC</dob>
    <contactDetailsList>
        <ContactDetails>
            <mobile>6673543265</mobile>
            <landline>0124-2460311</landline>
        </ContactDetails>
        <ContactDetails>
            <mobile>4676543565</mobile>
            <landline>0120-223312</landline>
        </ContactDetails>
    </contactDetailsList>
</customer>复制
```

假设我们需要省略*contactDetailsList*父标签*，*我们只希望每个*ContactDetails*元素都是*customer*元素的子元素。让我们再次修改示例：

```java
xstream.addImplicitCollection(Customer.class, "contactDetailsList");复制
```

Now, when the XML is generated, the root tags are omitted, resulting in the XML below:

```xml
<customer>
    <firstName>John</firstName>
    <lastName>Doe</lastName>
    <dob>1986-02-14 04:14:20.541 UTC</dob>
    <ContactDetails>
        <mobile>6673543265</mobile>
        <landline>0124-2460311</landline>
    </ContactDetails>
    <ContactDetails>
        <mobile>4676543565</mobile>
        <landline>0120-223312</landline>
    </ContactDetails>
</customer>复制
```

使用注释也可以实现相同的目的：

```java
@XStreamImplicit
private List<ContactDetails> contactDetailsList;复制
```

## **7.转换器**

XStream 使用*Converter*实例映射，每个实例都有自己的转换策略。这些将提供的数据转换为 XML 中的特定格式，然后再转换回来。

除了使用默认转换器之外，我们还可以修改默认值或注册自定义转换器。

### **7.1. 修改现有转换器**

假设我们对使用默认设置生成*dob*标签的方式不满意。 我们可以修改XStream 提供的*Date的自定义转换器（* *DateConverter*）：

```java
xstream.registerConverter(new DateConverter("dd-MM-yyyy", null));复制
```

以上将产生“ *dd-MM-yyyy* ”格式的输出：

```xml
<customer>
    <firstName>John</firstName>
    <lastName>Doe</lastName>
    <dob>14-02-1986</dob>
</customer>复制
```

### **7.2. 自定义转换器**

我们还可以创建一个自定义转换器来完成与上一节相同的输出：

```java
public class MyDateConverter implements Converter {

    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public boolean canConvert(Class clazz) {
        return Date.class.isAssignableFrom(clazz);
    }

    @Override
    public void marshal(
      Object value, HierarchicalStreamWriter writer, MarshallingContext arg2) {
        Date date = (Date)value;
        writer.setValue(formatter.format(date));
    }

    // other methods
}复制
```

最后，我们注册我们的*MyDateConverter*类如下：

```java
xstream.registerConverter(new MyDateConverter());复制
```

我们还可以创建实现*SingleValueConverter*接口的转换器，该接口旨在将对象转换为字符串。

```java
public class MySingleValueConverter implements SingleValueConverter {

    @Override
    public boolean canConvert(Class clazz) {
        return Customer.class.isAssignableFrom(clazz);
    }

    @Override
    public String toString(Object obj) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = ((Customer) obj).getDob();
        return ((Customer) obj).getFirstName() + "," 
          + ((Customer) obj).getLastName() + ","
          + formatter.format(date);
    }

    // other methods
}复制
```

最后，我们注册*MySingleValueConverter*：

```java
xstream.registerConverter(new MySingleValueConverter());
复制
```

使用*MySingleValueConverter*，*客户*的 XML 输出如下：

```xml
<customer>John,Doe,14-02-1986</customer>复制
```

### **7.3. 转换器优先级**

注册*Converter*对象时，也可以设置它们的优先级。

来自[XStream javadocs](https://x-stream.github.io/javadoc/com/thoughtworks/xstream/XStream.html)：

> 可以使用明确的优先级注册转换器。默认情况下，它们注册到
> XStream.PRIORITY_NORMAL。相同优先级的转换器将按照它们注册的相反顺序使用。默认转换器，即如果没有其他注册的转换器合适，将使用的转换器，可以使用优先级
> XStream.PRIORITY_VERY_LOW 进行注册。XStream 默认使用 ReflectionConverter 作为回退转换器。

API 提供了几个命名的优先级值：**
**

```java
private static final int PRIORITY_NORMAL = 0;
private static final int PRIORITY_LOW = -10;
private static final int PRIORITY_VERY_LOW = -20;
复制
```

## **8.** **省略字段**

我们可以使用注释或编程配置从生成的 XML 中省略字段。为了使用注释省略字段，我们只需将*@XStreamOmitField*注释应用于相关字段：

```java
@XStreamOmitField 
private String firstName;复制
```

为了以编程方式省略该字段，我们使用以下方法：

```java
xstream.omitField(Customer.class, "firstName");复制
```

无论我们选择哪种方法，输出都是相同的：

```xml
<customer> 
    <lastName>Doe</lastName> 
    <dob>14-02-1986</dob> 
</customer>复制
```

## **9. 属性字段**

有时我们可能希望将字段序列化为元素的属性而不是元素本身。假设我们添加一个*contactType*字段：

```java
private String contactType;复制
```

如果我们想将*contactType*设置为 XML 属性，我们可以使用*@XStreamAsAttribute*注解：

```java
@XStreamAsAttribute
private String contactType;
复制
```

或者，我们可以通过编程方式实现相同的目标：

```java
xstream.useAttributeFor(ContactDetails.class, "contactType");复制
```

上述任一方法的输出是相同的：

```xml
<ContactDetails contactType="Office">
    <mobile>6673543265</mobile>
    <landline>0124-2460311</landline>
</ContactDetails>复制
```

## **10. 并发**

XStream 的处理模型提出了一些挑战。一旦配置了实例，它就是线程安全的。

重要的是要注意注释的处理会在编组/解组之前修改配置。因此，如果我们需要使用注释即时配置实例，通常最好为每个线程使用一个单独的
*XStream实例。*

## **11.结论**

在本文中，我们介绍了使用 XStream 将对象转换为 XML 的基础知识。我们还了解了可用于确保 XML 输出满足我们需求的定制。最后，我们研究了注释的线程安全问题。

在本系列的下一篇文章中，我们将学习如何将 XML 转换回 Java 对象。