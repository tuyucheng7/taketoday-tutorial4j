## 1. 概述

默认情况下，Spring Boot Web应用程序包括一个预配置的嵌入式Web服务器。但是，在某些情况下，我们希望**修改默认配置**以满足自定义要求。

在本教程中，我们将了解一些通过application.properties文件配置Tomcat嵌入式服务器的常见用例。

## 2. 常见的嵌入式Tomcat配置

### 2.1 服务器地址和端口

我们可能希望更改的最常见配置是端口号：

```properties
server.port=80
```

如果我们不提供server.port参数，则默认情况下Spring将其设置为8080。

在某些情况下，我们可能希望设置服务器应绑定的网络地址。换句话说，我们定义了一个IP地址，我们的服务器将在其中监听：

```properties
server.address=my_custom_ip
```

默认情况下，该值设置为0.0.0.0，这允许通过所有IPv4地址进行连接。设置另一个值，例如localhost或者127.0.0.1将使服务器更具选择性。

### 2.2 错误处理

**默认情况下，Spring Boot提供了一个标准的错误网页**，此页面称为Whitelabel。默认情况下它处于启用状态，但是如果我们不想显示任何错误信息，我们可以禁用它：

```properties
server.error.whitelabel.enabled=false
```

Whitelabel的默认路径是/error，我们可以通过设置server.error.path参数来自定义它：

```properties
server.error.path=/user-error
```

我们还可以设置属性来确定显示哪些错误信息。例如，我们可以包含错误消息和堆栈跟踪：

```properties
server.error.include-exception=true
server.error.include-stacktrace=always
```

我们的[REST的异常消息处理]()和[自定义Whitelabel错误页面]()文章解释了有关在Spring Boot中处理错误的更多信息。

### 2.3 服务器连接

在资源不足的容器上运行时，我们可能希望**减少CPU和内存负载**，这样做的一种方法是限制我们的应用程序可以处理的同时请求的数量。相反，我们可以增加此值以使用更多可用资源来获得更好的性能。

在Spring Boot中，我们可以定义Tomcat工作线程的最大数量：

```properties
server.tomcat.threads.max=200
```

配置Web服务器时，**设置服务器连接超时**也可能很有用，这表示服务器在连接关闭之前等待客户端发出请求的最长时间：

```properties
server.connection-timeout=5s
```

我们还可以定义请求标头的最大大小：

```properties
server.max-http-header-size=8KB
```

请求正文的最大大小：

```properties
server.tomcat.max-swallow-size=2MB
```

或整个发布请求的最大大小：

```properties
server.tomcat.max-http-post-size=2MB
```

### 2.4 SSL

要在我们的Spring Boot应用程序中**启用SSL支持**，我们需要将server.ssl.enabled属性设置为true并定义一个SSL协议：

```properties
server.ssl.enabled=true
server.ssl.protocol=TLS
```

我们还应该配置保存证书的密钥库的密码、类型和路径：

```properties
server.ssl.key-store-password=my_password
server.ssl.key-store-type=keystore_type
server.ssl.key-store=keystore-path
```

我们还必须定义在密钥库中标识密钥的别名：

```properties
server.ssl.key-alias=tomcat
```

有关SSL配置的更多信息，请访问我们[在Spring Boot中使用自签名证书的HTTPS](https://www.baeldung.com/spring-boot-https-self-signed-certificate)文章。

### 2.5 Tomcat服务器访问日志

Tomcat访问日志在测量页面点击次数、用户会话活动等方面非常有用。

**要启用访问日志**，只需设置：

```properties
server.tomcat.accesslog.enabled=true
```

我们还应该配置其他参数，例如附加到日志文件的目录名、前缀、后缀和日期格式：

```properties
server.tomcat.accesslog.directory=logs
server.tomcat.accesslog.file-date-format=yyyy-MM-dd
server.tomcat.accesslog.prefix=access_log
server.tomcat.accesslog.suffix=.log
```

## 3. 嵌入式Tomcat版本

我们无法通过配置application.properties文件来更改正在使用的Tomcat版本。这有点复杂，主要取决于我们有没有使用spring-boot-starter-parent。

但是，在我们继续之前，我们必须知道**每个Spring Boot版本都是针对特定的Tomcat版本设计和测试的。如果我们更改它，我们可能会面临一些意想不到的兼容性问题**。

### 3.1 使用spring-boot-starter-parent

如果我们使用Maven并将我们的项目配置为继承自spring-boot-starter-parent，我们可以通过覆盖pom.xml中的特定属性来覆盖各个依赖项。

考虑到这一点，要更新Tomcat版本，我们必须使用tomcat.version属性：

```xml
<properties>
    <tomcat.version>9.0.44</tomcat.version>
</properties>
```

### 3.2 使用spring-boot-dependencies

在有些情况下我们不想或不能使用spring-boot-starter-parent。例如，如果我们[在我们的Spring Boot项目中使用自定义父级]()，在这种情况下，我们很有可能使用spring-boot-dependency仍然从依赖管理中受益。

但是，此设置不允许我们使用Maven属性覆盖各个依赖项，如上一节所示。

为了实现相同的目标并仍然使用不同的Tomcat版本，**我们需要在pom文件的dependencyManagement部分添加一个条目，要记住的关键是我们必须将它放在spring-boot-dependencies之前**：

```xml
<dependencyManagement>
	<dependencies>
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-core</artifactId>
			<version>9.0.44</version>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-dependencies</artifactId>
			<version>2.4.5</version>
			<type>pom</type>
			<scope>import</scope>
		</dependency>
	</dependencies>
</dependencyManagement>
```

## 4. 总结

在本教程中，我们学习了一些常见的Tomcat嵌入式服务器配置。要查看更多可能的配置，请访问官方[Spring Boot应用程序属性文档](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)页面。