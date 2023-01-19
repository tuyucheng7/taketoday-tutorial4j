# 带有 Apache POI 的数据驱动框架 - Excel

市场上大多数商业自动化软件工具都支持某种数据驱动测试，它允许你使用不同的输入和验证值自动多次运行测试用例。由于 Selenium Webdriver 与其说是一个现成的工具，不如说是一个自动化测试框架，因此你将不得不付出一些努力来支持自动化测试中的数据驱动测试。我通常更喜欢使用 Microsoft Excel 作为存储参数的格式。使用 Excel 的另一个优点是你可以轻松地将测试数据管理外包给你以外的其他人，这些人可能更了解需要运行的测试用例以及执行它们所需的参数。

## 从 Excel 中读取数据

我们需要一种方法来打开此 Excel 工作表并在我们的 Selenium 测试脚本中从中读取数据。为此，我使用了 Apache POI 库，它允许你使用 Java 阅读、创建和编辑 Microsoft Office 文档。我们将用于从 Excel 工作表读取数据的类和方法位于 org.apache.poi.hssf.usermodel 包中。

## 怎么做...

1) [下载](https://toolsqa.com/blogs/download-apache-poi/) Apache POI 的 JAR 文件并将 [Jars 添加](https://www.toolsqa.com/blogs/download-apache-poi/) 到你的项目库中。你可以从 [这里下载它。](https://poi.apache.org/)这就是关于使用 eclipse 配置 Apache POI 的全部内容。现在你已准备好编写测试。

1.  创建一个[“新包”](https://toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/) 文件并将其命名为“ testData ”，方法是右键单击“项目”并选择 “新建”>“包”。将所有测试数据放在这个文件夹(包)中，无论它是 SQL 文件、excel 文件还是其他任何文件。
2.  将Excel文件放在上面创建的包位置并将其另存为TestData.xlsx。在excel中填写数据如下图：

![阿帕奇-POI-15](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Apache-POI-15.png)

1.  在Constant类中添加两个常量变量(testData包路径&Excel文件名) 。

```java
package utility;

   public class Constant {

      public static final String URL = "https://www.store.demoqa.com";

      public static final String Username = "testuser_1";

      public static final String Password = "Test@123";

      public static final String Path_TestData = "D://ToolsQA//OnlineStore//src//testData//"

      public static final String File_TestData = "TestData.xlsx"

   }
```

1.  创建一个[“新类”](https://toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/) 文件，右键单击“实用程序”包并选择“新建”>“类”并将其命名为“ExcelUtils”。首先，我们将编写基本的读/写方法。

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

    		public static void setCellData(String Result,  int RowNum, int ColNum) throws Exception	{

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

	}
```

1.  一旦我们完成了 Excel 函数的编写，我们就可以继续修改SignIn_Action模块以接受来自 excel 文件的测试数据。

```java
package appModules;

        import org.openqa.selenium.WebDriver;

        import pageObjects.Home_Page;

        import pageObjects.LogIn_Page;

        import utility.ExcelUtils;

    // Now this method does not need any arguments

    public class SignIn_Action {

		public static void Execute(WebDriver driver) throws Exception{

			//This is to get the values from Excel sheet, passing parameters (Row num & Col num)to getCellData method

			String sUserName = ExcelUtils.getCellData(1, 1);

			String sPassword = ExcelUtils.getCellData(1, 2);

			Home_Page.lnk_MyAccount(driver).click();

			LogIn_Page.txtbx_UserName(driver).sendKeys(sUserName);

			LogIn_Page.txtbx_Password(driver).sendKeys(sPassword);

			LogIn_Page.btn_LogIn(driver).click();

        }

}
```

注意：在后面的章节中，我们还将看到如何参数化行列，因为我们还必须避免脚本中的硬编码值。这只是为了给你一个使用 Excel 的想法，我们将逐步朝着适当的框架前进。

1.  创建一个[“新类”](https://toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)并将其命名为Apache_POI_TC ，方法是右键单击“automationFramework”包并选择 “新建”>“类” 。在这里，我们将从 Excel 工作表中读取值，将它们用作测试数据，并将测试结果写入 Excel。

```java
package automationFramework;

		import java.util.concurrent.TimeUnit;

		import org.openqa.selenium.WebDriver;

		import org.openqa.selenium.firefox.FirefoxDriver;

		import pageObjects.;

		import utility.Constant;

		// Import Package utility.

		import utility.ExcelUtils;

		import appModules.SignIn_Action;

	public class Apache_POI_TC {

			private static WebDriver driver = null;

		public static void main(String[] args) throws Exception {

        //This is to open the Excel file. Excel path, file name and the sheet name are parameters to this method

        ExcelUtils.setExcelFile(Constant.Path_TestData + Constant.File_TestData,"Sheet1");

        driver = new FirefoxDriver();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.get(Constant.URL);

        SignIn_Action.Execute(driver);

        System.out.println("Login Successfully, now it is the time to Log Off buddy.");

        Home_Page.lnk_LogOut(driver).click(); 

        driver.quit();

        //This is to send the PASS value to the Excel sheet in the result column.

        ExcelUtils.setCellData("Pass", 1, 3);

		}

	}
```

试一试，看看美化你的脚本将如何执行代码。

1.  完成后打开 Excel 文件并检查结果。

![阿帕奇-POI-16](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Apache-POI-16.png)

你的项目浏览器窗口现在将如下所示。

![Apache-POI](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Apache-POI.png)

不要担心测试脚本上的红色叉号，这是因为我们已经从 SignIn_Action 类中删除了参数。

可以使用[TestNg Data Provider 构建 Data-Driven 框架。](https://toolsqa.com/testng/testng-dataproviders/)