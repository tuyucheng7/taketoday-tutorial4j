## 1. 概述

在本教程中，我们将了解如何使用Selenium处理浏览器选项卡。在某些情况下，单击链接或按钮会在新选项卡中打开页面。在这些情况下，我们必须正确处理选项卡以继续我们的测试。本教程涵盖了在新选项卡中打开页面、在选项卡之间切换以及关闭选项卡。对于我们的示例，我们将使用[https://testpages.herokuapp.com](https://testpages.herokuapp.com/)。

## 2. 设置

基于[WebDriver设置](https://www.baeldung.com/java-selenium-webdriver-path-error)，我们将创建一个SeleniumTestBase类来处理WebDriver设置和拆卸。我们的测试类将扩展此类。我们还将定义一个工具类，用于使用Selenium处理选项卡。这个工具类将包含用于打开、切换和关闭选项卡的方法，这些方法将在以下部分中展示和解释。我们将在SeleniumTestBase中初始化该工具类的一个实例。所有示例都使用JUnit 5。

初始化是在init()方法中完成的，该方法需要用@BeforeAll标注：

```java
@BeforeAll
public static void init() {
    setupChromeDriver();
}
```

teardown或cleanup方法将在所有测试后关闭整个浏览器：

```java
@AfterAll
public static void cleanup() {
    if (driver != null) {
        driver.quit();
    }
}
```

## 3. 基础知识

### 3.1 链接目标属性

我们需要处理选项卡的典型情况是单击链接或按钮时打开新选项卡。target属性设置为_blank的网站上的链接将在新选项卡中打开，例如：

```html
<a href="https://www.baeldung.com/" target="_blank">Baeldung.com</a>
```

这样的链接会在新选项卡中打开目标，例如[https://www.baeldung.com](https://www.baeldung.com/)。

### 3.2 窗口句柄

在Selenium中，每个选项卡都有一个窗口句柄，它是一个唯一的字符串。对于Chrome选项卡，字符串以CDwindow开头，后跟32个十六进制字符，例如CDwindow-CDE9BEF919431FDAA0FC9CB7EBBD4E1A。**切换到特定选项卡时，我们需要窗口句柄**。因此，我们需要在测试期间存储窗口句柄。

## 4. 标签处理

### 4.1 打开选项卡

**使用Selenium >= 4.0，可以在不使用Javascript或浏览器热键的情况下打开新选项卡**。WebDriver提供了以下打开新标签页的方法：

```java
driver.switchTo().newWindow(WindowType.TAB);
```

如上一节所述，存在与目标属性集的链接。在这些情况下，我们不需要自己打开选项卡，但我们需要注意切换和关闭它们。虽然这样的链接会在新选项卡中打开页面，但它不会切换到新选项卡。我们需要自己处理这个问题。一种方法是比较单击链接之前的窗口句柄和单击链接之后的窗口句柄。应该有一个新窗口句柄代表新打开的选项卡。

我们可以使用WebDriver的以下方法检索窗口句柄集合和活动选项卡的窗口句柄：

```java
driver.getWindowHandles();
driver.getWindowHandle()
```

借助这些方法，我们可以在TabHelper类中实现一个工具方法。它会在新选项卡中打开一个链接并切换到该选项卡。仅当打开新选项卡时才会执行选项卡切换。

```java
String openLinkAndSwitchToNewTab(By link) {
    String windowHandle = driver.getWindowHandle();
    Set<String> windowHandlesBefore = driver.getWindowHandles();

    driver.findElement(link).click();
    Set<String> windowHandlesAfter = driver.getWindowHandles();
    windowHandlesAfter.removeAll(windowHandlesBefore);

    Optional<String> newWindowHandle = windowHandlesAfter.stream().findFirst();
    newWindowHandle.ifPresent(s -> driver.switchTo().window(s));

    return windowHandle;
}
```

查找窗口句柄是通过在单击链接之前和之后检索窗口句柄来完成的。单击前的窗口句柄集合将从单击后的窗口句柄集合中移除。结果将是新选项卡的窗口句柄或空集。该方法还返回切换之前选项卡的窗口句柄。

### 4.2 切换选项卡

每当我们打开多个选项卡时，我们需要手动在它们之间切换。我们可以使用以下语句切换到特定选项卡，其中destinationWindowHandle表示我们要切换到的选项卡的窗口句柄：

```java
driver.switchTo().window(destinationWindowHandle)
```

可以实现一个工具方法，它将目标窗口句柄作为参数并切换到相应的选项卡。此外，该方法在切换之前返回活动选项卡的窗口句柄：

```java
public String switchToTab(String destinationWindowHandle) {
    String currentWindowHandle = driver.getWindowHandle();
    driver.switchTo().window(destinationWindowHandle);
    return currentWindowHandle;
}
```

### 4.3 关闭选项卡

在我们的测试之后或者当我们不再需要某个特定的选项卡时，我们需要关闭它。Selenium提供了一个语句来关闭当前选项卡或整个浏览器(如果它是最后一个打开的选项卡)：

```java
driver.close();
```

我们可以使用此语句关闭所有不再需要的选项卡。使用以下方法，我们可以关闭除特定选项卡之外的所有选项卡：

```java
void closeAllTabsExcept(String windowHandle) {
    for (String handle : driver.getWindowHandles()) {
        if (!handle.equals(windowHandle)) {
            driver.switchTo().window(handle);
            driver.close();
        }
    }
    driver.switchTo().window(windowHandle);
}
```

在这个方法中，我们遍历所有窗口句柄。如果窗口句柄与提供的窗口句柄不同，我们将切换到它并关闭它。最后，我们将确保再次切换到我们所需的选项卡。

我们可以使用此方法关闭除当前打开的选项卡之外的所有选项卡：

```java
void closeAllTabsExceptCurrent() {
    String currentWindow = driver.getWindowHandle();
    closeAllTabsExcept(currentWindow);
}
```

现在，我们可以在每次测试后在SeleniumTestBase类中使用此方法来关闭所有剩余的选项卡。如果测试未能在开始下一个测试之前清理浏览器而不影响结果，这将特别有用。我们将在带有@AfterEach注解的方法中调用该方法：

```java
@AfterEach
public void closeTabs() {
    tabHelper.closeAllTabsExceptCurrent();
}
```

### 4.4 使用热键处理选项卡

使用Selenium，可以使用浏览器热键处理选项卡。**不幸的是，由于ChromeDriver的更改，这似乎不再有效**。

## 5. 测试

我们可以通过一些简单的测试来验证TabHelper类的选项卡处理是否按预期工作。正如本文介绍中提到的，我们的测试类需要扩展SeleniumTestBase：

```java
class SeleniumTabsLiveTest extends SeleniumTestBase {
    // ...
}
```

对于这些测试，我们在测试类中为URL和定位器声明了一些常量，如下所示：

```java
By LINK_TO_ATTRIBUTES_PAGE_XPATH = By.xpath("//a[.='Attributes in new page']");
By LINK_TO_ALERT_PAGE_XPATH = By.xpath("//a[.='Alerts In A New Window From JavaScript']");

String MAIN_PAGE_URL = "https://testpages.herokuapp.com/styled/windows-test.html";
String ATTRIBUTES_PAGE_URL = "https://testpages.herokuapp.com/styled/attributes-test.html";
String ALERT_PAGE_URL = "https://testpages.herokuapp.com/styled/alerts/alert-test.html";
```

在我们的第一个测试用例中，我们在新选项卡中打开一个链接。我们正在验证我们是否打开了两个选项卡，并且我们可以在它们之间切换。请注意，我们始终存储相应的窗口句柄。以下代码表示此测试用例：

```java
void givenOneTab_whenOpenTab_thenTwoTabsOpen() {
    driver.get(MAIN_PAGE_URL);

    String mainWindow = tabHelper.openLinkAndSwitchToNewTab(LINK_TO_ATTRIBUTES_PAGE_XPATH);
    assertEquals(ATTRIBUTES_PAGE_URL, driver.getCurrentUrl());

    tabHelper.switchToTab(mainWindow);
    assertEquals(MAIN_PAGE_URL, driver.getCurrentUrl());
    assertEquals(2, driver.getWindowHandles().size());
}
```

在我们的第二个测试中，我们想要验证是否可以关闭除第一个打开的选项卡之外的所有选项卡。我们需要提供该选项卡的窗口句柄。该测试用例的代码如下：

```java
void givenTwoTabs_whenCloseAllExceptMainTab_thenOneTabOpen() {
    driver.get(MAIN_PAGE_URL);
    String mainWindow = tabHelper.openLinkAndSwitchToNewTab(LINK_TO_ATTRIBUTES_PAGE_XPATH);
    assertEquals(ATTRIBUTES_PAGE_URL, driver.getCurrentUrl());
    assertEquals(2, driver.getWindowHandles().size());

    tabHelper.closeAllTabsExcept(mainWindow);

    assertEquals(1, driver.getWindowHandles().size());
    assertEquals(MAIN_PAGE_URL, driver.getCurrentUrl());
}
```

## 6. 总结

在本文中，我们了解了如何使用Selenium处理浏览器选项卡。我们介绍了如何使用窗口句柄区分不同的选项卡。本文介绍了打开选项卡、切换选项卡以及在我们完成后关闭它们。