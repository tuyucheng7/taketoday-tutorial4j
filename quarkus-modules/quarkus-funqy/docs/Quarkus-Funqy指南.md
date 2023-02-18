## 一、概述

[Quarkus](https://www.baeldung.com/quarkus-io)使我们能够以极快的启动时间和更短的首次联系响应时间交付小型工件。

在本教程中，我们将探索Quarkus 框架的[Funqy扩展。](https://quarkus.io/guides/funqy)

## 2. 什么是Funqy？

Quarkus Funqy 是一个旨在提供可移植 Java API 的解决方案，它允许我们编写无服务器函数。我们可以轻松地将这些功能部署到 FAAS 环境，例如 AWS [Lambda](https://www.baeldung.com/aws-serverless)、Azure Functions、Google Cloud Functions 和 Kubernetes [Knative](https://www.baeldung.com/ops/knative-serverless)。我们也可以将它们用作独立服务。

## 3.实施

让我们使用 Quarkus Funky 创建一个简单的问候功能，并将其部署在 FAAS 基础设施上。[我们可以使用Quarkus Web 界面](https://code.quarkus.io/?g=com.baeldung.quarkus&a=quarkus-funqy-project&j=11&e=funqy-http)创建一个项目。我们也可以使用maven通过执行以下命令来创建项目：

```bash
$ mvn io.quarkus:quarkus-maven-plugin:2.7.7.Final:create
  -DprojectGroupId=com.baeldung.quarkus
  -DprojectArtifactId=quarkus-funqy-project
  -Dextensions="funqy-http"复制
```

我们正在使用[*quarkus-maven-plugin*](https://search.maven.org/artifact/io.quarkus/quarkus-maven-plugin)来创建项目。它将生成一个带有函数类的项目框架。

让我们将此项目导入我们的 IDE，以获得与下图类似的结构：

[![Quarkus 项目](https://www.baeldung.com/wp-content/uploads/2023/02/quarkus-project-1-172x300.png)](https://www.baeldung.com/wp-content/uploads/2023/02/quarkus-project-1.png)

 

 

### 3.1. Java代码

我们打开*MyFunctions.java*文件，分析一下内容：

```java
public class MyFunctions {

    @Funq
    public String fun(FunInput input) {
        return String.format("Hello %s!", input != null ? input.name : "Funqy");
    }

    public static class FunInput {
        public String name;
        // constructors, getters, setters
    }
}复制
```

**注释\*@Funq\*将方法标记为入口点函数。**最多只能有一个方法参数，它可能会或可能不会返回响应。默认函数名是注解的方法名；*我们可以通过在@Funq*注释中传递名称字符串来更新它。

让我们将名称更新为*GreetUser*并添加一个简单的日志语句：

```java
@Funq("GreetUser")
public String fun(FunInput input) {
    log.info("Function Triggered");
    ...
}复制
```

## 4.部署

现在让我们打开*MyFunctionTest.java*类并更新所有测试用例中路径中提到的方法名称。我们将首先通过运行以下命令在本地运行它：

```bash
$ ./mvnw quarkus:dev复制
```

它将启动服务器并执行测试用例。

让我们使用*[curl](https://www.baeldung.com/curl-rest)*测试它：

```bash
$ curl -X POST 'http://localhost:8080/GreetUser'
--header 'Content-Type: application/json'
--data-raw '{
    "name": "Baeldung"
}'复制
```

它会给我们问候回应。

### 4.1. Kubernetes Knative

现在让我们将它部署在[Kubernetes Knative](https://www.baeldung.com/ops/knative-serverless)上。我们将在*pom.xml*文件中添加[*quarkus-funqy-knative-events*](https://search.maven.org/search?q=quarkus-funqy-knative-events)依赖项：

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-funqy-knative-events</artifactId>
    <version>3.0.0.Alpha3</version>
</dependency>复制
```

让我们用单元测试来测试一下：

```java
@Test
public void givenFunctionAPI_whenCallWithEvent_thenShouldReturn200() {
    RestAssured.given().contentType("application/json")
      .header("ce-specversion", "1.0")
      .header("ce-id", UUID.randomUUID().toString())
      .header("ce-type", "GreetUser")
      .header("ce-source", "test")
      .body("{ \"name\": \"Baeldung\" }")
      .post("/")
      .then().statusCode(200);
}复制
```

现在让我们创建应用程序的构建和映像：

```bash
$ ./mvnw install
$ docker build -f src/main/docker/Dockerfile.jvm -t
  <<dockerAccountName>>/quarkus-funqy-project .
$ docker push <<ourDockerAccountName>>/quarkus-funqy-project
复制
```

我们将在用于资源创建的*src/main/kubernetes*目录中创建 Kubernetes Knative 配置*knative.yaml文件：*

```yaml
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: quarkus-funqy-project
spec:
  template:
    metadata:
      name: quarkus-funqy-project-v1
    spec:
      containers:
        - image: docker.io/<<dockerAccountName>>/quarkus-funqy-project复制
```

现在我们只需要创建一个 broker，broker 事件配置 YAML 文件，并全部部署即可。

让我们创建一个*knative-trigger.yaml*文件：

```yaml
apiVersion: eventing.knative.dev/v1
kind: Trigger
metadata:
  name: baeldung-event
spec:
  broker: baeldung
  filter:
    attributes:
      type: GreetUser
  subscriber:
    ref:
      apiVersion: serving.knative.dev/v1
      kind: Service
      name: quarkus-funqy-project复制
$ kn broker create baeldung
$ kubectl apply -f src/main/kubernetes/knative.yaml
$ kubectl apply -f src/main/kubernetes/knative-trigger.yaml复制
```

让我们验证 pod 和 pod 日志，因为 pod 应该正在运行。如果我们不发送任何事件，pod 将自动缩小为零。让我们获取代理 URL 以发送事件：

```bash
$ kubectl get broker baeldung -o jsonpath='{.status.address.url}'复制
```

现在，我们可以从任何 pod 向此 URL 发送事件，并看到我们的 Quarkus 应用程序的一个新 pod 将启动（如果它已经关闭）。我们还可以检查日志以验证我们的函数是否被触发：

```bash
$ curl -v "<<our_broker_url>>" 
  -X POST
  -H "Ce-Id: 1234"
  -H "Ce-Specversion: 1.0"
  -H "Ce-Type: GreetUser"
  -H "Ce-Source: curl"
  -H "Content-Type: application/json"
  -d "{\"name\":\"Baeldung\"}"复制
```

### 4.2. 云部署

我们可以类似地更新我们的应用程序以部署在云平台上。但是，**每个云部署只能导出一个 Funqy 函数**。如果我们的应用程序有多个 Funqy 方法，我们可以通过在 application.properties 文件中添加以下内容来指定活动函数（将*GreetUser*替换为活动函数名称）：

```bash
quarkus.funqy.export=GreetUser复制
```

## 5.结论

在本文中，我们看到 Quarkus Funqy 是一个很好的补充，它可以帮助我们在无服务器基础设施上非常轻松地运行 Java 函数。我们了解了 Quarkus Funqy 以及如何在无服务器环境中实施、部署和测试它。