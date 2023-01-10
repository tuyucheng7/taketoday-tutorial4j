## 1. 概述

Spring Cloud通过使用Netflix Ribbon提供客户端负载均衡。Ribbon的负载均衡机制可以通过重试来扩展。

在本教程中，我们将探索这种重试机制。

首先，我们将了解为什么在构建我们的应用程序时需要牢记这一特性是很重要的。
然后，我们将使用Spring Cloud Netflix Ribbon构建和配置一个应用程序来演示该机制。

## 2. 动机

在基于云的应用程序中，服务向其他服务发出请求是一种常见做法。但在这样一个动态多变的环境中，网络可能会出现故障或服务可能暂时不可用。

我们希望以优雅的方式处理故障并快速恢复。在许多情况下，这些问题是短暂的。如果我们在失败发生后一段时间重复相同的请求，也许它会成功。

这种做法有助于我们提高应用程序的弹性，这是可靠的云应用程序的关键方面之一。

然而，我们需要密切关注重试，因为它们也可能导致糟糕的情况。例如，它们会增加可能不希望出现的延迟。

## 3. 项目构建

为了演示重试机制，我们需要两个Spring Boot服务。首先，我们将创建一个Weather服务，它将通过REST API显示今天的天气信息。

其次，我们将定义一个使用Weather服务的客户端服务。

### 3.1 Weather服务

让我们构建一个非常简单的Weather服务，它有时会失败，并返回503 HTTP状态码(服务不可用)。
我们将通过在调用次数是success.call.divisor属性的倍数时选择返回503状态码，来模拟这种间歇性失败：

```java

@RestController
public class WeatherController {
  private static final Logger LOGGER = LoggerFactory.getLogger(WeatherController.class);
  private int numberOfCalls = 0;

  @Value("${successful.call.divisor}")
  private int divisor;

  @GetMapping("/")
  public String health() {
    return "I am Ok";
  }

  @GetMapping("/weather")
  public ResponseEntity<String> weather() {
    LOGGER.info("Providing today's weather information");
    if (isServiceUnavailable())
      return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    LOGGER.info("Today's a sunny day");
    return new ResponseEntity<>("Today's a sunny day", HttpStatus.OK);
  }

  private boolean isServiceUnavailable() {
    return ++numberOfCalls % divisor != 0;
  }
}
```

此外，为了帮助我们观察对服务进行的重试次数，我们添加日志记录。

稍后，我们将配置客户端服务以在Weather服务暂时不可用时触发重试机制。

### 3.2 Client服务

我们的第二个服务将使用Spring Cloud Netflix Ribbon。

首先，让我们定义Ribbon 客户端配置：

```java

@Configuration
@RibbonClient(name = "weather-service", configuration = RibbonConfiguration.class)
public class WeatherClientRibbonConfiguration {

  @Bean
  @LoadBalanced
  RestTemplate getRestTemplate() {
    return new RestTemplate();
  }
}
```

我们的HTTP客户端使用@LoadBalanced注解，这意味着我们希望它使用Ribbon进行负载均衡。

现在，我们将通过定义上面@RibbonClient注解中包含的RibbonConfiguration类来添加一个ping机制来确定weather服务的可用性，以及一个负载均衡策略：

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

接下来，我们需要从Ribbon客户端关闭Eureka，因为我们没有使用服务发现。相反，我们使用手动定义的可用于负载均衡的Weather服务实例列表。

所以，让我们也将这些添加到application.yml文件中：

```yaml
weather-service:
  ribbon:
    eureka:
      enabled: false
    listOfServers: http://localhost:8021, http://localhost:8022
```

最后，让我们构建一个控制器并让它调用Weather服务：

```java

@RestController
public class RibbonClientController {
  private static final String WEATHER_SERVICE = "weather-service";

  @Autowired
  private RestTemplate restTemplate;

  @GetMapping("/client/weather")
  public String weather() {
    String result = restTemplate.getForObject("http://" + WEATHER_SERVICE + "/weather", String.class);
    return "Weather Service Response: " + result;
  }
}
```

## 4. 启用重试机制

### 4.1 配置application.yml属性

我们需要将weather-service属性放在客户端应用程序的application.yml文件中：

```yaml
weather-service:
  ribbon:
    MaxAutoRetries: 3
    MaxAutoRetriesNextServer: 1
    OkToRetryOnAllOperations: true
    retryableStatusCodes: 503, 408
```

上面的配置使用了我们需要定义的标准Ribbon属性来启用重试：

+ MaxAutoRetries - 在同一台服务器上重试失败请求的次数(默认 0)。
+ MaxAutoRetriesNextServer – 尝试排除第一个服务的服务器数量(默认 0)。
+ retryableStatusCodes – 对返回的哪些HTTP状态码进行重试。
+ OkToRetryOnAllOperations – 当此属性设置为true时，将重试所有类型的HTTP请求，而不仅仅是GET请求(默认)。

当客户端服务收到503(服务不可用)或408(请求超时)响应码时，我们将重试失败的请求。

### 4.2 必需的依赖

Spring Cloud Netflix Ribbon使用Spring Retry重试失败的请求。

我们必须确保包含此依赖，否则，失败的请求将不会被重试。我们可以省略版本声明，因为它是由Spring Boot管理的：

```
<dependency>
  <groupId>org.springframework.retry</groupId>
  <artifactId>spring-retry</artifactId>
</dependency>
```

### 4.3 实践中的重试逻辑

最后，我们来看看实践中的重试逻辑。

出于这个原因，我们需要两个Weather服务实例，我们将在8021和8022端口上运行它们。
当然，这些实例应该匹配上一节中定义的listOfServers属性配置。

此外，我们需要在每个实例上配置successful.call.divisor属性，以确保我们的模拟服务在不同时间失败：

```
successful.call.divisor = 5 // instance 1
successful.call.divisor = 2 // instance 2
```

接下来，让我们在8080端口上运行客户端服务并调用：

```
http://localhost:8080/client/weather
```

让我们看一下Weather服务的控制台：

```
weather service instance 1:
    Providing today's weather information
    Providing today's weather information
    Providing today's weather information
    Providing today's weather information

weather service instance 2:
    Providing today's weather information
    Today's a sunny day
```

因此，经过几次重试(实例1 上4次，实例 2上2次)后，我们得到了有效响应。

