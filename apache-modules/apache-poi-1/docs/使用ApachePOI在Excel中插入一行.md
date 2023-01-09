## 1. 概述

有时，我们可能需要在Java应用程序中操作 Excel 文件。

[在本教程中，我们将专门介绍如何使用Apache POI](https://www.baeldung.com/java-microsoft-excel)库在 Excel 文件中的两行之间插入一个新行。

## 2.Maven依赖

首先，我们必须将[poi-ooxml](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.poi" AND a%3A"poi-ooxml") Maven 依赖项添加到我们的 pom.xml 文件中：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.0</version>
</dependency>
```

## 3.在两行之间插入行

### 3.1. Apache POI 相关类

Apache POI 是一个库的集合——每个库都专门用于处理特定类型的文件。XSSF库包含处理xlsx Excel 格式的类。下图展示了 Apache POI 相关接口和类，用于操作xlsx Excel 文件：

[![图第 4 页](https://www.baeldung.com/wp-content/uploads/2021/02/Figures-Page-4.png)](https://www.baeldung.com/wp-content/uploads/2021/02/Figures-Page-4.png)

### 3.2. 实现行插入

要在现有 Excel 工作表的中间插入m行，从插入点到最后一行的所有行都应向下移动m行。

首先，我们需要读取Excel文件。对于这一步，我们使用XSSFWorkbook类：

```java
Workbook workbook = new XSSFWorkbook(fileLocation);
```

第二步是使用getSheet()方法访问工作簿中的工作表：

```java
Sheet sheet = workbook.getSheetAt(0);
```

第三步是移动行，从当前位于我们要开始插入新行的行到工作表的最后一行：

```java
int lastRow = sheet.getLastRowNum(); 
sheet.shiftRows(startRow, lastRow, rowNumber, true, true);

```

在此步骤中，我们使用getLastRowNum()方法获取最后的行号，并使用shiftRows()方法移动行。此方法按rowNumber的大小在startRow和lastRow之间移动行。

最后，我们使用createRow()方法插入新行：

```java
sheet.createRow(startRow);
```

值得注意的是，上面的实现将保持移动行的格式。此外，如果我们移动的范围内有隐藏行，它们会在插入新行时移动。

### 3.3. 单元测试

让我们编写一个测试用例，读取资源目录中的工作簿，然后在位置 2 插入一行并将内容写入新的 Excel 文件。最后，我们用主文件断言结果文件的行号。

让我们定义一个测试用例：

```java
public void givenWorkbook_whenInsertRowBetween_thenRowCreated() {
    int startRow = 2;
    int rowNumber = 1;
    Workbook workbook = new XSSFWorkbook(fileLocation);
    Sheet sheet = workbook.getSheetAt(0);

    int lastRow = sheet.getLastRowNum();
    if (lastRow < startRow) {
        sheet.createRow(startRow);
    }

    sheet.shiftRows(startRow, lastRow, rowNumber, true, true);
    sheet.createRow(startRow);

    FileOutputStream outputStream = new FileOutputStream(NEW_FILE_NAME);
    workbook.write(outputStream);

    File file = new File(NEW_FILE_NAME);

    final int expectedRowResult = 5;
    Assertions.assertEquals(expectedRowResult, workbook.getSheetAt(0).getLastRowNum());

    outputStream.close();
    file.delete();
    workbook.close();
}
```

## 4. 总结

总之，我们学习了如何使用 Apache POI 库在 Excel 文件中的两行之间插入一行。