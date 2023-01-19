API 使开发人员的生活变得更加轻松。[API 或应用程序编程接口](https://www.toolsqa.com/postman/api-testing-with-postman/)是一种媒介或“接口”，可帮助我们在各种不相关的网站之间建立通信并在它们之间创建交互。API 的一个常见用例是将实时数据从另一台服务器传送到我们的服务器，而无需编写大量代码或更改我们的应用程序。例如，如果我想获得最近的股票市场价格，我可以每分钟(或任何其他时间间隔)调用股票市场 API ，它将从远程服务器检索最新的代码数据。就我们作为程序员的工作而言，我们需要调用 API 端点，这是接受请求并返回我们请求的数据作为响应的媒介。有趣的是，Browserling 为开发人员提供了自己的 API，称为 Browserling Live API。

它有助于将其浏览器基础设施利用到自己的应用程序中。它允许你在自己的浏览器中嵌入浏览器。Browserling Live API 及其分步指南将是本文的重点。目录如下：

-   什么是 Browserling Live API？
-   何时使用 Browserling Live API？
-   通过沙盒浏览器增强安全性
    -   运行旧版浏览器应用程序
    -   自动化质量检查
    -   持续集成
-   如何设置 Live API？

## 什么是 Browserling Live API？

Browserling Live API 让你可以将你选择的实时交互式浏览器嵌入到你自己的 Web 应用程序中。“实时交互”一词使这个 API 如此有趣。Live API 获取在线浏览器(在 Browserling 的服务器上运行)并将其作为真实浏览器嵌入到你的网站中。所以它不仅仅是一个图像截图，它还可以让你与之交互，就好像你在本地机器上运行浏览器一样。最终结果类似于[在 Browserling 上执行实时交互式跨浏览器测试，](https://www.toolsqa.com/browserling/cross-browser-testing-with-browserling/)但在你自己的 Web 应用程序中。此外，通过在线浏览器会话，你可以使用鼠标、键盘进行交互，甚至可以使用几行代码自动执行这些操作。![实时 api 浏览器](https://toolsqa.com/gallery/browserling/1.live%20api%20browserling.png)

[作为 Web 开发人员，你可以利用Browserling 的虚拟浏览器基础架构](https://www.browserling.com/)创建自己的跨浏览器测试解决方案，并节省开发自己的内部解决方案的成本。除了这个用例，人们还可以出于下一节中描述的许多其他原因使用 Browserling Live API。

## 何时使用 Browserling Live API？

我们可以在多个用例中使用 Browserling Live API。从跨浏览器测试到安全沙盒浏览，用户可以使他们的应用程序具有交互性，并通过 API 使它栩栩如生。从十几个不同的用例中，下面列出了其中的一些：

### 通过沙盒浏览器增强安全性

安全是一个必不可少的因素，尤其是当用户正在处理不受信任的网站时，例如软件下载网站或钓鱼网站。如果你从未知网站下载软件，如果下载的软件包含恶意软件，那么你距离清除所有数据只有一步之遥，否则它甚至可能导致你的系统被劫持。使用 Browserling Live API，用户将在 Browserling 服务器上的用户网络之外运行的完全虚拟化和[沙盒浏览器中进行交互。](https://www.browserling.com/browser-sandbox)如果木马、病毒和其他恶意文件被执行，它们会留在运行浏览器的虚拟机中，不会影响你的本地计算机和本地网络。一旦浏览器会话关闭，虚拟机连同其中的所有数据一起被销毁。

同样，Live API 可以用作检查钓鱼链接的 URL 沙箱。你可以轻松验证网络钓鱼链接指向的内容，而无需透露你自己的 IP。URL 沙箱中的所有交互都发生在 Browserling 服务器上，因此日志中显示的 IP 地址是 Browserling 云服务器的 IP。

### 运行旧版浏览器应用程序

通常，组织有许多仍在使用但不再维护的遗留浏览器应用程序。例如，可能存在无法在其他浏览器中运行的 Internet Explorer 8 应用程序。此外，随着技术的进步，组织淘汰了旧计算机，并且不再有运行带有 IE8 的 Windows XP 的计算机。在现代硬件和遗留应用程序不再兼容的情况下，你可以使用 Browserling Live API 将旧浏览器嵌入现代浏览器并运行几乎任何应用程序。例如，你可以在 Edge 中嵌入 IE8 8，或者在现代 Chrome 或 Safari 中嵌入 IE8。同样，你可以反转 Live API 并将现代浏览器嵌入到旧版浏览器中。例如，你可以在 Internet Explorer 8 中嵌入 Edge。

### 自动化质量检查

Browserling Live API 提供了许多功能来自动执行各种操作，例如鼠标单击和按键，类似于你在使用[Selenium](https://www.toolsqa.com/selenium-webdriver/selenium-tutorial/)时会遇到的情况。这些功能可以自动化测试过程，以确保质量、查找错误和实施完整的测试解决方案。

### 持续集成

我们还可以通过在每次代码提交时在应用程序中运行测试或浏览器，在持续集成管道中使用 Browserling Live API。

使用 Browserling Live API 的用例可能更多，这取决于个人的最终目标和应用程序结构。那么现在，让我们深入学习如何在本地任何网页上通过 Live API 运行 Browserling 浏览器的教程。

## 如何设置 Live API？

设置 Browserling Live API 实际上是最有趣的部分。即使你是编程新手，只需几步和不到 8 行 JavaScript 代码，就可以在你的网页上运行一个虚拟浏览器。对于此设置，我们首先需要的是 Browserling 服务器生成的 API 令牌。

请注意，每次使用 Live API 创建新会话时，都会生成一个[新会话令牌。](https://www.toolsqa.com/postman/oauth-2-0-authorization/)我将通过将 Browserling Live API 嵌入到我的虚拟应用程序中来使用 Browserling Live API 进行演示，因此我将使用[Postman](https://www.toolsqa.com/postman/postman-tutorial/)接收会话令牌。如果你的目标是一个完整的应用程序，请使用以下 URL 和 JavaScript 中的 GET 方法：

https://api.browserling.com/v1/token?username=login@gmail.com&password=password

用户名：你的浏览器帐户用户名。

密码：你的浏览器帐户密码。

在上述 URL 中输入用户名和密码后，将收到如下 JSON 响应(如果凭据正确)：

![会话令牌成功浏览](https://toolsqa.com/gallery/browserling/2.Session%20token%20Successful%20Browserling_0.png)

保存此令牌，因为我们将在前面使用它。对于错误的凭据或任何其他故障，状态将表示为“错误”，并在下一行中指定错误，如下所示：

![会话令牌失败浏览器](https://toolsqa.com/gallery/browserling/3.Session%20Token%20Failure%20Browserling_0.png)

上面用到的软件就是Postman，它可以帮助你流畅高效地处理API。[你可以在 Postman](https://www.toolsqa.com/postman/postman-tutorial/)的完整指南中了解更多信息。

如果你不使用 Postman，则必须从服务器请求令牌并从 JSON 响应中提取令牌。回到我们的小应用程序，既然我们手头有会话令牌，我们将构建我们的网页。

要构建我们的单页 Web 应用程序，我们首先需要包含 Browserling 的 JS 库，如下所示：

```
<\script src="https://api.browserling.com/v1/browserling.js"></script\>
```

该库将使我们能够访问所有 Browserling 方法和功能。

接下来，我们使用会话令牌创建一个 Browserling 实例，如下所示：

```
var browserling = new Browserling(token);
```

我们现在几乎完成了配置。接下来，我们需要选择打开网站的浏览器和操作系统。

```
browserling.setBrowser('ie');
browserling.setVersion('9');
```

接下来，我们设置要在浏览器中打开的 URL：

```
browserling.setUrl('https://www.catonmat.net');
```

只需这几行代码，我们所有的设置就完成了。使用 Browserling Live API 在你的应用程序中嵌入浏览器就这么简单。

接下来，我们需要在我们的 HTML 代码中定义区域，让 JavaScript 知道在何处嵌入浏览器。这可以通过基本的 querySelector 并将元素附加到指定的 HTML 元素(按 ID 定位)来完成。

```java
var div = document.querySelector('#browserling');

div.appendChild(browserling.iframe());

<div id = "browserling" style="width: 720; height: 640"></div>

Combining the above code together, we get the following:

<html lang="en" dir="ltr">
  <head>
    <meta charset="utf-8">
    <title> Browserling Live API</title>
  </head>
  <script src="https://api.browserling.com/v1/browserling.js"></script>
  <body>
    <script>
    function callingBrowser(){
      var browserling = new Browserling(token);
      browserling.setBrowser('ie');
      browserling.setVersion('9');
      browserling.setUrl('https://www.catonmat.net');
      var div = document.querySelector('#browserling');
      div.appendChild(browserling.iframe());
    }
    </script>
    <button onClick = "callingBrowser()">Call Browser</button>
    <div id = "browserling" style="width: 720; height: 640"></div>
  </body>

</html>
```

运行上面的代码将浏览器嵌入到网页中。

![浏览 API Gif](https://toolsqa.com/gallery/browserling/4.Browserling%20API%20Gif.gif)

你也可以使用configure方法来配置你需要运行的平台和浏览器，如下：

```java
browserling.configure({

platformName : 'win',

platformVersion : '7',

browser : 'chrome',

version : '90',

url : 'https://www.catonmat.net'

});
```

除了上述定义的方法外，Browserling 还提供了许多其他方法。此外，这些方法有助于在构建你的应用程序时实现自动化和其他此类用例。要了解有关这些功能的更多信息，请访问[Browserling Live API 文档](https://www.browserling.com/api/documentation)。

## 下一步是什么？

Browserling Live API 已成为致力于在其应用程序中嵌入虚拟浏览器的开发人员和组织中的一项流行功能。作为同一方向的下一步，Browserling 目前正在开发 Headless Live API。在这里，用户不需要运行 GUI 浏览器应用程序。尽管如此，这些方法仍将在后台执行，而不会看到浏览器。最后，Browserling 的 Headless Live API 将以类似于 Selenium Headless 测试的方式工作。在这里，你可以在不查看浏览器的情况下完成你的最终目标。例如，将 HTML 文档转换为 PDF、对 JavaScript 代码进行基准测试以及自动提交表单。

感谢阅读，如果你订阅这篇文章，我们将通过 Browserling 为你更新新版本。