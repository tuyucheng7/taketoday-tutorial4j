## 1. 简介

在本文中，我们将使用[Jenkins](https://jenkins.io/)、[Marathon](https://mesosphere.github.io/marathon/)和[Mesos](https://mesos.apache.org/)实现一个简单的持续交付管道。

首先，我们将对技术堆栈和架构进行高级概述，并解释所有内容如何组合在一起。之后，我们将进入一个实际的、一步一步的例子。

其结果将是一个完全自动化的 Jenkins 管道，使用 Marathon 将我们的应用程序部署到我们的 Mesos 集群。

## 二、技术栈概述

在使用容器和微服务架构时，我们面临着新的操作问题，而这些问题是使用更传统的堆栈无法解决的。

例如，在部署到集群时，我们必须处理扩展、故障转移、网络等问题。这些困难的分布式计算问题可以通过分布式内核和调度程序来解决，例如 Apache Mesos 和 Marathon。

### 2.1. 月

Mesos，用最简单的话来说，可以看作是我们的应用程序将运行的单一服务器。实际上，我们有一个集群，但正是这种抽象使它如此有用。

### 2.2. 马拉松

Marathon 是用于将我们的应用程序部署到 Mesos 的框架，为我们解决难题(健康检查、自动缩放、故障转移、监控等)。

## 3. 设置和安装

本文假设已经在运行 Jenkins、Mesos 和 Marathon。如果不是这种情况，请查阅它们各自的官方文档以了解如何设置它们。否则，将无法实施指南中的任何步骤。

## 4. 我们的交付渠道

我们将创建以下 Jenkins 管道：

 

[![管道 1-2](https://www.baeldung.com/wp-content/uploads/2017/02/pipeline_1-2-1024x157-1024x157.png)](https://www.baeldung.com/wp-content/uploads/2017/02/pipeline_1-2-1024x157.png)

这种方法没有什么特别复杂的——它是大多数现代 CD 流水线的同义词。在我们的例子中，构建意味着将应用程序容器化，而部署意味着使用 Marathon 将其调度到 Mesos 集群上。

## 5. 测试和构建我们的应用程序

第一步是构建和测试我们的应用程序。为简单起见，我们将要使用的应用程序是一个Spring Boot应用程序。因此，我们生成的工件将是一个可执行的 jar。除了 JRE 之外，它没有任何外部依赖项，因此执行起来非常简单。

### 5.1. 创造我们的工作

我们要做的第一件事是创建我们的 Jenkins 工作。让我们在左侧导航栏中选择“新建项目”，然后选择创建一个自由式项目，将其命名为“ marathon-mesos-demo ” ：

[![屏幕截图 2017-01-24-at-22.13.07](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-01-24-at-22.13.07-1024x494-1024x494.png)](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-01-24-at-22.13.07-1024x494.png)

### 5.2. 与 Git 集成

接下来，让我们配置它以克隆包含我们应用程序[的 Github 存储库：](https://github.com/eugenp/tutorials/tree/master/mesos-marathon)

[![屏幕截图 2017-02-18-at-15.12.59](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-02-18-at-15.12.59-1024x513-1024x513.png)](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-02-18-at-15.12.59-1024x513.png)

为了简单起见，我们的存储库是公开的，这意味着我们可以通过 https 进行克隆。如果不是这种情况，并且我们通过 SSH 进行克隆，那么将有一个额外的步骤来设置 SSH 用户和私钥，这超出了本文的范围。

### 5.3. 设置构建触发器

接下来，让我们设置一些构建触发器，以便我们的工作每分钟轮询 git 以获取新提交：

[![屏幕截图 2017-02-04-at-17.50.47](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-02-04-at-17.50.47-1024x691-1024x691.png)](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-02-04-at-17.50.47-1024x691.png)

### 5.4. 生成我们的构建脚本

我们现在可以告诉我们的作业在运行时执行 shell 脚本。由于我们正在处理一个简单的Spring BootMaven 项目，我们需要做的就是运行命令“ mvn clean install ”。这将运行所有测试，并构建我们的可执行 jar ：

[![屏幕截图 2017-02-18-at-15.14.24](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-02-18-at-15.14.24-1024x401-1024x401.png)](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-02-18-at-15.14.24-1024x401.png)

### 5.5. 建设我们的项目

现在我们已经设置了管道的开头，让我们通过单击作业上的“立即构建”来手动触发它。作业完成后，我们可以确认它已通过标记为蓝色。

## 6. 容器化我们的应用程序

让我们进入管道的下一阶段，即使用 Docker 打包和发布我们的应用程序。我们需要使用 Docker，因为容器是 Marathon 专门管理的。这并非不合理，因为几乎任何东西都可以在容器中运行。像 Marathon 这样的工具更容易处理这些授予的抽象。

### 6.1. 创建 Dockerfile

首先，让我们在项目根目录中创建一个[Dockerfile](https://docs.docker.com/engine/reference/builder/)。本质上，Dockerfile 是一个文件，其中包含有关如何构建映像的 Docker Deamon 指令：

```plaintext
FROM openjdk:8-jre-alpine
ADD target/mesos-marathon-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082 
ENTRYPOINT ["java","-jar","/app.jar"]
```

我们正在构建的镜像很简单——它只包含一个可执行的 jar 和一个 shell 命令，它将在容器启动时执行它。我们还必须确保公开我们的应用程序将侦听的端口，在本例中为“8082”。

### 6.2. 发布图像

现在我们可以构建我们的镜像了，让我们创建一个简单的 bash 脚本来构建并将它发布到我们的私有[Docker Hub](https://hub.docker.com/)存储库，并将它放在我们的项目根目录中：

```bash
#!/usr/bin/env bash
set -e
docker login -u baeldung -p $DOCKER_PASSWORD
docker build -t baeldung/mesos-marathon-demo:$BUILD_NUMBER .
docker push baeldung/mesos-marathon-demo:$BUILD_NUMBER

```

可能需要将的图像推送到公共 docker 注册表或的私有注册表。

$BUILD_NUMBER环境变量由 Jenkins 填充，随着每次构建而递增。虽然有点脆弱，但它是让每个构建增加版本号的快速方法。$DOCKER_PASSWORD也由 Jenkins 填充，在这种情况下，我们将使用[EnvInject 插件](https://wiki.jenkins-ci.org/display/JENKINS/EnvInject+Plugin)来保密。

虽然我们可以将这个脚本直接存储在 Jenkins 中，但最好将它保留在版本控制中，因为它可以与我们项目的其余部分一起进行版本控制和审计。

### 6.3. 在 Jenkins 上构建和发布

现在让我们修改 Jenkins 作业，让它在构建 jar 后运行“Dockerise.sh”：

 

[![屏幕截图 2017-02-04-at-17.52.27](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-02-04-at-17.52.27-1024x553-1024x553.png)](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-02-04-at-17.52.27-1024x553.png)

然后，让我们运行我们的工作来再次确认，通过它变蓝来确认一切正常。

## 7.部署我们的形象

我们的管道即将完成。只有一个阶段，就是使用Marathon将我们的应用程序部署到我们的Mesos集群中。

Jenkins 带有一个“ [Deploy with Marathon](https://wiki.jenkins-ci.org/display/JENKINS/Marathon+Plugin) ”插件。这充当了 Marathon API 的包装器，使其比使用传统 shell 脚本时更容易。可以通过插件管理器安装它。

### 7.1. 创建我们的 Marathon.Json 文件

在我们使用 Marathon 插件之前，我们需要创建一个“marathon.json”文件，并将其存储在我们的项目根目录中。这是因为插件依赖于它。

这个文件：“marathon.json”包含一个[Mesos 应用程序定义](https://mesosphere.github.io/marathon/docs/application-basics.html)。这是对我们要运行的长时间运行的服务(应用程序)的描述。最终，Jenkins Marathon 插件会将文件内容发布到 Marathon /v2/apps端点。Marathon 将依次安排定义的应用程序在 Mesos 上运行：

```plaintext
{
  "id": "mesos-marathon-demo",
  "container": {
    "type": "DOCKER",
    "docker": {
      "image": "",
      "network": "BRIDGE",
      "portMappings": [
        { "containerPort": 8082, "hostPort": 0 }
      ]
    }
  }
}
```

这是我们可以为容器化应用程序提供的最简单的配置。

属性：“ portMappings ” 需要正确设置，以便使我们的应用程序可以从我们的 Mesos 从设备访问。它基本上意味着，将容器端口8082映射到主机(mesos slave)上的一个随机端口，这样我们就可以从外部世界与我们的应用程序通信。部署我们的应用程序后，Marathon 将告诉我们该端口使用了什么。

### 7.2. 添加 Marathon 部署构建步骤

让我们为我们的工作添加一个 Marathon 部署后构建操作：

[![屏幕截图 2017-02-04-at-17.59.54](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-02-04-at-17.59.54-1-1024x660-1024x660.png)](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-02-04-at-17.59.54-1-1024x660.png)

请注意，我们告诉插件 Marathon 在哪里运行，在本例中为“localhost:8081”。我们还告诉它我们要部署的图像。这就是我们文件中空的“图像”字段被替换的内容。

现在我们已经创建了管道的最后阶段，让我们再运行一次我们的工作并确认它仍在通过，这次是将我们的应用程序发送到 Marathon 的额外步骤。

### 7.3. 验证我们在 Marathon 中的部署

现在已经部署好了，让我们在 Marathon UI 中看一下：

 

[![屏幕截图 2017-01-30-at-23.59.00](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-01-30-at-23.59.00-1024x276-1024x276.png)](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-01-30-at-23.59.00-1024x276.png)

如我们所见，我们的应用程序现在显示在 UI 中。为了访问它，我们只需要检查分配给它的主机和端口：

[![屏幕截图 2017-01-31-at-00.01.30](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-01-31-at-00.01.30-1024x524-1024x524.png)](https://www.baeldung.com/wp-content/uploads/2017/02/Screen-Shot-2017-01-31-at-00.01.30-1024x524.png)

在这种情况下，它在本地主机上随机分配了端口 31143，它将在内部映射到我们容器中的端口 8082，如应用程序定义中所配置的那样。然后我们可以在我们的浏览器中访问这个 URL 以确认应用程序被正确提供。

## 八. 总结

在本文中，我们使用 Jenkins、Marathon 和 Mesos 创建了一个简单的持续交付管道。每当我们对代码进行更改时，它都会在几分钟后在环境中运行。

本系列的后续文章将涵盖更高级的 Marathon 主题，例如应用程序健康检查、缩放、故障转移。还可能涵盖 Mesos 的其他用例，例如批处理。