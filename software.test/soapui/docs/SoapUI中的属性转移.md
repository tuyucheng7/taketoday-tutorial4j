每当我们测试应用程序的API时，都需要从一个 API 的响应中提取值。并在对另一个 API 的请求中传递这些值。为了处理这种情况，我们需要一种机制，使用它我们可以提取指定键的值并在其他一些 API 请求中传递相同的值。我们在上一篇文章中了解到，我们可以使用“ [SoapUI 中的属性](https://www.toolsqa.com/soapui/properties-in-soapui/)”来设置和提取键的特定值。此外，SoapUI 提供了在各种 TestStep 之间传输这些属性的功能。在本文中，我们将介绍以下详细信息以了解“ SoapUI 中的属性传输”：

-   SoapUI 中的属性转移是什么？
    -   如何在 SoapUI 中添加属性转移？
    -   如何在 SoapUI 中使用 Property Transfer？

## SOAPUI中的财产转移是什么？

正如我们所知，SoapUI 中的 Properties充当中央存储库，用于存储所需信息并使用命名密钥进行访问。现在，你想到的下一个问题是，什么是财产转让？

让我们借助[Bookstore API](https://www.toolsqa.com/soapui/properties-in-soapui/)的响应了解这一点。

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

假设我们想将上述响应中的“ ISBN ”字段值用作测试步骤的一部分，并将其传输到项目的其他元素，这些元素可以是属性或不同的请求。

在这种情况下，我们需要有一种机制来检索值并将其设置为另一个元素。SoapUI 通过Property Transfer TestStep支持此类功能。

让我们了解如何在 SoapUI的TestStep中添加属性传输：

### 如何在 SoapUI 中添加属性转移？

Property Transfer可以作为测试步骤添加到 SoapUI 中，SOAP UI 为 Property Transfer 提供了一个直观的向导。

按照以下步骤将 Property Transfer添加为测试步骤：

第 1 步- 选择 TestStep，右键单击 → Insert Step → Property Transfer。

![在 SoapUI 中添加属性传输](https://www.toolsqa.com/gallery/SoapUI/1.Adding%20property%20Transfer%20in%20SoapUI.png)

第 2 步- 你将看到“添加步骤”弹出窗口，输入测试步骤的逻辑名称，然后单击“确定”。

![在 SoapUI 中为 Step Property transfer 添加名称](https://www.toolsqa.com/gallery/SoapUI/2.Add%20name%20for%20Step%20Property%20transfer%20in%20SoapUI.png)

如上所示，我们将 TestStep 的名称用作“ ISBN Transfer ”。

第 3 步- 添加步骤后，将打开一个新向导。你可以从标题/正文中添加多个属性并使用它们

更远。

![SoapUI 属性转移向导](https://www.toolsqa.com/gallery/SoapUI/3.SoapUI%20Property%20Transfer%20Wizard.jpg)

由于我们要使用书店API响应中的“ ISBN ”，因此请单击突出显示的“ + ”按钮。

第 4 步- 按上述步骤指定的方式单击“ + ”后，系统会提示你输入要传输的值的名称。

![SoapUI 中的值传递](https://www.toolsqa.com/gallery/SoapUI/4.Value%20Transfer%20in%20SoapUI.png)

添加传输的逻辑名称，然后单击“确定” 。

![SoapUI 中的属性转移](https://www.toolsqa.com/gallery/SoapUI/5.Property%20Transfer%20In%20SoapUI.jpg)

如上面的代码片段所示，向列表中添加了一个新的Transfer。

### 如何在 SoapUI 中使用 Property Transfer？

由于我们添加了Property Transfer，让我们看看如何使用这些值。添加Property Transfer后，你将看到有两个区域Source和Target标记为步骤1 和 2。让我们详细了解这些。

![传输向导 SoapUI](https://www.toolsqa.com/gallery/SoapUI/6.Transfer%20Wizard%20SoapUI.jpg)

在此之前，还指定了另外两个值来标识属性，即“ Property ”和“ Path Language ”，如上面代码片段中的步骤 A 和步骤 B所示。让我们了解所有这些部分：

1.  来源：此区域记录从中提取属性值的属性的来源。它显示了我们已经定义属性的项目的所有可能值。其中很少有：

![SoapUI：属性传输源](https://www.toolsqa.com/gallery/SoapUI/7.SoapUI%20Property%20Transfer%20Sources.jpg)

在快照中，你会看到Globals、Project、Test Suite1等。选择Global将能够选择我们可能已经定义的全局属性。在下面的快照中，我们选择了Globals 作为来源。

![SoapUI - 属性转移向导全球资源](https://www.toolsqa.com/gallery/SoapUI/8.SoapUI%20-%20Property%20Transfer%20Wizard%20Global%20Source.jpg)

SOAP UI将自动填充所有已定义的全局属性，并方便用户选择其中任何一个作为源。有关如何设置不同级别的属性(包括Global 属性)的详细信息，请阅读上一篇文章“在 SoapUI 中使用属性”。

同样，如果你选择其他级别(项目、测试套件、测试用例、测试步骤)，SoapUI 将显示相应的已经定义的属性，以便它们可以用于存储值。

1.  目标： 该区域方便用户接收从源中提取的值。它还包含源中可用的所有不同属性。

如上所述，还指定了另外两个值来标识属性值，即“ Property ”和“ Path Language ”，由上面代码段中的步骤 A 和步骤 B指示。

-   属性：属性参数显示来自所选源的属性。
-   路径语言：如果我们想根据请求/响应的格式(可以是JSON 或 XML )从请求、响应或端点中提取属性值，我们选择路径语言。

对于我们想要从JSON响应中提取ISBN值的示例，我们将设置路径语言并在下面的详细步骤中查看。

第 1 步：首先，选择来源、响应和路径语言以提取 ISBN。

1.  由于我们要从目标响应中提取值，因此在Property 下拉列表中选择“ Response ”。
2.  从路径语言下拉列表中选择JSONPath ，因为我们要从[REST 请求](https://bookstore.toolsqa.com/BookStore/v1/Books)的响应中提取 ISBN 。

现在，你可能想知道如果你需要标题中的任何内容怎么办？坚持，稍等！ 在这种情况下， SoapUI也可以为你提供帮助。除了Headers 和 API response之外，你还可以从Property Dropdown中选择各种其他参数， 如下所示：

![属性下拉](https://www.toolsqa.com/gallery/SoapUI/9.Property%20Dropdown.png)

第 2 步- 其次，要获取源 JSON的声明，请指定 JSON 路径。在我们的例子中，让我们使用$.books[0].isbn从[Book Store API](https://bookstore.toolsqa.com/BookStore/v1/Books)获取 ISBN 编号。我们将使用此 ISBN 号来检索与此 ISBN 号相关的特定书籍的记录。

注意：前导 $ 表示根对象或数组，可以省略。

第 3 步- 第三，指定从上述JSON路径表达式中提取的值 ( ISBN ) 将传输的目标。为此，我们使用属性传输窗口底部的目标窗格。

让我们定义一个新属性，如下所示，我们将使用它来设置在步骤 2 中提取的 ISBN 的值。

![在 SoapUI 中设置属性](https://www.toolsqa.com/gallery/SoapUI/10.Setting%20Up%20Property%20in%20SoapUI.jpg)

现在导航到Property Transfer向导并从Target中选择getISBN属性，如下所示：

![在 SoapUI 的属性转移向导中设置目标值](https://www.toolsqa.com/gallery/SoapUI/11.Setting%20Target%20Value%20in%20Property%20Transfer%20Wizard%20of%20SoapUI.jpg)

在哪里，

-   Target as Properties(由步骤 1 指示)表明我们要将值设置为属性之一。
-   Property 作为属性的名称(在第 2 步中表示)，这是我们获取的值。

第 4 步- 最后，单击“运行”图标(在下面的屏幕截图中突出显示为第 1 步)。现在，一旦测试用例成功运行，属性“ ISBN ”就会根据API的响应进行更新。

![在 SoapUI 中运行传输向导](https://www.toolsqa.com/gallery/SoapUI/12.Run%20Transfer%20Wizard%20In%20SoapUI.jpg)

此日志显示(由标记 2 突出显示)传输的详细信息及其状态。在我们的例子中，我们成功地转移了财产，转移的价值在上面的截图中突出显示。

因此，我们可以将从一个 REST API 的 Response Header/Body 中提取的属性值传输到另一个 API 或 TestCase，并实现“ Property Transfer in SoapUI ”。此外，我们还可以按照相同的步骤在 SoapUI中为SOAP 项目实现属性传输。

## 要点：

-   Property Transfers 有助于在各种 TestSteps 或 TestCases 之间传输属性值。
-   此外，我们可以访问所有级别的属性(例如，Global、TestSuite 等)获取和传输 Properties 的值。
-   此外，我们可以使用“传输向导”快速传输 SoapUI 中的属性。我们可以通过选择属性的 Source 和 Targets 来完成。

因此，现在让我们转到下一篇文章，我们将在其中了解“ SoapUI 中的条件语句”的实现。