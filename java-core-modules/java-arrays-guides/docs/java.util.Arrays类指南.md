## 1. 概述

在本教程中，我们将了解java.util.Arrays，这是一个实用程序类，自Java1.2 以来一直是Java的一部分。

使用 数组， 我们可以创建、比较、排序、搜索、流式传输和转换数组。

## 2. 创造

让我们看一下创建数组的一些方法： copyOf、copyOfRange和fill。

### 2.1. copyOf和 copyOfRange

要使用copyOfRange，我们需要我们的原始数组和我们要的开始索引(包括)和结束索引(不包括)：

```java
String[] intro = new String[] { "once", "upon", "a", "time" };
String[] abridgement = Arrays.copyOfRange(storyIntro, 0, 3); 

assertArrayEquals(new String[] { "once", "upon", "a" }, abridgement); 
assertFalse(Arrays.equals(intro, abridgement));
```

为了使用 copyOf，我们将引入一个 目标数组大小，然后我们将返回一个该长度的新数组：

```java
String[] revised = Arrays.copyOf(intro, 3);
String[] expanded = Arrays.copyOf(intro, 5);

assertArrayEquals(Arrays.copyOfRange(intro, 0, 3), revised);
assertNull(expanded[4]);
```

请注意，如果我们的目标大小大于原始大小，copyOf 会用null填充数组 。

### 2.2. 充满

另一种方法，我们可以创建一个固定长度的数组，是填充， 当我们想要一个所有元素都相同的数组时，这很有用：

```java
String[] stutter = new String[3];
Arrays.fill(stutter, "once");

assertTrue(Stream.of(stutter)
  .allMatch(el -> "once".equals(el));
```

查看 setAll 以创建一个元素不同的数组。

请注意，我们需要事先自己实例化数组，而不是像 String[] filled = Arrays.fill(“once” , 3) ; ——因为这个特性是在泛型在语言中可用之前引入的。

## 3.比较

现在让我们切换到比较数组的方法。

### 3.1. 等于和 deepEquals

我们可以使用 equals 按大小和内容进行简单的数组比较。如果我们添加一个 null 作为元素之一，内容检查将失败：

```java
assertTrue(
  Arrays.equals(new String[] { "once", "upon", "a", "time" }, intro));
assertFalse(
  Arrays.equals(new String[] { "once", "upon", "a", null }, intro));
```

当我们有嵌套或多维数组时，我们可以使用deepEquals不仅检查顶级元素，而且递归地执行检查：

```java
Object[] story = new Object[] 
  { intro, new String[] { "chapter one", "chapter two" }, end };
Object[] copy = new Object[] 
  { intro, new String[] { "chapter one", "chapter two" }, end };

assertTrue(Arrays.deepEquals(story, copy));
assertFalse(Arrays.equals(story, copy));
```

请注意deepE quals如何 通过但 equals 失败。

这是因为deepEquals 最终会在每次遇到 array 时调用自己，而 equals 只会比较子数组的引用。

此外，这使得调用带有自引用的数组变得危险！

### 3.2. hashCode和 deepHashCode

hashCode的实现 将为我们提供推荐用于Java对象的equals / hashCode契约 的另一部分 。我们使用hashCode根据数组的内容计算一个整数：

```java
Object[] looping = new Object[]{ intro, intro }; 
int hashBefore = Arrays.hashCode(looping);
int deepHashBefore = Arrays.deepHashCode(looping);
```

现在，我们将原始数组的一个元素设置为 null 并重新计算哈希值：

```java
intro[3] = null;
int hashAfter = Arrays.hashCode(looping);

```

或者， deepHashCode检查嵌套数组以匹配元素和内容的数量。如果我们用 deepHashCode重新计算：

```java
int deepHashAfter = Arrays.deepHashCode(looping);
```

现在，我们可以看到两种方法的区别：

```java
assertEquals(hashAfter, hashBefore);
assertNotEquals(deepHashAfter, deepHashBefore);

```

deepHashCode是我们在处理数组上的HashMap 和 HashSet 等数据结构时使用的底层计算 。

## 4.排序和搜索

接下来，让我们看一下排序和搜索数组。

### 4.1. 种类

如果我们的元素是原始元素或者它们实现了Comparable，我们可以使用 sort 来执行内联排序：

```java
String[] sorted = Arrays.copyOf(intro, 4);
Arrays.sort(sorted);

assertArrayEquals(
  new String[]{ "a", "once", "time", "upon" }, 
  sorted);
```

注意sort会改变原始引用，这就是我们在这里执行的原因。

sort 将对不同的数组元素类型使用不同的算法。[原始类型使用双枢轴快速排序](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#sort(byte[]))，[对象类型使用 Timsort](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html#sort(java.lang.Object[]))。对于随机排序的数组，两者都具有O(n log(n)) 的平均情况 。

从Java8 开始， parallelSort 可用于并行排序合并。它提供了一种使用多个Arrays.sort 任务的并发排序方法 。

### 4.2. 二分查找

在未排序的数组中搜索是线性的，但如果我们有一个排序的数组，那么我们可以在O(log n)中进行搜索，这就是我们可以使用 binarySearch 进行的操作：

```java
int exact = Arrays.binarySearch(sorted, "time");
int caseInsensitive = Arrays.binarySearch(sorted, "TiMe", String::compareToIgnoreCase);

assertEquals("time", sorted[exact]);
assertEquals(2, exact);
assertEquals(exact, caseInsensitive);
```

如果我们不提供 Comparator作为第三个参数，则 binarySearch会依赖我们的元素类型为 Comparable类型。

再次注意，如果我们的数组没有首先排序，那么 binarySearch 将不会像我们预期的那样工作！

## 5.流媒体

正如我们之前看到的， Arrays 在Java8 中进行了更新，包括使用 Stream API 的方法，例如parallelSort(上面提到的)、 stream 和 setAll。

### 5.1. 溪流

stream使我们能够完全访问数组的 Stream API：

```java
Assert.assertEquals(Arrays.stream(intro).count(), 4);

exception.expect(ArrayIndexOutOfBoundsException.class);
Arrays.stream(intro, 2, 1).count();
```

我们可以为流提供包容性和排他性索引，但是 如果索引乱序、负数或超出范围，我们应该期待一个ArrayIndexOutOfBoundsException 。

## 6. 转型

最后， toString、 asList 和 setAll 为我们提供了几种不同的方法来转换数组。

### 6.1. toString和 deepToString

我们可以获得原始数组的可读版本的一个好方法是使用 toString：

```java
assertEquals("[once, upon, a, time]", Arrays.toString(storyIntro));

```

我们必须再次使用深层版本来打印嵌套数组的内容：

```java
assertEquals(
  "[[once, upon, a, time], [chapter one, chapter two], [the, end]]",
  Arrays.deepToString(story));
```

### 6.2. 作为列表

在所有Arrays方法中，我们使用起来最方便的是 asList 。 我们有一种简单的方法可以将数组转换为列表：

```java
List<String> rets = Arrays.asList(storyIntro);

assertTrue(rets.contains("upon"));
assertTrue(rets.contains("time"));
assertEquals(rets.size(), 4);
```

但是，返回的 List将是固定长度的，因此我们将无法添加或删除元素。

还要注意，奇怪的是， java.util.Arrays有它自己的 ArrayList子类，它 asList 返回。这在调试时非常具有欺骗性！

### 6.3. 全部设置

使用setAll，我们可以使用函数式接口设置数组的所有元素。生成器实现将位置索引作为参数：

```java
String[] longAgo = new String[4];
Arrays.setAll(longAgo, i -> this.getWord(i)); 
assertArrayEquals(longAgo, new String[]{"a","long","time","ago"});
```

当然，异常处理是使用 lambda 的比较危险的部分之一。所以请记住，如果 lambda 抛出异常，那么Java不会定义数组的最终状态。

## 7. 并列前缀

自Java8 以来Arrays中引入的另一个新方法 是parallelPrefix。使用parallelPrefix，我们可以以累积方式对输入数组的每个元素进行操作。

### 7.1. 并行前缀

如果运算符执行如下示例中的加法，[1, 2, 3, 4]将导致[1, 3, 6, 10]：

```java
int[] arr = new int[] { 1, 2, 3, 4};
Arrays.parallelPrefix(arr, (left, right) -> left + right);
assertThat(arr, is(new int[] { 1, 3, 6, 10}));
```

此外，我们可以为操作指定一个子范围：

```java
int[] arri = new int[] { 1, 2, 3, 4, 5 };
Arrays.parallelPrefix(arri, 1, 4, (left, right) -> left + right);
assertThat(arri, is(new int[] { 1, 2, 5, 9, 5 }));
```

请注意，该方法是并行执行的，因此累积操作应该是无副作用和[关联的](https://en.wikipedia.org/wiki/Associative_property)。

对于非关联函数：

```java
int nonassociativeFunc(int left, int right) {
    return left + rightleft;
}
```

使用parallelPrefix会产生不一致的结果：

```java
@Test
public void whenPrefixNonAssociative_thenError() {
    boolean consistent = true;
    Random r = new Random();
    for (int k = 0; k < 100_000; k++) {
        int[] arrA = r.ints(100, 1, 5).toArray();
        int[] arrB = Arrays.copyOf(arrA, arrA.length);

        Arrays.parallelPrefix(arrA, this::nonassociativeFunc);

        for (int i = 1; i < arrB.length; i++) {
            arrB[i] = nonassociativeFunc(arrB[i - 1], arrB[i]);
        }

        consistent = Arrays.equals(arrA, arrB);
        if(!consistent) break;
    }
    assertFalse(consistent);
}
```

### 7.2. 表现

并行前缀计算通常比顺序循环更有效，尤其是对于大型数组。[在带有JMH](https://www.baeldung.com/java-microbenchmark-harness)的 Intel Xeon 机器(6 核)上运行微基准测试时，我们可以看到很大的性能改进：

```plaintext
Benchmark                      Mode        Cnt       Score   Error        Units
largeArrayLoopSum             thrpt         5        9.428 ± 0.075        ops/s
largeArrayParallelPrefixSum   thrpt         5       15.235 ± 0.075        ops/s

Benchmark                     Mode         Cnt       Score   Error        Units
largeArrayLoopSum             avgt          5      105.825 ± 0.846        ops/s
largeArrayParallelPrefixSum   avgt          5       65.676 ± 0.828        ops/s
```

这是基准代码：

```java
@Benchmark
public void largeArrayLoopSum(BigArray bigArray, Blackhole blackhole) {
  for (int i = 0; i < ARRAY_SIZE - 1; i++) {
    bigArray.data[i + 1] += bigArray.data[i];
  }
  blackhole.consume(bigArray.data);
}

@Benchmark
public void largeArrayParallelPrefixSum(BigArray bigArray, Blackhole blackhole) {
  Arrays.parallelPrefix(bigArray.data, (left, right) -> left + right);
  blackhole.consume(bigArray.data);
}
```

## 七、总结

在本文中，我们了解了如何使用java.util.Arrays类创建、搜索、排序和转换数组的一些方法。

[此类在最近的Java版本中得到了扩展，在Java 8](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html)中包含了流生成和使用方法，在[Java 9](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/Arrays.html)中包含了不匹配方法。