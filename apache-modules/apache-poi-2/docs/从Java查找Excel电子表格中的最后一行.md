## **一、概述**

在本教程中，我们将讨论如何使用 Java 和Apache POI查找 Excel 电子表格中的最后一行。

首先，我们将了解如何使用 Apache POI 从文件中获取一行。然后，我们将研究计算工作表中所有行的方法。最后，我们将它们组合起来以获取给定工作表的最后一行。

## **2.获取单行**

正如我们已经知道**的****， Apache POI****提供了一个抽象层来用 Java 表示 Microsoft 文档**，[包括 Excel](https://www.baeldung.com/java-microsoft-excel)。我们可以访问文件中的工作表，甚至读取和修改每个单元格。

让我们从我们的 Excel 文件中获取一行开始。在我们继续之前，我们需要从文件中获取*工作表*：

```java
Workbook workbook = new XSSFWorkbook(fileLocation);
Sheet sheet = workbook.getSheetAt(0);
```

***Workbook*****是 Excel 文件的 Java 表示，而**Sheet***是*Workbook 中的主要*****结构**。**Worksheet*是*Sheet*最常见的子类型，代表单元格网格。

当我们在 Java 中打开我们的工作表时，我们可以访问它包含的数据，即行数据。要获取单行，我们可以使用[*getRow(int)*](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Sheet.html#getRow-int-)方法：

```java
Row row = sheet.getRow(2);
```

**该**方法返回***Row*****对象——Excel 文件中单行的高级表示，**如果该行不存在则返回 null。

如我们所见，我们需要提供一个参数，即所请求行的索引(从 0 开始）。不幸的是，没有可用于直接获取最后一行的 API。

## **3. 计算行数**

我们刚刚学习了如何使用 Java 从 Excel 文件中获取一行。*现在，让我们找到给定工作表*上最后一行的索引。

Apache POI 提供了两种帮助计算行数的方法：*getLastRowNum()*和*getPhysicalNumberOfRows()*。让我们来看看它们中的每一个。

### **3.1. 使用*getLastRowNum()***

根据文档，[*getLastRowNum()*](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Sheet.html#getLastRowNum--)方法返回工作表上最后一个初始化行的编号(从 0 开始），如果不存在任何行，则返回 -1：

```java
int lastRowNum = sheet.getLastRowNum();
```

一旦我们获取了*lastRowNum ，我们现在应该可以使用**getRow()*方法轻松访问最后一行。

我们应该注意，**之前有内容但后来设置为空的行可能仍被算作行**。因此，结果可能不如预期。要理解这一点，我们需要更多地了解物理行。

### **3.2. 使用*getPhysicalNumberOfRows()***

查看 Apache POI 文档，我们可以找到一个与行相关的特殊术语——物理行。

只要一行包含任何数据，它总是被解释为物理行。**不仅如果该行中的任何单元格包含文本或公式，而且如果它们有一些关于格式的数据**，例如背景颜色、行高或使用的非默认字体，该行都会被初始化。换句话说，**初始化的每一行也是物理的**。

为了获取物理行数，Apache POI 提供了[*getPhysicalNumberOfRows()*](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Sheet.html#getPhysicalNumberOfRows--)方法：

```
int physicalRows = sheet.getPhysicalNumberOfRows();
```

根据*物理行的解释，结果可能与使用**getLastRowNum()*方法获得的数字不同。

## **4.获取最后一行**

现在，让我们针对更复杂的 Excel 网格测试这两种方法：

[![百隆最后一排](https://www.baeldung.com/wp-content/uploads/2022/03/baeldung-lastrow.jpg)](https://www.baeldung.com/wp-content/uploads/2022/03/baeldung-lastrow.jpg)

此处，前导行包含文本数据、由公式 ( *=A1* ) 计算的值以及相应更改的背景颜色。然后，第 4 行修改了高度，而第 5 行和第 6 行保持不变。第 7 行再次包含文本。在第 8 行，文本之前被格式化但后来被清除。第 9 行及后续行未被编辑。

让我们检查一下计数方法的结果：

```java
assertEquals(7, sheet.getLastRowNum());
assertEquals(6, sheet.getPhysicalNumberOfRows());
```

正如我们之前提到的，在某些情况下**最后的行号和物理行数是不同的。**

现在让我们根据索引获取行：

```java
assertNotNull(sheet.getRow(0)); // data
assertNotNull(sheet.getRow(1)); // formula
assertNotNull(sheet.getRow(2)); // green
assertNotNull(sheet.getRow(3)); // height
assertNull(sheet.getRow(4));
assertNull(sheet.getRow(5));
assertNotNull(sheet.getRow(6)); // last?
assertNotNull(sheet.getRow(7)); // cleared later
assertNull(sheet.getRow(8));
...
```

如我们所见，***getPhysicalNumberOfRows() 返回工作表中非空(即已初始化）**行*****的总数**。***getLastRowNum()*值是最后一个非空*行***的索引。

因此，我们可以获取工作表的最后一行：

```java
Row lastRow = null;
int lastRowNum = sheet.getLastRowNum();
if (lastRowNum >= 0) {
    lastRow = sheet.getRow(lastRowNum);
}
```

但是，我们必须记住，**Apache POI 返回的最后一行并不总是显示文本或公式的行**，尤其是在某些 UI 编辑器(如 Microsoft Excel）中。

## **5.结论**

在本文中，我们检查了 Apache POI API 并从给定的 Excel 文件中获取了最后一行。

我们首先回顾了一些在 Java 中打开电子表格的基本方法。然后我们引入了*getRow(int)*方法来检索单个*Row*。之后，我们检查了*getLastRowNum()*和 *getPhysicalNumberOfRows()*的值并解释了它们的区别。最后，我们针对 Excel 网格检查了所有方法以获取最后一行。