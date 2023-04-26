## 1. 概述

在本文中，我们将使用[Jenkins](https://jenkins.io/)和[Apache JMeter](https://jmeter.apache.org/)配置持续交付管道。

我们将依赖[JMeter](https://www.baeldung.com/jmeter)文章作为一个很好的起点来首先了解 JMeter 的基础知识，因为它已经有一些我们可以运行的配置性能测试。而且，我们将使用该项目的构建输出来查看 Jenkins [Performance](https://plugins.jenkins.io/performance/)插件生成的报告。

## 2. 设置詹金斯

首先，我们需要下载[Jenkins](https://jenkins.io/download/)的最新稳定版本，导航到我们的文件所在的文件夹，然后使用java -jar jenkins.war命令运行它。

请记住，如果没有初始用户设置，我们将无法使用 Jenkins。

## 3.安装性能插件

让我们安装Performance插件，这是运行和显示 JMeter 测试所必需的：

[![安装性能插件](https://www.baeldung.com/wp-content/uploads/2017/12/install-performance-plugin.png)](https://www.baeldung.com/wp-content/uploads/2017/12/install-performance-plugin.png)

现在，我们需要记住重新启动实例。

## 4. 使用 Jenkins 运行 JMeter 测试

现在，让我们转到 Jenkins 主页并单击“创建新作业”，指定一个名称，选择Freestyle 项目并单击“确定”。

在下一步中，在General Tab上，我们可以使用这些常规详细信息对其进行配置：

[![一般信息图片](https://www.baeldung.com/wp-content/uploads/2017/12/General_info_image.png)](https://www.baeldung.com/wp-content/uploads/2017/12/General_info_image.png)

接下来，让我们设置要构建的存储库 URL 和分支：

[![源代码管理](https://www.baeldung.com/wp-content/uploads/2017/12/Source_code_mangement.png)](https://www.baeldung.com/wp-content/uploads/2017/12/Source_code_mangement.png)

现在转到“构建” 选项卡以指定我们将如何构建项目。这里不是直接指定 Maven 命令来构建整个项目，我们可以采取另一种方式来更好地控制我们的管道，因为我们的目的只是构建一个模块。

在Execute shell子选项卡上，我们编写了一个脚本来在克隆存储库后执行必要的操作：

-   导航到所需的子模块
-   我们编译它
-   我们部署了它，知道它是一个基于 spring-boot 的项目
-   我们等到应用程序在端口 8989 上可用
-   最后，我们只需指定用于性能测试的 JMeter 脚本(位于jmeter模块的资源文件夹内)的路径以及也在资源文件夹中的结果文件( JMeter.jtl )的路径

这是相应的小 shell 脚本：

```bash
cd jmeter
./mvnw clean install -DskipTests
nohup ./mvnw spring-boot:run -Dserver.port=8989 &

while ! httping -qc1 http://localhost:8989 ; do sleep 1 ; done

jmeter -Jjmeter.save.saveservice.output_format=xml 
  -n -t src/main/resources/JMeter.jmx 
    -l src/main/resources/JMeter.jtl
```

如下图所示：

[![构建命令](https://www.baeldung.com/wp-content/uploads/2017/12/Build_command.png)](https://www.baeldung.com/wp-content/uploads/2017/12/Build_command.png)

项目从GitHub上clone下来后，我们编译它，打开8989端口，进行性能测试，我们需要让性能插件以一种友好的方式显示结果。

我们可以通过添加专用的构建后操作来做到这一点。我们需要提供结果源文件并配置操作：

[![发布性能测试结果](https://www.baeldung.com/wp-content/uploads/2017/12/Publish_performance_testresult_1.png)](https://www.baeldung.com/wp-content/uploads/2017/12/Publish_performance_testresult_1.png)

我们选择具有后续配置的标准模式：

[![发布性能测试结果](https://www.baeldung.com/wp-content/uploads/2017/12/Publish_performance_testresult_2.png)](https://www.baeldung.com/wp-content/uploads/2017/12/Publish_performance_testresult_2.png)

让我们点击“保存”，在 Jenkins 仪表板的左侧菜单上单击“立即构建 ”按钮并等待它完成我们在那里配置的一组操作。

完成后，我们将在控制台上看到项目的所有输出。最后我们将得到Finished: SUCCESS 或Finished: FAILURE：

[![构建失败](https://www.baeldung.com/wp-content/uploads/2017/12/Failed_build.png)](https://www.baeldung.com/wp-content/uploads/2017/12/Failed_build.png)

让我们转到可通过左侧菜单访问的性能报告区域。

在这里，我们将获得所有过去构建的报告，包括当前构建，以查看性能方面的差异：

[![性能测试报告](https://www.baeldung.com/wp-content/uploads/2017/12/Performance_test_report.png)](https://www.baeldung.com/wp-content/uploads/2017/12/Performance_test_report.png)

让我们单击表格上方的指示，以仅查看我们刚刚创建的最后一个构建的结果：

[![性能测试报告最后](https://www.baeldung.com/wp-content/uploads/2017/12/Performance_test_report_last.png)](https://www.baeldung.com/wp-content/uploads/2017/12/Performance_test_report_last.png)

从我们项目的仪表板中，我们可以获得Performance Trend，这是显示最后构建结果的其他图表：

[![业绩趋势](https://www.baeldung.com/wp-content/uploads/2017/12/Performance_trend.png)](https://www.baeldung.com/wp-content/uploads/2017/12/Performance_trend.png)

注意：将相同的东西应用到Pipeline 项目也很简单：

1.  从仪表板创建另一个项目(项)并将其命名为JMeter-pipeline(例如，常规信息选项卡)
2.  选择管道作为项目类型
3.  在Pipeline Tab上，在定义上选择Pipeline script并选中Use Groovy Sandbox
4.  在脚本区域只需填写以下行：

```groovy
node {
    stage 'Build, Test and Package'
    git 'https://github.com/eugenp/tutorials.git'
  
    dir('jmeter') {
        sh "./mvnw clean install -DskipTests"
        sh 'nohup ./mvnw spring-boot:run -Dserver.port=8989 &'
        sh "while ! httping -qc1
          http://localhost:8989 ; do sleep 1 ; done"
                
        sh "jmeter -Jjmeter.save.saveservice.output_format=xml
          -n -t src/main/resources/JMeter.jmx 
            -l src/main/resources/JMeter.jtl"
        step([$class: 'ArtifactArchiver', artifacts: 'JMeter.jtl'])
        sh "pid=$(lsof -i:8989 -t); kill -TERM $pid || kill -KILL $pid"
    }
}

```

该脚本首先克隆项目，进入目标模块，编译并运行它以确保该应用程序可通过 http://localhost:8989 访问

接下来，我们运行位于资源文件夹中的 JMeter 测试，将结果保存为构建输出，最后关闭应用程序。

## 5.总结

在这篇快速文章中，我们已经建立了一个简单的持续交付环境，以两种方式在Jenkins中运行和显示 Apache JMeter测试；首先通过Freestyle 项目，然后通过Pipeline。