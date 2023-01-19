## 1. 概述

一般来说，Docker [build](https://docs.docker.com/engine/reference/commandline/build/)命令限制了我们可以在Docker镜像中使用的文件源。我们指定一个构建上下文，它是必须从中找到Dockerfile及其所有依赖文件的根。

然而，有时我们可能希望将文件系统一部分的Dockerfile与另一部分的文件一起使用。

在这个简短的教程中，我们介绍克服此限制的几种方法，以及它们的优缺点。

## 2. Docker构建及其上下文

### 2.1 传统的Docker构建

我们从一个例子开始(一个简单的nginx应用程序)，它有一个HTML文件和一个Dockerfile，目录结构为：

```powershell
projects
├ <some other projects>...
└── sample-site
       ├── html
       │    └── index.html
       └── Dockerfile
```

下面是Dockerfile：

```dockerfile
FROM nginx:latest
COPY html/ /etc/nginx/html/
```

现在，要为这个应用程序构建一个镜像，我们运行以下命令：

```shell
$ docker build -t sample-site:latest .
```

这里，通过“.”参数将构建上下文设置为当前目录。

将Dockerfile保存在项目根目录是一种常见的做法。默认情况下，该命令要求Dockerfile在sample-site文件夹中，我们想要包含在镜像中的所有文件都应该存在于该上下文中的某个位置。

### 2.2 更复杂的构建场景

有时传统的方法可能对我们不起作用。例如，我们可能会根据环境使用不同的Docker或docker-compose文件。或者，我们可能会将容器添加为独立于开发的活动。

在这些情况下，将与Docker相关的文件移动到单独的目录是有意义的。类似地，在某些情况下，我们可能会将镜像的配置文件保存在项目根目录之外。

不幸的是，Docker阻止我们从文件系统的任意部分添加文件，因为这可能会导致一个安全漏洞。但是，有一些解决方法：

-   在更大的上下文中构建以包含所需的一切
-   使用位于上下文之外的文件创建基础映像，然后扩展基础映像
-   复制所有必要的文件以创建临时上下文并从中构建镜像

## 3. 构建更大的环境

假设我们想将Dockerfile移动到一个名为docker的单独目录中，我们还希望使用项目根目录sample-site之外的config目录中的自定义配置文件覆盖标准的nginx配置。下面是新的目录结构：

```powershell
projects
├ <some other projects>...
├── sample-site
│      ├── html
│      │    └── index.html
│      └── docker
│           └── Dockerfile
└── config
      └── nginx.conf
```

在这种情况下，早期的Dockerfile和docker build命令都不起作用了。为了让它再次工作，**我们必须在更大的上下文中构建它，也就是projects目录**。

### 3.1 更改Dockerfile

既然我们的上下文已经改变，我们也需要更改我们的Dockerfile：

```dockerfile
FROM nginx:latest
COPY sample-site/html/ /etc/nginx/html/
COPY config/nginx.conf /etc/nginx/nginx.conf
```

这里根据新上下文修改了html目录的路径，并且包含了nginx.conf。

### 3.2 构建镜像

现在，让我们转到projects目录并运行命令来构建镜像：

```shell
$ cd projects
$ docker build -f sample-site/docker/Dockerfile -t sample-site:latest .
```

在这里，我们再次使用“.”作为上下文，因为我们正在从projects目录运行命令。这会将Dockerfile和nginx.conf带入当前构建上下文中，由于Dockerfile不在上下文目录的根目录中，我们使用-f选项提供Dockerfile文件的路径。

这种方法的问题是Docker客户端将构建上下文的副本(整个projects目录)发送到Docker守护进程，该目录可能包含许多其他不相关的文件和目录。因此，这可能需要Docker扫描大量资源，从而导致构建过程缓慢。

## 4. 使用外部文件创建基础镜像

另一种方法是**使用外部文件创建一个基础映像，然后再对其进行扩展**。我们重用与前面示例中相同的目录结构：

```powershell
projects
├ <some other projects>...
├── sample-site
│      ├── html
│      │    └── index.html
│      └── docker
│           └── Dockerfile
└── config
       └── nginx.conf
```

### 4.1 为基础镜像编写Dockerfile

首先，我们使用nginx.conf编写一个Dockerfile：

```dockerfile
FROM nginx:latest
COPY nginx.conf /etc/nginx/nginx.conf
```

该文件放入projects/config目录。

### 4.2 建立基础镜像

下一步是在projects/config中运行build命令来创建基础镜像：

```shell
$ docker build -t sample-site-base:latest .
```

现在，我们有一个名为sample-site-base:latest的Docker镜像，其中包含nginx服务器和配置文件。

### 4.3 为扩展镜像编写Dockerfile

接下来，我们编写sample-site的Dockerfile来扩展sample-site-base：

```dockerfile
FROM sample-site-base:latest
COPY html/ /etc/nginx/html/
```

### 4.4 构建扩展镜像

最后，我们在projects/sample-site中运行命令来构建我们应用程序的镜像：

```shell
$ docker build -f docker/Dockerfile -t sample-site:latest .
```

在这里，我们将Docker构建分为两个独立的阶段，每个阶段都与我们项目中的不同目录树相关。

这种方法在其子镜像中重用Dockerfile的公共部分，这种结构相对容易维护。如果基础镜像有任何变化，我们需要重新构建子镜像，同样的变化会反映在所有子镜像中。

同时应该注意，**这种方法会增加我们最终Docker镜像中的层数**。

## 5. 创建一个临时上下文

最后一种方法是**创建一个定制的临时上下文来构建镜像**，这使用围绕我们的docker build命令的一些脚本将必要的文件拉入Docker友好的目录结构。

### 5.1 创建临时目录

首先，让我们创建一个临时目录并复制所有必要的资源：

```shell
$ mkdir tmp-context
$ cp -R ../html tmp-context/
$ cp -R ../../config tmp-context/
```

该目录将是我们的构建上下文。

### 5.2 创建Dockerfile

现在，我们编写与此上下文相关的Dockerfile：

```dockerfile
FROM nginx:latest
COPY html/ /etc/nginx/html/
COPY config/nginx.conf /etc/nginx/nginx.conf
```

我们已经将需要的文件放在了tmp-context中，所以我们不需要在这里指定任何外部路径。

### 5.3 构建镜像

然后运行命令来构建镜像：

```shell
$ cd tmp-context
$ docker build -t sample-site:latest .
```

此命令将tmp-context作为构建上下文，它可以在目录中找到所需的所有内容，因此可以毫无问题地构建镜像。

### 5.4 清理

最后，我们清理临时目录：

```shell
$ rm -rf tmp-context
```

这是将文件从任何位置添加到Docker映像的最简介的方法。在这里，我们可以完全控制如何创建我们的上下文，并且可以使用上述所有命令创建一个shell脚本来简化整个过程。

但是，我们应该知道的是某些文件可能很大。它们可能需要很长时间才能复制到上下文中，这在每个构建中都会发生。

## 6. 总结

在本文中，我们了解了Docker通常如何从与Dockerfile相同目录中的文件构建镜像。我们介绍了一些从常规构建上下文之外添加文件的解决方案，并探讨了每种解决方案的优缺点。
