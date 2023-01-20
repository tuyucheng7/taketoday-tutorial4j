## 1. 概述

[Jenkins](https://www.baeldung.com/linux/jenkins-install-run)是一种自动化服务器，可为开发人员提供自动构建、测试和部署应用程序的能力。Jenkins 服务器执行作业，可以手动或自动触发。此外，我们可以同时或按特定顺序运行这些作业。

在本教程中，我们将逐步完成创建自由式作业和[管道作业](https://www.jenkins.io/pipeline/getting-started-pipelines/)以触发另一个作业的过程。

## 2. 工作设置

在 Jenkins 中，我们可以创建作业来运行代码覆盖测试、集成测试和部署应用程序。为了执行一组任务，Jenkins 提供了不同类型的作业。此外，根据任务，我们可能必须触发内部作业。

在这里，我们将构建两个作业，一个父管道作业和一个子自由式作业。我们将从 Jenkins UI 手动运行父作业，而子作业将由父作业在内部触发。

我们先来看孩子的工作。

### 2.1. 创建自由式子作业

在我们开始之前，让我们先构建一个演示设置。我们将使用自由式作业类型来创建子作业。自由式构建作业易于使用，并且有许多预制选项。此外，要创建自由式作业，我们需要遵循以下步骤：

-   单击 Jenkins 仪表板中的新建项目
-   将“工作名称”设置为childJob
-   选择“工作类型”为“自由式项目”
-    在构建步骤的执行 shell 中添加echo “childJob”命令
-   保存作业

一切都应该类似于这样：

[![儿童自由泳工作](https://www.baeldung.com/wp-content/uploads/2022/12/Screenshot-2022-12-15-at-12.05.11-PM.png)](https://www.baeldung.com/wp-content/uploads/2022/12/Screenshot-2022-12-15-at-12.05.11-PM.png)

作为上述步骤的结果，我们现在能够创建一个简单的自由式作业，它可以独立运行，也可以由另一个作业触发。

### 2.2. 创建管道父作业

Jenkins 管道是一系列相互关联的事件或作业，它们在我们的软件开发工作流程中产生[持续交付。](https://www.baeldung.com/cs/continuous-integration-deployment-delivery)在这里，我们将创建一个管道作业，它将在内部调用我们刚刚创建的childJob 。让我们看一下管道作业的 Groovy 脚本：

```bash
pipeline {
    agent any
    stages {
        stage('build') {
            steps {
                echo "parentJob"
            }
        }
        stage('triggerChildJob') {
            steps {
                build job: "childJob", wait: true
            }
        }
    }
}
```

上面的脚本在管道作业中包含两个阶段，build和triggerChildJob 。第一阶段只是执行[echo](https://www.baeldung.com/linux/printf-echo)命令，而第二阶段会在内部触发childJob。

现在让我们使用上面的 Groovy 脚本创建一个管道作业：

-   单击 Jenkins 仪表板中的新建项目
-   将“作业名称”设置为parentJob
-   选择“工作类型”作为管道项目
-   如上所述添加 Groovy 脚本并保存作业

因此：

[![img](https://www.baeldung.com/wp-content/uploads/2022/12/Screenshot-2022-12-15-at-11.43.37-AM.png)](https://www.baeldung.com/wp-content/uploads/2022/12/Screenshot-2022-12-15-at-11.43.37-AM.png)

我们现在可以从 Jenkins 管道作业中触发自由式作业。请注意，父作业构建将分阶段进行，如下图所示：

[![img](https://www.baeldung.com/wp-content/uploads/2022/12/Screenshot-2022-12-15-at-11.48.13-AM.png)](https://www.baeldung.com/wp-content/uploads/2022/12/Screenshot-2022-12-15-at-11.48.13-AM.png)

在上面的输出中，我们可以看到两个阶段都已构建，并且triggerChildJob 已成功运行。

## 3.总结

在本文中，我们演示了如何在 Jenkins 中创建一个内部触发另一个作业的作业。作为第一步，我们创建了一个示例自由式 Jenkins 作业，然后创建了一个管道作业以在内部触发它。