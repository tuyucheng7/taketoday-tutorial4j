## 1. 概述

[抽象](https://www.baeldung.com/java-oop#abstraction)是面向对象编程的关键特征之一。它允许我们**仅通过更简单的接口提供功能来隐藏实现的复杂性**。在Java中，我们通过使用[接口](https://www.baeldung.com/java-interfaces)或[抽象类](https://www.baeldung.com/java-abstract-class)来实现抽象。

在本文中，我们将讨论在设计应用程序时何时使用接口以及何时使用抽象类。此外，它们之间的主要区别以及根据我们要实现的目标选择哪一个。

## 2. 类与接口

首先，让我们看看普通具体类与接口之间的区别。

类是用户定义的类型，充当对象创建的蓝图。它可以具有分别表示对象的状态和行为的属性和方法。

接口也是一种用户定义的类型，在语法上类似于类。它可以有一系列字段常量和方法签名，这些常量和方法签名将被接口实现类覆盖。

除了这些，[Java 8的新特性](https://www.baeldung.com/java-8-new-features)还支持接口中的[静态方法和默认方法](https://www.baeldung.com/java-static-default-methods)，以支持向后兼容。如果接口中的方法不是静态的或默认的并且都是公共的，则它们是隐式抽象的。

但是，从Java 9开始，我们还可以在接口中添加[私有方法](https://www.baeldung.com/new-java-9#3-interface-private-method)。

## 3. 接口与抽象类

抽象类只不过是使用abstract关键字声明的类。它还允许我们使用abstract关键字(抽象方法)声明方法签名，并强制其子类实现所有声明的方法。假设如果一个类有一个抽象的方法，那么这个类本身也一定是抽象的。

抽象类对字段和方法修饰符没有限制，而在接口中，默认都是public。我们可以在抽象类中有实例和静态初始化块，而我们永远不能在接口中有它们。抽象类也可能有构造函数，这些构造函数将在子对象实例化期间执行。

Java 8引入了[函数式接口](https://www.baeldung.com/java-8-functional-interfaces)，一个接口只能声明一个抽象方法。除了静态方法和默认方法之外，任何具有单个抽象方法的接口都被视为函数式接口。我们可以使用这个特性来限制要声明的抽象方法的数量。而在抽象类中，我们永远不能对抽象方法声明的数量有这种限制。

抽象类在某些方面类似于接口：

-   **我们不能实例化它们中的任何一个**。也就是说，我们不能直接使用语句new TypeName()来实例化一个对象。如果我们使用上述语句，我们必须使用[匿名类覆盖所有方法](https://www.baeldung.com/java-anonymous-classes)
-   **它们都可能包含一组声明和定义的方法，有或没有实现**。即，接口中的静态和默认方法(定义)，抽象类中的实例方法(定义)，两者中的抽象方法(声明)

## 4. 何时使用接口

让我们看一些应该使用接口的场景：

-   如果问题需要使用多重继承来解决并且由不同的类层次结构组成
-   当不相关的类实现我们的接口时。例如，[Comparable](https://www.baeldung.com/java-comparator-comparable#comparable)提供了可以重写的compareTo()方法来比较两个对象
-   当应用程序功能必须定义为合同时，但不关心谁实施该行为。即，第三方供应商需要完全实施它

**当我们的问题陈述“A is capable of [doing this]”时考虑使用接口**。例如，“Clonable能够克隆一个对象”，“Drawable能够绘制一个形状”等。

让我们考虑一个使用接口的例子：

```java
public interface Sender {
    void send(File fileToBeSent);
}
```

```java
public class ImageSender implements Sender {
    @Override
    public void send(File fileToBeSent) {
        // image sending implementation code.
    }
}
```

在这里，Sender是一个带有方法send()的接口。因此，“发送者能够发送文件”我们将其实现为一个接口。ImageSender实现用于将图像发送到目标的接口。我们可以进一步使用上面的接口来实现VideoSender，DocumentSender来完成各种工作。

考虑使用上述接口及其实现类的单元测试用例：

```java
@Test
void givenImageUploaded_whenButtonClicked_thenSendImage() {
    File imageFile = new File(IMAGE_FILE_PATH);
 
    Sender sender = new ImageSender();
    sender.send(imageFile);
}
```

## 5. 何时使用抽象类

现在，让我们看看应该使用抽象类的一些场景：

-   当尝试在代码中使用继承概念时(在许多相关类之间共享代码)，通过提供子类覆盖的公共基类方法
-   如果我们有指定的要求并且只有部分实施细节
-   虽然扩展抽象类的类有几个公共字段或方法(需要非公共修饰符)
-   如果想要使用非最终或非静态方法来修改对象的状态

**当我们的问题证明“A是B”时，请考虑使用抽象类和继承**。例如，“狗是动物”、“兰博基尼是汽车”等。

让我们看一个使用抽象类的例子：

```java
public abstract class Vehicle {

    protected abstract void start();
    protected abstract void stop();
    protected abstract void drive();
    protected abstract void changeGear();
    protected abstract void reverse();

    // standard getters and setters
}
```

```java
public class Car extends Vehicle {

    @Override
    protected void start() {
        // code implementation details on starting a car.
    }

    @Override
    protected void stop() {
        // code implementation details on stopping a car.
    }

    @Override
    protected void drive() {
        // code implementation details on start driving a car.
    }

    @Override
    protected void changeGear() {
        // code implementation details on changing the car gear.
    }

    @Override
    protected void reverse() {
        // code implementation details on reverse driving a car.
    }
}
```

在上面的代码中，Vehicle类与其他抽象方法一起被定义为抽象类。它提供任何真实世界车辆的通用操作，还具有多种通用功能。扩展Vehicle类的Car类通过提供汽车的实现细节(“Car is a Vehicle”)覆盖所有方法。

因此，我们将Vehicle类定义为抽象类，其中的功能可以由任何单独的真实车辆(如汽车和公共汽车)实现。例如，在现实世界中，汽车和公共汽车的启动永远不会相同(它们各自需要不同的实现细节)。

现在，让我们考虑一个使用上述代码的简单单元测试：

```java
@Test
void givenVehicle_whenNeedToDrive_thenStart() {
    Vehicle car = new Car("BMW");

    car.start();
    car.drive();
    car.changeGear();
    car.stop();
}
```

## 6. 总结

本文讨论了接口和抽象类的概述以及它们之间的主要区别。此外，我们检查了何时在我们的工作中使用它们中的每一个来完成编写灵活和干净的代码。