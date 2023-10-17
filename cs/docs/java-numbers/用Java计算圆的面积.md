## 1. 概述

在本快速教程中，我们将说明如何在Java中计算圆的面积。

我们将使用著名的数学公式： r^2  PI。

## 2. 圆面积计算方法

让我们首先创建一个将执行计算的方法：

```java
private void calculateArea(double radius) {
    double area = radius  radius  Math.PI;
    System.out.println("The area of the circle [radius = " + radius + "]: " + area);
}
```

### 2.1. 将半径作为命令行参数传递

现在我们可以读取命令行参数并计算面积：

```java
double radius = Double.parseDouble(args[0]);
calculateArea(radius);
```

当我们编译运行程序时：

```plaintext
java CircleArea.java
javac CircleArea 7
```

我们将得到以下输出：

```plaintext
The area of the circle [radius = 7.0]: 153.93804002589985
```

### 2.2. 从键盘读取半径

获取半径值的另一种方法是使用来自用户的输入数据：

```java
Scanner sc = new Scanner(System.in);
System.out.println("Please enter radius value: ");
double radius = sc.nextDouble();
calculateArea(radius);
```

输出与前面的示例相同。

## 3. 圈课

除了调用第 2 部分中看到的方法来计算面积外，我们还可以创建一个表示圆的类：

```java
public class Circle {

    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    // standard getter and setter

    private double calculateArea() {
        return radius  radius  Math.PI;
    }

    public String toString() {
        return "The area of the circle [radius = " + radius + "]: " + calculateArea();
    }
}
```

我们应该注意一些事情。首先，我们不把面积保存为一个变量，因为它直接依赖于半径，所以我们可以很容易地计算出来。其次，计算面积的方法是私有的，因为我们在toString() 方法中使用了它。 toString()方法不应调用类中的任何公共方法，因为这些方法可能会被覆盖，并且它们的行为会与预期的不同。

我们现在可以实例化我们的 Circle 对象：

```java
Circle circle = new Circle(7);
```

当然，输出将与以前相同。

## 4。总结

在这篇简短而切题的文章中，我们展示了使用Java计算圆面积的不同方法。