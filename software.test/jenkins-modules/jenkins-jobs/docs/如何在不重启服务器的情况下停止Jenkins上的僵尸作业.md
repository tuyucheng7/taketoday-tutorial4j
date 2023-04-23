## 1. 概述

在[Jenkins](https://www.baeldung.com/linux/jenkins-install-run)中，僵尸作业是一个陷入无限循环的构建，消耗资源并可能导致其他构建出现问题。重要的是立即停止僵尸作业，以防止它们在 Jenkins 服务器上造成进一步的问题。

在本教程中，我们将解释如何在不重启服务器的情况下识别和停止 Jenkins 上的僵尸作业。

## 2. 理解问题

僵尸作业由一个永无止境的持续进程组成。它可能由于各种原因而发生，例如构建[步骤](https://www.jenkins.io/doc/pipeline/steps/workflow-basic-steps/)卡住或作业由于外部[依赖性](https://www.jenkins.io/doc/developer/plugin-development/dependency-management/)而无法完成。僵尸作业可能会因资源消耗和其他构建失败而导致问题。此外，这使得很难看到其他构建的状态。因此，立即停止僵尸作业以防止此类问题至关重要。

## 3.创建僵尸作业

[在 Jenkins 中，我们可以使用无限管道作业通过在管道](https://www.jenkins.io/pipeline/getting-started-pipelines/)中产生错误来创建僵尸作业。为了说明这一点，让我们为它创建一个管道作业：

```bash
pipeline {
    agent any
    stages {
        stage('Infinite Loop') {
            steps {
                script {
                    while (true) {
                        println 'This is an infinite loop!'
                        Thread.sleep(10000)
                    }
                }
            }
        }
    }
}
```

在此管道作业中，脚本步骤包含一个无限循环，内部调用了Thread.sleep(10000) 。Thread.sleep()方法将导致脚本在继续循环之前暂停10秒。这将使 s顶部按钮更难中断构建。在运行这个作业时，我们会得到以下错误：

```bash
Scripts not permitted to use staticMethod java.lang.Thread sleep long. Administrators can decide whether to approve or reject this signature.
```

管理员用户可以选择批准或拒绝此签名。Jenkins 服务器配置为阻止管道脚本中的Thread.sleep()方法。此安全功能可防止恶意脚本在服务器上造成问题。为了批准请求，管理员用户需要执行以下步骤：

-   转到詹金斯仪表板
-   在 Jenkins 仪表板中单击“ 管理 Jenkins ”
-   单击In-process Script Approval以批准Thread.sleep()签名

再次构建作业时，我们将无法使用停止按钮停止这个僵尸作业。

## 4.停止僵尸作业

在 Jenkins 中，我们可以使用构建视图中的停止按钮终止正常作业。此外，这将终止该构建并释放其所有依赖项。但是，如果作业卡住无法停止，则必须强行终止。

### 4.1. 使用完成 方法

要停止僵尸作业，可以直接在构建中使用 Jenkins API。通过使用 Jenkins builds finish方法，我们可以将构建标记为已完成并为其分配结果状态。要运行脚本，我们需要执行以下步骤：

-   转到詹金斯仪表板
-   在 Jenkins 仪表板中单击“ 管理 Jenkins ”
-   在脚本控制台中添加以下脚本

```bash
Jenkins.instance.getItemByFullName("sampleZombieJob")
  .getBuildByNumber(17)
  .finish(hudson.model.Result.ABORTED, new java.io.IOException("Aborting build")); 
```

在脚本控制台中运行以下脚本以强制终止构建。

### 4.2. 使用线程中断方法

我们还可以使用脚本控制台中的Thread.interrupt()方法停止僵尸作业：

```bash
Thread.getAllStackTraces().keySet().each() {
    if (it.name.contains('sampleZombieJob')) {  
        println "Stopping $it.name"
        it.interrupt()
    }
}
```

该脚本将中断sampleZombieJob的最后一次构建并释放它消耗的所有资源。

### 4.3. 使用线程停止方法

脚本控制台还允许我们使用Thread.stop()方法停止僵尸作业：

```bash
Thread.getAllStackTraces().keySet().each() {
    if (it.name.contains('sampleZombieJob')) {  
        println "Stopping $it.name"
        it.stop()
    }
}
```

上面的脚本将终止僵尸作业并释放其所有资源。Thread.stop()方法可能会导致数据丢失并使服务器不稳定。一般来说，我们应该通过点击构建页面的停止按钮或者使用interrupt()方法来停止构建。Thread.stop()方法只能用作最后的手段。

## 5.总结

在本文中，我们学习了如何停止 Jenkins 上的僵尸作业。首先，我们使用 Jenkins 管道创建了一个僵尸作业。之后，我们讨论了杀死僵尸进程的可能解决方案。