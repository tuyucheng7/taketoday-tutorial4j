在测试用例运行过程中，用户希望在控制台中记录一些信息。信息可以是任何细节，取决于目的。请记住，我们正在使用 Selenium 进行测试，我们需要帮助用户了解测试步骤或测试用例执行期间的任何失败的信息。在 Log4j 的帮助下，可以在 Selenium 测试用例执行期间启用日志记录，例如假设你在自动化测试脚本中遇到故障并且必须在系统中报告。报告错误所需的信息集是：

-   复制场景的完整测试步骤
-   问题，失败的描述或失败的测试用例的原因
-   开发人员详细调查问题的时间戳

Log4j 帮助我们在 Selenium Webdriver 中实现上述目标。当日志记录被明智地使用时，它可以证明是一个必不可少的工具。

## 在方法中记录

在测试用例中登录是一项非常繁琐的工作，迟早你会发现它乏味和烦人。此外，每个人都有自己编写日志消息的方式，并且消息的信息量和混淆度可能较低。那么为什么不让它通用呢。在方法内部写入日志消息是非常有用的方法，这样我们可以避免很多混乱，节省大量时间并保持一致性。

## 怎么做...

1) [下载](https://toolsqa.com/selenium-webdriver/download-log4j/) Log4j 的 JAR 文件并将 [Jars 添加](https://toolsqa.com/selenium-webdriver/add-log4j-jars/) 到你的项目库中。你可以从 [这里下载它。](https://logging.apache.org/)这就是关于使用 eclipse 配置 Apache POI 的全部内容。现在你已准备好编写测试。

1.  创建一个新的XML文件– log4j.xml并将其放在项目根文件夹下。

3) 将以下代码粘贴到log4j.xml文件中。

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

注意：粘贴代码后，请确保代码完全相同，因为从 HTML 复制可能会将某些符号 (") 更改为 (?)。

让我们以我们之前的Apache_POI_TC测试用例为例，并将日志消息放入它与之交互的每个方法和模块中。

1.  为此，我们需要创建一个静态Log类，以便我们可以在我们的任何项目类中访问它的 log 方法。日志类将如下所示：

```java
 package utility;

import org.apache.log4j.Logger;

 public class Log {

// Initialize Log4j logs

	 private static Logger Log = Logger.getLogger(Log.class.getName());//

 // This is to print log for the beginning of the test case, as we usually run so many test cases as a test suite

 public static void startTestCase(String sTestCaseName){

	Log.info("");

	Log.info("");

	Log.info("$$$$$$$$$$$$$$$$$$$$$                 "+sTestCaseName+ "       $$$$$$$$$$$$$$$$$$$$$$$$$");

	Log.info("");

	Log.info("");

	}

	//This is to print log for the ending of the test case

 public static void endTestCase(String sTestCaseName){

	Log.info("XXXXXXXXXXXXXXXXXXXXXXX             "+"-E---N---D-"+"             XXXXXXXXXXXXXXXXXXXXXX");

	Log.info("X");

	Log.info("X");

	Log.info("X");

	Log.info("X");

	}

	// Need to create these methods, so that they can be called  

 public static void info(String message) {

		Log.info(message);

		}

 public static void warn(String message) {

    Log.warn(message);

	}

 public static void error(String message) {

    Log.error(message);

	}

 public static void fatal(String message) {

    Log.fatal(message);

	}

 public static void debug(String message) {

    Log.debug(message);

	}

}
```

1.  在pageObject包的Home_Page类中插入日志消息。

```java
package pageObjects;

		import org.openqa.selenium.By;

		import org.openqa.selenium.WebDriver;

		import org.openqa.selenium.WebElement;

                import framework.utility.Log;

 	public class Home_Page {

			private static WebElement element = null;

		public static WebElement lnk_MyAccount(WebDriver driver){

			element = driver.findElement(By.id("account"));

			Log.info("My Account link element found");

			return element;

		}

		public static WebElement lnk_LogOut(WebDriver driver){

			element = driver.findElement(By.id("account_logout"));

			Log.info("Log Out link element found");

			return element;

		}

	}
```

1.  在pageObject包的LogIn_Page类中插入日志消息。

```java
 package pageObjects;

		import org.openqa.selenium.;

		import utility.Log;

	public class LogIn_Page {

			static WebElement element = null;

        public static WebElement txtbx_UserName(WebDriver driver){

            element = driver.findElement(By.id("log"));

			Log.info("Username text box found");

            return element;

            }

        public static WebElement txtbx_Password(WebDriver driver){

            element = driver.findElement(By.id("pwd"));

			Log.info("Password text box found");

            return element;

            }

        public static WebElement btn_LogIn(WebDriver driver){

            element = driver.findElement(By.id("login"));

			Log.info("Submit button found");

            return element;

            }

        }
```

7) 在appModule包的SignIn_Action类中插入日志消息。

```java
package appModules;

        import org.openqa.selenium.WebDriver;

        import pageObjects.Home_Page;

        import pageObjects.LogIn_Page;

        import utility.ExcelUtils;

        import utility.Log;

    public class SignIn_Action {

		public static void Execute(WebDriver driver) throws Exception{

			String sUserName = ExcelUtils.getCellData(1, 1);

			Log.info("Username picked from Excel is "+ sUserName );

			String sPassword = ExcelUtils.getCellData(1, 2);

			Log.info("Password picked from Excel is "+ sPassword );

			Home_Page.lnk_MyAccount(driver).click();

			Log.info("Click action performed on My Account link");

			LogIn_Page.txtbx_UserName(driver).sendKeys(sUserName);

			Log.info("Username entered in the Username text box");

			LogIn_Page.txtbx_Password(driver).sendKeys(sPassword);

			Log.info("Password entered in the Password text box");

			LogIn_Page.btn_LogIn(driver).click();

			Log.info("Click action performed on Submit button");

        }

}
```

1.  现在是将日志消息插入测试脚本的时候了，但在此之前创建一个[“新类”](https://toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)并通过右键单击“automationFramework”包并将其命名为Log4j_Logging_TC并选择 New > Class。

```java
package automationFramework;

		// Import Package Log4j.

		import org.apache.log4j.xml.DOMConfigurator;

		import java.util.concurrent.TimeUnit;

		import org.openqa.selenium.;

		import pageObjects.;

		import utility.;

		import appModules.;

	public class Log4j_Logging_TC {

			private static WebDriver driver = null;

        public static void main(String[] args) throws Exception {

			// Provide Log4j configuration settings

			DOMConfigurator.configure("log4j.xml");

			Log.startTestCase("Selenium_Test_001");

			ExcelUtils.setExcelFile(Constant.Path_TestData + Constant.File_TestData,"Sheet1");

			Log.info(" Excel sheet opened");

			driver = new FirefoxDriver();

			Log.info("New driver instantiated");

			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

			Log.info("Implicit wait applied on the driver for 10 seconds");

			driver.get(Constant.URL);

			Log.info("Web application launched");

			SignIn_Action.Execute(driver);

			System.out.println("Login Successfully, now it is the time to Log Off buddy.");

			Home_Page.lnk_LogOut(driver).click(); 

			Log.info("Click action is perfomred on Log Out link");

			driver.quit();

			Log.info("Browser closed");

			ExcelUtils.setCellData("Pass", 1, 3);

			Log.endTestCase("Selenium_Test_001");

	}

}
```

测试完成后，转到项目根文件夹并打开日志文件。你的日志文件将如下所示：

![Log4j-结果](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Log4j-Result.png)

你的项目浏览器窗口现在将如下所示。

![Log4j-窗口](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Log4j-Window.png)

注意：你仍然会在主测试脚本中看到一些日志记录，理想情况下必须避免或最小化。我这样做是为了让你明白。在接下来的章节中，你将看到如何避免登录主测试脚本。

下一章是关于[TestNG 框架](https://www.toolsqa.com/testng/testng-annotations/)的，这是 Selenium 自动化框架中最重要的一章。在此之前，我要求你使用 TestNG 访问第一个 [测试用例。](https://www.toolsqa.com/testng/testng-test/)