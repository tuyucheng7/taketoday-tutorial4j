## 1. 稳定性概念

如果具有相等或相同元素的两个对象在排序输出中的顺序与它们在要排序的输入数组中的顺序相同，则称排序算法是稳定的。

任何本质上不稳定的基于比较的排序算法都可以通过更改键比较操作来修改为稳定，以便两个键的比较将位置作为具有相同键的对象的一个因素，或者通过调整它的方式使其意义不会改变，它也会变得稳定。

示例：

下标仅用于理解概念。

> 输入：4<sub>A</sub>  5 3 2 4<sub>B</sub>  1

> 输出：1 2 3 4<sub>B</sub>  4<sub>A</sub>  5

稳定的选择排序产生以下结果：

> 输出：1 2 3 4<sub>A</sub>  4<sub>B</sub>  5

## 2. 算法思想

选择排序的工作原理是找到最小元素，然后通过与位于该最小元素位置的元素交换，将其插入正确的位置。这就是它不稳定的原因。

交换可能会影响将元素(假设A)推到大于等于相同元素的元素(假设B)的位置。这使它们失去了所需的顺序。

在上述示例中，4<sub>A</sub>在4<sub>B</sub>之后推送，完成排序后，4<sub>A</sub>保留在该4<sub>B</sub>之后。从而导致不稳定。

如果不是交换，而是将最小元素放置在其位置而不交换，即通过向前推动每个元素一步将数字放置在其位置，则选择排序可以变得稳定。

简单来说，使用插入排序之类的技术，这意味着将元素插入到正确的位置。

举例说明：

> 4<sub>A</sub>  5 3 2 4<sub>B</sub>  1

第一个最小元素为1，现在改为交换。在正确的位置插入1将每个元素向前推进一步。

> 1 4<sub>A</sub> 5 3 2 4<sub>B</sub>

下一个最小值为2：

> 1 2 4<sub>A</sub> 5 3 4<sub>B</sub>

下一个最小值为3：

> 1 2 3 4<sub>A</sub> 5 4<sub>B</sub>

重复这些步骤，直到数组已排序。

> 1 2 3 4<sub>A</sub> 4<sub>B</sub> 5

## 3. 算法实现

```java
public class SelectionSort {

  public static void stableSort(int[] arr) {
    int n = arr.length;
    for (int i = 0; i < n - 1; i++) {
      int minIndex = i;
      for (int j = i + 1; j < n; j++) {
        if (arr[j] < arr[minIndex])
          minIndex = j;
      }
      int key = arr[minIndex];
      while (minIndex > i) {
        arr[minIndex] = arr[minIndex - 1];
        minIndex--;
      }
      arr[i] = key;
    }
  }
}
```