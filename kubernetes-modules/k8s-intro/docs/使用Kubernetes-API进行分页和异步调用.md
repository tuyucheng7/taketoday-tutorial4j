## 1. 简介

在本教程中，我们将继续探索[Kubernetes API for Java](https://www.baeldung.com/kubernetes-java-client)。这一次，我们将重点介绍它的两个特性：分页和异步调用。

## 2.页面

简而言之，[分页](https://www.baeldung.com/spring-data-jpa-pagination-sorting)允许我们以块的形式遍历大型结果集，也称为页面——因此是该方法的名称。在 KubernetesJavaAPI 的上下文中，此功能在所有返回资源列表的方法中都可用。这些方法总是包含两个可选参数，我们可以使用它们来迭代结果：

-   limit：单个 API 调用中返回的最大项目数
-   continue：一个 继续标记，告诉服务器返回结果集的起点

使用这些参数，我们可以迭代任意数量的项目，而不会对服务器施加太大压力。更好的是，客户端保存结果所需的内存量也是有限的。

现在，让我们看看如何使用这些参数来使用此方法获取集群中所有可用 pod 的列表：

```java
ApiClient client = Config.defaultClient();
CoreV1Api api = new CoreV1Api(client);
String continuationToken = null;
do {
    V1PodList items = api.listPodForAllNamespaces(
      null,
      continuationToken, 
      null,
      null, 
      2, 
      null, 
      null,
      null,
      10,
      false);
    continuationToken = items.getMetadata().getContinue();
    items.getItems()
      .stream()
      .forEach((node) -> System.out.println(node.getMetadata()));
} while (continuationToken != null);

```

在这里， listPodForAllNamespaces() API 调用的第二个参数 包含延续令牌，第五个参数是限制 参数。虽然 限制通常只是一个固定值， 但继续需要一些额外的努力。

对于第一次调用，我们发送一个 空 值，向服务器发出信号这是分页请求序列的第一次调用。收到响应后，我们从相应的列表元数据字段中获取要使用的下一个继续值的新值。

当没有更多可用结果时，该值将为null ，因此我们使用此事实来定义迭代循环的退出条件。

### 2.1. 分页陷阱

分页机制非常简单，但我们必须牢记一些细节：

-   目前，API 不支持服务器端排序。鉴于目前缺乏对排序的存储级支持，这不太可能[很快改变](https://github.com/kubernetes/kubernetes/issues/80602)
-   除continue之外的所有调用参数在调用之间必须相同
-   continue 值必须被视为不透明句柄。我们永远不应该对其价值做出任何假设
-   迭代是单向的。我们无法使用先前收到的继续 令牌返回结果集 
-   即使返回的列表元数据包含remainingItemCount字段，它的值既不可靠也不受所有实现支持

### 2.2. 列表数据一致性

由于 Kubernetes 集群是一个非常动态的环境，因此与分页调用序列关联的结果集有可能在被客户端读取时被修改。在这种情况下，Kubernetes API 的行为如何？

[如 Kubernetes 文档中所述](https://kubernetes.io/docs/reference/using-api/api-concepts/#the-resourceversion-parameter)，列表 API 支持 resourceVersion参数，该参数与resourceVersionMatch一起定义如何选择包含的特定版本。然而，对于分页结果集的情况，行为总是相同的：“Continue Token, Exact”。

这意味着返回的资源版本对应于分页列表调用开始时可用的资源版本。虽然这种方法提供了一致性，但它不会包括后来修改的结果。例如，当我们完成对大型集群中所有 pod 的迭代时，其中一些可能已经终止。

## 3.异步调用

到目前为止，我们以同步方式使用了 Kubernetes API，这对于简单的程序来说很好，但从资源使用的角度来看效率不是很高，因为它会阻塞调用线程，直到我们收到来自集群的响应并对其进行处理。这种行为会严重损害应用程序的响应能力，例如，如果我们开始在 GUI 线程中进行这些调用。

幸运的是，该库支持基于回调的异步模式，即立即将控制权返回给调用者。

检查CoreV1Api类，我们会注意到，对于每个同步xxx()方法，还有一个xxxAsync() 变体。例如，listPodForAllNamespaces() 的异步方法是listPodForAllNamespacesAsync ()。参数是相同的，只是为回调实现添加了一个额外的参数。

### 3.1. 回调详情

回调参数对象必须实现通用接口ApiCallback<T>，它只包含四个方法：

-   onSuccess：当且仅当调用成功时调用。第一个参数类型与同步版本返回的类型相同
-   onFailure: 调用时调用服务器出错或回复包含错误代码
-   onUploadProgress：在上传期间调用。我们可以使用此回调在长时间操作期间向用户提供反馈
-   onDownloadProgress：与 onUploadProgress相同，但用于下载

异步调用也不返回常规结果。相反，它们返回一个[OkHttp](https://www.baeldung.com/guide-to-okhttp)(Kubernetes API 使用的底层 REST 客户端)调用实例，该实例用作正在进行的调用的句柄。我们可以使用这个对象来轮询完成状态，或者，如果我们愿意，在完成之前取消它。

### 3.2. 异步调用示例

可以想象，到处实现回调需要大量的样板代码。为避免这种情况，我们将使用一个[调用助手](https://github.com/eugenp/tutorials/blob/master/kubernetes-modules/k8s-intro/src/main/java/com/baeldung/kubernetes/intro/AsyncHelper.java)来稍微简化此任务：

```java
// Start async call
CompletableFuture<V1NodeList> p = AsyncHelper.doAsync(api,(capi,cb) ->
  capi.listNodeAsync(null, null, null, null, null, null, null, null, 10, false, cb)
);
p.thenAcceptAsync((nodeList) -> {
    nodeList.getItems()
      .stream()
      .forEach((node) -> System.out.println(node.getMetadata()));
});
// ... do something useful while we wait for results

```

在这里，帮助程序包装了异步调用调用并将其调整为更标准的CompletableFuture。这允许我们将它与其他库一起使用，例如来自[Reactor Project 的](https://www.baeldung.com/reactor-core)库。在此示例中，我们添加了一个将所有元数据打印到标准输出的完成阶段。

通常，在处理期货时，我们必须意识到可能出现的并发问题。这段代码的在线版本包含一些调试日志，这些日志清楚地表明，即使对于这段简单的代码，也至少使用了三个线程：

-   启动异步调用的主线程
-   OkHttp 的线程用于进行实际的 HTTP 调用
-   完成线程，处理结果的地方

## 4. 总结

在本文中，我们了解了如何通过 KubernetesJavaAPI 使用分页和异步调用。