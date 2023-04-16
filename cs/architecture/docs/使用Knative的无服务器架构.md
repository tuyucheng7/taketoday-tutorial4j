## 1. 概述

在本教程中，我们将探讨如何在 Kubernetes 平台上部署无服务器工作负载。我们将使用 Knative 作为执行此任务的框架。[在此过程中，我们还将了解使用 Knative 作为我们的无服务器应用](https://www.baeldung.com/cs/serverless-architecture)程序框架的好处。

## 2. Kubernetes 和 Knative

没有工具来帮助我们开发无服务器应用程序是没有乐趣的！还记得 Docker 和 Kubernetes 的结合是如何转变管理使用微服务架构构建的云原生应用程序的吗？当然，我们也可以从无服务器领域的框架和工具中受益。好吧，Kubernetes 没有理由不能在这里帮助我们。

### 2.1. 用于无服务器的 Kubernetes

[Kubernetes](https://kubernetes.io/)作为 CNCF 毕业项目，已成为编排容器化工作负载领域的领跑者之一。[它允许我们使用Docker](https://www.docker.com/)或[Buildah](https://buildah.io/)等流行工具自动部署、扩展和管理打包为[OCI 映像](https://github.com/opencontainers/image-spec)的应用程序：

[![Kubernetes 架构](https://www.baeldung.com/wp-content/uploads/2021/10/Kubernetes-Architecture-1-1024x536.jpg)](https://www.baeldung.com/wp-content/uploads/2021/10/Kubernetes-Architecture-1.jpg)

明显的好处包括优化资源利用。但是，这不是我们对无服务器的目标吗？

当然，就我们打算通过容器编排服务和无服务器服务实现的目标而言，存在许多重叠。但是，虽然 Kubernetes 为我们提供了一个很棒的工具来自动化很多事情，但我们仍然要负责配置和管理它。Serverless 旨在摆脱这种情况。

但是，我们当然可以利用 Kubernetes 平台来运行无服务器环境。这有很多好处。首先，它帮助我们摆脱特定于供应商的 SDK 和 API 将我们锁定到特定的云供应商。底层的 Kubernetes 平台帮助我们相对轻松地将我们的无服务器应用程序从一个云供应商移植到另一个云供应商。

此外，我们可以从用于构建应用程序的标准无服务器框架中获益。记住 Ruby on Rails 和 Spring Boot 的好处！最早的此类框架之一[来自 AWS ，并以](https://serverlesscode.com/post/serverless-formerly-jaws/)[无服务器](https://serverless.com/)而闻名。它是一个用 Node.js 编写的开源 Web 框架，可以帮助我们在多个 FaaS 服务提供商上部署我们的无服务器应用程序。

### 2.2. Knative 简介

[Knative](https://knative.dev/docs/)基本上是一个开源项目，它添加了用于在 Kubernetes 上部署、运行和管理无服务器应用程序的组件。我们可以将我们的服务或者功能打包成一个容器镜像，交给Knative。然后，Knative 仅在需要时为特定服务运行容器。

Knative 的核心架构包括两大组件，服务和事件，它们运行在底层 Kubernetes 基础设施之上。

[Knative Serving](https://knative.dev/docs/serving/)允许我们部署可以根据需要自动扩展的容器。[它通过将一组对象部署为自定义资源定义](https://kubernetes.io/docs/concepts/extend-kubernetes/api-extension/custom-resources/)(CRD)，构建在 Kubernetes 和 Istio 之上：

[![原生服务](https://www.baeldung.com/wp-content/uploads/2021/10/Knative-Serving.jpg)](https://www.baeldung.com/wp-content/uploads/2021/10/Knative-Serving.jpg)

Knative Serving 主要由四个这样的对象组成，Service、Route、Configuration 和 Revision。Service 对象管理我们工作负载的整个生命周期，并自动创建其他对象，如 Route 和 Configuration。每次我们更新服务时，都会创建一个新的修订版。我们可以定义服务以将流量路由到最新版本或任何其他版本。

[Knative Eventing](https://knative.dev/docs/eventing/)提供了一个用于为应用程序消费和生成事件的基础设施。这有助于将事件驱动架构与无服务器应用程序相结合：

[![Knative 三项赛](https://www.baeldung.com/wp-content/uploads/2021/10/Knative-Eventing-1--1024x376.jpg)](https://www.baeldung.com/wp-content/uploads/2021/10/Knative-Eventing-1-.jpg)

Knative Eventing适用于自定义资源，如 Source、Broker、Trigger 和 Sink。然后我们可以使用触发器过滤事件并将其转发给订阅者。Service 是向 Broker 发出事件的组件。此处的 Broker 充当事件的中心。我们可以使用触发器根据任何属性过滤这些事件，然后路由到接收器。

Knative Eventing 使用 HTTP POST 请求发送和接收符合[CloudEvents 的](https://cloudevents.io/)事件。CloudEvents 基本上是以标准方式描述事件数据的规范。目标是简化跨服务和平台的事件声明和传递。这是 CNCF Serverless 工作组下的一个项目。

## 3. 安装与设置

正如我们之前所见，Knative 基本上是一组组件，例如 Serving 和 Eventing，它们运行在 Istio 等服务网格和 Kubernetes 等工作负载编排集群上。然后是我们必须安装的命令行实用程序，以便于操作。因此，在继续安装 Knative 之前，我们需要确保很少的依赖关系。

### 3.1. 安装先决条件

安装 Kubernetes 有几个选项，本教程不会详细介绍它们。例如，[Docker Desktop](https://www.docker.com/products/docker-desktop)有可能启用一个非常简单的 Kubernetes 集群来满足大部分目的。然而，一种简单的方法是[在 Docker 中使用 Kubernetes](https://kind.sigs.k8s.io/)(种类)来运行具有 Docker 容器节点的本地 Kubernetes 集群。

在基于 Windows 的机器上，安装 kind 的最简单方法是使用[Chocolatey 包](https://chocolatey.org/packages/kind)：

```powershell
choco install kind复制
```

使用 Kubernetes 集群的一种便捷方式是使用命令行工具kubectl。同样，我们可以使用[Chocolaty 包](https://community.chocolatey.org/packages/kubernetes-cli)安装kubectl：

```powershell
choco install kubernetes-cli复制
```

最后，Knative 还附带了一个名为 kn 的命令行工具。Knative CLI 提供了一个快速简便的界面来创建 Knative 资源。它还有助于完成自动缩放和流量拆分等复杂任务。

在 Windows 机器上安装 Knative CLI 的最简单方法是从其[官方发布页面](https://github.com/knative/client/releases)下载兼容的二进制文件。然后我们可以简单地从命令行开始使用二进制文件。

### 3.2. 安装 Knative

一旦我们具备所有先决条件，我们就可以继续安装 Knative 组件。我们之前已经看到，Knative 组件只不过是我们部署在底层 Kubernetes 集群上的一堆 CRD。即使使用命令行实用程序，单独执行此操作也可能有点复杂。

幸运的是，对于开发环境，我们有一个可用的快速启动插件。该插件可以使用 Knative 客户端在 Kind 上安装本地 Knative 集群。和以前一样，在 Windows 机器上安装此快速入门插件的最简单方法是从其[官方发布页面](https://github.com/knative-sandbox/kn-plugin-quickstart/releases)下载二进制文件。

快速入门插件做了一些事情让我们准备好开始！首先，它确保我们已经安装了 Kind。然后它创建一个名为knative的集群。此外，它安装 Knative Serving，[Kourier](https://github.com/knative-sandbox/net-kourier)作为默认网络层，[nio.io](https://nip.io/)作为 DNS。最后，它安装 Knative Eventing 并创建内存中的 Broker 和 Channel 实现。

最后，为了确保 quickstart 插件安装正确，我们可以查询 Kind 集群并确保我们在那里有一个名为knative的集群。

## 4. 亲身体验 Knative

现在，我们已经了解了足够多的理论，可以在实践中尝试 Knative 提供的一些功能。首先，我们需要一个容器化的工作负载来使用。在 Java 中创建一个[简单的 Spring Boot 应用程序并使用 Docker](https://www.baeldung.com/dockerizing-spring-boot-application)将其容器化已经变得非常简单。我们不会深入讨论这个细节。

有趣的是，Knative 并没有限制我们开发应用程序的方式。因此，我们可以像以前一样使用任何我们喜欢的 Web 框架。此外，我们可以在 Knative 上部署各种类型的工作负载，从全尺寸应用程序到小型功能。当然，无服务器的好处在于创建更小的自治功能。

一旦我们有了容器化的工作负载，我们就可以主要使用两种方法将其部署到 Knative 上。由于所有工作负载最终都部署为 Kubernetes 资源，我们可以简单地创建一个包含资源定义的 YAML 文件并使用kubectl来部署该资源。或者，我们可以使用 Knative CLI 部署我们的工作负载，而无需深入了解这些细节。

### 4.1. 使用 Knative Serving 部署

[首先，我们将从Knative Serving](https://knative.dev/docs/serving/)开始。我们将了解如何在 Knative Serving 提供的无服务器环境中部署我们的工作负载。正如我们之前看到的，Service 是负责管理应用程序整个生命周期的 Knative Serving 对象。因此，我们首先将此对象描述为我们应用程序的 YAML 文件：

```powershell
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: my-service
spec:
  template:
    metadata:
      name: my-service-v1
    spec:
      containers:
        - image: <location_of_container_image_in_a_registry>
          ports:
            - containerPort: 8080复制
```

这是一个相当简单的资源定义，它提到了我们应用程序的容器镜像在可访问注册表中的位置。此处唯一需要注意的重要事项是我们为spec.template.metadata.name提供的值。这基本上用于命名修订，可以在以后识别它时派上用场。

使用 Kubernetes CLI 部署此资源非常容易。假设我们已将 YAML 文件命名为my-service.yaml ，我们可以使用以下命令：

```powershell
kubectl apply -f my-service.yaml复制
```

当我们部署此资源时，Knative 会代表我们执行许多步骤来管理我们的应用程序。首先，它为此版本的应用程序创建一个新的不可变修订版。然后它执行网络编程，为应用程序创建路由、入口、服务和负载均衡器。最后，它根据需求向上和向下扩展应用程序 pod。

如果创建 YAML 文件看起来有点笨拙，我们也可以使用 Knative CLI 来达到相同的结果：

```powershell
kn service create hello \
  --image <location_of_container_image_in_a_registry> \
  --port 8080 \
  --revision-name=my-service-v1复制
```

这是一种更简单的方法，可以为我们的应用程序部署相同的资源。此外，Knative 采取相同的必要步骤使我们的应用程序根据需求可用。

### 4.2. 使用 Knative Serving 进行流量拆分

自动上下扩展无服务器工作负载并不是使用 Kantive Serving 的唯一好处。它带有许多其他功能强大的功能，使无服务器应用程序的管理更加容易。在本教程的有限范围内不可能完全涵盖这一点。但是，其中一个功能是我们将在本节中重点介绍的流量拆分。

如果我们回想一下 Knative Serving 中 Revision 的概念，值得注意的是默认情况下 Knative 将所有流量定向到最新的 Revision。但由于我们仍然拥有所有以前的修订版，因此很有可能将某些或所有流量定向到较旧的修订版。

要实现这一点，我们需要做的就是修改包含服务描述的同一个 YAML 文件：

```powershell
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: my-service
spec:
  template:
    metadata:
      name: my-service-v2
    spec:
      containers:
        - image: <location_of_container_image_in_a_registry>
          ports:
            - containerPort: 8080
  traffic:
  - latestRevision: true
    percent: 50
  - revisionName: my-service-v1
    percent: 50复制
```

如我们所见，我们添加了一个新部分来描述修订版之间的流量划分。我们要求Knative 将一半的流量发送到新的 Revision 而另一半发送到以前的 Revision。部署此资源后，我们可以通过列出所有修订来验证拆分：

```powershell
kn revisions list复制
```

虽然 Knative 可以很容易地实现流量拆分，但我们真正可以用它做什么呢？好吧，此功能可能有多个用例。例如，如果我们想采用像蓝绿或金丝雀这样的部署模型，Knative 中的流量拆分就可以派上用场。如果我们想采用像 A/B 测试这样的建立信心的措施，我们可以再次依赖这个特性。

### 4.3. 使用 Knative Eventing 的事件驱动应用程序

接下来，让我们探索[Knative Eventing](https://knative.dev/docs/eventing/)。正如我们之前看到的，Knative Eventing 帮助我们将事件驱动的编程融入到无服务器架构中。但是我们为什么要关心事件驱动架构呢？基本上，事件驱动架构是一种软件架构范例，它促进事件的产生、检测、消费和反应。

通常，事件是状态的任何重大变化。例如，当订单从接受状态变为发货状态时。在这里，事件的生产者和消费者完全解耦。现在，任何架构中的解耦组件都有几个好处。例如，它在很大程度上简化了分布式计算模型中的横向扩展。

使用 Knative Eventing 的第一步是确保我们有可用的 Broker。现在，通常作为标准安装的一部分，我们应该在集群中为我们提供一个内存中的 Broker。我们可以通过列出所有可用的经纪人来快速验证这一点：

```powershell
kn broker list复制
```

现在，事件驱动的架构非常灵活，可以像单个服务一样简单，也可以像包含数百个服务的复杂网格一样简单。Knative Eventing 提供了底层基础设施，而没有对我们构建应用程序的方式施加任何限制。

为了本教程，我们假设我们有一个服务可以同时生成和使用事件。首先，我们必须为我们的事件定义源。我们可以扩展我们之前使用的相同服务定义，将其转换为 Source：

```powershell
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: my-service
spec:
  template:
    metadata:
      annotations:
        autoscaling.knative.dev/minScale: "1"
    spec:
      containers:
        - image: <location_of_container_image_in_a_registry>
          env:
            - name: BROKER_URL
              value: <broker_url_as_provided_by_borker_list_command>复制
```

这里唯一的重大变化是我们将代理 URL 作为环境变量提供。现在，和以前一样，我们可以使用 kubectl 来部署此资源，或者直接使用 Knative CLI。

由于 Knative Eventing使用 HTTP POST 发送和接收符合 CloudEvents 的事件，因此在我们的应用程序中使用它非常容易。我们可以使用 CloudEvents 简单地创建我们的事件负载，并使用任何 HTTP 客户端库将其发送到 Broker。

### 4.4. 使用 Knative Eventing 过滤和订阅事件

到目前为止，我们已经将事件发送给 Broker，但之后会发生什么？现在，我们感兴趣的是能够过滤这些事件并将其发送到特定目标。为此，我们必须定义一个触发器。基本上，代理使用触发器将事件转发给正确的消费者。现在，在此过程中，我们还可以根据任何事件属性过滤要发送的事件。

和以前一样，我们可以简单地创建一个 YAML 文件来描述我们的触发器：

```powershell
apiVersion: eventing.knative.dev/v1
kind: Trigger
metadata:
  name: my-trigger
  annotations:
    knative-eventing-injection: enabled
spec:
  broker: <name_of_the_broker_as_provided_by_borker_list_command>
  filter:
    attributes:
      type: <my_event_type>
  subscriber:
    ref:
      apiVersion: serving.knative.dev/v1
      kind: Service
      name: my-service复制
```

这是一个非常简单的触发器，它定义了我们用作源和事件接收器的相同服务。有趣的是，我们在这个触发器中使用了一个过滤器来只向订阅者发送特定类型的事件。我们可以创建更复杂的过滤器。

现在，和以前一样，我们可以使用 kubectl 部署这个资源，或者使用 Knative CLI 直接创建它。我们还可以创建任意数量的触发器，将事件发送给不同的订阅者。一旦我们创建了这个触发器，我们的服务就能够产生任何类型的事件，并且在这些事件中消费特定类型的事件！

在 Knative Eventing 中，Sink 可以是 Addressable 或 Callable 资源。可寻址资源接收并确认通过 HTTP 传送的事件。可调用资源能够接收通过 HTTP 传递的事件并转换该事件，可选择在 HTTP 响应中返回一个事件。除了我们现在看到的 Services，Channels 和 Brokers 也可以是 Sinks。

## 5.总结

在本教程中，我们讨论了如何利用 Kubernetes 作为底层基础设施来使用 Knative 托管无服务器环境。我们了解了 Knative 的基本架构和组件，即 Knative Serving 和 Knative Eventing。这让我们有机会了解使用 Knaitive 等框架构建无服务器应用程序的好处。