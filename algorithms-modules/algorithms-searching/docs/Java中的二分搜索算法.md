## 1. 概述

在本文中，我们将介绍[二分搜索相对于简单线性搜索的](https://www.baeldung.com/cs/linear-search-vs-binary-search)优势，并逐步介绍它在Java中的实现。

## 2. 需要高效搜索

假设我们从事葡萄酒销售业务，每天有数百万买家访问我们的应用程序。

通过我们的应用程序，客户可以过滤掉价格低于n美元的商品，从搜索结果中选择一瓶，然后将它们添加到他的购物车中。我们每秒钟都有数百万用户在寻找限价酒。结果需要很快。

在后端，我们的算法对整个葡萄酒列表进行线性搜索，将客户输入的价格限制与列表中每个酒瓶的价格进行比较。

然后，它返回价格小于或等于价格限制的项目。此线性搜索的时间复杂度为O(n)。

这意味着我们系统中的酒瓶数量越多，花费的时间就越多。搜索时间与引入的新项目数量成正比。

如果我们开始按排序顺序保存项目并使用二分搜索搜索项目，我们可以实现O(log n)的复杂性。

使用二进制搜索，搜索结果所花费的时间自然会随着数据集的大小而增加，但不成比例。

## 3.二分查找

简单地说，该算法将键值与数组的中间元素进行比较；如果它们不相等，则删除不能包含密钥的那一半，并继续搜索剩余的一半，直到成功为止。

请记住——这里的关键方面是数组已经排序。

如果搜索结束时剩余的一半为空，则该键不在数组中。

### 3.1. 迭代实现

```java
public int runBinarySearchIteratively(
  int[] sortedArray, int key, int low, int high) {
    int index = Integer.MAX_VALUE;
    
    while (low <= high) {
        int mid = low  + ((high - low) / 2);
        if (sortedArray[mid] < key) {
            low = mid + 1;
        } else if (sortedArray[mid] > key) {
            high = mid - 1;
        } else if (sortedArray[mid] == key) {
            index = mid;
            break;
        }
    }
    return index;
}
```

runBinarySearchIteratively方法采用sortedArray、键以及sortedArray的低索引和高索引作为参数。当该方法第一次运行时， sortedArray的第一个索引low为 0，而sortedArray的最后一个索引high等于其长度 – 1。

中间是sortedArray的中间索引。现在该算法运行一个while循环，将键与sortedArray的中间索引的数组值进行比较。

注意中间索引是如何生成的(int mid = low + ((high – low) / 2)。这是为了适应非常大的数组。如果中间索引只是通过获取中间索引(int mid = (low + high) / 2) ，对于包含 2 30 个或更多元素的数组可能会发生溢出，因为low + high的总和很容易超过最大正int值。

### 3.2. 递归实现

现在，让我们再看看一个简单的递归实现：

```java
public int runBinarySearchRecursively(
  int[] sortedArray, int key, int low, int high) {
    int middle = low  + ((high - low) / 2);
        
    if (high < low) {
        return -1;
    }

    if (key == sortedArray[middle]) {
        return middle;
    } else if (key < sortedArray[middle]) {
        return runBinarySearchRecursively(
          sortedArray, key, low, middle - 1);
    } else {
        return runBinarySearchRecursively(
          sortedArray, key, middle + 1, high);
    }
}

```

runBinarySearchRecursively方法接受sortedArray、键、sortedArray的低索引和高索引。

### 3.3. 使用数组。二元搜索() 

```java
int index = Arrays.binarySearch(sortedArray, key);

```

要在整数数组中搜索的 sortedArray和int key作为参数传递给Java Arrays类的binarySearch方法。

### 3.4. 使用集合。二元搜索()

```java
int index = Collections.binarySearch(sortedList, key);

```

要在Integer对象列表中搜索的 sortedList和Integer key作为参数传递给Java Collections类的binarySearch方法。

### 3.5. 表现

使用递归还是迭代方法来编写算法主要是个人喜好问题。但仍然有几点我们应该注意：

1. 由于维护堆栈的开销，递归可能会变慢，并且通常会占用更多内存
2. 递归对堆栈不友好。处理大数据集时可能会导致StackOverflowException
3. 递归增加了代码的清晰度，因为它比迭代方法更短

理想情况下，与线性搜索相比，二分搜索将执行较少数量的比较以获取较大的 n 值。对于较小的 n 值，线性搜索可以比二进制搜索执行得更好。

应该知道这一分析是理论上的，可能会因上下文而异。

此外，二分搜索算法需要一个排序的数据集，这也有其成本。如果我们使用合并排序算法对数据进行排序，我们的代码会增加n log n的额外复杂性。

因此，首先我们需要很好地分析我们的需求，然后决定哪种搜索算法最适合我们的需求。

## 4. 总结

本教程演示了一个二分搜索算法的实现，以及一个更适合使用它而不是线性搜索的场景。