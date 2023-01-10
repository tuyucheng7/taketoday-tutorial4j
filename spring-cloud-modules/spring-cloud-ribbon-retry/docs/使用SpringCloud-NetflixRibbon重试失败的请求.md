## 1. 概述

[Spring Cloud 通过使用Netflix Ribbon](https://www.baeldung.com/spring-cloud-rest-client-with-netflix-ribbon)提供客户端负载均衡。Ribbon 的负载均衡机制可以通过重试来补充。

在本教程中，我们将探索这种重试机制。

首先，我们将了解为什么在构建应用程序时考虑到此功能很重要。然后，我们将使用 Spring Cloud Netflix Ribbon 构建和配置一个应用程序来演示该机制。

## 2.动机

在基于云的应用程序中，服务向其他服务发出请求是一种常见的做法。但在这样一个动态多变的环境中，网络可能会出现故障或服务可能暂时不可用。

我们希望以优雅的方式处理故障并快速恢复。在许多情况下，这些问题是短暂的。如果我们在失败发生后不久重复相同的请求，也许它会成功。

这种做法有助于我们提高应用程序的弹性，这是可靠的云应用程序的关键方面之一。

然而，我们需要注意重试，因为它们也可能导致糟糕的情况。例如，它们可能会增加延迟，这可能是不可取的。

## 3.设置

为了试验重试机制，我们需要两个Spring Boot服务。首先，我们将创建一个天气服务，它将通过 REST 端点显示今天的天气信息。

其次，我们定义一个将使用天气端点的客户端服务。

### 3.1. 天气服务

让我们构建一个非常简单的天气服务，它有时会失败，并显示 503 HTTP 状态代码(服务不可用)。当调用次数是可配置的successful.call.divisor属性的倍数时，我们将通过选择失败来模拟这种间歇性失败：

```java
@Value("${successful.call.divisor}")
private int divisor;
private int nrOfCalls = 0;

@GetMapping("/weather")
public ResponseEntity<String> weather() {
    LOGGER.info("Providing today's weather information");
    if (isServiceUnavailable()) {
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }
    LOGGER.info("Today's a sunny day");
    return new ResponseEntity<>("Today's a sunny day", HttpStatus.OK);
}

private boolean isServiceUnavailable() {
    return ++nrOfCalls % divisor != 0;
}
```

此外，为了帮助我们观察对服务的重试次数，我们在处理程序中有一个消息记录器。

稍后，我们将配置客户端服务以在天气服务暂时不可用时触发重试机制。

### 3.2. 客户服务

我们的第二个服务将使用 Spring Cloud Netflix Ribbon。

首先，让我们定义[Ribbon 客户端配置](https://www.baeldung.com/spring-cloud-rest-client-with-netflix-ribbon)：

```java
@Configuration
@RibbonClient(name = "weather-service", configuration = RibbonConfiguration.class)
public class WeatherClientRibbonConfiguration {

    @LoadBalanced
    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
```

我们的 HTTP 客户端用@LoadBalanced 注解，这意味着我们希望它与 Ribbon 进行负载平衡。

我们现在将添加一个 ping 机制来确定服务的可用性，以及一个循环负载平衡策略，通过定义上面的@RibbonClient注解中包含的RibbonConfiguration类：

```java
public class RibbonConfiguration {
 
    @Bean
    public IPing ribbonPing() {
        return new PingUrl();
    }
 
    @Bean
    public IRule ribbonRule() {
        return new RoundRobinRule();
    }
}
```

接下来，我们需要从 Ribbon 客户端关闭[Eureka ，因为](https://www.baeldung.com/spring-cloud-netflix-eureka)我们没有使用服务发现。相反，我们正在使用可用于负载平衡的手动定义的天气服务实例列表。

因此，让我们也将这一切添加到application.yml文件中：

```plaintext
weather-service:
    ribbon:
        eureka:
            enabled: false
        listOfServers: http://localhost:8021, http://localhost:8022
```

最后，让我们构建一个控制器并让它调用后端服务：

```java
@RestController
public class MyRestController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/client/weather")
    public String weather() {
        String result = this.restTemplate.getForObject("http://weather-service/weather", String.class);
        return "Weather Service Response: " + result;
    }
}
```

## 4.启用重试机制

### 4.1. 配置application.yml属性

我们需要将天气服务属性放入客户端应用程序的application.yml文件中：

```plaintext
weather-service:
  ribbon:
    MaxAutoRetries: 3
    MaxAutoRetriesNextServer: 1
    retryableStatusCodes: 503, 408
    OkToRetryOnAllOperations: true
```

上面的配置使用了我们需要定义的标准功能区属性来启用重试：

-   MaxAutoRetries – 在同一台服务器上重试失败请求的次数(默认为 0)
-   MaxAutoRetriesNextServer – 尝试排除第一个服务器的服务器数(默认为 0)
-   retryableStatusCodes – 要重试的 HTTP 状态代码列表
-   OkToRetryOnAllOperations – 当此属性设置为 true 时，将重试所有类型的 HTTP 请求，而不仅仅是 GET 请求(默认)

当客户端服务收到 503(服务不可用)或 408(请求超时)响应代码时，我们将重试失败的请求。

### 4.2. 必需的依赖项

Spring Cloud Netflix Ribbon 利用[Spring Retry 重](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.retry" AND a%3A"spring-retry")试失败的请求。

我们必须确保依赖项在类路径上。否则，将不会重试失败的请求。我们可以省略版本，因为它由Spring Boot管理：

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
```

### 4.3. 实践中的重试逻辑

最后，让我们看看实践中的重试逻辑。

出于这个原因，我们需要我们的天气服务的两个实例，我们将在 8021 和 8022 端口上运行它们。当然，这些实例应该与上一节中定义的listOfServers列表相匹配。

此外，我们需要在每个实例上配置successful.call.divisor属性，以确保我们的模拟服务在不同时间失败：

```plaintext
successful.call.divisor = 5 // instance 1
successful.call.divisor = 2 // instance 2
```

接下来，我们还要在端口 8080 上运行客户端服务并调用：

```plaintext
http://localhost:8080/client/weather
```

让我们看一下weather-service的控制台：

```plaintext
weather service instance 1:
    Providing today's weather information
    Providing today's weather information
    Providing today's weather information
    Providing today's weather information

weather service instance 2:
    Providing today's weather information
    Today's a sunny day
```

因此，经过几次尝试(在实例 1 上尝试 4 次，在实例 2 上尝试 2 次)，我们得到了有效的响应。

## 5.退避策略配置

当网络遇到的数据量超出其处理能力时，就会发生拥塞。为了缓解它，我们可以设置退避策略。

默认情况下，重试之间没有延迟。在下面，Spring Cloud Ribbon 使用[Spring Retry](https://www.baeldung.com/spring-retry)的NoBackOffPolicy对象，它什么也不做。

但是，我们可以通过扩展RibbonLoadBalancedRetryFactory类来覆盖默认行为：

```java
@Component
private class CustomRibbonLoadBalancedRetryFactory 
  extends RibbonLoadBalancedRetryFactory {

    public CustomRibbonLoadBalancedRetryFactory(
      SpringClientFactory clientFactory) {
        super(clientFactory);
    }

    @Override
    public BackOffPolicy createBackOffPolicy(String service) {
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000);
        return fixedBackOffPolicy;
    }
}
```

FixedBackOffPolicy类在重试尝试之间提供固定延迟。如果我们不设置退避期，则默认为 1 秒。

或者，我们可以设置ExponentialBackOffPolicy或ExponentialRandomBackOffPolicy：

```java
@Override
public BackOffPolicy createBackOffPolicy(String service) {
    ExponentialBackOffPolicy exponentialBackOffPolicy = 
      new ExponentialBackOffPolicy();
    exponentialBackOffPolicy.setInitialInterval(1000);
    exponentialBackOffPolicy.setMultiplier(2); 
    exponentialBackOffPolicy.setMaxInterval(10000);
    return exponentialBackOffPolicy;
}
```

在这里，尝试之间的初始延迟是 1 秒。然后，在不超过 10 秒的情况下，每次后续尝试的延迟都会加倍：1000 毫秒、2000 毫秒、4000 毫秒、8000 毫秒、10000 毫秒、10000 毫秒……

此外，ExponentialRandomBackOffPolicy在不超过下一个值的情况下为每个睡眠周期添加一个随机值。因此，它可能会产生 1500 毫秒、3400 毫秒、6200 毫秒、9800 毫秒、10000 毫秒、10000 毫秒……

选择一个或另一个取决于我们有多少流量以及有多少不同的客户服务。从固定到随机，这些策略帮助我们更好地传播流量峰值，也意味着更少的重试。例如，对于许多客户端，随机因素有助于避免多个客户端在重试时同时访问服务。

## 六. 总结

在本文中，我们学习了如何使用 Spring Cloud Netflix Ribbon 在我们的 Spring Cloud 应用程序中重试失败的请求。我们还讨论了该机制提供的好处。

接下来，我们演示了重试逻辑如何通过由两个Spring Boot服务支持的 REST 应用程序工作。Spring Cloud Netflix Ribbon 通过利用 Spring Retry 库使这成为可能。

最后，我们了解了如何在重试尝试之间配置不同类型的延迟。