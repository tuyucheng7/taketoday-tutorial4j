## 1. 概述

在本教程中，我们将展示如何修复警告“log4j: WARN No appenders could be found for logger”。 我们将解释什么是 appender 以及如何定义它。此外，我们将展示如何以不同的方式解决警告。

## 2. Appender定义

先解释一下什么是appender。[Log4j](https://www.baeldung.com/java-logging-intro#Log4J2)允许我们将日志放入多个目的地。它打印输出的每个目的地称为 appender。我们有用于控制台、文件、JMS、GUI 组件等的附加程序。

log4j 中没有定义默认的附加程序。 此外，[一个记录器可以有多个附加程序](https://www.baeldung.com/log4j2-appenders-layouts-filters)，在这种情况下，记录器会将输出打印到所有附加程序中。

## 3.警告信息解释

现在我们知道了 appender 是什么，让我们了解手头的问题。警告消息说找不到记录器的附加程序。

让我们创建一个NoAppenderExample类来重现警告：

```java
public class NoAppenderExample {
    private final static Logger logger = Logger.getLogger(NoAppenderExample.class);

    public static void main(String[] args) {
        logger.info("Info log message");
    }
}
```

我们在没有任何 log4j 配置的情况下运行我们的类。在此之后，我们可以在控制台输出中看到警告以及更多详细信息：

```java
log4j:WARN No appenders could be found for logger (com.baeldung.log4j.NoAppenderExample).
log4j:WARN Please initialize the log4j system properly.
log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
```

## 4.解决配置问题

默认情况下，Log4j 在应用程序的资源中查找配置文件，该文件可以是 XML 或Java属性格式。现在让我们在资源目录下定义log4j.xml文件：

```xml
<log4j:configuration debug="false">
    <!--Console appender -->
    <appender name="stdout" class="org.apache.log4j.ConsoleAppender">
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss} %p %m%n"/>
        </layout>
    </appender>

    <root>
        <level value="DEBUG"/>
        <appender-ref ref="stdout"/>
    </root>
</log4j:configuration>
```

我们定义了根 记录器，它位于记录器层次结构的顶部。所有应用程序记录器都是它的子项并覆盖其配置。我们用一个附加器定义了根记录器，它将日志放入控制台。

让我们再次运行NoAppenderExample类并检查控制台输出。结果，日志包含我们的语句：

```java
2021-05-23 12:59:10 INFO Info log message
```

### 4.1. Appender 可加性

不必为每个记录器定义附加程序。给定记录器的记录请求将日志发送到为其定义的附加程序以及为层次结构中更高级别的记录器指定的所有附加程序。让我们用一个例子来展示它。

如果记录器A定义了控制台附加程序并且记录器B是A的子项，则记录器B也将其日志打印到控制台。仅当中间祖先中的可加性标志设置为true时，记录器才从其祖先继承附加程序。如果 additivity 标志设置为false，则不会继承来自层次结构中较高记录器的附加程序。

为了证明记录器从祖先那里继承了附加程序，让我们在log4j.xml文件中为NoAppenderExample添加一个记录器：

```xml
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd" >
<log4j:configuration debug="false">
    ...
    <logger name="com.baeldung.log4j.NoAppenderExample" />
    ...
</log4j:configuration>
```

让我们再次运行NoAppenderExample类。这次，日志语句出现在控制台中。尽管NoAppenderExample记录器没有显式定义附加器，但它从根记录器继承了附加器。

## 5. 配置文件不在类路径中

现在让我们考虑在应用程序类路径之外定义配置文件的情况。我们有两个选择：

-   使用java命令行选项指定文件路径：-Dlog4j.configuration=<log4j 配置文件路径>
-   在代码中定义路径：PropertyConfigurator.configure(“<log4j 属性文件的路径>”);

在下一节中，我们将看到如何在我们的Java代码中实现这一点。

## 6. 解决代码中的问题

假设我们不想要配置文件。让我们删除log4.xml文件并修改主要 方法：

```java
public class NoAppenderExample {
    private final static Logger logger = Logger.getLogger(NoAppenderExample.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        logger.info("Info log message");
    }
}
```

我们从BasicConfigurator类调用静态配置方法。它将ConsoleAppender 添加到根 记录器。我们看一下configure方法的源码：

```java
public static void configure() {
    Logger root = Logger.getRootLogger();
    root.addAppender(new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n")));
}
```

因为log4j中的根记录器始终存在，所以我们可以通过编程方式将控制台附加程序添加到它。

## 七. 总结

关于如何解决有关缺少附加程序的 log4j 警告的简短教程到此结束。我们解释了什么是 appender 以及如何使用配置文件解决警告问题。然后，我们解释了 appender 可加性是如何工作的。最后，我们展示了如何在代码中解决警告。