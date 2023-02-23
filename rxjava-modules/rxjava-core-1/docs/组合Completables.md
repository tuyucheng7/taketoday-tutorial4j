## 1. 概述

在本教程中，我们将使用[RxJava](https://www.baeldung.com/rx-java)的Completable类型，它表示没有实际值的计算结果。

## 2. RxJava依赖

让我们将RxJava2依赖项包含到我们的Maven项目中：

```xml
<dependency>
    <groupId>io.reactivex.rxjava2</groupId>
    <artifactId>rxjava</artifactId>
    <version>2.2.2</version>
</dependency>
```

我们通常可以在[Maven Central](https://search.maven.org/search?q=rxjava)上找到最新版本。

## 3. Completable类型

**[Completable](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Completable.html)类似于[Observable](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html)，唯一的例外是前者发出完成或错误信号，但不会发出任何元素**。Completable类包含几个方便的方法，用于从不同的响应源创建或获取它。

**我们可以使用[Completable.complete()](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Completable.html#complete--)生成一个立即完成的实例**。 

然后，我们可以使用[DisposableCompletableObserver](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/observers/DisposableCompletableObserver.html)观察它的状态：

```java
Completable
    .complete()
    .subscribe(new DisposableCompletableObserver() {
        @Override
        public void onComplete() {
            System.out.println("Completed!");
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }
});
```

**此外，我们可以从Callable、[Action](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/functions/Action.html)和Runnable构造一个Completable实例**：

```java
Completable.fromRunnable(() -> {});
```

此外，我们可以使用Completable.from()或在[Maybe](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Maybe.html#ignoreElement--)、[Single](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Single.html#ignoreElement--)、[Flowable](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Flowable.html#ignoreElements--)和[Observable](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#ignoreElements--)源本身上调用ignoreElement()从其他类型获取Completable实例：

```java
Flowable<String> flowable = Flowable
    .just("request received", "user logged in");
Completable flowableCompletable = Completable
    .fromPublisher(flowable);
Completable singleCompletable = Single.just(1)
    .ignoreElement();
```

## 4. 链接Completables

当我们只关心操作是否成功时，我们可以在许多实际用例中使用Completable链：

-   全有或全无操作，例如执行PUT请求以更新远程对象，然后在成功时更新本地数据库
-   事后记录和日志
-   多个操作的编排，例如在摄取操作完成后运行分析作业

我们将保持示例简单且与问题无关。考虑我们有几个Completable实例：

```java
Completable first = Completable
    .fromSingle(Single.just(1));
Completable second = Completable
    .fromRunnable(() -> {});
Throwable throwable = new RuntimeException();
Completable error = Single.error(throwable)
    .ignoreElement();
```

**要将两个Completable组合成一个，我们可以使用[andThen()](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Completable.html#andThen-io.reactivex.CompletableSource-)运算符**：

```java
first
    .andThen(second)
    .test()
    .assertComplete();
```

我们可以根据需要链接任意数量的Completables。同时，**如果至少有一个源未能完成，则生成的Completable也不会触发onComplete()**：

```java
first
    .andThen(second)
    .andThen(error)
    .test()
    .assertError(throwable);
```

此外，**如果其中一个源是无限的或由于某种原因没有到达onComplete，则生成的Completable也将永远不会触发onComplete()或onError()**。

好在我们仍然可以测试这个场景：

```java
...
    .andThen(Completable.never())
    .test()
    .assertNotComplete();
```

## 5. Completables系列

想象一下我们有一堆Completable。作为实际用例，假设我们需要在几个独立的子系统中注册一个用户。

**要将所有Completable合并为一个，我们可以使用[merge()](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Completable.html#merge-java.lang.Iterable-)系列方法**，merge()运算符允许订阅所有源。

**一旦所有源都完成，生成的实例就会完成**。此外，当任何来源发出错误时，它会以onError终止：

```java
Completable.mergeArray(first, second)
    .test()
    .assertComplete();

Completable.mergeArray(first, second, error)
    .test()
    .assertError(throwable);
```

让我们继续讨论一个稍微不同的用例。假设我们需要对从Flowable获得的每个元素执行一个操作。

然后，我们需要一个Completable来完成上游和所有元素级操作。[flatMapCompletable()](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Flowable.html#flatMapCompletable-io.reactivex.functions.Function-)运算符可以在这种情况下提供帮助：

```java
Completable allElementsCompletable = Flowable
    .just("request received", "user logged in")
    .flatMapCompletable(message -> Completable
        .fromRunnable(() -> System.out.println(message))
    );
allElementsCompletable
    .test()
    .assertComplete();
```

类似地，上述方法可用于其余的基础响应类，如Observable、Maybe或Single。

作为flatMapCompletable()的实际上下文，我们可以考虑用一些副作用来装饰每个元素。我们可以为每个已完成的元素编写一个日志条目，或者在每个成功的操作后创建一个存储快照。

最后，**我们可能需要从其他几个来源构建一个Completable，并在其中任何一个完成后立即终止它**。这就是amb运算符可以提供帮助的地方。

amb前缀是“ambiguous”的简写，表示不确定究竟是哪个Completable完成了。例如，[ambArray()](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Completable.html#ambArray-io.reactivex.CompletableSource...-)：

```java
Completable.ambArray(first, Completable.never(), second)
    .test()
    .assertComplete();
```

请注意，上述Completable也可能以onError()而不是onComplete()终止，具体取决于哪个源Completable首先终止：

```java
Completable.ambArray(error, first, second)
    .test()
    .assertError(throwable);
```

此外，一旦第一个源终止，剩余的源将保证被处理掉。

这意味着所有剩余的正在运行的Completable都通过[Disposable.dispose()](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/disposables/Disposable.html#dispose--)停止，并且相应的[CompletableObserver](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/CompletableObserver.html)将被取消订阅。

关于一个实际示例，当我们将备份文件流式传输到多个等效的远程存储时，我们可以使用amb()。一旦最佳备份完成，我们就会完成该过程，或者在出现错误时重复该过程。

## 6. 总结

在本文中，我们简要回顾了RxJava的Completable类型。

我们从获取Completable实例的不同选项开始，然后使用andThen()、merge()、flatMapCompletable()和amb...()运算符链接和组合Completable。