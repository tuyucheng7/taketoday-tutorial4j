## 1. 概述

在本文中，我们介绍中介者模式，它是[GoF行为型模式](https://en.wikipedia.org/wiki/Design_Patterns)中的一种，我们将描述它的用途并解释何时应该使用它。

## 2. 中介者模式

**在面向对象编程中，我们应该始终尝试以组件松散耦合和可重用的方式设计系统**，这种方法使我们的代码更易于维护和测试。

然而，在现实生活中，我们经常需要处理一组复杂的依赖对象，这时候中介者模式就可以派上用场了。

**中介者模式的目的是降低直接相互通信的紧密耦合对象之间的复杂性和依赖关系**，这是通过创建一个负责依赖对象之间交互的中介对象来实现的。因此，所有通信都通过该中介对象进行。

这促进了松散耦合，因为一组协同工作的组件不再需要直接交互。相反，它们只引用单个中介对象。这样，在系统的其他部分中重用这些对象也更容易。

## 3. 中介者模式的UML图

<img src="../assets/img_3.png">

在上面的UML图中，我们可以确定以下参与者：

-   Mediator定义了Colleague对象用来通信的接口
-   Colleague定义了持有对Mediator的单一引用的抽象类
-   ConcreteMediator封装了Colleague对象之间的交互逻辑
-   ConcreteColleague1和ConcreteColleague2仅通过Mediator进行通信

正如我们所见，**Colleague对象并不直接相互引用；相反，所有通信都由Mediator执行**。

因此，可以更轻松地重用ConcreteColleague1和ConcreteColleague2 。

此外，如果我们需要更改Colleague对象协同工作的方式，我们只需要修改ConcreteMediator逻辑，或者我们可以创建Mediator的新实现。

## 4. Java实现

现在我们对理论有了清晰的认识，让我们看一个例子，以便在实践中更好地理解这个概念。

### 4.1 示例场景

想象一下，我们正在构建一个简单的冷却系统，该系统由风扇、电源和按钮组成。按下按钮将打开或关闭风扇，在我们打开风扇之前，我们需要打开电源。同样，我们必须在关闭风扇后立即关闭电源。

现在让我们看一下示例实现：

```java
public class Button {
    private Fan fan;

    // constructor, getters and setters

    public void press(){
        if(fan.isOn()){
            fan.turnOff();
        } else {
            fan.turnOn();
        }
    }
}
```

```java
public class Fan {
    private Button button;
    private PowerSupplier powerSupplier;
    private boolean isOn = false;

    // constructor, getters and setters

    public void turnOn() {
        powerSupplier.turnOn();
        isOn = true;
    }

    public void turnOff() {
        isOn = false;
        powerSupplier.turnOff();
    }
}
```

```java
public class PowerSupplier {
    public void turnOn() {
        // implementation
    }

    public void turnOff() {
        // implementation
    }
}
```

接下来，让我们测试一下功能：

```java
@Test
void givenTurnedOffFan_whenPressingButtonTwice_fanShouldTurnOnAndOff() {
    assertFalse(fan.isOn());

    button.press();
    assertTrue(fan.isOn());

    button.press();
    assertFalse(fan.isOn());
}
```

一切似乎都很好。但请注意**Button、Fan和PowerSupplier类是如何紧密耦合的**，Button直接在Fan上运行，Fan与Button和PowerSupplier交互。很难在其他模块中重用Button类。此外，如果我们需要在我们的系统中添加第二个电源，那么我们将不得不修改Fan类的逻辑。

### 4.2 引入中介者模式

现在，让我们实现中介者模式来减少我们的类之间的依赖关系，并使代码更具可重用性。

首先是Mediator类：

```java
public class Mediator {
    private Button button;
    private Fan fan;
    private PowerSupplier powerSupplier;

    // constructor, getters and setters

    public void press() {
        if (fan.isOn()) {
            fan.turnOff();
        } else {
            fan.turnOn();
        }
    }

    public void start() {
        powerSupplier.turnOn();
    }

    public void stop() {
        powerSupplier.turnOff();
    }
}
```

接下来，我们修改其余的类：

```java
public class Button {
    private Mediator mediator;

    // constructor, getters and setters

    public void press() {
        mediator.press();
    }
}
```

```java
public class Fan {
    private Mediator mediator;
    private boolean isOn = false;

    // constructor, getters and setters

    public void turnOn() {
        mediator.start();
        isOn = true;
    }

    public void turnOff() {
        isOn = false;
        mediator.stop();
    }
}
```

再次，对改进的代码进行测试：

```java
@Test
void givenTurnedOffFan_whenPressingButtonTwice_fanShouldTurnOnAndOff() {
    assertFalse(fan.isOn());
 
    button.press();
    assertTrue(fan.isOn());
 
    button.press();
    assertFalse(fan.isOn());
}
```

测试表明我们的改进代码按预期工作。**现在我们已经实现了中介者模式，Button、Fan或PowerSupplier类都不会直接通信，他们只有一个对Mediator的引用**。如果将来我们需要添加第二个电源，我们所要做的就是更新Mediator的逻辑；Button和Fan类保持不变。

这个例子清楚的演示了我们如何轻松地分离依赖对象并使我们的系统更易于维护。

## 5. 何时使用中介者模式

**如果我们必须处理一组紧密耦合且难以维护的对象，那么中介者模式是一个不错的选择**。通过这种方式，我们可以减少对象之间的依赖关系并降低整体复杂性。

此外，通过使用中介对象，我们将通信逻辑提取到单个组件，因此我们遵循[单一职责原则]()。此外，我们可以引入新的中介对象而无需更改系统的其余部分，因此我们遵循开闭原则。

**然而，有时，由于系统的错误设计，我们可能会有太多紧密耦合的对象。如果是这种情况，我们不应该应用中介者模式**。相反，我们应该退后一步，重新思考我们为类建模的方式。

与所有其他模式一样，**在盲目实现中介者模式之前，我们需要考虑我们的具体用例**。

## 6. 总结

在本文中，我们了解了中介者模式，解释了这个模式解决了什么问题，以及我们什么时候应该真正考虑使用它。