我们大多数人都知道[***Docker 容器***](https://www.toolsqa.com/docker/docker-containers/)和服务可以连接其他非 Docker 工作负载。它被称为***Docker Networking***，就此网络而言，*Docker 容器*和服务不需要知道它们部署在[***Docker***](https://www.toolsqa.com/docker/introduction-to-docker-and-docker-architecture/)上或者它们连接到的对等点是否是*Docker 工作负载*。本文将更多地讨论容器和服务之间的连接。让我们从本文将要涵盖的主题开始。

-   什么是 Docker 网络？
    -   *而且，什么是容器网络模型 (CNM)？*
-   里面有什么各种网络驱动？
    -   *桥接网络驱动程序*
    -   *无网络驱动程序*
    -   *主机网络驱动程序*
    -   *覆盖网络驱动程序*
    -   *Macvlan 网络驱动程序*
-   Docker 的基本网络
    -   *docker 网络 ls 命令*
    -   *检查 Docker 网络 - docker network inspect*
    -   *码头网络创建*
    -   *如何将docker容器连接到网络？*
    -   *如何在 docker 中使用主机网络？*
-   Docker 组成网络
    -   *在 Docker Compose 网络中更新容器*
    -   *Docker Compose 默认网络*

## 什么是 Docker 网络？

它可以定义为一个通信包，允许隔离的 Docker 容器相互通信以执行所需的操作或任务。

Docker 网络通常具有如下所示的功能或目标：![Docker 网络目标](https://www.toolsqa.com/gallery/Docker/1-Docker%20Networking%20Goals.png)

-   ***灵活性**——它为不同平台上的各种应用程序相互通信提供了灵活性。*
-   ***跨平台**——我们可以使用**Docker Swarm 集群**，并在跨平台中使用 Docker，跨各种服务器工作。*
-   ***可扩展性**——作为一个完全分布式的网络，应用程序可以单独扩展和增长，同时确保性能。*
-   ***去中心化——Docker**网络是去中心化的。因此，它使应用程序具有高可用性和传播性。因此，如果资源池中缺少任何容器或主机，我们可以将其服务传递给其他可用资源或引入新资源。*
-   ***用户友好**——服务部署更容易。*
-   ***支持**——Docker 提供开箱即用的支持，其功能简单明了，使 docker 网络易于使用。*

我们有一个名为“***容器网络模型（CNM）*** ”的模型，它支持上述所有特性。让我们在以下部分中了解详细信息：

### ***什么是容器网络模型 (CNM)？***

***容器网络模型***( *CNM* ) 使用多个网络驱动程序为容器提供网络。*CNM*标准化了提供此网络所需的步骤。CNM 将网络配置存储在分布式键值存储中，例如控制台。

CNM架构如下图所示：![容器网络模型（CNM）](https://www.toolsqa.com/gallery/Docker/2-.png)

从上图可以看出，*CNM*有*IPAM*和网络插件的接口。*IPAM*插件*APIS*可以*创建/删除*地址池并分配/取消分配容器 IP 地址，以在网络中添加或删除容器。网络插件 API 用于创建/删除网络以及从网络中添加/删除容器。

上图还显示了 CNM 的 5 个主要对象。让我们一一讨论这些对象。

-   ***网络控制器**：网络控制器为分配和管理网络的 Docker 引擎公开简单的 APIS。*
-   ***驱动程序**：驱动程序负责管理网络。驱动程序拥有网络，可以有多个驱动程序参与网络以完成各种部署和用例场景。*
-   ***网络**：网络提供同一网络端点之间的连接，并将它们与其余端点隔离开来。每当网络更新或新建时，都会通知相应的驱动程序。*
-   ***端点**：端点为网络的一个容器公开的服务与其他容器提供的其他服务提供/提供连接。端点具有全局范围并且是服务而不是容器。*
-   ***沙箱**：当用户发出在网络上创建端点的请求时，将创建沙箱。一个沙箱可以有多个端点连接到不同的网络。*

有了这些基础知识，我们现在将继续了解其中支持的网络驱动程序类型。

## 里面有什么各种网络驱动？

Docker 支持各种使网络更容易的网络驱动程序。Docker 在主机上创建一个专用内部网络。主机上容器的典型通信如下所示：![桥接网络驱动程序](https://www.toolsqa.com/gallery/Docker/3-Bridge%20Network%20Driver.png)

由于这个默认的专用网络，所有容器都有一个***内部 IP 地址***，并且可以使用这个内部 IP 相互访问。它使用各种驱动程序来跨容器进行通信。*Bridge 是 Docker 中的默认网络驱动*程序。如果我们没有明确指定网络驱动程序的类型，则默认使用 Bridge 网络驱动程序。让我们了解Docker支持的用于容器间通信的各种网络驱动程序的详细信息：

### ***桥接网络驱动程序***

我们可以使用 docker network create 命令创建桥接网络，如下所示：

```
docker network create --driver bridge bridge_network
```

当我们执行上述命令时，它会返回一个标识我们刚刚创建的桥接网络的 id。例如，这显示在下面的屏幕截图中。![docker 网络创建命令](https://www.toolsqa.com/gallery/Docker/4-docker%20network%20create%20command.png)

从上面的屏幕截图可以看出，我们在命令中指定了网络类型（*在本例中为网桥，用红色圆圈表示*）。然后命令返回创建的网络的 id（*黄色矩形*），表示命令执行成功。

### ***无网络驱动程序***

当网络驱动程序为“*无*”时，容器不附加或与任何网络相关联。这些容器也无权访问任何其他容器或外部网络。下图显示了带有“*无*”网络 的*Docker 主机。*![没有任何](https://www.toolsqa.com/gallery/Docker/5-None.png)

那么我们什么时候可以使用这种类型的网络呢？当我们想要完全禁用特定容器上的网络堆栈并拥有环回设备时，我们使用这个“*无*”网络驱动程序。***请注意，*** “*无*”***网络驱动程序在 Swarm 设备上不可用***。

### ***主机网络驱动程序***

主机网络驱动程序消除了 Docker 主机和 Docker 容器之间的网络隔离。主机网络驱动程序如下图所示。![主机网络驱动](https://www.toolsqa.com/gallery/Docker/6-Host%20network%20driver.png)

从上图中我们可以看出，一旦解除隔离，容器就可以直接使用宿主机的网络。但是，我们不能在主机网络中的同一主机或同一端口上运行多个 Web 容器，因为现在主机网络中的所有容器都使用该端口。

### ***覆盖网络驱动程序***

覆盖网络主要用于 Swarm 集群。代表覆盖网络的图表如下所示。![覆盖网络](https://www.toolsqa.com/gallery/Docker/7-Overlay%20network.png)

覆盖网络创建了一个跨越 swarm 集群中所有节点的内部专用网络，从而促进了 swarm 服务与独立容器之间甚至不同 Docker 守护进程上存在的两个容器之间的通信。换句话说，Overlay 网络连接/链接多个 Docker 守护进程，并使 Swarm 服务能够相互通信。我们可以参考[***Networking with Overlay networks***](https://docs.docker.com/network/network-tutorial-overlay/)了解更多细节。

### ***Macvlan 网络驱动程序***

当我们直接连接到物理网络而不是通过 Docker 主机的网络堆栈进行路由时，*Macvlan 驱动程序应该是最佳选择。*

下图显示了 Macvlan 网络。![Macvlan网络](https://www.toolsqa.com/gallery/Docker/8-Macvlan%20network.png)

使用 Macvlan 网络，我们可以为一个容器分配一个 MAC 地址，然后容器在网络上显示为一个物理设备。然后，Docker 守护进程可以使用容器的 MAC 地址将流量路由到容器。通过[***与 Macvlan 网络联网***](https://docs.docker.com/network/network-tutorial-macvlan/)了解更多关于 Macvlan 网络的信息。

## Docker 的基本网络

Docker 容器之间*以及与 Docker Host*之间进行通信，以便它们可以共享*信息、数据*以及提供有效的服务和整体帮助应用程序的平稳运行。*Networking for Docker*使这成为可能。实际上，Docker 在安装到主机上时会创建一个 Ethernet Adapter。因此，当我们在 Docker 主机上执行“ *ifconfig* ”命令时，我们可以看到这些以太网适配器的详细信息。这个以太网适配器负责 Docker 的所有网络方面。

在 Docker 主机上执行***ifconfig***会产生以下输出。![Docker 以太网适配器信息](https://www.toolsqa.com/gallery/Docker/9-Docker%20Ethernet%20Adapter%20info.png)

上面的屏幕截图显示了 docker 主机上的以太网适配器“ *docker0* ”。现在让我们讨论 Docker 中使用的基本网络命令。

### ***docker 网络 ls 命令***

“ *docker network ls* ”命令具有以下语法：

```
docker network ls
```

执行此命令时会产生以下输出。![Docker 网络 - docker network ls](https://www.toolsqa.com/gallery/Docker/10-Docker%20Networking%20-%20docker%20network%20ls.png)

如上面的屏幕截图所示，“ *docker network ls* ”命令列出了 Docker 主机上与 Docker 关联的所有网络。此命令不带任何参数。

### ***检查 Docker 网络 - docker network inspect***

命令“ *docker network inspect* ”允许我们查看与 Docker 关联的特定网络的更多详细信息。该命令的一般/典型语法如下：

```
docker network inspect networkname 
```

这里的***networkname***是我们需要检查的网络的名称。

命令“ *docker network inspect* ”返回网络的详细信息，其名称被指定为命令的参数。

例如，命令

```
docker network inspect bridge
```

将产生以下输出。![Docker 网络 - docker network inspect](https://www.toolsqa.com/gallery/Docker/11-Docker%20Networking%20-%20docker%20network%20inspect.png)

如图所示，此命令生成桥接网络的详细信息，这是 Docker 主机上存在的默认网络。

### ***码头网络创建***

我们也可以在 Docker 中创建我们的网络。这是使用命令“ ***docker network create*** ”完成的。此命令具有以下一般语法：

```
docker network create –-driver drivername name
```

这里，“ ***drivername*** ”是使用的网络驱动程序的名称。

“***名称***”是新创建的网络名称。

*执行此命令时会创建一个由“名称*”指定的新网络，并返回该网络的长 ID。

让我们使用以下命令创建一个新网络“ *my_bridge_nw ”。*

```
docker network create --driver bridge my_bridge_nw
```

执行时，此命令会生成以下输出。![Docker 网络 - docker 网络创建](https://www.toolsqa.com/gallery/Docker/12-Docker%20Networking%20-%20docker%20network%20create.png)

从上面的屏幕截图中，我们可以看到“docker *network create* ”命令返回了刚刚创建的网络“ *my_bridge_nw* ”的长 ID 。为了验证新网络的创建，我们可以启动命令“ *docker network ls* ”，我们可以在其输出中看到刚刚创建的新网络（*屏幕截图中的红色矩形*）。

### ***如何将docker容器连接到网络？***

创建新网络后，我们需要将 docker 容器附加或连接到这个新网络。这是通过使用“ *docker run* ”命令指定“ *net* ”选项来完成的。接下来，我们指定该容器应附加到的网络名称作为 net 选项的值。

让我们执行以下命令。

```
docker run -d --net=my_bridge_nw --name my_db training/postgres
```

上面的命令会将容器“ *training/postgres* ”附加到网络“ *my_bridge_nw* ”。

命令的输出如下所示。![如何将 docker 容器连接到网络？](https://www.toolsqa.com/gallery/Docker/13-How%20to%20attach%20a%20docker%20container%20to%20a%20network.png)

我们可以看到容器现在已连接到网络。为了验证这一点，我们现在检查网络“ ***my_bridge_nw*** ”，如下所示：

```
docker network inspect my_bridge_nw
```

此命令现在显示以下输出。![如何检查附加的 docker 容器？](https://www.toolsqa.com/gallery/Docker/14-How%20to%20check%20an%20attached%20docker%20container.png)

上面的屏幕截图显示了连接到网络的容器（*由红色矩形指示*）。

### ***如何在docker中使用主机网络？***

我们可以直接将 Docker 容器绑定到宿主机网络，无需网络隔离。这给人一种在没有容器的情况下直接在 Docker 主机上运行进程的感觉。在进程和用户命名空间、存储等所有其他方式中，进程与主机隔离。

让我们执行以下命令。

```
docker run --rm -d --network host --name cont__nginx nginx
```

使用此命令，我们将容器作为分离进程启动。上面命令中的选项“ *rm* ”会在容器停止/退出后将其删除。同样，“- *d* ”选项在后台启动容器（*分离*）。

上述命令的输出/结果如下：![泊坞窗运行命令](https://www.toolsqa.com/gallery/Docker/15-docker%20run%20command.png)

所以这里我们创建一个新的分离容器***nginx***。现在要验证创建，我们可以执行以下命令。

```
docker ps
```

此命令的输出显示在上述命令中启动的容器实际上正在运行。![集装箱清单](https://www.toolsqa.com/gallery/Docker/16-Container%20list.png)

上面运行的容器（*红框所示*）可以使用docker的stop命令停止，如下图。

```
docker container stop cont__nginx
```

命令的输出如下。![断开容器与 docker 网络的连接](https://www.toolsqa.com/gallery/Docker/17-Disconnect%20container%20from%20docker%20network.png)

在上面的截图中，我们使用上面的命令停止了正在运行的容器*nginx 。*一旦容器停止，它将自动删除，因为它是使用 -rm 选项启动的。

## Docker 组成网络

我们在上一篇文章中讨论了***Docker Compose***。Docker Compose 建立了一个单一的网络。每个容器都加入这个默认网络，并且可以被网络中的其他容器访问，并被这些容器以与容器名称相同的主机名发现。

### ***在 Docker Compose 网络中更新容器***

我们可以使用 Docker Compose 文件更改容器的服务配置。这样当我们执行命令“ *docker-compose up* ”来更新容器时，旧容器将被删除，并插入一个新容器。请注意，新容器和旧容器将具有不同的 IP 地址但名称相同。然后容器将关闭与旧容器的连接，并通过使用名称查找新容器来连接到新容器。

***链接容器***

考虑以下 Docker Compose 文件 ( *YAML* )：

```
version: '2'
services:
    web:
        build: . 
        links: 
            - "db:pgdatabase"
    db:
        image: postgres
```

在上面的 YAML 文件中，“ *web* ”容器使用“ *links* ”标签为“ *db* ”容器指定了一个额外的别名“ *pgdatabase ”。*这意味着容器“ *web* ”可以通过“ *db* ”或“ *pgdatabase* ”作为主机名到达“ *db ”容器。*这样，我们就可以将容器相互链接起来。

### ***Docker Compose 默认网络***

对于 Docker Compose，我们可以指定将联网或连接容器的默认网络，甚至可以使用自定义网络。对于使用自定义网络的网络，我们可以使用预先存在的网络或创建一个新网络。现在让我们考虑一个指定了默认网络的 YAML 文件。

```
verision: '2'
 services: web: build
 ports: - "8000:8000"
 db: image: postgres networks:
 default: driver: my-nw-driver-1
```

在上面的 YAML 文件中，我们在“ *networks ”下定义了一个“* *default* ”条目。这指定容器应使用此默认网络进行联网。

我们还可以使用网络密钥指定我们自己的网络，甚至可以创建更复杂的拓扑、网络驱动程序等。我们甚至可以使用它来连接到不受 Docker Compose 管理的外部服务。当我们有多个服务时，每个服务指定要连接到哪个网络。

考虑以下 YAML 文件作为多个主机和多个自定义网络的示例。

```
version: '2'

services:
    proxy:
        build: ./proxy
        networks: 
            - my-nw1
    app:
        build: ./app
        networks:
            my-nw1:
                ipv4_address: 172.16.238.10 
                ipv6_address: "2001:3984:3989::10"
            - my-nw2
    db:
        image: postgres
        networks:
            - my-nw2

networks:
    my-nw1:
        # use the bridge driver, but enable IPv6
        driver: bridge
        driver_opts:
            com.docker.network.enable_ipv6: "true"
        ipam:
            driver: default
            config:
                - subnet: 172.16.238.0/24
                gateway: 172.16.238.1
                - subnet: "2001:3984:3989::/64"
                gateway: "2001:3984:3989::1"
    my-nw2:
        # use a custom driver, with no options
        driver: my-custom-driver-1
```

在上面的 YAML 文件中，我们创建了两个自定义网络，*my-nw1*和*my-nw2*。我们看到服务“ *proxy* ”不会连接到服务“ *db* ”，因为它们不共享网络。但是服务“ *app* ”连接到两个网络，这意味着它可以连接到服务“ *db* ”和“ *proxy* ”。

如果我们想访问任何预先存在的网络，我们应该使用下面的“*外部*”选项。

```
version: '2'

networks:
    default:
        external:
            name: pre-existing-nw
```

因此，在这种情况下，Docker Compose 会将容器连接到“ *pre-existing-nw* ”网络，而不是默认网络。

## 关键要点

-   *Docker 在 Docker 主机上安装时会安装以太网适配器 (Docker0)。这个适配器负责 Docker 的网络。*
-   *Docker 支持各种网络驱动程序，我们可以使用它们将容器联网。流行的有 Bridge、Host、Overlay、Macvlan 和 None。网桥是 docker 的默认网络。*
-   *Docker 支持“网络”命令，它为我们提供了各种操作，如列出主机上存在的网络、创建新网络、检查网络等。*
-   *我们可以使用 docker run 命令的“net”选项将任何容器连接到网络。*
-   *同样，我们可以使用“docker run”命令的 -rm 选项以分离方式运行容器。*
-   *当我们完成联网后，我们可以断开容器与网络的连接。*