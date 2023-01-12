## 1. 简介

在本教程中，我们将了解如何[使用 JAXB](https://www.baeldung.com/jaxb)解组具有不同格式的日期对象。

首先，我们将介绍默认架构日期格式。然后，我们将探讨如何使用不同的格式。我们还将了解如何应对这些技术带来的共同挑战。

## 2. Schema 到Java绑定

首先，我们需要了解 XML Schema 和Java数据类型之间的关系。特别是，我们对 XML 模式和Java日期对象之间的映射感兴趣。

根据[Schema 到Java的映射](https://docs.oracle.com/javase/tutorial/jaxb/intro/bind.html)，我们需要考虑三种 Schema 数据类型：xsd:date、xsd:time和xsd:dateTime。如我们所见，它们都映射到javax.xml.datatype.XMLGregorianCalendar。

我们还需要了解这些 XML 模式类型的[默认格式](https://www.w3schools.com/xml/schema_dtypes_date.asp)。xsd : date和xsd:time数据类型具有“ YYYY-MM-DD”和“ hh:mm:ss” 格式。xsd:dateTime格式为“ YYYY -MM-DDThh:mm:ss”，其中“ T” 是表示时间段开始的分隔符。

## 3.使用默认模式日期格式

我们将构建一个解组日期对象的示例。让我们关注xsd:dateTime数据类型，因为它是其他类型的超集。

让我们使用一个简单的 XML 文件来描述一本书：

```xml
<book>
    <title>Book1</title>
    <published>1979-10-21T03:31:12</published>
</book>
```

我们要将文件映射到相应的JavaBook对象：

```java
@XmlRootElement(name = "book")
public class Book {

    @XmlElement(name = "title", required = true)
    private String title;

    @XmlElement(name = "published", required = true)
    private XMLGregorianCalendar published;

    @Override
    public String toString() {
        return "[title: " + title + "; published: " + published.toString() + "]";
    }

}
```

最后，我们需要创建一个客户端应用程序，将 XML 数据转换为 JAXB 派生的Java对象：

```java
public static Book unmarshalDates(InputStream inputFile) 
  throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(Book.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    return (Book) jaxbUnmarshaller.unmarshal(inputFile);
}
```

在上面的代码中，我们定义了一个JAXBContext，它是 JAXB API 的入口点。然后，我们在输入流上使用了 JAXB Unmarshaller来读取我们的对象：

如果我们运行上面的代码并打印结果，我们将得到以下Book对象：

```plaintext
[title: Book1; published: 1979-11-28T02:31:32]
```

我们应该注意到，即使xsd:dateTime的默认映射是XMLGregorianCalendar，我们也可以使用更常见的Java类型：java.util.Date和 java.util.Calendar ，根据[JAXB 用户指南](https://javaee.github.io/jaxb-v2/doc/user-guide/ch03.html#customization-of-schema-compilation-using-different-datatypes)。

## 4.使用自定义日期格式

上面的示例之所以有效，是因为我们使用的是默认架构日期格式“YYYY-MM-DDThh:mm:ss”。

但是，如果我们想使用另一种格式，如“YYYY-MM-DD hh:mm:ss”，去掉“T”分隔符怎么办？如果我们在 XML 文件中用空格字符替换定界符，默认的解组将失败。

### 4.1. 构建自定义XmlAdapter

为了使用不同的日期格式，我们需要定义一个XmlAdapter。

我们还看看如何使用自定义XmlAdapter将xsd:dateTime类型映射到java.util.Date对象：

```java
public class DateAdapter extends XmlAdapter<String, Date> {

    private static final String CUSTOM_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

    @Override
    public String marshal(Date v) {
        return new SimpleDateFormat(CUSTOM_FORMAT_STRING).format(v);
    }

    @Override
    public Date unmarshal(String v) throws ParseException {
        return new SimpleDateFormat(CUSTOM_FORMAT_STRING).parse(v);
    }

}
```

在这个适配器中，我们使用[SimpleDateFormat](https://www.baeldung.com/java-simple-date-format)来格式化我们的日期。我们需要小心，因为SimpleDateFormat不是线程[安全的](https://www.baeldung.com/java-thread-safety)。为避免多个线程遇到共享SimpleDateFormat对象的问题，我们每次需要时都会创建一个新对象。

### 4.2. XmlAdapter的内部结构

如我们所见，XmlAdapter有两个类型参数，在本例中为String和Date。第一个是 XML 内部使用的类型，称为值类型。在这种情况下，JAXB 知道如何将 XML 值转换为String。第二个称为绑定类型，与我们的Java对象中的值相关。

适配器的目标是在值类型和绑定类型之间进行转换，默认情况下 JAXB 无法做到这一点。

为了构建自定义Xml Adapter，我们必须覆盖两个方法：XmlAdapter.marshal()和XmlAdapter.unmarshal()。

在解组期间，JAXB 绑定框架首先将 XML 表示形式解组为String，然后调用DateAdapter.unmarshal()将值类型调整为Date。在编组期间，JAXB 绑定框架调用DateAdapter.marshal()以将Date适配为String，然后将其编组为 XML 表示。

### 4.3. 通过 JAXB 注解进行集成

DateAdapter的工作方式类似于 JAXB 的插件，我们将使用@XmlJavaTypeAdapter注解将其附加到我们的日期字段。@XmlJavaTypeAdapter注解指定使用XmlAdapter进行自定义解组：

```java
@XmlRootElement(name = "book")
public class BookDateAdapter {

    // same as before

    @XmlElement(name = "published", required = true)
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date published;

    // same as before

}
```

我们还使用[标准的 JAXB 注解](https://www.baeldung.com/jaxb)：@XmlRootElement和@XmlElement注解。

最后，让我们运行新代码：

```plaintext
[title: Book1; published: Wed Nov 28 02:31:32 EET 1979]
```

## 5.Java8 中的日期解组

Java 8 引入了一个新的[Date/Time API](https://www.baeldung.com/java-8-date-time-intro)。在这里，我们将重点关注LocalDateTime类，它是最常用的类之一。

### 5.1. 构建基于LocalDateTime的XmlAdapter

默认情况下，无论日期格式如何， JAXB 都无法自动将xsd:dateTime值绑定到LocalDateTime对象。为了将 XML 架构日期值与LocalDateTime对象相互转换，我们需要定义另一个类似于前一个的XmlAdapter ：

```java
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String marshal(LocalDateTime dateTime) {
        return dateTime.format(dateFormat);
    }

    @Override
    public LocalDateTime unmarshal(String dateTime) {
        return LocalDateTime.parse(dateTime, dateFormat);
    }

}
```

在这种情况下，我们使用了[DateTimeFormatter](https://www.baeldung.com/java-datetimeformatter)而不是SimpleDateFormat。前者是在Java8 中引入的，它与新的Date/Time API 兼容。

请注意，转换操作可以共享 DateTimeFormatter对象，因为DateTimeFormatter是线程安全的。

### 5.2. 集成新适配器

现在，让我们用Book 类中的新适配器替换旧适配器，并将Date替换为LocalDateTime：

```java
@XmlRootElement(name = "book")
public class BookLocalDateTimeAdapter {

    // same as before

    @XmlElement(name = "published", required = true)
    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    private LocalDateTime published;

    // same as before

}
```

如果我们运行上面的代码，我们将得到输出：

```plaintext
[title: Book1; published: 1979-11-28T02:31:32]
```

请注意，LocalDateTime.toString()在日期和时间之间添加了“T” 分隔符。

## 六. 总结

在本教程中，我们探索了使用 JAXB 解组日期。

首先，我们查看了 XML 模式到Java数据类型的映射，并创建了一个使用默认 XML 模式日期格式的示例。

接下来，我们了解了如何使用基于自定义XmlAdapter的自定义日期格式，并了解了如何处理SimpleDateFormat的线程安全性。

最后，我们利用了高级、线程安全的Java8 日期/时间 API 和具有自定义格式的未编组日期。