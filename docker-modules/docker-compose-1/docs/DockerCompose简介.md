## 1. 概述

当广泛使用Docker时，管理多个不同的容器很快就会变得很麻烦。而Docker Compose工具可以帮助我们克服这个问题，**轻松地同时处理多个容器**。

在本教程中，我们介绍其主要功能和强大的机制。

## 2. YAML配置说明

简而言之，Docker Compose通过应用在**单个docker-compose.yml配置文件**中声明的许多规则来工作。

这些[YAML](https://en.wikipedia.org/wiki/YAML)规则，既是人类可读的，也是机器优化的，提供了一种有效的方法，可以用几行从一万英尺的高度拍摄整个项目的快照。

几乎每个规则都替换了一个特定的Docker命令，所以最后，我们只需要运行：

```shell
docker-compose up
```

我们可以通过Compose在背后应用数十种配置，这可以为我们省去用Bash或其他东西编写脚本的麻烦。

在此文件中，我们需要指定Compose文件格式的版本、至少一项服务以及可选的容器卷和网络：

```yaml
version: "3.7"
services:
    ...
volumes:
    ...
networks:
    ...
```

### 2.1 服务

首先，**服务是指容器的配置**。

例如，让我们来看一个由前端、后端和数据库组成的容器化Web应用程序。我们可能会将这些组件拆分为三个镜像，并在配置中将它们定义为三个不同的服务：

```yaml
services:
    frontend:
        image: my-vue-app
        ...
    backend:
        image: my-springboot-app
        ...
    db:
        image: postgres
        ...
```

我们可以将多种设置应用于服务，稍后我们会更详细地探讨这些设置。

### 2.2 卷和网络

另一方面，卷是主机和容器之间，甚至容器之间共享的磁盘空间的物理区域。换句话说，**卷是主机中的一个共享目录**，对部分或所有容器可见。

同样，**网络定义了容器之间以及容器与主机之间的通信规则。公共网络区域将使容器的服务可以相互发现，而私有区域将它们隔离在虚拟沙箱中**。

同样，我们将在下一节中详细了解它们。

## 3. 剖析服务

### 3.1 拉取镜像

有时，我们服务所需的镜像已经(由我们或其他人)在[Docker Hub](https://hub.docker.com/)或其他Docker Registry中发布。

如果是这种情况，那么我们通过指定镜像名称和标签来使用image属性引用它：

```yaml
services:
    my-service:
        image: ubuntu:latest
        ...
```

### 3.2 构建镜像

或者，我们可能需要通过读取其Dockerfile从源代码[构建](https://docs.docker.com/compose/compose-file/#build)一个镜像。

如果是这样的化，我们使用build关键字，将Dockerfile文件所在的路径作为值传递：

```yaml
services:
    my-custom-app:
        build: /path/to/dockerfile/
        ...
```

我们也可以[使用URL](https://gist.github.com/derianpt/420617ffa5d2409f9d2a4a1a60cfa9ae#file-build-contexts-yml)而不是路径：

```yaml
services:
    my-custom-app:
        build: https://github.com/my-company/my-project.git
        ...
```

此外，我们可以结合build属性指定镜像名称，该属性将在创建后命名镜像，[使其可供其他服务使用](https://stackoverflow.com/a/35662191/1654265)：

```yaml
services:
    my-custom-app:
        build: https://github.com/my-company/my-project.git
        image: my-project-image
        ...
```

### 3.3 配置网络

**Docker容器在由Docker Compose隐式或通过配置创建的网络中相互通信**。一个服务可以通过简单地通过容器名称和端口(例如network-example-service:80)引用它来与同一网络上的另一个服务通信，前提是我们已经通过expose关键字使端口可访问：

```yaml
services:
    network-example-service:
        image: karthequian/helloworld:latest
        expose:
            - "80"
```

在这种情况下，它也可以在不公开它的情况下工作，因为expose指令已经在[镜像Dockerfile](https://github.com/karthequian/docker-helloworld/blob/master/Dockerfile#L45)中。

**要从主机访问容器，端口必须通过ports关键字以声明方式公开**，这也允许我们选择是否在主机中以不同方式公开端口：

```yaml
services:
    network-example-service:
        image: karthequian/helloworld:latest
        ports:
            - "80:80"
        ...
    my-custom-app:
        image: myapp:latest
        ports:
            - "8080:3000"
        ...
    my-custom-app-replica:
        image: myapp:latest
        ports:
            - "8081:3000"
        ...
```

端口80现在可以从主机上访问，而其他两个容器的端口3000将在主机的端口8080和8081上可用。**这种强大的机制使我们能够运行不同的容器并暴露相同的端口，而不会发生冲突**。

最后，我们可以定义额外的虚拟网络来隔离我们的容器：

```yaml
services:
    network-example-service:
        image: karthequian/helloworld:latest
        networks:
            - my-shared-network
        ...
    another-service-in-the-same-network:
        image: alpine:latest
        networks:
            - my-shared-network
        ...
    another-service-in-its-own-network:
        image: alpine:latest
        networks:
            - my-private-network
        ...
networks:
    my-shared-network: { }
    my-private-network: { }
```

在最后一个示例中，我们可以看到another-service-in-the-same-network将能够ping并到达network-example-service的端口80，而another-service-in-its-own-network则不能。

### 3.4 设置卷

容器卷一种有三种类型：[匿名卷、命名卷和主机卷](https://success.docker.com/article/different-types-of-volumes)。

**Docker管理匿名卷和命名卷**，自动将它们装载到主机中自行生成的目录中。虽然匿名卷对旧版本的Docker(1.9之前)很有用，但现在建议使用命名卷。**主机卷还允许我们指定主机中的现有文件夹**。

我们可以在服务级别配置主机卷，并在配置的外层配置命名卷，以使后者对其他容器可见，而不仅仅是对它们所属的容器可见：

```yaml
services:
    volumes-example-service:
        image: alpine:latest
        volumes:
            - my-named-global-volume:/my-volumes/named-global-volume
            - /tmp:/my-volumes/host-volume
            - /home:/my-volumes/readonly-host-volume:ro
        ...
    another-volumes-example-service:
        image: alpine:latest
        volumes:
            - my-named-global-volume:/another-path/the-same-named-global-volume
        ...
volumes:
    my-named-global-volume:
```

在这里，两个容器都将具有对my-named-global-volume共享文件夹的读/写访问权限，无论它们将其映射到哪个路径。相反，这两个主机卷将仅对volumes-example-service可用。

主机文件系统的/tmp文件夹映射到容器的/my-volumes/host-volume文件夹。文件系统的这一部分是可写的，这意味着容器可以读取和写入(和删除)主机中的文件。

**我们可以通过将:ro附加到规则以只读模式挂载卷**，例如/home文件夹(我们不希望Docker容器错误地删除我们的用户)。

### 3.5 声明依赖关系

通常，我们需要在我们的服务之间创建一个依赖链，以便某些服务在其他服务之前加载(并在其他服务之后卸载)。我们可以通过depends_on关键字来实现这个效果：

```yaml
services:
    kafka:
        image: wurstmeister/kafka:2.11-0.11.0.3
        depends_on:
            - zookeeper
        ...
    zookeeper:
        image: wurstmeister/zookeeper
        ...
```

但是，我们应该知道，Compose不会等待zookeeper服务完成加载后再启动kafka服务；它只会等待它开始。如果我们需要在启动一个服务之前完全加载另一个服务，那么我们需要[更深入地控制Compose中的启动和关闭顺序](https://docs.docker.com/compose/startup-order/)。

## 4. 管理环境变量

在Compose中使用环境变量很容易，我们可以使用${}符号定义静态环境变量和动态变量：

```yaml
services:
    database:
        image: "postgres:${POSTGRES_VERSION}"
        environment:
            DB: mydb
            USER: "${USER}"
```

有多种方法可以将这些值提供给Compose。例如，一种方法是将它们设置在同一目录中的.env文件中，其结构类似于.properties文件，配置为key=value对：

```properties
POSTGRES_VERSION=alpine
USER=foo
```

或者，我们可以在调用命令之前在操作系统中设置它们：

```shell
export POSTGRES_VERSION=alpine
export USER=foo
docker-compose up
```

最后，在shell中使用一行简单的代码也很容易实现这一点：

```shell
POSTGRES_VERSION=alpine USER=foo docker-compose up
```

我们可以混合使用这些方法，但请记住，Compose使用以下优先级顺序，用较高的优先级覆盖低优先级：

1.  Compose文件
2.  Shell环境变量
3.  环境文件
4.  Dockerfile
5.  变量未定义

## 5. 扩展和复制

在较旧的Compose版本中，我们可以通过[docker-compose scale](https://docs.docker.com/compose/reference/scale/)命令扩展容器的实例，但较新的版本弃用了它，并将其替换为--scale参数。

我们可以利用[Docker Swarm](https://docs.docker.com/engine/swarm/)，一个Docker引擎集群，并通过deploy部分的replicas属性以声明方式自动扩展我们的容器：

```yaml
services:
    worker:
        image: dockersamples/examplevotingapp_worker
        networks:
            - frontend
            - backend
        deploy:
            mode: replicated
            replicas: 6
            resources:
                limits:
                    cpus: '0.50'
                    memory: 50M
                reservations:
                    cpus: '0.25'
                    memory: 20M
            ...
```

在deploy下，我们还可以指定许多其他选项，例如resources thresholds。然而，Compose仅在部署到Swarm时才考虑整个部署部分，否则忽略它。

## 6. 一个真实的例子：Spring Cloud Data Flow

通过介绍一些小型的例子可以帮助我们理解Docker Compose的概念，但通过实际运行的代码通常是更直接的方式。

Spring Cloud Data Flow是一个复杂的项目，但足够简单易懂。让我们[下载它的YAML文件](https://dataflow.spring.io/docs/installation/local/docker/)并运行：

```shell
DATAFLOW_VERSION=2.1.0.RELEASE SKIPPER_VERSION=2.0.2.RELEASE docker-compose up 
```

Compose将下载、配置和启动每个组件，然后**将容器的日志交叉到当前终端中的单个流中**。

它还将为它们中的每一个应用独特的颜色，以获得出色的用户体验：

<img src="../assets/img.png">

运行全新的Docker Compose安装时，我们可能会遇到以下错误：

```shell
lookup registry-1.docker.io: no such host
```

虽然针对这个常见的陷阱[有不同的解决方案](https://stackoverflow.com/questions/46036152/lookup-registry-1-docker-io-no-such-host)，但使用8.8.8.8作为DNS可能是最简单的。

## 7. 生命周期管理

现在让我们仔细看看Docker Compose的语法：

```shell
docker-compose [-f <arg>...] [options] [COMMAND] [ARGS...]
```

虽然有[许多选项和命令可用](https://docs.docker.com/compose/reference/overview/)，但我们至少需要知道正确启动和停止整个系统的选项和命令。

### 7.1 启动

我们可以使用up创建和启动配置中定义的容器、网络和卷：

```shell
docker-compose up
```

在第一次启动之后，我们可以简单地使用start来启动服务：

```shell
docker-compose start
```

如果我们的文件的名称与默认文件(docker-compose.yml)不同，我们可以使用-f和––file标志来指定备用文件名：

```shell
docker-compose -f custom-compose-file.yml start
```

当使用-d选项启动时，Compose还可以作为守护进程在后台运行：

```shell
docker-compose up -d
```

### 7.2 关闭

为了安全地停止处于活动的服务，我们可以使用stop，它将保留容器、卷和网络，以及对它们所做的每一个修改：

```shell
docker-compose stop
```

要重置服务的状态，我们可以简单地运行down，**这将销毁除外部卷之外的所有内容**：

```shell
docker-compose down
```

## 8. 总结

在本文中，我们介绍了Docker Compose及其工作原理。

像往常一样，我们可以[在GitHub上找到源代码]()docker-compose.yml文件，以及一组有用的测试，如下图所示：

<img src="../assets/img-1.png">