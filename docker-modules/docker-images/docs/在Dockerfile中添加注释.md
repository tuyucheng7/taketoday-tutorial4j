## 1. 概述

在本教程中，我们介绍如何在[Dockerfile]()中添加注释，并强调看似是注释但并非注释的指令之间的区别。

## 2. 向Dockerfile添加注释

我们使用以下Dockerfile：

```dockerfile
FROM ubuntu:latest
RUN echo 'This is a Tuyucheng tutorial'
```

下面是一些说明：

-   第一行表明我们使用最新的ubuntu镜像
-   第二行将[echo命令]()作为shell的参数传递

接下来我们构建[镜像]()：

```shell
$ docker build -t tuyuchengimage .
#4 [1/2] FROM docker.io/library/ubuntu:latest
#5 [2/2] RUN echo 'This is a Tuyucheng tutorial'
```

Docker打印成功运行的两个步骤(在我们没有列出的其他行中)，现在让我们看看如何向Dockerfile添加注释。

### 2.1 单行注释

**要注释一行内容，它必须以#开头**。

下面修改我们的Dockerfile，添加一些单行注释：

```dockerfile
# Declare parent image
FROM ubuntu:latest
# Print sentence
RUN echo 'This is a Tuyucheng tutorial'
```

然后我们构建修改后的镜像：

```shell
$ docker build -t tuyuchengimage .
#4 [1/2] FROM docker.io/library/ubuntu:latest
#5 [2/2] RUN echo 'This is a Tuyucheng tutorial'
```

正如预期的那样，Docker成功运行了与之前相同的两个步骤。

### 2.2 多行注释

Docker中没有专门的语法来编写多行注释，**因此，编写多行注释的唯一方法是编写多个单行注释**：

```dockerfile
# This file is a demonstration
# For a Tuyucheng article
FROM ubuntu:latest
RUN echo 'This is a Tuyucheng tutorial'
```

构建镜像仍然打印与之前相同的步骤：

```shell
$ docker build -t tuyuchengimage .
#4 [1/2] FROM docker.io/library/ubuntu:latest
#5 [2/2] RUN echo 'This is a Tuyucheng tutorial'
```

## 3. 避免陷阱

在本节中，我们介绍应该注意的几个陷阱。这些是棘手的代码行，它们看起来像注释，但实际上不是。

### 3.1 注释或命令参数？

**在Docker中，不可能在行尾添加注释**。让我们看看如果我们尝试在一条指令的末尾添加一个句子(格式类似于单行注释)会发生什么：

```dockerfile
FROM ubuntu:latest
RUN echo 'This is a Tuyucheng tutorial' # Print sentence
```

现在构建镜像：

```shell
$ docker build -t tuyuchengimage .
#4 [1/2] FROM docker.io/library/ubuntu:latest
#5 [2/2] RUN echo 'This is a Tuyucheng tutorial' # Print sentence
#5 0.371 This is a Tuyucheng tutorial
```

输出包含了echo命令打印的句子，以强调结果确实与我们之前的结果相同。那么这里发生了什么？我们真的在Dockerfile的第二行末尾添加的是注释吗？

实际上，#Print语句是作为附加参数传递给RUN命令的，在本例中，附加参数恰好被该命令忽略了。为了更好的证明这一点，现在我们在Dockerfile的第一行末尾添加一个类似的句子：

```dockerfile
FROM ubuntu:latest # Declare parent image
RUN echo 'This is a Tuyucheng tutorial'
```

然后再次构建这个镜像：

```shell
$ docker build -t tuyuchengimage .
failed to solve with frontend dockerfile.v0: failed to create LLB definition: dockerfile parse error line 1: FROM requires either one or three arguments
```

在这里，我们得到了一条非常明确的错误消息，这表明了我们的肯定。

### 3.2 解析器指令不是注释

**解析器指令告诉Dockerfile解析器如何处理文件的内容**。与注释类似，解析器指令以#开头。

此外，我们应该注意解析器指令必须位于Dockerfile的顶部。例如，我们将在我们的文件中使用转义解析器指令，该指令更改文件中使用的转义字符：

```dockerfile
# escape=`
FROM ubuntu:latest
RUN echo 'This is a Tuyucheng tutorial&' `
  && echo 'Print more stuff'
```

在这里，我们在RUN命令中添加了另一个echo指令。为了提高可读性，我们将这条指令换行，默认的行分隔符是\。但是，由于我们使用了解析器指令，所以我们需要使用`来代替。现在我们再次构建镜像，看看会发生什么：

```shell
$ docker build -t tuyuchengimage .
#4 [1/2] FROM docker.io/library/ubuntu:latest
#5 [2/2] RUN echo 'This is a Tuyucheng tutorial&' && echo 'Print more stuff'
#5 0.334 This is a Tuyucheng tutorial&
#5 0.334 Print more stuff
```

可以看到，这两个句子已按预期打印。

## 4. 总结

在本文中，我们介绍了如何在Dockerfile中编写注释。并介绍了与注释类似的解析器指令，它们看起来像是注释，但实则不是。