本教程我们将介绍使用 Selenium WebDriver API 拖放的概念。让我们首先了解拖放操作的含义以及如何使用 Actions 类在 Selenium 中执行拖放操作。

## 什么是拖放操作？

这是用户移动(拖动)网络元素然后将其放置(放下)到备用区域时使用鼠标执行的操作。

例如，这是在将任何文件从一个文件夹移动到另一个文件夹时在 Windows 资源管理器中使用的一种非常常见的操作。在这里，用户选择文件夹中的任何文件，将其拖到所需的文件夹中，然后将其放下。

在 Gmail 中，只需将文件拖放到新邮件中即可作为附件发送，如下所示：

![拖放操作](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Drag-and-Drop-Action.png)

移动文件以撰写电子邮件后，你可以将其视为附件。在底部，显示我们从 Windows 拖到 Gmail 的文件现在作为附件附加。

![拖放硒](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Drag-and-Drop-in-selenium.png)

在各种实例的自动化测试脚本中，需要模拟相同的操作：

-   选择网页上的某个元素，将其拖动，然后将其放置在备用区域上。

要通过 Selenium 脚本执行拖放操作，WebElement 界面中没有直接可用的拖放方法。与click() 等其他命令不同，sendKeys()没有任何可用于拖放的命令。在这里，我们利用[Actions 类](https://www.toolsqa.com/selenium-webdriver/actions-class-in-selenium/)，它提供了模拟此类复杂交互的各种方法。

因此，这里是 Actions 类为拖放操作提供的方法：

1.  dragAndDrop(WebElementsource，WebElement 目标)
2.  dragAndDropBy(WebElementsource，int xOffset，int yOffset)

## 在 Selenium 中拖放

dragAndDrop(WebElement source, WebElement target)：该方法执行左键单击，按住单击以按住源元素，移动到目标元素的位置，然后释放鼠标单击。

让我们看看如何使用 Action 类方法来执行拖放操作：

首先，实例化一个 Actions 类：

动作 actions = new Actions(driver);

如你所见，dragAndDrop(WebElement source, WebElement target)方法有两个要传递的参数。一个是源 Web 元素，另一个是目标 Web 元素。此源网页元素是任何需要拖动的网页元素。目标网络元素是需要放置或放下拖动对象的任何网络元素。要查找源元素和目标元素，请使用以下命令：

WebElement source = driver.findElement(Any By strategy & locator);

WebElement target = driver.findElement(Any By strategy & locator);

在这里，你可以使用任何 By 策略来定位 WebElement，例如通过其id、name 属性等查找元素。要了解有关所有 By 策略的更多信息，请参阅[查找元素](https://www.toolsqa.com/selenium-webdriver/find-element-selenium/) 教程。

现在，当我们获得动作类对象和元素时，只需 为拖放操作调用perform()方法：

actions.dragAndDrop(source,target).perform();

让我们看看调用上面的 perform() 方法时内部发生了什么：

-   单击并按住动作： dragAndDrop() 方法首先在源元素的位置执行单击并按住
-   移动鼠标操作：然后将源元素移动到目标元素的位置
-   按钮释放动作：最后，它释放鼠标
-   Build： build() 方法用于生成包含所有动作的复合动作。但是如果你观察的话，我们并没有在上面的命令中调用它。内部在perform方法中执行构建
-   Perform： perform() 方法执行我们指定的操作。但在此之前，它首先在内部调用 build() 方法。构建完成后，执行操作

### 练习使用 Selenium 中的 Actions 类执行拖放操作

让我们考虑一个来自 Toolsqa 上已经可用的演示页面的示例，如[http://demoqa.com/droppable/](https://demoqa.com/droppable/)

![可拖动](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Dragable.png)

在上图中，可以选择“Drag me to my target”对象并将其拖放到“Drop here”对象上。一旦对象被放置在“放置在这里” ，就会显示以下消息

![可投放](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Droppable.png)

现在让我们编写一个 selenium 脚本来使用拖放方法拖放对象。

#### 在下面找到要自动化的场景的步骤：

1.  启动网络浏览器并启动我们的练习页面 https://demoqa.com/droppable/
2.  在我们的示例中找到所需的源元素，即“将我拖到我的目标”对象
3.  在我们的示例中找到所需的目标元素，即“Drop Here”对象
4.  现在将“将我拖到我的目标”对象拖放到“放在此处”对象
5.  验证“Drop Here”上显示的消息以验证源元素是否被放置在目标元素上
6.  关闭浏览器结束程序

硒代码片段：

```java
package com.toolsqa.tutorials.actions;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
 
public class DragAndDrop1 {
 
	public static void main(String[] args) throws InterruptedException {		
	
		 //Note: Following statement is required since Selenium 3.0, 
    		//optional till Selenium 2.0
    		//Set system properties for geckodriver 
    		System.setProperty("webdriver.gecko.driver", "C:\\Selenium\\Toolsqa\\lib\\geckodriver.exe");
	 
		WebDriver driver = new FirefoxDriver();
		 
		String URL = "https://demoqa.com/droppable/";
		 
		driver.get(URL);
		 
		// It is always advisable to Maximize the window before performing DragNDrop action		 
		driver.manage().window().maximize();
		 
		driver.manage().timeouts().implicitlyWait(10000, TimeUnit.MILLISECONDS);
		
		//Actions class method to drag and drop		
		Actions builder = new Actions(driver);
		 
		WebElement from = driver.findElement(By.id("draggable"));
		 
		WebElement to = driver.findElement(By.id("droppable"));	 
		//Perform drag and drop
		builder.dragAndDrop(from, to).perform();
		
		//verify text changed in to 'Drop here' box 
		String textTo = to.getText();

		if(textTo.equals("Dropped!")) {
			System.out.println("PASS: Source is dropped to target as expected");
		}else {
			System.out.println("FAIL: Source couldn't be dropped to target as expected");
		}
	
		driver.close();

	}	
 
}
```

## 使用 OffSets 在 Selenium 中的 DragAndDropBy 操作

dragAndDropBy(WebElement source, int xOffset, int yOffset)：此方法单击并按住源元素并移动给定的偏移量，然后释放鼠标。偏移由 x & y 定义。

-   xOffset 是水平移动
-   yOffset 是垂直移动

让我们看看如何使用[Action 类](https://www.toolsqa.com/selenium-webdriver/actions-class-in-selenium/)方法使用偏移来执行拖放操作。

首先，实例化一个 Actions 类：

动作 actions = new Actions(driver);

如上所示，dragAndDropBy() 方法有三个要传递的参数。源 Web 元素、xOffset和yOffset。这个 WebElement 源是任何需要拖放的 Web 元素。要查找源元素，请使用以下命令：

WebElement source = driver.findElement(Any By strategy & locator);

现在，剩下的两个参数值，即xOffset和yOffset是以像素为单位的值。

例如，如果 xOffset 值设置为 50，则表示需要将对象拖放 50 个像素偏移水平方向。同样，如果yOffset 值设置为50，则表示需要将对象拖放垂直方向偏移50 个像素。

这里解释一下如何获取元素的偏移量。

现在，当我们获得所有对象时，只需调用perform() 方法进行拖放：

actions.dragAndDropBy(source, xOffset, yOffset).perform();

这与我们在文章中首先讨论的dragAndDrop(source, target)完全一样。但唯一的区别是，在dragAndDropBy 中，它不是将源元素移动到目标元素，而是移动到偏移量。

### 练习练习

让我们考虑一个来自 Toolsqa 上已经可用的演示页面的示例，如https://demoqa.com/droppable/ 。这与我们在第一次练习中看到的样本完全相同。

现在让我们编写一个 selenium 脚本来使用dragAndDropBy()方法拖放对象。

#### 在下面找到要自动化的场景的步骤：

1.  启动网络浏览器并启动我们的练习页面 https://demoqa.com/droppable/
2.  在我们的示例中找到所需的源元素，即“将我拖到我的目标”对象
3.  计算分别在水平和垂直方向拖动源元素所需的 xOffset 和 yOffset [为此，通过获取源元素和目标元素的 x 位置之间的差异来计算 xOffset。同样，通过获取源元素和目标元素的 y 位置之间的差异来获取 yOffset]
4.  现在将“将我拖到我的目标”对象拖放到“放在此处”对象
5.  验证“Drop Here”上显示的消息以验证源元素是否被放置在目标元素上
6.  关闭浏览器结束程序

硒代码片段：

```java
package com.toolsqa.tutorials.actions;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
 
public class DragAndDrop2 {
 
	public static void main(String[] args) throws InterruptedException {
		
 		//Note: Following statement is required since Selenium 3.0, 
    		//optional till Selenium 2.0
    		//Set system properties for geckodriver 
    		System.setProperty("webdriver.gecko.driver", "C:\\Selenium\\Toolsqa\\lib\\geckodriver.exe");
		WebDriver driver = new FirefoxDriver();
		 
		String URL = "https://demoqa.com/droppable/";
		 
		driver.get(URL);
		 
		// It is always advisable to Maximize the window before performing DragNDrop action		 
		driver.manage().window().maximize();
		 
		driver.manage().timeouts().implicitlyWait(10000, TimeUnit.MILLISECONDS);
		
		//Actions class method to drag and drop			
		Actions builder = new Actions(driver);
		 
		WebElement from = driver.findElement(By.id("draggable"));
		 
		WebElement to = driver.findElement(By.id("droppable"));	 
		
		//Here, getting x and y offset to drop source object on target object location
		//First, get x and y offset for from object
		int xOffset1 = from.getLocation().getX();
		
		int yOffset1 =  from.getLocation().getY();
		
		System.out.println("xOffset1--->"+xOffset1+" yOffset1--->"+yOffset1);
		
		//Secondly, get x and y offset for to object
		int xOffset = to.getLocation().getX();
				
		int yOffset =  to.getLocation().getY();
		
		System.out.println("xOffset--->"+xOffset+" yOffset--->"+yOffset);
		
		//Find the xOffset and yOffset difference to find x and y offset needed in which from object required to dragged and dropped
		xOffset =(xOffset-xOffset1)+10;
		yOffset=(yOffset-yOffset1)+20;
		

		//Perform dragAndDropBy 
		builder.dragAndDropBy(from, xOffset,yOffset).perform();
		
		//verify text changed in to 'Drop here' box 
		//Get text value of 'to' element
		String textTo = to.getText(); 

		if(textTo.equals("Dropped!")) {
			System.out.println("PASS: Source is dropped at location as expected");
		}else {
			System.out.println("FAIL: Source couldn't be dropped at location as expected");
		}

		driver.close();
		
	}	 
}
```

#### 为你练习：

现在，你刚刚了解了如何通过指定xOffset和yOffset 进行拖放，你可以在http://demoqa.com/draggable/上尝试 dragAndDropBy()

提示：你可以在水平和垂直方向上的任意位置拖放源对象。

如果你仍然觉得困难，那么只检查以下代码：

```java
package com.toolsqa.tutorials.actions;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
 
public class DragAndDrop3 {
 
	public static void main(String[] args) throws InterruptedException {
		
 		//Note: Following statement is required since Selenium 3.0, 
    		//optional till Selenium 2.0
    		//Set system properties for geckodriver 
    		System.setProperty("webdriver.gecko.driver", "C:\\Selenium\\Toolsqa\\lib\\geckodriver.exe");		
 
		WebDriver driver = new FirefoxDriver();
		 
		String URL = "https://demoqa.com/draggable/";
		 
		driver.get(URL);
		 
		// It is always advisable to Maximize the window before performing DragNDrop action		 
		driver.manage().window().maximize();
		 
		driver.manage().timeouts().implicitlyWait(10000, TimeUnit.MILLISECONDS);
		
		//Actions class method to drag and drop			
		Actions builder = new Actions(driver);
		 
		WebElement from = driver.findElement(By.id("draggable"));
		
		//Perform dragAndDropBy 
		builder.dragAndDropBy(from, 100,100).perform();		

		System.out.println("Dropped");

		driver.close();

	}
	
 
}
```

### 概括：

在本教程中，我们介绍了网页中拖放元素的概念以及如何通过 Actions 类的以下方法执行：

-   dragAndDrop：在这里，我们看到了如何在 Selenium 中使用拖放操作，我们需要将源元素拖放到目标
-   dragAndDropBy：在这里，我们看到了如何按指定的偏移量在水平和垂直方向上拖放源元素