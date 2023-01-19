Selenium 一直是最可靠的 Web 应用程序测试工具之一，并在同类产品中[占据最大的市场份额。](https://enlyft.com/tech/products/selenium)它的开发人员一直热衷于测试人员的反馈(由于开源)并在每次更新时实现新功能。其中一项功能是 Selenium Waits 或也称为 Selenium Wait 命令。正如它的名字已经提供了一个微妙的印记，Selenium Wait 命令让自动化执行在继续正常执行之前等待一段时间。在我看来，它确实让我们的执行看起来有点“人性化”或类似于最终用户在他们的互联网浏览过程中会面临的情况。

Selenium Wait 章节详细介绍了实现不同类型等待的不同命令，即我们如何实现暂停进程直到我们指定的时间。例如，如果我想每次都等待每个元素怎么办？或者如果我只想等待标题中包含特定单词怎么办？随着我们逐渐进入帖子，所有的答案都会慢慢浮出水面。因此，为了让你简要了解内容，帖子将按以下顺序移动：

-   Selenium 中的 Wait 是什么？
-   Selenium 等待的类型
    -   Selenium 中的隐式等待
        -   隐式等待()
        -   页面加载超时()
        -   setScriptTimeout()
    -   Selenium 中的显式等待
        -   WebDriver等待
        -   流利等待

## Selenium 中的 Wait 是什么？

简单来说，Selenium Wait 就是一组等待指定时间后才对元素执行测试脚本的命令。何时等待以及等待多长时间取决于编写的脚本和使用的等待类型。你可能正在等待元素加载或变得可见，或者你可能想等到整个页面加载完毕。因此，Selenium Wait 是在网页上遇到或预期会出现此类情况(通常是由于使用异步属性)时的首选方法。它有助于防止NoSuchElementFound异常。

从技术上讲，Selenium Wait 命令可以防止网页元素之间的竞争条件。产生Selenium Wait 的核心问题是Selenium 只是等待页面的document.readyState 变为“完成”。在这个过程中，由于大量使用JS，即使状态变为“完成”后，仍有大量元素累加。这可以在网页上尚不可用的元素上执行脚本。

## Selenium 等待的类型

Selenium 等待命令有两个部分：

-   隐式等待
-   显式等待

这两个部分都有自己的相关性和用例，在这些情况下，自动化测试中的测试人员更喜欢它们。在随后的部分中，我们将讨论这两个部分及其权限下的各种命令。

### Selenium 中的隐式等待

隐式等待是 Selenium 中的无条件等待命令。由于它是无条件的，因此它适用于网页上的所有网页元素。

这意味着我们可以告诉 Selenium 我们希望它在抛出无法在页面上找到元素或页面未加载或 javascript 执行未完成的异常之前等待一定时间。另外，需要注意的是，一旦设置，隐式等待将在浏览器打开的整个过程中保持不变。

我们可以通过三个函数应用隐式等待：

-   隐式等待()
-   页面加载超时()
-   setScriptTimeout()

尽管它们是三个独立的可调用函数，但它们都是 Selenium 中超时的一部分。

#### Selenium 超时中的 implicitlyWait 命令

在 implicitlyWait 期间，WebDriver 将在尝试查找任何元素时轮询DOM 以获取某些指定的时间单位。如果该元素较早找到，则测试会在该点执行，否则 WebDriver 将等待指定的持续时间。

轮询时间(或轮询间隔)是 Selenium 在上次尝试失败后再次开始搜索的时间间隔。这取决于你正在使用的浏览器驱动程序的类型。有些可能有 500 毫秒，而有些可能有 1 秒作为轮询时间。轮询时间是inbuild in implicitlyWait，没有办法修改时间间隔。但在 Fluent 等待中，你可以指定和覆盖轮询间隔。

要在测试脚本中添加隐式等待，请导入以下包。

```java
import java.util.concurrent.TimeUnit;
```

句法

```java
driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
```

implicitlyWait 命令等待元素加载指定的持续时间。

```java
WebDriver driver = new FirefoxDriver();
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
driver.get("https://somedomain/url_that_delays_loading");
WebElement myDynamicElement = driver.findElement(By.id("myDynamicElement"))
```

此处，测试人员已指定等待 10 秒(通过 Duration.ofSeconds(10))，然后继续与元素进行交互。但是，测试人员也可以应用其他时间单位，例如 IntelliJ 中的自动填充选项所示：

![隐式等待硒](https://www.toolsqa.com/gallery/selnium%20webdriver/1.implicitlyWait_Selenium.jpg)

隐式等待的默认值为 0。

请遵循我们的[Selenium 初学者系列](https://toolsqa.com/selenium-webdriver/selenium-tutorial/)的其余代码，该系列详细解释了这些功能。

#### Selenium 超时中的 pageLoadTimeout 命令

顾名思义，pageLoadTimeout命令等待页面完全加载指定的秒数。默认值为 0，负值表示无限等待。

```java
WebDriver driver = new FirefoxDriver();
driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
driver.get("https://www.google.com/");
```

此处，测试人员已指定等待 30 秒，然后再继续与元素进行交互。因此，WebDriver 最多等待 30 秒。当你希望测试页面加载性能是否在限制范围内时，pageLoadTimeout 是一个不错的选择。例如，我们可以在这里使用网络节流和降低带宽来检查页面加载时间。在相同的场景下使用上面的代码，我们可以知道我们的页面是否可以在 3G 网络或 2G 网络等上加载。

#### Selenium 超时中的 setScriptTimeout 命令

setScriptTimeout 命令等待网页的异步部分完成加载指定的秒数。

```java
driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
((JavascriptExecutor) driver).executeScript("alert('hello world');");
((JavascriptExecutor) driver).executeAsyncScript("window.setTimeout(arguments[arguments.length - 1], 500);");
```

此处，测试人员已指定等待 15 秒，然后继续与元素进行交互。

要使用的隐式等待命令的类型取决于测试人员，并且决定受测试脚本和目标元素特性的影响。通过对 Selenium 中的隐式等待的介绍，重要的是要注意它确实带来了一些缺点和一个重要的警告。

隐式等待不建议与测试脚本中的显式等待混淆。由于错误的超时持续时间，具有这两种等待的测试脚本可能会产生不可预测的行为。

除了上述警告之外，建议仅在你完全控制脚本并且极其需要使用这种类型的等待时才使用隐式等待。由于隐式等待应用于 Webdriver 的生命周期，它可以将测试执行时间延长到一个很大的值，具体取决于调用它的元素的数量。话虽如此，如果测试人员知道他们的脚本，就可以不用担心地应用隐式等待。

### Selenium 中的显式等待

显式等待是 Selenium 中的一种条件等待策略，换句话说，你等待直到你指定的条件变为真或持续时间结束。由于显式等待与条件一起工作，因此它们有助于同步浏览器、文档对象模型和测试执行脚本。因此，整体执行结果令人满意且有时限。显式等待提供以下使用条件：

-   alertIsPresent()
-   elementSelectionStateToBe()
-   elementToBeClickable()
-   elementToBeSelected()
-   frameToBeAvaliableAndSwitchToIt()
-   invisibilityOfTheElementLocated()
-   invisibilityOfElementWithText()
-   presenceOfAllElementsLocatedBy()
-   presenceOfElementLocated()
-   textToBePresentInElement()
-   textToBePresentInElementLocated()
-   textToBePresentInElementValue()
-   titleIs()
-   标题包含()
-   可见性()
-   visibilityOfAllElements()
-   visibilityOfAllElementsLocatedBy()
-   visibilityOfElementLocated()

上述条件属于两种类型的显式等待命令：

-   WebDriver等待
-   流利等待

让我们看看他们每个人在测试执行中有什么相关性。

#### Selenium 中的 WebDriverWait 命令

WebDriverWait 指定 WebDriver 需要等待的条件和时间。实际上，WebDriverWait 和显式等待是同义词，因为它们的定义和用法完全匹配。因此，如果有人要求你编写一些明确的等待脚本，可以安全地假设所需的脚本需要 WebDriverWait。

```java
WebElement firstResult = new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.elementToBeClickable(By.xpath("//a/h3")));
```

这里，测试人员通过 Duration.ofSeconds(10) 指定等待时间为 10 秒，使用的条件是 elementToBeClickable。因此，上面的代码总结为 - “WebDriver 将等待最多 10 秒，直到[XPath](https://toolsqa.com/selenium-webdriver/xpath-in-selenium/)定义的元素变为可点击。所有这些命令将作为我们在下一章讨论显式等待时的核心主题.

#### 在 Selenium 中流畅地等待

流畅等待类似于 Selenium 中的显式等待，但有一个额外的频率参数(也称为轮询时间)。频率数告诉 WebDriver 定期检查元素并等到"Duration.ofSeconds"的最大值。这节省了执行时间。如果该元素更早可用，我们可以继续执行并快速完成。

```java
Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
  .withTimeout(Duration.ofSeconds(30))
  .pollingEvery(Duration.ofSeconds(5))
  .ignoring(NoSuchElementException.class);
```

在上面的代码中，最大允许等待时间指定为 30 秒(Duration.ofSeconds(30))并且轮询时间为 5 秒(pollingEvery(Duration.ofSeconds(5)))。通过这种安排，WebDriver 将每 5 秒检查一次元素，最长持续 30 秒。

在代码的最后一行，请注意另一个函数“ignoring”。这是测试人员编写的自定义配置，不是每次都需要。“忽略”的意义在于忽略某些可能阻碍测试执行过程的异常。在此代码中，NoSuchElementException是被忽略的异常。

Selenium 中的 FluentWait 是我们处理 AJAX 元素时的一个重要类。当用户在较慢的网络上浏览网站时，加载这些元素可能需要比他们在实验室测试中花费更多的时间。为了安全起见，FluentWait 帮助我们模拟此类场景并生成最优质的 Web 应用程序。

### 关键要点

以下是我们关于 Selenium Wait 的帖子的主要内容：

-   Selenium wait 是一个概念，它告诉 Selenium 等待某个指定时间或直到元素可见/已加载/启用。
-   Selenium 等待分为隐式等待和显式等待。
-   隐式等待指定等待 WebDriver 生命周期的时间，适用于每个元素，即完成一次。然而，显式等待取决于条件。
-   显式等待取决于指定的条件或允许等待的最长持续时间。

## 常见问题

### 显式等待比隐式等待好吗？

显式等待的核心本质是节省时间，并不适用于所有元素。这给人的印象是显式等待更好，更适合测试创建。然而，没有这样的证据，每种类型的等待都有其自身的相关性。

### 什么时候使用隐式等待，什么时候使用显式等待？

隐式等待对所有元素应用一次，一旦前一个等待周期完成，下一个等待周期就会开始。如果这种情况符合你的要求，隐式等待命令在这种情况下是最好的。例如，如果你想在整个页面加载后才执行任务，你可以继续使用pageLoadTimeout。但是无论如何都应该应用这种等待。

显式等待本质上是有条件的。如果你只想根据条件等待少数 select 元素，显式等待是可行的方法。但是，在没有充分理解脚本的情况下使用隐式等待可能会导致脚本完成的时间过长。

### 隐式等待中的轮询是什么？

隐式等待一直进行到找到元素或已过最长时间。但是对于回退，Selenium 中的隐式等待带有一个称为轮询的功能。通过轮询，隐式等待会以固定的时间间隔持续搜索元素，直到它找到元素或最大时间已过。

### 哪种类型的 Selenium 等待最好？

在测试 Web 应用程序时，没有“最佳”类型的 Selenium 等待。不同的用例需要不同类型的等待条件，因此需要适当的命令。

## 练习

你可以继续进行以下有关 Selenium Wait 的练习，以加强你的基础知识。

-   启动一个新的浏览器(例如 ChromeDriver)。
-   打开 URL “https://demoqa.com/dynamic-properties”。
-   最大化窗口。
-   查找 ID 为“visibleAfter”的元素。此元素需要 5 秒来加载，这意味着此元素将在 5 秒后出现。
-   使用 implicitlyWait 等待该元素。

```java
package org.example;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ImplicitWait {

    protected WebDriver driver;
    @Test
    public void implicitWaitExcercise()
    {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // launch Chrome and redirect it to the URL
       	driver.get("https://demoqa.com/dynamic-properties");

        //This element will appear after 5 secs
	driver.findElement(By.id("visibleAfter")).click();

        //close browser
        driver.close();
    }
}
```

你会注意到，即使该元素需要 5 秒才能出现，selenium 脚本仍然会通过。因为隐式等待，因为它会自动等待元素出现。