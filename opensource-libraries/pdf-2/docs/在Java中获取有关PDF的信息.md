## 1. 概述

在本教程中，我们将了解使用Java中的[iText](https://itextpdf.com/)和[PDFBox](https://pdfbox.apache.org/)库获取 PDF 文件信息的不同方法。

## 2. 使用 iText 库

iText 是一个用于[创建](https://www.baeldung.com/java-pdf-creation)和操作 PDF 文档的库。此外，它还提供了一种获取文档信息的简便方法。

### 2.1. Maven 依赖

让我们首先在pom.xml中声明[itextpdf](https://search.maven.org/search?q=g:com.itextpdf AND a:itextpdf)依赖项：

```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itextpdf</artifactId>
    <version>5.5.13.3</version>
</dependency>
```

### 2.2. 获取页数

让我们使用getNumberOfPages()方法创建一个PdfInfoIText类，该方法返回 PDF 文档中的页数：

```java
public class PdfInfoIText {

    public static int getNumberOfPages(final String pdfFile) throws IOException {
        PdfReader reader = new PdfReader(pdfFile);
        int pages = reader.getNumberOfPages();
        reader.close();
        return pages;
    }
}
```

在我们的示例中，首先，我们使用PdfReader类从File对象加载 PDF 。之后，我们使用getNumberOfPages()方法。最后，我们关闭PdfReader对象。让我们为它声明一个测试用例：

```java
@Test
public void givenPdf_whenGetNumberOfPages_thenOK() throws IOException {
    Assert.assertEquals(4, PdfInfoIText.getNumberOfPages(PDF_FILE));
}
```

在我们的测试用例中，我们验证存储在测试资源文件夹中的给定 PDF 文件的页数。

### 2.3. 获取 PDF 元数据

现在让我们看看如何获取文档的元数据。我们将使用getInfo()方法。该方法可以获取文件的信息，如标题、作者、创建日期、创建者、制作者等。让我们将getInfo()方法添加到我们的PdfInfoIText类：

```java
public static Map<String, String> getInfo(final String pdfFile) throws IOException {
    PdfReader reader = new PdfReader(pdfFile);
    Map<String, String> info = reader.getInfo();
    reader.close();
    return info;
}
```

现在，让我们编写一个测试用例来获取文档的创建者和制作者：

```java
@Test
public void givenPdf_whenGetInfo_thenOK() throws IOException {
    Map<String, String> info = PdfInfoIText.getInfo(PDF_FILE);
    Assert.assertEquals("LibreOffice 4.2", info.get("Producer"));
    Assert.assertEquals("Writer", info.get("Creator"));
}
```

### 2.4. 了解 PDF 密码保护

我们现在想知道文档是否有密码保护。为此，让我们将isEncrypted()方法添加到PdfInfoIText类：

```java
public static boolean isPasswordRequired(final String pdfFile) throws IOException {
    PdfReader reader = new PdfReader(pdfFile);
    boolean isEncrypted = reader.isEncrypted();
    reader.close();
    return isEncrypted;
}
```

现在，让我们创建一个测试用例来查看此方法的行为方式：

```java
@Test
public void givenPdf_whenIsPasswordRequired_thenOK() throws IOException {
    Assert.assertFalse(PdfInfoIText.isPasswordRequired(PDF_FILE));
}
```

在下一节中，我们将使用 PDFBox 库完成相同的工作。

## 3. 使用 PDFBox 库

获取有关 PDF 文件的信息的另一种方法是使用 Apache PDFBox 库。

### 3.1. Maven 依赖

我们需要在项目中包含[pdfbox](https://search.maven.org/search?q=g:org.apache.pdfbox AND a:pdfbox) Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.0-RC1</version>
</dependency>
```

### 3.2. 获取页数

PDFBox 库提供了处理 PDF 文档的能力。要获取页数，我们只需使用Loader类及其loadPDF()方法从File对象加载文档。之后，我们使用PDDocument类的getNumberOfPages()方法：

```java
public class PdfInfoPdfBox {

    public static int getNumberOfPages(final String pdfFile) throws IOException {
        File file = new File(pdfFile);
        PDDocument document = Loader.loadPDF(file);
        int pages = document.getNumberOfPages();
        document.close();
        return pages;
    }
}
```

让我们为它创建一个测试用例：

```java
@Test
public void givenPdf_whenGetNumberOfPages_thenOK() throws IOException {
    Assert.assertEquals(4, PdfInfoPdfBox.getNumberOfPages(PDF_FILE));
}
```

### 3.3. 获取 PDF 元数据

获取 PDF 元数据也很简单。 我们需要使用getDocumentInformation()方法。此方法将文档元数据(例如文档的作者或其创建日期)作为PDDocumentInformation对象返回：

```java
public static PDDocumentInformation getInfo(final String pdfFile) throws IOException {
    File file = new File(pdfFile);
    PDDocument document = Loader.loadPDF(file);
    PDDocumentInformation info = document.getDocumentInformation();
    document.close();
    return info;
}
```

让我们为它写一个测试用例：

```java
@Test
public void givenPdf_whenGetInfo_thenOK() throws IOException {
    PDDocumentInformation info = PdfInfoPdfBox.getInfo(PDF_FILE);
    Assert.assertEquals("LibreOffice 4.2", info.getProducer());
    Assert.assertEquals("Writer", info.getCreator());
}
```

在这个测试用例中，我们只验证文档的制作者和创建者。

### 3.4. 了解 PDF 密码保护

我们可以使用PDDocument类的isEncrypted()方法检查 PDF 是否受密码保护：

```java
public static boolean isPasswordRequired(final String pdfFile) throws IOException {
    File file = new File(pdfFile);
    PDDocument document = Loader.loadPDF(file);
    boolean isEncrypted = document.isEncrypted();
    document.close();
    return isEncrypted;
}
```

让我们创建一个验证密码保护的测试用例：

```java
@Test
public void givenPdf_whenIsPasswordRequired_thenOK() throws IOException {
    Assert.assertFalse(PdfInfoPdfBox.isPasswordRequired(PDF_FILE));
}
```

## 4. 总结

在本文中，我们学习了如何使用两个流行的Java库获取有关 PDF 文件的信息。