## 1. 简介

在本教程中，我们将了解[堆排序](https://www.baeldung.com/cs/understanding-heapsort)的工作原理，并将用Java实现它。

堆排序基于堆数据结构。为了正确理解堆排序，我们将首先深入研究堆及其实现方式。

## 2. 堆数据结构

堆是一种专门的基于树的数据结构。因此它由节点组成。我们将元素分配给节点：每个节点只包含一个元素。

此外，节点可以有孩子。如果一个节点没有任何孩子，我们称它为叶子。

Heap 的特别之处在于两点：

1.  每个节点的值必须小于或等于存储在其子节点中的所有值
2.  它是一棵完整的树，这意味着它的高度最小

由于第一条规则，最少的元素总是在树的根部。

我们如何执行这些规则取决于实现。

堆通常用于实现优先级队列，因为堆是提取最少(或最多)元素的一种非常高效的实现。

### 2.1. 堆变体

堆有很多变体，它们都在一些实现细节上有所不同。

比如我们上面描述的是一个Min-Heap，因为一个parent总是小于它的所有children。或者，我们可以定义 Max-Heap，在这种情况下，父项总是大于其子项。因此，最大的元素将在根节点中。

我们可以从许多树实现中进行选择。最直接的是二叉树。在二叉树中，每个节点最多可以有两个孩子。我们称他们为left child和right child。

执行第二条规则的最简单方法是使用完整二叉树。完整的二叉树遵循一些简单的规则：

1.  如果一个节点只有一个孩子，那应该是它的左孩子
2.  只有最深层次上最右边的节点只能有一个孩子
3.  叶子只能在最深层次

让我们通过一些例子来看看这些规则：

```plaintext
  1        2      3        4        5        6         7         8        9       10
 ()       ()     ()       ()       ()       ()        ()        ()       ()       ()
         /              /       /       /        /        /        /        /  
        ()         ()   ()  ()   ()  ()   ()  ()    ()  ()    ()       ()       ()  ()
                                /                 /        /       /        /  
                               ()          ()     ()  ()    ()  ()   ()       ()  ()
                                                                             /
                                                                            ()
```

树 1、2、4、5 和 7 遵循规则。

树 3 和 6 违反了第一条规则，树 8 和 9 违反了第二条规则，树 10 违反了第三条规则。

在本教程中，我们将重点介绍具有二叉树实现的最小堆。

### 2.2. 插入元素

我们应该以一种保持堆不变量的方式实现所有操作。这样，我们就可以构建具有重复插入的堆，因此我们将专注于单次插入操作。

我们可以通过以下步骤插入一个元素：

1.  创建一个新叶子，它是最深层次上最右边的可用插槽，并将项目存储在该节点中
2.  如果元素小于它的父元素，我们交换它们
3.  继续第 2 步，直到元素小于它的父元素或者它成为新的根

请注意，第 2 步不会违反堆规则，因为如果我们用较小的值替换节点的值，它仍然会小于它的子节点。

让我们看一个例子！我们想将 4 插入到这个堆中：

```plaintext
        2
       / 
      /   
     3     6
    / 
   5   7
```

第一步是创建一个存储 4 的新叶子：

```plaintext
        2
       / 
      /   
     3     6
    /    /
   5   7 4
```

由于 4 小于它的父级 6，我们交换它们：

```plaintext
        2
       / 
      /   
     3     4
    /    /
   5   7 6
```

现在我们检查 4 是否小于它的父级。由于它的父母是 2，我们停止。堆仍然有效，我们插入了数字 4。

让我们插入 1：

```plaintext
        2
       / 
      /   
     3     4
    /    / 
   5   7 6   1
```

我们必须交换 1 和 4：

```plaintext
        2
       / 
      /   
     3     1
    /    / 
   5   7 6   4
```

现在我们应该交换 1 和 2：

```plaintext
        1
       / 
      /   
     3     2
    /    / 
   5   7 6   4
```

由于 1 是新根，我们停止。

## 3. Java中的堆实现

由于我们使用完整的二叉树，我们可以用一个数组来实现它：数组中的一个元素将是树中的一个节点。我们用从左到右、从上到下的数组索引标记每个节点，方法如下：

```plaintext
        0
       / 
      /   
     1     2
    /    /
   3   4 5
```

我们唯一需要做的就是跟踪我们在树中存储了多少元素。这样我们要插入的下一个元素的索引将是数组的大小。

使用这个索引，我们可以计算父节点和子节点的索引：

-   父母：(指数 - 1)/ 2
-   左孩子：2  index + 1
-   右孩子：2  index + 2

由于我们不想为数组重新分配而烦恼，我们将进一步简化实现并使用ArrayList。

基本的二叉树实现如下所示：

```java
class BinaryTree<E> {

    List<E> elements = new ArrayList<>();

    void add(E e) {
        elements.add(e);
    }

    boolean isEmpty() {
        return elements.isEmpty();
    }

    E elementAt(int index) {
        return elements.get(index);
    }

    int parentIndex(int index) {
        return (index - 1) / 2;
    }

    int leftChildIndex(int index) {
        return 2  index + 1;
    }

    int rightChildIndex(int index) {
        return 2  index + 2;
    }

}
```

上面的代码只是将新元素添加到树的末尾。因此，如果有必要，我们需要向上遍历新元素。我们可以使用以下代码来完成：

```java
class Heap<E extends Comparable<E>> {

    // ...

    void add(E e) {
        elements.add(e);
        int elementIndex = elements.size() - 1;
        while (!isRoot(elementIndex) && !isCorrectChild(elementIndex)) {
            int parentIndex = parentIndex(elementIndex);
            swap(elementIndex, parentIndex);
            elementIndex = parentIndex;
        }
    }

    boolean isRoot(int index) {
        return index == 0;
    }

    boolean isCorrectChild(int index) {
        return isCorrect(parentIndex(index), index);
    }

    boolean isCorrect(int parentIndex, int childIndex) {
        if (!isValidIndex(parentIndex) || !isValidIndex(childIndex)) {
            return true;
        }

        return elementAt(parentIndex).compareTo(elementAt(childIndex)) < 0;
    }

    boolean isValidIndex(int index) {
        return index < elements.size();
    }

    void swap(int index1, int index2) {
        E element1 = elementAt(index1);
        E element2 = elementAt(index2);
        elements.set(index1, element2);
        elements.set(index2, element1);
    }
    
    // ...

}
```

请注意，由于我们需要比较元素，因此它们需要实现java.util.Comparable。

## 4.堆排序

由于 Heap 的根总是包含最小的元素，因此 Heap Sort 背后的思想非常简单：删除根节点直到 Heap 变空。

我们唯一需要的是删除操作，它使堆保持一致状态。我们必须确保不违反二叉树的结构或堆属性。

为了保持结构，我们不能删除任何元素，除了最右边的叶子。所以思路是从根节点移除元素，把最右边的叶子存入根节点。

但是这个操作肯定会违反堆属性。因此，如果新根大于它的任何子节点，我们就将它与它的最小子节点交换。由于最小子节点小于所有其他子节点，因此它不违反堆属性。

我们一直交换直到元素变成叶子，或者它少于它的所有子元素。

让我们从这棵树中删除根：

```plaintext
        1
       / 
      /   
     3     2
    /    / 
   5   7 6   4
```

首先，我们将最后一片叶子放在根中：

```plaintext
        4
       / 
      /   
     3     2
    /    /
   5   7 6
```

然后，由于它大于它的两个孩子，我们将它与最小的孩子交换，即 2：

```plaintext
        2
       / 
      /   
     3     4
    /    /
   5   7 6
```

4 小于 6，所以我们停止。

## 5. 堆排序在Java中的实现

有了我们所有的东西，删除根(弹出)看起来像这样：

```java
class Heap<E extends Comparable<E>> {

    // ...

    E pop() {
        if (isEmpty()) {
            throw new IllegalStateException("You cannot pop from an empty heap");
        }

        E result = elementAt(0);

        int lasElementIndex = elements.size() - 1;
        swap(0, lasElementIndex);
        elements.remove(lasElementIndex);

        int elementIndex = 0;
        while (!isLeaf(elementIndex) && !isCorrectParent(elementIndex)) {
            int smallerChildIndex = smallerChildIndex(elementIndex);
            swap(elementIndex, smallerChildIndex);
            elementIndex = smallerChildIndex;
        }

        return result;
    }
    
    boolean isLeaf(int index) {
        return !isValidIndex(leftChildIndex(index));
    }

    boolean isCorrectParent(int index) {
        return isCorrect(index, leftChildIndex(index)) && isCorrect(index, rightChildIndex(index));
    }
    
    int smallerChildIndex(int index) {
        int leftChildIndex = leftChildIndex(index);
        int rightChildIndex = rightChildIndex(index);
        
        if (!isValidIndex(rightChildIndex)) {
            return leftChildIndex;
        }

        if (elementAt(leftChildIndex).compareTo(elementAt(rightChildIndex)) < 0) {
            return leftChildIndex;
        }

        return rightChildIndex;
    }
    
    // ...

}
```

正如我们之前所说，排序只是创建一个堆，并重复删除根：

```java
class Heap<E extends Comparable<E>> {

    // ...

    static <E extends Comparable<E>> List<E> sort(Iterable<E> elements) {
        Heap<E> heap = of(elements);

        List<E> result = new ArrayList<>();

        while (!heap.isEmpty()) {
            result.add(heap.pop());
        }

        return result;
    }
    
    static <E extends Comparable<E>> Heap<E> of(Iterable<E> elements) {
        Heap<E> result = new Heap<>();
        for (E element : elements) {
            result.add(element);
        }
        return result;
    }
    
    // ...

}
```

我们可以通过以下测试验证它是否正常工作：

```java
@Test
void givenNotEmptyIterable_whenSortCalled_thenItShouldReturnElementsInSortedList() {
    // given
    List<Integer> elements = Arrays.asList(3, 5, 1, 4, 2);
    
    // when
    List<Integer> sortedElements = Heap.sort(elements);
    
    // then
    assertThat(sortedElements).isEqualTo(Arrays.asList(1, 2, 3, 4, 5));
}
```

请注意，我们可以提供一个实现，它就地排序，这意味着我们在与元素相同的数组中提供结果。此外，这种方式我们不需要任何中间内存分配。但是，该实现会有点难以理解。

## 6.时间复杂度

堆排序包括两个关键步骤，插入一个元素和删除根节点。这两个步骤的复杂度都为O(log n)。

由于我们重复这两个步骤 n 次，因此整体排序复杂度为O(n log n)。

请注意，我们没有提到数组重新分配的成本，但由于它是O(n)，它不会影响整体复杂性。此外，正如我们之前提到的，可以实现就地排序，这意味着不需要重新分配数组。

另外值得一提的是，50% 的元素是叶子，75% 的元素在最底层的两层。因此，大多数插入操作不会超过两个步骤。

请注意，对于真实世界的数据，快速排序通常比堆排序更高效。一线希望是堆排序总是具有最坏情况的O(n log n)时间复杂度。

## 七. 总结

在本教程中，我们看到了二叉堆和堆排序的实现。

尽管它的时间复杂度是O(n log n)，但在大多数情况下，它并不是处理真实世界数据的最佳算法。