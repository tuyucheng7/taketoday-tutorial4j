## 1. 概述

在这个快速教程中，我们将学习如何在Kotlin中同时扩展类和实现接口。

## 2. 问题简介

在Java中，我们有扩展类和实现接口的[extends和implements关键字](https://www.baeldung.com/java-implements-vs-extends)。但是，在Kotlin方面，我们没有这些关键字。

因此，如何继承一个类并实现接口将是刚接触Kotlin世界的有经验的Java程序员经常被问到的问题之一。

像往常一样，我们将通过一个例子来了解类继承和接口实现是如何用Kotlin编写的。

假设我们有一个Person类和两个接口：

```kotlin
open class Person(private val firstName: String, private val lastName: String, val age: Int) {
    fun fullName(): String {
        return "$firstName $lastName"
    }
}

interface TuyuchengReader {
    fun readArticles(): String
}

interface TuyuchengAuthor {
    fun writeArticles(): String
}
```

如上面的代码所示，**Person类具有**[open](https://www.baeldung.com/kotlin/open-keyword)**关键字，这表明Person类是可以被继承的**。

这两个接口很容易理解，他们中的每个都定义了一个方法。

现在，我们的任务是创建一个Person的子类，并在Kotlin中实现上面的两个接口。

为简单起见，我们将使用单元测试断言来验证我们的类是否按预期工作。

接下来，让我们看看它的实际效果。

## 3. 扩展一个类实现两个接口

首先，和Java一样，一个Kotlin类只能继承一个超类，但是可以实现多个接口。

**Kotlin使用冒号字符“:”来表示继承和接口的实现**；因此，例如，class MyType(...) : SuperType(...)意味着MyType类继承了SuperType类。同样，MyType(...) : Interface1, Interface2表示MyType类实现了Interface1和Interface2。

如果我们想让MyType继承SuperType并实现接口，我们可以将它们放在用逗号分隔的冒号字符后：class MyType(...) : SuperType(...), Interface1, Interface2。

一个具体的例子可以很快地说明这一点。

接下来，让我们创建一个Developer类作为我们的Person类的子类，并使其实现TuyuchengReader和TuyuchengAuthor接口：

```kotlin
class Developer(firstName: String, lastName: String, age: Int, private val skills: Set<String>) :
    Person(firstName, lastName, age), TuyuchengAuthor, TuyuchengReader {
    override fun readArticles(): String {
        return "${fullName()} enjoys reading articles in these categories: $skills"
    }

    override fun writeArticles(): String {
        return "${fullName()} writes articles in these categories: $skills"
    }
}
```

如Developer类所示，我们将超类和两个接口放在冒号字符之后，**值得一提的是，冒号字符后面的超类和接口的顺序无关紧要**。

当然，由于接口定义了readArticles()和writeArticles()函数，因此Developer类必须实现它们。与Java不同，**当我们在Kotlin中重写函数时，我们使用override关键字而不是**[@Override注解](https://www.baeldung.com/java-override)。

接下来，让我们创建一个测试来检查我们的Developer类是否按预期工作：

```kotlin
val developer: Developer = Developer("James", "Bond", 42, setOf("Kotlin", "Java", "Linux"))
developer.apply {
    assertThat(this).isInstanceOf(Person::class.java).isInstanceOf(TuyuchengReader::class.java).isInstanceOf(TuyuchengAuthor::class.java)
    assertThat(fullName()).isEqualTo("James Bond")
    assertThat(readArticles()).isEqualTo("James Bond enjoys reading articles in these categories: [Kotlin, Java, Linux]")
    assertThat(writeArticles()).isEqualTo("James Bond writes articles in these categories: [Kotlin, Java, Linux]")
}

```

在测试中，我们首先通过调用Developer的构造函数创建了一个developer实例，然后我们验证了developer应该是Person、TuyuchengReader和TuyuchengAuthor的一个实例。

最后，我们检查了被重写的函数是否返回预期的结果。

如果我们运行，测试就会通过。

## 4. 总结

在本文中，我们学习了如何在Kotlin中同时扩展类和实现接口。