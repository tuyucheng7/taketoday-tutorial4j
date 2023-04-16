## 1. 简介

在这篇快速文章中，我们将专注于在 PDF 文件和Java中的其他格式之间进行编程转换。

更具体地说，我们将介绍如何使用多个Java开源库将 PDF 保存为图像文件(例如 PNG 或 JPEG)、将 PDF 转换为 Microsoft Word 文档、导出为 HTML 以及提取文本。

## 2.Maven依赖

我们要看的第一个库是[Pdf2Dom](http://cssbox.sourceforge.net/pdf2dom/documentation.php)。让我们从需要添加到项目中的 Maven 依赖项开始：

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox-tools</artifactId>
    <version>2.0.25</version>
</dependency>
<dependency>
    <groupId>net.sf.cssbox</groupId>
    <artifactId>pdf2dom</artifactId>
    <version>2.0.1</version>
</dependency>
```

我们将使用第一个依赖项来加载选定的 PDF 文件。第二个依赖项负责转换本身。最新版本可以在这里找到：[pdfbox-tools](https://search.maven.org/classic/#search|ga|1|a%3A"pdfbox-tools")和[pdf2dom](https://search.maven.org/classic/#search|ga|1|pdf2dom)。

此外，我们将使用[iText](http://itextpdf.com/)从 PDF 文件中提取文本，并使用[POI](https://poi.apache.org/)创建 . 文档。

让我们看一下我们需要在项目中包含的 Maven 依赖项：

```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.10</version>
</dependency>
<dependency>
    <groupId>com.itextpdf.tool</groupId>
    <artifactId>xmlworker</artifactId>
    <version>5.5.10</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>3.15</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-scratchpad</artifactId>
    <version>3.15</version>
</dependency>
```

可以在此处找到最新版本的 iText，你可以在[此处](https://search.maven.org/classic/#search|ga|1|poi)[查找](https://search.maven.org/classic/#search|ga|1|g%3A"com.itextpdf")Apache POI 。

## 3. PDF 和 HTML 转换

要处理 HTML 文件，我们将使用[Pdf2Dom——](http://cssbox.sourceforge.net/pdf2dom/documentation.php)一个将文档转换为 HTML DOM表示的 PDF 解析器。然后可以将获得的 DOM 树序列化为 HTML 文件或进一步处理。

要将 PDF 转换为 HTML，我们需要使用 XMLWorker，这是[iText](http://itextpdf.com/)提供的库。

### 3.1. PDF 到 HTML

让我们看一下从 PDF 到 HTML 的简单转换：

```java
private void generateHTMLFromPDF(String filename) {
    PDDocument pdf = PDDocument.load(new File(filename));
    Writer output = new PrintWriter("src/output/pdf.html", "utf-8");
    new PDFDomTree().writeText(pdf, output);
    
    output.close();
}

```

在上面的代码片段中，我们使用 PDFBox 的加载 API 加载 PDF 文件。加载 PDF 后，我们使用解析器解析文件并写入java.io.Writer 指定的输出。

请注意，将 PDF 转换为 HTML 永远不是 100%、像素到像素的结果。结果取决于特定 PDF 文件的复杂性和结构。

### 3.2. HTML 到 PDF

现在，让我们看一下从 HTML 到 PDF 的转换：

```java
private static void generatePDFFromHTML(String filename) {
    Document document = new Document();
    PdfWriter writer = PdfWriter.getInstance(document,
      new FileOutputStream("src/output/html.pdf"));
    document.open();
    XMLWorkerHelper.getInstance().parseXHtml(writer, document,
      new FileInputStream(filename));
    document.close();
}
```

注意，将 HTML 转换为 PDF，需要确保 HTML 的所有标签都正确开始和关闭，否则将无法创建 PDF。这种方法的积极方面是创建的 PDF 将与在 HTML 文件中完全相同。

## 4. PDF 到图像的转换

将 PDF 文件转换为图像的方法有很多种。最流行的解决方案之一是[Apache PDFBox](https://pdfbox.apache.org/)。这个库是一个用于处理 PDF 文档的开源Java工具。对于图像到 PDF 的转换，我们将再次使用[iText](http://itextpdf.com/)。

### 4.1. PDF 到图像

要开始将 PDF 转换为图像，我们需要使用上一节中提到的依赖项——pdfbox-tools。

我们来看看代码示例：

```java
private void generateImageFromPDF(String filename, String extension) {
    PDDocument document = PDDocument.load(new File(filename));
    PDFRenderer pdfRenderer = new PDFRenderer(document);
    for (int page = 0; page < document.getNumberOfPages(); ++page) {
        BufferedImage bim = pdfRenderer.renderImageWithDPI(
          page, 300, ImageType.RGB);
        ImageIOUtil.writeImage(
          bim, String.format("src/output/pdf-%d.%s", page + 1, extension), 300);
    }
    document.close();
}
```

上面提到的代码中有几个重要的部分。我们需要使用PDFRenderer，以便将 PDF 渲染为BufferedImage。此外，PDF 文件的每一页都需要单独呈现。

最后，我们使用Apache PDFBox Tools 中的ImageIOUtil来编写图像，并使用我们指定的扩展名。可能的文件格式为jpeg、jpg、gif、tiff或png。

请注意，Apache PDFBox 是一种高级工具——我们可以从头开始创建我们自己的 PDF 文件，在 PDF 文件中填写表格，签名和/或加密 PDF 文件。

### 4.2. 图片转PDF

我们来看看代码示例：

```java
private static void generatePDFFromImage(String filename, String extension) {
    Document document = new Document();
    String input = filename + "." + extension;
    String output = "src/output/" + extension + ".pdf";
    FileOutputStream fos = new FileOutputStream(output);

    PdfWriter writer = PdfWriter.getInstance(document, fos);
    writer.open();
    document.open();
    document.add(Image.getInstance((new URL(input))));
    document.close();
    writer.close();
}
```

请注意，我们可以提供图像作为文件，或从 URL 加载它，如上例所示。此外，我们可以使用的输出文件的扩展名是jpeg、jpg、gif、tiff或png。

## 5. PDF 到文本的转换

要从 PDF 文件中提取原始文本，我们还将再次使用[Apache PDFBox](https://pdfbox.apache.org/)。对于文本到 PDF 的转换，我们将使用[iText](http://itextpdf.com/)。

### 5.1. PDF转文本

我们创建了一个名为generateTxtFromPDF(...)的方法，并将其 分为三个主要部分：加载 PDF 文件、提取文本和创建最终文件。

让我们从加载部分开始：

```java
File f = new File(filename);
String parsedText;
PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
parser.parse();
```

为了读取 PDF 文件，我们使用带有“r”(读取)选项的PDFParser 。此外，我们需要使用parser.parse()方法将 PDF 解析为流并填充到COSDocument对象中。

我们来看看提取文本部分：

```java
COSDocument cosDoc = parser.getDocument();
PDFTextStripper pdfStripper = new PDFTextStripper();
PDDocument pdDoc = new PDDocument(cosDoc);
parsedText = pdfStripper.getText(pdDoc);
```

在第一行中，我们将 COSDocument 保存在cosDoc变量中。然后它将用于构造PDocument，它是 PDF 文档的内存表示。最后，我们将使用PDFTextStripper返回文档的原始文本。在所有这些操作之后，我们需要使用close()方法来关闭所有使用的流。

在最后一部分，我们将使用简单的JavaPrintWriter将文本保存到新创建的文件中：

```java
PrintWriter pw = new PrintWriter("src/output/pdf.txt");
pw.print(parsedText);
pw.close();
```

请注意，你不能保留纯文本文件中的格式，因为它只包含文本。

### 5.2. 文本转PDF

将文本文件转换为 PDF 有点棘手。为了保持文件格式，你需要应用额外的规则。

在下面的例子中，我们没有考虑文件的格式。

首先，我们需要定义 PDF 文件的大小、版本和输出文件。让我们看一下代码示例：

```java
Document pdfDoc = new Document(PageSize.A4);
PdfWriter.getInstance(pdfDoc, new FileOutputStream("src/output/txt.pdf"))
  .setPdfVersion(PdfWriter.PDF_VERSION_1_7);
pdfDoc.open();
```

在下一步中，我们将定义字体以及用于生成新段落的命令：

```java
Font myfont = new Font();
myfont.setStyle(Font.NORMAL);
myfont.setSize(11);
pdfDoc.add(new Paragraph("n"));
```

最后，我们要将段落添加到新创建的 PDF 文件中：

```java
BufferedReader br = new BufferedReader(new FileReader(filename));
String strLine;
while ((strLine = br.readLine()) != null) {
    Paragraph para = new Paragraph(strLine + "n", myfont);
    para.setAlignment(Element.ALIGN_JUSTIFIED);
    pdfDoc.add(para);
}	
pdfDoc.close();
br.close();
```

## 6. PDF 到 Docx 的转换

从 Word 文档创建 PDF 文件并不容易，我们不会在这里讨论这个主题。我们建议使用 3rd 方库来执行此操作，例如[jWordConvert](https://www.qoppa.com/wordconvert/)。

要从 PDF 创建 Microsoft Word 文件，我们需要两个库。这两个库都是开源的。第一个是[iText](http://itextpdf.com/)，它用于从 PDF 文件中提取文本。第二个是[POI](https://poi.apache.org/)，用于创建 . 文档。

让我们看一下PDF加载部分的代码片段：

```java
XWPFDocument doc = new XWPFDocument();
String pdf = filename;
PdfReader reader = new PdfReader(pdf);
PdfReaderContentParser parser = new PdfReaderContentParser(reader);

```

加载 PDF 后，我们需要在循环中分别读取和渲染每个页面，然后写入输出文件：

```java
for (int i = 1; i <= reader.getNumberOfPages(); i++) {
    TextExtractionStrategy strategy =
      parser.processContent(i, new SimpleTextExtractionStrategy());
    String text = strategy.getResultantText();
    XWPFParagraph p = doc.createParagraph();
    XWPFRun run = p.createRun();
    run.setText(text);
    run.addBreak(BreakType.PAGE);
}
FileOutputStream out = new FileOutputStream("src/output/pdf.docx");
doc.write(out);
// Close all open files
```

请注意，使用SimpleTextExtractionStrategy()提取策略，我们将丢失所有格式规则。为了修复它，请使用[此处](http://itextpdf.com/tags/text-extraction)描述的提取策略，以获得更复杂的解决方案。

## 7. PDF 到 X 商业图书馆

在前面的部分中，我们描述了开源库。还有几个值得关注的库，但它们是有偿的：

-   [jPDFImages](https://www.qoppa.com/pdfimages/) – jPDFImages 可以从 PDF 文档中的页面创建图像并将它们导出为 JPEG、TIFF 或 PNG 图像。
-   [JPEDAL](https://www.idrsolutions.com/jpedal/) – JPedal 是一个积极开发且功能强大的原生JavaPDF 库 SDK，用于文件的打印、查看和转换
-   [pdfcrowd](https://pdfcrowd.com/web-html-to-pdf-java/) – 这是另一个 Web/HTML 到 PDF 和 PDF 到 Web/HTML 的转换库，具有高级 GUI

## 八. 总结

在本文中，我们讨论了将 PDF 文件转换为各种格式的方法。

本教程的完整实现可以在[GitHub 项目](https://github.com/eugenp/tutorials/tree/master/pdf)中找到——这是一个基于 Maven 的项目。为了进行测试，只需简单地运行示例并在输出文件夹中查看结果。