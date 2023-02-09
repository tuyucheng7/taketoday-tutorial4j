## 一、概述

便携式文档格式 (PDF) 是一种常见的文档文件格式。用于分发需要保留其原始格式的电子文档。

在本教程中，我们将探索使用 Java 阅读 PDF 文件的两个最流行的库：[Apache PDFBox](https://www.baeldung.com/java-pdf-creation)和[iText](https://www.baeldung.com/java-watermarks-with-itext)。

## 2.设置

我们将使用 Maven 来管理依赖项。

此外，我们将向项目根目录添加一个示例 PDF 文件。该文件包含一个简单的短语“Hello World!”。

接下来，我们将阅读示例 PDF 文件并根据预期结果测试提取的文本。

## 3. 使用 Apache PDFBox

**Apache PDFBox 是一个免费的开源 Java 库，用于处理和操作 PDF 文档**。它的功能包括提取文本、将 PDF 渲染为图像以及合并和拆分 PDF。

让我们将[Apache PDFBox](https://search.maven.org/search?q=g:org.apache.pdfbox AND a:pdfbox)依赖项添加到*pom.xml*中：

```xml
<dependency> 
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>${pdfbox.version}</version>
</dependency>复制
```

下面是一个使用 Apache PDFBox 从 PDF 文件中读取文本的简单示例：

```java
@Test
public void givenSamplePdf_whenUsingApachePdfBox_thenCompareOutput() throws IOException {
    
    String expectedText = "Hello World!\n";
    File file = new File("sample.pdf");
    PDDocument document = PDDocument.load(file);
    PDFTextStripper stripper = new PDFTextStripper();
    String text = stripper.getText(document);
    document.close();
    
    assertEquals(expectedText, text);
}复制
```

在这个例子中，我们创建了一个新的*PDDocument*实例来将 PDF 文件加载到程序中。然后，我们创建了一个新的*PDFTextStripper*实例并调用了*getText()* 以从 PDF 文件中提取文本。

## 4. 使用 iText

**iText 是一个用于在 Java 中生成和使用 PDF 文件的开源库**。它提供了一个简单的 API，用于从 PDF 文件中读取文本。

首先，让我们在*pom.xml中包含*[iText](https://search.maven.org/search?q=g:com.itextpdf AND a:itextpdf)依赖项：

```xml
<dependency> 
    <groupId>com.itextpdf</groupId> 
    <artifactId>itextpdf</artifactId> 
    <version>${itextpdf.version}</version>
</dependency>复制
```

让我们看一个使用 iText PDF 库从 PDF 文件中提取文本的简单示例：

```java
@Test
public void givenSamplePdf_whenUsingiTextPdf_thenCompareOutput() throws IOException {
    
    String expectedText = "Hello World!";
    PdfReader reader = new PdfReader("sample.pdf");
    int pages = reader.getNumberOfPages();
    StringBuilder text = new StringBuilder();
    for (int i = 1; i <= pages; i++) {
        text.append(PdfTextExtractor.getTextFromPage(reader, i));
    }
    reader.close();
    
    assertEquals(expectedText, text.toString());
}复制
```

在这个例子中，我们创建了一个新的*PdfReader*实例来打开 PDF 文件。然后，我们调用*getNumberOfPages()* 方法来获取 PDF 文件的页数。最后，我们遍历页面并调用*PdfTextExtractor* 上的*getTextFromPage()*来提取页面的内容。

## 5.结论

在本文中，我们学习了用 Java 读取 PDF 文件的两种不同方式。我们使用 iText 和 Apache PDFBox 库从示例 PDF 文件中提取文本。这两个库都提供了简单有效的 API，用于从 PDF 文档中提取文本。