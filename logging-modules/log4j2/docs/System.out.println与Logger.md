## 1. 为什么是记录器？

在编写程序或开发企业生产应用程序时，使用System.out.println似乎是最简单易行的选择。没有要添加到类路径的额外库，也没有要进行的额外配置。

但是使用System.out.println有几个缺点会影响它在许多情况下的可用性。在本教程中，我们将讨论为什么以及何时要在普通的旧System.out和System.err上使用 Logger 。我们还将展示一些使用 Log4J2 日志记录框架的快速示例。

## 2.设置

在我们开始之前，让我们看看所需的 Maven 依赖项和配置。

### 2.1. Maven 依赖项

让我们从将 Log4J2 依赖项添加到我们的 pom.xml开始：

```java
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>2.12.1</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.12.1</version>
</dependency>
```

我们可以在 Maven Central 上找到最新版本的[log4j-api](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-api")和 [log4j-core 。](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-core")

### 2.2. Log4J2 配置

System.out的使用不需要任何额外的配置。但是，要使用 Log4J2，我们需要一个log4j.xml配置文件：

```xml
<Configuration status="debug" name="baeldung" packages="">
    <Appenders>
        <Console name="stdout" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %p %m%n"/>
        </Console>
    </Appenders>
    <Root level="error">
        <AppenderRef ref="STDOUT"/>
    </Root>
</Configuration>
```

几乎所有记录器框架都需要某种级别的配置，无论是通过[编程](https://www.baeldung.com/log4j2-programmatic-config)方式还是通过外部配置文件，例如此处显示的 XML 文件。

## 3.分离日志输出

### 3.1. System.out和System.err

当我们将应用程序部署到像 Tomcat 这样的服务器时，服务器会使用自己的记录器。如果我们使用System.out，日志最终会出现在catalina.out中。如果将日志放在单独的文件中，调试我们的应用程序会容易得多。使用 Log4j2，我们需要在配置中包含一个文件附加程序，以将应用程序日志保存在一个单独的文件中。

此外，对于System.out.println，无法控制或过滤要打印的日志。分离日志的唯一可能方法是将System.out.println 用于信息日志，将System.err.println 用于错误日志：

```java
System.out.println("This is an informational message");
System.err.println("This is an error message");
```

### 3.2. Log4J2 日志级别

在调试或开发环境中，我们希望看到应用程序正在打印的所有信息。但在实时企业应用程序中，更多日志意味着延迟增加。Log4J2 等记录器框架提供了多种日志级别控制：

-   致命的
-   错误
-   警告
-   信息
-   调试
-   痕迹
-   全部

使用这些级别，我们可以轻松过滤何时何地打印哪些信息：

```java
logger.trace("Trace log message");
logger.debug("Debug log message");
logger.info("Info log message");
logger.error("Error log message");
logger.warn("Warn log message");
logger.fatal("Fatal log message");
```

我们还可以为每个源代码包单独配置级别。有关日志级别配置的更多详细信息，请参阅我们的[Java 日志记录文章。](https://www.baeldung.com/java-logging-intro)

## 4. 将日志写入文件

### 4.1. 重新路由System.out和System.err

可以使用System.setOut()方法将System.out.println路由 到一个文件：

```java
PrintStream outStream = new PrintStream(new File("outFile.txt"));
System.setOut(outStream);
System.out.println("This is a baeldung article");
```

如果是System.err：

```java
PrintStream errStream = new PrintStream(new File("errFile.txt"));
System.setErr(errStream);
System.err.println("This is a baeldung article error");
```

当使用System.out或System.err将输出重定向到文件时，我们无法控制文件大小，因此文件在应用程序运行期间不断增长。

随着文件大小的增长，可能很难打开或分析这些较大的日志。

### 4.2. 使用 Log4J2 记录到文件

Log4J2 提供了一种机制来系统地将日志写入文件并根据某些策略滚动文件。例如，我们可以根据日期/时间模式配置要滚动更新的文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="INFO">
    <Appenders>
        <File name="fout" fileName="log4j/target/baeldung-log4j2.log"
          immediateFlush="false" append="false">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss} %p %m%n"/>
        </File>
    <Loggers>
        <AsyncRoot level="DEBUG">
            <AppenderRef ref="stdout"/>
            <AppenderRef ref="fout"/>
        </AsyncRoot>
    </Loggers>
</Configuration>
```

或者我们可以在文件达到给定阈值后根据大小滚动文件：

```xml
...
<RollingFile name="roll-by-size"
  fileName="target/log4j2/roll-by-size/app.log" filePattern="target/log4j2/roll-by-size/app.%i.log.gz"
  ignoreExceptions="false">
    <PatternLayout>
        <Pattern>%d{yyyy-MM-dd HH:mm:ss} %p %m%n</Pattern>
    </PatternLayout>
    <Policies>
        <OnStartupTriggeringPolicy/>
        <SizeBasedTriggeringPolicy size="5 KB"/>
    </Policies>
</RollingFile>
```

## 5. 登录到外部系统

正如我们在上一节中看到的，记录器框架允许将日志写入文件。同样，他们也提供appender 将日志发送到其他系统和应用程序。这使得使用 Log4J 附加程序而不是使用System.out.println 将日志发送到 Kafka Stream 或 Elasticsearch 数据库成为可能。


有关如何使用此类附加程序的更多详细信息，请参阅我们的[Log4j 附加程序文章。](https://www.baeldung.com/log4j2-appenders-layouts-filters)

## 6.自定义日志输出

通过使用记录器，我们可以自定义要打印的信息以及实际消息。我们可以打印的信息包括包名、日志级别、行号、时间戳、方法名等。

虽然使用System.out.println可以做到这一点，但它需要大量的手动工作，而日志框架提供了开箱即用的功能。使用记录器，我们可以简单地在记录器配置中定义一个模式：

```xml
<Console name="ConsoleAppender" target="SYSTEM_OUT">
    <PatternLayout pattern="%style{%date{DEFAULT}}{yellow}
      %highlight{%-5level}{FATAL=bg_red, ERROR=red, WARN=yellow, INFO=green} %message"/>
</Console>

```

如果我们考虑将 Log4J2 作为我们的记录器框架，我们可以从多种模式中进行选择或自定义。请参阅[官方 Log4J2 文档](https://logging.apache.org/log4j/2.x/)以了解有关它们的更多信息。

## 7.通过记录异常输出来避免printStackTrace()

当我们在代码中处理异常时，我们经常需要了解在运行时实际发生了什么异常。为此有两个常用选项：printStackTrace()或使用记录器调用。

使用printStackTrace()打印有关异常的详细信息的异常处理非常常见：

```java
try {
    // some code
} catch (Exception e) {
    e.printStackTrace();
}
```

这里的问题是printStackTrace()将其信息打印到System.err，我们已经说过我们想避免这种情况。

相反，我们可以使用日志记录框架记录异常，然后，我们将能够轻松检索日志：

```java
try {
    // some code
} catch (Exception e) {
    logger.log("Context message", e);
}
```

## 八. 总结

本文解释了为什么要使用记录器框架以及为什么不只依赖System.out.println来获取我们的应用程序日志的各种原因。虽然将System.out.println用于小型测试程序是合理的，但我们不希望将其用作企业生产应用程序的主要日志来源。