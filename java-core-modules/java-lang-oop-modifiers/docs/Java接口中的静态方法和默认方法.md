## 1. 概述

Java 8带来了一些全新的特性，包括[Lambda表达式](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html)、[函数式接口](https://www.baeldung.com/java-8-functional-interfaces)、[方法引用](https://www.baeldung.com/java-8-double-colon-operator)、[Stream](https://www.baeldung.com/java-8-streams)、[Optional](https://www.baeldung.com/java-optional)以及接口中的静态和默认方法。

我们已经在[另一篇文章](https://www.baeldung.com/java-8-new-features)中介绍了其中的一些功能。尽管如此，接口中的静态方法和默认方法本身值得更深入地研究。

在本教程中，我们将学习**如何在接口中使用静态方法和默认方法**，并讨论它们可以发挥作用的一些情况。

### 延伸阅读

### [Java接口中的私有方法](https://www.baeldung.com/java-interface-private-methods)

了解如何在接口中定义私有方法，以及我们如何在静态和非静态上下文中使用它们。

[阅读更多](https://www.baeldung.com/java-interface-private-methods)→

### [在Java中使用接口与抽象类](https://www.baeldung.com/java-interface-vs-abstract-class)

了解何时使用接口以及何时使用Java中的抽象类。

[阅读更多](https://www.baeldung.com/java-interface-vs-abstract-class)→

### [Java static关键字指南](https://www.baeldung.com/java-static)

了解Java静态字段、静态方法、静态块和静态内部类。

[阅读更多](https://www.baeldung.com/java-static)→

## 2. 为什么接口需要默认方法

与常规接口方法一样，**默认方法是隐式公共的**；无需指定public修饰符。

与常规接口方法不同，我们**在方法签名的开头使用default关键字声明它们，并且它们提供了实现**。

让我们看一个简单的例子：

```java
public interface MyInterface {

    // regular interface methods

    default void defaultMethod() {
        // default method implementation
    }
}
```

Java 8版本包含默认方法的原因很明显。

在一个典型的基于抽象的设计中，一个接口有一个或多个实现，如果将一个或多个方法添加到接口中，则所有的实现都将被迫实现它们。否则，设计就会崩溃。

默认接口方法是处理此问题的有效方法。它们**允许我们向在实现中自动可用的接口添加新方法**。因此，我们不需要修改实现类。

**通过这种方式，无需重构实现类就可以很好地保留向后兼容性**。

## 3. 默认接口方法的实际应用

为了更好地理解默认接口方法的功能，让我们创建一个简单的示例。

假设我们有一个简单的Vehicle接口和一个实现。可能还有更多，但让我们保持简单：

```java
public interface Vehicle {

    String getBrand();

    String speedUp();

    String slowDown();

    default String turnAlarmOn() {
        return "Turning the vehicle alarm on.";
    }

    default String turnAlarmOff() {
        return "Turning the vehicle alarm off.";
    }
}
```

现在让我们编写实现类：

```java
public class Car implements Vehicle {

    private String brand;

    // constructors/getters

    @Override
    public String getBrand() {
        return brand;
    }

    @Override
    public String speedUp() {
        return "The car is speeding up.";
    }

    @Override
    public String slowDown() {
        return "The car is slowing down.";
    }
}
```

最后，让我们定义一个典型的主类，它创建一个Car的实例并调用它的方法：

```java
public static void main(String[] args) { 
    Vehicle car = new Car("BMW");
    System.out.println(car.getBrand());
    System.out.println(car.speedUp());
    System.out.println(car.slowDown());
    System.out.println(car.turnAlarmOn());
    System.out.println(car.turnAlarmOff());
}
```

请注意Vehicle接口中的默认方法turnAlarmOn()和turnAlarmOff()如何在Car类中自动可用。

此外，如果在某个时候我们决定向Vehicle接口添加更多默认方法，应用程序仍将继续工作，并且我们不必强制类为新方法提供实现。

接口默认方法最常见的用途是**在不分解实现类的情况下逐步为给定类型提供附加功能**。

此外，我们可以使用它们**围绕现有的抽象方法提供额外的功能**：

```java
public interface Vehicle {

    // additional interface methods 

    double getSpeed();

    default double getSpeedInKMH(double speed) {
        // conversion      
    }
}
```

## 4. 多接口继承规则

默认接口方法是一个非常好的特性，但有一些注意事项值得一提。由于Java允许类实现多个接口，**因此了解当一个类实现多个定义相同默认方法的接口时会发生什么非常重要**。

为了更好地理解这种情况，让我们定义一个新的Alarm接口并重构Car类：

```java
public interface Alarm {

    default String turnAlarmOn() {
        return "Turning the alarm on.";
    }

    default String turnAlarmOff() {
        return "Turning the alarm off.";
    }
}
```

通过这个定义自己的默认方法集的新接口，Car类将同时实现Vehicle和Alarm：

```java
public class Car implements Vehicle, Alarm {
    // ...
}
```

在这种情况下，**代码根本无法编译，因为存在由多个接口继承引起的冲突(也称为[钻石问题](https://en.wikipedia.org/wiki/Multiple_inheritance))**。Car类将继承两组默认方法。那么我们应该调用哪组？

**为了解决这种歧义，我们必须明确地提供方法的实现**：

```java
@Override
public String turnAlarmOn() {
    // custom implementation
}
    
@Override
public String turnAlarmOff() {
    // custom implementation
}
```

我们也可以**让我们的类使用其中一个接口的默认方法**。

让我们看一个使用Vehicle接口的默认方法的例子：

```java
@Override
public String turnAlarmOn() {
    return Vehicle.super.turnAlarmOn();
}

@Override
public String turnAlarmOff() {
    return Vehicle.super.turnAlarmOff();
}
```

同样，我们可以让类使用Alarm接口中定义的默认方法：

```java
@Override
public String turnAlarmOn() {
    return Alarm.super.turnAlarmOn();
}

@Override
public String turnAlarmOff() {
    return Alarm.super.turnAlarmOff();
}
```

甚至**可以让Car类同时使用这两组默认方法**：

```java
@Override
public String turnAlarmOn() {
    return Vehicle.super.turnAlarmOn() + " " + Alarm.super.turnAlarmOn();
}
    
@Override
public String turnAlarmOff() {
    return Vehicle.super.turnAlarmOff() + " " + Alarm.super.turnAlarmOff();
}
```

## 5. 静态接口方法

除了在接口中声明默认方法外，**Java 8还允许我们在接口中定义和实现静态方法**。

由于静态方法不属于特定对象，因此它们不是实现接口的类的API的一部分；因此，**必须在方法名称之前使用接口名称来调用它们**。

要了解静态方法在接口中的工作方式，让我们重构Vehicle接口并向其添加静态实用方法：

```java
public interface Vehicle {

    // regular / default interface methods

    static int getHorsePower(int rpm, int torque) {
        return (rpm * torque) / 5252;
    }
}
```

**在接口中定义静态方法与在类中定义静态方法相同**。此外，可以在其他静态和默认方法中调用静态方法。

假设我们要计算给定车辆发动机的[马力](https://en.wikipedia.org/wiki/Horsepower)。我们只需调用getHorsePower()方法：

```java
Vehicle.getHorsePower(2500, 480));
```

静态接口方法背后的想法是提供一种简单的机制，使我们能够通过将相关方法放在一个地方而不需要创建对象来提高设计的[内聚度](https://en.wikipedia.org/wiki/Cohesion_(computer_science))。

**抽象类几乎可以完成同样的事情**。主要区别在于**抽象类可以有构造函数、状态和行为**。

此外，接口中的静态方法可以对相关的实用程序方法进行分组，而无需创建仅作为静态方法占位符的人工实用程序类。

## 6. 总结

在本文中，我们深入探讨了Java 8中静态和默认接口方法的使用。乍一看，这个特性可能看起来有点草率，特别是从面向对象的纯粹主义者的角度来看。理想情况下，接口不应该封装行为，我们应该只使用它们来定义某种类型的公共API。

然而，当谈到保持与现有代码的向后兼容性时，静态方法和默认方法是一个很好的权衡。