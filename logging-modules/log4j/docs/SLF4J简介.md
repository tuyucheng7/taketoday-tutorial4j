## 1. 概述

[Simple Logging Facade](https://en.wikipedia.org/wiki/Facade_pattern) for Java(缩写为 SLF4J)充当不同日志框架(例如，[java.util.logging、logback、Log4j](https://www.baeldung.com/java-logging-intro))的外观。它提供了一个通用 API，使日志记录独立于实际实现。

这允许不同的日志框架共存。它有助于从一个框架迁移到另一个框架。最后，除了标准化的 API 之外，它还提供了一些“语法糖”。

本教程将讨论将 SLF4J 与 Log4j、Logback、Log4j 2 和 Jakarta Commons Logging 集成所需的依赖项和配置。

有关这些实现的更多信息，请查看我们的文章[Introduction toJavaLogging](https://www.baeldung.com/java-logging-intro)。

## 延伸阅读：

## [登录指南](https://www.baeldung.com/logback)

探索在应用程序中使用 Logback 的基础知识。

[阅读更多](https://www.baeldung.com/logback)→

## [Java 日志简介](https://www.baeldung.com/java-logging-intro)

Java 日志记录快速介绍 - 库、配置详细信息以及每个解决方案的优缺点。

[阅读更多](https://www.baeldung.com/java-logging-intro)→

## [Log4j2 简介——Appenders、Layouts 和 Filters](https://www.baeldung.com/log4j2-appenders-layouts-filters)

本文使用示例丰富的方法介绍 Log4J 2 Appender、Layout 和 Filter 概念

[阅读更多](https://www.baeldung.com/log4j2-appenders-layouts-filters)→

## 2. Log4j 2 设置

要将 SLF4J 与 Log4j 2 一起使用，我们将以下库添加到pom.xml：

```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>2.7</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.7</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>2.7</version>
</dependency>
```

可以在此处找到最新版本：[log4j-api](https://search.maven.org/classic/#search|gav|1|g%3A"org.apache.logging.log4j" AND a%3A"log4j-api")、[log4j-core](https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core)、[log4j-slf4j-impl](https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-slf4j-impl)。

实际的日志记录配置遵循本机 Log4j 2 配置。

让我们看看如何创建Logger实例：

```java
public class SLF4JExample {

    private static Logger logger = LoggerFactory.getLogger(SLF4JExample.class);

    public static void main(String[] args) {
        logger.debug("Debug log message");
        logger.info("Info log message");
        logger.error("Error log message");
    }
}
```

请注意，Logger和LoggerFactory属于org.slf4j包。

[此处](https://github.com/eugenp/tutorials/tree/master/logging-modules/log4j)提供了使用此配置运行的项目示例。

## 3. Logback 设置

我们不需要将 SLF4J 添加到我们的类路径中以将其与 Logback 一起使用，因为 Logback 已经在使用 SLF4J。这是参考实现。

所以，我们只需要包含 Logback 库：

```xml
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.6</version>
</dependency>
```

最新版本可以在这里找到：[logback-classic](https://search.maven.org/classic/#search|gav|1|g%3A"ch.qos.logback" AND a%3A"logback-classic")。

该配置是特定于 Logback 的，但可以与 SLF4J 无缝协作。有了适当的依赖项和配置，我们可以使用前面部分中的相同代码来处理日志记录。

## 4 . Log4j 设置

在前面的部分中，我们介绍了一个用例，其中 SLF4J “位于”特定日志记录实现之上。像这样使用，它完全抽象掉了底层框架。

在某些情况下，我们无法替换现有的日志记录解决方案，例如，由于第三方要求。但这并不会将项目限制为仅已使用的框架。

我们可以将 SLF4J 配置为桥梁，并将对现有框架的调用重定向到它。

让我们添加必要的依赖项来为 Log4j 创建一个桥：

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>log4j-over-slf4j</artifactId>
    <version>1.7.30</version>
</dependency>
```

有了适当的依赖关系(在[log4j-over-slf4j](https://search.maven.org/classic/#search|gav|1|g%3A"org.slf4j" AND a%3A"log4j-over-slf4j")检查最新的)，所有对 Log4j 的调用都将重定向到 SLF4J。

查看[官方文档](http://www.slf4j.org/legacy.html)以了解有关桥接现有框架的更多信息。

与其他框架一样，Log4j 可以作为底层实现。

让我们添加必要的依赖项：

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.30</version>
</dependency>
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

这是[slf4j-log4j12](https://search.maven.org/classic/#search|gav|1|g%3A"org.slf4j" AND a%3A"slf4j-log4j12")和[log4j](https://search.maven.org/classic/#search|gav|1|g%3A"log4j" AND a%3A"log4j")的最新版本。[此处](https://github.com/eugenp/tutorials/tree/master/testing-modules/rest-assured)提供了以这种方式配置的示例项目。

## 5. JCL 桥设置

在前面的部分中，我们展示了如何使用相同的代码库来支持使用不同实现的日志记录。虽然这是 SLF4J 的主要承诺和优势，但它也是 JCL(Jakarta Commons Logging 或 Apache Commons Logging)背后的目标。

JCL 旨在作为类似于 SLF4J 的框架。主要区别在于 JCL 通过类加载系统在运行时解析底层实现。在使用自定义类加载器的情况下，这种方法似乎有问题。

SLF4J 在编译时解析它的绑定。它被认为更简单但足够强大。

幸运的是，两个框架可以在桥接模式下协同工作：

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jcl-over-slf4j</artifactId>
    <version>1.7.30</version>
</dependency>
```

最新的依赖版本可以在这里找到：[jcl-over-slf4j](https://search.maven.org/classic/#search|gav|1|g%3A"org.slf4j" AND a%3A"jcl-over-slf4j")。

与其他情况一样，相同的代码库将运行良好。[此处](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java)提供了运行此设置的完整项目示例。

## 6.更多SLF4J 特性

SLF4J 提供了额外的功能，可以使日志记录更高效，代码更易读。

例如，SLF4J 为处理参数提供了一个非常有用的接口：

```java
String variable = "Hello John";
logger.debug("Printing variable value: {}", variable);
```

这是做同样事情的 Log4j 代码：

```java
String variable = "Hello John";
logger.debug("Printing variable value: " + variable);
```

如我们所见，无论是否启用调试级别，Log4j 都会连接字符串。在高负载应用程序中，这可能会导致性能问题。另一方面，只有在启用调试级别时，SLF4J 才会连接字符串。

要对 Log4J 做同样的事情，我们需要添加一个额外的if块，它将检查是否启用了调试级别：

```java
String variable = "Hello John";
if (logger.isDebugEnabled()) {
    logger.debug("Printing variable value: " + variable);
}
```

SLF4J 标准化了日志记录级别，这些级别对于特定的实现是不同的。它降低了FATAL日志级别(在 Log4j 中引入)，前提是在日志框架中我们不应该决定何时终止应用程序。

使用的日志记录级别是ERROR、WARN、INFO、DEBUG和TRACE。[在我们的Java日志记录简介 中](https://www.baeldung.com/java-logging-intro)阅读有关使用它们的更多信息。

## 七. 总结

SLF4J 有助于在日志记录框架之间进行静默切换。它简单而灵活，可以提高可读性和性能。

和往常一样，代码可以[在 GitHub 上找到。](https://github.com/eugenp/tutorials/tree/master/logging-modules/log4j)此外，我们还引用了另外两个专用于不同文章但包含讨论的日志配置的项目，可以在[此处](https://github.com/eugenp/tutorials/tree/master/feign)和[此处](https://github.com/eugenp/tutorials/tree/master/core-java-modules/core-java)找到。