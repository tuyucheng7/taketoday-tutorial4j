Docker 提供了一套“平台即服务”产品，通过将应用程序打包到容器中来帮助我们开发和部署应用程序。此外，对于这些应用程序的开发和部署，Docker 提供了一系列新的术语和一组新的命令。到目前为止，我们已经熟悉了 Docker 中的主要概念，[***如***](https://www.toolsqa.com/docker/introduction-to-docker-and-docker-architecture/)Docker [***Containers***](https://www.toolsqa.com/docker/docker-containers/)、[***Docker Image***](https://www.toolsqa.com/docker/docker-images/)、***Dockerfile***等。此外，在详细探索这些主题时，我们还看到了一些***Docker 命令。***但是，为所有常用命令提供一个参考点总是有益的。随后，在本教程中，您将在一个地方找到最常用的*命令*。文章将涵盖：

-   *Docker 容器命令*
-   *Docker 镜像命令*
-   *另外，Docker 命令信息*
-   *Docker 网络命令*

## Docker 容器命令

*Docker 容器*是轻量级和可移植的组件，是我们可以共享的虚拟环境，而不必冒开发中出现不一致的风险。此外，下表总结了各种*Docker 容器命令*：

| ***容器命令***            | ***用法***                                          |
| ------------------------- | --------------------------------------------------- |
| 创建容器                  | 泊坞窗创建 [图像]                                   |
| 重命名容器                | docker 重命名 [CONTAINER_NAME] [NEW_CONTAINER_NAME] |
| 运行容器                  | docker run [选项] [图像] [命令] [ARG ...]           |
| 移除或删除容器            | docker rm [选项] [容器]                             |
| 启动一个容器              | docker 启动 [选项] [容器]                           |
| 停止一个容器              | docker stop [选项] [容器]                           |
| 重启一个容器              | docker restart [选项] [容器]                        |
| 暂停容器                  | 泊坞窗暂停 [容器]                                   |
| 取消暂停容器              | docker unpause [容器]                               |
| 更新容器                  | docker 更新 [选项] [容器]                           |
| 将本地标准 I/O 附加到容器 | docker attach [选项] [容器]                         |
| 阻塞容器直到其他人停止    | 码头工人等待[容器]                                  |
| 杀死一个容器              | docker kill [选项] [容器]                           |

一些命令包含一些可应用于该命令的选项。例如，在***docker restart***命令中，我们可以将重启前考虑的时间指定为选项的一部分。[***此外，您可以在Docker 的官方网站***](https://docs.docker.com/engine/reference/commandline/docker/)上探索这些选项。

因此，让我们在以下部分中更详细地了解所有这些命令的语法和用法：

### ***docker 创建命令***

我们使用/运行“ ***docker create*** ”命令来创建容器。[***此外，此命令在给定的Docker 映像***](https://www.toolsqa.com/docker/docker-images/)上创建了一个可写容器层。请注意，由于“ *docker create* ”命令而创建的容器未运行。此外，我们可以使用“ *docker run* ”命令启动它。随后，该命令的一般语法为：

```
docker create [IMAGE]
```

这里的 [IMAGE] 是要创建容器层的镜像。因此，在成功执行时，该命令返回容器 ID STDOUT。

让我们围绕名为“ *ubuntu* ”的图像创建一个容器。此外，该命令的输出是：![1-Docker命令-创建容器.png](https://www.toolsqa.com/gallery/Docker/1-Docker%20command%20-%20create%20container.png)

我们可以看到该命令返回了新创建的容器的 ID。接下来，运行命令“ *docker ps* ”。因此，我们看到输出为空。这是因为刚刚创建的容器没有运行。*此外，如果我们为“ docker ps* ”命令提供标志 -a ，我们将看到我们刚刚创建的容器（*如上面的输出所示*）。“ ***docker create*** ”命令在创建容器供将来使用而不是暂时使用时很有用。此外，您希望容器在需要启动时准备就绪。

### ***泊坞窗重命名命令***

重命名现有容器的命令是“ ***docker rename*** ”。此命令将容器的当前名称和新名称作为参数，并使用新名称重命名容器。此外，该命令具有以下语法：

```
docker rename [CONTAINER_NAME] [NEW_CONTAINER_NAME]
```

现在让我们使用以下命令重命名容器：

```
docker rename happy_lewin myUbuntuContainer
```

此外，上述命令的输出如下所示。![2-重命名 Docker container.png](https://www.toolsqa.com/gallery/Docker/2-rename%20a%20Docker%20container.png)

上面的输出显示在第一个突出显示的框下方将名为“ *happy_lewin* ”的容器重命名为*myUbuntuContainer 。*

### ***泊坞窗运行命令***

' *docker run* ' 命令在给定图像上创建一个可写容器层，然后使用指定命令作为参数启动它。此外，该命令相当于“ ***docker create*** ”+“ ***docker start*** ”。不同之处在于“ ***docker run*** ”还会在创建容器的同时启动容器。此外，此命令具有以下语法。

```
docker run [IMAGE] [COMMAND]
```

' *docker run* ' 命令有以下变化。

-   *docker run --rm [IMAGE]– 移除/删除容器一旦退出。*
-   *docker run -td [IMAGE]– 启动容器并保持运行状态。*
-   *接下来是 docker run -it [IMAGE] - 启动一个容器并分配一个连接到容器标准输入的伪 TTY。此外，它还在容器中创建一个交互式 bash shell。*
-   *docker run -it -rm [IMAGE] – 在容器内创建、启动和运行命令。此外，一旦执行命令，它就会删除容器。*

以下屏幕截图显示了此命令的执行输出。![3-创建并运行container.png](https://www.toolsqa.com/gallery/Docker/3-Create%20and%20run%20container.png)

在这里，我们创建并启动了一个名为*ubuntu*的容器。此外，当我们执行“ *docker ps* ”命令时，我们可以在输出中看到这个新创建的容器。

### ***docker rm 命令***

当容器未运行时，我们可以使用“ *docker rm* ”命令将其删除。此外，该命令的用法如下：

```
docker rm [CONTAINER]
```

我们只需将容器 ID 作为参数传递给 docker rm 命令。

以下屏幕截图显示了“ *docker rm* ”命令的工作。![4-删除容器.png](https://www.toolsqa.com/gallery/Docker/4-remove%20container.png)

这里我们有一个正在运行的容器，显示为第一个命令*docker ps*的输出。请注意，如果我们尝试删除它，Docker 将给出一个错误，因为容器正在运行。因此，我们需要先停止容器，然后再使用' *docker rm* '命令移除容器。上面的屏幕截图显示了这一点。所以首先，我们停止正在运行的容器，然后将其删除。此外，我们已经通过列出容器来验证这一点。因此，我们看到容器现在不存在。

### ***码头工人启动命令***

我们可以启动一个或多个使用“ *docker create* ”命令创建的容器或使用“ *docker start* ”提前停止的容器。此外，该命令的一般语法如下所示。

```
docker start [CONTAINER]
```

现在让我们使用“ *docker create* ”命令创建一个新容器，然后使用“ *docker start* ”命令启动它。![5-Docker命令-启动一个容器.png](https://www.toolsqa.com/gallery/Docker/5-Docker%20command%20-%20start%20a%20container.png)

我们看到执行“ *docker create* ”命令的结果是创建了一个新容器（*检查容器状态*）。之后，我们使用“ ***docker start*** ”命令启动容器。因此，成功执行会返回容器 ID 或名称。现在，如果我们检查状态，它显示容器已经退出。换句话说，这意味着它已启动然后退出。

### ***泊坞窗停止命令***

正如我们可以启动一个停止的容器一样，我们也可以使用命令“ *docker stop* ”停止一个正在运行的容器。作为此命令的结果，容器内的主进程接收到*SIGTERM*，并且在规定的等级周期后，它接收到将停止容器的*SIGKILL 。*此外，下面给出了停止命令的一般用法。

```
docker stop [CONTAINER]
```

可以为命令“ docker *stop* ”提供 --time（*或 -t*）选项，以指定容器停止和终止之前的时间限制（以秒为单位）。例如，假设我们要等待 500 秒来停止容器。因此，我们可以提供如下命令：

```
docker stop --time 500 xenodochial_cohen
```

此处，“ *xenodochial_cohen* ”是要停止的容器的名称。所以这里我们会等待 500 秒停止，然后再杀死这个容器。

### ***码头工人重新启动命令***

我们使用‘ *docker restart* ’命令来重启容器，即停止正在运行的容器并重新启动它。重启命令的一般语法是：

```
docker restart [CONTAINER]
```

我们可以使用此命令重新启动上面停止的容器。![6-重启容器.png](https://www.toolsqa.com/gallery/Docker/6-restart%20a%20container.png)

上面的屏幕截图显示了已停止容器的启动。

### ***泊坞窗暂停命令***

“ *docker pause* ”命令通过暂停 1given 容器中的所有进程来临时暂停或停止容器。下面是这个命令的用法。

```
docker pause [CONTAINER]
```

### ***码头工人取消暂停命令***

“ *docker unpause* ”命令与“ *docker pause* ”命令相反。此命令取消暂停给定容器中的所有进程。该命令的用法是：

```
docker unpause [CONTAINER]
```

下面的屏幕截图显示了容器的暂停和取消暂停。![7-暂停/取消暂停 Docker 容器.png](https://www.toolsqa.com/gallery/Docker/7-pause%20unpause%20a%20Docker%20container.png)

上面的截图显示了容器的暂停/取消暂停。首先，我们暂停正在运行的容器，然后取消暂停。

### ***泊坞窗更新命令***

我们甚至可以动态更新容器配置。这是使用“ *docker update* ”命令执行的。使用此命令，我们可以防止容器过度消耗 Docker 主机的资源。' *docker update* ' 命令可以一次更新一个或多个容器。命令的一般语法是：

```
docker update [CONTAINER]
```

### ***docker 附加命令***

可以将标准输入/输出或错误终端附加到 Docker 中正在运行的容器。这是使用“ *docker attach* ”命令完成的。通过将正在运行的容器附加到这些 I/O 流，我们可以查看容器正在进行的活动并以交互方式控制它们。例如，此命令具有以下语法：

```
docker attach [CONTAINER]
```

我们使用“ *docker attach* ”命令将标准终端附加到名为“ *zealous_turing* ”的容器。屏幕截图如下所示。![8-docker附加命令.png](https://www.toolsqa.com/gallery/Docker/8-docker%20attach%20command.png)

当我们将容器附加到标准 I/O 时，我们可以看到终端已打开。请注意，您还可以从 Docker 主机上的不同会话同时多次附加到同一个包含的进程。

### ***码头工人等待命令***

命令“ *docker wait* ”会阻塞容器，直到一个或多个容器停止。然后打印他们的退出代码。换句话说，这个命令等待其他容器。该命令的用法是：

```
docker wait [CONTAINER]
```

因此，如果我们有一个依赖其他容器继续进行的系统，我们可以阻塞当前容器，直到另一个容器停止。

### ***码头工人杀死命令***

命令“ *docker kill* ”杀死一个或多个容器。容器内的主进程在执行此命令时发送 SIGKILL 信号或用 --signal 选项指定的信号。我们可以使用容器 ID 或名称或 ID 前缀指定要杀死的容器。

```
docker kill [CONTAINER]
```

让我们执行 kill 命令。考虑以下屏幕截图。![9-docker kill 命令.png](https://www.toolsqa.com/gallery/Docker/9-docker%20kill%20command.png)

在上面的屏幕截图中，我们看到一个正在运行的容器“ *xenodochial_cohen* ”。我们执行“ *docker kill* ”命令，然后如果我们检查状态，我们会看到容器已退出，代码为 137。

## Docker 镜像命令

下表显示了与 Docker 镜像一起使用的重要命令。

| ***Docker 镜像命令***          | ***用法***                                           |
| ------------------------------ | ---------------------------------------------------- |
| 从 Dockerfile 创建 Docker 镜像 | docker build [选项] [URL]                            |
| 从注册表中拉取镜像             | docker pull [选项] [图像]                            |
| 将图像推送到注册表             | docker push [选项] [图像]                            |
| 从容器创建图像                 | docker commit [OPTIONS] [CONTAINER] [NEW_IMAGE_NAME] |
| 删除图像                       | docker rmi [选项] [图像]                             |
| 保存图像                       | docker save [OPTIONS] [IMAGE] > [TAR_FILE]           |

现在让我们讨论上述每个命令。

### ***docker构建命令***

我们可以使用“docker *build* ”命令从当前目录中的*Dockerfile构建镜像。**docker build 命令使用Dockerfile*和“*上下文*”创建/构建 Docker 镜像。上下文是位于指定 PATH 或 URL 中的一组文件。

```
docker build [URL]
```

此命令的变体， docker *build -t*从当前目录中的*Dockerfile* 构建图像并标记图像。

考虑以下*Dockerfile*。

```
FROM ubuntu

MAINTAINER guest

RUN apt-get update

CMD ["echo", "Hello World"]
```

现在，让我们给出以下命令从这个 Dockerfile 构建一个 Docker 镜像。

```
docker build -t my_docker_image .
```

执行上述命令时会创建一个名为“ *my_docker_image* ”的图像，如下面的屏幕截图所示。![10-Docker命令——从Dockerfile.png构建一个docker镜像](https://www.toolsqa.com/gallery/Docker/10-Docker%20command%20-%20build%20a%20docker%20image%20from%20Dockerfile.png)

*我们可以使用“ docker images* ”命令验证这个新图像的创建，这个图像将如下面的截图所示列出。![11-图像列表.png](https://www.toolsqa.com/gallery/Docker/11-image%20list.png)

除了指定*Dockerfile*来构建新镜像之外，我们还可以使用相同的命令使用 URL 构建 Docker 镜像，例如，直接从 GIT 存储库中构建。或远程压缩包。有关更多详细信息，请参阅[***docker 构建***](https://docs.docker.com/engine/reference/commandline/build/)。

### ***泊坞窗拉命令***

除了创建全新的镜像，我们还可以从容器注册表中提取现有镜像，例如包含许多预构建镜像的[***Docker Hub 。***](https://hub.docker.com/)拉取镜像的命令如下：

```
docker pull [IMAGE]
```

如果我们想从集线器中拉取名为“ *Redis* ”的图像，我们将发出命令并获得如下屏幕截图所示的输出。![12-Docker命令-从registry.png中拉取镜像](https://www.toolsqa.com/gallery/Docker/12-Docker%20command%20-%20pull%20image%20from%20registry.png)

我们可以看到从 hub 拉取 Redis 镜像时执行的整个过程。

### ***泊坞窗推送命令***

我们将镜像从注册表拉到我们的 Docker 主机上。同样，我们可以将图像或存储库推送到容器注册表。为此，我们使用“ *docker image push* ”命令，其一般语法如下所示。

```
docker push [IMAGE]
```

### ***码头工人提交命令***

我们还可以通过 docker commit 命令通过容器的更改从现有容器创建新镜像。在这里，我们通常将容器更改或设置提交到新图像。从容器创建映像的一个优点是通过运行交互式 shell 来调试容器。我们还可以将工作数据集导出到不同的服务器。该命令的用法如下：

```
docker commit [CONTAINER] [NEW_IMAGE_NAME]
```

例如，请考虑以下屏幕截图。![13-Docker命令——从container.png创建Docker镜像](https://www.toolsqa.com/gallery/Docker/13-Docker%20command%20-%20create%20Docker%20image%20from%20container.png)

这里我们有一个正在运行的容器（*由 docker ps 输出显示*）。我们在 docker commit 命令中使用这个容器 ID 来创建一个名为“ *my_docker_image* ”的新图像。我们看到该命令返回刚刚创建的图像的 ID。

### ***docker rmi 命令***

Docker 还允许从主机节点删除或取消标记一个或多个图像。为此，我们使用命令“ *docker rmi* ”。此命令的一般/通用语法是：

```
docker rmi [IMAGE]
```

上述命令不会从注册表中删除 docker 镜像。***要删除正在运行的容器的 docker 镜像，我们必须指定 -f 选项***。

请注意，如果图像有多个标签，此命令将仅删除标签。如果标签仅用于一个图像，则图像和标签都会被移除/删除。

我们可以简单地给出命令“ *docker rmi my_container_image* ”来取消标记图像。

### ***docker 保存命令***

我们可以使用“ *docker save* ”命令将一个或多个图像保存到 tar 存档中。这会生成一个到 STDOUT 的焦油存储库。存储库包含所有父层和所有标签和版本或指定的 repo::tag。此命令的一般/通用语法是：

```
docker save [IMAGE] > [TAR_FILE]
```

让我们将名为“ *testimage* ”的图像保存到 tar 文件中，如下面的屏幕截图所示。![14-保存Docker镜像.png](https://www.toolsqa.com/gallery/Docker/14-save%20Docker%20image.png)

命令“ *docker save testimage >testimage.tar* ”在本地保存 tar 存档文件。当我们执行“ *ls* ”命令时，它会列出存档文件，如上图所示。

## 用于信息的 Docker 命令

除了创建和维护镜像和容器之外，Docker 还提供了各种命令来帮助我们检索有关容器、镜像和其他 Docker 对象的重要信息。下表包含将提供有关图像和容器的详细信息的命令。

| ***Docker 信息命令***              | ***用法***                  |
| ---------------------------------- | --------------------------- |
| 获取 Docker 版本                   | 泊坞窗——版本                |
| 列出正在运行的容器                 | 泊坞窗                      |
| 列出正在运行的容器中的日志         | 泊坞窗日志 [容器]           |
| 列出有关 Docker 对象的低级信息     | 泊坞窗检查 [OBJECT_NAME/ID] |
| 列出实时容器事件                   | 码头事件[容器]              |
| 显示容器映射（*端口或任何特定的*） | 码头港口[容器]              |
| 显示容器中正在运行的进程           | docker top [容器]           |
| 显示容器统计信息的实时使用情况     | 泊坞窗统计 [容器]           |
| 显示对文件的更改                   | docker diff [容器]          |
| 列出所有图像                       | 泊坞窗图像 ls               |
| 显示图像历史                       | 码头历史 [图片]             |

让我们在以下部分中了解所有这些命令的详细信息：

### ***泊坞窗版本命令***

命令“ *docker --version* ”提供有关主机上存在的 Docker 版本的信息。此外，命令语法是

```
docker --version
```

执行时，此命令会提供以下输出。![15-获取Docker版本.png](https://www.toolsqa.com/gallery/Docker/15-Get%20Docker%20version.png)

此外，输出显示了 Docker 的版本和内部版本号。

### ***docker ps命令***

*我们可以使用“ docker ps* ”命令列出主机上存在的容器。此外，该命令的语法如下：

```
docker ps
```

要获取正在运行的容器和已停止的容器的列表，我们可以为上述命令提供一个“ *-a ”选项。*

```
docker ps -a
```

以下屏幕截图显示了“ *docker ps* ”命令的输出。![16-列表容器.png](https://www.toolsqa.com/gallery/Docker/16-List%20containers.png)

在这里我们可以看到正在运行的容器的详细信息。

### ***docker 日志命令***

*我们可以使用“ docker logs* ”命令获取 Docker 容器的日志。这将在执行时获取日志。此外，该命令的一般语法为：

```
docker logs [CONTAINER]
```

### ***码头检查命令***

如果我们需要关于任何 Docker 对象的任何低级信息，我们可以使用命令“ *docker inspect* ”。此命令为我们提供了有关 Docker 控制的构造的详细信息。此外，该命令的用法是：

```
docker inspect [OBJECT_NAME/ID]
```

默认情况下，命令“ *docker inspect* ”以 JSON 数组形式呈现输出。

例如，考虑以下命令，

```
docker inspect cbfa678479b6
```

这里 ' *cbfa678479b6* ' 是一个容器 ID。此外，此命令的输出如下所示。![17-Docker检查.png](https://www.toolsqa.com/gallery/Docker/17-Docker%20inspect.png)

因此，“ *docker inspect* ”命令提供了有关我们指定为参数的 Docker 对象的所有信息。

### ***码头事件命令***

命令“ *docker events* ”从服务器获取每个 Docker 对象类型的实时事件。此外，不同的事件类型将具有不同的范围，如本地范围、群体范围等。

```
docker events 
```

### ***docker 端口命令***

命令“ *docker port* ”列出容器的端口映射或特定映射。此外，其语法如下所示：

```
docker port [CONTAINER]
```

例如，我们可以提供容器 ID，' *docker port* ' 命令将输出有关容器端口的信息。除此之外，我们还可以为容器指定具体的映射，验证端口是否与容器相关联。此外，这显示在下面的屏幕截图中。![18-docker port.png](https://www.toolsqa.com/gallery/Docker/18-docker%20port.png)

这里我们为容器指定了端口信息。由于指定的容器没有这个端口，命令输出相应的消息，如上面的屏幕截图所示。

### ***泊坞窗顶部命令***

要显示容器的所有运行进程，我们使用命令“ *docker top* ”。此外，其语法如下所示：

```
docker top [CONTAINER]
```

下面的屏幕截图给出了“ *docker top* ”命令的执行。![19- docker top.png](https://www.toolsqa.com/gallery/Docker/19-%20docker%20top.png)

在上面的屏幕截图中，我们可以看到与正在运行的容器关联的进程，其 ID 被指定为“ *docker top* ”命令的参数。

### ***码头差异命令***

我们可以检查容器文件系统上文件或目录的更改。*此外，自从使用命令“ docker diff* ”创建容器以来，我们可以获得文件系统中已更改文件和目录的列表。此外，该命令具有以下通用语法。

```
docker diff [CONTAINER]
```

### ***docker 镜像 ls 命令***

我们可以使用命令“ *docker image ls* ”来列出存储在 Docker 系统本地的所有图像。此外，它的语法是：

```
docker image ls
```

除上述内容外，命令执行的输出如下所示。![20-列出所有图片.png](https://www.toolsqa.com/gallery/Docker/20-list%20all%20images.png)

我们可以看到该命令列出了主机上存在的所有图像。此外，等效命令“ *docker images* ”也给出了相同的输出。![21-获取图片列表.png](https://www.toolsqa.com/gallery/Docker/21-Get%20list%20of%20images.png)

比较两个输出，我们看到两个命令的行为相似。所以我们可以使用任何一个命令来获取图像列表。

### ***docker历史命令***

' *docker history* ' 命令显示指定为命令参数的 docker 映像的历史记录。

```
docker history [IMAGE]
```

例如，如果我们想知道名为“ *ubuntu* ”的 docker 镜像的历史，我们可以给出“ *docker history ubuntu* ”命令并获取输出。![22-历史.png](https://www.toolsqa.com/gallery/Docker/22-history.png)

因此，如输出所示，此命令提供了与指定图像关联的所有历史记录。

## Docker 网络命令

我们已经在“ [***Docker Networking***](https://www.toolsqa.com/docker/docker-networking/) ”一文中介绍了 Docker 网络命令。此外，[***docker 容器***](https://www.toolsqa.com/docker/docker-containers/)使用 Docker 网络命令相互连接和通信，并与其他非 Docker 环境连接和通信。随后，为了促进这种网络，Docker 提供了各种网络命令，如下表所示。

| ***Docker 网络命令*** | ***用法***                              |
| --------------------- | --------------------------------------- |
| 列出网络              | 码头网络 ls                             |
| 删除一个或多个网络    | docker 网络 rm [网络]                   |
| 显示网络信息          | 码头网络检查 [网络]                     |
| 将容器连接到网络      | docker 网络连接 [网络] [容器]           |
| 断开容器与网络的连接  | docker network disconnect [网络] [容器] |

## 要点：

-   *Docker 提供了各种命令来处理和管理 Docker 生态系统。*
-   *此外，主要命令根据它们在容器、图像、信息和网络中的用途进行分类。*