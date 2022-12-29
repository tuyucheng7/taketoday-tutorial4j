## 1. 概述

在本文中，我们将了解为什么我们不能在Kotlin中为基本类型使用延迟初始化的属性和变量。

为了更好地理解此限制背后的基本原理，首先，我们退后一步，看看Kotlin如何处理原始类型的[可空性](https://www.baeldung.com/kotlin/null-safety)。然后，我们将熟悉[lateinit](https://www.baeldung.com/kotlin/lazy-initialization#kotlins-lateinit)属性的动机。最后，理解为什么将两者混合没有意义会容易得多。

## 2. Kotlin和原始类型

为了更好地理解Kotlin如何编译原始类型，让我们考虑一个例子：

```kotlin
class LateInit {
    private val nonNullable: Int = 12
    private val nullable: Int? = null

    // omitted
}
```

在上面的示例中，我们有一个Int类型的可空变量和一个不可空变量。现在，让我们使用kotlinc编译这个文件，并使用javap工具查看[生成的字节码](https://www.baeldung.com/java-class-view-bytecode)：

```bash
>> kotlinc LateInit.kt
>> javap -c -p cn.tuyucheng.taketoday.lateinit.LateInit
public final class cn.tuyucheng.taketoday.lateinit.LateInit {
  private final int nonNullable;
  private final java.lang.Integer nullable;
  // omitted
}
```

自然地，**Kotlin将不可为null的原始类型编译为它们在Java中对应的原始类型**，在这个例子中是int。此外，它将**可为null的原始类型编译为它们在Java中相应的装箱类型**，在本例中为Integer。Kotlin中的其他原始类型也是如此。

## 3. lateinit复习

在Kotlin中，**我们应该始终使用属性初始化器在构造函数中初始化非空属性**，不幸的是，有时这样做可能是不可能的或不方便的。例如，当我们在测试框架中使用特定的依赖注入方法或设置方法时可能会出现这种情况：

```kotlin
@Autowired
private val userService: UserService // won't work
```

上面的示例甚至无法编译，因为我们没有初始化不可为null的属性。解决此问题的一种方法是使用可为空的类型：

```kotlin
@Autowired
private val userService: UserService? = null

// on the call site
userService?.createUser(user)
```

这肯定有效。但是，**每次我们要使用变量时，我们都应该处理可空类型(空安全运算符等)的尴尬**。

为了克服这个限制，Kotlin提供了lateinit变量，这些变量最初可以保持未初始化状态：

```kotlin
@Autowired
private lateinit var userService: UserService
```

基本上，**我们在这里告诉编译器我们知道我们没有立即初始化这个变量，但我们保证会尽快这样做**。如果我们未能初始化该属性，一旦我们尝试读取其值，运行时就会抛出异常。

这样，我们就可以像这个变量不可为空一样，这太棒了！有趣的是，在幕后，Kotlin像普通可空类型一样编译lateinit变量。例如，让我们考虑另一个例子：

```kotlin
private lateinit var lateinit: String
```

上面示例的字节码如下：

```bash
private java.lang.String lateinit;
```

与普通的变量不同，每次我们访问lateinit变量时，Kotlin都会检查我们是否初始化了它们：

```bash
6: ifnonnull     16
9: ldc           #22     // String lateinit
11: invokestatic  #28    // Method Intrinsics.throwUninitializedPropertyAccessException:(LString;)V
14: aconst_null
15: athrow
```

如上所示，**如果lateinit变量的值为null(索引6)，则Kotlin将在运行时抛出异常(索引11、14和15)**。

更具体地说，为了检查变量是否已初始化，Kotlin调用[throwUninitializedPropertyAccessException](https://github.com/JetBrains/kotlin/blob/68f14fdd87c0c50754d493547adbd933dc49ab63/libraries/stdlib/jvm/runtime/kotlin/jvm/internal/Intrinsics.java#L57)方法并抛出[UninitializedPropertyAccessException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-uninitialized-property-access-exception/)异常的实例 。

基本上，**一个额外的方法调用是我们为使用这种语法糖而付出的代价**。

## 4. 原始类型和lateinit

最重要的是，**Kotlin使用null作为特殊值来确定lateinit属性是否已初始化**。也就是说，即使我们可以将lateinit var重新分配给其他东西，我们也不能将它们显式设置为null，因为它具有特殊含义并且是为此保留的。

现在，让我们看看下面的声明有什么问题：

```kotlin
private lateinit var x: Int
```

根据我们目前所学到的知识，Kotlin会在底层将x编译为int。此外，由于这是一个lateinit变量，Kotlin需要使用null作为特殊值来表示未初始化的情况。**因为我们不能将null存储到int和其他原始类型中，所以这种声明在Kotlin中是非法的**。

有人可能会建议使用可为空的类型来解决上述限制：

```kotlin
private lateinit var x: Int?
```

显然，这没有意义，原因有两个：

1.  对于像Int?这样的可为空的类型，包括null在内的所有值都是可接受的。因此，我们不能将null用作未初始化情况的特殊持有者。出于这个原因，我们也不能将lateinit变量与可为空的类型一起使用。
2.  即使可以使用可为空的类型实现lateinit原始变量，这样做也没有意义。正如我们所知，我们使用lateinit变量来避免可空类型的尴尬，因此，对原始类型使用可空类型完全违背了使用lateinit的全部目的。

因此，总而言之，**我们不能在Kotlin中将lateinit变量用于基本类型(例如Int或Boolean)或可为空的类型**。

## 5. 总结

在本文中，我们了解了为什么Kotlin不允许对基本类型和任何可空类型使用lateinit变量，有兴趣的读者也可以关注[Project Valhalla](https://openjdk.java.net/projects/valhalla/)的进展，这可能会在不久的将来为这一限制提供很好的解决方案。