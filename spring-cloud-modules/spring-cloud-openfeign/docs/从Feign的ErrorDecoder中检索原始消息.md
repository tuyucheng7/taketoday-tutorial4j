## 1. 概述

RESTful 服务可能因多种原因而失败。在本教程中，我们将了解如果集成的 REST 服务抛出错误，如何从[Feign客户端检索原始消息。](https://www.baeldung.com/spring-cloud-openfeign)

## 2. 伪装客户端

Feign 是一个可插入和声明式的 Web 服务客户端，它使编写 Web 服务客户端变得更加容易。此外，对于Feign注解，它还支持[JAX-RS](https://www.baeldung.com/jax-rs-spec-and-implementations)，支持 encoder和decoder，提供更多的定制化。

## 3. 从ErrorDecoder 中获取消息 

当错误发生时，Feign 客户端会抑制原始消息，并且要检索它，我们需要编写一个自定义的[ErrorDecoder](https://appdoc.app/artifact/com.netflix.feign/feign-core/8.11.0/feign/codec/ErrorDecoder.html)。如果没有这样的定制，我们会得到以下错误：

```java
feign.FeignException$NotFound: [404] during [POST] to [http://localhost:8080/upload-error-1] [UploadClient#fileUploadError(MultipartFile)]: [{"timestamp":"2022-02-18T13:25:22.083+00:00","status":404,"error":"Not Found","path":"/upload-error-1"}]
	at feign.FeignException.clientErrorStatus(FeignException.java:219) ~[feign-core-11.7.jar:na]
	at feign.FeignException.errorStatus(FeignException.java:194) ~[feign-core-11.7.jar:na]

```

为了处理这个错误，我们将创建一个简单的ExceptionMessageJavabean 来表示错误消息：

```java
public class ExceptionMessage {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    // standard getters and setters
}
```

让我们通过在我们自定义的ErrorDecoder实现中提取它来检索原始消息：

```java
public class RetreiveMessageErrorDecoder implements ErrorDecoder {
    private ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ExceptionMessage message = null;
        try (InputStream bodyIs = response.body()
            .asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.readValue(bodyIs, ExceptionMessage.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        switch (response.status()) {
        case 400:
            return new BadRequestException(message.getMessage() != null ? message.getMessage() : "Bad Request");
        case 404:
            return new NotFoundException(message.getMessage() != null ? message.getMessage() : "Not found");
        default:
            return errorDecoder.decode(methodKey, response);
        }
    }
}

```

在我们的实现中，我们添加了基于可能的错误的逻辑，因此我们可以自定义它们以满足我们的要求。在我们的 switch 块的默认情况下，我们使用ErrorDecoder的[默认](https://appdoc.app/artifact/com.netflix.feign/feign-core/8.11.0/feign/codec/ErrorDecoder.Default.html)实现。

当状态不在 2xx 范围内时，默认实现解码 HTTP 响应。当throwable是[retryable](https://www.baeldung.com/feign-retry)时，它应该是RetryableException 的子类型，我们应该尽可能引发特定于应用程序的异常。

要配置我们自定义的ErrorDecoder，我们将在 Feign 配置中将我们的实现添加为一个 bean：

```java
@Bean
public ErrorDecoder errorDecoder() {
    return new RetreiveMessageErrorDecoder();
}
```

现在，让我们看看原始消息的异常：

```java
exception.cn.tuyucheng.taketoday.cloud.openfeign.NotFoundException: Page Not found
	at config.fileupload.cn.tuyucheng.taketoday.cloud.openfeign.RetreiveMessageErrorDecoder.decode(RetreiveMessageErrorDecoder.java:30) ~[classes/:na]
	at feign.AsyncResponseHandler.handleResponse(AsyncResponseHandler.java:96) ~[feign-core-11.7.jar:na]

```

## 4. 总结

在本文中，我们演示了如何自定义ErrorDecoder，以便我们可以捕获 Feign 错误以获取原始消息。