## 1. 概述

[Spring Cloud Consul](https://cloud.spring.io/spring-cloud-consul/)项目为Spring Boot应用程序提供了与Consul的轻松集成。

[Consul](https://www.consul.io/intro/)是一个提供组件的工具，用于解决微服务架构中一些最常见的挑战：

-   服务发现：自动注册和取消注册服务实例的网络位置
-   健康检查：检测服务实例何时启动并运行
-   分布式配置：确保所有服务实例使用相同的配置

在本文中，我们将了解如何配置Spring Boot应用程序以使用这些功能。

## 2. 先决条件

首先，建议快速浏览一下[Consul](https://www.consul.io/intro/)及其所有功能。

在本文中，我们将使用在localhost:8500上运行的Consul代理。有关如何安装Consul和运行代理的更多详细信息，请参阅[此链接](https://learn.hashicorp.com/tutorials/consul/get-started-install)。

首先，我们需要将[spring-cloud-starter-consul-all](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-consul-all/4.0.1)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-all</artifactId>
    <version>3.1.1</version>
</dependency>
```

## 3. 服务发现

让我们编写我们的第一个Spring Boot应用程序并连接正在运行的Consul代理：

```java
@SpringBootApplication
public class ServiceDiscoveryApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ServiceDiscoveryApplication.class)
              .web(true).run(args);
    }
}
```

**默认情况下，SpringBoot将尝试连接到位于localhost:8500的Consul代理**。要使用其他设置，我们需要更新application.yml文件：

```yaml
spring:
    cloud:
        consul:
            host: localhost
            port: 8500
```

然后，如果我们在浏览器中访问Consul代理的站点http://localhost:8500，我们将看到我们的应用程序已在Consul中正确注册，标识符来自“${spring.application.name}:${profiles用逗号分隔}:${server.port}”。

要自定义此标识符，我们需要使用另一个表达式更新属性spring.cloud.discovery.instanceId：

```yaml
spring:
    application:
        name: myApp
    cloud:
        consul:
            discovery:
                instanceId: ${spring.application.name}:${random.value}
```

如果我们再次运行该应用程序，我们会看到它是使用标识符“MyApp”加上一个随机值注册的。我们需要它来在我们的本地机器上运行我们的应用程序的多个实例。

最后，**要禁用服务发现，我们需要将属性spring.cloud.consul.discovery.enabled设置为false**。

### 3.1 查找服务

我们已经在Consul中注册了我们的应用程序，但是客户端如何找到服务端点？我们需要一个发现客户端服务来从Consul获取正在运行且可用的服务。

**Spring为此提供了一个DiscoveryClient API**，我们可以使用@EnableDiscoveryClient注解启用它：

```java
@SpringBootApplication
@EnableDiscoveryClient
public class DiscoveryClientApplication {
    // ...
}
```

然后，我们可以将DiscoveryClient bean注入我们的控制器并访问实例：

```java
@RestController
public class DiscoveryClientController {

    @Autowired
    private DiscoveryClient discoveryClient;

    public Optional<URI> serviceUrl() {
        return discoveryClient.getInstances("myApp")
              .stream()
              .findFirst()
              .map(si -> si.getUri());
    }
}
```

最后，我们将定义我们的应用程序端点：

```java
@GetMapping("/discoveryClient")
public String discoveryPing() throws RestClientException, ServiceUnavailableException {
    URI service = serviceUrl()
        .map(s -> s.resolve("/ping"))
        .orElseThrow(ServiceUnavailableException::new);
    return restTemplate.getForEntity(service, String.class)
        .getBody();
}

@GetMapping("/ping")
public String ping() {
    return "pong";
}
```

“myApp/ping”路径是带有服务端点的Spring应用程序名称。Consul将提供名为“myApp”的所有可用应用程序。

## 4. 健康检查

Consul会定期检查服务端点的健康状况。

默认情况下，**Spring实现health端点以在应用程序启动时返回200 OK**。如果我们想要自定义端点，我们必须更新application.yml：

```yaml
spring:
    cloud:
        consul:
            discovery:
                healthCheckPath: /my-health-check
                healthCheckInterval: 20s
```

因此，Consul将每20秒轮询一次“/my-health-check”端点。

让我们定义我们的自定义健康检查服务以返回FORBIDDEN状态：

```java
@GetMapping("/my-health-check")
public ResponseEntity<String> myCustomCheck() {
    String message = "Testing my healh check function";
    return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
}
```

如果我们转到Consul代理站点，我们会看到我们的应用程序失败了。要解决此问题，“/my-health-check”服务应返回HTTP 200 OK状态代码。

## 5. 分布式配置

**此功能允许在所有服务之间同步配置**。Consul将监视任何配置更改，然后触发所有服务的更新。

首先，我们需要将[spring-cloud-starter-consul-config](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-consul-config/4.0.1)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-config</artifactId>
    <version>3.1.1</version>
</dependency>
```

我们还需要将Consul和Spring应用程序名称的设置从application.yml文件移动到Spring首先加载的bootstrap.yml文件。

然后，我们需要启用Spring Cloud Consul Config：

```yaml
spring:
    application:
        name: myApp
    cloud:
        consul:
            host: localhost
            port: 8500
            config:
                enabled: true
```

Spring Cloud Consul Config将在“/config/myApp”中查找Consul中的属性。因此，如果我们有一个名为“my.prop”的属性，我们需要在Consul代理站点中创建这个属性。

我们可以通过转到“KEY/VALUE”部分来创建属性，然后在“CreateKey”表单中输入“/config/myApp/my/prop”和“HelloWorld”作为值。最后，单击“Create”按钮。

请记住，如果我们使用Spring Profile，我们需要将Profile附加到Spring应用程序名称旁边。例如，如果我们使用dev Profile，Consul中的最终路径将是“/config/myApp,dev”。

现在，让我们看看带有注入属性的控制器是什么样的：

```java
@RestController
public class DistributedPropertiesController {

    @Value("${my.prop}")
    String value;

    @Autowired
    private MyProperties properties;

    @GetMapping("/getConfigFromValue")
    public String getConfigFromValue() {
        return value;
    }

    @GetMapping("/getConfigFromProperty")
    public String getConfigFromProperty() {
        return properties.getProp();
    }
}
```

和MyProperties类：

```java
@RefreshScope
@Configuration
@ConfigurationProperties("my")
public class MyProperties {
    private String prop;

    // standard getter, setter
}
```

如果我们运行应用程序，则字段value和properties具有来自Consul的相同“HelloWorld”值。

### 5.1 更新配置

在不重启Spring Boot应用程序的情况下更新配置怎么样？

如果我们返回到Consul代理站点并使用另一个值(如“New Hello World”)更新属性“/config/myApp/my/prop”，那么字段value将不会更改并且字段properties将按预期更新为“New Hello World”。

这是因为字段properties是一个MyProperties类，具有@RefreshScope注解。**所有用@RefreshScope注解标注的bean都会在配置更改后刷新**。

在现实生活中，我们不应该直接在Consul中拥有这些属性，而应该将它们持久存储在某个地方。我们可以使用[配置服务器](https://www.baeldung.com/spring-cloud-configuration)来做到这一点。

## 6. 总结

在本文中，我们了解了如何设置我们的Spring Boot应用程序以与Consul一起用于服务发现目的、自定义健康检查规则和共享分布式配置。

我们还为客户端引入了多种方法来调用这些已注册的服务。