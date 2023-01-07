## 1. 概述

在本快速教程中，我们将展示如何找到由斜截式形式的线性函数定义的两条线的交点。

## 2. 相交的数学公式

平面上的任何直线(垂直线除外)都可以用线性函数定义：

```plaintext
y = mx + b
```

其中 m是斜率，b是 y 截距。

对于垂直线，m等于无穷大，这就是我们排除它的原因。如果两条线平行，则它们的斜率相同，即m的值相同。

假设我们有两条线。第一个函数定义了第一行：

```plaintext
y = m1x + b1
```

第二个函数定义了第二行：

```plaintext
y = m2x + b2
```

[![一般-y1-y2](https://www.baeldung.com/wp-content/uploads/2018/09/general-y1-y2.png)](https://www.baeldung.com/wp-content/uploads/2018/09/general-y1-y2.png)
我们想找到这些线的交点。显然，该等式对于交点是正确的：

```plaintext
y1 = y2
```

让我们替换y-变量：

```plaintext
m1x + b1 = m2x + b2
```

从上面的等式我们可以找到 x坐标：

```plaintext
x(m1 - m2) = b2 - b1
x = (b2 - b1) / (m1 - m2)
```

最后，我们可以找到交点的y坐标：

```plaintext
y = m1x + b1
```

现在让我们继续执行部分。

## 3.Java实现

首先，我们有四个输入变量——第一行的m1、b1和第二行的m2、b2。

其次，将计算出的交点转换为 java.awt.Point类型的对象。

最后，线可能是平行的，因此让我们将返回值设置为Optional<Point>：

```java
public Optional<Point> calculateIntersectionPoint(
    double m1, 
    double b1, 
    double m2, 
    double b2) {

    if (m1 == m2) {
        return Optional.empty();
    }

    double x = (b2 - b1) / (m1 - m2);
    double y = m1  x + b1;

    Point point = new Point();
    point.setLocation(x, y);
    return Optional.of(point);
}
```

现在让我们选择一些值并测试平行线和非平行线的方法。

例如，我们将x轴 ( y = 0 ) 作为第一行，将y = x – 1定义的行作为第二行。

对于第二条线，斜率m 等于1，即45度，y轴截距等于 -1，这意味着该线在点 (0, -1) 处截取y轴。

直观上很明显，第二条线与x轴的交点必须是 (1,0 )：

[![非平行](https://www.baeldung.com/wp-content/uploads/2018/09/non-parallel.png)](https://www.baeldung.com/wp-content/uploads/2018/09/non-parallel.png)

让我们检查一下。

首先，让我们确保存在一个点，因为线不平行，然后检查x和y的值：

```java
@Test
public void givenNotParallelLines_whenCalculatePoint_thenPresent() {
    double m1 = 0;
    double b1 = 0;
    double m2 = 1;
    double b2 = -1;

    Optional<Point> point = service.calculateIntersectionPoint(m1, b1, m2, b2);

    assertTrue(point.isPresent());
    assertEquals(point.get().getX(), 1, 0.001);
    assertEquals(point.get().getY(), 0, 0.001);
}
```

最后，让我们采用两条平行线并确保返回值为空：

[![平行](https://www.baeldung.com/wp-content/uploads/2018/09/parallel.png)](https://www.baeldung.com/wp-content/uploads/2018/09/parallel.png)

```java
@Test
public void givenParallelLines_whenCalculatePoint_thenEmpty() {
    double m1 = 1;
    double b1 = 0;
    double m2 = 1;
    double b2 = -1;

    Optional<Point> point = service.calculateIntersectionPoint(m1, b1, m2, b2);

    assertFalse(point.isPresent());
}
```

## 4。总结

在本教程中，我们展示了如何计算两条线的交点。