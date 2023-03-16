## 1. 概述

在使用Selenium进行自动化测试时，我们经常需要截取网页或网页的一部分。这可能很有用，尤其是在调试测试失败或验证我们的应用程序行为在不同浏览器之间是否一致时。

在这个快速教程中，**我们将介绍使用Selenium WebDriver从我们的[JUnit](https://www.baeldung.com/tag/junit/)测试中捕获屏幕截图的几种方法**。要了解有关使用Selenium进行测试的更多信息，请查看我们的[Selenium指南](https://www.baeldung.com/java-selenium-with-junit-and-testng)。

## 2. 依赖和配置

让我们首先将Selenium依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>3.141.59</version>
</dependency>
```

与往常一样，依赖的最新版本可以在[Maven Central](https://central.sonatype.com/artifact/org.seleniumhq.selenium/selenium-java/4.8.1)中找到。此外，可以从其[网站](https://chromedriver.chromium.org/downloads)下载最新版本的Chrome驱动程序。

现在让我们在单元测试中将驱动程序配置为使用Chrome：

```java
public class TakeScreenShotSeleniumLiveTest {

    private static ChromeDriver driver;

    @BeforeClass
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", resolveResourcePath("chromedriver.exe"));

        driver = new ChromeDriver();
        driver.manage()
              .timeouts()
              .implicitlyWait(Duration.ofSeconds(5));

        driver.get("http://www.google.com/");
    }

    @AfterClass
    public static void tearDown() throws IOException {
        driver.close();
        System.clearProperty("webdriver.chrome.driver");
    }
}
```

如我们所见，这是ChromeDriver的一个非常标准的Selenium配置，它可以让我们控制在本地机器上运行的Chrome浏览器。我们还将驱动程序在页面上搜索元素时应等待的时间配置为5秒。

最后，在我们的任何测试运行之前，我们在当前浏览器窗口中打开一个网页[www.google.com](www.google.com)。

## 3. 截取可视区域的屏幕截图

在第一个示例中，我们将看一下Selenium开箱即用的TakesScreenShot接口。顾名思义，我们可以使用这个接口来截取可视区域的屏幕截图。

让我们创建一个使用此接口截屏的简单方法：

```java
public void takeScreenshot(String pathname) throws IOException {
    File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    FileUtils.copyFile(src, new File(pathname));
}
```

在这个简洁的方法中，我们首先使用强制转换将驱动程序转换为TakesScreenshot。**然后我们可以调用getScreenshotAs()方法，使用指定的OutputType来创建图像文件**。

之后，我们可以使用[Apache Commons IO](https://www.baeldung.com/apache-commons-io) copyFile方法将文件复制到任何所需的位置。**只需两行，我们就可以捕获屏幕截图**。

现在让我们看看如何在单元测试中使用这个方法：

```java
@Test
public void whenGoogleIsLoaded_thenCaptureScreenshot() throws IOException {
    takeScreenshot(resolveTestResourcePath("google-home.png"));
    assertTrue(new File(resolveTestResourcePath("google-home.png")).exists());
}
```

在这个单元测试中，我们使用文件名google-home.png将生成的图像文件保存到我们的test/resources文件夹中，然后断言该文件是否存在。

## 4. 截取页面上的元素

在下一节中，我们将了解如何捕获页面上单个元素的屏幕截图。**为此，我们将使用一个名为ashot的库，这是一个由Selenium 3及更高版本原生支持的屏幕截图实用工具库**。

ashot可以从[Maven Central](https://central.sonatype.com/artifact/ru.yandex.qatools.ashot/ashot/1.5.4)获得，因此让我们将其包含在我们的pom.xml中：

```xml
<dependency>
    <groupId>ru.yandex.qatools.ashot</groupId>
    <artifactId>ashot</artifactId>
    <version>1.5.4</version>
</dependency>
```

ashot库提供了一个[流式API](https://en.wikipedia.org/wiki/Fluent_interface)，用于配置我们想要捕获屏幕截图的精确程度。

现在让我们看看如何从我们的一个单元测试中捕获Google主页的徽标：

```java
public class TakeScreenShotSeleniumLiveTest {

    private static String resolveResourcePath(String filename) {
        File file = new File("src/main/resources/" + filename);
        return file.getAbsolutePath();
    }

    private static String resolveTestResourcePath(String filename) {
        File file = new File("src/test/resources/" + filename);
        return file.getAbsolutePath();
    }

    @Test
    public void whenGoogleIsLoaded_thenCaptureLogo() throws IOException {
        WebElement logo = driver.findElement(By.id("hplogo"));

        Screenshot screenshot = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000))
              .coordsProvider(new WebDriverCoordsProvider())
              .takeScreenshot(driver, logo);

        ImageIO.write(screenshot.getImage(), "jpg", new File(resolveTestResourcePath("google-logo.png")));
        assertTrue(new File(resolveTestResourcePath("google-logo.png")).exists());
    }
}
```

我们首先使用id ”hplogo“在页面上找到一个WebElement。然后我们创建一个新的AShot实例并设置一个内置的拍摄策略-ShootingStrategies.viewportPasting(1000)。**此策略将在我们截取屏幕截图时滚动视口最多一秒钟(1000ms)**。

现在，我们配置了有关如何截取屏幕截图的策略。

当我们想要捕捉页面上的特定元素时，在内部，aShot会找到元素的大小和位置并裁剪原始图像。为此，我们调用coordsProvider()方法并传递一个WebDriverCoordsProvider类，该类将使用WebDriver API来查找任何坐标。

**请注意，默认情况下，aShot使用jQuery进行坐标解析。但是有些驱动程序在使用Javascript时存在问题**。

现在我们可以调用takeScreenshot()方法，传递我们的driver和logo元素，这反过来会为我们提供一个包含屏幕截图结果的Screenshot对象。和以前一样，我们通过写入图像文件并验证它的存在来完成我们的测试。

## 5. 总结

在这个快速教程中，我们看到了两种使用Selenium WebDriver捕获屏幕截图的方法。

在第一种方法中，我们看到了如何直接使用Selenium捕获整个屏幕。然后我们学习了如何使用名为ashot的强大实用程序库来捕获页面上的特定元素。

使用ashot的主要好处之一是不同的WebDriver在截屏时的行为不同。**使用ashot将我们从这种复杂性中抽象出来，并为我们提供透明的结果，而不管我们使用的是什么驱动程序**。请务必查看完整的[文档](https://github.com/pazone/ashot)以查看所有可用的受支持功能。