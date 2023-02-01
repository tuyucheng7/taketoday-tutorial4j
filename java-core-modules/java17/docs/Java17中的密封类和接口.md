## 1. 概述

Java SE 17的发布引入了密封类([JEP 409](https://openjdk.org/jeps/409))。

**这个功能是关于在Java中启用更细粒度的继承控制，密封允许类和接口定义它们允许的子类型**。

换句话说，一个类或一个接口现在可以定义哪些类可以实现或扩展它，它是域建模和提高库安全性的有用功能。

## 2. 动机

类层次结构使我们能够通过继承重用代码。但是，类层次结构也可以有其他用途。代码重用很好，但并不总是我们的主要目标。

### 2.1 建模的可能性

类层次结构的另一个目的是对域中存在的各种可能性进行建模。

例如，假设一个业务域只适用于汽车和卡车，而不适用于摩托车。在Java中创建Vehicle抽象类时，我们应该只允许Car和Truck类对其进行扩展。通过这种方式，我们希望确保不会在我们的域内滥用Vehicle抽象类。

**在这个例子中，我们更感兴趣的是代码处理已知子类的清晰度，而不是防御所有未知子类**。

在版本15(在该版本中，密封类作为预览特性引入)之前，Java认为代码重用始终是一个目标，每个类都可以由任意数量的子类进行扩展。

### 2.2 包私有方法

在早期版本中，Java在继承控制方面提供了有限的选项。

[final类](https://www.baeldung.com/java-final)不能有子类，[包私有类](https://www.baeldung.com/java-access-modifiers)只能在同一个包中具有子类。

使用package-private方法，用户在不允许扩展抽象类的情况下无法访问抽象类：

```java
public class Vehicles {

    abstract static class Vehicle {

        private final String registrationNumber;

        public Vehicle(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }
    }

    public static final class Car extends Vehicle {

        private final int numberOfSeats;

        public Car(int numberOfSeats, String registrationNumber) {
            super(registrationNumber);
            this.numberOfSeats = numberOfSeats;
        }

        public int getNumberOfSeats() {
            return numberOfSeats;
        }
    }

    public static final class Truck extends Vehicle {

        private final int loadCapacity;

        public Truck(int loadCapacity, String registrationNumber) {
            super(registrationNumber);
            this.loadCapacity = loadCapacity;
        }

        public int getLoadCapacity() {
            return loadCapacity;
        }
    }
}
```

### 2.3 父类可访问，不可扩展

使用一组子类开发的父类应该能够记录其预期用途，而不是限制其子类。此外，具有受限制的子类不应限制其父类的可访问性。

**因此，密封类背后的主要动机是让父类有可能被广泛访问但不能被广泛扩展**。

## 3. 正式实现

密封特性在Java中引入了几个新的修饰符和子句：sealed、non-sealed和permits。

### 3.1 密封接口

要密封一个接口，我们可以将sealed修饰符应用于它的声明。然后，permits子句用于指定允许实现密封接口的类：

```java
public sealed interface Service permits Car, Truck {

    int getMaxServiceIntervalInMonths();

    default int getMaxDistanceBetweenServicesInKilometers() {
        return 100000;
    }
}
```

### 3.2 密封类

与接口类似，我们可以通过应用相同的sealed修饰符来密封类。permits子句应在任何extends或implements子句之后定义：

```java
public abstract sealed class Vehicle permits Car, Truck {

    protected final String registrationNumber;

    public Vehicle(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }
}
```

允许的子类必须定义修饰符。它可以被[声明为final](https://www.baeldung.com/java-final)以防止任何进一步的扩展：

```java
public final class Truck extends Vehicle implements Service {

    private final int loadCapacity;

    public Truck(int loadCapacity, String registrationNumber) {
        super(registrationNumber);
        this.loadCapacity = loadCapacity;
    }

    public int getLoadCapacity() {
        return loadCapacity;
    }

    @Override
    public int getMaxServiceIntervalInMonths() {
        return 18;
    }
}
```

允许的子类也可以声明为sealed。但是，如果我们声明它是non-sealed的，那么它就可以扩展：

```java
public non-sealed class Car extends Vehicle implements Service {

    private final int numberOfSeats;

    public Car(int numberOfSeats, String registrationNumber) {
        super(registrationNumber);
        this.numberOfSeats = numberOfSeats;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    @Override
    public int getMaxServiceIntervalInMonths() {
        return 12;
    }
}
```

### 3.3 约束条件

密封类对其允许的子类施加了三个重要约束：

1.  所有允许的子类必须与密封类属于同一模块
2.  每个允许的子类都必须显式扩展密封类
3.  每个允许的子类都必须定义这些之中的一个修饰符：final、sealed或non-sealed

## 4. 用法

### 4.1 传统方式

**当密封类时，我们使客户端代码能够清楚地推断出所有允许的子类**。

推理子类的传统方法是使用一组if-else语句和instanceof检查：

```java
if (vehicle instanceof Car) {
	return ((Car) vehicle).getNumberOfSeats();
} else if (vehicle instanceof Truck) {
	return ((Truck) vehicle).getLoadCapacity();
} else {
	throw new RuntimeException("Unknown instance of Vehicle");
}
```

### 4.2 模式匹配

通过应用[模式匹配](https://www.baeldung.com/java-pattern-matching-instanceof)，我们可以避免额外的类型转换，但我们仍然需要一组if-else语句：

```java
if (vehicle instanceof Car car) {
	return car.getNumberOfSeats();
} else if (vehicle instanceof Truck truck) {
	return truck.getLoadCapacity();
} else {
	throw new RuntimeException("Unknown instance of Vehicle");
}
```

使用if-else使得编译器难以确定我们是否涵盖了所有允许的子类，出于这个原因，我们抛出一个RuntimeException。

在未来的Java版本中，客户端代码将能够使用switch语句而不是if-else([JEP 375](https://openjdk.java.net/jeps/375))。

通过使用[类型测试模式](https://openjdk.java.net/jeps/8213076)，编译器将能够检查是否涵盖了每个允许的子类。因此，不再需要default子句/case。

## 5. 兼容性

现在让我们看一下密封类与其他Java语言特性(如记录和反射API)的兼容性。

### 5.1 记录

密封类与[记录](https://www.baeldung.com/java-record-keyword)可以很好的配合使用，由于记录是隐式最终的，因此密封的层次结构更加简洁。让我们尝试使用记录重写我们的类示例：

```java
public sealed interface Vehicle permits Car, Truck {

    String getRegistrationNumber();
}

public record Car(int numberOfSeats, String registrationNumber) implements Vehicle {

    @Override
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }
}

public record Truck(int loadCapacity, String registrationNumber) implements Vehicle {

    @Override
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public int getLoadCapacity() {
        return loadCapacity;
    }
}
```

### 5.2 反射

[反射API](https://www.baeldung.com/java-reflection)也支持密封类，其中java.lang.Class中添加了两个公共方法：

+   isSealed：如果给定的类或接口是密封的，则isSealed方法返回true
+   getPermittedSubclasses：返回表示所有允许的子类的对象数组

我们可以使用这些方法来编写基于我们示例的断言：

```java
assertThat(truck.getClass().isSealed()).isEqualTo(false);
assertThat(truck.getClass().getSuperclass().isSealed()).isEqualTo(true);
assertThat(truck.getClass().getSuperclass().getPermittedSubclasses())
    .contains(ClassDesc.of(truck.getClass().getCanonicalName()));
```

## 6. 总结

**在本文中，我们探讨了密封类和接口，这是JavaSE 17中的一个新特性。我们介绍了密封类和接口的创建和使用，以及它们的约束和与其他语言特性的兼容性**。

在示例中，我们介绍了密封接口和密封类的创建、密封类的使用(使用和不使用模式匹配)以及密封类与记录和反射 API的兼容性。