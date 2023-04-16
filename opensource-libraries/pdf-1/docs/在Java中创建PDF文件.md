## 1. 简介

在本快速教程中，我们将专注于基于 iText 和 PdfBox 库从头开始创建 PDF 文档。

## 2.Maven依赖

首先，我们需要在项目中包含以下 Maven 依赖项：

```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.10</version>
</dependency>
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.4</version>
</dependency>
```

可以在此处找到最新版本的库：[iText](https://search.maven.org/classic/#search|ga|1|a%3A"itextpdf")和[PdfBox](https://search.maven.org/classic/#search|ga|1|a%3A"pdfbox")。

重要的是要知道 iText 在开源 AGPL 许可和商业许可下可用。如果我们购买商业许可证，我们可以将源代码留给自己，从而保留我们的知识产权。如果我们使用 AGPL 版本，我们需要免费发布我们的源代码。我们可以点击此[链接](https://itextpdf.com/en/blog/technical-notes/how-do-i-make-sure-my-software-complies-agpl-how-can-i-use-itext-free)来确定如何确保我们的软件符合 AGPL。

我们还需要添加一个额外的依赖项，以防我们需要加密我们的文件。Bouncy Castle Provider 包包含加密算法的实现，并且是两个库所必需的：

```xml
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.56</version>
</dependency>

```

可以在此处找到该库的最新版本：[Bouncy Castle Provider](https://mvnrepository.com/artifact/org.bouncycastle/bcprov-jdk15on/1.56)。

## 三、概述

iText 和 PdfBox 都是我们用来创建和操作 pdf 文件的Java库。尽管库的最终输出相同，但它们以不同的方式运行。让我们仔细看看它们中的每一个。

## 4. 在 IText 中创建 Pdf

### 4.1. 在 Pdf 中插入文本

让我们看看如何将带有“Hello World”文本的新文件插入到 pdf 文件中：

```java
Document document = new Document();
PdfWriter.getInstance(document, new FileOutputStream("iTextHelloWorld.pdf"));

document.open();
Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
Chunk chunk = new Chunk("Hello World", font);

document.add(chunk);
document.close();
```

使用 iText 库创建 pdf 是基于操作在Document中实现Elements接口的 对象(在 5.5.10 版本中有 45 个这样的实现)。

我们可以添加到文档并使用的最小元素是Chunk，它基本上是一个带有应用字体的字符串。

此外，我们可以将Chunk与其他元素(如Paragraphs、Section等)结合起来，从而生成美观的文档。

### 4.2. 插入图像

iText 库提供了一种向文档添加图像的简单方法。我们只需要创建一个Image实例并将其添加到Document 中：

```java
Path path = Paths.get(ClassLoader.getSystemResource("Java_logo.png").toURI());

Document document = new Document();
PdfWriter.getInstance(document, new FileOutputStream("iTextImageExample.pdf"));
document.open();
Image img = Image.getInstance(path.toAbsolutePath().toString());
document.add(img);

document.close();
```

### 4.3. 插入表格

如果我们想在 pdf 中添加表格，我们可能会遇到问题。幸运的是，iText 提供了这个开箱即用的功能。 

首先，我们需要创建一个PdfTable对象并在构造函数中为我们的表提供一些列。

然后我们可以通过在新创建的表对象上调用addCell方法来简单地添加新单元格。只要定义了所有必要的单元格，iText 就会创建表格行。这意味着一旦我们创建了一个包含三列的表格，并向其中添加了八个单元格，将只会显示两行，每行包含三个单元格。

让我们看一下这个例子：

```java
Document document = new Document();
PdfWriter.getInstance(document, new FileOutputStream("iTextTable.pdf"));

document.open();

PdfPTable table = new PdfPTable(3);
addTableHeader(table);
addRows(table);
addCustomRows(table);

document.add(table);
document.close();
```

现在我们将创建一个包含三列和三行的新表。我们将第一行视为具有已更改背景颜色和边框宽度的表格标题：

```java
private void addTableHeader(PdfPTable table) {
    Stream.of("column header 1", "column header 2", "column header 3")
      .forEach(columnTitle -> {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setBorderWidth(2);
        header.setPhrase(new Phrase(columnTitle));
        table.addCell(header);
    });
}
```

第二行将由三个只有文本的单元格组成，没有额外的格式：

```java
private void addRows(PdfPTable table) {
    table.addCell("row 1, col 1");
    table.addCell("row 1, col 2");
    table.addCell("row 1, col 3");
}
```

我们还可以在单元格中包含图像。此外，我们可以单独格式化每个单元格。

在此示例中，我们应用水平和垂直对齐调整：

```java
private void addCustomRows(PdfPTable table) 
  throws URISyntaxException, BadElementException, IOException {
    Path path = Paths.get(ClassLoader.getSystemResource("Java_logo.png").toURI());
    Image img = Image.getInstance(path.toAbsolutePath().toString());
    img.scalePercent(10);

    PdfPCell imageCell = new PdfPCell(img);
    table.addCell(imageCell);

    PdfPCell horizontalAlignCell = new PdfPCell(new Phrase("row 2, col 2"));
    horizontalAlignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(horizontalAlignCell);

    PdfPCell verticalAlignCell = new PdfPCell(new Phrase("row 2, col 3"));
    verticalAlignCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
    table.addCell(verticalAlignCell);
}
```

### 4.4. 文件加密

为了使用 iText 库应用权限，我们需要已经创建了 pdf 文档。在我们的示例中，我们将使用之前生成的iTextHelloWorld.pdf文件。

使用PdfReader加载文件后，我们需要创建一个PdfStamper，我们将使用它向文件应用其他内容，如元数据、加密等：

```java
PdfReader pdfReader = new PdfReader("HelloWorld.pdf");
PdfStamper pdfStamper 
  = new PdfStamper(pdfReader, new FileOutputStream("encryptedPdf.pdf"));

pdfStamper.setEncryption(
  "userpass".getBytes(),
  ".getBytes(),
  0,
  PdfWriter.ENCRYPTION_AES_256
);

pdfStamper.close();
```

在我们的示例中，我们使用两个密码加密文件：用户密码(“userpass”)，用户具有只读权限，无法打印它，以及所有者密码(“ownerpass”)，用作允许一个人完全访问 pdf 的主密钥。

如果我们想要允许用户打印 pdf，那么我们可以传递以下参数而不是 0(setEncryption的第三个参数)：

```java
PdfWriter.ALLOW_PRINTING
```

当然，我们也可以混合不同的权限，比如：

```java
PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY
```

请记住，在使用 iText 设置访问权限时，我们还会创建一个临时 pdf，应该将其删除。如果我们不删除它，任何人都可以完全访问它。

## 5.在PdfBox中创建Pdf

### 5.1. 在 Pdf 中插入文本

与iText相比，PdfBox库提供了一个基于流操作的 API。没有像Chunk / Paragraph等类。PDDocument类是内存中的 Pdf 表示，用户通过操作PDPageContentStream类来写入数据。

我们来看看代码示例：

```java
PDDocument document = new PDDocument();
PDPage page = new PDPage();
document.addPage(page);

PDPageContentStream contentStream = new PDPageContentStream(document, page);

contentStream.setFont(PDType1Font.COURIER, 12);
contentStream.beginText();
contentStream.showText("Hello World");
contentStream.endText();
contentStream.close();

document.save("pdfBoxHelloWorld.pdf");
document.close();
```

### 5.2. 插入图像

插入图像也很简单。

我们需要加载一个文件并创建一个PDImageXObject，随后将其绘制在文档上(需要提供精确的 x、y 坐标)：

```java
PDDocument document = new PDDocument();
PDPage page = new PDPage();
document.addPage(page);

Path path = Paths.get(ClassLoader.getSystemResource("Java_logo.png").toURI());
PDPageContentStream contentStream = new PDPageContentStream(document, page);
PDImageXObject image 
  = PDImageXObject.createFromFile(path.toAbsolutePath().toString(), document);
contentStream.drawImage(image, 0, 0);
contentStream.close();

document.save("pdfBoxImage.pdf");
document.close();

```

### 5.3. 插入表格

不幸的是，PdfBox没有提供任何开箱即用的方法来让我们创建表格。在这种情况下，我们可以做的是手动绘制它，逐条绘制每条线，直到我们的绘图类似于我们想要的表格。

### 5.4. 文件加密

PdfBox库提供了为用户加密和调整文件权限的能力。与iText相比，它不需要我们使用已经存在的文件，因为我们只需使用PDDocument 即可。Pdf 文件权限由AccessPermission类处理，我们可以在其中设置用户是否能够修改、提取内容或打印文件。

随后，我们创建一个StandardProtectionPolicy对象，它向文档添加基于密码的保护。我们可以指定两种类型的密码。用户密码允许用户打开具有应用访问权限的文件，所有者密码对文件没有限制：

```java
PDDocument document = new PDDocument();
PDPage page = new PDPage();
document.addPage(page);

AccessPermission accessPermission = new AccessPermission();
accessPermission.setCanPrint(false);
accessPermission.setCanModify(false);

StandardProtectionPolicy standardProtectionPolicy 
  = new StandardProtectionPolicy("ownerpass", "userpass", accessPermission);
document.protect(standardProtectionPolicy);
document.save("pdfBoxEncryption.pdf");
document.close();

```

我们的示例演示了如果用户提供用户密码，则无法修改或打印文件。

## 6. 总结

在本文中，我们学习了如何在两个流行的Java库中创建 pdf 文件。