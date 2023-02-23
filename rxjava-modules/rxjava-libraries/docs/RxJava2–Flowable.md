## 1. 简介

**RxJava是一个Reactive Extensions Java实现，允许我们编写事件驱动和异步应用程序**。有关如何使用RxJava的更多信息，请参阅此处的[介绍文章](https://www.baeldung.com/rx-java)。

RxJava 2从头开始重写，带来了许多新功能；其中一些是为了响应先前版本的框架中存在的问题而创建的。

其中一个特性是**io.reactivex.Flowable**。

## 2. Observable与Flowable

在之前的RxJava版本中，只有一个基类用于处理背压感知和非背压感知源-Observable。

RxJava 2在这两种源之间引入了明确的区别-背压感知源现在使用专用类Flowable来表示。

**Observable源不支持背压。正因为如此，我们应该将它用于我们仅仅消费而无法影响的源**。

此外，如果我们处理大量元素，根据Observable的类型，可能会出现两种与[背压](https://www.baeldung.com/rxjava-backpressure)相关的场景。

在使用所谓的“冷Observable”的情况下，事件会延迟发出，因此我们不会溢出观察者。

但是，当使用“热Observable”时，这将继续发出事件，即使消费者跟不上。

## 3. 创建Flowable

有多种创建Flowable的方法。对我们来说方便的是，这些方法看起来类似于第一版本RxJava中Observable中的方法。

### 3.1 简单Flowable

我们可以像使用Observable一样使用just()方法创建一个Flowable：

```java
Flowable<Integer> integerFlowable = Flowable.just(1, 2, 3, 4);
```

尽管使用just()非常简单，但从静态数据创建Flowable并不常见，它用于测试目的。

### 3.2 从Observable创建Flowable

**当我们有一个Observable时，我们可以使用toFlowable()方法轻松地将它转换为Flowable**：

```java
Observable<Integer> integerObservable = Observable.just(1, 2, 3);
Flowable<Integer> integerFlowable = integerObservable
  	.toFlowable(BackpressureStrategy.BUFFER);
```

请注意，为了能够执行转换，我们需要使用BackpressureStrategy丰富Observable。我们将在下一节中描述可用的策略。

### 3.3 从FlowableOnSubscribe创建Flowable

**RxJava 2引入了一个函数式接口FlowableOnSubscribe，它表示一个Flowable在消费者订阅它之后开始发射事件**。

因此，所有客户端都将收到相同的事件集，这使得FlowableOnSubscribe背压安全。

当我们有FlowableOnSubscribe时，我们可以使用它来创建Flowable：

```java
FlowableOnSubscribe<Integer> flowableOnSubscribe = flowable -> flowable.onNext(1);
Flowable<Integer> integerFlowable = Flowable
    .create(flowableOnSubscribe, BackpressureStrategy.BUFFER);
```

该文档描述了更多创建Flowable的方法。

## 4. Flowable BackpressureStrategy

toFlowable()或create()等一些方法将BackpressureStrategy作为参数。

**BackpressureStrategy是一个枚举，它定义了我们将应用于Flowable的背压行为**。

它可以缓存或丢弃事件或根本不执行任何行为，在最后一种情况下，我们将负责定义它，使用背压运算符。

BackpressureStrategy类似于之前版本的RxJava中的BackpressureMode。

RxJava 2中有五种不同的策略可用。

### 4.1 BUFFER

**如果我们使用BackpressureStrategy.BUFFER，源将缓冲所有事件，直到订阅者可以消费它们**：

```java
public void thenAllValuesAreBufferedAndReceived() {
    List testList = IntStream.range(0, 100000)
      	.boxed()
      	.collect(Collectors.toList());
 
    Observable observable = Observable.fromIterable(testList);
    TestSubscriber<Integer> testSubscriber = observable
      	.toFlowable(BackpressureStrategy.BUFFER)
      	.observeOn(Schedulers.computation()).test();

    testSubscriber.awaitTerminalEvent();

    List<Integer> receivedInts = testSubscriber.getEvents()
        .get(0)
        .stream()
        .mapToInt(object -> (int) object)
        .boxed()
        .collect(Collectors.toList());

    assertEquals(testList, receivedInts);
}
```

它类似于在Flowable上调用onBackpressureBuffer()方法，但它不允许明确定义缓冲区大小或onOverflow操作。

### 4.2 Drop

**我们可以使用BackpressureStrategy.DROP丢弃无法消费的事件，而不是缓冲它们**。

同样，这类似于在Flowable上使用onBackpressureDrop()：

```java
public void whenDropStrategyUsed_thenOnBackpressureDropped() {
    Observable observable = Observable.fromIterable(testList);
    TestSubscriber<Integer> testSubscriber = observable
        .toFlowable(BackpressureStrategy.DROP)
        .observeOn(Schedulers.computation())
        .test();
    testSubscriber.awaitTerminalEvent();
    List<Integer> receivedInts = testSubscriber.getEvents()
        .get(0)
        .stream()
        .mapToInt(object -> (int) object)
        .boxed()
        .collect(Collectors.toList());

    assertThat(receivedInts.size() < testList.size());
    assertThat(!receivedInts.contains(100000));
 }
```

### 4.3 Latest

**使用BackpressureStrategy.LATEST将强制源仅保留最新事件，从而在消费者无法跟上时覆盖任何以前的值**：

```java
public void whenLatestStrategyUsed_thenTheLastElementReceived() {
    Observable observable = Observable.fromIterable(testList);
    TestSubscriber<Integer> testSubscriber = observable
        .toFlowable(BackpressureStrategy.LATEST)
        .observeOn(Schedulers.computation())
        .test();

    testSubscriber.awaitTerminalEvent();
    List<Integer> receivedInts = testSubscriber.getEvents()
        .get(0)
        .stream()
        .mapToInt(object -> (int) object)
        .boxed()
        .collect(Collectors.toList());

    assertThat(receivedInts.size() < testList.size());
    assertThat(receivedInts.contains(100000));
 }
```

当我们查看代码时，BackpressureStrategy.LATEST和BackpressureStrategy.DROP看起来非常相似。

**但是，BackpressureStrategy.LATEST将覆盖我们的订阅者无法处理的元素并仅保留最新的元素，因此得名**。

另一方面，BackpressureStrategy.DROP将丢弃无法处理的元素。这意味着不一定会发出最新的元素。

### 4.4 Error

当我们使用BackpressureStrategy.ERROR时，我们只是在说**我们不希望发生背压**。因此，如果消费者跟不上源，则应抛出MissingBackpressureException：

```java
public void whenErrorStrategyUsed_thenExceptionIsThrown() {
    Observable observable = Observable.range(1, 100000);
    TestSubscriber subscriber = observable
        .toFlowable(BackpressureStrategy.ERROR)
        .observeOn(Schedulers.computation())
        .test();

    subscriber.awaitTerminalEvent();
    subscriber.assertError(MissingBackpressureException.class);
}
```

### 4.5 Missing

**如果我们使用BackpressureStrategy.MISSING，源将推送元素而不丢弃或缓冲**。

在这种情况下，下游将不得不处理溢出：

```java
public void whenMissingStrategyUsed_thenException() {
    Observable observable = Observable.range(1, 100000);
    TestSubscriber subscriber = observable
        .toFlowable(BackpressureStrategy.MISSING)
        .observeOn(Schedulers.computation())
        .test();
    subscriber.awaitTerminalEvent();
    subscriber.assertError(MissingBackpressureException.class);
}
```

在我们的测试中，我们将MissingBackpressureException排除在ERROR和MISSING策略之外。因为当源的内部缓冲区溢出时，它们都会抛出这样的异常。

但是，值得注意的是，它们都有不同的目的。

当我们根本不期望背压时，我们应该使用前者，并且我们希望源在发生背压时抛出异常。

如果我们不想在创建Flowable时指定默认行为，则可以使用后者。我们稍后将使用背压运算符来定义它。

## 5. 总结

在本教程中，我们介绍了RxJava 2中引入的名为Flowable的新类。

要查找有关Flowable本身及其API的更多信息，我们可以参考[文档](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Flowable.html)。