## 1. 简介

在本教程中，我们将学习如何使用Java调整(缩放)图像的大小。我们将探索提供图像大小调整功能的核心Java和开源第三方库。

值得一提的是，我们可以上下缩放图像。在本教程的代码示例中，我们会将图像调整为更小的尺寸，因为在实践中，这是最常见的情况。

## 2. 使用 CoreJava调整图像大小

CoreJava提供了以下调整图像大小的选项：

-   使用[java.awt.Graphics2D调整大小](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/Graphics2D.html)
-   使用[Image#getScaledInstance调整大小](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/Image.html)

### 2.1. java.awt.Graphics2D

Graphics2D是在Java平台上渲染二维形状、文本和图像的基础类。

让我们从使用Graphics2D调整图像大小开始：

```java
BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
    BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics2D = resizedImage.createGraphics();
    graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
    graphics2D.dispose();
    return resizedImage;
}
```

让我们看看图像在调整大小之前和之后的样子：

[![图像比例样本](https://www.baeldung.com/wp-content/uploads/2020/07/image-scale-sample-300x300.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/image-scale-sample.jpg)  [![sampleImage 调整大小的 graphics2d](https://www.baeldung.com/wp-content/uploads/2020/07/sampleImage-resized-graphics2d.jpg)](https://www.baeldung.com/wp-content/wp-content/uploads/2020/07/sampleImage-resized-graphics2d.jpg)

BufferedImage.TYPE_INT_RGB参数表示图像的颜色模型。[官方JavaBufferedImage文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/image/BufferedImage.html)中提供了可用值的完整列表。

Graphics2D接受称为RenderingHints的附加参数。我们使用RenderingHints来影响不同的图像处理方面，最重要的是图像质量和处理时间。

我们可以使用setRenderingHint方法添加RenderingHint：

```java
graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
```

可以在[本 Oracle 教程中找到](https://docs.oracle.com/javase/tutorial/2d/advanced/quality.html)RenderingHints的完整列表。

### 2.2. Image.getScaledInstance() 方法

这种使用Image的方法非常简单，并且可以生成质量令人满意的图像：

```java
BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
    Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
    BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
    outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
    return outputImage;
}
```

让我们看看一张美味的图片会发生什么：

[![图像比例样本2](https://www.baeldung.com/wp-content/uploads/2020/07/image-scale-sample2-300x300.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/image-scale-sample2.jpg)  [![sampleImage 调整大小 scaledinstance](https://www.baeldung.com/wp-content/uploads/2020/07/sampleImage-resized-scaledinstance.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/sampleImage-resized-scaledinstance.jpg)

我们还可以通过为getScaledInstance()方法提供一个标志来指示缩放机制使用一种可用的方法，该标志指示用于我们的图像重采样需求的算法类型：

```java
Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
```

[官方JavaImage 文档](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/Image.html)中描述了所有可用的标志。

## 3.图像缩放器

[Imgscalr](https://github.com/rkalla/imgscalr)在后台使用Graphic2D 。它有一个简单的 API，带有几种不同的图像大小调整方法。 

Imgscalr 为我们提供最好看的结果、最快的结果或平衡的结果，具体取决于我们选择的缩放选项。还提供其他图像处理功能，例如用于裁剪和旋转的功能。让我们在一个简单的例子中展示它是如何工作的。

我们将添加以下 Maven 依赖项：

```xml
<dependency>
    <groupId>org.imgscalr</groupId>
    <artifactId>imgscalr-lib</artifactId>
    <version>4.2</version>
</dependency>
```

检查[Maven Central](https://search.maven.org/search?q=g:org.imgscalr)以获取最新版本。

使用 Imgscalr 最简单的方法是：

```java
BufferedImage simpleResizeImage(BufferedImage originalImage, int targetWidth) throws Exception {
    return Scalr.resize(originalImage, targetWidth);
}
```

其中originalImage是要调整大小的BufferedImage ， targetWidth是结果图像的宽度。这种方法将保持原始图像比例并使用默认参数—— Method.AUTOMATIC和Mode.AUTOMATIC。

它如何处理美味水果的图片？让我们来看看：

[![图像比例样本 imgscalr](https://www.baeldung.com/wp-content/uploads/2020/07/image-scale-sample-imgscalr-300x300.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/image-scale-sample-imgscalr.jpg)  [![sampleImage 调整大小 imgscalr](https://www.baeldung.com/wp-content/uploads/2020/07/sampleImage-resized-imgscalr.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/sampleImage-resized-imgscalr.jpg)

该库还允许多个配置选项，并且它在后台处理图像透明度。

最重要的参数是：

-   mode – 用于定义算法将使用的调整大小模式。例如，我们可以定义是否要保持图像的比例(选项是AUTOMATIC、FIT_EXACT、FIT_TO_HEIGHT 和 FIT_TO_WIDTH)
-   方法——指示调整大小的过程，以便其重点放在速度、质量或两者上。可能的值为AUTOMATIC、BALANCED、QUALITY、SPEED、ULTRA_QUALITY

还可以定义额外的调整大小属性，这些属性将为我们提供日志记录或指示库对图像进行一些颜色修改(使其更亮、更暗、灰度等)。

让我们使用完整的resize()方法参数化：

```java
BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws Exception {
    return Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
}
```

现在我们得到：

[![图像比例样本 imgscalr](https://www.baeldung.com/wp-content/uploads/2020/07/image-scale-sample-imgscalr-300x300.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/image-scale-sample-imgscalr.jpg)  [![sampleImage 使用参数调整了 imgscalr 的大小](https://www.baeldung.com/wp-content/uploads/2020/07/sampleImage-resized-imgscalr-with-params.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/sampleImage-resized-imgscalr-with-params.jpg)

Imgscalr 适用于JavaImage IO 支持的所有文件——JPG、BMP、JPEG、WBMP、PNG 和 GIF。

## 4.缩略图

[Thumbnailator](https://github.com/coobird/thumbnailator)是一个用于Java的开源图像大小调整库，它使用渐进式双线性缩放。它支持 JPG、BMP、JPEG、WBMP、PNG 和 GIF。

我们将通过将以下 Maven 依赖项添加到我们的pom.xml来将其包含在我们的项目中：

```xml
<dependency>
    <groupId>net.coobird</groupId>
    <artifactId>thumbnailator</artifactId>
    <version>0.4.11</version>
</dependency>
```

可以在[Maven Central](https://search.maven.org/search?q=a:thumbnailator)上找到可用的依赖项版本。

它有一个非常简单的 API，允许我们以百分比设置输出质量：

```java
BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails.of(originalImage)
        .size(targetWidth, targetHeight)
        .outputFormat("JPEG")
        .outputQuality(1)
        .toOutputStream(outputStream);
    byte[] data = outputStream.toByteArray();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
    return ImageIO.read(inputStream);
}
```

让我们看看这张微笑的照片在调整大小之前和之后的样子：

[![调整图像 samplmage thumbnailator](https://www.baeldung.com/wp-content/uploads/2020/07/resize-image-samplmage-thumbnailator-284x300.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/resize-image-samplmage-thumbnailator.jpg)  [![sampleImage 调整大小缩略图](https://www.baeldung.com/wp-content/uploads/2020/07/sampleImage-resized-thumbnailator.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/sampleImage-resized-thumbnailator.jpg)

它还具有批处理选项：

```java
Thumbnails.of(new File("path/to/directory").listFiles())
    .size(300, 300)
    .outputFormat("JPEG")
    .outputQuality(0.80)
    .toFiles(Rename.PREFIX_DOT_THUMBNAIL);
```

作为 Imgscalr，Thumblinator 可以处理JavaImage IO 支持的所有文件——JPG、BMP、JPEG、WBMP、PNG 和 GIF。

## 5.马文

[Marvin](http://marvinproject.sourceforge.net/)是一个方便的图像处理工具，它提供了许多有用的基本(裁剪、旋转、倾斜、翻转、缩放)和高级(模糊、浮雕、纹理)功能。

和以前一样，我们将添加 Marvin 调整大小所需的 Maven 依赖项：

```xml
<dependency>
    <groupId>com.github.downgoon</groupId>
    <artifactId>marvin</artifactId>
    <version>1.5.5</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>com.github.downgoon</groupId>
    <artifactId>MarvinPlugins</artifactId>
    <version>1.5.5</version>
</dependency>
```

可以在[Maven Central](https://search.maven.org/search?q=a:marvin)上找到可用的 Marvin 依赖版本以及[Marvin 插件](https://search.maven.org/search?q=a:MarvinPlugins)版本。

Marvin 的缺点是它不提供额外的缩放配置。此外，scale 方法需要图像和图像克隆，这有点麻烦：

```java
BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
    MarvinImage image = new MarvinImage(originalImage);
    Scale scale = new Scale();
    scale.load();
    scale.setAttribute("newWidth", targetWidth);
    scale.setAttribute("newHeight", targetHeight);
    scale.process(image.clone(), image, null, null, false);
    return image.getBufferedImageNoAlpha();
}
```

现在让我们调整花朵图像的大小，看看效果如何：

[![调整图像样本图像 marvin](https://www.baeldung.com/wp-content/uploads/2020/07/resize-image-sampleImage-marvin-300x300.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/resize-image-sampleImage-marvin.jpg)  [![sampleImage resized 马文](https://www.baeldung.com/wp-content/uploads/2020/07/sampleImage-resized-marvin.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/sampleImage-resized-marvin.jpg)

## 6. 最佳实践

就资源而言，图像处理是一项昂贵的操作，因此当我们真的不需要时，选择最高质量不一定是最佳选择。

让我们看看所有方法的性能。我们拍摄了一张1920×1920 像素的图像，并将其缩放为200×200 像素。结果观察到的时间如下：

-   java.awt.Graphics2D – 34 毫秒
-   Image.getScaledInstance() – 235 毫秒
-   Imgscalr – 143 毫秒
-   缩略图 – 547 毫秒
-   马文 - 361 毫秒

另外，在定义目标图片的宽高时，要注意图片的纵横比。这样图像将保持其原始比例并且不会被拉伸。

## 七. 总结

本文介绍了几种在Java中调整图像大小的方法。我们还了解到有多少不同的因素会影响调整大小的过程。