[我们在上一个教程中讨论了SSH 身份验证](https://www.toolsqa.com/git/ssh-protocol/)的好处和用法，在本教程中，我们将学习Git SSH 身份验证。SSH 的优势表明，一旦初始设置完成并准备就绪，SSH 使用起来更加安全和方便。但这只是理论。要使用 SSH 密钥，我们必须生成它们并将它们添加到我们的 GitHub 帐户。在本教程中，我们将介绍以下内容：

-   如何在 Git 中通过 SSH 进行身份验证？
-   SSH 中生成公钥和私钥的过程。
-   如何将 SSH 密钥添加到 GitHub 帐户？
-   通过 Git Bash 验证 SSH 密钥的方法。

## 如何建立 Git SSH 认证？

身份验证为我们提供了一种只允许授权用户访问机密数据或任何数据的方法。身份验证和授权是一个广泛的话题。有兴趣的可以多学习[认证和授权](https://www.toolsqa.com/postman/basic-authentication-in-postman/)，刷刷概念。[在 Git 和 GitHub 中，虽然我们在存储库克隆](https://www.toolsqa.com/git/git-clone/)教程中学习了通过 HTTPS进行身份验证，但现在是使用 GitHub 提供的另一个选项进行身份验证的时候了。

你可能会疑惑，就像我们在 GitHub 中按 clone 复制 URL 一样；同样，我们将选择 SSH 选项并按照步骤操作。好吧，让我们试试看会发生什么。

1.导航到[ToolsQA 存储库页面](https://github.com/harishrajora805/ToolsQA)

![Github 克隆或下载按钮](https://www.toolsqa.com/gallery/Git/1.Github%20clone%20or%20download%20button.png)

1.  你会找到克隆或下载按钮。单击按钮以打开克隆选项。

![Git SSH 身份验证 - 克隆](https://www.toolsqa.com/gallery/Git/2.Git%20SSH%20Authentication%20-%20Cloning.png)

1.  转到“使用 SSH ”按钮，看看它是否带我们到某个地方。

![Git 中的 SSH 身份验证 - 无密钥警告](https://www.toolsqa.com/gallery/Git/3.SSH%20Authentication%20in%20Git%20-%20no%20keys%20warning.png)

我们收到一条消息，指出我们目前的 GitHub 帐户中没有任何公共 SSH 密钥，后面是生成密钥的链接。所以，我们在SSH认证贴中的学习是非常正确的。我们需要密钥通过 SSH 与服务器通信。在下一节中，我们将生成 SSH 密钥，但在此之前，作为预防措施，你应该记住一条注意事项。在某些情况下，用户可能已经在其存储库中拥有 SSH 密钥对。因此，在生成密钥之前，最好先检查现有的 SSH 密钥(如果有)。

### 检查本地存储库中的 SSH 密钥

检查本地存储库中的 SSH 密钥是一个简单的几步过程。

1.  在你的系统上打开Git Bash 。
2.  输入以下命令并按回车键：

```
ls -l ~/.ssh
```

![Git SSH 身份验证 - 检查 ssh 密钥](https://www.toolsqa.com/gallery/Git/4.Git%20SSH%20Authentication%20-%20Check%20ssh%20keys.png)

命令执行后，如果你没有密钥，你将看到结果总计为 0。然而，如果你有钥匙，它将显示如下：

![Git 中的 SSH 身份验证 - 存在 ssh 密钥](https://www.toolsqa.com/gallery/Git/5.SSH%20Authentication%20in%20Git%20-%20ssh%20keys%20present.png)

它表明存在两个密钥。如果你有密钥，则可以跳过下一节，该节详细介绍了如何在 GitHub 中生成公钥和私钥。键入以下命令来检查你是否有 ssh 密钥。

```
ls ~/.ssh
```

![ssh 不存在](https://www.toolsqa.com/gallery/Git/6.ssh-not-present.png)

由于我没有任何此类目录和密钥，因此我当然会收到相应的消息。在下一节中，我们将生成 ssh 密钥。

### 在 GitHub 中生成公钥和私钥

现在我确定我没有与我的远程帐户关联的密钥，让我们继续使用 SSH 进行身份验证。为此，我们生成两种类型的密钥。

-   私钥
-   公钥

#### 密码学中的私钥

如上所述，用户在通过互联网与其他人通信时会生成两种类型的密钥。在这两个密钥中，用户自己保留一个，不与任何人共享。该密钥是 SSH 协议的私钥。出于安全目的和增加黑客可以进行的猜测排列，此密钥通常非常长。它可以长达 1024 或 2048 位。因此，我们使用私钥来解密数据。如果丢失私钥，数据将永远处于解密状态。

#### 密码学中的公钥

用户生成的另一个密钥是公钥。公钥与所有人共享，并且只有通过公钥，用户才能加密他们试图发送的数据。在另一端，接收方将使用私钥解密数据。当且仅当公钥是你向其发送数据的同一个人时，用户才能解密数据。一旦解密，用户就可以保证正确的人发送了数据，发送者也可以保证正确的人已经解密了数据。

要通过 Git 生成一对 ssh 密钥，请键入以下命令：

`ssh-keygen -t rsa -C "myFirstSSHKeys"`![ssh-keygen](https://www.toolsqa.com/gallery/Git/7.ssh-keygen.png)

如果我们将此命令分解为构成它的元素，我们将得到以下实体：

-   ssh-keygen：ssh-keygen 是一种标准的加密密钥生成工具。它描述了我们正在请求 Git 生成 ssh 密钥。它预装了 Unix 系统和 Windows 10 以上版本。在 Windows 10 之前，PuTTY 实现了同样的目标。
-   -t：这个选项定义了我们将要描述我们想要生成的密钥类型。密钥可以是 DSA 或 RSA 类型。
-   rsa：这要求 Git 仅生成 RSA 密钥。
-   -C：这个选项描述了我们将要在这个选项旁边提到一个注释。
-   myFirstSSHKeys：这是评论。当我们有多个与我们的帐户相关联的密钥时，这是必不可少的。

按回车键执行命令。

![Git SSH 身份验证 - 生成 ssh 密钥](https://www.toolsqa.com/gallery/Git/8.Git%20SSH%20Authentication%20-%20generating%20ssh%20keys.png)

你将看到消息Generating a public/private rsa key pair，这表明 ssh-keygen 正在生成密钥。生成后，它将询问你要在其中保存密钥的文件。Ssh-Keygen 将显示默认位置。现在，我们将继续使用默认位置，所以只需按 enter。

![Git SSH 身份验证 - 输入密码](https://www.toolsqa.com/gallery/Git/9.Git%20SSH%20Authentication%20-%20enter%20passphrase.png)

接下来，系统将提示你输入密码。现在，我们将继续使用空密码。按回车键并再次输入相同的密码(无)以生成密钥。

![ssh 密钥生成](https://www.toolsqa.com/gallery/Git/10,.ssh-key-generated.png)

恭喜，如果你得到相同的屏幕！你的密钥生成成功。然而，突出显示的行显示较短的版本或公钥的指纹。

现在让我们再次输入以下命令来查看目录中的键：

```
ls -l ~/.ssh
```

![1键生成](https://www.toolsqa.com/gallery/Git/11.1-keys-generated.png)

此注释确认我们已成功生成密钥。现在，我们需要将公钥添加到我们的 GitHub 帐户，以便我们可以通过 SSH 从本地计算机上的 Git 与远程 GitHub 服务器进行通信。

## 将 SSH 密钥添加到 GitHub

1.  要将 SSH 密钥添加到我们的 GitHub 帐户，首先，我们需要复制该密钥。要复制密钥，请在你喜欢的文本编辑器中打开我们在上一节中生成的公钥：![Git SSH 身份验证 - 打开公钥](https://www.toolsqa.com/gallery/Git/12.Git%20SSH%20Authentication%20-%20opening%20public%20key.png)

注意：我们使用 Notepad++ 作为文本编辑器，而你可以使用你的任何一种选择。更多信息，你可以参考[Git 中的文本编辑器](https://www.toolsqa.com/git/set-up-notepad-for-git-bash/)。

1.  密钥将在 Notepad++ 中打开。复制此密钥。

![ssh 密钥记事本](https://www.toolsqa.com/gallery/Git/13.ssh%20key%20notepad.png)

1.  打开你的 GitHub 帐户，然后按页面右上角的个人资料图片转到设置。

![设置-github](https://www.toolsqa.com/gallery/Git/15.settings-github.png)

1.  从侧面板 导航到SSH 和 GPG 密钥选项。![ssh-gpg-密钥](https://www.toolsqa.com/gallery/Git/16.ssh-gpg-keys.png)
2.  按新建 SSH 密钥以在 GitHub 中输入新密钥。

![新的 ssh 密钥](https://www.toolsqa.com/gallery/Git/17.new-ssh-key.png)

1.  在给定的文本字段中输入标题和密钥，然后按添加 SSH 密钥。

![输入 SSH 密钥。](https://www.toolsqa.com/gallery/Git/18.entering-SSH-key.png)

完成后，你将看到密钥已添加成功，这是通过第 6 点中给出的标题识别的。

![已添加 ssh 密钥](https://www.toolsqa.com/gallery/Git/19.ssh-key-added.png)

如果这是你看到的屏幕，则你已成功将 SSH 密钥添加到你的 GitHub 帐户。现在，我们需要验证密钥是否已正确链接。

### 验证在 GitHub 帐户中添加的 SSH 密钥

现在 SSH 密钥已经生成并添加到我们的 GitHub 帐户中，是时候在 ssh 和 GitBash 的帮助下使用我们的 GitHub 帐户检查密钥了。

转到Git Bash并键入以下命令：

```
ssh -T git@github.com
```

![ssh-t-git-github](https://www.toolsqa.com/gallery/Git/20.ssh-t-git-github_0.png)

按enter键在屏幕上看到以下消息。它将提示你确认是否要进行身份验证。按是。

![验证-ssh_1.png](https://www.toolsqa.com/gallery/Git/21.verify-ssh_1.png)

按回车键，如果你看到一条包含你姓名的消息，则你已成功验证生成的密钥。

![ssh 认证成功](https://www.toolsqa.com/gallery/Git/22.ssh-authenticated-success_0.png)

成功验证我们的密钥使我们结束了本Git SSH 身份验证教程。生成、添加和验证帐户密钥的工作量很大，我们很高兴地完成了这些工作。在结束之前，我必须说，使用 HTTPS 还是 SSH 完全取决于用户及其方便程度。如果你觉得使用 HTTPS 更舒服，你应该仔细阅读它(在阅读其优点和缺点之后)。现在，我们将继续我们的下一篇文章。