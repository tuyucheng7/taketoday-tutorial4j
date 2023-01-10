## 1. 概述

[HTTP/2](https://en.wikipedia.org/wiki/HTTP/2)协议带有推送功能，允许服务器为单个请求向客户端发送多个资源。因此，它通过减少获取所有资源所需的多次往返来缩短页面的加载时间。

[Jetty](https://www.baeldung.com/jetty-eclipse)支持客户端和服务器实现的 HTTP/2 协议。

在本教程中，我们将探索 Jetty 中的 HTTP/2 支持并创建一个JavaWeb 应用程序来检查[HTTP/2 推送功能](https://en.wikipedia.org/wiki/HTTP/2_Server_Push)。

## 2. 开始

### 2.1. 下载码头

Jetty 需要 JDK 8 或更高版本以及[ALPN](https://en.wikipedia.org/wiki/Application-Layer_Protocol_Negotiation)(应用层协议协商)支持才能运行 HTTP/2。

通常，Jetty 服务器通过 SSL 部署，并通过 TLS 扩展 (ALPN) 启用 HTTP/2 协议。

首先，我们需要下载最新的[Jetty](https://www.eclipse.org/jetty/download.html)发行版并设置JETTY_HOME变量。

### 2.2. 启用 HTTP/2 连接器

接下来，我们可以使用Java命令在 Jetty 服务器上启用 HTTP/2 连接器：

```shell
java -jar $JETTY_HOME/start.jar --add-to-start=http2
```

此命令将 HTTP/2 协议支持添加到端口8443上的 SSL 连接器。此外，它还可以传递地启用 ALPN 模块进行协议协商：

```shell
INFO  : server          transitively enabled, ini template available with --add-to-start=server
INFO  : alpn-impl/alpn-1.8.0_131 dynamic dependency of alpn-impl/alpn-8
INFO  : alpn-impl       transitively enabled
INFO  : alpn            transitively enabled, ini template available with --add-to-start=alpn
INFO  : alpn-impl/alpn-8 dynamic dependency of alpn-impl
INFO  : http2           initialized in ${jetty.base}/start.ini
INFO  : ssl             transitively enabled, ini template available with --add-to-start=ssl
INFO  : threadpool      transitively enabled, ini template available with --add-to-start=threadpool
INFO  : bytebufferpool  transitively enabled, ini template available with --add-to-start=bytebufferpool
INFO  : Base directory was modified
```

在这里，日志显示了为 HTTP/2 连接器临时启用的ssl和alpn-impl/alpn-8等模块的信息。

### 2.3. 启动码头服务器

现在，我们准备启动 Jetty 服务器：

```shell
java -jar $JETTY_HOME/start.jar
```

服务器启动时，日志记录将显示已启用的模块：

```shell
INFO::main: Logging initialized @228ms to org.eclipse.jetty.util.log.StdErrLog
...
INFO:oejs.AbstractConnector:main: Started ServerConnector@42dafa95{SSL, (ssl, alpn, h2)}{0.0.0.0:8443}
INFO:oejs.Server:main: Started @872ms
```

### 2.4. 启用附加模块

同样，我们可以启用其他模块，如http和http2c：

```shell
java -jar $JETTY_HOME/start.jar --add-to-start=http,http2c
```

让我们验证日志：

```shell
INFO:oejs.AbstractConnector:main: Started ServerConnector@6adede5{SSL, (ssl, alpn, h2)}{0.0.0.0:8443}
INFO:oejs.AbstractConnector:main: Started ServerConnector@dc24521{HTTP/1.1, (http/1.1, h2c)}{0.0.0.0:8080}
INFO:oejs.Server:main: Started @685ms
```

另外，我们可以列出 Jetty 提供的所有模块：

```shell
java -jar $JETTY_HOME/start.jar --list-modules
```

输出将如下所示：

```shell
Available Modules:
==================
tags: [-internal]
Modules for tag '':
--------------------
     Module: alpn 
           : Enables the ALPN (Application Layer Protocol Negotiation) TLS extension.
     Depend: ssl, alpn-impl
        LIB: lib/jetty-alpn-client-${jetty.version}.jar
        LIB: lib/jetty-alpn-server-${jetty.version}.jar
        XML: etc/jetty-alpn.xml
    Enabled: transitive provider of alpn for http2
    // ...

Modules for tag 'connector':
----------------------------
     Module: http2 
           : Enables HTTP2 protocol support on the TLS(SSL) Connector,
           : using the ALPN extension to select which protocol to use.
       Tags: connector, http2, http, ssl
     Depend: ssl, alpn
        LIB: lib/http2/.jar
        XML: etc/jetty-http2.xml
    Enabled: ${jetty.base}/start.ini
    // ...

Enabled Modules:
================
    0) alpn-impl/alpn-8 dynamic dependency of alpn-impl
    1) http2           ${jetty.base}/start.ini
    // ...
```

### 2.5. 附加配置

与–list-modules参数类似，我们可以使用–list-config列出每个模块的所有 XML 配置文件：

```shell
java -jar $JETTY_HOME/start.jar --list-config
```

要为 Jetty 服务器配置主机和端口等公共属性，我们可以在start.ini文件中进行更改：

```plaintext
jetty.ssl.host=0.0.0.0
jetty.ssl.port=8443
jetty.ssl.idleTimeout=30000
```

此外，我们还可以配置一些http2属性，例如maxConcurrentStreams和maxSettingsKeys：


```plaintext
jetty.http2.maxConcurrentStreams=128
jetty.http2.initialStreamRecvWindow=524288
jetty.http2.initialSessionRecvWindow=1048576
jetty.http2.maxSettingsKeys=64
jetty.http2.rateControl.maxEventsPerSecond=20
```

## 3. 设置 Jetty 服务器应用程序

### 3.1. Maven 配置

现在我们已经配置了 Jetty，是时候创建我们的应用程序了。

让我们将[jetty-maven-plugin](https://search.maven.org/search?q=g:org.eclipse.jetty a:jetty-maven-plugin) Maven 插件添加到我们的pom.xml以及 Maven 依赖项，如[http2-server](https://search.maven.org/search?q=g:org.eclipse.jetty.http2 a:http2-server)、[jetty-alpn-openjdk8-server](https://search.maven.org/search?q=g:org.eclipse.jetty a:jetty-alpn-openjdk8-server)和[jetty-servlets](https://search.maven.org/search?q=g:org.eclipse.jetty a:jetty-servlets)：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-maven-plugin</artifactId>
            <version>9.4.27.v20200227</version>
            <dependencies>
                <dependency>
                    <groupId>org.eclipse.jetty.http2</groupId>
                    <artifactId>http2-server</artifactId>
                    <version>9.4.27.v20200227</version>
                </dependency>
                <dependency>
                    <groupId>org.eclipse.jetty</groupId>
                    <artifactId>jetty-alpn-openjdk8-server</artifactId>
                    <version>9.4.27.v20200227</version>
                </dependency>
                <dependency>
                    <groupId>org.eclipse.jetty</groupId>
                    <artifactId>jetty-servlets</artifactId>
                    <version>9.4.27.v20200227</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

然后，我们将使用 Maven 命令编译这些类：

```shell
mvn clean package
```

最后，我们可以将未组装的 Maven 应用程序部署到 Jetty 服务器：

```shell
mvn jetty:run-forked
```

默认情况下，服务器使用 HTTP/1.1 协议在端口8080上启动：

```shell
oejmp.Starter:main: Started Jetty Server
oejs.AbstractConnector:main: Started ServerConnector@4d910fd6{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
oejs.Server:main: Started @1045ms
```

### 3.2. 在jetty.xml中配置 HTTP/2

接下来，我们将通过添加适当的Call元素在jetty.xml文件中使用 HTTP/2 协议配置 Jetty 服务器：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE Configure PUBLIC "-//Jetty//Configure//EN" "http://www.eclipse.org/jetty/configure_9_0.dtd">
<Configure id="Server" class="org.eclipse.jetty.server.Server">
    <!-- sslContextFactory and httpConfig configs-->

    <Call name="addConnector">
        <Arg>
            <New class="org.eclipse.jetty.server.ServerConnector">
                <Arg name="server"><Ref id="Server"/></Arg>
                <Arg name="factories">
                    <Array type="org.eclipse.jetty.server.ConnectionFactory">
                        <Item>
                            <New class="org.eclipse.jetty.server.SslConnectionFactory">
                                <Arg name="sslContextFactory"><Ref id="sslContextFactory"/></Arg>
                                <Arg name="next">alpn</Arg>
                            </New>
                        </Item>
                        <Item>
                            <New class="org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory">
                                <Arg>h2</Arg>
                            </New>
                        </Item>
                        <Item>
                            <New class="org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory">
                                <Arg name="config"><Ref id="httpConfig"/></Arg>
                            </New>
                        </Item>
                    </Array>
                </Arg>
                <Set name="port">8444</Set>
            </New>
        </Arg>
    </Call>

    <!-- other Call elements -->
</Configure>
```

在这里，HTTP/2 连接器在端口8444上配置了 ALPN以及sslContextFactory和httpConfig配置。

此外，我们可以通过在jetty.xml中定义以逗号分隔的参数来添加其他模块，如h2-17和h2-16(h2的草稿版本) ：

```xml
<Item> 
    <New class="org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory"> 
        <Arg>h2,h2-17,h2-16</Arg> 
    </New> 
</Item>
```

然后，我们将在我们的pom.xml中配置jetty.xml的位置：

```xml
<plugin>
    <groupId>org.eclipse.jetty</groupId>
    <artifactId>jetty-maven-plugin</artifactId>
    <version>9.4.27.v20200227</version>
    <configuration>
        <stopPort>8888</stopPort>
        <stopKey>quit</stopKey>
        <jvmArgs>
            -Xbootclasspath/p:
            ${settings.localRepository}/org/mortbay/jetty/alpn/alpn-boot/8.1.11.v20170118/alpn-boot-8.1.11.v20170118.jar
        </jvmArgs>
        <jettyXml>${basedir}/src/main/config/jetty.xml</jettyXml>
        <webApp>
            <contextPath>/</contextPath>
        </webApp>
    </configuration>
    ...
</plugin>
```

注意：为了在我们的Java8 应用程序中启用 HTTP/2，我们已将[alpn-boot](https://search.maven.org/search?q=g:org.mortbay.jetty.alpn a:alpn-boot) jar添加到 JVM BootClasspath中。但是，ALPN 支持已经在Java9 或更高版本中可用。

让我们重新编译我们的类并重新运行应用程序以验证是否启用了 HTTP/2 协议：

```shell
oejmp.Starter:main: Started Jetty Server
oejs.AbstractConnector:main: Started ServerConnector@6fadae5d{SSL, (ssl, http/1.1)}{0.0.0.0:8443}
oejs.AbstractConnector:main: Started ServerConnector@1810399e{SSL, (ssl, alpn, h2)}{0.0.0.0:8444}
```

在这里，我们可以观察到端口8443配置了 HTTP/1.1 协议，8444配置了 HTTP/2。

### 3.3. 配置PushCacheFilter

接下来，我们需要一个过滤器，将图像、JavaScript 和 CSS 等辅助资源推送到客户端。

为此，我们可以使用org.eclipse.jetty.servlets包中可用的[PushCacheFilter](https://www.eclipse.org/jetty/documentation/jetty-9/index.html#http2-configuring-push)类。PushCacheFilter构建与主要资源(如index.html)关联的次要资源的缓存，并将它们推送到客户端。

让我们在web.xml中配置PushCacheFilter：

```xml
<filter>
    <filter-name>push</filter-name>
    <filter-class>org.eclipse.jetty.servlets.PushCacheFilter</filter-class>
    <init-param>
        <param-name>ports</param-name>
        <param-value>8444</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>push</filter-name>
    <url-pattern>/</url-pattern>
</filter-mapping>
```

### 3.4. 配置 Jetty Servlet 和 Servlet 映射

然后，我们将创建Http2JettyServlet类来访问图像，我们将在我们的web.xml文件中添加servlet 映射：

```xml
<servlet>
    <servlet-name>http2Jetty</servlet-name>
    <servlet-class>com.baeldung.jetty.http2.Http2JettyServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>http2Jetty</servlet-name>
    <url-pattern>/images/</url-pattern>
</servlet-mapping>
```

## 4. 设置 HTTP/2 客户端

最后，为了验证 HTTP/2 推送功能和改进的页面加载时间，我们将创建一个加载一些图像(辅助资源)的http2.html文件：

```html
<!DOCTYPE html>
<html>
<head>
    <title>Baeldung HTTP/2 Client in Jetty</title>
</head>
<body>
    <h2>HTTP/2 Demo</h2>
    <div>
        <img src="images/homepage-latest_articles.jpg" alt="latest articles" />
        <img src="images/homepage-rest_with_spring.jpg" alt="rest with spring" />
        <img src="images/homepage-weekly_reviews.jpg" alt="weekly reviews" />
    </div>
</body>
</html>
```

## 5. 测试 HTTP/2 客户端

为了获得页面加载时间的基线，让我们使用开发人员工具访问位于 [https://localhost:8443/http2.html](https://localhost:8443/http2.html)的 HTTP/1.1 应用程序以验证协议和加载时间：

 

[![http2 截图 1](https://www.baeldung.com/wp-content/uploads/2020/04/http2-screenshot-1.png)](https://www.baeldung.com/wp-content/uploads/2020/04/http2-screenshot-1.png)

在这里，我们可以观察到使用 HTTP/1.1 协议在 3-6 毫秒内加载图像。

然后，我们将在[https://localhost:8444/http2.html](https://localhost:8444/http2.html)访问启用了推送的 HTTP/2 应用程序：

 

 

[![http2 截图 2](https://www.baeldung.com/wp-content/uploads/2020/04/http2-screenshot-2.png)](https://www.baeldung.com/wp-content/uploads/2020/04/http2-screenshot-2.png)

在这里，我们观察到协议是h2，发起者是Push，所有图像(二级资源)的加载时间都是 1ms。

因此，PushCacheFilter缓存了 http2.html 的二级资源，推送到8444端口，极大地改善了页面的加载时间。

## 六. 总结

在本教程中，我们探索了 Jetty 中的 HTTP/2。

首先，我们检查了如何使用 HTTP/2 协议及其配置启动 Jetty。

然后，我们看到了一个具有 HTTP/2 推送功能的Java8 Web 应用程序，配置了PushCacheFilter，并观察了包含辅助资源的页面的加载时间如何比我们在 HTTP/1.1 协议中看到的有所改进。