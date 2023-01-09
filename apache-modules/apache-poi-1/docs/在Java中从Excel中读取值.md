## 一 、概述

对于 Microsoft Excel 文件，从不同单元格读取值可能有点棘手。Excel文件是以行和单元格组织的电子表格，其中可以包含字符串、数字、日期、布尔值，甚至公式值。[Apache POI](https://www.baeldung.com/java-microsoft-excel)是一个库，提供全套工具来处理不同的 excel 文件和值类型。

在本教程中，我们将重点学习如何处理 excel 文件、遍历行和单元格，以及使用正确的方法读取每个单元格值类型。

## 2.Maven依赖

让我们从将 Apache POI 依赖项添加到pom.xml开始：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.0</version>
</dependency>
```

可以在 Maven Central 找到最新版本的 [poi-ooxml 。](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.poi" AND a%3A"poi-ooxml")

## 3. Apache POI 概述

层次结构从代表整个 Excel 文件的工作簿开始。每个文件都可以包含一个或多个工作表，这些工作表是行和单元格的集合。根据 excel 文件的版本，HSSF 是表示旧 Excel 文件 ( .xls ) 的类的前缀，而 XSSF 用于表示最新版本 ( .xlsx )。因此我们有：

-   XSSFWorkbook和HSSFWorkbook类表示 Excel 工作簿
-   Sheet界面代表 Excel 工作表
-   Row接口表示行
-   Cell接口表示单元格

### 3.1. 处理 Excel 文件

首先，我们打开要读取的文件并将其转换为FileInputStream以供进一步处理。FileInputStream构造函数抛出一个java.io.FileNotFoundException，所以我们需要将它包裹在一个 try-catch 块中并在最后关闭流：

```java
public static void readExcel(String filePath) {
    File file = new File(filePath);
    try {
        FileInputStream inputStream = new FileInputStream(file);
        ...
        inputStream.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

```

### 3.2. 遍历 Excel 文件

在我们成功打开InputStream之后，是时候创建XSSFWorkbook 并遍历每个工作表的行和单元格了。如果我们知道工作表的确切数量或特定工作表的名称，我们可以分别使用 XSSFWorkbook 的getSheetAt(int index)和getSheet(String sheetName ) 方法。

由于我们想要通读任何类型的 Excel 文件，我们将使用三个嵌套的 for 循环遍历所有工作表，一个用于工作表，一个用于每个工作表的行，最后一个用于每个工作表的单元格。

为了本教程的缘故，我们只会将数据打印到控制台：

```java
FileInputStream inputStream = new FileInputStream(file);
Workbook baeuldungWorkBook = new XSSFWorkbook(inputStream);
for (Sheet sheet : baeuldungWorkBook) {
...
}

```

然后，为了遍历工作表的行，我们需要找到从工作表对象中获取的第一行和最后一行的索引：

```java
int firstRow = sheet.getFirstRowNum();
int lastRow = sheet.getLastRowNum();
for (int index = firstRow + 1; index <= lastRow; index++) {
    Row row = sheet.getRow(index);
}
```

最后，我们对细胞做同样的事情。此外，在访问每个单元格时，我们可以选择传递一个MissingCellPolicy，它基本上告诉 POI 在单元格值为空或 null 时返回什么。MissingCellPolicy枚举包含三个枚举值：

-   RETURN_NULL_AND_BLANK
-   RETURN_BLANK_AS_NULL
-   CREATE_NULL_AS_BLANK ;

单元格迭代的代码如下：

```java
for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
    Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    ...
}
```

### 3.3. 在 Excel 中读取单元格值

正如我们之前提到的，Microsoft Excel 的单元格可以包含不同的值类型，因此能够将一种单元格值类型与另一种区分开来并使用适当的方法提取值非常重要。下面是所有值类型的列表：

-   没有任何
-   数字
-   细绳
-   公式
-   空白的
-   布尔值
-   错误

我们将关注四种主要的单元格值类型：数字、字符串、布尔值和公式，其中最后一种包含前三种类型的计算值。

让我们创建一个辅助方法，它基本上会检查每个值类型，并基于该方法使用适当的方法来访问该值。也可以[将单元格值视为字符串并](https://www.baeldung.com/java-apache-poi-cell-string-value)使用相应的方法检索它。

有两件重要的事情值得注意。首先，日期值存储为数字值，而且如果单元格的值类型是FORMULA，我们需要使用getCachedFormulaResultType()而不是getCellType()方法来[检查公式的计算结果](https://www.baeldung.com/apache-poi-read-cell-value-formula)：

```java
public static void printCellValue(Cell cell) {
    CellType cellType = cell.getCellType().equals(CellType.FORMULA)
      ? cell.getCachedFormulaResultType() : cell.getCellType();
    if (cellType.equals(CellType.STRING)) {
        System.out.print(cell.getStringCellValue() + " | ");
    }
    if (cellType.equals(CellType.NUMERIC)) {
        if (DateUtil.isCellDateFormatted(cell)) {
            System.out.print(cell.getDateCellValue() + " | ");
        } else {
            System.out.print(cell.getNumericCellValue() + " | ");
        }
    }
    if (cellType.equals(CellType.BOOLEAN)) {
        System.out.print(cell.getBooleanCellValue() + " | ");
    }
}
```

现在，我们需要做的就是在单元格循环中调用printCellValue方法，我们就完成了。这是完整代码的片段：

```java
...
for (int cellIndex = row.getFirstCellNum(); cellIndex < row.getLastCellNum(); cellIndex++) {
    Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    printCellValue(cell);
}
...
```

## 4. 总结

在本文中，我们展示了一个使用 Apache POI 读取 Excel 文件和访问不同单元格值的示例项目。