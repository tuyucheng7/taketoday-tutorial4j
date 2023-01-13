## 1. 概述

日志记录是理解和调试程序运行时行为的有力帮助。日志捕获并保留重要数据，并使其在任何时间点都可用于分析。

本文讨论了最流行的Java日志框架、Log4j 2 和 Logback，以及它们的前身 Log4j，并简要介绍了 SLF4J，这是一个为不同日志框架提供通用接口的日志外观。

## 2.启用日志记录

本文中讨论的所有日志记录框架都共享记录器、附加程序和布局的概念。在项目内部启用日志记录遵循三个常见步骤：

1.  添加需要的库
2.  配置
3.  放置日志语句

接下来的部分分别讨论每个框架的步骤。

## 3.Log4j 2

Log4j 2 是 Log4j 日志记录框架的新改进版本。最引人注目的改进是异步日志记录的可能性。Log4j 2 需要以下库：

```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>2.6.1</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.6.1</version>
</dependency>
```

你可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-api")找到最新版本的log4j-api和log4j-core –[此处](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-core")。

### 3.1. 配置

配置Log4j 2主要是配置log4j2.xml文件。首先要配置的是appender。

这些决定了日志消息将被路由到哪里。目的地可以是控制台、文件、套接字等。

Log4j 2 有许多用于不同目的的 appender，你可以在[Log4j 2](https://logging.apache.org/log4j/2.x/manual/appenders.html)官方网站上找到更多信息。

让我们看一个简单的配置示例：

```xml
<Configuration status="debug" name="baeldung" packages="">
    <Appenders>
        <Console name="stdout" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %p %m%n"/>
        </Console>
    </Appenders>
</Configuration>
```

你可以为每个附加程序设置一个名称，例如使用名称console而不是stdout。

注意PatternLayout元素——它决定了消息的外观。在我们的示例中，模式是基于模式参数设置的，其中%d确定日期模式，%p - 日志级别的输出，%m - 记录消息的输出，%n - 添加新行符号。有关模式的更多信息，你可以在官方[Log4j 2](https://logging.apache.org/log4j/2.x/manual/layouts.html)页面上找到。

最后——要启用一个(或多个)appender，你需要将其添加到<Root>部分：

```xml
<Root level="error">
    <AppenderRef ref="STDOUT"/>
</Root>
```

### 3.2. 记录到文件

有时你需要将日志记录到文件中，因此我们将fout记录器添加到我们的配置中：

```xml
<Appenders>
    <File name="fout" fileName="baeldung.log" append="true">
        <PatternLayout>
            <Pattern>%d{yyyy-MM-dd HH:mm:ss} %-5p %m%nw</Pattern>
        </PatternLayout>
    </File>
</Appenders>
```

File appender 有几个可以配置的参数：

-   file – 确定日志文件的文件名
-   append – 此参数的默认值为 true，这意味着默认情况下File appender 将附加到现有文件而不是截断它。
-   前面示例中描述的PatternLayout 。

为了启用文件附加器，你需要将其添加到<Root>部分：

```xml
<Root level="INFO">
    <AppenderRef ref="stdout" />
    <AppenderRef ref="fout"/>
</Root>

```

### 3.3. 异步日志

如果你想让你的 Log4j 2 异步，你需要将 LMAX disruptor 库添加到你的pom.xml。LMAX disruptor 是一个无锁的线程间通信库。

将破坏者添加到 pom.xml：

```xml
<dependency>
    <groupId>com.lmax</groupId>
    <artifactId>disruptor</artifactId>
    <version>3.3.4</version>
</dependency>
```

可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"com.lmax" AND a%3A"disruptor")找到最新版本的 disruptor 。

如果你想使用 LMAX disruptor，你需要在你的配置中使用<asyncRoot>而不是<Root>。

```xml
<AsyncRoot level="DEBUG">
    <AppenderRef ref="stdout" />
    <AppenderRef ref="fout"/>
</AsyncRoot>
```

或者，你可以通过将系统属性Log4jContextSelector设置为org.apache.logging.log4j.core.async.AsyncLoggerContextSelector来启用异步日志记录。

你当然可以阅读有关 Log4j2 异步记录器配置的更多信息，并在[Log4j2 官方页面](https://logging.apache.org/log4j/2.x/manual/async.html)上查看一些性能图表。

### 3.4. 用法

下面是一个简单的例子，演示了使用 Log4j 进行日志记录：

```java
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Log4jExample {

    private static Logger logger = LogManager.getLogger(Log4jExample.class);

    public static void main(String[] args) {
        logger.debug("Debug log message");
        logger.info("Info log message");
        logger.error("Error log message");
    }
}
```

运行后，应用程序会将以下消息记录到控制台和名为baeldung.log 的文件中：

```plaintext
2016-06-16 17:02:13 INFO  Info log message
2016-06-16 17:02:13 ERROR Error log message
```

如果将根日志级别提升到ERROR：

```xml
<level value="ERROR" />
```

输出将如下所示：

```plaintext
2016-06-16 17:02:13 ERROR Error log message
```

如你所见，将日志级别更改为 upper 参数会导致具有较低日志级别的消息不会打印到 appenders。

方法logger.error也可用于记录发生的异常：

```java
try {
    // Here some exception can be thrown
} catch (Exception e) {
    logger.error("Error log message", throwable);
}
```

### 3.5. 包级配置

假设你需要显示日志级别为 TRACE 的消息——例如来自com.baeldung.log4j2等特定包的消息：

```java
logger.trace("Trace log message");
```

对于你希望继续仅记录 INFO 消息的所有其他包。

请记住，TRACE 低于我们在配置中指定的根日志级别 INFO。

要仅为其中一个包启用日志记录，你需要在<Root>之前添加以下部分到你的log4j2.xml：

```xml
<Logger name="com.baeldung.log4j2" level="debug">
    <AppenderRef ref="stdout"/>
</Logger>
```

它将为com.baeldung.log4j包启用日志记录，你的输出将如下所示：

```plaintext
2016-06-16 17:02:13 TRACE Trace log message
2016-06-16 17:02:13 DEBUG Debug log message
2016-06-16 17:02:13 INFO  Info log message
2016-06-16 17:02:13 ERROR Error log message
```

## 4. 登录

Logback 是 Log4j 的改进版本，由制作 Log4j 的同一位开发人员开发。

与 Log4j 相比，Logback 还具有更多功能，其中许多功能也被引入到 Log4j 2 中。[下面是官方网站](http://logback.qos.ch/reasonsToSwitch.html)上 Logback 的所有优点的快速浏览。

让我们首先将以下依赖项添加到pom.xml：

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.6</version>
</dependency>
```

此依赖项将传递引入另外两个依赖项，logback-core和slf4j-api。请注意，可以在[此处](https://search.maven.org/classic/#search|gav|1|g%3A"ch.qos.logback" AND a%3A"logback-classic")找到最新版本的 Logback 。

### 4.1. 配置

现在让我们看一个 Logback 配置示例：

```xml
<configuration>
  # Console appender
  <appender name="stdout" class="ch.qos.logback.core.ConsoleAppender">
    <layout class="ch.qos.logback.classic.PatternLayout">
      # Pattern of log message for console appender
      <Pattern>%d{yyyy-MM-dd HH:mm:ss} %-5p %m%n</Pattern>
    </layout>
  </appender>

  # File appender
  <appender name="fout" class="ch.qos.logback.core.FileAppender">
    <file>baeldung.log</file>
    <append>false</append>
    <encoder>
      # Pattern of log message for file appender
      <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5p %m%n</pattern>
    </encoder>
  </appender>

  # Override log level for specified package
  <logger name="com.baeldung.log4j" level="TRACE"/>

  <root level="INFO">
    <appender-ref ref="stdout" />
    <appender-ref ref="fout" />
  </root>
</configuration>
```

Logback使用SLF4J作为接口，所以需要导入SLF4J的Logger和LoggerFactory。

### 4.2. SLF4J

SLF4J 为大多数Java日志记录框架提供了通用接口和抽象。它充当外观并提供标准化 API 以访问日志框架的底层功能。

Logback 使用 SLF4J 作为其功能的本机 API。以下是使用 Logback 日志记录的示例：

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4jExample {

    private static Logger logger = LoggerFactory.getLogger(Log4jExample.class);

    public static void main(String[] args) {
        logger.debug("Debug log message");
        logger.info("Info log message");
        logger.error("Error log message");
    }
}
```

输出将与前面的示例保持相同。

## 5.Log4J

最后，让我们看一下古老的 Log4j 日志记录框架。

在这一点上它当然已经过时了，但值得讨论，因为它为其更现代的继任者奠定了基础。

许多配置细节与 Log4j 2 部分中讨论的相匹配。

### 5.1. 配置

首先，你需要将 Log4j 库添加到你的项目pom.xml 中：

```xml
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

[在这里](https://search.maven.org/classic/#search|gav|1|g%3A"log4j" AND a%3A"log4j")你应该能够找到最新版本的 Log4j。

让我们看一下只有一个控制台附加程序的简单 Log4j 配置的完整示例：

```xml
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd" >
<log4j:configuration debug="false">

    <!--Console appender-->
    <appender name="stdout" class="org.apache.log4j.ConsoleAppender">
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" 
              value="%d{yyyy-MM-dd HH:mm:ss} %p %m%n" />
        </layout>
    </appender>

    <root>
        <level value="INFO" />
        <appender-ref ref="stdout" />
    </root>

</log4j:configuration>
```

<log4j:configuration debug=”false”>是整个配置的开放标签，它有一个属性——调试。它确定是否要将 Log4j 调试信息添加到日志中。

### 5.2. 用法

添加 Log4j 库和配置后，你可以在代码中使用记录器。让我们看一个简单的例子：

```java
import org.apache.log4j.Logger;

public class Log4jExample {
    private static Logger logger = Logger.getLogger(Log4jExample.class);

    public static void main(String[] args) {
        logger.debug("Debug log message");
        logger.info("Info log message");
        logger.error("Error log message");
    }
}
```

## 六. 总结

本文展示了如何使用不同日志记录框架(如 Log4j、Log4j2 和 Logback)的非常简单的示例。它涵盖了所有提到的框架的简单配置示例。