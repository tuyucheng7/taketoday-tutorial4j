## 1. 概述

在本教程中，我们将演示如何在接口中定义伴生对象。此外，我们将解释伴生对象如何影响接口实现。最后，我们将展示如何通过伴生对象实现接口。

## 2. 接口中的伴生对象

让我们看看如何在接口中定义[伴生对象](https://www.baeldung.com/kotlin/companion-object)，最重要的是，**类和接口中的伴生对象声明之间没有区别**，让我们用Vehicle接口来演示它：

```kotlin
interface Vehicle {
    fun getNumberOfWheels(): Int

    companion object {
        const val DOUBLE_TRACK_AMOUNT_OF_WHEELS = 3
        fun isDoubleTrack(vehicle: Vehicle) = vehicle.getNumberOfWheels() > DOUBLE_TRACK_AMOUNT_OF_WHEELS
    }
}
```

伴生对象提供了一个公共常量，它定义了双轨车辆中的最小车轮数量。此外，我们提供了一个辅助方法isDoubleTrack()，**通用常量和辅助方法是在接口中使用伴生对象的常见用例**。

## 3. 用伴生对象实现接口

现在让我们看一下如何使用伴生对象实现接口，也就是说，**伴生对象对接口实现没有任何影响，它不会更改接口的约定**。此外，伴生对象定义了一个关联对象到与单例实例的接口，与在类中发生的方式完全相同。

现在，让我们实现Vehicle接口：

```kotlin
class Car(val brand: String, val model: String, val age: Int) : Vehicle {

    companion object {
        const val ANTIQUE_CAR_MINIMAL_AGE = 30
    }
    override fun getNumberOfWheels() = 4
    fun isAntique() = age >= ANTIQUE_CAR_MINIMAL_AGE
}
```

Car类提供Vehicle接口的实现，此外，它可能会创建自己的伴生对象，但对在接口中创建的伴生对象没有任何影响。

## 4. 在伴生对象中使用接口

之后，让我们看看我们是否可以通过伴生对象来实现一个接口。为此，我们将在[命名的伴生对象](https://www.baeldung.com/kotlin/companion-object#named-companion-object)中使用Vehicle接口：

```kotlin
class VehicleImplementedInCompanionObject {

    companion object Bicycle : Vehicle {
        override fun getNumberOfWheels(): Int {
            return 2
        }
    }
}
```

如上所示，**类中的接口实现与伴生对象中的接口实现之间没有区别**。

现在，让我们检查一下使用Car和Bicycle对象是否有区别，为此，让我们检查getNumberOfWheels方法的调用。

首先，让我们检查一下Car对象：

```kotlin
@Test
fun `given type implementing Vehicle, when use should work`(){
    val car = Car("Ford", "Mustang", 12)
    assertThat(car.getNumberOfWheels()).isEqualTo(4)
    assertThat(Vehicle.isDoubleTrack(car)).isTrue
}
```

接下来，让我们对Bicycle做同样的事情：

```kotlin
@Test
fun `given companion object implementing Vehicle, when use should work`(){
    val bicycle = VehicleImplementedInCompanionObject.Bicycle
    assertThat(bicycle.getNumberOfWheels()).isEqualTo(2)
    assertThat(Vehicle.isDoubleTrack(bicycle)).isFalse
}
```

我们在第一个测试中创建了一个Car对象，在第二个测试中，我们访问了Bicycle对象；之后，我们检查了两者的车轮数量。简而言之，**访问两个对象中的getNumberOfWheels方法没有区别**。

此外，我们可以有很多Car类的实例，但是VehicleImplementedInCompanionObject.Bicycle总是引用同一个单例实例。

## 5. 总结

在这篇简短的文章中，我们演示了如何在接口中定义伴生对象。之后，我们看到了如何在类和伴生对象中实现它。最后，我们了解到访问在类和伴生对象中实现的接口方法没有区别。