[Selenium WebDriver](https://www.selenium.dev/documentation/en/webdriver/)是一种广受欢迎的自动化工具，可用于针对多种浏览器(如Google Chrome 浏览器、Firefox 浏览器、Internet Explorer等)运行测试。这种在不同浏览器上进行的测试通常称为[跨浏览器测试](https://www.selenium.dev/documentation/en/webdriver/)。因此，如果我们要启动这些浏览器的驱动程序中的任何一个进行测试，我们必须显式设置相应的可执行路径。之后，我们实例化适当的驱动程序实例并继续执行我们要执行的代码。这些步骤变得很麻烦，因为每次版本更改时我们都需要执行它们。因此我们在 Selenium 中使用“ WebDriverManager ”类。

使用WebDriverManager，我们可以下载用于自动化测试的驱动程序的二进制文件(或 .exe 文件)。在本文中，我们将通过涵盖以下主题的详细信息来讨论WebDriverManager在自动化中的重要性以及如何在Selenium中使用它进行自动化测试：

-   在 Selenium 中实例化浏览器的传统方式是什么？
-   为什么需要 WebDriverManager？
    -   如何在 Selenium 中使用 WebDriverManager 实例化浏览器？
    -   如何将 WebDriverManager 添加到 Selenium 项目中？
-   Selenium 中的 WebDriverManager 有哪些不同的功能？
    -   如何实例化特定的浏览器版本？
    -   如何使用实例化平台版本(x32 或 x64)？
    -   以及如何设置代理用户名和密码？

## 在 Selenium 中实例化浏览器的传统方式是什么？

我们知道，要在 chrome 或firefox等浏览器上执行Selenium自动化脚本，我们必须下载chromedriver和[geckodriver](https://www.toolsqa.com/selenium-webdriver/selenium-geckodriver/)等这些驱动程序的二进制文件。之后，我们需要在自动化脚本中设置这些二进制文件的路径或添加类路径位置。

所以如果你想在Chrome浏览器上执行Selenium WebDriver自动化脚本，那么你需要先下载chromedriver.exe然后使用System.setProperty 方法设置它的路径如下：

```java
System.setProperty("webdriver.chrome.driver", "/absolute/path/to/binary/chromedriver");
```

同样，对于 Firefox 浏览器，我们将使用geckodriver.exe重复上述命令。如下：

```java
System.setProperty("webdriver.gecko.driver", "/absolute/path/to/binary/geckodriver");
```

如果我们未能定义此路径或提供了错误的路径，则脚本会引发错误，如下所示。

![Selenium 中的系统属性设置](https://www.toolsqa.com/gallery/selnium%20webdriver/1.system%20property%20setup%20in%20Selenium.png)

当我们没有设置驱动程序的路径时，上面的控制台窗口显示错误。

对于我们用来执行自动化脚本的每个驱动程序实例，都会重复上述下载过程和设置文件路径。如果我们想在不同的操作系统(比如 Windows、macOS 和 Linux)上运行脚本，那么下载驱动程序二进制文件和设置路径的整个过程就会变得更加单调。此外，艰苦的工作并不止于此。当发布新的二进制文件或新的浏览器版本时，我们将不得不再次检查每个可执行文件的兼容性，如果存在兼容性问题，则重复该过程。

简而言之，这种手动下载可执行文件、在脚本中设置其路径、然后执行脚本的过程既耗时又低效。在最新版本中，Selenium为我们提供了一个“ WebDriverManager ”类，它可以自动执行此过程，让我们专注于 Selenium 脚本而不是浏览器设置。

接下来，让我们熟悉一下WebDriverManager类。

## 为什么需要 WebDriverManager？

如上所述，Selenium中的WebDriverManager是一个允许我们下载和设置浏览器驱动程序二进制文件的类，作为开发人员，我们不必手动将它们放入自动化脚本中。

因此，Selenium 中的WebDriverManager类：

-   自动管理 WebDriver 二进制文件。
-   下载适当的驱动程序二进制文件(如果不存在)到本地缓存中。
-   下载最新版本的浏览器二进制文件，除非另有说明。
-   无需在本地存储驱动程序二进制文件。我们也不需要为不同的浏览器维护各种版本的二进制驱动程序文件。

### 如何在 Selenium 中使用 WebDriverManager 实例化浏览器？

但是我们如何使用这个类来设置浏览器呢？为此，我们使用WebDriverManager类代码。它如下所示，代替了“ System.setProperty() ”调用。

```java
WebDriverManager.chromedriver().setup();
```

它将在自动化脚本中设置Chrome 浏览器。

下一步是设置浏览器二进制文件。WebDriverManager有一种自动下载浏览器可执行文件 ( exes ) 或二进制文件的方法。它支持不同的浏览器，如Chrome、Firefox、Microsoft Edge、Internet Explorer、Opera 或 PhantomJS。

考虑以下代码行：

```java
WebDriverManager.chromedriver().setup();
driver = new ChromeDriver();
```

第一行执行以下功能：

-   WebDriverManager.chromedriver() .setup：检查指定 WebDriver 二进制文件的最新版本。如果机器上没有二进制文件，那么它将下载 WebDriver 二进制文件。接下来，它使用 ChromeDriver 实例化 Selenium WebDriver 实例。

在Selenium测试用例中使用WebDriverManager ，它需要包含在Selenium测试项目中。让我们看看如何在 Selenium 项目中下载和使用WebDriverManager ：

### 如何将 WebDriverManager 添加到 Selenium 项目中？

要将WebDriverManager添加到 Selenium 项目中，我们必须按照以下步骤操作：

1.  [从此处](https://jar-download.com/artifacts/io.github.bonigarcia/webdrivermanager)下载最新版本的 WebDriverManager，如下所示(或根据项目要求的任何版本)。

![下载 WebDriverManager jar 文件](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Download%20WebDriverManager%20jar%20file.png)

1.  它将下载一个 zip 文件。现在提取 jar/zip 文件。会在文件夹下显示各种jar，如下图：

![WebDriverManager 中的各种 JAR](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Various%20JARs%20in%20WebDriverManager.png)

1.  一旦我们提取了 zip 文件，我们就必须在我们的项目中引用这些 jar 文件。为此，导航到项目属性并单击Build Path-> Configure Build Path in Eclipse

![在 Eclipse 中配置构建路径](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Configure%20Build%20Path%20in%20Eclipse.png)

1.  按照下面突出显示的步骤单击“添加外部 Jar ”以包含所有提取的WebDriverManager jar。

![在 Eclipse 中添加外部库的步骤](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Steps%20to%20add%20external%20libraries%20in%20Eclipse.png)

1.  点击“ Add External JARs ”后，选中所有提取出来的JAR，如下图：

![选择 WebDriverManager 提取的 JAR](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Select%20WebDriverManager%20extracted%20JARs.png)

1.  完成后，项目引用会在项目资源管理器中显示这些引用的 jar，如下面突出显示的那样，它们已准备好在 Selenium 测试脚本中使用。

![添加到 Selenium 项目的 WebDriverManager 引用](https://www.toolsqa.com/gallery/selnium%20webdriver/7.WebDriverManager%20references%20added%20to%20Selenium%20project.png)

一旦这些 jar 出现在我们的项目引用中，如上所示，我们就可以在我们的程序中使用WebDriverManager了。

下面的代码片段显示了WebDriverManager的快速用法：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverManagerDemo {

	public static void main(String[] args) {
		ChromeOptions chromeOptions = new ChromeOptions();
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver(chromeOptions);
		
		// Navigate to the demoqa website
		driver.get("https://www.demoqa.com");
		
		driver.quit();

	}

}
```

-   在上面的程序中，我们使用“导入”语句将 WebDriverManager 类包含在我们的程序中。
-   然后我们在程序中使用 WebDriveManager.chromedriver.setup() 创建一个 WebDriverManager 实例
-   然后我们创建一个 Google Chrome 的驱动程序实例并打开一个站点“ www.demoqa.com ”。
-   所以当我们执行上面的程序时，预期的输出是 URL“ [https://www.demoqa.com](https://www.demoqa.com/) ” 将在新的 Chrome 浏览器实例中打开。

注意：我们还可以将 WebDriverManager 作为依赖项包含在 Maven 或 Gradle 项目中。然后这些 IDE 会下载最新版本的 WebDriverManager(如果缓存中不存在的话)。

下图显示了所有支持的浏览器，这些浏览器可以使用Selenium 中的WebDriverManager类进行管理和自动化：

![WebdriverManager 中支持的浏览器](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Supported%20Browsers%20in%20WebdriverManager.png)

从上面的截图可以看出，我们可以使用WebDriverManager类来设置以下任何一种浏览器。

```java
WebDriverManager.chromedriver().setup();

WebDriverManager.firefoxdriver().setup();

WebDriverManager.iedriver().setup();

WebDriverManager.edgedriver().setup();

WebDriverManager.operadriver().setup();

WebDriverManager.phantomjs().setup();
```

## Selenium 中的 WebDriverManager 有哪些不同的功能？

除了为自动化脚本设置合适的浏览器之外，WebDriverManager还拥有各种功能，如下所列。

### 如何实例化特定的浏览器版本？

如果我们希望使用特定版本的浏览器而不是最新版本，我们可以使用WebDriverManager来实现。

例如，[最新的 chromedriver 版本](https://chromedriver.chromium.org/downloads)是 87.0(发布于 15/10/2020)。但是如果我们想要更早的版本，比如Chromedriver 85.0 版，我们必须添加以下代码。

```java
WebDriverManager.chromedriver().driverVersion("85.0.4183.38").setup();
```

由于上面这行代码，我们将使用ChromeDriver版本 85.0.0 而不是最新的 87.0.0。

将chromedriver 版本设置为“ 85.0.4183.38 ”的示例代码如下：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

public class WebDriverManagerDemo {

	public static void main(String[] args) {
		ChromeOptions chromeOptions = new ChromeOptions();
		WebDriverManager.chromedriver().driverVersion("85.0.4183.38").setup();
		WebDriver driver = new ChromeDriver(chromeOptions);
		
		// Navigate to the demoqa website
		driver.get("https://www.demoqa.com");
		
		driver.quit();

	}

}
```

该程序提供以下输出。

![使用 WebdriverManager 设置 Chromedriver 版本](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Set%20Chromedriver%20version%20using%20WebdriverManager.png)

从上面的截图我们可以看到，由于执行上面的程序，Chromedriver启动成功。我们可以在第一行输出中看到启动 chrome 驱动程序实例的详细信息。这里我们将 Chrome 版本设置为“ 85.0.4183.87 ”。

与WebDriver版本类似，你也可以指定浏览器版本，WebDriverManager会自动选择对应的 WebDriver。你可以使用以下命令实现相同的目的：

```java
WebDriverManager.chromedriver().browserVersion("83.0.4103").setup();
```

因此，在这里你指定的不是“ driverVersion ”，而是“ browserVersion ”。

### 如何使用实例化平台版本(x32 或 x64)？

WebDriverManager为我们将用来执行自动化脚本的机器选择合适的二进制文件。但是如果我们希望使用不同的二进制文件，那么我们可以使用允许我们设置架构版本的架构方法。

体系结构版本接受以下参数之一：

-   github.bonigarcia.wdm.Architecture.X64
-   github.bonigarcia.wdm.Architecture.X32

例如，要设置 X32 版本，代码将是：

```java
WebDriverManager.chromedriver().architecture(io.github.bonigarcia.wdm.Architecture.X32).setup();
```

或者，我们也可以使用 arch32() 或 arch64() 方法来指定二进制类型。

-   chromedriver().arch32().setup();
-   chromedriver().arch64().setup();

### 如何设置代理用户名和密码？

如果你的组织有代理服务器，你需要向WebDriverManager指定代理服务器详细信息，例如服务器名称或 IP 地址、用户名和密码。否则，它可能会引发/导致错误，例如io.github.bonigarcia.wdm.WebDriverManagerException: java.net.UnknownHostException: chromedriver.storage.googleapis.com。

WebDriverManager 提供了适当的方法来设置代理详细信息，如下所示：

-   代理(“主机名：端口号”)
-   代理用户(“用户名”)
-   proxyPass(“密码”)

讨论完所有这些功能后，我们可以一起使用所有这些功能，如以下代码所示。

```java
WebDriverManager.chromedriver()
                 .version("83.0.0")
                 .arch32()
                 .proxy("proxyhostname:80")
                 .proxyUser("username")
                 .proxyPass("password")
.setup();
```

WebDriverManager更多的配置和特性，使得浏览器和驱动的管理变得非常容易。那么，你为什么要等？刚刚开始在所有 Selenium 测试脚本中使用WebDriverManager的旅程？

## 关键要点

-   WebDriverManager 在 Selenium 代码中自动设置浏览器。
-   默认情况下，它会下载最新版本的浏览器二进制文件以及适用于相应平台的二进制文件。
-   如果代码属于 Maven 或 Gradle 环境，我们必须添加 WebDriverManager 的依赖项。
-   它支持各种浏览器，如 Google Chrome、Firefox、IE、Edge、Opera 等。
-   除了自动设置浏览器二进制文件之外，WebDriverManager 还允许我们设置参数值，例如浏览器驱动程序版本(以防我们需要特定版本)、平台版本、代理详细信息等。