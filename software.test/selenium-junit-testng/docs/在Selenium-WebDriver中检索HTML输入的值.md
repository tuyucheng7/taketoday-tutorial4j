## 1. 概述

众所周知，自动化测试是软件开发的重要组成部分。**[Selenium](https://www.baeldung.com/java-selenium-with-junit-and-testng)是一种广泛使用的工具，它使用特定于浏览器的驱动程序来自动测试Web应用程序**。

在本教程中，我们将学习如何设置[Selenium](https://www.baeldung.com/selenium-webdriver-page-object)项目以及**如何从网页中检索HTML输入字段的值**。

## 2. 什么是Selenium WebDriver？

Selenium WebDriver是一个开源的自动化测试框架，它在本地驱动Web浏览器，像真实用户一样与它们交互。它支持一系列浏览器，包括Chrome、Firefox、Edge和Safari。

它可用于自动执行各种任务，例如Web测试、Web抓取和用户验收测试。

简单地说，**WebDriver是一个应用程序编程接口(API)，它以不同的编程语言实现**。驱动程序负责Selenium和浏览器之间的通信。

## 3. 设置Selenium WebDriver项目

**要在任何项目中开始使用Selenium，我们需要安装Selenium库和浏览器驱动程序**。我们可以通过将其依赖项添加到pom.xml使用Maven安装[Selenium](https://mvnrepository.com/artifact/org.seleniumhq.selenium/selenium-java)库：

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.8.3</version>
</dependency>
```

此外，要安装浏览器驱动程序，我们将使用驱动程序管理软件。[WebDriverManager](https://mvnrepository.com/artifact/io.github.bonigarcia/webdrivermanager)是一个提供此功能的Java库，让我们将其依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.3.2</version>
</dependency>
```

或者，我们可以从Selenium官方网站[下载](https://www.selenium.dev/documentation/webdriver/getting_started/install_drivers/#quick-reference)浏览器驱动程序。下载驱动程序后，我们需要解压它，将它放在项目根目录中，并在代码中指向它的位置。

在下一节中，我们将使用Selenium脚本检索DuckDuckGo主页中输入字段的值。

## 4. 使用Selenium WebDriver检索HTML输入值

首先，我们将检索[DuckDuckGo](https://duckduckgo.com/)的搜索字段中的输入值。主页将作为我们的示例HTML页面。它有一个ID为“search_form_input_homepage”的输入字段，用于输入搜索查询。

接下来，让我们编写一个Selenium脚本来检索输入字段的值：

```java
class SeleniumWebDriverUnitTest {

    private WebDriver driver;
    private static final String URL = "https://duckduckgo.com";
    private static final String INPUT_ID = "search_form_input_homepage";

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().set();
        driver = new ChromeDriver();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void givenDuckDuckGoHomePage_whenInputHelloWorld_thenInputValueIsHelloWorld() {
        driver.get(URL);
        WebElement inputElement = driver.findElement(By.id(INPUT_ID));
        inputElement.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        inputElement.sendKeys("Hello World!");
        String inputValue = inputElement.getAttribute("value");

        assertEquals("Hello World!", inputValue);
    }
}
```

在上面的示例中，我们创建了一个WebDriver实例来控制Chrome Web浏览器。然后，我们使用WebDriver实例导航到URL为[https://duckduckgo.com](https://duckduckgo.com)的网页。

此外，我们在WebDriverManager上调用chromedriver().set()来设置浏览器驱动程序。它会自动为我们下载并设置Chrome驱动程序。此外，我们将驱动程序初始化为ChromeDriver对象，这有助于调用Chrome浏览器。

接下来，我们通过调用driver上的get()方法导航到URL。然后，我们创建一个名为inputElement的WebElement变量，并通过其id找到输入元素并将其分配给inputElement。**WebElement是表示Selenium WebDriver中HTML元素的接口。它提供了多种与元素交互的方法，例如sendKeys()、getText()等**。我们可以使用不同的定位器策略来查找Web元素，例如id、name或xpath。

此外，我们从代码中模拟浏览器清空输入框，输入"Hello World!"作为输入值。然后，我们创建一个String类型的变量inputValue，并使用参数“value”调用inputElement上的getAttribute()方法，并将其分配给inputValue。

此外，我们还有tearDown()方法，该方法关闭浏览器窗口并释放ChromeDriver对象使用的资源。

最后，我们断言预期结果等于来自具有指定输入id的网页的输入值。

## 5. 总结

在本文中，我们首先了解了如何使用驱动管理软件安装Selenium浏览器驱动程序以及如何手动下载它。然后，我们学习了如何使用Selenium WebDriver从HTML网页获取输入值。