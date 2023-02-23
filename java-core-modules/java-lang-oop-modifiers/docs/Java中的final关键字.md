## 1. 概述

虽然继承使我们能够重用现有代码，但有时出于各种原因我们确实需要**对可扩展性设置限制**；final关键字使我们能够做到这一点。

在本教程中，我们将了解final关键字对类、方法和变量的含义。

## 2. 最终类

**标记为final的类无法扩展**。如果我们查看Java核心库的代码，我们会发现那里有许多最终类。一个例子是String类。

考虑一下这种情况，如果我们可以扩展String类，覆盖它的任何方法，并用我们特定的String子类的实例替换所有String实例。

然后，对String对象的操作结果将变得不可预测。鉴于到处都在使用String类，这是不可接受的。这就是String类被标记为final的原因。

任何从final类继承的尝试都会导致编译器错误。为了证明这一点，让我们创建最终类Cat：

```java
public final class Cat {

    private int weight;

    // standard getter and setter
}
```

然后我们尝试扩展它：

```java
public class BlackCat extends Cat {
}
```

我们将看到编译器错误：

```shell
The type BlackCat cannot subclass the final class Cat
```

请注意，**类声明中的final关键字并不意味着此类的对象是不可变的。我们可以自由更改Cat对象的字段**：

```java
Cat cat = new Cat();
cat.setWeight(1);

assertEquals(1, cat.getWeight());
```

我们只是无法扩展它。

如果我们严格遵循良好设计的规则，我们应该仔细创建和记录一个类，或者出于安全原因将其声明为final 。但是，在创建最终类时，我们应该谨慎行事。

请注意，使一个类最终化意味着没有其他程序员可以改进它。想象一下，我们正在使用一个类，但没有它的源代码，并且有一个方法有问题。

如果这个类是final的，我们就不能通过扩展它来覆盖方法以解决问题。换句话说，我们失去了可扩展性，这是面向对象编程的好处之一。

## 3. 最终方法

**标记为final的方法不能被覆盖**。当我们设计一个类并觉得某个方法不应该被重写时，我们可以让这个方法成为final。我们还可以在Java核心库中找到很多final方法。

有时我们并不需要完全禁止一个类的扩展，而只是防止重写某些方法。Thread类就是一个很好的例子。扩展它并因此创建自定义线程类是合法的。但它的isAlive()方法是最终的。

此方法检查线程是否处于活动状态。由于多种原因，不可能正确地覆盖isAlive()方法。其中之一是这种方法是本地的。本机代码以另一种编程语言实现，通常特定于其运行的操作系统和硬件。

让我们创建一个Dog类并将其sound()方法设置为final：

```java
public class Dog {
    public final void sound() {
        // ...
    }
}
```

现在让我们扩展Dog类并尝试覆盖它的sound()方法：

```java
public class BlackDog extends Dog {
    public void sound() {
    }
}
```

我们将看到编译器错误：

```shell
- overrides
cn.tuyucheng.taketoday.finalkeyword.Dog.sound
- Cannot override the final method from Dog
sound() method is final and can’t be overridden
```

如果我们类的某些方法被其他方法调用，我们应该考虑将被调用的方法设为final。否则，覆盖它们会影响调用者的工作并导致令人惊讶的结果。

如果我们的构造函数调用了其他方法，基于上述原因，我们通常应该将这些方法声明为final。

将类的所有方法都设置为final和将类本身标记为final之间有什么区别？在第一种情况下，我们可以扩展类并向其添加新方法。

在第二种情况下，我们不能这样做。

## 4. 最终变量

**不能重新分配标记为final的变量**。一旦final变量被初始化，它就不能被改变。

### 4.1 最终原始类型变量

让我们声明一个原始最终变量i，然后将1赋值给它。

让我们尝试为其分配一个值2：

```java
public void whenFinalVariableAssign_thenOnlyOnce() {
    final int i = 1;
    // ...
    i=2;
}
```

编译器报错：

```shell
The final local variable i may already have been assigned
```

### 4.2 最终引用变量

如果我们有一个final引用变量，我们也不能重新分配它。但**这并不意味着它所引用的对象是不可变的**。我们可以自由地改变这个对象的属性。

为了证明这一点，让我们声明最终引用变量cat并对其进行初始化：

```java
final Cat cat = new Cat();
```

如果我们尝试重新分配它，我们将看到一个编译器错误：

```shell
The final local variable cat cannot be assigned. It must be blank and not using a compound assignment
```

但是我们可以更改Cat实例的属性：

```java
cat.setWeight(5);

assertEquals(5, cat.getWeight());
```

### 4.3 最终字段

**最终字段可以是常量或一次性写入字段**。为了区分它们，我们应该问一个问题-如果我们要序列化对象，我们会包括这个字段吗？如果不是，那么它不是对象的一部分，而是一个常量。

请注意，根据命名约定，类常量应为大写，组件之间用下划线(“_”)字符分隔：

```java
static final int MAX_WIDTH = 999;
```

请注意，**任何final字段都必须在构造函数完成之前进行初始化**。

对于静态最终字段，这意味着我们可以初始化它们：

-   如上例所示的声明
-   在静态初始化块中

对于实例最终字段，这意味着我们可以初始化它们：

-   声明时
-   在实例初始化块中
-   在构造函数中

否则，编译器会给我们一个错误。

### 4.4 最终参数

final关键字放在方法参数之前也是合法的。**不能在方法内部更改最终参数**：

```java
public void methodWithFinalArguments(final int x) {
    x=1;
}
```

上面的赋值导致编译器错误：

```shell
The final local variable x cannot be assigned. It must be blank and not using a compound assignment
```

## 5. 总结

在本文中，我们了解了final关键字对类、方法和变量的含义。虽然我们在内部代码中可能不会经常使用final关键字，但它可能是一个很好的设计解决方案。