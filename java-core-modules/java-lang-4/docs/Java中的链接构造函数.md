## 1. 概述

在这个简短的教程中，我们将了解如何在Java中链接构造函数。这是一种方便的设计模式，可以创建更少的重复代码并使其更具可读性。

首先，我们将解释什么是[构造函数](https://www.baeldung.com/java-constructors)链接。然后，我们将看到如何将它们链接到同一个类中并使用父类中的构造函数。最后但同样重要的是，我们将分析这种方法的优点和缺点。

## 2. 链接构造函数定义与示例

构造函数链接是调用一系列构造函数的过程。我们可以通过两种方式做到这一点：

-   通过使用this()关键字链接同一个类中的构造函数
-   通过使用super()关键字从父类链接构造函数

让我们看一下显示这两种方法的示例。

### 2.1. 在同一个类中链接构造函数

让我们定义一个包含一些属性的简单Person类：

```java
public class Person {
    private final String firstName;
    private final String middleName;
    private final String lastName;
    private final int age;

    //getters, equals and hashcode
}
```

firstName、lastName 和age 是我们希望在对象初始化期间始终设置的属性。然而，并不是每个人都有中间名。因此middleName属性是可选的。

考虑到这一点，我们将创建两个构造函数。第一个接受所有四个属性：

```java
public Person(String firstName, String middleName, String lastName, int age) {
    this.firstName = firstName;
    this.middleName = middleName;
    this.lastName = lastName;
    this.age = age;
}
```

第二个构造函数将接受三个必需的属性并省略可选字段：

```java
public Person(String firstName, String lastName, int age) {
    this(firstName, null, lastName, age);
}
```

我们正在使用this() 关键字。它必须始终是构造函数的第一行。这确保我们链接到的构造函数将首先被调用。

请记住，类中构造函数的顺序无关紧要。这意味着我们的第二个构造函数可以放在 Person 类中的任何位置，并且它仍然可以正常工作。

### 2.2. 从父类链接构造函数

让我们定义一个 继承自上一节中创建的Person类的Customer类：

```java
public class Customer extends Person {
    private final String loyaltyCardId;

   //getters, equals and hashcode
}
```

它包含一个额外的属性。现在，让我们以与Person类类似的方式创建两个构造函数：

```java
public Customer(String firstName, String lastName, int age, String loyaltyCardId) {
    this(firstName, null, lastName, age, loyaltyCardId);
}

public Customer(String firstName, String middleName, String lastName, int age, String loyaltyCardId) {
    super(firstName, middleName, lastName, age);
    this.loyaltyCardId = loyaltyCardId;
}
```

第一个构造函数使用 this()关键字链接到第二个构造函数，后者接受所有必需和可选属性。在这里，我们第一次使用super()关键字。

它的行为与this()关键字非常相似 。唯一的区别是super()链接到父类中相应的构造函数，而this()链接到同一类中的构造函数。

请记住，与前面的关键字类似，super()必须始终位于构造函数的第一行。 这意味着父类的构造函数首先被调用。之后，将该值分配给loyaltyCardId属性。

## 3. 链接构造函数的优点和缺点

构造函数链接的最大优点是 重复代码较少。换句话说，我们使用 Don't Repeat Yourself ( [DRY](https://www.baeldung.com/java-clean-code#2-dry-amp-kiss) ) 原则。这是因为我们在单个构造函数中完成了对象的初始化，通常是接受所有属性的构造函数。其他构造函数仅用于传递接收到的数据并为缺少的属性添加默认值。

链接构造函数使代码更具可读性。 我们不必在所有构造函数中重复属性分配。相反，我们在一个地方做这件事。

另一方面，我们在使用构造函数链时公开了更多构造对象的方法。这在某些项目中可能是一个重大缺陷。在这些情况下，我们应该寻找 [工厂](https://www.baeldung.com/creational-design-patterns#factory-method)或[构建器](https://www.baeldung.com/creational-design-patterns#builder)模式来隐藏多个构造函数。

## 4。总结

在本文中，我们讨论了Java中的构造函数链接。首先，我们解释了所谓的构造函数链接。然后，我们展示了如何使用同一类中的构造函数以及使用父类的构造函数来执行此操作。最后，我们讨论了链接构造函数的优点和缺点。