## 1. 概述

通常，控制台日志使我们有机会以简单直观的方式调试我们的系统。然而，有时我们不想在我们的系统中启用此功能。

在本快速教程中，我们将了解**如何在运行Spring Boot应用程序时避免记录日志到控制台**。

无论我们使用的是Logback、Log4j2，还是Java Util Logging框架，我们都通过直截了当的示例演示如何实现这一点，以保持简单。

## 2. 如何禁用Logback的控制台输出

如果我们的项目使用[Spring Boot Starters]()，那么spring-boot-starter-logging依赖项也将包含在内。

这个特定的启动器将[Logback]()配置为默认框架，并且默认情况下最初只记录到控制台。

**可以通过将logback-spring.xml文件添加到我们的resources目录来自定义此配置**。

例如，让我们设置XML以禁用控制台输出并仅记录到一个文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/file-appender.xml"/>
    <root level="INFO">
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

此外，我们需要application.properties文件中的logging.file配置属性：

```properties
logging.file=tuyucheng-disabled-console.log
```

注意：这里实际上禁用控制台输出的原因是我们没有在XML文件中包含console-appender.xml，因此一个空的<configuration\>标签也可以达到目的。

**或者，我们可以通过使用应用程序属性覆盖默认配置来避免创建XML文件**。

例如，我们可以潜在地使用logging.pattern.console属性：

```properties
logging.pattern.console=
```

此属性被转换为CONSOLE_LOG_PATTERN系统属性，然后由Spring默认控制台配置使用。

**当然，这种方法并不像前一种方法那样干净和可靠**。这不是该属性的预期目的，因此Logback在某些时候可能不支持此“hack”。

此外，我们可以通过将根记录器级别的值设置为OFF来禁用所有日志记录活动：

```properties
logging.level.root=OFF
```

## 3. 如何禁用Log4j2的控制台输出

正如我们所知，[Log4j2]()支持XML、JSON、YAML或属性格式来配置其日志记录行为。

为了简单起见，这次我们只演示一个简单的log4j2.xml文件示例。

其他格式遵循相同的配置结构：

```xml

<Configuration status="INFO">
    <Appenders>
        <File name="MyFile" fileName="tuyucheng.log"
              immediateFlush="true" append="false">
            <PatternLayout pattern="%d{yyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="MyFile"/>
        </Root>
    </Loggers>
</Configuration>
```

与Logback设置一样，框架避免记录到控制台的原因不是配置“本身”，而是Root Logger不包含对Console Appender的引用这一事实。

## 4. 如何为Java Util Logging禁用控制台日志记录

Java Util Logging(或简称“JUL”)可能不是当今Spring Boot应用程序最流行的日志记录解决方案。

无论如何，我们将分析如何摆脱控制台日志，以防框架存在于我们的项目中。

我们所要做的就是将以下值添加到resources文件夹中的默认logging.properties：

```properties
handlers=java.util.logging.FileHandler
java.util.logging.FileHandler.pattern=tuyucheng.log
java.util.logging.FileHandler.formatter=java.util.logging.SimpleFormatter
```

并在我们的application.properties文件中包含logging.file属性，任何值都可以解决问题：

```properties
logging.file=true
```

## 5. 总结

通过这些简短的示例，无论我们使用什么日志记录框架，我们现在都可以轻松地禁用应用程序中的控制台日志。