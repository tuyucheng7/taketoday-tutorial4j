## 1. 简介

RxJava的普及促进创建了多个扩展其功能的第三方库。

其中许多库都是对开发人员在使用RxJava时遇到的典型问题的答案，[RxRelay](https://github.com/JakeWharton/RxRelay)就是这些解决方案之一。

## 2. 处理Subject

简单地说，Subject充当Observable和Observer之间的桥梁。由于它是一个Observer，它可以订阅一个或多个Observables并从它们接收事件。

此外，鉴于它同时是一个Observable，它可以重新发送事件或向其订阅者发送新事件。有关Subject的更多信息，请参见[本文](https://www.baeldung.com/rx-java)。

Subject的问题之一是在它收到onComplete()或onError()之后-它不再能够移动数据。有时这是期望的行为，但有时不是。

在不需要这种行为的情况下，我们应该考虑使用RxRelay。

## 3. Relay

**一个Relay基本上是一个Subject，但是没有调用onComplete()和onError()的能力，因此它能够不断地发射数据**。

这使我们能够在不同类型的API之间创建桥梁，而不必担心意外触发终止状态。

要使用RxRelay，我们需要将以下依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>com.jakewharton.rxrelay2</groupId>
    <artifactId>rxrelay</artifactId>
    <version>1.2.0</version>
</dependency>
```

## 4. Relay的类型

库中提供了三种不同类型的Relay，我们将在这里快速探索所有这三个。

### 4.1 PublishRelay

一旦观察者订阅了这种类型的Relay，它就会重新发送所有事件。

事件将发送给所有订阅者：

```java
public void whenObserverSubscribedToPublishRelay_itReceivesEmittedEvents() {
    PublishRelay<Integer> publishRelay = PublishRelay.create();
    TestObserver<Integer> firstObserver = TestObserver.create();
    TestObserver<Integer> secondObserver = TestObserver.create();
    
    publishRelay.subscribe(firstObserver);
    firstObserver.assertSubscribed();
    publishRelay.accept(5);
    publishRelay.accept(10);
    publishRelay.subscribe(secondObserver);
    secondObserver.assertSubscribed();
    publishRelay.accept(15);
    firstObserver.assertValues(5, 10, 15);
    
    // second receives only the last event
    secondObserver.assertValue(15);
}
```

在这种情况下没有事件缓冲，因此这种行为类似于冷Observable。

### 4.2 BehaviorRelay

一旦观察者订阅，这种类型的Relay将重新发送最近观察到的事件和所有后续事件：

```java
public void whenObserverSubscribedToBehaviorRelay_itReceivesEmittedEvents() {
    BehaviorRelay<Integer> behaviorRelay = BehaviorRelay.create();
    TestObserver<Integer> firstObserver = TestObserver.create();
    TestObserver<Integer> secondObserver = TestObserver.create();
    behaviorRelay.accept(5);     
    behaviorRelay.subscribe(firstObserver);
    behaviorRelay.accept(10);
    behaviorRelay.subscribe(secondObserver);
    behaviorRelay.accept(15);
    firstObserver.assertValues(5, 10, 15);
    secondObserver.assertValues(10, 15);
}
```

当我们创建BehaviorRelay时，我们可以指定默认值，如果没有其他事件可发出，则默认值将被发出。

要指定默认值，我们可以使用createDefault()方法：

```java
public void whenObserverSubscribedToBehaviorRelay_itReceivesDefaultValue() {
    BehaviorRelay<Integer> behaviorRelay = BehaviorRelay.createDefault(1);
    TestObserver<Integer> firstObserver = new TestObserver<>();
    behaviorRelay.subscribe(firstObserver);
    firstObserver.assertValue(1);
}
```

如果我们不想指定默认值，我们可以使用create()方法：

```java
public void whenObserverSubscribedToBehaviorRelayWithoutDefaultValue_itIsEmpty() {
    BehaviorRelay<Integer> behaviorRelay = BehaviorRelay.create();
    TestObserver<Integer> firstObserver = new TestObserver<>();
    behaviorRelay.subscribe(firstObserver);
    firstObserver.assertEmpty();
}
```

### 4.3 ReplayRelay

这种类型的Relay缓冲它接收到的所有事件，然后将其重新发送给所有订阅它的订阅者：

```java
public void whenObserverSubscribedToReplayRelay_itReceivesEmittedEvents() {
    ReplayRelay<Integer> replayRelay = ReplayRelay.create();
    TestObserver<Integer> firstObserver = TestObserver.create();
    TestObserver<Integer> secondObserver = TestObserver.create();
    replayRelay.subscribe(firstObserver);
    replayRelay.accept(5);
    replayRelay.accept(10);
    replayRelay.accept(15);
    replayRelay.subscribe(secondObserver);
    firstObserver.assertValues(5, 10, 15);
    secondObserver.assertValues(5, 10, 15);
}
```

所有元素都被缓冲并且所有订阅者都会收到相同的事件，因此**这种行为类似于冷Observable**。

当我们创建ReplayRelay时，我们可以为事件提供最大缓冲区大小和生存时间。

要创建具有有限缓冲区大小的Relay，我们可以使用createWithSize()方法。当要缓冲的事件多于设置的缓冲区大小时，前面的元素将被丢弃：

```java
public void whenObserverSubscribedToReplayRelayWithLimitedSize_itReceivesEmittedEvents() {
    ReplayRelay<Integer> replayRelay = ReplayRelay.createWithSize(2);
    TestObserver<Integer> firstObserver = TestObserver.create();
    replayRelay.accept(5);
    replayRelay.accept(10);
    replayRelay.accept(15);
    replayRelay.accept(20);
    replayRelay.subscribe(firstObserver);
    firstObserver.assertValues(15, 20);
}
```

我们还可以使用createWithTime()方法创建ReplayRelay，为缓冲事件留出最大时间：

```java
public void whenObserverSubscribedToReplayRelayWithMaxAge_thenItReceivesEmittedEvents() {
    SingleScheduler scheduler = new SingleScheduler();
    ReplayRelay<Integer> replayRelay =
        ReplayRelay.createWithTime(2000, TimeUnit.MILLISECONDS, scheduler);
    long current =  scheduler.now(TimeUnit.MILLISECONDS);
    TestObserver<Integer> firstObserver = TestObserver.create();
    replayRelay.accept(5);
    replayRelay.accept(10);
    replayRelay.accept(15);
    replayRelay.accept(20);
    Thread.sleep(3000);
    replayRelay.subscribe(firstObserver);
    firstObserver.assertEmpty();
}
```

## 5. 自定义Relay

上面描述的所有类型都扩展了公共抽象类Relay，这使我们能够编写自己的自定义Relay类。

要创建自定义Relay，我们需要实现三个方法：accept()、hasObservers()和subscribeActual()。

让我们编写一个简单的Relay，它将向随机选择的订阅者之一重新发送事件：

```java
public class RandomRelay extends Relay<Integer> {
    Random random = new Random();

    List<Observer<? super Integer>> observers = new ArrayList<>();

    @Override
    public void accept(Integer integer) {
        int observerIndex = random.nextInt() % observers.size();
        observers.get(observerIndex).onNext(integer);
    }

    @Override
    public boolean hasObservers() {
        return observers.isEmpty();
    }

    @Override
    protected void subscribeActual(Observer<? super Integer> observer) {
        observers.add(observer);
        observer.onSubscribe(Disposables.fromRunnable(
              () -> System.out.println("Disposed")));
    }
}
```

现在，我们可以测试只有一个订阅者会收到事件：

```java
public void whenTwoObserversSubscribedToRandomRelay_thenOnlyOneReceivesEvent() {
    RandomRelay randomRelay = new RandomRelay();
    TestObserver<Integer> firstObserver = TestObserver.create();
    TestObserver<Integer> secondObserver = TestObserver.create();
    randomRelay.subscribe(firstObserver);
    randomRelay.subscribe(secondObserver);
    randomRelay.accept(5);
    if(firstObserver.values().isEmpty()) {
        secondObserver.assertValue(5);
    } else {
        firstObserver.assertValue(5);
        secondObserver.assertEmpty();
    }
}
```

## 6. 总结

在本教程中，我们了解了RxRelay，它是一种类似于Subject但无法触发终端状态的类型。