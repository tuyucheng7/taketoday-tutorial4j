## 1. 概述

在本文中，我们将研究Java中的面向对象编程(OOP)概念。我们将讨论**类、对象、抽象、封装、继承和多态性**。

## 2. 类

类是所有对象的起点，我们可以将其视为创建对象的模板。一个类通常包含成员字段、成员方法和一个特殊的构造方法。

我们将使用构造函数来创建类的对象：

```java
public class Car {

    // member fields
    private String type;
    private String model;
    private String color;
    private int speed;

    // constructor
    public Car(String type, String model, String color) {
        this.type = type;
        this.model = model;
        this.color = color;
    }

    // member methods
    public int increaseSpeed(int increment) {
        this.speed = this.speed + increment;
        return this.speed;
    }

    // ...
}
```

请注意，一个类可能有多个构造函数。我们可以在我们的[类文章](https://www.baeldung.com/java-classes-objects#classes)中阅读更多关于类的信息。

## 3. 对象

对象是从类创建的，称为类的实例。我们使用它们的构造函数从类创建对象：

```java
Car veyron = new Car("Bugatti", "Veyron", "crimson");
Car corvette = new Car("Chevrolet", "Corvette", "black");
```

在这里，我们创建了Car类的两个实例。在我们的[对象文章](https://www.baeldung.com/java-classes-objects#objects)中阅读更多关于它们的信息。

## 4. 抽象

抽象隐藏了实现的复杂性并暴露了更简单的接口。

如果我们考虑一台典型的计算机，人们只能看到外部接口，这是与其交互最重要的接口，而内部芯片和电路则对用户隐藏。

在OOP中，抽象意味着隐藏程序的复杂实现细节，仅公开使用实现所需的API。在Java中，我们通过使用接口和抽象类来实现抽象。

我们可以在我们的[抽象类](https://www.baeldung.com/java-abstract-class)和[接口](https://www.baeldung.com/java-interfaces)文章中阅读更多关于抽象的内容。

## 5. 封装

**封装是向API的使用者隐藏对象的状态或内部表示**，并提供绑定到对象的可公开访问的方法以进行读写访问。这允许隐藏特定信息并控制对内部实现的访问。

例如，类中的成员字段对其他类是隐藏的，可以使用成员方法访问它们。一种方法是将所有数据字段设为私有，并且只能通过使用公共成员方法访问：

```java
public class Car {

    // ...
    private int speed;

    public int getSpeed() {
        return color;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
    // ...
}
```

这里，字段speed使用private访问修饰符封装，只能使用public getSpeed()和setSpeed()方法访问。我们可以在[访问修饰符](https://www.baeldung.com/java-access-modifiers)一文中阅读有关访问修饰符的更多信息。

## 6. 继承

**继承是一种机制，允许一个类通过继承该类来获得另一个类的所有属性**。我们称继承类为子类，被继承类为超类或父类。

在Java中，我们通过扩展父类来做到这一点。因此，子类从父类获取所有属性：

```java
public class Car extends Vehicle {
    // ...
}
```

当我们扩展一个类时，我们形成了一个[IS-A关系](https://www.baeldung.com/java-inheritance-composition)。**Car是一种Vehicle**。因此，它具有Vehicle的所有特征。

我们可能会问，**为什么我们需要继承**？为了回答这个问题，让我们考虑一家制造不同类型车辆(例如汽车、公共汽车、有轨电车和卡车)的车辆制造商。

为了简化工作，我们可以将所有车辆类型的共同特征和属性捆绑到一个模块中(在Java中是一个类)。我们可以让各个类型继承和重用这些属性：

```java
public class Vehicle {
    private int wheels;
    private String model;

    public void start() {
        // the process of starting the vehicle
    }

    public void stop() {
        // process to stop the vehicle
    }

    public void honk() {
        // produces a default honk 
    }
}
```

车辆类型Car现在将从父Vehicle类继承：

```java
public class Car extends Vehicle {
    private int numberOfGears;

    public void openDoors() {
        // process to open the doors
    }
}
```

Java支持单继承和多级继承。这意味着一个类不能直接从多个类扩展，但它可以使用层次结构：

```java
public class ArmoredCar extends Car {
    private boolean bulletProofWindows;

    public void remoteStartCar() {
        // this vehicle can be started by using a remote control
    }
}
```

在这里，ArmouredCar扩展了Car，而Car扩展了Vehicle。因此，ArmouredCar继承了Car和Vehicle的属性。

虽然我们从父类继承，但开发人员也可以覆盖父类的方法实现。**这称为[方法覆盖](https://www.baeldung.com/java-method-overload-override#method-overriding)**。

在我们上面的Vehicle类示例中，有honk()方法。扩展Vehicle类的Car类可以覆盖此方法并以它想要生成喇叭的方式实现：

```java
public class Car extends Vehicle {
    // ...

    @Override
    public void honk() {
        // produces car-specific honk 
    }
}
```

请注意，这也称为运行时多态性，如下一节所述。我们可以在我们的[Java继承](https://www.baeldung.com/java-inheritance)和[继承与组合](https://www.baeldung.com/java-inheritance-composition)文章中阅读更多关于继承的内容。

## 7. 多态性

[多态性](https://www.baeldung.com/cs/polymorphism)是OOP语言根据输入类型以不同方式处理数据的能力。在Java中，这可以是具有不同方法签名并执行不同功能的相同方法名称：

```java
public class TextFile extends GenericFile {
    // ...

    public String read() {
        return this.getContent()
              .toString();
    }

    public String read(int limit) {
        return this.getContent()
              .toString()
              .substring(0, limit);
    }

    public String read(int start, int stop) {
        return this.getContent()
              .toString()
              .substring(start, stop);
    }
}
```

在这个例子中，我们可以看到方法read()具有三种不同的形式，具有不同的功能。**这种类型的多态性是静态或编译时多态性，也称为[方法重载](https://www.baeldung.com/java-method-overload-override#method-overloading)**。

**还有运行时或动态多态性，其中子类覆盖父类的方法**：

```java
public class GenericFile {
    private String name;

    // ...

    public String getFileInfo() {
        return "Generic File Impl";
    }
}
```

子类可以扩展GenericFile类并覆盖getFileInfo()方法：

```java
public class ImageFile extends GenericFile {
    private int height;
    private int width;

    // ... getters and setters

    public String getFileInfo() {
        return "Image File Impl";
    }
}
```

在我们的[Java中的多态性](https://www.baeldung.com/java-polymorphism)一文中阅读有关多态性的更多信息。

## 8. 总结

在本文中，我们了解了OOP和Java的基本概念。