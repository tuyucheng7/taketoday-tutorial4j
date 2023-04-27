## 1. 概述

在[之前的文章](https://rieckpil.de/testing-your-spring-resttemplate-with-restclienttest/)中，我们介绍了如何使用@RestClientTest测试Spring RestTemplate。

借助这个优雅的解决方案，你可以轻松测试使用RestTemplate的应用程序部分并mock HTTP响应。**不幸的是，这个测试设置不适用于Spring WebClient**。

似乎不会与WebClient的MockRestServiceServer集成，推荐的方法是使用[OkHttp](https://github.com/square/okhttp/tree/master/mockwebserver)的MockWebServer。

在这篇简短的文章中，我们将**探讨如何使用MockWebServer来测试使用Spring WebClient的部分应用程序**。

## 2. 包含MockWebServer应用程序设置的Spring WebClient

我们的示例项目是一个基本的Spring Boot应用程序。我们将包括Web和WebFlux启动器，因此，Spring Boot会为我们自动配置嵌入式Tomcat，同时我们能够使用Spring WebFlux中的功能，例如[WebClient](https://rieckpil.de/howto-use-spring-webclient-for-restful-communication/)。

除了Spring Boot Starter Test之外，该项目还包括来自OkHttp([Java HTTP客户端库](https://github.com/square/okhttp))的MockWebServer依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>mockwebserver</artifactId>
    <version>${mockwebserver.version}</version>
    <scope>test</scope>
</dependency>
```

**将MockWebServer的依赖版本与Spring Boot Starter Parent定义的OkHttp版本对齐非常重要**。默认情况下，Spring Boot Parent 2.3引用OkHttp客户端库的版本3.14.8。

在不覆盖OkHttp版本的情况下包含最新版本的MockWebServer会导致以下错误：

```shell
java.lang.NoClassDefFoundError: kotlin/TypeCastException

	at cn.tuyucheng.taketoday.UsersClientIntegrationTest.<clinit>(UsersClientTest.java:17)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
Caused by: java.lang.ClassNotFoundException: kotlin.TypeCastException
	at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:581)
	at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:178)
	at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:522)
	... 54 more
```

这就是为什么我们在pom.xml的properties部分中覆盖版本并将两者对齐：

```xml
<properties>
    <mockwebserver.version>4.7.2</mockwebserver.version>
    <okhttp3.version>4.7.2</okhttp3.version>
</properties>
```

最新版本的[MockWebServer](https://central.sonatype.com/artifact/com.squareup.okhttp3/mockwebserver/5.0.0-alpha.11)可以在Maven Central上找到。

## 3. Spring WebClient用法

Spring WebClient的以下用法对你来说应该很熟悉：

```java
@Component
public class UsersClient {

    private final WebClient webClient;

    public UsersClient(WebClient.Builder builder, @Value("${clients.users.url}") String usersBaseUrl) {
        this.webClient = builder.baseUrl(usersBaseUrl).build();
    }

    public JsonNode getUserById(Long id) {
        return this.webClient
                .get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
```

此客户端注入自动配置的WebClient.Builder，并使用正确的基本URL配置WebClient。

现在，我们可以将UsersClient注入到我们的应用程序中，并发出HTTP请求来获取用户数据。

为了在生产中使用它，我们可以在application.properties文件中配置实际的URL：

```properties
clients.users.url=https://jsonplaceholder.typicode.com/
```

使此URL可配置非常重要，因为这将帮助我们测试应用程序的这一部分。此外，你还可以从中受益，因为你可以为阶段指定不同的URL。你的沙盒环境可能会使用外部服务的开发端点。

让我们在UsersClient中添加第二个方法，以便稍后测试不同的场景：

```java
public JsonNode createNewUser(JsonNode payload) {
    ClientResponse clientResponse = this.webClient
        .post()
        .uri("/users")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .bodyValue(payload)
        .exchange()
        .block();
    
    if (clientResponse.statusCode().equals(HttpStatus.CREATED)) {
        return clientResponse.bodyToMono(JsonNode.class).block();
    } else {
        throw new RuntimeException("Unable to create new user!");
    }
}
```

此方法确保在验证响应代码时成功创建新用户。如果外部系统返回201(CREATED)以外的任何其他HTTP状态代码，我们将抛出异常。

如果你想了解更多关于Spring WebClient的详细信息，请参考[本文](https://rieckpil.de/howto-use-spring-webclient-for-restful-communication/)。

## 4. 使用MockWebServer的第一个JUnit 5测试

让我们使用MockWebServer编写第一个测试来验证Spring WebClient是否可以检索用户数据。

MockWebServer生成的服务器足够轻量级，我们可以为每个测试方法创建一个服务器。这也确保我们不会在之前的测试中mock HTTP响应而产生任何副作用：

```java
class UsersClientIntegrationTest {

    private MockWebServer mockWebServer;
    private UsersClient usersClient;

    @BeforeEach
    public void setup() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        this.usersClient = new UsersClient(WebClient.builder(), mockWebServer.url("/").toString());
    }

    @Test
    public void testGetUserById() throws InterruptedException {
        MockResponse mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"id\": 1, \"name\":\"duke\"}")
                .throttleBody(16, 5, TimeUnit.SECONDS);
        mockWebServer.enqueue(mockResponse);

        JsonNode result = usersClient.getUserById(1L);

        assertEquals(1, result.get("id").asInt());
        assertEquals("duke", result.get("name").asText());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/users/1", request.getPath());
    }
}
```

**JUnit 5的@BeforeEach生命周期方法帮助我们为实际测试准备好一切**。除了实例化和启动MockWebServer，我们还将URL传递给我们的UsersClient实例。

对于我们启动的每个新MockWebServer，**为我们的被测类(UsersClientIntegrationTest)创建一个新实例很重要，因为mock服务器监听随机的临时端口**。

在调用我们想要测试的UsersClient方法之前，我们必须准备响应。因此，我们可以构造一个符合我们需求的MockResponse(正文、HTTP标头、响应代码)并使用mockWebServer.enqueue()对其进行排队。

与[WireMock](https://rieckpil.de/spring-boot-integration-tests-with-wiremock-and-junit-5/)等其他HTTP响应mock解决方案相比，你可以使用MockWebServer对响应进行排队。本地服务器将按照你对它们进行排队的确切顺序进行响应。这就是为什么我们不指定客户端将访问的实际路径("/users/1")并依赖于排队响应的顺序。

作为替代方案，你也可以使用Dispatcher，我们将在下一节中介绍。

最后，MockWebServer提供了一个解决方案来验证我们的应用程序在测试期间发出的HTTP请求。因此，我们可以请求RecordedRequest的实例并断言请求的几个参数(例如路径、标头、有效负载)。**如果在测试期间有多个请求，只需调用takeRequest()即可按照请求到达服务器的顺序返回请求**。

## 5. 使用Spring WebClient和MockWebServer进行进一步测试

通过第二次测试，我们可以确保我们的客户端能够创建新用户：

```java
@Test
public void testCreatingUsers() {
    MockResponse mockResponse = new MockResponse()
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .setBody("{\"id\": 1, \"name\":\"duke\"}")
        .throttleBody(16, 5, TimeUnit.SECONDS)
        .setResponseCode(201);
    
    mockWebServer.enqueue(mockResponse);
    JsonNode result = usersClient.createNewUser(new ObjectMapper().createObjectNode().put("name", "duke"));
    
    assertEquals(1, result.get("id").asInt());
    assertEquals("duke", result.get("name").asText());
}
```

设置类似于第一个测试，但这里我们修改服务器的响应代码(默认为200)。

通过此测试，还包含了MockWebServer另一个功能的演示：节流响应。当你想要测试你的应用程序在外部系统缓慢(或过载)的情况下的行为时，这非常有用。

让我们添加第三个测试来验证我们的自定义逻辑(验证响应代码201)是否正常工作。

```java
@Test
public void testCreatingUsersWithNon201ResponseCode() {
    MockResponse mockResponse = new MockResponse()
        .setResponseCode(204);
    mockWebServer.enqueue(mockResponse);

    assertThrows(RuntimeException.class, () -> usersClient.createNewUser(new ObjectMapper().createObjectNode().put("name", "duke")));
}
```

## 6. 使用MockWebServer定义不同请求的响应

如果mock响应的顺序不适合你要测试的用例，你可以使用所谓的Dispatcher。

这允许我们根据请求的任何属性(标头、路径、正文等)返回响应。对于大多数情况，URL路径是区分响应的最有用方式：

```java
@Test
public void testMultipleResponseCodes() {
	final Dispatcher dispatcher = new Dispatcher() {
		@Override
		public MockResponse dispatch(RecordedRequest request) {
			return switch (request.getPath()) {
				case "/users/1" -> new MockResponse().setResponseCode(200);
				case "/users/2" -> new MockResponse().setResponseCode(500);
				case "/users/3" -> new MockResponse().setResponseCode(200).setBody("{\"id\": 1, \"name\":\"duke\"}");
				default -> new MockResponse().setResponseCode(404);
			};
		}
	};
    
	mockWebServer.setDispatcher(dispatcher);
	assertThrows(WebClientResponseException.class, () -> usersClient.getUserById(2L));
	assertThrows(WebClientResponseException.class, () -> usersClient.getUserById(4L));
}
```

在这里，我们为不同的users端点返回不同的响应代码。与使用enqueue()准备响应的其他测试相比，我们在这里使用setDispatcher()并且不排队任何东西。

## 7. 总结

与我们用于[测试RestClient](https://rieckpil.de/testing-your-spring-resttemplate-with-restclienttest/)的解决方案相比，设置成本可以忽略不计。

启动MockWebServer速度非常快，直观的API使集成和使用变得简单明了。与[WireMock](https://rieckpil.de/spring-boot-integration-tests-with-wiremock-and-junit-5/)相比，功能集更基本，但对于大多数用例来说已经足够好了。