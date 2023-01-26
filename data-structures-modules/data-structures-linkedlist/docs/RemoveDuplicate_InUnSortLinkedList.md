## 1. 问题描述

编写一个removeDuplicates()函数，用于接收链表并从链表中删除任何重复节点。链表未排序。
例如，如果链表为12->11->12->21->41->43->21，则RemovedUpplicates()应将链表转换为12->11->21->41->43。

## 2. 使用两个循环

这是使用两个循环的简单方法。外循环用于遍历每个节点，内循环将选择的节点与其余节点进行比较。

以下为代码实现：

```
public void removeDeplicatesInUnSortUsingTwoLoop(Node head) {
  Node current = head;
  Node rest;
  while (current != null && current.next != null) {
    rest = current;
    while (rest.next != null) {
      if (current.data == rest.next.data)
        rest.next = rest.next.next;
      else
        rest = rest.next;
    }
    current = current.next;
  }
}
```

时间复杂度：O(n<sup>2</sup>)

## 3. 使用排序

通常，归并排序是最适合高效排序链表的排序算法。

1. 使用归并排序对元素进行排序。
2. 使用之前讨论过的移除排序链表中重复元素的算法。

请注意，此方法不会保留元素的原始顺序。

时间复杂度：O(nLogn)

## 4. 使用哈希

我们从头到尾遍历链表。对于每个新遇到的元素，我们检查它是否在HashSet中：如果是，我们删除它；否则，我们将其放入HashSet中。

```
public void removeDeplicatesInUnSortUsingHash(Node head) {
  HashSet<Integer> set = new HashSet<>();
  Node current = head;
  Node previous = null;
  while (current != null) {
    int currentData = current.data;
    if (set.contains(current.data))
      previous.next = current.next;
    else {
      set.add(currentData);
      previous = current;
    }
    current = current.next;
  }
}
```

时间复杂度：平均为O(n)，假设HashSet访问时间平均为O(1)。