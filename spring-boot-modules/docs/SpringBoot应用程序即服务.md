## 1. 概述

本文探讨了将Spring Boot应用程序作为服务运行的一些选项。

首先，我们将解释 Web 应用程序的打包选项和系统服务。在后续部分中，我们将探讨在为基于 Linux 的系统和基于 Windows 的系统设置服务时的不同选择。

最后，我们将以对其他信息来源的一些参考作为结尾。

## 2. 项目设置和构建说明

### 2.1. 包装

Web 应用程序传统上打包为 Web Application aRchives (WAR) 并部署到 Web 服务器。

Spring Boot 应用程序可以打包为 WAR 和 JAR 文件。后者在 JAR 文件中嵌入了一个 Web 服务器，这使你无需安装和配置应用程序服务器即可运行应用程序。

### 2.2. Maven 配置

让我们从定义pom.xml文件的配置开始：

```xml
<packaging>jar</packaging>

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.4.0.RELEASE</version>
</parent>

<dependencies>
    ....
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <executable>true</executable>
            </configuration>
        </plugin>
    </plugins>
</build>
```

包装必须设置为jar。在撰写本文时，我们正在使用最新的Spring Boot稳定版本，但 1.3 之后的任何版本都足够了。[你可以在此处](https://spring.io/projects/spring-boot)找到有关可用版本的更多信息。

请注意，我们已将spring-boot-maven-plugin工件的<executable>参数设置为true。这确保将MANIFEST.MF文件添加到 JAR 包中。此清单包含一个Main-Class条目，该条目指定哪个类定义了你的应用程序的主要方法。

### 2.3. 构建你的应用程序

在应用程序的根目录中运行以下命令：

```bash
$ mvn clean package
```

可执行 JAR 文件现在在目标目录中可用，我们可以通过在命令行中执行以下命令来启动应用程序：

```bash
$ java -jar your-app.jar
```

此时，你仍然需要使用-jar选项调用Java解释器。有很多原因可以解释为什么通过能够将其作为服务调用来启动你的应用程序会更可取。

## 3. 在 Linux 上

为了将程序作为后台进程运行，我们可以简单地使用nohup Unix 命令，但由于各种原因，这也不是首选方式。[此线程](http://stackoverflow.com/questions/958249/whats-the-difference-between-nohup-and-a-daemon)中提供了一个很好的解释。

相反，我们将守护进程。在 Linux 下，我们可以选择使用传统的System V 初始化脚本或Systemd配置文件来配置守护进程。前者传统上是最广为人知的选择，但正逐渐被后者取代。

[你可以在此处](http://www.tecmint.com/systemd-replaces-init-in-linux/)找到有关此差异的更多详细信息。

为了增强安全性，我们首先创建一个特定用户来运行服务并相应地更改可执行 JAR 文件权限：

```bash
$ sudo useradd baeldung
$ sudo passwd baeldung
$ sudo chown baeldung:baeldung your-app.jar
$ sudo chmod 500 your-app.jar
```

### 3.1. 系统 V 初始化

Spring Boot 可执行 JAR 文件使服务设置过程非常简单：

```bash
$ sudo ln -s /path/to/your-app.jar /etc/init.d/your-app
```

上面的命令创建一个指向可执行 JAR 文件的符号链接。你必须使用可执行 JAR 文件的完整路径，否则符号链接将无法正常工作。此链接使你能够将应用程序作为服务启动：

```bash
$ sudo service your-app start
```

该脚本支持标准服务启动、停止、重启和状态命令。而且：

-   它启动在我们刚刚创建的用户baeldung下运行的服务
-   它在/var/run/your-app/your-app.pid中跟踪应用程序的进程 ID
-   它将控制台日志写入/var/log/your-app.log，你可能需要检查它以防你的应用程序无法正常启动

### 3.2. 系统化

systemd服务设置也非常简单。首先，我们使用以下示例创建一个名为your-app.service 的脚本，并将其放在/etc/systemd/system目录中：

```plaintext
[Unit]
Description=A Spring Boot application
After=syslog.target

[Service]
User=baeldung
ExecStart=/path/to/your-app.jar SuccessExitStatus=143 

[Install] 
WantedBy=multi-user.target
```

请记住修改Description、User和ExecStart字段以匹配你的应用程序。此时你也应该能够执行上述标准服务命令。

与上一节中描述的System V init方法相反，进程 ID 文件和控制台日志文件应该使用服务脚本中的适当字段显式配置。[可在此处](http://www.freedesktop.org/software/systemd/man/systemd.service.html)找到详尽的选项列表。

### 3.3. 暴发户

[Upstart](http://upstart.ubuntu.com/)是一个基于事件的服务管理器，它是System V init的潜在替代品，它提供了对不同守护进程行为的更多控制。

该站点有很好的[设置说明](http://upstart.ubuntu.com/getting-started.html)，几乎适用于任何 Linux 发行版。使用 Ubuntu 时，你可能已经安装并配置了它(检查/etc/init中是否有任何名称以“upstart”开头的作业)。

我们创建一个工作your-app.conf来启动我们的Spring Boot应用程序：

```plaintext
# Place in /home/{user}/.config/upstart

description "Some Spring Boot application"

respawn # attempt service restart if stops abruptly

exec java -jar /path/to/your-app.jar

```

现在运行“启动你的应用程序”，你的服务将启动。

[Upstart 提供了许多作业配置选项，你可以在此处](http://upstart.ubuntu.com/cookbook/)找到大部分选项。

## 4. 在 Windows 上

在本节中，我们将介绍几个可用于将JavaJAR 作为 Windows 服务运行的选项。

### 4.1. Windows 服务包装器

由于[Java Service Wrapper](http://wrapper.tanukisoftware.org/doc/english/index.html)的 GPL 许可证(请参阅下一节)与 Jenkins 的 MIT 许可证相结合存在困难[，Windows Service Wrapper](https://github.com/kohsuke/winsw)项目(也称为winsw)应运而生。

Winsw提供了安装/卸载/启动/停止服务的编程方式。此外，它可用于在 Windows 下将任何类型的可执行文件作为服务运行，而JavaService Wrapper，顾名思义，仅支持Java应用程序。

[首先，你在此处](http://repo.jenkins-ci.org/releases/com/sun/winsw/winsw/)下载二进制文件。接下来，定义我们的 Windows 服务的配置文件MyApp.xml应该如下所示：

```plaintext
<service>
    <id>MyApp</id>
    <name>MyApp</name>
    <description>This runs Spring Boot as a Service.</description>
    <env name="MYAPP_HOME" value="%BASE%"/>
    <executable>java</executable>
    <arguments>-Xmx256m -jar "%BASE%\MyApp.jar"</arguments>
    <logmode>rotate</logmode>
</service>

```

最后，你必须将winsw.exe重命名为MyApp.exe，以便其名称与MyApp.xml配置文件匹配。此后，你可以像这样安装服务：

```plaintext
$ MyApp.exe install
```

同样，你可以使用uninstall、start、stop等。

### 4.2.Java服务包装器

如果你不介意[Java Service Wrapper](http://wrapper.tanukisoftware.org/doc/english/index.html)项目的 GPL 许可，此替代方案可能会同样满足你将 JAR 文件配置为 Windows 服务的需要。基本上，Java Service Wrapper 还需要你在配置文件中指定，该文件指定如何在 Windows 下将你的进程作为服务运行。

[本文](http://edn.embarcadero.com/article/32068)以非常详细的方式解释了如何在 Windows 下将 JAR 文件的执行设置为服务，因此我们无需重复这些信息。

## 5. 附加参考

[Spring Boot 应用程序也可以使用Apache Commons Daemon](http://commons.apache.org/daemon/index.html)项目的[Procrun](http://commons.apache.org/proper/commons-daemon/procrun.html)作为 Windows 服务启动。Procrun 是一组应用程序，允许 Windows 用户将Java应用程序包装为 Windows 服务。这样的服务可以设置为在机器启动时自动启动，并且将在没有任何用户登录的情况下继续运行。

有关在 Unix 下启动Spring Boot应用程序的更多详细信息，请参见[此处](http://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html)。还有关于如何[为基于 Redhat 的系统修改 Systemd 单元文件](https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/7/html/system_administrators_guide/chap-managing_services_with_systemd)的详细说明。最后

最后，[这个快速指南](https://coderwall.com/p/ssuaxa/how-to-make-a-jar-file-linux-executable)描述了如何将 Bash 脚本合并到你的 JAR 文件中，以便它本身成为一个可执行文件！

## 六，总结

服务允许你非常有效地管理应用程序状态，正如我们所见，Spring Boot 应用程序的服务设置现在比以往任何时候都容易。

请记住遵循有关用户权限的重要且简单的安全措施来运行你的服务。