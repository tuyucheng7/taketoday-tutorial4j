## 1. 概述

在这篇简短的文章中，我们将研究Java 中的[StringBuilder](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StringBuilder.html)和[StringBuffer](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/StringBuffer.html)之间的异同。

简单地说，StringBuilder是在Java1.5 中作为StringBuffer的替代品引入的。

## 2.相似之处

StringBuilder和StringBuffer都会创建包含可变字符序列的对象。让我们看看它是如何工作的，以及它与不可变String类的比较：

```java
String immutable = "abc";
immutable = immutable + "def";
```

尽管看起来我们正在通过附加“def”来修改同一个对象，但我们正在创建一个新对象，因为无法修改String实例。

当使用StringBuffer或StringBuilder 时，我们可以使用append()方法：

```java
StringBuffer sb = new StringBuffer("abc");
sb.append("def");
```

在这种情况下，没有创建新对象。我们已经在sb实例上调用了append()方法并修改了它的内容。StringBuffer和StringBuilder是可变对象。

## 3. 差异

StringBuffer是同步的，因此是线程安全的。 StringBuilder与StringBuffer API 兼容，但不保证同步。

因为不是线程安全的实现，速度更快，建议在不需要线程安全的地方使用。

### 3.1. 表现

在小的迭代中，性能差异是微不足道的。[让我们用JMH](https://www.baeldung.com/java-jvm-warmup)做一个快速的微基准测试：

```java
@State(Scope.Benchmark)
public static class MyState {
    int iterations = 1000;
    String initial = "abc";
    String suffix = "def";
}

@Benchmark
public StringBuffer benchmarkStringBuffer(MyState state) {
    StringBuffer stringBuffer = new StringBuffer(state.initial);
    for (int i = 0; i < state.iterations; i++) {
        stringBuffer.append(state.suffix);
    }
    return stringBuffer;
}

@Benchmark
public StringBuilder benchmarkStringBuilder(MyState state) {
    StringBuilder stringBuilder = new StringBuilder(state.initial);
    for (int i = 0; i < state.iterations; i++) {
        stringBuilder.append(state.suffix);
    }
    return stringBuilder;
}
```

我们使用了默认的吞吐量模式——即每单位时间的操作数(分数越高越好)，它给出：

```bash
Benchmark                                          Mode  Cnt      Score      Error  Units
StringBufferStringBuilder.benchmarkStringBuffer   thrpt  200  86169.834 ±  972.477  ops/s
StringBufferStringBuilder.benchmarkStringBuilder  thrpt  200  91076.952 ± 2818.028  ops/s
```

如果我们将迭代次数从 1k 增加到 1m，那么我们会得到：

```bash
Benchmark                                          Mode  Cnt   Score   Error  Units
StringBufferStringBuilder.benchmarkStringBuffer   thrpt  200  77.178 ± 0.898  ops/s
StringBufferStringBuilder.benchmarkStringBuilder  thrpt  200  85.769 ± 1.966  ops/s
```

但是，请记住，这是一个微基准测试，它可能会对应用程序的实际性能产生实际影响，也可能不会产生真正影响。

## 4。总结

简而言之，StringBuffer是线程安全的实现，因此比StringBuilder慢。

在单线程程序中，我们可以使用StringBuilder。然而，StringBuilder相对于StringBuffer的性能提升可能太小，不足以证明在任何地方都替换它。在执行任何类型的工作以将一种实现替换为另一种实现之前，分析应用程序并了解其运行时性能特征始终是一个好主意。