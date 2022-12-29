## 1. 概述

在本文中，我们将描述Java和Kotlin中的默认访问修饰符；之后，我们将看一下模块的定义；最后，我们将演示如何在Kotlin中封装内部逻辑。

## 2. Java中的包私有

让我们首先看一下[default修饰符](https://www.baeldung.com/java-access-modifiers#default)在Java中的工作原理，**它允许我们仅从同一包中的类访问该元素**。此外，不需要明确指定该关键字，因为它是默认修饰符，Java语言允许在顶级和成员级别使用包私有修饰符。

**最重要的是，包私有修饰符不提供真正的封装**。例如，我们可以将一个类放在与我们的包私有类相同的包中，然后，从新类中，我们可以访问包私有类的内容。这可能是Kotlin中没有相同修饰符的原因之一。

而且，如果我们想在包之外公开一个类，它必须是public。但是，它对每个人都可用。

## 3. Kotlin中的默认修饰符

现在，让我们看一下Kotlin中的默认修饰符，**我们可以使用**[public](https://www.baeldung.com/kotlin/visibility-modifiers#1-public-visibility)、[private](https://www.baeldung.com/kotlin/visibility-modifiers#2-private-visibility)或[internal](https://www.baeldung.com/kotlin/visibility-modifiers#5-internal-visibility)**限制关键字**。public修饰符是默认修饰符，此外，它的工作原理与Java相同，它将类、字段或方法暴露给类之外的任何其他代码。

**在Kotlin中，没有完全替代Java中的包私有修饰符**。

## 4. Kotlin中的模块定义

现在，让我们考虑一下Kotlin中的模块是什么，最重要的是，**它定义了一组在单个jar中一起编译的文件**。

这是一个普遍的定义，示例包括：

-   一个IntelliJ模块
-   一个Maven项目
-   一个Gradle源集
-   使用Ant任务编译的一组文件

从internal访问修饰符的角度来看，模块很重要，让我们仔细看看internal修饰符。

## 5. 使用internal来封装内部逻辑

现在，让我们看看如何在Kotlin中隐藏内部逻辑，Java中的解决方案，包私有修饰符，并不完美；此外，Kotlin确实允许隐藏内部逻辑。

**internal可见性修饰符与Java中的包私有访问修饰符有相似之处，但有一些好处**。首先，它不会在模块外公开成员，这意味着我们不会在库之外公开内部逻辑。另一方面，我们可以在整个模块中使用一个内部类，这意味着它被封装在我们的库中。

让我们创建一个简单的示例：

```kotlin
internal class InternalClass {
    internal fun helloFromInternalFunction(): String {
        return "Hello"
    }
}
```

我们创建了InternalClass，它位于cn.tuyucheng.taketoday.protectedmodifier包中，之后，让我们从包外部调用内部方法：

```kotlin
fun whenCallInternalClass_thenItWorks(){
    val internalClass = InternalClass()
    assertThat(internalClass.helloFromInternalFunction()).isEqualTo("Hello")
}
```

测试包位于同一个模块中，此外，我们在cn.tuyucheng.taketoday.outside.protectedmodifier包中创建了测试，**尽管测试类位于不同的包中，但它可以访问内部方法**。

## 6. 总结

在这篇简短的文章中，我们描述了Java和Kotlin中的默认修饰符。然后，我们简要讨论了Kotlin中模块的定义，此外，我们还演示了如何在Kotlin语言中隐藏内部逻辑。