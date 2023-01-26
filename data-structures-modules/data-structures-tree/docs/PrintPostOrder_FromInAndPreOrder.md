## 1. 问题描述

给定二叉树的中序和前序遍历，打印后序遍历。

示例：

```
输入:
中序遍历 in[] = {4, 2, 5, 1, 3, 6}
前序遍历 pre[] = {1, 2, 4, 5, 3, 6}

输出:
后序遍历为{4, 5, 2, 6, 3, 1}
```

上面示例中的遍历表示以下树

```
         1
      /        
     2       3
   /         
  4     5      6
```

## 2. 算法分析

一种简单的方法是先构造树，然后使用简单的递归方法打印所构造树的后序遍历。

我们可以在不构造树的情况下打印后序遍历。其思想是，根节点始终是前序遍历中的第一项，并且它必须是后序遍历中的最后一项。
我们首先递归打印左子树，然后递归打印右子树。最后，打印根节点。为了在pre[]和in[]中找到左右子树的边界，
我们在in[]中搜索root，in[]中root之前的所有元素都是左子树的元素，root之后的所有元素都是右子树的元素。
在pre[]中，in[]中root元素所在索引之后的所有元素都是右子树的元素。并且索引之前的元素(包括索引处的元素，不包括第一个元素)是左子树的元素。

```java
public class PrintPostOrder {

  private int search(int[] arr, int x, int n) {
    for (int i = 0; i < n; i++)
      if (arr[i] == x)
        return i;
    return -1;
  }

  public void printPostOrder(int[] in, int[] pre, int n) {
    // pre[]中的第一个元素总是root，在in[]中搜索pre[]中的第一个元素以便找到左右子树
    int root = search(in, pre[0], n);
    // 如果左子树不为空，打印左子树
    if (root != 0)
      printPostOrder(in, Arrays.copyOfRange(pre, 1, n), root);
    // 如果右子树不为空，打印右子树
    if (root != n - 1)
      printPostOrder(Arrays.copyOfRange(in, root + 1, n), Arrays.copyOfRange(pre, 1 + root, n), n - root - 1);
    // 最后打印根节点
    System.out.print(pre[0] + " ");
  }
}
```

## 3. 算法实现

以下是具体实现：

```java
public class PrintPostOrder {
  static int preIndex = 0;

  public void printPost(int[] in, int[] pre, int inStart, int inEnd) {
    if (inStart > inEnd)
      return;
    // 查找前序遍历中下一个元素在中序遍历中的索引
    int inIndex = search(in, inStart, inEnd, pre[preIndex++]);
    // 遍历左子树
    printPost(in, pre, inStart, inIndex - 1);
    // 遍历右子树
    printPost(in, pre, inIndex + 1, inEnd);
    // 在遍历结束时打印根节点
    System.out.print(in[inIndex] + " ");
  }

  private int search(int[] in, int inStart, int inEnd, int data) {
    int i;
    for (i = inStart; i < inEnd; i++)
      if (in[i] == data)
        return i;
    return i;
  }
}
```

时间复杂度：上述函数访问数组中的每个节点。对于每次访问，它都会调用search()，
这需要O(n)个时间。因此，总体时间复杂度为O(n<sup>2</sup>)。

## 4. 哈希改进

可以使用哈希优化上述解决方案。我们使用HashMap存储元素及其索引，以便快速找到元素的索引。

```java
public class PrintPostOrder {
  static int preIndex = 0;

  public void printPostUsingHashMain(int[] in, int[] pre) {
    int n = pre.length;
    HashMap<Integer, Integer> hashMap = new HashMap<>();
    for (int i = 0; i < n; i++) {
      hashMap.put(in[i], i);
    }
    printPostUsingHash(in, pre, 0, n - 1, hashMap);
  }

  private void printPostUsingHash(int[] in, int[] pre, int inStart, int inEnd, HashMap<Integer, Integer> hashMap) {
    if (inStart > inEnd)
      return;
    int inIndex = hashMap.get(pre[preIndex++]);
    printPostUsingHash(in, pre, inStart, inIndex - 1, hashMap);
    printPostUsingHash(in, pre, inIndex + 1, inEnd, hashMap);
    System.out.print(in[inIndex] + " ");
  }
}
```

时间复杂度：O(n)