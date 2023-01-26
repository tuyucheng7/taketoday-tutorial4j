## 一、概述

在本教程中，我们将使用 Java 讨论 Monad 及其定义。这个想法是为了理解这个概念，它解决了什么问题，以及 Java 语言是如何实现它的。

到最后，我们希望人们能够理解 Monad 以及如何充分利用它。

## 2.概念

**Monad 是函数式编程世界中流行的一种设计模式**。然而，它实际上起源于一个叫做[范畴论](https://en.wikipedia.org/wiki/Monad_(category_theory))的数学领域。本文将重点介绍 Monad 对软件工程领域的定义。尽管这两个定义有很多相似之处，但软件定义和该领域的行话更符合我们的上下文。

**简而言之，一个通用的概念是一个对象，它可以根据变换将自己映射到不同的结果**。

## 3. 设计模式

Monad 是封装值和计算的容器或结构。它们必须有两个基本操作：

-   *Unit* : monads 代表一种包装给定值的类型，这个操作负责包装值。例如，在 Java 中，此操作仅通过利用[泛型就可以接受来自不同类型的值](https://www.baeldung.com/java-generics)
-   *Bind*：此操作允许使用保存的值执行转换并返回一个新的 monad 值（monad 类型中的值包装）

尽管如此，还是有一些 monad 必须遵守的属性：

-   左身份：当应用于 monad 时，它应该产生与将转换应用于持有值相同的结果
-   正确的标识：发送 monad 转换（将值转换为 monad）时，yield 结果必须与将值包装在新的 monad 中相同
-   关联性：链接转换时，转换的嵌套方式无关紧要

函数式编程的挑战之一是允许在不损失可读性的情况下对此类操作进行流水线处理。这是采用 monad 概念的原因之一。**Monad 是函数式范例的基础，有助于实现声明式编程**。

## 4.Java解释

Java 8 通过像*[Optional](https://www.baeldung.com/java-optional)这样的类实现了 Monad 设计模式。* 不过，我们先来看一段添加*Optional*类之前的代码：

```java
public class MonadSample1 {
    //... 
    private double multiplyBy2(double n) {
        return n * 2;
    }

    private double divideBy2(double n) {
        return n / 2;
    }

    private double add3(double n) {
        return n + 3;
    }

    private double subtract1(double n) {
        return n - 1;
    }

    public double apply(double n) {
        return subtract1(add3(divideBy2(multiplyBy2(multiplyBy2(n)))));
    }
    //... 
复制
public class MonadSampleUnitTest {
    //...
    @Test
    public void whenNotUsingMonad_shouldBeOk() {
        MonadSample1 test = new MonadSample1();
        Assert.assertEquals(6.0, test.apply(2), 0.000);
    }
    //... 
}复制
```

我们可以观察到，*apply*方法看起来很难读，但有什么替代方法呢？也许是以下内容：

```java
public class MonadSample2 {
    //... 
    public double apply(double n) { 
        double n1 = multiplyBy2(n); 
        double n2 = multiplyBy2(n1); 
        double n3 = divideBy2(n2); 
        double n4 = add3(n3); 
        return subtract1(n4); 
    } 
    //...复制
public class MonadSampleUnitTest {
    //...
    @Test
    public void whenNotUsingMonadButUsingTempVars_shouldBeOk() {
        MonadSample2 test = new MonadSample2();
        Assert.assertEquals(6.0, test.apply(2), 0.000);
    }
    //...
}复制
```

这似乎更好，但它看起来仍然过于冗长。*那么让我们看看使用Optional*会是什么样子：

```java
public class MonadSample3 {
    //...
    public double apply(double n) {
        return Optional.of(n)
          .flatMap(value -> Optional.of(multiplyBy2(value)))
          .flatMap(value -> Optional.of(multiplyBy2(value)))
          .flatMap(value -> Optional.of(divideBy2(value)))
          .flatMap(value -> Optional.of(add3(value)))
          .flatMap(value -> Optional.of(subtract1(value)))
          .get();
    }
    //...复制
public class MonadSampleUnitTest {
    //...
    @Test
    public void whenUsingMonad_shouldBeOk() {
        MonadSample3 test = new MonadSample3();
        Assert.assertEquals(6.0, test.apply(2), 0.000);
    }
    //...
}复制
```

上面的代码看起来更干净。另一方面，这种设计允许开发人员根据需要应用尽可能多的后续转换，而不会牺牲可读性和减少临时变量声明的冗长。

还有更多; 想象一下，如果这些函数中的任何一个可以产生*空*值。在这种情况下，我们将不得不在每次转换之前添加验证，从而使代码更加冗长。这实际上是*Optional*类的主要目的。这个想法是为了避免使用*null*并提供一种易于使用的方法来将转换应用于对象，以便当它们不为*null 时，*将以空安全的方式执行一系列声明。也可以检查*Optional*包装的值是否为空（值为 null）。

### 4.1. 可选注意事项

正如开头所描述的，Monads需要有一些操作和属性，那么让我们看看Java实现中的那些属性。首先，为什么不检查 Monad 必须具有的操作：

-   **对于\*Unit\*操作，Java 提供了不同的风格，例如\*Optional.of()\*和*****Optional.nullable()**。*正如我们想象的那样，一个接受*空*值，另一个不接受
-   **至于\*Bind\*函数，Java 提供了\*Optional.flatMap()\*操作**，在代码示例中介绍

一个不在 Monad 定义中的特性是*映射*操作。它是一种类似于*flatMap*的转换和链式操作。两者之间的区别在于*map*操作接收一个转换，该转换返回一个原始值，由 API 在内部包装。虽然*flatMap*已经返回一个包装值，API 返回该值以形成管道。

现在，让我们检查一下 Monad 的属性：

```java
public class MonadSample4 {
    //... 
    public boolean leftIdentity() {
         Function<Integer, Optional> mapping = value -> Optional.of(value + 1);
         return Optional.of(3).flatMap(mapping).equals(mapping.apply(3));
     }

     public boolean rightIdentity() {
         return Optional.of(3).flatMap(Optional::of).equals(Optional.of(3));
     }

     public boolean associativity() {
         Function<Integer, Optional> mapping = value -> Optional.of(value + 1);
         Optional leftSide = Optional.of(3).flatMap(mapping).flatMap(Optional::of);
         Optional rightSide = Optional.of(3).flatMap(v -> mapping.apply(v).flatMap(Optional::of));
         return leftSide.equals(rightSide);
     }
     //... 
复制
public class MonadSampleUnitTest {
    //...  
    @Test
    public void whenTestingMonadProperties_shouldBeOk() {
        MonadSample4 test = new MonadSample4();
        Assert.assertEquals(true, test.leftIdentity());
        Assert.assertEquals(true, test.rightIdentity());
        Assert.assertEquals(true, test.associativity());
    }
    //...
}复制
```

乍一看，所有的属性似乎都符合要求，Java 也有妥善的 Monad 实现，但实际上并非如此。让我们再做一个测试：

```java
class MonadSample5 {
    //...
    public boolean fail() {
        Function<Integer, Optional> mapping = value -> Optional.of(value == null ? -1 : value + 1);
        return Optional.ofNullable((Integer) null).flatMap(mapping).equals(mapping.apply(null));
    }
    //...复制
class MonadSampleUnitTest {
    @Test
    public void whenBreakingMonadProperties_shouldBeFalse() {
        MonadSample5 test = new MonadSample5();
        Assert.assertEquals(false, test.fail());
    }
    //...
}复制
```

**正如所观察到的，Monad 的左标识属性被破坏了**。**[实际上，根据这个讨论](https://mail.openjdk.org/pipermail/lambda-dev/2013-February/008305.html)****，这似乎是一个有意识的决定。JDK 团队的一位成员说\*Optional\*的范围比其他语言更窄，他们不打算超过这个范围**。在其他情况下，此类属性可能不成立。

实际上，其他 API，如流，也有类似的设计，但并不打算完全实现 Monad 规范。

## 5.结论

在本文中，我们了解了 Monad 的概念、它们是如何在 Java 中引入的，以及这种实现的细微差别。

可以争辩说 Java 实际上并不是 Monad 实现，并且在设计空安全时，他们违反了原则。但是，这种模式的许多好处仍然存在。