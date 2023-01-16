## 1. 简介

在本教程中，我们将了解[Radix Sort](https://www.baeldung.com/cs/radix-sort)、分析其性能并查看其实现。

这里我们重点介绍使用 Radix Sort 对整数进行排序，但它不仅限于数字。我们也可以使用它来对其他类型(例如String)进行排序。

为了简单起见，我们将重点关注十进制系统，其中数字以基数(基数)10 表示。

## 2. 算法概述

基数排序是一种排序算法，它根据数字的位置对数字进行排序。基本上，它使用数字中数字的位值。与大多数其他排序算法(例如[归并排序](https://www.baeldung.com/java-merge-sort)、[插入排序](https://www.baeldung.com/java-insertion-sort)、[冒泡排序](https://www.baeldung.com/java-bubble-sort))不同，它不比较数字。

基数排序使用[稳定排序算法](https://www.baeldung.com/stable-sorting-algorithms)作为子程序对数字进行排序。我们在这里使用了计数排序的变体作为子例程，它使用基数对每个位置的数字进行排序。[计数排序](https://www.baeldung.com/java-counting-sort)是一种稳定的排序算法，在实践中效果很好。

基数排序的工作原理是将数字从最低有效数字 (LSD) 排序到最高有效数字 (MSD)。我们还可以实现基数排序来处理来自 MSD 的数字。

## 3. 一个简单的例子

让我们通过一个例子看看它是如何工作的。让我们考虑以下数组：

[![大批](https://www.baeldung.com/wp-content/uploads/2019/09/Array.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Array.png)

### 3.1. 迭代 1

我们将通过处理来自 LSD 的数字并转向 MSD 来对该数组进行排序。

因此，让我们从个位的数字开始：

[![迭代 1 之前](https://www.baeldung.com/wp-content/uploads/2019/09/Before-Iteration-1.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Before-Iteration-1.png)

第一次迭代后，数组现在看起来像：

[![迭代 1 之后](https://www.baeldung.com/wp-content/uploads/2019/09/After-Iteration-1.png)](https://www.baeldung.com/wp-content/uploads/2019/09/After-Iteration-1.png)

请注意，数字已根据个位的数字排序。

### 3.2. 迭代 2

让我们继续看十位数字：

[![迭代 2 之前](https://www.baeldung.com/wp-content/uploads/2019/09/Before-Iteration-2.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Before-Iteration-2.png)

现在数组看起来像：

[![迭代 2 后](https://www.baeldung.com/wp-content/uploads/2019/09/After-Iteration-2.png)](https://www.baeldung.com/wp-content/uploads/2019/09/After-Iteration-2.png)

我们看到数字 7 占据了数组中的第一个位置，因为它的十位没有任何数字。我们也可以认为这是在十位上有一个 0。

### 3.3. 迭代 3

让我们继续看百位上的数字：

[![迭代 3 之前](https://www.baeldung.com/wp-content/uploads/2019/09/Before-Iteration-3.png)](https://www.baeldung.com/wp-content/uploads/2019/09/Before-Iteration-3.png)

在这次迭代之后，数组看起来像：

[![迭代 3 之后](https://www.baeldung.com/wp-content/uploads/2019/09/After-Iteration-3.png)](https://www.baeldung.com/wp-content/uploads/2019/09/After-Iteration-3.png)

算法到此为止，所有元素都已排序。

## 4.实施

现在让我们看看实现。

```java
void sort(int[] numbers) {
    int maximumNumber = findMaximumNumberIn(numbers);
    int numberOfDigits = calculateNumberOfDigitsIn(maximumNumber);
    int placeValue = 1;
    while (numberOfDigits-- > 0) {
        applyCountingSortOn(numbers, placeValue);
        placeValue = 10;
    }
}
```

该算法的工作原理是找出数组中的最大数，然后计算其长度。这一步帮助我们确保我们为每个位值执行子例程。

例如，在数组[7, 37, 68, 123, 134, 221, 387, 468, 769]中，最大数量为 769，长度为 3。

因此，我们在每个位置的数字上迭代并应用子例程三次：

```java
void applyCountingSortOn(int[] numbers, int placeValue) {

    int range = 10 // decimal system, numbers from 0-9

    // ...

    // calculate the frequency of digits
    for (int i = 0; i < length; i++) {
        int digit = (numbers[i] / placeValue) % range;
        frequency[digit]++;
    }

    for (int i = 1; i < range; i++) {
        frequency[i] += frequency[i - 1];
    }

    for (int i = length - 1; i >= 0; i--) {
        int digit = (numbers[i] / placeValue) % range;
        sortedValues[frequency[digit] - 1] = numbers[i];
        frequency[digit]--;
    }

    System.arraycopy(result, 0, numbers, 0, length); 

}
```

在子例程中，我们使用基数(范围)来计算每个数字的出现次数并增加其频率。因此，0 到 9 范围内的每个 bin 都会有一些基于数字频率的值。然后我们使用频率来定位数组中的每个元素。这也有助于我们最大限度地减少对数组进行排序所需的空间。

现在让我们测试一下我们的方法：

```java
@Test
public void givenUnsortedArray_whenRadixSort_thenArraySorted() {
    int[] numbers = {387, 468, 134, 123, 68, 221, 769, 37, 7};
    RadixSort.sort(numbers);
    int[] numbersSorted = {7, 37, 68, 123, 134, 221, 387, 468, 769};
    assertArrayEquals(numbersSorted, numbers); 
}
```

## 5.基数排序与计数排序

子程序中，频率数组的长度为10(0-9)。在计数排序的情况下，我们不使用范围。频率数组的长度将是数组中的最大数字 + 1。因此我们不将它们划分为 bins，而 Radix Sort 使用 bins 进行排序。

当数组的长度不小于数组中的最大值时，计数排序非常有效，而基数排序允许数组中的最大值。

## 6. 复杂性

Radix Sort 的性能取决于选择用于对数字进行排序的稳定排序算法。

在这里，我们使用基数排序对基数b中的n 个数字的数组进行排序。在我们的例子中，基数是 10。我们应用了计数排序d次，其中d代表位数。所以 Radix Sort 的时间复杂度变成了O(d  (n + b))。

空间复杂度为O(n + b)，因为我们在这里使用了计数排序的变体作为子例程。

## 七. 总结

在本文中，我们描述了基数排序算法并说明了如何实现它。