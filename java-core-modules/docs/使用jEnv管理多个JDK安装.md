## 一、简介

对于 Java 的每个新发布周期，我们可能需要在我们的环境中管理多个并行版本的软件开发工具包 (SDK)。因此，设置和管理*[JAVA_HOME](https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux)*路径变量有时会变得非常痛苦。

在本教程中，我们将了解[jEnv](https://www.jenv.be/)如何帮助管理多个不同版本的[JDK](https://www.baeldung.com/jvm-vs-jre-vs-jdk)安装。

## 2.什么是jEnv *？*

jEnv 是一个命令行工具，可以帮助我们管理多个 JDK 安装。它基本上在我们的 shell 中设置*JAVA_HOME*，可以全局设置，本地设置到当前工作目录，或每个 shell。

它让我们可以在不同的 Java 版本之间快速切换。这在处理具有不同 Java 版本的多个应用程序时特别有用。

值得注意的是，**jEnv 不会为我们安装 Java JDK。相反，它只是帮助我们方便地管理多个 JDK 安装。**

此外，让我们深入了解 jEnv 安装并查看其最常用的命令。

## 3. 安装jEnv

jEnv 支持 Linux 和 MacOS 操作系统。此外，它还支持 Bash 和 Zsh shell。让我们从使用终端安装它开始：

在 MacOS 上，我们可以简单地使用[Homebrew](https://brew.sh/)安装 jEnv ：

```bash
$ brew install jenv复制
```

在 Linux 上，我们可以从源代码安装 jEnv：

```bash
$ git clone https://github.com/jenv/jenv.git ~/.jenv复制
```

接下来，让我们根据所使用的 shell将已安装的*jenv命令添加到路径中。*

为 Bash shell 添加 PATH 条目：

```bash
$ echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.bash_profile
$ echo 'eval "$(jenv init -)"' >> ~/.bash_profile复制
```

为 Zsh shell 添加 PATH 条目：

```bash
$ echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.zshrc
$ echo 'eval "$(jenv init -)"' >> ~/.zshrc复制
```

最后，为了验证 jEnv 安装，我们使用*jenv doctor*命令。在 MacOS 上，该命令将显示以下内容：

```bash
$ jenv doctor
[OK]	No JAVA_HOME set
[ERROR]	Java binary in path is not in the jenv shims.
[ERROR]	Please check your path, or try using /path/to/java/home is not a valid path to java installation.
	PATH : /opt/homebrew/Cellar/jenv/0.5.4/libexec/libexec:/Users/jenv/.jenv/shims:/Users/user/.jenv/bin:/opt/homebrew/bin:/opt/homebrew/sbin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin
[OK]	Jenv is correctly loaded复制
```

这表明*jenv*已正确安装和加载，但尚未安装 Java。

此外，让我们看一下如何安装和管理多个 JDK 版本。

## 4. 管理 JDK 安装

让我们从设置 JDK 版本开始。我们可以使用可用的包管理器之一安装 JDK，例如 brew、[yum 或 apt](https://www.baeldung.com/linux/yum-and-apt)。或者，我们也可以下载 JDK 并将其放在某个文件夹中。

**jEnv 的好处是我们不需要通过包管理器安装 JDK。我们可以简单地下载一个 JDK 并将其放入某个文件夹中。**

### 4.1. 将 JDK 添加到 jEnv

首先，要将新的 JDK 与 jEnv 一起使用，我们需要告诉 jEnv 在哪里找到它。为此，我们使用*jenv add*命令并指定 JDK 的路径：

```bash
$ jenv add /Library/Java/JavaVirtualMachines/openjdk-8.jdk/Contents/Home/
openjdk8-1.8.0.332 added
1.8.0.332 added
1.8 added复制
```

这会将 JDK 8 添加到 jEnv。每个版本都以三个不同的名称提供。让我们再次运行*jenv doctor*来确认 JDK 设置：

```bash
$ jenv doctor
[OK]	No JAVA_HOME set
[OK]	Java binaries in path are jenv shims
[OK]	Jenv is correctly loaded复制
```

我们可以看到 jEnv 现在可以识别配置的 JDK。

*此外，让我们使用jenv versions*命令列出所有可用的 JDK 和 jEnv ：

```bash
$ jenv versions
* system (set by /Users/user/.jenv/version)
  1.8
  1.8.0.332
  openjdk64-1.8.0.332复制
```

这列出了使用 jEnv 注册的所有 JDK。在我们的例子中，我们使用 jEnv 配置了 JDK 8。

为了演示使用多个 JDK，让我们再安装一个 JDK 版本 – 11 并使用 jEnv 配置它：

```bash
$ jenv add /Library/Java/JavaVirtualMachines/openjdk-11.jdk/Contents/Home/
openjdk64-11.0.15 added
11.0.15 added
11.0 added
11 added复制
```

最后，现在运行*jenv versions*命令将列出两个已配置的 JDK 版本：

```bash
$ jenv versions
* system (set by /Users/avinb/.jenv/version)
  1.8
  1.8.0.332
  11
  11.0
  11.0.15
  openjdk64-11.0.15
  openjdk64-1.8.0.332复制
```

显然，我们现在有两个 JDK 版本都配置了 jEnv。

### 4.2. 使用 jEnv 管理 JDK 版本

jEnv 支持三种类型的 JDK 配置：

-   全局 - 如果我们在计算机上的任何位置的命令行中键入*java*命令，将使用 JDK 。
-   本地 – 仅为特定文件夹配置的 JDK。在文件夹中键入 java 命令将使用本地 JDK 版本而不是全局 JDK 版本。
-   Shell – 将仅用于当前 shell 实例的 JDK。

首先，让我们检查一下全局 JDK 的版本：

```bash
$ jenv global
system复制
```

此命令输出“ *system”*，表示系统安装的 JDK 将用作全局 JDK。让我们将全局 JDK 版本设置为 JDK 11：

```bash
$ jenv global 11复制
```

现在检查全局版本将显示 JDK 11：

```bash
$ jenv global
11复制
```

接下来，让我们看看如何设置本地 JDK 版本。

例如，假设我们在*~/baeldung-project*目录中有一个使用 JDK 8 的示例项目。让我们*cd*进入该目录并检查该项目的本地 JDK 版本：

```bash
$ jenv local
jenv: no local version configured for this directory复制
```

此错误消息表示我们尚未为此目录设置任何本地 JDK 版本。在没有本地 JDK 版本的情况下运行*jenv version命令将显示全局 JDK 版本。*让我们为这个目录设置一个本地 JDK 版本：

```bash
$ jenv local 1.8复制
```

*此命令在~/baeldung-project*目录中设置本地 JDK 。设置本地 JDK 基本上会在当前目录中创建一个名为*.java-version的文件。*该文件包含我们设置的本地 JDK 版本“ *1.8” 。*

在此目录中再次运行 jenv version 命令现在将输出 JDK 8。让我们检查此目录中设置的本地 JDK 版本：

```bash
$ jenv local
1.8复制
```

最后，要为特定的 shell 实例设置 JDK 版本，我们使用*jenv shell*命令：

```bash
$ jenv local 1.8复制
```

这会为当前 shell 实例设置 JDK 版本，并覆盖已设置的任何本地和全局 JDK 版本。

### 4.3. 使用 Maven 和 Gradle 配置 jEnv

众所周知，[Maven](https://www.baeldung.com/maven)、[Gradle](https://www.baeldung.com/gradle)等工具都是使用系统JDK来运行的。它不使用 jEnv 配置的 JDK。为了确保 jEnv 与 Maven 和 Gradle 正常工作，我们必须启用它们各自的插件。

对于 Maven，我们将启用 jEnv *maven*插件：

```bash
$ jenv enable-plugin maven复制
```

同样，对于 Gradle，我们将启用 jEnv *gradle*插件：

```bash
$ jenv enable-plugin gradle复制
```

现在运行 Maven 和 Gradle 命令将使用特定于 jEnv 的 JDK 版本，而不是系统 JDK。

请注意，有时 jEnv 可能不会选择正确的 JDK 版本，我们可能会以错误告终。对于这种情况，我们可能必须启用 jEnv*导出*插件：

```bash
$ jenv enable-plugin exports复制
```

换句话说，此插件将确保正确设置*JAVA_HOME变量。*

此外，[SDKMAN](https://www.baeldung.com/java-sdkman-intro)是一种用于管理 JDK 的替代工具，以及其他工具。

## 5.结论

在本文中，我们首先了解了什么是 jEnv 以及如何安装它。

然后我们研究了 jEnv 如何帮助我们方便地配置和管理不同的 JDK 安装。接下来，我们看到了如何使用 jEnv 快速使用全局、本地和特定于 shell 的 JDK 版本。这在处理具有不同 JDK 版本的多个不同项目时特别有帮助。

最后，我们研究了如何配置 jEnv 以与 Maven 和 Gradle 等构建工具一起工作。