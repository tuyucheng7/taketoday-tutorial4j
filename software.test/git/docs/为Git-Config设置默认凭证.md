在前面的教程中，我们已经成功学习了各种[Git客户端](https://www.toolsqa.com/git/git-clients/)和一些常用命令。由于我们现在已经在我们的系统中[安装了 Git](https://www.toolsqa.com/git/install-git-on-windows/)并了解了它，现在是时候对Git Bash进行一些实践了。我们将尝试对 Git 的配置文件进行一些更改，并使用 Git Bash 设置我们的凭据。我希望你记住，我们将只在本课程中使用Git Bash 。在本教程中，我们将

-   为 Git Config 设置默认凭证
-   查看 Git Config 的用户设置列表

在继续这些部分之前，我们应该首先了解为什么我们需要在安装 Git 或第一次开始使用它时更改配置文件。

## 为什么要在 Git 中设置配置文件？

当我们在系统中安装 Git 时，配置文件会为某些字段采用默认值。这意味着 Git 首先为每个用户设置相同的文件。尽管这些值中有许多是默认值并以这种方式保留，但个人值不应该。这会导致团队中程序员的身份发生冲突，我们将通过本节中的示例了解这一点。因此，当你第一次启动 Git 时，会在 Git 的/etc/gitconfig文件中搜索通用默认文件(每个用户都通用)。一旦这些设置完成，Git 必须查看特定于特定用户的文件。这些特定文件在~/.gitconfig 或 ~/.config/git/config文件下可用。具体文件包括你的用户名、姓名等。

访问你的 Git Bash 并键入以下命令以查看个人配置设置。

vi ~/.gitconfig

![git_config_目录](https://www.toolsqa.com/gallery/Git/1%20git_config_directory.png)

不用担心 VI 编辑器。我们将在设置文本编辑器时讨论它。按回车键并查看你的配置文件。我的已经准备好给你看。如果你尚未设置它，你将不会获得任何值。

让我们看一个例子，看看为什么我们需要在处理项目时设置这些值。许多人在一个项目上工作，但唯一可以识别你身份的是你的用户名或电子邮件。如果你没有在配置文件中配置这些值，这些值(名称和电子邮件等)将被随机获取。因此，当你提交任何更改时，此更改会与你的随机用户名和电子邮件一起反映出来，没有人能够识别或记住你的凭据。这给团队带来了很多问题。因此，我们应该更改此配置文件。

## 如何为 Git Config 设置默认凭证？

### 在 Git Config 中设置用户的用户名

我们将在配置文件中进行的第一个更改是更改 Git 中的用户名。要更改我们的用户名，请按照以下步骤操作。

在你的系统中打开Git Bash 。使用你的用户名键入以下命令：

`git config --global user.name "Your UserName"`

![全局用户名](https://www.toolsqa.com/gallery/Git/2%20global_user_name.png)

注意：由于我在上面输入了自己的用户名，因此显示的是 Rajora, Harish。此外，更改你的用户名只会影响你未来的提交，而不会影响你过去的提交。

这会将用户名更改为你在命令中提供的值。按回车，如果没有任何反应，则名称更改成功。

你可能会遇到一些 Git 会自我解释的错误。例如，如果你忘记在用户和名称之间加上“ . ”，你可能会得到这样的错误

![global_user_name_error](https://www.toolsqa.com/gallery/Git/3%20global_user_name_error.png)

### 在 Git Config 中设置用户的电子邮件

成功执行上述命令后，我们将更改我们的电子邮件。键入以下命令

`git config --global user.email "Your EmailID"`

![git_global_email_set](https://www.toolsqa.com/gallery/Git/4%20git_global_email_set.png)

它会将 Git 配置中的电子邮件 ID 更改为你在命令中提到的电子邮件 ID。

注意：非常重要的是要注意，我们使用--global用于个人配置文件，而我们使用--system来更改上一节中讨论的默认文件。该文件应按原样保留。

## 如何查看 Git Config 的用户设置列表？

在配置文件中设置所有值后，你还可以通过 Git Bash 查看所有设置。

### 查看 Git Config 中的完整设置列表

为此，转到 Git Bash 并键入此命令。

`git config --list`

![git_config_list](https://www.toolsqa.com/gallery/Git/5%20git_config_list.png)

按回车键，你可以看到所有设置，包括我们刚刚在上一节中设置的设置。

![git_config_list](https://www.toolsqa.com/gallery/Git/6%20git_config_list.png)

### 在 Git Config 中查看特定设置

你还可以检查配置文件中的特定设置，而不是打开完整列表。要查看此内容，请执行以下简单步骤。

在 Git Bash 中键入以下命令：

`git config --global <key>`

![git_config_key](https://www.toolsqa.com/gallery/Git/7%20git_config_key.png)

`<key>`这里指的是你要查看的设置的名称。对于上面的示例，键是我们在上面部分中设置的 user.name。你需要原样记住键值对。在此示例中，它不是用户名，而是 user.name。如果你不记得了，你可以随时显示配置文件的完整列表。

按回车键查看键值

![git_config_key_value](https://www.toolsqa.com/gallery/Git/8%20git_config_key_value.png)

我在这里强烈建议访问关于git --config的[Git 配置](https://git-scm.com/docs/git-config)页面，并在阅读本教程后亲自动手。