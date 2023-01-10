## 1. 概述

[Spring Cloud](https://www.baeldung.com/spring-cloud-series)带来了广泛的特性和库，如客户端负载平衡、服务注册/发现、并发控制和配置服务器。另一方面，在微服务世界中，使用不同语言和框架编写的多语言服务是一种常见的做法。那么，如果我们想在整个生态系统中利用 Spring Cloud 呢？Spring Cloud Netflix Sidecar 是这里的解决方案。

在本教程中，我们将通过工作示例了解有关 Spring Cloud Sidecar 的更多信息。

## 2.什么是Spring Cloud Sidecar？

Cloud Netflix Sidecar 受到[Netflix Prana](https://github.com/Netflix/Prana)的启发，可以用作实用程序来简化以非 JVM 语言编写的服务的服务注册表的使用，并提高 Spring Cloud 生态系统中端点的互操作性。

使用 Cloud Sidecar，可以在服务注册表中注册非 JVM 服务。此外，该服务还可以使用服务发现来查找其他服务，甚至可以通过主机查找或[Zuul Proxy](https://www.baeldung.com/spring-rest-with-zuul-proxy)访问配置服务器。能够集成非 JVM 服务的唯一要求是具有可用的标准健康检查端点。

## 3. 示例应用

我们的示例用例包含 3 个应用程序。为了展示 Cloud Netflix Sidecar 的最佳功能，我们将在 NodeJS 中创建一个/hello端点，然后通过一个名为 sidecar 的 Spring 应用程序将其公开到我们的生态系统。我们还将开发另一个Spring Boot应用程序，以使用服务发现和 Zuul回显/hello端点响应。

通过这个项目，我们的目标是涵盖请求的两个流程：

-   用户在 echoSpring Boot应用程序上调用 echo 端点。echo 端点使用DiscoveryClient从 Eureka 中查找 hello 服务 URL，即指向 NodeJS 服务的 URL。然后 echo 端点调用 NodeJS 应用程序上的 hello 端点
-   用户在 Zuul 代理的帮助下直接从 echo 应用程序调用 hello 端点

### 3.1. NodeJS Hello 端点

让我们首先创建一个名为hello.js的 JS 文件。我们正在使用express来处理我们的问候请求。在我们的hello.js文件中，我们引入了三个端点——默认的“/”端点、/hello端点和/health端点，以满足 Spring Cloud Sidecar 的要求：

```javascript
const express = require('express')
const app = express()
const port = 3000

app.get('/', (req, res) => {
    res.send('Hello World!')
})

app.get('/health', (req, res) => {
    res.send({ "status":"UP"})
})

app.get('/hello/:me', (req, res) => {
    res.send('Hello ' + req.params.me + '!')
})

app.listen(port, () => {
    console.log(`Hello app listening on port ${port}`)
})
```

接下来，我们将安装express：

```bash
npm install express
```

最后，让我们开始我们的应用程序：

```bash
node hello.js
```

随着应用程序的启动，让我们卷曲hello 端点：

```bash
curl http://localhost:3000/hello/baeldung
Hello baeldung!
```

然后，我们测试健康端点：

```bash
curl http://localhost:3000/health
status":"UP"}
```

当我们为下一步准备好节点应用程序时，我们将对其进行 Springify。

### 3.2. 边车应用

首先，我们需要[有一个 Eureka 服务器](https://www.baeldung.com/spring-cloud-netflix-eureka)。Eureka Server启动后，我们可以访问：http: [//127.0.0.1 :8761](http://127.0.0.1:8761/)

让我们添加[spring-cloud-netflix-sidecar](https://search.maven.org/artifact/org.springframework.cloud/spring-cloud-netflix-sidecar)作为依赖：

```markdown
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-netflix-sidecar</artifactId>
    <version>2.2.10.RELEASE</version>
</dependency>
```

需要注意的是，此时spring-cloud-netflix-sidecar的最新版本是2.2.10.RELEASE，它只支持spring boot 2.3.12.RELEASE。因此，目前最新版本的Spring Boot不兼容 Netflix Sidecar。

然后让我们在启用 sidecar 的情况下实现我们的Spring Boot应用程序类：

```java
@SpringBootApplication
@EnableSidecar
public class SidecarApplication {
    public static void main(String[] args) {
        SpringApplication.run(SidecarApplication.class, args);
    }
}
```

对于下一步，我们必须设置连接到 Eureka 的属性。此外，我们使用 NodeJS hello 应用程序的端口和健康 URI 设置 sidecar 配置：

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

现在我们可以开始我们的应用程序了。在我们的应用程序成功启动后，Spring 在 Eureka Server 中注册了一个名为“hello”的服务。

要检查它是否有效，我们可以访问端点：http://localhost:8084/hosts/sidecar。

@EnableSidecar不仅仅是向 Eureka 注册辅助服务的标记。它还会导致添加@EnableCircuitBreaker 和@EnableZuulProxy，随后，我们的Spring Boot应用程序将受益于[Hystrix](https://www.baeldung.com/introduction-to-hystrix)和[Zuul ](https://www.baeldung.com/spring-rest-with-zuul-proxy)。

现在，当我们准备好 Spring 应用程序时，让我们进入下一步，看看我们生态系统中服务之间的通信是如何工作的。

### 3.3. Echo 应用程序也说你好！

对于 echo 应用程序，我们将创建一个在服务发现的帮助下调用 NodeJS hello 端点的端点。此外，我们将启用 Zuul Proxy 以显示这两个服务之间通信的其他选项。

首先，让我们添加依赖项：

```markdown
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

为了与 sidecar 应用程序保持一致，我们在 echo 应用程序中使用相同版本的2.2.10.RELEASE 来实现对[spring-cloud-starter-netflix-zuul](https://search.maven.org/artifact/org.springframework.cloud/spring-cloud-starter-netflix-zuul)和[spring-cloud-starter-](https://search.maven.org/artifact/org.springframework.cloud/spring-cloud-starter-netflix-eureka-client/2.2.10.RELEASE/jar) netflix-eureka-client 的依赖。

然后让我们创建Spring Boot主类并启用 Zuul Proxy：

```java
@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class EchoApplication {
    // ...
}
```

然后，我们像在上一节中所做的那样配置 Eureka 客户端：

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
 ...
```

接下来，我们启动我们的 echo 应用程序。启动后，我们可以检查两个服务之间的互操作性。

要检查 sidecar 应用程序，让我们查询它以获取 echo 服务的元数据：

```bash
curl http://localhost:8084/hosts/echo
```

然后验证 echo 应用程序是否可以调用 sidecar 应用程序公开的 NodeJS 端点，让我们使用 Zuul 代理的魔力并 curl 这个 URL：

```bash
curl http://localhost:8085/sidecar/hello/baeldung
Hello baeldung!
```

由于我们已验证一切正常，让我们尝试另一种方式来调用 hello 端点。首先，我们将在 echo 应用程序中创建一个控制器并注入DiscoveryClient。然后我们添加一个GET端点，它使用DiscoveryClient来查询 hello 服务并使用RestTemplate 调用它：

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

让我们重新启动 echo 应用程序并执行此 curl 以验证从 echo 应用程序调用的 echo 端点：

```bash
curl http://localhost:8085/hello/baeldung
Hello baeldung!
```

或者为了让它更有趣一点，从 sidecar 应用程序调用它：

```bash
curl http://localhost:8084/echo/hello/baeldung
Hello baeldung!
```

## 4. 总结

在本文中，我们了解了 Cloud Netflix Sidecar，并使用 NodeJS 和两个 Spring 应用程序构建了一个工作示例，以展示它在 Spring 生态系统中的用法。