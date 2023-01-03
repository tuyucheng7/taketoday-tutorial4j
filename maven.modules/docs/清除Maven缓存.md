## 1. 概述

在这个简短的教程中，我们将探索清除本地[Maven](https://www.baeldung.com/maven)缓存的方法，我们可能希望这样做的目的是为了节省磁盘空间或清理我们不再引用的工件。

我们将首先手动清除缓存，我们在其中物理删除目录。然后，我们将使用Maven dependency插件清除我们的缓存，使用我们可用的一些不同的插件选项。

## 2. 删除本地缓存目录

我们的[本地Maven仓库](https://www.baeldung.com/maven-local-repository#Repository)根据操作系统存储在不同的位置。此外，由于.m2目录很可能是隐藏的，我们需要更改目录属性才能显示它。

在Windows中，默认位置是：

```bash
C:\Users\<user_name>\.m2
```

在Mac上：

```bash
/Users/<user_name>/.m2
```

在基于Linux的系统上：

```bash
/home/<user_name>/.m2
```

找到目录后，我们可以简单地删除文件夹.m2/repository。对于MacOS或Linux等基于Unix的系统，我们可以使用一条命令删除目录：

```bash
rm -rf ~/.m2/repository
```

如果我们的缓存目录不在默认位置，我们可以使用[Maven Help插件](https://maven.apache.org/plugins/maven-help-plugin/evaluate-mojo.html)来找到它：

```bash
mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout
```

## 3. 使用Maven dependency插件

我们可以使用具有purge-local-repository目标的[Maven dependency插件](https://mvnrepository.com/artifact/org.apache.maven.plugins/maven-dependency-plugin)，而不是直接删除缓存目录。

首先，我们需要导航到Maven项目的根目录，然后，我们可以运行：

```bash
mvn dependency:purge-local-repository
```

当我们在没有任何附加标志的情况下运行此插件时，它可能会下载缓存中不存在的工件来解析依赖关系树，这称为传递依赖项解析。接下来，**它会删除我们的本地缓存，最后重新下载工件**。

或者，为了删除我们的缓存并避免第一步预下载缺失的依赖项，我们可以传入标志actTransitively=false：

```bash
mvn dependency:purge-local-repository -DactTransitively=false
```

最后，**如果我们只想清除缓存，而不预先下载或重新解析工件**：

```bash
mvn dependency:purge-local-repository -DactTransitively=false -DreResolve=false
```

在这里，我们传入了一个额外的reResolve=false标志，它告诉插件避免重新下载依赖项。

## 4. 总结

在这篇简短的文章中，我们研究了两种清除本地Maven缓存的方法。

首先，我们介绍了手动清空本地缓存目录。然后，我们使用Maven dependency插件，探索不同的选项来实现我们想要的结果。