## 一、简介

日志记录是任何生产就绪应用程序的重要组成部分。Java 应用程序中流行的选择是[slf4j——](https://www.baeldung.com/slf4j-with-log4j2-logback)一个用于日志框架(如[logback](https://www.baeldung.com/logback)或[log4j](https://www.baeldung.com/java-logging-intro#Log4j) )的简单外观。这很好，但他们并没有使用 Kotlin 为我们提供的所有强大功能。

在本文中，我们将研究一个名为[kotlin-logging](https://github.com/MicroUtils/kotlin-logging)的 Kotlin sfl4j 包装器。

## 2.快速入门

在进入细节之前，这里有一个快速使用指南。

### 2.1. 添加到项目

为了将 kotlin-logging 添加到我们的项目中，我们可以使用 Maven：

```xml
<dependency>
    <groupId>io.github.microutils</groupId>
    <artifactId>kotlin-logging-jvm</artifactId>
    <version>2.0.11</version>
</dependency>
```

或摇篮：

```apache
implementation 'io.github.microutils:kotlin-logging-jvm:2.0.11'
```

由于 kotlin-logging 是 slf4j 的包装器，它本身是不同日志记录后端的包装器，我们将需要一个配置。在我们的例子中，我们将使用 logback 并使用 Maven 添加它：

```kotlin
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.6</version>
</dependency>

```

或摇篮：

```csharp
implementation group: 'ch.qos.logback', name: 'logback-classic', version: '1.2.6'
```

为了使用 log back，我们需要一些配置。这是一个基本的：

```css
src/main/resources/logback.xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} MDC=%X{user} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="debug">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>

```

[logback 配置](https://www.baeldung.com/logback)还有更多内容，但对于基本使用来说应该足够了。现在我们都设置好了——让我们看看我们如何 实际记录一些东西！

### 2.2. 基本用法

要开始使用记录器，首先，我们需要导入 kotlin-logging：

```kotlin
import mu.KotlinLogging
```

然后将logger声明为顶级变量：

```kotlin
private val logger = KotlinLogging.logger {}
```

完成后，我们就可以开始了。例如，运行：

```kotlin
fun main() {
    logger.trace { "This is trace log" }
    logger.debug { "This is debug log" }
    logger.info { "This is info log" }
    logger.warn { "This is warn log" }
    logger.error { "This is error log" }
}
```

将打印：

```vim
16:29:20.390 [main] DEBUG com.baeldung.logging.BasicUsageMain MDC= - This is debug log
16:29:20.393 [main] INFO  com.baeldung.logging.BasicUsageMain MDC= - This is info log
16:29:20.394 [main] WARN  com.baeldung.logging.BasicUsageMain MDC= - This is warn log
16:29:20.394 [main] ERROR com.baeldung.logging.BasicUsageMain MDC= - This is error log

```

这应该是一个快速入门指南。不过，还有一些悬而未决的问题。为什么没有打印跟踪日志？为什么我们使用大括号而不是圆括号？让我们在下一节中回答这些问题(以及更多！)。

## 3.细节

kotlin-logging 库的基本用法很简单，但了解一些细节可能有助于更高级的用法。

### 3.1. 振作起来

使用 kotlin-logging 进行日志记录时，使用花括号代替圆括号非常有用。假设我们有一个很大的评估函数：

```kotlin
val bigEvaluationFunction: (String) -> String = {
    println("I am a VERY BIG evaluation: $it")
    "Big Evaluation"
}

```

和两个记录器调用，首先使用括号：

```kotlin
logger.trace("Running big evaluation: ${bigEvaluationFunction("eagerly")}")
```

其次，使用大括号：

```kotlin
logger.trace { "Running big evaluation: ${bigEvaluationFunction("lazily")}" }
```

假设我们在logback.xml中配置了 高于trace的日志记录级别 (因此，例如调试)，那么这两个日志应该什么都不做，对吧？错误的！虽然不会记录任何内容，但使用括号的跟踪调用将被急切地评估，因此此代码的输出将是：

```kotlin
I am a VERY BIG evaluation: eagerly
```

总而言之，如果我们希望每次都评估日志，请使用括号，如果我们希望仅在需要时评估日志，请使用花括号。

### 3.2. 记录器命名

正如我们在原始示例中看到的那样，我们不必在每次创建记录器时都为其命名。Kotlin-logging 将为我们做这些，所以这段代码：

```kotlin
val logger = KotlinLogging.logger {}
```

被翻译成如下代码：

```kotlin
val logger = LoggerFactory.getLogger("package.ClassName")
```

通常，这就足够了，但如果我们想给我们的记录器起一个非常特别的名字，kotlin-logging 允许我们这样做：

```kotlin
private val logger = KotlinLogging.logger("The Name")
```

### 3.3. 使用底层记录器

Kotlin-logging 是一个slf4j包装器，有时我们可能想直接访问它。当然，Kotlin-logging 允许我们这样做：

```kotlin
val slf4j: org.slf4j.Logger = logger.underlyingLogger
```

从这里，我们可以做我们可以用slf4j 记录器做的一切。

### 3.4. 创建记录器的不同方法

目前，在 kotlin-logging 中创建记录器的推荐方式是顶层声明方式：

```kotlin
private val staticLogger = KotlinLogging.logger {}
```

我们可以通过其他(不太推荐)的方式来创建它。其中之一是之前推荐的方法，涉及为相关类创建伴生对象并使用KLogging()扩展它：

```kotlin
class ClassForImportantWork {
    companion object: KLogging()

    fun importantWork() {
        logger.debug { "I'm logging via companion object" }
    }
}
```

另一种可能有用的方法是简单地扩展KLoggable接口并将记录器声明为类中的一个字段：

```kotlin
class ClassForImportantWork: KLoggable {
    override val logger: KLogger = logger()

    fun importantWork() {
        logger.debug { "I'm logging via non static member" }
    }
}
```

顶级声明方法应该足以满足更多用例，但如果我们遇到一些较旧的代码，了解较早的方法也可能会有用。

### 3.5. 在 Android 中使用 Kotlin 日志记录

我们可以在 Android 应用程序中使用 kotlin-logging。我们需要做的就是添加slf4j-android：

```csharp
implementation group: 'org.slf4j', name: 'slf4j-android', version: '1.7.32'
```

添加该依赖项后，我们可以使用前面示例中所示的 kotlin-logging。

### 3.6. Kotlin 日志记录和 MDC

[MDC](https://www.baeldung.com/mdc-in-log4j-2-logback)在改进应用程序日志记录方面发挥着重要作用，但这里的问题是 kotlin-logging 是否支持它。答案是肯定的。请注意原始 logback.xml 中的片段MDC=%X{user} 。

通过使用 withLoggingContext，我们可以用数据填充用户参数。所以这段代码：

```kotlin
withLoggingContext("user" to "Baeldung") {
    logger.info { "Log with MDC" }
}
```

会产生：

```apache
16:34:36.486 [main] INFO  com.baeldung.logging.LoggingContext MDC=Baeldung - Log with MDC
```

我们在日志上下文中提供的用户会自动传递给记录器。

## 4。总结

Kotlin-logging 是一个方便的工具，可以让我们的日志代码感觉更像 Kotlin 而不是 Java。我们已经了解了如何配置和使用它，以及可能影响我们使用该库的体验的不同细节。