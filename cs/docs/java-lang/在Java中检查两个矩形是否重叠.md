## 1. 概述

在本快速教程中，我们将学习解决检查两个给定矩形是否重叠的算法问题。

我们将从查看问题定义开始，然后逐步构建解决方案。

最后，我们将用Java实现它。

## 2. 问题定义

假设我们有两个给定的矩形—— r1和r2。我们需要检查r1和r2之间是否至少有一个共同点。如果是，则仅表示这两个矩形重叠。

让我们看一些例子：

[![重叠矩形](https://www.baeldung.com/wp-content/uploads/2018/09/OverlappingRectangles.png)](https://www.baeldung.com/wp-content/uploads/2018/09/OverlappingRectangles.png)

如果我们注意到最后一种情况，矩形r1和r2没有相交的边界。尽管如此，它们还是重叠的矩形，因为r1中的每个点也是r2中的一个点。

## 3.初始设置

要解决这个问题，我们应该首先通过编程定义一个矩形开始。矩形可以很容易地用其左下角和右上角坐标表示：

```java
public class Rectangle {
    private Point bottomLeft;
    private Point topRight;

    //constructor, getters and setters

    boolean isOverlapping(Rectangle other) {
        ...
    }
}
```

其中Point是表示空间中的点(x,y)的类：

```java
public class Point {
    private int x;
    private int y;

    //constructor, getters and setters
}
```

稍后我们将在我们的Rectangle类中定义isOverlapping(Rectangle other)方法来检查它是否与另一个给定的矩形 - other重叠。

## 4.解决方案

如果以下任一条件为真，则两个给定的矩形不会重叠：

1.  两个矩形之一位于另一个矩形的顶部边缘之上
2.  两个矩形之一位于另一个矩形左边缘的左侧

[![非重叠矩形1](https://www.baeldung.com/wp-content/uploads/2018/09/NonOverlappingRectangles1.png)](https://www.baeldung.com/wp-content/uploads/2018/09/NonOverlappingRectangles1.png)

对于所有其他情况，这两个矩形将相互重叠。为了说服自己，我们总是可以举出几个例子。

## 5.Java实现

现在我们了解了解决方案，让我们实现我们的isOverlapping()方法：

```java
public boolean isOverlapping(Rectangle other) {
    if (this.topRight.getY() < other.bottomLeft.getY() 
      || this.bottomLeft.getY() > other.topRight.getY()) {
        return false;
    }
    if (this.topRight.getX() < other.bottomLeft.getX() 
      || this.bottomLeft.getX() > other.topRight.getX()) {
        return false;
    }
    return true;
}
```

如果其中一个矩形位于另一个矩形的上方或左侧，我们在Rectangle类中的isOverlapping()方法返回false ，否则返回true。

要确定一个矩形是否在另一个矩形上方，我们比较它们的y 坐标。同样，我们比较x 坐标以检查一个矩形是否在另一个矩形的左侧。

## 六，总结

在这篇简短的文章中，我们学习了如何解决查找两个给定矩形是否相互重叠的算法问题。它用作两个矩形物体的碰撞检测策略。