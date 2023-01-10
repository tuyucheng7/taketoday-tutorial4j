## 1. 概述

在本教程中，我们将介绍 Spring Cloud Netflix Hystrix——容错库。我们将使用该库并实施断路器企业模式，该模式描述了一种针对应用程序中不同级别的故障级联的策略。

其原理类似于电子设备：[Hystrix](https://www.baeldung.com/introduction-to-hystrix)正在监视对相关服务调用失败的方法。如果出现此类故障，它将打开电路并将调用转发给回退方法。

该库将容忍达到阈值的故障。除此之外，它使电路保持打开状态。这意味着，它将所有后续调用转发给回退方法，以防止将来发生故障。这为相关服务从其失败状态中恢复创建了一个时间缓冲区。

## 2.REST生产者

要创建一个演示断路器模式的场景，我们首先需要一个服务。我们将其命名为“REST Producer”，因为它为支持 Hystrix 的“REST Consumer”提供数据，我们将在下一步中创建它。

[让我们使用spring-boot-starter-web](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-web")依赖创建一个新的 Maven 项目：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>


```

该项目本身有意保持简单。它由一个控制器接口和一个带有[@RequestMapping](https://www.baeldung.com/spring-requestmapping)注解的 GET 方法返回简单的字符串、一个实现此接口的 @RestController 和一个@SpringBootApplication[组成](https://www.baeldung.com/spring-boot-application-configuration)。

我们将从界面开始：

```java
public interface GreetingController {
    @GetMapping("/greeting/{username}")
    String greeting(@PathVariable("username") String username);
}
```

和实施：

```java
@RestController
public class GreetingControllerImpl implements GreetingController {
 
    @Override
    public String greeting(@PathVariable("username") String username) {
        return String.format("Hello %s!n", username);
    }
}
```

接下来，我们将写下主要的应用程序类：

```java
@SpringBootApplication
public class RestProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestProducerApplication.class, args);
    }
}
```

要完成本节，剩下要做的唯一一件事就是配置我们将在其上侦听的应用程序端口。我们不会使用默认端口 8080，因为该端口应为下一步中描述的应用程序保留。

此外，我们正在定义一个应用程序名称，以便能够从稍后介绍的客户端应用程序中查找我们的生产者。

然后让我们在application.properties文件中指定端口9090和rest-producer的名称：

```plaintext
server.port=9090
spring.application.name=rest-producer
```

现在我们可以使用 cURL 测试我们的生产者：

```bash
$> curl http://localhost:9090/greeting/Cid
Hello Cid!
```

## 3. 使用 Hystrix 的 REST 消费者

对于我们的演示场景，我们将实现一个 Web 应用程序，它使用RestTemplate和Hystrix使用上一步中的 REST 服务。为了简单起见，我们将其称为“REST 消费者”。

因此，我们创建一个新的 Maven 项目，其中包含[spring-cloud- starter- hystrix ](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.cloud" AND a%3A"spring-cloud-starter-hystrix")、[spring-boot-starter-web](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-web")和[spring-boot-starter-thymeleaf](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-thymeleaf")作为依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix</artifactId>
    <version>1.4.7.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
```

为了使断路器工作，Hystix 将扫描[@Component或@Service](https://www.baeldung.com/spring-bean-annotations)注解类以查找@HystixCommand注解方法，为其实现代理并监视其调用。

我们将首先创建一个@Service类，它将被注入到@Controller中。由于我们正在[使用 Thymeleaf 构建 Web 应用程序，因此](https://www.baeldung.com/thymeleaf-in-spring-mvc)我们还需要一个 HTML 模板作为视图。

这将是我们的可注入@Service实现一个带有关联回退方法的@HystrixCommand 。此回退必须使用与原始签名相同的签名：

```java
@Service
public class GreetingService {
    @HystrixCommand(fallbackMethod = "defaultGreeting")
    public String getGreeting(String username) {
        return new RestTemplate()
          .getForObject("http://localhost:9090/greeting/{username}", 
          String.class, username);
    }
 
    private String defaultGreeting(String username) {
        return "Hello User!";
    }
}
```

RestConsumerApplication将是我们的主要应用程序类。@EnableCircuitBreaker注解将扫描类路径以查找任何兼容的断路器实现。

要显式使用 Hystrix，我们必须使用@EnableHystrix注解此类：

```java
@SpringBootApplication
@EnableCircuitBreaker
public class RestConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestConsumerApplication.class, args);
    }
}
```

我们将使用我们的GreetingService设置控制器：

```java
@Controller
public class GreetingController {
 
    @Autowired
    private GreetingService greetingService;
 
    @GetMapping("/get-greeting/{username}")
    public String getGreeting(Model model, @PathVariable("username") String username) {
        model.addAttribute("greeting", greetingService.getGreeting(username));
        return "greeting-view";
    }
}
```

这是 HTML 模板：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <head>
        <title>Greetings from Hystrix</title>
    </head>
    <body>
        <h2 th:text="${greeting}"/>
    </body>
</html>
```

为确保应用程序正在侦听定义的端口，我们将以下内容放入application.properties文件中：

```plaintext
server.port=8080
```

要查看运行中的 Hystix 断路器，我们启动消费者并将浏览器指向[http://localhost:8080/get-greeting/Cid](http://localhost:8080/get-greeting/Cid)。一般情况下，会显示如下：

```plaintext
Hello Cid!
```

为了模拟我们的生产者的失败，我们将简单地停止它，并且在我们刷新浏览器之后我们应该看到一条通用消息，从我们的@Service中的回退方法返回：

```plaintext
Hello User!
```

## 4. 使用 Hystrix 和 Feign 的 REST 消费者

现在，我们将修改上一步中的项目，以使用 Spring Netflix Feign 作为声明式 REST 客户端，而不是 Spring RestTemplate。

优点是我们以后可以轻松重构我们的 Feign Client 接口，以使用[Spring Netflix Eureka](https://www.baeldung.com/spring-cloud-netflix-eureka)进行服务发现。

要启动新项目，我们将我们的消费者，并将我们的生产者和[spring-cloud-starter-feign](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.cloud" AND a%3A"spring-cloud-starter-feign")添加为依赖项：

```xml
<dependency>
    <groupId>com.baeldung.spring.cloud</groupId>
    <artifactId>spring-cloud-hystrix-rest-producer</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
    <version>1.1.5.RELEASE</version>
</dependency>
```

现在，我们可以使用我们的GreetingController来扩展一个 Feign Client。我们将Hystrix回退实现为一个用@Component注解的静态内部类。

或者，我们可以定义一个带 @Bean注解的方法，返回此后备类的实例。

@FeignClient的 name 属性是强制性的。它用于通过 Eureka 客户端通过服务发现或通过 URL 查找应用程序，如果给出此属性：

```java
@FeignClient(
  name = "rest-producer"
  url = "http://localhost:9090", 
  fallback = GreetingClient.GreetingClientFallback.class
)
public interface GreetingClient extends GreetingController {
     
    @Component
    public static class GreetingClientFallback implements GreetingController {
 
        @Override
        public String greeting(@PathVariable("username") String username) {
            return "Hello User!";
        }
    }
}
```

有关使用 Spring Netflix Eureka 进行服务发现的更多信息，请查看[本文](https://www.baeldung.com/spring-cloud-netflix-eureka)。

在RestConsumerFeignApplication中，我们将添加一个额外的注解来启用 Feign 集成，实际上是@EnableFeignClients到主应用程序类：

```java
@SpringBootApplication
@EnableCircuitBreaker
@EnableFeignClients
public class RestConsumerFeignApplication {
     
    public static void main(String[] args) {
        SpringApplication.run(RestConsumerFeignApplication.class, args);
    }
}
```

我们将修改控制器以使用自动连接的 Feign 客户端，而不是之前注入的@Service来检索我们的问候语：

```java
@Controller
public class GreetingController {
    @Autowired
    private GreetingClient greetingClient;
 
    @GetMapping("/get-greeting/{username}")
    public String getGreeting(Model model, @PathVariable("username") String username) {
        model.addAttribute("greeting", greetingClient.greeting(username));
        return "greeting-view";
    }
}
```

为了将此示例与之前的示例区分开来，我们将更改 application.properties 中的应用程序侦听端口：

```plaintext
server.port=8082
```

最后，我们将像上一节中的那样测试这个支持 Feign 的消费者。预期的结果应该是一样的。

## 5. 使用Hystrix缓存回退

现在，我们要将 Hystrix 添加到我们的[Spring Cloud](https://www.baeldung.com/spring-cloud-securing-services)项目中。在这个云项目中，我们有一个与数据库对话并获得书籍评级的评级服务。

让我们假设我们的数据库是一种需求不足的资源，它的响应延迟可能会随时间变化或可能有时不可用。我们将使用 Hystrix 断路器回退到数据缓存来处理这种情况。

### 5.1. 设置和配置

让我们将[spring-cloud-starter-hystrix](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.cloud" AND a%3A"spring-cloud-starter-hystrix")依赖项添加到我们的评级模块：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
```

当在数据库中插入/更新/删除评分时，我们将使用Repository将其到 Redis 缓存中。要了解有关 Redis 的更多信息，请查看[本文。](https://www.baeldung.com/spring-data-redis-tutorial)

让我们更新RatingService以使用@HystrixCommand将数据库查询方法包装在 Hystrix 命令中，并将其配置为回退以从 Redis 读取：

```java
@HystrixCommand(
  commandKey = "ratingsByIdFromDB", 
  fallbackMethod = "findCachedRatingById", 
  ignoreExceptions = { RatingNotFoundException.class })
public Rating findRatingById(Long ratingId) {
    return Optional.ofNullable(ratingRepository.findOne(ratingId))
      .orElseThrow(() -> 
        new RatingNotFoundException("Rating not found. ID: " + ratingId));
}

public Rating findCachedRatingById(Long ratingId) {
    return cacheRepository.findCachedRatingById(ratingId);
}
```

请注意，回退方法应具有与包装方法相同的签名，并且必须位于同一类中。现在，当findRatingById失败或延迟超过给定阈值时，Hystrix 回退到 findCachedRatingById。

由于 Hystrix 功能作为 AOP 建议透明地注入，我们必须调整建议堆叠的顺序，以防万一我们有其他建议，如 Spring 的事务性建议。这里我们将 Spring 的事务 AOP 建议调整为比 Hystrix AOP 建议具有更低的优先级：

```java
@EnableHystrix
@EnableTransactionManagement(
  order=Ordered.LOWEST_PRECEDENCE, 
  mode=AdviceMode.ASPECTJ)
public class RatingServiceApplication {
    @Bean
    @Primary
    @Order(value=Ordered.HIGHEST_PRECEDENCE)
    public HystrixCommandAspect hystrixAspect() {
        return new HystrixCommandAspect();
    }
 
    // other beans, configurations
}
```

在这里，我们将 Spring 的事务 AOP 建议调整为比 Hystrix AOP 建议具有更低的优先级。

## 5.2. 测试 Hystrix 回退

现在我们已经配置了电路，我们可以通过关闭与我们的存储库交互的 H2 数据库来测试它。但首先，让我们将 H2 实例作为外部进程运行，而不是将其作为嵌入式数据库运行。

让我们将 H2 库 ( h2-1.4.193.jar ) 到已知目录并启动 H2 服务器：

```bash
>java -cp h2-1.4.193.jar org.h2.tools.Server -tcp
TCP server running at tcp://192.168.99.1:9092 (only local connections)
```

现在让我们在rating-service.properties中更新模块的数据源 URL以指向此 H2 服务器：

```bash
spring.datasource.url = jdbc:h2:tcp://localhost/~/ratings
```

我们可以启动我们之前在 Spring Cloud 系列[文章](https://www.baeldung.com/spring-cloud-bootstrapping)中给出的服务，并通过关闭我们正在运行的外部 H2 实例来测试每本书的评分。

我们可以看到，当 H2 数据库无法访问时，Hystrix 会自动回退到 Redis 来读取每本书的评分。可以在[此处](https://github.com/eugenp/tutorials/tree/master/spring-cloud-modules/spring-cloud-bootstrap)找到演示此用例的源代码。

## 6. 使用范围

通常，@HytrixCommand注解方法在线程池上下文中执行。但有时它需要在本地范围内运行，例如@SessionScope或@RequestScope。这可以通过给命令注解提供参数来完成：

```java
@HystrixCommand(fallbackMethod = "getSomeDefault", commandProperties = {
  @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
})
```

## 7. Hystrix 仪表板

Hystrix 的一个不错的可选功能是能够在仪表板上监视其状态。

为了启用它，我们将把[spring-cloud-starter-hystrix-dashboard](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.cloud" AND a%3A"spring-cloud-starter-hystrix-dashboard")和[spring-boot-starter-actuator](https://search.maven.org/classic/#search|gav|1|g%3A"org.springframework.boot" AND a%3A"spring-boot-starter-actuator")放在消费者的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
    <version>1.4.7.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
```

前者需要通过使用@EnableHystrixDashboard注解@Configuration来启用，后者会自动在我们的 Web 应用程序中启用所需的指标。

重启应用程序后，我们将浏览器指向http://localhost:8080/hystrix，输入 Hystrix 流的指标 URL 并开始监控。

最后，我们应该看到这样的东西：

[![截图_20160819_031730](https://www.baeldung.com/wp-content/uploads/2016/09/Screenshot_20160819_031730-268x300-1-268x300.png)](https://www.baeldung.com/wp-content/uploads/2016/09/Screenshot_20160819_031730-268x300-1.png)

监视一个 Hystrix 流是一件好事，但是如果我们必须监视多个启用 Hystrix 的应用程序，它就会变得不方便。为此，Spring Cloud 提供了一个名为 Turbine 的工具，它可以聚合流以呈现在一个 Hystrix 仪表板中。

配置 Turbine 超出了本文的范围，但这里应该提到这种可能性。因此，也可以使用 Turbine 流通过消息传递来收集这些流。

## 八. 总结

正如我们目前所见，我们现在能够使用 Spring Netflix Hystrix 以及 Spring RestTemplate或 Spring Netflix Feign 来实现断路器模式。

这意味着我们能够使用默认数据使用包含回退的服务，并且我们能够监控此数据的使用情况。