## 1. 概述

**有时我们需要使用伴生对象来定义将独立于该类的任何实例使用的类成员**，Kotlin编译器保证我们将拥有一个并且只有一个伴生对象实例，对于我们这些具有Java和C#背景的人来说，伴生对象类似于静态声明。

作为类级别声明的示例，包括静态工厂方法和抽象工厂在内的工厂设计模式是与类具有上下文关联的示例。

## 2. 声明伴生对象

下面是定义伴生对象的语法：

```kotlin
class ClassName {
    companion object {
        const val propertyName: String = "Something..."
        fun funName() {
            //...
        }
    }
}
```

**现在，可以通过引用类名来访问伴生对象主体中定义的属性和函数**：

```kotlin
val property = ClassName.propertyName
ClassName.funName()
```

此外，可以在没有类名的情况下在类中使用它们：

```kotlin
class ClassName {
    fun anotherFun(){
        println(propertyName)
    }
    companion object {
        const val propertyName: String = "Something..."
        fun funName(){
            //...
        }
    }
}
```

当我们使用其中一个伴生对象成员时，它将被初始化。除此之外，伴生对象在实例化其封闭类之后被初始化。

## 3. 命名伴生对象

**默认情况下，伴生对象的名称是Companion，但是，可以重命名它**。让我们以最简单的形式实现工厂方法设计模式，工厂方法设计模式处理对象创建，我们将使用伴生对象来实现此设计模式。让我们将其伴生对象命名为Factory：

```kotlin
class MyClass {
    companion object Factory {
        fun createInstance(): MyClass = MyClass()
    }
}
```

现在，**如果我们更喜欢通过专用名称使用伴生对象，只需将其放在类名之后**：

```kotlin
val instance = MyClass.Factory.createInstance()
```

这里有一点需要注意，那就是每个类只能有一个伴生对象。

## 4. 继承和伴生对象

伴生对象不可继承，但它可以继承自另一个类或实现接口，这是伴生对象类似于Java和C#中的静态声明的原因之一。

让我们看另一个简单的例子，该示例受益于伴生对象继承来实现抽象工厂设计模式。从概念上讲，这种设计模式与前一种模式类似，因为它处理对象创建。不同之处在于它是一个创建其他工厂以生产具有共同主题的相关对象系列的工厂，让我们通过定义接口来开始实现这个设计模式：

```kotlin
interface Theme {
    fun someFunction(): String
}

abstract class FactoryCreator {
    abstract fun produce(): Theme
}
```

之后，让我们定义代表工厂的类：

```kotlin
class FirstRelatedClass : Theme {
    companion object Factory : FactoryCreator() {
        override fun produce() = FirstRelatedClass()
    }

    override fun someFunction(): String {
        return "I am from the first factory."
    }
}

class SecondRelatedClass : Theme {
    companion object Factory : FactoryCreator() {
        override fun produce() = SecondRelatedClass()
    }

    override fun someFunction(): String {
        return "I am from the second factory."
    }
}

```

现在，我们可以使用设计模式：

```kotlin
fun main() {
    val factoryOne: FactoryCreator = FirstRelatedClass.Factory
    println(factoryOne.produce().someFunction())

    val factoryTwo: FactoryCreator = SecondRelatedClass.Factory
    println(factoryTwo.produce().someFunction())
}
```

我们的文章[Kotlin中的抽象工厂模式](https://www.baeldung.com/kotlin/abstract-factory-pattern)解释了该示例的来龙去脉。

## 5. Java互操作性

**正如我们所知，伴生对象可以继承类或接口**-这在Java静态成员中是不可行的。**所以，如果我们需要Java互操作代码，解决方案是@JvmStatic函数和@JvmStatic属性**。通过使用@JvmStatic注解标注伴生对象的成员，我们将获得更好的Java互操作性：

```kotlin
companion object {
    @JvmStatic
    val propertyName: String = "Something..."
}
```

[在Java中访问Kotlin伴生对象](https://www.baeldung.com/kotlin/companion-objects-in-java)更详细地解释了该主题。

## 6. 接口和伴生对象

**伴生对象也可以用在接口中**，我们可以在包含在接口中的伴生对象中定义属性和具体函数，一种潜在的用途是存储与接口相关的常量和辅助函数：

```kotlin
interface MyInterface {
    companion object {
        const val PROPERTY = "value"
    }
}
```

## 7. 总结

在本文中，我们介绍了伴生对象的详细信息，这是一个与所有类实例共享函数和属性的地方。