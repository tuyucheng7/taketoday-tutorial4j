## 1. 简介

在本教程中，我们将探索使用 KubernetesJavaAPI 过滤资源的不同方法。

在我们之前介绍 KubernetesJavaAPI 的文章中，我们重点介绍了查询、操作和监控集群资源的可用方法。

这些示例假设我们正在寻找特定种类的资源或针对单一资源。然而，在实践中，大多数应用程序都需要一种方法来根据某些标准来定位资源。

Kubernetes 的 API 支持三种方式来限制这些搜索的范围：

-   命名空间：范围限于给定的 Kubernetes[命名空间](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/)
-   字段选择器：范围仅限于具有匹配字段值的资源
-   标签选择器：范围仅限于具有匹配标签的资源

此外，我们可以将这些方法组合在一个查询中。这为我们提供了很大的灵活性来满足复杂的需求。

现在，让我们更详细地了解每种方法。

## 2. 使用命名空间

使用名称空间是限制查询范围的最基本方法。顾名思义，命名空间查询仅返回指定命名空间内的项目。

在JavaAPI 中，命名空间查询方法遵循模式listNamespacedXXX()。例如，要列出特定命名空间中的[pod](https://kubernetes.io/docs/concepts/workloads/pods/)，我们将使用listNamespacedPod()：

```java
ApiClient client  = Config.defaultClient();
CoreV1Api api = new CoreV1Api(client);
String ns = "ns1";
V1PodList items = api.listNamespacedPod(ns,null, null, null, null, null, null, null, null, 10, false);
items.getItems()
  .stream()
  .map((pod) -> pod.getMetadata().getName() )
  .forEach((name) -> System.out.println("name=" + name));

```

在这里，[ApliClient](https://www.baeldung.com/kubernetes-java-client#1-apiclient-initialization) 和CoreV1Api 用于执行对 Kubernetes API 服务器的实际访问。我们使用 ns1 作为命名空间来过滤资源。我们还使用与非命名空间方法中的参数类似的剩余参数。

正如预期的那样，命名空间查询也有调用变体，因此允许我们使用[之前描述的](https://www.baeldung.com/java-kubernetes-watch)相同技术创建Watches。[异步调用和分页](https://www.baeldung.com/java-kubernetes-paging-async)也以与其非命名空间版本相同的方式工作。

## 3. 使用字段选择器

命名空间 API 调用使用简单，但有一些限制：

-   全有或全无，这意味着我们不能选择多个(但不是全部)名称空间
-   无法根据资源属性进行过滤
-   对每个场景使用不同的方法会导致更复杂/冗长的客户端代码

[字段选择器](https://kubernetes.io/docs/concepts/overview/working-with-objects/field-selectors/)提供了一种基于其中一个字段的值来选择资源的方法。Kubernetes 用语中的字段只是与资源的 YAML 或 JSON 文档中给定值关联的 JSON 路径。例如，这是运行 Apache HTTP 服务器的 pod 的典型 Kubernetes YAML：

```yaml
apiVersion: v1
kind: Pod
metadata:
  labels:
    app: httpd
  name: httpd-6976bbc66c-4lbdp
  namespace: ns1
spec:
  ... fields omitted
status:
  ... fields omitted
  phase: Running

```

字段status.phase包含现有 Pod 的状态。 相应的 字段选择器表达式只是字段名称后跟运算符和值。现在，让我们编写一个查询，返回所有命名空间中所有正在运行的 pod：

```java
String fs = "status.phase=Running";        
V1PodList items = api.listPodForAllNamespaces(null, null, fs, null, null, null, null, null, 10, false);
// ... process items
```

字段选择器表达式仅支持相等('=' 或 '==')和不等('!=')运算符。此外，我们可以在同一调用中传递多个逗号分隔的表达式。在这种情况下，最终效果是它们将被 AND 运算在一起以产生最终结果：

```java
String fs = "metadata.namespace=ns1,status.phase=Running";        
V1PodList items = api.listPodForAllNamespaces(null, null, fs, null, null, null, null, null, 10, false);
// ... process items

```

请注意：字段值区分大小写！在前面的查询中，使用“running”而不是“Running”(大写“R”)会产生一个空结果集。

字段选择器的一个重要限制是它们依赖于资源。所有资源类型仅支持metadata.name和 metadata.namespace字段。

然而，字段选择器在与动态字段一起使用时特别有用。一个例子是前面例子中的status.phase 。将字段选择器与Watch 一起使用， 我们可以轻松创建一个监控应用程序，在 pod 终止时得到通知。

## 4. 使用标签选择器

标签是包含任意键/值对的特殊字段，我们可以将其添加到任何 Kubernetes 资源中作为其创建的一部分。标签选择器类似于字段选择器，因为它们本质上允许根据其值过滤资源列表，但提供了更大的灵活性：

-   支持其他运算符： in/notin/exists/not exists
-   与字段选择器相比，跨资源类型的一致使用

回到JavaAPI，我们通过构造具有所需条件的字符串并将其作为参数传递给所需资源 API listXXX调用来使用标签选择器。使用等式和/或不等式过滤特定标签值使用的语法与字段选择器使用的语法相同。

让我们看看查找标签为“app”且值为“httpd”的所有 pod 的代码：

```java
String ls = "app=httpd";        
V1PodList items = api.listPodForAllNamespaces(null, null, null, ls, null, null, null, null, 10, false);
// ... process items
```

in运算符类似于它的 SQL 运算符，允许我们在查询中创建一些 OR 逻辑：

```java
String ls = "app in ( httpd, test )";        
V1PodList items = api.listPodForAllNamespaces(null, null, null, ls, null, null, null, null, 10, false);
```

此外，我们可以使用labelname或 !检查字段是否存在。标签名称语法：

```java
String ls = "app";
V1PodList items = api.listPodForAllNamespaces(null, null, null, ls, null, null, null, null, 10, false);
```

最后，我们可以在单个 API 调用中链接多个表达式。结果项目列表仅包含满足所有表达式的资源：

```java
String ls = "app in ( httpd, test ),version=1,foo";
V1PodList items = api.listPodForAllNamespaces(null, null, null, ls, null, null, null, null, 10, false);
```

## 5.总结

在本文中，我们介绍了使用JavaKubernetes API 客户端过滤资源的不同方法。