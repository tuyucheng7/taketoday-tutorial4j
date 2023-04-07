## 1. 概述

在本教程中，我们将了解服务网格架构的基础知识，并了解它如何补充分布式系统架构。

我们将主要关注 Istio，它是服务网格的一个实现。在此过程中，我们将介绍 Istio 的核心架构并了解如何在 Kubernetes 上从中受益。

## 2.什么是服务网格？

在过去的几十年里，我们已经看到单体应用程序是如何开始分解成更小的应用程序的。它在云原生计算和微服务架构中获得了空前的普及。此外，[Docker](https://www.docker.com/)等容器化技术和[Kubernetes](https://kubernetes.io/)等编排系统仅在这方面有所帮助。

虽然在像 Kubernetes 这样的分布式系统上采用微服务架构有很多优势，但它也有其相当的复杂性。由于分布式服务必须相互通信，我们必须考虑发现、路由、重试和故障转移。

我们还必须注意其他几个问题，例如安全性和可观察性：

[![之前的服务网格](https://www.baeldung.com/wp-content/uploads/2021/04/Service-Mesh-Before.jpg)](https://www.baeldung.com/wp-content/uploads/2021/04/Service-Mesh-Before.jpg)

现在，在每个服务中构建这些通信功能可能会非常乏味——当服务环境增长且通信变得复杂时更是如此。这正是服务网格可以帮助我们的地方。基本上，服务网格消除了管理分布式软件系统中所有服务到服务通信的责任。

服务网格能够做到这一点的方式是通过一系列网络代理。本质上，服务之间的请求是通过与服务一起运行但位于基础设施层之外的代理来路由的：

[![服务网格之后](https://www.baeldung.com/wp-content/uploads/2021/04/Service-Mesh-After.jpg)](https://www.baeldung.com/wp-content/uploads/2021/04/Service-Mesh-After.jpg)

这些代理基本上为服务创建了一个网状网络——因此得名，服务网格！通过这些代理，服务网格能够控制服务到服务通信的各个方面。因此，我们可以用它来解决[分布式计算的八个谬误](https://en.wikipedia.org/wiki/Fallacies_of_distributed_computing)，这是一组描述我们经常对分布式应用程序做出的错误假设的断言。

## 3. 服务网格的特点

现在让我们了解服务网格可以为我们提供的一些功能。请注意，实际功能列表取决于服务网格的实现。但是，总的来说，我们应该期待所有实现中的大多数这些功能。

我们可以将这些功能大致分为三类：流量管理、安全性和可观察性。

### 3.1. 交通管理

服务网格的基本特征之一是流量管理。这包括动态服务发现和路由。它还支持一些有趣的用例，例如流量跟踪和流量拆分。这些对于执行金丝雀发布和 A/B 测试非常有用。

由于所有服务到服务的通信都由服务网格处理，因此它还启用了一些可靠性功能。例如，服务网格可以提供重试、超时、速率限制和断路器。这些开箱即用的故障恢复功能使通信更加可靠。

### 3.2. 安全

服务网格通常还处理服务到服务通信的安全方面。这包括通过双向 TLS (MTLS) 强制执行流量加密，通过证书验证提供身份验证，以及通过访问策略确保授权。

服务网格中也可能有一些有趣的安全用例。例如，我们可以实现网络分段，允许某些服务进行通信，同时禁止其他服务进行通信。此外，服务网格可以为审计需求提供精确的历史信息。

### 3.3. 可观察性

强大的可观察性是处理分布式系统复杂性的基础要求。因为服务网格处理所有通信，所以它被正确地放置以提供可观察性特性。例如，它可以提供有关分布式跟踪的信息。

服务网格可以生成很多指标，例如延迟、流量、错误和饱和度。此外，服务网格还可以生成访问日志，为每个请求提供完整的记录。这些对于理解单个服务以及整个系统的行为非常有用。

## 4. Istio 简介

[Istio](https://istio.io/latest/)是最初由 IBM、Google 和 Lyft 开发的服务网格的开源实现。它可以透明地分层到分布式应用程序上，并提供服务网格的所有优势，如流量管理、安全性和可观察性。

它旨在与各种部署配合使用，例如内部部署、云托管、Kubernetes 容器以及在虚拟机上运行的服务程序。尽管Istio 是平台中立的，但它经常与部署在 Kubernetes 平台上的微服务一起使用。

从根本上说，Istio 的工作原理是部署一个扩展版本的[Envoy 作为](https://www.envoyproxy.io/)每个微服务的代理作为 sidecar：

[![Istio 数据平面](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Data-Plane-1024x345.jpg)](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Data-Plane.jpg)

这个代理网络构成了Istio 架构的数据平面。这些代理的配置和管理是从控制平面完成的：

[![Istio 控制平面](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Control-Plane-1024x550.jpg)](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Control-Plane.jpg)

控制平面基本上是服务网格的大脑。它在运行时为数据平面中的 Envoy 代理提供发现、配置和证书管理。

当然，只有当我们拥有大量相互通信的微服务时，我们才能体会到 Istio 的好处。在这里，sidecar 代理在专用基础设施层中形成了一个复杂的服务网格：

[![Istio 服务网格](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Service-Mesh.jpg)](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Service-Mesh.jpg)

Istio 在与外部库和平台集成方面非常灵活。例如，我们可以将 Istio 与外部日志记录平台、遥测或策略系统集成。

## 5. 了解 Istio 组件

我们已经看到 Istio 架构由数据平面和控制平面组成。此外，还有几个核心组件可以使 Istio 发挥作用。

在本节中，我们将详细介绍这些核心组件。

### 5.1. 数据平面

Istio 的数据平面主要包括 Envoy 代理的扩展版本。Envoy 是一种开源边缘和服务代理，有助于将网络问题与底层应用程序分离。应用程序只是简单地向 localhost 发送消息和从localhost接收消息，而无需了解网络拓扑。

Envoy 的核心是一个运行在[OSI 模型](https://en.wikipedia.org/wiki/OSI_model)L3 和 L4 层的网络代理。它通过使用一系列可插入网络过滤器来执行连接处理。此外，Envoy 还支持针对基于 HTTP 的流量的额外 L7 层过滤器。此外，Envoy 对 HTTP/2 和 gRPC 传输提供一流的支持。

Istio 作为服务网格提供的许多功能实际上是由 Envoy 代理的底层内置功能启用的：

-   流量控制：Envoy 通过丰富的 HTTP、gRPC、WebSocket 和 TCP 流量路由规则启用细粒度流量控制应用
-   网络弹性：Envoy 包括对自动重试、断路和故障注入的开箱即用支持
-   安全性：Envoy 还可以执行安全策略并对底层服务之间的通信应用访问控制和速率限制

Envoy 与 Istio 配合得很好的另一个原因是它的可扩展性。Envoy提供了一个基于 WebAssembly 的可插拔扩展模型。这在自定义策略实施和遥测生成中非常有用。此外，我们还可以使用基于 Proxy-Wasm 沙箱 API 的 Istio 扩展来扩展 Istio 中的 Envoy 代理。

### 5.2. 控制平面

正如我们之前看到的，控制平面负责管理和配置数据平面中的 Envoy 代理。在控制平面中负责此的组件是istiod。在这里，istiod负责将高级路由规则和流量控制行为转换为特定于 Envoy 的配置，并在运行时将它们传播到 sidecars。

如果我们回想一下 Istio 控制平面的架构，我们会注意到它曾经是一组独立的组件一起工作。它包括用于服务发现的 Pilot、用于配置的 Galley、用于证书生成的 Citadel 和用于可扩展性的 Mixer 等组件。由于复杂性，这些单独的组件被合并到一个名为istiod的组件中。

istiod的核心仍然使用与之前各个组件相同的代码和 API。例如，Pilot 负责抽象特定于平台的服务发现机制，并将它们合成为 Sidecar 可以使用的标准格式。因此，Istio 可以支持多种环境的发现，例如 Kubernetes 或虚拟机。

此外，istiod还提供安全性，通过内置身份和凭证管理实现强大的服务到服务和最终用户身份验证。此外，借助istiod，我们可以根据服务身份实施安全策略。istiod进程还充当证书颁发机构 (CA) 并生成证书以促进数据平面中的相互 TLS (MTLS) 通信。

## 6. Istio 是如何工作的

我们已经了解了服务网格的典型特征。此外，我们还了解了 Istio 架构及其核心组件的基础知识。现在，是时候了解 Istio 如何通过其架构中的核心组件提供这些功能了。

我们将重点关注我们之前经历过的相同类别的功能。

### 6.1. 交通管理

我们可以使用 Istio 流量管理 API 对服务网格中的流量进行精细控制。我们可以使用这些 API 将我们自己的流量配置添加到 Istio。此外，我们可以使用 Kubernetes 自定义资源定义 (CRD) 来定义 API 资源。帮助我们控制流量路由的关键 API 资源是虚拟服务和目标规则：

[![Istio 流量管理](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Traffic-Management.jpg)](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Traffic-Management.jpg)

基本上，虚拟服务让我们配置如何将请求路由到Istio 服务网格中的服务。因此，虚拟服务由一个或多个按顺序评估的路由规则组成。评估虚拟服务的路由规则后，将应用目标规则。目的地规则帮助我们控制到目的地的流量——例如，按版本对服务实例进行分组。

### 6.2. 安全

Istio 中的安全性始于为每个服务提供强身份。与每个 Envoy 代理一起运行的 Istio 代理与istiod一起运行以自动执行密钥和证书轮换：

[![Istio 安全](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Security.jpg)](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Security.jpg)

Istio提供两种类型的身份验证——对等身份验证和请求身份验证。对等身份验证用于服务到服务的身份验证，其中 Istio 提供双向 TLS 作为全栈解决方案。请求身份验证用于最终用户身份验证，其中 Istio 使用自定义身份验证提供程序或 OpenID Connect (OIDC) 提供程序提供 JSON Web Token (JWT) 验证。

Istio 还允许我们通过简单地对服务应用授权策略来实施对服务的访问控制。授权策略对 Envoy 代理中的入站流量实施访问控制。有了这个，我们可以在不同级别应用访问控制：网格、命名空间和服务范围。

### 6.3. 可观察性

Istio 为网格内的所有服务通信生成详细的遥测数据，如指标、分布式跟踪和访问日志。Istio生成一组丰富的代理级指标、面向服务的指标和控制平面指标。

早些时候，Istio 遥测架构将 Mixer 作为核心组件。但从 Telemetry v2 开始，Mixer 提供的功能被 Envoy 代理插件取代：

[![Istio 遥测](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Telemetry.jpg)](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Telemetry.jpg)

此外，Istio 通过 Envoy 代理生成分布式跟踪。Istio 支持许多跟踪后端，如[Zipkin](https://zipkin.io/)、[Jaeger](https://www.jaegertracing.io/)、[Lightstep](https://lightstep.com/)和[Datadog](https://www.datadoghq.com/)。我们还可以控制跟踪生成的采样率。此外，Istio 还以一组可配置的格式为服务流量生成访问日志。

## 7. 亲身体验 Istio

现在我们已经了解了足够多的背景知识，我们已经准备好了解 Istio 的实际应用了。首先，我们将在 Kubernetes 集群中安装 Istio。此外，我们将使用一个简单的基于微服务的应用程序来演示 Istio on Kubernetes 的功能。

### 7.1. 安装

有多种安装 Istio 的方法，但其中最简单的方法是下载并解压特定操作系统(如 Windows)的最新版本。提取的包在bin目录中包含istioctl客户端二进制文件。我们可以使用istioctl在目标 Kubernetes 集群上安装 Istio ：

```powershell
istioctl install --set profile=demo -y复制
```

这将使用演示配置文件在默认的 Kubernetes 集群上安装 Istio 组件。我们还可以使用任何其他供应商特定的配置文件来代替演示。

最后，我们需要指示 Istio 在我们在此 Kubernetes 集群上部署任何应用程序时自动注入 Envoy sidecar 代理：

```powershell
kubectl label namespace default istio-injection=enabled复制
```

我们在这里使用kubectl并假设[像 Minikube](https://kubernetes.io/docs/tutorials/hello-minikube/)和 Kubernetes CLI kubectl这样的 Kubernetes 集群已经在我们的机器上可用。

### 7.2. 示例应用程序

出于演示目的，我们将设想一个非常简单的在线下订单应用程序。该应用程序包含三个微服务，它们相互交互以满足最终用户的订单请求：

[![Istio 示例应用程序](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Sample-Application.jpg)](https://www.baeldung.com/wp-content/uploads/2021/04/Istio-Sample-Application.jpg)

[我们不会深入探讨这些微服务的细节，但使用Spring Boot 和 REST API](https://www.baeldung.com/spring-boot-start)可以相当简单地创建它们。最重要的是，我们[为这些微服务创建了一个 Docker 镜像](https://www.baeldung.com/dockerizing-spring-boot-application)，以便我们可以将它们部署在 Kubernetes 上。

### 7.3. 部署

[在 Kubernetes 集群上](https://www.baeldung.com/spring-boot-minikube)部署容器化工作负载(如 Minikube)非常简单。我们将使用Deployment和Service资源类型来声明和访问工作负载。通常，我们在 YAML 文件中定义它们：

```powershell
apiVersion: apps/v1beta1
kind: Deployment
metadata:
  name: order-service
  namespace: default
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: order-service
        version: v1
    spec:
      containers:
      - name: order-service
        image: kchandrakant/order-service:v1
        resources:
          requests:
            cpu: 0.1
            memory: 200
---
apiVersion: v1
kind: Service
metadata:
  name: order-service
spec:
  ports:
  - port: 80
    targetPort: 80
    protocol: TCP
    name: http
  selector:
    app: order-service复制
```

这是order-service的Deployment和Service的一个非常简单的定义。同样，我们可以为inventory-service和shipping-service定义 YAML 文件。

使用kubectl部署这些资源也相当简单：

```powershell
kubectl apply -f booking-service.yaml -f inventory-service.yaml -f shipping-service.yaml复制
```

由于我们已经为默认命名空间启用了 Envoy sidecar 代理的自动注入，所以一切都会为我们处理。或者，我们可以使用istioctl的kube-inject命令手动注入 Envoy sidecar 代理。

### 7.4. 访问应用程序

现在，Istio 主要负责处理所有网格流量。因此，默认情况下不允许任何进出网格外部的流量。Istio 使用网关来管理来自网格的入站和出站流量。这样，我们就可以精确控制进入或离开网格的流量。Istio 提供了一些预配置的网关代理部署：istio-ingressgateway 和istio-egressgateway。

我们将为我们的应用程序创建一个网关和一个虚拟服务来实现这一点：

```powershell
apiVersion: networking.istio.io/v1alpha3
kind: Gateway
metadata:
  name: booking-gateway
spec:
  selector:
    istio: ingressgateway
  servers:
  - port:
      number: 80
      name: http
      protocol: HTTP
    hosts:
    - "*"
---
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: booking
spec:
  hosts:
  - "*"
  gateways:
  - booking-gateway
  http:
  - match:
    - uri:
        prefix: /api/v1/booking
    route:
    - destination:
        host: booking-service
        port:
          number: 8080复制
```

在这里，我们使用 Istio 提供的默认入口控制器。此外，我们定义了一个虚拟服务来将我们的请求路由到booking-service。

同样，我们也可以为来自网格的出站流量定义一个出口网关。

## 8. Istio 的常见用例

现在，我们已经了解了如何使用 Istio 在 Kubernetes 上部署一个简单的应用程序。但是，我们仍然没有使用 Istio 为我们提供的任何有趣的功能。在本节中，我们将了解服务网格的一些常见用例，并了解如何使用 Istio 为我们的简单应用程序实现它们。

### 8.1. 请求路由

我们可能希望以特定方式处理请求路由的原因有多种。例如，我们可能会部署多个版本的微服务，例如运输服务，并希望仅将一小部分请求路由到新版本。

我们可以使用虚拟服务的路由规则来实现：

```powershell
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: shipping-service
spec:
  hosts:
    - shipping-service
  http:
  - route:
    - destination:
        host: shipping-service
        subset: v1
      weight: 90
    - destination:
        host: shipping-service
        subset: v2
      weight: 10
---
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: shipping-service
spec:
  host: shipping-service
  subsets:
  - name: v1
    labels:
      version: v1
  - name: v2
    labels:
      version: v2复制
```

路由规则还允许我们根据标头参数等属性定义匹配条件。此外，目的地字段指定与条件匹配的流量的实际目的地。

### 8.2. 断路器

断路器基本上是一种软件设计模式，用于检测故障并封装防止故障进一步级联的逻辑。这有助于创建弹性微服务应用程序，限制故障和延迟峰值的影响。

在 Istio 中，我们可以使用DestinationRule中的trafficPolicy配置在调用inventory-service等服务时应用熔断：

```powershell
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: inventory-service
spec:
  host: inventory-service
  trafficPolicy:
    connectionPool:
      tcp:
        maxConnections: 1
      http:
        http1MaxPendingRequests: 1
        maxRequestsPerConnection: 1
    outlierDetection:
      consecutive5xxErrors: 1
      interval: 1s
      baseEjectionTime: 3m
      maxEjectionPercent: 100复制
```

在这里，我们将DestinationRule配置为maxConnections为 1，httpMaxPendingRequests为 1，maxRequestsPerConnection为 1。这实际上意味着如果我们超过并发请求数超过 1，断路器将开始捕获一些请求.

### 8.3. 启用相互 TLS

相互认证是指在类似 TLS 的认证协议中，两方同时相互认证的情况。默认情况下，带有代理的服务之间的所有流量都使用 Istio 中的双向 TLS。但是，没有代理的服务仍会继续以纯文本形式接收流量。

虽然 Istio 自动将具有代理的服务之间的所有流量升级为双向 TLS，但这些服务仍然可以接收纯文本流量。我们可以选择使用PeerAuthentication策略在网格范围内强制执行相互 TLS：

```powershell
apiVersion: "security.istio.io/v1beta1"
kind: "PeerAuthentication"
metadata:
  name: "default"
  namespace: "istio-system"
spec:
  mtls:
    mode: STRICT复制
```

我们还可以选择在每个命名空间或服务而不是在网格范围内强制执行相互 TLS。但是，特定于服务的PeerAuthentication策略优先于命名空间范围的策略。

### 8.4. 使用 JWT 进行访问控制

[JSON Web Token (JWT)](https://jwt.io/)是一种创建数据的标准，其有效负载包含断言多个声明的 JSON。这已被广泛接受，用于在身份提供者和服务提供者之间传递经过身份验证的用户的身份和标准或自定义声明。

我们可以在 Istio 中启用授权策略以允许访问基于 JWT 的预订服务等服务：

```powershell
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: require-jwt
  namespace: default
spec:
  selector:
    matchLabels:
      app: booking-service
  action: ALLOW
  rules:
  - from:
    - source:
       requestPrincipals: ["testing@baeldung.com/testing@baeldung.io"]复制
```

在这里，AuthorizationPolicy强制所有请求都具有有效的 JWT，并将requestPrincipal设置为特定值。Istio通过结合JWT 的声明iss和sub创建requestPrincipal属性。

## 9.事后思考

因此，到目前为止，我们已经看到像 Istio 这样的服务网格如何让我们的生活更轻松地处理微服务等分布式架构中的许多常见问题。但无论如何，Istio 是一个复杂的系统，增加了最终部署的复杂性。与所有其他技术一样，Istio 不是灵丹妙药，使用时必须慎重考虑。

### 9.1. 我们应该始终使用服务网格吗？

虽然我们已经看到足够多的理由使用服务网格，但让我们列举一些可能会促使我们反对使用它的理由：

-   服务网格以部署和操作服务网格的额外成本处理所有服务到服务的通信。对于更简单的应用程序，这可能是不合理的
-   由于我们已经习惯于处理其中一些问题，例如应用程序代码中的断路，这可能会导致服务网格中的重复处理
-   增加对服务网格等外部系统的依赖可能不利于应用程序的可移植性，尤其是在没有服务网格行业标准的情况下
-   由于服务网格通常通过代理拦截网格流量来工作，因此它可能会增加请求的不良延迟
-   服务网格增加了很多需要精确处理的额外组件和配置；这需要专业知识并增加了学习曲线
-   最后，我们最终可能会混合操作逻辑——它应该在服务网格中——与业务逻辑，它不应该在服务网格中

因此，正如我们所看到的，服务网格的故事不仅仅与好处有关，但这并不意味着它们不是真的。对我们来说重要的是仔细评估我们的需求和应用程序的复杂性，然后权衡服务网格的好处和增加的复杂性。

### 9.2. Istio 的替代品是什么？

虽然 Istio 非常受欢迎并得到了一些行业领导者的支持，但它肯定不是唯一可用的选择。虽然我们不能在这里做一个彻底的比较，但让我们来看看其中的几个选项，Linkerd 和 Consul。

[Linkerd](https://linkerd.io/)是为 Kubernetes 平台创建的开源服务网格。它也很受欢迎，目前[在CNCF](https://www.cncf.io/)中具有孵化项目的地位它的工作原理类似于 Istio 等任何其他服务网格。它还利用 TCP 代理来处理网状流量。Linkerd 使用一个用 Rust 编写的微代理，称为 Linkerd-proxy。

总体而言，考虑到 Linkerd 仅支持 Kubernetes，Linkerd 没有 Istio 复杂。但是，除此之外，Linkerd 中可用的功能列表与 Istio 中可用的功能列表非常相似。Linkerd 的核心架构也与 Istio 非常相似。基本上，Linkerd 包含三个主要组件：用户界面、数据平面和控制平面。

[Consul](https://www.consul.io/)是来自[HashiCorp 的](https://www.hashicorp.com/)服务网格的开源实现。它的好处是可以与 HashiCorp 的其他基础设施管理产品套件很好地集成，以提供更广泛的功能。Consul 中的数据平面可以灵活地支持代理和本地集成模型。它带有内置代理，但也可以与 Envoy 配合使用。

除了 Kubernetes 之外，Consul 还设计用于与[Nomad](https://www.nomadproject.io/)等其他平台一起使用。Consul 通过在每个节点上运行 Consul 代理来执行健康检查。这些代理与一个或多个存储和复制数据的 Consul 服务器通信。虽然它提供了像 Istio 这样的服务网格的所有标准功能，但它是一个部署和管理起来更复杂的系统。

## 10.总结

总而言之，在本教程中，我们了解了服务网格模式的基本概念及其为我们提供的功能。特别是，我们详细了解了 Istio。这涵盖了 Istio 的核心架构及其基本组件。此外，我们详细介绍了为一些常见用例安装和使用 Istio 的细节。