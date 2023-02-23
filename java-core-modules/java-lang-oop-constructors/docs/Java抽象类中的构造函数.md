## 1. 概述

抽象类和构造函数似乎不兼容。**构造函数是类被实例化时调用的方法，并且抽象类不能被实例化**。这听起来有悖常理，对吧？

在本文中，我们将了解为什么抽象类可以有构造函数，以及使用它们如何在子类实例化中提供好处。

## 2. 默认构造函数

**当一个类没有声明任何构造函数时，编译器会为我们创建一个默认的构造函数**。对于抽象类也是如此。即使没有显式构造函数，抽象类也会有一个可用的默认构造函数。

在抽象类中，它的后代可以使用super()调用抽象默认构造函数：

```java
public abstract class AbstractClass {
    // compiler creates a default constructor
}

public class ConcreteClass extends AbstractClass {

    public ConcreteClass() {
        super();
    }
}
```

## 3. 无参数构造函数

我们可以在抽象类中声明一个没有参数的构造函数。它将覆盖默认构造函数，并且任何子类创建都会在构造链中首先调用它。

让我们用一个抽象类的两个子类来验证这个行为：

```java
public abstract class AbstractClass {
    public AbstractClass() {
        System.out.println("Initializing AbstractClass");
    }
}

public class ConcreteClassA extends AbstractClass {
}

public class ConcreteClassB extends AbstractClass {
    public ConcreteClassB() {
        System.out.println("Initializing ConcreteClassB");
    }
}
```

让我们看看调用new ConcreteClassA()时得到的输出：

```bash
Initializing AbstractClass
```

而调用new ConcreteClassB()的输出将是：

```bash
Initializing AbstractClass
Initializing ConcreteClassB
```

### 3.1 安全初始化

声明一个不带参数的抽象构造函数有助于安全初始化。

以下Counter类是用于计算自然数的超类。我们需要它的值从0开始。

让我们看看如何在这里使用无参数构造函数来确保安全初始化：

```java
public abstract class Counter {

    int value;

    public Counter() {
        this.value = 0;
    }

    abstract int increment();
}
```

我们的SimpleCounter子类使用++运算符实现了increment()方法。它在每次调用时将value递增1：

```java
public class SimpleCounter extends Counter {

    @Override
    int increment() {
        return ++value;
    }
}
```

请注意，SimpleCounter没有声明任何构造函数。它的创建依赖于默认调用的Counter的无参数构造函数。

下面的单元测试演示了value属性被构造函数安全地初始化：

```java
@Test
void givenNoArgAbstractConstructor_whenSubclassCreation_thenCalled() {
    Counter counter = new SimpleCounter();

    assertNotNull(counter);
    assertEquals(0, counter.value);
}
```

### 3.2 阻止访问

我们的Counter初始化工作正常，但假设我们不希望子类覆盖此安全初始化。

首先，我们需要将构造函数设为私有以防止子类访问：

```java
private Counter() {
    this.value = 0;
    System.out.println("Counter No-Arguments constructor");
}
```

其次，让我们创建另一个构造函数供子类调用：

```java
public Counter(int value) {
    this.value = value;
    System.out.println("Parametrized Counter constructor");
}
```

最后，我们的SimpleCounter需要覆盖参数化构造函数，否则将无法编译：

```java
public class SimpleCounter extends Counter {

    public SimpleCounter(int value) {
        super(value);
    }

    // concrete methods
}
```

请注意编译器如何期望我们在此构造函数上调用super(value)，以限制对我们的私有无参数构造函数的访问。

## 4. 参数化构造函数

**抽象类中构造函数最常见的用途之一是避免冗余**。让我们创建一个使用汽车的示例，看看我们如何利用参数化构造函数。

我们从一个抽象的Car类开始，以表示所有类型的汽车。我们还需要一个distance属性来知道它走了多远：

```java
public abstract class Car {

    int distance;

    public Car(int distance) {
        this.distance = distance;
    }
}
```

我们的超类看起来不错，但我们不希望使用非零值初始化distance属性。我们还希望防止子类更改distance属性或覆盖参数化构造函数。

让我们看看如何限制对distance的访问并使用构造函数来安全地初始化它：

```java
public abstract class Car {

    private int distance;

    private Car(int distance) {
        this.distance = distance;
    }

    public Car() {
        this(0);
        System.out.println("Car default constructor");
    }

    // getters
}
```

现在，我们的distance属性和参数化构造函数是私有的。有一个公共默认构造函数Car()委托私有构造函数初始化distance。

要使用我们的distance属性，让我们添加一些行为来获取和显示汽车的基本信息：

```java
abstract String getInformation();

protected void display() {
    String info = new StringBuilder(getInformation())
        .append("\nDistance: " + getDistance())
        .toString();
    System.out.println(info);
}
```

所有子类都需要提供getInformation()的实现，display()方法将使用它来打印所有详细信息。

现在让我们创建ElectricCar和FuelCar子类：

```java
public class ElectricCar extends Car {
    int chargingTime;

    public ElectricCar(int chargingTime) {
        this.chargingTime = chargingTime;
    }

    @Override
    String getInformation() {
        return new StringBuilder("Electric Car")
              .append("\nCharging Time: " + chargingTime)
              .toString();
    }
}

public class FuelCar extends Car {
    String fuel;

    public FuelCar(String fuel) {
        this.fuel = fuel;
    }

    @Override
    String getInformation() {
        return new StringBuilder("Fuel Car")
              .append("\nFuel type: " + fuel)
              .toString();
    }
}
```

让我们看看这些子类的实际应用：

```java
ElectricCar electricCar = new ElectricCar(8);
electricCar.display();

FuelCar fuelCar = new FuelCar("Gasoline");
fuelCar.display();
```

生成的输出如下所示：

```bash
Car default constructor
Electric Car
Charging Time: 8
Distance: 0

Car default constructor
Fuel Car
Fuel type: Gasoline
Distance: 0
```

## 5. 总结

与Java中的任何其他类一样，抽象类可以具有构造函数，即使它们仅从其具体子类调用时也是如此。

在本文中，我们从抽象类的角度介绍了每种类型的构造函数-它们与创建子类的关系以及我们如何在实际用例中使用它们。