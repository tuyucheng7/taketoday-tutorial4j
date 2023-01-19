docker 中的[***容器注册表是我们可以找到***](https://docs.docker.com/registry/)[***docker 镜像***](https://www.toolsqa.com/docker/docker-images/)、管理 docker 镜像、执行漏洞分析和决定访问控制的唯一地方。此外，使用 CI/CD，我们可以拥有完全自动化的 Docker 管道，以获得更快的反馈。在本文中，我们将讨论 Docker 中的容器注册表，主题如下：

-   Docker镜像存储和分发
    -   *什么是容器注册表？*
    -   *为什么要使用容器/Docker 注册表？*
    -   *如何部署 Docker Registry 服务器？*
-   Docker Hub 容器注册表
    -   *来自 AWS、Azure 和 Google 的其他公共容器注册表*
-   *Docker 存储库与 Docker 注册表*
-   *公共与私有容器注册中心*

## Docker 镜像存储和分发

在使用[***Docker***](https://www.toolsqa.com/docker/introduction-to-docker-and-docker-architecture/)时，我们创建一个应用程序或服务，然后继续将其打包到一个容器映像中，包括它的所有依赖项和/或配置。此外，我们可以将图像视为应用程序/服务、配置和依赖项的静态表示。最后，镜像需要实例化以创建一个在 Docker 主机上运行的容器来运行这个应用程序/服务。

可能有很多图像不容易处理，因此我们应该能够将这些图像存储在某个地方，最好是在注册表中。此外，这个注册表充当图像库，当我们将图像部署到生产环境时需要它。[***Docker 通过Docker Hub***](https://hub.docker.com/)维护一个官方图像库，这是一个公共注册表。此外，在组织级别和许多不同的公共注册表中也可以有许多私有注册表。

下图显示了这种安排：![容器注册和镜像分发](https://www.toolsqa.com/gallery/Docker/1-Container%20registry%20and%20image%20distribution.png)

如上所示，图像显示了 Docker 注册表分布。docker[***容器***](https://www.toolsqa.com/docker/docker-containers/)是图像的运行实例，它从注册表访问图像。该注册表可以在内部（*在组织级别私有*）或在公共云上。将 Docker 镜像放在注册表中，让我们可以存储静态和不可变的应用程序版本，这些版本将包括框架级别的依赖项和配置。这些图像是版本化的，可以将它们作为部署单元部署在多个环境中。

我们可以将注册表视为一个书架，其中存储了图像数量，并且可以拉取以构建可以运行服务、Web 应用程序等的容器。

现在让我们进入容器注册表的细节。

### ***什么是容器注册表？***

Docker 注册中心，或者我们通常所知道的容器注册中心，是一个用于[***Docker 镜像***](https://www.toolsqa.com/docker/docker-images/)的系统。容器注册表处理 Docker 镜像的存储和分发。例如，每个图像可能有不同的版本，并且每个图像都由一个标签标识。

Docker/容器注册表可以进一步组织成***Docker 存储库***。Docker 存储库包含/包含特定图像的所有版本。Docker registry 允许我们根据需要拉取镜像，并在适当的权限下将新镜像推送到其中。

正如我们在之前关于 Docker 的教程中多次看到的那样，默认情况下，Docker Engine 与 DockerHub 交互，DockerHub 是 Docker 的公共注册表，如前所述。我们还可以运行一个内部部署的 Docker 注册中心/发行版，它是开源的并且可以在市场上买到，叫做***Docker Trusted Registry***。

如果我们想从*DockerHub*中拉取名为*myimage/Hello*的镜像，我们可以执行以下命令：

```
docker pull myimage/Hello
```

它将从*DockerHub*本地拉取上述图像的最新版本。

要从本地注册中心拉取相同的镜像，我们需要运行一条命令，指定注册中心的位置、端口、名称和镜像的标签等，如下面的命令所示。

```
docker pull my-docregistry:9000/myimage/Hello:1.1
```

我们已经指定我们需要从端口 9000的*my-docregistry*中拉取标记为 1.1的*myimage/Hello镜像版本。*

所以现在我们可以看到我们从公共和私有注册表中提取镜像的方式之间的区别。

当出现以下情况时，***我们建议使用私有注册***中心（*本地或云）：*

1.  *Docker 镜像是机密的，我们不应该公开分享它们。*
2.  *我们希望图像与其选择的部署环境之间的网络延迟最小。例如，假设我们的特定应用程序的生产环境是本地的，以最大限度地减少网络延迟。在这种情况下，我们可能会选择在与应用程序相同的本地网络中拥有一个内部部署的 Docker Trusted Registry。*

在上述情况下，选择私有 Docker 注册表是有利的。

### ***为什么使用容器/Docker 注册表？***

现在问题来了：Docker registry 的目的是什么，或者我们为什么要使用 Docker registry？

我们可以用以下几点来证明自己。

-   *使用 Docker 注册表，我们可以严格控制可以存储图像的位置。*
-   *我们通过 Docker 注册表完全拥有我们的图像分发管道。*
-   *借助容器注册表，我们可以将图像存储和分发紧密集成到内部开发工作流程中。*

### ***如何部署 Docker Registry 服务器？***

“ ***registry ”是“*** ***registry*** ”镜像的一个实例，它在 Docker 中运行。因此，在部署注册表之前，我们需要在主机上安装 Docker。让我们按照步骤顺序来部署和配置注册表服务器。

#### ***运行本地注册表***

使用以下命令启动注册表容器。

```
& docker run -d -p 5000 : 5000 - -restart=always  - -name registry registry : 2
```

注册表容器现在可以使用了。

#### ***将镜像从 Docker Hub 复制到您的注册表***

接下来，我们从 Docker Hub 中拉取镜像。例如，让我们使用以下命令从 DockerHub 中拉取版本为“ ***16.04 ”的映像“*** ***ubuntu ”。***

```
$ docker pull ubuntu:16.04
```

然后我们将其重新标记为“ ***my-ubuntu-image*** ”，然后将其推送到本地注册表容器。为此，我们使用以下命令。

```
$ docker tag ubuntu:16.04 localhost:5000/my-ubuntu-image
```

以下屏幕截图显示/显示了上述命令的执行。![部署注册表容器](https://www.toolsqa.com/gallery/Docker/2-Deploy%20registry%20container.png)

作为最后一步，我们删除本地主机上的“ ***ubuntu:16:04*** ”和“ ***my-ubuntu-image*** ”，本地注册表拉取“ ***my-ubuntu-image*** ”。命令序列如下：

```
$ docker image remove ubuntu:16.04 
$ docker image remove localhost:5000/my-ubuntu
$ docker pull localhost:5000/my-ubuntu
```

下面的屏幕截图显示了上述命令的执行。![在本地拉取注册表](https://www.toolsqa.com/gallery/Docker/3-pull%20registry%20locally.png)

#### ***停止本地 docker registry - docker container stop registry 命令***

我们可以使用“ ***docker container stop*** ”命令停止本地注册表，如下所示：

```
& docker container stop registry
```

然后使用“ *docker container rm* ”删除容器：

```
& docker container stop registry && docker container rm -v registry
```

***基本配置***

现在我们继续容器的基本配置。例如，如果我们需要注册表成为永久基础设施的一部分，我们可以将其设置为在 Docker 重启或退出时自动重启。在下面的命令中，我们为此使用了“ ***--restart always*** ”标志。其命令如下。

```
& docker run  -d  -p 5000 : 5000  - -restart= always  - -name registry registry : 2
The screenshot below shows the execution of this command.
```

![配置容器注册表服务器](https://www.toolsqa.com/gallery/Docker/4-Configure%20container%20registry%20server.png)

如上图所示，我们可以通过查看容器的状态来验证registry容器是否已经启动。

#### ***在 Docker 注册表中自定义发布的端口***

我们还可以自定义用于发布的端口。如果端口 5000 已被占用，或者我们想在不同的端口上运行多个本地注册表，我们可以自定义发布的端口——例如，以下命令。

```
$ docker run -d -p 5001:5000 --name registry-test registry:2
```

在这里，我们在端口 5001 上运行名为“ ***registry-test ”的注册表。请注意选项“*** ***-p*** ”，它指定主机端口，第二部分（*在 : 之后*）是容器内的端口。这意味着在容器内，registry 默认监听 5000 端口。

我们也可以使用环境变量“ ***REGISTRY_HTTP_ADDR*** ”来改变registry监听的端口，如下图：

```
& docker run -d -e REGISTRY_HTTP_ADDR=0.0.0.0 : 5001 -P 5001 : 5001 - -name registry - test registry :
&  docker  run  -d  -e  REGISTRY_HTTP_ADDR=0.0.0.0 : 5001 -p 5001 : 5001 - -name registry - test registry :
```

这样，我们就可以在 Docker 主机上部署注册表容器。

## Docker Hub 容器注册表

[***DockerHub***](https://hub.docker.com/)是 Docker Inc. 的官方托管注册表解决方案。Docker Hub 提供公共和私有存储库和自动构建，与 GitHub 和 Bitbucket 等源代码控制解决方案以及组织帐户集成。

任何运行 Docker 的人都可以使用 Docker API 访问公共存储库。例如，“ ***docker pull ubuntu*** ”将拉取带有“ ***latest*** ”标签的“ *ubuntu* ”镜像。另一方面，私有存储库限制对存储库创建者的访问或仅对组织成员的访问。DockerHub 还支持官方存储库。其中包括经过安全验证并使用最佳实践的“ ***Nginx ”等图像。***

我们可以将 DockerHub 存储库链接到包含构建上下文的源代码控制存储库，并执行自动映像构建。在源存储库中触发的提交将反过来触发 DockerHub 中的构建。

DockerHub 的另一个特点是它会自动扫描私有存储库中的图像以查找任何漏洞并生成详细的漏洞报告。

### ***来自 AWS、Azure 和 Google 的其他公共容器注册表***

除了 DockerHub 之外，其他公司还托管在线 Docker 注册中心供公众使用。这些通常是有偿的。包括亚马逊网络服务 ( *AWS* ) 和谷歌在内的云提供商也提供容器托管服务：

-   [***Amazon Elastic Container Registry (ECR)***](https://aws.amazon.com/ecr/)：*ECR 仅支持私有存储库。它不提供自动映像构建。ECR 与 AWS Identity and Access Management (IAM) 集成以进行身份验证。*
-   [***Google Container Registry (GCR)***](https://cloud.google.com/container-registry/)：*GCR 使用 Google Cloud Storage 服务权限进行身份验证。与 ECR 一样，GCR 也仅支持私有存储库。但除此之外，它还通过与谷歌云源存储库和源代码控制（如 GitHub 和 Bitbucket）集成来提供自动化图像构建。*
-   [***Azure Container Registry (ACR)***](https://azure.microsoft.com/en-us/services/container-registry/)：*Microsoft 的此注册表支持多区域注册表并使用 Active Directory 进行身份验证。它不提供自动镜像构建，仅支持私有存储库。*
-   [***CoreOS Quay***](https://quay.io/)：*该注册表提供免费和付费（公共和私人）存储库。CoreOs Quay 使用 LDAP 和 OAuth 身份验证。此外，此容器注册表还提供自动安全扫描自动映像构建。*
-   [***私有 Docker 注册表***](https://private-docker-registry.com/)：*私有 Docker 容器注册表提供公共和私有存储库（最多 3 个免费存储库）并支持 OAuth、LDAP 和 Active Directory 身份验证。*

## Docker 存储库与 Docker 注册表

以下是两者之间的一些差异：

| ***码头工人登记处***                                         | ***码头工人仓库***                                           |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| 它是一种存储不同 Docker 镜像的服务。                         | 它是具有相同名称但不同标签的各种 Docker 镜像的集合。         |
| 第三方或任何其他公共或私有注册表（如 DockerHub、GCR 等）都可以托管它。 | 它是 Docker 注册表的一部分。                                 |
| Docker 注册表的示例有 DockerHub、私有 Docker 注册表、GCR 等。 | Docker 存储库的示例有 Ubuntu、Nginx 等，其中每个存储库都是各种图像的集合，例如不同版本的 Ubuntu。 |

## 公共与私有容器注册中心

| ***公共容器注册表***                                         | ***私有容器注册表***                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| 它本质上是公开的，任何人都可以访问它。                       | 本质上大多是私有的，需要访问权限。                           |
| 公共登记处提供基本且易于使用的服务。它适用于个人和较小的团队。 | 私有容器注册表具有扫描功能和基于角色的访问控制，从而提供更多的安全性、治理和高效的管理。 |
| 它遇到了问题，尤其是随着团队/应用程序的扩展。                | 私有 Docker 注册表为大型集群和高频推出提供更好/更高效的性能，以及访问身份验证等附加功能。 |
| 大部分免费使用。                                             | 通常可以通过付费访问来托管它。                               |
| 可供公众使用，例如 DockerHub。                               | 它主要由特定组织的本地主机使用。                             |

## 关键要点

-   *容器注册表是 Docker 映像的集合，可以是公共的也可以是私有的。*
-   *此外，默认情况下，DockerHub 是 Docker Inc. 提供的容器注册表，任何在机器上安装了 Docker 的人都可以访问它。*
-   *此外，我们可以在本地部署我们的注册表容器，这些步骤我们已经在上面讨论过了。*
-   *除了上述之外，存储库是具有相同名称和不同标签的图像的集合。此外，可以将存储库推送到注册表中。*

最后，通过本文，我们完成了关于 Docker 的系列文章。快乐学习！！