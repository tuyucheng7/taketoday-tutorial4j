## 1. 问题描述

给定一个链表和其中的两个key，交换两个给定key的节点。节点应该通过改变链接来交换。在数据包含多个字段的许多情况下，交换节点的数据可能代价高昂。

可以假设链表中的所有key都是不同的。

示例：

```
输入: 10->15->12->13->20->14,  x = 12, y = 20
输出: 10->15->20->13->12->14

输入: 10->15->12->13->20->14,  x = 10, y = 20
输出: 20->15->12->13->10->14

输入: 10->15->12->13->20->14,  x = 12, y = 13
输出: 10->15->13->12->20->14
```

这看起来可能是一个简单的问题，但其实不然，因为它需要处理以下情况。

1. x和y可能相邻，也可能不相邻。
2. x或y可以是头节点。
3. x或y可能是最后一个节点。
4. x和/或y可能不存在于链表中。

如何编写一个清晰明了的代码来处理上述所有可能性这并不简单。

## 2. 算法思想

其思想是首先在给定的链表中搜索x和y。如果其中任何一个不存在，则直接return。搜索x和y时，跟踪current和previous的指针。
首先更改previous指针的下一个，然后更改current的下一个。

以下是上述算法的具体实现。

```
public void swapNodes(int x, int y) {
  if (x == y) // 如果x和y相同，则无需执行任何操作
    return;
  Node preX = null, curX = head; // 搜索x(跟踪prevX和curX)
  while (curX != null && curX.data != x) {
    preX = curX;
    curX = curX.next;
  }
  Node preY = null, curY = head; 搜索y(跟踪prevY和curY)
  while (curY != null && curY.data != y) {
    preY = curY;
    curY = curY.next;
  }
  if (curX == null || curY == null) // 如果x或y不存在，直接return
    return;
  if (preX != null) // 如果x不是链表的头节点
    preX.next = curY;
  else
    head = curY; // 否则将y设置为链表头节点
  if (preY != null) // 如果y不是链表的头节点
    preY.next = curX;
  else
    head = curX; // 否则将x设置为链表头节点
  // 交换next指针
  Node temp = curX.next;
  curX.next = curY.next;
  curY.next = temp;
}
```

可以优化上述代码，以便在一次遍历中搜索x和y。这里使用两个循环为了直观。

## 3. 更简单的方法