## 1. 问题描述

编写一个方法，在给定的单链表中搜索给定的“x”。如果链表中存在x，则方法应返回true，否则返回false。

```
boolean search(Node head, int x) 
```

例如，如果要搜索的是15，而链表为14->21->11->30->10，那么方法应该返回false。如果要搜索的是14，则方法应返回true。

## 2. 迭代解法

```
1. 初始化辅助指针，current = head。
2. current不为null时执行以下操作
   a) 如果current.data等于要搜索的key，返回true
   b) current = current.next
3. 返回false
```

下面是上述算法的具体实现，用于搜索给定的key。

```java
public class LinkedList {
  public boolean searchUsingIterative(Node head, int key) {
    Node current = head;
    while (current != null) {
      if (current.data == key)
        return true;
      current = current.next;
    }
    return false;
  }
}
```

## 3. 递归解法

```
boolean search(Node head, int x)
1. 如果head为null，返回false。
2. 如果head.data = x，返回true。
3. 返回search(head.next, x)
```

下面是上述算法的具体实现，用于搜索给定的key。

```java
public class LinkedList {
  public boolean searchUsingRecursive(Node head, int key) {
    // 基准条件
    if (head == null)
      return false;
    // 如果当前节点的data = key，返回true
    if (head.data == key)
      return true;
    // 否则在链表的剩余部分进行搜索
    return searchUsingRecursive(head.next, key);
  }
}
```