## 1. 概述

在本快速教程中，我们将了解Java编程语言的两个基本构建块-类和对象。它们是面向对象编程(OOP)的基本概念，我们用它来对现实生活中的实体进行建模。

在OOP中，**类是对象的蓝图或模板。我们使用它们来描述实体的类型**。

另一方面，**对象是有生命的实体，由类创建。它们在它们的字段内包含某些状态，并用它们的方法呈现某些行为**。

## 2. 类

简单地说，一个类代表一个定义或一种类型的对象。在Java中，类可以包含字段、构造函数和方法。

让我们看一个使用表示Car的简单Java类的示例：

```java
class Car {

    // fields
    String type;
    String model;
    String color;
    int speed;

    // constructor
    Car(String type, String model, String color) {
        this.type = type;
        this.model = model;
        this.color = color;
    }

    // methods
    int increaseSpeed(int increment) {
        this.speed = this.speed + increment;
        return this.speed;
    }

    // ...
}
```

这个Java类代表一般的汽车。我们可以从这个类中创建任何类型的汽车。我们使用字段来保存状态，并使用构造函数从此类创建对象。

默认情况下，每个Java类都有一个空的构造函数。如果我们没有像上面那样提供特定的实现，我们就会使用它。以下是默认构造函数如何查找我们的Car类：

```java
Car(){}
```

**此构造函数只是使用默认值初始化对象的所有字段。字符串初始化为null，整数初始化为0**。

现在，我们的类有一个特定的构造函数，因为我们希望我们的对象在创建它们时定义它们的字段：

```java
Car(String type, String model) {
    // ...
}
```

综上所述，我们编写了一个定义汽车的类。它的属性由包含类对象状态的字段描述，它的行为使用方法描述。

## 3. 对象

类是在编译时翻译的，**而对象是在运行时从类创建的**。

类的对象称为实例，我们使用构造函数创建和初始化它们：

```java
Car focus = new Car("Ford", "Focus", "red");
Car auris = new Car("Toyota", "Auris", "blue");
Car golf = new Car("Volkswagen", "Golf", "green");
```

现在，我们已经创建了不同的Car对象，它们都来自一个类。**这就是一切的重点，在一个地方定义蓝图，然后在许多地方多次重用它**。

到目前为止，我们有3个Car对象，它们都停着，因为它们的速度为0。我们可以通过调用increaseSpeed方法来改变它：

```java
focus.increaseSpeed(10);
auris.increaseSpeed(20);
golf.increaseSpeed(30);
```

现在，我们改变了汽车的状态-它们都以不同的速度行驶。

此外，我们可以而且应该定义对我们的类、它的构造函数、字段和方法的访问控制。我们可以通过使用访问修饰符来做到这一点，我们将在下一节中看到。

## 4. 访问修饰符

在前面的示例中，我们省略了访问修饰符以简化代码。通过这样做，我们实际上使用了默认的package-private修饰符。该修饰符允许从同一包中的任何其他类访问该类。

通常，我们会对构造函数使用public修饰符以允许从所有其他对象访问：

```java
public Car(String type, String model, String color) {
    // ...
}
```

我们类中的每个字段和方法也应该通过特定的修饰符定义访问控制。**类通常有public修饰符，但我们倾向于将我们的字段保持为private**。

字段保存我们对象的状态，因此我们想要控制对该状态的访问。我们可以将其中一些设为私有，而另一些设为公开。我们使用称为getter和setter的特定方法来实现这一点。

让我们看一下具有完全指定访问控制的类：

```java
public class Car {
    private String type;
    // ...

    public Car(String type, String model, String color) {
        // ...
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSpeed() {
        return speed;
    }

    // ...
}
```

**我们的类被标记为public，这意味着我们可以在任何包中使用它**。此外，构造函数是public的，这意味着我们可以在任何其他对象中从此类创建一个对象。

**我们的字段标记为私有，这意味着它们不能直接从我们的对象访问**，但我们通过getter和setter提供对它们的访问。

type和model字段没有getter和setter，因为它们保存着我们对象的内部数据。我们只能在初始化时通过构造函数来定义它们。

此外，color可以访问和更改，而speed只能访问而不能更改。我们通过专门的公共方法increaseSpeed()和decreaseSpeed()强制执行速度调整。

也就是说，**我们使用访问控制来封装对象的状态**。

## 5. 总结

在本文中，我们介绍了Java语言的两个基本元素：类和对象，并展示了它们的使用方式和原因。我们还介绍了访问控制的基础知识并演示了其用法。

要学习Java语言的其他概念，我们建议下一步阅读有关[继承](https://www.baeldung.com/java-inheritance)、[super关键字](https://www.baeldung.com/java-super)和[抽象类](https://www.baeldung.com/java-abstract-class)的内容。