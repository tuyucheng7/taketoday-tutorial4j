## 1. 简介

在本教程中，我们将介绍使用官方JavaAPI 对 Kubernetes 资源进行 CRUD 操作。

我们已经在之前的文章中介绍了此 API 用法的基础知识，包括[基本的项目设置](https://www.baeldung.com/kubernetes-java-client)以及我们可以使用它来获取有关正在运行的集群的信息的[各种方式](https://www.baeldung.com/java-kubernetes-watch)。

一般来说，Kubernetes 部署大多是静态的。我们创建一些工件(例如 YAML 文件)来描述我们想要创建的内容并将它们提交到 DevOps 管道。在我们添加新组件或升级现有组件之前，我们系统的各个部分将保持不变。

但是，在某些情况下，我们需要动态添加资源。一个常见的是运行[作业](https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.19/#job-v1-batch)以响应用户发起的请求。作为响应，应用程序将启动一个后台作业来处理报告并使其可供以后检索。

这里的关键点是，通过使用这些 API，我们可以更好地利用可用的基础设施，因为我们可以仅在需要时使用资源，然后释放它们。

## 2. 创建新资源

在此示例中，我们将在 Kubernetes 集群中创建作业资源。作业是一种 Kubernetes 工作负载，与其他类型不同，它会运行直至完成。也就是说，一旦在其 pod 中运行的程序终止，作业本身也将终止。它的 YAML 表示与其他资源没有什么不同：

```yaml
apiVersion: batch/v1
kind: Job
metadata:
  namespace: jobs
  name: report-job
  labels:
    app: reports
spec:
  template:
    metadata:
      name: payroll-report
    spec:
      containers:
      - name: main
        image: report-runner
        command:
        - payroll
        args:
        - --date
        - 2021-05-01
      restartPolicy: Never
```

Kubernetes API 提供了两种创建等效Java对象的方法：

-   使用new创建 POJOS并通过 setter 填充所有必需的属性
-   使用流畅的 API 构建Java资源表示

使用哪种方法主要是个人喜好。在这里，我们将使用流畅的方法来创建V1Job对象，因为构建过程看起来与其对应的 YAML 非常相似：

```java
ApiClient client  = Config.defaultClient();
BatchV1Api api = new BatchV1Api(client);
V1Job body = new V1JobBuilder()
  .withNewMetadata()
    .withNamespace("report-jobs")
    .withName("payroll-report-job")
    .endMetadata()
  .withNewSpec()
    .withNewTemplate()
      .withNewMetadata()
        .addToLabels("name", "payroll-report")
        .endMetadata()
      .editOrNewSpec()
        .addNewContainer()
          .withName("main")
          .withImage("report-runner")
          .addNewCommand("payroll")
          .addNewArg("--date")
          .addNewArg("2021-05-01")
          .endContainer()
        .withRestartPolicy("Never")
        .endSpec()
      .endTemplate()
    .endSpec()
  .build(); 
V1Job createdJob = api.createNamespacedJob("report-jobs", body, null, null, null);
```

我们首先创建ApiClient，然后创建 API 存根实例。 作业资源是 Batch API 的一部分， 因此我们创建一个BatchV1Api 实例，我们将使用它来调用集群的 API 服务器。

接下来，我们实例化一个 V1JobBuilder实例，它引导我们完成填充所有属性的过程。注意嵌套构建器的使用：要“关闭”嵌套构建器，我们必须调用其endXXX()方法，这会将我们带回其父构建器。

或者，也可以使用 withXXX方法直接注入嵌套对象。当我们想要重用一组通用属性(例如元数据、标签和注解)时，这很有用。

最后一步只是调用 API 存根。这将序列化我们的资源对象并将 请求发送到服务器。正如预期的那样，API 有同步(上面使用的)和异步版本。

返回的对象将包含与创建的作业相关的元数据和状态字段。对于Job，我们可以使用它的状态字段来检查它何时完成。我们还可以使用我们关于监控资源的文章中介绍的技术之一来接收此通知。

## 3.更新现有资源

更新现有资源包括向 Kubernetes API 服务器发送 PATCH 请求，其中包含我们要修改的字段。从 Kubernetes 版本 1.16 开始，有四种方法可以指定这些字段：

-   JSON 补丁 (RFC 6092)
-   JSON 合并补丁 (RFC 7396)
-   战略合并补丁
-   应用 YAML

其中，最后一个是最容易使用的，因为它将所有合并和冲突解决留给了服务器：我们所要做的就是发送一个包含我们要修改的字段的 YAML 文档。

不幸的是，Java API 没有提供简单的方法来构建这个部分 YAML 文档。相反，我们必须求助于PatchUtil 帮助程序类来发送原始 YAML 或 JSON 字符串。但是，我们可以使用通过ApiClient对象提供的内置 JSON 序列化程序来获取它：

```java
V1Job patchedJob = new V1JobBuilder(createdJob)
  .withNewMetadata()
    .withName(createdJob.getMetadata().getName())
    .withNamespace(createdJob.getMetadata().getNamespace())
    .endMetadata()
  .editSpec()
    .withParallelism(2)
  .endSpec()
  .build();

String patchedJobJSON = client.getJSON().serialize(patchedJob);

PatchUtils.patch(
  V1Job.class, 
  () -> api.patchNamespacedJobCall(
    createdJob.getMetadata().getName(), 
    createdJob.getMetadata().getNamespace(), 
    new V1Patch(patchedJobJSON), 
    null, 
    null, 
    "baeldung", 
    true, 
    null),
  V1Patch.PATCH_FORMAT_APPLY_YAML,
  api.getApiClient());

```

在这里，我们使用从createNamespacedJob()返回的对象 作为模板，我们将从中构建修补版本。在这种情况下，我们只是将 并行度值从 1 增加到 2，而所有其他字段保持不变。这里很重要的一点是，当我们构建修改后的资源时，我们必须使用 withNewMetadata ()。 这确保我们不会构建包含托管字段的对象，这些字段存在于我们在创建资源后获得的返回对象中。有关托管字段的完整说明以及它们在 Kubernetes 中的使用方式，请参阅[文档](https://kubernetes.io/docs/reference/using-api/server-side-apply/#field-management)。

一旦我们用修改后的字段构建了一个对象，我们就可以使用 serialize 方法将它转换成它的 JSON 表示形式。然后我们使用这个序列化版本来构造一个 V1Patch对象，用作 PATCH 调用的有效负载。patch方法还需要一个额外的参数，我们在其中告知请求中存在的数据类型。 在我们的例子中，这是PATCH_FORMAT_APPLY_YAML，库将其用作HTTP 请求中包含的Content-Type标头。

传递给 fieldManager参数的“baeldung”值定义了操作资源字段的参与者名称。当两个或多个客户端尝试修改同一资源时，Kubernetes 在内部使用此值来解决最终冲突。我们还在 force参数中传递了true，这意味着我们将获得任何修改字段的所有权。

## 4.删除资源

与前面的操作相比，删除资源非常简单：

```java
V1Status response = api.deleteNamespacedJob(
  createdJob.getMetadata().getName(), 
  createdJob.getMetadata().getNamespace(), 
  null, 
  null, 
  null, 
  null, 
  null, 
  null ) ;

```

在这里，我们只是使用deleteNamespacedJob 方法来删除使用这种特定类型资源的默认选项的作业。如果需要，我们可以使用最后一个参数来控制删除过程的细节。这采用V1DeleteOptions对象的形式 ，我们可以使用它来指定任何依赖资源的宽限期和级联行为。

## 5.总结

在本文中，我们介绍了如何使用JavaKubernetes API 库操作 Kubernetes 资源。