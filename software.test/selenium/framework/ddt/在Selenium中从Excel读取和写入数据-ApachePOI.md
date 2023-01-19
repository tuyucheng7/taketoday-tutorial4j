[Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)是用于 Web 应用程序自动化的最常用的自动化工具。现在，我们知道这些 Web 应用程序由多个用户使用，并且每个用户都根据自己的数据使用这些应用程序。因此，考虑到使用情况，QA 的主要职责还包括使用不同的数据集测试 Web 应用程序。现在用户旅程将相同，但数据集将不同。因此，使用不同的数据执行相同的测试用例比使用每个数据集为每个用户旅程编写单独的测试用例更有意义。这就是[Microsoft Excel](https://www.microsoft.com/en-in/microsoft-365/excel)派上用场的地方，它是存储测试数据的最受欢迎的工具之一。精于硒是最常用的组合之一，用于存储测试数据，然后针对各种数据集运行相同的测试用例。

JAVA中有各种库可以帮助从Excel文件读取/写入数据。但是， Apache POI是最常用的库之一，它提供了各种类和方法来从各种格式的 Excel 文件(xls、xlsx 等)中读取/写入数据。随后，在本文中，我们将通过涵盖以下主题下的详细信息，了解 Apache POI 的详细信息以及如何使用它从 Excel 文件中读取/写入数据：

-   什么是 Apache 兴趣点？
    -   如何管理Excel工作簿？
    -   如何管理Excel工作表？
    -   另外，如何管理Excel行？
    -   如何管理Excel单元格？
-   如何使用 Apache POI 在 Selenium 测试中从 Excel 中读取数据？
    -   另外，如何读取特定的单元格值？
    -   如何阅读整个Excel工作表？
-   如何使用 Apache POI 在 Selenium 测试中将数据写入 Excel？
    -   此外，如何写入现有行中的新单元格？
    -   而且，如何写入新行中的新单元格？

## 什么是 Apache 兴趣点？

Apache POI，其中 POI 代表 (Poor Obfuscation Implementation) 是一种API，它提供了一组 Java 库，可以帮助我们读取、写入和操作不同的 Microsoft 文件，例如excel 工作表、power-point 和 word 文件。

![然后在硒中的apache](https://www.toolsqa.com/gallery/selnium%20webdriver/1.apache%20poi%20in%20selenium.png)

Apache POI使用特定术语来处理Microsoft Excel。在我们进入代码的细节之前，让我们先熟悉一下这些。

| 学期   | 细节                                                   |
| ------------ | ------------------------------------------------------------ |
| 工作簿 | 一个工作簿代表一个 Microsoft Excel 文件。它可用于创建和维护电子表格。一个工作簿可能包含许多工作表。 |
| 床单   | 工作表是指 Microsoft Excel 文件中包含行数和列数的页面。 |
| 排     | 一行表示单元格的集合，用于表示电子表格中的一行。       |
| 细胞   | 单元格由行和列的组合表示。用户输入的数据存储在单元格中。数据可以是字符串、数值或公式等类型。 |

在开始之前，第一步是下载使用该库所需的 jar 文件。Apache POI库可以参考下载Apache POI的[步骤下载Apache POI库。](https://www.toolsqa.com/blogs/download-apache-poi/)

下图清楚地描述了Apache POI中的结构以及类和接口是如何对齐的。

![apache poi 类和接口](https://www.toolsqa.com/gallery/selnium%20webdriver/2.apache%20poi%20classes%20and%20interfaces.png)

现在让我们了解如何使用Apache POI 访问和管理 Excel 文件中的各种组件？

### 如何务实地管理Excel工作簿？

Apache POI提供了各种接口和类来帮助我们使用Excel。它提供了一个“工作簿”界面来维护Excel工作簿。有一些类实现了这个接口，我们使用这些类来创建、修改、读取和写入 Excel 文件中的数据。用于管理Excel 工作簿的两个主要使用的类是：

-   HSSFWorkbook -这些类方法用于将数据读/写到.xls格式的 Microsoft Excel 文件。它与 MS-Office 版本 97–2003 兼容。
-   XSSFWorkbook -这些类方法用于以.xls或.xlsx格式将数据读写到 Microsoft Excel 。它与 MS-Office 2007 或更高版本兼容。

### 如何以编程方式管理 Excel 工作表？

还有另一个接口“Sheet”，我们用它来在工作簿中创建一个工作表。有两个用于处理工作表的类，与我们用于工作簿接口的类相同：

-   HSSFSheet -此类用于在 HSSFWorkbook 中创建新工作表，即 Excel 的旧格式。
-   XSSFSheet -该类用于在XSSFWorkbook中创建一个新的工作表，即Excel的新格式

### 如何务实地管理Excel行？

Row接口使我们能够处理 Excel 工作表中的行。下面两个类实现了这个接口：

-   HSSFRow - 这表示 HSSFSheet 中的一行。
-   XSSFRow - 这表示 XSSFSheet 中的一行。

### 如何管理Excel单元格？



Cell接口帮助我们访问特定行的单元格。有两个实现此接口的类，我们可以使用它们将数据读/写到单元格中：

-   HSSFCell - 我们用它来处理 HSSFRow 的单元格。
-   XSSFCell - 我们用它来处理 XSSFRow 的单元格。

现在我们已经了解了Apache POI库的详细信息，让我们尝试使用它通过Selenium WebDriver读取和写入 Excel 文件。

## 如何使用 Apache POI 在 Selenium 测试中从 Excel 中读取数据？

假设，对于一个Selenium测试用例，我们需要从Excel Sheet 中读取学生数据，示例数据如下所示：

![示例 excel 文件](https://www.toolsqa.com/gallery/selnium%20webdriver/3.sample%20excel%20file.png)

在读取Excel文件时，Apache POI可以通过两种方式读取数据：

-   你想要读取特定单元格的值，例如，你想要获取第二行中学生的地址。
-   你可以一次阅读整个 excel。它基于你的测试脚本的需要和测试执行所需的数据。

我们将了解在 Selenium 中读取excel 的两种方式，但在此之前，需要遵循一些通用步骤：

1.  第一步是根据其在计算机上的位置获取 Excel 工作簿。你可以通过引用指向 excel 文件的FileInputStream对象来创建工作簿的对象。我们可以使用HSSFWorkbook 类如下所示进行操作。 如果你使用的是 MS-Office 版本 97–2003 或XSSFWorkbook 类(如果是 MS-Office 版本 2007 或更高版本)。在下面的行中，我们使用 HSSFWorkbook ，因为 Excel 版本是 97-2003。

```java
File file =    new File("E:\\TestData\\TestData.xls");
FileInputStream inputStream = new FileInputStream(file);
HSSFWorkbook wb=new HSSFWorkbook(inputStream);
```

1.  一旦我们创建了工作簿，下一步就是在工作簿中创建一个工作表。此外，我们可以在getSheet (String sheetName)方法中使用工作表的名称，如下所示。此处，“STUDENT_DATA”是 Excel 工作簿中工作表的名称。

```java
HSSFSheet sheet=wb.getSheet("STUDENT_DATA");
```

你还可以使用getSheetAt (int index)方法根据索引创建工作表，如下所示 -

```java
 HSSFSheet sheet1=wb.getSheetAt(1);
```

1.  创建工作表后，我们必须获取工作表的行，我们可以使用工作表对象的getRow (int rowIndex)方法检索：

```java
 HSSFRow row1=sheet.getRow(1);
```

1.  获得行后，可以使用HSSFRow类的getCell (int index)方法获取行的单元格：

```java
sheet.getRow(1).getCell(1)
```

1.  获取包含数据的单元格后，你可以使用基于你在 Excel 工作表中指定的单元格格式的不同方法读取不同格式(如字符串、日期、数字)的数据。

-   String - getStringCellValue () [可用于从 Excel 中读取学生姓名]
-   Number-getNumericCellValue ()【可用于读取学生手机号】
-   Date - getDateCellValue () 【可以用来读取学生的出生日期】

![Excel中的各种数据格式](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Various%20data%20formats%20in%20Excel.png)

上图显示了 Excel 中单元格值的可行格式(常规、文本、数字、日期、时间)。对于更简单的方法，你可以将所有值指定为文本(偶数)并在String变量中读取它们。

### 如何读取特定的单元格值？

现在我们已经熟悉了Apache POI 库提供的不同类和方法，让我们尝试将它们组合到一个代码片段中，我们在示例 Excel 中尝试读取第一行中学生的地址。地址出现在该行的第 5 个单元格中。

注意：行和单元格的索引都从零开始。

![数据驱动框架-ExcelFile中的地址示例](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Data%20Driven%20Framework-SampleAddressinExcelFile.png)

你可以使用下面的代码片段使用上面解释的方法打印上图中突出显示的地址 -

```java
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ApachePOI {
    public static  void main(String args[]) throws IOException {
        
        //Create an object of File class to open xlsx file
        File file =    new File("E:\\TestData\\TestData.xls");
        
        //Create an object of FileInputStream class to read excel file
        FileInputStream inputStream = new FileInputStream(file);
        
        //Creating workbook instance that refers to .xls file
        HSSFWorkbook wb=new HSSFWorkbook(inputStream);
        
        //Creating a Sheet object using the sheet Name
        HSSFSheet sheet=wb.getSheet("STUDENT_DATA");
        
        //Create a row object to retrieve row at index 1
        HSSFRow row2=sheet.getRow(1);
        
        //Create a cell object to retreive cell at index 5
        HSSFCell cell=row2.getCell(5);
        
        //Get the address in a variable
        String address= cell.getStringCellValue();
        
        //Printing the address
        System.out.println("Address is :"+ address);
    }
}
```

当我们运行上面的程序时，我们将得到如下输出：

![使用 Apache POI 读取 Excel 的一个单元格](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Read%20one%20cell%20of%20Excel%20using%20Apache%20POI.png)

突出显示的区域显示使用代码打印的第一个学生的地址。

现在我们已经了解了如何读取特定的单元格值，现在我们来看看如何从 Excel 文件中读取完整的数据。

### 如何阅读整个Excel工作表？

要从Excel 中读取完整数据，你可以遍历工作表中行的每个单元格。对于迭代，你需要工作表中存在的行数和单元格总数。此外，我们可以从工作表中获取行数，这基本上是工作表中存在数据的总行数，方法是使用计算 -

RowCount = LastRowNumber - 第一行号

要获取最后一行和第一行的数字， sheet 类中有两种方法：

-   getLastRowNum()
-   getFirstRowNum()

因此，我们可以使用以下代码获取行数：

```java
int rowCount=sheet.getLastRowNum()-sheet.getFirstRowNum();
```

获得该行后，你可以使用单元格总数迭代行中存在的单元格，我们可以使用getLastCellNum()方法计算：

```java
int cellcount=sheet.getRow(1).getLastCellNum();
```

让我们尝试使用以下代码打印工作表中存在的全部数据：

```java
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ApachePOI {
    public static  void main(String args[]) throws IOException {
        //Create an object of File class to open xlsx file
        File file =    new File("E:\\TestData\\TestData.xls");

        //Create an object of FileInputStream class to read excel file
        FileInputStream inputStream = new FileInputStream(file);

        //creating workbook instance that refers to .xls file
        HSSFWorkbook wb=new HSSFWorkbook(inputStream);

        //creating a Sheet object
        HSSFSheet sheet=wb.getSheet("STUDENT_DATA");
        
        //get all rows in the sheet
        int rowCount=sheet.getLastRowNum()-sheet.getFirstRowNum();
        
        //iterate over all the row to print the data present in each cell.
        for(int i=0;i<=rowCount;i++){
            
            //get cell count in a row
            int cellcount=sheet.getRow(i).getLastCellNum();
            
            //iterate over each cell to print its value
            System.out.println("Row"+ i+" data is :");
            
            for(int j=0;j<cellcount;j++){
                System.out.print(sheet.getRow(i).getCell(j).getStringCellValue() +",");
            }
            System.out.println();
        }
    }
}
```

代码片段的输出是：

![使用 Apache POI 读取所有行](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Read%20all%20rows%20using%20Apache%20POI.png)

在上图中，你可以看到打印了 excel 中的数据，还注意到第 0 行打印了标题。此外，你可以通过从值 =1 而不是 0 开始循环来避免打印标题。

### 如何使用 Apache POI 在 Selenium 测试中将数据写入 Excel？

与阅读类似，在 Excel 文件中写入数据同样重要，因为它可以将测试结果保存回 Excel 工作表中。Apache POI提供了各种设置方法，我们可以使用这些方法在 Selenium测试本身中将数据写入Excel 。因此，让我们看看如何实现相同的目标：

### 如何写入现有行中的新单元格？

假设，我们想使用给定的测试数据在我们有输入数据的同一行中写入测试运行的结果。考虑一下，我们只需要在该行的最后一列中放置一个“PASS/FAIL” ，我们可以使用Apache POI实现相同的效果，如下所示：

```java
HSSFCell cell = sheet.getRow(1).createCell(6);
          if(confirmationMessage.isDisplayed()){
              cell.setCellValue("PASS");
          }else{
              cell.setCellValue("FAIL");
          }

//To write into Excel File
FileOutputStream outputStream = new FileOutputStream("E:\\TestData\\TestData.xls");
wb.write(outputStream);
```

假设考虑这样一个场景，在[\“https://demoqa.com/automation-practice-form”\](https://demoqa.com/automation-practice-form)页面上，我们通过从Excel文件中读取数据来填写学生注册表，然后将结果附加到表格的最后一个单元格中行，如果成功。随后，我们可以借助 Apache POI 库在 Selenium 中使用 Excel 的数据来实现相同的目的，如下所示：

```java
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WriteToExcel {
    public static  void main(String args[]) throws IOException {
        //set the ChromeDriver path
        System.setProperty("webdriver.chrome.driver","E:\\Projects\\chromedriver.exe");

        //Create an object of File class to open xls file
        File file =    new File("E:\\TestData\\TestData.xls");
        
        //Create an object of FileInputStream class to read excel file
        FileInputStream inputStream = new FileInputStream(file);
        
        //creating workbook instance that refers to .xls file
        HSSFWorkbook wb=new HSSFWorkbook(inputStream);
        
        //creating a Sheet object
        HSSFSheet sheet=wb.getSheet("STUDENT_DATA");
        
        //get all rows in the sheet
        int rowCount=sheet.getLastRowNum()-sheet.getFirstRowNum();
        
       //Creating an object of ChromeDriver
        WebDriver driver = new ChromeDriver();
        
        //Navigate to the URL
        driver.get("https://demoqa.com/automation-practice-form");


        //Identify the WebElements for the student registration form
        WebElement firstName=driver.findElement(By.id("firstName"));
        WebElement lastName=driver.findElement(By.id("lastName"));
        WebElement email=driver.findElement(By.id("userEmail"));
        WebElement genderMale= driver.findElement(By.id("gender-radio-1"));
        WebElement mobile=driver.findElement(By.id("userNumber"));
        WebElement address=driver.findElement(By.id("currentAddress"));
        WebElement submitBtn=driver.findElement(By.id("submit"));



        //iterate over all the rows in Excel and put data in the form.
        for(int i=1;i<=rowCount;i++) {
            //Enter the values read from Excel in firstname,lastname,mobile,email,address
            firstName.sendKeys(sheet.getRow(i).getCell(0).getStringCellValue());
            lastName.sendKeys(sheet.getRow(i).getCell(1).getStringCellValue());
            email.sendKeys(sheet.getRow(i).getCell(2).getStringCellValue());
            mobile.sendKeys(sheet.getRow(i).getCell(4).getStringCellValue());
            address.sendKeys(sheet.getRow(i).getCell(5).getStringCellValue());
            
            //Click on the gender radio button using javascript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", genderMale);
            
            //Click on submit button
            submitBtn.click();
            
            //Verify the confirmation message
            WebElement confirmationMessage = driver.findElement(By.xpath("//div[text()='Thanks for submitting the form']"));
            
            //create a new cell in the row at index 6
            HSSFCell cell = sheet.getRow(i).createCell(6);
            
            //check if confirmation message is displayed
            if (confirmationMessage.isDisplayed()) {
                // if the message is displayed , write PASS in the excel sheet
                cell.setCellValue("PASS");
                
            } else {
                //if the message is not displayed , write FAIL in the excel sheet
                cell.setCellValue("FAIL");
            }
            
            // Write the data back in the Excel file
            FileOutputStream outputStream = new FileOutputStream("E:\\TestData\\TestData.xls");
            wb.write(outputStream);

            //close the confirmation popup
            WebElement closebtn = driver.findElement(By.id("closeLargeModal"));
            closebtn.click();
            
            //wait for page to come back to registration page after close button is clicked
            driver.manage().timeouts().implicitlyWait(2000, TimeUnit.SECONDS);
        }
        
        //Close the workbook
        wb.close();
        
        //Quit the driver
        driver.quit();
        }
}
```

我们可以通过打开我们在代码中使用的 excel 文件来查看上述代码的输出(在我们的例子中为“E:\TestData\TestData.xls”)：

![使用 Apache POI 将数据写入同一行](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Writing%20data%20to%20same%20row%20using%20Apache%20POI.png)

如上图所示，在对给定时间注册的学生数据执行测试后， PASS被写入Excel文件。

现在假设，如果我们需要将数据完全写入一个新行，我们也可以使用Apache POI 来实现同样的目的。随后，让我们看看如何将数据写入新行中的新单元格？

### 如何写入新行中的新单元格？

Apache POI工作表类提供了创建新行以及这些行中的单元格的方法。此外，你可以在 Excel 工作表中创建一个新行，如下所示：

```java
HSSFRow row3=sheet.createRow(3);
```

创建行后，我们可以创建单元格并在excel表中输入数据，如下图-

```java
row3.createCell(0).setCellValue("Diana");
row3.createCell(1).setCellValue("Jane");
row3.createCell(2).setCellValue("Female");
```

随后，让我们通过在示例 Excel 中创建一个新行来在工作表中写入其他学生数据：

```java
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteToExcel {
    public static  void main(String args[]) throws IOException {
        
    	//Create an object of File class to open xlsx file
        File file =    new File("E:\\TestData\\TestData.xls");
        
        //Create an object of FileInputStream class to read excel file
        FileInputStream inputStream = new FileInputStream(file);
        
        //creating workbook instance that refers to .xls file
        HSSFWorkbook wb=new HSSFWorkbook(inputStream);
        
        //creating a Sheet object using the sheet Name
        HSSFSheet sheet=wb.getSheet("STUDENT_DATA");
        
        //Create a row object to retrieve row at index 3
        HSSFRow row2=sheet.createRow(3);
        
        //create a cell object to enter value in it using cell Index
        row2.createCell(0).setCellValue("Diana");
        row2.createCell(1).setCellValue("Jane");
        row2.createCell(2).setCellValue("djanes@gmail.com");
        row2.createCell(3).setCellValue("Female");
        row2.createCell(4).setCellValue("8786858432");
        row2.createCell(5).setCellValue("Park Lane, Flat C1 , New Jersey");
        
        //write the data in excel using output stream
        FileOutputStream outputStream = new FileOutputStream("E:\\TestData\\TestData.xls");
        wb.write(outputStream);
        wb.close();

    }
}
```

因此，上述代码的输出将出现在Excel 工作表中，如下所示 -

![使用 Apache POI 写入新行](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Write%20in%20a%20new%20row%20using%20Apache%20POI.png)

如上图所示，我们向 Excel 工作表添加了一个额外的行，其详细信息与代码中的相同。