## 1. 概述

当我们在我们的应用程序中使用 SLF4J 时，我们有时会看到一条关于类路径中多个绑定的警告消息打印到控制台。

在本教程中，我们将尝试了解为什么会看到此消息以及如何解决它。

## 2. 理解警告

首先，让我们看一个示例警告：

```plaintext
SLF4J: Class path contains multiple SLF4J bindings.
SLF4J: Found binding in [jar:file:.../slf4j-log4j12-1.7.21.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: Found binding in [jar:file:.../logback-classic-1.1.7.jar!/org/slf4j/impl/StaticLoggerBinder.class]
SLF4J: See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.
SLF4J: Actual binding is of type [org.slf4j.impl.Log4jLoggerFactory]
```

这个警告告诉我们 SLF4J 找到了两个绑定。一个在slf4j-log4j12-1.7.21.jar中，另一个在logback-classic-1.1.7.jar中。

现在让我们了解为什么会看到此警告。

[Simple Logging Facade forJava(SLF4J)](https://www.baeldung.com/slf4j-with-log4j2-logback)用作各种[日志](https://www.baeldung.com/java-logging-intro)框架的简单外观或抽象。它允许我们在部署时插入我们想要的日志记录框架。

为实现这一点，SLF4J 在类路径上查找绑定(也称为提供程序)。绑定基本上是特定 SLF4J 类的实现，旨在扩展以插入特定的日志记录框架。

按照设计，SLF4J 一次只会与一个日志框架绑定。因此，如果类路径上存在多个绑定，它将发出警告。

值得注意的是，库或框架等嵌入式组件永远不应声明对任何 SLF4J 绑定的依赖性。这是因为当库声明对 SLF4J 绑定的编译时依赖时，它会将绑定强加给最终用户。显然，这否定了 SLF4J 的基本目的。所以，他们应该只依赖于slf4j-api库。

同样重要的是要注意，这只是一个警告。如果 SLF4J 找到多个绑定，它将从列表中选择一个日志记录框架并与之绑定。从警告的最后一行可以看出，SLF4J 通过使用org.slf4j.impl.Log4jLoggerFactory选择 Log4j进行实际绑定。

## 3. 找到冲突的 JAR

该警告列出了它找到的所有绑定的位置。通常，这些信息足以识别不道德的依赖关系，这些依赖关系将不需要的 SLF4J 绑定传递到我们的项目中。

如果无法从警告中识别依赖项，我们可以使用dependency:tree maven 目标：

```plaintext
mvn dependency:tree
```

这将显示项目的依赖关系树：

```plaintext
[INFO] +- org.docx4j:docx4j:jar:3.3.5:compile 
[INFO] |  +- org.slf4j:slf4j-log4j12:jar:1.7.21:compile 
[INFO] |  +- log4j:log4j:jar:1.2.17:compile 
[INFO] +- ch.qos.logback:logback-classic:jar:1.1.7:compile 
[INFO] +- ch.qos.logback:logback-core:jar:1.1.7:compile

```

我们使用 Logback 来登录我们的应用程序。因此，我们特意添加了 Logback 绑定，它存在于logback-classic JAR 中。但是docx4j依赖项也引入了另一个与slf4j-log4j12 JAR 的绑定。

## 4.分辨率

现在我们知道有问题的依赖项，我们只需要从docx4j依赖项中排除slf4j-log4j12 JAR ：

```xml
<dependency>
    <groupId>org.docx4j</groupId>
    <artifactId>docx4j</artifactId>
    <version>${docx4j.version}</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
        <exclusion>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

由于我们不打算使用 Log4j，因此最好也将其排除在外。

## 5.总结

在本文中，我们了解了如何解决 SLF4J 发出的关于多重绑定的常见警告。