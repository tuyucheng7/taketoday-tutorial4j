## 1. 概述

REST是一种无状态架构，客户端可以在其中访问和操作服务器上的资源。通常，REST服务利用HTTP来公布它们管理的一组资源，并提供允许客户端获取或更改这些资源状态的API。

在本教程中，我们将了解一些处理REST API错误的最佳实践，包括为用户提供相关信息的有用方法、来自大型网站的示例以及使用示例Spring REST应用程序的具体实现。

## 2. HTTP状态码

当客户端向HTTP服务器发出请求并且服务器成功接收到请求时，服务器必须通知客户端请求是否已成功处理。

HTTP使用五类状态代码来完成此操作：

-   100类(信息性)：服务器确认请求
-   200类(成功)：服务器按预期完成了请求
-   300类(重定向)：客户端需要执行进一步的操作来完成请求
-   400类(客户端错误)：客户端发送了无效的请求
-   500类(服务器错误)：由于服务器错误，服务器无法满足有效请求

根据响应代码，客户端可以推测特定请求的结果。

## 3. 处理错误

处理错误的第一步是为客户端提供正确的状态代码。此外，我们可能需要在响应正文中提供更多信息。

### 3.1 基本响应

我们处理错误的最简单方法是使用适当的状态代码进行响应。

以下是一些常见的响应代码：

-   400 Bad Request：客户端发送了无效的请求，例如缺少所需的请求体或参数
-   401 Unauthorized：客户端无法通过服务器进行身份验证
-   403 Forbidden：客户端已通过身份验证但无权访问所请求的资源
-   404 Not Found：请求的资源不存在
-   412 Precondition Failed：请求标头字段中的一个或多个条件评估为假
-   500 Internal Server Error：服务器发生一般性错误
-   503 Service Unavailable：请求的服务不可用

这些代码虽然很基础，但可以让客户了解所发生错误的广泛性质。我们知道，如果我们收到403错误，例如，我们没有权限访问我们请求的资源。但在许多情况下，我们需要在回复中提供补充细节。

500错误表示服务器在处理请求时发生了一些问题或异常。一般来说，这个内部错误与我们客户无关。

因此，为了尽量减少对客户端的此类响应，我们应该努力尝试处理或捕获内部错误，并尽可能使用其他适当的状态代码进行响应。

例如，如果由于请求的资源不存在而发生异常，我们应该将其公开为404而不是500错误。

这并不是说永远不应该返回500，只是说它应该用于阻止服务器执行请求的意外情况(例如服务中断)。

### 3.2 默认Spring错误响应

这些原则是如此普遍，以至于Spring已将它们编入其默认的[错误处理机制](https://www.baeldung.com/exception-handling-for-rest-with-spring)中。

为了演示，假设我们有[一个简单的Spring REST应用程序](https://github.com/eugenp/tutorials/tree/master/spring-web-modules/spring-rest-simple)来管理书籍，并有一个端点通过其ID检索书籍：

```bash
curl -X GET -H "Accept: application/json" http://localhost:8082/spring-rest/api/book/1
```

如果没有ID为1的书，我们预计我们的控制器将抛出BookNotFoundException。

在此端点上执行GET，我们看到抛出了此异常，这是响应正文：

```json
{
    "timestamp":"2019-09-16T22:14:45.624+0000",
    "status":500,
    "error":"Internal Server Error",
    "message":"No message available",
    "path":"/api/book/1"
}
```

请注意，此默认错误处理程序包括错误发生时间的时间戳、HTTP状态代码、标题(错误字段)、在默认错误中启用消息时的消息(默认情况下为空)和URL路径错误发生的地方。

这些字段为客户或开发人员提供信息以帮助解决问题，并且还构成了构成标准错误处理机制的一些字段。

另请注意，当抛出BookNotFoundException时，Spring会自动返回HTTP状态代码500。尽管某些API将返回500状态代码或其他通用代码，正如我们将在Facebook和Twitter API中看到的那样，但为了简单起见，对于所有错误，最好尽可能使用最具体的错误代码。

在我们的示例中，我们可以添加[@ControllerAdvice](https://www.baeldung.com/exception-handling-for-rest-with-spring#controlleradvice)以便在抛出BookNotFoundException时，我们的API返回状态404来表示Not Found而不是500 Internal Server Error。

### 3.3 更详细的回复

如上面的Spring示例所示，有时状态代码不足以显示错误的细节。需要时，我们可以使用响应正文为客户端提供附加信息。

在提供详细回复时，我们应包括：

-   错误：错误的唯一标识符
-   消息：人类可读的简短消息
-   详细信息：对错误的详细解释

例如，如果客户端发送带有错误凭据的请求，我们可以发送带有此正文的401响应：

```json
{
    "error": "auth-0001",
    "message": "Incorrect username and password",
    "detail": "Ensure that the username and password included in the request are correct"
}
```

错误字段不应与响应代码匹配。相反，它应该是我们的应用程序独有的错误代码。通常，错误字段没有约定，希望它是唯一的。

通常，此字段仅包含字母数字和连接字符，例如破折号或下划线。例如，0001、auth-0001和incorrect-user-pass是错误代码的典型示例。

正文的消息部分通常被认为是可呈现在用户界面上的。因此，如果我们支持[国际化](https://www.baeldung.com/spring-boot-internationalization)，我们应该翻译这个标题。因此，如果客户端发送带有与法语对应的Accept-Language标头的请求，则标题值应翻译为法语。

详细信息部分供客户端开发人员而非最终用户使用，因此无需翻译。

此外，我们还可以提供一个URL(例如帮助字段)，客户可以按照该URL来发现更多信息：

```json
{
    "error": "auth-0001",
    "message": "Incorrect username and password",
    "detail": "Ensure that the username and password included in the request are correct",
    "help": "https://example.com/help/error/auth-0001"
}
```

有时，我们可能希望为一个请求报告多个错误。

在这种情况下，我们应该返回列表中的错误：

```json
{
    "errors": [
        {
            "error": "auth-0001",
            "message": "Incorrect username and password",
            "detail": "Ensure that the username and password included in the request are correct",
            "help": "https://example.com/help/error/auth-0001"
        },
        ...
    ]
}
```

当发生单个错误时，我们会响应一个包含一个元素的列表。

请注意，使用多个错误进行响应对于简单的应用程序来说可能过于复杂。在许多情况下，用第一个或最重要的错误进行响应就足够了。

### 3.4 标准化响应机构

虽然大多数REST API遵循类似的约定，但细节通常有所不同，包括字段名称和响应正文中包含的信息。这些差异使得库和框架很难统一处理错误。

为了标准化REST API错误处理，IETF设计了[RFC 7807](https://tools.ietf.org/html/rfc7807)，它创建了一个通用的错误处理模式。

该模式由五个部分组成：

1.  type：对错误进行分类的URI标识符
2.  标题：关于错误的简短的、人类可读的消息
3.  状态：HTTP响应代码(可选)
4.  detail：人类可读的错误解释
5.  instance：一个URI，标识错误的具体发生

我们可以转换我们的主体，而不是使用我们的自定义错误响应主体：

```json
{
    "type": "/errors/incorrect-user-pass",
    "title": "Incorrect username or password.",
    "status": 401,
    "detail": "Authentication failed due to incorrect username or password.",
    "instance": "/login/log/abc123"
}
```

请注意，type字段对错误类型进行分类，而instance分别以类似于类和对象的方式标识错误的特定发生。

通过使用URI，客户端可以按照这些路径查找有关错误的更多信息，就像使用[HATEOAS链接](https://www.baeldung.com/spring-hateoas-tutorial)导航REST API一样。

遵守RFC 7807是可选的，但如果需要统一性，它是有利的。

## 4. 例子

上述做法在一些最流行的REST API中很常见。虽然字段或格式的具体名称可能因站点而异，但一般模式几乎是通用的。

### 4.1 Twitter

让我们在不提供所需身份验证数据的情况下发送GET请求：

```bash
curl -X GET https://api.twitter.com/1.1/statuses/update.json?include_entities=true
```

Twitter API响应此正文错误：

```json
{
    "errors": [
        {
            "code":215,
            "message":"Bad Authentication data."
        }
    ]
}
```

此响应包括一个包含单个错误及其错误代码和消息的列表。在Twitter的例子中，没有详细的消息，并且使用一般错误——而不是更具体的401错误-来表示身份验证失败。

有时更通用的状态代码更容易实现，正如我们将在下面的Spring示例中看到的那样。它允许开发人员捕获异常组，而不区分应返回的状态代码。但是，如果可能，应该使用最具体的状态代码。

### 4.2 Facebook

与Twitter类似，[Facebook的Graph REST API](https://developers.facebook.com/docs/graph-api)也在其响应中包含详细信息。

让我们执行一个POST请求以使用Facebook Graph API进行身份验证：

```bash
curl -X GET https://graph.facebook.com/oauth/access_token?client_id=foo&client_secret=bar&grant_type=baz
```

我们收到此错误：

```json
{
    "error": {
        "message": "Missing redirect_uri parameter.",
        "type": "OAuthException",
        "code": 191,
        "fbtrace_id": "AWswcVwbcqfgrSgjG80MtqJ"
    }
}
```

与Twitter一样，Facebook也使用一般错误，而不是更具体的400级错误，来表示失败。除了消息和数字代码之外，Facebook还包括一个对错误进行分类的类型字段和一个充当[内部支持标识符的跟踪ID(fbtrace_id)](https://developers.facebook.com/docs/graph-api/using-graph-api/error-handling)) 。

## 5. 总结

在本文中，我们研究了REST API错误处理的一些最佳实践：

-   提供特定的状态代码
-   在响应正文中包含其他信息
-   统一处理异常

虽然错误处理的细节因应用程序而异，但这些一般原则适用于几乎所有REST API，应尽可能遵守。

这不仅允许客户端以一致的方式处理错误，而且还简化了我们在实现REST API时创建的代码。