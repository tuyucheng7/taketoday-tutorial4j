## 1. 概述

在本教程中，我们将重点介绍Maven在本地存储所有本地依赖项的位置，即**Maven本地仓库中的位置**。

简单地说，当我们运行Maven构建时，我们项目的依赖项(jar、插件jar、其他工件)都存储在本地以供以后使用。

还要记住，除了这种类型的本地仓库之外，Maven还支持三种类型的仓库：

-   Local：本地开发机器上的文件夹位置
-   Central：由Maven社区提供的仓库
-   Remote：组织拥有的自定义仓库

现在让我们关注本地仓库。

## 2. 本地仓库

Maven的本地仓库是本地计算机上存储所有项目工件的目录。

当我们执行Maven构建时，Maven会自动将所有依赖jar下载到本地仓库中。通常，此目录名为.m2。

这是基于操作系统的默认本地仓库所在的位置：

```bash
Windows: C:\Users\<User_Name>\.m2
```

```bash
Linux: /home/<User_Name>/.m2
```

```bash
Mac: /Users/<user_name>/.m2
```

对于Linux和Mac，我们可以写成简短的形式：

```bash
~/.m2
```

## 3. settings.xml中的自定义本地仓库

如果此默认位置不存在仓库，则可能是因为某些预先存在的配置。

该配置文件位于Maven安装目录中一个名为conf的文件夹中，名称为settings.xml。

下面是确定我们丢失的本地仓库位置的相关配置：

```xml
<settings>
    <localRepository>C:/maven_repository</localRepository>
    ...
</settings>
```

这基本上就是我们如何更改本地仓库的位置，当然，如果我们更改该位置，我们将不再在默认位置找到仓库。

**存储在较早位置的文件不会自动移动**。

## 4. 通过命令行传递本地仓库位置

除了在Maven的settings.xml中设置自定义本地仓库之外，mvn命令还支持maven.repo.local属性，它允许我们将本地仓库位置作为命令行参数传递：

```bash
mvn -Dmaven.repo.local=/my/local/repository/path clean install
```

这样，我们就不必更改Maven的settings.xml了。

## 5. 总结

在这篇简短的文章中，我们了解了Maven本地仓库的默认设置。

我们还讨论了如何告诉Maven使用自定义本地仓库位置。