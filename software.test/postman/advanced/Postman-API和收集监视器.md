Postman 中的监视器是一个重要的特性。它使你能够运行 Collections(一组 API 测试)以在一天中定期运行。简而言之，监视器允许你安排测试以定义的时间间隔自动运行。在开始本教程之前，我会要求你返回此处阅读集合的基础知识

-   [如何制作合集](https://toolsqa.com/postman/collections-in-postman/)
-   [收藏赛跑者](https://toolsqa.com/postman/test-and-collection-runner-in-postman/)

### 什么是 Postman 监视器？

Rest API 构成了现代分布式应用程序的支柱。确保你的 API 的响应和性能在一天中保持符合标准非常重要。监视器可以帮助你安排一组 测试运行以监视 API 的性能和响应。监视器可以安排为非常频繁地运行，例如每 5 分钟一次，或者可以安排为全天每隔几个小时运行一次。

## 如何监控 Postman 中的集合？

1.  创建一个名为Monitoring的集合

![采集_监控](https://www.toolsqa.com/gallery/Postman/1.Collection_Monitoring.png)

1.  在其中输入[天气 api](https://restapi.demoqa.com/utilities/weatherfull/city/Hyderabad)请求([GET 教程](https://toolsqa.com/postman/test-and-collection-runner-in-postman/))

![监控请求](https://www.toolsqa.com/gallery/Postman/2.Monitoring_Request.png)

注意：请记住，我们将在此请求中使用局部变量 url。你可以从[环境和变量](https://toolsqa.com/postman/environment-variables-in-postman/)教程中回忆起它。

1.  单击箭头按钮打开菜单

![Arrow_Collection](https://www.toolsqa.com/gallery/Postman/3.Arrow_Collection.png)

1.  查看“监视器”选项卡

![监控标签](https://www.toolsqa.com/gallery/Postman/4.Monitor_Tab.png)

1.  如果你还没有登录 postman，你必须现在登录，否则你将无法使用 Postman Monitor。

![Sign_In_Monitor_Tab](https://www.toolsqa.com/gallery/Postman/5.Sign_In_Monitor_Tab.png)

一旦你登录到 Postman，我们就可以创建一个Monitor来运行我们的Collection。

1.  点击添加监视器

![添加_监视器](https://www.toolsqa.com/gallery/Postman/6.Add_Monitor.png)

注意：此选项仅在登录后打开

1.  你将看到一个弹出窗口，如下图所示。

![Monitor_Collection_Tab](https://www.toolsqa.com/gallery/Postman/7.Monitor_Collection_Tab.png)

这包含一些输入选项，例如

-   监视器名称：监视器名称非常简单。它是你的显示器的名称。你可以将其命名为任何名称，因为我们在这里将其命名为 Monitoring Collection。你可以将多个监视器应用到同一个集合，因此如果你应用有意义的名称以便以后知道你在监视什么会更好。
-   Environment：Environment 字段允许你选择希望运行测试的环境。如果你之前在 Postman 中创建了一个环境，那么你将在下拉列表中找到这里。只需选择你想要的那个。如果你的脚本使用环境 URL 进行硬编码，那么你可以选择“无环境”选项。Postman 监控不允许使用全局环境。如果你正在使用任何，那么你可能必须在本地环境中再次编写它。有关更多详细信息，你可以访问[环境和变量](https://toolsqa.com/postman/environment-variables-in-postman/)教程以了解更多信息。
-   Schedule：Schedule 选项用于安排监视器运行的时间间隔。由于监视器以特定时间间隔运行，因此你必须相应地设置值。这些选项非常简单。你可以为分钟间隔设置分钟计时器，为每小时间隔设置小时计时器，同样还有每周计时器。
-   区域：区域选项在监视器中可用，以根据区域同步时间。有几个选项可用于匹配时间。为简单起见，我们将使用美国(东部)区域(这也是默认区域)。你应该根据你的物理位置使用区域，以便更好地进行测试和监控。

1.  按监控此集合

![Press_Monitor_Collection](https://www.toolsqa.com/gallery/Postman/8.Press_Monitor_Collection.png)

1.  在上一步中按下按钮后，你的监视器控制台将可以看到监视器。

![Monitor_Collection_Available](https://www.toolsqa.com/gallery/Postman/9.Monitor_Collection_Available.png)

1.  单击监视器后，将在浏览器中打开一个新窗口，将你重定向到你在 Postman 中的帐户。可以看到一个新的控制台，如下图所示。

![Monitor_Window_Postman](https://www.toolsqa.com/gallery/Postman/10.Monitor_Window_Postman.png)

现在你可以高枕无忧，让 Postman 完成它的工作。你已成功将监视器应用于你的其中一个收藏。邮递员将根据你设置的时间表运行它。

## 分析监控结果

一段时间后，监视器控制台将看起来像这样。

![如何定期监控 Postman 中的收藏？](https://www.toolsqa.com/gallery/Postman/11.%20Monitor%20Collections%20in%20Postman%20periodically.png)

这是一份 API 性能报告。你看到的绿色条表示请求的测试已经通过。如果你的测试失败，则该条将变为红色。测试需要在其中包含断言，以便我们可以捕获 API 中的任何问题。你可以在断言教程中了解有关断言的更多信息。

只需在网络浏览器中登录邮递员，即可再次查看监控页面。

登录后，你将看到 Postman 仪表板，如下图所示。单击监视器。

![邮递员_仪表板](https://www.toolsqa.com/gallery/Postman/12.Postman_Dashboard.png)

你将在下一个窗口中看到所有监视器。这里我们只有一个，所以我们只看到那个。

![监控_仪表板_Web](https://www.toolsqa.com/gallery/Postman/13.Monitor_Dashboard_Web.png)

单击监视器，相同的条将再次出现。你可以选择显示任何错误的任何栏来查看它。例如，假设我们选择对应于 12:55 AM 的柱

![Bar_Selection](https://www.toolsqa.com/gallery/Postman/14.Bar_Selection.png)

只要我们点击栏，相应的数据就会出现在它的正下方。

![Bar_Result_Data](https://www.toolsqa.com/gallery/Postman/15.Bar_Result_Data.png)

你可以查看数据并查看不同的请求、测试，甚至错误。这类似于 collection runner 控制台，具有相似的功能。上面显示的响应数据还有另一个特征。如果你更喜欢调试和查找特定错误，你还可以查看控制台日志。

点击控制台日志

![Bar_Result_Data](https://www.toolsqa.com/gallery/Postman/16.Bar_Result_Data.png)

这将向你显示记录错误的控制台以及所有已处理的日志。这有助于开发人员和调试人员了解出了什么问题。请记住在完成试验后删除监视器。有了这些特性，监视器是一种更好的开发和测试方法，但它也有一些缺点。

## 显示器的缺点

由于显示器及其功能几乎没有缺点，因此它是否对你来说是个问题完全取决于你。我们将简要讨论这些。

### 不能在 Postman 网络中使用

如果邮递员服务器与你在同一个网络中，邮递员监视器将无法工作。在同一网络中存在一个问题，即由于你的网络正在运行，你将永远无法诊断“你的收藏在另一个网络中的行为如何”，因此当位于同一网络中时不允许邮递员监视器。但由于这是一种可能的情况，Postman 对此有解决方案。

如果你和 Postman 在同一个网络，那么你需要购买 Postman 的专业版。完成后，邮递员将为你提供另一个可以运行监视器的 ip。

### 无法导入全局变量

正如前面在设置监视器时所讨论的，全局变量不能导入到监视器中。初始化监视器后，如果你使用任何局部变量，它只需要局部变量。如果你有全局变量，则需要将它们作为局部变量复制到新环境中。

### 监视器的 API 调用限制

你可以使用你的收藏初始化和运行 Postman 监视器，但监视器被设计为 Postman 中的专业功能。免费服务是让你熟悉显示器。如果你像我们在本教程中那样使用免费服务，你每月只会收到 1000 次 API 调用，其中包括每个监视器的总和。

你可以随时购买 Postman pro 功能，通过监视器享受无限的 API 调用，但这个缺点只是你认为的缺点。如果你能够管理你的任务并在免费版本限制内工作，那就太好了。

因此，在本教程中，我们熟悉了 Postman 监视器并学习了如何使用它们。我们现在将继续下一个教程。