在上一章中，我们学习了使用[WebDriver Element Locator](https://toolsqa.com/selenium-webdriver/webdriver-element-locator-firefox-add-on/) Tool，它是Firefox Browser 的 Add On。虽然这是一个非常简单的工具，而且一直是我的首选，因为它可以节省很多时间。但这会使你的 XPath 学习逻辑变弱，因此你可能会在面试中遇到困难。对于初学者，我总是建议避免使用此类工具或插件。

在本章中，我们将学习一个名为XPath Helper的新工具Chrome 浏览器的附加组件。尽管[Firefox](https://www.toolsqa.com/selenium-webdriver/xpath-firebug-firepath/)和[Firepath](https://toolsqa.com/selenium-webdriver/xpath-firebug-firepath/)插件可以完成相同的任务，但如果你喜欢使用 Chrome 浏览器，你可能希望在其中保留一些方便的东西。在开始使用之前，让我们看看它的安装步骤。

## 如何为 Chrome 浏览器安装 XPath Helper 插件？

1.  转到[https://chrome.google.com/webstore/detail/xpath-helper/hgimnogjllphhhkhlmebbmlgjoejdpjl?hl=en](https://chrome.google.com/webstore/detail/xpath-helper/hgimnogjllphhhkhlmebbmlgjoejdpjl?hl=en)
2.  单击添加到 Chrome。

![XpathHelper_1](https://www.toolsqa.com/gallery/selnium%20webdriver/1.XpathHelper_1.png)

注意：如果你在任何其他浏览器中打开 URL，你将看到选项“在 Chrome 中可用”而不是“添加到 Chrome”

1.  单击添加按钮。

![XpathHelper_2](https://www.toolsqa.com/gallery/selnium%20webdriver/2.XpathHelper_2.png)

1.  安装完成后，它将显示成功安装的弹出消息。

重要提示：安装此扩展程序后，你必须重新加载任何现有选项卡或重新启动 Chrome 才能使扩展程序正常工作。

1.  现在XPath Helper的图标将出现在 Chrome 浏览器的最右上角。只需单击它即可打开助手。![XpathHelper_3](https://www.toolsqa.com/gallery/selnium%20webdriver/3.XpathHelper_3.png)

## 如何使用 XPath 帮助工具？

指示：

1.  打开一个新选项卡并导航到任何网页。我使用[www.DemoQA.com](https://demoqa.com/)进行演示。
2.  按Ctrl-Shift-X(或 OS X 上的 Command-Shift-X)，或单击工具栏中的XPath Helper按钮，打开 XPath Helper 控制台。
3.  将鼠标悬停在页面上的元素上时按住 Shift 键。查询框将不断更新以显示鼠标指针下方元素的 XPath 查询，结果框将显示当前查询的结果。
4.  如果需要，直接在控制台中编辑 XPath 查询。结果框将立即反映你的更改。
5.  重复步骤(2)关闭控制台。

注意：如果控制台挡住了你的路，请按住 Shift 键，然后将鼠标移到它上面；它将移动到页面的另一侧。

一个警告：当呈现 HTML 表格时，Chrome`<tbody>`会在 DOM 中插入人工标记，因此这些标记将显示在此扩展程序提取的查询中。

### 构建你自己的 XPath

对于页面上的注册链接，请尝试你自己的 XPath：//li[@id='menu-item-374']

![XpathHelper_4](https://www.toolsqa.com/gallery/selnium%20webdriver/4.XpathHelper_4.png)

注意：如果 XPath 无效，结果端将显示错误，但如果 XPath 语法正确且未找到匹配项，则会显示 NULL。

为同一元素尝试另一个 XPath：//li[@id='menu-item-374']

![XpathHelper_5](https://www.toolsqa.com/gallery/selnium%20webdriver/5.XpathHelper_5.png)