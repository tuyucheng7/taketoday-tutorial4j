## 一、概述

[端点是API 设计](https://www.baeldung.com/building-a-restful-web-service-with-spring-and-java-based-configuration)中的一个重要概念。了解端点及其工作对于创建或使用 API 至关重要。

在本文中，我们将了解端点并讨论它们在 API 设计和开发中的作用。此外，我们还将检查端点的不同协议和[HTTP 方法](https://www.baeldung.com/cs/rest-architecture#1-methods)以及端点和 URI 之间的区别。

## 2. 什么是端点？

API(应用程序编程接口)上下文中的端点是链接到特定资源的特定 URL。与 API 交互时，端点可以执行特定的活动，例如请求数据或触发流程。

考虑包含以下端点的用户管理 API：

![API的工作](https://www.baeldung.com/wp-content/uploads/sites/4/2023/02/api-e1676874830157.png)

在上图中，具有相同基本 URL 的所有请求都发送到同一台[服务器](https://www.baeldung.com/java-servers)。例如，https://myserver1.com 请求转到“ Web 服务器 1”，而https://myserver2.com请求转到“ Web 服务器 2 ”。Web 服务器根据 API 端点采取不同的操作。我们使用/users端点来管理整个用户集合，并使用/users/{id}端点来管理单个用户。

端点是 API 设计中的一个重要概念，用于定义可以通过 API 执行的各种资源和操作。

### 2.1. 具有多个协议的端点

端点本质上是 URL 和 HTTP 方法的组合。HTTP 方法定义了该端点支持的操作类型。因此，端点可以通过允许不同的 HTTP 方法来支持多种协议。

例如，考虑一个端点/users表示 API 中的一组用户。该端点可以支持以下协议：

-   HTTP GET：使用 HTTP GET 技术从集合中检索所有用户
-   HTTP POST：我们可以使用 HTTP POST 技术将新用户添加到集合中
-   HTTP PUT：使用 HTTP PUT 技术更新用户的完整集合(即用新集合替换它)
-   HTTP DELETE：使用 HTTP DELETE 技术从集合中删除一个或多个用户

在这里，相同的端点 ( /users ) 通过允许不同的 HTTP 方法来支持多种协议(GET、POST、PUT、DELETE)。因此，API 可以提供多种功能，用于从单个端点控制用户组。

## 3.端点与URI

URI (统一资源标识符)是标识名称或[资源](https://www.baeldung.com/java-url-vs-uri)的字符串。相反，端点是表示特定 API 资源或活动的单个 URL。

API 使用端点来指定可以通过 API 执行的不同资源和操作。端点通常表示使用特定 URL 和 HTTP 方法(例如 GET、POST、PUT 或 DELETE)的资源或活动。

另一方面，URI 是一个更广泛的概念，可以识别任何类型的资源，而不仅仅是那些通过 API 公开的资源。URI 可以是 URL(统一资源定位符)，它指定资源在 Internet 上的位置，也可以是 URN(统一资源名称)，它通过名称而不是位置来标识资源。

总之，端点是表示特定 API 资源或操作的特定 URL。同时，URI 是一个更广泛的概念，可以标识任何资源类型。

## 4。总结

在本文中，我们了解了端点及其在 API 设计和开发中的作用。端点是表示 API 中特定资源或操作的 URL。我们可以将端点绑定到不同的协议和 HTTP 方法。我们还将端点与 URI 进行了比较，并强调了这两个概念之间的差异。