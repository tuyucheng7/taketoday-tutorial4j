## 1. 概述

在本快速教程中，我们将了解如何使用Java中的 OpenPDF 以编程方式将 HTML 文件转换为 PDF 格式。

## 2.打开PDF

OpenPDF 是一个免费的Java库，用于在 LGPL 和 MPL 许可下创建和编辑 PDF 文件。它是 iText 程序的一个分支。事实上，在版本 5 之前，使用 OpenPDF 生成 PDF 的代码几乎与 iText API 相同。它是一个维护良好的用Java生成 PDF 的解决方案。

## 3.使用飞碟转换

Flying Saucer 是一个Java库，它允许我们使用 CSS 2.1 呈现格式良好的 XML(或 XHTML)以进行样式和格式化，生成 PDF、图片和摆动面板的输出。

### 3.1. Maven 依赖项

我们将从 Maven 依赖项开始：

```xml
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.13.1</version>
</dependency>
<dependency>
    <groupId>org.xhtmlrenderer</groupId>
    <artifactId>flying-saucer-pdf-openpdf</artifactId>
    <version>9.1.20</version>
</dependency>

```

我们将使用库[jsoup](https://search.maven.org/classic/#search|gav|1|g%3A"org.jsoup" AND a%3A"jsoup")来解析 HTML 文件、输入流、URL，甚至字符串。它提供 DOM(文档对象模型)遍历功能、CSS 和类似 jQuery 的选择器以从 HTML 中提取数据。

[flying-saucer-pdf-openpdf](https://search.maven.org/classic/#search|gav|1|g%3A"org.xhtmlrenderer" AND a%3A"flying-saucer-pdf-openpdf")库接受 HTML 文件的XML 表示作为输入，应用 CSS 格式化和样式，并输出 PDF。

### 3.2. HTML 到 PDF

在本教程中，我们将尝试涵盖你在 HTML 到 PDF 转换中可能遇到的简单实例，例如使用 Flying Saucer 和 OpenPDF 的 HTML 图像和样式。我们还将讨论如何自定义代码以接受外部样式、图像和字体。

让我们来看看我们的示例 HTML 代码：

```xml
<html>
    <head>
        <style>
            .center_div {
                border: 1px solid gray;
                margin-left: auto;
                margin-right: auto;
                width: 90%;
                background-color: #d0f0f6;
                text-align: left;
                padding: 8px;
            }
        </style>
        <link href="style.css" rel="stylesheet">
    </head>
    <body>
        <div class="center_div">
            <h1>Hello Baeldung!</h1>
            <img src="Java_logo.png">
            <div class="myclass">
                <p>This is the tutorial to convert html to pdf.</p>
            </div>
        </div>
    </body>
</html>
```

要将 HTML 转换为 PDF，我们将首先从定义的位置读取 HTML 文件：

```java
File inputHTML = new File(HTML);
```

下一步，我们将使用[jsoup](https://www.baeldung.com/java-with-jsoup)将上述 HTML 文件转换为jsoup Document以呈现 XHTML。

下面给出的是 XHTML 输出：

```java
Document document = Jsoup.parse(inputHTML, "UTF-8");
document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
return document;
```

现在，作为最后一步，让我们从上一步生成的 XHTML 文档创建一个 PDF。ITextRenderer将获取此 XHTML 文档并创建一个输出 PDF 文件。请注意，我们将代码包装在一个[try-with-resources](https://www.baeldung.com/java-try-with-resources)块中，以确保关闭输出流：


```java
try (OutputStream outputStream = new FileOutputStream(outputPdf)) {
    ITextRenderer renderer = new ITextRenderer();
    SharedContext sharedContext = renderer.getSharedContext();
    sharedContext.setPrint(true);
    sharedContext.setInteractive(false);
    renderer.setDocumentFromString(xhtml.html());
    renderer.layout();
    renderer.createPDF(outputStream);
}
```

### 3.3. 自定义外部样式

我们可以将 HTML 输入文档中使用的其他字体注册到ITextRenderer ，以便它可以在生成 PDF 时包含它们：

```java
renderer.getFontResolver().addFont(getClass().getClassLoader().getResource("fonts/PRISTINA.ttf").toString(), true);
```

ITextRenderer可能需要注册相对 URL 才能访问外部样式：

```java
String baseUrl = FileSystems.getDefault()
  .getPath("src/main/resources/")
  .toUri().toURL().toString();
renderer.setDocumentFromString(xhtml, baseUrl);

```

我们可以通过实现ReplacedElementFactory来自定义图像相关的属性：

```java
public ReplacedElement createReplacedElement(LayoutContext lc, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
    Element e = box.getElement();
    String nodeName = e.getNodeName();
    if (nodeName.equals("img")) {
        String imagePath = e.getAttribute("src");
        try {
            InputStream input = new FileInputStream("src/main/resources/"+imagePath);
            byte[] bytes = IOUtils.toByteArray(input);
            Image image = Image.getInstance(bytes);
            FSImage fsImage = new ITextFSImage(image);
            if (cssWidth != -1 || cssHeight != -1) {
                fsImage.scale(cssWidth, cssHeight);
            } else {
                fsImage.scale(2000, 1000);
            }
            return new ITextImageElement(fsImage);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    return null;
}
```

注意：上面的代码将基本路径作为图像路径的前缀，并设置默认图像大小以防未提供。

然后，我们可以将自定义ReplacedElementFactory添加到SharedContext：

```java
sharedContext.setReplacedElementFactory(new CustomElementFactoryImpl());

```

## 4. 使用 Open HTML 转换

Open HTML to PDF 是一个Java库，它使用 CSS 2.1(及更高版本的标准)进行布局和格式化，将格式良好的 XML/XHTML(甚至一些 HTML5)输出为 PDF 或图片。

### 4.1. Maven 依赖项

除了上面显示的jsoup库之外，我们还需要将几个 Open HTML to PDF 库添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>com.openhtmltopdf</groupId>
    <artifactId>openhtmltopdf-core</artifactId>
    <version>1.0.6</version>
</dependency>
<dependency>
    <groupId>com.openhtmltopdf</groupId>
    <artifactId>openhtmltopdf-pdfbox</artifactId>
    <version>1.0.6</version>
</dependency>
```

库[openhtmltopdf-core](https://search.maven.org/classic/#search|gav|1|g%3A"com.openhtmltopdf" AND a%3A"openhtmltopdf-core") 呈现格式良好的 XML/XHTML，而[openhtmltopdf-pdfbox](https://search.maven.org/classic/#search|gav|1|g%3A"com.openhtmltopdf" AND a%3A"openhtmltopdf-pdfbox")从呈现的 XHTML 表示 生成 PDF 文档。

### 4.2. HTML 到 PDF

在此程序中，要使用 Open HTML 将 HTML 转换为 PDF，我们将使用 3.2 节中提到的相同 HTML。我们将首先将 HTML 文件转换为jsoup 文档，如我们在前面的示例中所示。

在最后一步中，要从 XHTML 文档创建 PDF，PdfRendererBuilder将采用此 XHTML 文档并创建一个 PDF 作为输出文件。同样，我们使用try-with-resources来包装我们的逻辑：


```java
try (OutputStream os = new FileOutputStream(outputPdf)) {
    PdfRendererBuilder builder = new PdfRendererBuilder();
    builder.withUri(outputPdf);
    builder.toStream(os);
    builder.withW3cDocument(new W3CDom().fromJsoup(doc), "/");
    builder.run();
}
```

### 4.3. 自定义外部样式

我们可以将 HTML 输入文档中使用的其他字体注册到PdfRendererBuilder ，以便它可以将它们包含在 PDF 中：

```java
builder.useFont(new File(getClass().getClassLoader().getResource("fonts/PRISTINA.ttf").getFile()), "PRISTINA");
```

PdfRendererBuilder库可能还需要注册相对 URL 以访问外部样式，类似于我们之前的示例：

```java
String baseUrl = FileSystems.getDefault()
  .getPath("src/main/resources/")
  .toUri().toURL().toString();
builder.withW3cDocument(new W3CDom().fromJsoup(doc), baseUrl);
```

## 5.总结

在本文中，我们学习了如何使用 Flying Saucer 和 Open HTML 将 HTML 转换为 PDF。我们还讨论了如何注册外部字体、样式和定制。