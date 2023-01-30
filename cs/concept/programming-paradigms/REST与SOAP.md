## 1. 概述

API(应用程序编程接口)是软件的一部分，它接收请求并通过响应进行回答。它允许多个软件组件相互通信。如今，API 在开发应用程序时至关重要，尤其是 Web 和移动应用程序。

在本教程中，我们将分析两种最著名的构建 API 的方法，即[REST](https://www.baeldung.com/rest-with-spring-series)和[SOAP](https://www.baeldung.com/spring-boot-soap-web-service)。

## 2. 休息

REST(REpresentational State Transfer)是一种创建 API 的架构风格。简而言之，它是一组用于开发可扩展、可靠和可维护的 Web 服务的良好实践。遵循 REST 约定的应用程序称为 RESTful。重要的是要说REST 不是标准的. 它在开发 API 的功能时提供了灵活性。

REST 可以使用多种协议，如[HTTP](https://www.baeldung.com/java-http-request)、 SMTP 或 FTP 。此外，它可以使用多种格式来传输数据，例如[JSON](https://www.baeldung.com/java-json)、[XML](https://www.baeldung.com/java-xml)、 HTML 。尽管在大多数情况下，RESTful Web 服务使用 HTTP 和 JSON。具有其他配置的应用程序并不典型。

正如我们前面提到的，REST 是可调整的。API 必须遵循一组准则才能称为 RESTful。让我们描述一下。

### 2.1. 核心概念

首先，RESTful API 必须遵循客户端-服务器约束。它提供可扩展性和可移植性。因此，客户端可以很容易地通过新平台进行扩展。换句话说，多个客户端应用程序可以使用单个 API，从而保持数据的一致性。

其次，Restful API 必须是无状态的。 服务器无法存储有关用户会话或中间状态的任何信息。因此，每个请求都是独立的，并且包含响应它所需的所有数据。每个请求都可能由于问题而失败，例如网络连接不稳定。如果需要多个请求来处理一项任务，失败的风险就会增加。因此，无状态提高了可靠性。

第三个重要的事情是可[缓存](https://www.baeldung.com/spring-cache-tutorial)性。客户端应用程序可以存储不经常更改的数据。因此，它通过减少发送到服务器的请求数量来提高性能。服务器负责将响应标记为可缓存或不可缓存。因此，它可以防止客户端缓存不适当的数据。

接下来，必须将 RESTful API 设计为分层系统。关注点分离提高了安全性和可扩展性。客户端应用程序不应该知道它们是直接连接到服务器还是中介。因此，我们可以轻松添加中间件，例如负载均衡器、授权服务器或共享缓存。

最后，REST 的关键约束是统一接口。 任何资源都应由单个逻辑 URI 表示。此外，它应该提供有关如何获取相关资源的信息。此外，API 返回的数据的表示不能依赖于数据库模式。

### 2.2. HTTP 请求

正如我们已经提到的，RESTful API 最常使用 HTTP 协议。那么，让我们分析一下对 RESTful API 的 HTTP 请求的结构。它由以下元素组成：

-   请求方法指定将在资源上执行的操作类型。最基本的是[POST、PUT、GET、DELETE](https://www.baeldung.com/spring-new-requestmapping-shortcuts)
-   标头是附加元数据的键值对。它们是可选的，因此可以有零个或多个。HTTP 规范定义了[可用的标头](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers)
-   资源路径是描述特定资源的 URI
-   可选主体包含处理特定请求所需的数据

现在，让我们看一个此类请求的真实示例。

```http
POST https://jsonplaceholder.typicode.com/posts
Accept: application/json
Body:
{
    "title":"foo",
    "body":"bar",
    "userId":1
}
```

上面的请求创建了一个新资源，即示例博客文章。我们正在使用 POST HTTP 方法，我们正在向以下 URI 发送请求https://jsonplaceholder.typicode.com/posts。

该请求包含一个标头Accept: application/json。它通知服务器客户端仅支持 JSON 数据格式作为响应。

最后，我们有一个包含我们要创建的博客文章数据的主体。JSON 包含三个字段，title、body(博客文章内容)和userId(作者的)。

## 3.肥皂

SOAP(简单对象访问协议)是 Microsoft 创建的一种消息传递协议，用于在分布式环境中的对等点之间传输数据。它旨在 提供独立性、中立性、可扩展性和冗长性。与 REST 相比，SOAP 是标准化的。因此，它不灵活，必须严格遵守给定的约束条件。SOAP 可以与几种不同的协议一起工作，但最常与 HTTP 搭配使用。与 REST 相比，它专门支持 XML 作为数据格式。

SOAP 是重量级协议。它使用 XML 作为序列化格式。XML 比 JSON 重得多，主要是因为它包含大量样板代码。此外，SOAP 使用[WSDL](https://www.baeldung.com/jax-ws#web-services-definition-language-wsdl)(Web 服务文档语言)来描述 API。WSDL 是一个复杂而厚重的文档 XML 文件。尽管不是必需的，但通常会使用和要求它。

尽管存在这些缺点，SOAP 仍然是一种安全可靠的协议。由于 SOAP 的标准化和安全性，大型企业应用程序经常使用它。

### 3.1. 肥皂消息

让我们分析一下 SOAP 消息的结构：

[![SOAP 消息第 1 页](https://www.baeldung.com/wp-content/uploads/sites/4/2020/11/SOAP-Message-Page-1.svg)](https://www.baeldung.com/wp-content/uploads/sites/4/2020/11/SOAP-Message-Page-1.svg)

SOAP 消息由以下元素组成：

-   信封——将 XML 文档描述为 SOAP 消息
-   标头——一个可选的块，包含额外的元数据，例如超时
-   Body——携带请求和响应数据
-   Fault – 一个可选块，提供有关过程中发生的错误的信息。如果存在，它位于体内

SOAP 消息由以下约束描述：

-   必须是 XML 文档
-   必须包含一个信封
-   可以有标题
-   必须有身体
-   必须使用信封命名空间
-   不需要 DTD 参考
-   不需要 XML 处理指令
-   必须使用编码命名空间

让我们看一个 SOAP 消息的示例。以下文件要求自行车的价格。

```xml
<soap:Envelope>
    <soap:Header>
        <maxTime value="10000"/>
    </soap:Header>
    <soap:Body>
        <GetPrice>
            <Name>bicycle</Name>
        </GetPrice>
    </soap:Body>
</soap:Envelope>
```

因此，在示例中，我们有一个带有header和body的 SOAP 信封。标头包含maxTime标记，它描述了发送方等待响应的最大毫秒数。然后是主体，其中包含GetPrice块。它指定了我们想要访问的 API 方法。在GetPrice块中，有一个Name属性。它是传递给 API 方法的参数，值为 a bicycle。

### 3.2. WSDL

前面我们提到，WSDL 是基于 SOAP 的 Web 服务的文档。 此外，还有一些工具，如 SoapUI，提供了[基于 WSDL 文件生成源代码的可能性。](https://www.baeldung.com/spring-soap-web-service#1-generate-client-code) WSDL 文件提供以下信息：

-   Web 服务提供的服务
-   如何调用可用操作
-   Web 服务的位置

按照我们的[文章](https://www.baeldung.com/jax-ws#web-services-definition-language-wsdl)阅读有关创建 WSDL 文档的更多详细信息。

## 4. REST 与 SOAP

我们已经知道使用 SOAP 或 REST 创建的 Web 服务的基础知识。它们都是创建 Web 服务的行之有效的方法。尽管它们彼此略有不同。让我们比较一下创建 Web 服务时要考虑的最重要的特性。

首先，REST在传输数据的数据格式上是有弹性的。 基于 REST 的 API 的不同端点甚至可以接受多种格式。另一方面，SOAP 只允许 XML 格式的消息。

由于 WS-Security 扩展，SOAP 提供了额外的安全层。该协议将嵌入式安全机制应用于 Web 服务。此外，它描述了消息的完整性和机密性。使用 REST 实现类似级别的安全性可能更难实现，也更耗时。值得注意的是，REST 和 SOAP 都支持[SSL](https://www.baeldung.com/java-ssl)。

如果性能是主要问题，REST 将是更好的选择。它是轻量级的，并且支持轻型数据格式。因此，它比 SOAP 需要更少的带宽。此外，REST 支持缓存，这可以稍微提高 Web 服务的性能。

最后，重要的是要注意 REST 是无状态的。因此，无状态 API 更易于扩展和扩展。但是，在某些情况下，首选在服务器端管理用户会话。SOAP 可以是无状态的或有状态的，具体取决于实现。

## 5.总结

在本文中，我们深入比较了 REST 和 SOAP。首先，我们介绍了REST及其核心概念。然后，我们分析了一个对基于 REST 的 Web 服务的 HTTP 请求的简单示例。我们描述了 SOAP 基础知识。随后，我们调查了一个 SOAP 消息的示例。接下来，我们简要定义了一个 WSDL 文件及其用途。最后，我们比较了 SOAP 和 REST 的关键特性。