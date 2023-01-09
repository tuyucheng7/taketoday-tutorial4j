## 1. 概述

[Apache POI](https://poi.apache.org/)是一个Java库，用于处理基于 Office Open XML 标准 (OOXML) 和 Microsoft 的 OLE 2 复合文档格式 (OLE2) 的各种文件格式。

本教程重点介绍[Apache POI 对 Microsoft Word](https://poi.apache.org/)的支持，Microsoft Word是最常用的 Office 文件格式。它介绍了格式化和生成 MS Word 文件所需的步骤以及如何解析该文件。

## 2.Maven依赖

Apache POI 处理 MS Word 文件所需的唯一依赖项是：

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>3.15</version>
</dependency>
```

请点击[此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.poi" AND a%3A"poi-ooxml")获取该神器的最新版本。

## 三、准备工作

现在让我们看一下用于促进 MS Word 文件生成的一些元素。

### 3.1. 资源文件

我们将收集三个文本文件的内容并将它们写入一个名为rest-with-spring.docx的 MS Word 文件。

此外，logo-leaf.png文件用于将图像插入到该新文件中。所有这些文件都存在于类路径中，并由几个静态变量表示：

```java
public static String logo = "logo-leaf.png";
public static String paragraph1 = "poi-word-para1.txt";
public static String paragraph2 = "poi-word-para2.txt";
public static String paragraph3 = "poi-word-para3.txt";
public static String output = "rest-with-spring.docx";
```

对于那些好奇的人，存储库中这些资源文件的内容(其链接在本教程的最后一节中给出)是从该站点的[本课程页面](https://www.baeldung.com/rest-with-spring-course?utm_source=blog&utm_medium=web&utm_content=menu&utm_campaign=rws)中提取的。

### 3.2. 辅助方法

由用于生成 MS Word 文件的逻辑组成的主要方法(将在下一节中进行描述)使用辅助方法：

```java
public String convertTextFileToString(String fileName) {
    try (Stream<String> stream 
      = Files.lines(Paths.get(ClassLoader.getSystemResource(fileName).toURI()))) {
        
        return stream.collect(Collectors.joining(" "));
    } catch (IOException | URISyntaxException e) {
        return null;
    }
}
```

此方法提取位于类路径上的文本文件中包含的内容，该文件的名称是传入的String参数。然后，它连接此文件中的行并返回连接的String。

## 4. MS Word 文件生成

本节说明如何格式化和生成 Microsoft Word 文件。在处理文件的任何部分之前，我们需要有一个XWPFDocument实例：

```java
XWPFDocument document = new XWPFDocument();
```

### 4.1. 格式化标题和副标题

为了创建标题，我们需要首先实例化XWPFParagraph类并在新对象上设置对齐方式：

```java
XWPFParagraph title = document.createParagraph();
title.setAlignment(ParagraphAlignment.CENTER);
```

段落的内容需要包装在XWPFRun对象中。我们可以配置此对象以设置文本值及其关联的样式：

```java
XWPFRun titleRun = title.createRun();
titleRun.setText("Build Your REST API with Spring");
titleRun.setColor("009933");
titleRun.setBold(true);
titleRun.setFontFamily("Courier");
titleRun.setFontSize(20);
```

人们应该能够从它们的名称中推断出设置方法的用途。

我们以类似的方式创建一个包含字幕的XWPFParagraph实例：

```java
XWPFParagraph subTitle = document.createParagraph();
subTitle.setAlignment(ParagraphAlignment.CENTER);
```

让我们也格式化字幕：

```java
XWPFRun subTitleRun = subTitle.createRun();
subTitleRun.setText("from HTTP fundamentals to API Mastery");
subTitleRun.setColor("00CC44");
subTitleRun.setFontFamily("Courier");
subTitleRun.setFontSize(16);
subTitleRun.setTextPosition(20);
subTitleRun.setUnderline(UnderlinePatterns.DOT_DOT_DASH);
```

setTextPosition方法设置字幕和后续图像之间的距离，而setUnderline确定下划线样式。

请注意，我们对标题和副标题的内容进行了硬编码，因为这些语句太短而无法证明使用辅助方法是合理的。

### 4.2. 插入图像

图像还需要包装在XWPFParagraph实例中。我们希望图像水平居中并放置在字幕下方，因此必须将以下代码片段放在上面给出的代码下方：

```java
XWPFParagraph image = document.createParagraph();
image.setAlignment(ParagraphAlignment.CENTER);
```

以下是如何设置此图像与其下方文本之间的距离：

```java
XWPFRun imageRun = image.createRun();
imageRun.setTextPosition(20);
```

从类路径上的文件中获取图像，然后将其插入到具有指定尺寸的 MS Word 文件中：

```java
Path imagePath = Paths.get(ClassLoader.getSystemResource(logo).toURI());
imageRun.addPicture(Files.newInputStream(imagePath),
  XWPFDocument.PICTURE_TYPE_PNG, imagePath.getFileName().toString(),
  Units.toEMU(50), Units.toEMU(50));
```

### 4.3. 格式化段落

以下是我们如何使用poi-word-para1.txt文件中的内容创建第一段：

```java
XWPFParagraph para1 = document.createParagraph();
para1.setAlignment(ParagraphAlignment.BOTH);
String string1 = convertTextFileToString(paragraph1);
XWPFRun para1Run = para1.createRun();
para1Run.setText(string1);
```

显然，段落的创建与标题或副标题的创建类似。这里唯一的区别是使用辅助方法而不是硬编码字符串。

以类似的方式，我们可以使用文件poi-word-para2.txt和poi-word-para3.txt中的内容创建另外两个段落：

```java
XWPFParagraph para2 = document.createParagraph();
para2.setAlignment(ParagraphAlignment.RIGHT);
String string2 = convertTextFileToString(paragraph2);
XWPFRun para2Run = para2.createRun();
para2Run.setText(string2);
para2Run.setItalic(true);

XWPFParagraph para3 = document.createParagraph();
para3.setAlignment(ParagraphAlignment.LEFT);
String string3 = convertTextFileToString(paragraph3);
XWPFRun para3Run = para3.createRun();
para3Run.setText(string3);
```

这三个段落的创建几乎相同，除了一些样式，例如对齐或斜体。

### 4.4. 生成 MS Word 文件

现在我们准备好从文档变量中将 Microsoft Word 文件写入内存：

```java
FileOutputStream out = new FileOutputStream(output);
document.write(out);
out.close();
document.close();
```

本节中的所有代码片段都包含在名为handleSimpleDoc的方法中。

## 5.解析和测试

本节概述了 MS Word 文件的解析和结果验证。

### 5.1. 准备

我们在测试类中声明一个静态字段：

```java
static WordDocument wordDocument;
```

该字段用于引用包含第 3 节和第 4 节中显示的所有代码片段的类的实例。

在解析和测试之前，我们需要初始化上面声明的静态变量，调用handleSimpleDoc方法在当前工作目录下生成rest-with-spring.docx文件：

```java
@BeforeClass
public static void generateMSWordFile() throws Exception {
    WordTest.wordDocument = new WordDocument();
    wordDocument.handleSimpleDoc();
}
```

让我们继续最后一步：解析 MS Word 文件并验证结果。

### 5.2. 解析 MS Word 文件并验证

首先，我们从项目目录中给定的 MS Word 文件中提取内容，并将内容存储在XWPFParagraph列表中：

```java
Path msWordPath = Paths.get(WordDocument.output);
XWPFDocument document = new XWPFDocument(Files.newInputStream(msWordPath));
List<XWPFParagraph> paragraphs = document.getParagraphs();
document.close();
```

接下来，让我们确保标题的内容和样式与我们之前设置的相同：

```java
XWPFParagraph title = paragraphs.get(0);
XWPFRun titleRun = title.getRuns().get(0);
 
assertEquals("Build Your REST API with Spring", title.getText());
assertEquals("009933", titleRun.getColor());
assertTrue(titleRun.isBold());
assertEquals("Courier", titleRun.getFontFamily());
assertEquals(20, titleRun.getFontSize());
```

为了简单起见，我们只验证文件其他部分的内容，而忽略了样式。他们的样式验证和我们对标题所做的类似：

```java
assertEquals("from HTTP fundamentals to API Mastery",
  paragraphs.get(1).getText());
assertEquals("What makes a good API?", paragraphs.get(3).getText());
assertEquals(wordDocument.convertTextFileToString
  (WordDocument.paragraph1), paragraphs.get(4).getText());
assertEquals(wordDocument.convertTextFileToString
  (WordDocument.paragraph2), paragraphs.get(5).getText());
assertEquals(wordDocument.convertTextFileToString
  (WordDocument.paragraph3), paragraphs.get(6).getText());
```

现在我们可以确信rest-with-spring.docx文件的创建已经成功。

## 六. 总结

本教程介绍了 Apache POI 对 Microsoft Word 格式的支持。它完成了生成 MS Word 文件和验证其内容所需的步骤。