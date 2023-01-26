## 1. 问题描述

编写一个函数，该函数接受一个升序排序的链表，并从链表中删除任何重复的节点。该链表只能遍历一次。
例如，如果链表为11->11->11->21->43->60，则RemovedUpplicates()应将链表转换为11->21->43->60。

## 2. 算法实现

从head(或start)节点遍历链表。遍历时，将每个节点与其下一个节点进行比较。如果下一个节点的数据与当前节点相同，
则删除下一个节点。在删除节点之前，需要存储该节点的next指针。

以下为具体实现：

```
public void removeDuplicates(){
  Node current = head;
  while (current != null){
    Node temp = current;
    while (temp != null && temp.data == current.data){
      temp = temp.next;
    }
    current.next = temp;
    current = current.next;
  }
}
```

时间复杂度：O(n)，其中n是给定链表中的节点数。

空间复杂度：O(1)，没有使用额外的空间。

## 3. 递归实现

```
public void removeDuplicate(Node head) {
  removeDuplicatesUsingRecursive(head);
}

public Node removeDuplicatesUsingRecursive(Node head) {
  if (head == null)
    return null;
  if (head.next != null) {
    if (head.data == head.next.data) {
      head.next = head.next.next;
      removeDuplicatesUsingRecursive(head);
    } else
      removeDuplicatesUsingRecursive(head.next);
  }
  return head;
}
```

时间复杂度：O(n)，其中n是给定链表中的节点数。

辅助空间：O(n)

## 4. 其他方法

创建一个指向每个元素第一次出现的指针和另一个将迭代每个元素的指针temp，当前一个指针的值不等于temp指针时，
我们将前一个指针的指针设置为另一个节点的第一次出现。

以下是上述方法的具体实现：

```
public void removeDuplicatesUsingOtherMethod() {
  Node current = head;
  Node previous = head;
  while (current != null) {
    if (current.data != previous.data) {
      previous.next = current;
      previous = previous.next;
    }
    current = current.next;
  }
  if (previous != null)
    previous.next = null;
}
```

时间复杂度：O(n)，其中n是给定链表中的节点数。

辅助空间：O(1)。

## 5. 使用Map

其思想是将所有的节点值添加到map中，并打印它的key。

以下是上述方法的具体实现：

```
public void removeDuplicatesUsingMap(Node head) {
  HashMap<Integer, Boolean> map = new HashMap<>();
  Node current = head;
  while (current != null) {
    if (!map.containsKey(current.data))
      map.put(current.data,true);
    current = current.next;
  }
  for (Integer key : map.keySet()) {
    System.out.println(key);
  }
}
```

时间复杂度：O(n)，其中n是给定链表中的节点数。

辅助空间：O(n)。