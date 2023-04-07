## 一、概述

随着 Java 的新发布周期，开发人员可能需要在其环境中管理并行版本和不同版本的软件开发工具包 (SDK)。因此，设置 PATH 变量有时会变得非常痛苦。

在本教程中，我们将了解如何使用 SDKMAN！可以帮助轻松管理 SDK 的安装和选择。

## 2.什么是SDKMAN！？

**软件开发人员！是一个管理多个SDK并行版本的工具，SDKMAN！称呼“候选人”**。

它提供了一个方便的命令行界面 (CLI) 和 API，用于列出、安装、切换和删除候选人。**此外，它负责为我们设置环境变量。**

它还允许开发人员安装基于 JVM 的 SDK，如 Java、Groovy、Scala、Kotlin 和 Ceylon。还支持 Maven、Gradle、SBT、Spring Boot、Vert.x 等。**软件开发人员！是一个用 Bash 编写的免费、轻量级、开源实用程序。**

## 3.安装SDKMAN！

软件开发人员！所有主要操作系统都支持它，并且可以轻松地安装在所有基于 Unix 的系统上。此外，它还支持 Bash 和 Zsh shell。

因此，让我们开始使用终端安装它：

```bash
$ curl -s "https://get.sdkman.io" | bash复制
```

然后，按照屏幕上的说明完成安装。

我们可能需要安装 zip 和 unzip 包来完成安装过程。

接下来，打开一个新终端或运行：

```bash
$ source "$HOME/.sdkman/bin/sdkman-init.sh"复制
```

最后，运行以下命令以确保安装成功。如果一切顺利，应该显示版本：

```bash
$ sdk version
SDKMAN 5.8.5+522
复制
```

更多定制请参考SDKMAN上的[安装指南！](https://sdkman.io/install)网站。

要查看所有可用命令，请使用*帮助*命令：

```bash
$ sdk help复制
```

## 4.列出所有候选SDK

因此，让我们首先列出所有可用的 SDK 候选者。

```bash
$ sdk list
复制
```

list命令**显示所有可用的候选者，由唯一名称、描述、官方网站和安装命令标识***：*

```bash
=====================================================
Available Candidates
=====================================================
q-quit                                  /-search down
j-down                                  ?-search up
k-up                                    h-help
-----------------------------------------------------
Java (11.0.7.hs-adpt)                https://zulu.org
...
                                   $ sdk install java
-----------------------------------------------------
Maven (3.6.3)                https://maven.apache.org
...
                                  $ sdk install maven
-----------------------------------------------------
Spring Boot (2.3.1.RELEASE)          http://spring.io
...
                             $ sdk install springboot
------------------------------------------------------
...
复制
```

因此，我们可以使用此标识符来安装候选的默认版本，如 Spring Boot (2.3.1.RELEASE) 或 Maven (3.6.3)。此列表中的指定版本代表每个 SDK 的稳定或 LTS 版本。

## 5. 安装和管理 Java 版本

### 5.1. 上市版本

要列出可用的 Java 版本，请使用*list*命令。结果是一个按供应商分组并按版本排序的条目表：

```bash
$ sdk list java
===================================================================
Available Java Versions
===================================================================
Vendor       | Use | Version | Dist    | Status | Identifier
-------------------------------------------------------------------
AdoptOpenJDK |     | 14.0.1  | adpt    |        | 14.0.1.j9-adpt
...
Amazon       |     | 11.0.8  | amzn    |        | 11.0.8-amzn
...
Azul Zulu    |     | 14.0.2  | zulu    |        | 14.0.2-zulu
...
BellSoft     |     | 14.0.2  | librca  |        | 14.0.2.fx-librca
...
GraalVM      |     | 20.1.0  | grl     |        | 20.1.0.r11-grl
...
Java.net     |     | 16.ea   | open    |        | 16.ea.6-open
...
SAP          |     | 14.0.2  | sapmchn |        | 14.0.2-sapmchn
...
复制
```

每次我们要检查、切换或管理候选人的存储时，我们都需要这个命令。

### 5.2. 安装 Java 版本

假设我们要安装来自 Azul Zulu 的最新版本的 Java 14。因此，我们复制其标识符，即表中的版本，并将其作为参数添加到*安装*命令中：

```bash
$ sdk install java 14.0.2-zulu
Downloading: java 14.0.2-zulu
In progress...
########### 100.0%
Repackaging Java 14.0.2-zulu...
Done repackaging...
Installing: java 14.0.2-zulu
Done installing!
Setting java 14.0.2-zulu as default.
复制
```

软件开发人员！将下载此版本并将其解压缩到我们计算机上的目录中。

**此外，它会更新环境变量，以便我们可以立即在终端中使用 Java。**

*我们可以使用list*命令来验证任何版本的状态和使用情况。因此，现在安装并使用了*14.0.1*版：

```bash
$ sdk list java
=================================================================
Available Java Versions
=================================================================
 Vendor    | Use | Version | Dist    | Status    | Identifier
-----------------------------------------------------------------
 ...
 Azul Zulu | >>> | 14.0.1  | adpt    | installed | 14.0.1.j9-adpt
 ...
复制
```

此外，可以使用相同的命令从计算机安装 Java 或任何自定义版本，但要将二进制文件的路径指定为附加参数：

```bash
$ sdk install java custom-8 ~/Downloads/my-company-jdk-custom-8
复制
```

### 5.3. 在版本之间切换

我们可以通过两种形式来控制版本之间的切换，暂时：

```bash
$ sdk use java 14.0.1.j9-adpt复制
```

或永久：

```bash
$ sdk default java 14.0.1.j9-adpt
复制
```

### 5.4. 删除版本

要删除已安装的版本，请使用目标版本运行*卸载命令：*

```bash
$ sdk uninstall java 14.0.1.j9-adpt
复制
```

### 5.5. 显示正在使用的版本

要检查当前的 Java 版本，我们运行*当前*命令：

```bash
$ sdk current java
Using java version 14.0.2-zulu
复制
```

同样，最后一个命令与以下命令具有相同的效果：

```bash
$ java -version
复制
```

要在我们的机器上通过 SDK 显示版本，我们可以运行不带参数的*当前*命令：

```bash
$ sdk current
Using:
java: 14.0.2-zulu
gradle: 6.2.2
复制
```

## 6.使用SDKMAN！用IDE

安装好的SDK存放在SDKMAN中！默认为*~/.sdkman/candidates 的*目录。

*例如，不同版本的 Java 也将在~/.sdkman/candidates/java/*目录下可用，子目录以版本命名：

```bash
$ ls -al ~/.sdkman/candidates/java/
total 0
drwxrwxrwx 1 user user 12 Jul 25 20:00 .
drwxrwxrwx 1 user user 12 Jul 25 20:00 ..
drwxrwxr-x 1 user user 12 Jul 25 20:00 14.0.2-zulu
lrwxrwxrwx 1 user user 14 Jul 25 20:00 current -> 14.0.2-zulu
复制
```

因此，当前选择的 Java 版本也将在该目录中作为当前版本可用。

同样，Gradle 或任何其他 SDK 将安装在*candidates* 目录下。

通过这种方式，我们可以使用任何特定版本的 Java，例如在我们最喜欢的 IDE 中。我们所要做的就是复制特定版本的路径并将其设置在我们IDE的配置中。

### 6.1. 我明白这个想法

在 IntelliJ IDEA 中，打开“项目结构”，然后打开“项目设置”。在项目配置中，我们可以通过从“项目 SDK”部分选择“新建...”来添加新的 Java 版本：

[![IntelliJ 中的项目结构对话框](https://www.baeldung.com/wp-content/uploads/2020/07/projectij-768x449-1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/projectij-768x449-1.jpg)

我们还可以定义要在“构建工具”部分中使用的 Java、Gradle 或 Maven 的版本：

[![IntelliJ 中的 Maven 配置](https://www.baeldung.com/wp-content/uploads/2020/07/maven-768x582-1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/maven-768x582-1.jpg)

### [![IntelliJ 中的 Gradle 配置](https://www.baeldung.com/wp-content/uploads/2020/07/gradle-768x544-1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/gradle-768x544-1.jpg)

提示：Java 版本必须与 Gradle 或 Maven 的“Project SDK”中使用的版本相同。

### 6.2. 蚀

在 Eclipse 中打开“Project Properties”，选择“Java Build Path”，然后切换到“Libraries”选项卡。在这里，我们可以通过“Add Library…”管理新的Java SDK，并按照说明操作：

[![Eclipse 中的库管理](https://www.baeldung.com/wp-content/uploads/2020/07/eclipse-project-768x417-1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/eclipse-project-768x417-1.jpg)

我们还可以控制所有项目安装的 SDK。打开“Window”菜单下的“Preferences”，然后转到“Installed JREs”。这里我们可以通过“Add…”管理Java的SDK，按照说明操作：

[![在 Eclipse 中安装的 JRE](https://www.baeldung.com/wp-content/uploads/2020/07/eclipse.jpg)](https://www.baeldung.com/wp-content/uploads/2020/07/eclipse.jpg)

## 七、结论

在本教程中，我们展示了 SDKMAN! 可以帮助我们在 Maven 等其他 Java 环境工具中管理不同版本的 Java SDK。