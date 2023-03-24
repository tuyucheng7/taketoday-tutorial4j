## 1. 概述

微服务架构允许我们独立开发、测试和部署应用程序的不同组件。

虽然这样的组件可以独立开发，但单独测试它可能具有挑战性。对于微服务的真正集成测试，我们必须测试它与其他API的交互。

当我们需要mock外部API以测试依赖于这些外部API的特定API以完成事务时，[WireMock](https://wiremock.org/)有助于集成测试。WireMock是一种流行的HTTP模拟服务器，有助于mock API和存根响应。

值得一提的是，WireMock可以作为应用程序的一部分或独立进程运行。

## 2. Maven依赖

首先将WireMock依赖项导入到项目中。我们可以在[Maven仓库](https://mvnrepository.com/artifact/com.github.tomakehurst/wiremock-jre8)中找到它的最新版本。

```xml
<dependency>
    <groupId>com.github.tomakehurst</groupId>
    <artifactId>wiremock-jre8</artifactId>
    <version>2.33.2</version>
    <scope>test</scope>
</dependency>
```

## 3. 引导WireMock

有几种方法可以开始使用WireMock。让我们看看它们。

### 3.1 使用WireMockServer

创建WireMockServer实例的最简单方法是调用其构造函数。**默认情况下，WireMock使用主机名localhost和端口号8080**。我们可以通过configureFor()方法使用随机/固定端口号和自定义主机名初始化WireMockServer。

**在测试执行之前启动服务器并在测试完成后停止服务器非常重要**。我们可以在测试之间重置mock存根。

以下是使用[JUnit 5](https://howtodoinjava.com/junit-5-tutorial/)测试设置WireMock的示例。请注意，此技术也可用于独立的Java应用程序。它不仅限于测试。

```java
public class WireMockServerTest {
    static WireMockServer wireMockServer = new WireMockServer();
    
    @BeforeAll
    public static void beforeAll() {
        // WireMock.configureFor("custom-host", 9000, "/api-root-url");
        wireMockServer.start();
    }
    
    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }
    
    @AfterEach
    public void afterEach() {
        wireMockServer.resetAll();
    }
}
```

### 3.2 使用WireMockRule

WireMockRule是在JUnit 4测试中配置、启动和停止服务器的首选方式，尽管我们也可以在JUnit 5测试中使用它。它在功能和控制方面与WireMockServer类非常相似。

以下是使用JUnit 4测试设置WireMock的示例。

```java
public class WireMockServerTest {
    @Rule
    WireMockRule wireMockRule = new WireMockRule();
    
    @Before
    public void beforeAll() {
        wireMockRule.start();
    }
    
    @After
    public void afterAll() {
        wireMockRule.stop();
    }
    
    @AfterEach
    public void afterEach() {
        wireMockRule.resetAll();
    }
}
```

### 3.3 使用@WireMockTest

@WireMockTest注解是另一种使用WireMock为JUnit测试提供支持的便捷方式。这是类级别的注解。

**@WireMockTest在测试开始前启动WireMock服务器，在测试结束后停止服务器并清理测试之间的上下文**。所以基本上，它隐含地执行了我们在前面部分中使用前后注解所做的所有三个步骤。

```java
@WireMockTest
public class WireMockTestAnnotationTest {
   // ...
}
```

### 3.4 启用HTTPS

我们可以通过httpsEnabled注解参数启用HTTPS。默认情况下，将分配一个随机端口。要固定HTTPS端口号，请使用httpsPort参数。

```java
@WireMockTest(httpsEnabled = true, httpsPort = 8443)
```

使用WireMockRule，我们可以将**WireMockConfiguration.options()**作为构造函数参数传递。相同的配置步骤也适用于WireMockServer。

```java
WireMockServer wiremockServer = new WireMockServer(options().port(8080).httpsPort(8443));

// or

@Rule
public WireMockRule wireMockRule = new WireMockRule(options().port(8080).httpsPort(8443);
```

## 4. WireMock的简单示例

让我们从创建一个非常简单的API存根开始，使用任何HTTP客户端调用它并验证mock服务器是否被命中。

-   要存根mock API响应，请使用该WireMock.stubFor()方法。它接收一个MappingBuilder实例，我们可以使用它来构建API映射信息，例如URL、请求参数和正文、标头、授权等。
-   要测试API，我们可以使用任何HTTP客户端，例如[HttpClient](https://howtodoinjava.com/java/library/jaxrs-client-httpclient-get-post/)、[RestTemplate](https://howtodoinjava.com/spring-boot2/resttemplate/spring-restful-client-resttemplate-example/)或[TestRestTemplate](https://howtodoinjava.com/spring-boot2/testing/testresttemplate-post-example/)。我们将在本文中使用TestRestTemplate。
-   要验证请求是否已命中mock API，我们可以使用WireMock.verify()方法。

以下是一个非常简单的mock API的所有三个步骤的示例。这应该能够帮助理解WireMock的基本用法。

>   如果我们更喜欢在测试中使用BDD语言，那么我们可以用givenThat()替换stubFor()。

```java
@WireMockTest
public class WireMockTestAnnotationTest {
    
    @Test
    void simpleStubTesting(WireMockRuntimeInfo wmRuntimeInfo) {
        String responseBody = "Hello World !!";
        String apiUrl = "/api-url";
        // define stub
        stubFor(get(apiUrl).willReturn(ok(responseBody)));
        // hit API and check response
        String apiResponse = getContent(wmRuntimeInfo.getHttpBaseUrl() + apiUrl);
        assertEquals(apiResponse, responseBody);
        // verify API is hit
        verify(getRequestedFor(urlEqualTo(apiUrl)));
    }
    
    private String getContent(String url) {
        TestRestTemplate testRestTemplate = new TestRestTemplate();
        return testRestTemplate.getForObject(url, String.class);
    }
}
```

## 5. 高级用法

### 5.1 配置API请求

WireMock提供了许多有用的静态方法来存根API请求和响应部分。

使用get()、put()、post()、delete()等方法匹配相应的HTTP方法。使用any()来匹配与URL匹配的任何HTTP方法。

```java
stubFor(delete("/url").willReturn(ok()));
stubFor(post("/url").willReturn(ok()));
stubFor(any("/url").willReturn(ok()));
```

使用withHeader()、withCookie()、withQueryParam()、withRequestBody()等其他方法来设置请求的其他部分。我们也可以使用withBasicAuth()信息传递授权信息。

```java
stubFor(get(urlPathEqualTo("/api-url"))
    .withHeader("Accept", containing("xml"))
    .withCookie("JSESSIONID", matching(".*"))
    .withQueryParam("param-name", equalTo("param-value"))
    .withBasicAuth("username", "plain-password")
    //.withRequestBody(equalToXml("part-of-request-body"))
    .withRequestBody(matchingXPath("//root-tag"))
    /*.withMultipartRequestBody(
        aMultipart()
            .withName("preview-image")
            .withHeader("Content-Type", containing("image"))
            .withBody(equalToJson("{}"))
    )*/
    .willReturn(aResponse()));
```

### 5.2 配置API响应

通常，我们只对响应状态、响应头和响应正文感兴趣。WireMock支持使用简单的方法在响应中存根所有这些组件。

```java
stubFor(get(urlEqualTo("/api-url"))
    .willReturn(aResponse()
        .withStatus(200)
        .withStatusMessage("Everything was just fine!")
        .withHeader("Content-Type", "application/json")
        .withBody("{ \"message\": \"Hello world!\" }")));
```

### 5.3 测试API延迟和超时

要测试延迟的API响应以及当前API如何处理超时，我们可以使用以下方法：

withFixedDelay()可用于**配置固定延迟**，在指定的毫秒数之前不会返回响应。

```java
stubFor(get(urlEqualTo("/api-url"))
    .willReturn(ok().withFixedDelay(2000)));
```

withRandomDelay()可用于**从随机分布中获取延迟**。WireMock支持随机分布类型：均匀分布和对数正态分布。

```java
stubFor(get(urlEqualTo("/api-url"))
    .willReturn(
        aResponse()
            .withStatus(200)
            .withFixedDelay(2000)
            // .withLogNormalRandomDelay(90, 0.1)
            // .withRandomDelay(new UniformDistribution(15, 25))
    ));
```

我们还可以使用withChunkedDribbleDelay()来**模拟一个慢速网络，其中响应以块的形式接收**，中间有时间延迟。它有两个参数：numberOfChunks和totalDuration。

```java
stubFor(get("/api-url").willReturn(
    aResponse()
        .withStatus(200)
        .withBody("api-response")
        .withChunkedDribbleDelay(5, 1000)));
```

### 5.4 测试不良响应

在微服务架构中，API可能随时出现异常行为，因此API消费者必须准备好处理这些情况。WireMock通过使用withFault()方法存根错误的响应来帮助这种响应处理。

```java
stubFor(get(urlEqualTo("/api-url"))
    .willReturn(aResponse()
        .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
```

它支持以下枚举常量：

-   EMPTY_RESPONSE：返回一个完全空的响应
-   RANDOM_DATA_THEN_CLOSE：发送垃圾然后关闭连接
-   MALFORMED_RESPONSE_CHUNK：发送一个OK状态标头，然后发送垃圾，然后关闭连接
-   CONNECTION_RESET_BY_PEER：关闭连接，导致“Connection reset by peer”错误

## 6. 验证API命中

如果我们希望验证mock的API是否被命中以及命中了多少次，我们可以通过以下方式执行WireMock.verify()方法。

```java
verify(exactly(1), postRequestedFor(urlEqualTo(api_url))
    .withHeader("Content-Type", "application/JSON"));
```

有很多方法可以验证命中计数，例如lessThan()、lessThanOrExactly()、exactly()、moreThanOrExactly()和moreThan()。

```java
verify(lessThan(5), anyRequestedFor(anyUrl()));
verify(lessThanOrExactly(5), anyRequestedFor(anyUrl()));
verify(exactly(5), anyRequestedFor(anyUrl()));
verify(moreThanOrExactly(5), anyRequestedFor(anyUrl()));
verify(moreThan(5), anyRequestedFor(anyUrl()));
```

## 7. 总结

这个WireMock教程可以帮助你通过mock外部REST API开始进行集成测试。它涵盖了初始化WireMockServer以及在需要时启动、停止或重置的各种方法。

我们学习了配置请求和响应存根、匹配API响应和验证API命中的基本和高级选项。我们还学习了在mock API中模拟各种成功、失败和错误情况。