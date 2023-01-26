## 1. 问题描述

给定两个按递增顺序排序的链表，创建并返回一个表示两个链表相交的新链表。新的链表应使用自己的内存-不应更改原始链表。

示例：

```
输入：
第一个链表：1->2->3->4->6
第二个链表：2->4->6->8
输出：2->4->6
解释：元素2、4、6在两个链表中都存在

输入：
第一个链表：1->2->3->4->5
第二个链表：2->3->4
输出：2->3->4
解释：元素2、3、4在两个链表中都存在
```

## 2. 使用虚拟节点

其思想是在结果链表的开头使用临时虚拟节点。指针尾部始终指向结果链表中的最后一个节点，因此可以轻松添加新节点。
虚拟节点最初为尾部提供一个指向的内存空间。这个虚拟节点是有效的，因为它只是临时的，并且是在堆栈中分配的。
循环继续，从“a”或“b”中删除一个节点，并将其添加到尾部。当遍历给定的链表时，结果是虚拟的。接下来，从虚拟对象的下一个节点分配值。
如果两个元素相等，则将两者都移除并将元素插入尾部。否则，删除两个链表中较小的元素。

```java
public class InterSection {
  LinkedList linkedList;
  Node head;
  Node a = null;
  Node b = null;
  Node tail = null;
  Node dummy = null;

  public InterSection(LinkedList linkedList) {
    this.linkedList = linkedList;
    head = linkedList.head;
  }

  public InterSection() {
  }

  public void push(int data) {
    Node temp = new Node(data);
    if (dummy == null) {
      dummy = temp;
    } else {
      tail.next = temp;
    }
    tail = temp;
  }

  void printList(Node start) {
    Node p = start;
    while (p != null) {
      System.out.print(p.data + " ");
      p = p.next;
    }
    System.out.println();
  }

  public void sortedIntersectUsingDummyNode() {
    Node p = a, q = b;
    while (p != null && q != null) {
      if (p.data == q.data) {
        push(p.data);
        p = p.next;
        q = q.next;
      } else if (p.data < q.data)
        p = p.next;
      else
        q = q.next;
    }
  }
}
```

时间复杂度：O(m+n)，其中m和n分别是第一个和第二个链表中的节点数。

辅助空间：O(min(m, n))，输出链表最多包含min(m, n)个节点。

## 3. 递归实现

递归方法与上述方法非常相似。构建一个递归函数，该函数接收两个节点并返回一个链表节点。比较两个链表的第一个元素。

1. 如果它们相等，则使用两个链表的下一个节点调用递归函数。使用当前节点的数据创建一个节点，并将递归函数返回的节点放在所创建节点的下一个指针上。返回创建的节点。
2. 如果值不相等，则删除两个链表中较小的节点并调用递归函数。

以下是上述方法的具体实现：

```
public Node sortedIntersectUsingRecursive(Node a, Node b) {
  if (a == null || b == null)
    return null;
  if (a.data < b.data)
    return sortedIntersectUsingRecursive(a.next, b);
  if (a.data > b.data)
    return sortedIntersectUsingRecursive(a, b.next);
  Node result = new Node();
  result.data = a.data;
  result.next = sortedIntersectUsingRecursive(a.next, b.next);
  return result;
}
```

时间复杂度：O(m+n)，其中m和n分别是第一个和第二个链表中的节点数。只需要遍历一次链表。

辅助空间：O(max(m, n)), 输出链表最多可以存储m+n个节点。

## 4. 使用哈希

```
public Integer[] sortedIntersectUsingHash(Node a, Node b, int k) {
  Integer[] result = new Integer[k];
  HashSet<Integer> set = new HashSet<>();
  while (a != null && b != null){
    if (a.data == b.data){
      set.add(a.data);
      a = a.next;
      b = b.next;
    }else if (a.data < b.data)
      a = a.next;
    else 
      b = b.next;
  }
  return set.toArray(result);
}
```

时间复杂度：O(n)