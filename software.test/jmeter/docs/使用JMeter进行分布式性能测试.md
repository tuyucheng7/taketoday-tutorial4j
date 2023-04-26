## 一、概述

在本文中，我们将探索[使用 JMeter 进行分布式性能测试](https://www.baeldung.com/jmeter)。

## 2. 什么是分布式性能测试？

**分布式性能测试是指使用主从配置的多个系统来测试Web应用程序或服务器的性能。**

在此过程中，我们将使用一个本地客户端作为主机，使用多个远程客户端处理测试执行，每个远程客户端作为从机将在我们的目标服务器上执行测试。

**每个从系统都按照主系统设置的确切条件执行负载测试。**因此，分布式性能测试有助于我们实现更高数量的并发用户请求目标服务器。

简单来说，使用 JMeter 进行分布式性能测试的大纲如下所示：

[![jmeter分布式](https://www.baeldung.com/wp-content/uploads/2020/11/jmeter_distributed.png)](https://www.baeldung.com/wp-content/uploads/2020/11/jmeter_distributed.png)

## 3.设置

### 3.1. 先决条件

为了顺利设置和测试运行，我们应该遵循一些先决条件：

-   多台计算机，每台计算机上都安装了 JMeter
-   系统上的防火墙已关闭，或所需端口已打开以进行连接
-   所有系统（主/从）都在同一个子网上
-   每个系统上的JMeter都可以访问目标服务器
-   在所有系统（主从）上使用相同版本的 Java 和 JMeter
-   为简单起见，禁用 RMI 的 SSL

现在我们已经准备好系统，让我们配置从属系统和主系统。

### 3.2. 配置从系统

在从属系统上，我们将转到*jmeter/bin*目录并在 Windows 上执行*jmeter-server.bat文件。*或者，我们可以在 Unix 上运行*jmeter-server*文件。

### 3.3. 配置主系统

在主系统上，我们将转到*jmeter/bin*目录并编辑*jmeter.properties*文件中的*remote_hosts*属性以添加从属系统的 IP 地址（以逗号分隔）：

```plaintext
remote_hosts=192.165.0.10,192.165.0.20,192.165.0.30复制
```

在这里，我们添加了三个从属系统。

*因此，通过在 GUI 模式下启动 JMeter（master），我们可以确认Run > Remote Start*选项中列出的所有 slaves ：

[![jmeter 奴隶](https://www.baeldung.com/wp-content/uploads/2020/11/jmeter-slaves.png)](https://www.baeldung.com/wp-content/uploads/2020/11/jmeter-slaves.png)

 

就是这样！我们已准备好启动 JMeter 主系统以使用多个客户端在目标服务器上执行测试。

## 4.远程测试

**对于远程测试，为了简单起见，我们可以在 GUI 模式下运行 JMeter**。[但是，我们在进行实际测试时](https://www.baeldung.com/jmeter#jmeter-nongui)应该使用 CLI 模式运行它。

首先，我们将在主系统中创建一个简单的测试计划，其中包含用于请求我们的 baeldung.com 服务器的*HTTP 请求*采样器和一个*查看结果树*侦听器。

### 4.1. 启动一个单一的奴隶

*然后，我们可以使用Run > Remote Start*选项选择使用 GUI 模式运行的从属系统：

[![jmeter 启动奴隶](https://www.baeldung.com/wp-content/uploads/2020/11/jmeter-start-slave.png)](https://www.baeldung.com/wp-content/uploads/2020/11/jmeter-start-slave.png)

### 4.2. 启动所有奴隶

*同样，我们可以使用Run > Remote Start All*选项来选择运行所有从系统：

[![jmeter全部启动](https://www.baeldung.com/wp-content/uploads/2020/11/jmeter-start-all.png)](https://www.baeldung.com/wp-content/uploads/2020/11/jmeter-start-all.png)

此外，还有一些选项可用于处理从属系统上的测试执行，例如*Remote Stop*、*Remote Stop All*和*Remote Shutdown All 。*

### 4.3. 检测结果

最后，一旦测试执行完成，我们就可以在本地 JMeter（master）中看到测试结果：

[![jmeter 主结果](https://www.baeldung.com/wp-content/uploads/2020/11/jmeter-master-results.png)](https://www.baeldung.com/wp-content/uploads/2020/11/jmeter-master-results.png)

此外，在远程 JMeter 系统（从属）上，我们可以找到有关测试执行开始/停止的日志：

```powershell
Starting the test on host 192.165.0.10 @ Sun Oct 25 17:50:21 EET 2020
Finished the test on host 192.165.0.10 @ Sun Oct 25 17:50:25 EET 2020复制
```

## 5.结论

在本快速教程中，我们了解了如何开始使用 JMeter 进行分布式性能测试。

首先，我们查看了顺利设置和测试运行的一些先决条件。然后，我们为分布式性能测试环境配置了从属系统和主系统。

最后，我们启动从属系统，从主系统运行测试，并观察结果。