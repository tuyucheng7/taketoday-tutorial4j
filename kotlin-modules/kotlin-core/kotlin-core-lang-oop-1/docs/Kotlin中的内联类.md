## 1. 概述

在Kotlin 1.3+中，我们有一个实验性的新型类，称为[内联类](https://kotlinlang.org/docs/reference/inline-classes.html)。在本教程中，我们将重点介绍内联类的用法以及它们的一些限制。

## 2. 设置

正如我们之前提到的，内联类是Kotlin的一项实验性功能，因此，编译器将发出警告，指示该功能的实验状态。

为了避免此警告，我们可以将以下[Maven编译器选项](https://kotlinlang.org/docs/reference/using-maven.html#specifying-compiler-options)添加到我们的配置中：

```xml
<configuration>
    <args>
        <arg>-XXLanguage:+InlineClasses</arg> 
    </args>
</configuration>
```

## 3. 什么是内联类？

内联类为我们提供了一种包装类型的方法，从而增加功能并自行创建新类型。

与常规(非内联)包装器相反，它们将受益于改进的性能。发生这种情况是因为数据被内联到它的用法中，并且在生成的编译代码中跳过了对象实例化。

让我们看一个名为InlinedCircleRadius的内联类的示例，它具有表示半径的Double类型的属性：

```kotlin
val circleRadius = InlinedCircleRadius(5.5)
```

对于JVM，我们的代码实际上只是：

```kotlin
val circleRadius = 5.5
```

请注意我们的InlinedCircleRadius是如何在编译代码中未实例化的，因为底层值是内联的，从而使我们免于与实例化相关的性能损失。

### 3.1 使用示例

现在我们知道什么是内联类，我们将讨论它们的用法。

**在主构造函数中初始化单个属性是内联类的基本要求**，单个属性将代表运行时的类实例。

因此，为了有一个正确的定义，我们可以使用一行代码：

```kotlin
inline class InlineDoubleWrapper(val doubleValue: Double)
```

我们将InlineDoubleWrapper定义为Double对象上的简单包装器，并对其应用inline关键字。最后，我们现在可以在我们的代码中使用这个类而无需进行额外的更改：

```kotlin
@Test
fun whenInclineClassIsUsed_ThenPropertyIsReadCorrectly() {
    val piDoubleValue = InlineDoubleWrapper(3.14)
    assertEquals(3.14, piDoubleValue.doubleValue)
}
```

## 4. 类成员

到目前为止，我们使用的内联类就像简单的包装器一样。但它们远不止于此，**它们还允许我们像普通类一样定义属性和函数**，下一个示例定义了一个表示直径的属性和一个返回圆面积的函数：

```kotlin
inline class CircleRadius(private val circleRadius : Double) {
    val diameterOfCircle get() = 2 * circleRadius
    fun areaOfCircle = 3.14 * circleRadius * circleRadius
}
```

现在，我们将为我们的diameterOfCircle属性创建一个测试，它将实例化我们的CircleRadius内联类，然后调用该属性：

```kotlin
@Test
fun givenRadius_ThenDiameterIsCorrectlyCalculated() {
    val radius = CircleRadius(5.0)
    assertEquals(10.0, radius.diameterOfCircle)
}
```

下面是areaOfCircle函数的简单测试：

```kotlin
@Test
fun givenRadius_ThenAreaIsCorrectlyCalculated() {
    val radius = CircleRadius(5.0)
    assertEquals(78.5, radius.areaOfCircle())
}
```

但是，对于我们在内联类中可以定义和不能定义的内容有一些限制。**虽然属性和函数是允许的，但我们必须提到init块、内部类和**[支持字段](https://kotlinlang.org/docs/reference/properties.html#backing-fields)**是不允许的**。

## 5. 继承

值得一提的是，**内联类只能从接口继承**，并且由于我们不能有子类，因此**内联类实际上是最终的**。

给定一个带有方法draw()的接口Drawable，我们将在我们的CircleRadius类中实现这个方法：

```kotlin
interface Drawable {
    fun draw()
}

inline class CircleRadius(private val circleRadius : Double) : Drawable {
    val diameterOfCircle get() = 2 * circleRadius
    fun areaOfCircle() = 3.14 * circleRadius * circleRadius

    override fun draw() {
        println("Draw my circle")
    }
}
```

## 6. 总结

在这篇简短的文章中，我们探讨了Kotlin中的内联类。此外，我们还谈到了继承以及属性和函数的定义。