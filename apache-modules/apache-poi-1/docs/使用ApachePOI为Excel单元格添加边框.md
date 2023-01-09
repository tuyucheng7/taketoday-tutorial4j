## 1. 概述

在本教程中，我们将学习如何使用[Apache POI](https://poi.apache.org/)Java库向 Excel 工作表添加边框。

有关 excel 处理的更多基础知识，我们可以从使用 [Java 中的 Microsoft Excel](https://www.baeldung.com/java-microsoft-excel)开始。

## 2. Excel 边框

我们可以为一个 Excel 单元格或一系列单元格创建边框。这些边界线可以有多种样式。一些示例样式包括粗线、细线、中线、虚线。为了增加多样性，我们可以有彩色边框。

此图像显示了其中一些品种边界：

[![Excel单元格边框](https://www.baeldung.com/wp-content/uploads/2021/11/ExcelCellBorders.png)](https://www.baeldung.com/wp-content/uploads/2021/11/ExcelCellBorders.png)

-   单元格B2具有粗线边框
-   D2单元格带有紫色宽边框
-   F2单元格有一个疯狂的边框，边框的每一边都有不同的样式和颜色
-   范围B4:F6带有中等大小的边框
-   B8:F9区域为中等大小的橙色边框

## 3. Excel 边框编码

Apache POI 库提供了多种处理边界的方法。一种简单的方法是引用单元格范围并应用边框。

### 3.1. 单元格范围或区域

要引用一系列单元格，我们可以使用CellRangeAddress类：

```java
CellRangeAddress region = new CellRangeAddress(7, 8, 1, 5);
```

CellRangeAddress构造函数采用四个参数第一行、最后一行、第一列和最后一列。每个行和列索引都从零开始。在上面的代码中，它指的是单元格范围B8:F9。

我们还可以使用CellRangeAddress类来引用一个单元格：

```java
CellRangeAddress region = new CellRangeAddress(1, 1, 5, 5);
```

上面的代码是指F2单元格。

### 3.2. 单元格边框

每个边框有四个边：顶部、底部、左侧和右侧边框。我们必须分别设置每一边的边框样式。BorderStyle类提供了多种样式。

我们可以使用RangeUtil类设置边框：

```java
RegionUtil.setBorderTop(BorderStyle.DASH_DOT, region, sheet);
RegionUtil.setBorderBottom(BorderStyle.DOUBLE, region, sheet);
RegionUtil.setBorderLeft(BorderStyle.DOTTED, region, sheet);
RegionUtil.setBorderRight(BorderStyle.SLANTED_DASH_DOT, region, sheet);

```

### 3.3. 边框颜色

边框颜色也必须在每一侧单独设置。IndexedColors类提供了一系列可供使用的颜色。

我们可以使用RangeUtil类设置边框颜色：

```java
RegionUtil.setTopBorderColor(IndexedColors.RED.index, region, sheet);
RegionUtil.setBottomBorderColor(IndexedColors.GREEN.index, region, sheet);
RegionUtil.setLeftBorderColor(IndexedColors.BLUE.index, region, sheet);
RegionUtil.setRightBorderColor(IndexedColors.VIOLET.index, region, sheet);

```

## 4. 总结

在这篇简短的文章中，我们了解了如何使用CellRangeAddress、RegionUtil、BorderStyles和IndexedColors类生成各种单元格边框。边界的每一侧都必须单独设置。