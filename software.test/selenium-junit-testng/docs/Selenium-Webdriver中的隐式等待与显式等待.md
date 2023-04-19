## 1. 概述

Web应用程序测试的挑战之一是处理网页的动态特性。网页可能需要一些时间才能加载，并且元素可能只在一段时间后才会出现。因此，Selenium提供了等待机制来帮助我们等待元素出现、消失或可点击，然后再继续执行测试。

在本文中，我们将探讨等待类型之间的差异以及如何在Selenium中使用它们。我们将比较隐式等待与显式等待，并学习一些在Selenium测试中使用等待的最佳实践。

## 2. Selenium中的等待类型

Selenium提供了多种等待机制来帮助等待元素出现、消失或可点击。这些等待机制可以分为三种类型：隐式等待、显式等待和流畅等待。

对于我们的测试用例，我们将为我们将用于浏览网页的页面定位器定义一些常量：

```java
private static final By LOCATOR_ABOUT = By.xpath("//a[starts-with(., 'About')]");
private static final By LOCATOR_ABOUT_TUYUCHENG = By.xpath("//h3[normalize-space()='About Tuyucheng']");
private static final By LOCATOR_ABOUT_HEADER = By.xpath("//h1");
```

### 2.1 隐式等待

**隐式等待是一个全局设置，适用于Selenium脚本中的所有元素**。如果未找到元素，它会等待指定的时间量，然后再引发异常。我们可以为每个会话设置等待一次，以后不能更改。默认值为0。

我们可以使用以下语句将隐式等待设置为10秒。**我们应该在初始化WebDriver实例后立即设置隐式等待**：

```java
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
```

使用隐式等待，我们不需要在测试中显式等待任何内容。

以下简单测试导航到[www.tuyucheng.com](https://www.baeldung.com/selenium-implicit-explicit-wait)并通过标题菜单导航到“About”页面。正如我们所见，没有明确的等待指令，测试通过了10秒的隐式等待：

```java
void givenPage_whenNavigatingWithImplicitWait_ThenOK() {
    final String expected = "About Tuyucheng";
    driver.navigate().to("https://www.tuyucheng.com/");

    driver.findElement(LOCATOR_ABOUT).click();
    driver.findElement(LOCATOR_ABOUT_TUYUCHENG).click();

    final String actual = driver.findElement(LOCATOR_ABOUT_HEADER).getText();
    assertEquals(expected, actual);
}
```

**重要的是要注意，不设置隐式等待将导致测试失败**。

### 2.2 显式等待

**显式等待是一种更灵活的等待，它允许我们在继续测试执行之前等待特定条件得到满足**。

我们可以使用ExpectedConditions类定义条件，例如元素存在或不存在。如果在指定时间内不满足条件，则抛出异常。

WebDriver检查预期条件的轮询频率固定为500毫秒。由于未全局设置显式等待，因此我们可以对所有内容使用不同的条件和超时。

查看前面的测试用例，我们注意到如果没有隐式等待，测试将失败。我们可以调整测试并引入显式等待。

首先，我们将为测试创建一个超时为10秒的Wait实例：

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
```

现在我们可以使用这个Wait实例并使用ExpectedConditions调用until()。在这种情况下，我们可以使用ExpectedConditions.visibilityOfElementLocated(...)。

在与元素交互之前，例如单击，我们需要等待元素可见或可单击：

```java
void givenPage_whenNavigatingWithExplicitWait_thenOK() {
    final String expected = "About Tuyucheng";
    driver.navigate().to("https://www.tuyucheng.com/");

    driver.findElement(LOCATOR_ABOUT).click();
    wait.until(ExpectedConditions.visibilityOfElementLocated(LOCATOR_ABOUT_TUYUCHENG));

    driver.findElement(LOCATOR_ABOUT_TUYUCHENG).click(); 
    wait.until(ExpectedConditions.visibilityOfElementLocated(LOCATOR_ABOUT_HEADER));

    final String actual = driver.findElement(LOCATOR_ABOUT_HEADER).getText();
    assertEquals(expected, actual);
}
```

我们需要手动管理等待，但我们要灵活得多。这可以显着提高测试性能。

[ExpectedConditions](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/ui/ExpectedConditions.html)类提供了许多可用于检查预期条件的方法，例如：

-   elementToBeClickable()
-   invisibilityOf()
-   presenceOfElementLocated()
-   textToBePresentInElement()
-   visibilityOf()

### 2.3 流畅等待

**流畅等待是另一种类型的显式等待，它允许对等待机制进行更细粒度的控制**。它使我们能够定义预期条件和轮询机制以检查要满足的特定条件。

我们可以再次调整之前的测试用例并引入流畅等待。流畅等待基本上是显式等待，因此我们只需调整Wait实例即可。我们指定轮询频率：

```java
Wait<WebDriver> wait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(TIMEOUT))
    .pollingEvery(Duration.ofMillis(POLL_FREQUENCY));
```

测试本身将与显式等待保持相同：

```java
void givenPage_whenNavigatingWithFluentWait_thenOK() {
    final String expected = "About Tuyucheng";
    driver.navigate().to("https://www.tuyucheng.com/");

    driver.findElement(LOCATOR_ABOUT).click();
    wait.until(ExpectedConditions.visibilityOfElementLocated(LOCATOR_ABOUT_TUYUCHENG));

    driver.findElement(LOCATOR_ABOUT_TUYUCHENG).click();
    wait.until(ExpectedConditions.visibilityOfElementLocated(LOCATOR_ABOUT_HEADER));

    final String actual = driver.findElement(LOCATOR_ABOUT_HEADER).getText();
    assertEquals(expected, actual);
}
```

## 3. 隐式等待与显式等待

隐式等待和显式等待用于等待元素出现在页面上。但是，它们之间存在一些差异：

-   超时：隐式等待为整个测试运行时设置默认超时，而显式等待为特定条件设置超时。
-   条件：隐式等待等待元素出现在页面上，而显式等待等待特定条件，例如元素的存在性或元素是否可点击。
-   范围：隐式等待适用于全局，而显式等待适用于局部特定元素。
-   异常：当WebDriver在指定的超时时间内找不到元素时，隐式等待会抛出NoSuchElementException。相反，当元素在指定的超时时间内不满足条件时，显式等待会抛出TimeoutException。

当我们想要等待一定时间让元素出现在页面上时，隐式等待很有用。但是，如果我们需要等待特定条件，显式等待是更好的选择。

**根据[Selenium文档](https://www.selenium.dev/documentation/webdriver/waits/#:~:text=Warning%3A)，我们不应该混合使用它们**：

>   警告：不要混合使用隐式和显式等待。这样做可能会导致不可预测的等待时间。例如，设置10秒的隐式等待和15秒的显式等待可能会导致20秒后发生超时。

## 4. 最佳实践

在Selenium中使用等待时，需要牢记一些最佳实践：

-   始终使用等待：等待元素加载是自动化测试中的关键步骤。
-   使用显式等待而不是隐式等待：如果无法找到元素，隐式等待可能会导致我们的测试花费比失败所需的时间更长的时间。显示和流畅的等待更好，因为它们允许我们在继续进行之前等待特定条件的发生。
-   必要时使用流畅等待：当我们需要反复验证特定条件直到它变为真时，我们应该使用流畅等待，因为它们提供了对等待机制的更大控制。
-   使用合理的等待时间：等待时间太短可能会导致漏报，而等待时间太长可能会不必要地增加测试的总执行时间。
-   使用ExpectedConditions：使用显式或流畅等待时，必须使用ExpectedConditions类来定义我们要等待的条件。通过这样做，我们可以确保我们的测试等待适当的条件，并在不满足条件时迅速失败。

## 5. 陈旧元素引用异常

[StaleElementReferenceException](https://www.selenium.dev/documentation/webdriver/troubleshooting/errors/#stale-element-reference-exception)是当先前定位的元素不再可用时发生的问题。

这可能发生在我们定位元素后DOM发生变化时，例如，在等待元素的指定条件为真时。

**为了解决StaleElementReferenceException，我们需要在发生此异常时重新定位元素**。每次出现SERE(StaleElementReferenceException)时，我们都会捕获异常，重新定位元素并再次重试该步骤。由于我们无法确定重新加载是否可以解决问题，因此我们需要在几次尝试后停止重试：

```java
boolean stale = true;
int retries = 0;
while(stale && retries < 5) {
    try {
        element.click();
        stale = false;
    } catch (StaleElementReferenceException ex) {
        retries++;
    }
}
if (stale) {
    throw new Exception("Element is still stale after 5 attempts");
}
```

由于此解决方案在到处实施时不方便，我们可以[包装WebElement和WebDriver](https://stackoverflow.com/a/57513438/7450414)并在内部处理StaleElementReferenceException。

使用这种方法，我们不需要在测试代码中处理StaleElementReferenceExceptions。我们必须包装WebDriver才能使用包装的WebElements。WebElement中可以抛出StaleElementReferenceException的所有方法都可以进行调整。

## 6. 总结

在本教程中，我们了解到等待是在Selenium中编写有效和高效测试的重要组成部分。

正确使用等待可以防止潜在的计时问题，确保元素在与它们交互之前加载，并降低测试失败的可能性。

**与隐式等待相比，显式等待提供了对等待机制的更多控制**。当我们需要更细粒度的控制时，我们可以使用流畅等待以指定频率检查特定条件，直到WebElement满足指定条件或发生超时。

通过遵循最佳实践，我们可以显著提高自动化测试的效率和可靠性。