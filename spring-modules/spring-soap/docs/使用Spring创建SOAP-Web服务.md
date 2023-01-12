## 1. 概述

在本教程中，我们将学习如何使用Spring BootStarter Web Services创建基于SOAP 的Web 服务。

## 2. SOAP 网络服务

简而言之，Web 服务是一种机器对机器、平台独立的服务，允许通过网络进行通信。

SOAP 是一种消息传递协议。消息(请求和响应)是基于 HTTP 的 XML 文档。 XML 合同由 WSDL(Web 服务描述语言)定义。它提供了一组规则来定义服务的消息、绑定、操作和位置。

SOAP 中使用的 XML 可能变得极其复杂。出于这个原因，最好将 SOAP 与框架一起使用，例如[JAX-WS](https://www.baeldung.com/jax-ws)或 Spring，正如我们将在本教程中看到的那样。

## 3.契约优先的开发方式

创建 Web 服务时有两种可能的方法： Contract-Last 和 [Contract-First](https://docs.spring.io/spring-ws/sites/1.5/reference/html/why-contract-first.html)。当我们使用合同最后的方法时，我们从Java代码开始，并从类中生成 Web 服务合同 ( WSDL )。当使用契约优先时，我们从 WSDL 契约开始，我们从中生成Java类。

Spring-WS 只支持契约优先的开发风格。

## 4. 设置Spring Boot项目

我们将创建一个[Spring Boot](https://www.baeldung.com/spring-boot)项目，我们将在其中定义我们的 SOAP WS 服务器。

### 4.1. Maven 依赖项

让我们首先将[spring-boot-starter-parent](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-parent)添加到我们的项目中：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.2</version>
</parent>

```

接下来，让我们添加 [spring-boot-starter-web-services](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-web-services)和 [wsdl4j](https://search.maven.org/search?q=g:wsdl4j  a:wsdl4j)依赖项：

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

### 4.2. XSD 文件

契约优先方法要求我们首先为我们的服务创建域(方法和参数)。我们将使用 XML 模式文件 (XSD)，Spring-WS 会自动将其导出为 WSDL：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:tns="http://www.baeldung.com/springsoap/gen"
           targetNamespace="http://www.baeldung.com/springsoap/gen" elementFormDefault="qualified">

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

在这个文件中，我们可以看到getCountryRequest网络服务请求的格式。我们将其定义为接受一个字符串类型的参数。

接下来，我们将定义响应的格式，其中包含一个country类型的对象。

最后，我们可以看到在国家对象中使用的货币对象。

### 4.3. 生成域Java类

现在我们将从上一节中定义的 XSD 文件生成Java类。jaxb2 [-maven-plugin](https://search.maven.org/search?q=g:org.codehaus.mojo a:jaxb2-maven-plugin)将在构建期间自动执行此操作。该插件使用 XJC 工具作为代码生成引擎。XJC 将 XSD 模式文件编译成完全注解的Java类。

让我们在 pom.xml 中添加和配置插件：

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

-   <schemaDirectory>${project.basedir}/src/main/resources</schemaDirectory> – XSD 文件的位置
-   <outputDirectory>${project.basedir}/src/main/java</outputDirectory> – 我们希望生成Java代码的地方

要生成Java类，我们可以使用Java安装中的 XJC 工具。不过在我们的 Maven 项目中它甚至更简单，因为类将在通常的 Maven 构建期间自动生成：

```bash
mvn compile
```

### 4.4. 添加 SOAP Web 服务端点

SOAP Web 服务端点类将处理所有传入的服务请求。它将启动处理，并发回响应。

在定义它之前，我们将创建一个Country存储库以便为 Web 服务提供数据：

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

    private static final String NAMESPACE_URI = "http://www.baeldung.com/springsoap/gen";

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

-   @Endpoint – 将类注册为 Spring WS 作为 Web 服务端点
-   @ PayloadRoot——根据 namespace和localPart属性定义handler方法
-   @ResponsePayload – 指示此方法返回一个值以映射到响应负载
-   @RequestPayload – 表示此方法接受要从传入请求映射的参数

### 4.5. SOAP Web 服务配置 Bean

现在让我们创建一个类来配置 Spring 消息调度程序 servlet 以接收请求：

```java
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
    // bean definitions
}
```

@EnableWs在此Spring Boot应用程序中启用 SOAP Web 服务功能。WebServiceConfig类扩展了WsConfigurerAdapter基 类，它配置了注解驱动的 Spring-WS 编程模型。

让我们创建一个 MessageDispatcherServlet，用于处理 SOAP 请求：

```java
@Bean
public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
    MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setApplicationContext(applicationContext);
    servlet.setTransformWsdlLocations(true);
    return new ServletRegistrationBean(servlet, "/ws/");
}

```

我们将设置servlet的注入ApplicationContext对象， 以便 Spring-WS 可以找到其他 Spring beans。

我们还将启用 WSDL 位置 servlet 转换。这会转换 WSDL 中soap:address的位置属性，以便它反映传入请求的 URL。

最后，我们将创建一个DefaultWsdl11Definition对象。这公开了一个使用 XsdSchema 的标准 WSDL 1.1。WSDL 名称将与 bean 名称相同：

```java
@Bean(name = "countries")
public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
    DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
    wsdl11Definition.setPortTypeName("CountriesPort");
    wsdl11Definition.setLocationUri("/ws");
    wsdl11Definition.setTargetNamespace("http://www.baeldung.com/springsoap/gen");
    wsdl11Definition.setSchema(countriesSchema);
    return wsdl11Definition;
}

@Bean
public XsdSchema countriesSchema() {
    return new SimpleXsdSchema(new ClassPathResource("countries.xsd"));
}

```

## 5. 测试 SOAP 项目

项目配置完成后，我们就可以对其进行测试了。

### 5.1. 构建并运行项目

可以创建 WAR 文件并将其部署到外部应用程序服务器。相反，我们将使用 Spring Boot，这是启动和运行应用程序的一种更快、更简单的方法。

首先，我们将添加以下类以使应用程序可执行：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

```

请注意，我们没有使用任何 XML 文件(如 web.xml)来创建此应用程序。都是纯Java。

现在我们准备构建和运行应用程序：

```bash
mvn spring-boot:run
```

要检查应用程序是否正常运行，我们可以通过 URL 打开 WSDL：http://localhost:8080/ws/countries.wsdl

### 5.2. 测试 SOAP 请求

为了测试请求，我们将创建以下文件并将其命名为 request.xml：

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:gs="http://www.baeldung.com/springsoap/gen">
    <soapenv:Header/>
    <soapenv:Body>
        <gs:getCountryRequest>
            <gs:name>Spain</gs:name>
        </gs:getCountryRequest>
    </soapenv:Body>
</soapenv:Envelope>

```

要将请求发送到我们的测试服务器，我们可以使用外部工具，如 SoapUI 或 Google Chrome 扩展程序 Wizdler。另一种方法是在我们的 shell 中运行以下命令：

```bash
curl --header "content-type: text/xml" -d @request.xml http://localhost:8080/ws
```

如果没有缩进或换行，生成的响应可能不容易阅读。

要查看它的格式，我们可以将其粘贴到我们的 IDE 或其他工具中。如果我们已经安装了 xmllib2，我们可以将 curl 命令的输出通过管道传递给xmllint：

```bash
curl [command-line-options] | xmllint --format -
```

响应应包含有关西班牙的信息：

```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Header/>
<SOAP-ENV:Body>
    <ns2:getCountryResponse xmlns:ns2="http://www.baeldung.com/springsoap/gen">
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

## 六. 总结

在本文中，我们学习了如何使用Spring Boot创建 SOAP Web 服务。我们还演示了如何从 XSD 文件生成Java代码。最后，我们配置了处理 SOAP 请求所需的 Spring bean。