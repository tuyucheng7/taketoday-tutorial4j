## 1. 概述

Boolean 是Java中的一种基本数据类型。通常，它只能有两个值，true或false。

在本教程中，我们将讨论如何初始化布尔值数组。

## 二、问题简介

问题很简单。简单地说，我们想要初始化一个具有相同默认值的布尔变量数组。

然而，Java 有两种“不同”的布尔类型，[原始](https://www.baeldung.com/java-primitives-vs-objects) 布尔值 和 [装箱](https://www.baeldung.com/java-wrapper-classes) 布尔值。因此，在本教程中，我们将涵盖这两种情况并解决如何初始化boolean和Boolean数组。

此外，为简单起见，我们将使用单元测试断言来验证我们的数组初始化是否按预期工作。

那么接下来，让我们从原始布尔类型开始。

## 3. 初始化原始布尔数组

在Java中，原始变量有一个默认值。例如，原始int变量的默认值为 0，而原始boolean变量默认 为false。

因此，如果我们想用所有 false初始化一个布尔数组，我们可以简单地创建数组而不设置值。

接下来，让我们创建一个测试来验证它：

```java
boolean[] expected = { false, false, false, false, false };
boolean[] myArray = new boolean[5];
assertArrayEquals(expected, myArray);
```

如果我们运行上面的测试，它就会通过。正如我们所见，boolean[] myArray = new boolean[5]; 初始化布尔数组中的五个false元素 。

值得一提的是，当我们想要根据[两个数组的值来比较它们时，](https://www.baeldung.com/java-comparing-arrays)我们应该使用assertArrayEquals()方法而不是assertEquals()。这是因为数组的equals()方法会检查两个数组的引用是否相同。

因此，初始化原始false数组很容易，因为我们可以使用boolean的默认值。然而，有时，我们可能想要创建一个真值数组。如果是这种情况，我们必须以某种方式将值设置为true。当然，我们可以单独或在循环中设置它们。但是[Arrays.fill()](https://www.baeldung.com/java-initialize-array#using-arraysfill)方法使工作更容易：

```java
boolean[] expected = { true, true, true, true, true };
boolean[] myArray = new boolean[5];
Arrays.fill(myArray, true);
assertArrayEquals(expected, myArray);
```

同样，如果我们运行它，测试就会通过。所以，我们有一组 true。

## 4. 初始化装箱布尔数组

到目前为止，我们已经学习了如何用true 或false初始化原始布尔 值数组。现在，让我们看看装箱布尔方面的相同操作。

首先，与原始布尔变量不同，装箱布尔变量没有默认值。如果我们不为其设置值，则其值为null。因此，如果我们创建一个Boolean 数组而不设置元素的值，则所有元素都是null。让我们创建一个测试来验证它：

```java
Boolean[] expectedAllNull = { null, null, null, null, null };
Boolean[] myNullArray = new Boolean[5];
assertArrayEquals(expectedAllNull, myNullArray);
```

如果我们想用true或 false初始化布尔数组 ，我们仍然可以使用Arrays.fill()方法：

```java
Boolean[] expectedAllFalse = { false, false, false, false, false };
Boolean[] myFalseArray = new Boolean[5];
Arrays.fill(myFalseArray, false);
assertArrayEquals(expectedAllFalse, myFalseArray);
                                                                   
Boolean[] expectedAllTrue = { true, true, true, true, true };
Boolean[] myTrueArray = new Boolean[5];
Arrays.fill(myTrueArray, true);
assertArrayEquals(expectedAllTrue, myTrueArray);
```

当我们运行上面的测试时，它通过了。

## 5.总结

在这篇简短的文章中，我们学习了如何在Java中初始化布尔值或布尔数组。