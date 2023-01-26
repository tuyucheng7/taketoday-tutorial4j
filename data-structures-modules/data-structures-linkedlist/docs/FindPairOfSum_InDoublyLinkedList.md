## 1. 问题描述

给定一个不同元素的正数的排序双向链表，任务是在一个双向链表中找到其和等于给定值x的对，而不使用任何额外的空间。

示例：

```
输入: head : 1 <-> 2 <-> 4 <-> 5 <-> 6 <-> 8 <-> 9
     x = 7
输出: (6, 1), (5, 2)
```

期望的时间复杂度为O(n)，辅助空间为O(1)。

## 2. 算法分析

解决这个问题的一个简单方法是一个接一个地选择每个节点，并通过遍历在剩余链表中找到其和等于x的第二个元素。
这个问题的时间复杂度将是O(n<sup>2</sup>)，n是双向链表中的节点总数。

一种更有效的算法如下：

1. 初始化两个指针变量，在已排序的双向链表中查找元素。首先用双向链表的头节点进行初始化，即：first=head。
   并用双向链表的最后一个节点初始化second，即：second=last_node。
2. 我们将first和second指针初始化为第一个和最后一个节点。这里我们没有随机访问，所以为了找到第二个指针，我们遍历链表来初始化second指针。
3. 若first和second指针当前和小于x，我们向后移动first指针。若first和second指针元素的当前和大于x，那么我们向前移动second指针。
4. 循环终止条件也不同于数组。当两个指针相互交叉(second.next == first)或它们变得相同(first == second)时，循环终止。
5. 不存在对的情况将由条件“first==second”处理。

## 3. 算法实现

```java
public class FindPairOfSum {
  DoublyLinkedList doublyLinkedList;
  Node head;

  public FindPairOfSum(DoublyLinkedList doublyLinkedList) {
    this.doublyLinkedList = doublyLinkedList;
    head = doublyLinkedList.head;
  }

  public static void pairSum(Node head, int x) {
    Node first = head;
    Node second = head;
    boolean isFound = false;
    while (second.next != null) {
      second = second.next;
    }
    while (first != second && second.next != first) {
      if ((first.data + second.data) == x) {
        isFound = true;
        System.out.println("(" + first.data + ", " + second.data + ")");
        first = first.next;
        second = second.previous;
      } else {
        if ((first.data + second.data) < x)
          first = first.next;
        else
          second = second.previous;
      }
    }
    if (isFound == false)
      System.out.println("not found");
  }
}
```

时间复杂度：O(n)

辅助空间：O(1)

如果链表没有排序，那么我们可以先对链表进行排序。但在那种情况下，整体时间复杂度将变为O(nLogn)。
如果没有空间复杂度的约束，我们可以在这种情况下使用HashSet。