## 1. 概述

在本教程中，我们将学习如何从Kotlin函数返回多个值。在前两节中，我们将简要讨论Kotlin中的元组和解构声明，然后我们将使用这些声明从一个函数返回多个值。

## 2. Kotlin中没有元组

某些编程语言(例如C#和Scala)对元组提供一流的支持，因此，我们可以轻松地将元组分配给变量，将它们传递给函数，甚至从函数返回它们。

另一方面，Kotlin不支持创建任意元组，因此我们不能返回多个值，例如：

```kotlin
// won't work
fun returnMultiple(): (Int, Int) {
    return 1, 2
}
```

尽管我们不能像上面的示例那样返回多个值，但**我们可以在调用点上模仿有限的类似元组的语法**。更具体地说，我们可以这样写：

```kotlin
val (id, name) = returnIdAndName()
```

**这种类似元组的语法仅适用于赋值**，所以，我们不能在将参数传递给函数时使用这个技巧。

现在我们对可能的解决方案有了一个大致的了解，让我们看看该解决方案是如何工作的。

## 3. 解构声明

**在Kotlin中，可以将对象解构为一组变量**。例如，给定以下[数据类](https://www.baeldung.com/kotlin/data-classes)：

```kotlin
data class User(val id: Int, val username: String)
```

我们可以在赋值中将一个User类型的变量解构为它的两个属性：

```kotlin
val (id, name) = User(1, "Ali")
```

如上所示，我们不是将对象实例分配给一个变量，而是将其部分分配给不同的变量，**这就是**[解构声明](https://www.baeldung.com/kotlin/destructuring-declarations)**的本质：它们一次创建多个变量**。

我们只能将这些类型的声明用于使用componentN命名约定定义[运算符](https://www.baeldung.com/kotlin/operator-overloading)函数的类型，也就是说，component1函数是第一个解构变量，component2函数是第二个变量，依此类推。

在Kotlin中，一些类型已经可用于解构声明：

-   数据类，因为它们为所有属性声明组件函数
-   一些内置类型，例如[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/)和[Triple](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-triple/)
-   集合类型

**所以，基本上，我们可以使用上述所有类型从一个函数返回多个值**；除此之外，我们还可以使用componentN函数定义自定义类型，并将它们用作返回类型。

## 4. 返回多个变量

### 4.1 Pair

现在我们知道我们可以使用解构模式来返回多个值，让我们看几个例子。对于初学者，**如果我们要返回任何类型的两个值，我们可以从函数返回一个Pair<T, R\>并在调用点上对其进行解构**：

```kotlin
fun twoPair(): Pair<String, Int> = "Ali" to 33 // equivalent to Pair("Ali", 33)

fun main() {
    val (name, age) = twoPair()
    println("$name is $age years old")
}
```

如上所示，尽管我们没有像函数体中的元组那样返回多个变量，但我们在调用点中将它们视为元组，这就是解构声明的魔力。

当返回任何类型的两个值时，Pair内置类型似乎是最佳选择。

### 4.2 Triple

非常相似，**我们可以返回任何类型的三个值，类型为Triple<T, R, Q\>**：

```kotlin
fun threeValues(): Triple<String, Int, String> = Triple("Ali", 33, "Neka")

fun main() {
    val (name, age, bornOn) = threeValues()
}
```

Triple也是返回任何类型的三个值的最佳选择。 

### 4.3 数组和集合

此外，我们可以使用数组和集合返回最多五个相同类型的值：

```kotlin
fun fiveValues() = arrayOf("Berlin", "Munich", "Amsterdam", "Madrid", "Vienna")

fun main() {
    val (v1, v2, v3, v4, v5) = fiveValues()
}
```

Kotlin限制为五个，因为它在数组和集合上定义了五个componentN扩展函数。

### 4.4 数据类

有时，**最好定义一个具有有意义名称的自定义类型**，这样，除了使用解构语法糖之外，我们还可以在函数声明中传达特定的含义：

```kotlin
data class Pod(val name: String, val ip: InetAddress, val assignedNode: String)
fun getUniquePod() = Pod("postgres", InetAddress.getLocalHost(), "Node 1")

fun main() {
    val (podName, ip, assignedNode) = getUniquePod()
}
```

**在这里，我们利用数据类可用于解构声明以返回多个值这一事实**，在这个特定的示例中，数据类的名称清楚地表明我们正在处理Kubernetes Pod-与以前的方法相比，这更具可读性。

### 4.5 自定义componentN函数

最后，有时我们甚至可以将componentN函数声明为扩展函数以利用解构声明：

```kotlin
operator fun KeyPair.component1(): ByteArray = public.encoded
operator fun KeyPair.component2(): ByteArray = private.encoded

fun getRsaKeyPair(): KeyPair = KeyPairGenerator.getInstance("RSA").genKeyPair()

fun main() {
    val (publicKey, privateKey) = getRsaKeyPair()
}
```

在上面的示例中，我们在[java.security.KeyPair](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/security/KeyPair.html)类型上定义了两个componentN函数，以更惯用的方式提取公钥和私钥！这在使用已经定义的类型和函数时特别有用。 

## 5. 总结

在这篇文章中，我们谈到了Kotlin不支持元组这一事实，因此我们不能从使用元组的函数中返回多个值。

尽管有这个坏消息，我们仍然可以使用解构声明来模仿从函数返回多个值的元组行为。更具体地说，我们可以使用Pair和Triple等内置类型、集合类型和数组，最后是数据类，以返回多个值。根据情况和值的数量及其类型，我们可以使用不同的方法。