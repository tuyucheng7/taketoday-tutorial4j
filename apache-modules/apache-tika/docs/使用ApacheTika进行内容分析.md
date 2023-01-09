## 1. 概述

[Apache Tika](https://tika.apache.org/index.html)是一个工具包，用于从各种类型的文档中提取内容和元数据，例如 Word、Excel 和 PDF，甚至是 JPEG 和 MP4 等多媒体文件。

所有基于文本和多媒体的文件都可以使用一个通用接口进行解析，使 Tika 成为一个功能强大且用途广泛的内容分析库。

在本文中，我们将介绍 Apache Tika，包括它的解析 API 以及它如何自动检测文档的内容类型。还将提供工作示例来说明该库的操作。

## 2. 开始

为了使用 Apache Tika 解析文档，我们只需要一个 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-parsers</artifactId>
    <version>1.17</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.tika" AND a%3A"tika-parsers")找到此工件的最新版本。

## 3.解析器API

解析器API 是 Apache Tika 的核心，抽象了解析操作的复杂性。这个 API 依赖于一个方法：

```java
void parse(
  InputStream stream, 
  ContentHandler handler, 
  Metadata metadata, 
  ParseContext context) 
  throws IOException, SAXException, TikaException
```

该方法参数的含义为：

-   stream –从要解析的文档创建的InputStream实例
-   handler——一个ContentHandler对象，接收从输入文档解析的一系列 XHTML SAX 事件；此处理程序然后将处理事件并以特定形式导出结果
-   元数据——一个元数据对象，在解析器内外传递元数据属性
-   context –携带特定上下文信息的ParseContext实例，用于自定义解析过程

如果无法从输入流中读取parse方法，则抛出IOException ，如果无法解析从流中获取的文档，则抛出 TikaException ，如果处理程序无法处理事件，则抛出SAXException 。

解析文档时，Tika 尝试尽可能重用现有的解析器库，例如 Apache POI 或 PDFBox。因此，大多数Parser实现类只是此类外部库的适配器。

在第 5 节中，我们将了解如何使用处理程序和元数据参数来提取文档的内容和元数据。

为了方便起见，我们可以使用外观类Tika来访问Parser API 的功能。

## 4.自动检测

Apache Tika 可以根据文档本身而不是附加信息自动检测文档的类型及其语言。

### 4.1. 文档类型检测

文档类型的检测可以使用Detector接口的实现类来完成，它有一个方法：

```java
MediaType detect(java.io.InputStream input, Metadata metadata) 
  throws IOException
```

此方法获取一个文档及其关联的元数据——然后返回一个MediaType对象，描述关于文档类型的最佳猜测。

元数据并不是检测器所依赖的唯一信息来源。检测器还可以使用魔术字节，这是文件开头附近的一种特殊模式，或者将检测过程委托给更合适的检测器。

事实上，检测器使用的算法是依赖于实现的。

例如，默认检测器首先处理魔术字节，然后处理元数据属性。如果此时还没有找到内容类型，它将使用服务加载器来发现所有可用的检测器并依次尝试它们。

### 4.2. 语言检测

除了文档的类型，Tika 还可以在没有元数据信息帮助的情况下识别其语言。

在以前的 Tika 版本中，使用LanguageIdentifier实例检测文档的语言。

但是，LanguageIdentifier已被弃用以支持 Web 服务，这在[入门](https://tika.apache.org/1.17/detection.html#Language_Detection)文档中并未明确说明。

语言检测服务现在通过抽象类LanguageDetector的子类型提供。使用网络服务，你还可以访问成熟的在线翻译服务，例如 Google Translate 或 Microsoft Translator。

为了简洁起见，我们不会详细介绍这些服务。

## 5. Tika 在行动

本节使用工作示例说明 Apache Tika 功能。

插图方法将包装在一个类中：

```java
public class TikaAnalysis {
    // illustration methods
}
```

### 5.1. 检测文档类型

下面是我们可以用来检测从InputStream读取的文档类型的代码：

```java
public static String detectDocTypeUsingDetector(InputStream stream) 
  throws IOException {
    Detector detector = new DefaultDetector();
    Metadata metadata = new Metadata();

    MediaType mediaType = detector.detect(stream, metadata);
    return mediaType.toString();
}
```

假设我们在类路径中有一个名为tika.txt的 PDF 文件。此文件的扩展名已更改，以试图欺骗我们的分析工具。仍然可以通过测试找到并确认文件的真实类型：

```java
@Test
public void whenUsingDetector_thenDocumentTypeIsReturned() 
  throws IOException {
    InputStream stream = this.getClass().getClassLoader()
      .getResourceAsStream("tika.txt");
    String mediaType = TikaAnalysis.detectDocTypeUsingDetector(stream);

    assertEquals("application/pdf", mediaType);

    stream.close();
}
```

很明显，错误的文件扩展名无法阻止 Tika 找到正确的媒体类型，这要归功于文件开头的魔法字节%PDF 。

为了方便起见，我们可以使用Tika门面类重写检测代码，结果相同：

```java
public static String detectDocTypeUsingFacade(InputStream stream) 
  throws IOException {
 
    Tika tika = new Tika();
    String mediaType = tika.detect(stream);
    return mediaType;
}
```

### 5.2. 提取内容

现在让我们提取文件的内容并将结果作为字符串返回——使用Parser API：

```java
public static String extractContentUsingParser(InputStream stream) 
  throws IOException, TikaException, SAXException {
 
    Parser parser = new AutoDetectParser();
    ContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    ParseContext context = new ParseContext();

    parser.parse(stream, handler, metadata, context);
    return handler.toString();
}
```

给定类路径中包含以下内容的 Microsoft Word 文件：

```plaintext
Apache Tika - a content analysis toolkit
The Apache Tika™ toolkit detects and extracts metadata and text ...
```

可以提取和验证内容：

```java
@Test
public void whenUsingParser_thenContentIsReturned() 
  throws IOException, TikaException, SAXException {
    InputStream stream = this.getClass().getClassLoader()
      .getResourceAsStream("tika.docx");
    String content = TikaAnalysis.extractContentUsingParser(stream);

    assertThat(content, 
      containsString("Apache Tika - a content analysis toolkit"));
    assertThat(content, 
      containsString("detects and extracts metadata and text"));

    stream.close();
}
```

同样，使用Tika类可以更方便地编写代码：

```java
public static String extractContentUsingFacade(InputStream stream) 
  throws IOException, TikaException {
 
    Tika tika = new Tika();
    String content = tika.parseToString(stream);
    return content;
}
```

### 5.3. 提取元数据

除了文档的内容，Parser API 还可以提取元数据：

```java
public static Metadata extractMetadatatUsingParser(InputStream stream) 
  throws IOException, SAXException, TikaException {
 
    Parser parser = new AutoDetectParser();
    ContentHandler handler = new BodyContentHandler();
    Metadata metadata = new Metadata();
    ParseContext context = new ParseContext();

    parser.parse(stream, handler, metadata, context);
    return metadata;
}
```

当类路径中存在 Microsoft Excel 文件时，此测试用例确认提取的元数据是正确的：

```java
@Test
public void whenUsingParser_thenMetadataIsReturned() 
  throws IOException, TikaException, SAXException {
    InputStream stream = this.getClass().getClassLoader()
      .getResourceAsStream("tika.xlsx");
    Metadata metadata = TikaAnalysis.extractMetadatatUsingParser(stream);

    assertEquals("org.apache.tika.parser.DefaultParser", 
      metadata.get("X-Parsed-By"));
    assertEquals("Microsoft Office User", metadata.get("Author"));

    stream.close();
}
```

最后，这是使用Tika外观类的提取方法的另一个版本：

```java
public static Metadata extractMetadatatUsingFacade(InputStream stream) 
  throws IOException, TikaException {
    Tika tika = new Tika();
    Metadata metadata = new Metadata();

    tika.parse(stream, metadata);
    return metadata;
}
```

## 六. 总结

本教程重点介绍使用 Apache Tika 进行内容分析。使用Parser和Detector API，我们可以自动检测文档的类型，并提取其内容和元数据。

对于高级用例，我们可以创建自定义Parser和Detector类以更好地控制解析过程。