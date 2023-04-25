## 1. 概述

在这篇简短的文章中，我们将研究消费者驱动契约的概念。

我们将通过使用[Pact](https://docs.pact.io/)库定义的契约来测试与外部REST服务的集成。**该契约可以由客户定义，然后由提供者选择并用于开发其服务**。

我们还将根据契约为客户端和提供者应用程序创建测试。

## 2. 什么是契约？

**使用Pact，我们可以以契约的形式(库的名称因此得名)定义消费者对给定提供者(可以是HTTP REST服务)的期望**。

我们将使用Pact提供的DSL来设置这个契约。定义后，我们可以使用基于定义的契约创建的mock服务来测试消费者和提供者之间的交互。此外，我们将使用mock客户端根据契约测试服务。

## 3. Maven依赖

首先，我们需要将[pact-jvm-consumer-junit5_2.12](https://search.maven.org/search?q=g:au.com.diusANDa:pact-jvm-consumer-junit5_2.12)库Maven依赖项添加到POM中：

```xml
<dependency>
    <groupId>au.com.dius</groupId>
    <artifactId>pact-jvm-consumer-junit5_2.12</artifactId>
    <version>3.6.3</version>
    <scope>test</scope>
</dependency>
```

## 4. 定义契约

当我们想要使用Pact创建测试时，首先我们需要使用将要使用的提供者来标注我们的测试类：

```java
@PactTestFor(providerName = "test_provider", hostInterface="localhost")
public class PactConsumerDrivenContractUnitTest
```

我们传递将启动服务器mock(从契约创建)的提供者名称和主机。

假设服务已经为它可以处理的两个HTTP方法定义了契约。

第一种方法是GET请求，它返回带有两个字段的JSON。当请求成功时，它会返回200 HTTP响应代码和JSON的Content-Type标头。

让我们使用Pact定义这样的契约。

**我们需要使用@Pact注解并传递为其定义契约的消费者名称**。在带注解的方法内部，我们可以定义我们的GET契约：

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
            .body("{\"condition\": true, \"name\": \"tom\"}")
            (...)
}
```

使用Pact DSL，我们定义对于给定的GET请求，我们希望返回具有特定标头和正文的200响应。

我们契约的第二部分是POST方法。当客户端使用正确的JSON正文向路径/pact发送POST请求时，它会返回201 HTTP响应代码。

让我们用Pact定义这样的契约：

```java
(...)
.given("test POST")
.uponReceiving("POST REQUEST")
    .method("POST")
    .headers(headers)
    .body("{\"name\": \"Michael\"}")
    .path("/pact")
.willRespondWith()
    .status(201)
.toPact();
```

请注意，我们需要在契约末尾调用toPact()方法以返回RequestResponsePact的实例。

### 4.1 生成的契约工件

默认情况下，Pact文件将在target/pacts文件夹中生成。要自定义此路径，我们可以配置maven-surefire-plugin：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <systemPropertyVariables>
            <pact.rootDir>target/mypacts</pact.rootDir>
        </systemPropertyVariables>
    </configuration>
    <!--...-->
</plugin>
```

Maven构建将在target/mypacts文件夹中生成一个名为test_consumer-test_provider.json的文件，其中包含请求和响应的结构：

```json
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

## 5. 使用契约测试客户端和提供者

现在我们有了契约，我们可以使用它为客户端和提供者创建针对它的测试。

这些测试中的每一个都将使用基于契约的对应项的mock，这意味着：

-   客户将使用mock提供者
-   提供者将使用mock客户端

**实际上，测试是根据契约进行的**。

### 5.1 测试客户端

定义契约后，我们就可以测试与将基于该契约创建的服务的交互。**我们可以创建普通的JUnit测试，但我们需要记住将@PactTestFor注解放在测试的开头**。

让我们为GET请求编写一个测试：

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

**@PactTestFor注解负责启动HTTP服务，可以放在测试类或测试方法上**。在测试中，我们只需要发送GET请求并断言我们的响应符合契约即可。

让我们也为POST方法调用添加测试：

```java
HttpHeaders httpHeaders = new HttpHeaders();
httpHeaders.setContentType(MediaType.APPLICATION_JSON);
String jsonBody = "{\"name\": \"Michael\"}";

// when
ResponseEntity<String> postResponse = new RestTemplate()
    .exchange(mockProvider.getUrl() + "/create", HttpMethod.POST, new HttpEntity<>(jsonBody, httpHeaders), String.class);

// then
assertThat(postResponse.getStatusCode().value()).isEqualTo(201);
```

正如我们所看到的，POST请求的响应代码等于201-与Pact契约中定义的完全相同。

当我们使用@PactTestFor()注解时，Pact库在我们的测试用例之前基于先前定义的契约启动Web服务器。

### 5.2 测试提供者

契约验证的第二步是使用基于契约的mock客户端为提供者创建测试。

我们的提供者实施将以TDD方式由该契约驱动。

对于我们的示例，我们将使用Spring Boot REST API。

首先，要创建我们的JUnit测试，我们需要添加[pact-jvm-provider-junit5_2.12](https://search.maven.org/search?q=a:pact-jvm-provider-junit5_2.12)依赖项：

```xml
<dependency>
    <groupId>au.com.dius</groupId>
    <artifactId>pact-jvm-provider-junit5_2.12</artifactId>
    <version>3.6.3</version>
</dependency>
```

**这允许我们创建一个JUnit测试，指定提供者名称和Pact工件的位置**：

```java
@Provider("test_provider")
@PactFolder("pacts")
public class PactProviderLiveTest {
    // ...
}
```

要使此配置生效，我们必须将test_consumer-test_provider.json文件放在REST服务项目的pacts文件夹中。

接下来，为了使用JUnit 5编写Pact验证测试，我们需要将PactVerificationInvocationContextProvider与@TestTemplate注解一起使用。我们需要向它传递PactVerificationContext参数，我们将使用该参数来设置目标Spring Boot应用程序的详细信息：

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

最后，我们在契约中指定要测试的状态：

```java
@State("test GET")
public void toGetState() { }

@State("test POST")
public void toPostState() { }
```

运行这个JUnit类将为两个GET和POST请求执行两个测试，让我们看一下日志：

```text
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

请注意，我们没有在此处包含用于创建REST服务的代码。完整的服务和测试可以在[GitHub项目](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/software.test/pact)中找到。

## 6. 总结

在这个快速教程中，我们了解了消费者驱动的契约。

我们使用Pact库创建了一个契约。一旦我们定义了契约，我们就能够根据契约测试客户端和服务，并断言它们符合规范。