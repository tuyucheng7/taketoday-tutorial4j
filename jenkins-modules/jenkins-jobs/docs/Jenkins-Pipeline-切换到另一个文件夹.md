## 1. 概述

[Jenkins管道使用](https://www.baeldung.com/linux/jenkins-install-run) [Jenkinsfile](https://www.baeldung.com/ops/jenkinsfile-comments)自动化项目的整个 CI/CD 过程。可以使用管道构建、测试和部署范围广泛的应用程序。此外，每当代码发生变化时，[Jenkins 管道](https://www.jenkins.io/pipeline/getting-started-pipelines/)都可以自动运行，从而节省时间并减少错误。

[在 Jenkins 中，有时我们需要在文件系统](https://www.baeldung.com/cs/files-file-systems)的特定位置运行一系列操作。因此，在这种情况下，我们可以使用“ dir”或“ sh” [步骤](https://www.jenkins.io/doc/pipeline/steps/workflow-basic-steps/)更改工作目录。

在本教程中，我们将了解如何更改 Jenkins 管道中的工作目录。

## 2.使用dir步骤

有几种方法可以更改 Jenkins 管道的工作目录。dir步骤是 Jenkins 中的一个内置步骤，它允许我们在一个块的持续时间内更改到不同的目录。当我们想从特定目录运行特定命令时，这非常有用。

让我们看一下将当前工作目录更改为子目录“ scripts”的dir步骤：

```bash
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                dir('scripts') {
                    / execute commands in the scripts directory /
                }
            }
        }
    }
}

```

我们在我们的管道中定义了一个构建阶段来更改工作目录。使用dir函数，脚本切换到目录“ scripts”，然后运行构建步骤。此外，我们在上面的脚本中使用了“ any”代理，这意味着任何可用的[Jenkins 代理](https://www.jenkins.io/doc/book/using/using-agents/)都可以执行此管道作业。一旦块结束，工作目录将返回到它以前的状态。

此外，我们可以使用此方法更改到任何目录，而不仅仅是子目录。为了更改当前工作目录之外的目录，我们需要提供绝对路径：

```bash
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                dir('/var/jenkins_home/workspace/SamplePipeline/scripts') {
                    / execute commands in the scripts directory /
                }
            }
        }
    }
}

```

在上述情况下，我们已成功将工作目录更改为/var/jenkins_home/workspace/SamplePipeline/scripts。

## 3.使用sh步骤

在 Jenkins 管道中更改目录的另一种方法是将sh步骤与[cd](https://www.baeldung.com/linux/cd-command-bash-script)命令结合使用。使用sh步骤，我们可以将工作目录更改为脚本目录：

```bash
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'cd scripts'
                    / execute commands in the scripts directory /
            }
        }
    }
}

```

在这种情况下，sh步骤使用cd命令将当前工作目录更改为“ scripts”。sh步骤允许我们在管道中执行 shell 命令。一旦工作目录发生变化，管道就会在“脚本” 目录中运行构建步骤。

## 4。总结

在本文中，我们学习了如何更改 Jenkins 管道中的工作目录。首先，我们查看了使用dir步骤更改目录。之后，我们使用带有sh步骤的cd命令进行了探索。