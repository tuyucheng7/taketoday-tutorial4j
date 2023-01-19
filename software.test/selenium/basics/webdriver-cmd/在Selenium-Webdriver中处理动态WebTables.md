## 什么是 HTML 中的表格？

表格是一种HTML数据，借助于`<table>`tag和tag 来显示。尽管还有其他用于创建表格的标记，但这些是在 HTML 中创建表格的基础知识。这里的标签定义了行，标签定义了表格的列。`<tr>``<td>``<tr>``<td>`

Excel 工作表是表格结构的一个简单示例。每当我们将一些数据放入 excel 中时，我们也会给它们一些标题。在 HTML 中，我们使用`<th>`标题标签来定义表格的标题。

Excel 工作表中的每个单元格都可以表示为`<td>`HTML 表格。元素是数据容器，`<td>`它们可以包含各种 HTML 元素，如文本、图像、列表、其他表格等。

查看下面的 HTML 表格的简单示例，其中第一行定义为标题，后面两行包含数据。

```java
<table>
	<tbody>
		<tr>
			  <th>Automation Tool</th>
			  <th>Licensing</th> 
			  <th>Market response</th>
		 </tr>
		<tr>
			  <td>Selenium</td>
			  <td>Free</td>
			  <td>In</td>
		</tr>
		<tr>
			  <td>QTP</td>
			  <td>Paid</td>
			  <td>Out</td>
		</tr>
	</tbody>
</table>
```

| 自动化工具 | 许可 | 市场反应 |
| ---------------- | ---------- | -------------- |
| 硒               | 自由的     | 在             |
| QTP              | 有薪酬的   | 出去           |

## 在 Selenium Webdriver 中处理动态 WebTables

在处理表格方面没有火箭科学。你所需要做的就是检查表格单元格并获取它的 HTML 位置。在大多数情况下，表格包含文本数据，你可能只是想提取表格每一行或每一列中给定的数据。但有时表格也有链接或图像，如果你能找到包含单元格的 HTML 位置，你就可以轻松地对这些元素执行任何操作。

### 示例 1：

让我们以上表为例，在上例中选择第 2 行的第 1 列为“ Selenium ”：

```java
/html/body/div[1]/div[2]/div/div[2]/article/div/table/tbody/tr[2]/td[1]
```

如果我们将这个 xpath 分成三个不同的部分，它将是这样的

-   第 1 部分- 表格在网页中的位置 </html/body/div[1]/div[2]/div/div[2]/article/div/>
-   第 2 部分- 表体(数据)从这里开始 <table/tbody/>
-   第 3 部分- 它表示表格第 2 行和表格第 1 列 <tr[2]/td[1]>

如果你使用此 xpath，你将能够获得表格的 Selenium 单元格。怎么办？如何从表格单元格中获取文本“ Selenium ”？

你需要使用 WebDriver 元素的“ getText() ”方法。

```java
driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div[2]/article/div/table/tbody/tr[2]/td[1]")).getText(); 
```

### 示例 2：

表格操作并不总是像上面那样简单，因为表格可以包含大量数据，并且你可能需要在测试用例中动态传递行和列。

在这种情况下，你需要使用变量构建你的 xpath，并且你将以变量的形式将行和列传递给你的 xpath。

```java
String sRow = "2";

String sCol = "1";

driver.findElement(By.xpath("/html/body/div[1]/div[2]/div/div[2]/article/div/table/tbody/tr["+sRow+"]/td["+sCol+"]")).getText();
```

### 示例 3：

上面的示例仍然很简单，至少你知道要从表中获取的行号和列号，并且你可以在外部 Excel 工作表或任何数据表源的 xpath 中提供它。但是当行和列本身是动态的并且你所知道的只是任何单元格的文本值并且你想取出该特定单元格的相应值时，你会怎么做。

例如，你所知道的只是上例中的文本“许可”，并且你只想记录该特定列的值，例如免费和付费。

```java
String sColValue = "Licensing";

	//First loop will find the 'ClOCK TWER HOTEL' in the first column
	for (int i=1;i<=3;i++){
		String sValue = null;
		sValue = driver.findElement(By.xpath(".//[@id='post-2924']/div/table/tbody/tr[1]/th["+i+"]")).getText();
		if(sValue.equalsIgnoreCase(sColValue)){
			
			// If the sValue match with the description, it will initiate one more inner loop for all the columns of 'i' row 
			for (int j=1;j<=2;j++){
				String sRowValue= driver.findElement(By.xpath(".//[@id='post-2924']/div/table/tbody/tr["+j+"]/td["+i+"]")).getText();
				System.out.println(sRowValue);
			}
		break;
	}
}
```

### 练习练习 1

1.  启动新浏览器
2.  打开网址“ http://toolsqa.com/automation-practice-table/ ”
3.  从单元格“Dubai”获取值并将其打印在控制台上
4.  单击第一行和最后一列的链接“详细信息”

```java
package automationFramework;
	import java.util.concurrent.TimeUnit;
	import org.openqa.selenium.By;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.firefox.FirefoxDriver;
public class PracticeTables {
	public static void main(String[] args) {
		WebDriver driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://toolsqa.com/automation-practice-table");
		
		//Here we are storing the value from the cell in to the string variable
		String sCellValue = driver.findElement(By.xpath(".//[@id='content']/table/tbody/tr[1]/td[2]")).getText();
		System.out.println(sCellValue);
		
		// Here we are clicking on the link of first row and the last column
		driver.findElement(By.xpath(".//[@id='content']/table/tbody/tr[1]/td[6]/a")).click();        
		System.out.println("Link has been clicked otherwise an exception would have thrown");
		driver.close();
	}
}
```

### 练习练习 2

1.  启动新浏览器
2.  打开网址“ http://toolsqa.com/automation-practice-table/ ”
3.  使用动态 xpath 从单元格“迪拜”获取值
4.  打印“Clock Tower Hotel”行的所有列值

```java
package automationFramework;
	import java.util.concurrent.TimeUnit;
	import org.openqa.selenium.By;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.firefox.FirefoxDriver;
public class PracticeTable_2 {
	public static void main(String[] args) {
		WebDriver driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("https://toolsqa.com/automation-practice-table");
		String sRow = "1";
		String sCol = "2";
		
		//Here we are locating the xpath by passing variables in the xpath
		String sCellValue = driver.findElement(By.xpath(".//[@id='content']/table/tbody/tr[" + sRow + "]/td[" + sCol + "]")).getText();
		System.out.println(sCellValue);
		String sRowValue = "Clock Tower Hotel";
		
		//First loop will find the 'ClOCK TWER HOTEL' in the first column
		for (int i=1;i<=5;i++){
			String sValue = null;
			sValue = driver.findElement(By.xpath(".//[@id='content']/table/tbody/tr[" + i + "]/th")).getText();
				if(sValue.equalsIgnoreCase(sRowValue)){
					// If the sValue match with the description, it will initiate one more inner loop for all the columns of 'i' row 
					for (int j=1;j<=5;j++){
						String sColumnValue= driver.findElement(By.xpath(".//[@id='content']/table/tbody/tr[" + i + "]/td["+ j +"]")).getText();
						System.out.println(sColumnValue);
					}
				break;
				}
			}
		driver.close();
	}
}
```