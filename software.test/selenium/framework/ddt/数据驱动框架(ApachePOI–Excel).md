你是否难以为你的应用程序维护大量测试用例？测试数据是否分散在各种测试脚本中？你是否必须为每个测试环境维护单独的测试脚本，然后在测试数据中的一个值发生变化时搜索所有脚本？这既费时又费力，不是吗？我们都希望测试用例是一致的，并以统一的方式编写，遵循一套规则，就像我们有交通规则，每个人在路上都试图遵守同样的规则。这就是数据驱动框架发挥作用的地方。

你还可以使你的测试用例遵循统一的模式和风格，为你在应用程序上工作的同行制定框架指南，以根据框架规则编写脚本。在测试自动化中，我们可以使用数据驱动框架来实现这一切。通过涵盖以下主题中的详细信息，让我们了解如何使用 Selenium WebDriver 创建数据驱动测试框架来自动化 Web 应用程序：

-   什么是自动化框架？
    -   为什么我们需要自动化测试框架？
    -   Selenium 中有哪些不同类型的自动化框架？
-   什么是数据驱动框架？
    -   另外，使用数据驱动测试框架有什么好处？
-   如何使用 Apache POI 在 Selenium 中创建数据驱动框架？

## 什么是自动化框架？

自动化测试框架是一组用于创建和设计测试用例的指南或规则。这些指南包括编码标准、对象存储库、测试数据处理方法、存储测试结果的过程，或有关如何访问外部资源的任何其他信息。

测试人员始终可以在没有框架的情况下编写测试，这不是强制性步骤，但使用有组织的框架会提供额外的好处，如增加代码重用、更高的可移植性、降低脚本维护成本和更高的代码可读性。它还可以帮助团队以标准格式写下测试脚本。使用自动化测试框架，可以高效地设计和开发自动化测试脚本，并确保对被测系统或应用程序的问题或错误进行可靠的分析。以下部分列出了一些重要的好处，这些好处证明了自动化测试框架的必要性：

### 为什么我们需要自动化测试框架？



使用框架进行自动化测试很重要，因为它可以提高自动化测试团队的效率和测试开发速度。使用自动化框架的一些好处如下：

-   所有测试的标准格式
-   提高测试效率
-   降低脚本维护成本
-   最大测试覆盖率
-   代码的可重用性
-   高效的测试数据管理

### Selenium 中有哪些不同类型的自动化框架？

使用Selenium WebDriver测试应用程序时，我们可以使用三种主要类型的框架来为任何 Web 应用程序创建自动化测试：

![框架类型 数据驱动框架](https://www.toolsqa.com/gallery/selnium%20webdriver/1.framework%20types%20data%20driven%20framework.png)

-   数据驱动测试框架。
-   关键字驱动的测试框架。
-   混合测试框架。

这些框架中的每一个都有自己的体系结构和不同的优缺点。在制定测试计划时，选择适合你的框架很重要。

-   数据驱动测试框架用于将测试脚本与测试数据分开。你可以使用多组数据测试同一个脚本。我们将在以下主题中详细讨论此框架。
-   关键字驱动测试框架是数据驱动框架的扩展。它允许在测试脚本外部的单独代码文件中存储一组称为“关键字”的代码。我们可以在多个测试脚本中重用这些关键字。有关详细信息，请参阅 -[关键字驱动框架](https://www.toolsqa.com/selenium-webdriver/keyword-driven-framework/introduction/)
-   混合驱动框架是数据驱动和关键字驱动框架的组合。在这里，关键字以及测试数据都是外部的。我们在单独的文件中维护关键字，并在 excel 文件或 CSV 文件或数据库中测试数据。有关详细信息，请参阅 -[混合框架。](https://www.toolsqa.com/selenium-webdriver/selenium-automation-hybrid-framework/)

在这篇文章中，让我们深入探讨数据驱动测试框架。

### 什么是数据驱动框架？

通常，我们在手动测试应用程序时，会针对多个测试数据运行相同的场景。此外，我们将相同的测试数据保存在某些文件中，例如Excel 文件、文本文件、CSV 文件或任何数据库。自动化也是如此，我们希望对多个测试数据运行相同的测试场景。假设你编写了一个自动化脚本来填写[ToolsQA 演示站点](https://demoqa.com/automation-practice-form)上的学生注册表。可以有很多学生注册，代码中唯一不同的是输入值(姓名、地址、电话、性别等)。你会为每个学生写一个单独的脚本来注册吗？有没有办法，我们可以重用代码，只更改学生数据？

是的，这就是数据驱动框架发挥作用的地方，它使测试脚本能够针对不同的测试数据集正常工作。它节省了编写额外代码的时间。这就像一次编写并运行多次机制，因为你可以多次运行相同的Selenium 脚本。

简而言之，当我们必须使用多组测试数据执行同一个脚本时，我们使用数据驱动框架，这些测试数据的存储在不同的地方并且不存在于测试脚本中。对数据所做的任何更改都不会影响测试代码。

![数据驱动框架](https://www.toolsqa.com/gallery/selnium%20webdriver/2.data%20driven%20framework.png)

### 使用数据驱动测试框架有什么好处？

以下是 QA 在使用数据驱动技术开发自动化框架时可以获得的一些主要好处：

-   无需对代码进行太多更改即可修改测试用例。
-   它允许使用多组数据值测试应用程序，尤其是在回归测试期间。
-   它帮助我们将测试用例/脚本的逻辑与测试数据分开。

最常用的测试数据源之一是Microsoft Excel 表格。我们可以在 excel 表中维护数据并在测试脚本中使用它们。让我们看看如何通过从 Excel 文件中读取测试数据来创建数据驱动的 UI 自动化框架。

## 如何使用 Apache POI 在 Selenium 中创建数据驱动框架？

我们在上一篇文章[“在 Selenium 中从 Excel 读取和写入数据”](https://www.toolsqa.com/selenium-webdriver/excel-in-selenium/)中学习了如何使用Apache POI在Excel 文件中读取和写入数据，然后将相同的数据集作为测试数据传递给 Selenium 测试。但是在该脚本中，所有从 Excel 文件读取数据、将数据写入 Excel 文件、将数据传递给 Selenium 操作的操作都发生在该类的 main 方法中。如果我们只是编写一个或两个测试用例，那么这种格式是可以接受的。然而，当我们必须开发一个具有多个测试场景的自动化框架时，它应该适当地组织并且应该有一个定义的文件夹层次结构。

数据驱动测试框架的基本经验法则是将测试数据与测试脚本分开。此外，从文件读取/写入数据的操作应该隔离并作为实用程序使用。

按照下面提到的步骤创建一个基本的数据驱动框架，该框架将用于自动化[“学生注册表”。](https://demoqa.com/automation-practice-form)

-   在你的项目中为测试用例、测试数据和实用程序创建三个[新包。](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)
-   在testData包下，放入包含测试数据的 Excel 工作表。使用它，我们将测试数据与测试用例分开。
-   在实用程序下， [创建一个新类](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/#package)并将其命名为“ExcelUtils”。它将包含与用于读取和写入的 Excel 相关的所有函数。
-   在utilities包下，创建另一个类“Constants”。它将包含跨框架的常量值，如测试数据文件路径、应用程序的 URL 等。
-   在testCases包下，我们将创建包含用于与 Web 元素交互的 Selenium 代码的测试文件。(例如，RegisterStudentTest.java)

![数据驱动框架中的项目结构](https://www.toolsqa.com/gallery/selnium%20webdriver/3.project%20Structure%20in%20datadrive%20framework.png)

执行上述步骤后，文件夹结构将如下所示：

让我们了解每个类的详细信息：

1.  ExcelUtils 类 -它是一个实用程序类，将包含与 Excel 工作表读写操作相关的所有方法以及初始化工作簿。然后，你可以通过创建 Excel Utils 类的对象在不同的测试用例中重用这些方法。此类的代码如下 -

```java
package utilities;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelUtils {
   private static HSSFWorkbook workbook;
    private static HSSFSheet sheet;
    private static HSSFRow row;
    private static HSSFCell cell;

   public void setExcelFile(String excelFilePath,String sheetName) throws IOException {
       //Create an object of File class to open xls file
       File file =    new File(excelFilePath);
       
       //Create an object of FileInputStream class to read excel file
       FileInputStream inputStream = new FileInputStream(file);
       
       //creating workbook instance that refers to .xls file
       workbook=new HSSFWorkbook(inputStream);
       
       //creating a Sheet object
        sheet=workbook.getSheet(sheetName);

   }

    public String getCellData(int rowNumber,int cellNumber){
       //getting the cell value from rowNumber and cell Number
        cell =sheet.getRow(rowNumber).getCell(cellNumber);
        
        //returning the cell value as string
        return cell.getStringCellValue();
    }

    public int getRowCountInSheet(){
       int rowcount = sheet.getLastRowNum()-sheet.getFirstRowNum();
       return rowcount;
    }

    public void setCellValue(int rowNum,int cellNum,String cellValue,String excelFilePath) throws IOException {
    	//creating a new cell in row and setting value to it      
    	sheet.getRow(rowNum).createCell(cellNum).setCellValue(cellValue);
        
    	FileOutputStream outputStream = new FileOutputStream(excelFilePath);
    	workbook.write(outputStream);
    }
}
```

上面的代码包含不同的方法，例如用于初始化Excel 工作簿的setExcelFile，用于检索文件中特定单元格中存在的值的getCellValue，用于将一些值设置到新创建的单元格中的setCellValue 。类似地，你可以在该类中创建与excel操作相关的不同方法。

1.  常量类 -它用于将常量值放入文件中，以便可以在测试用例中重复使用相同的值。将值放在单独的文件中的另一个优点是，由于这些值在各种测试中是通用的，如果任何值发生任何变化，你只需在一个地方进行更新。例如，如果更改了文件路径，则无需使用新值更新所有测试用例，只需在一个文件中更新即可。此类中的结构和值如下 -

```java
package utilities;

public class Constants {
    public static final String URL = "https://demoqa.com/automation-practice-form";
    public static final String Path_TestData = "E:\\Projects\\src\\testData\\";
    public static final String File_TestData = "TestData.xls";
}
```

1.  RegisterStudentTest-这是学生注册表的测试脚本，我们用来输入特定学生的名字、姓氏、手机、电子邮件、性别等。由于我们现在已经将excel相关的方法分离到一个单独的文件中，所以我们的测试用例的代码也发生了变化。

我们将在这个测试文件中创建一个ExcelUtils类的对象，并使用Constants来引用文件的路径。

更新后的代码现在看起来像 -

```java
package testCases;


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import utilities.Constants;
import utilities.ExcelUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RegisterStudentTest {
    
	//creating object of ExcelUtils class
    static ExcelUtils excelUtils = new ExcelUtils();
    
    //using the Constants class values for excel file path 
    static String excelFilePath =Constants.Path_TestData+Constants.File_TestData;

    public static  void main(String args[]) throws IOException {
        //set the Chrome Driver path
        System.setProperty("webdriver.chrome.driver","E:\\Projects\\chromedriver.exe");
        
        //Creating an object of ChromeDriver
        WebDriver driver = new ChromeDriver();
        
        //launching the specified URL
        driver.get("https://demoqa.com/automation-practice-form");
        
        //Identify the WebElements for the student registration form
        WebElement firstName=driver.findElement(By.id("firstName"));
        WebElement lastName=driver.findElement(By.id("lastName"));
        WebElement email=driver.findElement(By.id("userEmail"));
        WebElement genderMale= driver.findElement(By.id("gender-radio-1"));
        WebElement mobile=driver.findElement(By.id("userNumber"));
        WebElement address=driver.findElement(By.id("currentAddress"));
        WebElement submitBtn=driver.findElement(By.id("submit"));
        
        //calling the ExcelUtils class method to initialise the workbook and sheet
        excelUtils.setExcelFile(excelFilePath,"STUDENT_DATA");

        //iterate over all the row to print the data present in each cell.
        for(int i=1;i<=excelUtils.getRowCountInSheet();i++)
        {
        	//Enter the values read from Excel in firstname,lastname,mobile,email,address
        	firstName.sendKeys(excelUtils.getCellData(i,0));
        	lastName.sendKeys(excelUtils.getCellData(i,1));
        	email.sendKeys(excelUtils.getCellData(i,2));
        	mobile.sendKeys(excelUtils.getCellData(i,3));
        	address.sendKeys(excelUtils.getCellData(i,4));
        
        	//Click on the gender radio button using javascript
        	JavascriptExecutor js = (JavascriptExecutor) driver;
        	js.executeScript("arguments[0].click();", genderMale);
       
        	//Click on submit button
        	submitBtn.click();
        
        	//Verify the confirmation message
            WebElement confirmationMessage = driver.findElement(By.xpath("//div[text()='Thanks for submitting the form']"));
            
            //check if confirmation message is displayed
            if (confirmationMessage.isDisplayed()) {
            	// if the message is displayed , write PASS in the excel sheet using the method of ExcelUtils
            	excelUtils.setCellValue(i,6,"PASS",excelFilePath);
            } else {
                //if the message is not displayed , write FAIL in the excel sheet using the method of ExcelUtils
                excelUtils.setCellValue(i,6,"FAIL",excelFilePath);
            }

            //close the confirmation popup
            WebElement closebtn=driver.findElement(By.id("closeLargeModal"));
            closebtn.click();
         
            //wait for page to come back to registration page after close button is clicked
            driver.manage().timeouts().implicitlyWait(2000,TimeUnit.SECONDS);
        }
        //closing the driver
        driver.quit();
    }
}
```

上面的类将执行学生注册页面上的操作。但是，如果你注意到，ExcelUtils的方法会 处理所有与 Excel 相关的代码。

因此，这是你可以在 Selenium 中使用数据驱动框架的方法之一。此外，你还可以利用跨多组数据运行相同测试的优势。

## 关键要点

-   数据驱动是一种测试自动化框架，它以表格或电子表格格式存储测试数据。
-   此外，数据驱动测试框架有助于将测试数据与功能测试分开。此外，它允许使用多组数据值测试应用程序，而无需跨不同的测试脚本重写相同的代码。
-   此外，我们使用 Apache POI 在 Selenium 中执行数据驱动测试。它是一个有助于在 Excel 工作表中读取和写入数据的库。