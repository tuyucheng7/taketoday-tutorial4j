在本教程中，我们将了解 Selenium 中的 Actions 类。

大多数用户交互，如单击按钮、在文本框中输入文本，都可以使用[WebDriver 元素命令来完成。](https://www.toolsqa.com/selenium-webdriver/webelement-commands/)例如，WebElement.click()和WebElement.sendKeys() 等命令用于单击按钮并在文本框中输入文本。可以使用WebElement.submit()命令提交表单。甚至可以使用Select类启用与下拉列表的交互，该类公开诸如selectByValue()、deselectAll()之类的命令来选择和取消选择选项。

但是，有些复杂的交互，如拖放和双击，无法通过简单的WebElement命令完成。为了处理这些类型的高级操作，我们在 Selenium 中使用了Actions类。

在本教程中，我们将介绍 Actions 类及其用法。

## Selenium 中的 Actions 类是什么？

让我们首先看一下智能感知为Actions类显示的信息。你可以通过将鼠标悬停在任何 IDE 中的Actions类上来看到这一点，应该会打开一个弹出菜单，如下所示。

![Selenium 动作类](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Actions-Class-of-Selenium.png)

用于模拟复杂用户手势的面向用户的 API。使用此类而不是直接使用键盘或鼠标。

实现构建器模式：构建一个包含方法调用指定的所有操作的 CompositeAction。

类描述清楚地表明我们可以使用Actions类执行复杂的用户交互。

## 动作类

Actions类是你要执行的单个Action的集合。例如，我们可能想要在元素上执行鼠标单击。在这种情况下，我们正在查看两个不同的Action

1.  将鼠标指针移动到元素
2.  点击元素

上面提到的各个动作都是用一个叫做Action的类来表示的， 我们后面会讲到。此类Action的集合由Actions类表示。

Actions类中有大量可用的方法。下面的屏幕截图显示了可用方法的列表。

![Selenium 中的动作类](https://www.toolsqa.com/gallery/selnium%20webdriver/2.Actions-Class-In-Selenium.png)

另外，这里要提到的一件重要的事情是，还有一个叫做Action Class的类，它与 Actions 类不同。因为你可能已经注意到上面屏幕截图中顶部的蓝线，build 方法返回 Action 类。但是什么是 Action 类，它与 Actions 类有何不同。我们来看一下。

### 什么是动作类？

我有没有提到Action Class，实际上它不是一个类而是一个接口。

它仅用于表示单个用户交互以执行由 Actions 类构建的一系列操作项。

### Selenium 中的 Actions 类和 Action 类有什么区别？

通过上面对Actions Class和Action Class的解释，我们现在可以得出结论，Actions是一个基于构建器设计模式的类。这是一个面向用户的 API，用于模拟复杂的用户手势。

而Action 是表示单个用户交互操作的接口。它包含使用最广泛的方法之一 perform()。

## 如何在 Selenium 中使用 Actions 类？

让我们通过一个简单的例子来理解 Actions 类的工作：

考虑需要在文本框中输入大写字母的场景，让我们以 ToolsQA 的演示站点 [http://demoqa.com/auto-complete/上的文本框为例](https://demoqa.com/autocomplete/)

![Selenium 中的 KeyDown 方法](https://www.toolsqa.com/gallery/selnium%20webdriver/3.KeyDown-method-in-Selenium.png)

手动，这是通过按下Shift 键然后键入需要以大写形式输入的文本来完成的，按住 Shift 键然后释放 Shift 键。总之Shift + 字母键是一起按下的。

现在，要通过自动化脚本模拟相同的操作，使用 Actions 类方法：

1.导入包：

Actions 类和 Action 类位于WebDriver API的org.openqa.selenium.Interactions包中要使用这些，请导入它们的包：

导入 org.openqa.selenium.interactions.Actions；

导入 org.openqa.selenium.interactions.Action；

2.实例化Actions类：

需要调用 Actions 类对象以使用其方法。因此，让我们实例化 Actions 类，正如类签名所说，它需要 WebDriver 对象来启动它的类。

动作 actions = new Actions(webdriver object);

3. 生成动作序列：复杂动作是多个动作的序列，在这种情况下，步骤序列是：

-   按 Shift 键
-   发送所需的文本
-   释放 Shift 键

对于这些操作，Actions 类提供了如下方法：

-   按下 Shift 键：Actions 类方法 => keyDown
-   发送所需的文本：Actions Class Method => sendKeys
-   释放 Shift 键：Actions 类方法 => keyUp

keyDown 方法在聚焦于元素后执行修饰键按下，而 keyUp 方法释放按下的修饰键。

修改键是当两个键同时按下时修改另一个键的操作的键，如 Shift、Control 和 Alt。

生成一系列这些操作，但这些操作是在 webElement 上执行的。所以，让我们找到网络元素并生成序列：

WebElement element = driver.findElement(通过策略识别元素);

actions.keyDown(元素，Keys.SHIFT)；

actions.sendKeys(" TextToBeConvertAndSendInUpperCase" );

actions.keyUp(Keys.SHIFT);

这里需要注意的一件重要事情是，如果将鼠标悬停在任何操作类方法上，你会注意到它返回 Actions 类对象。

![动作类方法](https://www.toolsqa.com/gallery/selnium%20webdriver/4.Action-CLass-Method.png)

这就是建造者模式的美妙之处。这意味着所有动作都可以组合在一起，如下所示：

actions.keyDown(element, Keys.SHIFT).sendKeys(" TextToBeConvertAndSendInUpperCase ").keyUp(Keys.SHIFT);

4. 构建动作序列：

现在，使用Actions 类的build() 方法构建此序列并获取复合操作。Build 方法生成一个复合动作，其中包含到目前为止准备执行的所有动作。

动作 action = actions.build();

请注意，build 方法返回Action 的对象类型。它基本上代表了我们从一系列多个动作构建的复合动作。因此，Actions 类描述的第二部分现在将变得清晰，即Actions 类实现了构建器模式，即它构建了一个包含方法调用指定的所有操作的 CompositeAction。

5. 执行动作序列：最后，使用动作接口的perform()方法执行动作序列。

行动.执行()；

这样就完成了，一旦执行通过这一点，你就会注意到浏览器上的动作。

需要遵循相同的步骤来利用 Actions 类方法来执行用户手势的不同复杂组合。

Catch in Actions 类：

在上面的 Actions 类截图中，我突出显示了所有不同的方法，请看一下底部的蓝线。注意 Perform 方法在 Actions 类中也可用。这意味着它也可以直接使用，而无需像下面这样使用 Action Interface：

actions.keyDown(元素,Keys.SHIFT).sendKeys(TextToBeConvertAndSendInUpperCase).keyUp(Keys.SHIFT).perform();

我知道你一定想知道构建步骤发生了什么。同样，这就是构建器模式的魅力所在，build 方法在 perform 方法内部被自动调用。

## Selenium 的 Actions 类中的方法

这个类中有很多方法，可以分为两大类：

-   键盘事件
-   鼠标事件

### 执行键盘事件的不同方法：

-   keyDown(修饰键)：执行修饰键按下。
-   sendKeys(keys to send): 发送键到活动的网络元素。
-   keyUp(modifier key)：执行修饰键释放。

### 执行鼠标事件的不同方法：

-   click()：点击当前鼠标位置。
-   doubleClick()：在当前鼠标位置执行双击。
-   contextClick() ：在给定元素的中间执行上下文单击。
-   clickAndHold()：在给定元素的中间单击(不松开)。
-   dragAndDrop(source, target)：在源元素的位置点击并按住，移动到目标元素的位置
-   dragAndDropBy(source, xOffset, yOffset)： 在源元素的位置单击并按住，移动给定的偏移量
-   moveByOffset(x-offset, y-offset)：将鼠标从当前位置(或 0,0)移动给定的偏移量
-   moveToElement(toElement)：将鼠标移动到元素的中间
-   release()：在当前鼠标位置释放按下的鼠标左键

要查看所有方法的完整列表，请访问[https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/interactions/Actions.html](https://seleniumhq.github.io/selenium/docs/api/java/org/openqa/selenium/interactions/Actions.html)

在接下来的教程中，我们将使用 Actions Class 进行大量练习。请查看以下教程：

-   [右键单击 Selenium](https://www.toolsqa.com/selenium-webdriver/right-click-and-double-click-in-selenium/)
-   [在 Selenium 中拖放](https://www.toolsqa.com/selenium-webdriver/drag-and-drop-in-selenium/)
-   [在 Selenium 中验证工具提示](https://www.toolsqa.com/selenium-webdriver/tooltip-in-selenium/)
-   [双击 Selenium](https://www.toolsqa.com/selenium-webdriver/right-click-and-double-click-in-selenium/)
-   [Selenium 中的键盘事件](https://www.toolsqa.com/selenium-webdriver/keyboard-events-in-selenium/)