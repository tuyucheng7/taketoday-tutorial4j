---
layout: post
title:  使用Selenium处理浏览器选项卡
category: docker
copyright: docker
excerpt: Docker
---

## 1. 概述

将我们的服务与其配置分开通常是个好主意。对于[十二要素应用程序](https://www.baeldung.com/spring-boot-12-factor)，我们应该将配置存储在环境中。

当然，这意味着我们需要一种方法将配置注入到我们的服务中。

[在本教程中，我们将通过将环境变量传递给Docker](https://www.baeldung.com/tag/docker/)容器来实现这一点。

## 2.使用-env、-e

在本教程中，我们将使用一个名为 Alpine 的小型 (5MB)Linux镜像。让我们从本地拉取镜像开始：

```shell
docker pull alpine:3
```

当我们启动Docker容器时，我们可以使用参数–env(或其缩写形式-e)将环境变量作为键值对直接传递到命令行中。

例如，让我们执行以下命令：

```shell
$ docker run --env VARIABLE1=foobar alpine:3 env

```

简而言之，我们正在将我们设置回控制台的环境变量反映出来：

```shell
VARIABLE1=foobar
```

可以看出，Docker容器正确解释了变量VARIABLE1。

此外，如果变量已经存在于本地环境中，我们可以在命令行中省略该值。

例如，让我们定义一个本地环境变量：

```shell
$ export VARIABLE2=foobar2
```

然后，让我们指定没有值的环境变量：

```shell
docker run --env VARIABLE2 alpine:3 env
```

我们可以看到Docker仍然从周围环境中获取值：

```shell
VARIABLE2=foobar2
```

## 3.使用-env文件

当变量数量较少时，上述解决方案就足够了。然而，一旦我们有多个变量，它很快就会变得麻烦且容易出错。

另一种解决方案是使用文本文件来存储我们的变量，使用标准的键=值格式。

让我们在一个名为my-env.txt 的文件中定义一些变量：

```shell
$ echo VARIABLE1=foobar1 > my-env.txt
$ echo VARIABLE2=foobar2 >> my-env.txt
$ echo VARIABLE3=foobar3 >> my-env.txt
```

现在，让我们将这个文件注入到我们的Docker容器中：

```shell
$ docker run --env-file my-env.txt alpine:3 env
```

最后，让我们看一下输出：

```shell
VARIABLE1=foobar1
VARIABLE2=foobar2
VARIABLE3=foobar3
```

## 4. 使用Docker组合

Docker Compose还提供了定义环境变量的工具。对于那些对此特定主题感兴趣的人，请查看我们的[Docker Compose教程](https://www.baeldung.com/docker-compose#managing-environment-variables) 以了解更多详细信息。

## 5. 提防敏感值

通常，其中一个变量是数据库或外部服务的密码。我们必须小心将这些变量注入Docker容器的方式。

通过命令行直接传递这些值可能是最不安全的，因为在我们不期望的地方泄漏敏感值的风险更大，例如在我们的源代码控制系统或操作系统进程列表中。

在本地环境或文件中定义敏感值是更好的选择，因为两者都可以防止未经授权的访问。

然而，重要的是要认识到任何有权访问Docker运行时的用户都可以检查正在运行的容器并发现秘密值。

让我们检查一个正在运行的容器：



```shell
docker inspect 6b6b033a3240
```

输出显示环境变量：

```javascript
"Config": {
    // ...
    "Env": [
       "VARIABLE1=foobar1",
       "VARIABLE2=foobar2",
       "VARIABLE3=foobar3",
    // ...
    ]
}
```

对于那些关注安全性的情况，重要的是要提到Docker提供了一种称为[Docker Secrets](https://docs.docker.com/engine/swarm/secrets/)的机制。[容器服务，如Kubernetes](https://www.baeldung.com/kubernetes) 、AWS 或 Azure提供的服务，也提供类似的功能。

## 六，总结

在这个简短的教程中，我们了解了几种将环境变量注入Docker容器的不同选项。

虽然每种方法都运行良好，但我们的选择最终将取决于各种参数，例如安全性和可维护性。
