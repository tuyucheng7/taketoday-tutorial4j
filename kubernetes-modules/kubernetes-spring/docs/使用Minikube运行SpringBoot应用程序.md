## 1. 概述

在[上一篇文章](https://www.baeldung.com/kubernetes)中，我们对 Kubernetes 进行了理论介绍。

在本教程中，我们将讨论如何在本地 Kubernetes 环境(也称为 Minikube)上部署Spring Boot应用程序。

作为本文的一部分，我们将：

-   在我们的本地机器上安装 Minikube
-   开发包含两个Spring Boot服务的示例应用程序
-   使用 Minikube 在单节点集群上设置应用程序
-   使用配置文件部署应用程序

## 2.安装Minikube

Minikube 的安装基本上包括三个步骤：安装 Hypervisor(如 VirtualBox)、CLI kubectl以及 Minikube 本身。

[官方文档](https://kubernetes.io/docs/tasks/tools/install-minikube/)为每个步骤以及所有流行的操作系统提供了详细说明。

完成安装后，我们可以启动 Minikube，将 VirtualBox 设置为 Hypervisor，并配置 kubectl以与名为minikube的集群通信：

```bash
$> minikube start
$> minikube config set vm-driver virtualbox
$> kubectl config use-context minikube
```

之后，我们可以验证kubectl是否 与我们的集群正确通信：

```bash
$> kubectl cluster-info
```

输出应如下所示：

```bash
Kubernetes master is running at https://192.168.99.100:8443
To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
```

在此阶段，我们将关闭响应中的 IP(在本例中为192.168.99.100)。我们稍后会将其称为NodeIP，它需要从集群外部调用资源，例如从我们的浏览器。

最后，我们可以检查集群的状态：

```bash
$> minikube dashboard
```

此命令在我们的默认浏览器中打开一个站点，该站点提供有关集群状态的广泛概述。

## 4. 演示应用

由于我们的集群现在正在运行并准备部署，我们需要一个演示应用程序。

为此，我们将创建一个简单的“Hello world”应用程序，其中包含两个Spring Boot服务，我们称之为前端和后端。

后端在端口 8080 上提供一个 REST 端点，返回一个包含其主机名的字符串。前端在端口 8081 上可用，它将简单地调用后端端点并返回其响应。

之后，我们必须从每个应用程序构建一个 Docker 镜像。[GitHub 上](https://github.com/eugenp/tutorials/tree/master/spring-cloud-modules/spring-cloud-kubernetes)还提供了为此所需的所有文件 。

有关如何构建 Docker 映像的详细说明，请查看 [Dockerizing aSpring BootApplication](https://www.baeldung.com/dockerizing-spring-boot-application#Dockerize)。

在这里我们必须确保我们在 Minikube 集群的 Docker 主机上触发构建过程，否则，Minikube 在稍后部署时将找不到图像。此外，我们主机上的工作区必须安装到 Minikube VM 中：

```bash
$> minikube ssh
$> cd /c/workspace/tutorials/spring-cloud/spring-cloud-kubernetes/demo-backend
$> docker build --file=Dockerfile 
  --tag=demo-backend:latest --rm=true .
```

之后，我们可以从 Minikube VM 注销，所有进一步的步骤都将使用kubectl和minikube命令行工具在我们的主机上执行。

## 5. 使用命令式命令进行简单部署

第一步，我们将为我们的演示后端应用程序创建一个 Deployment，它只包含一个 Pod。在此基础上，我们将讨论一些命令，以便我们可以验证 Deployment、检查日志并在最后清理它。

### 5.1. 创建部署

我们将使用kubectl，将所有必需的命令作为参数传递：

```bash
$> kubectl run demo-backend --image=demo-backend:latest 
  --port=8080 --image-pull-policy Never
```

如我们所见，我们创建了一个名为demo-backend 的 Deployment，它 是从一个也称为demo-backend的图像实例化的，版本为latest。

使用–port，我们指定 Deployment 为其 Pod 打开端口 8080(因为我们的演示后端应用程序侦听端口 8080)。

标志 –image-pull-policy Never 确保 Minikube 不会尝试从注册表中拉取图像，而是从本地 Docker 主机获取它。

### 5.2. 验证部署

现在，我们可以检查部署是否成功：

```bash
$> kubectl get deployments
```

输出如下所示：

```bash
NAME           DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
demo-backend   1         1         1            1           19s
```

如果我们想查看应用程序日志，我们首先需要 Pod ID：

```bash
$> kubectl get pods
$> kubectl logs <pod id>
```

### 5.3. 为部署创建服务

为了使后端应用程序的 REST 端点可用，我们需要创建一个服务：

```bash
$> kubectl expose deployment demo-backend --type=NodePort
```

–type=NodePort使服务从集群外部可用。它将在 <NodeIP>:<NodePort>可用，即该服务将在 <NodePort>传入的任何请求映射 到其分配的 Pod 的端口 8080。

我们使用 expose 命令，所以 NodePort 会由集群自动设置(这是技术限制)，默认范围是 30000-32767。要获得我们选择的端口，我们可以使用配置文件，我们将在下一节中看到。

我们可以验证服务是否已成功创建：

```bash
$> kubectl get services
```

输出如下所示：

```bash
NAME           TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
demo-backend   NodePort    10.106.11.133   <none>        8080:30117/TCP   11m
```

如我们所见，我们有一个名为demo-backend的服务，类型为NodePort，可在集群内部 IP 10.106.11.133 上使用。

我们必须仔细查看 PORT(S) 列：由于在 Deployment 中定义了端口 8080，Service 将流量转发到该端口。但是，如果我们想从我们的浏览器调用 演示后端 ，我们必须使用端口 30117，它可以从集群外部访问。

### 5.4. 调用服务

现在，我们可以第一次调用我们的后端服务：

```bash
$> minikube service demo-backend
```

此命令将启动我们的默认浏览器，打开 <NodeIP>:<NodePort>。在我们的示例中，它将是http://192.168.99.100:30117。

### 5.5. 清理服务和部署

之后，我们可以删除服务和部署：

```bash
$> kubectl delete service demo-backend
$> kubectl delete deployment demo-backend
```

## 6. 使用配置文件进行复杂部署

对于更复杂的设置，配置文件是更好的选择，而不是通过命令行参数传递所有参数。

配置文件是记录我们的部署的好方法，并且可以对其进行版本控制。

### 6.1. 我们后端应用程序的服务定义

让我们使用配置文件为后端重新定义我们的服务：

```plaintext
kind: Service
apiVersion: v1
metadata:
  name: demo-backend
spec:
  selector:
    app: demo-backend
  ports:
  - protocol: TCP
    port: 8080
  type: ClusterIP
```

我们创建一个名为demo-backend的服务，由metadata: name字段指示。

它以带有app=demo-backend 标签的任何 Pod 上的 TCP 端口 8080 为目标。

最后，键入：ClusterIP表示它只能从集群内部使用(因为这次我们想从我们的演示前端应用程序调用端点，但不再像前面的示例那样直接从浏览器调用)。

### 6.2. 后端应用程序的部署定义

接下来，我们可以定义实际的 Deployment：

```plaintext
apiVersion: apps/v1
kind: Deployment
metadata:
  name: demo-backend
spec:
  selector:
      matchLabels:
        app: demo-backend
  replicas: 3
  template:
    metadata:
      labels:
        app: demo-backend
    spec:
      containers:
        - name: demo-backend
          image: demo-backend:latest
          imagePullPolicy: Never
          ports:
            - containerPort: 8080
```

我们创建一个名为demo-backend的Deployment，由metadata: name字段指示。

spec : selector字段定义 Deployment 如何找到要管理的 Pod。在这种情况下，我们仅选择 Pod 模板中定义的一个标签 ( app:demo-backend )。

我们希望拥有三个的 Pod，由replicas字段指示。

模板字段定义了实际的 Pod：

-   Pod 被标记为 app: demo-backend
-   template : spec字段表示每个 Pod 运行一个容器demo-backend，版本最新
-   Pod 开放 8080 端口

### 6.3. 后端应用程序的部署

我们现在可以触发部署：

```bash
$> kubectl create -f backend-deployment.yaml
```

让我们验证部署是否成功：

```bash
$> kubectl get deployments
```

输出如下所示：

```bash
NAME           DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
demo-backend   3         3         3            3           25s
```

我们还可以检查服务是否可用：

```bash
$> kubectl get services
```

输出如下所示：

```bash
NAME            TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
demo-backend    ClusterIP   10.102.17.114   <none>        8080/TCP         30s
```

正如我们所见，该服务的类型为ClusterIP，并且它不提供 30000-32767 范围内的外部端口，这与我们之前在第 5 节中的示例不同。

### 6.4. 我们的前端应用程序的部署和服务定义

之后，我们可以为前端定义服务和部署：

```plaintext
kind: Service
apiVersion: v1
metadata:
  name: demo-frontend
spec:
  selector:
    app: demo-frontend
  ports:
  - protocol: TCP
    port: 8081
    nodePort: 30001
  type: NodePort
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: demo-frontend
spec:
  selector:
      matchLabels:
        app: demo-frontend
  replicas: 3
  template:
    metadata:
      labels:
        app: demo-frontend
    spec:
      containers:
        - name: demo-frontend
          image: demo-frontend:latest
          imagePullPolicy: Never
          ports:
            - containerPort: 8081
```

前端和后端几乎相同， 后端和前端之间的唯一区别是 服务的规范：

对于前端，我们将类型定义为NodePort (因为我们想让前端对集群外部可用)。后端只需可从集群内访问，因此类型为 ClusterIP。

如前所述，我们还使用nodePort字段手动指定NodePort。

### 6.5. 部署前端应用程序

我们现在可以用同样的方式触发这个部署：

```bash
$> kubectl create -f frontend-deployment.yaml
```

让我们快速验证部署是否成功并且服务可用：

```bash
$> kubectl get deployments
$> kubectl get services
```

之后，我们终于可以调用前端应用程序的 REST 端点了：

```bash
$> minikube service demo-frontend
```

此命令将再次启动我们的默认浏览器，打开<NodeIP>:<NodePort>，在本例中为http://192.168.99.100:30001 。

### 6.6. 清理服务和部署

最后，我们可以通过删除服务和部署来清理：

```bash
$> kubectl delete service demo-frontend
$> kubectl delete deployment demo-frontend
$> kubectl delete service demo-backend
$> kubectl delete deployment demo-backend
```

## 七. 总结

在本文中，我们快速了解了如何使用 Minikube 在本地 Kubernetes 集群上部署Spring Boot“Hello world” 应用程序。

我们详细讨论了如何：

-   在我们的本地机器上安装 Minikube
-   开发和构建一个包含两个Spring Boot应用程序的示例
-   使用带有kubectl的命令式命令以及配置文件在单节点集群上部署服务