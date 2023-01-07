## 1. 概述

使用Java进行端口扫描是一种枚举目标机器的开放或活动端口的方法。目标主要是列出开放的端口，以便了解当前正在运行的应用程序和服务。

在本教程中，我们将解释如何使用Java开发一个简单的端口扫描应用程序，我们可以使用它来扫描主机的开放端口。

## 2. 什么是电脑端口？

计算机端口是一个逻辑实体，可以将特定服务与连接相关联。此外，端口由 1 到 65535 之间的整数标识。按照惯例，前 1024 位保留用于标准服务，例如：

-   端口 20：FTP
-   端口 23：远程登录
-   端口 25：SMTP
-   端口 80：HTTP

端口扫描器的想法是创建一个TCP Socket并尝试连接到特定端口。如果成功建立连接，那么我们会将此端口标记为打开，否则，我们会将其标记为关闭。

但是，在 65535 个端口中的每一个上建立连接最多可能需要每个端口 200 毫秒。这听起来时间不多，但加起来，一个一个扫描一台主机的所有端口，需要相当长的时间。

为了解决性能问题，我们将使用[多线程](https://www.baeldung.com/cs/multithreaded-algorithms)方法。与尝试按顺序连接到每个端口相比，这可以大大加快该过程。

## 3.实施

为了实现我们的程序，我们创建了一个函数portScan()，它有两个参数作为输入：

-   ip :要扫描的IP地址；它相当于本地主机的 127.0.0.1
-   nbrPortMaxToScan：要扫描的最大端口数；如果我们想扫描所有端口，这个数字相当于 65535

### 3.1. 执行

让我们看看我们的portScan()方法是什么样的：

```java
public void runPortScan(String ip, int nbrPortMaxToScan) throws IOException {
        ConcurrentLinkedQueue openPorts = new ConcurrentLinkedQueue<>();
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        AtomicInteger port = new AtomicInteger(0);
        while (port.get() < nbrPortMaxToScan) {
                final int currentPort = port.getAndIncrement();
                executorService.submit(() -> {
                        try {
                                Socket socket = new Socket();
                                socket.connect(new InetSocketAddress(ip, currentPort), timeOut);
                                socket.close();
                                openPorts.add(currentPort);
                                System.out.println(ip + " ,port open: " + currentPort);
                        } catch (IOException e) {
                                System.err.println(e);
                        }
                });
        }
        executorService.shutdown();
        try {
                executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
                throw new RuntimeException(e);
        }
        List openPortList = new ArrayList<>();
        System.out.println("openPortsQueue: " + openPorts.size());
        while (!openPorts.isEmpty()) {
                openPortList.add(openPorts.poll());
        }
        openPortList.forEach(p -> System.out.println("port " + p + " is open"));
}
```

我们的方法返回一个包含所有开放端口的列表。为此，我们创建一个新的Socket对象用作两个主机之间的连接器。如果连接成功建立，那么我们假设端口是打开的，在这种情况下我们继续下一行。另一方面，如果连接失败，则我们假设端口已关闭并抛出[SocketTimeoutException ](https://www.baeldung.com/java-socket-connection-read-timeout)，我们将被抛至异常捕获块。

### 3.2 多线程

为了优化扫描目标机器所有 65535 个端口所需的时间，我们将同时运行我们的方法。我们使用 ExecutorService [，](https://www.baeldung.com/java-executor-service-tutorial)它封装了一个线程池和一个要执行的任务队列。池中的所有线程仍在运行。

该服务检查队列中是否要处理任务，如果是，则撤回任务并执行。任务执行后，线程再次等待服务从队列中为其分配新任务。

此外，我们使用具有 10 个Thread 的FixedThreadPool，这意味着该程序将最多并行运行 10 个线程。我们可以根据我们的机器配置和容量调整这个池的大小。

## 4。总结

在本快速教程中，我们解释了如何使用 Java使用Socket和多线程方法开发一个简单的端口扫描应用程序。