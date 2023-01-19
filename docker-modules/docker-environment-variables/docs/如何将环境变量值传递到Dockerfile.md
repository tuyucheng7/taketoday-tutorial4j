## 1. 概述

环境变量是外部化应用程序配置的便捷方式；因此，它们对于构建Docker容器也很有用。但是，在Dockerfile中传递和使用它们并不像想象的那么容易。

在这个简短的教程中，我们学习如何将环境变量值传递到Dockerfile中。

首先，我们演示将环境变量传递给构建过程可能有用的用例，然后我们介绍如何使用ARG命令来实现这一目的。

## 2. Dockerfile与容器中的环境变量

**Dockerfile是一个脚本，其中包含有关如何构建Docker镜像的说明**。相反，**Docker容器是镜像的可运行实例**。根据我们的需要，我们可能需要构建时或[运行时环境变量]()。

在本文中，我们将通过将环境变量传递到我们的Dockerfile中以用于docker构建，只关注构建时自定义。

## 3. 在Dockerfile中使用环境变量的优势

使用环境变量的最大优点是灵活性，我们可以只创建一个Dockerfile，它将根据**用于构建容器的环境进行不同的配置**。作为说明，我们假设一个应用程序在开发环境中启用了调试选项，而在生产环境中禁用了相同的选项。对于环境变量，我们只需要创建一个Dockerfile，它将一个包含调试标志的环境变量传递给容器和其中的应用程序。

另一个重要的优势是安全问题。将密码或其他敏感信息直接存储在Dockerfile中可能不是最好的主意，而环境变量有助于克服这个问题。

## 4. 示例配置

在我们了解如何将环境变量值传递到Dockerfile之前，让我们构建一个示例来测试它。

我们将创建一个名为greetings.sh的简单shell脚本，它使用环境变量将输出语句打印到控制台：

```shell
#!/bin/sh

echo Hello $env_name
```

现在我们在同一目录中创建一个Dockerfile：

```dockerfile
FROM alpine:latest

COPY greetings.sh .

RUN chmod +x /greetings.sh

CMD ["/greetings.sh"]
```

它复制我们的脚本，使其可执行，然后运行它。接下来我们构建镜像：

```shell
docker build -t tuyucheng_greetings .
```

然后运行它：

```shell
docker run tuyucheng_greetings
```

我们应该在控制台中看到以下输出：

```shell
Hello
```

## 5. 将环境变量传递到Dockerfile

Dockerfile提供了一个专门的变量类型ENV来创建环境变量。**我们可以在构建期间以及容器运行后访问ENV值**。

让我们看看如何使用它向我们的greetins脚本传递一个值，有两种不同的方法可以做到这一点。

### 5.1 硬编码环境值

传递环境值的最简单方法是在Dockerfile中对其进行硬编码，在某些情况下，这已经足够了。下面我们在Dockerfile中将John硬编码为默认名称：

```dockerfile
FROM alpine:latest

ENV env_name John

COPY greetings.sh .

RUN chmod +x /greetings.sh

CMD ["/greetings.sh"]
```

现在我们构建并运行镜像，下面是控制台的输出：

```shell
Hello John
```

### 5.2 设置动态环境值

Dockerfile不提供在构建过程中设置ENV值的动态工具。但是，这个问题有一个解决方案，即我们必须使用ARG。**ARG值的工作方式与ENV不同，因为一旦构建了镜像，我们就无法再访问它们**。让我们看看如何解决这个问题：

```dockerfile
ARG name
ENV env_name $name
```

我们引入name ARG变量，然后使用它为使用ENV的env_name环境变量赋值。 

当我们想要设置这个参数时，我们需要使用–build-arg标志传递它：

```shell
docker build -t tuyucheng_greetings --build-arg name=Tuyucheng .
```

现在运行我们的容器，我们应该看到以下输出：

```shell
Hello Tuyucheng
```

如果我们想更改名称怎么办？我们所要做的就是用不同的build-arg值重新构建镜像。

## 6. 总结

在本文中，我们学习了如何在构建Dockerfile期间设置环境变量。

首先，我们介绍了参数化Dockerfile的优势。然后我们演示了如何使用ENV命令设置环境变量，以及如何使用ARG允许在构建时修改此值。