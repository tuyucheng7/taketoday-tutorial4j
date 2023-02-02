## 1. 概述

在本教程中，我们将讨论孵化器功能[结构化并发(JEP428)](https://openjdk.org/jeps/428)，它为Java 19提供了结构化并发功能。我们将指导你使用新的API来管理多线程代码。

## 2. 理念

通过采用并发编程风格来降低线程泄漏和取消延迟的可能性，从而增强多线程代码的可维护性、可靠性和可观察性，这是与取消和关闭相关的常见风险。为了更好地理解非结构化并发的问题，让我们看一个例子：

```java
Future<Shelter> shelter;
Future<List<Dog>> dogs;
try (ExecutorService executorService = Executors.newFixedThreadPool(3)) {
    shelter = executorService.submit(this::getShelter);
    dogs = executorService.submit(this::getDogs);
    Shelter theShelter = shelter.get();   // Join the shelter
    List<Dog> theDogs = dogs.get();  // Join the dogs
    Response response = new Response(theShelter, theDogs);
} catch (ExecutionException | InterruptedException e) {
    throw new RuntimeException(e);
}
```

当getShelter()正在运行时，代码不会注意到getDogs()是否可能失败，并且将继续不必要地导致阻塞shelter.get()调用。结果，只有在getShelter()完成并且getDogs()返回之后，dogs.get()才会抛出异常，我们的代码才会失败：

<img src="../assets/img.png">

但这不是唯一的问题。当执行我们代码的线程被中断时，它不会将中断传播到我们的子任务。此外，如果第一个执行的子任务shelter抛出异常，它不会委托给dogs子任务，它会继续运行，浪费资源。

结构化并发试图解决这些问题，我们将在下一章中看到。

## 3. 示例

对于我们的结构化并发示例，我们将使用以下记录：

```java
record Shelter(String name) {
}

record Dog(String name) {
}

record Response(Shelter shelter, List<Dog> dogs) {
}
```

我们还将提供两种方法，一个获取Shelter：

```java
private Shelter getShelter() {
    return new Shelter("Shelter");
}
```

另一个是检索Dog元素列表：

```java
private List<Dog> getDogs() {
    return List.of(new Dog("Buddy"), new Dog("Simba"));
}
```

由于结构化并发是一项孵化器功能，因此我们必须使用以下参数运行我们的应用程序：

```shell
--enable-preview --add-modules jdk.incubator.foreign
```

否则，我们可以添加一个module-info.java并将相关的包指定为required。

让我们看一个例子：

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<Shelter> shelter = scope.fork(this::getShelter);
    Future<List<Dog>> dogs = scope.fork(this::getDogs);
    scope.join();
    Response response = new Response(shelter.resultNow(), dogs.resultNow());
    // ...
}
```

由于StructuredTaskScope实现了AutoCloseable接口，因此我们可以在try-with-resources语句中使用它。StructuredTaskScope为我们提供了两个子类，它们有不同的用途。**在本教程中，我们将使用ShutdownOnFailure()，它会在出现问题时关闭子任务**。

还有一个ShutdownOnSuccess()构造函数，它的作用恰恰相反。如果成功，它会关闭子任务。这种短路模式有助于我们避免不必要的工作。

StructuredTaskScope的使用非常类似于同步代码的结构，创建scope的线程是所有者。scope允许我们在scope中fork(分叉)更多的子任务。此代码以异步方式调用，在join()方法的帮助下，我们可以阻塞所有任务，直到它们交付结果。

每个任务都可以在scope的shutdown()方法的帮助下终止其他任务，throwIfFailed()方法提供了另一种可能性：

```java
scope.throwIfFailed(e -> new RuntimeException("ERROR_MESSAGE"));
```

**如果任何fork失败，它允许我们传播任何异常**。此外，我们还可以使用joinUntil设置截止时间：

```java
scope.joinUntil(Instant.now().plusSeconds(1));
```

如果任务尚未完成，这将在时间到期后抛出异常。

## 4. 总结

在本文中，我们讨论了非结构化并发的缺点以及结构化并发如何尝试解决这些问题。我们学会了如何处理错误和执行截止时间。我们还看到了新构造如何使同步编写可维护、可读和可靠的多线程代码变得更加容易。