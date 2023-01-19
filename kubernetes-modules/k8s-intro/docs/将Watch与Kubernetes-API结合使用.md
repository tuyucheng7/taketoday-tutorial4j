## 1. 简介

在本教程中，我们将继续探索JavaKubernetes API。这一次，我们将展示如何使用Watches来高效地监控集群事件。

## 2. 什么是 Kubernetes 手表？

在我们之前介绍[Kubernetes API](https://www.baeldung.com/kubernetes-java-client)的文章中，我们展示了如何恢复有关给定资源或资源集合的信息。如果我们想要的只是在给定的时间点获取这些资源的状态，这很好。然而，鉴于 Kubernetes 集群本质上是高度动态的，这通常是不够的。

大多数情况下，我们还希望监视这些资源并在事件发生时对其进行跟踪。例如，我们可能对跟踪 pod 生命周期事件或部署状态更改感兴趣。虽然我们可以使用轮询，但这种方法会受到一些限制。首先，随着要监控的资源数量的增加，它无法很好地扩展。其次，我们冒着丢失轮询周期之间恰好发生的事件的风险。

为了解决这些问题，Kubernetes 提出了 Watches 的概念，通过 watch查询参数可用于所有资源收集 API 调用。当它的值为 false或省略时，GET操作照常运行：服务器处理请求并返回符合给定条件的资源实例列表。然而，传递watch=true会显着改变它的行为：

-   响应现在由一系列修改事件组成，包含修改类型和受影响的对象
-   使用称为长轮询的技术发送初始批事件后，连接将保持打开状态

## 3. 创建 手表

Java Kubernetes API通过 Watch类支持Watches，它有一个静态方法： createWatch。 此方法采用三个参数：

-   一个 [ApiClient](https://www.baeldung.com/kubernetes-java-client#1-apiclient-initialization)，它处理对 Kubernetes API 服务器的实际 REST 调用
-   描述要监视的资源集合的 Call实例
-   具有预期资源类型的 TypeToken

我们使用其中一个listXXXCall()方法从库中可用的任何xxxApi类创建一个Call 实例。例如，要创建一个检测[Pod事件的](https://kubernetes.io/docs/concepts/workloads/pods/)Watch，我们将使用listPodForAllNamespacesCall()：

```java
CoreV1Api api = new CoreV1Api(client);
Call call = api.listPodForAllNamespacesCall(null, null, null, null, null, null, null, null, 10, true, null);
Watch<V1Pod> watch = Watch.createWatch(
  client, 
  call, 
  new TypeToken<Response<V1Pod>>(){}.getType()));

```

在这里，我们对大多数参数使用null，意思是“使用默认值”，只有两个例外：timeout和watch。 后者必须设置为true以进行监视调用。否则，这将是一个常规的休息电话。超时，在这种情况下，作为手表“生存时间”，这意味着一旦超时，服务器将停止发送事件并终止连接。

为以秒表示的超时参数找到合适的值 需要反复试验，因为它取决于客户端应用程序的确切要求。此外，检查你的 Kubernetes 集群配置也很重要。通常，手表有 5 分钟的硬性限制，因此超过 5 分钟的任何时间都不会达到预期的效果。

## 4.接收事件

仔细观察Watch类，我们可以看到它实现了标准 JRE 中的Iterator和Iterable ，因此我们可以在for-each或hasNext()-next()循环中使用从createWatch()返回的值：

```java
for (Response<V1Pod> event : watch) {
    V1Pod pod = event.object;
    V1ObjectMeta meta = pod.getMetadata();
    switch (event.type) {
    case "ADDED":
    case "MODIFIED":
    case "DELETED":
        // ... process pod data
        break;
    default:
        log.warn("Unknown event type: {}", event.type);
    }
}

```

每个事件的类型字段告诉我们对象发生了什么类型的事件——在我们的例子中是一个 Pod。一旦我们使用了所有事件，我们必须重新调用Watch.createWatch()以再次开始接收事件。在[示例代码](https://github.com/eugenp/tutorials/tree/master/kubernetes-modules/k8s-intro)中，我们将Watch 的创建和结果处理包围在一个 while循环中。其他方法也是可能的，例如使用 ExecutorService或类似的方法在后台接收更新。

## 5. 使用资源版本和书签

上面代码的一个问题是，每次我们创建一个新的Watch 时， 都会有一个初始事件流，其中包含给定类型的所有现有资源实例。发生这种情况是因为服务器假设我们没有关于它们的任何先前信息，所以它只是将它们全部发送。

然而，这样做违背了高效处理事件的目的，因为我们只需要在初始加载后才需要新事件。为了防止再次接收所有数据，watch 机制支持两个额外的概念：资源版本和书签。

### 5.1. 资源版本

Kubernetes 中的每个资源都在其元数据中包含一个 resourceVersion字段，它只是服务器每次更改时设置的不透明字符串。此外，由于资源集合也是一种资源，因此有一个resourceVersion 与之关联。当从集合中添加、删除和/或修改新资源时，此字段将相应更改。

当我们进行返回集合并包含resourceVersion参数的 API 调用时，服务器将使用其值作为查询的“起点”。对于Watch API 调用，这意味着只包含在创建通知版本之后发生的事件。

但是，我们如何获得resourceVersion以包含在我们的调用中？很简单：我们只是进行初始同步调用以检索初始资源列表，其中包括集合的resourceVersion，然后在后续的 Watch调用中使用它：

```java
String resourceVersion = null;
while (true) {
    if (resourceVersion == null) {
        V1PodList podList = api.listPodForAllNamespaces(null, null, null, null, null, "false",
          resourceVersion, null, 10, null);
        resourceVersion = podList.getMetadata().getResourceVersion();
    }
    try (Watch<V1Pod> watch = Watch.createWatch(
      client,
      api.listPodForAllNamespacesCall(null, null, null, null, null, "false",
        resourceVersion, null, 10, true, null),
      new TypeToken<Response<V1Pod>>(){}.getType())) {
        
        for (Response<V1Pod> event : watch) {
            // ... process events
        }
    } catch (ApiException ex) {
        if (ex.getCode() == 504 || ex.getCode() == 410) {
            resourceVersion = extractResourceVersionFromException(ex);
        }
        else {
            resourceVersion = null;
        }
    }
}

```

在这种情况下，异常处理代码相当重要。当由于某种原因请求的资源 版本不存在时，Kubernetes 服务器将返回 504 或 410 错误代码。在这种情况下，返回的消息通常包含当前版本。不幸的是，这些信息不是以任何结构化的方式出现，而是作为错误消息本身的一部分。

提取代码(aka ugly hack)为此目的使用正则表达式，但由于错误消息往往依赖于实现，因此代码回退到空值。通过这样做，主循环回到它的起点，用新的resourceVersion恢复一个新列表并恢复监视操作。

无论如何，即使有这个警告，关键是现在事件列表不会在每只手表上从头开始。

### 5.2. 书签

书签是一项可选功能，可在Watch调用返回的事件流上启用特殊的BOOKMARK事件。此事件在其元数据中包含一个resourceVersion 值，我们可以在后续的Watch调用中将其用作新的起点。

由于这是一个可选功能，我们必须通过在 API 调用中将true 传递给allowWatchBookmarks来显式启用它。此选项仅在创建Watch时有效 ，否则将被忽略。此外，服务器可能会完全忽略它，因此客户端根本不应该依赖于接收这些事件。

与之前单独使用resourceVersion 的方法相比 ，书签使我们能够避免代价高昂的同步调用：

```java
String resourceVersion = null;

while (true) {
    // Get a fresh list whenever we need to resync
    if (resourceVersion == null) {
        V1PodList podList = api.listPodForAllNamespaces(true, null, null, null, null,
          "false", resourceVersion, null, null, null);
        resourceVersion = podList.getMetadata().getResourceVersion();
    }

    while (true) {
        try (Watch<V1Pod> watch = Watch.createWatch(
          client,
          api.listPodForAllNamespacesCall(true, null, null, null, null, 
            "false", resourceVersion, null, 10, true, null),
          new TypeToken<Response<V1Pod>>(){}.getType())) {
              for (Response<V1Pod> event : watch) {
                  V1Pod pod = event.object;
                  V1ObjectMeta meta = pod.getMetadata();
                  switch (event.type) {
                      case "BOOKMARK":
                          resourceVersion = meta.getResourceVersion();
                          break;
                      case "ADDED":
                      case "MODIFIED":
                      case "DELETED":
                          // ... event processing omitted
                          break;
                      default:
                          log.warn("Unknown event type: {}", event.type);
                  }
              }
          }
        } catch (ApiException ex) {
            resourceVersion = null;
            break;
        }
    }
}

```

在这里，我们只需要在第一次通过时以及在内部循环中获得 ApiException 时获取完整列表。请注意，BOOKMARK事件与其他事件具有相同的对象类型，因此我们不需要在此处进行任何特殊转换。然而，我们唯一关心的字段是 resourceVersion ，我们为下一次Watch调用保存它。

## 六. 总结

在本文中，我们介绍了使用JavaAPI 客户端创建 Kubernetes Watches的不同方法。