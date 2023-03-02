## 1. 概述

在本文中，我们将从一个[Spring Cloud Gateway](https://www.baeldung.com/spring-cloud-gateway)应用程序和一个[Spring Boot](https://www.baeldung.com/spring-boot)应用程序开始。然后，我们将更新它以改用[Dapr(分布式应用程序运行时)](http://dapr.io/)。最后，我们将更新Dapr配置以展示**Dapr在与云原生组件集成时提供的灵活性**。

## 2. Dapr简介

使用Dapr，我们可以管理云原生应用程序的部署，而不会对应用程序本身产生任何影响。Dapr使用[sidecar模式](https://docs.microsoft.com/en-us/azure/architecture/patterns/sidecar)来卸载应用程序的部署问题，这允许我们将其**部署到其他环境**(例如内部部署、不同的专有云平台、Kubernetes等)，**而无需对应用程序本身进行任何更改**。有关详细信息，请查看Dapr网站上的[概述](https://docs.dapr.io/concepts/overview/)。

## 3. 创建示例应用程序

我们将从创建示例Spring Cloud Gateway和Spring Boot应用程序开始。在“Hello world”示例的伟大传统中，网关会将请求代理到后端Spring Boot应用程序以获得标准的“Hello world”问候语。

### 3.1 Greeting服务

首先，让我们为greeting服务创建一个Spring Boot应用程序。这是一个标准的Spring Boot应用程序，只有spring-boot-starter-web作为依赖，标准的主类，服务器端口配置为3001。

让我们添加一个控制器来响应hello端点：

```java
@RestController
public class GreetingController {
    @GetMapping(value = "/hello")
    public String getHello() {
        return "Hello world!";
    }
}
```

在构建我们的greeting服务应用程序之后，我们将启动它：

```shell
java -jar greeting/target/greeting-1.0.0.jar
```

我们可以使用curl来测试它以返回“Hello world!”信息：

```shell
curl http://localhost:3001/hello
```

### 3.2 Spring Cloud网关

现在，我们将在端口3000上创建一个Spring Cloud Gateway作为标准Spring Boot应用程序，其中spring-cloud-starter-gateway作为唯一的依赖项和标准主类。我们还将配置路由以访问greeting服务：

```yaml
spring:
    cloud:
        gateway:
            routes:
                -   id: greeting-service
                    uri: http://localhost:3001/
                    predicates:
                        - Path=/**
                    filters:
                        - RewritePath=/?(?<segment>.*), /$\{segment}
```

一旦我们构建了网关应用程序，我们就可以启动网关：

```shell
java -Dspring.profiles.active=no-dapr -jar gateway/target/gateway-1.0.0.jar
```

我们可以使用curl来测试它以返回“Hello world!”来自greeting服务的消息：

```shell
curl http://localhost:3000/hello
```

## 4. 添加Dapr

现在我们有了一个基本示例，让我们将Dapr添加到组合中。

为此，**我们将网关配置为与Dapr sidecar通信**，而不是直接与greeting服务通信。然后Dapr将负责找到greeting服务并将请求转发给它；通信路径现在将从网关开始，通过Dapr sidecar到达greeting服务。

### 4.1 部署Dapr Sidecars

首先，我们需要部署两个Dapr sidecar实例-一个用于网关，一个用于greeting服务。我们使用[Dapr CLI](https://docs.dapr.io/reference/cli/)执行此操作。

我们将使用标准的Dapr配置文件：

```yaml
apiVersion: dapr.io/v1alpha1
kind: Configuration
metadata:
    name: daprConfig
spec: {}
```

让我们使用dapr命令在端口4000上为网关启动Dapr sidecar：

```shell
dapr run --app-id gateway --dapr-http-port 4000 --app-port 3000 --config dapr-config/basic-config.yaml
```

接下来，让我们使用dapr命令在端口4001上启动greeting服务的Dapr sidecar：

```shell
dapr run --app-id greeting --dapr-http-port 4001 --app-port 3001 --config dapr-config/basic-config.yaml
```

现在Sidecar正在运行，我们可以看到它们如何处理拦截请求并将请求转发到greeting服务。当我们使用curl对其进行测试时，它应该返回“Hello world!”问候语：

```shell
curl http://localhost:4001/v1.0/invoke/greeting/method/hello
```

让我们尝试使用网关sidecar进行相同的测试，以确认它也返回“Hello world!”问候语：

```shell
curl http://localhost:4000/v1.0/invoke/greeting/method/hello
```

幕后发生了什么？**网关的Dapr sidecar使用服务发现**(在本例中为本地环境的mDNS)来查找greeting服务的Dapr sidecar。然后，它**使用[服务调用](https://docs.dapr.io/developing-applications/building-blocks/service-invocation/service-invocation-overview/)来调用greeting服务**上的指定端点。

### 4.2 更新网关配置

下一步是将网关路由配置为使用其Dapr sidecar：

```yaml
spring:
    cloud:
        gateway:
            routes:
                -   id: greeting-service
                    uri: http://localhost:4000/
                    predicates:
                        - Path=/**
                    filters:
                        - RewritePath=//?(?<segment>.*), /v1.0/invoke/greeting/method/$\{segment}
```

然后，我们将使用更新后的路由重启网关：

```shell
java -Dspring.profiles.active=with-dapr -jar gateway/target/gateway-1.0.0.jar
```

我们可以使用curl命令对其进行测试，再次从greeting服务中获取“Hello world”问候语：

```shell
curl http://localhost:3000/hello
```

当我们使用[Wireshark](https://www.wireshark.org/)查看网络上发生的情况时，我们可以看到**网关和服务之间的流量通过Dapr sidecars**。

恭喜！我们现在已经成功地将Dapr纳入其中。让我们回顾一下这给我们带来了什么：不再需要配置网关来寻找greeting服务(即不再需要在路由配置中指定greeting服务的端口号)，网关不再需要了解如何将请求转发到greeting服务的详细信息。

## 5. 更新Dapr配置

现在我们已经有了Dapr，我们可以配置Dapr来使用其他云原生组件。

### 5.1 使用Consul进行服务发现

让我们使用[Consul](https://www.consul.io/)来进行服务发现而不是mDNS。

首先，我们需要在默认端口8500上安装并启动Consul，然后更新Dapr sidecar配置以使用Consul：

```yaml
apiVersion: dapr.io/v1alpha1
kind: Configuration
metadata:
    name: daprConfig
spec:
    nameResolution:
        component: "consul"
        configuration:
            selfRegister: true
```

然后我们将使用新配置重启两个Dapr sidecars：

```shell
dapr run --app-id greeting --dapr-http-port 4001 --app-port 3001 --config dapr-config/consul-config.yaml
```

```shell
dapr run --app-id gateway --dapr-http-port 4000 --app-port 3000 --config dapr-config/consul-config.yaml
```

一旦sidecars重新启动，我们就可以访问consul UI中的服务页面，并看到列出的网关和greeting应用程序。请注意，我们不需要重新启动应用程序本身。

看看这有多容易？**Dapr sidecars的简单配置更改现在为我们提供了对Consul的支持**，最重要的是，**对底层应用程序没有影响**。这与使用[Spring Cloud Consul](https://www.baeldung.com/spring-cloud-consul)不同，后者需要更新应用程序本身。

### 5.2 使用Zipkin进行追踪

Dapr还支持与[Zipkin](https://zipkin.io/)集成以跟踪跨应用程序的调用。

首先，在默认端口9411上安装并启动Zipkin，然后更新Dapr sidecar的配置以添加Zipkin：

```yaml
apiVersion: dapr.io/v1alpha1
kind: Configuration
metadata:
    name: daprConfig
spec:
    nameResolution:
        component: "consul"
        configuration:
            selfRegister: true
    tracing:
        samplingRate: "1"
        zipkin:
            endpointAddress: "http://localhost:9411/api/v2/spans"
```

我们需要重启两个Dapr sidecars来获取新的配置：

```shell
dapr run --app-id greeting --dapr-http-port 4001 --app-port 3001 --config dapr-config/consul-zipkin-config.yaml
```

```shell
dapr run --app-id gateway --dapr-http-port 4000 --app-port 3000 --config dapr-config/consul-zipkin-config.yaml
```

重新启动Dapr后，你可以发出curl命令并检查Zipkin UI以查看调用跟踪。

再一次，无需重新启动网关和greeting服务。**它只需要简单地更新Dapr配置**。将此与使用[Spring Cloud Zipkin](https://www.baeldung.com/tracing-services-with-zipkin)进行比较。

### 5.3 其他组件

Dapr支持许多组件来解决其他问题，例如安全、监控和报告。查看Dapr文档以获得[完整列表](https://docs.dapr.io/reference/components-reference/)。

## 6. 总结

我们已将Dapr添加到Spring Cloud Gateway与后端Spring Boot服务通信的简单示例中。我们已经展示了如何配置和启动Dapr sidecar，以及它如何处理服务发现、通信和跟踪等云原生问题。

尽管这是以部署和管理sidecar应用程序为代价的，但Dapr为部署到不同的云原生环境和云原生问题提供了灵活性，一旦与Dapr集成到位，就不需要更改应用程序。

这种方法还意味着开发人员在编写代码时不需要受到云原生问题的困扰，这使他们可以腾出时间专注于业务功能。一旦将应用程序配置为使用Dapr sidecar，就可以解决不同的部署问题，而不会对应用程序产生任何影响-无需重新编码、重新构建或重新部署应用程序。Dapr在应用程序和部署问题之间提供了清晰的分离。