## 一、简介

在本教程中，我们将了解 Podman(“Pod Manager”的缩写)及其功能和用法。

## 2.豆豆人

[Podman 是一个用于开发、管理和运行OCI](https://www.opencontainers.org/)容器的开源容器管理工具。让我们来看看 Podman 与其他容器管理工具相比的一些优势：

-   **Podman 创建的镜像与其他容器管理工具兼容**。Podman 创建的镜像遵循 OCI 标准，因此可以推送到其他容器注册中心，如 Docker Hub
-   **它可以作为普通用户运行而无需 root 权限。**当以非 root 用户身份运行时，Podman 会创建一个用户命名空间，并在其中获取 root 权限。这允许它挂载文件系统并设置所需的容器
-   **它提供了管理 pod 的能力。**与其他容器运行时工具不同，Podman 允许用户管理 pod(一组一起运行的一个或多个容器)。用户可以对 pod 执行创建、列出、检查等操作

但是，Podman 有一些限制：

-   **它仅在基于 Linux 的系统上运行。** 目前，Podman 仅在基于 Linux 的操作系统上运行，没有适用于 Windows 和 macOS 的包装器。
-   **Docker Compose 别无选择。**Podman 不支持在本地管理多个容器，类似于 Docker Compose 所做的。*[作为podman-compose](https://github.com/containers/podman-compose)*项目的一部分，正在开发使用 Podman 后端的 Docker Compose 实现，但这仍在进行中。

## 3. 与 Docker 的比较

现在我们已经了解了 Podman 是什么以及它的优点和局限性，让我们将它与使用最广泛的容器管理工具之一的 Docker 进行比较。

### 3.1. 命令行界面 (CLI)

Podman 提供了由 Docker 客户端公开的相同命令集。换句话说，这两个实用程序的命令之间存在一对一的映射。

但是，像*podman ps*和*podman images*这样的命令不会显示使用 Docker 创建的容器或镜像。这是因为 Podman 的本地存储库是*/var/lib/containers*而不是Docker 维护的*/var/lib/ docker。*

### 3.2. 容器模型

**Docker 为容器使用客户端-服务器架构，而 Podman 使用**跨 Linux 进程通用的传统 fork-exec 模型。使用 Podman 创建的容器是父 Podman 进程的子进程。这就是当为 Docker 和 Podman 运行版本命令时，Docker 列出客户端和服务器的版本而 Podman 只列出它的版本的原因。

*docker 版本*的示例输出：

```bash
Client:
 Version:       17.12.0-ce
 API version:   1.35
 Go version:    go1.9.2
 Git commit:    c97c6d6
 Built: Wed Dec 27 20:11:19 2017
 OS/Arch:       linux/amd64

Server:
 Engine:
  Version:      17.12.0-ce
  API version:  1.35 (minimum version 1.12)
  Go version:   go1.9.2
  Git commit:   c97c6d6
  Built:        Wed Dec 27 20:09:53 2017
  OS/Arch:      linux/amd64
  Experimental: false复制
```

*podman 版本*的示例输出：

```bash
Version:       0.3.2-dev
Go Version:    go1.9.4
Git Commit:    "4f4a78abb40fa0e8407e8a55d5a67a2650d8fd96"
Built:         Mon Mar  5 11:10:35 2018
OS/Arch:       linux/amd64复制
```

由于 Podman 本身作为进程运行，因此它不需要在后台运行任何守护进程。**与 Podman 不同，Docker 需要一个守护进程 Docker daemon 来协调客户端和服务器之间的 API 请求**。

### 3.3. 无根模式

如前所述，Podman 不需要 root 访问权限即可运行其命令。**另一方面，Docker 依赖于守护进程，需要 root 权限或要求用户是\*docker\*组**的一部分，以便能够在没有 root 权限的情况下运行 Docker 命令**。**

```bash
$ sudo usermod -aG docker $USER复制
```

## 四、安装使用

让我们从[安装 Podman](https://github.com/containers/libpod/blob/master/install.md)开始。podman *info*命令显示 Podman 系统信息并帮助检查安装状态。

```bash
$ podman info复制
```

此命令显示与主机相关的信息，例如内核版本、已用和可用的交换空间，以及与 Podman 相关的信息，例如它有权将图像拉入和推送到的注册表、它使用的存储驱动程序、存储位置等：

```bash
host:
  MemFree: 546578432
  MemTotal: 1040318464
  SwapFree: 4216320000
  SwapTotal: 4216320000
  arch: amd64
  cpus: 2
  hostname: base-xenial
  kernel: 4.4.0-116-generic
  os: linux
  uptime: 1m 2.64s
insecure registries:
  registries: []
registries:
  registries:
  - docker.io
  - registry.fedoraproject.org
  - registry.access.redhat.com
store:
  ContainerStore:
    number: 0
  GraphDriverName: overlay
  GraphOptions: null
  GraphRoot: /var/lib/containers/storage
  GraphStatus:
    Backing Filesystem: extfs
    Native Overlay Diff: "true"
    Supports d_type: "true"
  ImageStore:
    number: 0
  RunRoot: /var/run/containers/storage复制
```

让我们来看看一些基本的 Podman 命令。

### 4.1. 创建图像

首先，我们将了解如何使用 Podman 创建图像。让我们首先创建一个包含以下内容的*Dockerfile ：*

```bash
FROM centos:latest
RUN yum -y install httpd
CMD ["/usr/sbin/httpd", "-D", "FOREGROUND"]
EXPOSE 80

复制
```

现在让我们使用*构建*命令创建图像：

```bash
$ podman build .复制
```

在这里，我们首先拉取 CentOS 的基础映像，在其上安装 Apache，然后将其作为前台进程运行，并暴露 80 端口。我们可以通过运行此映像并将暴露的端口映射到主机端口来访问 Apache 服务器。

*构建*命令递归地传递上下文目录中的所有可用文件夹。当没有指定目录时，当前工作目录默认成为构建上下文。因此，建议不要在上下文目录中包含图像创建不需要的文件和文件夹。

### 4.2. 列出可用图像

podman *images*命令列出所有可用的图像。它还支持[各种选项](https://podman.readthedocs.io/en/latest/markdown/podman-images.1.html#options)来过滤图像。

```bash
$ podman images复制
```

此命令列出本地存储库中可用的所有图像。它包含有关图像从哪个存储库中提取的信息、标签、图像 ID、创建时间和大小。

```bash
REPOSITORY                 TAG      IMAGE ID         CREATED         SIZE
docker.io/library/centos   latest  0f3e07c0138f    2 months ago      227MB
<none>                     <none   49030e844ce7   27 seconds ago     277MB复制
```

### 4.3. 运行图像

*运行*命令创建给定图像的容器，然后运行它。让我们运行我们之前创建的 CentOS 镜像

```bash
$ podman run  -p 80:80 -dit centos复制
```

此命令首先检查是否有可用于 CentOS 的本地映像。如果图像不在本地，它会尝试从配置的注册表中提取图像。如果图像不在注册表中，它会显示有关无法找到图像的错误。

**上面的运行命令指定将容器暴露的 80 端口映射到主机的 80 端口，\*dit\*标志指定以分离和交互模式运行容器**。创建的容器的 ID 将作为输出。

### 4.4. 删除图像

*rmi*命令删除本地存储库中存在的图像。可以通过在输入中提供以空格分隔的 ID 来删除多个图像。指定*-a*标志会删除所有图像

```bash
$ podman rmi 785188cd988c复制
```

### 4.5. 列出容器

*可以使用ps -a*命令列出所有可用的容器，包括未运行的容器。类似于*images*命令，这也可以与[各种选项](https://podman.readthedocs.io/en/latest/markdown/podman-ps.1.html#options)一起使用。

```bash
$ podman ps -a复制
```

上述命令的输出列出了所有容器，其中包含创建它的图像、用于启动它的命令、它的状态、它运行的端口以及分配给它的名称等信息。

```bash
CONTAINER ID   IMAGE    COMMAND     CREATED AT                      STATUS              PORTS                                    NAMES
eed30719cd37   centos   /bin/bash   2019-12-09 02:57:37 +0000 UTC   Up 14 minutes ago   0.0.0.0:80->80/udp, 0.0.0.0:80->80/tcp   reverent_liskov复制
```

### 4.6. 删除容器

*rm*命令删除容器。此命令不会删除处于运行或暂停状态的容器。他们需要先被阻止，然后被移除。

```bash
$ podman stop eed30719cd37

$ podman rm eed30719cd37复制
```

### 4.7. 创建 Pod

*pod create*命令创建一个 pod 。创建命令支持[不同的选项](https://podman.readthedocs.io/en/latest/markdown/podman-pod-create.1.html#options)。

```bash
$ podman pod create复制
```

*pod create*命令创建一个 pod，默认情况下它带有一个与之关联的*infra*容器，除非将 infra 标志明确设置为*false。*

```bash
$ podman pod create --infra = false复制
```

Infra 容器允许 Podman 连接 pod 中的各种容器。

### 4.8. 列出豆荚

*pod list* 命令显示所有可用的pod

```bash
$ podman pod list复制
```

此命令的输出显示 pod id、其名称、关联容器的数量、infra 容器的 id(如果可用)等信息：

```bash
POD ID         NAME             STATUS      CREATED       # OF CONTAINERS   INFRA ID
7e0a68528aed   gallant_raman    Running    5 seconds ago        1           c6d06673c667复制
```

所有可用的 Podman 命令及其用法都可以在[官方文档](https://podman.readthedocs.io/en/latest/Commands.html)中找到。

## 5.总结

在本教程中，我们了解了 Podman 的基础知识及其功能、它与 Docker 的比较以及一些可用的命令。