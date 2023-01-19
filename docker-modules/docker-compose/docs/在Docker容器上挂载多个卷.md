## 1. 概述

Docker有多种方式来持久化和共享正在运行的容器的数据。但是，我们可能需要为一个运行中的容器提供多个文件存储，例如，创建备份或授予不同的访问权限。或者对于同一个容器，我们可能需要添加命名卷并将它们绑定到特定路径。

**在本教程中，我们介绍如何在容器上装载多个卷，演示一些使用命令行和Docker Compose的示例**。

## 2. Docker容器上的多个挂载

Docker使用[Storage](https://docs.docker.com/storage/)来持久化数据，因此如果容器重新启动，我们不会丢失我们的信息。此外，如果我们想在集群环境中共享，数据的持久性是关键。

**我们将创建一些使用[Volumes](https://docs.docker.com/storage/volumes/)和[Bind mounts](https://docs.docker.com/storage/bind-mounts/)的示例，以突出显示最常见的开发用例**。

### 2.1 使用多个卷

首先，让我们[创建](https://docs.docker.com/engine/reference/commandline/volume_create/)两个不同的命名卷：

```shell
docker volume create --name first-volume-data && docker volume create --name second-volume-data
```

假设我们要为我们的Web应用程序装载两个不同的卷，但其中一个路径必须是只读的。如果我们使用命令行，我们可以使用-v选项：

```shell
docker run -d -p 8080:8080 -v first-volume-data:/container-path-1 -v second-volume-data:/container-path-2:ro --name web-app web-app:latest
```

**我们还可以使用匿名卷，例如，通过包含-v container-path。Docker会为我们创建它，但一旦我们删除容器，它就会被删除。**

让我们[检查](https://docs.docker.com/engine/reference/commandline/inspect/)我们的容器以检查我们的挂载是否正确：

```shell
docker inspect 0050cda73c6f
```

我们可以看到相关信息，例如源和目标、类型以及第二个数据卷的只读状态：

```yaml
"Mounts": [
    {
        "Type": "volume",
        "Name": "first-volume-data",
        "Source": "/var/lib/docker/volumes/first-volume-data/_data",
        "Destination": "/container-path-1",
        "Driver": "local",
        "Mode": "z",
        "RW": true,
        "Propagation": ""
    },
    {
        "Type": "volume",
        "Name": "second-volume-data",
        "Source": "/var/lib/docker/volumes/second-volume-data/_data",
        "Destination": "/container-path-2",
        "Driver": "local",
        "Mode": "z",
        "RW": false,
        "Propagation": ""
    }
]
```

同样，Docker推荐我们使用–mount选项：

```shell
docker run -d \
  --name web-app \
  -p 8080:8080 \
  --mount source=first-volume-data,target=/container-path-1 \
  --mount source=second-volume-data,target=/container-path-2,readonly \
  web-app:latest
```

**结果与-v选项相同。然而，除了更清晰之外，-mount是我们在[Swarm](https://docs.docker.com/engine/swarm/)模式下使用[服务](https://docs.docker.com/engine/swarm/how-swarm-mode-works/services/)卷的唯一方式**。

所以，如果我们想为我们的Web应用程序创建一个有多个挂载的[服务](https://docs.docker.com/engine/reference/commandline/service/)，我们需要使用–mount选项：

```shell
docker service create --name web-app-service \
  --replicas 3 \
  --publish published=8080,target=80 \
  --mount source=first-volume-data,target=/container-path-1 \
  --mount source=second-volume-data,target=/container-path-2,readonly \
  web-app
```

同样，我们可以检查我们的服务：

```bash
docker service inspect web-app-service
```

同样，我们可以在服务规范中获取有关容器的一些信息：

```yaml
"Mounts": [
    {
        "Type": "volume",
        "Source": "first-volume-data",
        "Target": "/container-path-1"
    },
    {
        "Type": "volume",
        "Source": "second-volume-data",
        "Target": "/container-path-2",
        "ReadOnly": true
    }
]
```

### 2.2 使用卷和绑定挂载

**我们可能还希望在挂载到主机中的特定文件夹或文件时使用卷**。

假设我们有一个[MySQL]()数据库镜像，我们需要运行一个初始脚本来创建一个Schema(数据库)或填充一些数据：

```bash
docker run -d \
  --name db \
  -p 3306:3306 \
  --mount source=first-volume-data,target=/var/lib/mysql \
  --mount type=bind,source=/init.sql,target=/docker-entrypoint-initdb.d/init.sql \
  mysql:latest
```

如果我们检查容器，现在可以看到两种不同的挂载类型：

```yaml
"Mounts": [
    {
        "Type": "volume",
        "Name": "first-volume-data",
        "Source": "/var/lib/docker/volumes/first-volume-data/_data",
        "Destination": "/var/lib/mysql",
        "Driver": "local",
        "Mode": "z",
        "RW": true,
        "Propagation": ""
    },
    {
        "Type": "bind",
        "Source": "/init.sql",
        "Destination": "/docker-entrypoint-initdb.d/init.sql",
        "Mode": "",
        "RW": true,
        "Propagation": "rprivate"
    }
]
```

### 2.3 使用多个绑定挂在

同样，我们可以使用多个绑定挂载，例如，当我们使用本地AWS模拟器[Localstack](https://docs.localstack.cloud/get-started/)时。

假设我们要启动一个本地S3服务：

```bash
docker run --name localstack -d \
  -p 4563-4599:4563-4599 -p 8055:8080 \
  -e SERVICES=s3 -e DEBUG=1 -e DATA_DIR=/tmp/localstack/data \
  -v /.localstack:/var/lib/localstack -v /var/run/docker.sock:/var/run/docker.sock \
  localstack/localstack
```

在检查容器时，我们可以看到在主机配置中有多个绑定：

```yaml
"Binds": [
    "/.localstack:/var/lib/localstack",
    "/var/run/docker.sock:/var/run/docker.sock"
]
```

## 3. Docker Compose

### 3.1 使用多个卷

首先，我们从两个卷开始，例如以下YAML文件：

```yaml
services:
    my_app:
        image: web-app:latest
        container_name: web-app
        ports:
            - "8080:8080"
        volumes:
            - first-volume-data:/container-path-1
            - second-volume-data:/container-path-2:ro

volumes:
    first-volume-data:
        driver: local
    second-volume-data:
        driver: local
```

一旦我们定义了之前创建的卷，我们就可以以named-volume:container-path的形式在服务中添加挂载。

[Long语法](https://docs.docker.com/compose/compose-file/#volumes)也可用于Docker Compose，例如：

```yaml
volumes:
    -   type: volume
        source: volume-data
        target: /container-path
```

### 3.2 使用卷和绑定挂载

同样，下面是一个使用MySQL服务的例子：

```yaml
services:
    mysql-db:
        image: mysql:latest
        environment:
            - MYSQL_ROOT_PASSWORD=password
            - MYSQL_ROOT_HOST=localhost
        ports:
            - '3306:3306'
        volumes:
            - db:/var/lib/mysql
            - ./init.sql:/docker-entrypoint-initdb.d/init.sql

volumes:
    db:
        driver: local
```

### 3.3 使用多个绑定挂在

最后，我们将之前的Localstack示例转换为Docker Compose：

```yaml
services:
    localstack:
        privileged: true
        image: localstack/localstack:latest
        container_name: localstack
        ports:
            - '4563-4599:4563-4599'
            - '8055:8080'
        environment:
            - SERVICES=s3
            - DEBUG=1
            - DATA_DIR=/tmp/localstack/data
        volumes:
            - './.localstack:/var/lib/localstack'
            - '/var/run/docker.sock:/var/run/docker.sock'
```

## 4. 总结

在本文中，我们介绍了如何使用Docker创建多个装载。以及绑定挂载和命名卷的一些组合，并演示了使用Docker命令行和Docker Compose的用例。
