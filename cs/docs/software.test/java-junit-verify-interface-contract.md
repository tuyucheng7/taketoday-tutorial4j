## 1. 概述

[继承](https://www.baeldung.com/java-inheritance)是Java中的一个重要概念。[接口](https://www.baeldung.com/java-interfaces)是我们实现这一概念的方式之一。

接口定义了多个类可以实现的契约。随后，必须测试这些实现类以确保它们遵循相同的要求。

在本教程中，我们将了解用Java编写接口[JUnit 测试的不同方法。](https://www.baeldung.com/junit-5)

## 2. 设置

让我们创建一个用于不同方法的基本设置。

首先，我们首先创建一个名为Shape 的简单接口，它有一个方法area()：

```java
public interface Shape {

    double area();
}
```

其次，我们定义一个实现Shape接口的Circle类。它还有一个自己的方法circumference() ：

```java
public class Circle implements Shape {

    private double radius;

    Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double area() {
        return 3.14 * radius * radius;
    }

    public double circumference() {
        return 2 * 3.14 * radius;
    }
}
```

最后，我们定义另一个类Rectangle，它实现Shape接口。它有一个附加方法perimeter()：

```java
public class Rectangle implements Shape {

    private double length;
    private double breadth;

    public Rectangle(double length, double breadth) {
        this.length = length;
        this.breadth = breadth;
    }

    @Override
    public double area() {
        return length * breadth;
    }

    public double perimeter() {
        return 2 * (length + breadth);
    }
}
```

## 3. 测试方法

现在，让我们看一下测试实现类可以遵循的不同方法。

### 3.1. 实施类的单独测试

最流行的方法之一是为接口的每个实现类创建单独的JUnit测试类。我们将测试类的两种方法——继承的方法和类本身定义的方法。

最初，我们创建CircleUnitTest类，其中包含area()和circumference()方法的测试用例：

```java
@Test
void whenAreaIsCalculated_thenSuccessful() {
    Shape circle = new Circle(5);
    double area = circle.area();
    assertEquals(78.5, area);
}

@Test
void whenCircumferenceIsCalculated_thenSuccessful(){
    Circle circle = new Circle(2);
    double circumference = circle.circumference();
    assertEquals(12.56, circumference);
}
```

在下一步中，我们创建RectangleUnitTest类，其中包含area()和perimeter()方法的测试用例：

```java
@Test
void whenAreaIsCalculated_thenSuccessful() {
    Shape rectangle = new Rectangle(5,4);
    double area = rectangle.area();
    assertEquals(20, area);
}

@Test
void whenPerimeterIsCalculated_thenSuccessful() {
    Rectangle rectangle = new Rectangle(5,4);
    double perimeter = rectangle.perimeter();
    assertEquals(18, perimeter);
}
```

正如我们从上面的两个类中看到的，我们可以成功测试接口方法以及实现类可能定义的任何其他方法。 

使用这种方法，我们可能必须为所有实现类重复编写相同的接口方法测试。正如我们在各个测试中看到的那样，在两个实现类中测试了相同的area()方法。

随着实现类数量的增加，随着接口定义的方法数量的增加，跨实现的测试也会成倍增加。因此，代码的复杂性和冗余性也会增加，使得随着时间的推移很难维护和更改。

### 3.2. 参数化测试

为了克服这个问题，让我们创建一个[参数化测试](https://www.baeldung.com/parameterized-tests-junit-5)，它将不同实现类的实例作为输入：

```java
@ParameterizedTest
@MethodSource("data")
void givenShapeInstance_whenAreaIsCalculated_thenSuccessful(Shape shapeInstance, double expectedArea){
    double area = shapeInstance.area();
    assertEquals(expectedArea, area);
}

private static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      { new Circle(5), 78.5 },
      { new Rectangle(4, 5), 20 }
    });
}
```

通过这种方法，我们成功地测试了实现类的接口契约。

然而，除了接口中定义的内容之外，我们无法灵活地定义任何其他内容。因此，我们可能仍然需要以其他形式测试实现类。可能需要在它们自己的JUnit类中测试它们。

### 3.3. 使用基测试类

使用前两种方法，除了验证接口契约之外，我们没有足够的灵活性来扩展测试用例。同时，我们也要避免代码冗余。因此，让我们看看另一种可以解决这两个问题的方法。

在这种方法中，我们定义一个基测试类。这个抽象测试类定义了要测试的方法，即接口契约。随后，实现类的测试类可以扩展这个抽象测试类以构建测试。

我们将使用[模板方法模式](https://www.baeldung.com/java-template-method-pattern)，其中我们定义算法来测试基测试类中的area() 方法，然后，测试子类只需要提供算法中使用的实现。

让我们定义基测试类来测试area()方法：

```java
public abstract Map<String, Object> instantiateShapeWithExpectedArea();

@Test
void givenShapeInstance_whenAreaIsCalculated_thenSuccessful() {
    Map<String, Object> shapeAreaMap = instantiateShapeWithExpectedArea();
    Shape shape = (Shape) shapeAreaMap.get("shape");
    double expectedArea = (double) shapeAreaMap.get("area");
    double area = shape.area();
    assertEquals(expectedArea, area);
}
```

现在，让我们为Circle类创建JUnit测试类：

```java
@Override
public Map<String, Object> instantiateShapeWithExpectedArea() {
    Map<String,Object> shapeAreaMap = new HashMap<>();
    shapeAreaMap.put("shape", new Circle(5));
    shapeAreaMap.put("area", 78.5);
    return shapeAreaMap;
}

@Test
void whenCircumferenceIsCalculated_thenSuccessful(){
    Circle circle = new Circle(2);
    double circumference = circle.circumference();
    assertEquals(12.56, circumference);
}
```

最后，矩形类的测试类：

```java
@Override
public Map<String, Object> instantiateShapeWithExpectedArea() {
    Map<String,Object> shapeAreaMap = new HashMap<>();
    shapeAreaMap.put("shape", new Rectangle(5,4));
    shapeAreaMap.put("area", 20.0);
    return shapeAreaMap;
}

@Test
void whenPerimeterIsCalculated_thenSuccessful() {
    Rectangle rectangle = new Rectangle(5,4);
    double perimeter = rectangle.perimeter();
    assertEquals(18, perimeter);
}
```

在这种方法中，我们重写了instantiateShapeWithExpectedArea()方法。在此方法中，我们提供了Shape实例以及预期区域。基测试类中定义的测试方法可以使用这些参数来执行测试。

总而言之，通过这种方法，实现类可以对其自己的方法进行测试，并继承接口方法的测试。

## 4。总结

在本文中，我们探讨了编写JUnit测试来验证接口契约的不同方法。

首先，我们了解了为每个实现类定义单独的测试类是如何简单的。然而，这可能会导致大量冗余代码。

然后，我们探讨了使用参数化测试如何帮助我们避免冗余，但灵活性较差。

最后，我们看到了基本测试类方法，它解决了其他两种方法中的问题。