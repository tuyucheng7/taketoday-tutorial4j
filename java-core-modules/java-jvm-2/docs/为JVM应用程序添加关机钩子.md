## 1. 概述

启动服务通常很容易。然而，有时我们需要有一个优雅地关闭一个的计划。

在本教程中，我们将了解 JVM 应用程序终止的不同方式。然后，我们将使用JavaAPI 来管理 JVM 关闭挂钩。请 [参阅本文](https://www.baeldung.com/runtime-getruntime-halt-vs-system-exit-in-java)以了解有关在Java应用程序中关闭 JVM 的更多信息。

## 2. JVM 关闭

JVM 可以通过两种不同的方式关闭：

1.  受控过程
2.  突兀的方式

在以下任一情况下，受控进程会关闭 JVM：

-   最后一个非[守护](https://www.baeldung.com/java-daemon-thread)线程终止。例如，当主线程退出时，JVM 启动它的关闭进程
-   从操作系统发送中断信号。例如，通过按 Ctrl + C 或注销操作系统
-   从Java代码调用 System.exit() 

虽然我们都力求正常关闭，但有时 JVM 可能会突然和意外地关闭。JVM 在以下情况下突然关闭：

-   从操作系统发送终止信号。例如，通过发出 kill -9 <jvm_pid>
-   从Java代码调用 Runtime.getRuntime().halt() 
-   主机操作系统意外死机，例如，电源故障或操作系统崩溃

## 3.关机挂钩

JVM 允许注册函数在它完成关闭之前运行。这些功能通常是释放资源或其他类似内务管理任务的好地方。在 JVM 术语中，这些函数称为shutdown hooks。

关闭挂钩基本上是已初始化但未启动的线程。当 JVM 开始其关闭过程时，它将以未指定的顺序启动所有已注册的挂钩。运行所有钩子后，JVM 将停止。

### 3.1. 添加挂钩

为了添加关闭挂钩，我们可以使用 [Runtime.getRuntime().addShutdownHook()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Runtime.html#addShutdownHook(java.lang.Thread)) 方法：

```java
Thread printingHook = new Thread(() -> System.out.println("In the middle of a shutdown"));
Runtime.getRuntime().addShutdownHook(printingHook);
```

在这里，我们只是在 JVM 自行关闭之前向标准输出打印一些内容。如果我们像下面这样关闭 JVM：

```java
> System.exit(129);
In the middle of a shutdown
```

然后我们将看到挂钩实际上将消息打印到标准输出。

JVM 负责启动钩子线程。因此，如果给定的钩子已经启动，Java 将抛出异常：

```java
Thread longRunningHook = new Thread(() -> {
    try {
        Thread.sleep(300);
    } catch (InterruptedException ignored) {}
});
longRunningHook.start();

assertThatThrownBy(() -> Runtime.getRuntime().addShutdownHook(longRunningHook))
  .isInstanceOf(IllegalArgumentException.class)
  .hasMessage("Hook already running");

```

显然，我们也不能多次注册一个钩子：

```java
Thread unfortunateHook = new Thread(() -> {});
Runtime.getRuntime().addShutdownHook(unfortunateHook);

assertThatThrownBy(() -> Runtime.getRuntime().addShutdownHook(unfortunateHook))
  .isInstanceOf(IllegalArgumentException.class)
  .hasMessage("Hook previously registered");
```

### 3.2. 取下挂钩

Java 提供了一个双 删除 方法来在注册后删除一个特定的关闭钩子：

```java
Thread willNotRun = new Thread(() -> System.out.println("Won't run!"));
Runtime.getRuntime().addShutdownHook(willNotRun);

assertThat(Runtime.getRuntime().removeShutdownHook(willNotRun)).isTrue();

```

removeShutdownHook [()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Runtime.html#removeShutdownHook(java.lang.Thread))方法 在成功删除关闭挂钩时返回true 。

### 3.3. 注意事项

JVM 仅在正常终止的情况下运行关闭挂钩。因此，当外部力量突然杀死 JVM 进程时，JVM 将没有机会执行关闭挂钩。此外，从Java代码中停止 JVM 也会产生相同的效果：

```java
Thread haltedHook = new Thread(() -> System.out.println("Halted abruptly"));
Runtime.getRuntime().addShutdownHook(haltedHook);
        
Runtime.getRuntime().halt(129);

```

[halt](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Runtime.html#halt(int))方法强行终止当前运行的 JVM 。因此，已注册的关闭挂钩将没有机会执行。

## 4。总结

在本教程中，我们研究了 JVM 应用程序可能终止的不同方式。然后，我们使用一些运行时 API 来注册和注销关闭挂钩。