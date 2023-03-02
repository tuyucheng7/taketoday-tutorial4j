## 1. 概述

[Spring Cloud](https://www.baeldung.com/spring-cloud-series)带来了广泛的功能和库，如客户端负载平衡、服务注册/发现、并发控制和配置服务器。另一方面，在微服务世界中，使用不同语言和框架编写的多语言服务是一种常见的做法。那么，如果我们想在整个生态系统中利用Spring Cloud呢？Spring Cloud Netflix Sidecar就是这里的解决方案。

在本教程中，我们将通过工作示例了解有关Spring Cloud Sidecar的更多信息。

## 2. 什么是Spring Cloud Sidecar？

Cloud Netflix Sidecar受到[Netflix Prana](https://github.com/Netflix/Prana)的启发，可以用作实用程序来简化以非JVM语言编写的服务的服务注册的使用，并提高Spring Cloud生态系统中端点的互操作性。

使用Cloud Sidecar，可以在服务注册表中注册非JVM服务。此外，该服务还可以使用服务发现来查找其他服务，甚至可以通过主机查找或[Zuul代理](https://www.baeldung.com/spring-rest-with-zuul-proxy)访问配置服务器。能够集成非JVM服务的唯一要求是具有可用的标准健康检查端点。

## 3. 示例应用程序

我们的示例用例包含3个应用程序。为了展示Cloud Netflix Sidecar的最佳功能，我们将在NodeJS中创建一个/hello端点，然后通过一个名为sidecar的Spring应用程序将其公开到我们的生态系统。我们还将开发另一个Spring Boot应用程序，以使用服务发现和Zuul回显/hello端点响应。

通过这个项目，我们的目标是涵盖请求的两个流程：

-   用户在echo Spring Boot应用程序上调用echo端点。echo端点使用DiscoveryClient从Eureka中查找hello服务URL，即指向NodeJS服务的URL。然后echo端点调用NodeJS应用程序上的hello端点
-   用户在Zuul代理的帮助下直接从echo应用程序调用hello端点

### 3.1 NodeJS Hello端点

让我们首先创建一个名为hello.js的JS文件。我们正在使用express来处理我们的echo请求。在我们的hello.js文件中，我们引入了三个端点-默认的“/”端点、/hello端点和/health端点，以满足Spring Cloud Sidecar的要求：

```javascript
const express = require('express')
const app = express()
const port = 3000

app.get('/', (req, res) => {
    res.send('Hello World!')
})

app.get('/health', (req, res) => {
    res.send({"status": "UP"})
})

app.get('/hello/:me', (req, res) => {
    res.send('Hello ' + req.params.me + '!')
})

app.listen(port, () => {
    console.log(`Hello app listening on port ${port}`)
})
```

接下来，我们将安装express：

```shell
npm install express
```

最后，让我们开始我们的应用程序：

```shell
node hello.js
```

随着应用程序的启动，让我们使用curl调用hello端点：

```shell
curl http://localhost:3000/hello/tuyucheng
Hello tuyucheng!
```

然后，我们测试健康端点：

```bash
curl http://localhost:3000/health
status":"UP"}
```

当我们为下一步准备好节点应用程序时，我们将对其进行Springify。

### 3.2 Sidecar应用

首先，我们需要有一个[Eureka服务器](https://www.baeldung.com/spring-cloud-netflix-eureka)。Eureka Server启动后，我们可以访问[http://127.0.0.1 :8761](http://127.0.0.1:8761/)

让我们添加[spring-cloud-netflix-sidecar](https://search.maven.org/artifact/org.springframework.cloud/spring-cloud-netflix-sidecar)作为依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-netflix-sidecar</artifactId>
    <version>2.2.10.RELEASE</version>
</dependency>
```

需要注意的是，此时Spring-Cloud-Netflix-Sidecar的最新版本是2.2.10.RELEASE，它只支持Spring Boot 2.3.12.RELEASE。因此，目前最新版本的Spring Boot不兼容Netflix Sidecar。

然后让我们在启用Sidecar的情况下实现我们的Spring Boot应用程序类：

```java
@SpringBootApplication
@EnableSidecar
public class SidecarApplication {
    public static void main(String[] args) {
        SpringApplication.run(SidecarApplication.class, args);
    }
}
```

对于下一步，我们必须设置连接到Eureka的属性。此外，我们使用NodeJS hello应用程序的端口和健康URI设置Sidecar配置：

```yaml
server.port: 8084
spring:
    application:
        name: sidecar
eureka:
    instance:
        hostname: localhost
        leaseRenewalIntervalInSeconds: 1
        leaseExpirationDurationInSeconds: 2
    client:
        service-url:
            defaultZone: http://127.0.0.1:8761/eureka
        healthcheck:
            enabled: true
sidecar:
    port: 3000
    health-uri: http://localhost:3000/health
```

现在我们可以启动我们的应用程序了。在我们的应用程序成功启动后，Spring在Eureka Server中注册了一个名为“hello”的服务。

要检查它是否有效，我们可以访问端点[http://localhost:8084/hosts/sidecar](http://localhost:8084/hosts/sidecar)。

@EnableSidecar不仅仅是向Eureka注册辅助服务的标记。它还会导致添加@EnableCircuitBreaker和@EnableZuulProxy，随后，我们的Spring Boot应用程序将受益于[Hystrix](https://www.baeldung.com/introduction-to-hystrix)和[Zuul](https://www.baeldung.com/spring-rest-with-zuul-proxy)。

现在，当我们准备好Spring应用程序时，让我们进入下一步，看看我们生态系统中服务之间的通信是如何工作的。

### 3.3 Echo应用程序

对于echo应用程序，我们将创建一个在服务发现的帮助下调用NodeJS hello端点的端点。此外，我们将启用Zuul代理以显示这两个服务之间通信的其他选项。

首先，让我们添加依赖项：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
    <version>2.2.10.RELEASE</version>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    <version>2.2.10.RELEASE</version>
</dependency>
```

为了与Sidecar应用程序保持一致，我们在echo应用程序中使用相同版本的2.2.10.RELEASE来实现对[spring-cloud-starter-netflix-zuul](https://search.maven.org/artifact/org.springframework.cloud/spring-cloud-starter-netflix-zuul)和[spring-cloud-starter-netflix-eureka-client](https://search.maven.org/artifact/org.springframework.cloud/spring-cloud-starter-netflix-eureka-client/2.2.10.RELEASE/jar)的依赖。

然后让我们创建Spring Boot主类并启用Zuul代理：

```java
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class EchoApplication {
    // ...
}
```

然后，我们按照上一节中所做的那样配置Eureka客户端：

```yaml
server.port: 8085
spring:
    application:
        name: echo
eureka:
    instance:
        hostname: localhost
        leaseRenewalIntervalInSeconds: 1
        leaseExpirationDurationInSeconds: 2
    client:
        service-url:
            defaultZone: http://127.0.0.1:8761/eureka
    # ...
```

接下来，我们启动我们的echo应用程序。启动后，我们可以检查两个服务之间的互操作性。

要检查Sidecar应用程序，让我们查询它以获取echo服务的元数据：

```shell
curl http://localhost:8084/hosts/echo
```

然后验证echo应用程序是否可以调用Sidecar应用程序公开的NodeJS端点，让我们使用Zuul代理的魔力并使用curl调用这个URL：

```shell
curl http://localhost:8085/sidecar/hello/tuyucheng
Hello tuyucheng!
```

由于我们已经验证了一切正常，让我们尝试另一种方式来调用hello端点。首先，我们将在echo应用程序中创建一个控制器并注入DiscoveryClient。然后我们添加一个GET端点，它使用DiscoveryClient来查询hello服务并使用RestTemplate调用它：

```java
@Autowired
DiscoveryClient discoveryClient;

@GetMapping("/hello/{me}")
public ResponseEntity<String> echo(@PathVariable("me") String me) {
    List<ServiceInstance> instances = discoveryClient.getInstances("sidecar");
    if (instances.isEmpty()) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("hello service is down");
    }
    String url = instances.get(0).getUri().toString();
    return ResponseEntity.ok(restTemplate.getForObject(url + "/hello/" + me, String.class));
}
```

让我们重新启动echo应用程序并执行此curl以验证从echo应用程序调用的echo端点：

```bash
curl http://localhost:8085/hello/tuyucheng
Hello tuyucheng!
```

或者为了让它更有趣一点，从Sidecar应用程序调用它：

```bash
curl http://localhost:8084/echo/hello/tuyucheng
Hello tuyucheng!
```

## 4. 总结

在本文中，我们了解了Cloud Netflix Sidecar，并使用NodeJS和两个Spring应用程序构建了一个工作示例，以展示它在Spring生态系统中的用法。