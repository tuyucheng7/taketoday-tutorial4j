设置 Git可能会令人生畏，尤其是对于那些第一次尝试版本控制系统或从 Subversion 迁移过来的人。过去，在 Windows 上安装和使用 Git 非常麻烦。然而，今天在 Windows 上使用 Git 非常容易，无论是通过Git Bash，如果你是命令行的粉丝，或者如果你更喜欢图形界面。

## 在 Windows 上安装 Git 的步骤

1.  下载适用于 Windows 的最新[Git](https://git-for-windows.github.io/)。

![在 Windows 上安装 Git](https://www.toolsqa.com/gallery/Git/1.Install%20Git%20on%20Windows.png)

1.  转到存储新下载的文件夹，在我的机器上默认文件夹是下载文件夹。双击安装程序。安装程序根据 Windows 操作系统配置保存在计算机上。我的机器是64 位的。

![Git_Installation_1](https://www.toolsqa.com/gallery/Git/2.Git_Installation_1.png)

注意：当你成功启动安装程序后，你应该会看到 Git 安装向导屏幕。按照下一步和完成提示完成安装。默认选项对大多数用户来说非常合理。

注意：在 2017 年 9 月 9 日编写教程时，最新版本是Git-2.14.1。

1.  你可能希望将安装保存到另一个文件夹，所以现在是这样做的机会。我只想将它保存在我的Program Files\Git 中建议的默认文件夹中。

![Git_Installation_2](https://www.toolsqa.com/gallery/Git/3.Git_Installation_2.png)

1.  这是在Program Menu下存储Git 快捷方式的选项。

![Git_Installation_3](https://www.toolsqa.com/gallery/Git/4.Git_Installation_3.png)

1.  这是在询问你是喜欢从Windows 命令提示符使用 Git还是喜欢使用其他程序(如Git Bash)。截至目前，为了教程的简单性，只需选择Windows Cmd，稍后我们还将介绍Git Bash和其他工具。

![Git_Installation_4](https://www.toolsqa.com/gallery/Git/5.Git_Installation_4.png)

1.  如果你安装了PuTTY/TortoiseSVN，你可能会看到这个屏幕，否则就忽略它。无论如何，使用OpenSSL让事情变得简单。

![Git_Installation_5](https://www.toolsqa.com/gallery/Git/6.Git_Installation_5.png)

1.  在这里，我们建议选择Checkout Windows-style, commit Unix-style line endings选项。完成后选择下一步。

![Git_Installation_6](https://www.toolsqa.com/gallery/Git/7.Git_Installation_6.png)

1.  同样，只需使用默认选择并继续前进。

![Git_Installation_7](https://www.toolsqa.com/gallery/Git/8.Git_Installation_7.png)

1.  只需使用默认选择，因为我们将在后面的高级章节中介绍详细信息。

![Git_Installation_8](https://www.toolsqa.com/gallery/Git/9.Git_Installation_8.png)

1.  现在，一切都完成了。根据你的机器速度，这只需几分钟即可完成安装。

![Git_Installation_9](https://www.toolsqa.com/gallery/Git/10.Git_Installation_9.png)

1.  完成后，只需单击“完成”按钮。

![Git_Installation_10](https://www.toolsqa.com/gallery/Git/11.Git_Installation_10.png)

1.  让我们来验证 Git 的安装是否顺利。转到cmd并键入git并按enter。你应该在屏幕上得到以下输出。

![Git_Installation_11](https://www.toolsqa.com/gallery/Git/12.Git_Installation_11.png)

cmd wildow 将显示你可以尝试使用 git 的不同选项和命令。只需稍微热身并使用以下命令并观察输出以了解你的理解：

-   git --version
-   git --help