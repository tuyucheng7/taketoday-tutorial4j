## 1. 简介

在本教程中，我们将了解在[Kotlin](https://www.baeldung.com/kotlin-overview)中创建嵌套类和内部类的四种方法。

## 2. 与Java的快速比较

对于那些考虑[Java嵌套类](https://www.baeldung.com/kotlin-nested-classes)的人，让我们快速概括一下相关术语：

| Kotlin |  Java  |
|:------:|:------:|
|  内部类   | 非静态嵌套类 |
|  局部类   |  局部类  |
|  匿名对象  |  匿名类   |
|  嵌套类   | 静态嵌套类  |

虽然肯定不完全相同，但在考虑每种功能和用例时，我们可以使用此表作为指南。

## 3. 内部类

**首先，我们可以使用关键字inner在另一个类中声明一个类**。

这些类**可以访问封闭类的成员，甚至是私有成员**。

要使用它，我们需要先创建一个外部类的实例；没有它我们就不能使用内部类。

让我们在Computer类中创建一个HardDisk内部类：

```kotlin
class Computer(val model: String) {
    inner class HardDisk(val sizeInGb: Int) {
        fun getInfo() = "Installed on ${this@Computer} with $sizeInGb GB"
    }
}
```

请注意，我们使用[限定的this表达式](https://kotlinlang.org/docs/reference/this-expressions.html)来访问Computer类的成员，这类似于我们在HardDisk的Java等效项中执行Computer.this时的情况。

现在，让我们看看它的实际效果：

```kotlin
@Test
fun givenHardDisk_whenGetInfo_thenGetComputerModelAndDiskSizeInGb() {
    val hardDisk = Computer("Desktop").HardDisk(1000)
    assertThat(hardDisk.getInfo()).isEqualTo("Installed on Computer(model=Desktop) with 1000 GB")
}
```

## 4. 局部内部类

**接下来，我们可以在方法体内或作用域块中定义一个类**。

让我们举一个简单的例子来看看它是如何工作的。

首先，让我们为我们的Computer类定义一个powerOn方法：

```kotlin
fun powerOn(): String {
    // ...
}
```

在powerOn方法的内部，让我们声明一个Led类并使其闪烁：

```kotlin
fun powerOn(): String {
    class Led(val color: String) {
        fun blink(): String {
            return "blinking $color"
        }
    }

    val powerLed = Led("Green")
    return powerLed.blink()
}
```

请注意，Led类的作用域仅在方法内部。

**使用局部内部类，我们可以访问和修改在外部作用域中声明的变量**。让我们在powerOn方法中添加一个defaultColor：

```kotlin
fun powerOn(): String {
    var defaultColor = "Blue"
    // ...
}
```

现在，让我们在Led类中添加一个changeDefaultPowerOnColor：

```kotlin
class Led(val color: String) {
    // ...
    fun changeDefaultPowerOnColor() {
        defaultColor = "Violet"
    }
}

val powerLed = Led("Green")
log.debug("defaultColor is $defaultColor")
powerLed.changeDefaultPowerOnColor()
log.debug("defaultColor changed inside Led " + "class to $defaultColor")
```

这将输出：

```bash
[main] DEBUG c.b.n.Computer - defaultColor is Blue
[main] DEBUG c.b.n.Computer - defaultColor changed inside Led class to Violet
```

## 5. 匿名对象

**匿名**[对象](https://www.baeldung.com/kotlin-objects)**可用于定义接口或抽象类的实现，而无需创建可重用的实现**。

Kotlin中的匿名对象和Java中的匿名内部类之间的一个很大区别是**匿名对象可以实现多个接口和方法**。

首先，让我们在Computer类中添加一个Switcher接口：

```kotlin
interface Switcher {
    fun on(): String
}
```

现在，让我们在powerOn方法中添加此接口的实现：

```kotlin
fun powerOn(): String {
    //...
    val powerSwitch = object : Switcher {
        override fun on(): String {
            return powerLed.blink()
        }
    }
    return powerSwitch.on()
}
```

正如我们所见，为了定义我们的匿名powerSwitch对象，我们使用了一个[对象表达式](https://kotlinlang.org/docs/reference/object-declarations.html#object-expressions)。此外，我们需要考虑到每次调用对象表达式时都会创建一个新的对象实例。

使用像内部类这样的匿名对象，我们可以修改之前在作用域中声明的变量，这是因为Kotlin没有我们在Java中期望的有效最终限制。

现在，让我们在PowerSwitch对象中添加一个changeDefaultPowerOnColor并调用它：

```kotlin
val powerSwitch = object : Switcher {
    //...
    fun changeDefaultPowerOnColor() {
        defaultColor = "Yellow"
    }
}
powerSwitch.changeDefaultPowerOnColor()
log.debug("defaultColor changed inside powerSwitch " + "anonymous object to $defaultColor")
```

我们会看到这样的输出：

```bash
...
[main] DEBUG c.b.n.Computer - defaultColor changed inside powerSwitch anonymous object to Yellow
```

另外，请注意，如果我们的对象是具有单个抽象方法的接口或类的实例；我们可以使用[lambda表达式](https://www.baeldung.com/kotlin-lambda-expressions)创建它。

## 6. 嵌套类

最后，**我们可以在不使用关键字inner的情况下在另一个类中定义一个类**：

```kotlin
class Computer(val model: String) {
    class MotherBoard(val manufacturer: String)
}
```

**在这种类型的类中，我们无权访问外部类实例**。但是，我们可以访问封闭类的[伴生对象](https://www.baeldung.com/kotlin-objects)成员。

因此，让我们在Computer类中定义一个伴生对象来查看它：

```kotlin
companion object {
    const val originCountry = "China"
    fun getBuiltDate(): String {
        return "2018-07-15T01:44:25.38Z"
    }
}
```

然后是MotherBoard内部的一个方法来获取有关它和外部类的信息：

```kotlin
fun getInfo() = "Made by $manufacturer - $originCountry - ${getBuiltDate()}"
```

现在，我们可以测试它看看它是如何工作的：

```kotlin
@Test
fun givenMotherboard_whenGetInfo_thenGetInstalledAndBuiltDetails() {
    val motherBoard = Computer.MotherBoard("MotherBoard Inc.")
    assertThat(motherBoard.getInfo())
        .isEqualTo("Made by MotherBoard Inc. installed in China - 2018-05-23")
}
```

如我们所见，我们在没有Computer类实例的情况下创建了motherBoard。

## 7. 总结

在本文中，我们了解了如何在Kotlin中定义和使用嵌套类和内部类，以使我们的代码更加简洁和封装。此外，我们还看到了与相应Java概念的一些相似之处。