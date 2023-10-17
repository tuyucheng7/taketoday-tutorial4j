## 1. 概述

[继承](https://www.baeldung.com/java-inheritance)和组合，以及抽象、封装和多态是[面向对象编程](https://en.wikipedia.org/wiki/Object-oriented_programming)(OOP)的基石。

在本教程中，我们将介绍继承和组合的基础知识，并将重点放在发现两种关系类型之间的差异上。

## 2. 继承的基础知识

**继承是一种强大但被过度使用和误用的机制**。

简单地说，通过继承，基类(也称为基类型)定义给定类型的公共状态和行为，并让子类(也称为子类型)提供该状态和行为的专用版本。

为了清楚地了解如何使用继承，让我们创建一个简单的示例：一个基类Person定义一个人的公共字段和方法，而子类Waitress和Actress提供额外的、细粒度的方法实现。

这是Person类：

```java
public class Person {
    private final String name;

    // other fields, standard constructors, getters
}
```

这些是子类：

```java
public class Waitress extends Person {

    public String serveStarter(String starter) {
        return "Serving a " + starter;
    }

    // additional methods/constructors
}
```

```java
public class Actress extends Person {

    public String readScript(String movie) {
        return "Reading the script of " + movie;
    }

    // additional methods/constructors
}
```

此外，让我们创建一个单元测试来验证Waitress和Actress类的实例也是Person的实例，从而表明在类型级别满足“is-a”条件：

```java
@Test
public void givenWaitressInstance_whenCheckedType_thenIsInstanceOfPerson() {
    assertThat(new Waitress("Mary", "mary@domain.com", 22))
        .isInstanceOf(Person.class);
}
    
@Test
public void givenActressInstance_whenCheckedType_thenIsInstanceOfPerson() {
    assertThat(new Actress("Susan", "susan@domain.com", 30))
        .isInstanceOf(Person.class);
}
```

**在这里强调继承的语义方面很重要**。除了重用Person类的实现之外，我们还在基本类型Person和子类型Waitress和Actress之间**创建了一个定义明确的“is-a”关系**。女服务员和女演员实际上是人。

这可能会让我们问：**在哪些用例中继承是正确的方法**？

**如果子类型满足“is-a”条件并且主要在类层次结构的下方提供附加功能，那么继承就是可行的方法**。

当然，方法覆盖是允许的，只要被覆盖的方法保留由[里氏替换原则](https://en.wikipedia.org/wiki/Liskov_substitution_principle)提倡的基本类型/子类型可替换性。

此外，我们应该记住，**子类型继承了基类型的API**，在某些情况下，这可能是矫枉过正或仅仅是不受欢迎的。

否则，我们应该改用组合。

## 3. 设计模式中的继承

虽然共识是我们应该尽可能支持组合而不是继承，但在一些典型的用例中继承也有其用武之地。

### 3.1 层超类型模式

在这种情况下，**我们使用继承在每一层的基础上将公共代码移动到基类(超类型)**。

这是领域层中此模式的基本实现：

```java
public class Entity {

    protected long id;

    // setters
}
```

```java
public class User extends Entity {
    // additional fields and methods   
}
```

我们可以将相同的方法应用于系统中的其他层，例如服务层和持久层。

### 3.2 模板方法模式

在模板方法模式中，**我们可以使用基类来定义算法的不变部分，然后在子类中实现可变部分**：

```java
public abstract class ComputerBuilder {

    public final Computer buildComputer() {
        addProcessor();
        addMemory();
    }

    public abstract void addProcessor();

    public abstract void addMemory();
}
```

```java
public class StandardComputerBuilder extends ComputerBuilder {

    @Override
    public void addProcessor() {
        // method implementation
    }

    @Override
    public void addMemory() {
        // method implementation
    }
}
```

## 4. 组合基础

组合是OOP提供的另一种重用实现的机制。

简而言之，**组合允许我们对由其他对象组成的对象建模**，从而定义它们之间的“has-a”关系。

此外，**组合是[关联](https://en.wikipedia.org/wiki/Association_(object-oriented_programming))的最强形式，这意味着当一个对象被销毁时，组成或包含一个对象的对象也会被销毁**。

为了更好地理解组合的工作原理，让我们假设我们需要使用代表计算机的对象。

计算机由不同的部分组成，包括微处理器、内存、声卡等，因此我们可以将计算机及其每个部分建模为单独的类。

Computer类的简单实现如下所示：

```java
public class Computer {

    private Processor processor;
    private Memory memory;
    private SoundCard soundCard;

    // standard getters/setters/constructors

    public Optional<SoundCard> getSoundCard() {
        return Optional.ofNullable(soundCard);
    }
}
```

以下类为微处理器、内存和声卡建模(为简洁起见省略了接口)：

```java
public class StandardProcessor implements Processor {

    private String model;

    // standard getters/setters
}
```

```java
public class StandardMemory implements Memory {

    private String brand;
    private String size;

    // standard constructors, getters, toString
}
```

```java
public class StandardSoundCard implements SoundCard {

    private String brand;

    // standard constructors, getters, toString
}
```

很容易理解推动组合而不是继承背后的动机。**在可以在给定类和其他类之间建立语义上正确的“has-a”关系的每个场景中，组合都是正确的选择**。

在上面的例子中，Computer满足了对其部件建模的类的“has-a”条件。

还值得注意的是，在这种情况下，**当且仅当对象不能在另一个Computer对象中重用时，包含Computer对象才拥有所包含对象的所有权**。如果可以的话，我们将使用聚合而不是组合，在组合中不暗示所有权。

## 5. 没有抽象的组合

或者，我们可以通过对Computer类的依赖项进行硬编码来定义组合关系，而不是在构造函数中声明它们：

```java
public class Computer {

    private StandardProcessor processor = new StandardProcessor("Intel I3");
    private StandardMemory memory = new StandardMemory("Kingston", "1TB");

    // additional fields / methods
}
```

**当然，这将是一个严格的、紧耦合的设计，因为我们会使Computer强烈依赖于Processor和Memory的特定实现**。

我们不会利用接口和[依赖注入](https://en.wikipedia.org/wiki/Dependency_injection)提供的抽象级别。

通过基于接口的初始设计，我们得到了一个松耦合的设计，也更容易测试。

## 6. 总结

在本文中，我们了解了Java中继承和组合的基础知识，并深入探讨了两种关系类型(“is-a”与“has-a”)之间的区别。