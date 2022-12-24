## 1. 简介

[Graylog](https://www.graylog.org/)是一种日志聚合服务，简而言之，它能够从多个来源收集数百万条日志消息并将它们显示在一个界面中。

它还提供了许多其他功能，例如实时警报、带有图形和图表的仪表板等等。

在本教程中，我们将了解如何设置Graylog服务器并从Spring Boot应用程序向其发送日志消息。

## 2. 设置Graylog

有多种安装和运行Graylog的方法，在本教程中，我们将讨论两种最快的方法：Docker和Amazon Web Services。

### 2.1 Docker

以下命令将下载所有必需的Docker镜像并为每个服务启动一个容器：

```shell
$ docker run --name mongo -d mongo:3
$ docker run --name elasticsearch -p 9200:9200 -p 9300:9300 \
    -e ES_JAVA_OPTS="-Xms2g -Xmx4g" \
    -e "discovery.type=single-node" -e "xpack.security.enabled=false" \
    -e "bootstrap.memory_lock=true" --ulimit memlock=-1:-1 \
    -d docker.elastic.co/elasticsearch/elasticsearch:5.6.11
$ docker run --name graylog --link mongo --link elasticsearch \
    -p 9000:9000 -p 12201:12201 -p 514:514 -p 5555:5555 \
    -e GRAYLOG_WEB_ENDPOINT_URI="http://127.0.0.1:9000/api" \
    -d graylog/graylog:2.4.6-1
```

现在可以通过URL [http://localhost:9000/](http://localhost:9000/)使用Graylog仪表板，默认用户名和密码均为admin。

虽然Docker设置是最简单的，但它确实需要大量内存，并且它也不能在Docker for Mac上运行，因此可能并不适合所有平台。

### 2.2 亚马逊Web Services

为测试设置Graylog的下一个最简单的选项是Amazon Web Services，**Graylog提供了一个包含所有必需依赖项的官方AMI**，尽管它在安装后确实需要一些额外的配置。

我们可以在特定区域使用[Graylog AMI](https://github.com/Graylog2/graylog2-images/tree/2.4/aws)快速部署EC2实例，**Graylog建议使用内存至少为4GB的实例**。

实例启动后，我们需要通过SSH连接到主机并进行一些更改，以下命令将为我们配置Graylog服务：

```bash
$ sudo graylog-ctl enforce-ssl
$ sudo graylog-ctl set-external-ip https://<EC2 PUBLIC IP>:443/api/
$ sudo graylog-ctl reconfigure
```

我们还需要更新使用EC2实例创建的安全组，以允许特定端口上的网络流量，下图显示了需要启用的端口和协议：

<img src="../assets/img_3.png">

现在可以通过URL https://<EC2 PUBLIC IP\>/访问Graylog仪表板，默认用户名和密码均为admin。

### 2.3 其他Graylog安装

除了Docker和AWS，还有适用于各种操作系统的[Graylog软件包](http://docs.graylog.org/en/latest/pages/installation/operating_system_packages.html#operating-system-packages)。**通过这种方法，我们还必须设置ElasticSearch和MongoDB服务**。

出于这个原因，Docker和AWS更容易设置，特别是对于开发和测试目的。

## 3. 发送日志消息

Graylog启动并运行后，我们现在必须配置我们的Spring Boot应用程序以将日志消息发送到Graylog服务器。

任何Java日志记录框架都可以支持使用GELF协议将消息发送到Graylog服务器。

### 3.1 Log4J

目前唯一官方支持的日志框架是Log4J，Graylog提供了一个appender，它在[Maven central](https://search.maven.org/artifact/org.graylog2/gelfj)上可用。

我们可以通过将以下Maven依赖项添加到任何pom.xml文件来启用它：

```xml
<dependency>
    <groupId>org.graylog2</groupId>
    <artifactId>gelfj</artifactId>
    <version>1.1.16</version>
</dependency>
```

我们还必须在使用Spring Boot Starter模块的任何地方排除logging-starter模块：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

现在我们可以在log4j.xml文件中定义一个新的appender：

```xml
<appender name="graylog" class="org.graylog2.log.GelfAppender">
    <param name="graylogHost" value="<GRAYLOG IP>"/>
    <param name="originHost" value="localhost"/>
    <param name="graylogPort" value="12201"/>
    <param name="extractStacktrace" value="true"/>
    <param name="addExtendedInformation" value="true"/>
    <param name="facility" value="log4j"/>
    <param name="Threshold" value="INFO"/>
    <param name="additionalFields" value="{'environment': 'DEV', 'application': 'GraylogDemoApplication'}"/>
</appender>
```

这会将所有具有INFO级别或更高级别的日志消息配置为转到Graylog appender，后者又将日志消息发送到Graylog服务器。

### 3.2 其他日志记录框架

Graylog[市场](https://marketplace.graylog.org/addons?kind=gelf&tag=java)还有额外的库支持各种其他日志记录框架，例如Logback、Log4J2等。请注意，这些库不是由Graylog维护的，其中一些已被遗弃，而另一些则很少或根本没有文档。

依赖这些第3方库时应谨慎使用。

### 3.3 Graylog Collector Sidecar

日志收集的另一个选择是[Graylog Collector Sidecar](https://docs.graylog.org/docs/sidecar)，sidecar是一个沿着文件收集器运行的进程，将日志文件内容发送到Graylog服务器。

对于无法更改日志配置文件的应用程序，Sidecar是一个很好的选择。**而且因为它直接从磁盘读取日志文件，所以它也可以用来集成来自任何平台和编程语言的日志消息**。

## 4. 在Graylog中查看消息

我们可以使用Graylog仪表板来确认我们的日志消息是否成功传递，使用过滤器source:localhost将显示来自上面示例log4j配置的日志消息：

<img src="../assets/img_4.png">

## 5. 总结

Graylog只是众多日志聚合服务中的一种，它可以快速搜索数百万条日志消息，实时可视化日志数据，并在满足某些条件时发送警报。

将Graylog集成到Spring Boot应用程序中只需要几行配置，并且不需要任何新代码。