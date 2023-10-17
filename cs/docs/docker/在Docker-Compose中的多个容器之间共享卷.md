---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

Docker容器是隔离的环境。然而，容器有时需要持久化和共享数据。当第二个容器需要访问共享缓存或使用数据库数据时，可能会发生这种情况。我们可能还需要对用户生成的数据进行备份或操作。

在这个简短的教程中，我们将通过使用Docker Compose的示例了解如何在Docker容器之间共享数据。

## 2. 使用Docker存储持久化和共享数据

当容器运行时，所有文件都会获得一个可写空间。但是，一旦我们停止容器，它们就不再存在。

[如果我们需要保存数据， Docker](https://docs.docker.com/)会使用具有持久性和内存选项的[存储。](https://docs.docker.com/storage/)

存储文件还可以提高性能，因为它直接写入主机文件系统，而不是使用容器的可写层。

### 2.1. Docker卷

让我们快速浏览一下[Docker卷](https://www.baeldung.com/ops/docker-volumes)。例如，让我们运行一个带有命名卷的 Nginx 容器。

首先，让我们[创建](https://docs.docker.com/engine/reference/commandline/volume_create/)我们的卷：

```shell
docker volume create --name volume-data
```

然后，让我们运行我们的容器：

```shell
docker run -d -v volume-data:/data --name nginx-test nginx:latest
```

在这种情况下，Docker 将挂载到容器的/data文件夹中。如果容器在要挂载的路径中有文件或目录，它还会将目录的内容复制到卷中。

我们还可以查看 用于持久存储的[绑定挂载](https://docs.docker.com/storage/bind-mounts/)。

### 2.2. 与卷共享数据

当多个容器需要访问共享数据时，它们可以使用同一个卷运行。

例如，让我们启动我们的 Web 应用程序：

```shell
docker run -d -v volume-data:/usr/src/app/public --name our-web-app web-app:latest
```

Docker默认创建一个本地卷。但是，我们可以使用[volume diver](https://docs.docker.com/storage/volumes/#use-a-volume-driver) 在多台机器之间共享数据。

最后，Docker 也有[–volumes-from](https://docs.docker.com/storage/volumes/#backup-restore-or-migrate-data-volumes)来连接正在运行的容器之间的卷。它可能有助于数据共享或更一般的备份使用。

## 3. 使用Docker Compose共享数据

我们已经了解了如何使用Docker创建卷。[Docker Compose](https://www.baeldung.com/ops/docker-compose)还支持YAML 模板定义中的[volumes关键字。](https://docs.docker.com/compose/compose-file/compose-file-v3/#volume-configuration-reference)

让我们创建一个docker-compose.yml来运行一个 Nginx 容器和我们的 Web 应用程序共享同一个卷：

```yaml
services:
  nginx:
    container_name: nginx
    build: ./nginx/
    volumes:
      - shared-volume:/usr/src/app

  web-app:
    container_name: web-app
    env_file: .env
    volumes:
      - shared-volume:/usr/src/app/public
    environment:
      - NODE_ENV=production

volumes:
  shared-volume:
```

同样，在Docker Compose中，默认驱动程序将是local。我们还可以指定用于该卷的驱动程序：

```yaml
volumes:
  db:
    driver: some-driver
```

我们可能还需要使用Docker Compose外部的卷：

```yaml
volumes:
  data:
    external: true
    name: shared-data
```

## 4. 总结

在本文中，我们了解了如何使用卷共享Docker容器的数据。我们还通过一个使用Docker Compose的简单示例看到了相同的概念。
