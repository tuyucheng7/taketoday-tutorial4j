## 1. 概述

Decorator Pattern 是一种设计模式，它允许向现有对象添加新功能而不改变其结构并且不影响同一类中其他对象的行为。

在本教程中，我们将介绍一些在 Kotlin 中实现此模式的有效方法。

## 2. 装饰者模式

Decorator 模式允许我们通过为原始对象提供增强的接口来静态或动态地添加行为。静态方法可以使用继承来实现，覆盖主类的所有方法并添加我们想要的额外功能。

作为继承的替代方法并减少子类化的开销，我们可以使用组合和委托来动态添加额外的行为。在本文中，我们将遵循这些技术来实现此模式。

让我们考虑下一个我们要装饰的圣诞树对象的例子。装饰不会改变对象本身；只是除了圣诞树之外，我们还添加了一些装饰物品，例如花环或泡泡灯或任何其他类型：

[![装饰师 1](https://www.baeldung.com/wp-content/uploads/sites/5/2021/08/Decorator-1.svg)](https://www.baeldung.com/wp-content/uploads/sites/5/2021/08/Decorator-1.svg)

现在让我们看看使用这个例子的模式的实现。

## 3.实施

首先，我们需要创建ChristmasTree通用接口：

```kotlin
interface ChristmasTree {
    fun decorate(): String
}
```

现在，让我们定义这个接口的实现：

```kotlin
class PineChristmasTree : ChristmasTree {

    override fun decorate() = "Christmas tree"
}
```

接下来，我们将看到装饰 ChristmasTree对象的两种策略。

### 3.1. 按成分装饰

当使用组合来实现装饰器模式时，我们需要一个抽象类作为目标对象的组合器或装饰器：

```kotlin
abstract class TreeDecorator
    (private val tree: ChristmasTree) : ChristmasTree {

    override fun decorate(): String {
        return tree.decorate()
    }
}
```

我们现在将创建装饰元素。这个装饰器将扩展我们的抽象TreeDecorator类，并根据我们的要求修改它的decorate()方法：

```kotlin
class BubbleLights(tree: ChristmasTree) : TreeDecorator(tree) {

    override fun decorate(): String {
        return super.decorate() + decorateWithBubbleLights()
    }

    private fun decorateWithBubbleLights(): String {
        return " with Bubble Lights"
    }
}
```

现在，我们可以创建装饰过的ChristmasTree对象：

```kotlin
fun christmasTreeWithBubbleLights() {

    val christmasTree = BubbleLights(PineChristmasTree())
    val decoratedChristmasTree = christmasTree.decorate()
    println(decoratedChristmasTree)
}
```

### 3.2. 委派装饰

委托模式已被证明是实现继承的一个很好的替代方案，并且 Kotlin 原生支持它，需要零样板代码。此功能使使用by关键字的类委托创建装饰器变得容易。

现在，我们将通过将decorator()方法委托给指定对象来定义可以实现ChristmasTree接口的类：

```kotlin
class Garlands(private val tree: ChristmasTree) : ChristmasTree by tree {

    override fun decorate(): String {
        return tree.decorate() + decorateWithGarlands()
    }

    private fun decorateWithGarlands(): String {
        return " with Garlands"
    }
}
```

现在，我们可以创建装饰过的ChristmasTree对象：

```kotlin
fun christmasTreeWithGarlands() {

    val christmasTree = Garlands(PineChristmasTree())
    val decoratedChristmasTree = christmasTree.decorate()
    println(decoratedChristmasTree)
}
```

## 4。总结

在本文中，我们探索了一些在 Kotlin 中实现装饰器模式的有效方法。当我们想要添加行为，或者当我们想要增强甚至删除特定对象的功能时，这种模式很有用。我们还看到 Kotlin 提供了一个原生支持功能来通过类委托来实现这种模式。