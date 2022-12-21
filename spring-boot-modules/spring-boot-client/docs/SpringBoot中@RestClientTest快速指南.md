## 1. 简介

本文是对@RestClientTest注解的快速介绍。

新注解有助于简化和加快Spring应用程序中REST客户端的测试。

## 2. Spring Boot Pre-1.4中的REST Client支持

Spring Boot是一个方便的框架，它提供了许多具有典型设置的自动配置的Spring bean，使你可以减少对Spring应用程序配置的关注，而将更多精力放在代码和业务逻辑上。

但是在1.3版本中，当我们想要创建或测试REST服务客户端时，我们并没有得到很多帮助，它对REST客户端的支持不是很深入。

要为REST API创建客户端-通常使用RestTemplate实例。通常必须在使用前对其进行配置，并且其配置可能会有所不同，因此Spring Boot不提供任何通用配置的RestTemplate bean。

测试REST客户端也是如此。在Spring Boot 1.4.0之前，测试Spring REST客户端的过程与测试任何其他基于Spring的应用程序没有太大区别。你将创建一个MockRestServiceServer实例，将其绑定到测试中的RestTemplate实例，并为其提供对请求的模拟响应，如下所示：

```java
RestTemplate restTemplate = new RestTemplate();

MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();
mockServer.expect(requestTo("/greeting"))
      .andRespond(withSuccess());

// Test code that uses the above RestTemplate ...

mockServer.verify();
```

你还必须初始化Spring容器并确保仅将需要的组件加载到上下文中，以加快上下文加载时间(从而加快测试执行时间)。

## 3. Spring Boot 1.4+中新的REST客户端特性

在Spring Boot 1.4中，团队为简化和加快REST客户端的创建和测试做出了扎实的努力。

因此，让我们看看新功能。

### 3.1 将Spring Boot添加到你的项目中

首先，你需要确保你的项目使用的是Spring Boot 1.4.x或更高版本：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <relativePath/> <!-- lookup parent from repository -->
</parent>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

最新发布版本可在[此处](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-parent)找到。

### 3.2 RestTemplateBuilder

Spring Boot带来了自动配置的RestTemplateBuilder来简化RestTemplates的创建，以及匹配的@RestClientTest注解来测试使用RestTemplateBuilder构建的客户端。以下是如何创建一个简单的REST客户端，并为你自动注入RestTemplateBuilder：

```java
@Service
public class DetailsServiceClient {

    private final RestTemplate restTemplate;

    public DetailsServiceClient(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public Details getUserDetails(String name) {
        return restTemplate.getForObject("/{name}/details", Details.class, name);
    }
}
```

请注意，我们没有明确地将RestTemplateBuilder实例注入到构造函数，这要归功于一个名为隐式构造函数注入的新Spring特性，[本文]()对此进行了讨论。

RestTemplateBuilder提供了方便的方法来注册消息转换器、错误处理程序、URI模板处理程序、基本授权，还可以使用你需要的任何其他自定义程序。

### 3.3 @RestClientTest

为了测试这样一个用RestTemplateBuilder构建的REST客户端，你可以使用用@RestClientTest标注的SpringRunner执行的测试类。此注解禁用完全自动配置，并且仅应用与REST客户端测试相关的配置，即Jackson或GSON自动配置和@JsonComponent beans，但不适用于常规@Component beans。

@RestClientTest确保自动配置Jackson和GSON支持，并将预配置的RestTemplateBuilder和MockRestServiceServer实例添加到上下文中。被测bean由@RestClientTest注解的value或components属性指定：

```java
@ExtendWith(SpringExtension.class)
@RestClientTest({DetailsServiceClient.class})
class DetailsServiceClientIntegrationTest {

    @Autowired
    private DetailsServiceClient client;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        String detailsString = objectMapper.writeValueAsString(new Details("John Smith", "john"));
        this.server.expect(requestTo("/john/details"))
              .andRespond(withSuccess(detailsString, MediaType.APPLICATION_JSON));
    }

    @Test
    void whenCallingGetUserDetails_thenClientExecutesCorrectCall() throws Exception {
        Details details = this.client.getUserDetails("john");

        assertThat(details.getLogin()).isEqualTo("john");
        assertThat(details.getName()).isEqualTo("John Smith");
    }
}
```

首先，我们需要通过添加@ExtendWith(SpringExtension.class)注解来确保此测试使用SpringExtension运行。

那么，有什么新变化呢？

首先-@RestClientTest注解允许我们指定被测试的确切服务，在我们的例子中它是DetailsServiceClient类。此服务将加载到测试上下文中，而其他所有内容都将被过滤掉。

这允许我们在测试中自动装配DetailsServiceClient实例，并将其他所有内容留在测试之外，从而加快上下文的加载。

其次-由于MockRestServiceServer实例也配置为@RestClientTest注解测试(并为我们绑定到DetailsServiceClient实例)，我们可以简单地注入它并使用。

最后-对@RestClientTest的JSON支持允许我们注入Jackson的ObjectMapper实例来准备MockRestServiceServer的mock答案值。

剩下要做的就是执行对我们服务的调用并验证结果。

## 4. 总结

在本文中，我们讨论了新的@RestClientTest注解，它允许轻松快速地测试使用Spring构建的REST客户端。