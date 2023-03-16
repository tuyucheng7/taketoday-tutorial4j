## 1. 概述

在本教程中，我们将比较Spring [Feign](https://www.baeldung.com/spring-cloud-openfeign)(一个声明式REST客户端)和Spring [WebClient](https://www.baeldung.com/spring-5-webclient)(一个在Spring 5中引入的响应式Web客户端)。

## 2. 阻塞与非阻塞客户端

在当今的微服务生态系统中，后端服务通常需要使用HTTP调用其他Web服务。因此，Spring应用程序需要一个Web客户端来执行请求。

接下来，我们将研究阻塞Feign客户端和非阻塞WebClient实现之间的区别。

### 2.1 Spring Boot阻塞式Feign客户端

Feign客户端是一个声明式REST客户端，可以更轻松地编写Web客户端。使用Feign时，开发人员只需定义接口并相应地对其进行注解标注。然后，Spring在运行时提供实际的Web客户端实现。

在幕后，**使用@FeignClient标注的接口生成一个基于thread-per-request模型的同步实现**。因此，对于每个请求，分配的线程都会阻塞，直到它收到响应。保持多个线程存活的缺点是每个打开的线程占用内存和CPU周期。

接下来，假设我们的服务遇到流量高峰，每秒接收数千个请求。最重要的是，每个请求都需要等待几秒钟，以便上游服务返回结果。

根据分配给托管服务器的资源和流量峰值的长度，一段时间后，所有创建的线程将开始堆积并占用所有分配的资源。因此，这一系列事件将降低服务的性能并最终导致服务崩溃。

### 2.2 Spring Boot非阻塞WebClient

WebClient是[Spring WebFlux](https://www.baeldung.com/spring-webflux)库的一部分。它是**Spring Reactive框架提供的一种非阻塞解决方案，用于解决Feign Client等同步实现的性能瓶颈**。

Feign客户端为每个请求创建一个线程并阻塞它直到收到响应，而WebClient执行HTTP请求并将“等待响应”任务添加到队列中。稍后，在收到响应后从队列中执行“等待响应”任务，最终将响应传递给订阅者函数。

Reactive框架实现了由[Reactive Streams API](https://www.baeldung.com/java-9-reactive-streams)提供支持的事件驱动架构。正如我们所看到的，这使我们能够编写以最少数量的阻塞线程执行HTTP请求的服务。

因此，WebClient通过使用更少的系统资源处理更多的请求，帮助我们构建在恶劣环境中始终如一地执行的服务。

## 3. 比较示例

要查看Feign客户端和WebClient之间的区别，我们将实现两个HTTP端点，它们都调用返回产品列表的相同慢速端点。

我们将看到，在阻塞Feign实现的情况下，每个请求线程阻塞两秒钟，直到收到响应。

另一方面，非阻塞WebClient将立即关闭请求线程。

首先，我们需要添加三个依赖项：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

接下来，我们有慢端点定义：

```java
@GetMapping("/slow-service-products")
private List<Product> getAllProducts() throws InterruptedException {
    Thread.sleep(2000L); // delay
    return Arrays.asList(
        new Product("Fancy Smartphone", "A stylish phone you need"),
        new Product("Cool Watch", "The only device you need"),
        new Product("Smart TV", "Cristal clean images")
    );
}
```

### 3.1 使用Feign调用慢速服务

现在，让我们开始使用Feign实现第一个端点。

第一步是定义接口并使用@FeignCleint对其进行标注：

```java
@FeignClient(value = "productsBlocking", url = "http://localhost:8080")
public interface ProductsFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/slow-service-products", produces = "application/json")
    List<Product> getProductsBlocking(URI baseUrl);
}
```

最后，我们将使用定义的ProductsFeignClient接口来调用慢速服务：

```java
@GetMapping("/products-blocking")
public List<Product> getProductsBlocking() {
    log.info("Starting BLOCKING Controller!");
    final URI uri = URI.create(getSlowServiceBaseUri());

    List<Product> result = productsFeignClient.getProductsBlocking(uri);
    result.forEach(product -> log.info(product.toString()));

    log.info("Exiting BLOCKING Controller!");
    return result;
}
```

接下来，让我们执行一个请求并查看输出的日志：

```shell
Starting BLOCKING Controller!
Product(title=Fancy Smartphone, description=A stylish phone you need)
Product(title=Cool Watch, description=The only device you need)
Product(title=Smart TV, description=Cristal clean images)
Exiting BLOCKING Controller!
```

正如预期的那样，**在同步实现的情况下，请求线程正在等待接收所有产品**。之后，它会将它们打印到控制台并退出控制器函数，然后最终关闭请求线程。

### 3.2 使用WebClient调用慢速服务

其次，让我们实现一个非阻塞WebClient来调用相同的端点：

```java
@GetMapping(value = "/products-non-blocking", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<Product> getProductsNonBlocking() {
    log.info("Starting NON-BLOCKING Controller!");

    Flux<Product> productFlux = WebClient.create()
        .get()
        .uri(getSlowServiceBaseUri() + SLOW_SERVICE_PRODUCTS_ENDPOINT_NAME)
        .retrieve()
        .bodyToFlux(Product.class);

    productFlux.subscribe(product -> log.info(product.toString()));

    log.info("Exiting NON-BLOCKING Controller!");
    return productFlux;
}
```

**控制器函数不返回产品列表，而是返回Flux发布者并快速完成该方法**。在这种情况下，消费者将订阅Flux实例并在产品可用时对其进行处理。

现在，让我们再次查看日志：

```shell
Starting NON-BLOCKING Controller!
Exiting NON-BLOCKING Controller!
Product(title=Fancy Smartphone, description=A stylish phone you need)
Product(title=Cool Watch, description=The only device you need)
Product(title=Smart TV, description=Cristal clean images)
```

正如预期的那样，控制器函数立即完成，由此，它也完成了请求线程。一旦Product可用，订阅的函数就会处理它们。

## 4. 总结

在本文中，我们比较了Spring中两种编写Web客户端的风格。

首先，我们探索了Feign客户端，这是一种编写同步和阻塞Web客户端的声明式风格。

其次，我们探索了WebClient，它支持Web客户端的异步实现。

尽管Feign客户端在许多情况下是一个很好的选择，并且生成的代码具有较低的认知复杂性，但WebClient的非阻塞风格在高流量高峰期间使用的系统资源要少得多。考虑到这一点，最好为这些情况选择WebClient。