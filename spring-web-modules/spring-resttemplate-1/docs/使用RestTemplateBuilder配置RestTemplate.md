## 1. 简介

在本快速教程中，我们将了解如何配置Spring RestTemplate bean。

让我们首先讨论三种主要的配置类型：

-   使用默认的RestTemplateBuilder
-   使用RestTemplateCustomizer
-   创建我们自己的RestTemplateBuilder

为了能够轻松地对此进行测试，请按照有关如何[设置简单的Spring Boot应用程序](https://www.baeldung.com/spring-boot-start)的指南进行操作。

## 2. 使用默认的RestTemplateBuilder配置

要以这种方式配置RestTemplate，我们需要将Spring Boot提供的默认RestTemplateBuilder bean注入到我们的类中：

```java
private RestTemplate restTemplate;

@Autowired
public HelloController(RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
}
```

使用此方法创建的RestTemplate bean的范围仅限于我们在其中构建它的类。

## 3. 使用RestTemplateCustomizer进行配置

通过这种方法，我们可以创建应用程序范围的附加定制。

这是一种稍微复杂的方法。为此，我们需要创建一个实现RestTemplateCustomizer的类，并将其定义为一个bean：

```java
public class CustomRestTemplateCustomizer implements RestTemplateCustomizer {
    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(new CustomClientHttpRequestInterceptor());
    }
}
```

CustomClientHttpRequestInterceptor拦截器正在对请求进行基本记录：

```java
public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    private static Logger LOGGER = LoggerFactory.getLogger(CustomClientHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequestDetails(request);
        return execution.execute(request, body);
    }
    
    private void logRequestDetails(HttpRequest request) {
        LOGGER.info("Headers: {}", request.getHeaders());
        LOGGER.info("Request Method: {}", request.getMethod());
        LOGGER.info("Request URI: {}", request.getURI());
    }
}
```

现在，我们将CustomRestTemplateCustomizer定义为配置类或Spring Boot应用程序类中的bean：

```java
@Bean
public CustomRestTemplateCustomizer customRestTemplateCustomizer() {
    return new CustomRestTemplateCustomizer();
}
```

使用此配置，我们将在我们的应用程序中使用的每个RestTemplate都将在其上设置自定义拦截器。

## 4. 通过创建我们自己的RestTemplateBuilder进行配置

这是自定义RestTemplate的最极端的方法，它禁用了RestTemplateBuilder的默认自动配置，所以我们需要自己定义它：

```java
@Bean
@DependsOn(value = {"customRestTemplateCustomizer"})
public RestTemplateBuilder restTemplateBuilder() {
    return new RestTemplateBuilder(customRestTemplateCustomizer());
}
```

在此之后，我们可以将自定义构建器注入到我们的类中，就像我们使用默认的RestTemplateBuilder一样，并像往常一样创建一个RestTemplate：

```java
private RestTemplate restTemplate;

@Autowired
public HelloController(RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
}
```

## 5. 总结

我们已经了解了如何使用默认RestTemplateBuilder配置RestTemplate、构建我们自己的RestTemplateBuilder或使用RestTemplateCustomizer bean。