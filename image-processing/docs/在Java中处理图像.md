## 一、概述

在本教程中，我们将了解一些可用的图像处理库，并执行简单的图像处理操作——加载图像并在其上绘制形状。

我们将试用 AWT(和一些 Swing)库、ImageJ、OpenIMAJ 和 TwelveMonkeys。

## 2.AWT

AWT 是一个内置的 Java 库，允许用户执行与显示相关的简单操作，如创建窗口、定义按钮和监听器等。它还包括允许用户编辑图像的方法。它不需要安装，因为它随 Java 一起提供。

### 2.1. 加载图像

第一件事是从保存在我们磁盘驱动器上的图片创建一个BufferedImage对象：

```java
String imagePath = "path/to/your/image.jpg";
BufferedImage myPicture = ImageIO.read(new File(imagePath));

```

### 2.2. 编辑图像

要在图像上绘制形状，我们必须使用与加载图像相关的Graphics对象。图形对象封装了执行基本渲染操作所需的属性。Graphics2D是一个扩展Graphics的类。它提供了对二维形状的更多控制。

在这种特殊情况下，我们需要Graphic2D来扩展形状宽度以使其清晰可见。我们通过增加它的stroke属性来实现它。然后我们设置颜色，并绘制一个矩形，形状距离图像边界 10 像素：

```java
Graphics2D g = (Graphics2D) myPicture.getGraphics();
g.setStroke(new BasicStroke(3));
g.setColor(Color.BLUE);
g.drawRect(10, 10, myPicture.getWidth() - 20, myPicture.getHeight() - 20);

```

### 2.3. 显示图像

现在我们已经在图像上绘制了一些东西，我们想要显示它。我们可以使用 Swing 库对象来完成。首先，我们创建JLabel对象，它表示文本或/和图像的显示区域：

```java
JLabel picLabel = new JLabel(new ImageIcon(myPicture));
```

然后将我们的JLabel添加到JPanel，我们可以将其视为基于 Java 的 GUI 的<div></div> ：

```java
JPanel jPanel = new JPanel();
jPanel.add(picLabel);
```

最后，我们将所有内容添加到JFrame，即屏幕上显示的窗口。我们必须设置大小，这样我们就不必在每次运行程序时都扩展此窗口：

```java
JFrame f = new JFrame();
f.setSize(new Dimension(myPicture.getWidth(), myPicture.getHeight()));
f.add(jPanel);
f.setVisible(true);
```

## 3.图像J

ImageJ 是一种基于 Java 的软件，专为处理图像而创建。它有很多插件，可[在此处](https://imagej.nih.gov/ij/plugins/)获得。我们将只使用 API，因为我们想自己执行处理。

它是一个非常强大的库，比 Swing 和 AWT 更好，因为它的创建目的是图像处理而不是 GUI 操作。插件包含许多免费使用的算法，当我们想学习图像处理并快速查看结果而不是解决 IP 算法下的数学和优化问题时，这是一件好事。

### 3.1. Maven 依赖

要开始使用 ImageJ，只需将依赖项添加到项目的pom.xml文件中：

```xml
<dependency>
    <groupId>net.imagej</groupId>
    <artifactId>ij</artifactId>
    <version>1.51h</version>
</dependency>
```

将在[Maven 存储库](https://search.maven.org/classic/#search|gav|1|g%3A"net.imagej" AND a%3A"ij")中找到最新版本。

### 3.2. 加载图像

要加载图像，需要使用IJ类中的openImage()静态方法：

```java
ImagePlus imp = IJ.openImage("path/to/your/image.jpg");
```

### 3.3. 编辑图像

要编辑图像，我们必须使用附加到ImagePlus对象的ImageProcessor对象中的方法。将其视为AWT 中的Graphics对象：

```java
ImageProcessor ip = imp.getProcessor();
ip.setColor(Color.BLUE);
ip.setLineWidth(4);
ip.drawRect(10, 10, imp.getWidth() - 20, imp.getHeight() - 20);
```

### 3.4. 显示图像

你只需要调用ImagePlus对象的show()方法：

```java
imp.show();
```

## 4. 思想开放

OpenIMAJ 是一组 Java 库，不仅专注于计算机视觉和视频处理，还专注于机器学习、音频处理、使用 Hadoop 等等。OpenIMAJ 项目的所有部分都可以在[此处](http://openimaj.org/)的“模块”下找到。我们只需要图像处理部分。

### 4.1. Maven 依赖

要开始使用 OpenIMAJ，只需将依赖项添加到项目的 <em>pom.xml</em> 文件中：

```xml
<dependency>
    <groupId>org.openimaj</groupId>
    <artifactId>core-image</artifactId>
    <version>1.3.5</version>
</dependency>
```

[将在此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.openimaj" AND a%3A"core-image")找到最新版本。

### 4.1. 加载图像

要加载图像，请使用ImageUtilities.readMBF()方法：

```java
MBFImage image = ImageUtilities.readMBF(new File("path/to/your/image.jpg"));

```

MBF 代表多波段浮点图像(本例中为 RGB，但它不是表示颜色的唯一方式)。

### 4.2. 编辑图像

要绘制矩形，我们需要定义其形状，即由 4 个点(左上、左下、右下、右上)组成的多边形：

```java
Point2d tl = new Point2dImpl(10, 10);
Point2d bl = new Point2dImpl(10, image.getHeight() - 10);
Point2d br = new Point2dImpl(image.getWidth() - 10, image.getHeight() - 10);
Point2d tr = new Point2dImpl(image.getWidth() - 10, 10);
Polygon polygon = new Polygon(Arrays.asList(tl, bl, br, tr));
```

可能已经注意到，在图像处理中，Y 轴是相反的。定义形状后，我们需要绘制它：

```java
image.drawPolygon(polygon, 4, new Float[] { 0f, 0f, 255.0f });
```

绘图方法有 3 个参数：形状、线条粗细和由Float数组表示的 RGB 通道值。

### 4.3. 显示图像

我们需要使用DisplayUtilities：

```java
DisplayUtilities.display(image);
```

## 5.十二只猴子 ImageIO

TwelveMonkeys ImageIO库旨在作为 Java ImageIO API 的扩展 ，支持更多格式。

大多数时候，代码看起来与内置 Java 代码相同，但在添加必要的依赖项后，它可以处理其他图像格式。

默认情况下，Java 仅支持这五种图像格式：JPEG、PNG、BMP、WEBMP、GIF。

如果我们尝试使用不同格式的图像文件，我们的应用程序将无法读取它并在访问BufferedImage变量时抛出NullPointerException 。

TwelveMonkeys添加了对以下格式的支持：PNM、PSD、TIFF、HDR、IFF、PCX、PICT、SGI、TGA、ICNS、ICO、CUR、Thumbs.db、SVG、WMF。

要处理特定格式的图像，我们需要添加相应的依赖项，例如[imageio-jpeg](https://search.maven.org/classic/#search|ga|1|a%3A"imageio-jpeg" AND g%3A"com.twelvemonkeys.imageio")或[imageio-tiff](https://search.maven.org/classic/#search|ga|1|a%3A"imageio-tiff" AND g%3A"com.twelvemonkeys.imageio")。

可以在[TwelveMonkeys](https://github.com/haraldk/TwelveMonkeys)文档中找到完整的依赖项列表。

让我们创建一个读取.ico图像的示例。代码看起来与AWT部分相同，除了我们将打开不同的图像：

```java
String imagePath = "path/to/your/image.ico";
BufferedImage myPicture = ImageIO.read(new File(imagePath));
```

为了让这个例子正常工作，我们需要添加包含对.ico图像支持的TwelveMonkeys依赖项，即[imageio-bmp](https://search.maven.org/classic/#search|ga|1|a%3A"imageio-bmp" AND g%3A"com.twelvemonkeys.imageio")依赖项，以及[imageio-core](https://search.maven.org/classic/#search|ga|1|a%3A"imageio-core" AND g%3A"com.twelvemonkeys.imageio")依赖项：

```xml
<dependency>
    <groupId>com.twelvemonkeys.imageio</groupId>
    <artifactId>imageio-bmp</artifactId>
    <version>3.3.2</version>
</dependency>
<dependency>
    <groupId>com.twelvemonkeys.imageio</groupId>
    <artifactId>imageio-core</artifactId>
    <version>3.3.2</version>
</dependency>
```

这就是全部！内置的ImageIO Java API 在运行时自动加载插件。现在我们的项目也将使用.ico图像。

## 6.总结

已经了解了 4 个可以帮助处理图像的库。更进一步，可能需要寻找一些图像处理算法，例如提取边缘、增强对比度、使用滤镜或面部检测。

出于这些目的，最好开始学习 ImageJ 或 OpenIMAJ。两者都很容易包含在项目中，并且在图像处理方面比 AWT 强大得多。