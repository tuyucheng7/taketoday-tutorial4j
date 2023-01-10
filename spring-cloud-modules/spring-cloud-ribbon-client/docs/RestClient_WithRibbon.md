## 1. 概述

Netflix Ribbon是一个进程间通信(IPC)云库。Ribbon主要提供客户端负载均衡算法。

除了客户端负载均衡算法，Ribbon还提供了其他功能：

+ 服务发现集成 - Ribbon负载均衡器在云等动态环境中提供服务发现。与Eureka和Netflix服务发现组件的集成包含在Ribbon库中。
+ 容错 - Ribbon API可以动态确定服务器是否在实时环境中启动并运行，并且可以检测那些关闭的服务器。
+ 可配置的负载均衡策略 - Ribbon开箱即用的支持RoundRobinRule、AvailabilityFilteringRule、WeightedResponseTimeRule，还支持定义自定义规则。

Ribbon API基于“Named Client”的概念工作。在我们的application配置文件中配置Ribbon时，我们提供用于负载均衡的服务器列表的名称。

## 2. 依赖管理

通过将以下依赖项添加到我们的pom.xml中，可以将Netflix Ribbon API添加到我们的项目中：

```
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
</dependency>
```

## 3. 示例程序

为了查看Ribbon API的工作原理，我们使用Spring RestTemplate构建一个微服务应用程序，
并使用Netflix Ribbon API和Spring Cloud Netflix API对其进行增强。

我们将使用Ribbon的负载均衡策略之一，WeightedResponseTimeRule，在我们的应用程序中启用2个服务器之间的客户端负载均衡，
这些服务器在配置文件中的命名客户端下定义。

## 4. Ribbon配置

Ribbon API使我们能够配置负载均衡的以下组件：

+ Rule - 指定我们在应用程序中使用的负载均衡规则的逻辑组件。
+ Ping - 用来实时确定服务器可用性的机制。
+ ServerList - 可以是动态的或静态的。在我们的例子中，我们使用的是服务器的静态列表，因此我们直接在应用程序配置文件中定义它们。

让我们编写一个简单的配置：

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

请注意我们如何使用WeightedResponseTimeRule规则来确定服务器，以及PingUrl机制来实时确定服务器的可用性。

根据这个规则，每个服务器根据其平均响应时间被赋予一个权重，响应时间越短，权重越小。该规则随机选择一个服务器，其可能性取决于服务器的权重。

PingUrl将ping每个URL以确定服务器的可用性。

## 5. application.yml

下面是我们为此应用程序创建的application.yml配置文件：

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

+ 应用程序名称
+ 应用程序的端口号
+ 服务器列表的命名客户端：“ping-server”
+ 禁用Eureka服务发现组件，通过设置eureka: enabled为false
+ 定义了可用于负载均衡的服务器列表，在本例中为2个服务器
+ 使用ServerListRefreshInterval配置服务器刷新率

## 6. RibbonClient

现在让我们设置主要的应用程序组件——我们使用RibbonClient来启用负载均衡，而不是普通的RestTemplate：

```java

@SpringBootApplication
@RestController
@RibbonClient(name = "ping-a-server", configuration = RibbonConfiguration.class)
public class ServerLocationApplication {

  @Autowired
  RestTemplate restTemplate;

  public static void main(String[] args) {
    SpringApplication.run(ServerLocationApplication.class, args);
  }

  @RequestMapping("/server-location")
  public String serverLocation() {
    return this.restTemplate.getForObject("http://ping-server/locaus", String.class);
  }
}
```

下面是RestTemplate配置：

```java

@Configuration
public class RestTemplateConfiguration {

  @LoadBalanced
  @Bean
  RestTemplate getRestTemplate() {
    return new RestTemplate();
  }
}
```

我们定义了一个带有注解@RestController的控制器类；我们还用@RibbonClient用名称和配置类标注了该类。

我们在此处定义的配置类与我们之前定义的类相同，在该类中，我们为该应用程序提供了所需的Ribbon API配置。

请注意，我们使用@LoadBalanced标注了RestTemplate，这表明我们希望它是负载均衡的，在这种情况下使用Ribbon。

## 7. Ribbon的故障恢复能力

正如我们在本文前面所讨论的，Ribbon API不仅提供了客户端负载均衡算法，而且还内置了故障恢复能力。

如前所述，Ribbon API可以通过定期对服务器进行持续ping来确定服务器的可用性，并具有跳过不活跃的服务器的能力。

除此之外，它还实现了断路器模式以根据指定的条件过滤掉服务器。

断路器模式通过在不等待超时的情况下迅速拒绝对发生故障的服务器的请求，从而最大限度地减少服务器故障对性能的影响。
我们可以通过将属性niws.loadbalancer.availabilityFilteringRule.filterCircuitTripped设置为false来禁用此断路器功能。

当所有服务器都关闭时，因此没有服务器可用于处理请求，pingUrl()将失败，
我们会收到异常java.lang.IllegalStateException并显示消息“No instances are available to serve the request”。