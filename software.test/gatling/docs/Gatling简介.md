## 1. 概述

Gatling是一种**负载测试工具**，对HTTP协议提供了出色的支持-这使其成为对任何HTTP服务器进行负载测试的非常好的选择。

本快速指南将向你展示如何**设置一个简单的场景**来对HTTP服务器进行负载测试。

**Gatling模拟脚本是用Scala编写的**，但不用担心-该工具会通过GUI帮助我们记录场景。一旦我们完成了场景记录，GUI就会创建代表模拟的Scala脚本。

运行模拟后，我们有一个现成的**HTML报告**。

最后但同样重要的是，Gatling的架构是**异步**的。这种架构让我们可以将虚拟用户实现为消息而不是专用线程，从而使它们的资源消耗非常低。因此，运行数千个并发虚拟用户不是问题。

同样值得注意的是，核心引擎实际上是与**协议无关**的，因此完全有可能实现对其他协议的支持。例如，Gatling目前也提供JMS支持。

## 2. 使用原型创建项目

尽管我们可以获得.zip格式的Gatling包，但我们选择使用Gatling的Maven原型。这使我们能够集成Gatling并将其运行到IDE中，并使在版本控制系统中维护项目变得容易。小心，因为Gatling**需要JDK 8**。

在命令行中，输入：

```shell
mvn archetype:generate
```

然后，当出现提示时：

```shell
Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains):
```

类型：

```shell
gatling
```

然后你应该看到：

```shell
Choose archetype:
1: remote -> io.gatling.highcharts:gatling-highcharts-maven-archetype (gatling-highcharts-maven-archetype)
```

类型：

```shell
1
```

选择原型，然后选择要使用的版本(选择最新版本)。

在确认原型创建之前，为类选择groupId、artifactId、version和包名称。

最后将原型导入IDE-例如导入[Scala IDE](https://github.com/scala-ide/scala-ide)(基于Eclipse)或导入[IntelliJ IDEA](https://www.jetbrains.com/idea/)。

## 3. 定义场景

在启动记录器之前，我们需要**定义一个场景**。它将代表用户浏览Web应用程序时实际发生的情况。

在本教程中，我们将使用Gatling团队提供的应用程序作为示例，托管在URL [http://computer-database.gatling.io](http://computer-database.gatling.io/)上。

我们的简单场景可能是：

-   用户到达应用程序
-   用户搜索“amstrad”
-   用户打开其中一个相关型号
-   用户返回主页
-   用户遍历页面

## 4. 配置记录器

首先从IDE启动Recorder类。启动后，GUI允许你配置请求和响应的记录方式。选择以下选项：

-   8000作为监听端口
-   cn.tuyucheng.taketoday.simulation包
-   RecordedSimulation类名
-   选择Follow Redirects?
-   选择Automatic Referers?
-   选择黑名单优先过滤策略
-   黑名单过滤器中的.\*\\.css、.\*\\.js和.\*\\.ico

<img src="../assets/img.png">

现在我们必须将浏览器配置为使用在配置期间选择的定义端口(8000)。这是我们的浏览器必须连接到的端口，以便记录器能够捕获我们的导航。

以下是使用Firefox的方法，打开浏览器高级设置，然后转到“Network”面板并更新连接设置：

<img src="../assets/img_1.png">

## 5. 记录场景

现在一切都配置好了，我们可以记录上面定义的场景。步骤如下：

1.  单击“Start”按钮开始录制
2.  访问网站：[http://computer-database.gatling.io](http://computer-database.gatling.io/)
3.  搜索名称中带有“amstrad”的型号
4.  选择“Amstrad CPC 6128”
5.  返回首页
6.  通过单击“Next”按钮多次遍历模型页面
7.  单击“Stop & save”按钮

Simulation将在配置期间定义的cn.tuyucheng.taketoday包中生成，名为RecordedSimulation.scala。

## 6. 使用Maven运行模拟

要运行我们记录的模拟，我们需要更新我们的pom.xml：

```xml
<plugin>
    <groupId>io.gatling</groupId>
    <artifactId>gatling-maven-plugin</artifactId>
    <version>2.2.4</version>
    <executions>
        <execution>
            <phase>test</phase>
            <goals>
                <goal>execute</goal>
            </goals>
            <configuration>
                <disableCompiler>true</disableCompiler>
            </configuration>
        </execution>
    </executions>
</plugin>
```

这让我们在test阶段执行模拟。要开始测试，只需运行：

```shell
mvn test
```

模拟完成后，控制台将显示HTML报告的路径。

注意：使用配置<disableCompiler\>true</disableCompiler\>是因为我们将使用Scala和Maven，这个标志将确保我们不会最终编译我们的模拟两次。有关更多详细信息，请参阅[Gatling文档](https://gatling.io/docs/current/extensions/maven_plugin/#coexisting-with-scala-maven-plugin)。

## 7. 查看结果

如果我们在建议的位置打开index.html，报告如下所示：

<img src="../assets/img_2.png">

## 8. 总结

在本教程中，我们探讨了使用Gatling对HTTP服务器进行负载测试。这些工具允许我们在GUI界面的帮助下根据定义的场景记录模拟。录制完成后，我们可以启动测试。测试报告将采用HTML格式的简历。

为了构建我们的示例，我们选择使用Maven原型。这有助于我们集成Gatling并将其运行到IDE中，并且可以轻松地在版本控制系统中维护项目。