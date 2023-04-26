## 一、概述

在本教程中，我们将探索 JMeter 仪表板报告生成。[JMeter](https://www.baeldung.com/jmeter)是一种用 Java 编写的流行测试工具。我们使用 JMeter 进行负载、性能和压力测试。除了生成丰富的统计数据外，一个重要的功能是以有用的可视化格式显示测试结果。JMeter 正是这样做的，它允许我们生成除多种格式的文本报告之外的仪表板报告。

## 2.先决条件

我们需要一个带有 JMeter maven 插件的 Spring Boot 应用程序。我们已经设置了一个具有三个端点的示例 Spring Boot MVC 应用程序。端点返回一条问候消息、当天的报价和服务器时间。这就是我们运行 JMeter 测试并生成仪表板报告所需的全部内容。

## 3. 运行 JMeter 测试

现在，让我们看看针对我们的应用端点运行 JMeter 测试。

### 3.1. 创建 JMeter 测试计划

**使用 JMeter GUI，我们将生成一个 JMeter 测试计划。**

让我们使用 JMeter GUI创建一个测试计划*ReportsDashboardExample.jmx ：*

![JMeter 图形用户界面](https://www.baeldung.com/wp-content/uploads/2017/12/thread-group-menu-blur-1-1.png)

```xml
${project.basedir}/src/main/resources/dashboard/ReportsDashboardExample.jmx复制
```

除了将我们的测试计划保存在一个文件中，我们还可以将现有的计划加载回我们的 JMeter GUI 中*。*此外，我们可以根据需要对其进行审查和更新。在我们的例子中，我们有一个非常简单的测试计划，足以满足我们的演示目的。

当我们执行测试计划*ReportsDashboardExample.jmx 时，*它会将测试结果生成到 CSV 文件*ReportsDashboardExample.csv*中。

接下来，让我们生成 JMeter 仪表板报告。*JMeter 使用我们在ReportsDashboardExample.csv*文件中提供的测试结果来生成仪表板报告。

### 3.2. 配置 JMeter Maven 插件

JMeter Maven 插件配置很重要：

```xml
<configuration>
    <testFilesDirectory>${project.basedir}/src/main/resources/dashboard</testFilesDirectory>
    <resultsDirectory>${project.basedir}/src/main/resources/dashboard</resultsDirectory>
    <generateReports>true</generateReports>
    <ignoreResultFailures>true</ignoreResultFailures>
    <testResultsTimestamp>false</testResultsTimestamp>
</configuration>复制
```

generateReports元素设置为*true**指示*插件生成仪表板报告。JMeter默认在*target/jmeter*目录下生成报告。但是，我们也可以覆盖默认行为。

### 3.3. 生成仪表板报告

**为了运行 JMeter 测试，我们创建了一个名为\*dashboard\***的 Maven 配置文件。*将名为“ env”*的环境变量设置为“ *dash”*映射 的值 并激活仪表板配置文件：

```xml
...
<profile>
    <id>dashboard</id>
    <activation>
        <property>
            <name>env</name>
            <value>dash</value>
        </property>
    </activation>
...复制
```

使用此配置文件运行代码的 Maven 命令是：

```shell
mvn clean install -Denv=dash复制
```

虽然我们可以更改全局设置，但设置单独的配置文件会隔离我们的特定依赖项、插件和配置。*这使我们能够避免触及pom.xml*中的任何其他配置文件和全局部分。

### 3.4. 查看仪表板报告

在测试运行期间，生成的日志为我们提供了报告目标路径以及其他信息：

```plaintext
[INFO] Will generate HTML report in [PATH_TO_REPORT]复制
```

从此路径打开*index.html ，我们得到仪表板视图：*

[![JMeter 仪表板报告](https://www.baeldung.com/wp-content/uploads/2023/02/dashboard-300x295.jpg)](https://www.baeldung.com/wp-content/uploads/2023/02/dashboard.jpg)

这个仪表板以一种很好的格式为三个端点中的每一个提供了我们测试的统计数据。相应的图表支持我们的表格数据。饼图都是绿色的，这表明我们所有的测试都是成功的。但是，我们也可以引入一些错误以使其更真实。例如，我们可以创建一个指向不存在端点的*HTTP 请求采样器。*因此，这也会在饼图中引入红色区域。

我们的仪表板报告生成练习到此结束。接下来，我们来看看我们的项目配置。

## 4. Maven 目标

我们的目标之一是在我们的测试环境中运行示例应用程序。因此，我们的 JMeter 测试能够使用我们本地测试环境中的目标端点。让我们深入了解相应的*pom.xml*配置。

### 4.1. Spring Boot Maven 插件

在我们的例子中，我们希望 Maven 目标将 Spring Boot 应用程序作为守护进程运行。因此，我们使用*spring-boot-maven-plugin的**启动*和*停止*目标。此外，这两个目标包装了来自 JMeter Maven 插件的目标。

Spring Boot Maven 插件*启动*目标保持 Web 服务器运行，直到我们停止它：

```xml
<execution>
    <id>launch-web-app</id>
    <goals>
        <goal>start</goal>
    </goals>
    <configuration>
        <mainClass>com.baeldung.dashboard.DashboardApplication</mainClass>
    </configuration>
</execution>复制
```

我们的 Maven 配置文件中的最后一个目标是相应的*停止*目标：

```xml
<execution>
    <id>stop-web-app</id>
    <goals>
        <goal>stop</goal>
    </goals>
</execution>复制
```

### 4.2. JMeter Maven 插件

我们将来自 JMeter Maven 插件的目标包装在 Spring Boot*启动*和*停止*目标之间。我们希望在 JMeter 测试完成执行时保持 Spring Boot 应用程序运行。我们的*pom.xml*文件定义了*jmeter-maven-plugin的**configure*、*jmeter*和*results*目标。*此外，带有 id jmeter-tests 的*执行执行两个目标：*jmeter*目标和*结果*目标：

```xml
...
<execution>
    <id>jmeter-tests</id>
    <goals>
        <goal>jmeter</goal>
        <goal>results</goal>
    </goals>
</execution>
...复制
```

**在某些情况下，如果发生错误，最后停止服务器的目标将无法执行，从而导致 Web 服务器永远运行。但是，我们可以手动停止 Web 服务器。我们必须找出我们的 Spring Boot 应用程序的进程 ID，然后从我们的命令行或[Bash](https://www.baeldung.com/linux/kill-commands) shell 手动终止进程。**

## 5.结论

在本文中，我们了解了 JMeter 仪表板报告的生成。获取可视化报告始终是一种比纯文本分析数据更有用、更高效、更简单的方法。

在我们的例子中，我们使用 JMeter 来测试网络端点。JMeter 还涵盖其他用例。一些示例是测试 RESTful 服务、数据库和消息服务。

我们还可以添加断言来创建通过/失败标准。JMeter GUI 提供了一个更简单的界面来构建您的测试计划。然而，在生产中，我们对 JMeter 使用非 GUI 模式，因为 GUI 模式是资源密集型的。

我们还可以使用资源[集群](https://www.baeldung.com/jmeter-distributed-testing)来运行具有更大负载的 JMeter 测试。这是 JMeter 大规模测试的典型配置。