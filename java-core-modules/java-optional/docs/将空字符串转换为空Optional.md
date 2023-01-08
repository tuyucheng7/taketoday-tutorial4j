## 1. 概述

在本快速教程中，我们将介绍将null 或空String转换 为空[Optional](https://www.baeldung.com/java-optional)的不同方法。

从null中获取一个空的Optional很简单——我们只需要使用[Optional.ofNullable()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html#ofNullable(T))。但是，如果我们希望空String也以这种方式工作怎么办？

因此，让我们探索一些将空字符串转换为空可选的不同选项。

## 2. 使用Java8

在Java8 中，我们可以利用这样一个事实，即如果不满足Optional#filter的谓词，则它返回一个空的Optional：

```java
@Test
public void givenEmptyString_whenFilteringOnOptional_thenEmptyOptionalIsReturned() {
    String str = "";
    Optional<String> opt = Optional.ofNullable(str).filter(s -> !s.isEmpty());
    Assert.assertFalse(opt.isPresent());
}
```

我们甚至不需要在这里检查 null ，因为 在str为 null 的情况下ofNullable 将为我们短路 。

不过，为谓词创建一个特殊的[lambda](https://www.baeldung.com/java-8-lambda-expressions-tips)有点麻烦。我们不能以某种方式摆脱它吗？

## 3. 使用Java11

上述愿望的答案实际上直到Java11 才出现。

在Java11 中，我们仍将使用[Optional.filter()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Optional.html#filter(java.util.function.Predicate))，但Java11 引入了一个新的[Predicate.not()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/function/Predicate.html#not(java.util.function.Predicate)) API，可以轻松否定方法引用。

所以，让我们简化我们之前所做的，现在使用方法引用来代替：

```java
@Test
public void givenEmptyString_whenFilteringOnOptionalInJava11_thenEmptyOptionalIsReturned() {
    String str = "";
    Optional<String> opt = Optional.ofNullable(str).filter(Predicate.not(String::isEmpty));
    Assert.assertFalse(opt.isPresent());
}
```

## 4.使用番石榴

我们也可以使用[Guava](https://www.baeldung.com/guava-functions-predicates)来满足我们的需求。但是，在那种情况下，我们将使用一种稍微不同的方法。

我们不是在Optional#ofNullable 的结果 上调用过滤器方法，而是首先使用 Guava 的String#emptyToNull将空字符串转换为null ，然后才将其传递给Optional#ofNullable：

```java
@Test
public void givenEmptyString_whenPassingResultOfEmptyToNullToOfNullable_thenEmptyOptionalIsReturned() {
    String str = "";
    Optional<String> opt = Optional.ofNullable(Strings.emptyToNull(str));
    Assert.assertFalse(opt.isPresent());
}
```

## 5.总结

在这篇简短的文章中，我们探讨了将空String转换为空Optional 的不同方法。