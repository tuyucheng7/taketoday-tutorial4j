## 1. 概述

 在本教程中，我们将探讨在Java中从double转换为long的各种方法。

## 2. 使用类型转换

让我们检查一种使用转换运算符将double转换为long的直接方法：

```java
Assert.assertEquals(9999, (long) 9999.999);
```

对双精度值 9999.999应用(长)转换运算符会得到 9999。

这是一个[缩小的原始转换](https://www.baeldung.com/java-primitive-conversions)，因为我们正在失去精度。当double转换为long时，结果将保持不变，不包括小数点。

## 3. 使用Double.longValue

现在，让我们探索Double 的内置方法longValue以将double转换为long：

```java
Assert.assertEquals(9999, Double.valueOf(9999.999).longValue());
```

如我们所见，对双精度值 9999.999 应用longValue方法会产生 9999。在内部，longValue方法执行简单的转换。

## 4. 使用数学方法

最后，让我们看看如何使用[Math](https://www.baeldung.com/java-lang-math)类中的round、ceil 和 floor方法将double转换为long ：

让我们首先检查Math.round。这会产生一个最接近参数的值：

```java
Assert.assertEquals(9999, Math.round(9999.0));
Assert.assertEquals(9999, Math.round(9999.444));
Assert.assertEquals(10000, Math.round(9999.999));
```

其次，数学。ceil将产生大于或等于参数的最小值：

```java
Assert.assertEquals(9999, Math.ceil(9999.0), 0);
Assert.assertEquals(10000, Math.ceil(9999.444), 0);
Assert.assertEquals(10000, Math.ceil(9999.999), 0);
```

另一方面，Math.floor与 Math.ceil正好相反。返回小于或等于参数的最大值：

```java
Assert.assertEquals(9999, Math.floor(9999.0), 0);
Assert.assertEquals(9999, Math.floor(9999.444), 0);
Assert.assertEquals(9999, Math.floor(9999.999), 0);
```

请注意，Math.ceil和Math.round都返回一个double值，但在这两种情况下，返回的值都等同于一个long值。

## 5.总结

在本文中，我们讨论了在Java中将double转换为long的各种方法。明智的做法是先了解每种方法的行为方式，然后再将其应用于关键任务代码。