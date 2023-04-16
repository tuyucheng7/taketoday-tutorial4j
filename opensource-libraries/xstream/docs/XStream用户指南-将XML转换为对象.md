## **一、概述**

在[上一篇文章中](https://www.baeldung.com/xstream-serialize-object-to-xml)，我们学习了如何使用 XStream 将 Java 对象序列化为
XML。在本教程中，我们将学习如何执行相反的操作：将 XML 反序列化为 Java 对象。这些任务可以使用注释或以编程方式完成。

要了解设置 XStream 及其依赖项的基本要求，请参考上一篇文章。

## **2. 从 XML 中反序列化一个对象**

首先，假设我们有以下 XML：

```xml
<com.baeldung.pojo.Customer>
    <firstName>John</firstName>
    <lastName>Doe</lastName>
    <dob>1986-02-14 03:46:16.381 UTC</dob>
</com.baeldung.pojo.Customer>复制
```

我们需要将其转换为 Java *Customer*对象：

```java
public class Customer {
 
    private String firstName;
    private String lastName;
    private Date dob;
 
    // standard setters and getters
}
复制
```

可以通过多种方式输入 XML，包括*File*、*InputStream*、*Reader*或*String*。为简单起见，我们假设我们在*String*对象中有上面的
XML。

```java
Customer convertedCustomer = (Customer) xstream.fromXML(customerXmlString);
Assert.assertTrue(convertedCustomer.getFirstName().equals("John"));复制
```

## 3.安全方面

由于 XStream 使用未记录的 Java 功能以及 Java 反射，它可能容易受到任意代码执行或远程命令执行攻击。

深入的安全注意事项超出了本教程的范围，但我们确实有一篇[专门的文章](https://www.baeldung.com/xstream-deserialize-xml-to-object)
来解释这种威胁。此外，值得查看[XStream 的官方页面](https://x-stream.github.io/security.html)。

出于我们教程的目的，让我们假设我们所有的类都是“安全的”。因此，我们需要配置 XStream：

```java
XStream xstream = new XStream();
xstream.allowTypesByWildcard(new String[]{"com.baeldung.**"});
复制
```

## **4.别名**

在第一个示例中，XML 在最外层的 XML 标记中具有类的完全限定名称，与我们的*Customer*类的位置相匹配。使用此设置，XStream
无需任何额外配置即可轻松将 XML 转换为我们的对象。但我们可能并不总是具备这些条件。我们可能无法控制 XML
标记命名，或者我们可能决定为字段添加别名。

例如，假设我们将 XML 修改为不对外部标记使用完全限定的类名：

```xml
<customer>
    <firstName>John</firstName>
    <lastName>Doe</lastName>
    <dob>1986-02-14 03:46:16.381 UTC</dob>
</customer>复制
```

我们可以通过创建别名来隐藏这个 XML。

### **4.1. 类别名**

我们以编程方式或使用注释向 XStream 实例注册别名。我们可以用*@XStreamAlias注释我们的**Customer*类：

```java
@XStreamAlias("customer")
public class Customer {
    //...
}复制
```

现在我们需要配置我们的 XStream 实例来使用这个注解：

```java
xstream.processAnnotations(Customer.class);复制
```

或者，如果我们希望以编程方式配置别名，我们可以使用以下代码：
\~~~~

```java
xstream.alias("customer", Customer.class);复制
```

### **4.2. 字段别名**

假设我们有以下 XML：

```xml
<customer>
    <fn>John</fn>
    <lastName>Doe</lastName>
    <dob>1986-02-14 03:46:16.381 UTC</dob>
</customer>复制
```

*fn*标签不匹配我们的*Customer*对象中的任何字段，因此如果我们希望反序列化它，我们需要为该字段定义一个别名。我们可以使用以下注释来实现这一点：

```java
@XStreamAlias("fn")
private String firstName;复制
```

或者，我们可以通过编程方式实现相同的目标：

```java
xstream.aliasField("fn", Customer.class, "firstName");复制
```

## **5.****隐式集合**

假设我们有以下 XML，其中包含一个简单的*ContactDetails*列表：

```xml
<customer>
    <firstName>John</firstName>
    <lastName>Doe</lastName>
    <dob>1986-02-14 04:14:20.541 UTC</dob>
    <ContactDetails>
        <mobile>6673543265</mobile>
        <landline>0124-2460311</landline>
    </ContactDetails>
    <ContactDetails>...</ContactDetails>
</customer>复制
```

我们希望将*ContactDetails*列表加载到Java 对象中的*List<ContactDetails>字段中。*我们可以通过使用以下注释来实现这一点：

```java
@XStreamImplicit
private List<ContactDetails> contactDetailsList;复制
```

或者，我们可以通过编程方式实现相同的目标：

```java
xstream.addImplicitCollection(Customer.class, "contactDetailsList");复制
```

## **6.** **忽略字段**

假设我们有以下 XML：

```xml
<customer>
    <firstName>John</firstName>
    <lastName>Doe</lastName>
    <dob>1986-02-14 04:14:20.541 UTC</dob>
    <fullName>John Doe</fullName>
</customer>复制
```

在上面的 XML 中，我们的 Java *Customer*对象中缺少额外的元素*<fullName> 。*

如果我们尝试反序列化上述 xml 而不考虑额外的元素，程序将抛出*UnknownFieldException*。

```java
No such field com.baeldung.pojo.Customer.fullName复制
```

正如异常明确指出的那样，XStream 无法识别字段*fullName*。

为了克服这个问题，我们需要将其配置为忽略未知元素：

```java
xstream.ignoreUnknownElements();复制
```

## **7.属性字段**

假设我们有 XML，其属性作为元素的一部分，我们希望将其反序列化为对象中的一个字段。我们将向*ContactDetails对象添加一个*
*contactType*属性：

```xml
<ContactDetails contactType="Office">
    <mobile>6673543265</mobile>
    <landline>0124-2460311</landline>
</ContactDetails>复制
```

如果我们想要反序列化*contactType* XML 属性，我们可以在我们希望它出现的字段上使用*@XStreamAsAttribute注释：*

```java
@XStreamAsAttribute
private String contactType;复制
```

或者，我们可以通过编程方式实现相同的目标：

```java
xstream.useAttributeFor(ContactDetails.class, "contactType");复制
```

## **八、结论**

在本文中，我们探讨了使用 XStream 将 XML 反序列化为 Java 对象时可用的选项。