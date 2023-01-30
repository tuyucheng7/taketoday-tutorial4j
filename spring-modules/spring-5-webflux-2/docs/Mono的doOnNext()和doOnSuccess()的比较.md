## 1. 概述

在这个简短的教程中，我们将探索[Spring 5 WebFlux](https://www.baeldung.com/spring-webflux)中Mono对象的各种监听器。我们将比较doOnNext()和doOnSuccess()方法并发现，即使它们相似，它们对空Mono的行为也不同。

## 2. doOnNext

**Mono的doOnNext()允许我们附加一个监听器，该监听器将在数据发出时触发**。对于本文中的代码示例，我们将使用PaymentService类。在这种情况下，我们将仅在paymentMono发出数据时才使用doOnNext()调用processPayment方法：

```java
@Test
void givenAPaymentMono_whenCallingServiceOnNext_thenCallServiceWithPayment() {
    Payment paymentOf100 = new Payment(100);
    Mono<Payment> paymentMono = Mono.just(paymentOf100);

    paymentMono.doOnNext(paymentService::processPayment)
        .block();

    verify(paymentService).processPayment(paymentOf100);
}
```

**但是，空的Mono不会发出任何数据，并且也不会触发doOnNext**。因此，如果我们使用Mono.empty()重复测试，则不应再调用processPayment方法：

```java
@Test
void givenAnEmptyMono_whenCallingServiceOnNext_thenDoNotCallService() {
    Mono<Payment> emptyMono = Mono.empty();

    emptyMono.doOnNext(paymentService::processPayment)
        .block();

    verify(paymentService, never()).processPayment(any());
}
```

## 3. doOnSuccess

**我们可以使用doOnSuccess附加一个监听器，当Mono成功完成时将触发该监听器**。让我们重复测试，但这次使用doOnSuccess：

```java
@Test
void givenAPaymentMono_whenCallingServiceOnSuccess_thenCallServiceWithPayment() {
    Payment paymentOf100 = new Payment(100);
    Mono<Payment> paymentMono = Mono.just(paymentOf100);

    paymentMono.doOnSuccess(paymentService::processPayment)
        .block();

    verify(paymentService).processPayment(paymentOf100);
}
```

**但是，我们应该注意，即使没有发出任何数据，Mono也被认为是成功完成的**。因此，对于空的Mono，上面的代码将使用null Payment调用processPayment方法：

```java
Test
void givenAnEmptyMono_whenCallingServiceOnSuccess_thenCallServiceWithNull() {
    Mono<Payment> emptyMono = Mono.empty();

    emptyMono.doOnSuccess(paymentService::processPayment)
        .block();

    verify(paymentService).processPayment(null);
}
```

## 4. 总结

在这篇简短的文章中，我们了解了Mono的doOnNext和doOnSuccess监听器之间的区别。我们看到如果我们想对收到的数据做出反应，我们可以使用doOnNext。另一方面，如果我们希望方法调用在Mono成功完成时发生，无论它是否发出数据，我们应该使用doOnSuccess。