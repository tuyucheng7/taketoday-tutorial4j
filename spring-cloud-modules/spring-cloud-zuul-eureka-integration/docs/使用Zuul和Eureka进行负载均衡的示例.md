## 1. 概述

在本文中，我们将了解负载均衡如何与Zuul和Eureka一起工作。

**我们将通过Zuul Proxy将请求路由到Spring Cloud Eureka发现的REST服务**。

## 2. 初始设置

我们需要设置Eureka服务器/客户端，如文章[Spring Cloud Netflix Eureka](https://www.baeldung.com/spring-cloud-netflix-eureka)所示。

## 3. 配置Zuul

Zuul，除其他外，从Eureka服务位置获取并进行服务器端负载平衡。

### 3.1 Maven配置

首先，我们将Zuul Server和Eureka依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### 3.2 与Eureka通信

其次，我们将在Zuul的application.properties文件中添加必要的属性：

```properties
server.port=8762
spring.application.name=zuul-server
eureka.instance.preferIpAddress=true
eureka.client.registerWithEureka=true
eureka.client.fetchRegistry=true
eureka.client.serviceUrl.defaultZone=${EUREKA_URI:http://localhost:8761/eureka}

```

在这里，我们告诉Zuul在Eureka中将自己注册为服务并在端口8762上运行。

接下来，我们将使用@EnableZuulProxy和@EnableDiscoveryClient实现主类。@EnableZuulProxy表示这是Zuul服务器，@EnableDiscoveryClient表示这是Eureka客户端：

```java
@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class ZuulConfig {
	public static void main(String[] args) {
		SpringApplication.run(ZuulConfig.class, args);
	}
}
```

我们将浏览器指向[http://localhost:8762/routes](http://localhost:8762/routes)。这应该显示Eureka发现的Zuul可用的所有路由：

```json
{
	"/spring-cloud-eureka-client/**": "spring-cloud-eureka-client"
}
```

现在，我们将使用获得的Zuul Proxy路由与Eureka客户端通信。将我们的浏览器指向[http://localhost:8762/spring-cloud-eureka-client/greeting](http://localhost:8762/spring-cloud-eureka-client/greeting)应该会生成如下响应：

```html
Hello from 'SPRING-CLOUD-EUREKA-CLIENT with Port Number 8081'!
```

## 4. Zuul负载均衡

**当Zuul收到请求时，它会选择一个可用的物理位置并将请求转发到实际的服务实例**。缓存服务实例的位置并将请求转发到实际位置的整个过程是开箱即用的，不需要额外的配置。

在这里，我们可以看到Zuul是如何封装同一服务的三个不同实例的：

<img src="../assets/img.png">

在内部，Zuul使用Netflix Ribbon从服务发现(Eureka Server)中查找服务的所有实例。

让我们观察一下启动多个实例时的行为。

### 4.1 注册多个实例

我们将首先运行两个实例(8081和8082端口)。

一旦所有实例都启动，我们可以在日志中观察到实例的物理位置已在DynamicServerListLoadBalancer中注册，并且路由映射到负责将请求转发到实际实例的Zuul Controller：

```shell
Mapped URL path [/spring-cloud-eureka-client/**] onto handler of type [class org.springframework.cloud.netflix.zuul.web.ZuulController]
Client:spring-cloud-eureka-client instantiated a LoadBalancer:
  DynamicServerListLoadBalancer:{NFLoadBalancer:name=spring-cloud-eureka-client,
  current list of Servers=[],Load balancer stats=Zone stats: {},Server stats: []}ServerList:null
Using serverListUpdater PollingServerListUpdater
DynamicServerListLoadBalancer for client spring-cloud-eureka-client initialized: 
  DynamicServerListLoadBalancer:{NFLoadBalancer:name=spring-cloud-eureka-client,
  current list of Servers=[0.0.0.0:8081, 0.0.0.0:8082],
  Load balancer stats=Zone stats: {defaultzone=[Zone:defaultzone;	Instance count:2;	
  Active connections count: 0;	Circuit breaker tripped count: 0;	
  Active connections per server: 0.0;]},
  Server stats: 
    [[Server:0.0.0.0:8080;	Zone:defaultZone;......],
    [Server:0.0.0.0:8081;	Zone:defaultZone; ......],
```

注意：日志经过格式化以提高可读性。

### 4.2 负载平衡示例

让我们打开浏览器并访问几次http://localhost:8762/spring-cloud-eureka-client/greeting。

每次，我们都应该看到略有不同的结果：

```html
Hello from 'SPRING-CLOUD-EUREKA-CLIENT with Port Number 8081'!
```

```html
Hello from 'SPRING-CLOUD-EUREKA-CLIENT with Port Number 8082'!
```

```html
Hello from 'SPRING-CLOUD-EUREKA-CLIENT with Port Number 8081'!
```

Zuul收到的每个请求都以轮询方式转发到不同的实例。

如果我们启动另一个实例并在Eureka中注册它，Zuul将自动注册它并开始向它转发请求：

```html
Hello from 'SPRING-CLOUD-EUREKA-CLIENT with Port Number 8083'!
```

我们还可以将Zuul的负载平衡策略更改为任何其他Netflix Ribbon策略-更多相关信息可以在我们的[Ribbon文章](https://www.baeldung.com/spring-cloud-rest-client-with-netflix-ribbon)中找到。

## 5. 总结

正如我们所看到的，Zuul为Rest服务的所有实例提供了一个单一的URL，并进行负载平衡以将请求以轮询方式转发到其中一个实例。