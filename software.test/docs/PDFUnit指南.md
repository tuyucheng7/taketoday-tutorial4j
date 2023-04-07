## **一、简介**

在本文中，我们将探索用于测试 PDF 的[PDFUnit](http://www.pdfunit.com/)库。

使用 PDFUnit 提供的强大 API，我们可以处理 PDF 并验证文本、图像、书签和许多其他内容。

我们最终可以使用 PDFUnit 编写相当复杂的测试用例，但让我们从适用于大多数生产 PDF 的最常见用例开始，并为进一步开发提供良好的基础。

重要说明：PDFUnit 可免费用于评估目的，但不可用于商业用途。

## **2. 安装与设置**

当前版本的 PDFUnit (2016.05) 在 Maven 中央存储库中不可用。因此，我们需要手动下载和安装 jar。请按照[官方网站上的说明](http://www.pdfunit.com/en/documentation/java/install_update/classpath.html)进行手动安装。

## **3.页数**

让我们从一个简单的示例开始，该示例简单地验证给定 PDF 文件中的页数：

```java
@Test
public void givenSinglePage_whenCheckForOnePage_thenSuccess() {
 
    String filename = getFilePath("sample.pdf");
    AssertThat.document(filename)
      .hasNumberOfPages(1);
}复制
```

getFilePath *()*是一个简单的方法，与 PDFUnit 无关，它只是将 PDF 文件的路径作为 String*返回*。

所有 PDFUnit 测试都从调用*AssertThat.document()*开始，它准备文档进行测试。hasNumberOfPages *()*将一个*int*作为参数，指定 PDF 必须包含的页数。在我们的例子中，文件*sample.pdf*只包含一页，因此测试成功。

如果实际页数与参数不匹配，则抛出异常。

让我们看一个有关如何在抛出异常时测试场景的示例：

```java
@Test(expected = PDFUnitValidationException.class)
public void givenMultiplePages_whenCheckForOnePage_thenException() {
    String filename = getFilePath("multiple_pages.pdf");
    AssertThat.document(filename)
      .hasNumberOfPages(1);
}复制
```

在这种情况下，文件*multiple_pages.pdf*包含多个页面。因此抛出*PDFUnitValidationException异常。*

## **4. 受密码保护的文件**

处理受密码保护的文件也非常简单。唯一的区别在于调用*AssertThat.document()*时我们需要**传递第二个参数，即文件的密码**：

```java
@Test
public void givenPwdProtected_whenOpenWithPwd_thenSuccess() {
    String filename = getFilePath("password_protected.pdf");
    String userPassword = "pass1";

    AssertThat.document(filename, userPassword)
      .hasNumberOfPages(1);
}复制
```

## **5.文本比较**

现在让我们将测试 PDF ( *sample.pdf* ) 与参考 PDF ( *sample_reference.pdf* ) 进行比较。如果被测文件的文本与参考文件相同，则测试成功：

```java
@Test
public void whenMatchWithReferenceFile_thenSuccess() {
    String testFileName = getFilePath("sample.pdf");
    String referenceFileName = getFilePath("sample_reference.pdf");

    AssertThat.document(testFileName)
      .and(referenceFileName)
      .haveSameText();
}复制
```

haveSameText *()*是完成比较两个文件之间文本的所有工作的方法。

如果我们不想比较两个文件之间的完整文本，而是想验证特定页面上特定文本的存在，containing *()*方法就派上用场了：

```java
@Test
public void whenPage2HasExpectedText_thenSuccess() {
 
    String filename = getFilePath("multiple_pages.pdf");
    String expectedText = "Chapter 1, content";
 
    AssertThat.document(filename)
      .restrictedTo(PagesToUse.getPage(2))
      .hasText()
      .containing(expectedText);
}复制
```

*如果multiple_pages.pdf*文件的第 2 页在页面上的任意位置包含*expectedText*，则上述测试成功。*除了expectedText*之外，任何其他文本的缺失或存在都不会影响结果。

现在让我们通过验证特定文本是否出现在页面的特定区域而不是整个页面中来使测试更具限制性。为此，我们需要了解*PageRegion*的概念。

PageRegion是被测实际页面中的矩形子部分*。*PageRegion必须完全*属于*实际页面。如果*PageRegion*的任何部分落在实际页面之外，就会导致错误。

PageRegion*由*四个元素定义：

1.  *leftX* – 垂直线距页面最左侧垂直边缘的毫米数
2.  *upperY* – 水平线距页面最顶部水平边缘的毫米数
3.  *宽度*——区域的宽度，以毫米为单位
4.  *高度*——区域的高度（毫米）

为了更好地理解这个概念，让我们使用以下属性创建一个*PageRegion ：*

1.  *左 X* = 20
2.  *上Y* = 10
3.  *宽度*= 150
4.  *高度*= 50

这是上述 PageRegion 的近似图像表示*：*

[![页面区域](https://www.baeldung.com/wp-content/uploads/2017/07/PageRegion.png)](https://www.baeldung.com/wp-content/uploads/2017/07/PageRegion.png)

概念清楚了，相应的测试用例就相对简单了：

```java
@Test
public void whenPageRegionHasExpectedtext_thenSuccess() {
    String filename = getFilePath("sample.pdf");
    int leftX = 20;
    int upperY = 10;
    int width = 150;
    int height = 50;
    PageRegion regionTitle = new PageRegion(leftX, upperY, width, height);

    AssertThat.document(filename)
      .restrictedTo(PagesToUse.getPage(1))
      .restrictedTo(regionTitle)
      .hasText()
      .containing("Adobe Acrobat PDF Files");
}复制
```

在这里，我们在 PDF 文件的第 1 页中创建了一个*PageRegion*，并验证了该区域中的文本。

## **6.书签**

让我们看几个与书签相关的测试用例：

```java
@Test
public void whenHasBookmarks_thenSuccess() {
    String filename = getFilePath("with_bookmarks.pdf");

    AssertThat.document(filename)
      .hasNumberOfBookmarks(5);
}复制
```

如果 PDF 文件恰好有五个书签，则此测试将成功。

书签的标签也可以验证：

```java
@Test
public void whenHasBookmarksWithLabel_thenSuccess() {
    String filename = getFilePath("with_bookmarks.pdf");

    AssertThat.document(filename)
      .hasBookmark()
      .withLabel("Chapter 2")
      .hasBookmark()
      .withLinkToPage(3);
}复制
```

在这里，我们正在检查给定的 PDF 是否具有带有文本“第 2 章”的书签。它还会验证是否有链接到第 3 页的书签。

## **7. 图片**

图像是 PDF 文档的另一个重要方面。再次对 PDF 中的图像进行单元测试非常简单：

```java
@Test
public void whenHas2DifferentImages_thenSuccess() {
    String filename = getFilePath("with_images.pdf");

    AssertThat.document(filename)
      .hasNumberOfDifferentImages(2);
}复制
```

此测试验证 PDF 中确实使用了两个不同的图像。不同图像的数量是指PDF文档中实际存储的图像数量。

但是，文档中可能存储了一个徽标图像，但显示在文档的每一页上。这是指可见图像的数量，可以多于不同图像的数量。

让我们看看如何验证可见图像：

```java
@Test
public void whenHas2VisibleImages_thenSuccess() {
    String filename = getFilePath("with_images.pdf");
    AssertThat.document(filename)
      .hasNumberOfVisibleImages(2);
}复制
```

**PDFUnit 足够强大，可以逐字节比较图像的内容。**这也意味着 PDF 中的图像和参考图像必须完全相等。

由于字节比较，不同格式的图像如 BMP 和 PNG 被认为是不相等的：

```java
@Test
public void whenImageIsOnAnyPage_thenSuccess() {
    String filename = getFilePath("with_images.pdf");
    String imageFile = getFilePath("Superman.png");

    AssertThat.document(filename)
      .restrictedTo(AnyPage.getPreparedInstance())
      .hasImage()
      .matching(imageFile);
}复制
```

*注意这里AnyPage*的使用。我们并未将图像的出现限制在任何特定页面，而是在整个文档的任何页面上。

除了表示文件名的*字符串*之外，要比较的图像可以采用*BufferedImage*、*File*、*InputStream*或*URL的形式。*

## **8. 嵌入式文件**

某些 PDF 文档带有嵌入式文件或附件。也有必要测试这些：

```java
@Test
public void whenHasEmbeddedFile_thenSuccess() {
    String filename = getFilePath("with_attachments.pdf");
 
    AssertThat.document(filename)
      .hasEmbeddedFile();
}复制
```

这将验证被测文档是否至少有一个嵌入文件。

我们也可以验证嵌入文件的名称：

```java
@Test
public void whenHasmultipleEmbeddedFiles_thenSuccess() {
    String filename = getFilePath("with_attachments.pdf");

    AssertThat.document(filename)
      .hasNumberOfEmbeddedFiles(4)
      .hasEmbeddedFile()
      .withName("complaintform1.xls")
      .hasEmbeddedFile()
      .withName("complaintform2.xls")
      .hasEmbeddedFile()
      .withName("complaintform3.xls");
}复制
```

**我们可以更进一步并验证嵌入文件的内容：**

```java
@Test
public void whenEmbeddedFileContentMatches_thenSuccess() {
    String filename = getFilePath("with_attachments.pdf");
    String embeddedFileName = getFilePath("complaintform1.xls");

    AssertThat.document(filename)
      .hasEmbeddedFile()
      .withContent(embeddedFileName);
}复制
```

本节中的所有示例都相对简单且不言自明。

## **9.结论**

在本教程中，我们看到了几个涵盖与 PDF 测试相关的最常见用例的示例。

然而，PDFUnit 能做的还有很多；请务必访问[文档页面](http://www.pdfunit.com/en/documentation/java/)以了解更多信息。