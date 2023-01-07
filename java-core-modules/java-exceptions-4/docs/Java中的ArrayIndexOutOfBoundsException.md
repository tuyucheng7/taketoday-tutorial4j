## 1. 概述

在本教程中，我们将讨论Java 中的ArrayIndexOutOfBoundsException。我们将了解它发生的原因以及如何避免它。

## 2、 ArrayIndexOutOfBoundsException什么时候出现？

我们知道，在Java中，[数组](https://www.baeldung.com/java-arrays-guide)是一种静态数据结构，我们在创建的时候就定义了它的大小。

我们使用索引访问数组的元素。数组中的索引从零开始，绝不能大于或等于数组的大小。

简而言之，经验法则是 0 <= 索引 <(数组大小)。

当我们访问由具有无效索引的数组支持的数组或 Collection 时，会发生ArrayIndexOutOfBoundsException 。这意味着索引要么小于零，要么大于或等于数组的大小。

此外，绑定检查发生在运行时。因此，ArrayIndexOutOfBoundsException是一个运行时异常。因此，我们在访问数组的边界元素时需要格外小心。

让我们了解一些导致ArrayIndexOutOfBoundsException 的常见操作。

### 2.1. 访问数组

访问数组时可能发生的最常见错误是忘记了上限和下限。

数组的下限始终为 0，而上限则比其长度小 1。

访问这些边界之外的数组元素将抛出ArrayIndexOutOfBoundsException：

```java
int[] numbers = new int[] {1, 2, 3, 4, 5};
int lastNumber = numbers[5];
```

这里，数组的大小是 5，这意味着索引的范围是 0 到 4。

在这种情况下，访问第 5 个索引会导致ArrayIndexOutOfBoundsException：

```java
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 5
    at ...
```

同样，如果我们传递一个小于零的值作为数字的索引，我们会得到ArrayIndexOutOfBoundsException 。

### 2.2. 访问Arrays.asList()返回的列表

静态方法[Arrays.asList()](https://www.baeldung.com/java-arrays-aslist-vs-new-arraylist#arraysaslist)返回一个由指定数组支持的固定大小的列表。此外，它充当基于数组和基于集合的 API 之间的桥梁。

这个返回的List具有基于索引访问其元素的方法。此外，与数组类似，索引从零开始，范围比其大小小一。

如果我们尝试访问超出此范围的Arrays.asList()返回的List的元素 ，我们将得到ArrayIndexOutOfBoundsException：

```java
List<Integer> numbersList = Arrays.asList(1, 2, 3, 4, 5);
int lastNumber = numbersList.get(5);
```

在这里，我们再次尝试获取List的最后一个元素。最后一个元素的位置是 5，但它的索引是 4(大小 – 1)。因此，我们得到如下所示的ArrayIndexOutOfBoundsException ：

```java
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 5
    at java.base/java.util.Arrays$ArrayList.get(Arrays.java:4351)
    at  ...
```

类似地，如果我们传递一个负数索引，比如 -1，我们将得到类似的结果。

### 2.3. 循环迭代

有时，在 for 循环中遍历数组时，我们可能会输入错误的终止表达式。

我们可能会迭代直到它的长度结束，而不是在比数组长度小 1 处终止索引：

```java
int sum = 0;
for (int i = 0; i <= numbers.length; i++) {
    sum += numbers[i];
}
```

在上面的终止表达式中，循环变量i 被比较为小于或等于我们现有数组编号的长度。因此，在最后一次迭代中， i 的值将变为 5。

由于索引 5 超出了数字范围， 它将再次导致ArrayIndexOutOfBoundsException：

```java
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 5
    at com.baeldung.concatenate.IndexOutOfBoundExceptionExamples.main(IndexOutOfBoundExceptionExamples.java:22)
```

## 3. 如何避免ArrayIndexOutOfBoundsException？

现在让我们了解一些避免ArrayIndexOutOfBoundsException 的方法。

### 3.1. 记住起始索引

我们必须永远记住，Java 中的数组索引是从 0 开始的。因此，第一个元素始终位于索引 0 处，而最后一个元素位于比数组长度小一的索引处。

记住这个规则将帮助我们在大多数时候避免ArrayIndexOutOfBoundsException 。

### 3.2. 在循环中正确使用运算符

错误地将循环变量初始化为索引 1 可能会导致ArrayIndexOutOfBoundsException。

同样，在循环的终止表达式中错误使用运算符<、<=、>或>=也是导致此异常发生的常见原因。

我们应该正确判断这些运算符在循环中的使用。

### 3.3. 使用增强的for循环

如果我们的应用程序运行在Java1.5 或更高版本上，我们应该使用专门开发的[增强的for 循环语句来迭代集合和数组。](https://www.baeldung.com/java-for-loop)此外，它使我们的循环更加简洁易读。

此外，使用增强的for循环可以帮助我们完全避免ArrayIndexOutOfBoundsException，因为它不涉及索引变量：

```java
for (int number : numbers) {
    sum += number;
}
```

在这里，我们不必担心索引。增强的for循环选取一个元素，并在每次迭代时将其分配给循环变量number。因此，它完全避免了ArrayIndexOutOfBoundsException。

## 4. IndexOutOfBoundsException与ArrayIndexOutOfBoundsException

IndexOutOfBoundsException当我们尝试访问超出其范围的某种类型(字符串、数组、列表等)的索引时发生。它是ArrayIndexOutOfBoundsException和StringIndexOutOfBoundsException的超类。

与ArrayIndexOutOfBoundsException类似，当我们尝试访问索引超出其长度的String的字符时，会抛出StringIndexOutOfBoundsException 。

## 5.总结

在本文中，我们探讨了ArrayIndexOutOfBoundsException、它如何发生的一些示例，以及一些避免它的常用技术。