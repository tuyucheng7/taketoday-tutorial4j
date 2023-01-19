## 1. 概述

随着对编写非阻塞代码的需求不断增长，我们需要异步执行代码的方法。

在本文中，我们将介绍几种在Java中实现异步编程的方法，并介绍一些提供现成解决方案的Java库。

## 2. Java中的异步编程

### 2.1 Thread

我们可以创建一个新线程来异步执行任何操作，随着Java 8中lambda表达式的发布，它变得更简洁，可读性更强。

让我们创建一个新线程来计算和打印数字的阶乘：

```java
int number = 20;
Thread newThread = new Thread(() -> {
    System.out.println("Factorial of " + number + " is: " + factorial(number));
});
newThread.start();
```

### 2.2 FutureTask

自Java 5以来，Future接口提供了一种使用FutureTask执行异步操作的方法，我们可以使用ExecutorService的submit()方法异步执行任务，并返回FutureTask的实例。

下面的代码获取一个数的阶乘：

```java
ExecutorService threadpool = Executors.newCachedThreadPool();
Future<Long> futureTask = threadpool.submit(() -> factorial(number));

while (!futureTask.isDone()) {
    System.out.println("FutureTask is not finished yet..."); 
} 
long result = futureTask.get(); 

threadpool.shutdown();
```

在这里，我们使用Future接口提供的isDone()方法来检查任务是否完成。如果任务执行已完成，我们可以使用get()方法获取结果。

### 2.3 CompletableFuture

Java 8引入了CompletableFuture，它结合了Future和CompletionStage，为异步编程提供了各种方法，如supplyAsync、runAsync和thenApplyAsync。

现在，让我们使用CompletableFuture代替FutureTask来计算数字的阶乘：

```java
CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> factorial(number));
while (!completableFuture.isDone()) {
    System.out.println("CompletableFuture is not finished yet...");
}
long result = completableFuture.get();
```

我们不需要显式地使用ExecutorService，CompletableFuture在内部使用ForkJoinPool异步处理任务。

## 3. Guava

Guava提供ListenableFuture类来执行异步操作。

首先，我们需要添加Guava依赖：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

然后，让我们使用ListenableFuture计算一个数字的阶乘：

```java
ExecutorService threadpool = Executors.newCachedThreadPool();
ListeningExecutorService service = MoreExecutors.listeningDecorator(threadpool);

ListenableFuture<Long> guavaFuture = (ListenableFuture<Long>) service.submit(()-> factorial(number));
long result = guavaFuture.get();
```

这里，MoreExecutors类可以获取ListingExecutorService的实例，然后ListingExecutorService.submit()方法异步执行任务并返回ListenableFuture的实例。

Guava还有一个Futures类，它提供submitAsync、ScheduleAsync和transformAsync等方法来链接ListenableFutures，类似于CompletableFuture。

例如，让我们看看如何使用Futures.submitAsync()代替ListingExecutorService.submit()方法：

```java
ListeningExecutorService service = MoreExecutors.listeningDecorator(threadpool);
AsyncCallable<Long> asyncCallable = Callables.asAsyncCallable(new Callable<Long>() {
    public Long call() {
        return factorial(number);
    }
}, service);
ListenableFuture<Long> guavaFuture = Futures.submitAsync(asyncCallable, service);
```

这里的submitAsync方法需要一个AsyncCallable的参数，它是使用Callables类创建的。

此外，Futures类提供了addCallback方法来注册成功和失败回调：

```java
Futures.addCallback(
        factorialFuture,
        new FutureCallback<Long>() {
            public void onSuccess(Long factorial) {
                System.out.println(factorial);
            }
            
            public void onFailure(Throwable thrown) {
                thrown.getCause();
            }
        },
        service);
```

## 4. EA Async

Electronic Arts通过ea-async库将.NET的async-await功能引入Java生态系统，该库允许按顺序编写异步(非阻塞)代码。因此，它使异步编程更容易，并且可以自然扩展。

首先，我们将最新的ea-async依赖添加到pom.xml：

```xml
<dependency>
    <groupId>com.ea.async</groupId>
    <artifactId>ea-async</artifactId>
    <version>1.2.3</version>
</dependency>
```

然后我们将使用EA的Async类提供的await方法转换前面讨论的CompletableFuture代码：

```java
static { 
    Async.init(); 
}

public long factorialUsingEAAsync(int number) {
    CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> factorial(number));
    long result = Async.await(completableFuture);
}
```

这里，我们在静态块中调用Async.init方法来初始化Async运行时检测，异步检测在运行时转换代码，并重写对await方法的调用，使其行为类似于使用CompletableFuture链。因此，调用await方法类似于调用Future.join。

我们可以使用–javaagent JVM参数进行编译时检测，这是Async.init方法的替代方法：

```shell
java -javaagent:ea-async-1.2.3.jar -cp <claspath> <MainClass>
```

现在让我们看另一个顺序编写异步代码的例子。首先，我们将使用CompletableFuture类的thenComposeAsync和thenAcceptAsync等组合方法异步执行一些链操作：

```java
CompletableFuture<Void> completableFuture = hello()
        .thenComposeAsync(EAAsyncExample::mergeWorld)
        .thenAcceptAsync(EAAsyncExample::print)
        .exceptionally(throwable -> {
            System.out.println(throwable.getCause());
            return null;
        });
completableFuture.get();

public static CompletableFuture<String> hello() {
    return CompletableFuture.supplyAsync(() -> "Hello");
}

public static CompletableFuture<String> mergeWorld(String s) {
    return CompletableFuture.supplyAsync(() -> s + " World!");
}
        
public static void print(String str) {
    CompletableFuture.runAsync(() -> System.out.println(str));
}
```

然后我们可以使用EA的Async.await()转换代码：

```java
try {
    String hello = await(hello());
    String helloWorld = await(mergeWorld(hello));
    await(CompletableFuture.runAsync(() -> print(helloWorld)));
} catch (Exception e) {
    e.printStackTrace();
}
```

该实现类似于顺序阻塞代码；但是，await方法不会阻塞代码。如上所述，对await方法的所有调用都将由Async工具重写，以类似于Future.join方法。

因此，一旦hello方法的异步执行完成，Future结果就会传递给mergeWorld方法，然后使用CompletableFuture.runAsync方法将结果传递给最后一次执行。

## 5. Cactoos

Cactoos是一个基于面向对象原则的Java库。

它是Google Guava和Apache Commons的替代品，提供用于执行各种操作的通用对象。

首先，让我们添加最新的cactoos Maven依赖项：

```xml
<dependency>
    <groupId>org.cactoos</groupId>
    <artifactId>cactoos</artifactId>
    <version>0.43</version>
</dependency>
```

这个库为异步操作提供了一个Async类。

所以我们可以使用Cactoos的Async类的实例计算一个数字的阶乘：

```java
Async<Integer, Long> asyncFunction = new Async<Integer, Long>(input -> factorial(input));
Future<Long> asyncFuture = asyncFunction.apply(number);
long result = asyncFuture.get();
```

这里apply方法使用ExecutorService.submit方法执行操作，并返回一个Future接口的实例。

同样，Async类具有提供相同功能但没有返回值的exec方法。

注意：Cactoos库处于开发的初始阶段，可能还不适合生产使用。

## 6. Jcabi-Aspects

