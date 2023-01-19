## 1. 概述

开发人员通常使用Gradle来管理项目的构建生命周期，这是所有新Android项目构建工具的默认选择。

在本教程中，我们介绍Gradle Wrapper，它是一个附带的工具程序，可以更轻松地分发项目。

## 2. Gradle Wrapper

要构建一个基于Gradle的项目，我们需要在我们的机器上安装Gradle。但是，如果我们安装的版本与项目的版本不匹配，我们可能会面临很多不兼容的问题。

Gradle Wrapper，也简称为Wrapper，解决了这个问题。))它是一个运行带有声明版本的Gradle任务的脚本))。如果未安装声明的版本，Wrapper会安装所需的版本。

Wrapper的主要好处是我们可以：

-   ))在任何机器上使用Wrapper构建项目，而无需先安装Gradle))
-   有一个固定的Gradle版本，这可以让CI管道上的构建可重用且更健壮
-   通过更改Wrapper定义，可以轻松升级到新的Gradle版本

### 2.1 生成Wrapper文件

))要使用Wrapper，我们需要生成一些特定的文件，这里使用名为wrapper的内置Gradle任务生成这些文件))；请注意，我们只需要生成这些文件一次。

现在，我们在项目根目录中运行wrapper任务：

```shell
$ gradle wrapper
```

然后会生成以下文件：

<img src="../gradle-wrapper/assets/img.png">

下面是关于这些文件的介绍：

-   gradle-wrapper.jar包含用于下载gradle-wrapper.properties文件中指定的Gradle发行版的代码
-   gradle-wrapper.properties包含Wrapper运行时属性，其中最重要的是，与当前项目兼容的Gradle发行版版本
-   gradlew是使用Wrapper执行Gradle任务的脚本
-   gradlew.bat是适用于Windows系统的gradlew等效批处理脚本

默认情况下，wrapper任务使用机器上当前安装的Gradle版本生成Wrapper文件。如果需要，我们可以指定其他版本：

```java
$ gradle wrapper --gradle-version 7.3
```

))我们建议将Wrapper文件push到GitHub等版本控制系统中))。通过这种方式，我们可以确保其他开发人员无需安装Gradle即可运行该项目。

### 2.2 使用Wrapper运行Gradle命令

))我们可以通过将gradle替换为gradlew来使用Wrapper运行任何Gradle任务))。

例如，要列出项目中可用的Gradle任务，我们可以使用gradlew tasks命令：

```shell
$ ./gradlew tasks
```

下面是运行后的输出：

```shell
Help tasks
----------
buildEnvironment - Displays all buildscript dependencies declared in root project 'gradle-wrapper'.
dependencies - Displays all dependencies declared in root project 'gradle-wrapper'.
dependencyInsight - Displays the insight into a specific dependency in root project 'gradle-wrapper'.
help - Displays a help message.
javaToolchains - Displays the detected java toolchains.
outgoingVariants - Displays the outgoing variants of root project 'gradle-wrapper'.
projects - Displays the sub-projects of root project 'gradle-wrapper'.
properties - Displays the properties of root project 'gradle-wrapper'.
tasks - Displays the tasks runnable from root project 'gradle-wrapper'.
```

如我们所见，输出与使用gradle命令运行此任务时得到的输出相同。

## 3. 常见问题

现在，让我们看看我们在使用Wrapper时可能会遇到的一些常见问题。

### 3.1 忽略所有Jar文件的全局.gitignore

一些组织不允许开发人员将jar文件push他们的版本控制系统。通常，这样的项目会在全局.gitignore文件中配置忽略所有jar文件，这样的话可能导致不会将gradle-wrapper.jar文件push到git仓库。因此，该项目的其他拥有者无法在他们的机器上运行wrapper任务。在这种情况下，))我们需要将gradle-wrapper.jar文件强制添加到git中))：

```shell
git add -f gradle/wrapper/gradle-wrapper.jar
```

类似地，我们可能包含一个忽略jar文件的项目特定的.gitignore文件。我们可以通过放宽.gitignore规则或强制添加包装器jar文件来解决这个问题，如上所示。

### 3.2 缺少wrapper文件夹

在管理基于Wrapper的项目时，我们可能会忘记包含存在于gradle文件夹中的wrapper文件夹。但正如我们在上面看到的，wrapper文件夹包含两个关键文件gradle-wrapper.jar和gradle-wrapper.properties，如果没有这些文件，我们在使用Wrapper运行Gradle任务时会出错。因此，我们必须将wrapper文件夹同步到版本控制系统。

### 3.3 删除wrapper文件

基于Gradle的项目包含一个.gradle文件夹，用于存储缓存以加速Gradle任务的执行。有时，我们需要清除缓存以解决Gradle构建问题。通常，我们会删除整个.gradle文件夹。但是有时我们可能会将Wrapper的gradle文件夹与.gradle文件夹混淆，并将其删除，之后，我们在尝试使用Wrapper运行Gradle任务时肯定会遇到问题。))我们可以通过从远程pull最新的更改来解决这个问题；或者，我们可以重新生成Wrapper文件))。

## 4. 总结

在本教程中，我们介绍了Gradle Wrapper及其基本用法，并点出了使用Gradle Wrapper时可能遇到的一些常见问题。