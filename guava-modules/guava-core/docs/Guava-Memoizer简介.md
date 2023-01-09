## 1. 概述

在本教程中，我们将探索 Google 的 Guava 库的记忆功能。

记忆化是一种通过缓存第一次执行函数的结果来避免重复执行计算量大的函数的技术。

### 1.1. 记忆化与缓存

在内存存储方面，记忆化类似于缓存。这两种技术都试图通过减少对计算量大的代码的调用次数来提高效率。

然而，缓存是一个更通用的术语，它解决类实例化、对象检索或内容检索级别的问题，而记忆化则解决方法/函数执行级别的问题。

### 1.2. Guava Memoizer 和 Guava Cache

Guava 支持记忆和缓存。记忆适用于没有参数的函数 ( Supplier ) 和只有一个参数的函数 ( Function )。这里的Supplier和Function指的是 Guava 函数接口，它们是同名的Java8 Functional API 接口的直接子类。

从 23.6 版开始，Guava 不支持具有多个参数的函数的记忆。

我们可以按需调用记忆 API 并指定一个驱逐策略，该策略控制内存中保存的条目数量，并通过在符合策略条件的情况下从缓存中驱逐/删除条目来防止使用中的内存不受控制地增长。

记忆化利用了 Guava 缓存；有关 Guava Cache 的更多详细信息，请参阅我们的[Guava Cache 文章](https://www.baeldung.com/guava-cache)。

## 2.供应商记忆

Suppliers类中有两种方法可以启用记忆： memoize和memoizeWithExpiration。

当我们想要执行 memoized 方法时，我们可以简单地调用返回的Supplier的get方法。根据方法的返回值是否存在于内存中，get方法要么返回内存中的值，要么执行 memoized 方法并将返回值传递给调用者。

让我们探讨Supplier记忆的每种方法。

### 2.1. 没有逐出的供应商记忆

我们可以使用Suppliers的memoize方法并将委托的Supplier指定为方法引用：

```java
Supplier<String> memoizedSupplier = Suppliers.memoize(
  CostlySupplier::generateBigNumber);
```

由于我们没有指定逐出策略，一旦调用get方法，返回值将在Java应用程序仍在运行时保留在内存中。在初始调用之后对get的任何调用都将返回记忆值。

### 2.2. 通过生存时间 (TTL) 逐出的供应商记忆

假设我们只想在备忘录中保留Supplier返回的值一段时间。

除了委托的Supplier之外，我们还可以使用Suppliers的memoizeWithExpiration方法并指定到期时间及其相应的时间单位(例如，秒、分钟) ：

```java
Supplier<String> memoizedSupplier = Suppliers.memoizeWithExpiration(
  CostlySupplier::generateBigNumber, 5, TimeUnit.SECONDS);
```

在经过指定的时间(5 秒)后，缓存将从内存中逐出Supplier的返回值，随后对get方法的任何调用都将重新执行generateBigNumber。

有关更多详细信息，请参阅[Javadoc](https://google.github.io/guava/releases/23.0/api/docs/com/google/common/base/Suppliers.html#memoizeWithExpiration-com.google.common.base.Supplier-long-java.util.concurrent.TimeUnit-)。

### 2.3. 例子

让我们模拟一个名为generateBigNumber的计算量大的方法：

```java
public class CostlySupplier {
    private static BigInteger generateBigNumber() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {}
        return new BigInteger("12345");
    }
}
```

我们的示例方法将花费 2 秒来执行，然后返回一个BigInteger结果。我们可以使用memoize或memoizeWithExpiration API对其进行记忆。

为简单起见，我们将省略驱逐政策：

```java
@Test
public void givenMemoizedSupplier_whenGet_thenSubsequentGetsAreFast() {
    Supplier<BigInteger> memoizedSupplier; 
    memoizedSupplier = Suppliers.memoize(CostlySupplier::generateBigNumber);

    BigInteger expectedValue = new BigInteger("12345");
    assertSupplierGetExecutionResultAndDuration(
      memoizedSupplier, expectedValue, 2000D);
    assertSupplierGetExecutionResultAndDuration(
      memoizedSupplier, expectedValue, 0D);
    assertSupplierGetExecutionResultAndDuration(
      memoizedSupplier, expectedValue, 0D);
}

private <T> void assertSupplierGetExecutionResultAndDuration(
  Supplier<T> supplier, T expectedValue, double expectedDurationInMs) {
    Instant start = Instant.now();
    T value = supplier.get();
    Long durationInMs = Duration.between(start, Instant.now()).toMillis();
    double marginOfErrorInMs = 100D;

    assertThat(value, is(equalTo(expectedValue)));
    assertThat(
      durationInMs.doubleValue(), 
      is(closeTo(expectedDurationInMs, marginOfErrorInMs)));
}
```

第一次get方法调用需要两秒钟，就像在generateBigNumber方法中模拟的那样；然而，随后对get()的调用将执行得更快，因为generateBigNumber结果已被记忆。

## 3.功能记忆

为了记忆一个采用单个参数的方法，我们使用CacheLoader的from方法构建一个LoadingCache映射，以将我们的方法作为 Guava函数提供给构建器。

LoadingCache是一个并发映射，其值由CacheLoader自动加载。CacheLoader通过计算from方法中指定的Function并将返回值放入LoadingCache 来填充映射。有关更多详细信息，请参阅[Javadoc](https://google.github.io/guava/releases/23.0/api/docs/com/google/common/cache/CacheLoader.html#from-com.google.common.base.Function-)。

LoadingCache的键是Function的参数/输入，而映射的值是Function的返回值：

```java
LoadingCache<Integer, BigInteger> memo = CacheBuilder.newBuilder()
  .build(CacheLoader.from(FibonacciSequence::getFibonacciNumber));
```

由于LoadingCache是并发映射，因此它不允许空键或空值。因此，我们需要确保Function不支持 null 作为参数或返回 null 值。

### 3.1. 带逐出策略的函数记忆

当我们记忆一个Function时，我们可以应用不同的 Guava Cache 的逐出策略，如[Guava Cache 文章](https://www.baeldung.com/guava-cache)的第 3 节所述。

例如，我们可以驱逐闲置 2 秒的条目：

```java
LoadingCache<Integer, BigInteger> memo = CacheBuilder.newBuilder()
  .expireAfterAccess(2, TimeUnit.SECONDS)
  .build(CacheLoader.from(Fibonacci::getFibonacciNumber));
```

接下来，让我们看一下Function memoization的两个用例：Fibonacci sequence 和 factorial。

### 3.2. 斐波那契数列示例

我们可以从给定的数字n递归计算斐波那契数：

```java
public static BigInteger getFibonacciNumber(int n) {
    if (n == 0) {
        return BigInteger.ZERO;
    } else if (n == 1) {
        return BigInteger.ONE;
    } else {
        return getFibonacciNumber(n - 1).add(getFibonacciNumber(n - 2));
    }
}
```

没有memoization，当输入值比较高的时候，函数的执行会比较慢。

为了提高效率和性能，我们可以使用CacheLoader和CacheBuilder来记忆 getFibonacciNumber，必要时指定逐出策略。

在以下示例中，一旦备忘录大小达到 100 个条目，我们将删除最旧的条目：

```java
public class FibonacciSequence {
    private static LoadingCache<Integer, BigInteger> memo = CacheBuilder.newBuilder()
      .maximumSize(100)
      .build(CacheLoader.from(FibonacciSequence::getFibonacciNumber));

    public static BigInteger getFibonacciNumber(int n) {
        if (n == 0) {
            return BigInteger.ZERO;
        } else if (n == 1) {
            return BigInteger.ONE;
        } else {
            return memo.getUnchecked(n - 1).add(memo.getUnchecked(n - 2));
        }
    }
}
```

在这里，我们使用getUnchecked方法返回值(如果存在)而不抛出已检查的异常。

在这种情况下，在CacheLoader的from方法调用中指定getFibonacciNumber方法引用时，我们不需要显式处理异常。

有关更多详细信息，请参阅[Javadoc](https://google.github.io/guava/releases/23.0/api/docs/com/google/common/cache/LoadingCache.html#getUnchecked-K-)。

### 3.3. 阶乘例子

接下来，我们有另一种递归方法来计算给定输入值 n 的阶乘：

```java
public static BigInteger getFactorial(int n) {
    if (n == 0) {
        return BigInteger.ONE;
    } else {
        return BigInteger.valueOf(n).multiply(getFactorial(n - 1));
    }
}
```

我们可以通过应用记忆来提高这个实现的效率：

```java
public class Factorial {
    private static LoadingCache<Integer, BigInteger> memo = CacheBuilder.newBuilder()
      .build(CacheLoader.from(Factorial::getFactorial));

    public static BigInteger getFactorial(int n) {
        if (n == 0) {
            return BigInteger.ONE;
        } else {
            return BigInteger.valueOf(n).multiply(memo.getUnchecked(n - 1));
        }
    }
}
```

## 4. 总结

在本文中，我们了解了 Guava 如何提供 API 来执行Supplier和Function方法的记忆。我们还展示了如何指定内存中存储的函数结果的逐出策略。