## 1. 概述

在本教程中，我们将学习如何使用Java 8 和 11 中的[JAX-WS RI在Java中构建 SOAP 客户端。](https://javaee.github.io/metro-jax-ws/)

首先，我们将使用wsimport实用程序生成客户端代码，然后使用 JUnit 对其进行测试。

对于刚起步的人，我们[对 JAX-WS 的介绍](https://www.baeldung.com/jax-ws)为该主题提供了很好的背景知识。

## 2. 网络服务

在我们开始构建客户端之前，我们需要一个服务器。在这种情况下，我们需要一个公开 JAX-WS Web 服务的服务器。

出于本教程的目的，我们将使用一个 Web 服务来获取一个国家的数据，并给出其名称。

### 2.1。实施总结

由于我们专注于构建客户端，因此我们不会深入了解服务的实现细节。

假设一个接口CountryService用于将 Web 服务公开给外部世界。为简单起见，我们将使用CountryServicePublisher类中的[javax.xml.ws.Endpoint](https://docs.oracle.com/javase/7/docs/api/javax/xml/ws/Endpoint.html)[ API](https://docs.oracle.com/javase/7/docs/api/javax/xml/ws/Endpoint.html)构建和部署 Web 服务。

我们将 CountryServicePublisher作为Java应用程序运行，以发布将接受传入请求的端点。换句话说，这将是我们的服务器。

启动服务器后，点击 URL [http://localhost:8888/ws/country?wsdl](http://localhost:8888/ws/country?wsdl)会为我们提供 Web 服务描述文件。WSDL 充当了解服务产品和为客户端生成实现代码的指南。

### 2.2. Web 服务描述语言

让我们看看我们的 Web 服务的 WSDL，country：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<definitions <!-- namespace declarations -->
    targetNamespace="http://server.ws.soap.baeldung.com/" name="CountryServiceImplService">
    <types>
        <xsd:schema>
            <xsd:import namespace="http://server.ws.soap.baeldung.com/" 
              schemaLocation="http://localhost:8888/ws/country?xsd=1"></xsd:import>
        </xsd:schema>
    </types>
    <message name="findByName">
        <part name="arg0" type="xsd:string"></part>
    </message>
    <message name="findByNameResponse">
        <part name="return" type="tns:country"></part>
    </message>
    <portType name="CountryService">
        <operation name="findByName">
            <input wsam:Action="http://server.ws.soap.baeldung.com/CountryService/findByNameRequest" 
              message="tns:findByName"></input>
            <output wsam:Action="http://server.ws.soap.baeldung.com/CountryService/findByNameResponse" 
              message="tns:findByNameResponse"></output>
        </operation>
    </portType>
    <binding name="CountryServiceImplPortBinding" type="tns:CountryService">
        <soap:binding transport="http://schemas.xmlsoap.org/soap/http" style="rpc"></soap:binding>
        <operation name="findByName">
            <soap:operation soapAction=""></soap:operation>
            <input>
                <soap:body use="literal" namespace="http://server.ws.soap.baeldung.com/"></soap:body>
            </input>
            <output>
                <soap:body use="literal" namespace="http://server.ws.soap.baeldung.com/"></soap:body>
            </output>
        </operation>
    </binding>
    <service name="CountryServiceImplService">
        <port name="CountryServiceImplPort" binding="tns:CountryServiceImplPortBinding">
            <soap:address location="http://localhost:8888/ws/country"></soap:address>
        </port>
    </service>
</definitions>
```

简而言之，这是它提供的有用信息：

-   我们可以使用字符串参数调用方法findByName 。
-   作为响应，该服务将返回一个自定义类型的country。
-   类型在[http://localhost:8888/ws/country?xsd=1](http://localhost:8888/ws/country?xsd=1)位置生成的xsd模式中定义：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xs:schema <!-- namespace declarations -->
    targetNamespace="http://server.ws.soap.baeldung.com/">
    <xs:complexType name="country">
        <xs:sequence>
            <xs:element name="capital" type="xs:string" minOccurs="0"></xs:element>
            <xs:element name="currency" type="tns:currency" minOccurs="0"></xs:element>
            <xs:element name="name" type="xs:string" minOccurs="0"></xs:element>
            <xs:element name="population" type="xs:int"></xs:element>
        </xs:sequence>
    </xs:complexType>
    <xs:simpleType name="currency">
        <xs:restriction base="xs:string">
            <xs:enumeration value="EUR"></xs:enumeration>
            <xs:enumeration value="INR"></xs:enumeration>
            <xs:enumeration value="USD"></xs:enumeration>
        </xs:restriction>
    </xs:simpleType>
</xs:schema>
```

这就是我们实现客户端所需的全部内容。

让我们在下一节中看看如何。

## 3.使用wsimport生成客户端代码

### 3.1。对于 JDK 8

首先，让我们看看如何使用 JDK 8 生成客户端代码。

首先，让我们在pom.xml中添加一个插件，以通过 Maven 使用此工具：

```xml
<plugin> 
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>jaxws-maven-plugin</artifactId>
    <version>2.6</version>
    <executions> 
        <execution> 
            <id>wsimport-from-jdk</id>
            <goals>
                <goal>wsimport</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <wsdlUrls>
            <wsdlUrl>http://localhost:8888/ws/country?wsdl</wsdlUrl> 
        </wsdlUrls>
        <keep>true</keep> 
        <packageName>com.baeldung.soap.ws.client.generated</packageName> 
        <sourceDestDir>src/main/java</sourceDestDir>
    </configuration>
</plugin>
```

其次，让我们执行这个插件：

```bash
mvn clean jaxws:wsimport
```

就这样！上面的命令会在我们插件配置中提供的sourceDestDir里面的指定包com.baeldung.soap.ws.client.generated生成代码。

另一种实现相同目的的方法是使用[wsimport](https://docs.oracle.com/javase/7/docs/technotes/tools/share/wsimport.html)实用程序。它随标准 JDK 8 发行版开箱即用，可以在JAVA_HOME/bin目录下找到。

要使用wsimport生成客户端代码，我们可以导航到项目的根目录并运行以下命令：

```bash
JAVA_HOME/bin/wsimport -s src/main/java/ -keep -p com.baeldung.soap.ws.client.generated "http://localhost:8888/ws/country?wsdl"
```

重要的是要记住，服务端点应该可用才能成功执行插件或命令。

### 3.2. 对于 JDK 11

从 JDK 11 开始，wsimport作为 JDK 的一部分被删除，并且不再随标准发行版一起提供。

然而，它是开源给 Eclipse 基金会的。

为了使用wsimport生成Java11 及更高版本的客户端代码，除了[jaxws-maven-plugin](https://search.maven.org/search?q=g:com.sun.xml.ws AND a:jaxws-maven-plugin)之外，我们还需要添加[jakarta.xml.ws-api](https://search.maven.org/search?q=g:jakarta.xml.ws AND a:jakarta.xml.ws-api)、[jaxws-rt](https://search.maven.org/search?q=g:com.sun.xml.ws AND a:jaxws-rt)和[jaxws-ri](https://search.maven.org/search?q=g:com.sun.xml.ws AND a:jaxws-ri)依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>jakarta.xml.ws</groupId
        <artifactId>jakarta.xml.ws-api</artifactId
        <version>3.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.sun.xml.ws</groupId>
        <artifactId>jaxws-rt</artifactId>
        <version>3.0.0</version
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.sun.xml.ws</groupId>
        <artifactId>jaxws-ri</artifactId>
        <version>2.3.1</version
        <type>pom</type>
    </dependency>
</dependencies>
<build>
    <plugins>        
        <plugin>
            <groupId>com.sun.xml.ws</groupId>
            <artifactId>jaxws-maven-plugin</artifactId>
            <version>2.3.2</version>
            <configuration>
                <wsdlUrls>
                    <wsdlUrl>http://localhost:8888/ws/country?wsdl</wsdlUrl>
                </wsdlUrls>
                <keep>true</keep>
                <packageName>com.baeldung.soap.ws.client.generated</packageName>
                <sourceDestDir>src/main/java</sourceDestDir>
            </configuration>
        </plugin>
    </plugins>
</build>

```

现在，要在com.baeldung.soap.ws.client.generated包中生成客户端代码，我们需要与之前相同的 Maven 命令：

```bash
mvn clean jaxws:wsimport
```

接下来，让我们看看对于两个Java版本都相同的生成的工件。

### 3.3. 生成的 POJO

基于我们之前看到的xsd，该工具将生成一个名为 Country.java的文件：

```java
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "country", propOrder = { "capital", "currency", "name", "population" })
public class Country {
    protected String capital;
    @XmlSchemaType(name = "string")
    protected Currency currency;
    protected String name;
    protected int population;
    // standard getters and setters
}
```

正如我们所见，生成的类使用[JAXB 注解](https://www.baeldung.com/jaxb)进行修饰，用于将对象编组到 XML 和从 XML 解组。

此外，它还会生成一个Currency枚举：

```java
@XmlType(name = "currency")
@XmlEnum
public enum Currency {
    EUR, INR, USD;
    public String value() {
        return name();
    }
    public static Currency fromValue(String v) {
        return valueOf(v);
    }
}
```

### 3.4. 国家服务

第二个生成的工件是充当实际 Web 服务代理的接口。

CountryService接口声明了与我们的服务器相同的方法findByName：

```java
@WebService(name = "CountryService", targetNamespace = "http://server.ws.soap.baeldung.com/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@XmlSeeAlso({ ObjectFactory.class })
public interface CountryService {
    @WebMethod
    @WebResult(partName = "return")
    @Action(input = "http://server.ws.soap.baeldung.com/CountryService/findByNameRequest", 
      output = "http://server.ws.soap.baeldung.com/CountryService/findByNameResponse")
    public Country findByName(@WebParam(name = "arg0", partName = "arg0") String arg0);
}
```

值得注意的是，该接口被标记为javax.jws.WebService，其中[SOAPBinding.Style](https://docs.oracle.com/javase/7/docs/api/javax/jws/soap/SOAPBinding.Style.html)为由服务的 WSDL 定义的 RPC。

方法findByName被注解为声明它是javax.jws.WebMethod，具有预期的输入和输出参数类型。

### 3.5. CountryServiceImplService

我们生成的下一个类CountryServiceImplService扩展了 javax.xml.ws.Service。

它的注解[WebServiceClient](https://docs.oracle.com/javase/7/docs/api/javax/xml/ws/WebServiceClient.html) 表示它是一个服务的客户端视图：

```java
@WebServiceClient(name = "CountryServiceImplService", 
  targetNamespace = "http://server.ws.soap.baeldung.com/", 
  wsdlLocation = "http://localhost:8888/ws/country?wsdl")
public class CountryServiceImplService extends Service {

    private final static URL COUNTRYSERVICEIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException COUNTRYSERVICEIMPLSERVICE_EXCEPTION;
    private final static QName COUNTRYSERVICEIMPLSERVICE_QNAME = 
      new QName("http://server.ws.soap.baeldung.com/", "CountryServiceImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8888/ws/country?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        COUNTRYSERVICEIMPLSERVICE_WSDL_LOCATION = url;
        COUNTRYSERVICEIMPLSERVICE_EXCEPTION = e;
    }

    public CountryServiceImplService() {
        super(__getWsdlLocation(), COUNTRYSERVICEIMPLSERVICE_QNAME);
    }

    // other constructors 

    @WebEndpoint(name = "CountryServiceImplPort")
    public CountryService getCountryServiceImplPort() {
        return super.getPort(new QName("http://server.ws.soap.baeldung.com/", "CountryServiceImplPort"), 
          CountryService.class);
    }

    private static URL __getWsdlLocation() {
        if (COUNTRYSERVICEIMPLSERVICE_EXCEPTION != null) {
            throw COUNTRYSERVICEIMPLSERVICE_EXCEPTION;
        }
        return COUNTRYSERVICEIMPLSERVICE_WSDL_LOCATION;
    }

}
```

这里要注意的重要方法是getCountryServiceImplPort。给定服务端点的限定名称或QName以及动态代理的服务端点接口名称，它会返回一个代理实例。

要调用 Web 服务，我们需要使用这个代理，我们很快就会看到。

使用代理使我们看起来好像是在本地调用服务，抽象出远程调用的复杂性。

## 4. 测试客户端

接下来，我们将编写一个 JUnit 测试以使用生成的客户端代码连接到 Web 服务。

在我们这样做之前，我们需要在客户端获取服务的代理实例：

```java
@BeforeClass
public static void setup() {
    CountryServiceImplService service = new CountryServiceImplService();
    CountryService countryService = service.getCountryServiceImplPort();
}
```

对于更高级的场景，例如启用或禁用[WebServiceFeature](https://docs.oracle.com/javase/7/docs/api/javax/xml/ws/WebServiceFeature.html) ，我们可以为CountryServiceImplService使用其他生成的构造函数。

现在让我们看一些测试：

```java
@Test
public void givenCountryService_whenCountryIndia_thenCapitalIsNewDelhi() {
    assertEquals("New Delhi", countryService.findByName("India").getCapital());
}

@Test
public void givenCountryService_whenCountryFrance_thenPopulationCorrect() {
    assertEquals(66710000, countryService.findByName("France").getPopulation());
}

@Test
public void givenCountryService_whenCountryUSA_thenCurrencyUSD() {
    assertEquals(Currency.USD, countryService.findByName("USA").getCurrency());
}

```

正如我们所见，调用远程服务的方法变得像在本地调用方法一样简单。代理的findByName方法返回了一个与我们提供的名称匹配的Country实例。然后我们使用 POJO 的各种 getter 来断言期望值。

## 5. 总结

在本文中，我们看到了如何使用Java8 和 11 的 JAX-WS RI 和wsimport实用程序在Java中调用 SOAP Web 服务。

或者，我们可以使用其他 JAX-WS 实现，例如 Apache CXF、Apache Axis2 和 Spring 来做同样的事情。