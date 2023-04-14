## 1. 概述

在本教程中，我们将**重点介绍使用实时集成测试(使用JSON负载)测试REST API的基本原则和机制**。

我们的主要目标是介绍测试API的基本正确性，我们将使用最新版本的[GitHub REST API](https://docs.github.com/en/rest)作为示例。

对于内部应用程序，这种测试通常作为持续集成过程的最后一步运行，在部署后使用REST API。

测试REST资源时，通常有一些测试应关注的正交职责：

-   HTTP**响应代码**
-   响应中的其他HTTP**标头**
-   **有效负载**(JSON、XML)

**每个测试应该只关注单个职责并包含单个断言**。专注于清晰的分离总是有好处的，但是在进行这种黑盒测试时，这一点更为重要，因为一般趋势是在一开始就编写复杂的测试场景。

集成测试的另一个重要方面是遵守单级抽象原则；我们应该在高层次的测试中编写逻辑。创建请求、向服务器发送HTTP请求、处理IO等细节不应内联完成，而应通过实用程序方法完成。

## 延伸阅读

### [Spring中的集成测试](https://www.baeldung.com/integration-testing-in-spring)

为Spring Web应用程序编写集成测试的快速指南。

[阅读更多](https://www.baeldung.com/integration-testing-in-spring)→

### [在Spring Boot中进行测试](https://www.baeldung.com/spring-boot-testing)

了解Spring Boot如何支持测试，以高效地编写单元测试。

[阅读更多](https://www.baeldung.com/spring-boot-testing)→

### [REST-Assured指南](https://www.baeldung.com/rest-assured-tutorial)

探索REST-Assured的基础知识-一个简化REST API测试和验证的库。

[阅读更多](https://www.baeldung.com/rest-assured-tutorial)→

## 2. 测试状态码

```java
@Test
public void givenUserDoesNotExists_whenUserInfoIsRetrieved_then404IsReceived() throws ClientProtocolException, IOException {
    // Given
    String name = RandomStringUtils.randomAlphabetic(8);
    HttpUriRequest request = new HttpGet("https://api.github.com/users/" + name);

    // When
    HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);

    // Then
    assertThat(httpResponse.getStatusLine().getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));
}
```

这是一个相当简单的测试。**它验证基本的快乐路径是否正常工作**，而不会给测试套件增加太多的复杂性。

如果出于某种原因它失败了，那么在我们修复它之前，我们不需要查看此URL的任何其他测试。

## 3. 测试媒体类型

```java
@Test
public void 
givenRequestWithNoAcceptHeader_whenRequestIsExecuted_thenDefaultResponseContentTypeIsJson() throws ClientProtocolException, IOException {
   // Given
   String jsonMimeType = "application/json";
   HttpUriRequest request = new HttpGet("https://api.github.com/users/tuyucheng");

   // When
   HttpResponse response = HttpClientBuilder.create().build().execute(request);

   // Then
   String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
   assertEquals(jsonMimeType, mimeType);
}
```

**这可确保响应实际包含JSON数据**。

正如我们所看到的，**我们遵循测试的逻辑进展**。首先是响应状态代码(以确保请求正常)，然后是响应的媒体类型。只有在下一次测试中，我们才会查看实际的JSON负载。

## 4. 测试JSON负载

```java
@Test
public void givenUserExists_whenUserInformationIsRetrieved_thenRetrievedResourceIsCorrect() throws ClientProtocolException, IOException {
    // Given
    HttpUriRequest request = new HttpGet("https://api.github.com/users/tuyucheng");

    // When
    HttpResponse response = HttpClientBuilder.create().build().execute(request);

    // Then
    GitHubUser resource = RetrieveUtil.retrieveResourceFromResponse(response, GitHubUser.class);
    assertThat("tuyucheng", Matchers.is(resource.getLogin()));
}
```

在这种情况下，GitHub资源的默认表示形式是JSON，但通常，响应的Content-Type标头应与请求的Accept标头一起进行测试。客户端通过Accept请求特定类型的表示，服务器应该遵循这种表示。

## 5. 测试实用程序

我们将使用Jackson 2将原始JSON字符串解组为类型安全的Java实体：

```java
public class GitHubUser {
    private String login;

    // standard getters and setters
}
```

我们只使用一个简单的实用程序来保持测试的干净、可读性和高度抽象：

```java
public static <T> T retrieveResourceFromResponse(HttpResponse response, Class<T> clazz) throws IOException {
 
    String jsonFromResponse = EntityUtils.toString(response.getEntity());
    ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper.readValue(jsonFromResponse, clazz);
}
```

请注意，Jackson忽略了GitHub API发送给我们的[未知属性](https://www.baeldung.com/jackson-deserialize-json-unknown-properties)。这仅仅是因为GitHub上用户资源的表示非常复杂，我们在这里不需要任何这些信息。

## 6. 依赖

实用程序和测试使用以下库，所有这些都在Maven Central可用：

-   [HTTPClient](https://hc.apache.org/httpcomponents-client-ga/index.html)
-   [Jackson 2](https://github.com/FasterXML/jackson)
-   [Hamcrest](https://code.google.com/archive/p/hamcrest/)(可选)

## 7. 总结

这只是完整集成测试套件的一部分。这些测试侧重于确保REST API的基本正确性，而不涉及更复杂的场景。

例如，我们没有涵盖以下内容：API的可发现性、同一资源的不同表示形式的使用等。