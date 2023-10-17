---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 一、概述

在本教程中，我们会将Docker容器部署到Kubernetes并了解如何为这些容器使用本地镜像。我们将使用[Minikube](https://minikube.sigs.k8s.io/docs/)来运行Kubernetes集群。

## 2.文件

首先，我们需要一个[Dockerfile](https://docs.docker.com/engine/reference/builder/)来创建本地Docker镜像。这应该很简单，因为我们将专注于 Minikube 命令。

让我们用一个打印消息的echo命令创建一个Dockerfile：

```plaintext
FROM alpine 

CMD ["echo", "Hello World"]
```

## 3. docker-env命令

对于第一种方法，我们需要确保安装了[Docker CLI](https://docs.docker.com/engine/reference/commandline/cli/)。这是一个用于管理Docker资源(例如镜像和容器)的工具。

默认情况下，它在我们的机器上使用Docker引擎，但我们可以轻松更改它。我们将使用它并将我们的DockerCLI 指向 Minikube 中的Docker引擎。

让我们检查这个先决条件，看看DockerCLI 是否正常工作：

```shell
$ docker version
```

输出应与此类似：

```plaintext
Client: Docker Engine - Community
 Version:           19.03.12
 ...

Server: Docker Engine - Community
 Engine:
  Version:          19.03.12
  ...
```

让我们继续接下来的步骤。我们可以配置此 CLI 以使用 Minikube 中的Docker引擎。这样，我们就可以列出 Minikube 中可用的镜像，甚至可以在其中构建镜像。

让我们看看[配置DockerCLI 所需的步骤](https://minikube.sigs.k8s.io/docs/commands/docker-env/)：

```shell
$ minikube docker-env
```

我们可以在这里看到命令列表：

```shell
export DOCKER_TLS_VERIFY="1"
export DOCKER_HOST="tcp://172.22.238.61:2376"
export DOCKER_CERT_PATH="C:\Users\Baeldung\.minikube\certs"
export MINIKUBE_ACTIVE_DOCKERD="minikube"

# To point your shell to minikube's docker-daemon, run:
# eval $(minikube -p minikube docker-env)
```

让我们执行最后一行的命令，因为它将为我们进行配置：

```shell
$ eval $(minikube -p minikube docker-env)
```

现在，我们可以使用DockerCLI 来调查 Minikube 内部的Docker环境。

[让我们使用minikube image ls](https://minikube.sigs.k8s.io/docs/commands/image/#minikube-image-ls)命令列出可用的镜像：

```shell
$ minikube image ls --format table
```

这将打印一个包含镜像的表格：

```plaintext
|-----------------------------------------|---------|---------------|--------|
|                  Image                  |   Tag   |   Image ID    |  Size  |
|-----------------------------------------|---------|---------------|--------|
| docker.io/kubernetesui/dashboard        | <none>  | 1042d9e0d8fcc | 246MB  |
| docker.io/kubernetesui/metrics-scraper  | <none>  | 115053965e86b | 43.8MB |
| k8s.gcr.io/etcd                         | 3.5.3-0 | aebe758cef4cd | 299MB  |
| k8s.gcr.io/pause                        | 3.7     | 221177c6082a8 | 711kB  |
| k8s.gcr.io/coredns/coredns              | v1.8.6  | a4ca41631cc7a | 46.8MB |
| k8s.gcr.io/kube-controller-manager      | v1.24.3 | 586c112956dfc | 119MB  |
| k8s.gcr.io/kube-scheduler               | v1.24.3 | 3a5aa3a515f5d | 51MB   |
| k8s.gcr.io/kube-proxy                   | v1.24.3 | 2ae1ba6417cbc | 110MB  |
| k8s.gcr.io/pause                        | 3.6     | 6270bb605e12e | 683kB  |
| gcr.io/k8s-minikube/storage-provisioner | v5      | 6e38f40d628db | 31.5MB |
| k8s.gcr.io/echoserver                   | 1.4     | a90209bb39e3d | 140MB  |
| k8s.gcr.io/kube-apiserver               | v1.24.3 | d521dd763e2e3 | 130MB  |
|-----------------------------------------|---------|---------------|--------|
```

[如果我们将它与docker image ls](https://docs.docker.com/engine/reference/commandline/image_ls/)命令的输出进行比较，我们会发现两者都显示相同的列表。这意味着我们的DockerCLI 已正确配置。

让我们使用我们的Dockerfile并从中[构建一个镜像：](https://docs.docker.com/engine/reference/commandline/build/)

```shell
$ docker build -t first-image -f ./Dockerfile .
```

现在它在 Minikube 中可用，我们可以创建一个使用此镜像的 pod：

```shell
$ kubectl run first-container --image=first-image --image-pull-policy=Never --restart=Never
```

让我们检查一下这个 pod 的日志：

```shell
$ kubectl logs first-container
```

我们可以看到预期的“Hello World”消息。一切正常。让我们关闭终端以确保我们的DockerCLI 没有连接到下一个示例的 Minikube。

## 4. Minikube 镜像加载命令

让我们看看另一种使用本地镜像的方法。这一次，我们将在我们的机器上在 Minikube 之外构建Docker镜像并将其加载到 Minikube 中。让我们构建镜像：

```shell
$ docker build -t second-image -f ./Dockerfile .
```

现在镜像已经存在，但它在 Minikube 中还不可用。让我们[加载它](https://minikube.sigs.k8s.io/docs/commands/image/#minikube-image-load)：

```shell
$ minikube image load second-image
```

让我们列出镜像并检查它是否可用：

```shell
$ minikube image ls --format table
```

我们可以在列表中看到新镜像。这意味着我们可以创建 pod：

```shell
$ kubectl run second-container --image=second-image --image-pull-policy=Never --restart=Never
```

容器启动成功。让我们检查日志：

```shell
$ kubectl logs second-container
```

我们可以看到它打印了正确的消息。

## 5. Minikube 镜像构建命令

在前面的示例中，我们将预构建的Docker镜像加载到 Minikube。然而，我们也可以在 Minikube 中构建我们的镜像。

让我们使用相同的Dockerfile并构建一个新的Docker镜像：

```shell
$ minikube image build -t third-image -f ./Dockerfile .
```

现在镜像在 Minikube 中可用，我们可以用它启动一个容器：

```shell
$ kubectl run third-container --image=third-image --image-pull-policy=Never --restart=Never
```

让我们检查日志以确保其正常工作：

```shell
$ kubectl logs third-container
```

它按预期打印“Hello World”消息。

## 六，总结

在本文中，我们使用了三种不同的方式在 Minikube 中运行本地Docker镜像。

首先，我们配置DockerCLI 以连接到 Minikube 内的Docker引擎。然后，我们看到了加载预构建镜像和直接在 Minikube 中构建镜像的两条命令。
