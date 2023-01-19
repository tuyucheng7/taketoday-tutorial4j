众所周知，Web 应用程序在HTML代码中嵌入了JavaScript、CSS和其他依赖项，这些代码在 Web 浏览器中呈现以显示应用程序的最终外观。由于HTML代码不经过编译而是直接在浏览器中呈现，因此开发人员很难在代码文件中进行所需的更改，然后预览浏览器的更改。这提高了对 Web 浏览器功能的需求，使用户能够直接在浏览器内更改JavaScript、CSS和HTML 。甚至测试人员在浏览器中查找和检查元素也已成为自动化测试中的关键术语。一个Web 检查器或浏览器检查器有助于识别网页上可用元素的[定位器](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)，我们希望将其自动化。换句话说，它可以帮助我们检查元素。

不用担心。这不是你需要安装的附加软件，而是内置于浏览器中。Web 检查器在Google Chrome、Internet Explorer 和 Firefox 浏览器中类似，只是名称和功能略有不同。通过Web 开发人员工具，我们可以检查这些浏览器中的元素。在本文中，我们将重点关注以下几点来了解 Web 检查器在各种现代浏览器中的用法：

-   什么是网络检查员？
-   如何检查 Web 浏览器中的元素？
    -   如何检查谷歌浏览器中的元素？
    -   同样，如何在 Firefox 中检查元素？
    -   如何检查 Microsoft Edge 中的元素？
    -   另外，如何检查 Internet Explorer 中的元素？
-   如何使用 Web Inspector 操作 Web 元素？
    -   如何使用 Web Inspector 操作 HTML 属性？
    -   另外，如何使用 Web Inspector 操作 CSS 属性？
    -   如何使用 Web Inspector 操作 JavaScript 属性？

## 什么是网络检查员？

如前所述，网络检查员或浏览器检查员有助于在浏览器中识别/定位网页内的内容。有了我们身边的Web 检查员，我们可以探索和操作浏览器的代码，以查看屏幕的突然变化。这也通常称为“检查元素”。检查器显示网页的CSS、HTML 和 JavaScript，以便开发人员或测试人员可以分析网页并使用浏览器开发人员工具的功能运行一些检查。

一个很好的例子是检查网页上某些文本的颜色。如果我尝试在我的文本编辑器上更改颜色代码，保存文件，再次刷新网页，这会变得乏味。为此，我可以通过该特定行上的网络检查器更改浏览器中的颜色代码，并在网页上实时查看。这些在网络检查器上完成的更改是临时的，一旦我关闭选项卡就会消失。这使得调试非常容易和实时，用户可以直接在浏览器中更新属性。因此，你可以尽情尝试任何东西！

## 如何在网络浏览器中检查元素？

尽管有多种方法可以打开Web 检查器并检查网页上的元素，但最简单的方法是右键单击网页并选择检查。我们还可以使用以下快捷方式打开网页检查器。

-   视窗：CTRL+SHIFT+C
-   苹果机：Command + Shift + C

注意：我们这里使用的是 Google Chrome 浏览器进行演示。

在Google Chrome 浏览器上可以看到如下演示：

![浏览器中的 Web 检查器](https://www.toolsqa.com/gallery/selnium%20webdriver/1.web%20inspector%20in%20a%20browser.png)

[谷歌浏览器检查面板](https://www.toolsqa.com/selenium-webdriver/inspect-element-in-chrome/)的详细指南值得一读，以探索检查面板的全部潜力。

对于当前教程，知道我们想要检查的元素是可以的。打开检查元素面板后，单击下图所示的图标。

![谷歌浏览器开发者工具](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Google%20Chrome%20Developer%20Tools.png)

此图标将启用对Web 元素的悬停检查。单击它后，如果将鼠标悬停在网页上的任何元素上，它将在代码元素面板中突出显示。反之亦然。将鼠标悬停在代码中的元素上将突出显示网页中的特定元素：

![使用悬停检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/3.inspect%20element%20using%20hover.png)

尽管所有的浏览器都包含Web Inspector并且或多或少相似，但仍然存在细微的变化。让我们看看如何在各种流行的网络浏览器中打开检查面板。

### 如何检查谷歌浏览器中的元素？

[按照这些简单的步骤在Google Chrome](https://www.google.com/intl/en_in/chrome/)中检查网页元素。

1.  启动Google Chrome网络浏览器并通过按F12或上述方法(右键单击 -> 检查)打开检查面板。
2.  打开检查面板后，你会注意到顶部有多个可用选项卡，例如元素、控制台、源、网络和性能。但是，Elements 选项卡包含属于当前网页的所有HTML属性(这里我使用网站[demoqa.com](https://demoqa.com/text-box)进行演示)：

![网络检查员](https://www.toolsqa.com/gallery/selnium%20webdriver/4.web%20inspector.png)

1.  在下面的屏幕截图中，打开浏览器检查器后，单击当前地址文本框。在右侧(在 Inspect Panel 下)，关联的元素定位器会自动突出显示。

![在网络浏览器中检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Inspect%20an%20Element%20in%20web%20browser.png)

注意：请注意我们位于“元素”选项卡内。如果默认打开任何其他选项卡，请先导航到“元素”。

1.  检查 Web 元素的另一种方法是右键单击Web 元素并单击检查选项：

![通过右键单击检查来检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/6.inspect%20elements%20by%20right%20click%20inspect.png)

1.  单击Inspect 上下文菜单后，它将突出显示与元素关联的行，你将在Inspect 面板中看到所有定位器属性，如下所示：

```java
<input autocomplete="off" placeholder="Full Name" type="text" id="userName"
 class=" mr-sm-2 form-control">
```

在上面的代码中，你会看到占位符、类型、ID和类作为全名文本框的属性。如果你想阅读有关检查Google Chrome 元素的深入指南，请访问[检查 Google Chrome 中的元素。](https://www.toolsqa.com/selenium-webdriver/inspect-element-in-chrome/) 文本框和网页也只是为了演示；你可以使用任何属性或网络元素。

### 如何检查 Firefox 中的元素？

检查Firefox浏览器中的元素类似于检查Chrome 浏览器中的元素。请按照以下步骤检查Firefox浏览器中的网页元素：

1.启动Firefox浏览器并导航到你要检查的页面(例如，我在这里使用demoqa.com站点)。

![在 Firefox 中检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Inspect%20Elements%20in%20Firefox.png)

2.另一种检查元素的方法是右键单击网络元素。之后，单击Inspect Element (Q)选项(与 Chrome 浏览器相同)：

![在 Firefox 中检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Inspect%20Elements%20in%20Firefox.png)

注意：在 Firefox 浏览器中，“检查器”是你可以找到所有 HTML 元素的选项卡，这与 Chrome 浏览器中的选项卡是“元素”不同。因此，请导航到“检查器”选项卡。如果在 Firefox 浏览器上打开 Web Developer 工具时导航到任何其他选项卡。

1.  打开“Inspector”选项卡后，我们可以使用与在 Chrome 浏览器中所做的相同的步骤来检查 Web 元素。

### 如何检查 Microsoft Edge 中的元素？

检查[Microsoft Edge](https://developer.microsoft.com/en-us/microsoft-edge/)上的元素与Google Chrome相同。原因是Microsoft Edge已转移到构建 Google Chrome 的铬渲染[引擎](https://blog.chromium.org/2013/04/blink-rendering-engine-for-chromium.html#:~:text=Blink%3A A rendering engine for the Chromium project,-Wednesday%2C April 3&text=WebKit is a lightweight yet,engine back when we started.)。因此，它们都在UI 中显示了相似的底层结构。我们可以按照下面提到的步骤来检查Microsoft Edge 中的元素：

1.  假设我们要检查ToolsQA Demo 网站上的元素以在Microsoft Edge 浏览器中打开该网站。

![在 Microsoft Edge 中检查](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Inspect%20in%20Microsoft%20Edge.png)

1.  单击检查选项将打开你要检查的元素。

![inspect_panel_microsoft_edge](https://www.toolsqa.com/gallery/selnium%20webdriver/10.inspect_panel_microsoft_edge.png)

1.  应用程序代码和标签将在“元素”选项卡下可用，如上突出显示。
2.  我们可以按照同样的步骤来查找，它会在代码中高亮显示在元素面板和检查元素中。此外，我们还介绍了Chrome浏览器。

### 如何检查 Internet Explorer 中的元素？

与上面的Chrome和Firefox浏览器类似，按F12并通过激活 Web 检查器手动检查元素，或者右键单击该元素并选择检查元素(如下突出显示)以打开开发人员工具。此过程类似于图像中显示的Chrome和Firefox：

![在 Internet Explorer 中检查元素](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Inspect%20Elements%20in%20Internet%20Explorer.png)

注意：“ DOM Explorer ”是你可以找到所有 HTML 元素的选项卡，与 Chrome 浏览器不同，因此如果在 Internet Explorer 浏览器上打开 Web 开发人员工具时导航到任何其他选项卡，请导航到 DOM Explorer 选项卡。

到目前为止，我们已经了解了如何在各种现代浏览器中检查 Web 元素。我们可以参考各个浏览器对应的详细文章，更详细地了解在特定浏览器中定位元素。现在，正如我们所讨论的，我们也可以使用浏览器开发人员工具进行调试。因此，让我们看看我们如何操作元素的各种属性并在浏览器本身中验证它们：

## 如何使用 Web Inspector 操作 Web 元素？

使用网络检查器，可以更改或操纵网络元素以进行测试/调试。你可以通过双击元素来更新各种CSS属性，例如高度、宽度、大小、文本或任何其他样式元素。使用网络检查器，还可以编辑JavaScript和HTML的某些部分。让我们借助以下部分中的示例了解如何实现所有这些操作：

### 如何使用 Web Inspector 操作 HTML 属性？

让我们举个例子，我正在尝试使用Google Chrome 浏览器中的检查面板更改Web 元素的文本：

1.  导航到 URL “https://demoqa.com/checkbox”并右键单击 -> 检查“桌面”一词，如下所示：

![检查谷歌浏览器中的元素](https://www.toolsqa.com/gallery/selnium%20webdriver/12.Inspect%20elements%20in%20Google%20Chrome.jpg)

1.  只需双击“元素”面板中的“桌面”，即可将文本从“桌面”更新为“测试桌面” 。如下所示，当我们更新Elements 面板中的文本时，网页上的文本也会更新：

![检查 CSS 中的元素](https://www.toolsqa.com/gallery/selnium%20webdriver/13.inspect%20elements%20in%20CSS.jpg)

因此，更新元素面板下HTML标签的任何属性。因此，在浏览器中实时预览其结果。

### 如何使用 Web Inspector 操作 CSS 属性？

考虑一个场景，在同一页面“https://demoqa.com/checkbox”，我们想要更新网页元素的背景颜色；让我们看看如何实现相同的目标：

1.  导航到 URL [“https://demoqa.com/checkbox”](https://demoqa.com/checkbox)，然后右键单击并检查文件夹名称正下方的白色框，如下突出显示：

![检查 Div 元素](https://www.toolsqa.com/gallery/selnium%20webdriver/14.Inspect%20Div%20element.png)

1.  这将引导我们找到代表这个白色 div 框的代码，如下所示：

![检查元素显示](https://www.toolsqa.com/gallery/selnium%20webdriver/15.inspect_element_shown.png)

1.  双击这行代码并添加样式属性，如下行所示：

<div class = "col-12....." style = "background-color: cyan">

1.  按 enter 键，可以看到白色 div 框的颜色变为青色。

![使用CSS更改div颜色](https://www.toolsqa.com/gallery/selnium%20webdriver/16.div%20color%20change%20using%20CSS.png)

正如上面突出显示的，所有 CSS 属性都将显示在“样式”选项卡中。它们将出现在“元素”选项卡部分的下方或右侧。我们可以在 HTML 标记本身中嵌入 CSS 属性，也可以直接添加/更新样式部分的值。

1.  如果我直接在Styles选项卡下将属性“background-color”的值更新为“caderblue” ，就会在浏览器中反映相应的变化，如下图：

![检查面板中的 CSS 属性更改](https://www.toolsqa.com/gallery/selnium%20webdriver/17.CSS%20properties%20change%20in%20the%20inspect%20panel.png)

正如我们所看到的，一旦我们开始编写CSS属性或其对应的值，浏览器本身就会显示所有可能的值。此外，它还有助于调试/更改相应的值。与上述情况一样，当我们将鼠标悬停在值“caderblue”上时，它在浏览器中反映出相同的颜色。这样一来，我们就可以直接在浏览器中操作任何CSS属性。

### 如何使用 Web Inspector 操作 JavaScript 属性？

你的应用程序的 JavaScript 文件将驻留在检查面板的“源”选项卡下，如下所示：

![检查面板中的 JavaScript 操作](https://www.toolsqa.com/gallery/selnium%20webdriver/18.JavaScript%20manipulation%20in%20Inspect%20panel.png)

1.  当你单击“源”选项卡时，你将看到左侧面板中列出的所有源代码文件。它由上图中的标记 1 突出显示。
2.  在此左侧面板中，你可以选择要更新/操作的相应JavaScript文件。在我们的应用程序中，只有一个JavaScript文件，名为bundle.js(如上图中标记 2 突出显示的那样)。
3.  选择 JavaScript 文件后，右侧的执行面板将被激活。它由上图中的标记 3 突出显示。
4.  在此之后，你可以在 JavaScript 中进行所需的更改，并直接在浏览器中预览相应的更改。

注意：所有这些 HTML、CSS、JavaScript 的更改都是暂时的，并且会在你关闭浏览器选项卡后丢失。因此，它只是一个临时界面，用于快速调试和查看任何计划的更改。

## 关键要点

-   Web 检查器是每个浏览器中都可用的面板，允许你导航呈现你看到的网页的组件。
-   此外，用户可以使用 CTRL+SHIFT+C 检查元素。
-   此外，通过检查浏览器元素，你可以修改 Web 元素以查看 UI 的即时更改。但请记住，这不会对 UI 进行任何永久更改。