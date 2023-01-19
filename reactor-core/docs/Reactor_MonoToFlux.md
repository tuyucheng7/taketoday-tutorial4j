## 1. 概述

有时在响应式编程中，我们可能会有一个发布大量元素集合的发布者。在某些情况下，此发布者的消费者可能无法一次性处理所有元素。
因此，我们可能需要异步发布每个元素以匹配消费者的处理速度。

在本教程中，我们将探讨如何将集合类型的Mono转换为集合元素的Flux。

## 2. 问题描述

在处理响应流时，我们使用发布者Publisher及其两个实现Flux和Mono。
Mono<T\>是Publisher<T\>的一种类型，可以发出0或1个T类型的元素，而Flux<T\>可以发出0到N个T类型的元素。

假设我们有一个Mono发布者，它持有一个Mono<List<T\>> — T类型集合。我们的要求是使用Flux<T\>异步生成集合元素：

<img src="../assets/img1.png">

在这里，我们可以看到我们需要通过Mono<List<T\>>上的运算符来执行此转换。
首先，我们将从发布者Mono中提取集合元素，然后作为Flux异步地一个接一个地生成这些元素。

**Mono包含一个map操作符和一个flatMap操作符，前者可以同步转换Mono，后者可以异步转换Mono**。
此外，这两个操作符都生成一个元素作为输出。

**然而，对于我们在展平Mono<List<T\>>之后生成许多元素的用例，我们可以使用flatMapMany或FlatMapIterable**。

让我们看看如何使用这些运算符。

## 3. flatMapMany

首先我们从一个字符串集合创建我们的Mono发布者：

```java
class MonoUnitTest {

    private Mono<List<String>> monoOfList() {
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        list.add("four");
        return Mono.just(list);
    }
}
```

flatMapMany是Mono上返回发布者的通用运算符。让我们将flatMapMany应用于我们的解决方案：

```java
class MonoUnitTest {

    private <T> Flux<T> monoToFluxUsingFlatMapMany(Mono<List<T>> monoList) {
        return monoList.flatMapMany(Flux::fromIterable).log();
    }
}
```

在这种情况下，flatMapMany获取Mono的List集合，将其展平，并使用Flux的运算符fromIterable创建一个Flux发布者。
我们还在这里使用log()来记录生成的每个元素。因此，这将“one”、“two”、“three”、“four”这样的元素一个一个地输出，然后终止。

## 4. flatMapIterable

现在介绍flatMapIterable的用法 - 它是一个定制的运算符。

在这里，我们不需要从集合中显式创建Flux；我们只需要提供集合。这个操作符隐式地从集合的元素中创建了一个Flux。
让我们使用flatMapIterable作为我们的解决方案：

```java
class MonoUnitTest {

    private <T> Flux<T> monoToFluxUsingFlatMapIterable(Mono<List<T>> monoList) {
        return monoList.flatMapIterable(list -> list).log();
    }
}
```

flatMapIterable获取Mono的集合并在内部将其转换为其元素的Flux。因此，与flatMapMany运算符相比，它更加优化。
**这将输出相同的“one”、“two”、“three”、“four”，然后终止**。

## 5. 总结

在本文中，我们讨论了使用操作符flatMapMany和flatMapIterable将Mono转换为Flux的不同方法。
两者都是易于使用的操作符。flatMapMany对于更通用的发布者很有用，但flatMapIterable更适合于此类目的。