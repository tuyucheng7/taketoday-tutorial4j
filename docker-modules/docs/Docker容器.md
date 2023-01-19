在上一个教程中，我们简要地谈到了[***容器和 containerizarions***](https://www.toolsqa.com/docker/understanding-containerization-and-virtualization/)的主题。回想一下，容器是一个标准的软件单元。它包含所有代码及其依赖项，打包在该单元中，有助于在计算环境中快速可靠地运行应用程序。因此，让我们在本文中讨论*Docker 容器。*

***Docker 容器是一个独立的、轻量级的、可执行的软件包***。如前所述，由于它是一个容器，因此它捆绑了运行应用程序所需的所有内容，如应用程序代码、系统工具、运行时、二进制文件、库等。

作为*Docker 容器*的一部分，我们将在本文中讨论以下主题。

-   什么是 Docker 容器？
    -   *为什么我们需要 Docker 容器？*
    -   *Docker 容器如何工作？*
-   Docker 容器命令
    -   *docker run 命令 - 启动容器图像/运行容器*
    -   *接下来是 docker ps 命令——列出 Docker 容器*
    -   *docker commit 命令 - 保存 Docker 容器
    -   docker stop 命令 - 停止容器*
    -   *接下来是，docker history命令——查看Docker容器历史*
    -   *docker top 命令——查看容器进程*
    -   *docker rm 命令 - 删除容器*
    -   *此外，docker stats 命令 - 获取容器统计信息*
    -   *docker pause 命令 - 暂停/取消暂停容器*
    -   *docker attach 命令 - 附加到容器*
    -   *并且，docker kill 命令 - 杀死一个容器*
-   如何使用特权 Docker 容器？
    -   *特权 Docker 容器是个好主意吗？*
-   *如何保护容器？*

## 什么是 Docker 容器？

[***在我们之前关于“ Docker 镜像***](https://www.toolsqa.com/docker/docker-images/)”的章节中，我们详细讨论了 Docker 镜像。我们知道*Docker 镜像*是一个***静态实体***，要运行*Docker 镜像*，我们必须将它与一个容器相关联。换句话说，*Docker 镜像*在运行时或在***Docker Engine***上运行时成为***容器***。

所以一个*Docker容器*有以下特点：

-   ***标准**：Docker 容器是可移植的，因为它们遵循行业标准。*
-   ***轻量级**：Docker 容器共享主机的 OS 内核，因此不需要每个应用程序一个 OS。它降低了许可和服务器成本，还提供了更高的效率。*
-   ***安全**：应用程序打包在 Docker 容器中时更安全。Docker 还为其容器提供了业界最强大的隔离功能。*

Docker 容器是*运行时的 Docker 镜像，*简而言之是轻量级、标准和安全的。那么*Docker 容器*技术究竟是什么时候出现的呢？

2013 年，*Docker 容器*技术以[***开源 Docker 引擎***](https://docs.docker.com/engine/#:~:text=Docker Engine is an open,and instruct the Docker daemon.)的形式发布。因此，*可以将容器*视为*Docker的“**组织单元*” 。此外，我们的容器中还运行着便携软件。就像货船集装箱一样，我们可以移动这个集装箱（*运送软件*）、修改、管理、创建、删除或销毁它。

所以简单来说，如果*Docker镜像*是一个模板或者蓝图，那么容器就是这个镜像的运行副本。我们可以有这个镜像的多个副本，这意味着我们可以有多个容器具有相同的镜像。

### ***为什么我们需要 Docker 容器？***

我们运行软件时可能会出现各种问题。例如，我们可能在*Debian 版本的 Linux*上开发我们的软件，而生产环境可能有*Red Hat 版本*。或者我们可能使用*python 3*作为我们的开发语言，而测试环境可能使用另一个版本的 python。

或者，即使我们以某种方式实现了软件版本的统一，而不管软件开发周期的各个阶段，软件必须运行的机器可能还有其他因素，如网络拓扑、存储或安全策略等。因此，它会导致一些问题，使我们的应用程序难以运行。

为了解决这些问题，我们将应用程序、代码、它需要运行的二进制文件以及所有其他依赖项打包到一个 Docker 镜像中，然后将其运送到其他环境。然后这个镜像运行在各种环境的容器中。

所以通过使用 Docker 容器，我们实现了软件在所有类型的基础设施上的统一运行，我们不需要担心软件版本、网络拓扑、存储策略等其他细节。

*因此，我们可以总结出我们需要Docker容器*的原因如下：

1.  *与虚拟机相比，Docker 容器使我们能够更轻松、更快速地部署、复制、移动或备份整个工作负载。因此，它为我们节省了大量时间和复杂性。*
2.  *有了容器，我们对任何运行容器的基础设施都具有类似云的灵活性。*
3.  *Docker 容器是 Linux 容器 (LXC) 的高级形式，它允许我们构建图像库、从这些图像构建应用程序并在本地和远程基础设施上启动它们和容器。*
4.  *此外，Docker 容器解决了将软件从一个计算环境移动和运行到另一个计算环境的问题，例如从开发环境到测试或暂存或生产环境。*
5.  *Docker 容器还有助于将应用程序和图像从物理机移动到私有/公共云中的虚拟机。*

总之，由于容器将应用程序、依赖项、二进制文件、库、配置文件等整个运行时环境包含在一个包中，因此我们可以轻松地抽象出操作系统发行版及其底层基础设施的差异，而只专注于软件。

因此，应用程序/软件的容器化简化了软件专业人员的生活。

### ***Docker 容器如何工作？***

在本节中，我们将了解*Docker 容器*的基本工作原理。但是，首先，让我们了解 Docker 容器的生命周期。

下图显示了*容器生命周期*：![1-Docker容器生命周期.png](https://www.toolsqa.com/gallery/Docker/1-Docker%20container%20lifecycle.png)

如上面的生命周期所示，容器在其生命周期中将具有以下阶段：

1.  ***Created**：最初，一个 docker 容器被**创建***。
2.  ***Running** : 然后，我们执行使其进入**运行**状态的容器*
3.  ***Killed**：如果我们用完 docker 容器或不再需要它，我们会**杀死**容器。*
4.  ***暂停**：我们可能还想**暂停**容器一段时间然后**取消暂停***
5.  ***Stopped**：或者，我们可以**停止**容器进行一些外部工作，然后**恢复**它的执行，这样它就可以运行了。*

#### ***Docker容器的工作***

下图让我们深入了解*Docker 容器*的工作：![2-Docker Container.png 的工作](https://www.toolsqa.com/gallery/Docker/2-Working%20of%20a%20Docker%20Container.png)

我们可以通过以下步骤来解释上图。

-   *开发人员首先编写一个 docker 文件。这个 docker 文件包含整个项目代码。*
-   *然后从这个 docker 文件构建 docker 镜像。*
-   *此 docker 图像上传到 docker hub。Docker hub 是一个基于云的存储库，用于保存 docker 镜像。*
-   *其他团队，如账户、QA、生产、暂存等，然后可以从 docker hub 中拉取这个 docker 镜像。*

在继续本系列的过程中，我们将详细介绍每个步骤。

## Docker 容器命令

在我们的“ *Docker images* ”主题中，我们介绍了各种操作图像的命令。容器包含运行图像，为了有效地使用容器，我们也需要操作容器。

容器的操作涉及*启动/停止容器、创建、运行、列出容器进程、重新启动容器*等。通过为每个操作提供适当的命令，Docker 支持与操作容器相关的所有这些操作。

现在让我们讨论一些容器命令，这些命令用于更改容器的各种状态，或者换句话说，如我们上面讨论的那样操纵容器。

### ***Docker 运行命令 - 启动容器镜像/运行容器***

Docker 中的“***运行***”命令执行容器。

***容器的运行是通过Docker run***命令管理/处理的。要以交互模式运行容器，Docker 容器应首先启动。

运行命令的一般语法是：

```
docker run [OPTIONS] IMAGE [COMMAND] [ARG...]
```

“ *docker run* ”命令将首先在给定图像上创建一个可写容器层，然后使用上述命令启动它。所以命令“ *docker run* ”等同于首先期待“ */containers/create* ”和“ */containers/(id)/start* ”。

例如，以下命令：

```
docker run ubuntu bash
```

按 Ctrl+p，控件将返回到 OS shell。然后我们将运行 ubuntu bash 系统的一个实例。

上述命令的输出/结果如下。![3-Launch Docker Container.png](https://www.toolsqa.com/gallery/Docker/3-Launch%20Docker%20Container.png)

正如我们所见，执行上述命令会导致***下载 ubuntu 的最新映像，***因为它尚不存在。

### ***docker ps 命令 - 列出 Docker 容器***

我们可以列出机器上存在的容器。我们可以使用“ *docker ps* ”命令来完成。

命令的一般语法是：

```
docker ps [OPTIONS]
```

如果没有选项，' *docker ps* ' 命令会列出所有当前正在运行的容器。当我们想要列出机器上存在的所有容器时，我们将“ *-a* ”选项与“ *docker ps ”命令一起使用。*

例如，要列出主机中的所有容器，我们给出以下命令。

```
docker ps -a
```

上面的命令给出了以下输出。![4-Docker容器列表.png](https://www.toolsqa.com/gallery/Docker/4-List%20of%20Docker%20Container.png)

如上面的屏幕截图所示，该命令列出了系统上存在的所有容器。

### ***docker commit 命令 - 保存 Docker 容器***

*我们可以使用“ commit* ”命令保存容器。当容器正在运行并且我们调用 commit 命令时，容器会暂停。commit 命令根据容器的更改***创建一个新图像***。提交命令的一般语法是：

```
docker commit [OPTIONS] CONTAINER [REPOSITORY[:TAG]]
```

例如，让我们将当前正在运行的容器提交给名为“ *testimage* ”的新容器。

屏幕截图显示了此命令的执行。![5-保存Docker Container.png](https://www.toolsqa.com/gallery/Docker/5-Save%20Docker%20Container.png)

在上面的屏幕截图中，我们使用“ *docker ps* ”命令列出了正在运行的容器。然后我们执行' *docker commit 5cd67cfa20f7 testimage* '。现在我们再次使用“ *docker images* ”命令列出图像。我们可以看到新创建的 docker 镜像***testimage 。***

### ***docker stop 命令 - 停止容器***

容器可以使用“ *docker stop* ”命令停止。一般命令语法如下：

```
docker stop ContainerID
```

这里，***ContainerID***是要停止的容器的***Id 。***

***注意**：我们可以通过执行上面解释的“docker ps”命令来获取容器的 ContainerID。*

***停止***命令返回已停止的容器的 ID。

我们可以在下面的屏幕截图中看到停止容器的示例。![6-Stop Docker Container.png](https://www.toolsqa.com/gallery/Docker/6-Stop%20Docker%20Container.png)

在上面的屏幕截图中，我们有一个正在运行的容器“ *ubuntu* ”。*我们使用ubuntu*容器的*containerId*调用“ *docker stop* ”命令。下一个检查正在运行的容器列表的命令验证了 ubuntu 容器已停止。

### ***docker history 命令——查看Docker容器历史***

我们可以使用“ ***docker history*** ”命令来查看通过容器运行的所有命令。命令的一般语法是：

```
docker history ImageID
```

这里的*ImageID*是我们想要查看所有已运行命令的图像的*ID 。*

***注意**：我们可以通过执行上面解释的“docker ps”命令来获取容器的 imageID。*

此命令返回针对指定图像运行的所有命令。例如，考虑以下命令：

```
docker history ubuntu
```

这里我们将*imageId 指定*为“ *ubuntu* ”。该命令的输出如下：![7-Docker容器历史记录.png](https://www.toolsqa.com/gallery/Docker/7-Docker%20Container%20History.png)

上面的命令将显示针对***ubuntu*** 映像运行的所有命令。

### ***docker top 命令——查看容器进程***

使用“ *docker top* ”命令，我们可以看到容器内的顶级进程。*“ docker top* ”命令的一般语法是：

```
docker top ContainerID
```

***ContainerId***是我们需要为其列出顶级进程的容器的 ID 。执行此命令时，输出显示容器中的顶级进程。

请参考下面的示例来查看“ *docker top* ”命令的执行情况。![container.png 的 8-'Top' 命令](https://www.toolsqa.com/gallery/Docker/8-'Top'%20Command%20for%20container.png)

在上面的屏幕截图中，我们为“ *docker top* ”命令提供了正在运行的容器的容器 ID。命令的输出显示容器内的进程详细信息。

### ***docker rm 命令 - 删除容器***

如果我们不再需要一个容器，我们可以从系统中移除或删除它。我们可以通过使用“ *docker rm* ”命令来实现它。*“ docker rm* ”命令的一般语法如下：

```
docker rm ContainerID
```

此处，*ContainerId*是我们必须删除的容器的*ID 。*该命令返回我们成功删除的容器的 Id。

例如，请考虑以下屏幕截图。![9-删除Docker Container.png](https://www.toolsqa.com/gallery/Docker/9-Remove%20Docker%20Container.png)

从上面的屏幕截图可以看出，我们的系统中有各种容器（*docker ps 命令显示了它*）。我们想从系统中删除*ubuntu*容器。因此，我们将其 Id ( *4e94e8c0444f* ) 提供给 docker rm 命令。

为了验证容器的删除，我们再次列出所有容器。现在我们看到 id = 4e94e8c0444f 的 Container 不再列出。

### ***docker stats 命令 - 获取容器统计信息***

' *docker stats* '命令为我们提供了正在运行的容器的统计信息。此命令具有一般语法：

```
docker stats ContainerID
```

此命令将返回指定容器的*CPU 和内存利用率*。例如，观察以下命令，

```
docker stats 2d9173b71ca1
```

上面的命令给出了以下输出。![10-Docker容器统计.png](https://www.toolsqa.com/gallery/Docker/10-Docker%20Container%20Statistics.png)

如上所示，上述命令的输出显示了 id = 2d9173b71ca1 的容器的 CPU、内存和 I/O 利用率。

### ***docker pause 命令 - 暂停/取消暂停容器***

我们可以暂时停止或暂停正在运行的 Docker 容器。此外，暂停的容器可以取消暂停并再次运行。对于这些操作，我们分别使用“ *docker pause* ”和“ *docker unpause* ”命令。

*“ docker pause* ”命令的一般语法是：

```
docker pause ContainerID
```

*Container ID*是需要暂停的容器的id 。我们可以在下面的屏幕截图中看到暂停容器的命令执行。![11-暂停 Docker Container.png](https://www.toolsqa.com/gallery/Docker/11-Pause%20a%20Docker%20Container.png)

从截图中，我们可以看到暂停命令暂停的容器的状态（*红色矩形中所示*）。

同样，***unpause***命令的一般语法如下：

```
docker unpause ContainerID
```

它将取消暂停由先前暂停的*ContainerId指定的容器。*下面的屏幕截图显示了“***取消暂停***”命令的工作。![12-取消暂停 Docker Container.png](https://www.toolsqa.com/gallery/Docker/12-Unpause%20a%20Docker%20Container.png)

所以我们在上面的例子中有一个暂停的容器' *ubuntu '。**然后我们使用containerId*调用“ *docker unpause* ”命令。它将取消暂停容器。

### ***docker attach 命令 - 附加到容器***

' *docker attach* '命令帮助我们将本地标准输入/输出和错误流附加到正在运行的容器。例如，通过将终端的标准 I/O 和 Error 流附加到正在运行的容器中，我们可以查看容器的输出并以交互方式控制容器。

*' docker attach* '的一般语法如下：

```
docker attach ContainerID
```

它将带有“ *ContainerID* ”的容器附加到终端的标准输入、输出和错误流。

以下屏幕截图显示了此命令的工作原理。![13-附加到容器.png](https://www.toolsqa.com/gallery/Docker/13-Attach%20to%20a%20container.png)

上述命令已将标准 I/O 和错误附加到容器。

### ***docker kill 命令 - 杀死一个容器***

我们还可以使用“ *docker kill* ”命令终止正在运行的容器中的进程。

该命令的一般语法是：

```
docker kill ContainerID
```

参数*ContainerId*是使用此命令杀死其进程的容器的 Id。

考虑以下屏幕截图，其中我们展示了“ *docker kill* ”命令的工作。![14-Kill Docker Container.png](https://www.toolsqa.com/gallery/Docker/14-Kill%20Docker%20Container.png)

在上面的屏幕截图中，我们有一个正在运行的容器，其 ID 为“ *2d9173b71ca1* ”。现在我们使用上述容器 ID执行“ *docker kill ”命令。*当我们再次列出正在运行的容器时，我们发现容器已经消失了，这意味着它被杀死了。

所以现在我们已经介绍了一些我们用来操作容器的重要命令。[***我们可以在docker commands***](https://docs.docker.com/engine/reference/commandline/run/)找到更多这样的命令。

## 如何使用特权 Docker 容器？

在“*特权模式*”下运行*Docker 容器是**Docker*提供的最有用的功能之一。首先，让我们了解什么是特权容器。

*Docker 容器在以特权模式运行时具有特权。Docker 特权模式授予 Docker 容器访问主机系统上所有设备的根功能。*

这意味着特权*Docker 容器*可以访问主机的内核功能和其他设备功能。特权容器甚至可以在其中安装新的 Docker 实例。本质上，特权模式允许***在 docker 内部运行 docker***。

下图概述了特权容器的功能。![15-特权容器.png](https://www.toolsqa.com/gallery/Docker/15-Privileged%20Container.png)

正如我们从上图中看到的，特权容器可以访问运行 Docker 的主机系统，这与其他普通容器不同。

*我们可以使用命令“ docker inspect* ”来检查给定容器是否具有特权，如下所示：

```
docker inspect --format='{{.HostConfig.Privileged}}' [container_id]
```

这里“ *[container_id]* ”是我们要检查的容器的 ID，以检查它是否具有特权。

考虑以下屏幕截图。![16-检查Container是否有特权.png](https://www.toolsqa.com/gallery/Docker/16-Check%20if%20Container%20is%20privileged.png)

在上面的截图中，我们有一个正在运行的容器。现在我们执行*docker inspect*命令来检查它是否是一个特权容器。我们看到该命令返回 false。这意味着容器没有特权。

对于特权容器，该命令返回 true。

如果我们想以特权模式运行容器，那么我们可以进行如下操作。

我们在运行容器时提供了一个标志“ ***--privileged ”。***因此，为了以特权模式运行容器，docker run 命令将是：

```
docker run --privileged [image_name]
```

因此，如果我们想以特权模式运行上面示例中的*ubuntu*容器，我们可以给出以下命令。

```
docker run -it --privileged ubuntu
```

它将为 ubuntu 容器打开一个交互模式。为了验证容器是否确实在特权模式下运行，让我们尝试挂载一个临时文件系统。但是，首先，让我们发出命令。

```
mount -t tmpfs none /mnt
```

执行此命令后，我们可以使用以下命令列出磁盘空间统计信息。

```
df -h
```

该命令的输出如下：![17-特权模式下的容器.png](https://www.toolsqa.com/gallery/Docker/17-Container%20in%20privileged%20mode.png)

上面的输出显示了我们刚刚创建的文件系统。这意味着 ubuntu 容器以特权模式运行，甚至可以挂载新的文件系统。

### ***特权 Docker 容器是个好主意吗？***

虽然我们可以让 docker 容器以特权模式运行，但 Docker 并不鼓励以特权模式运行容器。

1.  *这样做的主要原因是将宿主系统的内核和硬件资源暴露给外界，对系统构成潜在威胁。*
2.  *拥有特权容器也会给任何组织带来安全风险，因为恶意用户可以控制系统。此外，即使是合法用户也可能滥用特权进行恶意活动。*

因此，我们建议***不要***在 Docker 中使用特权容器。

但是如果我们必须使用特权容器，我们可以通过“*用户名称空间重新映射*”将恶意活动的风险降至最低，该方法将特定容器（*需要 root 权限*）的用户重新映射到特权较低的主机系统用户。

这样，容器将用户视为 root，但主机系统将其视为普通用户。

但是如何实现这种重映射呢？

重新映射是通过分配在容器名称空间内运行的 UID 范围作为正常 UID（*范围从 0 到 65536*）而没有主机权限的。两个文件，*** /etc/subuid*** 用于用户 ID 范围和***/etc/subgid***用于组 ID 范围，管理用户配置。

默认情况下，Docker 使用名为***dockermap***的用户和组来实现重映射。[***单击此处***](https://docs.docker.com/engine/security/userns-remap/)查找有关在 Docker 中重新映射的更多信息。

## 如何保护容器？

Docker 容器比虚拟机更安全。这是因为 Docker 还允许应用程序分解为更小的、孤立的和松散耦合的组件。因此，它在某种程度上极大地限制了黑客。但是，无论采用何种安全系统，技术安全漏洞始终存在，因此我们必须遵循某些最佳实践来保护 Docker 容器。

下面我们列出了一些在使用*Docker*时可以遵循的最佳实践。

1.  ***以非 root 用户身份运行容器**：如前一节所述，以特权模式运行容器可能会将系统暴露给黑客。因此，我们应该尽可能避免使用非root权限的用户以特权模式运行容器。*
2.  ***使用他们的私有注册表**：与其使用可能具有非安全图像的公共图像注册表，不如使用一个人的私有注册表。组织可以使用本地基础设施或使用第三方注册表服务（如 Amazon ECR）来托管私有注册表。*
3.  ***保持图像更小、更精简、更干净**：图像越大，威胁就越大。因此，我们在从注册表中提取图像时应该谨慎。图像应该是占用空间最少的图像，然后将二进制文件和依赖项添加到应用程序中。我们还可以使用多阶段构建功能来优化图像大小。*

这些是我们在使用 Docker 时保护容器的一些重要方法。

## 关键要点

-   *docker 容器是我们可以运行 docker 镜像的组件。*
-   *Docker 容器使我们能够简化和最小化系统中的工作负载。*
-   *我们可以使用各种 docker 命令来操纵 docker 容器的状态。我们在本文中通过解释和示例讨论了主要的 Docker 容器命令。*
-   *Docker 容器还可以在特权模式下运行，在该模式下，容器获得 root 权限并具有对主机系统的 root 访问权限。虽然我们可以很容易地将Docker容器切换到特权模式，但是docker本身并不推荐在特权模式下使用容器。*