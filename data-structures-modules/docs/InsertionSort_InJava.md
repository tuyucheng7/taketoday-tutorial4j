## 1. 概述

在本文中，我们将讨论插入排序算法并使用Java实现。

插入排序是一种对少量元素项进行排序的有效算法。此方法基于对纸牌玩家对手上的扑克牌调整位置的方式。

我们从空的左手开始，牌放在桌子上。然后我们每次从桌子上取出一张牌并将其插入左手的正确位置。
为了找到一张新牌的正确位置，我们将它与手中已经整理好的牌序进行比较，从右到左。

让我们从了解伪代码形式的算法步骤开始。

## 2. 伪代码

我们将把插入排序的伪代码呈现为一个称为INSERTION-SORT的过程，将n个要排序的项的数组A[1 .. n]作为参数。
该算法对输入数组进行就地排序(通过在数组A中重新排列元素)。

过程完成后，输入数组A包含输入数组的数字，但按顺序排序：

```
INSERTION-SORT(A)

for i = 2 to A.length
    key = A[i]
    j = i - 1 
    while j > 0 and A[j] > key
        A[j+1] = A[j]
        j = j - 1
    A[j + 1] = key
```

让我们简要浏览一下上面的算法。

索引i指示当前项在要处理的数组中的位置。

我们从第2项开始，因为根据定义，一个包含一个元素的数组被认为是已排序的。索引i处的元素称为key。
一旦有了key，算法的第二部分就会处理找到它的正确索引。如果key小于索引j处的元素，则key向左移动一个位置。
这个过程一直持续到我们到达一个小于key的元素的情况。

需要注意的是，在开始迭代以查找索引i处key的正确位置之前，数组A[1..j–1]已经排序。

## 3. 强制性实现

对于命令式情况，我们将编写一个名为insertionSortImperative的方法，将整数数组作为参数。方法从第二项开始迭代数组。

在迭代过程中的任何给定时间，我们都可以认为这个数组在逻辑上分为两部分；左侧是已排序的元素，右侧包含尚未排序的元素。

这里需要注意的一点是，在找到插入新元素的正确位置后，我们将元素向右移动(而不是交换)以释放一个空间。

```
public static void insertionSortImperative(int[] input) {
  for (int i = 1; i < input.length; i++) {
    int key = input[i];
    int j = i - 1;
    while (j >= 0 && input[j] > key) {
      input[j + 1] = input[j];
      j = j - 1;
    }
    input[j + 1] = key;
  }
}
```

接下来，让我们为上述方法创建一个测试：

```
@Test
void givenUnsortedArray_whenInsertionSortImperative_thenSortedAsc() {
  int[] input = {6, 2, 3, 4, 5, 1};
  InsertionSort.insertionSortImperative(input);
  int[] expected = {1, 2, 3, 4, 5, 6};
  assertArrayEquals(expected, input, "the two arrays are not equal");
}
```

上面的测试证明了算法对输入数组<6,2,3,4,5,1>的升序排序正确。

## 4. 递归实现

递归情况下的方法名为insertionSortRecursive，并接收整数数组作为输入(与命令式情况相同)。

这里与命令式情况(尽管它是递归的)的区别在于它调用一个重载方法，第二个参数等于要排序的项数。

由于我们要对整个数组进行排序，我们将传递与其长度相等的项数：

```
public static void insertionSortRecursive(int[] input) {
  insertionSortRecursive(input, input.length);
}
```

递归情况更具挑战性。当我们尝试对包含一个元素的数组进行排序时，就会发生基准情况。在这种情况下，我们什么也不做。

所有后续的递归调用对输入数组的预定义部分进行排序--从第二项开始直到我们到达数组的末尾：

```
private static void insertionSortRecursive(int[] input, int i) {
  if (i <= 1) { // base case(基准条件)
    return;
  }
  insertionSortRecursive(input, i - 1); // sort the first i - 1 elements of the array.
  int key = input[i - 1];
  int j = i - 2;
  while (j >= 0 && input[j] > key) {
    input[j + 1] = input[j];
    j = j - 1;
  }
  input[j + 1] = key;
}
```

这就是6个元素输入数组的调用堆栈的样子：

```
insertionSortRecursive(input, 6)
insertionSortRecursive(input, 5) and insert the 6th item into the sorted array
insertionSortRecursive(input, 4) and insert the 5th item into the sorted array
insertionSortRecursive(input, 3) and insert the 4th item into the sorted array
insertionSortRecursive(input, 2) and insert the 3rd item into the sorted array
insertionSortRecursive(input, 1) and insert the 2nd item into the sorted array
```

让我们看看它的测试：

```
@Test
void givenUnsortedArray_whenInsertionSortRecursive_thenSortedAsc() {
  int[] input = {6, 4, 5, 2, 3, 1};
  InsertionSort.insertionSortRecursive(input);
  int[] expected = {1, 2, 3, 4, 5, 6};
  assertArrayEquals(expected, input, "the two arrays are not equal");
}
```

上面的测试证明了算法对输入数组<6,2,3,4,5,1>的升序排序正确。

## 5. 时间和空间复杂度

INSERTION-SORT过程的运行时间为O(n^2)。对于每个新元素，我们从右到左遍历数组的已排序部分以找到其正确位置。然后我们通过将元素向右移动一个位置来插入它。

该算法进行了适当的排序，因此对于命令式实现，其空间复杂度为O(1)，对于递归实现，其空间复杂度为O(n)。

## 6. 总结

在本文中，我们了解了如何实现插入排序。

此算法对于排序少量元素非常有用。当对超过100个元素的输入序列进行排序时，效率会变得很低。

请记住，尽管它具有二次方复杂度，但它可以在不需要辅助空间的情况下进行排序。