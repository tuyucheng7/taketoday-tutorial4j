## 1. 概述

[单向链表](https://www.baeldung.com/cs/linked-list-data-structure)是在大多数高级编程语言中实现的常见[数据结构。](https://www.baeldung.com/cs/common-data-structures)例如，这些是单链表的实现：

-   C++ STL 类型 std::foward_list
-   Scala 和 F# 类型列表

在最基本的形式中，链表由一组节点组成。每个节点都包含数据和对下一个节点的引用。

在本教程中，我们将学习如何通过[遍历](https://www.baeldung.com/java-iterate-list)![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)列表来查找单向链表的第 th 个元素。

## 2. 寻找第n 个元素的算法

我们有兴趣实现一个获取![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)链表第 th 个元素的函数。我们如何编写这样的函数？要获取第![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)th 个元素，我们必须遍历列表。

我们介绍了一种算法，用于获取单向链表的索引为![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg),的元素。![n = 0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-56fd6955aa1e512425f363a7fb56c72b_l3.svg)在随后的部分中，我们将以结构化的方式回顾每个重要步骤。

考虑以下伪代码：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-5c03291021586d7a7e5d8e245e289521_l3.svg)

## 3. GetItem(Head,n)的演练

我们从链表的头部开始。

我们的初始设置如下：

![链表遍历01](https://www.baeldung.com/wp-content/uploads/sites/4/2022/07/linked_list_traversal_01.png)

我们取一个空指针current并将其设置为指向头节点——![1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-69a7c7fb1023d315f416440bca10d849_l3.svg)链表的 st 节点。current总是指我们在链表中的当前位置。

current.next指向后续节点——![2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-8c267d62c3d7048247917e13baec69a5_l3.svg)链表的第 nd 个节点。如果我们分配 current = current.next，这会将current向右移动。下面的快照是在电流向右移动之后：

![链表遍历02](https://www.baeldung.com/wp-content/uploads/sites/4/2022/07/linked_list_traversal_02.png)

在 while 循环中多次执行current = current.next可以让我们遍历链表。但是，我们必须注意不要超出链表的末尾。如果current = null， 在 null 对象上调用current = current.next将导致我们的程序崩溃。所以，我们写边界条件current!=null来控制最大迭代次数。

到目前为止的逻辑帮助我们浏览列表。但是，最好跟踪我们所处的索引。因此，我们使用反变量i并计数到n。首先，我们初始化i=0，在每次迭代中，我们检查计数器i是否等于n。如果此条件为真，我们将当前节点内的数据返回给调用函数。如果为假，我们继续下一次迭代，将当前移动到下一个连续节点并将计数器变量i的值增加![1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-69a7c7fb1023d315f416440bca10d849_l3.svg)。

如果控制到达 while 循环结束后的语句，则意味着我们在计数到n 之前已经到达列表的末尾。 

## 4.从列表尾部查找第n 个元素的算法

我们可以在上述基本技术的基础上![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)从单向链表的末尾找到第 th 个元素。为了保持一致，我们使用以下约定。我们将链表的尾部称为具有索引的元素![-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-37abf2e602a43ae0ff9f12b1536fa74c_l3.svg)，最后一个元素称为具有索引![-2](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-6d9d64c8550082ac0eeea0b4d66a5165_l3.svg)，等等。

我们有兴趣编写一个函数，该函数将链表的头部![n < 0](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-af7360bb3f81610c5ece1407db4bae44_l3.svg)作为第一个参数，将索引 ( ) 作为第二个参数。

考虑以下用于获取具有索引的元素的伪代码![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)，其中![n = -1,-2,-3,ldots](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-18c3d9bfa5b5161fb3bcb96d52e249db_l3.svg)链表：

![由 QuickLaTeX.com 呈现](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-703401d1f638646eab2d3ab8fbefdc42_l3.svg)

## 5. GetItemFromEnd(head,n)的演练

让我们来看看上一节介绍的算法的重要步骤。我们采用一个空指针运行器并将其初始化为引用链表的头节点。我们现在遍历列表，将runner移动到具有 index 的节点![abs(n)-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-f80eff2a8104853fcae232ffa6df2163_l3.svg)。

此时，我们可能已经到达链表的末尾，在这种情况下runner = null。在这种情况下，我们中止并向用户打印一条消息，指出列表太小。如果不是这种情况，我们取一个空指针轨迹并将其设置为引用列表的头节点。

跑步者和 跟踪指针之间的距离等于 ![绝对值(n)-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b273dc3d059fad1c5a54cc9545855219_l3.svg)。所以，我们的设置如下：

![链表遍历04](https://www.baeldung.com/wp-content/uploads/sites/4/2022/07/linked_list_traversal_04.png)

 

现在，我们将runner和 trail指针迭代地移动到它们各自的连续节点。在这些迭代过程中，跑步者和轨迹![绝对值(n)-1](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-b273dc3d059fad1c5a54cc9545855219_l3.svg)之间的距离保持不变。这样，当 runner指向链表的尾部时，trail指针应该指向链表尾部的第th个节点。![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)

下面的快照描述了这种情况，当跑步者到达列表的末尾时，对于这种情况![n=-4](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-66a63a5ca0dde8d631d5e1da2f8fb1c7_l3.svg)：

![链表遍历05](https://www.baeldung.com/wp-content/uploads/sites/4/2022/07/linked_list_traversal_05.png)

## 六，总结

在本文中，我们了解了获取![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)链表索引元素的算法。回顾一下，首先，从列表开头检索元素需要将当前指针初始化为head。然后应将其移动到其后继节点![n](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-ec4217f4fa5fcd92a9edceba0e708cf7_l3.svg)时间。其次，当从列表末尾检索元素时，我们设置了两个指针 runner和 trail，![n-1个](https://www.baeldung.com/wp-content/ql-cache/quicklatex.com-3fd905b384548c9de7011828b88081d5_l3.svg)相隔一定距离。然后我们将两个指针迭代地移动到它们的后继节点，直到runner.next=null。