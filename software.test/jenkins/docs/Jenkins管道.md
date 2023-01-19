我们知道，Jenkins被广泛用作整个行业的持续集成工具，并在自动化交付管道方面提供了极大的便利。而且，我们已经了解[了Jenkins是什么？](https://www.toolsqa.com/jenkins/what-is-jenkins/)以及我们如何在之前的文章中使用它。现在我们将更进一步，了解什么是 Jenkins 管道？随后，我们将深入了解其概念并学习在本文中运行 Jenkins 管道。我们将涵盖的基本概念是：

-   什么是Jenkins管道？
    -   为什么要使用 Jenkins 管道？
-   什么是Jenkins文件？
    -   了解 Jenkinsfile 语法。
    -   了解 Jenkinsfile 的关键结构。
-   如何创建Jenkins管道？
    -   如何创建脚本化的 Jenkins 管道？
    -   以及，如何创建声明式 Jenkins 管道？

## 什么是Jenkins管道？

简而言之，管道是一组按特定顺序执行的相互关联的任务。此外，Jenkins Pipeline是一套插件，可帮助用户实施持续交付管道并将其集成到Jenkins中。此外，使用Pipeline ，你可以通过 Pipeline域特定语言 (DSL)语法将复杂或直接的交付管道创建为代码。随后，以下状态表示持续交付管道：-

![Jenkins 持续交付管道](https://www.toolsqa.com/gallery/Jenkins/1.Jenkins%20Continuous%20Deliver%20Pipeline.png)

### 为什么要使用 Jenkins 管道？

我们已经在之前关于[Jenkins 概述](https://www.toolsqa.com/jenkins/what-is-jenkins/)的文章中讨论了Jenkins如何改变软件交付过程。持续集成、测试和交付是一个无缝的过程，可以使用流水线概念实现这一切，使项目始终处于生产就绪状态。接下来，让我们看看 Jenkins Pipeline 提供的优势。

-   如前所述，我们将管道实现为代码。因此多个用户可以更新和执行流水线过程。
-   此外，流水线会在发生中断时从中断点恢复。
-   此外，如果用户暂停流水线进程，也会发生恢复。
-   多个作业可以通过管道执行，同时支持使用条件循环、分支或连接操作的复杂管道。
-   此外，管道可以集成多个插件。
-   此外，一个名为 Jenkinsfile 的文本文件存储代码，可以检查源代码管理(SCM)。

现在让我们了解如何在 Jenkins 中创建管道以及如何使用它：

## 什么是Jenkins文件？

如上所述，Jenkins Pipeline是使用名为Jenkinsfile的文本文件定义的。此外，管道通过编辑器或Jenkins实例上的配置页面使用Groovy 域特定语言作为代码实现。 此外，Jenkinsfile使开发人员可以随时轻松访问、编辑或检查代码。

### 了解 Jenkinsfile 语法：

我们可以使用两种类型的语法来定义Jenkinsfile：

-   声明式管道语法
-   脚本化管道语法

#### 声明式管道语法

声明性语法是一项新功能，它使用管道代码。它提供了有限的预定义结构。因此，它提供了一个简单易行的持续交付管道。此外，它使用了一个流水线块。

#### 脚本化管道语法

与声明式语法不同，脚本化管道语法是在 Jenkins Web UI 上编写Jenkinsfile的传统方式。此外，它严格遵循 groovy 语法，有助于将复杂的管道开发为代码。

随后，让我们快速了解Jenkins 中 Pipelines的一些重要构造：

### 了解 Jenkinsfile 的关键结构

我们现在将看到管道中使用的一些关键概念：

-   管道- 管道是一组指令，包括持续交付的过程。例如，创建应用程序、测试它并部署它。此外，它是声明性管道语法中的关键元素，它是 Jenkinsfile 中所有阶段的集合。我们在此块中声明不同的阶段和步骤。

```java
pipeline{

}
```

-   节点- 节点是脚本化管道语法中的关键元素。此外，它在 Jenkins 中充当执行流水线的机器。

```java
node{

}
```

-   阶段- 阶段由管道执行的一组进程组成。此外，任务分为每个阶段，这意味着管道中可以有多个阶段。下面的代码片段显示了可以在管道中定义的不同阶段。

```java
pipeline{
       agent any
       stages{
              stage('Build'){
                 ........
                }
              stage('Test'){
                 ........
                }
              stage('Deploy'){
                 ........
                }
              stage('Monitor'){
                 ........
                }
       }
}
```

-   步骤- Jenkins 中的步骤定义了我们在流程的特定步骤中必须执行的操作。阶段中可以有一系列步骤。此外，我们在阶段中定义的任何步骤都将在其中执行。

```java
pipeline{
     agent any
     stages{
          stage('Stage 1'){
              steps{
                //Perform steps related to this stage
              }
          }
          stage('Stage 2'){
              steps{
                //Perform steps related to this stage
              }
          }
     }

}
```

-   代理- 代理是一种指令，它使用户能够通过分配负载在同一个 Jenkins 实例中执行多个项目。此外，我们通过代理为构建分配一个执行者。你可以对整个管道使用单个代理，也可以对管道的不同阶段使用不同的代理。随后，一些与代理一起使用的参数是 -
    -   Any - 任何可用代理执行管道。
    -   无- 它在管道根部使用，意味着没有全局代理，但每个阶段都必须指定自己的代理。
    -   标签- 标签代理用于执行流水线或特定阶段。
    -   Docker - 可以使用 Docker 图像作为执行环境并将代理指定为 docker。

## 如何创建Jenkins管道？

现在我们已经了解了Jenkins Pipeline的基础知识，我们将继续创建我们的第一个Pipeline。此外，你可以参考我们关于[Jenkins 安装](https://www.toolsqa.com/jenkins/install-jenkins/)的详细文章，在执行你的第一个管道之前在你的系统上安装 Jenkins。如上所述，可以使用脚本式或声明式语法来声明管道。随后，让我们看看我们如何做到这两者，然后是两者的示例。

### 如何创建脚本化的 Jenkins 管道？

启动 Jenkins 并登录其 UI 后，你可以按照以下步骤创建你的第一个管道 -

1.  首先，在Jenkins 仪表板中，单击左侧面板上的New Item 。

![在Jenkins中创建新项目](https://www.toolsqa.com/gallery/Jenkins/2.Create%20new%20item%20in%20Jenkins.jpg)

1.  其次，输入管道的名称，从列表中选择管道。之后，单击确定。

![命名和创建管道](https://www.toolsqa.com/gallery/Jenkins/3.Naming%20and%20creating%20pipeline.jpg)

1.  之后，转到Pipeline选项卡，并从Definition的下拉列表中选择Pipeline脚本。

![脚本化管道选择](https://www.toolsqa.com/gallery/Jenkins/4.Scripted%20Pipeline%20selection.jpg)

1.  下一步是在Jenkins 提供的 Web UI 中编写管道代码。让我们看一下 Jenkins 中可用的示例管道示例-

```java
pipeline {
         agent any
         stages {
                 stage('One') {
                 steps {
                     echo 'Hi, welcome to pipeline demo...'
                 }
                 }
                 stage('Two') {
                 steps {
                    echo('Sample testing of Stage 2')
                 }
                 }
                 stage('Three') {
                
                 steps {
                       echo 'Thanks for using Jenkins Pipeline'
                 }
                 }
              }
}
```

你需要在 UI 中复制并粘贴相同内容，如下所示 -

![示例脚本管道](https://www.toolsqa.com/gallery/Jenkins/5.Sample%20scripted%20pipeline.jpg)

1.  之后，点击保存。最后，这完成了该过程。

![示例脚本管道 Jenkins 仪表板](https://www.toolsqa.com/gallery/Jenkins/6.Sample%20scripted%20pipeline%20Jenkins%20Dashboard.jpg)

我们将在下一节中看到运行该管道；在此之前，让我们看看如何在 Jenkins 中创建声明式管道。

### 如何创建声明式 Jenkins 管道？

要创建声明式管道，你需要准备好Jenkinsfile。因为我将使用我的 Github 帐户中的项目，所以我已经将Jenkinsfile放在我的项目中。

![Github 存储库中的 Jenkinsfile](https://www.toolsqa.com/gallery/Jenkins/7.Jenkinsfile%20in%20Github%20Repository.jpg)

注意：对于本教程，我使用的代码与 Jenkinsfile 中脚本化管道中使用的代码相同。

要创建声明式管道，你可以按照上述脚本化管道创建步骤中的步骤#1 和步骤#2 ，然后执行以下步骤 -

1.  转到 Pipeline 选项卡，然后从Definition的下拉列表中选择Pipeline script from SCM。

![声明式管道选择](https://www.toolsqa.com/gallery/Jenkins/8.Declarative%20Pipeline%20Selection.jpg)

1.  你现在需要从 SCM 下拉列表中选择 Git。

![选择源代码仓库为 GIT](https://www.toolsqa.com/gallery/Jenkins/9.Selecting%20source%20code%20repository%20as%20GIT.jpg)

1.  现在，你将可以选择输入你的存储库 URL和凭据。

![更新 git 存储库凭据](https://www.toolsqa.com/gallery/Jenkins/10.Updating%20git%20repository%20credentials.jpg)

1.  接下来，你可以设置分支或让它为任何分支留空。在脚本路径中，你需要写入存储库中存在的Jenkinsfile名称。单击Save，就这样，你的声明式管道就可以使用了。

![设置 Jenkins 文件名](https://www.toolsqa.com/gallery/Jenkins/11.Setting%20Jenkinsfile%20name.jpg)

现在你已完成所有管道设置，你可以从Jenkins UI执行相同的操作。你需要做的就是选择你的管道，然后单击左侧面板上的“立即构建”链接。

![运行管道构建](https://www.toolsqa.com/gallery/Jenkins/12.Running%20the%20pipeline%20build.jpg)

运行管道后，你将看到阶段视图上显示的结果，如下所示 -

![管道构建结果](https://www.toolsqa.com/gallery/Jenkins/13.Pipeline%20Build%20Results.jpg)

你还可以查看构建的控制台输出，其中将显示有助于调试的打印语句和错误(如果有)。

![构建的管道控制台输出](https://www.toolsqa.com/gallery/Jenkins/14.Pipeline%20Console%20Output%20for%20the%20build.jpg)

了解创建和构建管道有多么容易。

## 关键要点

-   Jenkins 提供了将管道创建为代码的能力，它可以包含构建、测试和部署的所有步骤。
-   此外，Jenkins 提供了两种创建管道的方法：脚本式和声明式。
-   最后，Jenkins 管道提供了以编程方式定义各种阶段和步骤的灵活性。