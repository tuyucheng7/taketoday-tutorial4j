## 1. 概述

之前，我们了解了如何[使用Spring创建SOAP Web服务](https://www.baeldung.com/spring-boot-soap-web-service)。

**在本教程中，我们将学习如何创建基于Spring的客户端来使用此Web服务**。

在[Java中调用SOAP Web服务时](https://www.baeldung.com/java-soap-web-service)，我们使用JAX-WS RI做了同样的事情。

## 2. Spring SOAP Web服务-快速回顾

早些时候，我们在Spring中创建了一个Web服务来获取一个国家的数据，给定它的名字。在深入研究客户端实现之前，让我们快速回顾一下我们是如何做到这一点的。

按照契约优先的方法，我们首先编写了一个定义域的XML模式文件。然后，我们使用此XSD通过[jaxb2-maven-plugin](https://central.sonatype.com/artifact/org.codehaus.mojo/jaxb2-maven-plugin/3.1.0)为请求、响应和数据模型生成类。

之后我们编写了四个类：

-   [CountryEndpoint](https://www.baeldung.com/spring-boot-soap-web-service#4-add-the-soap-web-service-endpoint)：回复请求的端点
-   [CountryRepository](https://www.baeldung.com/spring-boot-soap-web-service#4-add-the-soap-web-service-endpoint)：后端的Repository，用于提供国家数据
-   [WebServiceConfig](https://www.baeldung.com/spring-boot-soap-web-service#5-the-soap-web-service-configuration-beans)：定义所需bean的配置
-   [应用程序](https://www.baeldung.com/spring-boot-soap-web-service#1-build-and-run-the-project)：Spring Boot应用程序，使我们的服务可供使用

最后，我们通过cURL发送SOAP请求对其进行了测试。

现在让我们通过运行上面的Boot应用程序来启动服务器，然后继续下一步。

## 3. 客户端

在这里，**我们将构建一个Spring客户端来调用和测试上述Web服务**。

现在，让我们逐步地看看我们需要做什么来创建一个客户端。

### 3.1 生成客户端代码

首先，我们将使用[http://localhost:8080/ws/countries.wsdl](http://localhost:8080/ws/countries.wsdl)提供的WSDL生成一些类。我们将下载并将其保存在我们的src/main/resources文件夹中。

**要使用Maven生成代码，我们将[maven-jaxb2-plugin](https://central.sonatype.com/artifact/org.jvnet.jaxb2.maven2/maven-jaxb2-plugin/0.15.2)添加到我们的pom.xml中**：

```xml
<plugin>
    <groupId>org.jvnet.jaxb2.maven2</groupId>
    <artifactId>maven-jaxb2-plugin</artifactId>
    <version>0.14.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <schemaLanguage>WSDL</schemaLanguage>
        <generateDirectory>${project.basedir}/src/main/java</generateDirectory>
        <generatePackage>cn.tuyucheng.taketoday.springsoap.client.gen</generatePackage>
        <schemaDirectory>${project.basedir}/src/main/resources</schemaDirectory>
        <schemaIncludes>
            <include>countries.wsdl</include>
        </schemaIncludes>
    </configuration>
</plugin>
```

值得注意的是，在我们定义的插件配置中：

-   generateDirectory：将保存生成的工件的文件夹
-   generatePackage：工件将使用的包名称
-   schemaDirectory和schemaIncludes：WSDL的目录和文件名

为了执行JAXB生成过程，我们将通过简单地构建项目来执行此插件：

```shell
mvn compile
```

有趣的是，**此处生成的工件与为服务生成的工件相同**。

让我们列出我们将要使用的那些：

-   Country.java和Currency.java：代表数据模型的POJO
-   GetCountryRequest.java：请求类型
-   GetCountryResponse.java：响应类型

该服务可以部署在世界任何地方，并且仅使用它的WSDL，我们就能够在客户端生成与服务器相同的类！

### 3.2 CountryClient

接下来，我们需要扩展Spring的[WebServiceGatewaySupport](https://docs.spring.io/spring-ws/sites/2.0/apidocs/org/springframework/ws/client/core/support/WebServiceGatewaySupport.html)以与Web服务进行交互。

我们称这个类为CountryClient：

```java
public class CountryClient extends WebServiceGatewaySupport {

    public GetCountryResponse getCountry(String country) {
        GetCountryRequest request = new GetCountryRequest();
        request.setName(country);

        GetCountryResponse response = (GetCountryResponse) getWebServiceTemplate()
              .marshalSendAndReceive(request);
        return response;
    }
}
```

在这里，我们定义了单个方法getCountry，对应于Web服务公开的操作。在该方法中，我们创建了一个GetCountryRequest实例并调用Web服务来获取GetCountryResponse。换句话说，**这里是我们执行SOAP交换的地方**。

正如我们所见，Spring通过其[WebServiceTemplate](https://docs.spring.io/spring-ws/site/apidocs/org/springframework/ws/client/core/WebServiceTemplate.html)使调用变得非常简单。我们使用模板的方法marshalSendAndReceive来执行SOAP交换。

XML转换在这里通过插入式Marshaller处理。

现在让我们看看这个Marshaller来自哪里的配置。

### 3.3 CountryClientConfig

我们需要配置我们的Spring WS客户端的是两个bean。

首先，一个Jaxb2Marshaller将消息与XML相互转换，其次是我们的CountryClient，它将注入marshaller bean：

```java
@Configuration
public class CountryClientConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("cn.tuyucheng.taketoday.springsoap.client.gen");
        return marshaller;
    }
    @Bean
    public CountryClient countryClient(Jaxb2Marshaller marshaller) {
        CountryClient client = new CountryClient();
        client.setDefaultUri("http://localhost:8080/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
```

在这里，我们需要注意编组器的上下文路径与pom.xml的插件配置中指定的generatePackage相同。

另请注意此处客户端的默认URI。它被设置为WSDL中指定的soap:address位置。

## 4. 测试客户端

接下来，我们将编写一个JUnit测试来验证我们的客户端是否按预期运行：

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CountryClientConfig.class, loader = AnnotationConfigContextLoader.class)
public class ClientLiveTest {

    @Autowired
    CountryClient client;

    @Test
    public void givenCountryService_whenCountryPoland_thenCapitalIsWarsaw() {
        GetCountryResponse response = client.getCountry("Poland");
        assertEquals("Warsaw", response.getCountry().getCapital());
    }

    @Test
    public void givenCountryService_whenCountrySpain_thenCurrencyEUR() {
        GetCountryResponse response = client.getCountry("Spain");
        assertEquals(Currency.EUR, response.getCountry().getCurrency());
    }
}
```

正如我们所看到的，我们注入了CountryClientConfig中定义的CountryClient bean。然后，我们使用它的getCountry调用远程服务，如前所述。

此外，我们能够使用生成的数据模型POJOs、Country和Currency提取断言所需的信息。

## 5. 总结

在本教程中，我们了解了如何使用Spring WS调用SOAP Web服务的基础知识。

我们只是触及了Spring在SOAP Web服务领域所提供的内容的皮毛；有很多[值得探索](https://docs.spring.io/spring-ws/docs/2.4.0.RELEASE/reference/htmlsingle/)的地方。