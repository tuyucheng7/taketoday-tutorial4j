## 1. 简介

随着微服务架构变得越来越流行，运行分布在不同服务器上的多个服务变得越来越普遍。在本快速教程中，**我们将了解如何使用[Spring Cloud Load Balancer](https://spring.io/guides/gs/spring-cloud-loadbalancer/)创建更多容错应用程序**。

## 2. 什么是负载均衡？

**负载均衡是在同一应用程序的不同实例之间分配流量的过程**。

要创建容错系统，通常会运行每个应用程序的多个实例。因此，每当一个服务需要与另一个服务通信时，它都需要选择一个特定的实例来发送它的请求。

在负载平衡方面，有许多算法：

-   随机选择：随机选择一个实例
-   轮询：每次以相同的顺序选择一个实例
-   最少连接数：选择当前连接数最少的实例
-   加权指标：使用加权指标来选择最佳实例(例如，CPU或内存使用率)
-   IP哈希：使用客户端IP的哈希映射到实例

这些只是负载均衡算法的几个例子，**每种算法都有其优点和缺点**。

随机选择和轮询很容易实现，但可能无法以最佳方式使用服务。相反，最少连接和加权指标更复杂，但通常会创建更优化的服务利用率。当服务器粘性很重要时，IP哈希非常有用，但它的容错能力不是很强。

## 3. Spring Cloud Load Balancer简介

**Spring Cloud Load Balancer库允许我们创建以负载均衡方式与其他应用程序通信的应用程序**。使用我们想要的任何算法，我们可以在进行远程服务调用时轻松实现负载平衡。

为了说明这一点，让我们看一些示例代码。我们将从一个简单的服务器应用程序开始。服务器将具有单个HTTP端点，并且可以作为多个实例运行。

然后，我们将创建一个客户端应用程序，该应用程序使用Spring Cloud Load Balancer在服务器的不同实例之间交替请求。

### 3.1 示例服务器

对于我们的示例服务器，我们从一个简单的Spring Boot应用程序开始：

```java
@SpringBootApplication
@RestController
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Value("${server.instance.id}")
    String instanceId;

    @GetMapping("/hello")
    public String hello() {
        return String.format("Hello from instance %s", instanceId);
    }
}
```

我们首先注入一个名为instanceId的可配置变量。这使我们能够区分多个正在运行的实例。接下来，我们添加一个HTTP GET端点来回显消息和实例ID。

默认实例将在ID为1的端口8080上运行。**要运行第二个实例，我们只需要添加几个程序参数**：

```shell
--server.instance.id=2 --server.port=8081
```

### 3.2 示例客户端

现在，让我们看一下客户端代码。**这是我们使用Spring Cloud Load Balancer的地方**，所以让我们首先将其包含在我们的应用程序中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

接下来，**我们创建ServiceInstanceListSupplier的实现。这是Spring Cloud Load Balancer中的关键接口之一**。它定义了我们如何找到可用的服务实例。

对于我们的示例应用程序，我们将对示例服务器的两个不同实例进行硬编码。它们在同一台机器上运行但使用不同的端口：

```java
class DemoInstanceSupplier implements ServiceInstanceListSupplier {
    private final String serviceId;

    public DemoInstanceSupplier(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return Flux.just(Arrays
              .asList(new DefaultServiceInstance(serviceId + "1", serviceId, "localhost", 8080, false),
                    new DefaultServiceInstance(serviceId + "2", serviceId, "localhost", 8081, false)));
    }
}
```

在真实世界的系统中，我们希望使用不对服务地址进行硬编码的实现。稍后我们将对此进行更多讨论。

现在，让我们创建一个LoadBalancerConfiguration类：

```java
@Configuration
@LoadBalancerClient(name = "example-service", configuration = DemoServerInstanceConfiguration.class)
class WebClientConfig {
    @LoadBalanced
    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
```

这个类有一个作用：创建一个负载平衡的WebClient构建器来发出远程请求。**请注意，我们的注解使用了服务的伪名称**。

这是因为我们可能无法提前知道运行实例的实际主机名和端口。因此，我们使用伪名称作为占位符，框架将在选择运行实例时替换真实值。

接下来，让我们创建一个Configuration类来实例化我们的服务实例供应商。请注意，我们使用与上面相同的伪名称：

```java
@Configuration
class DemoServerInstanceConfiguration {
    @Bean
    ServiceInstanceListSupplier serviceInstanceListSupplier() {
        return new DemoInstanceSupplier("example-service");
    }
}
```

现在，我们可以创建实际的客户端应用程序。让我们使用上面的WebClient bean向示例服务器发送10个请求：

```java
@SpringBootApplication
public class ClientApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(ClientApplication.class)
              .web(WebApplicationType.NONE)
              .run(args);

        WebClient loadBalancedClient = ctx.getBean(WebClient.Builder.class).build();

        for(int i = 1; i <= 10; i++) {
            String response = loadBalancedClient.get().uri("http://example-service/hello")
                        .retrieve().toEntity(String.class)
                        .block().getBody();
            System.out.println(response);
        }
    }
}
```

查看输出，我们可以确认我们在两个不同的实例之间进行负载均衡：

```shell
Hello from instance 2
Hello from instance 1
Hello from instance 2
Hello from instance 1
Hello from instance 2
Hello from instance 1
Hello from instance 2
Hello from instance 1
Hello from instance 2
Hello from instance 1
```

## 4. 其他功能

**示例服务器和客户端显示了Spring Cloud Load Balancer的非常简单的使用**。但其他库功能也值得一提。

对于初学者，示例客户端使用默认的RoundRobinLoadBalancer策略。该库还提供了一个RandomLoadBalancer类。我们还可以使用我们想要的任何算法创建我们自己的ReactorServiceInstanceLoadBalancer实现。

此外，该库还提供了一种**动态发现服务实例**的方法。我们使用DiscoveryClientServiceInstanceListSupplier接口执行此操作。这对于与[Eureka](https://www.baeldung.com/spring-cloud-netflix-eureka)或[Zookeeper](https://www.baeldung.com/java-zookeeper)等服务发现系统集成非常有用。

除了不同的负载平衡和服务发现功能外，该库还提供基本的重试功能。在幕后，它最终依赖于[Spring Retry](https://www.baeldung.com/spring-retry)库。**这允许我们重试失败的请求**，可能在等待一段时间后使用相同的实例。

另一个内置功能是指标，它建立在[Micrometer](https://www.baeldung.com/micrometer)库之上。开箱即用，我们可以获得每个实例的基本服务水平指标，但我们也可以添加自己的指标。

最后，Spring Cloud Load Balancer库提供了一种使用LoadBalancerCacheManager接口来缓存服务实例的方法。这很重要，因为在现实中，**查找可用的服务实例可能涉及远程调用**。这意味着查找不经常更改的数据可能代价高昂，而且它还代表应用程序中可能出现的故障点。**通过使用服务实例的缓存，我们的应用程序可以解决其中的一些缺点**。

## 5. 总结

负载均衡是构建现代容错系统的重要组成部分。使用Spring Cloud Load Balancer，我们可以轻松创建使用各种负载均衡技术将请求分发到不同服务实例的应用程序。