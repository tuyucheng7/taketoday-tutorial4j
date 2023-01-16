## 1. 概述

在本教程中，我们将详细探讨 QuickSort 算法，重点放在其Java实现上。

我们还将讨论它的优点和缺点，然后分析它的时间复杂度。

## 2. 快速排序算法

[Quicksort](https://www.baeldung.com/cs/the-quicksort-algorithm)是一种排序算法，它利用了[分而治之的原则](https://www.baeldung.com/cs/divide-and-conquer-strategy)。 它的平均复杂度为O(n log n)，是最常用的排序算法之一，尤其是对于大数据量。

重要的是要记住 Quicksort 不是一个稳定的算法。 稳定排序算法是一种算法，其中具有相同值的元素在排序输出中的出现顺序与它们在输入列表中出现的顺序相同。

输入列表被称为 pivot 的元素分成两个子列表；一个子列表的元素小于主元，另一个子列表的元素大于主元。对每个子列表重复此过程。

最后，所有排序的子列表合并形成最终输出。

### 2.1. 算法步骤

1.  我们从列表中选择一个元素，称为枢轴。我们将使用它将列表分成两个子列表。
2.  我们重新排列枢轴周围的所有元素——值较小的元素放在它前面，所有大于枢轴的元素放在它后面。在此步骤之后，枢轴处于其最终位置。这是重要的分区步骤。
3.  我们递归地将上述步骤应用于枢轴左侧和右侧的两个子列表。

正如我们所看到的，快速排序自然是一种递归算法，就像每个分而治之的方法一样。

为了更好地理解这个算法，我们举一个简单的例子。

```java
Arr[] = {5, 9, 4, 6, 5, 3}
```

1.  假设我们为简单起见选择 5 作为基准
2.  我们首先将所有小于 5 的元素放在数组的第一个位置：{3, 4, 5, 6, 5, 9}
3.  然后我们将对左子数组 {3,4} 重复它，以 3 作为枢轴
4.  没有小于3的元素
5.  我们对枢轴右侧的子数组应用快速排序，即 {4}
6.  该子数组仅包含一个已排序元素
7.  我们继续原始数组的右边部分，{6, 5, 9}，直到我们得到最终的有序数组

### 2.2. 选择最佳枢轴

QuickSort 中的关键点是选择最佳枢轴。中间元素当然是最好的，因为它将列表分成两个相等的子列表。

但是从无序列表中找到中间元素既困难又耗时，这就是为什么我们将第一个元素、最后一个元素、中位数或任何其他随机元素作为基准。

## 3.Java实现

第一个方法是quickSort()，它将要排序的数组、第一个和最后一个索引作为参数。首先，我们检查索引并仅在仍有要排序的元素时才继续。

我们得到已排序数据透视表的索引，并用它递归调用partition()方法，参数与quickSort()方法相同，但索引不同：

```java
public void quickSort(int arr[], int begin, int end) {
    if (begin < end) {
        int partitionIndex = partition(arr, begin, end);

        quickSort(arr, begin, partitionIndex-1);
        quickSort(arr, partitionIndex+1, end);
    }
}
```

让我们继续partition()方法。为简单起见，此函数将最后一个元素作为基准。然后，检查每个元素，如果其值较小，则在枢轴之前交换它。

到分区结束时，所有小于主元的元素都在它的左边，所有大于主元的元素都在它的右边。枢轴位于其最终排序位置，函数返回此位置：

```java
private int partition(int arr[], int begin, int end) {
    int pivot = arr[end];
    int i = (begin-1);

    for (int j = begin; j < end; j++) {
        if (arr[j] <= pivot) {
            i++;

            int swapTemp = arr[i];
            arr[i] = arr[j];
            arr[j] = swapTemp;
        }
    }

    int swapTemp = arr[i+1];
    arr[i+1] = arr[end];
    arr[end] = swapTemp;

    return i+1;
}
```

## 4.算法分析

### 4.1. 时间复杂度

在最好的情况下，该算法会将列表分成两个大小相等的子列表。因此，完整的n大小列表的第一次迭代需要O(n)。对剩余的两个具有n/2 个元素的子列表进行排序每个需要2O(n/2)。因此，QuickSort 算法的复杂度为O(n log n)。

在最坏的情况下，算法将在每次迭代中只选择一个元素，因此O(n) + O(n-1) + … + O(1)等于O(n 2 )。

平均而言，QuickSort 具有O(n log n)复杂度，使其适用于大数据量。

### 4.2. 快速排序与归并排序

让我们讨论在哪些情况下我们应该选择 QuickSort 而不是 MergeSort。

尽管 Quicksort 和 Mergesort 的平均时间复杂度都是O(n log n)，但 Quicksort 是首选算法，因为它的[空间复杂度为](https://www.baeldung.com/cs/space-complexity)O(log(n)) 。另一方面，Mergesort 需要O(n)额外的存储空间，这使得它对于数组来说非常昂贵。

Quicksort 需要访问不同的索引以进行操作，但这种访问在链表中不能直接实现，因为没有连续的块；因此，要访问一个元素，我们必须从链表的开头开始遍历每个节点。此外，Mergesort 的实现没有额外的LinkedLists 空间。

在这种情况下，通常首选增加 Quicksort 和 Mergesort 的开销。

## 5.总结

快速排序是一种优雅的排序算法，在大多数情况下都非常有用。

它通常是一种“就地”算法，平均时间复杂度为O(n log n)。

另一个值得一提的有趣点是Java的Arrays.sort()方法使用 Quicksort 对基元数组进行排序。该实现使用两个枢轴并且比我们的简单解决方案执行得更好，这就是为什么对于生产代码通常最好使用库方法。