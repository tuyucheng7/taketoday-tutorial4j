## 1. 概述

在本教程中，我们将介绍 Jenkins 架构的基础知识。此外，我们将学习如何配置 Jenkins 以提高性能。此外，我们将讨论手动重启或关闭 Jenkins 的选项。

## 2. 詹金斯架构

一台 Jenkins 服务器无法满足某些需求。首先，我们可能需要多个不同的环境来测试我们的构建。单个 Jenkins 服务器无法做到这一点。其次，如果定期生产更大更重的项目，单个 Jenkins 服务器将不堪重负。

Jenkins分布式架构就是为了满足上述需求而产生的。此外，Jenkins 使用主从架构管理分布式构建。本设计中使用[TCP/IP 协议在 Master 和 Slave 之间进行通信。](https://www.baeldung.com/cs/udp-vs-tcp)

### 2.1. 詹金斯大师

Jenkins master 负责调度作业、分配 slaves 并将构建发送给 slaves 以执行作业。它还会跟踪从属状态(离线或在线)并检索来自从属的构建结果响应并将它们显示在控制台输出上。

### 2.2. 詹金斯奴隶

它在远程服务器上运行。Jenkins 服务器遵循 Jenkins master 的请求并兼容所有操作系统。master 派发的构建作业由 slave 执行。此外，项目可以配置为选择特定的从机。

### 2.3. 分布式主从架构

让我们用一个例子来看看 Jenkins 架构。下图描述了一个 master 和三个 Jenkins slave：

[![詹金斯架构 1](https://www.baeldung.com/wp-content/uploads/2021/06/Jenkins-Architecture-1.svg)](https://www.baeldung.com/wp-content/uploads/2021/06/Jenkins-Architecture-1.svg)

让我们看看我们如何利用 Jenkins 在各种系统(例如 Ubuntu、Windows 或 Mac)中进行测试：

[![代码提交](https://www.baeldung.com/wp-content/uploads/2021/06/code-commit.svg)](https://www.baeldung.com/wp-content/uploads/2021/06/code-commit.svg)

在图中，需要处理以下项目：

-   Jenkins 会定期检查 GIT 存储库中源代码的任何更改
-   每个 Jenkins 构建都需要自己的测试环境，不能在单个服务器上创建。詹金斯通过根据需要使用各种奴隶来实现这一点
-   Jenkins Master会向这些slave传达测试请求，以及测试报告

## 3. 詹金斯 CLI

Jenkins 有一个命令行界面，用户和管理员可以使用它从脚本或 shell 环境访问 Jenkins。SSH、Jenkins CLI 客户端或 Jenkins 附带的 JAR 文件可以使用此命令行界面。

为此，我们必须首先从位于 URL /jnlpJars/jenkins-cli.jar 的 Jenkins 控制器下载jenkins-cli.jar，实际上是JENKINS_URL/jnlpJars/jenkins-cli.jar，然后按如下方式运行它：

```bash
java -jar jenkins-cli.jar -s http://localhost:8080/ -webSocket help复制
```

通过从“管理 Jenkins”页面的“工具和操作”部分选择“Jenkins CLI”，可以使用此选项：

[![詹金斯 CLI 命令](https://www.baeldung.com/wp-content/uploads/2021/06/Jenkins-CLI-Commands-1024x472.png)](https://www.baeldung.com/wp-content/uploads/2021/06/Jenkins-CLI-Commands.png)

此区域显示可用命令列表。我们可以使用命令行工具来访问使用这些命令的各种功能。

## 4.手动重启Jenkins

如果我们希望手动重启或关闭 Jenkins，只需按照以下步骤操作：

### 4.1. 重新开始

我们可以使用 Jenkins Rest API 执行重启。这将强制程序重新启动而不等待现有作业完成：

```http
http://(jenkins_url)/restart
复制
```

我们可以使用 Jenkins Rest API执行safeRestart 。这使我们能够完成任何现有任务：

```http
http://(jenkins_url)/safeRestart复制
```

如果我们将它安装为rpm或deb包，下面的命令将起作用：

```bash
service jenkins restart
复制
```

### 4.2. Ubuntu

我们还可以使用 apt-get/dpkg安装以下内容：

```bash
sudo /etc/init.d/jenkins restart
Usage: /etc/init.d/jenkins {start|stop|status|restart|force-reload}
复制
```

### 4.3. 安全关闭 Jenkins

如果我们希望安全地关闭 Jenkins，我们可以使用 Jenkins Rest API 执行退出：

```http
http://(jenkins_url)/exit复制
```

我们可以使用 Jenkins Rest API 执行 kill 来终止我们所有的进程：

```http
http://(jenkins_url)/kill复制
```

## 5. 提升 Jenkins 的性能

滞后或响应速度慢是 Jenkins 用户的典型抱怨，并且有许多故障报告。缓慢的 CI 系统很不方便，因为它们会减慢开发速度并浪费时间。使用一些简单的建议，我们可以提高这些系统的性能。

在接下来的部分中，我们将讨论一些改进 Jenkins 并让我们的工程师开心的建议。

### 5.1. 最小化主节点上的构建

主节点是应用程序实际执行的地方；它是 Jenkins 的大脑，与奴隶不同，它无法被替换。因此，我们希望让我们的 Jenkins master 尽可能“无工作”，使用 CPU 和 RAM 来调度和触发 slave 上的构建。我们可以通过将我们的作业限制在一个节点标签上来实现这一点，例如SlaveNode。

在为管道作业分配节点时，使用标签，例如：

```groovy
stage("stage 1"){
    node("SlaveNode"){
        sh "echo \"Hello ${params.NAME}\" "
    }
}
复制
```

 在这些情况下，任务和节点块将仅在具有SlaveNode标签的从站上运行。

### 5.2. 不要保留太多构建历史

在配置作业时，我们可以指定要在文件系统上保留多少构建以及多长时间。当我们在短时间内触发作业的许多构建时，这个称为丢弃旧构建的功能就会变得有用。

我们已经看到历史限制设置得太高的例子，导致保存过多的构建。此外，在这种情况下，Jenkins 不得不加载大量旧版本。

### 5.3. 清除旧的詹金斯数据

继续之前关于构建数据的建议，另一个需要了解的关键要素是旧数据管理功能。正如我们所知，Jenkins 管理作业并将数据存储在文件系统上。当我们采取升级核心、安装或更新插件等操作时，数据格式可能会发生变化。

Jenkins 将旧的数据格式保存到文件系统，并在这种情况下将新格式加载到内存中。如果我们需要回滚升级，这是非常有益的，但有时会将太多数据加载到 RAM 中。缓慢的 UI 响应速度甚至 OutOfMemory 问题都是高内存使用的迹象。建议打开之前的数据管理页面，避免出现此类情况：

[![管理旧数据](https://www.baeldung.com/wp-content/uploads/2021/06/Manage-old-data-1024x476.png)](https://www.baeldung.com/wp-content/uploads/2021/06/Manage-old-data.png)

### 5.4. 定义正确的堆大小

许多当前的 Java 应用程序都使用最大堆大小选项。在定义堆大小时，有一个重要的 JVM 方面需要注意。[UseCompressedOops](https://www.baeldung.com/jvm-compressed-oops) 是此功能的名称，它仅适用于我们大多数人使用的 64 位平台。它将对象的指针从 64 位减少到 32 位，从而节省了大量内存。

默认情况下，此标志在最大 32GB(稍小)的堆上启用，并且它停止在大于该大小的堆上工作。堆应该扩展到 48GB 以补偿损失的容量。因此，在定义堆大小时，建议将其保持在 32GB 以下。

我们可以使用以下命令(jinfo包含在 JDK 中)来查看是否设置了标志：

```bash
jinfo -flag UseCompressedOops <pid>复制
```

### 5.5. 调整垃圾收集器

垃圾[收集器](https://www.baeldung.com/jvm-garbage-collectors)是一个在后台运行的内存管理系统。

它的主要目标是在堆中定位未使用的对象并释放它们包含的内存。Java 应用程序可能会由于某些 GC 操作而停止(还记得 UI 冻结吗？)。如果我们的应用程序有一个巨大的堆(超过 4GB)，这很可能发生。为了减少这些情况下的延迟时间，需要进行 GC 优化。在处理多个 Jenkins 设置中的这些挑战之后，我们提出了以下建议：

-   启用[G1GC](https://www.baeldung.com/jvm-garbage-collectors) — 最新的 GC 实现(JDK9 上的默认设置)
-   启用[GC 日志记录](https://www.baeldung.com/java-gc-logging-to-file)——这将有助于将来的监控和调整
-   如有必要，为 GC 配置额外的标志
-   持续监控

## 六，总结

在本快速教程中，我们首先讨论了 Jenkins 中的分布式主从架构。之后，我们查看了几个手动启动、停止和重启 Jenkins 的选项。最后，我们探索了 Jenkins 中的不同配置以提高性能。

如果我们花一些时间在 Jenkins 上并遵循这些指南，我们将能够利用它的许多有用功能，同时避免潜在的危险。