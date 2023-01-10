## 1. 概述

在本文中，我们将专注于创建一个 . [使用docx4j](https://www.docx4java.org/)库的docx文档。

Docx4j 是一个用于创建和操作 Office OpenXML文件的Java库——这意味着它只能处理.docx文件类型，而旧版本的 Microsoft Word 使用.doc扩展名(二进制文件)。

请注意，从 2007 版本开始，Microsoft Office 支持OpenXML格式。

## 2.Maven 设置

要开始使用 docx4j，我们需要将所需的依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.docx4j</groupId>
    <artifactId>docx4j</artifactId>
    <version>3.3.5</version>
</dependency>
<dependency> 
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.1</version>
</dependency>
```

[请注意，我们始终可以在Maven Central Repository](https://search.maven.org/classic/#search|gav|1|g%3A"org.docx4j" AND a%3A"docx4j")中查找最新的依赖项版本。

需要JAXB依赖项，因为 docx4j 在底层使用此库来编组/解组 docx 文件中的XML部分。

## 3.创建一个docx文件文档

### 3.1. 文本元素和样式

让我们首先看看如何创建一个简单的docx文件——带有一个文本段落：

```java
WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
mainDocumentPart.addStyledParagraphOfText("Title", "Hello World!");
mainDocumentPart.addParagraphOfText("Welcome To Baeldung");
File exportFile = new File("welcome.docx");
wordPackage.save(exportFile);

```

这是生成的welcome.docx文件：

[![im1](https://www.baeldung.com/wp-content/uploads/2017/10/im1-300x236.png)](https://www.baeldung.com/wp-content/uploads/2017/10/im1.png)

要创建新文档，我们必须使用WordprocessingMLPackage，它表示OpenXML格式的docx文件，而MainDocumentPart类包含主要document.xml部分的表示。

为清楚起见，让我们解压缩welcome.docx文件，然后打开word/document.xml文件以查看 XML 表示形式：

```java
<w:body>
    <w:p>
        <w:pPr>
            <w:pStyle w:val="Title"/>
        </w:pPr>
        <w:r>
            <w:t>Hello World!</w:t>
        </w:r>
    </w:p>
    <w:p>
        <w:r>
            <w:t>Welcome To Baeldung!</w:t>
        </w:r>
    </w:p>
</w:body>
```

正如我们所见，每个句子都由段落 ( p )内的文本 ( t )的运行 ( r ) 表示，这就是addParagraphOfText()方法的用途。

addStyledParagraphOfText()的作用不止于此；它创建一个段落属性 ( pPr )，其中包含要应用于该段落的样式。

简单地说，段落声明单独的运行，并且每个运行包含一些文本元素：

[![零件](https://www.baeldung.com/wp-content/uploads/2017/10/p-r-t.png)](https://www.baeldung.com/wp-content/uploads/2017/10/p-r-t.png)

要创建美观的文档，我们需要完全控制这些元素(段落、运行和文本)。

那么，让我们探索如何使用runProperties ( RPr ) 对象来风格化我们的内容：

```java
ObjectFactory factory = Context.getWmlObjectFactory();
P p = factory.createP();
R r = factory.createR();
Text t = factory.createText();
t.setValue("Welcome To Baeldung");
r.getContent().add(t);
p.getContent().add(r);
RPr rpr = factory.createRPr();       
BooleanDefaultTrue b = new BooleanDefaultTrue();
rpr.setB(b);
rpr.setI(b);
rpr.setCaps(b);
Color green = factory.createColor();
green.setVal("green");
rpr.setColor(green);
r.setRPr(rpr);
mainDocumentPart.getContent().add(p);
File exportFile = new File("welcome.docx");
wordPackage.save(exportFile);
```

结果如下所示：

[![im2a](https://www.baeldung.com/wp-content/uploads/2017/10/im2a-300x236.png)](https://www.baeldung.com/wp-content/uploads/2017/10/im2a.png)

在我们分别使用createP()、createR()和createText()创建段落、运行和文本元素后，我们声明了一个新的runProperties对象 ( RPr ) 以向文本元素添加一些样式。

rpr对象用于设置格式属性，粗体 ( B )、斜体 ( I ) 和大写 ( Caps )，这些属性使用setRPr()方法应用于文本运行。

### 3.2. 使用图像

Docx4j 提供了一种将图像添加到我们的 Word 文档的简单方法：

```java
File image = new File("image.jpg" );
byte[] fileContent = Files.readAllBytes(image.toPath());
BinaryPartAbstractImage imagePart = BinaryPartAbstractImage
  .createImagePart(wordPackage, fileContent);
Inline inline = imagePart.createImageInline(
  "Baeldung Image (filename hint)", "Alt Text", 1, 2, false);
P Imageparagraph = addImageToParagraph(inline);
mainDocumentPart.getContent().add(Imageparagraph);
```

下面是addImageToParagraph()方法的实现：

```java
private static P addImageToParagraph(Inline inline) {
    ObjectFactory factory = new ObjectFactory();
    P p = factory.createP();
    R r = factory.createR();
    p.getContent().add(r);
    Drawing drawing = factory.createDrawing();
    r.getContent().add(drawing);
    drawing.getAnchorOrInline().add(inline);
    return p;
}
```

首先，我们创建了包含要添加到主文档部分的图像的文件，然后，我们将表示图像的字节数组与wordMLPackage对象链接起来。

创建图像部分后，我们需要使用createImageInline( ) 方法创建一个内联对象。

addImageToParagraph ()方法将内联对象嵌入到绘图中，以便可以将其添加到运行中。

最后，像文本段落一样，包含图像的段落被添加到mainDocumentPart中。

这是生成的文档：

[![im3a](https://www.baeldung.com/wp-content/uploads/2017/10/im3a-298x300.png)](https://www.baeldung.com/wp-content/uploads/2017/10/im3a.png)

### 3.3. 创建表

Docx4j 还使操作表 (Tbl)、行 (Tr) 和列 (Tc) 变得非常容易。

让我们看看如何创建一个 3×3 的表格并向其中添加一些内容：

```java
int writableWidthTwips = wordPackage.getDocumentModel()
  .getSections().get(0).getPageDimensions().getWritableWidthTwips();
int columnNumber = 3;
Tbl tbl = TblFactory.createTable(3, 3, writableWidthTwips/columnNumber);     
List<Object> rows = tbl.getContent();
for (Object row : rows) {
    Tr tr = (Tr) row;
    List<Object> cells = tr.getContent();
    for(Object cell : cells) {
        Tc td = (Tc) cell;
        td.getContent().add(p);
    }
}
```

给定一些行和列，createTable()方法创建一个新的Tbl对象，第三个参数指的是以缇为单位的列宽(这是一种距离测量——1/1440 英寸)。

创建后，我们可以遍历tbl对象的内容，并将Paragraph对象添加到每个单元格中。

让我们看看最终结果是什么样的：

[![im4a](https://www.baeldung.com/wp-content/uploads/2017/10/im4a-300x236.png)](https://www.baeldung.com/wp-content/uploads/2017/10/im4a.png)

## 4. 读取 Docx 文件文档

现在我们已经了解了如何使用 docx4j 创建文档，让我们看看如何读取现有的 docx 文件并打印其内容：

```java
File doc = new File("helloWorld.docx");
WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
  .load(doc);
MainDocumentPart mainDocumentPart = wordMLPackage
  .getMainDocumentPart();
String textNodesXPath = "//w:t";
List<Object> textNodes= mainDocumentPart
  .getJAXBNodesViaXPath(textNodesXPath, true);
for (Object obj : textNodes) {
    Text text = (Text) ((JAXBElement) obj).getValue();
    String textValue = text.getValue();
    System.out.println(textValue);
}
```

在此示例中，我们使用load()方法基于现有的helloWorld.docx文件创建了一个WordprocessingMLPackage对象。

之后，我们使用XPath表达式 ( //w:t ) 从主文档部分获取所有文本节点。

getJAXBNodesViaXPath ()方法返回JAXBElement对象的列表。

结果，mainDocumentPart对象内的所有文本元素都打印在控制台中。

请注意，我们始终可以解压缩我们的 docx 文件以更好地了解 XML 结构，这有助于分析问题，并更好地了解如何解决这些问题。

## 5.总结

在本文中，我们发现了 docx4j 如何使对 MSWord 文档执行复杂操作变得更加容易，例如创建段落、表格、文档部分和添加图像。