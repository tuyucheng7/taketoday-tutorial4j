## 一、概述

[Jenkins](https://www.baeldung.com/linux/jenkins-install-run)是一种流行的开源自动化服务器，可通过其工作流或管道功能促进 CI/CD 流程。在软件开发中，持续集成和持续交付已成为标准做法，以确保代码更改得到全面测试并自动部署到生产环境。

**[与 Jenkins Workflow/Pipeline](https://www.jenkins.io/pipeline/getting-started-pipelines/)并行运行阶段是一种允许开发人员同时执行多个阶段的技术，可以提高开发过程的整体性能和速度。**

在本教程中，我们将探索如何与 Jenkins 工作流或管道作业并行运行阶段。

## 2. 了解 Jenkins 工作流/管道

Jenkins Workflow 或 Pipeline 是一个强大的功能，它允许我们定义代码的构建、测试和部署。软件开发生命周期中的每个步骤都由管道作业中的一个阶段表示。[一个阶段可以由多个顺序步骤](https://www.jenkins.io/doc/pipeline/steps/workflow-basic-steps/)或任务组成。

我们可以使用阶段来自动构建、测试和部署软件应用程序。开发人员使用 Jenkins 工作流/管道中的阶段来定义可以作为软件开发过程的一部分执行的任务序列。

## 3. 了解 Jenkins 工作流/管道中的阶段

在 Jenkins Workflow/Pipeline 中，阶段代表软件开发过程中的一个不同阶段。每个阶段都有特定的功能，例如编译代码、运行测试或部署代码。该过程确保代码更改得到全面测试，并快速、频繁地集成到主分支中。此外，这种方法可以帮助开发人员尽早发现错误并更可靠、更快速地发布软件应用程序。

通常，Jenkins 管道作业中存在三种不同类型的阶段：

-   顺序[阶段](https://www.jenkins.io/blog/2018/07/02/whats-new-declarative-piepline-13x-sequential-stages/)按顺序执行各个阶段，从第一阶段开始到第二阶段继续
-   [并行阶段](https://www.jenkins.io/blog/2017/09/25/declarative-1/)同时执行且彼此独立，这意味着多个阶段可以同时完成
-   条件阶段是在特定条件下执行的阶段，例如通过或未通过测试

此外，这些阶段旨在通过将流程分解为更小、更易于管理的部分来帮助简化流程并提高效率，这些部分可以以结构化和有组织的方式执行。

## 4.并行运行阶段

在某些情况下，我们可能希望通过并行运行多个阶段来加快我们的管道。我们可能有一个构建应用程序的阶段和另一个运行自动化测试的阶段。这些阶段可以并行运行，因为测试不依赖于已完成的构建。此外，我们需要定义一个并行块来与 Jenkins Workflow 或 Pipeline 并行运行阶段。

### 4.1. *JenkinsFile*中的并行块

并行块是一段代码，它定义了并行运行的多个阶段*。**让我们看一下带有并行*块的流水线作业：

```bash
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'echo "Building the application"'
                // Add commands to build application
            }
        }
        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh 'sleep 5s'
                        sh 'echo "Running unit tests"'
                        // Add commands to run unit tests
                    }
                }
                stage('Integration Tests') {
                    steps {
                        sh 'echo "Running integration tests"'
                        // Add commands to run integration tests
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                sh 'echo "Deploying the application"'
                // Add commands to deploy application
            }
        }
    }
}复制
```

在上面的管道作业中，我们分为三个阶段：*构建*、*测试*和*部署*。在此管道作业中，我们向每个阶段添加了简单的步骤来演示管道的执行和工作流程。构建*阶段*负责构建应用程序代码。为了说明这一点，构建阶段只有一个步骤，即向控制台打印消息*“正在构建应用程序”的*[*回显命令。*](https://www.baeldung.com/linux/echo-command)这个阶段可以有额外的命令来构建代码。

测试阶段有一个并行块，它定义了两个并行运行的阶段：单元*测试**和*集成*测试*。*parallel*指令允许两个阶段同时运行。因此，在这个阶段，我们不能确定哪个阶段先完成它的任务。在*“单元测试”*阶段，脚本首先使用[*sleep*](https://man7.org/linux/man-pages/man3/sleep.3.html#:~:text=sleep() causes the calling,arrives which is not ignored.)*命令休眠* 5秒 ，然后执行*echo命令将**“运行单元测试”*打印到控制台。然后，它运行命令来执行单元测试。在*“集成测试”*阶段，脚本执行命令以运行集成测试。

最后，部署*阶段*负责部署应用程序代码。在这一阶段中，只有一个步骤，即向控制台打印消息*“正在部署应用程序”的**回显命令。*

## 5. 并行运行阶段的好处

并行运行阶段对开发人员有几个好处。它减少了开发过程的持续时间，从而加快了执行速度。此外，开发人员还可以使用此功能在更短的时间内完成更多的工作。

**运行并行阶段可以加速错误和错误的检测，使软件应用程序更加可靠。通过利用并行阶段，开发人员可以享受这些好处并高效地开发高质量的软件应用程序。**

## 六，结论

在本文中，我们探讨了如何与 Jenkins 工作流或管道作业并行运行多个阶段。首先，我们了解了 Jenkins 管道作业及其阶段的基本概念和执行。之后，我们在 Jenkins 管道作业中运行了多个并行阶段。最后，我们还探讨了并行运行阶段的好处。