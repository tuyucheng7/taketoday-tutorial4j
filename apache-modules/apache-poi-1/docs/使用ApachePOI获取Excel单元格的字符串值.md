## 1. 概述

[Microsoft Excel](https://www.baeldung.com/java-microsoft-excel)单元格可以具有不同的类型，例如字符串、数字、布尔值和公式。

在本快速教程中，我们将展示如何使用 Apache POI 将单元格值读取为字符串(无论单元格类型如何)。

## 2.Apache 兴趣点

首先，我们首先需要将[poi](https://search.maven.org/search?q=g:org.apache.poi a:poi)依赖项添加到我们的项目pom.xml 文件中：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.0</version>
</dependency>
```

Apache POI 使用 [Workbook ](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Workbook.html)接口来表示 Excel 文件。它还使用[Sheet](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Sheet.html)、 [Row](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Row.html)和 [Cell ](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Cell.html)接口对 Excel 文件中不同级别的元素进行建模。在Cell层面，我们可以使用它的[getCellType()](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Cell.html#getCellType--)方法来获取[单元格类型](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/CellType.html)。Apache POI 支持以下单元格类型：

-   空白的
-   布尔值
-   错误
-   公式
-   数字
-   细绳

如果我们想在屏幕上显示 Excel 文件内容，我们希望获取单元格的字符串表示形式，而不是其原始值。因此，对于非STRING类型的单元格，我们需要将其数据转换为字符串值。

## 3.获取单元格字符串值

我们可以使用[DataFormatter](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/DataFormatter.html) 来获取 Excel 单元格的字符串值。它可以获得存储在单元格中的值的格式化字符串表示形式。例如，如果一个单元格的数值是 1.234，并且该单元格的格式规则是两位小数，我们将得到字符串表示形式“1.23”：

```java
Cell cell = // a numeric cell with value of 1.234 and format rule "0.00"

DataFormatter formatter = new DataFormatter();
String strValue = formatter.formatCellValue(cell);

assertEquals("1.23", strValue);
```

因此，DataFormatter.formatCellValue()的结果是显示字符串，与它在 Excel 中显示的完全一样。

## 4. 获取公式单元格的字符串值

如果单元格的类型是 FORMULA，前面的方法将返回原始公式字符串，而不是计算公式值。因此，要获得公式值的字符串表示，我们需要使用[FormulaEvaluator](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/FormulaEvaluator.html)来评估公式：

```java
Workbook workbook = // existing Workbook setup
FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

Cell cell = // a formula cell with value of "SUM(1,2)"

DataFormatter formatter = new DataFormatter();
String strValue = formatter.formatCellValue(cell, evaluator);

assertEquals("3", strValue);
```

此方法对所有细胞类型通用。如果单元格类型是 FORMULA，我们将使用给定的FormulaEvaluator对其进行评估。否则，我们将返回字符串表示而不进行任何评估。

## 5.总结

在这篇快速文章中，我们展示了如何获取 Excel 单元格的字符串表示形式，而不考虑其类型。