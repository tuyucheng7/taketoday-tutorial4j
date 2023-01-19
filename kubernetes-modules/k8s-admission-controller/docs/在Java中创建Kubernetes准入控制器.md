## 1. 简介

在使用 Kubernetes 一段时间后，我们很快就会意识到其中涉及很多样板代码。即使对于简单的服务，我们也需要提供所有必需的详细信息，通常采用非常冗长的 YAML 文档的形式。

此外，在处理部署在给定环境中的多个服务时，这些 YAML 文档往往包含大量重复元素。例如，我们可能希望将给定的ConfigMap或一些边车容器添加到所有部署中。

在本文中，我们将探讨如何使用 Kubernetes 准入控制器来坚持 DRY 原则并避免所有这些重复代码。

## 2. 什么是准入控制器？

准入控制器是 Kubernetes 使用的一种机制，用于在 API 请求通过身份验证后但在执行前对其进行预处理。

API 服务器进程 ( kube-apiserver ) 已经带有几个内置控制器，每个控制器负责 API 处理的给定方面。

AllwaysPullImage是一个很好的例子：这个准入控制器修改了 pod 创建请求，因此无论通知值如何，图像拉取策略都变为“始终”。[Kubernetes 文档](https://kubernetes.io/docs/reference/access-authn-authz/admission-controllers/#what-does-each-admission-controller-do)包含标准准入控制器的完整列表。

除了那些实际上作为kubeapi-server进程的一部分运行的内置控制器之外，Kubernetes 还支持外部准入控制器。在这种情况下，准入控制器只是一个处理来自 API 服务器的请求的 HTTP 服务。

此外，这些外部准入控制器可以动态添加和删除，因此得名动态准入控制器。这导致处理管道看起来像这样：

[![k8s 准入控制器](https://www.baeldung.com/wp-content/uploads/2021/07/k8s-admission-controllers.png)](https://www.baeldung.com/wp-content/uploads/2021/07/k8s-admission-controllers.png)

在这里，我们可以看到传入的 API 请求，一旦通过身份验证，就会通过每个内置的准入控制器，直到它到达持久层。

## 3.准入控制器类型

目前，有两种类型的准入控制器：

-   改变准入控制器
-   验证准入控制器

顾名思义，主要区别在于每个处理传入请求的类型。变异控制器可以在将请求传递到下游之前修改请求，而验证控制器只能验证它们。

关于这些类型的一个重点是 API 服务器执行它们的顺序：变异控制器首先出现，然后是验证控制器。这是有道理的，因为验证只会在我们有最终请求时发生，可能会被任何变异控制器更改。

### 3.1. 入学审查请求

内置准入控制器(修改和验证)使用简单的 HTTP 请求/响应模式与外部准入控制器通信：

-   Request：一个AdmissionReview JSON 对象，包含要在其 请求属性中处理的 API 调用
-   响应：一个AdmissionReview JSON 对象，在其响应属性中包含结果 

下面是一个请求示例：

```json
{
  "kind": "AdmissionReview",
  "apiVersion": "admission.k8s.io/v1",
  "request": {
    "uid": "c46a6607-129d-425b-af2f-c6f87a0756da",
    "kind": {
      "group": "apps",
      "version": "v1",
      "kind": "Deployment"
    },
    "resource": {
      "group": "apps",
      "version": "v1",
      "resource": "deployments"
    },
    "requestKind": {
      "group": "apps",
      "version": "v1",
      "kind": "Deployment"
    },
    "requestResource": {
      "group": "apps",
      "version": "v1",
      "resource": "deployments"
    },
    "name": "test-deployment",
    "namespace": "test-namespace",
    "operation": "CREATE",
    "object": {
      "kind": "Deployment",
      ... deployment fields omitted
    },
    "oldObject": null,
    "dryRun": false,
    "options": {
      "kind": "CreateOptions",
      "apiVersion": "meta.k8s.io/v1"
    }
  }
}
```

在可用字段中，有些字段特别重要：

-   operation：这表明此请求是否将创建、修改或删除资源
-   对象： 正在处理的资源规范详细信息。
-   oldObject： 修改或删除资源时，该字段包含现有资源

预期的响应也是一个AdmissionReview JSON 对象，使用 响应 字段代替响应：

```json
{
  "apiVersion": "admission.k8s.io/v1",
  "kind": "AdmissionReview",
  "response": {
    "uid": "c46a6607-129d-425b-af2f-c6f87a0756da",
    "allowed": true,
    "patchType": "JSONPatch",
    "patch": "W3sib3A ... Base64 patch data omitted"
  }
}
```

让我们剖析响应 对象的字段：

-   uid：此字段的值必须与传入请求字段中存在的相应字段相匹配
-   允许：审核操作的结果。true表示 API 调用处理可以进行下一步
-   patchType：仅对改变准入控制器有效。AdmissionReview请求返回的补丁类型
-   patch：要在传入对象中应用的补丁。下一节的详细信息

### 3.2. 补丁数据

来自变异准入控制器的响应中出现的补丁字段告诉 API 服务器在请求可以继续之前需要更改什么。它的值是一个 Base64 编码的[JSONPatch](https://www.baeldung.com/spring-rest-json-patch)对象，其中包含一组指令，API 服务器使用这些指令来修改传入的 API 调用的主体：

```json
[
  {
    "op": "add",
    "path": "/spec/template/spec/volumes/-",
    "value":{
      "name": "migration-data",
      "emptyDir": {}
    }
  }
]
```

在此示例中，我们有一条指令将卷附加到部署规范的卷数组。处理补丁时的一个常见问题是无法将元素添加到现有数组，除非它已经存在于原始对象中。这在处理 Kubernetes API 对象时尤其烦人，因为最常见的对象(例如，部署)包括可选数组。

例如，只有当传入 部署已经有至少一个卷时，前面的示例才有效。如果不是这种情况，我们将不得不使用稍微不同的指令：

```json
[
  {
    "op": "add",
    "path": "/spec/template/spec/volumes",
    "value": [{
      "name": "migration-data",
      "emptyDir": {}
    }]
  }
]
```

在这里，我们定义了一个新的卷字段，其值是一个包含卷定义的数组。以前，该值是一个对象，因为这是我们附加到现有数组的内容。

## 4. 示例用例：等待

现在我们对准入控制器的预期行为有了基本的了解，让我们写一个简单的例子。Kubernetes 中的一个常见问题是管理运行时依赖项，尤其是在使用微服务架构时。例如，如果某个特定的微服务需要访问数据库，那么如果前者处于离线状态，则没有必要启动它。

为了解决这样的问题，我们可以在我们的 pod中使用initContainer 在启动主容器之前进行此检查。一种简单的方法是使用流行的 [wait-for-it](https://github.com/vishnubob/wait-for-it) shell 脚本，也可以作为[docker image](https://hub.docker.com/r/willwill/wait-for-it/)获得。

该脚本采用主机名和端口参数并尝试连接到它。如果测试成功，容器将退出并返回成功状态码，然后 pod 初始化继续进行。否则，它将失败，关联的控制器将根据定义的策略不断重试。将这种飞行前检查外部化的好处在于，任何关联的 Kubernetes 服务都会注意到该故障。因此，不会向它发送任何请求，从而可能提高整体弹性。

### 4.1. 准入控制器的案例

这是添加了wait-for-it init 容器的典型部署：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend
  labels:
    app: nginx
spec:
  replicas: 1
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      initContainers:
      - name: wait-backend
        image: willwill/wait-for-it
        args:
        -www.google.com:80
      containers: 
      - name: nginx 
        image: nginx:1.14.2 
        ports: 
        - containerPort: 80
```

虽然没有那么复杂(至少在这个简单的示例中如此)，但将相关代码添加到每个部署中也有一些缺点。特别是，我们给部署作者施加了明确指定应该如何进行依赖性检查的负担。相反，更好的体验只需要定义应该测试的内容。

输入我们的准入控制器。为了解决这个用例，我们将编写一个变异的准入控制器，它在资源中查找特定注解的存在，并在存在时将initContainer添加到它。这是带注解的部署规范的样子：

```yaml
apiVersion: apps/v1 
kind: Deployment 
metadata: 
  name: frontend 
  labels: 
    app: nginx 
  annotations:
    com.baeldung/wait-for-it: "www.google.com:80"
spec: 
  replicas: 1 
  selector: 
    matchLabels: 
      app: nginx 
  template: 
    metadata: 
      labels: 
        app: nginx 
    spec: 
      containers:
      - name: nginx
        image: nginx:1.14.2
        ports:
          - containerPort: 80
```

在这里，我们使用注解 com.baeldung/wait-for-it来指示我们必须测试的主机和端口。但重要的是，没有什么可以告诉我们应该如何进行测试。理论上，我们可以在保持部署规范不变的情况下以任何方式更改测试。

现在，让我们继续实施。

### 4.2. 项目结构

如前所述，外部准入控制器只是一个简单的 HTTP 服务。因此，我们将创建一个Spring Boot项目作为我们的基本结构。对于这个例子，我们只需要[Spring Web Reactive](https://www.baeldung.com/spring-reactive-guide) starter，但对于真实世界的应用程序，添加 Actuator[和](https://www.baeldung.com/spring-boot-actuators)/或一些[Cloud Config](https://www.baeldung.com/spring-cloud-configuration)依赖项等功能可能也很有用。

### 4.3. 处理请求

准入请求的入口点是一个简单的 Spring REST 控制器，它将传入负载的处理委托给服务：

```java
@RestController
@RequiredArgsConstructor
public class AdmissionReviewController {

    private final AdmissionService admissionService;

    @PostMapping(path = "/mutate")
    public Mono<AdmissionReviewResponse> processAdmissionReviewRequest(@RequestBody Mono<ObjectNode> request) {
        return request.map((body) -> admissionService.processAdmission(body));
    }
}

```

在这里，我们使用 ObjectNode 作为输入参数。这意味着我们将尝试处理 API 服务器发送的任何格式良好的 JSON。采用这种松散方法的原因是，截至撰写本文时，仍未针对此有效载荷发布官方架构。在这种情况下，使用非结构化类型意味着一些额外的工作，但确保我们的实现更好地处理特定 Kubernetes 实现或版本决定扔给我们的任何额外字段。

此外，考虑到请求对象可以是 Kubernetes API 中的任何可用资源，因此在此处添加过多的结构也无济于事。

### 4.4. 修改录取请求

处理的核心发生在AdmissionService类中。这是一个@Component类，通过一个公共方法注入到控制器中： processAdmission。此方法处理传入的审核请求并返回适当的响应。

完整代码可在线获取，基本上由一长串 JSON 操作组成。他们中的大多数都是微不足道的，但一些摘录值得一些解释：

```java
if (admissionControllerProperties.isDisabled()) {
    data = createSimpleAllowedReview(body);
} else if (annotations.isMissingNode()) {
    data = createSimpleAllowedReview(body);
} else {
    data = processAnnotations(body, annotations);
}

```

首先，为什么要添加一个“disabled”属性？好吧，事实证明，在一些高度受控的环境中，更改现有部署的配置参数可能比删除和/或更新它容易得多。由于我们使用[@ConfigurationProperties机制](https://www.baeldung.com/configuration-properties-in-spring-boot)来填充此属性，因此它的实际值可以来自各种来源。

接下来，我们测试缺少的注解，我们将其视为我们应该保持部署不变的标志。这种方法确保了我们在这种情况下想要的“选择加入”行为。

另一个有趣的片段来自 injectInitContainer()方法中的 JSONPatch 生成逻辑：

```java
JsonNode maybeInitContainers = originalSpec.path("initContainers");
ArrayNode initContainers = 
maybeInitContainers.isMissingNode() ?
  om.createArrayNode() : (ArrayNode) maybeInitContainers;
ArrayNode patchArray = om.createArrayNode();
ObjectNode addNode = patchArray.addObject();

addNode.put("op", "add");
addNode.put("path", "/spec/template/spec/initContainers");
ArrayNode values = addNode.putArray("values");
values.addAll(initContainers);

```

由于无法保证传入的规范包含 initContainers字段，我们必须处理两种情况：它们可能缺失或存在。如果它丢失了，我们使用一个ObjectMapper实例(上面代码片段中的 om)来创建一个新的ArrayNode。否则，我们只使用传入数组。

为此，我们可以使用一个“添加”补丁指令。尽管名称如此，但其行为是创建该字段或替换具有相同名称的现有字段。value字段 始终是一个数组，其中包括(可能为空的)原始initContainers数组。最后一步添加实际的 wait-for-it容器：

```java
ObjectNode wfi = values.addObject();
wfi.put("name", "wait-for-it-" + UUID.randomUUID())
// ... additional container fields added (omitted)
```

由于容器名称在 pod 内必须是唯一的，我们只需将随机 UUID 添加到固定前缀即可。这避免了与现有容器的任何名称冲突。

### 4.5. 部署

开始使用准入控制器的最后一步是将其部署到目标 Kubernetes 集群。正如预期的那样，这需要编写一些 YAML 或使用[Terraform](https://www.baeldung.com/ops/terraform-intro)等工具。无论哪种方式，这些都是我们需要创建的资源：

-   运行我们的准入控制器的部署。旋转此服务的多个副本是个好主意，因为故障可能会阻止任何新部署的发生
-   将请求从 API 服务器路由到运行准入控制器的可用 Pod的服务 
-   MutatingWebhookConfiguration资源 ，描述应将哪些 API 调用路由到我们的 服务

例如，假设我们希望 Kubernetes 在每次创建或更新部署时使用我们的准入控制器。在MutatingWebhookConfiguration文档中，我们将看到 这样的规则定义：

```yaml
apiVersion: admissionregistration.k8s.io/v1
kind: MutatingWebhookConfiguration
metadata:
  name: "wait-for-it.baeldung.com"
webhooks:
- name: "wait-for-it.baeldung.com"
  rules:
  - apiGroups:   [""]
    apiVersions: [""]
    operations:  ["CREATE","UPDATE"]
    resources:   ["deployments"]
  ... other fields omitted
```

关于我们服务器的重要一点：Kubernetes 需要 HTTPS 与外部准入控制器通信。这意味着我们需要为我们的 SpringBoot 服务器提供适当的证书和私钥。请检查用于部署示例准入控制器的 Terraform 脚本以查看执行此操作的一种方法。

另外，一个快速提示：虽然文档中没有提及，但某些 Kubernetes 实现(例如 GCP)需要使用端口 443，因此我们需要更改 SpringBoot HTTPS 端口的默认值(8443)。

### 4.6. 测试

一旦我们准备好部署工件，终于可以在现有集群中测试我们的准入控制器了。在我们的例子中，我们使用 Terraform 来执行部署，所以我们所要做的就是应用：

```bash
$ terraform apply -auto-approve
```

完成后，我们可以使用kubectl检查部署和准入控制器的状态：

```bash
$ kubectl get mutatingwebhookconfigurations
NAME                               WEBHOOKS   AGE
wait-for-it-admission-controller   1          58s
$ kubectl get deployments wait-for-it-admission-controller         
NAME                               READY   UP-TO-DATE   AVAILABLE   AGE
wait-for-it-admission-controller   1/1     1            1           10m

```

现在，让我们创建一个简单的 nginx 部署，包括我们的注解：

```bash
$ kubectl apply -f nginx.yaml
deployment.apps/frontend created
```

我们可以检查相关的日志，看看 wait-for-it init 容器确实被注入了：

```bash
 $ kubectl logs --since=1h --all-containers deployment/frontend
wait-for-it.sh: waiting 15 seconds for www.google.com:80
wait-for-it.sh: www.google.com:80 is available after 0 seconds
```

为了确定，让我们检查一下部署的 YAML：

```bash
$ kubectl get deployment/frontend -o yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  annotations:
    com.baeldung/wait-for-it: www.google.com:80
    deployment.kubernetes.io/revision: "1"
		... fields omitted
spec:
  ... fields omitted
  template:
	  ... metadata omitted
    spec:
      containers:
      - image: nginx:1.14.2
        name: nginx
				... some fields omitted
      initContainers:
      - args:
        - www.google.com:80
        image: willwill/wait-for-it
        imagePullPolicy: Always
        name: wait-for-it-b86c1ced-71cf-4607-b22b-acb33a548bb2
	... fields omitted
      ... fields omitted
status:
  ... status fields omitted
```

此输出显示 我们的准入控制器添加到部署中的initContainer 。

## 5.总结

在本文中，我们介绍了如何使用Java创建 Kubernetes 准入控制器并将其部署到现有集群。