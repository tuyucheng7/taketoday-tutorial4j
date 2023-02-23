## 1. 概述

在本文中，我们将重点关注在Java中使用Reactive Extensions (Rx)来组合和使用数据序列。

乍一看，API可能看起来类似于Java 8 Streams，但实际上，它更加灵活和流畅，使其成为一种强大的编程范式。

如果你想了解更多关于RxJava的信息，请查看[这篇](https://www.baeldung.com/rxjava-backpressure)文章。

## 2. 设置

要在我们的[Maven](https://search.maven.org/search?q=a:rxjava)项目中使用RxJava，我们需要将以下依赖项添加到我们的pom.xml：

```xml
<dependency>
    <groupId>io.reactivex</groupId>
    <artifactId>rxjava</artifactId>
    <version>${rx.java.version}</version>
</dependency>
```

或者，对于Gradle项目：

```groovy
compile 'io.reactivex.rxjava:rxjava:x.y.z'
```

## 3. 函数响应式概念

一方面，**函数式编程是通过组合纯函数构建软件的过程，避免共享状态、可变数据和副作用**。

另一方面，**响应式编程是一种异步编程范式，与数据流和变化的传播有关**。

函数式响应式编程共同构成了函数式和响应式技术的组合，可以代表一种优雅的事件驱动编程方法-其值会随时间变化，并且消费者会在数据传入时对其做出反应。

这项技术汇集了其核心原则的不同实现方式，一些作者提出了一个文档，该文档定义了用于描述新型应用程序的通用词汇表。

### 3.1 响应式宣言

[Reactive Manifesto](http://www.reactivemanifesto.org/)是一个在线文档，为软件开发行业中的应用程序制定了高标准。简单地说，响应式系统是：

-   响应性：系统应及时响应
-   消息驱动：系统应该在组件之间使用异步消息传递来确保松散耦合
-   弹性：系统应在高负载下保持响应
-   扩展：当某些组件发生故障时，系统应保持响应

## 4. 可观察的(Observable)

使用Rx时需要了解两种关键类型：

-   Observable表示可以从数据源获取数据并且其状态可能以其他对象可能注册感兴趣的方式感兴趣的任何对象
-   Observer(观察者)是任何希望在另一个对象的状态发生变化时得到通知的对象

观察者订阅一个Observable序列，**该序列一次向观察者发送一个元素**。

观察者在处理下一个之前处理每个。如果许多事件异步进入，则必须将它们存储在队列中或丢弃。

在Rx中，观察者永远不会在元素乱序时被调用，也不会在前一个元素的回调返回之前被调用。

### 4.1 可观察的类型

有两种类型：

-   非阻塞：支持异步执行，并允许在事件流中的任何时候取消订阅。在本文中，我们将主要关注这种类型

-   阻塞：所有onNext观察者调用都是同步的，并且不可能在事件流的中间取消订阅。我们总是可以使用toBlocking方法将Observable转换为BlockingObservable：

```java
BlockingObservable<String> blockingObservable = observable.toBlocking();
```

### 4.2 运算符

**运算符是一个函数，它将一个Observable(源)作为其第一个参数并返回另一个Observable(目标)**。然后对于源Observable发出的每个元素，它将对该元素应用一个函数，然后在目标Observable上发出结果。

可以将运算符链接在一起以创建复杂的数据流，这些数据流可以根据特定条件过滤事件。多个运算符可以应用于同一个Observable。

不难遇到Observable发出元素的速度快于运算符或观察者消耗元素的速度。你可以在此处阅读有关[背压](https://www.baeldung.com/rxjava-backpressure)的更多信息。

### 4.3 创建Observable

基本运算符只生成一个Observable，它在完成之前发出一个通用实例，即字符串“Hello”。当我们想从Observable中获取信息时，我们实现一个观察者(Observer)接口，然后在所需的Observable上调用subscribe：

```java
Observable<String> observable = Observable.just("Hello");
observable.subscribe(s -> result = s);
 
assertTrue(result.equals("Hello"));
```

### 4.4 OnNext、OnError和OnCompleted 

我们想了解观察者接口上的三个方法：

1.  每次将新事件发布到附加的Observable时，我们的观察者都会调用onNext。这是我们将对每个事件执行一些操作的方法
2.  当与Observable关联的事件序列完成时，将调用onCompleted，这表明我们不应该期望对我们的观察者进行任何更多的onNext调用
3.  当在RxJava框架代码或我们的事件处理代码中抛出未处理的异常时调用onError

Observables subscribe方法的返回值是一个订阅接口：

```java
String[] letters = {"a", "b", "c", "d", "e", "f", "g"};
Observable<String> observable = Observable.from(letters);
observable.subscribe(
    i -> result += i,  // onNext
    Throwable::printStackTrace, // onError
    () -> result += "_Completed" // onCompleted
);
assertTrue(result.equals("abcdefg_Completed"));
```

## 5. Observable转换和条件运算符

### 5.1 map

map运算符通过对每个元素应用一个函数来转换Observable发出的元素。

假设有一个已声明的字符串数组，其中包含字母表中的一些字母，我们希望以大写模式打印它们：

```java
Observable.from(letters)
    .map(String::toUpperCase)
    .subscribe(letter -> result += letter);
assertTrue(result.equals("ABCDEFG"));
```

每当我们以嵌套的Observable结束时，flatMap可用于扁平化Observable。

可以在[此处](https://www.baeldung.com/java-difference-map-and-flatmap)找到有关map和flatMap之间区别的更多详细信息。

假设我们有一个从字符串列表返回Observable<String\>的方法。现在我们将从一个新的Observable中打印出基于订阅者看到的标题列表的每个字符串：

```java
Observable<String> getTitle() {
    return Observable.from(titleList);
}
Observable.just("book1", "book2")
    .flatMap(s -> getTitle())
    .subscribe(l -> result += l);

assertTrue(result.equals("titletitle"));
```

### 5.2 scan

scan运算符将一个函数应用于Observable发出的每个运算，并发出每个连续的值。

它允许我们将状态从一个事件传递到另一个事件：

```java
String[] letters = {"a", "b", "c"};
Observable.from(letters)
    .scan(new StringBuilder(), StringBuilder::append)
    .subscribe(total -> result += total.toString());
assertTrue(result.equals("aababc"));
```

### 5.3 groupBy

Group by运算符允许我们将输入Observable中的事件分类为输出类别。

假设我们创建了一个从0到10的整数数组，然后应用groupBy将它们分为偶数和奇数类别：

```java
Observable.from(numbers)
    .groupBy(i -> 0 == (i % 2) ? "EVEN" : "ODD")
    .subscribe(group ->
        group.subscribe((number) -> {
            if (group.getKey().toString().equals("EVEN")) {
                EVEN[0] += number;
            } else {
                ODD[0] += number;
            }
        })
    );
assertTrue(EVEN[0].equals("0246810"));
assertTrue(ODD[0].equals("13579"));
```

### 5.4 filter

filter运算符仅发出可观察对象中通过谓词测试的那些元素。

因此，让我们在整数数组中过滤奇数：

```java
Observable.from(numbers)
    .filter(i -> (i % 2 == 1))
    .subscribe(i -> result += i);
 
assertTrue(result.equals("13579"));
```

### 5.5 条件运算符

defaultIfEmpty从源Observable发出元素，或者如果源Observable为空则发出默认元素：

```java
Observable.empty()
    .defaultIfEmpty("Observable is empty")
    .subscribe(s -> result += s);
 
assertTrue(result.equals("Observable is empty"));
```

以下代码发出字母表“a”的第一个字母，因为数组letters不为空，这是它在第一个位置包含的内容：

```java
Observable.from(letters)
    .defaultIfEmpty("Observable is empty")
    .first()
    .subscribe(s -> result += s);
 
assertTrue(result.equals("a"));
```

takeWhile运算符在指定条件变为false后丢弃Observable发出的元素：

```java
Observable.from(numbers)
    .takeWhile(i -> i < 5)
    .subscribe(s -> sum[0] += s);
 
assertTrue(sum[0] == 10);
```

当然，还有更多其他运算符可以满足我们的需求，如contain、skipWhile、skipUntil、takeUntil等。

## 6. 可连接的Observables

**ConnectableObservable类似于普通的Observable，不同之处在于它在订阅时不会开始发射元素，而是仅在对它应用connect运算符时才开始发射元素**。

通过这种方式，我们可以在Observable开始发射数据之前等待所有预期的观察者订阅Observable：

```java
String[] result = {""};
ConnectableObservable<Long> connectable = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
connectable.subscribe(i -> result[0] += i);
assertFalse(result[0].equals("01"));

connectable.connect();
Thread.sleep(500);
 
assertTrue(result[0].equals("01"));
```

## 7. Single

Single就像一个Observable，它不是发出一系列值，而是发出一个值或错误通知。

使用这个数据源，我们只能通过两种方式进行订阅：

-   OnSuccess返回一个Single，它也调用我们指定的方法
-   OnError也返回一个Single，它会立即通知订阅者错误

```java
String[] result = {""};
Single<String> single = Observable.just("Hello")
    .toSingle()
    .doOnSuccess(i -> result[0] += i)
    .doOnError(error -> {
        throw new RuntimeException(error.getMessage());
    });
single.subscribe();
 
assertTrue(result[0].equals("Hello"));
```

## 8. Subject

一个Subject同时是两个元素，一个subscriber和一个observable。作为订阅者，Subject可用于发布来自多个可观察对象的事件。

而且由于它也是可观察的，因此来自多个订阅者的事件可以作为它的事件重新发送给任何观察它的人。

在下一个示例中，我们将看看观察者如何能够看到他们订阅后发生的事件：

```java
Integer subscriber1 = 0;
Integer subscriber2 = 0;
Observer<Integer> getFirstObserver() {
    return new Observer<Integer>() {
        @Override
        public void onNext(Integer value) {
           subscriber1 += value;
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("error");
        }

        @Override
        public void onCompleted() {
            System.out.println("Subscriber1 completed");
        }
    };
}

Observer<Integer> getSecondObserver() {
    return new Observer<Integer>() {
        @Override
        public void onNext(Integer value) {
            subscriber2 += value;
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("error");
        }

        @Override
        public void onCompleted() {
            System.out.println("Subscriber2 completed");
        }
    };
}

PublishSubject<Integer> subject = PublishSubject.create(); 
subject.subscribe(getFirstObserver()); 
subject.onNext(1); 
subject.onNext(2); 
subject.onNext(3); 
subject.subscribe(getSecondObserver()); 
subject.onNext(4); 
subject.onCompleted();
 
assertTrue(subscriber1 + subscriber2 == 14)
```

## 9. 资源管理

using运算符允许我们将资源(例如JDBC数据库连接、网络连接或打开的文件)关联到我们的可观察对象。

在这里，我们在评论中介绍了实现这一目标所需采取的步骤以及实施示例：

```java
String[] result = {""};
Observable<Character> values = Observable.using(
    () -> "MyResource",
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
    v -> result[0] += v,
    e -> result[0] += e
);
assertTrue(result[0].equals("MyResource"));
```

## 10. 总结

在本文中，我们讨论了如何使用RxJava库以及如何探索其最重要的功能。