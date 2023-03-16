## 1. 概述

在本教程中，我们将看看常见的Selenium错误：“The path to the driver executable must be set by the webdriver.chrome.driver system property”。此错误会阻止Selenium启动浏览器。这是由于配置不完整造成的。我们将学习如何通过手动或自动进行正确设置来解决此问题。

## 2. 错误原因

Selenium在我们使用它之前需要一些设置步骤，比如设置WebDriver的路径。**如果我们不配置WebDriver的路径，我们就无法运行它来控制浏览器，因此会得到一个java.lang.IllegalStateException**。

让我们看一下导致此错误的不完整设置：

```java
WebDriver driver = new ChromeDriver();
```

通过该语句，我们想要创建一个新的ChromeDriver实例，但由于我们没有提供WebDriver的路径，Selenium无法运行它并且失败并显示错误“java.lang.IllegalStateException: The path to the driver executable must be set by the webdriver.chrome.driver system property“。

要解决此问题，我们需要执行正确的设置。我们可以手动或使用专用库自动执行此操作。

## 3. 手动设置

首先，我们需要为我们的浏览器下载正确的WebDriver。根据我们的浏览器下载正确的版本至关重要，否则在运行时可能会出现无法预料的问题。

可以从以下站点下载正确的WebDriver：

-   Chrome：[https://chromedriver.chromium.org/downloads](https://chromedriver.chromium.org/downloads)
-   Edge：[https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/)
-   Firefox：[https://github.com/mozilla/geckodriver/releases](https://github.com/mozilla/geckodriver/releases)

然后，Selenium需要下载的驱动程序的路径，以便它可以运行它来控制浏览器。**我们可以使用[系统属性](https://www.baeldung.com/java-system-get-property-vs-system-getenv)设置驱动程序的路径**。每个浏览器的属性key是不同的：

-   Chrome：webdriver.chrome.driver
-   Firefox：webdriver.gecko.driver
-   Edge：webdriver.edge.driver

让我们看一下Chrome的手动设置。我们将路径设置为之前下载的WebDriver，然后创建一个ChromeDriver实例：

```java
WebDriver driver;

void setupChromeDriver() {
    System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
    driver = new ChromeDriver();
    options();
}

void options() {
    driver.manage().window().maximize();
}
```

路径可以是相对路径，也可以是绝对路径。此外，我们可以设置各种配置，例如在上面的示例中最大化浏览器窗口。

其他浏览器的设置非常相似。正如我们在下面看到的，我们只需要替换驱动程序设置方法并设置相应驱动程序的路径：

```java
void setupGeckoDriver() {
    System.setProperty("webdriver.gecko.driver", "src/test/resources/geckodriver.exe");
    driver = new FirefoxDriver();
    options();
}

void setupEdgeDriver() {
    System.setProperty("webdriver.edge.driver", "src/test/resources/msedgedriver.exe");
    driver = new EdgeDriver();
    options();
}
```

为了验证设置，我们可以对[https://www.baeldung.com](https://www.baeldung.com/java-selenium-webdriver-path-error)上执行一个小检查：

```java
String TITLE_XPATH = "//a[@href='/']";
String URL = "https://www.baeldung.com";

@Test
void givenChromeDriver_whenNavigateToBaeldung_thenFindTitleIsSuccessful() {
    setupChromeDriver();
    driver.get(URL);
    final WebElement title = driver.findElement(By.xpath(TITLE_XPATH));

    assertEquals("Baeldung", title.getAttribute("title"));
}
```

**如果设置仍然不起作用，我们需要确保WebDriver的路径是正确的**。

## 4. 自动设置

手动设置可能很麻烦，因为我们需要手动下载特定的WebDriver。我们还需要确保我们使用的是正确的版本。如果安装的浏览器启用了自动更新，这可能需要我们定期将WebDriver替换为更新的版本。

为了克服这个问题，我们可以使用[WebDriverManager](https://bonigarcia.dev/webdrivermanager/)库，它会在我们每次运行时为我们处理这些任务。

首先，我们需要将[依赖项](https://central.sonatype.com/artifact/io.github.bonigarcia/webdrivermanager/5.3.2)添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.3.0</version>
</dependency>
```

使用该库的设置非常简单，只需要一行代码：

```java
WebDriver driver;

void setupChromeDriver() {
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    options();
}

void options() {
    driver.manage().window().maximize();
}
```

在安装过程中，WebDriverManager会检查安装的浏览器版本并自动下载正确的WebDriver版本。它设置系统属性，然后运行浏览器。

针对其他浏览器调整设置也很简单：

```java
void setupGeckoDriver() {
    WebDriverManager.firefoxdriver().setup();
    driver = new FirefoxDriver();
    options();
}

void setupEdgeDriver() {
    WebDriverManager.edgedriver().setup();
    driver = new EdgeDriver();
    options();
}
```

同样，我们可以通过对[https://www.baeldung.com](https://www.baeldung.com/java-selenium-webdriver-path-error)进行测试来验证此设置：

```java
String TITLE_XPATH = "//a[@href='/']";
String URL = "https://www.baeldung.com";

@Test
void givenChromeDriver_whenNavigateToBaeldung_thenFindTitleIsSuccessful() {
    setupChromeDriver();
    driver.get(URL);
    final WebElement title = driver.findElement(By.xpath(TITLE_XPATH));

    assertEquals("Baeldung", title.getAttribute("title"));
}
```

## 5. 总结

在本文中，我们了解了导致Selenium错误“The path to the driver executable must be set by the webdriver.chrome.driver system property”的原因以及我们如何修复它。

我们可以进行手动设置，但会导致一些维护工作。使用WebDriverManager库的自动设置减少了使用Selenium时的维护。