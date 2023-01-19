以适当的格式组织测试是所有自动化工具提供的基本功能之一。遵循相同的合成，SoapUI 还提供了一些标准指南，以在定义的层次结构中构建测试用例。[我们已经介绍了在 SoapUI](https://www.toolsqa.com/soapui/soapui-project/)中创建和设置项目的详细信息。随后，在本文中，我们将通过涵盖以下主题下的详细信息来了解如何在 SoapUI 中创建和组织测试用例：

-   SoapUI 中测试用例的结构是怎样的？
    -   什么是 SoapUI 中的测试套件？
    -   同样，SoapUI 中的 TestCase 是什么？
    -   SoapUI 中的 TestStep 是什么？
    -   WebService的所有请求如何生成TestSuite？

## SoapUI 中测试用例的结构是怎样的？

SoapUI 在三层层次结构中构建测试用例，如下所示：

![SoapUI 组织测试套件和测试用例](https://www.toolsqa.com/gallery/SoapUI/1.SoapUI%20Organising%20TestSuites%20and%20TestCases.png)

从上图中可以明显看出，测试用例层次结构中的顶级元素指定“ TestSuite ”。每个TestSuite可以包含多个“ TestCases ”，每个TestCase又可以包含多个“ TestSteps ”。相同的层次结构将在 SoapUI 中表示如下：

![SoapUI 中的 TestSuite 结构](https://www.toolsqa.com/gallery/SoapUI/2.TestSuite%20Structure%20In%20SoapUI.jpg)

在哪里，

-   上面标记 1 的亮点是一个 TestSuite。此外，它下面还有两个测试用例，即 TestCase1 和 TestCase2。
-   类似地，标记 2 突出显示了一个 TestCase，它又带有 TestSteps。
-   并且，标记 3 突出显示了一个 TestStep。

让我们在以下部分中了解详细信息以及如何在 SoapUI 中创建这些测试元素：

### 什么是 SoapUI 中的测试套件？

TestSuite是对 TestCases 进行分组的逻辑单元。此外，一个 SoapUI 项目可以包含任意数量的TestSuite。下图显示了 TestSuite 如何反映 TestCases 的分组：

![测试用例的逻辑分组](https://www.toolsqa.com/gallery/SoapUI/3.Logical%20grouping%20of%20TestCases.png)

让我们在下一节中了解如何在 SoapUI 中创建 TestSuite：

#### 如何在 SoapUI 中创建一个 TestSuite？

要在 SoapUI 中创建一个新的 TestSuite，请按照以下步骤操作：

1.  首先，为 URI：[“http://bookstore.toolsqa.com/BookStoreService.wsdl”](https://bookstore.toolsqa.com/BookStoreService.wsdl)创建一个新的 SOAP 项目，如“ SoapUI：使用项目”一文中所述，并将其命名为“ BookStoreService ”。
2.  其次，右键单击左侧导航栏中的“项目”或单击顶部导航栏中的“项目”菜单。之后，选择“ New TestSuite ”，如下图：

![在 SoapUI 中创建新的测试套件](https://www.toolsqa.com/gallery/SoapUI/4.Creating%20New%20TestSuite%20in%20SoapUI.png)

或者

1.  第三，右键单击项目并选择“显示项目视图”，如下所示：

![显示项目视图](https://www.toolsqa.com/gallery/SoapUI/5.Show%20Project%20View.png)

1.  第四，在“Project View”对话框中，单击“TestSuites”选项卡。之后，点击“Create a new Test Suite”选项，如下图：

![从项目视图对话框创建新的测试套件](https://www.toolsqa.com/gallery/SoapUI/6.Create%20new%20TestSuite%20from%20Project%20View%20Dialog%20Box.png)

1.  之后，上述两种方式都会打开一个对话框，你可以在其中指定 TestSuite 的名称。其如下图：

![指定测试套件的名称](https://www.toolsqa.com/gallery/SoapUI/7.Specifying%20name%20of%20the%20TestSuite.jpg)

1.  最后点击“确定”按钮后，会在SoapUI项目下创建一个空的TestSuite，如下图：

![如何创建空测试套件](https://www.toolsqa.com/gallery/SoapUI/8.How%20to%20create%20Empty%20TestSuite.png)

所以，现在我们在SoapUI 中有一个可用的测试套件。 因此，让我们向测试套件中添加一些测试用例，如下一节所述：

### 什么是 SoapUI 中的测试用例？

测试用例是需要按给定顺序执行的有组织的一系列步骤，以验证被测系统的预期行为。遵循相同的概念，SoapUI 测试用例还指定了一组步骤来验证特定功能。此外，一个 SoapUI TestSuite 可以包含多个 TestCases。让我们在下面的部分中了解，我们如何在 SoapUI 中创建一个 TestCase？

#### 如何在 SoapUI 中创建一个测试用例？

按照下面提到的步骤将 TestCase 添加到上面创建的 TestSuite：

1.  首先，右键单击左侧导航栏中的TestSuite “Functional Test Suite”或单击顶部导航栏中的“Suite”菜单。之后，选择“New TestSuite”，如下图：

![SoapUI 在 SoapUI 中添加一个新的 TestCase](https://www.toolsqa.com/gallery/SoapUI/9.SoapUI%20Adding%20a%20new%20TestCase%20in%20SoapUI.png)

1.  其次，它会打开一个对话框，如下所示，你可以在其中指定 TestCase 的名称：

![在 SoapUI 中指定测试用例的名称](https://www.toolsqa.com/gallery/SoapUI/10.Specify%20name%20of%20the%20Test%20Case%20In%20SoapUI.jpg)

3)第三步，在TestSuite下添加一个新的空TestCase，并打开TestCase对话框，如下图：

![SoapUI 新的测试用例层次结构](https://www.toolsqa.com/gallery/SoapUI/11.SoapUI%20New%20Test%20case%20hierarchy.png)

正如我们在上面的屏幕截图中看到的，它将在右侧面板中打开 TestCase 对话框，如标记 1 所示。此外，我们还可以看到，由标记 2 标记，在测试套件“功能测试套件”下添加了一个名为“ValidateBooksCount”的新测试用例，并且它具有零(0)个测试步骤，即它没有测试步骤。

所以，现在我们在SoapUI中有一个可用的测试用例。 因此，让我们向测试用例添加一些测试步骤，如下一节所述：

### SoapUI 中的 TestStep 是什么？

TestSteps是 SoapUI 中功能测试的“构建块”。这些被添加到 TestCase 并验证需要测试的 Web 服务的功能。随后，让我们在下一节中了解如何将 TestStep 添加到 SoapUI TestSuite：

#### 如何在 SoapUI 中创建 SOAP TestStep？

按照下面提到的步骤将TestStep 添加到上面创建的TestCase：

1.  首先，右击TestCase，点击“Add Step >> SOAP Request”，如下图：

![在 SoapUI 中添加 SOAP 请求测试步骤](https://www.toolsqa.com/gallery/SoapUI/12.Add%20SOAP%20Request%20Test%20Step%20in%20SoapUI.png)

或者你可以通过右键单击“测试步骤”选项来添加相同的内容，如下所示：

![在 SoapUI 中向 SOAP 请求添加测试步骤](https://www.toolsqa.com/gallery/SoapUI/13.Add%20Test%20Step%20to%20SOAP%20Request%20in%20SoapUI.png)

1.  其次，点击“SOAP Request”选项后，将“Test Step”的名称指定为“ValidteSOAPResponse”，如下图：

![在 SoapUI 中指定测试步骤的名称](https://www.toolsqa.com/gallery/SoapUI/14.Specifying%20the%20name%20of%20Test%20Step%20in%20SoapUI.png)

1.  第三，单击“确定”按钮。之后，它将显示一个对话框，其中指定了 SOAP 服务公开的所有函数的列表，如下所示：

![在 SoapUI 中添加测试步骤时操作下拉](https://www.toolsqa.com/gallery/SoapUI/15.Operation%20drop%20down%20while%20adding%20test%20steps%20in%20SoapUI.png)

1.  第四，点击下拉选择Webservice暴露的“Books”端点，如下图：

![在 SoapUI 中添加测试步骤时选择 Web 服务端点](https://www.toolsqa.com/gallery/SoapUI/16.Selecting%20Webservice%20endpoint%20while%20adding%20Test%20Step%20in%20SoapUI.png)

1.  第五，选择“Books”方法后点击“OK”按钮，出现如下对话框，在TestStep中选择需要的断言。

![在 SoapUI 中添加 SOAP 测试步骤时的默认断言](https://www.toolsqa.com/gallery/SoapUI/17.Default%20assertions%20while%20adding%20SOAP%20Test%20Step%20in%20SoapUI.png)

注意：这些是我们在添加“SOAP Request TestStep”时可以添加的一些特定于 SOAP 的断言。此外，我们将在下一篇专门介绍 SoapUI 断言的文章中介绍所有 SOAP 和 REST 断言。

1.  第六，点击“确定”按钮后，新的TestStep (标记1所示)将添加到TestCase下。它将在右侧面板中打开 SOAP 请求的详细信息。之后，右侧面板的底部会显示在上一个对话框中选择的所有断言(如标记 2 所示)。

![SoapUI 显示测试步骤详细信息](https://www.toolsqa.com/gallery/SoapUI/18.SoapUI%20Displaying%20Test%20Step%20details.png)

1.  最后，点击上面截图中的播放按钮(由Marker3表示)，执行TestCase。它将显示结果，如下所示：

![执行测试用例后的测试结果](https://www.toolsqa.com/gallery/SoapUI/19.Test%20Results%20after%20executing%20a%20Test%20Case.png)

正如我们在上面的截图中看到的，所有的断言都显示为绿色，结果为“VALID”，这表明测试用例执行成功。

#### 如何在 SoapUI 中创建 REST TestStep？

现在考虑一个场景，我们必须测试REST服务而不是SOAP服务(我们在上面的 TestSuite 场景中提到过)。对于测试REST服务，最多的步骤与我们为 SOAP 服务所遵循的相同，不同之处在于，我们将为REST服务添加 TestSteps，而不是 SOAP 服务。让我们看看如何在 SoapUI 中实现相同的目的：

1.  首先，为 URI：[“http://bookstore.toolsqa.com/BookStore/v1/Books”](https://bookstore.toolsqa.com/BookStore/v1/Books)创建一个新的 REST 项目，如文章“SoapUI：使用项目”中所述，并将其命名为“BookStoreService”。
2.  其次，在名为“Functional Test Suite”的项目下添加一个新的TestSuite，如“How to create a TestSuite in SoapUI?”一节所述。
3.  第三，按照“如何在SoapUI中创建TestCase？”一节中提到的步骤，在上面创建的TestSuite下添加一个名为“ValidateBooksCount”的新TestCase 。
4.  现在，右键单击创建的测试用例并选择“添加步骤>> REST 请求”，如下所示：

![在 SoapUI 中为 REST 请求选择测试步骤](https://www.toolsqa.com/gallery/SoapUI/20.Selecting%20a%20Test%20Step%20for%20REST%20Request%20in%20SoapUI.png)

1.  第五，选择“REST Request”将显示一个对话框，你可以在其中指定测试步骤的名称， 如下所示：

![为 REST 请求指定测试步骤名称](https://www.toolsqa.com/gallery/SoapUI/21.Specifying%20Test%20Step%20name%20for%20a%20REST%20Request.png)

1.  第六，单击“确定”按钮将带你进入一个对话框，你可以在其中选择要测试的服务端点。

![为测试步骤选择 REST web 服务的端点](https://www.toolsqa.com/gallery/SoapUI/22.Selecting%20endpoint%20of%20REST%20webservice%20for%20Test%20Step.png)

1.  最后，选择“REST方法”并点击“确定”，就会在TestCase下添加“TestStep” ，并在右侧面板中打开TestStep对话框。点击“播放”图标会执行REST服务方法，并会在面板右侧屏幕显示响应，如下图：

![执行 REST 服务 TestStep](https://www.toolsqa.com/gallery/SoapUI/23.Executing%20a%20REST%20service%20TestStep.png)

注意：对于 REST 服务，在添加 TestStep 时没有发生断言验证。因此，理想情况下，没有向测试用例添加任何验证。我们将在下一篇文章中介绍断言的添加。

所以，到目前为止，我们已经了解了如何在 SoapUI 中为 SOAP 和 REST Web 服务添加 TestSuites、TestCases 和 TestSteps。

现在，SoapUI 提供了一种方法，我们可以使用它为 WebServices 提供的所有方法一步生成 TestSuite。让我们在下一节中了解如何实现相同的目标：

### WebService的所有请求如何生成TestSuite？

我们可以按照以下步骤自动生成测试套件、测试用例和测试步骤，而不是为 Web 服务的特定方法添加测试套件：

1.  假设你已经使用 URI 创建了一个 SOAP 项目：http: [//bookstore.toolsqa.com/BookStoreService.wsdl](https://bookstore.toolsqa.com/BookStoreService.wsdl)
2.  右击SOAP服务，选择“Generate TestSuite”，如下图：

![在 SOAPUI 中生成一个 TestSuite](https://www.toolsqa.com/gallery/SoapUI/24.Generate%20a%20TestSuite%20in%20SOAPUI.png)

1.  会打开“Generate TestSuite”对话框，如下图：

![在 SoapUI 中生成 TestSuite 配置](https://www.toolsqa.com/gallery/SoapUI/25.Generate%20TestSuite%20Configuration%20in%20SoapUI.png)

在哪里，

1.  指定“创建” TestSuite的选项。
2.  指定 TestCases 将如何在 TestSuite 下组织的样式。
3.  此外，它指定请求的内容，是创建空请求还是使用现有请求。
4.  指定 TestSuite 需要为其生成的服务的所有方法。

1.  单击“确定”按钮会生成询问测试套件名称的对话框：

![自动生成测试套件时指定测试套件名称](https://www.toolsqa.com/gallery/SoapUI/26.Specifying%20name%20of%20the%20TestSuite%20while%20auto%20generating%20TestSuite.png)

1.  将名称指定为“ValidateBookService”并单击“确定”按钮后，它将为WebService的所有方法生成TestSuite、TestCases和TestSteps 。

![SoapUI 中自动生成的 TestSuites TestCases 和 TestSteps](https://www.toolsqa.com/gallery/SoapUI/27.Auto%20generated%20TestSuites%20TestCases%20and%20TestSteps%20in%20SoapUI.png)

正如我们在上面的截图中看到的，SoapUI已经自动生成了名为“ValidateBookService”的 TestSuite 。它为WebService的每个方法生成了一个TestCase 。此外，它还生成了每个TestCase都有一个TestStep用于验证。

注意：我们也可以按照类似的步骤为 REST 服务生成 TestSuite。

## 关键要点

-   SoapUI 遵循 TestSuite >> TestCases >> TestSteps 的层次结构来组织验证步骤。
-   每个 SoapUI 项目都可以有多个测试套件。
-   此外，每个 TestSuite 可以有多个测试用例。
-   每个测试用例可以有多个 TestSteps。
-   除了单独创建 TestSuite、TestCase 和 TestSteps 之外，SoapUI 还可以在单个步骤中为 Webservice 的所有方法自动生成 TestSuite。

现在我们已经熟悉了SoapUI 中 TestCases 的概念，让我们转到下一篇文章，我们将了解“SoapUI 中的[断言”](https://www.toolsqa.com/soapui/assertions-in-soapui/)的不同类型，我们如何在 SoapUI 中使用和应用。