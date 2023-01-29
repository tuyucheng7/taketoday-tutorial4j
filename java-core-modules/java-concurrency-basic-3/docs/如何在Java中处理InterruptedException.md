## 1. 概述

在本文中，我们将探讨Java的InterruptedException。首先，我们简要回顾一下线程的生命周期。接下来，我们会看到在多线程应用程序中可能会出现的中断异常。最后，我们将了解如何处理此异常。

## 2. 多线程基础

在介绍中断之前，让我们回顾一下多线程。多线程是同时执行两个或多个线程的进程。Java应用程序从一个与main()方法关联的线程开始，称为主(main)线程。然后，这个主线程可以启动其他线程。

线程是轻量级的，这意味着它们在相同的内存空间中运行。因此，他们可以很容易地相互通信。让我们看看[线程的生命周期](https://www.baeldung.com/java-thread-lifecycle)：

<img src="../assets/img.png">

一旦我们创建了一个新线程，它就处于NEW状态。它将保持此状态，直到程序使用start()方法启动线程。

对线程调用start()方法会使其处于RUNNABLE状态。处于此状态的线程正在运行或准备运行。

当一个线程正在等待监视器锁并试图访问被其他线程锁定的代码时，它将进入BLOCKED状态。

线程可以通过各种事件进入WAITING状态，例如调用wait()方法。在这种状态下，一个线程正在等待来自另一个线程的唤醒。

当线程完成执行或异常终止时，它将以TERMINATED状态结束。**线程可以被中断，当线程被中断时，它将抛出InterruptedException**。

在接下来的部分中，我们将详细了解InterruptedException，并学习如何解决它。

## 3. 什么是InterruptedException？

当线程在等待、睡眠或其他状态下被中断时，就会抛出[InterruptedException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/InterruptedException.html)。换句话说，一些代码在我们的线程上调用了interrupt()方法。这是一个[受检异常](https://www.baeldung.com/java-checked-unchecked-exceptions)，Java中的许多阻塞操作都会抛出它。

### 3.1 中断

**中断系统的目的是提供一个定义良好的框架，允许线程中断其他线程中的任务(可能耗时的任务)**。考虑中断的一个好方法是，它实际上并不会中断正在运行的线程，只是请求线程在下一个方便的时机自行中断。

### 3.2 阻塞和可中断方法

线程可能由于以下几个原因而阻塞：等待从Thread.sleep()中唤醒，等待获取锁，等待I/O完成，或等待另一个线程中的计算结果，等等。

**InterruptedException通常由所有阻塞方法引发，以便可以处理它并执行纠正措施**。Java中有一些抛出[InterruptedException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/class-use/InterruptedException.html)的方法。这些包括Thread.sleep()，Thread.join()，Object类的wait()方法，以及BlockingQueue的put()和take()方法等等。

### 3.3 Thread中的中断方法

让我们快速了解一下Thread类中处理中断的一些关键方法：

```java
public void interrupt() { ... }
public boolean isInterrupted() { ... }
public static boolean interrupted() { ... }
```

**Thread提供中断线程的interrupt()方法，要知道线程是否被中断，可以使用isInterrupted()方法**。有时，我们可能希望测试当前线程是否已中断，如果是，则立即抛出此异常。在这里，我们可以使用interrupted()方法：

```java
if (Thread.interrupted()) {
    throw new InterruptedException();
}
```

### 3.4 中断状态标志

中断机制使用一个称为中断状态的标志来实现。**每个线程都有一个表示其中断状态的布尔属性。调用Thread.interrupt()方法设置此标志。当线程通过调用静态方法Thread.interrupted()来检查中断时，中断状态被清除**。

```java
public class Thread implements Runnable {
    private volatile boolean interrupted;
}
```

为了响应中断请求，我们必须处理InterruptedException。

## 4. 如何处理InterruptedException

需要注意的是，线程调度依赖于JVM。这是很自然的，因为JVM是一个虚拟机，需要本地操作系统资源来支持多线程。因此，我们不能保证我们的线程永远不会被中断。

中断表示线程应该停止正在做的事情并执行其他操作。更具体地说，如果我们编写一些由[Executor](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/Executor.html)或其他线程管理机制执行的代码，那么我们需要确保我们的代码能够及时响应中断。否则，我们的应用程序可能会导致[死锁](https://www.baeldung.com/java-deadlock-livelock)。

有一些处理InterruptedException的实用策略，让我们来看看它们。

### 4.1 传播InterruptedException

我们可以允许InterruptedException向调用堆栈上传播，例如，通**过依次向每个方法添加一个throws子句，并让调用方确定如何处理中断。这可能涉及我们不捕获异常或捕获并重新抛出异常**。让我们用一个例子来实现这一点：

```java
public static void propagateException() throws InterruptedException {
    Thread.sleep(1000);
    Thread.currentThread().interrupt();
    if (Thread.interrupted()) {
        throw new InterruptedException();
    }
}
```

这里，我们检查线程是否被中断，如果是，我们抛出InterruptedException。现在，让我们调用propagateException()方法：

```java
public static void main(String... args) throws InterruptedException {
    propagateException();
}
```

当我们尝试运行这段代码时，我们将收到一个带有堆栈跟踪信息的InterruptedException：

```shell
Exception in thread "main" java.lang.InterruptedException
    at cn.tuyucheng.taketoday.concurrent.interrupt.InterruptExample.propagateException(InterruptExample.java:16)
    at cn.tuyucheng.taketoday.concurrent.interrupt.InterruptExample.main(InterruptExample.java:7)
```

**虽然这是响应异常最明智的方式，但有时我们不能抛出它**。例如，当我们的代码是Runnable的一部分时。在这种情况下，我们必须捕获它并恢复状态。我们将在下一节中介绍如何处理这种情况。

### 4.2 恢复中断

有些情况下，我们不能传播InterruptedException。例如，假设我们的任务由一个Runnable或重写一个不抛出任何受检异常的方法定义。在这种情况下，我们可以保留中断。执行此操作的标准方法是恢复中断状态。

**我们可以再次调用interrupt()方法(它会将标志设置回true)，这样调用堆栈上层的代码就可以看到发出了中断**。例如，让我们中断一个线程并尝试访问其中断状态：

```java
public class InterruptExample extends Thread {

    public static Boolean restoreTheState() {
        InterruptExample thread1 = new InterruptExample();
        thread1.start();
        thread1.interrupt();
        return thread1.isInterrupted();
    }
}
```

下面是处理中断并恢复中断状态的run()方法：

```java
public void run() {
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();  //set the flag back to true
    }
}    
```

最后，让我们测试一下状态：

```java
assertTrue(InterruptExample.restoreTheState());
```

尽管Java异常涵盖了所有异常情况和条件，但我们可能希望抛出代码和业务逻辑特有的自定义异常。在这里，我们可以创建自定义异常来处理中断。

### 4.3 自定义异常处理

[自定义异常](https://www.baeldung.com/java-new-custom-exception)提供了添加不属于标准Java异常的属性和方法的灵活性。因此，**根据具体情况，以自定义方式处理中断是完全有效的**。

我们可以完成额外的工作，使应用程序能够优雅地处理中断请求。例如，当一个线程正在睡眠或等待一个I/O操作，并且它接收到中断时，我们可以在终止线程之前关闭任何资源。

让我们创建一个名为CustomInterruptedException的自定义受检异常：

```java
public class CustomInterruptedException extends Exception {

    CustomInterruptedException(String message) {
        super(message);
    }
}
```

**当线程被中断时，我们可以抛出CustomInterruptedException**：

```java
public static void throwCustomException() throws Exception {
    Thread.sleep(1000);
    Thread.currentThread().interrupt();
    if (Thread.interrupted()) {
        throw new CustomInterruptedException("This thread was interrupted");
    }
}
```

让我们看看如何检查异常是否与正确的消息一起抛出：

```java
@Test
void whenThrowCustomException_thenContainsExpectedMessage() {
    Exception exception = assertThrows(CustomInterruptedException.class, InterruptExample::throwCustomException);
    String expectedMessage = "This thread was interrupted";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
}
```

**同样，我们可以处理异常并恢复中断状态**：

```java
public static Boolean handleWithCustomException() throws CustomInterruptedException{
    try {
        Thread.sleep(1000);
        Thread.currentThread().interrupt();
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new CustomInterruptedException("This thread was interrupted...");
    }
    return Thread.currentThread().isInterrupted();
}
```

我们可以通过检查中断状态来测试代码，以确保它返回true：

```java
@Test
void whenHandleWithCustomException_thenReturnsTrue() throws CustomInterruptedException {
    assertTrue(InterruptExample.handleWithCustomException());
}
```

## 5. 总结

在本教程中，我们看到了处理InterruptedException的不同方法。如果处理得当，我们可以平衡应用程序的响应性和健壮性。