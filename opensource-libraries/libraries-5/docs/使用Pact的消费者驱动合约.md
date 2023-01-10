## 1. 概述

在这篇简短的文章中，我们将研究消费者驱动合同的概念。

我们将通过使用[Pact](https://docs.pact.io/)库定义的契约来测试与外部 REST 服务的集成。该合同可以由客户定义，然后由提供商选择并用于开发其服务。

我们还将根据合同为客户端和提供商应用程序创建测试。

## 2. 什么是契约？

使用Pact ，我们可以以合同的形式(因此是库的名称)定义消费者对给定提供者(可以是 HTTP REST 服务)的期望。

我们将使用Pact提供的 DSL 来设置这个合约。定义后，我们可以使用基于定义的契约创建的模拟服务来测试消费者和提供者之间的交互。此外，我们将使用模拟客户端根据合同测试服务。

## 3.Maven依赖

要开始，我们需要将 Maven 依赖项添加到[pact-jvm-consumer-junit5_2.12](https://search.maven.org/search?q=g:au.com.dius AND a:pact-jvm-consumer-junit5_2.12)库：

```xml
<dependency>
    <groupId>au.com.dius</groupId>
    <artifactId>pact-jvm-consumer-junit5_2.12</artifactId>
    <version>3.6.3</version>
    <scope>test</scope>
</dependency>
```

## 4.定义合约

当我们想要使用Pact创建测试时，首先我们需要使用将要使用的提供者来注解我们的测试类：

```java
@PactTestFor(providerName = "test_provider", hostInterface="localhost")
public class PactConsumerDrivenContractUnitTest
```

我们正在传递将启动服务器模拟(从合同创建)的提供者名称和主机。

假设服务已经为它可以处理的两个 HTTP 方法定义了契约。

第一种方法是 GET 请求，它返回带有两个字段的 JSON。当请求成功时，它会返回 200 HTTP 响应代码和 JSON 的 C ontent-Type标头。

让我们使用Pact定义这样的合约。

我们需要使用@Pact注解并传递为其定义合同的消费者名称。在带注解的方法内部，我们可以定义我们的 GET 契约：

```java
@Pact(consumer = "test_consumer")
public RequestResponsePact createPact(PactDslWithProvider builder) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    return builder
      .given("test GET")
        .uponReceiving("GET REQUEST")
        .path("/pact")
        .method("GET")
      .willRespondWith()
        .status(200)
        .headers(headers)
        .body("{"condition": true, "name": "tom"}")
        (...)
}
```

使用Pact DSL，我们定义对于给定的 GET 请求，我们希望返回具有特定标头和正文的 200 响应。

我们合同的第二部分是 POST 方法。当客户端使用适当的 JSON 正文向路径/pact发送 POST 请求时，它会返回 201 HTTP 响应代码。

让我们用Pact定义这样的合约：

```java
(...)
.given("test POST")
.uponReceiving("POST REQUEST")
  .method("POST")
  .headers(headers)
  .body("{"name": "Michael"}")
  .path("/pact")
.willRespondWith()
  .status(201)
.toPact();
```

请注意，我们需要在合约末尾调用toPact()方法以返回RequestResponsePact的实例。

### 4.1. 产生的契约神器

默认情况下，Pact 文件将在target/pacts文件夹中生成。要自定义此路径，我们可以配置maven-surefire-plugin：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <systemPropertyVariables>
            <pact.rootDir>target/mypacts</pact.rootDir>
        </systemPropertyVariables>
    </configuration>
    ...
</plugin>
```

Maven 构建将在target/mypacts文件夹中生成一个名为test_consumer-test_provider.json的文件，其中包含请求和响应的结构：

```plaintext
{
    "provider": {
        "name": "test_provider"
    },
    "consumer": {
        "name": "test_consumer"
    },
    "interactions": [
        {
            "description": "GET REQUEST",
            "request": {
                "method": "GET",
                "path": "/"
            },
            "response": {
                "status": 200,
                "headers": {
                    "Content-Type": "application/json"
                },
                "body": {
                    "condition": true,
                    "name": "tom"
                }
            },
            "providerStates": [
                {
                    "name": "test GET"
                }
            ]
        },
        {
            "description": "POST REQUEST",
            ...
        }
    ],
    "metadata": {
        "pact-specification": {
            "version": "3.0.0"
        },
        "pact-jvm": {
            "version": "3.6.3"
        }
    }
}
```

## 5. 使用合约测试客户和提供者

现在我们有了合同，我们可以使用它为客户和提供者创建针对它的测试。

这些测试中的每一个都将使用基于合约的对应项的模拟，这意味着：

-   客户将使用模拟提供者
-   提供商将使用模拟客户端

实际上，测试是根据合同进行的。

### 5.1. 测试客户端

一旦我们定义了契约，我们就可以测试与将基于该契约创建的服务的交互。我们可以创建普通的 JUnit 测试，但我们需要记住将@PactTestFor注解放在测试的开头。

让我们为 GET 请求编写一个测试：

```java
@Test
@PactTestFor
public void givenGet_whenSendRequest_shouldReturn200WithProperHeaderAndBody() {
 
    // when
    ResponseEntity<String> response = new RestTemplate()
      .getForEntity(mockProvider.getUrl() + "/pact", String.class);

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getHeaders().get("Content-Type").contains("application/json")).isTrue();
    assertThat(response.getBody()).contains("condition", "true", "name", "tom");
}
```

@PactTestFor注解负责启动 HTTP 服务，可以放在测试类或测试方法上。在测试中，我们只需要发送 GET 请求并断言我们的响应符合约定即可。

让我们也为 POST 方法调用添加测试：

```java
HttpHeaders httpHeaders = new HttpHeaders();
httpHeaders.setContentType(MediaType.APPLICATION_JSON);
String jsonBody = "{"name": "Michael"}";

// when
ResponseEntity<String> postResponse = new RestTemplate()
  .exchange(
    mockProvider.getUrl() + "/create",
    HttpMethod.POST,
    new HttpEntity<>(jsonBody, httpHeaders), 
    String.class
);

//then
assertThat(postResponse.getStatusCode().value()).isEqualTo(201);
```

如我们所见，POST 请求的响应代码等于 201——与Pact合约中定义的完全相同。

当我们使用@PactTestFor()注解时，Pact库在我们的测试用例之前基于先前定义的契约启动 Web 服务器。

### 5.2. 测试提供者

我们合同验证的第二步是使用基于合同的模拟客户端为提供者创建测试。

我们的提供商实施将以 TDD 方式由该合同驱动。

对于我们的示例，我们将使用Spring BootREST API。

首先，要创建我们的 JUnit 测试，我们需要添加[pact-jvm-provider-junit5_2.12](https://search.maven.org/search?q=a:pact-jvm-provider-junit5_2.12)依赖项：

```xml
<dependency>
    <groupId>au.com.dius</groupId>
    <artifactId>pact-jvm-provider-junit5_2.12</artifactId>
    <version>3.6.3</version>
</dependency>
```

这允许我们创建一个 JUnit 测试，指定提供者名称和 Pact 工件的位置：

```java
@Provider("test_provider")
@PactFolder("pacts")
public class PactProviderLiveTest {
    //...
}
```

要使此配置生效，我们必须将test_consumer-test_provider.json文件放在REST 服务项目的 pacts 文件夹中。

接下来，为了使用 JUnit 5 编写 Pact 验证测试，我们需要使用带有@TestTemplate注解的PactVerificationInvocationContextProvider。我们需要将PactVerificationContext参数传递给它，我们将使用它来设置目标Spring Boot应用程序详细信息：

```java
private static ConfigurableWebApplicationContext application;

@TestTemplate
@ExtendWith(PactVerificationInvocationContextProvider.class)
void pactVerificationTestTemplate(PactVerificationContext context) {
    context.verifyInteraction();
}

@BeforeAll
public static void start() {
    application = (ConfigurableWebApplicationContext) SpringApplication.run(MainApplication.class);
}

@BeforeEach
void before(PactVerificationContext context) {
    context.setTarget(new HttpTestTarget("localhost", 8082, "/spring-rest"));
}
```

最后，我们将指定我们要测试的合约中的状态：

```java
@State("test GET")
public void toGetState() { }

@State("test POST")
public void toPostState() { }
```

运行这个 JUnit 类将为两个 GET 和 POST 请求执行两个测试。我们看一下日志：

```plaintext
Verifying a pact between test_consumer and test_provider
  Given test GET
  GET REQUEST
    returns a response which
      has status code 200 (OK)
      includes headers
        "Content-Type" with value "application/json" (OK)
      has a matching body (OK)

Verifying a pact between test_consumer and test_provider
  Given test POST
  POST REQUEST
    returns a response which
      has status code 201 (OK)
      has a matching body (OK)
```

请注意，我们没有在此处包含用于创建 REST 服务的代码。完整的服务和测试可以在[GitHub 项目](https://github.com/eugenp/tutorials/tree/master/libraries-5)中找到。

## 六. 总结

在这个快速教程中，我们了解了消费者驱动的合同。

我们使用Pact库创建了一个合约。一旦我们定义了契约，我们就能够根据契约测试客户端和服务，并断言它们符合规范。