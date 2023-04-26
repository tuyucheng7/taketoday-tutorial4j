## 1. 概述

在本教程中，我们将学习在Jenkins上运行[TestNG](https://www.baeldung.com/testng)测试所需的步骤。

我们将专注于与使用TestNG框架编写测试的GitHub仓库集成，在我们的本地机器上设置 Jenkins，在[Jenkins](https://www.baeldung.com/ops/jenkins-pipelines)上运行这些测试，以及分析测试报告。

## 2.设置

让我们首先将TestNG 的Maven[依赖项添加到我们的](https://search.maven.org/search?q=g:org.testng AND a:testng)pom.xml文件中：

```xml
<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.6.1</version>
    <scope>test</scope>
</dependency>
```

如果我们使用 Eclipse，我们可以从[Eclipse Marketplace](https://marketplace.eclipse.org/)安装TestNG插件。

## 3. 使用TestNG编写测试

让我们使用从org.testng.annotations.Test导入的@Test注解添加一个单元测试：

```java
@Test
public void givenNumber_whenEven_thenTrue() {
    assertTrue(number % 2 == 0);
}
```

我们还将在/test/resources文件夹中添加一个XML文件，以通过指定测试类来运行TestNG套件：

```xml
<suite name="suite">
    <test name="test suite">
        <classes>
            <class name="com.baeldung.SimpleLongRunningUnitTest" />
        </classes>
    </test>
</suite>
```

然后我们可以在pom.xml的插件部分指定这个XML文件本身的名称：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <suiteXmlFiles>
            <suiteXmlFile>
                src\test\resources\test_suite.xml
            </suiteXmlFile>
        </suiteXmlFiles>
    </configuration>
</plugin>
```

## 4.运行TestNG测试

让我们使用mvn clean install命令来构建和测试我们的项目：

[![运行测试的Maven命令](https://www.baeldung.com/wp-content/uploads/2022/11/5_TestNG-maven-build-command.png)](https://www.baeldung.com/wp-content/uploads/2022/11/5_TestNG-maven-build-command.png)

我们还可以通过将配置文件指定为参数来为我们的Maven构建运行特定的[配置文件](https://www.baeldung.com/maven-profiles)，如mvn clean test install -Pdefault-second。

我们可以看到我们的测试已成功通过，以及已运行的测试数，以及失败或跳过的测试数。

## 5. 在本地机器上设置 Jenkins

正如我们所知，Jenkins是一个开源服务器，有助于自动化软件开发周期的多个部分，例如构建、测试和部署，以及促进[持续交付或持续集成](https://www.baeldung.com/spring-boot-ci-cd)。

我们将使用Jenkins以自动化方式运行我们的TestNG测试。为了运行这些测试，我们将首先在我们的本地机器上设置 Jenkins。设置完成后，我们将通过作业运行测试[，](https://www.baeldung.com/ops/jenkins-job-schedule)并通过日志和测试趋势可视化工具分析结果。

现在让我们完成设置过程。

### 5.1. 下载和安装詹金斯

如果我们使用这些操作系统之一，我们可以简单地按照[Linux](https://www.baeldung.com/linux/jenkins-install-run)或[Windows](https://www.jenkins.io/doc/book/installing/windows/)的详细说明进行操作，或者我们可以使用brew install jenkins在 macOS 上设置 Jenkins。

### 5.2. 设置Jenkins管理仪表板

让我们看看运行Jenkins的命令：

```bash
$ /usr/local/opt/jenkins/bin/jenkins --httpListenAddress=127.0.0.1 --httpPort=8080
```

这里，/usr/local/opt/jenkins/bin/jenkins表示Jenkins安装的位置，而参数httpListenAddress和httpPort指定我们可以访问Jenkins UI的地址和端口。

通过设置过程，我们将看到一条日志消息“ hudson.lifecycle.Lifecycle#onReady: Jenkins is fully up and running”，表明Jenkins UI已启动并正在运行。

之后，我们可以通过http://127.0.0.1:8080/访问Jenkins仪表板。可以在同一屏幕上将继续安装的初始密码视为日志：[![Jenkins 安装密码](https://www.baeldung.com/wp-content/uploads/2022/11/Jenkins-installation-password.png)](https://www.baeldung.com/wp-content/uploads/2022/11/Jenkins-installation-password.png)

输入密码后，会提示我们安装Jenkins需要的插件。此时，我们可以选择特定的插件进行安装或继续进行自定义选择。插件安装阶段开始并尝试安装相关插件： 绿色勾号标记安装过程已完成的插件，而正在进行的插件与右侧面板上的安装日志一起标记。[![詹金斯插件安装](https://www.baeldung.com/wp-content/uploads/2022/11/5_Jenkins-plugin-installation-e1668836270254.png)](https://www.baeldung.com/wp-content/uploads/2022/11/5_Jenkins-plugin-installation-e1668836270254.png)

安装插件后，我们将被引导到一个屏幕，我们可以在其中设置我们选择的用户名和密码，或者跳过以继续使用初始密码。我们也可以选择修改Jenkins URL或者继续初始URL http://127.0.0.1:8080/

### 5.3. 设置Maven和GitHub特定的插件

我们现在将设置用于在Jenkins中构建项目的Maven和GitHub插件。为此，我们可以在Dashboard中选择Manage Jenkins选项：

[![管理詹金斯选项](https://www.baeldung.com/wp-content/uploads/2022/11/3_manage_jenkins-e1668836507505.png)](https://www.baeldung.com/wp-content/uploads/2022/11/manage_jenkins.png)

在下一个屏幕中，我们单击Global Tool Configuration选项并通过选中Install automatically复选框添加Maven安装：
[![詹金斯专家安装](https://www.baeldung.com/wp-content/uploads/2022/11/jenkins_maven_installation.png)](https://www.baeldung.com/wp-content/uploads/2022/11/jenkins_maven_installation.png)

设置好Maven安装配置后，我们单击仪表板上的“管理 Jenkins”选项，它会打开一个窗口，我们会在其中看到一个名为“管理插件”的选项。在随后的窗口中，我们单击“可用插件”选项并搜索GitHub集成和Maven集成插件：

[![詹金斯插件安装](https://www.baeldung.com/wp-content/uploads/2022/11/5_plugins_installation-e1668836677119.png)](https://www.baeldung.com/wp-content/uploads/2022/11/5_plugins_installation-e1668836677119.png)

我们可以选择这些插件，然后点击Install without restarts。

### 5.4. 设置Jenkins作业以从GitHub项目运行测试

我们现在将设置一个Jenkins作业以从GitHub项目中的模块运行TestNG测试。为此，我们选择左侧面板上的New Item选项，为我们的作业输入一个名称，然后选择Maven Project选项。在此之后，我们可以从“配置”窗口链接我们的GitHub仓库。

首先，让我们选中GitHub 项目复选框，然后提及项目URL：
[![github项目详情](https://www.baeldung.com/wp-content/uploads/2022/11/5_github_project_details-e1668839906928.png)](https://www.baeldung.com/wp-content/uploads/2022/11/5_github_project_details-e1668839906928.png)

接下来，我们在源代码管理部分选择Git，然后输入.git仓库URL。此外，我们还提到了Branch Specifier部分下的分支：
[![github分支详细信息](https://www.baeldung.com/wp-content/uploads/2022/11/5_github_branch_details.png)](https://www.baeldung.com/wp-content/uploads/2022/11/5_github_branch_details.png)

在此之后，让我们导航到构建部分并提及pom.xml文件的路径。这里的一个关键点是路径是相对于项目的。作为下一步，我们还在目标和选项部分指定Maven命令clean test install -Pdefault-second (此处，-P用于表示要用于构建的配置[文件)：](https://maven.apache.org/guides/introduction/introduction-to-profiles.html)
[![maven构建命令](https://www.baeldung.com/wp-content/uploads/2022/11/5_maven_build_command-e1668840367789.png)](https://www.baeldung.com/wp-content/uploads/2022/11/5_maven_build_command-e1668840367789.png)

我们现在可以保存这些配置并返回到我们的工作并单击左侧面板中的立即构建。它为我们提供了构建编号以及构建作业的链接。单击链接会将我们带到一个屏幕，我们可以在其中选择查看控制台输出：[![詹金斯构建选项](https://www.baeldung.com/wp-content/uploads/2022/11/5_jenkins_build_options-1-e1668837345676.png)](https://www.baeldung.com/wp-content/uploads/2022/11/5_jenkins_build_options-1-e1668837345676.png)单击控制台输出后，我们可以看到测试的运行日志和测试的成功或失败事件，以及状态控制台输出中的构建：

[![詹金斯构建输出](https://www.baeldung.com/wp-content/uploads/2022/11/5_jenkins_build_logs-e1668837047367.png)](https://www.baeldung.com/wp-content/uploads/2022/11/5_jenkins_build_logs-e1668837047367.png)

我们还可以为TestNG测试设置[自定义测试报告。](https://www.baeldung.com/testng-custom-reporting)

当我们返回 TestNGJenkins作业的主页时，我们还可以将测试结果趋势作为可视化工具查看：
[![测试趋势可视化](https://www.baeldung.com/wp-content/uploads/2022/11/5_jenkins_test_result_trend_visualisation.png)](https://www.baeldung.com/wp-content/uploads/2022/11/5_jenkins_test_result_trend_visualisation.png)

[我们还可以在分布式](https://www.baeldung.com/ops/jenkins-slave-node-setup)模式下运行 Jenkins以扩展我们的Jenkins设置。

## 六，结论

在本文中，我们通过在Jenkins仪表板上配置作业、执行简单测试用例以及在仪表板上查看测试用例的执行报告，快速了解了如何设置Jenkins以从GitHub仓库运行TestNG测试。