## 1. 概述

在这个快速教程中，我们介绍[AssertJ](https://joel-costigliola.github.io/assertj/)中专用的异常断言。

## 2. 不使用AssertJ

为了测试是否抛出了异常，我们需要捕获异常然后执行断言：

```java
try {
    // ...
} catch (Exception e) {
    // assertions
}
```

但是，如果没有抛出异常怎么办？在这种情况下，测试将通过；这就是为什么必须手动失败测试用例的原因。

## 3. 使用AssertJ

使用Java 8，我们可以通过利用AssertJ和lambda表达式轻松地对异常进行断言。

### 3.1 使用assertThatThrownBy()

以下检查通过越界的索引获取值是否会引发IndexOutOfBoundsException：

```java
assertThatThrownBy(() -> {
    List<String> list = Arrays.asList("String one", "String two");
    list.get(2);
}).isInstanceOf(IndexOutOfBoundsException.class)
    .hasMessageContaining("Index: 2, Size: 2");
```

**注意可能引发异常的代码片段是如何作为lambda表达式传递的**，当然，我们可以在这里利用各种标准AssertJ断言，例如：

```java
.hasMessage("Index: %s, Size: %s", 2, 2)
.hasMessageStartingWith("Index: 2")
.hasMessageContaining("2")
.hasMessageEndingWith("Size: 2")
.hasMessageMatching("Index: d+, Size: d+")
.hasCauseInstanceOf(IOException.class)
.hasStackTraceContaining("java.io.IOException");
```

### 3.2 使用assertThatExceptionOfType

思路和上面的例子差不多，但是我们可以在开头指定异常类型：

```java
assertThatExceptionOfType(IndexOutOfBoundsException.class)
    .isThrownBy(() -> { 
        // ...
}).hasMessageMatching("Index: d+, Size: d+");
```

### 3.3 使用assertThatIOException和其他常见类型

**AssertJ为常见的异常类型提供了包装器，例如**：

```java
assertThatIOException().isThrownBy(() -> {
    // ...
});
```

和：

-   assertThatIllegalArgumentException()
-   assertThatIllegalStateException()
-   assertThatIOException()
-   assertThatNullPointerException()

### 3.4 将异常与断言分开

编写单元测试的另一种方法是**在单独的部分中编写when和then逻辑**：

```java
// when
Throwable thrown = catchThrowable(() -> {
    // ...
});

// then
assertThat(thrown)
    .isInstanceOf(ArithmeticException.class)
    .hasMessageContaining("/ by zero");
```

## 4. 总结

在这篇简短的文章中，我们介绍了使用AssertJ对异常执行断言的不同方法。