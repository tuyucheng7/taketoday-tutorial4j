## 1. 概述

在本文中，我们将通过一个使用[Jenkins](https://jenkins.io/)的持续交付示例来展示管道的用法。

我们将为我们的示例项目构建一个简单但非常有用的管道：

-   编译
-   简单的静态分析(与编译并行)
-   单元测试
-   集成测试(与单元测试并行)
-   部署

## 2. 设置Jenkins

首先，我们需要下载最新的稳定版[Jenkins](https://jenkins.io/download/)(撰写本文时为2.73.3)。

让我们导航到文件所在的文件夹，并使用java -jar jenkins.war命令运行它。**请记住，如果没有初始用户设置，我们将无法使用Jenkins**。

使用初始管理员生成的密码解锁Jenkins后，我们必须填写第一个管理员用户的个人资料信息，并确保安装所有推荐的插件。

<img src="../assets/img.png">

现在我们已经安装了全新的Jenkins，可以使用了。

<img src="../assets/img_1.png">

可以在[此处](http://mirrors.jenkins.io/war-stable/2.73.1/)找到Jenkins的所有可用版本。

## 3. 管道

Jenkins 2自带了一个很棒的特性，叫做Pipelines，当我们需要为一个项目定义一个持续集成环境时，它具有很强的可扩展性。

**管道是使用代码定义一些Jenkins步骤的另一种方式**，并自动执行部署软件的过程。

它使用具有两种不同语法的[领域特定语言(DSL)](https://jenkins.io/doc/book/pipeline/syntax/)：

-   声明式管道
-   脚本化管道

在我们的示例中，**我们将使用脚本化管道，它遵循使用Groovy构建的更具命令性的编程模型**。

让我们来看看Pipeline插件的一些特性：

-   管道被写入文本文件并被视为代码；这意味着它们可以添加到版本控制中并在以后进行修改
-   它们将在Jenkins服务器重启后保留
-   我们可以选择暂停管道
-   它们支持复杂的需求，例如并行执行作业
-   Pipeline插件也可以扩展或与其他插件集成

换句话说，设置Pipeline项目意味着编写一个脚本，该脚本将按顺序应用我们想要完成的流程的某些步骤。

**要开始使用管道，我们必须安装允许组合简单和复杂自动化的[Pipeline](https://plugins.jenkins.io/workflow-aggregator/)插件**。

我们也可以选择使用[Pipeline Stage View](https://plugins.jenkins.io/pipeline-stage-view/)，这样当我们运行构建时，我们将看到我们配置的所有阶段。

## 4. 一个简单的例子

对于我们的示例，我们将使用一个小型Spring Boot应用程序。然后我们将创建一个管道来克隆项目、构建它并运行几个测试，然后运行应用程序。

让我们安装[Checkstyle](https://www.jenkins.io/doc/pipeline/steps/checkstyle/)、[Static Analysis Collector](https://www.jenkins.io/doc/pipeline/steps/analysis-collector/)和[JUnit](https://plugins.jenkins.io/junit/)插件，它们分别用于收集Checkstyle结果、构建测试报告的组合分析图以及说明成功执行和失败的测试。

首先，让我们在这里了解一下Checkstyle的原因：它是一种开发工具，可以帮助程序员按照公认的众所周知的标准编写更好的Java代码。

Static Analysis Collector是一个附加组件，它收集不同的分析输出并将结果打印在组合趋势图中。此外，该插件还提供健康报告并根据这些分组结果构建稳定性。

最后，JUnit插件提供了一个发布者，该发布者使用构建期间生成的XML测试报告，并输出与项目测试相关的详细且有意义的信息。

我们还将在应用程序的pom.xml中配置Checkstyle：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-checkstyle-plugin</artifactId>
    <version>2.17</version>
</plugin>
```

## 5. 创建管道脚本

首先，我们需要创建一个新的Jenkins作业。让我们确保在点击“OK”按钮之前选择“Pipeline”作为类型，如以下屏幕截图中所述：

<img src="../assets/img_2.png">

下一个页面允许我们填写Jenkins作业不同步骤的更多详细信息，例如description、triggers、一些高级项目选项：

<img src="../assets/img_3.png">

让我们通过单击“Pipeline”选项卡来深入了解此类工作的主要和最重要的部分。

然后，对于定义，选择Pipeline script并选中Use Groovy Sandbox。

以下是Unix环境的工作脚本：

```groovy
node {
    stage 'Clone the project'
    git 'https://github.com/eugenp/tutorials.git'

    dir('spring-jenkins-pipeline') {
        stage("Compilation and Analysis") {
            parallel 'Compilation': {
                sh "./mvnw clean install -DskipTests"
            }, 'Static Analysis': {
                stage("Checkstyle") {
                    sh "./mvnw checkstyle:checkstyle"

                    step([$class: 'CheckStylePublisher',
                          canRunOnFailed: true,
                          defaultEncoding: '',
                          healthy: '100',
                          pattern: '**/target/checkstyle-result.xml',
                          unHealthy: '90',
                          useStableBuildAsReference: true
                    ])
                }
            }
        }

        stage("Tests and Deployment") {
            parallel 'Unit tests': {
                stage("Runing unit tests") {
                    try {
                        sh "./mvnw test -Punit"
                    } catch(err) {
                        step([$class: 'JUnitResultArchiver', testResults:
                                '**/target/surefire-reports/TEST-*UnitTest.xml'])
                        throw err
                    }
                    step([$class: 'JUnitResultArchiver', testResults:
                            '**/target/surefire-reports/TEST-*UnitTest.xml'])
                }
            }, 'Integration tests': {
                stage("Runing integration tests") {
                    try {
                        sh "./mvnw test -Pintegration"
                    } catch(err) {
                        step([$class: 'JUnitResultArchiver', testResults:
                                '**/target/surefire-reports/TEST-'
                                        + '*IntegrationTest.xml'])
                        throw err
                    }
                    step([$class: 'JUnitResultArchiver', testResults:
                            '**/target/surefire-reports/TEST-'
                                    + '*IntegrationTest.xml'])
                }
            }

            stage("Staging") {
                sh "pid=\$(lsof -i:8989 -t); kill -TERM \$pid "
                + "|| kill -KILL \$pid"
                withEnv(['JENKINS_NODE_COOKIE=dontkill']) {
                    sh 'nohup ./mvnw spring-boot:run -Dserver.port=8989 &'
                }
            }
        }
    }
}
```

首先，我们从GitHub克隆仓库，然后将目录更改为我们的项目，称为spring-jenkins-pipeline。

接下来，我们编译项目并以并行方式应用Checkstyle分析。

以下步骤表示并行执行单元测试和集成测试，然后部署应用程序。

**并行性用于优化管道，并使作业运行得更快**。在Jenkins中，最好的做法是同时运行一些可能会花费大量时间的独立操作。

例如，在现实世界的项目中，我们通常有很多单元测试和集成测试可能需要更长的时间。

请注意，如果任何测试失败，BUILD也将被标记为FAILED，并且不会进行部署。

此外，我们使用JENKINS_NODE_COOKIE来防止在管道到达终点时立即关闭我们的应用程序。

要查看在其他不同系统上运行的更通用的脚本，请查看[GitHub仓库](https://github.com/eugenp/tutorials/blob/master/spring-jenkins-pipeline/scripted-pipeline-unix-nonunix)。

## 6. 分析报告

创建作业后，我们将保存脚本并在Jenkins仪表板的项目主页上点击“Build Now” 。

以下是构建的概述：

<img src="../assets/img_4.png">

再往下一点，我们将找到管道的阶段视图，以及每个阶段的结果：

<img src="../assets/img_5.png">

将鼠标悬停在阶段单元格上并单击“Logs”按钮以查看在该步骤中打印的日志消息时，可以访问每个输出。

我们还可以找到代码分析的更多细节。让我们从右侧菜单的构建历史记录中单击所需的构建，然后点击Checkstyle Warnings。

在这里，我们可以通过单击浏览60个高优先级警告：

<img src="../assets/img_6.png">

详细信息选项卡显示突出警告的信息片段，使开发人员能够了解其背后的原因。

同样，可以通过单击“Test Result”链接访问完整的测试报告。让我们看看cn.tuyucheng.taketoday包的结果：

<img src="../assets/img_7.png">

在这里我们可以看到每个测试文件及其持续时间和状态。

## 7. 总结

在本文中，我们搭建了一个简单的持续交付环境，通过Pipeline作业在Jenkins中运行并展示静态代码分析和测试报告。