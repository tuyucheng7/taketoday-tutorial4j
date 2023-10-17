## 1. 概述

垃圾收集是 Java 编程语言的一个奇迹，它为我们提供了自动内存管理。垃圾收集隐藏了必须手动分配和释放内存的细节。虽然这个机制很棒，但有时它并不能按照我们想要的方式工作。在本教程中，我们将探索 Java 的垃圾收集统计日志记录选项，并探索如何将这些统计信息重定向到一个文件。

## 2. Java 8 及更早版本中的 GC 日志记录标志

首先，让我们探索 Java 9 之前的 Java 版本中与 GC 日志记录相关的 JVM 标志。

### 2.1. -XX:+打印GC

-XX:+PrintGC标志是-verbose:gc的别名，并打开基本的 GC 日志记录。在这种模式下，为每个年轻代和每个全代集合打印一行。现在让我们将注意力转向提供详细的 GC 信息。

### 2.2. -XX:+打印GCDetails

同样，我们有标志-XX:+PrintGCDetails用于激活详细的 GC 日志记录而不是-XX:+PrintGC。

请注意，-XX:+PrintGCDetails的输出 会根据所使用的 GC 算法而变化。

接下来，我们将看看用日期和时间信息注释我们的日志。

### 2.3. -XX:+PrintGCDateStamps和-XX:+PrintGCTimeStamps

我们可以分别使用标志-XX:+PrintGCDateStamps和-XX:+PrintGCTimeStamps将日期和时间信息添加到我们的 GC 日志中。

首先，-XX:+PrintGCDateStamps 将日志条目的日期和时间添加到每一行的开头。

其次，-XX:PrintGCTimeStamps向日志的每一行添加一个时间戳，详细说明自 JVM 启动以来经过的时间(以秒为单位)。

### 2.4. -Xloggc

最后，我们来将 GC 日志重定向到一个文件。此标志使用语法-Xloggc:file将可选文件名作为参数，并且在不存在文件名的情况下，GC 日志将写入标准输出。

此外，此标志还为我们设置了-XX:PrintGC和-XX:PrintGCTimestamps标志。让我们看一些例子：

如果我们想将 GC 日志写入标准输出，我们可以运行：

```bash
java -cp $CLASSPATH -Xloggc mypackage.MainClass
```

或者将 GC 日志写入文件，我们将运行：

```
java -cp $CLASSPATH -Xloggc:/tmp/gc.log mypackage.MainClass
```

## 3. Java 9 及更高版本中的 GC 日志记录标志

在 Java 9+ 中，-XX:PrintGC ( -verbose:gc的别名)已被弃用，取而代之的是统一日志记录选项-Xlog。上面提到的所有其他 GC 标志在 Java 9+ 中仍然有效。这个新的日志记录选项允许我们指定应显示哪些消息、设置日志级别以及重定向输出。

我们可以运行以下命令来查看日志级别、日志装饰器和标签集的所有可用选项：

```bash
java -Xlog:logging=debug -version

```

例如，如果我们想将所有 GC 消息记录到一个文件中，我们将运行：

```bash
java -cp $CLASSPATH -Xlog:gc=debug:file=/tmp/gc.log mypackage.MainClass
```

此外，这个新的统一日志记录标志是可重复的，因此你可以，例如，将所有 GC 消息记录到标准输出和文件中：

```bash
java -cp $CLASSPATH -Xlog:gc=debug:stdout -Xlog:gc=debug:file=/tmp/gc.log mypackage.MainClass
```

## 4。总结

在本文中，我们展示了如何在 Java 8 和 Java 9+ 中记录垃圾回收输出，包括如何将该输出重定向到文件。