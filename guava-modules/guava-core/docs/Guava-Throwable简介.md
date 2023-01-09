## 1. 概述

在本文中，我们将快速浏览一下 Google Guava 的Throwables类。

此类包含一组用于处理异常处理的静态实用方法，并且：

-   传播
-   处理原因链

## 2.Maven依赖

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>31.0.1-jre</version>
</dependency>
```

## 3.传播

假设我们与一些抛出通用Throwable的代码进行交互。

在大多数情况下，如果它是Throwable的直接子类，我们希望将其转换为RuntimeException。

但是，如果它是Error、RuntimeException或Exception的实例，我们可以调用propagateIfPossible 来按原样传播它：

```java
try {
    methodThatMightThrowThrowable();
} catch (Throwable t) {
    Throwables.propagateIfPossible(t, Exception.class);
    throw new RuntimeException(t);
}
```

## 4.因果链

Guava 还提供实用方法来检查抛出的异常及其链。

```java
Throwable getRootCause(Throwable)

```

getRootCause方法可以让我们得到最里面的异常，这在我们想要找到最初的原因时很有用。

```java
List<Throwable> getCausalChain(Throwable)
```

此getCausalChain方法将返回层次结构中所有可抛出对象的列表。如果我们想检查它是否包含某种类型的异常，这很方便。

```java
String getStackTraceAsString(Throwable)
```

getStackTraceAsString方法将返回异常的递归堆栈跟踪。

## 5.总结

在本教程中，我们举例说明了一些可以使用 Guava 的Throwables类来简化异常处理的示例。