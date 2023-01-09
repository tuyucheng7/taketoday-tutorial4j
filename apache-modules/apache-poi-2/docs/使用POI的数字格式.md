## 1. 概述

在本快速教程中，我们将演示如何使用 Apache POI 在 Excel 中格式化数字单元格。

## 2.Apache 兴趣点

[Apache POI](https://poi.apache.org/)是一个开源的纯Java项目。它提供了用于读取和写入 Microsoft Office 格式文件的库，例如 Word、PowerPoint 和[Excel](https://www.baeldung.com/java-microsoft-excel)。

在处理较新的.xlsx 文件格式时，我们将使用 XSSFWorkbook类，而对于.xls 格式，我们使用 HSSFWorkbook 类。

## 3. 数值格式

Apache POI的setCellValue方法只接受double作为输入或可以隐式转换为double并返回double作为数值的输入。setCellStyle方法用于添加所需的样式。Excel 数字格式中的 # 字符表示需要时在此处放置数字，而字符 0 表示始终在此处放置数字，即使它是不必要的 0。

### 3.1. 仅显示值的格式

让我们使用 0.00 或 #.## 等模式格式化双精度值。首先，我们将创建一个简单的实用方法来格式化单元格值：

```java
public static void applyNumericFormat(Workbook outWorkbook, Row row, Cell cell, Double value, String styleFormat) {
    CellStyle style = outWorkbook.createCellStyle();
    DataFormat format = outWorkbook.createDataFormat();
    style.setDataFormat(format.getFormat(styleFormat));
    cell.setCellValue(value);
    cell.setCellStyle(style);
}
```

让我们验证一个简单的代码来验证上述方法：

```java
File file = new File("number_test.xlsx");
try (Workbook outWorkbook = new XSSFWorkbook()) {
    Sheet sheet = outWorkbook.createSheet("Numeric Sheet");
    Row row = sheet.createRow(0);
    Cell cell = row.createCell(0);
    ExcelNumericFormat.applyNumericFormat(outWorkbook, row, cell, 10.251, "0.00");
    FileOutputStream fileOut = new FileOutputStream(file);
    outWorkbook.write(fileOut);
    fileOut.close();
}
```

这将在电子表格中添加数字单元格：

[![四舍五入值](https://www.baeldung.com/wp-content/uploads/2021/12/RoundedValue.png)](https://www.baeldung.com/wp-content/uploads/2021/12/RoundedValue.png)

注：显示值为格式化后的值，实际值保持不变。如果我们尝试访问同一个单元格，我们仍然会得到 10.251。

让我们验证实际值：

```java
try (Workbook inWorkbook = new XSSFWorkbook("number_test.xlsx")) {
    Sheet sheet = inWorkbook.cloneSheet(0);
    Row row = sheet.getRow(0);
    Assertions.assertEquals(10.251, row.getCell(0).getNumericCellValue());
}
```

### 3.2. 实际值和显示值的格式

让我们使用模式格式化显示值和实际值：

```java
File file = new File("number_test.xlsx");
try (Workbook outWorkbook = new HSSFWorkbook()) {
    Sheet sheet = outWorkbook.createSheet("Numeric Sheet");
    Row row = sheet.createRow(0);
    Cell cell = row.createCell(0);
    DecimalFormat df = new DecimalFormat("#,###.##");
    ExcelNumericFormat.applyNumericFormat(outWorkbook, row, cell, Double.valueOf(df.format(10.251)), "#,###.##");
    FileOutputStream fileOut = new FileOutputStream(file);
    outWorkbook.write(fileOut);
    fileOut.close();
}
```

这将在电子表格中添加数字单元格并显示格式化值，而且，它还会更改实际值：

[![excel 数字](https://www.baeldung.com/wp-content/uploads/2021/12/excel-numeric.png)](https://www.baeldung.com/wp-content/uploads/2021/12/excel-numeric.png)

让我们断言上述情况中的实际值：

```java
try (Workbook inWorkbook = new XSSFWorkbook("number_test.xlsx")) {
    Sheet sheet = inWorkbook.cloneSheet(0);
    Row row = sheet.getRow(0);
    Assertions.assertEquals(10.25, row.getCell(0).getNumericCellValue());
}
```

## 4. 总结

在本文中，我们演示了如何在更改或不更改 Excel 工作表中数字单元格的实际值的情况下显示格式化值。