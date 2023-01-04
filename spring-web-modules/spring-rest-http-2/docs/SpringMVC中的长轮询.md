## 1. 概述

长轮询是服务器应用程序用来保持客户端连接直到信息可用的一种方法。这通常在服务器必须调用下游服务以获取信息并等待结果时使用。

在本教程中，我们将使用[DeferredResult](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/context/request/async/DeferredResult.html)探索Spring MVC中长轮询的概念。我们将从查看使用DeferredResult的基本实现开始，然后讨论我们如何处理错误和超时。最后，我们将看看如何测试所有这些。 

## 2. 使用DeferredResult进行长轮询

我们可以在Spring MVC中使用DeferredResult作为异步处理入站HTTP请求的方式。它允许释放HTTP工作线程来处理其他传入请求，并将工作卸载到另一个工作线程。因此，它有助于提高需要长时间计算或任意等待时间的请求的服务可用性。

我们之前关于Spring的[DeferredResult](https://www.baeldung.com/spring-deferred-result)类的文章更深入地介绍了它的功能和用例。

### 2.1 Publisher

让我们通过创建一个使用DeferredResult的发布应用程序来开始我们的长轮询示例。 

首先，让我们定义一个Spring @RestController，它使用DeferredResult但不会将其工作卸载到另一个工作线程：

```java
@RestController
@RequestMapping("/api")
public class BakeryController {
    @GetMapping("/bake/{bakedGood}")
    public DeferredResult<String> publisher(@PathVariable String bakedGood, @RequestParam Integer bakeTime) {
        DeferredResult<String> output = new DeferredResult<>();
        try {
            Thread.sleep(bakeTime);
            output.setResult(format("Bake for %s complete and order dispatched. Enjoy!", bakedGood));
        } catch (Exception e) {
            // ...
        }
        return output;
    }
}
```

该控制器以与常规阻塞控制器相同的方式同步工作。因此，我们的HTTP线程被完全阻塞，直到bakeTime过去。如果我们的服务有大量入站流量，这并不理想。

现在让我们通过将工作卸载到工作线程来异步设置输出：

```java
private ExecutorService bakers = Executors.newFixedThreadPool(5);

@GetMapping("/bake/{bakedGood}")
public DeferredResult<String> publisher(@PathVariable String bakedGood, @RequestParam Integer bakeTime) {
    DeferredResult<String> output = new DeferredResult<>();
    bakers.execute(() -> {
        try {
            Thread.sleep(bakeTime);
            output.setResult(format("Bake for %s complete and order dispatched. Enjoy!", bakedGood));
        } catch (Exception e) {
            // ...
        }
    });
    return output;
}
```

在此示例中，我们现在可以释放HTTP工作线程来处理其他请求。我们面包师池中的一个工作线程正在做这项工作，并将在完成时设置结果。当worker调用setResult时，它将允许容器线程响应调用客户端。

我们的代码现在很适合长轮询，与传统的阻塞控制器相比，我们的服务对入站HTTP请求更可用。但是，我们还需要处理错误处理和超时处理等边缘情况。

为了处理我们的工作人员抛出的检查错误，我们将使用DeferredResult提供的setErrorResult方法：

```java
bakers.execute(() -> {
    try {
        Thread.sleep(bakeTime);
        output.setResult(format("Bake for %s complete and order dispatched. Enjoy!", bakedGood));
     } catch (Exception e) {
        output.setErrorResult("Something went wrong with your order!");
     }
});
```

工作线程现在能够优雅地处理抛出的任何异常。

由于长轮询通常用于异步和同步地处理来自下游系统的响应，因此我们应该添加一种机制来强制超时，以防我们从未收到来自下游系统的响应。DeferredResult API提供了执行此操作的机制。首先，我们在DeferredResult对象的构造函数中传入一个超时参数：

```java
DeferredResult<String> output = new DeferredResult<>(5000L);
```

接下来，我们来实现超时场景。为此，我们将使用onTimeout：

```java
output.onTimeout(() -> output.setErrorResult("the bakery is not responding in allowed time"));
```

这将一个Runnable作为输入，当达到超时阈值时，它由容器线程调用。如果达到超时，那么我们将其作为错误处理并相应地使用setErrorResult。

### 2.2 Subscriber

现在我们已经设置了发布应用程序，让我们编写一个订阅客户端应用程序。

编写调用此长轮询API的服务非常简单，因为它本质上与为标准阻塞REST调用编写客户端相同。唯一真正的区别是，由于长轮询的等待时间，我们要确保我们有一个超时机制。在Spring MVC中，我们可以使用[RestTemplate](https://www.baeldung.com/rest-template)或[WebClient](https://www.baeldung.com/spring-5-webclient)来实现这一点，因为它们都具有内置的超时处理。

首先，让我们从一个使用RestTemplate的例子开始。让我们使用RestTemplateBuilder创建一个RestTemplate实例，以便我们可以设置超时时间：

```java
public String callBakeWithRestTemplate(RestTemplateBuilder restTemplateBuilder) {
    RestTemplate restTemplate = restTemplateBuilder
        .setConnectTimeout(Duration.ofSeconds(10))
        .setReadTimeout(Duration.ofSeconds(10))
        .build();

    try {
        return restTemplate.getForObject("/api/bake/cookie?bakeTime=1000", String.class);
    } catch (ResourceAccessException e) {
        // handle timeout
    }
}
```

在这段代码中，通过从我们的长轮询调用中捕获ResourceAccessException，我们能够在超时时处理错误。

接下来，让我们创建一个使用WebClient实现相同结果的示例：

```java
public String callBakeWithWebClient() {
    WebClient webClient = WebClient.create();
    try {
        return webClient.get()
            .uri("/api/bake/cookie?bakeTime=1000")
            .retrieve()
            .bodyToFlux(String.class)
            .timeout(Duration.ofSeconds(10))
            .blockFirst();
    } catch (ReadTimeoutException e) {
        // handle timeout
    }
}
```

我们之前关于设置[Spring REST超时](https://www.baeldung.com/spring-rest-timeout)的文章更深入地介绍了这个主题。

## 3. 测试长轮询

现在我们已经启动并运行了我们的应用程序，让我们讨论如何测试它。我们可以开始使用[MockMvc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/test/web/servlet/MockMvc.html)来测试对我们的控制器类的调用：

```java
MvcResult asyncListener = mockMvc
    .perform(MockMvcRequestBuilders.get("/api/bake/cookie?bakeTime=1000"))
    .andExpect(request().asyncStarted())
    .andReturn();
```

在这里，我们调用我们的DeferredResult端点并断言该请求已启动异步调用。从这里开始，测试将等待异步结果的完成，这意味着我们不需要在测试中添加任何等待逻辑。

接下来，我们要断言异步调用何时返回并且它与我们期望的值匹配：

```java
String response = mockMvc
    .perform(asyncDispatch(asyncListener))
    .andReturn()
    .getResponse()
    .getContentAsString();

assertThat(response)
    .isEqualTo("Bake for cookie complete and order dispatched. Enjoy!");
```

通过使用asyncDispatch()，我们可以获得异步调用的响应并声明其值。

为了测试我们的DeferredResult的超时机制，我们需要通过在asyncListener和响应调用之间添加一个超时启动器来稍微修改测试代码：

```java
((MockAsyncContext) asyncListener
    .getRequest()
    .getAsyncContext())
    .getListeners()
    .get(0)
    .onTimeout(null);
```

这段代码可能看起来很奇怪，但我们以这种方式调用onTimeout是有特定原因的。我们这样做是为了让[AsyncListener](https://docs.oracle.com/javaee/7/api/javax/servlet/AsyncListener.html)知道操作已超时。这将确保正确调用我们为控制器中的onTimeout方法实现的Runnable类。

## 4. 总结

在本文中，我们介绍了如何在长轮询的上下文中使用DeferredResult。我们还讨论了如何为长轮询编写订阅客户端，以及如何对其进行测试。