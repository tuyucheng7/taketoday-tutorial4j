## 1. 概述

在这个简短的教程中，我们将讨论如何在RestTemplate实例中实现和注入ResponseErrorHandler接口，以优雅地处理远程API返回的HTTP错误。

## 2. 默认错误处理

默认情况下，RestTemplate将在HTTP错误的情况下抛出以下异常之一：

1.  HttpClientErrorException：在HTTP状态4xx的情况下
2.  HttpServerErrorException：在HTTP状态5xx的情况下
3.  UnknownHttpStatusCodeException：在未知HTTP状态的情况下

所有这些异常都是RestClientResponseException的扩展。

显然，添加自定义错误处理的最简单策略是将调用包装在try/catch块中。然后我们可以按照我们认为合适的方式处理捕获的异常。

但是，随着远程API或调用数量的增加，这种简单的策略无法很好地扩展。如果我们可以为我们所有的远程调用实现一个可重用的错误处理程序，那将会更有效率。

## 3. 实现ResponseErrorHandler

实现ResponseErrorHandler的类将从响应中读取HTTP状态，并且：

1.  抛出对我们的应用程序有意义的异常
2.  简单地忽略HTTP状态，让响应流不间断地继续

我们需要将ResponseErrorHandler实现注入到RestTemplate实例中。

因此，我们可以使用RestTemplateBuilder来构建模板，并在响应流中替换DefaultResponseErrorHandler。

所以让我们首先实现我们的RestTemplateResponseErrorHandler：

```java
@Component
public class RestTemplateResponseErrorHandler
      implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {

        return (httpResponse.getStatusCode().series() == CLIENT_ERROR
                    || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusCode()
              .series() == HttpStatus.Series.SERVER_ERROR) {
            // handle SERVER_ERROR
        } else if (httpResponse.getStatusCode()
              .series() == HttpStatus.Series.CLIENT_ERROR) {
            // handle CLIENT_ERROR
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException();
            }
        }
    }
}
```

然后我们可以使用RestTemplateBuilder构建RestTemplate实例来引入我们的RestTemplateResponseErrorHandler：

```java
@Service
public class BarConsumerService {

    private RestTemplate restTemplate;

    @Autowired
    public BarConsumerService(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder
              .errorHandler(new RestTemplateResponseErrorHandler())
              .build();
    }

    public Bar fetchBarById(String barId) {
        return restTemplate.getForObject("/bars/4242", Bar.class);
    }
}
```

## 4. 测试我们的实现

最后，我们将通过Mock服务器并返回NOT_FOUND状态来测试此处理程序：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { NotFoundException.class, Bar.class })
@RestClientTest
public class RestTemplateResponseErrorHandlerIntegrationTest {

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private RestTemplateBuilder builder;

    @Test
    public void  givenRemoteApiCall_when404Error_thenThrowNotFound() {
        Assertions.assertNotNull(this.builder);
        Assertions.assertNotNull(this.server);

        RestTemplate restTemplate = this.builder
              .errorHandler(new RestTemplateResponseErrorHandler())
              .build();

        this.server
              .expect(ExpectedCount.once(), requestTo("/bars/4242"))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withStatus(HttpStatus.NOT_FOUND));

        Assertions.assertThrows(NotFoundException.class, () -> {
            Bar response = restTemplate.getForObject("/bars/4242", Bar.class);
        });
    }
}
```

## 5. 总结

在本文中，我们提出了一个解决方案来实现和测试RestTemplate的自定义错误处理程序，该处理程序将HTTP错误转换为有意义的异常。