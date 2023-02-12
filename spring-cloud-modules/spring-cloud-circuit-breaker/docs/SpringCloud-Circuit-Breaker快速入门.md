## 1. 概述

在本教程中，**我们将介绍Spring Cloud Circuit Breaker项目**并了解如何使用它。

首先，我们将了解Spring Cloud Circuit Breaker除了现有的断路器实现之外还提供什么。接下来，我们将学习如何使用[Spring Boot自动配置](https://www.baeldung.com/spring-boot-custom-auto-configuration)机制将一个或多个断路器集成到我们的应用程序中。

请注意，我们在[Hystrix简介](https://www.baeldung.com/introduction-to-hystrix)、[Spring Cloud Netflix Hystrix](https://www.baeldung.com/spring-cloud-netflix-hystrix)和[Resilience4j指南](https://www.baeldung.com/resilience4j)中获得了有关断路器是什么以及它们如何工作的更多信息。

## 2. Spring Cloud Circuit Breaker

直到最近，Spring Cloud才为我们提供了一种在应用程序中添加断路器的方法。这是通过使用Netflix Hystrix作为Spring Cloud Netflix项目的一部分实现的。

Spring Cloud Netflix项目实际上只是一个围绕Hystrix的基于注解的包装器库。因此，这两个库是紧密耦合的。这意味着我们不能在不更改应用程序的情况下切换到另一个断路器实现。

Spring Cloud Circuit Breaker项目解决了这个问题。**它提供了一个跨不同断路器实现的抽象层**。这是一个可插拔的架构，因此，我们可以针对提供的抽象/接口进行编码，并根据我们的需要切换到另一个实现。

对于我们的示例，**我们将只关注Resilience4J实现**。但是，这些技术可用于其他插件。

## 3. 自动配置

**为了在我们的应用程序中使用特定的断路器实现，我们需要添加适当的Spring启动器**。在我们的例子中，让我们使用[spring-cloud-starter-circuitbreaker-resilience4j](https://search.maven.org/search?q=spring-cloud-starter-circuitbreaker-resilience4j)：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
    <version>1.0.2.RELEASE</version>
</dependency>
```

**如果自动配置机制在类路径中找到启动器之一，则会配置必要的断路器bean**。

如果我们想禁用Resilience4J自动配置，我们可以将spring.cloud.circuitbreaker.resilience4j.enabled属性设置为false。

## 4. 一个简单的断路器示例

让我们使用Spring Boot创建一个Web应用程序，以便我们探索Spring Cloud Circuit Breaker库的工作原理。

我们将构建一个返回相册列表的简单Web服务。假设原始列表由第三方服务提供。为简单起见，我们将使用[Jsonplaceholder](https://jsonplaceholder.typicode.com/)提供的外部虚拟API来检索列表：

```shell
https://jsonplaceholder.typicode.com/albums
```

### 4.1 创建断路器

让我们创建我们的第一个断路器。我们将从注入CircuitBreakerFactory bean的实例开始：

```java
@Service
public class AlbumService {

    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;

    //...
}
```

现在，我们可以使用CircuitBreakerFactory#create方法轻松创建断路器。它以断路器标识符作为参数：

```java
CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
```

### 4.2 在断路器中包装任务

为了包装和运行受断路器保护的任务，我们需要调用以Supplier作为参数的run方法。

```java
public String getAlbumList() {
    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
    String url = "https://jsonplaceholder.typicode.com/albums";

    return circuitBreaker.run(() -> restTemplate.getForObject(url, String.class));
}
```

**断路器为我们运行我们的方法并提供容错**。

有时，我们的外部服务可能需要很长时间才能响应、抛出意外异常或外部服务或主机不存在。在这种情况下，我们可以**提供一个回退**作为run方法的第二个参数：

```java
public String getAlbumList() {
    CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
    String url = "http://localhost:1234/not-real";
    
    return circuitBreaker.run(() -> restTemplate.getForObject(url, String.class), 
        throwable -> getDefaultAlbumList());
}
```

回退的lambda接收Throwable作为输入，描述错误。这意味着**我们可以根据触发回退的异常类型向调用者提供不同的回退结果**。

在这种情况下，我们不会考虑异常。我们将只返回缓存的相册列表。

如果外部调用以异常结束并且没有提供回退，则Spring将抛出NoFallbackAvailableException。

### 4.3 创建一个控制器

现在，让我们完成示例并创建一个简单的控制器来调用服务方法并通过浏览器显示结果：

```java
@RestController
public class Controller {

    @Autowired
    private Service service;

    @GetMapping("/albums")
    public String albums() {
        return service.getAlbumList();
    }
}
```

最后，让我们调用REST服务并查看结果：

```shell
[GET] http://localhost:8080/albums
```

## 5. 全局自定义配置

通常，默认配置是不够的。出于这个原因，我们需要根据我们的用例创建具有自定义配置的断路器。

为了覆盖默认配置，我们需要在@Configuration类中指定我们自己的bean和属性。

在这里，我们将为所有断路器定义一个全局配置。为此，**我们需要定义一个Customizer<CircuitBreakerFactory\> bean**。因此，让我们使用Resilience4JCircuitBreakerFactory实现。

首先，我们将按照[Resilience4j教程](https://www.baeldung.com/resilience4j)定义断路器和时间限制器配置类：

```java
CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
    .failureRateThreshold(50)
    .waitDurationInOpenState(Duration.ofMillis(1000))
    .slidingWindowSize(2)
    .build();
TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
    .timeoutDuration(Duration.ofSeconds(4))
    .build();
```

接下来，让我们使用Resilience4JCircuitBreakerFactory.configureDefault方法将配置嵌入到Customizer bean中：

```java
@Configuration
public class Resilience4JConfiguration {
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        // the circuitBreakerConfig and timeLimiterConfig objects

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
              .timeLimiterConfig(timeLimiterConfig)
              .circuitBreakerConfig(circuitBreakerConfig)
              .build());
    }
}
```

## 6. 具体自定义配置

当然，**我们的应用程序中可以有多个断路器**。因此，在某些情况下，我们需要为每个断路器进行特定配置。

同样，我们可以定义一个或多个Customizer bean。然后，我们可以使用Resilience4JCircuitBreakerFactory.configure方法为每一个提供不同的配置：

```java
@Bean
public Customizer<Resilience4JCircuitBreakerFactory> specificCustomConfiguration1() {
    // the circuitBreakerConfig and timeLimiterConfig objects

    return factory -> factory.configure(builder -> builder.circuitBreakerConfig(circuitBreakerConfig)
        .timeLimiterConfig(timeLimiterConfig).build(), "circuitBreaker");
}
```

这里我们提供了第二个参数，即我们正在配置的断路器的id。

我们还可以通过向同一方法提供断路器ID列表来设置具有相同配置的多个断路器：

```java
@Bean
public Customizer<Resilience4JCircuitBreakerFactory> specificCustomConfiguration2() {

    // the circuitBreakerConfig and timeLimiterConfig objects

    return factory -> factory.configure(builder -> builder.circuitBreakerConfig(circuitBreakerConfig)
        .timeLimiterConfig(timeLimiterConfig).build(),
            "circuitBreaker1", "circuitBreaker2", "circuitBreaker3");
}
```

## 7. 替代实施

我们已经了解了如何使用Resilience4j实现通过Spring Cloud Circuit Breaker创建一个或多个断路器。

但是，我们可以在我们的应用程序中利用Spring Cloud Circuit Breaker支持的其他实现：

-   [Hystrix](https://www.baeldung.com/spring-cloud-netflix-hystrix)
-   [Sentinel](https://github.com/alibaba/Sentinel)
-   [Spring Retry](https://www.baeldung.com/spring-retry)

**值得一提的是，我们可以在我们的应用程序中混合和匹配不同的断路器实现。我们不仅限于一个库**。

上述库的功能比我们在这里探索的要多。然而，Spring Cloud Circuit Breaker只是对断路器部分的抽象。例如，Resilience4j除了本文使用的CircuitBreaker和TimeLimiter模块外，还提供了其他模块，如RateLimiter、Bulkhead、Retry。

## 8. 总结

在本文中，我们介绍了Spring Cloud Circuit Breaker项目。

首先，我们了解了Spring Cloud Circuit Breaker是什么，以及它如何允许我们向应用程序添加断路器。

接下来，我们利用Spring Boot自动配置机制来展示如何定义和集成断路器。此外，我们还演示了Spring Cloud Circuit Breaker如何通过简单的REST服务工作。

最后，我们学会了一起配置所有断路器，以及单独配置。