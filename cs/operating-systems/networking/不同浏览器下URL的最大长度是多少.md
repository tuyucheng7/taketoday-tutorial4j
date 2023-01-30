## 1. 概述

在本教程中，我们将讨论不同浏览器中 URL 的最大长度。我们还介绍了 URL、Web 浏览器和 Web 服务器的基本概念。

## 2. URL介绍

[URL](https://www.baeldung.com/java-url-vs-uri)的完整形式是统一资源定位器。URL 也称为网址。它由一个独特的字符串组成，作为计算机网络上网络资源的参考。更具体地说，URL 提供了网络资源(例如图像、音频、视频、文本或 HTML 文件)的位置和检索机制。此外，URL 是一种类型的[统一资源标识符](https://www.baeldung.com/java-url-vs-uri)。

1994 年，  [Tim Berners-Lee在](https://en.wikipedia.org/wiki/Tim_Berners-Lee)[RFC 1738](https://en.wikipedia.org/wiki/RFC_(identifier))中定义了 URL 。我们可以很容易地在网络浏览器的顶部地址栏中找到网站的 URL。我们在引用网页时使用它们。此外，一些应用程序(例如访问数据库、传输文件)也使用它们。

一般来说，一个URL由三部分组成：协议、主机名和文件名。让我们看一个例子：

![newlinetextbf{https://www.baeldung.com/about}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5053b3c8dac915686dfe5716a012f7e5_l3.svg)

此处“HTTPS”表示协议：[安全超文本传输协议 (HTTPS)](https://www.baeldung.com/cs/https-urls-encrypted)。此外，“www.baeldung.com”代表网站的主机名。最后，“about”表示网站的文件名。

## 3.网络浏览器

我们通常使用网络浏览器从[万维网 (WWW)](https://en.wikipedia.org/wiki/World_Wide_Web)获取网络资源，并通过界面将内容呈现在用户的设备上。它是使用编程语言实现和开发的软件应用程序。

Web 浏览器不同于[搜索引擎](https://en.wikipedia.org/wiki/Search_engine)。搜索引擎重定向并提供指向其他网站的链接。相反，网络浏览器识别并连接到特定网站的网络服务器，并将网页内容显示到用户的设备。

最初，用户在网络浏览器的网络地址栏中提供输入 URL。接下来，网络浏览器通过万维网向网络服务器发送请求。网络服务器与存储网站网络资源的数据库相连。因此，它以所需的 Web 内容响应浏览器的请求。最后，浏览器显示输入 URL 的网页：

![hfhgfhgf.drawio](https://www.baeldung.com/wp-content/uploads/sites/4/2022/02/hfhgfhgf.drawio.png)

我们通常使用[HTTP](https://www.baeldung.com/cs/popular-network-protocols)或 HTTPS 来从网络服务器获取网络资源。但是，HTTPS 比 HTTP 更安全。通过 HTTPS 的通信已加密并增强了安全性。

某些网页可能包含将用户从一个网站重定向到另一个网站或资源的[超链接。](https://en.wikipedia.org/wiki/Hyperlink)如果用户点击此类链接，检索数据的过程将重新开始。

流行的网络浏览器包括[Google Chrome](https://en.wikipedia.org/wiki/Google_Chrome)、[Mozilla Firefox](https://en.wikipedia.org/wiki/Firefox)、[Internet Explorer](https://en.wikipedia.org/wiki/Internet_Explorer)、[Safari](https://en.wikipedia.org/wiki/Safari_(web_browser))、[Opera](https://en.wikipedia.org/wiki/Opera_(web_browser))、[Apache](https://en.wikipedia.org/wiki/Apache)。

## 4.网络服务器

Web 服务器是软件和硬件的组合。它访问存储在数据集中的 Web 资源，并通过万维网将它们转发到用户的浏览器。Web 服务器通常使用 HTTP 或 HTTPS。但是，Web 服务器还可以使用[SMTP](https://en.wikipedia.org/wiki/Simple_Mail_Transfer_Protocol)发送电子邮件和使用[FTP](https://www.baeldung.com/cs/popular-network-protocols)发送文件。

Web 服务器有两种类型：静态和动态。静态 Web 服务器仅包含 HTTP 和主机设备。它转发网络资源而不对其进行任何控制。另一方面，动态 Web 服务器包含 HTTP、主机设备、应用程序服务器和数据库。

动态网络服务器可以更新和控制网络资源的流动。此外，它还可以根据用户的请求生成和转发网络资源。

流行的 Web 服务器的一些示例是[Apache HTTP 服务器](https://en.wikipedia.org/wiki/Apache_HTTP_Server)、[Microsoft Internet Information Services](https://en.wikipedia.org/wiki/Internet_Information_Services)、[Lighttpd](https://en.wikipedia.org/wiki/Lighttpd)、[Sun Java System Web Server](https://en.wikipedia.org/wiki/Sun_Java_System)、[Nginx](https://en.wikipedia.org/wiki/Nginx)。

## 5. 不同浏览器中URL的最大长度

没有固定长度的 URL。此外，它因浏览器而异。通常，URL 的最大长度最多可达![mathbf{2048}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-733b60ff4a9aeebc6d26b954e1d19d71_l3.svg)字符。因此，网站的 URL 过长被认为是网页在 WWW 中排名的不利因素。

一般来说，由![mathsf{75-120}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-440c05d5f5daa8f44d538892cc5a1c0a_l3.svg)字符组成的 URL 可以被适当地索引。此外，字符较少的 URL 在 WWW 中获得良好排名的可能性更高。

现在让我们讨论一下不同网络浏览器中 URL 的最大长度。

最流行的 Web 浏览器Google Chrome 允许 URL 最多包含 2048 个字符。![数学{2048}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-dae3a5bee6b87a408bd9c5cb4f92805b_l3.svg)因此，我们不能在 Google Chrome 浏览器的网址栏中输入超过字符的字符。

在 Mozilla Firefox 中，没有这样的 URL 最大长度。但是，在![mathbf{65536}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ade0300fa09fabd226f73a4293fba080_l3.svg)字符之后，Firefox 的地址栏不再显示 URL。

对于 Internet Explorer，URL 的最大长度可以是![mathbf{2083}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ab7ffe38c49f7aefdadd0a4ab5797243_l3.svg)字符。

Safari 浏览器最多支持![mathbf{80000}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b1c4a47a8645d28502b4a19de356fc40_l3.svg)一个 URL 字符。提供包含多个![mathsf{80000}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-e0f40301b98d071aa148c59a8d4e697e_l3.svg)字符的 URL 会产生错误。

Opera 对 URL 长度没有这样的限制。但是，它最多可以在地址栏中显示![mathbf{190000}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-06635b3f9d64bd9dc5e5fca43c7f6d1c_l3.svg)字符。

最后，Apache 支持最大![mathbf{4000}](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ef8724dbc6207a5bc7464acc5369cbfb_l3.svg)字符数。超过 4000 个字符将产生错误。

让我们总结一下各种浏览器的 URL 最大长度：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6d6075cb754981690c2fc5424490014e_l3.svg)

## 六，总结

在本教程中，我们讨论了不同浏览器中 URL 的最大长度。为了给读者一个完整的画面，我们还介绍了 URL、Web 浏览器和 Web 服务器的基本概念。