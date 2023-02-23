## 1. 简介

在[RxJava简介](https://www.baeldung.com/rx-java)之后，我们将看看过滤运算符。

特别是，我们将重点关注过滤、跳过、时间过滤和一些更高级的过滤操作。

## 2. 过滤

使用Observable时，有时只选择发射元素的一个子集很有用。为此，**RxJava提供了各种过滤功能**。

让我们开始看一下过滤方法。

### 2.1 filter运算符

**简单地说，filter运算符过滤一个Observable以确保发出的元素匹配指定的条件**，该条件以谓词(Predicate)的形式出现。

让我们看看如何仅从发出的奇数中过滤奇数值：

```java
Observable<Integer> sourceObservable = Observable.range(1, 10);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable
    .filter(i -> i % 2 != 0);

filteredObservable.subscribe(subscriber);

subscriber.assertValues(1, 3, 5, 7, 9);
```

### 2.2 take运算符

**使用take进行过滤时，逻辑会导致发出前n个元素，同时忽略其余元素**。

让我们看看如何过滤源Observable并仅发出前两个：

```java
Observable<Integer> sourceObservable = Observable.range(1, 10);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable.take(3);

filteredObservable.subscribe(subscriber);

subscriber.assertValues(1, 2, 3);
```

### 2.3 takeWhile运算符

**当使用takeWhile时，过滤后的Observable将继续发射元素，直到它遇到第一个与Predicate不匹配的元素**。

让我们看看我们如何使用takeWhile(带有过滤谓词)：

```java
Observable<Integer> sourceObservable = Observable.just(1, 2, 3, 4, 3, 2, 1);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable
    .takeWhile(i -> i < 4);

filteredObservable.subscribe(subscriber);

subscriber.assertValues(1, 2, 3);
```

### 2.4 takeFirst运算符

**每当我们只想发出第一个符合给定条件的元素时，我们可以使用takeFirst()**。

让我们快速看一下我们如何发出大于5的第一个元素：

```java
Observable<Integer> sourceObservable = Observable
    .just(1, 2, 3, 4, 5, 7, 6);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable
    .takeFirst(x -> x > 5);

filteredObservable.subscribe(subscriber);

subscriber.assertValue(7);
```

### 2.5 first和firstOrDefault运算符

使用first API可以实现类似的行为：

```java
Observable<Integer> sourceObservable = Observable.range(1, 10);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable.first();

filteredObservable.subscribe(subscriber);

subscriber.assertValue(1);
```

**但是，如果我们想指定一个默认值，如果没有发出任何元素，我们可以使用firstOrDefault**：

```java
Observable<Integer> sourceObservable = Observable.empty();

Observable<Integer> filteredObservable = sourceObservable.firstOrDefault(-1);

filteredObservable.subscribe(subscriber);

subscriber.assertValue(-1);
```

### 2.6 takeLast运算符

**接下来，如果我们只想发出Observable发出的最后n个元素，我们可以使用takeLast**。

让我们看看如何只发出最后三个元素：

```java
Observable<Integer> sourceObservable = Observable.range(1, 10);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable.takeLast(3);

filteredObservable.subscribe(subscriber);

subscriber.assertValues(8, 9, 10);
```

我们必须记住，这会延迟源Observable中任何元素的发射，直到它完成。

### 2.7 last和lastOrDefault

**如果我们只想发出最后一个元素，而不是使用takeLast(1)，我们可以使用last**。

这会过滤Observable，仅发出最后一个元素，该元素可以选择验证过滤谓词：

```java
Observable<Integer> sourceObservable = Observable.range(1, 10);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable
    .last(i -> i % 2 != 0);

filteredObservable.subscribe(subscriber);

subscriber.assertValue(9);
```

如果Observable是空的，我们可以使用lastOrDefault来过滤发出默认值的Observable。

如果使用lastOrDefault运算符并且没有任何元素验证过滤条件，则也会发出默认值：

```java
Observable<Integer> sourceObservable = Observable.range(1, 10);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable.lastOrDefault(-1, i -> i > 10);

filteredObservable.subscribe(subscriber);

subscriber.assertValue(-1);
```

### 2.8 elementAt和elementAtOrDefault运算符

使用elementAt运算符，我们可以选择源Observable发出的单个元素，指定其索引：

```java
Observable<Integer> sourceObservable = Observable
    .just(1, 2, 3, 5, 7, 11);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable.elementAt(4);

filteredObservable.subscribe(subscriber);

subscriber.assertValue(7);
```

但是，如果指定的索引超过发出的元素数，elementAt将抛出IndexOutOfBoundException。

为了避免这种情况，可以使用elementAtOrDefault-如果索引超出范围，它将返回一个默认值：

```java
Observable<Integer> sourceObservable = Observable
    .just(1, 2, 3, 5, 7, 11);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable.elementAtOrDefault(7, -1);

filteredObservable.subscribe(subscriber);

subscriber.assertValue(-1);
```

### 2.9 ofType运算符

**每当Observable发出Object元素时，都可以根据它们的类型过滤它们**。

让我们看看我们如何只过滤发出的String类型的元素：

```java
Observable sourceObservable = Observable.just(1, "two", 3, "five", 7, 11);
TestSubscriber subscriber = new TestSubscriber();

Observable filteredObservable = sourceObservable.ofType(String.class);

filteredObservable.subscribe(subscriber);

subscriber.assertValues("two", "five");
```

## 3. 跳过

另一方面，当我们想要过滤掉或跳过Observable发出的某些元素时，**RxJava提供了一些运算符作为我们之前讨论过的过滤运算符的对应物**。

让我们开始研究skip运算符，它是take的对应物。

### 3.1 skip运算符

当Observable发出一系列元素时，可以使用skip过滤掉或跳过一些最先发出的元素。

例如。让我们看看如何跳过前四个元素：

```java
Observable<Integer> sourceObservable = Observable.range(1, 10);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable.skip(4);

filteredObservable.subscribe(subscriber);

subscriber.assertValues(5, 6, 7, 8, 9, 10);
```

### 3.2 skipWhile运算符

**每当我们想过滤掉Observable发出的所有未通过过滤谓词的第一个值时，我们可以使用skipWhile运算符**：

```java
Observable<Integer> sourceObservable = Observable
    .just(1, 2, 3, 4, 5, 4, 3, 2, 1);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable
    .skipWhile(i -> i < 4);

filteredObservable.subscribe(subscriber);

subscriber.assertValues(4, 5, 4, 3, 2, 1);
```

### 3.3 skipLast运算符

**skipLast运算符允许我们跳过Observable发出的最终元素，只接受在它们之前发出的那些**。

例如，跳过最后五项：

```java
Observable<Integer> sourceObservable = Observable.range(1, 10);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = sourceObservable.skipLast(5);

filteredObservable.subscribe(subscriber);

subscriber.assertValues(1, 2, 3, 4, 5);
```

### 3.4 distinct和distinctUntilChanged运算符

**distinct运算符返回一个Observable，它发出sourceObservable发出的所有不同的元素**：

```java
Observable<Integer> sourceObservable = Observable
    .just(1, 1, 2, 2, 1, 3, 3, 1);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> distinctObservable = sourceObservable.distinct();

distinctObservable.subscribe(subscriber);

subscriber.assertValues(1, 2, 3);
```

但是，如果我们想要获得一个Observable，它发出sourceObservable发出的所有不同于其直接前身的元素，我们可以使用distinctUntilChanged运算符：

```java
Observable<Integer> sourceObservable = Observable
    .just(1, 1, 2, 2, 1, 3, 3, 1);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> distinctObservable = sourceObservable.distinctUntilChanged();

distinctObservable.subscribe(subscriber);

subscriber.assertValues(1, 2, 1, 3, 1);
```

### 3.5 ignoreElements运算符

每当我们想要忽略sourceObservable发出的所有元素时，我们可以简单地使用ignoreElements：

```java
Observable<Integer> sourceObservable = Observable.range(1, 10);
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> ignoredObservable = sourceObservable.ignoreElements();

ignoredObservable.subscribe(subscriber);

subscriber.assertNoValues();
```

## 4. 时间过滤运算符

在处理Observable序列时，时间轴是未知的，但有时从序列中获取及时数据可能很有用。

出于这个目的，**RxJava提供了一些方法，允许我们使用时间轴来处理Observable**。

在继续第一个之前，让我们定义一个定时Observable，它将每秒发出一个元素：

```java
TestScheduler testScheduler = new TestScheduler();

Observable<Integer> timedObservable = Observable
    .just(1, 2, 3, 4, 5, 6)
    .zipWith(Observable.interval(
        0, 1, TimeUnit.SECONDS, testScheduler), (item, time) -> item);
```

**TestScheduler是一个特殊的调度器，它允许以我们喜欢的任何速度手动推进时钟**。

### 4.1 sample和throttleLast运算符

sample运算符过滤timedObservable，返回一个Observable，该Observable会在周期时间间隔内发出此API发出的最新元素。

让我们看看如何对timedObservable进行采样，每2.5秒仅过滤最后一个发出的元素：

```java
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> sampledObservable = timedObservable
    .sample(2500L, TimeUnit.MILLISECONDS, testScheduler);

sampledObservable.subscribe(subscriber);

testScheduler.advanceTimeBy(7, TimeUnit.SECONDS);

subscriber.assertValues(3, 5, 6);
```

这种行为也可以使用throttleLast运算符来实现。

### 4.2 throttleFirst运算符

throttleFirst运算符与throttleLast/sample不同，因为它在每个采样周期中发出timedObservable发出的第一个元素，而不是最近发出的元素。

让我们看看如何使用4秒的采样周期发出第一个元素：

```java
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = timedObservable
    .throttleFirst(4100L, TimeUnit.SECONDS, testScheduler);

filteredObservable.subscribe(subscriber);

testScheduler.advanceTimeBy(7, TimeUnit.SECONDS);

subscriber.assertValues(1, 6);
```

### 4.3 debounce和throttleWithTimeout运算符

使用debounce运算符，可以在特定时间跨度过去后仅发出一个元素而不发出另一个元素。

因此，如果我们选择的时间跨度大于timedObservable发出的元素之间的时间间隔，它只会发出最后一个。另一方面，如果它更小，它将发出timedObservable发出的所有元素。

让我们看看在第一种情况下会发生什么：

```java
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = timedObservable
    .debounce(2000L, TimeUnit.MILLISECONDS, testScheduler);

filteredObservable.subscribe(subscriber);

testScheduler.advanceTimeBy(7, TimeUnit.SECONDS);

subscriber.assertValue(6);
```

这种行为也可以使用throttleWithTimeout来实现。

### 4.4 timeout运算符

timeout运算符会镜像源Observable，但如果源Observable在指定的时间间隔内未能发出任何元素，则会发出通知错误，中止元素的发射。

让我们看看如果我们为timedObservable指定500毫秒的超时会发生什么：

```java
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = timedObservable
   .timeout(500L, TimeUnit.MILLISECONDS, testScheduler);

filteredObservable.subscribe(subscriber);

testScheduler.advanceTimeBy(7, TimeUnit.SECONDS);

subscriber.assertError(TimeoutException.class); subscriber.assertValues(1);
```

## 5. 多重Observable过滤

使用Observable时，绝对可以根据第二个Observable来决定是过滤还是跳过元素。

在继续之前，让我们定义一个delayedObservable，它将在3秒后仅发出1个元素：

```java
Observable<Integer> delayedObservable = Observable.just(1)
    .delay(3, TimeUnit.SECONDS, testScheduler);
```

让我们从takeUntil运算符开始。

### 5.1 takeUntil运算符

在第二个Observable(delayedObservable)发出一个元素或终止后，takeUntil运算符丢弃源Observable(timedObservable)发出的任何元素：

```java
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = timedObservable
    .skipUntil(delayedObservable);

filteredObservable.subscribe(subscriber);

testScheduler.advanceTimeBy(7, TimeUnit.SECONDS);

subscriber.assertValues(4, 5, 6);
```

### 5.2 skipUntil运算符

另一方面，skipUntil丢弃源Observable(timedObservable)发出的任何元素，直到第二个Observable(delayedObservable)发出一个元素：

```java
TestSubscriber<Integer> subscriber = new TestSubscriber();

Observable<Integer> filteredObservable = timedObservable
    .takeUntil(delayedObservable);

filteredObservable.subscribe(subscriber);

testScheduler.advanceTimeBy(7, TimeUnit.SECONDS);

subscriber.assertValues(1, 2, 3);
```

## 6. 总结

在这个广泛的教程中，我们探索了RxJava中可用的不同过滤运算符，并为每个运算符提供了一个简单的示例。