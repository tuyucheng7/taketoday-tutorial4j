## 1. 概述

lambda 参数的局部变量语法是Java11 中引入的唯一语言特性。在本教程中，我们将探索和使用这个新特性。

## 2. Lambda 参数的局部变量语法

Java 10 中引入的关键特性之一是[局部变量类型推断](https://www.baeldung.com/java-10-local-variable-type-inference)。它允许使用var作为局部变量的类型而不是实际类型。编译器根据分配给变量的值推断类型。

但是，我们不能将此功能与 lambda 参数一起使用。例如，考虑以下 lambda。这里我们明确指定参数的类型：

```java
(String s1, String s2) -> s1 + s2
```

我们可以跳过参数类型并将 lambda 重写为：

```java
(s1, s2) -> s1 + s2
```

甚至Java8 也支持这一点。Java 10 中对此的逻辑扩展是：

```java
(var s1, var s2) -> s1 + s2
```

但是，Java 10 不支持这一点。

Java 11 通过支持上述语法解决了这个问题。这使得var在局部变量和 lambda 参数中的使用是一致的。


## 3. 好处

当我们可以简单地跳过类型时，为什么我们要使用var作为 lambda 参数？

一致性的一个好处是修饰符可以应用于局部变量和 lambda 形式而不会失去简洁性。例如，常见的修饰符是类型注解：

```java
(@Nonnull var s1, @Nullable var s2) -> s1 + s2
```

我们不能在不指定类型的情况下使用此类注解。

## 4. 限制

在 lambda中使用var有一些限制。

例如，我们不能对某些参数使用var而对其他参数使用 skip：

```java
(var s1, s2) -> s1 + s2
```

同样，我们不能将var与显式类型混合：

```java
(var s1, String s2) -> s1 + s2
```

最后，即使我们可以跳过单参数 lambda 中的括号：

```java
s1 -> s1.toUpperCase()
```

使用var时我们不能跳过它们：

```java
var s1 -> s1.toUpperCase()
```

以上三种用法都会导致编译错误。

## 5. 总结

在这篇快速文章中，我们探索了Java11 中这个很酷的新特性，并了解了如何将局部变量语法用于 lambda 参数。