到目前为止，我们一直在类、函数和脚本中使用大量硬编码值。将测试数据硬编码到测试脚本中很容易。但有时固定的测试数据也用在这么多脚本中，如果它被更改，那么更新整个测试脚本(例如测试应用程序的URL )是一项艰巨的任务。它保持不变，但一旦你转移到其他环境，你需要在所有测试脚本中更改它。我们可以轻松地将URL放在测试脚本之外的文本文件或Excel 文件中，但是 Java 为我们提供了常量变量的特殊功能，它的工作方式与环境和全局变量完全相同QTP。

## 设置 Java 常量变量

1.  创建一个[“新类”](https://toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)文件，右键单击“配置” 包并选择 “新建”>“类” 并将其命名为 “常量”。
2.  为固定数据分配逻辑变量名称，例如 Url、用户名和密码。

### 常量类：

```java
package config;

public class Constants {

	//This is the list of System Variables
    //Declared as 'public', so that it can be used in other classes of this project
    //Declared as 'static', so that we do not need to instantiate a class object
    //Declared as 'final', so that the value of this variable can be changed
    // 'String' & 'int' are the data type for storing a type of value	
	public static final String URL = "https://www.store.demoqa.com";
	public static final String Path_TestData = "D://Tools QA Projects//trunk//Hybrid KeyWord Driven//src//dataEngine//DataEngine.xlsx";
	public static final String File_TestData = "DataEngine.xlsx";

	//List of Data Sheet Column Numbers
	public static final int Col_TestCaseID = 0;	
	public static final int Col_TestScenarioID =1 ;
	public static final int Col_ActionKeyword =3 ;

	//List of Data Engine Excel sheets
	public static final String Sheet_TestSteps = "Test Steps";

	// List of Test Data
	public static final String UserName = "testuser_3";
	public static final String Password = "Test@123";

}
```

常量 变量 被声明为'public static'，因此它们可以在其类之外和任何其他方法中调用，而无需实例化该类。

常量变量 被声明为'final'，因此在执行期间不能更改它们。

\3. Action Keyword类需要修改。

### 动作关键字类：

```java
package config;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class ActionKeywords {

		public static WebDriver driver;

	public void openBrowser(){		
		driver=new FirefoxDriver();	
		}

	public static void navigate(){	
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		//Constant Variable is used in place of URL
		//As it was declared as 'static', it can be used by referring the class name
		//Type the class name 'Constants' and press '.' dot, it will display all the memebers of the class Constants
		driver.get(Constants.URL);
		}

	public static void click_MyAccount(){
		driver.findElement(By.xpath(".//[@id='account']/a")).click();
		}

	public static void input_Username(){
		//Constant Variable is used in place of UserName
		driver.findElement(By.id("log")).sendKeys(Constants.UserName); 
		}

	public static void input_Password(){
		//Constant Variable is used in place of Password
		driver.findElement(By.id("pwd")).sendKeys(Constants.Password);
		}

	public static void click_Login(){
		driver.findElement(By.id("login")).click();
		}

	public static void waitFor() throws Exception{
		Thread.sleep(5000);
		}

	public static void click_Logout(){
			driver.findElement (By.xpath(".//[@id='account_logout']/a")).click();
		}

	public static void closeBrowser(){
			driver.quit();
		}

	}
```

1.  主驱动程序脚本也需要更改，需要用常量变量替换所有硬编码值。

### 驱动脚本：

```java
package executionEngine;

import java.lang.reflect.Method;

import config.ActionKeywords;
import config.Constants;
import utility.ExcelUtils;

public class DriverScript {
		public static ActionKeywords actionKeywords;
		public static String sActionKeyword;
		public static Method method[];

	public DriverScript() throws NoSuchMethodException, SecurityException{
		actionKeywords = new ActionKeywords();
		method = actionKeywords.getClass().getMethods();
		}

    public static void main(String[] args) throws Exception {

    	//Instead of hard coded Excel path, a Constant Variable is used
		String sPath = Constants.Path_TestData;   

    	//Here we are passing the Excel path and SheetName to connect with Excel file 
    	//Again a Constant Variable is used in place of Excel Sheet Name
    	ExcelUtils.setExcelFile(sPath, Constants.Sheet_TestSteps);

    	//Hard coded values are used for Excel row & columns for now
    	//In later chapters we will use these hard coded value much efficiently
    	//This is the loop for reading the values of the column 3 (Action Keyword) row by row
    	for (int iRow=1;iRow<=9;iRow++){
    		//Constant Variable is used in place of Column number
    		sActionKeyword = ExcelUtils.getCellData(iRow, Constants.Col_ActionKeyword);
    		execute_Actions();
    		}
    	}

     private static void execute_Actions() throws Exception {	
		for(int i=0;i<method.length;i++){		
			if(method[i].getName().equals(sActionKeyword)){
				method[i].invoke(actionKeywords);
				break;
				}
			}
		}
 }
```

注：以上未注释代码的任何解释，请参考前面的章节。

Eclipse 中的项目文件夹现在看起来像这样：

![关键词6](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Keyword-6.png)