某些日常任务是构建任何软件自动化项目所必需的，例如下载所需的依赖项、放置额外的 jar、将源代码编译成二进制代码、运行测试、将编译后的代码打包到不同的工件(如 Jar、Zip 和 War 文件)中，以及将这些工件部署到应用程序服务器或存储库。这些都是每次手动完成的非常繁琐的任务，因此我们需要一种工具来帮助我们在最短的时间内完成所有这些任务，以减少人力。[Maven](https://www.toolsqa.com/maven/maven-introduction/)出现在画面中，它是一个构建生命周期管理工具，可帮助自动化所有这些繁琐的任务，从编译资源到构建工件。现在，我们知道 Jenkins 是最常用的CI/CD 工具之一，Jenkins Maven 的集成是支持基于 Java 的项目的完整CI/CD的必要条件。

随后，让我们快速了解 Maven 的一些必要细节，然后通过涵盖以下主题中的详细信息来详细了解Jenkins Maven 集成：

-   什么是马文？
    -   Maven 和 Jenkins 有什么区别？
-   什么是 Jenkins 的 Maven 插件？
    -   如何在 Jenkins 中安装 Maven 插件？
-   如何将 Maven 与 Jenkins 集成？
    -   另外，如何在 Jenkins 中设置 Java Path？
    -   如何在 Jenkins 中设置 Maven 路径？
-   Jenkins 中的 Maven 项目是什么？
    -   如何在 Jenkins 中创建 Maven 项目？
    -   如何在 Jenkins 中执行 Maven 项目？

## 什么是马文？

Maven是一个强大的 Java 项目构建管理工具，可帮助执行构建生命周期框架。Maven 的基础是POM(项目对象模型)的概念，其中所有配置都可以在pom.xml文件的帮助下完成。它是一个包含所有项目和配置相关信息的文件，例如源目录、版本依赖项、插件和构建信息、测试源目录等。Maven 的一些主要功能是：

-   Maven 描述了项目是如何构建的。它使用页面对象模型构建项目。
-   Maven 自动下载、更新以及验证依赖项之间的兼容性。
-   在 Maven 中，依赖项是从依赖项存储库中检索的，而插件是从插件存储库中检索的，因此专家在项目依赖项和插件之间保持适当的隔离。
-   Maven 还可以从源代码生成文档，编译然后将编译后的代码打包成 JAR 文件或 ZIP 文件。

关于Maven的详细信息和理解，可以参考[Maven Tutorials](https://www.toolsqa.com/maven/maven-introduction/)。

正如我们所理解的，Maven和Jenkins都是自动化工具，用于构建各个阶段的自动化。尽管如此，它们并不是彼此的替代品。在了解Maven与Jenkins集成之前，我们先快速了解一下Jenkins与Maven的一些区别。

### Maven 和 Jenkins 有什么区别？

正如我们所讨论的，尽管 Maven 和 Jenkins 没有任何直接的比较，因为它们都实现了不同的目的。不过，我们可以根据以下参数比较它们：

| 比较参数     | Jenkins                                                     | 行家                                                       |
| -------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 目的     | Jenkins 是一个 CI/CD 工具，它使用各种其他工具/插件来自动化整个构建周期。 | Maven 是一个构建生命周期管理工具，有助于依赖管理和创建自动化构建。 |
| 编程模型 | Jenkins 使用 groovy 创建管道或使用 GUI 进行独立作业。        | Maven 使用基于 XML 的结构来执行构建生命周期中的各种操作。    |
| 依赖管理 | Jenkins 可以提供各种作业之间的依赖，但这更多的是一种动作依赖。 | Maven 提供了资源依赖管理的能力，包括构建的第三方依赖。       |
| 范围     | Jenkins 可以做的远不止构建和部署，比如服务器管理，数据库管理。为此，它使用与各种工具的集成。 | Maven 在代码和构建方面受到更多限制。这是 Jenkins 中用于构建基于 Maven 的 Java 项目的插件。 |

现在让我们快速看看如何将 Maven 与 Jenkins 集成：

## 什么是 Jenkins 的 Maven 插件？

Maven 插件 是一个插件，提供在 Jenkins 中配置、构建和运行基于 Maven 的项目的功能。这是Maven 与 Jenkins集成的必备先决条件。 让我们看看如何在 Jenkins 中安装 Maven 的集成插件：

### 如何在 Jenkins 中安装 Maven 插件？

按照下面提到的步骤在 Jenkins 中安装 Maven 插件：

第一步：点击左侧菜单栏中的Manage Jenkins链接，如下突出显示：

![管理Jenkins选项](https://www.toolsqa.com/gallery/Jenkins/1.Manage%20Jenkins%20option.png)

第二步：在“系统配置”部分下，单击“管理插件”选项：

![在 Jenkins 中管理插件部分](https://www.toolsqa.com/gallery/Jenkins/2.Manage%20Plugins%20Section%20in%20Jenkins.png)

第 3 步： 在插件管理器下，单击可用选项卡(标记 1)并搜索maven插件(标记 2)。结果它将显示Maven 集成插件(标记 3)：

![jenkins 中的 maven 插件](https://www.toolsqa.com/gallery/Jenkins/3.maven%20plugin%20in%20jenkins.png)

第 4 步：选中Maven Integration 插件前面的复选框，然后单击Install without restart按钮：

![安装 Maven Jenkins 插件](https://www.toolsqa.com/gallery/Jenkins/4.Install%20Maven%20Jenkins%20plugin.png)

第五步：插件安装成功后，点击复选框重启Jenkins：

![安装 maven 插件后重启 jenkins](https://www.toolsqa.com/gallery/Jenkins/5.restart%20jenkins%20after%20maven%20plugin%20installation.png)

第六步： Jenkins重启后， Maven Jenkins插件就安装成功，可以进行配置了。

Maven Jenkins 插件安装完成后，让我们看看如何配置和集成Maven 与 Jenkins：

## 如何将 Maven 与 Jenkins 集成？

将Maven与Jenkins集成的原因是我们可以通过Jenkins执行 Maven 命令，因为我们将主要将Maven用于Java项目。因此，JDK也是此设置的先决条件。那么，让我们先快速了解一下如何在Jenkins 中指定 java 路径：

### 如何在 Jenkins 中设置 Java 路径？

Maven与Jenkins的集成从在 Jenkins中设置Java 路径开始。请按照以下步骤在Jenkins中设置Java路径：

第 1 步：打开Jenkins并转到Jenkins 仪表板。之后，单击Manage Jenkins链接，如下所示：

![Jenkins仪表板](https://www.toolsqa.com/gallery/Jenkins/6.Jenkins%20Dashboard.jpg)

一旦我们点击“管理 Jenkins”链接，我们将重定向到“管理 Jenkins ”页面，我们可以在其中看到不同类型的选项，从这里我们可以看到“全局工具配置”选项。

第 2 步：现在单击如下突出显示的 Global Tool Configuration链接：

![单击全局工具配置](https://www.toolsqa.com/gallery/Jenkins/7.Click%20on%20Global%20tool%20configuration.jpg)

一旦我们点击全局工具配置，我们将被重定向到全局工具配置页面以指定不同的配置。

第三步：之后，我们需要在Jenkins中设置JDK 路径要在Jenkins中设置JDK路径请按照以下突出显示的步骤操作：

![在 Jenkins 中设置 JDK 路径](https://www.toolsqa.com/gallery/Jenkins/8.Setting%20JDK%20path%20in%20Jenkins.jpg)

-   单击添加 JDK 按钮。请注意，默认情况下，“自动安装”将被选中，因此由于我们要使用本地机器上安装的 JDK，“自动安装”将安装最新版本的 JDK，你还需要提供凭据下载相关JDK。
-   将 JDK 的名称命名为 JDK 1.8，因为它当前安装在我的机器中。
-   在 JAVA_HOME 文本框中给出 JDK 的路径。

之后，在Jenkins中正确设置JDK路径。现在，下一个任务是在Jenkins中设置Maven路径。

### 如何在 Jenkins 中设置 Maven 路径？

在上一节中，我们了解了如何在Jenkins中设置Java路径，现在，在本节中，我们将在 Jenkins 中设置Maven路径。请按照以下步骤在Jenkins中设置Maven路径。

![在 Jenkins 中设置 maven 路径](https://www.toolsqa.com/gallery/Jenkins/9.setup%20maven%20path%20in%20Jenkins.jpg)

1.  单击“添加 Maven ”按钮。请注意，默认情况下，“自动安装”将被选中，因此我们将取消选中它，因为我们不希望 Jenkins 自动安装最新版本的 Maven。
2.  将 Maven 的名称命名为Maven 3.6，因为这是在我的机器中设置的版本。
3.  在MAVEN_HOME文本框中给出 Maven 的路径。
4.  单击保存按钮。

现在我们已经在 Jenkins 中配置了Java和Maven ，下面我们来看看如何在Jenkins 中创建和执行Maven 项目？

## Jenkins 中的 Maven 项目是什么？

Jenkins 提供了一种特定的作业类型，它明确提供了用于配置和执行 Maven 项目的选项。这种作业类型称为“Maven 项目”。让我们看看如何在 Jenkins 中创建 Maven 项目并运行它。

### 如何在 Jenkins 中创建 Maven 项目？

我们知道pom.xml文件是Maven项目的核心。出于演示目的，我们已经创建了一个 Maven 项目，该项目已推送到[GitHub 存储库中。](https://github.com/toolsqa17061989/SetupBuildJob)请访问[了解 GitHub](https://www.toolsqa.com/git/github/)链接 以了解有关Git和GitHub 的更多信息。要在Jenkins中创建 Maven 项目，请按照以下步骤操作：

第 1 步：首先，我们需要创建一个工作。要创建它，请单击下面突出显示的“新项目”选项：

![新的 Maven 作业创建](https://www.toolsqa.com/gallery/Jenkins/10.New%20Maven%20job%20creation.jpg)

第二步：现在，执行以下步骤创建一个新的 maven 项目：

![创建 maven 作业](https://www.toolsqa.com/gallery/Jenkins/11.Creation%20of%20maven%20job.jpg)

1.  给出项目的名称。
2.  单击Maven 项目。请注意，如果此 Maven Project 选项不可见，我们需要检查 Jenkins 中是否安装了“Maven Integration”插件。如果没有安装，则安装它并重新启动 Jenkins。有关更多详细信息，请参阅我们的文章[“安装 Jenkins”](https://www.toolsqa.com/jenkins/install-jenkins/)。
3.  单击确定按钮。

第三步：在描述部分描述项目。

![项目描述](https://www.toolsqa.com/gallery/Jenkins/12.Description%20of%20project.jpg)

现在，转到其他部分，如源代码管理和构建触发器部分。

第 4 步：根据我们的要求在“源代码管理”中选择 Git 选项，因为我们将从GitHub存储库中提取我们的 Maven 项目。

![Github 仓库](https://www.toolsqa.com/gallery/Jenkins/13.Github%20repository.jpg)

此外，如果我们需要在“Build Triggers”部分选择“GitHub hook trigger for GITScm polling” 选项 ，我们将在 webhooks 的帮助下触发我们的构建。

![选择了 Webhook 选项](https://www.toolsqa.com/gallery/Jenkins/14.Webhook%20option%20selected.jpg)

第 5 步：现在执行以下突出显示的步骤以进一步移动：

![在 Jenkins 中指定 pom.xml](https://www.toolsqa.com/gallery/Jenkins/15.specify%20pom.xml%20in%20Jenkins.jpg)

1.  首先，在Root POM文本框中给出pom.xml的相对路径，因为我们在项目的根目录下有 pom.xml，所以我们直接提供文件名。
2.  其次，在目标和选项文本框中键入“clean install”作为“maven clean”、“maven install”  ，因为“maven test”是运行 maven build 时的 maven 命令，但这里“clean install”命令足以从Jenkins触发构建。
3.  最后，单击“保存”按钮。

因此，我们的Maven 项目设置完成并准备运行。在下一节中，我们将看到如何在Jenkins中执行Maven项目。

### 如何在 Jenkins 中执行 Maven 项目？

由于我们的 maven 项目已经在上一节中设置好，我们现在可以执行它了。设置后，Maven项目类似于Jenkins 中的其他作业类型。 Jenkins提供了多种执行作业的方法。除了手动执行之外，下面突出显示了一些自动执行作业的选项：

![Jenkins 中的 Maven 项目执行](https://www.toolsqa.com/gallery/Jenkins/16.Maven%20project%20execution%20in%20Jenkins.png)

你可以选择一个或所有上述选项来自动触发构建。让我们了解这些选项在什么情况下会触发构建：

| 构建触发选项                         | 行为                                                       |
| -------------------------------------- | ------------------------------------------------------------ |
| 每当构建 SNAPSHOT 依赖项时构建       | 如果选中，Jenkins 将解析该项目的 POM，并检查其快照依赖项是否构建在该 Jenkins 上。如果是这样，Jenkins 会建立一个构建依赖关系，这样每当构建依赖作业，并创建一个新的 SNAPSHOT jar 时，Jenkins 就会调度这个项目的构建。这样可以方便地自动执行持续集成。Jenkins 将从 POM 中的 <dependency> 元素以及 POM 中`<extension>`使用的 <plugin> 和 s 检查快照依赖项。 |
| 在构建其他项目后构建                 | 设置一个触发器，以便在其他一些项目完成构建时为该项目安排新的构建计划。例如，这对于在构建完成后运行广泛的测试很方便。此配置补充了上游项目“构建后操作”中的“构建其他项目”部分，但在你要配置下游项目时更可取. |
| 定期构建                             | 此功能主要是为了将 Jenkins 用作 CRON 的替代品，对于持续构建软件项目并不理想。当人们最初开始持续集成时，他们通常会使用像每晚/每周这样的定期计划构建的想法来使用此功能。然而，持续集成的要点是在做出更改后立即开始构建以提供快速反馈。为此，你需要将 SCM 更改通知连接到 Jenkins。 |
| 用于 GITScm 轮询的 GitHub 钩子触发器 | 如果 Jenkins 从 Git SCM 部分中定义的 repo 接收/获取 PUSH GitHub 挂钩，它将触发 Git SCM 轮询逻辑。事实上，轮询逻辑属于 Git SCM。 |
| 轮询单片机                           | 配置 Jenkins 以轮询 SCM 中的更改。请注意，这将是一项昂贵/产生成本的 CVS 操作，因为每次轮询都需要 Jenkins 扫描整个工作区并通过服务器进行验证。 |

你可以选择这些选项中的任何一个来自动执行Jenkins作业。我们将在以后的文章中详细介绍所有这些选项。

## 要点：

-   Maven 是一个强大的 Java 项目构建管理工具，可协助构建完整的生命周期构建框架。此外，它的基础是POM(项目对象模型)的概念，其中所有配置都可以在pom.xml文件的帮助下完成。
-   此外，我们可以根据我们的操作系统下载二进制 zip 并将其保存在任何目录中。之后，我们需要在系统中的环境变量部分设置maven路径，在Jenkins中设置全局工具配置。
-   最后，在设置之后，我们需要在构建部分编写 pom.xml 文本并命令“clean install”来驱动 maven 功能。
-   此外，我们可以在 Jenkins 中手动或使用 Jenkins 提供的各种构建触发选项自动触发构建。