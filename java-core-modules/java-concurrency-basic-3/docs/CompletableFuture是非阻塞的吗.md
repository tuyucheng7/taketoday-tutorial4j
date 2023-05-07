## 1. 概述

具有高性能和可用性是现代软件开发的重要组成部分。

实现这一目标的一种方法是通过非阻塞和异步编程。在Java中，CompletableFuture类提供了一种编写非阻塞代码的方法。但它真的是非阻塞的吗？

在本教程中，我们将研究CompletableFuture阻塞和非阻塞的情况。

## 2. CompletableFuture

首先，让我们简单了解一下[CompletableFuture](https://www.baeldung.com/java-completablefuture)类。它是Java 8中引入的一个功能强大的类，作为并发API的一部分。

此外，它实现了[Future](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html)接口并代表了[CompletionStage](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionStage.html)接口的主要实现。因此，它提供了将近50种不同的方法来创建和执行异步计算。

为什么我们首先需要CompletableFuture？使用Future接口，我们只能通过调用get()方法来检索结果。但是，此方法代表阻塞操作。换句话说，它将阻塞当前线程，直到任务的结果可用。

如果我们需要对结果执行额外的操作，我们最终会得到阻塞操作。

另一方面，多亏了CompletionStage，**CompletableFuture提供了将多个可以并发运行的计算链接在一起的能力。此功能允许我们创建一个任务链，在当前任务完成时触发下一个任务**。

此外，我们可以在不阻塞当前线程的情况下指定一旦我们从future获得结果应该发生什么。

CompletableFuture类表示依赖流程中的阶段(其中一个阶段的完成会触发另一个阶段)及其结果。

## 3. 阻塞与非阻塞

接下来，让我们了解阻塞和非阻塞处理之间的区别。

**在阻塞操作中，调用线程在继续执行之前等待另一个线程中的操作完成**：

<img src="../assets/img_1.png">

在这里，任务按顺序执行。线程1被线程2阻塞。换句话说，在线程2完成其任务处理之前，线程1无法继续执行。

我们可以把阻塞处理看作是同步操作。

但是，我们系统中的阻塞操作会导致性能问题，尤其是在需要高可用性和可扩展性的应用程序中。

**相比之下，非阻塞操作允许线程同时执行多个计算，而不必等待每个任务完成**。

当前线程可以继续执行，而其他线程并行执行任务：

<img src="../assets/img_2.png">

在上面的示例中，线程2不会阻塞线程1的执行。此外，两个线程都同时运行它们的任务。

除了提高性能之外，我们还可以决定在非阻塞操作完成执行后如何处理结果。

## 4. CompletableFuture和非阻塞操作

使用CompletableFuture的主要优点是它能够将多个任务链接在一起，这些任务将在不阻塞当前线程的情况下执行。**因此，我们可以说CompletableFuture是非阻塞的**。

此外，它还提供了几种允许我们以非阻塞方式执行任务的方法，包括：

-   supplyAsync()：异步执行任务并返回表示结果的CompletableFuture
-   thenApply()：将函数应用于上一个任务的结果并返回表示转换结果的CompletableFuture
-   thenCompose()：执行一个返回CompletableFuture的任务，并返回一个表示嵌套任务结果的CompletableFuture
-   allOf()：并行执行几个任务，并返回一个表示所有任务完成的CompletableFuture

接下来，让我们看一个简单的例子。例如，假设我们有两个任务想以非阻塞方式执行：

```java
CompletableFuture.supplyAsync(() -> "Tuyucheng")
    .thenApply(String::length)
    .thenAccept(s -> logger.info(String.valueOf(s)));
```

任务完成后，它将在标准输出中打印数字8。

计算在后台运行并返回future。如果我们有多个依赖操作，则每个操作都由阶段表示。一个阶段完成后，它会触发其他依赖阶段的计算。

## 5. CompletableFuture什么时候阻塞？

尽管CompletableFuture用于执行非阻塞操作，但在某些情况下它仍然会阻塞当前线程。

在异步通信中，我们通常有一个回调机制来检索计算结果。但是，CompletableFuture不会在其完成时通知我们。

如果需要，我们可以使用get()方法在调用线程中检索结果。

**然而，我们需要注意get()方法使用阻塞处理返回结果**。如果需要，它会等待计算完成，然后返回结果。

因此，我们最终会阻塞当前线程，直到future完成：

```java
CompletableFuture<String> completableFuture = CompletableFuture
    .supplyAsync(() -> "Tuyucheng")
    .thenApply(String::toUpperCase);

assertEquals("TUYUCHENG", completableFuture.get());
```

同样，调用join()方法也会阻塞当前线程：

```java
CompletableFuture<String> completableFuture = CompletableFuture
    .supplyAsync(() -> "Blocking")
    .thenApply(s -> s + " Operation")
    .thenApply(String::toLowerCase);

assertEquals("blocking operation", completableFuture.join());
```

这两种方法之间的主要区别在于，如果future异常完成，join()方法不会抛出受检的异常。

此外，我们可以在获取结果之前调用isDone()方法来检查future是否已完成。

但是，当需要在调用线程中获取计算结果时，我们可以创建CompletableFuture，在当前线程中做其他工作，然后调用get()或join()方法。通过给它更多的时间，future更有可能在我们得到结果之前完成计算。但是仍然不能保证检索不会最终阻塞线程。

## 6. 总结

在本文中，我们研究了CompletableFuture非阻塞和非阻塞的情况。

综上所述，CompletableFuture大多数时候都是非阻塞的。但是，如果我们调用get()或join()方法来检索结果，它们将阻塞当前线程。