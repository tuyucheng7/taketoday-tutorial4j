## 1. 概述

在本快速教程中，我们将了解如何在Maven中配置日志记录选项。

## 2. 命令行

默认情况下，Maven只记录info、warning和error日志。此外，对于error，它不会显示该日志的完整堆栈跟踪。**为了查看完整的堆栈跟踪，我们可以使用-e或–errors选项**：

```bash
$ mvn -e clean compile
// truncated
cannot find symbol
    symbol:   variable name
    location: class Compiled

        at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:213)
        at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:154)
        at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:146)
        ...
```

如上所示，现在Maven显示了完整的错误报告。**也可以通过-X或–debug选项查看debug级别的日志**：

```bash
$ mvn -X clean compile
// truncated
OS name: "mac os x", version: "10.15.5", arch: "x86_64", family: "mac"
[DEBUG] Created new class realm maven.api
[DEBUG] Importing foreign packages into class realm maven.api
...
```

当debug打开时，输出非常冗长。为了解决这个问题，我们可以通过-q或–quiet选项要求Maven不记录任何预期错误：

```bash
$ mvn --quiet clean compile
```

此外，我们可以使用-l或–log-file选项将Maven日志重定向到一个文件：

```bash
$ mvn --log-file ./mvn.log clean compile
```

所有日志都可以在当前目录的mvn.log文件中找到，而不是标准输出。作为替代方案，也可以使用操作系统功能将Maven输出重定向到文件：

```bash
$ mvn clean compile > ./mvn.log
```

## 3. SLF4J设置

**目前，Maven正在使用**[SLF4J API](https://www.baeldung.com/slf4j-with-log4j2-logback)**结合**[SLF4J Simple](https://www.slf4j.org/apidocs/org/slf4j/impl/SimpleLogger.html)**实现进行日志记录**，因此，要使用SLF4J Simple配置日志记录，我们可以编辑${maven.home}/conf/logging/simplelogger.properties文件中的属性。

例如，如果我们在此文件中添加以下行：

```properties
org.slf4j.simpleLogger.showDateTime=true
org.slf4j.simpleLogger.dateTimeFormat=yyyy-MM-dd HH:mm:ss
```

然后Maven将以上述格式显示日期时间信息。

让我们尝试另一个构建：

```bash
$ mvn clean compile
2023-01-03 18:08:07 [INFO] Scanning for projects...
```

我们还可以通过命令行中的-D参数传递这些属性：

```bash
$ mvn compile -Dorg.slf4j.simpleLogger.showThreadName=true
[main] [INFO] Scanning for projects...
```

除了其他信息之外，在这里我们还显示了线程名称。

除了提到的属性之外，我们还可以使用其他[属性](https://www.baeldung.com/slf4j-with-log4j2-logback)配置简单的记录器：

-   org.slf4j.simpleLogger.logFile使用日志文件进行日志记录，而不是标准输出
-   org.slf4j.simpleLogger.defaultLogLevel表示默认的日志级别，它可以是trace、debug、info、warn、error或off之一，默认值为info
-   org.slf4j.simpleLogger.showLogName显示SLF4j记录器名称(如果为true)
-   org.slf4j.simpleLogger.showShortLogName如果为true，则会截断长记录器名称

## 4. 总结

在这个简短的教程中，我们了解了如何在Maven中配置不同的日志记录和详细信息选项。