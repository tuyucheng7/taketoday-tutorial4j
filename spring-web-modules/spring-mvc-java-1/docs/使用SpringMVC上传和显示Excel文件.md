## 1. 简介

在本文中，我们将演示如何使用Spring MVC框架上传Excel文件并将其内容显示在网页中。

## 2. 上传Excel文件

为了能够上传文件，我们将首先创建一个控制器映射，它接收一个MultipartFile并将其保存在当前位置：

```java
private String fileLocation;

@PostMapping("/uploadExcelFile")
public String uploadFile(Model model, MultipartFile file) throws IOException {
    InputStream in = file.getInputStream();
    File currDir = new File(".");
    String path = currDir.getAbsolutePath();
    fileLocation = path.substring(0, path.length() - 1) + file.getOriginalFilename();
    FileOutputStream f = new FileOutputStream(fileLocation);
    int ch = 0;
    while ((ch = in.read()) != -1) {
        f.write(ch);
    }
    f.flush();
    f.close();
    model.addAttribute("message", "File: " + file.getOriginalFilename() + " has been uploaded successfully!");
    return "excel";
}
```

接下来，让我们创建一个JSP文件，其表单包含一个类型为file的输入，其accept属性设置为仅允许Excel文件：

```html
<c:url value="/uploadExcelFile" var="uploadFileUrl"/>
<form method="post" enctype="multipart/form-data"
      action="${uploadFileUrl}">
    <input type="file" name="file" accept=".xls,.xlsx"/> <input
        type="submit" value="Upload file"/>
</form>
```

## 3. 读取Excel文件

为了解析上传的excel文件，我们将使用Apache POI库，它可以处理.xls和.xlsx文件。

让我们创建一个名为MyCell的辅助类，它将包含与内容和格式相关的Excel单元格的属性：

```java
public class MyCell {
    private String content;
    private String textColor;
    private String bgColor;
    private String textSize;
    private String textWeight;

    public MyCell(String content) {
        this.content = content;
    }

    // standard constructor, getters, setters
}
```

我们会将Excel文件的内容读入包含MyCell对象列表的Map中。

### 3.1 解析.xls文件

.xls文件在Apache POI库中由HSSFWorkbook类表示，该类由HSSFSheet对象组成。要打开和阅读.xls文件的内容，你可以查看我们关于[在Java中使用Microsoft Excel](https://www.baeldung.com/java-microsoft-excel)的文章。

为了解析单元格的格式，我们将获得HSSFCellStyle对象，它可以帮助我们确定背景颜色和字体等属性。所有读取的属性都将设置在MyCell对象的属性中：

```java
HSSFCellStyle cellStyle = cell.getCellStyle();

MyCell myCell = new MyCell();

HSSFColor bgColor = cellStyle.getFillForegroundColorColor();
if (bgColor != null) {
    short[] rgbColor = bgColor.getTriplet();
    myCell.setBgColor("rgb(" + rgbColor[0] + "," + rgbColor[1] + "," + rgbColor[2] + ")");
}
HSSFFont font = cell.getCellStyle().getFont(workbook);
```

颜色以rgb(rVal, gVal, bVal)格式读取，以便更容易在JSP页面中使用CSS显示它们。

我们还获取字体大小、粗细和颜色：

```java
myCell.setTextSize(font.getFontHeightInPoints() + "");
if (font.getBold()) {
    myCell.setTextWeight("bold");
}
HSSFColor textColor = font.getHSSFColor(workbook);
if (textColor != null) {
    short[] rgbColor = textColor.getTriplet();
    myCell.setTextColor("rgb(" + rgbColor[0] + "," + rgbColor[1] + "," + rgbColor[2] + ")");
}
```

### 3.2 解析.xlsx文件

对于较新的.xlsx格式的文件，我们可以将XSSFWorkbook类和类似的类用于工作簿的内容，这也记录在[在Java中使用Microsoft Excel](https://www.baeldung.com/java-microsoft-excel)一文中。

让我们仔细看看如何读取.xlsx格式的单元格格式。首先，我们将检索与单元格关联的XSSFCellStyle对象并使用它来确定背景颜色和字体：

```java
XSSFCellStyle cellStyle = cell.getCellStyle();

MyCell myCell = new MyCell();
XSSFColor bgColor = cellStyle.getFillForegroundColorColor();
if (bgColor != null) {
    byte[] rgbColor = bgColor.getRGB();
    myCell.setBgColor("rgb("
      + (rgbColor[0] < 0 ? (rgbColor[0] + 0xff) : rgbColor[0]) + ","
      + (rgbColor[1] < 0 ? (rgbColor[1] + 0xff) : rgbColor[1]) + ","
      + (rgbColor[2] < 0 ? (rgbColor[2] + 0xff) : rgbColor[2]) + ")");
}
XSSFFont font = cellStyle.getFont();
```

在这种情况下，颜色的RGB值将是带符号的字节值，因此我们将通过向负值添加0xff来获得无符号值。

我们还确定字体的属性：

```java
myCell.setTextSize(font.getFontHeightInPoints() + "");
if (font.getBold()) {
    myCell.setTextWeight("bold");
}
XSSFColor textColor = font.getXSSFColor();
if (textColor != null) {
    byte[] rgbColor = textColor.getRGB();
    myCell.setTextColor("rgb("
      + (rgbColor[0] < 0 ? (rgbColor[0] + 0xff) : rgbColor[0]) + "," 
      + (rgbColor[1] < 0 ? (rgbColor[1] + 0xff) : rgbColor[1]) + "," 
      + (rgbColor[2] < 0 ? (rgbColor[2] + 0xff) : rgbColor[2]) + ")");
}
```

### 3.3 处理空行

上述方法不考虑Excel文件中的空行。如果我们想要忠实地再现显示空行的文件，我们将需要在生成的HashMap中使用包含空字符串作为内容的MyCell对象的ArrayList来模拟这些。

最初，在读取Excel文件后，文件中的空行将是大小为0的ArrayList对象。

为了确定我们应该添加多少个空字符串对象，我们将首先使用maxNrCols变量确定Excel文件中最长的行。然后我们将那个数量的空String对象添加到我们的HashMap中所有大小为0的列表：

```java
int maxNrCols = data.values().stream()
    .mapToInt(List::size)
    .max()
    .orElse(0);

data.values().stream()
    .filter(ls -> ls.size() < maxNrCols)
    .forEach(ls -> {
        IntStream.range(ls.size(), maxNrCols)
            .forEach(i -> ls.add(new MyCell("")));
    });
```

## 4. 显示Excel文件

为了显示使用Spring MVC读取的Excel文件，我们需要定义一个控制器映射和JSP页面。

### 4.1 Spring MVC控制器

让我们创建一个@RequestMapping方法，它将调用上面的代码来读取上传文件的内容，然后将返回的Map添加为Model属性：

```java
@Resource(name = "excelPOIHelper")
private ExcelPOIHelper excelPOIHelper;

@RequestMapping(method = RequestMethod.GET, value = "/readPOI")
public String readPOI(Model model) throws IOException {

  if (fileLocation != null) {
      if (fileLocation.endsWith(".xlsx") || fileLocation.endsWith(".xls")) {
          Map<Integer, List<MyCell>> data = excelPOIHelper.readExcel(fileLocation);
          model.addAttribute("data", data);
      } else {
          model.addAttribute("message", "Not a valid excel file!");
      }
  } else {
      model.addAttribute("message", "File missing! Please upload an excel file.");
  }
  return "excel";
}
```

### 4.2 JSP

为了直观地显示文件的内容，我们将创建一个HTML表格，并在每个表格单元格的样式属性中，添加与Excel文件中每个单元格对应的格式属性：

```html
<c:if test="${not empty data}">
    <table style="border: 1px solid black; border-collapse: collapse;">
        <c:forEach items="${data}" var="row">
            <tr>
                <c:forEach items="${row.value}" var="cell">
                    <td style="border:1px solid black;height:20px;width:100px;
                      background-color:${cell.bgColor};color:${cell.textColor};
                      font-weight:${cell.textWeight};font-size:${cell.textSize}pt;">
                        ${cell.content}
                    </td>
                </c:forEach>
            </tr>
        </c:forEach>
    </table>
</c:if>
```

## 5. 总结

在本文中，我们展示了一个使用Spring MVC框架上传Excel文件并将其显示在网页中的示例项目。