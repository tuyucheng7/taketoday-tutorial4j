在软件领域，每个工具都提供了根据用户需求使用工具功能的灵活性。此外，这种灵活性以“配置”的形式出现。同样的，Jenkins也提供了它的配置，让用户根据它的易用性和需求来使用它。因此，在本文中，我们将讨论一些重要且使用最广泛的 Jenkins 配置。随后，让我们通过涵盖以下主题下的详细信息来了解 Jenkins 配置选项的工作原理：

-   Jenkins 中的全局设置是什么？
-   Jenkins 中的主目录配置是什么？
    -   如何在 Jenkins 中配置 Home 目录？
-   Jenkins 中的系统消息配置是什么？
    -   如何在 Jenkins 中配置系统消息？
-   Jenkins 中的执行器配置是什么？
    -   如何在 Jenkins 中配置执行器？
-   Jenkins 中的 Usage 选项配置是什么？
-   Jenkins 中的静默期配置是什么？
    -   如何在 Jenkins 中配置静默期？
-   什么是Jenkins网址？
-   Jenkins 中的环境变量是什么？
    -   如何在 Jenkins 中配置环境变量？
-   什么是使用统计？
-   什么是 Git 插件选项？
-   而且，什么是电子邮件通知？

## Jenkins 中的全局设置是什么？

作为Jenkins管理员，配置系统(配置 Jenkins 设置的选项)是 Jenkins 最突出的部分。在这里，管理员可以定义适用于 Jenkins 中存在的所有项目的全局设置。现在，问题是如何导航到 Jenkins 中的配置系统选项。随后，要导航到“ 配置系统”选项，请按照以下步骤操作：

注意：请注意，我们假设 Jenkins 已经安装在系统中，并且我们使用管理员帐户登录。此外，要安装 Jenkins，请访问[InstallJenkins](https://www.toolsqa.com/jenkins/install-jenkins/)文章。

第 1 步：首先，导航到 Jenkins 仪表板，然后单击下面突出显示的“管理 Jenkins ”选项：

![Jenkins 配置：管理 Jenkins](https://www.toolsqa.com/gallery/Jenkins/1.Jenkins%20Configure%20Manage%20Jenkins.jpg)

第 2 步：其次，一旦我们单击“管理 Jenkins ”选项，我们将被重定向到“管理 Jenkins ”页面。在这里，我们需要单击“系统配置”部分下以红色突出显示的“配置系统”。

![Jenkins Configure：配置系统](https://www.toolsqa.com/gallery/Jenkins/2.Jenkins%20Configure%20configure%20system.jpg)

第 3 步：第三，我们将单击“配置系统”链接。随后，我们将进入配置页面，如下所示：

![Jenkins Configure：Jenkins的配置系统页面](https://www.toolsqa.com/gallery/Jenkins/3.Jenkins%20Configure%20Configure%20System%20Page%20of%20Jenkins.jpg)

这样，我们就进入了 Jenkins 中的 配置页面。不，让我们在Jenkins的这个页面上看一些最流行和最广泛使用的选项：

| 选项         | 描述                                                   |
| ------------------ | ------------------------------------------------------------ |
| Jenkins主目录 | 这个属性告诉我们Jenkins 的主目录在系统中的位置。         |
| 系统消息     | 系统消息显示Jenkins仪表板页面上的消息。                    |
| 执行者       | 这个配置选项告诉我们Jenkins机器上可以运行多少个并行作业。  |
| 使用选项     | 这个选项告诉我们Jenkins如何调度在任何节点上构建。          |
| 安静期       | 当此选项不为零时，该项目新触发的构建将被添加到队列中，但Jenkins会在实际开始构建之前等待指定的时间(以秒为单位)。 |
| Jenkins网址   | 默认情况下，此选项以 localhost 的形式描述Jenkins安装的HTTP地址。我们可以写我们机器的DNS(域名)或者用机器的 IP 地址覆盖 localhost。 |
| 环境变量     | 环境变量适用于每个节点上的每个构建。它们可以在Jenkins 的配置中使用(作为 $key 或 ${key} )，并将添加到从构建启动的进程的环境中。 |
| 使用情况统计 | 此选项用于跟踪Jenkins中的使用数据。                        |
| Git 插件     | 此选项用于在 Jenkins 中设置全局 git 用户名和电子邮件详细信息。 |
| 电子邮件通知 | 此选项用于配置电子邮件相关配置，以在运行构建后发送有关构建结果的电子邮件。 |

因此，让我们在以下部分中了解所有这些选项的含义、用法和详细信息：

## Jenkins 中的主目录配置是什么？

第一个属性是配置页面上的主目录。这个属性告诉我们Jenkins 的主目录在系统中的位置。另外，在这个home目录下，我们可以看到我们在Jenkins中创建的日志信息、插件信息、作业存储信息，以及一些其他配置相关的信息。所以默认情况下，Jenkins将其所有数据存储在文件系统的这个目录中。

![主目录信息](https://www.toolsqa.com/gallery/Jenkins/4.Home%20directory%20information.jpg)

我们也可以验证文件系统中的主目录，如下图所示：

![机器上 Jenkins 的主目录](https://www.toolsqa.com/gallery/Jenkins/6.Home%20directory%20of%20Jenkins%20on%20the%20Machine.jpg)

在下一节中，让我们看看如何在 Jenkins中配置主目录。

### 如何在 Jenkins 中配置 Home 目录？

在上一节中，我们讨论了主目录的概念。现在，在本节中，让我们看看如何在Jenkins中配置主目录？按照下面提到的步骤配置/更改主目录：

第 1 步：首先，复制当前目录中存在的所有项目并将它们粘贴到新目录中。要创建新目录，只需在我们移至桌面时转到要粘贴所有项目的位置。之后，像我们创建Jenkins Home一样创建一个文件夹，并在该文件夹中粘贴现有Jenkins目录中的所有项目。

![将项目移动到新目录以更改主目录](https://www.toolsqa.com/gallery/Jenkins/7.Moving%20items%20to%20new%20directory%20for%20changing%20Home%20Directory.jpg)

第 2 步：其次，转到[Windows 上的环境变量选项](https://www.computerhope.com/issues/ch000549.htm#:~:text=In the System Properties window,and click the Edit button.) 并创建一个新变量，如JENKINS_HOME。之后，将新目录路径粘贴到变量值部分，如下图所示：

![Jenkins 配置：创建环境变量](https://www.toolsqa.com/gallery/Jenkins/8.Jenkins%20Configure%20creating%20environment%20variable.jpg)

第 3 步：第三步，重启 Jenkins 并再次进入配置系统页面，这次我们可以在这里看到更改后的目录：

![更改了主目录](https://www.toolsqa.com/gallery/Jenkins/9.Changed%20Home%20directory.jpg)

在上图中，我们可以看到主目录已更改。因此，通过这种方式，我们可以更改Jenkins中的Home 目录。

## Jenkins 中的系统消息配置是什么？

系统消息在Jenkins 仪表板页面上显示消息。此外，此配置的目的是向用户发布任何类型的通知。当我们看到Jenkins 仪表板页面时，通常我们不会在突出显示的区域中看到任何消息，如下图所示：![Jenkins 配置：系统消息](https://www.toolsqa.com/gallery/Jenkins/10.Jenkins%20Configure%20System%20Message.jpg)

现在，让我们在下一小节中了解如何在Jenkins中配置系统消息。

### 如何在 Jenkins 中配置系统消息？

如果我们想显示一条消息，则必须配置系统消息选项并执行以下操作：

-   在系统消息文本框中输入一条消息。
-   单击保存按钮。

![Jenkins Configure：系统消息配置](https://www.toolsqa.com/gallery/Jenkins/11.Jenkins%20Configure%20System%20message%20configuration.jpg)

单击“保存”按钮后，我们将被重定向到Jenkins 仪表板页面，现在我们可以在突出显示的部分中看到消息，如下图所示：

![Jenkins 配置显示的系统消息](https://www.toolsqa.com/gallery/Jenkins/12.Jenkins%20Configure%20Displayed%20System%20Message.jpg)

因此，通过这种方式我们可以编辑消息以向用户提供任何通知。

## Jenkins 中的执行器配置是什么？

这个配置选项告诉我们Jenkins机器上可以运行多少个并行作业。如下图所示，我们看到数字2，这意味着可以同时运行两个并行作业。我们可以根据我们的要求增加这个数字。

![Jenkins 配置：执行者数量为 5](https://www.toolsqa.com/gallery/Jenkins/13.Jenkins%20Configure%20No%20of%20executors%20as%205.jpg)

在下一节中，我们将看到如何在Jenkins 中配置执行器？

### 如何在 Jenkins 中配置执行器？

如果我们改变executor的数量，那么Build Executor Status部分下的executor数量(如箭头所示)会相应增加，如下图：

![执行者数量增加](https://www.toolsqa.com/gallery/Jenkins/14.No%20of%20executors%20increased.jpg)

请注意，我们将在更多关于分布式构建的文章中看到我们如何使用这些执行器。

## Jenkins 中的 Usage 选项配置是什么？

当我们处理节点的概念时，使用 Jenkins 中的Usage 选项配置。节点基本上是一台机器，它是 Jenkins 环境的一部分，能够执行项目或管道。这个选项告诉我们 Jenkins 如何调度在任何节点上构建。此选项有两种模式：

-   尽可能使用此节点：这是默认选项。Jenkins 自由使用这个节点。每当有可以使用此节点完成的构建时，Jenkins 都会使用它。
-   Only build jobs with label expressions matching this node：在这种模式下，Jenkins 只会在该项目被限制为使用标签表达式的某些节点时在此节点上构建项目，并且该表达式与该节点的名称和/或标签相匹配。这允许为某些类型的作业保留一个节点。

![Jenkins 中的使用选项](https://www.toolsqa.com/gallery/Jenkins/15.Usage%20Option%20in%20Jenkins.jpg)

注意：至于这些节点的配置，我们将在本 Jenkins 系列中有关分布式构建的后续文章中进行讨论。

## Jenkins 中的静默期配置是什么？

当此选项为非零时，该项目新触发的构建将被添加到队列中，但Jenkins将在实际开始构建之前等待指定的时间段(以秒为单位)。如下图所示，Quiet period为 5，因此Jenkins将等待 5 秒，然后再开始队列中已存在的新构建。

![Jenkins 配置：静默期](https://www.toolsqa.com/gallery/Jenkins/16.Jenkins%20Configure%20Quiet%20period.jpg)

在下一节中，我们将看到如何在 Jenkins 中配置静默期。

### 如何在 Jenkins 中配置静默期？

在上一节中，我们看到了静默期的概念。因此，在本节中，让我们看看如何配置静默期。请按照以下步骤来实现它：

第 1 步：转到“配置系统”页面并设置你想要设置静默期的时间段，如下图所示。放置时间后，单击“保存”按钮。

![配置静默期](https://www.toolsqa.com/gallery/Jenkins/17.configuring%20quiet%20period.jpg)

第 2 步：现在通过单击“立即构建”链接两次来运行任何作业。对于下图中的示例，我运行了两次“ Simple Java Program ”作业，你将看到第二次构建的待处理消息。

![静默期选项演示](https://www.toolsqa.com/gallery/Jenkins/18.quiet%20period%20option%20demonstration.jpg)

所以它将等待时间(安静期)然后在构建将被执行之后。

## 什么是Jenkins网址？

默认情况下，此选项以 localhost 的形式描述Jenkins安装的 HTTP 地址，即http://localhost:8080/jenkins/。我们可以写我们机器的DNS(域名)或者用机器的IP 地址覆盖 localhost 。这个值让Jenkins知道如何引用自己，即显示图像或在电子邮件中创建链接。

![Jenkins 配置：Jenkins URL](https://www.toolsqa.com/gallery/Jenkins/19.Jenkins%20Configure%20Jenkins%20URL.jpg)

## Jenkins 中的环境变量是什么？

环境变量在Jenkins中以键值对的形式存在。这些自定义环境变量适用于每个节点上的每个构建。我们可以在Jenkins 的配置中使用它们(作为 $key 或 ${key})，我们会将它们添加到从构建启动的进程的环境中。

![Jenkins 中的环境变量](https://www.toolsqa.com/gallery/Jenkins/20.Environment%20variables%20in%20Jenkins.jpg)

在下一节中，我们将了解如何在 Jenkins 中配置和使用环境变量。

### 如何在 Jenkins 中配置环境变量？

在本节中，让我们看看如何在Jenkins中配置和使用环境变量。请按照以下步骤来实现它：

第 1 步：在配置页面下，指定环境变量的名称和值，如下图所示。放置后，单击“保存”按钮。请注意，如果要添加多个环境变量，则需要单击“添加”按钮并填写相同的名称和值信息。

![设置环境变量](https://www.toolsqa.com/gallery/Jenkins/21.Setting%20up%20environment%20variables.jpg)

第 2 步：为环境变量 demo 创建一个新作业。

![为环境变量 Demo 创建作业](https://www.toolsqa.com/gallery/Jenkins/22.Creation%20of%20a%20job%20for%20Environment%20variables%20Demo.jpg)

第 3 步：转到此作业的配置部分，然后转到构建部分。在构建部分下，单击“添加构建步骤”下拉菜单并选择选项“执行 Windows 批处理命令”。

![选择执行windows批处理命令](https://www.toolsqa.com/gallery/Jenkins/23.Selecting%20execute%20windows%20batch%20command.jpg)

第 4 步：现在，将以下命令放在文本区域部分，然后单击“保存”按钮。 回声 %ENV_DEMO%

这里的ENV_DEMO 是我们在环境变量部分定义的同一个变量名。

![放置Windows批处理命令](https://www.toolsqa.com/gallery/Jenkins/24.Putting%20windows%20batch%20command.jpg)

第 5 步：现在通过单击“ Build Now ”链接运行构建并转到控制台输出以查看结果：

![环境变量的输出](https://www.toolsqa.com/gallery/Jenkins/25.Output%20of%20environment%20variable.jpg)

正如我们在上图中看到的，红色矩形中突出显示的值与我们在配置系统下的环境变量部分中设置的值相同。所以，通过这种方式，我们就可以配置和使用环境变量了。

## 什么是使用统计？

在任何开源项目中，跟踪使用数据都非常困难。为了满足这一需求，我们使用了使用情况统计选项。当我们启用此选项时，Jenkins 会定期发送Jenkins 版本、有关代理、操作系统类型和执行程序的信息、已安装的插件和版本以及作业数量等信息。所有类型的使用统计信息都发布在[https://stats.jenkins 上。我/](https://stats.jenkins.io/)。

## 什么是 Git 插件选项？

当我们尝试从 Git 推送我们的代码时，我们需要配置我们的用户名以及用于身份验证的电子邮件。Jenkins在 Git 插件部分下提供了这个选项：

-   全局配置 user.name 值：如果我们在这里给出用户名，那么 git 命令git config user。将调用名称“你的用户名”。
-   全局配置 user.email 值：如果我们在这里提供电子邮件，那么将调用git 命令git config user.email "your email" 。

![Jenkins 配置：Git 插件配置](https://www.toolsqa.com/gallery/Jenkins/26.Jenkins%20Configure%20Git%20Plugin%20Configuration.jpg)

在这里，我们通常使用基于凭据的身份验证，因为我们使用用户名和电子邮件将我们的代码推送到存储库中。因此，我们可以将这些值作为全局配置的一部分。

## 什么是电子邮件通知？

电子邮件通知选项配置用于向指定收件人发送邮件的SMTP设置。这部分有两个字段我们需要配置：

-   SMTP 服务器：我们需要在这里指定 SMTP 邮件服务器的名称。Jenkins 使用 JavaMail 发送电子邮件。
-   默认用户电子邮件后缀：如果我们的用户的电子邮件地址可以通过简单地添加后缀自动计算，则指定该后缀，否则将其留空。

![电子邮件通知](https://www.toolsqa.com/gallery/Jenkins/27.Email%20Notification.jpg)

现在，问题是如何配置这些电子邮件通知选项。因此，我们将在名为“通知”的进一步文章中详细说明此部分。最后，关于Jenkins中一些重要且使用最广泛的配置的讨论就到此为止。随后，在下一节中，让我们看看一些重要的要点。

## 要点：

-   每个工具都可以根据用户的要求灵活地使用工具的功能。这种灵活性以“配置”的形式出现。
-   此外，主目录告诉我们主目录 Jenkins 在系统中的位置。Jenkins 将其所有数据存储在文件系统的这个目录中。
-   此外，系统消息会在 Jenkins 仪表板页面上显示消息。
-   #of executors 告诉我们有多少个并行作业可以在 Jenkins 机器上运行。
-   usage 选项描述了 Jenkins 如何安排在任何节点上构建。
-   此外，当quiet period选项不为零时，该项目的新触发构建将添加到队列中。但是 Jenkins 会在实际开始构建之前等待指定的时间段(以秒为单位)。
-   此外，Jenkins URL以 localhost 的形式描述了 Jenkins 安装的 HTTP 地址。这个值让Jenkins知道如何引用自己，即。显示图像或在电子邮件中创建链接。
-   环境变量适用于每个节点上的每个构建。我们可以在 Jenkins 的配置中使用它们(作为 $key 或 ${key})，我们会将它们添加到从构建启动的进程的环境中。
-   使用统计选项跟踪 Jenkins 中的使用数据。
-   当我们尝试从 Git 推送我们的代码时，我们需要配置我们的用户名以及用于身份验证的电子邮件。Jenkins 在Git 插件部分下提供了这个选项。
-   最后，电子邮件通知选项配置 SMTP 设置以将邮件发送给指定的收件人。