## 1. 概述

在这个简短的教程中，我们将看一个如何使用JavaScript在[Selenium](https://www.selenium.dev/) WebDriver中单击和元素的简单示例。

对于我们的演示，我们将使用[JUnit和Selenium](https://www.baeldung.com/java-selenium-with-junit-and-testng)打开[https://baeldung.com](https://baeldung.com)并搜索“Selenium”文章。

## 2. 依赖

首先，我们在pom.xml中将[selenium-java](https://central.sonatype.com/artifact/org.seleniumhq.selenium/selenium-java/4.8.1)和[junit](https://central.sonatype.com/artifact/junit/junit/4.13.2)依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>3.141.59</version>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13</version>
    <scope>test</scope>
</dependency>
```

## 3. 配置

接下来，我们需要配置WebDriver。在此示例中，我们将在下载其[最新版本](https://chromedriver.chromium.org/downloads)后使用其Chrome实现：

```java
public class SeleniumJavaScriptClickLiveTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", new File("src/main/resources/chromedriver.exe").getAbsolutePath());
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.of(20, ChronoUnit.SECONDS));
    }

    @After
    public void cleanUp() {
        driver.close();
    }
}
```

我们使用带有@Before注解的方法在每次测试之前进行初始设置。在内部，我们设置了webdriver.chrome.driver属性来定义[chrome驱动程序](https://chromedriver.chromium.org/downloads)位置。之后，我们实例化WebDriver对象。

测试完成后，我们应该关闭浏览器窗口。我们可以通过将driver.close()语句放在使用@After标注的方法中来做到这一点。这可确保即使测试失败也会执行它。

```java
@After
public void cleanUp() {
    driver.close();
}
```

## 4. 打开浏览器

现在，我们可以创建一个测试用例来完成我们的第一步-打开网站：

```java
@Test
public void whenSearchForSeleniumArticles_thenReturnNotEmptyResults() {
    driver.get("https://baeldung.com");
    String title = driver.getTitle();
    assertEquals("Baeldung | Java, Spring and Web Development tutorials", title);
}
```

在这里，我们使用driver.get()方法来加载网页。接下来，我们验证其标题以确保我们在正确的位置。

## 5. 使用JavaScript单击元素

**Selenium附带了一个方便的WebElement.click()方法**，该方法调用给定元素上的单击事件。**但在某些情况下，单击操作是不可能的**。

一个例子是如果我们想单击一个被禁用的元素。在这种情况下，WebElement.click()会引发IllegalStateException。相反，我们可以使用Selenium的JavaScript支持。

为此，我们首先需要的是创建一个JavascriptExecutor。由于我们使用的是FirefoxDriver实现，我们可以简单地将其转换为我们需要的内容：

```java
JavascriptExecutor executor = (JavascriptExecutor) driver;
```

得到JavascriptExecutor后，我们就可以使用它的executeScript()方法了。参数是脚本本身和脚本参数数组。在我们的例子中，我们在第一个参数上调用click()方法：

```java
executor.executeScript("arguments[0].click();", element);
```

现在，让我们把它放在一个称为clickElement()的方法中：

```java
private void clickElement(WebElement element) {
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    executor.executeScript("arguments[0].click();", element);
}
```

最后，我们可以将其添加到我们的测试中：

```java
@Test
public void whenSearchForSeleniumArticles_thenReturnNotEmptyResults() {
    // ... load https://baeldung.com
    WebElement searchButton = driver.findElement(By.className("nav--menu_item_anchor"));
    clickElement(searchButton);

    WebElement searchInput = driver.findElement(By.id("search"));
    searchInput.sendKeys("Selenium");

    WebElement seeSearchResultsButton = driver.findElement(By.cssSelector(".btn-search"));
    clickElement(seeSearchResultsButton);
}
```

## 6. 不可点击的元素

使用JavaScript单击元素时出现的最常见问题之一是在元素可单击之前执行单击脚本。**在这种情况下，单击动作不会发生，但代码会继续执行**。

为了克服这个问题，我们必须在点击可用之前暂停执行。我们可以使用WebDriverWait#until来等待按钮被渲染。

首先，WebDriverWait对象需要两个参数；驱动程序和超时：

```java
WebDriverWait wait = new WebDriverWait(driver, 5000);
```

然后，我们调用until，给出预期的elementToBeClickable条件：

```java
wait.until(ExpectedConditions.elementToBeClickable(By.className("nav--menu_item_anchor")));
```

一旦成功返回，我们知道我们可以继续：

```java
WebElement searchButton = driver.findElement(By.className("nav--menu_item_anchor"));
clickElement(searchButton);
```

有关更多可用的条件方法，请参阅[官方文档](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/ui/ExpectedConditions.html)。

## 7. 总结

在本教程中，我们学习了如何使用JavaScript在Selenium中单击元素。