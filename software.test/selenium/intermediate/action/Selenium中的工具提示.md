本教程是[Selenium 中的 Action Class 系列的一部分。](https://www.toolsqa.com/selenium-webdriver/actions-class-in-selenium/)这涵盖了在 Selenium Automation 中使用 WebDriver API捕获工具提示的概念。让我们首先了解什么是工具提示，为什么它在网站上可用以及测试它的目的。

## 什么是工具提示？

在许多网页中，将鼠标悬停在某些链接上时，文本、某些文本或有时会显示图像是很常见的。例如，在 Gmail 收件箱中，如果将鼠标悬停在右侧菜单选项上，则会 显示带有文本的小“悬停框” 。 这称为 web 元素的工具提示。

![Selenium 中的工具提示](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Tooltip%20in%20Selenium.png)

此文本通常是对象功能的简要描述，或者在某些情况下，它显示对象的详细描述。在某些情况下，它可能只显示对象的全名。所以基本上，工具提示的目的是向用户提供一些关于对象的提示。在许多情况下，需要验证此文本描述是否按预期显示。

基于以上原因，需要获取tooltip里面的文字，并验证文字。这可以通过不同的方式完成，具体取决于工具提示在 HTML 中的插入方式。让我们考虑以下情况：

-   情况一：当工具提示在“标题”属性中可用时。在这里，我们可以从 By strategy 检索工具提示
-   情况二：当工具提示在“div”中可用时。在这里，我们可以使用 Actions 类方法检索工具提示

我们将涵盖这两种情况。让我们考虑一下案例一：当工具提示在 title 属性中可用时。

## 如何使用 getAttribute 在 Selenium 中捕获工具提示？

让我们考虑一个来自 Toolsqa 上已经可用的演示页面的示例，如[http://demoqa.com/tool-tip。](https://www.demoqa.com/tool-tips/) 这是一种非常简单的工具提示显示方式。

![使用 Selenium Webdriver 自动化工具提示](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Automate%20Tooltip%20using%20Selenium%20Webdriver.png)

将鼠标悬停在 HTML 对象上时会显示工具提示。这也可以从浏览器的 Developer tool 中看出，tooltip 文本被视为在' title '属性中设置的值。

在大多数情况下，获取工具提示文本非常简单。你需要做的就是找到需要检索工具提示的网络元素。

WebElement webElement = driver.findElement(Any By strategy & locator);

在这里，可以使用任何 By 策略来定位 WebElement，例如通过其 id、name 属性等查找元素。要了解有关所有 By 策略的更多信息，请参阅[Find Elements](https://www.toolsqa.com/selenium-webdriver/find-element-selenium/) 教程。

现在，检索webElement的“ title ”属性值。

String tooltipText = webElement .getAttribute("title");

使用上述语句，工具提示中的文本将保存在名为tooltipText 的变量中。

### 在 Selenium 中检索工具提示的练习

在下面找到要自动化的场景的步骤：

1.  打开网址 http://demoqa.com/tooltip/
2.  识别网络元素年龄文本框
3.  检索 web 元素的“title”属性值
4.  验证“标题”属性值的文本是否与预期文本匹配
5.  关闭页面

硒代码片段：

```java
package com.toolsqa.tutorials.actions; 
	import org.openqa.selenium.By;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.firefox.FirefoxDriver;
	import org.openqa.selenium.interactions.Actions;

	public class TooltipDemo {
		public static WebDriver driver; 
		
		public static void main(String[] args) { 
		//Set system properties for geckodriver This is required since Selenium 3.0 
		System.setProperty("webdriver.gecko.driver", "C:\\Selenium\\Toolsqa\\lib\\geckodriver.exe"); 
		
		//Create a new instance of the Firefox driver driver = new FirefoxDriver(); 
		
		//CASE 1: Using getAttribute 
		// Launch the URL driver.get("https://demoqa.com/tool-tips/");
		System.out.println("Tooltip web Page Displayed");

		//Maximise browser window 
		driver.manage().window().maximize();

		// Get element for which we need to find tooltip 
		WebElement ageTextBox = driver.findElement(By.id("age"));

		//Get title attribute value 
		String tooltipText = ageTextBox.getAttribute("title"); 
		
		System.out.println("Retrieved tooltip text as :"+tooltipText); 
		
		//Verification if tooltip text is matching expected value 
		if(tooltipText.equalsIgnoreCase("We ask for your age only for statistical purposes.")){ 
			System.out.println("Pass : Tooltip matching expected value");
			}
		else{ 
			System.out.println("Fail : Tooltip NOT matching expected value"); 
		} 
		
		// Close the main window 
		driver.close(); 
	} 
}
```

## 如何使用 Actions 类在 Selenium 中捕获工具提示？

有多种方法可以在 HTML 页面中显示工具提示。如上所述，它可以放在标题标签内。同样的方法可以用不同的方式提取出来。上面我们只是定位了 title 元素并使用 Selenium 的 GetAttribute 方法读取它的属性。同样的方式也可以借助div元素来显示。

但这次我们尝试模仿与网站上任何其他用户相同的行为。将鼠标悬停在对象上并尝试阅读工具提示的描述。而这只能借助Selenium 中的 Actions 类来完成。 让我们看看如何使用 Actions 类方法来获取它：

![Tooltip_Case2](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Tooltip_Case2.png)

首先，实例化一个 Actions 类以使用它的对象。

动作 actions = new Actions(driver);

找到工具提示 web 元素。

WebElement element = driver.findElement(Any By strategy & locator);

现在，调用moveToElement()， 这个 Actions 类的方法将鼠标移动到元素的中间。

actions.moveToElement(元素).perform();

正如[Actions 类教程中所解释的，](https://www.toolsqa.com/selenium-webdriver/actions-class-in-selenium/) perform()方法实际上执行操作并在内部调用build()方法。所以，当这行代码执行时，你实际上可以在浏览器上看到元素的工具提示。

现在获取该工具提示元素并调用getText()以获取它的描述。

WebElement toolTip = driver.findElement(Any By strategy & locator);

String toolTipText = toolTip.getText();

### 练习使用 Actions 类在 Selenium 中检索工具提示。

在下面找到要自动化的场景的步骤：

1.  打开网址[http://demoqa.com/tool-tips](https://demoqa.com/tooltip-and-double-click/)
2.  识别“div”网络元素
3.  生成动作 moveToElement 并执行动作
4.  识别显示工具提示的网络元素
5.  检索文本属性值
6.  验证工具提示文本值是否与预期文本匹配
7.  关闭页面

硒代码片段：

```java
package com.toolsqa.tutorials.actions; 
	import org.openqa.selenium.By;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.firefox.FirefoxDriver;
	import org.openqa.selenium.interactions.Actions;

	public class TooltipDemo { 
		public static WebDriver driver; 
		
		public static void main(String[] args) { 
		
		//Set system properties for geckodriver This is required since Selenium 3.0 
		System.setProperty("webdriver.gecko.driver", "C:\\Selenium\\Toolsqa\\lib\\geckodriver.exe"); 
		
		// Create a new instance of the Firefox driver 
		driver = new FirefoxDriver(); 
		
		//CASE 2 : Using Actions class method 
		driver.get("https://demoqa.com/tool-tips"); 
		
		System.out.println("demoqa webpage Displayed"); 
		
		//Maximise browser window 
		driver.manage().window().maximize(); 
		
		//Instantiate Action Class 
		Actions actions = new Actions(driver); 
		
		//Retrieve WebElement 
		WebElement element = driver.findElement(By.id("tooltipDemo")); 
		
		// Use action class to mouse hover 
		actions.moveToElement(element).perform(); 
		
		WebElement toolTip = driver.findElement(By.cssSelector(".tooltiptext")); 
		
		// To get the tool tip text and assert 
		String toolTipText = toolTip.getText();
		System.out.println("toolTipText-->"+toolTipText); 
		
		//Verification if tooltip text is matching expected value 
		if(toolTipText.equalsIgnoreCase("We ask for your age only for statistical purposes.")){ 
			System.out.println("Pass : Tooltip matching expected value");
		}else{
			System.out.println("Fail : Tooltip NOT matching expected value"); 
		} 
		
		// Close the main window 
		driver.close();
	} 
}
```

在下一个教程中，我们将看看如何[在 Selenium 中执行拖放操作的场景。](https://www.toolsqa.com/selenium-webdriver/drag-and-drop-in-selenium/)