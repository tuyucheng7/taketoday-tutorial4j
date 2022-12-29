## 1. 概述

在本教程中，**我们将讨论如何在Kotlin中定义和实现接口**。

我们还将了解一个类如何实现多个接口，这肯定会导致冲突，我们将学习Kotlin解决冲突的机制。

## 2. Kotlin中的接口

接口是在面向对象编程中为类提供描述或契约的一种方式，它们可能以抽象或具体的方式包含属性和函数，具体取决于编程语言，我们将详细介绍Kotlin中的接口。

Kotlin中的接口类似于许多其他语言(如Java)中的接口，但是它们有特定的语法，让我们在接下来的几个小节中回顾它们。

### 2.1 定义接口

让我们从在Kotlin中定义我们的第一个接口开始：

```kotlin
interface SimpleInterface
```

这是最简单的接口，完全是空的，**这些接口也称为标记接口**。

现在让我们向我们的接口添加一些函数：

```kotlin
interface SimpleInterface {
    fun firstMethod(): String
    fun secondMethod(): String {
        return("Hello, World!")
    }
}
```

我们在之前定义的接口中添加了两个方法：

-   其中一个叫做firstMethod是一个抽象方法
-   而另一个名为secondMethod的方法有一个默认实现。

现在让我们继续向我们的接口添加一些属性：

```kotlin
interface SimpleInterface {
    val firstProp: String
    val secondProp: String
        get() = "Second Property"
    fun firstMethod(): String
    fun secondMethod(): String {
        return("Hello, from: " + secondProp)
    }
}
```

在这里，我们向接口添加了两个属性：

-   其中一个名为firstProp的类型是String，并且是抽象的
-   第二个名为secondProp的类型也是String，但它为其访问器定义了一个实现。

请注意，**接口中的属性无法维护状态**，所以下面是Kotlin中的非法表达式：

```kotlin
interface SimpleInterface {
    val firstProp: String = "First Property" // Illegal declaration
}
```

### 2.2 实现接口

现在我们已经定义了一个基本接口，让我们看看如何在Kotlin的类中实现它：

```kotlin
class SimpleClass: SimpleInterface {
    override val firstProp: String = "First Property"
    override fun firstMethod(): String {
        return("Hello, from: " + firstProp)
    }
}
```

请注意，当我们将SimpleClass定义为SimpleInterface的实现时，**我们只需提供抽象属性和函数的实现**。但是，我们也**可以覆盖任何先前定义的属性或函数**。

现在让我们覆盖我们类中所有先前定义的属性和函数：

```kotlin
class SimpleClass : SimpleInterface {
    override val firstProp: String = "First Property"
    override val secondProp: String
        get() = "Second Property, Overridden!"

    override fun firstMethod(): String {
        return ("Hello, from: " + firstProp)
    }
    override fun secondMethod(): String {
        return ("Hello, from: " + secondProp + firstProp)
    }
}
```

在这里，我们覆盖了先前在接口SimpleInterface中定义的属性secondProp和函数secondFunction。

### 2.3 通过委托实现接口

委托是面向对象编程中的一种设计模式，用于**通过组合而不是继承来实现代码可重用性**。虽然这可以在许多语言(比如Java)中实现，但**Kotlin对通过委派实现具有原生支持**。

如果我们从一个基本的接口和类开始：

```kotlin
interface MyInterface {
    fun someMethod(): String
}

class MyClass() : MyInterface {
    override fun someMethod(): String {
        return ("Hello, World!")
    }
}
```

到目前为止，没有什么新鲜事。但是现在，我们可以定义另一个通过委托实现MyInterface的类：

```kotlin
class MyDerivedClass(myInterface: MyInterface) : MyInterface by myInterface
```

MyDerivedClass需要一个委托作为实际实现接口MyInterface的参数。

让我们看看如何通过委托调用接口的函数：

```kotlin
val myClass = MyClass()
MyDerivedClass(myClass).someMethod()
```

这里我们实例化了MyClass并将其用作调用MyDerivedClass接口函数的委托，实际上它从未直接实现这些函数。

## 3. 多重继承

多重继承是面向对象编程范式中的一个关键概念，**这允许一个类从多个父对象(例如接口)继承特性**。

虽然这为对象建模提供了更大的灵活性，但它也有其自身的复杂性，其中之一就是“钻石问题”。

[Java 8有自己的机制来解决钻石问题](https://www.baeldung.com/kotlin-static-default-methods)，其他任何允许多重继承的语言也是如此。

让我们看看Kotlin是如何通过接口来解决它的。

### 3.1 继承多个接口

首先我们将定义两个简单的接口：

```kotlin
interface FirstInterface {
    fun someMethod(): String
    fun anotherMethod(): String {
        return("Hello, from anotherMethod in FirstInterface")
    }
}

interface SecondInterface {
    fun someMethod(): String {
        return("Hello, from someMethod in SecondInterface")
    }
    fun anotherMethod(): String {
        return("Hello, from anotherMethod in SecondInterface")
    }
}
```

**请注意，这两个接口都有具有相同契约的方法**。

现在让我们定义一个继承自这两个接口的类：

```kotlin
class SomeClass : FirstInterface, SecondInterface {
    override fun someMethod(): String {
        return ("Hello, from someMethod in SomeClass")
    }
    override fun anotherMethod(): String {
        return ("Hello, from anotherMethod in SomeClass")
    }
}
```

正如我们所见， SomeClass同时实现了FirstInterface和SecondInterface。虽然在语法上这很简单，但这里有一些语义需要注意，我们将在下一小节中讨论这个问题。

### 3.2 解决冲突

当实现多个接口时，一个类可能会继承一个函数，该函数在多个接口中具有相同契约的默认实现，这就引发了从实现类的实例调用此函数的问题。

**为了解决这个冲突，Kotlin要求子类为此类函数提供一个重写的实现，以使解决方案显式化**。

例如，上面的SomeClass实现了anotherMethod。但是，如果没有，Kotlin将不知道是调用FirstInterface还是SecondInterface的anotherMethod的默认实现，出于这个原因，**SomeClass必须实现anotherMethod**。

但是，someMethod有点不同，因为实际上没有冲突。FirstInterface没有为someMethod提供默认实现，也就是说，SomeClass仍然必须实现它，因为**Kotlin强制我们实现所有继承的函数，无论它们是在父接口中定义一次还是多次**。

### 3.3 解决钻石问题

当基础对象的两个子对象描述基础对象定义的特定行为时，就会出现“钻石问题”。现在，从这两个子对象继承的对象必须解析它订阅的继承行为。

Kotlin对这个问题的解决方案是通过上一小节中为多重继承定义的规则，让我们定义一些接口和一个实现类来呈现钻石问题：

```kotlin
interface BaseInterface {
    fun someMethod(): String
}

interface FirstChildInterface : BaseInterface {
    override fun someMethod(): String {
        return ("Hello, from someMethod in FirstChildInterface")
    }
}

interface SecondChildInterface : BaseInterface {
    override fun someMethod(): String {
        return ("Hello, from someMethod in SecondChildInterface")
    }
}

class ChildClass : FirstChildInterface, SecondChildInterface {
    override fun someMethod(): String {
        return super<SecondChildInterface>.someMethod()
    }
}
```

在这里我们定义了BaseInterface，它声明了一个名为someMethod的抽象函数。FirstChildInterface和SecondChildInterface接口都继承自BaseInterface并实现了函数someMethod。

现在，当我们实现继承自FirstChildInterface和SecondChildInterface的ChildClass时，我们有必要重写函数someMethod。然而，**即使我们必须重写该方法，我们仍然可以简单地调用super，就像我们在这里对SecondChildInterface所做的那样**。

## 4. Kotlin中的接口与抽象类的比较

Kotlin中的**抽象类是不能实例化的类**，这可能包含一个或多个属性和函数，这些属性和函数可以是抽象的，也可以是具体的。任何从抽象类继承的类都必须实现所有继承的抽象属性和函数，除非该类本身也被声明为抽象类。

### 4.1 接口和抽象类之间的差异

等等！这听起来不像接口的作用吗？

实际上，从一开始，抽象类与接口并没有太大区别。但是，有一些微妙的差异支配着我们做出的选择：

-  Kotlin中的一个类可以实现任意多个接口，但它只能从一个抽象类扩展
-  接口中的属性不能维护状态，而在抽象类中可以

### 4.2 我们什么时候应该使用什么？

接口只是定义类的蓝图，它们也可以选择有一些默认实现。另一方面，抽象类是由扩展类完成的不完整实现。

**通常应该使用接口来定义契约**，它引出了它承诺提供的能力,一个实现类负责实现这些承诺。但是，**应该使用抽象类来与扩展类共享部分特征**，扩展类可以更进一步来完成它。

## 5. 与Java接口的比较

**随着Java 8中Java接口的变化，它们已经非常接近Kotlin接口**。我们之前的一篇文章介绍了[Java 8中引入的新功能](https://www.baeldung.com/kotlin-8-new-features)，包括对接口的更改。

现在Java和Kotlin接口之间主要存在语法差异，一个突出的区别与关键字“override”有关。在Kotlin中，在实现从接口继承的抽象属性或函数时，必须使用关键字“override”来限定它们，Java中没有这样明确的要求。

## 6. 总结

在本教程中，我们讨论了Kotlin接口，以及如何定义和实现它们。然后我们讨论了从多个接口继承以及它们可能产生的冲突，我们了解了Kotlin如何处理此类冲突。

最后，我们讨论了接口与Kotlin中的抽象类的比较，我们还简要讨论了Kotlin接口与Java接口的比较。