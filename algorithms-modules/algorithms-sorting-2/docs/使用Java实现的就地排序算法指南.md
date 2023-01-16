## 1. 简介

在本教程中，我们将解释就地排序算法的工作原理。

## 2.就地算法

就地算法是那些不需要任何辅助数据结构来转换输入数据的算法。基本上，这意味着该算法不会为输入操作使用额外的空间。它实际上用输出覆盖了输入。

然而，在现实中，该算法实际上可能需要一个小的和非常量的附加空间用于辅助变量。这个空间的复杂度在大多数情况下是O(log n)，尽管有时任何低于线性的都是允许的。

## 3.伪代码

现在让我们看一些伪代码，并将就地算法与非就地算法进行比较。

我们假设我们想要反转一个包含n 个数字的数组。

### 3.1. 就地算法

如果我们考虑这个问题，我们会看到我们有一个输入数组和一个反向数组作为输出。最后，我们实际上并不需要我们的原始数组，只需要反转的数组。

那么，为什么我们不覆盖输入而不是将它的值移动到全新的数组，因为它可能看起来像一个最明显的方法？为此，我们只需要一个额外的变量来临时存储我们当前正在使用的值：

```plaintext
reversInPlace(array A[n])
    for i from 0 to n/2
    temp = A[i]
    A[i] = A[n - 1 - i]
    A[n - 1 - i] = temp
```

值得注意的是，无论数组有多大，在这种情况下我们需要的额外空间总是O(1) 。

插图显示我们需要的步骤比前一种情况少：

[![截图 2019-08-07-at-3.40.33-PM](https://www.baeldung.com/wp-content/uploads/2019/08/Screen-Shot-2019-08-07-at-3.40.33-PM.png)](https://www.baeldung.com/wp-content/uploads/2019/08/Screen-Shot-2019-08-07-at-3.40.33-PM.png)

### 3.2. 异地算法

另一方面，我们也可以用一种非常简单、更明显的方式来做到这一点。我们可以创建一个相同大小的新数组，按相应顺序从原始数组中值，然后删除原始数组：

```plaintext
reverseOutOfPlace(array A[n])
    create new array B[n]
    for i from 0 to n - 1
        B[i] = A[i]
    delete A
    return B
```

虽然这会做我们想做的事，但效率不够。我们需要O(n)的额外空间， 因为我们有两个数组来操作. 除此之外，创建和删除新数组通常是一个缓慢的操作。

让我们看一下过程的图示：

[![截图 2019-08-07-at-3.40.22-PM](https://www.baeldung.com/wp-content/uploads/2019/08/Screen-Shot-2019-08-07-at-3.40.22-PM.png)](https://www.baeldung.com/wp-content/uploads/2019/08/Screen-Shot-2019-08-07-at-3.40.22-PM.png)

## 4.Java实现

现在让我们看看如何用Java实现我们在上一节中学到的内容。

首先，我们将实现一个就地算法：

```java
public static int[] reverseInPlace(int A[]) {
    int n = A.length;
    for (int i = 0; i < n / 2; i++) {
        int temp = A[i];
        A[i] = A[n - 1 - i];
        A[n - 1 - i] = temp;
    }
    return A;
}
```

我们可以很容易地测试它是否按预期工作：

```java
@Test
public void givenArray_whenInPlaceSort_thenReversed() {
    int[] input = {1, 2, 3, 4, 5, 6, 7};
    int[] expected = {7, 6, 5, 4, 3, 2, 1};
    assertArrayEquals("the two arrays are not equal", expected,
      InOutSort.reverseInPlace(input));
}
```

其次，让我们检查一下异地算法的实现：

```java
public static int[] reverseOutOfPlace(int A[]) {
    int n = A.length;
    int[] B = new int[n];
    for (int i = 0; i < n; i++) {
        B[n - i - 1] = A[i];
    }
    return B;
}
```

测试非常简单：

```java
@Test
public void givenArray_whenOutOfPlaceSort_thenReversed() {
    int[] input = {1, 2, 3, 4, 5, 6, 7};
    int[] expected = {7, 6, 5, 4, 3, 2, 1};
    assertArrayEquals("the two arrays are not equal", expected,
      InOutSort.reverseOutOfPlace(input));
}
```

## 5.例子

有许多使用就地方法的排序算法。其中一些是[插入排序](https://www.baeldung.com/java-insertion-sort)、[冒泡排序](https://www.baeldung.com/java-bubble-sort)、[堆排序](https://www.baeldung.com/java-heap-sort)、[快速排序](https://www.baeldung.com/java-quicksort)和[shell 排序](https://www.baeldung.com/java-shell-sort)，你可以了解更多关于它们的信息并查看它们的Java实现。

另外，我们需要提到梳排序和堆排序。所有这些都具有空间复杂度O(log n)。

进一步了解[大 O 表示法理论](https://www.baeldung.com/big-o-notation)以及查看一些[有关算法复杂性的实用Java示例](https://www.baeldung.com/java-algorithm-complexity)也可能很有用。

## 六. 总结

在本文中，我们描述了所谓的就地算法，使用伪代码和一些示例说明了它们的工作原理，列出了几种基于此原理的算法，并最终在Java中实现了基本示例。