## 1. 概述

多个订阅者的默认行为并不总是可取的。在本文中，我们将介绍如何更改此行为并以正确的方式处理多个订阅者。

但首先，让我们看一下多个订阅者的默认行为。

## 2. 默认行为

假设我们有以下Observable：

```java
private static Observable getObservable() {
    return Observable.create(subscriber -> {
        subscriber.onNext(gettingValue(1));
        subscriber.onNext(gettingValue(2));

        subscriber.add(Subscriptions.create(() -> {
            LOGGER.info("Clear resources");
        }));
    });
}
```

一旦Subscribers订阅，它就会发出两个元素。

在我们的示例中，我们有两个订阅者：

```java
LOGGER.info("Subscribing");

Subscription s1 = obs.subscribe(i -> LOGGER.info("subscriber#1 is printing " + i));
Subscription s2 = obs.subscribe(i -> LOGGER.info("subscriber#2 is printing " + i));

s1.unsubscribe();
s2.unsubscribe();
```

想象一下，获取每个元素是一项成本高昂的操作-例如，它可能包括密集计算或打开URL连接。

为了简单起见，我们只返回一个数字：

```java
private static Integer gettingValue(int i) {
    LOGGER.info("Getting " + i);
    return i;
}
```

这是输出：

```bash
Subscribing
Getting 1
subscriber#1 is printing 1
Getting 2
subscriber#1 is printing 2
Getting 1
subscriber#2 is printing 1
Getting 2
subscriber#2 is printing 2
Clear resources
Clear resources
```

正如我们所看到的，**获取每个元素以及清除资源在默认情况下执行两次**-每个Subscriber一次，这不是我们想要的。ConnectableObservable类有助于解决这个问题。

## 3. ConnectableObservable

[ConnectableObservable](http://reactivex.io/RxJava/javadoc/rx/observables/ConnectableObservable.html)类允许与多个订阅者共享订阅，而不是多次执行底层操作。

但首先，让我们创建一个ConnectableObservable。

### 3.1 publish()

publish()方法是从Observable创建ConnectableObservable的方法：

```java
ConnectableObservable obs = Observable.create(subscriber -> {
    subscriber.onNext(gettingValue(1));
    subscriber.onNext(gettingValue(2));
    subscriber.add(Subscriptions.create(() -> {
        LOGGER.info("Clear resources");
    }));
}).publish();
```

但就目前而言，它什么都不做。使它起作用的是connect()方法。

### 3.2 connect()

**在ConnectableObservable的connect()方法未被调用之前，即使有一些订阅者，Observable的onSubscribe()回调也不会被触发**。

让我们证明这一点：

```java
LOGGER.info("Subscribing");
obs.subscribe(i -> LOGGER.info("subscriber #1 is printing " + i));
obs.subscribe(i -> LOGGER.info("subscriber #2 is printing " + i));
Thread.sleep(1000);
LOGGER.info("Connecting");
Subscription s = obs.connect();
s.unsubscribe();
```

我们订阅然后在连接之前等待一秒钟，输出为：

```bash
Subscribing
Connecting
Getting 1
subscriber #1 is printing 1
subscriber #2 is printing 1
Getting 2
subscriber #1 is printing 2
subscriber #2 is printing 2
Clear resources
```

我们可以看到：

+   如我们所愿，获取元素只发生一次
+   清除资源也只发生一次
+   获取元素在订阅后一秒开始
+   订阅不再触发元素的发射，只有connect()才这样做

这种延迟可能是有益的-有时我们需要为所有订阅者提供相同的元素序列，即使其中一个订阅者比另一个订阅者早。

### 3.3 Observables的一致视图-subscribe()之后connect()

这个用例无法在我们之前的Observable上演示，因为它冷运行并且两个订阅者无论如何都会获得整个元素序列。

相反，想象一下，元素发射不依赖于订阅的时刻，例如鼠标单击时发出的事件。现在还假设第二个订阅者在第一个订阅者之后订阅了第二个订阅者。

第一个Subscriber将获得在此示例中发出的所有元素，而第二个Subscriber将仅接收一些元素。

另一方面，在正确的位置使用connect()方法可以为两个订阅者提供关于Observable序列的相同视图。

**热Observable示例**

让我们创建一个热Observable，它将在JFrame上单击鼠标时发出元素。

每个元素将是点击的x坐标：

```java
private static Observable getObservable() {
    return Observable.create(subscriber -> {
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                subscriber.onNext(e.getX());
            }
        });
        subscriber.add(Subscriptions.create(() {
            LOGGER.info("Clear resources");
            for (MouseListener listener : frame.getListeners(MouseListener.class)) {
                frame.removeMouseListener(listener);
            }
        }));
    });
}
```

**热Observable的默认行为**

现在，如果我们以第二个间隔依次订阅两个Subscriber，运行程序并开始点击，我们将看到第一个Subscriber将获得更多元素：

```java
public static void defaultBehaviour() throws InterruptedException {
    Observable obs = getObservable();

    LOGGER.info("subscribing #1");
    Subscription subscription1 = obs.subscribe((i) -> LOGGER.info("subscriber#1 is printing x-coordinate " + i));
    Thread.sleep(1000);
    LOGGER.info("subscribing #2");
    Subscription subscription2 = obs.subscribe((i) -> LOGGER.info("subscriber#2 is printing x-coordinate " + i));
    Thread.sleep(1000);
    LOGGER.info("unsubscribe#1");
    subscription1.unsubscribe();
    Thread.sleep(1000);
    LOGGER.info("unsubscribe#2");
    subscription2.unsubscribe();
}
```

```bash
subscribing #1
subscriber#1 is printing x-coordinate 280
subscriber#1 is printing x-coordinate 242
subscribing #2
subscriber#1 is printing x-coordinate 343
subscriber#2 is printing x-coordinate 343
unsubscribe#1
clearing resources
unsubscribe#2
clearing resources
```

**connect()在subscribe()之后**

为了使两个订阅者获得相同的序列，我们将把这个Observable转换为ConnectableObservable并在订阅两个Subscriber之后调用connect()：

```java
public static void subscribeBeforeConnect() throws InterruptedException {
    ConnectableObservable obs = getObservable().publish();

    LOGGER.info("subscribing #1");
    Subscription subscription1 = obs.subscribe(i -> LOGGER.info("subscriber#1 is printing x-coordinate " + i));
    Thread.sleep(1000);
    LOGGER.info("subscribing #2");
    Subscription subscription2 = obs.subscribe(i ->  LOGGER.info("subscriber#2 is printing x-coordinate " + i));
    Thread.sleep(1000);
    LOGGER.info("connecting:");
    Subscription s = obs.connect();
    Thread.sleep(1000);
    LOGGER.info("unsubscribe connected");
    s.unsubscribe();
}
```

现在他们将得到相同的序列：

```bash
subscribing #1
subscribing #2
connecting:
subscriber#1 is printing x-coordinate 317
subscriber#2 is printing x-coordinate 317
subscriber#1 is printing x-coordinate 364
subscriber#2 is printing x-coordinate 364
unsubscribe connected
clearing resources
```

所以重点是等待所有订阅者都准备就绪的那一刻，然后调用connect()。

例如，在Spring应用程序中，我们可以在应用程序启动期间订阅所有组件，并在onApplicationEvent()中调用connect()。

但是让我们回到我们的例子；请注意，connect()方法之前的所有点击都将丢失。如果我们不想错误元素而是相反地处理它们，我们可以将connect()放在代码的前面，并强制Observable在没有任何Subscriber的情况下产生事件。

### 3.4 在没有任何订阅者的情况下强制订阅-connect()先于subscribe()

为了证明这一点，让我们更正我们的示例：

```java
public static void connectBeforeSubscribe() throws InterruptedException {
    ConnectableObservable obs = getObservable()
        .doOnNext(x -> LOGGER.info("saving " + x)).publish();
    LOGGER.info("connecting:");
    Subscription s = obs.connect();
    Thread.sleep(1000);
    LOGGER.info("subscribing #1");
    obs.subscribe((i) -> LOGGER.info("subscriber#1 is printing x-coordinate " + i));
    Thread.sleep(1000);
    LOGGER.info("subscribing #2");
    obs.subscribe((i) -> LOGGER.info("subscriber#2 is printing x-coordinate " + i));
    Thread.sleep(1000);
    s.unsubscribe();
}
```

步骤比较简单：

-   首先，我们连接
-   然后我们等待一秒钟并订阅第一个Subscriber
-   最后，我们再等一秒钟并订阅第二个Subscriber

请注意，我们添加了doOnNext()运算符。例如，在这里我们可以将元素存储在数据库中，但在我们的代码中，我们只打印“saving ...”。

如果我们启动代码并开始点击，我们将看到元素在connect()调用后立即被发射和处理：

```bash
connecting:
saving 306
saving 248
subscribing #1
saving 377
subscriber#1 is printing x-coordinate 377
saving 295
subscriber#1 is printing x-coordinate 295
saving 206
subscriber#1 is printing x-coordinate 206
subscribing #2
saving 347
subscriber#1 is printing x-coordinate 347
subscriber#2 is printing x-coordinate 347
clearing resources
```

如果没有订阅者，元素仍然会被处理。

**因此connect()方法开始发出和处理元素，而不管是否有人订阅**，就好像有一个带有空操作的人工订阅者消费了元素一样。

如果一些真正的Subscriber订阅了，这个人工中介只是将元素传播给他们。

要取消订阅人工订阅者，我们执行：

```java
s.unsubscribe();
```

并且：

```java
Subscription s = obs.connect();
```

### 3.5 autoConnect()

**此方法意味着connect()不会在订阅之前或之后调用，而是在第一个订阅者订阅时自动调用**。

使用这个方法，我们不能自己调用connect()，因为返回的对象是一个普通的Observable，它没有这个方法，但使用了一个底层的ConnectableObservable：

```java
public static void autoConnectAndSubscribe() throws InterruptedException {
    Observable obs = getObservable()
        .doOnNext(x -> LOGGER.info("saving " + x)).publish().autoConnect();

    LOGGER.info("autoconnect()");
    Thread.sleep(1000);
    LOGGER.info("subscribing #1");
    Subscription s1 = obs.subscribe((i) -> LOGGER.info("subscriber#1 is printing x-coordinate " + i));
    Thread.sleep(1000);
    LOGGER.info("subscribing #2");
    Subscription s2 = obs.subscribe((i) -> LOGGER.info("subscriber#2 is printing x-coordinate " + i));

    Thread.sleep(1000);
    LOGGER.info("unsubscribe 1");
    s1.unsubscribe();
    Thread.sleep(1000);
    LOGGER.info("unsubscribe 2");
    s2.unsubscribe();
}
```

请注意，我们不能同时取消订阅人造的Subscriber。我们可以取消订阅所有真实的订阅者，但人工订阅者仍将处理事件。

为了理解这一点，让我们看看最后一个订阅者取消订阅后最后发生了什么：

```bash
subscribing #1
saving 296
subscriber#1 is printing x-coordinate 296
saving 329
subscriber#1 is printing x-coordinate 329
subscribing #2
saving 226
subscriber#1 is printing x-coordinate 226
subscriber#2 is printing x-coordinate 226
unsubscribe 1
saving 268
subscriber#2 is printing x-coordinate 268
saving 234
subscriber#2 is printing x-coordinate 234
unsubscribe 2
saving 278
saving 268
```

正如我们所看到的，清除资源不会发生，并且在第二次取消订阅后继续使用doOnNext()保存元素。这意味着人工订阅者不会取消订阅而是继续消费元素。

### 3.6 refCount()

**refCount()与autoConnect()相似，因为连接也会在第一个订阅者订阅后立即自动发生**。

与autoconnect()不同，断开连接也会在最后一个订阅者取消订阅时自动发生：

```java
public static void refCountAndSubscribe() throws InterruptedException {
    Observable obs = getObservable()
        .doOnNext(x -> LOGGER.info("saving " + x)).publish().refCount();

    LOGGER.info("refcount()");
    Thread.sleep(1000);
    LOGGER.info("subscribing #1");
    Subscription subscription1 = obs.subscribe(i -> LOGGER.info("subscriber#1 is printing x-coordinate " + i));
    Thread.sleep(1000);
    LOGGER.info("subscribing #2");
    Subscription subscription2 = obs.subscribe(i -> LOGGER.info("subscriber#2 is printing x-coordinate " + i));

    Thread.sleep(1000);
    LOGGER.info("unsubscribe#1");
    subscription1.unsubscribe();
    Thread.sleep(1000);
    LOGGER.info("unsubscribe#2");
    subscription2.unsubscribe();
}
```

```bash
refcount()
subscribing #1
saving 265
subscriber#1 is printing x-coordinate 265
saving 338
subscriber#1 is printing x-coordinate 338
subscribing #2
saving 203
subscriber#1 is printing x-coordinate 203
subscriber#2 is printing x-coordinate 203
unsubscribe#1
saving 294
subscriber#2 is printing x-coordinate 294
unsubscribe#2
clearing resources
```

## 4. 总结

ConnectableObservable类有助于轻松处理多个订阅者。

它的方法看起来很相似，但由于实现的微妙之处，这意味着甚至方法的顺序也很重要，因此极大地改变了订阅者的行为。