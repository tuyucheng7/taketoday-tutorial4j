## 1. 概述

在本文中，我们将发现一些用于在RxJava中使用Observables的实用运算符以及如何实现自定义运算符。

**运算符是一个函数，它接收并改变上游Observable<T\>的行为并返回下游Observable<R\>或Subscriber**，其中类型T和R可能相同也可能不同。

运算符包装现有的Observables并通常通过拦截订阅来增强它们。这听起来可能很复杂，但它实际上非常灵活并且不难掌握。

## 2. Do运算符

有多种操作可以改变Observable生命周期事件。

**doOnNext运算符修改Observable源，以便它在调用onNext时调用一个操作**。

**doOnCompleted运算符注册一个操作，如果生成的Observable正常终止，调用Observer的onCompleted方法调用该操作**：

```java
Observable.range(1, 10)
    .doOnNext(r -> receivedTotal += r)
    .doOnCompleted(() -> result = "Completed")
    .subscribe();
 
assertTrue(receivedTotal == 55);
assertTrue(result.equals("Completed"));
```

doOnEach运算符修改Observable源，以便它通知每个元素的Observer并建立一个回调，该回调将在每次发射元素时调用。

doOnSubscribe运算符注册一个操作，每当观察者订阅生成的Observable就会调用该操作。

还有doOnUnsubscribe运算符，它做与doOnSubscribe相反的事情：

```java
Observable.range(1, 10)
    .doOnEach(new Observer<Integer>() {
        @Override
        public void onCompleted() {
            System.out.println("Complete");
        }
        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }
        @Override
        public void onNext(Integer value) {
            receivedTotal += value;
        }
    })
    .doOnSubscribe(() -> result = "Subscribed")
    .subscribe();
assertTrue(receivedTotal == 55);
assertTrue(result.equals("Subscribed"));
```

当Observable完成并出现错误时，我们可以使用doOnError运算符来执行操作。

doOnTerminate运算符注册一个将在Observable完成时调用的操作，无论是成功还是错误：

```java
thrown.expect(OnErrorNotImplementedException.class);
Observable.empty()
    .single()
    .doOnError(throwable -> { throw new RuntimeException("error");})
    .doOnTerminate(() -> result += "doOnTerminate")
    .doAfterTerminate(() -> result += "_doAfterTerminate")
    .subscribe();
assertTrue(result.equals("doOnTerminate_doAfterTerminate"));
```

还有一个**FinallyDo运算符-它已被弃用，取而代之的是doAfterTerminate。它在Observable完成时注册一个操作**。

## 3. observeOn与subscribeOn

**默认情况下，Observable和运算符链将在调用其Subscribe方法的同一线程上运行**。

observeOn运算符指定了一个不同的调度器，Observable将使用该调度器向观察者发送通知：

```java
Observable.range(1, 5)
    .map(i -> i * 100)
    .doOnNext(i -> {
        emittedTotal += i;
        System.out.println("Emitting " + i + " on thread " + Thread.currentThread().getName());
    })
    .observeOn(Schedulers.computation())
    .map(i -> i * 10)
    .subscribe(i -> {
        receivedTotal += i;
        System.out.println("Received " + i + " on thread " + Thread.currentThread().getName());
    });

Thread.sleep(2000);
assertTrue(emittedTotal == 1500);
assertTrue(receivedTotal == 15000);
```

我们看到元素是在主线程中生成的，并一直被推送到第一个map调用。

但在那之后，observeOn将处理重定向到一个computation线程，用于处理map和最终的Subscriber。

**observeOn可能出现的一个问题是底部流产生排放物的速度快于顶部流处理它们的速度**。这可能会导致我们可能不得不考虑的[背压问题](https://www.baeldung.com/rxjava-backpressure)。

要指定Observable应该在哪个调度器上运行，我们可以使用subscribeOn运算符：

```java
Observable.range(1, 5)
    .map(i -> i * 100)
    .doOnNext(i -> {
        emittedTotal += i;
        System.out.println("Emitting " + i + " on thread " + Thread.currentThread().getName());
    })
    .subscribeOn(Schedulers.computation())
    .map(i -> i * 10)
    .subscribe(i -> {
        receivedTotal += i;
        System.out.println("Received " + i + " on thread " + Thread.currentThread().getName());
    });

Thread.sleep(2000);
assertTrue(emittedTotal == 1500);
assertTrue(receivedTotal == 15000);
```

subscribeOn指示源Observable使用哪个线程来发射元素-只有这个线程才会将元素推送到Subscriber。它可以放在流中的任何位置，因为它只影响订阅。

实际上，我们只能使用一个subscribeOn，但我们可以有任意数量的observeOn运算符。通过使用observeOn，我们可以轻松地将发射从一个线程切换到另一个线程。

## 4. single和singleOrDefault

**运算符single返回一个Observable，该Observable发出由源Observable发出的单个元素**：

```java
Observable.range(1, 1)
    .single()
    .subscribe(i -> receivedTotal += i);
assertTrue(receivedTotal == 1);
```

如果源Observable产生零个或多个元素，将抛出异常：

```java
Observable.empty()
    .single()
    .onErrorReturn(e -> receivedTotal += 10)
    .subscribe();
assertTrue(receivedTotal == 10);
```

另一方面，运算符singleOrDefault与Single非常相似，这意味着它也返回一个从源发出单个元素的Observable，但另外，我们可以指定一个默认值：

```java
Observable.empty()
    .singleOrDefault("Default")
    .subscribe(i -> result +=i);
assertTrue(result.equals("Default"));
```

但是如果Observable源发射了不止一项，它仍然会抛出一个IllegalArgumentException：

```java
Observable.range(1, 3)
    .singleOrDefault(5)
    .onErrorReturn(e -> receivedTotal += 10)
    .subscribe();
assertTrue(receivedTotal == 10);
```

简单的总结：

-   如果预期源Observable可能没有或只有一个元素，那么应该使用singleOrDefault
-   如果我们正在处理Observable中可能发出的多个元素，并且我们只想发出第一个或最后一个值，我们可以使用其他运算符，如first或last

## 5. timestamp

**timestamp运算符在源Observable发射的每个元素上附加一个时间戳**，然后再按其自己的顺序重新发射该元素。时间戳表示元素发出的时间：

```java
Observable.range(1, 10)
    .timestamp()
    .map(o -> result = o.getClass().toString() )
    .last()
    .subscribe();
 
assertTrue(result.equals("class rx.schedulers.Timestamped"));
```

## 6. delay

**该运算符通过在发出源Observable的每个元素之前暂停特定的时间增量来修改其源Observable**。

它使用提供的值偏移整个序列：

```java
Observable source = Observable.interval(1, TimeUnit.SECONDS)
    .take(5)
    .timestamp();

Observable delayedObservable = source.delay(2, TimeUnit.SECONDS);

source.subscribe(
    value -> System.out.println("source :" + value),
    t -> System.out.println("source error"),
    () -> System.out.println("source completed"));

delayedObservable.subscribe(
    value -> System.out.println("delay : " + value),
    t -> System.out.println("delay error"),
    () -> System.out.println("delay completed"));
Thread.sleep(8000);
```

还有一个替代运算符，我们可以使用它来延迟对源Observable的订阅，称为delaySubscription。

默认情况下，延迟运算符在computation[调度器](http://reactivex.io/documentation/scheduler.html)上运行，但我们可以通过将其作为可选的第三个参数传递给delaySubscription来选择不同的调度器。

## 7. repeat

**repeat只是拦截来自上游的完成通知，而不是将其传递给下游，而是重新订阅**。

因此，不能保证repeat会在相同的事件序列中保持循环，但当上游是固定流时恰好是这种情况：

```java
Observable.range(1, 3)
    .repeat(3)
    .subscribe(i -> receivedTotal += i);
 
assertTrue(receivedTotal == 18);
```

## 8. cache

cache运算符位于订阅和我们自定义的Observable之间。

**当第一个订阅者出现时，cache将订阅委托给底层的Observable，并将所有通知(事件、完成或错误)转发到下游**。

但是，与此同时，它会在内部保留所有通知的副本。当后续订阅者想要接收推送的通知时，cache不再委托给底层的Observable，而是提供缓存的值：

```java
Observable<Integer> source =
    Observable.<Integer>create(subscriber -> {
        System.out.println("Create");
        subscriber.onNext(receivedTotal += 5);
        subscriber.onCompleted();
    }).cache();
source.subscribe(i -> {
    System.out.println("element 1");
    receivedTotal += 1;
});
source.subscribe(i -> {
    System.out.println("element 2");
    receivedTotal += 2;
});
 
assertTrue(receivedTotal == 8);
```

## 9. using

当观察者订阅从using()返回的Observable时，它将使用Observable工厂函数来创建观察者将观察的Observable，同时使用资源工厂函数创建我们设计要创建的任何资源。

当观察者取消订阅Observable或Observable终止时，using将调用第三个函数来释放创建的资源：

```java
Observable<Character> values = Observable.using(
    () -> "resource",
    r -> {
        return Observable.create(o -> {
            for (Character c : r.toCharArray()) {
                o.onNext(c);
            }
            o.onCompleted();
        });
    },
    r -> System.out.println("Disposed: " + r)
);
values.subscribe(
    v -> result += v,
    e -> result += e
);
assertTrue(result.equals("resource"));
```

## 10. 总结

在本文中，我们讨论了如何使用RxJava实用程序运算符以及如何探索它们最重要的功能。

RxJava的真正力量在于它的运算符。数据流的声明式转换既安全又富有表现力和灵活性。

凭借在函数式编程方面的坚实基础，运算符在RxJava的采用中起着决定性的作用。掌握内置运算符是在此库中取得成功的关键。