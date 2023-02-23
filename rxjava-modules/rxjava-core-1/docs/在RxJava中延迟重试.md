## 1. 概述

在本教程中，**我们将看到如何在RxJava中使用延迟重试**。使用Observable时，很可能会出现错误而不是成功。在这种情况下，我们可以使用延迟重试来在一段时间后重新尝试。

## 2. 项目设置

首先，让我们创建一个Maven或Gradle项目。在这里，**我们使用基于Maven的项目**。让我们将rxjava依赖项添加到我们的pom.xml文件中：

```xml
<dependency>
    <groupId>io.reactivex.rxjava2</groupId>
    <artifactId>rxjava</artifactId>
    <version>2.2.21</version>
</dependency>
```

可以在[Maven Central](https://search.maven.org/artifact/io.reactivex.rxjava2/rxjava/2.2.21/jar)上找到依赖项。

## 3. 在Observable生命周期中重试

当Observable源发出错误时使用重试。这是对源的重新订阅，希望在下一次尝试中没有错误地完成。因此，仅当Observable发出错误时，retry()及其其他变体才会出现。

### 3.1 retry的重载变体

除了上一节讨论的retry()方法外，RxJava还提供了两个重载变体，retry(long)和retry(Func2)。

retry(long)返回一个镜像源Observable的Observable。只要它调用onError指定次数，我们就会重新订阅这个镜像。

retry(Func2)方法接收一个函数，该函数接收一个Throwable作为输入并产生一个布尔值作为输出。在这里，只有当镜像针对特定异常和重试次数返回true时，我们才重新订阅镜像。

### 3.2 retryWhen与retry

retryWhen(Func1)为我们提供了一个选项来包含自定义逻辑，以确定我们是否应该重新订阅原始Observable的镜像。此方法接收一个函数，该函数接收onError处理程序抛出的异常。

如果函数发出一个元素，我们重新订阅镜像，如果它产生一个错误通知，我们就不会这样做。

## 4. 代码示例

现在让我们在一些代码片段的帮助下观察这些概念的实际应用。

### 4.1 成功的Observable

如果Observable调用成功，则调用Observer的onNext方法。让我们看一个例子：

```java
@Test
public void givenObservable_whenSuccess_thenOnNext(){
    Observable.just(remoteCallSuccess())
        .subscribe(success -> {
            System.out.println("Success");
            System.out.println(success);
        }, err -> {
            System.out.println("Error");
            System.out.println(err);
        });
}
```

remoteCallSuccess()方法成功完成并返回一个String。在这种情况下，调用订阅者的onNext()方法，打印成功消息。

### 4.2 错误的Observable

让我们介绍一个名为remoteCallError()的新实用函数，它模拟调用远程服务器。此方法容易出错。因此，我们的retryWhen逻辑将启动：

```java
@Test
public void givenObservable_whenError_thenOnError(){
    Observable.just(remoteCallError())
        .subscribe(success -> {
            System.out.println("Success");
            System.out.println(success);
        }, err -> {
            System.out.println("Error");
            System.out.println(err);
        });
}
```

在这种情况下，remoteCallError()方法会导致错误。因此，调用订阅者的onError()方法。因此，打印错误消息。

### 4.3 延迟重试

我们可以在重新订阅过程中添加延迟：

```java
@Test
public void givenError_whenRetry_thenCanDelay(){
    Observable.just(remoteCallError())
        .retryWhen(attempts -> {
            return attempts.flatMap(err -> {
                if (customChecker(err)) {
                    return Observable.timer(5000, TimeUnit.MILLISECONDS);
                } else {
                    return Observable.error(err);
                }
            });
        });
}
```

与前面的情况类似，remoteCallError()方法会导致错误。然而，在这里，我们没有通知观察者的onError()方法，而是给这个Observable一个重新订阅的机会。为此，我们将异常传递给customChecker()方法，该方法返回一个布尔值。如果响应为true，我们将返回一个延迟5000毫秒(5秒)的新Observable。否则，我们返回错误并通知观察者。

## 5. 总结

在本教程中，我们看到了如何使用retryWhen及其一些相关函数。我们了解retry和retryWhen帮助我们解决问题的情况。