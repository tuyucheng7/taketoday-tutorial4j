## 1. 概述

在编写单元测试时，有时我们会遇到这样的情况：在构造新对象时返回Mock会很有用。例如，在测试具有[紧密耦合的](https://www.baeldung.com/java-coupling-classes-tight-loose)对象依赖性的遗留代码时。

在本教程中，我们将了解Mockito的一个相对较新的功能，它允许我们在构造函数调用上生成Mock。

要了解有关使用Mockito进行测试的更多信息，请查看我们全面的[Mockito 系列](https://www.baeldung.com/tag/mockito/)。

## 2. 依赖关系

首先，我们需要将[mockito](https://mvnrepository.com/artifact/org.mockito/mockito-core)依赖项添加到我们的项目中：

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>
```

如果我们使用的Mockito版本低于版本 5，那么我们还需要显式添加Mockito的Mock生成器内联[依赖项](https://mvnrepository.com/artifact/org.mockito/mockito-inline)：

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-inline</artifactId>
    <version>5.2.0</version>
    <scope>test</scope>
</dependency>
```

## 3. 关于Mock构造函数调用的简单介绍

一般来说，有些人可能会说，在编写干净的面向对象代码时，我们不需要在创建对象时返回Mock实例。这通常可能暗示我们的应用程序中存在设计问题或代码异味。

为什么？首先，依赖于几个具体实现的类可能会紧密耦合，其次，这几乎总是会导致代码难以测试。理想情况下，类不应该负责获取其依赖项，如果可能，它们应该从外部注入。

因此，我们是否可以重构代码以使其更易于测试，这始终值得研究。当然，这并不总是可能的，有时，我们需要在构造类之后暂时替换它的行为。

这在几种情况下可能特别有用：

- 测试难以达到的场景 - 特别是当我们的被测类具有复杂的对象层次结构时
- 测试与外部库或框架的交互
- 使用遗留代码

在下面的部分中，我们将了解如何使用Mockito的MockConstruction来应对其中一些情况，以便控制对象的创建并指定它们在构造时的行为方式。

## 4. Mock构造函数

让我们首先创建一个简单的Fruit类，这将是我们第一个单元测试的重点：

```java
public class Fruit {

    public String getName() {
        return "Apple";
    }

    public String getColour() {
        return "Red";
    }
}

```

现在让我们继续编写测试，其中我们Mock对Fruit类进行的构造函数调用：

```java
@Test
void givenMockedContructor_whenFruitCreated_thenMockIsReturned() {
    assertEquals("Apple", new Fruit().getName());
    assertEquals("Red", new Fruit().getColour());

    try (MockedConstruction<Fruit> mock = mockConstruction(Fruit.class)) {
        Fruit fruit = new Fruit();
        when(fruit.getName()).thenReturn("Banana");
        when(fruit.getColour()).thenReturn("Yellow");

        assertEquals("Banana", fruit.getName());
        assertEquals("Yellow", fruit.getColour());

        List<Fruit> constructed = mock.constructed();
        assertEquals(1, constructed.size());
    }
}
```

在我们的示例中，我们首先检查真实的Fruit对象是否返回所需的值。

现在，为了使Mock对象构造成为可能，我们将使用Mockito.mockConstruction()方法。该方法采用非抽象Java类来构建我们要Mock的结构。在本例中，是一个Fruit类。

我们在[try-with-resources](https://www.baeldung.com/java-try-with-resources)块中定义它。这意味着当我们的代码在 try 语句中调用Fruit对象的构造函数时，它会返回一个Mock对象。我们应该注意，构造函数不会在我们的作用域块之外被MockitoMock。

这是一个特别好的功能，因为它确保我们的Mock保持临时状态。众所周知，如果我们在测试运行期间使用Mock构造函数调用，由于运行测试的并发和顺序性质，这可能会对我们的测试结果产生不利影响。

## 5. 在另一个类中Mock构造函数

更现实的场景是，当我们有一个正在测试的类时，它会在内部创建一些我们想要Mock的对象。

通常，在被测试类的构造函数内，我们可能会创建我们想要从测试中Mock的新对象的实例。在此示例中，我们将了解如何做到这一点。

让我们首先定义一个简单的咖啡制作应用程序：

```java
public class CoffeeMachine {

    private Grinder grinder;
    private WaterTank tank;

    public CoffeeMachine() {
        this.grinder = new Grinder();
        this.tank = new WaterTank();
    }

    public String makeCoffee() {
        String type = this.tank.isEspresso() ? "Espresso" : "Americano";
        return String.format("Finished making a delicious %s made with %s beans", type, this.grinder.getBeans());
    }
}
```

接下来，我们定义Grinder类：

```java
public class Grinder {

    private String beans;

    public Grinder() {
        this.beans = "Guatemalan";
    }

    public String getBeans() {
        return beans;
    }

    public void setBeans(String beans) {
        this.beans = beans;
    }
}
```

最后，我们添加WaterTank类：

```java
public class WaterTank {

    private int mils;

    public WaterTank() {
        this.mils = 25;
    }

    public boolean isEspresso() {
        return getMils() < 50;
    }
    
    //Getters and Setters
}
```

在这个简单的示例中，我们的CoffeeMachine在构建时创建了研磨机和水箱。我们有一个方法makeCoffee()，它打印出有关煮好的咖啡的消息。

现在，我们可以继续编写一些测试：

```java
@Test
 void givenNoMockedContructor_whenCoffeeMade_thenRealDependencyReturned() {
    CoffeeMachine machine = new CoffeeMachine();
    assertEquals("Finished making a delicious Espresso made with Guatemalan beans", machine.makeCoffee());
}
```

在第一个测试中，我们检查当我们不使用MockedConstruction 时，我们的咖啡机会返回内部的真实依赖项。

现在让我们看看如何返回这些依赖项的Mock：

```java
@Test
void givenMockedContructor_whenCoffeeMade_thenMockDependencyReturned() {
    try (MockedConstruction<WaterTank> mockTank = mockConstruction(WaterTank.class); 
      MockedConstruction<Grinder> mockGrinder = mockConstruction(Grinder.class)) {

        CoffeeMachine machine = new CoffeeMachine();

        WaterTank tank = mockTank.constructed().get(0);
        Grinder grinder = mockGrinder.constructed().get(0);

        when(tank.isEspresso()).thenReturn(false);
        when(grinder.getBeans()).thenReturn("Peruvian");

        assertEquals("Finished making a delicious Americano made with Peruvian beans", machine.makeCoffee());
    }
}
```

在此测试中，当我们调用Grinder和WaterTank的构造函数时，我们使用mockConstruction返回Mock实例 。然后，我们使用标准何时表示法指定这些Mock的期望。

这一次，当我们运行测试时，Mockito 确保Grinder和WaterTank的构造函数返回具有指定行为的Mock实例，从而允许我们单独测试makeCoffee方法。

## 6. 处理构造函数参数

另一个常见的用例是能够处理带有参数的构造函数。

值得庆幸的是，mockedConstruction提供了一种机制，允许我们访问传递给构造函数的参数：

让我们向WaterTank添加一个新的构造函数：

```java
public WaterTank(int mils) {
    this.mils = mils;
}
```

同样，我们还向 Coffee 应用程序添加一个新的构造函数：

```java
public CoffeeMachine(int mils) {
    this.grinder = new Grinder();
    this.tank = new WaterTank(mils);
}
```

最后，我们可以添加另一个测试：

```java
@Test
void givenMockedContructorWithArgument_whenCoffeeMade_thenMockDependencyReturned() {
    try (MockedConstruction<WaterTank> mockTank = mockConstruction(WaterTank.class, 
      (mock, context) -> {
          int mils = (int) context.arguments().get(0);
          when(mock.getMils()).thenReturn(mils);
      }); 
      MockedConstruction<Grinder> mockGrinder = mockConstruction(Grinder.class)) {
          CoffeeMachine machine = new CoffeeMachine(100);

          Grinder grinder = mockGrinder.constructed().get(0);
          when(grinder.getBeans()).thenReturn("Kenyan");
          assertEquals("Finished making a delicious Americano made with Kenyan beans", machine.makeCoffee());
        }
    }
```

这一次，我们使用 lambda 表达式来处理带参数的WaterTank构造函数。lambda 接收Mock实例和构造上下文，允许我们访问传递给构造函数的参数。

然后，我们可以使用这些参数来设置getMils方法所需的行为。

## 7. 更改默认的Mock行为

需要注意的是，对于方法，默认情况下我们不会存根Mock返回 null。我们可以将 Fruit 示例更进一步，让Mock行为像真正的Fruit实例一样：

```java
@Test
void givenMockedContructorWithNewDefaultAnswer_whenFruitCreated_thenRealMethodInvoked() {
    try (MockedConstruction<Fruit> mock = mockConstruction(Fruit.class, withSettings().defaultAnswer(Answers.CALLS_REAL_METHODS))) {

        Fruit fruit = new Fruit();

        assertEquals("Apple", fruit.getName());
        assertEquals("Red", fruit.getColour());
    }
}
```

这次，我们将一个额外的参数[MockSettings](https://www.baeldung.com/mockito-mocksettings)传递给mockConstruction方法，告诉它为我们没有存根的方法创建一个Mock，该Mock的行为就像真实的Fruit实例一样。

## 八、总结

在这篇简短的文章中，我们看到了几个如何使用Mockito来Mock构造函数调用的示例。总而言之，Mockito 提供了一个优雅的解决方案，可以在当前线程和用户定义的范围内生成构造函数调用的Mock。