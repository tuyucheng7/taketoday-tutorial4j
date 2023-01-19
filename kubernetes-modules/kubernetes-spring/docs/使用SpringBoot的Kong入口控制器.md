## 1. 概述

[Kubernetes (K8s)](https://www.baeldung.com/ops/kubernetes)是一种自动化软件开发和部署的编排器，是当今流行的 API 托管选择，可在本地或云服务(例如 Google Cloud Kubernetes Service (GKS) 或 Amazon Elastic Kubernetes Service (EKS))上运行。另一方面，[Spring](https://www.baeldung.com/spring-boot-minikube)已经成为最流行的Java框架之一。

在本教程中，我们将演示如何设置一个受保护的环境，以使用 Kong Ingress Controller (KIC) 在 Kubernetes 上部署我们的Spring Boot应用程序。我们还将通过为我们的应用程序实现一个简单的速率限制器而无需任何编码来演示 KIC 的高级用法。

## 2. 改进的安全和访问控制

现代应用程序部署，尤其是 API，需要应对许多挑战，例如：隐私法(例如 GPDR)、安全问题 (DDOS) 和使用跟踪(例如 API 配额和速率限制)。在这种情况下，现代应用程序和 API 需要额外的保护级别来应对所有这些挑战，例如防火墙、反向代理、速率限制器和相关服务。尽管 K8s 环境保护我们的应用程序免受其中许多威胁，但我们仍然需要采取一些措施来确保我们的应用程序安全。这些措施之一是部署入口控制器并设置其对你的应用程序的访问规则。

Ingress 是一个对象，它通过向部署的应用程序公开 HTTP / HTTPS 路由并强制执行访问规则来管理对 K8s 集群及其上部署的应用程序的外部访问。为了暴露应用程序以允许外部访问，我们需要定义入口规则并使用入口控制器，它是一个专门的反向代理和负载平衡器。通常，ingress controller 由第三方公司提供，功能各不相同，例如 本文使用的[Kong Ingress Controller 。](https://docs.konghq.com/kubernetes-ingress-controller/latest/)

## 3.搭建环境

为了演示 Kong Ingress Controller (KIC) 与Spring Boot应用程序的结合使用，我们需要访问 K8s 集群，因此我们可以使用完整的 Kubernetes、本地安装或云提供的，或者开发我们的示例应用程序[使用迷你库](https://www.baeldung.com/spring-boot-minikube)。启动我们的 K8s 环境后，我们需要[在我们的集群上](https://docs.konghq.com/kubernetes-ingress-controller/2.7.x/guides/getting-started/)部署 Kong Ingress Controller 。Kong 公开了一个外部 IP，我们需要使用它来访问我们的应用程序，因此最好使用该地址创建一个环境变量：

```bash
export PROXY_IP=$(minikube service -n kong kong-proxy --url | head -1)
```

而已！Kong Ingress Controller 已安装，我们可以通过访问该PROXY_IP来测试它是否正在运行：

```bash
curl -i $PROXY_IP
```

响应应该是 404 错误，这是正确的，因为我们还没有部署任何应用程序，所以它应该说没有路由与这些值匹配。[现在是创建示例应用程序的时候了，但在此之前，如果Docker](https://www.google.com/search?client=safari&rls=en&q=baeldung+docker&ie=UTF-8&oe=UTF-8) 不可用，我们可能需要安装它。为了将我们的应用程序部署到 K8s，我们需要一种创建容器镜像的方法，我们可以使用 Docker 来完成。

## 4. 创建一个示例Spring Boot应用程序

现在我们需要一个Spring Boot应用程序并将其部署到 K8s 集群。要生成具有至少一个公开 Web 资源的简单 HTTP 服务器应用程序，我们可以这样做：

```bash
curl https://start.spring.io/starter.tgz -d dependencies=webflux,actuator -d type=maven-project | tar -xzvf -
```

一件重要的事情是选择默认的Java版本。如果我们需要使用旧版本，则需要一个javaVersion属性：

```
curl https://start.spring.io/starter.tgz -d dependencies=webflux,actuator -d type=maven-project -d javaVersion=11 | tar -xzvf -
```

在此示例应用程序中，我们选择了webflux，它使用[Spring WebFlux 和 Netty](https://www.baeldung.com/spring-webflux)生成反应式 Web 应用程序。但是添加了另一个重要的依赖项。 [actuator](https://www.baeldung.com/spring-boot-actuators)，它是一个Spring应用的监控工具，已经暴露了一些web资源，这正是我们需要用Kong进行测试的。这样，我们的应用程序已经公开了一些我们可以使用的 Web 资源。让我们构建它：

```bash
./mvnw install
```

生成的 jar 是可执行的，所以我们可以通过运行它来测试应用程序：

java -jar 目标/.jar

要测试应用程序，我们需要打开另一个终端并键入以下命令：

```bash
curl -i http://localhost:8080/actuator/health
```

响应必须是执行器提供的应用程序的健康状态：

```bash
HTTP/1.1 200 OK
Content-Type: application/vnd.spring-boot.actuator.v3+json
Content-Length: 15

{"status":"UP"}
```

## 5. 从应用生成容器镜像

将应用程序部署到 Kubernetes 集群的过程涉及创建容器映像并将其部署到集群可访问的存储库。在现实生活中，我们会将镜像推送到 DockerHub 或我们自己的私有容器镜像注册中心。但是，由于我们正在使用 Minikube，让我们将 Docker 客户端环境变量指向 Minikube 的 Docker：

```bash
$(minikube docker-env)
```

我们可以构建应用程序映像：

```bash
./mvnw spring-boot:build-image
```

## 6. 部署应用

现在是时候在我们的 K8s 集群上部署应用程序了。我们需要创建一些 K8s 对象来部署和测试我们的应用程序，所有需要的文件都可以在演示的存储库中找到：

-   具有容器规范的应用程序的部署对象
-   为我们的 Pod 分配集群 IP 地址的服务定义
-   在我们的路由中使用 Kong 的代理 IP 地址的入口规则

部署对象只是创建运行我们的图像所需的 pod，这是创建它的 YAML 文件：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  creationTimestamp: null
  labels:
    app: demo
  name: demo
spec:
  replicas: 1
  selector:
    matchLabels:
      app: demo
  strategy: {}
  template:
    metadata:
      creationTimestamp: null
      labels:
        app: demo
    spec:
      containers:
      - image: docker.io/library/demo:0.0.1-SNAPSHOT
        name: demo
        resources: {}
        imagePullPolicy: Never

status: {}

```

我们指向在 Minikube 中创建的图像，获取其全名。请注意，有必要将imagePullPolicy 属性指定为Never ，因为我们没有使用镜像注册服务器，因此我们不希望 K8s 尝试下载镜像，而是使用其内部 Docker 存档中已有的镜像。我们可以使用kubectl部署它：

```bash
kubectl apply -f serviceDeployment.yaml
```

如果部署成功，我们可以看到消息：

```bash
deployment.apps/demo created
```

为了让我们的应用程序有一个统一的 IP 地址，我们需要创建一个服务，为它分配一个内部集群范围的 IP 地址，这是创建它的 YAML 文件：

```yaml
apiVersion: v1
kind: Service
metadata:
  creationTimestamp: null
  labels:
    app: demo
  name: demo
spec:
  ports:
  - name: 8080-8080
    port: 8080
    protocol: TCP
    targetPort: 8080
  selector:
    app: demo
  type: ClusterIP
status:
  loadBalancer: {}

```

现在我们也可以使用kubectl部署它：

```bash
kubectl apply -f clusterIp.yaml
```

请注意，我们正在选择指向我们部署的应用程序的标签演示。为了可以从外部访问(在 K8s 集群之外)，我们需要创建一个入口规则，在我们的例子中，我们将它指向路径/actuator/health和端口 8080：

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: demo
spec:
  ingressClassName: kong
  rules:
  - http:
      paths:
      - path: /actuator/health
        pathType: ImplementationSpecific
        backend:
          service:
            name: demo
            port:
              number: 8080
```

最后，我们使用kubectl部署它：

```bash
kubectl apply -f ingress-rule.yaml

```

现在我们可以使用 Kong 的代理 IP 地址进行外部访问：

```bash
$ curl -i $PROXY_IP/actuator/health
HTTP/1.1 200 OK
Content-Type: application/vnd.spring-boot.actuator.v3+json
Content-Length: 49
Connection: keep-alive
X-Kong-Upstream-Latency: 325
X-Kong-Proxy-Latency: 1
Via: kong/3.0.0
```

## 7. 演示速率限制器

我们设法在 Kubernetes 上部署了一个Spring Boot应用程序，并使用 Kong Ingress Controller 提供对它的访问。但 KIC 的功能远不止于此：身份验证、负载均衡、监控、速率限制和其他功能。为了展示 Kong 的真正力量，我们将对我们的应用程序实施一个简单的速率限制器，限制每分钟只能访问五个请求。为此，我们需要 在 K8s 集群中创建一个名为KongClusterPlugin的对象。YAML 文件执行以下操作：

```yaml
apiVersion: configuration.konghq.com/v1
kind: KongClusterPlugin
metadata:
  name: global-rate-limit
  annotations:
    kubernetes.io/ingress.class: kong
  labels:
    global: true
config:
  minute: 5
  policy: local
plugin: rate-limiting
```

插件配置允许我们为我们的应用程序指定额外的访问规则，我们将对它的访问限制为每分钟五个请求。让我们应用此配置并测试结果：

```bash
kubectl apply -f rate-limiter.yaml
```

为了测试它，我们可以在一分钟内重复我们之前使用的 CURL 命令五次以上，我们会得到一个 429 错误：

```bash
curl -i $PROXY_IP/actuator/health
HTTP/1.1 429 Too Many Requests
Date: Sun, 06 Nov 2022 19:33:36 GMT
Content-Type: application/json; charset=utf-8
Connection: keep-alive
RateLimit-Reset: 24
Retry-After: 24
X-RateLimit-Remaining-Minute: 0
X-RateLimit-Limit-Minute: 5
RateLimit-Remaining: 0
RateLimit-Limit: 5
Content-Length: 41
X-Kong-Response-Latency: 0
Server: kong/3.0.0

{
  "message":"API rate limit exceeded"
}
```

我们可以看到响应 HTTP 标头通知客户端有关速率限制。

## 8.清理资源

为了清理演示，我们需要按 LIFO 顺序删除所有对象：

```bash
kubectl delete -f rate-limiter.yaml
kubectl delete -f ingress-rule.yaml
kubectl delete -f clusterIp.yaml
kubectl delete -f serviceDeployment.yaml
```

并停止 Minikube 集群：

```bash
minikube stop
```

## 九. 总结

在本文中，我们演示了使用 Kong Ingress Controller 来管理对部署在 K8s 集群上的Spring Boot应用程序的访问。