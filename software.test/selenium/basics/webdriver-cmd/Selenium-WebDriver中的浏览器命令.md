我想到的第一个问题是什么是 Selenium WebDriver？它是一个自动化工具吗？这是一个班级吗？它是一个接口还是它实际上是什么？要回答这个问题，我们需要先了解Advance Java OOPs 概念，然后我们才能将WebDriver 实现可视化。为了简单起见，我们暂时避开这个WebDriver 实现主题，并将在后面的章节中介绍。到目前为止，我们从我们从 WebDriver 获得的所有方法开始。

## Selenium Webdriver 浏览器命令

现在下一个问题是，如何访问 WebDriver 的方法？要检查 WebDriver 中的所有内容，请从WebDriver创建一个驱动程序对象并按点键。这将列出 WebDriver 的所有方法。

![WebDriverCommands_01](https://www.toolsqa.com/gallery/selnium%20webdriver/1.WebDriverCommands_01.png)

注意：方法后跟Object关键字是从 Java 中的对象类获取的泛型方法。你会发现这些方法适用于 java 语言的每个对象。

-   橙色标记的建议是直接在 WebDriver 下的方法，将仅在本章中介绍这些方法。
-   蓝色标注的建议是WebDriver下的Nested Classes，后面的章节会单独详细介绍。
-   绿色标出的建议也是类似WebDriver的Interface，后面的章节会单独详细介绍。
-   Violet Color中标注的建议与Orange类似，但会在后面的章节中单独详细介绍。

让我们开始讨论Selenium WebDriver的橙色方法，但在此之前尝试理解 Eclipse 为 WebDriver 显示的建议的语法。

![Selenium Webdriver 浏览器命令](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Selenium%20Webdriver%20Browser%20Commands.png)

方法：Java 方法是组合在一起执行操作的语句的集合。

-   方法名：要访问任何类的任何方法，我们需要创建一个类的对象，然后该对象的所有公共方法都会出现。
-   Parameter：它是作为执行某些操作的参数传递给方法的参数。每个参数都必须以相同的数据类型传递。例如get(String arg0)：无效。这是要求一个String 类型的参数。
-   返回类型：方法可以返回一个值或什么都不返回 ( void )。如果在方法后面提到 void，则表示该方法没有返回值。如果它返回任何值，则它必须显示值的类型，例如 getTitle() : String。

现在就可以很容易地理解下一章中的 WebDriver 命令了。你喜欢使用 Selenium 做的第一件事是打开一个新浏览器，执行一些任务并关闭浏览器。以下是你可以在 Selenium 打开的浏览器上应用的命令数量。

### Get-Command - 如何在 Selenium 中打开网页？

get(String arg0) : void - 此方法在当前浏览器窗口中加载新网页。接受 String 作为参数并且不返回任何内容。

命令 - driver.get(appUrl);

其中appUrl是要加载的网站地址。最好使用完全限定的 URL。

```java
driver.get("https://www.google.com");

//Or can be written as 

String URL = "https://www.DemoQA.com";
driver.get(URL);
```

### 获取标题命令 - 如何在 Selenium 中获取网页的标题？

getTitle(): String - 此方法获取当前页面的标题。不接受任何参数并返回一个字符串值。

命令 - driver.getTitle();

由于返回类型是 String 值，因此输出必须存储在 String 对象/变量中。

```java
driver.getTitle();

//Or can be used as

String Title = driver.getTitle();
```

### 获取当前 URL 命令 - 如何在 Selenium 中读取网页的 URL？

getCurrentUrl(): String - 此方法获取表示在浏览器中打开的当前 URL的字符串。不接受任何参数并返回一个字符串值。

命令 - driver.getCurrentUrl();

由于返回类型是字符串值，输出必须存储在字符串对象/变量中。

```java
driver.getCurrentUrl();

//Or can be written as

String CurrentUrl = driver.getCurrentUrl();
```

### 获取页面源命令 - 如何在Selenium中读取网页的页面源？

getPageSource(): String - 此方法返回页面的源代码。不接受任何参数并返回一个字符串值。

命令 - driver.getPageSource();

由于返回类型是字符串值，输出必须存储在字符串对象/变量中。

```java
driver.getPageSource();

//Or can be written as 
String PageSource = driver.getPageSource();
```

### 关闭命令 - 如何在 Selenium 中关闭浏览器？

close(): void - 此方法仅关闭 WebDriver 当前控制的当前窗口。不接受任何参数并且不返回任何内容。

命令 - driver.close();

如果它是当前打开的最后一个窗口，请退出浏览器。

```java
driver.close();
```

### 退出命令 - 如何在 Selenium 中关闭所有浏览器窗口？

quit(): void - 此方法关闭WebDriver打开的所有窗口 。不接受任何参数并且不返回任何内容。

命令 - driver.quit();

关闭每个关联的窗口。

```java
driver.quit();
```

注意：请务必注意，此命令只会关闭同一会话中由 selenium 打开的浏览器窗口。如果手动打开任何浏览器，这不会对其产生影响。此外，即使是 Selenium，也不会影响在另一次运行或会话中打开的浏览器。close() 方法也是如此。

## Selenium WebDriver 浏览器命令 - 实践练习

### 练习练习 - 1

1.  启动新的 Chrome 浏览器。
2.  打开 Shop.DemoQA.com
3.  获取页面标题名称和标题长度
4.  在 Eclipse 控制台上打印页面标题和标题长度。
5.  获取页面 URL 并验证是否打开了正确的页面
6.  获取Page Source(HTML Source code)和Page Source长度
7.  在 Eclipse 控制台上打印页面长度。
8.  关闭浏览器。

#### 解决方案

```java
package automationFramework;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverCommands {

	public static void main(String[] args) {
		
		String driverExecutablePath = "D:\\Drivers\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", driverExecutablePath);
		// Create a new instance of the FireFox driver 
		WebDriver driver = new ChromeDriver(); 
		
		// Storing the Application Url in the String variable 
		String url = "https://www.shop.demoqa.com"; 
		
		//Launch the ToolsQA WebSite 
		driver.get(url); 
		
		// Storing Title name in the String variable 
		String title = driver.getTitle(); 
		
		// Storing Title length in the Int variable 
		int titleLength = driver.getTitle().length(); 
		
		// Printing Title & Title length in the Console window 
		System.out.println("Title of the page is : " + title); 
		System.out.println("Length of the title is : "+ titleLength); 
		
		// Storing URL in String variable 
		String actualUrl = driver.getCurrentUrl(); 
		
		if (actualUrl.equals(url)){ 
			System.out.println("Verification Successful - The correct Url is opened.");
		}
		else {
			System.out.println("Verification Failed - An incorrect Url is opened."); 
			
			//In case of Fail, you like to print the actual and expected URL for the record purpose 
			System.out.println("Actual URL is : " + actualUrl); 
			System.out.println("Expected URL is : " + url);
		}
		
		// Storing Page Source in String variable 
		String pageSource = driver.getPageSource(); 
		
		// Storing Page Source length in Int variable 
		int pageSourceLength = pageSource.length(); 
		
		// Printing length of the Page Source on console 
		System.out.println("Total length of the Pgae Source is : " + pageSourceLength); 
		
		//Closing browser 
		driver.close();
		}

	}
```

输出

页面标题为：ToolsQA 演示站点 – ToolsQA – 演示电子商务站点 标题长度为：50 验证失败 - 打开了不正确的 Url。 实际 URL 是：http ://shop.demoqa.com/ 预期 URL 是：http ://www.shop.demoqa.com Pgae 源的总长度是：88952

### 练习练习 - 2

1.  启动新的 Chrome 浏览器。
2.  打开 Switch Windows 的 ToolsQA 实践自动化页面：[https ://demoqa.com/browser-windows/](https://demoqa.com/browser-windows/)
3.  使用此语句单击新浏览器窗口按钮“ driver.findElement(By.id("New Browser Window")).click(); ”
4.  使用 close() 命令关闭浏览器

你会注意到只有一个窗口会关闭。下次使用 quit() 命令而不是 close()。那时硒将关闭两个窗口。

```java
package automationFramework;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverCommands_2 {

	public static void main(String[] args) {
		
		
		String driverExecutablePath = "D:\\Drivers\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", driverExecutablePath);
		// Create a new instance of the FireFox driver 
		WebDriver driver = new ChromeDriver(); 
		
		// Storing the Application Url in the String variable 
		String url = "https://demoqa.com/browser-windows/"; 
		
		//Launch the ToolsQA WebSite 
		driver.get(url); 
		
		
	}

}
```