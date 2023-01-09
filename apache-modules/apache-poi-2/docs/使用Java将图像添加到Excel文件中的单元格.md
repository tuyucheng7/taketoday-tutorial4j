## 1. 概述

在本教程中，我们将学习如何使用Java将图像添加到 Excel 文件中的单元格。

[我们将使用apache-poi](https://www.baeldung.com/java-microsoft-excel)动态创建一个 Excel 文件并将图像添加到单元格。

## 2.项目设置和依赖

Java 应用程序可以使用apache-poi动态地读取、写入和修改 Excel 电子表格的内容。它支持.xls和.xlsx Excel 格式。

### 2.1. Apache Poi API 的 Maven 依赖

首先，让我们将[poi](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.poi" AND a%3A"poi-ooxml")依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>3.15</version>
</dependency>
```

### 2.2. Excel 工作簿创建

首先，让我们创建一个要写入的工作簿和工作表。我们可以选择使用.xlsx文件的 XSSFWorkbook 或使用.xls文件的HSSFWorkbook 。让我们使用XSSFWorkbook：

```java
Workbook workbook = new XSSFWorkbook();
Sheet sheet = workbook.createSheet("Avengers");
Row row1 = sheet.createRow(0);
row1.createCell(0).setCellValue("IRON-MAN");
Row row2 = sheet.createRow(1);
row2.createCell(0).setCellValue("SPIDER-MAN");

```

在这里，我们创建了一个Avengers 工作表并用两个名字填充了A1和A2单元格。接下来，我们将复仇者联盟的图像添加到单元格B1和B2中。

## 3.在工作簿中插入图像

### 3.1. 从本地文件读取图像

要添加图像，我们首先需要从我们的项目目录中读取它们。对于我们的项目，资源目录中有两个图像：

-   /src/main/resources/ironman.png
-   /src/main/resources/spiderman.png

```java
InputStream inputStream1 = TestClass.class.getClassLoader()
    .getResourceAsStream("ironman.png");
InputStream inputStream2 = TestClass.class.getClassLoader()
    .getResourceAsStream("spiderman.png");

```

### 3.2. 将图像InputStream转换为字节数组

接下来，让我们将图像转换为字节数组。在这里，我们将使用apache -poi中的IOUtils：

```java
byte[] inputImageBytes1 = IOUtils.toByteArray(inputStream1);
byte[] inputImageBytes2 = IOUtils.toByteArray(inputStream2);

```

### 3.3. 在工作簿中添加图片

现在，我们将使用字节数组将图片添加到我们的工作簿中。支持的[图片类型](https://poi.apache.org/components/spreadsheet/quick-guide.html#Images)有 PNG、JPG 和 DIB。我们在这里使用 PNG：

```java
int inputImagePictureID1 = workbook.addPicture(inputImageBytes1, Workbook.PICTURE_TYPE_PNG);
int inputImagePictureID2 = workbook.addPicture(inputImageBytes2, Workbook.PICTURE_TYPE_PNG);

```

作为此步骤的结果，我们将获得用于创建Drawing对象的每张图片的索引。

### 3.4. 创建绘图容器

绘图族长是所有形状的顶级容器。这将返回一个Drawing接口——在我们的例子中是XSSFDrawing对象。我们将使用此对象来创建我们将放入我们定义的单元格中的图片。

让我们创建绘图族长：

```java
XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
```

## 4.在单元格中添加图像

现在，我们已准备好将图像添加到我们的单元格中。

### 4.1. 创建锚对象

首先，我们将创建一个客户端锚点对象，它附加到 Excel 工作表并用于设置图像在 Excel 工作表中的位置。它锚定在左上角和右下角的单元格上。

我们将创建两个锚点对象，每个图像一个：

```java
XSSFClientAnchor ironManAnchor = new XSSFClientAnchor();
XSSFClientAnchor spiderManAnchor = new XSSFClientAnchor();
```

接下来，我们需要指定图像与锚点对象的相对位置。

让我们将第一张图片放在单元格B1中：

```java
ironManAnchor.setCol1(1); // Sets the column (0 based) of the first cell.
ironManAnchor.setCol2(2); // Sets the column (0 based) of the Second cell.
ironManAnchor.setRow1(0); // Sets the row (0 based) of the first cell.
ironManAnchor.setRow2(1); // Sets the row (0 based) of the Second cell.
```

同样，我们将第二张图片放在单元格B2中：

```java
spiderManAnchor.setCol1(1);
spiderManAnchor.setCol2(2);
spiderManAnchor.setRow1(1);
spiderManAnchor.setRow2(2);
```

### 4.2. 将锚点对象和图片索引添加到绘图容器

现在，让我们在绘图族长上调用createPicture来添加图像。我们将使用之前创建的锚点对象和图像的图片索引：

```java
drawing.createPicture(ironManAnchor, inputImagePictureID1);
drawing.createPicture(spiderManAnchor, inputImagePictureID2);
```

## 5.保存工作簿

在我们保存之前，让我们确保单元格的宽度足以容纳我们使用autoSizeColumn添加的图片：

```java
for (int i = 0; i < 3; i++) {
    sheet.autoSizeColumn(i);
}
```

最后，让我们保存工作簿：

```java
try (FileOutputStream saveExcel = new FileOutputStream("target/baeldung-apachepoi.xlsx")) {
    workbook.write(saveExcel);
}
```

生成的 Excel 工作表应如下所示：

[![复仇者联盟](https://www.baeldung.com/wp-content/uploads/2021/12/xlavengers-295x300.png)](https://www.baeldung.com/wp-content/uploads/2021/12/xlavengers.png)

## 六. 总结

在本文中，我们学习了如何使用apache-poi库将图像添加到Java中的 Excel 工作表的单元格中。

我们需要加载图像，将其转换为字节，将其附加到工作表，然后使用绘图工具将图像定位到正确的单元格中。最后，我们能够调整列的大小并保存我们的工作簿。