在本教程中，我们将了解一个有趣的 Java 类，称为Robot 类。基于 Selenium 的测试自动化框架广泛使用它来模拟键盘和鼠标事件。在本文中，我们将学习：-

-   什么是 Robot 类、它的方法以及如何使用它们？
-   它的优点和局限性

## 机器人课有什么令人兴奋的地方？

Selenium 脚本使用 Robot 类来自动化浏览器和桌面弹出窗口，但令人兴奋的是这个类不是Web Driver API的org.openqa.selenium包的一部分。

那么这个类是从哪里来的呢？

它不驻留在 Web Driver API 中；它是[Java API awt](https://docs.oracle.com/javase/10/docs/api/java/awt/Robot.html)包的一部分。

### 需要机器人类

在[Actions 类](https://docs.oracle.com/javase/10/docs/api/java/awt/Robot.html)教程系列中，我们已经看到了处理键盘和鼠标事件的各种方法。现在，Actions 类处理 Web 驱动程序命令无法处理的情况。因此，有人可能会想，为什么我们需要这个甚至不是 WebDriver API 一部分的包？答案在于如下场景：

-   当用户需要处理网页上的警报弹出窗口时，或者
-   用户需要使用 Alt、Shift 等修饰键的组合在弹出窗口中输入文本。

在这里，弹出窗口/警报是 Windows 弹出窗口而不是网页弹出窗口。

我们知道要对 Web 元素执行任何操作，我们需要该元素的定位器。但是 Windows 弹出窗口没有任何定位器，因为它们不是网页的一部分，它们是本机操作系统弹出窗口。要处理此类弹出窗口，我们需要 Robot 类。

例如，如果你尝试下载电子邮件附件，Windows 会弹出“保存附件”提示指定下载位置。它只不过是一个本机操作系统弹出窗口。

不能使用 Action 类方法来处理桌面窗口弹出窗口上的键盘/鼠标事件。原因是，Actions 类方法需要 WebElement 对象来执行操作。而对于桌面窗口弹出窗口，不存在定位器，同样可以使用浏览器开发人员工具进行验证。因此，为了处理此类场景，使用了 Robot 类。

## 什么是机器人类？

根据类描述，此类用于生成本机系统输入事件。此类使用本机系统事件来控制鼠标和键盘。

![机器人类](https://www.toolsqa.com/gallery/selnium%20webdriver/1.Robot%20Class.png)

它不同于使用 WebDriver API 并向浏览器调用命令以执行操作的 Selenium。

### 如何使用机器人类方法？

[让我们借助 ToolsQa 的演示站点https://demoqa.com/keyboard-events/](https://demoqa.com/keyboard-events/)上的示例来了解 Robot 类方法的使用。考虑用户想要上传文件的场景。

![Demo介绍](https://www.toolsqa.com/gallery/selnium%20webdriver/2.DemoIntroduction.png)

所以，用户首先点击选择文件按钮，输入要上传的文件路径。在这里，出现选择文件的弹出窗口是桌面窗口。让我们使用 Robot 类方法来输入文件路径。

1.导入包：机器人类必须先导入，才能使用。

导入 java.awt.Robot；

2. 实例化：需要一个机器人类对象来调用它的方法。那么，让我们实例化 Robot 类。

机器人robot = new Robot();

3.调用方法：现在在机器人对象上调用所需的方法。

机器人.<required_method>();

Robot 类提供了多种处理鼠标和键盘事件的方法。为了输入文件路径，我们需要一种输入文本的方法。因此，这里使用的方法是keyPress(int keycode)，它按下给定的键码。

robot.keyPress(键码)；

### 机器人类中的方法：

如你所见，[java.awt.Robot](https://docs.oracle.com/javase/10/docs/api/java/awt/Robot.html)类提供了控制鼠标和键盘所需的各种方法。

![机器人方法](https://www.toolsqa.com/gallery/selnium%20webdriver/3.robot%20methods.png)

但是，我们只会介绍一些常用的浏览器测试自动化方法。

以下是浏览器测试自动化中常用的一些方法：

键盘方法：

-   keyPress(int keycode)：此方法按下给定的键。例如，keyPress(KeyEvent.VK_SHIFT) 方法按下“SHIFT”键
-   keyRelease(int keycode)：此方法释放给定的键。例如，keyRelease(KeyEvent.VK_SHIFT) 方法释放“ SHIFT ”键

鼠标方法：

-   mousePress(int buttons)：此方法按下一个或多个鼠标按钮。例如，mousePress(InputEvent.BUTTON1_DOWN_MASK)方法用于鼠标左键单击
-   mouseRelease(int buttons)：此方法释放一个或多个鼠标按钮。例如，mouseRelease(InputEvent.BUTTON1_DOWN_MASK) 方法用于释放鼠标左键单击
-   mouseMove(int x, int y)：此方法将鼠标指针移动到由 x 和 y 值指定的给定屏幕坐标。例如，mouseMove(100, 50) 会将鼠标指针移动到屏幕上的 x 坐标 100 和 y 坐标 50。

更多方法可以参考[Robot](https://docs.oracle.com/javase/10/docs/api/java/awt/Robot.html)类java文档。

### 优点：

以下是一些好处：

-   它提供对键盘和鼠标事件的控制。
-   它提供了一种处理与操作系统弹出窗口交互的方法，Selenium Web Driver API 无法支持这种交互。
-   Robot 类在通过与 OS 弹出窗口交互来管理文件上传/下载操作方面特别有用。
-   它很容易在 java Selenium 脚本中使用，因为此类是 Java 包的一部分。

### 限制：

但是上述控制键盘和鼠标的方法也有一定的局限性。在编写自动化脚本时考虑以下一些限制：

-   大多数方法(如 mouseMove)都依赖于屏幕分辨率，因此，该方法在不同屏幕上的执行可能会有所不同。
-   此类仅作用于焦点窗口，因此当打开多个窗口时行为可能不同。
-   使用 Robot 方法很难在不同的框架或窗口之间切换。

### 结论：

总之，本教程的目的是介绍 Robot 类。我们将在下一篇文章中更详细地介绍[键盘事件](https://www.toolsqa.com/selenium-webdriver/robot-class-keyboard-events/)和[鼠标事件方法。](https://www.toolsqa.com/selenium-webdriver/robot-class-mouse-events/)