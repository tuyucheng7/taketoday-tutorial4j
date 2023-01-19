[让我们从上一篇文章“SoapUI 中用于 Rest 服务的 REST 断言”](https://www.toolsqa.com/soapui/soapui-rest-assertions/)继续我们的旅程在那里我们了解了 SoapUI 提供的各种断言的详细信息，这些断言可以应用于 REST 服务。在本文中，我们将详细介绍SoapUI中特定于 SOAP 服务的 SOAP 服务断言。这些断言中的大部分属于SoapUI中的“合规性、状态和标准”类别。只有在测试 SOAP 服务时才会启用这些断言。在本文中，我们将在以下主题中详细介绍所有这些 SOAP 特定断言：

-   什么是 SoapUI 中 SOAP 服务的测试特定断言？
    -   什么是 SoapUI 中的非 SOAP 故障断言？
    -   同样，SoapUI 中的 SOAP Fault Assertion 是什么？
    -   什么是 SoapUI 中的 SOAP 响应断言？
    -   什么是 SoapUI 中的 WS-Addressing 响应断言？
    -   同样，SoapUI 中的 WS-Security Status Response Assertion 是什么？
    -   SoapUI 中的 JMS 断言是什么？

## SoapUI 中的 SOAP 服务断言是什么？

SoapUI 中有仅适用于 SOAP 服务的特定断言。这些声明中的大部分位于“合规性、状态和标准”的范围内，如下所示：

![SoapUI 中的 SOAP 服务特定断言](https://www.toolsqa.com/gallery/SoapUI/1.SOAP%20service%20specific%20assertions%20in%20SoapUI.png)

从上图中可以明显看出，所有这些断言都是特定于 SOAP 服务的验证。

-   为了理解这些断言的细节，我们将使用 SOAP 服务 WSDL： [“http://bookstore.toolsqa.com/BookStoreService.wsdl”](https://bookstore.toolsqa.com/BookStoreService.wsdl)。
-   按照文章“SoapUI：使用项目”中提到的步骤为此 SOAP 服务添加一个新项目。
-   该服务的示例输出如下所示：

```java
<s:Envelope xmlns:xsd="https://www.w3.org/2001/XMLSchema" xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance" xmlns:s="https://schemas.xmlsoap.org/soap/envelope/">
   <s:Body>
      <BooksResponse xmlns="https://tempuri.org/">
         <BooksResult>
            <Books>
               <CustomBookModel>
                  <Isbn>9781449325862</Isbn>
                  <Title>Git Pocket Guide</Title>
                  <SubTitle>A Working Introduction</SubTitle>
                  <Author>Richard E. Silverman</Author>
                  <Published>2013-08-02T00:00:00</Published>
                  <Publisher>O'Reilly Media</Publisher>
                  <Pages>234</Pages>
                  <Description>This pocket guide is the perfect on-the-job companion to Git, the distributed version control system. It provides a compact, readable introduction to Git for new users, as well as a reference to common commands and procedures for those of you with Git experience.</Description>
                  <Website>http://chimera.labs.oreilly.com/books/1230000000561/index.html</Website>
               </CustomBookModel>
               <CustomBookModel>
                  <Isbn>9781449331818</Isbn>
                  <Title>Learning JavaScript Design Patterns</Title>
                  <SubTitle>A JavaScript and jQuery Developer's Guide</SubTitle>
                  <Author>Addy Osmani</Author>
                  <Published>2012-07-01T00:00:00</Published>
                  <Publisher>O'Reilly Media</Publisher>
                  <Pages>254</Pages>
                  <Description>With Learning JavaScript Design Patterns, you'll learn how to write beautiful, structured, and maintainable JavaScript by applying classical and modern design patterns to the language. If you want to keep your code efficient, more manageable, and up-to-date with the latest best practices, this book is for you.</Description>
                  <Website>http://www.addyosmani.com/resources/essentialjsdesignpatterns/book/</Website>
               </CustomBookModel>
               <CustomBookModel>
                  <Isbn>9781449337711</Isbn>
                  <Title>Designing Evolvable Web APIs with ASP.NET</Title>
                  <SubTitle>Harnessing the Power of the Web</SubTitle>
                  <Author>Glenn Block et al.</Author>
                  <Published>2014-04-07T00:00:00</Published>
                  <Publisher>O'Reilly Media</Publisher>
                  <Pages>538</Pages>
                  <Description>Design and build Web APIs for a broad range of clients—including browsers and mobile devices—that can adapt to change over time. This practical, hands-on guide takes you through the theory and tools you need to build evolvable HTTP services with Microsoft’s ASP.NET Web API framework. In the process, you’ll learn how design and implement a real-world Web API.</Description>
                  <Website>http://chimera.labs.oreilly.com/books/1234000001708/index.html</Website>
               </CustomBookModel>
               <CustomBookModel>
                  <Isbn>9781449365035</Isbn>
                  <Title>Speaking JavaScript</Title>
                  <SubTitle>An In-Depth Guide for Programmers</SubTitle>
                  <Author>Axel Rauschmayer</Author>
                  <Published>2014-02-01T00:00:00</Published>
                  <Publisher>O'Reilly Media</Publisher>
                  <Pages>460</Pages>
                  <Description>Like it or not, JavaScript is everywhere these days-from browser to server to mobile-and now you, too, need to learn the language or dive deeper than you have. This concise book guides you into and through JavaScript, written by a veteran programmer who once found himself in the same position.</Description>
                  <Website>http://speakingjs.com/</Website>
               </CustomBookModel>
               <CustomBookModel>
                  <Isbn>9781491904244</Isbn>
                  <Title>You Don't Know JS</Title>
                  <SubTitle>ES6 &amp; Beyond</SubTitle>
                  <Author>Kyle Simpson</Author>
                  <Published>2015-12-27T00:00:00</Published>
                  <Publisher>O'Reilly Media</Publisher>
                  <Pages>278</Pages>
                  <Description>No matter how much experience you have with JavaScript, odds are you don’t fully understand the language. As part of the "You Don’t Know JS" series, this compact guide focuses on new features available in ECMAScript 6 (ES6), the latest version of the standard upon which JavaScript is built.</Description>
                  <Website>https://github.com/getify/You-Dont-Know-JS/tree/master/es6%20&amp;%20beyond</Website>
               </CustomBookModel>
               <CustomBookModel>
                  <Isbn>9781491950296</Isbn>
                  <Title>Programming JavaScript Applications</Title>
                  <SubTitle>Robust Web Architecture with Node, HTML5, and Modern JS Libraries</SubTitle>
                  <Author>Eric Elliott</Author>
                  <Published>2014-07-01T00:00:00</Published>
                  <Publisher>O'Reilly Media</Publisher>
                  <Pages>254</Pages>
                  <Description>Take advantage of JavaScript's power to build robust web-scale or enterprise applications that are easy to extend and maintain. By applying the design patterns outlined in this practical book, experienced JavaScript developers will learn how to write flexible and resilient code that's easier-yes, easier-to work with as your code base grows.</Description>
                  <Website>http://chimera.labs.oreilly.com/books/1234000000262/index.html</Website>
               </CustomBookModel>
               <CustomBookModel>
                  <Isbn>9781593275846</Isbn>
                  <Title>Eloquent JavaScript, Second Edition</Title>
                  <SubTitle>A Modern Introduction to Programming</SubTitle>
                  <Author>Marijn Haverbeke</Author>
                  <Published>2014-12-14T00:00:00</Published>
                  <Publisher>No Starch Press</Publisher>
                  <Pages>472</Pages>
                  <Description>JavaScript lies at the heart of almost every modern web application, from social apps to the newest browser-based games. Though simple for beginners to pick up and play with, JavaScript is a flexible, complex language that you can use to build full-scale applications.</Description>
                  <Website>http://eloquentjavascript.net/</Website>
               </CustomBookModel>
               <CustomBookModel>
                  <Isbn>9781593277574</Isbn>
                  <Title>Understanding ECMAScript 6</Title>
                  <SubTitle>The Definitive Guide for JavaScript Developers</SubTitle>
                  <Author>Nicholas C. Zakas</Author>
                  <Published>2016-09-03T00:00:00</Published>
                  <Publisher>No Starch Press</Publisher>
                  <Pages>352</Pages>
                  <Description>ECMAScript 6 represents the biggest update to the core of JavaScript in the history of the language. In Understanding ECMAScript 6, expert developer Nicholas C. Zakas provides a complete guide to the object types, syntax, and other exciting changes that ECMAScript 6 brings to JavaScript.</Description>
                  <Website>https://leanpub.com/understandinges6/read</Website>
               </CustomBookModel>
            </Books>
         </BooksResult>
      </BooksResponse>
   </s:Body>
</s:Envelope>
```

-   使用文章“在 SoapUI 中创建和构建测试用例”中提到的步骤向其中添加一个新的测试用例。
-   单击 SOAP 服务 TestStep 屏幕底部断言选项卡上的 (+) 图标，按照文章“了解 SoapUI 中的断言”中提到的步骤添加新断言。

我们将对上述 SOAP 服务的响应执行所有基于 SOAP 的断言的验证。

让我们在以下部分中了解所有这些断言类型的详细信息：

### 什么是 SoapUI 中的非 SOAP 故障断言？

SOAP 错误是SOAP([简单对象访问协议](https://en.wikipedia.org/wiki/SOAP))通信中的错误，它可能由不正确的消息格式、标头处理问题或应用程序之间的不兼容引起。

此断言将验证在调用 SOAP 服务期间没有发生错误。让我们按照下面提到的步骤来了解如何使用“Not SOAP Fault”断言：

1.  在“Add Assertion”对话框中，在“Compliance, Status and Standards”类别下，选择“ Not SOAP Fault ”断言，如下图：

![在 SoapUI 中添加 Not SOAP 错误断言](https://www.toolsqa.com/gallery/SoapUI/2.Add%20Not%20SOAP%20Fault%20Assertion%20in%20SoapUI.png)

1.  单击“添加”按钮，它将自动对服务的最后响应运行验证。在我们的场景中，由于服务返回了一个没有 SOAP 错误的有效 SOAP 响应，因此断言将通过并显示响应，如下所示：

![不是 SoapUI 中的 SOAP 错误断言](https://www.toolsqa.com/gallery/SoapUI/3.Not%20SOAP%20Fault%20Assertion%20in%20SoapUI.png)

1.  因此，我们已经成功地在 SOAP 服务上添加了一个断言，它验证了在调用 SOAP 服务时没有发生任何错误。

### 什么是 SoapUI 中的 SOAP 错误断言？

与“Not SOAP Fault”断言相反，“SOAP Fault”将检查SOAP服务响应中是否存在SOAP错误。所以，如果没有错误，这个断言就会失败。

让我们按照下面提到的步骤来了解如何使用“SOAP Fault”断言：

1.  导航到“添加断言”对话框后，单击“合规性、状态和标准”类别下的“SOAP 错误”断言，如下所示：

![在 SoapUI 中添加 SOAP 故障断言](https://www.toolsqa.com/gallery/SoapUI/4.Add%20SOAP%20Fault%20Assertion%20in%20SoapUI.png)

1.  单击“添加”按钮，它将自动对服务的最后响应运行验证。在我们的场景中，由于服务返回了一个没有 SOAP 错误的有效 SOAP 响应，因此断言将失败并显示响应，如下所示：

![SOAP Fault 断言失败](https://www.toolsqa.com/gallery/SoapUI/5.SOAP%20Fault%20assertion%20failure.png)

1.  因此，我们已经成功地在 SOAP 服务上添加了另一个断言，它验证了在调用 SOAP 服务时发生了错误并且失败了，因为实际上在调用 SOAP 服务时没有发生 SOAP 错误。

### 什么是 SoapUI 中的 SOAP 响应断言？

顾名思义，此断言检查响应是否是有效的 SOAP 响应。当我说有效的 SOAP 响应时，这意味着它符合所有[简单对象访问协议 (SOAP) 标准](https://en.wikipedia.org/wiki/SOAP#Data_encapsulation_concepts)。例如，如我们所见，书店 Web 服务的响应(如上所示)按照 SOAP 协议的要求包装在信封( `<s: Envelope>`)中。

让我们按照下面提到的步骤来了解如何在 SoapUI 中使用“SOAP Response”断言：

1.  导航到“添加断言”对话框后，单击“合规性、状态和标准”类别下的“SOAP 响应”断言，如下所示：

![在 SoapUI 中添加 SOAP 响应断言](https://www.toolsqa.com/gallery/SoapUI/6.Add%20SOAP%20Response%20Assertion%20in%20SoapUI.png)

1.  单击“添加”按钮，它将自动对服务的最后响应运行验证。在我们的场景中，由于服务已根据 SOAP 标准返回有效的 SOAP 响应，因此断言将通过并显示响应，如下所示：

![SoapUI 中的 SOAP 响应断言验证](https://www.toolsqa.com/gallery/SoapUI/7.SOAP%20response%20assertion%20validation%20in%20SoapUI.png)

1.  因此，我们成功地在 SOAP 服务上添加了另一个断言，它根据 SOAP 标准验证 SOAP 服务响应有效并通过，因为书店服务返回的响应是有效的 SOAP 响应。

### 什么是 SoapUI 中的 WS-Addressing 响应断言？

WS-Addressing 响应断言验证响应中的WS -Addressing 标头。导入WSDL 时， SoapUI 将尝试从中提取[WS-Addressing](https://en.wikipedia.org/wiki/WS-Addressing)相关信息。如果检测到，WS-Addressing 将自动启用包含正确版本和标头集的请求。示例 SOAP 服务响应(包括 SOAP 标头中的 WS-Addressing)如下所示：

```java
<S:Envelope
    xmlns:S="https://www.w3.org/2003/05/soap-envelope"
    xmlns:wsa="https://schemas.xmlsoap.org/ws/2004/08/addressing">
    <S:Header>
        <wsa:MessageID>
uuid:6B29FC40-CA47-1067-B31D-00DD010662DA
</wsa:MessageID>
        <wsa:ReplyTo>
            <wsa:Address>http://business456.example/client1</wsa:Address>
        </wsa:ReplyTo>
        <wsa:To>http://fabrikam123.example/Purchasing</wsa:To>
        <wsa:Action>http://fabrikam123.example/SubmitPO</wsa:Action>
    </S:Header>
    <S:Body>
 ...
</S:Body>
</S:Envelope>
```

让我们按照下面提到的步骤来了解如何在 SoapUI 中使用“WS-Addressing Response”断言：

1.  导航到“添加断言”对话框后，单击“合规性、状态和标准”类别下的“WS-Addressing Response”断言，如下所示：

![在 SoapUI 中添加 WS 寻址响应断言](https://www.toolsqa.com/gallery/SoapUI/8.Add%20WS%20Addressing%20Response%20Assertion%20in%20SoapUI.png)

1.  单击“添加”按钮后，你将看到如下所示的对话框：

![在 SoapUI 中显示 WS A 属性断言的对话框](https://www.toolsqa.com/gallery/SoapUI/9.Dialog%20showing%20WS%20A%20properties%20Assertion%20in%20SoapUI.png)

1.  通过选中复选框来选择要使用的断言类型，然后单击“确定”。这些复选框中的每一个都验证了 SOAP 响应标头中提到的标记的存在。在我们的场景中，由于服务不包含 WS-Addressing 标头，因此断言将失败并显示如下响应：

![SoapUI 中的 WS 地址响应验证失败](https://www.toolsqa.com/gallery/SoapUI/10.WS%20Adressing%20Response%20validation%20failure%20in%20SoapUI.png)

1.  因此，我们成功地在 SOAP 服务上添加了另一个断言，它验证 SOAP 服务响应标头包含WS-Addressing标头，但失败了，因为书店服务返回的响应不包含任何WS-Addressing标头。

### 什么是 SoapUI 中的 WS-Security 状态响应断言？

Web Services Security 或 [WS-Security](https://en.wikipedia.org/wiki/WS-Security)是一个说明如何在 Web 服务中实施安全措施的规范。它是一组协议，通过实施机密性、完整性和身份验证原则来确保基于 SOAP 的消息的安全性。

SoapUI 为我们提供了一个自动验证安全响应的断言。WS-Security Status 断言可以验证最后收到的消息是否包含有效的 WS-Security 标头。

让我们按照下面提到的步骤来了解如何在 SoapUI中使用“WS-Security Status”断言：

1.  导航到“添加断言”对话框后，单击“合规性、状态和标准”类别下的“WS-Security Status”断言，如下所示：

![在 SoapUI 中添加 WS 安全状态断言](https://www.toolsqa.com/gallery/SoapUI/11.Add%20WS%20Security%20Status%20assertion%20in%20SoapUI.png)

1.  单击“添加”按钮，它将自动对服务的最后响应运行验证。在我们的场景中，由于服务不包含 WS-Security 标头，断言将失败并显示如下响应：

![SoapUI 中的 WS 安全断言验证](https://www.toolsqa.com/gallery/SoapUI/12.WS%20Security%20assertion%20validation%20in%20SoapUI.png)

1.  因此，我们成功地在 SOAP 服务上添加了另一个断言，它验证 SOAP 服务响应标头是否包含WS-Security标头，但失败了，因为书店服务返回的响应不包含任何WS-Security标头。

### SoapUI 中的 JMS 断言是什么？

除了上述类型的断言外，JMS断言仅启用 SOAP 服务。JMS([J](https://en.wikipedia.org/wiki/Java_Message_Service) ava 消息服务)在两个或多个客户端之间发送消息时出现。它有助于通信更加可靠和异步。JMS 也是一个标准，允许基于 JEE 的应用程序创建、发送、接收和读取消息。JMS API 支持两种不同的模型：

-   点对点
-   发布和订阅

SoapUI 集成/结合了 HermesJMS 开源应用程序，后者支持许多不同的 JMS 提供程序。它支持 Java 消息服务，允许你发送和接收文本和二进制消息。

你可以在安装时在你的SoapUI中配置HermesJMS，然后可以借助 SoapUI 提供的以下两个断言来验证消息的时间和状态：

-   JMS 超时
-   JMS 状态。

让我们按照下面提到的步骤来了解如何在 SoapUI中使用“JMS”断言：

1.  导航到“添加断言”对话框后，单击“JMS”类别下的“JMS 超时”断言，如下所示：

![在 SoapUI 中添加 JMS 断言](https://www.toolsqa.com/gallery/SoapUI/13.Add%20JMS%20Assertion%20in%20SoapUI.png)

1.  点击“添加”按钮，会出现如下配置JMS超时的对话框

![JMS断言](https://www.toolsqa.com/gallery/SoapUI/14.JMS%20assertion.png)

1.  根据服务的响应时间输入值。单击“确定”添加它。由于 BookStore API 不使用任何与 JMS 相关的端点，因此在这种情况下它将失败。
2.  与 JMS 超时类似，你也可以添加“JMS 状态”断言。它有助于验证 JMS 的状态。由于 BookStore API 没有任何与 JMS 相关的端点，因此没有消息通信发生，因此认为断言已通过。添加两个 JMS 特定验证后，你将看到以下断言验证屏幕：

![SoapUI 中的 JMS 断言视图](https://www.toolsqa.com/gallery/SoapUI/15.JMS%20assertions%20view%20in%20SoapUI.png)

1.  因此，我们已经成功地添加了 JMS 特定的断言，这些断言仅在 SoapUI 中为 SOAP 服务启用。

因此，这就完成了所有 SoapUI 断言的细节，我们可以将其仅用于 SOAP Web 服务响应的验证。

## 关键要点

-   SoapUI 提供了仅适用于 REST 服务的断言类别。
-   SOAP 故障断言验证 SOAP 响应中故障的存在
-   SOAP 响应断言根据 SOAP 标准验证 SOAP 服务的响应。
-   WS-Addressing 和 WS-Security Status 断言验证 SOAP 响应中是否存在 <wsa> 标头。
-   JMS 特定断言验证基于 JMS 的 SOAP 服务端点的超时和状态。

让我们转到下一篇文章，我们将详细介绍一些[“适用于 SOAP 和 REST 服务的常见断言”。](https://www.toolsqa.com/soapui/soapui-common-assertions/)