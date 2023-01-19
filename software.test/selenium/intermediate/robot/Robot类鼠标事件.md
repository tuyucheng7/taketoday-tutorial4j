正如[机器人类简介](https://www.toolsqa.com/selenium-webdriver/robot-class/)教程中所讨论的，它提供了可用于模拟键盘和鼠标操作的方法。例如，模拟操作系统弹出窗口/警报或计算器、记事本等操作系统应用程序。在本教程中，我们将介绍一些模拟 Java 机器人类 ( Robot Class Mouse Events ) 提供的鼠标事件的方法。

## 机器人类鼠标事件

让我们了解一下 Robot 类的一些基本技术，它们用于模拟鼠标事件。

-   mousePress ( int buttons )：此方法按下一个或多个鼠标按钮。

此处，参数按钮是按钮掩码。反过来，它是一个或多个鼠标按钮掩码的组合。

以下是可用于 mousePress 方法的标准按钮掩码：

-   InputEvent.BUTTON1_DOWN_MASK : 鼠标左键单击
-   InputEvent.BUTTON2_DOWN_MASK ：鼠标中键点击。
-   InputEvent.BUTTON3_DOWN_MASK : 鼠标右键点击
-   输入事件.BUTTON1_MASK
-   输入事件.BUTTON2_MASK
-   输入事件.BUTTON3_MASK
-   mouseRelease (int buttons)：此方法释放一个或多个鼠标按钮。 例如，robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK) 将释放鼠标左键。
-   mouseMove(int x, int y)：此方法将鼠标指针移动到给定的屏幕位置。这里，x是X位置，y是坐标中的Y位置。例如，方法 mouseMove(100, 50) 会将鼠标指针移动到屏幕上的 x 坐标 100 和 y 坐标 50。

### 如何使用 Robot 类在 Selenium 中执行鼠标单击？

让我们借助键盘教程中使用的示例示例来了解其中一些方法，即[https://demoqa.com/keyboard-events/](https://demoqa.com/keyboard-events/)

![机器人类鼠标事件](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Robot%20Class%20Mouse%20Events.png)

要选择要上传的文件，我们需要执行以下步骤：

-   首先，根据 X 和 Y 位置确定“文件名”的屏幕分辨率。
-   其次，将鼠标光标移动到标识的 x 和 y 坐标。
-   最后，单击已识别的 x 和 y 坐标。

#### 在 Selenium 中使用 java Robot 类的鼠标事件

我们将按照以下步骤来自动化文件上传场景：

1.  首先，我们将启动网络浏览器并启动我们的练习页面。https://demoqa.com/keyboard-events/
2.  其次，我们将单击“选择文件”按钮以显示桌面窗口弹出窗口。
3.  第三，我们通过识别文件名的坐标，将鼠标光标移动到想要的文件名上。
4.  第四，我们将在识别出的坐标上单击鼠标左键来选择文件。
5.  之后，我们将按 Enter 键关闭弹出窗口。
6.  最后，我们将关闭浏览器来结束程序。

以下是示例代码：

```java
package com.toolsqa.tutorials.actions;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

public class RobotMouseDemo {

	public static void main(String[] args) throws InterruptedException, AWTException, IOException {

		System.setProperty("webdriver.gecko.driver","C:\\Selenium\\lib\\geckodriver-v0.24.0-win64\\geckodriver.exe");

		// Start browser
		WebDriver driver = new FirefoxDriver();

		String URL = "https://demoqa.com/keyboard-events/";
		// maximize browser
		driver.manage().window().maximize();

		driver.get(URL);
		Thread.sleep(2000);

		// This will click on Browse button
		WebElement webElement = driver.findElement(By.id("browseFile"));
		webElement.sendKeys("ENTER");

		//Create object of Robot class
		Robot robot = new Robot();
         
		//Find x and y coordinates to pass to mouseMove method 
		//1. Get the size of the current window. 
		//2. Dimension class is similar to java Point class which represents a location in a two-dimensional (x, y) coordinate space. 
		//But here Point point = element.getLocation() method can't be used to find the position 
		//as this is Windows Popup and its locator is not identifiable using browser developer tool 
		Dimension i = driver.manage().window().getSize(); 
		System.out.println("Dimension x and y :"+i.getWidth()+" "+i.getHeight()); 
		//3. Get the height and width of the screen 
		int x = (i.getWidth()/4)+20; 
		int y = (i.getHeight()/10)+50; 
		//4. Now, adjust the x and y coordinates with reference to the Windows popup size on the screen 
		//e.g. On current screen , Windows popup displays on almost 1/4th of the screen . So with reference to the same, file name x and y position is specified. 
		//Note : Please note that coordinates calculated in this sample i.e. x and y may vary as per the screen resolution settings
		robot.mouseMove(x,y); 

		//Clicks Left mouse button
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK); 
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		System.out.println("Browse button clicked");
		Thread.sleep(2000);

		//Closes the Desktop Windows popup
		robot.keyPress(KeyEvent.VK_ENTER);
		System.out.println("Closed the windows popup");
		Thread.sleep(1000);

		// Close the main window 
		driver.close();
	}

}
```

### 结论

总而言之，在本教程中，我们介绍了 Robot 类的基本方法来控制鼠标在桌面 Windows 弹出窗口上执行鼠标操作。

-   mousePress(int buttons)：按下一个或多个鼠标按钮的方法
-   mouseRelease(int buttons)：释放一个或多个鼠标按钮的方法
-   mouseMove(int x, int y)：将鼠标指针移动到给定屏幕坐标的方法

因此，我们试图在本教程中涵盖所有基本的键盘和鼠标事件方法。但是，Robot 类提供了许多其他方法，如mouseWheel()、createScreenCapture()等。此外，你可以通过单击下面的链接来探索它们。[https://docs.oracle.com/javase/10/docs/api/java/awt/Robot.html](https://docs.oracle.com/javase/10/docs/api/java/awt/Robot.html)