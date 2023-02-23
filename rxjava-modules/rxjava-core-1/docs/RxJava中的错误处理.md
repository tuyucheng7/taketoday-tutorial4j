## 1. 简介

在本文中，我们将了解如何使用RxJava处理异常和错误。

首先，请记住Observable通常不会抛出异常。相反，默认情况下，Observable调用其Observer的onError()方法，通知观察者刚刚发生了不可恢复的错误，然后退出而不调用任何更多的Observer方法。

**我们即将介绍的错误处理运算符通过恢复或重试Observable序列来改变默认行为**。

## 2. Maven依赖

首先，让我们在pom.xml中添加RxJava：

```xml
<dependency>
    <groupId>io.reactivex.rxjava2</groupId>
    <artifactId>rxjava</artifactId>
    <version>2.1.3</version>
</dependency>
```

可以在[此处](https://search.maven.org/search?q=rxjava)找到该工件的最新版本。

## 3. 错误处理

当错误发生时，我们通常需要通过某种方式进行处理。例如，改变相关的外部状态，使用默认结果恢复序列，或者简单地保持原样以便错误可以传播。

### 3.1 错误操作

使用[doOnError](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#doOnError-io.reactivex.functions.Consumer-)，我们可以在出现错误时调用所需的任何操作：

```java
@Test
public void whenChangeStateOnError_thenErrorThrown() {
    TestObserver testObserver = new TestObserver();
    AtomicBoolean state = new AtomicBoolean(false);
    Observable
        .error(UNKNOWN_ERROR)
        .doOnError(throwable -> state.set(true))
        .subscribe(testObserver);

    testObserver.assertError(UNKNOWN_ERROR);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
 
    assertTrue("state should be changed", state.get());
}
```

如果在执行操作时抛出异常，RxJava会将异常包装在[CompositeException](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/exceptions/CompositeException.html)中：

```java
@Test
public void whenExceptionOccurOnError_thenCompositeExceptionThrown() {
    TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_ERROR)
        .doOnError(throwable -> {
            throw new RuntimeException("unexcepted");
        })
        .subscribe(testObserver);

    testObserver.assertError(CompositeException.class);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
}
```

### 3.2 使用默认元素恢复

虽然我们可以使用doOnError调用操作，但错误仍然会破坏标准序列流。有时我们想使用默认选项恢复序列，这就是[onErrorReturnItem](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#onErrorReturnItem-T-)所做的：

```java
@Test
public void whenHandleOnErrorResumeItem_thenResumed(){
    TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_ERROR)
        .onErrorReturnItem("singleValue")
        .subscribe(testObserver);
 
    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValueCount(1);
    testObserver.assertValue("singleValue");
}
```

如果首选动态默认元素供应商，我们可以使用[onErrorReturn](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#onErrorReturn-io.reactivex.functions.Function-)：

```java
@Test
public void whenHandleOnErrorReturn_thenResumed() {
    TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_ERROR)
        .onErrorReturn(Throwable::getMessage)
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValueCount(1);
    testObserver.assertValue("unknown error");
}
```

### 3.3 用另一个序列恢复

当遇到错误时，我们可以使用[onErrorResumeNext](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#onErrorResumeNext-io.reactivex.ObservableSource-)提供回退(fallback)数据序列，而不是回退到单个元素。这将有助于防止错误传播：

```java
@Test
public void whenHandleOnErrorResume_thenResumed() {
    TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_ERROR)
        .onErrorResumeNext(Observable.just("one", "two"))
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValueCount(2);
    testObserver.assertValues("one", "two");
}
```

如果回退序列根据具体的异常类型不同，或者序列需要由函数生成，我们可以将函数传递给onErrorResumeNext：

```java
@Test
public void whenHandleOnErrorResumeFunc_thenResumed() {
    TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_ERROR)
        .onErrorResumeNext(throwable -> Observable
            .just(throwable.getMessage(), "nextValue"))
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValueCount(2);
    testObserver.assertValues("unknown error", "nextValue");
}
```

### 3.4 仅处理异常

RxJava还提供了一个回退方法，允许在引发异常(但没有错误)时使用提供的Observable继续序列：

```java
@Test
public void whenHandleOnException_thenResumed() {
    TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_EXCEPTION)
        .onExceptionResumeNext(Observable.just("exceptionResumed"))
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertValueCount(1);
    testObserver.assertValue("exceptionResumed");
}

@Test
public void whenHandleOnException_thenNotResumed() {
    TestObserver testObserver = new TestObserver();
    Observable
        .error(UNKNOWN_ERROR)
        .onExceptionResumeNext(Observable.just("exceptionResumed"))
        .subscribe(testObserver);

    testObserver.assertError(UNKNOWN_ERROR);
    testObserver.assertNotComplete();
}
```

如上面的代码所示，当确实发生错误时，onExceptionResumeNext不会启动以恢复序列。

## 4. 出错重试

正常序列可能会因临时系统故障或后端错误而中断。在这些情况下，我们希望重试并等待序列修复。

幸运的是，RxJava为我们提供了执行此操作的选项。

### 4.1 重试

通过使用[retry](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#retry--)，Observable将被重新订阅无限次，直到没有错误为止。但大多数时候我们更喜欢固定次数的重试：

```java
@Test
public void whenRetryOnError_thenRetryConfirmed() {
    TestObserver testObserver = new TestObserver();
    AtomicInteger atomicCounter = new AtomicInteger(0);
    Observable
        .error(() -> {
            atomicCounter.incrementAndGet();
            return UNKNOWN_ERROR;
        })
        .retry(1)
        .subscribe(testObserver);

    testObserver.assertError(UNKNOWN_ERROR);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
    assertTrue("should try twice", atomicCounter.get() == 2);
}
```

### 4.2 条件重试

条件重试在RxJava中也是可行的，使用[带有谓词的retry](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#retry-io.reactivex.functions.BiPredicate-)或使用[retryUntil](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#retryUntil-io.reactivex.functions.BooleanSupplier-)：

```java
@Test
public void whenRetryConditionallyOnError_thenRetryConfirmed() {
    TestObserver testObserver = new TestObserver();
    AtomicInteger atomicCounter = new AtomicInteger(0);
    Observable
        .error(() -> {
            atomicCounter.incrementAndGet();
            return UNKNOWN_ERROR;
        })
        .retry((integer, throwable) -> integer < 4)
        .subscribe(testObserver);

    testObserver.assertError(UNKNOWN_ERROR);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
    assertTrue("should call 4 times", atomicCounter.get() == 4);
}

@Test
public void whenRetryUntilOnError_thenRetryConfirmed() {
    TestObserver testObserver = new TestObserver();
    AtomicInteger atomicCounter = new AtomicInteger(0);
    Observable
        .error(UNKNOWN_ERROR)
        .retryUntil(() -> atomicCounter.incrementAndGet() > 3)
        .subscribe(testObserver);
    testObserver.assertError(UNKNOWN_ERROR);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
    assertTrue("should call 4 times", atomicCounter.get() == 4);
}
```

### 4.3 重试时间

除了这些基本选项之外，还有一个有趣的重试方法：retryWhen。

这将返回一个Observable，比如“NewO”，它发出与源[ObservableSource](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/ObservableSource.html)相同的值，比如“OldO”，但是如果返回的Observable “NewO”调用onComplete或onError，则订阅者的onComplete或onError将被调用。

如果“NewO”发出任何元素，将触发对源ObservableSource “OldO”的重新订阅。

下面的测试显示了它是如何工作的：

```java
@Test
public void whenRetryWhenOnError_thenRetryConfirmed() {
    TestObserver testObserver = new TestObserver();
    Exception noretryException = new Exception("don't retry");
    Observable
        .error(UNKNOWN_ERROR)
        .retryWhen(throwableObservable -> Observable.error(noretryException))
        .subscribe(testObserver);

    testObserver.assertError(noretryException);
    testObserver.assertNotComplete();
    testObserver.assertNoValues();
}

@Test
public void whenRetryWhenOnError_thenCompleted() {
    TestObserver testObserver = new TestObserver();
    AtomicInteger atomicCounter = new AtomicInteger(0);
    Observable
        .error(() -> {
            atomicCounter.incrementAndGet();
            return UNKNOWN_ERROR;
        })
        .retryWhen(throwableObservable -> Observable.empty())
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertNoValues();
    assertTrue("should not retry", atomicCounter.get()==0);
}

@Test
public void whenRetryWhenOnError_thenResubscribed() {
    TestObserver testObserver = new TestObserver();
    AtomicInteger atomicCounter = new AtomicInteger(0);
    Observable
        .error(() -> {
            atomicCounter.incrementAndGet();
            return UNKNOWN_ERROR;
        })
        .retryWhen(throwableObservable -> Observable.just("anything"))
        .subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertNoValues();
    assertTrue("should retry once", atomicCounter.get()==1);
}
```

retryWhen的典型用法是具有可变延迟的有限重试：

```java
@Test
public void whenRetryWhenForMultipleTimesOnError_thenResumed() {
    TestObserver testObserver = new TestObserver();
    long before = System.currentTimeMillis();
    Observable
        .error(UNKNOWN_ERROR)
        .retryWhen(throwableObservable -> throwableObservable
            .zipWith(Observable.range(1, 3), (throwable, integer) -> integer)
            .flatMap(integer -> Observable.timer(integer, TimeUnit.SECONDS)))
        .blockingSubscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertComplete();
    testObserver.assertNoValues();
    long secondsElapsed = (System.currentTimeMillis() - before)/1000;
    assertTrue("6 seconds should elapse",secondsElapsed == 6 );
}
```

请注意此逻辑如何重试3次并逐渐延迟每次重试。

## 5. 总结

在本文中，我们介绍了一些在RxJava中处理错误和异常的方法。

还有一些与错误处理相关的特定于RxJava的异常-请查看[官方wiki](https://github.com/ReactiveX/RxJava/wiki/Error-Handling#rxjava-specific-exceptions-and-what-to-do-about-them)了解更多详细信息。