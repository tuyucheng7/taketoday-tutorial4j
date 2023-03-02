## 1. 概述

在本教程中，我们将学习如何使用Spring Boot Starter Web Services创建**基于SOAP的Web服务**。

## 2. SOAP Web Services

简而言之，Web Service是一种机器对机器、独立于平台的服务，允许通过网络进行通信。

SOAP是一种消息传递协议。**消息(请求和响应)是基于HTTP的XML文档**。XML协定由WSDL(Web Services Description Language)定义。它提供了一组规则来定义服务的消息、绑定、操作和位置。

SOAP中使用的XML可能变得极其复杂。出于这个原因，最好将SOAP与框架一起使用，例如[JAX-WS](https://www.baeldung.com/jax-ws)或Spring，正如我们将在本教程中看到的那样。

## 3. 契约优先的开发方式

创建Web服务时有两种可能的方法：Contract-Last和[Contract-First](https://docs.spring.io/spring-ws/sites/1.5/reference/html/why-contract-first.html)。当我们使用契约最后的方法时，我们从Java代码开始，并从类中生成Web服务契约(WSDL)。**当使用契约优先时，我们从WSDL契约开始，从中生成Java类**。

Spring-WS只支持契约优先的开发风格。

## 4. 设置Spring Boot项目

我们将创建一个[Spring Boot](https://www.baeldung.com/spring-boot)项目，我们将在其中定义我们的SOAP WS服务器。

### 4.1 Maven依赖项

让我们首先将[spring-boot-starter-parent](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-parent/3.0.3)添加到我们的项目中：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.2</version>
</parent>
```

接下来，让我们添加[spring-boot-starter-web-services](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web-services/3.0.3)和[wsdl4j](https://central.sonatype.com/artifact/wsdl4j/wsdl4j/1.6.3)依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web-services</artifactId>
</dependency>
<dependency>
    <groupId>wsdl4j</groupId>
    <artifactId>wsdl4j</artifactId>
</dependency>
```

### 4.2 XSD文件

契约优先方法要求我们首先为我们的服务创建域(方法和参数)。我们将使用XML模式文件(XSD)，Spring-WS会自动将其导出为WSDL：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tns="http://www.tuyucheng.com/springsoap/gen"
           targetNamespace="http://www.tuyucheng.com/springsoap/gen" elementFormDefault="qualified">

    <xs:element name="getCountryRequest">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="name" type="xs:string"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>

    <xs:element name="getCountryResponse">
        <xs:complexType>
            <xs:sequence>
                <xs:element name="country" type="tns:country"/>
            </xs:sequence>
        </xs:complexType>
    </xs:element>

    <xs:complexType name="country">
        <xs:sequence>
            <xs:element name="name" type="xs:string"/>
            <xs:element name="population" type="xs:int"/>
            <xs:element name="capital" type="xs:string"/>
            <xs:element name="currency" type="tns:currency"/>
        </xs:sequence>
    </xs:complexType>

    <xs:simpleType name="currency">
        <xs:restriction base="xs:string">
            <xs:enumeration value="GBP"/>
            <xs:enumeration value="EUR"/>
            <xs:enumeration value="PLN"/>
        </xs:restriction>
    </xs:simpleType>
</xs:schema>
```

**在这个文件中，我们可以看到getCountryRequest Web服务请求的格式**。我们将其定义为接收一个字符串类型的参数。

接下来，我们将定义响应的格式，其中包含一个country类型的对象。

最后，我们可以看到在country对象中使用的currency对象。

### 4.3 生成域Java类

现在我们将从上一节中定义的XSD文件生成Java类。[jaxb2-maven-plugin](https://central.sonatype.com/artifact/org.codehaus.mojo/jaxb2-maven-plugin/3.1.0)将在构建期间自动执行此操作。该插件使用XJC工具作为代码生成引擎。XJC将XSD模式文件编译成完全注解的Java类。

让我们在pom.xml中添加和配置插件：

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>jaxb2-maven-plugin</artifactId>
    <version>1.6</version>
    <executions>
        <execution>
            <id>xjc</id>
            <goals>
                <goal>xjc</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <schemaDirectory>${project.basedir}/src/main/resources/</schemaDirectory>
        <outputDirectory>${project.basedir}/src/main/java</outputDirectory>
        <clearOutputDir>false</clearOutputDir>
    </configuration>
</plugin>
```

这里我们注意到两个重要的配置：

-   <schemaDirectory\>${project.basedir}/src/main/resources</schemaDirectory\>：XSD文件的位置
-   <outputDirectory\>${project.basedir}/src/main/java</outputDirectory\>：我们希望将Java代码生成到哪里

要生成Java类，我们可以使用Java安装中的XJC工具。**不过在我们的Maven项目中它甚至更简单，因为类将在通常的Maven构建期间自动生成**：

```shell
mvn compile
```

### 4.4 添加SOAP Web服务端点

SOAP Web服务端点类将处理所有传入的服务请求。它将启动处理，并发回响应。

在定义它之前，我们将创建一个CountryRepository以便为Web服务提供数据：

```java
@Component
public class CountryRepository {

    private static final Map<String, Country> countries = new HashMap<>();

    @PostConstruct
    public void initData() {
        // initialize countries map
    }

    public Country findCountry(String name) {
        return countries.get(name);
    }
}
```

接下来，我们将配置端点：

```java
@Endpoint
public class CountryEndpoint {

    private static final String NAMESPACE_URI = "http://www.tuyucheng.com/springsoap/gen";

    private CountryRepository countryRepository;

    @Autowired
    public CountryEndpoint(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCountryRequest")
    @ResponsePayload
    public GetCountryResponse getCountry(@RequestPayload GetCountryRequest request) {
        GetCountryResponse response = new GetCountryResponse();
        response.setCountry(countryRepository.findCountry(request.getName()));

        return response;
    }
}
```

以下是一些需要注意的细节：

-   @Endpoint：将类注册为Spring WS作为Web服务端点
-   @ PayloadRoot：**根据namespace和localPart属性定义处理程序方法**
-   @ResponsePayload：指示此方法返回一个值以映射到响应负载
-   @RequestPayload：表示此方法接收要从传入请求映射的参数

### 4.5 SOAP Web服务配置Bean

现在让我们创建一个类来配置Spring消息调度程序Servlet以接收请求：

```java
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
    // bean definitions
}
```

@EnableWs在此Spring Boot应用程序中启用SOAP Web Service功能。WebServiceConfig类扩展了WsConfigurerAdapter基类，该基类配置了注解驱动的Spring-WS编程模型。

让我们创建一个MessageDispatcherServlet，用于处理SOAP请求：

```java
@Bean
public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
    MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setApplicationContext(applicationContext);
    servlet.setTransformWsdlLocations(true);
    return new ServletRegistrationBean(servlet, "/ws/*");
}
```

我们将设置Servlet的注入ApplicationContext对象，以便Spring-WS可以找到其他Spring beans。

我们还将启用WSDL位置Servlet转换。这会转换WSDL中soap:address的location属性，以便它反映传入请求的URL。

最后，我们将创建一个DefaultWsdl11Definition对象。这公开了一个使用XsdSchema的标准WSDL 1.1。WSDL名称将与bean名称相同：

```java
@Bean(name = "countries")
public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
    DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
    wsdl11Definition.setPortTypeName("CountriesPort");
    wsdl11Definition.setLocationUri("/ws");
    wsdl11Definition.setTargetNamespace("http://www.tuyucheng.com/springsoap/gen");
    wsdl11Definition.setSchema(countriesSchema);
    return wsdl11Definition;
}

@Bean
public XsdSchema countriesSchema() {
    return new SimpleXsdSchema(new ClassPathResource("countries.xsd"));
}
```

## 5. 测试SOAP项目

项目配置完成后，我们就可以对其进行测试了。

### 5.1 构建并运行项目

可以创建一个WAR文件并将其部署到外部应用程序服务器。相反，我们将使用Spring Boot，这是启动和运行应用程序的一种更快、更简单的方法。

首先，我们将添加以下类以使应用程序可执行：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

请注意，我们没有使用任何XML文件(如web.xml)来创建此应用程序，都是纯Java。

现在我们准备构建和运行应用程序：

```shell
mvn spring-boot:run
```

要检查应用程序是否正常运行，我们可以通过URL打开WSDL：http://localhost:8080/ws/countries.wsdl

### 5.2 测试SOAP请求

为了测试请求，我们将创建以下文件并将其命名为request.xml：

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:gs="http://www.tuyucheng.com/springsoap/gen">
    <soapenv:Header/>
    <soapenv:Body>
        <gs:getCountryRequest>
            <gs:name>Spain</gs:name>
        </gs:getCountryRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

要将请求发送到我们的测试服务器，我们可以使用外部工具，如SoapUI或Google Chrome扩展程序Wizdler。另一种方法是在我们的shell中运行以下命令：

```shell
curl --header "content-type: text/xml" -d @request.xml http://localhost:8080/ws
```

如果没有缩进或换行，生成的响应可能不容易阅读。

要查看它的格式，我们可以将其复制粘贴到我们的IDE或其他工具中。如果我们已经安装了xmllib2，我们可以将curl命令的输出通过管道传递给xmllint：

```shell
curl [command-line-options] | xmllint --format -
```

响应应包含有关西班牙的信息：

```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns2:getCountryResponse xmlns:ns2="http://www.tuyucheng.com/springsoap/gen">
            <ns2:country>
                <ns2:name>Spain</ns2:name>
                <ns2:population>46704314</ns2:population>
                <ns2:capital>Madrid</ns2:capital>
                <ns2:currency>EUR</ns2:currency>
            </ns2:country>
        </ns2:getCountryResponse>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

## 6. 总结

在本文中，我们学习了如何使用Spring Boot创建SOAP Web服务。我们还演示了如何从XSD文件生成Java代码。最后，我们配置了处理SOAP请求所需的Spring bean。