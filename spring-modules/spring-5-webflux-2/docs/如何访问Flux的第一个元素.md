## 1. 概述

在本教程中，我们将探索使用[Spring 5 WebFlux](https://www.baeldung.com/spring-webflux)访问Flux的第一个元素的各种方法。

首先，我们将使用API的非阻塞方法，例如next()和take()。之后，我们将看到如何在elementAt()方法的帮助下实现相同的目标，我们需要在其中指定索引。

最后，我们将了解API的阻塞方法，我们将使用blockFirst()来访问flux的第一个元素。

## 2. 测试设置

对于本文中的代码示例，我们将使用Payment类，该类只有一个字段，即付款金额amount：

```java
public class Payment {
    private final int amount;
    // constructor and getter
}
```

在测试中，我们将使用名为fluxOfThreePayments的测试工具方法法来构造一个Payment流：

```java
private Flux<Payment> fluxOfThreePayments() {
    return Flux.just(paymentOf100, new Payment(200), new Payment(300));
}
```

之后，我们将使用Spring Reactor的[StepVerifier](https://www.baeldung.com/reactive-streams-step-verifier-test-publisher)来测试结果。

## 3. next()

首先，让我们尝试next()方法。此方法将返回flux的第一个元素，包装到响应式[Mono](https://www.baeldung.com/java-string-from-mono)类型中：

```java
@Test
void givenAPaymentFlux_whenUsingNext_thenGetTheFirstPaymentAsMono() {
    Mono<Payment> firstPayment = fluxOfThreePayments().next();

    StepVerifier.create(firstPayment)
        .expectNext(paymentOf100)
        .verifyComplete();
}
```

另一方面，如果我们在一个空的flux上调用next()，结果将是一个空的Mono。因此，阻塞它将返回null：

```java
@Test
void givenAEmptyFlux_whenUsingNext_thenGetAnEmptyMono() {
    Flux<Payment> emptyFlux = Flux.empty();

    Mono<Payment> firstPayment = emptyFlux.next();

    StepVerifier.create(firstPayment)
        .verifyComplete();
}
```

## 4. take()

**响应式flux的take()方法等同于Java 8 Streams的limit()**，换句话说，我们可以使用take(1)将flux限制为恰好一个元素，然后以阻塞或非阻塞的方式使用它：

```java
@Test
void givenAPaymentFlux_whenUsingTake_thenGetTheFirstPaymentAsFlux() {
    Flux<Payment> firstPaymentFlux = fluxOfThreePayments().take(1);

    StepVerifier.create(firstPaymentFlux)
        .expectNext(paymentOf100)
        .verifyComplete();
}
```

类似地，对于空(empty)flux，take(1)将返回空flux：

```java
@Test
void givenAEmptyFlux_whenUsingNext_thenGetAnEmptyFlux() {
    Flux<Payment> emptyFlux = Flux.empty();

    Flux<Payment> firstPaymentFlux = emptyFlux.take(1);

    StepVerifier.create(firstPaymentFlux)
        .verifyComplete();
}
```

## 5. elementAt()

Flux API还提供了elementAt()方法，我们可以使用elementAt(0)以非阻塞方式获取flux的第一个元素：

```java
@Test
void givenAPaymentFlux_whenUsingElementAt_thenGetTheFirstPaymentAsMono() {
    Mono<Payment> firstPayment = fluxOfThreePayments().elementAt(0);

    StepVerifier.create(firstPayment)
        .expectNext(paymentOf100)
        .verifyComplete();
}
```

但是，如果作为参数传递的索引大于flux发出的元素数，则会发出错误：

```java
@Test
void givenAEmptyFlux_whenUsingElementAt_thenGetAnEmptyMono() {
    Flux<Payment> emptyFlux = Flux.empty();

    Mono<Payment> firstPayment = emptyFlux.elementAt(0);

    StepVerifier.create(firstPayment)
        .expectError(IndexOutOfBoundsException.class);
}
```

## 6. blockFirst()

或者，我们也可以使用blockFirst()。顾名思义，这是一种阻塞方法。因此，如果我们使用blockFirst()，我们将离开响应式世界，并将失去它的所有好处：

```java
@Test
void givenAPaymentFlux_whenUsingBlockFirst_thenGetTheFirstPayment() {
    Payment firstPayment = fluxOfThreePayments().blockFirst();

    assertThat(firstPayment).isEqualTo(paymentOf100);
}
```

## 7. toStream()

最后，我们可以将flux转换为Java 8流，然后访问第一个元素：

```java
@Test
void givenAPaymentFlux_whenUsingToStream_thenGetTheFirstPaymentAsOptional() {
    Optional<Payment> firstPayment = fluxOfThreePayments().toStream()
        .findFirst();

    assertThat(firstPayment).contains(paymentOf100);
}
```

**但是，再一次，如果我们这样做，我们将无法继续使用响应式管道**。

## 8. 总结

在本文中，我们讨论了Java的[响应流](http://www.reactive-streams.org/)API。我们已经看到了访问Flux的第一个元素的各种方法，并且我们了解到如果我们想使用响应式管道，我们应该坚持使用非阻塞解决方案。