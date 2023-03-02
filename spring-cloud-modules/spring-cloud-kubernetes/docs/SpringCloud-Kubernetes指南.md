## 1. 概述

当我们构建微服务解决方案时，[Spring Cloud](https://www.baeldung.com/spring-cloud-series)和[Kubernetes](https://www.baeldung.com/kubernetes)都是最佳解决方案，因为它们提供了用于解决最常见挑战的组件。但是，如果我们决定选择Kubernetes作为我们解决方案的主要容器管理器和部署平台，我们仍然可以主要通过[Spring Cloud Kubernetes](https://cloud.spring.io/spring-cloud-static/spring-cloud-kubernetes/2.1.0.RC1/single/spring-cloud-kubernetes.html)项目使用Spring Cloud的有趣功能。

这个相对较新的项目无疑为[Spring Boot](https://www.baeldung.com/spring-boot-start)应用程序提供了与Kubernetes的轻松集成。在开始之前，了解一下如何在本地Kubernetes环境[Minikube上部署Spring Boot](https://www.baeldung.com/spring-boot-minikube)应用程序可能会有所帮助。

在本教程中，我们将：

-   在我们的本地机器上安装Minikube
-   开发一个微服务架构示例，其中两个独立的Spring Boot应用程序通过REST进行通信
-   使用Minikube在单节点集群上设置应用程序
-   使用YAML配置文件部署应用程序

## 2. 场景

在我们的示例中，我们使用的场景是旅行社向不时查询旅行社服务的客户提供各种优惠。我们将用它来演示：

-   通过Spring Cloud Kubernetes进行**服务发现**
-   **配置管理**以及使用Spring Cloud Kubernetes Config将Kubernetes ConfigMaps和密钥注入应用程序pod
-   使用Spring Cloud Kubernetes Ribbon进行**负载均衡**

## 3. 环境搭建

首先，我们需要**在本地机器上安装[Minikube](https://kubernetes.io/docs/setup/minikube/)**，最好是VM驱动程序，例如[VirtualBox](https://www.virtualbox.org/)。还建议在进行此环境设置之前查看[Kubernetes](https://kubernetes.io/)及其主要功能。

让我们启动本地单节点Kubernetes集群：

```shell
minikube start --vm-driver=virtualbox
```

此命令创建一个使用VirtualBox驱动程序运行Minikube集群的虚拟机。kubectl中的默认上下文现在将是minikube。但是，为了能够在上下文之间切换，我们使用：

```shell
kubectl config use-context minikube
```

启动Minikube后，我们可以**连接到Kubernetes仪表板**以访问日志并轻松监控我们的服务、pod、ConfigMaps和Secrets：

```shell
minikube dashboard
```

### 3.1 Deployment

首先，让我们从[GitHub](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-cloud-modules/spring-cloud-kubernetes)获取我们的示例。

此时，我们可以从父文件夹中运行“deployment-travel-client.sh”脚本，也可以一条一条地执行每条指令以更好地掌握流程：

```shell
### build the repository
mvn clean install

### set docker env
eval $(minikube docker-env)

### build the docker images on minikube
cd travel-agency-service
docker build -t travel-agency-service .
cd ../client-service
docker build -t client-service .
cd ..

### secret and mongodb
kubectl delete -f travel-agency-service/secret.yaml
kubectl delete -f travel-agency-service/mongo-deployment.yaml

kubectl create -f travel-agency-service/secret.yaml
kubectl create -f travel-agency-service/mongo-deployment.yaml

### travel-agency-service
kubectl delete -f travel-agency-service/travel-agency-deployment.yaml
kubectl create -f travel-agency-service/travel-agency-deployment.yaml

### client-service
kubectl delete configmap client-service
kubectl delete -f client-service/client-service-deployment.yaml

kubectl create -f client-service/client-config.yaml
kubectl create -f client-service/client-service-deployment.yaml

# Check that the pods are running
kubectl get pods
```

## 4. 服务发现

该项目为我们提供了Kubernetes中ServiceDiscovery接口的实现。在微服务环境中，通常有多个Pod运行同一个服务。**Kubernetes将服务公开为一组端点**，可以从在同一Kubernetes集群中的pod中运行的Spring Boot应用程序中获取和访问这些端点。

例如，在我们的示例中，我们有旅行社服务的多个副本，这些副本作为http://travel-agency-service:8080从我们的客户端服务访问。但是，这在内部会转化为访问不同的pod，例如travel-agency-service-7c9cfff655-4hxnp。

**Spring Cloud Kubernetes Ribbon使用此功能在服务的不同端点之间进行负载均衡**。

我们可以通过在客户端应用程序上添加[spring-cloud-starter-kubernetes](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-kubernetes/1.1.10.RELEASE)依赖项来轻松使用服务发现：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-kubernetes</artifactId>
</dependency>
```

此外，我们应该添加@EnableDiscoveryClient并通过在我们的类中使用@Autowired将DiscoveryClient注入到ClientController中：

```java
@SpringBootApplication
@EnableDiscoveryClient
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

```java
@RestController
public class ClientController {
    @Autowired
    private DiscoveryClient discoveryClient;
}
```

## 5. ConfigMaps

通常，**微服务需要某种配置管理**。例如，在Spring Cloud应用程序中，我们将使用Spring Cloud Config Server。

然而，我们可以通过使用Kubernetes提供的[ConfigMaps](https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/)来实现这一点-前提是我们打算将其仅用于非敏感、未加密的信息。或者，如果我们要共享的信息是敏感的，那么我们应该选择使用[Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)。

在我们的示例中，我们在客户端服务Spring Boot应用程序上使用ConfigMap。让我们创建一个client-config.yaml文件来定义客户端服务的ConfigMap：

```yaml
apiVersion: v1 by d
kind: ConfigMap
metadata:
    name: client-service
data:
    application.properties: |-
        bean.message=Testing reload! Message from backend is: %s <br/> Services : %s
```

**ConfigMap的名称必须与我们的“application.properties”文件中指定的应用程序名称相匹配**，这一点很重要。在这种情况下，它是client-service。接下来，我们应该为Kubernetes上的客户端服务创建ConfigMap：

```shell
kubectl create -f client-config.yaml
```

现在，让我们使用@Configuration和@ConfigurationProperties创建一个配置类ClientConfig并注入到ClientController中：

```java
@Configuration
@ConfigurationProperties(prefix = "bean")
public class ClientConfig {

    private String message = "Message from backend is: %s <br/> Services : %s";

    // getters and setters
}
```

```java
@RestController
public class ClientController {

    @Autowired
    private ClientConfig config;

    @GetMapping
    public String load() {
        return String.format(config.getMessage(), "", "");
    }
}
```

如果我们不指定ConfigMap，那么我们应该期望看到在类中设置的默认消息。但是，当我们创建ConfigMap时，此默认消息会被该属性覆盖。

此外，每次我们决定更新ConfigMap时，页面上的消息都会相应更改：

```shell
kubectl edit configmap client-service
```

## 6. Secrets

让我们通过查看示例中MongoDB连接设置的规范来了解[Secrets](https://kubernetes.io/docs/concepts/configuration/secret/)是如何工作的。我们将在Kubernetes上创建环境变量，然后将其注入到Spring Boot应用程序中。

### 6.1 创建Secret

第一步是创建一个secret.yaml文件，将用户名和密码编码为Base64：

```yaml
apiVersion: v1
kind: Secret
metadata:
    name: db-secret
data:
    username: dXNlcg==
    password: cDQ1NXcwcmQ=
```

让我们在Kubernetes集群上应用Secret配置：

```shell
kubectl apply -f secret.yaml
```

### 6.2 创建MongoDB服务

我们现在应该创建MongoDB服务和部署travel-agency-deployment.yaml文件。特别是，在部署部分，我们将使用我们之前定义的Secret用户名和密码：

```yaml
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
    name: mongo
spec:
    replicas: 1
    template:
        metadata:
            labels:
                service: mongo
            name: mongodb-service
        spec:
            containers:
                -   args:
                        - mongod
                        - --smallfiles
                    image: mongo:latest
                    name: mongo
                    env:
                        -   name: MONGO_INITDB_ROOT_USERNAME
                            valueFrom:
                                secretKeyRef:
                                    name: db-secret
                                    key: username
                        -   name: MONGO_INITDB_ROOT_PASSWORD
                            valueFrom:
                                secretKeyRef:
                                    name: db-secret
                                    key: password
```

默认情况下，mongo:latest镜像将在名为admin的数据库上使用username和password创建用户。

### 6.3 在旅行社服务上设置MongoDB

请务必更新应用程序属性以添加数据库相关信息。虽然我们可以自由指定数据库名称admin，但在这里我们隐藏了最敏感的信息，例如用户名和密码：

```properties
spring.cloud.kubernetes.reload.enabled=true
spring.cloud.kubernetes.secrets.name=db-secret
spring.data.mongodb.host=mongodb-service
spring.data.mongodb.port=27017
spring.data.mongodb.database=admin
spring.data.mongodb.username=${MONGO_USERNAME}
spring.data.mongodb.password=${MONGO_PASSWORD}
```

现在，让我们看一下我们的travel-agency-deployment属性文件，以使用连接到mongodb-service所需的用户名和密码信息更新服务和部署。

这是文件的相关部分，其中部分与MongoDB连接相关：

```yaml
env:
    -   name: MONGO_USERNAME
        valueFrom:
            secretKeyRef:
                name: db-secret
                key: username
    -   name: MONGO_PASSWORD
        valueFrom:
            secretKeyRef:
                name: db-secret
                key: password
```

## 7. 与Ribbon通信

在微服务环境中，我们通常需要复制我们的服务的pod列表，以便执行负载均衡。这是通过使用Spring Cloud Kubernetes Ribbon提供的机制来完成的。**此机制可以自动发现并访问特定服务的所有端点**，随后，它会使用有关端点的信息填充Ribbon ServerList。

让我们首先将[spring-cloud-starter-kubernetes-ribbon](https://central.sonatype.com/artifact/org.springframework.cloud/spring-cloud-starter-kubernetes-ribbon/1.1.10.RELEASE)依赖项添加到我们的客户端服务pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-kubernetes-ribbon</artifactId>
</dependency>
```

下一步是将注解@RibbonClient添加到我们的客户端服务应用程序：

```java
@RibbonClient(name = "travel-agency-service")
```

填充端点列表后，Kubernetes客户端将搜索与使用@RibbonClient注解定义的服务名称匹配的当前命名空间/项目中的已注册端点。

我们还需要在应用程序属性中启用Ribbon客户端：

```properties
ribbon.http.client.enabled=true
```

## 8. 附加功能

### 8.1 Hystrix

**[Hystrix](https://www.baeldung.com/introduction-to-hystrix)有助于构建容错且有弹性的应用程序**，它的主要目标是快速失败和快速恢复。

特别是，在我们的示例中，我们使用Hystrix通过使用@EnableCircuitBreaker标注Spring Boot应用程序类来在客户端-服务器上实现断路器模式。

此外，我们通过使用@HystrixCommand()标注方法TravelAgencyService.getDeals()来使用回退功能。这意味着在回退的情况下，将调用getFallBackName()并返回“Fallback”消息：

```java
@HystrixCommand(fallbackMethod = "getFallbackName", commandProperties = { 
    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000") })
public String getDeals() {
    return this.restTemplate.getForObject("http://travel-agency-service:8080/deals", String.class);
}

private String getFallbackName() {
    return "Fallback";
}
```

### 8.2 Pod健康指标

我们可以利用Spring Boot HealthIndicator和[Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)向用户公开与健康相关的信息。

特别是，Kubernetes健康指标提供：

-   pod名称
-   IP地址
-   命名空间
-   服务帐号
-   节点名称
-   指示Spring Boot应用程序是Kubernetes内部还是外部的标志

## 9. 总结

在本文中，我们全面概述了Spring Cloud Kubernetes项目。

那么我们为什么要使用它呢？如果我们支持Kubernetes作为微服务平台，但仍然欣赏Spring Cloud的特性，那么Spring Cloud Kubernetes为我们提供了两全其美的优势。