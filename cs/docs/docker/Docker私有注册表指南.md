---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[Docker](https://www.baeldung.com/ops/docker-guide)是一个软件平台，可在操作系统级虚拟化中运行，以在容器中运行应用程序。Docker 的独特功能之一是Docker容器提供相同的虚拟环境来运行应用程序。CI/CD 工具还可用于自动从注册表中推送或拉取镜像，以便在生产环境中进行部署。

[在本教程中，我们将学习了解公共](https://hub.docker.com/)和[私有Docker注册表](https://docs.docker.com/registry/deploying/)的使用。我们还将设置一个私有Docker注册表。此外，我们会将Docker镜像推送到私有Docker注册表，然后从同一注册表中拉取镜像。

## 2. 私有和公共Docker注册表

Docker 提供了在私有服务器上创建、存储和管理Docker镜像的支持。此外，Docker 还有一个免费的公共注册表。Docker Hub 可以托管我们的镜像，但它们将公开可用。在大多数情况下，镜像包含运行应用程序所需的所有代码和配置。在这种情况下，我们可以使用Docker Hub私有帐户或在机器上设置私有Docker注册表。

Docker Hub 私人账户是付费的，它是在云中存储多个镜像的昂贵选择。虽然私有Docker注册表设置是免费的，但从私有注册表访问镜像的所有命令都很简单，几乎与Docker Hub中的命令相同。使用私有注册表，我们可以平衡负载、自定义身份验证和日志记录，并进行更多配置更改。它创建了一个自定义管道，有助于将镜像存储在个人位置。在这里，我们将简要介绍如何在服务器上私下管理镜像。

典型的Docker镜像包含应用程序代码、安装、配置和所需的依赖项。一个Docker镜像通常由多个层组成。我们还可以将这些层推送到私有或公共注册表。此外，我们将检查一些安全和存储选项，这些选项将允许我们自定义配置。使用这些，我们可以安全地管理镜像并快速安全地拉取和推送它们。

## 3. 设置私有注册表

我们可以通过将容器镜像集中在私有或公共注册表中来减少构建时间。我们还可以从包含所有应用程序组件的注册表中以捆绑形式下载压缩镜像，而不是在不同的环境中安装不同的依赖项。要设置私有Docker注册表，我们首先需要更改Docker守护进程的默认配置。

### 3.1. 配置私有Docker注册表

在Docker中，我们可以通过运行注册表镜像的容器来设置注册表。在继续之前，让我们先更新Docker安装的默认配置。

在/etc/docker/daemon.json添加如下配置：

```shell
{
    "insecure-registries":[
        "localhost:5000"
    ]
}
```

在上面的 JSON 中，我们在“ insecure-registries ”属性中添加了端口为5000 的本地主机。要应用上述更改，让我们使用命令行重新加载Docker守护进程：

```shell
$ sudo systemctl daemon-reload
```

现在，我们将重新启动Docker服务：

```shell
$ sudo systemctl restart docker
```

至此，我们已经成功配置了私有注册中心。

### 3.2. 运行私有Docker注册表

要运行私有注册表，我们必须拉取存储在公共Docker Hub上的[注册表镜像：](https://hub.docker.com/_/registry)

```shell
$ docker pull registry
Using default tag: latest
latest: Pulling from library/registry
2408cc74d12b: Pull complete 
...
fc30d7061437: Pull complete 
Digest: sha256:bedef0f1d248508fe0a16d2cacea1d2e68e899b2220e2258f1b604e1f327d475
Status: Downloaded newer image for registry:latest
docker.io/library/registry:latest
```

我们还可以提取特定版本的注册表。现在让我们使用docker images命令验证注册表镜像：

```shell
$ docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
registry            latest              773dbf02e42e        21 hours ago        24.1MB
```

现在，让我们使用注册表镜像运行一个Docker容器：

```shell
$ docker run -itd -p 5000:5000 --name baeldung-registry registry
e2d09cd3a5ef9c88e17e0393f7125b6eeffad175fa0ce69fa3daa7803a0b3067 
```

baeldung-registry容器的内部服务器使用端口5000。因此，我们暴露了宿主机的5000端口：

```shell
$ docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                    NAMES
e2d09cd3a5ef        registry            "/entrypoint.sh /etc…"   3 minutes ago       Up 2 minutes        0.0.0.0:5000->5000/tcp   baeldung-registry
```

上面的命令确认注册表已启动并正在运行。

## 4. 将镜像推送到私有镜像仓库

要将镜像推送到私有注册表，我们首先从公共Docker注册表中拉取最新的[centos镜像：](https://hub.docker.com/_/centos)

```shell
$ docker pull centos
Using default tag: latest
latest: Pulling from library/centos
a1d0c7532777: Pull complete 
Digest: sha256:a27fd8080b517143cbbbab9dfb7c8571c40d67d534bbdee55bd6c473f432b177
Status: Downloaded newer image for centos:latest
docker.io/library/centos:latest
```

在这里，我们提取了一个示例Docker镜像，我们可以将其推送到Docker私有注册表。首先，我们将标记centos镜像，然后将其推送到私有 docker 注册表。在这里，我们将其标记为localhost:5000/baeldung-centos：

```shell
$ docker tag centos:latest localhost:5000/baeldung-centos
```

现在，让我们检查主机上的所有镜像：

```shell
$ docker images
REPOSITORY                          TAG                 IMAGE ID            CREATED             SIZE
registry                            latest              773dbf02e42e        22 hours ago        24.1MB
localhost:5000/baeldung-centos   latest              5d0da3dc9764        8 months ago        231MB
centos                              latest              5d0da3dc9764        8 months ago        231MB
```

在这里，我们可以看到 imageId 5d0da3dc9764有两个不同的仓库。Docker 中的标签类似于符号[链接](https://www.baeldung.com/linux/symbolic-and-hard-links)。要删除镜像，我们必须删除 imageId 或明确删除这两个标签。

让我们检查一下将镜像推送到 docker private registry 的命令：

```shell
$ docker push localhost:5000/baeldung-centos
The push refers to repository [localhost:5000/baeldung-centos]
74ddd0ec08fa: Pushed 
latest: digest: sha256:a1801b843b1bfaf77c501e7a6d3f709401a1e0c83863037fa3aab063a7fdb9dc size: 529
```

通过上面的命令，我们已经成功将baeldung-centos镜像推送到私有镜像仓库，私有镜像仓库在本地设置在端口5000上。同样，我们也可以在私有注册表中存储多个镜像。

## 5. 从私有镜像仓库中拉取镜像

从私有注册表中拉取镜像的命令类似于从Docker Hub中拉取镜像。在这里，首先，我们将删除所有 imageId 为5d0da3dc9764的镜像：

```shell
$ docker rmi 5d0da3dc9764
```

让我们检查存储在主机上的所有镜像：

```shell
$ docker-registry]# docker images
REPOSITORY                       TAG                 IMAGE ID            CREATED             SIZE
registry                         latest              773dbf02e42e        22 hours ago        24.1MB
```

我们可以看到 imageId 为5d0da3dc9764的镜像已被删除。让我们检查一下从私有Docker注册表中拉取镜像的命令：

```shell
$ docker pull  localhost5000/baeldung-centos
Using default tag: latest
latest: Pulling from baeldung-centos
a1d0c7532777: Pull complete 
Digest: sha256:a1801b843b1bfaf77c501e7a6d3f709401a1e0c83863037fa3aab063a7fdb9dc
Status: Downloaded newer image for localhost:5000/baeldung-centos:latest
localhost:5000/baeldung-centos:latest
```

上面的命令将从私有注册表中拉取baeldung-centos镜像。

## 6. 为私有注册表设置身份验证

Docker 允许我们将镜像本地存储在中央服务器上，但有时，有必要保护镜像免受外部滥用。在这种情况下，我们需要使用基本的[htpasswd](https://httpd.apache.org/docs/2.4/programs/htpasswd.html)身份验证来验证注册表。

让我们首先创建一个单独的目录来存储Docker注册表凭据：

```shell
$ mkdir -p Docker_registry/auth
```

接下来，让我们运行一个httpd容器来创建一个带有密码的htpasswd保护用户：

```shell
$ cd Docker_registry &&docker run \
  --entrypoint htpasswd \
  httpd:2 -Bbn baeldung-user baeldung > auth/htpasswd
```

上面的命令将创建一个具有htpasswd身份验证密码的用户。凭据的详细信息存储在auth/htpasswd文件中。

现在，让我们使用auth/htpasswd身份验证文件运行相同的Docker注册表容器：

```shell
$ docker run -itd \
  -p 5000:5000 \
  --name registry \
  -v "$(pwd)"/auth:/auth \
  -e "REGISTRY_AUTH=htpasswd" \
  -e "REGISTRY_AUTH_HTPASSWD_REALM=Registry Realm" \
  -e REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd \
  registry
  3a497bafed4adb21a5a3f0b52307b4beaa261c6abe265e543cd8f5a15358e29d
```

由于Docker注册表使用基本身份验证运行，我们现在可以使用以下方法测试登录：

```shell
$ docker login  localhost:5000 -u baeldung-user -p baeldung
WARNING! Using --password via the CLI is insecure. Use --password-stdin.
WARNING! Your password will be stored unencrypted in /root/.docker/config.json.
Configure a credential helper to remove this warning. See
https://docs.docker.com/engine/reference/commandline/login/#credentials-store
Login Succeeded
```

一旦成功登录到Docker注册表，我们就可以按照上面讨论的相同方式推送和拉取镜像。

## 七、总结

本教程演示了如何创建我们自己的私有Docker注册表并推送Docker镜像。

首先，我们建立了一个私有注册表。后来，我们向注册表推送和拉取镜像。最后，我们在私有Docker注册表中启用了身份验证。
