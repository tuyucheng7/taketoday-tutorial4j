## 1. 概述

在本教程中，我们将探讨如何使用[Apache Log4j2 库](https://www.baeldung.com/java-logging-intro#Log4j)将消息记录到文件和控制台。

这在非生产环境中非常有用，我们可能希望在控制台中看到调试消息，并且我们可能希望将更高级别的日志保存到文件中以供以后分析。

## 2.项目设置

让我们从创建一个Java项目开始。我们将添加 log4j2 依赖项并了解如何配置和使用记录器。

### 2.1. Log4j2 依赖

让我们将 log4j2 依赖项添加到我们的项目中。我们需要[Apache Log4J Core](https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core)和[Apache Log4J API](https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api)依赖项：

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>2.18.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
        <version>2.18.0</version>
    </dependency>
</dependencies>
```

### 2.2. 应用类

现在让我们使用 log4j2 库向我们的应用程序添加一些日志记录：

```java
public class Log4j2ConsoleAndFile {

    private static final Logger logger = LogManager.getLogger(Log4j2ConsoleAndFile.class);

    public static void main(String[] args) {
        logger.info("Hello World!");
        logger.debug("Hello World!");
    }
}
```

## 3.Log4j2配置

要自动配置记录器，我们需要在类路径上有一个配置文件。它可以是 JSON、XML、YAML 或属性格式。该文件应命名为 log4j2。 对于我们的示例，让我们使用名为log4j2.properties的配置文件。

### 3.1. 登录到控制台

要记录到任何目的地，我们首先需要定义一个记录到控制台的[附加程序。](https://www.baeldung.com/log4j2-appenders-layouts-filters)让我们看看执行此操作的配置：

```properties
appender.console.type = Console
appender.console.name = STDOUT
appender.console.layout.type = PatternLayout
appender.console.layout.pattern = [%-5level] %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %c{1} - %msg%n
```

让我们了解配置的每个组件：

-   appender.console.type – 在这里，我们指定将用于记录的附加程序的类型。 Console 类型 指定 appender 将只写入控制台。需要注意的是，键名中的console只是约定俗成的，并不是强制的。
-   appender.console.name—— 我们可以给出任何唯一的名称，我们以后可以使用它来引用这个 appender。
-   appender.console.layout.type – 这决定了用于格式化日志消息的布局类的名称。
-   appender.console.layout.pattern – 这是 将用于格式化日志消息的模式。

要启用控制台记录器，我们需要将控制台附加程序添加到根记录器。我们可以使用上面指定的名称来做到这一点：![img]()

```properties
rootLogger=debug, STDOUT
```

使用此配置，我们会将所有调试及以上消息记录到控制台。对于在本地环境中运行的控制台，调试级别的日志记录很常见。

### 3.2. 记录到文件

同样，我们可以将记录器配置为记录到文件中。这对于持久化日志通常很有用。让我们定义一个文件附加程序：

```properties
appender.file.type = File
appender.file.name = LOGFILE
appender.file.fileName=logs/log4j.log
appender.file.layout.type=PatternLayout
appender.file.layout.pattern=[%-5level] %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %c{1} - %msg%n
appender.file.filter.threshold.type = ThresholdFilter
appender.file.filter.threshold.level = info
```

在文件附加器的情况下，还必须指定文件名。

除此之外，我们还要设置阈值级别。由于我们正在记录到一个文件，我们不想记录所有消息，因为它会占用大量持久存储空间。我们只想记录信息级别或更高级别的消息。我们可以使用过滤器ThresholdFilter 并设置其级别信息来执行此操作。 

要启用文件记录器，我们需要将文件附加程序添加到根记录器。我们需要更改 rootLogger 配置以包含文件附加程序：![img]()

```properties
rootLogger=debug, STDOUT, LOGFILE
```

即使我们在根级别使用了调试级别，文件记录器也只会记录信息和以上消息。

## 4.测试

现在让我们运行应用程序并检查控制台中的输出：

```bash
12:43:47,891 INFO  Application:8 - Hello World!
12:43:47,892 DEBUG Application:9 - Hello World!
```

正如预期的那样，我们可以在控制台中看到两条日志消息。如果我们检查路径logs/log4j.log中的日志文件，我们只能看到信息级别的日志消息：

```bash
12:43:47,891 INFO  Application:8 - Hello World!
```

## 5.总结

在本文中，我们学习了如何将消息记录到控制台和文件中。我们创建了一个Java项目，使用属性文件配置了 Log4j2，并测试了消息是否同时打印到控制台和文件。