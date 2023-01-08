## 1. 概述

在本文中，我们将研究Java9 对[Optional](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html) API 的补充。

除了模块化之外，Java 9 还为Optional类添加了三个非常有用的方法。

## 2. or()方法

有时，当我们的Optional为空时，我们想要执行一些其他也返回 Optional 的操作。

在Java9 之前，Optional类只有orElse()和orElseGet()方法，但两者都需要返回未包装的值。

Java 9 引入了or()方法，如果我们的Optional为空，它会延迟返回另一个Optional。如果我们的第一个Optional有一个定义的值，传递给or()方法的 lambda将不会被调用，也不会计算和返回值：

```java
@Test
public void givenOptional_whenPresent_thenShouldTakeAValueFromIt() {
    //given
    String expected = "properValue";
    Optional<String> value = Optional.of(expected);
    Optional<String> defaultValue = Optional.of("default");

    //when
    Optional<String> result = value.or(() -> defaultValue);

    //then
    assertThat(result.get()).isEqualTo(expected);
}
```

在Optional为空的情况下，返回结果将与 defaultValue 相同：

```java
@Test
public void givenOptional_whenEmpty_thenShouldTakeAValueFromOr() {
    // given
    String defaultString = "default";
    Optional<String> value = Optional.empty();
    Optional<String> defaultValue = Optional.of(defaultString);

    // when
    Optional<String> result = value.or(() -> defaultValue);

    // then
    assertThat(result.get()).isEqualTo(defaultString);
}
```

## 3. ifPresentOrElse()方法

当我们有一个Optional实例时，我们通常希望对它的基础值执行特定的操作。另一方面，如果Optional为空，我们希望记录它或通过增加一些指标来跟踪该事实。

ifPresentOrElse ()方法正是为这种情况而创建的。我们可以传递一个Consumer，如果定义了Optional将被调用，如果Optional为空则将执行Runnable 。

假设我们有一个已定义的Optional，如果该值存在，我们想增加一个特定的计数器：

```java
@Test
public void givenOptional_whenPresent_thenShouldExecuteProperCallback() {
    // given
    Optional<String> value = Optional.of("properValue");
    AtomicInteger successCounter = new AtomicInteger(0);
    AtomicInteger onEmptyOptionalCounter = new AtomicInteger(0);

    // when
    value.ifPresentOrElse(
      v -> successCounter.incrementAndGet(), 
      onEmptyOptionalCounter::incrementAndGet);

    // then
    assertThat(successCounter.get()).isEqualTo(1);
    assertThat(onEmptyOptionalCounter.get()).isEqualTo(0);
}
```

请注意，作为第二个参数传递的回调未执行。

在 Optional 为空的情况下，将执行第二个回调：

```java
@Test
public void givenOptional_whenNotPresent_thenShouldExecuteProperCallback() {
    // given
    Optional<String> value = Optional.empty();
    AtomicInteger successCounter = new AtomicInteger(0);
    AtomicInteger onEmptyOptionalCounter = new AtomicInteger(0);

    // when
    value.ifPresentOrElse(
      v -> successCounter.incrementAndGet(), 
      onEmptyOptionalCounter::incrementAndGet);

    // then
    assertThat(successCounter.get()).isEqualTo(0);
    assertThat(onEmptyOptionalCounter.get()).isEqualTo(1);
}
```

## 4. stream()方法

在Java9 中添加到Optional类的最后一个方法是stream()方法。

Java 有一个非常流畅和优雅的Stream API，可以对集合进行操作并利用许多函数式编程概念。最新的Java版本在Optional类上引入了stream()方法，允许我们将Optional实例视为Stream。

假设我们有一个已定义的Optional，我们正在调用它的stream()方法。这将创建一个包含一个元素的Stream，我们可以在其上使用Stream API中可用的所有方法：

```java
@Test
public void givenOptionalOfSome_whenToStream_thenShouldTreatItAsOneElementStream() {
    // given
    Optional<String> value = Optional.of("a");

    // when
    List<String> collect = value.stream().map(String::toUpperCase).collect(Collectors.toList());

    // then
    assertThat(collect).hasSameElementsAs(List.of("A"));
}
```

另一方面，如果Optional不存在，调用它的stream()方法将创建一个空的Stream：

```java
@Test
public void givenOptionalOfNone_whenToStream_thenShouldTreatItAsZeroElementStream() {
    // given
    Optional<String> value = Optional.empty();

    // when
    List<String> collect = value.stream()
      .map(String::toUpperCase)
      .collect(Collectors.toList());

    // then
    assertThat(collect).isEmpty();
}
```

我们现在可以快速过滤[Optionals](https://www.baeldung.com/java-filter-stream-of-optional)[流。](https://www.baeldung.com/java-filter-stream-of-optional)

对空Stream进行操作不会产生任何影响，但由于有了stream()方法，我们现在可以将Optional API 与Stream API 链接起来。这使我们能够创建更优雅、更流畅的代码。

## 5.总结

在这篇简短的文章中，我们了解了Java9可选API 的新增功能。

我们看到了如何使用or()方法在源Optional为空的情况下返回 Optional 。如果值存在，我们使用ifPresentOrElse()来执行消费者，否则运行另一个回调。

最后，我们看到了如何使用stream()方法将Optional与Stream API 链接起来。