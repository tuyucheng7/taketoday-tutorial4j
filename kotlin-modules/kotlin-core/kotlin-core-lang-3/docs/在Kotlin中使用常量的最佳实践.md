## 1. 概述

在本文中，我们将介绍Kotlin中常量的最佳实践，首先，我们将列出允许的常量类型；之后，我们将在顶层和伴生对象中定义常量；最后，我们将演示如何从Java访问Kotlin的静态对象。

## 2. 允许的常量类型

**Kotlin语言只允许定义基本类型(数字类型、字符和布尔值)和String的**[常量](https://www.baeldung.com/kotlin/const-var-and-val-keywords#const)。

让我们尝试定义一个具有已定义类型的常量，首先，让我们创建一个没有任何逻辑的空类：

```kotlin
class SimpleClass {
}
```

现在，让我们尝试在常量声明中使用它：

```kotlin
const val constantAtTopLevel : = SimpleClass()
```

之后，编译器给我们一个错误：

```bash
Const 'val' has type 'SimpleClass'. Only primitives and String are allowed
```

## 3. 顶级声明的常量

我们首先应该注意Kotlin不会将文件与类匹配，在一个文件中，我们可以定义多个类。**与Java不同，Kotlin不需要每个文件有一个顶级类，每个文件可以有多个顶级类**。

**文件还可以具有类之外的函数和变量**，这些函数和变量可以直接访问。

让我们在ConstantsBestPractices.kt文件中创建一个常量：

```kotlin
const val CONSTANT_AT_TOP_LEVEL = "constant value defined at the top-level"

```

现在，让我们从另一个类访问它：

```kotlin
class ConstantAtTopLevelTest {
    @Test
    fun whenAccessingConstantAtTopLevel_thenItWorks() {
        Assertions.assertThat(CONSTANT_AT_TOP_LEVEL).isEqualTo("constant value defined at the top-level")
    }
}
```

首先，我们在类之外定义了常量CONSTANT_AT_TOP_LEVEL，之后，我们测试它是否可以从ConstantAtTopLevelTest类访问。

它在示例中的类中工作，事实上，该常量可以从任何类访问。

如果我们将一个常量声明为私有的，它就只能被同一个文件中的其他类访问。

**当我们想要在文件内或整个应用程序中的类之间共享值时，顶级常量是一个完美的解决方案**。此外，当值与特定类无关时，使用顶级常量是一个很好的选择。

## 4. 顶层声明的局限性

尽管它很简单，但我们应该注意在声明顶级常量时面临的一些限制。

首先，如上所述，在文件顶层定义的常量可以被同一文件中的任何类访问，即使它是私有的，我们不能限制该文件中特定类的可见性。因此，**此类常量不与任何类相关联**。

此外，**对于顶级常量，编译器会生成一个新类，这是必要的，因为最终这些常量必须位于某个地方**。为此，编译器创建了一个类，其原始文件的名称后缀为Kt。在我们的例子中，它是ConstantAtTopLevelTestKt，其中原始文件名是ConstantAtTopLevelTest.kt。

## 5. 在伴生对象中定义常量

现在，让我们在[伴随对象](https://www.baeldung.com/kotlin/companion-object)中定义一个常量：

```kotlin
class ConstantsBestPractices {
    companion object {
        const val CONSTANT_IN_COMPANION_OBJECT = "constant at in companion object"
    }
}
```

之后，让我们从ConstantInCompanionObjectTest类访问它：

```kotlin
class ConstantInCompanionObjectTest {

    @Test
    fun whenAccessingConstantInCompanionObject_thenItWorks() {
        Assertions.assertThat(CONSTANT_IN_COMPANION_OBJECT).isEqualTo("constant in companion object")
    }
}
```

ConstantInCompanionObjectTest类包含常量的导入：

```kotlin
import cn.tuyucheng.taketoday.constants.ConstantsBestPractices.Companion.CONSTANT_IN_COMPANION_OBJECT
```

这是必需的，因为该字段属于一个类，**当我们想要将值与类相关联时，在伴生对象中定义常量是一个完美的解决方案，我们通过类上下文访问它**。

## 6. 可从Java代码访问的静态值

最后，让我们看一下Java代码中的静态对象可访问性，让我们创建一个包含两个常量的简单示例：

```kotlin
const val CONSTANT_AT_TOP_LEVEL = "constant at top level"

class ConstantsBestPractices {
    companion object {
        const val CONSTANT_IN_COMPANION_OBJECT = "constant at in companion object"
    }
}
```

顶层有一个常量，伴生对象中有一个。

现在让我们创建一个AccessKotlinConstant Java类并使用创建的常量：

```java
public class AccessKotlinConstant {
    private String staticObjectFromTopLevel = ConstantsBestPracticesKt.CONSTANT_AT_TOP_LEVEL;
    private String staticObjectFromCompanion = ConstantsBestPractices.CONSTANT_IN_COMPANION_OBJECT;
}
```

**首先，可以从Java访问顶级声明中的常量，生成的类名后缀为Kt**。另一方面，可以通过类名直接访问伴生对象常量。

## 7. 总结

在这篇简短的文章中，我们描述了Kotlin中在顶层和伴生对象中的常量定义，此外，我们还演示了如何从Java类访问这两种类型的常量。