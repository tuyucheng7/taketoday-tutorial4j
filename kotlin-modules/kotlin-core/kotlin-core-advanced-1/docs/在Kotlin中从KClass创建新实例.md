## 1. 概述

在本教程中，我们将列举几种借助反射和[KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/)令牌实例化Kotlin对象的方法。

## 2. 依赖关系

在本文中，我们将广泛使用[kotlin-reflect](https://search.maven.org/artifact/org.jetbrains.kotlin/kotlin-reflect)模块，因此，我们应该将它的依赖添加到我们的构建文件中，例如 pom.xml：

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-reflect</artifactId>
    <version>1.5.21</version>
</dependency>
```

此外，为了操作KClass令牌，我们将使用这三个类：

```kotlin
class NoArg
class OptionalArgs(val arg: String = "default")
class RequiredArgs(val arg1: String, val arg2: String) {
    constructor(arg1: String): this(arg1, "default")
}
```

第一个有一个无参数构造函数，第二个构造函数只接收一个可选参数，最后一个需要两个强制参数。除了这些之外，最后一个类还有一个辅助构造函数。

## 3. 反射实例化

从Kotlin 1.1开始，KClass令牌上的扩展函数[createInstance()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect.full/create-instance.html)可以创建对象实例：

```kotlin
val noArgInstance = NoArg::class.createInstance()
assertNotNull(noArgInstance)
assertThat(noArgInstance).isInstanceOf(NoArg::class.java)
```

**此扩展函数仅适用于具有无参数构造函数或仅具有可选参数的构造函数的类**，因此，我们也可以创建OptionalArgs类的实例：

```kotlin
val instance = OptionalArgs::class.createInstance()
assertNotNull(instance)
assertThat(instance).isInstanceOf(OptionalArgs::class.java)
```

但是，如果我们尝试实例化一个类，其构造函数具有必需的构造函数参数，它将抛出异常：

```kotlin
val exception = assertThrows<IllegalArgumentException> { RequiredArgs::class.createInstance() }
assertThat(exception).hasMessageStartingWith("Class should have a single no-arg constructor")
```

如上所示，它抛出一个[IllegalArgumentException](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/IllegalArgumentException.html)实例，因为主构造函数有两个必需参数。

### 3.1 主构造函数

为了创建至少具有一个必需的构造函数参数的类的实例，**我们可以反射性地调用主构造函数**：

```kotlin
val primaryConstructor = RequiredArgs::class.primaryConstructor
val instance = primaryConstructor!!.call("first arg", "second arg")
assertNotNull(instance)
assertThat(instance).isInstanceOf(RequiredArgs::class.java)
```

在这里，[primaryConstructor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect.full/primary-constructor.html)扩展属性找到主构造函数，此外，[call()](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-callable/call.html)函数使用给定的参数执行给定的构造函数。

### 3.2 辅助构造函数

当有多个构造函数时，我们可以**使用**[constructors](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/constructors.html)**扩展属性来找到合适的构造函数**：

```kotlin
val constructors = RequiredArgs::class.constructors
val instance = constructors.first { it.parameters.size == 1 }.call("arg1")
assertEquals("arg1", instance.arg1)
assertEquals("default", instance.arg2)
```

在这里，我们过滤以找到只有一个参数的构造函数。

## 4. 总结

在本文中，我们熟悉了几种在给定KClass令牌的情况下实例化Kotlin对象的方法。