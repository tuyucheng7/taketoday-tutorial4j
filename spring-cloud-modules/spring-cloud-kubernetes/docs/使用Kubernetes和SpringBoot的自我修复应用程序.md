## 1. 简介

在本教程中，我们将讨论 [Kubernetes](https://kubernetes.io/)的[探测器](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-probes/) 并演示我们如何利用 Actuator的 [HealthIndicator](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/actuate/health/HealthIndicator.html) 来准确查看应用程序的状态。

出于本教程的目的，我们将假设一些预先存在的 [Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)、 [Kubernetes](https://www.baeldung.com/kubernetes)和 [Docker](https://www.baeldung.com/dockerizing-spring-boot-application)经验。

## 2. Kubernetes 探针

Kubernetes定义了两种不同的探测器，我们可以使用它们定期检查一切是否按预期工作： liveness和 readiness。

### 2.1. 活跃度和就绪度

借助 Liveness和 Readiness探测器， [Kubelet](https://kubernetes.io/docs/reference/command-line-tools-reference/kubelet/) 可以在检测到某些东西关闭时立即采取行动，并最大限度地减少我们应用程序的停机时间。

两者的配置方式相同，但它们具有不同的语义，并且Kubelet 会根据触发的事件执行不同的操作：

-   就绪性—— 就绪性验证我们的 Pod是否准备好开始接收流量。当所有容器准备就绪时，我们的 Pod就 准备好了
-   Liveness——与 readiness相反， liveness检查我们的 Pod是否应该重启。它可以选择我们的应用程序正在运行但处于无法取得进展的状态的用例；例如，它处于死锁状态

我们在容器级别配置两种探测类型：

```java
apiVersion: v1
kind: Pod
metadata:
  name: goproxy
  labels:
    app: goproxy
spec:
  containers:
  - name: goproxy
    image: k8s.gcr.io/goproxy:0.1
    ports:
    - containerPort: 8080
    readinessProbe:
      tcpSocket:
        port: 8080
      initialDelaySeconds: 5
      periodSeconds: 10
      timeoutSeconds: 2
      failureThreshold: 1
      successThreshold: 1
    livenessProbe:
      tcpSocket:
        port: 8080
      initialDelaySeconds: 15
      periodSeconds: 20
      timeoutSeconds: 2
      failureThreshold: 1
      successThreshold: 1
```

我们可以配置许多字段以更精确地控制我们的探测器的行为：

-   initialDelaySeconds – 创建容器后，等待n 秒再启动探测
-   periodSeconds –该探测应该多久运行一次，默认为 10 秒；最小为 1 秒
-   timeoutSeconds –在探测超时之前我们等待多长时间，默认为 1 秒；最小值又是 1 秒
-   failureThreshold – 在放弃之前尝试 n次。在 readiness的情况下，我们的 pod 将被标记为未就绪，而在liveness的情况下放弃意味着重新启动 Pod。这里默认失败3次，最少1次
-   successThreshold – 这是探测失败后被视为成功的最少连续成功次数。它默认为 1 成功，其最小值也是 1

在本例中，我们选择了 tcp探测，但是，我们也可以使用其他类型的探测。

### 2.2. 探头类型

根据我们的用例，一种探针类型可能比另一种更有用。例如，如果我们的容器是 Web 服务器，则使用 http探测可能比使用 tcp探测更可靠。

幸运的是， Kubernetes提供了三种不同类型的探测器供我们使用：

-   exec –在我们的容器中执行 bash指令。例如，检查特定文件是否存在。如果指令返回失败代码，则探测失败
-   tcpSocket – 尝试使用指定端口与容器建立tcp 连接。如果无法建立连接，则探测失败
-   httpGet –向在容器中运行并侦听指定端口的服务器发送 HTTP GET 请求。任何大于或等于200且小于400的代码都表示成功

重要的是要注意 ，除了我们之前提到的那些之外， HTTP探测还有其他字段：

-   host – 要连接的主机名，默认为我们 pod 的 IP
-   scheme – 应该用于连接的方案， HTTP或HTTPS，默认为 HTTP
-   path – 在网络服务器上访问的路径
-   httpHeaders – 在请求中设置的自定义标头
-   port – 容器中要访问的端口的名称或编号

## 3. Spring Actuator和Kubernetes的自愈能力

现在我们对 Kubernetes如何能够检测我们的应用程序是否处于损坏状态有了一个大致的了解，让我们看看我们 如何 利用 Spring 的 执行器来 密切关注我们的应用程序及其依赖关系！

出于这些示例的目的，我们将依赖 [Minikube](https://www.baeldung.com/spring-boot-minikube)。

### 3.1. 执行器及其健康指标

考虑到 Spring 有许多 HealthIndicator可供使用，反映我们应用程序对 Kubernetes探测器的某些依赖项的状态就像将 [Actuator](https://www.baeldung.com/spring-boot-actuators)依赖项添加到我们的 pom.xml 一样简单：

```java
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 3.2. 活性示例

让我们从一个将正常启动并 在 30 秒 后 转换 为 损坏状态的 应用程序开始。

我们将通过[创建一个 ](https://www.baeldung.com/spring-boot-actuators)验证布尔变量是否为true的[HealthIndicator](https://www.baeldung.com/spring-boot-actuators)来模拟中断状态。我们将变量初始化为 true，然后我们将安排一个任务 在 30 秒后将其更改为false ：

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {

    private boolean isHealthy = true;

    public CustomHealthIndicator() {
        ScheduledExecutorService scheduled =
          Executors.newSingleThreadScheduledExecutor();
        scheduled.schedule(() -> {
            isHealthy = false;
        }, 30, TimeUnit.SECONDS);
    }

    @Override
    public Health health() {
        return isHealthy ? Health.up().build() : Health.down().build();
    }
}
```

有了我们的 HealthIndicator，我们需要将我们的应用程序 dockerize 化：

```java
FROM openjdk:8-jdk-alpine
RUN mkdir -p /usr/opt/service
COPY target/.jar /usr/opt/service/service.jar
EXPOSE 8080
ENTRYPOINT exec java -jar /usr/opt/service/service.jar
```

接下来，我们创建我们的 Kubernetes模板：

```java
apiVersion: apps/v1
kind: Deployment
metadata:
  name: liveness-example
spec:
  ...
    spec:
      containers:
      - name: liveness-example
        image: dbdock/liveness-example:1.0.0
        ...
        readinessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 10
          timeoutSeconds: 2
          periodSeconds: 3
          failureThreshold: 1
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 20
          timeoutSeconds: 2
          periodSeconds: 8
          failureThreshold: 1
```

我们正在使用 指向 Actuator 的健康端点的httpGet探针。我们应用程序状态(及其依赖项)的任何更改都将反映在我们部署的健康状况上。

将我们的应用程序部署到 Kubernetes后，我们将能够看到两个探测器都在运行：大约 30 秒后，我们的 Pod将被标记为未就绪并从轮换中移除；几秒钟后， Pod重新启动。

我们可以看到我们的Pod 执行 kubectl describe pod liveness -example的事件：

```java
Warning  Unhealthy 3s (x2 over 7s)   kubelet, minikube  Readiness probe failed: HTTP probe failed ...
Warning  Unhealthy 1s                kubelet, minikube  Liveness probe failed: HTTP probe failed ...
Normal   Killing   0s                kubelet, minikube  Killing container with id ...
```

### 3.3. 准备示例

在前面的示例中，我们了解了如何使用 HealthIndicator来反映应用程序在 Kubernetes部署的健康状况方面的状态。

让我们在不同的用例中使用它：假设我们的应用程序需要 一些 时间 才能接收流量。例如，它需要将文件加载到内存中并验证其内容。 

这是我们何时可以利用 就绪探测的一个很好的例子。

让我们修改上一个示例中的 HealthIndicator和 Kubernetes 模板，并使它们适应这个用例：

```java
@Component
public class CustomHealthIndicator implements HealthIndicator {

    private boolean isHealthy = false;

    public CustomHealthIndicator() {
        ScheduledExecutorService scheduled =
          Executors.newSingleThreadScheduledExecutor();
        scheduled.schedule(() -> {
            isHealthy = true;
        }, 40, TimeUnit.SECONDS);
    }

    @Override
    public Health health() {
        return isHealthy ? Health.up().build() : Health.down().build();
    }
}
```

我们将变量初始化为false，40 秒后，任务将执行并将其设置为 true。

接下来，我们使用以下模板对我们的应用程序进行 docker 化和部署：

```java
apiVersion: apps/v1
kind: Deployment
metadata:
  name: readiness-example
spec:
  ...
    spec:
      containers:
      - name: readiness-example
        image: dbdock/readiness-example:1.0.0
        ...
        readinessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 40
          timeoutSeconds: 2
          periodSeconds: 3
          failureThreshold: 2
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 100
          timeoutSeconds: 2
          periodSeconds: 8
          failureThreshold: 1
```

虽然相似，但我们需要指出的是探针配置的一些变化：

-   因为我们知道我们的应用程序需要大约 40 秒才能准备好接收流量，所以我们将就绪探测的 initialDelaySeconds增加到40秒
-   同样，我们将 liveness probe 的 initialDelaySeconds 增加到100 秒，以避免被 Kubernetes过早杀死

如果它在 40 秒后仍未完成，则它还有大约 60 秒的时间可以完成。之后，我们的 liveness探测器将启动并重启 Pod。

## 4. 总结

在本文中，我们讨论了 Kubernetes探测器以及我们如何使用 Spring 的 Actuator 来改进应用程序的健康监控。