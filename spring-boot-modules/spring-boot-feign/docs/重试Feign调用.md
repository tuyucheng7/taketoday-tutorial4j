## 1. 简介

通过REST端点调用外部服务是一种常见的活动，它通过[Feign](https://www.baeldung.com/intro-to-feign)等库变得非常简单。然而，在这样的调用中，很多事情都可能出错。其中许多问题是随机的或暂时的。

在本教程中，我们将学习如何重试失败的调用并创建更具弹性的REST客户端。

## 2. Feign客户端设置

首先，让我们创建一个简单的Feign客户端构建器，稍后我们将通过重试功能对其进行增强。我们将使用[OkHttpClient](https://www.baeldung.com/guide-to-okhttp)作为HTTP客户端。此外，我们将使用GsonEncoder和GsonDecoder对请求和响应进行编码和解码。最后，我们需要指定目标的URI和响应类型：

```java
public class ResilientFeignClientBuilder {
    public static <T> T createClient(Class<T> type, String uri) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new GsonEncoder())
                .decoder(new GsonDecoder())
                .target(type, uri);
    }
}
```

或者，如果我们使用Spring，我们可以让它使用可用的bean自动注入Feign客户端。

## 3. Feign重试

幸运的是，Feign内置了重试功能，只需对其进行配置即可。**我们可以通过向客户端构建器提供Retryer接口的实现来做到这一点**。

它最重要的方法continueOrPropagate接收RetryableException作为参数并且不返回任何内容。执行时，它要么抛出异常，要么成功退出(通常在睡眠之后)。**如果没有抛出异常，Feign会继续重试调用**。如果抛出异常，它将被传播并有效地结束调用并显示错误。

### 3.1  天真的实现

让我们编写一个非常简单的Retryer实现，它始终会在等待一秒钟后重试调用：

```java
public class NaiveRetryer implements feign.Retryer {
    @Override
    public void continueOrPropagate(RetryableException e) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw e;
        }
    }
}
```

因为Retryer实现了Cloneable接口，所以我们还需要重写clone方法。

```java
@Override
public Retryer clone() {
    return new NaiveRetryer();
}
```

最后，我们需要将我们的实现添加到客户端构建器：

```java
public static <T> T createClient(Class<T> type, String uri) {
    return Feign.builder()
        // ...
        .retryer(new NaiveRetryer())    
        // ...
}
```

或者，如果我们使用Spring，我们可以使用[@Component](https://www.baeldung.com/spring-component-annotation)注解来标注NaiveRetryer，或者在配置类中定义一个[bean](https://www.baeldung.com/spring-bean)，让Spring完成剩下的工作：

```java
@Bean
public Retryer retryer() {
    return new NaiveRetryer();
}
```

### 3.2 默认实现

Feign提供了Retryer接口的合理默认实现。**它只会重试给定的次数，从某个时间间隔开始，然后在每次重试时将其增加到提供的最大值**。让我们用100毫秒的起始间隔、3秒的最大间隔和5的最大尝试次数来定义它：

```java
public static <T> T createClient(Class<T> type, String uri) {
    return Feign.builder()
    // ...
    .retryer(new Retryer.Default(100L, TimeUnit.SECONDS.toMillis(3L), 5))    
    // ...
}
```

### 3.3 不重试

如果我们不希望Feign重试任何调用，我们可以向客户端构建器提供Retryer.NEVER_RETRY实现。它每次都会简单地传播异常。

## 4. 创建可重试异常

在上一节中，我们学习了控制重试调用的频率。现在让我们看看如何控制何时重试调用以及何时简单地抛出异常。

### 4.1 ErrorDecoder和RetryableException

当我们收到错误响应时，Feign将其传递给ErrorDecoder接口的一个实例，该实例决定如何处理它。最重要的是，解码器可以将异常映射到RetryableException的实例，使Retryer能够重试调用。**ErrorDecoder的默认实现仅在响应包含“Retry-After”标头时创建RetryableException实例**。最常见的是，我们可以在503 Service Unavailable响应中找到它。

这是很好的默认行为，但有时我们需要更加灵活。例如，我们可能正在与外部服务通信，该服务有时会随机响应500 Internal Server Error，而我们无力修复它。我们可以做的是重试调用，因为我们知道下次它可能会起作用。为此，我们需要编写一个自定义的ErrorDecoder实现。

### 4.2 创建自定义错误解码器

我们只需要在自定义解码器中实现一种方法：decode。它接收两个参数，一个String方法键和一个Response对象。它返回一个异常，它应该是RetryableException的实例或依赖于实现的一些其他异常。

我们的decode方法将简单地检查响应的状态代码是否高于或等于500。如果是这样，它将创建RetryableException。如果不是，它将返回使用FeignException类的errorStatus工厂函数创建的基本FeignException：

```java
public class Custom5xxErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException exception = feign.FeignException.errorStatus(methodKey, response);
        int status = response.status();
        if (status >= 500) {
            return new RetryableException(
                    response.status(),
                    exception.getMessage(),
                    response.request().httpMethod(),
                    exception,
                    null,
                    response.request());
        }
        return exception;
    }
}
```

请注意，在这种情况下，我们创建并返回异常，而不是抛出异常。

最后，我们需要将解码器插入客户端构建器：

```java
public static <T> T createClient(Class<T> type, String uri) {
    return Feign.builder()
        // ...
        .errorDecoder(new Custom5xxErrorDecoder())
        // ...
}
```

## 5. 总结

在本文中，我们学习了如何控制Feign库的重试逻辑。我们研究了Retryer接口以及如何使用它来控制重试的时间和次数。然后我们创建了ErrorDecoder来控制哪些响应需要重试。