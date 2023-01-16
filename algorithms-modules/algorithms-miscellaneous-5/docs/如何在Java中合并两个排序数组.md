## 1. 简介

在本教程中，我们将学习如何将两个排序数组合并为一个排序数组。

## 2.问题

让我们理解这个问题。我们有两个排序数组，我们想将它们合并为一个。

![合并排序数组](https://www.baeldung.com/wp-content/uploads/2019/12/Merge-Sorted-Arrays.png)

## 3.算法

当我们分析问题时，很容易观察到我们可以通过使用归并[排序](https://www.baeldung.com/java-merge-sort)的归并操作来解决这个问题。

假设我们有两个排序数组foo和bar，长度分别为fooLength和barLength。接下来，我们可以声明另一个大小为fooLength + barLength的合并数组。

然后我们应该在同一个循环中遍历两个数组。我们将为每个 fooPosition和 barPosition维护一个当前索引值。在我们循环的给定迭代中，我们采用在其索引处具有较小值元素的数组并推进该索引。该元素将占据合并数组中的下一个位置。

最后，一旦我们从一个数组中转移了所有元素，我们将把另一个数组中的剩余元素到合并后的数组中。

现在让我们看看图片中的过程，以便更好地理解算法。

步骤1：

我们首先比较两个数组中的元素，然后选择较小的一个。

![合并数组第一步](https://www.baeldung.com/wp-content/uploads/2019/12/Merge-Step-1.png)

然后我们增加第一个数组中的位置。

第2步：

![合并数组第二步](https://www.baeldung.com/wp-content/uploads/2019/12/Merge-Step-2.png)

在这里，我们增加第二个数组中的位置并移动到下一个元素 8。

第 3 步：
![合并数组第三步](https://www.baeldung.com/wp-content/uploads/2019/12/Merge-Step-3.png)

在本次迭代结束时，我们已经遍历了第一个数组的所有元素。

第4步：

在这一步中，我们只是将第二个数组中的所有剩余元素到result中。

![合并数组第四步](https://www.baeldung.com/wp-content/uploads/2019/12/Merge-Step-4.png)

## 4.实施

现在让我们看看如何实现它：

```java
public static int[] merge(int[] foo, int[] bar) {

    int fooLength = foo.length;
    int barLength = bar.length;

    int[] merged = new int[fooLength + barLength];

    int fooPosition, barPosition, mergedPosition;
    fooPosition = barPosition = mergedPosition = 0;

    while(fooPosition < fooLength && barPosition < barLength) {
        if (foo[fooPosition] < bar[barPosition]) {
            merged[mergedPosition++] = foo[fooPosition++];
        } else {
            merged[mergedPosition++] = bar[barPosition++];
        }
    }

    while (fooPosition < fooLength) {
        merged[mergedPosition++] = foo[fooPosition++];
    }

    while (barPosition < barLength) {
        merged[mergedPosition++] = bar[barPosition++];
    }

    return merged;
}
```

让我们进行一个简短的测试：

```java
@Test
public void givenTwoSortedArrays_whenMerged_thenReturnMergedSortedArray() {

    int[] foo = { 3, 7 };
    int[] bar = { 4, 8, 11 };
    int[] merged = { 3, 4, 7, 8, 11 };

    assertArrayEquals(merged, SortedArrays.merge(foo, bar));
}
```

## 5. 复杂性

我们遍历两个数组并选择较小的元素。最后，我们从foo或bar数组中其余元素。所以时间复杂度变为O(fooLength + barLength)。我们使用辅助数组来获取结果。所以空间复杂度也是O(fooLength + barLength)。

## 六. 总结

在本教程中，我们学习了如何将两个排序数组合并为一个。