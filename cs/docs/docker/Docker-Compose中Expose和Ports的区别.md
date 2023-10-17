---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

众所周知，[Docker Compose](https://www.baeldung.com/ops/docker-compose)是一个同时定义和管理多个容器的工具。默认情况下，Docker Compose为定义的容器设置专用网络，使它们之间能够通信。因此，我们可以使用单个命令使用给定的配置文件创建和运行服务。

在本教程中，我们将重点关注两个 YAML 属性，它们允许我们自定义容器、公开和 端口之间的网络。我们将详细描述它们，探索基本用例，并强调它们的主要区别。

## 2. 暴露部分

首先，让我们看一下暴露配置。此属性定义Docker Compose从容器公开的端口。

连接到同一网络的其他服务可以访问这些端口，但不会在主机上发布。

我们可以通过在服务部分指定端口号来公开端口：

```yaml
services:
  myapp1:
    ...
    expose:
      - "3000"
      - "8000"
  myapp2:
    ...
    expose:
      - "5000"
```

如我们所见，我们可以为每个服务指定多个值。我们刚刚暴露了myapp1容器的端口3000 和8000 ，以及myapp2容器的端口5000。现在可以在这些端口上为同一网络中的其他容器访问这些服务。

现在让我们检查暴露的端口：

```shell
> docker ps
CONTAINER ID   IMAGE    COMMAND     CREATED     STATUS      PORTS               NAMES
8673c14f18d1   ...      ...         ...         ...         3000/tcp, 8000/tcp  bael_myapp1
bc044e180131   ...      ...         ...         ...         5000/tcp            bael_myapp2
```

在docker ps命令输出中，我们可以在PORTS 列中找到暴露的端口。

最后，我们来验证一下容器之间的通信：

```shell
> docker exec -it bc044e180131 /bin/bash

bash-5.1$ nc -vz myapp1 3000
myapp1 (172.18.0.1:3000) open
bash-5.1$ nc -vz myapp1 8000
myapp1 (172.18.0.1:8000) open
```

我们刚刚连接到myapp2 CLI。使用[netcat](https://www.baeldung.com/linux/netcat-command)命令，我们检查了从myapp1公开的两个端口是否可以访问。

## 3.端口部分

现在让我们检查端口部分。与expose一样，此属性定义了我们要从容器中公开的端口。但与公开配置不同的是，这些端口可以在内部访问并在主机上发布。

同样，和以前一样，我们可以在专用部分为每个服务定义端口，但配置可能更复杂。首先，我们必须在两种语法(短语法和长语法)之间进行选择来定义配置。

### 3.1. 短语法

让我们从分析短的开始。简短语法是一个以冒号分隔的字符串，用于设置主机 IP 地址、主机端口和容器端口：

```css
[HOST:]CONTAINER[/PROTOCOL]
```

在这里，HOST是主机端口号或端口号范围，可以在 IP 地址之前。如果我们不指定 IP 地址，Docker Compose会将端口绑定到所有网络接口。

CONTAINER定义容器端口号或端口号范围。

PROTOCOL将容器端口限制为指定的协议，或者如果为空则将它们设置为TCP。只有CONTAINER 部分是强制性的。

现在我们知道了语法，让我们在Docker Compose文件中定义端口：

```yaml
services:
  myapp1:
    ...
    ports:
    - "3000"                             # container port (3000), assigned to random host port
    - "3001-3005"                        # container port range (3001-3005), assigned to random host ports
    - "8000:8000"                        # container port (8000), assigned to given host port (8000)
    - "9090-9091:8080-8081"              # container port range (8080-8081), assigned to given host port range (9090-9091)
    - "127.0.0.1:8002:8002"              # container port (8002), assigned to given host port (8002) and bind to 127.0.0.1
    - "6060:6060/udp"                    # container port (6060) restricted to UDP protocol, assigned to given host (6060)
```

如上所述，我们还可以使用短语法的不同变体并更精确地配置它，一次发布多个容器端口。Docker Compose公开了所有指定的容器端口，使它们可以从本地计算机内部和外部访问。

和以前一样，让我们使用docker ps命令检查暴露的端口：

```rust
> docker ps -a
CONTAINER ID   ... PORTS                                                                        NAMES
e8c65b9eec91   ... 0.0.0.0:51060->3000/tcp, 0.0.0.0:51063->3001/tcp, 0.0.0.0:51064->3002/tcp,   bael_myapp1
                   0.0.0.0:51065->3003/tcp, 0.0.0.0:51061->3004/tcp, 0.0.0.0:51062->3005/tcp, 
                   0.0.0.0:8000->8000/tcp, 0.0.0.0:9090->8080/tcp, 0.0.0.0:9091->8081/tcp
                   127.0.0.1:8002->8002/tcp, 0.0.0.0:6060->6060/udp
```

再次，在PORTS列中，我们可以找到所有暴露的端口。箭头左侧的值显示了我们可以从外部访问容器的主机地址。

### 3.2. 长语法

使用长语法，我们可以用相同的方式配置端口。但是，我们将单独定义每个属性，而不是使用冒号分隔的字符串：

```yaml
services: 
  myapp1:
  ...
  ports:
  # - "127.0.0.1:6060:6060/udp"
  - target: 6060
    host_ip: 127.0.0.1
    published: 6060
    protocol: udp
    mode: host
```

这里的target是必选的，指定了要暴露的容器端口(或端口范围)，相当于简写语法中的CONTAINER 。

host_ip和 published是简称HOST的一部分，我们可以在 其中定义主机的 IP 地址和端口。

该协议与短语法中的PROTOCOL相同，将容器端口限制为指定的协议(如果为空，则为TCP)。

模式 是具有两个值的枚举，指定端口发布规则。我们应该使用主机值在本地发布一个端口。第二个值ingress是为更复杂的容器环境保留的，意味着端口将被负载平衡。

总之，任何短语法字符串都可以很容易地用长结构表示。但是，由于缺少模式属性，并非所有长语法配置都可以移动到短语法配置。

## 4. 总结

在本文中，我们介绍了Docker Compose中的部分网络配置。我们使用expose和 ports部分分析和比较了端口配置。

公开 部分允许我们将容器中的特定端口仅公开给同一网络上的其他服务。我们可以通过指定容器端口来简单地做到这一点。

端口 部分还公开了容器中的指定端口。与上一节不同，端口不仅对同一网络上的其他服务开放，而且对主机开放。配置稍微复杂一点，这里我们可以配置暴露端口、本地绑定地址、受限协议。最后，根据我们的喜好，我们可以在两种不同的语法之间进行选择。
