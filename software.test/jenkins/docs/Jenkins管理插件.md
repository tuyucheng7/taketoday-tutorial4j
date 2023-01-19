[在之前的文章中，我们演示了Jenkins](https://www.toolsqa.com/jenkins/what-is-jenkins/)中的这么多功能，而所有文章中的一个共同点就是针对特定功能的“插件安装”。那么，我们脑海中总会浮现出一个问题，什么是插件？为什么需要在 Jenkins 中实现任何功能？在本文中，我们将了解Plugins的核心概念。此外，我们将看到如何以不同的方式安装插件？ 因此，让我们通过涵盖以下主题中的详细信息来开始了解Jenkins 如何管理插件的旅程：

-   Jenkins 中的插件是什么？
    -   为什么需要插件？
-   如何在 Jenkins 中安装插件？
    -   如何在 Jenkins 中手动安装插件？
    -   以及，如何升级 Jenkins 中的插件？
-   如何卸载 Jenkins 中的插件？

## Jenkins 中的插件是什么？

Jenkins 插件是一个java-archive 格式的包，在开发过程中遵循特定的结构。每个插件都包含所有相关信息，如文件、图像、代码和其他额外信息。这就是为什么我们也将插件称为“独立的”。所有 Jenkins 插件都将.hpi 作为文件扩展名。

Jenkins 所需的所有这些插件都可以在[插件存储库](https://plugins.jenkins.io/)中找到，并可以从此处下载以满足特定需求，但主要问题仍然悬而未决，即为什么需要这些插件以及这个问题的答案，我们将在下一小节中获得。

### 为什么需要插件？

这个世界上的每个工具都试图匹配某些功能。“可扩展性”是此列表中的基本特征之一。除了其核心功能之外，我们还可以借助可扩展性来增强该工具的功能。Jenkins 中的插件根据用户特定的需求在这种情况下做同样的工作。

Jenkins 基本上定义了 Jenkins 社区开发人员通过一些自定义代码实现和扩展的接口集。所以，社区开发者根据需要的功能开发插件，我们可以在Jenkins中安装那个插件来实现那个功能。它是 Jenkins 中插件的基本概念。现在，在下一节中，让我们看看如何在 Jenkins 中安装插件。

## 如何在 Jenkins 中安装插件？

在上一节中，我们了解了插件的概念及其要求。在本节中，我们将看到如何在 Jenkins 中安装插件，以根据我们的要求使用 Jenkins 中的特定功能。请按照以下步骤在 Jenkins 中安装插件：

第一步：转到Jenkins 仪表板并单击下图中突出显示的“管理 Jenkins ”链接。

![点击管理Jenkins](https://www.toolsqa.com/gallery/Jenkins/1.Click%20on%20manage%20Jenkins.jpg)

第 2 步：一旦我们点击管理 Jenkins链接，我们将重定向到管理 Jenkins 页面，在这里我们需要点击系统配置部分下的“管理插件”。

![点击管理插件](https://www.toolsqa.com/gallery/Jenkins/2.Click%20on%20manage%20plugins.jpg)

第三步：现在，点击管理插件选项后，我们进入了可用插件的页面。假设我们要在 Jenkins 中安装“ GitHub 身份验证”插件。为此，请按照以下步骤操作：

-   单击“可用”选项卡。
-   正如我们搜索“ GitHub Authentication ”一样，在搜索框中搜索你要安装的插件。
-   选中所需插件的复选框。
-   之后，单击“立即下载并在重启后安装”按钮。我们也可以点击“ Install without restart ”按钮，但我们始终建议选择重启后安装，以有效安装插件。

![Jenkins Manage Plugins 在 Jenkins 中安装插件](https://www.toolsqa.com/gallery/Jenkins/3.Jenkins%20Manage%20Plugins%20Installing%20plugin%20in%20Jenkins.jpg)

第四步：以上步骤完成后，我们会进入插件安装进度页面，插件安装成功后，我们可以看到插件安装成功的信息。在安装过程中，任何依赖的插件都将与所需的插件一起自动开始安装。

![插件安装成功提示](https://www.toolsqa.com/gallery/Jenkins/4.successful%20message%20of%20plugin%20installation.jpg)

第五步：现在重启 Jenkins，重启成功后，我们可以验证插件安装是否成功，它会显示在“ Installed ”选项卡下。

![验证 pf 插件安装](https://www.toolsqa.com/gallery/Jenkins/5.validation%20pf%20plugin%20installation.jpg)

因此，通过这种方式，我们可以在 Jenkins 中安装任何插件。现在在下一小节中，让我们看看如何在 Jenkins 中手动安装任何插件。

### 如何在 Jenkins 中手动安装插件？

我们已经在上一节中看到如何在 Jenkins 中安装插件，但有时，我们需要在 Jenkins 中安装一些特定版本的插件。在这种情况下，我们无法通过上一节中提到的步骤安装插件。相反，在这种情况下我们需要手动安装插件。因此，让我们借助以下步骤了解手动插件安装过程：

第 1 步：假设我们要手动安装“ HTML Publisher ”插件。因此，首先我们需要转到 Jenkins 的[插件页面](https://plugins.jenkins.io/?src=contextnavchildmode)并搜索我们要安装的插件。

![搜索插件](https://www.toolsqa.com/gallery/Jenkins/6.Search%20plugin.jpg)

第二步：我们一点击，就会看到相关的插件。现在点击受人尊敬的插件。

![Jenkins Manage Plugins 点击尊享插件下载](https://www.toolsqa.com/gallery/Jenkins/7.Jenkins%20Manage%20Plugins%20Click%20on%20respected%20plugin%20to%20download.jpg)

第 3 步：点击相应的插件后，我们将前往插件页面。在这里，只要我们点击“发布”选项卡，我们就可以看到插件版本。

![单击发布选项卡](https://www.toolsqa.com/gallery/Jenkins/8.Click%20on%20release%20tab.jpg)

第 4 步：在这里，我们可以看到最新版本是 1.25，但假设我们要安装 1.24 版，所以我们需要向下滚动页面并转到该版本并单击该链接。单击后，将下载hpi文件。

![Jenkins Manage Plugins 插件已下载](https://www.toolsqa.com/gallery/Jenkins/9.Jenkins%20Manage%20Plugins%20Plugin%20downloaded.jpg)

第 5 步：现在，在Jenkins中，转到插件管理器并单击“高级”选项卡。

![单击高级选项卡](https://www.toolsqa.com/gallery/Jenkins/10.click%20on%20advanced%20tab.jpg)

第 6 步：现在，向下滚动页面，在上传插件部分下，我们需要选择我们在上一步中下载的文件。之后，单击上传按钮。

![Jenkins Manage Plugins 选择要上传的插件](https://www.toolsqa.com/gallery/Jenkins/11.Jenkins%20Manage%20Plugins%20Choose%20plugin%20to%20upload.jpg)

第七步：我们一点击上传按钮，就会进入插件安装进度页面，在这里我们可以看到插件安装开始了。安装后我们会看到一条成功消息。

![插件安装成功](https://www.toolsqa.com/gallery/Jenkins/12.Plugin%20installation%20successful.jpg)

第 8 步：现在，为了验证，转到插件管理器，在“已安装”选项卡下，我们可以看到我们安装的版本的HTML 发布者插件。

![插件安装验证](https://www.toolsqa.com/gallery/Jenkins/13.validation%20of%20plugin%20installation.jpg)

所以，通过这种方式，我们可以在 Jenkins 中手动安装插件。在下一小节中，我们将看到如何升级 Jenkins 中的任何插件。

### 如何在 Jenkins 中升级插件？

我们在上一节中看到如何在 Jenkins 中手动安装插件，此时，我们安装了 1.24 版的“ HTML 发布者插件”。尽管如此，正如我们之前看到的，它的最新版本是 1.25，所以问题是我们如何才能移动到任何插件的最新版本。答案是我们需要升级插件。所以请按照以下步骤升级插件：

第一步：转到Jenkins 仪表板并单击“管理 Jenkins ”。

![点击管理Jenkins](https://www.toolsqa.com/gallery/Jenkins/14.Click%20on%20manage%20jenkins.jpg)

第 2 步：一旦我们点击管理 Jenkins 链接，我们将重定向到管理 Jenkins 页面，在这里我们需要点击系统配置部分下的“管理插件”。

![点击管理插件](https://www.toolsqa.com/gallery/Jenkins/15.Click%20on%20manage%20plugins.jpg)

第三步：现在，我们将进入插件管理器页面。请按照以下步骤升级插件：

-   单击“更新”选项卡。
-   就像我们搜索“ HTML 发布者插件”一样，在搜索框中搜索你要升级的插件。在这里我们可以很容易地看到插件的最新版本以及当前安装的版本。
-   选中所需插件的复选框。
-   之后，单击“立即下载并在重启后安装”按钮。

![选择升级的 Jenkins Manage Plugins 插件](https://www.toolsqa.com/gallery/Jenkins/16.Jenkins%20Manage%20Plugins%20Plugin%20selected%20for%20upgrade.jpg)

第四步：我们完成以上步骤后，就会出现插件安装进度页面，安装成功后，我们可以看到安装成功的信息。

![Jenkins Manage Plugins 插件升级成功](https://www.toolsqa.com/gallery/Jenkins/17.Jenkins%20Manage%20Plugins%20Successful%20upgrade%20of%20plugin.jpg)

第五步：现在我们需要重启Jenkins，重启后，再次进入插件管理器页面，在已安装选项卡下，验证最新升级插件的版本。

![Jenkins Manage Plugins 成功升级插件](https://www.toolsqa.com/gallery/Jenkins/18.Jenkins%20Manage%20Plugins%20Successfully%20upgraded%20plugin.jpg)

那么，通过这种方式，我们就可以升级Jenkins 中的插件了。在下一节中，让我们看看如何卸载 Jenkins 中的任何插件。

## 如何在 Jenkins 中卸载插件？

在上一节中，我们了解了如何在 Jenkins 中安装任何插件以及如何在 Jenkins 中手动安装任何插件。因此，在本节中，让我们了解如何借助以下步骤在 Jenkins 中卸载任何插件：

第一步：成功登录后转到Jenkins 仪表板，然后单击下图中突出显示的“管理 Jenkins ”选项。

![点击管理Jenkins](https://www.toolsqa.com/gallery/Jenkins/19.Click%20on%20manage%20jenkins.jpg)

第 2 步：一旦我们点击管理 Jenkins 链接，我们将重定向到管理 Jenkins页面，在这里我们需要点击系统配置部分下的“管理插件”。

![Jenkins Manage Plugins 点击manage plugins](https://www.toolsqa.com/gallery/Jenkins/20.Jenkins%20Manage%20Plugins%20Click%20on%20manage%20plugins.jpg)

第 3 步：现在，例如，如果我们要卸载在前面步骤中安装的“ GitHub 身份验证”插件，请按照以下步骤操作：

-   单击“已安装”选项卡。
-   就像我们搜索“ GitHub 认证”插件一样，在搜索框中搜索要卸载的插件。
-   如果未选中，请选中所需插件的复选框。
-   单击“卸载”按钮。

![Jenkins Manage Plugins 卸载插件](https://www.toolsqa.com/gallery/Jenkins/21.Jenkins%20Manage%20Plugins%20uninstallation%20of%20plugin.jpg)

第四步：我们完成以上步骤后，就会出现卸载确认页面，这里我们需要点击“是”按钮。

![卸载确认](https://www.toolsqa.com/gallery/Jenkins/22.Uninstallation%20confirmation.jpg)

单击是按钮后，我们将再次重定向到插件管理器页面。

第 5 步：现在重新启动 Jenkins 并再次转到插件管理器页面。如果插件卸载成功，我们会在可用选项卡下再次找到该插件。

![验证卸载成功](https://www.toolsqa.com/gallery/Jenkins/23.Validation%20of%20successful%20uninstallation.jpg)

所以，通过这种方式，我们可以卸载 Jenkins 中的任何插件。

因此，通过使用上述方式，我们可以在 Jenkins 中安装/卸载和更新插件，并通过使用Jenkins Manage Plugins功能来利用 Jenkins 的扩展功能。

## 关键要点

-   Jenkins plugin是java-archive格式的包，在构建时基本遵循特定的结构。每个插件都包含Jenkins 中的.hpi扩展名。
-   Jenkins 基本上定义了 Jenkins 社区开发人员通过一些自定义代码实现和扩展的接口集。因此，社区开发人员根据需要的功能开发插件。然后，我们可以安装该插件以在 Jenkins 中实现该功能。
-   我们可以在 Jenkins 管理插件选项下搜索任何插件，然后从那里安装它。
-   如果我们想手动安装插件或任何特定版本的插件，那么我们需要上传插件。同样，我们应该在上传插件部分下进行。
-   我们也可以升级 Jenkins 中的插件版本。
-   此外，如果不需要该插件，我们可以在 Jenkins 中卸载该插件。