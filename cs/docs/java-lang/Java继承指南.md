## 1. 概述

面向对象编程的核心原则之一-**继承，使我们能够重用现有代码或扩展现有类型**。

简单的说，在Java中，一个类可以继承另一个类和多个接口，而一个接口可以继承其他接口。

在本文中，我们将从对继承的需求开始，然后介绍继承如何与类和接口一起工作。

然后，我们将介绍变量/方法名称和访问修饰符如何影响继承的成员。

最后，我们将看到继承类意味着什么。

## 2. 继承的必要性

想象一下，作为一家汽车制造商，你为客户提供多种车型。尽管不同的车型可能提供不同的功能，如天窗或防弹窗，但它们都包含共同的组件和功能，如发动机和车轮。

**创建一个基本设计并将其扩展以创建其专用版本是有意义的**，而不是从头开始单独设计每个车型。

以类似的方式，通过继承，我们可以创建一个具有基本特征和行为的类，并通过创建继承该基类的类来创建其专用版本。同样，接口可以扩展现有接口。

我们会注意到使用多个术语来指代由另一种类型继承的类型，具体来说：

-   **基类型也称为超类型或父类型**
-   **派生类型称为扩展类型或子类型**

## 3. 类继承

### 3.1 扩展类

一个类可以继承另一个类并定义额外的成员。

让我们从定义一个基类Car开始：

```java
public class Car {
    int wheels;
    String model;

    void start() {
        // Check essential parts
    }
}
```

ArmoredCar类可以通过**在其声明中使用关键字extends**来继承Car类的成员：

```java
public class ArmoredCar extends Car {
    int bulletProofWindows;

    void remoteStartCar() {
        // this vehicle can be started by using a remote control
    }
}
```

我们现在可以说ArmoredCar类是Car的子类，而后者是ArmoredCar的超类。

**Java中的类支持单继承**；ArmoredCar类不能扩展多个类。

另外，请注意，在没有extends关键字的情况下，一个类隐式继承类java.lang.Object。

**子类继承父类的非静态保护成员和公共成员**。此外，如果两个类在同一个包中，则会继承具有默认(包私有)访问权限的成员。

另一方面，类的私有成员和静态成员不被继承。

### 3.2 从子类访问父成员

要访问继承的属性或方法，我们可以直接使用它们：

```java
public class ArmoredCar extends Car {
    public String registerModel() {
        return model;
    }
}
```

请注意，我们不需要对超类的引用来访问其成员。

## 4. 接口继承

### 4.1 实现多个接口

**虽然类只能继承一个类，但它们可以实现多个接口**。

想象一下我们在上一节中定义的ArmoredCar是超级间谍所必需的。因此，汽车制造公司想到了添加飞行和漂浮功能：

```java
public interface Floatable {
    void floatOnWater();
}
```

```java
public interface Flyable {
    void fly();
}
```

```java
public class ArmoredCar extends Car implements Floatable, Flyable {
    public void floatOnWater() {
        System.out.println("I can float!");
    }

    public void fly() {
        System.out.println("I can fly!");
    }
}
```

在上面的示例中，我们注意到使用关键字implements来继承接口。

### 4.2 多重继承的问题

**Java允许使用接口进行多重继承**。

在Java 7之前，这不是问题。接口只能定义抽象方法，即没有任何实现的方法。因此，如果一个类实现了具有相同方法签名的多个接口，这不是问题。实现类最终只有一个要实现的方法。

让我们看看这个简单的等式如何随着Java 8在接口中引入默认方法而改变。

**从Java 8开始，接口可以选择为其方法定义默认实现(接口仍然可以定义抽象方法)**。这意味着如果一个类实现了多个接口，这些接口定义了具有相同签名的方法，则子类将继承单独的实现。这听起来很复杂，是不允许的。

**Java不允许继承在不同接口中定义的相同方法的多个实现**。

下面是一个例子：

```java
public interface Floatable {
    default void repair() {
        System.out.println("Repairing Floatable object");
    }
}
```

```java
public interface Flyable {
    default void repair() {
        System.out.println("Repairing Flyable object");
    }
}
```

```java
public class ArmoredCar extends Car implements Floatable, Flyable {
    // this won't compile
}
```

如果我们确实想要实现这两个接口，我们将不得不重写repair()方法。

如果前面示例中的接口定义了具有相同名称的变量，比如duration，则如果不在变量名前面加上接口名称，我们就无法访问它们：

```java
public interface Floatable {
    int duration = 10;
}
```

```java
public interface Flyable {
    int duration = 20;
}
```

```java
public class ArmoredCar extends Car implements Floatable, Flyable {

    public void aMethod() {
        System.out.println(duration); // won't compile
        System.out.println(Floatable.duration); // outputs 10
        System.out.println(Flyable.duration); // outputs 20
    }
}
```

### 4.3 扩展其他接口的接口

一个接口可以扩展多个接口。下面是一个例子：

```java
public interface Floatable {
    void floatOnWater();
}
```

```java
public interface Flyable {
    void fly();
}
```

```java
public interface SpaceTraveller extends Floatable, Flyable {
    void remoteControl();
}
```

接口通过使用关键字extends继承其他接口。类使用关键字implements来继承接口。

## 5. 继承类型

当一个类继承另一个类或接口时，除了继承它们的成员之外，它还继承了它们的类型。这也适用于继承其他接口的接口。

这是一个非常强大的概念，它允许开发人员**针对接口(基类或接口)进行编程**，而不是针对其实现进行编程。

例如，想象一个情况，组织维护其员工拥有的汽车列表。当然，所有员工可能拥有不同的车型。那么我们如何引用不同的汽车实例呢？这是解决方案：

```java
public class Employee {
    private String name;
    private Car car;

    // standard constructor
}
```

因为Car的所有派生类都继承了Car类型，所以可以使用Car类的变量来引用派生类实例：

```java
Employee e1 = new Employee("Shreya", new ArmoredCar());
Employee e2 = new Employee("Paul", new SpaceCar());
Employee e3 = new Employee("Pavni", new BMW());
```

## 6. 隐藏类成员

### 6.1 隐藏实例成员

**如果超类和子类都定义了同名的变量或方法会发生什么情况**？不用担心；我们仍然可以访问它们。但是，我们必须通过在变量或方法前加上关键字this或super来向Java表明我们的意图。

this关键字指的是使用它的实例。super关键字(看起来很明显)指的是父类实例：

```java
public class ArmoredCar extends Car {
    private String model;

    public String getAValue() {
        return super.model;   // returns value of model defined in base class Car
        // return this.model;   // will return value of model defined in ArmoredCar
        // return model;   // will return value of model defined in ArmoredCar
    }
}
```

许多开发人员使用this和super关键字来明确说明他们指的是哪个变量或方法。但是，将它们与所有成员一起使用会使我们的代码看起来很混乱。

### 6.2 隐藏静态成员

**当我们的基类和子类定义同名的静态变量和方法时会发生什么**？我们可以从派生类中的基类访问静态成员，就像我们访问实例变量的方式一样吗？

让我们通过一个例子来了解一下：

```java
public class Car {
    public static String msg() {
        return "Car";
    }
}
```

```java
public class ArmoredCar extends Car {
    public static String msg() {
        return super.msg(); // this won't compile.
    }
}
```

不，我们不能。静态成员属于类而不属于实例。所以我们不能在msg()中使用非静态的super关键字。

由于静态成员属于一个类，我们可以修改前面的调用如下：

```java
return Car.msg();
```

考虑以下示例，其中基类和派生类都定义了具有相同签名的静态方法msg()：

```java
public class Car {
    public static String msg() {
        return "Car";
    }
}
```

```java
public class ArmoredCar extends Car {
    public static String msg() {
        return "ArmoredCar";
    }
}
```

以下是我们如何调用它们：

```java
Car first = new ArmoredCar();
ArmoredCar second = new ArmoredCar();
```

对于前面的代码，first.msg()将输出“Car”，second.msg()将输出“ArmoredCar”。调用的静态消息取决于用于引用ArmoredCar实例的变量类型。

## 7. 总结

在本文中，我们介绍了Java语言的一个核心方面-继承。

我们了解了Java如何支持类的单继承和接口的多继承，并讨论了该机制是如何在语言中工作的复杂性。