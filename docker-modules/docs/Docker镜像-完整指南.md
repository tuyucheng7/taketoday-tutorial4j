在上一章中，我们介绍了[***Docker 平台***](https://www.toolsqa.com/docker/introduction-to-docker-and-docker-architecture/)，并简要介绍了*Docker 镜像*。随后，在本文中，我们将非常详细地看到这些图像（*和 Docker hub*）。

*Docker*中的一切都基于图像。图像放置是文件系统及其参数的组合。此外，对于任何打算第一次使用*Docker的人来说，* *Docker Image* 都是起点。***Docker 镜像是一个只读模板***。它通常包含一个指令集来创建一个在*Docker 平台*上执行的容器。它为我们打包应用程序和整个服务器环境（*预配置*）。因此，我们可以私下使用此图像或公开共享它。

现在让我们继续讨论与他们相关的一些话题。

-   什么是 Docker 镜像？
    -   *Docker 运行图像命令。*
    -   *Docker 容器注册表。*
    -   *和 Docker 容器存储库。*
-   如何查看Docker镜像？
    -   *Docker 镜像命令。*
-   它是如何工作的？
    -   *Docker 图像层。*
    -   *Docker 容器层。*
    -   *父图像。*
    -   *Docker 基础镜像。*
    -   *码头工人清单。*
-   如何构建docker镜像？
    -   *文件*
    -   *如何创建docker文件？*
    -   *Docker 构建命令。*
-   如何保存docker镜像？
    -   *Docker 保存命令*
-   *如何拉取 Docker 镜像？*
-   *流行的 Docker 命令。*
-   *Docker 镜像与容器。*

## 什么是 Docker 镜像？

*Docker 镜像*是一个由许多中间层组成的文件，用于在容器中执行代码。此外，这些层负责提高*Docker*环境中的可重用性和减少磁盘使用率。图像是根据应用程序的完整工作版本的一组指令构建的。此外，应用程序版本依赖于主机操作系统。

因此，当我们说我们正在运行一个图像时，我们正在运行容器的单个或多个实例。

### ***Docker 运行镜像命令***

Docker ***run image***是 Docker 生态系统中非常常用的命令。此外，其描述如下：

```
docker run hello-world
```

-   *上面的命令促使 OS 上的 Docker 程序执行某些操作（在本例中为运行）。*
-   *此外，“**运行**”命令指定我们需要创建指定图像的实例。这个实例将被称为容器。*
-   *指定的参数“ **hello-world** ”是一个图像，我们从中创建一个容器。*

下一个命令是

```
docker run -it centos /bin/bash
```

在上面的命令中，我们尝试使用来自***Docker Hub的******CentOS***映像在 Linux Ubuntu 机器上运行 CentOS。

此外，此命令的输出如下：![1-Docker运行命令.png](https://www.toolsqa.com/gallery/Docker/1-Docker%20Run%20Command.png)

这里的*CentOS*是我们想要创建实例并在我们的主机上安装*CentOS的镜像的名称。*正如我们从输出中看到的，该命令首先尝试使用本地图像。然而，由于它不存在，图像从*Docker Hub*中提取。

此外，它们存储在特定位置，我们将在接下来讨论。

### ***Docker 容器注册表***

Docker*容器注册表*是包含存储位置或***存储库的******目录***。此外，我们可以从这些存储库中推送/存储或拉取/下载它们，我们可以在 docker hub 上公开或私下使用它们。

我们有以下主要类型的注册表。

-   ***自托管注册表**：这是组织首选的注册表模型。在这里，组织将图像托管在他们的基础设施上（内部部署）。这样，他们可以确保合规性、安全性或更低的延迟要求。*
-   ***Docker Hub**：[**Docker Hub**](https://docs.docker.com/)是 Docker 的官方镜像资源。我们可以在 Docker Hub 上访问超过 10k 个容器 docker 镜像。软件供应商、社区用户或开源项目等任何人都可以在 Docker Hub 上共享容器镜像。同时，我们可以使用 Docker Hub 服务来托管和管理我们的私有容器镜像。*
-   ***第三方注册服务**：第三方注册服务是完全托管的服务，管理与容器镜像相关的所有操作。此外，当今流行的一些第三方注册表服务包括[**Amazon ECR**](https://aws.amazon.com/ecr/)、[**Google Container Registry**](https://cloud.google.com/container-registry)、[**Azure Container Registry**](https://azure.microsoft.com/en-in/free/kubernetes-service/search/?&ef_id=CjwKCAjw_sn8BRBrEiwAnUGJDv3acCz-bpyG6qjt6r2pm9ArRWyWxj_2Y2LYvCXxSFaEi42-rFquIxoCsuIQAvD_BwE:G:s&OCID=AID2100054_SEM_CjwKCAjw_sn8BRBrEiwAnUGJDv3acCz-bpyG6qjt6r2pm9ArRWyWxj_2Y2LYvCXxSFaEi42-rFquIxoCsuIQAvD_BwE:G:s)和[**JFrog 的 Container registry 服务**](https://jfrog.com/container-registry/)。*

### ***Docker 容器存储库***

存储库中的特定物理位置充当图像的存储。每个存储库都包含一组具有相同名称的相关图像。

例如，对于 Linux 的 Ubuntu 版本，Docker hub 上的官方存储库是“ [***ubuntu***](https://hub.docker.com/_/ubuntu) ”。所以我们可以导航这个链接，发现这个Linux版本会有不同版本的镜像。

## 如何查看Docker镜像？

现在我们知道它们是什么以及我们可以在哪里找到它们，下一个问题是如何查看它们？

答案是使用 Docker 命令“ ***docker images*** ”。我们将在下面讨论这个命令。

### ***Docker 镜像命令***

***docker images***命令列出特定机器上可用的图像。此外，此命令的一般/典型语法如下：

docker 图像 [选项] [存储库 [: TAG]]

默认的***“docker images”命令将显示所有顶级图像***及其存储库、标签和大小。但是，默认命令不显示图像的中间层（*这些将在本章后面解释）。*

例如，当我们在安装了 Docker 的机器上输入命令“ ***docker images*** ”时，它会列出以下输出。![2-List Docker Images.png](https://www.toolsqa.com/gallery/Docker/2-List%20Docker%20Images.png)

如上图所示，我们执行此命令的机器有两个图像。所以这个命令会列出这些图像和它们对应的 TAG、IMAGE ID、CREATED 和 SIZE。

Docker 镜像的“***大小***”是镜像及其所有父镜像的集合空间。此外，它也是由“ ***docker save*** ”映像创建的“ ***tar*** ”文件使用的磁盘空间。

除上述情况外，如果图像在存储库中出现多次，它将具有*不同的标签*并且会列出不止一次。

## 它是如何工作的？

*Docker 镜像*收集诸如*应用程序代码、安装、二进制文件和*捆绑在一起的依赖项之类的文件。我们需要这些文件来配置和运行容器环境。

随后，让我们现在讨论这个概念以了解它们是如何工作的？下面显示的是容器内的*图像。*它显示*图像*和该图像中的*多个图层*。![container.png 中的 3-Docker 图像](https://www.toolsqa.com/gallery/Docker/3-Docker%20Image%20within%20a%20container.png)

如上图所示，我们基于特定镜像创建了一个容器，并且我们无法更改它（*镜像层显示为只读*）。***此外，当我们运行一个容器时，我们会在插入容器层***的容器内创建一个图像的读写副本。因此，这个容器层允许我们对图像的副本进行修改。

从上图我们可以推断，我们可以从一个图像库中制作无限数量的图像。此外，每次我们这样做时，都会在其上创建一个带有附加层的新模板（*如上图中多个只读图像层所示*）。此外，下面更详细地讨论图像层。

### ***Docker 图像层***

镜像中的*层*是构成 Docker 镜像的每个文件。它们在不同阶段形成许多建立在另一个之上的中间图像。此外，这些层中的每一层都依赖于当前层的紧下方的层。

这种层次结构对于图像的生命周期管理至关重要。请注意，应组织图像层，以便经常将更改放置在堆栈中尽可能高的位置。

为什么这个经常变化的层应该在最上面？因为每当镜像中的镜像层发生变化时，Docker 都会重建该特定层以及基于该层构建的所有其他层。因此，当频繁变化的图层位于堆栈顶部时，我们需要最少的计算量来重建整个图像。

### ***Docker容器层***

我们知道，当我们运行一个*镜像*时，实际上 Docker 会从该镜像启动一个容器。每次发生这种情况时，Docker 都会添加一个“***薄可写层***”。此外，该层也称为“***容器层***”，负责存储在容器执行或运行时对容器所做的所有更改。

*这个薄的可写层是可操作的实时容器*和*Docker 镜像*之间的唯一区别。因此，任何数量的容器都可以访问相同的底层图像而不会干扰其他容器。除此之外，请注意，当容器共享相同的图像时，资源开销会大大减少。

### ***父图像***

Docker 镜像的第一层通常称为“ ***Parent image*** ”。基于这个父图像，构建其他层。因此，父镜像提供了容器环境的基础。

公共容器注册表*Docker Hub*提供了大量现成的图像。以及本文前面列出的其他私有容器注册表。

例如，我们在前面的示例中使用的*CentOS映像是父映像。*

### ***Docker 基础镜像***

如果我们想从头开始构建一个 Docker 镜像，那么我们有一个空的第一层。它被称为*docker base image*。此外，使用 docker 基础镜像，我们可以完全控制镜像内容。因此，Docker 基础镜像一般适用于更高级的 Docker 用户。

### ***码头工人清单***

它是除了其他独立层文件集之外的与 Docker 镜像关联的附加文件。此外，Docker manifest 包含*JSON 格式的镜像描述，*包含镜像标签、根据主机类型配置容器的信息、数字签名等信息。例如，命令

```
docker manifest inspect hello-world
```

将生成包含图像“ ***hello-world*** ”的详细信息的输出。此外，上述命令的部分输出在以下屏幕截图中。![4-Docker清单文件.png](https://www.toolsqa.com/gallery/Docker/4-Docker%20Manifest%20File.png)

现在我们知道了 Docker 镜像的组成部分和工作方式，让我们继续讨论如何*创建它们*。

它可以通过以下两种方式创建：

-   ***交互式方法**：在这里，我们从现有图像运行或执行容器。此外，为此，我们手动更改容器环境并将获得的状态保存在新图像中。*
-   ***Dockerfile 方法**：DockerFile 方法更受欢迎。在这里，我们构建了一个名为“ **Dockerfile** ”的简单纯文本文件，其中包含创建新图像的规范。*

随后，我们将专注于*使用 Dockerfile 创建和构建镜像*。

## 如何构建 Docker 镜像？

*我们可以使用Dockerfile*从头开始构建图像。那么，什么是 Dockerfile？

### ***文件***

Dockerfile 是一个简单的文本文件，其中包含***带有*** 构建映像的指令序列的脚本。此外，指令或命令序列在某种程度上与 Linux 命令相同。Docker 在 Docker 环境中自动执行这些命令并构建镜像。

### ***如何创建 Docker 文件？***

以下步骤将帮助您从头开始编写 Dockerfile。

***创建一个 Dockerfile***

1.  首先，使用以下命令创建一个新目录“ ***myDockerdir ”***

```
mkdir myDockerdir
```

1.  其次，进入“ ***myDockerdir*** ”目录。

```
cd myDockerdir
```

1.  然后创建一个名为“ ***Dockerfile*** ”的文件。

```
touch Dockerfile
```

1.  *现在，使用“ vi* ”等任何编辑器打开此文件。

```
vi Dockerfile
```

1.  接下来，编辑文件“ *Dockerfile* ”并在此文件中插入以下代码。

```
FROM ubuntu
MAINTAINER shilps
RUN apt-get update
CMD ["echo", "Hello World, welcome to Docker"]
```

*上述代码中使用的命令解释如下：*

1.  ***FROM** => 这是我们创建的图像的基础。通常，我们从父镜像（如上例中的 ubuntu）或基础镜像（从头开始）开始。*
2.  ***MAINTAINER** => 在这里，我们指定图像的作者。*
3.  ***RUN** => RUN 指令在其上层构建图像时执行命令。而且，Dockerfile中可以有多个RUN指令。*
4.  ***CMD** => 一个 Dockerfile 只能有一个 CMD 指令。因此，它为执行容器提供默认值。此外，我们还可以通过CMD指令设置一个默认命令，如果容器运行时没有指定命令，我们就可以执行它。*

将上述代码保存到*Dockerfile 后*，退出编辑器。

我们刚刚生成的 Dockerfile 如下所示。![5-Dockerfile.png](https://www.toolsqa.com/gallery/Docker/5-Dockerfile.png)

现在让我们按照一系列步骤从上面编写的*Dockerfile构建**Docker 镜像*。

### ***Docker构建命令***

使用*Dockerfile*构建*镜像*的命令是“ docker ***build*** ”。

*此外， docker build*命令的一般语法是：

```
docker build [OPTIONS] PATH | URL | -
```

通常，我们使用以下命令来构建 Docker 镜像。

```
docker build [location of Dockerfile]
```

如果我们仍然在与*Dockerfile*相同的目录中，我们可以简单地说

```
docker build .
```

现在要使用上面的*Dockerfile构建**图像*，我们按照以下步骤操作。

-   *首先，切换到目录“ **myDockerdir** ”。这是我们拥有 Dockerfile 的目录。*
-   *一旦我们进入“ **myDockerdir** ”，执行以下命令。*

```
docker build .
```

请注意，在此命令中，我们提供了*当前目录*作为参数。这是因为 Docker 会自动搜索名为“ *Dockerfile* ”的文件并执行它。

此外，上述命令的输出如下：![6-Docker构建命令.png](https://www.toolsqa.com/gallery/Docker/6-Docker%20Build%20Command.png)

上面的命令显示了“ ***docker build*** ”命令的输出。

-   *为了验证图像的创建，我们可以使用以下命令列出可用的图像。*

```
docker images
```

上面的命令给出了以下输出。![7-如何列出所有的docker images?.png](https://www.toolsqa.com/gallery/Docker/7-How%20to%20list%20all%20the%20docker%20images.png)

请注意上面屏幕截图中的图像细节。生成的图像没有标签和名称。这是因为我们没有指定图像的名称。

-   *如果我们必须为图像提供适当的名称，我们可以使用“ **-t** ”**标志**后跟图像名称来指定它，如以下命令所示。*

```
docker build -t first_docker_image .
```

在上面的命令中，我们提供了将生成的图像的名称。命令的输出如下所示：![8-如何标记docker镜像?.png](https://www.toolsqa.com/gallery/Docker/8-How%20to%20tag%20a%20docker%20image.png)

请注意，现在 docker build 输出也显示了图像名称。

-   *为了验证图像生成，让我们使用“ **docker images** ”命令列出图像。*

它生成以下输出。![9-Docker镜像列表.png](https://www.toolsqa.com/gallery/Docker/9-Docker%20Image%20list.png)

-   *现在镜像创建成功了，我们必须启动一个新的 Docker 容器来执行镜像。我们使用“ **docker run** ”命令来实现这一点。*

```
docker run --name test_container first_docker_image
```

在此命令中，我们提供一个名为“ ***test_container*** ”的容器，并将我们刚刚创建的图像与其相关联。例如，“ ***docker run*** ”命令输出如下：![10-运行命令.png](https://www.toolsqa.com/gallery/Docker/10-run%20command.png)

从上面的截图可以看出，当容器执行时，会显示我们在*Dockerfile*中的 CMD 指令中提供的消息。

这样，我们就成功构建并执行了一个简单的 Docker 镜像。有关“ ***docker build*** ”命令的更多详细信息，请参阅[***docker build 命令***](https://docs.docker.com/engine/reference/commandline/build/)。

## 如何保存 Docker 镜像？

一旦我们构建了一个*图像*，我们可能希望在未来使用它。因此我们需要将它保存在主机上。那么我们如何实现呢？

为此，我们“***保存***”它。Docker 提供了一个“ ***docker save*** ”命令来保存镜像。

此外，让我们现在讨论这个命令。

### ***Docker 保存命令***

命令“ ***docker save*** ”将一个或多个图像保存到存档中，通常是“ *tar* ”文件。

*下面给出了“ docker save* ”命令的一般语法。

```
docker save [OPTIONS] IMAGE [IMAGE...]
```

此命令生成标准输出流的压缩存储库。tarred 存储库包含所有版本、标签或为提供的每个参数指定的“ ***repo: tag*** ”。

该命令具有以下选项，我们可以使用该命令指定这些选项。

-   ***-- output, -o**：这意味着我们可以写入文件而不是 STDOUT。*

让我们看一个 docker save 命令用法的例子。以下屏幕截图显示了此命令的工作原理。![11-保存命令.png](https://www.toolsqa.com/gallery/Docker/11-Save%20Command.png)

我们可以从上面的截图中看到，首先，我们列出了当前的图像。主机上有两个。让我们将第一个图像“ *dockerimage2 (first rectangle)* ”保存到一个 tar 文件中。

为此，我们使用了“ ***docker save*** ”命令（*显示在第二个矩形中*）。当我们执行此命令然后列出存在的文件时，我们可以看到“ *docker save* ”命令生成的 tar 文件。

## 如何拉取 Docker 镜像？

大多数时候，它们将使用现有图像创建。*然而， Docker Hub*上有成千上万的图像，我们可以使用它们来创建我们的图像。为此，我们需要从*Docker Hub*拉取这些镜像。

Docker 提供了一个命令“ *docker pull* ”，允许用户从*Docker Hub*等 docker 注册表中拉取所需的图像。

随后，让我们就此进行更多讨论。

*“ docker pull* ”命令的一般语法是：

```
docker pull [OPTIONS] NAME[:TAG|@DIGEST]
```

该命令支持以下选项

| 选项           | 默认 | 描述                              |
| -------------- | ---- | --------------------------------- |
| --所有标签，-a |      | 此选项用于拉取/下载所有标记的图像 |
| --禁用内容信任 | 真的 | 跳过图像验证                      |
| --安静，-q     |      | 抑制详细输出                      |
| - 平台         |      | 如果服务器支持多平台，则设置平台  |

现在让我们尝试从存储库中拉取图像作为示例。此外，请考虑以下屏幕截图。![12-拉图.png](https://www.toolsqa.com/gallery/Docker/12-Pull%20Image.png)

在这里我们指定了图像名称“ *ubuntu* ”以及摘要“ *18.04* ”，因为我们想要拉出 ubuntu 18.04 的固定版本。

而且，上面的截图在我们列出镜像的时候也显示了刚刚拉取的镜像。

## 流行的 Docker 命令

除了主要的命令/操作，我们已经在上面讨论了 Docker 镜像；有更多流行的 Docker 命令与之相关。随后，这些显示在下面的列表中。

| ***码头工人命令*** | ***句法***            | ***描述***                                              |
| ------------------ | --------------------- | ------------------------------------------------------- |
| 删除图片           | *docker rmi ImageID*  | 删除由 ImageID 指定的图像。                             |
| 码头工人检查       | *码头工人检查存储库*  | 此命令查看由“*存储库*”指定的图像或容器的详细信息。      |
| 码头历史           | *docker 历史 ImageID* | 允许查看通过容器使用 ImageID 给出的图像运行的所有命令。 |
| 载入图片           | *码头图像加载*        | 从 tar 存档或流中加载图像以接收或读取输入 ( *STDIN* )。 |
| 修剪图像           | *码头图像修剪*        | 此命令删除未使用的图像。                                |
| 推送图片           | *码头图像推送*        | 将图像或存储库推送到注册表中。                          |

此外，要更详细地了解这些命令，请参阅[***Docker 命令***](https://docs.docker.com/engine/reference/commandline/docker/)。

## Docker 镜像与容器

下表区分了 Docker 镜像和容器。

| ***码头工人形象***                                     | ***Docker 容器***                                            |
| ------------------------------------------------------ | ------------------------------------------------------------ |
| 它是容器的蓝图。                                       | 它是 Docker 镜像的一个实例。                                 |
| 图像是一个逻辑实体，而不是物理实体。                   | 容器是真实世界的物理实体。                                   |
| 我们只创建一次图像。                                   | 我们使用 Docker 镜像创建容器，我们可以创建任意次数。         |
| 它本质上是不可变的。                                   | 当删除旧图像或使用新图像构建容器时，容器会发生变化。         |
| 由于图像不会自行运行，因此它们不需要计算资源即可工作。 | 容器作为 Docker 虚拟机运行。因此，它们需要计算资源才能运行。 |
| 我们使用用 Dockerfile 编写的脚本来创建它。             | 我们通过运行“ *docker build* ”命令来创建容器。               |
| 它打包了应用程序和预配置的服务器环境。                 | 容器使用镜像提供的服务器信息和文件系统运行。                 |
| 图像在 Docker Hub 上共享。                             | 由于容器是一个运行的实体，它不会被共享。                     |
| 它们没有“*运行*”状态。                                 | 容器处于运行状态。                                           |

## 关键要点

-   *Docker 镜像充当特定系统的模板或蓝图。*
-   *此外，它可能包含捆绑在一起的应用程序、代码和其他运行时环境。*
-   *而且，它是一个只读的实体，我们需要将它关联到一个容器中才能运行它。*
-   *此外，我们可以同时将一个图像与多个容器相关联。*
-   *此外，我们可以使用各种 docker 镜像命令构建、保存、归档等。*

在学习了*Docker 镜像*的基础知识之后，我们将在下一篇文章中转向*Docker 容器*。