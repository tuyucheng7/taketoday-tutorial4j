Web 浏览器是使用SeleniumWebDriver的 UI 自动化的一个组成部分。启动浏览器然后在浏览器上执行测试用例是 Web 自动化测试的重要组成部分。但是当我们在任何浏览器上运行Selenium测试时，我们通常会面临一些挑战，例如浏览器上的渲染速度慢，系统上运行的其他应用程序的干扰等。除此之外，目前[CI](https://codeship.com/continuous-integration-essentials#:~:text=Continuous Integration (CI) is a,CI it is typically implied.)系统的主要是非 UI(例如基于 Unix 的系统)。因此，为了在这些系统上运行测试用例，我们需要一种在非 UI 模式下运行测试用例的方法，这就是无头浏览器出现的地方，它有助于执行Selenium 无头浏览器测试在一个非 UI模式。

几乎所有现代浏览器都提供以无头模式运行它们的能力。在本文中，我们将讨论这些无头浏览器以及如何使用它们在无头模式下运行Selenium测试用例。

-   什么是无头浏览器？
    -   为什么要使用无头浏览器进行测试执行？
    -   无头测试的局限性。
-   如何在无头模式下运行 Selenium 测试？
    -   使用 HTMLUnitDriver 运行 Selenium 无头浏览器测试。
    -   使用无头 Chrome 浏览器运行 Selenium 无头浏览器测试。
    -   并使用无头 Firefox 浏览器运行 Selenium 无头浏览器测试。
    -   使用无头 Edge 浏览器运行 Selenium 无头浏览器测试用例。

## 什么是无头浏览器？

无头浏览器是一个术语，用于定义没有GUI 的浏览器模拟程序。这些程序像任何其他浏览器一样执行，但不显示任何UI。在无头浏览器中，当 Selenium 测试运行时，它们在后台执行。几乎所有现代浏览器都提供以无头模式运行它们的功能。

那么，是否可以使用[Selenium](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)进行无头测试？是的，Selenium支持无头测试。在旧版本的 Selenium 中，我们主要使用HTMLUnitDriver ，这是一个无头驱动程序，提供了Selenium WebDriver的非 GUI 实现。但是通过最新版本的SeleniumWebDriver 3和SeleniumWebDdriver 4， Selenium还支持真实浏览器的无头版本，如Chrome、Firefox 和 Edge。Selenium 提供了各种配置，我们可以使用这些配置以无头模式运行这些浏览器。在了解这些配置的细节之前，让我们先了解一下在 headless 模式下运行测试有什么好处？

### 为什么要使用无头浏览器进行测试执行？

在无头模式下运行 Selenium 测试用例具有以下好处：

-   在 CI 管道中有用：当我们需要在服务器上或在持续集成服务器(如[Jenkins](https://www.jenkins.io/) )的任何构建和发布管道中远程执行自动化测试用例时，并不总是可以在此类远程机器上安装真正的浏览器。我们可以使用无头浏览器来高效地运行自动化测试。
-   有利于网络抓取：当你想写一个网络抓取器或数据提取器需要访问一些网站并收集数据时，无头浏览器是一个完美的选择。因为在这些情况下我们不关心功能，所以我们访问网页并获取数据。
-   支持多个浏览器版本：有时，测试人员希望在同一台机器上模拟多个浏览器版本。在那种情况下，你会想要使用无头浏览器，因为它们中的大多数都支持模拟不同版本的浏览器。
-   更快的自动化测试执行：与真正的浏览器自动化相比，无头浏览器的性能更好。真正的浏览器，如 Google Chrome、Firefox 和 Internet Explorer 需要花费大量时间来加载CSS、JavaScript、图像以及打开和呈现HTML。无头浏览器不需要加载所有这些，并且会在不等待页面完全加载的情况下开始执行功能。当我们需要在无头浏览器中运行回归脚本时，我们可以节省时间，因为速度更快并且可以快速呈现结果。
-   多任务：无头浏览器可以帮助你，多任务。当测试在后台运行时，你可以使用你的浏览器或你的机器做任何其他事情。节省我们盯着屏幕看的时间。

尽管运行 Selenium 测试提供了这些好处，但也存在某些限制。其中一些如下所列：

### 无头浏览器测试的局限性

-   调试将不可行，因为检查浏览器上正在运行什么的唯一方法是抓取屏幕截图并验证输出。
-   无头浏览器不会模仿确切的用户行为，因为页面不会精确地呈现它将在实际浏览器中呈现的所有依赖项。
-   在无头模式下运行测试时，可能会遗漏 Web 元素的位置、Web 元素的颜色等外观错误。

现在让我们看看如何在无头模式下运行Selenium测试用例：

## 如何在无头模式下运行 Selenium 测试用例？

正如我们上面所讨论的，Selenium WebDriver提供了各种驱动程序和配置，有助于在无头模式下运行Selenium测试用例。让我们在以下部分中了解其中的一些：

-   使用 HTMLUnitDriver 运行 Selenium 测试用例。
-   使用无头 Chrome 浏览器运行 Selenium 测试用例。
-   并且，使用无头 Firefox 浏览器运行 Selenium 测试用例。
-   使用无头 Edge 浏览器运行 Selenium 测试用例。

### 使用 HTMLUnitDriver 运行 Selenium 无头浏览器测试。

HtmlUnitDriver是基于HtmlUnit的 Selenium WebDriver的实现，HtmlUnit 是一种基于Java 的 Web 浏览器实现，没有GUI。HtmlUnitDriver是目前最快、最轻量级的WebDriver 实现。HtmlUnitDriver是众所周知的无头浏览器驱动程序，具有许多优点 -

-   与其他浏览器相比，WebDriver 的最快实现。
-   HtmlUnitDriver 是独立于平台的。
-   HtmlUnitDriver 支持 JavaScript。
-   此外，HtmlUnitDriver 允许你选择其他浏览器版本来运行你的脚本。你可以在 HtmlUnitDriver 本身中提及 Chrome 或 Firefox 的不同浏览器版本。

#### 如何使用 HtmlUnitDriver 作为 Selenium 的无头浏览器？

为了实现无头测试，Selenium 使用了HtmlUnitDriver，它是WebDriver 的另一种实现，类似于FirefoxDriver、ChromeDriver等。HTMLUnitDriver可作为外部依赖项使用，需要你显式添加库。

注意：在Selenium的早期版本(2.53之前)，HtmlUnitDriver在selenium库中就可以使用，不需要外部下载jar。虽然此驱动程序仍受支持，但它现在是一个单独的依赖项并使用 Html Unit 框架。这些依赖需要像其他jar添加一样添加到项目中。

要下载HtmlUnitDriver依赖项，请按照以下步骤操作：

1.  导航到[https://github.com/SeleniumHQ/htmlunit-driver/releases](https://github.com/SeleniumHQ/htmlunit-driver/releases)。
2.  单击最新版本的 HTML 单元驱动程序，如下图所示：

![htmlUnitDriver 下载](https://www.toolsqa.com/gallery/selnium%20webdriver/1.htmlUnitDriver%20Download.png)

下载 jar 文件后，按照以下步骤将 jar 添加到[Eclipse](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)项目中，

1.  右键单击你的项目。
2.  选择构建路径 并单击配置构建路径

![在 Eclipse 中构建路径和配置构建路径](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Build%20Path%20and%20Configure%20Build%20Path%20in%20Eclipse.png)

1.  单击Libraries选项卡并选择Add External JARs。

![添加外部罐子](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Add%20External%20Jarg.png)

1.  从文件夹中选择下载的 jar 文件。
2.  单击并应用和确定。罐子将添加如下 -

![已添加 HtmlunitDriverJar](https://www.toolsqa.com/gallery/selnium%20webdriver/4.HtmlunitDriverJarAdded.png)

将 jar 添加到 Eclipse 项目后，你可以通过添加以下行将类“ org.openqa.selenium.htmlunit.HtmlUnitDriver ”导入到测试代码中 -

```java
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
```

之后，你可以创建一个HtmlUnitWebDriver实例，如下所示 -

```java
HtmlUnitDriver unitDriver = new HtmlUnitDriver();
```

上面的语法将构造一个禁用[JavaScript](https://www.toolsqa.com/javascript/javascript-tutorial/)和默认浏览器版本的新实例。如果你的应用程序需要某些JavaScript功能，你可以使用以下文章中提到的配置来启用它们：

#### 在 HtmlUnitWebDriver 中启用 JavaScript 支持

HtmlUnitWebDriver默认情况下不启用[JavaScript](https://www.toolsqa.com/javascript/javascript-tutorial/)。这意味着在上面的代码中，如果你尝试执行JavaScript，将抛出一个错误，指出 JavaScript 未启用。要创建一个启用了JavaScript的驱动程序实例，你可以使用构造函数HtmlUnitDriver(boolean enable javascript)并将值设置为 true，如下所示

```java
HtmlUnitDriver unitDriver=HtmlUnitDriver(true)
```

因此，这将创建启用JavaScript的HtmlUnitDriver实例。

现在我们已经创建了WebDriver的实例，让我们看看如何在我们的 Selenium 测试脚本中使用它来以无头模式运行测试。

#### 显示使用 HTMLUnitDriver 在 Headless 模式下运行 Selenium 测试用例的示例：

假设我们需要使用Selenium HTMLUnitDriver 自动化并运行以下测试场景：

1.  使用 URL - ["https://demoqa.com/"](https://demoqa.com/)启动网站。
2.  打印页面的标题。

我们可以使用以下代码片段实现相同的目的：

```java
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class headlessBrowserDemo {
	public static void main(String[] args) {
        // Declaring and initialising the HtmlUnitWebDriver
        HtmlUnitDriver unitDriver = new HtmlUnitDriver();
        
        // open demo site webpage
        unitDriver.get("https://demoqa.com/");
		
	//Print the title of the page
        System.out.println("Title of the page is -> " + unitDriver.getTitle());
        
    }
}
```

当你运行代码时，你不会在屏幕上看到任何浏览器，但是当执行完成时，它会打印页面标题，如下所示 -

![Selenium 中的 HtmlUnitDriver](https://www.toolsqa.com/gallery/selnium%20webdriver/5.HtmlUnitDriver%20In%20Selenium.png)

如上图突出显示的那样，页面标题打印为“ToolsQA”。

现在，当我们使用HTMLUnitDriver 的默认选项运行测试用例时，它会选择浏览器。该浏览器默认安装在你的机器上。如果你还想在特定浏览器版本中执行测试用例，你也可以在HTMLUnitDriver 的帮助下执行相同的操作。让我们了解如何实现相同的目标：

#### 如何使用 HtmlUnitDriver 在不同的浏览器版本上运行测试？

HtmlUnitDriver允许你选择要运行测试的浏览器版本。主要支持的浏览器是Chrome、Firefox 和 Internet Explorer。我们可以通过在构造函数中传递值来使用不同版本的浏览器，构造函数将使用指定版本的浏览器创建一个新实例，如下面的代码所示 -

```java
HTMLUnitDriver driver = new HTMLUnitDriver(BrowserVersion.Firefox_68);
```

你可以使用不同的浏览器版本，如下图所示-

![differentBrowserVersionsInHtmlUnitDriver](https://www.toolsqa.com/gallery/selnium%20webdriver/6.differentBrowserVersionsInHtmlUnitDriver.png)

此外，如果你想在特定浏览器版本中启用JavaScript ， HTMLUnitDriver的另一个构造函数允许使用指定的浏览器版本创建新实例，并通过设置值 = true 来支持JavaScript ，如下面的代码行 -

```java
HtmlUnitDriver unitDriver = new HtmlUnitDriver(BrowserVersion.FIREFOX_68,true);
```

让我们尝试使用 Firefox 68 版使用HTMLUnitDriver编写完整的代码 -

```java
import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class headlessBrowserDemo {
public static void main(String[] args) {
        // Declaring and initialising the HtmlUnitWebDriver using Firefox 68 version
        HtmlUnitDriver unitDriver = new HtmlUnitDriver(BrowserVersion.FIREFOX_68);
        
        // open demo site
        unitDriver.get("https://www.demoqa.com/");
        System.out.println("Title of the page is -> " + unitDriver.getTitle());

    }
}
```

同样，执行上述代码时，你不会在屏幕上看到任何浏览器，页面标题将打印在输出中：

![HtmlUnitDriver](https://www.toolsqa.com/gallery/selnium%20webdriver/7.HtmlUnitDriver.png)

如上图突出显示的那样，页面标题打印为“ToolsQA”。

### 使用无头 Chrome 浏览器运行 Selenium 无头浏览器测试。

[Headless Chrome](https://developers.google.com/web/updates/2017/04/headless-chrome)是一种在没有完整浏览器用户界面的无头环境中Chrome 浏览器Headless Chrome为你提供了一个真正的浏览器上下文，而无需运行完整版Chrome 的内存开销。 Google Chrome从 59 版本开始支持无头执行WebDriver提供了一个名为“ChromeOptions”的类，它可以指定某些配置来更改Chrome 的默认行为。其中一种配置是“无头”模式，它以无头模式启动Chrome运行测试用例时的模式。以下代码片段显示了我们如何使用ChromeOptions类传递“headless”选项。它还展示了我们如何在创建ChromeDriver 实例时使用这些选项：

```java
ChromeOptions options = new ChromeOptions()
options.addArgument("headless");
ChromeDriver driver = new ChromeDriver(options);
```

在上面的代码中，使用Selenium WebDriver提供的ChromeOptions类的addArgument()方法指示浏览器以 Headless 模式运行。

现在，假设我们必须运行与上一节相同的测试用例，其中我们使用HTMLUnitDriver运行Selenium测试，在Chrome 无头模式下，我们可以修改代码，如下所示：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class headlessChromeDemo {

    public static void main(String[] args) {
        //declare the chrome driver from the local machine location
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
       
        //create object of chrome options
        ChromeOptions options = new ChromeOptions();
        
        //add the headless argument
        options.addArguments("headless");
        
        //pass the options parameter in the Chrome driver declaration
        WebDriver driver = new ChromeDriver(options);
        
        //Navigate to toolsQA site url
        driver.get("https://demoqa.com/");
        
        //Print the Title of the Page
        System.out.println("Title of the page is -> " + driver.getTitle());
        
        //Close the driver
        driver.close();
    }
}
```

注意：关于在 Chrome 浏览器上设置 ChromeDriver 和运行 Selenium 测试，你可以参考文章[“在 Chrome 浏览器中运行测试”](https://www.toolsqa.com/selenium-webdriver/run-selenium-tests-on-chrome/)。

上面代码的输出/结果将打印页面的标题。

![使用 Chrome 在 Selenium 中进行无头浏览器测试](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Headless%20Browser%20Testing%20in%20Selenium%20with%20Chrome.png)

上面的屏幕截图突出显示了使用代码打印的页面的标题。

### 使用无头 Firefox 浏览器运行 Selenium 无头浏览器测试。

对于Headless Firefox，我们必须以与在 Headless Chrome 中相同的方式工作。首先，我们将设置[Gecko 驱动程序](https://www.toolsqa.com/selenium-webdriver/selenium-geckodriver/) (用于 Firefox 浏览器)的路径，然后将选项设置为无头。这里，我们使用Selenium WebDriver提供的FirfoxOptions类的setHeadless (true)方法。

你可以使用以下代码在Headless模式下使用Firefox -

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;


public class headlessFirefoxDemo {

    public static void main(String[] args) {
       //set the path of the Gecko driver as per the location on local machine
        System.setProperty("webdriver.gecko.driver", "C:\\geckodriver.exe");

        //Set Firefox Headless mode as TRUE
        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        
        //pass the options parameter in the Firefox driver declaration
        WebDriver driver = new FirefoxDriver(options);
        
        //Navigate to toolsQA site url
        driver.get("https://demoqa.com/");
        
        //Print the Title of the Page
        System.out.println("Title of the page is -> " + driver.getTitle());
        
        //Close the driver
        driver.close();
    }
}
```

关于FirefoxDriver的设置和在Firefox浏览器上运行Selenium测试，你可以参考文章[“在Firefox浏览器中运行测试”](https://www.toolsqa.com/selenium-webdriver/selenium-geckodriver/)。

上述代码的输出/结果如下 -

![使用 Firefox 在 Selenium 中进行无头浏览器测试](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Headless%20Browser%20Testing%20in%20Selenium%20with%20Firefox.png)

在上面的屏幕截图中，你可以看到脚本执行清楚地表明“Headless Mode is running”，如标记“1”突出显示，然后页面标题打印为“ToolsQA”，如标记“2”突出显示.

### 使用无头 Edge 浏览器运行 Selenium 无头浏览器测试。

对于Edge浏览器中的无头执行，我们将使用EdgeOptions类来配置无头执行。EdgeOptions是一个类，用于管理特定于Microsoft Edge Driver 的选项。你可以使用此类配置边缘驱动程序的设置。它类似于我们在本文的上述主题中使用ChromeOptions为 Chrome 浏览器所做的。由于Edge的基础主要是Chromium平台，因此它的选项也将与Chrome 几乎相似。你可以使用以下代码片段来设置EdgeDriver的“headless”选项。

```java
EdgeOptions edgeOptions =new EdgeOptions();
  edgeOptions.addArguments("headless");
  WebDriver driver= new EdgeDriver(edgeOptions);
```

要以无头模式运行 Edge，你必须使用addArguments()方法。并且，将值作为“无头”传递以指示驱动程序进行无头测试。

我们可以修改上面的测试，让它在Edge headless 上运行，如下图：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class headlessEdgeDemo {

    public static void main(String[] args) {
        // Set the edge driver path as per the machine location
        System.setProperty("webdriver.edge.driver","C:\\seleniumDemo\\msedgedriver85.exe");
       
        //Initialize the EdgeOptions class
        EdgeOptions edgeOptions =new EdgeOptions();
        
        //Use the addArguments method for configuring headless
        edgeOptions.addArguments("headless");
        
        //Pass the edgeOptions object to the Edge Driver
        WebDriver driver= new EdgeDriver(edgeOptions);
        
        //Navigate to toolsQA site url
        driver.get("https://demoqa.com/");
        
        //Print the Title of the Page
        System.out.println("Title of the page is -> " + driver.getTitle());
        
        //Close the driver
        driver.close();
    }
}
```

上述代码的输出/结果如下：

![无头边缘](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Headless%20Edge.png)

注意：之前还有其他的headless浏览器，比如PhantomJS、Opera，我们用它来做headless测试，现在我们可以用Headless Chrome或者Firefox，它可以服务大部分的headless测试。根据 Selenium 的最新更新，随着 Selenium 4 Alpha 版本的发布，对Opera和PhantomJS浏览器的支持已被删除。这是因为这些浏览器的 WebDriver 实现不再处于积极开发状态。

## 关键要点

-   无头浏览器是一种没有用户界面的浏览器模拟程序。
-   使用无头浏览器的显着好处之一是性能。由于无头浏览器没有 GUI，因此它们比真正的浏览器更快。
-   Selenium 支持使用 HtmlUnitDriver 进行无头浏览器测试。
-   HtmlUnitDriver 基于 java 框架 HtmlUnit，是所有无头浏览器中轻量级和最快的浏览器之一。
-   使用 HtmlUnitDriver，你可以在浏览器版本 Class 中提供的各种浏览器版本的 Chrome、Firefox 和 Internet Explorer 上测试应用程序。
-   此外，要在无头 Chrome 上执行测试，你可以使用 ChromeOptions 将参数设置为无头。
-   对于无头 Firefox，你可以将无头模式设置为 true 以执行测试。
-   此外，要在 Edge 上以无头模式运行测试，你可以使用 EdgeOptions 类并将值设置为“ headless in the addArgument() method ”。