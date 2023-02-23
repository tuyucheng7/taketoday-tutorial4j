## 1. 简介

在本教程中，我们将研究**RxJava中的Maybe<T\>类型-它表示可以发出单个值、在空状态下完成或报告错误的流**。

## 2. Maybe类型

Maybe是一种特殊的[Observable](https://www.baeldung.com/rx-java)，它只能发出零个或一个元素，如果计算在某个时刻失败，则会报告错误。

在这方面，它就像是Single和Completable的结合。所有这些简化的类型，包括Maybe，都提供了Flowable运算符的一个子集。这意味着**只要操作对0或1个元素有意义，我们就可以像Flowable一样使用Maybe**。

由于它只能发出一个值，因此它不像Flowable那样支持背压处理：

```java
Maybe.just(1)
    .map(x -> x + 7)
    .filter(x -> x > 0)
    .test()
    .assertResult(8);
```

可以从Maybe源订阅onSuccess、onError和onComplete信号：

```java
Maybe.just(1)
    .subscribe(
        x -> System.out.print("Emitted item: " + x),
        ex -> System.out.println("Error: " + ex.getMessage()),
        () -> System.out.println("Completed. No items.")
    );
```

上面的代码将打印“Emitted item: 1”，因为此源将发出成功值。

对于相同的订阅：

-   Maybe.empty().subscribe(...)将打印“Completed. No items.”
-   Maybe.error(new Exception("error")).subscribe(...)将打印“Error: error”

**这些事件对于Maybe是互斥的**。也就是说，onComplete不会在onSuccess之后被调用。这与Flowable略有不同，因为onComplete将在流完成时被调用，即使在可能的一些onNext调用之后。

Single没有像Maybe那样的onComplete信号，因为它旨在捕获一种可以发出一个元素或失败的反应模式。

另一方面，Completable缺少onSuccess，因为它只用于处理完成/失败的情况。

Maybe类型的另一个用例是将它与Flowable结合使用。firstElement()方法可用于从Flowable创建Maybe：

```java
Flowable<String> visitors = ...
visitors
    .skip(1000)
    .firstElement()
    .subscribe(
        v -> System.out.println("1000th visitor: " + v + " won the prize"), 
        ex -> System.out.print("Error: " + ex.getMessage()), 
        () -> System.out.print("We need more marketing"));
```

## 3. 总结

在这个简短的教程中，我们快速了解了RxJava Maybe<T\>的用法，以及它与其他响应类型(如Flowable、Single和Completable)的关系。