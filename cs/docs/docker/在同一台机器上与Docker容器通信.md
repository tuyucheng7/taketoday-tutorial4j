---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

使用 Docker，我们通常构建可以通过隔离网络进行通信的容器。但是，我们可能有多个容器在同一台主机上进行通信。例如，在集成测试期间会发生这种情况。Docker 提供了具有不同选项的[网络](https://docs.docker.com/network/)定义。我们通常使用例如容器名称连接到同一网络。我们可能还想使用静态 IP 或直接访问运行容器的主机。

[在本教程中，我们将探讨如何使用Docker Compose](https://www.baeldung.com/ops/docker-compose)在同一台机器上连接两个容器。

## 2. Docker设置

让我们创建一个场景，其中两个容器在网络中进行通信。

[我们将从一个简单的Alpine](https://hub.docker.com/_/alpine)Docker镜像运行容器。然后，我们将检查这些容器是否可以通过网络相互 ping 通。

让我们看看Dockerfile。我们还将添加 Alpine 镜像中不存在的命令，例如ping或bash：

```plaintext
FROM alpine:latest
MAINTAINER baeldung.com
RUN apk update && apk add bash && apk add iputils
```

我们将使用[Docker Compose](https://docs.docker.com/compose/gettingstarted/)。我们可能希望在同一会话中运行不同的示例，因此我们将使用-f选项：

```shell
docker-compose -f yaml-file up -d

```

另外，让我们确保我们删除了我们的容器，例如，当我们想要移动到另一个示例时：

```shell
docker-compose -f yaml-file down
```

在开始进行一些Docker测试之前，让我们确保不会与我们的网络发生冲突并删除未使用的网络：

```shell
docker network prune
```

尽管如此，关于网络，Docker 从 YAML 文件的目录名称创建网络名称。如果我们不提供网络名称，它还会附加默认后缀。

## 3.使用DNS

Docker 有一个内置的 DNS 服务。DNS 将 IP 地址映射到别名，例如容器名称。这使得容器始终可以访问，即使 IP 地址随时间变化也是如此。

让我们运行组合服务并使它们在同一网络上进行通信。默认情况下，如果未指定网络，Docker 会在运行时为容器分配一个默认[桥接网络。](https://docs.docker.com/network/bridge/)

我们在dns目录下的docker-compose.yml文件中定义服务：

```yaml
services:
  alpine-app-1:
    container_name: alpine-app-1
    image: alpine-app-1
    build:
      context: ..
      dockerfile: Dockerfile
    tty: true
  
  alpine-app-2:
    container_name: alpine-app-2
    image: alpine-app-2
    build:
      context: ..
      dockerfile: Dockerfile
    tty: true

```

我们正在使用tty选项在容器中启动交互式终端。

一旦容器运行，它们就属于同一个网络。我们可以运行docker network inspect dns_default命令来验证：

```shell
"Containers": {
    "577c6ac4aae4f1e915148ebdc04df9ca997bc919d954ec41334b4a9b14115528": {
        "Name": "alpine-app-1",
        "EndpointID": "247d49a3ccd1590c740b2f4dfc438567219d5edcb6b7d9c1c0ef88c638dba371",
        "MacAddress": "02:42:ac:19:00:03",
        "IPv4Address": "172.25.0.2/16",
        "IPv6Address": ""
    },
    "e16023ac252d73977567a6fb17ce3936413955e135812e9a866d84a3a7a06ef8": {
        "Name": "alpine-app-2",
        "EndpointID": "8bd4907e4fb85e41e2e854bb7c132c31d5ef02a8e7bba3b95065b9c10ec8cbfb",
        "MacAddress": "02:42:ac:19:00:02",
        "IPv4Address": "172.25.0.3/16",
        "IPv6Address": ""
    }
}
```

Docker 网络分配可用的 IP。在这种情况下，172.25.0.2 和172.25.0.3。

现在，让我们尝试从第一个容器 ping 第二个容器。我们可以尝试使用主机 IP：

```shell
docker exec -it alpine-app-1 ping 172.25.0.3
```

更有趣的是，我们可以通过容器名称 ping：

```shell
docker exec -it alpine-app-1 ping alpine-app-2
```

或者，我们甚至可以通过容器 ID ping：

```shell
docker exec -it alpine-app-1 ping 577c6ac4aae4
```

但是，它们都产生类似的 ping 响应：

```shell
64 bytes from alpine-app-2.dns_default (172.25.0.3): icmp_seq=1 ttl=64 time=0.152 ms
64 bytes from alpine-app-2.dns_default (172.25.0.3): icmp_seq=2 ttl=64 time=0.192 ms
64 bytes from alpine-app-2.dns_default (172.25.0.3): icmp_seq=3 ttl=64 time=0.159 ms
```

我们可以从第二个容器ping通第一个容器，例如：

```shell
docker exec -it alpine-app-2 ping alpine-app-1
```

为了理解，我们可以从容器的角度来审视网络。让我们检查一下alpine-app-2容器：

```shell
docker inspect --format='{{json .NetworkSettings.Networks}}' alpine-app-2 | jq .

```

我们可以看到一些网络别名可用：

```shell
{
  "dns_default": {
    "IPAMConfig": null,
    "Links": null,
    "Aliases": [
      "alpine-app-2",
      "alpine-app-2",
      "577c6ac4aae4"
    ],
    "NetworkID": "4a10961e55733500114537a9f8b454d256443b8fd50f8a01ef9ee1208c94dac9",
    "EndpointID": "247d49a3ccd1590c740b2f4dfc438567219d5edcb6b7d9c1c0ef88c638dba371",
    "Gateway": "172.25.0.1",
    "IPAddress": "172.25.0.3",
    "IPPrefixLen": 16,
    "IPv6Gateway": "",
    "GlobalIPv6Address": "",
    "GlobalIPv6PrefixLen": 0,
    "MacAddress": "02:42:ac:19:00:03",
    "DriverOpts": null
  }
}
```

这就解释了为什么我们可以使用我们分配给容器的名称进行通信。此外，这很可能是我们在运行时所需要的，例如，我们的服务在其中模拟类似微服务或[SOA](https://en.wikipedia.org/wiki/Service-oriented_architecture)架构的测试套件。

我们可能还想在不同的机器之间创建一个网络。在这种情况下，我们可以看看 Docker[覆盖网络](https://docs.docker.com/network/overlay/)。

## 4.使用静态IP

通常，我们不担心 IP 地址管理。Docker 内置网络已经可以处理这个问题。我们已经在 DNS 示例中看到了这一点。

但是，我们可能还想为容器分配一个[静态 IP 。](https://www.baeldung.com/ops/docker-assign-static-ip-container)

### 4.1. 带桥接网络的静态 IP

我们看static_ip_bridge目录下的docker-compose.yml文件：

```yaml
services:
  alpine-app-1:
    container_name: alpine-app-1
    build:
      context: ..
      dockerfile: Dockerfile
    image: alpine-app-1
    tty: true
    networks:
      network-example:
        ipv4_address: 10.5.0.2

  alpine-app-2:
    container_name: alpine-app-2
    build:
      context: ..
      dockerfile: Dockerfile
    image: alpine-app-2
    tty: true
    networks:
      network-example:
        ipv4_address: 10.5.0.3

networks:
  network-example:
    driver: bridge
    ipam:
      config:
        - subnet: 10.5.0.0/16
          gateway: 10.5.0.1
```

这仍然会创建一个子网。但是，这次Docker分配的是静态 IP。我们可以通过在主机中运行ifconfig命令来发现子网：

```shell
br-2fda6ab68472: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
    inet 10.5.0.1  netmask 255.255.0.0  broadcast 10.5.255.255
    inet6 fe80::42:9dff:fe07:5b59  prefixlen 64  scopeid 0x20<link>
    ether 02:42:9d:07:5b:59  txqueuelen 0  (Ethernet)
    RX packets 0  bytes 0 (0.0 B)
    RX errors 0  dropped 0  overruns 0  frame 0
    TX packets 30  bytes 4426 (4.3 KiB)
    TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

```

如果我们检查alpine-app-2容器，我们可以看到这次 IPAM(IP 地址管理)分配了一个 IPv4 地址：

```shell
{
  "static_ip_network-example": {
    "IPAMConfig": {
      "IPv4Address": "10.5.0.3"
    },
    "Links": null,
    "Aliases": [
      "alpine-app-2",
      "alpine-app-2",
      "acafb03c009c"
    ],
    "NetworkID": "33ebac5be422f1e8cef8509b69e3a7af55e1da365fe7f9c6fc184159c13bbdee",
    "EndpointID": "ebfd36619104ee368e64042090eb02b2dd3167d0c844b80c40b6ab63eb6afb76",
    "Gateway": "10.5.0.1",
    "IPAddress": "10.5.0.3",
    "IPPrefixLen": 16,
    "IPv6Gateway": "",
    "GlobalIPv6Address": "",
    "GlobalIPv6PrefixLen": 0,
    "MacAddress": "02:42:0a:05:00:05",
    "DriverOpts": null
  }
}

```

同样，我们可以在其 IP 地址上成功 ping alpine-app-2容器：

```shell
docker exec -it alpine-app-1 ping 10.5.0.3
```

但是，我们仍然可以使用容器的名称或 ID 进行 ping，因为我们处于同一网络中。因此，ping alpine-app-2或ping acafb03c009c仍然是有效选项。

在这种情况下，我们也可以直接从Docker主机访问容器的10.5.0.2 或10.5.0.3地址。

### 4.2. Macvlan

如果我们想为我们的容器之一分配 MAC 地址，我们可以查看[Macvlan](https://docs.docker.com/network/macvlan/)网络。这使得容器网络接口直接连接到物理网络。它仅适用于Linux操作系统。它不适用于Windows或 Mac。

Macvlan 网络是特殊的虚拟网络，它创建主机物理网络接口的克隆并将容器直接附加到 LAN。

我们看static_ip_macvlan目录下的docker -compose.yml文件：

```yaml
services:
  alpine-app-1:
    container_name: alpine-app-1
    build:
      context: ..
      dockerfile: Dockerfile
    image: alpine-app-1
    tty: true
    networks:
      network-example:
        ipv4_address: 192.168.2.2

  alpine-app-2:
    container_name: alpine-app-2
    build:
      context: ..
      dockerfile: Dockerfile
    image: alpine-app-2
    tty: true
    networks:
      network-example:
        ipv4_address: 192.168.2.3

networks:
  network-example:
    driver: macvlan
    driver_opts:
      parent: enp0s3
    ipam:
      config:
        - subnet: 192.168.2.0/24
          gateway: 192.168.2.1
```

该示例与上一个使用简单桥接网络的示例类似。尽管如此，Docker 通过 IPAM 配置分配一个 IPv4 地址。

但是，它的不同之处在于指定将Docker网络接口附加到何处的父级。在这种情况下，我们使用enp0s3，但同样适用于例如eth0接口。

再次，让我们 ping alpine-app-2容器：

```shell
docker exec -it alpine-app ping 192.168.2.3
```

值得注意的是，在这种情况下，我们无法直接从主机访问容器。为了使容器与主机通信，我们需要在Docker主机上创建一个macvlan接口，并配置到容器的macvlan接口的路由。

例如，让我们首先在我们的主机macvlan-net中创建一个macvlan接口 ，分配一个可用的 IP，然后调出该接口：

```shell
sudo ip link add macvlan-net link enp0s3 type macvlan mode bridge && sudo ip addr add 192.168.2.50/32 dev macvlan-net \\ 
&& sudo ip link set macvlan-net up 
```

最后，我们可以向Dockermacvlan接口添加 IP 路由：

```shell
sudo ip route add 192.168.2.0/24 dev macvlan-net
```

## 5. 使用host.docker.internal

安装Docker时，会创建一个名为docker0的默认桥接网络。每个新的Docker容器都会自动附加到该网络。

我们可以通过运行命令ifconfig docker0查看它的定义：

```shell
docker0: flags=4099<UP,BROADCAST,MULTICAST>  mtu 1500
    inet 172.17.0.1  netmask 255.255.0.0  broadcast 172.17.255.255
    ether 02:42:ee:96:b3:94  txqueuelen 0  (Ethernet)
    RX packets 0  bytes 0 (0.0 B)
    RX errors 0  dropped 0  overruns 0  frame 0
    TX packets 0  bytes 0 (0.0 B)
    TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
```

在这种情况下，IP 是172.17.0.1。Docker 提供了一种使用名称为host.docker.internal 的 DNS 解析此 IP 地址的方法。

因此，如果我们将该特定 DNS 添加到容器中的已知主机列表中，我们将能够与Docker主机通信。

虽然不推荐，但当我们想在本地运行容器但仍能获得连接时，这可能很有用，例如，与在我们的主机中运行的服务。

为了理解，这次我们将在不同的网络上设置容器。他们将只能通过位于host.docker.internal地址的Docker主机进行通信。

### 5.1. 创建一个 Nodejs 应用程序

因此，让我们从 Alpine 容器连接到名为node-app的 Nodejs 容器。Nodejs 应用程序将公开一个“hello world” REST 端点。让我们看看package.json：

```json
{
  "name": "host_docker_internal",
  "version": "1.0.0",
  "description": "node js app",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "author": "Baeldung",
  "license": "ISC",
  "dependencies": {
    "express": "^4.18.2"
  }
}

```

此外，我们需要在端口启动服务器，例如8080，并添加“ hello world ”端点：

```javascript
var express = require('express')
var app = express()

app.get('/', function (req, res) {
    res.send('Hello World!')
})

app.listen(8080, function () {
    console.log('app listening on port 8080!')
})
```

最后，我们需要一个Dockerfile：

```plaintext
FROM node:8.16.1-alpine
WORKDIR /app
COPY host_docker_internal/package.json /app
COPY host_docker_internal/index.js /app
RUN npm install
CMD node index.js
EXPOSE 8080
```

### 5.2. 连接到host.docker.internal

让我们在host_docker_internal目录中创建一个docker-compose.yml文件：

```yaml
services:
  alpine-app-1:
    container_name: alpine-app-1
    extra_hosts: # for linux hosts since version 20.10
      - host.docker.internal:host-gateway
    build:
      context: ..
      dockerfile: Dockerfile
    image: alpine-app-1
    tty: true
    networks:
      - first-network

  node-app:
    container_name: node-app
    build:
      context: ..
      dockerfile: Dockerfile.node
    image: node-app
    ports:
      - 8080:8080
    networks:
      - second-network

networks:
  first-network:
    driver: bridge
  second-network:
    driver: bridge
```

对于Linux操作系统，我们必须显式添加到extra_hosts。我们不需要为Windows或Mac执行此操作。此外，对于 Linux，这在Docker版本 20.10 之前是不可能的。

让我们从alpine-app-1调用node-app容器中的端点。如前所述，我们需要通过host.docker.internal：

```shell
docker exec -it alpine-app-1 curl -i -X GET http://host.docker.internal:8080
```

我们将通过响应获得200状态：

```shell
HTTP/1.1 200 OK
X-Powered-By: Express
Content-Type: text/html; charset=utf-8
Content-Length: 12
ETag: W/"c-Lve95gjOVATpfV8EL5X4nxwjKHE"
Date: Tue, 24 Jan 2023 18:33:38 GMT
Connection: keep-alive

Hello World!
```

最后，让我们看一下alpine-app-1容器中的/etc/hosts文件：

```shell
127.0.0.1       localhost
::1     localhost ip6-localhost ip6-loopback
fe00::0 ip6-localnet
ff00::0 ip6-mcastprefix
ff02::1 ip6-allnodes
ff02::2 ip6-allrouters
172.17.0.1      host.docker.internal
172.26.0.2      81945d220c5e

```

host.docker.internal是容器的已知主机。虽然不在网桥网络中，但容器现在可以像使用docker0接口作为发现彼此的网桥一样进行通信。

## 六，总结

在本文中，我们看到了Docker容器如何在同一台机器上进行通信的几个示例。

通过同一网络连接时，我们了解了内置 DNS 服务以及如何使用网桥或macvlan网络分配静态 IP。在不同的网络上，我们看到了一个使用解析为Docker主机的host.docker.internal DNS 名称的示例。

与往常一样，我们可以[在 GitHub 上](https://github.com/eugenp/tutorials/tree/master/docker-modules/docker-compose-2/)找到工作代码示例。
