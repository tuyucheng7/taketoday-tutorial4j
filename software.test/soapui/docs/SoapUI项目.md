众所周知，项目是一组定义的步骤，可以执行这些步骤以实现可衡量的结果。以同样的方式，为了测试任何系统，我们需要定义一组操作，当以特定顺序执行时，这些操作将导致被测系统的验证和验证。每个自动化工具都提供了一种特定的方式来定义和分组这些测试步骤，这是该工具中的一个项目。同样，SoapUI 项目定义了一组特定的命令或步骤来完成系统测试。SoapUI 作为自动化的标准工具，支持测试各种类型的 API 和 Web 服务，因此支持多种类型的项目，我们将通过本文的以下主题来尝试理解：

-   SoapUI 支持哪些类型的项目？
    -   SoapUI 中的 SOAP 项目是什么？
    -   另外，SoapUI 中的 REST 项目是什么？
    -   SoapUI 中的通用项目是什么？
-   SoapUI 项目遵循什么层次结构？
    -   如何在 SoapUI 中创建一个新的工作区？
    -   SoapUI如何在工作空间下添加项目？

## SoapUI 支持哪些类型的项目？

根据要测试的服务类型，你可以在 SoapUI 中创建以下类型的项目：

![SoapUI 中的项目类型](https://www.toolsqa.com/gallery/SoapUI/1.Type%20of%20projects%20in%20SoapUI.png)

让我们在以下部分中了解所有这些类型的项目的详细信息：

### SoapUI 中的 SOAP 项目是什么？

SOAP 项目基于 SOAP 协议测试 Web 服务。你可以在 SOAP 项目中导入 WSDL，它将列出该 WSDL 公开的所有请求(s) 或端点。你可以使用 SoapUI 对这些服务执行功能和非功能(负载等)测试，还可以验证各种标准，例如 WS-Security、WS-Addressing 等。在创建 SOAP 项目之前，让我们先了解什么是 WSDL是？

#### 什么是 WSDL？

Web 服务描述语言(WSDL)是一种基于 XML 的语言，用于指定基于 SOAP 的 Web 服务的功能。WSDL 定义了服务提供者和消费者之间的契约。下图展示了WSDL通信的基本用法：

![WSDL通信](https://www.toolsqa.com/gallery/SoapUI/2.WSDL%20communication.png)

从上图中我们可以看出，客户端应用程序和 Web 服务共享一个 WSDL 格式的契约，并使用基于 SOAP 的消息相互通信。

#### 如何在 SoapUI 中创建 SOAP 项目？



执行以下步骤以在 SoapUI 中使用 WSDL ( [http://bookstore.toolsqa.com/BookStoreService.wsdl ) 创建一个新的 SOAP 项目：](https://bookstore.toolsqa.com/BookStoreService.wsdl)

1.  单击“文件”菜单中的“新建 SOAP 项目”

![SoapUI 中的新 SOAP 项目](https://www.toolsqa.com/gallery/SoapUI/3.New%20SOAP%20Project%20in%20SoapUI.png)

或工具栏上的“ SOAP ”图标，如下突出显示：

![SoapUI 中的新 SOAP 项目](https://www.toolsqa.com/gallery/SoapUI/4.New%20SOAP%20project%20in%20SoapUI.png)

1.  在“Initial WSDL”部分指定 WSDL( [http://bookstore.toolsqa.com/BookStoreService.wsdl](https://bookstore.toolsqa.com/BookStoreService.wsdl) )的路径：

![SoapUI 中的新 SOAP 项目配置](https://www.toolsqa.com/gallery/SoapUI/5.New%20SOAP%20Project%20configurations%20in%20SoapUI.png)

1.  单击“确定”按钮将导入 WSDL 并显示服务公开的方法，如下突出显示：

![SOAP Web 服务公开的方法](https://www.toolsqa.com/gallery/SoapUI/6.Methods%20exposed%20by%20SOAP%20Webservice.png)

所以，现在我们在 SoapUI 中创建了一个名为“ BookStoreService ”的新 SOAP 项目，它可以为每个公开的方法编写测试用例。

注意：我们将在以后的文章中介绍 SOAP 服务方法的“编写测试用例”。

### SoapUI 中的 REST 项目是什么？

如果你的 Web 服务公开了 RESTful (表述性状态传输)端点，你将需要在 SoapUI 中创建一个[REST项目来测试 REST Web 服务。](https://www.toolsqa.com/rest-assured/what-is-rest/)你可以通过导入 WADL 文件或直接指定 URI 及其参数在 SoapUI 中创建 REST 项目。与 SOAP 项目类似，你可以使用 SoapUI 执行 REST API 的功能和非功能测试。在创建 SOAP 项目之前，我们先了解一下什么是 WADL？

#### 什么是 WADL？

Web 应用程序描述语言 (WADL)是对[基于 HTTP 的](https://www.toolsqa.com/client-server/client-server-architecture-and-http-protocol/)Web 服务的机器可读 XML 解释。WADL对服务提供的资源以及它们之间的关系进行建模。WADL旨在简化基于 Web 现有 HTTP 体系结构的 Web 服务的重用。下图显示了使用 WADL 的客户端和服务器之间的通信：

![WADL 宁静的沟通](https://www.toolsqa.com/gallery/SoapUI/7.WADL%20Restful%20communication.png)

从上图我们可以看出，在 RESTful 服务的情况下，使用 Http 协议，数据交换可以通过 JSON 或 XML 等格式进行。

#### 如何在 SoapUI 中创建 REST 项目？



执行以下步骤以在 SoapUI 中使用 Web 服务 ( [http://bookstore.toolsqa.com/BookStore/v1/Books ) 创建一个新的 REST 项目：](https://bookstore.toolsqa.com/BookStore/v1/Books)

1.  首先，单击“文件”菜单中的“新建 REST 项目”。

![SoapUI 中的新 Rest 项目](https://www.toolsqa.com/gallery/SoapUI/8.New%20Rest%20Project%20in%20SoapUI.png)

或者单击工具栏上的“ REST ”图标，如下突出显示：

![SoapUI 中的新 Rest 项目](https://www.toolsqa.com/gallery/SoapUI/9.New%20Rest%20Project%20in%20SoapUI.png)

1.  在“ URI ”部分指定 REST Web 服务 ( [http://bookstore.toolsqa.com/BookStore/v1/Books ) 的路径：](https://bookstore.toolsqa.com/BookStore/v1/Books)

![指定 Rest 服务的 URI](https://www.toolsqa.com/gallery/SoapUI/10.Specify%20URI%20for%20Rest%20Service.png)

1.  单击“确定”按钮将导入服务并显示服务公开的方法，如下高亮显示：

![SoapUI 中的休息项目](https://www.toolsqa.com/gallery/SoapUI/11.Rest%20Project%20in%20SoapUI.png)

所以，现在我们在 SoapUI 中创建了一个名为“ REST Project 1 ”的新 REST 项目，它可以为每个公开的方法编写测试用例。

注意：我们将在以后的文章中介绍 REST 服务方法的“编写测试用例”。

### SoapUI 中的通用项目是什么？

如果你的应用程序公开了 SOAP 和基于 REST 的服务，你可以在 SoapUI 中创建一个通用项目。它是 SOAP 和 REST 的组合。让我们了解如何在 SoapUI中创建“通用”项目：

#### 如何在 SoapUI 中创建通用项目？

执行以下步骤在 SoapUI 中创建一个新的通用项目

1.  首先，点击“文件”菜单中的“新建空项目”

![SoapUI 中的新空项目----](https://www.toolsqa.com/gallery/SoapUI/12.New%20Empty%20Project%20in%20SoapUI%20----.png)

或者单击工具栏上的“空”图标，如下突出显示：

![在 SoapUI 中创建空项目](https://www.toolsqa.com/gallery/SoapUI/13.Create%20Empty%20Project%20in%20SoapUI.png)

1.  它将创建一个空项目“ Project1 ”并显示如下弹出窗口：

![SoapUI 中的空项目详细信息](https://www.toolsqa.com/gallery/SoapUI/14.Empty%20project%20details%20in%20SoapUI.png)

在哪里，

1.  概览选项卡：显示项目的一般数据。
2.  TestSuites：它显示了项目中所有的功能测试套件。
3.  WS-Security 配置：它显示了基于 SOAP 的 Web 服务的项目级 WS-Security 配置。
4.  安全扫描默认值：它显示项目的敏感信息令牌。
5.  现在要向项目添加 SOAP 服务，请右键单击“项目”部分中的“ Project1 ”，然后单击“添加 WSDL ”。

![将新的 WSDL(SOAP URL)添加到 SoapUI 中的空项目](https://www.toolsqa.com/gallery/SoapUI/15.Adding%20a%20new%20WSDL%20(SOAP%20URL)%20to%20Empty%20Project%20in%20SoapUI.png)

1.  在“ WSDL Location ”部分指定 WSDL ( [http://bookstore.toolsqa.com/BookStoreService.wsdl](https://bookstore.toolsqa.com/BookStoreService.wsdl) )的路径，如下所示：

![将 WSDL 添加到 SoapUI 中的通用项目](https://www.toolsqa.com/gallery/SoapUI/16.Add%20WSDL%20to%20generic%20project%20in%20SoapUI.png)

1.  点击“确定”按钮，就会导入Project1下的SOAP服务，如下图

![在 SoapUI 中为 SOAP 服务导入 WSDL 后的通用项目结构](https://www.toolsqa.com/gallery/SoapUI/17.Structure%20of%20Generic%20project%20after%20importing%20WSDL%20for%20SOAP%20Service%20in%20SoapUI.png)

1.  现在再次右键单击“ Project1 ”并单击“从 URI 导入 Rest 服务”以将 Rest 服务添加到同一项目：

![从 URI 导入 web 服务](https://www.toolsqa.com/gallery/SoapUI/18.Import%20webservice%20from%20URI.png)

1.  在“ URI ”部分指定 REST Web 服务 ( [http://bookstore.toolsqa.com/BookStore/v1/Books ) 的路径：](https://bookstore.toolsqa.com/BookStore/v1/Books)

![将新的 webrvicee 添加到通用 SoapUI 项目](https://www.toolsqa.com/gallery/SoapUI/19.Add%20New%20webrvicee%20to%20generic%20SoapUI%20project.png)

1.  单击“确定”按钮，你将在同一项目“ Project1 ”下导入一个新的“服务” ，如下所示：

![REST 和 SOAP 都导入到 SoapUI 的一个项目中](https://www.toolsqa.com/gallery/SoapUI/20.REST%20and%20SOAP%20both%20imported%20in%20one%20project%20in%20SoapUI.png)

从上图中我们可以看到，SOAP 和 REST 服务都已导入到同一个项目“ Project1 ”下。

## SoapUI 项目遵循什么层次结构？

与所有标准自动化工具一样，SoapUI 还提供通用建议以将测试用例安排在特定的测试套件和项目层次结构中。示例层次结构在 SoapUI 中表示如下：

![SoapUI 中的项目层次结构](https://www.toolsqa.com/gallery/SoapUI/21.Project%20hierarchy%20in%20SoapUI.png)

从上图我们可以看出，一个SoapUI工作空间可以包含多个项目，每个项目可以有各种TestSuites，每个TestSuite可以有多个Test case，每个Testcase可以有无数个Test Steps。让我们了解所有这些术语的含义：

1.  工作区：工作区是一个占位符，可以容纳一个或多个项目。正如我们在上图中看到的，工作区中有两个项目。
2.  项目：项目包括所有请求、测试、模拟服务等。它是一个 XML 文件。你将在一个项目下安排你的测试用例。
3.  测试套件： 测试套件是各种测试用例的逻辑组。如果你想一次执行一组测试用例，你可以将它们分组在一个单一的测试套件中。
4.  测试用例： 测试用例是一组为实现/测试特定功能而分组的步骤。
5.  测试步骤：测试步骤是基本的构建块，代表一个独特的动作。按顺序执行的多个测试步骤构成一个测试用例。

让我们从头开始，了解如何在 SoapUI 中添加新的工作区：

### 如何在 SoapUI 中创建一个新的工作区？

当你在 SoapUI 中创建一个新项目时，它会添加一个名为“ Projects ”的默认工作区，并且新创建的项目将始终在该工作区下创建。但是如果你想在不同的工作空间中维护你的不同项目，你可以按照下面提到的步骤创建一个新的工作空间：

1.  单击“文件”菜单中的“新建工作区”选项，如下所示：

![SoapUI 中的新工作区](https://www.toolsqa.com/gallery/SoapUI/22.New%20workspace%20in%20SoapUI.png)

1.  它将打开一个弹出窗口。指定工作空间的名称，如下图：

![在 SoapUI 中指定工作区的名称](https://www.toolsqa.com/gallery/SoapUI/23.Specify%20names%20of%20workspace%20in%20SoapUI.png)

1.  点击“确定”按钮，将作为工作区的保存路径，如下图：

![在 SoapUI 中指定新工作区的路径](https://www.toolsqa.com/gallery/SoapUI/24.Specify%20path%20for%20new%20workspace%20in%20SoapUI.png)

1.  指定要保存新创建的工作区的文件夹，然后单击“保存”按钮。它将添加一个名为“ ToolsQA Workspace ”的新工作区，如下所示：

![SoapUI 中新创建的工作区](https://www.toolsqa.com/gallery/SoapUI/25.Newly%20created%20workspace%20in%20SoapUI.webp)

所以，现在我们在 SoapUI 中添加了一个新的工作区，我们可以在这个工作区下添加我们的项目。让我们在以下部分了解如何在这个新创建的框架下添加项目：

### SoapUI如何在工作空间下添加项目？

有两种方法可以将项目添加到工作区。

-   在创建新项目时将项目添加到工作区。
-   通过导入现有项目将项目添加到工作区。

让我们在以下部分中了解这两种方式的详细信息：

#### 如何在创建新项目时将项目添加到工作区？

将新项目添加到命名工作区(在上一节中创建) 可以按照与默认工作区相同的方式完成。因此，在创建工作区后，如果我们按照相同的步骤(如上文“如何创建 SOAP/REST/通用项目”部分所述)，它会自动将新创建的项目仅添加到工作区下。因此，理想情况下，无需明确的步骤即可在工作空间下添加新创建的项目，每当我们创建新项目时，它都会自动添加。

#### 如何通过导入现有项目将项目添加到工作区？

如果你已经有一个项目要导入到这个新创建的工作区中，请按照以下步骤操作：

1.  点击“文件”菜单下的“导入工程”选项，如下图：

![在 SoapUI 工作区中导入项目](https://www.toolsqa.com/gallery/SoapUI/26.Import%20project%20in%20SoapUI%20workspace.webp)

1.  选择要导入的项目。在我们的场景中，我们将导入在上述步骤中创建的“ Book Store SOAP 项目”：

![在 SoapUI 的新工作区中选择要导入的项目](https://www.toolsqa.com/gallery/SoapUI/27.Select%20project%20to%20import%20in%20new%20workspace%20in%20SoapUI.webp)

1.  单击“打开”按钮，将在新工作区中导入项目，如下图所示：

![在 SoapUI 的新工作区中新导入的项目](https://www.toolsqa.com/gallery/SoapUI/28.Newly%20imported%20project%20in%20new%20workspace%20in%20SoapUI.webp)

正如我们在上面的屏幕截图中看到的，SOAP 项目已成功导入到新工作区“ ToolQA Workspace ”中。

## 关键要点

-   SoapUI 支持三种类型的项目：SOAP 项目、REST 项目和通用项目。
-   SoapUI 提供了非常直观的 UI 小部件来创建新项目。
-   每个 SoapUI 项目都遵循 Workspace 到 Project 到 TestSuite 到 TestCase 的层次结构。
-   可以在创建新项目时或通过将现有项目导入工作区来将 SoapUI 项目添加到工作区。

所以到目前为止，我们已经了解了 SoapUI 项目和工作区的详细信息。让我们转到下一篇文章来了解[“SoapUI 中的测试用例”](https://www.toolsqa.com/soapui/soapui-test-case/)的详细信息。