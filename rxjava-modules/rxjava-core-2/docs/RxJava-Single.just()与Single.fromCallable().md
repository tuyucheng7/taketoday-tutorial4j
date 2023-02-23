## 1. 概述

在这个简短的教程中，我们将比较两种在RxJava中创建Single对象的流行方法，我们将使用[TestSubscriber](https://www.baeldung.com/rxjava-testing)测试实现。首先，我们将查看Single.just()工厂方法并急切地使用它来创建对象的实例。之后，我们将了解Single.fromCallable()并了解如何使用它来提高性能。

## 2. Single.just()

Single.just()是创建Observable实例的直接方式，它以一个对象作为参数并将其包装在RxJava的Single中：

```java
Single<String> employee = Single.just("John Doe");
```

不过，大多数时候，我们会以某种方式检索或计算这些数据。出于演示目的，假设我们正在从EmployeeRepository类中检索员工的姓名。为了进行测试，我们将使用[Mockito](https://www.baeldung.com/mockito-series)来检查与此Repository的交互，并使用[TestSubscriber](https://www.baeldung.com/rxjava-testing)来测试生成的可观察对象发布的值：

```java
@Test
void givenASubscriber_whenUsingJust_thenReturnTheCorrectValue() {
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
    Mockito.when(repository.findById(123L)).thenReturn("John Doe");

    Single<String> employee = Single.just(repository.findById(123L));
    employee.subscribe(testSubscriber);

    testSubscriber.assertValue("John Doe");
    testSubscriber.assertCompleted();
}
```

正如预期的那样，testSubscriber发布的值与Repository返回的值相同。另一方面，**即使没有订阅者，数据仍然会被获取**。让我们使用Mockito来验证与EmployeeRepository的交互次数：

```java
@Test
void givenNoSubscriber_whenUsingJust_thenDataIsFetched() {
    Mockito.when(repository.findById(123L)).thenReturn("John Doe");

    Single<String> employee = Single.just(repository.findById(123L));

    Mockito.verify(repository, times(1)).findById(123L);
}
```

## 3. Single.fromCallable()

作为替代方案，我们可以使用Single.fromCallable()。在这种情况下，我们需要提供Callable接口或lambda表达式的实现。换句话说，我们可以传递一个函数来获取数据，这个函数只有在有人订阅时才会被调用。因此，**如果没有订阅者，则不会与Repository进行交互**：

```java
@Test
void givenNoSubscriber_whenUsingFromCallable_thenNoDataIsFetched() {
    Single<String> employee = Single.fromCallable(() -> repository.findById(123L));

    Mockito.verify(repository, never()).findById(123L);
}
```

但是，一旦有人订阅了employee，数据就会被获取然后发布。让我们添加另一个测试并检查生成的Single对象是否发布了正确的值：

```java
@Test
void givenASubscriber_whenUsingFromCallable_thenReturnCorrectValue() {
    TestSubscriber<String> testSubscriber = new TestSubscriber<>();
    Mockito.when(repository.findById(123L)).thenReturn("John Doe");

    Single<String> employee = Single.fromCallable(() -> repository.findById(123L));
    employee.subscribe(testSubscriber);

    Mockito.verify(repository, times(1)).findById(123L);
    testSubscriber.assertCompleted();
    testSubscriber.assertValue("John Doe");
}
```

## 4. 总结

在本文中，我们比较了Single的just()和fromCallable()工厂方法。我们了解到，如果我们想在获取数据时利用惰性评估，我们应该使用fromCallable()选项。