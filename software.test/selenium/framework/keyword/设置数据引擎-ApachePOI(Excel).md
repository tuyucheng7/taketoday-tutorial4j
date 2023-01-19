在上一章中，我们创建了Action Keywords并将它们放入“DataEngine” excel 表中，现在我们需要Apache POI来连接 Selenium 测试中的 excel 表。

## 设置数据引擎 - Apache POI (Excel)

我们需要一种方法来打开此 Excel 工作表并在此 Selenium 测试脚本中从中读取数据。为此，我使用了Apache POI库，它允许你使用 Java阅读、创建和编辑Microsoft Office 文档。我们将用于从 Excel 工作表中读取数据的类和方法位于“org.apache.poi.hssf.usermodel”包中。

## 从 Excel 读取数据

1.  Apache POI 的[“下载 JAR 文件” 。](https://www.toolsqa.com/blogs/download-apache-poi/)[你可以从此处](https://poi.apache.org/)  下载 Apache POI Jar 文件 注意： Sep'14 的最新版本是'poi - 3.10.1'
2.  下载 JAR 文件后，[将 Jars](https://www.toolsqa.com/blogs/download-apache-poi/) 文件“添加”到项目库中。这就是关于使用 eclipse 配置 Apache POI 的全部内容。现在你已准备好编写代码。
3.  创建一个[“新包”](https://toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/) 并将其命名为“实用程序”，方法是右键单击项目文件夹并选择 新建 > 包。
4.  创建一个[“新类”](https://toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/) 文件，右键单击“实用程序” 包并选择 “新建”>“类 ”并将其命名为“ ExcelUtils ”。首先我们将编写基本的读取方法。

```java
package utility;

import java.io.FileInputStream;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
    public class ExcelUtils {
                private static XSSFSheet ExcelWSheet;
                private static XSSFWorkbook ExcelWBook;
                private static XSSFCell Cell;

            //This method is to set the File path and to open the Excel file
            //Pass Excel Path and SheetName as Arguments to this method
            public static void setExcelFile(String Path,String SheetName) throws Exception {
	                FileInputStream ExcelFile = new FileInputStream(Path);
	                ExcelWBook = new XSSFWorkbook(ExcelFile);
	                ExcelWSheet = ExcelWBook.getSheet(SheetName);
                   }

            //This method is to read the test data from the Excel cell
            //In this we are passing parameters/arguments as Row Num and Col Num
            public static String getCellData(int RowNum, int ColNum) throws Exception{
            	  Cell = ExcelWSheet.getRow(RowNum).getCell(ColNum);
                  String CellData = Cell.getStringCellValue();
                  return CellData;
            	}

    	}
```

现在修改你的主要“DriverScript”。在 Excel Utility 的帮助下，打开 Excel 文件，一个一个地读取Action Keywords并在每个 Action Keyword 上执行所需的步骤。

```java
package executionEngine;

import config.ActionKeywords;
import utility.ExcelUtils;

public class DriverScript {

    public static void main(String[] args) throws Exception {
    	// Declaring the path of the Excel file with the name of the Excel file
    	String sPath = "D://Tools QA Projects//trunk//Hybrid Keyword Driven//src//dataEngine//DataEngine.xlsx";

    	// Here we are passing the Excel path and SheetName as arguments to connect with Excel file 
    	ExcelUtils.setExcelFile(sPath, "Test Steps");

    	//Hard coded values are used for Excel row & columns for now
    	//In later chapters we will replace these hard coded values with varibales
    	//This is the loop for reading the values of the column 3 (Action Keyword) row by row
    	for (int iRow=1;iRow<=9;iRow++){
		    //Storing the value of excel cell in sActionKeyword string variable
    		String sActionKeyword = ExcelUtils.getCellData(iRow, 3);

    		//Comparing the value of Excel cell with all the project keywords
    		if(sActionKeyword.equals("openBrowser")){
                        //This will execute if the excel cell value is 'openBrowser'
    			//Action Keyword is called here to perform action
    			ActionKeywords.openBrowser();}
    		else if(sActionKeyword.equals("navigate")){
    			ActionKeywords.navigate();}
    		else if(sActionKeyword.equals("click_MyAccount")){
    			ActionKeywords.click_MyAccount();}
    		else if(sActionKeyword.equals("input_Username")){
    			ActionKeywords.input_Username();}
    		else if(sActionKeyword.equals("input_Password")){
    			ActionKeywords.input_Password();}
    		else if(sActionKeyword.equals("click_Login")){
    			ActionKeywords.click_Login();}
    		else if(sActionKeyword.equals("waitFor")){
    			ActionKeywords.waitFor();}
    		else if(sActionKeyword.equals("click_Logout")){
    			ActionKeywords.click_Logout();}
    		else if(sActionKeyword.equals("closeBrowser")){
    			ActionKeywords.closeBrowser();}

    		}
    	}
 }
```

至此，我们完成了关键字驱动框架的初始设置。Keyword Driven 框架背后的唯一想法是接受来自 Excel 的操作关键字，以便可以在 Excel 文件中轻松创建所有测试用例。但这个框架只是一个草案版本。有很多事情可以做，使它更健壮和有效。在接下来的章节中，我们将增强这个草稿版本。