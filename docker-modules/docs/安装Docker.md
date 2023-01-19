在上一个教程中，我们讨论了[***Docker 的基础知识***](https://www.toolsqa.com/docker/introduction-to-docker-and-docker-architecture)。要开始使用*Docker*进行试验，例如学习命令、使用*Docker*和部署应用程序，我们首先需要在我们的机器上安装 Docker。几乎所有操作系统都支持*Docker*，但我们需要遵循特定的安装顺序。在本教程中，我们将讨论*在 Windows 和 Linux 上安装 Docker*。此外，*Docker Engine*还支持以下平台。

1.  *Linux。*
2.  *Windows 操作系统。*
3.  *苹果系统。*

Windows 和 Mac OS 通过“ [***Docker Desktop***](https://www.docker.com/products/docker-desktop) ”提供 Docker 安装。此外，Docker 还为各种 Linux 版本的 Linux 架构和发行版提供“ ***.rpm*** ”或“ ***.deb*** ”包。

随后，我们将在本文中讨论以下主题：

-   如何在 Linux 中安装 Docker？
    -   *系统要求/先决条件。*
    -   *如何在 Ubuntu 上安装 Docker？*
    -   *如何卸载 Docker？*
    -   *以及，如何在其他 Linux 版本上安装 Docker？*
-   如何在 Windows 上安装 Docker？
    -   *在 Windows 上安装 Docker 的先决条件。*
    -   *如何在 Windows 上安装 Docker 桌面？*
    -   *还有，如何在 Windows 上卸载 Docker Desktop？*
-   如何在 macOS 上安装 Docker？
    -   *还有，如何在 macOS 上卸载 Docker？*

## 如何在 Linux 上安装 Docker？

在 Linux 环境中安装*Docker*之前，我们应该确保满足所有先决条件和/或系统要求，

### ***系统要求/先决条件***

我们应该确保 Linux 机器满足以下要求。

#### ***检查操作系统要求。***

要在 Linux 上安装 Docker，系统应该是*64 位*的并且安装了以下 Linux Ubuntu 版本之一。

-   *Ubuntu 焦点 20.04 (LTS)*
-   *Ubuntu 仿生 18.04 (LTS)*
-   *或者，Ubuntu Xenial 16.04 (LTS)*

支持 Docker Engine 的各种架构有 x86_64 ( *amd64* )、armhf 和 arm64。

#### ***卸载旧的 Docker 版本***

接下来，我们需要卸载机器上所有旧的 Docker 版本。旧版本的*Docker*被命名为*docker-engine、docker.io、docker*。要卸载这些版本，请运行以下命令。

```
$ docker apt-get remove docker docker-engine docker.io containerd runc
```

此命令将从机器中删除*Docker*软件（如果有）或给出一条消息，指出未找到任何软件。

#### ***配置驱动程序支持***

Docker Engine 支持*Ubuntu上的****overlay2、btrfs 和 aufs***存储驱动程序。默认情况下，Docker 引擎使用***overlay2***驱动程序。所以我们需要确保配置了适当的驱动程序。（*通常存在于机器上。如果我们需要另一个像aufs，那么我们需要**[**手动配置**](https://manpages.ubuntu.com/manpages/bionic/man5/aufs.5.html)**它*）。

### ***如何在 Ubuntu 上安装 Docker？***

我们可以通过三种方式在 Ubuntu 上安装 Docker：

1.  ***存储库**：这是推荐的方法。在这种方法中，我们设置了 Docker 的存储库，并从这些存储库在 Ubuntu 中安装 Docker。这种方法使安装和升级任务更加容易。*
2.  ***手动安装**：在这种方法中，用户下载“ **DEB** ”包并在 Ubuntu 中手动安装 docker。当无法访问 Internet 时，可以采用这种方法。*
3.  ***使用自动化便捷脚本**这种方法主要用于开发和测试环境，其中用户使用**自动化便捷脚本**来安装 Docker。*

在本文中，我们将提供***在 Ubuntu 中使用存储库安装 Docker***的详细方法。

#### ***使用存储库安装 Docker***

在新的 Linux 机器上首次安装 Docker Engine 之前，我们必须设置存储库。然后我们就可以安装Docker Engine并升级它了。

以下是在 Ubuntu 18.04 ( *LTS* ) 机器上安装 Docker 的步骤或命令序列。可以在装有其他 Ubuntu 版本的机器上重复这些相同的步骤。

##### ***为 Docker 设置存储库***

执行以下步骤序列以设置存储库。

一世。更新“ ***apt*** ”包索引。为此给出以下命令。

```
 $ docker apt-get update
```

上面的命令生成以下输出。![1-apt-get-update_output.png](https://www.toolsqa.com/gallery/Docker/1-apt-get-update_output.png)

“ ***apt*** ”包索引更新后，安装允许 apt 通过*HTTPS*使用存储库的包。为此，执行以下命令：

```
$ docker apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg-agent \
    software-properties-common
```

上面的命令将设置存储库。例如，此命令提供以下输出。![2-设置存储库.png](https://www.toolsqa.com/gallery/Docker/2-Set%20up%20repository.png)

***上面的屏幕截图显示了由于“ sudo apt-get install ...*** ”命令而设置存储库的整个过程。

二. 安装存储库后，我们必须添加并验证*Docker 的官方 GPG 密钥*。

***注意**：GPG 密钥：这不是特定于 Docker 的，而是特定于 Linux 的。使用 GPG 密钥，Linux 包管理器可以通过验证 PGP 或 GPG 密钥来验证正在安装的软件包的完整性。大多数现代 Linux 发行版都为该特定发行版的默认存储库安装了一组 PGP 密钥。Docker 为主要发行版运行自己的包存储库。*

在此之后，使用以下命令添加 Docker 的官方 GPG 密钥。

```
$ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```

添加 GPG 密钥后，我们可以通过以下命令对其进行验证。

```
$ docker apt-key fingerprint 0EBFCD88
```

一个接一个地执行上述命令会生成以下输出。![3-验证GPGKey.png](https://www.toolsqa.com/gallery/Docker/3-VerifyGPGKey.png)

在上面的截图中，我们可以看到 GPG 密钥的详细信息。

三. 添加 GPG 密钥后，我们可以通过以下命令设置“ ***nightly*** ”或“ ***test ”存储库的稳定存储库。***

```
$ docker add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable test"
```

此命令提供以下输出。![4-安装 Docker - 添加 repository.png](https://www.toolsqa.com/gallery/Docker/4-Install%20Docker%20-%20add%20repository.png)

从上面的输出可以看出，我们在这里使用了一个测试仓库。

现在，我们已经成功设置了存储库，我们可以继续下一步安装 Docker Engine。

##### ***安装 Docker 引擎***

首先，要安装 Docker Engine，我们更新“ ***apt*** ”包，然后安装最新版本的*Docker Engine*和“ *containerd* ”。

要更新“ *apt* ”包，我们给出与步骤 1 中相同的命令。

```
$ docker apt-get update
```

更新 apt 包后，我们安装最新版本的*Docker 引擎*。

命令如下：

```
$ docker apt-get install docker-ce docker-ce-cli containerd.io
```

上面的命令生成以下输出。![5-Docker - 安装CLI.png](https://www.toolsqa.com/gallery/Docker/5-Docker%20-%20Install%20CLI.png)

我们可以通过发出以下生成以下输出（*部分输出*） 的命令来验证*Docker 引擎是否已成功安装。*![6-验证Docker安装.png](https://www.toolsqa.com/gallery/Docker/6-Verify%20Docker%20Installation.png)

或者我们可以发出任何命令，例如

```
$ docker run hello-world
```

上面的命令将下载一个测试图像并在容器中运行它。最后，容器将运行并打印信息性消息并退出。

##### ***升级 Docker 引擎***

如果我们需要将Docker Engine升级到另一个版本，我们需要再次执行该命令。

```
docker apt-get update
```

进一步重复上述安装说明以安装新版本。

### ***如何卸载Docker？***

要从 Linux 机器（*使用 Ubuntu*）卸载 Docker，请输入以下命令。

```
$ docker apt-get purge docker-ce docker-ce-cli containerd.io
```

这将从机器上卸载*Docker、CLI 和 containerd*包。

请注意，***上述命令不会从机器中删除图像、容器、自定义配置或卷***。所以我们需要明确地使用以下命令删除它们。

```
$ docker rm -rf /var/lib/docker
```

尽管如此，如果有任何额外的配置，我们必须手动删除它们。

#### ***还有哪些其他的Docker安装方式？***

除了上述安装方法外，我们还可以使用其他安装方法。

下面我们简单讨论一下另外两种方法：

##### ***从包中安装 Docker***

虽然使用存储库安装 Docker 是推荐的方法，但我们也可以通过下载“ ***.deb*** ”包并手动安装来安装*Docker 。*

涉及的步骤如下：

访问链接 [***https://download.docker.com/linux/ubuntu/dists/***](https://download.docker.com/linux/ubuntu/dists/)。导航到“ ***pool/stable/*** ”。选择*amd64/armf/arm64*，然后下载“ ***.deb*** ”文件。存储库内容之一的屏幕截图如下所示：![7-Docker 安装程序列表.png](https://www.toolsqa.com/gallery/Docker/7-Docker%20installers%20list.png)

*我们可以从“ dists* ”文件夹中选择任何文件夹（*根据我们的要求*），此外，我们可以看到每个存储库的内容。例如，在上面的屏幕截图中，我们选择了“ ***eoan*** ”文件夹，然后按照以下步骤导航到“ ***.deb ”文件。***

使用以下命令安装 docker。

```
$ docker dpkg -i /path/to/package.deb
```

命令“ ***/path/to/package.deb*** ”中的路径应更改为适当的*Docker*包路径。

和第一种安装方式一样，我们可以通过命令来验证*Docker*是否安装正确。

```
$ docker run hello-world
```

***注意**：如果我们需要**夜间**或**测试**包，我们可以通过将“稳定”一词替换为“夜间”或“测试”来实现。*

##### ***使用便捷脚本安装 Docker***

使用便利脚本安装 Docker 不是推荐的选项，尤其是对于生产环境。不推荐使用*便捷*脚本的原因：

1.  *便利脚本大多在不要求确认的情况下安装包。*
2.  *此外，我们需要“root”或“sudo”权限才能运行便捷脚本。*
3.  *便利脚本不允许我们自定义任何安装。*
4.  *默认情况下，它会安装最新版本的 Docker，我们无法指定我们选择的版本。*

我们可以分别在[***get.docker.com***](https://get.docker.com/) 和 [***test.docker.com***](https://test.docker.com/)找到用于安装边缘和测试版本的 Docker Engine - Community 的便捷脚本。我们可以使用这些脚本在开发环境中以非交互方式安装*Docker 。*

运行便捷脚本的命令如下：

```
$ curl -fsSL https://get.docker.com -o get-docker.sh
$ sh get-docker.sh
```

***笔记：***

1.  *我们不应该在使用其他方法安装 Docker 的主机上使用便利脚本。*
2.  *在本地机器上运行之前，我们应该始终验证下载的便捷脚本。*

### ***如何在其他 Linux 版本上安装 Docker？***

我们已经*在 Ubuntu 版本的 Linux 上看到了 Docker 安装*。在本节中，让我们简单讨论一下 Docker 在其他 Linux 版本上的安装。

要记住的一件事是，无论 Linux 发行版如何，我们始终需要 64 位安装和 3.10 或更高版本的内核版本。4个

Docker 运行于：

-   Ubuntu
    -   *Xenial 16.04 LTS*
    -   *狡猾的 15.10*
    -   *可信赖的 14.04 LTS*
    -   *精确 12.04 LTS*
-   德比安
    -   *测试拉伸*
    -   *Debian 8.0 杰西*
    -   *Debian 7.0 Wheezy（启用了反向移植）*

在*Debian Linux*版本上安装*Docker*的步骤与上面介绍的基本相同。但是如果我们在 Debian 发行版上安装 docker，那么首先，我们需要启用 backports。

*在Debian Wheezy*上启用反向移植的步骤如下：

1.  ***使用root/sudo**权限登录 Linux 终端*
2.  *导航到并打开“ **/etc/apt/sources.list.d/backports.list** ”。（如果不存在则创建文件）*
3.  *删除现有条目。*
4.  *现在为向后移植添加一个条目：*

```
deb http://http.debian.net/debian wheezy-backports main
```

1.  *现在使用以下命令更新软件包：*

```
apt - get  update  -y
```

完成上述步骤后，继续安装 Docker。

## 如何在 Windows 上安装 Docker？

*Docker 以Docker Desktop*的形式为 Microsoft windows 提供了一个社区版本。

我们可以在 Windows 10 企业版、专业版和教育版上安装 Docker Desktop。另外，我们可以在Windows 10 Home上安装Docker。可以从[***这里***](https://hub.docker.com/editions/community/docker-ce-desktop-windows/)下载。此链接打开以下屏幕。![8-安装 Docker - 获取 Docker.png](https://www.toolsqa.com/gallery/Docker/8-Install%20Docker%20-%20Get%20Docker.png)

单击“***获取 Docker*** ”时，将下载安装程序“ ***Docker Desktop Installer.exe*** ”。

在我们继续安装之前，让我们讨论安装的系统要求/先决条件。

### ***在 Windows 上安装 Docker 的先决条件***

要安装 Docker Desktop 的 Windows 系统应满足以下先决条件。

1.  *Windows 10 应该是具有以下版本之一的 64 位计算机：专业版、企业版或教育版。内部版本应为 16299 或更高版本。*
2.  *应启用 Windows 功能，即 Hyper-V 和容器。*
3.  要在 Windows 10 上成功运行 Client Hyper-V，应满足以下硬件先决条件：
    -   *系统 RAM 应至少为 4GB。*
    -   *处理器（64 位）应具有 [**二级地址转换 (SLAT)**](https://en.wikipedia.org/wiki/Second_Level_Address_Translation)*
    -   *使用 BIOS 设置，启用 BIOS 级硬件虚拟化支持。*

一旦满足这些要求，我们就可以继续在 Windows 上安装 Docker Desktop。

### ***如何在 Windows 上安装 Docker 桌面？***

应该按照下面的步骤顺序在 Windows 上安装 Docker Desktop。

1.  双击“ ***Docker Desktop Installer.exe*** ”开始安装。
2.  Docker Desktop 安装首先下载文件，安装开始，如下面的屏幕截图所示。![9-安装Docker.png](https://www.toolsqa.com/gallery/Docker/9-Install%20Docker.png)

上面的屏幕截图显示了正在进行的安装。安装完所有打包文件后，它会提示用户选中/取消选中配置。

1.  在安装过程中，安装程序会提示我们启用 Hyper-v Windows 功能。![10-安装Docker-配置Docker Engine.png](https://www.toolsqa.com/gallery/Docker/10-Install%20Docker%20-%20Configure%20Docker%20Engine.png)

在这里我们应该确保启用所有 Hyper-v Windows 功能，并安装适当的组件。

选中配置对话框后，单击“***确定***”继续安装。

1.  安装完成后，系统会提示用户重新启动机器。![11-Docker安装完成.png](https://www.toolsqa.com/gallery/Docker/11-Docker%20Installation%20Complete.png)

我们可以点击“***关闭并重启***”来重启机器来完成安装。

Docker Desktop 安装成功后，并不会立即启动。相反，我们可以通过单击程序中的相关图标或单击 Docker Desktop 快捷方式来启动 Docker Desktop。完成后，将出现以下屏幕。![12-Docker安装working.png](https://www.toolsqa.com/gallery/Docker/12-Docker%20Install%20working.png)

上面的屏幕截图显示了“ *Docker Desktop 的设置屏幕*”。现在 Docker Desktop 已成功安装并运行，我们可以从任何终端窗口访问它。

### ***如何在 Windows 上卸载 Docker Desktop？***

我们按照以下步骤在 Windows 上卸载 Docker Desktop：

1.  *首先，单击 Windows**开始**菜单，然后选择**设置 > 应用程序 > 应用程序和功能**。*
2.   *现在从 **Apps & features**列表中选择/选择**Docker Desktop**，然后单击**Uninstall**。*
3.  *单击 **卸载**以确认您的选择。*

***注意**：作为 Docker Desktop 卸载的结果，机器本地的 Docker 容器和图像被破坏，并且还会删除应用程序生成的文件。*

## 如何在 macOS 上安装 Docker？

可以使用*HomeBrew*包安装程序或直接从 docker 站点***下载****包在macOS*上安装 Docker 。在本文中，我们将详细介绍如何使用*HomeBrew*包在*macOS*机器上安装*Docker ：*

运行以下命令以使用 HomeBrew 安装 Docker：

```
brew cask install docker
```

成功安装 Docker 后，它将显示一条成功消息，如下图所示：![13-安装.jpg](https://www.toolsqa.com/gallery/Docker/13-installation.jpg)

也可以在 Docker Desktop 中查看详细信息来验证 macOS 上是否安装了 Docker，如下图：![14-验证Docker安装成功.jpg](https://www.toolsqa.com/gallery/Docker/14-Validate%20Successful%20Installation%20of%20Docker.jpg)

Docker 运行后，您可以使用它来拉取任何 Docker 映像和旋转容器。

### ***如何在 macOS 上卸载 Docker？***

您可以运行以下命令从 Mac 计算机上卸载 Docker：

```
brew cask uninstall docker
```

这将从机器中移除所有组件。

***注意**：Docker 镜像不会从机器中删除。*

## 要点：

-   *Docker 支持几乎所有现代操作系统，我们可以使用操作系统提供的各种包管理器进行安装。*
-   *在 Windows 和 macOS 上，Docker 也提供了桌面版本，我们可以使用它来启用 Docker 的各种设置和配置。*