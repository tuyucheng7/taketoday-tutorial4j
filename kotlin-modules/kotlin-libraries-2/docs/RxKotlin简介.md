## 1. 概述

在本教程中，我们将使用 RxKotlin 库回顾反应式扩展 (Rx) 在惯用的 Kotlin 中的使用。

RxKotlin 本身并不是 Reactive Extensions 的实现。相反，它主要是一组扩展方法。也就是说，RxKotlin 在RxJava库中增加了一个以 Kotlin 为设计理念的 API。

因此，我们将使用我们文章[RxJava](https://www.baeldung.com/rxjava-2-flowable)简介中的概念，以及我们在专门文章中[介绍的 Flowables概念。](https://www.baeldung.com/rx-java)

## 2. RxKotlin 安装

要在我们的 Maven 项目中使用 RxKotlin，我们需要将[rxkotlin依赖项](https://search.maven.org/classic/#search|gav|1|g%3A"io.reactivex.rxjava2" AND a%3A"rxkotlin")添加到我们的pom.xml 中：

```xml
<dependency>
    <groupId>io.reactivex.rxjava2</groupId>
    <artifactId>rxkotlin</artifactId>
    <version>2.3.0</version>
</dependency>
```

或者，对于 Gradle 项目，我们的build.gradle：

```groovy
implementation 'io.reactivex.rxjava2:rxkotlin:2.3.0'
```

在这里，我们使用 RxKotlin 2.x，它以 RxJava 2 为目标。使用 RxJava 1 的项目应该使用 RxKotlin 1.x。相同的概念适用于两个版本。

请注意，RxKotlin 依赖于 RxJava，但它们不会经常将依赖项更新到最新版本。因此，我们建议明确包含我们将依赖的特定 RxJava 版本，如[我们的 RxJava 文章](https://www.baeldung.com/rx-java#setup)中所述。

## 3.在 RxKotlin 中创建Observable

RxKotlin 包含许多扩展方法来从集合中创建Observable和Flowable对象。

特别是，每种类型的数组都有一个toObservable()方法和一个toFlowable()方法：

```plaintext
val observable = listOf(1, 1, 2, 3).toObservable()
observable.test().assertValues(1, 1, 2, 3)
val flowable = listOf(1, 1, 2, 3).toFlowable()
flowable.buffer(2).test().assertValues(listOf(1, 1), listOf(2, 3))
```

### 3.1. 可完成的

RxKotlin 还提供了一些创建[Completable](https://www.baeldung.com/rxjava-completable)实例的方法。特别是，我们可以使用扩展方法toCompletable将Action s、Callable s、Future s 和零元函数转换为Completable ：

```plaintext
var value = 0
val completable = { value = 3 }.toCompletable()
assertFalse(completable.test().isCancelled())
assertEquals(3, value)
```

## 4. Observable和Flowable到Map和Multimap

当我们有一个生成Pair实例的Observable或Flowable时，我们可以将它们转换为一个生成Map的Single observable：

```plaintext
val list = listOf(Pair("a", 1), Pair("b", 2), Pair("c", 3), Pair("a", 4))
val observable = list.toObservable()
val map = observable.toMap()
assertEquals(mapOf(Pair("a", 4), Pair("b", 2), Pair("c", 3)), map.blockingGet())
```

正如我们在前面的示例中看到的那样，如果它们具有相同的键，toMap 会用较晚的值覆盖较早发出的值。

如果我们想将与键关联的所有值累积到一个集合中，我们使用toMultimap代替：

```plaintext
val list = listOf(Pair("a", 1), Pair("b", 2), Pair("c", 3), Pair("a", 4))
val observable = list.toObservable()
val map = observable.toMultimap()
assertEquals(
  mapOf(Pair("a", listOf(1, 4)), Pair("b", listOf(2)), Pair("c", listOf(3))),
  map.blockingGet())
```

## 5.结合Observable和Flowable

Rx 的卖点之一是以各种方式组合Observable和Flowable的可能性。事实上，RxJava 提供了许多开箱即用的运算符。

除此之外，RxKotlin 还包含一些用于组合Observable等的扩展方法。

### 5.1. 结合可观察到的排放

当我们有一个Observable发射其他Observable时，我们可以使用 RxKotlin 中的扩展方法之一将发射的值组合在一起。

特别是，mergeAll结合了 observables 和flatMap：

```plaintext
val subject = PublishSubject.create<Observable<String>>()
val observable = subject.mergeAll()
```

这将与以下内容相同：

```java
val observable = subject.flatMap { it }
```

结果Observable将以未指定的顺序发出源Observable的所有值。

类似地，concatAll使用concatMap(值以与源相同的顺序发出)，而switchLatest使用switchMap(值从最后发出的Observable发出)。

到目前为止，我们已经看到，所有上述方法都为Flowable源提供，具有相同的语义。

### 5.2. 组合Completable s 、 Maybe s 和Single s

当我们有一个发出Completable、Maybe或Single实例的Observable时，我们可以将它们与适当的mergeAllXs方法结合起来，例如mergeAllMaybes：

```plaintext
val subject = PublishSubject.create<Maybe<Int>>()
val observable = subject.mergeAllMaybes()
subject.onNext(Maybe.just(1))
subject.onNext(Maybe.just(2))
subject.onNext(Maybe.empty())
subject.onNext(Maybe.error(Exception("error")))
subject.onNext(Maybe.just(3))
observable.test().assertValues(1, 2).assertError(Exception::class.java)
```

### 5.3. 组合Observable的Iterable _

相反，对于Observable或Flowable实例的集合，RxKotlin 有几个其他操作符，merge和mergeDelayError。它们都具有将所有Observable或Flowable组合成一个将按顺序发出所有值的效果：

```plaintext
val observables = mutableListOf(Observable.just("first", "second"))
val observable = observables.merge()
observables.add(Observable.just("third", "fourth"))
observable.test().assertValues("first", "second", "third", "fourth")
```

这两个运算符(直接派生自 RxJava 中的同名运算符)之间的区别在于它们对错误的处理。

merge方法在源发出错误后立即发出错误：

```plaintext
// ...
observables.add(Observable.error(Exception("e")))
observables.add(Observable.just("fifth"))
// ...
observable.test().assertValues("first", "second", "third", "fourth")
```

而mergeDelayError在流的末尾发出它们：

```plaintext
// ...
observables.add(Observable.error(Exception("e")))
observables.add(Observable.just("fifth"))
// ...
observable.test().assertValues("first", "second", "third", "fourth", "fifth")
```

## 6. 处理不同类型的值

现在让我们看看 RxKotlin 中用于处理不同类型值的扩展方法。

这些是 RxJava 方法的变体，它们使用了 Kotlin 的具体化泛型。特别是，我们可以：

-   将发出的值从一种类型转换为另一种类型，或者
-   过滤掉不是特定类型的值

因此，例如，我们可以将Number的Observable转换为 Int之一：

```plaintext
val observable = Observable.just<Number>(1, 1, 2, 3)
observable.cast<Int>().test().assertValues(1, 1, 2, 3)
```

在这里，演员是不必要的。但是，当将不同的 observables 组合在一起时，我们可能需要它。

相反，使用ofType，我们可以过滤掉不是我们期望的类型的值：

```plaintext
val observable = Observable.just(1, "and", 2, "and")
observable.ofType<Int>().test().assertValues(1, 2)
```

与往常一样，cast和ofType适用于Observable和Flowable。

此外，Maybe 也支持这些方法。相反，Single类仅支持cast。

## 7. 其他辅助方法

最后，RxKotlin 包含几个辅助方法。让我们快速浏览一下。

我们可以使用subscribeBy而不是 subscribe——它允许命名参数：

```plaintext
Observable.just(1).subscribeBy(onNext = { println(it) })
```

同样，对于阻塞订阅，我们可以使用blockingSubscribeBy。

此外，RxKotlin 包括一些模仿 RxJava 中的方法但解决了 Kotlin 类型推断的限制。

例如，当使用 Observable#zip 时， 指定拉链 看起来不太好：

```plaintext
Observable.zip(Observable.just(1), Observable.just(2), BiFunction<Int, Int, Int> { a, b -> a + b })
```

因此，RxKotlin 添加了 Observables#zip以实现更惯用的用法：

```java
Observables.zip(Observable.just(1), Observable.just(2)) { a, b -> a + b }
```

注意Observables中最后的“s” 。同样，我们有Flowables、Singles和Maybes。

## 八、总结

在本文中，我们彻底回顾了 RxKotlin 库，它增强了 RxJava，使其 API 看起来更像惯用的 Kotlin。

有关更多信息，请参阅[RxKotlin GitHub 页面](https://github.com/ReactiveX/RxKotlin)。如需更多示例，我们推荐[RxKotlin tests](https://github.com/ReactiveX/RxKotlin/tree/2.x/src/test)。