---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

在Dockerfile中，我们经常会遇到诸如run、cmd或entrypoint之类的指令。乍一看，都是用来指定和运行命令的。但是它们之间有什么区别呢？他们如何互动？

在本教程中，我们将回答这些问题。我们将介绍每条指令的作用以及它们的工作原理。我们还将了解它们在构建镜像和运行[Docker容器](https://www.baeldung.com/docker-images-vs-containers)中扮演的角色。

## 2.设置

首先，让我们创建一个脚本log-event.sh。它只是向文件中添加一行，然后打印出来：

```shell
#!/bin/sh

echo `date` $@ >> log.txt;
cat log.txt;
```

现在，让我们创建一个简单的Dockerfile：

```shell
FROM alpine
ADD log-event.sh /
```

它将通过在不同场景中向log.txt附加行来使用我们的脚本。

## 3.运行命令

运行指令在我们构建镜像时执行。这意味着传递给run的命令在新层中的当前镜像之上执行。然后将结果提交给镜像。让我们看看实际效果如何。

首先，我们将向Dockerfile添加一条运行指令：

```shell
FROM alpine
ADD log-event.sh /
RUN ["/log-event.sh", "image created"]
```

其次，让我们建立我们的形象：

```shell
docker build -t myimage .
```

现在我们希望有一个包含log.txt文件的Docker镜像，其中包含一个镜像创建行。让我们通过运行基于镜像的容器来检查这一点：

```shell
docker run myimage cat log.txt
```

列出文件内容时，我们会看到如下输出：

```shell
Fri Sep 18 20:31:12 UTC 2020 image created
```

如果我们多次运行容器，我们会看到日志文件中的日期没有改变。这是有道理的，因为运行步骤是在镜像构建时执行的，而不是在容器运行时执行的。

现在让我们再次构建我们的形象。我们注意到日志中的创建时间没有改变。发生这种情况是因为如果Dockerfile没有更改，Docker会缓存运行指令的[结果。](https://www.baeldung.com/linux/docker-build-cache)如果我们想要使缓存失效，我们需要将–no-cache选项传递给构建命令。

## 4.cmd命令_

使用cmd指令，我们可以指定在容器启动时执行的默认命令。让我们在Dockerfile中添加一个cmd条目，看看它是如何工作的：

```shell
...
RUN ["/log-event.sh", "image created"]
CMD ["/log-event.sh", "container started"]
```

构建镜像后，让我们运行它并检查输出：

```shell
$ docker run myimage
Fri Sep 18 18:27:49 UTC 2020 image created
Fri Sep 18 18:34:06 UTC 2020 container started
```

如果我们多次运行这个，我们会看到创建的镜像条目保持不变。但是容器在每次运行时都开始更新条目。这显示了每次容器启动时cmd确实是如何执行的。

请注意，这次我们使用了一个稍微不同的docker run命令来启动我们的容器。让我们看看如果我们运行与之前相同的命令会发生什么：

```shell
$ docker run myimage cat log.txt
Fri Sep 18 18:27:49 UTC 2020 image created
```

这次Dockerfile中指定的cmd被忽略。那是因为我们已经为docker run命令指定了参数。

现在让我们继续，看看如果我们在Dockerfile中有多个cmd条目会发生什么。让我们添加一个将显示另一条消息的新条目：

```shell
...
RUN ["/log-event.sh", "image created"]
CMD ["/log-event.sh", "container started"]
CMD ["/log-event.sh", "container running"]
```

构建镜像并再次运行容器后，我们会发现以下输出：

```shell
$ docker run myimage
Fri Sep 18 18:49:44 UTC 2020 image created
Fri Sep 18 18:49:58 UTC 2020 container running
```

正如我们所看到的，容器启动条目不存在，只有容器正在运行。这是因为如果指定了多个，则只会调用最后一个 cmd。

## 5.入口点命令

正如我们在上面看到的，如果在启动容器时传递任何参数，cmd将被忽略。如果我们想要更大的灵活性怎么办？假设我们想要自定义附加文本并将其作为参数传递给docker run命令。为此，让我们使用入口点。我们将指定在容器启动时运行的默认命令。此外，我们现在能够提供额外的参数。

让我们用入口点替换Dockerfile中的cmd条目：

```shell
...
RUN ["/log-event.sh", "image created"]
ENTRYPOINT ["/log-event.sh"]
```

现在让我们通过提供自定义文本条目来运行容器：

```shell
$ docker run myimage container running now
Fri Sep 18 20:57:20 UTC 2020 image created
Fri Sep 18 20:59:51 UTC 2020 container running now
```

我们可以看到entrypoint 的行为与cmd相似。此外，它还允许我们自定义启动时执行的命令。

与cmd一样，如果有多个入口点条目，则只考虑最后一个。

## 6.cmd与entrypoint的交互

我们使用了cmd和entrypoint来定义运行容器时执行的命令。现在让我们继续看看如何结合使用cmd和入口点。

一个这样的用例是为入口点定义默认参数。让我们在Dockerfile的入口点之后添加一个cmd条目：

```shell
...
RUN ["/log-event.sh", "image created"]
ENTRYPOINT ["/log-event.sh"]
CMD ["container started"]
```

现在，让我们在不提供任何参数的情况下运行我们的容器，并使用cmd中指定的默认值：

```shell
$ docker run myimage
Fri Sep 18 21:26:12 UTC 2020 image created
Fri Sep 18 21:26:18 UTC 2020 container started
```

如果我们选择的话，我们也可以覆盖它们：

```shell
$ docker run myimage custom event
Fri Sep 18 21:26:12 UTC 2020 image created
Fri Sep 18 21:27:25 UTC 2020 custom event
```

需要注意的是入口点以其 shell 形式使用时的不同行为。让我们更新Dockerfile中的入口点：

```shell
...
RUN ["/log-event.sh", "image created"]
ENTRYPOINT /log-event.sh
CMD ["container started"]
```

在这种情况下，当运行容器时，我们将看到Docker如何忽略传递给docker run或cmd的任何参数。

## 七、总结

在本文中，我们了解了Docker指令之间的异同：run、cmd和entrypoint。我们已经观察到它们在什么时候被调用。此外，我们还研究了它们的用途以及它们如何协同工作。
