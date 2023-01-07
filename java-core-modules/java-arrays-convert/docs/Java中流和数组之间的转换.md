## 1. 概述

需要将各种[动态数据结构](https://www.baeldung.com/convert-array-to-list-and-list-to-array)[转换](https://www.baeldung.com/convert-map-values-to-array-list-set)为[数组](https://www.baeldung.com/convert-array-to-set-and-set-to-array)是很常见的。

在本教程中，我们将演示如何在Java中将[Stream](https://www.baeldung.com/java-8-streams-introduction)转换为数组，反之亦然。

## 2. 将流转换为数组

### 2.1. 方法参考

将Stream转换为数组的最佳方法是使用Stream 的toArray ()方法：

```java
public String[] usingMethodReference(Stream<String> stringStream) {
    return stringStream.toArray(String[]::new);
}
```

现在，我们可以轻松测试转换是否成功：

```java
Stream<String> stringStream = Stream.of("baeldung", "convert", "to", "string", "array");
assertArrayEquals(new String[] { "baeldung", "convert", "to", "string", "array" },
    usingMethodReference(stringStream));
```

### 2.2. 拉姆达表达式

另一个等效项是将lambda 表达式传递给toArray () 方法：

```java
public static String[] usingLambda(Stream<String> stringStream) {
    return stringStream.toArray(size -> new String[size]);
}
```

这将为我们提供与使用方法引用相同的结果。

### 2.3. 自定义类

或者，我们可以全力以赴创建一个成熟的班级。

[正如我们从Stream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html#toArray(java.util.function.IntFunction))[文档中](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html#toArray(java.util.function.IntFunction))看到的那样，它采用IntFunction作为参数。它将数组大小作为输入并返回该大小的数组。

当然， IntFunction 是一个接口，所以我们可以实现它：

```java
class MyArrayFunction implements IntFunction<String[]> {
    @Override
    public String[] apply(int size) {
        return new String[size];
    }
};
```

然后我们可以正常构造和使用：

```java
public String[] usingCustomClass(Stream<String> stringStream) {
    return stringStream.toArray(new MyArrayFunction());
}
```

因此，我们可以做出与之前相同的断言。

### 2.4. 原始数组

在前面的部分中，我们探讨了如何将String Stream转换为String数组。事实上，我们可以对任何对象执行这种转换，它看起来与上面的字符串示例非常相似。

不过，对于原语来说有点不同。例如，如果我们有一个要转换为int[]的Integer流，我们首先需要调用mapToInt()方法：

```java
public int[] intStreamToPrimitiveIntArray(Stream<Integer> integerStream) {
    return integerStream.mapToInt(i -> i).toArray();
}
```

我们还可以使用mapToLong()和mapToDouble()方法。另外，请注意这次我们没有向toArray()传递任何参数。

最后，让我们做相等断言并确认我们已经正确地得到了我们的int数组：

```java
Stream<Integer> integerStream = IntStream.rangeClosed(1, 7).boxed();
assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7}, intStreamToPrimitiveIntArray(integerStream));
```

但是，如果我们需要做相反的事情怎么办？让我们来看看。

## 3. 将数组转换为流

当然，我们也可以走另一条路。Java 有一些专用的方法。

### 3.1. 对象数组_

我们可以使用Arrays.stream()或Stream.of()方法将数组转换为Stream：

```java
public Stream<String> stringArrayToStreamUsingArraysStream(String[] stringArray) {
    return Arrays.stream(stringArray);
}

public Stream<String> stringArrayToStreamUsingStreamOf(String[] stringArray) {
    return Stream.of(stringArray);
}
```

我们应该注意，在这两种情况下，我们的Stream与我们的数组同时存在。

### 3.2. 基元数组

同样，我们可以转换原始数组：

```java
public IntStream primitiveIntArrayToStreamUsingArraysStream(int[] intArray) {
    return Arrays.stream(intArray);
}

public Stream<int[]> primitiveIntArrayToStreamUsingStreamOf(int[] intArray) {
    return Stream.of(intArray);
}
```

但是，与转换Object的数组相比，有一个重要的区别。转换基元数组时，Arrays.stream()返回IntStream，而Stream.of()返回Stream<int[]>。

### 3.3. Arrays.stream与Stream.of

为了理解前面几节中提到的差异，我们将看一下相应方法的实现。

我们先来看看Java对这两个方法的实现：

```java
public <T> Stream<T> stream(T[] array) {
    return stream(array, 0, array.length);
}

public <T> Stream<T> of(T... values) {
    return Arrays.stream(values);
}
```

我们可以看到Stream.of()实际上是在内部调用Arrays.stream()，这显然是我们得到相同结果的原因。

现在，我们将检查在我们想要转换基元数组的情况下的方法：

```java
public IntStream stream(int[] array) {
    return stream(array, 0, array.length);
}

public <T> Stream<T> of(T t) {
    return StreamSupport.stream(new Streams.StreamBuilderImpl<>(t), false);
}
```

这一次，Stream.of()没有调用Arrays.stream()。

## 4。总结

在本文中，我们了解了如何在Java中将Stream转换为数组，反之亦然。我们还解释了为什么在转换Object数组和使用基元数组时会得到不同的结果。