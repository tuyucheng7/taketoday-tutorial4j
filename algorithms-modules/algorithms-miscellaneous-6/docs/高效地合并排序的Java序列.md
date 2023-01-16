## 1. 概述

在这个简短的教程中，我们将了解如何使用堆有效地合并排序数组。

## 2.算法

由于我们的问题陈述是使用堆来合并数组，因此我们将使用最小堆来解决我们的问题。最小堆不过是一棵二叉树，其中每个节点的值都小于其子节点的值。

通常，最小堆是使用数组实现的，其中数组在查找节点的父节点和子节点时满足特定规则。

对于数组A[]和索引i处的元素：

-   A[(i-1)/2]将返回其父级
-   A[(2i)+1]将返回左孩子
-   A[(2i)+2]将返回正确的孩子

这是最小堆及其数组表示的图片：

[![最小堆合并](https://www.baeldung.com/wp-content/uploads/2020/01/MinHeapMerge-300x251.png)](https://www.baeldung.com/wp-content/uploads/2020/01/MinHeapMerge.png)

现在让我们创建合并一组排序数组的算法：

1.  创建一个数组来存储结果，其大小由所有输入数组的长度相加决定。
2.  创建第二个大小等于输入数组数量的数组，并用所有输入数组的第一个元素填充它。
3.  通过在所有节点及其子节点上应用最小堆规则，将先前创建的数组转换为最小堆。
4.  重复接下来的步骤，直到结果数组完全填充。
5.  从最小堆中获取根元素并将其存储在结果数组中。
6.  用填充当前根的数组中的下一个元素替换根元素。
7.  在我们的最小堆数组上再次应用最小堆规则。

我们的算法有一个递归流来创建最小堆，我们必须访问输入数组的所有元素。

该算法的时间[复杂度为](https://www.baeldung.com/java-algorithm-complexity)O(k log n)，其中k是所有输入数组中元素的总数， n是已排序数组的总数。

现在让我们看看运行算法后的示例输入和预期结果，以便我们可以更好地理解问题。所以对于这些数组：

```java
{ { 0, 6 }, { 1, 5, 10, 100 }, { 2, 4, 200, 650 } }
```

该算法应返回结果数组：

```java
{ 0, 1, 2, 4, 5, 6, 10, 100, 200, 650 }
```

## 3.Java实现

现在我们对什么是最小堆以及合并算法的工作原理有了基本的了解，让我们看看Java实现。我们将使用两个类——一个代表堆节点，另一个实现合并算法。

### 3.1. 堆节点表示

在实现算法本身之前，让我们创建一个表示堆节点的类。这将存储节点值和两个支持字段：

```java
public class HeapNode {

    int element;
    int arrayIndex;
    int nextElementIndex = 1;

    public HeapNode(int element, int arrayIndex) {
        this.element = element;
        this.arrayIndex = arrayIndex;
    }
}
```

请注意，为了简单起见，我们特意省略了此处的getter和setter 。我们将使用arrayIndex属性来存储当前堆节点元素所在数组的索引。我们将使用nextElementIndex属性来存储在将根节点移动到结果数组后我们将采用的元素的索引。

最初，nextElementIndex的值为1。我们将在替换最小堆的根节点后增加它的值。

### 3.2. 最小堆合并算法

我们的下一个类是表示最小堆本身并实现合并算法：

```java
public class MinHeap {

    HeapNode[] heapNodes;

    public MinHeap(HeapNode heapNodes[]) {
        this.heapNodes = heapNodes;
        heapifyFromLastLeafsParent();
    }

    int getParentNodeIndex(int index) {
        return (index - 1) / 2;
    }

    int getLeftNodeIndex(int index) {
        return (2  index + 1);
    }

    int getRightNodeIndex(int index) {
        return (2  index + 2);
    }

    HeapNode getRootNode() {
        return heapNodes[0];
    }

    // additional implementation methods
}
```

现在我们已经创建了我们的最小堆类，让我们添加一个方法来堆化一个子树，其中子树的根节点位于数组的给定索引处：

```java
void heapify(int index) {
    int leftNodeIndex = getLeftNodeIndex(index);
    int rightNodeIndex = getRightNodeIndex(index);
    int smallestElementIndex = index;
    if (leftNodeIndex < heapNodes.length 
      && heapNodes[leftNodeIndex].element < heapNodes[index].element) {
        smallestElementIndex = leftNodeIndex;
    }
    if (rightNodeIndex < heapNodes.length
      && heapNodes[rightNodeIndex].element < heapNodes[smallestElementIndex].element) {
        smallestElementIndex = rightNodeIndex;
    }
    if (smallestElementIndex != index) {
        swap(index, smallestElementIndex);
        heapify(smallestElementIndex);
    }
}
```

当我们使用数组来表示最小堆时，最后一个叶节点将始终位于数组的末尾。所以在迭代调用heapify() 方法将数组转为最小堆时，只需要从最后一个叶子的父节点开始迭代即可：

```java
void heapifyFromLastLeafsParent() {
    int lastLeafsParentIndex = getParentNodeIndex(heapNodes.length);
    while (lastLeafsParentIndex >= 0) {
        heapify(lastLeafsParentIndex);
        lastLeafsParentIndex--;
    }
}
```

我们的下一个方法将实际执行我们的算法。为了更好地理解，让我们将该方法分为两部分，看看它是如何工作的：

```java
int[] merge(int[][] array) {
    // transform input arrays
    // run the minheap algorithm
    // return the resulting array
}
```

第一部分将输入数组转换为包含第一个数组的所有元素的堆节点数组，并找到结果数组的大小：

```java
HeapNode[] heapNodes = new HeapNode[array.length];
int resultingArraySize = 0;

for (int i = 0; i < array.length; i++) {
    HeapNode node = new HeapNode(array[i][0], i);
    heapNodes[i] = node;
    resultingArraySize += array[i].length;
}
```

下一部分通过执行我们算法的步骤 4、5、6 和 7 来填充结果数组：

```java
MinHeap minHeap = new MinHeap(heapNodes);
int[] resultingArray = new int[resultingArraySize];

for (int i = 0; i < resultingArraySize; i++) {
    HeapNode root = minHeap.getRootNode();
    resultingArray[i] = root.element;

    if (root.nextElementIndex < array[root.arrayIndex].length) {
        root.element = array[root.arrayIndex][root.nextElementIndex++];
    } else {
        root.element = Integer.MAX_VALUE;
    }
    minHeap.heapify(0);
}
```

## 4. 测试算法

现在让我们使用之前提到的相同输入来测试我们的算法：

```java
int[][] inputArray = { { 0, 6 }, { 1, 5, 10, 100 }, { 2, 4, 200, 650 } };
int[] expectedArray = { 0, 1, 2, 4, 5, 6, 10, 100, 200, 650 };

int[] resultArray = MinHeap.merge(inputArray);

assertThat(resultArray.length, is(equalTo(10)));
assertThat(resultArray, is(equalTo(expectedArray)));
```

## 5.总结

在本教程中，我们学习了如何使用最小堆有效地合并排序数组。