## 1. 概述

在这个简短的教程中，我们将看一个如何使用JavaScript在Selenium WebDriver中单击和元素的简单示例。

对于我们的演示，我们将使用JUnit和Selenium打开https://baeldung.com并搜索“Selenium”文章。

## 2. 配置

我们需要配置WebDriver。在本例中，我们将在下载其最新版本后使用其FireFox实现：

```java
public class SeleniumJavaScriptClickLiveTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        System.setProperty("webdriver.firefox.bin", "D:softwareFireFoxfirefox.exe");
        System.setProperty("webdriver.gecko.driver", "D:java-workspaceintellij-workspacetesting-develop-in-actiontesting-in-action-selenium-junit-testngsrcmainresourcesgeckodriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 20);
    }

    @After
    public void cleanUp() {
        driver.close();
    }
}
```

我们使用带有@Before注解的方法在每次测试之前进行初始设置。
在内部，我们设置了定义firefox驱动程序位置的webdriver.chrome.driver属性。之后，我们实例化WebDriver对象。

测试完成后，我们应该关闭浏览器窗口。我们可以通过将driver.close()语句放在使用@After注解的方法中来做到这一点。
这确保即使测试失败也会执行它。

## 3. 打开浏览器

现在，我们可以创建一个测试用例来完成我们的第一步-打开网站：

```java
public class SeleniumJavaScriptClickLiveTest {

    @Test
    public void whenSearchForSeleniumArticles_thenReturnNotEmptyResults() {
        driver.get("https://baeldung.com");
        String title = driver.getTitle();
        assertEquals("Baeldung | Java, Spring and Web Development tutorials", title);
    }
}
```

在这里，我们使用driver.get()方法来加载网页。接下来，我们验证其标题以确保我们在正确的位置。

## 4. 使用JavaScript单击元素

**Selenium带有一个方便的WebElement.click()方法**，该方法调用给定元素的单击事件。**但在某些情况下，单击操作是不可能的**。

一个例子是如果我们想点击一个被禁用的元素。在这种情况下，WebElement.click()会引发IllegalStateException。
相反，我们可以使用Selenium的JavaScript支持。

为此，我们首先需要的是创建一个JavascriptExecutor。由于我们使用的是FirefoxDriver实现，我们可以简单地将其转换为我们需要的内容：

```
JavascriptExecutor executor = (JavascriptExecutor) driver;
```

拿到JavascriptExecutor后，我们就可以使用它的executeScript()方法了。
参数是脚本本身和脚本参数数组。在我们的例子中，我们在第一个参数上调用click()方法：

```
executor.executeScript("arguments[0].click();", element);
```

现在，让我们把它放在一个我们将调用clickElement()的方法中：

```java
public class SeleniumJavaScriptClickLiveTest {

    private void clickElement(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }
}
```

最后，我们可以将其添加到我们的测试中：

```java
public class SeleniumJavaScriptClickLiveTest {
    @Test
    public void whenSearchForSeleniumArticles_thenReturnNotEmptyResults() {
        WebElement searchButton = driver.findElement(By.className("nav--menu_item_anchor"));
        clickElement(searchButton);

        WebElement searchInput = driver.findElement(By.id("search"));
        searchInput.sendKeys("Selenium");

        WebElement seeSearchResultsButton = driver.findElement(By.cssSelector(".btn-search"));
        clickElement(seeSearchResultsButton);
    }
}
```

## 5. 不可点击的元素

使用JavaScript单击元素时最常见的问题之一是在元素可单击之前执行单击脚本。**
在这种情况下，点击动作不会发生，但代码会继续执行**。

为了克服这个问题，我们必须阻止执行，直到点击可用。我们可以使用WebDriverWait#until来等待按钮被渲染。

首先，WebDriverWait对象需要两个参数；驱动程序和超时：

```
WebDriverWait wait = new WebDriverWait(driver, 5000);
```

然后，我们调用until，给出预期的elementToBeClickable条件：

```
wait.until(ExpectedConditions.elementToBeClickable(By.className("nav--menu_item_anchor")));
```

一旦成功返回，我们知道我们可以继续：

```
WebElement searchButton = driver.findElement(By.className("nav--menu_item_anchor"));
clickElement(searchButton);
```

## 6. 总结

在本教程中，我们学习了如何使用JavaScript在Selenium中单击元素。