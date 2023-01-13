##  1. 概述

在本教程中，我们将讨论[Flogger](https://google.github.io/flogger/)框架，这是一个由 Google 设计的适用于Java的流畅日志记录 API。

## 2. 为什么要使用 Flogger？

现在市场上有那么多日志框架，比如 Log4j 和 Logback，为什么我们还需要另一个日志框架呢？

事实证明，与其他框架相比，Flogger 有几个优势——让我们来看看。

### 2.1. 可读性

Flogger API 的流畅特性大大提高了它的可读性。

让我们看一个示例，我们希望每十次迭代记录一条消息。

使用传统的日志记录框架，我们会看到如下内容：

```java
int i = 0;

// ...

if (i % 10 == 0) {
    logger.info("This log shows every 10 iterations");
    i++;
}
```

但是现在，有了 Flogger，上面的内容可以简化为：

```java
logger.atInfo().every(10).log("This log shows every 10 iterations");
```

虽然有人会争辩说 Flogger 版本的记录器语句看起来比传统版本更冗长，但它确实允许更强大的功能并最终导致更具可读性和表现力的日志语句。

### 2.2. 表现

只要我们避免在记录的对象上调用toString ，就会优化记录对象：

```java
User user = new User();
logger.atInfo().log("The user is: %s", user);
```

如果我们记录日志，如上所示，后端有机会优化日志记录。另一方面，如果我们直接调用toString 或连接字符串，那么这个机会就失去了：

```java
logger.atInfo().log("Ths user is: %s", user.toString());
logger.atInfo().log("Ths user is: %s" + user);
```

### 2.3. 可扩展性

Flogger 框架已经涵盖了我们期望从日志框架获得的大部分基本功能。

但是，在某些情况下我们需要添加功能。在这些情况下，可以扩展 API。

目前，这需要一个单独的支持类。例如，我们可以通过编写一个UserLogger 类来扩展 Flogger API：

```java
logger.at(INFO).forUserId(id).withUsername(username).log("Message: %s", param);
```

这在我们想要一致地格式化消息的情况下可能很有用。然后 UserLogger 将提供自定义方法的实现 forUserId(String id)和 withUsername(String username)。

为此，UserLogger类必须扩展AbstractLogger 类并提供 API 的实现。如果我们看一下FluentLogger，它只是一个没有其他方法的记录器，因此我们可以按原样此类开始，然后通过向其添加方法在此基础上进行构建。

### 2.4. 效率

传统框架广泛使用可变参数。这些方法需要在调用方法之前分配并填充新的Object[] 。此外，传入的任何基本类型都必须自动装箱。

这一切都会在调用站点产生额外的字节码和延迟。如果实际上没有启用日志语句，那将是特别不幸的。在经常出现在循环中的调试级别日志中，成本变得更加明显。Flogger 通过完全避免可变参数来降低这些成本。

Flogger 通过使用可以构建日志语句的流畅调用链来解决这个问题。这允许框架仅对日志方法进行少量覆盖 ，从而能够避免诸如可变参数和自动装箱之类的事情。这意味着 API 可以容纳各种新功能，而不会出现组合爆炸式增长。

典型的日志记录框架将具有以下方法：

```java
level(String, Object)
level(String, Object...)
```

其中level可以是大约七个日志级别名称之一(例如severe)，以及具有接受附加日志级别的规范日志方法：

```java
log(Level, Object...)
```

除此之外，通常还有一些方法的变体，这些方法采用与日志语句相关联的原因(一个 Throwable实例)：

```java
level(Throwable, String, Object)
level(Throwable, String, Object...)
```

很明显，API 将三个关注点耦合到一个方法调用中：

1.  它正在尝试指定日志级别(方法选择)
2.  尝试将元数据附加到日志语句(Throwable原因)
3.  并且，指定日志消息和参数。

这种方法可以快速增加满足这些独立问题所需的不同日志记录方法的数量。

我们现在可以明白为什么在链中有两个方法很重要：

```java
logger.atInfo().withCause(e).log("Message: %s", arg);
```

现在让我们看看如何在我们的代码库中使用它。

## 3.依赖关系

设置 Flogger 非常简单。我们只需要将[flogger](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.flogger") 和 [flogger-system-backend](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.flogger")添加到我们的pom 中：

```xml
<dependencies>
    <dependency>
        <groupId>com.google.flogger</groupId>
        <artifactId>flogger</artifactId>
        <version>0.4</version>
    </dependency>
    <dependency>
        <groupId>com.google.flogger</groupId>
        <artifactId>flogger-system-backend</artifactId>
        <version>0.4</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

设置好这些依赖项后，我们现在可以继续探索可供我们使用的 API。

## 4. 探索 Fluent API

首先，让我们为记录器声明一个静态实例：

```java
private static final FluentLogger logger = FluentLogger.forEnclosingClass();
```

现在我们可以开始记录了。我们将从一些简单的事情开始：

```java
int result = 45 / 3;
logger.atInfo().log("The result is %d", result);
```

日志消息可以使用任何Java的printf格式说明符，例如%s、%d或%016x。

### 4.1. 避免在日志站点工作

Flogger 创建者建议我们避免在日志站点工作。

假设我们有以下长期运行的方法来总结组件的当前状态：

```java
public static String collectSummaries() {
    longRunningProcess();
    int items = 110;
    int s = 30;
    return String.format("%d seconds elapsed so far. %d items pending processing", s, items);
}
```

在我们的日志语句中直接调用collectSummaries很诱人：

```java
logger.atFine().log("stats=%s", collectSummaries());
```

但是，无论配置的日志级别或速率限制如何，现在每次都会调用collectSummaries方法。

使禁用日志语句的成本几乎免费是日志框架的核心。反过来，这意味着它们中的更多可以在代码中完好无损地保留下来。像我们刚才那样写日志语句会失去这个优势。

相反，我们应该使用 LazyArgs.lazy 方法：

```java
logger.atFine().log("stats=%s", LazyArgs.lazy(() -> collectSummaries()));
```

现在，几乎没有在日志站点完成任何工作——只是为 lambda 表达式创建实例。如果 Flogger 打算实际记录消息，它只会评估此 lambda。

尽管允许使用 isEnabled保护日志语句：

```java
if (logger.atFine().isEnabled()) {
    logger.atFine().log("summaries=%s", collectSummaries());
}
```

这不是必需的，我们应该避免它，因为 Flogger 会为我们做这些检查。这种方法也只能按级别保护日志语句，对限速日志语句没有帮助。

### 4.2. 处理异常

异常怎么样，我们如何处理它们？

好吧，Flogger 带有一个withStackTrace方法，我们可以用它来记录一个Throwable实例：

```java
try {
    int result = 45 / 0;
} catch (RuntimeException re) {
    logger.atInfo().withStackTrace(StackSize.FULL).withCause(re).log("Message");
}
```

其中withStackTrace将具有常量值SMALL、MEDIUM、LARGE或FULL的StackSize枚举作为参数。withStackTrace()生成的堆栈跟踪将在默认的java.util.logging后端中显示为LogSiteStackTrace异常。不过，其他后端可能会选择以不同方式处理。

### 4.3. 记录配置和级别

到目前为止，我们在大多数示例中一直使用logger.atInfo ，但 Flogger 确实支持许多其他级别。我们将看看这些，但首先，让我们介绍如何配置日志记录选项。

要配置日志记录，我们使用 LoggerConfig类。

例如，当我们要将日志记录级别设置为 FINE时：

```java
LoggerConfig.of(logger).setLevel(Level.FINE);
```

Flogger 支持各种日志记录级别：

```java
logger.atInfo().log("Info Message");
logger.atWarning().log("Warning Message");
logger.atSevere().log("Severe Message");
logger.atFine().log("Fine Message");
logger.atFiner().log("Finer Message");
logger.atFinest().log("Finest Message");
logger.atConfig().log("Config Message");
```

### 4.4. 速率限制

限速的问题呢？我们如何处理不想记录每次迭代的情况？

Flogger 使用every(int n) 方法来拯救我们：

```java
IntStream.range(0, 100).forEach(value -> {
    logger.atInfo().every(40).log("This log shows every 40 iterations => %d", value);
});
```

当我们运行上面的代码时，我们得到以下输出：

```java
Sep 18, 2019 5:04:02 PM com.baeldung.flogger.FloggerUnitTest lambda$givenAnInterval_shouldLogAfterEveryTInterval$0
INFO: This log shows every 40 iterations => 0 [CONTEXT ratelimit_count=40 ]
Sep 18, 2019 5:04:02 PM com.baeldung.flogger.FloggerUnitTest lambda$givenAnInterval_shouldLogAfterEveryTInterval$0
INFO: This log shows every 40 iterations => 40 [CONTEXT ratelimit_count=40 ]
Sep 18, 2019 5:04:02 PM com.baeldung.flogger.FloggerUnitTest lambda$givenAnInterval_shouldLogAfterEveryTInterval$0
INFO: This log shows every 40 iterations => 80 [CONTEXT ratelimit_count=40 ]
```

如果我们想每 10 秒记录一次怎么办？然后，我们可以使用atMostEvery(int n, TimeUnit unit)：

```java
IntStream.range(0, 1_000_0000).forEach(value -> {
    logger.atInfo().atMostEvery(10, TimeUnit.SECONDS).log("This log shows [every 10 seconds] => %d", value);
});
```

有了这个，结果现在变成了：

```bash
Sep 18, 2019 5:08:06 PM com.baeldung.flogger.FloggerUnitTest lambda$givenATimeInterval_shouldLogAfterEveryTimeInterval$1
INFO: This log shows [every 10 seconds] => 0 [CONTEXT ratelimit_period="10 SECONDS" ]
Sep 18, 2019 5:08:16 PM com.baeldung.flogger.FloggerUnitTest lambda$givenATimeInterval_shouldLogAfterEveryTimeInterval$1
INFO: This log shows [every 10 seconds] => 3545373 [CONTEXT ratelimit_period="10 SECONDS [skipped: 3545372]" ]
Sep 18, 2019 5:08:26 PM com.baeldung.flogger.FloggerUnitTest lambda$givenATimeInterval_shouldLogAfterEveryTimeInterval$1
INFO: This log shows [every 10 seconds] => 7236301 [CONTEXT ratelimit_period="10 SECONDS [skipped: 3690927]" ]
```

## 5. 将 Flogger 与其他后端一起使用

那么，如果我们想将 Flogger 添加到已经使用 [Slf4j](https://www.baeldung.com/slf4j-with-log4j2-logback)或[Log4j](https://www.baeldung.com/java-logging-intro)的现有应用程序中 怎么办？这在我们想要利用现有配置的情况下可能很有用。正如我们将看到的，Flogger 支持多个后端。

### 5.1. 使用 Slf4j 的鞭笞者

配置 Slf4j 后端很简单。首先，我们需要将[flogger-slf4j-backend](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.flogger")依赖项添加到我们的pom中：

```xml
<dependency>
    <groupId>com.google.flogger</groupId>
    <artifactId>flogger-slf4j-backend</artifactId>
    <version>0.4</version>
</dependency>
```

接下来，我们需要告诉 Flogger 我们想要使用与默认后端不同的后端。我们通过系统属性注册一个 Flogger 工厂来做到这一点：

```java
System.setProperty(
  "flogger.backend_factory", "com.google.common.flogger.backend.slf4j.Slf4jBackendFactory#getInstance");
```

现在我们的应用程序将使用现有配置。

### 5.2. 使用 Log4j 的鞭笞者

我们按照类似的步骤配置 Log4j 后端。让我们将[flogger-log4j-backend](https://search.maven.org/classic/#search|ga|1|g%3A"com.google.flogger")依赖项添加到我们的pom中：

```java
<dependency>
    <groupId>com.google.flogger</groupId>
    <artifactId>flogger-log4j-backend</artifactId>
    <version>0.4</version>
    <exclusions>
        <exclusion>
            <groupId>com.sun.jmx</groupId>
            <artifactId>jmxri</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.sun.jdmk</groupId>
            <artifactId>jmxtools</artifactId>
        </exclusion>
        <exclusion>
            <groupId>javax.jms</groupId>
            <artifactId>jms</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
<dependency>
    <groupId>log4j</groupId>
    <artifactId>apache-log4j-extras</artifactId>
    <version>1.2.17</version>
</dependency>
```

我们还需要为 Log4j 注册一个 Flogger 后端工厂：

```java
System.setProperty(
  "flogger.backend_factory", "com.google.common.flogger.backend.log4j.Log4jBackendFactory#getInstance");
```

就是这样，我们的应用程序现在设置为使用现有的 Log4j 配置！

## 六. 总结

在本教程中，我们了解了如何使用 Flogger 框架作为传统日志记录框架的替代品。我们已经看到了一些强大的功能，我们可以在使用该框架时从中受益。

我们还了解了如何通过注册不同的后端(如 Slf4j 和 Log4j)来利用现有配置。