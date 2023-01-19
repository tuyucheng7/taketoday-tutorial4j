在现代世界中，日新月异，数百个网络和移动应用程序被部署到网络上。QA团队必须始终保持警惕，确保这些 Web 应用程序在开发环境之外正常运行，方法是确保在向客户发布之前对功能进行充分测试。早些时候，这项繁琐的工作是由人工测试人员通过人力观察来完成的，耗费了大量时间。[Selenium](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)进入该行业。Selenium 是一套工具，即。Selenium IDE、WebDriver、Selenium RC 等帮助 QA 团队模拟用户在 Web 浏览器上的操作并自动化用户流程，从而帮助在短时间内执行大量测试用例。

Selenium Webdriver是该家族的重要成员之一，以其在 Web 自动化方面的多样性和稳定性而闻名。Selenium Webdriver已经成为UI 自动化的事实，超过 80% 的公司都在使用它。让我们通过涵盖以下部分来了解此工具的详细信息：

-   什么是 Selenium WebDriver？
    -   为什么要使用 Selenium WebDriver 进行 Web 自动化？
    -   为什么 Selenium WebDriver 受欢迎？
    -   Selenium WebDriver 的缺点是什么？
-   了解 Selenium WebDriver 架构？
    -   Selenium WebDriver 是如何工作的？
-   如何使用 Selenium WebDriver 实现 Web 自动化？



## 什么是 Selenium WebDriver？

Selenium WebDriver是一组开源[API，](https://en.wikipedia.org/wiki/Application_programming_interface)它提供了与任何现代 Web 浏览器交互的功能，然后反过来使用该浏览器自动执行用户操作。它是Selenium家族的重要组成部分。众所周知，Selenium 不是一个独立的工具；相反，它是构成 Selenium 套件的工具集合，它是在合并两个项目 Selenium RC 和 WebDriver 时创建的。

![Selenium WebDriver](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Selenium%20WebDriver.png)

WebDriver 与Selenium RC集成以克服 Selenium RC 的一些局限性，现在已成为 Web 自动化的事实。你必须知道，在 Selenium 2 之后，已经发布了新版本。到 2021 年 1 月，他们已经达到了Selenium 4。

### 为什么要使用 Selenium WebDriver 进行 Web 自动化？

现在我们知道了 Selenium WebDriver是什么以及它的作用让我们来看看为什么它是用于 Web 自动化的最佳选择。

| 特点            | 描述                                                   |
| ---------------------- | ------------------------------------------------------------ |
| 动态网页自动化   | Selenium WebDriver可以自动化动态网站，其中页面内容会因用户操作而改变。 |
| 靠近浏览器工作   | 浏览器供应商发布了他们的WebDriver实现。因此，它们与浏览器紧密耦合，提供更好的测试体验。 |
| 技术不可知论者   | Selenium WebDriver允许你自动化所有 Web 应用程序的测试用例，而不管用于开发被测应用程序的技术如何。 |
| 模仿真实用户     | Selenium WebDriver允许 QA 模仿网站上的用户操作。Selenium WebDriver 可以模拟典型的用户操作，如表单填写、单击、双击、按键，以及高级用户操作，如拖放、单击并按住等。 |
| 支持跨浏览器测试 | Selenium WebDriver在进行跨浏览器测试时具有最显着的优势 - QA 可以在不同浏览器上使用同一段代码测试同一网站。它可以同时在多组浏览器上验证和验证测试用例。 |
| 支持并行执行     | 如果要在多个浏览器上执行的脚本比较多，那么一个一个做起来很费时间。所以 Selenium WebDriver允许并行执行，使用像 TestNG 这样的框架，这样测试用例的执行速度更快。这允许在短时间内大规模执行测试用例。 |
| 查看执行结果     | Selenium WebDriver允许 QA 通过支持屏幕截图、测试用例的视频录制等功能，查看在计算机系统以及任何其他 CI/CD 管道机器上运行的自动化测试的实时执行。 |
| 支持现代开发技术 | Selenium WebDriver通过与 Cucumber 库集成，与行为驱动开发等现代软件开发原则很好地集成。 |

从广义上讲，Selenium WebDriver是 Selenium 套件中最重要的部分之一，它支持 Web 应用程序自动化所需的几乎所有功能。

### 为什么 Selenium WebDriver 受欢迎？

除了上述功能外，作为 Selenium 家族的一部分，WebDriver还包含一些独特的特性，这增加了它作为 Web 自动化工具的流行度。其中一些特征是：

-   多浏览器兼容性——Selenium 和 WebDriver 流行的主要原因之一是它使用同一段代码支持跨浏览器。它提供了运行特定代码段的能力，该代码段模仿真实世界的用户，使用浏览器的本机支持直接调用 API，而无需任何中间件软件或设备。下面显示了支持的浏览器的示例列表：

![支持的 WebDriver 浏览器](https://www.toolsqa.com/gallery/selnium%20webdriver/2.WebDriver%20Browsers%20Supported.png)

-   多语言支持——并非所有测试人员都精通某种特定语言。由于 Selenium 提供对多种语言的支持，因此测试人员可以使用受支持语言之外的任何语言，然后使用 WebDriver 进行自动化。这使人们可以自由地使用人们熟悉的语言编写代码。
-   更快的执行- 与 Selenium RC 不同，WebDriver 不依赖中间件服务器与浏览器通信。WebDriver 使用定义的协议 (JSON Wire) 直接与浏览器通信，这使其能够比大多数 Selenium 工具更快地进行通信。此外，由于 JSON Wire 本身使用非常轻量级的 JSON，因此每次调用的数据传输量非常小。下图清楚地展示了 WebDriver 是如何与浏览器通信的：

![WebDriver 与浏览器的通信](https://www.toolsqa.com/gallery/selnium%20webdriver/3.WebDriver%20communication%20with%20Browsers.png)

-   定位 Web 元素——为了执行点击、键入、拖放等操作，我们首先需要确定我们需要在哪个 Web 元素(如按钮、复选框、下拉菜单、文本区域)上执行操作。为了促进这一点，WebDriver 提供了使用各种 HTML 属性(如 id、名称、类、CSS、标签名称、XPath、链接文本等)来识别 Web 元素的方法。
-   处理动态网络元素——有时页面上的网络元素会随着页面的每次重新加载而改变。由于 HTML 属性发生变化，识别这些元素成为一个挑战。Selenium 提供了多种方法来处理这些情况——
    -   绝对 Xpath - 这包含相关元素的完整 XML 路径。
    -   Contains( ) - 使用这些功能元素可以搜索部分或全文，并可用于处理动态元素。
    -   Starts-With( ) - 此函数基于使用相关属性的起始文本查找元素。
-   处理等待元素- 并非所有页面都具有相同的结构。有些是轻量级的，而有些则有大量的数据处理或 AJAX 调用。很多时候网页元素需要一些时间来加载。为了解决这个问题，WebDriver 提供了多种等待机制，可用于根据特定条件暂停脚本执行所需的时间，然后在条件完全满足后继续执行。下图显示了一个示例列表，其中显示了 WebDriver 的功能，有助于处理网页的动态行为。

![动态网络元素处理能力](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Dynamic%20Web%20Elements%20handling%20capabilities.png)

### Selenium WebDriver 的缺点是什么？

尽管 Selenium 在解决 Web 应用程序的 UI  和功能自动化方面大有作为，但它并非没有缺点。让我们来看看一些缺点/缺点：

-   需要编程知识和专业技能——由于 WebDriver 允许你使用以某种编程语言编写的代码自动执行用户操作，任何想要使用它的人都应该对该语言的编码工作原理有基本的了解。不了解编程语言编码的人会发现很难使用 WebDriver。
-   不支持桌面应用程序。- Selenium 生态系统，包括 WebDriver，是为 Web 应用程序的自动化而构建的。因此，如果你希望自动化基于 Windows 的应用程序，你将无法做到。
-   无客户支持——Selenium 生态系统，包括 WebDriver 是完全开源的，这意味着它是由个人而非任何公司驱动的。因此，没有专门的支持团队来调查你的问题。如果一个人被困在某个地方，有很多社区、论坛可以让他依赖，但仅此而已。
-   无内置对象存储库- UFT/QTP 等付费工具提供了一个集中位置来存储对象/元素，称为对象存储库。Selenium WebDriver 默认不提供此功能。这可以使用页面对象模型等方法来克服，但它需要相当多的编码技能和专业知识。
-   缺少内置报告——Selenium WebDriver 可以帮助你运行自动化测试，但要提供报告功能，你需要将其与 Junit、TestNG、PyTest、Allure 等测试框架集成。
-   管理浏览器-Selenium 依赖关系——由于 Selenium 必须依赖于浏览器驱动程序与实际浏览器本身之间的兼容性，因此很多时候由于浏览器驱动程序或浏览器中的不兼容性或错误，功能中断，用户必须依赖社区支持把它修好。

## 了解 Selenium WebDriver 架构

作为整个组件系统的一部分，我们推断Selenium WebDriver不是一个独立的测试工具。它包含运行测试所需的各种组件。这些是 Selenium 的架构组件。

所以首先让我们来看看下面这张图片

![Selenium WebDriver 架构](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Selenium%20WebDriver%20Architecture.png)

这张图片告诉我们核心 selenium webdriver 架构和构成WebDriver 的主要 selenium 组件。

-   Selenium WebDriver 客户端库/语言绑定——软件测试人员希望选择他们熟悉的语言。由于 WebDriver Architecture 支持不同的语言，因此有适用于[Java、](https://www.toolsqa.com/java/java-tutorial/) C#、[Python、](https://www.toolsqa.com/python-tutorial/) Ruby、PHP 等一系列语言的绑定。任何具有使用任何编程语言的基本知识的人都可以获得特定的语言绑定并可以开始离开。这就是 Selenium Achitecture 为测试人员提供灵活性以在他们的舒适区进行自动化的方式。
-   JSON WIRE PROTOCOL - 根据上面的 Selenium 架构，JSON Wire 协议促进了浏览器和代码之间在 Selenium 中发生的所有通信。这是 Selenium 的核心。JSON Wire Protocol 提供了一种使用 RESTful(表述性状态传输)API 进行数据传输的媒介，它提供了一种传输机制并使用 JSON over HTTP 定义了 RESTful Web 服务。
-   浏览器驱动程序——由于 Selenium 支持多种浏览器，每个浏览器都有自己的 Selenium 提供的 W3C 标准的实现。因此，特定于浏览器的特定于浏览器的二进制文件是可用的，并且对最终用户隐藏了实现逻辑。JSONWire 协议在浏览器二进制文件和客户端库之间建立连接。
-   浏览器——如果浏览器是本地安装的，Selenium 将只能在浏览器上运行测试，无论是在本地机器上还是在服务器机器上。所以需要安装浏览器。

### Selenium WebDriver 是如何工作的？

在上面的部分中，我们看到了 Selenium 的架构。现在让我们看看幕后所有的沟通是如何发生的？看看下面的图片——它显示了实际工作流程的样子。

![与浏览器交互的命令流](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Commands%20Flow%20for%20interacting%20with%20Browser.png)

当用户在 Selenium 中编写 WebDriver 代码并执行它时，后台会发生以下操作 -

-   生成 HTTP 请求，并将其转到相应的浏览器驱动程序(Chrome、IE、Firefox)。每个 Selenium 命令都有一个单独的请求。
-   浏览器驱动程序通过 HTTP 服务器接收请求。
-   HTTP 服务器决定需要在浏览器上执行哪些操作/指令。
-   浏览器执行上面决定的指令/步骤。
-   然后 HTTP 服务器接收执行状态，然后将状态发送回自动化脚本，然后显示结果(通过或异常或错误)。

## 如何使用 Selenium WebDriver 实现 Web 自动化？

Selenium WebDriver提供了一种非常无缝、用户友好且代码友好的方法来使用各种浏览器进行自动化。由于它支持大多数主要浏览器供应商，因此只需使用相应的浏览器驱动程序和浏览器并设置 Selenium 以使用它们即可。

对于任何 Selenium 测试脚本，一般有以下 7 个步骤，适用于所有测试用例和所有被测应用程序 ( AUT )：

1.  创建特定于浏览器的 WebDriver 实例：



-   例如：要创建 Firefox 驱动程序的实例，我们可以使用以下命令：

```
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
WebDriver driver = new FirefoxDriver();
```

1.  导航到需要自动化的所需网页：

-   例如：要导航到[“https://demoqa.com/text-box”，](https://demoqa.com/text-box)我们可以使用以下命令：

```
driver.get("https://demoqa.com/text-box")
```

1.  在网页上找到一个 HTML 元素：

-   为了与网页进行交互，我们需要在网页上定位 HTML 元素。[我们可以使用“Selenium Locators”](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)中提到的任何元素定位器策略。eg：如果我们想获取“ Full Name ”文本框，可以使用如下命令：

```
import org.openqa.selenium.By; 
import org.openqa.selenium.WebElement; 
WebElement usernameElement = driver.findElement(By.id("userName"));
```

1.  对 HTML 元素执行操作：

我们可以对 HTML 元素执行某些操作，例如使用SendKeys方法键入内容，如果元素是按钮则单击该元素。eg：如果我们想在标识的文本框中输入姓名，可以使用如下命令：

```
usernameElement.sendKeys("Ravinder Singh");
```

1.  使用测试框架运行测试并记录测试结果。

而且，我们已经完成了使用WebDriver来识别 Web 应用程序并在 Web 应用程序上执行所需的操作。根据我们需要测试应用程序的浏览器，我们可以使用相应的WebDriver。

以下是各种浏览器及其各自的浏览器驱动程序的列表：

![WebDriver 支持的驱动程序](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Drivers%20Supported%20by%20WebDriver.png)

最近，Microsoft 将他们的 Edge 浏览器移到了与 Chromium( Chrome 的父级)相同的平台上，因此ChromeDriver现在也可以支持Microsoft Edge Chromium。

## 常见问题

Selenium 和 Selenium WebDriver 有什么区别？

尽管Selenium和Selenium WebDriver是单一生态系统的一部分，但它们有一定的区别。让我们来看看 -

-   Selenium 不是一个单一的工具，它是由各种独立的工具组成的框架，如 Selenium IDE、Selenium RC、WebDriver 和 Selenium Grid。另一方面，WebDriver 是一个单一的工具，它是 Selenium 的一个组件。
-   Selenium 支持并为可以编写基于代码的自动化以及无代码自动化的 QA 提供结构和工具。WebDriver 仅提供基于代码的自动化机制。

Selenium WebDriver 是一个框架吗？

框架的一般定义是它提供了一组不同的插件、库、编译器、软件程序、API 等，有助于在一个平台内构建整个软件。按照这个定义，它是一个巨大的组件，可以在其内部处理软件开发生命周期的多个组件。

Selenium WebDriver 本质上是一组 API，可帮助测试人员或 QA 在浏览器上实现模拟用户操作。它巧妙地做到了这一点，但缺少上面定义的大部分属性。因此 WebDriver 不是一个框架。但是，如果我们谈论 Selenium 及其所有组件，那么我们将称它们为框架。

Selenium WebDriver 是一个工具吗？

没有一个正确的工具定义。大多数时候，在软件开发中，人们将工具与框架混合在一起。框架支持一系列功能，提供单独的选项，并且本质上更广泛。然而，工具具有明确定义的功能范围，旨在执行专门的任务。

谈到 WebDriver，它具有定义明确的用户操作自动化范围。例如，单击、双击、选择菜单项、下拉菜单等。它可以与 TestNG 或 pyTest 等测试运行器结合以增加功能。

所以 Selenium WebDriver 可以粗略地归类为一个工具，尽管它不是一个独立的工具，而不是一个框架。它可以结合各种其他工具、组件、库和测试运行器来创建可扩展的测试框架。

## 关键要点

-   Selenium WebDriver 是 Selenium 家族的一个组件。它在系统中的引入克服了 Selenium RC 的一些缺点。
-   Selenium WebDriver 是一组 API，使得在浏览器上的交互和操作非常简单快捷。
-   Selenium WebDriver 提供了很多独特的功能，比如它可以自动化动态网页。此外，它可以自动化所有 Web 应用程序，无论我们使用哪种编程语言来开发它们。
-   Selenium WebDriver 借助相应供应商提供的各种驱动程序与浏览器进行交互。