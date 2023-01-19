随着微服务的趋势越来越大，并行构建和部署多个服务正在成为每个组织的需求。同样，由于自动化测试的增加，并行执行测试以减少整体执行时间变得至关重要。这是 CI 工具需要提供一种机制的地方，使用它可以在各种机器/服务器之间共享负载。Jenkin's 还提供了在大量机器上分发构建作业( Jenkins distributed builds )的强大功能。使用[Jenkins](https://www.toolsqa.com/jenkins/what-is-jenkins/)，我们可以建立一个构建服务器场来分担负载或在各种环境中运行构建作业。

这种能力还可以帮助我们配置 Jenkins 以触发分布式构建，从而提高整个系统的效率和性能。但是，首先，让我们通过涵盖以下主题中提到的细节来了解 Jenkin 用于调度和处理分布式构建的整体功能：

-   什么是分布式构建？
    -   为什么需要分布式构建？
-   了解 Jenkins 分布式架构。
    -   Jenkins大师
    -   Jenkins奴隶
    -   如何在 Jenkins 中设置从站/节点？
    -   如何使用 Jenkins 中的分布式设置运行构建？

## 什么是分布式构建？

在构建系统中，可能会出现当前机器数量不足的情况，我们可能需要更多的机器来承担额外的负载。此外，我们可能需要额外的机器来在特定环境中运行特定任务。所有这些情况都需要在构建过程中的某个时间点添加更多的机器。那么我们如何实现呢？答案是分布式构建。

分布式构建通过在需要时“动态地”添加额外的机器来吸收额外的负载，例如，在上面讨论的情况下。此外，对于定期构建的较重项目，在单个中央机器上运行这些构建可能并不明智。在这种情况下，我们可以将其他机器配置为从机，以减轻 Jenkins 主服务器的负载。让我们了解一些证明分布式构建的主要需求的重要原因：

### 为什么需要分布式构建？

以下是证明在 CI 系统中需要分布式构建的几个主要原因：

1.  容错：我们需要在机器或数据中心发生故障或整个区域消失时保留信息。
2.  减少延迟：我们需要减少可能遍布全球的客户响应时间。
3.  可用性：如果一台机器出现故障或变慢，我们需要附近的另一台机器来处理请求以避免请求失败。单体系统的可用性仅限于它运行的硬件。在分布式系统的情况下，有几台机器，可用性大大提高。
4.  持久性：分布式存储系统通过制作一份数据的多个副本，在成本、持久性、恢复时间等方面提供了很大的灵活性。它们对关联故障也具有高度容忍度，并且可以完全避免关联。
5.  可扩展性：分布式系统可以提供更好的可扩展性。由于我们将系统分布在多台机器上，因此我们可以非常灵活地扩展系统。
6.  效率：不用说，分布式系统效率更高。尽管工作负载很少是恒定的，我们需要按小时、每天甚至每分钟工作，但分布式系统可以为我们提供自动化扩展工具，从而提高效率。

所有这些都有助于分布式系统更加高效、可扩展和持久，我们可以在单台机器上没有太多负载的情况下完成我们的工作。

## 了解 Jenkins 分布式架构

下图显示了 Jenkins 分布式架构的基本设置：

![Jenkins分布式架构](https://www.toolsqa.com/gallery/Jenkins/1.Jenkins%20distributed%20architecture.png)

如上所示，Jenkins 使用主从架构来管理分布式构建。Master 和 Slave 通过TCP/IP协议进行通信。让我们更详细地了解这些组件中的每一个：

### Jenkins大师

Jenkins Master是主要的Jenkins 服务器，执行以下功能：

-   它安排构建作业。
-   此外，它将构建分派给各种从属以进行实际执行。
-   它持续监视奴隶。
-   此外，记录构建结果并展示它们。
-   如果需要，Master 也可以直接执行构建作业。

### Jenkins奴隶

Slave 在远程机器上运行，是一个 Java 可执行文件。从站执行以下功能：

-   它听取来自 Jenkins Master 的请求。
-   从站可以在各种操作系统上运行。
-   此外，它还执行 Master 分派的构建作业。
-   除了 Jenkins 总是选择下一个可用的保存来执行构建作业之外，我们始终可以将项目配置为始终在特定类型的从机上运行。

现在让我们了解如何在 Jenkins 分布式架构中设置从站以及如何在从站上调度和运行作业。

### 如何在 Jenkins 中设置从站/节点？

首先，我们需要在机器上[安装](https://project.toolsqa.com/issues/163)Jenkins ，然后才能设置从节点。完成后，我们可以按照以下步骤在 Jenkins 中设置从属/节点。

1.  首先，转到“管理Jenkins”部分，如下所示。

![管理Jenkins部分](https://www.toolsqa.com/gallery/Jenkins/2.Manage%20Jenkins%20Section.png)

1.  单击“ Manage Jenkins ”并向下滚动到“ Manage Nodes ”部分，如下所示：

![Jenkins 中的管理节点和云部分](https://www.toolsqa.com/gallery/Jenkins/3.Manage%20Nodes%20and%20Clouds%20section%20in%20Jenkins.png)

1.  单击下面突出显示的新节点：

![Jenkins 分布式构建 Jenkins New Node 部分](https://www.toolsqa.com/gallery/Jenkins/4.Jenkins%20distributed%20builds%20Jenkins%20New%20Node%20section.png)

在这里，为节点指定一个名称。例如，在 Slave1中，选择Permanent Agent选项并单击OK按钮。

1.  现在，它将显示节点屏幕，我们必须在其中输入有关节点的数据，如下所示：

![Jenkins 分布式构建 配置节点](https://www.toolsqa.com/gallery/Jenkins/5.Jenkins%20distributed%20builds%20Configuring%20a%20node.png)

我们将在这里输入从节点机器的详细信息。

-   executors的数量就是这个slave可以并行运行的jobs的数量。让我们保持在 1。
-   “Labels”是节点的标签，我们可以将“Slave1”指定为标签或留空。
-   选择Usage为“尽可能多地使用这个节点”。
-   对于启动方法，选择“ Launch agent by connecting it to master ”选项。
-   输入/设置Custom WorkDir 路径 作为从属节点的工作空间。
-   在 可用性中，选择/选择“尽可能保持此代理在线”。
-   单击 保存。

注意：如果在启动方法下看不到“ Launch agent by connecting it to master ”选项，请转到Jenkins 主页 -> Manage Jenkins -> Configure Global Security。在 Agents 部分，选择/单击 Random并保存。

完成上述步骤后，新的从节点将出现在节点列表中，如下所示：

![Jenkins 中的节点列表](https://www.toolsqa.com/gallery/Jenkins/6.Nodes%20list%20in%20Jenkins.png)

最初，新节点机器将处于离线状态，但如果先前设置的所有设置都正确，则会上线。我们可以在需要时使从节点离线。

现在让我们看看如何在这台从机上运行构建：

### 如何使用 Jenkins 中的分布式设置运行构建？

现在我们已经设置了从节点，我们可以在这个从节点上执行构建作业。但是，首先，让我们在奴隶上运行一个现有的工作。为此，打开作业(在 Jenkins Dashboard 的作业列表中)并单击“配置”选项。它将打开作业的配置页面，如下所示：

![Jenkins 分布式构建配置从站](https://www.toolsqa.com/gallery/Jenkins/7.Jenkins%20distributed%20builds%20Configuring%20Slaves.png)

在 General 部分中，单击“ Restrict where this project can run ”，然后在Label Expression中，输入从站的名称(我们在上面创建的 Slave1)并保存。

现在构建作业并检查作业的输出。构建现在正在所选的从站上运行，我们可以在 Jenkins 的左侧面板中验证这一点，它将列出所有作业和运行该作业的从站。

## 关键要点

-   当我们必须构建一个大项目并且负载需要在网络中的不同机器之间分配时，分布式构建提高了构建过程的效率。
-   Jenkins 使用主/从架构处理分布式构建的概念。
-   一旦我们创建并配置了从节点，我们就可以将一个项目与这个节点相关联，然后安排它进行构建。