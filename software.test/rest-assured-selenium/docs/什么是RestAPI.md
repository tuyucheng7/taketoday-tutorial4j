## 什么是 REST API？

符合 REST 架构风格或 REST 标准的 API *（应用程序编程接口）*称为***“REST API”。***Rest API 促进了客户端和 RESTful Web 服务*（服务器）* 之间的交互，以获取所需的信息。通过与系统交互，它们可以用于各种目的。这些可能包括特定操作，例如检索特定城市的位置或数据更新，例如注册新用户。API 开发人员以各种方式使用 REST 标准来开发 REST API。下图显示了一般 REST API 功能。

![REST API 组织](https://toolsqa.com/gallery/selnium%20webdriver/1.REST%20API%20organization.png)

如上图所示，REST API 位于数据库和表示层（*即交互系统）的中间层。*其他应用程序*（显示为顶层）*将在一个地方调用具有集中核心逻辑的 REST API。应用程序调用 REST API 来访问所需的数据。例如，如果我们尝试对所有内容进行硬编码，我们需要为网站上的每个操作进行编码。逐书序号取书是一个动作，可能需要经过中间层，然后是数据库。根据数据库和网站的大小，可能需要很长时间。使用 RESTful API，流程变得更快，因为它们是轻量级的。因此，我们不是为每个应用程序编写单独的代码和逻辑，而是编写可供任何应用程序访问的 REST API。

### ***当客户端通过 RESTful API 发出请求时会发生什么？***

当客户端发出特定请求时，RESTful API 将资源的状态表示传输到请求者或端点。此表示的格式是 HTTP 等多种格式之一：JSON *（Javascript 对象表示法）、* HTML、XLT、Python、PHP 或纯文本。现在最流行的格式是 JSON，因为它易于阅读且非常轻量级。

[***标头和参数在 RESTful API HTTP 请求***](https://www.toolsqa.com/client-server/http-request/)的 HTTP 方法中也起着重要作用。它们包含有关请求的统一资源标识符*(URI)、*元数据、授权、缓存、cookie 等的重要标识符信息。这些请求和响应标头具有自己的 HTTP 连接和状态代码信息。

接下来，让我们继续讨论我们为什么使用 REST API。

## 为什么使用 REST API？

我们主要使用 REST API 的原因如下：

1.  *正如我们前面所讨论的，REST API 创建一个对象并传输对象值以响应客户端的请求。REST API 将此事务分解为更小的模块，其中每个模块处理事务的特定部分。将事务分解为更小的模块需要付出很多努力，但也为用户提供了更大的灵活性。*
2.  *REST API 有严格的标准要遵守。[***在我们早期的什么是 REST***](https://toolsqa.com/rest-assured/what-is-rest/)和[***Rest 架构元素***](https://toolsqa.com/rest-assured/rest-architectural-elements/)的教程中，我们已经看到了要 RESTAssured 的应用程序的指导原则（约束） 。这种严格的遵守会在开发后产生高效的 REST API。此外，我们可以根据需要实施这套准则。*
3.  *REST API 被认为比[***SOAP***](https://en.wikipedia.org/wiki/SOAP)（简单对象访问协议）等其他协议更容易，后者具有更具体的要求，如内置安全性、事务合规性、XML 消息传递等。所有这些要求使这些协议变得更慢、更重。*
4.  *REST 应用程序（和 API）更轻量级，具有更高的可扩展性，并且更适合 IoT（物联网）和移动应用程序开发。*

## RESTful API 的方法

当我们使用 Web 技术和应用程序时，我们会执行 CRUD 操作。如下图所示，CRUD 代表创建***、***读取***、******更新***、***删除***。这意味着使用 CRUD 操作，我们可以创建、读取、更新和删除资源。通常，我们使用 HTTP 方法来执行 CRUD 操作。对于 REST API 方法，REST 以 API 的形式提供这四种方法。参考下图：

![REST API 方法](https://toolsqa.com/gallery/selnium%20webdriver/2.REST%20API%20methods.png)

如上所示，POST、GET、PUT 和 DELETE 是用于 CRUD 操作的 HTTP 方法。下表显示了这些方法的描述以及使用 swagger 工具[***https://demoqa.com/swagger/的示例 URL***](https://demoqa.com/swagger/)

| HTTP 方法 | 手术                         | 操作类型                           | 示例网址                                                     |
| --------- | ---------------------------- | ---------------------------------- | ------------------------------------------------------------ |
| 得到      | 获取书籍列表                 | 只读                               | ***curl -X GET "https://demoqa.com/BookStore/v1/Books" -H "接受：应用程序/json"*** |
| 邮政      | 添加书籍列表                 | 非幂等的                           | ***curl -X POST "https://demoqa.com/BookStore/v1/Books" -H "accept: application/json" -H "Content-Type: application/json" -d "{ "userId": "toolsqa_test" , "collectionOfIsbns": [ { "isbn": "9781449325862" } ]}"*** |
| 放        | 用给定的 ISBN 替换 ISBN 对象 | 不适用                             | ***curl -X PUT "https://demoqa.com/BookStore/v1/Books/9781449325889" -H "accept: application/json" -H "Content-Type: application/json" -d "{ "userId": " toolsqa_test", "isbn": "9781449325862"}"*** |
| 删除      | 删除具有给定 ISBN 的图书     | 幂等：无论调用多少次，结果都相同。 | ***curl -X DELETE "https://demoqa.com/BookStore/v1/Book" -H "accept: application/json" -H "Content-Type: application/json" -d "{ "isbn": "9781449325862" , "userId": "toolsqa_test"}"*** |

### ***如何使用 REST API 测试 GET 操作？***

那么我们如何在 Swagger 工具中测试这些方法呢？让我们看一个*GET*操作的例子。导航到以下链接：[***https ://demoqa.com/swagger/#/BookStore/BookStoreV1BooksGet***](https://demoqa.com/swagger/#/BookStore/BookStoreV1BooksGet)

我们可以看到以下 REST API 以*获取 GET*图书详细信息。

![获取图书详细信息请求](https://toolsqa.com/gallery/selnium%20webdriver/3.Get%20Book%20Details%20Request.png)

现在让我们***“执行”*** ***“获取书籍详细信息”***的 GET 操作。当我们单击此按钮并执行 API 时，将执行以下给出的命令或 GET 语法：

```java
curl -X GET "https://demoqa.com/BookStore/v1/Books" -H "accept: application/json"
```

在执行上述操作时，我们得到以下响应。

![获取图书详细信息响应](https://toolsqa.com/gallery/selnium%20webdriver/4.GET%20Book%20details%20response.png)

以类似的方式，我们也可以执行其他操作，我们已经为此制作了专门的帖子。在后续的文章中，我们将学习自己创建一个 REST API。

因此，对于 REST API 方法，我们应该记住以下几点。

-   *GET 方法是只读且安全的。*
-   *PUT 和 DELETE 方法是幂等的：无论调用方法的时间如何，它们返回的响应总是相同的。*
-   *PUT 和 POST 操作通常是相同的，只是 POST 操作可以返回不同的结果，而 PUT 是幂等的。*

## 关键要点

在本文中，我们给出了 REST API 的一般概念。

-   *一组符合 REST 标准并满足其所有约束的 API 就是 REST API。*
-   *使用 RESTful API，应用程序可以使用上面讨论的 REST API 方法从数据库等数据源访问信息。*
-   *不同类型的应用程序可以访问 RESTful API，例如动态 Web 应用程序、Android 应用程序或桌面应用程序。REST API 包含实现的核心逻辑，我们无需在每次新应用程序进入环境时都重写它。*