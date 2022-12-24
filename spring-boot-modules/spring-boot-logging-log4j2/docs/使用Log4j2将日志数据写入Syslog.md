## 1. 概述

日志记录是每个应用程序中的重要组成部分，当我们在应用程序中使用日志记录机制时，我们可以将日志存储在文件或数据库中。此外，我们可以将日志数据发送到集中式日志管理应用程序，如[Graylog]()或[Syslog](https://en.wikipedia.org/wiki/Syslog)：

<img src="../assets/img_5.png">

在本教程中，我们将介绍如何在[Spring Boot应用程序]()中使用[Log4j2]()将日志记录信息发送到Syslog服务器。

## 2. Log4j2

Log4j2是Log4j的最新版本，它是高性能日志记录的常见选择，并用于许多生产应用程序。

### 2.1 Maven依赖

首先我们将[spring-boot-starter-log4j2](https://search.maven.org/search?q=a:spring-boot-starter-log4j2)依赖项添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
    <version>2.5.2</version>
</dependency>
```

为了在Spring Boot应用程序中配置Log4j2，我们需要从pom.xml中的任何Starter库中排除默认的Logback日志记录框架。在我们的项目中，只有spring-boot-starter-web starter依赖项，让我们从中排除默认日志记录：

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

### 2.2 Log4j2配置

现在，我们将创建Log4j2配置文件，Spring Boot项目在类路径中搜索log4j2-spring.xml或log4j2.xml文件，我们在resources目录下配置一个简单的log4j2-spring.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
    <Appenders>
        <Console name="ConsoleAppender" target="SYSTEM_OUT">
            <PatternLayout
                    pattern="%style{%date{DEFAULT}}{yellow} %highlight{%-5level}{FATAL=bg_red, ERROR=red, WARN=yellow, INFO=green} %message"/>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="ConsoleAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

该配置具有一个控制台Appender，用于将日志记录数据显示到控制台。

### 2.3 Syslog Appender

Appender是日志记录框架中的主要组件，可将日志记录数据传送到目的地，Log4j2支持许多Appender，例如Syslog Appender。让我们更新我们的log4j2-spring.xml文件以添加用于将日志记录数据发送到Syslog服务器的Syslog Appender：

```xml
<Syslog name="Syslog" format="RFC5424" host="localhost" port="514" 
        protocol="UDP" appName="tuyucheng" facility="LOCAL0" />
```

Syslog Appender具有很多属性：

-   name：appender的名称
-   format：可以设置为BSD或RFC5424
-   host：Syslog服务器的地址
-   port：Syslog服务器的端口
-   protocol：是否使用TCP或UPD
-   appName：正在记录的应用程序的名称
-   facility：消息的类别

## 3. Syslog服务器

现在，让我们设置Syslog服务器。在许多Linux发行版中，[rsyslog](https://www.rsyslog.com/)是主要的日志记录机制，它包含在大多数Linux发行版中，例如Ubuntu和CentOS。

### 3.1 Syslog配置

**我们的rsyslog配置应该与Log4j2设置相匹配**，rsyslog配置在/etc/rsyslog.conf文件中定义，我们在Log4j2配置中分别使用UDP和端口514作为协议和端口。因此，我们将在rsyslog.conf文件中添加或取消注解以下行：

```conf
# provides UDP syslog reception
module(load="imudp")
input(type="imudp" port="514")
```

在这种情况下，我们设置module(load=”imudp”)以加载imudp模块以通过UDP接收Syslog消息。然后，我们使用input配置将端口设置为514，input将端口分配给模块。之后，我们应该重启rsyslog服务器：

```shell
sudo service rsyslog restart
```

现在配置已准备就绪。

### 3.2 测试

让我们创建一个简单的Spring Boot应用程序来记录一些消息：

```java
@SpringBootApplication
public class SpringBootSyslogApplication {

    private static final Logger logger = LogManager.getLogger(SpringBootSyslogApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSyslogApplication.class, args);

        logger.debug("Debug log message");
        logger.info("Info log message");
        logger.error("Error log message");
        logger.warn("Warn log message");
        logger.fatal("Fatal log message");
        logger.trace("Trace log message");
    }
}
```

日志记录信息存储在/var/log/目录中，让我们检查以下syslog文件：

```shell
tail -f /var/log/syslog
```

当运行我们的Spring Boot应用程序时，我们应该能够看到我们的日志消息：

```shell
Jun 30 18:43:35 tuyucheng[16841] Info log message
Jun 30 18:43:35 tuyucheng[16841] Error log message
Jun 30 18:43:35 tuyucheng[16841] Warn log message
Jun 30 18:43:35 tuyucheng[16841] Fatal log message
```

## 4. 总结

在本教程中，我们描述了Spring Boot应用程序中Log4j2中的Syslog配置，此外，我们将rsyslog服务器配置为Syslog服务器。