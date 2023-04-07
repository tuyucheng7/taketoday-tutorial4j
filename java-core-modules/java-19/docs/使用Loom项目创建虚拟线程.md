## 1. 概述

Loom项目仍处于预览阶段，这意味着API可能随时更改。如果你想自己尝试这些示例，它们是使用[抢先体验版本19-loom+4-115(2022/2/13)](https://jdk.java.net/loom/)构建的。

## 2. 虚拟线程简介

Java中的线程只是围绕由操作系统管理和调度的线程的小包装器。Project Loom向Java添加了一种称为虚拟线程的新型线程，这些线程由JVM管理和调度。

要创建平台线程(由操作系统管理的线程)，你需要进行系统调用，而这些调用是昂贵的。要创建一个虚拟线程，你不必进行任何系统调用，从而使这些线程在你需要时创建起来很便宜。这些虚拟线程在载体线程上运行，在幕后，JVM创建了一些平台线程供虚拟线程运行。由于我们没有系统调用和上下文切换，因此我们可以在几个平台线程上运行数千个虚拟线程。

## 3. 创建虚拟线程

创建虚拟线程的最简单方法是使用Thread类。使用Loom，我们获得了一个新的构建器方法和工厂方法来创建虚拟线程。

```java
Runnable task = () -> System.out.println("Hello, world");

// platform thread
(new Thread(task)).start();
Thread platformThread = new Thread(task);
platformThread.start();

// virtual thread
Thread virtualThread = Thread.startVirtualThread(task);
Thread ofVirtualThread = Thread.ofVirtual().start(task);

// virtual thread created with a factory
ThreadFactory factory = Thread.ofVirtual().factory();
Thread virtualThreadFromAFactory = factory.newThread(task);
virtualThreadFromAFactory.start();
```

该示例首先向我们展示了如何创建平台线程，然后是虚拟线程的示例。虚拟线程和平台线程都将Runnable作为参数并返回Thread的实例。此外，启动虚拟线程与我们习惯于通过调用start()方法对平台线程执行的操作相同。

## 4. 使用并发API创建虚拟线程

Loom还向Concurrency API添加了一个新的执行器来创建新的虚拟线程。newVirtualThreadPerTaskExecutor返回一个实现ExecutorService接口的执行器，就像其他执行器一样。让我们从一个使用Executors.newVirtualThreadPerTaskExecutor()方法获取使用虚拟线程的ExecutorService的示例开始。

```java
Runnable task = () -> System.out.println("Hello, world");
ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
executorService.execute(task);
```

如你所见，它看起来与现有的执行器没有什么不同。在此示例中，我们使用Executors.newVirtualThreadPerTaskExecutor()创建一个executorService。这个虚拟线程执行器在一个新的虚拟线程上执行每个任务。由VirtualThreadPerTaskExecutor创建的线程数是无限的。

### 4.1 我可以使用现有的执行器吗？

简短的回答是肯定的；你可以通过为虚拟线程工厂提供虚拟线程来使用现有的执行器。请记住，创建这些执行器是为了池化线程，因为创建平台线程的成本很高。使用将线程与虚拟线程组合在一起的执行程序可能可行，但它有点忽略了虚拟线程的要点。你不必将它们集中在一起，因为它们的创建成本很低。

```java
ThreadFactory factory = Thread.ofVirtual().factory();
Executors.newVirtualThreadPerTaskExecutor();
Executors.newThreadPerTaskExecutor(factory); // same as newVirtualThreadPerTaskExecutor
Executors.newSingleThreadExecutor(factory);
Executors.newCachedThreadPool(factory);
Executors.newFixedThreadPool(1, factory);
Executors.newScheduledThreadPool(1, factory);
Executors.newSingleThreadScheduledExecutor(factory);
```

在第一行，我们创建了一个虚拟线程工厂来处理执行器的线程创建。接下来，我们为每个执行器调用new方法，并为它提供我们刚刚创建的工厂。请注意，使用虚拟线程工厂调用newThreadPerTaskExecutor与直接调用newVirtualThreadPerTaskExecutor相同。

## 5. CompletableFuture

当我们使用CompletableFuture时，我们会尝试在调用get之前尽可能多地链接我们的操作，因为调用它会阻塞线程。使用虚拟线程调用get不会再阻塞(操作系统)线程。没有使用惩罚，你可以随时使用它，而不必编写异步代码。这使得编写和阅读Java代码变得更加容易。

## 6. 结构化并发

由于创建线程的成本很低，Loom项目还为Java带来了结构化并发。使用结构化并发，你可以将线程的生命周期绑定到代码块。在代码块中，你可以创建所需的线程，并在所有线程完成或停止时离开该块。

```java
System.out.println("---------");
try (ExecutorService e = Executors.newVirtualThreadPerTaskExecutor()) {
    e.submit(() -> System.out.println("1"));
    e.submit(() -> System.out.println("2"));
}
System.out.println("---------");
```

Try-with-resources语句可以使用ExecutorService，因为Loom项目使用AutoCloseable接口扩展了Executors。在try中，我们提交所有需要完成的任务，一旦线程完成，我们就离开了try。控制台中的输出将如下所示：

```shell
---------
2
1
---------
```

第二条虚线永远不会在数字之间打印，因为该线程等待try-with-resources完成。

## 7. 总结

在这篇文章中，我们研究了Loom可能会给未来版本的Java带来什么。该项目仍处于预览阶段，API可能会在我们看到它投入生产之前发生变化。但很高兴探索新的API并了解它已经为我们带来了哪些性能改进。