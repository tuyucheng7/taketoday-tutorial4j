[Internet Explorer](https://en.wikipedia.org/wiki/Internet_Explorer)浏览器仍然被一部分人选择用于互联网浏览。这些用户主要是长期使用IE浏览器，习惯了还想继续使用的用户。此外，必须记住，IE是市场上最早建立的品牌之一。由于 Selenium 提供了在多个浏览器中运行我们的测试的选项，因此Selenium 与 IE 浏览器混合可用于测试任何应用程序。IE 有一个驱动程序，它在Selenium WebDriver和IE之间创建连接然后在 Internet Explorer 上执行 Selenium 测试。 让我们通过讨论以下主题探索如何在 Internet Explorer 上运行 Selenium 测试：

-   什么是 Selenium IE 驱动程序？
    -   Selenium IE 驱动程序的先决条件是什么？
-   如何在 Windows 上安装 IE 驱动程序？
    -   如何在 Windows 上下载 IE 驱动程序？
    -   另外，如何在 Windows 上配置 IE 驱动程序？
    -   如何在 Internet Explorer 浏览器上运行 Selenium 测试？
-   如何在 Mac OS 上下载 IE 驱动程序？

## 什么是 Selenium Internet Explorer 驱动程序或 IE 驱动程序？

IE Driver是使用户能够在IE浏览器上执行Selenium测试用例的连接。它是一个独立的服务器，管理开源的Selenium WebDriver 协议。Selenium 测试通过JsonWireProtocol与IE Driver通信，它将 Selenium 中的命令转换为IE浏览器上的操作。

![在 Internet Explorer 上运行 Selenium 测试：Selenium IE 驱动程序通信](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Run%20Selenium%20tests%20on%20Internet%20Explorer%20Selenium%20IE%20Driver%20communication.png)

IE 驱动程序的主要目标是与Internet Explorer 进行通信。[没有它，在 IE 上执行Selenium](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)测试用例是不可行的。可以通过实例化 IE 驱动程序的对象并将其分配 给WebDriver对象来使用 IE 驱动程序。然后将其用于浏览器操作。

### Selenium Internet Explorer 驱动程序或 IE 驱动程序的前提条件是什么？

在我们开始在Selenium中创建测试或配置IE 驱动程序以便我们可以在 Internet Explorer 上运行 Selenium 测试之前，我们应该注意我们系统上的一些先决条件：

-   Java JDK - 我们需要用于 Java 程序的[JDK](https://www.toolsqa.com/selenium-webdriver/install-java/)或 Java Development Kit。它有JRE和多种开发工具，如调试器和编译器。
-   Java IDE - 集成开发环境 (IDE) 协助编写 Java 程序。它具有用于最终用户编程的多种功能。在这里，我们将使用[Eclipse IDE。](https://www.toolsqa.com/selenium-webdriver/download-and-start-eclipse/)也可以使用任何其他替代 Java IDE。
-   Selenium WebDriver - 要构建 Selenium 测试，我们需要 Selenium WebDriver。你可以从官方 Selenium 站点下载它。更详细的内容可以参考专题：[配置Selenium WebDriver。](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)

对于本教程，我们可以使用Selenium 3或Selenium 4版本。

## 如何在 Windows 上安装 IE 驱动程序？

让我们进一步讨论如何使用你的Selenium Java 项目设置IE 驱动程序并在 IE 上运行测试用例。最初，我们将下载IE 驱动程序。让我们研究一下我们如何在Windows操作系统上执行它？

### 如何在 Windows 上下载 IE 驱动程序？

在下载IE 驱动程序之前，我们会注意系统上 IE 浏览器的版本。我们需要下载的IE Driver版本必须与 IE 浏览器版本兼容。按照下面列出的步骤检查浏览器版本：

1.  首先，要了解你机器上的 IE 浏览器版本，请单击菜单中的帮助。

![IE 帮助菜单](https://www.toolsqa.com/gallery/selnium%20webdriver/2.IE%20Help%20menu.png)

1.  其次，单击子菜单中的关于 Internet Explorer 。

![IE 关于 Internet Explorer 子菜单](https://www.toolsqa.com/gallery/selnium%20webdriver/3.IE%20About%20Internet%20Explorer%20sub%20menu.png)

1.  单击关于 Internet Explorer 选项后，将打开以下弹出窗口。因此，我们将得到如下图所示的IE版本信息：

![在 Internet Explorer 上运行 Selenium 测试：检查 IE 版本](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Run%20Selenium%20tests%20on%20Internet%20Explorer%20Check%20IE%20version.png)

现在，我们有了IE浏览器的版本，我们就可以进行下一步了。使用以下步骤配置IE 驱动程序：

1.  首先，转到[链接。](https://www.selenium.dev/downloads/)在下图中，我们看到了适用于 Windows 的32 位 Windows IE和64 位 Windows IE。你可以下载与你的操作系统和浏览器兼容的IE 驱动程序的 zip 文件。

![在 Internet Explorer 上运行 Selenium 测试：选择兼容的 Selenium IE 驱动程序](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Run%20Selenium%20tests%20on%20Internet%20Explorer%20Select%20compatible%20Selenium%20IE%20Driver.png)

1.  然后，当下载成功时，解压缩zip 文件并将其保存在特定位置。

![IE 驱动程序 zip 文件](https://www.toolsqa.com/gallery/selnium%20webdriver/6.IE%20Driver%20zip%20file.png)

1.  打开Eclipse后创建一个 Java 项目。合并所有项目依赖项。要了解如何在 Eclipse 中使用Selenium WebDriver创建Java项目，请阅读[Configure Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)中的完整教程。
2.  作为下一步，我们需要使这个IE 驱动程序可以被 Selenium 测试访问。

让我们讨论如何配置IE 驱动程序 ，以便它可以在 Selenium 脚本中使用。

### 如何在 Windows 上配置 IE 驱动程序？

要使用Selenium配置IE 驱动程序，以便我们可以在 Internet Explorer 上运行 Selenium 测试，IE 驱动程序可执行文件应该可用于测试脚本。Selenium 测试用例可以通过以下任何一种方式接近IE 驱动程序：

1.  通过环境变量中的系统属性设置 IE 驱动程序。
2.  通过脚本中的系统属性设置 IE 驱动程序。

让我们了解所有这些并尝试使用 Selenium 3 或 Selenium 4 执行我们的代码。

#### 如何通过环境变量中的系统属性配置 IE 驱动程序？

我们可以通过环境变量定义系统级变量。此外，用户可以选择声明系统变量或用户级变量。此外，这里声明的变量可以被系统上执行的每个程序使用。

此外，我们可以借助环境变量来设置IE Driver路径。由于我们有一个 WebDriver 实例，它默认会从系统变量中识别IE 驱动程序路径。随后，让我们探讨实现该目标的步骤。

1.  首先，我们打开环境变量弹出窗口。为此，单击搜索框并键入env。因此，它将显示编辑系统环境变量，如下图所示。点击它。

![搜索系统变量](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Search%20System%20variables.png)

1.  其次，将出现系统属性弹出窗口。转到弹出窗口中的“高级”选项卡。接下来，在“高级”选项卡中，单击“环境变量”。

![在 Internet Explorer 上运行 Selenium 测试：Windows 上的环境变量](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Run%20Selenium%20tests%20on%20Internet%20Explorer%20Environment%20variables%20on%20Windows.png)

1.  第三，它将打开环境变量弹出窗口。在系统变量部分中，搜索下图中突出显示的路径变量。然后，选择它。最后，选择后，单击图像中突出显示的编辑。

![环境变量Pth](https://www.toolsqa.com/gallery/selnium%20webdriver/9.EnvironmentVariablesPth.png)

1.  第四，当编辑环境变量弹出窗口打开时，单击新建。

![在 Internet Explorer 上运行 Selenium 测试：编辑环境变量](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Run%20Selenium%20tests%20on%20Internet%20Explorer%20Edit%20Environment%20variable.png)

1.  最后，添加IE Driver文件夹路径的位置。我们将驱动程序保存在C:\Selenium\iedriver 位置，因此我们将其添加到路径变量中。然后单击“确定”。

![在 Internet Explorer 上运行 Selenium 测试：添加 IE Driver 文件夹的路径](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Run%20Selenium%20tests%20on%20Internet%20Explorer%20Add%20path%20of%20IE%20Driver%20folder.png)

### 如何在 Internet Explorer 浏览器上运行 Selenium 测试？

那么，现在让我们看看如何在 Internet Explorer 上运行 Selenium 测试。因此，我们可以在IE Driver的帮助下初始化WebDriver实例，如下所述：

```java
package demoPackageIE;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

 
public class IEDriverDemo {
    public static void main(String[] args) throws InterruptedException{
 
        System.out.println("Setting IE Driver Path in System Variables");    
        WebDriver driver=new InternetExplorerDriver();  
      	driver.get("https://google.com");
        Thread.sleep(3000);
        driver.quit();
         System.out.println("Execution done"); 
 
    }
}
```

运行上述代码时，输出应为：

![系统变量中设置的IE驱动路径](https://www.toolsqa.com/gallery/selnium%20webdriver/12.IE%20Driver%20path%20set%20in%20System%20Variables.png)

从控制台输出可以看出，WebDriver没有问题，说明设置是正确的。另外，我们可以在执行的开始和结束时找到打印语句。

#### 如何在脚本中通过系统属性初始化IE Driver？

我们可以直接在脚本中指定路径来初始化IE Driver。简单来说，我们必须添加以下代码行来设置IE 驱动程序的系统属性，如下所示：

```java
System.setProperty("webdriver.ie.driver", "<Path of the IE Driver Executable>");
```

让我们更新上面使用的代码并确认我们可以准确启动 IE 浏览器。更新后的代码应该与此类似：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

 
public class DemoIE {
    public static void main(String[] args) throws InterruptedException{
 
        System.out.println("IE Driver execution on Windows!!");
        System.setProperty("webdriver.ie.driver", "E:\\IEDriver\\IEDriverServer.exe");
        WebDriver driver=new InternetExplorerDriver();  
      	driver.get("https://google.com");
        Thread.sleep(3000);
        Capabilities c = ((RemoteWebDriver) driver).getCapabilities();
        // get the browser name, version and platform with Capabilities object and getCapability() method
        System.out.println("Current browser Name is : " +c.getBrowserName());
        System.out.println("Current browser Version is : " +c.getVersion());
        System.out.println("Current platform is : " +c.getPlatform().toString());
        driver.quit();
 
    }
}
```

你会发现[google.com](https://www.google.com/)在 IE 浏览器中打开没有任何问题和异常。

![IE浏览器启动](https://www.toolsqa.com/gallery/selnium%20webdriver/13.IE%20Browser%20launch.png)

因此，通过这种方式，你可以通过在setProperty()方法中提及IE 驱动程序的直接路径，在特定版本的IE上执行你的Selenium测试。此外，日志指出我们的WebDriver会话以 print 语句开始，并确认测试是在 Windows 的 IE 版本 11 中执行的。

## 如何在 Mac OS 上下载 IE 驱动程序？

macOS 不支持 IE。但是如果我们必须在 IE 中执行我们的 Selenium 测试，有多种方法可以完成：

1.  Oracle VM Virtual Box - 要在 macOS 上验证 IE，我们可以在我们的系统上[安装VM Virtual Box 。](https://www.virtualbox.org/)
2.  在云上进行跨浏览器测试——这是进行跨浏览器测试的流行方法之一。[LambdaTest](https://www.lambdatest.com/feature)是执行跨浏览器检查的测试平台之一。

## 关键要点

-   IE 已经弃用，但我们仍然使用它进行跨浏览器测试。
-   此外，请确保在执行前将 IE 的浏览器缩放级别设置为 100%。