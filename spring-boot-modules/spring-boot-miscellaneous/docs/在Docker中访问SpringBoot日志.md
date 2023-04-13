## 1. 概述

在本教程中，我们将解释如何在Docker中访问Spring Boot日志，从本地开发到可持续的多容器解决方案。

## 2. 基本控制台输出

首先，让我们构建[之前文章](https://www.baeldung.com/spring-boot-docker-images)中的Spring Boot Docker镜像：

```shell
$> mvn spring-boot:build-image
```

然后，**当我们运行容器时，我们可以立即在控制台中看到标准输出日志**：

```shell
$> docker run --name=demo-container docker.io/library/spring-boot-docker:1.0.0
Setting Active Processor Count to 1
WARNING: Container memory limit unset. Configuring JVM for 1G container.
```

此命令遵循Linux shell tail -f命令之类的日志。

现在，让我们通过向application.properties文件添加一行来使用日志文件appender配置我们的Spring Boot应用程序：

```properties
logging.file.path=logs
```

然后，我们可以通过在我们正在运行的容器中运行tail -f命令来获得相同的结果：

```shell
$> docker exec -it demo-container tail -f /workspace/logs/spring.log > $HOME/spring.log
Setting Active Processor Count to 1
WARNING: Container memory limit unset. Configuring JVM for 1G container.
```

这就是单容器解决方案。在接下来的章节中，我们将学习如何分析组合容器的日志历史记录和日志输出。

## 3. 日志文件的Docker卷

**如果我们必须从主机文件系统访问日志文件，则必须创建一个Docker卷**。

为此，我们可以使用以下命令运行我们的应用程序容器：

```shell
$> mvn spring-boot:build-image -v /path-to-host:/workspace/logs
```

然后，我们可以在/path-to-host目录下看到spring.log文件。

从我们[之前关于Docker Compose的文章](https://www.baeldung.com/docker-compose)开始，我们可以从一个Docker Compose文件运行多个容器。

如果我们使用的是Docker Compose文件，我们应该添加卷配置：

```yaml
network-example-service-available-to-host-on-port-1337:
image: karthequian/helloworld:latest
container_name: network-example-service-available-to-host-on-port-1337
volumes:
    - /path-to-host:/workspace/logs
```

然后，让我们运行Compose文件：

```shell
$> docker-compose up
```

日志文件位于/path-to-host目录中。

现在我们已经了解了基本的解决方案，让我们探索更高级的docker logs命令。

**在接下来的章节中，我们假设我们的Spring Boot应用程序配置为将日志打印到标准输出**。

## 4. 多个容器的Docker日志

一旦我们同时运行多个容器，我们将无法再从多个容器中读取混合日志。

在Docker Compose文档中我们可以发现，容器默认设置了json-file日志驱动，它支持docker logs命令。

让我们看看它如何与我们的[Docker Compose示例](https://www.baeldung.com/docker-compose)一起工作。

首先，让我们找到我们的容器ID：

```shell
$> docker ps
CONTAINER ID        IMAGE                           COMMAND                  
877bb028a143        karthequian/helloworld:latest   "/runner.sh nginx"           
```

然后，**我们可以使用docker logs -f命令显示我们的容器日志**。我们可以看到，尽管有json-file驱动程序，但输出仍然是纯文本-JSON仅在Docker内部使用：

```shell
$> docker logs -f 877bb028a143
172.27.0.1 - - [22/Oct/2020:11:19:52 +0000] "GET / HTTP/1.1" 200 4369 "
172.27.0.1 - - [22/Oct/2020:11:19:52 +0000] "GET / HTTP/1.1" 200 4369 "
```

-f选项的行为类似于tail -f shell命令：它在生成时回显日志输出。

请注意，**如果我们在Swarm模式下运行容器，我们应该改用docker service ps和docker service logs命令**。

在[文档](https://docs.docker.com/config/containers/logging/configure/#limitations-of-logging-drivers)中，我们可以看到docker logs命令支持有限的输出选项：json-file、local或journald。

## 5. 用于日志聚合服务的Docker驱动程序

docker logs命令对于即时查看特别有用：它不提供复杂的过滤器或长期统计数据。

为此，**Docker支持多种[日志聚合服务驱动程序](https://docs.docker.com/config/containers/logging/configure/#supported-logging-drivers)**。正如我们在[之前的文章](https://www.baeldung.com/graylog-with-spring-boot)中研究Graylog时，我们将为该平台配置适当的驱动程序。

**对于daemon.json文件中的主机，此配置可以是全局的**。它位于Linux主机上的/etc/docker或Windows服务器上的C:\ProgramData\docker\config中。

请注意，如果daemon.json文件不存在，我们应该创建它：

```json
{
    "log-driver": "gelf",
    "log-opts": {
        "gelf-address": "udp://1.2.3.4:12201"
    }
}
```

Graylog驱动程序称为GELF-我们只需指定Graylog实例的IP地址。

**我们也可以在运行单个容器时覆盖此配置**：

```shell
$> docker run \
      --log-driver gelf –-log-opt gelf-address=udp://1.2.3.4:12201 \
      alpine echo hello world
```

## 6. 总结

在本文中，我们回顾了在Docker中访问Spring Boot日志的不同方法。

记录到标准输出使得从单容器执行中观察日志变得非常容易。

但是，如果我们想从Docker日志记录功能中受益，使用文件附加器并不是最佳选择，因为容器没有与适当服务器相同的约束。