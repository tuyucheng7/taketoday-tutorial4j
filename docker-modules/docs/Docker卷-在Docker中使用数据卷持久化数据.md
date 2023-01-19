卷存储 Docker 生成的数据。使用***Docker Volume ，我们可以在***[***Docker 容器***](https://www.toolsqa.com/docker/docker-containers/)中实现数据持久化。我们可以使用 Docker Volumes 在容器和容器版本之间共享目录。此外，我们可以在不丢失数据的情况下升级容器、重启机器或共享数据。因此，让我们在本文中讨论更多的*数据持久性和 Docker 卷*。作为本教程的一部分，我们将介绍以下主题。

-   如何在 Docker 中管理数据？
    -   *卷与 Docker 中的绑定挂载*
    -   *如何在 Docker 中使用 Mount？*
    -   *如何使用数据卷？*
-   *-v 或 -mount 标志？*
-   *创建和管理 Docker 数据卷。*
-   *如何通过 docker-compose 使用数据卷？*
-   *什么是只读数据卷？*
-   *使用 Docker 数据卷启动容器/服务*
-   *如何使用容器填充 Docker 数据卷？*
-   什么是音量驱动程序？
    -   *初始设置 - docker plugin install 命令*
    -   *使用卷驱动程序创建数据卷 - docker volume create 命令*
    -   *启动使用卷驱动程序创建卷的容器*
-   在 Docker 中备份、恢复、迁移或删除数据卷
    -   *备份一个容器*
    -   *从备份中恢复容器*
    -   *删除泊坞窗卷*

## 如何在 Docker 中管理数据？

我们知道[***Docker***](https://www.toolsqa.com/docker/introduction-to-docker-and-docker-architecture/)有一个分层存储实现，有助于提高可移植性、效率和更快的性能。这种存储实现也是跨各种环境检索、存储和传输图像的最佳选择。当我们删除一个 Docker 容器时，所有关联或写入容器的数据都会随之删除。因此，即使容器被删除，也需要以某种方式持久化容器数据，以便我们不必担心数据并在容器不复存在后持久化这些数据。

因此需要在容器中持久化数据。最佳推荐做法之一是将数据与容器隔离，以保留容器化的[***优势***](https://www.toolsqa.com/docker/understanding-containerization-and-virtualization/)。在这里，我们将数据管理与容器生命周期明显分开。我们可以实施多种策略来持久化数据或将持久性添加到我们接下来要讨论的容器中。这些策略如下图所示。![数据量、挂载](https://www.toolsqa.com/gallery/Docker/1-Data%20volume,%20mounts_0.png)

如上所示，Docker 提供了两种数据持久化选项，以便即使在容器停止后文件也能持久化。

-   *卷*
-   *坐骑*

让我们了解这两者之间的区别：

### ***卷与 Docker 中的绑定挂载***

1.  ***卷：卷是******联合文件系统***之外的目录或文件（*只读层与容器顶部的读写层的组合*）。卷作为普通文件和目录存在/存储在主机文件系统上。因此，为了在容器之间持久化和共享数据，Docker 使用了卷。卷是将数据保存在 Docker 容器中的最佳选择。Docker 管理卷并存储在主机文件系统的一部分（ */var/lib/docker/volumes/ 在 Linux 上*）。

***注意**：不属于 Docker 的进程（非 Docker 进程）不应修改文件系统的这一部分。*

1.  ***挂载***：在 Docker 中，我们可以使用以下挂载。

-   ***绑定挂载**：存储在容器主机文件系统上任何位置并挂载到正在运行的容器中的文件或文件夹称为绑定挂载。它们可以是从基本系统文件到目录的任何内容。有趣的是，Docker 主机上存在的 Docker 容器或非 Docker 进程可以随时修改这些挂载。*
-   ***tmpfs 挂载**主要由在 Linux 系统上运行的 Docker 使用。它们的存储仅在主机系统的内存中。此外，我们从不将 tmpfs 挂载写入主机系统的文件系统。与卷和绑定挂载相反，“ ***tmpfs*** ”挂载是临时的，只保留在主机内存中。当容器停止时，“ ***tmpfs*** ” mount 将被移除，并且写入那里的文件将不会保留。*

***注意**：如果 Docker 在 Windows 上运行，我们可以使用**命名管道**。*

在使用[***Docker 容器***](https://www.toolsqa.com/docker/docker-containers/)*时， Docker 卷*是用于数据持久化的广泛使用和有用的工具。编译额外的可写层会增加图像大小，因此卷是更好的选择。其次，卷独立于容器生命周期并存储在主机上。正因为如此，我们可以轻松地在一个或多个容器之间备份数据和共享文件系统。

现在，请记住这些方法，因为稍后将在帖子中分享详细信息。

### ***如何在 Docker 中使用 Mount？***

当我们使用绑定挂载时，我们可以控制确切的***挂载点***，即当前可访问文件系统中的一个目录（*通常是一个空*目录），我们希望主机系统在该目录上***挂载*** （*即逻辑附加）其他文件系统。*除了使用绑定挂载来持久化数据外，它还经常向容器提供额外的数据。使用绑定挂载将源代码挂载到容器中可以让我们立即看到代码更改并做出响应。

*现在的问题是我们应该使用哪个支架以及何时使用？*

虽然我们通常应该尽可能使用卷，但绑定挂载在以下用例中是合适的：

-   *当我们需要从主机共享配置文件到容器时。例如，为了向容器提供 DNS 解析，Docker 将“ **/etc/resolv.conf** ”从主机挂载到每个容器中。*
-   *当我们想要在主机上的开发环境和容器之间共享构建工件或源代码时。例如，我们可以将 Maven 的“target/”目录挂载到容器中。因此，每次我们在主机上构建 Maven 项目时，容器都会访问更新后的工件。*
-   *当Docker宿主机的目录或文件结构与容器所需的bind mounts一致时，我们也可以使用bind mounts。*

所以上面讨论的是我们发现绑定挂载有用的一些情况。

另一方面，当我们不想在主机和容器上持久化数据时，我们可以使用 tmpfs 挂载。它主要是出于安全原因并保护容器性能，尤其是当应用程序必须写入大量非持久数据时。

从上面的讨论可以明显看出，我们很少使用*tmpfs*挂载。尽管如此，在使用绑定挂载和卷时，我们应该牢记一些提示。

-   *当我们将一个空卷挂载到容器内的目录中时，容器内的文件和/或目录会传播到该卷中。*
-   *当我们启动容器并指定一个不存在的卷时，Docker 会创建一个空卷。*
-   *同样，当我们挂载非空卷或绑定挂载到容器内已经有一些文件和/或目录的目录时，这些文件和/或目录会被挂载遮挡，并且在卷或绑定挂载时无法访问已安装。*

在本文的其余部分，我们将详细讨论卷。

### ***如何使用数据卷？***

*卷是Docker 容器*中数据持久化的流行和首选机制。与依赖于主机操作系统和目录结构的绑定挂载不同，卷完全由 Docker 管理。卷相对于绑定挂载的一些优点如下：

-   *我们使用 Docker API 命令管理卷。*
-   *Linux 和 Windows 都支持卷。*
-   *与绑定安装相比，备份或迁移卷更容易。*
-   *我们可以在多个容器之间快速安全地共享卷。*
-   *使用卷驱动程序，我们还可以将卷存储在远程主机或云提供商上。我们还可以加密卷的内容或添加任何其他功能。*
-   *容器可以预填充新卷。*
-   *Docker Desktop 上的卷比从 Windows 或 Mac 主机绑定挂载更有效。*

***卷不会增加使用它们的容器的大小***，而且卷的内容存在于该容器的生命周期之外。因此，卷是持久化数据的流行选择。下图显示了正在运行的卷和安装。![Docker 中卷和挂载的生命周期](https://www.toolsqa.com/gallery/Docker/2-Lifecycle%20of%20Volume%20and%20Mounts%20in%20Docker_0.png)

所以如果容器要存储临时的非持久化数据，理想情况下我们应该考虑使用tmpfs挂载。对于永久存储数据或将其写入容器的可写层等其他事情，我们选择卷或绑定挂载。

## -v 或 -mount 标志？

就卷而言，它们使用***私有***绑定传播，我们无法为卷配置绑定传播。

至于选项*--mount 或 --v，--mount*更加冗长和明确。*--v 和 --mount*之间的主要区别在于--v 语法将所有选项组合在一个字段中，而 --mount 语法将它们分开。

为了指定卷驱动程序选项，我们应该使用 --mount。

1.  ***-v 或 --volume***：此选项由三个字段组成，由冒号 ( *:)*分隔。我们必须确保字段的顺序正确。字段说明如下。

-   *对于匿名卷，第一个字段被省略。在命名卷的情况下，第一个字段包含卷的名称。该名称对于给定的主机是唯一的。*
-   *第二个字段包含容器中安装的文件或目录的位置路径。*
-   *第三个字段是可选的。它包含以逗号分隔的选项列表，例如 ro。*

1.  ***--mount***：此选项包含多个键值对，以逗号 (,) 分隔，每个键值对由 ( *<key>=<value>* ) 元组组成。请注意，“--mount”的语法*比“* *--v* ”或“ *--volume* ”更冗长。

-   *挂载的**类型**（类型）：这可以是**bind**、 **volume**或 **tmpfs**。*
-   ***挂载源**(source/src)：对于 命名卷，此字段包含卷的名称。对于匿名卷，我们省略了这个字段。*
-   ***目的地**（destination/dst/target）：该字段的值是挂载目录或文件的路径。*
-   ***只读** 选项：如果存在，此字段使绑定挂载以只读方式挂载。*
-   ***volume-opt**选项：该 字段由具有选项名称和值的键值对组成。它可以指定多次。*

现在让我们用表格列出 --v 和 --mount 选项之间的主要区别。

| ***财产***             | ***命名卷***            | ***绑定坐骑***                |
| ---------------------- | ----------------------- | ----------------------------- |
| 主机位置               | 被 Docker 选择          | 用户控制                      |
| 挂载示例（*使用 -v*）  | 我的卷：/usr/local/data | /路径/到/数据：/usr/本地/数据 |
| 使用容器内容预填充新卷 | 是的                    | 不                            |
| 卷驱动程序支持         | 是的                    | 不                            |

***注意**：与绑定挂载相反，卷的所有选项都存在于 --mount 以及 -v 标志中。当我们将卷与服务一起使用时，仅支持 --mount。*

在下一节中，让我们了解*与卷相关的 Docker 命令*。

## 创建和管理 Docker 数据卷

可以在任何容器范围之外创建和管理卷。要创建 docker volume，请在控制台上使用“ *docker volume create* ”命令。命令如下。

```
$ docker volume create my-vol
```

我们可以使用以下命令列出现有卷。

```
$ docker volume ls
```

给定一个卷，我们可以使用“ ***docker volume inspect 命令***”检索它的详细信息。

```
$ docker volume inspect volume_name
```

以下屏幕截图显示了上述命令的工作情况。![docker 卷创建命令](https://www.toolsqa.com/gallery/Docker/3-docker%20volume%20create%20command.png)

如上面的屏幕截图所示，我们使用docker volume create 命令创建了一个新卷“ ***my_docker_volume ”：***

```
docker volume create my_docker_volume
```

下一个命令“ ***docker volume ls*** ”列出了刚刚创建的卷。接下来，我们指定命令：

```
docker volume inspect my_docker_volume
```

此命令提供卷的详细信息，如上面的屏幕截图所示。

同样，我们可以使用以下命令删除卷。

```
$ docker volume rm Volume_name
```

因此要删除上面创建的卷，我们可以指定命令，

```
docker volume mu_docker_volume
```

命令的结果如下图所示。![卷 rm 命令](https://www.toolsqa.com/gallery/Docker/4-volume%20rm%20command.png)

为了验证我们确实删除了卷，我们可以给出命令“ ***docker volume ls*** ”。

## 如何使用 docker-compose 的数据量？

[***我们还可以使用Docker 组合***](https://www.toolsqa.com/docker/docker-compose/)服务创建卷或指定现有卷。例如，以下屏幕截图显示了一个“ *docker-compose* ”文件，该文件创建了一个带有卷的 docker-compose 服务。![带有卷的 Docker 组合服务](https://www.toolsqa.com/gallery/Docker/5-Docker%20Compose%20Service%20with%20a%20Volume.png)

命令如下：

```
docker-compose up
```

产生的输出：![将数据卷与 Docker Compose 结合使用](https://www.toolsqa.com/gallery/Docker/6-Using%20Dat%20Volume%20with%20Docker%20Compose.png)

作为上述命令的结果，一个名为“ *myvolume* ”的卷被创建。*“ docker-compose up* ”的后续调用将重用此卷。

或者，我们也可以在[***Docker Compose***](https://www.toolsqa.com/docker/docker-compose/)之外创建一个卷，然后在“ *docker-compose.yaml* ”文件中引用它，如下例所示。

```
version: "3.3" 
services: 
frontend: 
image: node:lts 
volumes: 
- myvolume:/home/node/app 
volumes: 
myvolume: 
external: true
```

在上面的 docker-compose 文件中，我们使用了名为“ *myvolume* ”的卷。请注意，指定了标志“ *external* ”并将其设置为 true ，表示已在 docker-compose 之外创建了卷。

## 什么是只读数据卷？

在大多数情况下，容器只需要出于读取目的访问数据。此外，某些文件夹可能会发生很大变化，并且为每次执行创建图像可能很困难且成本很高。在这种情况下，我们选择只读卷。

要将卷指定为只读，我们将“ *ro* ”附加到 -v 开关，如下所示：

```
docker run -v volume-name:/path/in/container:ro my/image
```

我们还可以使用带有“ *readonly ”选项的“* ***mount*** ”开关，如下所示。

```
$ docker run --mount source=volume-name,destination=/path/in/container,readonly my/image
```

对于应用程序中使用的动态数据，我们使用只读卷。

## 使用 Docker 数据卷启动容器/服务

当我们使用不存在的卷启动容器时，Docker 会创建一个具有指定名称的新卷。以下示例显示了这一点。

在这里，我们给出以下命令。

```
docker run -d --name volumetest -v my_docker_volume:/app ubuntu:latest
```

如上述命令所示，新容器“ ***volumetest*** ”以指定的卷名称“ ***my_docker_volume*** ”启动。此卷不存在。执行上述命令后，我们将获得容器 volumetest 的 ID。为了验证我们确实创建了卷，我们可以给出检查容器命令如下：

```
docker inspect volumetest
```

执行此命令时，我们可以直接跳转到检查输出的“ ***Mounts*** ”部分，如下面的屏幕截图所示。![安装部分](https://www.toolsqa.com/gallery/Docker/7-Mount%20Section.png)

如上面的屏幕截图所示，我们有创建的卷的详细信息。volume' 并且此挂载的读写选项为真。然后我们可以停止容器并使用以下命令序列删除卷。

```
$ docker container stop volumetest

$ docker container rm volumetest

$ docker volume rm my_docker_volume
```

当服务启动并且我们定义了一个卷时，每个服务容器将使用其本地卷。因此，如果我们使用本地卷驱动程序，则没有容器会共享数据。但也有一些卷驱动程序支持共享存储的例外情况。例如，适用于 AWS 和 Azure 的 Docker 使用 Cloutstor 插件来支持共享持久存储。

使用本地卷启动服务类似于启动容器。不同之处在于，在“ docker *service create* ”命令中，不支持“ *-v* ”或“ *--volume ”标志。*相反，我们必须使用“ *--mount* ”标志来安装卷。

## 如何使用容器填充 Docker 数据卷？

当我们启动一个创建新卷的容器时，如果容器里面有文件和目录，那么这个目录的内容就会被复制到卷中。因此，安装此卷的容器和使用此卷的其他容器将可以访问预填充的内容。

例如，我们给出以下命令来启动 ubuntu 容器并使用“ ***/usr/share/ubuntu/html*** ”目录的内容填充新卷“ ***ubuntu_vol ”。***

```
docker run -d --name=mounttest --mount source=ubuntu_vol,
              destination=/usr/share/ubuntu/html ubuntu:latest
```

在此之后，我们可以通过执行以下命令序列来清理容器和卷。

```
$ docker container stop mounttest

$ docker container rm mounttest

$ docker volume rm ubuntu_vol
```

除了这个命令，我们还可以使用上一节中使用的命令预填充卷以启动容器。

## 什么是音量驱动程序？

当我们需要将卷存储在远程主机或云提供商上以加密卷的内容或添加更多功能时，我们可以使用卷驱动程序。例如，当我们使用“ ***docker volume create*** ”命令，或者我们用一个不存在的卷启动一个容器时，我们可以指定卷驱动程序。卷驱动程序的示例之一是“ ***Vieux/sshfs*** ”。在本节中，我们将只接触音量驱动程序的基础知识。

### ***初始设置 - docker plugin install 命令***

使用卷驱动程序的第一步是安装适当的插件。例如，我们可以使用以下命令安装插件 vieux/sshfs。

```
$ docker plugin install --grant-all-permissions vieux/sshfs
```

它将在 docker 主机上安装卷驱动程序插件。

### ***使用卷驱动程序创建数据卷 - docker volume create 命令***

下一步是使用刚刚安装的插件创建一个卷。

```
$ docker volume create --driver vieux/sshfs \
  -o sshcmd=test@node2:/home/test \
  -o password=testpassword \
  sshvolume
```

我们在上面的命令中指定了 SSH 密码，但如果两台主机配置了共享密钥，我们可以省略它。标志“ *-o* ”指定零个或多个可配置选项（*如上述命令中的用户名和密码*）。

### ***启动使用卷驱动程序创建卷的容器***

安装插件后，我们也可以通过指定volume driver的不存在的volume来启动一个容器。Docker 将在启动容器时创建一个新卷。***以下示例显示了使用“ vieux/sshfs*** ”卷驱动程序创建 Nginx 容器。

```
$ docker run -d --name sshfs-container --volume-driver vieux/sshfs --mount src=sshvolume,
         target=/app,volume-opt=sshcmd=test@node2:/home/test,
         volume-opt=password=testpassword nginx:latest
```

同样，如果有共享密钥，我们可以省略密码，并且可以使用“ *volume-opt* ”指定零个或多个可配置选项。当卷驱动程序需要传递选项时，请记住使用“ *--mount ”标志来挂载卷。*

## 在 Docker 中备份、恢复、迁移或删除数据卷

我们可以使用卷进行迁移、备份和恢复。让我们用卷来讨论这些。

### ***备份一个容器***

我们按照以下步骤来备份一个容器。

1.  *创建一个新容器*
2.  *启动一个新容器并从第 1 步中创建的容器装载卷。*
3.  *将 localhost 目录挂载为 /backup*
4.  *将卷的内容打包到 /backup 目录中的 backup.tar 文件。*

因此，我们在 /backup 本地目录中有卷的备份。让我们举个例子来说明这些命令。

首先，我们使用以下命令创建一个新容器：

```
docker run -v /dbdata --name dbcontnr ubuntu /bin/bash
```

接下来，我们可以创建一个备份目录或备份当前目录本身中的卷。一旦发生这种情况，我们执行以下命令来压缩卷内容。

```
docker run --rm --volumes-from dbcontnr -v $(pwd):/backup ubuntu tar 
cvf /backup/backup.tar /dbdata
```

以下屏幕截图显示了上述命令的执行结果。![备份卷](https://www.toolsqa.com/gallery/Docker/8-Backup%20volume.png)

当我们列出当前目录的内容时，我们可以看到在主机上创建的 dbdata 卷的 backup.tar 文件。

### ***从备份中恢复容器***

现在我们已经创建了备份，我们如何将它恢复到容器中呢？我们可以将备份恢复到确切的容器或另一个容器。首先，让我们将上一节中创建的备份恢复到一个新容器中。为此，我们首先创建一个新容器，如下所示。

```
docker run -v /dbdata --name dbcontainer2 ubuntu /bin/bash
```

现在我们将备份 ( *backup.tar* ) 恢复到这个容器，如下所示：

```
docker run --rm --volumes-from dbcontainer2 -v $(pwd):/backup ubuntu bash -c 
     "cd /dbdata && tar xvf /backup/backup.tar --strip 1"
```

一旦命令成功，容器' *dbcontainer2's* '数据卷就会有untar文件的内容。

我们可以使用这些备份、迁移和恢复技术来自动化整个过程。

### ***删除泊坞窗卷***

Docker 容器的数据量在容器删除后仍然存在。Docker 数据卷在容器删除后仍然存在。我们有两种类型的数据量：

-   ***命名卷**：命名卷具有容器外部的特定源，例如 awesome：/hello。*
-   ***匿名卷**：这些卷在容器删除时没有特定的来源。我们应该指示 Docker 引擎守护进程以防我们需要删除它们。*

要自动删除匿名卷，我们使用“ ***--rm*** ”选项。例如，如果我们有一个匿名 /bkup 卷。然后，当容器删除时，Docker 引擎将使用以下命令自动删除 /bkup 卷。

```
$ docker run --rm -v /bkup -v awesome:/hello busybox top
```

上面的命令创建了一个匿名的 /bkup 卷。因此，当我们删除容器时，Docker 引擎会删除 /bkup 卷，但不会删除 awesome 卷。

要删除所有未使用的卷，我们可以使用以下命令：

```
$ docker volume prune
```

它会删除所有未使用的卷并释放空间。

## 关键要点

-   *除了卷之外，我们还可以使用一些挂载来持久化数据。*
-   *当我们创建一个容器并指定一个不存在的容器时，Docker 会为我们创建卷。*
-   *我们还可以创建一个卷，然后使用 -v 标志将其与容器一起使用。*
-   *Docker-compose 允许我们使用现有的或新的卷。*
-   *使用卷，可以更轻松地备份、迁移和恢复数据，甚至可以使整个过程自动化。*