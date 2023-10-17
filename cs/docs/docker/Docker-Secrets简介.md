---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 简介

[12 因素应用程序](https://12factor.net/)的众多理念之一是配置应存储在环境中。实际上，这意味着将配置与我们的代码分开存储。

在本教程中，我们将了解Docker秘密如何帮助我们实现这一目标。我们将了解如何创建和管理Docker机密。然后，我们将研究如何将Docker机密用作我们应用程序部署的一部分。

## 2. 什么是秘密？

一般来说，秘密提供了一种安全存储数据的机制，应用程序可以在运行时读取这些数据。机密在将敏感数据与应用程序代码分开存储方面起着至关重要的作用。这包括密码、主机名、SSH 密钥等数据。

例如，假设我们的应用程序需要数据库连接。为此，它需要一个主机名、用户名和密码。此外，还有用于开发、测试和生产的不同数据库服务器。

有了秘密，每个环境都可以向应用程序提供自己的数据库信息。应用程序代码不需要知道它在哪个环境中运行。它只需要一种一致的方式来查找值。

虽然Docker机密是一项相对较新的功能，但大多数云平台多年来一直提供某种形式的机密。

例如，Amazon Web Services 和 Google Cloud 都有 Secrets Manager 工具，而 Azure 提供了 Key Vault 服务。[Kubernetes](https://www.baeldung.com/ops/kubernetes)还为机密提供一流的支持。

Docker 的秘密实施使用了许多与前面提到的系统相同的功能：

-   秘密与应用程序分开创建和管理
-   遵循最低特权和需要知道的访问原则
-   灵活地存储各种不同的数据类型

在对Dockersecret 有了基本的了解后，让我们来看看如何管理它们。

## 3. 管理Docker秘密

在本节中，我们将了解如何管理从创建到删除的Docker机密。

### 3.1. Docker Secrets

目前，Docker secrets仅适用于 swarm 服务。这意味着独立容器无法访问机密。

因此，要使用这些秘密，我们必须使用以下命令[为 swarm 配置我们的集群：](https://docs.docker.com/engine/swarm/swarm-tutorial/create-swarm/)

```shell
docker swarm init --advertise-addr <MANAGER-IP>
```

其中 <MANAGER-IP> 是Docker分配给管理器节点的 IP 地址。

在Windows和Mac的DockerDesktop 上，我们可以简化命令：

```shell
docker swarm init
```

为 swarm 配置集群后，我们现在可以创建秘密。

### 3.2. 创建DockerSecret

Docker secrets 可以存储几乎任何类型的数据，这些数据可以表示为字符串或二进制：

-   用户名和密码
-   主机名和端口
-   SSH 密钥
-   TLS 证书

唯一真正的限制是数据的大小必须低于 500KB。

创建机密时，该命令接受来自命令行的输入：

```shell
docker secret create my_secret -
```

在这种形式下，该命令允许我们输入秘密的值，甚至支持多行数据。要完成数据输入，我们必须给它一个 EOF 信号(在基于 Unix 的系统上为 Ctrl+D)。

但是，与自动流程结合使用时，使用键盘手动输入既容易出错又不实用。因此，我们也可以使用文件的内容来创建一个秘密：

```lua
docker secret create my_secret /path/to/secret/file
```

### 3.3. 显示Docker秘密

一旦我们创建了一个秘密，我们就可以确认它是成功的：

```shell
docker secret ls
ID                          NAME        DRIVER    CREATED          UPDATED
2g9z0nabsi6v7hsfra32unb1o   my_secret             30 minutes ago   30 minutes ago
```

这显示了我们所有的秘密，以及分配给它们的唯一 ID。我们也可以检查个人秘密：

```shell
docker secret inspect my_secret
[
    {
        "ID": "2g9z0nabsi6v7hsfra32unb1o",
        "Version": {
            "Index": 15
        },
        "CreatedAt": "2022-05-13T00:34:41.2802246Z",
        "UpdatedAt": "2022-05-13T00:34:41.2802246Z",
        "Spec": {
            "Name": "my_secret",
            "Labels": {}
        }
    }
]
```

### 3.4. 删除Docker机密

一旦不再需要秘密，就将其删除被认为是最佳做法。例如，我们可以从命令行永久删除一个秘密：

```shell
docker secret rm my_secret
```

请注意，如果任何服务正在使用该机密，则此命令不起作用。

## 4. 使用DockerSecrets

了解了如何管理机密后，我们现在可以看看如何在我们的应用程序中实际使用它们。与Docker生态系统中的大多数事物一样，有多种方法可以做到这一点。

### 4.1. 码头服务

因为Dockersecrets 要求我们的集群处于 swarm 模式，所以我们无法从正常的Dockerrun命令访问 secrets。相反，我们必须创建服务，我们可以使用命令行指定一个或多个秘密：

```shell
docker service create --name my_app --secret my_secret openjdk:19-jdk-alpine
```

### 4.2. Docker Compose

对于[Docker Compose](https://www.baeldung.com/ops/docker-compose)版本 3 及更高版本，我们有两种使用机密的选项。下面是定义服务和机密的简单示例：

```yaml
version: '3.1'
services:
  my_app:
    image: my_app:latest
    secrets:
     - my_external_secret
     - my_file_secret
secrets:
  my_external_secret:
    external: true
  my_file_secret:
    file: /path/to/secret/file.txt
```

在这个例子中，我们定义了两个秘密。第一个是外部的，这意味着它指的是使用Dockersecret命令创建的秘密。第二个引用一个文件，不需要使用Docker进行任何初始设置。

请记住，使用文件方法会绕过使用Docker机密的大部分好处。此外，该文件必须对可能运行该服务的所有主机可用，我们必须注意使用Docker以外的机制来保护其内容。

### 4.3. 访问秘密

Docker 将秘密作为文件提供给我们的应用程序。默认行为是在目录/run/secrets中使每个秘密成为其自己的文件。使用我们之前的示例， my_secret的内容将在文件/run/secrets/my_secret中可用。

我们可以通过在我们的服务中指定来更改文件的位置：

```shell
docker service create
  --name my_app
  --secret source=my_secret,target=/different/path/to/secret/file,mode=0400
```

如果秘密包含我们的应用程序期望在特定位置的信息，这将很有用。例如，我们可以使用一个秘密来存储一个 Nginx 配置文件，然后在标准位置 ( /etc/nginx/conf.d/site.conf )中提供它。

## 5.总结

秘密是任何基于容器的架构的重要工具，因为它们帮助我们实现保持代码和配置分离的目标。此外，Docker 机密提供了一种安全存储敏感数据并使其可供需要它的应用程序使用的方法。
