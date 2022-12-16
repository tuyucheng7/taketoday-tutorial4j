## 1. 概述

四人组(GoF)引入的桥接设计模式的官方定义是将抽象与其实现解耦，以便两者可以独立变化。

这意味着创建一个桥接接口，该接口使用OOP原则将职责分离到不同的抽象类中。

## 2. 桥接模式示例

对于桥接模式，我们将考虑两层抽象；一个是几何形状(如三角形和正方形)，它填充了不同的颜色(我们的第二个抽象层)：

<img src="../assets/img_4.png">

首先，我们定义一个颜色接口：

```java
public interface Color {
    String fill();
}
```

现在我们将为该接口创建一个具体类：

```java
public class Blue implements Color {
    @Override
    public String fill() {
        return "Color is Blue";
    }
}
```

现在让我们创建一个抽象的Shape类，它包含对Color对象的引用(桥接)：

```java
public abstract class Shape {
    protected Color color;

    // standard constructors

    abstract public String draw();
}
```

然后创建一个具体的Shape接口类，它也将使用Color接口中的方法：

```java
public class Square extends Shape {

    public Square(Color color) {
        super(color);
    }

    @Override
    public String draw() {
        return "Square drawn. " + color.fill();
    }
}
```

对于此模式，以下断言将为真：

```java
@Test
void whenBridgePatternInvoked_thenConfigSuccess() {
    // a square with red color
    Shape square = new Square(new Red());
 
    assertEquals("Square drawn. Color is Red", square.draw());
}
```

在这里，我们使用桥接模式并传递所需的颜色对象，正如我们在输出中注意到的那样，形状会用所需的颜色绘制：

```shell
Square drawn. Color: Red
Triangle drawn. Color: Blue
```

## 3. 总结

在本文中，我们了解了桥接设计模式，在以下情况下这是一个不错的选择：

-   当我们希望父抽象类定义基本规则集，而具体类来添加额外的规则时
-   当我们有一个引用对象的抽象类时，它具有将在每个具体类中定义的抽象方法