## 1. 简介

在本教程中，我们将描述Java中的 Shell 排序算法。

## 2. Shell排序概述

### 2.1. 描述

让我们首先描述 Shell 排序算法，这样我们就知道我们要实现的是什么。

Shell 排序基于[插入排序算法](https://www.baeldung.com/java-insertion-sort)，它属于非常高效的算法组。通常，该算法将原始集合分成更小的子集，然后使用插入排序对每个子集进行排序。

但是，它如何制作子集并不简单。它不会像我们预期的那样选择相邻元素来形成子集。相反，shell 排序使用所谓的区间或间隙来创建子集。例如，如果我们有间隙I，则意味着一个子集将包含I位置分开的元素。

首先，该算法对彼此远离的元素进行排序。然后，间隙变得更小，并且比较更近的元素。这样，一些不在正确位置的元素可以比我们从相邻元素中创建子集更快地定位。

### 2.2. 一个例子

让我们在示例中看到这个，间隔为 3 和 1，以及 9 个元素的未排序列表：

[![9 个元素的未排序列表](https://www.baeldung.com/wp-content/uploads/2019/07/Screen-Shot-2019-07-22-at-11.10.24-PM.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screen-Shot-2019-07-22-at-11.10.24-PM.png)

如果我们按照上面的描述，在第一次迭代中，我们将拥有三个包含 3 个元素的子集(用相同的颜色突出显示)：

[![具有 3 个元素的三个子集](https://www.baeldung.com/wp-content/uploads/2019/07/Screen-Shot-2019-07-22-at-11.12.28-PM.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screen-Shot-2019-07-22-at-11.12.28-PM.png)

在第一次迭代中对每个子集进行排序后，列表将如下所示：

[![在第一次迭代中对每个子集进行排序](https://www.baeldung.com/wp-content/uploads/2019/07/Screen-Shot-2019-07-22-at-11.13.29-PM.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screen-Shot-2019-07-22-at-11.13.29-PM.png)

我们可以注意到，虽然我们还没有排序列表，但元素现在更接近所需的位置。

最后，我们还需要做一个增量为 1 的排序，这实际上是一个基本的插入排序。现在，我们需要执行的对列表进行排序的移位操作的数量比我们不进行第一次迭代时的情况要少：

[![另一种](https://www.baeldung.com/wp-content/uploads/2019/07/Screen-Shot-2019-07-22-at-11.43.27-PM.png)](https://www.baeldung.com/wp-content/uploads/2019/07/Screen-Shot-2019-07-22-at-11.43.27-PM.png)

### 2.3. 选择间隙序列

正如我们提到的，shell 排序有一种独特的选择空位序列的方式。这是一项艰巨的任务，我们应该注意不要选择太少或太多的差距。更多细节可以在[最推荐的缺口序列列表](https://en.wikipedia.org/wiki/Shellsort#Gap_sequences)中找到。

## 3.实施

现在让我们看一下实现。我们将使用 Shell 的原始序列进行间隔增量：

```plaintext
N/2, N/4, …, 1 (continuously dividing by 2)
```

实现本身并不太复杂：

```java
public void sort(int arrayToSort[]) {
    int n = arrayToSort.length;

    for (int gap = n / 2; gap > 0; gap /= 2) {
        for (int i = gap; i < n; i++) {
            int key = arrayToSort[i];
            int j = i;
            while (j >= gap && arrayToSort[j - gap] > key) {
                arrayToSort[j] = arrayToSort[j - gap];
                j -= gap;
            }
            arrayToSort[j] = key;
        }
    }
}
```

我们首先使用 for 循环创建一个空位序列，然后对每个空位大小进行插入排序。

现在，我们可以轻松地测试我们的方法：

```java
@Test
public void givenUnsortedArray_whenShellSort_thenSortedAsc() {
    int[] input = {41, 15, 82, 5, 65, 19, 32, 43, 8};
    ShellSort.sort(input);
    int[] expected = {5, 8, 15, 19, 32, 41, 43, 65, 82};
    assertArrayEquals("the two arrays are not equal", expected, input);
}
```

## 4. 复杂性

通常，Shell 排序算法对于中等大小的列表非常有效。复杂度很难确定，因为它在很大程度上取决于间隙序列，但时间复杂度在O(N)和O(N^2)之间变化。

最坏情况下的空间复杂度为O(N)， 辅助空间为O(1) 。

## 5.总结

在本教程中，我们描述了 Shell 排序并说明了我们如何在Java中实现它。