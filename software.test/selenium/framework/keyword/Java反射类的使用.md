在上一章中我们已经看到，要执行任何 操作，我们需要将从 Excel 工作表中获取的值与操作关键字类中每个方法的值进行比较。直到只有几种方法时，这种技术才能正常工作。但是想一想几乎每天都会在框架中添加新操作的场景。首先在“ActionKeyword”类中添加一个新方法然后添加该方法以比较“DriverEngine”测试的语句将是一项繁琐的任务。想想几个版本后 IF/ELSE 循环列表的大小。

## Java反射类的使用

Java 提供了在Refection Classes的帮助下克服这个问题的能力。 反射是在运行时处理Java 类的一种非常有用的方法，因为它可用于在运行时加载 Java 类、调用其方法或分析类。如果你对 Java 不太熟悉，我建议你简单地复制粘贴代码并开始使用它。否则最好谷歌“Java Reflection Classes”并阅读它。请记住它的实际需要，我们正在使用它在运行时创建一个类并在运行时分析Action Keyword类。

换句话说，让我再次告诉你它的必要性，这样就可以理解原因了。到目前为止，在框架中，每当在Action 关键字类中添加任何新方法时，都需要将新创建的方法放入主驱动程序脚本的if/else循环中。为了避免这种情况，需要使用Java Reflection类，以便在添加新方法时，该反射类会在运行时加载Action Keyword类的所有方法。

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
		driver.get("https://www.store.demoqa.com");
		}

	public static void click_MyAccount(){
		driver.findElement(By.xpath(".//[@id='account']/a")).click();
		}

	public static void input_Username(){
		driver.findElement(By.id("log")).sendKeys("testuser_3"); 
		}

	public static void input_Password(){
		driver.findElement(By.id("pwd")).sendKeys("Test@123");
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

注意： Action Keyword类没有变化，和上一章一样。

### 驱动脚本类：

```java
package executionEngine;

import java.lang.reflect.Method;
import config.ActionKeywords;
import utility.ExcelUtils;

public class DriverScript {
	//This is a class object, declared as 'public static'
	//So that it can be used outside the scope of main[] method
	public static ActionKeywords actionKeywords;
	public static String sActionKeyword;
	//This is reflection class object, declared as 'public static'
	//So that it can be used outside the scope of main[] method
	public static Method method[];

	//Here we are instantiating a new object of class 'ActionKeywords'
	public DriverScript() throws NoSuchMethodException, SecurityException{
		actionKeywords = new ActionKeywords();
		//This will load all the methods of the class 'ActionKeywords' in it.
                //It will be like array of method, use the break point here and do the watch
		method = actionKeywords.getClass().getMethods();
	}

    public static void main(String[] args) throws Exception {

    	//Declaring the path of the Excel file with the name of the Excel file
    	String sPath = "D://Tools QA Projects//trunk//Hybrid Keyword Driven//src//dataEngine//DataEngine.xlsx";

    	//Here we are passing the Excel path and SheetName to connect with the Excel file
        //This method was created in the last chapter of 'Set up Data Engine' 		
    	ExcelUtils.setExcelFile(sPath, "Test Steps");

    	//Hard coded values are used for Excel row & columns for now
    	//In later chapters we will use these hard coded value much efficiently
    	//This is the loop for reading the values of the column 3 (Action Keyword) row by row
		//It means this loop will execute all the steps mentioned for the test case in Test Steps sheet
    	for (int iRow = 1;iRow <= 9;iRow++){
		    //This to get the value of column Action Keyword from the excel
    		sActionKeyword = ExcelUtils.getCellData(iRow, 3);
            //A new separate method is created with the name 'execute_Actions'
			//You will find this method below of the this test
			//So this statement is doing nothing but calling that piece of code to execute
    		execute_Actions();
    		}
    	}
	
	//This method contains the code to perform some action
	//As it is completely different set of logic, which revolves around the action only,
	//It makes sense to keep it separate from the main driver script
	//This is to execute test step (Action)
    private static void execute_Actions() throws Exception {
		//This is a loop which will run for the number of actions in the Action Keyword class 
		//method variable contain all the method and method.length returns the total number of methods
		for(int i = 0;i < method.length;i++){
			//This is now comparing the method name with the ActionKeyword value got from excel
			if(method[i].getName().equals(sActionKeyword)){
				//In case of match found, it will execute the matched method
				method[i].invoke(actionKeywords);
				//Once any method is executed, this break statement will take the flow outside of for loop
				break;
				}
			}
		}
 }
```

上面的代码现在更加清晰和简单了。如果在Action 关键字类中添加了任何方法，驱动程序脚本将不会对其产生任何影响。它会自动考虑新创建的方法。