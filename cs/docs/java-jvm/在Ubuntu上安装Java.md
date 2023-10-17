## **一、概述**

在本教程中，我们将介绍**在 Ubuntu 上安装 JDK 的不同方法**。然后，我们将简要比较这些方法。最后，我们将展示如何在 Ubuntu 系统上管理多个 Java 安装。

作为每种方法的先决条件，我们需要

-   Ubuntu系统
-   *以具有sudo*权限的非 root 用户身份登录

下面描述的说明已经在 Ubuntu 18.10、18.04 LTS、16.04 LTS 和 14.04 LTS 上进行了测试。对于 Ubuntu 14.04 LTS，文本中提到了一些差异。

请注意，您可以从 OpenJDK 和 Oracle 下载的包以及存储库中可用的包都会定期更新。确切的软件包名称可能会在几个月内发生变化，但基本的安装方法将保持不变。

## **2. 安装 JDK 11**

如果我们想使用最新最好的 JDK 版本，通常手动安装是可行的方法。这意味着从 OpenJDK 或 Oracle 站点下载一个包并进行设置，使其符合*apt*如何设置 JDK 包的约定。

### **2.1. 手动安装 OpenJDK 11**

首先，让我们下载最近发布的 OpenJDK 11 的*tar存档：*

```bash
$ wget https://download.java.net/java/ga/jdk11/openjdk-11_linux-x64_bin.tar.gz复制
```

我们将下载包的*sha256和与*[OpenJDK 站点上提供的包](https://download.java.net/java/ga/jdk11/openjdk-11_linux-x64_bin.tar.gz.sha256)进行比较：

```bash
$ sha256sum openjdk-11_linux-x64_bin.tar.gz复制
```

让我们提取*tar*存档：

```bash
$ tar xzvf openjdk-11_linux-x64_bin.tar.gz复制
```

接下来，让我们将刚刚提取的*jdk-11目录移动到**/usr/lib/jvm*的子目录中。下一节中描述的 apt 包也将它们的 JDK 放入此目录*：*

```bash
$ sudo mkdir /usr/lib/jvm
$ sudo mv jdk-11 /usr/lib/jvm/openjdk-11-manual-installation/
复制
```

现在，我们想让**java\*和\*javac\*命令\*可用**。一种可能性是为它们创建符号链接，例如，在*/usr/bin*目录中。但相反，我们将为它们安装一个替代方案。这样，如果我们希望安装其他版本的 JDK，它们可以很好地协同工作：

```bash
$ sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/openjdk-11-manual-installation/bin/java 1
$ sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/openjdk-11-manual-installation/bin/javac 1复制
```

让我们验证安装：

```bash
$ java -version复制
```

从输出中可以看出，我们确实安装了最新版本的 OpenJDK JRE 和 JVM：

```bash
openjdk version "11" 2018-09-25
OpenJDK Runtime Environment 18.9 (build 11+28)
OpenJDK 64-Bit Server VM 18.9 (build 11+28, mixed mode)
复制
```

让我们也看看编译器版本：

```bash
$ javac -version复制
javac 11复制
```

### **2.2. 手动安装 Oracle JDK 11**

如果我们想确保使用最新版本的 Oracle JDK，我们可以遵循与 OpenJDK 类似的手动安装流程。为了从[Oracle 网站下载 JDK 11 的](https://www.oracle.com/technetwork/java/javase/downloads/index.html)*tar*存档，**我们必须首先接受许可协议**。出于这个原因，通过*wget*下载比 OpenJDK 稍微复杂一些：

```bash
$ wget -c --header "Cookie: oraclelicense=accept-securebackup-cookie" \
http://download.oracle.com/otn-pub/java/jdk/11.0.1+13/90cf5d8f270a4347a95050320eef3fb7/jdk-11.0.1_linux-x64_bin.tar.gz复制
```

上面的示例下载了 11.0.1 的包。每个次要版本的确切下载链接都会发生变化。

以下步骤与 OpenJDK 相同：

```bash
$ sha256sum jdk-11.0.1_linux-x64_bin.tar.gz
$ tar xzvf jdk-11.0.1_linux-x64_bin.tar.gz
$ sudo mkdir /usr/lib/jvm
$ sudo mv jdk-11.0.1 /usr/lib/jvm/oracle-jdk-11-manual-installation/
$ sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/oracle-jdk-11-manual-installation/bin/java 1
$ sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/oracle-jdk-11-manual-installation/bin/javac 1复制
```

验证也是一样的。但输出显示，这次我们安装的不是 OpenJDK，而是 Java(TM)：

```bash
$ java -version复制
java version "11.0.1" 2018-10-16 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.1+13-LTS)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.1+13-LTS, mixed mode)复制
```

对于编译器：

```bash
$ javac -version复制
javac 11.0.1复制
```

### **2.3. 从 PPA 安装 Oracle JDK 11**

目前，Oracle JDK 11 也可以在 PPA（个人包存档）中使用。此安装涉及 2 个步骤：将存储库添加到我们的系统并通过 apt 从存储库安装软件包*：*

```bash
$ sudo add-apt-repository ppa:linuxuprising/java
$ sudo apt update
$ sudo apt install oracle-java11-installer复制
```

验证步骤应显示与第 2.2.1 节中手动安装后相同的结果：

```bash
$ java -version复制
java version "11.0.1" 2018-10-16 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.1+13-LTS)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.1+13-LTS, mixed mode)复制
```

对于编译器：

```bash
$ javac -version复制
javac 11.0.1复制
```

在 Ubuntu 14.04 LTS 上， *add-apt-repository* 命令默认不可用。为了添加存储库，首先我们需要安装*software-properties-common*包。

```bash
$ sudo apt update
$ sudo apt install software-properties-common复制
```

之后，我们可以继续 *添加 apt-repository、apt update* 和*apt install*，如上所示。

## **3. 安装 JDK 8**

### **3.1. 在 Ubuntu 16.04 LTS 和更新版本上安装 OpenJDK 8**

JDK 8 是一个 LTS 版本，已经存在了一段时间。出于这个原因，我们可以在大多数受支持的 Ubuntu 版本的“主”存储库中找到最新版本的 OpenJDK 8。当然，我们也可以前往 OpenJDK 网站，在那里获取一个包，然后按照我们在上一节中看到的方式安装它。

但是使用*apt*工具和“主”存储库提供了一些好处。默认情况下，“主”存储库在所有 Ubuntu 系统上可用。它由 Canonical 提供支持——这家公司维护着 Ubuntu 本身。

让我们使用*apt*从“主”存储库安装 OpenJDK 8 ：

```bash
$ sudo apt update
$ sudo apt install openjdk-8-jdk复制
```

现在，让我们验证安装：

```bash
$ java -version复制
```

结果应列出一个运行时环境和一个 JVM：

```bash
openjdk version "1.8.0_181"
OpenJDK Runtime Environment (build 1.8.0_181-8u181-b13-0ubuntu0.18.04.1-b13)
OpenJDK 64-Bit Server VM (build 25.181-b13, mixed mode)复制
```

让我们检查 *javac*可执行文件是否也可用：

```bash
$ javac -version复制
```

现在我们应该看到与上图相同的版本号：

```bash
javac 1.8.0_181复制
```

### **3.2. 在 Ubuntu 14.04 LTS 上安装 OpenJDK 8**

在 Ubuntu 14.04 LTS 上，OpenJDK 包在“主”存储库中不可用，因此我们将从*openjdk-r* PPA 安装它们。正如我们在上面 2.3 节中看到的，默认情况下*add-apt-repository*命令不可用。我们需要它的*software-properties-common包：*

```bash
$ sudo apt update
$ sudo apt install software-properties-common
$ sudo add-apt-repository ppa:openjdk-r/ppa
$ sudo apt update
$ sudo apt install openjdk-8-jdk复制
```

### **3.3. 从 PPA 安装 Oracle JDK 8**

“主”存储库不包含任何专有软件。 **如果我们想使用\*apt\*安装 Oracle Java ，我们将不得不使用 PPA 中的包**。我们已经了解了如何从*linuxuprising* PPA 安装 Oracle JDK 11。对于 Java 8，我们可以在*webupd8team* PPA 中找到这些包。

首先，我们需要将 PPA *apt*存储库添加到我们的系统中：

```bash
$ sudo add-apt-repository ppa:webupd8team/java复制
```

然后我们可以按照通常的方式安装包：

```bash
$ sudo apt update
$ sudo apt install oracle-java8-installer复制
```

在安装过程中，我们必须接受 Oracle 的许可协议。让我们验证安装：

```bash
$ java -version复制
```

输出显示 Java(TM) JRE 和 JVM：

```bash
java version "1.8.0_181"
Java(TM) SE Runtime Environment (build 1.8.0_181-b13)
Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)复制
```

我们还可以验证编译器是否已经安装：

```bash
$ javac -version复制
javac 1.8.0_181复制
```

## 4. 安装 JDK 10

不再支持 Java 10 和 Java 9 版本。您可以按照与第 2 节中类似的步骤手动安装它们。您可以从以下位置获取软件包：

-   https://jdk.java.net/archive/
-   https://www.oracle.com/technetwork/java/javase/archive-139210.html

这两个站点都包含相同的警告：

>   提供这些旧版本的 JDK 是为了帮助开发人员调试旧系统中的问题。 它们未使用最新的安全补丁进行更新，因此不建议在生产中使用。

### **4.1. 手动安装 OpenJDK 10**

让我们看看如何安装 OpenJDK 10.0.1：

```bash
$ wget https://download.java.net/java/GA/jdk10/10.0.1/fb4372174a714e6b8c52526dc134031e/10/openjdk-10.0.1_linux-x64_bin.tar.gz
$ sha256sum openjdk-10.0.1_linux-x64_bin.tar.gz
$ tar xzvf openjdk-10.0.1_linux-x64_bin.tar.gz
$ sudo mkdir /usr/lib/jvm
$ sudo mv jdk-10.0.1 /usr/lib/jvm/openjdk-10-manual-installation/
$ sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/openjdk-10-manual-installation/bin/java 1
$ sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/openjdk-10-manual-installation/bin/javac 1
$ java -version
$ javac -version复制
```

### **4.2. 手动安装 Oracle JDK 10**

正如我们在 2.2 节中看到的，为了从 Oracle 网站下载包， **我们必须首先接受许可协议**。与受支持的版本相反，我们无法通过*wget*和 cookie 下载旧的 Oracle JDK。我们需要前往 https://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase10-4425482.html并下载*tar.gz*文件。之后，我们按照熟悉的步骤进行操作：

```bash
$ sha256sum jdk-10.0.2_linux-x64_bin.tar.gz
$ tar xzvf jdk-10.0.2_linux-x64_bin.tar.gz
$ sudo mkdir /usr/lib/jvm
$ sudo mv jdk-10.0.2 /usr/lib/jvm/oracle-jdk-10-manual-installation/
$ sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/oracle-jdk-10-manual-installation/bin/java 1
$ sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/oracle-jdk-10-manual-installation/bin/javac 1
$ java -version
$ javac -version复制
```

## 5. 安装 JDK 9

### 5.1. 手动安装 OpenJDK 9

*就像我们在上面看到的 OpenJDK 10.0.1 一样，我们通过wget*下载 OpenJDK 9 包并按照约定进行设置：

```bash
$ wget https://download.java.net/java/GA/jdk9/9.0.4/binaries/openjdk-9.0.4_linux-x64_bin.tar.gz
$ sha256sum openjdk-9.0.4_linux-x64_bin.tar.gz
$ tar xzvf openjdk-9.0.4_linux-x64_bin.tar.gz
$ sudo mkdir /usr/lib/jvm
$ sudo mv jdk-9.0.4 /usr/lib/jvm/openjdk-9-manual-installation/
$ sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/openjdk-9-manual-installation/bin/java 1
$ sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/openjdk-9-manual-installation/bin/javac 1
$ java -version
$ javac -version复制
```

### 5.2. 手动安装 Oracle JDK 9

我们再次使用与 JDK 10 相同的方法。我们需要前往 https://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase9-3934878.html并下载*tar。 .gz*文件。之后，我们按照熟悉的步骤进行操作：

```bash
$ sha256sum jdk-9.0.4_linux-x64_bin.tar.gz
$ tar xzvf jdk-9.0.4_linux-x64_bin.tar.gz
$ sudo mkdir /usr/lib/jvm
$ sudo mv jdk-9.0.4 /usr/lib/jvm/oracle-jdk-9-manual-installation/
$ sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/oracle-jdk-9-manual-installation/bin/java 1
$ sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/oracle-jdk-9-manual-installation/bin/javac 1
$ java -version
$ javac -version复制
```

## **6.比较**

我们已经看到了在 Ubuntu 上安装 JDK 的三种不同方式。让我们快速浏览一下它们中的每一个，指出它们的优点和缺点。

### **6.1. “主要”存储库**

这是**“Ubuntu native”安装方式**。*一个很大的优势是我们通过“通常的apt*工作流程”使用*apt update*和*apt upgrade*更新软件包。

此外，“主”存储库由 Canonical 维护，它**提供相当快速（如果不是即时）的更新**。例如，OpenJDK 版本 10.0.1 和 10.0.2 都在发布后的一个月内同步。

### **6.2. PPA**

**PPA 是由单个开发人员或团队维护的**小型存储库。这也意味着更新频率取决于维护者。

来自 PPA 的包被认为**比“主”存储库中的包风险更大**。首先，我们必须将 PPA 显式添加到系统的存储库列表中，表明我们信任它。*之后，我们可以通过常用的apt*工具（*apt update*和*apt upgrade* ）管理包。

### **6.3. 手动安装**

我们直接从 OpenJDK 或 Oracle 站点下载包。**虽然这种方法提供了很大的灵活性，但更新是我们的责任。**如果我们想要拥有最新最好的 JDK，这就是要走的路。

## **7. 探索其他版本的 JDK**

第 2 节和第 3 节中的示例反映了 Ubuntu 18.04 LTS 的当前状态。请记住，JDK 和相应的包会定期更新。因此，了解如何**探索我们当前的可能性**是很有用的。

在本节中，我们将重点调查“主”存储库中的 OpenJDK 包。如果我们已经使用*add-apt-repository添加了一个 PPA，我们可以使用**apt list*和*apt show*以类似的方式探索它。

要发现哪些 PPA 可用，我们可以前往 https://launchpad.net/。如果我们在“主”存储库和 PPA 中找不到我们要找的东西，我们将不得不退回到手动安装。

如果我们想使用不受支持的版本，即使那样也很困难。在撰写本文时，我们没有在 OpenJDK 和 Oracle 网站上找到任何适用于 Java 9 或 Java 10 的包。

让我们看看“Main”存储库中还存在哪些其他 JDK 包：

```bash
$ apt list openjdk*jdk复制
```

在 Ubuntu 18.04 LTS 上，我们可以在两个当前的 LTS Java 版本之间进行选择：

```bash
Listing... Done
openjdk-11-jdk/bionic-updates,bionic-security,now 10.0.2+13-1ubuntu0.18.04.2 amd64 [installed,automatic]
openjdk-8-jdk/bionic-updates,bionic-security 8u181-b13-0ubuntu0.18.04.1 amd64复制
```

还值得注意的是，虽然该包名为*openjdk-11-jdk*，但在撰写本文时，它实际上安装了 10.0.2 版。这可能很快就会改变。我们可以看到，如果我们检查包裹：

```bash
$ apt show openjdk-11-jdk复制
```

让我们看一下输出的“Depends”部分。请注意，这些包（例如 JRE）也会与*openjdk-11-jdk*一起安装：

```bash
Depends: openjdk-11-jre (= 10.0.2+13-1ubuntu0.18.04.2),
openjdk-11-jdk-headless (= 10.0.2+13-1ubuntu0.18.04.2),
libc6 (>= 2.2.5)复制
```

让我们探索一下除了默认的 jdk 包之外还有哪些其他包可供我们使用：

```bash
$ apt list openjdk-11*复制
Listing... Done
openjdk-11-dbg/bionic-updates,bionic-security 10.0.2+13-1ubuntu0.18.04.2 amd64
openjdk-11-demo/bionic-updates,bionic-security 10.0.2+13-1ubuntu0.18.04.2 amd64
openjdk-11-doc/bionic-updates,bionic-updates,bionic-security,bionic-security 10.0.2+13-1ubuntu0.18.04.2 all
openjdk-11-jdk/bionic-updates,bionic-security 10.0.2+13-1ubuntu0.18.04.2 amd64
openjdk-11-jdk-headless/bionic-updates,bionic-security 10.0.2+13-1ubuntu0.18.04.2 amd64
openjdk-11-jre/bionic-updates,bionic-security,now 10.0.2+13-1ubuntu0.18.04.2 amd64 [installed,automatic]
openjdk-11-jre-headless/bionic-updates,bionic-security,now 10.0.2+13-1ubuntu0.18.04.2 amd64 [installed,automatic]
openjdk-11-jre-zero/bionic-updates,bionic-security 10.0.2+13-1ubuntu0.18.04.2 amd64
openjdk-11-source/bionic-updates,bionic-updates,bionic-security,bionic-security 10.0.2+13-1ubuntu0.18.04.2 all复制
```

我们可能会发现其中一些软件包很有用。例如， *openjdk-11-source* 包含 Java 核心 API 类的源文件，而 *openjdk-11-dbg* 包含调试符号。

除了*openjdk-**系列之外，还有值得探索的*default-jdk包：*

```bash
$ apt show default-jdk复制
```

在输出的末尾，描述说：

>   “此依赖包指向 Java 运行时，或为此架构推荐的 Java 兼容开发工具包……”

对于 Ubuntu 18.04 LTS， 目前是*openjdk-11-jdk包。*

## **8. 概述：Java 版本和包**

现在，让我们看看在撰写本文时如何在 Ubuntu 18.04 LTS 上安装不同版本的 Java：

| 版本 | OpenJDK                       | 甲骨文Java                                                |
| ---- | ----------------------------- | --------------------------------------------------------- |
| 11   | 手动安装                      | 在*linuxuprising* PPA中手动安装 *oracle-java11-installer* |
| 10   | 手动安装 – 不支持             | 手动安装 – 不支持                                         |
| 9    | 手动安装 – 不支持             | 手动安装 – 不支持                                         |
| 8    | “主”存储库中的*openjdk-8-jdk* | *webupd8team* PPA中的*oracle-java8-installer*             |

## **9. Ubuntu 系统上的多个 Java 版本**

在 Ubuntu 上管理同一软件的多个版本的标准方法是通过 Debian Alternatives System。大多数时候，我们通过*update-alternatives*程序创建、维护和显示备选方案。

**当\*apt\*安装 JDK 包时，它会自动添加备选项的条目。**在手动安装的情况下，我们已经了解了如何分别添加*java*和*javac*的替代项。

让我们看看我们的替代方案：

```bash
$ update-alternatives --display java复制
```

在我们的测试系统上，我们安装了两个不同版本的 OpenJDK，输出列出了两个备选方案及其各自的优先级：

```bash
java - auto mode
link best version is /usr/lib/jvm/java-11-openjdk-amd64/bin/java
link currently points to /usr/lib/jvm/java-11-openjdk-amd64/bin/java
link java is /usr/bin/java
slave java.1.gz is /usr/share/man/man1/java.1.gz
/usr/lib/jvm/java-11-openjdk-amd64/bin/java - priority 1101
slave java.1.gz: /usr/lib/jvm/java-11-openjdk-amd64/man/man1/java.1.gz
/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java - priority 1081
slave java.1.gz: /usr/lib/jvm/java-8-openjdk-amd64/jre/man/man1/java.1.gz复制
```

现在我们已经看到了我们的替代方案，**我们也可以在它们之间切换：**

```bash
$ sudo update-alternatives --config java复制
```

此外，我们得到一个交互式输出，我们可以通过键盘在备选方案之间切换：

```bash
There are 2 choices for the alternative java (providing /usr/bin/java).

Selection Path Priority Status
------------------------------------------------------------
* 0 /usr/lib/jvm/java-11-openjdk-amd64/bin/java 1101 auto mode
1 /usr/lib/jvm/java-11-openjdk-amd64/bin/java 1101 manual mode
2 /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java 1081 manual mode

Press <enter> to keep the current choice[*], or type selection number:复制
```

如果我们正在处理用不同版本的 Java 编写的多个应用程序，我们很可能还需要不同版本的其他软件（例如 Maven，一些应用程序服务器）。在那种情况下，我们可能要考虑使用更大的抽象，例如 Docker 容器。

## **10.结论**

总而言之，在本文中，我们看到了从“主”存储库、PPA 和手动安装 JDK 的示例。我们已经简要比较了这三种安装方法。

最后，我们了解了如何使用*update-alternatives*管理 Ubuntu 系统上的多个 Java 安装。

下一步，[设置*JAVA_HOME*环境变量](https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux)可能会有用。