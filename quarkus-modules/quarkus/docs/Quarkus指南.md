## 1. 简介

如今，编写应用程序并将其部署到云中而不用担心基础架构是很常见的。Serverless和FaaS已经变得非常流行。

在这种类型的环境中，实例频繁创建和销毁，启动时间和首次请求时间极其重要，因为它们可以创造完全不同的用户体验。

在这种情况下，JavaScript和Python等语言始终处于聚光灯下。换句话说，具有大量JAR和长启动时间的Java从来都不是顶级竞争者。

在本教程中，**我们将介绍Quarkus并讨论它是否是将Java更有效地引入云的替代方案**。

## 2. Quarkus

[QuarkusIO](https://quarkus.io/)，超音速亚原子Java，**承诺提供小工件、极快的启动时间和更短的首次请求时间**。当与[GraalVM](https://www.baeldung.com/graal-java-jit-compiler)结合使用时，Quarkus将提前编译(AOT)。

**而且，由于Quarkus是建立在标准之上的，我们不需要学习任何新东西**。因此，我们可以使用CDI和JAX-RS等。此外，Quarkus有很多[扩展](https://quarkus.io/extensions/)，包括支持Hibernate、Kafka、OpenShift、Kubernetes和Vert.x的扩展。

## 3. 我们的第一个应用程序

创建新Quarkus项目的最简单方法是打开终端并键入：

```shell
mvn io.quarkus:quarkus-maven-plugin:2.16.5.Final:create \
    -DprojectGroupId=cn.tuyucheng.taketoday.quarkus \
    -DprojectArtifactId=quarkus-project \
    -DclassName="cn.tuyucheng.taketoday.quarkus.HelloResource" \
    -Dpath="/hello"
```

这将生成项目框架、一个公开/hello端点的HelloResource、配置、Maven项目和Dockerfile。

导入到我们的IDE后，我们将拥有类似于下图所示的结构：

<img src="../assets/img.png">

让我们检查一下HelloResource类的内容：

```java
@Path("/hello")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }
}
```

到目前为止一切看起来都很好。至此，我们有了一个带有单个RESTEasy JAX-RS端点的简单应用程序。让我们继续通过打开终端并运行以下命令来测试它：

```shell
mvn compile quarkus:dev:
```

<img src="../assets/img_1.png">

我们的REST端点应该在localhost:8080/hello公开。让我们使用curl命令测试一下：

```shell
$ curl localhost:8080/hello
hello
```

## 4. 热重载

在开发模式(./mvn compile quarkus:dev)下运行时，Quarkus提供了热重载功能。换句话说，**对Java文件或配置文件所做的更改将在浏览器刷新后自动编译**。这里最令人印象深刻的功能是我们不需要保存我们的文件。这可能是好是坏，取决于我们的偏好。

现在，我们将修改示例以演示热重载功能。如果应用程序停止了，我们可以简单地以开发模式重新启动它。我们将使用与之前相同的示例作为起点。

首先，我们将创建一个HelloService类：

```java
@ApplicationScoped
public class HelloService {
    public String politeHello(String name){
        return "Hello Mr/Mrs " + name;
    }
}
```

现在，我们将修改HelloResource类，注入HelloService并添加一个新方法：

```java
@Inject
HelloService helloService;

@GET
@Produces(MediaType.APPLICATION_JSON)
@Path("/polite/{name}")
public String greeting(@PathParam("name") String name) {
    return helloService.politeHello(name);
}
```

接下来，让我们测试我们的新端点：

```shell
$ curl localhost:8080/hello/polite/Tuyucheng
Hello Mr/Mrs Tuyucheng
```

我们将再进行一项更改，以证明同样可以应用于属性文件。让我们编辑application.properties文件并添加一个属性：

```properties
greeting=Good morning
```

之后，我们将修改HelloService以使用我们的新属性：

```java
@ConfigProperty(name = "greeting")
private String greeting;

public String politeHello(String name){
    return greeting + " " + name;
}
```

如果我们执行相同的curl命令，我们现在应该看到：

```shell
Good morning Tuyucheng
```

我们可以通过运行以下命令来打包应用程序：

```shell
mvn package
```

这将在target目录中生成2个jar文件：

-   quarkus-project-1.0.0-runner.jar：一个可执行的jar，其依赖项已复制到target/lib
-   quarkus-project-1.0.0.jar：包含类和资源文件

现在，我们可以运行打包的应用程序：

```shell
java -jar target/quarkus-project-1.0.0-runner.jar
```

## 5. 本机镜像

接下来，我们将生成应用程序的本机镜像。本机镜像将缩短启动时间和首次响应时间。换句话说，**它包含运行所需的一切，包括运行应用程序所需的最小JVM**。

首先，我们需要安装[GraalVM](https://www.graalvm.org/)并配置GRAALVM_HOME环境变量。

我们现在将停止应用程序(Ctrl+C)，并运行以下命令：

```shell
mvn package -Pnative
```

这可能需要几分钟才能完成。因为本机镜像会尝试创建所有代码AOT以加快启动速度，因此，我们将有更长的构建时间。

我们可以运行mvn verify -Pnative来验证我们的原生工件是否正确构建：

<img src="../assets/img_2.png">

其次，**我们将使用我们的本机可执行文件创建一个容器镜像**。为此，我们必须在我们的机器上运行一个容器运行时(即[Docker](https://www.baeldung.com/docker-test-containers))。让我们打开一个终端窗口并执行：

```shell
mvn package -Pnative -Dnative-image.docker-build=true
```

这将创建一个Linux 64位可执行文件，因此如果我们使用不同的操作系统，它可能无法再运行。暂时没关系。

项目构建为我们创建了一个Dockerfile.native：

```dockerfile
FROM registry.fedoraproject.org/fedora-minimal
WORKDIR /work/
COPY target/*-runner /work/application
RUN chmod 775 /work
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
```

如果我们检查该文件，我们就会知道接下来会发生什么。首先，我们将**创建一个docker镜像**：

```shell
docker build -f src/main/docker/Dockerfile.native -t quarkus/quarkus-project .
```

现在，我们可以使用以下命令运行容器：

```shell
docker run -i --rm -p 8080:8080 quarkus/quarkus-project
```

<img src="../assets/img_3.png" align="left">

容器以令人难以置信的0.009秒启动，这是Quarkus的优势之一。

最后，我们应该测试修改后的REST来验证我们的应用程序：

```shell
$ curl localhost:8080/hello/polite/Tuyucheng
Good morning Tuyucheng
```

## 6. 部署到OpenShift

使用Docker在本地完成测试后，我们将容器部署到[OpenShift](https://www.baeldung.com/spring-boot-deploy-openshift)。假设我们的注册中心中有Docker镜像，我们可以按照以下步骤部署应用程序：

```shell
oc new-build --binary --name=quarkus-project -l app=quarkus-project
oc patch bc/quarkus-project -p '{"spec":{"strategy":{"dockerStrategy":{"dockerfilePath":"src/main/docker/Dockerfile.native"}}}}'
oc start-build quarkus-project --from-dir=. --follow
oc new-app --image-stream=quarkus-project:latest
oc expose service quarkus-project
```

现在，我们可以通过运行以下命令获取应用程序URL：

```shell
oc get route
```

最后，我们将访问相同的端点(请注意，URL可能会有所不同，具体取决于我们的IP地址)：

```shell
$ curl http://quarkus-project-myproject.192.168.64.2.nip.io/hello/polite/Tuyucheng
Good morning Tuyucheng
```

## 7. 总结

在本文中，我们证明了Quarkus是一个很好的补充，可以更有效地将Java带入云端。例如，现在可以想象AWS Lambda上的Java。此外，Quarkus基于JPA和JAX/RS等标准。因此，我们不需要学习任何新东西。

Quarkus最近引起了很多关注，并且每天都在添加许多新功能。[Quarkus GitHub仓库](https://github.com/quarkusio/quarkus-quickstarts)中有几个快速入门项目可供我们试用Quarkus。