如果我们经常浏览互联网，“ cookie ”或“ HTTP cookie ”这个词对我们任何人来说都不是一个新术语。我们在访问网站时经常会遇到这个词，屏幕上会弹出一条消息，说明该网站使用了“第三方”cookies，我们通常会按下“接受”。请记住这个想法，因为我们将在本文后面返回它。设计这篇文章的主要原因首先是让你熟悉HTTP cookies世界。如果你不知道什么是 cookie？很难理解它们的工作原理以及如何在任何API测试工具(例如 [Postman )中使用和管理它们](https://www.toolsqa.com/postman/postman-tutorial/). 有了这个注释，这里是我们将在本教程中介绍的主要部分的列表：

-   什么是 HTTP Cookie？
    -   为什么我们使用 HTTP Cookie？
    -   如何在浏览器中设置 HTTP Cookie？
    -   什么是第三方 Cookie？如何设置它们？

## 什么是 HTTP Cookie？

HTTP Cookies，通常也称为浏览器 cookies 或 internet cookies，是保存在客户端(即网络浏览器)上并由服务器发送的一小段信息。这是什么信息？这完全取决于设计网站的开发人员。开发人员可以将登录信息保存为 Internet cookie，将用户浏览器历史记录保存为 Internet cookie，或以后可能使用的任何其他内容。

换句话说，你可以将HTTP cookie视为特定网站或其身份的记忆。一旦用户在网址上按下回车键，如果为该网站保存了浏览器 cookie，服务器就会召回用户并相应地为他们提供服务。很久以前我在某处读到过一个完美的陈述；HTTP cookie 会记住无状态 HTTP 协议的有状态信息。

为了让你熟悉HTTP cookie的样子，让我们探索一下我们的浏览器：

1.  在你的 Chrome 浏览器中输入chrome://settings 或在你使用的任何浏览器中访问“设置”部分。

它将打开设置面板。

1.  在搜索面板中输入“ Cookies ”。

![在浏览器中搜索 http cookie](https://www.toolsqa.com/gallery/Postman/1.search%20http%20cookies%20in%20browser.png)

1.  打开查看所有 cookie 和站点数据。

![选择 http cookie](https://www.toolsqa.com/gallery/Postman/2.select%20http%20cookies.png)

你可以看到所有在你的系统上保存了 HTTP cookie 或浏览器 cookie 的网站，当我在我的浏览器上数一数时，它们实际上有数百个。

选择任一网站，你将能够看到特定网站保存的 HTTP cookie。

好吧，很明显，即使我们不知道任何这样的事情，我们也得到了它的帮助，以改善我们的浏览器体验。但是我们仍然不明白为什么我们首先需要HTTP cookie？有那么重要吗？如果我从字面上告诉你，一个HTTP cookie是数十亿美元贸易的原因呢？让我们看看如何。

### 为什么我们使用 HTTP Cookie？

如果我想通俗地了解HTTP cookie，最好的说法是将这些 internet cookie 描述为只存在于 internet 上的你的影子。他们到处跟着你！！ 他们也跟着你来了！老实说，浏览器 cookie 是你不能忽视的东西，这使得它们在开发人员和测试人员的生活中至关重要。从广义上讲，我们在三个部分使用互联网 cookie：

-   会话管理：一旦用户登录到网站，就会为他们创建一个会话，并使用识别该会话的会话 ID。HTTP cookie 可以很好地管理这一点。通过 HTTP cookie，我们可以保存你的游戏分数或记住你是以前的用户并自动登录。它可以扩展到服务器想要记住的任何东西；我们可以使用我们的浏览器 cookie 来做到这一点。
-   跟踪：使用 HTTP cookie 进行跟踪有助于企业了解你的兴趣并为你提供更好的服务。例如，如果我在亚马逊上浏览了一个笔式驱动器，则表明我对它感兴趣。因此，当我访问另一个网站时，如果广告对我有用，让我感兴趣会增加点击它的机会，这是有道理的。这只是跟踪的一个小例子。通过浏览器 cookie 进行跟踪可用于向你展示推荐的产品等等。
-   个性化：通过 HTTP cookie 的个性化帮助用户根据自己对网站或网站上的任何其他组件进行个性化设置。例如，流行的搜索引擎 DuckDuckGo 可以帮助用户为页面设置颜色。当用户第一次选择颜色时，DuckDuckGo 服务器会发送一个用用户名/系统 ID 包裹的浏览器 cookie，以便特定用户在任何时候搜索时，颜色页面都是相同的。

所以在某种程度上，HTTP cookie是一条双向道路。它为企业提供了一种赚取数十亿美元的方法，并为用户提供了更好、更舒适的体验。想一想当你在 Amazon 上的会话用完时你将不得不一次又一次地登录(你会知道进行网络抓取时的痛苦)。如果你正在看与帽子有关的广告，而我只对笔式驱动器感兴趣怎么办？HTTP cookies对我们来说是有益的，作为开发人员和测试人员，我们必须知道如何设置这些cookies。

### 如何在浏览器中设置 HTTP Cookie？

在本节中，我们将探讨服务器用于在用户代理端(即浏览器)设置 cookie 的不同属性和方法。在我们继续设置 HTTP cookie 之前，请务必记住有两种类型的浏览器 cookie：

-   会话 Cookie：一旦会话结束，这些类型的浏览器 cookie 就会被删除。
-   永久性 Cookie：这些类型的浏览器 cookie 保留在系统中，并在每次打开网站时与服务器通信。

要设置 cookie，我们根据需要使用带有一长串属性的“ Set-Cookie ”标头。

句法：

设置 Cookie：`<cookie-name>`=`<cookie-value>`

使用[Postman](https://www.toolsqa.com/postman/postman-tutorial/)，我们将能够看到来自服务器的完整响应以及 cookie；对于本教程，我们将只使用语法。

#### HTTP Cookie 属性

如前一节所述，互联网 cookie 确实具有为 cookie 提供更多含义的属性。否则，cookie 只是一个名称和一个值。这些属性将帮助我们在用户的浏览器上设置 cookie。让我们更详细地了解所有这些属性：

##### 过期

HTTP cookie的“ expires ”属性提供了 cookie 的生命周期值。一旦达到该值，cookie 将自动删除。在浏览器 cookie 中提供过期值很重要，这样它会随着信息根据用户行为不断变化而定期刷新。如果未在标头中指定此属性，则HTTP cookie会自动成为会话 cookie，并在会话结束后被删除。我们可以在语法上设置如下：

设置 Cookie: `<cookie-name>`= `<cookie-value>`; 过期 =`<date>`

##### 最大年龄

与“ expires ”属性类似，max-age属性指定HTTP cookie过期之前的时间。如果同时指定了“ expires ”和“ max-age ”属性，则“ \\max-age\\ ”属性优先于它。此外，值为0 或负值将立即使 cookie 过期，因此此属性中需要一个非零正值。我们可以在句法上设置如下：

设置 Cookie: `<cookie-name>`= `<cookie-value>`; 最大年龄 =`<number>`

##### 安全的

指定安全属性意味着对 cookie 进行编码并在客户端系统上保存机密信息。我们只能通过 HTTPS  方案请求安全的HTTP cookie 。我们可以在语法上设置如下：

设置 Cookie: `<cookie-name>`= `<cookie-value>`; 安全的

##### 小路

路径值指定应在请求的[URL](https://en.wikipedia.org/wiki/URL)内的路径，否则浏览器不会将 cookie 发送到服务器。路径URL可能类似于[ToolsQA](https://www.toolsqa.com/)上的 /Back-End/Postman，因此仅当包含此路径时才会发送浏览器 cookie。只要指定的路径存在，这条路径前面是什么并不重要。我们可以在语法上设置如下：

设置 Cookie: `<cookie-name>`= `<cookie-value>`; 路径=`<path-value>`

##### 领域

域值指定HTTP cookie需要发送到的主机。例如，toolsqa.com是一个域名。所有子域都在指定的主域下，所有子域都包含在 cookie 中。我们可以在语法上设置如下：

设置 Cookie: `<cookie-name>`= `<cookie-value>`; 域名=`<domain-value>`

##### 仅限 HTTP 的 Cookie

如果为 HTTP- only属性设置了 cookie ，则客户端将无法访问 cookie。拥有HTTP-Only属性可以探索客户端存在任何缺陷的可能性，并且更加安全，因为 Javascript 无法从客户端访问 cookie。我们可以在语法上设置如下：

设置 Cookie: `<cookie-name>`= `<cookie-value>`; 仅限HTTP

所有这些属性都是可选的，这取决于开发人员对网站的要求。

### 什么是第三方 Cookie？

很长一段时间以来，作为用户，每当我们通过 Internet 访问网站时，都会被弹出窗口分散注意力。弹出窗口显示“此网站使用第三方 cookie ”。大多数时候，弹出窗口只允许我们选择一个选项，“接受”。这很粗鲁，不是吗？不过，这让我们想知道，“什么是第三方 cookie？ ”以及网站为何使用它们？

第三方 HTTP cookie 由除用户正在访问的网站之外的其他网站放置在客户端的浏览器中(因此称为“第三方”cookie)。例如，第三方 cookie 可能由广告代理机构设置，用于在使用该特定机构的广告的另一个网站上投放广告。第三方 cookie 主要用于广告目的和跟踪用户。尽管开发人员可以将它用于任何目的，但他们想要。用户可以将第三方 cookie 视为开发者与第三方之间的合作伙伴关系，以便更好地为用户服务。因此，下次你看到弹出窗口时，“此网站使用 cookie”，几乎所有时候，都是根据用户的兴趣投放广告。这种做法并不新鲜，但严格的网络法律已经强制执行，要求浏览器开发人员将这些事情告知用户。

#### 如何设置第三方Cookie？

设置第三方cookie，应该是开发者愿意的。因此，开发人员在他们的网站中放置了一个链接，该链接在加载时会访问第三方服务器。服务器然后识别用户。如果用户是新用户，则会将第三方HTTP cookie放置到他的浏览器中。如果用户不是新用户，则发送到服务器的请求会检索用户信息。例如，他的兴趣、浏览历史等来自HTTP cookie，并在网站上放置适当的广告。

![设置第三方 Cookie](https://www.toolsqa.com/gallery/Postman/3.Setting%20up%20Third%20Party%20Cookies.png)

这是一个简单的过程。此外，如果你访问浏览器上的 cookie 部分，你可以在其中看到所有“广告 cookie ”。用户从浏览器中清除 cookie 后，你会注意到当你再次访问同一网站时广告发生了怎样的变化。

## 结论

这篇文章围绕HTTP cookies展开，它是我们互联网生活的一部分。无论你多么讨厌每次都被跟踪或被监视，你都不能忽略HTTP cookie。Google 90% 以上的收入来自广告收入。因此很明显，无论你做什么，Cookie 始终是你的影子。另一方面，也不算太糟。因为它有助于通过互联网为用户创造更舒适的体验。

当 cookie 可以自动为你登录时，每天登录网站真的很烦人。访问网站、保存 cookie，然后将该 cookie 和数据发送给第三方是一种安全威胁。此外，这也是一个耗时的过程。这会产生第三方 cookie。广告公司直接设置这些 cookie 是为了让这个过程更安全、更快捷。因此，下次你在网站上看到该弹出窗口时，你就会知道它的含义及其影响。