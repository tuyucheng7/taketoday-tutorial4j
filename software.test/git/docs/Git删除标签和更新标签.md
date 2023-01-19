在上一篇关于 Git 中的[标签](https://www.toolsqa.com/git/git-tags/)的教程中，我们了解了 Git 中的标签是什么以及它对软件开发的重要性。在该教程的后面部分，我们创建了一些标签，然后将它们推送到远程。这些是涉及标签中基本操作的初始步骤。在本教程中，我们将做相反的事情。我们将通过：

-   如何在 Git 中删除标签？
    -   如何从本地机器上删除 Git 中的标签？
    -   通过本地 (Git Bash) 从远程存储库删除标签的过程
-   如何更新标签？
    -   如何从本地机器更新 Git 中的标签？
    -   通过本地 (Git Bash) 从远程存储库更新标签的过程

# 如何在 Git 中删除标签？

与其他任何东西一样，标签不是永久实体。如果我们不能删除标签，这对我们来说将是混乱和困难的，而这样做的原因有很多。也许你错误地假设了发布的执行。或者，也许你标记了错误的提交等等。在本节中，我们将探索如何从本地和远程存储库中删除刚刚创建的标签。然而，对于这两个操作，我们将使用 Git bash(即我们的本地机器)。

先决条件：

-   什么是标签？(参考[Git 中的标签](https://www.toolsqa.com/git/git-tags/))
-   如何在 Git 中创建标签？(参考[Git 中的标签](https://www.toolsqa.com/git/git-tags/))
-   如何推送标签到远程仓库？(参考[Git 中的标签](https://www.toolsqa.com/git/git-tags/))

我们有两个标签，即v1.0.1和ongoing。为了演示本教程的目标，我又添加了两个标签。此外，你可以将其作为练习。

执行以下命令来检查你刚刚创建的标签。

```
git tag
```

![git delete tag - 列出 git 标签](https://www.toolsqa.com/gallery/Git/1.git%20delete%20tag%20-%20List%20git%20tags.png)

已成功添加两个名为v1.1和v2.0的新标签。下一步，将这些标签推送到远程存储库。

![git delete tag - 查看所有 git 标签](https://www.toolsqa.com/gallery/Git/2.git%20delete%20tag%20-%20View%20All%20git%20tags.png)

现在我们都设置好了，我们可以继续对这些标签执行操作。

### 如何从本地存储库中删除 Git 中的标签？

从本地存储库中删除 Git 中的标签非常简单。要成功，你应该知道要删除的标签的名称(或者你可以使用 git tag 命令查看所有标签)。

执行以下命令删除标签“正在进行”。

```
git tag -d ongoing
```

![git 删除标签](https://www.toolsqa.com/gallery/Git/3.git%20delete%20tag.png)

注意：与“ d ”一起使用的标志`git tag`表示我们正在请求删除操作。

Git 以删除标记的成功消息作为响应。除此之外，操作的哈希码 ( d3d18bd ) 也是 Git 响应的一部分。

转到远程存储库帐户，我们仍然在那里列出了四个标签，而在本地，我们有三个(删除后)。

![标签_github](https://www.toolsqa.com/gallery/Git/4.tags_github.webp)

让我们看看如何使用本地存储库中的 Git 从远程存储库中删除标签。

### 如何使用 Git 从远程存储库中删除标签？

现在我们已经从本地存储库中删除了标签，下一步是将此更改反映到我们的远程存储库中。这个想法是每个人都可以与之同步。此外，用户可以通过转到他的存储库并按删除按钮从 GitHub 手动删除。我们将在Tags In GitHub教程中对此进行讨论，该教程将向你简要介绍有关 GitHub 的 Tags 的各种选项和重要性。

要从本地存储库中删除远程存储库中的标签，请输入以下命令。

```
git push origin :<tag_name>
```

![推送 Git 删除标签](https://www.toolsqa.com/gallery/Git/5.Push%20Git%20Delete%20Tag.png)

注意：请记住 origin 和“ ： ”之间的空格否则该命令将不起作用。

该命令由三个部分组成：

-   git push： Git push 命令 git 将我们的更改推送到远程(参考 Git Push)。
-   origin： Origin 是我们为远程存储库创建的别名的名称。
-   ongoing：这是标签的名称，我们正在将其更改推送到远程。

执行命令以查看响应。

![Git删除标签推送到远程](https://www.toolsqa.com/gallery/Git/6.Git%20Delete%20Tag%20Push%20to%20Remote.png)

Git 以积极的消息回应。刷新 GitHub 上的“标签”页面以进行交叉检查。

![tag_deleted_github](https://www.toolsqa.com/gallery/Git/7.tag_deleted_github.webp)

好吧！操作成功，这样我们就可以使用Git删除远程和本地仓库中的标签了。

除了删除这种常规操作外，在 Git 中工作时也会经常出现更新标签的需求。下一节将向你介绍相同的内容。

## 如何在 Git 中更新标签？

在 Git 项目中工作时，我们经常需要删除和更新内容。我们需要删除/更新分支或删除/更新文件等。与此类似，有时我们需要更新 Git 中的标签。更新标签会将你的标签带到另一个提交。例如，我们可以将标签 v1.1 更新为另一个描述稳定版 Version1.1 的提交。该软件的直到现在新提交。执行更新可能有多种原因。本节将引导你完成更新本地机器中的标签并将这些更改反映到远程存储库的步骤。我们将在这两个操作中使用 Git。

### 如何在本地存储库中更新 Git 中的标签？

要更新本地存储库中的标签，请在标记的提交之上创建一个新的提交。例如，请参见下图：

![git_oneline_with_tags](https://www.toolsqa.com/gallery/Git/8.git_oneline_with_tags.webp)

带有标签 v2.0 的提交是最后一次提交。上面没有提交，并且 HEAD 指向与我们看到的相同的提交。让我们对自述文件进行一些更改并使用提交消息提交它们(请参阅 [Git 中的首次提交](https://www.toolsqa.com/git/first-commit-in-git/))。

完成此操作后，Git 日志将如下所示：

![new_update_for_tags](https://www.toolsqa.com/gallery/Git/9.new_update_for_tags.webp)

因此，我们打算将标签 v2.0 从9e81b61更新为6b405e5。

为实现此目标，请执行以下命令。

```
git tag -f <tag_name_to_update> <hash_code_new_commit>
```

![git_tag_update](https://www.toolsqa.com/gallery/Git/10.git_tag_update.webp)

注意：该`<hash_code>`选项可以留空，这将自动更新标签到指向 HEAD 的提交。此外，对于任何其他提交，你需要使用哈希码。

执行命令更新标签成功。

![tag_updated](https://www.toolsqa.com/gallery/Git/11.tag_updated.webp)

通过再次显示 Git 日志进行交叉检查。

![git_log_updated](https://www.toolsqa.com/gallery/Git/12.git_log_updated.webp)

伟大的！我们的本地存储库已更新。现在让我们更新我们的远程存储库。

### 如何在远程存储库中更新 Git 中的标签？

和删除类似，我们需要更新我们更新到远程仓库的操作。当我们推动创建标签([Git](https://www.toolsqa.com/git/git-tags/)中的标签)时，让我们看看同一个命令是否有效。

```
git push origin v2.0
```

![git_push_updated_tags](https://www.toolsqa.com/gallery/Git/13.git_push_updated_tags.webp)

响应构成三行。

-   ！[rejected]：这表示命令执行的总体结果。
-   error: Failed to push some refs :此行显示某些引用无法与远程存储库同步。
-   hint: Updates rejected as tag already exists：这一行帮助我们分析我们正在使用的命令仅适用于新创建的标签。

因此，我们需要一些其他方法来强制进入远程存储库。

要将更新后的标签强行推送到远程仓库，执行以下命令：

```
git push --force origin v2.0
```

![force_pushing_tag_to_github](https://www.toolsqa.com/gallery/Git/14.force_pushing_tag_to_github.webp)

注意：除了采用这种方式，你还可以通过首先删除提交然后使用预期提交创建一个新标记(\具有相同名称)来获得相同的结果。\在此之后，将标签推送到远程存储库将完成必要的工作。

此外，哈希码的变化表示我们已经成功更新了远程存储库中的标签。

我们可以通过查看远程存储库(验证哈希码)来确认这一点。

![github_tag_hash](https://www.toolsqa.com/gallery/Git/15.github_tag_hash.webp)

给你。我们在远程存储库中也有更新的标签。

总而言之，标签在涉及 Git 的项目开发中非常重要。它们帮助我们标记开发/生产和发布周期中的各个点。此外，通过这个标签，我们可以更轻松地了解项目的不同版本。[虽然这涵盖了 Git 的一部分，但标签可以揭示GitHub 上](https://github.com/)的更多信息。换句话说，它们在 GitHub 组件中占有特殊的位置。我很高兴在 GitHub 上向你展示这一切。我希望你也是！