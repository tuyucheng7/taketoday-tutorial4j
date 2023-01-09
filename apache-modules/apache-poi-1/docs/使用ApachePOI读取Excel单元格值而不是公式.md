## 1. 简介

在用Java读取 Excel 文件时，我们通常希望读取单元格的值来执行一些计算或生成报告。然而，我们可能会遇到一个或多个包含公式而不是原始数据值的单元格。那么，我们如何获得这些单元格的实际数据值呢？

在本教程中，我们将研究使用[Apache POI](https://www.baeldung.com/java-microsoft-excel)Java库读取 Excel 单元格值的不同方法——而不是计算单元格值的公式。

有两种方法可以解决这个问题：

-   获取单元格的最后一个缓存值
-   在运行时计算公式以获取单元格值

## 2.Maven依赖

我们需要在 Apache POI 的 pom.xml 文件中添加以下依赖项：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.0</version>
</dependency>
```

 可以从 Maven Central 下载最新版本的[poi-ooxml 。](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.poi" AND a%3A"poi-ooxml")

## 3.获取最后缓存的值

当公式计算其值时，Excel 会为单元格存储两个对象。一是公式本身，二是缓存值。缓存值包含公式计算的最后一个值。

所以这里的想法是我们可以获取最后一个缓存值并将其视为单元格值。最后缓存的值可能并不总是正确的单元格值。但是，当我们使用已保存的 Excel 文件并且最近没有对该文件进行修改时，最后缓存的值应该是单元格值。

让我们看看如何获取单元格的最后一个缓存值：

```java
FileInputStream inputStream = new FileInputStream(new File("temp.xlsx"));
Workbook workbook = new XSSFWorkbook(inputStream);
Sheet sheet = workbook.getSheetAt(0);

CellAddress cellAddress = new CellAddress("C2");
Row row = sheet.getRow(cellAddress.getRow());
Cell cell = row.getCell(cellAddress.getColumn());

if (cell.getCellType() == CellType.FORMULA) {
    switch (cell.getCachedFormulaResultType()) {
        case BOOLEAN:
            System.out.println(cell.getBooleanCellValue());
            break;
        case NUMERIC:
            System.out.println(cell.getNumericCellValue());
            break;
        case STRING:
            System.out.println(cell.getRichStringCellValue());
            break;
    }
}
```

## 4. 计算公式以获得单元格值

Apache POI 提供了一个FormulaEvaluator 类，它使我们能够计算Excel 工作表中公式的结果。

因此，我们可以使用FormulaEvaluator 直接在运行时计算单元格值。FormulaEvaluator 类提供了一个名为evaluateFormulaCell 的方法，该方法计算给定Cell对象的单元格值并返回一个CellType 对象，该对象表示单元格值的数据类型。

让我们看看这种方法的实际应用：

```java
// existing Workbook setup

FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator(); 

// existing Sheet, Row, and Cell setup

if (cell.getCellType() == CellType.FORMULA) {
    switch (evaluator.evaluateFormulaCell(cell)) {
        case BOOLEAN:
            System.out.println(cell.getBooleanCellValue());
            break;
        case NUMERIC:
            System.out.println(cell.getNumericCellValue());
            break;
        case STRING:
            System.out.println(cell.getStringCellValue());
            break;
    }
}

```

## 5.选择哪种方法

这两种方法之间的简单区别在于第一种方法使用最后缓存的值，第二种方法在运行时计算公式。

如果我们正在使用已保存的 Excel 文件，并且我们不打算在运行时更改该电子表格，那么缓存值方法更好，因为我们不必计算公式。

但是，如果我们知道我们将在运行时进行频繁更改，那么最好在运行时评估公式以获取单元格值。

## 六. 总结

在这篇快速文章中，我们看到了两种获取 Excel 单元格值的方法，而不是计算它的公式。