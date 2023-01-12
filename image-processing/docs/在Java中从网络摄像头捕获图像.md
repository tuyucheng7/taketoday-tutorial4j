## 1. 概述

通常，Java 不提供对计算机硬件的轻松访问。这就是为什么我们可能会发现很难使用Java访问网络摄像头。

在本教程中，我们将探索一些允许我们通过访问网络摄像头来捕获图像的Java库。

## 2.JavaCV

首先，我们将检查[javacv](https://github.com/bytedeco/javacv)库。这是[Bytedeco](http://bytedeco.org/)的 OpenCV计算机视觉库的[Java 实现](https://www.baeldung.com/java-opencv)。

让我们将最新的[javacv-platform](https://search.maven.org/search?q=g:org.bytedeco a:javacv-platform) Maven 依赖项添加到我们的pom.xml中：

```java
<dependency>
    <groupId>org.bytedeco</groupId>
    <artifactId>javacv-platform</artifactId>
    <version>1.5.5</version>
</dependency>
```

同样，在使用 Gradle 时，我们可以在build.gradle文件中添加javacv-platform依赖：

```plaintext
compile group: 'org.bytedeco', name: 'javacv-platform', version: '1.5.5'
```

现在我们已经准备好设置，让我们使用OpenCVFrameGrabber[类](http://bytedeco.org/javacv/apidocs/org/bytedeco/javacv/OpenCVFrameGrabber.html)访问网络摄像头并捕获帧：

```java
FrameGrabber grabber = new OpenCVFrameGrabber(0);
grabber.start();
Frame frame = grabber.grab();
```

在这里，我们将设备号作为0传递，指向系统的默认网络摄像头。但是，如果我们有多个摄像头可用，则第二个摄像头可访问的值为 1，第三个摄像头的访问值为 2，依此类推。

然后，我们可以使用OpenCVFrameConverter将捕获的帧转换为图像。此外，我们将使用opencv_imgcodecs类的cvSaveImage方法保存图像：

```java
OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
IplImage img = converter.convert(frame);
opencv_imgcodecs.cvSaveImage("selfie.jpg", img);

```

最后，我们可以使用[CanvasFrame](http://bytedeco.org/javacv/apidocs/org/bytedeco/javacv/CanvasFrame.html)类来显示捕获的帧：

```java
CanvasFrame canvas = new CanvasFrame("Web Cam");
canvas.showImage(frame);

```

让我们检查一个完整的解决方案，它访问网络摄像头、捕获图像、在框架中显示图像并在两秒后自动关闭框架：

```java
CanvasFrame canvas = new CanvasFrame("Web Cam");
canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

FrameGrabber grabber = new OpenCVFrameGrabber(0);
OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

grabber.start();
Frame frame = grabber.grab();

IplImage img = converter.convert(frame);
cvSaveImage("selfie.jpg", img);

canvas.showImage(frame);

Thread.sleep(2000);

canvas.dispatchEvent(new WindowEvent(canvas, WindowEvent.WINDOW_CLOSING));
```

## 3.网络摄像头捕获

接下来，我们将检查允许通过支持多个捕获框架来使用网络摄像头的网络摄像头捕获库。

首先，让我们将最新的[webcam-capture](https://search.maven.org/search?q=g:com.github.sarxos a:webcam-capture) Maven 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.github.sarxos</groupId>
    <artifactId>webcam-capture</artifactId>
    <version>0.3.12</version>
</dependency>
```

或者，我们可以在Gradle 项目的build.gradle中添加网络摄像头捕获：

```plaintext
compile group: 'com.github.sarxos', name: 'webcam-capture', version: '0.3.12'
```

然后，让我们编写一个简单的示例来使用[Webcam](https://javadoc.io/static/com.github.sarxos/webcam-capture/0.3.12/com/github/sarxos/webcam/Webcam.html)类捕获图像：

```java
Webcam webcam = Webcam.getDefault();
webcam.open();

BufferedImage image = webcam.getImage();

ImageIO.write(image, ImageUtils.FORMAT_JPG, new File("selfie.jpg"));
```

在这里，我们访问了默认的网络摄像头来捕捉图像，然后我们将图像保存到一个文件中。

或者，我们可以使用[WebcamUtils](https://javadoc.io/static/com.github.sarxos/webcam-capture/0.3.12/com/github/sarxos/webcam/WebcamUtils.html)类来捕获图像：

```java
WebcamUtils.capture(webcam, "selfie.jpg");
```

此外，我们可以使用[WebcamPanel](https://javadoc.io/static/com.github.sarxos/webcam-capture/0.3.12/com/github/sarxos/webcam/WebcamPanel.html)类[在框架中显示捕获的图像](https://www.baeldung.com/java-images#3-displaying-an-image)：

```java
Webcam webcam = Webcam.getDefault();
webcam.setViewSize(WebcamResolution.VGA.getSize());

WebcamPanel panel = new WebcamPanel(webcam);
panel.setImageSizeDisplayed(true);

JFrame window = new JFrame("Webcam");
window.add(panel);
window.setResizable(true);
window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
window.pack();
window.setVisible(true);
```

在这里，我们将VGA设置为网络摄像头的视图大小，创建[JFrame](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/JFrame.html)对象，并将WebcamPanel组件添加到框架中。

## 4.马文框架

最后，我们将探索 Marvin 框架来访问网络摄像头和捕获图像。

像往常一样，我们将最新的[marvin](https://search.maven.org/search?q=g:com.github.downgoon a:marvin) 依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>com.github.downgoon</groupId>
    <artifactId>marvin</artifactId>
    <version>1.5.5</version>
</dependency>
```

或者，对于 Gradle 项目，我们将在build.gradle文件中添加marvin依赖项：

```plaintext
compile group: 'com.github.downgoon', name: 'marvin', version: '1.5.5'
```

现在设置已准备就绪，让我们使用[MarvinJavaCVAdapter](http://marvinproject.sourceforge.net/javadoc/marvin/video/MarvinJavaCVAdapter.html)类通过为设备编号提供 0 来连接到默认网络摄像头：

```java
MarvinVideoInterface videoAdapter = new MarvinJavaCVAdapter();
videoAdapter.connect(0);
```

接下来，我们可以使用getFrame方法捕获帧，然后我们将使用[MarvinImageIO](http://marvinproject.sourceforge.net/javadoc/marvin/io/MarvinImageIO.html)类的saveImage方法保存图像：

```java
MarvinImage image = videoAdapter.getFrame();
MarvinImageIO.saveImage(image, "selfie.jpg");
```

此外，我们可以使用[MarvinImagePanel](http://marvinproject.sourceforge.net/javadoc/marvin/gui/MarvinImagePanel.html)类在框架中显示图像：

```java
MarvinImagePanel imagePanel = new MarvinImagePanel();
imagePanel.setImage(image);

imagePanel.setSize(800, 600);
imagePanel.setVisible(true);
```

## 5.总结

在这篇简短的文章中，我们研究了一些可以轻松访问网络摄像头的Java库。

首先，我们探索了javacv-platform库，它提供了 OpenCV 项目的Java实现。然后，我们看到了使用网络摄像头捕获图像的网络摄像头捕获库的示例实现。最后，我们查看了使用 Marvin 框架捕获图像的简单示例。