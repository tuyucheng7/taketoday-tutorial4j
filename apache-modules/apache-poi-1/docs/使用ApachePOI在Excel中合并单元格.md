## 1. 概述

在本教程中，我们将展示如何使用 Apache POI合并[Excel中的单元格。](https://www.baeldung.com/java-microsoft-excel)

## 2.Apache 兴趣点

首先，我们首先需要将[poi](https://search.maven.org/search?q=g:org.apache.poi a:poi)依赖项添加到我们的项目pom.xml 文件中：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.0</version>
</dependency>
```

Apache POI 使用 [Workbook ](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Workbook.html)接口来表示 Excel 文件。它还使用[Sheet](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Sheet.html)、 [Row](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Row.html)和 [Cell ](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Cell.html)接口对 Excel 文件中不同级别的元素进行建模。

## 3.合并单元格

在 Excel 中，我们有时希望在两个或多个单元格中显示一个字符串。例如，我们可以水平合并多个单元格以创建一个跨越多列的表格标题：

[![合并](https://www.baeldung.com/wp-content/uploads/2020/01/merge.png)](https://www.baeldung.com/wp-content/uploads/2020/01/merge.png)

为此，我们可以使用[addMergedRegion](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Sheet.html#addMergedRegion-org.apache.poi.ss.util.CellRangeAddress-)合并由[CellRangeAddress](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/util/CellRangeAddress.html)定义的多个单元格。 有两种设置单元格范围的方法。首先，我们可以使用四个从零开始的索引来定义左上角的单元格位置和右下角的单元格位置：

```java
sheet = // existing Sheet setup
int firstRow = 0;
int lastRow = 0;
int firstCol = 0;
int lastCol = 2
sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
```

我们还可以使用单元格范围引用字符串来提供合并区域：

```java
sheet = // existing Sheet setup
sheet.addMergedRegion(CellRangeAddress.valueOf("A1:C1"));
```

如果单元格在我们合并它们之前有数据，Excel 将使用左上角的单元格值作为合并区域值。对于其他单元格，Excel 将丢弃它们的数据。

当我们在一个 Excel 文件上添加多个合并区域时，我们不应该创建任何重叠。否则，Apache POI 将在运行时抛出异常。

## 4.总结

在这篇快速文章中，我们展示了如何使用 Apache POI 合并多个单元格。我们还讨论了两种定义合并区域的方法。