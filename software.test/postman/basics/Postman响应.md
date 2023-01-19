### 什么是响应？

Response是服务器接收到的消息，以返回我们发送的Request。当我们请求某些东西时，服务器根据请求执行操作并发回请求信息的数据包。响应主要取决于请求。每个请求都有不同类型的响应，从所有响应中提取有用信息非常重要。Postman 有一个漂亮的响应界面，非常人性化。我们可以在 Postman 中看到很多信息，无需付出太多努力，或者任何我可能会说的任何回应。


# 了解 Postman 中的响应

谈到Postman 中的 Response，Response 用户界面包含很多不同的东西。我们将在本教程中详细处理它们。用户界面有以下信息块

-   响应状态和信息
-   响应体
-   回应饼干
-   响应头

让我们从获取www.google.com的响应开始，如下所示：

![response_with_request 回复](https://www.toolsqa.com/gallery/Postman/1.response_with_request.png)

## 响应状态和信息

### 状态码：

状态代码告诉你请求的状态。请求中可能有很多错误，如果不查看状态代码，我们可能无法始终了解我们的请求出了什么问题。有时，URL 中可能存在输入错误，或者服务器端可能存在问题，状态代码可帮助我们了解出了什么问题(如果出现问题)。有不同的状态代码，每个状态代码都有不同的含义。

[你可以在此处](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes)了解状态代码的完整列表。

![Status_Code_200_2](https://www.toolsqa.com/gallery/Postman/2.Status_Code_200_2.png)

状态代码200 OK表示请求是正确的，所需的响应已发送到客户端。现在，将 URL 更改为http://restapi.demoqa.com/utilities/weatherfull/city/hyderabd 。 按发送并立即查看状态代码。

![400_Bad_Request](https://www.toolsqa.com/gallery/Postman/3.400_Bad_Request.png)

它说400 BAD REQUEST。之所以如此，是因为我们已将城市名称从Hyderabad 更改为 Hyderabad。这意味着请求不正确，因此请求响应错误。同样，对于不同的请求，你也可以看到其他状态代码。

### 时间

时间是我们发送请求并收到响应后响应所用的持续时间。这有时非常重要，因为许多项目都有服务级别协议 ( SLA )，规定 Web 服务返回响应所需的时间，这个时间可用于确定 Web 服务端点的 SLA。

![时间](https://www.toolsqa.com/gallery/Postman/4.Time.png)

注意：此处给出的时间不是请求实际花费的时间。它只是近似值，但几乎是它应该的样子，因为 Postman 在得到响应后会做很多事情，例如分别格式化和划分 Headers 和 cookie。由于Postman额外的工作可以粗略地认为是一个常数时间(\WebServiceTime + Constant processing time by Postman\)。因此，它是时间的近似值，与实际时间成正比。因此，你也可以将其视为实际时间。

### 尺寸

Size就是当它被保存在内存中时的响应大小。此响应大小是完整响应、标头和 cookie 以及随响应一起发送的所有内容的大小。

![尺寸_响应](https://www.toolsqa.com/gallery/Postman/5.Size_Reponse.png)

注意：Postman 中显示的响应大小是近似响应大小，而不是准确大小。

## 响应体

body描述了响应的主体，它是从服务器发送的主要响应内容。在这种情况下，如你所见，它是作为响应发送给我们的网页代码。现在，我们可以通过三种方式来看待这种反应：

![体型](https://www.toolsqa.com/gallery/Postman/6.Body_Types.png)

### 漂亮的

Pretty是发送内容的更漂亮版本。内容更漂亮，因为它更具可读性。它有彩色关键字，不同的颜色有不同的含义。这使代码更具可读性并且看起来更好看。这个格式化是Postman拿到code后自己做的。

![漂亮的回应](https://www.toolsqa.com/gallery/Postman/7.pretty_response.png)

### 生的

单击预览后，你将获得从服务器接收到的内容的简单视图。它只是代码的原始版本，没有任何丰富多彩的关键字。通过查看这段代码，你可能会明白为什么其他代码被称为“ Pretty ”。

![原始响应](https://www.toolsqa.com/gallery/Postman/8.raw_response.png)

### 预习

如果页面在浏览器中运行，代码预览将向你显示页面的预览。单击预览，你将看到与在浏览器中看到的完全一样的页面。所以这会让你在不访问浏览器的情况下知道响应预览。

![预览_响应](https://www.toolsqa.com/gallery/Postman/9.preview_response.png)

### 格式类型

如上所述，一个请求有一个定义的响应，由Content-Type头定义。该响应可以采用任何格式。例如，在本例中，我们将响应作为 HTML 代码文件。

![格式_类型_HTML](https://www.toolsqa.com/gallery/Postman/10.Format_Type_HTML.png)

Postman 足够聪明，可以检测响应类型并以所需的格式向你显示，但有时 Postman 也会出错。例如，使用 http://restapi.demoqa.com/utilities/weatherfull/city/hyderabad获取响应。

你会看到我们收到了状态码 200，但仍然没有任何回应。这是因为 Postman 无法识别响应的格式，并且需要一个 HTML 文件，如下拉列表中所示。

在下拉列表中选择文本，你现在将能够看到响应。

![格式_类型_文本](https://www.toolsqa.com/gallery/Postman/11.Format_Type_Text.png)

有时，服务器会以两种或多种不同的格式发送响应。响应的类型将对其相应的格式类型可见。

注意： Content-Type 标头定义了响应的格式。例如，Content-Type 标头可能表明响应是 JSON，但是，发送的内容是 XML 或格式错误的 JSON。在那种情况下，Postman 将无能为力。以此作为练习，理解为什么 Postman 无法理解 http://restapi.demoqa.com/utilities/weatherfull/city/hyderabad返回的响应格式

### 复制回复

你在角落看到的带有两个矩形的图标用于将完整的响应复制到剪贴板，这对于将响应发送给你的队友或之后使用非常方便。

![复制_回复](https://www.toolsqa.com/gallery/Postman/12.Copy_Response.png)

## 曲奇饼

Cookie 是与服务器文件(网站页面)相关的小文件。一旦你第一次访问网站，就会在客户的机器上下载一个 cookie。该 cookie 包含你再次访问同一网站时可以使用的信息。这有助于网站根据你的上次访问为你提供特定的响应和特定信息。在邮递员中我们可以清楚地看到服务器发送的cookie作为响应。这使客户端可以轻松查看浏览器中保存了哪些 cookie。我们无法操作这些 cookie，因为它们是从服务器发送的，Postman 只是用来将它与响应分开并有一个清晰的视图。

![饼干](https://www.toolsqa.com/gallery/Postman/13.Cookies.png)

## 标头

HTTP 请求或响应中的标头是传输给用户或服务器的附加信息。在邮递员中，标题可以在“标题”选项卡中看到。

![标题_响应](https://www.toolsqa.com/gallery/Postman/14.Header_Response.png)

单击标题后，你可以看到不同的信息，如下所示。尽管 Headers 选项卡中的每个条目都是一个标题项，但我们将只查看最重要的条目。

-   Content-Type ： 这是响应的内容类型。在上面的示例中，当我们使用 www.google.com 时，内容类型被指定为文本/HTML，因为响应是以 HTML 格式发送的，这是选项之一。
-   日期： 此选项显示响应的日期、日期和时间以及时区。
-   服务器： 此选项告诉已响应请求的服务器的名称。在上面的示例中，服务器名称显示为gws ，对应于Google Web Server。
-   Cookie expire time : 顾名思义，这个选项告诉响应中已经发送的 cookie 的过期时间。