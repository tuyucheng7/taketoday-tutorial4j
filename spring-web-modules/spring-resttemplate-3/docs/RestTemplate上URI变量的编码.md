## 一、概述

在本教程中，我们将学习如何在 Spring 的*[RestTemplate](https://www.baeldung.com/rest-template)*上对 URI 变量进行编码。

我们面临的常见编码问题之一是**当我们有一个包含加号 ( \*+\* )**的 URI 变量时。例如，如果我们有一个值为 *http://localhost:8080/api/v1/plus+sign 的*URI 变量，**加号将被编码为空格，这可能会导致意外的服务器响应**。

让我们看看解决这个问题的几种方法。

## 2.项目设置

我们将创建一个使用*RestTemplate*调用 API 的小项目。

### 2.1. Spring Web 依赖

让我们从将 [Spring Web Starter](https://search.maven.org/search?q=g:org.springframework.boot a:spring-boot-starter-web)依赖项添加到我们的*pom.xml*开始：![img]()

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
复制
```

或者，我们可以使用[Spring Initializr](https://start.spring.io/)生成项目并添加依赖项。

### 2.2. *休息模板*Bean

接下来，我们将创建一个*RestTemplate* bean：![img]()

```java
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
复制
```

## 3.API调用

让我们创建一个调用公共 API *http://httpbin.org/get*的服务类。

API 返回带有请求参数的 JSON 响应。例如，如果我们在浏览器上调用 URL *https://httpbin.org/get?parameter=springboot*，我们会得到这样的响应：

```json
{
  "args": {
    "parameter": "springboot"
  },
  "headers": {
  },
  "origin": "",
  "url": ""
}
复制
```

这里， *args* 对象包含请求参数。为简洁起见，省略了其他值。

### 3.1. 服务等级

让我们创建一个调用 API 并返回参数键值的服务 *类* ：

```java
@Service
public class HttpBinService {
    private final RestTemplate restTemplate;

    public HttpBinService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String get(String parameter) {
        String url = "http://httpbin.org/get?parameter={parameter}";
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class, parameter);
        Map<String, String> args = (Map<>) response.getBody().get("args");
        return args.get("parameter");
    }
}复制
```

get *()*方法调用指定的 URL，将响应解析为 *Map ，并检索**args* 对象中 字段*参数* 的值。

### 3.2. 测试

让我们用两个参数测试我们的服务类——springboot*和* spring *+boot——*并检查响应是否符合预期：

```java
@SpringBootTest
class HttpBinServiceTest {
    @Autowired
    private HttpBinService httpBinService;

    @Test
    void givenWithoutPlusSign_whenGet_thenSameValueReturned() throws JsonProcessingException {
        String parameterWithoutPlusSign = "springboot";
        String responseWithoutPlusSign = httpBinService.get(parameterWithoutPlusSign);
        assertEquals(parameterWithoutPlusSign, responseWithoutPlusSign);
    }

    @Test
    void givenWithPlusSign_whenGet_thenSameValueReturned() throws JsonProcessingException {
        String parameterWithPlusSign = "spring+boot";
        String responseWithPlusSign = httpBinService.get(parameterWithPlusSign);
        assertEquals(parameterWithPlusSign, responseWithPlusSign);
    }
}
复制
```

如果我们运行测试，我们会看到**第二个测试失败了**。响应是 *spring boot*而不是*spring+boot*。

## 4. 将拦截器与*RestTemplate一起使用*

我们可以使用拦截器对 URI 变量进行编码。

让我们创建一个实现*ClientHttpRequestInterceptor* 接口的类：![img]()

```java
public class UriEncodingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        HttpRequest encodedRequest = new HttpRequestWrapper(request) {
            @Override
            public URI getURI() {
                URI uri = super.getURI();
                String escapedQuery = uri.getRawQuery().replace("+", "%2B");
                return UriComponentsBuilder.fromUri(uri)
                  .replaceQuery(escapedQuery)
                  .build(true).toUri();
            }
        };
        return execution.execute(encodedRequest, body);
    }
}复制
```

我们已经实现了*intercept()* 方法。***此方法将在RestTemplate\*****发出每个请求之前执行。** 

让我们分解一下代码：

-   我们创建了一个新的 *HttpRequest* 对象来包装原始请求。
-   对于这个包装器，我们覆盖了 *getURI()* 方法来编码 URI 变量。 在这种情况下，我们将查询字符串中的加号替换为 *%2B 。*
-   使用 *UriComponentsBuilder*，我们创建一个新的 *URI* 并将查询字符串替换为编码的查询字符串。
-    我们从将替换原始请求的*intercept()*方法返回编码请求 。

### 4.1. 添加拦截器

接下来，我们需要将拦截器添加到*RestTemplate* bean：

```java
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new UriEncodingInterceptor()));
        return restTemplate;
    }
}
复制
```

如果我们再次运行测试，我们会看到它通过了。

**[拦截器](https://www.baeldung.com/spring-rest-template-interceptor)提供了更改我们想要的请求的任何部分的灵活性。**它们对于复杂的场景很有用，例如添加额外的标头或对请求中的字段执行更改。

对于像我们的示例这样更简单的任务，我们还可以使用*DefaultUriBuilderFactory* 来更改编码。接下来让我们看看如何做到这一点。

## 5. 使用*DefaultUriBuilderFactory*

另一种对 URI 变量进行编码的方法是更改 * RestTemplate*内部使用的*DefaultUriBuilderFactory*对象。

默认情况下，URI 构建器首先对整个 URL 进行编码，然后分别对值进行编码。我们将**创建一个新的\*DefaultUriBuilderFactory\*对象并将编码模式设置为\*VALUES_ONLY。\*这将编码限制为仅值。**

**然后我们可以使用 \*setUriTemplateHandler()\*方法 在我们的\*RestTemplate\* bean 中设置新的\*DefaultUriBuilderFactory\*对象。**

让我们用它来创建一个新的*RestTemplate* bean：![img]()

```java
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
        return restTemplate;
    }
}
复制
```

这是对 URI 变量进行编码的另一种选择。同样，如果我们运行测试，我们会看到它通过了。

## 六，结论

在本文中，我们了解了如何在*RestTemplate*请求中对 URI 变量进行编码。我们看到了执行此操作的两种方法 — 使用拦截器和更改*DefaultUriBuilderFactory* 对象。