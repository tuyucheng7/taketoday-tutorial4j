## 1. 概述

在Java中，我们有[匿名类](https://www.baeldung.com/java-anonymous-classes)，它允许我们在使用时在单个表达式中声明和实例化匿名类。

在本教程中，我们将探索Kotlin中的匿名类和对象。

## 2. 匿名对象简介

匿名类的实例称为匿名对象，因为表达式(而不是名称)定义了它们。

通常，匿名对象非常轻量级并且只供一次性使用。在Kotlin中，匿名对象是由[对象表达式](https://kotlinlang.org/docs/object-declarations.html#object-expressions)创建的。

**我们应该注意，我们正在谈论的对象表达式不是创建类型的单个静态实例的**[对象声明](https://www.baeldung.com/kotlin/objects#objects-in-kotlin)。

在本教程中，我们将首先了解如何从超类型(例如抽象类)创建匿名对象。然后，我们将讨论一个有趣的用法：从头开始创建匿名对象。

此外，我们将比较Kotlin的匿名对象和它们在Java中的对应对象。

为简单起见，我们将使用单元测试的断言来验证我们的匿名对象是否按预期工作。

接下来，让我们看看它们的实际效果。

## 3. 从抽象类创建匿名对象

与在Java中一样，**我们也不能在Kotlin中直接实例化抽象类**。例如，假设我们有一个名为Doc的抽象类：

```kotlin
abstract class Doc(
    val title: String,
    val author: String,
    var words: Long = 0L
) {
    abstract fun summary(): String
}
```

正如我们在上面的类中看到的，Doc有一个构造函数，但是如果我们尝试直接实例化它：

```kotlin
val article = Doc(title = "A nice article", author = "Kai", words = 420) // won't compile!
```

编译器会抱怨：“cannot create an instance of an abstract class”。

但是，我们可以使用对象表达式来创建继承Doc的匿名类的对象：

```kotlin
val article = object : Doc(title = "A nice article", author = "Kai", words = 420) {
    override fun summary() = "Title: <$title> ($words words) By $author"
}
```

如上面的代码所示，从类型创建匿名对象的对象表达式语法是：

```kotlin
object : TheType(...constructor parameters...) { ... implementations ... }
```

在我们的示例中，我们实现了抽象函数summary。现在，让我们验证article对象是否符合我们的预期：

```kotlin
article.let {
    assertThat(it).isInstanceOf(Doc::class.java)
    assertThat(it.summary()).isEqualTo("Title: <A nice article> (420 words) By Kai")
}
```

当我们运行测试时，它通过了。因此，article对象按预期工作。

除了抽象类之外，我们还可以使用类似的语法从接口创建匿名对象。接下来，让我们看看它是如何工作的。

## 4. 从接口创建匿名对象

在Kotlin中，由于**接口不能有构造函数**，因此对象表达式的语法变为：

```kotlin
object : TheInterface { ... implementations ... }
```

接下来，让我们看一个例子。首先，假设我们有一个接口：

```kotlin
interface Printable {
    val content: String
    fun print(): String
}
```

接下来，让我们从中创建一个匿名对象：

```kotlin
val sentence = object : Printable {
    override val content: String = "A beautiful sentence."
    override fun print(): String = "[Print Result]n$content"
}
```

如上面的代码所示，我们的匿名类已经覆盖了content属性并实现了print函数。

测试可以验证sentence对象是否按预期工作：

```kotlin
sentence.let {
    assertThat(it).isInstanceOf(Printable::class.java)
    assertThat(it.print()).isEqualTo("[Print Result]nA beautiful sentence.")
}
```

接下来，让我们看一下匿名对象的另一个有趣用例。

## 5. 从头开始定义匿名对象

到目前为止，我们已经探讨了创建超类型的匿名对象。除此之外，在Kotlin中，**我们仍然可以从头开始定义一个匿名对象**，我们先来看一个例子：

```kotlin
val player = object {
    val name = "Kai"
    val gamePlayed = 6L
    val points = 42L
    fun pointsPerGame() = "$name: AVG points per Game: ${points / gamePlayed}"
}

player.let {
    assertThat(it.name).isEqualTo("Kai")
    assertThat(it.pointsPerGame()).isEqualTo("Kai: AVG points per Game: 7")
}
```

正如我们在上面的代码中看到的，**player变量包含一个没有显式超类型的匿名对象**。

这次创建这样一个对象的对象表达式非常简单：object { ... implementations ...}。

许多人可能认为这是Kotlin中的新事物，我们在Java中没有对应物，因为在Java中，我们的匿名类必须具有超类型。

实际上，由于Java中的每个类都是Object类的子类，因此对象表达式的工作方式与以下Java代码非常相似：

```java
Object player = new Object() {
    String name = "Kai";
    Long gamePlayed = 6L;
    String pointsPerGame() {
        return "$name: AVG points per Game: ${points / gamePlayed}"
    }
};
```

但是，如果我们用Object类型定义了player对象，我们就无法访问player的属性或方法。但是，如果我们的Java版本是10或更高版本，我们可以使用var关键字来定义player对象，然后我们可以用同样的方法访问它的成员：

```java
var player = new Object() {
    String name = "Kai";
    ...
    String pointsPerGame() { ...}
};

player.pointsPerGame();
String playerName = player.name;
```

在Kotlin中，我们可以使用匿名对象作为方法的返回值。此外，**如果匿名对象是通过私有方法返回的，我们仍然可以访问它的成员**：

```kotlin
class PlayerService() {
    private fun giveMeAPlayer() = object {
        val name = "Kai"
        val gamePlayed = 6L
        val points = 42L
        fun pointsPerGame() = "$name: AVG points per Game: ${points / gamePlayed}"
    }

    fun getTheName(): String {
        val thePlayer = giveMeAPlayer()
        print(thePlayer.pointsPerGam())
        return thePlayer.name
    }
}
```

另一方面，Java没有这个功能，这是因为如果一个Java方法返回一个类似的匿名对象，该方法必须将Object定义为返回类型。因此，我们无法访问匿名对象的成员。

## 6. 总结

在本文中，我们介绍了如何在Kotlin中使用对象表达式实例化抽象类或接口。

此外，我们比较了Kotlin和Java中的匿名对象，尤其是当我们在Kotlin中从头开始定义匿名对象时。