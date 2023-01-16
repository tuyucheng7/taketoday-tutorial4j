## 1. 概述

在本教程中，我们将讨论插入排序算法并了解其Java实现。

插入排序是一种对少量项目进行排序的有效算法。这种方法是基于纸牌玩家对一手扑克牌进行排序的方式。

我们从空着的左手和放在桌子上的牌开始。然后，我们一次从桌子上取出一张牌，并将其插入左手的正确位置。为了找到一张新牌的正确位置，我们将它与手中已经排序的牌组从右到左进行比较。

让我们从了解伪代码形式的算法步骤开始。

## 2.伪代码

我们将把我们的插入排序伪代码作为一个名为INSERTION-SORT的过程来呈现，将要排序的 n 个项目的数组A[1 .. n]作为参数。该算法就地对输入数组进行排序 (通过重新排列数组 A 中的项目)。

该过程完成后，输入数组 A 包含输入序列的排列但按排序顺序排列：

```plaintext
INSERTION-SORT(A)

for i=2 to A.length
    key = A[i]
    j = i - 1 
    while j > 0 and A[j] > key
        A[j+1] = A[j]
        j = j - 1
    A[j + 1] = key
```

让我们简要回顾一下上面的算法。

索引i表示要处理的数组中当前项的位置。

我们从第二项开始，因为根据定义，具有一项的数组被认为是已排序的。索引 i处的项目称为key。一旦有了密钥， 算法的第二部分就是寻找正确的索引。如果键小于索引 j处的项的值，则键向左移动一个位置。这个过程一直持续到我们到达一个小于键的元素的情况。

重要的是要注意，在开始迭代以找到索引 i处键的正确位置之前，数组A[1 .. j – 1]已经排序。

## 3.命令式实现

对于命令式情况，我们将编写一个名为insertionSortImperative的函数，将一个整数数组作为参数。该函数从第二项开始遍历数组。

在迭代过程中的任何给定时间，我们都可以认为这个数组在逻辑上分为两部分；左侧是已排序的，右侧包含尚未排序的项目。

这里需要注意的是，在找到插入新项目的正确位置后，我们将项目向右移动(而不是交换)以为其腾出空间。

```java
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

接下来，让我们为上面的方法创建一个测试：

```java
@Test
public void givenUnsortedArray_whenInsertionSortImperative_thenSortedAsc() {
    int[] input = {6, 2, 3, 4, 5, 1};
    InsertionSort.insertionSortImperative(input);
    int[] expected = {1, 2, 3, 4, 5, 6};
    assertArrayEquals("the two arrays are not equal", expected, input);
}
```

上面的测试证明算法正确地按输入数组<6, 2, 3, 4, 5, 1>的升序排序。

## 4.递归实现

递归情况下的函数称为insertionSortR ecursive 并接受整数数组作为输入(与命令式情况相同)。

这里与命令式情况的区别(尽管它是递归的)是它调用一个重载函数，第二个参数等于要排序的项目数。

因为我们想要对整个数组进行排序，所以我们将传递一些等于其长度的项目：

```java
public static void insertionSortRecursive(int[] input) {
    insertionSortRecursive(input, input.length);
}
```

递归的情况更具挑战性。当我们试图用一个项目对数组进行排序时，就会出现基本情况。在这种情况下，我们什么都不做。

所有后续的递归调用都会对输入数组的预定义部分进行排序——从第二项开始直到我们到达数组的末尾：

```java
private static void insertionSortRecursive(int[] input, int i) {
    if (i <= 1) {
        return;
    }
    insertionSortRecursive(input, i - 1);
    int key = input[i - 1];
    int j = i - 2;
    while (j >= 0 && input[j] > key) {
        input[j + 1] = input[j];
        j = j - 1;
    }
    input[j + 1] = key;
}
```

这是包含 6 个项目的输入数组的调用堆栈的样子：

```plaintext
insertionSortRecursive(input, 6)
insertionSortRecursive(input, 5) and insert the 6th item into the sorted array
insertionSortRecursive(input, 4) and insert the 5th item into the sorted array
insertionSortRecursive(input, 3) and insert the 4th item into the sorted array
insertionSortRecursive(input, 2) and insert the 3rd item into the sorted array
insertionSortRecursive(input, 1) and insert the 2nd item into the sorted array
```

让我们也看看它的测试：

```java
@Test
public void givenUnsortedArray_whenInsertionSortRecursively_thenSortedAsc() {
    int[] input = {6, 4, 5, 2, 3, 1};
    InsertionSort.insertionSortRecursive(input);
    int[] expected = {1, 2, 3, 4, 5, 6};
    assertArrayEquals("the two arrays are not equal", expected, input);
}
```

上面的测试证明算法正确地按输入数组<6, 2, 3, 4, 5, 1>的升序排序。

## 5. 时空复杂度

INSERTION-SORT过程运行所花费的时间 是O(n^2)。对于每个新项目，我们从右到左遍历数组的已排序部分以找到其正确位置。然后我们通过将项目向右移动一个位置来插入它。

该算法就地排序，因此其空间复杂度对于命令式实现为O (1) ，对于递归实现为O(n)。

## 六. 总结

在本教程中，我们了解了如何实现插入排序。

该算法对于对少量项目进行排序很有用。当对超过 100 个项目的输入序列进行排序时，它变得低效。 

请记住，尽管它具有二次复杂性，但它在不需要辅助空间的情况下就地排序，就像合并排序的情况一样。