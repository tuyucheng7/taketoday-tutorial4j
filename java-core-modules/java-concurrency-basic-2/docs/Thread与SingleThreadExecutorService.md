## 1. 概述

线程和Executor框架是用于在Java中并行执行代码的两种机制，这提高了应用程序的性能。Executor框架提供了不同种类的线程池，其中一个池仅包含一个工作线程。

在本教程中，**我们将了解线程和具有单个工作线程的ExecutorService之间的区别**。

## 2. 线程

线程是具有独立执行路径的轻量级进程，它用于并行执行任务。因此，可以有多个线程同时运行而不会相互干扰。

Thread对象执行Runnable任务。

让我们看看如何创建线程，**我们可以通过**[扩展Thread类或实现Runnable接口](https://www.baeldung.com/java-runnable-vs-extending-thread)**来创建线程**。

让我们通过扩展Thread类来创建一个线程：

```java
public class CustomThread extends Thread {
	// override the run() method to provide custom implementation

	public static void main(String[] args) {
		CustomThread t1 = new CustomThread();
		t1.start();
	}
}
```

在上面的示例中，CustomThread类扩展了Thread类。在main()方法中，我们创建了CustomThread类的对象，然后调用了它的start()方法，它开始执行线程。

下面我们来看一个实现Runnable接口创建线程的例子：

```java
public class TestClass implements Runnable {
	// implement the run() method of Runnable interface

	public static void main(String[] args) {
		TestClass testClassRef = new TestClass();
		Thread t1 = new Thread(testClassRef);
		t1.start();
	}
}
```

在上面的示例中，TestClass实现了Runnable接口。我们在Thread类的构造函数中传递了TestClass对象的引用，然后，我们调用start()方法。这反过来又调用了TestClass实现的run()方法。

## 3. Executor框架

现在我们将学习[Executor框架](https://www.baeldung.com/java-executor-service-tutorial)，它是在JDK 1.5中引入的。**它是一个多线程框架，用于维护工作线程池并对其进行管理**，任务在队列中提交，然后由这些工作线程执行。

**它消除了在代码中显式创建线程的开销，相反，它会重用池中的线程来异步执行任务**。

现在让我们看看由Executor框架维护的不同种类的[线程池](https://www.baeldung.com/thread-pool-java-and-guava)。

### 3.1 固定线程池

**该池包含固定数量的线程**，我们在创建池期间指定线程数。如果发生异常并且线程终止，则会创建一个新线程。

让我们看看如何创建一个固定的线程池：

```java
ExecutorService executorService = Executors.newFixedThreadPool(5);
```

在上面的代码片段中，我们创建了一个具有5个工作线程的固定线程池。

### 3.2 缓存线程池

**该线程池在需要时创建新线程**，如果没有线程可用于执行提交的任务，则将创建一个新线程。

以下是我们创建缓存线程池的方法：

```java
ExecutorService executorService = Executors.newCachedThreadPool();
```

在缓存线程池中，我们没有指定池大小，这是因为它在没有线程可用于执行提交的任务时创建新线程，它还会在已创建的线程可用时重复使用它们。

### 3.3 调度线程池

**此线程池在给定延迟后或定期运行任务**。

下面是我们如何创建一个调度线程池：

```java
ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
```

在上面的代码片段中，整数参数是核心池大小，它表示要保留在池中的线程数，即使它们处于空闲状态。

### 3.4 单线程池

**这个池只包含一个线程，它按顺序执行提交的任务。如果发生异常并且线程终止，则会创建一个新线程**。

下面的代码片段显示了如何创建单线程池：

```java
ExecutorService executorService = Executors.newSingleThreadExecutor();
```

在这里，Executors类的静态方法newSingleThreadExecutor()创建了由单个工作线程组成的ExecutorService。

## 4. 线程与单线程ExecutorService

我们可能想知道如果单线程池ExecutorService只包含一个线程，那么它与显式创建一个线程并使用它来执行任务有何不同。

现在让我们探讨Thread和只有一个工作线程的ExecutorService之间的区别，以及何时使用哪个。

### 4.1 任务处理

**线程只能处理Runnable任务，而单线程ExecutorService可以同时执行Runnable和Callable任务**。因此，使用它，我们也可以运行可以返回一些值的任务。

ExecutorService接口中的submit()方法接收Callable任务或Runnable任务并返回Future对象，该对象表示异步任务的结果。

此外，一个线程只能处理一个任务并退出，但是单线程ExecutorService可以处理一系列任务并按顺序执行它们。

### 4.2 线程创建开销

创建线程会产生开销，例如，JVM需要分配内存。当在代码中重复创建线程时，它会影响性能。但是在单线程ExecutorService的情况下，会重用同一个工作线程。因此，**它可以防止创建多个线程的开销**。

### 4.3 内存消耗

线程对象占用大量内存，因此，如果我们为每个异步任务创建线程，就会导致OutOfMemoryError。但是在单线程ExecutorService中，可以重复使用同一个工作线程，从而减少内存消耗。

### 4.4 资源释放

线程在执行完成后释放资源，但是对于ExecutorService，我们需要关闭服务，否则JVM无法关闭。**像shutdown()和shutdownNow()这样的方法可用于关闭ExecutorService**。

## 5. 总结

在本文中，我们了解了线程、Executor框架和不同类型的线程池，我们还看到了线程和单线程ExecutorService之间的差异。

我们了解到，如果有任何重复的作业或者有很多异步任务，那么ExecutorService是更好的选择。