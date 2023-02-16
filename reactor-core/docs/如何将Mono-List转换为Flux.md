## 1. 概述

有时在[响应式编程](https://www.baeldung.com/reactor-core)中，我们可能有一个发布大量元素集合的发布者。在某些情况下，此发布者的消费者可能无法一次性处理所有元素。因此，我们可能需要异步发布每个元素以匹配消费者的处理速度。

在本教程中，我们将研究一些方法，通过这些方法可以将集合的Mono转换为集合元素的Flux。

## 2. 问题描述

在使用响应式流时，我们使用[Publisher](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Publisher.html?is-external=true)及其两个实现[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)和[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)。Mono<T\>是Publisher<T\>的一种类型，可以发出0或1个T类型的元素，而Flux<T\>可以发出0到N个T类型的元素。

假设我们有一个Mono发布者，它持有一个Mono<List<T\>>—一个可迭代的T类型元素集合。我们的要求是使用Flux<T\>异步生成集合元素：

<img src="../assets/img1.png">

在这里，我们可以看到我们需要Mono<List<T\>>上的运算符来执行此转换。首先，我们将从流发布者Mono中提取集合元素，然后将元素一个一个地异步生成为Flux。

**Mono发布者包含一个可以同步转换Mono的[map](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#map-java.util.function.Function-)运算符，以及一个用于异步转换Mono的[flatMap](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#flatMap-java.util.function.Function-)运算符**。此外，这两个运算符都生成一个元素作为输出。

**然而，对于我们在展平Mono<List<T\>>之后生成许多元素的用例，我们可以使用[flatMapMany](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#flatMapMany-java.util.function.Function-)或[FlatMapIterable](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html#flatMapIterable-java.util.function.Function-)**。

让我们探讨如何使用这些运算符。

## 3. flatMapMany

让我们从一个示例字符串列表开始，以创建我们的Mono发布者：

```java
private Mono<List<String>> monoOfList() {
    List<String> list = new ArrayList<>();
    list.add("one");
    list.add("two");
    list.add("three");
    list.add("four");
    return Mono.just(list);
}
```

flatMapMany是Mono上的一个通用运算符，它返回一个Publisher。让我们将flatMapMany应用到我们的解决方案中：

```java
private <T> Flux<T> monoToFluxUsingFlatMapMany(Mono<List<T>> monoList) {
    return monoList
        .flatMapMany(Flux::fromIterable)
        .log();
}
```

在这种情况下，flatMapMany获取Mono的List集合，将其展平，并使用Flux的运算符[fromIterable](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html#fromIterable-java.lang.Iterable-)创建一个Flux发布者。我们还在这里使用了log()来记录生成的每个元素。因此，**这将“one”、“two”、“three”、“four”这样的元素逐个输出，然后终止**。

## 4. flatMapIterable

对于相同的字符串列表示例，我们现在将探索flatMapIterable—一个自定义的运算符。

在这里，我们不需要从List中显式创建Flux；我们只需要提供List。此运算符隐式地从集合的元素中创建一个Flux。让我们使用flatMapIterable作为我们的解决方案：

```java
private <T> Flux<T> monoToFluxUsingFlatMapIterable(Mono<List<T>> monoList) {
    return monoList
        .flatMapIterable(list -> list)
        .log();
}
```

在这里，flatMapIterable获取Mono的List将其在内部转换为其元素的Flux。因此，与flatMapMany运算符相比，它更加优化。**这将输出相同的“one”、“two”、“three”、“four”，然后终止**。

## 5. 总结

在本文中，我们讨论了使用运算符flatMapMany和flatMapIterable将Mono<List<T\>>转换为Flux<T\>的不同方法。两者都是易于使用的运算符。flatMapMany对于更通用的发布者很有用，而flatMapIterable针对此类目的进行了更好的优化。