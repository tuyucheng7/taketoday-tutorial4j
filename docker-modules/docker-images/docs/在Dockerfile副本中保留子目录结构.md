## 1. 概述

在本文中，我们介绍将目录复制到保留子目录结构的Docker镜像中。

## 2. 将本地目录复制到镜像

首先我们创建以下文件树：

```powershell
|   Dockerfile
|   
---folder1
    +---subfolder1
    |       file1.txt
    |       
    ---subfolder2
            file2.txt
```

这可以通过运行以下命令来完成：

```shell
$ mkdir folder1
$ cd folder1
$ mkdir subfolder1
$ cd subfolder1
$ touch file1.txt
$ cd ..
$ mkdir subfolder2
$ cd subfolder2
$ touch file2.txt
$ cd ../..
```

现在打开我们的[Dockerfile]()：

```shell
$ touch Dockerfile
```

然后，我们添加以下内容：

```dockerfile
FROM ubuntu:latest
COPY folder1/ /workdir/
RUN ls --recursive /workdir/
```

-   第一行表明我们使用最新的ubuntu镜像作为我们的基础镜像
-   **第二行将folder1目录的内容复制到镜像的workdir目录中**；如果workdir不存在，它将被创建
-   第三行在镜像的shell中执行[ls]()命令，递归地列出workdir文件夹的所有子目录的内容

现在，我们可以构建我们的Docker镜像：

```shell
$ docker build .
#4 [1/3] FROM docker.io/library/ubuntu:latest
#6 [2/3] COPY folder1/ /workdir/
#7 [3/3] RUN ls --recursive /workdir/
#7 0.324 /workdir/:
#7 0.324 subfolder1
#7 0.324 subfolder2
#7 0.324
#7 0.324 /workdir/subfolder1:
#7 0.324 file1.txt
#7 0.324
#7 0.324 /workdir/subfolder2:
#7 0.324 file2.txt
```

正如预期的那样，递归打印workdir的内容突出显示它包含folder1的所有子目录和文件。

## 3. 将本地目录合并到镜像中

现在我们稍微更新我们的文件树，变成以下结构：

```powershell
|   Dockerfile
|   
+---folder1
|   +---subfolder1
|   |       file1.txt
|   |       
|   ---subfolder2
|           file2.txt
|           
---folder2
        file3.txt
```

为了进行此修改，我们可以运行以下命令：

```shell
$ mkdir folder2
$ cd folder2
$ touch file3.txt
$ cd ..
```

**现在，我们要将folder2的内容合并到镜像的workdir中**，因此，我们更新Dockerfile：

```shell
FROM ubuntu:latest
COPY folder1/ /workdir/
RUN ls --recursive /workdir/
COPY folder2/ /workdir/
RUN ls --recursive /workdir/
```

第二个COPY指令不会删除之前添加的文件，让我们构建我们的镜像来观察这种行为：

```shell
$ docker build .
#4 [1/5] FROM docker.io/library/ubuntu:latest
#6 [2/5] COPY folder1/ /workdir/
#7 [3/5] RUN ls --recursive /workdir/
#8 [4/5] COPY folder2/ /workdir/
#9 [5/5] RUN ls --recursive /workdir/
#9 0.398 /workdir/:
#9 0.398 file3.txt
#9 0.398 subfolder1
#9 0.398 subfolder2
#9 0.398
#9 0.398 /workdir/subfolder1:
#9 0.398 file1.txt
#9 0.398
#9 0.398 /workdir/subfolder2:
#9 0.398 file2.txt
```

如日志所示，folder1和folder2的所有子目录确实已复制到workdir。

作为补充说明，我们选择这个例子是为了演示合并行为。如果我们只想将folder1和folder2的内容同时复制到镜像的workdir目录，那么我们也可以简单地像下面这样，**COPY指令可以接收多个要复制的源目录**：

```dockerfile
FROM ubuntu:latest
COPY folder1/ folder2/ /workdir/
RUN ls --recursive /workdir/
```

## 4. 总结

在本教程中，我们了解了如何将本地目录复制到Docker镜像，同时保持其子目录结构。