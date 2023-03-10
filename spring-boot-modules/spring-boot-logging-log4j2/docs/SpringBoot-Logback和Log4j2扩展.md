## 1. 概述

[日志记录](https://www.baeldung.com/java-logging-intro)是任何软件应用程序的重要组成部分，它有助于解决问题和调试问题。此外，它还可用于监控目的。Spring Boot支持流行的日志记录框架，例如[Logback](https://www.baeldung.com/spring-boot-logging)和[Log4j2](https://www.baeldung.com/spring-boot-logging#log4j2-configuration-logging)。**Spring Boot为Logback和Log4j2提供了一些扩展，它们可能对高级配置很有用**。

在本教程中，我们将研究Spring Boot应用程序中的Logback和Log4j2扩展。

## 2. Logback扩展

默认情况下，Spring Boot使用Logback库进行日志记录。在本节中，我们将学习一些有助于高级配置的Logback扩展。

还值得一提的是，**Spring Boot建议对Logback使用logback-spring.xml而不是默认的logback.xml。我们不能在标准logback.xml中使用扩展，因为logback.xml配置文件加载得太早了**。

### 2.1 Maven依赖项

要使用Logback，我们需要将logback-classic依赖项添加到我们的pom.xml中。但是，Spring Boot Starter依赖项中已经提供了logback-classic依赖项。

所以，我们只需要将[spring-boot-starter-web](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-web/3.0.3)依赖添加到pom.xml：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 2.2 基本日志记录

首先，我们将向主Spring Boot类添加一些日志以在我们的示例中使用：

```java
@SpringBootApplication
public class SpringBootLogbackExtensionsApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringBootLogbackExtensionsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringBootLogbackExtensionsApplication.class, args);

        logger.debug("Debug log message");
        logger.info("Info log message");
        logger.error("Error log message");
        logger.warn("Warn log message");
        logger.trace("Trace log message");
    }
}
```

### 2.3 Profile特定设置

[Spring Profile](https://www.baeldung.com/spring-profiles)提供了一种根据激活的Profile调整应用程序配置的机制。

例如，我们可以为各种环境定义单独的Logback配置，例如“development”和“production”。**如果我们想要一个单一的Logback配置，我们可以使用logback-spring.xml中的springProfile元素**。

**使用springProfile元素，我们可以根据激活的Spring Profile选择性地包含或排除配置部分。我们可以使用name属性来允许哪个Profile接受配置**。

默认情况下，Spring Boot仅记录到控制台。现在，假设我们想将内容记录到一个文件中以用于“production”和一个控制台以用于“development”。我们可以使用springProfile元素轻松地做到这一点。

让我们创建一个logback-spring.xml文件：

```xml
<springProfile name="development">
    <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</Pattern>
        </layout>
    </appender>
    <root level="info">
        <appender-ref ref="Console"/>
    </root>
</springProfile>

<springProfile name="production">
    <appender name="RollingFile" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOGS}/spring-boot-logger.log</file>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</Pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOGS}/archived/spring-boot-logger-%d{yyyy-MM-dd}.log</fileNamePattern>
        </rollingPolicy>
    </appender>
    <root level="info">
        <appender-ref ref="RollingFile" />
    </root>
</springProfile>
```

然后，我们可以使用JVM系统参数-Dspring.profiles.active=development或-Dspring.profiles.active=production设置激活的Profile。

现在，如果我们使用development Profile运行我们的Spring Boot应用程序，我们将在控制台中获得以下输出：

```shell
10:31:13.766 [main] INFO  c.t.t.e.SpringBootLogbackExtensionsApplication - Info log message
10:31:13.766 [main] ERROR c.t.t.e.SpringBootLogbackExtensionsApplication - Error log message
10:31:13.766 [main] WARN  c.t.t.e.SpringBootLogbackExtensionsApplication - Warn log message
```

此外，我们可能希望将内容记录到文件中以用于“staging”或“production”。为了支持这种情况，springProfile的name属性接受Profile表达式。Profile表达式允许更复杂的Profile逻辑：

```xml
<springProfile name="production | staging">
    <!-- configuration -->
</springProfile>
```

当“production”或“staging” Profile处于激活状态时，将启用上述配置。

### 2.4 环境属性

**有时，我们需要在日志配置中访问application.properties文件的值。在这种情况下，我们在Logback配置中使用springProperty元素**。

springProperty元素类似于Logback的标准property元素。但是，我们可以从环境中确定属性的来源，而不是直接指定一个值。

springProperty元素有一个source属性，它采用应用程序属性的值。如果某个属性未在环境中设置，则它会从defaultValue属性中获取默认值。此外，我们需要为name属性设置一个值，以在配置中的其他元素中引用该属性。

最后，我们可以设置[scope](https://logback.qos.ch/manual/configuration.html#scopes)属性。context作用域内的属性是上下文的一部分，并且在所有日志记录事件中都可用。

假设我们想使用应用程序名称作为日志文件的名称。首先，我们在application.properties文件中定义应用名称：

```properties
spring.application.name=logback-extension
```

然后，我们公开应用程序名称以供在logback-spring.xml中使用：

```xml
<property name="LOGS" value="./logs" />
<springProperty scope="context" name="application.name" source="spring.application.name" />
<springProfile name="production">
    <appender name="RollingFile" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOGS}/${application.name}.log</file>
        <!-- configuration -->
    </appender>
</springProfile>
```

在上面的配置中，我们将springProperty元素的source属性设置为属性spring.application.name。此外，我们使用${application.name}引用该属性。

在下一节中，我们将讨论Spring Boot应用程序中的Log4j2扩展。

## 3. Log4j2扩展

Log4j2扩展类似于Logback扩展。**我们不能在标准log4j2.xml配置文件中使用扩展，因为它加载得太早了**。

Spring Boot中推荐的方法是将Log4j2配置存储在名为log4j2-spring.xml的单独文件中。如果存在，Spring Boot将在任何其他Log4j2配置之前加载它。

### 3.1 Maven依赖项

要使用Log4j2库而不是默认的Logback，我们需要**从启动器依赖项中排除Logback**：

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

此外，我们需要将[spring-boot-starter-log4j2](https://central.sonatype.com/artifact/org.springframework.boot/spring-boot-starter-log4j2/3.0.3)和[log4j-spring-boot](https://central.sonatype.com/artifact/org.apache.logging.log4j/log4j-spring-boot/2.20.0)依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-spring-boot</artifactId>
</dependency>
```

我们在应用程序中包含log4j-spring-boot库，以支持Log4j2设置中的Spring Boot Profile。当我们没有此依赖项时，我们会看到以下错误信息：

```shell
2023-01-21 15:56:12,473 main ERROR Error processing element SpringProfile ([Loggers: null]): CLASS_NOT_FOUND
```

### 3.2 Profile特定设置

尽管将设置放入[不同的日志记录配置](https://www.baeldung.com/spring-log4j2-config-per-profile)在大多数情况下都可行，但在某些情况下，我们可能希望为不同的环境使用单一的日志记录设置。在这种情况下，**我们可以使用SpringProfile元素将Spring Boot Profile添加到我们的配置中**。

让我们写一个简单的log4j2-spring.xml：

```xml
<SpringProfile name="development">
    <Logger name="cn.tuyucheng.taketoday.extensions" level="debug"></Logger>
</SpringProfile>

<SpringProfile name="production">
    <Logger name="cn.tuyucheng.taketoday.extensions" level="error"></Logger>
</SpringProfile>
```

这类似于我们在Logback部分中讨论的示例。

如果我们使用production Profile运行应用程序，我们现在将看到来自应用程序的ERROR日志，并且不会再有来自cn.tuyucheng.taketoday.extensions包的DEBUG日志：

```shell
2023-01-22T11:19:52,885 ERROR [main] c.t.t.e.SpringBootLog4j2ExtensionsApplication: Error log message
2023-01-22T11:19:52,885 FATAL [main] c.t.t.e.SpringBootLog4j2ExtensionsApplication: Fatal log message
```

重要的是要注意configuration元素内的任何地方都支持SpringProfile部分。

### 3.3 环境属性查找

[Log4j2查找](https://logging.apache.org/log4j/2.x/manual/lookups.html)提供了一种为我们的日志记录配置文件提供值的方法。

**我们可以在Log4j2配置中使用Log4j2 Lookup访问application.properties文件中的值。此外，我们可以使用激活的Profile和默认Profile的值。为此，在我们的Log4j2配置中，我们可以使用带有spring前缀的查找**。

让我们在application.properties文件中设置spring.application.name=log4j2-extension。然后，我们从Spring Environment中读取spring.application.name：

```xml
<Console name="Console-Extensions" target="SYSTEM_OUT">
    <PatternLayout
        pattern="%d %p %c{1.} [%t] ${spring:spring.application.name} %m%n" />
</Console>
```

在上面的配置中，我们使用spring前缀查找设置spring.application.name。运行后，应用程序会将以下消息记录到控制台：

```shell
2023-01-22 16:17:30,301 ERROR c.t.t.e.SpringBootLog4j2ExtensionsApplication [main] log4j2-extension Error log message
2023-01-22 16:17:30,301 FATAL c.t.t.e.SpringBootLog4j2ExtensionsApplication [main] log4j2-extension Fatal log message
```

### 3.4 Log4j2系统属性

Log4j2提供了多个可用于配置各个方面的[系统属性](https://logging.apache.org/log4j/2.x/manual/configuration.html)。我们可以将这些系统属性添加到log4j2.system.properties文件中。

让我们添加log4j2.debug=true属性。此系统属性将所有内部日志打印到控制台：

```shell
TRACE StatusLogger Log4jLoggerFactory.getContext() found anchor class java.util.logging.Logger
```

此外，我们可以将系统属性添加到application.properties文件中。log4j2.system.properties文件中定义的属性比application.properties文件具有更高的优先级。

## 4. 总结

Spring Boot带有广泛的日志记录支持。在本教程中，我们探索了Spring Boot中的Logback和Log4j2扩展。

与往常一样，Logback扩展的完整源代码可在[GitHub上](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-boot-modules/spring-boot-logging-logback)获得。此外，[GitHub上](https://github.com/tu-yucheng/taketoday-tutorial4j/tree/master/spring-boot-modules/spring-boot-logging-log4j2)还提供了Log4j2扩展的源代码。