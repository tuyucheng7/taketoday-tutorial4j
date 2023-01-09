## 1. 简介

在本文中，我们将了解如何使用[Apache POI](https://poi.apache.org/)创建演示文稿。

该库使我们有可能创建 PowerPoint 演示文稿、阅读现有演示文稿并更改其内容。

## 2.Maven依赖

首先，我们需要将以下依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>3.17</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>3.17</version>
</dependency>
```

[这两个库](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.poi" AND (a%3A"poi" OR a%3A"poi-ooxml"))的最新版本可以从 Maven Central 下载。

## 3.Apache 兴趣点

[Apache POI](https://poi.apache.org/)库同时支持.ppt和.pptx文件，它为 Powerpoint '97(-2007) 文件格式提供 HSLF 实现，为 PowerPoint 2007 OOXML 文件格式提供 XSLF。

由于两种实现不存在通用接口，因此在处理较新的.pptx文件格式时，我们必须记住使用XMLSlideShow、XSLFSlide和XSLFTextShape类。

而且，当需要使用旧的.ppt格式时，请使用HSLFSlideShow、HSLFSlide和HSLFTextParagraph类。

我们将在我们的示例中使用新的.pptx文件格式，我们要做的第一件事是创建一个新的演示文稿，向其中添加一张幻灯片(可能使用预定义的布局)并保存它。

一旦清楚了这些操作，我们就可以开始处理图像、文本和表格了。

### 3.1. 创建新演示文稿

让我们首先创建新的演示文稿：

```java
XMLSlideShow ppt = new XMLSlideShow();
ppt.createSlide();
```

### 3.2. 添加新幻灯片

在向演示文稿添加新幻灯片时，我们还可以选择从预定义的布局创建它。为此，我们首先必须检索保存布局的XSLFSlideMaster(第一个是默认母版)：

```java
XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
```

现在，我们可以检索XSLFSlideLayout并在创建新幻灯片时使用它：

```java
XSLFSlideLayout layout 
  = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);
XSLFSlide slide = ppt.createSlide(layout);
```

让我们看看如何在模板中填充占位符：

```java
XSLFTextShape titleShape = slide.getPlaceholder(0);
XSLFTextShape contentShape = slide.getPlaceholder(1);
```

请记住，每个模板都有其占位符，即XSLFAutoShape子类的实例，每个模板的数量可能不同。

让我们看看如何从幻灯片中快速检索所有占位符：

```java
for (XSLFShape shape : slide.getShapes()) {
    if (shape instanceof XSLFAutoShape) {
        // this is a template placeholder
    }
}
```

### 3.3. 保存演示文稿

一旦我们创建了幻灯片，下一步就是保存它：

```java
FileOutputStream out = new FileOutputStream("powerpoint.pptx");
ppt.write(out);
out.close();
```

## 4. 使用对象

现在我们了解了如何创建新演示文稿、向其中添加幻灯片(使用或不使用预定义模板)并保存它，我们可以开始添加文本、图像、链接和表格。

让我们从文本开始。

### 4.1. 文本

在演示文稿中处理文本时，如在 MS PowerPoint 中，我们必须在幻灯片中创建文本框，添加一个段落，然后将文本添加到该段落：

```java
XSLFTextBox shape = slide.createTextBox();
XSLFTextParagraph p = shape.addNewTextParagraph();
XSLFTextRun r = p.addNewTextRun();
r.setText("Baeldung");
r.setFontColor(Color.green);
r.setFontSize(24.);
```

配置XSLFTextRun时，可以通过选择字体系列以及文本是否应为粗体、斜体或下划线来自定义其样式。

### 4.2. 超级链接

向演示文稿添加文本时，有时添加超链接会很有用。

一旦我们创建了XSLFTextRun对象，我们现在可以添加一个链接：

```java
XSLFHyperlink link = r.createHyperlink();
link.setAddress("http://www.baeldung.com");
```

### 4.3. 图片

我们也可以添加图像：

```java
byte[] pictureData = IOUtils.toByteArray(
  new FileInputStream("logo-leaf.png"));

XSLFPictureData pd
  = ppt.addPicture(pictureData, PictureData.PictureType.PNG);
XSLFPictureShape picture = slide.createPicture(pd);
```

但是，如果配置不当，图像将放置在幻灯片的左上角。为了正确放置它，我们必须配置它的锚点：

```java
picture.setAnchor(new Rectangle(320, 230, 100, 92));
```

XSLFPictureShape接受一个Rectangle作为锚点，这允许我们使用前两个参数配置 x/y 坐标，并使用后两个参数配置图像的宽度/高度。

### 4.4. 列表

演示文稿中的文本通常以列表的形式表示，无论是否编号。

现在让我们定义一个要点列表：

```java
XSLFTextShape content = slide.getPlaceholder(1);
XSLFTextParagraph p1 = content.addNewTextParagraph();
p1.setIndentLevel(0);
p1.setBullet(true);
r1 = p1.addNewTextRun();
r1.setText("Bullet");
```

同样，我们可以定义一个编号列表：

```java
XSLFTextParagraph p2 = content.addNewTextParagraph();
p2.setBulletAutoNumber(AutoNumberingScheme.alphaLcParenRight, 1);
p2.setIndentLevel(1);
XSLFTextRun r2 = p2.addNewTextRun();
r2.setText("Numbered List Item - 1");
```

如果我们正在处理多个列表，那么定义indentLevel以实现正确的项目缩进总是很重要的。

### 4.5. 表

表格是演示文稿中的另一个关键对象，在我们想要显示数据时很有用。

让我们从创建一个表开始：

```java
XSLFTable tbl = slide.createTable();
tbl.setAnchor(new Rectangle(50, 50, 450, 300));
```

现在，我们可以添加一个标题：

```java
int numColumns = 3;
XSLFTableRow headerRow = tbl.addRow();
headerRow.setHeight(50);

for (int i = 0; i < numColumns; i++) {
    XSLFTableCell th = headerRow.addCell();
    XSLFTextParagraph p = th.addNewTextParagraph();
    p.setTextAlign(TextParagraph.TextAlign.CENTER);
    XSLFTextRun r = p.addNewTextRun();
    r.setText("Header " + (i + 1));
    tbl.setColumnWidth(i, 150);
}
```

标题完成后，我们可以向表中添加行和单元格以显示数据：

```java
for (int rownum = 1; rownum < numRows; rownum++) {
    XSLFTableRow tr = tbl.addRow();
    tr.setHeight(50);

    for (int i = 0; i < numColumns; i++) {
        XSLFTableCell cell = tr.addCell();
        XSLFTextParagraph p = cell.addNewTextParagraph();
        XSLFTextRun r = p.addNewTextRun();
        r.setText("Cell " + (irownum + 1));
    }
}
```

在处理表格时，重要的是要提醒你可以自定义每个单元格的边框和背景。

## 5.改变演示文稿

并非总是在制作幻灯片时，我们必须创建一个新的，但我们必须改变一个已经存在的。

让我们看看我们在上一节中创建的那个，然后我们可以开始改变它：

[![演示文稿 1](https://www.baeldung.com/wp-content/uploads/2017/12/presentation-1-300x161.jpg)](https://www.baeldung.com/wp-content/uploads/2017/12/presentation-1.jpg)

### 5.1. 阅读演示文稿

阅读演示文稿非常简单，可以使用接受FileInputStream的XMLSlideShow重载构造函数来完成：

```java
XMLSlideShow ppt = new XMLSlideShow(
  new FileInputStream("slideshow.pptx"));
```

### 5.2. 更改幻灯片顺序

在我们的演示文稿中添加幻灯片时，最好将它们按正确的顺序放置，以使幻灯片正常播放。

如果没有发生这种情况，则可以重新安排幻灯片的顺序。让我们看看如何将第四张幻灯片移动到第二张幻灯片：

```java
List<XSLFSlide> slides = ppt.getSlides();

XSLFSlide slide = slides.get(3);
ppt.setSlideOrder(slide, 1);
```

### 5.3. 删除幻灯片

也可以从演示文稿中删除幻灯片。

让我们看看如何删除第 4 张幻灯片：

```java
ppt.removeSlide(3);
```

## 六. 总结

本快速教程从Java的角度说明了如何使用Apache POI API 读写 PowerPoint 文件。