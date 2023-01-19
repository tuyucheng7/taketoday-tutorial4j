谷歌浏览器是当今所有现代浏览器市场的领导者。毫无疑问，它是 Web 开发人员最喜欢的浏览器之一，也是所有从事UI自动化工作的QA工程师的首选之一此外， Chrome随着时间的推移而成长和成熟，并提供各种功能来帮助开发人员和测试人员在浏览器本身内部验证应用程序。所有这些有用的工具和选项组合在Chrome 浏览器的“开发人员工具”选项下，它提供了用于编辑/调试HTML/CSS和JavaScript的各种选项。此外，它还提供了“inspect element ”，这对测试人员来说是一个有益的工具。

WebElements检查是Selenium Automation的核心。早些时候，我们有用于此目的[的 Firebug 和 Fire-path](https://toolsqa.com/selenium-webdriver/xpath-firebug-firepath/)工具。今天我们有几个附加组件和插件可以与浏览器一起使用来检查DOM(文档对象模型)中的元素。但是使用开发人员工具检查元素和修改DOM 比使用插件或附加组件更高效、更舒适。毕竟，它们嵌入是有原因的。

因此，让我们从使用开发人员工具在 Chrome中检查元素这一章开始。随后，我们将在本文中介绍以下主题。

-   什么是 Chrome DevTools 面板？
    -   如何访问 Chrome DevTools 面板？
    -   如何停靠/取消停靠 DevTools 面板？
-   使用 Chrome DevTools 检查元素
    -   为什么要检查一个元素？
    -   如何检查 Chrome 开发工具中的元素？
-   如何使用 Chrome DevTools 定位元素？
    -   通过字符串定位元素
    -   通过 CSS 选择器定位元素
    -   并且，通过 XPath 定位一个元素。

## 什么是 Chrome DevTools 面板？

Chrome 开发人员工具提供了一个名为“ DevTools Panel ”或“ Element Panel ”的功能，我们可以使用它从前端检查元素并修改它们以进行调试。我们还可以更改网页的外观甚至内容，因为我们可以在运行时编辑“ CSS ”和“ HTML ”文件并对应用程序进行快速调试。

那么我们如何访问谷歌浏览器开发者工具中的元素面板呢？让我们看看实现相同目标的各种方法：

### 如何访问 Chrome DevTools 面板？

按照以下步骤访问Chrome 浏览器中的DevTools面板：

1.  首先，单击浏览器屏幕右上角的“自定义和控制 Google Chrome ”按钮(垂直线上的 3 个点)。
2.  其次，在那之后，点击More tools->Developer Tools。

![打开开发者工具检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Open%20Developer%20Tools%20to%20inspect%20element.png)

1.  第三，如上图所示，我们点击Developer Tools ，这会在chrome 开发者工具部分打开元素框。随后，下图显示了元素框的外观：

![网页的 DOM 面板](https://www.toolsqa.com/gallery/selnium%20webdriver/2.DOM%20panel%20of%20a%20webpage.png)

本节主要由三部分组成，其中，

-   DOM 面板：DOM 面板(标记为 1)是 Elements 下方框的上部。它是一种我们可以更改 HTML 页面布局的工具。在这里我们可以完全控制 HTML 并可以自由修改文件。
-   控制台面板：控制台面板(标记为 2)位于元素框的底部，显示开发人员在脚本中记录的日志消息。它还显示了 Chrome 开发人员工具中的新功能。
-   CSS 面板：CSS 面板(标记为 3)更改网页的 CSS 属性 - 字体、大小和颜色。

除了点击“更多工具->开发者工具”，我们还可以使用以下选项打开元素框：

-   单击 F12 键。
-   在 Windows 操作系统上使用键盘快捷键“ Ctrl + Shift + i”或“Ctrl + Shift + c ”。相同的命令适用于 Chrome OS 和 Linux。

在 Mac OS 上，我们可以使用命令“ Cmd + Opt + C ”打开 chrome 开发者工具。

-   右键单击 Chrome 浏览器中的网页，然后单击“检查”。

我们可以使用上述任何选项在Chrome 开发者工具中打开元素框。

### 如何停靠/取消停靠 DevTools 面板？

我们还可以将Chrome 开发者工具窗口停靠在窗口的左侧/右侧或底部，甚至将其取消停靠到单独的窗口。

元素面板中提供了停靠选项，如下突出显示：

![开发者工具窗口的对接](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Docking%20of%20Developer%20Tools%20window.png)

当我们点击窗口右上角的三个垂直点时，我们会看到“ Dock side ”选项以及其他选项。此选项具有以下子选项(由选项旁边的小图像指示)，我们可以使用它们根据我们的要求停靠窗口。

1.  取消停靠到一个单独的窗口
2.  停靠在左边
3.  停靠到底部
4.  而且，靠右停靠

因此，单击这些选项中的任何一个，我们可以根据我们的舒适度和需要将DevTools面板停靠/取消停靠到屏幕上的任何位置。

正如我们所讨论的，Chrome DevTools面板提供的主要功能之一是提供检查 Web 元素和查找[各种类型的定位器](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)的功能，我们可以使用这些定位器在Selenium 测试用例中定位元素。随后，让我们看看如何使用DevTools面板检查不同的 Web 元素并识别 Web 元素的各种定位器。

## 使用 Chrome DevTools 检查元素

在我们继续如何使用 Chrome DevTools检查元素之前，我们将首先尝试回答这个问题，“检查元素的必要性是什么？ ”

### 为什么要检查一个元素？

inspect 元素提供以下好处/用例，具体取决于谁是相同的消费者/用户：

| 角色               | 用例                                                         |
| ------------------ | ------------------------------------------------------------ |
| 开发商       | 如果开发人员需要对网页进行一些临时更改，那么 inspect 元素是最好的解决方案。 |
| 数字内容作家 | 当数字内容编写者需要包含敏感信息的网页的屏幕截图时，他/她可以使用 inspect 元素来删除敏感内容。 |
| 网页设计师   | 网页设计人员需要此功能来检查他们对网页所做的各种设计和布局更改。 |
| 数字营销人员 | 数字营销人员可以通过允许用户在网站上查看隐藏的关键字，在竞争对手的网页上使用检查元素来发挥自己的优势。inspect 元素还可以告诉我们页面加载速度是太慢还是太快 |
| 支持代理     | 支持代理向开发人员解释网站上的修复可能希望我们展示一些可以使用 inspect 元素进行的快速修复 |

上面讨论的要点只是 inspect 元素使用的几个例子。inspect element 工具在 web 开发中有更多的用途。

### 如何检查/突出显示 DOM 中的元素？

有多种方法可以使用Chrome 开发者工具检查/突出显示元素。其中一些是：

#### 直接从网页检查元素：

1.  首先，在网页上选择我们需要检查的元素，然后右键单击。现在在显示的上下文菜单中选择“检查”。

![选择检查选项](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Select%20an%20inspect%20option.png)

1.  其次，DOM(元素框)会高亮选中的元素。

![突出显示使用 Inspect 选择的元素](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Highlight%20element%20selected%20using%20Inspect.png)

在上图中，我们在网页上选择了元素“ BLOGS ”，然后右键单击并选择Inspect选项。因此，在DOM面板中，我们可以看到相应的元素被突出显示(由红色矩形指示)。

#### 使用 DevTools 的 DOM 面板检查元素：

检查元素的下一个方法是使用DOM 面板。打开 Chrome DevTools的DOM面板，按照下面的步骤检查一个 web 元素：

-   首先，单击DOM 面板左上角的“选择元素”按钮(如以下屏幕截图中标记 1 突出显示的那样)。之后，你可以单击要检查的元素；该元素将突出显示。

![使用 DOM 按钮检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Inspect%20element%20using%20DOM%20button.png)

在上图中，指针 1 显示“选择一个元素”按钮。当我们单击此按钮然后选择我们要检查的任何元素时，该元素会在网页上突出显示。例如，在上图中，我们选择了菜单类，该元素被突出显示(导航栏中的博客)。

## 如何使用 Chrome DevTools 定位元素？

Chrome DevTools的DOM面板提供了一个名为“ find ”的特殊工具，用于根据指定的条件定位 Web 元素。因此在浏览DOM面板时，我们可以使用这个“查找”工具 搜索各种节点/元素。

那么我们如何在DOM 面板中搜索节点/元素呢？打开Chrome 开发人员工具后，按“ Ctrl + f ”并在DOM 面板中为你打开一个查找栏(如下突出显示)，你可以在其中输入搜索条件。

![在 DOM 中查找工具](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Find%20tool%20in%20DOM.png)

查找工具指示上图中的红色方块。我们可以看到我们可以在查找工具中指定的条件是字符串、[CSS 选择器](https://www.toolsqa.com/selenium-webdriver/css-selectors-in-selenium/)或[XPath](https://www.toolsqa.com/selenium-webdriver/xpath-in-selenium/)。因此我们可以在查找工具中搜索元素或节点：

-   通过指定一个字符串
-   通过像 HTML 标签一样指定 CSS 选择器
-   并且，通过像 Id 一样指定 XPath。

### 通过字符串定位元素

在 DOM 面板中，我们可以提供任何字符串来搜索元素，如下所示。

![按字符串检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Inspect%20element%20by%20string.png)

在上图中，我们将“ selenium ”指定为字符串。它突出显示所有值为“ selenium ”的条目。我们可以在查找工具的右侧看到，它为我们提供了条目总数以及当前突出显示的条目 ( number )(在上图中，总结果为 75)。它还为我们提供了一个用于滚动条目的按钮。

### 通过 CSS 选择器定位元素

我们还可以在查找工具中指定CSS 选择器，它将为我们提供与指定选择器关联的所有条目。那么我们如何找到特定标签的CSS 选择器呢？

在元素面板中，我们可以右键单击我们想要CSS 选择器的任何标签，然后选择复制选项。然后我们可以将标签复制为CSS 选择器。如下面的屏幕截图所示。

![获取 CSS 选择器](https://www.toolsqa.com/gallery/selnium%20webdriver/9.get%20CSS%20selector.png)

所以在上图中，作为示例，我们选择了`<script>`标签并复制了它的选择器，如上所示。选择器是

```java
html > script:nth-child(2)
```

请注意，nth-child(2) 表示我们选择的确切标签。标签的通用选择器`<script>`将是：

```java
html > script
```

从上面的截图中可以看出，我们还可以复制 XPath、完整的XPath、JS 路径等。使用复制选项。下图显示了在查找框中提供CSS 选择器的示例。

![使用 CSS 选择器检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Inspect%20element%20using%20CSS%20selector.png)

在这里，我们提供了 CSS 选择器作为“ #gsr > script ”，如你所见，它突出显示了`<script>`标签。

注意：要了解更多关于 CSS 选择器的知识，你可以参考文章“ [CSS 选择器](https://www.toolsqa.com/selenium-webdriver/css-selectors-in-selenium/)”。

### 通过 XPath 定位元素

我们还可以将XPath指定为“查找”工具的搜索字符串。当我们提供有效的XPath时，查找工具会为我们搜索适当的节点。请注意，我们还可以使用 Copy 选项检索特定元素的XPath，如上所示。因此，请考虑下图。

![通过 XPath 检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Inspect%20element%20by%20XPath.png)

在上面的示例中，我们将 XPath 指定为：

```java
//script[@id='search-suggestions-loader']
```

上面的表达式表明我们正在寻找具有给定id属性值的节点“脚本”。如图所示，查找工具突出显示获得的结果(红色箭头所指)。这样，我们就可以利用 DOM 面板中的查找工具来搜索节点并对其进行检查，从而高效地设计我们的网页。

注意：要了解更多有关 Xpath 的信息，可以参考文章“ [Selenium 中的 XPath](https://www.toolsqa.com/selenium-webdriver/xpath-in-selenium/) ”。

## 关键要点

-   Google Chrome 提供了一个有益的开发者工具选项，对于任何运行时调试和检查任何元素都非常方便。
-   第一种方法是选择一个我们需要检查的元素，然后单击右键并选择检查。它显示 DOM 面板，并突出显示所选元素。
-   第二种方法是打开 DOM 面板并选择我们需要检查的特定元素。
-   最后，第三种方法是使用 DOM 面板中的查找工具。我们可以指定字符串、CSS 选择器或 XPath 作为查找工具的搜索条件，它会为我们提供相关结果。