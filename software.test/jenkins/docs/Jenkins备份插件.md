备份和还原是任何大型组织通常需要并定期遵循的两个基本操作。从安全和恢复的角度来看，这个备份和恢复过程对任何组织来说都是必不可少的。备份是任何用户创建和存储数据副本的过程，以便在发生数据丢失或损坏时，用户应该恢复受保护的数据。同样的情况也发生在Jenkins身上。在本文中，我们将讨论如何使用Jenkins 备份插件在 Jenkins 中进行备份。让我们从了解以下几点开始学习：

-   Jenkins 中的备份是什么？
-   如何在 Jenkins 中安装 ThinBackup 插件？
    -   如何使用 ThinBackup 插件进行备份？
    -   还有，如何使用 ThinBackup 插件恢复？
-   如何在 Jenkins 中安装 Backup 插件？
    -   如何使用备份插件备份？
    -   还有，如何使用 ThinBackup 插件恢复？

## Jenkins 中的备份是什么？

无论我们通过Jenkins执行什么，或者在 Jenkins 中进行任何设置，它都会以备份的形式存储在Jenkins 主目录中。此备份包括数据和与配置相关的设置，如[作业配置、](https://www.original.toolsqa.com/jenkins-configuration/) [插件、](https://www.original.toolsqa.com/jenkins-manage-plugins/) 插件配置、构建日志等。因此，如果需要，必须备份此重要信息以便将来使用此备份。Jenkins提供了不同类型的插件来实现上述目标。在接下来的部分中，让我们探索不同类型的插件。

## 如何在 Jenkins 中安装 ThinBackup 插件？

如果我们想备份最关键的全局和特定于作业的配置文件，那么 Jenkins 中的ThinBackup插件很方便。下面我们进一步看一下这个Plugin的安装过程：

第 1 步：转到插件管理器，在“可用” 选项卡下，执行以下步骤：

1.  在搜索框中输入文本“备份” 。
2.  选中“ThinBackup”插件的复选框。
3.  单击“立即下载并在重启后安装”按钮。

第二步：我们点击按钮后，很快就会开始安装插件，安装成功会显示成功状态。

![插件安装成功](https://www.toolsqa.com/gallery/Jenkins/2.Successful%20installtion%20of%20plugin.jpg)

第三步：安装成功后，我们可以验证它应该显示在“已安装” 选项卡下，并且会显示卸载按钮。

![插件安装验证](https://www.toolsqa.com/gallery/Jenkins/3.Validation%20of%20installation%20of%20plugin.jpg)

那么，这样，我们就可以在Jenkins中安装ThinBackup插件了。在下一节中，我们将了解如何使用Jenkins 中的 ThinBackup 插件执行备份。

### 如何使用 ThinBackup 插件进行备份？

在上一节中，我们看到了如何在 Jenkins 中安装 ThinBackup 插件。在本节中，我们将了解如何使用ThinBackup插件在 Jenkins 中进行备份。请按照以下步骤来实现它：

第 1 步：转到管理插件并单击Jenkins 中“未分类”部分下提到的“ThinBackup” 选项。

![单击 ThinBackup 选项](https://www.toolsqa.com/gallery/Jenkins/4.Click%20on%20ThinBackup%20option.jpg)

第 2 步：一旦我们点击ThinBackup选项，我们将重定向到ThinBackup 插件页面，我们将在其中看到 3 个选项。第一步，我们需要进行一些设置，因此请单击“设置” 链接。

![点击设置链接](https://www.toolsqa.com/gallery/Jenkins/5.Click%20on%20settings%20link.jpg)

第 3 步：单击“设置”链接后，我们将重定向到ThinBackup 配置页面。在这里，我们需要提供要保存备份的“备份目录”  ，然后单击“保存” 按钮。比如我们在C目录下创建了一个名为Jenkins Backup的文件夹，我们想把Jenkins备份存放在这个文件夹里面。

![备份目录设置](https://www.toolsqa.com/gallery/Jenkins/6.backup%20directory%20settings.jpg)

第 4 步：单击保存按钮后，我们再次重定向到ThinBackup页面，现在我们已准备好进行备份。因此，单击“立即备份” 链接。

![单击立即备份](https://www.toolsqa.com/gallery/Jenkins/7.Click%20on%20Backup%20Now.jpg)

第五步：点击立即备份链接后，备份过程将开始，操作成功后，我们可以在第三步提到的同一目录中看到备份。

![目录中存在备份](https://www.toolsqa.com/gallery/Jenkins/8.Backup%20present%20in%20directory.jpg)

单击上面的文件夹后，我们可以看到该文件夹中存在的所有文件。

![备份文件夹中的文件](https://www.toolsqa.com/gallery/Jenkins/9.Files%20inside%20Backup%20folder.jpg)

因此，通过这种方式，我们可以使用ThinBackup插件在 Jenkins 中进行备份。现在，要恢复备份，请按照进一步的步骤操作。

### 如何使用 ThinBackup 插件恢复？

使用上述步骤完成备份后，我们可以按照以下步骤使用相同的插件恢复备份：

第 1 步：单击ThinBackup页面上的“还原” 链接

![点击恢复链接](https://www.toolsqa.com/gallery/Jenkins/10.Click%20on%20Restore%20Link.jpg)

第 2 步：一旦我们点击恢复链接，我们将重定向到“恢复配置”页面，在这里我们需要执行以下步骤：

1.  单击下拉菜单“从中恢复备份”  ，然后选择要进行的备份。我们只备份了一次，所以目前，它只显示一个选项。
2.  现在，单击“恢复”按钮。
3.  一旦我们单击恢复按钮，该消息将显示在“构建队列”部分下。这里 Jenkins 将重新启动。

![恢复备份](https://www.toolsqa.com/gallery/Jenkins/11.Restoring%20backup.jpg)

所以，通过这种方式，我们可以在 Jenkins 中恢复备份。在下一节中，我们将讨论 Jenkins 中的正常备份策略。

## 如何在 Jenkins 中安装 Backup 插件？

完成ThinBackup插件后，让我们转向常规备份插件。如果我们想备份Hudson 配置文件，那么我们可以使用这个插件。但是，首先，让我们进一步了解此插件的安装过程：

第 1 步：转到插件管理器并执行以下步骤：

1.  在搜索框中输入文本“备份” 。
2.  选中“备份”插件的复选框。
3.  单击“立即下载并在重启后安装”按钮。

![Jenkins Backup Plugin 安装备份插件](https://www.toolsqa.com/gallery/Jenkins/12.Jenkins%20Backup%20Plugin%20Installation%20of%20backup%20plugin.jpg)

第二步：完成以上步骤后，我们会看到备份插件的安装进度。必须看到ThinBackup会自动显示成功，因为我们已经成功安装了它。

![正在进行备份插件安装](https://www.toolsqa.com/gallery/Jenkins/13.Backup%20plugin%20installation%20in%20progress.jpg)

第 3 步：

那么，这样，我们就可以在Jenkins中安装Backup插件了。在下一节中，我们将了解如何使用 Jenkins 中的备份插件执行备份。

### 如何使用备份插件备份？

在上一节中，我们看到了如何在 Jenkins 中安装备份插件。在本节中，我们将了解如何使用备份插件在 Jenkins 中进行备份。请按照以下步骤来实现它：

第 1 步：转到管理 Jenkins，然后单击未分类部分下的“备份管理器”  。

![Jenkins 备份插件备份管理器选项](https://www.toolsqa.com/gallery/Jenkins/14.Jenkins%20Backup%20Plugin%20Backup%20manager%20option.jpg)

第 2 步：点击后，它会将我们重定向到备份管理器屏幕，在这里我们将看到 3 个选项，但首先，我们需要点击“设置” 链接。

![Jenkins Backup Plugin 点击设置链接](https://www.toolsqa.com/gallery/Jenkins/15.Jenkins%20Backup%20Plugin%20Click%20on%20Setup%20link.jpg)

第 3 步：只要我们点击设置链接，它就会将我们重定向到备份配置文件页面。在这里，我们需要提供要保存备份的“备份目录”  ，然后单击“保存” 按钮。比如我们在C目录下创建了一个名为Backup_Hudson的文件夹，我们要在这个文件夹里面存放备份。

![Jenkins Backup Plugin 备份目录设置](https://www.toolsqa.com/gallery/Jenkins/16.Jenkins%20Backup%20Plugin%20backup%20directory%20setup.jpg)

第 4 步：单击保存按钮后，我们将重定向到备份管理器页面，现在我们已准备好进行备份。因此，单击“Backup Hudson configuration” 链接。

![单击 Backup Hudson 配置链接](https://www.toolsqa.com/gallery/Jenkins/17.Click%20on%20Backup%20Hudson%20configuration%20link.jpg)

第五步：点击之后，我们可以看到备份的进度。完成后，屏幕上会显示一条消息。

![Jenkins Backup Plugin 备份成功日志](https://www.toolsqa.com/gallery/Jenkins/18.Jenkins%20Backup%20Plugin%20Success%20logs%20of%20backup.jpg)

我们可以在步骤 3 中提到的目录中看到 zip 格式的备份。

![Jenkins Backup Plugin 备份成功日志](https://www.toolsqa.com/gallery/Jenkins/18.Jenkins%20Backup%20Plugin%20Success%20logs%20of%20backup.jpg)

第六步：解压后我们可以看到文件夹里面的文件。

![目录中的 Jenkins Backup Plugin 备份](https://www.toolsqa.com/gallery/Jenkins/19.Jenkins%20Backup%20Plugin%20backup%20in%20directory.jpg)

因此，通过这种方式，我们可以使用备份插件在 Jenkins 中进行备份。现在，要恢复备份，请按照进一步的步骤操作。

### 如何使用 ThinBackup 插件恢复？

使用上述步骤完成备份后，我们可以按照以下步骤使用相同的插件恢复备份：

第 1 步：单击“备份管理器”页面上的“恢复 Hudson 配置” 链接

![Jenkins Backup Plugin 解压缩备份文件夹](https://www.toolsqa.com/gallery/Jenkins/20.Jenkins%20Backup%20Plugin%20unzipping%20of%20backup%20folder.jpg)

第 2 步：只要我们点击恢复 Hudson 配置链接，它就会将我们重定向到“恢复” 页面，在这里我们需要执行以下步骤：

1.  单击要进行备份的单选按钮。我们只备份了一次，所以目前，它只显示一个选项。
2.  现在，单击“启动还原”按钮。

![Jenkins 备份插件恢复设置](https://www.toolsqa.com/gallery/Jenkins/21.Jenkins%20Backup%20Plugin%20restore%20setup.jpg)

只要我们点击启动恢复按钮，恢复过程就会开始。

![Jenkins 备份插件恢复过程开始](https://www.toolsqa.com/gallery/Jenkins/22.Jenkins%20Backup%20Plugin%20Restoration%20process%20started.jpg)

第三步：操作成功后，我们可以在上一步高亮显示的目录下看到restore文件夹。

![Jenkins Backup Plugin 还原目录](https://www.toolsqa.com/gallery/Jenkins/23.Jenkins%20Backup%20Plugin%20Restore%20directory.jpg)

所以，这样，我们就可以使用备份插件在 Jenkins 中恢复备份了。

## 关键要点

-   备份是任何用户创建和存储数据副本的过程，以便在发生数据丢失或损坏时，用户应恢复受保护的数据。
-   Jenkins 提供了不同类型的插件来备份数据。ThinBackup 插件和备份插件。
-   如果我们想要备份最关键的全局和特定于作业的配置文件，我们可以使用ThinBackup 插件。
-   如果我们想备份 Hudson 配置文件，我们可以使用Backup 插件。