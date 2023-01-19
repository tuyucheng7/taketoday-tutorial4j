Selenium 用户知道为自动化脚本定位元素的重要性。尽管我们有不同的工具，如 Browser Inspector、FirePath 和 FireBug 来帮助我们，让我们在 Selenium Automation 世界中的生活变得轻松。仍然定位一个元素，然后将该元素放入语言脚本中是自动化活动的一项关键任务。当然，你可以手动创建定位器或使用 Selenium IDE 来获取这些元素定位器的详细信息。但是，如果你可以在浏览器的上下文菜单中获取元素定位器的详细信息，那不是很好吗？

## WebDriver 元素定位器

WebDriver Element Locator是Firefox 浏览器的一个不错的附加组件，它可以让你做到这一点并节省大量时间。由于这是 Firebox 的一个插件，使用起来很简单，使用它，只需右键单击你想要定位的 Web 元素，选择一个合适的定位器字符串，它就会被复制到你的剪贴板。它会在浏览器的上下文菜单中显示元素定位器的多个选项。它以C#、Java、Python 和 Ruby等不同语言显示带有完整 Selenium 脚本的元素定位器。请参阅下面的屏幕截图以查看正在运行的 WebDriver 元素定位器。

![WebDriver_Element_Locator_1](https://www.toolsqa.com/gallery/selnium%20webdriver/1.WebDriver_Element_Locator_1.png)

注意：要禁用你不需要的任何定位器类型(例如支持、Ruby)，只需转到工具>附加组件 (Ctrl+Shift+A)，然后在附加选项中取消选中它们。

## 如何下载 WebDriver 元素定位器？

就像任何其他 Firefox 附加组件一样，它也可以从 Firefox 本身下载。

-   转到 工具 > 附加组件
-   搜索WebDriver 元素定位器
-   点击安装
-   重新启动火狐浏览器

或者直接去下面的网址

[https://addons.mozilla.org/en-us/firefox/addon/element-locator-for-webdriv/](https://addons.mozilla.org/en-us/firefox/addon/element-locator-for-webdriv/)

单击添加到 Firefox

![WebDriver_Element_Locator_2](https://www.toolsqa.com/gallery/selnium%20webdriver/2.WebDriver_Element_Locator_2.png)

窗口顶部将显示一个小弹出窗口，单击“安装”按钮。

![WebDriver_Element_Locator_3](https://www.toolsqa.com/gallery/selnium%20webdriver/3.WebDriver_Element_Locator_3.png)

安装后，它将显示成功消息。现在重新启动 Firefox 浏览器。

![WebDriver_Element_Locator_4](https://www.toolsqa.com/gallery/selnium%20webdriver/4.WebDriver_Element_Locator_4.png)

### 以下是此附加组件的一些显着功能。

1.  为了帮助防止错误测试(并节省你的调试时间)，它还将检查定位器的唯一性，用红色十字和绿色勾号表示。
2.  如果元素具有长的、脆弱的、自动生成的属性，例如 id=" ctl00_ElementContainer_Inputs_txtForename "，它将尝试仅根据值的最后(也是最重要的)部分进行定位。
3.  如果通过属性定位困难，它还会尝试通过文本值定位。
4.  此扩展将尝试使用适用于 dotnet、python 和 ruby 绑定的基于 webdriver XPATH 的 findElement 命令填充上下文菜单，并为聚焦的 web 元素提供支持定位器库引用。

我们将在本系列中学习更多关于定位器的工具，但总而言之，它也是一个很好且非常有用的自动化测试插件。试一试，让我们知道你对它的看法。