## 1. 简介

Git是领先的软件开发版本控制系统，另一方面，Dockerfile包含自动构建应用程序镜像的所有命令。这两个产品是任何采用DevOps项目的完美组合。

在本教程中，我们介绍一些结合这两种技术的解决方案，并详细介绍每种解决方案及其优缺点。

## 2. Git仓库中的Dockerfile

要始终访问Dockerfile中的Git仓库，最简单的解决方案是将Dockerfile直接保存在Git仓库中：

```powershell
ProjectFolder/
  .git/
  src/
  pom.xml
  Dockerfile
  ...
```

通过以上设置，我们可以访问整个项目源目录。接下来，我们可以使用[ADD命令]()将其包含在我们的容器中，例如：

```shell
ADD . /project/
```

我们也可以限制到构建目录的范围：

```shell
ADD /build/ /project/
```

或类似.jar文件的构建输出：

```shell
ADD /output/project.jar /project/
```

这个解决方案的最大优点是我们可以测试任何代码更改，而无需将它们提交到仓库，所有内容都将位于同一本地目录中。

这里需要记住的一件事是创建一个[.dockerignore](https://docs.docker.com/engine/reference/builder/#dockerignore-file)文件，它类似于.gitignore文件，但在本例中，它会从 Docker上下文中排除与其中的模式匹配的文件和目录。这有助于我们避免不必要地将大型或敏感文件和目录发送到 Docker构建过程，并避免将它们添加到镜像中。

## 3. 克隆Git仓库

另一个简单的解决方案是在镜像构建过程中只获取我们的git仓库，我们可以通过简单地将[SSH密钥]()添加到本地存储并调用git clone命令来实现它：

```shell
ADD ssh-private-key /root/.ssh/id_rsa
RUN git clone git@github.com:tuyucheng/fullstack-roadmaps.git
```

上面的命令将获取整个仓库，并将其放置在我们容器中的./fullstack-roadmaps目录中。

不幸的是，这种解决方案也有一些缺点。首先，我们会将我们的SSH私钥存储在Docker镜像中，这可能会带来潜在的安全问题。我们可以通过使用git仓库的用户名和密码来应用解决方法：

```plaintext
ARG username=$GIT_USERNAME
ARG password=$GIT_PASSWORD
RUN git clone https://username:password@github.com:tuyucheng/fullstack-roadmaps.git
```

并将[它们作为环境变量](https://www.baeldung.com/ops/docker-container-environment-variables)从我们的机器传递，这样我们就可以将git凭据保存在我们的镜像之外。

其次，此步骤将在以后的构建中缓存，即使我们的仓库发生了变化。这是因为除非你在前面的步骤中破坏缓存，否则带有RUN命令的行是不变的。尽管如此，我们可以通过在docker build命令中添加–no-cache参数来解决这个问题。

另一个小缺点是我们必须在我们的容器中安装git包。

## 4. 容器卷映射

第三种解决方案是[容器卷映射]()，它使我们能够将目录从我们的机器挂载到Docker容器中，这是存储Docker容器使用的数据的首选机制。

我们可以通过在Dockerfile中添加以下行来实现：

```dockerfile
VOLUME /build/ /project/
```

这将在容器上创建/project目录，并将其挂载到我们机器上的/build目录。

当我们的Git仓库仅用于构建过程时，容器卷映射将是最佳选择。通过将仓库保留在容器之外，我们不会增加其大小并允许仓库内容超出给定容器的生命周期。

需要记住的一点是，容器卷映射为Docker容器提供了对挂载目录的写访问权限。不正确地使用此功能可能会导致git仓库目录发生一些不必要的更改。

## 5. Git子模块

如果我们将Dockerfile和源代码保存在单独的仓库中，或者我们的Docker构建需要多个源仓库，我们可以考虑使用[Git子模块](https://git-scm.com/book/en/v2/Git-Tools-Submodules)。

首先，我们必须创建一个新的Git仓库，并将我们的Dockerfile放在其中。接下来，我们可以通过将子模块添加到.gitmodules文件中来定义它们：

```shell
[submodule "project"]
  path = project
  url = https://github.com/tuyucheng/fullstack-roadmaps.git
  branch = master
```

现在，我们可以像使用标准目录一样使用子模块。例如，我们可以将其内容到我们的容器中：

```shell
ADD /build/ /project/
```

记住，子模块不会自动更新，我们需要运行以下git命令来获取最新的更改：

```shell
git submodule update --init --recursive
```

## 6. 总结

在本教程中，我们学习了几种在Dockerfile中使用Git仓库的方法。