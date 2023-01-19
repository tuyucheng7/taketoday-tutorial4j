## 使用 Log4j 日志记录编写测试用例

## 要遵循的步骤：

1.  创建一个新的 XML 文件 - log4j.xml 并将其放在项目根文件夹下并将以下代码粘贴到 log4j.xml 文件中

```java
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">

<log4j:configuration xmlns:log4j="https://jakarta.apache.org/log4j/" debug="false">

<appender name="fileAppender" class="org.apache.log4j.FileAppender">

<param name="Threshold" value="INFO" />

<param name="File" value="logfile.log"/>

<layout class="org.apache.log4j.PatternLayout">

<param name="ConversionPattern" value="%d %-5p [%c{1}] %m %n" />

</layout>

</appender>

<root>

<level value="INFO"/>

<appender-ref ref="fileAppender"/>

</root>

</log4j:configuration>
```

注意：粘贴代码后确保代码完全相同，因为从 HTML 复制可能会将某些符号(“)更改为(？)。2) 现在将登录代码包含到你的测试脚本中

```java
package automationFramework;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import org.apache.log4j.xml.DOMConfigurator;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.firefox.FirefoxDriver;

public class Log4j {

	private static WebDriver driver;

	private static Logger Log = Logger.getLogger(Log4j.class.getName());

	public static void main(String[] args) {

		DOMConfigurator.configure("log4j.xml");

		// Create a new instance of the Firefox driver

        driver = new FirefoxDriver();

        Log.info("New driver instantiated");

        //Put a Implicit wait, this means that any search for elements on the page could take the time the implicit wait is set for before throwing exception

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        Log.info("Implicit wait applied on the driver for 10 seconds");

        //Launch the Online Store Website

        driver.get("https://www.onlinestore.toolsqa.com");

        Log.info("Web application launched");

        // Find the element that's ID attribute is 'account'(My Account)

        driver.findElement(By.id("account")).click();

        Log.info("Click action performed on My Account link");

        // Find the element that's ID attribute is 'log' (Username)

        // Enter Username on the element found by above desc.

        driver.findElement(By.id("log")).sendKeys("testuser_1");

        Log.info("Username entered in the Username text box");

        // Find the element that's ID attribute is 'pwd' (Password)

        // Enter Password on the element found by the above desc.

        driver.findElement(By.id("pwd")).sendKeys("Test@123");

        Log.info("Password entered in the Password text box");

        // Now submit the form. WebDriver will find the form for us from the element

        driver.findElement(By.id("login")).click();

        Log.info("Click action performed on Submit button");

        // Print a Log In message to the screen

        System.out.println(" Login Successfully, now it is the time to Log Off buddy.");

         // Find the element that's ID attribute is 'account_logout' (Log Out)

        driver.findElement(By.id("account_logout"));

        Log.info("Click action performed on Log out link");

        // Close the driver

        driver.quit();

        Log.info("Browser closed");

	}

}
```

1.  检查输出文件“ logfile.txt ”。输出如下所示：

![Log4j-FTC](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Log4j-FTC.png)

![Log4j-FTC-1](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Log4j-FTC-1.png)