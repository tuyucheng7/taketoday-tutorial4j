## 1. 概述

在这个简短的教程中，我们将探讨Spring Boot中可用的主要日志记录选项。

有关Logback的更深入信息可在[Logback指南]()中找到，而在[Log4j2介绍–Appenders、Layouts和Filters]()中介绍了Log4j2。

## 2. 初始设置

首先我们创建一个Spring Boot模块，推荐的方法是使用[Spring Initializr](https://start.spring.io/)，我们在[Spring Boot教程]()对此进行了介绍。

现在让我们创建我们唯一的类文件LoggingController：

```java
@RestController
public class LoggingController {

    Logger logger = LoggerFactory.getLogger(LoggingController.class);

    @RequestMapping("/")
    public String index() {
        logger.trace("A TRACE Message");
        logger.debug("A DEBUG Message");
        logger.info("An INFO Message");
        logger.warn("A WARN Message");
        logger.error("An ERROR Message");

        return "Howdy! Check out the Logs to see the output...";
    }
}
```

一旦我们加载了Web应用程序，**我们就可以通过简单地访问**http://localhost:8080/**来触发这些日志记录行**。

## 3. 零配置日志记录

Spring Boot是一个非常有用的框架，它允许我们忘记大多数配置设置，其中许多设置都是自以为是地自动调整的。

在日志记录的情况下，唯一的强制性依赖项是Apache Commons Logging。

我们只有在使用Spring 4.x[(Spring Boot 1.x](https://github.com/spring-projects/spring-boot/blob/1.5.x/spring-boot-dependencies/pom.xml#L154))时才需要导入它，因为它是由Spring Framework的**spring-jcl**模块在Spring 5([Spring Boot 2.x](https://github.com/spring-projects/spring-boot/blob/2.0.x/spring-boot-project/spring-boot-dependencies/pom.xml#L154))中提供的。

**如果我们使用的是Spring Boot Starter(我们几乎总是这样)，我们根本不应该担心导入spring-jcl**，这是因为每个Starter，比如我们的spring-boot-starter-web，都依赖于spring-boot-starter-logging，它已经为我们引入了spring-jcl。

### 3.1 默认Logback日志记录

**当使用Starter时，默认情况下使用Logback进行日志记录**。

Spring Boot使用模式和ANSI颜色对其进行预配置，以使标准输出更具可读性。

现在让我们运行应用程序并访问http://localhost:8080/页面，看看控制台中发生了什么：

<img src="../assets/img.png">

正如我们所见，**Logger的默认日志记录级别预设为INFO，这意味着TRACE和DEBUG消息是不可见的**。

为了在不更改配置的情况下激活它们，**我们可以在命令行上传递–debug或–trace参数**：

```bash
java -jar target/spring-boot-logging-1.0.0.jar --trace
```

### 3.2 日志级别

Spring Boot还**允许我们通过环境变量访问更细粒度的日志级别设置**，我们可以通过多种方式实现这一目标。

首先，我们可以在VM选项中设置日志记录级别：

```bash
-Dlogging.level.org.springframework=TRACE 
-Dlogging.level.cn.tuyucheng.taketoday=TRACE
```

或者，如果我们使用Maven，我们可以**通过命令行**定义我们的日志设置：

```bash
mvn spring-boot:run 
  -Dspring-boot.run.arguments=--logging.level.org.springframework=TRACE,--logging.level.cn.tuyucheng.taketoday=TRACE
```

使用Gradle时，我们可以通过命令行传递日志设置，这将需要[设置bootRun任务]()。

完成后，我们运行应用程序：

```bash
./gradlew bootRun -Pargs=--logging.level.org.springframework=TRACE,--logging.level.cn.tuyucheng.taketoday=TRACE
```

如果我们想永久更改详细程度，我们可以在application.properties文件中执行此操作，如下[所述](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html#boot-features-custom-log-levels)：

```properties
logging.level.root=WARN
logging.level.cn.tuyucheng.taketoday=TRACE
```

最后，我们**可以使用我们的日志框架配置文件永久更改日志记录级别**。

我们提到Spring Boot Starter默认使用Logback，让我们看看如何定义Logback配置文件的片段，其中我们为两个单独的包设置级别：

```xml
<logger name="org.springframework" level="INFO" />
<logger name="cn.tuyucheng.taketoday" level="INFO" />
```

请记住，**如果使用上述不同选项多次定义包的日志级别，但使用不同的日志级别，则将使用最低级别**。

因此，如果我们同时使用Logback、Spring Boot和环境变量设置日志记录级别，则日志级别将为TRACE，因为它是请求级别中最低的。

## 4. Logback配置日志记录

即使默认配置很有用(例如，在POC或快速实验期间零时间上手)，但它很有可能不足以满足我们的日常需求。

让我们看看如何包含一个具有不同颜色和日志记录模式的Logback配置，具有单独的控制台和文件输出规范，以及一个合适的滚动策略来避免生成巨大的日志文件。

首先，我们应该找到一个解决方案，允许单独处理我们的日志记录设置，而不是污染application.properties，application.properties通常用于许多其他应用程序设置。

**当类路径中的文件具有以下名称之一时，Spring Boot将自动加载它并覆盖默认配置**：

-   logback-spring.xml
-   logback.xml
-   logback-spring.groovy
-   logback.groovy

**Spring建议尽可能使用-spring变体而不是普通变体**，如[此处](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html#boot-features-custom-log-configuration)所述。

让我们写一个简单的logback-spring.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property name="LOGS" value="./logs"/>

    <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable
            </Pattern>
        </layout>
    </appender>

    <appender name="RollingFile" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOGS}/spring-boot-logger.log</file>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%d %p %C{1.} [%t] %m%n</Pattern>
        </encoder>

        <rollingPolicy
                class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- rollover daily and when the file reaches 10 MegaBytes -->
            <fileNamePattern>${LOGS}/archived/spring-boot-logger-%d{yyyy-MM-dd}.%i.log
            </fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy
                    class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>

    <!-- LOG everything at INFO level -->
    <root level="info">
        <appender-ref ref="RollingFile"/>
        <appender-ref ref="Console"/>
    </root>

    <!-- LOG "cn.tuyucheng.taketoday*" at TRACE level -->
    <logger name="cn.tuyucheng.taketoday" level="trace" additivity="false">
        <appender-ref ref="RollingFile"/>
        <appender-ref ref="Console"/>
    </logger>

</configuration>
```

当我们运行应用程序时，输出如下：

<img src="../assets/img_1.png">

正如我们所见，它现在记录了TRACE和DEBUG消息，并且整个控制台模式在文本和色彩上都与以前不同。

它还会在当前路径下创建的/logs文件夹中记录一个文件，并通过滚动策略将其存档。

## 5. Log4j2配置日志记录

虽然Apache Commons Logging是核心，而Logback是提供的参考实现，但已经包含了到其他日志库的所有路由，以便轻松切换到它们。

**但是，为了使用除Logback之外的任何日志记录库，我们需要将其从我们的依赖项中排除**。

对于像这样的每个Starter(它是我们示例中唯一的一个，但我们可以有很多)：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

我们需要将它变成一个精简版，并且(仅一次)添加我们的替代库，这里通过Starter本身：

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
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

此时，我们需要在类路径中放置一个名为以下之一的文件：

-   log4j2-spring.xml
-   log4j2.xml

我们将通过Log4j2(通过SLF4J)打印，无需进一步修改。

让我们创建一个简单的log4j2-spring.xml：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration>
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout
                    pattern="%style{%d{ISO8601}}{black} %highlight{%-5level }[%style{%t}{bright,blue}] %style{%C{1.}}{bright,yellow}: %msg%n%throwable"/>
        </Console>

        <RollingFile name="RollingFile"
                     fileName="./logs/spring-boot-logger-log4j2.log"
                     filePattern="./logs/$${date:yyyy-MM}/spring-boot-logger-log4j2-%d{-dd-MMMM-yyyy}-%i.log.gz">
            <PatternLayout>
                <pattern>%d %p %C{1.} [%t] %m%n</pattern>
            </PatternLayout>
            <Policies>
                <!-- rollover on startup, daily and when the file reaches 
                    10 MegaBytes -->
                <OnStartupTriggeringPolicy/>
                <SizeBasedTriggeringPolicy
                        size="10 MB"/>
                <TimeBasedTriggeringPolicy/>
            </Policies>
        </RollingFile>
    </Appenders>

    <Loggers>
        <!-- LOG everything at INFO level -->
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="RollingFile"/>
        </Root>

        <!-- LOG "cn.tuyucheng.taketoday*" at TRACE level -->
        <Logger name="cn.tuyucheng.taketoday" level="trace"></Logger>
    </Loggers>
</Configuration>
```

当我们运行应用程序时，输出如下：

<img src="../assets/img_2.png">

正如我们所看到的，输出与Logback的输出有很大不同，这证明我们现在使用的确实是Log4j2。

除了XML配置之外，Log4j2还允许我们使用YAML或JSON配置，[如下所述](https://docs.spring.io/spring-boot/docs/current/reference/html/howto-logging.html#howto-configure-log4j-for-logging-yaml-or-json-config)。

## 6. 没有SLF4J的Log4j2

我们也可以在本机使用Log4j2，而无需通过SLF4J。

为了做到这一点，我们只需使用原生类：

```java
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
// [...]
Logger logger = LogManager.getLogger(LoggingController.class);
```

我们不需要对标准Log4j2 Spring Boot配置执行任何其他修改。

我们现在可以利用Log4j2的全新功能，而不会受困于旧的SLF4J接口。但是我们也依赖于这个实现，当切换到另一个日志记录框架时我们需要重写我们的代码。

## 7. 使用Lombok记录

在我们目前看到的示例中，**我们必须从我们的日志记录框架中声明一个Logger实例**。这个样板代码可能很烦人，我们可以使用Lombok引入的各种注解来避免它。

首先我们需要在我们的构建脚本中添加Lombok依赖项才能使用它：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.20</version>
    <scope>provided</scope>
</dependency>
```

### 7.1 @Slf4j和@CommonsLog

SLF4J和Apache Commons Logging APIs允许我们灵活地改变我们的日志框架而不影响我们的代码。

我们可以**使用Lombok的@Slf4j和@CommonsLog注解**将正确的记录器实例添加到我们的类中：用于SLF4J的org.slf4j.Logger和用于Apache Commons Logging的org.apache.commons.logging.log。

要查看这些注解的实际效果，让我们创建一个类似于LoggingController但没有记录器实例的类，我们将其命名为LombokLoggingController并使用@Slf4j对其进行标注：

```java
@RestController
@Slf4j
public class LombokLoggingController {

    @RequestMapping("/lombok")
    public String index() {
        log.trace("A TRACE Message");
        log.debug("A DEBUG Message");
        log.info("An INFO Message");
        log.warn("A WARN Message");
        log.error("An ERROR Message");

        return "Howdy! Check out the Logs to see the output...";
    }
}
```

请注意，我们使用log作为记录器实例稍微调整了代码片段，这是因为添加注解@Slf4j会自动添加一个名为log的字段。

**使用零配置日志记录，应用程序将使用底层日志记录实现Logback进行日志记录**。同样，Log4j2实现用于使用Log4j2-Configuration Logging进行日志记录。

当我们用@CommonsLog替换注解@Slf4j时，我们得到相同的行为。

### 7.2 @Log4j2

我们可以使用注解@Log4j2来直接使用Log4j2，因此，我们对LombokLoggingController进行了简单的更改，以使用@Log4j2而不是@Slf4j或@CommonsLog：

```java
@RestController
@Log4j2
public class LombokLoggingController {

    @RequestMapping("/lombok")
    public String index() {
        log.trace("A TRACE Message");
        log.debug("A DEBUG Message");
        log.info("An INFO Message");
        log.warn("A WARN Message");
        log.error("An ERROR Message");

        return "Howdy! Check out the Logs to see the output...";
    }
}
```

除了日志记录之外，还有来自Lombok的其他注解有助于保持我们的代码干净整洁。有关它们的更多信息，请参阅[Project Lombok介绍]()，我们还有一个关于[使用Eclipse和IntelliJ设置Lombok]()的教程。

## 8. 谨防Java Util Logging

Spring Boot还通过logging.properties配置文件支持JDK日志记录。

不过，在有些情况下使用它并不是一个好主意，从[文档中](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html#boot-features-custom-log-configuration)可知：

>   Java Util Logging存在已知的类加载问题，这些问题在从“可执行jar”运行时会导致问题，我们建议你在从“可执行jar”运行时尽可能避免使用它。

当使用Spring 4手动排除pom.xml中的commons-logging时，这也是一种很好的做法，以避免日志库之间的潜在冲突。Spring 5会自动处理它，因此我们在使用Spring Boot 2时不需要做任何事情。

## 9. Windows上的JANSI

虽然Linux和Mac OS X等基于Unix的操作系统默认支持ANSI颜色代码，但在Windows控制台上，一切都将是单色的，**Windows可以通过一个名为JANSI的库获取ANSI颜色**。

**不过，我们应该注意可能的类加载缺点**。

我们必须在配置中导入并显式激活它，如下所示：

[Logback](https://logback.qos.ch/manual/layouts.html#coloring)：

```xml
<configuration debug="true">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <withJansi>true</withJansi>
        <encoder>
            <pattern>[%thread] %highlight(%-5level) %cyan(%logger{15}) - %msg %n</pattern>
        </encoder>
    </appender>
    <!-- more stuff -->
</configuration>
```

[Log4j2](https://logging.apache.org/log4j/2.x/manual/layouts.html#enable-jansi)：

>   许多平台原生支持ANSI转义序列，但默认情况下Windows不支持。**要启用ANSI支持，请将Jansi jar添加到我们的应用程序并将属性log4j.skipJansi设置为false**，这允许Log4j在写入控制台时使用Jansi添加ANSI转义码。
>   注意：在Log4j 2.10之前，默认情况下启用Jansi，Jansi需要本机代码这一事实意味着**Jansi只能由单个类加载器加载**。对于Web应用程序，这意味着**Jansi jar必须位于Web容器的类路径中**。为了避免给Web应用程序带来问题，Log4j不再自动尝试在没有显式配置的情况下从Log4j 2.10开始加载Jansi。

还值得注意的是：

-   [布局文档页面](https://logging.apache.org/log4j/2.x/manual/layouts.html)在**highlight{pattern}{style}**部分包含有用的Log4j2 JANSI信息。
-   虽然JANSI可以为输出着色，但Spring Boot的横幅(原生的或通过banner.txt文件自定义的)将保持单色。

## 10. 总结

我们了解了在Spring Boot项目中与主要日志记录框架交互的主要方法，并探讨了每种解决方案的主要优点和缺陷。