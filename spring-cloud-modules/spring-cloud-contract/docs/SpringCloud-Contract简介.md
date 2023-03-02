## 1. 简介

**[Spring Cloud Contract](https://cloud.spring.io/spring-cloud-contract/)是一个项目，简单来说就是帮助我们编写[Consumer-Driven Contracts(CDC)](https://martinfowler.com/articles/consumerDrivenContracts.html)**。

这确保了分布式系统中生产者和消费者之间的契约-用于基于HTTP和基于消息的交互。

在这篇快速文章中，我们将探讨如何通过HTTP交互为Spring Cloud Contract编写生产者和消费者端测试用例。

## 2. 生产者-服务器端

我们将以EvenOddController的形式编写一个生产者端CDC-它只是告诉数字参数是偶数还是奇数：

```java
@RestController
public class EvenOddController {

    @GetMapping("/validate/prime-number")
    public String isNumberPrime(@RequestParam("number") Integer number) {
        return Integer.parseInt(number) % 2 == 0 ? "Even" : "Odd";
    }
}
```

### 2.1 Maven依赖项

对于我们的生产者端，我们需要[spring-cloud-starter-contract-verifier](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-contract-verifier/4.0.1)依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-contract-verifier</artifactId>
    <version>2.1.1.RELEASE</version>
    <scope>test</scope>
</dependency>
```

我们需要使用我们的基础测试类的名称配置[spring-cloud-contract-maven-plugin](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-contract-maven-plugin/4.0.1)，我们将在下一节中进行描述：

```xml
<plugin>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-contract-maven-plugin</artifactId>
    <version>2.1.1.RELEASE</version>
    <extensions>true</extensions>
    <configuration>
        <baseClassForTests>
            cn.tuyucheng.taketoday.spring.cloud.springcloudcontractproducer.BaseTestClass
        </baseClassForTests>
    </configuration>
</plugin>
```

### 2.2 生产者端设置

我们需要在加载Spring上下文的测试包中添加一个基类：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext
@AutoConfigureMessageVerifier
public class BaseTestClass {

    @Autowired
    private EvenOddController evenOddController;

    @Before
    public void setup() {
        StandaloneMockMvcBuilder standaloneMockMvcBuilder = MockMvcBuilders.standaloneSetup(evenOddController);
        RestAssuredMockMvc.standaloneSetup(standaloneMockMvcBuilder);
    }
}
```

**在/src/test/resources/contracts/包中，我们将添加测试存根**，例如文件shouldReturnEvenWhenRequestParamIsEven.groovy中的这个：

```groovy
import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should return even when number input is even"
    request{
        method GET()
        url("/validate/prime-number") {
            queryParameters {
                parameter("number", "2")
            }
        }
    }
    response {
        body("Even")
        status 200
    }
}
```

**当我们运行构建时，插件会自动生成一个名为ContractVerifierTest的测试类，该类扩展了我们的BaseTestClass并将其放在/target/generated-test-sources/contracts/中**。

测试方法的名称派生自前缀“validate_”与我们的Groovy测试存根的名称相拼接。对于上述Groovy文件，生成的方法名称将为“validate_shouldReturnEvenWhenRequestParamIsEven”。

让我们看看这个自动生成的测试类：

```java
public class ContractVerifierTest extends BaseTestClass {

    @Test
    public void validate_shouldReturnEvenWhenRequestParamIsEven() throws Exception {
        // given:
        MockMvcRequestSpecification request = given();

        // when:
        ResponseOptions response = given().spec(request)
              .queryParam("number", "2")
              .get("/validate/prime-number");

        // then:
        assertThat(response.statusCode()).isEqualTo(200);

        // and:
        String responseBody = response.getBody().asString();
        assertThat(responseBody).isEqualTo("Even");
    }
}
```

**该构建还将在我们的本地Maven仓库中添加存根jar，以便我们的消费者可以使用它**。

存根将存在于stubs/mapping/下的输出文件夹中。

## 3. 消费者-客户端

**我们CDC的消费者端会消费生产者端通过HTTP交互生成的存根来维护合约，因此生产者端的任何更改都会破坏合约**。

我们将添加BasicMathController，它将发出HTTP请求以从生成的存根中获取响应：

```java
@RestController
public class BasicMathController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/calculate")
    public String checkOddAndEven(@RequestParam("number") Integer number) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");

        ResponseEntity<String> responseEntity = restTemplate.exchange(
              "http://localhost:8090/validate/prime-number?number=" + number,
              HttpMethod.GET,
              new HttpEntity<>(httpHeaders),
              String.class);

        return responseEntity.getBody();
    }
}
```

### 3.1 Maven依赖项

对于我们的消费者，我们需要添加[spring-cloud-contract-wiremock](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-contract-wiremock/4.0.1)和[spring-cloud-contract-stub-runner](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-contract-stub-runner/4.0.1)依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-contract-wiremock</artifactId>
    <version>2.1.1.RELEASE</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-contract-stub-runner</artifactId>
    <version>2.1.1.RELEASE</version>
    <scope>test</scope>
</dependency>
```

### 3.2 消费者端设置

现在是时候配置我们的存根运行器了，它将通知我们的消费者我们本地Maven仓库中的可用存根：

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@AutoConfigureStubRunner(
      stubsMode = StubRunnerProperties.StubsMode.LOCAL,
      ids = "cn.tuyucheng.taketoday.spring.cloud:spring-cloud-contract-producer:+:stubs:8090")
public class BasicMathControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void given_WhenPassEvenNumberInQueryParam_ThenReturnEven()
          throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/calculate?number=2")
                    .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(content().string("Even"));
    }
}
```

请注意，@AutoConfigureStubRunner注解的ids属性指定：

-   cn.tuyucheng.taketoday.spring.cloud：我们工件的groupId
-   spring-cloud-contract-producer：生产者存根jar的artifactId
-   8090：生成的存根将在其上运行的端口

## 4. 当契约被破坏

如果我们在生产者方面进行任何直接影响契约的更改而不更新消费者端，**这可能会导致契约失败**。

例如，假设我们要在生产者端将EvenOddController请求URI更改为/validate/change/prime-number。

如果我们未能将此更改通知我们的消费者，消费者仍会将其请求发送到/validate/prime-number URI，并且消费者端测试用例将抛出org.springframework.web.client.HttpClientErrorException: 404 Not Found。

## 5. 总结

我们已经了解了Spring Cloud Contract如何帮助我们维护服务消费者和生产者之间的契约，这样我们就可以推出新的代码，而不必担心破坏契约。