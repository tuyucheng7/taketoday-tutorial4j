## 1. 概述

有时，我们需要在没有实际显示器、键盘或鼠标的情况下使用Java中基于图形的应用程序，比方说，在服务器或容器上。


在这个简短的教程中，我们将学习Java的无头模式来解决这种情况。我们还将看看在无头模式下我们可以做什么，不能做什么。

## 2.设置无头模式

我们可以通过多种方式在Java中显式设置无头模式：

-   以编程方式将系统属性java.awt.headless设置为true
-   使用命令行参数：java -Djava.awt.headless=true
-   在服务器启动脚本中将 -Djava.awt.headless=true添加到JAVA_OPTS环境变量

如果环境实际上是无头的，则 JVM 会隐式地意识到它。但是，在某些场景下会有细微的差别。我们很快就会见到他们。

## 3. Headless模式下的UI组件示例

在无头环境中运行的 UI 组件的典型用例可能是图像转换器应用程序。虽然图像处理需要图形数据，但显示器并不是必需的。该应用程序可以在服务器上运行，并将转换后的文件保存或通过网络发送到另一台机器进行显示。

让我们看看实际效果。

首先，我们将在JUnit类中以编程方式打开无头模式：

```java
@Before
public void setUpHeadlessMode() {
    System.setProperty("java.awt.headless", "true");
}

```

为了确保它设置正确，我们可以使用java.awt.GraphicsEnvironment # isHeadless：

```java
@Test
public void whenSetUpSuccessful_thenHeadlessIsTrue() {
    assertThat(GraphicsEnvironment.isHeadless()).isTrue();
}

```

我们应该记住，即使没有明确打开模式，上述测试也会在无头环境中成功。

现在让我们看看我们的简单图像转换器：

```java
@Test
public void whenHeadlessMode_thenImagesWork() {
    boolean result = false;
    try (InputStream inStream = HeadlessModeUnitTest.class.getResourceAsStream(IN_FILE); 
      FileOutputStream outStream = new FileOutputStream(OUT_FILE)) {
        BufferedImage inputImage = ImageIO.read(inStream);
        result = ImageIO.write(inputImage, FORMAT, outStream);
    }

    assertThat(result).isTrue();
}
```

在下一个示例中，我们可以看到所有字体的信息，包括字体指标，也可供我们使用：

```java
@Test
public void whenHeadless_thenFontsWork() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    String fonts[] = ge.getAvailableFontFamilyNames();
      
    assertThat(fonts).isNotEmpty();

    Font font = new Font(fonts[0], Font.BOLD, 14);
    FontMetrics fm = (new Canvas()).getFontMetrics(font);
        
    assertThat(fm.getHeight()).isGreaterThan(0);
    assertThat(fm.getAscent()).isGreaterThan(0);
    assertThat(fm.getDescent()).isGreaterThan(0);
}
```

## 4.无头异常

有些组件需要外围设备，无法在无头模式下工作。在非交互式环境中使用时，它们会抛出HeadlessException ：

```shell
Exception in thread "main" java.awt.HeadlessException
	at java.awt.GraphicsEnvironment.checkHeadless(GraphicsEnvironment.java:204)
	at java.awt.Window.<init>(Window.java:536)
	at java.awt.Frame.<init>(Frame.java:420)
```

该测试断言在无头模式下使用Frame确实会抛出HeadlessException：

```java
@Test
public void whenHeadlessmode_thenFrameThrowsHeadlessException() {
    assertThatExceptionOfType(HeadlessException.class).isThrownBy(() -> {
        Frame frame = new Frame();
        frame.setVisible(true);
        frame.setSize(120, 120);
    });
}

```

根据经验，请记住Frame和Button等顶级组件始终需要交互式环境，因此会抛出此异常。但是，如果未明确设置无头模式，它将作为不可恢复的错误抛出。

## 5. 在 Headless 模式下绕过重量级组件

在这一点上，我们可能会问自己一个问题——但是如果我们有带有 GUI 组件的代码可以在两种类型的环境中运行——有头的生产机器和无头的源代码分析服务器呢？

在上面的例子中，我们已经看到重量级组件不会在服务器上工作并且会抛出异常。

因此，我们可以使用条件方法：

```java
public void FlexibleApp() {
    if (GraphicsEnvironment.isHeadless()) {
        System.out.println("Hello World");
    } else {
        JOptionPane.showMessageDialog(null, "Hello World");
    }
}
```

使用这种模式，我们可以创建一个灵活的应用程序，根据环境调整其行为。

## 六，总结

通过不同的代码示例，我们了解了Java中无头模式的方式和原因。[这篇技术文章](https://www.oracle.com/technical-resources/articles/javase/headless.html)提供了在无头模式下操作时可以完成的所有操作的完整列表。