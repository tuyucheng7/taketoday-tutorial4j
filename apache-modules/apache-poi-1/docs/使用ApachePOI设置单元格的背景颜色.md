## 1. 概述

在 Excel 工作表上，当我们通过更改表格标题的背景颜色来突出显示它时，它总是看起来很优雅。本文介绍如何使用[Apache POI](https://poi.apache.org/)更改单元格背景颜色。

此外，我们建议阅读我们的[在Java中使用 Microsoft Excel](https://www.baeldung.com/java-microsoft-excel)教程，以了解在Java中使用 Excel 的一些基础知识。

## 2.Maven依赖

首先，我们需要在我们的pom.xml中添加poi-ooxml作为依赖项：

```xml
<dependency>
     <groupId>org.apache.poi</groupId>
     <artifactId>poi-ooxml</artifactId>
     <version>5.2.0</version>
 </dependency>
```

## 3.改变单元格背景颜色

### 3.1. 关于单元格背景

在 Excel 工作表上，我们可以通过填充颜色或图案来更改单元格背景。在下图中，单元格A1填充了浅蓝色背景，而单元格B1填充了图案。这个图案有黑色背景和上面的浅蓝色斑点：

[![Excel单元格背景颜色](https://www.baeldung.com/wp-content/uploads/2021/11/ExcelCellBackgroundColor-300x78-1.png)](https://www.baeldung.com/wp-content/uploads/2021/11/ExcelCellBackgroundColor-300x78-1.png)

### 3.2. 更改背景颜色的代码

Apache POI 提供了三种改变背景颜色的方法。在CellStyle类中，我们可以为此目的使用setFillForegroundColor、setFillPattern和setFillBackgroundColor方法。颜色列表在IndexedColors类中定义。同样，模式列表在FillPatternType中定义。

有时，名称setFillBackgroundColor可能会误导我们。但是，该方法本身不足以改变细胞背景。要通过填充纯色来更改单元格背景，我们使用setFillForegroundColor和setFillPattern方法。第一个方法告诉填充什么颜色，而第二个方法指定要使用的实心填充图案。

以下代码段是更改单元格背景的示例方法，如单元格A1所示：

```java
public void changeCellBackgroundColor(Cell cell) {
    CellStyle cellStyle = cell.getCellStyle();
    if(cellStyle == null) {
        cellStyle = cell.getSheet().getWorkbook().createCellStyle();
    }
    cellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    cell.setCellStyle(cellStyle);
}

```

要使用图案更改单元格背景，我们需要使用两种颜色：一种颜色填充整个背景，另一种颜色填充第一种颜色之上的图案。在这里，我们需要使用所有这三种方法。

这里使用方法setFillBackgroundColor来指定背景色。仅使用这种方法我们不会得到任何效果。我们需要使用setFillForegroundColor来选择第二种颜色，并使用setFillPattern来说明图案类型。

以下代码段是更改单元格背景的示例方法，如单元格B1所示：

```java
public void changeCellBackgroundColorWithPattern(Cell cell) {
    CellStyle cellStyle = cell.getCellStyle();
    if(cellStyle == null) {
        cellStyle = cell.getSheet().getWorkbook().createCellStyle();
    }
    cellStyle.setFillBackgroundColor(IndexedColors.BLACK.index);
    cellStyle.setFillPattern(FillPatternType.BIG_SPOTS);
    cellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
    cell.setCellStyle(cellStyle);
}
```

有关更多代码细节，请查看整个[Java 类](https://github.com/eugenp/tutorials/blob/5e4e1e4af0917fc73e59860b77a20b4775c453e8/apache-poi/src/main/java/com/baeldung/poi/excel/cellstyle/CellStyleHandler.java)和相关的[JUnit 测试用例](https://github.com/eugenp/tutorials/blob/5e4e1e4af0917fc73e59860b77a20b4775c453e8/apache-poi/src/test/java/com/baeldung/poi/excel/cellstyle/CellStyleHandlerUnitTest.java)。

## 4. 总结

在本快速教程中，我们学习了如何使用 Apache POI 更改 Excel 工作表中单元格的单元格背景。

仅使用三种方法——CellStyle类中的setFillForegroundColor、setFillPattern和setFillBackgroundColor——我们可以轻松更改单元格的背景颜色和填充图案。