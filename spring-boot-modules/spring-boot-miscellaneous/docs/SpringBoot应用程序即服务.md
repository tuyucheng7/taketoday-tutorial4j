## 1. 概述

本文探讨了将Spring Boot应用程序作为服务运行的一些选项。

首先，我们将解释Web应用程序的打包选项和系统服务。在后续部分中，我们将探讨在为基于Linux的系统和基于Windows的系统设置服务时的不同选择。

最后，我们将以对其他信息来源的一些参考作为结尾。

## 2. 项目设置和构建说明

### 2.1 打包

Web应用程序传统上打包为WAR并部署到Web服务器。

Spring Boot应用程序可以打包为WAR和JAR文件。后者在JAR文件中嵌入了一个Web服务器，这使你无需安装和配置应用程序服务器即可运行应用程序。

### 2.2 Maven配置

让我们从定义pom.xml文件的配置开始：

```xml
<packaging>jar</packaging>

<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.4.0.RELEASE</version>
</parent>

<dependencies>
    <!--....-->
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

packaging必须设置为jar。在撰写本文时，我们使用的是最新的Spring Boot稳定版本，但1.3之后的任何版本都足够了。你可以在[此处](https://spring.io/projects/spring-boot)找到有关可用版本的更多信息。

请注意，我们已将spring-boot-maven-plugin工件的<executable\>参数设置为true。这确保将MANIFEST.MF文件添加到JAR包中。此清单包含一个Main-Class条目，该条目指定哪个类定义了应用程序的main方法。

### 2.3 构建应用程序

在应用程序的根目录中运行以下命令：

```shell
$ mvn clean package
```

可执行JAR文件现在在target目录中可用，我们可以通过在命令行中执行以下命令来启动应用程序：

```shell
$ java -jar your-app.jar
```

此时，你仍然需要使用-jar选项调用Java解释器。有很多原因可以解释为什么通过能够将其作为服务调用来启动你的应用程序会更可取。

## 3. 在Linux上

为了将应用程序作为后台进程运行，我们可以简单地使用nohup Unix命令，但由于各种原因，这也不是首选方式。[本文](http://stackoverflow.com/questions/958249/whats-the-difference-between-nohup-and-a-daemon)中提供了一个很好的解释。

相反，我们将daemonize进程。在Linux下，我们可以选择使用传统的System V初始化脚本或Systemd配置文件来配置守护进程。前者传统上是最广为人知的选择，但正逐渐被后者取代。

你可以在[此处](http://www.tecmint.com/systemd-replaces-init-in-linux/)找到有关此差异的更多详细信息。

为了增强安全性，我们首先创建一个特定用户来运行服务并相应地更改可执行JAR文件权限：

```shell
$ sudo useradd tuyucheng
$ sudo passwd tuyucheng
$ sudo chown tuyucheng:tuyucheng your-app.jar
$ sudo chmod 500 your-app.jar
```

### 3.1 System V初始化

Spring Boot可执行JAR文件使服务设置过程非常简单：

```shell
$ sudo ln -s /path/to/your-app.jar /etc/init.d/your-app
```

上面的命令创建一个指向可执行JAR文件的符号链接。你必须使用可执行JAR文件的完整路径，否则符号链接将无法正常工作。通过此链接，可以将应用程序作为服务启动：

```shell
$ sudo service your-app start
```

该脚本支持标准服务start、stop、restart和status命令。而且：

-   它启动在我们刚刚创建的用户tuyucheng下运行的服务
-   它在/var/run/your-app/your-app.pid中跟踪应用程序的进程ID
-   它将控制台日志写入/var/log/your-app.log，你可能需要检查这些日志，以防应用程序无法正常启动

### 3.2 Systemd

systemd服务设置也非常简单。首先，我们使用以下示例创建一个名为your-app.service的脚本，并将其放在/etc/systemd/system目录中：

```properties
[Unit]
Description=A Spring Boot application
After=syslog.target

[Service]
User=tuyucheng
ExecStart=/path/to/your-app.jar SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

请记住修改Description、User和ExecStart字段以匹配你的应用程序。此时你也应该能够执行上述标准服务命令。

与上一节中描述的System V init方法相反，进程ID文件和控制台日志文件应该使用服务脚本中的适当字段显式配置。可在[此处](http://www.freedesktop.org/software/systemd/man/systemd.service.html)找到详尽的选项列表。

### 3.3 Upstart

[Upstart](http://upstart.ubuntu.com/)是一个基于事件的服务管理器，它是System V init的潜在替代品，它提供了对不同守护进程行为的更多控制。

该站点有很好的[设置说明](http://upstart.ubuntu.com/getting-started.html)，几乎适用于任何Linux发行版。使用Ubuntu时，你可能已经安装并配置了它(检查/etc/init中是否有任何名称以“upstart”开头的作业)。

我们创建一个作业your-app.conf来启动我们的Spring Boot应用程序：

```conf
# Place in /home/{user}/.config/upstart

description "Some Spring Boot application"

respawn # attempt service restart if stops abruptly

exec java -jar /path/to/your-app.jar
```

现在运行“start your-app”，你的服务将启动。

Upstart提供了许多作业配置选项，你可以在[此处](http://upstart.ubuntu.com/cookbook/)找到大部分选项。

## 4. 在Windows上

在本节中，我们将介绍几个可用于将Java JAR作为Windows服务运行的选项。

### 4.1 Windows服务包装器

由于[Java Service Wrapper](http://wrapper.tanukisoftware.org/doc/english/index.html)的GPL许可证(请参阅下一节)与Jenkins的MIT许可证相结合存在困难，[Windows Service Wrapper](https://github.com/kohsuke/winsw)项目(也称为winsw)应运而生。

Winsw提供了安装/卸载/启动/停止服务的编程方式。此外，它可用于在Windows下将任何类型的可执行文件作为服务运行，而Java Service Wrapper，顾名思义，仅支持Java应用程序。

首先，在[此处](http://repo.jenkins-ci.org/releases/com/sun/winsw/winsw/)下载二进制文件。接下来，定义我们的Windows服务的配置文件MyApp.xml应该如下所示：

```xml
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

```shell
$ MyApp.exe install
```

同样，你可以使用uninstall、start、stop等。

### 4.2 Java服务包装器

如果你不介意[Java Service Wrapper](http://wrapper.tanukisoftware.org/doc/english/index.html)项目的GPL许可，此替代方案可以同样很好地满足你将JAR文件配置为Windows服务的需求。基本上，Java Service Wrapper还需要你在配置文件中指定如何在Windows下将进程作为服务运行。

[本文](http://edn.embarcadero.com/article/32068)以非常详细的方式解释了如何在Windows下将JAR文件的执行设置为服务，因此我们无需重复这些信息。

## 5. 其他参考资料

Spring Boot应用程序也可以使用[Apache Commons Daemon](http://commons.apache.org/daemon/index.html)项目的[Procrun](http://commons.apache.org/proper/commons-daemon/procrun.html)作为Windows服务启动。Procrun是一组应用程序，允许Windows用户将Java应用程序包装为Windows服务。这样的服务可以设置为在机器启动时自动启动，并且将在没有任何用户登录的情况下继续运行。

有关在Unix下启动Spring Boot应用程序的更多详细信息，请参见[此处](http://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html)。还有关于如何[为基于Redhat的系统修改Systemd单元文件](https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/7/html/system_administrators_guide/chap-managing_services_with_systemd)的详细说明。

最后，[这个](https://coderwall.com/p/ssuaxa/how-to-make-a-jar-file-linux-executable)快速指南描述了如何将Bash脚本合并到你的JAR文件中，以便它本身成为一个可执行文件！

## 6. 总结

服务允许你非常有效地管理应用程序状态，正如我们所看到的，Spring Boot应用程序的服务设置现在比以往任何时候都容易。

请记住遵循有关用户权限的重要且简单的安全措施来运行你的服务。