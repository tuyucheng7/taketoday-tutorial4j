## 1. 概述

有时我们需要向[图像](https://www.baeldung.com/java-images)或一组图像添加一些文本。使用图像编辑工具手动执行此操作很容易。但是当我们想以相同的方式向大量图片添加相同的文本时，以编程方式执行此操作将非常有用。

在本快速教程中，我们将学习如何使用 Java向图像添加一些文本。

## 2. 向图像添加文本

要读取图像并添加一些文本，我们可以使用不同的类。在后续部分中，我们将看到几个选项。

### 2.1. ImagePlus和ImageProcessor

首先，让我们看看如何使用[ImageJ 库](https://imagej.nih.gov/ij/developer/api/index.html)中可用的类[ImagePlus](https://imagej.nih.gov/ij/source/ij/ImagePlus.java)和[ImageProcessor](https://imagej.nih.gov/ij/developer/api/ij/ij/process/ImageProcessor.html)。要使用这个库，我们需要在我们的项目中包含这个依赖项：

```xml
<dependency>
    <groupId>net.imagej</groupId>
    <artifactId>ij</artifactId>
    <version>1.51h</version>
</dependency>
```

要读取图像，我们将使用openImage静态方法。此方法的结果将使用ImagePlus对象存储在内存中：

```java
ImagePlus image = IJ.openImage(path);
```

一旦我们将图像加载到内存中，让我们使用类ImageProcessor向它添加一些文本：

```java
Font font = new Font("Arial", Font.BOLD, 18);

ImageProcessor ip = image.getProcessor();
ip.setColor(Color.GREEN);
ip.setFont(font);
ip.drawString(text, 0, 20);
```

使用此代码，我们所做的是在图像的左上角添加指定的绿色文本。请注意，我们使用drawString方法的第二个和第三个参数设置位置，它们分别表示从左侧和顶部开始的像素数。

### 2.2. 缓冲图像和图形

接下来，我们将看看如何使用类[BufferedImage](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/image/BufferedImage.html)和[Graphics](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/Graphics.html#drawString(java.text.AttributedCharacterIterator,int,int))获得相同的结果。Java 的标准构建包括这些类，因此不需要额外的库。

与我们使用ImageJ的openImage的方式相同，我们将使用ImageIO中可用的读取方法：

```java
BufferedImage image = ImageIO.read(new File(path));
```

一旦我们将图像加载到内存中，让我们使用类Graphics向它添加一些文本：

```java
Font font = new Font("Arial", Font.BOLD, 18);

Graphics g = image.getGraphics();
g.setFont(font);
g.setColor(Color.GREEN);
g.drawString(text, 0, 20);
```

正如我们所见，这两种选择在使用方式上非常相似。在这种情况下，方法drawString的第二个和第三个参数的指定方式与我们为ImageProcessor方法所做的相同。

### 2.3. 基于AttributedCharacterIterator绘制

Graphics中可用的方法drawString允许我们使用[AttributedCharacterIterator](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/text/AttributedCharacterIterator.html)打印文本。这意味着我们可以使用具有某些关联属性的文本，而不是使用普通的String 。让我们看一个例子：

```java
Font font = new Font("Arial", Font.BOLD, 18);

AttributedString attributedText = new AttributedString(text);
attributedText.addAttribute(TextAttribute.FONT, font);
attributedText.addAttribute(TextAttribute.FOREGROUND, Color.GREEN);

Graphics g = image.getGraphics();
g.drawString(attributedText.getIterator(), 0, 20);
```

这种打印文本的方式使我们有机会直接将格式与String相关联，这比我们想更改格式时更改Graphics对象属性更清晰。

## 3. 文字对齐

现在我们已经学习了如何在图像的左上角添加简单的文本，现在让我们看看如何在特定位置添加此文本。

### 3.1. 居中文本

我们要解决的第一种对齐方式是使文本居中。要动态设置我们要写入文本的正确位置，我们需要弄清楚一些信息：

-   图片尺寸
-   字体大小

这些信息可以很容易地获得。在图像大小的情况下，可以通过BufferedImage对象的getWidth和getHeight方法访问此数据。另一方面，要获取与字体大小相关的数据，我们需要使用对象FontMetrics。

让我们看一个例子，我们计算文本的正确位置并绘制它：

```java
Graphics g = image.getGraphics();

FontMetrics metrics = g.getFontMetrics(font);
int positionX = (image.getWidth() - metrics.stringWidth(text)) / 2;
int positionY = (image.getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

g.drawString(attributedText.getIterator(), positionX, positionY);
```

### 3.2. 文本右下对齐

我们将要看到的下一种对齐方式是右下角。在这种情况下，我们需要动态获取正确的位置：

```java
int positionX = (image.getWidth() - metrics.stringWidth(text));
int positionY = (image.getHeight() - metrics.getHeight()) + metrics.getAscent();
```

### 3.3. 文字位于左上角

最后，让我们看看如何在左上角打印我们的文本：

```java
int positionX = 0;
int positionY = metrics.getAscent();
```

其余的排列可以从我们看到的三个中推导出来。

## 4. 根据图片调整文字大小

当我们在图片中绘制文字时，可能会发现这段文字超出了图片的大小。为了解决这个问题，我们必须根据图像大小调整我们使用的字体大小。

首先，我们需要使用基本字体获取文本的预期宽度和高度。为了实现这一点，我们将使用 类[FontMetrics](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/FontMetrics.html)、[GlyphVector](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/font/GlyphVector.html)和[Shape](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/java/awt/Shape.html)。

```java
FontMetrics ruler = graphics.getFontMetrics(baseFont);
GlyphVector vector = baseFont.createGlyphVector(ruler.getFontRenderContext(), text);
    
Shape outline = vector.getOutline(0, 0);
    
double expectedWidth = outline.getBounds().getWidth();
double expectedHeight = outline.getBounds().getHeight();

```

下一步是检查是否需要调整字体大小。为此，让我们比较文本的预期大小和图像的大小：

```java
boolean textFits = image.getWidth() >= expectedWidth && image.getHeight() >= expectedHeight;
```

最后，如果我们的文字不适合图像，我们必须减小字体大小。为此，我们将使用deriveFont方法：

```java
double widthBasedFontSize = (baseFont.getSize2D()image.getWidth())/expectedWidth;
double heightBasedFontSize = (baseFont.getSize2D()image.getHeight())/expectedHeight;

double newFontSize = widthBasedFontSize < heightBasedFontSize ? widthBasedFontSize : heightBasedFontSize;
newFont = baseFont.deriveFont(baseFont.getStyle(), (float)newFontSize);
```

请注意，我们需要根据宽度和高度获取新的字体大小，并应用其中最小的一个。

## 5.总结

在本文中，我们了解了如何使用不同的方法在图像中写入文本。

我们还学习了如何根据图像大小和字体属性动态获取我们想要打印文本的位置。

最后，我们看到了如何调整文本的字体大小，以防它超过我们正在绘制的图像的大小。