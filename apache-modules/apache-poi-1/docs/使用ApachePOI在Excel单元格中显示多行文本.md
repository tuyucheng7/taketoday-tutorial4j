## 1. 简介

我们可以使用 Apache POI 在 Microsoft Excel 电子表格中以编程方式创建多行文本。但是，它不会显示为多行。这是因为使用代码向单元格添加文本不会自动调整单元格高度并应用所需的格式将其转换为多行文本。

这个简短的教程将演示正确显示此类文本所需的代码。

## 2.Apache POI与Maven依赖

Apache POI 是一个开源库，允许软件开发人员创建和操作 Microsoft Office 文档。作为先决条件，读者可以参考我们关于[在Java中使用 Microsoft Excel](https://www.baeldung.com/java-microsoft-excel)的文章和关于如何[使用 Apache POI 在 Excel 中插入行](https://www.baeldung.com/apache-poi-insert-excel-row)的教程。

首先，我们首先需要将[Apache POI](https://search.maven.org/search?q=g:org.apache.poi a:poi) 依赖项添加到我们的项目 pom.xml 文件中：

```xml
<dependency>
    <groupId>org.apache.poi</groupId> 
    <artifactId>poi</artifactId> 
    <version>5.2.0</version> 
</dependency>
```

## 3. 添加和格式化多行文本

让我们从一个包含多行文本的单元格开始：

```java
cell.setCellValue("Hello n world!");
```

如果我们只用上面的代码生成并保存一个Excel文件。它看起来像下面的图像：

[![格式化前的多行文本](https://www.baeldung.com/wp-content/uploads/2021/10/multiline_text_before_formatting-1024x360.png)](https://www.baeldung.com/wp-content/uploads/2021/10/multiline_text_before_formatting.png)

我们可以点击上图中所指的 1 和 2 来验证文本是否确实是多行文本。

使用代码，格式化单元格并将其行高扩展到等于或大于两行文本的任意值：

```java
cell.getRow()
  .setHeightInPoints(cell.getSheet().getDefaultRowHeightInPoints()  2);
```

之后，我们需要设置单元格样式来包裹文本：

```java
CellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
cellStyle.setWrapText(true);
cell.setCellStyle(cellStyle);
```

保存使用上述代码生成的文件并在 Microsoft Excel 中查看它会在一个单元格中显示多行文本。

[![格式化后](https://www.baeldung.com/wp-content/uploads/2021/10/multiline_text_after_formatting-1024x449.png)](https://www.baeldung.com/wp-content/uploads/2021/10/multiline_text_after_formatting.png)

## 4.总结

在本教程中，我们学习了如何使用 Apache POI 将多行文本添加到单元格。然后我们通过对单元格应用一些格式来确保它作为两行文本可见。否则，单元格将显示为一行。