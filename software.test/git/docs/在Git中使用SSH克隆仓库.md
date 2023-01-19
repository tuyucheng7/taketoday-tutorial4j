在前面关于 SSH 的教程中，我们学习了什么是 SSH 认证以及如何在 Git 中生成 SSH 密钥。到目前为止，我们希望你对 SSH 有很好的理解，并且已经在你的系统上生成了 SSH 密钥。

在本教程中，我们将分享一些关于使用 SSH 克隆远程 GitHub 存储库的想法。克隆是 Git 的主要过程之一。我的意思是，如果你没有代码，你将如何做出贡献？在本课程的前面，我们遇到[了使用 HTTPS 在 Git 中进行克隆](https://www.toolsqa.com/git/git-clone/)。使用 SSH 进行克隆与使用 SSH 进行克隆几乎相似，但有一些隐藏的变化。但是，在使用 SSH 克隆之前，你必须具备一些先决条件：

-   [什么是克隆？](https://www.toolsqa.com/git/git-clone/)
-   [什么是 SSH 身份验证？](https://www.toolsqa.com/git/ssh-protocol/)
-   如何生成和添加 SSH 密钥？(本章)

使用 SSH 进行克隆的主要优点之一是你不必为一天中进行的数百次操作多次输入密码。这是通过称为 ssh-agent 的工具或程序实现的。在开始克隆过程并在我们的本地系统上下载存储库之前，最好让你了解一个简单但相关的术语，称为SSH 代理。

## 什么是 SSH 代理？

SSH Agent 是一个用于登录客户端的帮助程序，这需要登录。那么为什么知道密码还需要ssh-agent呢？好吧，除了它的安全性之外，我们对 SSH 感兴趣的原因也是一样的。将 ssh-agent 用于我们的签名目的将要求用户仅登录一次，ssh-agent 将在整个会话期间处理其余的身份验证。SSH-Agent 提供了一种安全的方式来保存你的密码并将其用于客户端程序。用户无需担心其密码的安全性，因为 ssh-agent 在与客户端程序进行身份验证时不会共享或移交这些密钥。SSH-Agent 打开一个套接字，客户端和用户可以通过该套接字交换签名数据. 因此，用户完全无需担心。

SSH-Agent 在基于 Linux 的系统和 Git Bash 中默认自带，当然。因此，无需任何额外工作，一旦用户在基于 Linux 的系统和 Git Bash 中打开终端，ssh-agent 就会激活并开始播放。但是，如果你使用其他 SSH 客户端在 Linux 以外的任何其他操作系统上使用它，则需要执行几个步骤。

### 在本地系统中设置 SSH 代理

要设置 SSH-Agent，请在目录中打开 Git Bash。

键入以下命令：

```
eval "$(ssh-agent -s)"
```

![使用 SSH 克隆存储库 - Eval ssh 代理](https://www.toolsqa.com/gallery/Git/1.Cloning%20Repository%20using%20SSH%20-%20Eval%20ssh%20agent.png)

按回车键执行命令。

![使用 SSH 克隆存储库 - 代理 pid](https://www.toolsqa.com/gallery/Git/2.Cloning%20Repository%20using%20SSH%20-%20Agent%20pid.png)

Agent Xyz将显示 ssh-agent 已启动并正在运行。屏幕上显示的数字 Xyz 是进程“ssh-agent”的进程 ID。

### 将密钥添加到 SSH 代理

运行 ssh-agent 后，我们需要通过以下命令将密钥添加到 ssh-agent：

```
ssh-add ~/.ssh/id_rsa
```

![使用 SSH 克隆存储库 - ssh 添加密钥 ssh 代理](https://www.toolsqa.com/gallery/Git/3.Cloning%20Repository%20using%20SSH%20-%20ssh%20add%20keys%20ssh%20agent.png)

按回车键执行命令，密钥将添加到你的帐户。

![添加身份](https://www.toolsqa.com/gallery/Git/4.identity-added.png)

如果你收到与上述相同的消息，则表示已成功将你的密钥添加到 ssh-agent。

现在我们都设置好了，让我们使用 ssh 克隆存储库。

## 如何使用 SSH 协议克隆存储库？

使用 SSH 克隆存储库非常简单，尤其是当你已经熟悉[通过 HTTPS 协议进行克隆时](https://www.toolsqa.com/git/git-clone/)。不过，在克隆之前，你应该确认已检查以下步骤：

-   [在你的 GitHub 帐户中有一个分叉的存储库](https://www.toolsqa.com/git/git-fork/)。
-   已生成 SSH 密钥。
-   已将 SSH 密钥添加到你的 GitHub 帐户。
-   已启动 SSH-Agent(适用于非 Linux 和非 Git Bash 用户)。
-   已将密钥添加到你的 SSH 代理(对于非 Linux 和非 Git Bash 用户)。

不过，最后两个步骤是可选的。

检查上述步骤后，导航到你的 GitHub 帐户到你要克隆的存储库页面。

![工具QA-Repo-Home](https://www.toolsqa.com/gallery/Git/5.ToolsQA-Repo-Home.png)

按克隆或下载，然后在出现的面板中按使用 SSH。

![使用 ssh 按钮](https://www.toolsqa.com/gallery/Git/6.use-ssh-button.png)

该面板将更改为使用更新后的链接 使用 SSH 克隆。![使用 ssh 克隆](https://www.toolsqa.com/gallery/Git/7.clone-with-ssh.png)

按复制到剪贴板图标复制链接。

![复制 ssh 链接](https://www.toolsqa.com/gallery/Git/8.copy-ssh-link.png)

打开Git Bash并导航到要克隆存储库的目录。

通过ls命令检查存储库的内容。

![存储库内容](https://www.toolsqa.com/gallery/Git/9.repository-content.png)

在 Git bash 中键入以下命令以使用 SSH 克隆存储库。

```
git clone <link>
```

![git-clone-ssh](https://www.toolsqa.com/gallery/Git/10.git-clone-ssh.png)

克隆到 ToolsQA将显示克隆正在进行中。

![克隆到 Toolsqa-ssh](https://www.toolsqa.com/gallery/Git/11.Cloning-into-Toolsqa-ssh.png)

克隆完成后将出现以下消息。

![克隆完成-ssh](https://www.toolsqa.com/gallery/Git/12.cloning-done-ssh.png)

再次通过ls命令检查目录的内容，并注意存储库的创建。

![toolsqa 创建的](https://www.toolsqa.com/gallery/Git/13.toolsqa-created.png)

很简单，不是吗？克隆是[Git](https://www.toolsqa.com/git/git-fork/)的核心部分。由于 GitHub 将具有相似兴趣的人们聚集在一起并为彼此的项目做出贡献，因此克隆是实现这一过程的原因。同样，正如我之前所说，使用 SSH 或 HTTPS 完全取决于个人使用。通过本教程，我们完成了 GitHub 和 Git 中的 SSH 部分，现在将转向 Git 中的分支。