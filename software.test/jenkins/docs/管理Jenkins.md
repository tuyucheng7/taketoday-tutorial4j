Jenkins提供了一些管理员级别的配置，可以根据各种项目、团队和用户的需要进行设置和配置。其中很少是有助于将 Jenkins 作为工具进行管理的设置，也很少是有助于监控 Jenkins 服务器及其节点健康状况的工具或统计信息。随后，在本文中，我们将了解所有这些配置的详细信息，并将学习如何管理Jenkins的各种配置、插件、工具和统计信息，涵盖以下主题下的详细信息：

-   如何管理 Jenkins 配置？
-   系统配置部分提供了哪些选项来管理 Jenkins？
    -   Jenkins 中的系统配置是什么？
    -   同样，Jenkins 中的全局工具配置是什么？
    -   Jenkins 中的插件配置是什么？
    -   另外，Jenkins 中的节点配置是什么？
-   安全部分提供了哪些选项来管理 Jenkins？
    -   Jenkins 中的配置全局安全选项是什么？
    -   同样，Jenkins 中的管理凭证选项是什么？
    -   Jenkins 中的 Configure Credential Providers 选项是什么？
-   Status Information 部分提供了哪些选项来管理 Jenkins？
    -   另外，Jenkins 中的系统信息部分是什么？
    -   Jenkins 中的系统日志部分是什么？
    -   Jenkins 中的负载统计部分是什么？
    -   同样，Jenkins 中的 About Jenkins 部分是什么？
-   工具和操作部分提供了哪些选项来管理 Jenkins？
    -   Jenkins 中的“从磁盘重新加载配置”选项是什么？
    -   另外，Jenkins 中的 Jenkins CLI 选项是什么？
    -   Jenkins 中的脚本控制台是什么？
    -   同样，Jenkins 中的 Prepare for Shutdown 选项是什么？

## 如何管理 Jenkins 配置？

Manage Jenkins是 Jenkins 最突出的部分之一[，](https://www.toolsqa.com/jenkins/what-is-jenkins/)它允许管理员用户在 Jenkins 中配置各种设置和插件。当你以管理员用户身份登录时，你可以按照以下步骤导航到各个部分来管理 Jenkins ：

第 1 步：首先，转到Jenkins 仪表板并单击下面突出显示的“管理 Jenkins” ：

![点击管理Jenkins](https://www.toolsqa.com/gallery/Jenkins/1.Click%20on%20Manage%20Jenkins.jpg)

第 2 步：其次，一旦我们点击管理 Jenkins，我们将重定向到管理 Jenkins页面，如下所示：

![管理Jenkins页面](https://www.toolsqa.com/gallery/Jenkins/2.Manage%20Jenkins%20page.png)

此页面包含与以下各项相关的各种配置和设置相关的所有选项：

1.  系统配置
2.  安全
3.  状态信息
4.  故障排除
5.  工具和行动

随后，让我们在以下部分中了解所有这些选项的详细信息：

## 系统配置部分提供了哪些选项来管理 Jenkins？

系统配置部分讨论了Jenkins的配置方面。此外，这些配置与系统级别、工具级别、插件级别以及节点级别相关。下图显示了此部分下可用的各种配置：

![Jenkins 中的系统配置设置](https://www.toolsqa.com/gallery/Jenkins/3.System%20Configuration%20settings%20in%20Jenkins.png)

随后，让我们了解所有这些子部分的详细信息：

### Jenkins 中的系统配置是什么？

配置系统选项将提供各种选项来设置全局设置和路径。此外，这些设置对于不同的用户和安装的各种插件可能不同。下表显示了可在此部分下配置的基本设置列表：

| 配置                       | 细节                                                       |
| ---------------------------- | ------------------------------------------------------------ |
| 主目录                 | 默认情况下，Jenkins 将其所有数据存储/保存在文件系统的这个目录中。此外，你可以单击右上角的 ( ? ) 符号来检查各种选项以更新此路径。 |
| Maven 项目配置         | 本节提供与 Maven 设置相关的各种全局选项。此外，一旦安装了 Maven 插件，这将可用。 |
| Jenkins位置             | 此部分提供配置 Jenkins URL 和系统管理员电子邮件地址的选项。  |
| 从另一个域提供资源文件 | Jenkins 提供许多不受信任的用户创建的文件，例如项目工作区中的文件或存档的工件。此外，当未定义资源根 URL 时，Jenkins 将使用 HTTP 标头 Content-Security-Policy(“ CSP ”)提供这些文件。默认情况下，它设置为禁用许多现代 Web 功能的值，以防止/避免跨站点脚本 ( XSS ) 和对访问这些文件的 Jenkins 用户的其他攻击。如果定义了资源根 URL，Jenkins 会将对用户创建的资源文件的请求重定向到以此处配置的 URL 开头的 URL。此外，这些 URL 不会设置 CSP 标头，从而允许 JavaScript 和类似功能发挥作用。 |
| 全局属性               | 这些是与清除节点、环境变量和各种工具的位置相关的各种全局属性。 |
| 流水线速度/持久性设置  | 此设置允许用户更改运行管道的默认持久性模式。在大多数情况下，这是性能与在 Jenkins 意外中断后恢复运行管道的能力之间的权衡。 |
| 使用情况统计           | 对于任何项目来说，了解软件的使用方式至关重要，但跟踪使用数据在开源项目中本来就很困难。此外，匿名使用统计可以满足这一需求。启用后，Jenkins 会定期向 Jenkins 项目发送信息。此外，Jenkins 项目使用此信息来设置开发优先级。 |
| GitHub                 | 本节提供与 GitHub 服务器、API 和其他 GitHub 插件设置相关的各种选项。 |
| 电子邮件通知           | 本节提供与电子邮件 SMTP 服务器和其他详细信息相关的各种配置。 |

因此，现在让我们了解管理 Jenkins 部分下可用的下一组系统配置：

### Jenkins 中的全局工具配置是什么？

全局工具配置是系统配置选项下的另一个子部分。随后，在本节中，我们可以定义配置以及有关我们想要与 Jenkins 集成的全局工具的安装信息，如Maven、JDK、Git、Gradle 和 Ant。下表显示了可在此部分下配置的基本设置列表：

| 配置           | 细节                                          |
| ---------------- | ----------------------------------------------- |
| Maven 配置 | 本节提供各种选项来配置和安装特定版本的 Maven。  |
| JDK配置    | 本节提供各种选项来配置和安装特定版本的 JAVA。   |
| Git 配置   | 本节提供各种选项来配置和安装特定版本的 Git。    |
| 摇篮配置   | 本节提供各种选项来配置和安装特定版本的 Gradle。 |
| 蚂蚁配置   | 本节提供各种选项来配置和安装特定版本的 Ant。    |

这只是工具的一个子集及其相应的配置。此列表将根据 Jenkins 中安装的各种工具/插件进行扩展。

### Jenkins 中的插件配置是什么？

Manage Plugins部分提供了添加、删除、更新、启用或禁用Jenkins中使用的第三方插件的选项，这些插件可以帮助我们扩展Jenkins 的功能。因此，此页面上有四个选项可用，如下所示：

![Manage Jenkins 下的插件选项](https://www.toolsqa.com/gallery/Jenkins/4.Plugin%20Options%20under%20Manage%20Jenkins.png)

在哪里，

1.  更新：此部分显示所有已安装的插件，其新版本可用，可以作为更新安装。
2.  可用：此部分显示可根据需要安装且当前未安装的各种插件。
3.  已安装：此部分显示所有已安装插件及其版本的列表。
4.  高级：此部分提供通过上传“.hpi”文件安装新插件的选项。

因此，我们可以使用上述任何选项来添加、更新和编辑Jenkins 中任何受支持的插件。

### Jenkins 中的节点配置是什么？

管理节点部分提供添加、删除、控制和监视Jenkins运行作业的各种节点的功能。此外，它列出了在Jenkins服务器上注册并可用于在其上执行作业的各种节点。

![管理节点部分](https://www.toolsqa.com/gallery/Jenkins/5.Manage%20nodes%20section.jpg)

我们还可以通过单击左侧菜单中的“新节点”链接来添加新节点。此外，我们可以通过单击每个节点上的齿轮图标来编辑/更新特定节点的设置。因此，通过这种方式我们可以管理与Jenkins服务器关联的各种节点。

随后，我们现在来了解manage Jenkins部分下的“Security Section”的详细信息：

## 安全部分提供了哪些选项来管理 Jenkins？

Jenkins 被用于各种规模，从内联网上的个人机器到连接到互联网的大型服务器。根据其实施，安全性成为它的重要组成部分，并且是必须的，因为安装和实施足够安全。

从Jenkins 2.0 版开始，大多数安全功能默认启用，以在 Jenkins 中提供严格的安全性。但是，Jenkins 管理员可以根据项目/团队要求禁用这些功能。下图显示了“管理 Jenkins”部分下可用的各种安全选项：

![管理Jenkins部分的安全选项](https://www.toolsqa.com/gallery/Jenkins/6.Security%20Options%20in%20Manage%20Jenkins%20Section.png)

让我们了解 Jenkins 中“安全”部分下可用的各种安全选项。

### Jenkins 中的配置全局安全选项是什么？

“配置全局安全”部分提供了各种选项，有助于定义允许访问/使用系统的人员。此外，它还为各种与安全相关的设置提供了以下选项：

| 配置               | 头                                                         |
| -------------------- | ------------------------------------------------------------ |
| 验证           | 本节提供了各种选项，使用这些选项可以在 Jenkins 服务器上启用身份验证。其中一些选项可以是使用 LDAP、用户/组数据库、Jenkin 的用户数据库等进行身份验证。 |
| 授权           | 本节提供了在 Jenkins 服务器上配置授权级别的选项。一些选项可以是基于 Matrix 的安全性、基于项目的 Matrix 授权策略、传统模式、登录用户可以做任何事情等。 |
| 标记格式化程序 | 这指定将为输入实施的文本格式。                               |
| 代理商         | Jenkins 使用 TCP 端口与入站连接的代理进行通信。由于防火墙很难保护随机端口，你可以指定一个固定端口号并相应地配置你的防火墙。 |
| CSRF保护       | 如果 HTTP 代理位于你的浏览器客户端和 Jenkins 服务器之间，并且你在向 Jenkins 提交表单时收到 403 响应，则选中此选项可能会有所帮助。 |
| 隐藏的安全警告 | 此部分允许你禁止更新站点提供的适用于你的 Jenkins 配置的警告。如果这样做，更新站点警告管理监视器将不会显示它们。 |
| API令牌        | 此部分提供了应如何生成 API 令牌的选项。                      |
| SSH 服务器     | Jenkins 可以充当 SSH 服务器来运行 Jenkins CLI 命令的子集。你还可以在此处为 SSH 服务器指定 TCP/IP 端口号。 |

```
.
```

所以这样我们就可以在Jenkins中配置各种安全相关的选项，让它更加安全。

### Jenkins 中的管理凭证选项是什么？

Manage Credentials部分提供了创建和存储可用于各种 Jenkins 构建作业的全局凭证的选项。这是保护凭证的一种方法，因此对于用户来说，只有密钥是可访问的，他们可以直接在相应的工作中使用该密钥。

1.  首先，当你单击“管理凭据”选项时，它将显示如下所示的凭据页面：

![Jenkins的凭证存储](https://www.toolsqa.com/gallery/Jenkins/7.Credentials%20Store%20in%20Jenkins.png)

1.  其次，单击“Jenkins”选项，它会显示存储凭据的商店名称。随后，它将打开域页面，如下所示：

![凭证存储域](https://www.toolsqa.com/gallery/Jenkins/8.Domain%20for%20Credentials%20Store.png)

1.  第三，单击域“全局凭据”，它将显示凭据页面。由于没有向商店添加凭据，因此它将显示一个空页面，如下所示：

![Jenkins 中的全局凭证页面](https://www.toolsqa.com/gallery/Jenkins/9.Global%20Credentials%20Page%20in%20Jenkins.png)

1.  第四，单击“添加凭据”链接以添加新凭据。它将打开包含以下选项的页面：

![在 Jenkins 中创建凭证](https://www.toolsqa.com/gallery/Jenkins/10.Create%20Credentials%20in%20Jenkins.png)

1.  第五，从下面突出显示的值中为新创建的凭据选择“种类”和 “范围” ：

![Jenkins 中的凭据类型](https://www.toolsqa.com/gallery/Jenkins/11.Credentials%20Type%20in%20Jenkins.png)

![Jenkins 中的凭证范围](https://www.toolsqa.com/gallery/Jenkins/12.Credentials%20Scope%20in%20Jenkins.png)

1.  第六，之后指定其他字段的值，如下所示：

![Jenkins 中的凭证值](https://www.toolsqa.com/gallery/Jenkins/13.Credentials%20values%20in%20Jenkins.png)

1.  第七，这将在 Jenkins 范围内创建一个新的凭证，可以使用凭证的 ID 访问它。

因此，通过这种方式，我们可以在 Jenkins中创建和管理凭证，这些凭证可以根据它们被定义的范围在各种作业和节点中进行访问。

### Jenkins 中的 Configure Credential Providers 选项是什么？

这些选项允许配置在Jenkins服务器上允许的所有凭证提供程序。此外，当你在“管理Jenkins”页面上单击此选项时。默认情况下，它将显示允许所有提供程序和所有类型的凭据，如下所示：

![Jenkins 中的默认凭证提供程序](https://www.toolsqa.com/gallery/Jenkins/14.Default%20Credential%20Providers%20in%20Jenkins.png)

你可以允许特定类型的提供者和凭证类型，方法是从相应的下拉列表中选择包含/排除值并选择相应的复选框，如下突出显示：

![在Jenkins中包括排除凭证提供者](https://www.toolsqa.com/gallery/Jenkins/15..IncludingExcluding%20Credential%20providers%20in%20jenkins.png)

因此，通过这种方式，作为管理员，我们可以允许Jenkins服务器可以使用的所有类型的凭证提供程序，然后用户将能够创建仅允许类型的凭证。

## Status Information 部分提供了哪些选项来管理 Jenkins？

这部分更像是一个监控部分，它提供了有关Jenkins服务器和节点的各种详细信息。下图显示了在此部分下可以访问的所有类型信息的基本视图：

![Manage jenkins 中的状态信息部分](https://www.toolsqa.com/gallery/Jenkins/16.Status%20information%20section%20in%20Manage%20jenkins.png)

让我们了解所有这些部分提供的各种细节？

### Jenkins 中的系统信息部分是什么？

系统信息部分用于收集与 Java 相关的所有系统属性的信息，有关环境变量的信息，例如 Jenkins当前使用的环境变量类型，有关用户的信息，例如 Jenkins 在哪个用户下运行，有关插件的信息，例如哪个插件已启用以及有关图形形式的内存使用情况的信息。

 我们在Manage Jenkins页面的Status Information部分下点击“System Information”后就可以进入该部分。它将显示所有系统属性，如下所示：

![系统信息部分](https://www.toolsqa.com/gallery/Jenkins/17.System%20information%20section.jpg)

因此，在本节的帮助下，我们可以提取与 Java、环境变量、插件以及内存使用相关的系统信息。

### Jenkins 中的系统日志部分是什么？

系统日志部分用于查看Jenkins 日志文件。如果发生任何故障，那么这些日志在调试中起着至关重要的作用。系统日志部分为我们提供了所有 Jenkins 日志的信息。此外，我们可以在管理Jenkins页面的状态信息部分 下点击“系统日志”后进入该部分。要查看 Jenkins 相关日志，请单击下面突出显示的“所有 Jenkins 日志” 链接：

![系统日志画面](https://www.toolsqa.com/gallery/Jenkins/18.System%20Log%20screen.jpg)

我们只要点击“All Jenkins Logs”链接，就可以看到Jenkins相关的日志。

![Jenkins日志](https://www.toolsqa.com/gallery/Jenkins/19.Jenkins%20log.jpg)

因此，通过这种方式我们可以访问 Jenkins 服务器的日志。

### Jenkins 中的负载统计部分是什么？

正如我们所知，Jenkins能够处理并发构建，因此需要观察Jenkins在构建执行期间所承受的负载。还需要观察构建队列是如何工作的，以及在多长时间后构建将从构建队列中执行。从基础架构的角度来看，我们需要观察构建队列可以处理多少个构建以及一次可以执行多少个并发构建。为了获得上述所有这些要求，Jenkins提供了一个名为负载统计的部分，我们可以在其中看到显示上述功能的图形表示。

我们在Manage Jenkins页面的Status Information部分 下点击“Load Statistics”后就可以进入该部分。

![负载统计部分](https://www.toolsqa.com/gallery/Jenkins/20.Load%20statistics%20Section.jpg)

因此，一旦我们在并发构建上运行我们的作业，我们就可以在此处以图形形式看到负载性能。

### Jenkins 中的关于 Jenkins 部分是什么？

本节描述了关于Jenkins 版本的信息、它的许可证信息、它对 3rd 方库的依赖、关于许可证的静态资源的信息以及插件的依赖信息。我们在Manage Jenkins页面的Status Information栏目 下点击“About Jenkins”即可进入该版块。

![关于Jenkins部分](https://www.toolsqa.com/gallery/Jenkins/21.About%20Jenkins%20Section.jpg)

所以，这就是本节的全部内容。让我们转到下一部分，即工具和操作。

## 工具和操作部分提供了哪些选项来管理 Jenkins？

工具和操作部分描述了有关我们如何从磁盘重新加载配置、我们如何通过命令行工具使用 Jenkins 中的可用命令、有关脚本控制台的信息以及我们如何 安全地关闭 Jenkins 的信息。下图显示了该部分下的各种选项：

![管理Jenkins的工具和操作小节](https://www.toolsqa.com/gallery/Jenkins/22.Tools%20and%20Actions%20Subsections%20on%20Manage%20jenkins.png)

让我们了解这些子部分的用途是什么？

### Jenkins 中的“从磁盘重新加载配置”选项是什么？

从磁盘选项重新加载配置丢弃/拒绝内存中的所有加载数据，并从文件系统重新加载所有内容。当我们直接在磁盘上修改配置文件时，它很有用。

[Jenkins 以XML](https://en.wikipedia.org/wiki/XML)文件的形式存储它所有的系统和配置相关的数据，这些 XML 文件存储在Jenkins的主目录中。除此之外，Jenkins还存储所有构建历史。因此，如果我们计划将我们的构建作业从一个Jenkins实例移动到另一个Jenkins实例，或者如果我们计划归档我们的旧构建作业，那么我们需要在Jenkins 的构建目录中添加或删除相应的作业目录。

我们可以在“管理 Jenkins ”页面的“工具和操作”部分 下单击“从磁盘重新加载配置” 。

![从磁盘重新加载配置](https://www.toolsqa.com/gallery/Jenkins/23.Reload%20configuration%20from%20disk.jpg)

当我们点击上面的选项时，Jenkins将显示一个弹出窗口，用于确认从磁盘加载配置，接受弹出窗口后，它将加载配置并重新启动Jenkins以实现这些配置。

### Jenkins 中的 Jenkins CLI 选项是什么？

Jenkins 提供/提供了一个命令行界面，允许用户和管理员从脚本或 shell 环境访问 Jenkins。[我们可以通过SSH](https://www.ssh.com/ssh/)或[Jenkins CLI 客户端](https://www.jenkins.io/doc/book/managing/cli/)或在 Jenkins 分发的JAR 文件的帮助下访问此命令行界面。

为此，我们需要下载“jenkins-cli.jar” 并运行它，如下所示：

```java
java -jar jenkins-cli.jar -s http://localhost:8080/ -webSocket help
```

在“管理 Jenkins ”页面的“工具和操作”部分 下单击“Jenkins CLI”后，我们可以到达此选项。

![Jenkins CLI 部分](https://www.toolsqa.com/gallery/Jenkins/24.Jenkins%20CLI%20section.jpg)

在这里，我们可以看到显示的可用命令列表。借助这些命令，我们可以通过命令行工具访问不同类型的功能。

### Jenkins 中的脚本控制台是什么？

脚本控制台功能用于在服务器上运行 groovy 脚本。Jenkins提供了一个Groovy 脚本控制台，它允许我们在Jenkins主运行时或代理运行时中运行Groovy 脚本。单击“管理 Jenkins ”页面上“工具和操作”部分 下的“脚本控制台”后，我们可以到达此部分。

![脚本控制台部分](https://www.toolsqa.com/gallery/Jenkins/25.Script%20Console%20section.jpg)

要运行脚本并查看结果，请将 groovy 脚本放在控制台窗口中并单击“运行”按钮。只要我们单击“运行”按钮，我们就可以在“结果”部分下看到显示的结果。例如，为了演示目的，我在下面使用了一个简单的 groovy 脚本：

```java
println(Jenkins.instance.pluginManager.plugins)
```

现在让我们看看结果：

![Groovy 脚本结果](https://www.toolsqa.com/gallery/Jenkins/26.Groovy%20Script%20result.jpg)

因此，我们可以在上图中的结果部分看到脚本运行的结果。

### Jenkins 中的 Prepare for Shutdown 选项是什么？

我们使用Prepare for Shutdown选项来安全地关闭Jenkins。当我们点击Prepare for shutdown选项时，它会停止执行新的构建，然后它会安全地关闭Jenkins。此外，它将等待当前构建的执行完成。

![Jenkins关闭消息](https://www.toolsqa.com/gallery/Jenkins/27.Jenkins%20shutdown%20message.jpg)

一旦我们点击Prepare for shutdown选项，我们将被重定向到 Jenkins 仪表板页面，其中显示的消息 如上图所示“Jenkins 将要关闭” 。

## 要点：

-   除了配置功能之外，Jenkins 管理选项还使用 Jenkins 管理功能。
-   在配置系统中，我们可以设置主目录、系统消息、执行者数量、使用选项、相当时期、Jenkins URL、环境变量以及有关电子邮件通知的设置。
-   在全局工具配置中，我们可以定义配置以及有关我们要与 Jenkins 集成的全局工具的安装信息，如 Maven、JDK、Git、Gradle 和 Ant。
-   Manage Plugins部分添加、删除、更新、启用或禁用 Jenkins 中使用的第三方插件，这些插件可以帮助我们扩展 Jenkins 的功能。
-   管理节点部分根据我们的构建管理节点功能。
-   配置全局安全部分主要定义 Jenkins 中的身份验证和授权功能。
-   管理用户部分在 Jenkins 中创建、删除或修改用户。
-   系统信息部分收集有关系统属性、环境变量、用户和插件信息以及内存使用情况的信息。
-   系统日志部分查看 Jenkins 日志文件。
-   关于 Jenkins部分描述了关于 Jenkins 版本的信息、它的许可证信息、它对 3rd 方库的依赖、关于许可证的静态资源的信息以及插件的依赖信息。
-   从磁盘选项重新加载配置会丢弃内存中的所有加载数据，并从文件系统重新加载所有内容。
-   Jenkins CLI描述了有关可由命令行工具执行的可用命令的信息。
-   脚本控制台功能在服务器上运行 groovy 脚本。
-   Prepare for shutdown选项可以安全地关闭 Jenkins。