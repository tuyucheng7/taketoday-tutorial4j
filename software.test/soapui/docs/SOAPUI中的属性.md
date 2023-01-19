众所周知，属性表示特定对象的特征或特性，它具有可以用定义的术语评估或测量的值。同样，在计算机科学中，术语属性用于表示或描述与对象或数据结构相关联的属性。各种编程语言提供定义的语法来[定义和使用属性](https://en.wikipedia.org/wiki/Property_(programming)). Property 一般是一个有特定值的命名字符串，我们可以通过脚本或程序访问。属性提供了扩展功能，可以将一般信息存储在一个中心位置，然后可以根据公开的范围在不同的位置进行访问。SoapUI 还提供了在多个范围内定义和访问属性的功能，这使得测试的维护变得非常容易，因为它们允许它们在测试和范围之间流动特定的特征。

让我们通过涵盖以下主题下的详细信息来详细了解我们如何使用SoapUI 中的属性：

-   SoapUI 中的属性是什么？
    -   SoapUI 中的默认属性。
    -   SoapUI 中的自定义属性。
-   SoapUI 中的属性有哪些不同类别？
    -   另外，SoapUI 中的全局属性是什么？
    -   SoapUI 中的项目级属性是什么？
    -   以及，SoapUI 中的测试级别属性是什么？

## SoapUI 中的属性是什么？

正如我们所讨论的，SoapUI 提供了添加和使用属性的能力。SoapUI 中的属性有助于实现以下目标：

-   属性可以存储端点、登录凭据等配置，这将使管理它们变得容易。此外，如果其中任何一项发生更改，则需要仅在一个地方进行更改。
-   属性从目标服务的响应中接收值/数据，这些值/数据可以作为测试步骤/断言的输入。

SoapUI 中的所有属性都分为两种类型，根据它们是由SoapUI内置提供还是由用户定义来分类。下图显示了SoapUI中属性的广泛分类：

![SoapUI 中的属性类型](https://www.toolsqa.com/gallery/SoapUI/1.Types%20of%20properties%20in%20SoapUI.png)

让我们借助示例更详细地了解这些类型的属性。假设我们已经在SoapUI 中为[REST 服务](https://bookstore.toolsqa.com/BookStore/v1/Books)[](https://bookstore.toolsqa.com/BookStore/v1/Books)或[SOAP 服务](https://bookstore.toolsqa.com/BookStoreService.wsdl)创建了一个SOAP或REST项目，如文章“ [SoapUI 项目](https://www.toolsqa.com/soapui/soapui-project/)”中所定义。

现在，让我们了解如何在 SoapUI 项目中查看、定义和使用Default和Custom 属性：

### SoapUI 中的默认属性

SoapUI中的默认属性是在你创建新项目时默认随SoapUI一起提供的属性。你可以在“项目导航器”的底部查看所有默认属性。它将根据你在“项目导航器”中选择的部分显示各种属性。这些属性中的很少一部分如下面的部分所示：

#### 工作区默认属性：

当你在“Project Navigator”中选择“Workspace”时，它将显示工作空间的“Default Properties”，如下所示：

![SoapUI 中的工作区默认属性](https://www.toolsqa.com/gallery/SoapUI/2.Workspace%20default%20properties%20in%20SoapUI.png)

所选工作区中的所有项目和项目元素都可以访问这些属性。

#### 项目默认属性：

当你在“项目导航器”中选择一个“项目”时，它将显示该项目的“默认属性”，如下所示：

![SoapUI 中的项目默认属性](https://www.toolsqa.com/gallery/SoapUI/3.Project%20default%20properties%20in%20SoapUI.png)

所选项目下的元素将可以访问这些属性。

#### 服务默认属性：

当你在“Project Navigator”中选择“WebService”时，它将显示WebService的“Default Properties”，如下所示：

![SoapUI 中的 WebService 默认属性](https://www.toolsqa.com/gallery/SoapUI/4.WebService%20default%20properties%20in%20SoapUI.png)

所选WebService下的方法可以访问这些属性。

#### 资源默认属性：

当你在“Project Navigator”中选择“WebService”时，它会显示WebService的“Default Properties”，如下所示：

![SoapUI 中的资源默认属性](https://www.toolsqa.com/gallery/SoapUI/5.Resource%20default%20properties%20in%20SoapUI.png)

WebService的选定资源下的元素将可以访问这些属性。

#### 方法默认属性：

当你在“Project Navigator”中选择“WebService”时，它会显示WebService的“Default Properties”，如下所示：

![SoapUI 中的方法默认属性](https://www.toolsqa.com/gallery/SoapUI/6.Method%20default%20properties%20in%20SoapUI.png)

WebService的选定方法下的元素将可以访问这些属性。

#### 请求默认属性：

当你在“Project Navigator”中选择“WebService”时，它会显示WebService的“Default Properties”，如下所示：

![在 SoapUI 中请求默认属性](https://www.toolsqa.com/gallery/SoapUI/7.Request%20default%20properties%20in%20SoapUI.png)

根据WebService的选定请求，这些属性将可供元素访问。

除了 SoapUI 提供的默认属性外，我们还可以提供各种级别的自定义属性。让我们了解这些细节：

### SoapUI 中的自定义属性

用户定义或自定义属性是最终用户根据要求定义的一组属性。我们主要在测试级别定义这些属性。要在SoapUI 测试中为测试步骤添加自定义属性，请按照如下所示的步骤操作：

1.  你应右键单击测试步骤，然后单击突出显示的“添加步骤”，然后选择属性。

![将自定义属性添加到测试步骤](https://www.toolsqa.com/gallery/SoapUI/8.Adding%20a%20Custom%20Property%20To%20a%20Test%20Step.jpg)

1.  选择属性后，你将看到下面的 对话框以提供属性集的名称：

![定义属性步骤名称](https://www.toolsqa.com/gallery/SoapUI/9.Define%20Property%20Step%20Name.jpg)

\3. 指定逻辑名称后，SoapUI 将显示如下屏幕以添加测试用例的属性。

![添加自定义属性](https://www.toolsqa.com/gallery/SoapUI/10.Adding%20Custom%20Properties.png)

1.  单击突出显示的 “+”按钮以添加新属性。如上面的快照中所述，你可以使用属性的名称，即作者和BookStore API响应中的值“ Kyle Simpson” 。

因此，通过这种方式，我们可以将尽可能多的自定义属性添加到测试步骤，然后在SoapUI 测试中的验证或其他操作期间使用相同的属性。

## SoapUI 中的属性有哪些不同类别？

SoapUI 中的属性根据其定义的属性范围进行分类。范围是一个项目内部的级别，或者是识别和访问属性的项目之间的级别。

SoapUI 主要识别属性分类的三个类别/区域。这些是：

-   全局属性。
-   项目级属性。
-   测试级别属性。

让我们在以下部分中详细了解所有这些内容：

### SoapUI 中的全局属性是什么？

全局属性指定/定义在 SoapUI 全局级别关联的属性。当我们说全局时，这意味着我们可以跨工作区、项目、测试套件和测试用例访问这些属性。

#### 如何在 SoapUI 中定义全局属性？

可以从SoapUI 工具栏中的首选项选项卡或从

菜单栏中的文件 --> 首选项 --> 全局属性，如下面的屏幕截图中突出显示的那样。

![在 SoapUI 中添加全局属性](https://www.toolsqa.com/gallery/SoapUI/11.Add%20Global%20Property%20In%20SoapUI.jpg)

现在要定义一个新的全局属性，请单击添加(+)按钮，如上所示。

假设，在 SoapUI 工作区中，我们有两个 Web 服务：一个SOAP项目和另一个REST项目，它们需要客户端密码来请求服务器，并且我们在两个服务之间共享客户端密码。

为了处理这种情况，我们可以添加一个全局属性，其中“client-key”作为关键，“Client-key-encryption”作为值，如上面的屏幕截图所示。

现在，让我们看看如何跨两个项目访问这些属性：

#### 如何访问 SoapUI 中的全局属性？

要访问 SoapUI 中的任何属性，我们可以使用以下语法：

```java
${KeyOftheProperty}
```

其中$是要加前缀的唯一字符，然后在花括号下提供访问所需的密钥。

假设，我们想要访问上面定义的键“client-key”，并在调用请求时将其作为标头传递。我们可以按照下面显示的步骤来实现相同的目的：

![在 SoapUI 中访问全局属性](https://www.toolsqa.com/gallery/SoapUI/12.Access%20Global%20Property%20In%20SoapUI.jpg)

如上所示，我们通过以下步骤向REST请求添加了一个用户定义的标头：

1.  导航到工作区中的 REST 项目并通过双击突出显示的请求来启动显示书籍详细信息的请求。
2.  单击“标头”，如第 2 步突出显示的那样，SoapUI 现在将有助于在发出请求之前手动添加标头。
3.  单击添加按钮，如第 3 步中突出显示的那样，并提供你要设置的标题的名称。
4.  现在，在第 4 步中，要访问全局属性，请指定${client-key}，这是全局属性集。

现在通过单击播放按钮执行请求。执行后，你可以通过单击“Raw”按钮来检查请求中发送的标头，如下所示：

![SOAP UI 请求中的标头](https://www.toolsqa.com/gallery/SoapUI/13.Headers%20In%20SOAP%20UI%20Request.jpg)

如我们所见，传递给请求的标头值与为全局属性定义的相同。这样，我们可以在全局级别创建属性，我们可以在该工作区中的所有项目中访问这些属性。

注意：我们可以尝试使用与 REST 项目相同的步骤访问 SOAP 项目中的相同属性。

### SoapUI 中的项目级属性是什么？

项目级属性指定仅与当前项目关联的属性。这种属性的范围将跨越同一项目中的所有TestSuit。但与全局属性一样，这些属性将无法跨项目使用和访问。

#### 如何在 SoapUI 中定义项目级属性？

我们可以通过选择项目并单击自定义属性下的“+”按钮来在项目级别添加新属性。假设，我们要为项目“REST - bookstore ToolsQA”添加一个新属性，即值为234的“Pages” ，如下所示：

![在 SoapUI 中添加项目级属性](https://www.toolsqa.com/gallery/SoapUI/14.Adding%20Project%20Level%20Property%20In%20SoapUI.jpg)

#### 如何访问 SoapUI 中的项目级属性？

现在让我们看看如何使用上面定义的项目级属性。假设我们要添加一个基于属性值的断言。我们cIt是可以做到的，如下图：

![访问项目级别](https://www.toolsqa.com/gallery/SoapUI/15.Accessing%20a%20Project%20Level.jpg)

在上面的截图中，我们添加了一个测试来验证名为“ValidatePagesOfABook”的书页(请参阅左侧突出显示的页面)，而在屏幕截图的右侧，它已突出显示：

-   第 1步：书店服务的响应包含一个书籍数组。
-   第二步： books数组的每个对象都包含一个属性page，其值为234

此外，添加JsonPath 匹配断言以使用项目属性验证页面属性值，如下所示：

![访问项目属性](https://www.toolsqa.com/gallery/SoapUI/16.Accessing%20Project%20Properties.jpg)

在上面的快照中，我们使用了两个表达式：

1.  获取书籍数组的第一个对象的页面

```java
books[0].pages
```

在JSON 中，我们可以通过使用从 0 开始的索引并使用点运算符来遍历数组。我们可以进一步访问子属性。

1.  从项目属性中获取预期值

```java
${#Project#Pages}
```

作为全局属性，要获取任何属性，包括项目属性，我们从关键字美元和花括号(${) 开始。此外，我们指定以哈希为前缀的属性类型和我们要访问的属性的键。因此，使用上面的表达式，它将使用我们已经定义的键“Pages”获取项目级属性。

### SoapUI 中的测试级别属性是什么？

我们已经看到可以在项目级别或全局级别定义和使用属性。为了进一步深入，SoapUI 中的属性还可以仅在“测试级别”定义其范围仅限于测试步骤。我们可以在三个级别测试级别属性：

-   测试套件级别的属性
-   测试用例级属性
-   测试步骤级别属性

让我们更详细地了解所有这些：

#### SoapUI 中的测试套件级别属性是什么？

这些类型的属性指定与当前测试套件关联的属性。该测试套件下的所有组件都可以使用该属性，即测试用例、测试步骤、脚本，但不能被另一个测试套件使用。

为了清楚地理解，假设你要验证这本书的书名，该书有 234 页。这里需要在同一测试套件的多个测试中访问标题。对于这种用法，我们可以选择测试套件级别的属性，如下所示：

添加测试套件级别属性的步骤类似于添加项目级别属性，唯一的区别在于，你需要选择测试套件并添加自定义属性而不是项目，如下所示：

![添加测试套件属性](https://www.toolsqa.com/gallery/SoapUI/17.Adding%20a%20Test%20Suite%20Property.jpg)

在上面的截图中，我们在测试套件“TestSuite1”下添加了一个名称为“FirstBookTitle”、值为“Git Pocket Guide”的属性。

现在，要访问“TestSuite”级别属性，我们只需在其前面添加“TestSuite#”，如下所示：

![访问 TestSuite 级别的属性](https://www.toolsqa.com/gallery/SoapUI/18.Accessing%20a%20TestSuite%20level%20property.jpg)

因此，访问测试套件级别属性的语法是：

```java
${#TestSuite#FirstBookTitle}
```

#### SoapUI 中的测试用例级属性是什么？

这些是与当前测试用例关联的属性。我们可以通过测试用例的所有子集(测试步骤、脚本)使用此属性。你可以使用与添加测试套件级别属性类似的方式设置测试用例级别属性的值。唯一的区别是，你需要选择要为其定义属性的测试用例，而不是选择测试套件。

同样，要访问测试用例级别的属性，你可以使用以下语法：

```java
${#TestCase#PropertyName}
```

同样，获取测试用例级别属性的语法类似于获取项目/测试套件级别属性。我们使用 TestCase 代替关键字 TestSuite 来获取测试套件级别的属性。

#### SoapUI 中的测试步骤级别属性是什么？

测试步骤属性是与当前测试步骤关联的属性。我们可以通过测试步骤的所有子集(测试步骤、属性传输、脚本)使用此属性。

此外，这一级别属性的实现与 TestCase 属性非常相似；唯一的区别是使用范围。

要访问测试步骤级别属性，你可以使用以下语法：

```java
${#TestStep#PropertyName}
```

此处，关键字TestStep用于获取属性的值。

## 关键要点

-   属性包含我们可以在不同地方使用的值。例如，设置标头并在断言中广泛使用
-   此外，分类为默认的属性和自定义属性基于它们的定义。
-   此外，基于访问范围，我们可以将它们定义为从全局级别一直到测试步骤级别。

最后，在下一篇文章中，我们将了解所有更高级的使用属性的方法，即围绕[属性传递。](https://www.toolsqa.com/soapui/property-transfer-in-soapui/)