## 1. 概述

StaleElementReferenceException是我们在使用Selenium测试Web应用程序时遇到的常见错误。**当我们引用一个过时的元素时，Selenium会抛出StaleElementReferenceException**。由于页面刷新或DOM更新，元素变得过时。

在本教程中，我们将了解[Selenium](https://www.baeldung.com/java-selenium-with-junit-and-testng)中的StaleElementReferenceException是什么以及它发生的原因。然后，我们将看看如何在我们的Selenium测试中避免异常。

## 2. 避免StaleElementReferenceException的策略

为避免StaleElementReferenceException，必须确保元素被动态定位并与之交互，而不是存储对它们的引用。这意味着我们应该在每次需要时找到元素，而不是将它们保存在变量中。

在某些情况下，这种方法是行不通的，**我们需要在再次与元素交互之前刷新元素**。因此，我们的解决方案是捕获StaleElementReferenceException并在刷新元素后执行重试。我们可以直接在测试中执行此操作，也可以在所有测试中全局执行此操作。

对于我们的测试，我们将为定位器定义几个常量：

```java
By LOCATOR_REFRESH = By.xpath("//a[.='click here']");
By LOCATOR_DYNAMIC_CONTENT = By.xpath("(//div[@id='content']//div[@class='large-10 columns'])[1]");
```

对于设置，我们选择[使用WebDriverManager的自动化方法](https://www.baeldung.com/java-selenium-webdriver-path-error#automated-setup)。

### 2.1 生成StaleElementReferenceException

首先，我们将看一个以StaleElementReferenceException结束的测试：

```java
void givenDynamicPage_whenRefreshingAndAccessingSavedElement_thenSERE() {
    driver.navigate().to("https://the-internet.herokuapp.com/dynamic_content?with_content=static");
    final WebElement element = driver.findElement(LOCATOR_DYNAMIC_CONTENT);

    driver.findElement(LOCATOR_REFRESH).click();
    assertThrows(StaleElementReferenceException.class, element::getText);
}
```

该测试通过单击该页面上的链接来存储元素并更新DOM。**当重新访问不再存在的元素时，将抛出StaleElementReferenceException**。

### 2.2 刷新元素

让我们**使用重试逻辑在重新访问元素之前刷新元素**：

```java
boolean retryingFindClick(By locator) {
    boolean result = false;
    int attempts = 0;
    while (attempts < 5) {
        try {
             driver.findElement(locator).click();
             result = true;
             break;
        } catch (StaleElementReferenceException ex) {
             System.out.println(ex.getMessage());
        }
        attempts++;
    }
    return result;
}
```

每当发生StaleElementReferenceException时，我们将使用存储的元素定位器在再次执行单击之前再次定位该元素。

现在，让我们更新测试以使用新的重试逻辑：

```java
void givenDynamicPage_whenRefreshingAndAccessingSavedElement_thenHandleSERE() {
    driver.navigate().to("https://the-internet.herokuapp.com/dynamic_content?with_content=static");
    final WebElement element = driver.findElement(LOCATOR_DYNAMIC_CONTENT);

    if (!retryingFindClick(LOCATOR_REFRESH)) {
        Assertions.fail("Element is still stale after 5 attempts");
    }
    assertDoesNotThrow(() -> retryingFindGetText(LOCATOR_DYNAMIC_CONTENT));
}
```

我们看到**我们需要更新测试，如果我们需要对许多测试执行此操作，这会很麻烦**。幸运的是，我们可以在一个中心位置使用此逻辑，而无需更新所有测试。

## 3. 避免StaleElementReferenceException的通用策略

我们将为通用解决方案创建两个新类：RobustWebDriver和RobustWebElement。

### 3.1 RobustWebDriver

首先，我们需要创建一个实现WebDriver实例的新类。我们将它编写为WebDriver的包装器，它调用WebDriver方法，方法findElement和findElements将返回RobustWebElement：

```java
class RobustWebDriver implements WebDriver {

    WebDriver originalWebDriver;

    RobustWebDriver(WebDriver webDriver) {
        this.originalWebDriver = webDriver;
    }
    // ...
    @Override
    public List<WebElement> findElements(By by) {
        return originalWebDriver.findElements(by)
                .stream().map(e -> new RobustWebElement(e, by, this))
                .collect(Collectors.toList());
    }

    @Override
    public WebElement findElement(By by) {
        return new RobustWebElement(originalWebDriver.findElement(by), by, this);
    }
    // ...
}
```

### 3.2 RobustWebElement

RobustWebElement是WebElement的包装器。该类实现WebElement接口并包含重试逻辑：

```java
class RobustWebElement implements WebElement {

    WebElement originalElement;
    RobustWebDriver driver;
    By by;

    int MAX_RETRIES = 10;
    String SERE = "Element is no longer attached to the DOM";

    RobustWebElement(WebElement element, By by, RobustWebDriver driver) {
        originalElement = element;
        by = by;
        driver = driver;
    }
    // ...
}
```

我们必须实现WebElement接口的每个方法，以便在抛出StaleElementReferenceException时执行元素的刷新。为此，让我们介绍一些包含刷新逻辑的辅助方法。我们将从这些重写的方法中调用它们。

我们可以利用[函数接口](https://www.baeldung.com/java-8-functional-interfaces)并创建一个辅助类来调用WebElement的各种方法：

```java
class WebElementUtils {

    private WebElementUtils() {
    }

    static void callMethod(WebElement element, Consumer<WebElement> method) {
        method.accept(element);
    }

    static <U> void callMethod(WebElement element, BiConsumer<WebElement, U> method, U parameter) {
        method.accept(element, parameter);
    }

    static <T> T callMethodWithReturn(WebElement element, Function<WebElement, T> method) {
        return method.apply(element);
    }

    static <T, U> T callMethodWithReturn(WebElement element, BiFunction<WebElement, U, T> method, U parameter) {
        return method.apply(element, parameter);
    }
}
```

在WebElement中，我们实现了四个包含刷新逻辑的方法，并调用了之前介绍的WebElementUtils：

```java
void executeMethodWithRetries(Consumer<WebElement> method) { ... }

<T> T executeMethodWithRetries(Function<WebElement, T> method) { ... }

<U> void executeMethodWithRetriesVoid(BiConsumer<WebElement, U> method, U parameter) { ... }

<T, U> T executeMethodWithRetries(BiFunction<WebElement, U, T> method, U parameter) { ... }
```

click方法将如下所示：

```java
@Override
public void click() {
    executeMethodWithRetries(WebElement::click);
}
```

现在我们需要为测试更改的只是WebDriver实例：

```java
driver = new RobustWebDriver(new ChromeDriver(options));
```

其他一切都可以保持不变，并且StaleElementReferenceException不应该再发生。

## 4. 总结

在本教程中，我们了解到在DOM已更改且元素尚未刷新后访问元素时可能会发生StaleElementReferenceException。我们在测试中引入了重试逻辑，以便在发生StaleElementReferenceException时刷新元素。