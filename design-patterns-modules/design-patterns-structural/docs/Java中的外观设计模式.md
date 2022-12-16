## 1. 简介

在本快速教程中，我们介绍一种**结构型设计模式：外观**。

首先，我们将概述该模式，列出它的优点并描述它解决的问题。然后，我们将外观模式应用到Java的现有实际问题中。

## 2. 什么是门面？

简单地说，外观将复杂的子系统封装在一个简单的接口后面，**它隐藏了大部分复杂性并使子系统易于使用**。

另外，如果我们需要直接使用复杂的子系统，我们仍然可以这样做；我们不必一直被迫使用外观。

除了更简单的接口之外，使用这种设计模式还有一个好处，**它将客户端实现与复杂子系统分离**。由于这一点，我们可以对现有子系统进行更改而不会影响客户端。

## 3. 例子

假设我们想启动一辆车，下图表示遗留系统，它允许我们这样做：

<img src="../assets/img.png">

如你所见，它可能非常复杂，并且确实需要一些努力才能正确启动引擎：

```java
airFlowController.takeAir()
fuelInjector.on()
fuelInjector.inject()
starter.start()
coolingController.setTemperatureUpperLimit(DEFAULT_COOLING_TEMP)
coolingController.run()
catalyticConverter.on()
```

同样，停止引擎也需要相当多的步骤：

```java
fuelInjector.off()
catalyticConverter.off()
coolingController.cool(MAX_ALLOWED_TEMP)
coolingController.stop()
airFlowController.off()
```

门面正是我们在这里所需要的，**我们将在两个方法中隐藏所有的复杂性：startEngine()和stopEngine()**。

让我们看看如何实现它：

```java
public class CarEngineFacade {
    private static int DEFAULT_COOLING_TEMP = 90;
    private static int MAX_ALLOWED_TEMP = 50;
    private FuelInjector fuelInjector = new FuelInjector();
    private AirFlowController airFlowController = new AirFlowController();
    private Starter starter = new Starter();
    private CoolingController coolingController = new CoolingController();
    private CatalyticConverter catalyticConverter = new CatalyticConverter();

    public void startEngine() {
        fuelInjector.on();
        airFlowController.takeAir();
        fuelInjector.on();
        fuelInjector.inject();
        starter.start();
        coolingController.setTemperatureUpperLimit(DEFAULT_COOLING_TEMP);
        coolingController.run();
        catalyticConverter.on();
    }

    public void stopEngine() {
        fuelInjector.off();
        catalyticConverter.off();
        coolingController.cool(MAX_ALLOWED_TEMP);
        coolingController.stop();
        airFlowController.off();
    }
}
```

现在，要启动和停止汽车，我们只需要2行代码，而不是13行：

```java
facade.startEngine();
// ...
facade.stopEngine();
```

## 4. 缺点

外观模式不会强迫我们做出不必要的权衡，因为它只会增加额外的抽象层。

有时，该模式可能会在简单方案中被过度使用，这将导致冗余实现。

## 5. 总结

在本文中，我们解释了外观模式并演示了如何在现有系统之上实现它。