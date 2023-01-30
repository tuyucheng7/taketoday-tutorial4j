## 1. 概述

有时，从我们网页上的 JavaScript 触发的 HTTP 请求之前是一个 OPTIONS 请求。

在本教程中，我们将了解发送此额外请求的原因。

接下来，我们将看看如何抑制这种所谓的预检请求。在无法选择抑制的情况下，我们将研究如何限制它们。

## 2. 背景

介绍中提到的OPTIONS请求是preflight请求，属于CORS(Cross-Origin Resource Sharing)的一部分。CORS 是一种提供配置以配置对共享资源的访问的机制。当网页向其原始服务器以外的另一台服务器发出请求时，CORS 适用，这可能意味着域、协议或端口不同。

浏览器使用请求检查服务器是否允许该请求。只有当请求被允许时，它才会实际执行它。

在执行从foo.baeldung.com到www.baeldung.com/demo的 PUT 请求之前，将执行预检请求，它看起来如下：

```plaintext
OPTIONS /demo HTTP/1.1
Host: www.baeldung.com
User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)
  AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36
Accept: /
Accept-language: nl-NL,nl;q=0.9,en-US;q=0.8,en;q=0.7
Connection: keep-alive
Origin: http://foo.baeldung.com
Access-Control-Request-Method: PUT
```

请注意，即使对于没有预检请求的 CORS 请求，仍然需要服务器允许跨源请求，为此，浏览器需要在服务器的响应中使用适当的Access-Control-Allow-Origin标头。

## 3.请求类型

CORS 区分两种不同类型的请求。官方规范没有给没有预检的请求命名，但是，[MDN](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)使用名称“简单请求”，我们也将在此处使用。我们还将使用[CORS 规范](https://fetch.spec.whatwg.org/#cors-safelisted-request-header)中提到的规则的轻微简化。该规范包含对值中允许的字符的额外限制，以进一步增强安全性。

当请求是 GET、POST 或 HEAD 请求时，它可以是简单请求。除此之外，请求只允许包含安全列表中的标头，包括以下标头：

-   接受
-   接受语言
-   内容语言
-   内容类型

对于Content-Type标头，仅允许以下有限值集：

-   应用程序/x-www-form-urlencoded
-   多部分/表单数据
-   文本/纯文本

最后，不应在XMLHttpRequestUpload对象上使用事件侦听器或在请求中使用ReadableStream 。

所有其他请求只有在预检请求的响应确认该请求被允许后才会执行。

## 4. 禁用 OPTIONS 请求

让我们看一下禁用附加 OPTIONS 请求的选项。

### 4.1. 使其成为同源请求

让我们首先验证是否有意将我们执行的调用转到不同的服务器？也许还有其他事情正在发生。我们在设置环境时犯了错误吗？我们是否无意中通过不同的协议调用了我们的 API，例如，我们是否在我们的 HTTPS 网站上使用了 HTTP 调用？好的，这些是最重要的检查。我们还能做什么？

我们可以考虑代理 API 请求，使它们通过源服务器。或者我们可以考虑使用反向代理服务器使我们的网站和 API 在同一主机名的不同路径下可用。好吧，如果我们确定我们真的需要一个跨源请求，让我们看看下一节中的一些其他选项。

### 4.2. 让它成为一个简单的请求

在前面的一节中，我们了解到不会为简单请求发送预检请求。因此，如果我们想要禁用预检请求，我们的下一个最佳选择是确保该请求是一个简单的请求。这假定服务器发送了正确的Access-Control-Allow-Origin标头。

这意味着，我们可以在不需要预检请求的情况下执行 GET 请求。但是，对 POST 请求的限制更严格。这意味着，例如，我们不能在没有预检的情况下发送 JSON 请求。但是，发送“表格”就可以了。

因此，以下 JavaScript 片段不会触发预检请求：

```javascript
var request = new XMLHttpRequest();
request.open("POST", 'http://localhost:8080/with-valid-cors-header');
request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
request.send("a=b&c=d");
```

但是，当我们更改请求方法、添加额外的标头或更改内容类型时，我们最终会得到一个需要额外的 OPTIONS 请求的请求。

最后，即使请求需要预检，我们也可以限制预检的次数。我们通过指示浏览器缓存 CORS 配置来实现这一点，在下一节中我们将对此进行更详细的介绍。

## 5.缓存OPTIONS请求

有时，我们无法将请求设为同源请求或简单请求。在这种情况下，我们可以确保至少限制预检请求的数量。让我们通过在预检响应上设置 max-age 来做到这一点。

标头Access-Control-Max-Age负责缓存 CORS 配置。此标头的值以秒为单位表示。允许缓存 5 分钟的示例如下所示：

```plaintext
Access-Control-Allow-Origin: 
Access-Control-Allow-Methods: POST, PUT, OPTIONS
Access-Control-Allow-Headers: Content-Type
Access-Control-Max-Age: 300
```

有关其他标头的更多背景信息，请参阅[MDN 的 CORS 文章](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)。

## 六，总结

在本文中，我们研究了为什么某些请求前面有 OPTIONS 请求。我们简要解释了 CORS 和确定请求是否将被预检的规则。接下来，我们查看了一些提示和技巧，以确保我们不会发出不必要的预检请求。最后，我们查看了缓存 CORS 配置的选项。