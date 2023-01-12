## 1. 简介

在本教程中，我们将学习如何安装和使用 OpenCV 计算机视觉库并将其应用于实时人脸检测。

## 2. 安装

要在我们的项目中使用 OpenCV 库，我们需要将[opencv Maven依赖](https://search.maven.org/search?q=g:org.openpnp a:opencv)项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.openpnp</groupId>
    <artifactId>opencv</artifactId>
    <version>3.4.2-0</version>
</dependency>
```

对于 Gradle 用户，我们需要将依赖项添加到我们的build.gradle文件中：

```java
compile group: 'org.openpnp', name: 'opencv', version: '3.4.2-0'
```

将库添加到我们的依赖项后，我们可以使用 OpenCV 提供的功能。

## 3. 使用图书馆

要开始使用 OpenCV，我们需要初始化库，我们可以在main方法中完成：

```java
OpenCV.loadShared();
```

OpenCV是一个类，它包含与为各种平台和体系结构加载 OpenCV 库所需的本机包相关的方法。

值得注意的是[文档](https://opencv-java-tutorials.readthedocs.io/)做的事情略有不同：

```java
System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
```

这两个方法调用都将实际加载所需的本机库。

这里的区别在于后者需要安装本地库。但是，如果库在给定机器上不可用，前者可以将库安装到临时文件夹中。由于这种差异，loadShared方法 通常是最好的方法。

现在我们已经初始化了库，让我们看看我们可以用它做什么。

## 4.加载图片

首先，让我们使用 OpenCV 从磁盘加载示例图像：

```java
public static Mat loadImage(String imagePath) {
    Imgcodecs imageCodecs = new Imgcodecs();
    return imageCodecs.imread(imagePath);
}
```

此方法会将给定图像加载为Mat对象，这是一种矩阵表示。

要保存之前加载的图像，我们可以使用Imgcodecs类的imwrite()方法：

```java
public static void saveImage(Mat imageMatrix, String targetPath) {
    Imgcodecs imgcodecs = new Imgcodecs();
    imgcodecs.imwrite(targetPath, imageMatrix);
}
```

## 5. 毛发级联分类器

在深入研究面部识别之前，让我们先了解使之成为可能的核心概念。

简而言之，分类器是一种程序，它试图根据过去的经验将新的观察结果放入一个组中。级联分类器试图使用几个分类器的串联来做到这一点。每个后续分类器都使用前一个分类器的输出作为附加信息，从而大大改进了分类。

### 5.1. 头发特征

OpenCV 中的人脸检测是由基于 Haar 特征的级联分类器完成的。

[Haar 特征](https://docs.opencv.org/3.4/db/d28/tutorial_cascade_classifier.html)是用于检测图像边缘和线条的过滤器。过滤器被视为具有黑色和白色的正方形：

[![头发特征](https://www.baeldung.com/wp-content/uploads/2020/02/haar_features.jpg)](https://www.baeldung.com/wp-content/uploads/2020/02/haar_features.jpg)

这些过滤器逐个像素地多次应用于图像，并将结果收集为单个值。该值是黑色方块下的像素总和与白色方块下的像素总和之差。

## 6.人脸检测

通常，级联分类器需要经过预训练才能检测到任何东西。

由于训练过程可能很长并且需要大数据集，我们将使用[OpenCV 提供的预训练模型](https://github.com/opencv/opencv/tree/master/data/haarcascades)之一。我们将把这个 XML 文件放在我们的资源 文件夹中以便于访问。

让我们逐步完成检测人脸的过程：

[![人脸检测](https://www.baeldung.com/wp-content/uploads/2020/02/TestImg-300x300.jpg)](https://www.baeldung.com/wp-content/uploads/2020/02/TestImg.jpg)

我们将尝试通过用红色矩形勾勒出面部轮廓来检测面部。

首先，我们需要从源路径加载Mat 格式的图像：

```java
Mat loadedImage = loadImage(sourceImagePath);
```

然后，我们将声明一个 MatOfRect 对象来存储我们找到的面：

```java
MatOfRect facesDetected = new MatOfRect();
```

接下来，我们需要初始化CascadeClassifier 来进行识别：

```java
CascadeClassifier cascadeClassifier = new CascadeClassifier(); 
int minFaceSize = Math.round(loadedImage.rows()  0.1f); 
cascadeClassifier.load("./src/main/resources/haarcascades/haarcascade_frontalface_alt.xml"); 
cascadeClassifier.detectMultiScale(loadedImage, 
  facesDetected, 
  1.1, 
  3, 
  Objdetect.CASCADE_SCALE_IMAGE, 
  new Size(minFaceSize, minFaceSize), 
  new Size() 
);
```

上面，参数 1.1 表示我们要使用的比例因子，指定在每个图像比例下图像尺寸缩小了多少。下一个参数3是minNeighbors。这是候选矩形为了保留它应该具有的邻居数。

最后，我们将遍历面孔并保存结果：

```java
Rect[] facesArray = facesDetected.toArray(); 
for(Rect face : facesArray) { 
    Imgproc.rectangle(loadedImage, face.tl(), face.br(), new Scalar(0, 0, 255), 3); 
} 
saveImage(loadedImage, targetImagePath);
```

当我们输入源图像时，我们现在应该收到输出图像，其中所有面孔都标有红色矩形：

[![检测到人脸](https://www.baeldung.com/wp-content/uploads/2020/02/detected-300x300-1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/02/detected-300x300-1.jpg)

## 7. 使用 OpenCV 访问相机

到目前为止，我们已经了解了如何对加载的图像执行人脸检测。但大多数时候，我们希望实时进行。为了能够做到这一点，我们需要访问相机。

然而，为了能够显示来自相机的图像，除了显而易见的东西之外，我们还需要一些额外的东西——相机。为了显示图像，我们将使用 JavaFX。

由于我们将使用ImageView来显示我们的相机拍摄的照片，因此我们需要一种将 OpenCV Mat 转换为 JavaFX Image的方法：

```java
public Image mat2Img(Mat mat) {
    MatOfByte bytes = new MatOfByte();
    Imgcodecs.imencode("img", mat, bytes);
    InputStream inputStream = new ByteArrayInputStream(bytes.toArray());
    return new Image(inputStream);
}
```

在这里，我们将 Mat 转换为字节，然后将字节转换为 Image 对象。

我们将从将相机视图流式传输到 JavaFX舞台开始。

现在，让我们使用loadShared 方法初始化库：

```java
OpenCV.loadShared();
```

接下来，我们将使用VideoCapture 和 ImageView 创建舞台以显示 图像：

```java
VideoCapture capture = new VideoCapture(0); 
ImageView imageView = new ImageView(); 
HBox hbox = new HBox(imageView); 
Scene scene = new Scene(hbox);
stage.setScene(scene); 
stage.show();
```

这里，0是我们要使用的相机的 ID。我们还需要创建一个AnimationTimer 来处理图像设置：

```java
new AnimationTimer() { 
    @Override public void handle(long l) { 
        imageView.setImage(getCapture()); 
    } 
}.start();
```

最后，我们的 getCapture 方法负责将 Mat 转换为 Image：

```java
public Image getCapture() { 
    Mat mat = new Mat(); 
    capture.read(mat); 
    return mat2Img(mat); 
}
```

应用程序现在应该创建一个窗口，然后将视图从相机实时流式传输到imageView 窗口。

## 8.实时人脸检测

最后，我们可以将所有的点连接起来创建一个实时检测人脸的应用程序。

上一节中的代码负责从相机中抓取图像并将其显示给用户。现在，我们所要做的就是在使用我们的CascadeClassifier类将抓取的图像显示在屏幕上之前对其进行处理。

让我们简单地修改我们的getCapture 方法来执行人脸检测：

```java
public Image getCaptureWithFaceDetection() {
    Mat mat = new Mat();
    capture.read(mat);
    Mat haarClassifiedImg = detectFace(mat);
    return mat2Img(haarClassifiedImg);
}
```

现在，如果我们运行我们的应用程序，脸部应该用红色矩形标记。

我们还可以看到级联分类器的缺点。如果我们将脸朝任何方向转动太多，那么红色矩形就会消失。这是因为我们使用了一个特定的分类器，该分类器仅经过训练以检测面部的正面。

## 9.总结

在本教程中，我们学习了如何在Java中使用 OpenCV。

我们使用预训练的级联分类器来检测图像上的人脸。在 JavaFX 的帮助下，我们设法让分类器使用来自相机的图像实时检测人脸。