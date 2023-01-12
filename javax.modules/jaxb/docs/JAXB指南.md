## 1. 概述

这是关于 JAXB(XML 绑定的Java体系结构)的介绍性教程。

首先，我们将展示如何将Java对象转换为 XML，反之亦然。

然后我们将重点介绍如何使用 JAXB-2 Maven 插件从 XML 模式生成Java类，反之亦然。

## 2.JAXB简介

JAXB 提供了一种将Java对象编组(写入)为 XML 以及将 XML 解组(读取)为对象的快速方便的方法。它支持使用Java注解将 XML 元素和属性映射到Java字段和属性的绑定框架。

JAXB-2 Maven 插件将其大部分工作委托给 JDK 提供的两个工具[XJC](https://docs.oracle.com/javase/7/docs/technotes/tools/share/xjc.html)和[Schemagen](https://docs.oracle.com/javase/7/docs/technotes/tools/share/schemagen.html)中的任何一个。

## 3.JAXB注解

JAXB 使用Java注解来使用附加信息扩充生成的类。将此类注解添加到现有Java类可以为 JAXB 运行时做好准备。

让我们首先创建一个简单的Java对象来说明编组和解组：

```java
@XmlRootElement(name = "book")
@XmlType(propOrder = { "id", "name", "date" })
public class Book {
    private Long id;
    private String name;
    private String author;
    private Date date;

    @XmlAttribute
    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement(name = "title")
    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public void setAuthor(String author) {
        this.author = author;
    }
    
    // constructor, getters and setters
}
```

上面的类包含这些注解：

-   @XmlRootElement：根 XML 元素的名称是从类名派生的，我们也可以使用其 name 属性指定 XML 根元素的名称。
-   @XmlType：定义字段在XML文件中的写入顺序
-   @XmlElement：定义将使用的实际 XML 元素名称
-   @XmlAttribute：定义 id 字段映射为属性而不是元素
-   @XmlTransient：注解我们不想包含在 XML 中的字段

有关 JAXB 注解的更多详细信息，请查看此[链接](https://docs.oracle.com/javaee/7/api/javax/xml/bind/annotation/package-summary.html)。

## 4.编组——将Java对象转换为 XML

编组使客户端应用程序能够将 JAXB 派生的Java对象树转换为 XML 数据。默认情况下，Marshaller在生成 XML 数据时使用 UTF-8 编码。接下来，我们将从Java对象生成 XML 文件。

让我们使用JAXBContext创建一个简单的程序，它提供了一个抽象来管理实现 JAXB 绑定框架操作所必需的 XML/Java 绑定信息：

```java
public void marshal() throws JAXBException, IOException {
    Book book = new Book();
    book.setId(1L);
    book.setName("Book1");
    book.setAuthor("Author1");
    book.setDate(new Date());

    JAXBContext context = JAXBContext.newInstance(Book.class);
    Marshaller mar= context.createMarshaller();
    mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    mar.marshal(book, new File("./book.xml"));
}
```

javax.xml.bind.JAXBContext类提供了客户端到 JAXB API的入口点。默认情况下，JAXB 不格式化 XML 文档。这样可以节省空间并防止任何空格被意外解释为重要。

要让 JAXB 格式化输出，我们只需将Marshaller上的Marshaller.JAXB_FORMATTED_OUTPUT属性设置为true即可。marshal 方法使用对象和输出文件将生成的 XML 存储为参数。

当我们运行上面的代码时，我们可以检查book.xml中的结果来验证我们已经成功地将一个Java对象转换为 XML 数据：

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<book id="1">
    <title>Book1</title>
    <date>2016-11-12T11:25:12.227+07:00</date>
</book>
```

## 5. 解组——将 XML 转换为Java对象

解组使客户端应用程序能够将 XML 数据转换为 JAXB 派生的Java对象。

让我们使用 JAXB Unmarshaller将book.xml解组回Java对象：

```java
public Book unmarshall() throws JAXBException, IOException {
    JAXBContext context = JAXBContext.newInstance(Book.class);
    return (Book) context.createUnmarshaller()
      .unmarshal(new FileReader("./book.xml"));
}
```

当我们运行上面的代码时，我们可以检查控制台输出以验证我们是否已成功将 XML 数据转换为Java对象：

```xml
Book [id=1, name=Book1, author=null, date=Sat Nov 12 11:38:18 ICT 2016]
```

## 6.复杂数据类型

在处理 JAXB 中可能无法直接获得的复杂数据类型时，我们可以编写一个适配器来向 JAXB 指示如何管理特定类型。

为此，我们将使用 JAXB 的XmlAdapter来定义自定义代码，以将不可映射的类转换为 JAXB 可以处理的类。@XmlJavaTypeAdapter注解使用扩展 XmlAdapter 类的适配器来进行自定义编组。

让我们创建一个适配器来指定编组时的日期格式：

```java
public class DateAdapter extends XmlAdapter<String, Date> {

    private static final ThreadLocal<DateFormat> dateFormat 
      = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    @Override
    public Date unmarshal(String v) throws Exception {
        return dateFormat.get().parse(v);
    }

    @Override
    public String marshal(Date v) throws Exception {
        return dateFormat.get().format(v);
    }
}
```

我们使用日期格式yyyy-MM-dd HH:mm:ss在编组时 将Date转换为String ，并使用ThreadLocal使我们的DateFormat线程安全。

让我们将DateAdapter应用于我们的Book：

```java
@XmlRootElement(name = "book")
@XmlType(propOrder = { "id", "name", "date" })
public class Book {
    private Long id;
    private String name;
    private String author;
    private Date date;

    @XmlAttribute
    public void setId(Long id) {
        this.id = id;
    }

    @XmlTransient
    public void setAuthor(String author) {
        this.author = author;
    }

    @XmlElement(name = "title")
    public void setName(String name) {
        this.name = name;
    }

    @XmlJavaTypeAdapter(DateAdapter.class)
    public void setDate(Date date) {
        this.date = date;
    }
}
```

当我们运行上面的代码时，我们可以检查book.xml中的结果，以验证我们是否已使用新的日期格式yyyy-MM-dd HH:mm:ss成功地将Java对象转换为 XML ：

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<book id="1">
    <title>Book1</title>
    <date>2016-11-10 23:44:18</date>final
</book>
```

## 7. JAXB-2 Maven 插件

此插件使用JavaAPI for XML Binding (JAXB)，版本 2+，从 XML 模式(和可选的绑定文件)生成Java类，或从带注解的Java类创建 XML 模式。

请注意，有两种构建 Web 服务的基本方法，Contract Last和Contract First。有关这些方法的更多详细信息，请查看此[链接](https://docs.spring.io/spring-ws/site/reference/html/why-contract-first.html)。

### 7.1. 从 XSD 生成Java类

JAXB-2 Maven 插件使用 JDK 提供的工具 XJC，这是一种 JAXB 绑定编译器工具，可从 XSD(XML 模式定义)生成Java类。

让我们创建一个简单的user.xsd文件并使用 JAXB-2 Maven 插件从这个 XSD 模式生成Java类：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://www.w3.org/2001/XMLSchema"
    targetNamespace="/jaxb/gen"
    xmlns:userns="/jaxb/gen"
    elementFormDefault="qualified">

    <element name="userRequest" type="userns:UserRequest"></element>
    <element name="userResponse" type="userns:UserResponse"></element>

    <complexType name="UserRequest">
        <sequence>
            <element name="id" type="int" />
            <element name="name" type="string" />
        </sequence>
    </complexType>

    <complexType name="UserResponse">
        <sequence>
            <element name="id" type="int" />
            <element name="name" type="string" />
            <element name="gender" type="string" />
            <element name="created" type="dateTime" />
        </sequence>
    </complexType>
</schema>
```

让我们配置 JAXB-2 Maven 插件：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>jaxb2-maven-plugin</artifactId>
    <version>2.3</version>
    <executions>
        <execution>
            <id>xjc</id>
            <goals>
                <goal>xjc</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <xjbSources>
            <xjbSource>src/main/resources/global.xjb</xjbSource>
        </xjbSources>
        <sources>
            <source>src/main/resources/user.xsd</source>
        </sources>
        <outputDirectory>${basedir}/src/main/java</outputDirectory>
        <clearOutputDir>false</clearOutputDir>
    </configuration>
</plugin>
```

默认情况下，此插件将 XSD 文件定位在src/main/xsd中。我们可以通过相应地修改pom.xml中此插件的配置部分来配置 XSD 查找。

同样默认情况下，这些Java类是在target/generated-resources/jaxb文件夹中生成的。我们可以通过向插件配置添加outputDirectory元素来更改输出目录。我们还可以添加一个值为 false 的clearOutputDir元素，以防止该目录中的文件被删除。

此外，我们可以配置一个覆盖默认绑定规则的全局 JAXB 绑定：

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<jaxb:bindings version="2.0" xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
    xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    jaxb:extensionBindingPrefixes="xjc">

    <jaxb:globalBindings>
        <xjc:simple />
        <xjc:serializable uid="-1" />
        <jaxb:javaType name="java.util.Calendar" xmlType="xs:dateTime"
            parse="javax.xml.bind.DatatypeConverter.parseDateTime"
            print="javax.xml.bind.DatatypeConverter.printDateTime" />
    </jaxb:globalBindings>
</jaxb:bindings>
```

上面的global.xjb将dateTime类型覆盖为java.util.Calendar类型。

当我们构建项目时，它会在src/main/java文件夹中生成类文件并打包com.baeldung.jaxb.gen。

### 7.2. 从Java生成 XSD 架构

同一插件使用 JDK 提供的工具Schemagen。这是一个 JAXB 绑定编译器工具，可以从Java类生成 XSD 模式。为了使Java类符合 XSD 模式候选者的条件，必须使用@XmlType注解对该类进行注解。

我们将重用前面示例中的Java类文件来配置插件：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>jaxb2-maven-plugin</artifactId>
    <version>2.3</version>
    <executions>
        <execution>
            <id>schemagen</id>
            <goals>
                <goal>schemagen</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <sources>
            <source>src/main/java/com/baeldung/jaxb/gen</source>
        </sources>
        <outputDirectory>src/main/resources</outputDirectory>
        <clearOutputDir>false</clearOutputDir>
        <transformSchemas>
            <transformSchema>
                <uri>/jaxb/gen</uri>
                <toPrefix>user</toPrefix>
                <toFile>user-gen.xsd</toFile>
            </transformSchema>
        </transformSchemas>
    </configuration>
</plugin>
```

默认情况下，JAXB 递归扫描src/main/java下的所有文件夹以查找带 注解的 JAXB 类。我们可以通过向插件配置添加源元素来为我们的 JAXB 注解类指定不同的源文件夹。

我们还可以注册一个transformSchemas，一个负责命名 XSD 模式的后处理器。它通过将命名空间与我们的Java类的@XmlType的命名空间相匹配来工作。

当我们构建项目时，它会在src/main/resources目录中生成一个user-gen.xsd文件。

## 八. 总结

在本文中，我们介绍了 JAXB 的介绍性概念。有关更多详细信息，请查看[JAXB 主页](http://www.oracle.com/technetwork/articles/javase/index-140168.html)。