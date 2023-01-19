[Selenium](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)无疑通过在 Web 浏览器上模拟人类交互来简化重复的测试任务，但在通过自动化执行测试时存在特定差异。你可能遇到过手动打开网站工作正常的情况，但通过[Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/selenium-tutorial/)进行的相同操作会抛出一个错误：“ This Connection is Untrusted ”。你有没有想过为什么会这样？答案很简单，手动打开一个URL，浏览器会自动导入所需的证书，不会出现错误。同时，在Selenium WebDriver中，每次运行都发生在没有SSL Certificates，因此出现错误。这篇文章将了解有关SSL 证书，看看我们如何在 Selenium 中处理 SSL 证书，并在各种浏览器中对 SSL 证书错误采取适当的措施。我们将介绍以下详细信息：

-   什么是 SSL 证书？
    -   什么是不受信任的 SSL 证书？
    -   SSL 证书如何工作？
    -   SSL 证书有哪些不同类型？
    -   SSL 证书错误有哪些不同类型？
-   如何使用 Selenium WebDriver 处理 SSL 证书错误？
    -   Selenium Webdriver 如何处理 Chrome 中的 SSL 证书？
    -   Selenium Webdriver 如何在 Firefox 中处理 SSL 证书？
    -   Selenium Webdriver 如何处理 Edge 中的 SSL 证书？
    -   Selenium Webdriver 如何处理 Safari 中的 SSL 证书？

## 什么是 SSL 证书？

SSL(安全套接字层)是一种标准的安全协议，它在服务器和客户端(浏览器)之间建立安全连接。使用 SSL 证书发送的信息经过加密，可确保将其传送到正确的服务器。它是网站身份的验证器，有助于防止黑客入侵。

### 什么是不受信任的 SSL 证书？

每当你尝试访问网站时，SSL 证书都会帮助确定该网站是否与其声称的一样。如果证书有任何问题，你将在浏览器窗口中看到一条错误消息：“此连接不受信任”。或者“ Your connection is not private ”，如下图：

![不受信任的 SSL 证书错误](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Untrusted%20SSL%20Certificate%20Error.png)

如你所见，在不受信任的 SSL情况下可能会出现一个示例错误。在接下来的部分中，我们将看到存在哪些不同类型的SSL 证书错误，以及我们如何在 Selenium 自动化中处理它们。在开始之前，让我们现在看看 SSL 证书是如何工作的？

### SSL 证书如何工作？

如前所述，SSL证书有助于创建安全连接。下图显示了客户端和服务器之间的SSL 握手工作：

![SSL 的工作原理](https://www.toolsqa.com/gallery/selnium%20webdriver/2.How%20SSL%20Works.png)

我们可以将内部工作总结为以下步骤：

1.  浏览器向服务器发送HTTPS请求。
2.  为了证明服务器是可信的，服务器向浏览器发送 SSL 证书。
3.  现在每个浏览器都有自己的可信证书颁发机构 (CA) 列表。浏
4.  如果上述步骤中的所有检查都正确，则浏览器信任该证书。此外，它还会导致在服务器和浏览器之间创建加密会话。
5.  服务器和浏览器现在可以以加密格式相互发送消息。

### SSL 证书有哪些不同类型？

如果你曾经看过从证书颁发机构收到的用于你的网站 SSL 证书的 zip 文件，你就会看到该 zip 文件包含的不是一个而是不同的 SSL 文件。证书签名机构提供三种类型的 SSL 证书：

1.  根证书
2.  中级证书
3.  服务器证书

根证书 是属于证书颁发机构的数字证书。它已在大多数浏览器中预下载，并且CA严密保护它。

另一方面，中间证书就像根 证书和服务器证书之间的链接。它将客户端链接到 CA 并需要在你的服务器上安装。

服务器证书 是你获得的用于在服务器上安装的主要证书。 

由于我们的议程是使用 Selenium 处理不受信任的 SSL 证书，我们将快速跳到下一个主题，你可以阅读有关[SSL 证书的更多信息 https://en.wikipedia.org/wiki/Transport_Layer_Security](https://www.toolsqa.com/selenium-webdriver/ssl-certificate-in-selenium/)。

### SSL 证书错误有哪些不同类型？

当你执行HTTPS请求并收到类似“此站点不安全”或“你的连接不是私密的”之类的消息时，可能会有机会。此SSL证书错误可能会在其他浏览器中显示不同的错误消息，如下所示：

1.  Chrome - 你的连接不是私密的。

![不受信任的 SSL 错误 Chrome](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Untrusted%20SSL%20Error%20Chrome.png)

\2. Firefox - 警告：潜在的安全风险。

![不受信任的 SSL 错误 Firefox](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Untrusted%20SSL%20Error%20Firefox.png)

\3. IE - 此站点不安全。

![不受信任的 SSL 错误 IE](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Untrusted%20SSL%20Error%20IE.png)

4. Safari - Safari 无法验证网站的身份。

![不受信任的 SSL 错误 Safari](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Untrusted%20SSL%20Error%20Safari.png)

虽然在不同的浏览器上出现的错误信息可能会有所不同，但所有这些错误都有一个共同点，即错误的原因。每个不受信任的 SSL 都有其原因。下面列出了一些标准错误原因：

-   过期证书错误- 当网站证书过期并显示错误代码(如 ERR_CERT_DATE_INVALID)时发生。
-   吊销证书错误- 当网站的证书被吊销并显示错误代码时发生 - ERR_CERT_REVOKED
-   自签名证书错误- 当证书是自签名的或由不受信任的来源签名并显示错误代码 ERR_CERT_AUTHORITY_INVALID 时发生。

上述错误很常见，但你访问的网站可能会遇到其他一些SSL 错误。现在让我们了解如何处理各种广泛使用的浏览器中的不受信任错误的错误？

## 如何使用 Selenium WebDriver 处理 SSL 证书错误？

现在我们了解了为什么会出现SSL 错误，现在我们将了解如何在Selenium Automation中处理此错误。由于我们可以在任何浏览器中遇到此错误，因此我们将了解如何在Firefox、Chrome、IE 和 Safari 浏览器中处理此错误。但在进入处理部分之前，让我们先看看如果我们访问一个有SSL 证书问题的网站会发生什么。

我们会使用一个[Demo网站(https://badssl.com/](https://badssl.com/))，里面有不同类型的证书错误，大家也可以参考练习。下面是一个简单的代码，当我们在 Chrome 浏览器中导航到URL时，它会引发证书错误：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class SSLHandling {

	public static void main(String[] args) {

		//Creating instance of Chrome driver (Assuming Chromedriver is installed at system level)
		WebDriver driver = new ChromeDriver();
		
		//Launching the URL
		driver.get("https://expired.badssl.com/");
		System.out.println("The page title is : " +driver.getTitle());
		driver.quit();
	}

}
```

执行上述代码后，你将看到网页启动。尽管如此，屏幕仍显示Connection Error，这是过期/无效证书的 SSL 证书错误之一。

![过期证书的 SSL 错误](https://www.toolsqa.com/gallery/selnium%20webdriver/7.SSL%20Error%20for%20Expired%20Certificate.png)

执行日志正在捕获页面标题，如下图所示-

![在不处理 SSL 错误的情况下执行的控制台日志](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Console%20logs%20for%20execution%20without%20handling%20SSL%20error.png)

我们现在将看到如何在不同的Web 浏览器中处理此类错误，并在 Selenium 代码中处理 SSL 证书后查看实际网页。

### Selenium Webdriver 如何处理 Chrome 中的 SSL 证书？

对于 Chrome 浏览器，可以使用Selenium WebDriver提供的ChromeOptions类处理SSL 证书。这是一个我们可以用来为 Chrome 浏览器设置属性的类。现在让我们看看我们将对现有代码进行哪些添加，然后我们就会理解相同的内容。

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SSLHandling {

	public static void main(String[] args) {

		//Create instance of ChromeOptions Class
		ChromeOptions handlingSSL = new ChromeOptions();

		//Using the accept insecure cert method with true as parameter to accept the untrusted certificate
		handlingSSL.setAcceptInsecureCerts(true);
				
		//Creating instance of Chrome driver by passing reference of ChromeOptions object
		WebDriver driver = new ChromeDriver(handlingSSL);
		
		//Launching the URL
		driver.get("https://expired.badssl.com/");
		System.out.println("The page title is : " +driver.getTitle());
		driver.quit();
	}

}
```

我们可以将上面的代码总结如下 -

-   ChromeOptions handlingSSL = new ChromeOptions(); -- 我们创建一个 ChromeOptions 类的对象。
-   handlingSSL.setAcceptInsecureCerts(真)；- 现在我们将使用功能setAcceptInsecureCerts。我们将参数作为 true 传递，这意味着无效证书将被浏览器隐式信任。
-   WebDriver 驱动程序 = new ChromeDriver (handlingSSL); -- 接下来，我们创建一个 chrome 驱动程序实例并将 ChromeOptions 对象作为参数传递，以便我们的浏览器会话将继承我们刚刚设置的属性。

你会看到该网页没有显示我们之前在运行代码时看到的任何错误。另请注意，控制台日志中的页面标题不是“隐私错误”-

![在 chrome 中处理不受信任的 SSL 证书错误后的日志](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Logs%20after%20handling%20Untrusted%20SSL%20Certificate%20error%20in%20chrome.png)

注意：对于你的练习，请尝试将参数保持为“ False ”并查看相同的代码结果。

### Selenium Webdriver 如何处理 Firefox 中的 SSL 证书？

在Selenium 4之前，我们曾经使用FirefoxOptions或FirefoxProfile或DesiredCapabilities来处理Firefox 中的 SSL 证书错误。但是随着Selenium WebDriver上最新和更新版本的引入，SSL 证书将自动为Firefox处理。与我们在Chrome和其他浏览器中遇到的错误不同，我们不需要编写额外的代码行来接受 Firefox 中不受信任的 SSL 证书。让我们在不编写任何代码来处理Firefox 浏览器的 Selenium 中的SSL 证书的情况下执行：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class HandleSsl {

	public static void main(String[] args) {


		System.setProperty("webdriver.gecko.driver", "D:\\Selenium\\geckodriver.exe");
		
		WebDriver driver = new FirefoxDriver();
		
		driver.get("https://self-signed.badssl.com/");
		System.out.println("The page title is : " +driver.getTitle());
		driver.quit();
	}

}
```

从上面的代码可以看出，我们没有使用任何额外的类。在执行/运行上面的代码时，你将看到如下结果 -

![selenium 中 firefox 的 ssl 证书的执行结果](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Execution%20results%20for%20firefox%20for%20ssl%20certificate%20in%20selenium.png)

注意：由于我们的文章系列是关于 Selenium 4 的，因此我们假设你必须使用最新的 selenium 版本。

如你所见，你无需编写任何特殊代码来处理 Firefox 中不受信任的证书；让我们尝试一下，如果我们显式使用FirefoxOptions和setAcceptInsecureCerts 方法，并将“ False ”作为 Firefox 执行的参数。

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class SSLHandling {

	public static void main(String[] args) {

		//Creating an object of the FirefoxOptions Class
		FirefoxOptions firefoxOptions = new FirefoxOptions();
				
		//Using the setAcceptInsecureCerts() method to pass parameter as False
		firefoxOptions.setAcceptInsecureCerts(false);
				
		WebDriver driver = new FirefoxDriver(firefoxOptions);
		
		driver.get("https://self-signed.badssl.com/");
		System.out.println("The page title is : " +driver.getTitle());
		driver.quit();
	}

}
```

-   FirefoxOptions opts = new FirefoxOptions(); -- 首先创建一个 FirefoxOptions 对象。
-   opts.setAcceptInsecureCerts(假)；-- 我们通过将布尔值 false 作为参数传递给 Accept Insecure certificates 方法来拒绝接受不受信任的证书。
-   WebDriver driver = new FirefoxDriver (opts); -- 我们现在将 Firefox 选项传递给 WebDriver 实例，以便浏览器以预加载的设置打开。

注意：为了大家的理解和学习，执行上面的代码，看看结果。

### Selenium Webdriver 如何处理 Edge 中的 SSL 证书？

你必须知道，Edge浏览器正在慢慢接管Internet Explorer，后者很快就会被弃用。现在，我们将看到通过使用Selenium WebDriver提供的EdgeOptions类在 Selenium 中为 Edge 浏览器处理 SSL 。它类似于使用ChromeOptions或FirefoxOptions。

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class SSLHandling {

	public static void main(String[] args) {

		//Creating an object of EdgeOptions class
		EdgeOptions edgeOptions = new EdgeOptions();
				
		//Accepting the Insecure certificates through boolean parameter
		edgeOptions.setAcceptInsecureCerts(true);
						
		//Creating instance of Edge driver by passing reference of EdgeOptions object
                // Assuming EdgeDriver path has been set in system properties
		WebDriver driver = new EdgeDriver(edgeOptions);
		
		driver.get("https://self-signed.badssl.com/");
		System.out.println("The page title is : " +driver.getTitle());
		driver.quit();
	}

}
```

如前所述，EdgeOptions的工作方式类似于 Firefox 和 Chrome 选项。因此，我们的代码将与其他两个浏览器的代码类似，但在使用类名方面有所不同。

-   EdgeOptions ssl = new EdgeOptions(); -- 创建了 EdgeOptions 的对象。
-   ssl.setAcceptInsecureCerts(真)；-- 不安全的证书通过使用布尔值 true 作为参数来接受。
-   WebDriver 驱动程序 = new EdgeDriver (ssl); -- 选项现在传递给 WebDriver 实例以从所需设置开始。

上面的代码的执行将在接受不受信任/不安全的证书后为我们获取网页，然后打印页面的标题。

![Edge 中不受信任的 SSL 处理的控制台输出](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Console%20output%20for%20Untrusted%20SSL%20handling%20in%20Edge.png)

### Selenium Webdriver 如何处理 Safari 中的 SSL 证书？

对于Safari，不受信任的证书处理有点不同。我们需要执行一个 JavaScript 片段，允许浏览器通过证书并导航到预期的页面。让我们看看，当我们尝试导航到具有不受信任的 SSL 证书的网页时， Safari 浏览器中的错误会是什么样子：

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;

public class SSLHandling {

    public static void main(String[] args) {

        WebDriver driver = new SafariDriver();

        driver.get("https://revoked.badssl.com");
        
        System.out.println("The page title is : " + driver.getTitle());
        driver.quit();
    }
}
```

当我们执行上面的测试脚本时，浏览器会卡在SSL证书错误界面，如下图：

![错误：Safari 浏览器中 Selenium 中的 SSL 证书](https://www.toolsqa.com/gallery/selnium%20webdriver/12.Error%20SSL%20Certificate%20in%20Selenium%20in%20Safari%20browser.png)

此外，它将在控制台中显示输出，如下所示：

![在 Safari 浏览器中处理 SSL](https://www.toolsqa.com/gallery/selnium%20webdriver/13.Handle%20SSL%20in%20Safari%20browser.png)

现在，为了在 Safari 浏览器中处理这种情况，我们可以通过添加以下 JavaScript 来更新上面编写的 Selenium 脚本：

```
CertificateWarningController.visitInsecureWebsiteWithTemporaryBypass()
```

它将允许暂时绕过不安全的 SSL 证书。包含此 JavaScript 代码后的最终代码片段如下所示：

```java
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;

public class SSLHandling {

    public static void main(String[] args) {

        WebDriver driver = new SafariDriver();

        driver.get("https://revoked.badssl.com");
        
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("CertificateWarningController.visitInsecureWebsiteWithTemporaryBypass()");

        System.out.println("The page title is : " + driver.getTitle());
        driver.quit();
    }
}
```

当我们执行上面的代码片段时，它将能够跳过不受信任的 SSL 证书并显示如下所示的输出：

![在 Selenium 中为 Safari 浏览器处理 SSL 证书后的控制台输出](https://www.toolsqa.com/gallery/selnium%20webdriver/14.Console%20output%20after%20handling%20SSL%20certificate%20in%20Selenium%20for%20Safari%20browser.png)

如我们所见，现在正在打印页面的正确标题。

注意：我们可以在 Internet Explorer 浏览器的 Selenium 中处理 SSL 证书，也使用相同的 JavaScript 代码。

## 要点：

-   SSL 有助于在客户端和服务器之间建立安全连接。
-   此外，SSL 证书以增量步骤工作以建立安全连接。
-   除此之外，根证书、中间证书和服务器证书是证书签名机构提供的不同类型的证书。
-   各种类型的 SSL 证书错误，如已撤销、自签名和已过期。
-   此外，可以分别在 Chrome、Firefox 和 Edge 浏览器中使用 ChromeOptions()、FirefoxOptions() 和 EdgeOptions() 处理不受信任的 SSL 证书。
-   最后，可以通过 JavaScriptExecutor 在 Safari 中处理不受信任的 SSL。