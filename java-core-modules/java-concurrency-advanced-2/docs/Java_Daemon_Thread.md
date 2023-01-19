## 1. 概述

在这篇短文中，我们将了解Java中的守护线程，并了解它们的用途。解释守护线程和用户线程之间的区别。

## 2. 守护和用户线程之间的区别

Java提供了两种类型的线程：用户线程和守护线程。

用户线程是高优先级线程。JVM将等待任何用户线程完成其任务，然后再终止它。

另一方面，守护线程是低优先级线程，其唯一作用是向用户线程提供服务。

由于守护线程是为用户线程服务的，并且只在用户线程运行时才需要，所以一旦所有用户线程都完成了执行，它们不会阻止JVM退出。

这就是为什么通常存在于守护线程中的无限循环不会引起问题的原因，因为任何代码，包括finally块，在所有用户线程完成执行后都不会执行。
因此，不建议将守护线程用于I/O任务。

然而，这条规则也有例外。守护线程中设计不当的代码可能会阻止JVM退出。例如，在运行的守护线程上调用Thread.join()可能会阻止应用程序关闭。

## 3. 守护线程的使用

守护线程对于后台支持的任务非常有用，例如垃圾回收、释放未使用对象的内存以及从缓存中删除不需要的数据。大多数JVM线程都是守护线程。

## 4. 创建守护线程

要将线程设置为守护线程，我们只需调用Thread.setDaemon()。在本例中，我们将使用NewThread类来继承Thread类：

```text
NewThread daemonThread = new NewThread();
daemonThread.setDaemon(true);
daemonThread.start();
```

任何线程都会继承创建它的线程的守护线程状态。因为主线程是一个用户线程，所以在main方法中创建的任何线程在默认情况下都是一个用户线程。

方法setDaemon()只能在创建线程对象且线程尚未启动后调用。在线程运行时尝试调用setDaemon()将引发IllegalThreadStateException：

```java
class DaemonThreadUnitTest {

    @Test
    @Disabled
    void givenUserThread_whenSetDaemonWhileRunning_thenIllegalThreadStateException() {
        NewThread daemonThread = new NewThread();
        daemonThread.start();
        assertThrows(IllegalThreadStateException.class, () -> daemonThread.setDaemon(true));
    }
}
```

## 5. 检查线程是否为守护线程

最后，为了检查线程是否是守护线程，我们只需调用方法isDaemon()：

```java
class DaemonThreadUnitTest {

    @Test
    @Disabled
    void whenCallIsDaemon_thenCorrect() {
        NewThread daemonThread = new NewThread();
        NewThread userThread = new NewThread();
        daemonThread.setDaemon(true);
        daemonThread.start();
        userThread.start();

        assertTrue(daemonThread.isDaemon());
        assertFalse(userThread.isDaemon());
    }
}
```

## 6. 总结

在这个教程中，我们已经了解了守护线程是什么以及它们在一些实际场景中的用途。