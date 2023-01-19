众所周知，市场上存在许多[IDE](https://en.wikipedia.org/wiki/Integrated_development_environment)，它们通过提供各种功能(例如“自动代码完成”、“语法高亮显示”、“调试”等)使开发人员的工作变得非常轻松[。Eclipse](https://www.toolsqa.com/selenium-webdriver/download-and-start-eclipse/)是其中之一Java 开发人员最常用的IDE。现在要在项目中使用任何第三方库，所有这些IDE都提供了在项目中集成和使用这些库的功能的方法。正如我们所知，所有的 Java 库都被捆绑并以[JARS(Java Archives)的形式提供，](https://en.wikipedia.org/wiki/JAR_(file_format))可以将其包含在一个项目中，然后该项目可以调用这些 jar 文件中捆绑的各种类的所需功能。相似地，[Selenium WebDriver for Java](https://www.toolsqa.com/selenium-webdriver/download-selenium-webdriver/)还提供了一组 jar 文件，我们需要将其包含在项目中以访问和使用Selenium WebDriver 提供的功能。因此，我们将在后续讨论 Selenium Eclipse。

在本文中，我们将详细介绍如何在 Eclipse 中配置 Selenium WebDriver jar。此外，我们还将了解如何在Eclipse 中设置其他细节。它已准备好启动使用Selenium WebDriver编写 Web 自动化测试的学习之旅。

-   如何启动 Eclipse 并创建工作区？
    -   在 Eclipse 中创建一个新项目？
    -   在项目下新建一个包？
    -   并且，在包下创建一个新类？
    -   如何将 Selenium WebDriver Jars 添加到项目中？

## 如何在 Eclipse 中配置 Selenium WebDriver？

用任何编程语言编写测试用例几乎都与编写代码来开发应用程序相同。在开始为任何应用程序编码之前，我们需要在IDE中进行一些设置和配置，我们将使用它们进行开发。同理，在开始使用Selenium WebDriver开发自动化测试用例之前，我们需要在Eclipse IDE中进行具体的配置。它确保它具有我们开发测试用例所需的所有依赖项。因此，在使用Selenium WebDriver for Java进行测试用例开发之前，我们需要配置 Eclipse。它导致Selenium WebDriver 的正确的配置及其对 Web 自动化测试用例开发的可用性。

[下载 Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/download-selenium-webdriver/)后，请按照以下部分中提到的步骤在 Eclipse 中配置 Selenium WebDriver：

### 启动 Eclipse 并创建一个工作区？

开始使用Eclipse IDE的第一步 是下载并启动Eclipse。Eclipse 启动后，它会要求你创建一个工作区。

注意： [Eclipse 工作区](https://dvteclipse.com/documentation/pss/What_is_a_Workspace.html#:~:text=The workspace is a directory,your "space of work".)是系统上的一个目录，其中将存储所有 Eclipse 插件、配置或其他项目相关信息。

你可以按照[“下载并启动 Eclipse”](https://www.toolsqa.com/selenium-webdriver/download-and-start-eclipse/)一文中详述的步骤来快速安装和启动Eclipse。

设置工作区后，下一步是在 Eclipse中创建一个“项目” 。让我们看看如何在 Eclipse 中实现相同的目的：

### 在 Eclipse 中创建一个新项目？

在 Eclipse 中创建新项目之前， 你需要知道它是什么？项目是源文件和有助于构建、运行和调试源文件的设置的集合。在 Eclipse 中，项目位于最顶层，紧接在工作区之后。一个项目将包含所有相关的源文件和依赖库。你可以在一个Eclipse 工作区下拥有多个项目。

按照下面提到的步骤在 Eclipse 中创建一个新的 Java 项目：

1.  导航到文件 > 新建 > 项目。 或者，你可以直接选择“新建”菜单下的“ Java 项目”选项。

![通过文件菜单导航到新项目选项](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Navigating%20to%20New%20Project%20option%20through%20File%20Menu.png)

1.  它将打开“新建项目”向导。从New Project对话框中，选择Java Project并单击Next。

![在 Eclipse 中选择项目类型](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Selecting%20Type%20of%20Project%20in%20Eclipse.png)

1.  在“创建 Java 项目”向导中，为 Java 项目指定一个名称并选择其他选项，如下所示：

![Eclipse 指定 Java 项目名称和 JRE 配置](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Eclipse%20Specify%20Java%20Project%20Name%20and%20JRE%20Configurations.png)

正如我们所看到的，我们在上面的对话框中指定了以下选项：

-   如突出显示的1，“DemoQA”已指定为项目名称。
-   标记 2突出显示项目的路径。默认情况下，项目将创建在工作区文件夹下。但是，你也可以将项目保存在任何其他文件夹中，方法是取消选中“使用默认位置”复选框并单击“浏览”按钮选择文件夹路径。
-   标记 3突出显示了我们将用于此项目的[JRE(Java 运行时环境) 。](https://www.infoworld.com/article/3304858/what-is-the-jre-introduction-to-the-java-runtime-environment.html)默认情况下，Eclipse 将显示你机器上安装的最新版本的 JRE(例如，在我们的示例中，它显示JavaSE-1.8。 如果你想为项目使用一些不同版本的 JRE，你可以从下拉列表。此外，还有一个选项可以选择“项目特定的 JRE”，它仅适用于当前项目。当你在同一工作区下有多个项目并且想要设置不同的版本时，这些选项会派上用场用于各种项目的 JRE。
-   标记 4显示了源代码和编译文件在项目下的排列方式的选项。当我们选择默认选项“为源文件和类文件创建单独的文件夹”时， Eclipse 将为源文件创建一个不同的文件夹，默认情况下将命名为“src”，并为编译后的类文件创建一个不同的文件夹，这将默认命名为"bin"。如果我们想将源文件和类文件都保存在父项目文件夹下，我们可以选择“使用项目文件夹作为源文件和类文件的根目录”选项。
-   完成上述所有配置后，你可以单击“完成”按钮关闭对话框或单击“下一步”按钮，如标记 5 所示，这将打开显示文件夹的对话框项目、源和类如下所示：

![显示源和类的不同路径的 Eclipse 配置](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Eclipse%20configurations%20showing%20different%20paths%20of%20sources%20and%20classes.png)

-   正如我们在上面的屏幕截图中看到的，Eclipse 创建了一个文件夹“src”(突出显示为 1)。它将存储所有源文件并创建一个文件夹“bin”(突出显示为 2)，该文件夹将存储所有已编译的类文件。单击“完成”按钮(突出显示为 3)以关闭对话框。

1.  单击“完成”按钮将显示一个弹出窗口以选择 Java 透视图，它更多地围绕在 Eclipse 的左侧资源管理器框中对齐 Java 项目的不同组件。示例透视弹出窗口如下所示：

![Eclipse 透视图对话框](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Eclipse%20Perspective%20Dialog%20box.png)

注意：以上对话框只会出现在“Eclipse企业版”，因为这个版本也可以用于其他类型项目的开发(Java除外)，每一种项目都有它的视角。

当我们在Eclipse中创建完Java项目后，下一步就是在项目下创建一个[包](https://www.w3schools.com/java/java_packages.asp)。下面看看如何在Eclipse中创建项目下的包：

### 在项目下新建一个包

要在新创建的项目“DemoQA”下创建新包，请按照以下步骤操作：

1.  右键单击你的项目名称“DemoQA”并导航到 “新建”>“包”。

![在 Eclipse 项目下新建一个包](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Creating%20a%20new%20package%20under%20project%20in%20Eclipse.png)

1.  为你的包命名，例如“automationFramework”，然后单击“完成”按钮。

![为 Java 包指定一个名称](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Specify%20a%20name%20to%20the%20Java%20package.png)

1.  包创建成功后，会在Eclipse Project Explorer窗口中反映到项目的src 文件夹下 ，如下图：

![包在项目资源管理器中的 src 文件夹下可见](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Package%20visible%20under%20src%20folder%20in%20project%20explorer.png)

在项目下创建完包后，下一步是在包下创建一个[Java 类](https://www.toolsqa.com/java/classes-objects/)，该类将使用Selenium WebDriver编写测试用例。

### 在包下新建一个类

现在我们已经创建了项目和包，我们将需要类来编写代码。要创建新类，请按照以下步骤操作：

1.  右键单击“automationFramework”包并导航到 “新建”>“类”。

![在包下新建一个类](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Creating%20a%20new%20class%20under%20the%20package.png)

1.  单击“Class”选项后，你将看到一个 New Java Class 向导，如下所示：

![Eclipse 中的新建 Java 类向导](https://www.toolsqa.com/gallery/selnium%20webdriver/10.New%20Java%20Class%20Wizard%20in%20Eclipse.png)

-   正如我们从上面的截图中看到的，源文件夹“DemoQA/src”和包文件夹“automation framework”(如标记 1)将由 Eclipse 自动填充。如果你需要更改其中任何一个，你可以单击相应的“浏览”文件夹。之后，你可以选择所需的文件夹。
-   之后，你需要在标记为 2的字段中指定类的名称。 在我们的例子中，我们将类的名称定义为“FirstTestCase”。
-   接下来，你需要为类选择[修饰符](https://www.toolsqa.com/java/access-modifiers/)(如标记 3)。
-   下一个选项是为你的类选择一个超类/父类(如标记 4)，因为我们知道在 java 中“Object”类是所有类的父类。因此，Eclipse 自动填充了“java.lang.对象”类默认。
-   如果你想在你的类中自动创建任何方法，那么你需要选择选项(如标记 5)。因为我们想在我们的类中创建一个“Main”方法，所以我们只选择了第一个选项。
-   单击“完成”按钮关闭对话框。

1.  一旦我们成功创建了 Java 类。会在Eclipse的“Project Explorer”窗口下反映如下：

![Project Explorer 窗口下的 Java 类](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Java%20class%20under%20Project%20Explorer%20window.png)

正如我们在上面的屏幕截图中看到的，在“automationFramework”包下创建了一个名为“FirstTestCase.java”的新文件。 它包含一个名为“FirstTestCase”的类，如上面屏幕截图的右窗格所示。

最后，现在我们有一个可用的占位符，我们可以在其中编写我们的 Selenium 测试用例。下一步是在新创建的项目中包含“Selenium WebDriver”提供的各种库。让我们如何在 Eclipse 的项目中包含 Selenium WebDriver 库？

### 如何将 Selenium WebDriver Jars 添加到项目中？

要在 Eclipse 项目中包含任何 Java 库，同样需要在 Eclipse 的[ Build Path 中包含。](https://docs.streambase.com/sb77/index.jsp?topic=%2Fcom.streambase.sb.ide.help%2Fdata%2Fhtml%2Fauthoring%2Fjavabuildpath.html)同样，Selenium WebDriver库也需要包含在Eclipse的“构建路径”中。由于我们已经[下载了 Selenium WebDriver，](https://www.toolsqa.com/selenium-webdriver/download-selenium-webdriver/) 我们可以按照以下步骤将这些Selenium WebDriver Jar 文件 包含到Eclipse 项目中：

1.  首先，右键单击项目“DemoQA” >构建路径>配置构建路径

![在 Eclipse 中导航到项目构建路径](https://www.toolsqa.com/gallery/selnium%20webdriver/12.Navigating%20to%20Project%20Build%20Path%20in%20Eclipse.png)

或者

右击“DemoQA”项目，点击“属性”选项，打开Eclipse的“项目属性向导”：

![Eclipse 中的项目属性](https://www.toolsqa.com/gallery/selnium%20webdriver/13.Project%20properties%20in%20Eclipse.png)

1.  其次，由于“Selenium WebDriver”是项目的外部库，因此我们需要将“Selenium WebDriver”依赖项添加为“外部 JAR”。

![Java 构建路径配置以包含 Selenium WebDriver Jars](https://www.toolsqa.com/gallery/selnium%20webdriver/14.Java%20Build%20Path%20Configuration%20to%20include%20Selenium%20WebDriver%20Jars.png)

-   首先，选择左侧面板中的“Java 构建路径”(如标记 1 所示)(如果未自动选择)。
-   之后，单击“库”选项卡，如标记 2 所示。
-   最后，单击“添加外部 JAR”按钮，如标记 3 所示。

1.  第三，浏览到你提取所有Selenium WebDriver jar 文件的文件夹。因为我们使用“Selenium -4”，所以我们将添加“Selenium 4.0.0 -alpha”提供的所有 jar 文件。结果，如下图所示：

![Selenium WebDriver 4 的提取 Jar 文件](https://www.toolsqa.com/gallery/selnium%20webdriver/15.Extracted%20Jar%20files%20of%20Selenium%20WebDriver%204.png)

正如我们在上面的截图中看到的，很少有Selenium WebDriver jar 文件直接位于“ selenium-java-4.0.0-alpha ”文件夹下。同样，其中一些位于“ selenium-java-4.0.0-alpha ”中的“ libs ”文件夹下。此外，我们需要使用“添加外部 JAR ”选项添加所有这些。

1.  最后，添加完所有“JAR”文件后，单击“应用并关闭”按钮。它将在“Referenced Libraries”文件夹下的“Project Explorer”窗口中显示所有JAR 文件，如下所示：

![在 Eclipse 中配置的 Selenium WebDriver 库](https://www.toolsqa.com/gallery/selnium%20webdriver/16.Selenium%20WebDriver%20libraries%20configured%20in%20Eclipse.png)

Selenium WebDriver jar的配置现已完成。最后，你已经准备好在 Eclipse 中使用 Selenium WebDriver编写你的第一个测试脚本。

## 关键要点

-   为了在 Eclipse 中编写测试脚本，我们需要在 Eclipse 中创建项目、包和类。
-   此外，所有第三方库都应出现在 Eclipse 的构建路径中，以便在当前项目中访问它们的功能。
-   此外，Selenium WebDriver 提供了一组 jar 文件，需要包含在 Eclipse 的构建路径中。
-   此外，一旦 Selenium WebDriver 库在构建路径中可用，你就可以在当前项目中访问 Selenium WebDriver 的所有功能。