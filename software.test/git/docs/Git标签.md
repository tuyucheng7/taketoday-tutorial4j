“态度就像一张价签，它决定了你的价值”。这是一个非常典型的引用，真实地描述了“标签”这个词的含义。标签对我们任何人来说都不是一个陌生的词。标签是用作附加标识符的标签(如果没有，则为标识符)。因此，如果我想将伟大的科学家 APJ Abdul Kalam 博士标记为“导弹人”，那么这就是他的标识符。下次有人说“导弹人”时，它会暗示那是什么意思。让我们看看这个类比是如何与Git 标签相关的……

标签在 Git 和 GitHub 中具有相似的含义。标签有助于识别重要到足以被识别或注意到的不同提交。例如，将提交标记为发布版本 3.0 意味着该提交是软件 3.0 版本发布之前的最终提交。而且，它方便易行。GitHub 提供了一个完全不同的部分来查看标签并在那里进行分析。在本教程中，我们将看到相同的亮点：

-   Git 中的标签是什么？
    -   如何在 Git 中创建标签？
    -   如何在Git中查看标签？
-   将标签推送到远程存储库的过程
-   如何在 GitHub 中查看标签？

## Git 中的标签是什么？

标签看起来并不重要，但它们是 Git 和 GitHub 中最强大的功能之一。尽管 Git 将该功能限制为仅标记项目中的重要事件或里程碑，但 GitHub 已将此功能扩展到软件的发布版本。我们将在本文的后面部分看到这一点。

标签作为一个概念听起来可能有点不同。Git 中的标签是 Git 历史中表示特殊事件的参考点。标记发行版不是规则。你可以出于任何目的标记任何提交。除此之外，无论我们在项目上花费多少时间，任何新成员都可以查看 git 日志并通过 Git 识别项目时间线中的独特点。对于发布，GitHub 提供了另一种选择。那么让我们看看我们如何在 Git 中打标签并将相同的本地标签推送到位于远程存储库中的远程存储库。

### 如何在 Git 中创建标签？

在 Git 中创建标签非常简单。但是，用户必须事先熟悉[Git 日志](https://www.toolsqa.com/git/git-log/)。所以，如果 git log 对你来说似乎很陌生，建议先阅读有关 git log 命令的教程。标签可以与 HEAD 指向的提交或用户喜欢的特定提交相关联。在本节中，我们将同时看到它们。

#### 如何在 Git 中添加标签？

1.  在工作目录中打开Git Bash 。
2.  检查你是否有干净的工作目录。
3.  执行以下命令查看提交：

```
git log --oneline
```

![git log oneline 标签](https://www.toolsqa.com/gallery/Git/1.git%20log%20oneline%20tags.png)

1.  我们现在可以在任何这些提交上创建一个标签。让我们通过执行以下命令来标记 dev 分支上的最后一次提交：

```
git tag ongoing dev
```

![git tag 正在进行的开发命令](https://www.toolsqa.com/gallery/Git/2.git%20tag%20ongoing%20dev%20command.png)

注意：对dev分支的提交不会反映在任何其他分支中。dev 命令仅供个人使用。

1.  现在你可以通过执行`git log --oneline`命令来检查自己是否已成功创建标签。

尽管此命令将标记分支dev上的最后一次提交，但你也可以标记 Git 中的特定提交。

#### 如何在 Git 中标记特定的提交？

为了标记特定的提交，我们将使用该特定提交的哈希码。要列出提交，让我们`git log --oneline`再次执行命令。

![git log oneline 命令再次执行](https://www.toolsqa.com/gallery/Git/3.git%20log%20oneline%20command%20executed%20again.png)

提交是f030ee9。让我们用以下命令标记它：

```
git tag -a <tag_name> -m "<Message_for_commit>" <commit_hash_code>
```

![特定提交的 git 标签](https://www.toolsqa.com/gallery/Git/4.git%20tag%20on%20particular%20commit.png)

注意：“ -a ”表示带注释的标签，通俗地说，这意味着标签是特定的而不是一般化的。-m是告诉后面的句子是提交消息的标志。

开始了！我们已成功标记提交。如果用户打算查看标签和提交，他们也可以使用`git log --oneline`如下所示的命令：

![git 日志标签](https://www.toolsqa.com/gallery/Git/5.git%20log%20tag.png)

注意：有时这样查看标签取决于客户端和操作系统。有时不同的操作系统也不会在以前版本的一行命令中显示标签。所以，最好使用`git alias`.

### 如何在 Git 中查看标签？

用户可以通过以下命令查看仓库中的所有标签：

```
git tag
```

![查看所有git标签的命令](https://www.toolsqa.com/gallery/Git/6.command%20to%20view%20all%20git%20tags.png)

所以，我们现在都通过在本地机器上标记提交来设置。但是，这些提交在 GitHub 上的远程存储库中尚不可见。因此，让我们推送这些标签，看看它们在 GitHub 中的哪些位置可用。

## 如何推送标签到GitHub？

推送标签类似于推送远程存储库上的任何内容。按照以下步骤将标签推送到远程存储库：

1.  首先，在本地工作目录中打开Git Bash 。
2.  确保远程上没有尚未与本地计算机同步的更改。我们可以通过 git pull 命令来实现。(参考 [Git Pull](https://www.toolsqa.com/git/git-pull/))。

![git 拉命令](https://www.toolsqa.com/gallery/Git/7.git%20pull%20command.png)

1.  Git 不会通过普通`git push`命令推送标签。如果我们按原样使用 git push，我们会得到以下响应：

![git 推送检查](https://www.toolsqa.com/gallery/Git/8.git%20push%20check.png)

1.  Git 说一切都是最新的，这意味着没有什么可推送的，但事实并非如此。要将标签推送到远程存储库，请输入以下命令：

```
git push --tags
```

![将 git 标签推送到远程存储库的命令](https://www.toolsqa.com/gallery/Git/9.Command%20to%20push%20the%20git%20tags%20into%20the%20remote%20repository.png)

1.  按回车键执行命令。

![命令执行](https://www.toolsqa.com/gallery/Git/10.command%20executed.png)

1.  Git 表示我们的两个标签：ongoing和v1.0.1都已成功推送。让我们在 GitHub 存储库上验证这些。

## 如何在 GitHub 中查看标签？

要验证我们在本地存储库中创建的标签，请访问你的[GitHub 帐户](https://www.github.com/)。

转到[Git 存储库主页](https://github.com/harishrajora805/Git)并访问主页上的发布选项卡。

![查看标签](https://www.toolsqa.com/gallery/Git/11.View%20Tags.png)

在此面板中，我们刚刚在本地存储库中创建的所有标签都将可见。![标签推送到github](https://www.toolsqa.com/gallery/Git/12.tags%20pushed%20github.png)

这样，你可以将所有本地标签推送到你的远程存储库并在你的帐户中查看它们。但是，我们仍然需要使用标签。请注意，GitHub 将该选项卡命名为 Releases，而标签只是 Releases 中的一个选项卡。我们将在后面的教程中看到这一点。我们将在下一个教程中继续讨论，我们将尝试删除和使用 GitHub 上的标签，并了解有关 GitHub 的术语发布。

## 常见的 Git 标签问题

可以直接通过GitHub添加标签吗？

是的，用户可以直接通过 GitHub 标记提交，而无需使用 Git。它可以通过发布选项卡和起草新版本来完成。GitHub 教程中的标签中对此进行了详细讨论。

Git 标签是不可变的吗？

是的，Git 标签是不可变的，一旦创建就无法更改。你需要删除标签并重新创建它，尽管标签可以更新为另一个提交。

我们可以在 Git 中标记一个分支吗？

不，标签只能是对提交的引用。分支不能在 Git 中标记。

Git Tag 和 Release 一样吗？

不是，Tag 是 Git 的概念，而 Release 是 GitHub 特有的。Release 的用途是发布发行说明和二进制文件以及其他信息。标签的使用通常用于标记发布提交，但它们的使用不限于此。