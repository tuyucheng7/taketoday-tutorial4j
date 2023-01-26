## 1. 问题描述

编写一个函数detectAndCountLoop()，该函数检查给定链表是否包含环，如果存在环，则返回环中的节点数。
例如，下面的链表中存在环的长度为4。如果环不存在，则函数应返回0。

<img src="../assets/FindLengthOfCycle_InLinkedList.png">

## 2. 算法实现

众所周知，当快指针和慢指针在一个公共点相遇时，Floyd循环检测算法终止。该公共点是环中的节点之一。
将此公共点存储在指针变量(例如ptr)中。然后初始化count为1，从公共点开始，继续访问下一个节点并增加count，
直到再次到达公共指针。此时，count的值将等于环的长度。

```
1. 使用Floyd循环检测算法找到环中的公共点。
2. 将公共点存储在临时变量中，初始化count为1。
3. 遍历链表，直到再次到达同一节点，并在移动到下一节点时增加count。
4. 返回count
```

```
public int countNodesinLoop(Node head){
  Node slowPointer = head;
  Node fastPointer = head;
  int count = 1;
  while (slowPointer != null && fastPointer != null && fastPointer.next != null){
    slowPointer = slowPointer.next;
    fastPointer = fastPointer.next.next;
    if (slowPointer == fastPointer){
      Node temp = slowPointer;
      while (temp.next != slowPointer){
        count++;
        temp = temp.next;
      }
      return count;
    }
  }
  return 0;
}
```

## 3. 复杂度分析

时间复杂度：O(n)，只需要遍历一次链表。

辅助空间：O(1)，不需要额外的空间。