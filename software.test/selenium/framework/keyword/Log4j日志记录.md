在测试用例运行期间，用户希望在控制台中记录一些信息。信息可以是任何细节，取决于目的。请记住，我们正在使用 Selenium 进行测试，我们需要帮助用户了解测试步骤或测试用例执行期间的任何失败的信息。在Log4j的帮助下，可以在 Selenium 测试用例执行期间启用日志记录，例如假设你在自动化测试脚本中遇到故障并且必须在系统中报告。报告错误所需的信息集是：

-   复制场景的完整测试步骤
-   问题，失败的描述或失败的测试用例的原因
-   开发人员详细调查问题的时间戳

Log4j帮助我们在 Selenium Webdriver 中实现上述目标。当日志记录被明智地使用时，它可以证明是一个必不可少的工具。

## 在方法中记录

在测试用例中登录是一项非常乏味的工作，迟早你会发现它很无聊和烦人。此外，每个人都有自己编写日志消息的方式，并且消息的信息量和混淆度可能较低。那么为什么不让它通用呢。在方法中写入日志消息是一种非常有用的方法，可以避免很多混淆，节省大量时间并保持一致性。

## 第 1 步：设置 Log4j

1) [下载](https://toolsqa.com/selenium-webdriver/download-log4j/) Log4j 的 JAR 文件并将 [Jars 添加](https://toolsqa.com/selenium-webdriver/add-log4j-jars/) 到你的项目库中。你可以从 [这里下载它。](https://logging.apache.org/)

1.  创建一个新的 XML 文件—— “log4j.xml”并将其放在项目根文件夹下。

3) 将以下代码粘贴到log4j.xml 文件中。

```java
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

注意： 粘贴代码后确保代码完全相同，因为从 HTML 复制可能会将某些符号(“)更改为(？)。

## 第 2 步：设置静态日志类

1.  通过右键单击“实用程序”包并选择 “新建”> “类”，创建一个[“新类”](https://toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/) 文件并将其命名为“日志” 。
2.  这个日志类 的所有方法都是静态的，因此它的方法可以在任何类中访问。

### Log4j 日志类：

```java
package utility;

		import org.apache.log4j.Logger;

	public class Log {

		//Initialize Log4j logs
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

## 第 3 步：在 Action Keyword Class 中输入日志

### 动作关键字类：

```java
package config;

import java.util.concurrent.TimeUnit;
import static executionEngine.DriverScript.OR;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import utility.Log;

public class ActionKeywords {

		public static WebDriver driver;

	public static void openBrowser(String object){		
		Log.info("Opening Browser");
		driver=new FirefoxDriver();
		}

	public static void navigate(String object){	
		Log.info("Navigating to URL");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get(Constants.URL);
		}

	public static void click(String object){
		Log.info("Clicking on Webelement "+ object);
		driver.findElement(By.xpath(OR.getProperty(object))).click();
		}

	public static void input_UserName(String object){
		Log.info("Entering the text in UserName");
		driver.findElement(By.xpath(OR.getProperty(object))).sendKeys(Constants.UserName); 
		}

	public static void input_Password(String object){
		Log.info("Entering the text in Password");
		driver.findElement(By.xpath(OR.getProperty(object))).sendKeys(Constants.Password);
		}

	public static void waitFor(String object) throws Exception{
		Log.info("Wait for 5 seconds");
		Thread.sleep(5000);
		}

	public static void closeBrowser(String object){
		Log.info("Closing the browser");
		driver.quit();
		}

	}
```

## 第 4 步：在驱动程序脚本中初始化日志

1.  在所有类的方法中完成日志记录后，只需从主驱动程序脚本初始化日志。

### 驱动脚本：

```java
package executionEngine;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;
import org.apache.log4j.xml.DOMConfigurator;
import config.ActionKeywords;
import config.Constants;
import utility.ExcelUtils;
import utility.Log;

public class DriverScript {

	public static Properties OR;
	public static ActionKeywords actionKeywords;
	public static String sActionKeyword;
	public static String sPageObject;
	public static Method method[];

	public static int iTestStep;
	public static int iTestLastStep;
	public static String sTestCaseID;
	public static String sRunMode;

	public DriverScript() throws NoSuchMethodException, SecurityException{
		actionKeywords = new ActionKeywords();
		method = actionKeywords.getClass().getMethods();
	}

    public static void main(String[] args) throws Exception {
    	ExcelUtils.setExcelFile(Constants.Path_TestData);
    	
		//This is to start the Log4j logging in the test case
		DOMConfigurator.configure("log4j.xml");
		
    	String Path_OR = Constants.Path_OR;
		FileInputStream fs = new FileInputStream(Path_OR);
		OR= new Properties(System.getProperties());
		OR.load(fs);

		DriverScript startEngine = new DriverScript();
		startEngine.execute_TestCase();
    }

    private void execute_TestCase() throws Exception {
    	int iTotalTestCases = ExcelUtils.getRowCount(Constants.Sheet_TestCases);
		for(int iTestcase=1;iTestcase<=iTotalTestCases;iTestcase++){
			sTestCaseID = ExcelUtils.getCellData(iTestcase, Constants.Col_TestCaseID, Constants.Sheet_TestCases); 
			sRunMode = ExcelUtils.getCellData(iTestcase, Constants.Col_RunMode,Constants.Sheet_TestCases);
			if (sRunMode.equals("Yes")){
				iTestStep = ExcelUtils.getRowContains(sTestCaseID, Constants.Col_TestCaseID, Constants.Sheet_TestSteps);
				iTestLastStep = ExcelUtils.getTestStepsCount(Constants.Sheet_TestSteps, sTestCaseID, iTestStep);
				Log.startTestCase(sTestCaseID);
				
				for (;iTestStep<=iTestLastStep;iTestStep++){
		    		sActionKeyword = ExcelUtils.getCellData(iTestStep, Constants.Col_ActionKeyword,Constants.Sheet_TestSteps);
		    		sPageObject = ExcelUtils.getCellData(iTestStep, Constants.Col_PageObject, Constants.Sheet_TestSteps);
		    		execute_Actions();
		    		}
				Log.endTestCase(sTestCaseID);
				}
    		}
    	}

    private static void execute_Actions() throws Exception {

		for(int i=0;i<method.length;i++){
			if(method[i].getName().equals(sActionKeyword)){
				method[i].invoke(actionKeywords,sPageObject);
				break;
				}
			}
		}
 }
```

注： 以上非注释代码的任何解释，请参考前面的章节。

### 日志会产生这样的：

![关键词10](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Keyword-10.png)

### 现在项目看起来像这样：

![关键词11](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Keyword-11.png)