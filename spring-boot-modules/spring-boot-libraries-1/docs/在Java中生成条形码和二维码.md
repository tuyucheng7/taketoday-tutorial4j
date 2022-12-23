## 1. 概述

条形码用于直观地传达信息，我们很可能会在网页、电子邮件或可打印文档中提供适当的条形码图像。

在本教程中，我们将了解如何在Java中生成最常见的条形码类型。

首先，我们将了解几种条形码的内部结构。接下来，我们将探讨用于生成条形码的最流行的Java库。最后，我们将了解如何通过使用Spring Boot从[Web服务]()提供条形码来将条形码集成到我们的应用程序中。

## 2. 条形码的种类

**条形码对产品编号、序列号和批号等信息进行编码**。此外，它们还使零售商、制造商和运输提供商等各方能够通过整个供应链跟踪资产。

我们可以将许多不同的条形码符号体系分为两大类：

-   线性条形码
-   二维码

### 2.1 UPC(通用产品代码) Codes

UPC码是一些最常用的一维条形码，我们主要在美国找到它们。

**UPC-A是一个纯数字代码**，包含12位数字：制造商标识号(6位数字)、项目编号(5位数字)和校验位。还有一个只有8位数字的UPC-E代码，用于小包裹。

### 2.2 EAN Codes

EAN代码在世界范围内被称为欧洲商品编号和[国际商品编号](https://en.wikipedia.org/wiki/International_Article_Number)，它们专为销售点扫描而设计。EAN代码也有一些不同的变体，包括EAN-13、EAN-8、JAN-13和ISBN。

EAN-13码是最常用的EAN标准，与UPC码类似，**它由13位数字组成-前导“0”后跟UPC-A代码**。

### 2.3 Code 128

**Code 128条形码是一种紧凑、高密度的线性代码**，用于物流和运输行业的订购和配送。**它可以对ASCII的全部128个字符进行编码，并且其长度是可变的**。

### 2.4 PDF417

**PDF417是一种堆叠式线性条形码，由多个一维条形码堆叠而成**。因此，它可以使用传统的线性扫描仪。

我们可能希望在各种应用程序中找到它，例如旅行(登机牌)、身份证和库存管理。

**PDF417使用Reed-Solomon纠错而不是校验位**，这种纠错允许符号承受一些损坏而不会导致数据丢失。然而，它的尺寸可能很大-比Datamatrix和QR码等其他二维条形码大4倍。

### 2.5 二维码

QR码正在成为全球最广泛认可的二维条形码，**二维码的最大好处就是我们可以在有限的空间里存储大量的数据**。

他们使用四种标准化的[编码模式](https://en.wikipedia.org/wiki/QR_code)来高效地存储数据：

-   数字
-   字母数字
-   字节/二进制
-   汉字

此外，它们的尺寸灵活，可以使用智能手机轻松扫描。与PDF417类似，QR码可以承受一些损坏而不会导致数据丢失。

## 3. 条形码库

我们将探索几个库：

-   Barbecue
-   Barcode4j
-   ZXing
-   QRGen

[Barbecue](http://barbecue.sourceforge.net/)是一个开源的Java库，支持广泛的一维条形码格式集。此外，条形码可以输出为PNG、GIF、JPEG和SVG。

[Barcode4j](http://barcode4j.sourceforge.net/)也是一个开源库。此外，它还提供二维条形码格式，如DataMatrix和PDF417以及更多输出格式。PDF417格式在两个库中均可用，但是，与Barcode4j不同，Barbecue将其视为线性条形码。

[ZXing](https://github.com/zxing/zxing)(“斑马线”)是一个用Java实现的开源、多格式一维/二维条形码图像处理库，具有其他语言的接口，这是Java中支持二维码的主要库。

[QRGen](https://github.com/kenglxn/QRGen)库提供了一个构建在ZXing之上的简单QRCode生成API，它为Java和Android提供单独的模块。

## 4. 生成线性条形码

让我们为每个库和条形码对创建一个条形码图像生成器，我们将以PNG格式检索图像，但我们也可以使用其他格式，如GIF或JPEG。

### 4.1 使用Barbecue库

正如我们将看到的，Barbecue提供了用于生成条形码的最简单的API，**我们只需要提供条形码文本作为最小输入**，但是我们可以选择设置字体和分辨率(每英寸点数)。关于字体，我们可以使用它来显示图像下方的条形码文本。

首先，我们需要添加[Barbecue](https://search.maven.org/search?q=g:net.sourceforge.barbecue AND a:barbecue) Maven依赖项：

```xml
<dependency>
    <groupId>net.sourceforge.barbecue</groupId>
    <artifactId>barbecue</artifactId>
    <version>1.5-beta1</version>
</dependency>
```

让我们为EAN13条形码创建一个生成器：

```java
public static BufferedImage generateEAN13BarcodeImage(String barcodeText) throws Exception {
    Barcode barcode = BarcodeFactory.createEAN13(barcodeText);
    barcode.setFont(BARCODE_TEXT_FONT);

    return BarcodeImageHandler.getImage(barcode);
}
```

我们可以以类似的方式为其余的线性条形码类型生成图像。

我们应该注意，我们不需要为EAN/UPC条形码提供校验和数字，因为它是由库自动添加的。

### 4.2 使用Barcode4j库

首先我们添加[Barcode4j](https://search.maven.org/search?q=g:net.sf.barcode4j AND a:barcode4j) Maven依赖项：

```xml
<dependency>
    <groupId>net.sf.barcode4j</groupId>
    <artifactId>barcode4j</artifactId>
    <version>2.1</version>
</dependency>
```

同样，让我们为EAN13条形码构建一个生成器：

```java
public static BufferedImage generateEAN13BarcodeImage(String barcodeText) {
    EAN13Bean barcodeGenerator = new EAN13Bean();
    BitmapCanvasProvider canvas = new BitmapCanvasProvider(160, BufferedImage.TYPE_BYTE_BINARY, false, 0);

    barcodeGenerator.generateBarcode(canvas, barcodeText);
    return canvas.getBufferedImage();
}
```

BitmapCanvasProvider的构造函数有几个参数：分辨率、图像类型、是否启用抗锯齿和图像方向。另外，我们不需要设置字体，因为**默认情况下会显示图片下方的文字**。

### 4.3 使用ZXing库

在这里，我们需要添加两个Maven依赖项：[核心图像库](https://search.maven.org/search?q=g:com.google.zxing AND a:core)和[Java客户端](https://search.maven.org/search?q=g:com.google.zxing AND a:javase)：

```xml
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.3.0</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.3.0</version>
</dependency>
```

让我们创建一个EAN13生成器：

```java
public static BufferedImage generateEAN13BarcodeImage(String barcodeText) throws Exception {
    EAN13Writer barcodeWriter = new EAN13Writer();
    BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.EAN_13, 300, 150);

    return MatrixToImageWriter.toBufferedImage(bitMatrix);
}
```

在这里，我们需要提供几个参数作为输入，例如条形码文本、条形码格式和条形码尺寸。与其他两个库不同，**我们还必须为EAN条形码添加校验和数字**。但是，对于UPC-A条形码，校验和是可选的。

此外，该库不会在图像下方显示条形码文本。

## 5. 生成二维条形码

### 5.1 使用ZXing库

我们将使用这个库来生成二维码，API类似于线性条形码的API：

```java
public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
    QRCodeWriter barcodeWriter = new QRCodeWriter();
    BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);

    return MatrixToImageWriter.toBufferedImage(bitMatrix);
}
```

### 5.2 使用QRGen库

该库不再部署到Maven Central，但我们可以在[jitpack.io](https://jitpack.io/#kenglxn/QRGen/2.6.0)上找到它。

首先，我们需要将jitpack仓库和QRGen依赖项添加到我们的pom.xml中：

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.kenglxn.qrgen</groupId>
        <artifactId>javase</artifactId>
        <version>2.6.0</version>
    </dependency>
</dependencies>
```

让我们创建一个生成二维码的方法：

```java
public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
    ByteArrayOutputStream stream = QRCode
      .from(barcodeText)
      .withSize(250, 250)
      .stream();
    ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

    return ImageIO.read(bis);
}
```

正如我们所看到的，该API基于[构建器模式]()，它提供两种类型的输出：File和OutputStream，我们可以使用ImageIO库将其转换为BufferedImage。

## 6. 构建REST服务

现在我们可以选择要使用的条形码库，让我们看看如何从Spring Boot Web服务提供条形码。

首先是我们的RestController：

```java
@RestController
@RequestMapping("/barcodes")
public class BarcodesController {

    @GetMapping(value = "/barbecue/ean13/{barcode}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> barbecueEAN13Barcode(@PathVariable("barcode") String barcode) throws Exception {
        return okResponse(BarbecueBarcodeGenerator.generateEAN13BarcodeImage(barcode));
    }
    //...
}
```

此外，**我们需要为BufferedImage HTTP Responses手动注册一个[消息转换器]()，因为没有默认值**：

```java
@Bean
public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
    return new BufferedImageHttpMessageConverter();
}
```

最后，我们可以使用Postman或者浏览器来查看生成的条形码。

### 6.1 生成UPC-A条形码

让我们使用Barbecue库调用UPC-A Web服务：

```text
[GET] http://localhost:8080/barcodes/barbecue/upca/12345678901
```

结果如下：

<img src="../assets/img.png">

### 6.2 生成EAN13条形码

类似地，我们调用EAN13 Web服务：

```text
[GET] http://localhost:8080/barcodes/barbecue/ean13/012345678901
```

这是我们的条形码：

<img src="../assets/img_1.png">

### 6.3 生成Code128条形码

在这种情况下，我们将使用POST方法。让我们使用Barbecue库调用Code128 Web服务：

```text
[POST] http://localhost:8080/barcodes/barbecue/code128
```

我们将提供包含以下数据的请求正文：

```text
Lorem ipsum dolor sit amet, consectetur adipiscing elit,
 sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
```

让我们看看结果：

<img src="../assets/img_2.png">

### 6.4 生成PDF417条形码

在这里，我们将调用类似于Code128的PDF417 Web服务：

```text
[POST] http://localhost:8080/barcodes/barbecue/pdf417
```

我们将提供包含以下数据的请求正文：

```text
Lorem ipsum dolor sit amet, consectetur adipiscing elit,
 sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
```

这是生成的条形码：

<img src="../assets/img_3.png">

### 6.5 生成二维码条形码

让我们使用ZXing库调用QR码Web服务：

```text
[POST] http://localhost:8080/barcodes/zxing/qrcode
```

我们将提供包含以下数据的请求正文：

```text
Lorem ipsum dolor sit amet, consectetur adipiscing elit,
 sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
 quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
```

这是我们的二维码：

<img src="../assets/img_4.png">

在这里，我们可以看到二维码在有限空间内存储大量数据的强大功能。

## 7. 总结

在本文中，我们学习了如何在Java中生成最常见的条形码类型。

首先，我们了解了几种类型的线性和二维条形码的格式。接下来，我们探讨了用于生成它们的最流行的Java库，虽然我们尝试了一些简单的示例，但我们可以进一步研究这些库以获得更多自定义实现。

最后，我们了解了如何将条形码生成器集成到REST服务中，以及如何测试它们。