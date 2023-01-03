## 1. 概述

在本教程中，我们将解释为什么Maven可能使用与系统中设置的默认版本不同的Java版本。此外，我们将展示Maven的配置文件所在的位置。然后，我们将解释如何在Maven中配置Java版本。

## 2. Maven配置

首先，让我们看一下可能的系统配置，其中[Maven](https://www.baeldung.com/maven)使用与系统中默认设置不同的Java版本，Maven配置返回：

```bash
$ mvn -v
Apache Maven 3.8.4 (9b656c72d54e5bacbed989b64718c159fe39b537)
Maven home: D:\develop-tools\apache-maven-3.8.4
Java version: 11.0.10, vendor: Oracle Corporation, runtime: D:\develop-tools\jdk-11.0.10
Default locale: zh_CN, platform encoding: GBK
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```

如我们所见，它返回Maven版本、Java版本和操作系统信息，Maven工具使用JDK版本11.0.10。

现在让我们看一下我们系统中设置的Java版本：

```bash
$ java -version
java version "17.0.5" 2022-10-18 LTS
Java(TM) SE Runtime Environment (build 17.0.5+9-LTS-191)
Java HotSpot(TM) 64-Bit Server VM (build 17.0.5+9-LTS-191, mixed mode, sharing)
```

默认JDK设置为17.0.5，在接下来的部分中，我们将解释为什么它与Maven使用的不匹配。

## 3. 全局设置JAVA_HOME

让我们看一下默认设置，最重要的是，[JAVA_HOME](https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux)**变量是强制性的Maven配置**。此外，在未设置时，mvn命令会返回一条错误消息：

```vhdl
$ mvn
Error: JAVA_HOME not found in your environment.
Please set the JAVA_HOME variable in your environment to match the
location of your Java installation.
```

在Linux上，我们使用[export命令](https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux#linux)设置系统变量，Windows具有专门的[系统变量设置](https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux#windows)用于此目的。当设置为全局时，Maven使用系统中设置的默认Java版本。

## 4. Maven配置文件

现在让我们快速浏览一下在哪里可以找到Maven配置文件，**有几个地方可以提供配置**：

-   .mavenrc/mavenrc_pre.cmd：位于用户主目录中的用户定义脚本
-   settings.xml：位于~/.m2目录中的文件，其中包含跨项目的配置
-   .mvn：配置位于项目中的目录

此外，我们可以使用MAVEN_OPTS环境变量来设置JVM启动参数。

## 5. 只为Maven设置JAVA_HOME

我们看到Java版本与Maven使用的版本不同，换句话说，**Maven覆盖了JAVA_HOME变量提供的默认配置**。

它可以在mvn命令开头执行的用户定义脚本中进行设置，**在Windows中，我们将其设置在%HOME%\mavenrc_pre.bat或%HOME%\mavenrc_pre.cmd文件中**。Maven支持“.bat”和“.cmd”文件，在文件中，我们只需设置JAVA_HOME变量：

```bash
set JAVA_HOME="C:\my\java\jdk-11.0.10"
```

另一方面，**Linux具有用于相同目的的$HOME/.mavenrc文件**，在这里，我们以几乎相同的方式设置变量：

```bash
JAVA_HOME=C:/my/java/jdk-11.0.10
```

由于这种设置，Maven使用JDK 11，即使系统中的默认版本是JDK 17。

**我们可以使用MAVEN_SKIP_RC标志跳过用户定义脚本的执行**。

此外，我们可以直接在Maven的可执行文件中设置变量。但是，不推荐这种方法，因为如果我们将Maven升级到更高版本，它不会自动应用。

## 6. 总结

在这篇简短的文章中，我们解释了Maven如何使用与默认版本不同的Java版本。然后，我们展示了Maven的配置文件所在的位置。最后，我们解释了如何为Maven设置Java版本。