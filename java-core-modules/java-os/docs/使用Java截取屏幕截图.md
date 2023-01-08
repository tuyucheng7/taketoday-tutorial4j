## 1. 概述

在本教程中，我们将了解在Java中截取屏幕截图的几种不同方法。

## 2. 使用机器人截图

在我们的第一个示例中，我们将截取主屏幕的屏幕截图。

为此，我们将使用Robot类中的createScreenCapture()方法。它采用Rectangle作为设置屏幕截图边界的参数，并返回BufferedImage对象。BufferedImage可以进一步用于创建图像文件：

```java
@Test
public void givenMainScreen_whenTakeScreenshot_thenSaveToFile() throws Exception {
    Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    BufferedImage capture = new Robot().createScreenCapture(screenRect);

    File imageFile = new File("single-screen.bmp");
    ImageIO.write(capture, "bmp", imageFile );
    assertTrue(imageFile .exists());
}
```

屏幕的尺寸可以通过Toolkit类使用其getScreenSize()方法访问。在具有多个屏幕的系统上，默认使用主显示器。

将屏幕捕获到BufferedImage 后，我们可以使用ImageIO.write()将其写入文件。为此，我们需要两个额外的参数。图像格式和图像文件本身。在我们的示例中，我们使用 . bmp格式，但其他人喜欢 . 也可以使用png、.jpg或.gif 。

## 3. 多屏截图

也可以一次截取多个显示器的屏幕截图。就像前面的例子一样，我们可以使用Robot类的createScreenCapture()方法。但是这次截图的边界需要覆盖所有需要的屏幕。

为了获得所有显示，我们将使用GraphicsEnvironment类及其 getScreenDevices()方法。

接下来，我们将获取每个单独屏幕的边界并创建一个适合所有屏幕的矩形：

```java
@Test
public void givenMultipleScreens_whenTakeScreenshot_thenSaveToFile() throws Exception {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] screens = ge.getScreenDevices();

    Rectangle allScreenBounds = new Rectangle();
    for (GraphicsDevice screen : screens) {
        Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
        allScreenBounds.width += screenBounds.width;
        allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
    }

    BufferedImage capture = new Robot().createScreenCapture(allScreenBounds);
    File imageFile = new File("all-screens.bmp");
    ImageIO.write(capture, "bmp", imageFile);
    assertTrue(imageFile.exists());
}
```

在遍历显示时，我们总是将宽度相加并只选择一个最大高度，因为屏幕将水平连接。

更进一步，我们需要保存屏幕截图图像。与前面的示例一样，我们可以使用ImageIO.write()方法。

## 4. 截取给定 GUI 组件的屏幕截图

我们还可以截取给定 UI 组件的屏幕截图。

可以通过getBounds()方法轻松访问维度，因为每个组件都知道它的大小和位置。

在这种情况下，我们不会使用Robot API。相反，我们将使用Component类中的paint()方法将内容直接绘制到BufferedImage中：

```java
@Test
public void givenComponent_whenTakeScreenshot_thenSaveToFile(Component component) throws Exception {
    Rectangle componentRect = component.getBounds();
    BufferedImage bufferedImage = new BufferedImage(componentRect.width, componentRect.height, BufferedImage.TYPE_INT_ARGB);
    component.paint(bufferedImage.getGraphics());

    File imageFile = new File("component-screenshot.bmp");
    ImageIO.write(bufferedImage, "bmp", imageFile );
    assertTrue(imageFile.exists());
}
```

获取组件的绑定后，我们需要创建 BufferedImage 。为此，我们需要宽度、高度和图像类型。在这种情况下，我们使用BufferedImage.TYPE_INT_ARGB，它指的是 8 位彩色图像。

然后我们继续调用paint()方法来填充BufferedImage并且与前面的示例相同，我们使用ImageIO.write()方法将其保存到文件中。

## 5.总结

在本教程中，我们学习了几种使用Java截取屏幕截图的方法。