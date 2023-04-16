## 1. 概述

在本文中，我们将了解如何使用Java编辑现有 PDF 文件的内容。首先，我们将只添加新内容。然后，我们将专注于删除或替换一些预先存在的内容。

## 2. 添加 iText7 依赖

我们将使用[iText7](https://search.maven.org/artifact/com.itextpdf/itext7-core)库向 PDF 文件添加内容。稍后，我们将使用[pdfSweep](https://search.maven.org/artifact/com.itextpdf/cleanup)附加组件来删除或替换内容。

请注意，iText 是根据 AGPL 获得许可的，这可能会限制商业应用程序的分发：[iText License Model](https://itextpdf.com/how-buy)。

首先，让我们将这些依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.3</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>cleanup</artifactId>
    <version>3.0.1</version>
</dependency>
```

## 3. 文件处理

让我们了解使用 iText7 处理我们的 PDF 的步骤：

-   首先，我们打开一个PdfReader来读取源文件的内容。如果在读取文件时随时发生错误，则会抛出IOException 。
-   然后，我们打开一个PdfWriter到目标文件。如果此文件不存在或无法创建，则抛出[FileNotFoundException](https://www.baeldung.com/java-filenotfound-exception)。
-   之后，我们将打开一个PdfDocument，它使用我们的PdfReader和PdfWriter。
-   最后，关闭PdfDocument 会关闭底层的PdfReader和PdfWriter。

让我们编写一个运行整个处理的[main()方法。](https://www.baeldung.com/java-main-method)为了简单起见，我们将重新抛出任何可能发生的[异常](https://www.baeldung.com/java-exceptions)：

```java
public static void main(String[] args) throws IOException {
    PdfReader reader = new PdfReader("src/main/resources/baeldung.pdf");
    PdfWriter writer = new PdfWriter("src/main/resources/baeldung-modified.pdf");
    PdfDocument pdfDocument = new PdfDocument(reader, writer);
    addContentToDocument(pdfDocument);
    pdfDocument.close();
}
```

在下一节中，我们将逐步完成addContentToDocument()方法，以便用新内容填充我们的 PDF。源文档是一个 PDF 文件，仅在左上角包含文本“Hello Baeldung ”。目标文件将由程序创建。

## 4. 向文件添加内容

我们现在将向文件中添加各种类型的内容。

### 4.1. 添加表格

我们将从向文件中添加一个表单开始。我们的表单将非常简单，并包含一个名为name的唯一字段。

此外，我们需要告诉 iText 在哪里放置字段。在这种情况下，我们将把它放在以下位置：(35,400)。坐标(0,0)指的是文档的左下角。最后，我们将字段的尺寸设置为100×30：

```java
PdfFormField personal = PdfFormField.createEmptyField(pdfDocument);
personal.setFieldName("information");
PdfTextFormField name = PdfFormField.createText(pdfDocument, new Rectangle(35, 400, 100, 30), "name", "");
personal.addKid(name);
PdfAcroForm.getAcroForm(pdfDocument, true)
    .addField(personal, pdfDocument.getFirstPage());
```

此外，我们明确指定 iText 将表单添加到文档的第一页。

### 4.2. 添加新页面

现在让我们看一下如何向文档添加新页面。我们将使用addNewPage()方法。

如果我们想指定它，这个方法可以接受添加页面的索引。例如，我们可以在文档的开头添加一个新页面：

```java
pdfDocument.addNewPage(1);
```

### 4.3. 添加注解

我们现在要向文档添加注解。具体来说，注解看起来像一个方形的漫画泡泡。

我们将把它添加到现在位于文档第二页的表单之上。因此，我们将把它放在坐标(40,435)上。此外，我们会给它一个简单的名称和内容。这些只会在将鼠标悬停在注解上时显示：

```java
PdfAnnotation ann = new PdfTextAnnotation(new Rectangle(40, 435, 0, 0)).setTitle(new PdfString("name"))
    .setContents("Your name");
pdfDocument.getPage(2)
    .addAnnotation(ann);
```

这是我们第二页的中间部分现在的样子：

 

[![img](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung_modified_form_and_annotation.webp)](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung_modified_form_and_annotation.webp)

### 4.4. 添加图像

从现在开始，我们将向页面添加布局元素。为了做到这一点，我们将无法再直接操作PdfDocument。我们宁愿从中创建一个文档并使用它。此外，我们最后需要关闭文档。关闭文档会自动关闭基础PdfDocument。所以我们可以删除之前关闭PdfDocument的部分：

```java
Document document = new Document(pdfDocument);
// add layout elements
document.close();
```

现在，要添加图像，我们需要从它的位置加载它。我们将使用ImageDataFactory类的create()方法来完成此操作。如果无法解析传递的文件 URL，则会抛出[MalformedURLException 。](https://www.baeldung.com/java-common-exceptions#1-ioexception)在此示例中，我们将使用放置在资源目录中的 Baeldung 徽标图像：

```
ImageData imageData = ImageDataFactory.create("src/main/resources/baeldung.png");
```

下一步是在文件中设置图像的属性。我们将其大小设置为550×100。我们将把它放在 PDF 的第一页，坐标(10,50)处。让我们看看添加图像的代码：

```java
Image image = new Image(imageData).scaleAbsolute(550,100)
    .setFixedPosition(1, 10, 50);
document.add(image);
```

图像会自动重新缩放到给定的大小。所以这是它在文档中的样子：

 

[![img](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung-modified-p1-baeldung-image.png)](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung-modified-p1-baeldung-image.png)

### 4.5. 添加段落

iText 库提供了一些向文件添加文本的工具。字体可以在片段本身上进行参数化，也可以直接在段落元素上进行参数化。

例如，让我们在第一页的顶部添加以下句子：This is a demo from Baeldung tutorials。我们将这句话开头的字体大小设置为16 ，将段落的全局字体大小设置为8：

```java
Text title = new Text("This is a demo").setFontSize(16);
Text author = new Text("Baeldung tutorials.");
Paragraph p = new Paragraph().setFontSize(8)
    .add(title)
    .add(" from ")
    .add(author);
document.add(p);
```

### 4.6. 添加表格

最后但同样重要的是，我们还可以向文件中添加一个表。例如，我们将定义一个包含两个单元格和两个表头的复式表。我们不会指定任何位置。所以它会自然地添加到文档的顶部，就在我们刚刚添加的段落之后：

```java
Table table = new Table(UnitValue.createPercentArray(2));
table.addHeaderCell("#");
table.addHeaderCell("company");
table.addCell("name");
table.addCell("baeldung");
document.add(table);
```

现在让我们看看文档第一页的开头：

[![img](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung-modified-p1-top.png)](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung-modified-p1-top.png)

## 5. 从文件中删除内容

现在让我们看看如何从 PDF 文件中删除内容。为了简单起见，我们将编写另一个main()方法。

我们的源 PDF 文件将是baeldung-modified.pdf文件，目标文件将是一个新的 baeldung-cleaned.pdf文件。我们将直接处理PdfDocument对象。从现在开始，我们将使用 iText 的 pdfSweep 插件。

### 5.1. 从文件中删除文本

要从文件中删除给定的文本，我们需要定义一个清理策略。在此示例中，策略将简单地找到所有匹配Baeldung的文本。最后一步是调用 PdfCleaner 的autoSweepCleanUp () [静态](https://www.baeldung.com/java-static-methods-use-cases)方法。此方法将创建一个自定义PdfCleanUpTool ，如果在文件处理期间发生任何错误，它将抛出IOException ：

```java
CompositeCleanupStrategy strategy = new CompositeCleanupStrategy();
strategy.add(new RegexBasedCleanupStrategy("Baeldung"));
PdfCleaner.autoSweepCleanUp(pdfDocument, strategy);
```

正如我们所见，源文件中出现的Baeldung单词在结果文件中被黑色矩形覆盖。例如，此行为适用于数据匿名化：

[![img](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung-cleaned-p1-top.png)](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung-cleaned-p1-top.png)

### 5.2. 从文件中删除其他内容

不幸的是，很难检测到文件中的任何非文本内容。但是，pdfSweep 提供了删除文件的一部分内容的可能性。因此，如果我们知道要删除的内容位于何处，我们就可以利用这种可能性。

例如，我们将擦除位于第二页(35,400)处的大小为100×35的矩形的内容。这意味着我们将摆脱表单和注解的所有内容。此外，我们将擦除位于第一页(10,50)的大小为90×70的矩形。这基本上从 Baeldung 的徽标中删除了B。使用PdfCleanUpTool类，这是执行所有操作的代码：

```java
List<PdfCleanUpLocation> cleanUpLocations = Arrays.asList(new PdfCleanUpLocation(1, new Rectangle(10, 50, 90,70)), new PdfCleanUpLocation(2, new Rectangle(35, 400, 100, 35)));
PdfCleanUpTool cleaner = new PdfCleanUpTool(pdfDocument, cleanUpLocations, new CleanUpProperties());
cleaner.cleanUp();
```

我们现在可以在baeldung-cleaned.pdf中看到下图：

## [![img](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung-cleaned-p1-baeldung-image.png)](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung-cleaned-p1-baeldung-image.png)6.替换文件中的内容

在本节中，我们将执行与之前相同的工作，只是我们将用新文本替换以前的文本，而不是仅将其擦除。

为了更清楚，我们将再次使用新的main()方法。我们的源文件将是baeldung-modified.pdf文件。我们的目标文件将是一个新的 baeldung-fixed.pdf文件。

早些时候我们看到删除的文本被黑色背景覆盖。但是，此颜色是可配置的。因为我们知道文件中文本的背景是白色的，所以我们将强制叠加层为白色。处理的开始将与我们之前所做的类似，除了我们将搜索文本Baeldung 教程。

但是，在调用autoSweepCleanUp()之后，我们将查询该策略以获取已删除代码的位置。然后我们将实例化一个PdfCanvas，它将包含替换文本HIDDEN。此外，我们将删除上边距以使其与原始文本更好地对齐。默认对齐确实不太好。让我们看看结果代码：

```java
CompositeCleanupStrategy strategy = new CompositeCleanupStrategy();
strategy.add(new RegexBasedCleanupStrategy("Baeldung").setRedactionColor(ColorConstants.WHITE));
PdfCleaner.autoSweepCleanUp(pdfDocument, strategy);

for (IPdfTextLocation location : strategy.getResultantLocations()) {
    PdfPage page = pdfDocument.getPage(location.getPageNumber() + 1);
    PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), page.getDocument());
    Canvas canvas = new Canvas(pdfCanvas, location.getRectangle());
    canvas.add(new Paragraph("HIDDEN").setFontSize(8)
        .setMarginTop(0f));
}
```

我们可以看看这个文件：

[![img](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung-fixed-p1-top.png)](https://www.baeldung.com/wp-content/uploads/2022/10/baeldung-fixed-p1-top.png)

## 七. 总结

在本教程中，我们了解了如何编辑 PDF 文件的内容。我们已经看到我们可以添加新内容、删除现有内容，甚至可以用新内容替换原始文件中的文本。