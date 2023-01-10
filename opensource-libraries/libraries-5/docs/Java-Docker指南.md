## 1. 概述

在本文中，我们将了解另一个成熟的特定于平台的 API—— [Docker 的JavaAPI 客户端](https://github.com/docker-java/docker-java)。

在整篇文章中，我们了解了如何连接正在运行的 Docker 守护程序的方式，以及 API 为Java开发人员提供了哪些类型的重要功能。

## 2.Maven依赖

首先，我们需要将主要依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>com.github.docker-java</groupId>
    <artifactId>docker-java</artifactId>
    <version>3.0.14</version>
</dependency>
```

在撰写本文时，API 的最新版本是 3.0.14。可以从[GitHub 发布页面](https://github.com/docker-java/docker-java/releases)或[Maven 存储库](https://search.maven.org/classic/#search|gav|1|g%3A"com.github.docker-java" AND a%3A"docker-java")查看每个版本。

## 3. 使用 Docker 客户端

DockerClient是我们可以在 Docker 引擎/守护进程和我们的应用程序之间建立连接的地方。

默认情况下，Docker 守护进程只能在unix:///var/run/docker.sock文件中访问。除非另有配置，否则我们可以在本地与侦听 Unix 套接字的 Docker 引擎通信。

在这里，我们通过接受默认设置向DockerClientBuilder类申请创建连接：

```java
DockerClient dockerClient = DockerClientBuilder.getInstance().build();
```

同样，我们可以分两步打开一个连接：

```java
DefaultDockerClientConfig.Builder config 
  = DefaultDockerClientConfig.createDefaultConfigBuilder();
DockerClient dockerClient = DockerClientBuilder
  .getInstance(config)
  .build();
```

由于引擎可以依赖其他特性，因此客户端也可以根据不同的条件进行配置。

例如，构建器接受服务器 URL，也就是说，如果引擎在端口 2375 上可用，我们可以更新连接值：

```java
DockerClient dockerClient
  = DockerClientBuilder.getInstance("tcp://docker.baeldung.com:2375").build();
```

请注意，我们需要根据连接类型在连接字符串前加上unix://或tcp:// 。

如果我们更进一步，我们可以使用DefaultDockerClientConfig类得到更高级的配置：

```java
DefaultDockerClientConfig config
  = DefaultDockerClientConfig.createDefaultConfigBuilder()
    .withRegistryEmail("info@baeldung.com")
    .withRegistryPassword("baeldung")
    .withRegistryUsername("baeldung")
    .withDockerCertPath("/home/baeldung/.docker/certs")
    .withDockerConfig("/home/baeldung/.docker/")
    .withDockerTlsVerify("1")
    .withDockerHost("tcp://docker.baeldung.com:2376").build();

DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
```

同样，我们可以使用Properties执行相同的方法：

```java
Properties properties = new Properties();
properties.setProperty("registry.email", "info@baeldung.com");
properties.setProperty("registry.password", "baeldung");
properties.setProperty("registry.username", "baaldung");
properties.setProperty("DOCKER_CERT_PATH", "/home/baeldung/.docker/certs");
properties.setProperty("DOCKER_CONFIG", "/home/baeldung/.docker/");
properties.setProperty("DOCKER_TLS_VERIFY", "1");
properties.setProperty("DOCKER_HOST", "tcp://docker.baeldung.com:2376");

DefaultDockerClientConfig config
  = DefaultDockerClientConfig.createDefaultConfigBuilder()
    .withProperties(properties).build();

DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
```

除非我们在源代码中配置引擎的设置，否则另一种选择是设置相应的环境变量，这样我们就可以只考虑项目中DockerClient的默认实例化：

```bash
export DOCKER_CERT_PATH=/home/baeldung/.docker/certs
export DOCKER_CONFIG=/home/baeldung/.docker/
export DOCKER_TLS_VERIFY=1
export DOCKER_HOST=tcp://docker.baeldung.com:2376
```

## 4.容器管理

API 允许我们对容器管理有多种选择。让我们来看看他们中的每一个。

### 4.1. 列出容器

现在我们已经建立了连接，我们可以列出位于 Docker 主机上的所有正在运行的容器：

```java
List<Container> containers = dockerClient.listContainersCmd().exec();
```

如果显示正在运行的容器不符合需要，我们可以使用提供的选项来查询容器。

在这种情况下，我们显示状态为“已退出”的容器：

```java
List<Container> containers = dockerClient.listContainersCmd()
  .withShowSize(true)
  .withShowAll(true)
  .withStatusFilter("exited").exec()
```

它相当于：

```bash
$ docker ps -a -s -f status=exited
# or 
$ docker container ls -a -s -f status=exited
```

### 4.2. 创建容器

创建容器由createContainerCmd方法提供。我们可以使用以“ with”前缀开头的可用方法来声明更复杂的声明。

假设我们有一个docker create命令定义了一个依赖于主机的 MongoDB 容器，在内部侦听端口 27017：

```java
$ docker create --name mongo 
  --hostname=baeldung 
  -e MONGO_LATEST_VERSION=3.6 
  -p 9999:27017 
  -v /Users/baeldung/mongo/data/db:/data/db 
  mongo:3.6 --bind_ip_all
```

我们能够以编程方式引导同一个容器及其配置：

```java
CreateContainerResponse container
  = dockerClient.createContainerCmd("mongo:3.6")
    .withCmd("--bind_ip_all")
    .withName("mongo")
    .withHostName("baeldung")
    .withEnv("MONGO_LATEST_VERSION=3.6")
    .withPortBindings(PortBinding.parse("9999:27017"))
    .withBinds(Bind.parse("/Users/baeldung/mongo/data/db:/data/db")).exec();
```

### 4.3. 启动、停止和终止容器

创建容器后，我们可以分别通过名称或 ID 启动、停止和终止它：

```java
dockerClient.startContainerCmd(container.getId()).exec();

dockerClient.stopContainerCmd(container.getId()).exec();

dockerClient.killContainerCmd(container.getId()).exec();
```

### 4.4. 检查容器

inspectContainerCmd方法采用String参数，该参数指示容器的名称或 ID。使用此方法，我们可以直接观察容器的元数据：

```java
InspectContainerResponse container 
  = dockerClient.inspectContainerCmd(container.getId()).exec();
```

### 4.5. 快照容器

与docker commit命令类似，我们可以使用commitCmd方法创建一个新镜像。

在我们的示例中，场景是，我们之前运行了一个 alpine:3.6 容器，其 id 为“3464bb547f88”，并在其上安装了git。

现在，我们要从容器中创建一个新的镜像快照：

```java
String snapshotId = dockerClient.commitCmd("3464bb547f88")
  .withAuthor("Baeldung <info@baeldung.com>")
  .withEnv("SNAPSHOT_YEAR=2018")
  .withMessage("add git support")
  .withCmd("git", "version")
  .withRepository("alpine")
  .withTag("3.6.git").exec();
```

由于我们与git捆绑在一起的新图像保留在主机上，我们可以在 Docker 主机上搜索它：

```bash
$ docker image ls alpine --format "table {{.Repository}} {{.Tag}}"
REPOSITORY TAG
alpine     3.6.git
```

## 5.图像管理

我们得到了一些适用的命令来管理图像操作。

### 5.1. 列出图像

要列出 Docker 主机上所有可用的图像，包括悬挂图像，我们需要应用listImagesCmd方法：

```java
List<Image> images = dockerClient.listImagesCmd().exec();
```

如果我们的 Docker 主机上有两个图像，我们应该在运行时获取它们的图像对象。我们寻找的图像是：

```bash
$ docker image ls --format "table {{.Repository}} {{.Tag}}"
REPOSITORY TAG
alpine     3.6
mongo      3.6
```

接下来，要查看中间图像，我们需要明确请求它：

```java
List<Image> images = dockerClient.listImagesCmd()
  .withShowAll(true).exec();
```

如果只显示悬挂图片，则必须考虑withDanglingFilter方法：

```java
List<Image> images = dockerClient.listImagesCmd()
  .withDanglingFilter(true).exec();
```

### 5.2. 构建图像

让我们关注使用 API 构建图像的方式。buildImageCmd方法从Dockerfile构建 Docker 镜像。在我们的项目中，我们已经有一个 Dockerfile，它提供了一个安装了 git 的 Alpine 镜像：

```plaintext
FROM alpine:3.6

RUN apk --update add git openssh && 
  rm -rf /var/lib/apt/lists/ && 
  rm /var/cache/apk/

ENTRYPOINT ["git"]
CMD ["--help"]
```

新镜像将在不使用缓存的情况下构建，并且在开始构建过程之前，无论如何，Docker 引擎将尝试拉取更新版本的alpine:3.6。如果一切顺利，我们最终应该会看到具有给定名称 alpine:git 的图像：

```java
String imageId = dockerClient.buildImageCmd()
  .withDockerfile(new File("path/to/Dockerfile"))
  .withPull(true)
  .withNoCache(true)
  .withTag("alpine:git")
  .exec(new BuildImageResultCallback())
  .awaitImageId();
```

### 5.3. 检查图像

由于inspectImageCmd方法，我们可以检查有关图像的低级信息：

```java
InspectImageResponse image 
  = dockerClient.inspectImageCmd("161714540c41").exec();
```

### 5.4. 标记图像

使用docker tag命令向我们的图像添加标签非常简单，因此 API 也不例外。我们也可以使用tagImageCmd方法实现相同的目的。使用 git 将 ID 为161714540c41的 Docker 镜像标记到 baeldung/alpine 存储库中：

```java
String imageId = "161714540c41";
String repository = "baeldung/alpine";
String tag = "git";

dockerClient.tagImageCmd(imageId, repository, tag).exec();
```

我们将列出新创建的图像，它是：

```bash
$ docker image ls --format "table {{.Repository}} {{.Tag}}"
REPOSITORY      TAG
baeldung/alpine git
```

### 5.5. 推送图像

在将图像发送到注册表服务之前，docker 客户端必须配置为与该服务合作，因为与注册表的合作需要提前进行身份验证。

由于我们假设客户端配置了 Docker Hub，我们可以将baeldung/alpine镜像推送到 baeldung DockerHub 帐户：

```java
dockerClient.pushImageCmd("baeldung/alpine")
  .withTag("git")
  .exec(new PushImageResultCallback())
  .awaitCompletion(90, TimeUnit.SECONDS);
```

我们必须遵守进程的持续时间。在示例中，我们等待 90 秒。

### 5.6. 拉取镜像

要从注册表服务下载图像，我们使用pullImageCmd方法。此外，如果图像是从私有注册表中提取的，则客户端必须知道我们的凭据，否则该过程将以失败告终。和拉取镜像一样，我们指定一个回调和一个固定的周期来拉取镜像：

```java
dockerClient.pullImageCmd("baeldung/alpine")
  .withTag("git")
  .exec(new PullImageResultCallback())
  .awaitCompletion(30, TimeUnit.SECONDS);
```

拉取后查看Docker主机上是否存在上述镜像：

```bash
$ docker images baeldung/alpine --format "table {{.Repository}} {{.Tag}}"
REPOSITORY      TAG
baeldung/alpine git
```

### 5.7. 删除图像

其余的另一个简单函数是removeImageCmd方法。我们可以删除带有短 ID 或长 ID 的图像：

```java
dockerClient.removeImageCmd("beaccc8687ae").exec();
```

### 5.8. 在注册表中搜索

要从 Docker Hub 搜索图像，客户端会附带searchImagesCmd方法，该方法采用表示术语的字符串值。在这里，我们探索与Docker Hub 中包含“ Java”的名称相关的图像：

```java
List<SearchItem> items = dockerClient.searchImagesCmd("Java").exec();
```

输出返回SearchItem对象列表中的前 25 个相关图像。

## 6.音量管理

如果Java项目需要与 Docker 进行卷交互，我们也应该考虑这一部分。简要地，我们看看 DockerJavaAPI 提供的卷的基本技术。

### 6.1. 列出卷

包括命名和未命名的所有可用卷都列在：

```java
ListVolumesResponse volumesResponse = dockerClient.listVolumesCmd().exec();
List<InspectVolumeResponse> volumes = volumesResponse.getVolumes();
```

### 6.2. 检查卷

inspectVolumeCmd方法是显示卷详细信息的形式。我们通过指定其短 ID 来检查卷：

```java
InspectVolumeResponse volume 
  = dockerClient.inspectVolumeCmd("0220b87330af5").exec();
```

### 6.3. 创建卷

API 提供两种不同的选项来创建卷。非参数createVolumeCmd方法创建一个卷，名称由 Docker 提供：

```java
CreateVolumeResponse unnamedVolume = dockerClient.createVolumeCmd().exec();
```

与其使用默认行为，不如使用名为withName的辅助方法让我们为卷设置名称：

```java
CreateVolumeResponse namedVolume 
  = dockerClient.createVolumeCmd().withName("myNamedVolume").exec();
```

### 6.4. 删除卷

我们可以使用removeVolumeCmd方法直观地从 Docker 主机中删除一个卷。需要注意的重要一点是，如果卷正在从容器中使用，我们将无法删除它。我们从卷列表中删除卷myNamedVolume：

```java
dockerClient.removeVolumeCmd("myNamedVolume").exec();
```

## 七、网络管理

我们的最后一部分是关于使用 API 管理网络任务。

### 7.1. 列出网络

我们可以使用以 list 开头的传统 API 方法之一显示网络单元列表：

```java
List<Network> networks = dockerClient.listNetworksCmd().exec();
```

### 7.2. 创建网络

与docker network create命令等效的是使用createNetworkCmd方法执行的。如果我们有三十方或自定义网络驱动程序，除了内置驱动程序之外， withDriver方法还可以接受它们。在我们的例子中，让我们创建一个名为baeldung的桥接网络：

```java
CreateNetworkResponse networkResponse 
  = dockerClient.createNetworkCmd()
    .withName("baeldung")
    .withDriver("bridge").exec();
```

此外，使用默认设置创建网络单元并不能解决问题，我们可以申请其他辅助方法来构建高级网络。因此，要使用自定义值覆盖默认子网：

```java
CreateNetworkResponse networkResponse = dockerClient.createNetworkCmd()
  .withName("baeldung")
  .withIpam(new Ipam()
    .withConfig(new Config()
    .withSubnet("172.36.0.0/16")
    .withIpRange("172.36.5.0/24")))
  .withDriver("bridge").exec();
```

我们可以使用docker命令运行的相同命令是：

```bash
$ docker network create 
  --subnet=172.36.0.0/16 
  --ip-range=172.36.5.0/24 
  baeldung
```

### 7.3. 检查网络

API 中还介绍了显示网络的低级详细信息：

```java
Network network 
  = dockerClient.inspectNetworkCmd().withNetworkId("baeldung").exec();
```

### 7.4. 删除网络

我们可以使用removeNetworkCmd方法安全地删除带有名称或 ID 的网络单元：

```java
dockerClient.removeNetworkCmd("baeldung").exec();
```

## 八. 总结

在这个广泛的教程中，我们探索了Java Docker API Client的各种不同功能，以及部署和管理场景的几种实现方法。

本文中说明的所有示例都可以[在 GitHub 上找到](https://github.com/eugenp/tutorials/tree/master/libraries-5)。

### 通过Learn Spring课程开始使用 Spring 5 和Spring Boot2 ：

[>> 查看课程](https://www.baeldung.com/ls-course-end)