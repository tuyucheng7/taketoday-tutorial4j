## 1. 概述

在本快速教程中，我们介绍适配器模式及其Java实现。

## 2. 适配器模式

**适配器模式充当两个不兼容接口之间的连接器，否则无法直接连接**。适配器使用新接口包装现有类，以便它与客户端的接口兼容。

使用此模式背后的主要动机是将现有接口转换为客户期望的另一个接口，它通常在设计应用程序后实现。

### 2.1 适配器模式示例

考虑一个场景，其中有一个在美国开发的应用程序，它以英里每小时(MPH)为单位返回豪华车的最高速度。现在，我们需要为英国的客户使用相同的应用程序，该应用程序希望获得相同的结果，但单位为公里每小时(km/h)。

为了解决这个问题，我们将创建一个适配器来转换值并为我们提供所需的结果：

<img src="../assets/img_2.png">

首先，我们将创建原始接口Movable，它应该以英里每小时为单位返回一些豪华车的速度：

```java
public interface Movable {
    // returns speed in MPH 
    double getSpeed();
}
```

现在，我们将创建此接口的一个具体实现：

```java
public class BugattiVeyron implements Movable {

    @Override
    public double getSpeed() {
        return 268;
    }
}
```

现在我们将创建一个基于相同Movable类的适配器接口MovableAdapter，它可能会被稍微修改以在不同的场景中产生不同的结果：

```java
public interface MovableAdapter {
    // returns speed in KM/H 
    double getSpeed();
}
```

此接口的实现将包含用于转换的私有方法convertMPHtoKMPH()：

```java
public class MovableAdapterImpl implements MovableAdapter {
    private Movable luxuryCars;

    // standard constructors

    @Override
    public double getSpeed() {
        return convertMPHtoKMPH(luxuryCars.getSpeed());
    }

    private double convertMPHtoKMPH(double mph) {
        return mph * 1.60934;
    }
}
```

现在我们将只使用适配器中定义的方法，并且我们将获得转换后的速度。在这种情况下，以下断言将为真：

```java
@Test
void whenConvertingMPHToKMPH_thenSuccessfullyConverted() {
    Movable bugattiVeyron = new BugattiVeyron();
    MovableAdapter bugattiVeyronAdapter = new MovableAdapterImpl(bugattiVeyron);
 
    assertEquals(bugattiVeyronAdapter.getSpeed(), 431.30312, 0.00001);
}
```

正如我们在这里可以注意到的，对于这种特殊情况，我们的适配器将268英里/小时转换为431公里/小时。

### 2.2 何时使用适配器模式

-   **当外部组件提供了我们想要重用的有用功能，但它与我们当前的应用程序不兼容时，可以开发合适的适配器使它们相互兼容**
-   当我们的应用程序与客户期望的界面不兼容时
-   当我们想在我们的应用程序中重用遗留代码而不对原始代码进行任何修改时

## 3. 总结

在本文中，我们了解了Java中的适配器设计模式。