## 1. 概述

在本教程中，我们将讨论Kotlin中的继承规则和open关键字。

首先，我们将从一些关于继承的哲学开始，然后我们将看看open关键字如何影响Kotlin中的类、方法和属性。最后，我们将看看open关键字和企业框架(如Spring)如何相互交互。

## 2. 继承设计

**在Java中，类和方法**[默认是开放扩展的](https://www.baeldung.com/java-inheritance)，这意味着我们可以从任何类扩展或覆盖Java中的任何方法，除非它们被标记为[final](https://www.baeldung.com/java-effectively-final)。由于继承所带来的密切关系以及这种关系的风险，Joshua Bloch在[Effective Java](https://learning.oreilly.com/library/view/effective-java/9780134686097/ch4.xhtml#lev19)中专门为此写了一整篇文章来讨论这个问题：

>   设计和记录继承或禁止继承

**这意味着类和方法应该是密封的并且不可扩展或不可覆盖，除非我们有充分的理由来扩展或覆盖它们**，当我们决定我们的类应该对扩展开放时，我们应该记录覆盖任何方法的效果。

更好的做法倾向于默认是最终的，但实现有另一个想法。

让我们看看Kotlin如何处理相同的概念。

## 3. 类层次结构

**在Kotlin中，默认情况下一切都是最终的**。因此，如果我们尝试从其默认配置中的任何类进行扩展：

```kotlin
class Try
class Success : Try()
```

然后编译器将失败：

```bash
Kotlin: This type is final, so it cannot be inherited from
```

为了使一个类对扩展开放，**我们应该用open关键字标记该类**：

```kotlin
open class Try
class Success : Try()
```

现在我们可以扩展Try类，如果我们需要扩展Success类，我们也应该用open关键字标记它。**因此，open关键字不会对类产生传递影响**。

当我们不使用open关键字标记类时，Kotlin编译器会在字节码级别反映最终类。

让我们使用一个简单的类来验证这一点：

```kotlin
class Sealed
```

现在，让我们检查生成的字节码：

```bash
$ kotlinc Inheritance.kt 
$ javap -c -p -v cn.tuyucheng.taketoday.inheritance.Sealed 
Compiled from "Inheritance.kt"
public final class cn.tuyucheng.taketoday.inheritance.Sealed
// truncated
```

正如我们所看到的，这个类就像Java中的最终类一样。

## 4. 重写方法和属性

与类类似，默认情况下，**方法和属性在Kotlin中是最终的，即使封闭类是open也是如此**。例如：

```kotlin
open class Try {
    fun isSuccess(): Boolean = false
}
class Success : Try() {
    override fun isSuccess(): Boolean = true
}
```

编译器将无法编译Success类，因为我们无法重写final方法：

```bash
Kotlin: 'isSuccess' in 'Try' is final and cannot be overridden
```

与类类似，编译器在这里发出一个final方法：

```apache
$ kotlinc Inheritance.kt
$ javap -c -p -v cn.tuyucheng.taketoday.inheritance.Try
// truncated
public final boolean isSuccess();
   descriptor: ()Z
   flags: (0x0011) ACC_PUBLIC, ACC_FINAL
```

**要覆盖子类中的方法，我们应该在超类中用open关键字标记该方法**：

```kotlin
open class Try {
    open fun isSuccess(): Boolean = false
}
class Success : Try() {
    override fun isSuccess(): Boolean = true
}
```

属性也是如此，它们是最终的，除非我们用open关键字标记它们：

```kotlin
open class Try {
    open val value: Any? = null
    // omitted
}
class Success(override val value: Any?) : Try() {
    // omitted
}
```

如上所示，我们正在重写子类构造函数中的属性。

**用override关键字标记的方法和属性是隐式打开的，这样我们就可以在子类中覆盖它们**。为了防止这种情况，我们可以用final标记它们：

```kotlin
open class Success(override val value: Any?) : Try() {
    final override fun isSuccess(): Boolean = true
}
```

如上所示，Success类的子类不能重写isSuccess()方法。

## 5. 框架互操作性

JVM生态系统中的许多企业框架，例如Spring和Hibernate，都依赖于继承来创建[动态代理](https://www.baeldung.com/java-dynamic-proxies)，所以我们不能将最终的Kotlin类用作Spring bean或JPA实体。

解决此问题的一种方法是使用open关键字标记每个Spring bean或JPA实体，我们可能还需要标记他们建议的方法和属性：

```kotlin
@Service
@Transactional
open class UserService {

    open fun register(u: User) {
        // omitted
    }
}
```

使用open关键字标记每个特殊类及其成员是一种不方便且无效的方法，**为了改善这种情况，JetBrains提供了**[Kotlin-allopen](https://www.baeldung.com/kotlin-allopen-spring)**插件**，该插件使Kotlin类适应Spring等框架的要求。

基本上，**这个插件使用open关键字标记某些类及其成员，而无需显式open**。为此，应该使用特殊的Spring注解对这些类进行标注，例如@Service或@Entity：

```kotlin
@Service
@Transactional
class UserService {

    fun register(u: User) {
        // omitted
    }
}
```

即使我们没有显式添加这些open关键字，编译器插件也会在编译期间添加它们。

## 6. 总结

在本教程中，我们了解了继承在Kotlin中的工作原理。此外，我们了解了open关键字如何影响Kotlin中的类层次结构。