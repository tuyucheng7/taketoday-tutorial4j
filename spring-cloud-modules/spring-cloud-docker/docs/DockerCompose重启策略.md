## 1. 概述

在本教程中，我们将学习如何在[Docker Compose](https://www.baeldung.com/ops/docker-compose)中使用重启策略。

首先，我们将介绍如何使用重启策略重启Docker容器。然后我们将介绍Docker Compose如何在正常模式和集群模式下定义重启策略作为多容器Docker应用程序的配置。

## 2. Docker重启策略

**重启策略是我们可以用来自动重启Docker容器并管理其生命周期的策略**。

**考虑到容器可能会意外失败，Docker有保护措施来防止服务进入重启循环**。如果发生故障，除非容器成功运行至少10秒，否则重启策略不会生效。

我们还可以假设手动停止容器将使Docker在提供重启策略时自动重启服务。但是，在这种情况下，这些策略将被取消，以防止容器在任意停止后重新启动。

要使用重启策略，Docker提供了以下选项：

-   no：容器不会自动重启
-   on-failure[:max-retries]：如果容器以非0退出代码退出，则重新启动容器，并为Docker守护进程提供重新启动容器的最大尝试次数
-   always：如果容器停止，则始终重新启动容器
-   unless-stopped：总是重新启动容器，除非它被任意停止，或者被Docker守护进程停止

现在让我们看一个如何使用Docker CLI为单个容器设置重启策略的示例：

```shell
docker run --restart always my-service
```

**从上面的示例中，如果容器停止运行，my-service将始终重新启动**。但是，如果我们显式停止容器，则重启策略只会在Docker守护进程重启时生效，或者当我们使用[restart命令](https://www.baeldung.com/ops/docker-compose-restart-container)时。

前面的示例演示了restart标志如何配置自动重启单个容器的策略。但是，**Docker Compose允许我们在正常模式或swarm模式下使用restart或restart_policy属性配置重启策略来管理多个容器**。

## 3. 设置

在深入研究使用Docker Compose实现重启策略之前，让我们设置一个工作环境。

我们必须有一个正在运行的Docker容器来测试我们将指定的重启策略。我们将使用上一篇文章中的项目[spring-cloud-docker](https://github.com/eugenp/tutorials/tree/master/spring-cloud-modules/spring-cloud-docker)，它是一个容器化的Spring Boot应用程序。这个项目有两个Docker服务，我们将使用这些服务通过Docker Compose实现不同的重启策略。

首先，我们必须通过从项目根目录运行以下命令来确认我们可以运行这两个容器：

```shell
docker-compose up --detach --build
```

现在我们应该能够通过执行docker-compose ps看到这两个服务正在运行：

```shell
$ docker ps
     Name                   Command              State            Ports         
--------------------------------------------------------------------------------
message-server   java -jar /message-server.jar   Up      0.0.0.0:18888->8888/tcp
product-server   java -jar /product-server.jar   Up      0.0.0.0:19999->9999/tcp
```

或者，我们可以在浏览器中转到localhost:18888或localhost:19999并验证我们是否看到应用程序服务显示的消息。

## 4. Docker Compose中的重启策略

**与restart Docker命令一样，Docker Compose包含restart属性以自动重启容器**。

我们还可以通过在docker-compose.yml文件中为服务提供restart属性来在Docker Compose中定义重启策略。D**ocker Compose使用Docker CLI restart命令提供的相同值来定义重启策略**。

现在让我们为我们的容器创建一个重启策略。在spring-cloud-docker项目中，我们必须通过添加重启策略属性来更改docker-compose.yml配置文件：

```yaml
message-server:
    container_name: message-server
    build:
        context: docker-message-server
        dockerfile: Dockerfile
    image: message-server:latest
    ports:
        - 18888:8888
    networks:
        - spring-cloud-network
    restart: no
product-server:
    container_name: product-server
    build:
        context: docker-product-server
        dockerfile: Dockerfile
    image: product-server:latest
    ports:
        - 19999:9999
    networks:
        - spring-cloud-network
    restart: on-failure
```

请注意我们是如何为这两个服务添加restart属性的。在这种情况下，message-server永远不会自动重启。product-server只有在它以on-failure指定的非零代码退出时才会重新启动。

接下来，让我们看看如何在swarm模式下使用Docker Compose声明相同的策略。

## 5. Docker Compose Swarm模式下的重启策略

**集群模式下的Docker Compose在指定容器如何自动重启时提供了更多选项。但是，以下实现仅适用于Docker Compose v3，它在配置中引入了deploy键值对**。

下面我们可以找到不同的属性来进一步扩展swarm模式下重启策略的配置：

-   condition：none、on-failure、或any(默认)
-   delay：重启尝试之间的持续时间
-   max_attempts：重启窗口之外的最大尝试次数
-   window：确定重新启动是否成功的持续时间

现在让我们定义重启策略。首先，我们必须通过更改version属性来确保我们使用的是Docker Compose v3：

```yaml
version: '3'
```

更改版本后，我们可以将restart_policy属性添加到我们的服务中。与上一节类似，我们的message-server容器将始终通过在condition中提供any值来自动重启：

```yaml
deploy:
    restart_policy:
        condition: any
        delay: 5s
        max_attempts: 3
        window: 120s
```

同样，我们将向product-server添加一个on-failure重启策略：

```yaml
deploy:
    restart_policy:
        condition: on-failure
        delay: 3s
        max_attempts: 5
        window: 60s
```

**请注意restart_policy属性是如何在deploy键中的，这表明重启策略是我们提供的部署配置，用于在swarm模式下管理容器集群**。

此外，这两个服务中的重启策略都包含额外的配置元数据，使这些策略成为容器更可靠的重启策略。

## 6. 总结

在本文中，我们学习了如何使用Docker Compose定义重启策略。介绍完Docker重启策略后，我们使用之前的项目演示了两种配置容器重启策略的方式。

首先，我们使用了Docker Compose restart属性配置，其中包含与Docker CLI中的原生restart命令相同的选项。然后我们使用了restart_policy属性，它只在swarm模式和Docker Compose版本3中可用，以及其他定义重启策略的配置值。