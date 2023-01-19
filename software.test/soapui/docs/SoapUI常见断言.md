在前两篇文章中，我们介绍了[Test Specific Assertions 的细节，](https://www.toolsqa.com/soapui/assertions-in-soapui/)它仅适用于特定类型的TestCase或被测WebService 。[《SoapUI 测试特定断言：REST 服务》](https://www.toolsqa.com/soapui/soapui-rest-assertions/)一文中提到的所有断言仅适用于REST WebServices ， [《SoapUI 测试特定断言：SOAP 服务》](https://www.toolsqa.com/soapui/soap-services-assertions-in-soapui/)一文中提到的所有断言仅适用于SOAP WebServices。现在，SoapUI 还提供了一些其他断言，它们适用于SOAP和REST服务，被称为SoapUI Common Assertions. 在本文中，我们将通过涵盖以下主题下的详细信息来了解所有这些常见断言的用法：

-   SoapUI 中常见的属性内容断言有哪些？
    -   什么是 SoapUI 中的包含断言？
    -   另外，SoapUI 中的 Not Contains Assertion 是什么？
    -   SoapUI 中的 XPath 断言是什么？
    -   什么是 SoapUI 中的 XQuery 匹配断言？
-   同样，SoapUI 中常见的 Compliance、Status 和 Standard Assertions 有哪些？
    -   SoapUI 提供的 HTTP 状态代码断言是什么？
    -   此外，SoapUI 中的架构合规性断言是什么？
-   SoapUI 中常见的 SLA 断言是什么？

由于所有这些断言都适用于 SOAP 和 REST 服务，我们将使用以下信息来验证所有这些断言：

-   根据[_](https://bookstore.toolsqa.com/BookStore/v1/Books) _ [_](https://bookstore.toolsqa.com/BookStoreService.wsdl) _文章[“SoapUI：使用项目”](https://www.toolsqa.com/soapui/soapui-project/)中提到的详细信息。
-   你可以参考[《SoapUI 测试特定断言：REST 服务》一文中的 REST 项目示例输出，以及](https://www.toolsqa.com/soapui/soapui-rest-assertions/)[《SoapUI 测试特定断言：SOAP 服务》](https://www.toolsqa.com/soapui/soap-services-assertions-in-soapui/)一文中的 SOAP 项目示例输出。

## SoapUI 中常见的属性内容断言有哪些？

我们已经知道，Property Content Assertions验证收到的响应的内容。SoapUI 提供了多种适用于 SOAP 和 REST 请求的内容断言。让我们在下面的部分中看看，我们如何使用这些断言？

### 什么是 SoapUI 中的包含断言？

Contains Assertion搜索属性值中是否存在字符串标记。

考虑一个场景，在BookStore Service中，我们需要检查是否存在作者名为“Richard E. Silverman”的书？让我们按照下面提到的步骤使用“包含”断言来验证相同的步骤：

1.  首先，导航到“添加断言”对话框。之后，点击“Property Content”断言类别下的“ Contains ”断言，如下高亮显示：

![如何在 SoapUI 中为 SOAP 和 REST 添加 Contains AssertionCommon](https://www.toolsqa.com/gallery/SoapUI/1.How%20to%20Add%20Contains%20AssertionCommon%20for%20both%20SOAP%20and%20REST%20In%20SoapUI.jpg)

1.  其次，点击“添加”按钮，弹出“包含断言”对话框，如下图：

![包含 SoapUI 中的断言对话框](https://www.toolsqa.com/gallery/SoapUI/2.Contains%20Assertion%20Dialogue%20box%20in%20SoapUI.png)

1.  第三，在“内容”部分输入作者姓名“Richard E. Silverman” 。它是你要在 WebService 的响应中验证的搜索字符串。
2.  第四，在上面的对话框中，你还会看到两个复选框。他们是：

-   忽略大小写：如果你选择忽略大小写复选框，它将通过忽略大小写来验证字符串。假设你 在内容文本框中键入“richARD E. SilVerMan”并选中“比较时忽略大小写”复选框。此外，它会忽略大小写，只会检查输入的字符串的值。

![IgnoreCase 包含 SoapUI 中的断言](https://www.toolsqa.com/gallery/SoapUI/3.IgnoreCase%20Contains%20Assertion%20in%20SoapUI.png)

因此，断言将通过，因为如果我们忽略大小写，字符串中的字符是相同的。

-   Regular Expression：如果你想根据 RegEx 验证输出，你可以选中此复选框，然后在 Content 部分指定你的正则表达式。SoapUI 遵循[Oracle 指定的正则表达式模式](https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)例如，如果我们要验证图书服务响应中是否包含“O'Reilly”，则可以指定“ \.O'Reilly\ ”。作为 Content 部分中的 RegEx，如下所示：

![SoapUI 中包含断言的正则表达式](https://www.toolsqa.com/gallery/SoapUI/4.Regular%20Expression%20in%20Contain%20Assertion%20in%20SoapUI.png)

因此，我们可以使用Contains 断言来验证 WebService 响应中字符串的存在，我们可以将其用于SOAP和REST Web 服务。

### 什么是 SoapUI 中不包含断言？

与 Contains 断言相反，Not Contains断言搜索属性值中不存在的字符串。此外，如果响应不包含指定值，则此断言将通过。

让我们假设；我们想验证书店服务的响应中没有标题为“Groovy Book”的书，我们可以使用此断言来验证 BookStore API 的响应。

让我们按照下面提到的步骤使用“不包含”断言来验证相同的步骤：

1.  首先，导航到“添加断言”对话框并单击“属性内容”断言类别下的“不包含”断言，如下突出显示：

![如何在 SoapUI 中为 SOAP 和 REST 添加不包含 assertionCommon](https://www.toolsqa.com/gallery/SoapUI/5.How%20to%20add%20not%20contains%20assertionCommon%20for%20both%20SOAP%20and%20REST%20in%20SoapUI.jpg)

1.  其次，点击“添加”按钮，弹出“NotContains Assertion”对话框，如下图：
2.  第三，在“内容”部分输入字符串“Groo Book”的名称。你要验证的搜索字符串不存在于 Web 服务的响应中。

注意：这里的两个复选框(忽略大小写和正则表达式)的工作方式与 Contains 断言相同。

1.  第四，单击“确定”按钮将对 WebService 响应执行断言并显示结果，如下所示：

![SoapUI 中不包含断言配置](https://www.toolsqa.com/gallery/SoapUI/6.Not%20Contains%20Assertion%20Configuration%20in%20SoapUI.png)

1.  因此，只要目标服务的响应中不存在该字符串，断言就会通过。

### SoapUI 中的 XPath 匹配断言是什么？

XPath Match断言允许你使用XPath表达式从目标响应的特定节点中选择内容并将其与你期望的值进行比较。

假设你要验证 BookStore API 响应中出现的第一本书的 ISBN 不为空。我们可以使用 XPath 断言快速验证相同的内容。

让我们按照下面提到的步骤使用“XPath Match”断言来验证相同的步骤：

1.  首先，导航到添加断言对话框。之后，单击“Property Content”断言类别下的“XPath Match”断言，如下高亮显示：

![如何在 SoapUI 中为 SOAP 和 REST 添加 XPath Match AssertionCommon](https://www.toolsqa.com/gallery/SoapUI/7.How%20to%20add%20XPath%20Match%20AssertionCommon%20for%20both%20SOAP%20and%20REST%20in%20SoapUI.png)

1.  其次，点击“添加”按钮，弹出“Xpath匹配配置”对话框，如下图：

![Xpath Match Assertion ConfigurationsCommon for both both both SOAP and REST in SoapUI](https://www.toolsqa.com/gallery/SoapUI/8.Xpath%20Match%20Assertion%20ConfigurationsCommon%20for%20both%20SOAP%20and%20REST%20in%20SoapUI.png)

在哪里，



-   声明：单击此按钮会自动获取并填充命名空间。如果你对命名空间的详细信息有信心，可以手动输入，否则只需单击“声明”按钮，它将填充命名空间详细信息，如下所示：
    -   SOAP服务的名称空间将被填充，如下所示：

![在 SoapUI 中的 Xpath 匹配断言中为 SOAP 服务声明命名空间](https://www.toolsqa.com/gallery/SoapUI/9.Declaring%20Namespace%20for%20SOAP%20service%20in%20Xpath%20match%20assertion%20in%20SoapUI.png)

-   同样，将填充REST服务的命名空间，如下所示：

![在 SoapUI 中的 Xpath 匹配断言中为 REST 服务声明命名空间](https://www.toolsqa.com/gallery/SoapUI/10.Declaring%20Namespace%20for%20REST%20service%20in%20Xpath%20match%20assertion%20in%20SoapUI.png)

声明命名空间后，我们可以在XPath Expression部分指定所需节点的XPATH 。

-   你可以为 SOAP 服务指定示例XPATH “//ns1:BooksResult[1]/ns1:Books[1]/ns1:CustomBookModel[1]/ns1:Isbn”，如下所示：

![在 SoapUI 中为 SOAP 服务指定 XPath](https://www.toolsqa.com/gallery/SoapUI/11.Specifying%20XPath%20for%20a%20SOAP%20service%20in%20SoapUI.png)

同样，你可以为 REST 服务指定示例XPATH “//ns1:books//ns1:e[1]/ns1:isbn”，如下所示：

![在 SoapUI 中为 REST 服务指定 XPath](https://www.toolsqa.com/gallery/SoapUI/12.Specifying%20XPath%20for%20a%20REST%20service%20in%20SoapUI.png)

请注意在这两种情况下命名空间 ns1 的用法。为了访问节点，必须使用名称空间。

-   Select from current : 通过点击这个按钮，它会自动选择上面Expected Result部分提到的节点的值，在上面的例子中是isbn。此外，它会将节点isbn的值选择为“9781449325862”。如果你不想通过单击此按钮自动选择值，你甚至可以手动输入预期结果。
-   测试：通过单击“测试”按钮，你可以测试输出，如果断言通过，它将显示以下成功警报：

![SoapUI 中的 Xpath 匹配测试成功消息警报](https://www.toolsqa.com/gallery/SoapUI/13.Xpath%20Match%20Test%20Success%20message%20alert%20in%20SoapUI.png)

或者如果断言失败，它会抛出 Error 并显示一个对话框，如下所示：

![XpathMatch 测试错误](https://www.toolsqa.com/gallery/SoapUI/14.XpathMatch%20test%20Error.png)

-   可选复选框：预期结果部分还提供了一些复选框，这在验证断言方面提供了额外的帮助。让我们了解这些复选框的详细信息：
-   允许通配符复选框：如果我们要验证的值在目标响应中动态更改，那么我们可以使用通配符来验证响应。假设，如果我们认为第 1 本书的isbn总是以“862”结尾，那么我们可以使用 WildCard  “ \862”，\这将验证 isbn 总是以“862”结尾。它将出现在“预期结果”部分中，如下所示：

![在 SoapUI 中允许通配符 XPath 匹配](https://www.toolsqa.com/gallery/SoapUI/15.Allow%20Wildcard%20XPath%20match%20in%20SoapUI.png)

-   忽略命名空间前缀复选框：如果我们想在验证响应时忽略命名空间前缀，我们可以通过选中此复选框来实现。
-   Ignore XML Comments Checkbox：如果我们想忽略响应中的注释，我们可以通过选中这个复选框来实现。
-   浏览完上述所有选项后，单击“保存”按钮以保存断言。它应作为断言通过，并将获得预期的 Isbn 作为“9781449325862”。因此，它将显示示例输出如下：

![SoapUI 中 SOAP 和 REST 输出视图的 Xpath Match AssertionCommon](https://www.toolsqa.com/gallery/SoapUI/16.Xpath%20Match%20AssertionCommon%20for%20both%20SOAP%20and%20RESToutput%20View%20in%20SoapUI.png)

### 什么是 SoapUI 中的 XQuery 匹配断言？

XQuery 匹配与XPath断言非常相似，唯一的区别是它使用[XQuery](https://en.wikipedia.org/wiki/XQuery)表达式并将其与你期望的结果进行比较。

考虑一个场景，假设你想要验证响应中可用书籍的所有“标题”，而不管它们存在的顺序如何。你可以编写一个简单的 XQuery，然后就可以开始了。

让我们按照下面提到的步骤使用“XQuery Match”断言来验证相同的步骤：

1.  首先，导航到“添加断言”对话框。之后，单击“Property Content”断言类别下的“XQuery Match”断言，如下高亮显示：

![如何在 SoapUI 中为 SOAP 和 REST 添加 XQuery Match AssertionCommon](https://www.toolsqa.com/gallery/SoapUI/17.How%20to%20add%20XQuery%20Match%20AssertionCommon%20for%20both%20SOAP%20and%20REST%20in%20SoapUI.jpg)

1.  其次，点击“添加”按钮，弹出“XQuery匹配配置”对话框，如下图：

![SoapUI 中 SOAP 和 REST 通用的 Xquery 匹配断言配置](https://www.toolsqa.com/gallery/SoapUI/18.Xquery%20Match%20Assertion%20Configuration%20Common%20for%20both%20SOAP%20and%20REST%20in%20SoapUI.png)

在哪里，

-   XQuery 表达式：这是你需要指定的 XQuery 表达式，用于从 WebService 的响应中提取值。例如，要获取所有书籍的标题，我们可以指定 XQuery 表达式如下：

```java
<Result>
{
for$z in //:CustomBookModel
return (<Title>{data($z/:Title)}</Title>)
}
</Result>
```

在上面的代码片段中：

-   `<Result>`是将存储基于 XQuery 响应的结果的标记。此外，它可以是基于用户选择的任何标签。
-   此外，我们使用for循环迭代响应，$z是任何变量，它将从  `<CustomBookModel>`.
-   //。  指定CustomBookModel 的根，你还可以使用命名空间进行导航，然后指定命名空间而不是 "//。 "。
-   除了上述之外，“return”函数将返回`<Title>`标签的值。
-   此外，“数据”函数返回标签中每个标题标签的数据`<CustomBookModel>`。

2) Select from current：单击按钮以根据提到的XQuery 从响应中选择当前值。

3) 预期结果： 点击“Select from current”按钮后，会返回服务响应中的所有标题。你也可以手动指定预期结果。

4)点击“测试”按钮会显示成功响应，如下图：

![SoapUI 中成功的 Xquery 匹配断言](https://www.toolsqa.com/gallery/SoapUI/19.Succesfull%20Xquery%20match%20Assertion%20in%20SoapUI.png)

1.  单击“确定”按钮将显示断言的有效执行。

## SoapUI 中常见的合规性、状态和标准断言是什么？

与 SoapUI 在“属性内容”部分下提供的常见断言一样，它还在“合规性、状态和标准”断言类别下提供了一些断言，这些断言对于SOAP和REST Web服务都是通用的。让我们在以下部分中了解所有这些断言的详细信息：

### SoapUI 提供的 HTTP 状态代码断言是什么？

众所周知，HTTP Status Codes表示HTTP请求是否完成。所有这些响应代码通常分为以下几类：

|      |                                                              |
| ---- | ------------------------------------------------------------ |
| 1    | 1xx：信息：这些状态代码表示请求的接收及其处理。        |
| 2    | 2xx：成功：这些状态代码表示操作已成功接收、处理和接受。 |
| 3    | 3xx：重定向：这些状态代码表示完成请求需要进一步的操作。 |
| 4    | 4xx: Client Error:这些状态码表示请求包含不正确的语法，或者我们的请求有问题。 |
| 5    | 5xx: Server Error：这些状态代码表示存在服务器端错误，这意味着服务器未能满足有效请求。 |

为了验证这些状态代码，SoapUI 提供了几个断言。让我们在以下部分中了解这些断言的详细信息：

#### 什么是 SoapUI 中的有效 HTTP 状态代码断言？

此断言验证 Web 服务返回的 HTTP 响应代码位于预期 HTTP代码列表中。

考虑一个场景，对于 BookStore API，我们想要验证返回的响应代码始终是 200 或 201。我们可以使用“有效的 HTTP 状态代码”断言来验证相同的情况。

让我们按照下面提到的步骤使用“有效的 HTTP 状态代码”断言来验证相同的步骤：

1.  导航到添加断言对话框并单击“合规性、状态和标准”断言类别下的“有效 HTTP 状态代码”断言，如下突出显示：

![如何在 SoapUI 中为 SOAP 和 REST 添加有效的 HTTP 状态代码 assertionCommon](https://www.toolsqa.com/gallery/SoapUI/20.How%20to%20add%20Valid%20HTTP%20status%20code%20assertionCommon%20for%20both%20SOAP%20and%20REST%20in%20SoapUI.png)

1.  点击“添加”按钮，弹出“有效HTTP状态码断言配置”对话框，如下图：

![SoapUI 中 SOAP 和 REST 的有效 HTTP 状态代码断言配置通用](https://www.toolsqa.com/gallery/SoapUI/21.Valid%20HTTP%20Status%20code%20Assertion%20configurationsCommon%20for%20both%20SOAP%20and%20REST%20in%20SoapUI.png)

1.  在“指定代码”部分，输入值200、201。你可以指定单个代码或以逗号分隔的代码列表。单击“确定”按钮将根据服务的最后响应验证 HTTP 状态代码。

#### 什么是 SoapUI 中的无效 HTTP 状态代码断言？

与“有效 HTTP 状态代码”断言相反，“无效 HTTP 状态代码”断言验证“预期代码”列表不包含 Web 服务返回的 HTTP 状态代码。

考虑一个场景，对于 BookStore API，我们想要验证返回的响应代码不是 401。我们可以使用“无效的 HTTP 状态代码”断言来验证它。

让我们按照下面提到的步骤使用“无效的 HTTP 状态代码”断言来验证相同的步骤：

1.  导航到添加断言对话框并单击“合规性、状态和标准”断言类别下的“无效 HTTP 状态代码”断言，如下突出显示：

![如何在 SoapUI 中为 SOAP 和 REST 指定无效的 HTTP 状态代码 assertionCommon](https://www.toolsqa.com/gallery/SoapUI/22.How%20to%20specify%20Invalid%20HTTP%20status%20code%20assertionCommon%20for%20both%20SOAP%20and%20REST%20in%20SoapUI.png)

1.  点击“添加”按钮，弹出“Invalid HTTP status codes Assertion Configuration”对话框，如下图：

![无效的 HTTP 状态代码断言配置对于 SoapUI 中的 SOAP 和 REST 都是通用的](https://www.toolsqa.com/gallery/SoapUI/23.Invalid%20HTTP%20Status%20code%20Assertion%20configurations%20Common%20for%20both%20SOAP%20and%20REST%20in%20SoapUI.png)

1.  在“指定代码”部分，输入值 401。你可以指定单个代码或逗号分隔代码列表。单击“确定”按钮将根据服务的最后响应验证不存在HTTP 状态代码。

注意：我们可以将这些断言应用于 SOAP 和 REST 端点。

### SoapUI 中的模式合规性断言是什么？

除了状态代码，在 SoapUI 中，我们还可以根据被测目标服务的WSDL ( SOAP ) 或WADL ( REST ) 中的定义来验证响应消息。

考虑一个场景，我们想要快速检查我们获得的 SOAP 响应是否符合WSDL？

让我们按照下面提到的步骤使用“Schema Compliance”断言来验证相同的步骤：

1.  导航到添加断言 对话框并单击“合规性、状态和标准”断言类别下的“架构合规性”断言，如下突出显示：

![如何在 SoapUI 中为 SOAP 和 REST 添加 Schema Compliance AssertionCommon](https://www.toolsqa.com/gallery/SoapUI/24..How%20to%20Add%20Schema%20Compliance%20AssertionCommon%20for%20both%20SOAP%20and%20REST%20in%20SoapUI.jpg)

1.  点击“添加”按钮，弹出“Schema Compliance Assertion Configuration”对话框，如下图：
2.  在 Configuration 对话框中，它会自动填充创建项目的WSDL，但如果你想指定其他一些WSDL，你也可以更新它。单击“确定”继续。

![SoapU 中的架构合规性断言响应视图](https://www.toolsqa.com/gallery/SoapUI/25.Schema%20Compliance%20Assertions%20response%20view%20in%20SoapUI.png)

1.  如果响应符合上述模式，你将看到成功响应，如上所示。只要最后一个响应不符合WSDL模式，你就会看到错误。

注意：同样，你可以根据指定的 WADL 验证 REST 服务的响应。

## SoapUI 中常见的 SLA 断言是什么？

众所周知，服务水平协议(Service Level Agreement ， SLA)一般指的是服务提供商与客户之间的协议。协议可以根据可用性、质量、响应时间等各种约定特征进行分类。SoapUI 提供验证特定服务响应时间的功能。

考虑一个假设场景，BookStore WebService 的商定响应时间少于 4 秒。我们可以使用“SLA”断言类别在 SoapUI 中验证相同的内容。

让我们按照下面提到的步骤使用“响应 SLA”断言来验证相同的步骤：

1.  导航到添加断言 对话框并单击“SLA”断言类别下的“响应 SLA”断言，如下突出显示：

![如何在 SoapUI 中为 SOAP 和 REST 添加 Response SLA assertionCommon](https://www.toolsqa.com/gallery/SoapUI/26.,How%20to%20add%20Response%20SLA%20assertionCommon%20for%20both%20SOAP%20and%20REST%20in%20SoapUI.png)

1.  点击“添加”按钮，弹出“响应SLA断言配置”对话框，如下图：

![在 SoapUI_0 中为 SOAP 和 REST 配置 SLA assertionCommon](https://www.toolsqa.com/gallery/SoapUI/27.Configure%20SLA%20assertionCommon%20for%20both%20SOAP%20and%20REST%20in%20SoapUI_0.png)

1.  指定服务期望返回响应的最长时间(以毫秒为单位) 。对于我们的场景，我们指定了4000 毫秒。单击确定按钮添加断言。它将显示测试结果，如下所示：

![SoapUI 中的 SLA 断言响应视图](https://www.toolsqa.com/gallery/SoapUI/28.SLA%20Assertion%20response%20view%20in%20SoapUI.png)

1.  正如我们在上面的屏幕截图中看到的那样，由于服务的响应时间为 1374 毫秒，因此它符合上述 4000 毫秒的 SLA，并导致断言成功。

## 关键要点

-   SoapUI 提供了广泛的断言，可以应用于 SOAP 和 REST Web 服务。
-   此外，很少有常见的断言是包含、不包含、Xpath 和 XQuery 匹配，它们用于验证 Web 服务内容的响应。
-   此外，SoapUI 提供的另一组通用断言用于验证 HTTP 状态代码和 Web 服务响应的模式。
-   此外，SoapUI 还提供了一个 SLA 断言，可用于验证 SOAP 和 REST 服务的响应时间。

让我们转到下一篇文章，我们将进一步深入了解如何在 SoapUI 中使用[“脚本断言”实现一些高级断言。](https://www.toolsqa.com/soapui/script-assertions-in-soapui/)