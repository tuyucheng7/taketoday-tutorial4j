## 1. 概述

在本文中，我们将介绍如何使用 Spring Cloud 连接器在 Heroku 上设置Spring Boot应用程序。

Heroku 是一种为 Web 服务提供托管的服务。此外，它们还提供大量第三方服务，称为附加组件，提供从系统监控到数据库存储的一切服务。

除此之外，他们还有一个自定义的 CI/CD 管道，可以无缝集成到 Git 中，从而加快开发到生产的速度。

Spring 通过它的 Spring Cloud Connectors 库支持 Heroku。我们将使用它在我们的应用程序中自动配置 PostgreSQL 数据源。

让我们开始编写应用程序。

## 2.Spring Boot图书服务

首先，让我们创建[一个新的简单的Spring Boot服务](https://www.baeldung.com/spring-boot-start)。

## 3. Heroku 注册

现在，我们需要注册一个 Heroku 帐户。让我们转到[heroku.com](https://www.heroku.com/home)并单击页面右上角的注册按钮。

现在我们已经有了一个帐户，我们需要获取 CLI 工具。我们需要导航到[heroku-cli](https://devcenter.heroku.com/articles/heroku-cli)安装页面并安装此软件。这将为我们提供完成本教程所需的工具。

## 4. 创建 Heroku 应用

现在我们有了 Heroku CLI 让我们回到我们的应用程序。

### 4.1. 初始化 Git 存储库

Heroku 在使用 git 作为我们的源代码控制时效果最好。

让我们首先转到我们的应用程序的根目录，与我们的pom.xml文件相同的目录，然后运行命令git init来创建一个 git 存储库。然后运行git 添加。和git commit -m “第一次提交”。

现在我们已经将我们的应用程序保存到我们的本地 git 存储库中。

### 4.2. 提供 Heroku Web 应用程序

接下来，让我们使用 Heroku CLI 在我们的帐户上配置 Web 服务器。

首先，我们需要验证我们的 Heroku 帐户。从命令行运行heroku login并按照登录和创建 SSH 密钥的说明进行操作。

接下来，运行heroku create。这将提供 Web 服务器并添加一个远程存储库，我们可以将我们的代码推送到该存储库以进行部署。我们还会在控制台中看到一个域，这个域以便我们稍后访问它。

### 4.3. 将代码推送到 Heroku

现在我们将使用 git 将我们的代码推送到新的 Heroku 存储库。

运行命令git push heroku master将我们的代码发送到 Heroku。

在控制台输出中，我们应该看到表明上传成功的日志，然后他们的系统将下载任何依赖项，构建我们的应用程序，运行测试(如果存在)，并在一切顺利的情况下部署应用程序。

就是这样——我们现在将我们的应用程序公开部署到 Web 服务器。

## 5. 在 Heroku 上测试 In-Memory

让我们确保我们的应用程序正常运行。使用我们创建步骤中的域，让我们测试我们的实时应用程序。

让我们发出一些 HTTP 请求：

```plaintext
POST https://{heroku-domain}/books HTTP
{"author":"baeldung","title":"Spring Boot on Heroku"}
```

我们应该返回：

```plaintext
{
    "title": "Spring Boot on Heroku",
    "author": "baeldung"
}
```

现在让我们尝试读取我们刚刚创建的对象：

```bash
GET https://{heroku-domain}/books/1 HTTP
```

这应该返回：

```plaintext
{
    "id": 1,
    "title": "Spring Boot on Heroku",
    "author": "baeldung"
}
```

这一切看起来不错，但在生产中，我们应该使用永久数据存储。

让我们逐步了解如何配置 PostgreSQL 数据库并配置我们的 Spring 应用程序以使用它。

## 6. 添加 PostgreSQL

要添加 PostgreSQL 数据库，请运行此命令heroku addons:create heroku-postgresql:hobby-dev

这将为我们的 Web 服务器提供一个数据库，并添加一个提供连接信息的环境变量。

Spring Cloud Connector 配置为检测此变量并自动设置数据源，因为 Spring 可以检测到我们要使用 PostgreSQL。

为了让Spring Boot知道我们正在使用 PostgreSQL，我们需要进行两处更改。

首先，我们需要添加一个依赖项来包含 PostgreSQL 驱动程序：

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.2.10</version>
</dependency>
```

接下来，让我们添加属性，以便 Spring Data Connectors 可以根据其可用资源配置数据库。

在src/main/resources创建一个 application.properties 文件并添加以下属性：

```plaintext
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.maxActive=10
spring.datasource.maxIdle=5
spring.datasource.minIdle=2
spring.datasource.initialSize=5
spring.datasource.removeAbandoned=true
spring.jpa.hibernate.ddl-auto=create
```

这将汇集我们的数据库连接并限制我们应用程序的连接。Heroku 将开发层数据库中的活动连接数限制为 10，因此我们将最大值设置为 10。此外，我们将hibernate.ddl属性设置为 create，以便自动创建 book 表。

最后，提交这些更改并运行git push heroku master。这会将这些更改推送到我们的 Heroku 应用程序。在我们的应用程序启动后，尝试运行上一节中的测试。

我们需要做的最后一件事是更改 ddl 设置。让我们也更新该值：

```plaintext
spring.jpa.hibernate.ddl-auto=update
```

这将指示应用程序在重新启动应用程序时对实体进行更改时更新架构。像以前一样提交并推送此更改，以将更改推送到我们的 Heroku 应用程序。

我们不需要为此编写自定义数据源集成。这是因为 Spring Cloud Connectors 检测到我们正在使用 Heroku 和 PostgreSQL 运行——并自动连接 Heroku 数据源。

## 5.总结

我们现在在 Heroku 中有一个正在运行的Spring Boot应用程序。

最重要的是，从单一想法到运行应用程序的简单性使 Heroku 成为一种可靠的部署方式。

要了解有关 Heroku 和所有工具的更多信息，我们可以在[heroku.com](https://www.heroku.com/)上阅读更多内容。