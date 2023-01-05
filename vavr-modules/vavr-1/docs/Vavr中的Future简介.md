## 1. 简介

Java核心为异步计算提供了一个基本的API-Future，CompletableFuture是其最新的实现之一。

Vavr提供了Future API的新功能替代方案。在本文中，我们将讨论新的API并展示如何使用它的一些新功能。

可以在[此处](https://www.baeldung.com/vavr-tutorial)找到有关Vavr的更多文章。

## 2. Maven依赖

Future API包含在VavrMaven依赖项中。

所以，让我们将它添加到我们的pom.xml中：

```xml
<dependency>
    <groupId>io.vavr</groupId>
    <artifactId>vavr</artifactId>
    <version>0.9.2</version>
</dependency>
```

我们可以在[Maven Central](https://search.maven.org/search?q=a:vavr)上找到最新版本的依赖。

## 3. Vavr的Future

Future可以处于以下两种状态之一：

-   Pending：计算正在进行中
-   Completed：计算成功完成并返回结果，失败并出现异常或被取消

与核心Java Future相比的主要优势在于我们可以轻松地以非阻塞方式注册回调和组合操作。

## 4. Future的基本操作

### 4.1 启动异步计算

现在，让我们看看如何使用Vavr开始异步计算：

```java
String initialValue = "Welcome to ";
Future<String> resultFuture = Future.of(() -> someComputation());
```

### 4.2 从Future检索价值

我们可以通过简单地调用get()或getOrElse()方法之一从Future中提取值：

```java
String result = resultFuture.getOrElse("Failed to get underlying value.");
```

get()和getOrElse()之间的区别在于get()是最简单的解决方案，而getOrElse()使我们能够返回任何类型的值，以防我们无法在Future中检索值。

建议使用getOrElse()以便我们可以处理在尝试从Future检索值时发生的任何错误。为了简单起见，我们将在接下来的几个示例中只使用get()。

请注意，如果需要等待结果，get()方法会阻塞当前线程。

一种不同的方法是调用非阻塞getValue()方法，该方法返回一个Option<Try<T\>>，只要计算未决，该方法就会为空。

然后我们可以提取Try对象中的计算结果：

```java
Option<Try<String>> futureOption = resultFuture.getValue();
Try<String> futureTry = futureOption.get();
String result = futureTry.get();
```

有时我们需要在从中检索值之前检查Future是否包含值。

我们可以简单地通过使用来做到这一点：

```java
resultFuture.isEmpty();
```

重要的是要注意方法isEmpty()是阻塞的-它将阻塞线程直到它的操作完成。

### 4.3 更改默认的ExecutorService

Futures使用ExecutorService异步运行它们的计算，默认的ExecutorService是Executors.newCachedThreadPool()。

我们可以通过传递我们选择的实现来使用另一个ExecutorService：

```java
@Test
public void whenChangeExecutorService_thenCorrect() {
    String result = Future.of(newSingleThreadExecutor(), () -> HELLO)
		.getOrElse(error);
    
    assertThat(result).isEqualTo(HELLO);
}
```

## 5. 完成后执行操作

API提供onSuccess()方法，该方法在Future成功完成后立即执行操作。

类似地，方法onFailure()在Future失败时执行。

让我们看一个简单的例子：

```java
Future<String> resultFuture = Future.of(() -> appendData(initialValue))
  	.onSuccess(v -> System.out.println("Successfully Completed - Result: " + v))
  	.onFailure(v -> System.out.println("Failed - Result: " + v));
```

onComplete()方法接受在Future完成执行后立即运行的操作，无论Future是否成功。andThen()方法类似于onComplete()–它只是保证回调以特定顺序执行：

```java
Future<String> resultFuture = Future.of(() -> appendData(initialValue))
  	.andThen(finalResult -> System.out.println("Completed - 1: " + finalResult))
  	.andThen(finalResult -> System.out.println("Completed - 2: " + finalResult));
```

## 6. Future的实用操作

### 6.1 阻塞当前线程

await()方法有两种情况：

-   如果Future处于挂起状态，它将阻塞当前线程，直到Future完成
-   如果Future完成，它会立即完成

使用此方法很简单：

```java
resultFuture.await();
```

### 6.2 取消计算

我们总是可以取消计算：

```java
resultFuture.cancel();
```

### 6.3 检索底层ExecutorService

要获得Future使用的ExecutorService，我们可以简单地调用executorService()：

```java
resultFuture.executorService();
```

### 6.4 从失败的Future中获取Throwable

我们可以使用getCause()方法来做到这一点，该方法返回包装在io.vavr.control.Option对象中的Throwable。

我们稍后可以从Option对象中提取Throwable：

```java
@Test
public void whenDivideByZero_thenGetThrowable2() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0).await();
    
    assertThat(resultFuture.getCause().get().getMessage()).isEqualTo("/ by zero");
}
```

此外，我们可以使用failed()方法将实例转换为包含Throwable实例的Future：

```java
@Test
public void whenDivideByZero_thenGetThrowable1() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0);
    
    assertThatThrownBy(resultFuture::get).isInstanceOf(ArithmeticException.class);
}
```

### 6.5 isCompleted()、isSuccess()和isFailure()

这些方法几乎是不言自明的。他们检查Future是否完成，是成功完成还是失败。当然，它们都返回布尔值。

我们将在前面的示例中使用这些方法：

```java
@Test
public void whenDivideByZero_thenCorrect() {
    Future<Integer> resultFuture = Future.of(() -> 10 / 0)
      	.await();
    
    assertThat(resultFuture.isCompleted()).isTrue();
    assertThat(resultFuture.isSuccess()).isFalse();
    assertThat(resultFuture.isFailure()).isTrue();
}
```

### 6.6 在Future之上应用计算

map()方法允许我们在待处理的Future之上应用计算：

```java
@Test
public void whenCallMap_thenCorrect() {
    Future<String> futureResult = Future.of(() -> "from Tuyucheng")
        .map(a -> "Hello " + a)
        .await();
    
    assertThat(futureResult.get()).isEqualTo("Hello from Tuyucheng");
}
```

如果我们将一个返回Future的函数传递给map()方法，我们最终会得到一个嵌套的Future结构。为了避免这种情况，我们可以利用flatMap()方法：

```java
@Test
public void whenCallFlatMap_thenCorrect() {
    Future<Object> futureMap = Future.of(() -> 1)
      	.flatMap((i) -> Future.of(() -> "Hello: " + i));
         
    assertThat(futureMap.get()).isEqualTo("Hello: 1");
}
```

### 6.7 转换Future

方法transformValue()可用于在Future之上应用计算，并将其中的值更改为相同类型或不同类型的另一个值：

```java
@Test
public void whenTransform_thenCorrect() {
    Future<Object> future = Future.of(() -> 5)
      	.transformValue(result -> Try.of(() -> HELLO + result.get()));
                
    assertThat(future.get()).isEqualTo(HELLO + 5);
}
```

### 6.8 压缩Future

API提供了zip()方法，该方法将Futures压缩成元组——元组是多个元素的集合，这些元素可能彼此相关，也可能不相关。它们也可以是不同的类型。让我们看一个简单的例子：

```java
@Test
public void whenCallZip_thenCorrect() {
    Future<String> f1 = Future.of(() -> "hello1");
    Future<String> f2 = Future.of(() -> "hello2");
    
    assertThat(f1.zip(f2).get()).isEqualTo(Tuple.of("hello1", "hello2"));
}
```

这里要注意的一点是，只要至少有一个基础Futures仍处于待定状态，由此产生的Future就会处于待定状态。

### 6.9 Futures和CompletableFutures之间的转换

API支持与java.util.CompletableFuture的集成。因此，如果我们想执行只有核心Java API支持的操作，我们可以轻松地将Future转换为CompletableFuture。

让我们看看我们如何做到这一点：

```java
@Test
public void whenConvertToCompletableFuture_thenCorrect()throws Exception {
    CompletableFuture<String> convertedFuture = Future.of(() -> HELLO)
      	.toCompletableFuture();
    
    assertThat(convertedFuture.get()).isEqualTo(HELLO);
}
```

我们还可以使用fromCompletableFuture()方法将CompletableFuture转换为Future。

### 6.10 异常处理

在Future失败时，我们可以通过几种方式处理错误。

例如，我们可以使用recover()方法返回另一个结果，例如错误消息：

```java
@Test
public void whenFutureFails_thenGetErrorMessage() {
    Future<String> future = Future.of(() -> "Hello".substring(-1))
      	.recover(x -> "fallback value");
    
    assertThat(future.get()).isEqualTo("fallback value");
}
```

或者，我们可以使用recoverWith()返回另一个Future计算的结果：

```java
@Test
public void whenFutureFails_thenGetAnotherFuture() {
    Future<String> future = Future.of(() -> "Hello".substring(-1))
      	.recoverWith(x -> Future.of(() -> "fallback value"));
    
    assertThat(future.get()).isEqualTo("fallback value");
}
```

fallbackTo()方法是另一种处理错误的方法。它在Future上调用并接受另一个Future作为参数。

如果第一个Future成功，则返回其结果。否则，如果第二个Future成功，则返回其结果。如果两个Future都失败，则failed()方法返回一个Throwable的Future，它包含第一个Future的错误：

```java
@Test
public void whenBothFuturesFail_thenGetErrorMessage() {
    Future<String> f1 = Future.of(() -> "Hello".substring(-1));
    Future<String> f2 = Future.of(() -> "Hello".substring(-2));
    
    Future<String> errorMessageFuture = f1.fallbackTo(f2);
    Future<Throwable> errorMessage = errorMessageFuture.failed();
    
    assertThat(errorMessage.get().getMessage())
      	.isEqualTo("String index out of range: -1");
}
```

## 7. 总结

在本文中，我们了解了什么是Future并学习了它的一些重要概念。我们还使用一些实际示例介绍了API的一些功能。