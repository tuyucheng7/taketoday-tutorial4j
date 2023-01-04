## 1. 概述

在本教程中，我们将学习如何实现Spring [RestTemplate](https://www.baeldung.com/rest-template)拦截器。

我们将通过一个示例，在该示例中我们将创建一个向响应添加自定义标头的拦截器。

## 2. 拦截器使用场景

除了标头修改之外，RestTemplate拦截器有用的其他一些用例是：

-   请求和响应日志记录
-   使用可配置的退避策略重试请求
-   基于某些请求参数的请求拒绝
-   更改请求URL地址

## 3. 创建拦截器

在大多数编程范式中，拦截器是使程序员能够通过拦截来控制执行的重要部分。Spring框架还支持多种用于不同目的的拦截器。

Spring RestTemplate允许我们添加实现[ClientHttpRequestInterceptor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/client/ClientHttpRequestInterceptor.html)接口的拦截器。该接口的intercept(HttpRequest, byte[], ClientHttpRequestExecution)方法将拦截给定的请求并通过让我们访问请求、主体和执行对象来返回响应。

我们将使用ClientHttpRequestExecution参数进行实际执行，并将请求传递给后续流程链。

作为第一步，让我们创建一个实现ClientHttpRequestInterceptor接口的拦截器类：

```java
public class RestTemplateHeaderModifierInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
          HttpRequest request,
          byte[] body,
          ClientHttpRequestExecution execution) throws IOException {

        ClientHttpResponse response = execution.execute(request, body);
        response.getHeaders().add("Foo", "bar");
        return response;
    }
}
```

我们的拦截器将为每个传入请求调用，一旦执行完成并返回，它将为每个响应添加一个自定义标头Foo。

由于intercept()方法包含请求和正文作为参数，因此还可以对请求进行任何修改，甚至可以根据特定条件拒绝请求执行。

## 4. 设置RestTemplate

现在我们已经创建了我们的拦截器，让我们创建RestTemplate bean并将我们的拦截器添加到它：

```java
@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new RestTemplateHeaderModifierInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
```

在某些情况下，可能已将拦截器添加到RestTemplate对象中。因此，为确保一切按预期工作，我们的代码仅在拦截器列表为空时才初始化拦截器列表。

正如我们的代码所示，我们使用默认构造函数创建RestTemplate对象，但在某些情况下我们需要读取请求/响应流两次。

例如，如果我们希望我们的拦截器用作请求/响应记录器，那么我们需要读取它两次——第一次由拦截器读取，第二次由客户端读取。

默认实现只允许我们读取响应流一次。为了满足此类特定场景，Spring提供了一个名为BufferingClientHttpRequestFactory的特殊类。顾名思义，此类将在JVM内存中缓冲请求/响应以供多次使用。

以下是如何使用BufferingClientHttpRequestFactory初始化RestTemplate对象以启用请求/响应流缓存：

```java
RestTemplate restTemplate = new RestTemplate(
    new BufferingClientHttpRequestFactory(
        new SimpleClientHttpRequestFactory()
    )
);
```

## 5. 测试我们的例子

这是用于测试我们的RestTemplate拦截器的JUnit测试用例：

```java
public class RestTemplateItegrationTest {

    @Autowired
    RestTemplate restTemplate;

    @Test
    public void givenRestTemplate_whenRequested_thenLogAndModifyResponse() {
        LoginForm loginForm = new LoginForm("username", "password");
        HttpEntity<LoginForm> requestEntity
              = new HttpEntity<LoginForm>(loginForm);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
              "http://httpbin.org/post", requestEntity, String.class
        );

        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getHeaders()
              .get("Foo")
              .get(0), "bar");
    }
}
```

在这里，我们使用免费托管的HTTP请求和响应服务[http://httpbin.org](https://httpbin.org/)来发布我们的数据。该测试服务将返回我们的请求主体以及一些元数据。

## 6. 总结

本教程是关于如何设置拦截器并将其添加到RestTemplate对象的。这种拦截器也可以用于过滤、监视和控制传入的请求。

RestTemplate拦截器的一个常见用例是标头修改，我们在本文中对此进行了详细说明。