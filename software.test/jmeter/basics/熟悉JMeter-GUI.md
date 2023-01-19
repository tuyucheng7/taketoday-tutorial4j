在[前面的教程](https://toolsqa.com/jmeter/how-to-install-jmeter/)中，我们了解了 JMeter 4.0 中的新功能；在 MAC 操作系统和 Windows 操作系统上安装并启动了 JMeter。在本教程中，我们将继续并熟悉 JMeter GUI。

## 熟悉 JMeter GUI

使用这两个简单的步骤开始启动 JMeter。

1.  转到你机器的终端/命令提示符。
2.  键入此命令：

-   Windows：`<file path>`/apache-jmeter-4.0/bin/jmeter.bat
-   MAC：`<file path>`/apache-jmeter-4.0/bin/jmeter.sh

几秒钟后，JMeter 将启动，如下所示

![第一眼](https://www.toolsqa.com/gallery/Jmeter/1.FirstLook.png)

注意：请参考我们之前的教程以详细了解如何[启动 JMeter](https://toolsqa.com/jmeter/how-to-install-jmeter/)

JMeter主要分为三个主要部分：

1.  左窗格：左窗格是你要执行的测试所在的位置。
2.  配置窗口：在这个窗口中，我们设置配置并控制我们要执行的测试的行为。
3.  菜单栏：这是一个直观的菜单栏，你可以从中执行所有功能。

在 JMeter 4.0 中，选项菜单已从 GUI 中删除。因此，用户可以使用菜单栏中的按钮执行几乎所有功能。

![熟悉 JMeter GUI](https://www.toolsqa.com/gallery/Jmeter/2.Getting%20Familiar%20with%20JMeter%20GUI.png)

## 左窗格

JMeter 中的左窗格包含一个称为Test Plan的节点。顾名思义，测试计划可以被认为是一个由测试场景和测试数据组成的容器。

同样，JMeter 测试计划也可以被认为是一个容器，其中包含一系列在我们运行测试计划时将执行的步骤。测试计划可以包含一个或多个元素，如 线程组、逻辑控制器、配置元素、采样器、计时器、监听器和断言 ，我们将在后面的教程中介绍这些元素。

![测试计划](https://www.toolsqa.com/gallery/Jmeter/3.TestPlan.png)

注意：在 JMeter 中一次只执行一个测试计划。

## 配置面板

你可以在此窗口中配置你的测试计划及其元素。可以在此处配置测试计划的名称、用户定义的变量及其属性。配置测试计划可帮助你控制其属性以及你希望根据你的要求运行测试计划的行为。

默认情况下，当我们启动 JMeter 时，下面的屏幕是可见的。

![配置面板](https://www.toolsqa.com/gallery/Jmeter/4.ConfigPane.png)

上面的画面由三大部分组成：

-   姓名
-   用户定义的变量
-   测试计划属性。

#### 姓名

这显示了你的测试计划的名称。你可以在此处更改测试计划的名称。我们将在下一个教程中讨论如何创建、命名和保存测试计划。

![姓名](https://www.toolsqa.com/gallery/Jmeter/5.Name.png)

#### 用户定义的变量

用户定义的变量不过是 名称-值对。

你可以通过单击添加按钮来添加变量。你可以通过每次单击“添加”按钮或使用“从剪贴板添加”按钮来添加多个名称-值对。以下屏幕显示了添加的变量及其值。

![添加变量](https://www.toolsqa.com/gallery/Jmeter/6.Variables_added.png)

#### 测试计划属性

测试计划的三个主要配置属性可用于根据要求控制测试计划的行为。这三个属性是：

-   连续运行线程组(即一次一个)
-   主线程关闭后运行 tearDown 线程组
-   功能测试模式(即保存响应数据和采样器数据)

![特性](https://www.toolsqa.com/gallery/Jmeter/7.Properties.png)

#### 连续运行线程组(即一次一个)

一个测试计划可以有一个或多个线程组。它应该至少有一个线程组(这是最低要求)但也可以有多个。如果测试计划有多个线程组，那么如果选中此复选框，它们将一个接一个地执行。如果此复选框保持未选中状态，则所有线程组将并行执行。

#### 主线程关闭后运行 tearDown 线程组

TearDown 线程在测试完成其常规线程组执行后执行。如果选中此复选框，则此线程将在测试执行后运行。tearDown 特性通常用于报告或清理操作。例如，如果你希望在执行测试计划后自动清理你的日志，或者希望你的报告采用特定格式，那么你可以使用测试计划的这个属性。

#### 功能测试模式(即保存响应数据和采样器数据)

如果选中此复选框，则采样器请求和响应数据将保存在侦听器中。不要担心这里的听众和其他技术术语。我们将在后面的教程中讨论所有内容，本教程只是为了让你熟悉 JMeter 的 GUI。此复选框允许你验证测试是否按预期工作。

注意：线程组是测试计划的子元素。每个线程组代表一个用例。(我们将在接下来的教程中讨论线程组)

## 菜单栏

这是 JMeter GUI 上最上面的栏。它有许多按钮，可以帮助我们通过单击相应的按钮来执行各种功能。

我们可以使用上面的菜单栏执行许多操作，但没有必要在这里讨论所有这些操作。因此，我们将只讨论最重要的一个。如果课程后面出现任何其他功能，我们将在那里详细说明。

![菜单栏](https://www.toolsqa.com/gallery/Jmeter/8.MenubAr.png)

下图显示了最常用的图标及其工作方式。

![第一菜单](https://www.toolsqa.com/gallery/Jmeter/9.FirstMenu.png)

-   新建：使用新建菜单项，你可以创建新的测试计划。
-   打开：要打开现有的测试计划，你可以使用菜单栏中的打开按钮。
-   保存：要保存测试计划或其元素，你只需单击此按钮即可。当你单击此按钮时，会出现一个提示

![第二菜单](https://www.toolsqa.com/gallery/Jmeter/10.SecondMenu.png)

-   开始：用于执行测试的开始图标。创建测试计划并向其中添加元素后，你只需单击此按钮即可开始执行测试。
-   Start with no pauses：像 Start 一样，我们可以使用 Start with no pauses 来执行测试。两者之间的区别在于，如果在线程组中配置的 Start No Pause 计时器被跳过，线程将运行而无需任何手动暂停。
-   Stop：顾名思义，你可以使用 Stop 来突然停止正在运行的测试。
-   关机：关机也会停止执行，但会优雅地停止。当你关闭正在运行的测试时，它不会立即停止它，而是让正在运行的线程停止运行。

![第三菜单](https://www.toolsqa.com/gallery/Jmeter/11.ThirdMenu.png)

-   全部清除：你可以通过单击全部清除按钮来清除日志窗口。
-   Elapsed time of current running test : 显示执行测试所花费的时间。
-   在日志中显示错误数：单击此菜单选项后，你将在配置窗口的底部看到日志。
-   Running thread/total number of threads：此选项告诉你当前正在运行的线程总数中有多少线程。

因此，在本教程中，我们了解了 熟悉 JMeter GUI并详细了解什么是测试计划以及测试计划的属性。在下一个教程中，我们将学习如何在 JMeter 中执行一些基本操作。