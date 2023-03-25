## 1. 概述

[Quarkus](https://www.baeldung.com/quarkus-io)使我们能够以极快的启动时间和更短的首次访问响应时间交付小型工件。

在本教程中，我们将探索Quarkus框架的[Funqy扩展](https://quarkus.io/guides/funqy)。

## 2. 什么是Funqy？

Quarkus Funqy是一个旨在提供可移植Java API的解决方案，它允许我们编写Serverless函数。我们可以轻松地将这些函数部署到FAAS环境中，例如[AWS Lambda](https://www.baeldung.com/aws-serverless)、Azure Functions、Google Cloud Functions和[Kubernetes Knative](https://www.baeldung.com/ops/knative-serverless)。我们也可以将它们用作独立服务。

## 3. 实现

让我们使用Quarkus Funky创建一个简单的问候函数，并将其部署在FAAS基础设施上。我们可以使用[Quarkus Web界面](https://code.quarkus.io/?g=com.baeldung.quarkus&a=quarkus-funqy-project&j=11&e=funqy-http)创建一个项目。我们也可以使用Maven通过执行以下命令来创建项目：

```shell
$ mvn io.quarkus:quarkus-maven-plugin:2.7.7.Final:create \
  -DprojectGroupId=cn.tuyucheng.taketoday.quarkus \
  -DprojectArtifactId=quarkus-funqy-project \
  -Dextensions="funqy-http"
```

我们使用[quarkus-maven-plugin](https://search.maven.org/artifact/io.quarkus/quarkus-maven-plugin)来创建项目。它将生成一个带有函数类的项目框架。

让我们将此项目导入到IDE中，项目结构与下图类似：

<img src="../assets/img.png">

### 3.1 Java代码

让我们打开MyFunctions.java文件，分析一下内容：

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
}
```

**注解@Funq将方法标记为入口点函数**。最多只能有一个方法参数，它可能会或可能不会返回响应。默认函数名称是带注解的方法名称；我们可以通过在@Funq注解中传递名称字符串来更新它。

让我们将名称更新为GreetUser并添加一个简单的日志语句：

```java
@Funq("GreetUser")
public String fun(FunInput input) {
    log.info("Function Triggered");
    // ...
}
```

## 4. 部署

现在让我们打开MyFunctionTest.java类并更新所有测试用例中路径中提到的方法名称。我们将首先通过运行以下命令在本地运行它：

```shell
$ mvn quarkus:dev
```

它将启动服务器并执行测试用例。

让我们使用[curl](https://www.baeldung.com/curl-rest)测试它：

```shell
$ curl -X POST 'http://localhost:8080/GreetUser'
--header 'Content-Type: application/json'
--data-raw '{
    "name": "Tuyucheng"
}'
```

它会返回给我们问候响应。

### 4.1 Kubernetes Knative

现在让我们将它部署到[Kubernetes Knative](https://www.baeldung.com/ops/knative-serverless)上。我们将在pom.xml文件中添加[quarkus-funqy-knative-events](https://search.maven.org/search?q=quarkus-funqy-knative-events)依赖项：

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-funqy-knative-events</artifactId>
    <version>3.0.0.Alpha3</version>
</dependency>
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
        .body("{ \"name\": \"Tuyucheng\" }")
        .post("/")
        .then().statusCode(200);
}
```

现在让我们创建应用程序的构建和镜像：

```shell
$ mvn install
$ docker build -f src/main/docker/Dockerfile.jvm -t
  <<dockerAccountName>>/quarkus-funqy-project .
$ docker push <<ourDockerAccountName>>/quarkus-funqy-project
```

我们将在用于资源创建的src/main/kubernetes目录中创建Kubernetes Knative配置knative.yaml文件：

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
                -   image: docker.io/<<dockerAccountName>>/quarkus-funqy-project
```

现在我们只需要创建一个broker，broker事件配置YAML文件，并部署所有这些文件。

让我们创建一个knative-trigger.yaml文件：

```yaml
apiVersion: eventing.knative.dev/v1
kind: Trigger
metadata:
    name: tuyucheng-event
spec:
    broker: tuyucheng
    filter:
        attributes:
            type: GreetUser
    subscriber:
        ref:
            apiVersion: serving.knative.dev/v1
            kind: Service
            name: quarkus-funqy-project
```

```shell
$ kn broker create tuyucheng
$ kubectl apply -f src/main/kubernetes/knative.yaml
$ kubectl apply -f src/main/kubernetes/knative-trigger.yaml
```

让我们验证pod和pod日志，因为pod应该正在运行。如果我们不发送任何事件，pod将自动缩小为零。让我们获取代理URL以发送事件：

```shell
$ kubectl get broker tuyucheng -o jsonpath='{.status.address.url}'
```

现在，我们可以从任何pod向此URL发送事件，并看到我们的Quarkus应用程序的一个新pod将启动(如果它已经关闭)。我们还可以检查日志以验证我们的函数是否被触发：

```shell
$ curl -v "<<our_broker_url>>" 
  -X POST
  -H "Ce-Id: 1234"
  -H "Ce-Specversion: 1.0"
  -H "Ce-Type: GreetUser"
  -H "Ce-Source: curl"
  -H "Content-Type: application/json"
  -d "{\"name\":\"Tuyucheng\"}"
```

### 4.2 云部署

我们可以类似地更新我们的应用程序以部署在云平台上。但是，**每个云部署只能导出一个Funqy函数**。如果我们的应用程序有多个Funqy方法，我们可以通过在application.properties文件中添加以下内容来指定激活函数(将GreetUser替换为几乎激活函数名称)：

```shell
quarkus.funqy.export=GreetUser
```

## 5. 总结

在本文中，我们看到Quarkus Funqy是一个很好的补充，它可以帮助我们在Serverless基础设施上非常轻松地运行Java函数。我们了解了Quarkus Funqy以及如何在无服务器环境中实施、部署和测试它。