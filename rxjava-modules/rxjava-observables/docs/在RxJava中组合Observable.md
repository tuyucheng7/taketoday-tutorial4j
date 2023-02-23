## 1. 简介

在这个快速教程中，我们将讨论在RxJava中组合Observables的不同方法。

如果你是RxJava的新手，一定要先看看这个[介绍教程](https://www.baeldung.com/rxjava-tutorial)。

现在，让我们直接进入。

## 2. Observables

Observable序列，或简称Observables，是异步数据流的表示形式。

这些基于[观察者模式](https://en.wikipedia.org/wiki/Observer_pattern)，其中称为Observer的对象订阅Observable发出的元素。

订阅是非阻塞的，因为Observer可以对Observable将来发出的任何内容做出反应。这反过来又促进了并发性。

这是RxJava中的一个简单演示：

```java
Observable
    .from(new String[] { "John", "Doe" })
    .subscribe(name -> System.out.println("Hello " + name))
```

## 3. 组合Observables

当使用响应式框架进行编程时，组合各种Observables是一个常见的用例。

比如在一个Web应用中，我们可能需要获取两组相互独立的异步数据流。

**我们可以同时调用两者并订阅合并的流，而不是等待前一个流完成后再请求下一个流**。

在本节中，我们将讨论在RxJava中组合多个Observable的一些不同方式，以及每种方法适用的不同用例。

### 3.1 merge

我们可以使用merge运算符来组合多个Observable的输出，使它们像一个一样：

```java
@Test
public void givenTwoObservables_whenMerged_shouldEmitCombinedResults() {
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();

    Observable.merge(
        Observable.from(new String[] {"Hello", "World"}),
        Observable.from(new String[] {"I love", "RxJava"})
    ).subscribe(testSubscriber);

    testSubscriber.assertValues("Hello", "World", "I love", "RxJava");
}
```

### 3.2 mergeDelayError

mergeDelayError方法与merge相同，它将多个Observables合并为一个，但**如果在合并过程中发生错误，它允许无错误的元素在传播错误之前继续**：

```java
@Test
public void givenMutipleObservablesOneThrows_whenMerged_thenCombineBeforePropagatingError() {
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        
    Observable.mergeDelayError(
        Observable.from(new String[] { "hello", "world" }),
        Observable.error(new RuntimeException("Some exception")),
        Observable.from(new String[] { "rxjava" })
    ).subscribe(testSubscriber);

    testSubscriber.assertValues("hello", "world", "rxjava");
    testSubscriber.assertError(RuntimeException.class);
}
```

上面的示例**发出所有无错误值**：

```shell
hello
world
rxjava
```

**请注意，如果我们使用merge而不是mergeDelayError，则不会发出字符串“rxjava”，因为merge会在发生错误时立即停止来自Observables的数据流**。

### 3.3 zip

zip扩展方法**将两个值序列作为对汇集在一起**：

```java
@Test
public void givenTwoObservables_whenZipped_thenReturnCombinedResults() {
    List<String> zippedStrings = new ArrayList<>();

    Observable.zip(
        Observable.from(new String[] { "Simple", "Moderate", "Complex" }), 
        Observable.from(new String[] { "Solutions", "Success", "Hierarchy"}),
        (str1, str2) -> str1 + " " + str2).subscribe(zippedStrings::add);
        
    assertThat(zippedStrings).isNotEmpty();
    assertThat(zippedStrings.size()).isEqualTo(3);
    assertThat(zippedStrings).contains("Simple Solutions", "Moderate Success", "Complex Hierarchy");
}
```

### 3.4 带间隔的压缩

在这个例子中，我们将压缩一个带有[间隔](http://reactivex.io/documentation/operators/interval.html)的流，这实际上会延迟第一个流元素的发射：

```java
@Test
public void givenAStream_whenZippedWithInterval_shouldDelayStreamEmmission() {
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        
    Observable<String> data = Observable.just("one", "two", "three", "four", "five");
    Observable<Long> interval = Observable.interval(1L, TimeUnit.SECONDS);
        
    Observable
        .zip(data, interval, (strData, tick) -> String.format("[%d]=%s", tick, strData))
        .toBlocking().subscribe(testSubscriber);
        
    testSubscriber.assertCompleted();
    testSubscriber.assertValueCount(5);
    testSubscriber.assertValues("[0]=one", "[1]=two", "[2]=three", "[3]=four", "[4]=five");
}
```

## 4. 总结

在本文中，我们看到了一些将Observables与RxJava结合的方法。你可以在官方[RxJava文档](https://github.com/ReactiveX/RxJava/wiki/Combining-Observables)中了解其他方法，例如[combineLatest](http://reactivex.io/documentation/operators/combinelatest.html)、[join](http://reactivex.io/documentation/operators/join.html)、[groupJoin](http://reactivex.io/documentation/operators/join.html)、[switchOnNext](http://reactivex.io/documentation/operators/switch.html)。