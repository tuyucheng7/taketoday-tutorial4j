## 1. 概述

在本文中，我们将介绍在[Reactor](https://projectreactor.io/)中组合发布者的各种方法。

## 2. Maven依赖

```xml
<dependencies>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-core</artifactId>
        <version>3.4.12</version>
    </dependency>
    <dependency>
        <groupId>io.projectreactor</groupId>
        <artifactId>reactor-test</artifactId>
        <version>3.4.12</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## 3. 合并Publishers

给定一个必须使用Flux<T>或Mono<T>的场景，有许多不同的方法来组合流。

让我们创建几个示例来说明Flux<T>类中一些静态方法的用法，例如concat、concatWith、merge、zip和combineLatest。

我们的案例将使用两个Flux<Integer>类型的发布者，即evenNumbers，它是Integer类型的Flux，
包含一个偶数序列，从1(MIN变量)开始，最大为5(MAX变量)。

我们还将创建oddNumbers，包含奇数序列的Flux：

```java
class CombiningPublishersIntegrationTest {
    private static final Integer MIN = 1;
    private static final Integer MAX = 5;

    private static final Flux<Integer> evenNumbers = Flux.range(min, max).filter(x -> x % 2 == 0);
    private static final Flux<Integer> oddNumbers = Flux.range(min, max).filter(x -> x % 2 > 0);
}
```

### 3.1 concat()

concat()方法执行输入的串联，转发由下游源发出的元素。

concat()是通过顺序订阅第一个源，然后等待它完成，再订阅下一个源，依此类推，直到最后一个源完成。任何错误都会立即中断序列并被转发到下游。

下面是一个简单的例子：

```java
class CombiningPublishersIntegrationTest {

    @Test
    void givenFluxes_whenConcatIsInvoked_thenConcat() {
        Flux<Integer> fluxOfIntegers = Flux.concat(evenNumbers, oddNumbers);
        StepVerifier.create(fluxOfIntegers)
                .expectNext(2)
                .expectNext(4)
                .expectNext(1)
                .expectNext(3)
                .expectNext(5)
                .expectComplete()
                .verify();
    }
}
```

### 3.2 concatWith()

使用静态方法concatWith()，我们将生成两个类型为Flux<T>的源的串联结果：

```java
class CombiningPublishersIntegrationTest {

    @Test
    void givenFluxes_whenConcatWithIsInvoked_thenConcatWith() {
        Flux<Integer> fluxOfIntegers = evenNumbers.concatWith(oddNumbers);
        // same stepVerifier as in the concat example above ...
    }
}
```

### 3.3 combineLatest()

Flux的静态方法combineLatest()将生成由来自每个发布者源的最新发布值的组合所提供的数据。

下面是使用此方法的示例，其中两个发布者源Publisher和一个BiFunction作为参数：

```java
class CombiningPublishersIntegrationTest {

    @Test
    void givenFluxes_whenCombineLatestIsInvoked_thenCombineLatest() {
        BiFunction<Integer, Integer, Integer> adder = Integer::sum;
        Flux<Integer> fluxOfIntegers = Flux.combineLatest(evenNumbers, oddNumbers, adder);

        StepVerifier.create(fluxOfIntegers)
                .expectNext(5) // 4 + 1
                .expectNext(7) // 4 + 3
                .expectNext(9) // 4 + 5
                .expectComplete()
                .verify();
    }
}
```

我们可以看到，combineLatest()方法使用evenNumbers(4)的最新元素和oddNumbers(1, 3, 5)的元素，
应用了函数“Integer::sum”(方法引用)，从而生成了序列5, 7, 9。

### 3.4 merge()

merge()方法执行将数组中包含的发布者序列中的数据合并到交错的合并序列中：

```java
class CombiningPublishersIntegrationTest {

    @Test
    void givenFluxes_whenMergeIsInvoked_thenMerge() {
        Flux<Integer> fluxOfIntegers = Flux.merge(evenNumbers, oddNumbers);
        StepVerifier.create(fluxOfIntegers)
                .expectNext(2)
                .expectNext(4)
                .expectNext(1)
                .expectNext(3)
                .expectNext(5)
                .expectComplete()
                .verify();
    }
}
```

需要注意的一件事情是，与concat(惰性订阅)相反，资源被急切地订阅。

在这里，如果我们在发布者元素之间插入延迟，我们可以看到merge()方法的不同结果：

```java
class CombiningPublishersIntegrationTest {

    @Test
    void givenFluxes_whenMergeWithDelayedElementsIsInvoked_thenMergeWithDelayedElements() {
        Flux<Integer> fluxOfIntegers = Flux.merge(
                evenNumbers.delayElements(Duration.ofMillis(500L)),
                oddNumbers.delayElements(Duration.ofMillis(300L)));
        StepVerifier.create(fluxOfIntegers)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(5)
                .expectNext(4)
                .expectComplete()
                .verify();
    }
}
```

### 3.5 mergeSequential()

mergeSequential()方法将数组中提供的发布者序列中的数据合并为有序的合并序列。

与concat()不同，源被热切地订阅。

此外，与merge()不同，它们发出的值按订阅顺序合并到最终序列中：

```java
class CombiningPublishersIntegrationTest {

    @Test
    void givenFluxes_whenMergeSequentialIsInvoked_thenMergeSequential() {
        Flux<Integer> fluxOfIntegers = Flux.mergeSequential(evenNumbers, oddNumbers);
        StepVerifier.create(fluxOfIntegers)
                .expectNext(2)
                .expectNext(4)
                .expectNext(1)
                .expectNext(3)
                .expectNext(5)
                .expectComplete()
                .verify();
    }
}
```

### 3.6 mergeDelayError()

mergeDelayError()将数组中包含的发布者序列中的数据合并到交错的合并序列中。

与concat()不同，源被热切地订阅。

静态merge()方法的这种变体将延迟任何错误，直到处理完剩余的合并积压。

以下是mergeDelayError()的一个示例：

```java
class CombiningPublishersIntegrationTest {

    @Test
    void givenFluxes_whenMergeDelayErrorIsInvoked_thenMergeDelayError() {
        Flux<Integer> fluxOfIntegers = Flux.mergeDelayError(1,
                evenNumbers.delayElements(Duration.ofMillis(500L)),
                oddNumbers.delayElements(Duration.ofMillis(300L)));

        StepVerifier.create(fluxOfIntegers)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(5)
                .expectNext(4)
                .expectComplete()
                .verify();
    }
}
```

### 3.7 mergeWith()

静态方法mergeWith()将来自该Flux(this)和一个发布者的数据合并到一个交错的合并序列中。

同样，与concat()不同的是，内部资源被热切地订阅：

```java
class CombiningPublishersIntegrationTest {

    @Test
    void givenFluxes_whenMergeWithIsInvoked_thenMergeWith() {
        Flux<Integer> fluxOfIntegers = evenNumbers.mergeWith(oddNumbers);

        StepVerifier.create(fluxOfIntegers)
                .expectNext(2)
                .expectNext(4)
                .expectNext(1)
                .expectNext(3)
                .expectNext(5)
                .expectComplete()
                .verify();
    }
}
```

### 3.8 zip()

静态方法zip()将多个源聚合在一起，即等待所有源发出一个元素，并将这些元素组合成一个输出值(由提供的combinator函数构造)。

此操作一直继续下去，直到任何源消费完成：

```java
class CombiningPublishersIntegrationTest {

    @Test
    void givenFluxes_whenZipIsInvoked_thenZip() {
        Flux<Integer> fluxOfIntegers = Flux.zip(evenNumbers, oddNumbers, Integer::sum);

        StepVerifier.create(fluxOfIntegers)
                .expectNext(3) // 2 + 1
                .expectNext(7) // 4 + 3
                .expectComplete() // 此时evenNumbers消费完毕
                .verify();
    }
}
```

由于evenNumbers中没有剩余的元素可以配对，因此忽略oddNumbers发布者中的元素5。

### 3.9 zipWith()

zipWith()方法执行与zip()相同的操作，但仅使用两个发布者：

```java
class CombiningPublishersIntegrationTest {

    @Test
    void givenFluxes_whenZipWithIsInvoked_thenZipWith() {
        Flux<Integer> fluxOfIntegers = evenNumbers.zipWith(oddNumbers, (a, b) -> a  b);
        StepVerifier.create(fluxOfIntegers)
                .expectNext(2) // 2  1
                .expectNext(12) // 4  3
                .expectComplete()
                .verify();
    }
}
```

## 4. 总结

在本教程中，我们介绍了多种组合发布者的方法。