众所周知，[Selenium](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)是一组工具，可提供大量功能来自动化复杂的 Web 应用程序。此外，它的流行已经产生了广泛的社区粉丝群，而且它是开源的这一事实有助于它每天添加一流的功能。[Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/download-selenium-webdriver/)提供了多种方法，允许用户在被测 Web 应用程序上自动化各种用户旅程。为了测试 Web 应用程序，用户需要在浏览器中打开应用程序，然后需要对各种 Web 元素执行特定的操作/验证。随后，在本文中，我们将学习如何编写和运行 Selenium 测试，通过涵盖以下主题下的详细信息，使用 Selenium WebDriver 在 Web 应用程序上执行多个步骤：

-   编写第一个 Selenium 测试用例
    -   如何使用 Selenium WebDriver 打开浏览器？
    -   如何使用 Selenium WebDriver 导航到网页？
    -   另外，如何使用 Selenium WebDriver 在网页上定位网页元素？
    -   如何使用 Selenium WebDriver 对 Web 元素执行操作？
    -   如何使用 Selenium WebDriver 对 Web 元素执行验证？
    -   还有，如何使用 Selenium WebDriver 关闭浏览器？
-   如何运行 Selenium 测试用例
    -   如何在 Eclipse 中执行 Selenium 测试用例？

## 编写第一个 Selenium 测试用例

在开始编写第一个 Selenium 之前，我们需要满足一些先决条件，如下所述：

-   [安装和设置 Java](https://www.toolsqa.com/selenium-webdriver/install-java/)
-   [下载 WebDriver Java 客户端](https://www.toolsqa.com/selenium-webdriver/download-selenium-webdriver/)
-   [在 Eclipse 中配置 Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)

完成上述步骤后，你现在就可以编写第一个 Selenium 测试用例了。假设我们使用Selenium WebDriver 进行自动化的下一个用户旅程：

-   首先，打开浏览器。
-   其次，导航到[ToolsQA 演示网站。](https://demoqa.com/login)
-   第三，最大化浏览器窗口。
-   之后，检索页面的标题。
-   第五，通过指定凭据登录网站。
-   第六，验证注销按钮是否可见。
-   最后，我们从网站注销。

注意：我们将使用登录用户名 - 'testuser' 和密码 - ' Password@123 ' 作为我们的测试脚本。此外，如果需要，你可以为自己创建一个用户。

现在让我们开始逐步编写我们的第一个Selenium 测试用例。

### 如何使用 Selenium WebDriver 打开浏览器？

使用Selenium WebDriver自动化任何测试用例的第一步是“打开浏览器”。Selenium WebDriver为不同的浏览器提供了多种驱动程序，例如Chrome 浏览器的 ChromeDriver、IE 的 InternetExplorerDriver、Firefox 浏览器的 GeckoDriver。因此，根据你的要求，你可以选择所需的驱动程序并为指定的浏览器实例化WebDriver 。

正如我们所知，WebDriver是一个可以使用任何浏览器的驱动程序类实例化的[接口。](https://docs.oracle.com/javase/tutorial/java/concepts/interface.html)使用Firefox和Chrome驱动程序实例化WebDriver的一些示例如下所示：

使用 Firefox Driver 实例化 WebDriver：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
System.setProperty("webdriver.gecko.driver", "<Gecko Driver executable location on your system>"); 
WebDriver driver = new FirefoxDriver();
```

使用 Chrome Driver 实例化 WebDriver：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
System.setProperty("webdriver.chrome.driver", "Chrome Driver executable location on your system"); 
WebDriver driver = new ChromeDriver();
```

随后，我们来了解一下上述代码片段的细节：

#### 导入 WebDriver 包：

正如我们所见，上面的代码片段都以两个import语句开头。[Java 导入语句](https://www.programiz.com/java-programming/packages-import)允许访问我们在其他包中声明的类/方法。我们可以使用import 关键字在我们的 java 源文件中导入内置和用户定义的包。此外，在这样做时，我们的类可以通过直接使用其名称来引用另一个包中的类。

你可以看到第一个 import 语句对于所有三个示例都是通用的，即

-   导入 org.openqa.selenium.WebDriver；-- 此导入语句引用实例化浏览器的 WebDriver 接口。

第二条导入语句，即

-   导入 org.openqa.selenium.firefox.FirefoxDriver；, 导入 org.openqa.selenium.chrome.ChromeDriver; 另一方面，引用 Driver 类，它使用 WebDriver 接口实例化浏览器驱动程序。

#### 设置浏览器驱动路径：

从 Selenium 3 开始，Selenium WebDriver需要一个外部可执行文件，它将充当 Selenium 测试和相应浏览器之间的通信媒介。此外，需要显式设置此可执行文件的路径。有多种方法可以设置驱动程序对应于每个浏览器的路径，其中之一是使用“System.setProperty”方法，使用它我们可以设置各种驱动程序特定的属性，例如“webdriver.chrome.driver “用于chrome，”webdriver.gecko.driver“用于firefox。

#### WebDriver 的对象实例化

第四个也是最重要的语句是通过引用WebDriver 接口创建驱动程序类的对象。如上所述，这个对象/实例将在我们的测试脚本中调用各种WebDriver方法。让我们在这里考虑 chrome 示例，

WebDriver 驱动程序 = new ChromeDriver();

上面的代码行将在安全模式下实例化一个 Chrome 浏览器，没有插件和扩展。

另外，其他浏览器的详细介绍以及WebDriver的各种实例化方式，可以参考以下文章：

-   [在 Firefox 浏览器上运行 Selenium 测试用例。](https://www.toolsqa.com/selenium-webdriver/selenium-geckodriver/)
-   [在 Chrome 浏览器上运行 Selenium 测试用例。](https://www.toolsqa.com/selenium-webdriver/run-selenium-tests-on-chrome/)
-   同样，你可以阅读有关在 Edge 浏览器上运行 Selenium 测试用例的信息。
-   [在 Safari 浏览器上运行 Selenium 测试用例。](https://www.toolsqa.com/selenium-webdriver/running-tests-in-safari-browser/)
-   [在 Internet Explorer 浏览器上运行 Selenium 测试用例。](https://www.toolsqa.com/selenium-webdriver/selenium-tests-on-internet-explorer/)

一旦我们创建了对应于特定浏览器的WebDriver对象，下一步就是在浏览器中打开我们的测试应用程序。随后，让我们看看如何使用Selenium WebDriver 实现相同的目的：

### 如何使用 Selenium WebDriver 导航到网页？

WebDriver实例化后，我们将导航到所需的网页。Selenium WebDriver中有两种方法，你可以使用它们导航到特定网页。

1.  driver.get ("URL") - 导航到作为参数传递的 URL 并等待页面加载
2.  driver.navigate().to ("URL") - 导航到作为参数传递的 URL，不等待页面加载。此外，它还维护浏览器历史记录以向前和向后导航。

你可以使用这两种方法中的任何一种，但通常首选get()方法，因为它会在页面完全加载之前停止任何进一步的操作。我们将在示例中使用get()方法。此外，它的代码如下所示 -

```java
driver.get("https://demoqa.com/login");
```

驱动程序变量将调用get()方法来导航到网站 URL，该 URL 作为 String 参数传递。

通常，Selenium WebDriver打开浏览器时，不会全屏打开浏览器，这是用户使用任何浏览器的方式。因此，即使在测试自动化中，我们也应该以全尺寸打开浏览器。让我们看看如何使用Selenium WebDriver 实现相同的目的：

#### 使用 Selenium Webdriver 最大化浏览器窗口？

当你使用Selenium WebDriver 启动浏览器时，它可能无法以全尺寸启动。我们可以随时使用一行简单的代码最大化浏览器窗口 -

```java
driver.manage().window().maximize();
```

在manage()方法返回Options接口实例的地方 ，现在这个 Options 接口有一个返回Window类型的window方法 。然后我们使用Window 接口的最大化方法来最大化浏览器窗口。

#### 检索页面的标题：

就像 get() 方法一样，getTitle() 是WebDriver 提供的另一个有趣的方法。它获取当前打开的网页的标题并返回与字符串相同的标题。在下面的代码中，驱动程序变量调用getTitle()方法并将值存储在字符串变量“ title ”中。 然后我们使用Java print 语句在控制台窗口上打印这个字符串变量：

```java
String title = driver.getTitle();
System.out.println("The page title is : " +title);
```

所以，现在我们已经了解了 Selenium WebDriver 提供了什么，不同的浏览器特定方法，以及我们如何导航到特定页面，最大化浏览器并在浏览器中获取当前打开的网页的标题。现在，让我们看看Selenium WebDriver如何定位我们需要执行特定操作的特定 Web 元素：

### 如何使用 Selenium WebDriver 在网页上定位网页元素？

编写自动化脚本的下一步是定位我们想要与之交互的 Web 元素。Selenium WebDriver提供了多种[定位器策略](https://www.toolsqa.com/selenium-webdriver/selenium-locators/)，使用这些策略可以定位页面上的各种 Web 元素。“ [By”](https://www.toolsqa.com/selenium-webdriver/find-element-selenium/)提供了所有这些定位器。Selenium WebDriver类。对于我们的示例场景，我们使用了[XPath](https://www.toolsqa.com/selenium-webdriver/xpath-in-selenium/)定位器策略来定位用户名、密码和登录按钮元素，如下所示：

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

WebElement uName = driver.findElement(By.xpath("//[@id='userName']"));
WebElement pswd = driver.findElement(By.xpath("//[@id='password']"));
WebElement loginBtn = driver.findElement(By.xpath("//[@id='login']"))
```

如你所见，不同的 Web 元素存储在将对这些元素执行操作的变量中。这样，你还会注意到代码中添加了另外两个导入语句：

-   导入 org.openqa.selenium.By; -它引用我们调用定位器类型的 By 类。
-   导入 org.openqa.selenium.WebElement；-它引用有助于实例化新 Web 元素的 WebElement 类。

现在我们已经找到了 Web 元素，让我们看看如何在这些 Web 元素上执行各种操作，例如键入、单击等：

### 如何使用 Selenium WebDriver 对 Web 元素执行操作？

在找到 Web 元素并将它们存储为WebElement实例后，我们将对它们执行操作，例如输入用户名和密码的文本并单击登录按钮。Selenium WebDriver提供了多种方法来对各种网络元素执行操作。WebElement 接口公开的一些方法是：

-   sendKeys() - 用于在 web 元素中输入文本
-   submit() - 用于提交表单
-   click() - 用于执行对网页元素的点击
-   clear() - 用于清除输入的文本

例如，我们可以使用上面提到的方法，输入用户名和密码，如下图：

```java
uName.sendKeys("testuser");
pswd.sendKeys("Password@123");
loginBtn.click();
```

注意：可以从文章[WebElement 命令中了解更多不同的 WebElement 命令。](https://www.toolsqa.com/selenium-webdriver/webelement-commands/)

### 如何使用 Selenium WebDriver 对 Web 元素执行验证？

一旦我们对 Web 元素执行了相应的操作，下一步应该是对某些 Web 元素执行验证，这将确保我们执行的操作成功执行并导致元素的所需状态。

例如，对于测试用户旅程，一旦我们能够成功登录到 ToolsQA 演示站点，注销按钮应该是可见的。让我们看看如何验证注销按钮的可见性，如果它可见，则单击注销按钮：

```java
try {
			
WebElement logoutBtn = driver.findElement(By.xpath("//div[@class='text-right col-md-5 col-sm-12']//button[@id='submit']"));
							
if(logoutBtn.isDisplayed()){
   logoutBtn.click();
   System.out.println("LogOut Successful!");
 }
} 
catch (Exception e) {
     System.out.println("Incorrect login....");
}
```

在哪里，

-   我们首先使用 XPath 找到注销按钮
-   之后，我们使用“isDisplayed()”方法检查它是否显示。
-   如果是，我们继续单击它并打印成功消息。如果没有，我们打印错误信息。请注意，我们将代码包围在 try-catch 块中。由于在登录凭据不正确的情况下可能会出现异常，因此我们的测试脚本将失败。使用try-catch块，我们可以处理这种情况并优雅地退出执行。

注意：你可以参考有关[异常处理](https://www.toolsqa.com/selenium-webdriver/exception-handling-selenium-webdriver/)的文章以了解更多相关信息。

现在测试脚本所需的操作和验证已完成，最后一步是关闭浏览器。让我们看看如何使用Selenium WebDriver 关闭浏览器：

### 如何使用 Selenium WebDriver 关闭浏览器？

标记测试脚本关闭的最后一步是关闭浏览器。你可以使用Selenium WebDriver提供的以下任何方法关闭浏览器：

-   close() -它关闭当前浏览器窗口。
-   quit() - 它调用 WebDriver 的 dispose() 方法关闭 WebDriver 打开的所有浏览器窗口并终止 WebDriver 会话。总是推荐使用 quit() 方法，因为它会释放驱动程序。

以下代码用于驱动程序调用quit方法。

```java
driver.quit();
```

现在，我们可以组合上面提到的所有步骤来创建完整的测试脚本。合并后的代码如下所示：

```java
package firstPackage;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class MyFirstTestClass {

	public static void main(String[] args){
		//Setting the driver path
		System.setProperty("webdriver.chrome.driver", "E:\\Softwares\\chromedriver.exe");
		
		//Creating WebDriver instance
		WebDriver driver = new ChromeDriver();
		
		//Navigate to web page
		driver.get("https://demoqa.com/login");
		
		//Maximizing window
		driver.manage().window().maximize();
		
		//Retrieving web page title
		String title = driver.getTitle();
		System.out.println("The page title is : " +title);
		
		//Locating web element
		WebElement uName = driver.findElement(By.xpath("//[@id='userName']"));
		WebElement pswd = driver.findElement(By.xpath("//[@id='password']"));
		WebElement loginBtn = driver.findElement(By.xpath("//[@id='login']"));
		
		
		//Peforming actions on web elements
		uName.sendKeys("testuser");
		pswd.sendKeys("Password@123");
		loginBtn.click();
		
		//Putting implicit wait
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		try {
			
			//Locating web element
			WebElement logoutBtn = driver.findElement(By.xpath("//div[@class='text-right col-md-5 col-sm-12']//button[@id='submit']"));
			//Validating presence of element				
			if(logoutBtn.isDisplayed()){
				
				//Performing action on web element
				logoutBtn.click();
				System.out.println("LogOut Successful!");
			}
		} 
		catch (Exception e) {
			System.out.println("Incorrect login....");
		}
		
		//Closing browser session
		driver.quit();
		
	}

}
```

现在我们已经准备好了Selenium WebDriver测试用例，让我们看看如何执行这个测试脚本：

## 运行 Selenium 测试用例

执行 Selenium 测试用例会改变它的编写方式和使用的测试执行器。有各种可用的测试执行器，例如[Junit、](https://www.toolsqa.com/java/junit-framework/junit-introduction/) [TestNG，](https://www.toolsqa.com/testng/testng-tutorial/)但为了更简单，我们将所有细节放在 Java 类的“main”方法中，因此我们可以像运行一样执行此测试脚本一个简单的 Java 类。让我们看看如何通过[Eclipse IDE 做到这一点：](https://www.toolsqa.com/selenium-webdriver/download-and-start-eclipse/)

### 如何在 Eclipse 中执行 Selenium 测试用例？

假设我们已经将上面的 Selenium 测试脚本保存为“firstPackage”包下的“MyFirstTestClass ” 。要执行测试脚本，我们执行以下步骤：

1.  转到运行 > 运行方式 > Java 应用程序。或者，你可以右键单击Eclipse 代码并单击运行方式 > Java 应用程序。

![通过 Eclipse 运行 Selenium WebDriver 测试](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Selenium%20WebDriver%20Test%20Run%20through%20Eclipse.png)

1.  接下来，你将看到Chrome 浏览器启动，并且根据我们的脚本，演示网站将打开。通过测试脚本捕获的标题将打印在 Eclipse 控制台窗口中，然后会发生登录。你将在完成执行时看到成功注销。控制台日志如下所示，你的第一个 Selenium WebDriver测试脚本成功执行了！

![Selenium WebDriver 测试脚本执行结果](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Selenium%20WebDriver%20Test%20Script%20Execution%20Results.png)

从上面的屏幕截图可以明显看出，测试脚本能够成功登录到演示站点，然后通过单击“注销”按钮注销。

## 关键要点

-   你现在已经可以逐步编写你的第一个 Selenium WebDriver 测试脚本，涵盖所有必要的操作。
-   此外，你可以快速找到你希望使用的 Web 元素并执行它们。
-   此外，你还了解了最基本的 selenium 命令，包括 wait 命令，并将它们合并到你的第一个脚本中。
-   除了上述之外，你现在还可以使用 Java 概念通过 Selenium WebDriver 验证你的网页。
-   最后，你可以通过 Eclipse IDE 快速执行测试。