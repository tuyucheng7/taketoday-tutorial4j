## 1. 概述

如今，我们希望在我们的大部分服务中调用REST API。Spring提供了一些构建REST客户端的选项，**推荐使用WebClient**。

在本快速教程中，我们将学习如何**对使用WebClient调用API的服务进行单元测试**。

## 2. Mock

我们在测试中有两个主要的Mock选项：

-   使用[Mockito](https://www.baeldung.com/mockito-series)模仿WebClient的行为
-   实际使用WebClient，但使用[MockWebServer(okhttp)](https://github.com/square/okhttp/tree/master/mockwebserver) Mock它调用的服务

## 3. 使用Mockito

[Mockito](https://www.baeldung.com/mockito-series)是最常见的Java Mock库。它擅长为方法调用提供预定义的响应，但在Mock流式的API时，事情变得具有挑战性。这是因为在流式的API中，许多对象在调用代码和Mock代码之间传递。

例如，假设我们有一个带有getEmployeeById方法的EmployeeService类，使用WebClient通过HTTP获取数据：

```java
public class EmployeeService {

    public EmployeeService(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }
    public Mono<Employee> getEmployeeById(Integer employeeId) {
        return webClient
              .get()
              .uri("http://localhost:8080/employee/{id}", employeeId)
              .retrieve()
              .bodyToMono(Employee.class);
    }
}
```

我们可以使用Mockito来Mock这个：

```java
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Test
    void givenEmployeeId_whenGetEmployeeById_thenReturnEmployee() {
        Integer employeeId = 100;
        Employee mockEmployee = new Employee(100, "Adam", "Sandler", 32, Role.LEAD_ENGINEER);
        when(webClientMock.get())
              .thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriMock.uri("/employee/{id}", employeeId))
              .thenReturn(requestHeadersSpecMock);
        when(requestHeadersMock.retrieve())
              .thenReturn(responseSpecMock);
        when(responseMock.bodyToMono(Employee.class))
              .thenReturn(Mono.just(mockEmployee));

        Mono<Employee> employeeMono = employeeService.getEmployeeById(employeeId);

        StepVerifier.create(employeeMono)
              .expectNextMatches(employee -> employee.getRole()
                    .equals(Role.LEAD_ENGINEER))
              .verifyComplete();
    }
}
```

如我们所见，我们需要为链中的每个调用提供一个不同的Mock对象，需要四个不同的when/thenReturn调用。**这是冗长和繁琐的**。它还要求我们知道我们的服务究竟如何使用WebClient的实现细节，这使得这是一种脆弱的测试方式。

那么我们如何才能为WebClient编写更好的测试呢？

## 4. 使用MockWebServer

[MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)由Square团队构建，是一个可以接收和响应HTTP请求的小型Web服务器。

**从我们的测试用例与MockWebServer交互允许我们的代码使用对本地端点的真实HTTP调用**。我们获得了测试预期HTTP交互的好处，并且没有Mock复杂的流式客户端的挑战。

**[Spring团队推荐](https://github.com/spring-projects/spring-framework/issues/19852#issuecomment-453452354)使用MockWebServer来编写集成测试**。

### 4.1 MockWebServer依赖项

要使用MockWebServer，我们需要将[okhttp](https://central.sonatype.com/artifact/com.squareup.okhttp3/okhttp/5.0.0-alpha.11)和[mockwebserver](https://central.sonatype.com/artifact/com.squareup.okhttp3/mockwebserver/5.0.0-alpha.11)的Maven依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.0.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>mockwebserver</artifactId>
    <version>4.0.1</version>
    <scope>test</scope>
</dependency>
```

### 4.2 将MockWebServer添加到我们的测试中

让我们用MockWebServer测试我们的EmployeeService：

```java
public class EmployeeServiceMockWebServerTest {

    public static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }
}
```

在上面的JUnit测试类中，setUp和tearDown方法负责创建和关闭MockWebServer。

下一步是**将实际REST服务调用的端口映射到MockWebServer的端口**：

```java
@BeforeEach
void initialize() {
    String baseUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
    employeeService = new EmployeeService(baseUrl);
}
```

现在是时候创建一个stub，以便MockWebServer可以响应HttpRequest。

### 4.3 Stubbing响应

让我们使用MockWebServer方便的入队方法在webserver上对测试响应进行排队：

```java
@Test
void getEmployeeById() throws Exception {
    Employee mockEmployee = new Employee(100, "Adam", "Sandler", 32, Role.LEAD_ENGINEER);
    mockBackEnd.enqueue(new MockResponse()
      	.setBody(objectMapper.writeValueAsString(mockEmployee))
      	.addHeader("Content-Type", "application/json"));

    Mono<Employee> employeeMono = employeeService.getEmployeeById(100);

    StepVerifier.create(employeeMono)
        .expectNextMatches(employee -> employee.getRole()
            .equals(Role.LEAD_ENGINEER))
        .verifyComplete();
}
```

当从我们的EmployeeService类中的getEmployeeById(Integer employeeId)方法进行实际的API调用时，**MockWebServer将使用排队的stub进行响应**。

### 4.4 检查请求

我们可能还想确保向MockWebServer发送了正确的HttpRequest。

MockWebServer有一个名为takeRequest的便捷方法，它返回RecordedRequest的一个实例：

```java
RecordedRequest recordedRequest = mockBackEnd.takeRequest();
 
assertEquals("GET", recordedRequest.getMethod());
assertEquals("/employee/100", recordedRequest.getPath());
```

使用RecordedRequest，我们可以验证收到的HttpRequest以确保我们的WebClient正确发送它。

## 5. 总结

在本文中，我们演示了可用于**Mock基于WebClient的REST客户端**代码的两个主要选项。

虽然Mockito有效，并且对于简单示例来说可能是一个不错的选择，但推荐的方法是使用MockWebServer。