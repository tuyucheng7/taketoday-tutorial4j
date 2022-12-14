## 1. 概述

在本教程中，我们介绍Java中的工厂设计模式，我们描述两种模式：工厂方法和抽象工厂,两者都是创建型的设计模式。

## 2. 工厂方法模式

首先，我们需要定义一个例子。假设我们正在为一家汽车制造商开发一款应用程序，最初，我们只有一个客户，该客户制造了仅配备燃油发动机的车辆。所以，为了遵循[单一职责原则]()(SRP)和[开闭原则]()(OCP)，我们使用了工厂方法设计模式。

在我们进入一些代码之前，我们为这个模式定义一个默认的[UML]()图：

<img src="../../design-patterns-creational-2/assets/img_5.png"> 

使用上面的UML图作为参考，我们定义了与此模式相关的一些主要概念。**工厂方法模式通过将产品的构造代码与使用该产品的代码分开来放松耦合代码，这种设计使得独立于应用程序的其余部分提取Product结构变得容易。此外，它允许在不破坏现有代码的情况下引入新产品**。

让我们进入代码。首先，在我们的示例应用程序中，我们定义了MotorVehicle接口。这个接口只有一个方法build()，该方法用于制造特定的机动车辆：

```java
public interface MotorVehicle {
    void build();
}
```

下一步是实现实现MotorVehicle接口的具体类，我们创建两种类型：Motorcycle和Car：

```java
public class Motorcycle implements MotorVehicle {
    @Override
    public void build() {
        System.out.println("Build Motorcycle");
    }
}
```

对于Car类，代码为：

```java
public class Car implements MotorVehicle {
    @Override
    public void build() {
        System.out.println("Build Car");
    }
}
```

然后，我们创建MotorVehicleFactory类，此类负责创建每个新的车辆实例。它是一个抽象类，因为它为其特定工厂制造特定的车辆：

```java
public abstract class MotorVehicleFactory {
    public MotorVehicle create() {
        MotorVehicle vehicle = createMotorVehicle();
        vehicle.build();
        return vehicle;
    }

    protected abstract MotorVehicle createMotorVehicle();
}
```

正如你所注意到的，方法create()调用抽象方法createMotorVehicle()来创建特定类型的机动车辆，这就是为什么每个特定的机动车工厂都必须实现其正确的MotorVehicle类型。之前，我们实现了两种MotorVehicle类型，Motorcycle和Car。现在，我们从我们的基类MotorVehicleFactory扩展来实现两者。

首先是MotorcycleFactory类：

```java
public class MotorcycleFactory extends MotorVehicleFactory {
    @Override
    protected MotorVehicle createMotorVehicle() {
        return new Motorcycle();
    }
}
```

然后，CarFactory类：

```java
public class CarFactory extends MotorVehicleFactory {
    @Override
    protected MotorVehicle createMotorVehicle() {
        return new Car();
    }
}
```

就这样，我们的应用程序是使用工厂方法模式设计的，我们现在可以添加任意数量的新机动车辆。最后，我们需要使用[UML]()符号查看最终设计的外观：

<img src="../../design-patterns-creational-2/assets/img_6.png">

## 3. 抽象工厂模式

在我们的第一次应用程序迭代之后，两家新的汽车品牌公司对我们的系统感兴趣：NextGen和FutureVehicle。这些新公司不仅生产纯燃料汽车，还生产电动汽车，并且每个公司都有自己的车辆设计。

我们当前的系统还没有准备好应对这些新情况。我们必须支持电动汽车，并考虑到每个公司都有自己的设计，为了解决这些问题，我们可以使用[抽象工厂模式]()。**这种模式在我们刚开始使用工厂方法模式，需要将我们的系统演进到一个更复杂的系统时常用，它将产品创建代码集中在一个地方**。UML表示形式为：

<img src="../../design-patterns-creational-2/assets/img_7.png">

我们已经有了MotorVehicle接口。此外，我们必须添加一个接口来表示电动汽车：

```java
public interface ElectricVehicle {
    void build();
}
```

接下来，创建我们的抽象工厂。新类是抽象的，因为对象创建的责任将由我们的具体工厂负责。此行为遵循[OCP]()和[SRP]()。下面是该类的定义：

```java
public abstract class Corporation {
    public abstract MotorVehicle createMotorVehicle();

    public abstract ElectricVehicle createElectricVehicle();
}
```

在我们为每个公司创建Vehicle工厂之前，我们必须为我们的新公司实现一些车辆。让我们为FutureVehicle公司创建一些新类。

```java
public class FutureVehicleMotorcycle implements MotorVehicle {
    @Override
    public void build() {
        System.out.println("Future Vehicle Motorcycle");
    }
}
```

然后，电动汽车实例：

```java
public class FutureVehicleElectricCar implements ElectricVehicle {
    @Override
    public void build() {
        System.out.println("Future Vehicle Electric Car");
    }
}
```

我们为NexGen公司做同样的事情：

```java
public class NextGenMotorcycle implements MotorVehicle {
    @Override
    public void build() {
        System.out.println("NextGen Motorcycle");
    }
}
```

另外，其他电动车的具体实现：

```java
public class NextGenElectricCar implements ElectricVehicle {
    @Override
    public void build() {
        System.out.println("NextGen Electric Car");
    }
}
```

最后，我们准备建造我们的Vehicle工厂，首先是FutureVehicle工厂：

```java
public class FutureVehicleCorporation extends Corporation {
    @Override
    public MotorVehicle createMotorVehicle() {
        return new FutureVehicleMotorcycle();
    }
    
    @Override
    public ElectricVehicle createElectricVehicle() {
        return new FutureVehicleElectricCar();
    }
}
```

接下来，NextGenCorporation：

```java
public class NextGenCorporation extends Corporation {
    @Override
    public MotorVehicle createMotorVehicle() {
        return new NextGenMotorcycle();
    }
    
    @Override
    public ElectricVehicle createElectricVehicle() {
        return new NextGenElectricCar();
    }
}
```

就这样，我们使用[抽象工厂模式]()来完成实现。这是我们自定义实现的UML图：

<img src="../../design-patterns-creational-2/assets/img_8.png">

## 4. 工厂方法与抽象工厂

综上所述，**工厂方法使用继承作为设计工具**；同时，**抽象工厂使用委托**。第一个依赖派生类来实现，而基类提供预期的行为，此外，**它是over-method而不是class**。另一方面，**抽象工厂应用于类。两者都遵循[OCP]()和[SRP]()**，生成松散耦合的代码，并为我们代码库的未来更改提供更大的灵活性。创建代码在一个地方。

## 5. 总结

最后，我们对工厂设计模式进行了演练。我们描述了工厂方法和抽象工厂。我们提供了一个示例系统来说明这些模式的使用。此外，我们还简要比较了这两种模式。