## 1. 简介

Netflix [Ribbon](https://github.com/Netflix/ribbon)是一个进程间通信(IPC)云库。Ribbon主要提供客户端负载均衡算法。

除了客户端负载均衡算法外，Ribbon还提供了其他功能：

-   **服务发现集成**：Ribbon负载均衡器在云等动态环境中提供服务发现。Ribbon库中包含与Eureka和Netflix服务发现组件的集成
-   **容错**：Ribbon API可以动态确定服务器是否在实时环境中启动和运行，并且可以检测那些已关闭的服务器
-   **可配置的负载均衡规则**：Ribbon支持开箱即用的RoundRobinRule、AvailabilityFilteringRule、WeightedResponseTimeRule并且还支持定义自定义规则

Ribbon API基于称为“命名客户端”的概念工作。在我们的应用程序配置文件中配置Ribbon时，我们为包含在负载平衡中的服务器列表提供了一个名称。

## 2. 依赖管理

通过将以下依赖项添加到我们的pom.xml，可以将Netflix Ribbon API添加到我们的项目中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|ga|1|spring-cloud-starter-netflix-ribbon)找到最新的库。

## 3. 示例应用

为了了解Ribbon API的工作原理，我们使用Spring RestTemplate构建了一个示例微服务应用程序，并使用Netflix Ribbon API和Spring Cloud Netflix API对其进行了增强。

我们将使用Ribbon的负载平衡策略之一WeightedResponseTimeRule来启用2个服务器之间的客户端负载平衡，这些服务器在我们的应用程序中的配置文件中的命名客户端下定义。

## 4. Ribbon配置

Ribbon API使我们能够配置负载均衡器的以下组件：

-   Rule：指定我们在应用程序中使用的负载平衡规则的逻辑组件
-   Ping：一个组件，它指定了我们用来实时确定服务器可用性的机制
-   ServerList：可以是动态的或静态的。在我们的例子中，我们使用的是静态服务器列表，因此我们直接在应用程序配置文件中定义它们

让我们为库编写一个简单的配置：

```java
public class RibbonConfiguration {

	@Autowired
	IClientConfig ribbonClientConfig;

	@Bean
	public IPing ribbonPing(IClientConfig config) {
		return new PingUrl();
	}

	@Bean
	public IRule ribbonRule(IClientConfig config) {
		return new WeightedResponseTimeRule();
	}
}
```

请注意我们如何使用WeightedResponseTimeRule规则来确定服务器和PingUrl机制来实时确定服务器的可用性。

根据这个规则，每个服务器根据其平均响应时间被赋予一个权重，响应时间越小，权重越小。该规则随机选择一个服务器，其中的可能性由服务器的权重决定。

PingUrl将ping每个URL以确定服务器的可用性。

## 5. application.yml

下面是我们为此示例应用程序创建的application.yml配置文件：

```yaml
spring:
    application:
        name: spring-cloud-ribbon

server:
    port: 8888

ping-server:
    ribbon:
        eureka:
            enabled: false
        listOfServers: localhost:9092,localhost:9999
        ServerListRefreshInterval: 15000
```

在上面的文件中，我们指定：

-   应用程序名称
-   应用程序的端口号
-   服务器列表的命名客户端：“ping-server”
-   通过将eureka: enabled设置为false来禁用Eureka服务发现组件
-   定义了可用于负载平衡的服务器列表，在本例中为2个服务器
-   使用ServerListRefreshInterval配置服务器刷新率

## 6. Ribbon客户端

现在让我们设置主应用程序组件片段-我们使用RibbonClient来启用负载平衡而不是普通的RestTemplate：

```java
@SpringBootApplication
@RestController
@RibbonClient(name = "ping-a-server", configuration = RibbonConfiguration.class)
public class ServerLocationApp {

	@Autowired
	RestTemplate restTemplate;

	@RequestMapping("/server-location")
	public String serverLocation() {
		return this.restTemplate.getForObject("http://ping-server/locaus", String.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ServerLocationApp.class, args);
	}
}
```

这是RestTemplate配置：

```java
@Configuration
public class RestTemplateConfiguration{
	@LoadBalanced
	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
```

我们使用注解@RestController定义了一个控制器类；我们还使用带有名称和配置类的@RibbonClient对类进行了标注。

我们在此处定义的配置类与我们之前定义的类相同，我们在其中为该应用程序提供了所需的Ribbon API配置。

请注意，我们用@LoadBalanced标注了RestTemplate，这表明我们希望它是负载平衡的，在本例中是使用Ribbon。

## 7. Ribbon中的故障弹性

正如我们在本文前面讨论的那样，Ribbon API不仅提供客户端负载平衡算法，而且还内置了故障恢复能力。

如前所述，Ribbon API可以通过定期对服务器进行持续ping来确定服务器的可用性，并具有跳过不活跃的服务器的能力。

除此之外，它还实现了断路器模式以根据指定条件过滤掉服务器。

断路器模式通过迅速拒绝对失败的服务器的请求而不等待超时来最大限度地减少服务器故障对性能的影响。我们可以通过将属性niws.loadbalancer.availabilityFilteringRule.filterCircuitTripped设置为false来禁用此断路器功能。

当所有服务器都关闭时，因此没有服务器可用于为请求提供服务，pingUrl()将失败并且我们收到异常java.lang.IllegalStateException并显示消息“No instances are available to serve the request”。

## 8. 总结

在本文中，我们讨论了Netflix Ribbon API及其在一个简单的示例应用程序中的实现。