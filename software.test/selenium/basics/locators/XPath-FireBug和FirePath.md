## 什么是 XPath

XPath是一种语言，它描述了一种在可扩展标记语言 ( XML ) 文档中定位和处理项目的方法，方法是使用基于通过文档逻辑结构或层次结构的路径的寻址语法。Selenium 中使用 XPath 来唯一标识网页上的元素作为元素定位器，就像我们在现实世界中使用 PostCode 和 House address 来定位 Home Address 一样。

## 萤火虫是什么

Firebug 与 Firefox 集成，在你浏览时将丰富的 Web 开发工具置于你的指尖。你可以在任何网页中实时编辑、调试和监控 CSS、HTML 和 JavaScript。此页面的全部内容取自 https://getfirebug.com/html。

## 为什么对 Selenium Automation Tester 有用

1.  实时查看源代码：Firefox 有一个“查看源代码”窗口，但它不会向你显示 HTML 源代码在被 JavaScript 转换后的真实外观。Firebug 的 HTML 选项卡向你显示 HTML现在的样子。
2.  查看突出显示的更改：在任何 JavaScript 驱动的网站中，HTML 元素都在不断地创建、删除和修改。如果你能准确地看到这些变化发生的内容、时间和地点，那不是很好吗？Firebug 会在 HTML 发生更改时立即以黄色突出显示更改。如果你想更仔细地观察，你还可以选择将每个更改滚动到视图中，这样你就不会错过任何东西。
3.  用鼠标查找元素：你页面上的某些内容看起来不太正确，你想知道原因。没有比单击 Firebug 工具栏上的“检查”按钮然后准备立即获得答案更快捷的方法了。当你在页面上移动时，鼠标下方的任何内容都会立即显示在 Firebug 中，向你显示其背后的 HTML 和 CSS。
4.  复制源代码：右键单击任何元素，你将有几个选项可以将该元素的各个方面复制到剪贴板，包括它的 HTML 片段、它的“ innerHTML ”属性的值，或者标识该元素的 XPath 表达式独特的。

## 如何下载萤火虫

FireBug 是 Firefox 浏览器自带的插件，因此可以很容易地从 Firefox 本身下载。

1.  转到工具 > Web 开发人员 > 获取更多工具。

![萤火虫一号](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Firebug-1.png)

1.  它将打开一个网页并显示所有可用于 Firefox 浏览器的插件。由于我们需要 Firebug，只需单击 Firebug 的添加到 Firefox按钮。

![萤火虫2](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Firebug-2.png)

1.  点击立即安装 按钮继续。

![萤火虫3](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Firebug-3.png)

1.  安装完成后，按“ F-12 ”打开 Firebug 工具。它会像这样显示。

![萤火虫 5](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Firebug-5.png)

## 如何使用它

大多数情况下，它用于检查网页上的元素并从网页中获取元素的XPath。

1.  检查元素：请访问 [使用浏览器检查器查找元素以](https://www.toolsqa.com/selenium-webdriver/inspect-elements-using-browser-inspector/) 获取有关如何使用浏览器检查器查找元素的详细说明。
2.  复制 XPath：复制 XPath 真的非常方便。使用检查器完成选择元素后，你需要做的就是右键单击所选元素的HTML 代码并选择复制 XPath。

![萤火虫6](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Firebug-6.png)

1.  现在你可以通过按“ Ctrl + V ”将复制的 XPath 粘贴到你的测试脚本中。它会像这样显示：

```java
/html/body/div/header/div/a/img
```

## 什么是 FirePath

它是 FireBug 的扩展，添加了一个开发工具来编辑、检查和生成 XPath 表达式和 CSS3 选择器。

## 为什么对 Selenium Automation Tester 有用

1.  你可以键入自己编写的 XPath，并通过直接在网页上突出显示结果来检查它是否正确。
2.  通过右键单击元素并在上下文菜单中选择“ Inspect in FirePath ”，为元素生成 XPath 表达式或 CSS 选择器。
3.  与 Firebug 一样，它也为你提供所选元素的 Xpath。

## 如何下载 Firepath

Firepath 是 Firebug 的扩展，因此你只能在安装 FireBug 后安装它。

1) 转到 Tools > Web Developer > Get More Tools。

![萤火虫一号](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Firebug-1.png)

2) 它将打开一个网页并显示所有适用于 Firefox 浏览器的插件。正如我之前所说，它是 Firebug 的扩展，你需要单击 扩展 链接并在搜索字段中输入Firepath 。由于我们需要 FirePath，只需单击 FirePath 的 添加到 Firefox 按钮。

![FirePath-1](https://www.toolsqa.com/gallery/selnium%20webdriver/7.FirePath-1.png)

3) 点击 立即安装 按钮继续。

![FirePath-2](https://www.toolsqa.com/gallery/selnium%20webdriver/8.FirePath-2.png)

1.  安装后，它会要求重新启动浏览器。单击立即重新启动按钮。

![FirePath-3](https://www.toolsqa.com/gallery/selnium%20webdriver/9.FirePath-3.png)

1.  打开后，按“ F-12 ”打开 Firebug 工具。它将在同一个控制台上显示FirePath，如下所示：

![FirePath-4](https://www.toolsqa.com/gallery/selnium%20webdriver/10.FirePath-4.png)

## 如何使用 FirePath

1) 检查元素：请访问 [使用浏览器检查器查找元素以](https://www.toolsqa.com/selenium-webdriver/inspect-elements-using-browser-inspector/) 获取有关如何使用浏览器检查器查找元素的详细说明。但与 FireBug 不同的是，它在控制台上显示所选元素的 XPath。

![FirePath-6](https://www.toolsqa.com/gallery/selnium%20webdriver/11.FirePath-6.png)

2) 复制 XPath：复制 XPath 真的非常方便。使用检查器完成选择元素后，你需要做的就是复制所选元素的 XPath，然后按“ Ctrl + V ”将其粘贴到测试脚本中。它会像这样显示：

```java
//[@id='masthead']/div/a/img
```

## FireBug 和 FirePath 的区别

从自动化测试人员的角度来看，唯一的区别是 FireBug 返回绝对 XPath，而 FirePath 返回相对 XPath。