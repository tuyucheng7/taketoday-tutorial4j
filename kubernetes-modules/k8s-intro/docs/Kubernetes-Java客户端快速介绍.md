## 1. 简介

在本教程中，我们将展示如何使用官方客户端库从Java应用程序中使用[Kubernetes API。](https://www.baeldung.com/kubernetes)

## 2. 为什么要使用 Kubernetes API？

如今，可以肯定地说 Kubernetes 已成为管理容器化应用程序的事实标准。它提供了丰富的 API，使我们能够部署、扩展和监控应用程序和相关资源，例如存储、机密和环境变量。事实上，考虑此 API 的一种方法是对常规操作系统中可用的系统调用进行分布式模拟。

大多数时候，我们的应用程序可以忽略它们在 Kubernetes 下运行的事实。这是一件好事，因为它允许我们在本地开发它们，并通过一些命令和 YAML 咒语，快速将它们部署到多个云提供商，只需进行微小的更改。

但是，在一些有趣的用例中，我们需要与 Kubernetes API 对话以实现特定功能：

-   启动一个外部程序来执行一些任务，然后检索它的完成状态
-   动态创建/修改某些服务以响应某些客户请求
-   为跨多个 Kubernetes 集群甚至跨云提供商运行的解决方案创建自定义监控仪表板

诚然，这些用例并不常见，但由于其 API，我们将看到它们很容易实现。

此外，由于 Kubernetes API 是一个开放规范，我们可以非常有信心，我们的代码无需对任何经过认证的实现进行任何修改即可运行。

## 三、本地开发环境

在继续创建应用程序之前，我们需要做的第一件事是访问正常运行的 Kubernetes 集群。虽然我们可以为此使用公共云提供商，但本地环境通常可以对其设置的所有方面提供更多控制。

有一些适合此任务的轻量级发行版：

-   [K3S](https://k3s.io/)
-   [Minikube](https://www.baeldung.com/spring-boot-minikube)
-   [种类](https://kind.sigs.k8s.io/docs/user/quick-start/)

实际的设置步骤超出了本文的范围，但是无论你选择什么选项，只要确保[kubectl](https://kubernetes.io/docs/reference/kubectl/overview/) 在开始任何开发之前运行良好即可。

## 4.Maven依赖

首先，让我们将 KubernetesJavaAPI 依赖项添加到项目的pom.xml中：

```xml
<dependency>
    <groupId>io.kubernetes</groupId>
    <artifactId>client-java</artifactId>
    <version>11.0.0</version>
</dependency>

```

可以从 Maven Central 下载最新版本的[client-java 。](https://search.maven.org/search?q=g:io.kubernetes a:client-java)

## 5. 你好，Kubernetes

现在，让我们创建一个非常简单的 Kubernetes 应用程序，它将列出可用节点以及有关它们的一些信息。

尽管它很简单，但此应用程序说明了连接到正在运行的集群并执行 API 调用所必须执行的必要步骤。无论我们在实际应用程序中使用哪种 API，这些步骤总是相同的。

### 5.1. ApiClient初始化

ApiClient类是 API 中最重要的类之一，因为它包含执行对 Kubernetes API 服务器的调用的所有逻辑。创建此类实例的推荐方法是使用Config类中可用的静态方法之一。特别是，最简单的方法是使用defaultClient()方法：

```java
ApiClient client = Config.defaultClient();
```

使用此方法可确保我们的代码在远程和集群内场景下都能正常工作。此外，它会自动按照kubectl实用程序使用的相同步骤来定位配置文件

-   由KUBECONFIG环境变量定义的配置文件
-   $HOME/.kube/配置文件
-   /var/run/secrets/kubernetes.io/serviceaccount下的服务帐户令牌
-   直接访问http://localhost:8080

第三步是使我们的应用程序可以作为任何pod的一部分在集群内运行的步骤，只要它可以使用适当的服务帐户。

另外，请注意，如果我们在配置文件中定义了多个上下文，此过程将选择“当前”上下文，如使用kubectl config set-context命令定义的那样。

### 5.2. 创建 API 存根

一旦我们掌握了ApiClient 实例，我们就可以使用它为任何可用的 API 创建一个存根。在我们的例子中，我们将使用CoreV1Api类，它包含列出可用节点所需的方法：

```java
CoreV1Api api = new CoreV1Api(client);
```

在这里，我们使用现有的 ApiClient来创建 API 存根。

请注意，还有一个可用的无参数构造函数，但一般来说，我们应该避免使用它。不使用它的原因是，在内部，它将使用必须事先通过 Configuration.setDefaultApiClient()设置的全局ApiClient。这会在使用存根之前对调用此方法的人产生隐式依赖，这反过来可能会导致运行时错误和维护问题。

更好的方法是使用任何依赖项注入框架来执行此初始连接，在需要的地方注入生成的存根。

### 5.3. 调用 Kubernetes API

最后，让我们进入返回可用节点的实际 API 调用。CoreApiV1存根有一个方法可以精确地执行此操作，因此这变得微不足道：

```java
V1NodeList nodeList = api.listNode(null, null, null, null, null, null, null, null, 10, false);
nodeList.getItems()
  .stream()
  .forEach((node) -> System.out.println(node));

```

在我们的示例中，我们 为大多数方法的参数传递null ，因为它们是可选的。最后两个参数与所有listXXX 调用相关，因为它们指定调用超时以及这是否是 Watch调用。检查方法的签名会显示剩余的参数：

```java
public V1NodeList listNode(
  String pretty,
  Boolean allowWatchBookmarks,
  String _continue,
  String fieldSelector,
  String labelSelector,
  Integer limit,
  String resourceVersion,
  String resourceVersionMatch,
  Integer timeoutSeconds,
  Boolean watch) {
    // ... method implementation
}

```

对于这个快速介绍，我们将忽略 paging、watch 和 filter 参数。在这种情况下，返回值是一个 POJO，带有返回文档的Java表示。对于这个 API 调用，文档包含一个V1Node 对象列表，其中包含关于每个节点的几条信息。这是此代码在控制台上产生的典型输出：

```plaintext
class V1Node {
    metadata: class V1ObjectMeta {
        labels: {
            beta.kubernetes.io/arch=amd64,
            beta.kubernetes.io/instance-type=k3s,
            // ... other labels omitted
        }
        name: rancher-template
        resourceVersion: 29218
        selfLink: null
        uid: ac21e09b-e3be-49c3-9e3a-a9567b5c2836
    }
    // ... many fields omitted
    status: class V1NodeStatus {
        addresses: [class V1NodeAddress {
            address: 192.168.71.134
            type: InternalIP
        }, class V1NodeAddress {
            address: rancher-template
            type: Hostname
        }]
        allocatable: {
            cpu=Quantity{number=1, format=DECIMAL_SI},
            ephemeral-storage=Quantity{number=18945365592, format=DECIMAL_SI},
            hugepages-1Gi=Quantity{number=0, format=DECIMAL_SI},
            hugepages-2Mi=Quantity{number=0, format=DECIMAL_SI},
            memory=Quantity{number=8340054016, format=BINARY_SI}, 
            pods=Quantity{number=110, format=DECIMAL_SI}
        }
        capacity: {
            cpu=Quantity{number=1, format=DECIMAL_SI},
            ephemeral-storage=Quantity{number=19942490112, format=BINARY_SI}, 
            hugepages-1Gi=Quantity{number=0, format=DECIMAL_SI}, 
            hugepages-2Mi=Quantity{number=0, format=DECIMAL_SI}, 
            memory=Quantity{number=8340054016, format=BINARY_SI}, 
            pods=Quantity{number=110, format=DECIMAL_SI}}
        conditions: [
            // ... node conditions omitted
        ]
        nodeInfo: class V1NodeSystemInfo {
            architecture: amd64
            kernelVersion: 4.15.0-135-generic
            kubeProxyVersion: v1.20.2+k3s1
            kubeletVersion: v1.20.2+k3s1
            operatingSystem: linux
            osImage: Ubuntu 18.04.5 LTS
            // ... more fields omitted
        }
    }
}
```

正如我们所看到的，有相当多的可用信息。为了进行比较，这是具有默认设置的等效kubectl输出：

```shell
root@rancher-template:~# kubectl get nodes
NAME               STATUS   ROLES                  AGE   VERSION
rancher-template   Ready    control-plane,master   24h   v1.20.2+k3s1

```

## 六. 总结

在本文中，我们简要介绍了 Kubernetes API for Java。在以后的文章中，我们将更深入地研究这个 API 并探索它的一些附加功能：

-   解释可用 API 调用变体之间的区别
-   使用 Watch实时监控集群事件
-   如何使用分页高效地从集群中检索大量数据