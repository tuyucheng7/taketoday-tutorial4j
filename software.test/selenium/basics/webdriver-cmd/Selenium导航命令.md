在 Firefox 浏览器上成功运行我们的[第一个测试用例](https://www.toolsqa.com/selenium-webdriver/run-selenium-test/)之后，我们现在正朝着掌握基本的Selenium 导航命令的方向迈进。因此，我们将讨论我们将在日常自动化测试中使用的各种 Selenium 导航命令。导航界面公开了在浏览器历史记录中前后移动的能力。

## Selenium Webdriver - 浏览器导航命令

要访问导航的方法，只需键入driver.navigate()。eclipse 的 IntelliSense 功能会自动显示Navigate Interface的所有公共方法，如下图所示。

![Selenium 导航命令 - Selenium Webdriver 浏览器导航命令](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Selenium%20Navigation%20Command%20-%20Selenium%20Webdriver%20Browser%20Navigation%20Command.png)

注意：只有后面跟着Navigation关键字的方法才属于 navigate。Rest 后跟Object关键字是从 Java 中的 Object 类获取的通用方法。你会为 Java 语言的每个对象找到这些方法。

### 导航到命令 - 如何导航到 URL 或如何在 Selenium 浏览器中打开网页？

to(String arg0) : void - 此方法在当前浏览器窗口中加载新网页。它接受一个 String 参数并且不返回任何内容。

命令 - driver.navigate().to(appUrl);

它与driver.get(appUrl)方法做的事情完全一样。其中appUrl是要加载的网站地址。最好使用完全限定的 URL。

```java
driver.navigate().to("https://www.DemoQA.com");
```

### 转发命令 - 如何在 Selenium 浏览器中浏览转发？

forward() : void - 此方法执行与单击任何浏览器的前进按钮相同的操作它既不接受也不返回任何东西。

命令 - driver.navigate().forward();

使你在浏览器的历史记录中前进一页。

```java
driver.navigate().forward();
```

### 后退命令 - 如何在 Selenium 浏览器中向后浏览？

back() : void - 此方法执行与单击任何浏览器的后退按钮相同的操作 它既不接受也不返回任何东西。

命令 - driver.navigate().back();

将你带回浏览器历史记录的一页。

```java
driver.navigate().back();
```

### 刷新命令 - 如何刷新 Selenium 浏览器？

refresh() : void - 此方法 刷新当前页面。它既不接受也不返回任何东西。

命令 - driver.navigate().refresh();

执行与在浏览器中按 F5 相同的功能。

```java
driver.navigate().refresh();
```

## Selenium 导航命令练习

### 练习练习

1.  启动新浏览器
2.  打开 DemoQA.com 网站
3.  使用 "driver.findElement(By.xpath(".// [@id='menu-item-374']/a")).click();"点击注册链接
4.  返回主页(使用“返回”命令)
5.  再次返回注册页面(这次使用“转发”命令)
6.  再次返回主页(这次使用“To”命令)
7.  刷新浏览器(使用“刷新”命令)
8.  关闭浏览器

### 解决方案

```java
package automationFramework;
	import org.openqa.selenium.By;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.firefox.FirefoxDriver;
public class NavigateCommands {
	public static void main(String[] args) {
		// Create a new instance of the FireFox driver
		WebDriver driver = new FirefoxDriver();

		// Open ToolsQA web site
		String appUrl = "https://www.DemoQA.com";
		driver.get(appUrl);

		// Click on Registration link
		driver.findElement(By.xpath(".//[@id='menu-item-374']/a")).click();

		// Go back to Home Page
		driver.navigate().back();

		// Go forward to Registration page
		driver.navigate().forward();

		// Go back to Home page
		driver.navigate().to(appUrl);

		// Refresh browser
		driver.navigate().refresh();

		// Close browser
		driver.close();
	}
}
```