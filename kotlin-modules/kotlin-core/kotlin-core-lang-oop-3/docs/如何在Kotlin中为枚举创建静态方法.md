## 1. 概述

众所周知，Java在1.5版本中引入了[枚举](https://www.baeldung.com/a-guide-to-java-enums)，枚举使处理常量类型安全、不易出错且自我记录。因此，Kotlin也有[枚举](https://www.baeldung.com/kotlin/enum)类型。

在这个快速教程中，我们将探讨如何在Kotlin的枚举类中创建“静态”函数。

## 2. 问题简介

首先，关于“方法”或“函数”的术语，在本教程中，当我们谈论Kotlin时，我们将使用“函数”。“方法”将用于Java上下文。

在Java中，我们可以将静态方法添加到枚举中，以对枚举实例执行一些常见操作。然而，与Java不同的是，**Kotlin没有static关键字**。

因此，如果我们刚来自Java世界，我们可能会有几个问题：

-   如何在Kotlin的枚举中创建“静态”函数？
-   在Kotlin中，我们可以像在Java中那样调用“静态”函数吗？
-   此外，能否在Java中调用这些Kotlin的“静态”函数？以及如何调用？

下面，我们就通过实例来解开这些疑惑。

为简单起见，我们将使用单元测试断言来验证函数调用是否返回预期结果。

## 3. 在伴生对象中创建“静态”函数

首先，让我们看一个Kotlin枚举类的示例：

```kotlin
enum class MagicNumber(val value: Int) {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6),
}
```

如上面的代码所示，MagicNumber枚举类有一个构造函数和一些预定义的实例。

我们可以向[伴生对象](https://www.baeldung.com/kotlin/companion-object)添加函数，使它们作为“静态”函数工作。**这是因为可以使用类名访问伴随对象中的属性和函数，例如ClassName.functionName()**。

因此，接下来，让我们向MagicNumber枚举类添加两个“静态”函数：

```kotlin
enum class MagicNumber(val value: Int) {
    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6);

    companion object {

        fun pickOneRandomly(): MagicNumber {
            return values().random()
        }

        fun greaterThan(n: Int): List<MagicNumber> {
            return values().filter { it.value > n }
        }
    }
}
```

如我们所见，我们在伴生对象主体中创建了两个简单的函数。

值得一提的是，**如果我们在枚举实例块之后有更多的对象或函数，则在最后一个枚举实例之后需要一个分号“;”**。所以在我们的例子中，它是“SIX(6);“。

现在，让我们创建一个测试来查看函数是如何被调用的：

```kotlin
assertNotNull(MagicNumber.pickOneRandomly())
assertEquals(listOf(THREE, FOUR, FIVE, SIX), MagicNumber.greaterThan(2))
```

当我们运行它时，测试通过，所以这些函数按预期工作。

如上面的代码所示，由于函数是在伴生对象中定义的，因此我们可以像在Java中调用静态方法一样调用它们。

接下来，让我们看看如何从Java中调用它们。

## 4. 从Java调用Kotlin的“静态”函数

**Kotlin枚举的伴生对象中的函数可以在Java中通过ClassName.Companion.functionName()调用**，例如MagicNumber.Companion.pickOneRandomly()。

但是，**我们可以在伴生对象主体中的函数中添加**[@JvmStatic注解](https://www.baeldung.com/kotlin/jvmstatic-annotation)，**这样Kotlin将生成额外的静态方法来委托编译后的字节码中相应的Companion.functions**，然后，我们可以将它们作为我们熟悉的普通静态方法调用。

为了演示，我们将只向一个函数添加@JvmStatic注解：

```kotlin
enum class MagicNumber(val value: Int) {
    ONE(1), TWO(2), THREE(3) ...

    companion object {
        fun pickOneRandomly(): MagicNumber { ... }

        @JvmStatic
        fun greaterThan(n: Int): List<MagicNumber> { ... }
    }
}
```

接下来，让我们在Java中创建一个测试，看看如何调用这两个函数：

```java
// the greaterThan() function with @JvmStatic
assertEquals(Arrays.asList(THREE, FOUR, FIVE, SIX), MagicNumber.greaterThan(2));
                                                                                    
// calling it through the Companion object
assertEquals(Arrays.asList(THREE, FOUR, FIVE, SIX), MagicNumber.Companion.greaterThan(2));
                                                                                    
// the pickOneRandomly() function without @JvmStatic
assertNotNull(MagicNumber.Companion.pickOneRandomly());
```

如果我们运行，测试就会通过。

正如我们在上面的Java代码中看到的，由于@JvmStatic注解，我们可以将greaterThan()函数作为标准Java静态方法调用。更进一步，无论函数是否具有@JvmStatic，我们都可以通过Java中的Companion对象来调用它。

## 5. 总结

在本文中，我们讨论了如何在Kotlin的枚举类中创建“静态”方法。

此外，我们通过示例了解了@JvmStatic注解，并学习了如何从Java调用Kotlin的“静态”方法。