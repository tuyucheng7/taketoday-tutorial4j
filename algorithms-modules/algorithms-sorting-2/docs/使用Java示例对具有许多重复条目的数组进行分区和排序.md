## 1. 概述

算法的运行时复杂性通常取决于输入的性质。

在本教程中，我们将看到Quicksort 算法的简单实现对重复元素的性能如何不佳。

此外，我们将学习一些快速排序变体，以有效地对具有高密度重复键的输入进行分区和排序。

## 2. 平凡快速排序

[快速排序](https://www.baeldung.com/java-quicksort)是一种基于分而治之范式的高效排序算法。从功能上讲，它对输入数组进行就地操作，并通过简单的比较和交换操作重新排列元素。

### 2.1. 单轴分区

快速排序算法的简单实现在很大程度上依赖于单轴划分过程。换句话说，分区将数组 A=[a p , a p+1 , a p+2 ,…, a r ] 分成两部分 A[p..q] 和 A[q+1..r] 这样那：

-   第一个分区中的所有元素A[p..q] 都小于或等于主元值 A[q]
-   第二个分区A[q+1..r] 中的所有元素都大于或等于主元值 A[q]

[![琐碎的快速排序](https://www.baeldung.com/wp-content/uploads/2020/01/trivial_quicksort.png)](https://www.baeldung.com/wp-content/uploads/2020/01/trivial_quicksort.png)

之后，这两个分区被视为独立的输入数组，并将其自身提供给快速排序算法。让我们看看[Lomuto 的快速排序](https://www.baeldung.com/algorithm-quicksort#1-lomuto-partitioning)：

[![快速排序简单演示](https://www.baeldung.com/wp-content/uploads/2020/01/quicksort-trivial-demo.gif)](https://www.baeldung.com/wp-content/uploads/2020/01/quicksort-trivial-demo.gif)

### 2.2. 重复元素的性能

假设我们有一个数组 A = [4, 4, 4, 4, 4, 4, 4]，它的所有元素都相等。

在使用单轴分区方案对该数组进行分区时，我们将得到两个分区。第一个分区将为空，而第二个分区将包含 N-1 个元素。此外，分区过程的每次后续调用只会将输入大小减少一个。让我们看看它是如何工作的：

[![快速排序琐碎的重复项](https://www.baeldung.com/wp-content/uploads/2020/01/quicksort_trivial_duplicates.gif)](https://www.baeldung.com/wp-content/uploads/2020/01/quicksort_trivial_duplicates.gif)

由于分区过程具有线性时间复杂度，因此在这种情况下，总时间复杂度是二次的。这是我们输入数组的最坏情况。

## 3. 三向分区

为了有效地对具有大量重复键的数组进行排序，我们可以选择更负责任地处理相等键。我们的想法是在我们第一次遇到它们时将它们放在正确的位置。所以，我们正在寻找的是数组的三分区状态：

-   最左边的分区包含严格小于分区键的元素
-   中间分区包含等于分区键的所有元素
-   最右边的分区包含所有严格大于分区键的元素

[![3路分区预览](https://www.baeldung.com/wp-content/uploads/2020/01/3-way-partition-preview.png)](https://www.baeldung.com/wp-content/uploads/2020/01/3-way-partition-preview.png)

现在，我们将深入探讨可用于实现三向分区的几种方法。

## 4. Dijkstra 的方法

Dijkstra 的方法是一种进行三向划分的有效方法。为了理解这一点，让我们研究一个经典的编程问题。

### 4.1. 荷兰国旗问题

受[荷兰三色旗的](https://en.wikipedia.org/wiki/Flag_of_the_Netherlands)启发，[Edsger Dijkstra](https://en.wikipedia.org/wiki/Edsger_Dijkstra)提出了一个名为[荷兰国旗问题](https://en.wikipedia.org/wiki/Dutch_national_flag_problem)(DNF)的编程问题。

简而言之，这是一个重排问题，给定三种颜色的球随机排成一行，我们被要求将相同颜色的球分到一组。此外，重新排列必须确保组遵循正确的顺序。

有趣的是，DNF 问题与具有重复元素的数组的 3 路分区做了一个惊人的类比。

我们可以根据给定的键将数组的所有数字分为三组：

-   红色组包含所有严格小于键的元素
-   白色组包含等于键的所有元素
-   蓝色组包含所有严格大于键的元素

[![DNF分区1](https://www.baeldung.com/wp-content/uploads/2020/01/DNF_Partition_1.png)](https://www.baeldung.com/wp-content/uploads/2020/01/DNF_Partition_1.png)

### 4.2. 算法

解决 DNF 问题的方法之一是选择第一个元素作为分区键并从左到右扫描数组。当我们检查每个元素时，我们将它移到正确的组中，即 Lesser、Equal 和 Greater。

为了跟踪我们的分区进度，我们需要三个指针的帮助，即lt、current和gt。在任何时间点，lt左侧的元素将严格小于分区键，而gt右侧的元素将严格大于分区键。

此外，我们将使用current指针进行扫描，这意味着位于current和gt指针之间的所有元素还有待探索：

[![DNF 不变量](https://www.baeldung.com/wp-content/uploads/2020/01/DNF_Invariant.png)](https://www.baeldung.com/wp-content/uploads/2020/01/DNF_Invariant.png)

首先，我们可以在数组的开头设置lt和current指针，在数组的结尾设置gt指针：

[![DNF 东西 1](https://www.baeldung.com/wp-content/uploads/2020/01/DNF_Algo_1.png)](https://www.baeldung.com/wp-content/uploads/2020/01/DNF_Algo_1.png)

对于通过当前指针读取的每个元素，我们将其与分区键进行比较，并采取以下三个复合操作之一：

-   如果input[current] < key，那么我们交换input[current]和input[lt]并增加current和lt 指针
-   如果input[current] == key，那么我们增加当前指针
-   如果input[current] > key，那么我们交换input[current]和input[gt]并递减gt

最终，我们将在current和gt指针相互交叉时停止。这样，未探索区域的大小就会减少到零，我们将只剩下三个必需的分区。

最后，让我们看看该算法如何处理具有重复元素的输入数组：

[![快排dnf](https://www.baeldung.com/wp-content/uploads/2020/01/quicksort_dnf.gif)](https://www.baeldung.com/wp-content/uploads/2020/01/quicksort_dnf.gif)

### 4.3. 执行

首先，让我们编写一个名为compare()的实用程序来对两个数字进行三向比较：

```java
public static int compare(int num1, int num2) {
    if (num1 > num2)
        return 1;
    else if (num1 < num2)
        return -1;
    else
        return 0;
}
```

接下来，让我们添加一个名为swap()的方法来交换同一数组的两个索引处的元素：

```java
public static void swap(int[] array, int position1, int position2) {
    if (position1 != position2) {
        int temp = array[position1];
        array[position1] = array[position2];
        array[position2] = temp;
    }
}
```

为了唯一标识数组中的一个分区，我们需要它的左右边界索引。那么，让我们继续创建一个Partition类：

```java
public class Partition {
    private int left;
    private int right;
}
```

现在，我们准备编写我们的三向partition()过程：

```java
public static Partition partition(int[] input, int begin, int end) {
    int lt = begin, current = begin, gt = end;
    int partitioningValue = input[begin];

    while (current <= gt) {
        int compareCurrent = compare(input[current], partitioningValue);
        switch (compareCurrent) {
            case -1:
                swap(input, current++, lt++);
                break;
            case 0:
                current++;
                break;
            case 1:
                swap(input, current, gt--);
                break;
        }
    }
    return new Partition(lt, gt);
}
```

最后，让我们编写一个quicksort()方法，它利用我们的 3 向分区方案递归地对左右分区进行排序：

```java
public static void quicksort(int[] input, int begin, int end) {
    if (end <= begin)
        return;

    Partition middlePartition = partition(input, begin, end);

    quicksort(input, begin, middlePartition.getLeft() - 1);
    quicksort(input, middlePartition.getRight() + 1, end);
}
```

## 5.宾利-麦克罗伊的方法

[Jon Bentley](https://en.wikipedia.org/wiki/Jon_Bentley_(computer_scientist))和[Douglas McIlroy](https://en.wikipedia.org/wiki/Douglas_McIlroy)共同编写了Quicksort 算法的优化版本。让我们在Java中理解和实现这个变体：

### 5.1. 分区方案

该算法的关键是基于迭代的分区方案。一开始，整个数字数组对我们来说是一个未开发的领域：

[![宾利未开发](https://www.baeldung.com/wp-content/uploads/2020/01/Bentley-Unexplored.png)](https://www.baeldung.com/wp-content/uploads/2020/01/Bentley-Unexplored.png)

然后我们开始从左右方向探索数组的元素。每当我们进入或离开探索循环时，我们都可以将数组可视化为五个区域的组合：

-   在极端的两端，位于具有等于分区值的元素的区域
-   未探索区域位于中心，其大小随着每次迭代不断缩小
-   未探索区域的左侧是所有小于分区值的元素
-   未探索区域右侧为大于划分值的元素

[![Bentley 分区不变量](https://www.baeldung.com/wp-content/uploads/2020/01/Bentley-Partitioning-Invariant.png)](https://www.baeldung.com/wp-content/uploads/2020/01/Bentley-Partitioning-Invariant.png)

最终，当没有元素可供探索时，我们的探索循环终止。在这个阶段，未探索区域的大小实际上为零，我们只剩下四个区域：

[![Bentley 循环终止](https://www.baeldung.com/wp-content/uploads/2020/01/Bentley-loop-termination.png)](https://www.baeldung.com/wp-content/uploads/2020/01/Bentley-loop-termination.png)

接下来，我们将中心的两个等域中的所有元素移动，使得中心只有一个等域被左边的小域和右边的大域包围。为此，首先，我们将左等区域中的元素与小区域右端的元素交换。类似地，右等域中的元素与大域左端的元素交换。

[![宾利隔断](https://www.baeldung.com/wp-content/uploads/2020/01/Bentley-partition.png)](https://www.baeldung.com/wp-content/uploads/2020/01/Bentley-partition.png)

最后，我们将只剩下三个分区，我们可以进一步使用相同的方法来划分 less 和 greater 区域。

### 5.2. 执行

在三向快速排序的递归实现中，我们需要为具有不同下限和上限集的子数组调用分区过程。因此，我们的partition()方法必须接受三个输入，即数组及其左右边界。

```java
public static Partition partition(int input[], int begin, int end){
	// returns partition window
}
```

为了简单起见，我们可以选择分区值作为数组的最后一个元素。另外，让我们定义两个变量left=begin 和right=end来向内探索数组。

此外，我们还需要跟踪位于最左边和最右边的相等元素的数量。因此，让我们初始化leftEqualKeysCount=0和rightEqualKeysCount=0，现在我们准备探索和分区数组。

首先，我们从两个方向开始移动，找到一个反转，其中左边的元素不小于分区值，右边的元素不大于分区值。然后，除非 left 和 right 两个指针相互交叉，否则我们交换这两个元素。

在每次迭代中，我们将等于partitioningValue的元素移向两端并增加适当的计数器：

```java
while (true) {
    while (input[left] < partitioningValue) left++; 
    
    while (input[right] > partitioningValue) {
        if (right == begin)
            break;
        right--;
    }

    if (left == right && input[left] == partitioningValue) {
        swap(input, begin + leftEqualKeysCount, left);
        leftEqualKeysCount++;
        left++;
    }

    if (left >= right) {
        break;
    }

    swap(input, left, right);

    if (input[left] == partitioningValue) {
        swap(input, begin + leftEqualKeysCount, left);
        leftEqualKeysCount++;
    }

    if (input[right] == partitioningValue) {
        swap(input, right, end - rightEqualKeysCount);
        rightEqualKeysCount++;
    }
    left++; right--;
}
```

在下一阶段，我们需要从中心的两端移动所有相等的元素。退出循环后，左指针将指向一个值不小于partitioningValue的元素。使用这个事实，我们开始从两端向中心移动相等的元素：

```java
right = left - 1;
for (int k = begin; k < begin + leftEqualKeysCount; k++, right--) { 
    if (right >= begin + leftEqualKeysCount)
        swap(input, k, right);
}
for (int k = end; k > end - rightEqualKeysCount; k--, left++) {
    if (left <= end - rightEqualKeysCount)
        swap(input, left, k);
}

```

在最后一个阶段，我们可以返回中间分区的边界：

```java
return new Partition(right + 1, left - 1);
```

最后，让我们看一下我们在示例输入上的实现演示

[![快速排序本特利](https://www.baeldung.com/wp-content/uploads/2020/01/quicksort_bentley.gif)](https://www.baeldung.com/wp-content/uploads/2020/01/quicksort_bentley.gif)

## 六、算法分析

通常，Quicksort 算法的平均时间复杂度为 O(nlog(n))，最坏情况时间复杂度为 O(n 2 )。对于高密度的重复键，我们几乎总是通过简单的快速排序实现获得最坏的性能。

但是，当我们使用 Quicksort 的三向分区变体时，例如 DNF 分区或 Bentley 分区，我们能够防止重复键的负面影响。此外，随着重复键密度的增加，我们算法的性能也会提高。结果，当所有键都相等时，我们获得了最佳性能，并且我们在线性时间内获得了包含所有相等键的单个分区。

然而，我们必须注意，当我们从普通的单轴分区切换到三路分区方案时，我们实际上是在增加开销。

对于基于 DNF 的方法，开销不依赖于重复键的密度。因此，如果我们对具有所有唯一键的数组使用 DNF 分区，那么与我们优化选择枢轴的简单实现相比，我们将获得较差的性能。

但是，Bentley-McIlroy 的方法做得很聪明，因为从两个极端移动相同键的开销取决于它们的数量。因此，如果我们将此算法用于具有所有唯一键的数组，即使那样，我们也会获得相当好的性能。

总之，单轴分区和三路分区算法的最坏情况时间复杂度都是O(nlog(n))。然而，真正的好处在最佳情况下是可见的，我们看到时间复杂度从单轴分区的O(nlog(n))到三向分区的O(n)。

## 七. 总结

在本教程中，我们了解了当输入包含大量重复元素时快速排序算法的简单实现所带来的性能问题。

为了解决这个问题，我们学习了不同的三向分区方案以及如何在Java中实现它们。