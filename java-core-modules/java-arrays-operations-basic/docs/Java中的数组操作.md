## 1. 概述

任何Java开发人员都知道，在处理数组操作时生成一个干净、高效的解决方案并不总是容易实现。尽管如此，它们仍然是Java生态系统中的核心部分——我们将不得不多次处理它们。

出于这个原因，最好有一张“备忘单”——最常见程序的摘要，以帮助我们快速解决难题。本教程将在这些情况下派上用场。

## 2. 数组和辅助类

在继续之前，了解什么是Java中的数组以及如何使用它是很有用的。如果这是你第一次在Java中使用它，我们建议你查看 [之前的这篇文章](https://www.baeldung.com/java-arrays-guide) ，其中介绍了所有基本概念。

请注意，数组支持的基本操作在某种程度上是有限的。当涉及到数组时，执行相对简单任务的复杂算法并不少见。

为此，对于我们的大部分操作，我们将使用辅助类和方法来协助我们：Java提供的[Arrays类和 Apache 的](https://www.baeldung.com/java-util-arrays)[ArrayUtils](https://www.baeldung.com/array-processing-commons-lang) 类。

要将后者包含在我们的项目中，我们必须添加 [Apache Commons](https://commons.apache.org/)依赖项：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

[我们可以在 Maven Central 上](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")查看这个工件的最新版本。

## 3. 获取数组的第一个和最后一个元素

由于数组的按索引访问的特性，这是最常见和最简单的任务之一。

让我们首先声明并初始化一个将在所有示例中使用的int数组(除非我们另有说明)：

```java
int[] array = new int[] { 3, 5, 2, 5, 14, 4 };
```

知道数组的第一项与索引值 0 相关联并且它具有我们可以使用的长度属性，那么很容易弄清楚我们如何获得这两个元素：

```java
int firstItem = array[0];
int lastItem = array[array.length - 1];
```

## 4. 从数组中获取随机值

通过使用java.util.Random对象，我们可以轻松地从数组中获取任何值：

```java
int anyValue = array[new Random().nextInt(array.length)];
```

## 5. 向数组追加一个新项

正如我们所知，数组包含固定大小的值。因此，我们不能只添加一个项目就超过这个限制。

我们需要先声明一个更大的新数组，然后将基本数组的元素到第二个数组。

幸运的是，Arrays类提供了一种方便的方法来将数组的值到新的不同大小的结构中：

```java
int[] newArray = Arrays.copyOf(array, array.length + 1);
newArray[newArray.length - 1] = newItem;
```

或者，如果ArrayUtils类在我们的项目中可以访问，我们可以使用它的add 方法 (或其addAll替代方法)在一行语句中实现我们的目标：

```java
int[] newArray = ArrayUtils.add(array, newItem);
```

可以想象，这个方法并没有修改原来的数组对象；我们必须将其输出分配给一个新变量。

## 6. 在两个值之间插入一个值

由于其索引值字符，在数组中的两个项目之间插入一个项目并非易事。

Apache 认为这是一个典型的场景，并在其ArrayUtils类中实现了一个方法来简化解决方案：

```java
int[] largerArray = ArrayUtils.insert(2, array, 77);
```

我们必须指定要插入值的索引，输出将是一个包含更多元素的新数组。

最后一个参数是可变参数(又名vararg)，因此我们可以在数组中插入任意数量的项目。

## 7.比较两个数组

尽管数组是Object并因此提供了equals方法，但它们使用它的默认实现，仅依赖于引用相等性。

我们无论如何都可以调用java.util.Arrays的equals方法来检查两个数组对象是否包含相同的值：

```java
boolean areEqual = Arrays.equals(array1, array2);
```

[注意：此方法对锯齿状数组](https://www.baeldung.com/java-jagged-arrays)无效。验证多维结构相等性的适当方法是Arrays.deepEquals方法。

## 8. 检查数组是否为空

这是一个简单的赋值，因为我们可以使用 数组的长度属性：

```java
boolean isEmpty = array == null || array.length == 0;
```

此外，我们还可以使用ArrayUtils帮助程序类中的空安全方法：

```java
boolean isEmpty = ArrayUtils.isEmpty(array);
```

此函数仍然取决于数据结构的长度，它也将空值和空子数组视为有效值，因此我们必须关注这些边缘情况：

```java
// These are empty arrays
Integer[] array1 = {};
Integer[] array2 = null;
Integer[] array3 = new Integer[0];

// All these will NOT be considered empty
Integer[] array3 = { null, null, null };
Integer[][] array4 = { {}, {}, {} };
Integer[] array5 = new Integer[3];
```

## 9. 如何打乱数组的元素

为了打乱数组中的项目，我们可以使用 ArrayUtil的功能：

```java
ArrayUtils.shuffle(array);
```

这是一个void方法，对数组的实际值进行操作。

## 10. 装箱和拆箱阵列

我们经常遇到仅支持基于对象的数组的方法。

ArrayUtils辅助类再次派上用场，以获取原始数组的盒装版本：

```java
Integer[] list = ArrayUtils.toObject(array);
```

逆运算也是可能的：

```java
Integer[] objectArray = { 3, 5, 2, 5, 14, 4 };
int[] array = ArrayUtils.toPrimitive(objectArray);
```

## 11. 从数组中删除重复项

删除重复项的最简单方法是将数组转换为Set实现。

正如我们所知，Collection使用泛型，因此不支持原始类型。

出于这个原因，如果我们不像我们的例子那样处理基于对象的数组，我们首先需要装箱我们的值：

```java
// Box
Integer[] list = ArrayUtils.toObject(array);
// Remove duplicates
Set<Integer> set = new HashSet<Integer>(Arrays.asList(list));
// Create array and unbox
return ArrayUtils.toPrimitive(set.toArray(new Integer[set.size()]));
```

注意：我们 也可以使用[其他技术在数组和Set对象之间进行转换。](https://www.baeldung.com/convert-array-to-set-and-set-to-array)

此外，如果我们需要保留元素的顺序，我们必须使用不同的Set实现，例如 LinkedHashSet。

## 12. 如何打印数组

与equals方法一样，数组的toString函数使用Object类提供的默认实现，这不是很有用。

Arrays和ArrayUtils 类都附带了它们的实现，以将数据结构转换为可读的String。

除了它们使用的格式略有不同外，最重要的区别是它们如何处理多维对象。

Java Util 的类提供了两个我们可以使用的静态方法：

-   toString：不适用于锯齿状数组
-   deepToString：支持任何基于Object的数组，但不使用原始数组参数进行编译

另一方面，Apache 的实现提供了一个在任何情况下都能正常工作的toString方法：

```java
String arrayAsString = ArrayUtils.toString(array);
```

## 13. 将数组映射到另一种类型

对所有数组项应用操作通常很有用，可能会将它们转换为另一种类型的对象。

考虑到这个目标，我们将尝试使用泛型创建一个灵活的辅助方法：

```java
public static <T, U> U[] mapObjectArray(
  T[] array, Function<T, U> function,
  Class<U> targetClazz) {
    U[] newArray = (U[]) Array.newInstance(targetClazz, array.length);
    for (int i = 0; i < array.length; i++) {
        newArray[i] = function.apply(array[i]);
    }
    return newArray;
}
```

如果我们不在我们的项目中使用Java8，我们可以丢弃 Function参数，并为我们需要执行的每个映射创建一个方法。

我们现在可以为不同的操作重用我们的通用方法。让我们创建两个测试用例来说明这一点：

```java
@Test
public void whenMapArrayMultiplyingValues_thenReturnMultipliedArray() {
    Integer[] multipliedExpectedArray = new Integer[] { 6, 10, 4, 10, 28, 8 };
    Integer[] output = 
      MyHelperClass.mapObjectArray(array, value -> value  2, Integer.class);

    assertThat(output).containsExactly(multipliedExpectedArray);
}

@Test
public void whenMapDividingObjectArray_thenReturnMultipliedArray() {
    Double[] multipliedExpectedArray = new Double[] { 1.5, 2.5, 1.0, 2.5, 7.0, 2.0 };
    Double[] output =
      MyHelperClass.mapObjectArray(array, value -> value / 2.0, Double.class);

    assertThat(output).containsExactly(multipliedExpectedArray);
}
```

对于原始类型，我们必须先将我们的值装箱。

作为替代方案，我们可以求助于[Java 8 的 Streams](https://www.baeldung.com/java-8-streams-introduction) 来为我们执行映射。

我们需要先将数组转换 为对象流。我们可以使用Arrays.stream方法来做到这一点。

例如，如果我们想将我们的int值映射到一个自定义的String表示，我们将实现这个：

```java
String[] stringArray = Arrays.stream(array)
  .mapToObj(value -> String.format("Value: %s", value))
  .toArray(String[]::new);
```

## 14.过滤数组中的值

从集合中过滤出值是一项常见任务，我们可能不得不在不止一次的情况下执行。

这是因为在我们创建将接收值的数组时，我们无法确定其最终大小。因此，我们将再次依赖Stream的方法。

假设我们要从数组中删除所有奇数：

```java
int[] evenArray = Arrays.stream(array)
  .filter(value -> value % 2 == 0)
  .toArray();
```

## 15.其他常见的数组操作

当然，我们可能需要执行许多其他数组操作。

除了本教程中显示的那些之外，我们还在专门的帖子中广泛介绍了其他操作：

-   [检查Java数组是否包含值](https://www.baeldung.com/java-array-contains-value)
-   [如何在Java中数组](https://www.baeldung.com/java-array-copy)
-   [删除数组的第一个元素](https://www.baeldung.com/java-array-remove-first-element)
-   [使用Java查找数组中的最小值和最大值](https://www.baeldung.com/java-array-min-max)
-   [在Java数组中查找总和和平均值](https://www.baeldung.com/java-array-sum-average)
-   [如何在Java中反转数组](https://www.baeldung.com/java-invert-array)
-   [在Java中连接和拆分数组和集合](https://www.baeldung.com/java-join-and-split)
-   [在Java中组合不同类型的集合](https://www.baeldung.com/java-combine-collections)
-   [查找数组中加起来等于给定总和的所有数字对](https://www.baeldung.com/java-algorithm-number-pairs-sum)
-   [在Java中排序](https://www.baeldung.com/java-sorting)
-   [Java 中高效的词频计算器](https://www.baeldung.com/java-word-frequency)
-   [Java 中的插入排序](https://www.baeldung.com/java-insertion-sort)

## 16.总结

数组是Java的核心功能之一，因此了解它们的工作原理以及我们能用它们做什么和不能用它们做什么非常重要。

在本教程中，我们了解了如何在常见场景中适当地处理数组操作。