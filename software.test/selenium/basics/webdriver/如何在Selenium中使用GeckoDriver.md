[Selenium](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)是当今 IT 行业中最常用的 Web 自动化工具之一。随着用户群不断增加，新功能不断添加，并且随着时间的推移，新版本的Selenium 正在推出。最近，随着 Selenium 3 和 4 的推出， Gecko 驱动程序的使用已成为必需。随后，在本文中，我们将了解有关Selenium GeckoDriver的所有内容，并了解如何在我们的 selenium 脚本中使用它。我们将在本文中主要关注以下几点：

-   什么是 GeckoDriver？
    -   GeckoDriver 是如何工作的？
    -   为什么要使用它？
-   如何在 Windows 上安装 GeckoDriver？
    -   如何在 Windows 上下载 GeckoDriver？
    -   同样，如何在 Windows 上进行设置？
-   如何在 macOS 上安装 GeckoDriver？
    -   如何在 macOS 中下载 GeckoDriver？
    -   同样，如何在 macOS 上设置 GeckoDriver？
-   如何使用 GeckoDriver 在 Headless 模式下运行测试？
-   使用 GeckoDriver 时常见的异常是什么？

## 什么是 GeckoDriver？

在了解GeckoDriver之前，让我们先了解Gecko 。Gecko 是由Mozilla 开发的 Web 浏览器引擎。Mozilla Foundation 或 Mozilla Corporation开发的不同应用程序使用它。GeckoDriver使用C++ 和 JavaScript，自 2016 年起使用 Rust。此外，我们可以在Windows、macOS、Linux、Unix 和 BSD操作系统上使用它。

GeckoDriver是Selenium 测试和Firefox 浏览器之间的链接换句话说， GeckoDriver是一个代理，它在[兼容 W3C WebDriver 的客户端](https://developer.mozilla.org/en-US/docs/Web/WebDriver)和[基于 Gecko 的浏览器](https://en.wikipedia.org/wiki/Gecko_(software)#:~:text=Other web browsers using Gecko,the OLPC XO-1 computer.)(如 Firefox)之间进行交互。因此，有时人们在表示它是GeckoDriver时，通常将其称为Firefox 驱动程序。简而言之， GeckoDriver 或 Firefox 驱动程序将我们的 Selenium 测试链接到 Mozilla Firefox 浏览器。此外，它是一个可执行文件，你的测试所需的系统路径。

### GeckoDriver 是如何工作的？



WebDriver使用GeckoDriver与firefox浏览器连接。就像其他驱动程序(例如[ChromeDriver](https://chromedriver.chromium.org/))一样，本地服务器由这个可执行文件启动，它运行你的 selenium 测试。它作为本地和远程端之间的代理，将调用转换为[Marionette 自动化协议。](https://firefox-source-docs.mozilla.org/testing/marionette/Intro.html)此外，要了解有关其交互方式的更多信息，请参阅下图：

![通过 Marionette 协议进行客户端服务器交互](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Client%20Server%20Interaction%20through%20Marionette%20Protocol.png)

客户端或本地系统发送请求，即WebDriver调用GeckoDriver。 GeckoDriver将这些请求转换为Marionette协议并传输给Marionette Driver。现在，服务器通过GeckoDriver 将响应发送回客户端。因此，执行发生在浏览器内部。现在我们知道了firefox驱动程序的工作原理，让我们理解为什么我们需要它来执行我们的 Selenium 测试。

### 为什么要使用 Gecko 驱动程序？

对于早期版本的 Selenium (如 2.53 及以下版本)，启动 Firefox 浏览器非常简单。此外，这可以通过使用下面的代码行直接完成，你可以使用[WebDriver](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)引用实例化 Firefox 驱动程序，如下所示：

```java
WebDriver driver = new FirefoxDriver();
```

要更深入地了解它，请参考下图：

![GeckoDriver 之前的 FirefoxDriver Selenium 实现](https://www.toolsqa.com/gallery/selnium%20webdriver/2.FirefoxDriver%20Selenium%20Implementation%20before%20GeckoDriver.png)

在Marionette 成为 Firefox 的一部分之前， WebDriver的默认浏览器是 Firefox，它有内置的驱动程序来启动 Firefox 浏览器。此外，WebDriver直接实现了FirefoxDriver，因此无需使用任何可执行文件来启动 Firefox。只需一行简单的代码，如上所示，即可打开 Firefox 浏览器。但是在Firefox v47.0+之后，我们需要使用代理来与浏览器进行交互。

此外，对于 Firefox 47.0+，无法让任何第三方驱动程序直接与浏览器交互。因此，在最新的 Firefox 版本中使用 Selenium 2 是一项挑战。因此，我们必须使用具有MarionetteDriver 的 Selenium 3。 Marionette 驱动程序是 Mozilla 的自动化驱动程序，它可以直接控制 Firefox 等 Gecko 平台的 UI 或内部 JavaScript。因此，我们需要GeckoDriver来实例化一个对象并启动 Firefox。让我们看看如果我们不使用带有 Selenium 3 或最新版本的GeckoDriver会发生什么，通过下面的例子-

```java
package gecko;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class GeckoDriver {

	public static void main(String[] args) {

		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com/");
		
	}

}
```

运行上面的代码，你会得到IllegalStateException，如下所示：

![不使用 Gecko Driver 的执行结果](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Execution%20results%20without%20using%20Gecko%20Driver.png)

注意：如果你已经在你的系统中设置了 GeckoDriver，你将不会得到这样的任何错误。

我们可以使用GeckoDriver代替常规的FirefoxDriver来解决上述异常。在下一节中，我们将看到如何在我们的系统中设置GeckoDriver。

## 如何在 Windows 上安装 GeckoDriver？

在本节中，我们将了解如何在Windows 操作系统上下载、设置和使用GeckoDriver 。有多种方法可以为你的 selenium 脚本设置它，我们将对此进行详细讨论。因此，让我们首先从下载适用于Windows平台的驱动程序可执行文件开始：

### 如何在 Windows 上下载 GeckoDriver？

1.  首先，你可以直接从[Github](https://github.com/mozilla/geckodriver/releases)下载特定于平台的GeckoDriver(最好是最新版本)。因为我们是为 Windows 64 位平台下载它，所以我们将下载文件“geckodriver-<latest-version>-win64.zip”，如下面的截图所示：

![从 Github 下载 GeckoDriver](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Downloading%20GeckoDriver%20from%20Github.png)

1.  其次，解压缩下载的gecko 驱动程序 zip 文件。

![从下载的 ZIP 中提取 GeckoDriver](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Extract%20GeckoDriver%20from%20downloaded%20ZIP.png)

1.  第三，请选择一个目的地来保存它。

![将GeckoDriver保存到指定目录](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Save%20GeckoDriver%20to%20the%20specified%20directory.png)

你现在已准备好在测试脚本中使用GeckoDriver。下一步，我们需要在我们的系统上设置驱动程序。

### 如何在 Windows 上安装 GeckoDriver？

与早期的 Firefox 驱动程序实现不同，GeckoDriver不能直接实例化。我们需要在显式创建WebDriver实例之前对其进行初始化。FirefoxDriver应该可以访问GeckoDriver可执行文件的路径，这样当用户使用FirefoxDriver 创建WebDriver的实例时，它应该能够找到GeckoDriver可执行文件的路径。这将导致成功的初始化。我们可以按照以下任何一种方法来设置GeckoDriver：

1.  使用环境变量中的系统属性设置 GeckoDriver。
2.  使用测试脚本中的系统属性设置 GeckoDriver。
3.  同样，通过初始化浏览器的所需功能来设置 GeckoDriver。

让我们了解所有这些并尝试使用[Selenium 3](https://seleniumhq.wordpress.com/2016/10/13/selenium-3-0-out-now/)或Selenium 4 运行我们的测试代码。

#### 如何使用环境变量中的系统属性设置 GeckoDriver？

在 Windows 上，环境变量是声明任何全局系统级变量的最简单方法之一，系统上运行的所有程序都可以访问这些变量。使用相同的方法，我们可以使用环境变量来设置GeckoDriver 的路径。因此，当我们创建WebDriver 的实例时，它会自动在系统的PATH变量中找到GeckoDriver的路径并执行相同的。我们可以按照下面提到的步骤将GeckoDriver的路径添加 到系统的 PATH 变量中：

1.  首先，右键单击此PC打开属性。

![下载文件夹中的 geckodriver tar 文件](https://www.toolsqa.com/gallery/selnium%20webdriver/7.geckodriver%20tar%20file%20in%20Downloads%20folder.png)

1.  其次，打开高级系统设置并单击环境变量。

![打开系统环境变量](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Opening%20System%20Environment%20Variables.png)

1.  第三，在系统变量下，选择 路径并单击编辑。

![编辑系统路径](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Editing%20the%20System%20Path.png)

1.  之后，你需要附加GeckoDriver 的路径。单击新建并将路径粘贴到最后一个可编辑行，然后单击 确定。此外，我们需要指定GeckoDriver可执行文件所在的文件夹路径。在我们的例子中，它是“ E:\drivers ”

![将驱动程序路径添加到系统变量](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Adding%20Driver%20Path%20to%20System%20Variables.png)

关闭所有后续窗口后，你可以在不使用系统属性代码的情况下使用GeckoDriver 。请注意，你可能必须重新启动系统才能使环境变量更改生效。你现在可以更新测试代码以直接实例化WebDriver  ，如下所示：

```java
package gecko;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class GeckoDriver {

	public static void main(String[] args) throws InterruptedException{
		
		System.out.println("Execution after setting driver path in system variables");
		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com");
		Thread.sleep(3000);
		driver.quit();
		System.out.println("Execution complete");

	}
}
```

执行上面的代码，你会看到如下结果——

![在系统变量中设置geckodriver路径后的执行结果](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Execution%20Results%20after%20setting%20geckodriver%20path%20in%20System%20Variables.png)

从控制台结果中可以清楚地看出，没有WebDriver错误，这意味着WebDriver设置是正确的。你可以将打印语句视为我们执行的入口点和出口点。相应地，你将能够在你的系统中看到执行情况。

#### 如何使用 Selenium 测试脚本中的系统属性初始化 Gecko Driver？

我们需要添加一行代码来设置 GeckoDriver 的系统属性-

```java
System.setProperty("webdriver.gecko.driver","<Path to geckodriver.exe>)");
```

让我们修改上面使用的代码，看看我们是否可以成功启动 Firefox 浏览器。修改后的代码如下所示：

```java
package gecko;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class GeckoDriver {

	public static void main(String[] args) throws InterruptedException {

                System.out.println("Execution started-- Opening Firefox browser.");
		System.setProperty("webdriver.gecko.driver", "E:\\drivers\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();
		driver.get("https://demoqa.com/");
		Thread.sleep(3000);
		driver.quit();
		System.out.println("Execution ending-- Webdriver session is closed.");

	}
}
```

你将看到[demoqa.com](https://demoqa.com/) 在 Firefox 浏览器中打开，没有任何错误和异常。

![system.properties 的执行结果](https://www.toolsqa.com/gallery/selnium%20webdriver/12.Execution%20Results%20with%20system.properties.png)

执行日志表明我们的WebDriver会话开始时打印语句就在开头显示。红色的线是浏览器会话对应的一些浏览器日志。你可以看到浏览器在你的系统中打开，网站打开后，浏览器会话关闭。

#### 如何通过为浏览器设置所需功能来设置 Selenium GeckoDriver？

[Desired Capabilities](https://stackoverflow.com/questions/17527951/what-is-the-use-of-desiredcapabilities-in-selenium-webdriver)帮助 Selenium 了解浏览器的详细信息，例如其名称、版本和操作系统。我们可以使用Desired Capabilities并将Marionette为 true 以启动 Firefox。你需要将以下行添加到你的代码中：

```java
DesiredCapabilities cap = DesiredCapabilities.firefox();
cap.setCapability("marionette", true);
WebDriver driver = new FirefoxDriver(cap);
```

完整的代码如下所示：

```java
package gecko;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class GeckoDriver {

	public static void main(String[] args) throws InterruptedException {

		System.out.println("Execution with desired capabilities");
                DesiredCapabilities cap = DesiredCapabilities.firefox();
		cap.setCapability("marionette", true);
		WebDriver driver = new FirefoxDriver(cap);
		driver.get("https://demoqa.com");
		Thread.sleep(3000);
		driver.quit();
                System.out.println("Execution completed");
		
	}

}
```

执行上面的代码，你会看到如下结果——

![使用所需 Capabilities 的执行结果](https://www.toolsqa.com/gallery/selnium%20webdriver/13.Execution%20results%20using%20desired%20Capabilities.png)

就像上面的执行一样，使用所需的功能，你将能够看到无一例外地成功执行。可以看到我们的测试通过了，完整的代码运行没有任何停顿。

## 如何在 macOS 上安装 GeckoDriver？

GeckoDriver在macOS上的安装和设置几乎与 Windows 平台相同，唯一的区别是macOS的可执行文件会有所不同，我们在系统的PATH变量中包含GeckoDriver可执行文件的方式也略有不同。让我们看看如何在macOS 上安装和设置GeckoDriver ：

### 如何在 macOS 中下载 GeckoDriver？

与 Windows 类似，你可以导航到[GitHub](https://github.com/mozilla/geckodriver/releases) 并下载 macos.tar.gz 文件，如下所示：

![从 github 下载 GeckoDriver for mac](https://www.toolsqa.com/gallery/selnium%20webdriver/14.Downloading%20GeckoDriver%20for%20mac%20from%20github.png)

默认情况下，tar 文件将下载到Downloads 文件夹下， 如下所示：

![下载文件夹中的 geckodriver tar 文件](https://www.toolsqa.com/gallery/selnium%20webdriver/15.geckodriver%20tar%20file%20in%20Downloads%20folder.png)

注意：你可以将文件下载到你选择的任何文件夹中，具体取决于你的系统设置。

接下来，你需要从我们在上一步中下载的 tar 文件中提取驱动程序。为此，双击macos.tar.gz 文件，你会注意到一个 名为“ geckodriver ”的Unix 可执行文件被提取到相同的位置，如下所示：

![从 tar 文件中提取可执行的 geckodriver](https://www.toolsqa.com/gallery/selnium%20webdriver/16.Extracting%20the%20executable%20geckodriver%20from%20the%20tar%20file.png)

所以，现在我们在系统上确实有了GeckoDriver可执行文件，让我们看看如何在我们的测试脚本中设置和使用它。

### 如何在 macOS 上设置 Selenium GeckoDriver？

现在你已经下载了Selenium GeckoDriver，下一步是设置它，以便你可以在测试脚本中使用它。同样在macOS 上 ，我们可以按照与在 Windows 上相同的方式来设置GeckoDriver：

1.  使用系统的 PATH 变量设置 GeckoDriver。
2.  使用测试脚本中的系统属性设置 GeckoDriver。
3.  同样，通过初始化浏览器的所需功能来设置 GeckoDriver。

最后2点，直接嵌入到JAVA代码中，在所有平台上都是一样的，所以我们可以按照上面提到的Windows平台相同的步骤进行操作。对于第一点，因为它取决于操作系统，全局变量如何设置，我们如何暴露给平台上的所有应用程序。尽管有很多方法可以在系统的 PATH变量中设置GeckoDriver 的可执行文件路径，但最简单的方法之一是将可执行文件复制到任何文件夹下，这些文件夹已经在macOS的“ PATH ”变量下。让我们看看如何实现相同的目标：

#### 如何在 macOS 中使用系统的 PATH 变量设置 Selenium GeckoDriver？

正如我们上面提到的，使可执行文件在macOS上全局可用的最简单方法之一是将可执行文件复制到PATH变量中已有的任何文件夹下。让我们按照下面提到的步骤来实现相同的目的：

1.  在终端上使用命令“ echo $PATH ”识别包含在 PATH 变量中的文件夹。它将给出示例输出，如下所示：

![验证 macOS PATh 变量中的文件夹](https://www.toolsqa.com/gallery/selnium%20webdriver/17.Validate%20folders%20in%20macOS%20PATh%20variable.png)

1.  正如我们所见，多个目录已经是 PATH 变量的一部分。假设我们选择“ /usr/local/bin ”作为输出目录来保存GeckoDriver可执行文件。
2.  接下来，你需要将驱动程序保存在以下位置'/usr/local/bin'。只需复制GeckoDriver可执行文件并导航到“前往”>“前往文件夹”

![导航到 bin 文件夹](https://www.toolsqa.com/gallery/selnium%20webdriver/18.Navigation%20to%20bin%20folder.png)

1.  输入目标目录路径并单击 Go。

![导航到 geckodriver 的 usr/local/bin](https://www.toolsqa.com/gallery/selnium%20webdriver/19.Navigating%20to%20usr%20local%20bin%20for%20geckodriver.png)

1.  将 Unix 可执行文件粘贴到 bin 文件夹中。

![将 GeckoDriver 保存到 bin 目录](https://www.toolsqa.com/gallery/selnium%20webdriver/20.Saving%20GeckoDriver%20to%20bin%20directory.png)

现在你的GeckoDriver已准备好在你的Selenium 测试脚本中使用。现在我们将编写一个简单的程序并在mac系统中执行。

```java
package DemoProject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class GeckoDriver {

	public static void main(String[] args) throws InterruptedException {
		
		WebDriver driver = new FirefoxDriver();
		System.out.println("GeckoDriver execution on mac!!");
		driver.get("https://demoqa.com");
		Thread.sleep(3000);
		driver.quit();
		System.out.println("Execution completed on mac!!");

	}

}
```

在执行相同的操作时，你可以在控制台窗口中找到结果 -

![GeckoDriver在mac上的执行结果](https://www.toolsqa.com/gallery/selnium%20webdriver/21.Execution%20results%20on%20mac%20for%20GeckoDriver.png)

你可以看到执行成功，没有任何错误。两个打印语句都得到显示，这表明我们的执行没有遇到任何错误。那么你看到在macOS中运行GeckoDriver测试是多么容易了吗？不像windows系统，你必须记住你的驱动程序可执行文件的路径，在mac中只要把驱动程序放在一个位置就可以让我们的生活变得如此简单！

## 如何使用 GeckoDriver 在 Headless 模式下运行测试？

你可以在headless 模式下运行你的测试，即没有UI显示，只有后台执行。只需使用Firefox 选项即可使用GeckoDriver中的无头模式。 你需要做的就是使用setHeadless() 方法，它是Options类的一部分。下面是我们将添加到现有测试中的Firefox 选项代码。

```java
FirefoxOptions options = new FirefoxOptions();  
options.setHeadless(true); 
WebDriver driver = new FirefoxDriver(options);
```

在无头模式下运行测试的完整代码如下所示：

```java
package gecko;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class GeckoDriver {

	public static void main(String[] args) throws InterruptedException {

		System.out.println("Execution in Headless Mode");
                FirefoxOptions options = new FirefoxOptions();  
	        options.setHeadless(true); 
		WebDriver driver = new FirefoxDriver(options);
		driver.get("https://demoqa.com");
		Thread.sleep(3000);
		System.out.println("The page title is " +driver.getTitle());
		driver.quit();
		System.out.println("Execution completed");
	}

}
```

在运行这段代码时，你会看到浏览器窗口没有显示出来，而在控制台中，你可以看到打印语句。

![GeckoDriver中Headless Driver的执行结果](https://www.toolsqa.com/gallery/selnium%20webdriver/22.Execution%20Results%20of%20Headless%20Driver%20in%20GeckoDriver.png)

看看使用这个无头选项来加快测试执行是多么容易！现在让我们看看在使用GeckoDriver时可能遇到的常见异常。

## 使用 GeckoDriver 时常见的异常是什么？

在使用GeckoDriver时，你可能会遇到一些问题。下面列出了其中很少的解决方案-

-   未连接异常

```java
org.openqa.selenium.firefox.NotConnectedException: Unable to connect to host 127.0.0.1 on port 7055 after 45000 ms.
```

当你使用最新的 Firefox 版本但使用旧的 Selenium 版本时，会发生此异常。要解决，请将 selenium jar 更新到最新版本。



-   WebDriver异常

```java
Exception in thread “main” org.openqa.selenium.WebDriverException: Failed to decode response from marionette
```

当GeckoDriver版本或 Selenium 版本或 Firefox 版本之间存在不匹配时，会发生此异常。要修复，更新最新的Gecko 驱动程序版本并确保 Firefox 更新到最新版本。

-   连接拒绝异常

```java
org.openqa.selenium.WebDriverException: Connection refused (Connection refused)
```

当 WebDriver 无法与 Firefox 建立连接时，就会出现这种异常。要解决，你可以清除浏览器缓存。

-   无法访问的浏览器异常

```java
Exception in thread “main” org.openqa.selenium.remote.UnreachableBrowserException: Error communicating with the remote browser. It may have died.
```

它通常发生在WebDriver试图访问某些元素时，但会话已关闭或浏览器未启动。确保你的quit()或close()方法正在终止任务管理器中的浏览器实例。

## 要点：

-   使用 GeckoDriver，你将能够执行你的 selenium 脚本，并能够使用上面列出的多种方式中的任何一种启动 Firefox 浏览器。
-   此外，你可以在无头模式下自由执行脚本并优化执行速度。
-   此外，你可以在遇到各种异常时知道出路，从而有效地构建测试！