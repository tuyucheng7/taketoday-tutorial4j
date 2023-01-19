当你第一次尝试在 IE 浏览器上执行 Selenium 脚本时，你很可能会遇到一些 IE 错误。因此，大多数刚开始使用 Selenium IE 的人在开始时都会遇到五个常见问题。

## Selenium Internet Explorer (IE) 错误

在本章中，我们将介绍使用 IE 浏览器运行 Selenium 脚本的挑战。这些挑战在[Selenium 官方网站](https://code.google.com/p/selenium/wiki/InternetExplorerDriver)上有详细记录。但是我仍然经常在各种论坛上看到这些问题，所以我想为所有正在逐步遵循我的 Selenium 教程的人捕捉这些问题。

1.  驱动程序可执行文件的路径必须由 webdriver.ie.driver 设置
2.  所有区域的保护模式设置都不相同。
3.  意外错误浏览器缩放级别
4.  在 IE 浏览器中运行脚本时，SendKeys 键入字符非常慢
5.  IE 浏览器中出现不受信任的 SSL 证书错误

让我们开始一一讨论。

## IE 驱动路径

任何人在使用 IE 浏览器时都会面临的第一个问题是人们希望 IE 浏览器能够像 Selenium 中的 Firefox 浏览器一样工作。看看当你使用下面的 Selenium 脚本在 IE 浏览器上打开应用程序时会发生什么。

```java
// Change the package name accordingly, as per your 

project
package selenium;
	import org.openqa.selenium.ie.InternetExplorerDriver;
public class IEExplorerTest {

	public static void main(String[] args) {

		 InternetExplorerDriver  driver = new InternetExplorerDriver();
		 driver.get("https://demoqa.com");

	}
}
```

一旦执行上述代码，Selenium 将抛出异常：

线程“ main ”中的异常java.lang.IllegalStateException：驱动程序可执行文件的路径必须由 webdriver.ie.driver 系统属性设置；有关详细信息，请参阅 http://code.google.com/p/selenium/wiki/InternetExplorerDriver。最新版本可以从 http://selenium-release.storage.googleapis.com/index.html 下载

异常清楚地表明驱动程序的路径必须由 webdriver.ie.driver 系统属性设置。此错误通常是由于你的本地计算机上没有所需的IEDriverServer.exe 或未在你的PATH 环境变量中设置它造成的。

### 如何在 Selenium 脚本中设置 IE 浏览器的系统属性

我希望你已经阅读了[在 IE Explorer 中运行测试](https://toolsqa.com/selenium-webdriver/selenium-tests-on-internet-explorer/)这一章，并且你知道如何下载以及从哪里下载IEDriverServer exe。我希望你已经下载了IEDriverServer并将其保存到本地系统的某个 xyz 位置。

Selenium 脚本看起来像这样：

```java
// Change the package name accordingly, as per your project
package selenium;
	import org.openqa.selenium.ie.InternetExplorerDriver;
public class IEExplorerTest {
	public static void main(String[] args) {

		//Path to the folder where you have extracted the IEDriverServer executable
		 String service = "D:\\ToolsQA\\trunk\\Library\\drivers\\IEDriverServer.exe";
		 System.setProperty("webdriver.ie.driver", service);

		 InternetExplorerDriver  driver = new InternetExplorerDriver();
		 driver.get("https://demoqa.com");
	}
}
```

### IE浏览器路径环境变量设置方法

1.  打开控制面板->系统或安全->系统；同样的事情可以通过右键单击“我的电脑”并选择“属性”来完成。

![Java_Environment_Path_1](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Java_Environment_Path_1.png)

1.  选择“高级系统设置”。

![Java_Environment_Path_2](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Java_Environment_Path_2.png)

1.  在“高级”选项卡下，选择“环境变量... ”选项。![Java_Environment_Path_3](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Java_Environment_Path_3.png)

4) 现在我们需要在PATH变量中指定位置。对于PATH，它很可能已经存在于你的机器中。所以只需选择它并选择“编辑”选项。

![Java_Environment_Path_6](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Java_Environment_Path_6.png)

1.  在编辑器中添加值“你的 IE 浏览器位置”，然后单击“确定”。

![InternetExplorer_2](https://www.toolsqa.com/gallery/selnium%20webdriver/5.InternetExplorer_2.png)

注意：新值与现有值之间用分号分隔，请注意不要对现有字符串进行任何更改，因为它是非常敏感的信息。

现在尝试打开 IE 浏览器而不在 Selenium 脚本中指定系统属性。

```java
// Change the package name accordingly, as per your project
package selenium;
	import org.openqa.selenium.ie.InternetExplorerDriver;
public class IEExplorerTest {
	public static void main(String[] args) {

		 InternetExplorerDriver  driver = new InternetExplorerDriver();
		 driver.get("https://demoqa.com");
	}
}
```

注意：如果它不起作用，请重新启动机器。

## 保护模式设置

首先，让我们看看使用 Selenium WebDriver 脚本打开 Internet Explorer 驱动程序的代码。

```java
package selenium;

import org.openqa.selenium.ie.InternetExplorerDriver;

public class IEExplorerTest {

	public static void main(String[] args) {
		 //Path to the folder where you have extracted the IEDriverServer executable
		 String service = "D:\\ToolsQA\\trunk\\Library\\drivers\\IEDriverServer.exe";
		 System.setProperty("webdriver.ie.driver", service);

		 InternetExplorerDriver  driver = new InternetExplorerDriver();
		 driver.get("https://demoqa.com");
	}
}
```

一旦执行上述代码，Selenium 将抛出异常：

线程“ main ”中的异常 org.openqa.selenium.remote.SessionNotFoundException：启动 Internet Explorer 时出现意外错误。所有区域的保护模式设置都不相同。必须为所有区域将启用保护模式设置为相同的值(启用或禁用)。(警告：服务器未提供任何堆栈跟踪信息)

通过上面的错误，它清楚地表明保护模式设置存在一些问题，并且所有区域的保护模式设置都不相同。为避免此错误，我们需要为所有区域设置相同的保护模式设置。

### 如何在 IE 浏览器中设置保护模式设置

1.  转到工具 > Internet 选项，然后在 Internet 选项下单击安全选项卡。
2.  单击Internet区域以选择一个区域并查看其保护模式属性。
3.  现在选中启用保护模式复选框。无论你选择哪个，都需要为所有其他区域进行设置。这意味着它可以对所有区域关闭或对所有区域打开。

![InternetExplorer_1](https://www.toolsqa.com/gallery/selnium%20webdriver/6.InternetExplorer_1.png)

1.  对所有区域重复此任务

-   互联网
-   本地内联网
-   可信任的网站
-   受限网站

1.  单击确定并再次运行 Selenium 脚本。这次它会起作用。

## 浏览器缩放级别

如果你的应用程序没有启动，或者你只是收到一个或多个InternetExplorerDriver Listening on port window 消息，或者如果 Eclipse 控制台中出现以下异常，那么你的 IE 浏览器的缩放级别可能设置为 100% 以外的值。

线程“ main ”中的异常 org.openqa.selenium.remote.SessionNotFoundException：启动 Internet Explorer 时出现意外错误。浏览器缩放级别设置为 125%。它应该设置为 100%(警告：服务器未提供任何堆栈跟踪信息)

要修复，请确保将Internet Explorer 的缩放属性设置为 100%

![InternetExplorer_3](https://www.toolsqa.com/gallery/selnium%20webdriver/7.InternetExplorer_3.png)

## IE 浏览器上的 SendKeys 缓慢

这又是 IE 浏览器的一个非常常见的问题，即发送键在 Selenium 脚本中的运行速度非常慢。但同样不是火箭科学来解决这个问题。只需将当前的IEDriverServer.exe(如果你的机器是 64 位机器)替换为 32 位的 IEDriverServer.exe

1.  下载32位系统的IEDriverServer.exe，即使系统是64位机。[下载链接](https://selenium-release.storage.googleapis.com/2.41/IEDriverServer_Win32_2.41.0.zip)
2.  解压缩 Zip 文件并将IEDriverServer放在早期版本的IEDriverServer 所在的同一文件夹中。它将用新文件替换旧文件。
3.  再次运行脚本，这一次 sendsKey() 将起作用，并且它在 Chrome 和 Firefox 中的工作方式。

## 不受信任的 SSL 证书

Internet Explorer 是微软的产品， IE非常担心安全问题，被誉为最安全的浏览器。有时使用带有 Selenium 的 IE 浏览器会弹出SLL 证书。

有两种方法可以解决 SLL 证书问题。

### 方案一

在代码下方添加以下脚本以打开应用程序：

driver.navigate().to(“javascript:document.getElementById('overridelink').click()”);

完整的代码如下所示：

```java
// Change the package name accordingly, as per your project
package selenium;
	import org.openqa.selenium.ie.InternetExplorerDriver;
public class IEExplorerTest {

	public static void main(String[] args) {
		 //Path to the folder where you have extracted the IEDriverServer executable
		 String service = "D:\\ToolsQA\\trunk\\Library\\drivers\\IEDriverServer.exe";
		 System.setProperty("webdriver.ie.driver", service);

		 InternetExplorerDriver  driver = new InternetExplorerDriver();
		 driver.get("URL for which certificate error is coming");
		 driver.navigate().to("javascript:document.getElementById('overridelink').click()");  
	}
}
```

### 方案二

避免此错误的另一种方法是使用浏览器的 DesiredCapability 设置。

```java
// Change the package name accordingly, as per your project
package selenium;
	import org.openqa.selenium.ie.InternetExplorerDriver;
	import org.openqa.selenium.remote.CapabilityType;
	import org.openqa.selenium.remote.DesiredCapabilities;
public class IEExplorerTest {

	public static void main(String[] args) {
		 // Path to the folder where you have extracted the IEDriverServer executable
		 String service = "D:\\ToolsQA\\trunk\\Library\\drivers\\IEDriverServer.exe";
		 System.setProperty("webdriver.ie.driver", service);

		 // Create the DesiredCapability object of InternetExplorer
		 DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();

		 // Settings to Accept the SSL Certificate in the Capability object
		 capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

		 InternetExplorerDriver driver = new InternetExplorerDriver(capabilities); 
		 driver.get("URL for which certificate error is coming");

	}
}
```

放置上面的代码后，运行你的脚本，这次屏幕上不会出现 SSL 证书错误，脚本可以正常运行。