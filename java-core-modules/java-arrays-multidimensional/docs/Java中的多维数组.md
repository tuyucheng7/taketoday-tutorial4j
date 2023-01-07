## 1. 概述

Java 中的多维数组是由不同大小的数组作为其元素的数组。它也被称为“数组的数组”或“参差不齐的数组”或“参差不齐的数组”。

在本快速教程中，我们将更深入地了解多维数组的定义和使用。

## 2. 创建多维数组

让我们从创建多维数组的方法开始：

### 2.1. 速记形式

定义多维数组的一种简单方法是：

```java
int[][] multiDimensionalArr = {{1, 2}, {3, 4, 5}, {6, 7, 8, 9}};
```

在这里，我们 一步声明并初始化了multiDimensionalArr 。

### 2.2. 声明然后初始化

我们首先声明一个大小为三的多维数组：

```java
int[][] multiDimensionalArr = new int[3][];
```

在这里，我们省略了指定第二个维度，因为它会有所不同。

接下来，让我们通过声明和初始化multiDimensionalArr中的各个元素来更进一步：

```java
multiDimensionalArr[0] = new int[] {1, 2};
multiDimensionalArr[1] = new int[] {3, 4, 5};
multiDimensionalArr[2] = new int[] {6, 7, 8, 9};
```

我们也可以简单地声明它的元素而不初始化它们：

```java
multiDimensionalArr[0] = new int[2];
multiDimensionalArr[1] = new int[3];
multiDimensionalArr[2] = new int[4];
```

这些可以稍后被初始化，例如通过使用用户输入。

我们也可以使用 java.util.Arrays.fill方法来初始化数组元素：

```java
void initialize2DArray(int[][] multiDimensionalArray) {
    for (int[] array : multiDimensionalArray) {
        Arrays.fill(array, 7);
    }
}

```

数组中的所有元素都使用相同的值进行初始化。

## 3.内存表示

我们的multiDimensionalArr的内存表示是什么样 的？

正如我们所知，Java 中的数组只不过是一个对象，其元素可以是基元或引用。因此，Java 中的二维数组可以被认为是一维数组的数组。

我们在内存中的multiDimensionalArr 看起来类似于：

[![多维数组](https://www.baeldung.com/wp-content/uploads/2020/03/multi-dimensional-array.png)](https://www.baeldung.com/wp-content/uploads/2020/03/multi-dimensional-array.png)

显然，multiDimensionalArr [0]持有对大小为 2 的一维数组的引用，multiDimensionalArr[1]持有对另一个大小为 3 的一维数组的引用，依此类推。

这样Java就让我们定义和使用多维数组成为可能。

## 4. 遍历元素

我们可以像Java中的任何其他数组一样迭代多维数组。

让我们尝试 使用用户输入迭代和初始化multiDimensionalArr元素：

```java
void initializeElements(int[][] multiDimensionalArr) {
    Scanner sc = new Scanner(System.in);
    for (int outer = 0; outer < multiDimensionalArr.length; outer++) {
        for (int inner = 0; inner < multiDimensionalArr[outer].length; inner++) {
            multiDimensionalArr[outer][inner] = sc.nextInt();
        }
    }
}
```

此处，multiDimensionalArr[outer].length是multiDimensionalArr中索引outer处数组的长度。

它帮助我们确保我们只在每个子数组的有效范围内寻找元素，从而避免ArrayIndexOutOfBoundException。

## 5.打印元素

如果我们想打印多维数组的元素怎么办？

一种明显的方法是使用我们已经介绍过的迭代逻辑。这涉及遍历我们的多维数组中的每个项目，它本身就是一个数组，然后遍历该子数组——一次一个元素。

另一个选择是使用java.util.Arrays.toString()辅助方法：

```java
void printElements(int[][] multiDimensionalArr) {
    for (int index = 0; index < multiDimensionalArr.length; index++) {
        System.out.println(Arrays.toString(multiDimensionalArr[index]));
    }
}
```

我们最终得到了干净简单的代码。生成的控制台输出如下所示：

```bash
[1, 2] [3, 4, 5] [6, 7, 8, 9]
```

## 6.元素的长度

我们可以通过遍历主数组来找到多维数组中数组的长度：

```java
int[] findLengthOfElements(int[][] multiDimensionalArray) {
    int[] arrayOfLengths = new int[multiDimensionalArray.length];
    for (int i = 0; i < multiDimensionalArray.length; i++) {
        arrayOfLengths[i] = multiDimensionalArray[i].length;
    }
    return arrayOfLengths;
}
```

我们还可以使用Java流查找数组的长度：

```java
Integer[] findLengthOfArrays(int[][] multiDimensionalArray) {
    return Arrays.stream(multiDimensionalArray)
      .map(array -> array.length)
      .toArray(Integer[]::new);
}
```

## 7.一个二维数组

我们可以使用Arrays.copyOf方法一个二维数组：

```java
int[][] copy2DArray(int[][] arrayOfArrays) {
    int[][] copied2DArray = new int[arrayOfArrays.length][];
    for (int i = 0; i < arrayOfArrays.length; i++) {
        int[] array = arrayOfArrays[i];
        copied2DArray[i] = Arrays.copyOf(array, array.length);
    }
    return copied2DArray;
}
```

我们也可以通过使用Java流来实现：

```java
Integer[][] copy2DArray(Integer[][] arrayOfArrays) {
    return Arrays.stream(arrayOfArrays)
      .map(array -> Arrays.copyOf(array, array.length))
      .toArray(Integer[][]::new);
}
```

## 八、总结

在本文中，我们了解了什么是多维数组、它们在内存中的外观以及我们定义和使用它们的方式。