## 1. 简介

在本教程中，我们将介绍JAX-RS对使用Jersey API的响应式(Rx)编程的支持。本文假定读者了解Jersey REST客户端API。

熟悉[响应式编程概念](https://www.baeldung.com/rx-java)会有所帮助，但不是必需的。

## 2. 依赖关系

首先，我们需要标准的Jersey客户端库依赖项：

```xml
<dependency>
    <groupId>org.glassfish.jersey.core</groupId>
    <artifactId>jersey-client</artifactId>
    <version>2.27</version>
</dependency>
<dependency>
    <groupId>org.glassfish.jersey.inject</groupId>
    <artifactId>jersey-hk2</artifactId>
    <version>2.27</version>
</dependency>
```

这些依赖项为我们提供了常用的JAX-RS响应式编程支持。[jersey-client](https://central.sonatype.com/artifact/org.glassfish.jersey.core/jersey-client/3.1.1)和[jersey-hk2](https://central.sonatype.com/artifact/org.glassfish.jersey.inject/jersey-hk2/3.1.1)的当前版本在Maven Central上可用。

对于第三方响应式框架支持，我们将使用这些扩展：

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext.rx</groupId>
    <artifactId>jersey-rx-client-rxjava</artifactId>
    <version>2.27</version>
</dependency>
```

上面的依赖提供了对RxJava的[Observable](https://github.com/ReactiveX/RxJava/wiki/Observable)的支持；对于较新的RxJava 2的[Flowable](https://www.baeldung.com/rxjava-2-flowable)，我们使用以下扩展：

```xml
<dependency>
    <groupId>org.glassfish.jersey.ext.rx</groupId>
    <artifactId>jersey-rx-client-rxjava2</artifactId>
    <version>2.27</version>
</dependency>
```

Maven Central也提供了对[rxjava](https://central.sonatype.com/artifact/org.glassfish.jersey.ext.rx/jersey-rx-client-rxjava/3.1.1)和[rxjava2](https://central.sonatype.com/artifact/org.glassfish.jersey.ext.rx/jersey-rx-client-rxjava2/3.1.1)的依赖。

## 3. 为什么我们需要响应式JAX-RS客户端

假设我们要使用三个REST API：

-   id-service提供长用户ID列表
-   name-service为给定的用户ID提供用户名
-   hash-service将返回用户ID和用户名的哈希值

我们为每个服务创建一个客户端：

```java
Client client = ClientBuilder.newClient();
WebTarget userIdService = client.target("http://localhost:8080/id-service/ids");
WebTarget nameService = client.target("http://localhost:8080/name-service/users/{userId}/name");
WebTarget hashService = client.target("http://localhost:8080/hash-service/{rawValue}");
```

这是一个人为的例子，但它适用于我们的说明目的。JAX-RS规范至少支持三种方法来一起使用这些服务：

-   同步(阻塞)
-   异步(非阻塞)
-   响应式(函数式，非阻塞)

### 3.1 同步Jersey客户端调用的问题

使用这些服务的普通方法将看到我们使用id-service来获取用户ID，然后为返回的每个ID依次调用name-service和hash-service API。

**使用这种方法，每次调用都会阻塞正在运行的线程，直到请求得到满足**，总共花费大量时间来完成组合的请求。在任何重要的用例中，这显然都不能令人满意。

### 3.2 异步Jersey客户端调用的问题

一种更复杂的方法是使用JAX-RS支持的[InvocationCallback](https://docs.oracle.com/javaee/7/api/javax/ws/rs/client/InvocationCallback.html)机制。在最基本的形式中，我们将回调传递给get方法以定义给定API调用完成时发生的情况。

虽然我们现在获得了真正的异步执行([对线程效率有一些限制](https://stackoverflow.com/questions/26150257/jersey-client-non-blocking))，但很容易看出这种代码风格如何在除了琐碎的场景之外的任何情况下变得难以阅读和笨拙。[JAX-RS规范](https://github.com/jax-rs/spec)特别将此场景突出为[末日金字塔](https://en.wikipedia.org/wiki/Pyramid_of_doom_(programming))：

```java
// used to keep track of the progress of the subsequent calls
CountDownLatch completionTracker = new CountDownLatch(expectedHashValues.size()); 

userIdService.request()
    .accept(MediaType.APPLICATION_JSON)
    .async()
    .get(new InvocationCallback<List<Long>>() {
        @Override
        public void completed(List<Long> employeeIds) {
            employeeIds.forEach((id) -> {
            // for each employee ID, get the name
            nameService.resolveTemplate("userId", id).request()
                .async()
                .get(new InvocationCallback<String>() {
                    @Override
                    public void completed(String response) {
                           hashService.resolveTemplate("rawValue", response + id).request()
                          .async()
                          .get(new InvocationCallback<String>() {
                              @Override
                              public void completed(String response) {
                                  //complete the business logic
                              }
                              // ommitted implementation of the failed() method
                          });
                    }
                    // omitted implementation of the failed() method
                });
            });
        }
        // omitted implementation of the failed() method
});

// wait for inner requests to complete in 10 seconds
if (!completionTracker.await(10, TimeUnit.SECONDS)) {
    logger.warn("Some requests didn't complete within the timeout");
}
```

所以我们已经实现了异步的、高效的代码，但是：

-   很难阅读
-   每次调用都会生成一个新线程

请注意，我们在所有代码示例中都使用了CountDownLatch，以等待hash-service交付所有预期值。我们这样做是为了通过检查所有预期值是否实际交付来断言代码在单元测试中有效。

通常的客户端不会等待，而是会在回调中对结果做任何应该做的事情，以免阻塞线程。

### 3.3 函数式反应式解决方案

函数式和反应式方法将为我们提供：

-   出色的代码可读性
-   流式的编码风格
-   有效的线程管理

JAX-RS在以下组件中支持这些目标：

-   CompletionStageRxInvoker支持CompletionStage接口作为默认的响应式组件
-   RxObservableInvokerProvider支持RxJava的Observable 
-   RxFlowableInvokerProvider支持RxJava的Flowable

还有一个[API](https://eclipse-ee4j.github.io/jersey.github.io/documentation/latest/rx-client.html#rx.client.spi)用于添加对其他Reactive库的支持。

## 4. JAX-RS响应式组件支持

### 4.1 JAX-RS中的CompletionStage

使用[CompletionStage](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CompletionStage.html)及其具体实现[CompletableFuture](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CompletableFuture.html)，我们可以编写一个优雅、非阻塞和流式的服务调用编排。

让我们从检索用户ID开始：

```java
CompletionStage<List<Long>> userIdStage = userIdService.request()
    .accept(MediaType.APPLICATION_JSON)
    .rx()
    .get(new GenericType<List<Long>>() {
}).exceptionally((throwable) -> {
    logger.warn("An error has occurred");
    return null;
});
```

**rx()方法调用是响应式处理的起点**。我们使用exceptionally函数来流式地定义我们的异常处理场景。

从这里，我们可以干净地编排调用以从name-service检索用户名，然后对名称和用户ID的组合进行哈希处理：

```java
List<String> expectedHashValues = ...;
List<String> receivedHashValues = new ArrayList<>(); 

// used to keep track of the progress of the subsequent calls 
CountDownLatch completionTracker = new CountDownLatch(expectedHashValues.size()); 

userIdStage.thenAcceptAsync(employeeIds -> {
    logger.info("id-service result: {}", employeeIds);
    employeeIds.forEach((Long id) -> {
        CompletableFuture completable = nameService.resolveTemplate("userId", id).request()
            .rx()
            .get(String.class)
            .toCompletableFuture();
        
        completable.thenAccept((String userName) -> {
            logger.info("name-service result: {}", userName);
            hashService.resolveTemplate("rawValue", userName + id).request()
                .rx()
                .get(String.class)
                .toCompletableFuture()
                .thenAcceptAsync(hashValue -> {
                    logger.info("hash-service result: {}", hashValue);
                    receivedHashValues.add(hashValue);
                    completionTracker.countDown();
                }).exceptionally((throwable) -> {
                    logger.warn("Hash computation failed for {}", id);
                    return null;
                });
        });
    });
});

if (!completionTracker.await(10, TimeUnit.SECONDS)) {
    logger.warn("Some requests didn't complete within the timeout");
}

assertThat(receivedHashValues).containsAll(expectedHashValues);
```

在上面的示例中，我们用流式且可读的代码组合了3个服务的执行。

thenAcceptAsync方法将在给定的CompletionStage完成执行(或抛出异常)后执行提供的函数。

每个连续的调用都是非阻塞的，可以明智地使用系统资源。

**CompletionStage接口提供了多种暂存和编排方法，允许我们在多步骤编排(或单个服务调用)中组合、排序和异步执行任意数量的步骤**。

### 4.2 JAX-RS中的Observable

要使用Observable RxJava组件，我们必须首先在客户端注册RxObservableInvokerProvider提供者(而不是Jersey规范文档中规定的“ObservableRxInvokerProvider”)：

```java
Client client = client.register(RxObservableInvokerProvider.class);
```

然后我们覆盖默认调用程序：

```java
Observable<List<Long>> userIdObservable = userIdService
    .request()
    .rx(RxObservableInvoker.class)
    .get(new GenericType<List<Long>>(){});
```

从这一点出发，**我们可以使用标准的Observable语义来编排处理流程**：

```java
userIdObservable.subscribe((List<Long> listOfIds)-> { 
    /** define processing flow for each ID */
});
```

### 4.3 JAX-RS中的Flowable

使用RxJava Flowable的语义类似于 Observable。我们注册合适的Provider：

```java
client.register(RxFlowableInvokerProvider.class);
```

然后我们提供RxFlowableInvoker：

```java
Flowable<List<Long>> userIdFlowable = userIdService
    .request()
    .rx(RxFlowableInvoker.class)
    .get(new GenericType<List<Long>>(){});
```

之后，我们可以使用普通的Flowable API。

## 5. 总结

JAX-RS规范提供了大量选项，可以编写干净、非阻塞的REST调用执行。

特别是CompletionStage接口提供了一组强大的方法，涵盖各种服务编排场景，以及提供自定义Executors以更细粒度地控制线程管理的机会。