[Microsoft Edge](https://www.microsoft.com/en-us/edge)可能是浏览器领域的最新进入者之一，但已经赢得了用户的青睐。此外，它是从头开始构建的，牢记性能、隐私和安全性作为重要标准之一。凭借约 7% 的市场份额和[微软的支持，](https://www.microsoft.com/) Edge赢得了良好的品牌声誉。因此，这对于测试团队在Edge浏览器上测试他们的 Web 应用程序变得非常重要。此外，在多个浏览器上进行测试可确保兼容性并确保应用程序在不同浏览器平台上完美运行。与其他浏览器厂商一样，Microsoft Edge也提供了一个名为“ EdgeDriver"，它充当 Selenium 和Edge 浏览器之间的中介，有助于在Edge浏览器上执行Selenium测试用例。

随后，在本文中，我们将通过涵盖以下主题的详细信息来详细介绍如何使用 EdgeDriver 在 Edge 浏览器上运行 Selenium 测试：

-   什么是 Selenium EdgeDriver？
-   Selenium EdgeDriver 的先决条件是什么？
-   如何在 Windows 上安装 EdgeDriver？
-   如何在 Windows 上下载 EdgeDriver？
-   以及如何在 Windows 上设置 EdgeDriver？
-   以及如何在 Windows 的 Edge 浏览器上运行 Selenium 测试？
-   如何在 macOS 上安装 EdgeDriver？
-   如何在 macOS 中下载 EdgeDriver？
-   以及如何在 macOS 上设置 EdgeDriver？
-   以及如何在 Mac 上的 Edge 浏览器上运行 Selenium 测试？

## 什么是 Selenium EdgeDriver？

微软提供Microsoft WebDriver来在Edge浏览器上执行Selenium WebDriver自动化测试。此外，该驱动程序允许 selenium 测试与Edge浏览器通信以执行Selenium测试。此外，Edge驱动程序有不同的版本，具体取决于你的浏览器版本或你的系统是x86还是x64。可以根据你要使用的浏览器版本下载任何版本。

![硒边](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Selenium%20Edge.jpg)

此外，微软在 2020 年 1 月发布了全新版本的Edge浏览器。新版本建立在[Chromium 引擎之上，](https://www.chromium.org/Home)类似于Google Chrome。而且，之前版本的Microsoft Edge浏览器是基于[EdgeHTML 引擎的。](https://en.wikipedia.org/wiki/EdgeHTML)随后，要了解这两个浏览器引擎之间的区别，可以参考“浏览器引擎之间的区别[”链接。](https://en.wikipedia.org/wiki/Comparison_of_browser_engines)为了区分这两个版本的 Edge 浏览器，微软将Chromium版本简称为“Edge”，将旧版本称为“Edge”。 “边缘遗产”.

因此，在下载时，请确保根据你当前的浏览器版本为Edge或Edge Legacy下载正确的驱动程序。

### Selenium EdgeDriver 的先决条件是什么？

在我们开始为Edge浏览器编写Selenium测试之前，让我们先看看执行Selenium测试所需的一些先决条件。随后，我们将查看在 Edge 浏览器上运行Selenium测试用例所需的所有配置和程序：

1.  Edge 浏览器： 最明显的先决条件，确保你已经在系统上安装了Edge浏览器。除此之外，我们会推荐最新版本，但只要你可以获得指定版本的正确驱动程序，现有版本就可以正常工作。
2.  Java JDK：编写 Java 程序需要 JDK 或 Java Development Kit。由于我们将用 Java 编写 Selenium 测试，因此必须拥有 JDK。此外，要了解如何下载和安装 Java JDK，请阅读此处的完整教程：[如何安装 Java？](https://www.toolsqa.com/selenium-webdriver/install-java/)
3.  Eclipse IDE： Eclipse 是市场上最流行的 Java IDE 之一。此外，你可以轻松地从其官方网站下载一个，并将其设置为 Java 编程。此外，要了解 Eclipse 及其设置，请阅读我们之前的教程：[下载并安装 Eclipse。](https://www.toolsqa.com/selenium-webdriver/download-and-start-eclipse/)
4.  Selenium：安装需要最新版本的 Selenium WebDriver。但是，你可以使用任何最新的稳定版本。在本教程中，我们将使用最新的 Selenium 4。此外，你可以在此处学习如何使用 Eclipse 设置 Selenium：[配置 Selenium WebDriver。](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)

## 如何在 Windows 上安装 EdgeDriver？

完成所有先决条件的下载和设置后，我们就可以继续前进了。下一步将是在 Windows 平台上安装EdgeDriver 。因此，让我们开始在你的计算机上下载与Edge浏览器兼容的EdgeDriver 。

### 如何在 Windows 上下载 EdgeDriver？

在开始下载EdgeDriver 之前，我们首先需要检查系统中Edge浏览器的版本。由于EdgeDriver版本取决于浏览器版本，因此有必要下载兼容版本的驱动程序。你可以按照以下步骤检查Edge浏览器的版本：

1.  首先，打开 Edge 浏览器并单击右上角的“设置和更多” (三个点)或按alt + F。
2.  其次，将鼠标悬停在设置菜单中的“帮助和反馈”上。
3.  第三，单击关于 Microsoft Edge。

![Edge 浏览器的设置和更多](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Settings%20and%20more%20of%20Edge%20Browser.png)

1.  因此，这将打开包含有关你的 Edge 浏览器的所有详细信息的页面。

![关于Edge 获取Edge浏览器版本](https://www.toolsqa.com/gallery/selnium%20webdriver/3.About%20Edge%20get%20the%20version%20of%20Edge%20browser.png)

现在，由于我们拥有Microsoft Edge 浏览器的版本，我们可以继续从官方Microsoft Edge WebDriver站点下载EdgeDriver 。请按照以下步骤下载EdgeDriver：

1.  首先，导航到[Microsoft Edge WebDriver 的下载页面。](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/#downloads)
2.  其次，向下滚动到下载部分并选择兼容版本，如下所示：

![下载 EdgeDriver 兼容版本](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Download%20EdgeDriver%20compatible%20version.png)

1.  第三，确保下载正确的版本。在上图中，我们显示了x86 和 x64是 Windows 平台的兼容版本；你可以下载与你的浏览器和操作系统兼容的任何版本。例如，由于我们的浏览器版本是 84 (64 位)，因此我们将为 Release 84 下载 x64 版本。
2.  之后，下载完成后，解压缩 zip 文件并将内容放在任何首选位置。
3.  现在，打开eclipse并创建一个 Java 项目。将所有依赖项添加到项目中。此外，要了解如何在 Eclipse 中设置 Java 项目和 Selenium WebDriver ，请访问我们在[配置 Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)上的详细教程。

现在我们已经下载了EdgeDriver让我们看看如何配置它，以便在Selenium测试脚本中使用它。

### 如何在 Windows 上设置 EdgeDriver？

 要使用 Selenium设置和配置EdgeDriver ， EdgeDriver 可执行文件应该可以在测试脚本中访问。 如果通过以下任何一种方式设置，Selenium 测试可以访问EdgeDriver ：

1.  使用环境变量中的系统属性设置 EdgeDriver。
2.  使用测试脚本中的系统属性设置 EdgeDriver。

让我们理解所有这些并尝试使用Selenium 3 或 Selenium 4 运行我们的测试代码。

#### 如何使用环境变量中的系统属性设置 EdgeDriver？

在 Windows 上，声明系统级变量的方法之一是使用环境变量。用户可以定义用户级环境变量或系统级变量。环境变量可用于将EdgeDriver的路径直接设置到系统属性中。因此，每当需要WebDriver的实例时，都可以轻松地从系统变量中找到EdgeDriver的路径。让我们按照以下步骤为EdgeDriver 设置系统属性路径。

1.  首先，我们需要打开环境变量弹出窗口。为此，请单击搜索栏并搜索“环境变量”。它将搜索并显示“为你的帐户编辑环境变量”，如下图所示。单击“打开”以打开“系统属性”弹出窗口。

![如何在 Windows 10 上访问环境变量](https://www.toolsqa.com/gallery/selnium%20webdriver/5.How%20to%20access%20Environment%20Variables%20on%20Windows%2010.png)

1.  “系统属性”弹出窗口将打开。在弹出窗口中，选择箭头标记的“高级”选项卡。之后，在“高级”选项卡下，单击“环境变量”按钮。

![Windows 上的环境变量](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Environment%20Variable%20on%20Windows.png)

1.  这将打开“环境变量”弹出窗口。在弹出的系统变量部分中，查找下图中标记的“Path”变量。单击Path变量以将其选中。选择后，单击箭头标记的“编辑”按钮。

![Windows 上的系统变量](https://www.toolsqa.com/gallery/selnium%20webdriver/7.System%20Variable%20on%20Windows.png)

1.  第四，在“编辑环境变量”弹出窗口中，单击“新建”按钮。

![在 Windows 上编辑环境变量](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Edit%20Environment%20Variables%20on%20Windows.png)

1.  单击“编辑”按钮将添加一个新行。现在，将EdgeDriver 的可执行文件的父文件夹位置添加到路径中。我们已将驱动程序放置在以下位置“C:\Selenium\edgedriver ”，因此我们添加了与路径变量相同的内容。完成后，单击箭头所指的“确定”按钮。

![EdgeDriver 到系统变量 Path](https://www.toolsqa.com/gallery/selnium%20webdriver/9.EdgeDriver%20to%20Systems%20variables%20Path.png)

### 如何在 Windows 上使用 EdgeDriver 在 Edge 浏览器上运行 Selenium 测试？

我们现在可以 使用EdgeDriver直接初始化WebDriver 实例 ， 如下所示：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class DemoEdge {
    public static void main(String[] args) throws InterruptedException{

        System.out.println("Execution after setting EdgeDriver path in System Variables on Windows!!");
        WebDriver driver = new EdgeDriver();
        driver.get("https://demoqa.com");
        Thread.sleep(3000);
        driver.quit();
        System.out.println("Execution complete on Windows");

    }
}
```

执行上面的代码，你会看到如下结果：

![在 Edge 浏览器上运行 Selenium 测试](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Run%20Selenium%20tests%20on%20Edge%20browser.png)

从控制台结果可以看出，没有WebDriver 错误，说明 WebDriver 设置是正确的。你可以将打印语句视为我们执行的入口点和出口点。相应地，你将能够在你的系统中看到执行情况。

#### 如何在 Selenium 测试脚本中使用系统属性初始化 EdgeDriver？

现在，如果我们想使用特定版本的 EdgeDriver，而不是使用 EdgeDriver 的全局实例，我们可以 通过 在测试脚本本身中明确指定 EdgeDriver的路径来实现相同的目的。我们只需要添加一行代码来为 EdgeDriver 设置系统属性，如下所示：

```java
System.setProperty("webdriver.edge.driver", "<Path of the EdgeDriver Executable>")
;
```

让我们修改上面使用的代码，看看我们是否可以成功启动Edge浏览器。修改后的代码如下所示：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class DemoEdge {
    public static void main(String[] args) throws InterruptedException{

        System.out.println("EdgeDriver execution on Windows!!");
        System.setProperty("webdriver.edge.driver", "E:\\EdgeDriver\\85\\msedgedriver.exe");
        WebDriver driver = new EdgeDriver();
        driver.get("https://demoqa.com");
        Thread.sleep(3000);
        driver.quit();
        System.out.println("Execution complete on Windows");

    }
}
```

你将看到 [demoqa.com](https://demoqa.com/) 在 Edge 浏览器中打开，没有任何错误和异常。

![边缘浏览器上的硒测试](https://www.toolsqa.com/gallery/selnium%20webdriver/11.selenium%20test%20on%20edge%20browser.png)

因此，通过这种方式，你可以通过在setProperty()方法中提供EdgeDriver的显式路径，在指定版本的EdgeDriver上运行你的Selenium测试用例。

## 如何在 macOS 上安装 EdgeDriver？

EdgeDriver 在 macOS上的安装和设置 与 Windows 平台几乎相同，唯一的区别是 macOS的可执行文件 会有所不同。此外，我们将EdgeDriver 可执行文件包含在系统的 PATH变量中的方式有点不同。随后，让我们看看如何 在 macOS上安装和设置EdgeDriver：

### 如何在 macOS 中下载 EdgeDriver？

在macOS 上，我们可以 从[Mircosoft Edge 官方网站](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/#downloads)下载EdgeDriver 。因此，根据“Edge 浏览器”的版本，单击相应的EdgeDriver for mac，如下突出显示：

![适用于 macOS 的边缘驱动程序](https://www.toolsqa.com/gallery/selnium%20webdriver/12.EdgeDriver%20for%20macOS.png)

由于我们机器上确实有Edge 浏览器版本 85，所以我们正在下载相应的兼容版本的EdgeDriver。

它将下载一个 zip 文件，你可以将其解压缩到你选择的任何文件夹中。解压后会显示 EdgeBrowser的可执行文件， 如下图：

![适用于 macOS 的 EdgeDriver 可执行文件](https://www.toolsqa.com/gallery/selnium%20webdriver/13.EdgeDriver%20executable%20for%20macOS.png)

所以，现在你的机器上有可用的 EdgeDriver可执行文件，我们可以在我们的测试脚本中使用它。随后，让我们看看如何 在 macOS上设置EdgeDriver 并在 Selenium 测试脚本中使用：

### 如何在 macOS 上设置 EdgeDriver？

现在你已经下载了EdgeDriver，下一步是设置它，以便你可以在测试脚本中使用它。同样在 macOS 上 ，我们可以按照与在 Windows 上相同的方式来设置 EdgeDriver：

1.  使用系统的 PATH 变量设置 EdgeDriver。
2.  使用测试脚本中的系统属性设置 EdgeDriver。

第二点是与Windows平台相同的设置，因为我们使用 JAVA 进行测试开发，而 JAVA 是平台无关的，跨平台行为相同。那么，让我们看看如何 使用系统的 PATH 变量设置EdgeDriver ：

#### 如何使用系统的 PATH 变量设置 EdgeDriver？

正如我们上面提到的，使可执行文件在 macOS上全局可用的最简单方法之一是将可执行文件复制到PATH 变量中已有的任何文件夹下。让我们按照下面提到的步骤来实现相同的目的：

1.  首先，在终端上使用命令“echo $PATH”识别包含在 PATH 变量中的文件夹。它将给出示例输出，如下所示：

![验证 macOS PATh 变量中的文件夹](https://www.toolsqa.com/gallery/selnium%20webdriver/14..Validate%20folders%20in%20macOS%20PATh%20variable.png)

1.  其次，正如我们所见，多个目录已经是 PATH 变量的一部分。假设我们选择“/usr/local/bin”作为占位符目录来保存EdgeDriver可执行文件。
2.  第三，使用mv命令将EdgeDriver 可执行文件从下载目录复制到 “/usr/local/bin”目录，如下所示：

```java
mv msedgedriver /usr/local/bin/ 

or 

cp msedgedriver /usr/local/bin/
```

### 如何在 Mac 上使用 EdgeDriver 在 Edge 浏览器上运行 Selenium 测试？

现在你的EdgeDriver已准备好在你的 Selenium 测试脚本中使用。因此，现在我们将编写一个简单的程序并在macOS平台上执行它。

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class DemoEdge {
    public static void main(String[] args) throws InterruptedException{

        System.out.println("EdgeDriver execution on macOS!!");
        WebDriver driver = new EdgeDriver();
        driver.get("https://demoqa.com");
        Thread.sleep(3000);
        driver.quit();
        System.out.println("Execution complete on macOS");

    }
}
```

在执行相同的操作时，你可以在控制台窗口中找到结果：

![在 macOS 上对边缘 EdgeDriver 执行的硒测试](https://www.toolsqa.com/gallery/selnium%20webdriver/15.selenium%20test%20on%20edge%20EdgeDriver%20execution%20on%20macOS.png)

你可以看到执行成功，没有任何错误。两个打印语句都得到显示，这表明我们的执行没有遇到任何错误。那么你是否看到在macOS中运行EdgeDriver测试 是多么容易 ？与Windows系统不同，你必须记住驱动程序可执行文件的路径，只需将驱动程序放在macOS中的某个位置，我们的生活就变得如此简单！

## 关键要点

-   微软在 Chromium 引擎上从头开始重建了 Edge。此外，凭借大约 7% 的市场份额，Edge 仍然有理由在 Edge 上测试你的应用程序的兼容性。
-   此外，Selenium 的跨浏览器功能允许用户在所有不同类型的当代浏览器上执行测试，包括但不限于 Microsoft Edge。
-   除了上述之外，MSEdgeDriver 是一个独立的服务器，它在 Selenium WebDriver 和 Microsoft Edge 之间进行通信，以在 Edge 浏览器上执行所有 selenium 测试。