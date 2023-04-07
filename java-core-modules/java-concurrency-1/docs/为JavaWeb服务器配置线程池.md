## 1. 概述

在本教程中，我们将了解Java Web应用程序服务器(例如Apache Tomcat、Glassfish Server和Oracle Weblogic)的线程池配置。

## 2. 服务器线程池

**[服务器线程池](https://www.baeldung.com/thread-pool-java-and-guava)由Web应用程序服务器为已部署的应用程序使用和管理**。这些线程池存在于Web容器或Servlet之外，因此它们不受相同上下文边界的约束。

与应用程序线程不同，服务器线程甚至在部署的应用程序停止后仍然存在。

## 3. Apache Tomcat

首先，我们可以通过server.xml中的Executor配置类来配置Tomcat的服务器线程池：

```xml
<Executor name="tomcatThreadPool" namePrefix="catalina-exec-" maxThreads="150" minSpareThreads="25"/>
```

minSpareThreads是最小的池，包括在启动时。**maxThreads是服务器开始排队请求之前池的最大数量**。

Tomcat分别默认为25和200。在此配置中，我们使线程池比默认值小了一点。

### 3.1 嵌入式Tomcat

同样，我们可以通过设置应用程序属性来更改Spring Boot的[嵌入式Tomcat服务器](https://www.baeldung.com/spring-boot-configure-tomcat)以配置线程池：

```properties
server.tomcat.max-threads=250
```

从Boot 2.3开始，该属性已更改为：

```properties
server.tomcat.threads.max=250
```

## 4. Glassfish

接下来，让我们更新我们的Glassfish服务器。

与Tomcat的XML配置文件server.xml不同，Glassfish使用管理命令。根据提示，我们运行：

```shell
create-threadpool
```

我们可以向create-threadpool添加标志maxthreadpoolsize和minthreadpoolsize。它们的功能类似于Tomcat minSpareThreads和maxThreads：

```shell
--maxthreadpoolsize 250 --minthreadpoolsize 25
```

我们还可以指定线程在返回池之前可以空闲多长时间：

```shell
--idletimeout=2
```

然后，我们在最后提供线程池的名称：

```shell
asadmin> create-threadpool --maxthreadpoolsize 250 --minthreadpoolsize 25 --idletimeout=2 threadpool-1
```

## 5. Weblogic

Oracle Weblogic使我们能够使用WorkManager更改自调整线程池。

与线程队列类似，WorkManager 将线程池作为队列进行管理。但是，WorkManager会根据实时吞吐量添加动态线程。Weblogic定期对吞吐量进行分析以优化线程利用率。

这对我们意味着什么？这意味着虽然我们可能会更改线程池，但Web服务器最终将决定是否生成新线程。

我们可以在Weblogic管理控制台中配置我们的线程池：

[![Weblogic 屏幕 1](https://www.baeldung.com/wp-content/uploads/2020/02/Weblogic_screen_1.jpg)](https://www.baeldung.com/wp-content/uploads/2020/02/Weblogic_screen_1.jpg)

更新自调整最小线程池大小和自调整线程最大池大小值设置WorkManager的最小和最大边界。

注意Stuck Thread Max Time和Stuck Thread Timer Interval值。这些有助于WorkManager对卡住的线程进行分类。

有时，长时间运行的进程可能会导致卡住线程的累积。WorkManager将从线程池中生成新线程以进行补偿。对这些值的任何更新都可能会延长允许流程完成的时间。

**线程卡住可能表示代码有问题，因此最好始终解决根本原因而不是使用变通方法**。

## 6. 总结

在这篇简短的文章中，我们研究了配置应用程序服务器线程池的多种方法。

虽然应用程序服务器管理各种线程池的方式有所不同，但它们是使用相似的概念配置的。

最后，让我们记住，更改Web服务器的配置值并不是针对性能不佳的代码和糟糕的应用程序设计的适当修复。