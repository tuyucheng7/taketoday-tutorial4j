## 1. 概述

在本教程中，我们将讨论如何在Java中连接两个[数组](https://www.baeldung.com/java-common-array-operations)。

首先，我们将使用标准JavaAPI 实现我们自己的方法。

然后，我们将看看如何使用常用库解决问题。

## 二、问题简介

快速示例可以清楚地说明问题。

比方说，我们有两个数组：

```java
String[] strArray1 = {"element 1", "element 2", "element 3"};
String[] strArray2 = {"element 4", "element 5"};
```

现在，我们想加入他们并获得一个新数组：

```java
String[] expectedStringArray = {"element 1", "element 2", "element 3", "element 4", "element 5"}
```

此外，我们不希望我们的方法只适用于String数组，因此我们将寻找通用解决方案。

此外，我们不应该忘记原始数组的情况。如果我们的解决方案也适用于原始数组，那就太好了：

```java
int[] intArray1 = { 0, 1, 2, 3 };
int[] intArray2 = { 4, 5, 6, 7 };
int[] expectedIntArray = { 0, 1, 2, 3, 4, 5, 6, 7 };

```

在本教程中，我们将介绍解决问题的不同方法。

## 3. 使用 Java集合

当我们审视这个问题时，可能会想出一个快速的解决方案。

好吧，Java 不提供连接数组的辅助方法。但是，从Java5 开始，Collections实用程序类引入了[addAll(Collection c, T ... elements)](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Collections.html#addAll(java.util.Collection,T...))方法。

我们可以创建一个List对象，然后调用此方法两次将两个数组添加到列表中。最后，我们将结果列表转换回数组：

```java
static <T> T[] concatWithCollection(T[] array1, T[] array2) {
    List<T> resultList = new ArrayList<>(array1.length + array2.length);
    Collections.addAll(resultList, array1);
    Collections.addAll(resultList, array2);

    @SuppressWarnings("unchecked")
    //the type cast is safe as the array1 has the type T[]
    T[] resultArray = (T[]) Array.newInstance(array1.getClass().getComponentType(), 0);
    return resultList.toArray(resultArray);
}

```

在上面的方法中，我们使用Java反射 API 创建[了一个通用数组](https://www.baeldung.com/java-generic-array)实例：resultArray。

让我们编写一个测试来验证我们的方法是否有效：

```java
@Test
public void givenTwoStringArrays_whenConcatWithList_thenGetExpectedResult() {
    String[] result = ArrayConcatUtil.concatWithCollection(strArray1, strArray2);
    assertThat(result).isEqualTo(expectedStringArray);
}

```

如果我们执行测试，它就会通过。

这种方法非常简单。但是，由于该方法接受 T[]数组，因此它不支持连接原始数组。

除此之外，它的效率很低，因为它创建了一个ArrayList对象，稍后我们调用toArray()方法将其转换回 array。在此过程中，Java List对象增加了不必要的开销。

接下来，让我们看看能不能找到更高效的方法来解决这个问题。

## 4. 使用数组技术

Java 不提供数组连接方法，但它提供了两种数组方法：[System.arraycopy()](https://www.baeldung.com/java-array-copy#the-system-class)和[Arrays.copyOf()](https://www.baeldung.com/java-array-copy#the-arrays-class)。

我们可以使用Java的数组方式来解决这个问题。

这个想法是，我们创建一个新数组，比如result，它有result。length = array1.length + array2.length，并将每个数组的元素到结果数组。

### 4.1. 非原始数组

首先，让我们看一下方法实现：

```java
static <T> T[] concatWithArrayCopy(T[] array1, T[] array2) {
    T[] result = Arrays.copyOf(array1, array1.length + array2.length);
    System.arraycopy(array2, 0, result, array1.length, array2.length);
    return result;
}

```

该方法看起来很紧凑。此外，整个方法只创建了一个新的数组对象：result。

现在，让我们编写一个测试方法来检查它是否按我们预期的那样工作：

```java
@Test
public void givenTwoStringArrays_whenConcatWithCopy_thenGetExpectedResult() {
    String[] result = ArrayConcatUtil.concatWithArrayCopy(strArray1, strArray2);
    assertThat(result).isEqualTo(expectedStringArray);
}

```

如果我们试一试，测试就会通过。

没有不必要的对象创建。因此，此方法比使用JavaCollections 的方法性能更高。

另一方面，这个泛型方法只接受T[]类型的参数。因此，我们不能将原始数组传递给该方法。

但是，我们可以修改该方法，使其支持原始数组。

接下来，让我们仔细看看如何添加原始数组支持。

### 4.2. 添加原始数组支持

为了使该方法支持原始数组，我们需要将参数的类型从T[]更改为T并进行一些类型安全检查。

首先我们看一下修改后的方法：

```java
static <T> T concatWithCopy2(T array1, T array2) {
    if (!array1.getClass().isArray() || !array2.getClass().isArray()) {
        throw new IllegalArgumentException("Only arrays are accepted.");
    }

    Class<?> compType1 = array1.getClass().getComponentType();
    Class<?> compType2 = array2.getClass().getComponentType();

    if (!compType1.equals(compType2)) {
        throw new IllegalArgumentException("Two arrays have different types.");
    }

    int len1 = Array.getLength(array1);
    int len2 = Array.getLength(array2);

    @SuppressWarnings("unchecked")
    //the cast is safe due to the previous checks
    T result = (T) Array.newInstance(compType1, len1 + len2);

    System.arraycopy(array1, 0, result, 0, len1);
    System.arraycopy(array2, 0, result, len1, len2);

    return result;
}

```

显然，concatWithCopy2()方法比原始版本更长。不过也不难理解。现在，让我们快速浏览一下以了解其工作原理。

由于该方法现在允许使用T类型的参数，因此我们需要确保两个参数都是数组：

```java
if (!array1.getClass().isArray() || !array2.getClass().isArray()) {
    throw new IllegalArgumentException("Only arrays are accepted.");
}
```

如果两个参数是数组，它仍然不够安全。例如，我们不想连接一个Integer[]数组和一个String[]数组。因此，我们需要确保两个数组的ComponentType相同：

```java
if (!compType1.equals(compType2)) {
    throw new IllegalArgumentException("Two arrays have different types.");
}
```

在类型安全检查之后，我们可以使用ConponentType对象创建一个通用数组实例，并将参数数组到结果数组。它与之前的concatWithCopy()方法非常相似。

### 4.3. 测试concatWithCopy2()方法

接下来，让我们测试一下我们的新方法是否按预期工作。首先，我们传递两个非数组对象并查看该方法是否引发了预期的异常：

```java
@Test
public void givenTwoStrings_whenConcatWithCopy2_thenGetException() {
    String exMsg = "Only arrays are accepted.";
    try {
        ArrayConcatUtil.concatWithCopy2("String Nr. 1", "String Nr. 2");
        fail(String.format("IllegalArgumentException with message:'%s' should be thrown. But it didn't", exMsg));
    } catch (IllegalArgumentException e) {
        assertThat(e).hasMessage(exMsg);
    }
}

```

在上面的测试中，我们将两个String对象传递给该方法。如果我们执行测试，它就会通过。这意味着我们得到了预期的异常。

最后，让我们构建一个测试来检查新方法是否可以连接原始数组：

```java
@Test
public void givenTwoArrays_whenConcatWithCopy2_thenGetExpectedResult() {
    String[] result = ArrayConcatUtil.concatWithCopy2(strArray1, strArray2);
    assertThat(result).isEqualTo(expectedStringArray);

    int[] intResult = ArrayConcatUtil.concatWithCopy2(intArray1, intArray2);
    assertThat(intResult).isEqualTo(expectedIntArray);
}

```

这一次，我们调用了两次concatWithCopy2()方法。首先，我们传递两个String[]数组。然后，我们传递两个 int[]原始数组。

如果我们运行它，测试将通过。现在，我们可以说，concatWithCopy2()方法按我们预期的方式工作。

## 5. 使用JavaStream API

如果我们使用的Java版本是 8 或更高版本，则可以使用[Stream API 。](https://www.baeldung.com/java-8-streams)我们也可以使用 Stream API 来解决这个问题。

首先，我们可以通过[Arrays.stream()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#stream(T[]))方法从数组中获取Stream。此外，Stream类提供了一个静态[concat()](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/Stream.html#concat(java.util.stream.Stream,java.util.stream.Stream))方法来连接两个Stream对象。

现在，让我们看看如何使用Stream 连接两个数组。

### 5.1. 连接非原始数组

使用JavaStreams 构建通用解决方案非常简单：

```java
static <T> T[] concatWithStream(T[] array1, T[] array2) {
    return Stream.concat(Arrays.stream(array1), Arrays.stream(array2))
      .toArray(size -> (T[]) Array.newInstance(array1.getClass().getComponentType(), size));
}

```

首先，我们将两个输入数组转换为Stream对象。其次，我们使用Stream.concat()方法连接两个 Stream对象。

最后，我们返回一个包含串联 Stream 中所有元素的数组。

接下来，让我们构建一个简单的测试方法来检查解决方案是否有效：

```java
@Test
public void givenTwoStringArrays_whenConcatWithStream_thenGetExpectedResult() {
    String[] result = ArrayConcatUtil.concatWithStream(strArray1, strArray2);
    assertThat(result).isEqualTo(expectedStringArray);
}

```

如果我们传递两个String[]数组，测试将通过 。

可能，我们已经注意到我们的泛型方法接受 T[]类型的参数。因此，它不适用于原始数组。

接下来，让我们看看如何使用JavaStreams 连接两个原始数组。

### 5.2. 连接原始数组

Stream API 提供了不同的Stream类，可以将 Stream 对象转换为相应的原始数组，例如[IntStream、](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/IntStream.html) [LongStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/LongStream.html)和[DoubleStream](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/stream/DoubleStream.html)。

但是，只有int、long和double有它们的 Stream类型。也就是说，如果我们要连接的原始数组的类型为int[]、long[]或double[]，我们可以选择正确的Stream类并调用 concat()方法。

让我们看一个使用IntStream连接两个int[]数组的示例：

```java
static int[] concatIntArraysWithIntStream(int[] array1, int[] array2) {
    return IntStream.concat(Arrays.stream(array1), Arrays.stream(array2)).toArray();
}

```

如上面的方法所示，Arrays.stream(int[]) 方法将返回一个IntStream对象。

此外， IntStream.toArray()方法返回 int[]。因此，我们不需要处理类型转换。

像往常一样，让我们创建一个测试，看看它是否适用于我们的int[]输入数据：

```java
@Test
public void givenTwoIntArrays_whenConcatWithIntStream_thenGetExpectedResult() {
    int[] intResult = ArrayConcatUtil.concatIntArraysWithIntStream(intArray1, intArray2);
    assertThat(intResult).isEqualTo(expectedIntArray);
}

```

如果我们运行测试，它就会通过。

## 6. 使用 Apache Commons Lang 库

Apache [Commons Lang](https://www.baeldung.com/java-commons-lang-3)库广泛用于现实世界中的Java应用程序。

它附带一个[ArrayUtils](https://www.baeldung.com/array-processing-commons-lang)类，其中包含许多方便的数组辅助方法。

ArrayUtils类提供了一系列[addAll()](https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/ArrayUtils.html#add-T:A-T-)方法，支持连接非原始数组和原始数组。

我们通过一个测试方法来验证一下：

```java
@Test
public void givenTwoArrays_whenConcatWithCommonsLang_thenGetExpectedResult() {
    String[] result = ArrayUtils.addAll(strArray1, strArray2);
    assertThat(result).isEqualTo(expectedStringArray);

    int[] intResult = ArrayUtils.addAll(intArray1, intArray2);
    assertThat(intResult).isEqualTo(expectedIntArray);
}

```

在内部，ArrayUtils.addAll()方法使用高性能的System.arraycopy()方法进行数组连接。

## 7. 使用 Guava 库

与 Apache Commons 库类似，[Guava](https://www.baeldung.com/guava-guide)是另一个受到许多开发人员喜爱的库。

Guava 还提供了方便的辅助类来进行数组连接。

如果我们想连接非原始数组， ObjectArrays.concat [()](https://guava.dev/releases/19.0/api/docs/com/google/common/collect/ObjectArrays.html#concat(T[], T[], java.lang.Class))方法是一个不错的选择：

```java
@Test
public void givenTwoStringArrays_whenConcatWithGuava_thenGetExpectedResult() {
    String[] result = ObjectArrays.concat(strArray1, strArray2, String.class);
    assertThat(result).isEqualTo(expectedStringArray);
}

```

Guava 为每个原语提供了[原语实用程序](https://github.com/google/guava/wiki/PrimitivesExplained)。所有原始实用程序都提供 一个 concat() 方法来将数组与相应的类型连接起来，例如：

-   int[] – Guava: Ints.concat(int[] … arrays)
-   long[] ——番石榴：Longs.concat(long[] …数组)
-   byte[] – Guava: Bytes.concat(byte[] … arrays)
-   双[] ——番石榴:双打.concat(双[]…数组)

我们可以选择正确的原始实用程序类来连接原始数组。

接下来，让我们使用Ints.concat()方法连接我们的两个int[]数组：

```java
@Test
public void givenTwoIntArrays_whenConcatWithGuava_thenGetExpectedResult() {
    int[] intResult = Ints.concat(intArray1, intArray2);
    assertThat(intResult).isEqualTo(expectedIntArray);
}
```

同样，Guava在上述方法内部使用System.arraycopy()进行数组拼接，以获得良好的性能。

## 八、总结

在本文中，我们通过示例介绍了在Java中连接两个数组的不同方法。