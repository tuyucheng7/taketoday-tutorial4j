很多时候，当我们填写表格并按下提交按钮时，会弹出一个警告，告诉我“需要联系电话”。有时，我会忘记勾选条款和条件框，我会通过弹出式警报收到与通知相同的信息。按下警告框上的OK按钮将我带回表单，并且在我填写所有“必填项” 之前表单不会提交。这些用例显示了Web 应用程序中“警报”或“弹出窗口”的必要性。在本文中，

-   Selenium 中的警报/弹出窗口是什么？
    -   不同类型的警报/弹出窗口
-   Web 应用程序提供的各种警报有哪些？
    -   如何使用 Selenium WebDriver 处理警报/弹出窗口？
    -   如何使用 Selenium WebDriver 处理意外警报？

## Selenium 中的警报/弹出窗口是什么？

警报是显示消息/通知的小弹出框/窗口，并通过一些信息通知用户寻求某些类型操作的许可。此外，我们还可以将它们用于警告目的。有时，用户也可以在警告框中输入一些详细信息。

例如，下面显示的警告框需要用户执行操作以按“确定”并接受或按“取消”并关闭消息框。

![Selenium 中的警报](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Alerts%20in%20Selenium.png)

应用程序警报将你的焦点从当前浏览器窗口转移到新打开的窗口，并强制用户阅读显示的消息并采取相应行动。除非处理警告消息框，否则用户将被阻止并且无法进一步工作。

### 警报/弹出窗口有哪些不同类型？

在自动化任何 Web 应用程序时，[Selenium WebDriver](https://www.toolsqa.com/selenium-webdriver/selenium-tutorial/)可能会遇到警报，这些警报可能取决于应用程序或取决于用户正在使用的操作系统。基于这些分类，我们可以将警报主要分为以下几类：

-   Windows/OS 警报：基于窗口的警报是系统生成的警报/弹出窗口。开发人员调用操作系统 API 来显示这些警报/对话框。在 Selenium 中处理这些警报有点棘手并且超出了 WebDriver 的能力，因为 Selenium 是一种仅用于 Web 应用程序的自动化测试工具，我们需要第三方实用程序来自动化基于窗口的弹出窗口。其中一些实用程序是[AutoIT](https://www.toolsqa.com/selenium-webdriver/autoit-selenium-webdriver/)和Java 中的[Robot Class](https://www.toolsqa.com/selenium-webdriver/robot-class/)。基于示例操作系统的警报如下所示，主要称为对话框：

![操作系统警报](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Operating%20system%20alerts.png)

-   基于 Web/Javascript/浏览器的警报：基于 Web/浏览器的警报主要称为Javascript 警报，是那些依赖于浏览器的警报。这些警报主要称为 Popups。

在本教程中，我们将主要关注基于浏览器的警报，因为它们在这个网络时代更为普遍，并且与使用Selenium WebDriver的自动化兼容。

现在让我们看看我们可以在多个 Web 应用程序上看到的各种 Web 警报是什么。

## Web 应用程序提供的各种警报有哪些？

正如我们所讨论的，开发人员可以在 Web 应用程序上实现各种类型的警报。这些警报/弹出窗口中的每一个都需要不同类型的操作来处理这些警报。让我们看看 Web 应用程序提供的这些不同类型的警报是什么：

-   简单警报：这些警报只是信息性警报，上面有一个确定按钮。用户可以在阅读警告框上显示的消息后单击“确定”按钮。一个简单的警告框如下所示：

![Selenium 中的简单警报](https://www.toolsqa.com/gallery/selnium%20webdriver/3.simple%20alert%20in%20Selenium.png)

-   提示警报：在提示警报中，用户需要以文本形式在警报框中输入一些输入要求。如下所示显示提示框，用户可以在其中输入他/她的用户名并按确定按钮或取消警告框而不输入任何详细信息 。![硒中的提示警报](https://www.toolsqa.com/gallery/selnium%20webdriver/4.prompt%20alerts%20in%20selenium.png)
-   确认警报：这些警报以接受或关闭消息框的形式从用户那里得到一些确认。它们与提示警报的不同之处在于用户无法输入任何内容，因为没有可用的文本框。用户只能阅读消息并通过按“确定/取消”按钮提供输入。

![硒中的确认警报框](https://www.toolsqa.com/gallery/selnium%20webdriver/5.confirmation%20alert%20box%20in%20selenium.png)

让我们看看如何使用Selenium WebDriver处理和自动化这些警报：

### 如何使用 Selenium WebDriver 处理警报/弹出窗口？

正如我们所知，每当我们使用Selenium WebDriver执行任何自动化脚本时，WebDriver始终将焦点放在主浏览器窗口上，并且只会在主浏览器窗口上运行所有命令。但是，每当出现警报/弹出窗口时，它都会打开一个新窗口。因此，为了使用 Selenium WebDriver 处理警报，需要将焦点转移到警报打开的子窗口。为了将控件从父窗口切换到 Alert 窗口，Selenium WebDriver 提供了以下命令：

```java
driver.switchTo( ).alert( );
```

一旦我们将控件从主浏览器窗口切换到警报窗口，我们就可以使用 警报接口提供的方法 来执行各种所需的操作。例如，接受警报、解除警报、从警报窗口获取文本、在警报窗口上写一些文本等等。

为了处理Javascript 警报，Selenium WebDriver提供了包org.openqa.selenium.Alert 并公开了以下方法：

-   Void accept()：此方法单击警告框的“确定”按钮。

```java
driver.switchTo( ).alert( ).accept();
```

-   Void dismiss() ：当在警告框中单击“取消”按钮时，我们使用此方法

```java
driver.switchTo( ).alert( ).dismiss();
```

-   String getText()：此方法从警告框中捕获消息。

```java
driver.switchTo().alert().getText();
```

-   void sendKeys (String stringToSend)：此方法将数据发送到警报框。

```java
driver.switchTo().alert().sendKeys("Text");
```

### 如何使用 Selenium WebDriver 处理简单警报？

要了解我们如何使用 Selenium WebDriver 处理简单警报，请考虑以下场景：

在此场景中，我们使用[demoqa.com](https://demoqa.com/alerts)来说明简单的警报处理。

第一步：启动网站“[ https://demoqa.com/alerts](https://demoqa.com/alerts) ”。

第 2 步：单击箭头突出显示的“单击我”按钮，以查看简单的警告框。

![警报主窗口](https://www.toolsqa.com/gallery/selnium%20webdriver/6.alerts%20main%20window.png)

第 3 步：打开简单的警告框，用户必须通过点击OK 按钮接受它。

![简单的警告框](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Simple%20alert%20box.png)

下面的代码片段显示了我们如何使用Selenium WebDriver处理上述警报：

```java
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class simplealert{
 public static void main(String[] args) {
   System.setProperty("webdriver.chrome.driver","./src/resources/chromedriver");
    WebDriver driver = new ChromeDriver();
    driver.get("https://demoqa.com/alerts");
    driver.manage().window().maximize();
  // This step will result in an alert on screen
    driver.findElement(By.id("alertButton")).click();
    Alert simpleAlert = driver.switchTo().alert();
    simpleAlert.accept();
  }
}
```

在哪里，

1.  WebDriver 实例的创建发生，浏览器的启动打开了网站。
2.  引用变量是为 Alert 类创建的，它通过Alert simpleAlert = driver.switchTo().alert(); 引用警报；.
3.  将控件切换到最近打开的弹出窗口Driver.switchTo().alert(); 用来
4.  使用alert.accept() 接受警报；方法。

#### 如何使用 Selenium WebDriver 处理提示警报？

为了理解使用 Selenium WebDriver 处理“ Prompt Alert ”，请考虑以下场景：

第一步：启动网站“[ https://demoqa.com/alerts](https://demoqa.com/alerts) ”。

第 2 步：单击“单击我”按钮，如下面的屏幕截图中突出显示的那样，可以看到提示警报弹出框。

![提示框](https://www.toolsqa.com/gallery/selnium%20webdriver/8.Prompt%20alert%20box.png)

第三步：提示警告框打开，用户可以在文本框中输入文本。输入后用户可以接受或关闭警告框。

注意：在 Selenium Webdriver 中，XPath、CSS 等定位器识别并执行网页上的操作。如果定位器不起作用，我们可以使用 JavaScriptExecutor 在 Web 元素上实现所需的操作。

![提示警告框打开](https://www.toolsqa.com/gallery/selnium%20webdriver/9.prompt%20alert%20box%20opened.png)

第四步：当用户在文本框中成功输入文本并接受警告后，父窗口显示执行的操作“你输入了测试用户”。

![提示框输出窗口](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Prompt%20alert%20box%20output%20window.png)

下面的代码片段显示了一个示例代码，它解释了如何 使用Selenium WebDriver处理提示警报：

```java
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class promptalert {
 public static void main(String[] args) throws InterruptedException {
   System.setProperty("webdriver.chrome.driver","./src/resources/chromedriver");
   WebDriver driver = new ChromeDriver();
   driver.get("https://demoqa.com/alerts");
   driver.manage().window().maximize();
  // This step will result in an alert on screen
   WebElement element = driver.findElement(By.id("promtButton");
   ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
   Alert promptAlert  = driver.switchTo().alert();
   String alertText = promptAlert.getText();
   System.out.println("Alert text is " + alertText);
  //Send some text to the alert
   promptAlert.sendKeys("Test User");
   promptAlert.accept();
  }
}
```

在哪里，

1.  网站启动后，我们点击“提示按钮”，提示框打开。

2.  为了找到 WebElement，我们在这里使用了 javascript 执行器 ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);。此方法在当前选定的框架或窗口的上下文中执行 JavaScript。

3.  它将使用

    String alertText = promptAlert.getText()

    从警告框中读取文本；值以字符串格式存储。

    1.  发送密钥()；该方法在 Prompt 警报框中输入文本，然后使用alert.accept()方法接受警报。下面的屏幕显示了从提示框获取的文本。

#### 如何使用 Selenium WebDriver 处理确认警报？

为了理解使用 Selenium WebDriver 处理“ Confirmation Alert ”，请考虑以下场景：

第一步：启动网站“[ https://demoqa.com/alerts](https://demoqa.com/alerts) ”。

第 2 步：单击“单击我”按钮，如以下屏幕截图中突出显示的那样，以查看确认提示框。

![确认提示框](https://www.toolsqa.com/gallery/selnium%20webdriver/11.Confirmation%20alert%20box.png)

第三步：打开确认框后，用户可以使用Alert.accept()接受或关闭警告框；或Alert.dismiss(); 方法。

![确认提示框](https://www.toolsqa.com/gallery/selnium%20webdriver/12.confirmation%20alert%20box.png)

第 4 步：当用户接受警报时，父窗口将显示在确认警报框上执行的操作。

![确认提示框](https://www.toolsqa.com/gallery/selnium%20webdriver/13.confirmation%20alert%20box.png)

下面是代码片段，展示了如何使用Selenium WebDriver处理确认警报：

```java
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class alerts {
 public static void main(String[] args) {
  System.setProperty("webdriver.chrome.driver","./src/resources/chromedriver");
  WebDriver driver = new ChromeDriver();
  driver.get("https://demoqa.com/alerts");
  driver.manage().window().maximize();
 // This step will result in an alert on screen
  WebElement element = driver.findElement(By.id("confirmButton"));
  ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
  Alert confirmationAlert = driver.switchTo().alert();
  String alertText = confirmationAlert.getText();
  System.out.println("Alert text is " + alertText);
  confirmationAlert.accept();
 }
}
```

正如我们在这里看到的，代码的最大值是相似的；唯一的区别是用户的输入不可能像提示框那样。因此，我们不使用它。我们可以接受或关闭警告消息框。在这里，我们使用Alert.accept() 接受了警报；方法。

### 如何使用 Selenium WebDriver 处理意外警报？

有时，当我们浏览任何 Web 应用程序时，由于某些错误或其他原因会出现意外警报。这种警报不会在你每次打开网站时显示，但它们只会以随机间隔出现。如果你已经为此类页面创建了自动化测试用例并且未处理此类警报，那么如果显示此类意外警报弹出窗口，你的脚本将立即失败。

我们必须专门处理这些意外警报，为此，我们可以使用[try-catch 块](https://www.toolsqa.com/selenium-webdriver/exception-handling-selenium-webdriver/)。

注意：如果我们直接编写代码(没有 try-catch)来接受或关闭警报，并且如果警报没有出现，那么我们的测试用例将无法在 Selenium 中抛出任何异常，如 timeout [Exception](https://www.toolsqa.com/selenium-webdriver/exception-handling-selenium-webdriver/)。Try catch 块可以处理这两种情况。

以下代码演示了我们如何使用[try-catch](https://www.toolsqa.com/selenium-webdriver/exception-handling-selenium-webdriver/)块处理 Selenium 中的意外警报。

```java
import org.openqa.selenium.;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class alerts {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver","./src/resources/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://demoqa.com/alerts");

        try {
            driver.findElement(By.id("timerAlertButton")).click();
            WebDriverWait wait = new WebDriverWait(driver,10);
            wait.until(ExpectedConditions.alertIsPresent());
            Alert simpleAlert = driver.switchTo().alert();
            simpleAlert.accept();
            System.out.println("Unexpected alert accepted");
        } catch (Exception e) {
            System.out.println("unexpected alert not present");
        }
        driver.quit();
    }
}
```

让我们一步一步地理解这一点：

1.  启动浏览器并打开站点“ [https://demoqa.com/alerts](https://www.toolsqa.com/selenium-webdriver/exception-handling-selenium-webdriver/) ”。
2.  找到 WebElement“ timerAlertButton ”并单击它
3.  我们在这里使用了显式等待，因为驱动程序将等待 10 秒以查看是否出现警报。它将使用try-catch 块检查警报。我们使用显式等待作为WebDriverWait wait = new WebDriverWait ( \driver,10\ ) ; 和wait.until (ExpectedCondition.alertIsPresent())；
4.  如果存在警报，那么我们将使用driver.switchTo().alert().accept(); 接受它；方法否则它将去捕捉块并打印消息“意外警报不存在”。

## 关键要点

-   警报是显示消息/通知并向用户通知某些信息或可能请求对某些类型的操作的许可的小弹出窗口。
-   有两种类型的警报：Selenium 中基于 Web/Javascript/浏览器的警报和基于 Windows/OS 的警报。基于 Web 的警报可以进一步分为简单警报、提示警报和确认警报。
-   这些警报在在线申请表、银行网站和社交网络/电子邮件服务提供商网站(如 Gmail)中非常明显。每个 QA 在自动化被测应用程序时一定遇到过此类警报。