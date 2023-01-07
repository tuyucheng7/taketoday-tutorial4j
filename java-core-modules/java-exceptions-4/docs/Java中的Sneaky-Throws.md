## 1. 概述

在Java中，sneaky throw概念允许我们抛出任何已检查的异常，而无需在方法签名中明确定义它。这允许省略throws声明，有效地模仿了运行时异常的特征。

在本文中，我们将通过查看一些代码示例来了解这在实践中是如何完成的。

## 2.关于Sneaky Throws

检查异常是Java的一部分，而不是 JVM。在字节码中，我们可以不受限制地从任何地方抛出任何异常。

Java 8 带来了一个新的类型推断规则，规定只要允许，抛出 T就被推断为RuntimeException。这提供了在没有辅助方法的情况下实现偷偷摸摸的抛出的能力。

偷偷摸摸抛出的一个问题是你可能希望最终捕获异常，但Java编译器不允许你使用针对特定异常类型的异常处理程序捕获偷偷抛出的已检查异常。

## 3. 偷偷摸摸的投掷

正如我们已经提到的，编译器和 Jave 运行时可以看到不同的东西：

```java
public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
    throw (E) e;
}

private static void throwSneakyIOException() {
    sneakyThrow(new IOException("sneaky"));
}
```

编译器看到带有推断为RuntimeException类型的throws T 的签名，因此它允许传播未经检查的异常。Java 运行时在抛出中看不到任何类型，因为所有抛出都是相同的简单抛出 e 。

这个快速测试演示了这个场景：

```java
@Test
public void throwSneakyIOException_IOExceptionShouldBeThrown() {
    assertThatThrownBy(() -> throwSneakyIOException())
      .isInstanceOf(IOException.class)
      .hasMessage("sneaky")
      .hasStackTraceContaining("SneakyThrowsExamples.throwSneakyIOException");
}
```

此外，可以使用字节码操作或Thread.stop(Throwable)抛出检查异常，但它很麻烦，不推荐使用。

## 4. 使用 Lombok 注解

来自[Lombok的](https://projectlombok.org/)@SneakyThrows注解允许你在不使用throws声明的情况下抛出已检查的异常。当你需要从非常严格的接口(如 Runnable)中的方法引发异常时，这会派上用场。

假设我们从Runnable中抛出一个异常；它只会传递给线程的未处理异常处理程序。

此代码将抛出Exception实例，因此你无需将其包装在RuntimeException 中：

```java
@SneakyThrows
public static void throwSneakyIOExceptionUsingLombok() {
    throw new IOException("lombok sneaky");
}
```

此代码的缺点是你无法捕获未声明的已检查异常。例如，如果我们试图捕获上述方法偷偷抛出的IOException ，我们会得到一个编译错误。

现在，让我们调用throwSneakyIOExceptionUsingLombok 并期望 Lombok 抛出 IOException：

```java
@Test
public void throwSneakyIOExceptionUsingLombok_IOExceptionShouldBeThrown() {
    assertThatThrownBy(() -> throwSneakyIOExceptionUsingLombok())
      .isInstanceOf(IOException.class)
      .hasMessage("lombok sneaky")
      .hasStackTraceContaining("SneakyThrowsExamples.throwSneakyIOExceptionUsingLombok");
}
```

## 5.总结

正如我们在本文中看到的，我们可以欺骗Java编译器将已检查异常视为未检查异常。