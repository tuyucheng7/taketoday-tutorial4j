## 1. 概述

Java 19将引入正在孵化的结构化并发项目(JEP 428)。顾名思义，该项目将为Java添加结构化并发功能。这篇文章将介绍如何使用新的API来处理多线程代码。

## 2. 为什么要使用结构化并发

当你在Java中创建一个新的线程时，会对操作系统进行系统调用，告诉它创建一个新的系统线程。创建一个系统线程是昂贵的，因为调用会占用很多时间，而且每个线程都会占用一些内存。你的线程也共享同一个CPU，因此你不希望它们阻塞它，导致其他线程不必要地等待。

你可以使用异步编程来防止这种情况发生。你启动一个线程并告诉它在数据到达时做什么。在数据可用之前，其他线程可以使用CPU资源来完成它们的任务。在下面的示例中，我们使用CompletableFuture来获取一些数据并告诉它在数据可用时将其打印到控制台。

```java
CompletableFuture.supplyAsync(() -> "some data")
    .thenAccept(System.out::println);
```

异步编程工作正常，但还有另一种工作和思考并发的方法，称为结构化并发。结构化并发是用于开发并发应用程序的Java增强提案(JEP)。它旨在使在Java中编写和调试多线程代码变得更加容易。

## 3. 什么是结构化并发

通过JEP 428，我们获得了一个“结构化并发”模型来处理和思考线程。结构化并发背后的想法是让线程的生命周期与结构化编程中的代码块一样工作。例如，在像Java这样的结构化编程语言中，如果你在方法A中调用方法B，则必须先完成方法B，然后才能退出方法A。方法B的生命周期不能超过方法A的生命周期。

对于结构化并发，我们需要与结构化编程相同的规则。当你在虚拟线程Y中创建虚拟线程X时，线程X的生命周期不能超过线程Y的生命周期。结构化并发使线程的工作和思考变得更加容易。例如，当你停止父线程Y时，它的所有子线程也将被取消，因此你不必担心失控线程仍在运行。该模式的关键是避免火灾并忘记并发。

<img src="../assets/img_2.png">

线程Y启动一个新的线程X；两者彼此独立工作，但在线程Y完成之前，它必须等待线程X完成其工作。让我们看看它在Java中是什么样子的！

## 4. Invoke All模式-使用结构化并发

结构化并发将线程的生命周期绑定到创建它们的代码块。绑定是通过在try-with-recourses中使用StructuredTaskScope来完成的。在这个try中，你有一个用于fork新子线程的作用域对象。

在下面的示例中，我们使用new StructuredTaskScope.ShutdownOnFailure()创建一个作用域。如果其中一个子线程在执行期间抛出异常，则这种类型的作用域会关闭每个正在运行的子线程。只有当所有创建的子线程都成功完成时，我们才会得到结果。此行为看起来类似于ExecutorService的invokeAll方法。

```java
String getDog() throws ExecutionException, InterruptedException {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<String>  name  = scope.fork(this::getName);
        Future<String> breed = scope.fork(this::dogBreed);

        scope.join();
        scope.throwIfFailed();

        return "it's name is:" + name.resultNow() + ", and is a " + breed.resultNow();
    }
}

String getName() {
    return "Max";
}

String dogBreed(){
    return "Golden retriever";
}
```

## 5. Invoke Any模式-使用结构化并发

与等待每个子线程完成的Invoke All模式不同。相反，Invoke Any模式将返回第一个线程完成并关闭其余子线程的结果。下面的示例看起来与前一个类似，但有一些细微的变化。现在我们像这样new StructuredTaskScope.ShutdownOnSuccess<String\>()初始化StructuredTaskScope，我们不为future引用创建持有者。

只有在调用scope.join()之后，我们才能通过调用scope.result()来获取第一个完成线程的结果。下面的示例将返回“result: Golden retriever”。

```java
String getDog() throws ExecutionException, InterruptedException {
    try (var scope = new StructuredTaskScope.ShutdownOnSuccess<String>()) {
        scope.fork(this::getName);
        scope.fork(this::dogBreed);

        scope.join();

        return "result: " + scope.result();
    }
}

String getName() throws InterruptedException {
    Thread.sleep(5000);
    return "Max";
}

String dogBreed(){
    return "Golden retriever";
}
```

## 6. 异常和结构化并发

我们将回顾Invoke all模式的示例，看看抛出异常时会发生什么。调用dogBreed()的第二个子线程将在第一个子线程仍在运行时抛出异常。抛出异常时，StructuredTaskScope将关闭它创建的每个子线程。要获取底层异常，你必须在作用域对象上调用throwIfFailed()方法。请记住，你只能在join()调用后才能调用throwIfFailed()。

如果不调用throwIfFailed()方法，你将在控制台中看到的异常将是IllegalStateException。这很令人困惑，因为我们在dogBreed()方法中抛出了一个RuntimeException。你需要调用throwIfFailed方法以在控制台中显示RuntimeException。

```java
String getDog() throws ExecutionException, InterruptedException {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<String>  name  = scope.fork(this::getName);
        Future<String> breed = scope.fork(this::dogBreed);

        scope.join();
        scope.throwIfFailed();

        return "it's name is:" + name.resultNow() + ", and is a " + breed.resultNow();
    }
}

String getName() throws InterruptedException {
    Thread.sleep(5000);
    return "Max";
}

String dogBreed(){
    throw new RuntimeException();
}
```

## 7. 总结

这篇文章探讨了结构化并发对Java语言的好处，以及它是如何在Java 19 EA和JEP 428中实现的。我们讨论了如何为线程创建不同类型的作用域，以及当一个作用域内的虚拟线程之一引发错误时会发生什么。