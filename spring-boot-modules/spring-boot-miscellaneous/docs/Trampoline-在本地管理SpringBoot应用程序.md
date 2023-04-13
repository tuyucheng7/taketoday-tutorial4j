## 1. Trampoline概述

从历史上看，在运行时了解系统状态的一种简单方法是在终端中手动运行它。最好的情况是，我们会使用脚本自动化一切。

当然，DevOps改变了这一切，幸运的是，我们的行业已经远远超越了这种方法。**[Trampoline](https://github.com/ErnestOrt/Trampoline)是Java生态系统中解决这个问题(针对Unix和Windows用户)的解决方案之一**。

该工具建立在Spring Boot之上，**旨在通过干净清新的用户界面帮助Spring Cloud开发人员进行日常开发**。

以下是它的一些功能：

-   使用Gradle或Maven作为构建工具启动实例
-   管理Spring Boot实例
-   在启动阶段配置VM参数
-   监控已部署的实例：内存使用情况、日志和跟踪
-   向作者提供反馈

在这篇相当不错的文章中，我们将回顾Trampoline渴望解决的问题，并在实践中看看它。我们将继续进行导览，介绍如何注册新服务并启动该服务的一个实例。

<img src="../assets/img_7.png">

## 2. 微服务：单一部署已死

正如我们所讨论的，使用单个部署单元部署应用程序的时代已经一去不复返了。

这有积极的后果，不幸的是，也有消极的后果。尽管Spring Boot和Spring Cloud有助于这一过渡，但我们需要注意一些副作用。

**从单体应用到微服务的转变给开发人员构建应用程序的方式带来了巨大的改进**。

众所周知，打开一个包含30个类、包之间结构良好并带有相应单元测试的项目，与打开一个包含大量类的怪物代码库是不一样的，事情很容易变得复杂。

不仅如此-可重用性、解耦和关注点分离都从这种演变中受益。虽然好处是众所周知的，但让我们列出其中的一些：

-   单一职责原则：在可维护性和测试方面很重要
-   弹性：一项服务的故障不会影响其他服务
-   高可扩展性：要求苛刻的服务可以部署在多个实例中

但是，在使用微服务架构时，我们不得不面临一些权衡，尤其是在网络开销和部署方面。

然而，专注于部署，**我们失去了单体架构的优势之一-单一部署**。为了在生产环境中解决它，我们有一整套CD工具，这将有助于并使我们的生活更轻松。

## 3. Trampoline：设置第一个服务

在本节中，我们将在Trampoline中注册一个服务，并展示所有可用的功能。

### 3.1 下载最新版本

转到Trampoline仓库，在[release部分](https://github.com/ErnestOrt/Trampoline/releases)，我们将能够下载最新发布的版本。

然后，启动Trampoline，例如使用mvn spring-boot:run或./gradlew(或gradle.bat)bootRun。

最后，可以通过[http://localhost:8080](http://localhost:8080/)访问UI。

### 3.2 注册服务

启动并运行Trampoline后，让我们转到“Settings”部分，在这里我们将能够注册我们的第一个服务。我们可以在Trampoline源代码中找到两个微服务示例：microservice-example-gradle和microservice-example-maven。

要注册服务，需要以下信息：名称\*、默认端口\*、POM或构建位置\*、构建工具\*、执行器前缀和VM默认参数。

如果我们决定使用Maven作为构建工具，首先我们必须设置Maven位置。但是，如果我们决定使用Gradle Wrapper，则必须将其放在我们的microservices文件夹中。不需要其他任何东西。

在此示例中，我们将同时设置：

<img src="../assets/img_8.png">

在任何时候，我们都可以通过单击info按钮来查看服务信息，或单击trash按钮将其删除。

最后，为了能够享受所有这些功能，唯一的要求是在我们的Spring Boot项目中包含actuator starter(示例见代码片段)，以及通过众所周知的日志记录属性包含/logfile端点：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 3.3 管理服务实例

现在，我们准备转到Instances部分。在这里，我们将能够启动和停止服务实例，还可以监控它们的状态、跟踪、日志和内存消耗。

对于本教程，我们启动之前注册的每个服务的一个实例：

<img src="../assets/img_9.png">

### 3.4 仪表板

最后，让我们快速浏览一下Dashboard部分。在这里，我们可以可视化一些统计数据，例如我们计算机的内存使用情况或注册或启动的服务。

我们还可以在设置部分查看是否引入了所需的Maven信息：

<img src="../assets/img_10.png">

### 3.5 反馈

最后但同样重要的是，我们可以找到一个Feedback按钮，该按钮重定向到GitHub仓库，可以在其中创建Issues或提出问题和改进。

<img src="../assets/img_11.png">

## 4. 总结

在本教程中，我们讨论了Trampoline旨在解决的问题。

我们还展示了其功能的概述，以及关于如何注册服务和如何监控服务的简短教程。

最后，请注意，这是一个开源项目，欢迎你做出贡献。