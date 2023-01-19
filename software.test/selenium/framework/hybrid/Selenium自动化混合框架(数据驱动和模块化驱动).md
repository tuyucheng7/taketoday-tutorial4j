# Selenium 自动化混合框架

(数据驱动和模块化驱动)初级

到目前为止，我希望你喜欢  “设置 Selenium 自动化框架的十个步骤”的旅程，并且你已经很好地理解了所有主题。让我们修改我们所涵盖的内容并对其进行练习。

我们在前面的章节中介绍了：

1)页面对象模型

2)对象存储库

3)模块化驱动技术

4) 函数参数

5)常量变量

6)数据驱动技术

7) Log4j 日志记录

8) TestNG 报告

9) 用户自定义函数

10) 异常处理

框架搭建差不多就结束了。我们已经涵盖了所有内容，剩下的就是扩展测试以涵盖端到端 场景、确定测试用例的优先级、准备测试套件和测试用例分组。

在进入下一个级别之前，我想对我们到目前为止在[演示应用程序中学到的内容进行练习。](https://demoqa.com/)到目前为止，我们所涵盖的只是登录功能。我希望你自动化端到端流程，涵盖以下步骤：

1) 登录到演示应用程序[在线商店](https://shop.demoqa.com/)

2) 从顶部菜单中选择产品类别

3)选择商品并加入购物车

4) 前往付款详情页面并完成订单

5) 从最终确认页面验证详细信息

Selenium 自动化混合框架

这个框架处于非常初级的水平，非常容易理解。这实现了页面对象模型技术、数据驱动技术、模块化驱动技术、Log4j 日志记录、TestNG 报告和 TestNG Reporter 日志。

请从这里下载代码 [Selenium Automation Hybrid Framework](https://toolsqa.com/selenium-webdriver/automation-framework-introduction/)

或者请阅读下面的代码。

# 测试数据表

![测试数据](https://www.toolsqa.com/gallery/selnium%20webdriver/1.TestData.png)

# 测试用例包

## 测试用例

```java
package testCases;

import org.apache.log4j.xml.DOMConfigurator;

import org.openqa.selenium.WebDriver;

import org.testng.annotations.AfterMethod;

import org.testng.annotations.BeforeMethod;

import org.testng.annotations.Test;

import pageObjects.BaseClass;

import utility.Constant;

import utility.ExcelUtils;

import utility.Log;

import utility.Utils;

import appModules.CheckOut_Action;

import appModules.Confirmation_Action;

import appModules.PaymentDetails_Action;

import appModules.ProductSelect_Action;

import appModules.SignIn_Action;

import appModules.Verification_Action;

public class Framework_001{

	public WebDriver driver;

	private String sTestCaseName;

	private int iTestCaseRow;

  // Following TestNg Test case pattern, and divided a Test case in to three different part.

  // In Before Method, your code will always be the same for every other test case.

  // In other way before method is your prerequisites of your main Test Case	

  @BeforeMethod

  public void beforeMethod() throws Exception {

	    // Configuring Log4j logs, please see the following posts to learn about Log4j Logging

	    // http://toolsqa.com/selenium-webdriver/test-case-with-log4j/

	    // http://toolsqa.com/selenium-webdriver/log4j-logging/

	  	DOMConfigurator.configure("log4j.xml");

	  	// Getting the Test Case name, as it will going to use in so many places

	  	// The main use is to get the TestCase row from the Test Data Excel sheet

	  	sTestCaseName = this.toString();

	  	// From above method we get long test case name including package and class name etc.

	  	// The below method will refine your test case name, exactly the name use have used

	  	sTestCaseName = Utils.getTestCaseName(this.toString());

	  	// Start printing the logs and printing the Test Case name

		Log.startTestCase(sTestCaseName);

		// Setting up the Test Data Excel file using Constants variables

		// For Constant Variables please see http://toolsqa.com/selenium-webdriver/constant-variables/

		// For setting up Excel for Data driven testing, please see http://toolsqa.com/selenium-webdriver/data-driven-testing-excel-poi/

		ExcelUtils.setExcelFile(Constant.Path_TestData + Constant.File_TestData,"Sheet1");

		// Fetching the Test Case row number from the Test Data Sheet

		// This row number will be feed to so many functions, to get the relevant data from the Test Data sheet 

		iTestCaseRow = ExcelUtils.getRowContains(sTestCaseName,Constant.Col_TestCaseName);

		// Launching the browser, this will take the Browser Type from Test Data Sheet 

		driver = Utils.OpenBrowser(iTestCaseRow);

		// Initializing the Base Class for Selenium driver

		// Now we do need to provide the Selenium driver to any of the Page classes or Module Actions

		// Will soon write a post on Base Class

		new BaseClass(driver);  

        }

  // This is the starting of the Main Test Case

  @Test

  public void main() throws Exception {

	  // Every exception thrown from any class or method, will be catch here and will be taken care off

	  // For Exception handling please see http://toolsqa.com/selenium-webdriver/exception-handling-selenium-webdriver/

	  try{

		// Here we are calling the SignIN Action and passing argument (iTestCaseRow)

		// This is called Modularization, when we club series of actions in to one Module

		// For Modular Driven Framework, please see http://toolsqa.com/selenium-webdriver/modular-driven/  

		SignIn_Action.Execute(iTestCaseRow);

		// This action is to select the Product category from the Top Navigation of the Home Page

		// I have converted this in to a module, as there are so many logics involved in to this selection

		// And it is always a best idea to keep your logics separate from your test case

		ProductSelect_Action.productType(iTestCaseRow);

		// This action is to select the Product from the Product Listing Page

		// I have again converted this in to a module, as there are so many logics involved in to this selection

		ProductSelect_Action.productNumber(iTestCaseRow);

		// This is to assigning Product Name & Price to the variables from the Check Out page, so that it can be matched later for verification

		CheckOut_Action.Execute();

		// Here we are calling the Payment Details Action and passing argument (iTestCaseRow)

		// This action will provide all the personal detail and payment detail on the page and complete the payment for the selected product

		PaymentDetails_Action.execute(iTestCaseRow);

		// This is to assigning Product Name & Price to the variables from the Confirmation page, so that it can be matched later for verification

		Confirmation_Action.Execute();

		// This is to match the Product Name & Price we have stored in variables of Checkout & Confirmation page 

		Verification_Action.Execute();

		// Now your test is about to finish but before that you need to take decision to Pass your test or Fail

		// For selenium your test is pass, as you do not face any exception and you come to the end or you test did not stop anywhere

		// But for you it can be fail, if any of your verification is failed

		// This is to check that if any of your verification during the execution is failed

		if(BaseClass.bResult==true){

			// If the value of boolean variable is True, then your test is complete pass and do this

			ExcelUtils.setCellData("Pass", iTestCaseRow, Constant.Col_Result);

		}else{

			// If the value of boolean variable is False, then your test is fail, and you like to report it accordingly

			// This is to throw exception in case of fail test, this exception will be caught by catch block below

			throw new Exception("Test Case Failed because of Verification");

		}

	  // Below are the steps you may like to perform in case of failed test or any exception faced before ending your test 

	  }catch (Exception e){

		  // If in case you got any exception during the test, it will mark your test as Fail in the test result sheet

		  ExcelUtils.setCellData("Fail", iTestCaseRow, Constant.Col_Result);

		  // If the exception is in between the test, bcoz of any element not found or anything, this will take a screen shot

		  Utils.takeScreenshot(driver, sTestCaseName);

		  // This will print the error log message

		  Log.error(e.getMessage());

		  // Again throwing the exception to fail the test completely in the TestNG results

		  throw (e);

	  }

  }

  // Its time to close the finish the test case		

  @AfterMethod

  public void afterMethod() {

	    // Printing beautiful logs to end the test case

	    Log.endTestCase(sTestCaseName);

	    // Closing the opened driver

	    driver.close();

  		}



}
```

# 应用模块包

## 登录操作

```java
package appModules;

import org.testng.Reporter;

import pageObjects.Home_Page;

import pageObjects.LogIn_Page;

import utility.Constant;

import utility.ExcelUtils;

import utility.Log;

import utility.Utils;

    // This is called Modularization, when we club series of actions in to one Module

	// For Modular Driven Framework, please see http://toolsqa.com/selenium-webdriver/modular-driven/   

    public class SignIn_Action {

    	// iTestcaseRow is the row number of our Testcase name in the Test Data sheet

    	// iTestcaseRow is passed as an Argument to this method, so that it can used inside this method

    	// For use of Functions & Parameters, please see http://toolsqa.com/selenium-webdriver/function-parameters/

        public static void Execute(int iTestCaseRow) throws Exception{

        	// Clicking on the My Account link on the Home Page

        	Home_Page.lnk_MyAccount().click();

        	Log.info("Click action is perfromed on My Account link" );

        	// Storing the UserName in to a String variable and Getting the UserName from Test Data Excel sheet

        	// iTestcaseRow is the row number of our Testcase name in the Test Data sheet

        	// Constant.Col_UserName is the column number for UserName column in the Test Data sheet

        	// Please see the Constant class in the Utility Package

        	// For Use of Constant Variables, please see http://toolsqa.com/selenium-webdriver/constant-variables/

        	String sUserName = ExcelUtils.getCellData(iTestCaseRow, Constant.Col_UserName);

        	// Here we are sending the UserName string to the UserName Textbox on the LogIN Page

        	// This is call Page Object Model (POM)

        	// For use of POM, please see http://toolsqa.com/selenium-webdriver/page-object-model/

            LogIn_Page.txtbx_UserName().sendKeys(sUserName);

            // Printing the logs for what we have just performed

            Log.info(sUserName+" is entered in UserName text box" );

            String sPassword = ExcelUtils.getCellData(iTestCaseRow, Constant.Col_Password);

            LogIn_Page.txtbx_Password().sendKeys(sPassword);

            Log.info(sPassword+" is entered in Password text box" );

            LogIn_Page.btn_LogIn().click();

            Log.info("Click action is performed on Submit button");

            // I noticed in few runs that Selenium is trying to perform the next action before the complete Page load

            // So I have decided to put a wait on the Logout link element

            // Now it will wait 10 secs separately before jumping out to next step

            Utils.waitForElement(Home_Page.lnk_LogOut());

            // This is another type of logging, with the help of TestNg Reporter log

            // This has to be very carefully used, you should only print the very important message in to this

            // This will populate the logs in the TestNG HTML reports

            // I have used this Reporter log just once in this whole module 

            Reporter.log("SignIn Action is successfully perfomred");

        }

    }
```

## 产品选择行动

```java
package appModules;

import pageObjects.Home_Page;

import pageObjects.ProductListing_Page;

import utility.Constant;

import utility.ExcelUtils;

import utility.Log;

// This is called Modularization, when we club series of actions in to one Module

// For Modular Driven Framework, please see http://toolsqa.com/selenium-webdriver/modular-driven/ 

public class ProductSelect_Action {

	// iTestcaseRow is the row number of our Testcase name in the Test Data sheet

	// iTestcaseRow is passed as an Argument to this method, so that it can used inside this method

	// For use of Functions & Parameters, please see http://toolsqa.com/selenium-webdriver/function-parameters/

	public static void productType(int iTestCaseRow) throws Exception{

		try{

        	// iTestcaseRow is the row number of our Testcase name in the Test Data sheet

        	// Constant.Col_ProductType is the column number for Product Type column in the Test Data sheet

        	// Please see the Constant class in the Utility Package

        	// For Use of Constant Variables, please see http://toolsqa.com/selenium-webdriver/constant-variables/

			// If condition will check that if the Excel value for the Product Type is Accessories, then do this

			if("Accessories".equals(ExcelUtils.getCellData(iTestCaseRow, Constant.Col_ProductType))){

				// Selecting the link Accessories from Home Page under Top Navigation

				// This is call Page Object Model (POM)

	        	// For use of POM, please see http://toolsqa.com/selenium-webdriver/page-object-model/

				Home_Page.TopNavigation.Product_Type.Accessories();

	            // Printing the logs

				Log.info("Product Type Accessories is selected from the Top menu");

			}

			// If the Excel value for the Product Type is iMacs, then do this

			if("iMacs".equals(ExcelUtils.getCellData(iTestCaseRow, Constant.Col_ProductType))){

				Home_Page.TopNavigation.Product_Type.iMacs();

				Log.info("Product Type iMacs is selected from the Top menu");

			}

			// If the Excel value for the Product Type is iPads, then do this

			if("iPads".equals(ExcelUtils.getCellData(iTestCaseRow, Constant.Col_ProductType))){

				Home_Page.TopNavigation.Product_Type.iPads();

				Log.info("Product Type iPads is selected from the Top menu");

			}

			// If the Excel value for the Product Type is iPhones, then do this

			if("iPhones".equals(ExcelUtils.getCellData(iTestCaseRow, Constant.Col_ProductType))){

				Home_Page.TopNavigation.Product_Type.iPhones();

				Log.info("Product Type iPhones is selected from the Top menu");

			}

			// If the Excel value for the Product Type is null, then do this

			if("".equals(ExcelUtils.getCellData(iTestCaseRow, Constant.Col_ProductType))){

				Log.warn("Excel value for Product Type is Blank");

			}

		// Every exception thrown from any class or method, will be catch here and will be taken care off

		// For Exception handling please see http://toolsqa.com/selenium-webdriver/exception-handling-selenium-webdriver/

	    }catch(Exception e){

			// Here I have used this as just for the sake of an example

			// I am just catching the Exception and again throwing it back to the Main testcase, without handling it

	    	// You may like to print some information here, in case of exception

	    	throw(e);

			}

		}

	// iTestcaseRow is the row number of our Testcase name in the Test Data sheet

	// iTestcaseRow is passed as an Argument to this method, so that it can used inside this method

	// For use of Functions & Parameters, please see http://toolsqa.com/selenium-webdriver/function-parameters/

	public static void productNumber(int iTestCaseRow) throws Exception{

		try{

			// iTestcaseRow is the row number of our Testcase name in the Test Data sheet

        	// Constant.Col_ProductNumber is the column number for Product Number column in the Test Data sheet

        	// Please see the Constant class in the Utility Package

        	// For Use of Constant Variables, please see http://toolsqa.com/selenium-webdriver/constant-variables/

			// If condition will check that if the Excel value for the Product Number is "Product 1", then do this

			if("Product 1".equals(ExcelUtils.getCellData(iTestCaseRow, Constant.Col_ProductNumber))){

				// Clicking on the Add to Cart button for the Product 1

				// This is call Page Object Model (POM)

	        	// For use of POM, please see http://toolsqa.com/selenium-webdriver/page-object-model/

				ProductListing_Page.Product_1.btn_AddToCart().click();

				// Printing logs for the performed action

				Log.info("Product 1 is selected from the Product listing page");

			}

			// If the Excel value for the Product Number is "Product 2", then do this

			if("Product 2".equals(ExcelUtils.getCellData(iTestCaseRow, Constant.Col_ProductNumber))){

				ProductListing_Page.Product_2.btn_AddToCart().click();

				Log.info("Product 2 is selected from the Product listing page");

			}

			/// If the Excel value for the Product Type is null, then do this

			if("".equals(ExcelUtils.getCellData(iTestCaseRow, Constant.Col_ProductNumber))){

				Log.warn("Excel value for Product Number is Blank");

			}

			// Clicking on the "Go to Cart" button on the Pop Up Box

			ProductListing_Page.PopUpAddToCart.btn_GoToCart().click();

		 // Every exception thrown from any class or method, will be catch here and will be taken care off

		 // For Exception handling please see http://toolsqa.com/selenium-webdriver/exception-handling-selenium-webdriver/

		 }catch(Exception e){

			// Here I have used this as just for the sake of an example

			// I am just catching the Exception and again throwing it back to the Main testcase, without handling it

		    // You may like to print some information here, in case of exception

			throw(e);

			}

		}

	}
```

## 签出操作

```java
package appModules;

import org.testng.Reporter;

import pageObjects.BaseClass;

import pageObjects.CheckOut_Page;

// This is called Modularization, when we club series of actions in to one Module

// For Modular Driven Framework, please see http://toolsqa.com/selenium-webdriver/modular-driven/ 

public class CheckOut_Action {

	 // I could have created a Function for it but I keep calculations in Functions and test steps in Module Actions

	 // It could have been avoided and simply put these steps in Test Case, it depends totally on you, everybody has their own choice

	 public static void Execute() throws Exception{

		// This is to get the Product name on the Check Out page with using getText() method 

		// CheckOut_Page.sProductName is a static variable and can be used anywhere with its class name

		// Once some text is stored in this variable can be used later in any other class 

     	CheckOut_Page.sProductName=CheckOut_Page.txt_ProductName().getText();

     	// This is all about Verification checks, these does not stop your execution but simply report fail at the end

     	// This is to check that if the value in the variable sProductName is not null, then do this

     	if(!"".equals(CheckOut_Page.sProductName)){

     		// Here I have put a verification check on the Product Name, if it is displayed my verification will pass

     		Reporter.log("Verification Passed for Product Name on Check Out page.");

     	}else{

     		// If it not displayed then the verification check is failed

     		Reporter.log("Verification Failed for Product Name on Check Out page.");

     		// If the above verification gets failed then I have to report this to my test and fail the test accordingly

     		// To achieve this, I have initialized this variable of Base class at the start of my test with value true in it

     		// At the end of my test, i will match the value, if it will be false then I will fail the test, else the test will be pass

     		BaseClass.bResult=false;

     	}

     	CheckOut_Page.sProductPrice= CheckOut_Page.txt_ProductPrice().getText();

     	if(!"".equals(CheckOut_Page.sProductPrice)){

     		Reporter.log("Verification Passed for Product Price on Check Out page.");

     	}else{

     		Reporter.log("Verification Failed for Product Price on Check Out page.");

     		BaseClass.bResult=false;

     	}

     	// Clicking on the Continue button on the Check Out page

     	CheckOut_Page.btn_Continue().click();

	 }

}
```

## 付款详情操作

```java
package appModules;

import org.testng.Reporter;

import pageObjects.PaymentDetails_Page;

import utility.Constant;

import utility.ExcelUtils;

import utility.Log;

// This is called Modularization, when we club series of actions in to one Module

// For Modular Driven Framework, please see http://toolsqa.com/selenium-webdriver/modular-driven/ 

public class PaymentDetails_Action {

	// iTestcaseRow is the row number of our Testcase name in the Test Data sheet

	// iTestcaseRow is passed as an Argument to this method, so that it can used inside this method

	// For use of Functions & Parameters, please see http://toolsqa.com/selenium-webdriver/function-parameters/

	public static void execute(int iTestCaseRow) throws Exception{

		try{

        	// Storing the Email in to a String variable and Getting the Email address from Test Data Excel sheet

        	// iTestcaseRow is the row number of our Testcase name in the Test Data sheet

        	// Constant.Col_Email is the column number for Email column in the Test Data sheet

        	// Please see the Constant class in the Utility Package

        	// For Use of Constant Variables, please see http://toolsqa.com/selenium-webdriver/constant-variables/

			String sEmail = ExcelUtils.getCellData(iTestCaseRow, Constant.Col_Email);

			// Clearing the pre-populated details on this field

			PaymentDetails_Page.txt_Email().clear();

			// Here we are sending the Email string to the Email Textbox on the Payment Detail Page

        	// This is call Page Object Model (POM)

        	// For use of POM, please see http://toolsqa.com/selenium-webdriver/page-object-model/

			PaymentDetails_Page.txt_Email().sendKeys(sEmail);

			// Printing the logs for what we have just performed

			Log.info(sEmail +" is entered as First Name on the Payment detail page");

			String sFirstName = ExcelUtils.getCellData(iTestCaseRow, Constant.Col_FirstName);

			PaymentDetails_Page.txt_FirstName().clear();

			PaymentDetails_Page.txt_FirstName().sendKeys(sFirstName);

			Log.info(sFirstName +" is entered as First Name on the Payment detail page");

			String sLastName = ExcelUtils.getCellData(iTestCaseRow, Constant.Col_LastName);

			PaymentDetails_Page.txt_LastName().clear();

			PaymentDetails_Page.txt_LastName().sendKeys(sLastName);

			Log.info(sLastName +" is entered as Last Name on the Payment detail page");

			String sAddress = ExcelUtils.getCellData(iTestCaseRow, Constant.Col_Address);

			PaymentDetails_Page.txt_Address().clear();

			PaymentDetails_Page.txt_Address().sendKeys(sAddress);

			Log.info(sAddress +" is entered as Address on the Payment detail page");

			String sCity = ExcelUtils.getCellData(iTestCaseRow, Constant.Col_City);

			PaymentDetails_Page.txt_City().clear();

			PaymentDetails_Page.txt_City().sendKeys(sCity);

			Log.info(sCity +" is entered as City on the Payment detail page");

			String sCountry = ExcelUtils.getCellData(iTestCaseRow, Constant.Col_Country);

			PaymentDetails_Page.drpdwn_Country(sCountry);

			Log.info(sCountry +" is Selected as Country on the Payment detail page");

			String sPhone = ExcelUtils.getCellData(iTestCaseRow, Constant.Col_Phone);

			PaymentDetails_Page.txt_Phone().clear();

			PaymentDetails_Page.txt_Phone().sendKeys(sPhone);

			Log.info(sPhone +" is entered as Phone on the Payment detail page");

			// This is to check that if the Check box for "Same as Billing address" is not already checked

			if(!PaymentDetails_Page.chkbx_SameAsBillingAdd().isSelected()){

				// It it is unchecked, then check the check box

				PaymentDetails_Page.chkbx_SameAsBillingAdd().click();

				Log.info("Same as Billing address check box is selected on the Payment detail page");

			}

			// Clicking on the Purchase button to complete the payment

			PaymentDetails_Page.btn_Purchase().click();

			Log.info("Click action is performed on Purchase button on the Payment detail page");

            // This is another type of logging, with the help of TestNg Reporter log

            // This has to be very carefully used, you should only print the very important message in to this

            // This will populate the logs in the TestNG HTML reports

            // I have used this Reporter log just once in this whole module 

			Reporter.log("Payment is successfully perfromed for the purchased product.");

	    }catch(Exception e){

			throw(e);

			}

		}

}
```

## 确认页面操作

```java
package appModules;

import org.testng.Reporter;

import pageObjects.BaseClass;

import pageObjects.Confirmation_Page;

// This is called Modularization, when we club series of actions in to one Module

// For Modular Driven Framework, please see http://toolsqa.com/selenium-webdriver/modular-driven/ 

public class Confirmation_Action {

	 // I could have created a Function for it but I keep calculations in Functions and test steps in Module Actions

	 // It could have been avoided and simply put these steps in Test Case, it depends totally on you, everybody has their own choice

	 public static void Execute() throws Exception{

		    // This is to get the Product name on the Confirmation page with using getText() method 

			// Confirmation_Page.sProductName is a static variable and can be used anywhere with its class name

			// Once some text is stored in this variable can be used later in any other class 

	     	Confirmation_Page.sProductName=Confirmation_Page.txt_ProductName().getText();

	        // This is all about Verification checks, these does not stop your execution but simply report fail at the end

	     	// This is to check that if the value in the variable sProductName is not null, then do this

	     	if(!"".equals(Confirmation_Page.sProductName)){

	     	    // Here I have put a verification check on the Product Name, if it is displayed my verification will pass

	     		Reporter.log("Verification Passed for Product Name on Confirmation page");

	     	}else{

	     	    // If it not displayed then the verification check is failed

	     		Reporter.log("Verification Failed for Product Name on Confirmation page");

	     		// If the above verification gets failed then I have to report this to my test and fail the test accordingly

	     		// To achieve this, I have initialized this variable of Base class at the start of my test with value true in it

	     		// At the end of my test, i will match the value, if it will be false then I will fail the test, else the test will be pass

	     		BaseClass.bResult=false;

	     	}

	     	Confirmation_Page.sProductPrice= Confirmation_Page.txt_ProductPrice().getText();

	     	if(!"".equals(Confirmation_Page.sProductPrice)){

	     		Reporter.log("Verification Passed for Product Price on Confirmation page");

	     	}else{

	     		Reporter.log("Verification Failed for Product Price on Confirmation page");

	     		BaseClass.bResult=false;

	     	}

		 }

}
```

## 验证操作

```java
package appModules;

import org.testng.Reporter;

import pageObjects.BaseClass;

import pageObjects.CheckOut_Page;

import pageObjects.Confirmation_Page;

// This is called Modularization, when we club series of actions in to one Module

// For Modular Driven Framework, please see http://toolsqa.com/selenium-webdriver/modular-driven/ 

public class Verification_Action {

	 // I could have created a Function for it but I keep calculations in Functions and test steps in Module Actions

	 // It could have been avoided and simply put these steps in Test Case, it depends totally on you, everybody has their own choice

	public static void Execute() throws Exception{

		// This is to check that if the Product Name stored from Checkout & Confirmation page is same 

		// These are static variables, see how easy is to use them in your test

		if(CheckOut_Page.sProductName.equals(Confirmation_Page.sProductName)){

			// Here I have put a verification check on the Product Name, if it is matched, my verification will pass

			Reporter.log("Verification Passed for Product Name");

		}else{

			// If it not matched then the verification check is failed

			Reporter.log("Verification Failed for Product Name");

     		// If the above verification gets failed then I have to report this to my test and fail the test accordingly

     		// To achieve this, I have initialized this variable of Base class at the start of my test with value true in it

     		// At the end of my test, i will match the value, if it will be false then I will fail the test, else the test will be pass

			BaseClass.bResult=false;

		}

		if(CheckOut_Page.sProductPrice.equals(Confirmation_Page.sProductPrice)){

			Reporter.log("Verification Passed for Product Price");

		}else{

			Reporter.log("Verification Failed for Product Price");

			BaseClass.bResult=false;

		}

	}

}
```

# 实用程序包

## 常量变量类

```java
package utility;

public class Constant {

	    public static final String URL = "https://www.store.demoqa.com";

	    public static final String Username = "testuser_1";

	    public static final String Password ="Test@123";

		public static final String Path_TestData = "D://ToolsQA//OnlineStore//src//testData//";

		public static final String File_TestData = "TestData.xlsx";

		//Test Data Sheet Columns

		public static final int Col_TestCaseName = 0;	

		public static final int Col_UserName =1 ;

		public static final int Col_Password = 2;

		public static final int Col_Browser = 3;

		public static final int Col_ProductType = 4;

		public static final int Col_ProductNumber = 5;

		public static final int Col_FirstName = 6;

		public static final int Col_LastName = 7;

		public static final int Col_Address = 8;

		public static final int Col_City = 9;

		public static final int Col_Country = 10;

		public static final int Col_Phone = 11;

		public static final int Col_Email = 12;

		public static final int Col_Result = 13;

		public static final String Path_ScreenShot = "D://ToolsQA//OnlineStore//src//Screenshots//";

	}
```

## Excel 实用程序类

```java
package utility;

            import java.io.FileInputStream;

import java.io.FileOutputStream;

import org.apache.poi.xssf.usermodel.XSSFCell;

import org.apache.poi.xssf.usermodel.XSSFRow;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

    public class ExcelUtils {

                private static XSSFSheet ExcelWSheet;

                private static XSSFWorkbook ExcelWBook;

                private static XSSFCell Cell;

                private static XSSFRow Row;

            //This method is to set the File path and to open the Excel file, Pass Excel Path and Sheetname as Arguments to this method

            public static void setExcelFile(String Path,String SheetName) throws Exception {

                   try {

                       // Open the Excel file

                    FileInputStream ExcelFile = new FileInputStream(Path);

                    // Access the required test data sheet

                    ExcelWBook = new XSSFWorkbook(ExcelFile);

                    ExcelWSheet = ExcelWBook.getSheet(SheetName);

                    Log.info("Excel sheet opened");

                    } catch (Exception e){

                        throw (e);

                    }

            }

            //This method is to read the test data from the Excel cell, in this we are passing parameters as Row num and Col num

            public static String getCellData(int RowNum, int ColNum) throws Exception{

                   try{

                	  Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);

                      String CellData = Cell.getStringCellValue();

                      return CellData;

                      }catch (Exception e){

                        return"";

                      }

            }

            //This method is to write in the Excel cell, Row num and Col num are the parameters

            @SuppressWarnings("static-access")

			public static void setCellData(String Result,  int RowNum, int ColNum) throws Exception    {

                   try{

                      Row  = ExcelWSheet.getRow(RowNum);

                    Cell = Row.getCell(ColNum, Row.RETURN_BLANK_AS_NULL);

                    if (Cell == null) {

                        Cell = Row.createCell(ColNum);

                        Cell.setCellValue(Result);

                        } else {

                            Cell.setCellValue(Result);

                        }

          // Constant variables Test Data path and Test Data file name

                          FileOutputStream fileOut = new FileOutputStream(Constant.Path_TestData + Constant.File_TestData);

                          ExcelWBook.write(fileOut);

                          fileOut.flush();

                        fileOut.close();

                        }catch(Exception e){

                            throw (e);

                    }

                }

        	public static int getRowContains(String sTestCaseName, int colNum) throws Exception{

        		int i;

        		try {

        			int rowCount = ExcelUtils.getRowUsed();

        			for ( i=0 ; i<rowCount; i++){

        				if  (ExcelUtils.getCellData(i,colNum).equalsIgnoreCase(sTestCaseName)){

        					break;

        				}

        			}

        			return i;

        				}catch (Exception e){

        			Log.error("Class ExcelUtil | Method getRowContains | Exception desc : " + e.getMessage());

        			throw(e);

        			}

        		}

        	public static int getRowUsed() throws Exception {

        		try{

        			int RowCount = ExcelWSheet.getLastRowNum();

        			Log.info("Total number of Row used return as < " + RowCount + " >.");		

        			return RowCount;

        		}catch (Exception e){

        			Log.error("Class ExcelUtil | Method getRowUsed | Exception desc : "+e.getMessage());

        			System.out.println(e.getMessage());

        			throw (e);

        		}

        	}

    }
```

## 日志类

```java
 package utility;

import org.apache.log4j.Logger;

public class Log {

// Initialize Log4j logs

     private static Logger Log = Logger.getLogger(Log.class.getName()); 

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

## 实用类

```java
package utility;

import java.io.File;

import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.By;

import org.openqa.selenium.OutputType;

import org.openqa.selenium.TakesScreenshot;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.firefox.FirefoxDriver;

import org.openqa.selenium.interactions.Actions;

import org.openqa.selenium.support.ui.ExpectedConditions;

import org.openqa.selenium.support.ui.WebDriverWait;

public class Utils {

		public static WebDriver driver = null;

	public static WebDriver OpenBrowser(int iTestCaseRow) throws Exception{

		String sBrowserName;

		try{

		sBrowserName = ExcelUtils.getCellData(iTestCaseRow, Constant.Col_Browser);

		if(sBrowserName.equals("Mozilla")){

			driver = new FirefoxDriver();

			Log.info("New driver instantiated");

		    driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

		    Log.info("Implicit wait applied on the driver for 10 seconds");

		    driver.get(Constant.URL);

		    Log.info("Web application launched successfully");

			}

		}catch (Exception e){

			Log.error("Class Utils | Method OpenBrowser | Exception desc : "+e.getMessage());

		}

		return driver;

	}

	public static String getTestCaseName(String sTestCase)throws Exception{

		String value = sTestCase;

		try{

			int posi = value.indexOf("@");

			value = value.substring(0, posi);

			posi = value.lastIndexOf(".");	

			value = value.substring(posi + 1);

			return value;

				}catch (Exception e){

			Log.error("Class Utils | Method getTestCaseName | Exception desc : "+e.getMessage());

			throw (e);

					}

			}

	 public static void mouseHoverAction(WebElement mainElement, String subElement){

		 Actions action = new Actions(driver);

         action.moveToElement(mainElement).perform();

         if(subElement.equals("Accessories")){

        	 action.moveToElement(driver.findElement(By.linkText("Accessories")));

        	 Log.info("Accessories link is found under Product Category");

         }

         if(subElement.equals("iMacs")){

        	 action.moveToElement(driver.findElement(By.linkText("iMacs")));

        	 Log.info("iMacs link is found under Product Category");

         }

         if(subElement.equals("iPads")){

        	 action.moveToElement(driver.findElement(By.linkText("iPads")));

        	 Log.info("iPads link is found under Product Category");

         }

         if(subElement.equals("iPhones")){

        	 action.moveToElement(driver.findElement(By.linkText("iPhones")));

        	 Log.info("iPhones link is found under Product Category");

         }

         action.click();

         action.perform();

         Log.info("Click action is performed on the selected Product Type");

	 }

	 public static void waitForElement(WebElement element){

		 WebDriverWait wait = new WebDriverWait(driver, 10);

	     wait.until(ExpectedConditions.elementToBeClickable(element));

	 	}

	 public static void takeScreenshot(WebDriver driver, String sTestCaseName) throws Exception{

			try{

				File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

				FileUtils.copyFile(scrFile, new File(Constant.Path_ScreenShot + sTestCaseName +".jpg"));	

			} catch (Exception e){

				Log.error("Class Utils | Method takeScreenshot | Exception occured while capturing ScreenShot : "+e.getMessage());

				throw new Exception();

			}

		}

	}
```

# 页面对象包



## 基类

```java
package pageObjects;

import org.openqa.selenium.WebDriver;

public class BaseClass {

	public static WebDriver driver;

	public static boolean bResult;

	public  BaseClass(WebDriver driver){

		BaseClass.driver = driver;

		BaseClass.bResult = true;

	}

}
```

## 主页类

```java
package pageObjects;

        import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;

import utility.Log;

import utility.Utils;

    public class Home_Page extends BaseClass{

            private static WebElement element = null;

        public Home_Page(WebDriver driver){

            	super(driver);

        }    

        public static WebElement lnk_MyAccount() throws Exception{

            try{ 

	        	 element = driver.findElement(By.xpath(".//[@id='account']/a"));

	             Log.info("My Account link is found on the Home Page");

            }catch (Exception e){

           		Log.error("My Acocunt link is not found on the Home Page");

           		throw(e);

           		}

           	return element;

        }

        public static WebElement lnk_LogOut() throws Exception{

            try{

	        	element = driver.findElement(By.id("account_logout"));

	            Log.info("Log Out link is found on the Home Page");

            }catch (Exception e){

            	Log.error("Log Out link is not found on the Home Page");

           		throw(e);

           		}

           	return element;

        }

        public static class TopNavigation{

        	public static class Product_Type{

        			static WebElement mainElement;

        		public static void Accessories() throws Exception{

        			try{

	        			mainElement = driver.findElement(By.linkText("Product Category"));

	        			Log.info("Product category link is found under Top Navigation");

	        			Utils.mouseHoverAction(mainElement, "Accessories");

        			}catch (Exception e){

        				Log.error("Accessories link is not found under Product Category");

        				throw(e);

        			}

                   }

        		public static void iMacs() throws Exception{

        			try{

	        			mainElement = driver.findElement(By.linkText("Product Category"));

	        			Log.info("Product category link is found under Top Navigation");

	        			Utils.mouseHoverAction(mainElement, "iMacs");

        			}catch (Exception e){

        				Log.error("Accessories link is not found under Product Category");

        				throw(e);

        			}

                   }

        		public static void iPads() throws Exception{

        			try{

	        			mainElement = driver.findElement(By.linkText("Product Category"));

	        			Log.info("Product category link is found under Top Navigation");

	        			Utils.mouseHoverAction(mainElement, "iPads");

        			}catch (Exception e){

        				Log.error("Accessories link is not found under Product Category");

        				throw(e);

        			}

                   }

        		public static void iPhones() throws Exception{

        			try{

	        			mainElement = driver.findElement(By.linkText("Product Category"));

	        			Log.info("Product category link is found under Top Navigation");

	        			Utils.mouseHoverAction(mainElement, "iPhones");

        			}catch (Exception e){

        				Log.error("Accessories link is not found under Product Category");

        				throw(e);

        			}

                   }

        	}

        }

    }
```



## 登录页面类

```java
package pageObjects;

        import org.openqa.selenium.;

import utility.Log;

    public class LogIn_Page extends BaseClass {

           private static WebElement element = null;

        public LogIn_Page(WebDriver driver){

            	super(driver);

        }     

        public static WebElement txtbx_UserName() throws Exception{

        	try{

	            element = driver.findElement(By.id("log"));

	            Log.info("Username text box is found on the Login Page");

        	}catch (Exception e){

           		Log.error("UserName text box is not found on the Login Page");

           		throw(e);

           		}

           	return element;

            }

        public static WebElement txtbx_Password() throws Exception{

        	try{

	        	element = driver.findElement(By.id("pwd"));

	            Log.info("Password text box is found on the Login page");

        	}catch (Exception e){

        		Log.error("Password text box is not found on the Login Page");

           		throw(e);

           		}

           	return element;

        }

        public static WebElement btn_LogIn() throws Exception{

        	try{

	        	element = driver.findElement(By.id("login"));

	            Log.info("Submit button is found on the Login page");

        	}catch (Exception e){

        		Log.error("Submit button is not found on the Login Page");

           		throw(e);

           		}

           	return element;

        }

    }
```

## 产品列表页面类

```java
package pageObjects;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;

import utility.Log;

public class ProductListing_Page extends BaseClass {

	private static WebElement element;

    public ProductListing_Page(WebDriver driver){

    	super(driver);

    	} 

     public static class Product_1{

    	 public static WebElement txt_Price(){

        	 element = null;

           	try{

           		element= driver.findElement(By.xpath(".//[@id='default_products_page_container']/div[3]/div[2]/form/div[1]/p[1]/span[2]"));

           		Log.info("Product Price is found for Product 1");

           	}catch (Exception e){

           		Log.error("Product 1 Sales Price is not found");

           		throw(e);

           		}

           	return element;

            }

         public static WebElement img_Product(){

        	 element = null;

          	try{

          		element= driver.findElement(By.xpath(".//[@id='default_products_page_container']/div[3]/div[1]/a"));

          		Log.info("Product Image is found for Product 1");

          	}catch (Exception e){

          		Log.error("Product 1 Image is not found");

          		throw(e);

          		}

          	return element;

            }

         public static WebElement txt_Name() throws Exception{

            element = null;

         	try{

         		element= driver.findElement(By.xpath(".//[@id='default_products_page_container']/div[3]/div[2]/h2"));

         		Log.info("Product Name is found for Product 1");

         	}catch (Exception e){

         		Log.error("Product 1 Name is not found");

         		throw(e);

         		}

         	return element;

            }

         public static WebElement btn_AddToCart(){

        	  element = null;

           	try{

           		element= driver.findElement(By.xpath(".//[@id='default_products_page_container']/div[3]/div[2]/form/div[2]/div[1]/span/input"));

           		Log.info("Add to Cart button is found for Product 1");

           	}catch (Exception e){

           		Log.error("Product 1 Add to Cart button is not found");

           		throw(e);

           		}

           	return element;

         	}

         }

	public static class Product_2{

		 public static WebElement txt_Price(){

	    	 element = null;

	       	try{

	       		element= driver.findElement(By.xpath(".//[@id='default_products_page_container']/div[4]/div[2]/form/div[1]/p[1]/span[2]"));

	       		Log.info("Product Price is found for Product 2");

	       	}catch (Exception e){

	       		Log.error("Product 2 Sales Price is not found");

	       		throw(e);

	       		}

	       	return element;

	        }

	     public static WebElement img_Product(){

	    	 element = null;

	      	try{

	      		element= driver.findElement(By.xpath(".//[@id='default_products_page_container']/div[4]/div[1]/a"));

	      		Log.info("Product Image is found for Product 2");

	      	}catch (Exception e){

	      		Log.error("Product 2 Image is not found");

	      		throw(e);

	      		}

	      	return element;

	        }

	     public static WebElement txt_Name() throws Exception{

	        element = null;

	     	try{

	     		element= driver.findElement(By.xpath(".//[@id='default_products_page_container']/div[3]/div[2]/h2"));

	     		Log.info("Product Name is found for Product 2");

	     	}catch (Exception e){

	     		Log.error("Product 2 Name is not found");

	     		throw(e);

	     		}

	     	return element;

	        }

	     public static WebElement btn_AddToCart(){

	    	  element = null;

	       	try{

	       		element= driver.findElement(By.xpath(".//[@id='default_products_page_container']/div[4]/div[2]/form/div[2]/div[1]/span/input"));

	       		Log.info("Add to Cart button is found for Product 2");

	       	}catch (Exception e){

	       		Log.error("Product 2 Add to Cart button is not found");

	       		throw(e);

	       		}

	       	return element;

	     	}

	     }

	public static class PopUpAddToCart{

		 public static WebElement btn_GoToCart(){

	   	 element = null;

	      	try{

	      		element= driver.findElement(By.xpath(".//[@id='fancy_notification_content']/a[1]"));

	      		Log.info("Go to Cart button is found on the Cart Pop Up window");

	      	}catch (Exception e){

	      		Log.info("Go to Cart button is not found on the Cart Pop Up window");

	      		throw(e);

	      		}

	      	return element;

	       }

		 public static WebElement btn_ContShopping(){

		   	 element = null;

		      	try{

		      		element= driver.findElement(By.xpath(".//[@id='fancy_notification_content']/a[2]"));

		      		Log.info("Continue Shopping button is found on Cart Pop Up window");

		      	}catch (Exception e){

		      		Log.info("Continue Shopping button is not found on Cart Pop Up window");

		      		throw(e);

		      		}

		      	return element;

		       }

		}

}
```

## 签出页面类

```java
package pageObjects;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;

import utility.Log;

public class CheckOut_Page extends BaseClass{

		private static WebElement element;

		public static String sProductName;

		public static String sProductPrice;

     public CheckOut_Page(WebDriver driver){

    	super(driver);

    	}

	 public static WebElement txt_ProductPrice(){

    	 element = null;

       	try{

       		element= driver.findElement(By.xpath(".//[@id='checkout_page_container']/div[1]/table/tbody/tr[2]/td[4]"));

       		Log.info("Product Price for purchased product is found on the Check Out Page");

       	}catch (Exception e){

       		Log.error("Price for purchased product on Check Out page is not found");

       		throw(e);

       		}

       	return element;

        }

	 public static WebElement txt_ProductName(){

    	 element = null;

       	try{

       		element= driver.findElement(By.xpath(".//[@id='checkout_page_container']/div[1]/table/tbody/tr[2]/td[2]/a"));

       		Log.info("Product Name for purchased product is found on the Check Out Page");

       	}catch (Exception e){

       		Log.error("Price for purchased product on Check Out page is not found");

       		throw(e);

       		}

       	return element;

        }

	 public static WebElement btn_Continue(){

    	 element = null;

       	try{

       		element= driver.findElement(By.xpath(".//[@id='checkout_page_container']/div[1]/a/span"));

       		Log.info("Continue button is found on the Check Out Page");

       	}catch (Exception e){

       		Log.error("Continue button on Check Out page is not found");

       		throw(e);

       		}

       	return element;

        }

}
```

## 付款明细页面类

```java
package pageObjects;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;

import org.openqa.selenium.support.ui.Select;

import utility.Log;

public class PaymentDetails_Page extends BaseClass {

		private static WebElement element;

	 public PaymentDetails_Page(WebDriver driver){

   	     super(driver);

   	    }

	 public static WebElement txt_Email(){

    	 element = null;

       	try{

       		element= driver.findElement(By.id("wpsc_checkout_form_9"));

       		Log.info("Email text box on Payment Details page is found.");

       	}catch (Exception e){

       		Log.error("Email text box on Payment Details page is not found");

       		throw(e);

       		}

       	return element;

        }

	 public static WebElement txt_FirstName(){

    	 element = null;

       	try{

       		element= driver.findElement(By.id("wpsc_checkout_form_2"));

       		Log.info("First Name text box on Payment Details page is found.");

       	}catch (Exception e){

       		Log.error("First Name text box on Payment Details page is not found");

       		throw(e);

       		}

       	return element;

        }

	 public static WebElement txt_LastName(){

    	 element = null;

       	try{

       		element= driver.findElement(By.id("wpsc_checkout_form_3"));

       		Log.info("Last Name text box on Payment Details page is found.");

       	}catch (Exception e){

       		Log.error("Last Name text box on Payment Details page is not found");

       		throw(e);

       		}

       	return element;

        }

	 public static WebElement txt_Address(){

    	 element = null;

       	try{

       		element= driver.findElement(By.id("wpsc_checkout_form_4"));

       		Log.info("Address text box on Payment Details page is found.");

       	}catch (Exception e){

       		Log.error("Address text box on Payment Details page is not found");

       		throw(e);

       		}

       	return element;

        }

	 public static WebElement txt_City(){

    	 element = null;

       	try{

       		element= driver.findElement(By.id("wpsc_checkout_form_5"));

       		Log.info("City text box on Payment Details page is found.");

       	}catch (Exception e){

       		Log.error("City text box on Payment Details page is not found");

       		throw(e);

       		}

       	return element;

        }

	 public static void drpdwn_Country(String sCountry){

    	 element = null;

       	try{

       		Select element= new Select(driver.findElement(By.id("wpsc_checkout_form_7")));

       		Log.info("Country dropdown on Payment Details page is found.");

       		element.selectByVisibleText(sCountry);

       	}catch (Exception e){

       		Log.error("Country dropdown on Payment Details page is not found");

       		throw(e);

       		}

        }

	 public static WebElement txt_Phone(){

    	 element = null;

       	try{

       		element= driver.findElement(By.id("wpsc_checkout_form_18"));

       		Log.info("Phone text box on Payment Details page is found.");

       	}catch (Exception e){

       		Log.error("Phone text box on Payment Details page is not found");

       		throw(e);

       		}

       	return element;

        }

	 public static WebElement chkbx_SameAsBillingAdd(){

    	 element = null;

       	try{

       		element= driver.findElement(By.id("shippingSameBilling"));

       		Log.info("Same as Billing address check box on Payment Details page is found.");

       	}catch (Exception e){

       		Log.error("Same as Billing address check box on Payment Details page is not found");

       		throw(e);

       		}

       	return element;

        }

	 public static WebElement btn_Purchase(){

    	 element = null;

       	try{

       		element= driver.findElement(By.xpath(".//[@id='wpsc_shopping_cart_container']/form/div[4]/div/div/span/input"));

       		Log.info("Purchase button on Payment Details page is found.");

       	}catch (Exception e){

       		Log.error("Purchase button on Personal Details page is not found");

       		throw(e);

       		}

       	return element;

        }

}
```

## 确认页面类

```java
package pageObjects;

import org.openqa.selenium.By;

import org.openqa.selenium.WebDriver;

import org.openqa.selenium.WebElement;

import utility.Log;

public class Confirmation_Page extends BaseClass{

    private static WebElement element = null;

    public static String sProductName;

    public static String sProductPrice;

    public Confirmation_Page(WebDriver driver){

        	super(driver);

    }    

    public static WebElement txt_ProductName() throws Exception{

        try{ 

        	 element = driver.findElement(By.xpath(".//[@id='post-30']/div/div[2]/table/tbody/tr/td[1]"));

             Log.info("Product name is found on the Confirmation Page");

        }catch (Exception e){

       		Log.error("Product name is not found on the Confirmation Page");

       		throw(e);

       		}

       	return element;

    }

    public static WebElement txt_ProductPrice() throws Exception{

        try{ 

        	 element = driver.findElement(By.xpath(".//[@id='post-30']/div/div[2]/table/tbody/tr/td[4]"));

             Log.info("Product price is found on the Confirmation Page");

        }catch (Exception e){

       		Log.error("Product price is not found on the Confirmation Page");

       		throw(e);

       		}

       	return element;

    }

}
```