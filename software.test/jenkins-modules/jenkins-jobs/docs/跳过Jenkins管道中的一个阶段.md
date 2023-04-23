## 一、概述

[Jenkins](https://www.baeldung.com/linux/jenkins-install-run)是一种开源自动化服务器，可促进软件应用程序的构建、测试和部署。Jenkins 提供了[脚本化管道语法](https://www.jenkins.io/doc/book/pipeline/syntax/)，允许用户使用 Groovy 脚本语言编写[Jenkins 管道。](https://www.jenkins.io/pipeline/getting-started-pipelines/)Jenkins 中的脚本化管道具有很强的适应性，可以定制以满足特定要求。

在脚本化管道中，阶段被布置为Jenkins 将执行的一系列[步骤。](https://www.jenkins.io/doc/pipeline/steps/workflow-basic-steps/)每个阶段对应于流程中的一个不同阶段，例如构建、测试和部署应用程序。在某些情况下，可能需要跳过管道中的特定阶段。

在本教程中，我们将讨论在 Jenkins 脚本管道中跳过阶段的不同解决方案。

## 2. 流水线作业中跳过阶段的重要性

出于多种原因，跳过流水线作业中的阶段可能很重要。加快管道执行时间非常有用。有时某些阶段可能是不必要的或需要很长时间才能运行，从而减慢整个管道。

**跳过管道作业中的阶段可能会通过减少完成作业所需的时间来帮助加快开发过程。**如果管道包含测试阶段，开发人员可能希望在本地开发期间跳过此阶段，以便更快地获得有关其更改的反馈。

## 3. 使用*when*指令

**Jenkins 脚本管道中的when指令允许开发人员定义一个条件，该条件必须为\*真\**才能\*执行特定阶段。**我们可以使用*when*指令来自定义管道作业的执行。我们可以将*when*指令应用于阶段、步骤或代码块：

```bash
pipeline {
    agent any
    parameters {
        booleanParam(name: 'skip_test', defaultValue: false, description: 'Set to true to skip the test stage')
    }
    stages {
        stage('Build') {
            steps {
                sh 'echo "Building application"'
                // Add build steps here
            }
        }
        stage('Test') {
            when { expression { params.skip_test != true } }
            steps {
                sh 'echo "Testing application"'
                // Add test steps here
            }
        }
        stage('Deploy') {
            steps {
                sh 'echo "Deploying application"'
                // Add deployment steps here
            }
        }
    }
}复制
```

上面的 Jenkins 流水线自动化了应用程序的[持续集成/持续部署过程。](https://www.baeldung.com/cs/continuous-integration-deployment-delivery)它定义了三个阶段——*构建*、*测试*和*部署*。

在*Build*阶段，我们使用[*echo*](https://www.baeldung.com/linux/echo-command)语句来指示我们正在构建应用程序。测试阶段是偶然的，只有在布尔参数*skip_test*为*false**时*才会执行。如果*skip_test*是*true*，测试阶段将被完全省略。测试阶段包括一个*回显*语句，通知我们应用程序正在测试中*。*部署*阶段*涉及部署应用程序。

总的来说，Jenkins 管道是一种自动化 CI/CD 并节省时间和资源的高效方法。

## 4.使用*输入* *步骤*

Jenkins 脚本化管道允许开发人员在等待用户输入时暂停管道作业。这在开发人员希望能够选择是否跳过管道中的特定阶段的情况下很有用：

```bash
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh 'echo "Building the application"'
                // Add steps to build the application
            }
        }
        stage('Test') {
            steps {
                input message: 'Want to skip the test stage?', ok: 'Yes',
                  parameters: [booleanParam(name: 'skip_test', defaultValue: false)], timeout: time(minutes: 5))
                script {
                    if(params.skip_test) {
                        sh 'echo "Testing the application"'
                        return
                    }
                }
                // Add steps to test the application
            }
        }
        stage('Deploy') {
            steps {
                sh 'echo "Deploying the application"'
                // Add steps to deploy the application
            }
        }
    }
}复制
```

stages 块定义了流水线的三个阶段。Build阶段包含构建应用程序的指令，而 Deploy*阶段**包含*部署应用程序的指令。测试阶段涉及一个输入步骤，提示用户确定是否跳过测试阶段*。*脚本块检查输入参数*skip_test并仅在**skip_test*为*false*时执行测试应用程序的命令。

通过将它们添加到步骤块，每个阶段可以有多个步骤。为了说明，我们可以将构建或部署步骤添加到相应的阶段。*总的来说，这个管道定义了一个构建、测试和部署应用程序的工作流，同时提供了跳过测试*阶段的选项。

## 5.使用函数控制流水线

在 Jenkins 脚本管道中跳过阶段的第三种方法需要使用一个函数来调节管道的流程。当我们需要跳过管道中的几个阶段并且我们希望避免多次复制相同的代码时，这种方法是有利的。

基本概念是创建一个函数，该函数接受阶段名称和一个布尔参数以指定是否跳过该阶段。*该函数随后在if*语句中包含阶段的步骤，仅当*skip*参数为*false*时才执行这些步骤：

```bash
pipeline {
    agent any
    parameters {
        booleanParam(name: 'skip_test', defaultValue: false, description: 'Set to true to skip the test stage')
    }
    stages {
        stage('Build') {
            steps {
                sh 'echo "Building the application"'
            }
        }
        stage('Test') {
            steps {
                execute_stage('Test', params.skip_test)
            }
        }
        stage('Deploy') {
            steps {
                sh 'echo "Deploying the application"'
            }
        }
    }
}

def execute_stage(stage_name, skip) {
    stage(stage_name) {
        if(skip) {
            echo "Skipping ${stage_name} stage"
            return
        }
        // Add steps to test the application
    }
}复制
```

上面的管道作业包含一个阶段块，它建立了三个管道阶段。Build阶段包含执行 shell 命令以*回显*消息*“Building application”的**唯一*步骤。测试阶段使用名为*execute_stage 的*自定义函数通过提供阶段名称*和**skip_test*参数的值作为参数来执行测试阶段。execute_stage函数最初生成一个具有指定阶段名称*的*新阶段，然后评估 skip 是否为*true*。

*默认情况下，如果skip*为*true*，此函数将跳过该阶段，打印一条消息并结束而不执行任何进一步的步骤。如果*skip*为*false*，则函数继续执行集成到*execute_stage*函数中的任何其他步骤。使用此管道，我们可以通过将*skip_test*设置为*true来跳过**测试*阶段，这将在执行期间省略*测试阶段。*

## 六，结论

在本文中，我们讨论了在 Jenkins 脚本管道中跳过阶段的三种不同解决方案。首先，我们探索了*when* *指令*根据条件跳过阶段。之后，我们使用*输入步骤*提示用户跳过一个阶段。最后，我们查看了一个自定义函数来控制管道的流动。