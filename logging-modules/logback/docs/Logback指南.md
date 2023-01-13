## 1. 概述

[Logback](https://logback.qos.ch/)是Java社区中使用最广泛的日志记录框架之一。它是[其前身 Log4j 的替代品。](https://logback.qos.ch/reasonsToSwitch.html)Logback 提供了更快的实现，提供了更多的配置选项，以及更灵活地归档旧日志文件。

## 延伸阅读：

## [SLF4J简介](https://www.baeldung.com/slf4j-with-log4j2-logback)

关于如何将 Log4j2 和 Logback 与 SLF4J 一起使用，以及如何将其他日志记录 API(例如 JCL)桥接到 SLF4J 的快速指南

[阅读更多](https://www.baeldung.com/slf4j-with-log4j2-logback)→

## [使用 Logback 发送电子邮件](https://www.baeldung.com/logback-send-email)

了解如何配置 Logback 以针对任何应用程序错误发送电子邮件通知

[阅读更多](https://www.baeldung.com/logback-send-email)→

## [Java 日志简介](https://www.baeldung.com/java-logging-intro)

Java 日志记录快速介绍 - 库、配置详细信息以及每个解决方案的优缺点。

[阅读更多](https://www.baeldung.com/java-logging-intro)→

在本教程中，我们将介绍 Logback 的架构并研究如何使用它来改进我们的应用程序。

## 2. Logback架构

Logback 架构由三个类组成：Logger、Appender和Layout。

Logger是日志消息的上下文。这是应用程序与之交互以创建日志消息的类。

Appenders将日志消息放在它们的最终目的地。一个Logger可以有多个Appender。我们通常认为Appenders是附加到文本文件的，但 Logback 比这更强大。

布局为输出准备消息。Logback 支持创建用于格式化消息的自定义类，以及现有类的强大配置选项。

## 3.设置

### 3.1. Maven 依赖

Logback 使用 Simple Logging Facade forJava(SLF4J) 作为其本地接口。在我们开始记录消息之前，我们需要将 Logback 和 SLF4J 添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-core</artifactId>
    <version>1.2.6</version>
</dependency>

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.30</version>
    <scope>test</scope>
</dependency>

```

Maven Central 拥有[最新版本的 Logback Core](https://search.maven.org/classic/#search|gav|1|g%3A"ch.qos.logback" AND a%3A"logback-core")和 [最新版本的slf4j-api](https://search.maven.org/classic/#search|gav|1|g%3A"org.slf4j" AND a%3A"slf4j-api")。

### 3.2. 类路径

Logback 还需要运行时类路径中的[logback-classic.jar ](https://search.maven.org/classic/#search|ga|1|logback-classic)。

我们将把它作为测试依赖项添加到pom.xml中：

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.6</version>
</dependency>

```

## 4. 基本示例和配置

让我们从一个在应用程序中使用 Logback 的快速示例开始。

首先，我们需要一个配置文件。我们将创建一个名为logback.xml的文本文件并将其放在我们的类路径中的某个位置：

```xml
<configuration>
  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <root level="debug">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
```

接下来，我们需要一个带有main方法的简单类：

```java
public class Example {

    private static final Logger logger 
      = LoggerFactory.getLogger(Example.class);

    public static void main(String[] args) {
        logger.info("Example log from {}", Example.class.getSimpleName());
    }
}
```

此类创建一个 Logger并调用 info()以生成日志消息。

当我们运行 Example 时，我们看到我们的消息记录到控制台：

```plaintext
20:34:22.136 [main] INFO Example - Example log from Example
```

很容易看出为什么 Logback 如此受欢迎；我们在几分钟内启动并运行。

此配置和代码为我们提供了一些有关其工作原理的提示：

1.  我们有一个名为STDOUT的附加程序 ，它引用了类名ConsoleAppender。
2.  有一种模式描述了我们的日志消息的格式。
3.  我们的代码创建了一个 Logger，我们通过 info()方法将消息传递给它。

现在我们了解了基础知识，让我们仔细看看。

## 5.记录器上下文

### 5.1. 创建上下文

要将消息记录到 Logback，我们从 SLF4J 或 Logback初始化一个Logger ：

```java
private static final Logger logger 
  = LoggerFactory.getLogger(Example.class);

```

然后我们使用它：

```java
logger.info("Example log from {}", Example.class.getSimpleName());

```

这是我们的日志上下文。当我们创建它时，我们将LoggerFactory传递给我们的类。这为记录器提供了一个名称(还有一个接受字符串的重载)。 

日志上下文存在于与Java对象层次结构非常相似的层次结构中：

1.  当记录器的名称后跟一个点作为后代记录器名称的前缀时，记录器就是祖先
2.  当记录器和孩子之间没有祖先时，记录器就是父母

例如，下面的Example类位于com.baeldung.logback包中。com.baeldung.logback.appenders包中还有另一个名为 ExampleAppender的类。

ExampleAppender 的 Logger是Example 的 Logger 的子项。

所有记录器都是预定义根记录器的后代。

记录器有一个级别，可以通过配置或使用 Logger.setLevel() 来设置。在代码中设置级别会覆盖配置文件。

可能的级别按优先顺序排列为：TRACE、DEBUG、INFO、WARN和ERROR。 每个级别都有一个相应的方法，我们用它来记录该级别的消息。

如果未明确为Logger分配级别，它将继承其最近祖先的级别。根记录器默认为 DEBUG。我们将在下面看到如何覆盖它。

### 5.2. 使用上下文

让我们创建一个示例程序来演示在日志记录层次结构中使用上下文：

```java
ch.qos.logback.classic.Logger parentLogger = 
  (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.baeldung.logback");

parentLogger.setLevel(Level.INFO);

Logger childlogger = 
  (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("com.baeldung.logback.tests");

parentLogger.warn("This message is logged because WARN > INFO.");
parentLogger.debug("This message is not logged because DEBUG < INFO.");
childlogger.info("INFO == INFO");
childlogger.debug("DEBUG < INFO");

```

当我们运行它时，我们会看到这些消息：

```plaintext
20:31:29.586 [main] WARN com.baeldung.logback - This message is logged because WARN > INFO.
20:31:29.594 [main] INFO com.baeldung.logback.tests - INFO == INFO
```

我们首先检索名为com.baeldung.logback的记录器并将其转换为ch.qos.logback.classic.Logger。

需要一个 Logback 上下文来设置下一条语句中的级别；请注意，SLF4J 的抽象记录器没有实现setLevel()。

我们将上下文级别设置为INFO。然后我们创建另一个名为com.baeldung.logback.tests 的记录器。

最后，我们在每个上下文中记录两条消息以演示层次结构。Logback 记录WARN 和INFO消息，并过滤DEBUG 消息。

现在让我们使用根记录器：

```java
ch.qos.logback.classic.Logger logger = 
  (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("com.baeldung.logback");
logger.debug("Hi there!");

Logger rootLogger = 
  (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
logger.debug("This message is logged because DEBUG == DEBUG.");

rootLogger.setLevel(Level.ERROR);

logger.warn("This message is not logged because WARN < ERROR.");
logger.error("This is logged.");

```

执行此代码段时，我们会看到这些消息：

```plaintext
20:44:44.241 [main] DEBUG com.baeldung.logback - Hi there!
20:44:44.243 [main] DEBUG com.baeldung.logback - This message is logged because DEBUG == DEBUG.
20:44:44.243 [main] ERROR com.baeldung.logback - This is logged.

```

总而言之，我们从Logger上下文开始并打印了DEBUG消息。

然后我们使用其静态定义的名称检索根记录器，并将其级别设置为ERROR。

最后，我们证明了 Logback 实际上确实过滤了任何小于错误的语句。

### 5.3. 参数化消息

与上面示例片段中的消息不同，最有用的日志消息需要附加字符串。这需要分配内存、序列化对象、连接 字符串以及稍后可能清理垃圾。

请考虑以下消息：

```java
log.debug("Current count is " + count);

```

无论Logger是否记录消息，我们都会承担构建消息的成本 。

Logback 提供了一个带有参数化消息的替代方案：

```java
log.debug("Current count is {}", count);

```

大括号 {} 将接受任何对象，并仅在验证日志消息是必需的后才使用其toString()方法构建消息。

让我们尝试一些不同的参数：

```java
String message = "This is a String";
Integer zero = 0;

try {
    logger.debug("Logging message: {}", message);
    logger.debug("Going to divide {} by {}", 42, zero);
    int result = 42 / zero;
} catch (Exception e) {
    logger.error("Error dividing {} by {} ", 42, zero, e);
}

```

此代码段产生：

```plaintext
21:32:10.311 [main] DEBUG com.baeldung.logback.LogbackTests - Logging message: This is a String
21:32:10.316 [main] DEBUG com.baeldung.logback.LogbackTests - Going to divide 42 by 0
21:32:10.316 [main] ERROR com.baeldung.logback.LogbackTests - Error dividing 42 by 0
java.lang.ArithmeticException: / by zero
  at com.baeldung.logback.LogbackTests.givenParameters_ValuesLogged(LogbackTests.java:64)
...

```

我们看到了如何将String、 int和Integer作为参数传入。

此外，当异常作为最后一个参数传递给日志记录方法时，Logback 将为我们打印堆栈跟踪。

## 六、详细配置

在前面的示例中，我们使用在[第 4 节](https://www.baeldung.com/logback#example)中创建的 11 行配置文件将日志消息打印到控制台。这是 Logback 的默认行为；如果找不到配置文件，它会创建一个ConsoleAppender 并将其与根记录器相关联。

### 6.1. 定位配置信息

配置文件可以放在类路径中，并命名为 logback.xml 或 logback-test.xml。

以下是 Logback 将如何尝试查找配置数据：

1.  按顺序在类路径中搜索名为logback-test.xml 、 logback.groovy 或logback.xml的文件
2.  如果库没有找到这些文件，它将尝试使用Java的[ServiceLoader](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/ServiceLoader.html)来定位com.qos.logback.classic.spi.Configurator 的实现者。
3.  配置自身以将输出直接记录到控制台

重要提示：由于 Logback 的官方文档，他们已经停止支持 logback.groovy。所以如果你想在你的应用程序中配置 Logback，最好使用 XML 版本。

### 6.2. 基本配置

让我们仔细看看我们的[示例配置。](https://www.baeldung.com/logback#example)

整个文件都在 <configuration> 标签中。

我们看到一个标签，它声明了一个 类型为 ConsoleAppender的Appender，并将其命名为 STDOUT。嵌套在该标签内的是一个编码器。它有一个看起来像 sprintf 风格转义码的模式：

```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

最后，我们看到一个根标签。此标记将根记录器设置为 DEBUG模式，并将其输出与名为 STDOUT的Appender相关联：

```xml
<root level="debug">
    <appender-ref ref="STDOUT" />
</root>
```

### 6.3. 配置故障排除

Logback 配置文件可能会变得复杂，因此有几种内置的故障排除机制。

要在 Logback 处理配置时查看调试信息，我们可以打开调试日志记录：

```xml
<configuration debug="true">
  ...
</configuration>
```

Logback 将在处理配置时将状态信息打印到控制台：

```plaintext
23:54:23,040 |-INFO in ch.qos.logback.classic.LoggerContext[default] - Found resource [logback-test.xml] 
  at [file:/Users/egoebelbecker/ideaProjects/logback-guide/out/test/resources/logback-test.xml]
23:54:23,230 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - About to instantiate appender 
  of type [ch.qos.logback.core.ConsoleAppender]
23:54:23,236 |-INFO in ch.qos.logback.core.joran.action.AppenderAction - Naming appender as [STDOUT]
23:54:23,247 |-INFO in ch.qos.logback.core.joran.action.NestedComplexPropertyIA - Assuming default type 
  [ch.qos.logback.classic.encoder.PatternLayoutEncoder] for [encoder] property
23:54:23,308 |-INFO in ch.qos.logback.classic.joran.action.RootLoggerAction - Setting level of ROOT logger to DEBUG
23:54:23,309 |-INFO in ch.qos.logback.core.joran.action.AppenderRefAction - Attaching appender named [STDOUT] to Logger[ROOT]
23:54:23,310 |-INFO in ch.qos.logback.classic.joran.action.ConfigurationAction - End of configuration.
23:54:23,313 |-INFO in ch.qos.logback.classic.joran.JoranConfigurator@5afa04c - Registering current configuration 
  as safe fallback point
```

如果在解析配置文件时遇到警告或错误，Logback 会将状态消息写入控制台。

还有第二种打印状态信息的机制：

```xml
<configuration>
    <statusListener class="ch.qos.logback.core.status.OnConsoleStatusListener" />  
    ...
</configuration>
```

StatusListener拦截状态消息并在 配置期间以及程序运行时打印它们。

打印所有配置文件的输出，这对于在类路径上定位“恶意”配置文件非常有用。

### 6.4. 自动重新加载配置

在应用程序运行时重新加载日志记录配置是一种强大的故障排除工具。Logback 通过 scan参数使这成为可能：

```plaintext
<configuration scan="true">
  ...
</configuration>
```

默认行为是每 60 秒扫描一次配置文件以查找更改。我们可以通过添加scanPeriod来修改此间隔：

```xml
<configuration scan="true" scanPeriod="15 seconds">
  ...
</configuration>
```

我们可以以毫秒、秒、分钟或小时为单位指定值。

### 6.5. 修改记录器

在上面的示例文件中，我们设置了根记录器的级别并将其与控制台 Appender相关联。

我们可以为任何记录器设置级别：

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <logger name="com.baeldung.logback" level="INFO" /> 
    <logger name="com.baeldung.logback.tests" level="WARN" /> 
    <root level="debug">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

让我们将它添加到我们的类路径并运行代码：

```java
Logger foobar = 
  (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.baeldung.foobar");
Logger logger = 
  (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.baeldung.logback");
Logger testslogger = 
  (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.baeldung.logback.tests");

foobar.debug("This is logged from foobar");
logger.debug("This is not logged from logger");
logger.info("This is logged from logger");
testslogger.info("This is not logged from tests");
testslogger.warn("This is logged from tests");

```

我们看到这个输出：

```plaintext
00:29:51.787 [main] DEBUG com.baeldung.foobar - This is logged from foobar
00:29:51.789 [main] INFO com.baeldung.logback - This is logged from logger
00:29:51.789 [main] WARN com.baeldung.logback.tests - This is logged from tests

```

通过不以编程方式设置我们的记录器的级别 ，配置设置它们；com.baeldung.foobar从根记录器继承 DEBUG 。

记录器 还从根记录器继承 appender-ref 。正如我们将在下面看到的，我们可以覆盖它。

### 6.6. 变量替换

Logback 配置文件支持变量。我们在配置脚本内部或外部定义变量。可以在配置脚本中的任何位置指定变量来代替值。

例如，这是 FileAppender的配置：

```xml
<property name="LOG_DIR" value="/var/log/application" />
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
    <file>${LOG_DIR}/tests.log</file>
    <append>true</append>
    <encoder>
        <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
    </encoder>
</appender>
```

在配置的顶部，我们声明了一个 名为 LOG_DIR的属性。然后我们将它用作 appender定义中文件路径的一部分。 

属性在配置脚本的 <property>标记中声明，但它们也可从外部来源获得，例如系统属性。我们可以省略这个例子中 的属性声明并在命令行上设置LOG_DIR的值：

```plaintext
$ java -DLOG_DIR=/var/log/application com.baeldung.logback.LogbackTests
```

我们使用 ${propertyname} 指定属性的值。Logback 将变量实现为文本替换。变量替换可以发生在配置文件中可以指定值的任何位置。

## 7.追加器

Loggers将 LoggingEvents传递给 Appenders。 Appenders做日志记录的实际工作。我们通常认为日志记录是发送到文件或控制台的东西，但 Logback 的功能远不止于此。Logback-core提供了几个有用的附加程序。

### 7.1. ConsoleAppender

我们已经看到 了ConsoleAppender 的实际应用。尽管有它的名字， ConsoleAppender将消息附加到 System.out 或System.err。

它使用 OutputStreamWriter来缓冲 I/O，因此将其定向到 System.err不会导致无缓冲写入。

### 7.2. 文件追加器

FileAppender 将消息附加到文件。它支持广泛的配置参数。让我们在基本配置中添加一个文件附加器：

```java
<configuration debug="true">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>tests.log</file>
        <append>true</append>
        <encoder>
            <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="com.baeldung.logback" level="INFO" /> 
    <logger name="com.baeldung.logback.tests" level="WARN"> 
        <appender-ref ref="FILE" /> 
    </logger> 

    <root level="debug">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

FileAppender 通过 <file>配置了一个文件名。< append>标记指示 Appender附加到现有文件而不是截断它。如果我们多次运行测试，我们会看到日志输出附加到同一个文件。 

如果我们从上面重新运行测试，来自 com.baeldung.logback.tests的消息将同时发送到控制台和名为 tests.log 的文件。后代 记录器继承根记录器与 ConsoleAppender的关联及其与 FileAppender 的关联。 附加程序是累积的。

我们可以覆盖此行为：

```xml
<logger name="com.baeldung.logback.tests" level="WARN" additivity="false" > 
    <appender-ref ref="FILE" /> 
</logger> 

<root level="debug">
    <appender-ref ref="STDOUT" />
</root>

```

将 可加性设置为false 会禁用默认行为。 测试不会记录到控制台，它的任何后代也不会。

### 7.3. RollingFileAppender

通常，将日志消息附加到同一个文件并不是我们需要的行为。我们希望文件根据时间、日志文件大小或两者的组合“滚动”。

为此，我们有RollingFileAppender：

```xml
<property name="LOG_FILE" value="LogFile" />
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_FILE}.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <!-- daily rollover -->
        <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.gz</fileNamePattern>

        <!-- keep 30 days' worth of history capped at 3GB total size -->
        <maxHistory>30</maxHistory>
        <totalSizeCap>3GB</totalSizeCap>
    </rollingPolicy>
    <encoder>
        <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
    </encoder>
</appender> 

```

RollingFileAppender 有一个 RollingPolicy 。 在此示例配置中，我们看到了一个 TimeBasedRollingPolicy。 

与 FileAppender 类似，我们为这个 appender配置了一个文件名。我们声明了一个属性并使用了它，因为我们将重用下面的文件名。

我们 在 RollingPolicy 中定义了一个fileNamePattern 。这种模式不仅定义了文件的名称，还定义了滚动它们的频率。 TimeBasedRollingPolicy检查模式并在最精确定义的时间段滚动。 

例如：

```xml
<property name="LOG_FILE" value="LogFile" />
<property name="LOG_DIR" value="/var/logs/application" />
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_DIR}/${LOG_FILE}.log</file> 
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>${LOG_DIR}/%d{yyyy/MM}/${LOG_FILE}.gz</fileNamePattern>
        <totalSizeCap>3GB</totalSizeCap>
    </rollingPolicy>
```

活动日志文件是 /var/logs/application/LogFile。 该文件在每个月的月初滚动到 /Current Year/Current Month/LogFile.gz 并且RollingFileAppender创建 一个新的活动文件。

当存档文件的总大小达到 3GB 时，RollingFileAppender 将按照先进先出的原则删除存档。

有星期、小时、分钟、秒甚至毫秒的代码。Logback 在[这里](https://logback.qos.ch/manual/appenders.html#TimeBasedRollingPolicy)有一个参考。

RollingFileAppender 还内置了对压缩文件的支持。它压缩我们滚动的文件，因为我们将它们命名为LogFile.gz。

TimeBasedPolicy 不是我们滚动文件的唯一选择。Logback 还提供 SizeAndTimeBasedRollingPolicy， 它将根据当前日志文件大小和时间滚动。它还提供了一个 FixedWindowRollingPolicy， 它会在每次启动记录器时滚动日志文件名。

我们也可以编写自己的[RollingPolicy](https://logback.qos.ch/manual/appenders.html#onRollingPolicies)。

### 7.4. 自定义附加程序

我们可以通过扩展 Logback 的基本附加程序类之一来创建自定义附加程序。[我们在这里](https://www.baeldung.com/custom-logback-appender)有创建自定义appender 的教程。

## 8.布局

布局格式日志消息。与 Logback 的其余部分一样， 布局 是可扩展的，我们可以[创建自己的布局。](https://logback.qos.ch/manual/layouts.html#writingYourOwnLayout) 然而，默认的PatternLayout提供了大多数应用程序所需要的，然后是一些。

到目前为止，我们在所有示例中都使用了PatternLayout ：

```xml
<encoder>
    <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
</encoder>

```

此配置脚本包含 PatternLayoutEncoder 的配置。 我们将一个编码器传递给我们的 Appender， 这个编码器使用 PatternLayout来格式化消息。

<pattern>标签中的文本 定义了日志消息的格式。 PatternLayout 实现了多种用于创建模式的转换词和格式修饰符。

让我们分解一下。PatternLayout识别带有 % 的转换词，因此我们模式中的转换生成：

-   %d{HH:mm:ss.SSS} – 包含小时、分钟、秒和毫秒的时间戳
-   [%thread] – 生成日志消息的线程名称，用方括号括起来
-   %-5level – 记录事件的级别，填充为 5 个字符
-   %logger{36} – logger的名称，截断为 35 个字符
-   %msg%n – 日志消息后跟平台相关的行分隔符

所以我们看到类似这样的消息：

```plaintext
21:32:10.311 [main] DEBUG com.baeldung.logback.LogbackTests - Logging message: This is a String
```

可在[此处](https://logback.qos.ch/manual/layouts.html#conversionWord)找到详尽的转换词和格式修饰符列表。

## 9.总结

在这篇内容广泛的文章中，我们介绍了在应用程序中使用 Logback 的基础知识。

我们查看了 Logback 架构中的三个主要组件：Logger、Appender和Layout。Logback 有强大的配置脚本，我们用它来操作过滤和格式化消息的组件。我们还讨论了两个最常用的文件附加程序来创建、滚动、组织和压缩日志文件。