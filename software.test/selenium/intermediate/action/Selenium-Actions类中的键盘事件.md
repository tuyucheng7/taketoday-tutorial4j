在有关Selenium 中的[Actions 类的文章中，](https://www.toolsqa.com/selenium-webdriver/actions-class-in-selenium/)我们讨论了 Selenium 提供了一种功能来处理可以模拟用户手势的键盘事件。例如，假设你在谷歌搜索引擎上搜索“印度” 。为此，你将键入“India”并按 Enter 键，以便你看到结果查询。比如说，你想使用 Selenium 测试自动执行此 google 搜索，你将如何使用 selenium 代码模拟按下 ENTER 键？提供模拟此类键盘操作的能力是通过使用 Selenium WebDriver的Actions 类生成的键盘事件。在本教程中，我们将详细介绍 Selenium 支持的所有键盘事件。此外，我们还将了解 Actions 类如何满足在 Selenium 中模拟键盘事件的需要。

-   Selenium 中的键盘事件是什么？
-   为什么需要 Actions 类来使用 Selenium WebDriver 执行键盘操作？
-   Selenium WebDriver 中的 Actions 类是什么？
-   Actions 类为键盘事件提供了哪些不同的方法？
-   如何使用 Actions 类处理连续的键盘操作？

## Selenium 中的键盘事件是什么？

键盘事件描述了用户与键盘的交互。当用户按下单个或多个键时，将生成键盘事件。Selenium 提供了多种方法来自动执行这些键盘事件，其中一些是：

-   [使用 WebElement 类的 sendKeys() 方法自动执行键盘事件。](https://www.toolsqa.com/selenium-webdriver/webelement-commands/)
-   [使用 Robot 类自动化键盘事件。](https://www.toolsqa.com/selenium-webdriver/robot-class-keyboard-events/)
-   并使用 Actions 类自动化键盘事件。

我们已经在相应链接给出的文章中讨论了使用WebDriver 的 WebElement 类和“Robot 类”的“sendKeys()”方法处理键盘事件的前两种方法。在本文中，我们将具体介绍Selenium WebDriver中“Actions”类的细节。在深入理解“Actions”类的概念之前，让我们先了解一下为什么需要专门的“Actions”类来处理那些键盘事件？

### 为什么需要 Actions 类来使用 Selenium WebDriver 执行键盘操作？

当我们与 Web 应用程序交互时，用户执行以下操作时会出现各种场景：

-   输入大写/驼峰字母：只要用户需要输入大写的单词或字母，他/她将按下“SHIFT”键并键入必要的字符，以及按下“SHIFT”键时将键入的任何字符，将键入大写字母。
-   复制和粘贴文本：当我们需要将一些文本从一个文本框复制到另一个文本框时，我们通过按“CTRL+A”选择文本，他们使用“CTRL+C”复制文本并将文本粘贴到新的文本框中只需单击文本框并按下“CTRL+V”键即可。

这些是非常常见的用户操作，我们几乎每天都执行这些操作。现在，正如我们所讨论的，Selenium WebDriver提供了两种将任何键盘事件发送到 Web 元素的方法：

-   WebElement 类的 sendKeys() 方法。
-   动作类

现在让我们尝试详细了解一下，如果我们想使用WebElement类的sendKeys()方法自动执行上面提到的以大写形式键入字母(按下 SHIFT 键)的场景。

考虑以下场景以快速了解行为：

-   首先，导航至[“https://demoqa.com/text-box”。](https://demoqa.com/text-box)
-   其次，输入全名：“ Mr.Peter Haynes ”。
-   第三，输入电子邮件：“ PeterHaynes@toolsqa.com。”
-   之后，输入当前地址：“43 School Lane London EC71 9GO”。
-   第五，单击当前地址文本框并复制当前地址。
-   之后，将复制的地址粘贴到永久地址文本框中。
-   最后，验证 Current Address 和 Permanent Address 中的文本是否相同。

让我们尝试使用WebElement类的sendKeys()方法自动执行上述场景：

```java
package automationFramework;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class KeyboardEvents {

	public static void main(String[] args) {
		
        // Initialize ChromeDriver
	// Here we assume that the ChromeDriver path has been set in the System Global variables
        WebDriver driver=new ChromeDriver();
       
        //Navigate to the demo site
        driver.get("https://demoqa.com/text-box");
       
        // Enter the Full Name
        WebElement fullName = driver.findElement(By.id("userName"));
        fullName.sendKeys("Mr.Peter Haynes");
        
        //Enter the Email
        WebElement email=driver.findElement(By.id("userEmail"));
        email.sendKeys("PeterHaynes@toolsqa.com");
        
        // Enter the Current Address
        WebElement currentAddress=driver.findElement(By.id("currentAddress"));
        currentAddress.sendKeys("43 School Lane London EC71 9GO");
        
        // Copy the Current Address
        currentAddress.sendKeys(Keys.CONTROL);
        currentAddress.sendKeys("A");
        currentAddress.sendKeys(Keys.CONTROL);
        currentAddress.sendKeys("C");
        
        //Press the TAB Key to Switch Focus to Permanent Address
        currentAddress.sendKeys(Keys.TAB);
        
        //Paste the Address in the Permanent Address field
        WebElement permanentAddress=driver.findElement(By.id("permanentAddress"));
        permanentAddress.sendKeys(Keys.CONTROL);
        permanentAddress.sendKeys("V");
        
        //Compare Text of current Address and Permanent Address
        assertEquals(currentAddress.getAttribute("value"),permanentAddress.getAttribute("value"));
        
        driver.close();

	}
}
```

在上面的代码片段中，虽然WebElement的sendKeys()方法允许使用Control Key，但它不能执行复制和粘贴操作，因为它无法组合键序列。

从下面的截图我们可以看出，Permanent Address 文本字段中没有粘贴 Current Address 的内容，而是粘贴了字符“V”。

![WebElement 类的 sendKeys 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/1.sendKeys%20method%20of%20WebElement%20class.png)

从上面的截图可以看出，Current Address 文本域的文本并没有复制到 Permanent Address 文本域中，所以在比较这两个域的文本时会导致断言失败。因此，当我们执行上述测试时，它会失败并显示以下错误消息：

![使用 WebElement 类的 sendKeys 方法未成功复制文本时的 Selenium 错误消息](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Selenium%20Error%20message%20when%20text%20is%20not%20copied%20successfully%20using%20sendKeys%20method%20of%20WebElement%20Class.png)

因此，这就是WebElement类的sendKeys() 方法失败的地方。换句话说，当我们需要将特殊键(如“SHIFT”、“CONTROL”等)与不同的键序列组合时，它会失败，我们都知道，当我们作为用户使用任何网络应用程序。

因此，这就是Selenium WebDriver 的 Actions 类发挥作用的地方，它提供了各种方法来专门处理这些元键盘键的操作，这些元键盘键需要在对其他键盘键进行操作时按下。让我们看看如何使用Selenium WebDriver 的 Actions 类来处理此类键盘操作。

## Selenium WebDriver 中的 Actions 类是什么？

正如我们上面所讨论的，Selenium WebDriver提供了一个名为“Actions”的类，它提供了各种有助于自动化和模拟键盘和鼠标操作的方法。下图显示了Selenium Web Driver提供的详尽方法列表，突出显示的是最常用的模拟键盘操作的方法：

![Selenium WebDriver 中 Actions 类提供的键盘事件方法](https://www.toolsqa.com/gallery/selnium%20webdriver/3.Methods%20for%20keyboard%20events%20provided%20by%20Actions%20Class%20in%20Selenium%20WebDriver.png)

让我们了解Actions类提供的特定于键盘的方法：

### Actions 类为键盘事件提供了哪些不同的方法？

如上图所示，Actions类主要提供以下三种模拟键盘事件的方法：

1.  sendKeys()：此方法将一系列击键发送到给定的 Web 元素。该方法有两种重载格式：

-   sendKeys (CharSequence...KeysToSend)：以下屏幕截图显示了此方法的语法细节：

![键盘事件：Selenium WebDriver 的 Actions 类中的 sendKeys() 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/4..keyboard%20events%20sendKeys()%20method%20in%20Actions%20class%20of%20Selenium%20WebDriver.png)

此方法将一系列键发送到当前聚焦的 Web 元素，即，如果我们要将特定字符发送到 Web 元素，则必须首先聚焦该元素，然后才会将提到的字符发送到该 Web 元素。

-   sendKeys (WebElement element, CharSequence...KeysToSend)：下面的截图显示了这个方法的语法细节：

![重载 Actions 类的 sendKeys 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/5.Overloaded%20sendKeys%20method%20of%20Actions%20Class.png)

sendKeys() 方法的此实现将一系列字符/键发送到特定的 Web 元素，该元素作为第一个参数传递给该方法。此方法首先关注目标网络元素，然后执行与 sendKeys(CharSequence keys) 相同的操作。

1.  keyDown()：此方法模拟需要按下特定键盘键时的键盘动作。因此，每当你需要按下一个键然后执行特定的其他操作时，我们可以使用 keyDown() 方法来保持按下该键。例如，假设用户必须在 Capital 中键入一些字符。然后模拟用户行为，用户按下SHIFT 键，然后按下需要输入大写字母的字符集。此方法也可用于以下两个重载变体：

-   keyDown(CharSequence 键)：以下屏幕截图显示了此方法的语法细节：

![Selenium WebDriver 中 Actions 类的键盘事件 keyDown 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/6.keyboard%20events%20keyDown%20method%20of%20Actions%20class%20in%20Selenium%20WebDriver.png)

此方法在当前获得焦点的 Web 元素上按下指定的键。该方法一般按下SHIFT、CTRL等“修饰键”，如果要在指定的网页元素上按下键盘按键，则需要先显式聚焦该网页元素，然后调用该方法.

-   keyDown(WebElement 元素，CharSequence 键)：以下屏幕截图显示了此方法的语法细节：

![Selenium WebDriver 中 Actions 类的重载 keyDown 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/7.Overloaded%20keyDown%20method%20of%20Actions%20class%20in%20Selenium%20WebDriver.png)

此方法首先关注 Web 元素，该元素已作为参数传递给该方法并按下该 Web 元素上提到的键。

1.  keyUp()：我们主要将此方法与keyDown()方法配合使用。使用keyDown()方法按下的键盘键不会自动释放，因此需要使用keyUp()方法显式释放。因此，与keyDown()方法类似，此方法有两个重载变体：

-   keyUp(CharSequence 键)：以下屏幕截图显示了此方法的语法细节：

![Selenium WebDriver 中 Actions 类的键盘事件 keyUp 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/8.keyboard%20events%20keyUp%20method%20of%20Actions%20class%20in%20Selenium%20WebDriver.png)

此方法释放当前获得焦点的 Web 元素上的指定键。如果要在指定的网页元素上释放键盘按键，那么首先需要显式聚焦该网页元素，然后需要调用此方法。

-   keyUp(WebElement 元素，CharSequence 键)：以下屏幕截图显示了此方法的语法细节：

![Selenium WebDriver 中 Actions 类的重载 keyUp 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/9.Overloaded%20keyUp%20method%20of%20Actions%20class%20in%20Selenium%20WebDriver.png)

此方法首先关注 Web 元素，该元素作为参数传递给该方法。然后，它释放该 Web 元素上提到的密钥。

总而言之，我们清楚了Actions类提供的所有键盘特定方法。随后，让我们看看如何使用Selenium WebDriver 的 Actions 类提供的方法来自动化上述场景中的用户。

让我们修改上面编写的代码片段以使用 Actions 类的方法，而不是使用WebElement类的方法：

```java
package automationFramework;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

public class KeyboardEventsUsingActions {

	public static void main(String[] args) {
		
        // Initialize ChromeDriver
		// Here we assume that the ChromeDriver path has been set in the System Global variables
        WebDriver driver=new ChromeDriver();
       
        //Navigate to the demo site
        driver.get("https://demoqa.com/text-box");
        
        //Create object of the Actions class
        Actions actions = new Actions(driver);
       
        
        // Enter the Full Name
        WebElement fullName = driver.findElement(By.id("userName"));
        fullName.sendKeys("Mr.Peter Haynes");
        
        //Enter the Email
        WebElement email=driver.findElement(By.id("userEmail"));
        email.sendKeys("PeterHaynes@toolsqa.com");
        
        
        // Enter the Current Address
        WebElement currentAddress=driver.findElement(By.id("currentAddress"));
        
        currentAddress.sendKeys("43 School Lane London EC71 9GO");
        
        
        // Select the Current Address using CTRL + A
        actions.keyDown(Keys.CONTROL);
        actions.sendKeys("a");
        actions.keyUp(Keys.CONTROL);
        actions.build().perform();
        
        // Copy the Current Address using CTRL + C
        actions.keyDown(Keys.CONTROL);
        actions.sendKeys("c");
        actions.keyUp(Keys.CONTROL);
        actions.build().perform();
        
        //Press the TAB Key to Switch Focus to Permanent Address
        actions.sendKeys(Keys.TAB);
        actions.build().perform();
        
        //Paste the Address in the Permanent Address field using CTRL + V
        actions.keyDown(Keys.CONTROL);
        actions.sendKeys("v");
        actions.keyUp(Keys.CONTROL);
        actions.build().perform();
        
        
        //Compare Text of current Address and Permanent Address
        WebElement permanentAddress=driver.findElement(By.id("permanentAddress"));
        assertEquals(currentAddress.getAttribute("value"),permanentAddress.getAttribute("value"));
        
        
        driver.close();

	}

}
```

当我们运行上面的代码片段时，我们会得到一个示例输出，如下所示：

![在 Selenium WebDriver 的 Actions 类中使用键盘事件复制粘贴场景](https://www.toolsqa.com/gallery/selnium%20webdriver/10.Copy%20Paste%20Scenario%20using%20keyboard%20events%20in%20Actions%20class%20of%20Selenium%20WebDriver.png)

正如我们在上面的屏幕截图中看到的，地址从“当前地址”字段复制到“永久地址”字段是成功的。还有一点，我们应该明确关注上面的代码：

-   无论我们使用keyDown()方法按下哪个META键(例如上述用例中的CONTROL ) ，都必须使用keyUp()方法将其释放。否则，它将保持按下状态并可能对下一行代码产生副作用。
-   当我们调用“build()”和“perform()”方法时， “Actions”类的所有命令都会执行/执行它们的操作。因此，每个预期的操作/命令都应遵循这些方法。
-   以上代码只适用于Windows平台，因为CTRL+C等只是Windows特有的操作。当我们需要在其他平台上运行相同的程序时，我们可以更新特定于平台的密钥。

## 如何使用 Actions 类处理连续的键盘操作？

如上一节所述，上面介绍的Actions类的所有方法仅返回 Actions 类的对象。[因此，这给了我们使用“方法链接”](https://stackoverflow.com/questions/2872222/how-to-do-method-chaining-in-java-o-m1-m2-m3-m4)的灵活性，我们可以在一行代码中将特定于一个操作的所有方法调用组合在一起。

让我们修改上面编写的代码以进一步减少它。我们将使用方法链，一次性 处理各种连续的键盘操作：

```java
package automationFramework;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

public class KeyboardEventsUsingActions {

	public static void main(String[] args) {
		
        // Initialize ChromeDriver
		// Here we assume that the ChromeDriver path has been set in the System Global variables
        WebDriver driver=new ChromeDriver();
       
        //Navigate to the demo site
        driver.get("https://demoqa.com/text-box");
        
        //Create object of the Actions class
        Actions actions = new Actions(driver);
       
        
        // Enter the Full Name
        WebElement fullName = driver.findElement(By.id("userName"));
        fullName.sendKeys("Mr.Peter Haynes");
        
        //Enter the Email
        WebElement email=driver.findElement(By.id("userEmail"));
        email.sendKeys("PeterHaynes@toolsqa.com");
        
        
        // Enter the Current Address
        WebElement currentAddress=driver.findElement(By.id("currentAddress"));
        
        currentAddress.sendKeys("43 School Lane London EC71 9GO");
        
        
        // Select the Current Address
        actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).build().perform();
        
        // Copy the Current Address
        actions.keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL).build().perform();
        
        //Press the TAB Key to Switch Focus to Permanent Address
        actions.sendKeys(Keys.TAB).build().perform();
        
        //Paste the Address in the Permanent Address field
        actions.keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).build().perform();
        
        
        //Compare Text of current Address and Permanent Address
        WebElement permanentAddress=driver.findElement(By.id("permanentAddress"));
        assertEquals(currentAddress.getAttribute("value"),permanentAddress.getAttribute("value"));
        
        
        driver.close();

	}

}
```

上面的代码片段将执行与上一节中介绍的功能完全相同的功能。两者之间的唯一区别是这段代码看起来更紧凑，更易于阅读。因此，通过这种方式，我们可以组合 Actions 类的各种方法。此外，我们可以模拟不同键盘操作的用户行为。

## 关键要点

-   键盘事件是任何键盘键生成的事件。
-   此外，它们是在使用 Selenium WebDriver 自动化 Web 应用程序时模拟用户行为所必需的
-   Selenium WebDriver 的 Actions 类提供 - sendKeys(),keyUp(),keyDown() 方法来处理各种键盘操作
-   修饰键永远不会在 keyDown() 方法之后隐式释放——我们应该调用 keyUp(theKey) 或 sendKeys(Keys.NULL) 来释放修饰符。