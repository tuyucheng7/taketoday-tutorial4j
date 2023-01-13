## 1. 概述

Log4j 2库从 2.4 版开始添加了对Java8 lambda 表达式的支持。Logger接口可以使用这些表达式来启用惰性日志记录。

让我们看一个快速示例，说明如何使用此功能。

有关Log4j 2的更多信息，还请查看我们的[介绍性文章](https://www.baeldung.com/log4j2-appenders-layouts-filters)。

## 2. 使用 Lambda 表达式进行延迟日志记录

如果未启用相应的日志级别，则可以通过避免计算日志消息来提高使用日志记录的应用程序的性能。

首先，让我们看一个简单的 TRACE 级别的日志语句：

```java
logger.trace("Number is {}", getRandomNumber());
```

在本例中，无论是否显示 TRACE 语句，都会调用getRandomNumber()方法来替换日志消息参数。例如，如果日志级别设置为 DEBUG，log4j 2将不会记录消息，但getRandomNumber()方法仍然运行。

换句话说，该方法的执行可能是不必要的。

在添加对 lambda 表达式的支持之前，我们可以通过在执行日志语句之前显式检查日志级别来避免构造未记录的消息：

```java
if (logger.isTraceEnabled()) {
    logger.trace("Number is {}", getRandomNumer());
}
```

在这种情况下，只有在启用 TRACE 日志级别时才会调用getRandomNumber()方法。这可以提高性能，具体取决于用于替换参数的方法的执行成本。

通过使用 lambda 表达式，我们可以进一步简化上面的代码：

```java
logger.trace("Number is {}", () -> getRandomNumber());
```

lambda 表达式只有在相应的日志级别启用时才会被评估。这称为惰性日志记录。

我们还可以对一条日志消息使用多个 lambda 表达式：

```java
logger.trace("Name is {} and age is {}", () -> getName(), () -> getRandomNumber());
```

## 3.总结

在本快速教程中，我们了解了如何将 lambda 表达式与Log4j 2记录器一起使用。