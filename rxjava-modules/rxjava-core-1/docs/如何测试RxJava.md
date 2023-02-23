## 1. 概述

在本文中，我们将研究测试使用[RxJava](https://github.com/ReactiveX/RxJava)编写的代码。

我们使用RxJava创建的典型流由Observable和Observer组成，Observable是作为元素序列的数据源，一个或多个Observer订阅它以接收发出的事件。

通常，观察者和可观察者对象以异步方式在单独的线程中执行-这使得代码难以以传统方式进行测试。

幸运的是，**RxJava提供了一个[TestSubscriber](http://reactivex.io/RxJava/javadoc/rx/observers/TestSubscriber.html)类，它使我们能够测试异步的、事件驱动的流**。

## 2. 测试RxJava-传统方式

**让我们从一个例子开始**-我们有一个字母序列，我们想要用一个从1开始的整数序列来压缩。

我们的测试应该断言监听由压缩的可观察对象发出的事件的订阅者接收到用整数压缩的字母。

以传统方式编写这样的测试意味着我们需要保留结果列表并从观察者那里更新该列表。将元素添加到整数列表意味着我们的可观察对象和观察者需要在同一个线程中工作-它们不能异步工作。

**因此我们将错过RxJava的最大优势之一-在单独的线程中处理事件**。

这是测试的受限版本的样子：

```java
List<String> letters = Arrays.asList("A", "B", "C", "D", "E");
List<String> results = new ArrayList<>();
Observable<String> observable = Observable
    .from(letters)
    .zipWith(
        Observable.range(1, Integer.MAX_VALUE), 
        (string, index) -> index + "-" + string);

observable.subscribe(results::add);

assertThat(results, notNullValue());
assertThat(results, hasSize(5));
assertThat(results, hasItems("1-A", "2-B", "3-C", "4-D", "5-E"));
```

我们通过向results列表添加元素来聚合来自观察者的结果。观察者和可观察者对象在同一个线程中工作，因此我们的断言正确地阻塞并等待subscribe()方法完成。

## 3. 使用TestSubscriber测试RxJava

RxJava带有一个TestSubscriber类，它允许我们编写与事件的异步处理一起工作的测试，这是一个订阅Observable的普通观察者。

在测试中，我们可以检查TestSubscriber的状态并对该状态进行断言：

```java
List<String> letters = Arrays.asList("A", "B", "C", "D", "E");
TestSubscriber<String> subscriber = new TestSubscriber<>();

Observable<String> observable = Observable
    .from(letters)
    .zipWith(
        Observable.range(1, Integer.MAX_VALUE), 
        ((string, index) -> index + "-" + string));

observable.subscribe(subscriber);

subscriber.assertCompleted();
subscriber.assertNoErrors();
subscriber.assertValueCount(5);
assertThat(
    subscriber.getOnNextEvents(),
    hasItems("1-A", "2-B", "3-C", "4-D", "5-E"));
```

我们将TestSubscriber实例传递给observable对象的subscribe()方法。然后我们可以检查这个订阅者的状态。

**TestSubscriber有一些非常有用的断言方法**，我们将使用它们来验证我们的期望。订阅者应该从观察者接收到5个发出的元素，我们通过调用assertValueCount()方法断言。

我们可以通过调用getOnNextEvents()方法检查订阅者收到的所有事件。

调用assertCompleted()方法检查观察者订阅的流是否已完成。assertNoErrors()方法断言订阅流时没有错误。

## 4. 测试预期异常

有时在我们的处理过程中，当一个observable正在发出事件或者一个观察者正在处理事件时，会发生错误。TestSubscriber有一个检查错误状态的特殊方法-assertError()方法，它将异常的类型作为参数：

```java
List<String> letters = Arrays.asList("A", "B", "C", "D", "E");
TestSubscriber<String> subscriber = new TestSubscriber<>();

Observable<String> observable = Observable
    .from(letters)
    .zipWith(Observable.range(1, Integer.MAX_VALUE), ((string, index) -> index + "-" + string))
    .concatWith(Observable.error(new RuntimeException("error in Observable")));

observable.subscribe(subscriber);

subscriber.assertError(RuntimeException.class);
subscriber.assertNotCompleted();
```

我们正在使用concatWith()方法创建与另一个可观察对象连接的可观察对象，第二个可观察对象在发出下一个事件时抛出RuntimeException。我们可以通过调用assertError()方法在TestSubscriber上检查该异常的类型。

收到错误的观察者停止处理并以未完成状态结束，可以通过assertNotCompleted()方法检查该状态。

## 5. 测试基于时间的Observable

假设我们有一个每秒发出一个事件的Observable，我们想用TestSubscriber测试该行为。

我们可以使用Observable.interval()方法定义一个基于时间的Observable，并传递一个TimeUnit作为参数：

```java
List<String> letters = Arrays.asList("A", "B", "C", "D", "E");
TestScheduler scheduler = new TestScheduler();
TestSubscriber<String> subscriber = new TestSubscriber<>();
Observable<Long> tick = Observable.interval(1, TimeUnit.SECONDS, scheduler);

Observable<String> observable = Observable.from(letters)
    .zipWith(tick, (string, index) -> index + "-" + string);

observable.subscribeOn(scheduler)
    .subscribe(subscriber);
```

tick observable将每隔一秒发出一个新值。

在测试开始时我们的时间为零，因此我们的TestSubscriber将不会完成：

```java
subscriber.assertNoValues();
subscriber.assertNotCompleted();
```

为了在我们的测试中模拟时间流逝，我们需要使用[TestScheduler](http://reactivex.io/RxJava/javadoc/rx/schedulers/TestScheduler.html)类。我们可以通过调用TestScheduler上的advanceTimeBy()方法来模拟一秒钟的传递：

```java
scheduler.advanceTimeBy(1, TimeUnit.SECONDS);
```

advanceTimeBy()方法将使可观察对象产生一个事件。我们可以断言一个事件是通过调用assertValueCount()方法产生的：

```java
subscriber.assertNoErrors();
subscriber.assertValueCount(1);
subscriber.assertValues("0-A");
```

我们的letters列表中有5个元素，所以当我们想要让一个可观察对象发出所有事件时，需要经过6秒的处理。为了模拟这6秒，我们使用advanceTimeTo()方法：

```java
scheduler.advanceTimeTo(6, TimeUnit.SECONDS);
 
subscriber.assertCompleted();
subscriber.assertNoErrors();
subscriber.assertValueCount(5);
assertThat(subscriber.getOnNextEvents(), hasItems("0-A", "1-B", "2-C", "3-D", "4-E"));
```

在模拟经过的时间后，我们可以在TestSubscriber上执行断言。我们可以断言所有事件都是通过调用assertValueCount()方法产生的。

## 6. 总结

在本文中，我们研究了在RxJava中测试观察者和可观察对象的方法。我们研究了一种测试发出的事件、错误和基于时间的可观察对象的方法。