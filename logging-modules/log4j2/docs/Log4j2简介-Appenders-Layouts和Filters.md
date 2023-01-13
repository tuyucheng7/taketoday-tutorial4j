## 1. 概述

记录事件是软件开发的一个重要方面。虽然Java生态系统中有许多可用的框架，但 Log4J 几十年来一直是最受欢迎的，因为它提供了灵活性和简单性。

[Log4j 2](https://www.baeldung.com/java-logging-intro)是经典 Log4j 框架的新改进版本。

在本文中，我们将通过实际示例介绍最常见的附加程序、布局和过滤器。

在 Log4J2 中，appender 只是日志事件的目的地；它可以像控制台一样简单，也可以像任何 RDBMS 一样复杂。布局决定了日志的呈现方式，过滤器根据各种标准过滤数据。

## 2.设置

为了理解几个日志记录组件及其配置，让我们设置不同的测试用例，每个用例都包含一个log4J2.xml配置文件和一个JUnit 4测试类。

所有示例共有两个 Maven 依赖项：

```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.7</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.7</version>
    <type>test-jar</type>
    <scope>test</scope>
</dependency>
```

除了主要的[log4j-core](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-core")包之外，我们还需要包含属于该包的“test jar”，以访问测试不常见命名的配置文件所需的上下文规则。

## 3.默认配置

ConsoleAppender是Log4J 2核心包的默认配置。它以一种简单的模式将消息记录到系统控制台：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="ConsoleAppender" target="SYSTEM_OUT">
            <PatternLayout 
              pattern="%d [%t] %-5level %logger{36} - %msg%n%throwable"/>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="ERROR">
            <AppenderRef ref="ConsoleAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

让我们分析一下这个简单的 XML 配置中的标签：

-   配置：Log4J 2配置文件和属性状态的根元素是我们要记录的内部 Log4J 事件的级别
-   Appenders ：此元素包含一个或多个附加程序。在这里，我们将配置一个 appender，它以标准输出输出到系统控制台
-   Loggers ：此元素可以包含多个已配置的Logger元素。使用特殊的Root标记，你可以配置一个无名的标准记录器，它将接收来自应用程序的所有日志消息。每个记录器都可以设置为最低日志级别
-   AppenderRef ：此元素定义对Appenders部分中元素的引用。因此，属性“ ref ”与附加程序的“ name ”属性相关联

相应的单元测试同样简单。我们将获得一个Logger引用并打印两条消息：

```java
@Test
public void givenLoggerWithDefaultConfig_whenLogToConsole_thanOK()
  throws Exception {
    Logger logger = LogManager.getLogger(getClass());
    Exception e = new RuntimeException("This is only a test!");

    logger.info("This is a simple message at INFO level. " +
      "It will be hidden.");
    logger.error("This is a simple message at ERROR level. " +
    "This is the minimum visible level.", e);
}

```

## 4. ConsoleAppender与PatternLayout

让我们在单独的 XML 文件中定义一个带有自定义颜色模式的新控制台附加程序，并将其包含在我们的主要配置中：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Console name="ConsoleAppender" target="SYSTEM_OUT">
    <PatternLayout pattern="%style{%date{DEFAULT}}{yellow}
      %highlight{%-5level}{FATAL=bg_red, ERROR=red, WARN=yellow, INFO=green} 
      %message"/>
</Console>
```

该文件使用了一些在运行时被Log4J 2替换的模式变量：

-   %style{…}{colorname} ：这将以给定颜色 ( colorname ) 打印第一对括号 ( … ) 中的文本。
-   %highlight{…}{FATAL=colorname, …} ：这类似于“style”变量。但是可以为每个日志级别指定不同的颜色。
-   %date{format} ：这将被指定格式的当前日期替换。这里我们使用“默认”日期时间格式，“ yyyy -MM-dd HH:mm:ss,SSS”。
-   %-5level ：以右对齐方式打印日志消息的级别。
-   %message :代表原始日志信息

但是PatternLayout中存在更多的变量和格式。你可以参考[Log4J 2](https://logging.apache.org/log4j/2.x/)的官方文档。

现在我们将定义的控制台附加程序包含到我们的主要配置中：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" xmlns:xi="http://www.w3.org/2001/XInclude">
    <Appenders>
        <xi:include href="log4j2-includes/
          console-appender_pattern-layout_colored.xml"/>
    </Appenders>
    <Loggers>
        <Root level="DEBUG">
            <AppenderRef ref="ConsoleAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

单元测试：

```java
@Test
public void givenLoggerWithConsoleConfig_whenLogToConsoleInColors_thanOK() 
  throws Exception {
    Logger logger = LogManager.getLogger("CONSOLE_PATTERN_APPENDER_MARKER");
    logger.trace("This is a colored message at TRACE level.");
    ...
}

```

## 5. 使用JSONLayout和BurstFilter 的异步文件附加器

有时以异步方式编写日志消息很有用。例如，如果应用程序性能优先于日志的可用性。

在这种用例中，我们可以使用AsyncAppender。

对于我们的示例，我们正在配置一个异步JSON日志文件。此外，我们将包括一个突发过滤器，它以指定的速率限制日志输出：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        ...
        <File name="JSONLogfileAppender" fileName="target/logfile.json">
            <JSONLayout compact="true" eventEol="true"/>
            <BurstFilter level="INFO" rate="2" maxBurst="10"/>
        </File>
        <Async name="AsyncAppender" bufferSize="80">
            <AppenderRef ref="JSONLogfileAppender"/>
        </Async>
    </Appenders>
    <Loggers>
        ...
        <Logger name="ASYNC_JSON_FILE_APPENDER" level="INFO"
          additivity="false">
            <AppenderRef ref="AsyncAppender" />
        </Logger>
        <Root level="INFO">
            <AppenderRef ref="ConsoleAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

请注意：

-   JSONLayout的配置方式是每行写入一个日志事件
-   BurstFilter将丢弃每个具有“INFO”级别和更高级别的事件(如果有两个以上)，但最多丢弃 10 个事件
-   AsyncAppender设置为 80 条日志消息的缓冲区；之后，缓冲区被刷新到日志文件

我们来看看相应的单元测试。我们在循环中填充附加缓冲区，让它写入磁盘并检查日志文件的行数：

```java
@Test
public void givenLoggerWithAsyncConfig_whenLogToJsonFile_thanOK() 
  throws Exception {
    Logger logger = LogManager.getLogger("ASYNC_JSON_FILE_APPENDER");

    final int count = 88;
    for (int i = 0; i < count; i++) {
        logger.info("This is async JSON message #{} at INFO level.", count);
    }
    
    long logEventsCount 
      = Files.lines(Paths.get("target/logfile.json")).count();
    assertTrue(logEventsCount > 0 && logEventsCount <= count);
}
```

## 6. RollingFile Appender 和XMLLayout

接下来，我们将创建一个滚动日志文件。配置文件大小后，日志文件将被压缩和轮换。

这次我们使用XML布局：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <RollingFile name="XMLRollingfileAppender"
          fileName="target/logfile.xml"
          filePattern="target/logfile-%d{yyyy-MM-dd}-%i.log.gz">
            <XMLLayout/>
            <Policies>
                <SizeBasedTriggeringPolicy size="17 kB"/>
            </Policies>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Logger name="XML_ROLLING_FILE_APPENDER" 
       level="INFO" additivity="false">
            <AppenderRef ref="XMLRollingfileAppender" />
        </Logger>
        <Root level="TRACE">
            <AppenderRef ref="ConsoleAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

请注意：

-   RollingFile appender 有一个 ' filePattern ' 属性，用于命名轮换的日志文件，并且可以使用占位符变量进行配置。在我们的例子中，它应该在文件后缀之前包含一个日期和一个计数器。
-   XMLLayout的默认配置将写入没有根元素的单个日志事件对象。
-   我们正在使用基于大小的策略来轮换我们的日志文件。

我们的单元测试类将类似于上一节中的类：

```java
@Test
public void givenLoggerWithRollingFileConfig_whenLogToXMLFile_thanOK()
  throws Exception {
    Logger logger = LogManager.getLogger("XML_ROLLING_FILE_APPENDER");
    final int count = 88;
    for (int i = 0; i < count; i++) {
        logger.info(
          "This is rolling file XML message #{} at INFO level.", i);
    }
}
```

## 7.系统日志附加程序

假设我们需要通过网络将记录的事件发送到远程机器。使用 Log4J2 最简单的方法是使用它的Syslog Appender：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        ...
        <Syslog name="Syslog" 
          format="RFC5424" host="localhost" port="514" 
          protocol="TCP" facility="local3" connectTimeoutMillis="10000" 
          reconnectionDelayMillis="5000">
        </Syslog>
    </Appenders>
    <Loggers>
        ...
        <Logger name="FAIL_OVER_SYSLOG_APPENDER" 
          level="INFO" 
          additivity="false">
            <AppenderRef ref="FailoverAppender" />
        </Logger>
        <Root level="TRACE">
            <AppenderRef ref="Syslog" />
        </Root>
    </Loggers>
</Configuration>
```

Syslog标签中的属性：

-   name ：定义附加程序的名称，并且必须是唯一的。由于我们可以为同一个应用程序和配置设置多个 Syslog appender
-   format ：它可以设置为 BSD 或 RFC5424，系统日志记录将相应地格式化
-   host & port ：远程 Syslog 服务器机器的主机名和端口
-   protocol :是否使用 TCP 或 UPD
-   facility ：事件将写入哪个 Syslog 设施
-   connectTimeoutMillis :等待建立连接的时间，默认为零
-   reconnectionDelayMillis ：重新尝试连接之前等待的时间

## 8.故障转移器

现在可能会有一个 appender 无法处理日志事件的情况，我们不想丢失数据。在这种情况下，FailoverAppender就派上用场了。

例如，如果Syslog appender 无法将事件发送到远程机器，我们可能会暂时回退到 FileAppender ，而不是丢失该数据。

FailoverAppender采用主附加程序和辅助附加程序的数量。万一主要失败，它会尝试按顺序使用次要事件处理日志事件，直到一个成功或没有任何次要尝试：

```xml
<Failover name="FailoverAppender" primary="Syslog">
    <Failovers>
        <AppenderRef ref="ConsoleAppender" />
    </Failovers>
</Failover>
```

让我们测试一下：

```java
@Test
public void givenLoggerWithFailoverConfig_whenLog_thanOK()
  throws Exception {
    Logger logger = LogManager.getLogger("FAIL_OVER_SYSLOG_APPENDER");
    Exception e = new RuntimeException("This is only a test!"); 

    logger.trace("This is a syslog message at TRACE level.");
    logger.debug("This is a syslog message at DEBUG level.");
    logger.info("This is a syslog message at INFO level. 
      This is the minimum visible level.");
    logger.warn("This is a syslog message at WARN level.");
    logger.error("This is a syslog message at ERROR level.", e);
    logger.fatal("This is a syslog message at FATAL level.");
}
```

## 9.JDBC 附加程序

JDBC 附加程序使用标准 JDBC 将日志事件发送到 RDBMS。可以使用任何 JNDI 数据源或任何连接工厂来获取连接。

基本配置由DataSource或ConnectionFactory、ColumnConfigs 和tableName 组成：

```java
<JDBC name="JDBCAppender" tableName="logs">
    <ConnectionFactory 
      class="com.baeldung.logging.log4j2.tests.jdbc.ConnectionFactory" 
      method="getConnection" />
    <Column name="when" isEventTimestamp="true" />
    <Column name="logger" pattern="%logger" />
    <Column name="level" pattern="%level" />
    <Column name="message" pattern="%message" />
    <Column name="throwable" pattern="%ex{full}" />
</JDBC>
```

现在让我们试试：

```java
@Test
public void givenLoggerWithJdbcConfig_whenLogToDataSource_thanOK()
  throws Exception {
    Logger logger = LogManager.getLogger("JDBC_APPENDER");
    final int count = 88;
    for (int i = 0; i < count; i++) {
        logger.info("This is JDBC message #{} at INFO level.", count);
    }

    Connection connection = ConnectionFactory.getConnection();
    ResultSet resultSet = connection.createStatement()
      .executeQuery("SELECT COUNT() AS ROW_COUNT FROM logs");
    int logCount = 0;
    if (resultSet.next()) {
        logCount = resultSet.getInt("ROW_COUNT");
    }
    assertTrue(logCount == count);
}
```

## 10.总结

本文展示了如何将不同的日志记录附加程序、过滤器和布局与 Log4J2 一起使用以及配置它们的方法的非常简单的示例。