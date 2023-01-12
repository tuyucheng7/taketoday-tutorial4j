## 1. 简介

在本教程中，我们探索了[JBoss WildFly 应用服务器](https://jbossorg.github.io/wildflysite/)的不同服务器模式和配置。WildFly 是一个带有 CLI 和管理控制台的轻量级应用程序服务器。

不过，在开始之前，我们需要确保将JAVA_HOME变量设置为 JDK。版本 8 之后的任何内容都适用于 WildFly 17。

## 2. 服务器模式

WildFly 默认带有独立模式和域模式。我们先来看一下standalone。

### 2.1. 单机

下载 WildFly 并将其解压到本地目录后，我们需要执行用户脚本。

对于 Linux，我们会这样做：

```bash
~/bin/add-user.sh
```

或者对于 Windows：

```bash
~binadd-user.bat
```

admin用户已经存在；但是，我们希望将密码更新为不同的内容。更新默认管理员密码始终是最佳做法，即使计划使用另一个帐户进行服务器管理也是如此。

UI 将提示我们更新选项 (a) 中的密码：

```bash
Enter the details of the new user to add.
Using realm 'ManagementRealm' as discovered from the existing property files.
Username : admin
User 'admin' already exists and is enabled, would you like to...
 a) Update the existing user password and roles
 b) Disable the existing user
 c) Type a new username
(a):
```

更改密码后，我们需要运行适合操作系统的启动脚本：

```bash
~binstandalone.bat
```

服务器输出停止后，我们看到服务器正在运行。

我们现在可以按照输出中的提示在[http://127.0.0.1:9990](http://127.0.0.1:9990/)访问管理控制台。如果我们访问服务器 URL http://127.0.0.1:8080/，我们应该收到一条消息提示我们服务器正在运行：

```plaintext
Server is up and running!
```

这种快速简便的设置非常适合在单个服务器上运行的单独实例，但让我们检查多个实例的域模式。

### 2.2. 领域

如上所述，域模式具有由单个主机控制器管理的多个服务器实例。默认数量是两台服务器。

与独立实例类似，我们通过添加用户脚本添加用户。但是，启动脚本被恰当地称为域而不是独立的。

在启动过程中运行相同的步骤后，我们现在访问各个服务器实例。

例如，我们可以访问第一台服务器的同一个单实例 URL [http://127.0.0.1:8080/](http://127.0.0.1:8080/)

```plaintext
Server is up and running!
```

同样，我们可以访问服务器二[http://127.0.0.1:8230](http://127.0.0.1:8230/)

```plaintext
Server is up and running!
```

稍后我们将看一下服务器二的奇怪端口配置。

## 3.部署

要部署我们的第一个应用程序，我们需要一个很好的示例应用程序。

让我们使用[Spring Boot Hello World](https://spring.io/guides/gs/spring-boot/)。稍微修改一下，它就可以在 WildFly 上运行。

首先，让我们更新pom.xml以构建 WAR。我们将更改 包装 元素并添加[maven-war-plugin](https://search.maven.org/search?q=g:org.apache.maven.plugins AND a:maven-war-plugin)：

```xml
<packaging>war</packaging>
...
<build>
  <plugins>
	<plugin>
	  <groupId>org.apache.maven.plugins</groupId>
	  <artifactId>maven-war-plugin</artifactId>
	  <configuration>
		<archive>
		  <manifestEntries>
			<Dependencies>jdk.unsupported</Dependencies>
		  </manifestEntries>
		</archive>
	  </configuration>
	</plugin>
  </plugins>
</build>
```

接下来，我们需要更改Application类以[扩展SpringBootServletInitializer](https://www.baeldung.com/spring-boot-servlet-initializer)：

```java
public class Application extends SpringBootServletInitializer
```

并覆盖配置方法：

```java
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
```

最后，让我们构建项目：

```bash
./mvnw package
```

现在，我们可以部署它了。

### 3.1. 部署应用程序

让我们看看通过控制台部署新的 WildFly 应用程序是多么容易。

首先，我们使用之前设置的admin用户和密码登录控制台。

单击添加 -> 上传部署 -> 选择一个文件...

其次，我们浏览到Spring Boot应用程序的目标目录并上传 WAR 文件。单击下一步继续，并编辑新应用程序的名称。

[最后，我们通过相同的 URL – http://127.0.0.1:8080/](http://127.0.0.1:8080/)访问应用程序。我们现在看到应用程序输出为：

```plaintext
Greetings from Spring Boot!
```

作为应用程序部署成功的结果，让我们回顾一些常见的配置属性。

## 4.服务器配置

对于这些公共属性，在管理控制台中查看是最方便的。

### 4.1. 通用配置属性

首先，让我们看一下子系统——每个子系统都有一个启用统计的值，可以将其设置为true以进行运行时统计：

[![子系统配置](https://baeldung.com/wp-content/uploads/2019/10/subsystems_configuration.png)](https://baeldung.com/wp-content/uploads/2019/10/subsystems_configuration.png)

在路径部分下，我们看到服务器使用的几个重要文件路径：

[![路径配置](https://baeldung.com/wp-content/uploads/2019/10/paths_configuration-1024x362.png)](https://baeldung.com/wp-content/uploads/2019/10/paths_configuration-1024x362.png)

例如，为配置目录和日志目录使用特定值是有帮助的：

```plaintext
jboss.server.config.dir
jboss.server.log.dirw
```

此外，数据源和驱动程序部分提供用于管理外部数据源和数据持久层的不同驱动程序：

[![数据源配置](https://baeldung.com/wp-content/uploads/2019/10/datasources_configuration-1024x522.png)](https://baeldung.com/wp-content/uploads/2019/10/datasources_configuration-1024x522.png)

在Logging子系统中，我们将日志级别从 INFO 更新为 DEBUG 以查看更多应用程序日志：

[![日志配置](https://baeldung.com/wp-content/uploads/2019/10/logging_configuration-1024x520.png)](https://baeldung.com/wp-content/uploads/2019/10/logging_configuration-1024x520.png)

### 4.2. 服务器配置文件

还记得域模式下的服务器二 URL， http: [//127.0.0.1](http://127.0.0.1:8230/) :8230吗？奇数端口8230 是由于host-slave.xml配置文件中的默认偏移值150所致：

```xml
<server name="server-one" group="main-server-group"/>
<server name="server-two" group="main-server-group" auto-start="true">
  <jvm name="default"/>
  <socket-bindings port-offset="150"/>
</server>

```

但是，我们可以对端口偏移值进行简单更新：

```xml
<socket-bindings port-offset="10"/>
```

[因此，我们现在通过 URL http://127.0.0.1:8090](http://127.0.0.1:8090/)访问服务器二。

在方便的管理控制台中使用和配置应用程序的另一个重要功能是这些设置可以导出为 XML 文件。通过 XML 文件使用一致的配置可以简化对多个环境的管理。

## 5.监控

WildFly 的命令行界面允许查看和更新与管理控制台相同的配置值。

要使用 CLI，我们只需执行：

```bash
~binjboss-cli.bat
```

之后我们输入：

```bash
[disconnected /] connect
[domain@localhost:9990 /]
```

因此，我们可以按照提示连接到我们的服务器实例。连接后，我们可以实时访问配置。

例如，要在连接到服务器实例时以 XML 形式查看所有当前配置，我们可以运行read-config-as-xml命令：

```bash
[domain@localhost:9990 /] :read-config-as-xml
```

CLI 还可以用于任何服务器子系统的运行时统计。最重要的是，CLI 非常适合通过服务器指标识别不同的问题。

## 六. 总结

在本教程中，我们介绍了在 WildFly 中设置和部署第一个应用程序以及服务器的一些配置选项。