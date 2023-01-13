## 1. 简介

如今，大多数Java日志库都为格式化日志提供了不同的布局选项——以准确地满足每个项目的需求。

在这篇快速文章中，我们希望将日志条目格式化并输出为 JSON。我们将看到如何为两个最广泛使用的日志记录库执行此操作：Log4j2和Logback。

两者都在内部使用Jackson来表示 JSON 格式的日志。

有关这些库的介绍，请查看我们[的Java日志记录介绍一文](https://www.baeldung.com/java-logging-intro)。

## 2.Log4j2

[Log4j2](https://www.baeldung.com/log4j2-appenders-layouts-filters)是最流行的Java日志记录库 Log4J 的直接继承者。

由于它是Java项目的新标准，我们将展示如何配置它以输出 JSON。

### 2.1. 依赖关系

首先，我们必须在我们的pom.xml中包含以下依赖项。xml文件：

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>2.10.0</version>
    </dependency>

    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.13.0</version>
    </dependency>
    
</dependencies>
```

可以在 Maven Central 上找到之前依赖项的最新版本：[log4j-api](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-api")、[log4j-core](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-core")、[jackson-databind。](https://search.maven.org/classic/#search|gav|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")

### 2.2. 配置

然后，在我们的log4j2. 在 xml 文件中，我们可以创建一个使用JsonLayout的新Appender和一个使用此Appender的新Logger：

```xml
<Appenders>
    <Console name="ConsoleJSONAppender" target="SYSTEM_OUT">
        <JsonLayout complete="false" compact="false">
            <KeyValuePair key="myCustomField" value="myCustomValue" />
        </JsonLayout>
    </Console>
</Appenders>

<Logger name="CONSOLE_JSON_APPENDER" level="TRACE" additivity="false">
    <AppenderRef ref="ConsoleJSONAppender" />
</Logger>
```

正如我们在示例配置中看到的那样，可以使用KeyValuePair将我们自己的值添加到日志中，它甚至支持查看日志上下文。

将compact参数设置为false会增加输出的大小，但也会使其更易于阅读。

### 2.3. 使用 Log4j2

在我们的代码中，我们现在可以实例化我们的新 JSON 记录器并进行新的调试级别跟踪：

```java
Logger logger = LogManager.getLogger("CONSOLE_JSON_APPENDER");
logger.debug("Debug message");

```

先前代码的调试输出消息将是：

```javascript
{
  "timeMillis" : 1513290111664,
  "thread" : "main",
  "level" : "DEBUG",
  "loggerName" : "CONSOLE_JSON_APPENDER",
  "message" : "My debug message",
  "endOfBatch" : false,
  "loggerFqcn" : "org.apache.logging.log4j.spi.AbstractLogger",
  "threadId" : 1,
  "threadPriority" : 5,
  "myCustomField" : "myCustomValue"
}
```

## 3. 登录

[Logback](https://www.baeldung.com/custom-logback-appender)可以被认为是 Log4J 的另一个继承者。它由相同的开发人员编写，并声称比其前身更高效、更快速。

那么，让我们看看如何配置它以获取 JSON 格式的日志输出。

### 3.1. 依赖关系

让我们在pom.xml中包含以下依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.6</version>
    </dependency>

    <dependency>
        <groupId>ch.qos.logback.contrib</groupId>
        <artifactId>logback-json-classic</artifactId>
        <version>0.1.5</version>
    </dependency>

    <dependency>
        <groupId>ch.qos.logback.contrib</groupId>
        <artifactId>logback-jackson</artifactId>
        <version>0.1.5</version>
    </dependency>

    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.9.3</version>
    </dependency>
</dependencies>
```

我们可以在这里检查这些依赖项的最新版本：[logback-classic](https://search.maven.org/classic/#search|ga|1|g%3A"ch.qos.logback" AND a%3A"logback-classic")、[logback-json-classic](https://search.maven.org/classic/#search|ga|1|g%3A"ch.qos.logback.contrib" AND a%3A"logback-json-classic")、[logback-jackson](https://search.maven.org/classic/#search|ga|1|g%3A"ch.qos.logback.contrib" AND a%3A"logback-jackson")、[jackson-databind](https://search.maven.org/classic/#search|ga|1|g%3A"com.fasterxml.jackson.core" AND a%3A"jackson-databind")

### 3.2. 配置

首先，我们在使用JsonLayout和JacksonJsonFormatter 的logback.xml中创建一个新的附加程序。

之后，我们可以创建一个使用此appender的新记录器：

```xml
<appender name="json" class="ch.qos.logback.core.ConsoleAppender">
    <layout class="ch.qos.logback.contrib.json.classic.JsonLayout">
        <jsonFormatter
            class="ch.qos.logback.contrib.jackson.JacksonJsonFormatter">
            <prettyPrint>true</prettyPrint>
        </jsonFormatter>
        <timestampFormat>yyyy-MM-dd' 'HH:mm:ss.SSS</timestampFormat>
    </layout>
</appender>

<logger name="jsonLogger" level="TRACE">
    <appender-ref ref="json" />
</logger>
```

如我们所见，启用了参数prettyPrint以获得人类可读的 JSON。

### 3.3. 使用登录

让我们在代码中实例化记录器并记录调试消息：

```java
Logger logger = LoggerFactory.getLogger("jsonLogger");
logger.debug("Debug message");

```

有了这个 - 我们将获得以下输出：

```javascript
{
  "timestamp" : "2017-12-14 23:36:22.305",
  "level" : "DEBUG",
  "thread" : "main",
  "logger" : "jsonLogger",
  "message" : "Debug log message",
  "context" : "default"
}
```

## 4. 总结

我们在这里看到了如何轻松配置 Log4j2 和 Logback 具有 JSON 输出格式。我们已将解析的所有复杂性委托给日志库，因此我们不需要更改任何现有的记录器调用。