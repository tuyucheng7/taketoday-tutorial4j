## 1. 概述

Gatling 是一种负载测试工具，对HTTP协议提供了出色的支持——这使其成为对任何HTTP服务器进行负载测试的真正不错的选择。

本快速指南将向你展示如何设置一个简单的场景来对HTTP服务器进行负载测试。

Gatling模拟脚本是用Scala编写的，但不用担心 - 该工具会通过 GUI 帮助我们记录场景。一旦我们完成场景记录，GUI 就会创建代表模拟的Scala脚本。

运行模拟后，我们有一个现成的HTML报告。

最后但同样重要的是，Gatling 的架构是异步的。这种架构让我们可以将虚拟用户实现为消息而不是专用线程，从而使它们的资源消耗非常低。因此，运行数千个并发虚拟用户不是问题。

同样值得注意的是，核心引擎实际上是协议不可知的，因此完全有可能实现对其他协议的支持。例如，Gatling 目前也提供JMS支持。

## 2. 使用原型创建项目

尽管我们可以获得.zip格式的Gatling 包，但我们选择使用 Gatling 的Maven Archetype。这使我们能够集成 Gatling 并将其运行到IDE中，并使在版本控制系统中维护项目变得容易。小心，因为 Gatling需要 JDK8。

从命令行键入：

```bash
mvn archetype:generate
```

然后，当出现提示时：

```bash
Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains):
```

类型：

```bash
gatling
```

然后你应该看到：

```bash
Choose archetype:
1: remote -> 
  io.gatling.highcharts:gatling-highcharts-maven-archetype (gatling-highcharts-maven-archetype)
```

类型：

```bash
1
```

选择原型，然后选择要使用的版本(选择最新版本)。

在确认原型创建之前，为类选择groupId、artifactId、版本和包名称。

最后将原型导入 IDE——例如导入[Scala IDE](https://github.com/scala-ide/scala-ide)(基于 Eclipse)或导入[IntelliJ IDEA](https://www.jetbrains.com/idea/)。

## 3.定义场景

在启动记录器之前，我们需要定义一个场景。它将代表用户浏览 Web 应用程序时实际发生的情况。

在本教程中，我们将使用 Gatling 团队提供的应用程序作为示例，托管在 URL [http://computer-database.gatling.io](http://computer-database.gatling.io/)上。

我们的简单场景可能是：

-   用户到达应用程序。
-   用户搜索“amstrad”。
-   用户打开其中一个相关模型。
-   用户返回主页。
-   用户遍历页面。

## 4.配置记录器

首先从 IDE启动Recorder类。启动后，GUI 允许你配置请求和响应的记录方式。选择以下选项：

-   8000作为监听端口
-   org.baeldung.simulation包
-   RecordedSimulation类名
-   关注重定向？检查
-   自动推荐人？检查
-   选择黑名单优先过滤策略
-   ..css , ..js和..ico在黑名单过滤器中

[![环境](https://www.baeldung.com/wp-content/uploads/2016/06/setting-1024x576.png)](https://www.baeldung.com/wp-content/uploads/2016/06/setting-1024x576.png)

 

现在我们必须将浏览器配置为使用在配置期间选择的定义端口 ( 8000 )。这是我们的浏览器必须连接到的端口，以便记录器能够捕获我们的导航。

这是使用 Firefox 的方法，打开浏览器高级设置，然后转到网络面板并更新连接设置：

 

[![代理设置](https://www.baeldung.com/wp-content/uploads/2016/06/settings.png)](https://www.baeldung.com/wp-content/uploads/2016/06/settings.png)

## 5.记录场景

现在一切都已配置，我们可以记录上面定义的场景。步骤如下：

1.  单击“开始”按钮开始录制
2.  访问网站：[http ://computer-database.gatling.io](http://computer-database.gatling.io/)
3.  搜索名称中带有“amstrad”的模型
4.  选择“Amstrad CPC 6128”
5.  返回首页
6.  通过单击“下一步”按钮在模型页面中迭代多次
7.  单击“停止并保存”按钮

Simulation 将在配置期间定义的名为RecordedSimulation.scala的包org.baeldung中生成

## 6. 使用 Maven 运行模拟

要运行我们记录的模拟，我们需要更新我们的pom.xml：

```xml
<plugin>
    <groupId>io.gatling</groupId>
    <artifactId>gatling-maven-plugin</artifactId>
    <version>2.2.4</version>
    <executions>
        <execution>
            <phase>test</phase>
            <goals><goal>execute</goal></goals>
            <configuration> 
                <disableCompiler>true</disableCompiler> 
            </configuration>
        </execution>
    </executions>
</plugin>
```

这让我们在测试阶段执行模拟。要开始测试，只需运行：

```shell
mvn test
```

模拟完成后，控制台将显示 HTML 报告的路径。

注意：使用配置<disableCompiler>true</disableCompiler>是因为我们将使用 Scala 和 maven 这个标志将确保我们不会最终编译我们的模拟两次。[Gatling 文档](https://gatling.io/docs/current/extensions/maven_plugin/#coexisting-with-scala-maven-plugin)中提供了更多详细信息。

## 7. 查看结果

如果我们在建议的位置打开index.html，报告如下所示：

[![报告](https://www.baeldung.com/wp-content/uploads/2016/06/reports-1024x486.png)](https://www.baeldung.com/wp-content/uploads/2016/06/reports-1024x486.png)

## 八、总结

在本教程中，我们探索了使用 Gatling 对 HTTP 服务器进行负载测试。这些工具允许我们在 GUI 界面的帮助下根据定义的场景记录模拟。录制完成后，我们可以启动我们的测试。测试报告将采用 HTML 格式的简历。

为了构建我们的示例，我们选择使用 Maven 原型。这有助于我们集成 Gatling 并将其运行到 IDE 中，并且可以轻松地在版本控制系统中维护项目。