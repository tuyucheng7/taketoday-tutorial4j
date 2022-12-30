## 1. 概述

在这个快速教程中，我们将学习如何在Kotlin中查找特定密封类的所有子类。

## 2. 获取密封类的子类

我们知道，[密封的类和接口](https://www.baeldung.com/kotlin/sealed-classes)是继承层次结构，有一个简单的限制：**只有在同一个包中的类和接口才能扩展它们**。因此，特定密封类的所有子类在编译时都是已知的。

让我们以一个简单的类层次结构为例：

```kotlin
sealed class Expr(val keyword: String)
class ForExpr : Expr("for")
class IfExpr : Expr("if")
class WhenExpr : Expr("when")
class DeclarationExpr : Expr("val")
```

在这里，我们有一个带有四个具体扩展的密封类。

从Kotlin 1.3开始，为了找到密封类的所有子类，**我们可以使用**[sealedSubclasses](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/sealed-subclasses.html)**属性**：

```kotlin
val subclasses: List<KClass<*>> = Expr::class.sealedSubclasses

assertThat(subclasses).hasSize(4)
assertThat(subclasses).containsExactlyInAnyOrder(
    ForExpr::class, IfExpr::class, WhenExpr::class, DeclarationExpr::class
)
```

如上所示，我们必须找到超类的KClass<T\>，然后在其上使用sealedSubclasses属性。在这里，我们还验证了Expr超类有四个子类。

## 3. 总结

在本教程中，我们学习了如何在Kotlin中查找特定密封类的子类。