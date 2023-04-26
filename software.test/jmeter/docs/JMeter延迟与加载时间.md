## 1. 概述

性能测试是软件开发的重要组成部分。它有助于揭示瓶颈和错误，并确保我们的应用程序具有响应能力。一个特别重要的方面是 Web 应用程序需要加载和响应用户交互的时间。

在本文中，我们将探索两个有助于检测和改进加载时间问题的指标：延迟和加载时间。我们将了解这些指标是如何定义的，它们之间的区别是什么，以及如何使用性能工具[JMeter](https://www.baeldung.com/jmeter)来衡量它们。

## 2. JMeter 中的延迟和加载时间指标

在 JMeter 中，延迟和加载时间都是衡量往返的指标。换句话说，它们都测量从客户端向服务器发送请求到它收到响应的时间。但是，这两者之间有一个重要的区别。

延迟定义为从发送请求之前到收到响应的第一部分之后的时间，而加载时间是从发送请求之前到收到响应的最后部分之后的时间。

对于这两个指标，JMeter 包括组装请求所需的时间。延迟还包括组装响应的第一部分所需的时间，加载时间包括组装整个响应的时间。组装不包括响应的呈现或任何客户端代码的执行。

## 3. 如何测量延迟和加载时间

我们可以通过创建发送 HTTP 请求并使用View Results Tree Listener的测试计划来测量JMeter 中的延迟和加载时间。

我们将从打开工具时 JMeter 自动创建的测试计划开始。在我们的例子中，让我们将其重命名为LatencyVsLoadTime：

[![JMeter 测试计划的屏幕截图](https://www.baeldung.com/wp-content/uploads/2022/10/2_test-plan-e1664110726752.png)](https://www.baeldung.com/wp-content/uploads/2022/10/2_test-plan-e1664110726752.png)

接下来，让我们创建一个线程组，这是每个测试计划的起点。这是通过右键单击测试计划然后选择Add -> Thread (Users) -> Thread Group来完成的：

[![关于如何创建线程组的 JMeter 屏幕截图](https://www.baeldung.com/wp-content/uploads/2022/10/2_thread-group-e1664110971895.png)](https://www.baeldung.com/wp-content/uploads/2022/10/2_thread-group-e1664110971895.png)

接下来，让我们通过右键单击线程组并选择添加 -> 采样器 -> HTTP 请求来添加 HTTP 请求：

[![关于如何创建 http 请求采样器的 JMeter 屏幕截图](https://www.baeldung.com/wp-content/uploads/2022/10/http-request-create.png)](https://www.baeldung.com/wp-content/uploads/2022/10/http-request-create.png)

最后但同样重要的是，我们需要一个监听器来跟踪我们的请求结果。让我们通过右键单击Thread Group并选择Add -> Listener -> View Results Tree来添加它：

[![关于如何创建查看结果树监听器的 JMeter 屏幕截图](https://www.baeldung.com/wp-content/uploads/2022/10/view-results-tree.png)](https://www.baeldung.com/wp-content/uploads/2022/10/view-results-tree.png)

现在我们已经准备好测试计划的所有部分，让我们配置我们的 HTTP 请求。

为此，我们选择HTTP 请求并将路径设置为我们要测试的 URL。对于我们的示例，让我们选择https://www.google.com：

[![关于如何配置 http 请求的 JMeter 屏幕截图](https://www.baeldung.com/wp-content/uploads/2022/10/2_http-request-configure.png)](https://www.baeldung.com/wp-content/uploads/2022/10/2_http-request-configure.png)

单击顶部栏中的磁盘图标保存测试计划后，我们可以执行它。让我们选择HTTP 请求并单击顶部栏中的播放按钮：

[![关于如何保存测试计划的 JMeter 屏幕截图](https://www.baeldung.com/wp-content/uploads/2022/10/2_test-plan-save.png)](https://www.baeldung.com/wp-content/uploads/2022/10/2_test-plan-save.png)

最后，让我们通过选择Sampler 结果选项卡下的View Results Tree元素来检查结果。在我们的示例中，请求的延迟为 215 毫秒，加载时间为 218 毫秒：

[![一次http请求的JMeter测试结果截图](https://www.baeldung.com/wp-content/uploads/2022/10/test-restults.png)](https://www.baeldung.com/wp-content/uploads/2022/10/test-restults.png)

## 4. 总结

在本文中，我们讨论了两个性能指标，延迟和加载时间，它们可以帮助检测加载时间问题并提高应用程序的可用性。

首先，我们在JMeter的上下文中定义了指标，并详细说明了两者之间的区别。然后，我们了解了如何使用可用于衡量指标的 HTTP 请求来设置 JMeter 测试计划。最后，我们学习了如何执行测试计划并检查结果。