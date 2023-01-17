## 1. 简介

在这篇文章中，我们将探讨一个有趣的[JHipster](https://jhipster.github.io/)示例——构建一个简单的微服务架构。我们将展示如何构建和部署所有必需的部分，最后，我们将启动并运行一个成熟的微服务应用程序。

如果你是 JHipster 的新手，请先阅读[我们的介绍性文章](https://www.baeldung.com/jhipster)，然后再深入了解此项目生成工具的基础知识。

## 2. 单体与微服务

在我们最初的文章中，我们说明了如何创建和引导一个相对易于维护的单体应用程序。

另一方面，我们的微服务系统将前端和后端分开，而后端又可以拆分成几个小应用程序，每个应用程序处理完整应用程序域的一个子集。当然，与所有微服务实现一样，这解决了一些问题，但也引入了一些复杂性，例如处理组件注册和安全性。

[JHipster 将在 Netflix 的Eureka Server](https://github.com/Netflix/eureka/)和 Hashicorp 的[Consul](https://www.hashicorp.com/blog/consul-announcement/)等现代开源工具的帮助下解决管理微服务应用程序的大部分困难。

当然，这里有一些事情需要考虑，比如我们的域有多大或有多复杂，我们的应用程序有多重要，我们需要什么级别的可用性，我们是否要在不同的服务器和位置上托管我们的服务等。这些工具的目标当然是使这些排列成为可能并且易于管理。

### 2.1. JHipster 微服务组件

在使用 JHipster 构建微服务架构时，我们需要构建和部署至少三个不同的项目：一个 JHipster 注册表、一个微服务网关和至少一个微服务应用程序。

JHipster Registry是微服务架构的重要组成部分。它将所有其他组件联系在一起，并使它们能够相互通信。

微服务应用程序包含后端代码。一旦运行，它将公开它所关注的域的 API。微服务架构可能由许多微服务应用程序组成，每个微服务应用程序包含一些相关的实体和业务规则。

微服务网关拥有所有前端(Angular)代码，并将使用由整组微服务应用程序创建的 API：

[![JHipster 微服务架构](https://www.baeldung.com/wp-content/uploads/2017/05/JHipster-Microservice-Architecture.png)](https://www.baeldung.com/wp-content/uploads/2017/05/JHipster-Microservice-Architecture.png)

## 三、安装

有关安装过程的所有详细信息，请查看我们[关于 JHipster 的介绍性文章](https://www.baeldung.com/jhipster)。

## 4.创建微服务项目

现在让我们安装微服务项目的三个核心组件。

### 4.1. 安装 JHipster 注册表

由于JHipster Registry是一个标准的JHipster，所以我们只需要下载并运行它。不需要修改它：

```shell
git clone https://github.com/jhipster/jhipster-registry
cd jhipster-registry && ./mvnw
```

这将从 GitHub 克隆jhipster -registry项目并启动应用程序。成功启动后，我们可以访问 http://localhost:8761/ 并使用用户admin和密码admin登录：

[![打印屏幕](https://www.baeldung.com/wp-content/uploads/2017/05/Captura-de-Tela-2017-05-16-a%CC%80s-19.21.58-300x174.png)](https://www.baeldung.com/wp-content/uploads/2017/05/Captura-de-Tela-2017-05-16-às-19.21.58.png)

 

### 4.2. 安装微服务应用程序

这是我们开始构建项目的实际功能的地方。在此示例中，我们将创建一个管理汽车的简单微服务应用程序。所以首先我们将创建应用程序，然后我们将向其中添加一个实体：

```shell
# create a directory for the app and cd to it
mkdir car-app && cd car-app
# run the jhipster wizard
yo jhipster
```

向导启动后，让我们按照说明创建一个名为carapp的微服务类型应用程序。其他一些相关参数是：

-   端口：8081
-   包：com.car.app
-   身份验证：JWT
-   服务发现：JHipster Registry

下面的屏幕截图显示了完整的选项集：

[![jhipster 微服务应用向导](https://www.baeldung.com/wp-content/uploads/2017/05/jhipster-microservice-app-wizard.png)](https://www.baeldung.com/wp-content/uploads/2017/05/jhipster-microservice-app-wizard.png)

现在我们将向我们的应用程序添加一个汽车实体：

```shell
# runs entity creation wizard
yo jhipster:entity car
```

实体创建向导将启动。我们应该按照说明创建一个名为 the car的实体，其中包含三个字段：make、model和price。

一旦完成，我们的第一个微服务应用程序就完成了。如果我们查看生成的代码，我们会注意到没有 javascript、HTML、CSS 或任何前端代码。一旦创建了微服务网关，这些都会产生。此外，请查看 README 文件以获取有关项目和有用命令的重要信息。

最后，让我们运行我们新创建的组件：

```shell
./mvnw
```

在运行上述命令之前，我们应该确保jhipster-registry组件已启动并运行。否则，我们会得到一个错误。

如果一切按计划进行，我们的car-app就会启动，jhipster-registry日志会告诉我们应用已成功注册：

```shell
Registered instance CARAPP/carapp:746e7525dffa737747dcdcee55ab43f8
  with status UP (replication=true)
```

### 4.3. 安装微服务网关

现在是前端位。我们将创建一个微服务网关并向它表明我们在现有组件上有一个实体，我们要为其创建前端代码：

```shell
# Create a directory for our gateway app
mkdir gateway-app && cd gateway-app
# Runs the JHipster wizard
yo jhipster
```

让我们按照说明创建微服务网关类型的应用程序。我们将命名应用程序网关，并为其他参数选择以下选项：

-   端口：8080
-   包：com.gateway
-   授权：智威汤逊
-   服务发现：JHipster Registry

以下是完整参数集的摘要：

[![jhipster 微服务网关向导](https://www.baeldung.com/wp-content/uploads/2017/05/jhipster-microservice-gateway-wizard.png)](https://www.baeldung.com/wp-content/uploads/2017/05/jhipster-microservice-gateway-wizard.png)

让我们继续创建实体：

```bash
# Runs entity creation wizard
yo jhipster:entity car
```

当系统询问我们是否要从现有微服务生成时，选择是，然后输入car-app根目录的相对路径(例如：../car-app)。最后，当询问我们是否要更新实体时，选择Yes, regenerate the entity。

JHipster 将找到Car.json文件，它是我们之前创建的现有微服务应用程序的一部分，并将使用该文件中包含的元数据为该实体创建所有必要的 UI 代码：

```shell
Found the .jhipster/Car.json configuration file, entity can be automatically generated!
```

是时候运行gateway-app并测试是否一切正常：

```bash
# Starts up the gateway-app component
./mvnw
```

现在让我们导航到 http://localhost:8080/ 并使用用户admin和密码admin登录。在顶部菜单上，我们应该会看到一个项目Car，它将把我们带到汽车列表页面。都好！

[![jhipster 汽车页面](https://www.baeldung.com/wp-content/uploads/2017/05/jhipster-car-page.png)](https://www.baeldung.com/wp-content/uploads/2017/05/jhipster-car-page.png) [![jhipster创造汽车](https://www.baeldung.com/wp-content/uploads/2017/05/jhipster-create-car.png)](https://www.baeldung.com/wp-content/uploads/2017/05/jhipster-create-car.png)

### 4.4. 创建第二个微服务应用程序

接下来，让我们的系统更进一步，创建第二个 Microservice Application 类型的组件。这个新组件将管理汽车经销商，因此我们将向其添加一个名为经销商的实体。

让我们创建一个新目录，导航到它并运行yo jhipster命令：

```shell
mkdir dealer-app && cd dealer-app
yo jhipster
```

之后，我们输入dealerapp作为应用程序的名称，并选择端口8082让它运行(重要的是，这与我们用于jhipster - registry和car-app的端口不同)。

对于其他参数，我们可以选择任何我们想要的选项。请记住，这是一个单独的微服务，因此它可以使用与car-app组件不同的数据库类型、缓存策略和测试。

让我们向我们的经销商实体添加几个字段。例如姓名和地址：

```shell
# Runs the create entity wizard
yo jhipster:entity dealer
```

我们不应该忘记导航到gateway-app并告诉它为经销商实体生成前端代码：

```shell
# Navigate to the gateway-app root directory
cd ../gateway-app
# Runs the create entity wizard 
yo jhipster:entity dealer
```

最后，在dealer-app根目录上运行./mvnw以启动该组件。

接下来，我们可以在 http://localhost:8080 访问我们的网关应用程序并刷新页面以查看为 Dealer 实体新创建的菜单项。

在我们结束之前，让我们在 http://localhost:8761/ 再次查看jhipster-registry应用程序。单击 Applications 菜单项以检查我们所有的三个组件是否已成功识别和注册： 就是这样！我们在短短几分钟内创建了一个复杂的架构，其中包含一个网关应用程序和由两个微服务支持的所有前端代码。
[![jhipster 注册应用程序 1](https://www.baeldung.com/wp-content/uploads/2017/05/jhipster-registered-applications-1-300x184.png)](https://www.baeldung.com/wp-content/uploads/2017/05/jhipster-registered-applications-1.png)
[![打印屏幕](https://www.baeldung.com/wp-content/uploads/2017/05/Captura-de-Tela-2017-05-16-a%CC%80s-19.26.48-300x192.png)](https://www.baeldung.com/wp-content/uploads/2017/05/Captura-de-Tela-2017-05-16-às-19.26.48.png)

## 5.总结

使用 JHipster 启动微服务架构项目非常简单；我们只需要创建所需数量的微服务应用程序和一个微服务网关，就可以开始了。

你可以在[官方 JHipster 网站上](https://jhipster.github.io/microservices-architecture/)进一步探索该框架。

与往常一样，我们的汽车应用程序、经销商应用程序和网关应用程序的代码库可[在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/jhipster-modules/jhipster-microservice)获得。