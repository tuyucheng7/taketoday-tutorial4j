## 1. 概述

本文是对使用[Selenium](http://www.seleniumhq.org/)以及使用[JUnit](http://junit.org/)和[TestNG](http://testng.org/)编写测试的快速、实用的介绍。

## 2. Selenium集成

在本节中，我们将从一个简单的场景开始-打开一个浏览器窗口，导航到给定的URL并在页面上查找一些所需的内容。

### 2.1 Maven依赖

在pom.xml文件中，添加以下依赖项：

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>3.4.0</version>
</dependency>
```

最新版本可以在[Maven中央仓库](https://central.sonatype.com/artifact/org.seleniumhq.selenium/selenium-java/4.8.1)中找到。

### 2.2 Selenium配置

首先，创建一个名为SeleniumConfig的新Java类：

```java
public class SeleniumConfig {

    private WebDriver driver;
    // ...
}
```

鉴于我们使用的是Selenium 3.x版本，我们必须使用名为webdriver.chrome.driver的系统属性指定可执行ChromeDriver文件的路径(基于你的操作系统)。最新版本的ChromeDriver可以从[这里](https://chromedriver.chromium.org/downloads)下载。

现在让我们在构造函数中初始化WebDriver，我们还将设置5秒作为WebDriver等待页面上元素出现的超时时间：

```java
static {
    System.setProperty("webdriver.chrome.driver", findFile("chromedriver.exe"));
}

public SeleniumConfig() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--remote-allow-origins=*");
    driver = new ChromeDriver(options);
    driver.manage()
        .timeouts()
        .implicitlyWait(5, TimeUnit.SECONDS);
}

private static String findFile(String filename) {
    String[] paths = {"", "bin/", "target/classes"};
    for (String path : paths) {
        if (new File(path + filename).exists())
            return path + filename;
    }
    return "";
}
```

这个配置类包含几个我们暂时忽略的方法，但我们将在本系列的第二部分看到有关这些方法的更多信息。

接下来，我们需要实现一个SeleniumExample类：

```java
public class SeleniumExample {

    private SeleniumConfig config;
    private String url = "http://www.baeldung.com/";

    public SeleniumExample() {
        config = new SeleniumConfig();
        config.getDriver().get(url);
    }

    // ...
}
```

在这里，我们将初始化SeleniumConfig并设置要导航到的所需URL。同样，我们将实现一个简单的API来关闭浏览器并获取页面标题：

```java
public void closeWindow() {
    this.config.getDriver().close();
}

public String getTitle() {
    return this.config.getDriver().getTitle();
}
```

为了导航到baeldung.com的About部分，我们需要创建一个closeOverlay()方法来检查和关闭主页加载时的覆盖层。此后，我们使用getAboutBaeldungPage()方法导航到About Baeldung页面：

```java
public void getAboutBaeldungPage() {
    closeOverlay();
    clickAboutLink();
    clickAboutUsLink();
}

private void closeOverlay() {
    List<WebElement> webElementList = this.config.getDriver()
          .findElements(By.tagName("a"));
    if (webElementList != null) {
        webElementList.stream()
            .filter(webElement -> "Close".equalsIgnoreCase(webElement.getAttribute("title")))
            .filter(WebElement::isDisplayed)
            .findAny()
            .ifPresent(WebElement::click);
    }
}

private void clickAboutLink() {
    Actions actions = new Actions(config.getDriver());
    WebElement aboutElement = this.config.getDriver().findElement(By.id("menu-item-6138"));
    actions.moveToElement(aboutElement).perform();
}

private void clickAboutUsLink() {
    WebElement element = this.config.getDriver().findElement(By.partialLinkText("About Baeldung."));
    element.click();
}
```

我们可以检查显示的页面上是否提供了所需的信息：

```java
public boolean isAuthorInformationAvailable() {
    return this.config.getDriver()
        .getPageSource()
        .contains("Hey ! I'm Eugen");
}
```

接下来，我们将使用JUnit和TestNG测试这个类。

## 3. JUnit

让我们创建一个新的测试类SeleniumWithJUnitLiveTest：

```java
public class SeleniumWithJUnitLiveTest {

    private static SeleniumExample seleniumExample;
    private String expectedTitle = "About Baeldung | Baeldung";

    // more code goes here ...
}
```

我们将使用org.junit.BeforeClass中的@BeforeClass注解进行初始设置。在这个setUp()方法中，我们将初始化SeleniumExample对象：

```java
@BeforeClass
public static void setUp() {
    seleniumExample = new SeleniumExample();
}
```

以类似的方式，当我们的测试用例完成时，我们应该关闭新打开的浏览器。

我们将使用@AfterClass注解来执行此操作-在测试用例执行完成时清理设置：

```java
@AfterClass
public static void tearDown() throws IOException {
    seleniumExample.closeWindow();
}
```

请注意我们的SeleniumExample成员变量上的static修饰符-因为我们需要在setUp()和tearDown()静态方法中使用这个变量，而@BeforeClass和@AfterClass只能在静态方法上调用。

最后，我们可以创建完整的测试：

```java
@Test
public void whenAboutBaeldungIsLoaded_thenAboutEugenIsMentionedOnPage() {
    seleniumExample.getAboutBaeldungPage();
    String actualTitle = seleniumExample.getTitle();
    
    assertNotNull(actualTitle);
    assertEquals(expectedTitle, actualTitle);
    assertTrue(seleniumExample.isAuthorInformationAvailable());
}
```

此测试方法断言网页的标题不为null并且按预期设置。除此之外，我们检查页面是否包含预期信息。

当测试运行时，它只是在Chrome中打开URL，然后在验证网页标题和内容后将其关闭。

## 4. TestNG

现在让我们使用TestNG来运行我们的测试用例/套件。

请注意，如果你使用的是Eclipse，则可以从[Eclipse Marketplace](https://marketplace.eclipse.org/)下载并安装TestNG插件。

首先，让我们创建一个新的测试类：

```java
public class SeleniumWithTestNGLiveTest {

    private SeleniumExample seleniumExample;
    private String expectedTitle = "About Baeldung | Baeldung";

    // more code goes here ...
}
```

我们将使用来自org.testng.annotations.BeforeSuite的@BeforeSuite注解来实例化我们的SeleniumExample类。setUp()方法将在激活测试套件之前启动：

```java
@BeforeSuite
public void setUp() {
    seleniumExample = new SeleniumExample();
}
```

同样，一旦测试套件完成，我们将使用org.testng.annotations.AfterSuite中的@AfterSuite注解来关闭我们打开的浏览器：

```java
@AfterSuite
public void tearDown() {
    seleniumExample.closeWindow();
}
```

最后，让我们实现我们的测试：

```java
@Test
public void whenAboutBaeldungIsLoaded_thenAboutEugenIsMentionedOnPage() {
    seleniumExample.getAboutBaeldungPage();
    String actualTitle = seleniumExample.getTitle();
    
    assertNotNull(actualTitle);
    assertEquals(expectedTitle, actualTitle);
    assertTrue(seleniumExample.isAuthorInformationAvailable());
}
```

成功完成测试套件后，我们会在项目的测试输出文件夹中得到HTML和XML报告。这些报告总结了测试结果。

## 5. 总结

在这篇简短的文章中，我们重点介绍了如何使用JUnit和TestNG编写Selenium 3测试。