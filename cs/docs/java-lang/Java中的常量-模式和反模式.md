## 1. 概述

在本文中，我们将学习如何在Java中使用常量，重点是常见模式和反模式。

我们将从定义常量的一些基本约定开始。从那里开始，我们将继续讨论常见的反模式，然后再看看常见的模式。

## 2. 基础知识

常量是一个变量，它的值在定义后就不会改变。

让我们看一下定义常量的基础知识：

```java
private static final int OUR_CONSTANT = 1;
```

我们将要研究的一些模式将解决[公共](https://www.baeldung.com/java-public-keyword)或[私有](https://www.baeldung.com/java-private-keyword) [访问修饰符](https://www.baeldung.com/java-access-modifiers)的决定。我们将常量设置为static和[final](https://www.baeldung.com/java-final)并赋予它们适当的类型，无论是Java原语、类还是枚举。名称应全部为大写字母，单词之间用下划线分隔，有时称为 screaming snake case。最后，我们提供价值本身。

## 3.反模式

首先，让我们从了解不该做什么开始。让我们看一下在使用Java常量时可能遇到的几个常见反模式。

### 3.1. 神奇数字

[幻数](https://www.baeldung.com/cs/antipatterns-magic-numbers)是代码块中的数字文字：

```java
if (number == 3.14159265359) {
    // ...
}
```

其他开发人员很难理解它们。此外，如果我们在整个代码中使用数字，则很难处理更改值。相反，我们应该将数字定义为常量。

### 3.2. 一个大的全局常量类

当我们开始一个项目时，可能会很自然地创建一个名为Constants或Utils的类，目的是在其中定义应用程序的所有常量。对于较小的项目，这可能没问题，但让我们考虑一下这不是理想解决方案的几个原因。

首先，假设我们的常量类中有一百个或更多的常量。如果不维护该类，既要跟上文档，又要偶尔将常量重构为逻辑分组，那么它将变得非常不可读。我们甚至可能会得到名称略有不同的重复常量。除了最小的项目外，这种方法很可能会给我们带来可读性和可维护性问题。

除了维护Constants类本身的后勤工作之外，我们还通过鼓励与这个全局常量类和我们应用程序的其他各个部分的过多相互依赖而引发其他可维护性问题。

在更技术性的方面，Java 编译器将常量的值放入我们使用它们的类中的引用变量中。因此，如果我们更改常量类中的一个常量并且只重新编译该类而不是引用类，我们可能会得到不一致的常量值。

### 3.3. 常量接口反模式

常量接口模式是指我们定义一个接口，其中包含特定功能的所有常量，然后让需要这些功能的类来实现该接口。

让我们为计算器定义一个常量接口：

```java
public interface CalculatorConstants {
    double PI = 3.14159265359;
    double UPPER_LIMIT = 0x1.fffffffffffffP+1023;
    enum Operation {ADD, SUBTRACT, MULTIPLY, DIVIDE};
}
```

接下来，我们将实现我们的CalculatorConstants接口：

```java
public class GeometryCalculator implements CalculatorConstants {    
    public double operateOnTwoNumbers(double numberOne, double numberTwo, Operation operation) {
       // Code to do an operation
    }
}
```

反对使用常量接口的第一个论点是它违背了接口的目的。我们打算使用接口来为我们的实现类将要提供的行为创建契约。当我们创建一个充满常量的接口时，我们并没有定义任何行为。

其次，使用常量接口会使我们面临由场阴影引起的运行时问题。让我们通过在我们的GeometryCalculator类中定义一个UPPER_LIMIT常量来看看这是如何发生的：

```java
public static final double UPPER_LIMIT = 100000000000000000000.0;
```

一旦我们在我们的GeometryCalculator类中定义了该常量，我们就将该值隐藏在我们类的CalculatorConstants接口中。然后我们可能会得到意想不到的结果。

反对这种反模式的另一个论点是它会导致名称空间污染。我们的CalculatorConstants现在将位于实现该接口的任何类及其任何子类的命名空间中。

## 4.图案

早些时候，我们研究了定义常量的适当形式。让我们看看在我们的应用程序中定义常量的其他一些好的做法。

### 4.1. 一般良好做法

如果常量在逻辑上与类相关，我们可以在那里定义它们。如果我们将一组常量视为枚举类型的成员，我们可以使用枚举来定义它们。

让我们在计算器类中定义一些常量：

```java
public class Calculator {
    public static final double PI = 3.14159265359;
    private static final double UPPER_LIMIT = 0x1.fffffffffffffP+1023;
    public enum Operation {
        ADD,
        SUBTRACT,
        DIVIDE,
        MULTIPLY
    }

    public double operateOnTwoNumbers(double numberOne, double numberTwo, Operation operation) {
        if (numberOne > UPPER_LIMIT) {
            throw new IllegalArgumentException("'numberOne' is too large");
        }
        if (numberTwo > UPPER_LIMIT) {
            throw new IllegalArgumentException("'numberTwo' is too large");
        }
        double answer = 0;
        
        switch(operation) {
            case ADD:
                answer = numberOne + numberTwo;
                break;
            case SUBTRACT:
                answer = numberOne - numberTwo;
                break;
            case DIVIDE:
                answer = numberOne / numberTwo;
                break;
            case MULTIPLY:
                answer = numberOne  numberTwo;
                break;
        }
        
        return answer;
    }
}
```

在我们的示例中，我们为UPPER_LIMIT定义了一个常量 ，我们只打算在Calculator类中使用它，因此我们将其设置为private。我们希望其他类能够使用PI和Operation枚举，因此我们将它们设置为public。

让我们考虑一下对Operation使用枚举的一些优势。第一个优点是它限制了可能的值。想象一下，我们的方法采用一个字符串作为操作值，并期望提供四个常量字符串之一。我们可以很容易地预见到调用该方法的开发人员发送他们自己的字符串值的场景。使用enum，值仅限于我们定义的值。我们还可以看到枚举特别适合在[switch](https://www.baeldung.com/java-switch)语句中使用。

### 4.2. 常量类

现在我们已经了解了一些通用的良好实践，让我们考虑一下常量类可能是个好主意的情况。假设我们的应用程序包含一组需要进行各种数学计算的类。在这种情况下，我们在该包中为我们将在计算类中使用的常量定义一个常量类可能是有意义的。

让我们创建一个MathConstants类：

```java
public final class MathConstants {
    public static final double PI = 3.14159265359;
    static final double GOLDEN_RATIO = 1.6180;
    static final double GRAVITATIONAL_ACCELERATION = 9.8;
    static final double EULERS_NUMBER = 2.7182818284590452353602874713527;
    
    public enum Operation {
        ADD,
        SUBTRACT,
        DIVIDE,
        MULTIPLY
    }
    
    private MathConstants() {
        
    }
}
```

我们应该注意的第一件事是我们的类是final以防止它被扩展。此外，我们已经定义了一个私有构造函数，所以它不能被实例化。最后，我们可以看到我们应用了本文前面讨论的其他良好实践。我们的常量PI是公开的，因为我们预计需要在我们的包之外访问它。我们将其他常量保留为package-private，因此我们可以在我们的包中访问它们。我们已将所有常量设为静态和最终常量，并在尖叫的蛇形盒中命名它们。这些操作是一组特定的值，所以我们使用了一个枚举来定义它们。

我们可以看到我们特定的包级常量类不同于大型全局常量类，因为它本地化到我们的包中并包含与该包的类相关的常量。

## 5.总结

在本文中，我们考虑了在Java中使用常量时看到的一些最流行模式和反模式的优缺点。在介绍反模式之前，我们从一些基本的格式化规则开始。在了解了几个常见的反模式之后，我们研究了我们经常看到的应用于常量的模式。