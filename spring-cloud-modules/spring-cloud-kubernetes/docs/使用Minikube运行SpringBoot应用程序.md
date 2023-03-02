## 1. 概述

在[上一篇](https://www.baeldung.com/kubernetes)文章中，我们对Kubernetes进行了理论介绍。

在本教程中，**我们将讨论如何在本地Kubernetes环境(也称为Minikube)上部署Spring Boot应用程序**。

作为本文的一部分，我们将：

-   在我们的本地机器上安装Minikube
-   开发一个由两个Spring Boot服务组成的示例应用程序
-   使用Minikube在单节点集群上设置应用程序
-   使用配置文件部署应用程序

## 2. 安装Minikube

Minikube的安装基本上包括三个步骤：安装Hypervisor(如VirtualBox)、CLI kubectl以及Minikube本身。

[官方文档](https://kubernetes.io/docs/tasks/tools/install-minikube/)为每个步骤以及所有流行的操作系统提供了详细说明。

完成安装后，我们可以启动Minikube，将VirtualBox设置为Hypervisor，并配置kubectl以与名为minikube的集群通信：

```shell
$> minikube start
$> minikube config set vm-driver virtualbox
$> kubectl config use-context minikube
```

之后，我们可以验证kubectl是否与我们的集群正确通信：

```shell
$> kubectl cluster-info
```

输出应如下所示：

```shell
Kubernetes master is running at https://192.168.99.100:8443
To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
```

在此阶段，我们将关闭响应中的IP(在本例中为192.168.99.100)。我们稍后会将其称为NodeIP，它需要从集群外部调用资源，例如从我们的浏览器。

最后，我们可以检查集群的状态：

```shell
$> minikube dashboard
```

此命令在我们的默认浏览器中打开一个站点，该站点提供有关集群状态的广泛概述。

## 3. Demo应用程序

由于我们的集群现在正在运行并准备好进行部署，因此我们需要一个演示应用程序。

为此，我们将创建一个简单的“Hello world”应用程序，其中包含两个Spring Boot服务，我们称之为前端和后端。

后端在端口8080上提供一个REST端点，返回一个包含其主机名的字符串。前端在端口8081上可用，它将简单地调用后端端点并返回其响应。

之后，我们必须从每个应用程序构建一个Docker镜像。[GitHub上](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-cloud-modules/spring-cloud-kubernetes)还提供了为此所需的所有文件。

有关如何构建Docker镜像的详细说明，请查看[容器化一个Spring应用程序](https://www.baeldung.com/dockerizing-spring-boot-application#Dockerize)。

**在这里我们必须确保我们在Minikube集群的Docker主机上触发构建过程，否则，Minikube在稍后部署时将找不到镜像**。此外，我们主机上的工作区必须挂载到Minikube VM中：

```shell
$> minikube ssh
$> cd /c/workspace/tutorials/spring-cloud/spring-cloud-kubernetes/demo-backend
$> docker build --file=Dockerfile \
  --tag=demo-backend:latest --rm=true .
```

之后，我们可以从Minikube VM注销，所有进一步的步骤都将使用kubectl和minikube命令行工具在我们的主机上执行。

## 4. 使用命令式命令进行简单部署

第一步，我们将为我们的Demo后端应用程序创建一个Deployment，它只包含一个Pod。在此基础上，我们将讨论一些命令，以便我们可以验证Deployment、检查日志并在最后清理它。

### 4.1 创建Deployment

我们将使用kubectl，将所有必需的命令作为参数传递：

```shell
$> kubectl run demo-backend --image=demo-backend:latest \
  --port=8080 --image-pull-policy Never
```

正如我们所看到的，我们创建了一个名为demo-backend的Deployment，它是从一个也称为demo-backend的镜像实例化的，版本为latest。

使用–port，我们指定Deployment为其Pod打开端口8080(因为我们的Demo后端应用程序监听端口8080)。

标志–image-pull-policy Never确保Minikube不会尝试从注册表中拉取镜像，而是从本地Docker主机获取镜像。

### 4.2 验证Deployment

现在，我们可以检查部署是否成功：

```shell
$> kubectl get deployments
```

输出如下所示：

```shell
NAME           DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
demo-backend   1         1         1            1           19s
```

如果我们想查看应用程序日志，我们首先需要Pod ID：

```shell
$> kubectl get pods
$> kubectl logs <pod id>
```

### 4.3 为部署创建服务

**为了使后端应用程序的REST端点可用，我们需要创建一个服务**：

```shell
$> kubectl expose deployment demo-backend --type=NodePort
```

–type=NodePort使服务可从群集外部使用。它将在<NodeIP\>:<NodePort\>可用，即该服务将在<NodePort\>传入的任何请求映射到其分配的Pod的端口8080。

我们使用expose命令，所以NodePort会由集群自动设置(这是一个技术限制)，默认范围是30000-32767。要获得我们选择的端口，我们可以使用配置文件，我们将在下一节中看到。

我们可以验证服务是否已成功创建：

```shell
$> kubectl get services
```

输出如下所示：

```shell
NAME           TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
demo-backend   NodePort    10.106.11.133   <none>        8080:30117/TCP   11m
```

如我们所见，我们有一个名为demo-backend的服务，类型为NodePort，可在集群内部IP 10.106.11.133上使用。

我们必须仔细查看PORT(S)列：由于在Deployment中定义了端口8080，因此Service将流量转发到此端口。但是，如果我们想从我们的浏览器调用Demo后端，则我们必须使用端口30117，该端口可从集群外部访问。

### 4.4 调用服务

现在，我们可以第一次调用我们的后端服务：

```shell
$> minikube service demo-backend
```

此命令将启动我们的默认浏览器，打开<NodeIP\>:<NodePort\>。在我们的示例中，它将是http://192.168.99.100:30117。

### 4.5 清理服务和部署

之后，我们可以删除服务和部署：

```shell
$> kubectl delete service demo-backend
$> kubectl delete deployment demo-backend
```

## 5. 使用配置文件进行复杂部署

对于更复杂的设置，配置文件是更好的选择，而不是通过命令行参数传递所有参数。

配置文件是记录我们的部署的好方法，并且可以对其进行版本控制。

### 5.1 后端应用的服务定义

让我们使用配置文件为后端重新定义我们的服务：

```yaml
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

它以带有app=demo-backend标签的任何Pod上的TCP端口8080为目标。

最后，type: ClusterIP表示它只能从集群内部使用(因为这次我们希望从我们的Demo前端应用程序调用端点，但不再像前面的示例那样直接从浏览器调用)。

### 5.2 后端应用程序的部署定义

接下来，我们可以定义实际的Deployment：

```yaml
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

spec: selector字段定义Deployment如何找到要管理的Pod。在这种情况下，我们仅选择Pod模板中定义的一个标签(app:demo-backend)。

我们希望拥有三个复制的Pod，由replicas字段指示。

template字段定义了实际的Pod：

-   Pod被标记为app: demo-backend
-   template: spec字段表示每个Pod复制运行一个容器demo-backend，版本latest
-   Pod开放8080端口

### 5.3 后端应用程序的部署

现在，我们可以触发部署：

```shell
$> kubectl create -f backend-deployment.yaml
```

让我们验证部署是否成功：

```shell
$> kubectl get deployments
```

输出如下所示：

```shell
NAME           DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
demo-backend   3         3         3            3           25s
```

我们还可以检查服务是否可用：

```shell
$> kubectl get services
```

输出如下所示：

```shell
NAME            TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
demo-backend    ClusterIP   10.102.17.114   <none>        8080/TCP         30s
```

正如我们所见，该服务的类型为ClusterIP，它不提供30000-32767范围内的外部端口，这与我们之前在第5节中的示例不同。

### 5.4 前端应用程序的部署和服务定义

之后，我们可以为前端定义服务和部署：

```yaml
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

前端和后端几乎相同，**后端和前端之间的唯一区别是服务的规范**。

对于前端，我们将类型定义为NodePort(因为我们想让前端对集群外部可用)。后端只需可从集群内访问，因此类型为ClusterIP。

如前所述，我们还使用nodePort字段手动指定NodePort。

### 5.5 部署前端应用程序

我们现在可以用同样的方式触发这个部署：

```shell
$> kubectl create -f frontend-deployment.yaml
```

让我们快速验证部署是否成功并且服务可用：

```shell
$> kubectl get deployments
$> kubectl get services
```

之后，我们终于可以调用前端应用程序的REST端点了：

```shell
$> minikube service demo-frontend
```

此命令将再次启动我们的默认浏览器，打开<NodeIP\>:<NodePort\>，在本例中为http://192.168.99.100:30001。

### 5.6 清理服务和部署

最后，我们可以通过删除服务和部署来清理：

```shell
$> kubectl delete service demo-frontend
$> kubectl delete deployment demo-frontend
$> kubectl delete service demo-backend
$> kubectl delete deployment demo-backend
```

## 6. 总结

在本文中，我们快速了解了如何使用Minikube在本地Kubernetes集群上部署Spring Boot “Hello world”应用程序。

我们详细讨论了如何：

-   在我们的本地机器上安装Minikube
-   开发并构建一个由两个Spring Boot应用程序组成的示例
-   使用带有kubectl的命令式命令以及配置文件在单节点集群上部署服务