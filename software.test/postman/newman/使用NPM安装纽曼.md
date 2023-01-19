在上一个教程中，我们了解了自动化测试的基础知识，并熟悉了Newman。Newman，让我想起Seinfeld(80年代美剧)中的一个著名人物Newman，是Postman交付的一个命令行集成工具。在本教程中，我们将尝试在我们的系统中安装 Newman。

在上一个教程中，我们还讨论了Newman 完全在 Node.js 上工作，所以它一定让你印象深刻，我们肯定会在我们的系统中需要它。对，那是正确的。纽曼安装需要两件事：

-   节点.js
-   NPM(节点包管理器)

但在开始安装之前，我们将在下一节中了解什么是 NPM。

### 什么是 NPM？

Node Package Manager 或NPM 是 Javascript 编程语言的包管理器，也是 Node.js 的默认包管理器。它就像一个项目存储库，了解每个项目的需求。据 npm 官网介绍，它是全球最大的软件注册中心，每周约有 30 亿次下载。注册表包含超过 600,000 个包(代码构建块)。

NPM 使 JS ( Javascript ) 开发人员可以轻松地在存储库上共享代码和问题。然后你可以在下一个项目中或任何想要与你已经开发的相同功能的人重用此代码。这使得开发人员可以非常轻松地在更短的时间内更好地编写代码。

虽然我们不需要太多关于 NPM 的知识，但 NPM 比包和注册表要多得多。如果你对此感兴趣，可以[在此处](https://www.npmjs.com/)访问他们的网站。我们现在将尝试安装 Newman，正如我们上面所讨论的，Newman 需要 node.js 和 NPM。因此，首先我们将尝试按照以下步骤安装这两个东西。

## 如何安装 Node.js

如前所述，我们已经在我们的网站上为你提供了安装 node.js 的教程。但是在访问该页面之前，你必须确保你之前没有安装过 node.js。

1.  打开命令提示符( Mac终端)
2.  输入以下内容

节点 -v(适用于 Windows)

节点 --version (对于 mac)

![Command_Prompt_Node-v](https://www.toolsqa.com/gallery/Postman/1.Command_Prompt_Node-v.png)

1.  如果你看到版本号，那么你之前已经安装了 node.js，不需要做任何其他事情。

![命令提示符节点版本](https://www.toolsqa.com/gallery/Postman/2.Command_Prompt_Node_Version.png)

1.  如果你看到任何错误或版本号以外的任何其他内容，则必须安装 node.js。

此外，如果你已按照我们网站上提供的安装 node js 的步骤进行操作，则你的系统中也必须安装了 NPM。

## 如何安装节点包管理器

1.  打开命令提示符(mac终端)

2.在命令提示符中键入以下内容

Windows中的 npm -v

npm --linux /mac中的版本

你也可以通过此[链接](https://www.npmjs.com/get-npm)下载和了解 npm

![命令_Prmopt_NPM-V](https://www.toolsqa.com/gallery/Postman/3.Command_Prmopt_NPM-V.png)

3.如果你在按回车键时看到版本号，那么你已经安装了 npm，你可以继续安装 Newman。

![命令_Prmopt_NPM_版本](https://www.toolsqa.com/gallery/Postman/4.Command_Prmopt_NPM_Version.png)

如果你没有看到某个版本，那么你可能需要从我们网站上的教程中重新安装它并再次检查。

由于现在我们已经安装了两个先决条件，我们现在将继续在我们的系统上安装 Newman。

## 如何使用 NPM 安装 Newman？

要在你的系统中安装 Newman，请按照以下步骤操作。

1.打开命令提示符(Mac 终端)

1.  输入 npm install -g newman

![使用 NPM 安装纽曼](https://www.toolsqa.com/gallery/Postman/5.Install%20Newman%20using%20NPM.png)

注意：该命令与 Mac 相同。

1.  这将通过 NPM 安装新的依赖项。按回车键后，你将看到以下屏幕(如果成功获取并安装了 npm)。

![Command_Prompt_Newman_In_Progress](https://www.toolsqa.com/gallery/Postman/6.Command_Prompt_Newman_In_Progress.png)

1.  安装 Newman 需要几分钟时间。安装后，你将看到以下行。

纽曼@3.9.4

在 187.889 秒内添加了 196 个包(时间可能会有所不同)。

为了确认，你还可以查看纽曼的版本。

1.  在命令提示符中键入以下内容(如果是 Mac，则为终端)

纽曼 -v (视窗)

纽曼 --version ( \Mac\ )

![Command_Prompt_Newman-v](https://www.toolsqa.com/gallery/Postman/7.Command_Prompt_Newman-v.png)

1.  如果你在回车后看到版本号，说明你已经成功安装了 Newma，否则，它就失败了，你必须重试。

![Command_Prompt_Newman_Version](https://www.toolsqa.com/gallery/Postman/8.Command_Prompt_Newman_Version.png)

因此，按照这些步骤，我们已经在我们的系统上成功安装了 Newman。我们现在将继续下一个教程，开始将 Newman 与 Postman 结合使用。