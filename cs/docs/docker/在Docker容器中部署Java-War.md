---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

[在本教程中，我们将学习在Docker](https://www.baeldung.com/ops/docker-guide)容器中部署Java WAR文件。

[我们将在Apache Tomcat](https://tomcat.apache.org/)上部署 WAR 文件，Apache Tomcat 是一种在Java社区中广泛使用的免费开源 Web 服务器。

## 2.部署一个WAR文件到Tomcat

WAR(Web Application Archive)是一个压缩的归档文件，它打包了所有与 Web 应用程序相关的文件及其目录结构。

为了简单起见，[在 Tomcat 上部署一个 WAR 文件](https://www.baeldung.com/tomcat-deploy-war)只不过是将该 WAR 文件复制到 Tomcat 服务器的部署目录中。Linux 中的部署目录是$CATALINA_HOME/webapps。$CATALINA_HOME表示Tomcat服务器的安装目录。

在此之后，我们需要重新启动 Tomcat 服务器，这将提取部署目录中的 WAR 文件。

## 3.在Docker容器中部署WAR

假设我们的应用程序有一个 WAR 文件ROOT.war，我们需要将其部署到 Tomcat 服务器。

为了实现我们的目标，我们需要先创建一个Dockerfile。这个Dockerfile将包含运行我们的应用程序所需的所有依赖项。

此外，我们将使用此Dockerfile创建一个Docker镜像，然后执行启动Docker容器的步骤。

现在让我们逐一深入研究这些步骤。

### 3.1. 创建Dockerfile

[我们将使用Tomcat 的](https://hub.docker.com/_/tomcat)最新Docker镜像作为Dockerfile的基础镜像。使用此镜像的优点是所有必要的依赖项/包都已预安装。例如，如果我们使用最新的 Ubuntu/CentOSDocker镜像，那么我们需要手动安装 Java、Tomcat 和其他所需的包。

由于已经安装了所有必需的包，我们需要做的就是将 WAR 文件ROOT.war复制到 Tomcat 服务器的部署目录中。就是这样！

让我们仔细看看：

```shell
$ ls
Dockerfile  ROOT.war
$ cat Dockerfile 
FROM tomcat

COPY ROOT.war /usr/local/tomcat/webapps/
```

$CATALINA_HOME/webapps表示 Tomcat 的部署目录。这里，Tomcat 的官方Docker镜像的 CATALINA_HOME是/usr/local/tomcat。结果，完整的部署目录变成了 /usr/local/tomcat/webapps。

我们在这里使用的应用程序非常简单，不需要任何其他依赖项。

### 3.2. 构建Docker镜像

现在让我们使用刚刚创建的Dockerfile创建Docker镜像：

```shell
$ pwd
/baeldung
$ ls
Dockerfile  ROOT.war
$ docker build -t myapp .
Sending build context to Docker daemon  19.97kB
Step 1/2 : FROM tomcat
 ---> 710ec5c56683
Step 2/2 : COPY ROOT.war /usr/local/tomcat/webapps/
 ---> Using cache
 ---> 8b132ab37a0e
Successfully built 8b132ab37a0e
Successfully tagged myapp:latest

```

docker build命令将创建一个带有myapp 标签的Docker镜像。 

确保从Dockerfile所在的目录中构建Docker镜像。在上面的示例中，我们在构建Docker镜像时位于/baeldung目录中。

### 3.3. 运行Docker容器

到目前为止，我们已经创建了一个Dockerfile并从中构建了一个Docker镜像。现在让我们运行Docker容器：

```shell
$ docker run -itd -p 8080:8080 --name my_application_container myapp
e90c61fdb4ac85b198903e4d744f7b0f3c18c9499ed6e2bbe2f39da0211d42c0
$ docker ps 
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS                    NAMES
e90c61fdb4ac        myapp               "catalina.sh run"   6 seconds ago       Up 5 seconds        0.0.0.0:8080->8080/tcp   my_application_container

```

此命令将使用Docker镜像myapp启动名为 my_application_container 的Docker容器。 

Tomcat 服务器的默认端口是 8080。因此，在启动Docker容器时，确保始终将容器端口 8080 与任何可用的主机端口绑定。为简单起见，我们在这里使用了主机端口 8080。

### 3.4. 验证设置

现在让我们验证我们到目前为止所做的一切。我们将在浏览器中访问 URL http://<IP>:<PORT>以查看应用程序。

此处，IP表示Docker主机的公共 IP(或在某些情况下为私有 IP)。PORT是我们在运行Docker容器时公开的容器端口(在我们的例子中是 8080)。

[我们还可以在Linux中使用curl](https://curl.se/docs/manpage.html)实用程序验证设置：

```shell
$ curl http://localhost:8080
Hi from Baeldung!!!
```

在上面的命令中，我们从Docker主机执行命令。因此，我们能够使用本地主机连接到应用程序。作为响应，curl实用程序打印应用程序网页的原始 HTML。

## 4. 总结

在本文中，我们学习了如何在Docker容器中部署Java WAR文件。我们首先使用官方 TomcatDocker镜像创建Dockerfile。然后，我们构建了Docker镜像并运行了应用程序容器。

最后，我们通过访问应用程序 URL 验证了设置。
