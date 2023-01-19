正如我们在上一篇文章“[了解 SoapUI 中的断言”中](https://www.toolsqa.com/soapui/assertions-in-soapui/)讨论的那样，SoapUI支持各种断言，这些断言可以根据它们是否可用于特定类型的 Web 服务进行逻辑分类。同样，SoapUI 支持特定的断言，这些断言仅适用于REST服务，并且仅在你测试REST webservice时才会启用。随后，在本文中，我们将在以下主题中介绍所有这些SoapUI REST 断言的详细信息：

-   什么是 SoapUI 中 REST 服务的测试特定断言？
    -   SoapUI 中的 JsonPath 计数断言是什么？
    -   同样，SoapUI 中的 JsonPath Existence Match Assertion 是什么？
    -   SoapUI 中的 JsonPath 匹配断言是什么？
    -   同样，SoapUI 中的 JsonPath RegEx Match Assertion 是什么？

## 什么是 SoapUI REST 断言？

SoapUI 中有仅适用于 REST 服务的特定断言。此外，这些断言中的大部分位于“ Property Content Assertions ”类别下，并在下图中突出显示：

![SoapUI 中的 REST 服务特定断言](https://www.toolsqa.com/gallery/SoapUI/1.REST%20Service%20specific%20assertions%20in%20SoapUI.png)

所有这些断言都是特定于 REST 服务响应的验证，它始终是 JSON。从上面的屏幕截图可以看出，突出显示的断言仅特定于 JSON 验证。

-   为了理解这些断言的细节，我们将使用 REST 服务 URI：“ [http://bookstore.toolsqa.com/BookStore/v1/Books](https://bookstore.toolsqa.com/BookStore/v1/Books) ”。
-   除此之外，我们将按照文章“ SoapUI：使用项目”中提到的步骤为此 REST 服务添加一个新项目。
-   该服务的示例输出如下所示：

```java
{"books": [
      {
      "isbn": "9781449325862",
      "title": "Git Pocket Guide",
      "subTitle": "A Working Introduction",
      "author": "Richard E. Silverman",
      "published": "2013-08-02T00:00:00",
      "publisher": "O'Reilly Media",
      "pages": 234,
      "description": "This pocket guide is the perfect on-the-job companion to Git, the distributed version control system. It provides a compact, readable introduction to Git for new users, as well as a reference to common commands and procedures for those of you with Git experience.",
      "website": "https://chimera.labs.oreilly.com/books/1230000000561/index.html"
   },
      {
      "isbn": "9781449331818",
      "title": "Learning JavaScript Design Patterns",
      "subTitle": "A JavaScript and jQuery Developer's Guide",
      "author": "Addy Osmani",
      "published": "2012-07-01T00:00:00",
      "publisher": "O'Reilly Media",
      "pages": 254,
      "description": "With Learning JavaScript Design Patterns, you'll learn how to write beautiful, structured, and maintainable JavaScript by applying classical and modern design patterns to the language. If you want to keep your code efficient, more manageable, and up-to-date with the latest best practices, this book is for you.",
      "website": "https://www.addyosmani.com/resources/essentialjsdesignpatterns/book/"
   },
      {
      "isbn": "9781449337711",
      "title": "Designing Evolvable Web APIs with ASP.NET",
      "subTitle": "Harnessing the Power of the Web",
      "author": "Glenn Block et al.",
      "published": "2014-04-07T00:00:00",
      "publisher": "O'Reilly Media",
      "pages": 538,
      "description": "Design and build Web APIs for a broad range of clients—including browsers and mobile devices—that can adapt to change over time. This practical, hands-on guide takes you through the theory and tools you need to build evolvable HTTP services with Microsoft’s ASP.NET Web API framework. In the process, you’ll learn how design and implement a real-world Web API.",
      "website": "https://chimera.labs.oreilly.com/books/1234000001708/index.html"
   },
      {
      "isbn": "9781449365035",
      "title": "Speaking JavaScript",
      "subTitle": "An In-Depth Guide for Programmers",
      "author": "Axel Rauschmayer",
      "published": "2014-02-01T00:00:00",
      "publisher": "O'Reilly Media",
      "pages": 460,
      "description": "Like it or not, JavaScript is everywhere these days-from browser to server to mobile-and now you, too, need to learn the language or dive deeper than you have. This concise book guides you into and through JavaScript, written by a veteran programmer who once found himself in the same position.",
      "website": "https://speakingjs.com/"
   },
      {
      "isbn": "9781491904244",
      "title": "You Don't Know JS",
      "subTitle": "ES6 & Beyond",
      "author": "Kyle Simpson",
      "published": "2015-12-27T00:00:00",
      "publisher": "O'Reilly Media",
      "pages": 278,
      "description": "No matter how much experience you have with JavaScript, odds are you don’t fully understand the language. As part of the \"You Don’t Know JS\" series, this compact guide focuses on new features available in ECMAScript 6 (ES6), the latest version of the standard upon which JavaScript is built.",
      "website": "https://github.com/getify/You-Dont-Know-JS/tree/master/es6%20&%20beyond"
   },
      {
      "isbn": "9781491950296",
      "title": "Programming JavaScript Applications",
      "subTitle": "Robust Web Architecture with Node, HTML5, and Modern JS Libraries",
      "author": "Eric Elliott",
      "published": "2014-07-01T00:00:00",
      "publisher": "O'Reilly Media",
      "pages": 254,
      "description": "Take advantage of JavaScript's power to build robust web-scale or enterprise applications that are easy to extend and maintain. By applying the design patterns outlined in this practical book, experienced JavaScript developers will learn how to write flexible and resilient code that's easier-yes, easier-to work with as your code base grows.",
      "website": "https://chimera.labs.oreilly.com/books/1234000000262/index.html"
   },
      {
      "isbn": "9781593275846",
      "title": "Eloquent JavaScript, Second Edition",
      "subTitle": "A Modern Introduction to Programming",
      "author": "Marijn Haverbeke",
      "published": "2014-12-14T00:00:00",
      "publisher": "No Starch Press",
      "pages": 472,
      "description": "JavaScript lies at the heart of almost every modern web application, from social apps to the newest browser-based games. Though simple for beginners to pick up and play with, JavaScript is a flexible, complex language that you can use to build full-scale applications.",
      "website": "https://eloquentjavascript.net/"
   },
      {
      "isbn": "9781593277574",
      "title": "Understanding ECMAScript 6",
      "subTitle": "The Definitive Guide for JavaScript Developers",
      "author": "Nicholas C. Zakas",
      "published": "2016-09-03T00:00:00",
      "publisher": "No Starch Press",
      "pages": 352,
      "description": "ECMAScript 6 represents the biggest update to the core of JavaScript in the history of the language. In Understanding ECMAScript 6, expert developer Nicholas C. Zakas provides a complete guide to the object types, syntax, and other exciting changes that ECMAScript 6 brings to JavaScript.",
      "website": "https://leanpub.com/understandinges6/read"
   }
]}
```

-   使用文章“在 SoapUI 中创建和构建测试用例”中提到的步骤向其中添加一个新的测试用例。
-   此外，单击 REST 服务 TestStep 屏幕底部断言选项卡上的 (+) 图标，按照文章“了解 SoapUI 中的断言”中提到的步骤添加新断言。

我们将对 REST 服务的上面讨论的响应执行所有基于 JSON 的断言的验证。

随后，让我们在以下部分中了解所有这些断言类型的详细信息：

### SoapUI 中的 JsonPath 计数断言是什么？

JsonPath Count断言有助于计算响应中字符串的出现次数。它适用于返回 JSON作为输出的所有响应。

考虑一个场景，我们想要验证书店服务响应中的图书数量。每次都应该是“ 8 ”，我们可以从目标服务的响应中确定。此外，我们可以对这个计数进行断言，如果服务返回的计数为 8，断言应该不会失败。

让我们按照下面提到的步骤使用“ JsonPath Count ”断言进行验证：

1.  首先，导航到“添加断言”对话框并单击“ JsonPath Count ”断言，如下所示：![在 SoapUI 中为 Rest 服务添加 JsonPath 计数断言](https://www.toolsqa.com/gallery/SoapUI/2.Adding%20JsonPath%20count%20assertion%20for%20Rest%20Services%20in%20SoapUI.png)
2.  其次，选择“ Property Content ”下的“ JsonPath Count ”断言，然后单击“ Add”按钮。因此，它将显示一个对话框，如下所示：

![指定 JsonPath 计数断言的表达式和验证](https://www.toolsqa.com/gallery/SoapUI/3.Specify%20the%20expression%20and%20validation%20for%20the%20JsonPath%20Count%20assertion.png)

1.  第三，在“ JSONPath Expression ”中，指定字符串“ books ”(如标记 1 所示)，并在“ Expected Result ”部分指定“ 8 ”(如标记 2 所示)。单击“保存”按钮保存断言。因此，它将显示通过的断言结果，如下所示：

![SoapUI 中 Rest 服务 JsonPath 计数视图断言的结果视图](https://www.toolsqa.com/gallery/SoapUI/4.Result%20View%20for%20Rest%20Service%20JsonPath%20Count%20view%20assertion%20in%20SoapUI.png)

1.  之后，由于服务响应中“ books ”的计数为“ 8 ”，所以断言成功通过。按照相同的步骤，我们可以为响应中的任何关键值的计数添加验证。

### 什么是 SoapUI 中的 JsonPath 存在匹配断言？

JsonPathExistence Match 断言帮助我们找到响应 JSON中键的存在。

考虑这样一个场景，我们想要在书店服务的响应中验证键“ isbn ”的存在。它应该返回“ true ”，因为密钥存在于上述 bookStore 服务的响应中。因此，可以使用SoapUI中的JsonPath Existence Match断言来确定。

让我们按照下面提到的步骤使用“ JsonPath Existence Match ”断言来验证相同的步骤：

1.  首先，从“ Add Assertion ”对话框中，添加“ JsonPath Existence Match ”断言，如下高亮显示：

![为 SoapUI 中的 Rest 服务添加 JsonPath 存在匹配断言](https://www.toolsqa.com/gallery/SoapUI/5.Add%20JsonPath%20Existence%20Match%20assertion%20for%20Rest%20Services%20in%20SoapUI.png)

1.  其次，点击“添加”按钮后，会弹出“ JSONPath存在匹配配置”对话框，如下图：

![在 SoapUI 中配置 JsonPathExistence 匹配断言](https://www.toolsqa.com/gallery/SoapUI/6.Configure%20JsonPathExistence%20Match%20assertion%20in%20SoapUI.png)

1.  第三，在“ JsonPath Expression ”部分，指定“ books[1].isbn ”，这将检查数组响应的第二个元素中是否存在“ isbn ”键。此外，你期望的结果应该在“预期结果”部分的“布尔值(真/假) ”中。你必须在此部分中看到两个按钮：

-   1.测试按钮：此按钮可帮助你检查你编写的表达式是否通过。此外，如果响应中包含 JSON 路径 (books[1].isbn)，你将看到以下对话框：

![SoapUI 中 JSONPath 存在匹配断言的成功响应](https://www.toolsqa.com/gallery/SoapUI/7.Success%20response%20for%20the%20JSONPath%20Existence%20Match%20assertion%20in%20SoapUI.png)

-   1.  Select from Current：它可以帮助你从 JSON 响应(\True/False\)中设置当前值。单击“从当前按钮选择”后，它将根据当前响应根据键的存在获取布尔值 。

1.  最后，完成此操作后，单击“保存”按钮以保存断言。它应该根据 BookStore API 的当前响应通过。

### SoapUI 中的 JsonPath 匹配断言是什么？

JSONPath Match 断言使用 JSONPath 表达式来检查任何节点是否存在并将其与你期望的值进行比较。

考虑一个场景，我们想要验证上述BookStore API 响应中“ publisher ”字段的值。正如我们从显示 REST 服务响应的代码片段中看到的那样，在books 数组的每个对象中都存在一个“ publisher ”节点。

让我们按照下面提到的步骤使用“ JsonPath Match ”断言来验证相同的步骤：

1.  导航到“添加断言”对话框后，单击“ JsonPath Match ”断言，如下突出显示：

![为 SoapUI 中的 Rest 服务添加 JsonPath 匹配断言](https://www.toolsqa.com/gallery/SoapUI/8.Add%20JsonPath%20Match%20Assertion%20for%20Rest%20Services%20in%20SoapUI.png)

1.  单击“添加”按钮将显示“ JSONPath 匹配配置”对话框，如下所示：

![SoapUI 中的 JsonPath 匹配配置](https://www.toolsqa.com/gallery/SoapUI/9.JsonPath%20Match%20configurations%20in%20SoapUI.png)

在哪里，

1.  你需要指定“ JsonPath Expression ”，在我们的例子中，我们将检查出现在数组中第三个数字上的图书出版商，以便我们将 Json 路径写为“ books[2].publisher ”。
2.  你可以将“预期结果”指定为“ O'Reilly Media ”。
3.  Select from Current：确定上述 JSONPath 正确后，你可以通过单击屏幕截图中标记为“ 3 ”的按钮从响应中选择当前值。因此，我们可以单击此按钮自动填充预期值，而不是在步骤 2 中手动指定值。
4.  单击突出显示为“ 3 ”的“测试”按钮，如果 JSONPath 正确，你应该会看到以下通知。

![JsonPath 成功](https://www.toolsqa.com/gallery/SoapUI/10.JsonPath%20Success.png)

1.  完成配置后，单击“保存”按钮以添加断言。

![断言](https://www.toolsqa.com/gallery/SoapUI/11.Assertion.png)

在我们的例子中，断言将通过，如上所示，因为预期值与响应中的键值匹配。

### SoapUI 中的 JsonPath RegEx 匹配断言是什么？

JsonPath Regex Match断言的工作方式类似于JSONPath Match断言。它帮助我们通过使用正则表达式而不是固定值来验证特定节点的值是否存在。

考虑一个场景，你想要验证 Books Store API 的第一个节点是否包含isbn并且isbn仅包含0-9的数字。

让我们按照下面提到的步骤使用“ JsonPath RegEx Match ”断言来验证相同的步骤：

1.  从“添加断言”对话框中，选择如下突出显示的“ JSONPath RegEx Match ”表达式：

![在 SoapUI 中为 Rest 服务选择 JsonPath RegEx 断言](https://www.toolsqa.com/gallery/SoapUI/12.Selecting%20JsonPath%20RegEx%20Assertion%20for%20Rest%20Services%20in%20SoapUI.png)

1.  单击添加按钮。之后，你将看到显示“ JSONPath RegEx Match Configuration ”的对话框，如下所示：

![SoapUI 中的 JSONPath RegEx 匹配配置](https://www.toolsqa.com/gallery/SoapUI/13.JSONPath%20RegEx%20Match%20Configuration%20in%20SoapUI.png)

1.  这里我们将验证“ books[0].isbn ”节点是否仅包含 ( 0-9 )数字的值。所以将把 RegEx 写成“ ^[0-9]\ $ ”。它验证 JSON 响应中节点的值是否在0-9 数字之间。指定的正则表达式验证以下值：

-   ^ 表示表达式的开始。
-   [0-9] 表示整数范围。
-   \ 表示字符数(在本例中为 n)。
-   $表示表达式结束。

1.  同样，你可以使用“从当前选择”按钮或手动指定“真”值来选择预期结果。
2.  单击“测试”按钮，你将看到此断言通过并给出如下成功消息：

![SoapUI 中 JSONPath RegEx 匹配断言的成功消息](https://www.toolsqa.com/gallery/SoapUI/14.Success%20message%20for%20JSONPath%20RegEx%20Match%20assertion%20in%20SoapUI.png)

1.  单击“确定”按钮，你将再次进入显示断言验证成功的配置对话框。
2.  单击“保存”按钮将断言添加到测试步骤。

因此，这完成了所有 SoapUI 断言的详细信息，我们可以仅将其用于验证 REST Web 服务的响应。

## 关键要点

-   SoapUI 提供了仅适用于 REST 服务的断言类别。
-   JsonPath Count Assertion 验证服务响应中节点的计数
-   JsonPath Existence Match Assertion 验证一个节点是否存在于服务的响应中。
-   此外，JsonPath Match Assertion 用于验证服务响应中节点的准确值。
-   JsonPath RegEx Match Assertion 用于根据正则表达式验证服务响应中节点的值。

让我们转到下一篇文章，我们将详细介绍“ [SoapUI 中的 SOAP 服务断言](https://www.toolsqa.com/soapui/soap-services-assertions-in-soapui/)”。