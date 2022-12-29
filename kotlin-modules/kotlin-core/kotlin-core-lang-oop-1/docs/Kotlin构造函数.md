## 1. 概述

在本教程中，我们将深入了解Kotlin中的构造函数。

让我们从快速回顾一下这个概念开始：我们使用构造函数来创建对象，它们看起来像方法声明，但始终具有与类相同的名称，并且它们不返回任何内容。

**在Kotlin中，一个类可以有一个主构造函数和一个或多个附加的辅助构造函数**。

在接下来的部分中，我们将介绍每种类型和相关概念。

## 2. 主构造函数

在Kotlin中创建对象的第一种方法是使用主构造函数。

**这是类头的一部分**，参数也可能是类字段，我们将其放在类声明之后。

让我们看一个基本的类声明，它有两个属性和一个主构造函数：

```kotlin
class Person constructor(val name: String, val age: Int? = null)
```

在此示例中，我们通过val关键字声明了属性，这与常规变量的行为方式相同，但要提到val属性是只读的(Java final关键字对应物)。

如果我们想稍后更改引用，我们应该改用var关键字。但是，我们不能在主构造函数中省略字段类型，因为这必须是显式的。

**在某些情况下，我们可以省略constructor关键字**，这仅在两种情况下是强制性的：当我们使用注解时(如@Autowired)或访问修饰符(如private或protected)。

此外，我们可以在构造函数中使用Kotlin默认参数。

接下来，让我们看看如何使用我们的主构造函数：

```kotlin
val person = Person("John")
val personWithAge = Person("Mark", 22)
```

**我们可以看到类名是一个构造函数调用，无需使用new关键字**。

作为参考，让我们也看一下构造函数声明的Java等价物：

```java
class PersonJava {
    final String name;
    final Integer age;

    public PersonJava(String name) {
        this.name = name;
        this.age = null;
    }

    public PersonJava(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
```

如你所见，Java需要更多的代码才能获得相同的结果。

### 2.1 JVM上的主构造函数

请注意，编译器会在JVM上生成一个额外的不带参数的构造函数。为此，它将实例化一个具有默认值的对象。

这样，Kotlin可以很好地与Jackson或JPA等库一起使用，这些库使用无参数构造函数来创建类实例：

```kotlin
class Person(val name: String = "")
```

## 3. 从构造函数参数初始化属性

**来自类主体的属性初始值设定项可以使用主构造函数参数**。

让我们将name转换为upperCaseName属性：

```kotlin
class Person(val name: String, val age: Int? = null) {
    val upperCaseName: String = name.toUpperCase()
}
```

我们可以通过添加第二个init块在控制台中看到输出：

```kotlin
init {
    println("Upper case name is $upperCaseName")
}
```

## 4. 初始化块

**我们不能在主构造函数中放置任何代码**。

然而，我们有时不得不执行一些初始化代码，放置这些代码的一个好地方是一个初始化块，它以init关键字为前缀。

**初始化程序块在主构造函数之后调用**，我们也可以在这个地方访问类字段。

一个类可以有一个或多个初始化块。

让我们将初始化程序块附加到我们的Person类：

```kotlin
init {
    println("Hello, I'm $name")
    if (surname.isEmpty()) {
        throw IllegalArgumentException("Surname cannot be empty!")
    }
}
```

接下来，当我们创建一个Person类对象时，我们将在控制台中看到：

```bash
Hello, I'm John
```

我们会为空surname抛出IllegalArgumentException。

当我们有很多init块时，它们将按照它们在类主体中出现的顺序执行。

## 5. 辅助构造函数

在Kotlin类中，我们还可以声明一个或多个辅助构造函数，**辅助构造函数以constructor关键字为前缀**：

```kotlin
class Car {
    val id: String
    val type: String

    constructor(id: String, type: String) {
        this.id = id
        this.type = type
    }
}
```

以及基本用法：

```kotlin
fun main(args: Array<String>) {
    val car = Car("1", "sport")
    val suvCar = Car("2", "suvCar")
}
```

**每个辅助构造函数都必须委托给主构造函数**，我们将通过this关键字来完成此操作。

让我们将我们的属性移动到主构造函数并修改辅助构造函数：

```kotlin
class Car(val id: String, val type: String) {
    constructor(id: String) : this(id, "unknown")
}
```

## 6. 构造函数和继承

我们可以使用超类的主构造函数。

请注意，Kotlin中的所有类在默认情况下都是最终的，这意味着我们需要添加open关键字，以便我们可以继承我们的Person类。

让我们添加一个继承自Person类的Employee类，他们都使用主构造函数：

```kotlin
class Employee(name: String, val salary: Int) : Person(name)
```

通过这样做，我们将一个name传递给Person类的主构造函数。此外，我们还在Employee类中添加了一个名为salary的新字段。

## 7. 总结

在这篇简短的文章中，我们讨论了使用Kotlin语言创建构造函数的各种方法，我们可以根据自己的意愿以多种不同的方式实例化我们的字段。