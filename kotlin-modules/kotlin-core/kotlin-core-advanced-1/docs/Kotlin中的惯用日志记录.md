## 1. 简介

在本教程中，我们将了解一些符合典型[Kotlin](https://www.baeldung.com/category/kotlin/)编程风格的日志记录习惯用法。

## 2. 日志记录

日志记录是编程中无处不在的需求。虽然这显然是一个简单的想法(只是打印东西！)，但有很多方法可以做到这一点。

事实上，每种语言、操作系统和环境都有自己惯用的，有时甚至是特殊的日志记录解决方案。实际上，通常不止一个。

在这里，我们将重点关注Kotlin的日志记录故事。

我们还将使用日志记录作为深入研究一些高级Kotlin功能并探索它们的细微差别的借口。

## 3. 设置

**对于代码示例，我们将使用**[SLF4J](https://www.slf4j.org/)**库，但相同的模式和解决方案适用于**[Log4J](https://www.baeldung.com/log4j2-appenders-layouts-filters)、[JUL](https://docs.oracle.com/en/java/javase/11/core/java-logging-overview.html)**和其他日志记录库**。

因此，让我们首先在我们的pom中包含[SLF4J API](https://search.maven.org/search?q=a:slf4j-api)和[Logback](https://search.maven.org/artifact/ch.qos.logback/logback-core)依赖项：

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.30</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.3</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-core</artifactId>
    <version>1.2.3</version>
</dependency>
```

现在，让我们来看看四种不同方法的日志记录是什么样的：

-   一个属性
-   伴生对象
-   扩展方法
-   委托属性

## 4. Logger作为一个属性

我们可能会尝试的第一件事是在我们需要的地方声明一个记录器属性：

```kotlin
class Property {
    private val logger = LoggerFactory.getLogger(javaClass)

    //...
}
```

在这里，我们使用javaClass从定义的类名动态计算记录器的名称，因此，我们可以轻松地将此代码段并粘贴到任何我们想要的地方。

然后，我们可以在声明类的任何方法中使用记录器：

```kotlin
fun log(s: String) {
    logger.info(s)
}
```

我们选择将记录器声明为私有的，因为我们不希望其他类(包括子类)访问它并代表我们的类进行记录。

**当然，这只是对程序员的提示**，而不是强制执行的规则，因为很容易获得同名的记录器。

### 4.1 节省一些输入

我们可以通过将getLogger调用分解为一个函数来缩短我们的代码：

```kotlin
fun getLogger(forClass: Class<*>): Logger =
  LoggerFactory.getLogger(forClass)
```

通过将其放入实用程序类中，**我们现在可以在下面的示例中简单地调用getLogger(javaClass)而不是LoggerFactory.getLogger(javaClass)**。

## 5. 伴生对象

虽然最后一个例子的简单性很强，但它并不是最有效的。

首先，在每个类实例中保存对记录器的引用会消耗内存。其次，即使记录器被缓存，我们仍然会为每个具有记录器的对象实例进行缓存查找。

让我们看看伴生对象是否表现得更好。

### 5.1. 第一次尝试

在Java中，将记录器声明为静态是解决上述问题的一种模式。

**但是，在Kotlin中，我们没有静态属性**。

但是我们可以用[伴生对象](https://www.baeldung.com/kotlin-objects)来模拟它们：

```kotlin
class LoggerInCompanionObject {
    companion object {
        private val loggerWithExplicitClass
          = getLogger(LoggerInCompanionObject::class.java)
    }

    //...
}
```

请注意我们如何重用4.1节中的getLogger便捷函数，我们将在整篇文章中不断提及它。

因此，通过上面的代码，我们可以像以前一样在类的任何方法中再次使用记录器：

```kotlin
fun log(s: String) {
    loggerWithExplicitClass.info(s)
}
```

### 5.2 javaClass发生了什么？

遗憾的是，上述方法有一个缺点。因为我们直接引用封闭类：

```kotlin
LoggerInCompanionObject::class.java
```

我们已经失去了粘贴的便利。

**但是为什么不像以前那样只使用javaClass呢**？事实上，我们不能，如果我们有，我们将错误地获得一个以伴生对象的类命名的记录器：

```kotlin
//Incorrect!
class LoggerInCompanionObject {
    companion object {
        private val loggerWithWrongClass = getLogger(javaClass)
    }
}
//...
loggerWithWrongClass.info("test")

```

以上将输出一个稍微错误的记录器名称。看看$Companion位：

```bash
21:46:36.377 [main] INFO
cn.tuyucheng.taketoday.kotlin.logging.LoggerInCompanionObject$Companion - test
```

事实上，**IntelliJ IDEA用警告标记记录器的声明**，因为它认识到在伴生对象中对javaClass的引用可能不是我们想要的。

### 5.3 通过反射派生类名

不过，并非一切都丢失了。

我们确实有办法自动派生类名并恢复我们和粘贴代码的能力，但我们需要额外的反射才能做到这一点。

首先，让我们确保我们的pom中有[kotlin-reflect](https://search.maven.org/search?q=a:kotlin-reflect)依赖项：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-reflect</artifactId>
    <version>1.2.51</version>
</dependency>
```

然后，我们可以动态获取正确的日志类名：

```kotlin
companion object {
    @Suppress("JAVA_CLASS_ON_COMPANION")
    private val logger = getLogger(javaClass.enclosingClass)
}
//...
logger.info("I feel good!")

```

我们现在将获得正确的输出：

```bash
10:00:32.840 [main] INFO
cn.tuyucheng.taketoday.kotlin.logging.LoggerInCompanionObject - I feel good!
```

我们使用enclosingClass的原因是伴生对象最终是内部类的实例，因此enclosingClass指的是外部类，或者在本例中是LoggerInCompanionObject。

此外，现在我们可以取消IntelliJ IDEA对javaClass发出的警告，因为现在我们正在用它做正确的事情。

### 5.4 @JvmStatic

虽然伴生对象的属性看起来像静态字段，但伴生对象更像是单例。

Kotlin伴生对象有一个特殊的功能，至少在JVM上运行时，它将[伴生对象](https://www.baeldung.com/kotlin-objects)转换为静态字段：

```kotlin
@JvmStatic
private val logger = getLogger(javaClass.enclosingClass)
```

### 5.5 把它们放在一起

让我们将所有三个改进放在一起，当结合在一起时，这些改进使我们的日志记录构造可和静态：

```kotlin
class LoggerInCompanionObject {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun log(s: String) {
        logger.info(s)
    }
}
```

## 6. 从扩展方法记录

虽然有趣且高效，但使用伴生对象是冗长的，一开始是一行代码，现在是多行代码，可以在整个代码库中粘贴。

此外，使用伴生对象会产生额外的内部类，与Java中简单的static logger声明相比，使用伴生对象要重一些。

因此，让我们尝试一种使用[扩展方法](https://www.baeldung.com/kotlin-extension-methods)的方法。

### 6.1 第一次尝试

**基本思想是定义一个返回Logger的扩展方法，这样每个需要它的类都可以调用该方法并获得正确的实例**。

我们可以在类路径的任何地方定义它：

```kotlin
fun <T : Any> T.logger(): Logger = getLogger(javaClass)
```

扩展方法基本上被到它们适用的任何类；所以，我们可以简单地再次直接引用javaClass。

现在，所有类都将拥有方法记录器，就好像它已在类型中定义一样：

```kotlin
class LoggerAsExtensionOnAny { // implied ": Any"
    fun log(s: String) {
        logger().info(s)
    }
}
```

**虽然这种方法比伴生对象更简洁**，但我们可能希望先解决一些问题。

### 6.2 Any类型的污染

我们的第一个扩展方法的一个显着缺点是它污染了Any类型。

因为我们将它定义为适用于任何类型，所以它最终有点侵入性：

```kotlin
"foo".logger().info("uh-oh!")
// Sample output:
// 13:19:07.826 [main] INFO java.lang.String - uh-oh!
```

**通过在Any上定义logger()，我们用该方法污染了语言中的所有类型**。

这不一定是个问题，它不会阻止其他类拥有自己的记录器方法。

但是，除了额外的噪音之外，它还会破坏封装。类型现在可以互相记录，这是我们不想要的。

现在几乎每个IDE代码建议都会弹出记录器。 

### 6.3 标记接口上的扩展方法

**我们可以使用标记接口缩小扩展方法的范围**：

```kotlin
interface Logging
```

定义了这个接口后，我们可以指出我们的扩展方法只适用于实现这个接口的类型：

```kotlin
fun <T : Logging> T.logger(): Logger = getLogger(javaClass)
```

现在，如果我们改变我们的类型来实现Logging，我们可以像以前一样使用logger：

```kotlin
class LoggerAsExtensionOnMarkerInterface : Logging {
    fun log(s: String) {
        logger().info(s)
    }
}
```

### 6.4 具体化类型参数

在最后两个示例中，我们使用反射来获取javaClass并为我们的记录器提供可分辨的名称。

但是，我们也可以从T类型参数中提取此类信息，从而避免在运行时进行反射调用。为此，我们将函数声明为内联函数并[具体化类型参数](https://kotlinlang.org/docs/reference/inline-functions.html#reified-type-parameters)：

```kotlin
inline fun <reified T : Logging> T.logger(): Logger =
  getLogger(T::class.java)
```

请注意，这会改变代码在继承方面的语义。我们将在第8节中详细讨论这个问题。

### 6.5 结合记录器属性

扩展方法的一个好处是我们可以将它们与我们的第一种方法结合起来：

```kotlin
val logger = logger()
```

### 6.6 结合伴生对象

**但是如果我们想在伴生对象中使用我们的扩展方法，情况就更复杂了**：

```kotlin
companion object : Logging {
    val logger = logger()
}
```

因为我们会遇到与以前相同的javaClass问题：

```bash
cn.tuyucheng.taketoday.kotlin.logging.LoggerAsExtensionOnMarkerInterface$Companion
```

为了解决这个问题，让我们首先定义一个更稳健地获取类的方法：

```kotlin
inline fun <T : Any> getClassForLogging(javaClass: Class<T>): Class<*> {
    return javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass
}
```

此处，如果javaClass引用伴生对象，则getClassForLogging返回enclosingClass。

现在我们可以再次更新我们的扩展方法：

```kotlin
inline fun <reified T : Logging> T.logger(): Logger
  = getLogger(getClassForLogging(T::class.java))
```

**这样，无论记录器是作为属性还是伴生对象，我们实际上都可以使用相同的扩展方法**。

## 7. Logger作为委托属性

最后，让我们看看[委托属性](https://www.baeldung.com/kotlin-delegated-properties)。

这种方法的优点在于我们**无需标记接口就可以避免名称空间污染**：

```kotlin
class LoggerDelegate<in R : Any> : ReadOnlyProperty<R, Logger> {
    override fun getValue(thisRef: R, property: KProperty<*>)
     = getLogger(getClassForLogging(thisRef.javaClass))
}
```

然后我们可以将它与属性一起使用：

```kotlin
private val logger by LoggerDelegate()
```

由于getClassForLogging，这也适用于伴生对象：

```kotlin
companion object {
    val logger by LoggerDelegate()
}
```

虽然委托属性很强大，但请注意**每次读取属性时都会重新计算getValue**。

另外，我们应该记住**委托属性必须使用反射**才能工作。

## 8. 关于继承的一些注意事项

每个班级有一个记录器是很典型的。这就是为什么我们通常也将记录器声明为私有的。

**然而，有时我们希望我们的子类引用它们超类的记录器**。

根据我们的用例，上述四种方法的行为会有所不同。

通常，当我们使用反射或其他动态特性时，我们会在运行时获取对象的实际类。

但是，当我们通过名称静态引用类或具体化类型参数时，该值将在编译时固定。

例如，对于委托属性，由于每次读取属性时都会动态获取记录器实例，因此它将采用使用它的类的名称：

```kotlin
open class LoggerAsPropertyDelegate {
    protected val logger by LoggerDelegate()
    //...
}

class DelegateSubclass : LoggerAsPropertyDelegate() {
    fun show() {
        logger.info("look!")
    }
}
```

让我们看看输出：

```bash
09:23:33.093 [main] INFO
cn.tuyucheng.taketoday.kotlin.logging.DelegateSubclass - look!
```

**即使在超类中声明了logger，它也会打印子类的名称**。

当记录器被声明为属性并使用javaClass实例化时，也会发生同样的情况。

**扩展方法也表现出这种行为，除非我们具体化类型参数**。

相反，**使用具体化的泛型、显式类名和伴生对象，记录器的名称在整个类型层次结构中保持不变**。

## 9. 总结

在本文中，我们研究了几种可应用于声明和实例化记录器任务的Kotlin技术。

从简单开始，我们在一系列提高效率和减少样板代码的尝试中逐步增加复杂性，查看Kotlin伴生对象、扩展方法和委托属性。