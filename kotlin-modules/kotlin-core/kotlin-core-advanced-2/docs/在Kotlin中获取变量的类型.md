## 1. 概述

有时，我们想确定一个变量的具体类型。例如，当我们从另一个系统接收到一些原始类型的值时，我们需要类型信息。

通常，有两种常见的情况来确定变量的类型，一种情况是检查对象以查看它是否是我们预期的类型并执行一些操作，另一种情况是获取变量类型的名称作为字符串。

在本教程中，我们将探讨如何在Kotlin中确定变量的类型。当然，我们将涵盖上述两种情况。

## 2. 检查变量的类型

我们将通过示例说明如何检查变量的类型。

### 2.1 一个例子：玩家类

首先，让我们创建一些类：

```kotlin
package cn.tuyucheng.taketoday.typeOfVariable

open class Person(val name: String, val age: Int)

interface Ranking

class Player(name: String, age: Int, val numberOfWins: Int) : Person(name, age), Ranking
```

如上面的代码所示，在cn.tuyucheng.taketoday.typeOfVariable包中，我们定义了一个Person类、一个Ranking[接口](https://www.baeldung.com/kotlin/interfaces)和一个Player类。而且，**Player类继承了Person类，实现了Ranking接口**。

接下来，让我们创建一些变量：

```kotlin
private val myInt: Any = 42
private val myString: Any = "I am a string"
private val myPlayer: Any = Player("Jackson", 42, 100)
```

如我们所见，我们创建了三个变量：一个Int、一个String和一个Player。我们应该注意到，我们已经使用相同的类型声明了三个变量：Any。值得一提的是，**Kotlin的Any与Java的Object非常相似**，这意味着它是所有其他类的超类型，换句话说，我们已经在原始类型中声明了三个变量。

为简单起见，我们将使用单元测试断言来验证我们是否获得了预期的类型。因此，让我们创建一个简单的[枚举](https://www.baeldung.com/kotlin/enum)类型来简化验证：

```kotlin
enum class VariableType {
    INT, STRING, PLAYER, UNKNOWN
}
```

接下来，让我们看看如何检查变量的类型。

### 2.2 is运算符

**我们可以使用is运算符来检查变量是否属于预期类型：var is ExpectedType**。

那么接下来，让我们在Any类的[扩展函数](https://www.baeldung.com/kotlin/extension-methods)中使用is运算符来检查变量的类型并返回相应的VariableType枚举实例：

```kotlin
fun Any.getType(): VariableType {
    return when (this) {
        is Int -> INT
        is String -> STRING
        is Player -> PLAYER
        else -> UNKNOWN
    }
}
```

现在，让我们编写一个测试来验证它是否可以为我们提供预期的类型：

```kotlin
assertTrue { INT == myInt.getType() }
assertTrue { STRING == myString.getType() }
assertTrue { PLAYER == myPlayer.getType() }
```

如果我们运行测试，它就会通过。

值得一提的是，由于我们的Player类是Person和Ranking的子类型，**如果我们在Player实例和两个超类型上应用is运算符，我们也会得到true**：

```kotlin
assertTrue { myPlayer is Person }
assertTrue { myPlayer is Ranking }
```

因此，如果我们想检查一个对象是否是预期类型，则is运算符是可行的方法。但是，有时，我们没有要检查的预期类型，相反，我们需要获取对象类型的名称。

接下来，让我们看看如何实现它。

## 3. 获取变量的类型名称

我们可以从对象的[KClass](https://www.baeldung.com/kotlin/reflection#1-kotlin-class-references)对象或其Class对象中获取类型名称，接下来，让我们看看它们的实际效果。

### 3.1 Kotlin类型名称

**KClass定义了simpleName和qualifiedName属性来携带对象的类型名称**。

从Kotlin对象获取KClass实例的一种直接方法是使用类引用myObject::class。

那么接下来，让我们验证类引用是否可以报告预期的类型名称：

```kotlin
assertTrue { "String" == myString::class.simpleName }
assertTrue { "Int" == myInt::class.simpleName }
assertTrue { "Player" == myPlayer::class.simpleName }
```

如果我们试一试，测试就会通过。如果我们需要类型的限定名称而不是简单名称，我们可以读取KClass.qualifiedName字段：

```kotlin
assertTrue { "kotlin.String" == myString::class.qualifiedName }
assertTrue { "kotlin.Int" == myInt::class.qualifiedName }
assertTrue { "cn.tuyucheng.taketoday.typeOfVariable.Player" == myPlayer::class.qualifiedName }
```

### 3.2 Java类型名称

或者，我们可以从相应的[Java类对象](https://www.baeldung.com/java-class-name)中获取类型名称。

所以，关键是如何在Kotlin中获取Class对象，**我们可以通过Kotlin中的myObject::class.java获取一个对象的Class对象**。 

接下来，让我们创建一个测试来检查我们是否可以通过Class对象获取预期的类型名称：

```kotlin
assertTrue { "java.lang.String" == myString::class.java.typeName }
assertTrue { "java.lang.Integer" == myInt::class.java.typeName }
assertTrue { "cn.tuyucheng.taketoday.typeOfVariable.Player" == myPlayer::class.java.typeName }
```

如果我们执行测试，它就会通过。

## 4. 总结

在本文中，我们学习了如何在Kotlin中获取变量的类型名称。