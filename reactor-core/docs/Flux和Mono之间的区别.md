## 1. 概述

在本教程中，我们将了解[Reactor Core](https://www.baeldung.com/reactor-core)库中[Flux](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html)和[Mono](https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html)之间的区别。

## 2. 什么是Mono？

Mono是一种特殊类型的[Publisher](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/Publisher.html)，**Mono对象表示单个值或空值**。这意味着它最多只能为onNext()请求发出一个值，然后以onComplete()信号终止。如果失败，它只会发出一个onError()信号。

让我们看一个带有完成信号的Mono示例：

```java
@Test
void givenMonoPublisher_whenSubscribeThenReturnSingleValue() {
	Mono<String> helloMono = Mono.just("Hello");

	StepVerifier.create(helloMono)
	    .expectNext("Hello")
	    .expectComplete()
	    .verify();
}
```

我们在这里可以看到，当helloMono被订阅时，它只发出一个值，然后发送完成信号。

## 3. 什么是Flux？

Flux是一个标准的Publisher，代表0到N个异步序列值。这意味着**它可以为onNext()请求发出0到许多值，可能是无限值，然后以完成或错误信号终止**。

让我们看一个带有完成信号的Flux示例：

```java
@Test
void givenFluxPublisher_whenSubscribeThenReturnMultipleValues() {
	Flux<String> stringFlux = Flux.just("Hello", "Tuyucheng");

	StepVerifier.create(stringFlux)
	    .expectNext("Hello")
	    .expectNext("Tuyucheng")
	    .expectComplete()
	    .verify();
}
```

现在，让我们看一个带有错误信号的Flux示例：

```java
@Test
void givenFluxPublisher_whenSubscribeThenReturnMultipleValuesWithError() {
	Flux<String> stringFlux = Flux.just("Hello", "Tuyucheng", "Error")
	    .map(str -> {
	    	if (str.equals("Error"))
	    		throw new RuntimeException("Throwing Error");
	    	return str;
	    });

	StepVerifier.create(stringFlux)
	    .expectNext("Hello")
	    .expectNext("Tuyucheng")
	    .expectError()
	    .verify();
}
```

我们可以在这里看到，在从Flux中获取两个值后，我们得到了一个错误。

## 4. Mono与Flux

Mono和Flux都是Publisher接口的实现。简单来说，我们可以说：当我们在做计算或向数据库或外部服务发出请求，并期望得到最多一个结果时，我们应该使用Mono。

当我们期望从计算、数据库或外部服务调用中获得多个结果时，我们应该使用Flux。

Mono与Java中的[Optional](https://www.baeldung.com/java-optional)类更相关，因为它包含0或1个值，而Flux与[List](https://www.baeldung.com/java-arraylist)更相关，因为它可以有N个值。

## 5. 总结

在本文中，我们了解了Mono和Flux之间的区别。