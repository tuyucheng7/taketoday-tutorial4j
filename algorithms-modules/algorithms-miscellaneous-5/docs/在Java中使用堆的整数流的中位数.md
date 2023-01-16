## 1. 概述

在本教程中，我们将学习如何计算整数流的中值。

我们将通过示例说明问题，然后分析问题，最后用Java实现几个解决方案。

## 2.问题陈述

[中位数](http://www.icoachmath.com/math_dictionary/median.html)是有序数据集的中间值。对于一组整数，小于中位数的元素与大于中位数的元素一样多。

在一个有序的集合中：

-   奇数个整数，中间元素为中位数——在有序集合{5,7,10}中，中位数为7
-   偶数个整数，没有中间元素；中位数计算为两个中间元素的平均值 - 在有序集合{5, 7, 8, 10}中，中位数为(7 + 8) / 2 = 7.5

现在，让我们假设我们正在从数据流中读取整数而不是有限集。我们可以将整数流的中位数定义为 到目前为止读取的整数集的中位数。

让我们将问题陈述形式化。给定一个整数流的输入，我们必须设计一个类来为我们读取的每个整数执行以下两个任务：

1.  将整数添加到整数集
2.  找到到目前为止读取的整数的中位数

例如：

```plaintext
add 5         // sorted-set = { 5 }, size = 1
get median -> 5

add 7         // sorted-set = { 5, 7 }, size = 2 
get median -> (5 + 7) / 2 = 6

add 10        // sorted-set = { 5, 7, 10 }, size = 3 
get median -> 7

add 8         // sorted-set = { 5, 7, 8, 10 }, size = 4 
get median -> (7 + 8) / 2 = 7.5
..

```

尽管流是非有限的，但我们可以假设我们可以一次将流的所有元素保存在内存中。

我们可以将我们的任务表示为代码中的以下操作：

```java
void add(int num);

double getMedian();

```

## 3. 天真的方法

### 3.1. 排序列表

让我们从一个简单的想法开始——我们可以通过索引访问列表的中间元素或中间两个元素来计算排序的整数列表的中位数。getMedian 操作的时间复杂度为O(1)。

添加新整数时，我们必须确定它在列表中的正确位置，以便列表 保持排序。此操作可以在O(n) 时间内执行，其中n是列表的大小。因此，将新元素添加到列表并计算新中位数的总成本为O(n)。

### 3.2. 改进朴素的方法

添加操作以线性时间运行，这不是最优的。让我们尝试在本节中解决这个问题。

我们可以将列表拆分为两个排序列表——较小的一半整数按降序排列，较大的一半整数按升序排列。我们可以在适当的一半中添加一个新的整数，这样列表的大小最多相差 1：

```plaintext
if element is smaller than min. element of larger half:
    insert into smaller half at appropriate index
    if smaller half is much bigger than larger half:
        remove max. element of smaller half and insert at the beginning of larger half (rebalance)
else
    insert into larger half at appropriate index:
    if larger half is much bigger than smaller half:
        remove min. element of larger half and insert at the beginning of smaller half (rebalance)

```

[![一半中位数缩放 1](https://www.baeldung.com/wp-content/uploads/2019/12/Halves-Median-scaled-1-1024x304.png)](https://www.baeldung.com/wp-content/uploads/2019/12/Halves-Median-scaled-1.png)

现在，我们可以计算中位数：

```plaintext
if lists contain equal number of elements:
    median = (max. element of smaller half + min. element of larger half) / 2
else if smaller half contains more elements:
    median = max. element of smaller half
else if larger half contains more elements:
    median = min. element of larger half
```

虽然我们只是将加法 操作的时间复杂度提高了一些常数因子，但我们已经取得了进步。

让我们分析一下我们在两个排序列表中访问的元素。在(排序的)添加 操作期间，我们可能会在移动它们时访问每个元素。更重要的是，在重新平衡的添加 操作和getMedian 操作期间，我们分别访问较大和较小一半的最小值和最大值(极值)。

我们可以看到极值是它们各自列表的第一个元素。因此，我们必须针对 每一半优化访问索引0处的元素，以改进添加操作的整体运行时间。

## 4.基于堆的方法

让我们通过应用我们从天真的方法中学到的知识来完善我们对问题的理解：

1.  我们必须在O(1) 时间内获得数据集的最小/最大元素
2.  只要我们能够有效地获得最小/最大元素，元素就不必保持排序顺序
3.  我们需要找到一种方法来向我们的数据集添加一个元素，其成本低于O(n) 时间

接下来，我们将看看帮助我们高效实现目标的堆数据结构。

### 4.1. 堆数据结构

[堆](https://www.baeldung.com/java-heap-sort#heap-data-structure)是一种数据结构，通常用数组来实现，但可以认为是二叉树。

[![最小最大堆缩放 1](https://www.baeldung.com/wp-content/uploads/2019/12/Min-Max-Heap-scaled-1-1024x461.png)](https://www.baeldung.com/wp-content/uploads/2019/12/Min-Max-Heap-scaled-1.png)

堆受堆属性约束：

#### 4.1.1. Max – heap 属性

(子)节点的值不能大于其父节点的值。因此，在最大堆中，根节点始终具有最大值。

#### 4.1.2. 最小堆属性

(父)节点的值不能大于其子节点的值。因此，在最小堆中，根节点始终具有最小值。

在Java中，[PriorityQueue](https://algs4.cs.princeton.edu/24pq/) 类代表一个堆。让我们继续使用堆的第一个解决方案。

### 4.2. 第一个解决方案

让我们用两个堆替换我们天真的方法中的列表：

-   包含较大一半元素的最小堆，最小元素位于根部
-   包含较小一半元素的最大堆，最大元素位于根部

现在，我们可以通过将传入的整数与最小堆的根进行比较来将传入的整数添加到相关的一半。接下来，如果在插入之后，一个堆的大小与另一个堆的大小相差超过 1，我们可以重新平衡堆，从而保持最多 1 的大小差异：

```plaintext
if size(minHeap) > size(maxHeap) + 1:
    remove root element of minHeap, insert into maxHeap
if size(maxHeap) > size(minHeap) + 1:
    remove root element of maxHeap, insert into minHeap
```

使用这种方法，如果两个堆的大小相等，我们可以将中位数计算为两个堆的根元素的平均值。否则，元素较多的堆的根元素就是中位数。

我们将使用 PriorityQueue类来表示堆。PriorityQueue的默认堆属性是最小堆。我们可以使用Comparator.reverserOrder创建一个最大堆，它使用自然顺序的反转：

```java
class MedianOfIntegerStream {

    private Queue<Integer> minHeap, maxHeap;

    MedianOfIntegerStream() {
        minHeap = new PriorityQueue<>();
        maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
    }

    void add(int num) {
        if (!minHeap.isEmpty() && num < minHeap.peek()) {
            maxHeap.offer(num);
            if (maxHeap.size() > minHeap.size() + 1) {
                minHeap.offer(maxHeap.poll());
            }
        } else {
            minHeap.offer(num);
            if (minHeap.size() > maxHeap.size() + 1) {
                maxHeap.offer(minHeap.poll());
            }
        }
    }

    double getMedian() {
        int median;
        if (minHeap.size() < maxHeap.size()) {
            median = maxHeap.peek();
        } else if (minHeap.size() > maxHeap.size()) {
            median = minHeap.peek();
        } else {
            median = (minHeap.peek() + maxHeap.peek()) / 2; 
        }
        return median;
    }
}
```

在分析我们代码的运行时间之前，先看看我们用过的堆操作的时间复杂度：

```plaintext
find-min/find-max        O(1)    

delete-min/delete-max    O(log n)

insert                   O(log n)

```

因此，getMedian 操作可以在O(1) 时间内执行，因为它只需要find-min和find-max 函数。添加 操作的时间复杂度为O(log n) – 三个 插入/删除 调用，每个调用都需要O(log n) 时间。

### 4.3. 堆大小不变的解决方案

在我们之前的方法中，我们将每个新元素与堆的根元素进行比较。让我们探索另一种使用堆的方法，在这种方法中，我们可以利用堆属性在适当的一半中添加新元素。

正如我们为之前的解决方案所做的那样，我们从两个堆开始——一个最小堆和一个最大堆。接下来，让我们引入一个条件：最大堆的大小必须始终为 (n / 2)，而最小堆的大小可以是 (n / 2)或(n / 2) + 1，取决于两个堆中元素的总数。换句话说，当元素总数为奇数时，我们可以只允许最小堆有一个额外的元素。

由于我们的堆大小不变，如果两个堆的大小都是(n / 2) ，我们可以将中位数计算为两个堆的根元素的平均值。否则，最小堆的根元素是中位数。

当我们添加一个新的整数时，我们有两种情况：

```plaintext
1. Total no. of existing elements is even
   size(min-heap) == size(max-heap) == (n / 2)

2. Total no. of existing elements is odd
   size(max-heap) == (n / 2)
   size(min-heap) == (n / 2) + 1

```

我们可以通过将新元素添加到其中一个堆并每次重新平衡来保持不变性：

[![堆解决方案](https://www.baeldung.com/wp-content/uploads/2019/12/Heap-Solution-1024x345.png)](https://www.baeldung.com/wp-content/uploads/2019/12/Heap-Solution.png)

重新平衡通过将最大元素从最大堆移动到最小堆，或通过将最小元素从最小堆移动到最大堆来进行。这样，虽然我们在将新整数添加到堆之前没有比较它，但随后的重新平衡确保我们遵守 smaller 和 larger halves 的基础不变性。

让我们使用PriorityQueues在Java中实现我们的解决方案 ：

```java
class MedianOfIntegerStream {

    private Queue<Integer> minHeap, maxHeap;

    MedianOfIntegerStream() {
        minHeap = new PriorityQueue<>();
        maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
    }

    void add(int num) {
        if (minHeap.size() == maxHeap.size()) {
            maxHeap.offer(num);
            minHeap.offer(maxHeap.poll());
        } else {
            minHeap.offer(num);
            maxHeap.offer(minHeap.poll());
        }
    }

    double getMedian() {
        int median;
        if (minHeap.size() > maxHeap.size()) {
            median = minHeap.peek();
        } else {
            median = (minHeap.peek() + maxHeap.peek()) / 2;
        }
        return median;
    }
}
```

我们操作的时间复杂度保持不变：getMedian 花费 O(1) 时间，而 add 在时间 O(log n) 中运行，操作次数完全相同。

两种基于堆的解决方案都提供相似的空间和时间复杂性。虽然第二个解决方案很聪明并且实现更简洁，但该方法并不直观。另一方面，第一个解决方案自然地遵循我们的直觉，并且更容易推理其添加操作的正确性。

## 5. 总结

在本教程中，我们学习了如何计算整数流的中值。我们评估了一些方法并使用PriorityQueue在Java中实现了几个不同的解决方案。