## 构建 JMeter 测试计划

测试计划可以被视为一个根节点，它可能具有许多级别的附加元素/节点，形成层次结构，JMeter在我们运行测试计划时执行。测试计划可以由一个或多个元素组成，例如线程组、逻辑控制器、配置元素、计时器、侦听器和断言。每个测试计划中至少应该有一个线程组。我们可以轻松地从测试计划中添加或删除元素。

让我们按照以下两个步骤启动 JMeter 来开始构建测试计划：

• 转到你的 JMeter bin文件夹，例如 E:apache-jmeter-3.0bin

•双击ApacheJMeter.jar文件。

现在，你可以在短暂休息后看到以下 JMeter GUI。

![构建 JMeter 测试计划](https://www.toolsqa.com/gallery/Jmeter/1.Build%20JMeter%20Test%20Plan.png)

1.  重命名你的测试计划
2.  当你必须在测试计划的多个部分重复任何值时，用户定义的变量提供了灵活性。你将能够从一个点更改值，它会反映到任何地方。
3.  可选 - 或者你可以根据需要使用
4.  WorkBench用于临时存储用于复制/粘贴的测试元素。它还用于非测试元素，如 HTTP(S) 测试脚本记录器、HTTP 镜像服务器和属性显示。如果你需要将它与测试计划一起保存，那么你应该从 WorkBench 控制面板中选择选项“ Save WorkBench ”。

重命名测试计划并定义变量后，JMeter 将如下所示：

![测试计划_2](https://www.toolsqa.com/gallery/Jmeter/2.TestPlan_2.png)

1.  新的测试计划名称也会反映在左侧
2.  例如变量名设置为“ PATH ”
3.  例如，变量值设置为 URL(没有 http://)：“ store.demoqa.com ”

现在，这个变量可以用在测试计划元素的任何部分，比如 ${PATH}

看下面的例子。

![测试计划_3](https://www.toolsqa.com/gallery/Jmeter/3.TestPlan_3.png)

## 添加和删除测试计划元素

一旦创建了 JMeter 的测试计划，接下来我们应该学习的是向 JMeter 测试计划添加和删除元素。

### 将元素添加到 JMeter 测试计划的步骤

1.  选择测试计划节点或任何元素
2.  右键单击所选元素
3.  鼠标悬停在“添加”选项上，然后将显示元素列表
4.  将鼠标悬停在所需的列表元素上，然后通过单击选择适当的选项

在我们的教程中，我们添加了线程组元素：

![测试计划_4](https://www.toolsqa.com/gallery/Jmeter/4.TestPlan_4.png)

添加Thread Group元素后，可以看到如下画面：

![测试计划_5](https://www.toolsqa.com/gallery/Jmeter/5.TestPlan_5.png)

注意：元素也可以从文件中加载并通过选择“合并”或“打开”选项添加。

### 从 JMeter 测试计划中删除元素的步骤

当你向测试计划添加多个元素时，你可能希望删除一个不再需要的元素。这可以通过以下步骤完成：

1.  选择所需的元素
2.  右键单击元素
3.  选择“删除”选项

![测试计划_6](https://www.toolsqa.com/gallery/Jmeter/6.TestPlan_6.png)

在删除确认弹出窗口中单击“是”

![测试计划_7](https://www.toolsqa.com/gallery/Jmeter/7.TestPlan_7.png)

## 加载和保存 JMeter 测试计划元素

上面我们学习了添加和删除元素。现在是将元素加载和保存到 JMeter 测试计划的时候了。

### 将元素加载到 JMeter 测试计划树的步骤

1.  选择并右键单击要在其中添加加载元素的任何树元素
2.  选择“合并”选项
3.  选择保存元素的.jmx文件
4.  元素将被合并到树中
5.  不要忘记保存测试计划/元素

![测试计划_8](https://www.toolsqa.com/gallery/Jmeter/8.TestPlan_8.png)

## 将元素加载到 JMeter 测试计划树的步骤

1.  选择并右键单击元素
2.  选择“将选择另存为”选项
3.  将文件保存在所需位置

JMeter 将保存该元素及其下方的所有子元素。

![测试计划_9](https://www.toolsqa.com/gallery/Jmeter/9.TestPlan_9.png)

### 配置树元素的步骤

可以使用 JMeter 右侧框架上的控件来配置元素。这些控件允许我们配置所选元素的行为。配置因元素而异。

-   例如Thread Group可以配置为Number of Threads、Ramp-Up Period、Scheduler等，如下图：

![测试计划_10](https://www.toolsqa.com/gallery/Jmeter/10.TestPlan_10.png)

## JMeter 对测试计划的操作

到现在为止，我们已经完成了创建测试计划、添加元素和配置树的工作。让我们继续对测试计划执行不同的操作，例如保存、运行和停止。

### 保存 JMeter 测试计划的步骤

在执行之前保存测试计划总是更好。可以通过从文件菜单中选择“保存”或“将测试计划另存为”来保存测试计划。

![测试计划_11](https://www.toolsqa.com/gallery/Jmeter/11.TestPlan_11.png)

注意：WorkBench 不会与测试计划一起保存，你应该从 WorkBench 控制面板中选择“Save WorkBench”选项，否则你的 WorkBench 数据将会丢失。

![测试计划_12](https://www.toolsqa.com/gallery/Jmeter/12.TestPlan_12.png)

### 运行 JMeter 测试计划的步骤

可以从“运行”菜单项或单击绿色播放按钮运行测试计划。

![测试计划_13](https://www.toolsqa.com/gallery/Jmeter/13..TestPlan_13.png)

1.  绿色按钮显示测试计划正在运行。
2.  活动线程数/线程总数。
3.  测试计划运行时启用停止按钮。

### 停止 JMeter 测试计划的步骤

1.  Stop (Control + '.') 如果可能，它会立即停止线程。许多采样器是可中断的，因此可以提前终止活动采样。
2.  Shutdown (Control + ',') 它请求线程在任何正在进行的任务结束时停止。它不会中断任何活动样本。

![测试计划_14](https://www.toolsqa.com/gallery/Jmeter/14.TestPlan_14.png)

### 检查 JMeter 测试计划执行日志的步骤

JMeter 默认将测试运行详细信息、警告和错误存储到jmeter.log文件中，你可以访问 Jmeter 日志以进行调试。

1.  它显示日志中的错误总数
2.  点击黄色三角按钮查看日志面板
3.  日志显示在日志面板中

![测试计划_15](https://www.toolsqa.com/gallery/Jmeter/15.TestPlan_15.png)