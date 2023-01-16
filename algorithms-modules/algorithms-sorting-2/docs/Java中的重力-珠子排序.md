## 1. 简介

在本教程中，我们将讨论[重力排序](https://www.baeldung.com/cs/gravity-sort)算法及其在Java中的单线程实现。

## 2.算法

重力排序是一种自然排序算法，其灵感来自于自然事件——在本例中为重力。也称为 Bead Sort，此算法模拟重力对正整数列表进行排序。

该算法的思想是使用垂直杆和水平水平的珠子来表示正整数——类似于算盘，除了每个水平代表输入列表的单个数字。下一步是将珠子放到尽可能低的位置，这会导致算盘的数字按升序排列：

例如，下面是对 [4, 2] 的输入列表进行排序的过程：

[![img](https://www.baeldung.com/wp-content/uploads/2022/10/1_Gravity-Sort-in-Java-Diagram-2.png)](https://www.baeldung.com/wp-content/uploads/2022/10/1_Gravity-Sort-in-Java-Diagram-2.png)

我们对算盘施加重力。随后，所有珠子落下后都将处于尽可能低的位置。算盘的结果状态现在是正整数从上到下的升序。

实际上，重力同时使所有珠子下落。然而，在软件中，我们必须模拟珠子在不同的迭代中落下。接下来，我们将看看如何将算盘表示为二维数组，以及如何模拟下落的珠子对列表中的数字进行排序。

## 3.实施

要在软件中实现引力排序，我们将按照[本文](https://www.baeldung.com/cs/gravity-sort)中的伪代码用Java 编写代码。

首先，我们需要将输入列表转换为算盘。我们将使用二维数组来表示杆(列)和水平(行)作为矩阵的维度。此外，我们将使用true或false分别表示珠子或空单元格。

在设置我们的算盘之前，让我们找出矩阵的维度。列数m等于列表中的最大元素。因此，让我们创建一个方法来查找这个数字：

```java
static int findMax(int[] A) {
    int max = A[0];
    for (int i = 1; i < A.length; i++) {
        if (A[i] > max) {
            max = A[i];
        }
    }
    return max;
}
```

现在，我们可以将最大的数字分配给m：

```java
int[] A = {1, 3, 4, 2};
int m = findMax(A);
```

使用m，我们现在能够创建算盘的表示。我们将使用setupAbacus()方法来执行此操作：

```java
static boolean[][] setupAbacus(int[] A, int m) {
    boolean[][] abacus = new boolean[A.length][m];
    for (int i = 0; i < abacus.length; i++) {
        int number = A[i];
        for (int j = 0; j < abacus[0].length && j < number; j++) {
            abacus[A.length - 1 - i][j] = true;
        }
    }
    return abacus;
}
```

setupAbacus ()方法返回算盘的初始状态。 该方法遍历矩阵中的每个单元格，分配一个珠子或一个空单元格。

在矩阵的每一层，我们将使用列表A中的第i个数字来确定一行中的珠子数。此外，我们遍历每一列j，如果number大于第j列索引，我们将此单元格标记为true以指示珠子。否则，循环提前终止，使第i行的其余单元格为空或为假。

让我们创建算盘：

```java
boolean[][] abacus = setupAbacus(A, m);
```

我们现在准备好让重力通过将珠子放到尽可能低的位置来对珠子进行分类：

```java
static void dropBeads(boolean[][] abacus, int[] A, int m) {
    for (int i = 1; i < A.length; i++) {
        for (int j = m - 1; j >= 0; j--) {
            if (abacus[i][j] == true) {
                int x = i;
                while (x > 0 && abacus[x - 1][j] == false) {
                    boolean temp = abacus[x - 1][j];
                    abacus[x - 1][j] = abacus[x][j];
                    abacus[x][j] = temp;
                    x--;
                }
            }
        }
    }
}
```

最初，dropBeads()方法循环遍历矩阵中的每个单元格。从 1 开始， i 是开始的行，因为不会有任何珠子从最底层的 0 层开始掉落。关于列，我们从j = m – 1开始掉落珠子右到左。

在每次迭代中，我们检查当前单元格abacus[i][j]是否包含珠子。如果是这样，我们使用变量x来存储下落珠子的当前水平。 我们通过减小x 来放下珠子，只要它不是最底层，并且下面的单元格是空白空间。

最后，我们需要将算盘的最终状态转换为排序数组。toSortedList ()方法将算盘作为参数以及原始输入列表，并相应地修改数组：

```java
static void toSortedList(boolean[][] abacus, int[] A) {
    int index = 0;
    for (int i = abacus.length - 1; i >=0; i--) {
        int beads = 0;
        for (int j = 0; j < abacus[0].length && abacus[i][j] == true; j++) {
            beads++;
        }
        A[index++] = beads;
    }
}
```

我们可以回想一下，每行中的珠子数代表列表中的一个数字。出于这个原因，该方法遍历算盘中的每个级别，计算珠子，并将值分配给列表。该方法从最高行值开始按升序设置值。但是，从i = 0 开始，它按降序排列数字。

让我们将算法的所有部分放在一个单一的gravitySort()方法中：

```java
static void gravitySort(int[] A) {
    int m = findMax(A);
    boolean[][] abacus = setupAbacus(A, m);
    dropBeads(abacus, A, m);
    transformToList(abacus, A);
}
```

我们可以通过创建单元测试来确认算法是否有效：

```java
@Test
public void givenIntegerArray_whenSortedWithGravitySort_thenGetSortedArray() {
    int[] actual = {9, 9, 100, 3, 57, 12, 3, 78, 0, 2, 2, 40, 21, 9};
    int[] expected = {0, 2, 2, 3, 3, 9, 9, 9, 12, 21, 40, 57, 78, 100};
    GravitySort.sort(actual);
    Assert.assertArrayEquals(expected, actual);
}
```

## 4. 复杂性分析

我们看到重力排序算法需要大量处理。因此，让我们将其分解为时间和空间复杂度。

### 4.1. 时间复杂度

重力排序算法的实现从找到输入列表中的最大数m开始。这个过程是一个O(n)运行时操作，因为我们只遍历一次数组。一旦我们获得m，我们就设置了算盘的初始状态。因为算盘实际上是一个矩阵，访问每一行和每一列中的每个单元格会导致O(m  n)操作，其中n是行数。

一旦我们的设置准备就绪，我们必须将珠子放到矩阵中尽可能低的位置，就好像重力正在影响我们的算盘一样。同样，我们正在访问矩阵中的每个单元格以及一个内部循环，该循环在每列中最多放置n层珠子。这个过程有一个O(n  n  m)运行时间。

此外，我们必须执行O(n)额外的步骤来根据算盘中的排序表示重新创建我们的列表。

总的来说，重力排序是一个O(n  n  m)算法，考虑到它模拟珠子掉落的努力。

### 4.2. 空间复杂度

重力排序的空间复杂度与输入列表的大小及其最大值成正比。例如，一个包含n个元素且最大数量为 m的列表需要nxm维的矩阵表示。因此，空间复杂度为O(n  m)以在内存中为矩阵分配额外空间。

尽管如此，我们尝试通过用单个位或数字表示珠子和空单元格来最小化空间复杂性。即，1或true表示珠子，类似地，0或false值表示空单元格。

## 5.总结

在本文中，我们学习了如何为重力排序算法实现单线程方法。也称为珠子排序，该算法的灵感来自于重力自然地对我们在算盘中表示为珠子的正整数进行排序。然而，在软件中，我们使用二维矩阵和单位值来重现这种环境。

虽然单线程实现具有代价高昂的时间和空间复杂度，但该算法在硬件和多线程应用程序中表现良好。尽管如此，重力排序算法举例说明了自然事件如何激发软件实现的解决方案。