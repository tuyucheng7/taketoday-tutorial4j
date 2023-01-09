## 1. 概述

在本教程中，我们将演示如何使用Apache POI 和 JExcel API 处理 Excel 电子表格。

这两个库都可用于动态读取、写入和修改 Excel 电子表格的内容，并提供将 Microsoft Excel 集成到Java应用程序中的有效方法。

## 延伸阅读：

## [使用 Apache POI 在 Excel 中插入一行](https://www.baeldung.com/apache-poi-insert-excel-row)

了解如何使用 Apache POI 库在 Excel 文件的两行之间插入新行

[阅读更多](https://www.baeldung.com/apache-poi-insert-excel-row)→

## [使用 Apache POI 向 Excel 工作表添加列](https://www.baeldung.com/java-excel-add-column)

了解如何使用Java和 Apache POI 库向 Excel 工作表添加列。

[阅读更多](https://www.baeldung.com/java-excel-add-column)→

## [在Java中从 Excel 中读取值](https://www.baeldung.com/java-read-dates-excel)

了解如何使用 Apache POI 访问不同的单元格值。

[阅读更多](https://www.baeldung.com/java-read-dates-excel)→

## 2.Maven依赖

首先，我们需要将以下依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.0</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.0</version>
</dependency>
```

可以从 Maven Central 下载最新版本的[poi-ooxml](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.poi" AND a%3A"poi-ooxml")和[jxls-jexcel 。](https://search.maven.org/classic/#search|ga|1|g%3A"org.jxls" AND a%3A"jxls-jexcel")

## 3.Apache 兴趣点

Apache POI 库同时支持.xls和.xlsx文件，并且是一个比处理 Excel 文件的其他Java库更复杂的库。

它提供了用于建模 Excel 文件的Workbook接口，以及用于建模Excel文件元素的Sheet、Row 和Cell接口，以及两种文件格式的每个接口的实现。

当使用较新的.xlsx文件格式时，我们将使用XSSFWorkbook、XSSFSheet、XSSFRow 和 XSSFCell类。

要使用旧的.xls格式，我们使用HSSFWorkbook、HSSFSheet、HSSFRow和HSSFCell类。

### 3.1. 从 Excel 读取

让我们创建一个方法来打开.xlsx文件，然后从文件的第一张工作表中读取内容。

读取单元格内容的方法因单元格中数据的类型而异。可以使用Cell接口的getCellType()方法确定单元格内容的类型。

首先，让我们从给定位置打开文件：

```java
FileInputStream file = new FileInputStream(new File(fileLocation));
Workbook workbook = new XSSFWorkbook(file);
```

接下来，让我们检索文件的第一张纸并遍历每一行：

```java
Sheet sheet = workbook.getSheetAt(0);

Map<Integer, List<String>> data = new HashMap<>();
int i = 0;
for (Row row : sheet) {
    data.put(i, new ArrayList<String>());
    for (Cell cell : row) {
        switch (cell.getCellType()) {
            case STRING: ... break;
            case NUMERIC: ... break;
            case BOOLEAN: ... break;
            case FORMULA: ... break;
            default: data.get(new Integer(i)).add(" ");
        }
    }
    i++;
}
```

Apache POI 有不同的方法来读取每种类型的数据。让我们扩展上面每个 switch case 的内容。

当单元格类型枚举值为STRING时，将使用Cell接口的getRichStringCellValue()方法读取内容：

```java
data.get(new Integer(i)).add(cell.getRichStringCellValue().getString());
```

具有NUMERIC内容类型的单元格可以包含日期或数字，并按以下方式读取：

```java
if (DateUtil.isCellDateFormatted(cell)) {
    data.get(i).add(cell.getDateCellValue() + "");
} else {
    data.get(i).add(cell.getNumericCellValue() + "");
}
```

对于BOOLEAN值，我们有getBooleanCellValue()方法：

```java
data.get(i).add(cell.getBooleanCellValue() + "");
```

当单元格类型为FORMULA时，我们可以使用getCellFormula()方法：

```java
data.get(i).add(cell.getCellFormula() + "");
```

### 3.2. 写入 Excel

Apache POI 使用上一节中介绍的相同接口写入 Excel 文件，并且比 JExcel 对样式有更好的支持。

让我们创建一个方法，将人员列表写入名为“Persons”的工作表。

首先，我们将创建一个包含“姓名”和“年龄”单元格的标题行并设置样式：

```java
Workbook workbook = new XSSFWorkbook();

Sheet sheet = workbook.createSheet("Persons");
sheet.setColumnWidth(0, 6000);
sheet.setColumnWidth(1, 4000);

Row header = sheet.createRow(0);

CellStyle headerStyle = workbook.createCellStyle();
headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

XSSFFont font = ((XSSFWorkbook) workbook).createFont();
font.setFontName("Arial");
font.setFontHeightInPoints((short) 16);
font.setBold(true);
headerStyle.setFont(font);

Cell headerCell = header.createCell(0);
headerCell.setCellValue("Name");
headerCell.setCellStyle(headerStyle);

headerCell = header.createCell(1);
headerCell.setCellValue("Age");
headerCell.setCellStyle(headerStyle);
```

接下来，让我们用不同的风格来写表格的内容：

```java
CellStyle style = workbook.createCellStyle();
style.setWrapText(true);

Row row = sheet.createRow(2);
Cell cell = row.createCell(0);
cell.setCellValue("John Smith");
cell.setCellStyle(style);

cell = row.createCell(1);
cell.setCellValue(20);
cell.setCellStyle(style);
```

最后，让我们将内容写入当前目录中的“temp.xlsx”文件并关闭工作簿：

```java
File currDir = new File(".");
String path = currDir.getAbsolutePath();
String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

FileOutputStream outputStream = new FileOutputStream(fileLocation);
workbook.write(outputStream);
workbook.close();
```

让我们在JUnit测试中测试上述方法，该测试将内容写入temp.xlsx文件，然后读取同一文件以验证它是否包含我们编写的文本：

```java
public class ExcelIntegrationTest {

    private ExcelPOIHelper excelPOIHelper;
    private static String FILE_NAME = "temp.xlsx";
    private String fileLocation;

    @Before
    public void generateExcelFile() throws IOException {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        fileLocation = path.substring(0, path.length() - 1) + FILE_NAME;

        excelPOIHelper = new ExcelPOIHelper();
        excelPOIHelper.writeExcel();
    }

    @Test
    public void whenParsingPOIExcelFile_thenCorrect() throws IOException {
        Map<Integer, List<String>> data
          = excelPOIHelper.readExcel(fileLocation);

        assertEquals("Name", data.get(0).get(0));
        assertEquals("Age", data.get(0).get(1));

        assertEquals("John Smith", data.get(1).get(0));
        assertEquals("20", data.get(1).get(1));
    }
}
```

## 4. 杰赛克

JExcel库是一个轻量级的库，优点是比Apache POI更容易使用，缺点是只支持处理.xls (1997-2003)格式的Excel文件。

目前，不支持.xlsx文件。

### 4.1. 从 Excel 读取

为了处理 Excel 文件，该库提供了一系列代表 Excel 文件不同部分的类。Workbook类代表整个工作表集合。Sheet类表示单个工作表，Cell类表示电子表格的单个单元格。

让我们编写一个方法，从指定的 Excel 文件创建工作簿，获取文件的第一张工作表，然后遍历其内容并将每一行添加到HashMap中：

```java
public class JExcelHelper {

    public Map<Integer, List<String>> readJExcel(String fileLocation) 
      throws IOException, BiffException {
 
        Map<Integer, List<String>> data = new HashMap<>();

        Workbook workbook = Workbook.getWorkbook(new File(fileLocation));
        Sheet sheet = workbook.getSheet(0);
        int rows = sheet.getRows();
        int columns = sheet.getColumns();

        for (int i = 0; i < rows; i++) {
            data.put(i, new ArrayList<String>());
            for (int j = 0; j < columns; j++) {
                data.get(i)
                  .add(sheet.getCell(j, i)
                  .getContents());
            }
        }
        return data;
    }
}
```

### 4.2. 写入 Excel

为了写入 Excel 文件，JExcel 库提供了与上面使用的类似的类，它们模拟电子表格文件：WritableWorkbook、WritableSheet 和WritableCell。

WritableCell类具有对应于可写入的不同类型内容的子类：Label、DateTime、Number、Boolean、Blank 和Formula。

该库还提供对基本格式设置的支持，例如控制字体、颜色和单元格宽度。

让我们编写一个方法，在当前目录中创建一个名为“temp.xls”的工作簿，然后写入我们在 Apache POI 部分中写入的相同内容。

首先，让我们创建工作簿：

```java
File currDir = new File(".");
String path = currDir.getAbsolutePath();
String fileLocation = path.substring(0, path.length() - 1) + "temp.xls";

WritableWorkbook workbook = Workbook.createWorkbook(new File(fileLocation));
```

接下来，让我们创建第一个工作表并写入 excel 文件的标题，其中包含“姓名”和“年龄”单元格：

```java
WritableSheet sheet = workbook.createSheet("Sheet 1", 0);

WritableCellFormat headerFormat = new WritableCellFormat();
WritableFont font
  = new WritableFont(WritableFont.ARIAL, 16, WritableFont.BOLD);
headerFormat.setFont(font);
headerFormat.setBackground(Colour.LIGHT_BLUE);
headerFormat.setWrap(true);

Label headerLabel = new Label(0, 0, "Name", headerFormat);
sheet.setColumnView(0, 60);
sheet.addCell(headerLabel);

headerLabel = new Label(1, 0, "Age", headerFormat);
sheet.setColumnView(0, 40);
sheet.addCell(headerLabel);
```

使用新的样式，让我们写下我们创建的表格的内容：

```java
WritableCellFormat cellFormat = new WritableCellFormat();
cellFormat.setWrap(true);

Label cellLabel = new Label(0, 2, "John Smith", cellFormat);
sheet.addCell(cellLabel);
Number cellNumber = new Number(1, 2, 20, cellFormat);
sheet.addCell(cellNumber);
```

记住写入文件并在最后关闭它非常重要，这样它就可以被其他进程使用，使用Workbook类的write()和close()方法：

```java
workbook.write();
workbook.close();
```

## 5. 总结

本文说明了如何使用Apache POI API 和JExcel API 从Java程序读取和写入 Excel 文件。