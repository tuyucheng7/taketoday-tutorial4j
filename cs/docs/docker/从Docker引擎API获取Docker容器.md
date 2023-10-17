---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在本教程中，我们将了解如何使用Docker Engine API从容器内部访问Docker容器信息。

## 2.设置

我们可以通过多种方式连接到Docker引擎。我们将涵盖Linux下最有用的功能，但它们也适用于其他操作系统。

但是，我们应该非常小心，因为启用远程访问会带来安全风险。当容器可以访问引擎时，它就打破了与主机操作系统的隔离。

对于设置部分，我们将认为我们拥有对主机的完全控制权。

### 2.1. 转发默认的 Unix 套接字

默认情况下，Docker 引擎使用挂载在主机操作系统/var/run/docker.sock下的 Unix 套接字：

```shell
$ ss -xan | grep var

u_str LISTEN 0      4096              /var/run/docker/libnetwork/dd677ae5f81a.sock 56352            * 0           
u_dgr UNCONN 0      0                                 /var/run/chrony/chronyd.sock 24398            * 0           
u_str LISTEN 0      4096                                      /var/run/nscd/socket 23131            * 0           
u_str LISTEN 0      4096                              /var/run/docker/metrics.sock 42876            * 0           
u_str LISTEN 0      4096                                      /var/run/docker.sock 53704            * 0    
...       
```

通过这种方法，我们可以严格控制哪个容器可以访问 API。这就是DockerCLI 在幕后的工作方式。

让我们启动alpineDocker容器并使用-v标志挂载此路径：

```shell
$ docker run -it -v /var/run/docker.sock:/var/run/docker.sock alpine

(alpine) $
```

接下来，让我们在容器中安装一些实用程序：

```shell
(alpine) $ apk add curl && apk add jq

fetch http://dl-cdn.alpinelinux.org/alpine/v3.11/community/x86_64/APKINDEX.tar.gz
(1/4) Installing ca-certificates (20191127-r2)
(2/4) Installing nghttp2-libs (1.40.0-r1)
...
```

现在让我们使用带有–unix-socket标志的curl和[Jq](https://www.baeldung.com/linux/jq-command-json)来获取和过滤一些容器数据：

```shell
(alpine) $ curl -s --unix-socket /var/run/docker.sock http://dummy/containers/json | jq '.'

[
  {
    "Id": "483c5d4aa0280ca35f0dbca59b5d2381ad1aa455ebe0cf0ca604900b47210490",
    "Names": [
      "/wizardly_chatelet"
    ],
    "Image": "alpine",
    "ImageID": "sha256:e7d92cdc71feacf90708cb59182d0df1b911f8ae022d29e8e95d75ca6a99776a",
    "Command": "/bin/sh",
    "Created": 1595882408,
    "Ports": [],
...
```

在这里，我们在/containers/json端点上发出GET并获取当前正在运行的容器。然后我们使用jq美化输出。

稍后我们将介绍引擎 API 的详细信息。

### 2.2. 启用 TCP 远程访问

我们还可以使用 TCP 套接字启用远程访问。

对于systemd自带的Linux发行版，我们需要自定义Docker服务单元。对于其他Linux发行版，我们需要自定义通常位于/etc/docker的daemon.json。

我们将只介绍第一种设置，因为大多数步骤都是相似的。

默认的Docker设置包括一个桥接网络。除非另有说明，这是所有容器连接的地方。

由于我们只想允许容器访问引擎 API，因此我们首先确定它们的网络：

```shell
$ docker network ls

a3b64ea758e1        bridge              bridge              local
dfad5fbfc671        host                host                local
1ee855939a2a        none                null                local

```

让我们看看它的细节：

```shell
$ docker network inspect a3b64ea758e1

[
    {
        "Name": "bridge",
        "Id": "a3b64ea758e1f02f4692fd5105d638c05c75d573301fd4c025f38d075ed2a158",
...
        "IPAM": {
            "Driver": "default",
            "Options": null,
            "Config": [
                {
                    "Subnet": "172.17.0.0/16",
                    "Gateway": "172.17.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
...
```

接下来我们看看Docker服务单元在什么位置：

```shell
$ systemctl status docker.service

docker.service - Docker Application Container Engine
     Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
...
     CGroup: /system.slice/docker.service
             ├─6425 /usr/bin/dockerd --add-runtime oci=/usr/sbin/docker-runc
             └─6452 docker-containerd --config /var/run/docker/containerd/containerd.toml --log-level warn

```

现在让我们看一下服务单元定义：

```shell
$ cat /usr/lib/systemd/system/docker.service

[Unit]
Description=Docker Application Container Engine
Documentation=http://docs.docker.com
After=network.target lvm2-monitor.service SuSEfirewall2.service

[Service]
EnvironmentFile=/etc/sysconfig/docker
...
Type=notify
ExecStart=/usr/bin/dockerd --add-runtime oci=/usr/sbin/docker-runc $DOCKER_NETWORK_OPTIONS $DOCKER_OPTS
ExecReload=/bin/kill -s HUP $MAINPID
...
```

ExecStart属性定义了systemd(dockerd可执行文件)运行的命令。我们将-H标志传递给它并指定要侦听的相应网络和端口。

我们可以直接修改这个服务单元(不推荐)，但让我们使用$DOCKER_OPTS变量(在 EnvironmentFile=/etc/sysconfig/docker中定义)：

```shell
$ cat /etc/sysconfig/docker 

## Path           : System/Management
## Description    : Extra cli switches for docker daemon
## Type           : string
## Default        : ""
## ServiceRestart : docker
#
DOCKER_OPTS="-H unix:///var/run/docker.sock -H tcp://172.17.0.1:2375"
```

这里，我们使用桥接网络的网关地址作为绑定地址。这对应于主机上的docker0接口：

```shell
$ ip address show dev docker0

3: docker0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP group default 
    link/ether 02:42:6c:7d:9c:8d brd ff:ff:ff:ff:ff:ff
    inet 172.17.0.1/16 brd 172.17.255.255 scope global docker0
       valid_lft forever preferred_lft forever
    inet6 fe80::42:6cff:fe7d:9c8d/64 scope link 
       valid_lft forever preferred_lft forever

```

我们还启用了本地 Unix 套接字，以便DockerCLI 仍然可以在主机上运行。

我们还需要做一步。让我们的容器数据包到达主机：

```shell
$ iptables -I INPUT -i docker0 -j ACCEPT
```

在这里，我们将Linux防火墙设置为接受来自docker0接口的所有包。

现在，让我们重新启动Docker服务：

```shell
$ systemctl restart docker.service
$ systemctl status docker.service
 docker.service - Docker Application Container Engine
     Loaded: loaded (/usr/lib/systemd/system/docker.service; disabled; vendor preset: disabled)
...
     CGroup: /system.slice/docker.service
             ├─8110 /usr/bin/dockerd --add-runtime oci=/usr/sbin/docker-runc -H unix:///var/run/docker.sock -H tcp://172.17.0.1:2375
             └─8137 docker-containerd --config /var/run/docker/containerd/containerd.toml --log-level wa
```

让我们再次运行我们的alpine容器：

```shell
(alpine) $ curl -s http://172.17.0.1:2375/containers/json | jq '.'

[
  {
    "Id": "45f13902b710f7a5f324a7d4ec7f9b934057da4887650dc8fb4391c1d98f051c",
    "Names": [
      "/unruffled_cray"
    ],
    "Image": "alpine",
    "ImageID": "sha256:a24bb4013296f61e89ba57005a7b3e52274d8edd3ae2077d04395f806b63d83e",
    "Command": "/bin/sh",
    "Created": 1596046207,
    "Ports": [],
...

```

我们应该知道，所有连接到 bridge 网络的容器都可以访问 daemon API。

此外，我们的 TCP 连接未加密。

## 3. Docker引擎API

现在我们已经设置了远程访问，让我们来看看 API。

我们将探索几个有趣的选项，但我们始终可以查看[完整的文档](https://docs.docker.com/engine/api/latest)以了解更多信息。

让我们获取一些关于容器的信息：

```shell
(alpine) $ curl -s http://172.17.0.1:2375/containers/"$(hostname)"/json | jq '.'

{
  "Id": "45f13902b710f7a5f324a7d4ec7f9b934057da4887650dc8fb4391c1d98f051c",
  "Created": "2020-07-29T18:10:07.261589135Z",
  "Path": "/bin/sh",
  "Args": [],
  "State": {
    "Status": "running",
...

```

在这里，我们使用/containers/{container-id}/json URL 来获取有关我们容器的详细信息。

在这种情况下，我们运行hostname命令来获取container-id。

接下来，让我们监听Docker守护进程上的事件：

```shell
(alpine) $ curl -s http://172.17.0.1:2375/events | jq '.'
```

现在在不同的终端让我们启动hello-world容器：

```shell
$ docker run hello-world

Hello from Docker!
This message shows that your installation appears to be working correctly.
...

```

回到我们的alpine容器中，我们得到了一堆事件：

```shell
{
  "status": "create",
  "id": "abf881cbecfc0b022a3c1a6908559bb27406d0338a917fc91a77200d52a2553c",
  "from": "hello-world",
  "Type": "container",
  "Action": "create",
...
}
{
  "status": "attach",
  "id": "abf881cbecfc0b022a3c1a6908559bb27406d0338a917fc91a77200d52a2553c",
  "from": "hello-world",
  "Type": "container",
  "Action": "attach",
...
```

到目前为止，我们一直在做非侵入性的事情。是时候稍微动摇一下了。

让我们创建并启动一个容器。首先，我们定义它的清单：

```shell
(alpine) $ cat > create.json << EOF
{
  "Image": "hello-world",
  "Cmd": ["/hello"]
}
EOF

```

现在让我们使用清单调用/containers/create端点：

```shell
(alpine) $ curl -X POST -H "Content-Type: application/json" -d @create.json http://172.17.0.1:2375/containers/create

{"Id":"f96a6360ad8e36271cc75a3cff05348761569cf2f089bbb30d826bd1e2d52f59","Warnings":[]}
```

然后，我们使用id来启动容器：

```shell
(alpine) $ curl -X POST http://172.17.0.1:2375/containers/f96a6360ad8e36271cc75a3cff05348761569cf2f089bbb30d826bd1e2d52f59/start
```

最后，我们可以探索日志：

```shell
(alpine) $ curl http://172.17.0.1:2375/containers/f96a6360ad8e36271cc75a3cff05348761569cf2f089bbb30d826bd1e2d52f59/logs?stdout=true --output -

Hello from Docker!
KThis message shows that your installation appears to be working correctly.

;To generate this message, Docker took the following steps:
3 1. The Docker client contacted the Docker daemon.
...
```

请注意，我们在每一行的开头都有一些奇怪的字符。发生这种情况是因为传输日志的流被[多路复用](https://docs.docker.com/engine/api/v1.40/#operation/ContainerAttach)以区分stderr和stdout。

因此，输出需要进一步处理。

我们可以通过在创建容器时启用TTY选项来避免这种情况：

```shell
(alpine) $ cat create.json

{
  "Tty":true,	
  "Image": "hello-world",
  "Cmd": ["/hello"]
}
```

## 4. 总结

在本教程中，我们学习了如何使用Docker Engine Remote API。

我们首先从 UNIX 套接字或 TCP 设置远程访问，然后进一步展示我们如何使用远程 API。
