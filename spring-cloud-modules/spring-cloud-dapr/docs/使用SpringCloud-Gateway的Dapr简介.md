## 1. 概述

在本文中，我们将从一个 [Spring Cloud Gateway](https://www.baeldung.com/spring-cloud-gateway) 应用程序和一个[Spring Boot](https://www.baeldung.com/spring-boot)应用程序开始。然后，我们将更新它以改用[Dapr(分布式应用程序运行时)](http://dapr.io/)。最后，我们将更新 Dapr 配置以展示Dapr 在与云原生组件集成时提供的灵活性。

## 2. Dapr 简介

使用 Dapr，我们可以管理云原生应用程序的部署，而不会对应用程序本身产生任何影响。Dapr 使用[sidecar 模式](https://docs.microsoft.com/en-us/azure/architecture/patterns/sidecar) 来卸载应用程序的部署问题，这允许我们将其部署到其他环境(例如内部部署、不同的专有云平台、Kubernetes 等)，而无需对应用程序本身进行任何更改。有关详细信息，请查看Dapr 网站上的[概述。](https://docs.dapr.io/concepts/overview/)

## 3. 创建示例应用程序

我们将从创建示例 Spring Cloud Gateway 和Spring Boot应用程序开始。在“Hello world”示例的伟大传统中，网关会将请求代理到后端Spring Boot应用程序以获得标准的“Hello world”问候语。

### 3.1. 迎宾服务

首先，让我们为问候服务创建一个Spring Boot应用程序。这是一个标准的Spring Boot应用程序，只有 spring-boot-starter-web作为依赖，标准的主类，服务器端口配置为 3001。

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

在构建我们的问候服务应用程序之后，我们将启动它：

```shell
java -jar greeting/target/greeting-1.0-SNAPSHOT.jar
```

我们可以使用curl来测试它以返回“Hello world!” 信息：

```shell
curl http://localhost:3001/hello
```

### 3.2. 春云网关

现在，我们将在端口 3000 上创建一个 Spring Cloud Gateway 作为标准Spring Boot应用程序，其中spring-cloud-starter-gateway作为唯一的依赖项和标准主类。我们还将配置路由以访问问候服务：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: greeting-service
          uri: http://localhost:3001/
          predicates:
            - Path=/
          filters:
          - RewritePath=/?(?<segment>.), /${segment}

```

一旦我们构建了网关应用程序，我们就可以启动网关：

```shell
java -Dspring.profiles.active=no-dapr -jar gateway/target/gateway-1.0-SNAPSHOT.jar
```

我们可以使用curl来测试它以返回“Hello world!” 来自问候服务的消息：

```shell
curl http://localhost:3000/hello
```

## 4.添加Dapr

现在我们有了一个基本示例，让我们将 Dapr 添加到组合中。

为此，我们将网关配置为与 Dapr sidecar 通信，而不是直接与问候服务通信。然后 Dapr 将负责找到问候服务并将请求转发给它；通信路径现在将从网关开始，通过 Dapr sidecar 到达问候服务。

### 4.1. 部署 Dapr Sidecars

首先，我们需要部署两个 Dapr sidecar 实例——一个用于网关，一个用于问候服务。我们使用[Dapr CLI](https://docs.dapr.io/reference/cli/)执行此操作。

我们将使用标准的 Dapr 配置文件：

```yaml
apiVersion: dapr.io/v1alpha1
kind: Configuration
metadata:
  name: daprConfig
spec: {}

```

让我们使用dapr命令在端口 4000 上为网关启动 Dapr sidecar ：

```shell
dapr run --app-id gateway --dapr-http-port 4000 --app-port 3000 --config dapr-config/basic-config.yaml
```

接下来，让我们使用dapr命令在端口 4001 上启动问候语服务的 Dapr sidecar：

```shell
dapr run --app-id greeting --dapr-http-port 4001 --app-port 3001 --config dapr-config/basic-config.yaml
```

现在 Sidecar 正在运行，我们可以看到它们如何处理拦截请求并将请求转发到问候服务。当我们使用curl对其进行测试时，它应该返回“Hello world!” 问候语：

```shell
curl http://localhost:4001/v1.0/invoke/greeting/method/hello
```

让我们尝试使用网关 sidecar 进行相同的测试，以确认它也返回“Hello world!” 问候语：

```shell
curl http://localhost:4000/v1.0/invoke/greeting/method/hello
```

幕后发生了什么？网关的 Dapr sidecar 使用服务发现(在本例中为本地环境的 mDNS)来查找问候服务的 Dapr sidecar。然后，它使用[服务调用](https://docs.dapr.io/developing-applications/building-blocks/service-invocation/service-invocation-overview/)来调用问候服务上 的指定端点。

### 4.2. 更新网关配置

下一步是将网关路由配置为使用其 Dapr sidecar：

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: greeting-service
          uri: http://localhost:4000/
          predicates:
            - Path=/
          filters:
          - RewritePath=//?(?<segment>.), /v1.0/invoke/greeting/method/${segment}

```

然后，我们将使用更新后的路由重启网关：

```shell
java -Dspring.profiles.active=with-dapr -jar gateway/target/gateway-1.0-SNAPSHOT.jar
```

我们可以使用curl命令对其进行测试，再次从问候语服务中获取“Hello world”问候语：

```shell
curl http://localhost:3000/hello
```

[当我们使用Wireshark](https://www.wireshark.org/)查看网络上发生的情况时，我们可以看到网关和服务之间的流量通过 Dapr sidecars。

恭喜！我们现在已经成功地将 Dapr 纳入其中。让我们回顾一下这给我们带来了什么：不再需要配置网关来寻找问候服务(即不再需要在路由配置中指定问候服务的端口号)，网关不再需要了解如何将请求转发到问候服务的详细信息。

## 5.更新Dapr配置

现在我们已经有了 Dapr，我们可以配置 Dapr 来使用其他云原生组件。

### 5.1. 使用 Consul 进行服务发现

让我们使用[Consul](https://www.consul.io/) 来进行服务发现而不是 mDNS。

首先，我们需要在默认端口 8500 上安装并启动 Consul，然后更新 Dapr sidecar 配置以使用 Consul：

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

然后我们将使用新配置重启两个 Dapr sidecars：

```shell
dapr run --app-id greeting --dapr-http-port 4001 --app-port 3001 --config dapr-config/consul-config.yaml
dapr run --app-id gateway --dapr-http-port 4000 --app-port 3000 --config dapr-config/consul-config.yaml
```

一旦 sidecars 重新启动，我们就可以访问 consul UI 中的服务页面，并看到列出的网关和问候应用程序。请注意，我们不需要重新启动应用程序本身。

看看这有多容易？Dapr sidecars 的简单配置更改现在为我们提供了对 Consul 的支持，最重要的是，对底层应用程序没有影响。这与使用[Spring Cloud Consul](https://www.baeldung.com/spring-cloud-consul)不同，后者需要更新应用程序本身。

### 5.2. 使用 Zipkin 进行追踪

Dapr 还支持与[Zipkin](https://zipkin.io/)集成以跟踪跨应用程序的调用。

首先，在默认端口 9411 上安装并启动 Zipkin，然后更新 Dapr sidecar 的配置以添加 Zipkin：

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

我们需要重启两个 Dapr sidecars 来获取新的配置：

```shell
dapr run --app-id greeting --dapr-http-port 4001 --app-port 3001 --config dapr-config/consul-zipkin-config.yaml
dapr run --app-id gateway --dapr-http-port 4000 --app-port 3000 --config dapr-config/consul-zipkin-config.yaml
```

重新启动 Dapr 后，你可以发出curl命令并检查 Zipkin UI 以查看调用跟踪。

再一次，无需重新启动网关和问候服务。它只需要 简单地更新 Dapr 配置。将此与使用[Spring Cloud Zipkin](https://www.baeldung.com/tracing-services-with-zipkin)进行比较。

### 5.3. 其他组件

Dapr 支持许多组件来解决其他问题，例如安全、监控和报告。查看 Dapr 文档以获得[完整列表](https://docs.dapr.io/reference/components-reference/)。

## 六. 总结

我们已将 Dapr 添加到 Spring Cloud Gateway 与后端Spring Boot服务通信的简单示例中。我们已经展示了如何配置和启动 Dapr sidecar，以及它如何处理服务发现、通信和跟踪等云原生问题。

尽管这是以部署和管理 sidecar 应用程序为代价的，但 Dapr为部署到不同的云原生环境和云原生问题提供了灵活性，一旦与 Dapr 集成到位，就不需要更改应用程序。

这种方法还意味着开发人员在编写代码时无需担心云原生问题，这使他们可以腾出时间专注于业务功能。一旦将应用程序配置为使用 Dapr sidecar，就可以解决不同的部署问题，而不会对应用程序产生任何影响——无需重新编码、重新构建或重新部署应用程序。Dapr 在应用程序和部署问题之间提供了清晰的分离。