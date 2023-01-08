## 1. 概述

Java 中的进程 API 在Java5 之前非常原始，生成新进程的唯一方法是使用 Runtime.getRuntime ().exec() API。然后在Java5 中，引入了ProcessBuilder API，它支持一种更干净的生成新进程的方式。

Java 9 添加了一种获取有关当前进程和任何派生进程的信息的新方法。

在本文中，我们将研究这两项增强功能。

## 2.当前Java进程信息

我们现在可以通过 API java.lang.ProcessHandle.Info API获取很多关于进程的信息：

-   用于启动进程的命令
-   命令的参数
-   进程开始的时刻
-   它花费的总时间和创建它的用户

我们可以这样做：

```java
private static void infoOfCurrentProcess() {
    ProcessHandle processHandle = ProcessHandle.current();
    ProcessHandle.Info processInfo = processHandle.info();

    log.info("PID: " + processHandle.pid());
    log.info("Arguments: " + processInfo.arguments());
    log.info("Command: " + processInfo.command());
    log.info("Instant: " + processInfo.startInstant());
    log.info("Total CPU duration: " + processInfo.totalCpuDuration());
    log.info("User: " + processInfo.user());
}
```

重要的是要注意java.lang.ProcessHandle.Info是在另一个接口java.lang.ProcessHandle中定义的公共接口。JDK 提供者(Oracle JDK、Open JDK、Zulu 或其他)应该为这些接口提供实现，以便这些实现返回进程的相关信息。

输出取决于操作系统和Java版本。下面是输出的示例：

```java
16:31:24.784 [main] INFO  c.b.j.process.ProcessAPIEnhancements - PID: 22640
16:31:24.790 [main] INFO  c.b.j.process.ProcessAPIEnhancements - Arguments: Optional[[Ljava.lang.String;@2a17b7b6]
16:31:24.791 [main] INFO  c.b.j.process.ProcessAPIEnhancements - Command: Optional[/Library/Java/JavaVirtualMachines/jdk-13.0.1.jdk/Contents/Home/bin/java]
16:31:24.795 [main] INFO  c.b.j.process.ProcessAPIEnhancements - Instant: Optional[2021-08-31T14:31:23.870Z]
16:31:24.795 [main] INFO  c.b.j.process.ProcessAPIEnhancements - Total CPU duration: Optional[PT0.818115S]
16:31:24.796 [main] INFO  c.b.j.process.ProcessAPIEnhancements - User: Optional[username]
```

## 3. 生成的进程信息

也可以获取新生成的进程的进程信息。在这种情况下，在生成进程并获得java.lang.Process的实例之后，我们对其调用toHandle()方法以获取java.lang.ProcessHandle的实例。

其余细节与上一节相同：

```java
String javaCmd = ProcessUtils.getJavaCmd().getAbsolutePath();
ProcessBuilder processBuilder = new ProcessBuilder(javaCmd, "-version");
Process process = processBuilder.inheritIO().start();
ProcessHandle processHandle = process.toHandle();
```

## 4.枚举系统中的实时进程

我们可以列出当前系统中的所有进程，这些进程是当前进程可见的。返回的列表是调用 API 时的快照，因此有可能某些进程在拍摄快照后终止或添加了一些新进程。

为此，我们可以使用java.lang.ProcessHandle接口中可用的静态方法allProcesses()，它返回一个ProcessHandle流：

```java
private static void infoOfLiveProcesses() {
    Stream<ProcessHandle> liveProcesses = ProcessHandle.allProcesses();
    liveProcesses.filter(ProcessHandle::isAlive)
        .forEach(ph -> {
            log.info("PID: " + ph.pid());
            log.info("Instance: " + ph.info().startInstant());
            log.info("User: " + ph.info().user());
        });
}
```

## 5.枚举子进程

有两种变体可以做到这一点：

-   获取当前进程的直接子进程
-   获取当前进程的所有后代

前者是通过使用方法children()实现的，后者是通过使用方法descendants()实现的：

```java
private static void infoOfChildProcess() throws IOException {
    int childProcessCount = 5;
    for (int i = 0; i < childProcessCount; i++) {
        String javaCmd = ProcessUtils.getJavaCmd()
          .getAbsolutePath();
        ProcessBuilder processBuilder
          = new ProcessBuilder(javaCmd, "-version");
        processBuilder.inheritIO().start();
    }

    Stream<ProcessHandle> children = ProcessHandle.current()
      .children();
    children.filter(ProcessHandle::isAlive)
      .forEach(ph -> log.info("PID: {}, Cmd: {}", ph.pid(), ph.info()
        .command()));
    Stream<ProcessHandle> descendants = ProcessHandle.current()
      .descendants();
    descendants.filter(ProcessHandle::isAlive)
      .forEach(ph -> log.info("PID: {}, Cmd: {}", ph.pid(), ph.info()
        .command()));
}
```

## 6. 触发进程终止的相关操作

我们可能想在进程终止时运行一些东西。这可以通过使用java.lang.ProcessHandle接口中的onExit()方法来实现。该方法向我们返回一个[CompletableFuture](https://www.baeldung.com/java-completablefuture) ，它提供了在CompletableFuture完成时触发相关操作的能力。

在这里，CompletableFuture表示进程已经完成，但是进程是否成功完成并不重要。我们调用CompletableFuture的get()方法，等待它完成：

```java
private static void infoOfExitCallback() throws IOException, InterruptedException, ExecutionException {
    String javaCmd = ProcessUtils.getJavaCmd()
      .getAbsolutePath();
    ProcessBuilder processBuilder
      = new ProcessBuilder(javaCmd, "-version");
    Process process = processBuilder.inheritIO()
      .start();
    ProcessHandle processHandle = process.toHandle();

    log.info("PID: {} has started", processHandle.pid());
    CompletableFuture onProcessExit = processHandle.onExit();
    onProcessExit.get();
    log.info("Alive: " + processHandle.isAlive());
    onProcessExit.thenAccept(ph -> {
        log.info("PID: {} has stopped", ph.pid());
    });
}
```

onExit ()方法在java.lang.Process接口中也可用。

## 七、总结

在本教程中，我们介绍了Java 9 中Process API的有趣新增功能，这些新增功能使我们能够更好地控制正在运行和生成的进程。