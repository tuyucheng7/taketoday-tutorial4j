我们知道Jenkins是一种自动化工具，有助于自动化[SDLC](https://www.toolsqa.com/software-testing/software-development-life-cycle/) 流程的各个部分。此外， SDLC的所有这些不同部分都可以被视为交付产品/软件需要完成的各种任务。因此，理想情况下，Jenkins 需要提供一种机制来自动执行所有这些单独的任务，并且众所周知该机制可以在 Jenkins 中构建作业。随后，在本文中，我们将通过涵盖以下主题下的详细信息来了解Jenkins 构建作业的各种深层概念：

-   Jenkins的工作是什么？
    -   如何在 Jenkins 中创建工作？
    -   如何在 Jenkins 中配置作业？
    -   而且，如何运行Jenkins的工作？
-   如何在 Jenkins post-build 中触发另一项工作？

## Jenkins的工作是什么？

作业是Jenkins 构建过程的核心。在Jenkins中，可以将作业视为实现所需目标的特定任务。此外，我们可以创建和构建这些作业来测试我们的应用程序或项目。Jenkins提供以下类型的构建作业，用户可以根据需要创建这些作业。因此，下图突出显示了一些Jenkins 构建作业，这些作业现在使用得非常频繁：

![Jenkins的项目/工作](https://www.toolsqa.com/gallery/Jenkins/1.Projects%20Jobs%20in%20Jenkins.jpg)

让我们了解这些不同类型的Jenkins 构建作业的详细信息：

| 选项        | 描述                                                   |
| ----------------- | ------------------------------------------------------------ |
| 自由式项目  | Jenkins 中的 Freestyle Project 是一种即兴或不受限制的构建作业或具有多个操作的任务。操作可以是构建、管道运行或任何脚本运行。 |
| Maven项目   | 选择 Maven 项目来管理和构建包含 POM 文件的项目。Jenkins 将自动选择 POM 文件、进行配置并运行我们的构建。 |
| 管道        | 管道演示了包含多个构建代理的长时间运行的活动。它适用于运行无法通过普通自由式作业运行的流水线。 |
| 多配置项目  | 此选项适用于需要不同配置(例如在多个环境中进行测试、特定于平台的构建)的情况。 |
| GitHub 组织 | 此选项扫描用户的 GitHub 帐户以查找与某些定义的标记匹配的所有存储库。 |

所以，这就是 Jenkins 中 Jobs 的所有简短介绍。在下一节中，我们将开始在 Jenkins 中创建工作的旅程。

注意：在本文中，我们将以 Freestyle 作业进行演示。我们将在以后的文章中展示其他类型的工作。

### 如何在 Jenkins 中创建工作？

在Jenkins中创建作业是继续运行任何构建的第一步。要创建独立作业，请按照以下步骤操作：

第 1 步：首先，使用有效凭据登录Jenkins帐户。之后，单击Jenkins 仪表板中的“ New Item ”选项。

![Jenkins build jobs：在 Jenkins 中创建新的 Standalone job](https://www.toolsqa.com/gallery/Jenkins/2.Jenkins%20build%20jobs%20Creation%20of%20new%20Standalone%20job%20in%20Jenkins.jpg)

一旦单击，我们将被重定向到一个新页面，我们需要在该页面中填写职位名称并选择职位类型。

第 2 步：其次，让我们创建一个Freestyle 项目来构建和运行存储在 [GitHub 存储库](https://github.com/toolsqa17061989/DemoJava)中的项目：

![Jenkins 构建作业：Freestyle 项目](https://www.toolsqa.com/gallery/Jenkins/3.Jenkins%20build%20jobs%20Freestyle%20Project.jpg)



-   首先，输入项目名称。
-   其次，选择项目类型(我选择的是Freestyle项目)。
-   第三，单击“确定”按钮。

一旦我们点击 OK 按钮，Jenkins Job就会被创建。

![在 Jenkins 中创建的自由式作业](https://www.toolsqa.com/gallery/Jenkins/4.Freestyle%20Job%20created%20in%20Jenkins.jpg)

因此，通过这种方式可以在 Jenkins 中创建 Job。在下一节中，我们将看到如何配置这个新创建的作业。

### 如何在 Jenkins 中配置作业？

在上一节中，我们在 Jenkins 中创建了一个 FreeStyle 作业。让我们在本节中看看如何配置上面创建的 Jenkins 构建作业？请按照以下步骤操作：

第 1 步：首先，选择下图中下拉列表中显示的“配置”选项。

![Jenkins 构建作业：配置 FreeStyle 作业](https://www.toolsqa.com/gallery/Jenkins/5.Jenkins%20build%20jobs%20Configure%20the%20FreeStyle%20Job.jpg)

此外，一旦我们单击配置选项，我们就会重定向到配置页面。

第 2 步：其次，在“描述”部分设置工作目的。

![项目描述](https://www.toolsqa.com/gallery/Jenkins/6.Description%20of%20project.jpg)

除了描述之外，常规部分还会有一些选项。让我们很快看看这些选项：

| 选项                   | 描述                                                   |
| ---------------------------- | ------------------------------------------------------------ |
| 丢弃旧构建             | 如果我们想在开始执行新构建时丢弃旧构建，那么我们选择此选项。 |
| GitHub 项目            | 此选项指定我们正在使用 GitHub 运行我们的构建。我们指定 GitHub 项目的 URL。 |
| 本项目参数化           | 如果我们想使用在运行时传递的不同参数来运行我们的构建，那么我们将使用此选项。每个参数都有一些名称和值。 |
| 油门构建               | 此选项根据所需的最大速率强制执行构建之间的最短时间。         |
| 禁用此项目             | 如果选中此选项，则不会执行此项目的新构建。                   |
| 如有必要，执行并发构建 | 如果选中此选项，那么我们可以并行执行该项目的多个构建。       |

放置描述和其他选项后，我们将转向“源代码管理”部分。

第 3 步：第三，在源代码管理部分，我们需要选择我们推送代码的存储库。

![源代码管理部分的选择](https://www.toolsqa.com/gallery/Jenkins/7.Selection%20of%20Source%20code%20management%20section.jpg)

当我将代码推送到[GitHub 存储库](https://github.com/toolsqa17061989/DemoJava)时，我选择了上图中的 Git 选项。

第 4 步：第四，转到“构建触发器”部分并根据要求选择适当的选项。在构建触发器部分下有不同的选项可用。

![构建触发选项](https://www.toolsqa.com/gallery/Jenkins/8.Build%20trigger%20option.jpg)

让我们了解所有这些选项的详细信息：

| 选项                                 | 描述                                                   |
| ------------------------------------------ | ------------------------------------------------------------ |
| 远程触发构建                         | 当我们想通过访问一个特殊的预定义 URL 来触发新构建时使用此选项。 |
| 在构建其他项目后构建                 | 在其他构建完成后，将立即为该项目触发一个新构建。             |
| 定期构建                             | 在 build periodically 选项中，我们需要给出一个正确的时间格式，在此期间我们需要构建我们的工作。 |
| GitHub 拉取请求                      | 与 GitHub 拉取请求和问题活动集成并启动响应运行的触发器。     |
| 用于 GITScm 轮询的 GitHub 钩子触发器 | 如果选中此选项，则意味着构建将在 GitHub webhook 的帮助下执行。 |
| 轮询单片机                           | Poll SCM 选项几乎类似于 build periodically 选项。在这里，我们也可以提供计时器，但不同之处在于只有在这段时间内检测到任何代码更改时才会执行构建。 |

在下一节中，我们将使用一些最常用的构建触发器选项并通过这些选项运行构建。

第 5 步：第五，转到“构建”部分。在本节中， 下表列出了一些最受欢迎的选项：

![Jenkin 构建作业：构建部分中的选项部分](https://www.toolsqa.com/gallery/Jenkins/9.Jenkin%20build%20jobs%20Section%20of%20option%20in%20Build%20section.jpg)

随后，让我们了解所有构建选项的详细信息：

| 选项                    | 描述                                                   |
| ----------------------------- | ------------------------------------------------------------ |
| 执行 Windows 批处理命令 | 此选项运行用于构建项目的 Windows 批处理脚本。该脚本以工作区作为当前目录运行。 |
| 执行外壳                | 此选项运行用于构建项目的 shell 脚本。该脚本以工作区作为当前目录运行。 |
| 调用蚂蚁                | 此选项指定要调用的 Ant 目标列表(以空格分隔)，或将其留空以调用构建脚本中指定的默认 Ant 目标。 |
| 调用 Gradle 脚本        | 此选项适用于那些使用 Gradle 作为构建系统的项目。在这里，Jenkins 使用给定的开关和任务调用 Gradle。 |
| 调用顶级 Maven 目标     | 这适用于那些使用 Maven 作为构建系统的项目。这导致 Jenkins 使用给定的目标和选项调用 Maven。Jenkins 将各种环境变量传递给 Maven，你可以从 Maven 作为“ ${env.VARIABLE NAME} ”访问这些变量。 |
| 超时运行                | 如果构建未在指定的时间内完成，则构建将自动终止并标记为已中止。默认时间至少为 3 分钟。 |

因为我将在 Windows 批处理命令的帮助下运行我们的 java 项目，所以我选择了“执行 Windows 批处理命令”。

第 6 步：一旦我们点击上面的选项，我们就会看到文本区域，我们将在其中编写下面提到的命令：

```java
cd C:/Users/Er Jagrat Gupta/IdeaProjects/Simple_Java/src
javac Basic/Hello_ToolsQA.java
java Basic/Hello_ToolsQA
```

![Jenkins 构建作业：Windows 批处理命令](https://www.toolsqa.com/gallery/Jenkins/10.Jenkins%20build%20jobs%20Windows%20batch%20command.jpg)



最后，单击“保存”按钮。因此，通过这种方式，我们可以在 Jenkins 中配置我们的工作。在下一节中，我们将看到最常用的构建触发器选项并使用这些选项运行我们的构建。

### 如何运行Jenkins工作？

在上一节中，我们配置了我们的作业，它已准备好运行。在本节中，我们将手动运行我们的作业以及使用最常用的构建触发器选项。

#### 如何手动运行 Jenkins 作业？

由于我们已经在上一节中看到了配置部分，所以现在在本节中，我们可以很好地运行 Jenkins 构建作业。请按照以下步骤手动运行作业：

第 1 步：转到我们要运行的相应作业，然后单击下图中突出显示的“立即构建”链接：

![Jenkins 构建作业手动运行 Jenkins 构建作业](https://www.toolsqa.com/gallery/Jenkins/11.Jenkins%20build%20jobs%20Running%20Jenkins%20build%20job%20manually.jpg)

第 2 步：只要我们单击“立即构建”链接，构建就会成功开始。我们可以 在下图中突出显示的构建历史记录中看到构建的执行：

![Build的执行状态](https://www.toolsqa.com/gallery/Jenkins/12.Execution%20status%20of%20Build.jpg)

第 3 步：一旦构建执行完成，我们就可以在控制台输出屏幕上看到构建结果。

![构建结果 1](https://www.toolsqa.com/gallery/Jenkins/13.Build%20result%201.jpg)

![构建结果状态](https://www.toolsqa.com/gallery/Jenkins/14.Build%20Result%20Status.jpg)



所以，通过这种方式，我们可以手动运行我们的工作。在下一节中，我们将看到如何自动运行我们的构建。

#### 如何自动运行 Jenkins 作业？

在上一小节中，我们了解了如何手动运行 Jenkins 作业。因此，在本节中，我们将讨论如何在不同构建触发器选项的帮助下自动运行 Jenkins 构建作业。那么，让我们一一看看最受欢迎的选项：

##### 如何使用 GitHub 挂钩触发器触发 Jenkins 作业以进行 GITScm 轮询？

按照下面提到的步骤，根据 GitHub 的 webhook 配置自动触发 Jenkins 作业：

第 1 步：转到相应作业的配置页面，在构建触发器部分下，选中“ GitHub hook trigger for GITScm polling ”复选框，然后单击“Save”按钮。

![Jenkins 构建作业：使用 webhook 运行 Jenkins 构建作业](https://www.toolsqa.com/gallery/Jenkins/15.Jenkins%20build%20jobs%20Running%20Jenkins%20build%20job%20using%20webhooks.jpg)



第 2 步：我们已经在GitHub存储库 中为此项目设置了WebHook请访问文章“[ Jenkins GitHub Integration](https://www.toolsqa.com/jenkins/jenkins-github-integration/) ”以了解如何在GitHub中设置Webhook。现在对 GitHub 存储库中的代码进行一些更改，并观察 Jenkins 中的构建。一旦Jenkins通过连接检测到代码中的某些更改，它将自动启动。

![通过 webhook 构建执行](https://www.toolsqa.com/gallery/Jenkins/16.Build%20execution%20via%20webhook.jpg)

我们也可以看到构建结果，如下图所示，如下图所示。

![构建结果 vis webhook](https://www.toolsqa.com/gallery/Jenkins/17.Build%20result%20vis%20webhook.jpg)

##### 如何定期触发Jenkins工作？

在Build Periodically选项中，我们需要给出一个正确的时间格式，在此期间我们需要构建我们的工作。在下图中，我们可以看到完整的格式：

![Jenkins 中的计时器格式](https://www.toolsqa.com/gallery/Jenkins/18.Timer%20format%20in%20Jenkins.png)

现在，在 Jenkins 中，我们需要选择“定期构建”选项并给出正确的时间格式。如下图所示，我将格式设置为      因为我想在每一分钟内运行我们的构建。只要我们给出格式，Jenkins 就会自动给出格式的解释，如下图所示：

![Jenkins通过定期构建来构建作业](https://www.toolsqa.com/gallery/Jenkins/19.jenkins%20build%20Job%20via%20build%20periodically.jpg)

此外，当我们单击“保存”按钮时，我们将重定向到相应作业的 Jenkins 仪表板，下一分钟后，我们将看到 Build 已自动触发。

![通过计时器构建执行](https://www.toolsqa.com/gallery/Jenkins/20.Build%20execution%20via%20timer.jpg)



如上图所示，我们可以看到第 4 个构建 ID 被触发，并且每分钟都会启动一个新构建作为下一个构建的一部分。我们可以在这次运行的控制台输出中看到构建结果。在构建结果中，我们可以看到文本“ Started by Timer ”，因为我们使用定期构建选项触发了我们的构建。

![由定时器启动](https://www.toolsqa.com/gallery/Jenkins/21.Started%20by%20timer.jpg)

![构建成功结果](https://www.toolsqa.com/gallery/Jenkins/22.Build%20success%20result.jpg)

因此，通过这种方式，我们可以通过定期构建选项来构建我们的工作。

##### 如何通过轮询 SCM 触发 Jenkins 作业：

Poll SCM选项几乎类似于 build periodically 选项。在这里，我们也可以提供计时器，但不同之处在于只有在这段时间内检测到任何代码更改时才会执行构建，而在定期构建中，选项构建将在这段时间内自动运行，即使代码更改会发生或不发生.

![Jenkins 通过 Poll SCM 建立工作](https://www.toolsqa.com/gallery/Jenkins/23.jenkins%20Build%20job%20via%20Poll%20SCM.jpg)



正如我们在上图中看到的那样，我们每分钟都这样设置计时器，它会寻找是否检测到任何代码更改，并且一旦我们在代码中提交了一些更改，就会在下一分钟自动触发构建。如下图所示。

![构建的执行](https://www.toolsqa.com/gallery/Jenkins/24.Execution%20of%20Build.jpg)



我们可以在这次运行的控制台输出中看到构建结果。在控制台输出中，我们可以看到文本“ Started by an SCM Change ”。这意味着我们通过Poll SCM选项触发构建。

![由 SCM 更改启动](https://www.toolsqa.com/gallery/Jenkins/25.Started%20by%20SCM%20change.jpg)



最终，我们可以看到构建结果是成功的。

![构建执行状态](https://www.toolsqa.com/gallery/Jenkins/26.Build%20execution%20status.jpg)



所以通过这种方式，我们可以通过 Poll SCM 触发我们的构建。

##### 如何远程触发 Jenkins 作业？

当我们想通过访问一个特殊的预定义 URL来触发新构建时，我们使用这个选项。在 Jenkins 中，只要我们选择“远程构建触发器”选项，我们就可以看到建议的 URL。现在我们的任务是构建此URL并在浏览器中点击该 URL。作为其中的一部分，我们需要执行以下步骤：

1.  复制 URL (JENKINS_URL/job/Simple_Java_Program/build?token=TOKEN_NAME) 并将此 URL 粘贴到记事本中的某处。
2.  把 Jenkins URL 放在我的例子中，它是 http://localhost:8080/ 代替 JENKINS_URL。
3.  将令牌名称放在 TOKEN_NAME 的位置，就像我在这种情况下放置 1234 一样。我们需要在身份验证令牌文本框中输入相同的令牌名称。

![Jenkins通过远程建立工作](https://www.toolsqa.com/gallery/Jenkins/27.jenkins%20build%20job%20via%20remote.jpg)

现在，按照上述步骤，我们的URL将如下所示：

```
http://localhost:8080/job/Simple_Java_Program/build?token=1234
```

现在，我们将在单独的浏览器中点击上面的 URL。

![命中远程 URL](https://www.toolsqa.com/gallery/Jenkins/28.Hitting%20the%20remote%20URL.jpg)



只要我们在浏览器中点击 URL，Jenkins 就会自动触发构建，如下图所示。

![构建的执行](https://www.toolsqa.com/gallery/Jenkins/29.Execution%20of%20build.jpg)



对于上面的构建，我们可以在控制台输出中看到结果。在控制台输出中，我们可以看到文本“ Started by remote host ”。这意味着构建会远程触发。

![由远程主机启动](https://www.toolsqa.com/gallery/Jenkins/30.Started%20by%20remote%20host.jpg)



我们还可以在控制台输出中看到构建成功的结果。

![构建的执行状态](https://www.toolsqa.com/gallery/Jenkins/31.Execution%20Status%20of%20build.jpg)



因此，通过这种方式，我们可以通过远程 URL触发我们的Jenkins 构建作业。这就是本节的全部内容。在下一节中，让我们看看如何在 Jenkins post-build 中触发另一个作业。

## 如何在 Jenkins post-build 中触发另一项工作？

有时，我们需要在 Jenkins 中运行多个作业，而这些作业相互依赖甚至不依赖。对于依赖作业，我们可以说“如果一个作业构建成功，那么另一个作业应该运行”，对于独立作业，我们可以检查“如果对于一个作业，构建将成功，那么只会检查新构建另一份工作”。在本节中，我们将看到如何实现上述场景以执行Jenkins 构建作业。请按照以下步骤来实施它：

第 1 步：正如我们在Jenkins 仪表板中看到的那样，我们有两个作业，我希望如果“ Simple_Java_Program ”成功，则只运行“ Setup Build Job ”作业。

![Jenkins仪表板](https://www.toolsqa.com/gallery/Jenkins/32.Jenkins%20dashboard.jpg)

第 2 步：现在转到Simple_Java_Program作业的配置部分。转到“构建后操作”，我们将看到“添加构建后操作”下拉列表。

![构建后操作](https://www.toolsqa.com/gallery/Jenkins/33.Post%20build%20actions.jpg)



第 3 步：单击“添加构建后操作”下拉菜单并选择“构建其他项目”选项。

![构建其他项目选项](https://www.toolsqa.com/gallery/Jenkins/34.Build%20other%20projects%20option.jpg)

在这里，我们需要给出我们要运行的项目名称。

第 4 步：在“ Projects to Build ”文本框中输入项目名称“ Setup Build Job ”。默认情况下，我们会选择“仅当构建稳定时才触发”选项，这很好。提及另一个作业名称后，现在单击“保存”按钮。

![提及另一个项目](https://www.toolsqa.com/gallery/Jenkins/35.Mentioning%20another%20project.jpg)



第 5 步：一旦我们单击“保存”按钮，我们将到达“设置构建作业”页面，我们将在“下游项目”文本下方看到我们在上述步骤中提到的相同作业名称所以在这里，我们将“ Simple_Java_Program ”称为“上游项目”，将“安装构建作业”称为“下游项目”。

![下游项目](https://www.toolsqa.com/gallery/Jenkins/36.Downstream%20project.jpg)



第 6 步：现在我们将单击“立即构建”选项来运行此作业。重要的一点是“一旦一个简单的 java 程序成功运行，另一个作业即“设置构建作业”将立即自动运行”。

![Jenkins 为上游项目建立工作](https://www.toolsqa.com/gallery/Jenkins/37.Jenkins%20build%20job%20for%20upstream%20project.jpg)



第 7 步：在这里，我们可以在下图中看到简单 java程序作业开始运行。

![一项工作开始执行](https://www.toolsqa.com/gallery/Jenkins/38.One%20job%20execution%20started.jpg)



我们可以在控制台输出中看到构建结果。

![简单Java程序执行结果](https://www.toolsqa.com/gallery/Jenkins/39.Simple%20Java%20Program%20execution%20results.jpg)



第八步：一旦上述构建成功，另一个工作将自动启动。

![Jenkins 为下游项目构建工作](https://www.toolsqa.com/gallery/Jenkins/40.Jenkins%20build%20job%20for%20Downstream%20project.jpg)



第 9 步：一旦执行完成，我们就可以在控制台输出中看到构建结果。在控制台输出中，我们可以看到文本“ Started by upstream project ”，这表明该作业在成功执行上游作业后自动启动。

![下游作业的控制台输出文本](https://www.toolsqa.com/gallery/Jenkins/42.Console%20output%20text%20of%20downstream%20job.jpg)

因此，通过这种方式，我们可以通过构建后操作运行另一个作业。

## 要点：

-   作业是 Jenkins 控制以实现所需目标的可运行任务。
-   此外，我们可以通过单击Jenkins 仪表板中的“新建项目”来创建新作业。
-   我们需要根据我们的要求配置我们的作业，使其完全可运行。
-   此外，我们可以通过 GitHub 连接或定期构建或轮询 SCM 或远程触发构建来触发我们的构建。这些是我们在 IT 世界中最常用的方法。
-   最后，另一个作业可以通过 Jenkins 中的 Post-build 操作自动触发。