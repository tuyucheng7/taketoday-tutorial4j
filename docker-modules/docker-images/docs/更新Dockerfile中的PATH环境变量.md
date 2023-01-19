## 1. 概述

在本文中，我们介绍如何在Docker中更新PATH变量。首先，我们在全局范围内更新它。然后，我们将更改限制为指令的一个子集。

## 2. 更新全局PATH变量

**ENV语句可用于更新PATH变量**，我们通过编写一个示例[Dockerfile]()来演示这种行为：

```dockerfile
FROM ubuntu:latest
RUN echo $PATH
ENV PATH="$PATH:/etc/profile"
RUN echo $PATH
```

第一行声明我们使用最新的Ubuntu映像，我们还在ENV指令之前和之后记录PATH变量的值。

然后构建该镜像：

```shell
$ docker build -t tuyuchengimage .
#4 [1/3] FROM docker.io/library/ubuntu:latest
#5 [2/3] RUN echo /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
#5 0.683 /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
#6 [3/3] RUN echo /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/etc/profile
#6 0.893 /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/etc/profile
```

正如预期的那样，/etc/profile已附加到PATH。

## 3. 仅为指令序列更新PATH

**我们使用RUN指令运行shell脚本以[导出新的PATH](https://www.baeldung.com/linux/path-variable#adding-to-path)**。之后，我们将在同一RUN语句中添加另一条指令，这将在RUN语句中打印PATH的本地值。然后我们还将记录PATH全局变量以确认它没有改变。

下面是新的Dockerfile：

```dockerfile
FROM ubuntu:latest
RUN echo $PATH
RUN export PATH="$PATH:/etc/profile"; echo $PATH
RUN echo $PATH
```

现在可以构建镜像：

```shell
$ docker build -t tuyuchengimage . 
#7 [1/4] FROM docker.io/library/ubuntu:latest
#4 [2/4] RUN echo /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
#4 0.477 /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
#5 [3/4] RUN export PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/etc/profile"; echo /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
#5 0.660 /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/etc/profile
#6 [4/4] RUN echo /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
#6 0.661 /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
```

日志确认在导出PATH变量后，它的值被同一RUN命令中的其他指令使用。但是，全局变量没有改变。

## 4. 更新Shell会话内的PATH

现在让我们看看如何只为会话更新PATH。首先，我们修改.bashrc文件以在每个会话开始时更新PATH。然后，我们将启动一个会话来观察这种行为。

### 4.1 编辑.bashrc文件

**我们需要编辑[.bashrc文件]()以在每个会话开始时导出一个新的PATH**。为此，我们运行一个[快速脚本]()以将导出附加到原始文件中。正如我们之前所做的那样，我们会检查此更改不会影响全局PATH变量。

这是新的Dockerfile：

```dockerfile
FROM ubuntu:latest
RUN echo $PATH
RUN echo "export PATH=$PATH:/etc/profile" >> ~/.bashrc
RUN cat ~/.bashrc
RUN echo $PATH
```

此外，请注意我们使用了[cat]()命令来查看文件。

然后构建镜像：

```shell
$ docker build -t tuyuchengimage . 
#4 [1/5] FROM docker.io/library/ubuntu:latest
#5 [2/5] RUN echo /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
#5 0.447 /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
#6 [3/5] RUN echo "export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/etc/profile" >> ~/.bashrc
#7 [4/5] RUN cat ~/.bashrc
#7 0.956 # ~/.bashrc: executed by bash(1) for non-login shells.
#7 0.956 # see /usr/share/doc/bash/examples/startup-files (in the package bash-doc)
#7 0.956 # for examples
#7 0.956
#7 0.956 # If not running interactively, don't do anything
#7 0.956 [ -z "$PS1" ] && return
[... .bashrc full content]
#7 0.956 export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/etc/profile
#7 DONE 1.0s
#8 [5/5] RUN echo /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
#8 0.867 /usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
```

如我们所见，全局PATH并没有改变。但是，确实在文件末尾添加了导出行。因此，无论何时加载.bashrc，它都会更新正在运行的的PATH变量。

### 4.2 以交互模式运行容器

现在让我们关注之前看到的输出中.bashrc文件的前几行。这些行来自原始文件，它明确指出“If not running interactively, don't do anything(如果不以交互方式运行，则不要做任何事情)”。

当我们构建Dockerfile时，RUN命令不是交互式的，理解这一点至关重要。因此，我们将无法仅获取我们的.bashrc文件并在构建期间运行脚本来检查PATH是否已更新。

**相反，我们可以[以交互模式运行容器]()并打开会话**：

```shell
$ docker run -it --name interactiveimage tuyuchengimage
root@18781222594f:/# echo $PATH
/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/etc/profile
```

进入会话后，我们打印了PATH。并且可以看到附加了/etc/profile，可以确认我们的.bashrc文件已被考虑在内。

## 5. 总结

在本教程中，我们了解了如何在Docker中更新PATH变量。首先，我们在全局范围内更新了变量，然后也学习了如何使用更多限制来更新它。