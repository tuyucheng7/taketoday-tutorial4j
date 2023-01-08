## 1. 概述

在本教程中，我们将展示如何在 Optional为空时抛出自定义异常。

如果你想更深入地了解Optional， 请在此处查看我们的完整 [指南](https://www.baeldung.com/java-optional)。

## 2.Optional.orElseThrow _

简单地说，如果该值存在，则isPresent() 将返回true，而调用 get() 将返回该值。否则，它会抛出NoSuchElementException。

还有一个方法 orElseThrow(Supplier<? extends X> exceptionSupplier)允许我们提供一个自定义的 Exception 实例。只有存在时，此方法才会返回值。否则，它将抛出由提供的供应商创建的异常。

## 3. 行动中

想象一下，我们有一个返回可空结果的方法：

```java
public String findNameById(String id) {
    return id == null ? null : "example-name";
}
```

现在我们将调用我们的findNameById(String id)方法两次，并使用ofNullable(T value)方法用Optional包装结果。

Optional提供了一个用于创建新实例的静态工厂方法。 这个方法叫做ofNullable(T value)。然后我们可以调用orElseThrow。

我们可以通过运行此测试来验证行为：

```java
@Test
public void whenIdIsNull_thenExceptionIsThrown() {
    assertThrows(InvalidArgumentException.class, () -> Optional
      .ofNullable(personRepository.findNameById(null))
      .orElseThrow(InvalidArgumentException::new));
}
```

根据我们的实现，findNameById 将返回null。因此，新的InvalidArgumentException 将从 orElseThrow 方法中抛出。

我们可以使用非空参数调用此方法。然后，我们不会得到InvalidArgumentException：

```java
@Test
public void whenIdIsNonNull_thenNoExceptionIsThrown() {
    assertAll(() -> Optional
      .ofNullable(personRepository.findNameById("id"))
      .orElseThrow(RuntimeException::new));
}

```

## 4。总结 

在这篇快速文章中，我们讨论了如何从Java8 Optional 中抛出异常。 