## 1. 概述

在本教程中，我们将使用[Gatling](https://gatling.io/)在 Jenkins 管道中集成负载测试。首先，让我们确保我们熟悉[Gatling 的概念。](https://www.baeldung.com/introduction-to-gatling) 

## 2. 使用 Maven 创建 Gatling 项目

我们的目标是使用 Gatling 将负载测试插入到 Jenkins CI/CD 管道中。为了自动执行此验证步骤，我们可以使用 Maven 打包该工具。

### 2.1. 依赖关系

Gatling 提供了一个插件——Gatling Maven 插件——它允许我们在项目的 Maven 构建阶段使用 Gatling 启动负载测试。通过这种方式，可以将负载测试集成到任何持续集成工具中。

因此，让我们将 Gatling 集成到示例 Maven 项目中。首先，我们的pom.xml文件中需要以下依赖项：

```xml
<dependency>
    <groupId>io.gatling.highcharts</groupId>
    <artifactId>gatling-charts-highcharts</artifactId>
    <version>3.3.1</version>
</dependency>
<dependency>
    <groupId>io.gatling</groupId>
    <artifactId>gatling-app</artifactId>
    <version>3.3.1</version>
</dependency>
<dependency>
    <groupId>io.gatling</groupId>
    <artifactId>gatling-recorder</artifactId>
    <version>3.3.1</version>
</dependency>
```

除了前面的依赖项，我们还需要 在pom.xml的插件部分指定gatling-maven-plugin：

```xml
<plugin>
    <groupId>io.gatling</groupId>
    <artifactId>gatling-maven-plugin</artifactId>
    <version>3.0.5</version>
    <configuration>
        <simulationClass>org.baeldung.${SimulationClass}</simulationClass>
    </configuration>
</plugin>
```

SimulationClass值表示用于执行负载测试的模拟类。Gatling 版本和 Gatling Maven Plugin 版本不必相同。在这里，我们可以找到[最新版本的 Gatling](https://search.maven.org/classic/#search|ga|1|Gatling)，而在下面的链接中，我们可以找到[最新版本的 Gatling Maven Plugin。](https://search.maven.org/classic/#search|ga|1|Gatling Maven Plugin)

### 2.2. 创建场景

模拟由场景组成，场景可以包含多个请求的执行。

模拟是用[Scala](https://www.baeldung.com/scala-intro)编写的，使用 Gatling 的 DSL，简单直观。

### 2.3. 运行场景

一旦我们编写了执行负载测试所需的代码，我们就可以构建项目并运行模拟：

```bash
mvn clean package
mvn gatling:test
```

在生成的目标文件夹中，我们可以找到 Gatling 执行负载测试的报告。

## 3. 将 Gatling 与 Jenkins 集成

将 Gatling 集成到 Jenkins 管道中允许我们在其执行期间执行负载测试。

通过这种方式，我们可以验证在我们发布的代码中所做的更改不会导致性能显着下降。

这增加了对即将发布的新代码的可靠性和信心。

### 3.1. 创建詹金斯文件

在我们的项目中，我们创建了一个Jenkinsfile 来指示它如何运行 Gatling：

```plaintext
pipeline {
    agent any
    stages {
        stage("Build Maven") {
            steps {
                sh 'mvn -B clean package'
            }
        }
        stage("Run Gatling") {
            steps {
                sh 'mvn gatling:test'
            }
            post {
                always {
                    gatlingArchive()
                }
            }
        }
    }
}
```

脚本分为两个阶段：

-   构建项目(Maven)
-   运行并归档我们的场景 (Gatling)

接下来，我们将测试代码提交并推送到我们的源代码管理系统。配置完成后，Jenkins 将能够读取和执行新创建的脚本。

### 3.2. 创建管道

使用这个JenkinsFile，我们将创建我们自己的管道。在 Jenkins 中创建管道很简单。

让我们首先导航到 Jenkins 主页，然后单击New Item，选择Pipeline并为其命名。要了解有关在 Jenkins 中创建管道的更多信息，我们可以访问[专门针对该主题的教程](https://www.baeldung.com/jenkins-pipelines)。

让我们配置新创建的管道。在管道部分，我们选择脚本的来源。

特别是，让我们从下拉菜单中选择Pipeline script from SCM，设置我们存储库的 URL，设置检索源代码所需的凭据，提供从中接收脚本的分支，最后指明路径找到我们新创建的Jenkinsfile。

该窗口应如下所示：

[![img](https://www.baeldung.com/wp-content/uploads/2019/11/PipelineSCM-1.png)](https://www.baeldung.com/wp-content/uploads/2019/11/PipelineSCM-1.png)

### 3.3. Gatling Jenkins 插件

在运行新创建的管道之前，我们需要安装 Gatling Jenkins 插件。 

该插件允许我们：

-   在每次管道运行时获取并发布详细报告
-   跟踪每个模拟并提供趋势图

插入到管道中的gatlingArchive()命令是该插件的一部分，它允许我们启用刚才提到的报告。

让我们安装插件并重新启动 Jenkins。 

此时， 我们的负载测试管道已准备好运行。

### 3.4. 分离负载生成

就资源而言，生成大量调用以执行测试是一项相当昂贵的操作。 因此，在运行管道的主 Jenkins 节点上执行这些操作不是一个好主意。

我们将使用 Jenkins 从节点来执行管道中的一些步骤。 

假设我们已经在 Jenkins 上正确配置了一个或多个从节点；插入到新创建的管道中的代理任何命令允许我们分配一个执行器节点并在该节点上运行指定的代码。

## 4. 运行流水线

是时候运行我们的管道了。

在 Jenkins 主页中，我们选择新创建的管道并单击立即构建。然后我们等待管道运行。在执行结束时，我们应该看到类似这样的图表：

[![img](https://www.baeldung.com/wp-content/uploads/2019/11/PipelineRun-1024x450.png)](https://www.baeldung.com/wp-content/uploads/2019/11/PipelineRun-1024x450.png)

## 5. 查看结果

在我们管道的执行页面中，我们可以看到一个图表是如何生成的，它显示了我们的负载测试生成的平均响应时间。该图由 Gatling Jenkins 插件生成。它将包含最近 15 个构建的数据，以便立即提供我们发布的性能趋势的证据。

如果我们在管道的执行页面中单击左侧菜单中的Gatling按钮，我们将看到显示最近 15 个构建趋势的图表。

特别是，我们将获得以下信息：

-   平均响应时间
-   响应时间的第 95 个百分位数，以及
-   “KO”(即“不好”)请求的百分比

在页面底部，在刚刚提到的图表之后，我们将找到指向为每个构建生成的 Gatling 报告的链接。

通过单击链接，我们可以直接在 Jenkins 中查看已生成的 Gatling 报告：

[![img](https://www.baeldung.com/wp-content/uploads/2019/11/GatlingReport-1024x457.png)](https://www.baeldung.com/wp-content/uploads/2019/11/GatlingReport-1024x457.png)

## 六，总结

在本教程中，我们了解了如何将使用 Gatling 执行的负载测试插入到 Jenkins 管道中。我们首先解释了如何使用 Gatling 生成负载测试，如何创建Jenkinsfile来运行它，以及如何将它集成到 Jenkins 管道中。

最后，我们展示了 Gatling Jenkins 插件如何用于直接在我们的 Jenkins 部署中生成 Gatling 报告。

要了解有关如何构建测试场景以监控网站性能的更多信息，请访问我们的[另一个 Gatling 教程](https://www.baeldung.com/load-test-a-website-with-gatling)。