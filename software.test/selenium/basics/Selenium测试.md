我们都知道，软件测试是SDLC不可或缺的一部分，有时会涉及到很多重复性的工作，一遍又一遍地梳理场景。现在，为了减少这些重复性工作，软件测试人员需要一些工具的帮助，这些工具可以使他们自动执行这些重复性任务。Selenium是出色的自动化工具套件之一，它提供了在 Web 应用程序上自动执行用户操作的功能。它是一个开放源代码的综合项目，包含一系列启用和支持 Web 浏览器自动化的工具和库。让我们在本文中通过涵盖以下主题的详细信息来了解Selenium 自动化测试的所有复杂性：

-   什么是硒？
-   硒是如何起源的？
-   硒的各种成分是什么？
-   硒集成开发环境
-   硒钢筋混凝土
-   Selenium WebDriver (Selenium 4)
-   硒网格
-   为什么硒受欢迎？
-   硒能做什么？
-   Selenium 不能做什么？
-   而且，学习Selenium的先决条件是什么？
-   哪种 Selenium 工具适合你的需要？

## 什么是硒测试？

正如我们所讨论的，Selenium 是一组开源 Web 自动化工具，它利用 Web 浏览器的强大功能，并有助于自动化用户如何在浏览器中与 Web 应用程序交互的工作流。[正如Selenium 网站](https://www.selenium.dev/)所强调的那样，Selenium 的主要目的是——“ Selenium 使浏览器自动化，你可以用这种力量做什么完全取决于你”。即使使用新兴的工具和技术，Selenium 仍然在 Web 自动化工具和自动化测试列表中处于领先地位。以下[由 Katalon 进行的调查](https://www.katalon.com/resources-center/blog/infographic-challenges-test-automation/)描述了 Selenium 的受欢迎程度：

![Katalon 测试自动化工具调查](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Katalon%20survey%20for%20test%20automation%20tools.png)

如此流行的工具和如此广泛的受众 在了解 Selenium 套件的更多细节之前，让我们先了解一下 Selenium 是如何诞生的，它的起源是什么：

### 硒是如何起源的？

Selenium 的历史可以追溯到 2000 年代初期。[ThoughtWorks](https://www.thoughtworks.com/)的工程师Jason Huggins创建了一个[JavaScript](https://www.toolsqa.com/javascript/what-is-javascript/)模块。它的名字是JavaScriptTestRunner，用于内部网站的自动化。硒的名字来源于哈金斯的一个笑话，他说“你可以通过服用硒补充剂来治愈汞中毒”。尽管这只是一个嘲笑名为[Mercury 的竞争对手的笑话，](https://en.wikipedia.org/wiki/Mercury_Interactive)但其他收到包含该笑话的电子邮件的人还是取了这个名字并使用了它。下图展示了 Selenium 这些年来的演变：

![多年来硒的演变](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Selenium%20evolution%20over%20the%20years.png)

Selenium4 仍处于测试版，开发仍在进行中。



## 硒的各种成分是什么？

正如我们上面所讨论的，Selenium 不仅仅是一个自动化工具。它是一套工具，套件中的每个工具都具有特定的独特功能，有助于设计和开发自动化框架。所有这些组件都可以单独使用，也可以相互配对以实现一定程度的测试自动化。

下图显示了Selenium Suite 的各个组件：

![硒成分](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Selenium%20components.png)

让我们更详细地了解所有这些组件：

### 什么是硒集成开发环境？

Selenium IDE是一个可用于 Firefox 和 Chrome 的扩展，它具有可用的记录和重放功能。Selenium IDE现在还能够以各种预定义语言导出代码。它还提供了在另一个测试用例中使用一个测试用例的能力。

### 什么是硒 RC？



[Selenium RC](https://selenium.dev/documentation/en/legacy_docs/selenium_rc/)是一个服务器，充当用户和需要交互的浏览器之间的中间人。RC 使用 Javascript 与浏览器一起工作，同时允许用户用他们选择的语言编写代码。有一段时间它是 Selenium 的主要版本。Selenium RC 在[One Origin Policy](https://developer.mozilla.org/en-US/docs/Web/Security/Same-origin_policy)方面存在问题，并弃用了 WebDriver。

### 什么是 Selenium WebDriver？

[Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/selenium-tutorial/)是 Selenium 最常用的组件。WebDriver 允许用户使用他们选择的语言编写自定义代码，并通过特定于浏览器的驱动程序与他们选择的浏览器进行交互。WebDriver 在操作系统级别工作，并使用名为[JSONWireProtocol](https://github.com/SeleniumHQ/selenium/wiki/JsonWireProtocol)的协议与浏览器进行通信。

如上所示，Selenium 2是WebDriver 和 RC项目合并的实际实现。WebDriver 和 RC 的特性被吸收在 WebDriver 的版本 2 中，称为Selenium 2。

Selenium 3在许多方面都是对 Selenium 2 的升级。Selenium 3成为万维网联盟 (W3C)标准。Selenium RC移至遗留包，并添加了许多改进和新功能以适应不断变化的网络环境。

此外， Selenium 4是该家族的新成员(selenium 最新版本)，仍处于面向最终用户的测试阶段。

### 什么是硒网格？

[Selenium GRID](https://www.toolsqa.com/selenium-webdriver/selenium-grid/)允许用户在不同的机器上运行测试，同时使用不同的浏览器和操作系统，这提供了并行运行测试的能力，因此节省了在多台机器上进行测试的大量时间和资源。





## 为什么 Selenium 流行以及 selenium 的用途是什么？

Selenium 已成为最流行的 Web 应用程序自动化工具之一，背后有一个庞大的社区支持。谷歌、苹果、亚马逊和许多其他财富 500 强公司等大型跨国巨头每天都在使用 Selenium，甚至为该工具提供支持。最初的 Selenium 贡献者仍在积极地持续贡献，这使得 Selenium 能够及时应对现代 Web 应用程序测试的挑战。

许多顶级公司将[LambdaTest](https://www.lambdatest.com/selenium-automation?utm_source=toolsqa&utm_medium=listing&utm_campaign=paid-listing&utm_term=selenium-testing)与 Selenium 一起用于跨浏览器测试。

此外，正如我们所讨论的，Selenium 工具套件包含不同的组件，如Selenium IDE、WebDriver、Grid等，可用于跨不同浏览器、平台和编程语言的自动化。Selenium 还支持所有主要操作系统，如macOS、Windows、Linux，并支持移动操作系统，如 iOS、Android。下图显示了一些归因于 Selenium 流行的标准功能：

![Selenium 人气贡献者](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Selenium%20popularity%20contributors.png)

除了这个视野之外，让我们了解一下 Selenium 的能力是什么？



### 硒能做什么？

Selenium 提供了很多功能，其特性使其成为市场上最受欢迎的自动化工具之一。让我们快速了解一下这些功能：

-   开源——与 QTP 等工具不同，Selenium 是开源的，这意味着设置和使用 Selenium 不需要任何成本。Selenium 可以免费下载和使用。
-   模仿用户操作——几乎所有真实世界的用户操作，如按钮单击、拖放选择、复选框、按键、点击、滚动都可以使用 Selenium 自动执行。
-   易于实施——Selenium 以用户友好着称。由于代码是开源的，用户可以开发自定义扩展供他们使用。
-   语言支持——Selenium 最显着的好处是对各种语言的广泛支持。Selenium 支持最多的编程语言，如 Java、Python、JavaScript、C#、Ruby、Perl、Haskell、Go 等。
-   浏览器支持——Selenium 可以在所有现有的浏览器供应商上运行。Selenium 支持 Chrome、Firefox、Edge、Internet Explorer、Safari。
-   操作系统支持——Selenium 绑定可用于所有主要操作系统，如 Linux、macOS、Windows。
-   框架支持——Selenium 支持多种框架，如 Maven、TestNG、PYTest、NUnit、Mocha、Jasmine 等。Selenium 与 Jenkins、Circle CI、GOCD、Travis CI、Gitlab 等 CI 工具集成得很好。
-   代码可重用性——为 Selenium 编写的脚本是跨浏览器兼容的。可以使用各自的浏览器二进制文件在网格配置中的不同机器上为多个浏览器运行相同的代码，
-   社区支持——因为有很多质量检查人员在使用这个工具，所以很容易在 Github、StackOverflow 等社区上找到资源、教程和支持。



除了上面提到的功能之外，正如我们所讨论的，Selenium 还支持各种网络浏览器、编程语言和操作系统。下图显示了其中的一些：

![Selenium 的编程语言浏览器和操作系统支持](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Programming%20language%20browser%20and%20OS%20support%20for%20Selenium.png)

这不是一个详尽的列表，但肯定给出了有关 Selenium 在整个平台上提供的广泛支持的基本概念。现在，除了这些功能之外，还有一些 Selenium 不具备的限制。让我们了解一下 Selenium 无法解决的限制是什么：

### Selenium 不能做什么？

尽管 Selenium 可以帮助实现许多用户操作的自动化，但它也有一定的局限性。其中很少有：

-   不支持基于本地桌面的应用程序自动化——Selenium 可用于自动化在网络浏览器上运行的基于网络的应用程序。但是，它不能自动化基于桌面的应用程序。
-   不支持断言和有效性——Selenium 提供对浏览器的控制；但是，Selenium 不提供断言和检查机制。Selenium 需要与 JUnit、TestNG、PyTest 等测试框架配对以进行断言。
-   不支持图像和代码扫描- 使用 Selenium 无法实现代码扫描场景的自动化，例如条形码读取、CAPTCHA。
-   不支持 API 测试- Selenium 模仿浏览器上的用户操作。因此，Selenium 不提供测试 API 的能力。
-   不支持性能测试——Selenium 无法执行 Web 应用程序的性能检查或性能测试。
-   没有内置报告——Selenium也不提供报告功能。也就是说; 报告只能通过将其与 JUnit、TestNG 等框架配对来完成。





## 学习Selenium的先决条件是什么？

尽管使用 Selenium 学习和启动 Web 应用程序的自动化很简单，但在开始学习 Selenium 之前确保用户了解以下概念总是有用的：

-   用户了解手动测试的基础知识
-   使用 Selenium 支持的编程语言进行编码的基本知识。
-   用户具有 HTML、CSS 的基础知识。
-   此外，他们还具备 XML 和 JSON 的基本知识。
-   此外，他们了解 DOM 并使用 DOM 中的定位器识别 Web 元素。

然而，没有必要知道所有这些。ToolsQA 专门设计了他们的[Selenium 教程](https://www.toolsqa.com/selenium-webdriver/selenium-tutorial/)，新手可以快速上手并学习自动化。通过这些 Selenium WebDriver 教程，你应该能够开始你的 Selenium 测试之旅。事实上，如果你想被称为 Selenium Professional，我们建议你也参加[Selenium 认证](https://www.toolsqa.com/selenium-training/)。

在开始使用 Selenium 套件之前，第一个困惑是哪种 Selenium 工具需要用于哪种类型的场景。让我们了解一些特定的 Selenium 工具可以满足需求的情况：



## 哪种 Selenium 测试工具适合你的需要？

由于 Selenium 有多个组件，对于 Selenium 新手来说，决定使用哪个部分来满足他们的自动化需求可能会造成混淆。你可以使用以下指南来选择最适合你需要的 Selenium 组件：

-   硒集成开发环境
    -   如果你想了解自动化测试和 Selenium。
    -   如果你以前很少或没有测试自动化经验。
    -   如果你想编写简单的测试用例并在以后为 RC 或 WebDriver 导出它们。
    -   如果你想使用 runScript 执行定制的 JavaScript 代码
    -   如果你想导出各种语言的脚本。
    -   如果你想针对 Chrome 和 Firefox 进行测试。
-   硒钢筋混凝土
    -   如果你想用比 IDE 更具表现力的语言编写测试用例。
    -   如果你想针对支持 JavaScript 的新浏览器测试应用程序。
    -   或者，如果你想测试 AJAX 繁重的应用程序。
-   Selenium WebDriver
    -   如果你想为你的自动化测试用例使用特定的编程语言。
    -   如果你想使用 Selenium Grid 在不同平台上测试应用程序
    -   或者，如果你想在 CI/CD 中测试应用程序。
    -   如果你想测试应用程序并生成自定义的 HTML 格式的报告。
    -   如果你想测试现代动态数据密集型网站。
-   硒网格
    -   如果你想在不同的浏览器和操作系统中同时运行你的自动化测试用例。
    -   如果你想运行一个庞大的测试套件并希望最小化执行时间。

## 关键要点

-   Selenium 是一种通用的 Web 自动化工具，可满足 Web 应用程序自动化的重要需求。
-   此外，Selenium 是一套由各种组件组成的工具 - IDE、RC、WebDriver、GRID。
-   Selenium WebDriver 是 Selenium 套件中最受欢迎的工具。
-   除了上述之外，Selenium 流行的主要原因是它的广泛覆盖和对不同操作系统、编程语言和 Web 浏览器的支持。

到现在为止，我们都应该清楚Selenium 测试的基础知识。让我们转到下一篇文章，在那里我们将了解“ Selenium WebDriver ”的详细信息。