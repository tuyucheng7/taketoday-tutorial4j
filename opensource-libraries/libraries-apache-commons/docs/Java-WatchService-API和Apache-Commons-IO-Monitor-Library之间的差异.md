## 1. 概述

早在Java7 中发布JavaWatchService API 之前，Apache Commons IO Monitoring 库就已经解决了监视文件系统位置或目录更改的相同用例。

在本文中，我们将探讨这两个 API 之间的区别。

## 2.Maven依赖

要使用 Apache Commons IO，需要在pom中添加以下依赖项：

```xml
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.11.0</version>
</dependency>
```

当然，watch 服务是 JDK 的一部分，因此它不需要外部依赖。

## 3. 特征对比

### 3.1. 事件驱动处理

WatchService API 由操作系统触发的文件系统更改事件驱动。这种方法避免了应用程序重复轮询文件系统以获取更改。

另一方面，Apache Commons IO Monitor 库通过调用File类的listFiles()方法以可配置的睡眠间隔轮询文件系统位置。这种方法会浪费 CPU 周期，尤其是在没有发生变化的情况下。

### 3.2. 回调方法

WatchService API 不提供回调方法。相反，它提供了两种类型的轮询方法来检查是否有新的更改事件可供处理：

1.  阻塞方法，如poll()(带有超时参数)和take()
2.  像poll()这样的非阻塞方法(没有超时参数)

使用阻塞方法，应用程序线程仅在有新的更改事件可用时才开始处理。因此，它不需要继续轮询新事件。

这些方法的详细信息和用法可以在我们的文章中[找到](https://www.baeldung.com/java-nio2-watchservice)。

相比之下，Apache Commons IO 库在FileAlterationListener接口上提供回调方法，当检测到文件系统位置或目录发生更改时调用这些回调方法。

```java
FileAlterationObserver observer = new FileAlterationObserver("pathToDir");
FileAlterationMonitor monitor = new FileAlterationMonitor(POLL_INTERVAL);
FileAlterationListener listener = new FileAlterationListenerAdaptor() {
    @Override
    public void onFileCreate(File file) {
        // code for processing creation event
    }

    @Override
    public void onFileDelete(File file) {
        // code for processing deletion event
    }

    @Override
    public void onFileChange(File file) {
        // code for processing change event
    }
};
observer.addListener(listener);
monitor.addObserver(observer);
monitor.start();
```

### 3.3. 事件溢出

WatchService API 是由操作系统事件驱动的。因此，如果应用程序不能足够快地处理事件，则保存事件的操作系统缓冲区可能会溢出。在这种情况下，事件StandardWatchEventKinds.OVERFLOW被触发，表明某些事件在应用程序可以读取它们之前丢失或丢弃。

这需要在应用程序中正确处理OVERFLOW事件，以确保应用程序可以处理任何可能触发OVERFLOW事件的突发变化事件。

另一方面，Commons IO 库不基于操作系统事件，因此不存在溢出问题。

在每次轮询中，观察者获取目录中的文件列表，并将其与上一次轮询获得的列表进行比较。

1.  如果在上次轮询中找到新文件名，则在侦听器上调用onFileCreate()
2.  如果上次轮询中找到的文件名在上次轮询获得的文件列表中丢失，则在侦听器上调用onFileDelete()
3.  如果找到匹配项，则检查文件是否有任何属性更改，如上次修改日期、长度等。如果检测到更改，则在侦听器上调用onFileChange()

## 4. 总结

在本文中，我们设法强调了这两个 API 的主要区别。