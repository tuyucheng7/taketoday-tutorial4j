## 1. 概述

在本教程中，我们将展示如何使用 Apache POI向[Excel文件中的工作表添加列。](https://www.baeldung.com/java-microsoft-excel)

## 2.Apache 兴趣点

首先，我们首先需要将 [poi-ooxml](https://search.maven.org/search?q=g:org.apache.poi a:poi)依赖项添加到项目的pom.xml 文件中：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.0.0</version>
</dependency>
```

Apache POI 使用 [Workbook ](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Workbook.html)接口来表示 Excel 文件。它还使用 [Sheet](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Sheet.html)、 [Row](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Row.html)和 [Cell ](https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/Cell.html)接口对 Excel 文件中的不同元素建模。

## 3.添加新列

在 Excel 中，我们有时想在现有行上添加一个新列。为此，我们可以遍历每一行并在行尾创建一个新单元格：

```java
void addColumn(Sheet sheet, CellType cellType) {
    for (Row currentRow : sheet) {
        currentRow.createCell(currentRow.getLastCellNum(), cellType);
    }
}
```

在此方法中，我们使用循环遍历输入 Excel 工作表的所有行。对于每一行，我们首先找到它的最后一个单元格编号，然后在最后一个单元格之后创建一个新单元格。

## 4.总结

在这篇快速文章中，我们展示了如何使用 Apache POI 添加新列。