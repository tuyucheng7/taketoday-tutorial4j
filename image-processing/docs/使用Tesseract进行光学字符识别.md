## 1. 概述

随着人工智能和机器学习技术的进步，我们需要工具来识别图像中的文本。

在本教程中，我们将探索 Tesseract，一种光学字符识别 (OCR) 引擎，并提供一些图像到文本处理的示例。

## 2. 宇宙立方体

[Tesseract](https://github.com/tesseract-ocr/tesseract)是由 HP 开发的开源 OCR 引擎，可识别 100 多种语言，并支持表意文字和从右到左的语言。此外，我们可以训练 Tesseract 识别其他语言。

它包含两个用于图像处理的 OCR 引擎——一个 LSTM(长短期记忆)OCR 引擎和一个通过识别字符模式工作的传统 OCR 引擎。

OCR 引擎使用[Leptonica 库](https://github.com/DanBloomberg/leptonica)打开图像并支持各种输出格式，如纯文本、hOCR(用于 OCR 的 HTML)、PDF 和 TSV。

## 3.设置

Tesseract 可在所有主要操作系统上下载/安装。

例如，如果我们使用的是 macOS，我们可以使用[Homebrew](https://brew.sh/)安装 OCR 引擎：

```shell
brew install tesseract

```

我们将观察到该包默认包含一组语言数据文件，例如英语，以及方向和脚本检测 (OSD)：

```shell
==> Installing tesseract 
==> Downloading https://homebrew.bintray.com/bottles/tesseract-4.1.1.high_sierra.bottle.tar.gz
==> Pouring tesseract-4.1.1.high_sierra.bottle.tar.gz
==> Caveats
This formula contains only the "eng", "osd", and "snum" language data files.
If you need any other supported languages, run `brew install tesseract-lang`.
==> Summary
/usr/local/Cellar/tesseract/4.1.1: 65 files, 29.9MB
```

但是，我们可以安装tesseract-lang模块以支持其他语言：

```shell
brew install tesseract-lang
```

对于 Linux，我们可以使用yum命令安装 Tesseract：

```shell
yum install tesseract
```

同样，让我们添加语言支持：

```shell
yum install tesseract-langpack-eng
yum install tesseract-langpack-spa
```

在这里，我们添加了英语和西班牙语的语言训练数据。

对于 Windows，我们可以从[UB Mannheim 的 Tesseract](https://github.com/UB-Mannheim/tesseract/wiki)获得安装程序。

## 4. Tesseract 命令行

### 4.1. 跑

我们可以使用 Tesseract 命令行工具从图像中提取文本。

例如，让我们拍一张我们网站的快照：

[![截图 2020-02-28-at-6.29.53-PM](https://www.baeldung.com/wp-content/uploads/2020/03/Screen-Shot-2020-02-28-at-6.29.53-PM-1024x419.png)](https://www.baeldung.com/wp-content/uploads/2020/03/Screen-Shot-2020-02-28-at-6.29.53-PM.png)

然后，我们将运行tesseract命令来读取baeldung.png快照并将文本写入output.txt文件：

```shell
tesseract baeldung.png output
```

output.txt文件将如下所示：

```plaintext
a REST with Spring Learn Spring (new!)
The canonical reference for building a production
grade API with Spring.
From no experience to actually building stuff.
y
Java Weekly Reviews
```

我们可以观察到 Tesseract 没有处理图像的全部内容。因为输出的准确性取决于各种参数，如图像质量、语言、页面分割、训练数据和用于图像处理的引擎。

### 4.2. 语言支持

默认情况下，OCR 引擎在处理图像时使用英文。但是，我们可以使用-l参数来声明语言：

让我们看另一个多语言文本的例子：

[![截图 2020-03-08-at-10.43.12-AM](https://www.baeldung.com/wp-content/uploads/2020/03/Screen-Shot-2020-03-08-at-10.43.12-AM-1024x467.png)](https://www.baeldung.com/wp-content/uploads/2020/03/Screen-Shot-2020-03-08-at-10.43.12-AM.png)

首先，让我们用默认的英语语言处理图像：

```shell
tesseract multiLanguageText.png output

```

输出将如下所示：

```plaintext
Der ,.schnelle” braune Fuchs springt
iiber den faulen Hund. Le renard brun
«rapide» saute par-dessus le chien
paresseux. La volpe marrone rapida
salta sopra il cane pigro. El zorro
marron rapido salta sobre el perro
perezoso. A raposa marrom rapida
salta sobre 0 cao preguicoso.
```

然后，让我们用葡萄牙语处理图像：

```shell
tesseract multiLanguageText.png output -l por
```

因此，OCR 引擎也会检测葡萄牙语字母：

```plaintext
Der ,.schnelle” braune Fuchs springt
iber den faulen Hund. Le renard brun
«rapide» saute par-dessus le chien
paresseux. La volpe marrone rapida
salta sopra il cane pigro. El zorro
marrón rápido salta sobre el perro
perezoso. A raposa marrom rápida
salta sobre o cão preguiçoso.
```

同样，我们可以声明一种语言的组合：

```shell
tesseract multiLanguageText.png output -l spa+por
```

在这里，OCR 引擎将主要使用西班牙语，然后使用葡萄牙语进行图像处理。但是，输出可能因我们指定的语言顺序而异。

### 4.3. 页面分割模式

Tesseract 支持多种页面分割模式，如 OSD、自动页面分割和稀疏文本。

我们可以通过使用值为 0 到 13 的–psm参数为各种模式声明页面分割模式：

```shell
tesseract multiLanguageText.png output --psm 1
```

在这里，通过定义值 1，我们声明了使用 OSD 进行图像处理的自动页面分割。

让我们看看所有支持的页面分割模式：

[![屏幕截图 2020-03-08-at-2.28.30-PM](https://www.baeldung.com/wp-content/uploads/2020/03/Screen-Shot-2020-03-08-at-2.28.30-PM-1024x452.png)](https://www.baeldung.com/wp-content/uploads/2020/03/Screen-Shot-2020-03-08-at-2.28.30-PM.png)

### 4.4. OCR引擎模式

同样，我们可以在处理图像时使用各种引擎模式，如传统和 LSTM 引擎。

为此，我们可以使用值为 0 到 3的–oem参数：

```shell
tesseract multiLanguageText.png output --oem 1
```

OCR 引擎模式是：

[![屏幕截图 2020-03-08-at-2.28.48-PM](https://www.baeldung.com/wp-content/uploads/2020/03/Screen-Shot-2020-03-08-at-2.28.48-PM.png)](https://www.baeldung.com/wp-content/uploads/2020/03/Screen-Shot-2020-03-08-at-2.28.48-PM.png)

### 4.5. 苔丝数据

Tesseract 包含两组用于 LSTM OCR 引擎的训练数据——[训练最好的 LSTM 模型和训练有素的 LSTM 模型](https://github.com/tesseract-ocr/tessdata_best)的[快速整数版本](https://github.com/tesseract-ocr/tessdata_fast)。

前者提供更好的准确性，后者提供更快的图像处理速度。

此外，Tesseract 还提供了[经过训练的组合数据](https://github.com/tesseract-ocr/tessdata)，同时支持旧版和 LSTM OCR 引擎。

如果我们在不提供支持训练数据的情况下使用 Legacy OCR 引擎，Tesseract 将抛出错误：

```shell
Error: Tesseract (legacy) engine requested, but components are not present in /usr/local/share/tessdata/eng.traineddata!!
Failed loading language 'eng'
Tesseract couldn't load any languages!
```

因此，我们应该下载所需的.traineddata文件并将它们保存在默认的tessdata位置或使用–tessdata-dir参数声明该位置：

```shell
tesseract multiLanguageText.png output --tessdata-dir /image-processing/tessdata
```

### 4.6. 输出

我们可以声明一个参数来获得所需的输出格式。

例如，要获得可搜索的 PDF 输出：

```shell
tesseract multiLanguageText.png output pdf
```

这将在提供的图像上创建带有可搜索文本层(带有可识别文本)的output.pdf文件。

同样，对于 hOCR 输出：

```shell
tesseract multiLanguageText.png output hocr
```

此外，我们可以使用tesseract –help和tesseract –help-extra命令获取有关 tesseract 命令行用法的更多信息。

## 5.苔丝4J

Tess4J 是 Tesseract API 的Java包装器，它为 JPEG、GIF、PNG 和 BMP 等各种图像格式提供 OCR 支持。

首先，让我们将最新的[tess4j](https://search.maven.org/search?q=g:net.sourceforge.tess4j a:tess4j) Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>net.sourceforge.tess4j</groupId>
    <artifactId>tess4j</artifactId>
    <version>4.5.1</version>
</dependency>
```

然后，我们可以使用tess4j提供的[Tesseract](http://tess4j.sourceforge.net/docs/docs-4.4/net/sourceforge/tess4j/Tesseract.html)类来处理图像：

```java
File image = new File("src/main/resources/images/multiLanguageText.png");
Tesseract tesseract = new Tesseract();
tesseract.setDatapath("src/main/resources/tessdata");
tesseract.setLanguage("eng");
tesseract.setPageSegMode(1);
tesseract.setOcrEngineMode(1);
String result = tesseract.doOCR(image);
```

在这里，我们将数据路径的值设置为包含osd.traineddata和eng.traineddata文件的目录位置。

最后，我们可以验证处理后的图像的String输出：

```java
Assert.assertTrue(result.contains("Der ,.schnelle” braune Fuchs springt"));
Assert.assertTrue(result.contains("salta sopra il cane pigro. El zorro"));
```

此外，我们可以使用setHocr方法来获取 HTML 输出：

```java
tesseract.setHocr(true);
```

默认情况下，库处理整个图像。但是，我们可以在调用doOCR方法时使用[java.awt.Rectangle](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/Rectangle.html)对象来处理图像的特定部分：

```java
result = tesseract.doOCR(imageFile, new Rectangle(1200, 200));
```

与 Tess4J 类似，我们可以使用[Tesseract Platform](https://github.com/bytedeco/javacpp-presets/tree/master/tesseract)将 Tesseract 集成到Java应用程序中。这是基于[JavaCPP 预设](https://github.com/bytedeco/javacpp-presets)库的 Tesseract API 的 JNI 包装器。

## 六. 总结

在本文中，我们通过一些图像处理示例探索了 Tesseract OCR 引擎。

首先，我们检查了用于处理图像的tesseract命令行工具，以及一组参数，如-l、-psm和-oem。

然后，我们探索了tess4j，这是一个将 Tesseract 集成到Java应用程序中的Java包装器。