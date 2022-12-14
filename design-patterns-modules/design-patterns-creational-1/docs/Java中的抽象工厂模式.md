## 1. 概述

在本文中，我们介绍抽象工厂设计模式。

《[设计模式：可重用面向对象软件的元素](https://books.google.co.in/books?id=K4qv1D-LKhoC&lpg=PP1&dq=Design Patterns%3A Elements of Reusable Object-Oriented Software&pg=PP1#v=onepage&q=Design Patterns: Elements of Reusable Object-Oriented Software&f=false)》一书指出，**抽象工厂“提供了一个接口，用于创建相关或依赖对象的族，而无需指定它们的具体类”**。 换句话说，这个模型允许我们创建遵循一般模式的对象。

JDK中抽象工厂设计模式的一个例子是javax.xml.parsers.DocumentBuilderFactory类的newInstance()。

## 2. 抽象工厂设计模式示例

在这个例子中，我们将创建工厂方法设计模式的两个实现：AnimalFactory和ColorFactory。

之后，我们将使用抽象工厂AbstractFactory管理对它们的访问：

<img src="../assets/img_1.png">

首先，我们创建一个Animal类族，稍后将在我们的抽象工厂中使用它。

这是动物接口：

```java
public interface Animal {
    String getAnimal();

    String makeSound();
}
```

和一个具体的实现鸭子：

```java
public class Duck implements Animal {

    @Override
    public String getAnimal() {
        return "Duck";
    }

    @Override
    public String makeSound() {
        return "Squeks";
    }
}
```

此外，我们可以完全以这种方式创建Animal接口(如Dog、Bear等)的更具体实现。

抽象工厂处理依赖对象的族，考虑到这一点，我们将再引入一个Color系列作为具有一些实现(White、Brown等)的接口。

具体的代码这里不多展示，，但可以在[此处]()找到它。

现在我们已经准备好多个对象系列(Animal和Color)，我们可以为它们创建一个AbstractFactory接口：

```java
public interface AbstractFactory<T> {
    T create(String animalType);
}
```

接下来，我们将使用我们在上一节中讨论的工厂方法设计模式来实现一个AnimalFactory：

```java
public class AnimalFactory implements AbstractFactory<Animal> {

    @Override
    public Animal create(String animalType) {
        if ("Dog".equalsIgnoreCase(animalType)) {
            return new Dog();
        } else if ("Duck".equalsIgnoreCase(animalType)) {
            return new Duck();
        }

        return null;
    }
}
```

同样，我们可以使用相同的设计模式为Color接口实现一个工厂。

当所有这些类都准备好后，我们创建一个FactoryProvider类，该类将根据我们提供给getFactory()方法的参数为我们提供AnimalFactory或ColorFactory的实现：

```java
public class FactoryProvider {

    public static AbstractFactory getFactory(String choice) {
        if ("Animal".equalsIgnoreCase(choice)) {
            return new AnimalFactory();
        } else if ("Color".equalsIgnoreCase(choice)) {
            return new ColorFactory();
        }

        return null;
    }
}
```

## 3. 何时使用抽象工厂模式

-   客户端独立于我们如何创建和组合系统中的对象
-   该系统由多个对象族组成，这些族被设计为一起使用
-   我们需要一个运行时值来构造一个特定的依赖

**虽然该模式在创建预定义对象时非常有用，但添加新对象可能具有挑战性**。为了支持新类型的对象，需要更改AbstractFactory类及其所有子类。

## 4. 总结

在本文中，我们了解了抽象工厂设计模式。