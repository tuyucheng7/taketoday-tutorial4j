## 1. 概述

在本快速教程中，我们将展示如何在Java中计算两点之间的距离。

## 2.距离的数学公式

假设我们在平面上有两个点：第一个点 A 的坐标为 (x1, y1)，第二个点 B 的坐标为 (x2, y2)。我们要计算 AB，即点之间的距离。

首先，让我们用斜边 AB 构建一个直角三角形：

[![三角形](https://www.baeldung.com/wp-content/uploads/2018/09/triangle.png)](https://www.baeldung.com/wp-content/uploads/2018/09/triangle.png)

根据勾股定理，三角形边长的平方和等于三角形斜边长度的平方： AB 2 = AC 2 + CB 2。

其次，让我们计算 AC 和 CB。

明显地：

```plaintext
AC = y2 - y1
```

相似地：

```plaintext
BC = x2 - x1
```

让我们替换等式的部分：

```plaintext
distance  distance = (y2 - y1)  (y2 - y1) + (x2 - x1)  (x2 - x1)
```

最后，从上面的等式我们可以计算出点之间的距离：

```plaintext
distance = sqrt((y2 - y1)  (y2 - y1) + (x2 - x1)  (x2 - x1))
```

现在让我们继续执行部分。

## 3.Java实现

### 3.1. 使用普通公式

尽管 java.lang.Math 和 java.awt.geom.Point2D 包提供了现成的解决方案，但让我们首先按原样实现上面的公式：

```java
public double calculateDistanceBetweenPoints(
  double x1, 
  double y1, 
  double x2, 
  double y2) {       
    return Math.sqrt((y2 - y1)  (y2 - y1) + (x2 - x1)  (x2 - x1));
}
```

为了测试解决方案，让我们以第3条和第 4条边的三角形为例(如上图所示)。很明显，数字5适合作为斜边的值：

```plaintext
3  3 + 4  4 = 5  5
```

让我们检查解决方案：

```java
@Test
public void givenTwoPoints_whenCalculateDistanceByFormula_thenCorrect() {
    double x1 = 3;
    double y1 = 4;
    double x2 = 7;
    double y2 = 1;

    double distance = service.calculateDistanceBetweenPoints(x1, y1, x2, y2);

    assertEquals(distance, 5, 0.001);
}
```

### 3.2. 使用java.lang.Math包

如果calculateDistanceBetweenPoints()方法中的乘法结果太大，可能会发生溢出。与此不同， Math.hypot()方法可防止中间溢出或下溢：

```java
public double calculateDistanceBetweenPointsWithHypot(
    double x1, 
    double y1, 
    double x2, 
    double y2) {
        
    double ac = Math.abs(y2 - y1);
    double cb = Math.abs(x2 - x1);
        
    return Math.hypot(ac, cb);
}
```

让我们采用与之前相同的点并检查距离是否相同：

```java
@Test
public void givenTwoPoints_whenCalculateDistanceWithHypot_thenCorrect() {
    double x1 = 3;
    double y1 = 4;
    double x2 = 7;
    double y2 = 1;

    double distance = service.calculateDistanceBetweenPointsWithHypot(x1, y1, x2, y2);

    assertEquals(distance, 5, 0.001);
}
```

### 3.3. 使用java.awt.geom.Point2D包

最后，让我们用Point2D.distance()方法计算距离：

```java
public double calculateDistanceBetweenPointsWithPoint2D( 
    double x1, 
    double y1, 
    double x2, 
    double y2) {
        
    return Point2D.distance(x1, y1, x2, y2);
}
```

现在让我们以相同的方式测试该方法：

```java
@Test
public void givenTwoPoints_whenCalculateDistanceWithPoint2D_thenCorrect() {

    double x1 = 3;
    double y1 = 4;
    double x2 = 7;
    double y2 = 1;

    double distance = service.calculateDistanceBetweenPointsWithPoint2D(x1, y1, x2, y2);

    assertEquals(distance, 5, 0.001);
}
```

## 4。总结

在本教程中，我们展示了几种在Java中计算两点之间距离的方法。