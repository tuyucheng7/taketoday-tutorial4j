在本教程中，详细介绍了如何在 Selenium 中执行鼠标事件(如右键单击和双击)的概念。右键单击和双击是与网站交互的两个重要用户操作。需要在Selenium 测试自动化中执行这些操作。

## 如何使用 Action 类右键单击 Selenium？

在跳转到 Selenium Automation 中的右键单击操作之前，让我们首先了解什么是一般的右键单击。

### 什么是右键单击？

顾名思义，当用户试图在网站或 Web 元素上单击鼠标右键以查看其上下文菜单时。

例如，这是几乎每天都使用的非常常见的操作，尤其是在 Windows 资源管理器中使用文件夹时。同样在 Gmail 中，右键单击收件箱中的任何邮件会打开一个菜单，其中显示回复、删除、全部回复等选项。可以从中选择任何选项来执行所需的操作。

![右键单击硒](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Right%20Click%20in%20Selenium.png)

在许多情况下，自动化测试脚本需要执行类似的操作，即右键单击网页上的某个元素，然后从显示的上下文菜单中选择一个选项。要通过 Selenium 脚本执行右键单击操作，WebDriver API 不具备像其他 Action 命令那样的右键单击命令的功能：click、sendKeys。

这就是 Action 类通过提供各种重要方法来模拟用户操作而发挥作用的地方。该类最常用的方法之一是contextClick(WebElement)，它用于执行右键单击操作。

注意：动作类存在于包中：org.openqa.selenium.interactions 包

### 右键单击硒？

让我们看看如何使用 Action 类方法来右键单击：

首先，实例化一个 Actions 类：

动作 actions = new Actions(driver);



现在如上所示，contextClick()方法具有要传递的参数WebElement 。因此，需要将一个 WebElement 对象传递给该方法。这个 WebElement 应该是我们想要执行右键单击的按钮或任何 Web 元素。要查找元素，请使用以下命令：

WebElement webElement = driver.findElement(Any By strategy & locator);

在这里，你可以使用任何By 策略来定位 WebElement，例如通过其 id、name 属性等查找元素。要了解所有 By 策略的更多信息，请参阅[Find Elements](https://www.toolsqa.com/selenium-webdriver/find-element-selenium/)教程。

现在，当我们获得动作类对象和元素时，只需为右键单击调用perform()方法：

actions.contextClick(webElement).perform();

让我们看看调用上面的perform()方法时内部发生了什么：

-   移动到元素：  contextClick() 方法首先执行 mouseMove 到元素位置的中间。此函数在 Web 元素的中间执行右键单击。
-   Build： build() 方法用于生成包含所有动作的复合动作。但是如果你观察的话，我们并没有在上面的命令中调用它。内部在 perform 方法中执行构建。
-   Perform： perform() 方法执行我们指定的操作。但在此之前，它首先在内部调用 build() 方法。构建完成后，执行操作。

### 使用 Selenium 中的 Action 类练习执行右键单击

让我们考虑一个来自 Toolsqa 上已经可用的演示页面的示例，如“ http://demoqa.com/buttons ”。

![右键单击 Selenium 中的动作类](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Right%20Click%20with%20Action%20Class%20in%20Selenium.png)

在上图中，右键单击按钮会显示上下文菜单。现在让我们编写一个 selenium 脚本来使用contextClick()方法右键单击按钮。

在下面找到要自动化的场景的步骤：

1.  启动网络浏览器并启动我们的练习页面 https://demoqa.com/tooltip-and-double-click/
2.  在我们的示例中找到所需的元素，即按钮，然后右键单击该元素
3.  转到“复制”选项并单击它
4.  接受警报信息
5.  关闭浏览器结束程序

在下面找到用于在 Web 元素上执行右键单击的代码：

```java
package com.toolsqa.tutorials.actions;
 
	import org.openqa.selenium.By;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.firefox.FirefoxDriver;
	import org.openqa.selenium.interactions.Actions;
 
	public class RightClickDemo {

	 public static void main(String[] args) {
	   //Note: Following statement is required since Selenium 3.0, 
	   //optional till Selenium 2.0
	   //Set system properties for geckodriver 
	   System.setProperty("webdriver.gecko.driver", "C:\\Selenium\\Toolsqa\\lib\\geckodriver.exe");
	 
	   // Create a new instance of the Firefox driver
	   WebDriver driver = new FirefoxDriver();

	   // Launch the URL 
	   driver.get("https://demoqa.com/buttons");
	   System.out.println("demoqa webpage displayed");
		
	   //Maximise browser window
	   driver.manage().window().maximize();
		 
	   //Instantiate Action Class
	   Actions actions = new Actions(driver);
		 
	   //Retrieve WebElement to perform right click
	   WebElement btnElement = driver.findElement(By.id("rightClickBtn"));
		 
	   //Right Click the button to display Context Menu&nbsp;
	   actions.contextClick(btnElement).perform();
	   System.out.println("Right click Context Menu displayed");
		 
	   //Following code is to select item from context menu which gets open up on right click, this differs 
	   //depending upon your application specific test case: 
	   //Select and click 'Copy me' i.e. 2nd option in Context menu 
	   WebElement elementOpen = driver.findElement(By.xpath(".//div[@id='rightclickItem']/div[1]"));  
	   elementOpen.click(); 
		 
	   // Accept the Alert 
	   driver.switchTo().alert().accept();
	   System.out.println("Right click Alert Accepted");
		 
	   // Close the main window 
	   driver.close();
	   
	  }
	  
	}
```

注意：如果对 Alert 窗口操作感到困惑，请查看[如何在 Selenium 中处理警报？](https://www.toolsqa.com/selenium-webdriver/alerts-in-selenium/)

现在，让我们继续进行另一个重要操作，即双击。

## 如何使用 Action 类在 Selenium 中双击？

### 什么是双击？

双击 是经常使用的用户操作。双击最常见的用法发生在文件资源管理器中，例如在文件资源管理器中，双击文件夹中的任何文件夹或文件都可以打开它。

类似地，在任何网页上，可能有一些元素需要双击才能调用对它们的操作。与右键单击类似，在 Selenium 中没有 WebDriver API 命令可以双击 Web 元素。

因此，需要使用Action 类方法 doubleClick(WebElement)来执行此用户操作。

### 双击硒

让我们看看如何使用 Actions 类方法来双击：

首先，让我们实例化一个 Actions 类

动作 actions = new Actions(driver);

现在如上所示，doubleClick()方法具有要传递的参数 WebElement。因此将 WebElement 对象传递给需要执行双击的方法。

WebElement webElement = driver.findElement(Any By strategy);

同样，就像右键单击一样，使用任何 By 策略来定位 WebElement，例如通过其 id、name 属性等查找元素。

现在，只需为我们的双击调用构建和执行

actions.doubleClick(webElement).perform();

doubleClick() 方法也遵循Move to Element >> Build >> Perform的相同过程，这与右键单击相同。

### 使用 Selenium 中的 Action 类练习执行双击

让我们考虑以下示例：

![双击 Selenium](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Double%20CLick%20in%20Selenium.png)

在上面的示例中，双击按钮会在屏幕上显示警告消息。

在下面找到要自动化的场景的步骤：

1.  启动网络浏览器并启动我们的练习页面 http://demoqa.com/buttons
2.  在我们的示例中找到所需的元素，即按钮，然后双击该元素
3.  接受警报信息
4.  关闭浏览器结束程序

在下面找到用于双击 Web 元素的代码：

```java
package com.toolsqa.tutorials.actions; 
import org.openqa.selenium.By; 
import org.openqa.selenium.WebDriver; 
import org.openqa.selenium.WebElement; 
import org.openqa.selenium.firefox.FirefoxDriver; 
import org.openqa.selenium.interactions.Actions; 

public class DoubleClickDemo { 

	public static void main(String[] args) {

	//Note: Following statement is required since Selenium 3.0, 
	//optional till Selenium 2.0 
	//Set system properties for geckodriver 
	System.setProperty("webdriver.gecko.driver", "C:\\Selenium\\Toolsqa\\lib\\geckodriver.exe"); 
	
	// Create a new instance of the Firefox driver 
	WebDriver driver = new FirefoxDriver(); 
	
	// Launch the URL 
	driver.get("https://demoqa.com/buttons"); 
	System.out.println("Demoqa Web Page Displayed"); 
	
	//Maximise browser window 
	driver.manage().window().maximize(); 
	
	//Instantiate Action Class 
	Actions actions = new Actions(driver); 
	
	//Retrieve WebElement to perform double click WebElement
	btnElement = driver.findElement(By.id("doubleClickBtn")); 
	
	//Double Click the button 
	actions.doubleClick(btnElement).perform(); 
	
	System.out.println("Button is double clicked"); 
	
	//Following code just click on OK button on alert , this differs 
	//depending upon application(under test) specific test case  
	// Accept the Alert  
	driver.switchTo().alert().accept(); 
	System.out.println("Double click Alert Accepted"); 
	
	//Close the main window 
	driver.close();
	} 
}
```

### 概括

在本文中，我们了解了如何使用 Action 类方法对 Web 元素执行右键单击和双击。Actions 类还提供其他各种方法来执行各种键盘事件。我们将在 Selenium 系列的另一篇文章中看到这些内容。