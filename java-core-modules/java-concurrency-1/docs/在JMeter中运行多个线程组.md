## 1. 概述

使用JMeter，我们可以对场景进行分组并以不同的方式运行它们以复制真实世界的流量。

在本教程中，我们将学习如何以及何时使用多个线程组复制真实场景，以及如何使用简单的测试计划顺序或并行运行它们。

## 2. 创建多个线程组

线程组是JMeter的一个元素，它控制执行测试的线程数。

JMeter测试计划中的每个线程组都模拟了一个特定的真实应用场景。

大多数基于服务器的应用程序通常有多个场景，因此创建一个单独的线程组映射到每个用例可以让我们更灵活地在测试期间正确分配此负载。

有两种运行多线程组的方法：顺序或并行。

## 3. 顺序运行线程组

这主要在我们要一个接一个地执行应用场景时有用，尤其是当各个场景之间存在依赖关系时。

### 3.1 用例

假设我们有一个电子商务应用程序，用户可以在其中浏览产品、将特定产品(他们喜欢的)添加到他们的购物车，最后开始结帐，然后下最终订单。

对于此类应用程序，当我们想要模拟用户旅程时，我们希望我们的脚本遵循特定的顺序。例如，我们的脚本可能首先执行浏览产品，然后将产品添加到购物车，最后下订单。

### 3.2 配置

从测试计划中，你可以通过选中复选框连续运行线程组(即一次一个)来实现此行为： 

[![Multiple_Thread_Groups_Sequencially](https://www.baeldung.com/wp-content/uploads/2022/12/Multiple_Thread_Groups_Sequencially.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/Multiple_Thread_Groups_Sequencially.jpg)

## 4. 并行运行线程组

这主要在各种场景之间没有依赖关系时有用。

测试操作同时执行，模拟被测系统的混合负载。

### 4.1 用例

例如，假设一个网站的新闻分为技术新闻、市场新闻、体育新闻等类别。

本网站的主页将始终显示所有不同类别的最新头条新闻。

对于此类应用程序，我们仍然可以创建多个线程组以在不同页面上具有不同的用户负载分布。

但是，可以同时执行这些线程组，因为它们是互斥的。

### 4.2 配置

JMeter中的Test Plans默认配置为并行运行Multiple Thread Groups，所以我们不需要勾选Run Thread Groups continuously。

[![平行线](https://www.baeldung.com/wp-content/uploads/2022/12/2_Parallel.png)](https://www.baeldung.com/wp-content/uploads/2022/12/2_Parallel.png)

## 5. 测试用例设置

要试用测试计划，我们需要一个API。我们可以使用[JSON Placeholder](https://jsonplaceholder.typicode.com/)站点公开的一个。该站点提供了假API供我们进行试验。

我们将为我们的测试计划使用两个场景

场景1：阅读特定帖子

场景2：创建新帖子

由于大多数最终用户将对阅读帖子而不是撰写新帖子感兴趣，因此我们希望将它们保留为两个单独的线程组的一部分。

## 6. 将线程组添加到测试计划

### 6.1 创建基本测试计划

我们将[运行JMeter](https://www.baeldung.com/jmeter#run-jmeter)开始。

默认情况下，JMeter创建一个名为Test Plan的默认测试计划。让我们将此名称更新为我的测试计划：

[![测试计划](https://www.baeldung.com/wp-content/uploads/2022/12/1_Test_Plan-1-e1669565285646.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/Test_Plan.jpg)

### 6.2 添加多个线程组

要创建线程组，我们将右键单击测试计划并选择Add -> Threads (Users) -> Thread Group：

[![添加线程组](https://www.baeldung.com/wp-content/uploads/2022/12/2_Add_Thread_Group.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/2_Add_Thread_Group.jpg)

现在我们将创建两个线程组，从GET请求线程组开始：

[![获取请求](https://www.baeldung.com/wp-content/uploads/2022/12/Get_Request.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/Get_Request.jpg)

该线程组将用于阅读特定的帖子。

我们在这里指定了一些关键参数：

-   Name：GET请求线程组(我们要给这个线程组起的名字)
-   Number of Threads：5(我们将模拟的虚拟用户数作为负载的一部分)
-   Ramp up Period：10(启动和运行配置的线程数所需的时间)
-   Loop Count：1(JMeter应执行特定场景的次数)

接下来，我们将创建POST请求线程组：

[![发布请求线程组](https://www.baeldung.com/wp-content/uploads/2022/12/Post_request_thread_group.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/Post_request_thread_group.jpg)

该线程组将用于创建新帖子。

在这里，我们指定了：

-   Name：POST请求线程组(我们要给这个线程组起的名字)
-   Number of Threads：5(我们将模拟的虚拟用户数作为负载的一部分)。
-   Ramp-up Period：10(为特定线程组启动和运行配置的线程数所需的时间)
-   Loop Count：1(JMeter应执行在单个线程组中定义的特定场景的次数)

### 6.3 添加请求

现在，对于每个线程组，我们将添加一个新的HTTP请求。

要创建请求，我们右键单击测试组并选择Add -> Sampler -> HTTP Request：

[![添加请求](https://www.baeldung.com/wp-content/uploads/2022/12/2_add_request.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/2_add_request.jpg)

现在我们在GET Request Thread Group下创建一个请求：

[![阅读帖子](https://www.baeldung.com/wp-content/uploads/2022/12/read_post.png)](https://www.baeldung.com/wp-content/uploads/2022/12/read_post.png)

在这里，我们指定：

-   Name：Read Post(我们要给这个HTTP请求的名称)
-   Comments：阅读ID = 1的特定帖子
-   Server Name or IP：my-json-server.typicode.com 
-   HTTP Request Type：GET(HTTP请求方法)
-   Path：/typicode/demo/posts
-   Send Parameters with the request：在这里，我们使用了1个参数，即 id(这是检索具有特定id的帖子所必需的)

现在我们将在POST请求线程组下创建另一个请求：

[![创建帖子](https://www.baeldung.com/wp-content/uploads/2022/12/create_post.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/create_post.jpg)

在这里，我们指定了：

-   Name：创建帖子(我们要为此HTTP请求提供的名称)
-   Comments：创建ID = p1的新帖子(通过将其发布到服务器)
-   Server Name or IP：my-json-server.typicode.com 
-   Path：/typicode/demo/posts
-   Send Parameters with the request：在这里，我们使用了两个参数，即id和title(这些是创建新帖子所需的属性)

### 6.4 添加汇总报告

JMeter使我们能够以多种格式查看结果。

要查看执行结果，我们将在表监听器中添加一个查看结果。

要创建请求，我们右键单击“测试计划”并选择Add -> Listener -> View Results in Table：

[![添加摘要](https://www.baeldung.com/wp-content/uploads/2022/12/add_summary.png)](https://www.baeldung.com/wp-content/uploads/2022/12/add_summary.png)

### 6.5 运行测试(并行)

现在我们按下工具栏上的运行按钮(Ctrl + R)以启动JMeter性能测试。

测试结果实时显示：

[![查看结果](https://www.baeldung.com/wp-content/uploads/2022/12/view_results.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/view_results.jpg)

这表明Read Post和Create Post针对配置的线程数一个接一个(并行)运行。

此测试结果是并行运行多个线程组的结果。这是测试计划的默认设置(未选中复选框)：

[![未选中复选框](https://www.baeldung.com/wp-content/uploads/2022/12/checkbox_unselected.jpg)](https://www.baeldung.com/wp-content/uploads/2022/12/checkbox_unselected.jpg)

### 6.6 运行测试(按顺序)

现在我们从我们的测试计划中选择连续运行线程组(即一次一个)复选框：

[![复选框被选中](https://www.baeldung.com/wp-content/uploads/2022/12/checkbox_selected.png)](https://www.baeldung.com/wp-content/uploads/2022/12/checkbox_selected.png)

现在我们再次按下工具栏上的运行按钮(Ctrl + R)以启动JMeter性能测试。

测试结果实时显示：

[![顺序总结](https://www.baeldung.com/wp-content/uploads/2022/12/summary_in_sequence.png)](https://www.baeldung.com/wp-content/uploads/2022/12/summary_in_sequence.png)

这表明映射到Read Post的所有线程首先执行，然后是Create Post线程。

## 7. 总结

在本教程中，我们了解了如何创建多个线程组并使用它们来模拟真实的应用程序用户负载。

我们还了解了有关如何按顺序或并行配置多个线程组的场景。