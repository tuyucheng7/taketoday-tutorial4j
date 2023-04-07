## 1. 概述

InvokeAll是ExecutorService上的一个方法，用于同时启动多个提交的任务。ExecutorService将使用其池中的平台线程来运行提交的任务。除了使用这些昂贵且占用大量资源的平台线程，我们还可以使用虚拟线程来运行提交给ExecutorService的任务。本文将介绍使用虚拟线程、结构化并发和平台线程实现invokeAll方法的所有方法。

## 2. 虚拟线程与InvokeAll

我们要看的第一个例子使用虚拟线程。我们有一个try-with-recourse语句，该语句创建一个为每个任务创建一个虚拟线程的执行器。在第8行，使用tasks列表调用invokeAll方法。

启动所有任务后，我们创建一个流，该流将等待结果并返回它们。

```java
static List<String> invokeAllWithVirtualThreads() throws ExecutionException, InterruptedException {
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

        var tasks = new ArrayList<Callable<String>>();
        tasks.add(() -> getStringFromResourceA());
        tasks.add(() -> getStringFromResourceB());

        List<Future<String>> futures = executor.invokeAll(tasks);

        return futures.stream().map(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
```

## 3. 结构化并发与InvokeAll

结构化并发是Java中管理线程生命周期的一种新方法，生命周期通过StructuredTaskScope进行控制。主要有两种作用域，一种具有InvokeAny行为，一种具有InvokeAll行为。对于本文，我们将实现InvokeAll。要创建一个等待所有线程运行完毕的StructuredTaskScope，我们需要调用ShutdownOnFailure方法。

我们需要调用fork方法来为作用域添加一个任务来执行。第7行的scope.join()方法调用将阻塞，直到所有线程都完成。在第10行，我们在获取结果之前检查是否抛出了异常。

因为我们在作用域内，所以我们将使用新的resultNow方法从future中获取结果，而不是调用get。这是新的首选方法，因为我们已经等待线程完成。

```java
static String invokeAllWithStructuredConcurrency() throws ExecutionException, InterruptedException {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future<String> futureA = scope.fork(() -> getStringFromResourceA());
        Future<String> futureB = scope.fork(() -> getStringFromResourceB());

        // wait till all threads are done
        scope.join();

        // throw an exception if one occurred
        scope.throwIfFailed();

        return "result: " + futureA.resultNow() + " " + futureB.resultNow();
    }
}
```

## 4. 平台线程与InvokeAll

平台线程是我们从以前的Java版本中了解到的线程，这些线程与操作系统管理的线程紧密耦合。下面的示例看起来像第一个使用虚拟线程的示例，因为我们使用了ExecutorService。这次我们使用的ExecutorService是一个CachedThreadPool，这个池在需要时创建一个新线程，但会重用已经完成任务的线程。

```java
static List<String> invokeAllWithPlatformThreads() throws ExecutionException, InterruptedException {
    try (ExecutorService executor = Executors.newCachedThreadPool()) {

        var tasks = new ArrayList<Callable<String>>();
        tasks.add(() -> getStringFromResourceA());
        tasks.add(() -> getStringFromResourceB());

        List<Future<String>> futures = executor.invokeAll(tasks);

        return futures.stream().map(f -> {
            try {
                return f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
```

## 5. 总结

本文介绍了实现invokeAll方法的三种方式。我们首先使用了ExecutorService，它为需要执行的每个任务创建虚拟线程。之后，我们看到了如何创建一个行为类似于invokeAll方法的StructuredTaskScope。最后一个示例使用了我们已经从以前的Java版本中熟悉的平台线程。