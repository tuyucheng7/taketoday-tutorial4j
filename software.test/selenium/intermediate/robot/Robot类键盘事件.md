在本教程中，我们将介绍一些机器人类键盘事件。

正如[Robot 类介绍](https://www.toolsqa.com/selenium-webdriver/robot-class/)教程中所讨论的，Robot 类提供了可用于模拟键盘和鼠标操作的方法，例如，模拟操作系统弹出窗口/警报，甚至模拟计算器、记事本等操作系统应用程序。

## Robot 类中的键盘方法

要模拟键盘操作，应使用以下 Robot 类方法：

键盘方法：

-   keyPress(int keycode)：此方法按下给定的键。参数键码是按下的键的整数值。例如，要按下字母 A 的键，必须传递的值是KeyEvent.VK_A即 keyPress(KeyEvent.VK_A)

    。

    -   KeyEvent通常是一个低级事件。在 Java AWT 中，低级事件是指示来自用户的直接通信的事件，例如按键、按键释放或鼠标单击、拖动、移动或释放等。KeyEvent 指示在按下、释放或键入时发生的事件组件对象上的键，如文本字段。
    -   这个 KeyEvent 类有各种常量字段，如整数类型的 VK_0、VK_1 到 VK_9。这些值与数字“0”到“9”的 ASCII 码相同。同样，对于字母表，此类具有常量字段，如 VK_A、VK_B 到 VK_Z。它还具有用于表示特殊字符的常量字段，如VK_DOLLAR表示“ $ ”键，修饰键如 VK_SHIFT 表示 Shift 键等。

-   keyRelease(int keycode)：此方法释放给定的键。例如，使用 keyPress(KeyEvent.VK_SHIFT ) 方法按下的 Shift 键需要使用keyRelease (KeyEvent.VK_SHIFT ) 方法释放。

### 在 Selenium 中使用 java 机器人类执行键盘事件练习

让我们讨论一个来自 Toolsqa 上已经可用的演示页面的示例，如“ [https://demoqa.com/keyboard-events/](https://demoqa.com/keyboard-events/) ”。

![机器人类键盘事件](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Robot%20Class%20Keyboard%20Events.png)

在这里，要上传任何文件，首先从桌面 Windows 弹出窗口的打开弹出窗口中选择文件。这反过来又需要输入文件名，例如本例中的D1.txt。

让我们了解如何使用 Robot 类方法输入文件名。

-   实例化机器人类

机器人robot = new Robot();

-   调用keyPress方法输入文字

robot.keyPress(<键码整数值>);

如果用户需要在网页的文本框中输入“ D1.txt ”，他/她可以使用 sendKeys 方法，只需调用一个方法 sendKeys(“ D1.txt ”) 就可以达到目的。但是，要使用 keyPress() 方法在桌面 Windows 弹出窗口中输入文本，需要为输入字符串的每个字符调用该方法，如keyPress(KeyEvent.VK_D)、keyPress(KeyEvent.VK_1)等。

让我们自动化以下场景：

1.  启动网络浏览器并启动我们的练习页面[https://demoqa.com/keyboard-events/](https://demoqa.com/keyboard-events/)
2.  单击“单击此处浏览”按钮
3.  按 Shift 键
4.  输入 d 将其键入为 D 作为修饰符 Shift 键按下
5.  释放 Shift 键
6.  输入文件名的剩余部分，即 1.txt 以将其显示为 D1.txt
7.  按回车键
8.  单击“上传”按钮并关闭警报
9.  关闭浏览器结束程序

前提条件：创建文件 C:\demo\D1.txt

以下是示例代码：

```java
package com.toolsqa.tutorials.actions;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RobotKeyboardDemo {

	public static void main(String[] args) throws InterruptedException, AWTException, IOException {
		
		System.setProperty("webdriver.gecko.driver","C:\\Selenium\\lib\\geckodriver-v0.24.0-win64\\geckodriver.exe");

		// Create a new instance of the Firefox driver
		WebDriver driver = new FirefoxDriver();
		String URL = "https://demoqa.com/keyboard-events/";
		
		//Start Browser
		driver.get(URL);
		
		//maximize browser
		driver.manage().window().maximize();
		Thread.sleep(2000);
		
		// This will click on Browse button
		WebElement webElement = driver.findElement(By.id("browseFile"));		
		//click  Browse button 
		webElement.sendKeys(Keys.ENTER);
		
		//Create object of Robot class
		Robot robot = new Robot();
		//Code to Enter D1.txt 
		//Press Shify key 
		robot.keyPress(KeyEvent.VK_SHIFT);
		//Press d , it gets typed as upper case D as Shift key is pressed
		robot.keyPress(KeyEvent.VK_D);
		//Release SHIFT key to release upper case effect
		robot.keyRelease(KeyEvent.VK_SHIFT);
		robot.keyPress(KeyEvent.VK_1);
		robot.keyPress(KeyEvent.VK_PERIOD);
		robot.keyPress(KeyEvent.VK_T);
		robot.keyPress(KeyEvent.VK_X);
		robot.keyPress(KeyEvent.VK_T);
		
		//Press ENTER to close the popup
        robot.keyPress(KeyEvent.VK_ENTER);  

        //Wait for 1 sec
        Thread.sleep(1000);
			 
		//This is just a verification part, accept alert
        webElement = driver.findElement(By.id("uploadButton"));
		
		webElement.click();				 
		WebDriverWait wait = new WebDriverWait(driver, 10);		 
		Alert myAlert = wait.until(ExpectedConditions.alertIsPresent());	       	 
		//Accept the Alert		 
		myAlert.accept();

		//Close the main window 
		driver.close();
	}

}
```

注意：即使 Robot 类指定为每个 keyPress 事件遵循 keyRelease，字母和数字对下一个语句没有任何副作用。因此，一般情况下，用户会跳过字母和数字的 keyRelease 事件。另一方面，所有修饰键(如 SHIFT、ALT 等)总是会对下一个语句产生副作用。因此，仍然必须为修改键的每个 keyPress 事件指定 keyRelease。

只需尝试注释掉robot.keyRelease ( KeyEvent.VK_SHIFT ); 在上面的示例代码中运行脚本。你会注意到文件名类型为“ D!.TXT ”。这是因为 SHIFT 键的按键效果被传递到下一个键入的文本并以大写形式键入它。

### 概括

最后，在本教程中，我们介绍了用于与 Selenium 脚本中的操作系统特定弹出窗口或应用程序交互的机器人键盘方法。

-   keyPress(int keycode)：按下给定键的方法。
-   keyRelease(int keycode)：释放给定键的方法。

正如 Robot 介绍教程中已经介绍的那样，Robot 类还提供了模拟鼠标操作的方法。我们将在下一个教程中详细介绍这些方法，即机器人类鼠标事件。