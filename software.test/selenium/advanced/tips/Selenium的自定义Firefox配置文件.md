## 什么是简介

Firefox 将你的个人信息(如书签、密码和用户首选项)保存在一组称为你的个人资料的文件中，该文件与 Firefox 程序文件存储在不同的位置。你可以拥有多个 Firefox 配置文件，每个配置文件包含一组单独的用户信息。配置文件管理器允许你创建、删除、重命名和切换配置文件。

## 为什么我需要新配置文件

默认的 Firefox 配置文件不是很自动化友好。当你想在 Firefox 浏览器上可靠地运行自动化时，建议制作一个单独的配置文件。自动化配置文件应该轻载并且有特殊的代理和其他设置来运行良好的测试。

你应该与在所有开发和测试执行机器上使用的配置文件保持一致。如果你到处使用不同的配置文件，你接受的 SSL 证书或你安装的插件就会不同，这会使测试在机器上的表现不同。

-   有好几次你需要在配置文件中添加一些特殊的东西来使测试执行可靠。最常见的示例是 SSL 证书设置或处理自签名证书的浏览器插件。创建一个处理这些特殊测试需求的配置文件并将其与测试执行代码一起打包和部署是很有意义的。
-   你应该使用非常轻量级的配置文件，其中仅包含执行所需的设置和插件。每次 Selenium 启动一个驱动 Firefox 实例的新会话时，它都会将整个配置文件复制到某个临时目录中，如果配置文件很大，它不仅速度慢而且不可靠。

## 查找你的个人资料文件夹

你的配置文件文件夹的位置取决于你使用的操作系统。下表显示了默认配置文件的典型位置：

| 操作系统                | 配置文件文件夹路径                                     |
| ----------------------------- | ------------------------------------------------------------ |
| Windows XP / 2000 / Vista / 7 | `%AppData%MozillaFirefoxProfilesxxxxxxxx.default`            |
| Linux                         | `~/.mozilla/firefox/xxxxxxxx.default/`                       |
| 苹果操作系统                  | `~/Library/Application Support/Firefox/Profiles/xxxxxxxx.default/` |

这张表有两个有趣的地方。第一个是`xxxxxxxx`每个配置文件名称之前的字符串。这个字符串只是简单的8个随机数和字符的集合，用来保证每个配置文件都是唯一的。Firefox 会自动为你的任何新配置文件添加一个随机字符串，因此你永远不必担心创建名称的这一部分。

第二个兴趣点在于 Windows XP / 2000 / Vista / 7 路径。该`%AppData%`字符串实际上是一个特殊的 Windows 变量，指向你的“应用程序数据”路径。这通常是以下形式：

```
C:Documents and Settings{User Name}Application Data.
```

## 创建新配置文件

创建新的 Firefox 配置文件并在测试脚本中使用相同的配置文件涉及三个步骤。首先，你需要启动配置文件管理器，其次是创建新配置文件，第三是在测试脚本中使用相同的配置文件。

### 第 1 步：启动配置文件管理器

1.  在 Firefox 窗口的顶部，单击“文件”菜单，然后选择“退出”。

![FF-配置文件-1](https://www.toolsqa.com/gallery/selnium%20webdriver/1.FF-Profile-1.png)

2) 按“ + R ”或单击 Windows 开始菜单(左下角按钮)，然后选择 “运行”。

![FF-配置文件-2](https://www.toolsqa.com/gallery/selnium%20webdriver/2.FF-Profile-2.png)

3) 在“ 运行” 对话框中，键入：' `firefox.exe -p`'，然后单击“ 确定” 。

![FF-配置文件-3](https://www.toolsqa.com/gallery/selnium%20webdriver/3.FF-Profile-3.png)

注意：如果配置文件管理器窗口没有出现，它可能在后台打开。需要正常关闭，可以使用Ctrl+Alt+Del程序杀掉。如果它仍然没有打开，那么你可能需要指定 Firefox 程序的完整路径，用引号括起来；例如：

-   在 32 位 Windows 上：“`C:Program FilesMozilla Firefoxfirefox.exe" -p`
-   在 64 位 Windows 上：“`C:Program Files (x86)Mozilla Firefoxfirefox.exe" -p`

1.  选择用户配置文件窗口将如下所示。

![FF-配置文件-4](https://www.toolsqa.com/gallery/selnium%20webdriver/4.FF-Profile-4.png)

## 第 2 步：创建配置文件

1) 单击“创建配置文件... ” 出现的“ Firefox - 选择用户配置文件”窗口上的按钮。

![FF-配置文件-5](https://www.toolsqa.com/gallery/selnium%20webdriver/5.FF-Profile-5.png)

2)在出现的“创建配置文件向导”窗口中单击“下一步> ”。

![FF-配置文件-6](https://www.toolsqa.com/gallery/selnium%20webdriver/6.FF-Profile-6.png)

3) 在“输入新配置文件名称”框中键入新名称“ profileToolsQA ”，然后单击“完成”。

![FF-配置文件-7](https://www.toolsqa.com/gallery/selnium%20webdriver/7.FF-Profile-7.png)

1.  “选择用户配置文件”窗口将在列表中显示新创建的配置文件。

![FF-配置文件-8](https://www.toolsqa.com/gallery/selnium%20webdriver/8.FF-Profile-8.png)

1.  单击“启动 Firefox ”框。Firefox 将从新的配置文件开始。

注意：你会注意到新的 Firefox 窗口不会显示你的任何书签和收藏夹图标。

注意：上次选择的配置文件将在你下次启动 Firefox 时自动启动，你需要再次启动配置文件管理器才能切换配置文件。

## 第 3 步：Selenium 中的用户自定义配置文件

创建自动化配置文件后，需要在测试脚本中调用它。你现在可以将以下代码添加到你的测试脚本中以实例化 Firefox 驱动程序：

```java
ProfilesIni profile = new ProfilesIni();

FirefoxProfile myprofile = profile.getProfile("profileToolsQA");

WebDriver driver = new FirefoxDriver(myprofile);
```