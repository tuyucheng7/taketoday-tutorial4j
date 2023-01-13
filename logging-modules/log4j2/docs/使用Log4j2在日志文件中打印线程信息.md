## 1. 概述 

本教程将展示使用[Log4j2 库](https://www.baeldung.com/log4j2-appenders-layouts-filters)记录线程信息的想法和示例。 

## 2.日志和线程

日志是一种强大的工具，可以提供有关发生某些错误或流程时系统中发生的情况的上下文。日志记录帮助我们捕获和保存相关信息以供随时分析。

线程允许我们的应用程序同时执行多项操作以处理更多请求并使我们的工作更有效率。

在这种情况下，许多Java应用程序使用日志记录和线程来控制它们的进程。然而，由于日志通常集中在一个特定的文件上，不同线程的日志杂乱无章，用户无法识别和理解事件的顺序。我们将使用最流行的Java日志记录框架之一 Log4j2 来显示有关我们线程的相关信息，以解决此问题。

## 3. Log4j2的使用

向前，我们有一个使用 Log4j2 中的一些参数来显示有关我们线程的信息的示例：

```xml
<Property name="LOG_PATTERN"> %d{yyyy-MM-dd HH:mm:ss.SSS} --- thread_id="%tid" thread_name="%tn" thread_priority="%tp" --- [%p] %m%n </Property>
```

Log4j2 在其模式中使用参数来引用数据。所有参数在它们的 beginner 中都以% 开头。以下是线程参数的一些示例：

-   tid：线程标识符，是线程创建时产生的正长整数。
-   tn：这是一个命名线程的字符序列。
-   tp：线程优先级是一个介于 1 和 10 之间的整数，其中更重要的数字意味着更高的优先级。

首先，正如它所暗示的，我们要添加有关线程的 ID、名称和优先级的信息。因此，为了可视化它，我们需要创建一个简单的应用程序来创建新线程并记录一些信息：

```java
public class Log4j2ThreadInfo{
    private static final Logger logger = LogManager.getLogger(Log4j2ThreadInfo.class);
    
    public static void main(String[] args) {
        IntStream.range(0, 5).forEach(i -> {
            Runnable runnable = () -> logger.info("Logging info");
            Thread thread = new Thread(runnable);
            thread.start();
        });
    }
}
```

换句话说，我们只是在[Java Streams](https://www.baeldung.com/java-8-streams)的帮助下在 0 到 5 的范围内运行 forEach ，然后启动一个带有一些日志记录的新线程。结果，我们将拥有：

```bash
2022-01-14 23:44:56.893 --- thread_id="22" thread_name="Thread-2" thread_priority="5" --- [INFO] Logging info
2022-01-14 23:44:56.893 --- thread_id="21" thread_name="Thread-1" thread_priority="5" --- [INFO] Logging info
2022-01-14 23:44:56.893 --- thread_id="20" thread_name="Thread-0" thread_priority="5" --- [INFO] Logging info
2022-01-14 23:44:56.893 --- thread_id="24" thread_name="Thread-4" thread_priority="5" --- [INFO] Logging info
2022-01-14 23:44:56.893 --- thread_id="23" thread_name="Thread-3" thread_priority="5" --- [INFO] Logging info
```

## 4. 总结

本文展示了一种使用 Log4j2 参数在Java项目中添加线程信息的简单方法。如果你想检查代码，可以[在 GitHub 上找到](https://github.com/eugenp/tutorials/tree/master/logging-modules/log4j2)。