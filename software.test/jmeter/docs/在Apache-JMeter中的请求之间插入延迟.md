## 一、概述

当我们使用 Apache JMeter 进行测试时，我们可能希望在请求之间添加延迟，以便更好地模拟我们的用户行为。

在本教程中，我们将创建一个简单的测试计划。我们将查看用于调整生成的工作负载的可用参数，然后配置计时器以添加延迟。

## 2.用例

有时我们可能希望在请求之间添加延迟：

-   避免与在给定时间内发送过多请求相关的错误
-   模拟真实的用户操作，执行操作有自然间隙
-   调整每分钟的请求数以更好地控制工作负载配置

## 3.使用延迟

首先，我们需要定义加载配置文件。我们可以在这里有不同的目标：

-   查看系统在不断增长的工作负载下的行为，以找出性能限制
-   检查应用程序在峰值负载后如何恢复

模拟这些用例有两个 JMeter 选项：

-   线程组——有多少并行用户
-   计时器——每个用户请求之间的延迟

## 4. 测试计划

### 4.1. 基本计划

让我们用一个线程组创建一个基本的测试计划。我们将设置并行请求的数量、加速周期和执行测试的次数。我们应该注意，**JMeter 表示法中的一个线程表示一个并发用户。**

[![线程组](https://www.baeldung.com/wp-content/uploads/2021/08/thread-group.png)](https://www.baeldung.com/wp-content/uploads/2021/08/thread-group.png)

我们可以利用*加速期*来增加工作量。这里我们需要设置从 1 个线程开始达到定义的*线程数的*周期。

要创建更复杂的加载配置文件，我们还可以指定线程生命周期。此设置意味着两件事：

-   *启动延迟*——JMeter 等待启动线程的时间
-   *持续时间*——它运行了多长时间

循环*计数*也是一个有用的设置，用于指定指定 HTTP 请求的重复次数。

### 4.2. 添加请求

接下来，我们将添加两个 HTTP 请求。*我们将使用https://gorest.co.in/*上的在线 REST API来测试我们的脚本。HTTP 请求设置在用户界面中配置：

[![http请求设置](https://www.baeldung.com/wp-content/uploads/2021/08/http-request-settings.png)](https://www.baeldung.com/wp-content/uploads/2021/08/http-request-settings.png)

我们还添加两个断言，只是为了检查请求是否返回了一些数据。

我们需要检查我们的测试是否没有错误。为此，让我们添加*View Results Tree*元素，然后运行我们的测试计划。

运行第一个请求的结果显示在*“查看结果树”*面板中。

[![运行结果](https://www.baeldung.com/wp-content/uploads/2021/08/run-results-1-e1621249469601.png)](https://www.baeldung.com/wp-content/uploads/2021/08/run-results-1-e1621249469601.png)

让我们看看第二个请求的*Sampler 结果输出。*在这里，*Sample Start*是*2021-05-17 15:00:40*，与第一个请求的时间相同。这意味着默认情况下，我们在请求之间没有任何延迟。

```
线程名称：线程组1-1
样品开始时间：2021-05-17 15:00:40 GMT
```

考虑到这一点，让我们看看如何增加请求之间的差距。

## 5. 添加定时器

### 5.1. 常量定时器

要添加计时器元素，我们需要右键单击*Thread Group*元素并选择*Add, Timer, Constant Timer*。

[![添加定时器](https://www.baeldung.com/wp-content/uploads/2021/08/Adding-timer-e1621250184753.png)](https://www.baeldung.com/wp-content/uploads/2021/08/Adding-timer-e1621250184753.png)

在这里，我们向线程组添加了一个线程*延迟*为三秒的*常量计时器。*这个计时器在每个请求之间增加了一个延迟。

现在让我们重新运行我们的测试计划并检查*查看结果树。*我们应该看到请求以我们在计时器元素中设置的延迟运行。

```
线程名称：线程组1-1
样品开始：2021-05-17 15:18:17 SAMT
```

我们可以看到下一个 HTTP 请求在第一个 HTTP 请求之后运行了三秒。

```
线程名称：线程组1-1
样品开始：2021-05-17 15:18:20 SAMT
```

### 5.2. 常量定时器的替代品

*作为Constant Timer*的替代品，我们可以使用*Uniform Random Timer*。这种类型的定时器可以像Constant Timer一样添加。

在下拉菜单中，它就在*Constant Timer*之后。

从定时器名称可以看出，当我们希望这个延迟在某个指定范围内变化时，我们应该使用它。让我们将这个计时器添加到我们的示例中，看看它是如何工作的：

[![统一定时器](https://www.baeldung.com/wp-content/uploads/2021/08/uniform-timer.png)](https://www.baeldung.com/wp-content/uploads/2021/08/uniform-timer.png)

*Constant Delay Offset*为每个延迟添加一个永久部分。*Random Delay Maximum*帮助我们定义将添加到 Constant Delay Offset 的附加随机部分。这些设置允许我们提供一个随机因素，而延迟不会变得太小。

让我们运行此测试并查看 View Results Tree 元素：

[![统一计时器结果](https://www.baeldung.com/wp-content/uploads/2021/08/uniform-timer-results.png)](https://www.baeldung.com/wp-content/uploads/2021/08/uniform-timer-results.png)

如果我们仔细查看样本开始点，我们会看到根据定义的定时器参数添加了随机延迟。

```
线程名称：线程组1-1
样品开始：2021-07-15 09:43:45 SAMT

线程名称：线程组1-1
样品开始：2021-07-15 09:43:49 SAMT

线程名称：线程组1-1
样品开始：2021-07-15 09:43:55 SAMT
```

在这里，我们查看了几个计时器选项，尽管还有[其他可用的计时器配置](https://jmeter.apache.org/usermanual/component_reference.html#timers)。

## 六，结论

在本教程中，我们了解了如何在 Apache JMeter 中的两个请求之间插入自定义延迟，并使用线程组设置为创建的工作负载模型增加更多灵活性。