## JMeter 中的线程组是什么？

线程组是一 组执行相同场景的线程。在配置中设置迭代次数。一旦每个线程的迭代次数过去，线程行为是根据上升和销毁来定义的。这是线程组元素的外观：

![JMeter 测试计划中的线程组](https://www.toolsqa.com/gallery/Jmeter/1.Thread%20Group%20in%20JMeter%20Test%20Plan.png)

线程组元素是[JMeter 测试计划](https://toolsqa.com/jmeter/build-jmeter-test-plan/)的初始步骤。线程组中可以定义多个线程(用户)。每个线程模拟一个真实的用户向被测服务器发出请求。

如果设置线程数为20；JMeter 将在负载测试期间创建并模拟 20 个虚拟用户。这里有一张图可以帮助我们更好地理解它

![线程组用户](https://www.toolsqa.com/gallery/Jmeter/2.Thread_Group_User.png)

## 如何在 JMeter 测试计划中创建线程组元素？

在上一章中我们学习了如何创建测试计划。线程组是测试计划的一个元素。要创建线程组，需要创建测试计划。

-   启动JMeter
-   在树上选择测试计划
-   添加线程组

通过右键单击 Test Plan 打开线程组面板，然后转到Add >> Threads >> Thread Group。如下图所示：

![线程组_1](https://www.toolsqa.com/gallery/Jmeter/3.Thread_Group_1.png)

## 线程组的组成部分

线程组面板包含以下组件 -

### 1) 采样器错误后采取的措施

如果 JMeter 在测试执行期间捕获任何采样器错误，你可以通过以下可用选项告诉它如何在该场景中做出反应。

![线程组_2](https://www.toolsqa.com/gallery/Jmeter/4.Thread_Group_2.png)

-   Continue， 忽略错误并移动到树中的下一个元素
-   启动下一个线程循环 以停止当前线程并启动下一个
-   Stop Thread，停止当前线程的执行。
-   停止测试， 停止整个测试执行。
-   Stop Test Now，整个测试会突然停止。

默认为继续。

### 2)线程属性

![Thread_Group_3](https://www.toolsqa.com/gallery/Jmeter/5.Thread_Group_3.png)

-   线程数 (用户)：模拟用户数或服务器应用程序的连接数。

-   Ramp-Up Period(以秒为单位)：告诉 JMeter 需要多长时间才能达到所选线程的全部数量。例如：如果你将“ Number of Threads ”设置为“ 20 ”，并将“ Ramp-Up Period 设置为 40 seconds ”，那么 JMeter 将等待 40 秒以使所有线程启动并运行。这意味着每个线程将启动 2 秒上一个线程启动后很晚

    。

    -   公式：加速期/线程数即 40 / 20 = 2(秒)

-   Loop Count : 测试被执行的次数。如果你需要永远运行测试，则选中“永远”复选框。

-   调度程序：安排测试执行。当你选中此复选框时，调度程序配置底部面板将被启用。时间表功能在浸泡/耐力测试中也非常有帮助。

### 3)调度器配置

你可以使用调度程序配置部分配置负载测试计划的测试开始时间、结束时间、持续时间和启动延迟。要启用此配置区域，必须从上述线程属性部分中选中调度程序复选框。

![Thread_Group_4](https://www.toolsqa.com/gallery/Jmeter/6.Thread_Group_4.png)

线程组配置面板如下：

-   开始时间：这计划测试在预定时间开始。前提条件是 JMeter 应该在“开始时间”字段中的给定日期和时间运行。
-   结束时间：此命令 JMeter 在提到的时间结束测试。结束时间覆盖并在两者之间停止执行。表示结束时间是完成测试计划执行的最大允许时间。一旦 End Time 发生，JMeter 立即结束执行。
-   持续时间(秒)：这告诉 JMeter 在特定的持续时间内执行测试。如果持续时间设置为 60 秒，JMeter 将继续执行 60 秒，并在时间结束后结束。它还会忽略或覆盖 End Time 和 All threads has completed its test or not。
-   启动延迟(秒)：这告诉 JMeter 在开始测试之前等待指定的时间。如果启动时间设置为 10 秒，JMeter 将不会开始加载用户，直到 10 秒结束。