## 1. 简介

在我们的项目中，我们经常使用[docker-compose]()来部署我们的容器化应用程序。如今，使用CI和CD，代码更改和部署非常频繁。因此，必须确保docker-compose始终使用最新的应用程序镜像。

在本教程中，我们介绍实现此目的的几种方法。

## 2. 显式拉取镜像

我们以一个简单的docker-compose文件为例：

```yaml
version: '2.4'
services:
    db:
        image: postgres
    my_app:
        image: "tuyucheng/test-app:latest"
        ports:
            - "8080:8080"
```

在这里，我们使用了两个服务，一个是PostgreSQL数据库，另一个是test-app。然后使用以下[命令](https://docs.docker.com/compose/reference/up/)来部署我们的应用程序：

```shell
$ docker-compose up -d
```

这会从远程仓库(比如说[DockerHub](https://hub.docker.com/))中拉取镜像并创建相应的容器。

当我们使用相同的命令重新部署我们的应用程序时，就会出现问题。由于镜像已经存在于本地仓库中，因此它不会检查远程仓库中这些镜像是否有任何更改。**因此，我们的选择是预先[拉取](https://docs.docker.com/compose/reference/pull/)docker-compose文件中的所有镜像**：

```shell
$ docker-compose pull
Pulling db     ... done
Pulling my_app ... done
```

此命令首先检查DockerHub中的两个镜像是否有可用的更新，然后，它只下载必要的层以使镜像与远程保持同步。

然而，我们可能并不总是喜欢拉取Postgres的镜像，如果我们使用数据库的稳定版本，镜像是不会有任何变化的。因此，为每一次部署下载它没有意义。有时，现有数据文件与新版本不兼容，这可能会破坏部署。在这种情况下，**我们可以在pull命令中仅使用特定服务的名称**：

```shell
$ docker-compose pull my_app
Pulling my_app ... done
```

**现在，如果我们执行up命令，这肯定会用最新的镜像重新创建容器**：

```shell
$ docker-compose up -d
Starting docker-test_db_1     ... done
Starting docker-test_my_app_1 ... done
```

当然，我们可以将这两个命令结合起来组成一个单独的命令：

```shell
$ docker-compose pull && docker-compose up -d
```

## 3. 移除本地镜像

以同一个例子为例，我们已经知道，需要不断拉取镜像的主要问题是本地镜像的存在。**因此，我们在这里的第二个方法是停止所有容器并从本地仓库中删除它们的镜像**：

```shell
$ docker-compose down --rmi all
Stopping docker-test_my_app_1 ... done
Removing docker-test_db_1     ... done
Removing docker-test_my_app_1 ... done
Removing network docker-test_default
Removing image postgres
Removing image tuyucheng/test-app:latest
```

[down](https://docs.docker.com/compose/reference/down/)命令用于停止并删除容器，–rmi选项从本地仓库中删除镜像，rmi类型local仅删除那些已在本地构建的镜像。因此，最好使用rmi类型all以确保删除当前配置使用的所有镜像。

**现在，我们使用up命令再次启动我们的容器**：

```shell
$ docker-compose up -d
Creating network "docker-test_default" with the default driver
Pulling db (postgres:)...
latest: Pulling from library/postgres
7d63c13d9b9b: Pull complete
cad0f9d5f5fe: Pull complete
...
Digest: sha256:eb83331cc518946d8ee1b52e6d9e97d0cdef6195b7bf25323004f2968e91a825
Status: Downloaded newer image for postgres:latest
Pulling my_app (tuyucheng/test-app:latest)...
latest: Pulling from tuyucheng/test-app
df5590a8898b: Already exists
705bb4cb554e: Already exists
...
Digest: sha256:31c05c8245192b32b8b359fc58b5e45d8397674ccf41f5f17a7d3109772ab5c1
Status: Downloaded newer image for tuyucheng/test-app:latest
Creating docker-test_db_1     ... done
Creating docker-test_my_app_1 ... done
```

我们可以看到，这不会再能够从本地仓库中找到镜像。因此，它从远程仓库中拉取最新镜像用于重新创建容器。

这种方法的缺点是你无法选择要删除的特定镜像。所以，它总是删除postgres镜像并再次下载相同的镜像，这是没有必要的。为了解决这个问题，我们可以使用docker rmi删除特定镜像：

```shell
$ docker rmi -f tuyucheng/test-app:latest
Untagged: tuyucheng/test-app:latest
Untagged: tuyucheng/test-app@sha256:31c05c8245192b32b8b359fc58b5e45d8397674ccf41f5f17a7d3109772ab5c1
Deleted: sha256:7bc07b4eb1c23f7a91afeb7133f107e0a8318fb77655d7d5f2f395a035a13eb7
```

## 4. 重新构建镜像

让我们看看具有不同风格的docker-compose文件的相同示例：

```yaml
version: '2.4'
services:
    db:
        image: postgres
    my_app:
        build: ./test-app
        ports:
            - "8080:8080"
```

在这里，我们使用了与Postgres相同的数据库服务。但是，对于服务my_app，我们提供了build部分，而不是使用现成的镜像。此部分包含test-app的构建上下文。驻留在test-app目录中的Dockerfile文件如下所示：

```dockerfile
FROM openjdk:11
COPY target/test-app-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

在这种情况下，当我们使用up命令重新部署时，docker-compose再次重用本地镜像(如果存在)。因此，**我们需要确保每次触发部署时docker-compose都会重新构建镜像。我们可以使用选项--build来做到这一点**：

```shell
$ docker-compose up -d --build
Building my_app
[+] Building 2.5s (8/8) FINISHED                                                                                            
 => [internal] load build definition from Dockerfile                                                                    0.0s
 => => transferring dockerfile: 41B                                                                                     0.0s
 => [internal] load .dockerignore                                                                                       0.0s
 => => transferring context: 2B                                                                                         0.0s
 => [internal] load metadata for docker.io/library/openjdk:11                                                           2.3s
 => [auth] library/openjdk:pull token for registry-1.docker.io                                                          0.0s
 => [1/2] FROM docker.io/library/openjdk:11@sha256:1b04b1958a4a61900feec994e3938a2a5d8f88db8ec9515f46a25cbe561b65d9     0.0s
 => [internal] load build context                                                                                       0.0s
 => => transferring context: 84B                                                                                        0.0s
 => CACHED [2/2] COPY target/test-app-1.0.0.jar app.jar                                                        0.0s
 => exporting to image                                                                                                  0.0s
 => => exporting layers                                                                                                 0.0s
 => => writing image sha256:4867a3f0b0a043cd54e16086e2d3c81dbf4c418806399f60fc7d7ffc094c7159                            0.0s
 => => naming to docker.io/library/docker-test_my_app                                                                   0.0s

Starting docker-test_db_1 ... 
Starting docker-test_db_1 ... done
```

**另一种方法是在运行up命令之前使用[build](https://docs.docker.com/compose/reference/build/)命令**：

```shell
$ docker-compose build --pull --no-cache
db uses an image, skipping
Building my_app
...                                                                                                           0.0s
 => => naming to docker.io/library/docker-test_my_app 
```

此命令提供了另外两个选项，--pull选项要求在构建镜像时从远程仓库中拉取Dockerfile的基础镜像。--no-cache选项表示跳过本地缓存。但是，它会跳过构建db服务，因为我们在那里使用了直接镜像。

**现在，如果我们重新启动我们的Compose配置，容器将使用最新的镜像**：

```shell
$ docker-compose up -d
Starting docker-test_db_1       ... done
Recreating docker-test_my_app_1 ... done
```

## 5. 总结

在本文中，我们了解了为什么docker-compose在部署时可能不使用最新的镜像，并学习了几种使docker-compose始终使用最新镜像重新创建容器的方法。