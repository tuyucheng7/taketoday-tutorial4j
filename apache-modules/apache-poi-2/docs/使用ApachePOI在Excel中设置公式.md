## 1. 概述

在本快速教程中，我们将通过一个简单示例讨论如何使用 [Apache POI](https://poi.apache.org/)在 Microsoft Excel 电子表格中设置公式。

## 2.Apache 兴趣点

Apache POI 是一个流行的开源Java库，它为程序员提供 API 来创建、修改和显示 MS Office文件。

它使用[工作簿](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Workbook.html)来表示 Excel 文件及其元素。Excel 文件中的[单元格](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Cell.html)可以具有不同的类型，例如FORMULA。

为了查看 Apache POI 的运行情况，我们将设置一个公式来减去[Excel](https://github.com/eugenp/tutorials/blob/master/apache-poi-2/src/main/resources/com/baeldung/poi/excel/setformula/SetFormulaTest.xlsx)文件中 A 列和 B 列中的值之和。链接文件包含以下数据：

[![示例 Excel 文件](https://www.baeldung.com/wp-content/uploads/2020/07/Sample-Excel-File-1-300x223-1-1.png)](https://www.baeldung.com/wp-content/uploads/2020/07/Sample-Excel-File-1-300x223-1-1.png)

## 3.依赖关系

首先，我们需要将 POI 依赖项添加到我们的项目pom.xml文件中。要使用 Excel 2007+ 工作簿，我们应该使用[poi-ooxml](https://search.maven.org/search?q=g:org.apache.poi a:poi-ooxml)：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.0</version>
</dependency>
```

请注意，对于早期版本的 Excel，我们应该改用[poi](https://search.maven.org/search?q=g:org.apache.poi a:poi)依赖项。

## 4. 单元格查找

首先，让我们首先打开我们的文件并构建适当的工作簿：

```java
FileInputStream inputStream = new FileInputStream(new File(fileLocation));
XSSFWorkbook excel = new XSSFWorkbook(inputStream);
```

然后，我们需要创建或查找我们将要使用的单元格。使用之前共享的数据，我们要编辑单元格 C1。

这是在第一张纸和第一行上，我们可以向 POI 询问第一个空白列：

```java
XSSFSheet sheet = excel.getSheetAt(0);
int lastCellNum = sheet.getRow(0).getLastCellNum();
XSSFCell formulaCell = sheet.getRow(0).createCell(lastCellNum + 1);
```

## 5.公式

接下来，我们要在我们查找的单元格上设置一个公式。

如前所述，让我们从 A 列的总和中减去 B 列的总和。在 Excel 中，这将是：

```java
=SUM(A:A)-SUM(B:B)
```

我们可以使用 setCellFormula方法将其写入我们 的formulaCell：

```java
formulaCell.setCellFormula("SUM(A:A)-SUM(B:B)");
```

现在，这不会评估公式。为此，我们需要使用 POI 的XSSFFormulaEvaluator：

```java
XSSFFormulaEvaluator formulaEvaluator = 
  excel.getCreationHelper().createFormulaEvaluator();
formulaEvaluator.evaluateFormulaCell(formulaCell);
```

结果将设置在下一个空列的第一个单元格 中：[![示例 Excel 文件 2](https://www.baeldung.com/wp-content/uploads/2020/07/Sample-Excel-File2-1-300x200.png)](https://www.baeldung.com/wp-content/uploads/2020/07/Sample-Excel-File2-1.png)

如我们所见，计算结果并保存在 C 列的第一个单元格中。公式也显示在公式栏中。

请注意，[FormulaEvaluator](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/FormulaEvaluator.html)类为我们提供了在 Excel 工作簿中计算FORMULA的其他方法，例如evaluateAll，它将遍历所有单元格并对其进行计算。

## 六. 总结

在本教程中，我们展示了如何使用 Apache POI API 在Java中的 Excel 文件中的单元格上设置公式。