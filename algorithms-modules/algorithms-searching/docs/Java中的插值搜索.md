## 1. 简介

在本教程中，我们将介绍插值搜索算法并讨论它们的优缺点。此外，我们将用Java实现它并讨论算法的时间复杂度。

## 2.动机

插值搜索是对为均匀分布数据量身定制的[二分搜索](https://www.baeldung.com/java-binary-search)的改进。

无论数据分布如何，二进制搜索都会在每一步将搜索空间减半，因此它的时间复杂度始终为O(log(n))。

另一方面，插值搜索时间复杂度因数据分布而异。它比时间复杂度为O(log(log(n)))的均匀分布数据的二进制搜索更快。但是，在最坏的情况下，它的性能可能与O(n)一样差。

## 3.插值搜索

与二分查找类似，插值查找只能作用于排序好的数组。它将探针放置在每次迭代的计算位置。如果探测正好在我们要找的物品上，则返回位置；否则，搜索空间将被限制在探测器的右侧或左侧。

探针位置计算是二进制搜索和插值搜索之间的唯一区别。

在二分搜索中，探测位置始终是剩余搜索空间的最中间项。

相反，插值搜索根据以下公式计算探针位置：

[![探头位置](https://www.baeldung.com/wp-content/uploads/2019/08/probe-position.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/probe-position.jpg)

让我们来看看每个术语：

-   probe：新的探针位置将分配给此参数。
-   lowEnd：当前搜索空间中最左边的项目的索引。
-   highEnd：当前搜索空间中最右边的项目的索引。
-   data[]：包含原始搜索空间的数组。
-   item：我们正在寻找的项目。

为了更好地理解插值搜索的工作原理，让我们用一个例子来演示它。

假设我们要在下面的数组中找到 84 的位置：

[![步骤 0](https://www.baeldung.com/wp-content/uploads/2019/08/step-0.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/step-0.jpg)

数组的长度是 8，所以最初highEnd = 7 和lowEnd = 0(因为数组的索引从 0 开始，而不是 1)。

在第一步中，探针位置公式将导致探针= 5：

[![步骤1](https://www.baeldung.com/wp-content/uploads/2019/08/step-1.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/step-1.jpg)

因为84(我们要找的item)大于73(当前探测位置item)，下一步会放弃数组的左边，赋值lowEnd = probe + 1。现在搜索空间只有84和101.探针位置公式将设置probe = 6 正好是 84 的索引：

[![第2步](https://www.baeldung.com/wp-content/uploads/2019/08/step-2.jpg)](https://www.baeldung.com/wp-content/uploads/2019/08/step-2.jpg)

由于找到了我们正在寻找的项目，因此将返回位置 6。

## 4.Java实现

现在我们了解了该算法的工作原理，让我们用Java实现它。

首先，我们初始化lowEnd和highEnd：

```java
int highEnd = (data.length - 1);
int lowEnd = 0;
```

接下来，我们设置一个循环，在每次迭代中，我们根据上述公式计算新的探针。循环条件通过将item与data[lowEnd]和data[highEnd]进行比较来确保我们没有超出搜索空间：

```java
while (item >= data[lowEnd] && item <= data[highEnd] && lowEnd <= highEnd) {
    int probe
      = lowEnd + (highEnd - lowEnd)  (item - data[lowEnd]) / (data[highEnd] - data[lowEnd]);
}
```

我们还会在每次新的探测分配后检查是否找到了该项目。

最后，我们调整lowEnd或highEnd以减少每次迭代的搜索空间：

```java
public int interpolationSearch(int[] data, int item) {

    int highEnd = (data.length - 1);
    int lowEnd = 0;

    while (item >= data[lowEnd] && item <= data[highEnd] && lowEnd <= highEnd) {

        int probe
          = lowEnd + (highEnd - lowEnd)  (item - data[lowEnd]) / (data[highEnd] - data[lowEnd]);

        if (highEnd == lowEnd) {
            if (data[lowEnd] == item) {
                return lowEnd;
            } else {
                return -1;
            }
        }

        if (data[probe] == item) {
            return probe;
        }

        if (data[probe] < item) {
            lowEnd = probe + 1;
        } else {
            highEnd = probe - 1;
        }
    }
    return -1;
}
```

## 5.总结

在本文中，我们通过示例探索了插值搜索。我们也用Java实现了它。