## 1. 概述

在本教程中，我们将讨论Java中的接口。我们还将看到Java如何使用它们来实现多态性和多重继承。

## 2. Java中的接口是什么？

在Java中，接口是包含方法和常量变量集合的一种抽象类型。它是Java中的核心概念之一，**用于实现抽象、[多态](https://www.baeldung.com/java-polymorphism)和[多重继承](https://www.baeldung.com/java-inheritance)**。

让我们看一个简单的Java接口示例：

```java
public interface Electronic {

    // Constant variable
    String LED = "LED";

    // Abstract method
    int getElectricityUse();

    // Static method
    static boolean isEnergyEfficient(String electtronicType) {
        if (electtronicType.equals(LED)) {
            return true;
        }
        return false;
    }

    // Default method
    default void printDescription() {
        System.out.println("Electronic Description");
    }
}
```

我们可以使用implements关键字在Java类中实现接口。

接下来，让我们创建一个Computer类来实现我们刚刚创建的Electronic接口：

```java
public class Computer implements Electronic {

    @Override
    public int getElectricityUse() {
        return 1000;
    }
}
```

### 2.1 接口创建规则

在接口中，我们可以使用：

-   [常量变量](https://www.baeldung.com/java-final)
-   [抽象方法](https://www.baeldung.com/java-abstract-class)
-   [静态方法](https://www.baeldung.com/java-static-default-methods)
-   [默认方法](https://www.baeldung.com/java-static-default-methods)

我们还应该记住：

-   我们不能直接实例化接口
-   接口可以是空的，其中没有方法或变量
-   我们不能在接口定义中使用final，因为它会导致编译器错误
-   所有接口声明都应具有public或default访问修饰符；abstract修饰符将由编译器自动添加
-   接口方法不能为protected或final
-   在Java 9之前 9，接口方法不能是私有的；但是，Java 9引入了[在接口中定义私有方法](https://www.baeldung.com/java-interface-private-methods)的可能性
-   根据定义，接口变量是public、static和final；我们不允许更改他们的可见性

## 3. 我们可以通过使用它们实现什么？

### 3.1 行为功能

我们使用接口来添加某些行为功能，这些功能可以被不相关的类使用。例如，Comparable、Comparator和Cloneable是可以由不相关的类实现的Java接口。下面是用于比较Employee类的两个实例的Comparator接口的示例： 

```java
public class Employee {

    private double salary;

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}

public class EmployeeSalaryComparator implements Comparator<Employee> {

    @Override
    public int compare(Employee employeeA, Employee employeeB) {
        if (employeeA.getSalary() < employeeB.getSalary()) {
            return -1;
        } else if (employeeA.getSalary() > employeeB.getSalary()) {
            return 1;
        } else {
            return 0;
        }
    }
}
```

有关更多信息，请访问我们关于[Java中的Comparator和Comparable](https://www.baeldung.com/java-comparator-comparable)的教程。

### 3.2 多重继承

Java类支持单一继承。但是，通过使用接口，我们也能够实现多重继承。

例如，在下面的示例中，我们注意到Car类实现了Fly和Transform接口。通过这样做，它继承了fly和transform方法：

```java
public interface Transform {
    void transform();
}

public interface Fly {
    void fly();
}

public class Car implements Fly, Transform {

    @Override
    public void fly() {
        System.out.println("I can Fly!!");
    }

    @Override
    public void transform() {
        System.out.println("I can Transform!!");
    }
}
```

### 3.3 多态性

让我们从提出问题开始：什么是[多态性](https://www.baeldung.com/java-polymorphism)？它是对象在运行时采用不同形式的能力。更具体地说，它是在运行时执行与特定对象类型相关的重写方法。

**在Java中，我们可以使用接口来实现多态性**。例如，Shape接口可以采用不同的形式-它可以是Circle或Square。

让我们从定义Shape接口开始：

```java
public interface Shape {
    String name();
}
```

现在让我们创建Circle类：

```java
public class Circle implements Shape {

    @Override
    public String name() {
        return "Circle";
    }
}
```

还有Square类：

```java
public class Square implements Shape {

    @Override
    public String name() {
        return "Square";
    }
}
```

最后，是时候使用我们的Shape接口及其实现来了解多态性的实际效果了。让我们实例化一些Shape对象，将它们添加到List中，最后，循环打印它们的名称：

```java
List<Shape> shapes = new ArrayList<>();
Shape circleShape = new Circle();
Shape squareShape = new Square();

shapes.add(circleShape);
shapes.add(squareShape);

for (Shape shape : shapes) {
    System.out.println(shape.name());
}
```

## 4. 接口中的默认方法

Java 7及更低版本中的传统接口不提供向后兼容性。

这意味着**如果你有用Java 7或更早版本编写的遗留代码，并且你决定向现有接口添加抽象方法，那么所有实现该接口的类都必须覆盖新的抽象方法**。否则，代码将被破坏。

**Java 8通过引入可选的可以在接口级别实现的默认方法解决了这个问题**。

## 5. 接口继承规则

为了通过接口实现多重继承，我们必须记住一些规则。让我们详细了解一下。

### 5.1 接口扩展另一个接口

当一个接口扩展另一个接口时，它会继承该接口的所有抽象方法。让我们首先创建两个接口HasColor和Shape：

```java
public interface HasColor {
    String getColor();
}

public interface Box extends HasColor {
    int getHeight();
}
```

在上面的示例中，Box使用关键字extends从HasColor继承。通过这样做，Box接口继承了getColor。因此，Box接口现在有两个方法：getColor和 getHeight。

### 5.2 实现接口的抽象类

当一个抽象类实现一个接口时，它继承了它所有的抽象方法和默认方法。让我们考虑一下 Transform 接口和实现它的抽象类 Vehicle：

```java
public interface Transform {

    void transform();

    default void printSpecs() {
        System.out.println("Transform Specification");
    }
}

public abstract class Vehicle implements Transform {
}
```

在这个例子中，Vehicle类继承了两个方法：抽象的transform方法和默认的printSpecs方法。

## 6. 函数接口

Java从早期就有很多函数式接口，比如Comparable(Java 1.2以来)和Runnable(Java 1.0以来)。

Java 8引入了新的函数式接口，例如Predicate、Consumer和Function。要了解有关这些的更多信息，请访问我们关于[Java 8中的函数式接口](https://www.baeldung.com/java-8-functional-interfaces)的教程。

## 7. 总结

在本教程中，我们概述了Java接口，并讨论了如何使用它们来实现多态性和多重继承。