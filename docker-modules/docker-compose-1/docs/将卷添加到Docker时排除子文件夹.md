## 1. 概述

当我们需要将容器资源链接到主机时，我们会挂载[Docker卷。]()我们可以使用不同的卷，比如命名卷或绑定挂载。此外，无论它们是否持久化，我们都可以使用本地或远程资源。但是，在挂载时，我们可能需要排除一些不需要的文件或文件夹。

在本教程中，**我们将通过一些[Docker Compose]()示例学习如何在挂载卷时排除文件夹**。

## 2. 创建一个Nodejs Docker镜像

那么为什么我们需要用[Docker]()排除一些文件或文件夹呢？首先，我们讨论一下Docker镜像。

**我们在构建镜像的时候，通常会添加应用程序文件**。为了演示，我们将使用[Nodejs](https://nodejs.org/en/)创建一个[Docker示例应用程序](https://www.docker.com/blog/getting-started-with-docker-using-node-jspart-i/)。

一旦我们设置了主应用程序，接下来就是编写[Dockerfile](https://docs.docker.com/engine/reference/builder/)：

```dockerfile
FROM node:12.18.1
ENV NODE_ENV=production

WORKDIR /app

COPY ["package.json", "package-lock.json", "./"]

RUN npm install --production

COPY . .

CMD [ "node", "server.js" ]
```

现在我们可以构建我们将调用的镜像，例如node-docker：

```shell
$ docker build -t node-docker .
```

在这种情况下，构建上下文是我们本地的/app文件夹。但是，它可以是位于指定路径或URL中的一组文件，例如Git仓库。

当我们构建Docker镜像时，我们会将文件发送到存储镜像的Docker服务器。Docker根据copy或run等命令创建分层镜像。

让我们运行docker [history](https://docs.docker.com/engine/reference/commandline/history/)命令来查看镜像的不同层：

```shell
$ docker history --format "ID-> {{.ID}} | Created-> {{.CreatedSince}} | Created By-> {{.CreatedBy}} | Size: {{.Size}}" e870a50eed97
```

我们可以使用–format选项来创建自定义输出，并显示相关命令或大小等信息：

```shell
ID-> e870a50eed97 | Created-> 36 hours ago | Created By-> /bin/sh -c #(nop)  CMD ["node" "server.js"] | Size: 0B
ID-> 708a43cd0ef2 | Created-> 36 hours ago | Created By-> /bin/sh -c #(nop) COPY dir:7cc2842dd32649457… | Size: 11.3MB
ID-> d49b84f48e41 | Created-> 36 hours ago | Created By-> /bin/sh -c npm install --production | Size: 14.7MB
ID-> a351be0717a1 | Created-> 36 hours ago | Created By-> /bin/sh -c #(nop) COPY multi:9959dc16241ba60… | Size: 80.7kB
ID-> 56b22d35f315 | Created-> 36 hours ago | Created By-> /bin/sh -c #(nop) WORKDIR /app | Size: 0B
ID-> c28b64493ce8 | Created-> 36 hours ago | Created By-> /bin/sh -c #(nop)  ENV NODE_ENV=production | Size: 0B
ID-> f5be1883c8e0 | Created-> 2 years ago | Created By-> /bin/sh -c #(nop)  CMD ["node"] | Size: 0B
```

## 3. 从镜像构建中排除文件和文件夹

让我们考虑一种情况，我们有一个大文件，例如日志、zip或jar文件。或者我们可能有不想在最终构建中公开的文件，例如密钥或密码。

**我们可以使用[.dockerignore](https://docs.docker.com/engine/reference/builder/#dockerignore-file)文件来避免将这些文件发送到Docker服务器**，它的工作方式类似于[.gitignore](https://git-scm.com/docs/gitignore)文件。

假设我们的项目中有一个带有密码的文件，例如：

```shell
$ echo 'password' >secret.txt
```

现在，我们想从我们的镜像中排除这个文件，可以将它添加到.dockerignore文件：

```shell
# Ignoring the password file 
secret.txt
```

这样，我们就可以从构建上下文中排除资源；此外，遵循[最佳实践](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)，它可以提高性能。要上传的镜像大小稍微小点。

**此外，如果我们添加一个要复制的文件，我们将不会遇到新层的缓存失效问题**。我们也可以用下面这种方法排除文件夹：

```shell
# Ignore the logs directory
logs/
```

让我们用.dockerignore忽略文件，然后，我们可以启动一个容器：

```shell
$ docker run -d --publish 8000:8000 node-docker
```

或者，如果我们使用Docker Compose，我们可以使用[docker-compose up](https://docs.docker.com/engine/reference/commandline/compose_up/)运行docker-compose.yml文件，例如：

```yaml
services:
    node-app:
        image: node-docker:latest
        ports:
            - 8080:8080
```

我们可以通过在我们的容器中执行bash命令进行双重检查：

```shell
$ docker exec -it d8938bc93406 bash
```

进入容器后，如果我们使用ls命令检查内容，则正在运行的容器中一定没有secret.txt文件或其他排除的资源。

## 4. 使用Docker卷排除文件和文件夹

我们已经了解了如何在构建镜像时排除文件和文件夹，我们可能想对使用[卷]()的运行容器做同样的事情。

**其中一个原因可能是添加文件或文件夹而不影响我们在主机中的内容**。

假设我们现在想要将一个项目文件夹添加到Docker容器中，我们可以使用[mount bind](https://docs.docker.com/storage/bind-mounts/)来实现它。

让我们用我们的Nodejs应用程序为此创建一个Docker Compose示例，下面是docker-compose.yml文件：

```yaml
services:
    node-app:
        build: .
        ports:
            - 8081:8080
        volumes:
            - .:/app
```

现在可以在项目的根目录下运行我们的容器：

```shell
$ docker-compose up -d
```

容器启动，如预期的那样，如果容器中有文件或目录要挂载，Docker会将内容复制到卷中。

假设现在，出于某种原因，我们删除了容器中的文件或文件夹。同样的结果发生在我们的主机上，我们将失去那些资源。让我们看看一些避免删除主机中资源的解决方案。

### 4.1 排除文件

我们可以从排除容器中文件的挂载开始，一旦我们设置了卷，就可以使用/dev/null命令来解决这个问题：

再一次，下面是docker-compose.yml文件：

```yaml
services:
    node-app:
        build: .
        ports:
            - 8080:8080
        volumes:
            - .:/app/
            - /dev/null:/app/secret.txt
```

当使用/dev/null时，我们会丢弃写入文件的所有内容。在这种情况下，我们将以secret.txt为空而告终。此外，由于文件绑定到/dev/null，因此无法修改该文件。

### 4.2 排除文件夹

更有趣的是，我们可以排除文件夹和子文件夹。我们可以通过在该特定目录或子目录上创建匿名或[命名卷](https://docs.docker.com/storage/volumes/)来实现这一点。

下面是我们的YAML文件：

```yaml
services:
    node-app:
        build: .
        ports:
            - 8080:8080
        volumes:
            - .:/app
            - /app/node_modules/
```

**这里的顺序是相关的。首先，我们绑定了之前创建的/app目录，然后，我们为要排除的内容挂载一个卷，在本例中为/node_modules子目录**。

如果要持久化我们的数据，我们可以使用命名卷：

```yaml
volumes:
    - .:/app
    - my-vol:/app/node_modules/

volumes:
    my-vol:
        driver: local
```

最后，我们可以尝试修改容器中/node_modules目录的内容，在这种情况下，不会对我们的主机造成任何影响。

## 5. 总结

在本文中，我们学习了如何使用Docker排除文件或文件夹等资源，并演示了从镜像构建到运行容器的示例。对于容器，我们介绍了使用Docker Compose的示例，以及使用卷排除子文件夹的可能性。