## 1. 概述

方法重载和覆盖是Java编程语言的关键概念，因此，它们值得深入研究。

在本文中，我们将学习这些概念的基础知识并了解它们在哪些情况下有用。

## 2. 方法重载

**方法重载是一种强大的机制，允许我们定义内聚类API**。为了更好地理解为什么方法重载是一个如此有价值的功能，让我们看一个简单的例子。

假设我们已经编写了一个朴素的实用程序类，它实现了将两个数字、三个数字相乘等的不同方法。

如果我们为这些方法提供了误导性或模棱两可的名称，例如multiply2()、multiply3()、multiply4()，那么这将是一个设计糟糕的类API。这就是方法重载发挥作用的地方。

**简单来说，我们可以通过两种不同的方式来实现方法重载**：

-   实现两个或多个**具有相同名称但接收不同数量参数的方法**
-   实现两个或多个**具有相同名称但接收不同类型参数的方法**

### 2.1 不同数量的参数

简而言之，Multiplier类展示了如何通过简单地定义两个接收不同数量参数的实现来重载multiply()方法：

```java
public class Multiplier {

    public int multiply(int a, int b) {
        return a * b;
    }

    public int multiply(int a, int b, int c) {
        return a * b * c;
    }
}
```

### 2.2 不同类型的参数

同样，我们可以通过使multiply()方法接收不同类型的参数来重载它：

```java
public class Multiplier {

    public int multiply(int a, int b) {
        return a * b;
    }

    public double multiply(double a, double b) {
        return a * b;
    }
}
```

此外，使用两种类型的方法重载定义Multiplier类是合法的：

```java
public class Multiplier {

    public int multiply(int a, int b) {
        return a * b;
    }

    public int multiply(int a, int b, int c) {
        return a * b * c;
    }

    public double multiply(double a, double b) {
        return a * b;
    }
}
```

然而，值得注意的是，**不可能有两个仅在返回类型上不同的方法实现**。

为了理解原因-让我们考虑以下示例：

```java
public int multiply(int a, int b) { 
    return a * b; 
}
 
public double multiply(int a, int b) { 
    return a * b; 
}
```

在这种情况下，**由于方法调用不明确，代码根本无法编译**-编译器不知道要调用multiply()的哪个实现。

### 2.3 类型提升

方法重载提供的一个巧妙的特性是所谓的类型提升，也就是扩大原始转换。

简而言之，当传递给重载方法的参数类型与特定方法实现不匹配时，一种给定类型将隐式提升为另一种类型。

为了更清楚地了解类型提升的工作原理，请考虑multiply()方法的以下实现：

```java
public double multiply(int a, long b) {
    return a * b;
}

public int multiply(int a, int b, int c) {
    return a * b * c;
}
```

现在，使用两个int参数调用该方法将导致第二个参数被提升为long，因为在这种情况下，不存在具有两个int参数的方法的匹配实现。

让我们看一个快速单元测试来演示类型提升：

```java
@Test
public void whenCalledMultiplyAndNoMatching_thenTypePromotion() {
    assertThat(multiplier.multiply(10, 10)).isEqualTo(100.0);
}
```

相反，如果我们调用具有匹配实现的方法，类型提升就不会发生：

```java
@Test
public void whenCalledMultiplyAndMatching_thenNoTypePromotion() {
    assertThat(multiplier.multiply(10, 10, 10)).isEqualTo(1000);
}
```

以下是适用于方法重载的类型提升规则的摘要：

-   byte可以提升为short、int、long、float或double
-   short可以提升为int、long、float或double
-   char可以提升为int、long、float或double
-   int可以提升为long、float或double
-   long可以提升为float或double
-   float可以提升为double

### 2.4 静态绑定

将特定方法调用与方法主体相关联的能力称为绑定。

在方法重载的情况下，绑定是在编译时静态执行的，因此称为静态绑定。

编译器可以通过简单地检查方法的签名在编译时有效地设置绑定。

## 3. 方法覆盖

**方法覆盖允许我们在子类中为基类中定义的方法提供细粒度的实现**。

虽然方法覆盖是一个强大的功能，考虑到这是使用[继承](https://en.wikipedia.org/wiki/Inheritance_(object-oriented_programming))的逻辑结果，[OOP](https://en.wikipedia.org/wiki/Object-oriented_programming)的最大支柱之一-但**何时何地使用它应该在每个用例的基础上仔细分析**。

现在让我们看看如何通过创建一个简单的、基于继承的(“is-a”)关系来使用方法覆盖。

下面是基类：

```java
public class Vehicle {

    public String accelerate(long mph) {
        return "The vehicle accelerates at : " + mph + " MPH.";
    }

    public String stop() {
        return "The vehicle has stopped.";
    }

    public String run() {
        return "The vehicle is running.";
    }
}
```

这是一个人为的子类：

```java
public class Car extends Vehicle {

    @Override
    public String accelerate(long mph) {
        return "The car accelerates at : " + mph + " MPH.";
    }
}
```

在上面的层次结构中，我们简单地重写了accelerate()方法，以便为子类型Car提供更精确的实现。

在这里，可以清楚地看到，**如果应用程序使用Vehicle类的实例，那么它也可以使用Car的实例**，因为accelerate()方法的两个实现具有相同的签名和相同的返回类型。

让我们编写一些单元测试来检查Vehicle和Car类：

```java
@Test
public void whenCalledAccelerate_thenOneAssertion() {
    assertThat(vehicle.accelerate(100))
        .isEqualTo("The vehicle accelerates at : 100 MPH.");
}
    
@Test
public void whenCalledRun_thenOneAssertion() {
    assertThat(vehicle.run())
        .isEqualTo("The vehicle is running.");
}
    
@Test
public void whenCalledStop_thenOneAssertion() {
    assertThat(vehicle.stop())
        .isEqualTo("The vehicle has stopped.");
}

@Test
public void whenCalledAccelerate_thenOneAssertion() {
    assertThat(car.accelerate(80))
        .isEqualTo("The car accelerates at : 80 MPH.");
}
    
@Test
public void whenCalledRun_thenOneAssertion() {
    assertThat(car.run())
        .isEqualTo("The vehicle is running.");
}
    
@Test
public void whenCalledStop_thenOneAssertion() {
    assertThat(car.stop())
        .isEqualTo("The vehicle has stopped.");
}
```

现在，让我们看一些单元测试，它们显示未被覆盖的run()和stop()方法如何为Car和Vehicle返回相等的值：

```java
@Test
public void givenVehicleCarInstances_whenCalledRun_thenEqual() {
    assertThat(vehicle.run()).isEqualTo(car.run());
}
 
@Test
public void givenVehicleCarInstances_whenCalledStop_thenEqual() {
   assertThat(vehicle.stop()).isEqualTo(car.stop());
}
```

在我们的例子中，我们可以访问这两个类的源代码，因此我们可以清楚地看到在基础Vehicle实例上调用accelerate()方法和在Car实例上调用accelerate()将为相同的参数返回不同的值。

因此，以下测试演示了为Car的实例调用了覆盖的方法：

```java
@Test
public void whenCalledAccelerateWithSameArgument_thenNotEqual() {
    assertThat(vehicle.accelerate(100))
        .isNotEqualTo(car.accelerate(100));
}
```

### 3.1 类型可替换性

OOP的核心原则是类型可替换性，它与[Liskov替换原则(LSP)](https://en.wikipedia.org/wiki/Liskov_substitution_principle)密切相关。

简而言之，**LSP声明如果应用程序适用于给定的基类型，那么它也应该适用于其任何子类型**。这样，类型可替换性就得到了适当的保留。

**方法覆盖的最大问题是派生类中的某些特定方法实现可能不完全遵循LSP，因此无法保持类型可替换性**。

当然，使重写方法接收不同类型的参数并返回不同类型的参数是有效的，但要完全遵守这些规则：

-   如果基类中的方法采用给定类型的参数，则重写的方法应采用相同类型或超类型(也称为逆变方法参数)
-   如果基类中的方法返回void，则重写方法应返回void
-   如果基类中的方法返回原始类型，则重写方法应返回相同的原始类型
-   如果基类中的方法返回特定类型，则重写方法应返回相同类型或子类型(也称为协变返回类型)
-   如果基类中的方法抛出异常，则被覆盖的方法必须抛出相同的异常或基类异常的子类型

### 3.2 动态绑定

考虑到方法重写只能通过继承来实现，其中存在基类型和子类型的层次结构，编译器无法在编译时确定要调用的方法，因为基类和子类都定义了同样的方法。

因此，编译器需要检查对象的类型以了解应该调用什么方法。

由于这种检查发生在运行时，因此方法覆盖是动态绑定的典型示例。

## 4. 总结

在本教程中，我们学习了如何实现方法重载和方法重写，并探讨了它们有用的一些典型情况。