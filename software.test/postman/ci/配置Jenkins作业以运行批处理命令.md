在之前的[如何安装 Jenkins 教程中，](https://toolsqa.com/jenkins/install-jenkins/)我们成功地在我们的系统上安装并运行了 Jenkins。在本教程中，我们将从离开的地方继续。如果你已停止 Jenkins，则需要将 shell 中的默认目录更改为下载 war 文件的目录，然后再次运行命令 java -jar jenkins.war。这将初始化 Jenkins 并要求你输入用户名和密码。如果你在之前的教程中没有更改密码和用户名，你的用户名将是admin，密码将与用于解锁下载过程的密码相同。回到 Jenkins，在本教程中我们将重点关注以下内容：

-   创建一个新的詹金斯工作
-   配置 Jenkins Job 以运行批处理命令

在开始使用 Jenkins 之前，我想告诉你我们将在本教程中使用 Windows 命令。虽然转换不是很困难，我们之前也讨论过，但如果你使用的是 Windows 以外的其他操作系统，请事先了解转换命令。

## 如何创建新的 Jenkins 作业？

Jenkins Job 是父元素，有时也称为项目。有了它，你可以在一个地方查看结果、控制台、报告等。我们稍后会讨论所有这些事情，但现在，让我们创建一个新的 Jenkins Job。

1.点击新建项目

![jenkins_Main_Logo](https://www.toolsqa.com/gallery/Postman/1.jenkins_Main_Logo.png)

2.随便写名字，我们这里用的是Postman Collection

![名称_项目_Jenkins](https://www.toolsqa.com/gallery/Postman/2.Name_Project_Jenkins.png)

你将在下面看到你可以启动的不同项目的列表。每个项目都用几行定义。我们将在本教程中使用Freestyle 项目。现在，如果你阅读 Jenkins 中的 Freestyle 项目下面的几行，它们会有点令人困惑。所以让我们简化它。

### 什么是自由式项目？

自由式项目是你可以在其中运行任何类型的构建的项目。这些构建可以包括 shell 脚本、dos 脚本或者可能调用 ant 等。如果你访问 Jenkins 的[开发指南](https://wiki.jenkins.io/display/JENKINS/Building+a+software+project)，你会看到它们指定了一个自由式项目，因为 Jenkins 可用于执行典型的构建服务器工作，例如连续执行/official/nightly 构建、运行测试或执行一些重复的批处理任务。这在 Jenkins 中被称为“自由风格软件项目” 。

所以基本上自由式项目是非常灵活的，它可以让你构建、运行和执行各种东西。最重要的是，大多数插件都是为 Jenkins 中的自由式项目编写的。所以它有点成为一个原始的选择。

回到创建工作区，只需通过单击选择 Freestyle 项目并按OK。 将创建工作区。

离开此屏幕并通过单击Jenkins移至仪表板。

![Move_To_Jenkins_Dashboard](https://www.toolsqa.com/gallery/Postman/3.Move_To_Jenkins_Dashboard.png)

你将在仪表板上看到你的项目名称

![仪表板_Jenkins](https://www.toolsqa.com/gallery/Postman/4.Dashboard_Jenkins.png)

单击项目名称，你将输入项目或作业。这样我们就在 Jenkins 中创建了我们的第一个项目/工作。我们将只在这个项目中工作。在下一节中，我们将学习如何在 Jenkins 中运行批处理命令

## 如何配置 Jenkins Job 运行批处理命令？

现在我们已经创建了自己的 Jenkins Job，我们将继续在 Jenkins 中运行批处理命令。批处理命令是在系统外壳中运行的命令，如 Windows 中的命令提示符。此名称来自为 DOS 和 Windows 等早期操作系统创建的批处理文件。这些文件中提到的命令只能通过系统的 shell 运行。因此，名称派生为单个命令的批处理命令。因此，按照这些步骤，我们将在 Jenkins 中运行批处理命令。

我希望你在 Jenkins 作业/项目中并且在你的系统中可以看到以下屏幕。

![项目_工作区_詹金斯](https://www.toolsqa.com/gallery/Postman/5.Project_Workspace_Jenkins.png)

1.转到配置。

![配置_Jenkins](https://www.toolsqa.com/gallery/Postman/6.Configure_Jenkins.png)

2.转到构建。

![Build_Option_Jenkins](https://www.toolsqa.com/gallery/Postman/7.Build_Option_Jenkins.png)

1.  打开批处理部分的下拉菜单，然后单击执行 Windows 批处理命令

![Build_Dropdown_Jenkins](https://www.toolsqa.com/gallery/Postman/8.Build_Dropdown_Jenkins.png)

注意：由于我们在 Windows 机器上，我们将运行在 Windows 命令提示符下运行的命令。如果你有 MAC，那么你应该在上面的部分中选择Execute Shell然后只执行 shell 命令。我们之前已经讨论了我们将在此处运行的 MAC 命令。请参考教程[如何使用 Newman 命令在 Postman 中运行 Collections。](https://toolsqa.com/postman/running-collection-using-newman/)

1.  选择选项后，将出现一个框以输入命令。所以现在，只是为了测试，我们将运行命令来检查我们是否在机器上安装了 Newman，这是使用 Newman 运行 Jenkins 所必需的。这可以使用命令 newman --version来完成

![配置 Jenkins 作业以运行批处理命令](https://www.toolsqa.com/gallery/Postman/9.Configure%20Jenkins%20Job%20to%20Run%20Batch%20Command.png)

1.  单击保存，命令将被保存。
2.  单击立即构建。

![Build_Now_Jenkins](https://www.toolsqa.com/gallery/Postman/10.Build_Now_Jenkins.png)

1.  你将在上面的菜单下方看到正在运行的构建

![构建_运行_詹金斯](https://www.toolsqa.com/gallery/Postman/11.Build_Running_Jenkins.png)

1.  构建完成后，单击位于Build Number左侧的球。它会带你到控制台。
2.  现在你可以看到命令成功运行并且输出成功显示。

![控制台_日志_詹金斯](https://www.toolsqa.com/gallery/Postman/12.Console_Log_Jenkins.png)

现在我们已经安装了 Jenkins并学习了如何配置 Jenkins 作业以运行批处理命令，现在是时候在 Jenkins 上运行 Postman Collections 了。