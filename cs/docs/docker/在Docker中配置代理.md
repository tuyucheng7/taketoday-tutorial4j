---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

我们将为Docker Engine和Docker Client提供代理设置，以便在使用Docker时不允许直接访问互联网时，它们可以连接到互联网。当我们在企业网络或私有云中使用Docker时，我们通常可能必须通过代理服务器连接到互联网。在这些情况下，我们需要使用代理。

在本教程中，我们将学习如何解决在使用Docker配置代理时可能遇到的问题。

## 2. 什么是代理

代理服务器控制和路由请求用户和网站之间的流量。代理旨在保护用户并维护网络安全和隐私政策。在没有代理的情况下，用户直接向目标服务器发送请求并接收响应：

[![泊坞窗代理 2](https://www.baeldung.com/wp-content/uploads/2022/05/docker-proxy-2-e1650617273178.png)](https://www.baeldung.com/wp-content/uploads/2022/05/docker-proxy-2.png)

当我们使用代理时，我们的请求先到代理服务器，然后代理访问目标服务器。如下图所示，代理位于客户端和目标服务器之间，客户端的每一个请求都会先到达代理，然后由代理提供对目标服务器的访问：

[![码头代理 1](https://www.baeldung.com/wp-content/uploads/2022/05/docker-proxy-1-e1650617401563.png)](https://www.baeldung.com/wp-content/uploads/2022/05/docker-proxy-1.png)

## 3.配置Docker代理

在Docker17.07 及更高版本中，我们可以配置Docker客户端自动将代理信息传递给容器。在Docker17.06 及更早版本中，我们可以通过环境变量来设置Docker客户端代理设置。

让我们将以下 JSON 示例添加到~/.docker/config.json文件并完成我们的代理设置。支持使用  字符作为主机的通配符，并支持对 IP 地址使用 CIDR 表示法：

```json
{ 
  "proxies":
    { 
      "default": 
        { 
          "httpProxy": "http://<ip-address>:<port>", 
          "httpsProxy": "https://<ip-address>:<port>", 
          "noProxy": "*.<domain>,127.0.0.0/8" 
        } 
    } 
}


```

当我们保存更改时，将使用config.json文件中指定的环境变量创建每个Docker容器，并且我们的代理设置将有效。

## 4.代理服务器设置

我们应该使用HTTP_PROXY、HTTPS_PROXY、FTP_PROXY和NO_PROXY环境变量来为Docker守护进程配置代理服务。让我们详细看看这些变量：

-   HTTP_PROXY 是一种代理，充当客户端和 Web 服务器之间的中间服务器。使用HTTP代理服务器，请求不会转到网站；它以纯文本形式发送给代理。代理对此进行分析，然后通过(可选)使用随请求提供的数据更改我们的 IP 地址，向网站发送新请求。该网站收到它并向代理发送响应。然后代理将响应转发给我们。
-   HTTPS_PROXY比HTTP代理更安全、更匿名。HTTPS 协议不以纯文本格式传输数据。SSL 层对数据进行加密，以便第三方永远不会看到它。
-   FTP_PROXY管理主动和被动 FTP 会话。它还保护FTP服务器并限制客户端和服务器之间的FTP协议命令。
-   NO_PROXY设置用于指定不应使用代理的地址。

## 5.手动配置代理设置

在Docker17.07 及更早版本中，我们必须使用–env标志设置代理设置：

```shell
docker run [docker_image] --env FTP_PROXY="ftp://<ip-address>:<port>"
docker run [docker_image] --env HTTP_PROXY="http://<ip-address>:<port>"
docker run [docker_image] --env HTTPS_PROXY="https://<ip-address>:<port>"
docker run [docker_image] --env NO_PROXY="*.<domain>,127.0.0.0/8"
```

或者，我们必须将它们添加到我们的Dockerfile中：

```shell
ENV FTP_PROXY="ftp://<ip-address>:<port>"
ENV HTTP_PROXY="http://<ip-address>:<port>"
ENV HTTPS_PROXY="https://<ip-address>:<port>"
ENV NO_PROXY="*.<domain>,127.0.0.0/8"
```

通过这些操作，我们现在可以执行我们的Docker代理操作。

## 六，总结

在本教程中，我们了解了代理是什么以及如何在不同版本的Docker中设置它。
