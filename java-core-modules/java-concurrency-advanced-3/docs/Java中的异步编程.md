## 1. 概述

随着对编写非阻塞代码的需求不断增长，我们需要异步执行代码的方法。

在本文中，我们将介绍几种在Java中实现[异步编程](https://www.baeldung.com/cs/async-vs-multi-threading)的方法。我们还将探索一些提供现成解决方案的Java库。

## 2. Java中的异步编程

### 2.1 Thread

我们可以创建一个新线程来异步执行任何操作。随着Java 8中[Lambda表达式](https://www.baeldung.com/java-8-lambda-expressions-tips)的发布，它变得更清晰，更具可读性。

让我们创建一个新线程来计算和打印数字的阶乘：

```java
int number = 20;
Thread newThread = new Thread(() -> {
    System.out.println("Factorial of " + number + " is: " + factorial(number));
});
newThread.start();
```

### 2.2 FutureTask

自Java 5以来，Future接口提供了一种使用[FutureTask](https://www.baeldung.com/java-future#1-implementing-futures-with-futuretask)执行异步操作的方法。

我们可以使用[ExecutorService](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/ExecutorService.html)的submit()方法异步执行任务，并返回FutureTask的实例。

因此，让我们找出一个数字的阶乘：

```java
ExecutorService threadpool = Executors.newCachedThreadPool();
Future<Long> futureTask = threadpool.submit(() -> factorial(number));

while (!futureTask.isDone()) {
    System.out.println("FutureTask is not finished yet..."); 
} 
long result = futureTask.get(); 

threadpool.shutdown();
```

在这里，我们使用Future接口提供的isDone()方法来检查任务是否完成。如果任务执行已完成，我们可以使用get()方法检索结果。

### 2.3 CompletableFuture

**Java 8引入了[CompletableFuture](https://www.baeldung.com/java-completablefuture)，它结合了Future和[CompletionStage](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CompletionStage.html)**。为异步编程提供了各种方法，如supplyAsync、runAsync和thenApplyAsync。

现在让我们使用CompletableFuture代替FutureTask来计算一个数字的阶乘：

```java
CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> factorial(number));
while (!completableFuture.isDone()) {
    System.out.println("CompletableFuture is not finished yet...");
}
long result = completableFuture.get();
```

我们不需要显式使用ExecutorService，**CompletableFuture在内部使用[ForkJoinPool](https://www.baeldung.com/java-fork-join)异步处理任务**。因此，它使我们的代码更清晰。

## 3. Guava

**[Guava](https://www.baeldung.com/guava-21-new)提供[ListenableFuture](https://guava.dev/releases/28.2-jre/api/docs/index.html?com/google/common/util/concurrent/ListenableFuture.html)类来执行异步操作**。

首先，我们将添加最新的[Guava](https://search.maven.org/search?q=a:guava) Maven依赖项：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

然后让我们使用ListenableFuture计算一个数字的阶乘：

```java
ExecutorService threadpool = Executors.newCachedThreadPool();
ListeningExecutorService service = MoreExecutors.listeningDecorator(threadpool);

ListenableFuture<Long> guavaFuture = (ListenableFuture<Long>) service.submit(()-> factorial(number));
long result = guavaFuture.get();
```

这里的[MoreExecutors](https://guava.dev/releases/28.2-jre/api/docs/index.html?com/google/common/util/concurrent/MoreExecutors.html)类提供了[ListeningExecutorService](https://guava.dev/releases/28.2-jre/api/docs/index.html?com/google/common/util/concurrent/ListeningExecutorService.html)类的实例。然后ListingExecutorService.submit()方法异步执行任务并返回ListenableFuture的实例。

**Guava还有一个[Futures](https://guava.dev/releases/28.2-jre/api/docs/com/google/common/util/concurrent/Futures.html)类，该类提供submitAsync、ScheduleAsync和transformAsync等方法来链接ListenableFutures，类似于CompletableFuture**。

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

这里的submitAsync方法需要一个[AsyncCallable](https://guava.dev/releases/28.2-jre/api/docs/com/google/common/util/concurrent/AsyncCallable.html)的参数，它是使用[Callables](https://guava.dev/releases/28.2-jre/api/docs/com/google/common/util/concurrent/Callables.html)类创建的。

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

**Electronic Arts通过[ea-async库](https://github.com/electronicarts/ea-async)将.NET的async-await功能引入到Java生态系统中**。

该库允许按顺序编写异步(非阻塞)代码。因此，它使异步编程更容易并且自然地扩展。

首先，我们将最新的[ea-async](https://search.maven.org/search?q=a:ea-async) Maven依赖添加到pom.xml中：

```xml
<dependency>
    <groupId>com.ea.async</groupId>
    <artifactId>ea-async</artifactId>
    <version>1.2.3</version>
</dependency>
```

然后我们将使用EA的[Async](https://javadoc.io/doc/com.ea.async/ea-async/latest/com/ea/async/Async.html)类提供的await方法转换之前讨论的CompletableFuture代码：

```java
static { 
    Async.init(); 
}

public long factorialUsingEAAsync(int number) {
    CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> factorial(number));
    long result = Async.await(completableFuture);
}
```

在这里，我们在静态块中调用Async.init方法来初始化Async运行时检测。

异步检测在运行时转换代码，并重写对await方法的调用，使其行为类似于使用CompletableFuture链。

因此，**调用await方法类似于调用Future.join**。

我们可以使用–javaagent JVM参数进行编译时检测。这是Async.init方法的替代方法：

```shell
java -javaagent:ea-async-1.2.3.jar -cp <claspath> <MainClass>
```

现在让我们看另一个顺序编写异步代码的例子。

首先，我们将使用CompletableFuture类的thenComposeAsync和thenAcceptAsync等组合方法异步执行一些链式操作：

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

**该实现类似于顺序阻塞代码；但是，await方法不会阻塞代码**。

如前所述，对await方法的所有调用都将由Async工具重写，以类似于Future.join方法的方式工作。

因此，一旦hello方法的异步执行完成，Future结果就会传递给mergeWorld方法。然后使用CompletableFuture.runAsync方法将结果传递给最后一次执行。

## 5. Cactoos

**Cactoos是一个基于面向对象原则的Java库**。

它是Google Guava和Apache Commons的替代品，提供用于执行各种操作的通用对象。

首先，让我们添加最新的[cactoos](https://search.maven.org/search?q=a:cactoos) Maven依赖项：

```xml
<dependency>
    <groupId>org.cactoos</groupId>
    <artifactId>cactoos</artifactId>
    <version>0.43</version>
</dependency>
```

该库为异步操作提供了一个[Async](https://javadoc.io/doc/org.cactoos/cactoos/latest/org/cactoos/func/Async.html)类。

所以我们可以使用Cactoos的Async类的实例计算一个数字的阶乘：

```java
Async<Integer, Long> asyncFunction = new Async<Integer, Long>(input -> factorial(input));
Future<Long> asyncFuture = asyncFunction.apply(number);
long result = asyncFuture.get();
```

**这里apply方法使用ExecutorService.submit方法执行操作，并返回一个Future接口的实例**。

类似地，Async类具有exec方法，该方法提供相同的功能但没有返回值。

注意：Cactoos库处于开发的初始阶段，可能还不适合生产使用。

## 6. Jcabi-Aspects

Jcabi-Aspects通过[AspectJ](https://www.baeldung.com/aspectj) AOP切面为异步编程提供[@Async](https://aspects.jcabi.com/apidocs-0.22.6/com/jcabi/aspects/Async.html)注解。

首先，让我们添加最新的[jcabi-aspects](https://search.maven.org/search?q=a:jcabi-aspects) Maven依赖项：

```xml
<dependency>
    <groupId>com.jcabi</groupId>
    <artifactId>jcabi-aspects</artifactId>
    <version>0.22.6</version>
</dependency>
```

jcabi-aspects库需要AspectJ运行时支持，因此我们将添加[aspectjrt](https://search.maven.org/search?q=a:aspectjrt) Maven依赖项：

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjrt</artifactId>
    <version>1.9.5</version>
</dependency>
```

接下来，我们将添加[jcabi-maven-plugin](https://search.maven.org/search?q=a:jcabi-maven-plugin)插件，该插件将二进制文件与AspectJ切面编织在一起。该插件提供了为我们完成所有工作的ajc目标：

```xml
<plugin>
    <groupId>com.jcabi</groupId>
    <artifactId>jcabi-maven-plugin</artifactId>
    <version>0.14.1</version>
    <executions>
        <execution>
            <goals>
                <goal>ajc</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjtools</artifactId>
            <version>1.9.1</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.1</version>
        </dependency>
    </dependencies>
</plugin>
```

现在我们都准备好使用AOP切面进行异步编程：

```java
@Async
@Loggable
public Future<Long> factorialUsingJcabiAspect(int number) {
    Future<Long> factorialFuture = CompletableFuture.supplyAsync(() -> factorial(number));
    return factorialFuture;
}
```

当我们编译代码时，**库将通过AspectJ编织注入AOP通知代替@Async注解**，以异步执行factorialUsingJcabiAspect方法。

让我们使用Maven命令编译类：

```shell
mvn install
```

jcabi-maven-plugin的输出可能类似于：

```shell
 --- jcabi-maven-plugin:0.14.1:ajc (default) @ java-async ---
[INFO] jcabi-aspects 0.18/55a5c13 started new daemon thread jcabi-loggable for watching of @Loggable annotated methods
[INFO] Unwoven classes will be copied to /tutorials/java-async/target/unwoven
[INFO] jcabi-aspects 0.18/55a5c13 started new daemon thread jcabi-cacheable for automated cleaning of expired @Cacheable values
[INFO] ajc result: 10 file(s) processed, 0 pointcut(s) woven, 0 error(s), 0 warning(s)
```

我们可以通过检查Maven插件生成的jcabi-ajc.log文件中的日志来验证我们的类是否被正确编织：

```plaintext
Join point 'method-execution(java.util.concurrent.Future 
com.baeldung.async.JavaAsync.factorialUsingJcabiAspect(int))' 
in Type 'cn.tuyucheng.taketoday.async.JavaAsync' (JavaAsync.java:158) 
advised by around advice from 'com.jcabi.aspects.aj.MethodAsyncRunner' 
(jcabi-aspects-0.22.6.jar!MethodAsyncRunner.class(from MethodAsyncRunner.java))
```

然后我们将该类作为一个简单的Java应用程序运行，输出将如下所示：

```shell
17:46:58.245 [main] INFO com.jcabi.aspects.aj.NamedThreads - 
jcabi-aspects 0.22.6/3f0a1f7 started new daemon thread jcabi-loggable for watching of @Loggable annotated methods
17:46:58.355 [main] INFO com.jcabi.aspects.aj.NamedThreads - 
jcabi-aspects 0.22.6/3f0a1f7 started new daemon thread jcabi-async for Asynchronous method execution
17:46:58.358 [jcabi-async] INFO cn.tuyucheng.taketoday.async.JavaAsync - 
#factorialUsingJcabiAspect(20): 'java.util.concurrent.CompletableFuture@14e2d7c1[Completed normally]' in 44.64µs
```

正如我们所见，一个新的守护线程jcabi-async是由异步执行任务的库创建的。

同样，日志记录由库提供的[@Loggable](https://aspects.jcabi.com/apidocs-0.22.6/com/jcabi/aspects/Loggable.html)注解启用。

## 7. 总结

在本文中，我们学习了Java中异步编程的几种方法。

首先，我们探索了Java的内置功能，例如用于异步编程的FutureTask和CompletableFuture。然后我们检查了一些库，例如EA Async和Cactoos，它们具有开箱即用的解决方案。

我们还讨论了使用Guava的ListenableFuture和Futures类异步执行任务的支持。最后，我们介绍了jcabi-AspectJ库，它通过异步方法调用的@Async注解提供AOP功能。