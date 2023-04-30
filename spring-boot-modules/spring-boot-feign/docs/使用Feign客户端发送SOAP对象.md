## 1. 概述

[Feign](https://github.com/OpenFeign/feign)抽象了HTTP调用并使它们具有声明性。通过这样做，Feign隐藏了较低级别的细节，如HTTP连接管理、硬编码URL和其他样板代码。使用Feign客户端的显著优势在于HTTP调用变得简单并消除了大量代码。通常，我们使用Feign用于REST API application/json媒体类型。但是，Feign客户端与其他媒体类型(如text/xml、multipart请求等)也能很好地配合使用，

在本教程中，让我们学习如何使用Feign调用基于SOAP的Web服务(text/xml)。

## 2. SOAP Web服务

假设有一个[SOAP Web服务](https://www.baeldung.com/spring-boot-soap-web-service)有两个操作-getUser和createUser。

让我们使用[cURL](https://man7.org/linux/man-pages/man1/curl.1.html)调用操作createUser：

```shell
curl -d @request.xml -i -o -X POST --header 'Content-Type: text/xml'
  http://localhost:18080/ws/users
```

在这里，request.xml包含SOAP负载：

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:feig="http://www.tuyucheng.com/springbootsoap/feignclient">
    <soapenv:Header/>
    <soapenv:Body>
        <feig:createUserRequest>
            <feig:user>
                <feig:id>1</feig:id>
                <feig:name>john doe</feig:name>
                <feig:email>john.doe@gmail.com</feig:email>
            </feig:user>
        </feig:createUserRequest>
    </soapenv:Body>
</soapenv:Envelope>
```

如果所有配置都正确，我们会得到一个成功的响应：

```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Header/>
    <SOAP-ENV:Body>
        <ns2:createUserResponse xmlns:ns2="http://www.tuyucheng.com/springbootsoap/feignclient">
            <ns2:message>Success! Created the user with id - 1</ns2:message>
        </ns2:createUserResponse>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

同样，另一个操作getUser也可以使用cURL调用。

## 3. 依赖关系

接下来，让我们看看如何使用[Feign](https://www.baeldung.com/intro-to-feign)来调用这个SOAP Web服务。让我们开发两个不同的客户端来调用SOAP服务。Feign支持多个现有的HTTP客户端，如[Apache HttpComponents](https://hc.apache.org/)、[OkHttp](https://square.github.io/okhttp/)、java.net.URL等。让我们使用Apache HttpComponents作为我们的底层HTTP客户端。首先，让我们为[OpenFeign Apache HttpComponents](https://central.sonatype.com/artifact/io.github.openfeign/feign-hc5/12.3)添加依赖项：

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-hc5</artifactId>
    <version>11.8</version>
</dependency>
```

在接下来的部分中，我们将学习几种使用Feign调用SOAP Web服务的方法。

## 4. 纯文本形式的SOAP对象

我们可以将SOAP请求作为纯文本发送，content-type和accept标头设置为text/xml。现在让我们开发一个客户端来演示这种方法：

```java
public interface SoapClient {
    @RequestLine("POST")
    @Headers({"SOAPAction: createUser", "Content-Type: text/xml;charset=UTF-8", "Accept: text/xml"})
    String createUserWithPlainText(String soapBody);
}
```

在这里，createUserWithPlainText接收一个字符串SOAP负载。请注意，我们明确定义了accept和content-type标头。这是因为当以文本形式发送SOAP正文时，必须将Content-Type和Accept标头作为text/xml提及。

这种方法的一个缺点是我们应该事先知道SOAP负载。幸运的是，如果WSDL可用，则可以使用[SoapUI](https://www.soapui.org/)等开源工具生成有效负载。一旦有效负载准备就绪，让我们使用Feign调用SOAP Web服务：

```java
@Test
void givenSOAPPayload_whenRequest_thenReturnSOAPResponse() throws Exception {
    String successMessage="Success! Created the user with id";
    SoapClient client = Feign.builder()
       .client(new ApacheHttp5Client())
       .target(SoapClient.class, "http://localhost:18080/ws/users/");
    
    assertDoesNotThrow(() -> client.createUserWithPlainText(soapPayload()));
    
    String soapResponse= client.createUserWithPlainText(soapPayload());
 
    assertNotNull(soapResponse);
    assertTrue(soapResponse.contains(successMessage));
}
```

Feign支持记录SOAP消息和其他与HTTP相关的信息，此信息对于调试至关重要。因此，让我们启用Feign日志记录。这些消息的记录需要额外的[feign-slf4j](https://central.sonatype.com/artifact/io.github.openfeign/feign-slf4j/12.3)依赖：

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-slf4j</artifactId>
    <version>11.8</version>
</dependency>
```

让我们增强我们的测试用例以包含日志信息：

```java
SoapClient client = Feign.builder()
    .client(new ApacheHttp5Client())
    .logger(new Slf4jLogger(SoapClient.class))
    .logLevel(Logger.Level.FULL)
    .target(SoapClient.class, "http://localhost:18080/ws/users/");
```

现在，当我们运行测试时，得到类似于以下内容的日志：

```shell
18:01:58.295 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> "SOAPAction: createUser[\r][\n]"
18:01:58.295 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> "<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
  xmlns:feig="http://www.tuyucheng.com/springbootsoap/feignclient">[\n]"
18:01:58.295 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> " <soapenv:Header/>[\n]"
18:01:58.295 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> " <soapenv:Body>[\n]"
18:01:58.295 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> " <feig:createUserRequest>[\n]"
18:01:58.295 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> " <feig:user>[\n]"
18:01:58.295 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> " <feig:id>1</feig:id>[\n]"
18:01:58.295 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> " <feig:name>john doe</feig:name>[\n]"
18:01:58.296 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> " <feig:email>john.doe@gmail.com</feig:email>[\n]"
18:01:58.296 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> " </feig:user>[\n]"
18:01:58.296 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> " </feig:createUserRequest>[\n]"
18:01:58.296 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> " </soapenv:Body>[\n]"
18:01:58.296 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 >> "</soapenv:Envelope>"
18:01:58.300 [main] DEBUG org.apache.hc.client5.http.wire - http-outgoing-0 << "<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"><SOAP-ENV:Header/><SOAP-ENV:Body><ns2:createUserResponse xmlns:ns2="http://www.tuyucheng.com/springbootsoap/feignclient"><ns2:message>Success! Created the user with id - 1</ns2:message></ns2:createUserResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>"
```

## 5. Feign SOAP编解码器

调用SOAP Web服务的一种更简洁、更好的方法是使用[Feign的SOAP编解码器](https://github.com/OpenFeign/feign/tree/master/soap)。编解码器有助于编组SOAP消息(Java到SOAP)/解组(SOAP到Java)。但是，编解码器需要额外的[feign-soap](https://central.sonatype.com/artifact/io.github.openfeign/feign-soap/12.3)依赖项。因此，让我们声明此依赖项：

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-soap</artifactId>
    <version>11.8</version>
</dependency>
```

Feign SOAP编解码器使用[JAXB](https://www.baeldung.com/jaxb)和[SoapMessage](https://docs.oracle.com/javase/8/docs/api/javax/xml/soap/SOAPMessage.html)对SOAP对象进行编码和解码，而JAXBContextFactory提供所需的编组器和解组器。

接下来，基于我们创建的XSD，让我们[生成](https://www.baeldung.com/maven-wsdl-stubs)域类。JAXB需要这些域类来编组和解组SOAP消息。首先，让我们向pom.xml添加一个插件：

```xml
<plugin>
    <groupId>org.jvnet.jaxb2.maven2</groupId>
    <artifactId>maven-jaxb2-plugin</artifactId>
    <version>0.14.0</version>
    <executions>
        <execution>
            <id>feign-soap-stub-generation</id>
            <phase>compile</phase>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <schemaDirectory>target/generated-sources/jaxb</schemaDirectory>
                <schemaIncludes>
                    <include>*.xsd</include>
                </schemaIncludes>
                <generatePackage>cn.tuyucheng.taketoday.feign.soap.client</generatePackage>
                <generateDirectory>target/generated-sources/jaxb</generateDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```

在这里，我们将插件配置为在compile阶段运行。现在，让我们生成存根：

```shell
mvn clean compile
```

成功构建后，target文件夹包含源：

<img src="../assets/img.png">

接下来，让我们使用这些存根和Feign来调用SOAP Web服务。但是，首先，让我们向SoapClient添加一个新方法：

```java
@RequestLine("POST")
@Headers({"Content-Type: text/xml;charset=UTF-8"})
CreateUserResponse createUserWithSoap(CreateUserRequest soapBody);
```

接下来，让我们测试SOAP Web服务：

```java
@Test
void whenSoapRequest_thenReturnSoapResponse() {
    JAXBContextFactory jaxbFactory = new JAXBContextFactory.Builder()
        .withMarshallerJAXBEncoding("UTF-8").build();
    SoapClient client = Feign.builder()
        .encoder(new SOAPEncoder(jaxbFactory))
        .decoder(new SOAPDecoder(jaxbFactory))
        .target(SoapClient.class, "http://localhost:18080/ws/users/");
    CreateUserRequest request = new CreateUserRequest();
    User user = new User();
    user.setId("1");
    user.setName("John Doe");
    user.setEmail("john.doe@gmail");
    request.setUser(user);
    CreateUserResponse response = client.createUserWithSoap(request);

    assertNotNull(response);
    assertNotNull(response.getMessage());
    assertTrue(response.getMessage().contains("Success")); 
}
```

让我们增强我们的测试用例以记录HTTP和SOAP消息：

```java
SoapClient client = Feign.builder()
    .encoder(new SOAPEncoder(jaxbFactory))
    .errorDecoder(new SOAPErrorDecoder())
    .logger(new Slf4jLogger())
    .logLevel(Logger.Level.FULL)
    .decoder(new SOAPDecoder(jaxbFactory))
    .target(SoapClient.class, "http://localhost:18080/ws/users/");
```

此代码生成我们之前看到的类似日志。

最后，让我们处理SOAP错误。Feign提供了一个[SOAPErrorDecoder](https://javadoc.io/static/io.github.openfeign/feign-soap/10.3.0/feign/soap/SOAPErrorDecoder.html)，它将SOAP错误作为SOAPFaultException返回。因此，让我们将这个SOAPErrorDecoder设置为一个Feign错误解码器并处理SOAP错误：

```java
SoapClient client = Feign.builder()
    .encoder(new SOAPEncoder(jaxbFactory))
    .errorDecoder(new SOAPErrorDecoder())  
    .decoder(new SOAPDecoder(jaxbFactory))
    .target(SoapClient.class, "http://localhost:18080/ws/users/");
try {
    client.createUserWithSoap(request);
} catch (SOAPFaultException soapFaultException) {
    assertNotNull(soapFaultException.getMessage());
    assertTrue(soapFaultException.getMessage().contains("This is a reserved user id"));
}
```

在这里，如果SOAP Web服务抛出SOAP错误，它将由SOAPFaultException处理。

## 6. 总结

在本文中，我们学习了使用Feign调用SOAP Web服务。Feign是一种声明式HTTP客户端，可以轻松调用SOAP/REST Web服务。使用Feign的好处是它减少了代码行数，从而减少错误和测试代码。