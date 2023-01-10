## 1. 概述

[Apache Commons Lang 3](https://commons.apache.org/proper/commons-lang/)库提供对JavaAPI 核心类操作的支持。这种支持包括处理字符串、数字、日期、并发、对象反射等的方法。

在本快速教程中，我们将重点介绍使用非常有用的ArrayUtils实用程序类进行数组处理。

## 2.Maven依赖

为了使用 Commons Lang 3 库，只需使用以下依赖项从中央 Maven 存储库中拉取它：

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

[可以在此处](https://search.maven.org/classic/#search|ga|1|g%3A"org.apache.commons" AND a%3A"commons-lang3")找到该库的最新版本。

## 3.数组实用程序

ArrayUtils类提供了用于处理数组的实用方法。这些方法试图通过防止在传入空值时抛出异常来优雅地处理输入。

本节说明了ArrayUtils类中定义的一些方法。请注意，所有这些方法都适用于任何元素类型。

为方便起见，还定义了它们的重载风格以处理包含基本类型的数组。

## 4.添加和添加全部

add方法给定数组并在新数组的给定位置插入给定元素。如果未指定位置，则将新元素添加到数组的末尾。

以下代码片段在oldArray数组的第一个位置插入数字零并验证结果：

```java
int[] oldArray = { 2, 3, 4, 5 };
int[] newArray = ArrayUtils.add(oldArray, 0, 1);
int[] expectedArray = { 1, 2, 3, 4, 5 };
 
assertArrayEquals(expectedArray, newArray);
```

如果未指定位置，则在oldArray的末尾添加附加元素：

```java
int[] oldArray = { 2, 3, 4, 5 };
int[] newArray = ArrayUtils.add(oldArray, 1);
int[] expectedArray = { 2, 3, 4, 5, 1 };
 
assertArrayEquals(expectedArray, newArray);
```

addAll方法将所有元素添加到给定数组的末尾。以下片段说明了此方法并确认了结果：

```java
int[] oldArray = { 0, 1, 2 };
int[] newArray = ArrayUtils.addAll(oldArray, 3, 4, 5);
int[] expectedArray = { 0, 1, 2, 3, 4, 5 };
 
assertArrayEquals(expectedArray, newArray);
```

## 5.删除和删除所有 

remove方法从给定数组的指定位置删除一个元素。所有后续元素都向左移动。请注意，这适用于所有删除操作。

此方法返回一个新数组而不是对原始数组进行更改：

```java
int[] oldArray = { 1, 2, 3, 4, 5 };
int[] newArray = ArrayUtils.remove(oldArray, 1);
int[] expectedArray = { 1, 3, 4, 5 };
 
assertArrayEquals(expectedArray, newArray);
```

removeAll方法从给定数组中删除指定位置的所有元素：

```java
int[] oldArray = { 1, 2, 3, 4, 5 };
int[] newArray = ArrayUtils.removeAll(oldArray, 1, 3);
int[] expectedArray = { 1, 3, 5 };
 
assertArrayEquals(expectedArray, newArray);
```

## 6.移除元素和移除元素

removeElement方法从给定数组中删除第一次出现的指定元素。

如果给定数组中不存在这样的元素，则删除操作将被忽略，而不是抛出异常：

```java
int[] oldArray = { 1, 2, 3, 3, 4 };
int[] newArray = ArrayUtils.removeElement(oldArray, 3);
int[] expectedArray = { 1, 2, 3, 4 };
 
assertArrayEquals(expectedArray, newArray);
```

removeElements方法从给定数组中删除第一次出现的指定元素。

如果给定数组中不存在指定元素，则忽略删除操作，而不是抛出异常：

```java
int[] oldArray = { 1, 2, 3, 3, 4 };
int[] newArray = ArrayUtils.removeElements(oldArray, 2, 3, 5);
int[] expectedArray = { 1, 3, 4 };
 
assertArrayEquals(expectedArray, newArray);
```

## 7. removeAllOccurences API

removeAllOccurences方法从给定数组中删除所有出现的指定元素。

如果给定数组中不存在这样的元素，则删除操作将被忽略，而不是抛出异常：

```java
int[] oldArray = { 1, 2, 2, 2, 3 };
int[] newArray = ArrayUtils.removeAllOccurences(oldArray, 2);
int[] expectedArray = { 1, 3 };
 
assertArrayEquals(expectedArray, newArray);
```

## 8.包含API

contains方法检查给定数组中是否存在值。这是一个代码示例，包括对结果的验证：

```java
int[] array = { 1, 3, 5, 7, 9 };
boolean evenContained = ArrayUtils.contains(array, 2);
boolean oddContained = ArrayUtils.contains(array, 7);
 
assertEquals(false, evenContained);
assertEquals(true, oddContained);
```

## 9.反向API

reverse方法反转给定数组的指定范围内的元素顺序。此方法对传入的数组进行更改，而不是返回一个新数组。

让我们快速看一下：

```java
int[] originalArray = { 1, 2, 3, 4, 5 };
ArrayUtils.reverse(originalArray, 1, 4);
int[] expectedArray = { 1, 4, 3, 2, 5 };
 
assertArrayEquals(expectedArray, originalArray);
```

如果未指定范围，则颠倒所有元素的顺序：

```java
int[] originalArray = { 1, 2, 3, 4, 5 };
ArrayUtils.reverse(originalArray);
int[] expectedArray = { 5, 4, 3, 2, 1 };
 
assertArrayEquals(expectedArray, originalArray);
```

## 10.转换API

shift方法将给定数组中的一系列元素移动多个位置。此方法对传入的数组进行更改，而不是返回一个新数组。

以下代码片段将索引 1(含)和索引 4(不含)的元素之间的所有元素向右移动一位并确认结果：

```java
int[] originalArray = { 1, 2, 3, 4, 5 };
ArrayUtils.shift(originalArray, 1, 4, 1);
int[] expectedArray = { 1, 4, 2, 3, 5 };
 
assertArrayEquals(expectedArray, originalArray);
```

如果未指定范围边界，则移动数组的所有元素：

```java
int[] originalArray = { 1, 2, 3, 4, 5 };
ArrayUtils.shift(originalArray, 1);
int[] expectedArray = { 5, 1, 2, 3, 4 };
 
assertArrayEquals(expectedArray, originalArray);
```

## 11.子数组API

subarray方法创建一个新数组，其中包含给定数组指定范围内的元素。以下是结果断言的示例：

```java
int[] oldArray = { 1, 2, 3, 4, 5 };
int[] newArray = ArrayUtils.subarray(oldArray, 2, 7);
int[] expectedArray = { 3, 4, 5 };
 
assertArrayEquals(expectedArray, newArray);
```

请注意，当传入的索引大于数组的长度时，它会降级为数组长度，而不是让方法抛出异常。同样，如果传入负索引，它会被提升为零。

## 12.交换API

swap方法交换给定数组中指定位置的一系列元素。

以下代码片段交换从索引 0 和 3 开始的两组元素，每组包含两个元素：

```java
int[] originalArray = { 1, 2, 3, 4, 5 };
ArrayUtils.swap(originalArray, 0, 3, 2);
int[] expectedArray = { 4, 5, 3, 1, 2 };
 
assertArrayEquals(expectedArray, originalArray);
```

如果没有传入长度参数，则每个位置只有一个元素被交换：

```java
int[] originalArray = { 1, 2, 3, 4, 5 };
ArrayUtils.swap(originalArray, 0, 3);
int[] expectedArray = { 4, 2, 3, 1, 5 };
assertArrayEquals(expectedArray, originalArray);
```

## 13.总结

本教程介绍 Apache Commons Lang 3 中的核心数组处理实用程序 – ArrayUtils。