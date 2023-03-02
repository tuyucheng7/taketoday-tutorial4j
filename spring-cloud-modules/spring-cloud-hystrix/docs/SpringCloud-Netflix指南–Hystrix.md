## 1. 概述

在本教程中，我们将介绍Spring Cloud Netflix Hystrix-容错库。我们将使用该库并实施断路器企业模式，该模式描述了一种针对应用程序中不同级别的故障级联的策略。

其原理类似于电子设备：[Hystrix](https://www.baeldung.com/introduction-to-hystrix)正在监视对相关服务调用失败的方法。如果出现此类故障，它将打开电路并将调用转发给回退方法。

该库将容忍不超过阈值的故障。除此之外，它使电路保持打开状态。这意味着，它将所有后续调用转发给回退方法，以防止将来发生故障。**这为相关服务从其失败状态中恢复创建了一个时间缓冲区**。

## 2. REST生产者

要创建一个演示断路器模式的场景，我们首先需要一个服务。我们将其命名为“REST Producer”，因为它为支持Hystrix的“REST Consumer”提供数据，我们将在下一步中创建该消费者。

让我们使用[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.3)依赖创建一个新的Maven项目：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>2.2.6.RELEASE</version>
</dependency>
```

该项目本身有意保持简单。它由一个控制器接口和一个带有[@RequestMapping](https://www.baeldung.com/spring-requestmapping)注解的GET方法(该方法只返回一个字符串)组成、一个实现此接口的@RestController和一个[@SpringBootApplication](https://www.baeldung.com/spring-boot-application-configuration)组成。

我们将从接口开始：

```java
public interface GreetingController {
    @GetMapping("/greeting/{username}")
    String greeting(@PathVariable("username") String username);
}
```

和实现：

```java
@RestController
public class GreetingControllerImpl implements GreetingController {

    @Override
    public String greeting(@PathVariable("username") String username) {
        return String.format("Hello %s!\n", username);
    }
}
```

接下来，创建我们的主应用程序类：

```java
@SpringBootApplication
public class RestProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestProducerApplication.class, args);
    }
}
```

要完成本节，剩下要做的唯一一件事就是配置我们将在其上监听的应用程序端口。我们不会使用默认端口8080，因为该端口应保留用于下一步中所述的应用程序。

此外，我们正在定义一个应用程序名称，以便能够从稍后介绍的客户端应用程序中查找我们的生产者。

然后让我们在application.properties文件中指定端口9090和rest-producer的名称：

```properties
server.port=9090
spring.application.name=rest-producer
```

现在我们可以使用cURL测试我们的生产者：

```shell
$> curl http://localhost:9090/greeting/Cid
Hello Cid!
```

## 3. 使用Hystrix的REST消费者

对于我们的演示场景，我们将实现一个Web应用程序，该应用程序使用RestTemplate和Hystrix消费上一步中的REST服务。为了简单起见，我们将其称为“REST Consumer”。

因此，我们创建一个新的Maven项目，其中包含[spring-cloud-starter-hystrix](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-hystrix/1.4.7.RELEASE)、[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.3)和[spring-boot-starter-thymeleaf](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-thymeleaf/3.0.3)作为依赖项：

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

为了使断路器工作，Hystrix将扫描[@Component或@Service](https://www.baeldung.com/spring-bean-annotations)注解类以查找@HystixCommand注解方法，为其实现代理并监视其调用。

我们将首先创建一个@Service类，该类将被注入到@Controller中。由于我们正在[使用Thymeleaf构建Web应用程序](https://www.baeldung.com/thymeleaf-in-spring-mvc)，因此我们还需要一个HTML模板作为视图。

这将是我们的可注入@Service实现一个带有关联回退方法的@HystrixCommand。此回退必须使用与原始签名相同的签名：

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

RestConsumerApplication将是我们的主应用程序类。@EnableCircuitBreaker注解将扫描类路径以查找任何兼容的断路器实现。

要显式使用Hystrix，我们必须使用@EnableHystrix标注这个类：

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

这是HTML模板：

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

为了确保应用程序正在监听定义的端口，我们将以下内容放入application.properties文件中：

```properties
server.port=8080
```

为了看到Hystrix断路器的实际运行情况，我们启动消费者并将浏览器指向[http://localhost:8080/get-greeting/Cid](http://localhost:8080/get-greeting/Cid)。一般情况下，会显示以下内容：

```html
Hello Cid!
```

为了模拟我们的生产者的失败，我们只需简单地停止它，并且在我们刷新浏览器之后我们应该看到一条通用消息，从我们的@Service中的回退方法返回：

```html
Hello User!
```

## 4. 使用Hystrix和Feign的REST消费者

现在，我们将修改上一步中的项目，以使用Spring Netflix Feign作为声明式REST客户端，而不是Spring RestTemplate。

优点是我们以后可以轻松重构我们的Feign Client接口，以使用[Spring Netflix Eureka](https://www.baeldung.com/spring-cloud-netflix-eureka)进行服务发现。

为了启动新项目，我们将复制我们的消费者，并将我们的生产者和[spring-cloud-starter-feign](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-feign/1.4.7.RELEASE)添加为依赖项：

```xml
<dependency>
    <groupId>cn.tuyucheng.taketoday.spring.cloud</groupId>
    <artifactId>spring-cloud-hystrix-rest-producer</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
    <version>1.1.5.RELEASE</version>
</dependency>
```

现在，我们可以使用我们的GreetingController来扩展一个Feign Client。我们将Hystrix回退实现为一个用@Component标注的静态内部类。

或者，我们可以定义一个带@Bean注解的方法，返回此回退类的实例。

@FeignClient的name属性是强制性的。如果给出此属性，它用于通过Eureka客户端通过服务发现或通过URL查找应用程序：

```java
@FeignClient(
      name = "rest-producer",
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

有关使用Spring Netflix Eureka进行服务发现的更多信息，请查看[本文](https://www.baeldung.com/spring-cloud-netflix-eureka)。

在RestConsumerFeignApplication中，我们将添加一个额外的注解来启用Feign集成，实际上是将@EnableFeignClients添加到主应用程序类：

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

我们将修改控制器以使用自动注入的Feign客户端，而不是之前注入的@Service来检索我们的问候语：

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

为了将此示例与之前的示例区分开来，我们将更改application.properties中的应用程序监听端口：

```properties
server.port=8082
```

最后，我们将像上一节中的那样测试这个支持Feign的消费者。预期的结果应该是一样的。

## 5. 使用Hystrix缓存回退

现在，我们要将Hystrix添加到我们的[Spring Cloud](https://www.baeldung.com/spring-cloud-securing-services)项目中。在这个云项目中，我们有一个与数据库对话并获得书籍评级的评级服务。

让我们假设我们的数据库是一种需求不足的资源，它的响应延迟可能会随时间变化或可能有时不可用。我们将使用Hystrix断路器回退到数据缓存来处理这种情况。

### 5.1 设置和配置

让我们将[spring-cloud-starter-hystrix](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-hystrix/1.4.7.RELEASE)依赖项添加到我们的评级模块中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
```

当在数据库中插入/更新/删除评级时，我们将使用Repository将其到复制Redis缓存中。要了解有关Redis的更多信息，请查看[本文](https://www.baeldung.com/spring-data-redis-tutorial)。

让我们更新RatingService以使用@HystrixCommand将数据库查询方法包装在Hystrix命令中，并将其配置为回退以从Redis读取：

```java
@HystrixCommand(
    commandKey = "ratingsByIdFromDB", 
    fallbackMethod = "findCachedRatingById", 
    ignoreExceptions = { RatingNotFoundException.class })
public Rating findRatingById(Long ratingId) {
    return Optional.ofNullable(ratingRepository.findOne(ratingId))
        .orElseThrow(() -> new RatingNotFoundException("Rating not found. ID: " + ratingId));
}

public Rating findCachedRatingById(Long ratingId) {
    return cacheRepository.findCachedRatingById(ratingId);
}
```

请注意，回退方法应具有与包装方法相同的签名，并且必须位于同一类中。现在，当findRatingById失败或延迟超过给定阈值时，Hystrix会回退到findCachedRatingById。

由于Hystrix功能作为AOP通知透明地注入，因此我们必须调整通知堆叠的顺序，以防万一我们有其他通知，如Spring的事务性通知。在这里我们将Spring的事务AOP通知调整为比Hystrix AOP通知具有更低的优先级：

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

### 5.2 测试Hystrix回退

现在我们已经配置了电路，我们可以通过关闭与我们的Repository交互的H2数据库来测试它。但首先，让我们将H2实例作为外部进程运行，而不是将其作为嵌入式数据库运行。

让我们将H2库(h2-1.4.193.jar)复制到已知目录并启动H2服务器：

```shell
>java -cp h2-1.4.193.jar org.h2.tools.Server -tcp
TCP server running at tcp://192.168.99.1:9092 (only local connections)
```

现在让我们在rating-service.properties中更新模块的数据源URL以指向此H2服务器：

```properties
spring.datasource.url = jdbc:h2:tcp://localhost/~/ratings
```

我们可以启动我们之前在[Spring Cloud系列文章](https://www.baeldung.com/spring-cloud-bootstrapping)中给出的服务，并通过关闭我们正在运行的外部H2实例来测试每本书的评级。

我们可以看到，当H2数据库无法访问时，Hystrix会自动回退到Redis来读取每本书的评级。可以在[此处](https://github.com/eugenp/tutorials/tree/master/spring-cloud-modules/spring-cloud-bootstrap)找到演示此用例的源代码。

## 6. 使用作用域

通常，@HystrixCommand注解方法在线程池上下文中执行。但有时它需要在本地作用域内运行，例如@SessionScope或@RequestScope。这可以通过为@HystrixCommand注解提供参数来完成：

```java
@HystrixCommand(fallbackMethod = "getSomeDefault", commandProperties = {
    @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
})
```

## 7. Hystrix仪表板

Hystrix的一个不错的可选功能是能够在仪表板上监视其状态。

为了启用它，我们将[spring-cloud-starter-hystrix-dashboard](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-hystrix-dashboard/1.4.7.RELEASE)和[spring-boot-starter-actuator](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-actuator/3.0.3)放在消费者的pom.xml中：

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

前者需要通过使用@EnableHystrixDashboard注解@Configuration来启用，后者会自动在我们的Web应用程序中启用所需的指标。

重新启动应用程序后，我们将浏览器指向http://localhost:8080/hystrix，输入Hystrix流的指标URL并开始监控。

最后，我们应该看到这样的东西：

<img src="../assets/img.png">

监视Hystrix流是一件好事，但是如果我们必须监视多个启用Hystrix的应用程序，它就会变得不方便。为此，Spring Cloud提供了一个名为Turbine的工具，该工具可以聚合流以呈现在一个Hystrix仪表板中。

配置Turbine超出了本文的范围，但这里应该提到这种可能性。因此，也可以使用Turbine流通过消息传递来收集这些流。

## 8. 总结

正如我们目前所见，我们现在能够使用Spring Netflix Hystrix以及Spring RestTemplate或Spring Netflix Feign来实现断路器模式。

这意味着我们能够使用默认数据使用包含回退的服务，并且我们能够监控此数据的使用情况。