## 1. 简介

[Apache POI](https://www.baeldung.com/java-microsoft-excel)是一个开源库，供软件开发人员创建和[操作 Microsoft Office 文档](https://www.baeldung.com/apache-poi-insert-excel-row)。除其他功能外，它还允许开发人员以编程方式更改文档格式。

在本文中，我们将讨论如何在使用名为CellStyle的类时更改 Microsoft Excel 中的单元格样式。也就是说，利用这个类，我们可以编写代码修改Microsoft Excel文档中单元格的样式。首先，它是 Apache POI 库提供的一项功能，允许在工作簿中创建具有多个格式化属性的样式。其次，该样式随后可以应用于该工作簿中的多个单元格。

除此之外，我们还将了解使用CellStyle类的常见陷阱。

## 2.Apache POI与Maven依赖

让我们将[Apache POI](https://search.maven.org/search?q=g:org.apache.poi a:poi)作为依赖项添加到我们的项目pom.xml 文件中：

```xml
<dependency>
    <groupId>org.apache.poi</groupId> 
    <artifactId>poi</artifactId> 
    <version>5.2.0</version> 
</dependency>
```

## 3.创建CellStyle

让我们从实例化CellStyle开始：

```java
Workbook workbook = new XSSFWorkbook(fileLocation);
CellStyle cellStyle = wb.createCellStyle();
```

接下来，设置所需的格式属性。例如，下面的代码会将其设置为具有日期格式：

```java
cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
```

最重要的是，我们可以设置CellStyle的多个格式化属性以获得所需的样式组合。例如，我们将以下代码应用于同一个CellStyle对象。因此，它具有日期格式样式和居中对齐的文本样式：

```java
cellStyle.setAlignment(HorizontalAlignment.CENTER);

```

请注意，CellStyle有几个我们可以修改的格式属性：

| 财产                                    | 描述                                           |
| --------------------------------------- | ---------------------------------------------- |
| 数据格式                              | 单元格的数据格式，例如日期                     |
| 结盟                                  | 单元格的水平对齐类型                           |
| 隐                                    | 是否隐藏单元格                                 |
| 缩进                                  | 单元格中文本缩进的空格数                       |
| 边框底部,左边框,右边框,边框顶部 | 用于单元格底部、左侧、右侧和顶部边框的边框类型 |
| 字体                                  | 此样式的字体属性，例如字体颜色                 |

稍后当我们使用它来更改字体样式时，我们将再次查看Font属性。

## 4.使用CellStyle格式化字体

CellStyle的Font属性是我们设置字体相关格式的地方。例如，我们可以设置字体名称、颜色和大小。我们可以设置字体是粗体还是斜体。Font的两个属性都可以是true或false。我们还可以将下划线样式设置为：

| 价值                  | 财产                                             |
| --------------------- | ------------------------------------------------ |
| U_NONE              | 不带下划线的文字                                 |
| 你单身              | 单下划线文本，其中只有单词有下划线               |
| U_SINGLE_ACCOUNTING | 几乎整个单元格宽度都带有下划线的单下划线文本     |
| U_DOUBLE            | 双下划线文本，其中只有单词有下划线               |
| U_DOUBLE_ACCOUNTING | 双下划线文本，其中几乎整个单元格宽度都带有下划线 |

让我们从前面的例子继续。我们将编写一个名为CellStyler的类，其中包含一个为警告文本创建样式的方法：

```java
public class CellStyler {
    public CellStyle createWarningColor(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Courier New");
        font.setBold(true);
        font.setUnderline(Font.U_SINGLE);
        font.setColor(HSSFColorPredefined.DARK_RED.getIndex());
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}
```

现在，让我们创建一个 Apache POI 工作簿并获取第一个工作表：

```java
Workbook workbook = new XSSFWorkbook(fileLocation);
Sheet sheet = workbook.getSheetAt(0);
```

请注意，我们正在设置行高，以便我们可以看到文本对齐的效果：

```java
Row row1 = sheet.createRow(0);
row1.setHeightInPoints((short) 40);
```

让我们实例化类并使用它来设置样式

```java
CellStyler styler = new CellStyler();
CellStyle style = styler.createWarningColor(workbook);

Cell cell1 = row1.createCell(0);
cell1.setCellStyle(style);
cell1.setCellValue("Hello");

Cell cell2 = row1.createCell(1);
cell2.setCellStyle(style);
cell2.setCellValue("world!");
```

现在，让我们将这个工作簿保存到一个文件中，然后在 Microsoft Excel 中打开该文件以查看字体样式效果，我们应该在其中看到：

[![应用字体样式的结果](https://www.baeldung.com/wp-content/uploads/2022/01/Change_Cell_Font_Style_with_Apache_POI-e1638670076814.png)](https://www.baeldung.com/wp-content/uploads/2022/01/Change_Cell_Font_Style_with_Apache_POI-e1638669975805.png)

## 5. 常见陷阱

让我们看看使用CellStyle时犯的两个常见错误。

### 5.1. 不小心修改了所有单元格样式

首先，从单元格获取CellStyle并开始修改它是一个常见的错误。getCellStyle方法的 Apache POI 文档提到单元格的getCellStyle方法将始终返回一个非空值。这意味着该单元格具有默认值，这也是工作簿中所有单元格最初使用的默认样式。因此，下面的代码将使所有单元格都具有日期格式：

```java
cell.setCellValue(rdf.getEffectiveDate());
cell.getCellStyle().setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"));
```

### 5.2. 为每个单元格创建新样式

另一个常见错误是工作簿中有太多相似的样式：

```java
CellStyle style1 = codeToCreateCellStyle();
Cell cell1 = row1.createCell(0);
cell1.setCellStyle(style1);

CellStyle style2 = codeToCreateCellStyle();
Cell cell2 = row1.createCell(1);
cell2.setCellStyle(style2);
```

CellStyle的范围限定为工作簿。因此，类似的样式应该由多个单元格共享。在上面的示例中，样式应该只创建一次并在cell1和cell2之间共享：

```java
CellStyle style1 = codeToCreateCellStyle();
Cell cell1 = row1.createCell(0);
cell1.setCellStyle(style1);
cell1.setCellValue("Hello");

Cell cell2 = row1.createCell(1);
cell2.setCellStyle(style1);
cell2.setCellValue("world!");
```

## 6.总结

在本文中，我们学习了如何使用CellStyle及其Font属性为 Apache POI 中的单元格设置样式。如果我们设法避免陷阱，那么为单元格设置样式会相对简单，如本文所述。

在代码示例中，我们展示了如何根据需要以编程方式设置电子表格文档的样式，就好像我们在使用 Excel 应用程序本身一样。当需要生成具有漂亮数据表示的电子表格时，这一点最为重要。