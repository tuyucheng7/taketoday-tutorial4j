## 1. 概述

在本教程中，我们将解释网络浏览器使用 cookie 域的方式。cookie 域是 cookie 的一个属性，其值为[域名](https://www.baeldung.com/cs/dns-intro)。Web 浏览器使用此域名来确定是否应在当前 HTTP 请求中发送 cookie。我们将首先激发对 cookie 的需求。然后我们将解释 Set-cookie 和 Cookie 标头的格式。最后，我们将解释浏览器如何使用 cookie 域和其他 cookie 属性来确定何时将 cookie 发送到 Web 服务器。

## 2. 对 Cookie 的需求

超[文本传输协议 (HTTP)](https://www.baeldung.com/cs/rest-vs-http)是万维网 (WWW) 中的主要协议。它是一个[客户端-服务器](https://www.baeldung.com/cs/client-vs-server-terminology)协议。HTTP 客户端或用户代理(如 Web 浏览器)向 HTTP 服务器发送请求。因此，服务器通过发送响应来回复。通常，Web 浏览器会发送请求，从服务器托管的网站下载某个网页。随后，浏览器可能会向同一个服务器发送多个请求。

然而，HTTP 服务器是[无状态](https://www.baeldung.com/cs/networking-stateless-stateful-protocols)的。换句话说，他们不记得请求。因此，他们对待客户端发送的每个请求与任何其他请求完全一样。这里的好处是服务器变得更快，更容易实现。尽管如此，在许多情况下，服务器需要记住以前与某些客户端的交互。这就是 cookie 发挥作用的地方。

### 2.1. 饼干

让我们通过一个例子来激励 cookie 的使用。我们知道许多网站需要识别个人用户。例如，电子商务网站可能喜欢向用户推荐新商品。这些项目可能基于他们的购买或浏览历史。

因此，网站的服务器可能会在用户首次访问时为其分配 ID。然后它应该将这些 ID 连同响应一起发送给用户。最后，在随后访问该网站时，用户应将其 ID 连同他们的请求一起发送。这是 cookie 的一种可能用法。

因此，cookie 是 HTTP 服务器发送给 HTTP 客户端(例如网络浏览器)的数据。例如，它可能包含 ID 信息。浏览器然后将此数据存储在存储空间中，有时称为“cookie jar”。随后，当浏览器再次联系同一台服务器时，它会发送之前收到的 cookie。

### 2.2. Cookie 格式

[HTTP 服务器在响应消息](https://www.baeldung.com/cs/http-status-codes)的标头中发送 cookie 。标头有一个名为 Set-cookie 的字段：

```
设置cookie：id =“user1”；到期 = 2023 年 1 月 16 日星期一 22:00:00 GMT；最大年龄=3600；域名=“thedomain.com”；路径=“/示例”；安全的; 仅限HTTP
```

我们注意到 cookie 由几个部分组成，以分号分隔。所有部分都是可选的，除了第一个是强制性的。它由一个 cookie 名称和值组成(上例中的 id = “user1”)。可选部分都在上面的示例中显示。它们是 cookie 属性：

-   Expires：用户代理(浏览器)丢弃 cookie 的日期
-   Max-age：用户代理应保留 cookie 的最大秒数
-   域：浏览器用来确定是否应在请求中发送 cookie 的域名
-   Path：浏览器用来确定是否应该在请求中发送 cookie 的域名路径
-   安全：浏览器仅在连接安全时才发送 cookie，例如，使用传输层安全 (TLS) 协议的安全套接字连接
-   HttpOnly：浏览器只有在它只想发送 HTTP 消息时才应该访问 cookie，即它不应该通过下载页面中的 Javascript 代码访问它

另一方面，HTTP 客户端(浏览器)在请求消息的标头中将 cookie 发送到服务器。标头字段称为Cookie：

```
饼干：id =“user1”
```

服务器的响应消息可能包含多个Set-cookie标头，条件是所有 cookie 名称都不同。cookie 属性的值决定了浏览器在发送 cookie 时必须遵循的规则。

## 3. Cookie 交换规则

假设用户是第一次![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a9cba92437b396a60e4bfd0abc81355f_l3.svg)连接到网站![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。用户代理，即浏览器，然后向 发送一个 GET 请求![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。现在![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)注意到请求中没有 cookie。因此，![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)可以决定在其对客户端的响应中发送Set-cookie标头。

当然，这个标头将包含一个 cookie 名称和由 选定的值![小号](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-52fd2a0fc27878e7dfce68d4632b4ffb_l3.svg)。它还可能包含上述任何一种可选属性。Cookie 属性确定是否应在后续请求中发送 cookie。 我们将重点关注Domain和Path属性的作用。

### 3.1. 发送 Cookie 的决定

当用户代理在服务器响应中收到 Set-cookie 标头时，它会存储 cookie 名称和值。此外，如果存在Domain属性，则其值(即域名)在名称前使用隐式通配符 (.) 进行存储。如果缺少此属性，则将存储的值设置为源服务器的确切域名。

此外，如果存在 Path 属性，则存储其值。如果缺少，则将存储的值设置为源服务器的统一资源标识符 (URI) 的路径部分。

例如，假设浏览器在 HTTP 响应中收到以下标头：

```
设置 Cookie：id="user1"
```

如果发送响应的服务器的 URI 是 thedomain.com/test，那么域名将存储为“thedomain.com”，Path name 将设置为“test”。

现在，假设同一 URI 的 Web 服务器向浏览器发送了以下标头：

```
设置 Cookie: id="user1"; 域名=“thedomain.com”
```

在这种情况下，存储的域名将指示“.thedomain.com”，即域和任何子域。

然后，当用户代理发送请求时，它会检查请求的主机属性。它还检查请求的路径。只有在以下情况下，cookie 才会作为请求中的标头发送：

-   Host 属性的值与存储的域名匹配，包括任何通配符
-   请求的路径与存储的路径名相同或者是其子路径

### 3.2. 交易所示例

假设用户![在](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-a9cba92437b396a60e4bfd0abc81355f_l3.svg)连接到域名为thedomain.com的网站。这是消息交换的示例：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-df52d8a85980ef5b47879acc720feaf7_l3.svg)

在前面的示例中，用户代理决定在第二个请求中将 cookie 发送到服务器。这个决定是基于前面解释的规则。

### 3.3. HTTP Cookie 标头的更多示例

让我们考虑更多示例：

-   Set-cookie: id=”user1″ was received from URI thedomain.com/index
    标头 Cookie: id=”user1″将在请求中发送到 URI：thedomain.com/index 和 thedomain.com/index/subpath

它不会被发送到 www.thedomain.com/index。这是因为Set-cookie 标头中缺少Domain属性。因此，域名必须与浏览器从中接收到Set-cookie标头的域名相似。

-   设置cookie：id=”user1”；从 URI thedomain.com/index 收到Domain=”thedomain.com”
    标头 Cookie：id=”user1”将在请求中发送到 URI：www.thedomain.com/index，www.subdomain.thedomain.com/ index 和 www.domain.com/index/lists
    我们在这里注意到，我们将 cookie 发送到Set-cookie中包含的Domain属性值的任何子域。正如我们前面提到的，这相当于使用通配符。

## 4.隐私问题

一些公司，例如广告公司，可能能够跟踪不同网站上的用户行为。他们使用第三方 cookie。

### 4.1. 第三方 Cookie

假设我们访问 URI thedomain.com 的网页。通常，网页包含来自比方说 the-ads.com 的广告。现在，我们的浏览器在下载 thedomain.com 的页面时会联系 the-ads.com。这使 the-ads.com(即我们案例中的第三方)能够将 cookie 存储在浏览器的 cookie 罐中。例如，这些 cookie 可能包括用户的偏好。

然后，过了一会儿，我们访问了一个 URI 为 anotherdomain.com 的网页。巧合的是，anotherdomain.com 也包含来自 the-ads.com 的广告。在这种情况下，the-ads.com 将收到浏览器在我们访问 thedomain.com 时存储的 cookie。换句话说，the-ads.com 现在可以跟踪我们在多个站点(即 thedomain.com 和 anotherdomain.com)上的操作

### 4.2. 浏览器设置和规定

目前，大多数浏览器都有隐私设置，用户可以在其中选择是否愿意接收第三方 cookie。此外，欧盟 (EU) 的通用数据保护条例 (GDPR) 要求网站在发送 cookie 之前征得用户的同意。

## 5.总结

在本文中，我们介绍了 cookie 的作用及其在万维网中的用途。我们还解释了浏览器根据 cookie 的域属性决定发送 cookie 的方式。