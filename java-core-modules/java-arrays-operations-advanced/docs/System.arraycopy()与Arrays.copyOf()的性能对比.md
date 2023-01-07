## 1. 概述

在本教程中，我们将研究两种Java方法的性能：System.arraycopy()和Arrays.copyOf()。首先，我们将分析它们的实现。其次，我们将运行一些基准测试来比较它们的平均执行时间。

## 2.System.arraycopy()的性能

System.arraycopy()从指定位置开始，将数组内容从源数组到目标数组中的指定位置。此外，在之前，JVM 会检查源类型和目标类型是否相同。

在评估System.arraycopy()的性能时，我们需要记住它是一个本地方法。本机方法在平台相关代码(通常是 C)中实现，并通过 JNI 调用访问。

因为本地方法已经针对特定架构进行了编译，所以我们无法准确估计运行时复杂度。此外，它们的复杂性可能因平台而异。我们可以确定最坏的情况是O(N)。但是，处理器可以一次一个块地连续的内存块( C 中的memcpy())，因此实际结果会更好。

我们只能查看System.arraycopy()的签名：

```java
public static native void arraycopy(Object src, int srcPos, Object dest, int destPos, int length);
```

## 3. Arrays.copyOf()的性能

Arrays.copyOf()在System.arraycopy()实现的功能之上提供了额外的功能。System.arraycopy()只是将值从源数组到目标，而Arrays.copyOf()也会创建新数组。如有必要，它将截断或填充内容。

第二个区别是新数组可以是与源数组不同的类型。如果是这种情况，JVM 将使用反射，这会增加性能开销。

当使用对象数组调用时，copyOf()将调用反射Array.newInstance()方法：

```java
public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
    @SuppressWarnings("unchecked")
    T[] copy = ((Object)newType == (Object)Object[].class) 
      ? (T[]) new Object[newLength]
      : (T[]) Array.newInstance(newType.getComponentType(), newLength);
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
    return copy;
}
```

但是，当以基元作为参数调用时，不需要反射来创建目标数组：

```java
public static int[] copyOf(int[] original, int newLength) {
    int[] copy = new int[newLength];
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
    return copy;
}
```

我们可以清楚地看到，目前Arrays.copyOf()的实现调用了System.arraycopy()。因此，运行时执行应该是相似的。为了证实我们的怀疑，我们将使用基元和对象作为参数对上述方法进行基准测试。

## 4.代码基准

让我们通过实际测试来检查哪种方法更快。为此，我们将使用[JMH](https://www.baeldung.com/java-microbenchmark-harness)(Java Microbenchmark Harness)。我们将创建一个简单的测试，其中我们将使用System.arraycopy()和Arrays.copyOf()将值从一个数组到另一个数组。

我们将创建两个测试类。在一个测试类中，我们将测试基元，在第二个测试类中，我们将测试对象。两种情况下的基准配置都相同。

### 4.1. 基准配置

首先，让我们定义我们的基准参数：

```java
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10)
@Fork(1)
@Measurement(iterations = 100)
```

在这里，我们指定我们只想运行一次基准测试，包括 10 次预热迭代和 100 次测量迭代。此外，我们想计算平均执行时间并以纳秒为单位收集结果。要获得准确的结果，执行至少五次预热迭代非常重要。

### 4.2. 参数设置

我们需要确保我们只测量方法执行所花费的时间，而不是数组创建所花费的时间。为此，我们将在基准设置阶段初始化源数组。用大数字和小数字运行基准测试是个好主意。

在 setup 方法中，我们简单地用随机参数初始化一个数组。首先，我们定义基元的基准设置：

```java
public class PrimitivesCopyBenchmark {

    @Param({ "10", "1000000" })
    public int SIZE;

    int[] src;

    @Setup
    public void setup() {
        Random r = new Random();
        src = new int[SIZE];

        for (int i = 0; i < SIZE; i++) {
            src[i] = r.nextInt();
        }
    }
}

```

对象基准测试遵循相同的设置：

```java
public class ObjectsCopyBenchmark {

    @Param({ "10", "1000000" })
    public int SIZE;
    Integer[] src;

    @Setup
    public void setup() {
        Random r = new Random();
        src = new Integer[SIZE];

        for (int i = 0; i < SIZE; i++) {
            src[i] = r.nextInt();
        }
    }
}
```

### 4.3. 测试

我们定义了两个将执行操作的基准。首先，我们将调用System.arraycopy()：

```java
@Benchmark
public Integer[] systemArrayCopyBenchmark() {
    Integer[] target = new Integer[SIZE];
    System.arraycopy(src, 0, target, 0, SIZE);
    return target;
}
```

为了使这两个测试等效，我们在基准测试中包含了目标数组创建。

其次，我们将测量Arrays.copyOf()的性能：

```java
@Benchmark
public Integer[] arraysCopyOfBenchmark() {
    return Arrays.copyOf(src, SIZE);
}
```

### 4.4. 结果

运行我们的测试后，让我们看看结果：

```java
Benchmark                                          (SIZE)  Mode  Cnt        Score       Error  Units
ObjectsCopyBenchmark.arraysCopyOfBenchmark             10  avgt  100        8.535 ±     0.006  ns/op
ObjectsCopyBenchmark.arraysCopyOfBenchmark        1000000  avgt  100  2831316.981 ± 15956.082  ns/op
ObjectsCopyBenchmark.systemArrayCopyBenchmark          10  avgt  100        9.278 ±     0.005  ns/op
ObjectsCopyBenchmark.systemArrayCopyBenchmark     1000000  avgt  100  2826917.513 ± 15585.400  ns/op
PrimitivesCopyBenchmark.arraysCopyOfBenchmark          10  avgt  100        9.172 ±     0.008  ns/op
PrimitivesCopyBenchmark.arraysCopyOfBenchmark     1000000  avgt  100   476395.127 ±   310.189  ns/op
PrimitivesCopyBenchmark.systemArrayCopyBenchmark       10  avgt  100        8.952 ±     0.004  ns/op
PrimitivesCopyBenchmark.systemArrayCopyBenchmark  1000000  avgt  100   475088.291 ±   726.416  ns/op
```

正如我们所见，System.arraycopy()和Arrays.copyOf()的性能在基元对象和Integer对象的测量误差范围上有所不同。考虑到Arrays.copyOf()在后台使用System.arraycopy()这一事实，这并不奇怪。由于我们使用了两个原始int数组，因此没有进行反射调用。

我们需要记住，JMH只是粗略估计执行时间，机器和 JVM 之间的结果可能不同。

## 5. 内在候选人

值得注意的是，在 HotSpot JVM 16 中，Arrays.copyOf()和System.arraycopy()都被标记为@IntrinsicCandidate。这个注解意味着被注解的方法可以被 HotSpot VM 替换为更快的低级代码。

JIT 编译器可以(对于某些或所有体系结构)将内部方法替换为依赖于机器的、经过极大优化的指令。由于本机方法对于编译器来说是一个黑盒，具有显着的调用开销，因此这两种方法的性能都可以更好。同样，不能保证这样的性能提升。

## 六，总结

在这个例子中，我们研究了System.arraycopy( ) 和Arrays.copyOf( )的性能。首先，我们分析了这两种方法的源代码。其次，我们建立了一个示例基准来衡量他们的平均执行时间。

结果，我们证实了我们的理论，因为Arrays.copyOf()使用System.arraycopy()，所以这两种方法的性能非常相似。