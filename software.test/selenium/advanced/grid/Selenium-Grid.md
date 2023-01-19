在当前情况下，技术正在经历快速变化。我们看到大量新软件和大量现有软件经常更新。同样，我们使用的浏览器也在一天天升级。截至 2017 年 3 月，Chrome 浏览器的版本达到了 57。

为了获得更好的用户体验，有必要使用不同的浏览器组合以及同一浏览器的不同版本来验证应用程序。当然，我们可以在我们的盒子上安装浏览器，但是我们会受到限制，因为我们在给定时间只能有一个浏览器版本。此外，不建议在一台机器上管理同一浏览器的不同版本。

问题

如果你希望在不同的浏览器和操作系统组合上运行测试，但你无法证明使用第三方解决方案(如Sauce Labs或Browser Stack )是合理的，那么你会怎么做？

解决方案

使用Selenium Grid ，你可以创建连接测试机(也称为节点)的网络。这个测试机器网络由一个集线器控制，你可以使用它在不同的连接节点上运行测试。每个节点基本上都是一台结合了操作系统和浏览器的计算机(甚至是虚拟机)。这使我们能够创建具有不同操作系统和浏览器组合的测试机网络。使用 Selenium Grid，你可以在各种操作系统和浏览器组合上运行测试。让我们在下面更详细地了解 Selenium Grid。

## 什么是硒网格？

Selenium Grid是一个测试工具，它允许我们在不同的机器上针对不同的浏览器运行我们的测试。它是 Selenium Suite 的一部分，专门用于在不同的浏览器、操作系统和机器上运行多个测试。你可以通过指定所需的浏览器、浏览器版本和操作系统，使用 Selenium Remote 连接到它。你通过Selenium Remote 的 Capabilities指定这些值。

Selenium Grid 有两个主要元素——集线器和节点。

### 什么是集线器？

在 Selenium Grid 中，集线器是一台计算机，它是我们可以将测试加载到的中心点。集线器还充当服务器，因此它充当控制测试机网络的中心点。Selenium Grid 只有一个集线器，它是网络的主人。当向集线器提供具有给定 DesiredCapabilities 的测试时，集线器搜索与给定配置匹配的节点。例如，你可以说要在 Windows 10 和版本 XXX 的 Chrome 浏览器上运行测试。Hub 将尝试在 Grid 中找到符合条件的机器，并将在该机器上运行测试。如果没有匹配项，则集线器返回错误。一个网格中应该只有一个集线器。

### 什么是节点？

在 Selenium Grid 中， 节点指的是选择与集线器连接的测试机。Hub 将使用此测试机来运行测试。一个网格网络可以有多个节点。一个节点应该有不同的平台，即不同的操作系统和浏览器。节点不需要与集线器运行相同的平台。

### 这个怎么运作？

首先，你需要创建一个集线器。然后你可以将节点连接(或“注册” )到该集线器。节点是你的测试将运行的地方，而集线器负责确保你的测试在正确的节点上结束(例如，具有你在测试中指定的操作系统和浏览器的机器)。

### 网格 1 和网格 2 有什么区别？

Selenium Grid 有两个版本——旧的 Grid 1 和新的 Grid 2。

| 网格 1                                            | 网格 2                                     |
| ------------------------------------------------------- | ------------------------------------------------ |
| 它有自己的远程控制，这与 Selenium RC 服务器不同。     | 它包含在 Selenium Server jar 文件中。          |
| 它只能支持 Selenium RC 命令。                         | 它可以同时支持 Selenium RC 和 WebDriver 脚本。 |
| 在使用 Grid 1 之前，我们需要先安装和配置 Apache Ant。 | 在此我们不需要在 Grid 2 中安装 Apache Ant。    |
| 每个遥控器我们只能自动化一个浏览器。                  | 在这一个遥控器中最多可以自动化 5 个浏览器。    |

## 为什么使用硒网格？

使用 Selenium Grid ，你可以在不同操作系统上创建各种浏览器的简单基础架构，不仅可以分配测试负载，还可以为你提供多种浏览器。

使用 Selenium Grid 的原因有很多。这里有一些

-   当我们想要针对多个浏览器、多个版本的浏览器以及运行在不同操作系统上的浏览器运行测试时。
-   它还用于通过并行运行测试来减少测试套件完成测试通过所花费的时间。

## 架构和 RemoteWebDriver 工作流程

你可以像在本地使用 WebDriver 一样使用 RemoteWebDriver。主要区别在于 RemoteWebDriver 需要配置，以便它可以在单独的机器上运行你的测试。RemoteWebDriver 由两部分组成：客户端和服务器。客户端是你的 WebDriver 测试，服务器只是一个 Java servlet，可以托管在任何现代 JEE 应用程序服务器中。

-   RemoteWebDriver 是 WebDriver 接口的一个实现类，测试脚本开发人员可以使用它通过远程计算机上的 RemoteWebDriver 服务器执行他们的测试脚本。

-   RemoteWebDriver 有两部分：服务器(

    集线器

    )和客户端(

    节点

    )

    -   RemoteWebDriverserver 是一个组件，它在一个端口上侦听来自 RemoteWebDriver 的各种请求。一旦它收到请求，它就会将它们转发给以下任何一个：Firefox 驱动程序、IE 驱动程序或 Chrome 驱动程序，以请求为准。
    -   用作 RemoteWebDriver 的语言绑定客户端库 客户端，就像它在本地执行测试时一样，将你的测试脚本请求转换为 JSON 有效负载，并使用 JSON 有线协议将它们发送到 RemoteWebDriver 服务器。

-   当你在本地执行测试时，WebDriver 客户端库直接与你的 Firefox 驱动程序、IE 驱动程序或 Chrome 驱动程序对话。现在，当你尝试远程执行测试时，WebDriver 客户端库会与 RemoteWebDriver 服务器对话，而服务器会与 Firefox Driver、IE Driver 或 Chrome Driver 对话，这取决于 WebDriver 客户端的请求。

![Selenium 网格架构](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Selenium%20Grid%20Architecture.png)

我们将在本教程中逐步执行此操作，但首先让我们看一下整体架构图，了解工作流是如何发生的。下面是一些行话。

-   JSON over wire：JSON 消息包含服务器所需的全部信息 [可以是 Selenium GRID 或 WebDriver 组件]，因此代替 RPC，纯 JSON 消息在服务器和客户端之间交换。也称为 WebDriver 协议
-   所需功能：自动化代码中具有请求浏览器配置信息的对象
-   RemoteWebDriver：自动化代码中的对象，知道如何与 WebDriver / Selenium GRID 进行远程通信

有关[Selenium 教程](https://toolsqa.com/selenium-webdriver/selenium-tutorial/)的更多更新，请[订阅](https://feedburner.google.com/fb/a/mailverify?uri=ToolsQA) 我们的时事通讯。