## 一、简介

在本教程中，我们将了解如何使用[Gatling](https://gatling.io/)进行分布式性能测试。在此过程中，我们将创建一个简单的应用程序来使用 Gatling 进行测试，了解使用分布式性能测试的基本原理，最后，了解 Gatling 中可以提供哪些支持来实现它。

## 2. Gatling 性能测试

性能测试是**评估系统在一定工作负载下的响应能力和稳定性的测试实践**。有几种类型的测试通常属于性能测试。这些包括负载测试、压力测试、浸泡测试、尖峰测试等。所有这些都有自己要实现的特定目标。

然而，任何性能测试的一个共同方面是模拟工作负载，而[Gatling](https://www.baeldung.com/introduction-to-gatling)、[JMeter](https://www.baeldung.com/jmeter)和[K6 等](https://k6.io/)工具可以帮助我们做到这一点。但是，在我们进一步进行之前，我们需要一个可以测试性能的应用程序。

然后，我们将为该应用程序的性能测试开发一个简单的工作负载模型。

### 2.1. 创建应用程序

对于本教程，**我们将**使用 Spring CLI 创建一个简单的 Spring Boot Web 应用程序：

```powershell
spring init --dependencies=web my-application复制
```

接下来，我们将创建一个简单的 REST API，根据请求提供随机数：

```java
@RestController
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/api/random")
    public Integer getRandom() {
        Random random = new Random();
        return random.nextInt(1000);
    }
}复制
```

这个 API 没有什么特别之处——它只是在每次调用时返回一个 0 到 999 范围内的随机整数。

使用 Maven 命令启动这个应用程序非常简单：

```powershell
mvnw spring-boot:run复制
```

### 2.2. 创建工作负载模型

如果我们需要将这个简单的 API 部署到生产中，我们需要确保它能够处理预期的负载并仍然提供所需的服务质量。这是我们需要进行各种性能测试的地方。工作负载模型**通常会识别一个或多个工作负载配置文件以模拟现实生活中的使用情况**。

对于具有用户界面的 Web 应用程序，定义适当的工作负载模型可能非常具有挑战性。但是对于我们简单的 API，我们可以假设负载测试的负载分布。

Gatling 提供**Scala DSL 来创建场景以在模拟中进行测试**。让我们首先为我们之前创建的 API 创建一个基本场景：

```scala
package randomapi

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class RandomAPILoadTest extends Simulation {
    val protocol: HttpProtocolBuilder = http.baseUrl("http://localhost:8080/")
    val scn: ScenarioBuilder = scenario("Load testing of Random Number API")
      .exec(
        http("Get Random Number")
          .get("api/random")
          .check(status.is(200))
      )

    val duringSeconds: Integer = Integer.getInteger("duringSeconds", 10)
    val constantUsers: Integer = Integer.getInteger("constantUsers", 10)
    setUp(scn.inject(constantConcurrentUsers(constantUsers) during (duringSeconds))
      .protocols(protocol))
      .maxDuration(1800)
      .assertions(global.responseTime.max.lt(20000), global.successfulRequests.percent.gt(95))
}复制
```

让我们讨论一下这个基本模拟中的要点：

-   我们首先添加一些必要的 Gatling DSL 导入
-   接下来我们定义HTTP协议配置
-   然后，我们通过对 API 的单个请求定义一个场景
-   最后，我们为要注入的负载创建一个模拟定义；在这里，我们使用 10 个并发用户注入负载 10 秒

为具有用户界面的更复杂的应用程序创建这种场景可能非常复杂。值得庆幸的是，Gatling 附带了另一个[实用程序，称为记录器](https://www.baeldung.com/introduction-to-gatling)。**使用这个记录器，我们可以通过让它**代理浏览器和服务器之间的交互来创建场景。它还可以使用[HAR（HTTP 存档）](https://w3c.github.io/web-performance/specs/HAR/Overview.html)文件来创建场景。

### 2.3. 执行模拟

现在，我们已准备好执行负载测试。为此，我们可以将模拟文件“RandomAPILoadTest.scala”放在目录*“%GATLING_HOME%/user-file/randomapi/”中。*请注意，这不是执行模拟的唯一方法，但它肯定是最简单的方法之一。

我们可以通过运行命令来启动 Gatling：

```powershell
$GATLING_HOME/bin/gatling.sh复制
```

这将提示我们选择要运行的模拟：

```powershell
Choose a simulation number:
     [0] randomapi.RandomAPILoadTest复制
```

选择模拟后，它将运行模拟并生成带有摘要的输出：

[![加特林输出](https://www.baeldung.com/wp-content/uploads/2021/02/Gatling-Output-1.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/Gatling-Output-1.jpg)

此外，它会在“%GATLING_HOME%/results”目录中生成 HTML 格式的报告：

[![加特林报告](https://www.baeldung.com/wp-content/uploads/2021/02/Gatling-Report-1024x329.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/Gatling-Report.jpg)

这只是**生成的报告的一部分，但我们可以清楚地看到**结果的摘要。这非常详细且易于遵循。

## 3.分布式性能测试

到目前为止，一切都很好。但是，如果我们回想一下，性能测试的目的是模拟现实生活中的工作负载。对于流行的应用程序，这**可能比我们在此处的普通案例中看到的负载要高得多**。如果我们在测试摘要中注意到，我们设法实现了大约 500 个请求/秒的吞吐量。对于现实生活中的应用程序，处理现实生活中的工作负载，这可能要高很多倍！

我们如何使用任何性能工具模拟这种工作负载？仅从一台机器注入负载真的有可能实现这些数字吗？也许不是。即使负载注入工具可以处理更高的负载，**底层操作系统和网络也有其自身的局限性**。

这是我们必须在多台机器上分配负载注入的地方。当然，与任何其他分布式计算模型一样，这也有其自身的挑战：

-   我们如何在参与的机器之间分配工作负载？
-   谁协调他们的完成和从可能发生的任何错误中恢复？
-   我们如何收集和汇总合并报告的结果？

分布式性能测试的典型架构使用主节点和从节点来解决其中的一些问题：

[![加特林分布式测试](https://www.baeldung.com/wp-content/uploads/2021/02/Gatling-Distributed-Testing.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/Gatling-Distributed-Testing.jpg)

但是，又一次，如果主人崩溃了怎么办？[解决分布式计算的所有问题](https://www.baeldung.com/cs/distributed-systems-guide)不在本教程的范围内，但在选择分布式模型进行性能测试时，我们一定要强调它们的含义。

## 4. 使用 Gatling 进行分布式性能测试

现在我们已经了解了分布式性能测试的必要性，我们将看看如何使用 Gatling 来实现这一点。集群**模式是 Gatling Frontline 的内置功能**。但是，Frontline 是 Gatling 的企业版，不能作为开源软件使用。Frontline 支持在本地或任何流行的云供应商上部署注入器。

尽管如此，**仍然可以使用 Gatling open-source 来实现这一点**。但是，我们必须自己完成大部分繁重的工作。我们将在本节中介绍实现它的基本步骤。在这里，我们将使用我们之前定义的相同模拟来生成多机负载。

### 4.1. 设置

我们将从**创建一台控制器机器和几台远程工作者机器**开始，这些机器可以是本地的，也可以是在任何云供应商上。我们必须在所有这些机器上执行某些先决条件。其中包括在所有 worker 机器上安装 Gatling open-source 并设置一些 controller 机器环境变量。

为了获得一致的结果，我们应该在所有 worker 机器上安装相同版本的 Gatling，并在每台机器上使用相同的配置。这包括我们安装 Gatling 的目录以及我们创建的用于安装它的用户。

让我们看看我们需要在控制器机器上设置的重要环境变量：

```shell
HOSTS=( 192.168.x.x 192.168.x.x 192.168.x.x)复制
```

让我们还定义我们将用于从以下位置注入负载的远程工作者机器列表：

```powershell
GATLING_HOME=/gatling/gatling-charts-highcharts-1.5.6
GATLING_SIMULATIONS_DIR=$GATLING_HOME/user-files/simulations
SIMULATION_NAME='randomapi.RandomAPILoadTest'
GATLING_RUNNER=$GATLING_HOME/bin/gatling.sh
GATLING_REPORT_DIR=$GATLING_HOME/results/
GATHER_REPORTS_DIR=/gatling/reports/复制
```

一些变量指向 Gatling 安装目录和我们需要启动模拟的其他脚本。它还提到了我们希望生成报告的目录。我们稍后会看到在哪里使用它们。

请务必注意，**我们假设机器具有类似 Linux 的环境**。但是，我们可以轻松地为 Windows 等其他平台调整该过程。

### 4.2. 分配负载

在这里，我们将**相同的场景复制到**我们之前创建的多台工作机器。可以通过多种方式将模拟复制到远程主机。最简单的方法是对支持的主机使用[*scp*](https://www.ssh.com/ssh/scp/)。我们还可以使用 shell 脚本自动执行此操作：

```shell
for HOST in "${HOSTS[@]}"
do
  scp -r $GATLING_SIMULATIONS_DIR/* $USER_NAME@$HOST:$GATLING_SIMULATIONS_DIR
done复制
```

上面的命令将本地主机上的目录内容复制到远程主机上的目录。对于 Windows 用户，[PuTTY](https://www.putty.org/)是更好的选择，它还附带了 PSCP（PuTTY 安全复制协议）。我们可以使用PSCP在 Windows 客户端和 Windows 或 Unix 服务器之间[传输文件。](https://www.ssh.com/ssh/putty/putty-manuals/0.68/Chapter5.html)

### 4.3. 执行模拟

一旦我们将模拟复制到工作机器，我们就可以触发它们了。实现并发用户总数的关键是**几乎同时在所有主机上执行模拟**。

我们可以再次使用 shell 脚本自动执行此步骤：

```shell
for HOST in "${HOSTS[@]}"
do
  ssh -n -f $USER_NAME@$HOST \
    "sh -c 'nohup $GATLING_RUNNER -nr -s $SIMULATION_NAME \
    > /gatling/run.log 2>&1 &'"
done复制
```

我们正在使用*ssh*来触发远程工作者机器上的模拟。这里要注意的关键点是**我们使用的是“无报告”选项**(-nr)。这是因为我们只对收集现阶段的日志感兴趣，稍后我们将通过合并来自所有工作机器的日志来创建报告。

### 4.4. 收集结果

现在，我们需要**收集所有工作机器上模拟产生的日志文件**。同样，我们可以使用 shell 脚本自动化并从控制器机器执行：

```shell
for HOST in "${HOSTS[@]}"
do
  ssh -n -f $USER_NAME@$HOST \
    "sh -c 'ls -t $GATLING_REPORT_DIR | head -n 1 | xargs -I {} \
    mv ${GATLING_REPORT_DIR}{} ${GATLING_REPORT_DIR}report'"
  scp $USER_NAME@$HOST:${GATLING_REPORT_DIR}report/simulation.log \
    ${GATHER_REPORTS_DIR}simulation-$HOST.log
done复制
```

对于我们这些不熟悉 shell 脚本的人来说，这些命令可能看起来很复杂。但是，当我们将它们分成几部分时，它并不那么复杂。首先，我们*ssh*到远程主机，按时间倒序列出Gatling报告目录下的所有文件，取第一个文件。

然后，我们将选定的日志文件从远程主机复制到控制器机器并重命名以附加主机名。这很重要，因为我们将有多个来自不同主机的同名日志文件。

### 4.5. 生成报告

最后，我们必须**根据在不同工作机器上执行的模拟收集的所有日志文件生成报告**。值得庆幸的是，加特林在这里完成了所有繁重的工作：

```shell
mv $GATHER_REPORTS_DIR $GATLING_REPORT_DIR
$GATLING_RUNNER -ro reports复制
```

我们将所有日志文件复制到标准的 Gatling 报告目录中，并执行 Gating 命令生成报告。这假设我们也在控制器机器上安装了 Gatling。最终报告与我们之前看到的类似：

[![加特林报告合并](https://www.baeldung.com/wp-content/uploads/2021/02/Gatling-Report-Combined-1024x329-1.jpg)](https://www.baeldung.com/wp-content/uploads/2021/02/Gatling-Report-Combined-1024x329-1.jpg)

在这里，我们甚至没有意识到负载实际上是从多台机器注入的！我们可以清楚地看到，当我们使用三台 worker 机器时，请求的数量几乎增加了两倍。但是，在现实生活中，缩放比例不会是完美的线性！

## 5. 扩展性能测试的注意事项

我们已经看到，分布式性能测试是一种扩展性能测试以模拟真实工作负载的方法。现在，虽然分布式性能测试很有用，但它确实有其细微差别。因此，**我们绝对应该尝试尽可能垂直地扩展负载注入能力**。只有当我们达到单机的垂直极限时，我们才考虑使用分布式测试。

通常，在机器上扩展负载注入的限制因素来自底层操作系统或网络。我们可以优化某些事情以使其变得更好。在类似 Linux 的环境中，**负载注入器可以产生的并发用户数通常受打开文件限制的限制**。我们可以考虑使用[*ulimit*命令](https://ss64.com/bash/ulimit.html)增加它。

另一个重要因素涉及机器上可用的资源。例如，负载注入通常会消耗大量网络带宽。如果机器的网络吞吐量是限制因素，我们可以考虑升级它。同样，机器上可用的 CPU 或内存可能是其他限制因素。**在基于云的环境中，切换到功能更强大的机器相当容易**。

最后，我们在模拟中包含的场景应该具有弹性，因为我们不应该假设总是在负载下做出积极响应。因此，我们在写关于响应的断言时应该小心和防御。此外，**我们应该将断言的数量保持在最低限度**，以节省增加吞吐量的工作量。

## 六，结论

在本教程中，我们了解了使用 Gatling 执行分布式性能测试的基础知识。我们创建了一个简单的应用程序来测试，用 Gatling 开发了一个简单的模拟，然后了解了我们如何从多台机器上执行它。

在这个过程中，我们也了解了分布式性能测试的必要性以及与之相关的最佳实践。