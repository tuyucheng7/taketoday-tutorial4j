## 1. 概述

在之前的文章中，我们[介绍了 JHipster](https://www.baeldung.com/jhipster)的基础知识以及如何使用它来生成基于[微服务的应用程序](https://www.baeldung.com/jhipster-microservices)。

在本教程中，我们将探讨 JHipster 的用户帐户和授权服务 (简称 UAA)，以及如何使用它来保护成熟的基于 JHispter 的微服务应用程序。更好的是，这一切都可以在不编写一行代码的情况下实现！

## 2. UAA 核心功能

我们在之前的文章中构建的应用程序的一个重要特征是用户帐户是其中不可或缺的一部分。现在，当我们只有一个应用程序时这很好，但是如果我们想在多个 JHipster 生成的应用程序之间共享用户帐户怎么办？这就是 JHipster 的 UAA 发挥作用的地方。

JHipster 的 UAA 是一个微服务，它独立于我们应用程序中的其他服务构建、部署和运行。它用作：

-   OAuth2 授权服务器，基于Spring Boot的实现
-   身份管理服务器，公开用户帐户 CRUD API

JHipster UAA 还支持典型的登录功能，如自助注册和“记住我”。当然，它与其他 JHipster 服务完全集成。

## 三、开发环境搭建

在开始任何开发之前，我们必须首先确保我们的环境已设置好所有先决条件。除了我们的[JHipster 介绍文章中描述的所有工具之外，](https://www.baeldung.com/jhipster)我们还需要一个正在运行的 JHipster Registry。快速回顾一下，注册表服务允许我们将创建的不同服务相互查找和对话。

生成和运行注册表的完整过程在我们的 [JHipster with a Microservice Architecture 文章](https://www.baeldung.com/jhipster-microservices)的第 4.1 节中进行了描述， 因此我们不会在这里重复。也可以使用[Docker 映像](https://hub.docker.com/r/jhipster/jhipster-registry) 作为替代方案。

## 4. 生成一个新的 JHipster UAA 服务

让我们使用 JHipster 命令行实用程序生成我们的 UAA 服务：

```plaintext
$ mkdir uaa
$ cd uaa
$ jhipster

```

我们必须回答的第一个问题是我们要生成哪种类型的应用程序。使用箭头键，我们将选择“JHipster UAA(用于微服务 OAuth2 身份验证)”选项：

[![应用类型](https://www.baeldung.com/wp-content/uploads/2018/12/app-type.png)](https://www.baeldung.com/wp-content/uploads/2018/12/app-type.png)

接下来，系统将提示我们提出一些关于生成服务的具体细节的问题，例如应用程序名称、服务器端口和服务发现：

[![jhipster uaa 设置](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-uaa-setup-1024x244.png)](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-uaa-setup.png)

大多数情况下，默认答案都很好。至于影响许多生成的工件的应用程序的基本名称，我们选择了“uaa”(小写)——一个合理的名称。如果需要，我们可以尝试使用其他值，但它不会改变生成项目的主要特性。

回答完这些问题后，JHipster 将创建所有项目文件并安装npm包依赖项(在本例中并未真正使用)。

我们现在可以使用本地 Maven 脚本来构建和运行我们的 UAA 服务：

```plaintext
$ ./mvnw
... build messages omitted
2018-10-14 14:07:17.995  INFO 18052 --- [  restartedMain] com.baeldung.jhipster.uaa.UaaApp         :
----------------------------------------------------------
        Application 'uaa' is running! Access URLs:
        Local:          http://localhost:9999/
        External:       http://192.168.99.1:9999/
        Profile(s):     [dev, swagger]
----------------------------------------------------------
2018-10-14 14:07:18.000  INFO 18052 --- [  restartedMain] com.baeldung.jhipster.uaa.UaaApp         :
----------------------------------------------------------
        Config Server:  Connected to the JHipster Registry config server!
----------------------------------------------------------

```

这里要注意的关键消息是声明 UAA 已连接到 JHipster Registry。此消息表明 UAA 能够自行注册并将可供其他微服务和网关发现。

## 5.测试UAA服务

由于生成的 UAA 服务本身没有 UI，我们必须使用直接 API 调用来测试它是否按预期工作。

在与其他部分或我们的系统一起使用之前，我们必须确保有两个功能正常工作： OAuth2 令牌生成和帐户检索。

首先，让我们使用简单的 curl命令从 UAA 的 OAuth 端点获取一个新令牌：

```plaintext
$ curl -X POST --data 
 "username=user&password=user&grant_type=password&scope=openid" 
 http://web_app:changeit@localhost:9999/oauth/token

```

在这里，我们使用了 密码授予 流程，使用了两对凭据。在这种流程中，我们使用基本的 HTTP 身份验证发送客户端凭据，我们直接在 URL 中对其进行编码。

使用标准用户名和密码参数，最终用户凭据作为正文的一部分发送。我们还使用名为 “user”的用户帐户，默认情况下在测试配置文件中可用。

假设我们已正确提供所有详细信息，我们将获得包含访问令牌和刷新令牌的答案：

```plaintext
{
  "access_token" : "eyJh...(token omitted)",
  "token_type" : "bearer",
  "refresh_token" : "eyJ...(token omitted)",
  "expires_in" : 299,
  "scope" : "openid",
  "iat" : 1539650162,
  "jti" : "8066ab12-6e5e-4330-82d5-f51df16cd70f"
}
```

现在，我们可以使用返回的 access_token来获取使用 帐户资源的关联帐户的信息，该资源在 UAA 服务中可用：

```plaintext
$ curl -H "Authorization: Bearer eyJh...(access token omitted)"  
 http://localhost:9999/api/account
{
  "id" : 4,
  "login" : "user",
  "firstName" : "User",
  "lastName" : "User",
  "email" : "user@localhost",
  "imageUrl" : "",
  "activated" : true,
  "langKey" : "en",
  "createdBy" : "system",
  "createdDate" : "2018-10-14T17:07:01.336Z",
  "lastModifiedBy" : "system",
  "lastModifiedDate" : null,
  "authorities" : [ "ROLE_USER" ]
}

```

请注意，我们必须在访问令牌过期之前发出此命令。默认情况下，UAA 服务会发出有效期为五分钟的令牌，这对于生产来说是一个合理的值。

我们可以通过编辑与我们运行应用程序的配置文件对应 的application-<profile>.yml文件并设置uaa.web-client-configuration.access-token-validity-in-来轻松更改有效令牌的生命周期 秒 键。设置文件位于 我们的 UAA 项目的src/main/resources/config目录中。

## 6. 生成支持 UAA 的网关

现在我们确信我们的 UAA 服务和服务注册表正在运行，让我们创建一个生态系统供它们交互。到最后，我们将添加：

-   基于 Angular 的前端
-   微服务后端
-   面向这两者的 API 网关

让我们实际上从网关开始，因为它将是与 UAA 协商以进行身份验证的服务。它将托管我们的前端应用程序并将 API 请求路由到其他微服务。

再一次，我们将在新创建的目录中使用 JHipster 命令行工具：

```plaintext
$ mkdir gateway
$ cd gateway
$ jhipster
```

和以前一样，我们必须回答几个问题才能生成项目。重要的如下：

-   应用程序类型： 必须是“微服务网关”
-   应用程序名称：这次我们将使用“网关”
-   服务发现：选择“JHipster registry”
-   身份验证类型： 我们必须在此处选择“Authentication with JHipster UAA server”选项
-   UI 框架： 让我们选择“Angular 6”

一旦 JHipster 生成了它的所有工件，我们就可以使用提供的 Maven 包装器脚本构建和运行网关：

```plaintext
$ ./mwnw
... many messages omitted
----------------------------------------------------------
        Application 'gateway' is running! Access URLs:
        Local:          http://localhost:8080/
        External:       http://192.168.99.1:8080/
        Profile(s):     [dev, swagger]
----------------------------------------------------------
2018-10-15 23:46:43.011  INFO 21668 --- [  restartedMain] c.baeldung.jhipster.gateway.GatewayApp   :
----------------------------------------------------------
        Config Server:  Connected to the JHipster Registry config server!
----------------------------------------------------------

```

通过上面的消息，我们可以通过将浏览器指向[http://localhost:8080](http://localhost:8080/)来访问我们的应用程序，这应该会显示默认生成的主页：

[![时尚之家](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-home-1024x453.png)](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-home.png)

让我们继续并登录到我们的应用程序，方法是导航到“ 帐户”>“登录”菜单项。我们将使用admin/admin 作为凭据，JHipster 默认会自动创建它。一切顺利，欢迎页面将显示一条确认登录成功的消息：

[![jhipster 成功 1](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-success-ok-1.png)](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-success-ok-1.png)

让我们回顾一下让我们到达这里所发生的事情：首先，网关将我们的凭据发送到 UAA 的 OAuth2 令牌端点，该端点验证它们并生成包含访问和刷新 JWT 令牌的响应。然后网关获取这些令牌并将它们作为 cookie 发送回浏览器。

接下来，Angular 前端调用 /uaa/api/account API，网关再次将其转发给 UAA。在此过程中，网关获取包含访问令牌的 cookie，并使用其值向请求添加授权标头。

如果需要，我们可以通过检查 UAA 和网关的日志来详细了解所有这些流程。我们还可以通过将org.apache.http.wire记录器级别设置为 DEBUG 来获取完整的线路级数据 。

## 7. 生成支持 UAA 的微服务

现在我们的应用程序环境已经启动并运行，是时候向它添加一个简单的微服务了。我们将创建一个“报价”微服务，它将公开一个完整的 REST API，允许我们创建、查询、修改和删除一组股票报价。每个报价将只有三个属性：

-   报价的交易符号
-   它的价格，和
-   最后一笔交易的时间戳

让我们回到我们的终端并使用 JHipster 的命令行工具来生成我们的项目：

```plaintext
$ mkdir quotes
$ cd quotes
$ jhipster

```

这一次，我们将要求 JHipster 生成一个微服务应用程序，我们称之为“quotes”。这些问题与我们之前回答过的问题类似。我们可以保留其中大多数的默认值，除了这三个：

-   服务发现： 选择“JHipster Registry”，因为我们已经在我们的架构中使用它
-   UAA 应用程序的路径：由于我们将所有项目目录保存在同一个文件夹下，因此这将是../uaa (当然，除非我们更改了它)
-   身份验证类型：选择“JHipster UAA 服务器”

在我们的案例中，典型的答案序列如下所示：

[![jhipster微服务](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-microservice-1024x237.png)](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-microservice.png)

一旦 JHipster 完成生成项目，我们就可以继续构建它：

```plaintext
$ mvnw
... many, many messages omitted
----------------------------------------------------------
        Application 'quotes' is running! Access URLs:
        Local:          http://localhost:8081/
        External:       http://192.168.99.1:8081/
        Profile(s):     [dev, swagger]
----------------------------------------------------------
2018-10-19 00:16:05.581  INFO 16092 --- [  restartedMain] com.baeldung.jhipster.quotes.QuotesApp   :
----------------------------------------------------------
        Config Server:  Connected to the JHipster Registry config server!
----------------------------------------------------------
... more messages omitted

```

消息“已连接到 JHipster Registry 配置服务器！” 是我们在这里寻找的。它的存在告诉我们微服务在注册中心注册了自己，因此，网关将能够将请求路由到我们的“报价”资源，并在我们创建它后将其显示在一个漂亮的 UI 上。由于我们使用的是微服务架构，因此我们将此任务分为两部分：

-   创建“quotes”资源后端服务
-   在前端创建“quotes”UI(网关项目的一部分)

### 7.1. 添加报价资源

首先，我们需要 确保 quotes 微服务应用程序已停止 ——我们可以在之前用于运行它的同一控制台窗口中按 CTRL-C。

现在，让我们使用 JHipster 的工具向项目添加一个实体。这次我们将使用 import-jdl命令，这将使我们免于单独提供所有详细信息的繁琐且容易出错的过程。有关 JDL 格式的更多信息，请参阅[完整的 JDL 参考](https://www.jhipster.tech/jdl/)。

接下来，我们创建一个名为 quotes.jh的文本文件，其中包含我们的 Quote 实体定义以及一些代码生成指令：

```plaintext
entity Quote {
  symbol String required unique,
  price BigDecimal required,
  lastTrade ZonedDateTime required
}
dto Quote with mapstruct
paginate Quote with pagination
service Quote with serviceImpl
microservice Quote with quotes
filter Quote
clientRootFolder Quote with quotes

```

我们现在可以将这个实体定义导入到我们的项目中：

```plaintext
$ jhipster import-jdl quotes.jh

```

注意：在导入期间，JHipster 将在对master.xml文件应用更改时抱怨冲突 。在这种情况下，我们可以安全地选择 覆盖选项。

我们现在可以使用 mvnw 再次构建和运行我们的微服务。 启动后，我们可以验证网关是否选择了访问 网关视图的新路由，该视图可从“ 管理”菜单获得。这一次，我们可以看到“/quotes/”路由的条目，这 表明后端已准备好供 UI 使用。

### 7.2. 添加报价 UI

最后，让我们在网关项目中生成 CRUD UI，我们将使用它来访问我们的报价。我们将使用来自“quotes”微服务项目的相同 JDL 文件来生成 UI 组件，我们将使用 JHipster 的import-jdl命令导入它：

```plaintext
$ jhipster import-jdl ../jhipster-quotes/quotes.jh
...messages omitted
? Overwrite webpackwebpack.dev.js? <b>y</b>
... messages omitted
Congratulations, JHipster execution is complete!

```

在导入过程中，JHipster 将提示几次它应该对冲突文件采取的操作。在我们的例子中，我们可以简单地覆盖现有资源，因为我们没有进行任何定制。

现在我们可以重新启动网关，看看我们完成了什么。让我们将浏览器指向[http://localhost:8080](http://localhost:8080/)的网关，确保刷新其内容。Entities菜单现在应该有一个新的 Quotes资源条目：

[![jhipster 网关实体菜单](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-gateway-entities-menu.png)](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-gateway-entities-menu.png)

单击此菜单选项会弹出报价列表屏幕：

[![jhipster 网关报价](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-gateway-quotes-1024x252.png)](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-gateway-quotes.png)

正如预期的那样，列表是空的——我们还没有添加任何引号！让我们尝试通过单击此屏幕右上角的“创建新报价按钮”来添加一个，这会将我们带到创建/编辑表单：

[![jhipster 网关添加报价](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-gateway-add_quote.png)](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-gateway-add_quote.png)

我们可以看到生成的表单具有所有预期的功能：

-   必填字段标有红色指示器，填写后变为绿色
-   日期/时间和数字字段使用本机组件来帮助进行数据输入
-   我们可以取消此活动，这将保留数据不变，或保存我们新的或修改后的实体

填写此表格并点击 保存后， 我们将在列表屏幕上看到结果。我们现在可以在数据网格中看到新的Quotes实例： 

[![jhipster 网关报价2](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-gateway-quotes2-1024x298.png)](https://www.baeldung.com/wp-content/uploads/2018/12/jhipster-gateway-quotes2.png)

作为管理员，我们还可以访问 API 菜单项，这会将我们带到标准的 Swagger API 开发人员门户。在此屏幕中，我们可以选择其中一个可用的 API 进行练习：

-   default： 网关自己的API，显示可用路由
-   uaa：帐户和用户 API
-   报价： 报价API

## 8. 后续步骤

到目前为止，我们构建的应用程序按预期工作，并为进一步开发提供了坚实的基础。我们肯定还需要编写一些(或大量)自定义代码，具体取决于我们需求的复杂程度。可能需要一些工作的一些领域是：

-   UI 外观和感觉定制：由于前端应用程序的结构方式，这通常很容易——我们可以通过简单地摆弄 CSS 和添加一些图像来走很长一段路
-   用户存储库更改：一些组织已经有某种内部用户存储库(例如 LDAP 目录)——这将需要在 UAA 上进行更改，但好的部分是我们只需要更改它一次
-   对实体的更细粒度授权： 生成的实体后端使用的标准安全模型没有任何类型的实例级和/或字段级安全性 ——由开发人员在适当的级别添加这些限制(API或服务，视情况而定)

即使有这些评论，在开发新应用程序时使用像 JHispter 这样的工具也会有很大帮助。它将带来坚实的基础，并且可以随着系统和开发人员的发展在我们的代码库中保持良好的一致性。

## 9.总结

在本文中，我们展示了如何使用 JHispter 创建基于微服务架构和 JHipster 的 UAA 服务器的工作应用程序。我们在不编写一行Java代码的情况下实现了这一目标，这令人印象深刻。