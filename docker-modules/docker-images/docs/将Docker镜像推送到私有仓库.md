## 1. 概述

本教程介绍如何将Docker镜像推送到私有仓库，我们首先创建一个示例应用程序，它将作为我们Docker镜像的基础。然后我们演示如何登录到我们的私有Docker仓库，最后学习如何标记镜像并将其推送到仓库。

## 2. 私有Docker仓库

**私有Docker仓库提供对其包含的镜像的受限访问**。与公共仓库不同，只有授权用户才能访问镜像；这样，就可以只允许特定的用户组访问，例如组织、团队，甚至是单个用户。这使其成为不想公开其Docker镜像的项目的完美选择。

通过仓库设置实现Docker仓库的私有化。关于如何完成此操作的详细信息可能因不同的提供商而异，但通常，这只是选中复选框的问题。

现在我们知道私有Docker仓库有什么用，让我们探讨如何将镜像推送到这样的仓库。**这些步骤与将镜像推送到公共仓库相同。唯一的区别是仓库被标记为私有。**

## 3. 准备镜像

首先，我们需要通过可选地提供正确的别名和标签来准备镜像。这可以在构建镜像时完成，也可以通过使用现有镜像并对其重命名来完成。

### 3.1. 构建新镜像

首先，我们从一个简单的Spring Boot应用程序创建一个Docker镜像，该应用程序包含一个返回“Hello World”的RestController：

```java
@RestController
public class HelloWorldController {
    
    @GetMapping("/helloworld")
    String helloWorld() {
        return "Hello World!";
    }
}
```

我们使用以下Dockerfile：

```dockerfile
FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

最后，我们运行构建镜像的命令，该命令通常的形式为：

```shell
docker build [OPTIONS] PATH | URL | -
```

在我们的例子中，指定了-t参数，表明我们想要标记镜像。我们还提供点“.”作为我们的jar文件的路径。选择由你的用户名和仓库名称组成的正确名称很重要，版本标签是可选的。如果不指定，镜像将被标记为latest：

```shell
docker build -t username/fancy-repository:v1.0.0 .
```

现在我们可以使用以下命令列出现有镜像：

```shell
docker images
```

然后，我们可以看到新创建的镜像：

```shell
REPOSITORY                  TAG       IMAGE ID        CREATED              SIZE
username/fancy-repository   v1.0.0    e20b5a89a0f2   About a minute ago   471MB
```

### 3.2 准备现有镜像

在某些情况下，我们不想从头开始创建镜像，而是推送现有镜像。这需要我们将在本节中探讨的一些准备步骤，假设我们的机器上有以下镜像：

```shell
REPOSITORY       TAG         IMAGE ID       CREATED        SIZE
existing-image   some-tag    e20b5a89a0f2   2 weeks ago   471MB
```

为了将它推送到我们的fancy-repository，我们首先需要使用以下命令使用正确的名称/别名标记镜像：

```shell
docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]
```

在我们的示例中，SOURCE_IMAGE[:TAG]是现有镜像的名称和标签，**TARGET_IMAGE[:TAG]是由我们的用户名和我们要将镜像推送到的仓库的名称组成的别名**。例如下面的命令：

```shell
docker tag existing-image:some-tag username/fancy-repository:v1.0.0
```

然后使用以下命令观察结果：

```shell
docker images
```

现在我们可以看到有一个新增的镜像，显示仓库的名称和新应用的版本标签。镜像ID、时间戳和大小都相同，因为它仍然是同一个镜像，只是使用了另一个别名：

```shell
REPOSITORY                      TAG         IMAGE ID        CREATED               SIZE
existing-image                  some-tag    e20b5a89a0f2    2 weeks ago          471MB
username/fancy-repository       v1.0.0      e20b5a89a0f2    2 weeks ago          471MB
```

这样，我们就可以继续将镜像推送到我们的私有仓库。

## 4. 推送镜像

此时我们已经准备好Docker镜像，可以将它推送到我们的私有仓库。第一步是使用以下命令登录DockerHub注册表(如果你没有DockerHub账号，请[从此](https://hub.docker.com)创建一个)：

```shell
docker login
```

最后一步是使用以下命令推送镜像：

```shell
docker push [OPTIONS] NAME[:TAG]
```

在我们的例子中，我们不需要指定任何选项，只需要提供镜像名称和标签。该命令如下所示：

```shell
docker push username/fancy-repository:v1.0.0
```

这样，镜像将上传到我们在DockerHub上的私有Docker仓库，我们可以通过运行以下命令来验证镜像是否在DockerHub上：

```shell
docker search username/fancy-repository
```

我们将得到以下输出，显示我们的镜像详细信息并证明它在DockerHub上实际可用：

```shell
NAME                        DESCRIPTION   STARS     OFFICIAL   AUTOMATED
username/fancy-repository                 0                             
```

## 5. 总结

在本文中，我们探讨了如何将Docker镜像推送到私有仓库。我们了解了私有仓库是什么以及它的用途，然后我们演示了如何准备镜像并将其推送到私有仓库。