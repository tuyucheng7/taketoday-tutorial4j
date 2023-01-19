[谷歌浏览器](https://en.wikipedia.org/wiki/Google_Chrome)目前主导着全球网络浏览器市场份额。易用性与多种有用的功能相结合，使其在用户中广受欢迎。鉴于其重要性和高用户覆盖率，质量工程师在 Chrome 浏览器上测试网站/网络应用程序变得至关重要。由于 Selenium 提供跨浏览器功能，允许用户在不同浏览器上运行测试用例，因此Selenium 与 Chrome 浏览器成为在浏览器平台上测试任何 Web 应用程序的主要组合。Chrome提供了一个驱动程序，可以建立Selenium WebDriver和Google Chrome之间的连接，并在 Chrome 浏览器中运行Selenium 测试. 让我们通过以下部分的详细信息来了解Selenium ChromeDriver的详细信息和用法，以便在Chrome 浏览器上运行自动化测试：

-   什么是 Selenium ChromeDriver？
    -   Selenium ChromeDriver 的先决条件是什么？
-   如何在 Windows 上安装 ChromeDriver？
    -   如何在 Windows 上下载 ChromeDriver？
    -   以及如何在 Windows 上设置 ChromeDriver？
    -   以及如何在 Chrome 浏览器上运行 Selenium 测试？
-   如何在 macOS 上安装 ChromeDriver？
    -   如何在 macOS 上下载 ChromeDriver？
    -   以及如何在 macOS 上设置 ChromeDriver？
    -   如何使用 Homebrew 安装 ChromeDriver？

## 什么是 Selenium ChromeDriver？

ChromeDriver是允许用户在 Chrome 浏览器上运行他们的 Selenium 测试的通信媒介。它是一个独立的服务器，实现了开源的Selenium WebDriver Chromium 协议。Selenium测试使用 JsonWireProtocol 与 ChromeDriver 交互，它将Selenium命令转换为Chrome浏览器上的相应操作。

![ChromeDriver 在 Chrome 上运行测试](https://www.toolsqa.com/gallery/selnium%20webdriver/1.ChromeDriver%20running%20tests%20on%20Chrome.png)

ChromeDriver的唯一目的是启动Google Chrome并与之交互。如果不使用ChromeDriver，就不可能在 chrome 浏览器上运行[Selenium测试。](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)这就是为什么它是在 Chrome 上执行测试最重要的先决条件之一。通过实例化ChromeDriver的对象，将其分配给WebDriver对象，并将该对象用于基于浏览器的操作，可以轻松使用ChromeDriver 。

### Selenium ChromeDriver 的先决条件是什么？

在我们开始编写Selenium测试或设置ChromeDriver之前，我们的系统应该具备一些先决条件：

1.  Java JDK：我们需要[JDK](https://www.infoworld.com/article/3296360/what-is-the-jdk-introduction-to-the-java-development-kit.html)或 Java Development Kit 来编写 Java 程序。它包含[JRE](https://www.infoworld.com/article/3304858/what-is-the-jre-introduction-to-the-java-runtime-environment.html)和其他开发工具，包括编译器和调试器。由于我们将用 java 编写 selenium 测试，因此必须拥有 JDK。你可以从此处获取有关 JDK 的更多信息并阅读其安装指南：[如何安装 Java？](https://www.toolsqa.com/selenium-webdriver/install-java/)
2.  Java IDE：IDE 或集成开发环境有助于编写 Java 程序。它为用户提供了许多不同的功能，以减轻他们的编程需求。对于本教程，我们将使用 Eclipse IDE，尽管任何其他 Java IDE 都可以。要了解更多信息或了解如何安装 Eclipse，请访问此处： [安装 Eclipse](https://www.toolsqa.com/selenium-webdriver/download-and-start-eclipse/)。
3.  Selenium WebDriver：要开发 Selenium 测试，我们需要 Selenium WebDriver。[你可以从Selenium官方网站](https://www.selenium.dev/)下载Selenium WebDriver ，你可以在教程中学习如何配置Selenium；[配置 Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)。对于本教程，我们将使用Selenium 4。

## 如何在 Windows 上安装 ChromeDriver？

现在，我们已经了解了ChromeDriver是什么以及为什么要这样做，我们需要它来在 chrome 浏览器上执行 Selenium 测试。让我们更进一步，了解如何使用Selenium Java 项目设置ChromeDriver并在 Chrome 上执行测试。第一部分是下载ChromeDriver。让我们看看我们如何在 Windows 平台上做同样的事情？

### 如何在 Windows 上下载 ChromeDriver？

在我们下载ChromeDriver之前，我们需要检查你系统上Chrome 浏览器的版本。ChromeDriver对Chrome浏览器版本有直接的兼容性依赖，所以需要下载兼容版本的ChromeDriver。按照下面提到的步骤下载一个。与你系统上的Chrome浏览器 兼容的ChromeDriver：

1.  首先，要检查你机器上的 Chrome 浏览器版本，请单击浏览器右上角的三个点
2.  其次，单击菜单中的帮助。
3.  第三，点击子菜单中的关于谷歌浏览器。

![验证 Chrome 浏览器版本](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Validate%20Chrome%20browser%20version.png)

1.  单击“关于谷歌浏览器”选项后，将打开以下页面。因此，你将获得如下图所示的 Chrome 版本详细信息：

![Chrome 浏览器版本](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Chrome%20browser%20version.png)

现在，由于我们已经获得了Chrome浏览器版本，所以我们可以下载兼容的ChromeDriver。此外，要下载ChromeDriver，请导航至[ChromeDriver 官方网站](https://chromedriver.chromium.org/downloads)的链接。按照以下步骤下载ChromeDriver可执行文件：

1.  在[ChromeDriver 下载页面上](https://chromedriver.chromium.org/downloads)，会有不同ChromeDriver版本的链接。根据你的 Chrome 浏览器版本，下载对应的ChromeDriver，如下图所示。随后，单击你需要下载的ChromeDriver版本。由于我们之前的 Chrome 浏览器版本为“ 84 ”，所以我们将下载对应的ChromeDriver。

![chromedriver 页面](https://www.toolsqa.com/gallery/selnium%20webdriver/4.chromedriver%20page.png)

1.  其次，单击“ ChromeDriver 84.0.4147.30 ”链接将带你进入ChromeDriver索引页面。在这里，你将根据你的操作系统获得不同的ChromeDriver选项。此外，对于 Windows 操作系统，你可以选择下图中标记的Win32版本。是的，即使你的系统上安装了 64 位 Windows，Win32版本也可以正常工作。

![特定于操作系统的 ChromeDriver](https://www.toolsqa.com/gallery/selnium%20webdriver/5.ChromeDriver%20specific%20to%20operating%20system.png)

1.  第三，下载完成后，解压缩 zip 文件并将“ chromedriver.exe ”放在系统上的任何首选位置。

现在我们已经下载了ChromeDriver，我们将打开 Eclipse 并创建一个新的 java 项目。此外，我们会将所有 selenium 依赖项添加到项目中。此外，要了解有关使用 Eclipse 设置 Selenium 的更多信息，你可以访问我们之前的教程[Configure Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)。

下一步，我们需要使下载的 ChromeDriver 可执行文件可用于 Selenium 测试。随后，让我们看看如何设置ChromeDriver，以便我们可以在 Selenium 测试用例中使用它：

### 如何在 Windows 上设置 ChromeDriver？

要使用 Selenium设置和配置ChromeDriver ，应该可以在测试脚本中访问ChromeDriver可执行文件。 此外，如果通过以下任何一种方式进行设置，Selenium 测试可以访问ChromeDriver ：

1.  使用环境变量中的系统属性设置 ChromeDriver。
2.  使用测试脚本中的系统属性设置 ChromeDriver。

让我们了解所有这些并尝试使用 Selenium 3 或 Selenium 4运行我们的测试代码。

#### 如何使用环境变量中的系统属性设置 ChromeDriver？

在 Windows 操作系统上，声明系统级变量的方法之一是使用环境变量。用户可以定义用户级环境变量或系统变量。此外，此处定义的变量可供系统上运行的所有程序访问。我们可以使用环境变量来设置ChromeDriver的路径。因此，每当我们创建WebDriver的实例时，它会自动从系统变量中检测ChromeDriver的路径并可以使用它。随后，让我们看看我们可以通过哪些步骤来做到这一点。

1.  首先，我们需要打开环境变量弹出窗口。为此，请单击搜索栏并搜索“环境变量”。它将搜索并显示“为你的帐户编辑环境变量”，如下图所示。之后，单击“打开”以打开“系统属性”弹出窗口。![在 Windows 10 上访问环境变量](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Accessing%20Environment%20Variables%20on%20Windows%2010.png)
2.  其次，将打开“系统属性”弹出窗口。在弹出窗口中，选择箭头标记的“高级”选项卡。之后，在“高级”选项卡中，单击“环境变量”按钮。

![Windows 上的环境变量](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Environment%20Variables%20on%20Windows.png)

1.  第三，这将打开“环境变量”弹出窗口。在弹出的系统变量部分中，查找下图中标记的“路径”变量。之后，单击路径变量将其选中。选择后，单击箭头标记的“编辑”按钮。

![Windows 上的系统变量](https://www.toolsqa.com/gallery/selnium%20webdriver/8.System%20Variables%20on%20Windows.png)

1.  第四，弹出“编辑环境变量”后，单击“新建”按钮。

![在 Windows 上编辑环境变量](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Edit%20Environment%20Variable%20on%20Windows.png)

1.  第五，将ChromeDriver的文件夹位置添加到路径中。我们已将驱动程序放置在以下位置“ C:\Selenium\chromedriver ”，因此我们添加了与路径变量相同的内容。完成后，单击箭头所指的“确定”按钮。

![Windows平台系统变量添加Selenium ChromeDriver Path](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Add%20Selenium%20ChromeDriver%20Path%20in%20System%20variables%20on%20Windows%20Platform.png)

#### 如何使用 ChromeDriver 在 Chrome 浏览器上运行 Selenium 测试？

最后，我们现在可以使用ChromeDriver直接初始化 WebDriver 实例，如下所示：

```java
package demoPackage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ChromeDriverDemo {
	public static void main(String[] args) throws InterruptedException{
		 
		 System.out.println("Execution after setting ChromeDriver path in System Variables");
		 WebDriver driver=new ChromeDriver();
		 driver.get("https://demoqa.com");
		 Thread.sleep(3000);
		 driver.quit();
		 System.out.println("Execution complete");
		 
		 }

}
```

执行上面的代码，你会看到下面的结果。

![selenium webdriver chrome 在系统变量中设置 ChromerDriver 路径时在 Chrome 中运行 Selenium 测试](https://www.toolsqa.com/gallery/selnium%20webdriver/11.selenium%20webdriver%20chrome%20Running%20Selenium%20tests%20in%20Chrome%20when%20ChromerDriver%20path%20setup%20in%20System%20Variables.png)

从控制台结果可以看出，没有WebDriver错误，说明 WebDriver设置是正确的。此外，你可以将打印语句视为我们执行的入口点和出口点。相应地，你将能够在你的系统中查看执行情况。

#### 如何使用 Selenium 测试脚本中的系统属性初始化 ChromeDriver？

如果我们想使用特定版本的ChromeDriver而不是使用ChromeDriver的全局实例，我们可以通过在测试脚本本身中明确指定ChromeDriver的路径来实现相同的目的。换句话说，我们需要添加一行代码来设置ChromeDriver的系统属性，如下所示：

```java
System.setProperty("webdriver.chrome.driver", "<Path of the ChromeDriver Executable>");
```

最后，让我们修改上面使用的代码，看看我们是否可以成功启动 Chrome 浏览器。修改后的代码如下所示：

```java
package demoPackage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ChromeDriverDemo {
	public static void main(String[] args) throws InterruptedException{
		 
		 System.out.println("Execution after setting ChromeDriver path in System setProperty method");
		 System.setProperty("webdriver.chrome.driver", "E:\\drivers\\ChromeDrivers\\85\\chromedriver.exe");
		 WebDriver driver=new ChromeDriver();
		 driver.get("https://demoqa.com");
		 Thread.sleep(3000);
		 driver.quit();
		 System.out.println("Execution complete");
		 
		 }

}
```

你将看到 [demoqa.com](https://demoqa.com/) 在Chrome浏览器中打开，没有任何错误和异常。

![selenium webdriver chrome Selenium 测试在 Chrome 中使用 SetProperty 方法运行](https://www.toolsqa.com/gallery/selnium%20webdriver/12.selenium%20webdriver%20chrome%20Selenium%20tests%20running%20in%20Chrome%20using%20SetProperty%20method.png)

如果你注意到了，这里我们使用的是 ChromeDriver版本 85，而不是默认的全局ChromerDriver版本 84。执行日志表明我们的WebDriver会话从一开始显示的打印语句开始。红色突出显示的行是与浏览器会话对应的一些浏览器日志。此外，你可以看到浏览器在你的系统中打开，网站打开后，浏览器会话关闭。

## 如何在 macOS 上安装 ChromeDriver？

ChromeDriver在 macOS 上的安装和设置 与Windows 平台 几乎相同 。唯一的区别是 macOS的可执行文件 会有所不同，而且我们 在系统的 PATH 变量中包含ChromeDriver可执行文件的方式也有所不同。让我们看看如何在 macOS 上安装和设置ChromeDriver ：

### 如何在 macOS 上下载 ChromeDriver？

在macOS 上，我们可以使用以下任意一种方式下载ChromeDriver ：

-   从[Chromium 网站下载可执行文件](https://chromedriver.chromium.org/)
-   使用任何包管理器下载，例如[Homebrew](https://brew.sh/)。

 让我们了解一下在 macOS 上下载和设置ChromeDriver的这两种方式的详细信息：

#### 如何从 Chromium 网站下载适用于 macOS 的 ChromeDriver？

你可以下载ChromerDrive for macOS，与我们为 Windows 平台所做的一样，不同的是，现在选择macOS平台的二进制文件，如下所示：

![适用于 macOS 的 Selenium ChromeDriver 可下载二进制文件](https://www.toolsqa.com/gallery/selnium%20webdriver/13.Selenium%20ChromeDriver%20downloadable%20binary%20for%20macOS.png)

它将下载一个zip文件，你可以将其解压缩到你选择的任何文件夹中。解压后会显示ChromeDriver的可执行文件，如下图：

![macOS 上的 Selenium ChromeDriver 可执行文件](https://www.toolsqa.com/gallery/selnium%20webdriver/14.Selenium%20ChromeDriver%20executable%20file%20on%20macOS.png)

所以，现在你的机器上有可用的ChromeDriver可执行文件，我们可以在我们的测试脚本中使用它。随后，让我们看看如何在 macOS 上设置ChromeDriver 并在 Selenium 测试脚本中使用：

### 如何在 macOS 上设置 ChromeDriver？

现在你已经下载了ChromeDriver，下一步是将其设置为在你的测试脚本中使用它。在 macOS 上，我们也可以按照与在 Windows 上相同的方式来设置 ChromeDriver：

1.  使用系统的 PATH 变量设置 ChromeDriver。
2.  使用测试脚本中的系统属性设置 ChromeDriver。

第二点是与Windows平台相同的设置，因为我们使用JAVA进行测试开发，而JAVA是平台无关的，跨平台行为相同。那么，让我们看看如何 使用系统的PATH变量设置ChromerDriver ：

#### 如何使用系统的 PATH 变量设置 ChromeDriver？

正如我们上面提到的，使可执行文件在 macOS上全局可用的最简单方法之一是将可执行文件复制到PATH 变量中已有的任何文件夹下。让我们按照下面提到的步骤来实现相同的目的：

首先，在终端上使用命令“ echo $PATH ”识别 PATH 变量中包含的文件夹。它将给出示例输出，如下所示：

![验证 macOS PATh 变量中的文件夹](https://www.toolsqa.com/gallery/selnium%20webdriver/15.Validate%20folders%20in%20macOS%20PATh%20variable.png)

1.  其次，正如我们所见，多个目录已经是 PATH 变量的一部分。假设我们选择“ /usr/local/bin ”作为占位符目录来保存ChromeDriver可执行文件。
2.  第三，使用 mv 命令将ChromeDriver 可执行文件从下载目录复制到“ /usr/local/bin ”目录，如下所示：

```java
mv chromedriver /usr/local/bin/
```

现在你的 ChromeDriver已准备好在你的 Selenium 测试脚本中使用。因此，现在我们将编写一个简单的程序并在macOS平台上执行它。

```java
package demoPackage;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ChromeDriverDemo {
	public static void main(String[] args) throws InterruptedException{
		 
		System.out.println("ChromeDriver execution on mac!!");
		 WebDriver driver=new ChromeDriver();
		 driver.get("https://demoqa.com");
		 Thread.sleep(3000);
		 driver.quit();
		 System.out.println("Execution complete on macOS");
		 
	}
}
```

在执行相同的操作时，你可以在控制台窗口中找到结果：

![Selenium ChromeDriver 在 macOS 上的执行](https://www.toolsqa.com/gallery/selnium%20webdriver/16.Selenium%20ChromeDriver%20execution%20on%20macOS.png)

你可以看到执行成功，没有任何错误。两个打印语句都得到显示，这表明我们的执行没有遇到任何错误。那么你是否看到在 macOS 中运行 ChromeDriver 测试是多么容易 ？与 Windows 系统不同，你必须记住驱动程序可执行文件的路径，只需将驱动程序放在macOS中的某个位置，我们的生活就变得如此简单！

### 如何使用 Homebrew 安装 ChromeDriver？

Homebrew是macOS上可用的包管理器之一，它可以下载任何注册为Homebrew包的二进制文件。幸运的是， ChromeDriver可以作为 Homebrew 包使用，我们可以使用如下简单的命令下载并设置它：

```java
brew cask install chromedriver
```

当我们运行上面的命令时，它会在“ /usr/local/bin ”目录中下载并安装ChromeDriver ，从上面命令的以下输出可以验证这一点：

![使用 Homebrew 安装 selenium webdriver ChromeDriver](https://www.toolsqa.com/gallery/selnium%20webdriver/17.selenium%20webdriver%20ChromeDriver%20installation%20using%20Homebrew.png)

正如我们所见，ChromeDriver的最新稳定版本已下载并安装在“ /usr/local/bin ”目录中，这使其成为PATH变量的一部分，系统上的所有应用程序都可以访问它。

因此，通过这种方式，ChromeDriver使用单个命令在macOS上安装和设置。其他包管理器，例如 NPM，也提供了安装ChromeDriver的功能，可以根据你在你的机器上使用的包管理器进行探索。

## 关键要点

-   就用户份额而言，Chrome 浏览器是最受欢迎的浏览器之一。此外，2/3 的网络用户使用它。
-   Selenium 的跨浏览器功能允许用户使用 ChromeDriver 配置和运行他们的 Selenium 测试，以在 Chrome 浏览器上执行所有测试。
-   此外，ChromeDriver 是一个独立的服务器，它与 Selenium WebDriver 交互以在 Chrome 浏览器上执行所有 selenium 测试。
-   此外，ChromeDriver 提供了针对每个平台的可执行文件，例如 Windows、macOS 等，可以下载并用于在 Chrome 浏览器上执行 Selenium 测试。