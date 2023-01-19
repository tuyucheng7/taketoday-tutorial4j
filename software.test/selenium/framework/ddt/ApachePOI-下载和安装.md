在测试自动化中，测试数据起着重要的作用。测试自动化框架开发人员以各种格式维护测试数据，而[Microsoft Excel](https://www.microsoft.com/en-in/microsoft-365/excel)是存储和维护测试数据的最简单和最常用的方法之一。即使是手动 QA 也会将他们的测试数据保存在 Excel 文件中。现在为了在自动化框架中访问这些测试数据，[Java](https://www.toolsqa.com/java/java-tutorial/)提供了各种库，而[Apache POI](https://poi.apache.org/)是其中最常用的库之一。随后，在本文中，我们将通过了解如何下载 apache poi 并在自动化框架中使用它来开始我们使用Apache POI的旅程，通过涵盖以下主题下的详细信息：

-   Apache POI 有什么用？
-   如何下载 Apache POI？
-   如何安装兴趣点库？
    -   以及，如何在 Eclipse 中配置 POI 库？

## Apache POI 有什么用？

Apache POI是由Apache Foundation开发和分发的开源库 此外，它主要用于创建、读取和编辑[Microsoft Office](https://www.microsoft.com/en-in/download/office.aspx)文件，主要是 Java 程序中的 Excel 文件。此外，它以 JAR 的形式分发，提供了多种操作 Microsoft Excel 文件的方法。下图显示了 Apache POI 支持的各种格式和操作的详细信息：

![Apache POI 操作](https://www.toolsqa.com/gallery/Blogs/1.Apache%20POI%20actions.png)

旧版本的Apache POI支持二进制文件格式，如doc、xls、ppt 等，而从 3.5 版开始，Apache POI支持OOXML文件格式，如docx、xlsx、pptx等。此外，下表简要总结了Apache POI提供的各种组件：

| 零件     | 解释               | 细节                                                 |
| -------------- | ------------------------ | ---------------------------------------------------------- |
| POIF     | 糟糕的混淆实现文件系统。 | 该组件提供读取各种文件的能力。                             |
| 高速钢   | 可怕的电子表格格式       | 该组件用于读取/写入旧格式的 Excel( xls )。               |
| XSSF     | XML电子表格格式          | 该组件用于读取/写入新格式的 Excel( xlsx )。              |
| HPSF     | 糟糕的属性集格式         | 该组件用于提取各种类型的 MS-Office 文件的“属性集” 。 |
| HWPF     | 可怕的文字处理器格式     | 该组件读取/写入旧格式的 Word ( doc )。                   |
| XWPF     | XML 字处理器格式         | 该组件读取/写入 Word ( docx ) 的新格式。                 |
| 高速低频 | 可怕的幻灯片布局格式     | 该组件读取/写入 PowerPoint演示文稿。                 |
| HDGF     | 可怕的图表格式           | 该组件读取/写入 MS-Visio文件。                       |
| HPBF     | 可怕的出版商格式         | 该组件读取/写入 MS-Publisher文件。                   |

现在让我们快速了解如何下载 Apache POI 库：

## 如何下载 Apache POI？

在 Excel 文件中存储和访问测试数据的第一步是下载Apache POI库。因此，请按照以下步骤下载Apache POI 库：

1.  首先，导航到[Apache POI](https://poi.apache.org/) 网页。之后，单击左侧菜单中的下载 链接。此外，它如下所示：

![网站上的下载链接](https://www.toolsqa.com/gallery/Blogs/2.download%20link%20on%20the%20site.png)

1.  其次，单击下载链接将导航到显示Apache POI最新版本的页面。此外，它如下突出显示：

![最新版本的 Apache POI](https://www.toolsqa.com/gallery/Blogs/3.latest%20release%20of%20Apache%20POI.png)

1.  第三，你可以单击“Latest Stable Release Link” (如标记 1 所示)，这会将页面向下滚动到Apache POI 的二进制文件(如标记 2 所示)，或者可以直接向下滚动到该部分标记 2 显示的二进制文件。随后，单击“zip”文件后，它将导航到显示各种下载链接的页面，如下所示：

![镜像下载链接](https://www.toolsqa.com/gallery/Blogs/4.mirror%20links%20for%20downloads.png)

1.  之后，当你单击任何突出显示的链接时，它将下载一个 zip。此外，你可以将其保存在你选择的任何文件夹中，如下所示：

![下载apache然后压缩文件](https://www.toolsqa.com/gallery/Blogs/5.download%20apache%20poi%20zip%20file.png)

1.  第五，解压文件后，会显示如下内容：

![apache 然后库](https://www.toolsqa.com/gallery/Blogs/6.apache%20poi%20libraries.png)

这些是各种JAR 文件，它们提供了我们用于处理各种MS-Office文件类型的类和方法。随后，让我们看看如何在我们的项目中安装这些JAR  ，并使用它们来处理各种支持的文件类型。

### 如何安装兴趣点库？

正如我们在上一节中了解到的那样，所有Apache POI 库都以JAR 形式提供。现在要访问POI的功能，这些JAR应该在你的应用程序/框架的构建路径中可用。此外，我们在文章中主要使用Eclipse作为IDE。因此，让我们快速了解如何在Eclipse项目的构建路径中安装Apache POI JAR ：

### 如何在 Eclipse 中配置 POI 库？

按照下面提到的步骤在Eclipse的项目中添加POI JAR ：

1.  首先，假设你已经按照文章[“使用 Eclipse 配置 Selenium WebDriver”](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)中提到的步骤在Eclipse中创建了一个JAVA项目。
2.  之后，右键单击Eclipse 中的项目。随后， 选择Build Path >> Configure Build Path如下所示：-

![在 Eclipse 中配置构建路径](https://www.toolsqa.com/gallery/Blogs/7.Configure%20Build%20Path%20in%20Eclipse.png)

1.  第三，它将打开项目的“属性”。之后，选择库选项卡。最后，单击下面突出显示的添加外部 JAR 。

![在 Eclipse 项目中添加外部 JAR](https://www.toolsqa.com/gallery/Blogs/8.Add%20External%20JARs%20in%20Eclipse%20Project.png)

注意：添加外部 JAR 时应选择类路径(如标记 1 突出显示)。

1.  第四，选择解压缩的POI文件的父文件夹中的JAR。随后，单击“打开”按钮将它们包含在Eclipse项目中：

![父 POI JAR 文件](https://www.toolsqa.com/gallery/Blogs/9.Parent%20POI%20JAR%20files.png)

1.  接下来选择解压缩的POI文件夹中ooxml-lib文件夹下的JAR。此外，它如下所示：

![ooxml lib 文件夹中的 POI JAR](https://www.toolsqa.com/gallery/Blogs/10.POI%20JARs%20in%20ooxml%20lib%20folder.png)

1.  第六步，在解压后的POI文件夹中选择lib文件夹下的JAR  。此外，它如下突出显示：

![lib 文件夹中的 POI JAR](https://www.toolsqa.com/gallery/Blogs/11.POI%20JARs%20in%20the%20lib%20folder.png)

1.  之后，添加所有POI JAR后 ，单击Apply and Close按钮。此外，它如下所示：

![安装 POI 库](https://www.toolsqa.com/gallery/Blogs/12.Install%20POI%20libraries.png)

1.  当所有的POI库在Eclipse项目 中安装成功后，它们将反映在Eclipse项目结构左侧面板的Referenced Libraries文件夹下，如下图：

![Eclipse 项目中的 Apache POI JAR](https://www.toolsqa.com/gallery/Blogs/13.Apache%20POI%20JARs%20in%20Eclipse%20Project.png)

这样，就完成了Eclipse 项目中Apache POI 的安装。随后，我们现在可以开始在我们的JAVA项目中使用这些库的功能。

## 关键要点

-   Apache POI 库提供了处理各种类型的 MS-Office 文件的能力。
-   此外，对于自动化框架，将测试数据保存在文件(例如 Excel 文件)中是一种常见做法，Apache POI 使得读取测试数据和将测试数据写入 Excel 文件变得非常容易。
-   最后，Apache POI 库以一组 JAR 文件的形式提供，我们只需将 JAR 文件包含在项目中即可将其下载并安装到 Eclipse 项目中。