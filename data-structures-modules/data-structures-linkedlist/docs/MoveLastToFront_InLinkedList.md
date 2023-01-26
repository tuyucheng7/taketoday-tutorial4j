## 1. 问题描述

编写一个函数，将给定链表中的最后一个元素移到前面。例如，如果给定的链表是1->2->3->4->5，那么函数应该将链表更改为5->1->2->3->4。

## 2. 算法实现

遍历链表直到最后一个节点。使用两个指针：一个(last)用于指向最后一个节点，另一个(secondLast)用于指向倒数第二个节点。循环结束后，执行以下操作：

1. 将secondLast作为最后一个节点(secondLast.next = null)。
2. 将头节点设置为last的下一个节点(last.next = head)。
3. 将last设置为头节点(head = last)。

```
public void moveToFront(){
  Node current = head;
  Node previous = null;
  while (current != null && current.next != null){
    previous = current;
    current = current.next;
  }
  previous.next = null;
  current.next = head;
  head = current;
}
```

时间复杂度：O(n)，其中n是给定链表中的节点数。