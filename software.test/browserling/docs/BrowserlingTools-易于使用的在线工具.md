作为 Web 开发人员和测试人员，我们经常需要快速完成工作。我们需要美化 CSS 或 JavaScript 代码、在数据格式之间进行转换、查找数据的哈希摘要(例如 MD5 或 SHA1)、进行日期计算等等。此外，我们不能花很多时间来做这件事，因为分秒必争，所以我们经常在互联网上搜索快速解决方案。如果你查看搜索历史记录，你会发现诸如“将 XML 转换为 JSON”或“将秒数转换为人类时间”或“计算字符串的 md5”之类的查询。你很可能每天重复这些搜索几次。通常，你最终会访问缓慢的网站、充满广告或运行不佳。

Browserling 是一种[跨浏览器测试](https://www.toolsqa.com/browserling/cross-browser-testing-with-browserling/)服务，我们已经在我们的网站上进行了审查，它决定改变这一切，并为所有开发人员的生活情况创建有用的实用程序集合。因此，我们称它们为[Browserling 开发人员工具](https://www.browserling.com/tools)，并且有 400 多种此类工具。所有工具都有相同的用户界面，它们的工作方式也相同——按下一个按钮，得到结果。如果你正在进行跨浏览器测试，这些工具会特别有用，因为它们包含有关如何在各种浏览器测试场景中使用它们的示例和说明。随后，在这篇文章中，我们将介绍Browserling 开发人员工具的使用，并展示它们对测试人员的重要性。

-   什么是浏览器工具？
-   面向 Web 开发人员的浏览器工具。
    -   CSS 美化。
    -   编码网址。
    -   图像转换工具 - Browserling。
    -   Javascript 验证器。
    -   时间转换工具。

## 什么是浏览器工具？

在Browserling 工具中，你可以找到一组基于浏览器的实用程序，它们可以实现广泛的目标。此外，我们可以使用它们为表单生成数据，以在各种格式(JSON、XML、CSV、TSV、YAML 等)之间转换数据，以检查数据有效性以简化编写浏览器测试。[浏览器](https://www.browserling.com/)通过为所有工具提供单一的一站式解决方案，满足数以千计的 Web 开发人员的需求并简化他们的工作。目前，Browserling 已经托管了 400 多个工具，并且每周都会添加更多工具。Browserling 托管的所有工具都可以在线和在你的浏览器中使用，因为它们是用 JavaScript 编写的。你只需要良好且快速的 Internet 连接即可使用它们。此外，所有工具均可免费使用，也无需广告或下载。

随后，我们可以将 Browserling 工具分为 12 类：

![浏览工具面板](https://toolsqa.com/gallery/browserling/1.browserling%20tool%20panel.png)

有用于执行 web 开发相关操作的web 工具，然后是用于在各种二进制和非二进制数据格式和编码(例如 UTF8、UTF16、Unicode 等)之间进行转换的转换工具，用于处理加密数据的加密工具，哈希用于计算数据加密散列的工具，用于生成各种密码的密码工具，用于处理文本的文本工具，用于生成随机数据的随机化工具，用于快速编辑图像和在各种图像格式之间转换的图像工具，时间和日期工具 用于进行时间和日期计算以及在时间格式之间进行转换，用于进行数学运算和生成数字的数学工具，包括查找 IP、浏览器信息在内的其他工具，以及合并所有类别并在单个列表中显示所有工具的所有工具。

随后，在下一节中，我们将探讨一些 Browserling 工具以及它们如何在 Web 开发和跨浏览器测试中发挥作用。

## Web 开发人员的浏览器工具

在本节中，我们将了解各种工具，了解它们如何帮助 Web 开发人员、QA 团队和测试人员在日常任务中节省大量时间。让我们来看看每个类别中的一个工具。

### CSS美化

现代网站现在的 CSS 需要数千行代码。为了使它们加载速度更快，CSS 被缩小和压缩。在浏览 Internet 并寻找完美的用户界面时，我们经常注意到其他网站上的一些 UI 元素，这些元素激发我们通过将它们与我们的想法混合来重塑它们。我们经常想知道如何在某些网站上设置特定元素的样式，并且我们希望从其他 Web 开发人员的工作中获得灵感。但是当你浏览他们的 CSS 文件时，它看起来像这样：

![原始CSS](https://toolsqa.com/gallery/browserling/2.CSS%20raw_0.png)

很难理解这个特定文件中的 CSS 代码。它提升了对可以美化 CSS 并提供常规缩进代码输出的工具的需求。

Browserling 提供的 CSS 美化工具要求用户粘贴他们想要美化的 CSS，然后按下按钮，美化后的 CSS 就会生成。为此，请访问 Browserling 上的 CSS 美化工具并将 CSS 粘贴到文本字段中。

![CSS 美化输入](https://toolsqa.com/gallery/browserling/3.css%20prettify%20input.png)

按下CSS Prettify按钮后，可以观察 Browserling 工具如何美化凌乱的 CSS：

![浏览器工具 - CSS 美化输出](https://toolsqa.com/gallery/browserling/4.Browserling%20Tools%20CSS%20Prettify%20Output_0.png)

网页CSS样式表中使用的属性和选择器现在已经清晰易懂，你可以复制到他们的项目中并根据需要进行调整。

### 编码网址

对 URL 进行编码对于测试人员尤其是参与跨浏览器测试的测试人员来说是一个有用的工具。当 URL 出现在代码中或需要作为输入时，它们会在代码中造成很多麻烦。例如，URL 中的空格可被视为无效且无效。同样，特殊字符也会被浏览器误读和误解。此类字符修复和替换可能需要测试人员进行额外的工作并浪费时间。

为了避免这种情况，我们使用在线URL 编码器工具。Browserling 的 URL 编码器对 URL 进行编码，并将保留字符替换为有意义的代码(例如 %20 表示空格)。使用编码后的 URL 可以顺利通过跨浏览器测试而不会产生任何问题。要对 URL 进行编码，请访问 Browserling 的[URL 编码器](https://www.browserling.com/tools/url-encode)并在框中输入一个 URL，如下所示：

![URL 编码器输入](https://toolsqa.com/gallery/browserling/5.URL%20Encoder%20Input_0.png)

按URL 编码按钮获取编码后的 URL：

![在线浏览器工具 URL 编码器](https://toolsqa.com/gallery/browserling/6.Browserling%20Tools%20URL%20Encoder%20Online_0.png)

你还可以按“复制到剪贴板”按钮以在测试和测试过程中快速使用它。

### 图像转换工具 - Browserling

图像转换是一种非常流行的操作，我们这样做的原因有很多。比如我曾经在做一个博客，要求所有的博客图片都必须转为JPG格式。其背后的主要原因是 JPG 在压缩方面更好，因此使博客加载速度更快。此外，有时你可能会发现自己填写的表单只接受上传的 PNG 图像。图像压缩还有更多用例。为了帮助人们更快地完成工作，Browserling 提供了一系列用于图像转换的工具。

在这篇文章中，我们将演示JPG 到 PNG 的转换。单击浏览器工具中的图像工具类别，然后选择将 JPG 转换为 PNG工具。

请通过文件资源管理器上传或使用拖放来选择图像。

![选择图像对话框](https://toolsqa.com/gallery/browserling/7.Choose%20Image%20Dialog%20Box_0.png)

单击转换为 PNG按钮，图像将通过快速下载对话框类似地保存在你的系统上。尽管此示例演示了 JPG 到 PNG 的转换，但 Browserling 为其他图像文件格式(例如 BMP、Webp、GIF、ICO 等)提供了更多选择。

### Javascript 验证器

Web 开发人员的另一个重要工具是Javascript 验证器。作为 Web 开发人员，你的 Javascript 代码通常会有微小的错误，并且随着 Javascript 解析和运行(而不是编译)，浏览器无论如何都会运行代码。如果你遇到了一个解析错误并且你绞尽脑汁想找出错误几个小时，那么通过使用 Browserling 的 Javascript 验证器，你可以在几秒钟内找到 JS 代码错误，因为它会指出代码中的错误。

要使用已检查的 Javascript 代码，请访问 Javascript 验证器页面并输入 JS 代码，如下所示：

![Javascript Validator- 验证 Javascript 在线浏览器工具](https://toolsqa.com/gallery/browserling/8.Javascript%20Validator%20Validate%20Javascript%20online%20Browserling%20Tools_0.png)

请注意，alert 函数中的字符串不以逗号结尾。因此它是一个开放字符串，在函数执行期间可能会引发错误。按JS Validate按钮来验证 JS 代码：

![Javascript 验证器在线输出](https://toolsqa.com/gallery/browserling/9.Javascript%20validator%20online%20output_0.png)

如果验证不成功，将出现该错误。对于成功的验证，“一切都很好！” 将显示为成功消息。

### 时间转换工具

浏览器工具也有一个时间和日期工具类别。你将在此类别中找到所有可能用于处理日历日期和时钟时间的工具。例如，在后端数据库中，时间通常存储为 UNIX 时间戳。因为它是一个整数，所以很难说出它包含什么值。通常，在编写跨浏览器测试时，你需要使用 UNIX 时间戳，因为它们在所有系统上都是一致的(没有格式歧义)。因此 Browserling 的时间转换工具将帮助你将时间从一种单位(如 UNIX)转换为另一种单位(如 UTC)。下面演示这个例子。

要转换时间，请访问 Browserling 的时间转换工具部分并选择你要执行的转换。在这里，我们将 UNIX 时间转换为 UTC。

![UNIX Time 时间转换工具](https://toolsqa.com/gallery/browserling/10.UNIX%20Time%20Time%20conversion%20tools_0.png)

输入 UNIX 时间后按Convert按钮将其转换为 UTC 时间。

![在线将 UNIX 转换为 UTC](https://toolsqa.com/gallery/browserling/11.Convert%20UNIX%20to%20UTC%20online_0.png)

转换后的时间将显示在输入文本字段中，如你所见，现在是 2021 年 7 月 15 日。

## 最后的想法

好吧！我希望你了解 Browserling 工具的要点以及它们如何帮助 Web 开发人员和程序员努力工作。它们对于开发 Web 应用程序和执行跨浏览器测试非常有帮助。就个人而言，作为网络开发人员，Browserling 的在线工具最有趣的地方在于没有烦人的广告或弹出窗口。这只是简单的生意！按一个按钮——得到结果。

最后，由于篇幅限制，不可能在这里解释所有工具(因为它们有 400 多个！)。因此，我们建议你探索其工具页面上的所有工具，我相信你会在那里找到一些我们未在此处介绍的非常有趣的工具。