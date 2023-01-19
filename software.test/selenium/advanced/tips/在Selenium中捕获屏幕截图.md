当我们编写自动化测试时，我们在被测应用程序上执行测试场景，以减少执行回归测试的手动工作量。如果你有一个巨大的回归套件并且测试失败了，你需要检查和分析测试流程以确定失败的地方以及失败的原因。如果 Selenium 测试在执行时捕获屏幕截图以便你查看屏幕截图以查看应用程序在特定时间的状态，那不是很好吗？自动化测试帮助我们实现了这一点，这样我们就可以节省每次脚本失败时重新运行整个测试的时间。

我们可以使用测试脚本在运行时截取屏幕截图，该脚本通过查看应用程序在失败时的状态来帮助我们进行错误分析。在本教程中，我们将详细讨论屏幕截图的必要性以及如何使用网页的 Selenium 屏幕截图。

-   为什么自动化测试需要截图？
-   如何在 Selenium 中截取屏幕截图？
    -   如何在 Selenium 中截取整个页面的屏幕截图？
    -   还有，如何截取 Selenium 中特定元素的屏幕截图？

## 为什么自动化测试需要截图？

众所周知，自动化测试的主要目的之一是减少手动工作。因此，使用在自动化测试运行期间捕获的屏幕截图变得非常有用。你不希望每次执行测试时都监视你的应用程序。该脚本可以截取屏幕截图，这有助于在测试执行完成时检查应用程序功能/状态。当你的测试用例失败时，屏幕截图还可以帮助你确定脚本或应用程序中出现的问题。

屏幕截图很有用，特别是在[无头测试执行](https://www.toolsqa.com/selenium-webdriver/selenium-headless-browser-testing/)中，你看不到应用程序的GUI。尽管如此，Selenium 仍会通过屏幕截图捕获它并将其存储在一个文件中，以便你稍后可以验证该应用程序。

它们还有助于区分失败是由于应用程序失败还是由于测试脚本失败。以下场景将是重要的用例，其中的屏幕截图将方便调试导致的自动化测试用例失败：

-   当应用程序出现问题时
-   当发生断言失败时。
-   此外，当在页面上查找 Web 元素有一些困难时。
-   在网页上查找网络元素时超时的地方

因此，考虑到这些用例，捕获屏幕截图已成为所有 UI 自动化工具不可或缺的一部分。Selenium WebDriver还提供了多种捕获屏幕截图的方法。让我们看看我们如何在使用Selenium 自动化 Web 应用程序时做同样的事情？

## 如何在 Selenium 中截取屏幕截图？

要在 Selenium 中截取屏幕截图，我们使用一个名为TakesScreenshot的接口，它使[Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/selenium-testing/)能够捕获屏幕截图并以不同的方式存储它。它有一个获取屏幕截图并将其存储在指定位置的方法“ getScreenshotAs() ”。

下面是使用Selenium WebDriver捕获网页当前可见部分的屏幕截图的基本语法：

```java
//Convert webdriver to TakeScreenshot
File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
```

在上面的代码中，我们将WebDriver对象 ( driver ) 转换为TakeScreenshot。并通过提供参数 \ OutputType .FILE调用getScreenshotAs()方法来创建图像文件。

现在我们可以使用这个[File](https://www.toolsqa.com/selenium-webdriver/screenshot-in-selenium/)对象将图像复制到我们想要的位置，如下所示，使用[FileUtils](https://commons.apache.org/proper/commons-io/javadocs/api-2.5/org/apache/commons/io/FileUtils.html)类。

```java
FileUtils.copyFile(screenshotFile , new File("C:\\temp\\screenshot.png));
```

让我们试着结合上面的代码，编写代码来截取[ToolsQA演示站点](https://demoqa.com/)主页的截图——

```java
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;

public class ScreenshotDemo {
    public static void main(String[] args) {
        //set the location of chrome browser
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        
        // Initialize browser
        WebDriver driver = new ChromeDriver();
        
        //navigate to url
        driver.get("https://demoqa.com");
        
       //Take the screenshot
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        
        //Copy the file to a location and use try catch block to handle exception
        try {
            FileUtils.copyFile(screenshot, new File("C:\\projectScreenshots\\homePageScreenshot.png"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
        //closing the webdriver
        driver.close();
    }
}
```

上述程序的输出将在指定位置生成一个文件，如下所示 -

![HomePageScreenshot 作为代码输出](https://www.toolsqa.com/gallery/selnium%20webdriver/1.HomePageScreenshot%20as%20code%20output.png)

如果你打开这个文件，你会看到由Selenium WebDriver捕获的网页图像是可视部分。如果你注意到图块(如突出显示的那样)，则它们没有完全捕获。这样，Selenium WebDriver只捕获了可见的部分网页的屏幕截图。我们无法使用此方法捕获整个页面的屏幕截图。

![可视区域截图](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Viewable%20area%20screenshot.png)

让我们看看如何使用Selenium WebDriver解决此问题以捕获整页屏幕截图。

### 如何在 Selenium 中截取整个页面的屏幕截图？

Selenium WebDriver不提供捕获整个页面屏幕截图的固有功能。[要捕获完整的页面屏幕截图，我们必须使用名为Ashot](https://github.com/pazone/ashot)的第三方库。它提供了截取特定WebElement的屏幕截图以及整页屏幕截图的功能。[你可以从“ https://jar-download.com/artifacts/ru.yandex.qatools.ashot/ashot](https://jar-download.com/artifacts/ru.yandex.qatools.ashot/ashot) ”下载 Ashot 库的 jar 文件， 然后按照指定的步骤将其添加为外部依赖项文章《[Add External library to Eclipse project](https://www.toolsqa.com/selenium-webdriver/configure-selenium-webdriver-with-eclipse/)》 将Ashot库添加到Eclipse项目后，我们就可以使用以下方法进行网页截图：

-   捕获屏幕尺寸图像，即屏幕上的可视区域。捕获当前可见页面的屏幕截图将与上一节中看到的相同。你也可以通过以下代码使用Ashot完成相同的任务。在那里我们创建一个Ashot对象并调用takeScreenshot()方法：

```java
Screenshot screenshot = new Ashot().takeScreenshot(driver);
```

-   捕获整页屏幕截图，这比屏幕上当前可见的部分要多。创建AShot对象后，我们需要先调用shootingStrategy()方法，然后再调用takeScreenshot()方法来设置策略。我们可以写下面的代码来做到这一点：

```java
Screenshot s=new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
ImageIO.write(s.getImage(),"PNG",new File("<< file path>>"));
```

-   在上面的代码中，1000 是以毫秒为单位的滚出时间。换句话说，这意味着程序将每 1000 毫秒滚动一次以截取屏幕截图。

让我们尝试将其应用于ToolsQA 演示站点主页以获取整个页面的屏幕截图，包括可视区域屏幕截图中缺少的图块。

```java
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ScreenshotDemo {
    public static void main(String[] args) throws IOException {
        //set the location of chrome browser
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        
        // Initialize browser
        WebDriver driver = new ChromeDriver();
        
        //navigate to url
        driver.get("https://demoqa.com");

        // capture screenshot and store the image
        Screenshot s=new AShot().shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
        ImageIO.write(s.getImage(),"PNG",new File("C:\\projectScreenshots\\fullPageScreenshot.png"));
        
        //closing the webdriver
        driver.close();
    }
}
```

上述代码的输出将在指定路径中生成图像。它看起来像下面，包含页面的所有元素 -

![全页截图](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Full%20Page%20Screenshot.png)

另一个用例是当我们想要捕获特定 Web 元素的屏幕截图时。Selenium WebDriver也提供了实现相同目的的特定方法。让我们看看如何使用 Selenium WebDriver 捕获 Web 特定元素的屏幕截图？

### 如何截取 Selenium 中特定元素的屏幕截图？

有时，我们需要页面上特定元素的屏幕截图。有两种方法可以在 Selenium 中捕获 Web 元素的屏幕截图 -

1.  获取全屏图像，然后根据网络元素的尺寸裁剪图像。
2.  在 web 元素上使用getScreenshotAs()方法。(这仅在 selenium 版本 4.X 中可用)

让我们在以下部分中了解这两种方法：

#### 如何通过裁剪整页截图来捕获特定元素的屏幕截图？

你可以按照以下步骤截取[ToolsQA 演示站点](https://demoqa.com/)上显示的ToolsQA徽标的屏幕截图。

-   将视口屏幕截图捕获为缓冲图像。
-   使用 getSize() 方法获取元素的高度和宽度。
-   使用 Point 类获取元素的 XY 坐标。
-   读取缓冲图像。
-   使用元素的 x、y 坐标位置和高度、宽度参数裁剪缓冲图像。
-   将裁剪后的图像物理保存在目标位置。

以下代码片段显示了我们如何以编程方式实现相同的目的：

```java
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.;
import org.openqa.selenium.chrome.ChromeDriver;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ScreenshotDemo {
    public static void main(String[] args) throws IOException {
        //set the location of chrome browser
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        
        // Initialize browser
        WebDriver driver = new ChromeDriver();
        
        //navigate to url
        driver.get("https://demoqa.com");
        
        // Locate the element on the web page
        WebElement logo = driver.findElement(By.xpath("//[@id=\"app\"]/header/a/img"));
        
        // Get screenshot of the visible part of the web page
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        
        // Convert the screenshot into BufferedImage
        BufferedImage fullScreen = ImageIO.read(screenshot);
        
        //Find location of the webelement logo on the page
        Point location = logo.getLocation();
        
        //Find width and height of the located element logo
        int width = logo.getSize().getWidth();
        int height = logo.getSize().getHeight();

	//cropping the full image to get only the logo screenshot
        BufferedImage logoImage = fullScreen.getSubimage(location.getX(), location.getY(),
                width, height);
        ImageIO.write(logoImage, "png", screenshot);
        
        //Save cropped Image at destination location physically.
        FileUtils.copyFile(screenshot, new File("C:\\projectScreenshots\\particularElementScreenshot.PNG"));

        driver.quit();
    }
}
```

上述代码的输出将是在指定位置生成的文件，如下所示，仅显示徽标：

![为特定元素生成的文件](https://www.toolsqa.com/gallery/selnium%20webdriver/4.File%20generated%20for%20particular%20element.png)

打开截图文件，只有logo，如下图：

![特定元素截图](https://www.toolsqa.com/gallery/selnium%20webdriver/5.particular%20element%20screenshot.png)

这样一来，你就可以裁剪任何现有的屏幕截图。它可以帮助你获取特定 Web 元素的一部分或屏幕截图。

但是，Selenium随着时间的推移而成熟，并在Selenium 4.xx中提供了一种更好、更简单的方法 来捕获 Web 元素的屏幕截图。让我们看看我们如何才能得到相同的？

#### 如何使用 getScreenshotAs() 方法截取特定元素的屏幕截图？

[我们可以使用WebElement](https://www.toolsqa.com/selenium-webdriver/webelement-commands/)类 的getScreenshotAs ( OutputType.File ) 方法捕获Selenium 4.0中特定元素的屏幕截图。因此，现在可以在WebElement上直接调用getScreenshotAs()方法，我们要为其捕获屏幕截图：

```java
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.File;
import java.io.IOException;

public class ScreenshotDemo {
    public static void main(String[] args) throws IOException {
        //set the location of chrome browser
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        
        // Initialize browser
        WebDriver driver = new ChromeDriver();
        
        //navigate to url
        driver.get("https://demoqa.com");
        
        // Locate the web element
        WebElement logo = driver.findElement(By.xpath("//[@id=\"app\"]/header/a/img"));
        
        // capture screenshot with getScreenshotAs() of the WebElement class
        File f = logo.getScreenshotAs(OutputType.FILE);
        
        FileUtils.copyFile(f, new File("C:\\projectScreenshots\\logoScreeshot.png"));
        
        driver.close();
    }
}
```

以上代码的输出将是logo的截图，如下图：

![标志截图文件](https://www.toolsqa.com/gallery/selnium%20webdriver/6.Logo%20Screenshot%20File.png)

因此，通过这种方式，我们可以通过直接在WebElement类的对象上调用该方法来捕获任何Web 元素的屏幕截图。

## 关键要点

-   Selenium Webdriver 允许你使用TakeScreenshot接口的getScreenShotAs方法截取屏幕截图。
-   你可以截取可视区域、整页或特定元素的屏幕截图。
-   对于整页截图，我们可以使用提供截图能力的第三方库AShot 。
-   对于特定元素，我们可以截取整页屏幕截图，然后裁剪图像。如果我们使用的是 Selenium 4.X，我们可以直接调用 WebElement 上的 getScreenShotAs() 方法来捕获它的屏幕截图。